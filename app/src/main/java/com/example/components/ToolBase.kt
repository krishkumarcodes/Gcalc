package com.example.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalGlassColors
import com.example.ui.theme.SpaceGroteskFontFamily

@Composable
fun ToolHeader(
    title: String,
    icon: Any? = null,
    onBack: () -> Unit
) {
    val colors = LocalGlassColors.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(colors.surfaceGlass)
                .border(1.dp, colors.borderGlass, CircleShape)
                .clickable { onBack() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = colors.textPrimary,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        if (icon != null) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(colors.accent.copy(alpha = 0.2f))
                    .border(1.dp, colors.accent.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                when (icon) {
                    is ImageVector -> {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = colors.accent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    is String -> {
                        Text(text = icon, fontSize = 18.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
        }

        Text(
            text = title,
            fontFamily = SpaceGroteskFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = colors.textPrimary
        )
    }
}

@Composable
fun ToolActionRow(
    modifier: Modifier = Modifier,
    onReset: (() -> Unit)? = null,
    onCopy: (() -> Unit)? = null,
    onShare: (() -> Unit)? = null,
    onSwap: (() -> Unit)? = null
) {
    val colors = LocalGlassColors.current

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onReset != null) {
            OutlinedButton(
                onClick = onReset,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.textPrimary)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Reset", modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Reset", fontSize = 12.sp)
            }
        }
        if (onSwap != null) {
            OutlinedButton(
                onClick = onSwap,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.textPrimary)
            ) {
                Icon(Icons.Default.SwapVert, contentDescription = "Swap", modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Swap", fontSize = 12.sp)
            }
        }
        if (onCopy != null) {
            Button(
                onClick = onCopy,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colors.accent, contentColor = Color.White)
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Copy", fontSize = 12.sp)
            }
        }
    }
}
