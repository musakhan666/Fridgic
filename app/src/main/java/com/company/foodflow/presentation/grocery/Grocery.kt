package com.company.foodflow.presentation.grocery

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberImagePainter
import com.company.foodflow.presentation.inventory.InventoryViewModel
import com.company.foodflow.R
import com.company.foodflow.data.model.InventoryItem
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.company.foodflow.presentation.inventory.SearchBar
import com.company.foodflow.presentation.main.Graph

@Composable
fun GroceryScreen(viewModel: InventoryViewModel = hiltViewModel(), openAndPopup: (String) -> Unit) {
    val groceryItems by viewModel.groceryItems.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val searchQuery by viewModel.searchQuery
    val isLoading by viewModel.isLoading
    val context = LocalContext.current
    val selectedItems by viewModel.selectedItems.collectAsState()
    val showWarningModel by viewModel.showWarningModal.collectAsState()


    LaunchedEffect("fetchItems") {
        viewModel.fetchGroceryItems()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Top Search Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(colorResource(R.color.theme_color))
                .padding(top = 16.dp)
        ) {
            SearchBar(query = searchQuery, onQueryChanged = viewModel::onSearchQueryChangedGrocery)
        }

        // Category Tabs
        CategoryTabs(
            selectedTab = selectedCategory, onTabSelected = viewModel::onCategoryCategorySelected
        )

        if (groceryItems.isEmpty()) {
            // Show "No items" message and "Add New Item" button if no items are present
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.no_items),
                    color = Color.Gray,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { openAndPopup.invoke(Graph.ADD_NEW_ITEM) },
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.theme_color)),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .height(56.dp)
                        .fillMaxWidth(0.8f)
                        .align(Alignment.BottomCenter)
                ) {
                    Text(stringResource(R.string.add_new_item), color = Color.White)
                }
            }
        } else {
            // Grocery Items List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                items(groceryItems.size) { index ->
                    val item = groceryItems[index]
                    GroceryListItem(item = item, onCheckedChange = { isSelected ->
                        viewModel.toggleItemSelected(item)
                    }, onClick = { })
                }
            }
        }

        // Dynamic Bottom Button: "Add New Item", "Transfer", or "Delete Selected"
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            when {
                selectedItems.isEmpty() -> {
                    // Show "Add New Item" Button when no items are selected
                    Button(
                        onClick = { openAndPopup.invoke(Graph.ADD_NEW_ITEM) },
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.theme_color)),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .height(56.dp)
                            .fillMaxWidth(0.8f)
                    ) {
                        Text(stringResource(R.string.add_new_item), color = Color.White)
                    }
                }

                selectedItems.isNotEmpty() -> {
                    // Show "Delete Selected" Button when items are selected
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = {
                                viewModel.deleteSelectedItems()
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.items_deleted),
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier
                                .height(56.dp)
                                .fillMaxWidth(0.8f)
                                .align(Alignment.CenterHorizontally)
                        ) {
                            Text(stringResource(R.string.delete_selected), color = Color.White)
                        }

                        TransferButton(onTransferTo = {
                            viewModel.transferSelectedItems(it) {
                                Toast.makeText(
                                    context,
                                    "Item has been transferred successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
                    }

                }
            }
        }
    }


    // Loading Indicator
    if (isLoading) LoadingIndicator()
}

@Composable
fun TransferButton(
    onTransferTo: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = { onTransferTo("Fridge") },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        ) {
            Text("Move to Fridge", textAlign = TextAlign.Center)
        }
        Button(
            onClick = { onTransferTo("Freezer") },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp)
        ) {
            Text("Move to Freezer", textAlign = TextAlign.Center)
        }
        Button(
            onClick = { onTransferTo("Pantry") },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        ) {
            Text("Move to Pantry", textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun CategoryTabs(
    selectedTab: String, onTabSelected: (String) -> Unit
) {
    val tabs = listOf(
        stringResource(R.string.all),
        stringResource(R.string.fridge),
        stringResource(R.string.freezer),
        stringResource(R.string.pantry)
    )

    TabRow(
        selectedTabIndex = tabs.indexOf(selectedTab),
        modifier = Modifier.padding(bottom = 8.dp),
        containerColor = colorResource(R.color.theme_color),
        contentColor = Color.White,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                Modifier.tabIndicatorOffset(tabPositions[tabs.indexOf(selectedTab)]),
                color = Color.White
            )
        }
    ) {
        tabs.forEach { title ->
            Tab(selected = selectedTab == title, onClick = { onTabSelected(title) }, text = {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (selectedTab == title) Color.White else Color.Gray
                )
            })
        }
    }
}

@Composable
fun LoadingIndicator() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White.copy(alpha = 0.5f))
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = colorResource(R.color.theme_color),
            strokeWidth = 4.dp
        )
    }
}

@Composable
fun GroceryListItem(
    item: InventoryItem, onCheckedChange: (Boolean) -> Unit, onClick: () -> Unit
) {
    var isChecked by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick)
    ) {
        Checkbox(
            checked = isChecked, onCheckedChange = { checked ->
                isChecked = checked
                onCheckedChange(checked)
            }, modifier = Modifier.padding(end = 8.dp)
        )

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.Gray.copy(alpha = 0.2f)), contentAlignment = Alignment.Center
        ) {
            if (item.imageUrl != null) {
                Image(
                    painter = rememberImagePainter(data = item.imageUrl),
                    contentDescription = "${item.name} icon",
                    modifier = Modifier.size(40.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.ic_placeholder),
                    contentDescription = "Placeholder icon",
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.name,
                fontSize = 16.sp,
                color = Color.Black,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = if (item.daysRemaining < 0) stringResource(R.string.expired)
                else stringResource(R.string.storage_time, item.daysRemaining),
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}
