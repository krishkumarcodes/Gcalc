package com.example.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.components.GlassCard
import com.example.components.GlassKey
import com.example.components.GlassNumPad
import com.example.components.KeyType
import com.example.engine.MathEngine
import com.example.formulas.FormulaRepository
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalGlassColors
import com.example.ui.theme.SpaceGroteskFontFamily
import kotlin.math.*
import com.example.screens.tools.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput

private fun Modifier.scale(scale: Float) = this.graphicsLayer(scaleX = scale, scaleY = scale)

enum class ToolType(val title: String, val desc: String, val icon: String) {
    UNIT_CONVERTER("Unit Converter", "15 categories", "⇄"),
    PROGRAMMER_MODE("Programmer Mode", "HEX, DEC, OCT, BIN", "01"),
    EQUATION_SOLVER("Equation Solver", "Linear, Quadratic, Cubic", "ƒ(x)"),
    GRAPHING("Graphing Canvas", "Plot f(x) functions", "📈"),
    STATISTICS("Statistics & Chart", "Mean, median, std dev", "📊"),
    PERIODIC_TABLE("Periodic Table & Molar Mass", "118 elements", "⚛"),
    DATE_TIME("Date & Time", "Date diff, age, duration", "📅"),
    FINANCE("Finance Tools", "Loan, interest, tip", "💰"),
    CONSTANTS("Physical Constants", "Scientific constants", "ħ")
}

@Composable
fun ToolsScreen(
    onUseInCalculator: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalGlassColors.current
    val settingsStore = com.example.store.LocalSettingsStore.current
    var activeTool by remember { mutableStateOf<ToolType?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    if (activeTool != null) {
        val tool = activeTool!!
        LaunchedEffect(tool) {
            settingsStore.updateRecentTool(tool.name)
        }
        when (tool) {
            ToolType.UNIT_CONVERTER -> UnitConverterTool(onBack = { activeTool = null })
            ToolType.PROGRAMMER_MODE -> ProgrammerModeTool(onBack = { activeTool = null })
            ToolType.EQUATION_SOLVER -> EquationSolverTool(onBack = { activeTool = null })
            ToolType.GRAPHING -> GraphingTool(onBack = { activeTool = null })
            ToolType.STATISTICS -> StatisticsTool(onBack = { activeTool = null })
            ToolType.PERIODIC_TABLE -> PeriodicTableTool(onBack = { activeTool = null })
            ToolType.DATE_TIME -> DateTimeTool(onBack = { activeTool = null })
            ToolType.FINANCE -> FinanceTool(onBack = { activeTool = null })
            ToolType.CONSTANTS -> ConstantsTool(onBack = { activeTool = null }, onUseInCalculator = onUseInCalculator)
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Search Box
        GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 24.dp) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Search, contentDescription = null, tint = colors.accent, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Box(modifier = Modifier.weight(1f)) {
                    if (searchQuery.isEmpty()) {
                        Text("Search tools...", fontFamily = SpaceGroteskFontFamily, color = colors.textTertiary, fontSize = 15.sp)
                    }
                    androidx.compose.foundation.text.BasicTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontFamily = SpaceGroteskFontFamily,
                            color = colors.textPrimary,
                            fontSize = 15.sp
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        cursorBrush = androidx.compose.ui.graphics.SolidColor(colors.accent),
                        singleLine = true
                    )
                }
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, null, tint = colors.textTertiary, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        // Recently used tools
        if (settingsStore.recentlyUsedTools.isNotEmpty() && searchQuery.isEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Recently used",
                    fontFamily = SpaceGroteskFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = colors.textSecondary,
                    modifier = Modifier.padding(start = 4.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    settingsStore.recentlyUsedTools.forEach { toolName ->
                        val tool = ToolType.entries.find { it.name == toolName }
                        if (tool != null) {
                            RecentToolItem(tool = tool, onClick = { activeTool = tool })
                        }
                    }
                }
            }
        }

        // Tool Grid Header
        Text(
            text = if (searchQuery.isEmpty()) "All Tools" else "Search Results",
            fontFamily = SpaceGroteskFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = colors.textSecondary,
            modifier = Modifier.padding(start = 4.dp)
        )

        // Tool Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(ToolType.entries.filter { it.title.contains(searchQuery, true) || it.desc.contains(searchQuery, true) }) { tool ->
                ToolGridItem(tool = tool, onClick = { activeTool = tool })
            }
        }
    }
}

@Composable
fun RecentToolItem(tool: ToolType, onClick: () -> Unit) {
    val colors = LocalGlassColors.current
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (isPressed) 0.94f else 1f, label = "scale")

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { isPressed = true; tryAwaitRelease(); isPressed = false },
                    onTap = { onClick() }
                )
            }
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(colors.surfaceGlassSecondary)
                .border(1.dp, colors.borderGlass.copy(alpha = 0.3f), RoundedCornerShape(18.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(tool.icon, fontSize = 24.sp, color = colors.accent)
        }
        Text(
            text = tool.title.split(" ").first(),
            fontFamily = SpaceGroteskFontFamily,
            fontSize = 11.sp,
            color = colors.textSecondary,
            maxLines = 1
        )
    }
}

@Composable
fun ToolGridItem(tool: ToolType, onClick: () -> Unit) {
    val colors = LocalGlassColors.current
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (isPressed) 0.96f else 1f, label = "scale")
    val glowAlpha by animateFloatAsState(if (isPressed) 0.6f else 0f, label = "glow")

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { isPressed = true; tryAwaitRelease(); isPressed = false },
                    onTap = { onClick() }
                )
            },
        cornerRadius = 24.dp
    ) {
        // Soft Glow on press
        if (glowAlpha > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(colors.accent.copy(alpha = 0.15f * glowAlpha), Color.Transparent),
                            center = Offset.Zero
                        )
                    )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(colors.accentContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = tool.icon,
                    fontFamily = JetBrainsMonoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = colors.accent
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = tool.title,
                    fontFamily = SpaceGroteskFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = colors.textPrimary,
                    maxLines = 1
                )
                Text(
                    text = tool.desc,
                    fontFamily = SpaceGroteskFontFamily,
                    fontSize = 11.sp,
                    color = colors.textSecondary,
                    maxLines = 2,
                    lineHeight = 14.sp
                )
            }
        }
    }
}
