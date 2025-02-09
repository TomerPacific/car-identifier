package com.tomerpacific.caridentifier.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun LoaderAnimation(animationResourceId: Int) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(animationResourceId))
    LottieAnimation(composition,
        iterations =  LottieConstants.IterateForever)
}
