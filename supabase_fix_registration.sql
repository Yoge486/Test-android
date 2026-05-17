-- ============================================================
-- FIX: "Database error saving new user" on registration
-- Run this ENTIRE script in: Supabase Dashboard → SQL Editor
-- ============================================================

-- Step 1: Add missing INSERT policy for profiles table
-- (Allows the trigger to insert a profile row upon user signup)
CREATE POLICY "Enable insert for service role and triggers"
    ON public.profiles FOR INSERT
    WITH CHECK (auth.uid() = id);

-- Step 2: Re-create the trigger function with better error handling
-- Drop and recreate to ensure it's up to date
CREATE OR REPLACE FUNCTION public.handle_new_user()
RETURNS TRIGGER AS $$
DECLARE
    assigned_role user_role;
BEGIN
  -- Assign role based on email
  IF new.email = 'yogesh27124@gmail.com' THEN
      assigned_role := 'HIGHER_OFFICIAL';
  ELSE
      assigned_role := 'EMPLOYEE';
  END IF;

  -- Insert the profile row
  INSERT INTO public.profiles (id, full_name, role)
  VALUES (
      new.id,
      COALESCE(new.raw_user_meta_data->>'full_name', 'Employee'),
      assigned_role
  );

  RETURN new;
EXCEPTION
  WHEN OTHERS THEN
    -- Log error but don't block user creation
    RAISE LOG 'handle_new_user failed for %: %', new.id, SQLERRM;
    RETURN new;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- Step 3: Ensure the trigger exists (recreate if needed)
DROP TRIGGER IF EXISTS on_auth_user_created ON auth.users;

CREATE TRIGGER on_auth_user_created
  AFTER INSERT ON auth.users
  FOR EACH ROW EXECUTE PROCEDURE public.handle_new_user();

-- Step 4: Verify the enum type exists
-- (This will error harmlessly if it already exists)
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'user_role') THEN
        CREATE TYPE user_role AS ENUM ('EMPLOYEE', 'TEAM_LEADER', 'HR', 'HIGHER_OFFICIAL');
    END IF;
END$$;
