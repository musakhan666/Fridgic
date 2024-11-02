package com.company.foodflow.data

import com.company.foodflow.domain.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<FirebaseUser?> {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Result.success(authResult.user) // Return a successful result if login succeeds
        } catch (e: Exception) {
            Result.failure(e) // Return a failure result if login fails
        }
    }

    override suspend fun register(email: String, password: String): Result<FirebaseUser?> {
        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user
            user?.let { saveUserToFirestore(it) }  // Save user details to Firestore if registration succeeds
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun saveUserToFirestore(user: FirebaseUser) {
        val userData = mapOf(
            "uid" to user.uid,
            "email" to user.email,
            "name" to user.displayName.orEmpty() // Add more fields if needed
        )
        firestore.collection("users").document(user.uid).set(userData).await()
    }

}
