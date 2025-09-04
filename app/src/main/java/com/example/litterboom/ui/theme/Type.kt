package com.example.litterboom.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.litterboom.R

val LuloClean = FontFamily(
    Font(R.font.lulo_clean, FontWeight.Bold)
)

val Avenir = FontFamily(
    Font(R.font.avenir_light, FontWeight.Normal)
)

val AppTypography = Typography(
    // For large headings
    displayLarge = TextStyle(
        fontFamily = LuloClean,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    // For smaller headings
    headlineLarge = TextStyle(
        fontFamily = LuloClean,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    // For all body text, paragraphs, and input field labels
    bodyLarge = TextStyle(
        fontFamily = Avenir,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    // For button text and other small, important text
    labelLarge = TextStyle(
        fontFamily = Avenir,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.5.sp
    )
)