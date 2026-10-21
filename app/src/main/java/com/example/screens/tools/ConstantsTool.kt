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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.components.GlassCard
import com.example.formulas.FormulaRepository
import com.example.formulas.ScientificConstant
import com.example.ui.theme.LocalGlassColors
import com.example.ui.theme.SpaceGroteskFontFamily
import com.example.ui.theme.JetBrainsMonoFontFamily

@Composable
fun ConstantsTool(onBack: () -> Unit, onUseInCalculator: (Double) -> Unit = {}) {
    BackHandler { onBack() }
    val colors = LocalGlassColors.current
    val context = LocalContext.current

    var searchQuery by remember { mutableStateOf("") }
    var expandedSymbol by remember { mutableStateOf<String?>(null) }
    val starredSymbols = remember { mutableStateListOf<String>() }

    val filteredConstants = remember(searchQuery, starredSymbols.toList()) {
        val list = FormulaRepository.constants.filter {
            it.name.contains(searchQuery, true) ||
            it.symbol.contains(searchQuery, true) ||
            it.unit.contains(searchQuery, true) ||
            it.desc.contains(searchQuery, true)
        }
        // Pin starred at top
        list.sortedByDescending { starredSymbols.contains(it.symbol) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = colors.textPrimary)
            }
            Text("Physical Constants", fontFamily = SpaceGroteskFontFamily, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = colors.textPrimary)
        }

        // Search Box
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search constants or symbols...", color = colors.textTertiary) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            leadingIcon = { Icon(Icons.Default.Search, null, tint = colors.accent) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = colors.surfaceGlass,
                unfocusedContainerColor = colors.surfaceGlass,
                focusedBorderColor = colors.accent,
                unfocusedBorderColor = Color.Transparent
            )
        )

        // List
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
            items(filteredConstants, key = { it.symbol }) { constant ->
                val isExpanded = expandedSymbol == constant.symbol
                val isStarred = starredSymbols.contains(constant.symbol)

                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize()
                        .clickable {
                            expandedSymbol = if (isExpanded) null else constant.symbol
                        },
                    cornerRadius = 20.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(colors.accentContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(constant.symbol, fontFamily = JetBrainsMonoFontFamily, fontWeight = FontWeight.Bold, color = colors.accent, fontSize = 16.sp)
                            }

                            Spacer(Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(constant.name, fontFamily = SpaceGroteskFontFamily, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = colors.textPrimary)
                                Text(constant.displayValue, fontFamily = JetBrainsMonoFontFamily, fontSize = 12.sp, color = colors.accent)
                            }

                            IconButton(
                                onClick = {
                                    if (isStarred) starredSymbols.remove(constant.symbol) else starredSymbols.add(constant.symbol)
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = if (isStarred) Icons.Default.Star else Icons.Outlined.StarBorder,
                                    contentDescription = "Star",
                                    tint = if (isStarred) Color(0xFFF59E0B) else colors.textTertiary
                                )
                            }
                        }

                        if (isExpanded) {
                            HorizontalDivider(color = colors.borderGlass.copy(alpha = 0.3f), thickness = 1.dp)

                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Full Value:", fontSize = 11.sp, color = colors.textSecondary)
                                    Text("${constant.value} ${constant.unit}", fontFamily = JetBrainsMonoFontFamily, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = colors.textPrimary)
                                }

                                if (constant.desc.isNotEmpty()) {
                                    Text(constant.desc, fontSize = 12.sp, color = colors.textSecondary, fontFamily = SpaceGroteskFontFamily)
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Button(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            val clip = ClipData.newPlainText("constant", constant.value.toString())
                                            clipboard.setPrimaryClip(clip)
                                            Toast.makeText(context, "Copied ${constant.symbol} value", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = colors.accentContainer, contentColor = colors.accent),
                                        shape = RoundedCornerShape(12.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Icon(Icons.Default.ContentCopy, null, modifier = Modifier.size(14.dp))
                                        Spacer(Modifier.width(6.dp))
                                        Text("Copy Value", fontSize = 11.sp, fontFamily = SpaceGroteskFontFamily)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
