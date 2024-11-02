package com.company.foodflow.presentation.signup


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,

    ) : ViewModel() {

    private val _accountCreationState =
        MutableStateFlow<CreateAccountState>(CreateAccountState.Idle)
    val accountCreationState: StateFlow<CreateAccountState> = _accountCreationState
    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName
    private val _passwordResetEvent = MutableSharedFlow<Boolean>()
    val passwordResetEvent = _passwordResetEvent.asSharedFlow()

    init {
        fetchUserName()
    }

    fun changePassword() {
        val user = auth.currentUser
        user?.let {
            val emailAddress = it.email
            if (emailAddress != null) {
                viewModelScope.launch {
                    try {
                        auth.sendPasswordResetEmail(emailAddress).await()
                        _passwordResetEvent.emit(true)  // Password reset email sent successfully
                    } catch (e: Exception) {
                        _passwordResetEvent.emit(false) // Failed to send email
                    }
                }
            }
        }
    }

    private fun fetchUserName() {
        val user = auth.currentUser
        _userName.value = user?.displayName ?: user?.email ?: "User"
    }

    fun createAccount(name: String, email: String, password: String) {
        _accountCreationState.value = CreateAccountState.Loading

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Account created, update the user's profile with the name
                    val user = auth.currentUser
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build()

                    user?.updateProfile(profileUpdates)?.addOnCompleteListener { profileTask ->
                        if (profileTask.isSuccessful) {
                            // Save user data to Firestore
                            saveUserDataToFirestore(user.uid, name, email)
                        } else {
                            _accountCreationState.value = CreateAccountState.Error(
                                profileTask.exception?.message ?: "Profile update failed"
                            )
                        }
                    }
                } else {
                    _accountCreationState.value = CreateAccountState.Error(
                        task.exception?.message ?: "Account creation failed"
                    )
                }
            }
    }

    private fun saveUserDataToFirestore(userId: String, name: String, email: String) {
        val userMap = mapOf(
            "name" to name,
            "email" to email
        )

        firestore.collection("users").document(userId).set(userMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _accountCreationState.value = CreateAccountState.Success
                } else {
                    _accountCreationState.value = CreateAccountState.Error(
                        task.exception?.message ?: "Failed to save user data"
                    )
                }
            }
    }

    fun logout(onLogout: () -> Unit) {
        auth.signOut()
        onLogout() // Call the provided callback function after sign-out
    }
}

sealed class CreateAccountState {
    object Idle : CreateAccountState()
    object Loading : CreateAccountState()
    object Success : CreateAccountState()
    data class Error(val message: String) : CreateAccountState()
}
