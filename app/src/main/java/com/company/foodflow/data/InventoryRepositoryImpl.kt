package com.company.foodflow.data

import android.net.Uri
import android.util.Log
import com.company.foodflow.data.model.InventoryItem
import com.company.foodflow.domain.InventoryRepository
import com.company.foodflow.presentation.inventory.SortOrder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class InventoryRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val firebaseStorage: FirebaseStorage
) : InventoryRepository {

    override fun getInventoryItems(
        category: String,
        sortOrder: SortOrder
    ): Flow<List<InventoryItem>> = callbackFlow {
        try {
            // Ensure user is authenticated and retrieve UID
            val userId = firebaseAuth.currentUser?.uid ?: throw Exception("User not authenticated")

            Log.d("Items", category.toString())

            // Construct query based on the category filter
            val userInventoryRef =
                firestore.collection("inventory").document(userId).collection("items")
            val query = if (category != "All") {
                userInventoryRef.whereEqualTo("location", category)
            } else {
                userInventoryRef
            }

            // Fetch and deserialize documents into InventoryItem objects
            val items = query.get().await().documents.mapNotNull { doc ->
                doc.toObject(InventoryItem::class.java)?.copy(id = doc.id) // Set document ID as item id
            }
            Log.d("Items", items.toString())

            // Sort items based on the specified sort order
            val sortedItems = sortItems(items, sortOrder)
            trySend(sortedItems) // Send the sorted list to the flow
        } catch (e: Exception) {
            Log.d("Items", e.localizedMessage ?: "Error fetching items")
            close(e) // Close the flow with the exception
        }

        awaitClose { } // Await closing
    }



    private fun sortItems(items: List<InventoryItem>, order: SortOrder): List<InventoryItem> {
        return when (order) {
            SortOrder.BY_NAME -> items.sortedBy { it.name }
            SortOrder.BY_EXPIRATION -> items.sortedBy { it.expirationDate }
            SortOrder.BY_QUANTITY -> items.sortedBy { it.quantity }
            SortOrder.DAYS_REMAINING -> items.sortedBy { it.daysRemaining }
            SortOrder.ALPHABETICAL -> items.sortedBy { it.name }
        }
    }
    override suspend fun deleteItem(item: InventoryItem) {

        try {
            val userId = firebaseAuth.currentUser?.uid ?: throw Exception("User not authenticated")
                firestore.collection("inventory")
                    .document(userId)
                    .collection("items")
                    .document(item.id)
                    .delete()
                    .await() // Await to ensure the deletion completes

        } catch (e: Exception) {
            // Handle exceptions (e.g., log error, retry logic, etc.)
            e.printStackTrace()
        }
    }

    override suspend fun updateItemLocation(item: InventoryItem, location: String) {
        try {
            val userId = firebaseAuth.currentUser?.uid ?: throw Exception("User not authenticated")
            val itemRef = firestore.collection("inventory").document(userId).collection("items")
                .document(item.id) // Assuming item has a unique id
            itemRef.update("location", location).await() // Update the location field
        } catch (e: Exception) {
            Log.e("InventoryRepository", "Error updating item location: ${e.localizedMessage}")
            throw e
        }
    }


    override suspend fun addInventoryItem(item: InventoryItem, imageUri: Uri?) {
        try {
            // Get the current user's UID
            val userId = firebaseAuth.currentUser?.uid ?: throw Exception("User not authenticated")

            // Initialize variables
            var imageUrl: String? = null

            // Upload image if available
            imageUri?.let {
                imageUrl = uploadImageToFirebaseStorage(userId, it)
            }

            // Update the item with the image URL (if it was uploaded)
            val itemWithImage = item.copy(imageUrl = imageUrl)

            // Define the collection path and use UID as document ID for user isolation
            val inventoryDocument = firestore.collection("inventory").document(userId)

            // Add item data to Firestore under the user's document
            inventoryDocument.collection("items").add(itemWithImage).await()

        } catch (e: Exception) {
            // Handle any exceptions here (e.g., log or rethrow)
            throw e
        }
    }

    private suspend fun uploadImageToFirebaseStorage(userId: String, imageUri: Uri): String {
        // Define the image reference in Firebase Storage
        val imageRef =
            firebaseStorage.reference.child("inventory_images/$userId/${imageUri.lastPathSegment}")

        // Upload the image and await completion
        imageRef.putFile(imageUri).await()

        // Retrieve and return the image URL
        return imageRef.downloadUrl.await().toString()
    }

    override suspend fun clearAllItems() {
        try {
            // Ensure the user is authenticated
            val userId = firebaseAuth.currentUser?.uid ?: throw Exception("User not authenticated")

            // Reference to the user's items collection
            val itemsCollection =
                firestore.collection("inventory").document(userId).collection("items")

            // Fetch all items in the collection
            val itemsSnapshot = itemsCollection.get().await()

            // Use a WriteBatch to delete all items in a single transaction
            val batch = firestore.batch()
            itemsSnapshot.documents.forEach { document ->
                batch.delete(document.reference)
            }

            // Commit the batch deletion
            batch.commit().await()

        } catch (e: Exception) {
            Log.e("InventoryRepository", "Error clearing items: ${e.localizedMessage}")
            throw e
        }
    }


}
