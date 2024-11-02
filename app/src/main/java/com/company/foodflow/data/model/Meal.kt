package com.company.foodflow.data.model

data class Meal(
    val id: String,
    val name: String,
    val imageUrl: String,
    val category: String,
    val time: Int, // Preparation time in minutes
    val ingredientsUsed: Int,
    val totalIngredients: Int,
    val tags: List<String>,
    val calories: Int,
    val ingredients: List<String>,
    val instructions: String
)
