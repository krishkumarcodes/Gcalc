package com.example.engine

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import kotlin.math.*

enum class AngleMode {
    DEG, RAD, GRAD
}

enum class NumberFormatMode {
    AUTO, DECIMAL, FRACTION, SCIENTIFIC, ENGINEERING
}

data class CalculationResult(
    val value: Double,
    val displayString: String,
    val fractionString: String? = null,
    val scientificString: String? = null,
    val isExact: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String? = null
)

object MathEngine {
    val MATH_CONTEXT = MathContext(64, RoundingMode.HALF_UP)

    fun toRadians(angle: Double, mode: AngleMode): Double = when (mode) {
        AngleMode.RAD -> angle
        AngleMode.DEG -> Math.toRadians(angle)
        AngleMode.GRAD -> angle * (PI / 200.0)
    }

    fun fromRadians(rad: Double, mode: AngleMode): Double = when (mode) {
        AngleMode.RAD -> rad
        AngleMode.DEG -> Math.toDegrees(rad)
        AngleMode.GRAD -> rad * (200.0 / PI)
    }

    fun cleanNumber(value: Double): Double {
        if (value.isNaN() || value.isInfinite()) return value
        if (abs(value) < 1e-12) return 0.0
        val roundInt = round(value)
        if (abs(value - roundInt) < 1e-11) return roundInt
        return value
    }

    fun formatNumber(
        value: Double,
        formatMode: NumberFormatMode = NumberFormatMode.AUTO,
        maxDecimals: Int = 14,
        thousandsSeparator: Boolean = true
    ): String {
        if (value.isNaN()) return "Invalid input"
        if (value.isInfinite()) return if (value > 0) "Infinity" else "-Infinity"
        val cleaned = cleanNumber(value)
        if (abs(cleaned) == 0.0) return "0"

        return when (formatMode) {
            NumberFormatMode.FRACTION -> toFraction(cleaned) ?: formatStandard(cleaned, maxDecimals, thousandsSeparator)
            NumberFormatMode.SCIENTIFIC -> formatScientific(cleaned, 6)
            NumberFormatMode.ENGINEERING -> formatEngineering(cleaned, 6)
            NumberFormatMode.DECIMAL -> formatStandard(cleaned, maxDecimals, thousandsSeparator)
            NumberFormatMode.AUTO -> {
                val absVal = abs(cleaned)
                if (absVal >= 1e12 || (absVal < 1e-6 && absVal > 0.0)) {
                    formatScientific(cleaned, 6)
                } else {
                    formatStandard(cleaned, maxDecimals, thousandsSeparator)
                }
            }
        }
    }

    private fun formatStandard(value: Double, maxDecimals: Int, thousandsSeparator: Boolean): String {
        val bd = try {
            BigDecimal.valueOf(value).setScale(maxDecimals, RoundingMode.HALF_UP).stripTrailingZeros()
        } catch (_: Exception) {
            BigDecimal.valueOf(value)
        }
        val plain = bd.toPlainString()
        if (!thousandsSeparator) return plain

        val parts = plain.split(".")
        val intPart = parts[0]
        val isNeg = intPart.startsWith("-")
        val cleanInt = if (isNeg) intPart.substring(1) else intPart

        val sb = StringBuilder()
        val len = cleanInt.length
        for (i in 0 until len) {
            sb.append(cleanInt[i])
            val rem = len - 1 - i
            if (rem > 0 && rem % 3 == 0) {
                sb.append(",")
            }
        }
        val formattedInt = if (isNeg) "-$sb" else sb.toString()
        return if (parts.size > 1) "$formattedInt.${parts[1]}" else formattedInt
    }

    fun formatScientific(value: Double, precision: Int = 6): String {
        if (abs(value) == 0.0) return "0"
        val exponent = floor(log10(abs(value))).toInt()
        val mantissa = value / 10.0.pow(exponent.toDouble())
        val roundedMantissa = BigDecimal(mantissa).setScale(precision, RoundingMode.HALF_UP).stripTrailingZeros()
        return "${roundedMantissa.toPlainString()} × 10^$exponent"
    }

