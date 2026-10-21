package com.example.screens.tools

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.components.*
import com.example.ui.theme.LocalGlassColors
import com.example.ui.theme.SpaceGroteskFontFamily
import com.example.ui.theme.JetBrainsMonoFontFamily
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.*

@Composable
fun StatisticsTool(onBack: () -> Unit) {
    BackHandler { onBack() }
    val colors = LocalGlassColors.current
    val data = remember { mutableStateListOf<Double>(12.0, 15.0, 14.0, 12.0, 25.0, 31.0, 22.0) }
    var inputStr by remember { mutableStateOf("") }
    
    val stats = remember(data.toList()) {
        if (data.isEmpty()) return@remember emptyMap<String, String>()
        val count = data.size
        val sum = data.sum()
        val mean = sum / count
        val sorted = data.sorted()
        val median = if (count % 2 == 1) sorted[count / 2] else (sorted[count / 2 - 1] + sorted[count / 2]) / 2.0
        
        val variance = data.sumOf { (it - mean).pow(2) } / count
        val stdDev = sqrt(variance)
        val min = data.minOrNull() ?: 0.0
        val max = data.maxOrNull() ?: 0.0
        
        val freqMap = data.groupingBy { it }.eachCount()
        val maxFreq = freqMap.values.maxOrNull() ?: 0
        val mode = freqMap.filter { it.value == maxFreq }.keys.joinToString(", ")

        linkedMapOf(
            "Count" to count.toString(),
            "Sum" to "%.4g".format(sum),
            "Mean" to "%.4g".format(mean),
            "Median" to "%.4g".format(median),
            "Mode" to mode,
            "Std Dev" to "%.4g".format(stdDev),
            "Min" to "%.4g".format(min),
            "Max" to "%.4g".format(max),
            "Range" to "%.4g".format(max - min),
            "Variance" to "%.4g".format(variance)
        )
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ToolHeader(title = "Statistics", icon = "📊", onBack = onBack)

        // Data Entry Card
        GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 24.dp) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Data Points", fontFamily = SpaceGroteskFontFamily, fontSize = 12.sp, color = colors.accent)
                
                LazyRow(
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    itemsIndexed(data) { index, value ->
                        GlassCard(
                            modifier = Modifier.clickable { data.removeAt(index) },
                            cornerRadius = 10.dp,
                            backgroundColor = colors.surfaceGlassSecondary
                        ) {
                            Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(value.toString().removeSuffix(".0"), fontFamily = JetBrainsMonoFontFamily, fontSize = 13.sp, color = colors.textPrimary)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.Close, null, tint = colors.textTertiary, modifier = Modifier.size(12.dp))
                            }
                        }
                    }
                    if (data.isEmpty()) {
                        item { Text("No data. Add numbers below.", fontSize = 12.sp, color = colors.textTertiary, fontFamily = SpaceGroteskFontFamily) }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.weight(1f).height(44.dp).clip(RoundedCornerShape(12.dp)).background(colors.surfaceGlassSecondary).padding(horizontal = 12.dp), contentAlignment = Alignment.CenterStart) {
                        if (inputStr.isEmpty()) Text("Add value...", fontSize = 14.sp, color = colors.textTertiary)
                        Text(inputStr, fontFamily = JetBrainsMonoFontFamily, color = colors.textPrimary)
                    }
                    Button(
                        onClick = { 
                            inputStr.toDoubleOrNull()?.let { data.add(it); inputStr = "" }
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(44.dp)
                    ) {
                        Text("Add")
                    }
                    IconButton(onClick = { data.clear() }) {
                        Icon(Icons.Default.DeleteSweep, null, tint = Color(0xFFEF4444))
                    }
                }
            }
        }

        // Stats Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.height(200.dp)
        ) {
            items(stats.toList()) { (label, value) ->
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 14.dp,
                    backgroundColor = colors.surfaceGlassSecondary
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(label, fontFamily = SpaceGroteskFontFamily, fontSize = 10.sp, color = colors.textSecondary)
                        Text(value, fontFamily = SpaceGroteskFontFamily, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = colors.accent)
                    }
                }
            }
        }

        // Chart Card
        GlassCard(modifier = Modifier.fillMaxWidth().weight(1f), cornerRadius = 24.dp) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Visual Distribution", fontFamily = SpaceGroteskFontFamily, fontSize = 12.sp, color = colors.textSecondary)
                Spacer(Modifier.height(16.dp))
                
                if (data.isNotEmpty()) {
                    val maxVal = data.maxOrNull() ?: 1.0
                    val minVal = data.minOrNull() ?: 0.0
                    val range = (maxVal - minVal).coerceAtLeast(1.0)
                    
                    Canvas(Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        
                        // Simple Bar Chart
                        val barWidth = w / (data.size * 1.5f).coerceAtLeast(1f)
                        data.forEachIndexed { i, v ->
                            val barHeight = ((v / maxVal) * h * 0.8f).toFloat()
                            val x = i * (barWidth * 1.5f)
                            val y = h - barHeight
                            
                            drawRect(
                                brush = androidx.compose.ui.graphics.Brush.verticalGradient(listOf(colors.accent, colors.accent.copy(alpha = 0.5f))),
                                topLeft = Offset(x, y),
                                size = Size(barWidth, barHeight)
                            )
                        }
                        
                        // Mean Line
                        val mean = data.sum() / data.size
                        val meanY = (h - (mean / maxVal) * h * 0.8f).toFloat()
                        drawLine(Color(0xFFEF4444), Offset(0f, meanY), Offset(w, meanY), 2f, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 10f)))
                    }
                } else {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Add data to see chart", color = colors.textTertiary, fontSize = 12.sp)
                    }
                }
            }
        }

        // Num Pad for Input
        GlassNumPad(
            onDigit = { d -> inputStr += d },
            onBackspace = { if(inputStr.isNotEmpty()) inputStr = inputStr.dropLast(1) },
            onClear = { inputStr = "" },
            onToggleSign = { inputStr = if(inputStr.startsWith("-")) inputStr.drop(1) else "-$inputStr" },
            onDone = { 
                inputStr.toDoubleOrNull()?.let { data.add(it); inputStr = "" }
            },
            modifier = Modifier.padding(bottom = 8.dp)
        )
    }
}
