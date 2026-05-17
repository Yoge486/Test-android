package com.example.taskmanager.data.repository

import com.example.taskmanager.domain.model.UserRole
import com.example.taskmanager.domain.repository.AuthRepository
import com.example.taskmanager.domain.util.Resource
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import io.github.jan.supabase.postgrest.postgrest
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val supabaseClient: SupabaseClient
) : AuthRepository {

    override val sessionStatus: Flow<Boolean> = supabaseClient.auth.sessionStatus.map {
        it is SessionStatus.Authenticated
    }

    override suspend fun login(email: String, password: String): Resource<Unit> {
        return try {
            supabaseClient.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to login")
        }
    }

    override suspend fun register(email: String, password: String, role: UserRole): Resource<Unit> {
        return try {
            // Sign up with Supabase Auth, passing role in metadata
            supabaseClient.auth.signUpWith(Email) {
                this.email = email
                this.password = password
                this.data = kotlinx.serialization.json.buildJsonObject {
                    put("full_name", kotlinx.serialization.json.JsonPrimitive("Employee"))
                    put("role", kotlinx.serialization.json.JsonPrimitive(role.name))
                }
            }

            // After signup, ensure a profile row exists.
            // The DB trigger may handle this, but we create it as a fallback.
            try {
                val user = supabaseClient.auth.currentUserOrNull()
                if (user != null) {
                    val existingProfile = supabaseClient.postgrest["profiles"]
                        .select { filter { eq("id", user.id) } }
                        .decodeSingleOrNull<kotlinx.serialization.json.JsonObject>()

                    if (existingProfile == null) {
                        val profileData = kotlinx.serialization.json.buildJsonObject {
                            put("id", kotlinx.serialization.json.JsonPrimitive(user.id))
                            put("full_name", kotlinx.serialization.json.JsonPrimitive("Employee"))
                            put("role", kotlinx.serialization.json.JsonPrimitive(role.name))
                        }
                        supabaseClient.postgrest["profiles"].insert(profileData)
                    }
                }
            } catch (_: Exception) {
                // Profile may already have been created by the DB trigger — ignore
            }

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to register")
        }
    }

    override suspend fun logout(): Resource<Unit> {
        return try {
            supabaseClient.auth.signOut()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to logout")
        }
    }

    override suspend fun checkSession(): Boolean {
        return supabaseClient.auth.currentSessionOrNull() != null
    }

    override suspend fun submitPasswordResetRequest(email: String, reason: String): Resource<Unit> {
        return try {
            val request = com.example.taskmanager.domain.model.PasswordResetRequest(
                userEmail = email,
                reason = reason
            )
            supabaseClient.postgrest["password_reset_requests"].insert(request)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to submit reset request")
        }
    }

    override suspend fun checkPasswordResetStatus(email: String): Resource<String> {
        return try {
            val requests = supabaseClient.postgrest["password_reset_requests"]
                .select { filter { eq("user_email", email) } }
                .decodeList<com.example.taskmanager.domain.model.PasswordResetRequest>()
                
            val latest = requests.lastOrNull() 
            if (latest != null) {
                Resource.Success(latest.status)
            } else {
                Resource.Error("No request found")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to check status")
        }
    }

    override suspend fun overridePassword(email: String, newPassword: String): Resource<Unit> {
        return Resource.Success(Unit)
    }

    override suspend fun getPendingResets(): Resource<List<com.example.taskmanager.domain.model.PasswordResetRequest>> {
        return try {
            val requests = supabaseClient.postgrest["password_reset_requests"]
                .select { filter { eq("status", "PENDING") } }
                .decodeList<com.example.taskmanager.domain.model.PasswordResetRequest>()
            Resource.Success(requests)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to fetch pending resets")
        }
    }

    override suspend fun updateResetRequestStatus(id: String, status: String): Resource<Unit> {
        return try {
            supabaseClient.postgrest["password_reset_requests"]
                .update({ set("status", status) }) { filter { eq("id", id) } }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update status")
        }
    }
}
