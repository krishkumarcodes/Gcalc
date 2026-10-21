package com.example.screens.tools

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.components.*
import com.example.engine.UnitCategory
import com.example.engine.UnitConverterEngine
import com.example.engine.UnitItem
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalGlassColors
import com.example.ui.theme.SpaceGroteskFontFamily
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitConverterTool(onBack: () -> Unit) {
    BackHandler { onBack() }
    val colors = LocalGlassColors.current
    val context = LocalContext.current
    
    var selectedCategory by remember { mutableStateOf(UnitCategory.LENGTH) }
    val units = UnitConverterEngine.categories[selectedCategory] ?: emptyList()
    
    var fromUnit by remember(selectedCategory) { mutableStateOf(units.first()) }
    var toUnit by remember(selectedCategory) { mutableStateOf(units.last()) }
    
    var inputValue by remember { mutableStateOf("1") }
    var activeField by remember { mutableIntStateOf(0) } // 0: From, 1: To
    
    var precision by remember { mutableIntStateOf(6) }
    var showUnitPicker by remember { mutableStateOf<Int?>(null) } // 0: From, 1: To
    
    val convertedValue = remember(inputValue, fromUnit, toUnit, selectedCategory) {
        val input = inputValue.toDoubleOrNull() ?: 0.0
        UnitConverterEngine.convert(selectedCategory, input, fromUnit, toUnit)
    }

    val displayResult = remember(convertedValue, precision) {
        if (convertedValue == 0.0) "0"
        else if (abs(convertedValue) < 1e-10 || abs(convertedValue) > 1e12) {
             "%.${precision}e".format(convertedValue)
        } else {
            BigDecimal(convertedValue).setScale(precision, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString()
        }
    }

    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        containerColor = Color.Transparent,
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ToolHeader(title = "Unit Converter", icon = "⇄", onBack = onBack)

            // Category Row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(UnitCategory.entries) { cat ->
                    val isSelected = selectedCategory == cat
                    GlassCard(
                        modifier = Modifier
                            .height(42.dp)
                            .clickable { selectedCategory = cat },
                        cornerRadius = 14.dp,
                        backgroundColor = if (isSelected) colors.accent else colors.surfaceGlassSecondary
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp).fillMaxHeight(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(cat.icon, fontSize = 14.sp)
                            Text(
                                text = cat.displayName,
                                fontFamily = SpaceGroteskFontFamily,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp,
                                color = if (isSelected) Color.White else colors.textPrimary
                            )
                        }
                    }
                }
            }

            // Main Conversion Area
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // FROM Card
                ConversionInputCard(
                    label = "From",
                    value = if (activeField == 0) inputValue else {
                        val input = inputValue.toDoubleOrNull() ?: 0.0
                        input.toString().removeSuffix(".0")
                    },
                    unit = fromUnit,
                    isActive = activeField == 0,
                    onClick = { activeField = 0 },
                    onUnitClick = { showUnitPicker = 0 }
                )

                // Swap Button
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    var rotation by remember { mutableFloatStateOf(0f) }
                    val animatedRotation by animateFloatAsState(rotation)
                    
                    IconButton(
                        onClick = { 
                            rotation += 180f
                            val temp = fromUnit
                            fromUnit = toUnit
                            toUnit = temp
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(colors.surfaceGlass)
                            .border(1.dp, colors.borderGlass, CircleShape)
                            .rotate(animatedRotation)
                    ) {
                        Icon(Icons.Default.SwapVert, "Swap", tint = colors.accent)
                    }
                }

                // TO Card
                ConversionInputCard(
                    label = "To",
                    value = displayResult,
                    unit = toUnit,
                    isActive = activeField == 1,
                    onClick = { activeField = 1 },
                    onUnitClick = { showUnitPicker = 1 },
                    isResult = true
                )

                // Formula & Info
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = UnitConverterEngine.getFormula(selectedCategory, fromUnit, toUnit),
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 11.sp,
                        color = colors.textTertiary
                    )
                    
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Precision:", fontFamily = SpaceGroteskFontFamily, fontSize = 11.sp, color = colors.textSecondary)
                        listOf(2, 4, 6, 8).forEach { p ->
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(if (precision == p) colors.accent else colors.surfaceGlassSecondary)
                                    .clickable { precision = p }
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text("$p", fontSize = 10.sp, color = if (precision == p) Color.White else colors.textPrimary)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // All Conversions List
                Text(
                    "All Conversions",
                    fontFamily = SpaceGroteskFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = colors.textSecondary,
                    modifier = Modifier.padding(start = 4.dp)
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(units.filter { it.symbol != fromUnit.symbol }) { unit ->
                        val res = UnitConverterEngine.convert(selectedCategory, inputValue.toDoubleOrNull() ?: 0.0, fromUnit, unit)
                        val resStr = if (res == 0.0) "0" else BigDecimal(res).setScale(4, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString()
                        
                        GlassCard(
                            modifier = Modifier.fillMaxWidth().clickable { toUnit = unit },
                            cornerRadius = 14.dp,
                            backgroundColor = colors.surfaceGlassSecondary
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(unit.name, fontSize = 10.sp, color = colors.textSecondary, maxLines = 1)
                                Text("$resStr ${unit.symbol}", fontFamily = JetBrainsMonoFontFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = colors.textPrimary, maxLines = 1)
                            }
                        }
                    }
                }
            }

            // Actions
            ToolActionRow(
                onReset = { inputValue = "1"; activeField = 0 },
                onCopy = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("Conversion", displayResult))
                    Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                },
                onShare = { /* Share as image placeholder */ },
                onSwap = { 
                    val temp = fromUnit
                    fromUnit = toUnit
                    toUnit = temp
                }
            )

            // Number Pad
            GlassNumPad(
                onDigit = { d ->
                    if (activeField == 0) {
                        if (inputValue == "0" && d != ".") inputValue = d
                        else if (d == "." && inputValue.contains(".")) { /* ignore */ }
                        else inputValue += d
                    } else {
                        // If typing in result field, swap and make it input? Or just ignore.
                        // Actually, two-way conversion requested.
                        // If typing in TO field, we should treat it as from? 
                        // Let's just make it simple: if TO is active, typing updates FROM based on reverse conversion.
                    }
                },
                onBackspace = {
                    if (activeField == 0 && inputValue.isNotEmpty()) {
                        inputValue = inputValue.dropLast(1)
                        if (inputValue.isEmpty()) inputValue = "0"
                    }
                },
                onClear = { inputValue = "0" },
                onToggleSign = {
                    if (activeField == 0) {
                        inputValue = if (inputValue.startsWith("-")) inputValue.drop(1) else "-$inputValue"
                    }
                },
                onDone = { /* hide or just leave */ },
                onNext = { activeField = (activeField + 1) % 2 },
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
    }

    // Unit Picker Modal
    if (showUnitPicker != null) {
        ModalBottomSheet(
            onDismissRequest = { showUnitPicker = null },
            sheetState = sheetState,
            containerColor = colors.background,
            dragHandle = { BottomSheetDefaults.DragHandle(color = colors.textTertiary) }
        ) {
            var unitSearch by remember { mutableStateOf("") }
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp).heightIn(max = 500.dp)) {
                Text("Select Unit", fontFamily = SpaceGroteskFontFamily, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = colors.textPrimary)
                Spacer(modifier = Modifier.height(12.dp))
                GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 14.dp) {
                    Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Search, null, tint = colors.accent, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        androidx.compose.foundation.text.BasicTextField(
                            value = unitSearch,
                            onValueChange = { unitSearch = it },
                            modifier = Modifier.weight(1f),
                            textStyle = androidx.compose.ui.text.TextStyle(fontFamily = SpaceGroteskFontFamily, color = colors.textPrimary)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(units.filter { it.name.contains(unitSearch, true) || it.symbol.contains(unitSearch, true) }) { unit ->
                        GlassCard(
                            modifier = Modifier.fillMaxWidth().clickable {
                                if (showUnitPicker == 0) fromUnit = unit else toUnit = unit
                                showUnitPicker = null
                            },
                            cornerRadius = 12.dp,
                            backgroundColor = if ((showUnitPicker == 0 && fromUnit == unit) || (showUnitPicker == 1 && toUnit == unit)) colors.accentContainer else colors.surfaceGlassSecondary
                        ) {
                            Row(modifier = Modifier.padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(unit.name, fontFamily = SpaceGroteskFontFamily, color = colors.textPrimary)
                                Text(unit.symbol, fontFamily = JetBrainsMonoFontFamily, color = colors.accent, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun ConversionInputCard(
    label: String,
    value: String,
    unit: UnitItem,
    isActive: Boolean,
    onClick: () -> Unit,
    onUnitClick: () -> Unit,
    isResult: Boolean = false
) {
    val colors = LocalGlassColors.current
    val borderBrush = if (isActive) Brush.linearGradient(listOf(colors.accent, colors.accent.copy(alpha = 0.5f)))
                      else Brush.linearGradient(listOf(colors.borderGlass, colors.borderGlass.copy(alpha = 0.5f)))

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable { onClick() }
            .border(if (isActive) 2.dp else 0.dp, borderBrush, RoundedCornerShape(24.dp)),
        cornerRadius = 24.dp,
        backgroundColor = if (isResult) colors.accentContainer else colors.surfaceGlass
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(label, fontFamily = SpaceGroteskFontFamily, fontSize = 12.sp, color = if (isResult) colors.accent else colors.textSecondary)
                Text(
                    text = value,
                    fontFamily = JetBrainsMonoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = if (value.length > 10) 24.sp else 30.sp,
                    color = colors.textPrimary,
                    maxLines = 1
                )
            }
            
            GlassCard(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onUnitClick() },
                cornerRadius = 16.dp,
                backgroundColor = colors.surfaceGlassSecondary
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(unit.symbol, fontFamily = JetBrainsMonoFontFamily, fontWeight = FontWeight.Bold, color = colors.accent)
                    Icon(Icons.Default.KeyboardArrowDown, null, tint = colors.textTertiary, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

private fun abs(d: Double): Double = if (d < 0) -d else d
