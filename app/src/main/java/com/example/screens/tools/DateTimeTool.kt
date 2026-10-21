package com.example.screens.tools

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.components.GlassCard
import com.example.components.ToolHeader
import com.example.ui.theme.LocalGlassColors
import com.example.ui.theme.SpaceGroteskFontFamily
import com.example.ui.theme.JetBrainsMonoFontFamily
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.abs

enum class DateToolMode(val title: String) {
    DIFFERENCE("Date Difference"),
    ADD_SUBTRACT("Add / Subtract"),
    AGE("Age Calculator")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimeTool(onBack: () -> Unit) {
    BackHandler { onBack() }
    val colors = LocalGlassColors.current

    var activeMode by remember { mutableStateOf(DateToolMode.DIFFERENCE) }

    // Date state
    var startDate by remember { mutableStateOf(LocalDate.now()) }
    var endDate by remember { mutableStateOf(LocalDate.now().plusDays(14)) }
    var birthDate by remember { mutableStateOf(LocalDate.of(2000, 1, 1)) }

    // Add/subtract state
    var offsetAmount by remember { mutableIntStateOf(30) }
    var offsetUnit by remember { mutableStateOf("Days") } // Days, Weeks, Months, Years
    var isAdd by remember { mutableStateOf(true) }

    // Picker dialog state
    var activePickerTarget by remember { mutableStateOf<String?>(null) } // "start", "end", "birth"

    val dateFormatter = remember { DateTimeFormatter.ofPattern("EEE, MMM d, yyyy") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        ToolHeader(title = "Date & Time", icon = "📅", onBack = onBack)

        // Mode Selector Chips
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(DateToolMode.entries) { mode ->
                val isSelected = activeMode == mode
                GlassCard(
                    modifier = Modifier.clickable { activeMode = mode },
                    cornerRadius = 14.dp,
                    backgroundColor = if (isSelected) colors.accent else colors.surfaceGlassSecondary
                ) {
                    Text(
                        text = mode.title,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        fontFamily = SpaceGroteskFontFamily,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.White else colors.textPrimary
                    )
                }
            }
        }

        when (activeMode) {
            DateToolMode.DIFFERENCE -> {
                // Difference Inputs
                GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 24.dp) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Select Dates", fontSize = 12.sp, color = colors.accent, fontWeight = FontWeight.Bold)
                        DateBox(
                            label = "Start Date",
                            formattedDate = startDate.format(dateFormatter),
                            onClick = { activePickerTarget = "start" }
                        )
                        DateBox(
                            label = "End Date",
                            formattedDate = endDate.format(dateFormatter),
                            onClick = { activePickerTarget = "end" }
                        )
                    }
                }

                // Difference Results
                val totalDays = ChronoUnit.DAYS.between(startDate, endDate)
                val absDays = abs(totalDays)
                val period = Period.between(
                    if (startDate.isBefore(endDate)) startDate else endDate,
                    if (startDate.isBefore(endDate)) endDate else startDate
                )
                val weeks = absDays / 7
                val remDays = absDays % 7

                GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 24.dp, backgroundColor = colors.accentContainer) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("Duration", fontSize = 12.sp, color = colors.accent, fontFamily = SpaceGroteskFontFamily, fontWeight = FontWeight.Bold)
                        Text(
                            text = "$absDays Days",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = SpaceGroteskFontFamily,
                            color = colors.textPrimary
                        )

                        HorizontalDivider(color = colors.borderGlass.copy(alpha = 0.3f), thickness = 1.dp)

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Calendar breakdown", fontFamily = SpaceGroteskFontFamily, fontSize = 13.sp, color = colors.textSecondary)
                            Text("${period.years}y, ${period.months}m, ${period.days}d", fontFamily = JetBrainsMonoFontFamily, fontWeight = FontWeight.Bold, color = colors.textPrimary)
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("In weeks", fontFamily = SpaceGroteskFontFamily, fontSize = 13.sp, color = colors.textSecondary)
                            Text("$weeks w + $remDays d", fontFamily = JetBrainsMonoFontFamily, fontWeight = FontWeight.Bold, color = colors.textPrimary)
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total hours", fontFamily = SpaceGroteskFontFamily, fontSize = 13.sp, color = colors.textSecondary)
                            Text("${absDays * 24} hrs", fontFamily = JetBrainsMonoFontFamily, color = colors.textPrimary)
                        }
                    }
                }
            }

            DateToolMode.ADD_SUBTRACT -> {
                // Add / Subtract Inputs
                GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 24.dp) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Base Date & Operation", fontSize = 12.sp, color = colors.accent, fontWeight = FontWeight.Bold)
                        DateBox(
                            label = "Start From",
                            formattedDate = startDate.format(dateFormatter),
                            onClick = { activePickerTarget = "start" }
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            GlassCard(
                                modifier = Modifier.weight(1f).clickable { isAdd = true },
                                cornerRadius = 12.dp,
                                backgroundColor = if (isAdd) colors.accent else colors.surfaceGlassSecondary
                            ) {
                                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
                                    Text("+ Add", fontWeight = FontWeight.Bold, color = if (isAdd) Color.White else colors.textPrimary)
                                }
                            }
                            GlassCard(
                                modifier = Modifier.weight(1f).clickable { isAdd = false },
                                cornerRadius = 12.dp,
                                backgroundColor = if (!isAdd) colors.accent else colors.surfaceGlassSecondary
                            ) {
                                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
                                    Text("− Subtract", fontWeight = FontWeight.Bold, color = if (!isAdd) Color.White else colors.textPrimary)
                                }
                            }
                        }

                        // Amount & Unit Selector
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            listOf(7, 14, 30, 90, 365).forEach { preset ->
                                GlassCard(
                                    modifier = Modifier.weight(1f).clickable { offsetAmount = preset; offsetUnit = "Days" },
                                    cornerRadius = 10.dp,
                                    backgroundColor = if (offsetAmount == preset && offsetUnit == "Days") colors.accentContainer else colors.surfaceGlassSecondary
                                ) {
                                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), contentAlignment = Alignment.Center) {
                                        Text("$preset d", fontSize = 11.sp, fontFamily = JetBrainsMonoFontFamily, color = colors.textPrimary)
                                    }
                                }
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Days", "Weeks", "Months", "Years").forEach { unit ->
                                GlassCard(
                                    modifier = Modifier.weight(1f).clickable { offsetUnit = unit },
                                    cornerRadius = 10.dp,
                                    backgroundColor = if (offsetUnit == unit) colors.accent else colors.surfaceGlassSecondary
                                ) {
                                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                                        Text(unit, fontSize = 11.sp, fontWeight = if (offsetUnit == unit) FontWeight.Bold else FontWeight.Normal, color = if (offsetUnit == unit) Color.White else colors.textPrimary)
                                    }
                                }
                            }
                        }
                    }
                }

                // Calculated result date
                val calculatedDate = remember(startDate, offsetAmount, offsetUnit, isAdd) {
                    val factor = if (isAdd) 1L else -1L
                    when (offsetUnit) {
                        "Days" -> startDate.plusDays(factor * offsetAmount)
                        "Weeks" -> startDate.plusWeeks(factor * offsetAmount)
                        "Months" -> startDate.plusMonths(factor * offsetAmount)
                        "Years" -> startDate.plusYears(factor * offsetAmount)
                        else -> startDate
                    }
                }

                GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 24.dp, backgroundColor = colors.accentContainer) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("Calculated Date", fontSize = 12.sp, color = colors.accent, fontFamily = SpaceGroteskFontFamily, fontWeight = FontWeight.Bold)
                        Text(
                            text = calculatedDate.format(dateFormatter),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = SpaceGroteskFontFamily,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "${if (isAdd) "+" else "-"}$offsetAmount $offsetUnit from ${startDate.format(DateTimeFormatter.ofPattern("MMM d"))}",
                            fontSize = 13.sp,
                            color = colors.textSecondary
                        )
                    }
                }
            }

            DateToolMode.AGE -> {
                // Age Calculator
                GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 24.dp) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Date of Birth", fontSize = 12.sp, color = colors.accent, fontWeight = FontWeight.Bold)
                        DateBox(
                            label = "Birth Date",
                            formattedDate = birthDate.format(dateFormatter),
                            onClick = { activePickerTarget = "birth" }
                        )
                    }
                }

                val today = LocalDate.now()
                val agePeriod = remember(birthDate) { Period.between(birthDate, today) }
                val totalDaysLived = remember(birthDate) { ChronoUnit.DAYS.between(birthDate, today) }
                val nextBirthday = remember(birthDate) {
                    var next = birthDate.withYear(today.year)
                    if (next.isBefore(today) || next.isEqual(today)) {
                        next = next.plusYears(1)
                    }
                    next
                }
                val daysUntilBirthday = remember(nextBirthday) { ChronoUnit.DAYS.between(today, nextBirthday) }

                GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 24.dp, backgroundColor = colors.accentContainer) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("Exact Age", fontSize = 12.sp, color = colors.accent, fontFamily = SpaceGroteskFontFamily, fontWeight = FontWeight.Bold)
                        Text(
                            text = "${agePeriod.years} Years old",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = SpaceGroteskFontFamily,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "${agePeriod.months} months, ${agePeriod.days} days",
                            fontSize = 14.sp,
                            color = colors.textSecondary
                        )

                        HorizontalDivider(color = colors.borderGlass.copy(alpha = 0.3f), thickness = 1.dp)

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Next birthday in", fontFamily = SpaceGroteskFontFamily, fontSize = 13.sp, color = colors.textSecondary)
                            Text("$daysUntilBirthday days", fontFamily = JetBrainsMonoFontFamily, fontWeight = FontWeight.Bold, color = colors.accent)
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total days lived", fontFamily = SpaceGroteskFontFamily, fontSize = 13.sp, color = colors.textSecondary)
                            Text("$totalDaysLived days", fontFamily = JetBrainsMonoFontFamily, color = colors.textPrimary)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }

    // Material 3 Date Picker Dialog
    if (activePickerTarget != null) {
        val initialDate = when (activePickerTarget) {
            "start" -> startDate
            "end" -> endDate
            "birth" -> birthDate
            else -> LocalDate.now()
        }
        val initialEpochMillis = initialDate.atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli()
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialEpochMillis)

        DatePickerDialog(
            onDismissRequest = { activePickerTarget = null },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val selected = Instant.ofEpochMilli(millis).atZone(ZoneId.of("UTC")).toLocalDate()
                        when (activePickerTarget) {
                            "start" -> startDate = selected
                            "end" -> endDate = selected
                            "birth" -> birthDate = selected
                        }
                    }
                    activePickerTarget = null
                }) {
                    Text("OK", color = colors.accent, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { activePickerTarget = null }) {
                    Text("Cancel", color = colors.textSecondary)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
fun DateBox(label: String, formattedDate: String, onClick: () -> Unit) {
    val colors = LocalGlassColors.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Text(label, fontSize = 11.sp, color = colors.textSecondary, fontFamily = SpaceGroteskFontFamily)
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(colors.surfaceGlassSecondary)
                .border(1.dp, colors.borderGlass.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                .padding(horizontal = 14.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Text(formattedDate, fontFamily = SpaceGroteskFontFamily, fontWeight = FontWeight.Medium, color = colors.textPrimary)
                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = colors.accent, modifier = Modifier.size(18.dp))
            }
        }
    }
}
