package com.company.foodflow.presentation.login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.company.foodflow.R
import com.company.foodflow.presentation.main.Graph
import com.company.foodflow.ui.theme.AppTypography
import com.company.foodflow.ui.theme.CustomFontFamily

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel,
    openAndPopUp: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val loginState by loginViewModel.loginState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        // Title with Icon
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(id = R.string.login_title_part1),
                fontSize = 35.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = CustomFontFamily,
                color = colorResource(R.color.theme_color)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                painter = painterResource(id = R.drawable.ic_fridge),
                contentDescription = stringResource(id = R.string.app_name),
                modifier = Modifier.size(32.dp),
                tint = colorResource(R.color.theme_color)
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = stringResource(id = R.string.login_title_part2),
            fontSize = 35.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = CustomFontFamily,
            color = colorResource(R.color.theme_color)
        )
        // Subtitle
        Text(
            text = stringResource(id = R.string.login_subtitle),
            color = colorResource(R.color.theme_color).copy(alpha = 0.8f),
            style = AppTypography.bodyLarge.copy(
                fontSize = 24.sp,
                fontFamily = CustomFontFamily,
                fontWeight = FontWeight.Bold,
                lineHeight = 32.sp
            ),
            modifier = Modifier.padding(top = 15.dp, bottom = 40.dp)
        )

        // Email Text Field
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(id = R.string.email_label)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)
        )

        // Password Text Field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(id = R.string.password_label)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password)
        )
        // Display error message if login fails or validation error occurs
        if (loginState is LoginState.Error) {
            Text(
                text = (loginState as LoginState.Error).message,
                color = Color.Red,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // Log In Button
        Button(
            onClick = {
                loginViewModel.onLoginClick(
                    email = email,
                    password = password,
                    onSuccessNavigate = { openAndPopUp(Graph.INVENTORY) }
                )
            },
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.theme_color)),
            shape = RoundedCornerShape(50.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .height(48.dp)
        ) {
            if (loginState is LoginState.Loading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text(stringResource(id = R.string.login_button), color = Color.White)
            }
        }


        // Create Account Button
        OutlinedButton(
            onClick = { openAndPopUp.invoke(Graph.SIGNUP) },
            colors = ButtonDefaults.outlinedButtonColors(contentColor = colorResource(R.color.theme_color)),
            shape = RoundedCornerShape(50.dp), // Make button fully rounded
            border = BorderStroke(1.dp, colorResource(R.color.theme_color)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
                .height(48.dp) // Standard height for round buttons
        ) {
            Text(stringResource(id = R.string.create_account_button))
        }

        // Divider with "Or"
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Divider(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(id = R.string.or_text),
                modifier = Modifier.padding(horizontal = 8.dp),
                color = Color.Gray,
                fontSize = 14.sp
            )
            Divider(modifier = Modifier.weight(1f))
        }

        // Social Login Buttons
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = loginViewModel::onGoogleSignInClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_google), // Replace with Google icon
                    contentDescription = stringResource(id = R.string.google_sign_in),
                    tint = colorResource(R.color.theme_color),
                    modifier = Modifier.size(48.dp) // Make social icons round
                )
            }
        }
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}
