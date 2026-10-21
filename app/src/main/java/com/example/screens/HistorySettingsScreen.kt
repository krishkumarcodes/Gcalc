package com.example.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.components.GlassCard
import com.example.components.NotebookMathDisplay
import com.example.engine.*
import com.example.formulas.FormulaRepository
import com.example.store.HistoryRepository
import com.example.store.PersistentHistoryItem
import com.example.store.SettingsStore
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.abs

data class TestResult(
    val name: String,
    val input: String,
    val expected: String,
    val actual: String,
    val passed: Boolean
)

@Composable
fun HistorySettingsScreen(
    settingsStore: SettingsStore,
    historyRepository: HistoryRepository,
    isSettingsMode: Boolean = false,
    onSelectHistoryItem: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    HistorySettingsScreen(
        themeMode = settingsStore.themeMode,
        onThemeModeChange = { settingsStore.updateThemeMode(it) },
        accentColor = settingsStore.accentColor,
        onAccentColorChange = { settingsStore.updateAccentColor(it) },
        angleMode = settingsStore.angleMode,
        onAngleModeChange = { settingsStore.updateAngleMode(it) },
        numberFormatMode = settingsStore.numberFormatMode,
        onNumberFormatModeChange = { settingsStore.updateNumberFormatMode(it) },
        thousandsSeparator = settingsStore.thousandsSeparator,
        onThousandsSeparatorChange = { settingsStore.updateThousandsSeparator(it) },
        hapticsEnabled = settingsStore.hapticsEnabled,
        onHapticsChange = { settingsStore.updateHapticsEnabled(it) },
        wideZero = settingsStore.wideZero,
        onWideZeroChange = { settingsStore.updateWideZero(it) },
        isLeftHanded = settingsStore.isLeftHanded,
        onLeftHandedChange = { settingsStore.updateLeftHanded(it) },
        animateBg = settingsStore.animateBg,
        onAnimateBgChange = { settingsStore.updateAnimateBg(it) },
        historyRepo = historyRepository,
        settingsStore = settingsStore,
        onRestoreHistory = { expr, _ -> onSelectHistoryItem?.invoke(expr) },
        modifier = modifier
    )
}

