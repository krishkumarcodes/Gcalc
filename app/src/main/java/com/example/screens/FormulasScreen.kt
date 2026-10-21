package com.example.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.components.NotebookMathDisplay
import com.example.formulas.*
import com.example.ui.theme.LocalGlassColors
import com.example.ui.theme.SpaceGroteskFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormulasScreen(
    onUseInCalculator: (String) -> Unit = {}
) {
    val colors = LocalGlassColors.current

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<FormulaCategory?>(null) }
    var activeFormula by remember { mutableStateOf<Formula?>(null) }

    val filteredFormulas = remember(searchQuery, selectedCategory) {
        FormulaRepository.formulas.filter { f ->
            val matchesCategory = selectedCategory == null || f.category == selectedCategory
            val matchesSearch = searchQuery.isBlank() ||
                    f.name.contains(searchQuery, ignoreCase = true) ||
                    f.mathExpression.contains(searchQuery, ignoreCase = true) ||
                    f.explanation.contains(searchQuery, ignoreCase = true) ||
                    f.tags.any { it.contains(searchQuery, ignoreCase = true) }
            matchesCategory && matchesSearch
        }
    }

    if (activeFormula != null) {
        FormulaDetailScreen(
            formula = activeFormula!!,
            onBack = { activeFormula = null },
            onUseInCalculator = onUseInCalculator
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background)
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.accent.copy(alpha = 0.2f))
                        .border(1.dp, colors.accent.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Functions,
                        contentDescription = null,
                        tint = colors.accent,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Formula Library",
                    fontFamily = SpaceGroteskFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = colors.textPrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search 50+ formulas...", color = colors.textTertiary, fontFamily = SpaceGroteskFontFamily) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = colors.accent) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = colors.textSecondary)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.accent,
                    unfocusedBorderColor = colors.borderGlass,
                    focusedContainerColor = colors.surfaceGlass,
                    unfocusedContainerColor = colors.surfaceGlass
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Category Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { selectedCategory = null },
                        label = { Text("All", fontFamily = SpaceGroteskFontFamily) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = colors.accent,
                            selectedLabelColor = Color.White,
                            containerColor = colors.surfaceGlass,
                            labelColor = colors.textPrimary
                        )
                    )
                }
                items(FormulaCategory.entries.toTypedArray()) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = if (selectedCategory == cat) null else cat },
                        label = { Text(cat.displayName, fontFamily = SpaceGroteskFontFamily) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = colors.accent,
                            selectedLabelColor = Color.White,
                            containerColor = colors.surfaceGlass,
                            labelColor = colors.textPrimary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Formula Cards List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredFormulas) { formula ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(colors.surfaceGlass)
                            .border(1.dp, colors.borderGlass, RoundedCornerShape(18.dp))
                            .clickable { activeFormula = formula }
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = formula.name,
                                    fontFamily = SpaceGroteskFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = colors.textPrimary
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(colors.accentContainer.copy(alpha = 0.4f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = formula.category.displayName,
                                        fontFamily = SpaceGroteskFontFamily,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.accent
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            NotebookMathDisplay(
                                mathString = formula.mathExpression,
                                textStyle = MaterialTheme.typography.titleMedium
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = formula.explanation,
                                fontFamily = SpaceGroteskFontFamily,
                                fontSize = 12.sp,
                                color = colors.textSecondary,
                                maxLines = 2
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FormulaDetailScreen(
    formula: Formula,
    onBack: () -> Unit,
    onUseInCalculator: (String) -> Unit
) {
    val colors = LocalGlassColors.current

    val inputValues = remember {
        mutableStateMapOf<String, String>().apply {
            formula.variables.forEach { v ->
                this[v.symbol] = v.defaultValue.toString()
            }
        }
    }

    var targetVariable by remember {
        mutableStateOf(formula.variables.firstOrNull()?.symbol ?: "")
    }

    val calculatedResult = remember(targetVariable, inputValues.toMap()) {
        val parsedInputs = inputValues.mapValues { it.value.toDoubleOrNull() ?: 0.0 }
        try {
            formula.solver(parsedInputs, targetVariable)
        } catch (_: Exception) {
            null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = colors.textPrimary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = formula.name,
                fontFamily = SpaceGroteskFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = colors.textPrimary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Math Formula Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(colors.surfaceGlass)
                .border(1.5.dp, colors.borderGlassHighlight, RoundedCornerShape(20.dp))
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                NotebookMathDisplay(
                    mathString = formula.mathExpression,
                    textStyle = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = formula.explanation,
                    fontFamily = SpaceGroteskFontFamily,
                    fontSize = 12.sp,
                    color = colors.textSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Target Variable Selector
        Text(
            text = "Solve For Variable",
            fontFamily = SpaceGroteskFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = colors.textSecondary
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(formula.variables) { v ->
                FilterChip(
                    selected = targetVariable == v.symbol,
                    onClick = { targetVariable = v.symbol },
                    label = { Text("${v.symbol} (${v.name})", fontFamily = SpaceGroteskFontFamily) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = colors.accent,
                        selectedLabelColor = Color.White,
                        containerColor = colors.surfaceGlass,
                        labelColor = colors.textPrimary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Input Fields for other variables
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(formula.variables.filter { it.symbol != targetVariable }) { v ->
                OutlinedTextField(
                    value = inputValues[v.symbol] ?: "",
                    onValueChange = { inputValues[v.symbol] = it },
                    label = { Text("${v.name} (${v.symbol}) in ${v.unit}", fontFamily = SpaceGroteskFontFamily) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.accent,
                        unfocusedBorderColor = colors.borderGlass,
                        focusedContainerColor = colors.surfaceGlass,
                        unfocusedContainerColor = colors.surfaceGlass
                    )
                )
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
                if (calculatedResult != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(colors.accentContainer.copy(alpha = 0.3f))
                            .border(1.5.dp, colors.accent, RoundedCornerShape(18.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Text(
                                text = "Result (${calculatedResult.targetName})",
                                fontFamily = SpaceGroteskFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = colors.accent
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${calculatedResult.targetSymbol} = ${calculatedResult.value} ${calculatedResult.unit}",
                                fontFamily = SpaceGroteskFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,
                                color = colors.textPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}
