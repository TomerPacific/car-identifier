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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tomerpacific.caridentifier.EIGHT_DIGIT_LICENSE_NUMBER_LENGTH_WITH_DASHES
import com.tomerpacific.caridentifier.R
import com.tomerpacific.caridentifier.SEVEN_DIGIT_LICENSE_NUMBER_LENGTH_WITH_DASHES
import com.tomerpacific.caridentifier.isLicensePlateNumberValid
import com.tomerpacific.caridentifier.model.MainViewModel
import com.tomerpacific.caridentifier.model.Screen

private val TEXT_FIELD_BACKGROUND_COLOR = Color(0xFFFDD13F)
private const val FIRST_DASH_INDEX = 2
private const val SECOND_DASH_INDEX = 6

@Composable
fun LicensePlateNumberDialog(
    navController: NavController,
    mainViewModel: MainViewModel,
) {
    mainViewModel.resetData()

    val focusRequester =
        remember {
            FocusRequester()
        }

    var licensePlateNumberState by remember {
        mutableStateOf(
            TextFieldValue(
                text = "",
            ),
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
                                unfocusedContainerColor = TEXT_FIELD_BACKGROUND_COLOR,
                                focusedContainerColor = TEXT_FIELD_BACKGROUND_COLOR,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                            ),
                        value = licensePlateNumberState,
                        onValueChange = { textFieldValue ->
                            didClickConfirmBtn = false

                            if (doesLicensePlateNumberExceedLimit(textFieldValue)) {
                                isLicensePlateLengthLimitReached = true
                                return@TextField
                            }

                            if (wasCharacterDeleted(textFieldValue.text, licensePlateNumberState.text)) {
                                isLicensePlateLengthLimitReached = false
                                licensePlateNumberState = handleCharacterDeletion(textFieldValue)
                                return@TextField
                            }

                            if (textFieldValue.text.isEmpty() || licensePlateInputPattern.matches(textFieldValue.text)) {
                                licensePlateNumberState = textFieldValue
                            }
                            val formattedText = formatLicensePlateWithDashes(textFieldValue.text)

                            licensePlateNumberState =
                                TextFieldValue(
                                    text = formattedText,
                                    selection = TextRange(formattedText.length),
                                )
                        },
                        placeholder = {
                            Text(stringResource(R.string.license_plate_placeholder), maxLines = 1)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = isLicensePlateLengthLimitReached,
                        supportingText = {
                            if (isLicensePlateLengthLimitReached) {
                                Text(
                                    stringResource(R.string.license_plate_input_limit_error),
                                    modifier = Modifier.fillMaxWidth(),
                                    color = Color.Red,
                                )
                            }
                            if (didClickConfirmBtn) {
                                Text(
                                    stringResource(R.string.license_plate_input_amount_of_digits_error),
                                    modifier = Modifier.fillMaxWidth(),
                                    color = Color.Red,
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
                    didClickConfirmBtn = true
                    if (isLicensePlateNumberValid(
                            licensePlateNumberState.text,
                            validLicensePlatePattern,
                        )
                    ) {
                        mainViewModel.getCarDetails(licensePlateNumberState.text)
                        navController.navigate(Screen.CarDetailsScreen.route)
                    }
                },
                enabled = licensePlateNumberState.text.isNotEmpty(),
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

private fun doesLicensePlateNumberExceedLimit(textFieldValue: TextFieldValue): Boolean {
    return textFieldValue.text.length > EIGHT_DIGIT_LICENSE_NUMBER_LENGTH_WITH_DASHES
}

private fun handleCharacterDeletion(textFieldValue: TextFieldValue): TextFieldValue {
    return if (textFieldValue.text.length == SEVEN_DIGIT_LICENSE_NUMBER_LENGTH_WITH_DASHES) {
        val formattedText = "${textFieldValue.text.substring(0, FIRST_DASH_INDEX)}-${
            textFieldValue.text.substring(
                FIRST_DASH_INDEX,
                3,
            )
        }${textFieldValue.text.substring(
            4,
            SECOND_DASH_INDEX,
        )}-${textFieldValue.text.substring(7, SEVEN_DIGIT_LICENSE_NUMBER_LENGTH_WITH_DASHES)}"
        TextFieldValue(
            text = formattedText,
            selection = TextRange(formattedText.length),
        )
    } else {
        textFieldValue
    }
}

private fun wasCharacterDeleted(
    currentText: String,
    previousText: String,
): Boolean {
    return currentText.length < previousText.length
}

private fun formatLicensePlateWithDashes(input: String): String {
    return when (input.length) {
        FIRST_DASH_INDEX -> "${input.substring(0, FIRST_DASH_INDEX)}-"
        SECOND_DASH_INDEX -> "${input.substring(0, FIRST_DASH_INDEX)}-${input.substring(
            3,
            SECOND_DASH_INDEX,
        )}-"
        in EIGHT_DIGIT_LICENSE_NUMBER_LENGTH_WITH_DASHES..EIGHT_DIGIT_LICENSE_NUMBER_LENGTH_WITH_DASHES + 1 ->
            "${input.substring(
                0,
                FIRST_DASH_INDEX,
            )}${input.substring(3,4)}-${input.substring(4, SECOND_DASH_INDEX)}-${input.substring(7, input.length)}"
        else -> input
    }
}
