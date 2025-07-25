package com.tomerpacific.caridentifier.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tomerpacific.caridentifier.BuildConfig
import com.tomerpacific.caridentifier.composable.CarLicensePlateSearchOptionButton
import com.tomerpacific.caridentifier.R
import com.tomerpacific.caridentifier.model.MainViewModel
import com.tomerpacific.caridentifier.ui.theme.CarIdentifierTheme

@Composable
fun MainScreen(navController: NavController,
               mainViewModel: MainViewModel) {

    val shouldShowRationale = mainViewModel.shouldShowRationale.collectAsState()

    val didRequestPermission = mainViewModel.didRequestCameraPermission.collectAsState()

    val shouldDisableButton = didRequestPermission.value && !shouldShowRationale.value

    CarIdentifierTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Scaffold(contentWindowInsets = WindowInsets.safeContent) { innerPadding ->
                    Column(modifier = Modifier.fillMaxSize().padding(innerPadding),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top) {

                        LicensePlateInputOptionsHeader()
                        Spacer(modifier = Modifier.size(150.dp))
                        LicensePlateInputOptions(navController, shouldDisableButton, Modifier.weight(1f))
                        AppVersion()
                    }
                }
            }
        }
    }
}

@Composable
private fun LicensePlateInputOptionsHeader() {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(top = 30.dp, start = 10.dp, end = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center) {
        Text(
            stringResource(R.string.main_screen_header),
            fontWeight = FontWeight.Bold,
            fontSize = 25.sp,
            textAlign = TextAlign.Center)
    }
}

@Composable
private fun LicensePlateInputOptions(navController: NavController,
                                     shouldDisableButton:Boolean,
                                     modifier: Modifier = Modifier) {
    Box(modifier = modifier
        .fillMaxWidth()
        .height(300.dp)) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CarLicensePlateSearchOptionButton(
                buttonText = stringResource(R.string.main_screen_search_by_image),
                drawableId = R.drawable.license_plate,
                drawableContentDescription = "License Plate",
                navController,
                shouldDisableButton
            )
            CarLicensePlateSearchOptionButton(
                buttonText = stringResource(R.string.main_screen_search_by_license_plate),
                drawableId = R.drawable.keyboard,
                drawableContentDescription = "Smartphone Keyboard",
                navController
            )
        }
    }
}

@Composable
private fun AppVersion() {
    Row(modifier = Modifier
        .fillMaxWidth()
        .height(intrinsicSize = IntrinsicSize.Max)
        .padding(end = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End) {
        Text(
            text = "v.${BuildConfig.VERSION_NAME}",
            fontSize = 16.sp
        )
    }
}
