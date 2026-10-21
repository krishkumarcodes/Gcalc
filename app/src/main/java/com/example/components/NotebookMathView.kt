package com.example.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalGlassColors

sealed class MathNode {
    data class TextNode(val text: String) : MathNode()
    data class FractionNode(val num: MathNode, val den: MathNode) : MathNode()
    data class PowerNode(val base: MathNode, val exponent: MathNode) : MathNode()
    data class RowNode(val nodes: List<MathNode>) : MathNode()
}

object MathParser {
    fun parse(expr: String): MathNode {
        val trimmed = expr.trim()
        if (trimmed.contains("/")) {
            val parts = trimmed.split("/", limit = 2)
            if (parts.size == 2) {
                val numNode = parse(parts[0])
                val denNode = parse(parts[1])
                return MathNode.FractionNode(numNode, denNode)
            }
        }
        return MathNode.TextNode(trimmed)
    }
}

@Composable
fun NotebookMathDisplay(
    mathString: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle.Default,
    color: Color = Color.Unspecified
) {
    val node = MathParser.parse(mathString)
    val glassColors = LocalGlassColors.current
    val textColor = if (color != Color.Unspecified) color else glassColors.textPrimary

    RenderMathNode(node = node, modifier = modifier, textStyle = textStyle, color = textColor)
}

@Composable
fun RenderMathNode(
    node: MathNode,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle.Default,
    color: Color = Color.White
) {
    val glassColors = LocalGlassColors.current

    when (node) {
        is MathNode.TextNode -> {
            Text(
                text = node.text,
                modifier = modifier,
                style = textStyle.copy(fontFamily = JetBrainsMonoFontFamily),
                color = color
            )
        }
        is MathNode.FractionNode -> {
            Column(
                modifier = modifier,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                RenderMathNode(
                    node = node.num,
                    textStyle = textStyle.copy(fontSize = (textStyle.fontSize.value * 0.85f).sp),
                    color = color
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(vertical = 2.dp)
                        .height(1.5.dp)
                        .background(color)
                )
                RenderMathNode(
                    node = node.den,
                    textStyle = textStyle.copy(fontSize = (textStyle.fontSize.value * 0.85f).sp),
                    color = color
                )
            }
        }
        is MathNode.PowerNode -> {
            Row(modifier = modifier, verticalAlignment = Alignment.Top) {
                RenderMathNode(node = node.base, textStyle = textStyle, color = color)
                RenderMathNode(
                    node = node.exponent,
                    textStyle = textStyle.copy(fontSize = (textStyle.fontSize.value * 0.7f).sp),
                    color = color
                )
            }
        }
        is MathNode.RowNode -> {
            Row(
                modifier = modifier,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                node.nodes.forEach { child ->
                    RenderMathNode(node = child, textStyle = textStyle, color = color)
                }
            }
        }
    }
}
