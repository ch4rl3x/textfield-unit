package de.charlex.compose.material3

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextFieldDefaults.contentPaddingWithLabel
import androidx.compose.material3.TextFieldDefaults.indicatorLine
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import de.charlex.compose.unit.IUnit
import de.charlex.compose.unit.UnitType
import de.charlex.compose.unit.extensions.uiFormat
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.ParseException
import kotlin.Double
import kotlin.Float
import kotlin.Int
import kotlin.Long
import kotlin.String


@OptIn(ExperimentalMaterial3Api::class)
@Suppress("UNCHECKED_CAST")
@Composable
fun <UNIT_TYPE : UnitType, VALUE_TYPE> TextFieldWithUnit(
    modifier: Modifier = Modifier,
    unitWidth: UnitWidth = UnitWidth.NonFixed,
    label: @Composable (() -> Unit)? = null,
    customUnit: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    unit: IUnit<UNIT_TYPE, VALUE_TYPE>,
    enabled: Boolean = true,
    asLabel: Boolean = false,
    readOnly: Boolean = asLabel,
    valueRepresentation: ValueRepresentation = ValueRepresentation(),
    onValueChange: (VALUE_TYPE?) -> Unit = {},
    onUnitChange: (IUnit<UNIT_TYPE, VALUE_TYPE>) -> Unit = {},
    convertToDefaultSaveTargetType: Boolean = true,
    isError: Boolean = false,
    showIndicatorLine: Boolean = true,
    textStyle: TextStyle = LocalTextStyle.current.copy(
        fontWeight = FontWeight.Bold
    ),
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    maxLines: Int = Int.MAX_VALUE,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = TextFieldDefaults.shape,
    colors: TextFieldColors
) {

    val materialColors = TextFieldDefaults.colors(
        focusedTextColor = colors.focusedTextColor,
        unfocusedTextColor = colors.unfocusedTextColor,
        disabledTextColor = colors.disabledTextColor,
        errorTextColor = colors.errorTextColor,
        cursorColor = colors.cursorColor,
        errorCursorColor = colors.errorCursorColor,
        focusedLeadingIconColor = colors.focusedLeadingIconColor,
        unfocusedLeadingIconColor = colors.unfocusedLeadingIconColor,
        disabledLeadingIconColor = colors.disabledLeadingIconColor,
        errorLeadingIconColor = colors.errorLeadingIconColor,
        focusedTrailingIconColor = colors.focusedTrailingIconColor,
        unfocusedTrailingIconColor = colors.unfocusedTrailingIconColor,
        disabledTrailingIconColor = colors.disabledTrailingIconColor,
        errorTrailingIconColor = colors.errorTrailingIconColor,
        focusedLabelColor = colors.focusedLabelColor,
        unfocusedLabelColor = colors.unfocusedLabelColor,
        disabledLabelColor = colors.disabledLabelColor,
        errorLabelColor = colors.errorLabelColor
    )

    val defaultDecimalSeparator = DecimalFormatSymbols.getInstance().decimalSeparator
    val currentUnitType = unit.getCurrentUnitType()
    val paddingValues = contentPaddingWithLabel(
        start = 0.dp,
        end = 5.dp
    )

    val state by remember(unit, currentUnitType) {
        mutableStateOf(
            ValueTextFieldValue(
                value = unit.convertTo(currentUnitType).value,
                unit = currentUnitType.unit,
                type = when (unit.fieldType) {
                    String::class -> ValueType.String
                    Float::class -> ValueType.Float
                    Double::class -> ValueType.Double
                    Long::class -> ValueType.Long
                    Int::class -> ValueType.Int
                    else -> ValueType.Float
                }
            )
        )
    }

    val textColor = colors.textColor(
        enabled = enabled && (asLabel || readOnly.not()),
        readOnly = readOnly,
        isError = isError,
        interactionSource = interactionSource
    ).value
    val cursorColor = colors.cursorColor(isError = isError).value
    val labelColor = colors.labelColor(
        enabled = enabled,
        isError = isError,
        interactionSource = interactionSource
    ).value

    val leadingIconColor = colors.leadingIconColor(enabled, isError, interactionSource).value
    val trailingIconColor = colors.trailingIconColor(enabled, isError, interactionSource).value

    val mergedTextStyle = textStyle.copy(
        textAlign = when (state.type) {
            ValueType.String -> TextAlign.Start
            ValueType.Float -> TextAlign.End
            ValueType.Int -> TextAlign.End
            ValueType.Long -> TextAlign.End
            ValueType.Double -> TextAlign.End
        },
        color = textColor
    )

    val maxLinesTyped = when (state.type) {
        ValueType.String -> maxLines
        ValueType.Float -> 1
        ValueType.Int -> 1
        ValueType.Long -> 1
        ValueType.Double -> 1
    }

    val fieldTyp = remember {
        when (state.type) {
            ValueType.String -> FieldType.Text
            ValueType.Float -> FieldType.FloatingPoint
            ValueType.Int -> FieldType.Number
            ValueType.Long -> FieldType.Number
            ValueType.Double -> FieldType.FloatingPoint
        }
    }

    var lastCharDecimalSeperator by remember { mutableStateOf(false) }
    var leadingZeros by remember { mutableStateOf(0) }
    var trailingZeros by remember { mutableStateOf(0) }

    val modifierKeyboardOptions = keyboardOptions.copy(
        keyboardType = when (state.type) {
            ValueType.String -> KeyboardType.Text
            ValueType.Float -> KeyboardType.Decimal
            ValueType.Int -> KeyboardType.Number
            ValueType.Long -> KeyboardType.Number
            ValueType.Double -> KeyboardType.Decimal
        }
    )

    val stringRepresentation: String = when (state.type) {
        ValueType.String -> valueRepresentation.string(state.value as String?)
        ValueType.Float -> {
            buildString {
                append(valueRepresentation.float(state.value as Float?))
                if (lastCharDecimalSeperator && leadingZeros == 0 && trailingZeros == 0) {
                    append(defaultDecimalSeparator)
                } else if (leadingZeros > 0) {
                    append(defaultDecimalSeparator)
                    append((0 until leadingZeros).joinToString(separator = "") { "0" })
                } else if (trailingZeros > 0) {
                    append((0 until trailingZeros).joinToString(separator = "") { "0" })
                }
            }
        }
        ValueType.Int -> valueRepresentation.int(state.value as Int?)
        ValueType.Long -> valueRepresentation.long(state.value as Long?)
        ValueType.Double -> {
            buildString {
                append(valueRepresentation.double(state.value as Double?))
                if (lastCharDecimalSeperator && leadingZeros == 0 && trailingZeros == 0) {
                    append(defaultDecimalSeparator)
                } else if (leadingZeros > 0) {
                    append(defaultDecimalSeparator)
                    append((0 until leadingZeros).joinToString(separator = "") { "0" })
                } else if (trailingZeros > 0) {
                    append((0 until trailingZeros).joinToString(separator = "") { "0" })
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    val typeRepresentation: (String) -> VALUE_TYPE? = { stringValue: String ->
        when (state.type) {
            ValueType.String -> stringValue as VALUE_TYPE?
            ValueType.Float -> {
                val format = DecimalFormat("0.#", DecimalFormatSymbols.getInstance().also { it.decimalSeparator = defaultDecimalSeparator })
                try {
                    format.parse(stringValue)?.toFloat() as VALUE_TYPE?
                } catch (parseException: ParseException) {
                    null
                }
            }
            ValueType.Int -> stringValue.toIntOrNull() as VALUE_TYPE?
            ValueType.Long -> stringValue.toLongOrNull() as VALUE_TYPE?
            ValueType.Double -> {
                val format = DecimalFormat("0.#", DecimalFormatSymbols.getInstance().also { it.decimalSeparator = defaultDecimalSeparator })
                try {
                    format.parse(stringValue)?.toDouble() as VALUE_TYPE?
                } catch (parseException: ParseException) {
                    null
                }
            }
        }
    }

    val selectionColors = TextSelectionColors(
        handleColor = cursorColor,
        backgroundColor = cursorColor.copy(alpha = 0.4f)
    )
    CompositionLocalProvider(LocalTextSelectionColors provides selectionColors) {
        BasicTextField(
            enabled = enabled,
            readOnly = readOnly,
            modifier = modifier
                .background(Color.Transparent, shape)
                .then(
                    if (showIndicatorLine && readOnly.not()) Modifier.indicatorLine(
                        enabled,
                        isError,
                        interactionSource,
                        materialColors
                    ) else Modifier
                )
                .defaultMinSize(
                    minWidth = TextFieldDefaults.MinWidth,
                    minHeight = if (asLabel) 0.dp else TextFieldDefaults.MinHeight
                ),
            cursorBrush = SolidColor(cursorColor),
            visualTransformation = visualTransformation,
            keyboardOptions = modifierKeyboardOptions,
            keyboardActions = keyboardActions,
            interactionSource = interactionSource,
            maxLines = maxLinesTyped,
            value = stringRepresentation,
            onValueChange = {
                val fieldValue = when (fieldTyp) {
                    FieldType.FloatingPoint -> {
                        Log.d("TextFieldWithUnit", "FloatingPoint: $it")

                        it.filterIndexed { index, char ->
                            char.isDigit() || char == '.' || char == ',' || (char == '-' && index == 0)
                        }.let {
                            if (it.contains(',') || it.contains('.')) {
                                val useOfDefaultDecimalSeparator = it.contains(defaultDecimalSeparator)
                                val otherDecimalDeparator = if (defaultDecimalSeparator == ',') '.' else ','
                                if (useOfDefaultDecimalSeparator.not()) {
                                    it.replace(otherDecimalDeparator, defaultDecimalSeparator)
                                } else {
                                    it
                                }
                            } else {
                                it
                            }
                        }.let {
                            if ((it.lastOrNull() == defaultDecimalSeparator) && (it.count { it == defaultDecimalSeparator } > 1)) {

                                /**
                                 * Remove last character, because the text still contains a dot
                                 */
                                it.dropLast(1)
                            } else it
                        }.also {
                            lastCharDecimalSeperator = it.lastOrNull() == defaultDecimalSeparator

                            /**
                             * If the string has a comma/period, the last zeros are counted
                             */
                            val regExDecimalSeperator = if (defaultDecimalSeparator == ',') "," else "\\."
                            leadingZeros = if (Regex("$regExDecimalSeperator(0)*\$").containsMatchIn(it)) it.takeLastWhile { it == '0' }.count() else 0
                            trailingZeros = if (Regex("$regExDecimalSeperator(0)*\$").containsMatchIn(it).not() && Regex("(0)*\$").containsMatchIn(it) && it.contains(defaultDecimalSeparator)) it.takeLastWhile { it == '0' }.count() else 0
                        }
                    }
                    FieldType.Number -> {
                        it.filterIndexed { index, char ->
                            char.isDigit() || (char == '-' && index == 0)
                        }
                    }
                    FieldType.Text -> it
                }

                if (convertToDefaultSaveTargetType) {
                    onValueChange(unit.with(typeRepresentation(fieldValue), currentUnitType).convertTo(unit.defaultSaveTargetType).value)
                    onUnitChange(unit.with(typeRepresentation(fieldValue), currentUnitType).convertTo(unit.defaultSaveTargetType))
                } else {
                    onValueChange(unit.with(typeRepresentation(fieldValue), currentUnitType).value)
                    onUnitChange(unit.with(typeRepresentation(fieldValue), currentUnitType))
                }
//                when (state.type) {
//                    ValueType.String -> onValueChange(typeRepresentation(fieldValue))
//                    ValueType.Float -> {
//                        if (convertToDefaultSaveTargetType) {
//                            onValueChange(unitedValue.with(typeRepresentation(fieldValue) as VALUE_TYPE, currentUnitType).convertTo(unitedValue.defaultSaveTargetType).value as VALUE_TYPE)
//                            onUnitedValueChange(unitedValue.with(typeRepresentation(fieldValue) as VALUE_TYPE, currentUnitType).convertTo(unitedValue.defaultSaveTargetType))
//                        } else {
//                            onValueChange(unitedValue.with(typeRepresentation(fieldValue) as VALUE_TYPE, currentUnitType).value as VALUE_TYPE)
//                            onUnitedValueChange(unitedValue.with(typeRepresentation(fieldValue) as VALUE_TYPE, currentUnitType))
//                        }
//                    }
//                    ValueType.Int -> onValueChange(typeRepresentation(fieldValue))
//                    ValueType.Long -> onValueChange(typeRepresentation(fieldValue))
//                    ValueType.Double -> {
//                        onValueChange(typeRepresentation(fieldValue))
//                    }
//                }
            },
            textStyle = mergedTextStyle,
            decorationBox = @Composable { coreTextField ->
                val layoutDirection = LocalLayoutDirection.current
                val startTextFieldPadding =
                    paddingValues.calculateStartPadding(layoutDirection)
                val endTextFieldPadding = paddingValues.calculateEndPadding(layoutDirection)
                val padding = Modifier.padding(
                    start = if (leadingIcon != null) {
                        (startTextFieldPadding - 12.dp).coerceAtLeast(
                            0.dp
                        )
                    } else {
                        startTextFieldPadding
                    },
                    end = if (trailingIcon != null) {
                        (endTextFieldPadding - 12.dp).coerceAtLeast(0.dp)
                    } else {
                        endTextFieldPadding
                    }
                )

                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .then(padding),
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    if (leadingIcon != null) {
                        Box(
                            modifier = Modifier
                                .layoutId("Leading")
                                .defaultMinSize(48.dp, 48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CompositionLocalProvider(LocalContentColor provides leadingIconColor) {
                                leadingIcon()
                            }
                        }
                    }

                    label?.let {
                        Box(
                            Modifier
                                .weight(1f, true)
                                .layoutId("Label")
                        ) {
                            CompositionLocalProvider(LocalContentColor provides labelColor) {
                                it.invoke()
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .layoutId("TextField"),
                        contentAlignment = Alignment.CenterEnd,
                    ) {
                        coreTextField()
                    }

                    Box(
                        modifier = Modifier
                            .layoutId("TextFieldUnit")
                            .then(if (unitWidth is UnitWidth.Width) Modifier.requiredWidth(unitWidth.width) else Modifier),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (customUnit != null) {
                            customUnit()
                        } else {
                            state.unit?.let { unit ->
                                Text(
                                    text = stringResource(id = unit),
                                )
                            }
                        }
                    }

                    if (trailingIcon != null) {
                        Box(
                            modifier = Modifier
                                .layoutId("Trailing")
                                .defaultMinSize(48.dp, 48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CompositionLocalProvider(LocalContentColor provides trailingIconColor) {
                                trailingIcon()
                            }
                        }
                    }
                }
            }
        )
    }
}

@Immutable
data class ValueTextFieldValue<T>(
    val value: T?,
    val unit: Int? = null,
    val type: ValueType
)

enum class ValueType {
    String,
    Float,
    Int,
    Long,
    Double
}

enum class FieldType {
    FloatingPoint,
    Number,
    Text
}

// interface ValueRepresentation {
//    val int: @Composable (Int?) -> String
//    val float: @Composable (Float?) -> String
//    val long: @Composable (Long?) -> String
//    val double: @Composable (Double?) -> String
//    val string: @Composable (String?) -> String
//
//    companion object {
//        val DefaultValueRepresentation = object : ValueRepresentation {
//            override val int: @Composable (Int?) -> String = {
//                it?.toString() ?: ""
//            }
//            override val float: @Composable (Float?) -> String = {
//                it?.uiFormat("#.##") ?: ""
//            }
//            override val long: @Composable (Long?) -> String = {
//                it?.toString() ?: ""
//            }
//            override val double: @Composable (Double?) -> String = {
//                it?.uiFormat("#.##") ?: ""
//            }
//            override val string: @Composable (String?) -> String = {
//                it ?: ""
//            }
//        }
//    }
// }

data class ValueRepresentation(
    val int: @Composable (Int?) -> String = {
        it?.toString() ?: ""
    },
    val float: @Composable (Float?) -> String = {
        it?.uiFormat("#.##") ?: ""
    },
    val long: @Composable (Long?) -> String = {
        it?.toString() ?: ""
    },
    val double: @Composable (Double?) -> String = {
        it?.uiFormat("#.##") ?: ""
    },
    val string: @Composable (String?) -> String = {
        it ?: ""
    },
)

sealed class UnitWidth(open val width: Dp?) {
    object NonFixed : UnitWidth(null)
    data class Width(override val width: Dp) : UnitWidth(width)
}
