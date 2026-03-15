package com.tomerpacific.caridentifier.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tomerpacific.caridentifier.R
import com.tomerpacific.caridentifier.composable.Advice
import com.tomerpacific.caridentifier.composable.Details
import com.tomerpacific.caridentifier.composable.Reviews
import com.tomerpacific.caridentifier.model.CarViewModel

private const val CHAT_GPT_ICON_SIZE = 40
private const val TAB_DETAILS_INDEX = 0
private const val TAB_REVIEWS_INDEX = 1
private const val TAB_AI_INDEX = 2
private const val TAB_TIRE_PRESSURE_INDEX = 3

@Composable
fun CarDetailsScreen(
    carViewModel: CarViewModel,
    navController: NavController,
) {
    var tabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        stringResource(R.string.tab_name_details),
        stringResource(R.string.tab_name_reviews),
        stringResource(R.string.tab_name_recommendations),
        stringResource(R.string.tab_name_tire_pressure)
    )

    Scaffold(contentWindowInsets = WindowInsets.safeContent) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            CarDetailsTabs(tabs, tabIndex, carViewModel) { newTabIndex ->
                tabIndex = newTabIndex
            }

            when (tabIndex) {
                TAB_DETAILS_INDEX -> Details(carViewModel)
                TAB_REVIEWS_INDEX -> Reviews(carViewModel)
                TAB_AI_INDEX -> {
                    LaunchedEffect(tabIndex) {
                        carViewModel.getCarReview()
                    }
                    Advice(carViewModel)
                }
                TAB_TIRE_PRESSURE_INDEX -> {
                    LaunchedEffect(tabIndex) {
                        carViewModel.getTirePressure()
                    }
                    TirePressureScreen(carViewModel)
                }
            }
        }
        BackHandler { navController.popBackStack() }
    }
}

@Composable
private fun CarDetailsTabs(
    tabs: List<String>,
    tabIndex: Int,
    carViewModel: CarViewModel,
    onTabClicked: (Int) -> Unit
) {
    val mainUiState by carViewModel.mainUiState.collectAsState()

    TabRow(selectedTabIndex = tabIndex) {
        tabs.forEachIndexed { index, title ->
            Tab(
                text = { Text(title) },
                selected = tabIndex == index,
                onClick = { onTabClicked(index) },
                icon = {
                    when (index) {
                        TAB_DETAILS_INDEX -> Icon(painterResource(id = R.drawable.ic_fact_check), "list")
                        TAB_REVIEWS_INDEX -> Icon(painterResource(id = R.drawable.ic_reviews), "reviews")
                        TAB_AI_INDEX -> Icon(
                            painter = painterResource(id = R.drawable.ic_chatgpt),
                            contentDescription = "ai",
                            modifier = Modifier.size(CHAT_GPT_ICON_SIZE.dp)
                        )
                        TAB_TIRE_PRESSURE_INDEX -> Icon(
                            painter = painterResource(id = R.drawable.ic_tire_pressure),
                            contentDescription = "tire pressure"
                        )
                    }
                },
                enabled = mainUiState.errorMessage == null
            )
        }
    }
}
