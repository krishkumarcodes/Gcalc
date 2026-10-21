package com.example.engine

import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.*

data class WorkingStep(
    val stepNumber: Int,
    val title: String,
    val description: String,
    val expressionAfter: String,
    val highlightedPart: String? = null,
    val category: String = "General" // "Brackets", "Powers", "Multiply/Divide", "Add/Subtract"
)

data class FullCalculationWorking(
    val rawExpression: String,
    val formattedResult: String,
    val numericResult: Double,
    val steps: List<WorkingStep>,
    val canShowLongDivision: Boolean = false,
    val canShowColumnMethod: Boolean = false,
    val operandA: String? = null,
    val operandB: String? = null,
    val operator: String? = null
) {
    val finalResult: String get() = formattedResult
}

object StepWorkingEngine {

    fun breakdownWorking(rawExpr: String): FullCalculationWorking = generateWorking(rawExpr)

    fun generateWorking(rawExpr: String, angleMode: AngleMode = AngleMode.DEG, ans: Double = 0.0): FullCalculationWorking {
        val eval = MathEngine.evaluate(rawExpr, angleMode, ans)
        val resultStr = if (eval.isError) eval.errorMessage ?: "Error" else eval.displayString

        val steps = mutableListOf<WorkingStep>()
        var currentExpr = rawExpr.trim()
            .replace("*", " × ")
            .replace("/", " ÷ ")
            .replace("-", " − ")

        val isSimpleTwoOperand = checkIfSimpleTwoOperand(rawExpr)

        if (eval.isError) {
            steps.add(
                WorkingStep(
                    stepNumber = 1,
                    title = "Evaluate Expression",
                    description = "An error occurred during evaluation: $resultStr",
                    expressionAfter = resultStr,
                    category = "Error"
                )
            )
            return FullCalculationWorking(
                rawExpression = rawExpr,
                formattedResult = resultStr,
                numericResult = Double.NaN,
                steps = steps
            )
        }

        // Generate BODMAS Breakdown steps
        var stepCount = 1

        // Step 1: Parentheses / Brackets
        if (currentExpr.contains("(") && currentExpr.contains(")")) {
            steps.add(
                WorkingStep(
                    stepNumber = stepCount++,
                    title = "Brackets First",
                    description = "Evaluate expressions inside parentheses according to BODMAS rules.",
                    expressionAfter = currentExpr,
                    category = "Brackets"
                )
            )
        }

        // Step 2: Powers, Roots, Factorials, Functions
        if (currentExpr.contains("^") || currentExpr.contains("!") || currentExpr.contains("sin") || currentExpr.contains("cos") || currentExpr.contains("tan") || currentExpr.contains("sqrt") || currentExpr.contains("√")) {
            val funcDesc = buildString {
                append("Evaluate powers, roots, factorials, and trigonometric functions.")
                if (currentExpr.contains("sin") || currentExpr.contains("cos") || currentExpr.contains("tan")) {
                    append(" (Angle mode: ${angleMode.name})")
                }
            }
            steps.add(
                WorkingStep(
                    stepNumber = stepCount++,
                    title = "Powers & Functions",
                    description = funcDesc,
                    expressionAfter = currentExpr,
                    category = "Powers"
                )
            )
        }

        // Step 3: Multiplication & Division / Fractions
        if (currentExpr.contains("×") || currentExpr.contains("÷") || currentExpr.contains("*") || currentExpr.contains("/")) {
            val isFractionMath = currentExpr.contains("/") || currentExpr.contains("÷")
            val desc = if (isFractionMath) {
                "Perform multiplication and division from left to right. Convert division to fraction form and simplify by dividing by GCD."
            } else {
                "Perform multiplication and division from left to right."
            }
            steps.add(
                WorkingStep(
                    stepNumber = stepCount++,
                    title = "Multiply & Divide",
                    description = desc,
                    expressionAfter = currentExpr,
                    category = "Multiply/Divide"
                )
            )
        }

        // Step 4: Addition & Subtraction
        if (currentExpr.contains("+") || currentExpr.contains("−") || currentExpr.contains("-")) {
            steps.add(
                WorkingStep(
                    stepNumber = stepCount++,
                    title = "Add & Subtract",
                    description = "Perform addition and subtraction from left to right to obtain the final answer.",
                    expressionAfter = resultStr,
                    category = "Add/Subtract"
                )
            )
        } else if (steps.isEmpty()) {
            steps.add(
                WorkingStep(
                    stepNumber = 1,
                    title = "Direct Evaluation",
                    description = "Evaluate statement directly.",
                    expressionAfter = resultStr,
                    category = "General"
                )
            )
        }

        // Final result step
        val fractionEquivalent = MathEngine.toFraction(eval.value)
        val finalDesc = if (fractionEquivalent != null && fractionEquivalent != resultStr && !resultStr.contains("/")) {
            "Final Answer: $resultStr (Fraction form: $fractionEquivalent)"
        } else {
            "Final Answer: $resultStr"
        }

        steps.add(
            WorkingStep(
                stepNumber = steps.size + 1,
                title = "Final Answer",
                description = finalDesc,
                expressionAfter = resultStr,
                category = "General"
            )
        )

        return FullCalculationWorking(
            rawExpression = rawExpr,
            formattedResult = resultStr,
            numericResult = eval.value,
            steps = steps,
            canShowLongDivision = isSimpleTwoOperand.first && isSimpleTwoOperand.second == "÷",
            canShowColumnMethod = isSimpleTwoOperand.first && isSimpleTwoOperand.second in listOf("+", "−", "×"),
            operandA = isSimpleTwoOperand.third,
            operandB = isSimpleTwoOperand.fourth,
            operator = isSimpleTwoOperand.second
        )
    }

    private fun checkIfSimpleTwoOperand(expr: String): Quadruple<Boolean, String?, String?, String?> {
        val s = expr.trim()
            .replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")

        val ops = listOf("+", "-", "*", "/")
        for (op in ops) {
            val parts = s.split(op)
            if (parts.size == 2) {
                val a = parts[0].trim().toDoubleOrNull()
                val b = parts[1].trim().toDoubleOrNull()
                if (a != null && b != null) {
                    val opSymbol = when (op) {
                        "+" -> "+"
                        "-" -> "−"
                        "*" -> "×"
                        "/" -> "÷"
                        else -> op
                    }
                    return Quadruple(true, opSymbol, parts[0].trim(), parts[1].trim())
                }
            }
        }
        return Quadruple(false, null, null, null)
    }
}

data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
