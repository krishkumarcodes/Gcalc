package com.example.screens.tools

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.components.GlassCard
import com.example.components.ToolHeader
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalGlassColors
import com.example.ui.theme.SpaceGroteskFontFamily
import kotlin.math.abs
import kotlin.math.sqrt

enum class EquationMode(val title: String, val formula: String) {
    LINEAR("Linear Equation", "ax + b = 0"),
    QUADRATIC("Quadratic Equation", "ax² + bx + c = 0"),
    SYSTEM_2X2("2x2 Linear System", "a₁x + b₁y = c₁\na₂x + b₂y = c₂")
}

@Composable
fun EquationSolverTool(onBack: () -> Unit) {
    BackHandler { onBack() }
    val colors = LocalGlassColors.current

    var selectedMode by remember { mutableStateOf(EquationMode.QUADRATIC) }

    // Coefficient state
    var aStr by remember { mutableStateOf("1") }
    var bStr by remember { mutableStateOf("-3") }
    var cStr by remember { mutableStateOf("2") }

    // 2x2 System
    var a1Str by remember { mutableStateOf("2") }
    var b1Str by remember { mutableStateOf("3") }
    var c1Str by remember { mutableStateOf("8") }
    var a2Str by remember { mutableStateOf("1") }
    var b2Str by remember { mutableStateOf("-1") }
    var c2Str by remember { mutableStateOf("1") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        ToolHeader(title = "Equation Solver", icon = "ƒ(x)", onBack = onBack)

        // Mode selector
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(EquationMode.entries) { mode ->
                val isSelected = selectedMode == mode
                GlassCard(
                    modifier = Modifier
                        .clickable { selectedMode = mode },
                    cornerRadius = 14.dp,
                    backgroundColor = if (isSelected) colors.accent else colors.surfaceGlassSecondary
                ) {
                    Text(
                        text = mode.title,
                        fontFamily = SpaceGroteskFontFamily,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 13.sp,
                        color = if (isSelected) Color.White else colors.textPrimary,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                    )
                }
            }
        }

        GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 20.dp) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = selectedMode.formula,
                    fontFamily = JetBrainsMonoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = colors.accent
                )

                when (selectedMode) {
                    EquationMode.LINEAR -> {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = aStr,
                                onValueChange = { aStr = it },
                                label = { Text("a") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = bStr,
                                onValueChange = { bStr = it },
                                label = { Text("b") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                    EquationMode.QUADRATIC -> {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = aStr,
                                onValueChange = { aStr = it },
                                label = { Text("a") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = bStr,
                                onValueChange = { bStr = it },
                                label = { Text("b") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = cStr,
                                onValueChange = { cStr = it },
                                label = { Text("c") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                    EquationMode.SYSTEM_2X2 -> {
                        Text("Eq 1: a₁x + b₁y = c₁", fontSize = 12.sp, color = colors.textSecondary)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = a1Str, onValueChange = { a1Str = it }, label = { Text("a₁") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
                            OutlinedTextField(value = b1Str, onValueChange = { b1Str = it }, label = { Text("b₁") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
                            OutlinedTextField(value = c1Str, onValueChange = { c1Str = it }, label = { Text("c₁") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
                        }
                        Text("Eq 2: a₂x + b₂y = c₂", fontSize = 12.sp, color = colors.textSecondary)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = a2Str, onValueChange = { a2Str = it }, label = { Text("a₂") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
                            OutlinedTextField(value = b2Str, onValueChange = { b2Str = it }, label = { Text("b₂") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
                            OutlinedTextField(value = c2Str, onValueChange = { c2Str = it }, label = { Text("c₂") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
                        }
                    }
                }
            }
        }

        // Solutions & Working
        GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 20.dp) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Solution & Step-by-Step", fontFamily = SpaceGroteskFontFamily, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = colors.textPrimary)

                when (selectedMode) {
                    EquationMode.LINEAR -> {
                        val a = aStr.toDoubleOrNull() ?: 1.0
                        val b = bStr.toDoubleOrNull() ?: 0.0
                        if (a == 0.0) {
                            Text("No unique solution (a = 0)", color = Color(0xFFEF4444))
                        } else {
                            val x = -b / a
                            Text("x = -b / a = -($b) / $a", fontFamily = JetBrainsMonoFontFamily, color = colors.textSecondary)
                            Text("x = $x", fontFamily = JetBrainsMonoFontFamily, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = colors.accent)
                        }
                    }
                    EquationMode.QUADRATIC -> {
                        val a = aStr.toDoubleOrNull() ?: 1.0
                        val b = bStr.toDoubleOrNull() ?: 0.0
                        val c = cStr.toDoubleOrNull() ?: 0.0
                        val disc = b * b - 4 * a * c

                        Text("Discriminant Δ = b² - 4ac = ($b)² - 4($a)($c) = $disc", fontFamily = JetBrainsMonoFontFamily, fontSize = 12.sp, color = colors.textSecondary)

                        if (a == 0.0) {
                            Text("a = 0: Degenerates to linear equation", color = colors.textSecondary)
                        } else if (disc > 0) {
                            val x1 = (-b + sqrt(disc)) / (2 * a)
                            val x2 = (-b - sqrt(disc)) / (2 * a)
                            Text("Two Real Roots:", fontWeight = FontWeight.Medium, color = colors.textPrimary)
                            Text("x₁ = $x1", fontFamily = JetBrainsMonoFontFamily, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = colors.accent)
                            Text("x₂ = $x2", fontFamily = JetBrainsMonoFontFamily, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = colors.accent)
                        } else if (disc == 0.0) {
                            val x = -b / (2 * a)
                            Text("One Repeated Real Root:", fontWeight = FontWeight.Medium, color = colors.textPrimary)
                            Text("x = $x", fontFamily = JetBrainsMonoFontFamily, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = colors.accent)
                        } else {
                            val real = -b / (2 * a)
                            val imag = sqrt(-disc) / (2 * a)
                            Text("Two Complex Conjugate Roots:", fontWeight = FontWeight.Medium, color = colors.textPrimary)
                            Text("x₁ = $real + ${abs(imag)}i", fontFamily = JetBrainsMonoFontFamily, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = colors.accent)
                            Text("x₂ = $real - ${abs(imag)}i", fontFamily = JetBrainsMonoFontFamily, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = colors.accent)
                        }
                    }
                    EquationMode.SYSTEM_2X2 -> {
                        val a1 = a1Str.toDoubleOrNull() ?: 1.0
                        val b1 = b1Str.toDoubleOrNull() ?: 1.0
                        val c1 = c1Str.toDoubleOrNull() ?: 1.0
                        val a2 = a2Str.toDoubleOrNull() ?: 1.0
                        val b2 = b2Str.toDoubleOrNull() ?: -1.0
                        val c2 = c2Str.toDoubleOrNull() ?: 0.0

                        val det = a1 * b2 - a2 * b1
                        if (det == 0.0) {
                            Text("Determinant = 0: System has parallel lines or infinite solutions", color = Color(0xFFEF4444))
                        } else {
                            val x = (c1 * b2 - c2 * b1) / det
                            val y = (a1 * c2 - a2 * c1) / det
                            Text("Determinant D = $det", fontFamily = JetBrainsMonoFontFamily, fontSize = 12.sp, color = colors.textSecondary)
                            Text("x = $x", fontFamily = JetBrainsMonoFontFamily, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = colors.accent)
                            Text("y = $y", fontFamily = JetBrainsMonoFontFamily, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = colors.accent)
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(20.dp))
    }
}
