package com.company.foodflow.presentation.new_item

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberImagePainter
import com.company.foodflow.R
import com.company.foodflow.data.model.InventoryItem
import com.company.foodflow.presentation.inventory.InventoryViewModel
import com.company.foodflow.ui.theme.AppTypography
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.DialogProperties
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNewItemScreen(
    viewModel: InventoryViewModel = hiltViewModel(),
    onBackPressed: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("Fridge") }
    var quantity by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val isLoading by viewModel.isLoading
    val context = LocalContext.current
    var selectedDate by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    val showWarningModal by viewModel.showWarningModal.collectAsState()

    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, 7)
    val defaultDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

    if (selectedDate.isEmpty()) {
        selectedDate = defaultDate
    }

    var expirationDate by remember { mutableStateOf(defaultDate) }

    val daysDifference by remember {
        derivedStateOf {
            val currentDate = Calendar.getInstance()
            val expirationCalendar = Calendar.getInstance().apply {
                time = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(expirationDate)
            }
            val differenceInMillis = expirationCalendar.timeInMillis - currentDate.timeInMillis
            TimeUnit.MILLISECONDS.toDays(differenceInMillis).toInt()
        }
    }


    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { imageUri = it }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.add_item_title),
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colorResource(R.color.theme_color))
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                if (imageUri != null) {
                    Image(
                        painter = rememberImagePainter(imageUri),
                        contentDescription = "Selected Image",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .clickable { galleryLauncher.launch("image/*") }
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color.Gray)
                            .clickable { galleryLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "+", fontSize = 32.sp, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    stringResource(R.string.add_to),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LocationOption(stringResource(R.string.freezer), location == "Freezer") {
                        location = "Freezer"
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    LocationOption(stringResource(R.string.pantry), location == "Pantry") {
                        location = "Pantry"
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    LocationOption(stringResource(R.string.fridge), location == "Fridge") {
                        location = "Fridge"
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.name)) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.default_expiration_message, daysDifference),
                    fontSize = 16.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true }
                        .border(1.dp, Color.Gray, shape = MaterialTheme.shapes.small)
                        .padding(16.dp)
                ) {
                    Text(
                        text = if (expirationDate.isNotEmpty()) expirationDate else stringResource(R.string.expiration_date),
                        color = if (expirationDate.isNotEmpty()) Color.Black else Color.Gray,
                        style = AppTypography.bodyMedium
                    )
                }

                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text(stringResource(R.string.amount)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                ) {
                    Button(
                        onClick = {
                            viewModel.addInventoryItem(
                                item = InventoryItem(
                                    name = name,
                                    location = location,
                                    quantity = quantity.toIntOrNull() ?: 0,
                                    expirationDate = expirationDate
                                ),
                                imageUri = imageUri,
                                onSuccess = {
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.item_added_success),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = name.isNotBlank() && quantity.isNotBlank() && expirationDate.isNotBlank()
                    ) {
                        Text(stringResource(R.string.add))
                    }

                    Button(
                        onClick = { onBackPressed.invoke() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.exit))
                    }
                }

                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
                }
            }
        }
    )

    if (showDatePicker) {
        MyDatePickerDialog(onDateSelected = { selectedDate ->
            expirationDate = selectedDate
            showDatePicker = false
        }, onDismiss = { showDatePicker = false })
    }

    // Warning Modal for Duplicates
    if (showWarningModal) {
        WarningModal(
            itemName = viewModel.duplicateItem?.name.orEmpty(),
            onProceed = {
                viewModel.confirmAddDuplicateItem {
                    Toast.makeText(context, "Item added successfully", Toast.LENGTH_SHORT).show()
                }
            },
            onCancel = viewModel::cancelAddDuplicateItem
        )
    }

}

@Composable
fun LocationOption(
    label: String, isSelected: Boolean, onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clickable { onClick() }
            .padding(4.dp)
            .background(
                color = if (isSelected) Color.LightGray else Color.Transparent,
                shape = RoundedCornerShape(10.dp)
            )
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) colorResource(R.color.theme_color) else Color.Transparent,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(
                    id = when (label) {
                        stringResource(R.string.freezer) -> R.drawable.ic_freezer
                        stringResource(R.string.pantry) -> R.drawable.ic_pantry
                        stringResource(R.string.fridge) -> R.drawable.ic_fridge
                        else -> R.drawable.ic_fridge
                    }
                ),
                contentDescription = label,
                modifier = Modifier.size(48.dp),
                tint = if (isSelected) colorResource(R.color.theme_color) else Color.Gray
            )
            Text(text = label, fontSize = 14.sp)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDatePickerDialog(
    onDateSelected: (String) -> Unit, onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(selectableDates = object : SelectableDates {
        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
            return utcTimeMillis >= System.currentTimeMillis()
        }
    })

    val selectedDate = datePickerState.selectedDateMillis?.let {
        convertMillisToDate(it)
    } ?: ""

    DatePickerDialog(onDismissRequest = { onDismiss() }, confirmButton = {
        Button(onClick = {
            onDateSelected(selectedDate)
            onDismiss()
        }) {
            Text(text = stringResource(R.string.ok))
        }
    }, dismissButton = {
        Button(onClick = { onDismiss() }) {
            Text(text = stringResource(R.string.cancel))
        }
    }) {
        DatePicker(state = datePickerState)
    }
}

fun convertMillisToDate(millis: Long): String {
    val calendar = Calendar.getInstance().apply { timeInMillis = millis }
    return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
}

@Composable
fun WarningModal(
    itemName: String,
    onProceed: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = {
            Text(
                text = stringResource(R.string.duplicate_item_warning_title),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        },
        text = {
            Text(
                text = stringResource(R.string.duplicate_item_warning_text, itemName),
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                modifier = Modifier.padding(vertical = 8.dp),
                color = Color.Gray
            )
        },
        confirmButton = {
            Button(onClick = onProceed, shape = RoundedCornerShape(8.dp)) {
                Text(stringResource(R.string.yes_add))
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onCancel, shape = RoundedCornerShape(8.dp)) {
                Text(stringResource(R.string.no_cancel))
            }
        },
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    )
}