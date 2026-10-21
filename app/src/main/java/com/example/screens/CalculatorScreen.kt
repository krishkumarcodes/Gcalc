package com.example.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.components.GlassKey
import com.example.components.KeyType
import com.example.components.NotebookMathDisplay
import com.example.components.ShowWorkingSheet
import com.example.engine.AngleMode
import com.example.engine.MathEngine
import com.example.engine.NumberFormatMode
import com.example.service.LocalHapticFeedbackService
import com.example.store.HistoryRepository
import com.example.store.SettingsStore
import com.example.ui.theme.LocalGlassColors
import com.example.ui.theme.SpaceGroteskFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    settingsStore: SettingsStore,
    historyRepository: HistoryRepository,
    initialExpression: String? = null,
    onNavigateToFormulas: () -> Unit = {}
) {
    val context = LocalContext.current
    val colors = LocalGlassColors.current
    val haptic = LocalHapticFeedbackService.current

    var expression by remember { mutableStateOf(initialExpression ?: "") }
    var angleMode by remember { mutableStateOf(settingsStore.angleMode) }
    var formatMode by remember { mutableStateOf(settingsStore.numberFormatMode) }
    var showWorking by remember { mutableStateOf(false) }
    var isScientificOpen by remember { mutableStateOf(false) }

    val undoStack = remember { mutableStateListOf<String>() }
    val redoStack = remember { mutableStateListOf<String>() }

    fun updateExpression(newExpr: String) {
        if (newExpr != expression) {
            undoStack.add(expression)
            if (undoStack.size > 100) undoStack.removeAt(0)
            redoStack.clear()
            expression = newExpr
        }
    }

    val liveResult = remember(expression, angleMode, formatMode) {
        if (expression.isBlank()) null
        else {
            val eval = MathEngine.evaluate(expression, angleMode, settingsStore.memoryValue)
            if (eval.isError) eval
            else {
                val formatted = MathEngine.formatNumber(eval.value, formatMode, settingsStore.decimalPlaces, settingsStore.thousandsSeparator)
                eval.copy(displayString = formatted)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(16.dp)
    ) {
        // Top Toolbar (Angle Mode Chip, Format Chip, Working Button)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Angle Mode Toggle Chip
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(colors.accentContainer.copy(alpha = 0.4f))
                    .border(1.dp, colors.accent, RoundedCornerShape(20.dp))
                    .clickable {
                        angleMode = when (angleMode) {
                            AngleMode.DEG -> AngleMode.RAD
                            AngleMode.RAD -> AngleMode.GRAD
                            AngleMode.GRAD -> AngleMode.DEG
                        }
                        settingsStore.updateAngleMode(angleMode)
                        haptic?.performKeyTap(10L)
                    }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = angleMode.name,
                    fontFamily = SpaceGroteskFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = colors.accent
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Undo
                IconButton(
                    onClick = {
                        if (undoStack.isNotEmpty()) {
                            redoStack.add(expression)
                            expression = undoStack.removeAt(undoStack.lastIndex)
                            haptic?.performKeyTap(8L)
                        }
                    },
                    enabled = undoStack.isNotEmpty()
                ) {
                    Icon(
                        imageVector = Icons.Default.Undo,
                        contentDescription = "Undo",
                        tint = if (undoStack.isNotEmpty()) colors.textPrimary else colors.textTertiary
                    )
                }

                // Redo
                IconButton(
                    onClick = {
                        if (redoStack.isNotEmpty()) {
                            undoStack.add(expression)
                            expression = redoStack.removeAt(redoStack.lastIndex)
                            haptic?.performKeyTap(8L)
                        }
                    },
                    enabled = redoStack.isNotEmpty()
                ) {
                    Icon(
                        imageVector = Icons.Default.Redo,
                        contentDescription = "Redo",
                        tint = if (redoStack.isNotEmpty()) colors.textPrimary else colors.textTertiary
                    )
                }

                // Show Working Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(colors.surfaceGlass)
                        .border(1.dp, colors.borderGlass, RoundedCornerShape(20.dp))
                        .clickable { showWorking = true }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Show Working",
                            tint = colors.accent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Working",
                            fontFamily = SpaceGroteskFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp,
                            color = colors.textPrimary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Main Calculator Display Glass Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(24.dp))
                .background(colors.surfaceGlass)
                .border(1.5.dp, colors.borderGlassHighlight, RoundedCornerShape(24.dp))
                .padding(20.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Bottom
            ) {
                if (expression.isNotEmpty()) {
                    if (settingsStore.showWorkingDefault && expression.contains("/")) {
                        NotebookMathDisplay(
                            mathString = expression,
                            textStyle = TextStyle(fontSize = 24.sp, color = colors.textSecondary)
                        )
                    } else {
                        Text(
                            text = expression,
                            fontSize = 28.sp,
                            color = colors.textSecondary,
                            fontFamily = SpaceGroteskFontFamily
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Text(
                    text = liveResult?.displayString ?: "0",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (liveResult?.isError == true) Color(0xFFEF4444) else colors.textPrimary,
                    fontFamily = SpaceGroteskFontFamily
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Scientific Panel Toggle Handle
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(colors.surfaceGlassSecondary)
                .clickable { isScientificOpen = !isScientificOpen }
                .padding(vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isScientificOpen) "▼ Standard Keypad" else "▲ Scientific Keys",
                fontFamily = SpaceGroteskFontFamily,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = colors.accent
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Keypad Grid
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (isScientificOpen) {
                // Scientific Row 1: Trig
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    GlassKey("sin", modifier = Modifier.weight(1f), keyType = KeyType.OPERATOR) { updateExpression(expression + "sin(") }
                    GlassKey("cos", modifier = Modifier.weight(1f), keyType = KeyType.OPERATOR) { updateExpression(expression + "cos(") }
                    GlassKey("tan", modifier = Modifier.weight(1f), keyType = KeyType.OPERATOR) { updateExpression(expression + "tan(") }
                    GlassKey("ln", modifier = Modifier.weight(1f), keyType = KeyType.OPERATOR) { updateExpression(expression + "ln(") }
                    GlassKey("log", modifier = Modifier.weight(1f), keyType = KeyType.OPERATOR) { updateExpression(expression + "log(") }
                }

                // Scientific Row 2: Powers & Memory
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    GlassKey("x²", modifier = Modifier.weight(1f), keyType = KeyType.OPERATOR) { updateExpression(expression + "^2") }
                    GlassKey("xⁿ", modifier = Modifier.weight(1f), keyType = KeyType.OPERATOR) { updateExpression(expression + "^") }
                    GlassKey("√", modifier = Modifier.weight(1f), keyType = KeyType.OPERATOR) { updateExpression(expression + "sqrt(") }
                    GlassKey("M+", modifier = Modifier.weight(1f), keyType = KeyType.ACTION) {
                        val currentVal = liveResult?.value ?: 0.0
                        settingsStore.updateMemoryValue(settingsStore.memoryValue + currentVal)
                        Toast.makeText(context, "M = ${settingsStore.memoryValue}", Toast.LENGTH_SHORT).show()
                    }
                    GlassKey("MR", modifier = Modifier.weight(1f), keyType = KeyType.ACTION) {
                        updateExpression(expression + settingsStore.memoryValue.toString())
                    }
                }
            }

            // Standard Keypad Row 1: AC, (), %, ÷
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                GlassKey("AC", modifier = Modifier.weight(1f), keyType = KeyType.ACTION) {
                    expression = ""
                    undoStack.clear()
                    redoStack.clear()
                }
                GlassKey("( )", modifier = Modifier.weight(1f), keyType = KeyType.OPERATOR) {
                    val openCount = expression.count { it == '(' }
                    val closeCount = expression.count { it == ')' }
                    if (openCount > closeCount && expression.lastOrNull()?.isDigit() == true) {
                        updateExpression("$expression)")
                    } else {
                        updateExpression("$expression(")
                    }
                }
                GlassKey("%", modifier = Modifier.weight(1f), keyType = KeyType.OPERATOR) { updateExpression("$expression%") }
                GlassKey("÷", modifier = Modifier.weight(1f), keyType = KeyType.OPERATOR) { updateExpression("$expression/") }
            }

            // Standard Row 2: 7, 8, 9, ×
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                GlassKey("7", modifier = Modifier.weight(1f)) { updateExpression(expression + "7") }
                GlassKey("8", modifier = Modifier.weight(1f)) { updateExpression(expression + "8") }
                GlassKey("9", modifier = Modifier.weight(1f)) { updateExpression(expression + "9") }
                GlassKey("×", modifier = Modifier.weight(1f), keyType = KeyType.OPERATOR) { updateExpression("$expression*") }
            }

            // Standard Row 3: 4, 5, 6, −
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                GlassKey("4", modifier = Modifier.weight(1f)) { updateExpression(expression + "4") }
                GlassKey("5", modifier = Modifier.weight(1f)) { updateExpression(expression + "5") }
                GlassKey("6", modifier = Modifier.weight(1f)) { updateExpression(expression + "6") }
                GlassKey("−", modifier = Modifier.weight(1f), keyType = KeyType.OPERATOR) { updateExpression("$expression-") }
            }

            // Standard Row 4: 1, 2, 3, +
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                GlassKey("1", modifier = Modifier.weight(1f)) { updateExpression(expression + "1") }
                GlassKey("2", modifier = Modifier.weight(1f)) { updateExpression(expression + "2") }
                GlassKey("3", modifier = Modifier.weight(1f)) { updateExpression(expression + "3") }
                GlassKey("+", modifier = Modifier.weight(1f), keyType = KeyType.OPERATOR) { updateExpression("$expression+") }
            }

            // Standard Row 5: 0, ., ⌫, =
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (settingsStore.wideZero) {
                    GlassKey("0", modifier = Modifier.weight(2f)) { updateExpression(expression + "0") }
                } else {
                    GlassKey("0", modifier = Modifier.weight(1f)) { updateExpression(expression + "0") }
                    GlassKey(".", modifier = Modifier.weight(1f)) { updateExpression(expression + ".") }
                }

                GlassKey(
                    text = "⌫",
                    onClick = {
                        if (expression.isNotEmpty()) {
                            updateExpression(expression.dropLast(1))
                        }
                    },
                    modifier = Modifier.weight(1f),
                    keyType = KeyType.ACTION,
                    onLongClick = { expression = "" }
                )

                GlassKey("=", modifier = Modifier.weight(1f), keyType = KeyType.EQUALS) {
                    if (expression.isNotEmpty() && liveResult != null && !liveResult.isError) {
                        historyRepository.addHistoryItem(
                            expression = expression,
                            result = liveResult.displayString,
                            numericResult = liveResult.value
                        )
                        expression = liveResult.displayString
                    }
                }
            }
        }
    }

    if (showWorking) {
        ShowWorkingSheet(
            expression = if (expression.isBlank()) "0" else expression,
            angleMode = angleMode,
            onDismiss = { showWorking = false }
        )
    }
}
