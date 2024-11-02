package com.company.foodflow.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.company.foodflow.domain.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> get() = _loginState

    fun onLoginClick(email: String, password: String, onSuccessNavigate: () -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            _loginState.value = LoginState.Error("Email and password must not be empty")
            return
        }

        _loginState.value = LoginState.Loading

        viewModelScope.launch {
            val result = try {
                repository.login(email, password)
            } catch (e: Exception) {
                Result.failure(e)
            }

            _loginState.value = when {
                result.isSuccess -> {
                    onSuccessNavigate() // Navigate to Inventory on success
                    LoginState.Success
                }

                else -> LoginState.Error(result.exceptionOrNull()?.message ?: "Login failed")
            }
        }
    }

    fun onGoogleSignInClick() {
        // Google sign-in logic goes here
    }
}
