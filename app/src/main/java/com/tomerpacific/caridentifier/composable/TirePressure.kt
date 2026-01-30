package com.tomerpacific.caridentifier.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tomerpacific.caridentifier.R
import com.tomerpacific.caridentifier.model.MainViewModel
import com.tomerpacific.caridentifier.model.allPropertiesNull

@Composable
fun TirePressure(viewModel: MainViewModel) {
    val mainUiState by viewModel.mainUiState.collectAsState()
    val tirePressure = mainUiState.tirePressure

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = stringResource(id = R.string.tire_pressure),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 20.dp, bottom = 20.dp)
        )
        when {
            mainUiState.isLoading -> {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    LoaderAnimation(R.raw.loading_tire_animation)
                }
            }
            tirePressure != null -> {
                if (tirePressure.allPropertiesNull()) {
                    Text(text = stringResource(id = R.string.no_tire_pressure_data_for_car_error_msg))
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        tirePressure.frontPsi?.let {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Image(
                                    painter = painterResource(id = R.drawable.tire_pressure),
                                    contentDescription = "Front Tire"
                                )
                                Text(text = "Front PSI: $it", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        tirePressure.rearPsi?.let {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_tire_pressure),
                                    contentDescription = "Rear Tire"
                                )
                                Text(text = "Rear PSI: $it", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    tirePressure.note?.let {
                        Text(text = it, fontSize = 16.sp, modifier = Modifier.padding(top = 10.dp))
                    }
                }
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
                if (viewModel.shouldShowRetryRequestButton()) {
                    IconButton(onClick = {
                        viewModel.getTirePressure()
                    }) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Retry")
                    }
                }
            }
        }
    }
}