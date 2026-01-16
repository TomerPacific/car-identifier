package com.tomerpacific.caridentifier.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tomerpacific.caridentifier.R
import com.tomerpacific.caridentifier.isLicensePlateNumberValid
import com.tomerpacific.caridentifier.model.MainViewModel
import com.tomerpacific.caridentifier.model.Screen

private const val LICENSE_PLATE_NUMBER_MAX_LENGTH = 10

@Composable
fun LicensePlateNumberDialog(
    navController: NavController,
    mainViewModel: MainViewModel,
) {
    mainViewModel.resetData()

    val focusRequester = remember { FocusRequester() }

    var licensePlateNumber by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = {
            navController.popBackStack()
        },
        text = {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Column {
                    Text(stringResource(R.string.amount_of_digits_in_license_plate), fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(10.dp))
                    TextField(
                        modifier = Modifier.focusRequester(focusRequester),
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                        singleLine = true,
                        colors =
                            TextFieldDefaults.colors().copy(
                                unfocusedContainerColor = colorResource(R.color.gold),
                                focusedContainerColor = colorResource(R.color.gold),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                            ),
                        value = licensePlateNumber,
                        onValueChange = { input ->
                            if (input.length <= LICENSE_PLATE_NUMBER_MAX_LENGTH) {
                                licensePlateNumber = input
                                showError = false
                            }
                        },
                        placeholder = {
                            Text("XX-XXX-XX", maxLines = 1)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = showError,
                        supportingText = {
                            if (showError) {
                                Text(
                                    stringResource(R.string.license_plate_input_amount_of_digits_error),
                                    modifier = Modifier.fillMaxWidth(),
                                    color = Color.Red,
                                )
                            }
                        },
                        trailingIcon = {
                            if (showError) {
                                Icon(Icons.Filled.Info, "error", tint = Color.Red)
                            }
                        },
                        leadingIcon = {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.offset(x = (-9).dp).background(Color.Blue),
                            ) {
                                Image(
                                    painterResource(id = R.drawable.israel_flag),
                                    "flag",
                                    modifier = Modifier.width(20.dp).height(20.dp),
                                )
                                Text(
                                    "IL",
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                )
                                Text(
                                    "ישראל",
                                    textAlign = TextAlign.Center,
                                    color = Color.White,
                                    fontSize = 10.sp,
                                )
                            }
                        },
                    )
                }

                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (isLicensePlateNumberValid(licensePlateNumber)) {
                        mainViewModel.getCarDetails(licensePlateNumber)
                        navController.navigate(Screen.CarDetailsScreen.route)
                    } else {
                        showError = true
                    }
                },
                enabled = licensePlateNumber.isNotEmpty(),
            ) {
                Text(stringResource(R.string.approve))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    navController.popBackStack()
                },
            ) {
                Text(stringResource(R.string.cancel))
            }
        },
    )
}
