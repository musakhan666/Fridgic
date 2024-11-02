package com.company.foodflow.presentation.signup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.company.foodflow.R
import com.company.foodflow.presentation.main.Graph
import com.company.foodflow.ui.theme.CustomFontFamily

@Composable
fun CreateAccountScreen(
    viewModel: AccountViewModel = hiltViewModel(),
    openAndPopUp: (String) -> Unit
) {
    val accountCreationState by viewModel.accountCreationState.collectAsState()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isTermsAccepted by remember { mutableStateOf(false) }

    // UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        // Title with Icon
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.ic_fridge), // Replace with actual fridge icon resource
                contentDescription = null,
                tint = colorResource(R.color.theme_color),
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.create_account_title),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = CustomFontFamily,
                color = colorResource(R.color.theme_color),
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Name Field
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(stringResource(R.string.name_label)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Email Field
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(R.string.email_label)) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password Field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(R.string.password_label)) },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Terms and Conditions with Annotated String
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            Checkbox(
                checked = isTermsAccepted,
                onCheckedChange = { isTermsAccepted = it },
                colors = CheckboxDefaults.colors(checkmarkColor = Color(0xFF004080))
            )
            ClickableText(
                text = buildAnnotatedString {
                    append("I have read and agree to the ")

                    // Apply color and styling to terms part
                    pushStyle(
                        SpanStyle(
                            color = colorResource(id = R.color.theme_color),
                            fontWeight = FontWeight.Bold
                        )
                    )
                    append("Terms of Use, Community Guidelines and Privacy Policy.")
                    pop()
                },
                modifier = Modifier.padding(top = 15.dp),
                style = LocalTextStyle.current.copy(fontSize = 14.sp, color = Color.Black),
                onClick = { /* Handle click on terms text if necessary */ }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Create Account Button
        Button(
            onClick = {
                if (isTermsAccepted) {
                    viewModel.createAccount(name, email, password)
                }
            },
            enabled = accountCreationState !is CreateAccountState.Loading,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF004080)),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(50.dp)
        ) {
            if (accountCreationState is CreateAccountState.Loading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text(text = stringResource(R.string.create_account_button), color = Color.White)
            }
        }

        // Handle different states for success and error
        when (accountCreationState) {
            is CreateAccountState.Error -> {
                val error = (accountCreationState as CreateAccountState.Error).message
                Text(text = error, color = Color.Red, modifier = Modifier.padding(top = 16.dp))
            }

            is CreateAccountState.Success -> {
                openAndPopUp.invoke(Graph.INVENTORY)
            }

            else -> {}
        }
    }
}
