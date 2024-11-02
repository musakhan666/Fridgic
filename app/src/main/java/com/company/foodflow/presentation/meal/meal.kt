package com.company.foodflow.presentation.meal


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Text
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberImagePainter
import com.company.foodflow.data.model.Meal
import com.company.foodflow.presentation.inventory.SearchBar
import com.company.foodflow.R

@Composable
fun MealsScreen(
    viewModel: MealsViewModel = hiltViewModel(),
    onMealClick: (String) -> Unit // Pass mealId as a parameter
) {
    val meals by viewModel.meals.collectAsState()
    val categories = listOf("All", "High protein", "Vegan", "Halal")
    var selectedCategory by remember { mutableStateOf("All") }
    val searchQuery by viewModel.searchQuery.collectAsState()
    var selectedCategoryIndex by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(R.color.white))
    ) {
        // Search Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(colorResource(R.color.theme_color))
                .padding(vertical = 16.dp)
        ) {
            SearchBar(query = searchQuery, onQueryChanged = viewModel::searchMeals)
        }

        // Category Tabs
        TabRow(
            selectedTabIndex = selectedCategoryIndex,
            modifier = Modifier.fillMaxWidth(),
            containerColor = Color.White,
            contentColor = Color.Blue,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedCategoryIndex]),
                    color = Color.Blue
                )
            }
        ) {
            categories.forEachIndexed { index, category ->
                Tab(
                    selected = selectedCategoryIndex == index,
                    onClick = {
                        selectedCategoryIndex = index
                        viewModel.filterMealsByCategory(categories[selectedCategoryIndex]) // Update meals based on category
                    },
                    text = {
                        Text(
                            text = category,
                            fontSize = 14.sp,
                            color = if (selectedCategoryIndex == index) Color.Blue else Color.Gray
                        )
                    }
                )
            }
        }

        // Meal Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.padding(16.dp),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val items =
                meals.filter { it.category == selectedCategory || selectedCategory == "All" }
            items(items.size) { index ->
                val meal = items[index]
                MealItem(meal = meal, onClick = { onMealClick(meal.id) })
            }
        }
    }
}

@Composable
fun MealItem(meal: Meal, onClick: () -> Unit) {
    Column(
        modifier = Modifier.clickable { onClick() }
    ) {
        Image(
            painter = rememberImagePainter(data = meal.imageUrl),
            contentDescription = meal.name,
            modifier = Modifier.size(100.dp)
        )
        Text(text = meal.name, fontWeight = FontWeight.Bold)
        Text(text = "${meal.time} min", color = Color.Gray)
        Text(
            text = "${meal.ingredientsUsed}/${meal.totalIngredients} ingredients",
            color = Color.Gray
        )
    }
}