    fun formatEngineering(value: Double, precision: Int = 6): String {
        if (abs(value) == 0.0) return "0"
        val exp = floor(log10(abs(value))).toInt()
        val remainder = exp % 3
        val engExp = if (remainder >= 0) exp - remainder else exp - (remainder + 3)
        val mantissa = value / 10.0.pow(engExp.toDouble())
        val roundedMantissa = BigDecimal(mantissa).setScale(precision, RoundingMode.HALF_UP).stripTrailingZeros()
        val plainStr = roundedMantissa.toPlainString()
        return if (engExp != 0) "$plainStr × 10^$engExp" else plainStr
    }

    fun toFraction(value: Double, maxDenominator: Long = 100000L): String? {
        if (value.isNaN() || value.isInfinite()) return null
        val sign = if (value < 0) "-" else ""
        val x = abs(value)
        val intPart = floor(x).toLong()
        val fracPart = x - intPart
        if (fracPart < 1e-9) return "$sign$intPart"

        var h1 = 1L
        var h0 = 0L
        var k1 = 0L
        var k0 = 1L
        var b = fracPart

        while (true) {
            val a = floor(b).toLong()
            val auxH = h1
            h1 = a * h1 + h0
            h0 = auxH

            val auxK = k1
            k1 = a * k1 + k0
            k0 = auxK

            if (b - a < 1e-12) break
            b = 1.0 / (b - a)
            if (abs(fracPart - (h1.toDouble() / k1.toDouble())) <= 1e-9 || k1 >= maxDenominator) break
        }

        val totalNum = intPart * k1 + h1
        return if (k1 > 1) "$sign$totalNum/$k1" else "$sign$totalNum"
    }

    fun factorial(n: Double): Double {
        if (n >= 0 && n == floor(n)) {
            if (n == 0.0 || n == 1.0) return 1.0
            if (n == 20.0) return 2.43290200817664e18
            if (n > 170.0) return Double.POSITIVE_INFINITY
            var res = 1.0
            val count = n.toInt()
            for (i in 2..count) {
                res *= i
            }
            return res
        }
        throw IllegalArgumentException("Domain error")
    }

    fun nPr(n: Double, r: Double): Double {
        if (n >= 0 && r >= 0 && r <= n && n == floor(n) && r == floor(r)) {
            if (r == 0.0) return 1.0
            var res = 1.0
            val count = r.toInt()
            val nLong = n.toLong()
            for (i in 0 until count) {
                res *= (nLong - i)
            }
            return res
        }
        throw IllegalArgumentException("Domain error")
    }

    fun nCr(n: Double, r: Double): Double {
        if (n >= 0 && r >= 0 && r <= n && n == floor(n) && r == floor(r)) {
            if (r == 0.0 || r == n) return 1.0
            val rClean = min(r, n - r).toInt()
            var num = 1.0
            var den = 1.0
            val nLong = n.toLong()
            for (i in 1..rClean) {
                num *= (nLong - i + 1)
                den *= i
            }
            return num / den
        }
        throw IllegalArgumentException("Domain error")
    }

    fun sanitizeExpression(expr: String): String {
        var s = expr.trim()
            .replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")
            .replace("π", "PI")
            .replace("×10^", "*10^")
            .replace("EXP", "*10^")
            .replace("sin⁻¹", "asin")
            .replace("cos⁻¹", "acos")
            .replace("tan⁻¹", "atan")
            .replace("√", "sqrt")
            .replace("∛", "cbrt")
            .replace("ⁿPᵣ", "npr")
            .replace("ⁿCᵣ", "ncr")

        if (s.isEmpty()) return "0"

        var balance = 0
        for (c in s) {
            if (c == '(') balance++
            else if (c == ')') balance--
        }
        while (balance > 0) {
            s += ")"
            balance--
        }
        return s
    }

