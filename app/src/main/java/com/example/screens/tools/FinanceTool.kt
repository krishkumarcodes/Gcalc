package com.example.screens.tools

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.components.GlassCard
import com.example.components.ToolHeader
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalGlassColors
import com.example.ui.theme.SpaceGroteskFontFamily
import kotlin.math.pow

enum class FinanceMode(val title: String) {
    LOAN("Loan / Mortgage"),
    COMPOUND("Compound Interest"),
    TIP("Tip & Split")
}

@Composable
fun FinanceTool(onBack: () -> Unit) {
    BackHandler { onBack() }
    val colors = LocalGlassColors.current

    var selectedMode by remember { mutableStateOf(FinanceMode.LOAN) }

    // Loan inputs
    var principalStr by remember { mutableStateOf("100000") }
    var interestRateStr by remember { mutableStateOf("5.5") }
    var yearsStr by remember { mutableStateOf("15") }

    // Tip inputs
    var billStr by remember { mutableStateOf("85.00") }
    var tipPercentStr by remember { mutableStateOf("18") }
    var peopleStr by remember { mutableStateOf("3") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        ToolHeader(title = "Finance Tools", icon = "💰", onBack = onBack)

        // Modes
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(FinanceMode.entries) { mode ->
                val isSelected = selectedMode == mode
                GlassCard(
                    modifier = Modifier.clickable { selectedMode = mode },
                    cornerRadius = 14.dp,
                    backgroundColor = if (isSelected) colors.accent else colors.surfaceGlassSecondary
                ) {
                    Text(
                        text = mode.title,
                        fontFamily = SpaceGroteskFontFamily,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 13.sp,
                        color = if (isSelected) Color.White else colors.textPrimary,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                    )
                }
            }
        }

        GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 20.dp) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                when (selectedMode) {
                    FinanceMode.LOAN -> {
                        OutlinedTextField(value = principalStr, onValueChange = { principalStr = it }, label = { Text("Principal Amount ($)") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                        OutlinedTextField(value = interestRateStr, onValueChange = { interestRateStr = it }, label = { Text("Annual Interest Rate (%)") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                        OutlinedTextField(value = yearsStr, onValueChange = { yearsStr = it }, label = { Text("Loan Term (Years)") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    }
                    FinanceMode.COMPOUND -> {
                        OutlinedTextField(value = principalStr, onValueChange = { principalStr = it }, label = { Text("Initial Deposit ($)") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                        OutlinedTextField(value = interestRateStr, onValueChange = { interestRateStr = it }, label = { Text("Annual Interest Rate (%)") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                        OutlinedTextField(value = yearsStr, onValueChange = { yearsStr = it }, label = { Text("Investment Horizon (Years)") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    }
                    FinanceMode.TIP -> {
                        OutlinedTextField(value = billStr, onValueChange = { billStr = it }, label = { Text("Bill Amount ($)") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                        OutlinedTextField(value = tipPercentStr, onValueChange = { tipPercentStr = it }, label = { Text("Tip Percentage (%)") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                        OutlinedTextField(value = peopleStr, onValueChange = { peopleStr = it }, label = { Text("Number of People") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    }
                }
            }
        }

        // Calculation Output
        GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 20.dp) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Calculation Summary", fontFamily = SpaceGroteskFontFamily, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = colors.textPrimary)

                when (selectedMode) {
                    FinanceMode.LOAN -> {
                        val p = principalStr.toDoubleOrNull() ?: 100000.0
                        val rateAnnual = (interestRateStr.toDoubleOrNull() ?: 5.0) / 100.0
                        val years = yearsStr.toDoubleOrNull() ?: 15.0
                        val n = years * 12.0
                        val r = rateAnnual / 12.0

                        val monthlyPayment = if (r > 0) {
                            p * (r * (1 + r).pow(n)) / ((1 + r).pow(n) - 1)
                        } else p / n

                        val totalPayment = monthlyPayment * n
                        val totalInterest = totalPayment - p

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Monthly Payment:", color = colors.textSecondary)
                            Text("$%.2f".format(monthlyPayment), fontFamily = JetBrainsMonoFontFamily, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = colors.accent)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Interest:", color = colors.textSecondary)
                            Text("$%.2f".format(totalInterest), fontFamily = JetBrainsMonoFontFamily, fontSize = 14.sp, color = colors.textPrimary)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Paid:", color = colors.textSecondary)
                            Text("$%.2f".format(totalPayment), fontFamily = JetBrainsMonoFontFamily, fontSize = 14.sp, color = colors.textPrimary)
                        }
                    }
                    FinanceMode.COMPOUND -> {
                        val p = principalStr.toDoubleOrNull() ?: 10000.0
                        val r = (interestRateStr.toDoubleOrNull() ?: 5.0) / 100.0
                        val t = yearsStr.toDoubleOrNull() ?: 10.0
                        val amount = p * (1 + r).pow(t)
                        val totalInterest = amount - p

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Future Balance:", color = colors.textSecondary)
                            Text("$%.2f".format(amount), fontFamily = JetBrainsMonoFontFamily, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = colors.accent)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Interest Earned:", color = colors.textSecondary)
                            Text("$%.2f".format(totalInterest), fontFamily = JetBrainsMonoFontFamily, fontSize = 14.sp, color = colors.textPrimary)
                        }
                    }
                    FinanceMode.TIP -> {
                        val bill = billStr.toDoubleOrNull() ?: 0.0
                        val tipPct = (tipPercentStr.toDoubleOrNull() ?: 15.0) / 100.0
                        val numPeople = (peopleStr.toIntOrNull() ?: 1).coerceAtLeast(1)

                        val tipAmount = bill * tipPct
                        val totalBill = bill + tipAmount
                        val perPerson = totalBill / numPeople

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Per Person:", color = colors.textSecondary)
                            Text("$%.2f".format(perPerson), fontFamily = JetBrainsMonoFontFamily, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = colors.accent)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Tip Amount:", color = colors.textSecondary)
                            Text("$%.2f".format(tipAmount), fontFamily = JetBrainsMonoFontFamily, fontSize = 14.sp, color = colors.textPrimary)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Bill:", color = colors.textSecondary)
                            Text("$%.2f".format(totalBill), fontFamily = JetBrainsMonoFontFamily, fontSize = 14.sp, color = colors.textPrimary)
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(20.dp))
    }
}
