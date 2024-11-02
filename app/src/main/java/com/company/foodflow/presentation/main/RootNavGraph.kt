package com.company.foodflow.presentation.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.company.AppState
import com.company.foodflow.presentation.onboarding.OnboardingScreen
import com.company.foodflow.utills.AppStatusManager
import com.company.foodflow.R
import com.company.foodflow.presentation.about.AboutAppScreen
import com.company.foodflow.presentation.about.HelpCenterScreen
import com.company.foodflow.presentation.grocery.GroceryScreen
import com.company.foodflow.presentation.inventory.InventoryScreen
import com.company.foodflow.presentation.inventory.InventoryViewModel
import com.company.foodflow.presentation.login.LoginScreen
import com.company.foodflow.presentation.login.LoginViewModel
import com.company.foodflow.presentation.meal.MealDetailScreen
import com.company.foodflow.presentation.meal.MealsScreen
import com.company.foodflow.presentation.meal.MealsViewModel
import com.company.foodflow.presentation.new_item.AddNewItemScreen
import com.company.foodflow.presentation.profile.ProfileScreen
import com.company.foodflow.presentation.signup.CreateAccountScreen
import com.company.foodflow.presentation.signup.AccountViewModel

@Composable
fun RootNavigationGraph() {
    val context = LocalContext.current
    val settings = AppStatusManager(context)
    val hasShownTutorial = settings.hasShownTutorial()
    val isLoggedIn = settings.isLoggedIn()

    // Determine the start destination
    val startDestination = when {
        !hasShownTutorial -> Graph.TUTORIAL
        isLoggedIn -> Graph.INVENTORY
        else -> Graph.LOGIN
    }

    val appState = rememberAppState()
    val navController = appState.navController
    val currentBackStackEntry = navController.currentBackStackEntryAsState().value
    val currentRoute = currentBackStackEntry?.destination?.route

    Scaffold(modifier = Modifier.fillMaxSize(), bottomBar = {
        if (currentRoute in listOf(
                Graph.INVENTORY, Graph.MEALS, Graph.GROCERY, Graph.PROFILE
            )
        ) {
            BottomBar(navController)
        }
    }) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(paddingValues)
        ) {
            AppGraph(appState)
        }
    }
}


@Composable
fun BottomBar(navController: NavHostController) {
    BottomNavigation(
        backgroundColor = Color.White, contentColor = colorResource(id = R.color.theme_color_light)
    ) {
        BottomNavigationItem(icon = {
            Icon(
                Icons.Filled.List,
                contentDescription = stringResource(id = R.string.label_inventory),
                tint = if (navController.currentDestination?.route == Graph.INVENTORY) colorResource(
                    id = R.color.theme_color_light
                ) else Color.LightGray
            )
        },
            label = {
                Text(
                    stringResource(id = R.string.label_inventory),
                    color = if (navController.currentDestination?.route == Graph.INVENTORY) colorResource(
                        id = R.color.theme_color_light
                    ) else Color.LightGray
                )
            },
            selected = navController.currentDestination?.route == Graph.INVENTORY,
            onClick = { navController.navigate(Graph.INVENTORY) })
        BottomNavigationItem(icon = {
            Icon(
                Icons.Filled.RestaurantMenu,
                contentDescription = stringResource(id = R.string.label_meals),
                tint = if (navController.currentDestination?.route == Graph.MEALS) colorResource(
                    id = R.color.theme_color_light
                ) else Color.LightGray
            )
        },
            label = {
                Text(
                    stringResource(id = R.string.label_meals),
                    color = if (navController.currentDestination?.route == Graph.MEALS) colorResource(
                        id = R.color.theme_color_light
                    ) else Color.LightGray
                )
            },
            selected = navController.currentDestination?.route == Graph.MEALS,
            onClick = { navController.navigate(Graph.MEALS) })
        BottomNavigationItem(icon = {
            Icon(
                Icons.Filled.ShoppingCart,
                contentDescription = stringResource(id = R.string.label_grocery),
                tint = if (navController.currentDestination?.route == Graph.GROCERY) colorResource(
                    id = R.color.theme_color_light
                ) else Color.LightGray
            )
        },
            label = {
                Text(
                    stringResource(id = R.string.label_grocery),
                    color = if (navController.currentDestination?.route == Graph.GROCERY) colorResource(
                        id = R.color.theme_color_light
                    ) else Color.LightGray
                )
            },
            selected = navController.currentDestination?.route == Graph.GROCERY,
            onClick = { navController.navigate(Graph.GROCERY) })
        BottomNavigationItem(icon = {
            Icon(
                Icons.Filled.Person,
                contentDescription = stringResource(id = R.string.label_profile),
                tint = if (navController.currentDestination?.route == Graph.PROFILE) colorResource(
                    id = R.color.theme_color_light
                ) else Color.LightGray
            )
        },
            label = {
                Text(
                    stringResource(id = R.string.label_profile),
                    color = if (navController.currentDestination?.route == Graph.PROFILE) colorResource(
                        id = R.color.theme_color_light
                    ) else Color.LightGray
                )
            },
            selected = navController.currentDestination?.route == Graph.PROFILE,
            onClick = { navController.navigate(Graph.PROFILE) })
    }
}


