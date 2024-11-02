package com.company.foodflow.domain

import android.net.Uri
import com.company.foodflow.data.model.InventoryItem
import com.company.foodflow.presentation.inventory.SortOrder
import kotlinx.coroutines.flow.Flow

interface InventoryRepository {
    fun getInventoryItems(category: String, sortOrder: SortOrder): Flow<List<InventoryItem>>
    suspend fun addInventoryItem(item: InventoryItem, imageUri: Uri?)
    suspend fun clearAllItems()
    suspend fun updateItemLocation(item: InventoryItem, location: String)
    suspend fun deleteItem(item: InventoryItem)


}