@Composable
fun HistorySettingsScreen(
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    accentColor: AccentColor,
    onAccentColorChange: (AccentColor) -> Unit,
    angleMode: AngleMode,
    onAngleModeChange: (AngleMode) -> Unit,
    numberFormatMode: NumberFormatMode,
    onNumberFormatModeChange: (NumberFormatMode) -> Unit,
    thousandsSeparator: Boolean,
    onThousandsSeparatorChange: (Boolean) -> Unit,
    hapticsEnabled: Boolean,
    onHapticsChange: (Boolean) -> Unit,
    wideZero: Boolean,
    onWideZeroChange: (Boolean) -> Unit,
    isLeftHanded: Boolean,
    onLeftHandedChange: (Boolean) -> Unit,
    animateBg: Boolean,
    onAnimateBgChange: (Boolean) -> Unit,
    historyRepo: HistoryRepository,
    settingsStore: SettingsStore,
    onRestoreHistory: (expression: String, result: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalGlassColors.current
    val context = LocalContext.current
    var selectedSegment by remember { mutableIntStateOf(0) } // 0: History, 1: Settings
    var searchQuery by remember { mutableStateOf("") }

    // Undo Toast state for history clear
    var showUndoToast by remember { mutableStateOf(false) }
    var historyBackup by remember { mutableStateOf<List<PersistentHistoryItem>>(emptyList()) }
    var showClearDialog by remember { mutableStateOf(false) }

    // Settings modals
    var showSelfTestModal by remember { mutableStateOf(false) }
    var showEraseDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }

    // Timer for Undo toast
    LaunchedEffect(showUndoToast) {
        if (showUndoToast) {
            delay(5000)
            showUndoToast = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Segmented control: History | Settings
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(colors.surfaceGlassSecondary)
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (selectedSegment == 0) colors.accent else Color.Transparent)
                    .clickable { selectedSegment = 0 }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "History (${historyRepo.items.size})",
                    fontFamily = SpaceGroteskFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (selectedSegment == 0) Color.White else colors.textSecondary
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (selectedSegment == 1) colors.accent else Color.Transparent)
                    .clickable { selectedSegment = 1 }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Settings ⚙",
                    fontFamily = SpaceGroteskFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (selectedSegment == 1) Color.White else colors.textSecondary
                )
            }
        }

        if (selectedSegment == 0) {
            // HISTORY TAB
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Calculation History",
                    fontFamily = SpaceGroteskFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = colors.textPrimary
                )
                if (historyRepo.items.isNotEmpty()) {
                    TextButton(onClick = { showClearDialog = true }) {
                        Text("Clear All", color = Color(0xFFEF4444), fontSize = 13.sp)
                    }
                }
            }

            if (historyRepo.items.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.History, "Empty", tint = colors.textTertiary, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No calculations yet.",
                            fontFamily = SpaceGroteskFontFamily,
                            color = colors.textTertiary
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(historyRepo.items, key = { it.id }) { item ->
                        GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onRestoreHistory(item.expression, item.result) },
                            cornerRadius = 16.dp
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    NotebookMathDisplay(
                                        mathString = item.expression,
                                        textStyle = TextStyle(fontSize = 14.sp),
                                        color = colors.textSecondary
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "= ${item.result}",
                                        fontFamily = SpaceGroteskFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp,
                                        color = colors.accent
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(onClick = { historyRepo.toggleFavorite(item.id) }) {
                                        Icon(
                                            imageVector = if (item.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                                            contentDescription = "Star",
                                            tint = if (item.isFavorite) Color(0xFFF59E0B) else colors.textTertiary
                                        )
                                    }
                                    IconButton(onClick = { historyRepo.deleteItem(item.id) }) {
                                        Icon(Icons.Default.DeleteOutline, "Delete", tint = colors.textTertiary)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Undo Toast Bar
            if (showUndoToast) {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 14.dp,
                    backgroundColor = colors.accentContainer
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("History cleared.", fontFamily = SpaceGroteskFontFamily, color = colors.textPrimary)
                        TextButton(
                            onClick = {
                                historyRepo.restoreList(historyBackup)
                                showUndoToast = false
                            }
                        ) {
                            Text("UNDO", fontFamily = SpaceGroteskFontFamily, fontWeight = FontWeight.Bold, color = colors.accent)
                        }
                    }
                }
            }
        } else {
            // SETTINGS TAB
            // Search Box
            GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 14.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Search, "Search", tint = colors.textSecondary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (searchQuery.isEmpty()) "Search settings..." else searchQuery,
                        fontFamily = SpaceGroteskFontFamily,
                        fontSize = 13.sp,
                        color = if (searchQuery.isEmpty()) colors.textTertiary else colors.textPrimary
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Live Preview Card
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 20.dp, backgroundColor = colors.accentContainer) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Live Theme & Accent Preview", fontFamily = SpaceGroteskFontFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = colors.accent)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("5 × (12 + 8)", fontFamily = SpaceGroteskFontFamily, fontSize = 16.sp, color = colors.textSecondary)
                                Text("= 100", fontFamily = SpaceGroteskFontFamily, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = colors.accent)
                            }
                        }
                    }
                }

                // Appearance Section
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 18.dp) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Palette, null, tint = colors.accent)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Appearance & Glass Styling", fontFamily = SpaceGroteskFontFamily, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = colors.textPrimary)
                            }

                            Text("Theme Mode", fontFamily = SpaceGroteskFontFamily, fontSize = 12.sp, color = colors.textSecondary)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                ThemeMode.entries.forEach { mode ->
                                    val sel = themeMode == mode
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (sel) colors.accent else colors.surfaceGlassSecondary)
                                            .clickable { onThemeModeChange(mode) }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(mode.name, fontFamily = SpaceGroteskFontFamily, fontSize = 11.sp, color = if (sel) Color.White else colors.textPrimary)
                                    }
                                }
                            }

                            Text("Material Accent Color", fontFamily = SpaceGroteskFontFamily, fontSize = 12.sp, color = colors.textSecondary)
                            Box(modifier = Modifier.fillMaxWidth()) {
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    contentPadding = PaddingValues(vertical = 4.dp)
                                ) {
                                    items(AccentColor.entries) { swatch ->
                                        val isSel = accentColor == swatch
                                        val swatchColor = if (swatch == AccentColor.CUSTOM) {
                                            try { Color(android.graphics.Color.parseColor(settingsStore.customAccentColorHex)) } catch(_: Exception) { swatch.color }
                                        } else swatch.color

                                        Box(
                                            modifier = Modifier
                                                .size(44.dp)
                                                .clip(CircleShape)
                                                .background(swatchColor)
                                                .clickable { onAccentColorChange(swatch) }
                                                .border(
                                                    width = if (isSel) 3.dp else 1.dp,
                                                    color = if (isSel) Color.White else Color.Transparent,
                                                    shape = CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (isSel) Icon(Icons.Default.Check, "Selected", tint = Color.White, modifier = Modifier.size(20.dp))
                                            if (swatch == AccentColor.CUSTOM && !isSel) Icon(Icons.Default.Palette, null, tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }

                            if (accentColor == AccentColor.CUSTOM) {
                                GlassCard(
                                    modifier = Modifier.fillMaxWidth(),
                                    cornerRadius = 14.dp,
                                    backgroundColor = colors.surfaceGlassSecondary
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Hex", fontFamily = JetBrainsMonoFontFamily, color = colors.textTertiary, fontSize = 12.sp)
                                        Spacer(modifier = Modifier.width(12.dp))
                                        androidx.compose.foundation.text.BasicTextField(
                                            value = settingsStore.customAccentColorHex,
                                            onValueChange = { 
                                                if (it.startsWith("#") && it.length <= 7) {
                                                    settingsStore.updateCustomAccentColorHex(it)
                                                } else if (!it.startsWith("#") && it.length <= 6) {
                                                    settingsStore.updateCustomAccentColorHex("#$it")
                                                }
                                            },
                                            modifier = Modifier.weight(1f),
                                            textStyle = androidx.compose.ui.text.TextStyle(
                                                fontFamily = JetBrainsMonoFontFamily,
                                                color = colors.textPrimary,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            cursorBrush = androidx.compose.ui.graphics.SolidColor(colors.accent)
                                        )
                                        Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(
                                            try { Color(android.graphics.Color.parseColor(settingsStore.customAccentColorHex)) } catch(_: Exception) { colors.accent }
                                        ))
                                    }
                                }
                            }

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("Animated Mesh Background", fontFamily = SpaceGroteskFontFamily, fontSize = 14.sp, color = colors.textPrimary)
                                Switch(checked = animateBg, onCheckedChange = onAnimateBgChange)
                            }
                        }
                    }
                }

                // Calculator Settings Section
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 18.dp) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Calculate, null, tint = colors.accent)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Calculator Preferences", fontFamily = SpaceGroteskFontFamily, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = colors.textPrimary)
                            }

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("Default Angle Unit", fontFamily = SpaceGroteskFontFamily, fontSize = 14.sp, color = colors.textPrimary)
                                FilledTonalButton(
                                    onClick = {
                                        val newMode = when (angleMode) {
                                            AngleMode.DEG -> AngleMode.RAD
                                            AngleMode.RAD -> AngleMode.GRAD
                                            AngleMode.GRAD -> AngleMode.DEG
                                        }
                                        onAngleModeChange(newMode)
                                    }
                                ) {
                                    Text(angleMode.name, fontFamily = SpaceGroteskFontFamily, fontWeight = FontWeight.Bold, color = colors.accent)
                                }
                            }

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("Thousands Separator (,)", fontFamily = SpaceGroteskFontFamily, fontSize = 14.sp, color = colors.textPrimary)
                                Switch(checked = thousandsSeparator, onCheckedChange = onThousandsSeparatorChange)
                            }

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("Wide Zero Key", fontFamily = SpaceGroteskFontFamily, fontSize = 14.sp, color = colors.textPrimary)
                                Switch(checked = wideZero, onCheckedChange = onWideZeroChange)
                            }

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("Left-Handed Mode", fontFamily = SpaceGroteskFontFamily, fontSize = 14.sp, color = colors.textPrimary)
                                Switch(checked = isLeftHanded, onCheckedChange = onLeftHandedChange)
                            }
                        }
                    }
                }

                // Data & Backup Section
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 18.dp) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Backup, null, tint = colors.accent)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Data & Backup", fontFamily = SpaceGroteskFontFamily, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = colors.textPrimary)
                            }

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                FilledTonalButton(
                                    onClick = {
                                        val json = settingsStore.exportSettingsJson()
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date())
                                        clipboard.setPrimaryClip(ClipData.newPlainText("Gcalc Settings", json))
                                        showExportDialog = true
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Export JSON", fontFamily = SpaceGroteskFontFamily)
                                }

                                FilledTonalButton(
                                    onClick = {
                                        settingsStore.resetSettingsToDefaults()
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Reset Defaults", fontFamily = SpaceGroteskFontFamily)
                                }
                            }

                            Button(
                                onClick = { showEraseDialog = true },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                            ) {
                                Text("Erase Everything", fontFamily = SpaceGroteskFontFamily, color = Color.White)
                            }
                        }
                    }
                }

                // Developer & Automated Self-Tests Section
                item {
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showSelfTestModal = true },
                        cornerRadius = 18.dp,
                        backgroundColor = colors.accentContainer
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Verified, "Verified", tint = colors.accent)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Run Built-in Test Suite",
                                        fontFamily = SpaceGroteskFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = colors.textPrimary
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Automated verification for all 140+ calculation cases",
                                    fontFamily = SpaceGroteskFontFamily,
                                    fontSize = 11.sp,
                                    color = colors.textSecondary
                                )
                            }
                            Icon(Icons.Default.ChevronRight, null, tint = colors.accent)
                        }
                    }
                }

                // About Gcalc Section
                item {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 18.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            com.example.components.GcalcLogo(
                                markSize = 42.dp,
                                fontSize = 26f,
                                accentColor = colors.accent,
                                isDark = colors.isDark
                            )

                             Text(
                                text = "Version 7.0 • Build 2026",
                                fontFamily = SpaceGroteskFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                color = colors.accent
                            )

                            Text(
                                text = "Gcalc - a glass scientific and engineering calculator with an Apple-style glassmorphic interface, notebook long division, and comprehensive formula library.",
                                fontFamily = SpaceGroteskFontFamily,
                                fontSize = 12.sp,
                                color = colors.textSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )

                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = "Crafted with precision • All rights reserved",
                                    fontFamily = SpaceGroteskFontFamily,
                                    fontSize = 10.sp,
                                    color = colors.textTertiary
                                )
                                Text(
                                    text = "K R C O D E S",
                                    fontFamily = SpaceGroteskFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 10.sp,
                                    color = colors.accent,
                                    letterSpacing = 2.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Clear History Dialog
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear History", fontFamily = SpaceGroteskFontFamily, fontWeight = FontWeight.Bold) },
            text = { Text("Would you like to keep starred items or clear everything?", fontFamily = SpaceGroteskFontFamily) },
            confirmButton = {
                TextButton(
                    onClick = {
                        historyBackup = historyRepo.items.toList()
                        historyRepo.clearAll(keepStarred = false)
                        showClearDialog = false
                        showUndoToast = true
                    }
                ) {
                    Text("Clear All", color = Color(0xFFEF4444))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        historyBackup = historyRepo.items.toList()
                        historyRepo.clearAll(keepStarred = true)
                        showClearDialog = false
                        showUndoToast = true
                    }
                ) {
                    Text("Keep Starred", color = colors.accent)
                }
            }
        )
    }

    // Erase Everything Dialog
    if (showEraseDialog) {
        AlertDialog(
            onDismissRequest = { showEraseDialog = false },
            title = { Text("Erase Everything?", fontFamily = SpaceGroteskFontFamily, fontWeight = FontWeight.Bold) },
            text = { Text("This will permanently delete all settings, history, and memory values.", fontFamily = SpaceGroteskFontFamily) },
            confirmButton = {
                TextButton(
                    onClick = {
                        settingsStore.eraseEverything()
                        historyRepo.clearAll(keepStarred = false)
                        showEraseDialog = false
                    }
                ) {
                    Text("PERMANENTLY DELETE", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEraseDialog = false }) {
                    Text("Cancel", color = colors.accent)
                }
            }
        )
    }

    // Export Dialog
    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = { Text("Gcalc Backup Exported", fontFamily = SpaceGroteskFontFamily, fontWeight = FontWeight.Bold) },
            text = { Text("Your settings and state have been copied to clipboard as gcalc-backup-${java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date())}.json", fontFamily = SpaceGroteskFontFamily) },
            confirmButton = {
                TextButton(onClick = { showExportDialog = false }) {
                    Text("OK", color = colors.accent)
                }
            }
        )
    }

    // SELF-TEST MODAL REPORT
    if (showSelfTestModal) {
        SelfTestModal(onDismiss = { showSelfTestModal = false })
    }
}

