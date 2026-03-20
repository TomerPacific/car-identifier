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
import com.tomerpacific.caridentifier.model.CarViewModel
import com.tomerpacific.caridentifier.model.Screen

/**
 * Israeli license plates come in two formats:
 * 1. 7 digits: XX-XXX-XX (9 characters total with dashes)
 * 2. 8 digits: XXX-XX-XXX (10 characters total with dashes)
 */

// Digit positions in a raw (numeric) 7-digit license plate: XX (0-1), XXX (2-4), XX (5-6)
private const val RAW_7_DIGIT_G2_START = 2
private const val RAW_7_DIGIT_G3_START = 5
private const val RAW_7_DIGIT_LEN = 7

// Digit positions in a raw (numeric) 8-digit license plate: XXX (0-2), XX (3-4), XXX (5-7)
private const val RAW_8_DIGIT_G2_START = 3
private const val RAW_8_DIGIT_G3_START = 5
private const val RAW_8_DIGIT_LEN = 8

// Character positions for dashes during input formatting
private const val FORMAT_7_DIGIT_FIRST_DASH_INDEX = 2
private const val FORMAT_7_DIGIT_SECOND_DASH_INDEX = 6

private const val RAW_5_DIGIT_LEN = 5

@Composable
fun LicensePlateNumberDialog(
    navController: NavController,
    carViewModel: CarViewModel,
) {
    carViewModel.resetData()

    val focusRequester = remember { FocusRequester() }

    var licensePlateNumberState by remember {
        mutableStateOf(TextFieldValue(text = ""))
    }

    var isLicensePlateLengthLimitReached by remember { mutableStateOf(false) }

    var didClickConfirmBtn by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { navController.popBackStack() },
        text = {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Column {
                    Text(stringResource(R.string.amount_of_digits_in_license_plate), fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(10.dp))
                    LicensePlateTextField(
                        value = licensePlateNumberState,
                        onValueChange = { newValue ->
                            didClickConfirmBtn = false
                            licensePlateNumberState = handleValueChange(
                                newValue,
                                licensePlateNumberState.text,
                                onLimitReached = { isLicensePlateLengthLimitReached = it }
                            )
                        },
                        focusRequester = focusRequester,
                        isError = isLicensePlateLengthLimitReached,
                        didClickConfirmBtn = didClickConfirmBtn
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
                    if (isLicensePlateNumberValid(licensePlateNumberState.text)) {
                        carViewModel.getCarDetails(licensePlateNumberState.text)
                        navController.navigate(Screen.CarDetailsScreen.route)
                    }
                },
                enabled = licensePlateNumberState.text.isNotEmpty(),
            ) {
                Text(stringResource(R.string.approve))
            }
        },
        dismissButton = {
            TextButton(onClick = { navController.popBackStack() }) {
                Text(stringResource(R.string.cancel))
            }
        },
    )
}

@Composable
private fun LicensePlateTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    focusRequester: FocusRequester,
    isError: Boolean,
    didClickConfirmBtn: Boolean
) {
    TextField(
        modifier = Modifier.focusRequester(focusRequester),
        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
        singleLine = true,
        colors = TextFieldDefaults.colors().copy(
            unfocusedContainerColor = colorResource(R.color.gold),
            focusedContainerColor = colorResource(R.color.gold),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(stringResource(R.string.license_plate_placeholder), maxLines = 1)
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        isError = isError,
        supportingText = {
            LicensePlateSupportingText(isError, didClickConfirmBtn)
        },
        trailingIcon = {
            if (isError) {
                Icon(Icons.Filled.Info, "error", tint = Color.Red)
            }
        },
        leadingIcon = { IsraelFlagIcon() },
    )
}

@Composable
private fun IsraelFlagIcon() {
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
}

@Composable
private fun LicensePlateSupportingText(isError: Boolean, didClickConfirmBtn: Boolean) {
    if (isError) {
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
}

private fun handleValueChange(
    newValue: TextFieldValue,
    oldText: String,
    onLimitReached: (Boolean) -> Unit
): TextFieldValue {
    val licensePlateInputPattern = Regex("^[0-9-]*$")

    return when {
        doesLicensePlateNumberExceedLimit(newValue) -> {
            onLimitReached(true)
            TextFieldValue(text = oldText, selection = TextRange(oldText.length))
        }
        wasCharacterDeleted(newValue.text, oldText) -> {
            onLimitReached(false)
            handleCharacterDeletion(newValue)
        }
        newValue.text.isNotEmpty() && !licensePlateInputPattern.matches(newValue.text) -> {
            TextFieldValue(text = oldText, selection = TextRange(oldText.length))
        }
        else -> {
            val formattedText = formatLicensePlateWithDashes(newValue.text)
            TextFieldValue(text = formattedText, selection = TextRange(formattedText.length))
        }
    }
}

private fun doesLicensePlateNumberExceedLimit(textFieldValue: TextFieldValue): Boolean {
    return textFieldValue.text.length > EIGHT_DIGIT_LICENSE_NUMBER_LENGTH_WITH_DASHES
}

/**
 * Handles character deletion.
 * If we just reduced an 8-digit number back to 7 digits, it might be in a broken format (XXX-XX-XX),
 * so we force it back to the 7-digit format (XX-XXX-XX).
 */
private fun handleCharacterDeletion(textFieldValue: TextFieldValue): TextFieldValue {
    val input = textFieldValue.text
    if (input.length == SEVEN_DIGIT_LICENSE_NUMBER_LENGTH_WITH_DASHES) {
        val digits = input.filter { it.isDigit() }
        if (digits.length == RAW_7_DIGIT_LEN) {
            val g1 = digits.substring(0, RAW_7_DIGIT_G2_START)
            val g2 = digits.substring(RAW_7_DIGIT_G2_START, RAW_7_DIGIT_G3_START)
            val g3 = digits.substring(RAW_7_DIGIT_G3_START)
            val formattedText = "$g1-$g2-$g3"
            return TextFieldValue(text = formattedText, selection = TextRange(formattedText.length))
        }
    }
    return textFieldValue
}

private fun wasCharacterDeleted(
    currentText: String,
    previousText: String,
): Boolean {
    return currentText.length < previousText.length
}

/**
 * Formats the license plate with dashes as the user types.
 * Transitions between 7-digit (XX-XXX-XX) and 8-digit (XXX-XX-XXX) formats.
 */
private fun formatLicensePlateWithDashes(input: String): String {
    return when (input.length) {
        FORMAT_7_DIGIT_FIRST_DASH_INDEX -> "$input-"
        FORMAT_7_DIGIT_SECOND_DASH_INDEX -> {
            val digits = input.filter { it.isDigit() }
            if (digits.length == RAW_5_DIGIT_LEN) {
                val g1 = digits.substring(0, RAW_7_DIGIT_G2_START)
                val g2 = digits.substring(RAW_7_DIGIT_G2_START)
                "$g1-$g2-"
            } else input
        }
        EIGHT_DIGIT_LICENSE_NUMBER_LENGTH_WITH_DASHES -> {
            val digits = input.filter { it.isDigit() }
            if (digits.length == RAW_8_DIGIT_LEN) {
                val g1 = digits.substring(0, RAW_8_DIGIT_G2_START)
                val g2 = digits.substring(RAW_8_DIGIT_G2_START, RAW_8_DIGIT_G3_START)
                val g3 = digits.substring(RAW_8_DIGIT_G3_START)
                "$g1-$g2-$g3"
            } else input
        }
        else -> input
    }
}
