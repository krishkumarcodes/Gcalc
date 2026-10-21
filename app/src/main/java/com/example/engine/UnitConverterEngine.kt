package com.example.engine

enum class UnitCategory(val displayName: String, val icon: String) {
    LENGTH("Length", "📏"),
    AREA("Area", "📐"),
    VOLUME("Volume", "🧪"),
    MASS("Mass", "⚖️"),
    TEMPERATURE("Temperature", "🌡️"),
    TIME("Time", "⏱️"),
    SPEED("Speed", "🚗"),
    PRESSURE("Pressure", "🎈"),
    ENERGY("Energy", "⚡"),
    POWER("Power", "🔌"),
    FORCE("Force", "💪"),
    ANGLE("Angle", "📐"),
    DATA_SIZE("Data Size", "💾"),
    FREQUENCY("Frequency", "📻"),
    FUEL_ECONOMY("Fuel Economy", "⛽")
}

data class UnitItem(
    val name: String,
    val symbol: String,
    val category: UnitCategory,
    val factorToBase: Double = 1.0
)

object UnitConverterEngine {

    val categories: Map<UnitCategory, List<UnitItem>> get() = units

    val units: Map<UnitCategory, List<UnitItem>> = mapOf(
        UnitCategory.LENGTH to listOf(
            UnitItem("Meter", "m", UnitCategory.LENGTH, 1.0),
            UnitItem("Kilometer", "km", UnitCategory.LENGTH, 1000.0),
            UnitItem("Centimeter", "cm", UnitCategory.LENGTH, 0.01),
            UnitItem("Millimeter", "mm", UnitCategory.LENGTH, 0.001),
            UnitItem("Micrometer", "µm", UnitCategory.LENGTH, 1e-6),
            UnitItem("Nanometer", "nm", UnitCategory.LENGTH, 1e-9),
            UnitItem("Mile", "mi", UnitCategory.LENGTH, 1609.344),
            UnitItem("Yard", "yd", UnitCategory.LENGTH, 0.9144),
            UnitItem("Foot", "ft", UnitCategory.LENGTH, 0.3048),
            UnitItem("Inch", "in", UnitCategory.LENGTH, 0.0254),
            UnitItem("Nautical Mile", "nmi", UnitCategory.LENGTH, 1852.0)
        ),
        UnitCategory.MASS to listOf(
            UnitItem("Kilogram", "kg", UnitCategory.MASS, 1.0),
            UnitItem("Gram", "g", UnitCategory.MASS, 0.001),
            UnitItem("Milligram", "mg", UnitCategory.MASS, 1e-6),
            UnitItem("Metric Ton", "t", UnitCategory.MASS, 1000.0),
            UnitItem("Pound", "lb", UnitCategory.MASS, 0.45359237),
            UnitItem("Ounce", "oz", UnitCategory.MASS, 0.028349523125)
        ),
        UnitCategory.TEMPERATURE to listOf(
            UnitItem("Celsius", "°C", UnitCategory.TEMPERATURE, 1.0),
            UnitItem("Fahrenheit", "°F", UnitCategory.TEMPERATURE, 1.0),
            UnitItem("Kelvin", "K", UnitCategory.TEMPERATURE, 1.0)
        ),
        UnitCategory.TIME to listOf(
            UnitItem("Second", "s", UnitCategory.TIME, 1.0),
            UnitItem("Millisecond", "ms", UnitCategory.TIME, 0.001),
            UnitItem("Minute", "min", UnitCategory.TIME, 60.0),
            UnitItem("Hour", "h", UnitCategory.TIME, 3600.0),
            UnitItem("Day", "d", UnitCategory.TIME, 86400.0),
            UnitItem("Week", "wk", UnitCategory.TIME, 604800.0),
            UnitItem("Year", "yr", UnitCategory.TIME, 31536000.0)
        ),
        UnitCategory.SPEED to listOf(
            UnitItem("Meters per second", "m/s", UnitCategory.SPEED, 1.0),
            UnitItem("Kilometers per hour", "km/h", UnitCategory.SPEED, 1.0 / 3.6),
            UnitItem("Miles per hour", "mph", UnitCategory.SPEED, 0.44704),
            UnitItem("Knot", "kn", UnitCategory.SPEED, 0.514444)
        ),
        UnitCategory.PRESSURE to listOf(
            UnitItem("Pascal", "Pa", UnitCategory.PRESSURE, 1.0),
            UnitItem("Kilopascal", "kPa", UnitCategory.PRESSURE, 1000.0),
            UnitItem("Bar", "bar", UnitCategory.PRESSURE, 100000.0),
            UnitItem("Atmosphere", "atm", UnitCategory.PRESSURE, 101325.0),
            UnitItem("PSI", "psi", UnitCategory.PRESSURE, 6894.757293)
        ),
        UnitCategory.DATA_SIZE to listOf(
            UnitItem("Byte", "B", UnitCategory.DATA_SIZE, 1.0),
            UnitItem("Kilobyte", "KB", UnitCategory.DATA_SIZE, 1000.0),
            UnitItem("Megabyte", "MB", UnitCategory.DATA_SIZE, 1e6),
            UnitItem("Gigabyte", "GB", UnitCategory.DATA_SIZE, 1e9),
            UnitItem("Terabyte", "TB", UnitCategory.DATA_SIZE, 1e12),
            UnitItem("Kibibyte", "KiB", UnitCategory.DATA_SIZE, 1024.0),
            UnitItem("Mebibyte", "MiB", UnitCategory.DATA_SIZE, 1048576.0),
            UnitItem("Gibibyte", "GiB", UnitCategory.DATA_SIZE, 1073741824.0)
        ),
        UnitCategory.AREA to listOf(
            UnitItem("Square Meter", "m²", UnitCategory.AREA, 1.0),
            UnitItem("Square Kilometer", "km²", UnitCategory.AREA, 1e6),
            UnitItem("Square Foot", "ft²", UnitCategory.AREA, 0.09290304),
            UnitItem("Acre", "ac", UnitCategory.AREA, 4046.8564224),
            UnitItem("Hectare", "ha", UnitCategory.AREA, 10000.0)
        ),
        UnitCategory.VOLUME to listOf(
            UnitItem("Cubic Meter", "m³", UnitCategory.VOLUME, 1.0),
            UnitItem("Liter", "L", UnitCategory.VOLUME, 0.001),
            UnitItem("Milliliter", "mL", UnitCategory.VOLUME, 1e-6),
            UnitItem("Gallon (US)", "gal", UnitCategory.VOLUME, 0.003785411784)
        ),
        UnitCategory.ENERGY to listOf(
            UnitItem("Joule", "J", UnitCategory.ENERGY, 1.0),
            UnitItem("Kilojoule", "kJ", UnitCategory.ENERGY, 1000.0),
            UnitItem("Calorie", "cal", UnitCategory.ENERGY, 4.184),
            UnitItem("Kilocalorie", "kcal", UnitCategory.ENERGY, 4184.0),
            UnitItem("Watt-hour", "Wh", UnitCategory.ENERGY, 3600.0),
            UnitItem("Kilowatt-hour", "kWh", UnitCategory.ENERGY, 3.6e6)
        ),
        UnitCategory.POWER to listOf(
            UnitItem("Watt", "W", UnitCategory.POWER, 1.0),
            UnitItem("Kilowatt", "kW", UnitCategory.POWER, 1000.0),
            UnitItem("Horsepower", "hp", UnitCategory.POWER, 745.699872)
        ),
        UnitCategory.FORCE to listOf(
            UnitItem("Newton", "N", UnitCategory.FORCE, 1.0),
            UnitItem("Kilonewton", "kN", UnitCategory.FORCE, 1000.0),
            UnitItem("Pound-force", "lbf", UnitCategory.FORCE, 4.4482216153)
        ),
        UnitCategory.ANGLE to listOf(
            UnitItem("Degree", "°", UnitCategory.ANGLE, 1.0),
            UnitItem("Radian", "rad", UnitCategory.ANGLE, 180.0 / Math.PI),
            UnitItem("Gradian", "gon", UnitCategory.ANGLE, 0.9)
        ),
        UnitCategory.FREQUENCY to listOf(
            UnitItem("Hertz", "Hz", UnitCategory.FREQUENCY, 1.0),
            UnitItem("Kilohertz", "kHz", UnitCategory.FREQUENCY, 1000.0),
            UnitItem("Megahertz", "MHz", UnitCategory.FREQUENCY, 1e6),
            UnitItem("Gigahertz", "GHz", UnitCategory.FREQUENCY, 1e9)
        ),
        UnitCategory.FUEL_ECONOMY to listOf(
            UnitItem("Miles per Gallon (US)", "mpg", UnitCategory.FUEL_ECONOMY, 1.0),
            UnitItem("Liters per 100km", "L/100km", UnitCategory.FUEL_ECONOMY, 1.0)
        )
    )

    fun convert(category: UnitCategory, value: Double, from: UnitItem, to: UnitItem): Double {
        return convert(value, from, to)
    }

    fun convert(value: Double, from: UnitItem, to: UnitItem): Double {
        if (from == to) return value
        if (from.category == UnitCategory.TEMPERATURE) {
            val celsius = when (from.symbol) {
                "°C" -> value
                "°F" -> (value - 32.0) * (5.0 / 9.0)
                "K" -> value - 273.15
                else -> value
            }
            return when (to.symbol) {
                "°C" -> celsius
                "°F" -> celsius * (9.0 / 5.0) + 32.0
                "K" -> celsius + 273.15
                else -> celsius
            }
        }
        val baseVal = value * from.factorToBase
        return baseVal / to.factorToBase
    }

    fun getFormula(category: UnitCategory, from: UnitItem, to: UnitItem): String {
        return getFormula(from, to)
    }

    fun getFormula(from: UnitItem, to: UnitItem): String {
        return "1 ${from.symbol} = ${convert(1.0, from, to)} ${to.symbol}"
    }
}
