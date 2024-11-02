package com.company.foodflow.presentation.meal

import android.util.Log
import androidx.lifecycle.ViewModel
import com.company.foodflow.data.model.Meal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


@HiltViewModel
class MealsViewModel @Inject constructor() : ViewModel() {

    // Original list of meals
    private val backupMeals = listOf(
        Meal(
            id = "1", // Unique ID for Pizza
            name = "Pizza",
            imageUrl = "https://www.cicis.com/content/images/cicis/Jalapeno%20pizza.png",
            category = "High protein",
            time = 30,
            ingredientsUsed = 9,
            totalIngredients = 10,
            tags = listOf("Vegan", "Halal"),
            calories = 900,
            ingredients = listOf("Tomato", "Cheese", "Flour"),
            instructions = "1. Preheat oven to 475°F (245°C).\n" +
                    "2. Roll out the pizza dough on a floured surface.\n" +
                    "3. Spread tomato sauce over the dough.\n" +
                    "4. Sprinkle cheese and add desired toppings.\n" +
                    "5. Bake for 12-15 minutes until crust is golden brown.\n" +
                    "6. Slice and serve hot.",
        ),
        Meal(
            id = "2", // Unique ID for Pasta
            name = "Pasta",
            imageUrl = "https://jow.fr/_next/image?url=https%3A%2F%2Fstatic.jow.fr%2F880x880%2Frecipes%2Fjkk2G8R1Rt.png&w=2560&q=100",
            category = "Vegan",
            time = 60,
            ingredientsUsed = 5,
            totalIngredients = 10,
            tags = listOf("Vegan"),
            calories = 700,
            ingredients = listOf("Pasta", "Tomato Sauce", "Olives"),
            instructions = "1. Boil pasta in salted water for 8-10 minutes.\n" +
                    "2. In a pan, heat tomato sauce and add seasonings.\n" +
                    "3. Drain pasta and add it to the sauce.\n" +
                    "4. Stir in olives and mix well.\n" +
                    "5. Serve hot with a sprinkle of fresh herbs.",

            ),
        Meal(
            id = "3", // Unique ID for Chicken Biryani
            name = "Chicken Biryani",
            imageUrl = "https://static.toiimg.com/thumb/84786366.cms?imgsize=152314&width=800&height=800",
            category = "Halal",
            time = 90,
            ingredientsUsed = 8,
            totalIngredients = 12,
            tags = listOf("Halal", "High protein"),
            calories = 1200,
            ingredients = listOf("Rice", "Chicken", "Spices", "Yogurt"),
            instructions = "1. Marinate chicken with yogurt and spices for 30 minutes.\n" +
                    "2. Cook basmati rice halfway and set aside.\n" +
                    "3. In a large pot, layer marinated chicken and half-cooked rice.\n" +
                    "4. Add saffron milk and ghee over the layers.\n" +
                    "5. Cover and cook on low heat for 40 minutes.\n" +
                    "6. Serve hot garnished with fresh coriander and fried onions.",

            )
    )
    fun getMealById(mealId: String?): Meal? {
        return meals.value.find { it.id == mealId }
    }
    // Current displayed meals
    private val _meals = MutableStateFlow(backupMeals)
    val meals: StateFlow<List<Meal>> = _meals.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    fun filterMealsByCategory(category: String) {
        Log.d("Meals", "Filtering by category: $category")
        _meals.value = if (category == "All") {
            backupMeals // Reset to original data
        } else {
            backupMeals.filter { it.category == category }
        }
    }

    fun searchMeals(query: String) {
        _searchQuery.value = query
        _meals.value = if (query.isBlank()) {
            backupMeals // Show original data if search is blank
        } else {
            backupMeals.filter { it.name.contains(query, ignoreCase = true) }
        }
    }
}
