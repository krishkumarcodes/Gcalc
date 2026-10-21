package com.example.formulas

import com.example.engine.CalculationResult

data class ChemicalElement(
    val atomicNumber: Int,
    val symbol: String,
    val name: String,
    val atomicMass: Double,
    val category: String,
    val period: Int,
    val group: Int
)

data class ScientificConstant(
    val id: String,
    val symbol: String,
    val name: String,
    val value: Double,
    val unit: String,
    val displayValue: String,
    val desc: String
)

enum class FormulaCategory(val displayName: String) {
    PHYSICS("Physics"),
    CHEMISTRY("Chemistry"),
    COMPUTER_SCIENCE("Computer Science"),
    MATHS("Mathematics")
}

data class FormulaVariable(
    val symbol: String,
    val name: String,
    val unit: String,
    val defaultValue: Double = 0.0,
    val min: Double? = null,
    val max: Double? = null
)

data class FormulaSubstitutionStep(
    val equationText: String,
    val substitutedText: String,
    val resultText: String
)

data class FormulaResult(
    val targetSymbol: String,
    val targetName: String,
    val value: Double,
    val unit: String,
    val steps: List<FormulaSubstitutionStep> = emptyList()
)

data class MathFormula(
    val id: String,
    val name: String,
    val latex: String,
    val category: String,
    val desc: String,
    val variables: List<String>,
    val solver: (Map<String, Double>, String) -> CalculationResult
)

data class Formula(
    val id: String,
    val name: String,
    val category: FormulaCategory,
    val subcategory: String,
    val mathExpression: String,
    val explanation: String,
    val variables: List<FormulaVariable>,
    val tags: List<String>,
    val solver: (Map<String, Double>, String) -> FormulaResult
)
