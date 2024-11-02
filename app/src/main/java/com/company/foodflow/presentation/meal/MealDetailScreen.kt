package com.company.foodflow.presentation.meal

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.company.foodflow.data.model.Meal
import com.company.foodflow.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealDetailScreen(
    meal: Meal,
    onBackPressed: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = meal.name, fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack, // Correct icon reference
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Blue)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .background(Color.White)
        ) {
            // Meal Image
            Image(
                painter = rememberImagePainter(data = meal.imageUrl),
                contentDescription = meal.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.Gray, shape = RoundedCornerShape(8.dp))
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Meal Information (Time, Tags, Calories)
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("${meal.time} min", fontSize = 14.sp, color = Color.Gray)
                Text(
                    text = meal.tags.joinToString(" | "),
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text("${meal.calories} kcal", fontSize = 14.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Ingredients and Preparation Tabs
            var selectedTab = remember { mutableStateOf(0) }
            val tabs = listOf("Ingredients", "Preparation")
            TabRow(selectedTabIndex = selectedTab.value) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab.value == index,
                        onClick = { selectedTab.value = index },
                        text = { Text(title) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Ingredients or Preparation content
            if (selectedTab.value == 0) {
                // Ingredients
                Column(modifier = Modifier.padding(8.dp)) {
                    meal.ingredients.forEach { ingredient ->
                        Text(
                            text = "• $ingredient",
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                    }
                }
            } else {
                // Preparation
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = meal.instructions,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Exit Button
            Button(
                onClick = onBackPressed,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(text = stringResource(R.string.exit), color = Color.White, fontSize = 16.sp)
            }
        }
    }
}
