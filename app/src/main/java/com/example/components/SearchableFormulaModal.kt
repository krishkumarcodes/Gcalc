package com.example.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
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
import com.example.formulas.FormulaCategory
import com.example.formulas.FormulaRepository
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalGlassColors
import com.example.ui.theme.SpaceGroteskFontFamily

@Composable
fun SearchableFormulaModal(
    onInsertFormula: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = LocalGlassColors.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<FormulaCategory?>(null) }

    val filtered = remember(searchQuery, selectedCategory) {
        FormulaRepository.formulas.filter { f ->
            val matchesCategory = selectedCategory == null || f.category == selectedCategory
            val matchesSearch = searchQuery.isEmpty() ||
                    f.name.contains(searchQuery, ignoreCase = true) ||
                    f.mathExpression.contains(searchQuery, ignoreCase = true) ||
                    f.explanation.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = colors.accent)
            }
        },
        title = {
            Text(
                text = "Formula Library",
                fontFamily = SpaceGroteskFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = colors.textPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 480.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Search Field
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 14.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Search, "Search", tint = colors.textSecondary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (searchQuery.isEmpty()) "Search formulas..." else searchQuery,
                            fontFamily = SpaceGroteskFontFamily,
                            fontSize = 13.sp,
                            color = if (searchQuery.isEmpty()) colors.textTertiary else colors.textPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }, modifier = Modifier.size(20.dp)) {
                                Icon(Icons.Default.Clear, "Clear", tint = colors.textSecondary)
                            }
                        }
                    }
                }

                // Category Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val categories = listOf(null) + com.example.formulas.FormulaCategory.entries
                    categories.forEach { cat ->
                        val isSel = selectedCategory == cat
                        val title = cat?.displayName?.take(4) ?: "All"
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) colors.accent else colors.surfaceGlassSecondary)
                                .clickable { selectedCategory = cat }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(title, fontFamily = SpaceGroteskFontFamily, fontSize = 10.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal, color = if (isSel) Color.White else colors.textPrimary)
                        }
                    }
                }

                // Formulas List
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filtered) { formula ->
                        GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onInsertFormula(formula.mathExpression)
                                },
                            cornerRadius = 14.dp
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(formula.name, fontFamily = SpaceGroteskFontFamily, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = colors.textPrimary)
                                    Text(formula.mathExpression, fontFamily = JetBrainsMonoFontFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = colors.accent)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(colors.accentContainer)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("Insert ↵", fontFamily = SpaceGroteskFontFamily, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = colors.accent)
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}
