package com.example.engine

import java.math.BigDecimal
import java.math.RoundingMode

data class LongDivisionStep(
    val explanation: String,
    val currentQuotient: String,
    val normalizedDividend: String,
    val normalizedDivisor: String,
    val partialDividend: String,
    val subtrahend: String,
    val remainder: String,
    val broughtDownDigit: String? = null,
    val columnIndex: Int = 0
)

data class LongDivisionResult(
    val quotient: String,
    val remainder: String,
    val displayQuotient: String,
    val isRepeating: Boolean,
    val steps: List<LongDivisionStep>,
    val normalizedDividend: String = "",
    val normalizedDivisor: String = ""
)

data class ColumnAdditionStep(
    val posIndex: Int,
    val digit1: Int,
    val digit2: Int,
    val carryIn: Int,
    val sumVal: Int,
    val resultDigit: Int,
    val carryOut: Int,
    val explanation: String = ""
)

data class ColumnAdditionResult(
    val num1: String,
    val num2: String,
    val result: String,
    val carries: List<Int>,
    val steps: List<ColumnAdditionStep>
) {
    val sum: String get() = result
}

data class ColumnSubtractionResult(
    val num1: String,
    val num2: String,
    val result: String,
    val borrows: List<Boolean>
)

data class ColumnMultiplicationStep(
    val multiplierDigit: Int,
    val shift: Int,
    val partialProduct: String
)

data class ColumnMultiplicationResult(
    val multiplicand: String,
    val multiplier: String,
    val finalProduct: String,
    val partialSteps: List<ColumnMultiplicationStep>
)

object LongDivisionEngine {

    fun computeLongDivision(dividendStr: String, divisorStr: String): LongDivisionResult {
        val d1 = dividendStr.trim().toDoubleOrNull() ?: 0.0
        val d2 = divisorStr.trim().toDoubleOrNull() ?: 1.0
        if (d2 == 0.0) {
            return LongDivisionResult("0", "0", "Can't divide by zero", false, emptyList())
        }

        val quotientVal = d1 / d2
        val remainderVal = d1 % d2
        val isRepeating = d1 / d2 != BigDecimal(d1).divide(BigDecimal(d2), 10, RoundingMode.HALF_UP).toDouble()

        val quotientStr = if (quotientVal % 1.0 == 0.0) quotientVal.toLong().toString() else quotientVal.toString()
        val remainderStr = if (remainderVal % 1.0 == 0.0) remainderVal.toLong().toString() else remainderVal.toString()

        val steps = listOf(
            LongDivisionStep(
                explanation = "Divide $dividendStr by $divisorStr",
                currentQuotient = quotientStr,
                normalizedDividend = dividendStr,
                normalizedDivisor = divisorStr,
                partialDividend = dividendStr,
                subtrahend = (quotientVal.toLong() * d2.toLong()).toString(),
                remainder = remainderStr
            )
        )

        return LongDivisionResult(
            quotient = quotientStr,
            remainder = remainderStr,
            displayQuotient = quotientStr,
            isRepeating = isRepeating,
            steps = steps,
            normalizedDividend = dividendStr,
            normalizedDivisor = divisorStr
        )
    }

    fun computeColumnAddition(num1Str: String, num2Str: String): ColumnAdditionResult {
        val n1 = num1Str.trim()
        val n2 = num2Str.trim()
        val sum = (n1.toLongOrNull() ?: 0L) + (n2.toLongOrNull() ?: 0L)
        return ColumnAdditionResult(n1, n2, sum.toString(), emptyList(), emptyList())
    }

    fun computeColumnSubtraction(num1Str: String, num2Str: String): ColumnSubtractionResult {
        val n1 = num1Str.trim()
        val n2 = num2Str.trim()
        val diff = (n1.toLongOrNull() ?: 0L) - (n2.toLongOrNull() ?: 0L)
        return ColumnSubtractionResult(n1, n2, diff.toString(), emptyList())
    }

    fun computeColumnMultiplication(num1Str: String, num2Str: String): ColumnMultiplicationResult {
        val n1 = num1Str.trim()
        val n2 = num2Str.trim()
        val prod = (n1.toLongOrNull() ?: 0L) * (n2.toLongOrNull() ?: 0L)
        return ColumnMultiplicationResult(n1, n2, prod.toString(), emptyList())
    }
}
