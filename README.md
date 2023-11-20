# TextField with unit

<a href="https://github.com/ch4rl3x/textfield-unit/actions?query=workflow%3ABuild"><img src="https://github.com/ch4rl3x/textfield-unit/actions/workflows/build.yml/badge.svg" alt="Build"></a>
<a href="https://www.codefactor.io/repository/github/ch4rl3x/textfield-unit"><img src="https://www.codefactor.io/repository/github/ch4rl3x/textfield-unit/badge" alt="CodeFactor" /></a>
<a href="https://repo1.maven.org/maven2/de/charlex/compose/material/material-textfield-unit/"><img src="https://img.shields.io/maven-central/v/de.charlex.compose.material/material-textfield-unit" alt="Maven Central" /></a>
<a href="https://repo1.maven.org/maven2/de/charlex/compose/material3/material3-textfield-unit/"><img src="https://img.shields.io/maven-central/v/de.charlex.compose.material3/material3-textfield-unit" alt="Maven Central" /></a>

A simple to use Jetpack Compose TextField with automatically value transformations and user specific ui transformations. E.g. save temperature in °C, present as °F in ui

## Dependency

Add the library to your module `build.gradle`
```gradle
dependencies {
    implementation 'de.charlex.compose.material:material-textfield-unit:1.0.0-rc04'
}
```
or
```gradle
dependencies {
    implementation 'de.charlex.compose.material3:material-textfield3-unit:1.0.0-rc04'
}
```

## Usage

### Declaring custom units

```kotlin
val LocalTemperatureType = compositionLocalOf<Temp.TemperatureType> {
    error("LocalTemperature not present")
}
```

```kotlin
@Immutable
class Temp(value: Float?, type: TemperatureType = TemperatureType.Celsius) : BaseUnit<Temp.TemperatureType, Float>(value, type) {

    enum class TemperatureType(val id: Int, @StringRes override val unit: Int) : UnitType {
        Celsius(0, R.string.einheit_celsius),
        Fahrenheit(1, R.string.einheit_fahrenheit)
    }

    override fun convertTo(targetType: TemperatureType): Temp {
        if (targetType == type) return this

        val celsius = when (type) {
            TemperatureType.Celsius -> value
            TemperatureType.Fahrenheit -> value?.let { Umrechnungen.convertFarToCel(value) }
        }

        val newValue = when (targetType) {
            TemperatureType.Celsius -> celsius?.round(1)
            TemperatureType.Fahrenheit -> celsius?.let { Umrechnungen.convertCelToFar(celsius) }?.round(1)
        }
        return Temp(newValue, targetType)
    }

    @Composable
    override fun getCurrentUnitType(): TemperatureType {
        return LocalTemperatureType.current
    }

    override val defaultSaveTargetType: TemperatureType
        get() = TemperatureType.Celsius

    override fun with(value: Float?, type: TemperatureType): IUnit<TemperatureType, Float> = Temp(value, type)

    override fun compareTo(other: IUnit<TemperatureType, Float>): Int {
        return compareValues(value, other.value)
    }

    override fun compareTo(value: Float): Int {
        return compareValues(this.value, value)
    }
}

/**
 * Create a [Temp] using an [Int]:
 *     val left = 10
 *     val x = left.temp
 *     // -- or --
 *     val y = 10.temp
 */
@Stable
inline val Int?.temp: Temp
    get() = Temp(value = this?.takeIf { it != -1 }?.toFloat())

/**
 * Create a [Temp] using a [Double]:
 *     val left = 10.0
 *     val x = left.temp
 *     // -- or --
 *     val y = 10.0.temp
 */
@Stable
inline val Double?.temp: Temp
    get() = Temp(value = this?.takeIf { it != -1.0 }?.toFloat())

/**
 * Create a [Temp] using a [Float]:
 *     val left = 10f
 *     val x = left.temp
 *     // -- or --
 *     val y = 10f.temp
 */
@Stable
inline val Float?.temp: Temp
    get() = Temp(value = this?.takeIf { it != -1f })
```

### Using Preferences

```kotlin
/**
 * temperatureFromViewModel is always celsius
 */
val temperatureFromViewModel: Float? by remember { mutableStateOf(5f) }

/**
 * The provided LocalTemperatureType could be changed with settings from DataStore
 */
CompositionLocalProvider(
    LocalTemperatureType provides Temp.TemperatureType.Fahrenheit,
    LocalUiFormatPattern provides "#.#####",
) {
    /**
     * The user will see the fahrenheit value of the variable temperatureFromViewModel
     */
    TextFieldWithUnit(
        label = {
            Text(text = "Temperature")
        },
        unitedValue = temperatureFromViewModel.temp,
        onValueChange = { value ->
            /**
             * The user input will automatically be transformed back to celsius
             */
            temperatureFromViewModel = value
        },
        valueRepresentation = DefaultValueRepresentation(
            float = {
                it?.let { it.uiFormat("#.#") } ?: ""
            }
        )
    )
}
```

That's it!

License
--------

    Copyright 2023 Alexander Karkossa

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
