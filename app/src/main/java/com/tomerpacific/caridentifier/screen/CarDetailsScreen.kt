package com.tomerpacific.caridentifier.screen


import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tomerpacific.caridentifier.R
import com.tomerpacific.caridentifier.composable.Details
import com.tomerpacific.caridentifier.composable.Advice
import com.tomerpacific.caridentifier.composable.Reviews
import com.tomerpacific.caridentifier.model.MainViewModel

@Composable
fun CarDetailsScreen(mainViewModel: MainViewModel, navController: NavController) {


    var tabIndex by remember { mutableStateOf(0) }

    val tabs = listOf("Details", "Reviews", "Advice")

    val serverError = mainViewModel.serverError.collectAsState()

    val searchTerm: String = mainViewModel.searchTerm

    Column(modifier = Modifier.fillMaxWidth()) {
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
                                contentDescription = "chatgpt",
                                modifier = Modifier.size(40.dp))
                        }
                    }
                )
            }
        }
        when (tabIndex) {
            0 -> Details(mainViewModel, serverError)
            1 -> Reviews(searchTerm, serverError)
            2 -> {
                if (serverError.value == null) {
                    mainViewModel.getCarReview(searchTerm)
                }
                Advice(mainViewModel, serverError)
            }
        }
    }
    BackHandler {
        navController.navigateUp()
        mainViewModel.resetData()
    }

}

@Composable
fun Spinner() {
    CircularProgressIndicator(
        modifier = Modifier.width(64.dp),
        color = MaterialTheme.colorScheme.secondary,
        trackColor = MaterialTheme.colorScheme.surfaceVariant,
    )
}