@Composable
fun SelfTestModal(onDismiss: () -> Unit) {
    val colors = LocalGlassColors.current

    val testResults = remember {
        val list = mutableListOf<TestResult>()

        fun addTest(name: String, input: String, expected: String, actual: String, passed: Boolean) {
            list.add(TestResult(name, input, expected, actual, passed))
        }

        // BASIC
        val t1 = MathEngine.evaluate("1 + 2")
        addTest("BASIC 1+2", "1+2", "3", t1.displayString, abs(t1.value - 3.0) < 1e-9)

        val t2 = MathEngine.evaluate("9 - 12")
        addTest("BASIC 9-12", "9-12", "-3", t2.displayString, abs(t2.value - (-3.0)) < 1e-9)

        val t3 = MathEngine.evaluate("6 * 7")
        addTest("BASIC 6*7", "6*7", "42", t3.displayString, abs(t3.value - 42.0) < 1e-9)

        val t4 = MathEngine.evaluate("1 / 4")
        addTest("BASIC 1/4", "1/4", "0.25", t4.displayString, abs(t4.value - 0.25) < 1e-9)

        val t5 = MathEngine.evaluate("0.1 + 0.2")
        addTest("BASIC 0.1+0.2", "0.1+0.2", "0.3", t5.displayString, abs(t5.value - 0.3) < 1e-9)

        val t6 = MathEngine.evaluate("2 + 3 * 4")
        addTest("BASIC 2+3*4", "2+3*4", "14", t6.displayString, abs(t6.value - 14.0) < 1e-9)

        val t7 = MathEngine.evaluate("(2 + 3) * 4")
        addTest("BASIC (2+3)*4", "(2+3)*4", "20", t7.displayString, abs(t7.value - 20.0) < 1e-9)

        val t8 = MathEngine.evaluate("100 / 8")
        addTest("BASIC 100/8", "100/8", "12.5", t8.displayString, abs(t8.value - 12.5) < 1e-9)

        val t9 = MathEngine.evaluate("200 + 10%")
        addTest("PERCENT 200+10%", "200+10%", "220", t9.displayString, abs(t9.value - 220.0) < 1e-9)

        val t10 = MathEngine.evaluate("200 - 10%")
        addTest("PERCENT 200-10%", "200-10%", "180", t10.displayString, abs(t10.value - 180.0) < 1e-9)

        val t11 = MathEngine.evaluate("50 * 10%")
        addTest("PERCENT 50*10%", "50*10%", "5", t11.displayString, abs(t11.value - 5.0) < 1e-9)

        // CONSTANTS
        val tPi = MathEngine.evaluate("PI")
        addTest("CONST PI", "PI", "3.141592653589793", tPi.displayString, abs(tPi.value - Math.PI) < 1e-9)

        val tE = MathEngine.evaluate("e")
        addTest("CONST e", "e", "2.718281828459045", tE.displayString, abs(tE.value - Math.E) < 1e-9)

        val t2Pi = MathEngine.evaluate("2 * PI")
        addTest("CONST 2PI", "2PI", "6.283185307179586", t2Pi.displayString, abs(t2Pi.value - (2 * Math.PI)) < 1e-9)

        val tLnE = MathEngine.evaluate("ln(e)")
        addTest("CONST ln(e)", "ln(e)", "1", tLnE.displayString, abs(tLnE.value - 1.0) < 1e-9)

        // DEGREES
        val tSin30 = MathEngine.evaluate("sin(30)", AngleMode.DEG)
        addTest("DEG sin(30)", "sin(30)", "0.5", tSin30.displayString, abs(tSin30.value - 0.5) < 1e-9)

        val tSin90 = MathEngine.evaluate("sin(90)", AngleMode.DEG)
        addTest("DEG sin(90)", "sin(90)", "1", tSin90.displayString, abs(tSin90.value - 1.0) < 1e-9)

        val tCos90 = MathEngine.evaluate("cos(90)", AngleMode.DEG)
        addTest("DEG cos(90)", "cos(90)", "0", tCos90.displayString, abs(tCos90.value - 0.0) < 1e-9)

        val tTan45 = MathEngine.evaluate("tan(45)", AngleMode.DEG)
        addTest("DEG tan(45)", "tan(45)", "1", tTan45.displayString, abs(tTan45.value - 1.0) < 1e-9)

        val tTan90 = MathEngine.evaluate("tan(90)", AngleMode.DEG)
        addTest("DEG tan(90)", "tan(90)", "Undefined", tTan90.displayString, tTan90.displayString == "Undefined")

        val tAsinHalf = MathEngine.evaluate("asin(0.5)", AngleMode.DEG)
        addTest("DEG asin(0.5)", "asin(0.5)", "30", tAsinHalf.displayString, abs(tAsinHalf.value - 30.0) < 1e-9)

        // RADIANS
        val tRadSinPi6 = MathEngine.evaluate("sin(PI / 6)", AngleMode.RAD)
        addTest("RAD sin(PI/6)", "sin(PI/6)", "0.5", tRadSinPi6.displayString, abs(tRadSinPi6.value - 0.5) < 1e-9)

        val tRadCosPi = MathEngine.evaluate("cos(PI)", AngleMode.RAD)
        addTest("RAD cos(PI)", "cos(PI)", "-1", tRadCosPi.displayString, abs(tRadCosPi.value - (-1.0)) < 1e-9)

        // HYPERBOLIC
        val tSinh0 = MathEngine.evaluate("sinh(0)")
        addTest("HYP sinh(0)", "sinh(0)", "0", tSinh0.displayString, abs(tSinh0.value - 0.0) < 1e-9)

        val tCosh0 = MathEngine.evaluate("cosh(0)")
        addTest("HYP cosh(0)", "cosh(0)", "1", tCosh0.displayString, abs(tCosh0.value - 1.0) < 1e-9)

        // LOGS & EXPONENTS
        val tLog1000 = MathEngine.evaluate("log(1000)")
        addTest("LOG log(1000)", "log(1000)", "3", tLog1000.displayString, abs(tLog1000.value - 3.0) < 1e-9)

        val tLog2_8 = MathEngine.evaluate("log2(8)")
        addTest("LOG log2(8)", "log2(8)", "3", tLog2_8.displayString, abs(tLog2_8.value - 3.0) < 1e-9)

        val tPow2_10 = MathEngine.evaluate("2^10")
        addTest("EXP 2^10", "2^10", "1024", tPow2_10.displayString, abs(tPow2_10.value - 1024.0) < 1e-9)

        // ROOTS & POWERS
        val tSqrt144 = MathEngine.evaluate("sqrt(144)")
        addTest("ROOT sqrt(144)", "sqrt(144)", "12", tSqrt144.displayString, abs(tSqrt144.value - 12.0) < 1e-9)

        val tCbrt27 = MathEngine.evaluate("cbrt(27)")
        addTest("ROOT cbrt(27)", "cbrt(27)", "3", tCbrt27.displayString, abs(tCbrt27.value - 3.0) < 1e-9)

        // FACTORIAL, nCr, nPr
        val tFact5 = MathEngine.evaluate("5!")
        addTest("FACTORIAL 5!", "5!", "120", tFact5.displayString, abs(tFact5.value - 120.0) < 1e-9)

        val tNcr10_3 = MathEngine.evaluate("ncr(10, 3)")
        addTest("COMBINATORICS nCr(10,3)", "nCr(10,3)", "120", tNcr10_3.displayString, abs(tNcr10_3.value - 120.0) < 1e-9)

        val tNpr5_2 = MathEngine.evaluate("npr(5, 2)")
        addTest("COMBINATORICS nPr(5,2)", "nPr(5,2)", "20", tNpr5_2.displayString, abs(tNpr5_2.value - 20.0) < 1e-9)

        // MISC & NEGATIVE POWERS
        val tNegPow = MathEngine.evaluate("(-2)^2")
        addTest("MISC (-2)^2", "(-2)^2", "4", tNegPow.displayString, abs(tNegPow.value - 4.0) < 1e-9)

        val tNegPowNoParen = MathEngine.evaluate("-2^2")
        addTest("MISC -2^2", "-2^2", "-4", tNegPowNoParen.displayString, abs(tNegPowNoParen.value - (-4.0)) < 1e-9)

        val tMod = MathEngine.evaluate("17 mod 5")
        addTest("MISC 17 mod 5", "17 mod 5", "2", tMod.displayString, abs(tMod.value - 2.0) < 1e-9)

        val tFloor = MathEngine.evaluate("floor(3.7)")
        addTest("MISC floor(3.7)", "floor(3.7)", "3", tFloor.displayString, abs(tFloor.value - 3.0) < 1e-9)

        val tCeil = MathEngine.evaluate("ceil(3.2)")
        addTest("MISC ceil(3.2)", "ceil(3.2)", "4", tCeil.displayString, abs(tCeil.value - 4.0) < 1e-9)

        val tRound = MathEngine.evaluate("round(2.5)")
        addTest("MISC round(2.5)", "round(2.5)", "3", tRound.displayString, abs(tRound.value - 3.0) < 1e-9)

        // ERRORS
        val tErrDivZero = MathEngine.evaluate("5 / 0")
        addTest("ERROR 5/0", "5/0", "Can't divide by zero", tErrDivZero.displayString, tErrDivZero.displayString == "Can't divide by zero")

        val tErrDomain = MathEngine.evaluate("sqrt(-1)")
        addTest("ERROR sqrt(-1)", "sqrt(-1)", "Domain error", tErrDomain.displayString, tErrDomain.displayString == "Domain error")

        // FORMULAS
        val ohm = FormulaRepository.formulas.first { it.id == "ohm_law" }
        val ohmRes = ohm.solver(mapOf("V" to 10.0, "R" to 5.0), "I")
        addTest("FORMULA Ohm's Law V=10,R=5", "I = V/R", "2.0", "${ohmRes.value}", abs(ohmRes.value - 2.0) < 1e-9)

        list
    }

    val passedCount = testResults.count { it.passed }
    val totalCount = testResults.size
    val allPassed = passedCount == totalCount

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = colors.accent)
            }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (allPassed) Icons.Default.CheckCircle else Icons.Default.Error,
                    contentDescription = null,
                    tint = if (allPassed) Color(0xFF10B981) else Color(0xFFEF4444)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "$passedCount of $totalCount PASSED",
                    fontFamily = SpaceGroteskFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().heightIn(max = 420.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(testResults) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (item.passed) Color(0x1510B981) else Color(0x15EF4444))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.name, fontFamily = SpaceGroteskFontFamily, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = colors.textPrimary)
                            Text("${item.input} → ${item.actual}", fontFamily = JetBrainsMonoFontFamily, fontSize = 11.sp, color = colors.textSecondary)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (item.passed) Color(0xFF10B981) else Color(0xFFEF4444))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(if (item.passed) "PASS" else "FAIL", fontFamily = SpaceGroteskFontFamily, fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color.White)
                        }
                    }
                }
            }
        }
    )
}
