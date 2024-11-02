package com.company.foodflow.presentation.main

object Graph {
    const val ROOT = "root_graph"
    const val LOGIN = "login"
    const val SIGNUP = "sign_up"
    const val TUTORIAL = "tutorial_graph"
    const val INVENTORY = "inventory_graph"
    const val MEALS = "meals_graph"
    const val MEALS_DETAIL = "meals_detail/{mealId}" // Add mealId as a path argument

    const val GROCERY = "grocery_graph"
    const val PROFILE = "profile_graph"
    const val ABOUT = "about"
    const val HELP_CENTER = "help"
    const val ADD_NEW_ITEM = "add_new_item"

    fun mealDetailRoute(mealId: String) = "meals_detail/$mealId" // Helper function to create the route

}
