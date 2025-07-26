package com.tomerpacific.caridentifier.composable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tomerpacific.caridentifier.R
import com.tomerpacific.caridentifier.SectionHeader
import com.tomerpacific.caridentifier.model.MainViewModel

@Composable
fun Advice(mainViewModel: MainViewModel) {
    val mainUiState by mainViewModel.mainUiState.collectAsState()

    val columnVerticalArrangement: Arrangement.Vertical =
        when (mainUiState.carReview) {
            null -> Arrangement.Center
            else -> Arrangement.Top
        }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = columnVerticalArrangement,
    ) {
        when {
            mainUiState.isLoading -> {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    LoaderAnimation(R.raw.truck_loading_animation)
                }
            }
            mainUiState.carReview != null -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text(stringResource(R.string.car_advice_header), fontSize = 25.sp, fontWeight = FontWeight.Bold)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text(stringResource(R.string.car_advice_disclaimer), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.size(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Image(
                        modifier =
                            Modifier
                                .size(200.dp)
                                .border(
                                    BorderStroke(1.dp, Color.Black),
                                    CircleShape,
                                )
                                .clip(CircleShape),
                        painter = painterResource(R.drawable.car_advice),
                        contentDescription = "mechanic in garage",
                    )
                }

                AdviceList(
                    title = mainViewModel.getTranslatedSectionHeader(SectionHeader.PROS),
                    adviceList = mainUiState.carReview!!.prosList,
                    Modifier.align(Alignment.Start),
                )
                AdviceList(
                    title = mainViewModel.getTranslatedSectionHeader(SectionHeader.CONS),
                    adviceList = mainUiState.carReview!!.consList,
                    Modifier.align(Alignment.Start),
                )
            }
            mainUiState.errorMessage != null -> {
                Spacer(modifier = Modifier.size(20.dp))
                Text(
                    text = stringResource(R.string.car_details_not_obtained_error_msg),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    style = TextStyle(textDirection = TextDirection.Rtl),
                )
                Text(
                    text = mainUiState.errorMessage!!,
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun AdviceList(
    title: String,
    adviceList: List<String>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .padding(start = 5.dp),
    ) {
        Text(
            "$title:",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(4.dp))
        adviceList.forEach { advice ->
            Text(
                text = advice,
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(start = 10.dp),
            )
        }
    }
}
