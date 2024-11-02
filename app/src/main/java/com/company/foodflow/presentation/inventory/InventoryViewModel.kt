package com.company.foodflow.presentation.inventory

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.company.foodflow.data.model.InventoryItem
import com.company.foodflow.domain.InventoryRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val repository: InventoryRepository,
    private val auth: FirebaseAuth
) : ViewModel() {
    val isLoading = mutableStateOf(false) // Loader state

    private val _inventoryItems = MutableStateFlow<List<InventoryItem>>(emptyList())
    val inventoryItems: StateFlow<List<InventoryItem>> = _inventoryItems.asStateFlow()

    private val _groceryItems = MutableStateFlow<List<InventoryItem>>(emptyList())
    val groceryItems: StateFlow<List<InventoryItem>> = _groceryItems.asStateFlow()

    private val _userName = MutableStateFlow<String>("")
    val userName: StateFlow<String> = _userName.asStateFlow()
    var searchQuery = mutableStateOf("")

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _sortOrder = MutableStateFlow(SortOrder.BY_NAME)
    val sortOrder: StateFlow<SortOrder> = _sortOrder.asStateFlow()

    private val _showWarningModal = MutableStateFlow<Boolean>(false)
    val showWarningModal: StateFlow<Boolean> = _showWarningModal.asStateFlow()

    var duplicateItem: InventoryItem? = null

    private val _selectedItems = MutableStateFlow<List<InventoryItem>>(emptyList())
    val selectedItems: StateFlow<List<InventoryItem>> = _selectedItems.asStateFlow()

    // Function to clear selected items list after transfer
    fun clearSelectedItems() {
        _selectedItems.value = emptyList()
        _groceryItems.value = emptyList()

        fetchGroceryItems()
    }

    // Confirms adding a duplicate item to the grocery list
    // User confirms to add the duplicate item
    fun confirmAddDuplicateItem(onSuccess: () -> Unit) {
        duplicateItem?.let { item ->
            viewModelScope.launch {
                isLoading.value = true
                try {
                    repository.addInventoryItem(
                        item,
                        imageUri = null
                    ) // Assuming no additional image
                    onSuccess() // Notify the success callback
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    isLoading.value = false
                    _showWarningModal.value = false // Hide the warning modal
                    duplicateItem = null // Clear duplicate item reference
                }
            }
        }
    }

    // User cancels adding the duplicate item
    fun cancelAddDuplicateItem() {
        _showWarningModal.value = false // Hide warning modal
        duplicateItem = null // Clear duplicate item reference
    }


    // Fetches grocery items with selected category and sort order
    fun fetchGroceryItems() {
        viewModelScope.launch {
            repository.getInventoryItems(selectedCategory.value, sortOrder.value)
                .catch {
                    _groceryItems.value = emptyList() // Handle error
                }
                .collect { items ->
                    Log.d("InventoryViewModel", "Fetched items: $items")

                    _groceryItems.value = filterItemsByQuery(items, searchQuery.value)
                }
        }
    }

    fun toggleItemSelected(item: InventoryItem) {
        _selectedItems.value = if (_selectedItems.value.contains(item)) {
            _selectedItems.value - item
        } else {
            _selectedItems.value + item
        }
    }

    fun clearAllItems(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                repository.clearAllItems() // Clear all items in Firestore
                _groceryItems.value = emptyList() // Update local state to reflect an empty list
                onSuccess.invoke()
            } catch (e: Exception) {
                Log.e("GroceryViewModel", "Error clearing items: ${e.localizedMessage}")
            }
        }
    }

    fun transferSelectedItems(location: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                // Implement the logic to transfer selected items to the specified location
                _selectedItems.value.forEach { item ->
                    // Update each item with the new location (update Firestore)
                    repository.updateItemLocation(item, location)
                }
                clearSelectedItems()
                onSuccess.invoke()
            } catch (e: Exception) {
                Log.e("GroceryViewModel", "Error transferring items: ${e.localizedMessage}")
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        searchQuery.value = query
        fetchInventoryItems()

    }

    fun onSearchQueryChangedGrocery(query: String) {
        searchQuery.value = query
        fetchGroceryItems()

    }


    fun addInventoryItem(
        item: InventoryItem,
        imageUri: Uri?,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            isLoading.value = true // Show loader while checking and adding item

            try {
                // Check for duplicates before adding the item
                val existingItems = repository.getInventoryItems("All", SortOrder.BY_NAME).first()
                val duplicateExists =
                    existingItems.any { it.name.equals(item.name, ignoreCase = true) }

                if (duplicateExists) {
                    // Duplicate found: set the duplicate item and show warning modal
                    duplicateItem = item
                    _showWarningModal.value = true
                } else {
                    // No duplicate found, proceed to add the item
                    repository.addInventoryItem(item, imageUri)
                    onSuccess() // Call onSuccess callback to notify of successful addition
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading.value = false // Hide loader
            }
        }
    }


    init {
        fetchUserName()
        fetchInventoryItems()
        fetchGroceryItems()
    }

    fun fetchInventoryItems() {

        viewModelScope.launch {
            repository.getInventoryItems(selectedCategory.value, sortOrder.value)
                .catch {
                    _inventoryItems.value = emptyList() // Handle error
                }
                .collect { items ->
                    Log.d("InventoryViewModel", "Fetched Inv items: $items")

                    _inventoryItems.value = filterItemsByQuery(items, searchQuery.value)
                }
        }
    }

    private fun filterItemsByQuery(items: List<InventoryItem>, query: String): List<InventoryItem> {
        return if (query.isBlank()) items
        else items.filter { it.name.contains(query, ignoreCase = true) }
    }

    private fun fetchUserName() {
        val user = auth.currentUser
        _userName.value = user?.displayName ?: "User"
    }


    fun onCategorySelected(category: String) {
        _selectedCategory.value = category
        fetchInventoryItems()
    }

    fun onCategoryCategorySelected(category: String) {
        _selectedCategory.value = category
        fetchGroceryItems()
    }

    fun changeSortOrder(newOrder: SortOrder) {
        _sortOrder.value = newOrder
        fetchInventoryItems()
    }

    fun deleteSelectedItems() {
        viewModelScope.launch {
            val itemsToDelete = _selectedItems.value
            itemsToDelete.forEach { item ->
                repository.deleteItem(item)
            }
            // Clear selection and refresh items
            clearSelectedItems() // Clear selection after transfer
        }
    }
}

enum class SortOrder {
    BY_NAME, BY_EXPIRATION, BY_QUANTITY, DAYS_REMAINING, ALPHABETICAL
}
