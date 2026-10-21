package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.screens.CalculatorScreen
import com.example.screens.FormulasScreen
import com.example.screens.HistorySettingsScreen
import com.example.screens.ToolsScreen
import com.example.service.HapticFeedbackService
import com.example.service.LocalHapticFeedbackService
import com.example.store.HistoryRepository
import com.example.store.SettingsStore
import com.example.ui.theme.GcalcTheme
import com.example.ui.theme.LocalGlassColors
import com.example.ui.theme.SpaceGroteskFontFamily

enum class BottomNavItem(val title: String, val icon: ImageVector) {
    CALCULATOR("Calculator", Icons.Default.Calculate),
    HISTORY("History", Icons.Default.History),
    TOOLS("Tools", Icons.Default.Widgets),
    FORMULAS("Formulas", Icons.Default.Functions),
    SETTINGS("Settings", Icons.Default.Settings)
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val context = LocalContext.current
            val settingsStore = remember { SettingsStore(context) }
            val historyRepository = remember { HistoryRepository(context) }
            val hapticService = remember { HapticFeedbackService(context, settingsStore) }

            GcalcTheme(
                themeMode = settingsStore.themeMode,
                accentColor = settingsStore.accentColor.color
            ) {
                CompositionLocalProvider(
                    LocalHapticFeedbackService provides hapticService
                ) {
                    val colors = LocalGlassColors.current
                    var currentTab by remember { mutableStateOf(BottomNavItem.CALCULATOR) }
                    var currentCalculatorExpr by remember { mutableStateOf<String?>(null) }

                    Scaffold(
                        bottomBar = {
                            NavigationBar(
                                containerColor = colors.surfaceGlass,
                                contentColor = colors.textPrimary,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                            ) {
                                BottomNavItem.entries.forEach { item ->
                                    val isSelected = currentTab == item
                                    NavigationBarItem(
                                        selected = isSelected,
                                        onClick = {
                                            currentTab = item
                                            hapticService.performKeyTap(8L)
                                        },
                                        icon = {
                                            Icon(
                                                imageVector = item.icon,
                                                contentDescription = item.title,
                                                tint = if (isSelected) colors.accent else colors.textTertiary
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = item.title,
                                                fontFamily = SpaceGroteskFontFamily,
                                                color = if (isSelected) colors.accent else colors.textTertiary
                                            )
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            indicatorColor = colors.accentContainer.copy(alpha = 0.5f)
                                        )
                                    )
                                }
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            when (currentTab) {
                                BottomNavItem.CALCULATOR -> CalculatorScreen(
                                    settingsStore = settingsStore,
                                    historyRepository = historyRepository,
                                    initialExpression = currentCalculatorExpr,
                                    onNavigateToFormulas = { currentTab = BottomNavItem.FORMULAS }
                                )
                                BottomNavItem.HISTORY -> HistorySettingsScreen(
                                    settingsStore = settingsStore,
                                    historyRepository = historyRepository,
                                    isSettingsMode = false,
                                    onSelectHistoryItem = { expr ->
                                        currentCalculatorExpr = expr
                                        currentTab = BottomNavItem.CALCULATOR
                                    }
                                )
                                BottomNavItem.TOOLS -> ToolsScreen(
                                    onUseInCalculator = { expr ->
                                        currentCalculatorExpr = expr.toString()
                                        currentTab = BottomNavItem.CALCULATOR
                                    }
                                )
                                BottomNavItem.FORMULAS -> FormulasScreen(
                                    onUseInCalculator = { expr ->
                                        currentCalculatorExpr = expr
                                        currentTab = BottomNavItem.CALCULATOR
                                    }
                                )
                                BottomNavItem.SETTINGS -> HistorySettingsScreen(
                                    settingsStore = settingsStore,
                                    historyRepository = historyRepository,
                                    isSettingsMode = true
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
