package de.charlex.compose.textfield.unit

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import kotlin.reflect.KClass

abstract class BaseUnit<UNIT_TYPE : UnitType, VALUE_TYPE>(
    override val value: VALUE_TYPE?,
    val type: UNIT_TYPE,
    override val fieldType: KClass<*> = Float::class
) : IUnit<UNIT_TYPE, VALUE_TYPE> {

    override val valueString: String
        @Composable
        get() {
            return string(
                uiFormatPattern = LocalUiFormatPattern.current,
                unit = false
            )
        }

    override val string: String
        @Composable
        get() {
            return string(
                uiFormatPattern = LocalUiFormatPattern.current,
                unit = true
            )
        }

    override val unit: String
        @Composable
        get() = getCurrentUnitType().unit?.let { unitStringId -> stringResource(id = unitStringId) } ?: ""

    @Composable
    override fun string(uiFormatPattern: String, unit: Boolean): String {
        return convertTo(getCurrentUnitType()).let { convertedValue ->
            val formatted = convertedValue.value?.let { unboxedValue ->
                DecimalFormat(
                    uiFormatPattern,
                    DecimalFormatSymbols.getInstance()
                ).format(unboxedValue)
            } ?: ""
            "$formatted${if (unit)getCurrentUnitType().unit?.let { unitStringId -> " ${stringResource(id = unitStringId)}" } ?: "" else ""}"
        }
    }
}
