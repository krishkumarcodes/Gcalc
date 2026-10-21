package com.example.screens.tools

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.components.GlassCard
import com.example.components.ToolHeader
import com.example.formulas.ChemicalElement
import com.example.formulas.FormulaRepository
import com.example.ui.theme.LocalGlassColors
import com.example.ui.theme.SpaceGroteskFontFamily
import com.example.ui.theme.JetBrainsMonoFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeriodicTableTool(onBack: () -> Unit) {
    BackHandler { onBack() }
    val colors = LocalGlassColors.current
    var selectedElement by remember { mutableStateOf<ChemicalElement?>(null) }
    var formulaInput by remember { mutableStateOf("") }
    
    val (molarMass, elementCounts) = remember(formulaInput) {
        FormulaRepository.calculateMolarMass(formulaInput)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ToolHeader(title = "Periodic Table", icon = "⚛", onBack = onBack)

        // Molar Mass Calculator Card
        GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 24.dp) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Molar Mass Calculator", fontSize = 12.sp, color = colors.accent, fontWeight = FontWeight.Bold)
                
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(colors.surfaceGlassSecondary)
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (formulaInput.isEmpty()) Text("Enter formula (e.g. H2SO4)", fontSize = 14.sp, color = colors.textTertiary)
                        androidx.compose.foundation.text.BasicTextField(
                            value = formulaInput,
                            onValueChange = { formulaInput = it },
                            textStyle = androidx.compose.ui.text.TextStyle(fontFamily = JetBrainsMonoFontFamily, color = colors.textPrimary, fontSize = 16.sp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                if (molarMass > 0) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Molar Mass:", fontSize = 13.sp, color = colors.textSecondary)
                        Text("%.4f g/mol".format(molarMass), fontFamily = SpaceGroteskFontFamily, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = colors.accent)
                    }
                    
                    Text(
                        text = elementCounts.entries.joinToString(" + ") { "${it.value}${it.key}" },
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 11.sp,
                        color = colors.textTertiary
                    )
                }
            }
        }

        // Periodic Table Grid
        Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(20.dp)).background(colors.surfaceGlass)) {
             val vertScrollState = rememberScrollState()
             val horizScrollState = rememberScrollState()
             Column(
                 modifier = Modifier
                     .fillMaxSize()
                     .verticalScroll(vertScrollState)
                     .horizontalScroll(horizScrollState)
                     .padding(12.dp),
                 verticalArrangement = Arrangement.spacedBy(4.dp)
             ) {
                 // Map elements to group/period
                 for (p in 1..10) { // 1-7 periods + lanthanides/actinides
                     Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                         for (g in 1..18) {
                             val element = FormulaRepository.periodicTable.find { it.period == p && it.group == g }
                             if (element != null) {
                                 ElementCell(element = element, isSelected = selectedElement == element) {
                                     selectedElement = element
                                 }
                             } else {
                                 Spacer(modifier = Modifier.size(36.dp))
                             }
                         }
                     }
                 }
             }
        }

        // Selected Element Detail Card
        AnimatedVisibility(
            visible = selectedElement != null,
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut()
        ) {
            selectedElement?.let { el ->
                GlassCard(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    cornerRadius = 24.dp,
                    backgroundColor = colors.accentContainer
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(colors.accent),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${el.atomicNumber}", fontSize = 10.sp, color = Color.White.copy(alpha = 0.8f))
                                Text(el.symbol, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(el.name, fontFamily = SpaceGroteskFontFamily, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = colors.textPrimary)
                            Text(el.category, fontSize = 12.sp, color = colors.accent)
                            Text("Atomic Mass: ${el.atomicMass} u", fontSize = 13.sp, color = colors.textSecondary)
                        }
                        IconButton(onClick = { selectedElement = null }) {
                            Icon(Icons.Default.Close, null, tint = colors.textTertiary)
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
fun ElementCell(element: ChemicalElement, isSelected: Boolean, onClick: () -> Unit) {
    val colors = LocalGlassColors.current
    val categoryColor = remember(element.category) {
        when (element.category) {
            "Alkali Metal" -> Color(0xFFEF4444)
            "Alkaline Earth" -> Color(0xFFF59E0B)
            "Transition Metal" -> Color(0xFF3B82F6)
            "Post-transition" -> Color(0xFF10B981)
            "Metalloid" -> Color(0xFF8B5CF6)
            "Nonmetal" -> Color(0xFF06B6D4)
            "Halogen" -> Color(0xFF6366F1)
            "Noble Gas" -> Color(0xFFEC4899)
            "Lanthanide" -> Color(0xFFA855F7)
            "Actinide" -> Color(0xFF7C3AED)
            else -> colors.textTertiary
        }
    }

    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) colors.accent else categoryColor.copy(alpha = 0.2f))
            .border(1.dp, if (isSelected) Color.White else categoryColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("${element.atomicNumber}", fontSize = 7.sp, color = colors.textSecondary)
            Text(element.symbol, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (isSelected) Color.White else colors.textPrimary)
        }
    }
}
