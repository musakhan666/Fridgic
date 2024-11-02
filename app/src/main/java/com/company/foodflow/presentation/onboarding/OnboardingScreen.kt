package com.company.foodflow.presentation.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.company.foodflow.R
import com.company.foodflow.presentation.main.Graph
import com.company.foodflow.ui.theme.AppTypography
import com.company.foodflow.utills.AppStatusManager

@Composable
fun OnboardingScreen(openAndPopUp: (String) -> Unit) {
    val activeMessageIndex = remember { mutableIntStateOf(1) }
    val context = LocalContext.current
    val appStatus = AppStatusManager(context = context)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.bg_main_screen)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Image Section
        OnboardingContentImage(activeMessageIndex.intValue)

        // Title and Text Instructions
        OnboardingTextContent(activeMessageIndex.intValue)

        // Dot Indicators for each message
        OnboardingDotIndicators(activeMessageIndex.intValue) { index ->
            activeMessageIndex.intValue = index
        }

        // Navigation Buttons
        OnboardingNavigationButtons(activeMessageIndex, {
            appStatus.setTutorialShown()
            openAndPopUp.invoke(Graph.INVENTORY)
        })
    }
}

@Composable
fun OnboardingContentImage(activeMessageIndex: Int) {
    Image(
        painter = when (activeMessageIndex) {
            1 -> painterResource(id = R.drawable.ic_logo)
            2 -> painterResource(id = R.drawable.ic_logo)
            3 -> painterResource(id = R.drawable.ic_logo)
            else -> painterResource(id = R.drawable.ic_logo)
        }, contentDescription = null, modifier = Modifier
            .size(200.dp)
            .padding(16.dp)
    )
}

@Composable
fun OnboardingTextContent(activeMessageIndex: Int) {
    val title = when (activeMessageIndex) {
        1 -> stringResource(id = R.string.onboarding_title_1)
        2 -> stringResource(id = R.string.onboarding_title_2)
        3 -> stringResource(id = R.string.onboarding_title_3)
        else -> stringResource(id = R.string.onboarding_title_1)
    }

    val description = when (activeMessageIndex) {
        1 -> stringResource(id = R.string.onboarding_message_1)
        2 -> stringResource(id = R.string.onboarding_message_2)
        3 -> stringResource(id = R.string.onboarding_message3)
        else -> stringResource(id = R.string.onboarding_message_1)
    }

    // Title Text with Wrapping
    Text(
        text = title,
        style = AppTypography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            lineHeight = 35.sp
        ),
        textAlign = TextAlign.Center,
        maxLines = 2, // Restrict to a maximum of two lines
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(0.8f) // Restrict width to force wrapping
    )

    // Description Text
    Text(
        text = description,
        style = AppTypography.bodyLarge.copy(fontSize = 16.sp),
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
        color = colorResource(id = R.color.title_text)
    )
}


@Composable
fun OnboardingDotIndicators(activeMessageIndex: Int, onDotClick: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        for (i in 1..3) {
            Icon(
                imageVector = Icons.Filled.Circle,
                contentDescription = null,
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(if (activeMessageIndex == i) 12.dp else 8.dp)
                    .clickable { onDotClick(i) },
                tint = if (activeMessageIndex == i)
                    colorResource(id = R.color.theme_color) else
                    colorResource(id = R.color.title_text).copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
fun OnboardingNavigationButtons(
    activeMessageIndex: MutableState<Int>,
    onFinish: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Skip Button, only visible if it's not the last screen
        if (activeMessageIndex.value < 3) {
            OutlinedButton(
                onClick = {
                    onFinish()
                },
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = colorResource(id = R.color.theme_color_light)
                ),
                border = BorderStroke(
                    2.dp,
                    colorResource(id = R.color.theme_color_light)
                ), // Border with theme color
                shape = RoundedCornerShape(50), // Rounded corners
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.skip),
                    style = AppTypography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                )
            }
        }


        // Next or Let's Start Button
        Button(
            onClick = {
                if (activeMessageIndex.value == 3) {
                    onFinish()
                } else activeMessageIndex.value++
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.theme_color),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(fraction = if (activeMessageIndex.value == 3) 1f else 0.4f) // Adjust width
        ) {
            Text(
                text = if (activeMessageIndex.value == 3) stringResource(id = R.string.lets_start) else stringResource(
                    id = R.string.next
                ),
                style = AppTypography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = if (activeMessageIndex.value == 3) 18.sp else 16.sp
                )
            )
        }
    }
}

