package de.charlex.compose.unit

import androidx.compose.runtime.Composable
import kotlin.reflect.KClass

interface IUnit<UNIT_TYPE, VALUE_TYPE> {
    val value: VALUE_TYPE?
    val fieldType: KClass<*>
    val defaultSaveTargetType: UNIT_TYPE
    @Composable fun getCurrentUnitType(): UNIT_TYPE
    fun convertTo(targetType: UNIT_TYPE): IUnit<UNIT_TYPE, VALUE_TYPE>
    val valueString: String @Composable get
    val string: String @Composable get
    val unit: String @Composable get
    @Composable fun string(uiFormatPattern: String, unit: Boolean): String
    fun with(value: VALUE_TYPE?, type: UNIT_TYPE): IUnit<UNIT_TYPE, VALUE_TYPE>
    operator fun compareTo(other: IUnit<UNIT_TYPE, VALUE_TYPE>): Int
    operator fun compareTo(value: VALUE_TYPE): Int
}
