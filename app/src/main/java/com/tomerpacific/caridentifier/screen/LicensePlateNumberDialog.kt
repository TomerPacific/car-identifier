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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tomerpacific.caridentifier.R
import com.tomerpacific.caridentifier.isLicensePlateNumberValid
import com.tomerpacific.caridentifier.model.MainViewModel
import com.tomerpacific.caridentifier.model.Screen


private val TEXT_FIELD_BACKGROUND_COLOR = Color(253, 209, 63, 255)
private const val FIRST_DASH_INDEX = 2
private const val SECOND_DASH_INDEX = 6

@Composable
fun LicensePlateNumberDialog(navController: NavController, mainViewModel: MainViewModel) {

    mainViewModel.resetData()

    val focusRequester = remember {
        FocusRequester()
    }

    var licensePlateNumberState by remember {
        mutableStateOf(
            TextFieldValue(
                text = ""
            )
        )
    }

    val licensePlateInputPattern = Regex("^[0-9-]*\$")

    val validLicensePlatePattern = Regex("^[0-9]{2,3}-[0-9]{2,3}-[0-9]{2,3}")

    var isLicensePlateLengthLimitReached by remember {
        mutableStateOf(false)
    }

    var didClickConfirmBtn by remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = {
            navController.popBackStack()
        },
        text = {
            Column {
                Text("הכנס מספר לוחית רישוי בן 7 או 8 ספרות", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(10.dp))
                TextField(
                    modifier = Modifier.focusRequester(focusRequester),
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                    singleLine = true,
                    colors = TextFieldDefaults.colors().copy(
                        unfocusedContainerColor = TEXT_FIELD_BACKGROUND_COLOR,
                        focusedContainerColor = TEXT_FIELD_BACKGROUND_COLOR,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    value = licensePlateNumberState,
                    onValueChange = {
                        didClickConfirmBtn = false
                        if (wasCharacterDeleted(it.text, licensePlateNumberState.text)) {
                            isLicensePlateLengthLimitReached = false
                            licensePlateNumberState = if (it.text.length == 9) {
                                val formattedText = "${it.text.substring(0, FIRST_DASH_INDEX)}-${
                                    it.text.substring(
                                        FIRST_DASH_INDEX,
                                        3
                                    )
                                }${it.text.substring(4, SECOND_DASH_INDEX)}-${it.text.substring(7, 9)}"
                                TextFieldValue(
                                    text = formattedText,
                                    selection = TextRange(formattedText.length)
                                )
                            } else {
                                it
                            }
                            return@TextField
                        }


                        if (it.text.length > 10) {
                            isLicensePlateLengthLimitReached = true
                            return@TextField
                        }

                        if (it.text.isEmpty() || licensePlateInputPattern.matches(it.text)) {
                            licensePlateNumberState = it
                        }
                        val formattedText = formatLicensePlateWithDashes(it.text)

                        licensePlateNumberState = TextFieldValue(
                            text = formattedText,
                            selection = TextRange(formattedText.length)
                        )
                    },
                    placeholder = {
                        Text("?מספר לוחית רישוי", maxLines = 1)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = isLicensePlateLengthLimitReached,
                    supportingText = {
                        if (isLicensePlateLengthLimitReached) {
                            Text(
                                "מספר לוחית רישוי יכול להכיל עד 8 ספרות",
                                modifier = Modifier.fillMaxWidth(),
                                color = Color.Red
                            )
                        }
                        if (didClickConfirmBtn) {
                            Text(
                                "מספר לוחית רישוי צריך להכיל בין 7 ל-8 ספרות",
                                modifier = Modifier.fillMaxWidth(),
                                color = Color.Red
                            )
                        }
                    },
                    trailingIcon = {
                        if (isLicensePlateLengthLimitReached) {
                            Icon(Icons.Filled.Info, "error", tint = Color.Red)
                        }
                    },
                    leadingIcon = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.offset(x = (-9).dp).background(Color.Blue)
                        ) {
                            Image(
                                painterResource(id = R.drawable.israel_flag),
                                "flag",
                                modifier = Modifier.width(20.dp).height(20.dp)
                            )
                            Text(
                                "IL",
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 12.sp
                            )
                            Text(
                                "ישראל",
                                textAlign = TextAlign.Center,
                                color = Color.White,
                                fontSize = 10.sp
                            )
                        }
                    }
                )
            }

            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    didClickConfirmBtn = true
                    if (isLicensePlateNumberValid(
                            licensePlateNumberState.text,
                            validLicensePlatePattern
                        )
                    ) {
                        mainViewModel.getCarDetails(context, licensePlateNumberState.text)
                        navController.navigate(Screen.CarDetailsScreen.route)
                    }
                },
                enabled = licensePlateNumberState.text.isNotEmpty()
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    navController.popBackStack()
                }
            ) {
                Text("Cancel")
            }
        }
    )
}

private fun wasCharacterDeleted(currentText: String, previousText: String): Boolean {
    return currentText.length < previousText.length
}

private fun formatLicensePlateWithDashes(input: String): String {
    return when (input.length) {
        FIRST_DASH_INDEX -> "${input.substring(0, FIRST_DASH_INDEX)}-"
        SECOND_DASH_INDEX -> "${input.substring(0, FIRST_DASH_INDEX)}-${input.substring(3,
            SECOND_DASH_INDEX)}-"
        in 10..11 ->
            "${input.substring(0, FIRST_DASH_INDEX)}${input.substring(3,4)}-${input.substring(4, SECOND_DASH_INDEX)}-${input.substring(7, input.length)}"
        else -> input
    }
}