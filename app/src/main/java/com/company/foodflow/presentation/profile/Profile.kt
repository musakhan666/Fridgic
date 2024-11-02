package com.company.foodflow.presentation.profile

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.company.foodflow.presentation.signup.AccountViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: AccountViewModel, openAndPopUp: (String) -> Unit, navigate: (String) -> Unit
) {
    val userName by viewModel.userName.collectAsState(initial = "")
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            viewModel.passwordResetEvent.collectLatest { isSuccess ->
                val message = if (isSuccess) {
                    context.getString(R.string.password_reset_success)
                } else {
                    context.getString(R.string.password_reset_failure)
                }
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.app_name_with_logo),
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(bottom = 20.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = colorResource(R.color.theme_color))
        )

        Spacer(modifier = Modifier.height(16.dp))

        Image(
            painter = painterResource(R.drawable.ic_profile_pic),
            contentDescription = stringResource(R.string.profile_image),
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = userName,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ProfileOptionItem(
                    iconRes = R.drawable.ic_key,
                    text = stringResource(R.string.change_password),
                    onClick = {
                        viewModel.changePassword()
                    }
                )

                Divider(color = Color.LightGray, thickness = 0.5.dp)

                ProfileOptionItem(
                    iconRes = R.drawable.ic_about,
                    text = stringResource(R.string.about_app),
                    onClick = { navigate.invoke(Graph.ABOUT) }
                )

                Divider(color = Color.LightGray, thickness = 0.5.dp)

                ProfileOptionItem(
                    iconRes = R.drawable.ic_help,
                    text = stringResource(R.string.help_center),
                    onClick = { navigate.invoke(Graph.HELP_CENTER) }
                )

                Divider(color = Color.LightGray, thickness = 0.5.dp)

                ProfileOptionItem(
                    iconRes = R.drawable.ic_logout,
                    text = "Logout",
                    onClick = {
                        viewModel.logout {
                            Toast.makeText(
                                context,
                                "Successfully logged out",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        openAndPopUp.invoke(Graph.LOGIN)

                    }
                )
            }
        }
    }
}

@Composable
fun ProfileOptionItem(
    iconRes: Int,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = text,
            tint = colorResource(R.color.theme_color),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            fontSize = 16.sp,
            color = Color.Black
        )
    }
}