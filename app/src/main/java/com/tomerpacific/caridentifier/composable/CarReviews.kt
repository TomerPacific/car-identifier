package com.tomerpacific.caridentifier.composable

import android.webkit.WebView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.tomerpacific.caridentifier.R
import com.tomerpacific.caridentifier.model.MainViewModel

@Composable
fun Reviews(mainViewModel: MainViewModel,
            serverError: String?) {

    val webview: State<WebView?> = mainViewModel.webView.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (serverError != null) {
            Text(
                text = stringResource(R.string.car_details_not_obtained_error_msg),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                style = TextStyle(textDirection = TextDirection.Rtl)
            )
            Text(
                text = serverError,
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        } else if (webview.value != null) {
            AndroidView(factory = {
                webview.value!!
            }, update = {

            })
        }
    }
}