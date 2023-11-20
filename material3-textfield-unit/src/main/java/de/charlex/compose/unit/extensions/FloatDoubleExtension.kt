package de.charlex.compose.unit.extensions

import androidx.compose.runtime.Composable
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

@Composable
fun Float?.uiFormat(pattern: String = "#.#####"): String {
    return this?.let { DecimalFormat(pattern, DecimalFormatSymbols.getInstance()).format(this) } ?: ""
}

@Composable
fun Double?.uiFormat(pattern: String = "#.#####"): String {
    return this?.let { DecimalFormat(pattern, DecimalFormatSymbols.getInstance()).format(this) } ?: ""
}
