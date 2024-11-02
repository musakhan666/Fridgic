package com.company.foodflow.presentation.inventory

import androidx.compose.foundation.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.company.foodflow.R
import com.company.foodflow.data.model.InventoryItem
import com.company.foodflow.ui.theme.CustomFontFamily

@Composable
fun InventoryScreen(
    viewModel: InventoryViewModel,
    openAndPopUp: (String) -> Unit
) {
    val userName by viewModel.userName.collectAsState()
    val inventoryItems by viewModel.inventoryItems.collectAsState()
    val sortOrder by viewModel.sortOrder.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val searchQuery by viewModel.searchQuery



    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.theme_color))
    ) {
        // Search bar
        SearchBar(
            query = searchQuery,
            onQueryChanged = viewModel::onSearchQueryChanged
        )

        // Greeting and Subtitle
        GreetingSection(name = userName)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            InventoryTabs(
                selectedTab = selectedCategory,
                onTabSelected = { category -> viewModel.onCategorySelected(category) },
                Modifier.weight(0.9f)
            )
            SortMenu(
                sortOrder = sortOrder,
                onSortSelected = { newOrder -> viewModel.changeSortOrder(newOrder) },
                Modifier.weight(0.1f)
            )
        }

        ExpiringItemsList(items = inventoryItems)
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChanged: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = { onQueryChanged(it) },
        placeholder = {
            Text(
                text = "Search items and recipes",
                color = Color.Gray
            )
        },
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_search), // Replace with your search icon resource
                contentDescription = "Search Icon",
                tint = Color.Gray // Icon color
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(56.dp),
        shape = RoundedCornerShape(50), // Fully rounded for pill shape
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        ),
        singleLine = true
    )
}

@Composable
fun GreetingSection(name: String) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
            text = "Hello, $name",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = CustomFontFamily,
            color = colorResource(R.color.white)
        )
        Text(
            text = "The following items are expiring soon.",
            fontSize = 16.sp,
            color = Color.White,
            modifier = Modifier.padding(top = 10.dp)
        )
    }
}

@Composable
fun SortMenu(
    sortOrder: SortOrder,
    onSortSelected: (SortOrder) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier.wrapContentSize(Alignment.TopEnd)) {
        IconButton(onClick = { expanded = true }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_sort),
                contentDescription = "Sort",
                tint = Color.White,
                modifier = Modifier
                    .padding(start = 10.dp)
                    .size(24.dp)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Days Remaining") },
                onClick = {
                    onSortSelected(SortOrder.DAYS_REMAINING)
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("Alphabetical") },
                onClick = {
                    onSortSelected(SortOrder.ALPHABETICAL)
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("By Name") },
                onClick = {
                    onSortSelected(SortOrder.BY_NAME)
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("By Expiration Date") },
                onClick = {
                    onSortSelected(SortOrder.BY_EXPIRATION)
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("By Quantity") },
                onClick = {
                    onSortSelected(SortOrder.BY_QUANTITY)
                    expanded = false
                }
            )
        }
    }
}


@Composable
fun InventoryTabs(
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    modifier: Modifier
) {
    val tabs = listOf("All", "Fridge", "Freezer", "Pantry")

    TabRow(
        selectedTabIndex = tabs.indexOf(selectedTab),
        modifier = modifier.padding(vertical = 8.dp),
        containerColor = Color.Transparent,
        contentColor = colorResource(R.color.white),
        indicator = { tabPositions ->
            if (selectedTab in tabs) {
                TabRowDefaults.Indicator(
                    Modifier.tabIndicatorOffset(tabPositions[tabs.indexOf(selectedTab)]),
                    color = Color.White
                )
            }
        },
        divider = {}
    ) {
        tabs.forEach { title ->
            Tab(
                selected = selectedTab == title,
                onClick = { onTabSelected(title) },
                text = { Text(title) }
            )
        }
    }
}

@Composable
fun ExpiringItemsList(items: List<InventoryItem>) {
    if (items.isEmpty()) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White)
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.no_items),
                color = Color.Gray,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White)
        ) {
            items(items.size) { index ->
                ExpiringItemRow(item = items[index])
            }
        }
    }
}


@Composable
fun ExpiringItemRow(item: InventoryItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            ) {
                Image(
                    painter = rememberImagePainter(
                        data = item.imageUrl,
                    ),
                    contentScale = ContentScale.FillBounds,
                    contentDescription = item.name,
                    modifier = Modifier
                        .padding(1.dp)
                        .size(55.dp)
                )
            }


            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.theme_color)
                )
                Text(
                    text = item.location,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                val daysText =
                    if (item.daysRemaining < 0) "Expired" else "Expires in ${item.daysRemaining} days"

                Text(
                    text = daysText,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )

                // Expiration progress bar
                LinearProgressIndicator(
                    progress = item.getExpirationProgress(),
                    color = item.expirationColor,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )
            }
        }
    }
}