    fun evaluate(
        rawExpr: String,
        angleMode: AngleMode = AngleMode.DEG,
        ans: Double = 0.0,
        variables: Map<String, Double> = emptyMap()
    ): CalculationResult {
        return try {
            val sanitized = sanitizeExpression(rawExpr)
            val parser = ExpressionParser(sanitized, angleMode, ans, variables)
            val value = parser.parse()
            if (value.isNaN()) {
                CalculationResult(Double.NaN, "Domain error", isError = true, errorMessage = "Domain error")
            } else if (value.isInfinite()) {
                CalculationResult(value, "Can't divide by zero", isError = true, errorMessage = "Can't divide by zero")
            } else {
                val cleaned = cleanNumber(value)
                val displayStr = formatNumber(cleaned, NumberFormatMode.AUTO)
                val fracStr = toFraction(cleaned)
                val sciStr = formatScientific(cleaned)
                CalculationResult(cleaned, displayStr, fracStr, sciStr)
            }
        } catch (e: ArithmeticException) {
            val msg = e.message ?: "Can't divide by zero"
            CalculationResult(Double.NaN, msg, isError = true, errorMessage = msg)
        } catch (e: IllegalArgumentException) {
            val msg = e.message ?: "Domain error"
            CalculationResult(Double.NaN, msg, isError = true, errorMessage = msg)
        } catch (_: Exception) {
            CalculationResult(Double.NaN, "Invalid input", isError = true, errorMessage = "Invalid input")
        }
    }

    fun evaluateRepeatEquals(initialExpr: String, repeatCount: Int, angleMode: AngleMode = AngleMode.DEG): List<Double> {
        val results = mutableListOf<Double>()
        var current = evaluate(initialExpr, angleMode).value
        results.add(current)

        // Find last operation, e.g. "+ 2"
        val opRegex = Regex("""([+\-*/])\s*([0-9.]+)$""")
        val match = opRegex.find(initialExpr.trim())
        if (match != null && repeatCount > 1) {
            val op = match.groupValues[1]
            val operand = match.groupValues[2]
            for (i in 2..repeatCount) {
                val stepExpr = "$current $op $operand"
                current = evaluate(stepExpr, angleMode).value
                results.add(current)
            }
        }
        return results
    }
}