private fun NavGraphBuilder.AppGraph(
    appState: AppState,
) {

    composable(Graph.INVENTORY) {
        val viewModel: InventoryViewModel = hiltViewModel()

        InventoryScreen(
            viewModel = viewModel,
            openAndPopUp = { route -> appState.clearAndNavigate(route) })
    }
    composable(Graph.LOGIN) {
        val viewModel: LoginViewModel = hiltViewModel()
        LoginScreen(loginViewModel = viewModel, openAndPopUp = { route ->
            appState.clearAndNavigate(route)
        })
    }
    composable(Graph.SIGNUP) {
        val viewModel: AccountViewModel = hiltViewModel()
        CreateAccountScreen(viewModel = viewModel, openAndPopUp = { route ->
            appState.clearAndNavigate(route)
        })
    }
    composable(Graph.MEALS) {
        MealsScreen { mealId ->
            appState.navigate(Graph.mealDetailRoute(mealId)) // Use helper function to create the route

        }
    }
    composable(
        route = Graph.MEALS_DETAIL,
        arguments = listOf(navArgument("mealId") { type = NavType.StringType }) // Define mealId as a String argument
    ) { backStackEntry ->
        val mealId = backStackEntry.arguments?.getString("mealId")
        val viewModel: MealsViewModel = hiltViewModel()

        // Use mealId to get the meal details
        val meal = viewModel.getMealById(mealId)

        meal?.let { MealDetailScreen(meal = it, onBackPressed = { appState.popUp() }) }
    }

    composable(Graph.ADD_NEW_ITEM) {
        val viewModel: InventoryViewModel = hiltViewModel()
        AddNewItemScreen(viewModel, onBackPressed = { appState.popUp() })
    }
    composable(Graph.GROCERY) {
        val viewModel: InventoryViewModel = hiltViewModel()

        GroceryScreen(viewModel) { route ->
            appState.navigate(route)
        }
    }
    composable(Graph.HELP_CENTER) {
        HelpCenterScreen { appState.popUp() }
    }
    composable(Graph.ABOUT) {

        AboutAppScreen { appState.popUp() }
    }

    composable(Graph.PROFILE) {
        val viewModel: AccountViewModel = hiltViewModel()
        ProfileScreen(viewModel = viewModel, openAndPopUp = { route ->
            appState.clearAndNavigate(route)
        }, navigate = { route ->
            appState.navigate(route)
        })
    }
    composable(Graph.TUTORIAL) {
        OnboardingScreen(openAndPopUp = { route ->
            appState.clearAndNavigate(route)
        })
    }
}

@Composable
fun rememberAppState(
    navController: NavHostController = rememberNavController(),
) = remember(navController) {
    AppState(navController)
}
