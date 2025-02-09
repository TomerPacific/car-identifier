package com.tomerpacific.caridentifier.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.tomerpacific.caridentifier.R

@Composable
fun MainLoaderAnimation() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.license_plate_scan_animation))
    LottieAnimation(composition,
        iterations =  LottieConstants.IterateForever)
}

@Composable
fun AdviceLoaderAnimation() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.truck_loading_animation))
    LottieAnimation(composition,
        iterations =  LottieConstants.IterateForever)
}
