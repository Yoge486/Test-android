-- ============================================================
-- TaskManager Supabase Schema (Idempotent - safe to re-run)
-- ============================================================

-- 1. Create Enums for application states (only if they don't exist)
DO $$ BEGIN CREATE TYPE user_role AS ENUM ('EMPLOYEE', 'TEAM_LEADER', 'HR'); EXCEPTION WHEN duplicate_object THEN NULL; END $$;
DO $$ BEGIN CREATE TYPE task_priority AS ENUM ('LOW', 'MEDIUM', 'HIGH'); EXCEPTION WHEN duplicate_object THEN NULL; END $$;
DO $$ BEGIN CREATE TYPE task_status AS ENUM ('TO_DO', 'IN_PROGRESS', 'UNDER_REVIEW', 'COMPLETED', 'OVERDUE', 'ESCALATED'); EXCEPTION WHEN duplicate_object THEN NULL; END $$;
DO $$ BEGIN CREATE TYPE leave_status AS ENUM ('PENDING', 'APPROVED_BY_LEADER', 'APPROVED_BY_HR', 'REJECTED'); EXCEPTION WHEN duplicate_object THEN NULL; END $$;
DO $$ BEGIN CREATE TYPE reset_status AS ENUM ('PENDING', 'APPROVED', 'REJECTED'); EXCEPTION WHEN duplicate_object THEN NULL; END $$;

-- 2. Create foundational tables
CREATE TABLE IF NOT EXISTS public.password_reset_requests (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_email TEXT NOT NULL,
    reason TEXT NOT NULL,
    status reset_status DEFAULT 'PENDING',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.teams (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    name TEXT NOT NULL,
    leader_id UUID, -- Will be constrained to profiles later after creation
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.profiles (
    id UUID REFERENCES auth.users(id) ON DELETE CASCADE PRIMARY KEY,
    full_name TEXT,
    role user_role DEFAULT 'EMPLOYEE',
    department TEXT,
    team_id UUID REFERENCES public.teams(id) ON DELETE SET NULL,
    manager_id UUID REFERENCES public.profiles(id) ON DELETE SET NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Add foreign key constraint for leader_id back onto the profiles table
DO $$ BEGIN
    ALTER TABLE public.teams
    ADD CONSTRAINT fk_team_leader
    FOREIGN KEY (leader_id) REFERENCES public.profiles(id) ON DELETE SET NULL;
EXCEPTION WHEN duplicate_object THEN NULL;
END $$;

CREATE TABLE IF NOT EXISTS public.tasks (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    title TEXT NOT NULL,
    description TEXT,
    priority task_priority DEFAULT 'MEDIUM',
    status task_status DEFAULT 'TO_DO',
    category TEXT,
    due_date TIMESTAMP WITH TIME ZONE,
    assignee_id UUID REFERENCES public.profiles(id) ON DELETE CASCADE,
    creator_id UUID REFERENCES public.profiles(id) ON DELETE CASCADE,
    team_id UUID REFERENCES public.teams(id) ON DELETE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.leave_requests (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    employee_id UUID REFERENCES public.profiles(id) ON DELETE CASCADE,
    leave_type TEXT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    reason TEXT,
    status leave_status DEFAULT 'PENDING',
    leader_comment TEXT,
    hr_comment TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.communities (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT,
    team_id UUID REFERENCES public.teams(id) ON DELETE CASCADE,
    is_org_wide BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.messages (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    community_id UUID REFERENCES public.communities(id) ON DELETE CASCADE,
    sender_id UUID REFERENCES public.profiles(id) ON DELETE CASCADE,
    content TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 3. Enable Row-Level Security (RLS) on all tables
ALTER TABLE public.teams ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.tasks ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.leave_requests ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.communities ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.messages ENABLE ROW LEVEL SECURITY;

-- 4. Create RLS Policies (drop + recreate for idempotency)

-- Profiles
DROP POLICY IF EXISTS "Profiles are readable by everyone" ON public.profiles;
CREATE POLICY "Profiles are readable by everyone"
    ON public.profiles FOR SELECT USING (true);

DROP POLICY IF EXISTS "Users can edit their own profile" ON public.profiles;
CREATE POLICY "Users can edit their own profile"
    ON public.profiles FOR UPDATE USING (auth.uid() = id);

DROP POLICY IF EXISTS "Users can insert their own profile" ON public.profiles;
CREATE POLICY "Users can insert their own profile"
    ON public.profiles FOR INSERT WITH CHECK (auth.uid() = id);

-- Tasks
DROP POLICY IF EXISTS "Tasks viewable by assignee or creator" ON public.tasks;
CREATE POLICY "Tasks viewable by assignee or creator"
    ON public.tasks FOR SELECT 
    USING (auth.uid() = assignee_id OR auth.uid() = creator_id);

DROP POLICY IF EXISTS "Tasks editable by assignee or creator" ON public.tasks;
CREATE POLICY "Tasks editable by assignee or creator"
    ON public.tasks FOR UPDATE 
    USING (auth.uid() = assignee_id OR auth.uid() = creator_id);

DROP POLICY IF EXISTS "Tasks insertable by creator" ON public.tasks;
CREATE POLICY "Tasks insertable by creator"
    ON public.tasks FOR INSERT WITH CHECK (auth.uid() = creator_id);

-- Communities & Messages
DROP POLICY IF EXISTS "Communities readable by authenticated users" ON public.communities;
CREATE POLICY "Communities readable by authenticated users"
    ON public.communities FOR SELECT USING (auth.role() = 'authenticated');

DROP POLICY IF EXISTS "Communities insertable by authenticated users" ON public.communities;
CREATE POLICY "Communities insertable by authenticated users"
    ON public.communities FOR INSERT WITH CHECK (auth.role() = 'authenticated');

DROP POLICY IF EXISTS "Messages readable by authenticated users" ON public.messages;
CREATE POLICY "Messages readable by authenticated users"
    ON public.messages FOR SELECT USING (auth.role() = 'authenticated');

DROP POLICY IF EXISTS "Messages insertable by sender" ON public.messages;
CREATE POLICY "Messages insertable by sender"
    ON public.messages FOR INSERT WITH CHECK (auth.uid() = sender_id);


-- 5. Automate Profile Creation on sign up (reads role from signup metadata)
CREATE OR REPLACE FUNCTION public.handle_new_user()
RETURNS TRIGGER AS $$
DECLARE
    assigned_role user_role;
    role_text TEXT;
BEGIN
  -- Read role from user metadata passed during signup, default to EMPLOYEE
  role_text := COALESCE(new.raw_user_meta_data->>'role', 'EMPLOYEE');
  
  -- Validate the role is a valid enum value
  BEGIN
    assigned_role := role_text::user_role;
  EXCEPTION WHEN invalid_text_representation THEN
    assigned_role := 'EMPLOYEE';
  END;

  INSERT INTO public.profiles (id, full_name, role)
  VALUES (
      new.id, 
      COALESCE(new.raw_user_meta_data->>'full_name', 'Employee'), 
      assigned_role
  );
  RETURN new;
EXCEPTION
  WHEN OTHERS THEN
    RAISE LOG 'handle_new_user failed for %: %', new.id, SQLERRM;
    RETURN new;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

DROP TRIGGER IF EXISTS on_auth_user_created ON auth.users;
CREATE TRIGGER on_auth_user_created
  AFTER INSERT ON auth.users
  FOR EACH ROW EXECUTE PROCEDURE public.handle_new_user();
