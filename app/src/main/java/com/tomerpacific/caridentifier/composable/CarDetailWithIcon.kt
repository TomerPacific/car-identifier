package com.tomerpacific.caridentifier.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarDetailWithIcon(
    iconId: String,
    labelText: String,
    content: String,
    tooltipDescription: String,
    icon: @Composable () -> Unit,
) {
    val annotatedString =
        buildAnnotatedString {
            appendInlineContent(iconId, "[icon]")
            append(" ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append(labelText)
                append(" ")
            }
            append(content)
        }

    val inlineTextContent =
        mapOf(
            Pair(
                iconId,
                InlineTextContent(
                    Placeholder(
                        width = 20.sp,
                        height = 20.sp,
                        placeholderVerticalAlign = PlaceholderVerticalAlign.Center,
                    ),
                ) {
                    icon()
                },
            ),
        )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        TooltipBox(
            positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
            tooltip = {
                PlainTooltip {
                    Column(
                        modifier =
                            Modifier
                                .padding(8.dp)
                                .clip(
                                    RoundedCornerShape(16.dp),
                                )
                                .background(Color.White)
                                .padding(8.dp),
                    ) {
                        icon()
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(tooltipDescription, color = Color.Black, fontSize = 20.sp, lineHeight = 28.sp)
                    }
                }
            },
            state = rememberTooltipState(),
        ) {
            Text(
                annotatedString,
                inlineContent = inlineTextContent,
                fontSize = 20.sp,
            )
        }
    }
}
