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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tomerpacific.caridentifier.CONS
import com.tomerpacific.caridentifier.PROS
import com.tomerpacific.caridentifier.R
import com.tomerpacific.caridentifier.model.MainViewModel

@Composable
fun Advice(mainViewModel: MainViewModel, serverError: String?) {

    val carReview = mainViewModel.searchTermCompletionText.collectAsState()

    val columnVerticalArrangement: Arrangement.Vertical = when (carReview.value) {
        null -> Arrangement.Center
        else -> Arrangement.Top
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = columnVerticalArrangement
    ) {
        if (carReview.value == null && serverError == null) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                LoaderAnimation(R.raw.truck_loading_animation)
            }
        } else if (carReview.value != null) {

            val prosText: String = createBulletPoints(carReview.value!!.prosList)
            val consText: String = createBulletPoints(carReview.value!!.consList)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(stringResource(R.string.car_advice_header), fontSize = 25.sp, fontWeight = FontWeight.Bold)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("הבינה המלאכותית יכולה לטעות, אנא שימו לב", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.size(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    modifier = Modifier
                        .size(200.dp)
                        .border(
                            BorderStroke(1.dp, Color.Black),
                            CircleShape
                        )
                        .clip(CircleShape),
                    painter = painterResource(R.drawable.car_advice),
                    contentDescription = "mechanic in garage",
                )
            }

            AdviceList(title = PROS, adviceList = prosText, Modifier.align(Alignment.Start))
            AdviceList(title = CONS, adviceList = consText, Modifier.align(Alignment.Start))
        } else if (serverError != null) {
            Spacer(modifier = Modifier.size(20.dp))
            Text(
                text = " לא ניתן להשיג את פרטי הרכב. נסו שנית.",
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
        }
    }
}

@Composable
private fun AdviceList(title: String,
                       adviceList: String,
                       modifier: Modifier = Modifier) {
    Text(
        "$title:",
        fontSize = 25.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .padding(start = 5.dp)
    )
    Text(
        text = adviceList,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Start,
        modifier = Modifier.padding(start = 10.dp)
    )
}

fun createBulletPoints(list: List<String>): String {
    val paragraphStyle = ParagraphStyle(textIndent = TextIndent(restLine = 12.sp))
    return buildAnnotatedString {
        list.forEach {
            withStyle(style = paragraphStyle) {
                append(it)
                append("\n")
            }
        }
    }.text
}
