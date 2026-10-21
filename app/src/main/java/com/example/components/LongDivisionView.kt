package com.example.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.LongDivisionEngine
import com.example.engine.LongDivisionResult
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalGlassColors
import com.example.ui.theme.SpaceGroteskFontFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LongDivisionNotebookView(
    dividendStr: String,
    divisorStr: String,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalGlassColors.current
    val coroutineScope = rememberCoroutineScope()

    var activeTab by remember { mutableIntStateOf(0) } // 0: Long Division, 1: Column Multiplication, 2: Column Addition

    val divisionResult = remember(dividendStr, divisorStr) {
        try {
            LongDivisionEngine.computeLongDivision(dividendStr, divisorStr)
        } catch (_: Exception) {
            null
        }
    }

    val multResult = remember(dividendStr, divisorStr) {
        try {
            LongDivisionEngine.computeColumnMultiplication(dividendStr, divisorStr)
        } catch (_: Exception) {
            null
        }
    }

    val addResult = remember(dividendStr, divisorStr) {
        try {
            LongDivisionEngine.computeColumnAddition(dividendStr, divisorStr)
        } catch (_: Exception) {
            null
        }
    }

    val maxSteps = divisionResult?.steps?.size ?: 0
    var currentStepIndex by remember(dividendStr, divisorStr) { mutableIntStateOf(maxSteps) }
    var isPlaying by remember { mutableStateOf(false) }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            if (currentStepIndex >= maxSteps) currentStepIndex = 1
            while (isPlaying && currentStepIndex < maxSteps) {
                delay(900)
                currentStepIndex++
            }
            isPlaying = false
        }
    }

    GlassCard(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp),
        cornerRadius = 28.dp,
        backgroundColor = if (colors.isDark) Color(0xDD0F172A) else Color(0xF8F8FAFC)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(colors.accent)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Notebook Working",
                        fontFamily = SpaceGroteskFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = colors.textPrimary
                    )
                }
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = colors.textSecondary
                    )
                }
            }

            // Mode Selector Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(colors.surfaceGlassSecondary)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val tabTitles = listOf("Long Division", "Multiplication", "Addition & Carry")
                tabTitles.forEachIndexed { index, title ->
                    val selected = activeTab == index
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selected) colors.accent else Color.Transparent)
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            fontFamily = SpaceGroteskFontFamily,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp,
                            color = if (selected) Color.White else colors.textSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }

            if (divisionResult == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Unable to compute long working for these operands.",
                        color = colors.textSecondary,
                        fontFamily = SpaceGroteskFontFamily
                    )
                }
                return@GlassCard
            }

            when (activeTab) {
                0 -> {
                    // LONG DIVISION NOTEBOOK VIEW
                    // Playback Controls
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            FilledTonalIconButton(
                                onClick = {
                                    if (currentStepIndex > 1) currentStepIndex--
                                }
                            ) {
                                Icon(Icons.Default.SkipPrevious, "Previous Step")
                            }
                            FilledTonalIconButton(
                                onClick = { isPlaying = !isPlaying }
                            ) {
                                Icon(
                                    if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    "Play/Pause"
                                )
                            }
                            FilledTonalIconButton(
                                onClick = {
                                    if (currentStepIndex < maxSteps) currentStepIndex++
                                }
                            ) {
                                Icon(Icons.Default.SkipNext, "Next Step")
                            }
                        }

                        Button(
                            onClick = {
                                isPlaying = false
                                currentStepIndex = maxSteps
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = colors.surfaceGlassSecondary)
                        ) {
                            Text("Show All", color = colors.textPrimary, fontSize = 12.sp)
                        }

                        Text(
                            text = "Step $currentStepIndex of $maxSteps",
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 12.sp,
                            color = colors.accent
                        )
                    }

                    // Current step explanation pill
                    val visibleSteps = divisionResult.steps.take(currentStepIndex)
                    val activeStep = visibleSteps.lastOrNull()

                    if (activeStep != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(colors.accentContainer)
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "👉 " + activeStep.explanation,
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 12.sp,
                                color = colors.textPrimary
                            )
                        }
                    }

                    // Paper notebook simulation canvas
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (colors.isDark) Color(0xFF0B0F19) else Color(0xFFFFFFFF))
                            .border(1.dp, colors.borderGlass, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .horizontalScroll(rememberScrollState())
                        ) {
                            val activeQuotient = activeStep?.currentQuotient ?: divisionResult.quotient
                            val divisorText = divisionResult.normalizedDivisor
                            val dividendText = divisionResult.normalizedDividend
                            val leadSpace = " ".repeat(divisorText.length + 3)

                            // Quotient row on top of bracket
                            Text(
                                text = leadSpace + activeQuotient,
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = colors.accent,
                                letterSpacing = 2.sp
                            )

                            // Division Bracket: ⟌
                            // Divisor ) Dividend with top bar
                            val topBar = "─".repeat(dividendText.length + 2)
                            Text(
                                text = leadSpace + "┌" + topBar,
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 16.sp,
                                color = colors.textSecondary
                            )

                            Text(
                                text = "$divisorText │ $dividendText",
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 20.sp,
                                color = colors.textPrimary,
                                letterSpacing = 2.sp
                            )

                            // Step rows
                            visibleSteps.forEachIndexed { idx, step ->
                                val isCurrent = idx == visibleSteps.size - 1
                                val indent = " ".repeat(leadSpace.length + step.columnIndex)

                                // Subtrahend
                                Text(
                                    text = "$indent-${step.subtrahend}",
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontSize = 18.sp,
                                    color = if (isCurrent) Color(0xFFEF4444) else colors.textSecondary,
                                    letterSpacing = 2.sp
                                )

                                // Horizontal subtraction line
                                val lineLen = maxOf(step.partialDividend.length, step.subtrahend.length) + 2
                                Text(
                                    text = "$indent" + "─".repeat(lineLen),
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontSize = 14.sp,
                                    color = colors.borderGlass
                                )

                                // Remainder + Brought down digit
                                val remText = step.remainder + (step.broughtDownDigit ?: "")
                                Text(
                                    text = "$indent $remText",
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 18.sp,
                                    color = if (isCurrent) colors.accent else colors.textPrimary,
                                    letterSpacing = 2.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Summary Box
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(colors.surfaceGlassSecondary)
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "Result: ${divisionResult.displayQuotient}",
                                        fontFamily = SpaceGroteskFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = colors.accent
                                    )
                                    Text(
                                        text = "Remainder: ${divisionResult.remainder}" + (if (divisionResult.isRepeating) " (Repeating decimal detected)" else ""),
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontSize = 13.sp,
                                        color = colors.textSecondary
                                    )
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // COLUMN MULTIPLICATION
                    if (multResult != null) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (colors.isDark) Color(0xFF0B0F19) else Color(0xFFFFFFFF))
                                .border(1.dp, colors.borderGlass, RoundedCornerShape(16.dp))
                                .padding(16.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            val maxLen = maxOf(multResult.finalProduct.length, multResult.multiplicand.length, multResult.multiplier.length) + 4

                            Text(
                                text = multResult.multiplicand.padStart(maxLen, ' '),
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 20.sp,
                                color = colors.textPrimary,
                                letterSpacing = 2.sp
                            )
                            Text(
                                text = "× " + multResult.multiplier.padStart(maxLen - 2, ' '),
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 20.sp,
                                color = colors.textPrimary,
                                letterSpacing = 2.sp
                            )
                            Text(
                                text = "─".repeat(maxLen),
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 16.sp,
                                color = colors.borderGlass
                            )

                            multResult.partialSteps.forEach { step ->
                                val padded = (step.partialProduct + "0".repeat(step.shift)).padStart(maxLen, ' ')
                                Text(
                                    text = padded,
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontSize = 18.sp,
                                    color = colors.textSecondary,
                                    letterSpacing = 2.sp
                                )
                            }

                            if (multResult.partialSteps.size > 1) {
                                Text(
                                    text = "─".repeat(maxLen),
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontSize = 16.sp,
                                    color = colors.borderGlass
                                )
                            }

                            Text(
                                text = multResult.finalProduct.padStart(maxLen, ' '),
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp,
                                color = colors.accent,
                                letterSpacing = 2.sp
                            )
                        }
                    }
                }

                2 -> {
                    // COLUMN ADDITION WITH CARRY
                    if (addResult != null) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (colors.isDark) Color(0xFF0B0F19) else Color(0xFFFFFFFF))
                                .border(1.dp, colors.borderGlass, RoundedCornerShape(16.dp))
                                .padding(16.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            val maxLen = maxOf(addResult.sum.length, addResult.num1.length, addResult.num2.length) + 3

                            // Carry row
                            Text(
                                text = "Carry: " + addResult.carries.joinToString("").padStart(maxOf(0, maxLen - 7), ' '),
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 14.sp,
                                color = Color(0xFFF59E0B),
                                letterSpacing = 2.sp
                            )

                            Text(
                                text = addResult.num1.padStart(maxLen, ' '),
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 20.sp,
                                color = colors.textPrimary,
                                letterSpacing = 2.sp
                            )
                            Text(
                                text = "+ " + addResult.num2.padStart(maxLen - 2, ' '),
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 20.sp,
                                color = colors.textPrimary,
                                letterSpacing = 2.sp
                            )
                            Text(
                                text = "─".repeat(maxLen),
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 16.sp,
                                color = colors.borderGlass
                            )
                            Text(
                                text = addResult.sum.padStart(maxLen, ' '),
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp,
                                color = colors.accent,
                                letterSpacing = 2.sp
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Step explanations:",
                                fontFamily = SpaceGroteskFontFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                color = colors.textSecondary
                            )
                            addResult.steps.forEach { step ->
                                Text(
                                    text = "• ${step.explanation}",
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontSize = 12.sp,
                                    color = colors.textPrimary,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
