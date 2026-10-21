package com.example.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.AngleMode
import com.example.engine.StepWorkingEngine
import com.example.ui.theme.LocalGlassColors
import com.example.ui.theme.SpaceGroteskFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowWorkingSheet(
    expression: String,
    angleMode: AngleMode,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val colors = LocalGlassColors.current
    val working = remember(expression, angleMode) {
        StepWorkingEngine.generateWorking(expression, angleMode)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = colors.background,
        contentColor = colors.textPrimary,
        scrimColor = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.6f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Notebook Working",
                    fontFamily = SpaceGroteskFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = colors.accent
                )
                Button(
                    onClick = {
                        val textToCopy = working.steps.joinToString("\n") { "Step ${it.stepNumber} [${it.title}]: ${it.expressionAfter}" }
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Gcalc Working", textToCopy))
                        Toast.makeText(context, "Copied working steps to clipboard", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.surfaceGlassSecondary)
                ) {
                    Text("Copy", color = colors.textPrimary, fontFamily = SpaceGroteskFontFamily)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(working.steps) { step ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(colors.surfaceGlass)
                            .border(1.dp, colors.borderGlass, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Step ${step.stepNumber}: ${step.title}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = colors.accent,
                                    fontFamily = SpaceGroteskFontFamily
                                )
                                Text(
                                    text = step.category,
                                    fontSize = 11.sp,
                                    color = colors.textTertiary,
                                    fontFamily = SpaceGroteskFontFamily
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = step.description,
                                fontSize = 12.sp,
                                color = colors.textSecondary,
                                fontFamily = SpaceGroteskFontFamily
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            NotebookMathDisplay(
                                mathString = step.expressionAfter,
                                textStyle = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(colors.accentContainer.copy(alpha = 0.3f))
                    .border(1.5.dp, colors.accent, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Final Answer",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = colors.accent,
                        fontFamily = SpaceGroteskFontFamily
                    )
                    Text(
                        text = working.finalResult,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = colors.textPrimary,
                        fontFamily = SpaceGroteskFontFamily
                    )
                }
            }
        }
    }
}