class ExpressionParser(
    private val text: String,
    private val angleMode: AngleMode,
    private val ansValue: Double,
    private val variables: Map<String, Double> = emptyMap()
) {
    private var pos = 0
    private val length = text.length
    private var lastOperandWasPercent = false

    private fun peek(): Char = if (pos < length) text[pos] else '\u0000'

    private fun get(): Char {
        if (pos >= length) return '\u0000'
        return text[pos++]
    }

    private fun skipWhitespace() {
        while (pos < length && text[pos].isWhitespace()) {
            pos++
        }
    }

    fun parse(): Double {
        skipWhitespace()
        if (pos >= length) return 0.0
        val res = parseAddSub()
        skipWhitespace()
        if (pos < length) {
            throw IllegalArgumentException("Invalid input")
        }
        return res
    }

    private fun parseAddSub(): Double {
        var left = parseMulDiv()
        while (true) {
            skipWhitespace()
            val c = peek()
            if (c != '+' && c != '-') break
            get()
            lastOperandWasPercent = false
            val right = parseMulDiv()
            if (lastOperandWasPercent) {
                val delta = left * right
                left = if (c == '+') left + delta else left - delta
                lastOperandWasPercent = false
            } else {
                left = if (c == '+') left + right else left - right
            }
        }
        return left
    }

    private fun parseMulDiv(): Double {
        var left = parseUnary()
        while (true) {
            skipWhitespace()
            val c = peek()
            if (c != '*' && c != '/' && c != 'm') break
            if (c == 'm' && text.substring(pos).startsWith("mod")) {
                pos += 3
                val right = parseUnary()
                left %= right
            } else if (c == '*') {
                get()
                val right = parseUnary()
                left *= right
            } else if (c == '/') {
                get()
                val right = parseUnary()
                if (right == 0.0) throw ArithmeticException("Can't divide by zero")
                left /= right
            } else {
                break
            }
        }
        return left
    }

    private fun parseUnary(): Double {
        skipWhitespace()
        val c = peek()
        if (c == '+') {
            get()
            return parseUnary()
        }
        if (c == '-') {
            get()
            return -parseUnary()
        }
        return parsePowerAndPostfix()
    }

    private fun parsePowerAndPostfix(): Double {
        var left = parsePrimary()
        while (true) {
            skipWhitespace()
            val c = peek()
            if (c == '^') {
                get()
                val right = parseUnary()
                left = left.pow(right)
            } else if (c == '!') {
                get()
                left = MathEngine.factorial(left)
            } else if (c == '%') {
                get()
                left /= 100.0
                lastOperandWasPercent = true
            } else {
                break
            }
        }
        return left
    }

    private fun parsePrimary(): Double {
        skipWhitespace()
        val c = peek()
        if (c == '(') {
            get()
            val res = parseAddSub()
            skipWhitespace()
            if (peek() == ')') {
                get()
            }
            return res
        }
        if (c.isDigit() || c == '.') {
            return parseNumber()
        }
        if (c.isLetter()) {
            return parseIdentifierOrFunction()
        }
        throw IllegalArgumentException("Invalid input")
    }

    private fun parseNumber(): Double {
        val start = pos
        var hasDot = false
        while (pos < length) {
            val ch = text[pos]
            if (ch.isDigit()) {
                pos++
            } else if (ch == '.' && !hasDot) {
                hasDot = true
                pos++
            } else {
                break
            }
        }
        val str = text.substring(start, pos)
        return str.toDoubleOrNull() ?: throw IllegalArgumentException("Invalid input")
    }

    private fun parseIdentifierOrFunction(): Double {
        val start = pos
        while (pos < length && (text[pos].isLetterOrDigit() || text[pos] == '_')) {
            pos++
        }
        val name = text.substring(start, pos)
        skipWhitespace()

        if (peek() == '(') {
            get()
            val args = mutableListOf<Double>()
            if (peek() != ')') {
                while (true) {
                    args.add(parseAddSub())
                    skipWhitespace()
                    if (peek() == ',') {
                        get()
                    } else {
                        break
                    }
                }
            }
            skipWhitespace()
            if (peek() == ')') {
                get()
            }
            return evaluateFunction(name, args)
        }

        // Variable or constant
        return when (name) {
            "PI" -> PI
            "e" -> E
            "Ans" -> ansValue
            else -> variables[name] ?: variables[name.uppercase()] ?: throw IllegalArgumentException("Invalid input")
        }
    }

    private fun evaluateFunction(name: String, args: List<Double>): Double {
        if (args.isEmpty()) throw IllegalArgumentException("Invalid input")
        val x = args[0]
        return when (name) {
            "sin" -> sin(MathEngine.toRadians(x, angleMode))
            "cos" -> cos(MathEngine.toRadians(x, angleMode))
            "tan" -> {
                val rad = MathEngine.toRadians(x, angleMode)
                if (abs(cos(rad)) < 1e-12) throw ArithmeticException("Undefined")
                tan(rad)
            }
            "asin" -> {
                if (x < -1.0 || x > 1.0) throw IllegalArgumentException("Domain error")
                MathEngine.fromRadians(asin(x), angleMode)
            }
            "acos" -> {
                if (x < -1.0 || x > 1.0) throw IllegalArgumentException("Domain error")
                MathEngine.fromRadians(acos(x), angleMode)
            }
            "atan" -> MathEngine.fromRadians(atan(x), angleMode)
            "sinh" -> sinh(x)
            "cosh" -> cosh(x)
            "tanh" -> tanh(x)
            "log" -> {
                if (x <= 0) throw IllegalArgumentException("Domain error")
                log10(x)
            }
            "ln" -> {
                if (x <= 0) throw IllegalArgumentException("Domain error")
                ln(x)
            }
            "log2" -> {
                if (x <= 0) throw IllegalArgumentException("Domain error")
                log2(x)
            }
            "exp" -> exp(x)
            "sqrt" -> {
                if (x < 0) throw IllegalArgumentException("Domain error")
                sqrt(x)
            }
            "cbrt" -> cbrt(x)
            "abs" -> abs(x)
            "floor" -> floor(x)
            "ceil" -> ceil(x)
            "round" -> {
                if (args.size > 1) {
                    val decimals = args[1].toInt()
                    val factor = 10.0.pow(decimals.toDouble())
                    round(x * factor) / factor
                } else {
                    round(x)
                }
            }
            "ncr" -> {
                if (args.size < 2) throw IllegalArgumentException("Invalid input")
                MathEngine.nCr(x, args[1])
            }
            "npr" -> {
                if (args.size < 2) throw IllegalArgumentException("Invalid input")
                MathEngine.nPr(x, args[1])
            }
            "yroot" -> {
                if (args.size < 2) throw IllegalArgumentException("Invalid input")
                val n = args[1]
                if (n == 0.0) throw ArithmeticException("Can't divide by zero")
                x.pow(1.0 / n)
            }
            else -> throw IllegalArgumentException("Invalid input")
        }
    }
}
