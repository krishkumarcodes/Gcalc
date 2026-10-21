package com.example.screens.tools

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.components.GlassKey
import com.example.components.KeyType
import com.example.components.ToolHeader
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalGlassColors
import com.example.ui.theme.SpaceGroteskFontFamily

enum class NumberBase(val displayName: String, val prefix: String) {
    HEX("Hexadecimal", "0x"),
    DEC("Decimal", ""),
    OCT("Octal", "0o"),
    BIN("Binary", "0b")
}

@Composable
fun ProgrammerModeTool(onBack: () -> Unit) {
    BackHandler { onBack() }
    val colors = LocalGlassColors.current
    val clipboardManager = LocalClipboardManager.current

    var selectedBase by remember { mutableStateOf(NumberBase.DEC) }
    var inputString by remember { mutableStateOf("255") }

    val decimalValue = remember(inputString, selectedBase) {
        val clean = inputString.trim()
        if (clean.isBlank()) 0L
        else try {
            when (selectedBase) {
                NumberBase.HEX -> clean.toLong(16)
                NumberBase.DEC -> clean.toLong(10)
                NumberBase.OCT -> clean.toLong(8)
                NumberBase.BIN -> clean.toLong(2)
            }
        } catch (_: Exception) {
            0L
        }
    }

    val hexStr = remember(decimalValue) { decimalValue.toString(16).uppercase() }
    val decStr = remember(decimalValue) { decimalValue.toString(10) }
    val octStr = remember(decimalValue) { decimalValue.toString(8) }
    val binStr = remember(decimalValue) {
        val rawBin = decimalValue.toString(2)
        rawBin.padStart((((rawBin.length - 1) / 4) + 1) * 4, '0')
            .chunked(4)
            .joinToString(" ")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        ToolHeader(title = "Programmer Mode", icon = Icons.Default.Code, onBack = onBack)

        Spacer(modifier = Modifier.height(12.dp))

        // Base Selector Chips
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(NumberBase.entries.toTypedArray()) { base ->
                FilterChip(
                    selected = selectedBase == base,
                    onClick = {
                        inputString = when (base) {
                            NumberBase.HEX -> hexStr
                            NumberBase.DEC -> decStr
                            NumberBase.OCT -> octStr
                            NumberBase.BIN -> binStr.replace(" ", "")
                        }
                        selectedBase = base
                    },
                    label = { Text("${base.name} (${base.displayName})", fontFamily = SpaceGroteskFontFamily) },
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

        // Big Active Input Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(colors.surfaceGlass)
                .border(1.5.dp, colors.borderGlassHighlight, RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "Input (${selectedBase.displayName})",
                    fontFamily = SpaceGroteskFontFamily,
                    fontSize = 12.sp,
                    color = colors.accent
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${selectedBase.prefix}${if (inputString.isBlank()) "0" else inputString}",
                    fontFamily = JetBrainsMonoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    color = colors.textPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 4 Base Synchronized Cards
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            BaseResultCard("HEX", hexStr, colors.accent) { clipboardManager.setText(AnnotatedString(hexStr)) }
            BaseResultCard("DEC", decStr, colors.textPrimary) { clipboardManager.setText(AnnotatedString(decStr)) }
            BaseResultCard("OCT", octStr, colors.textSecondary) { clipboardManager.setText(AnnotatedString(octStr)) }
            BaseResultCard("BIN", binStr, colors.accent) { clipboardManager.setText(AnnotatedString(binStr)) }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Keypad for Programmer Mode
        ProgrammerKeypad(
            selectedBase = selectedBase,
            onInput = { digit -> inputString = if (inputString == "0") digit else inputString + digit },
            onBackspace = { if (inputString.isNotEmpty()) inputString = inputString.dropLast(1) },
            onClear = { inputString = "0" }
        )
    }
}

@Composable
fun BaseResultCard(label: String, value: String, accentColor: Color, onCopy: () -> Unit) {
    val colors = LocalGlassColors.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(colors.surfaceGlass)
            .border(1.dp, colors.borderGlass, RoundedCornerShape(14.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = label, fontFamily = SpaceGroteskFontFamily, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = accentColor)
                Text(text = value, fontFamily = JetBrainsMonoFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = colors.textPrimary)
            }
            IconButton(onClick = onCopy) {
                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = colors.textTertiary, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
fun ProgrammerKeypad(
    selectedBase: NumberBase,
    onInput: (String) -> Unit,
    onBackspace: () -> Unit,
    onClear: () -> Unit
) {
    val isHex = selectedBase == NumberBase.HEX
    val isBin = selectedBase == NumberBase.BIN
    val isOct = selectedBase == NumberBase.OCT

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (isHex) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("A", "B", "C", "D", "E", "F").forEach { hexDigit ->
                    GlassKey(hexDigit, modifier = Modifier.weight(1f), keyType = KeyType.OPERATOR) { onInput(hexDigit) }
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            GlassKey("7", modifier = Modifier.weight(1f), keyType = if (isBin) KeyType.SCIENTIFIC else KeyType.DIGIT) { if (!isBin && !isOct) onInput("7") }
            GlassKey("8", modifier = Modifier.weight(1f), keyType = if (isBin || isOct) KeyType.SCIENTIFIC else KeyType.DIGIT) { if (!isBin && !isOct) onInput("8") }
            GlassKey("9", modifier = Modifier.weight(1f), keyType = if (isBin || isOct) KeyType.SCIENTIFIC else KeyType.DIGIT) { if (!isBin && !isOct) onInput("9") }
            GlassKey("AC", modifier = Modifier.weight(1f), keyType = KeyType.ACTION) { onClear() }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            GlassKey("4", modifier = Modifier.weight(1f), keyType = if (isBin) KeyType.SCIENTIFIC else KeyType.DIGIT) { if (!isBin) onInput("4") }
            GlassKey("5", modifier = Modifier.weight(1f), keyType = if (isBin) KeyType.SCIENTIFIC else KeyType.DIGIT) { if (!isBin) onInput("5") }
            GlassKey("6", modifier = Modifier.weight(1f), keyType = if (isBin) KeyType.SCIENTIFIC else KeyType.DIGIT) { if (!isBin) onInput("6") }
            GlassKey("⌫", modifier = Modifier.weight(1f), keyType = KeyType.ACTION) { onBackspace() }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            GlassKey("1", modifier = Modifier.weight(1f)) { onInput("1") }
            GlassKey("2", modifier = Modifier.weight(1f), keyType = if (isBin) KeyType.SCIENTIFIC else KeyType.DIGIT) { if (!isBin) onInput("2") }
            GlassKey("3", modifier = Modifier.weight(1f), keyType = if (isBin) KeyType.SCIENTIFIC else KeyType.DIGIT) { if (!isBin) onInput("3") }
            GlassKey("0", modifier = Modifier.weight(1f)) { onInput("0") }
        }
    }
}
