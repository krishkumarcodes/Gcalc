package com.example.screens.tools

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.components.GlassCard
import com.example.components.ToolHeader
import com.example.engine.MathEngine
import com.example.ui.theme.LocalGlassColors
import com.example.ui.theme.SpaceGroteskFontFamily
import com.example.ui.theme.JetBrainsMonoFontFamily
import kotlin.math.*

data class GraphFunction(
    val expression: String,
    val color: Color,
    val isVisible: Boolean = true
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GraphingTool(onBack: () -> Unit) {
    BackHandler { onBack() }
    val colors = LocalGlassColors.current
    val density = LocalDensity.current
    
    var functions by remember { mutableStateOf(listOf(
        GraphFunction("sin(x)", colors.accent),
        GraphFunction("cos(x)", Color(0xFFEC4899), false)
    )) }
    
    var scale by remember { mutableFloatStateOf(80f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    
    var showFunctionSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ToolHeader(title = "Graphing Canvas", icon = "📈", onBack = onBack)

        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(28.dp))
                .background(colors.surfaceGlass)
                .border(1.dp, colors.borderGlass, RoundedCornerShape(28.dp))
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale = (scale * zoom).coerceIn(10f, 2000f)
                        offsetX += pan.x
                        offsetY += pan.y
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures(onDoubleTap = { scale *= 1.5f })
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val center = Offset(w / 2f + offsetX, h / 2f + offsetY)
                
                // Adaptive step size for grid
                val step = when {
                    scale > 500 -> 10f * (scale / 100f).roundToInt() / 10f
                    scale > 100 -> 10f
                    scale > 20 -> 50f
                    else -> 100f
                }
                val pxStep = scale // 1 unit = scale pixels
                
                // Grid and Ticks
                val paint = android.graphics.Paint().apply {
                    color = if (colors.isDark) 0x66FFFFFF.toInt() else 0x66000000.toInt()
                    textSize = 24f
                    textAlign = android.graphics.Paint.Align.CENTER
                }

                // Draw X Axis ticks and grid
                var unitX = floor(-center.x / scale).toInt()
                while (unitX * scale + center.x < w + scale) {
                    val px = unitX * scale + center.x
                    if (px in 0f..w) {
                        drawLine(colors.textTertiary.copy(alpha = 0.05f), Offset(px, 0f), Offset(px, h), 1f)
                        if (unitX != 0) {
                            drawContext.canvas.nativeCanvas.drawText("$unitX", px, center.y.coerceIn(20f, h - 10f) + 30f, paint)
                        }
                    }
                    unitX++
                }

                // Draw Y Axis ticks and grid
                var unitY = floor((center.y - h) / scale).toInt()
                while (center.y - unitY * scale > -scale) {
                    val py = center.y - unitY * scale
                    if (py in 0f..h) {
                        drawLine(colors.textTertiary.copy(alpha = 0.05f), Offset(0f, py), Offset(w, py), 1f)
                        if (unitY != 0) {
                            drawContext.canvas.nativeCanvas.drawText("$unitY", center.x.coerceIn(20f, w - 20f) - 30f, py + 10f, paint)
                        }
                    }
                    unitY++
                }

                // Main Axes
                drawLine(colors.textSecondary.copy(alpha = 0.4f), Offset(0f, center.y), Offset(w, center.y), 2.5f)
                drawLine(colors.textSecondary.copy(alpha = 0.4f), Offset(center.x, 0f), Offset(center.x, h), 2.5f)

                // Plot Functions
                functions.filter { it.isVisible }.forEach { func ->
                    val path = Path()
                    var lastPy = 0f
                    var started = false
                    
                    val samplingStep = maxOf(1, (100f / scale).toInt())
                    
                    for (px in 0 until w.toInt() step samplingStep) {
                        val x = (px - center.x) / scale
                        val res = try { MathEngine.evaluate(func.expression, variables = mapOf("x" to x.toDouble())) } catch(_: Exception) { null }
                        
                        if (res != null && !res.isError) {
                            val py = (center.y - (res.value * scale)).toFloat()
                            
                            // Handle Discontinuities (asymptotes)
                            val isDiscontinuous = started && abs(py - lastPy) > h * 0.8f
                            
                            if (py in -h..h * 2 && !isDiscontinuous) {
                                if (!started) {
                                    path.moveTo(px.toFloat(), py)
                                    started = true
                                } else {
                                    path.lineTo(px.toFloat(), py)
                                }
                                lastPy = py
                            } else {
                                started = false
                            }
                        } else {
                            started = false
                        }
                    }
                    drawPath(path, func.color, style = Stroke(width = 4f, cap = androidx.compose.ui.graphics.StrokeCap.Round))
                }
            }

            // Toolbar Overlay
            Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Column(modifier = Modifier.align(Alignment.TopEnd), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    FloatingActionButton(
                        onClick = { offsetX = 0f; offsetY = 0f; scale = 80f },
                        containerColor = colors.surfaceGlass,
                        contentColor = colors.accent,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(Icons.Default.Refresh, "Reset", modifier = Modifier.size(20.dp))
                    }
                    FloatingActionButton(
                        onClick = { showFunctionSheet = true },
                        containerColor = colors.accent,
                        contentColor = Color.White,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(Icons.Default.Add, "Functions", modifier = Modifier.size(20.dp))
                    }
                }
                
                Text(
                    "Pinch to zoom • Pan to explore", 
                    modifier = Modifier.align(Alignment.BottomStart),
                    fontSize = 10.sp, color = colors.textTertiary, fontFamily = SpaceGroteskFontFamily
                )
            }
        }
        
        Spacer(modifier = Modifier.height(80.dp)) // Bottom nav space
    }

    if (showFunctionSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFunctionSheet = false },
            sheetState = sheetState,
            containerColor = colors.background
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp).heightIn(max = 400.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Functions", fontFamily = SpaceGroteskFontFamily, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = colors.textPrimary)
                    Button(onClick = { 
                        functions = functions + GraphFunction("x", colors.accent)
                    }) {
                        Text("Add f(x)")
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    itemsIndexed(functions) { index, func ->
                        GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 14.dp) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(func.color))
                                Spacer(modifier = Modifier.width(12.dp))
                                androidx.compose.foundation.text.BasicTextField(
                                    value = func.expression,
                                    onValueChange = { 
                                        val newList = functions.toMutableList()
                                        newList[index] = func.copy(expression = it)
                                        functions = newList
                                    },
                                    modifier = Modifier.weight(1f),
                                    textStyle = androidx.compose.ui.text.TextStyle(fontFamily = JetBrainsMonoFontFamily, color = colors.textPrimary)
                                )
                                IconButton(onClick = {
                                    val newList = functions.toMutableList()
                                    newList[index] = func.copy(isVisible = !func.isVisible)
                                    functions = newList
                                }) {
                                    Icon(if (func.isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, null, tint = colors.textTertiary)
                                }
                                IconButton(onClick = {
                                    functions = functions.filterIndexed { i, _ -> i != index }
                                }) {
                                    Icon(Icons.Default.Delete, null, tint = Color(0xFFEF4444))
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
