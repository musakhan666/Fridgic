package com.company.foodflow.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.colorResource
import com.company.foodflow.ui.theme.FoodFlowTheme
import dagger.hilt.android.AndroidEntryPoint
import com.company.foodflow.R

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FoodFlowTheme {
                window.statusBarColor = colorResource(R.color.theme_color).toArgb()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RootNavigationGraph()

                }
            }
        }
    }
}

