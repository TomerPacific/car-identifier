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
 * 1. 7 digits: XX-XXX-XX
 * 2. 8 digits: XXX-XX-XXX
 */
private const val SEVEN_DIGIT_FIRST_DASH_INDEX = 2
private const val SEVEN_DIGIT_SECOND_DASH_INDEX = 6
private const val SEVEN_DIGIT_SECOND_GROUP_START = 3
private const val SEVEN_DIGIT_THIRD_GROUP_START = 7
private const val EIGHT_DIGIT_SECOND_GROUP_START = 4

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
private fun LicensePlateSupportingText(isLimitReached: Boolean, didClickConfirmBtn: Boolean) {
    if (isLimitReached) {
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

private fun handleCharacterDeletion(textFieldValue: TextFieldValue): TextFieldValue {
    return if (textFieldValue.text.length == SEVEN_DIGIT_LICENSE_NUMBER_LENGTH_WITH_DASHES) {
        val formattedText = "${textFieldValue.text.substring(0, SEVEN_DIGIT_FIRST_DASH_INDEX)}-${
            textFieldValue.text.substring(
                SEVEN_DIGIT_FIRST_DASH_INDEX,
                SEVEN_DIGIT_SECOND_GROUP_START,
            )
        }${textFieldValue.text.substring(
            EIGHT_DIGIT_SECOND_GROUP_START,
            SEVEN_DIGIT_SECOND_DASH_INDEX,
        )}-${textFieldValue.text.substring(
            SEVEN_DIGIT_THIRD_GROUP_START,
            SEVEN_DIGIT_LICENSE_NUMBER_LENGTH_WITH_DASHES
        )}"
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
        SEVEN_DIGIT_FIRST_DASH_INDEX -> "${input.substring(0, SEVEN_DIGIT_FIRST_DASH_INDEX)}-"
        SEVEN_DIGIT_SECOND_DASH_INDEX -> "${input.substring(0, SEVEN_DIGIT_FIRST_DASH_INDEX)}-${input.substring(
            SEVEN_DIGIT_SECOND_GROUP_START,
            SEVEN_DIGIT_SECOND_DASH_INDEX,
        )}-"
        in EIGHT_DIGIT_LICENSE_NUMBER_LENGTH_WITH_DASHES..EIGHT_DIGIT_LICENSE_NUMBER_LENGTH_WITH_DASHES + 1 -> {
            val firstPart = input.substring(0, SEVEN_DIGIT_FIRST_DASH_INDEX)
            val secondPart = input.substring(SEVEN_DIGIT_SECOND_GROUP_START, EIGHT_DIGIT_SECOND_GROUP_START)
            val thirdPart = input.substring(EIGHT_DIGIT_SECOND_GROUP_START, SEVEN_DIGIT_SECOND_DASH_INDEX)
            val fourthPart = input.substring(SEVEN_DIGIT_THIRD_GROUP_START, input.length)
            "$firstPart$secondPart-$thirdPart-$fourthPart"
        }
        else -> input
    }
}
