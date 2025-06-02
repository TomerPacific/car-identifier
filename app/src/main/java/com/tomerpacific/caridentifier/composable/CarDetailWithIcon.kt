package com.tomerpacific.caridentifier.composable

import android.view.View
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp

@Composable
fun CarDetailWithIcon(iconId: String,
                      labelText: String,
                      content: String,
                      icon: @Composable () -> Unit) {

    val annotatedString = when (LocalContext.current.resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL) {
        true ->
        buildAnnotatedString {
            appendInlineContent(iconId, "[icon]")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append(labelText)
                append(" ")
            }
            append(content)
        }
        false ->
            buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(labelText)
                    append(" ")
                }
                append(content)
                appendInlineContent(iconId, "[icon]")
        }
    }

    val inlineTextContent = mapOf(
        Pair(
            iconId,
            InlineTextContent(
                Placeholder(
                    width = 20.sp,
                    height = 20.sp,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                )
            ) {
                icon()
            }
        )
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(annotatedString,
            inlineContent = inlineTextContent,
            fontSize = 20.sp)
    }


}