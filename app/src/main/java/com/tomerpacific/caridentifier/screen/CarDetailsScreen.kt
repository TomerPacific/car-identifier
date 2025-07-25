package com.tomerpacific.caridentifier.screen


import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.tomerpacific.caridentifier.composable.Details
import com.tomerpacific.caridentifier.composable.Advice
import com.tomerpacific.caridentifier.composable.Reviews
import com.tomerpacific.caridentifier.model.MainViewModel

@Composable
fun CarDetailsScreen(mainViewModel: MainViewModel, navController: NavController) {


    var tabIndex by remember { mutableIntStateOf(0) }

    val tabs = listOf(
        stringResource(R.string.tab_name_details),
        stringResource(R.string.tab_name_reviews),
        stringResource(R.string.tab_name_recommendations))

    val mainUiState by mainViewModel.mainUiState.collectAsState()

    Scaffold(contentWindowInsets = WindowInsets.safeContent) { innerPadding ->
        Column(modifier = Modifier.fillMaxWidth().padding(innerPadding)) {
            TabRow(selectedTabIndex = tabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(text = { Text(title) },
                        selected = tabIndex == index,
                        onClick = { tabIndex = index },
                        icon = {
                            when (index) {
                                0 -> Icon(painterResource(id = R.drawable.ic_fact_check), contentDescription = "list")
                                1 -> Icon(painterResource(id = R.drawable.ic_reviews), contentDescription = "reviews")
                                2 -> Icon(painterResource(
                                    id = R.drawable.ic_chatgpt),
                                    contentDescription = "ai",
                                    modifier = Modifier.size(40.dp))
                            }
                        },
                        enabled = mainUiState.errorMessage == null
                    )
                }
            }
            when (tabIndex) {
                0 -> Details(mainViewModel)
                1 -> Reviews(mainViewModel)
                2 -> {
                    mainViewModel.getCarReview()
                    Advice(mainViewModel)
                }
            }
        }
        BackHandler {
            navController.popBackStack()
        }
    }
}
