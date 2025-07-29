package com.abrar.astrolearn.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp

// Helper function to parse markdown-style bold text and headings
@Composable
fun parseMarkdownText(text: String): AnnotatedString {
    return buildAnnotatedString {
        val lines = text.split("\n")

        for (lineIndex in lines.indices) {
            val line = lines[lineIndex]

            // Handle heading (### text)
            if (line.startsWith("### ")) {
                val headingText = line.removePrefix("### ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp)) {
                    append(headingText)
                }
            } else {
                // Handle bold text (**text**)
                val parts = line.split("**")
                var isBold = false

                for (i in parts.indices) {
                    val part = parts[i]
                    if (part.isNotEmpty()) {
                        if (isBold) {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(part)
                            }
                        } else {
                            append(part)
                        }
                    }
                    isBold = !isBold
                }
            }

            // Add line break except for the last line
            if (lineIndex < lines.size - 1) {
                append("\n")
            }
        }
    }
}
