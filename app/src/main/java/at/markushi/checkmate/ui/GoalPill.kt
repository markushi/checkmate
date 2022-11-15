package at.markushi.checkmate.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.ColorUtils
import at.markushi.checkmate.ui.icon.IconChecked
import at.markushi.checkmate.ui.icon.IconUnchecked

@Composable
fun GoalPill(
    name: String,
    color: Color,
    checked: Boolean,
    compact: Boolean,
    showCheckBox: Boolean,
    onGoalClicked: () -> Unit
) {
    val shape = RoundedCornerShape(if (compact) 12.dp else 24.dp)
    val dotRadius = if (compact) 8.dp else 16.dp
    val textSize = if (compact) 14.sp else 18.sp
    val pillHeight = if (compact) 80.dp else 56.dp
    val outerPadding = if (compact) 8.dp else 16.dp
    val dotPositionX = if (compact) 16.dp else 36.dp

    val spacerSize = when {
        compact && checked -> 8.dp
        compact && !checked -> 24.dp
        checked -> 8.dp
        else -> 48.dp
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp))
            .clickable(onClick = onGoalClicked),
    ) {
        val radius = animateFloatAsState(
            targetValue = if (checked) 1f else 0f,
            animationSpec = tween(360)
        )

        Canvas(modifier = Modifier.matchParentSize(), onDraw = {
            if (radius.value < 1.0f) {
                val actualRadius = dotRadius.toPx() + radius.value * size.width
                drawCircle(
                    color = color,
                    radius = actualRadius,
                    Offset(dotPositionX.toPx(), size.height / 2f)
                )
            } else {
                drawRect(color)
            }
        })

        Row(
            modifier = Modifier
                .padding(outerPadding)
                .padding(end = 4.dp)
                .height(pillHeight),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(
                modifier = Modifier
                    .animateContentSize(tween(easing = FastOutSlowInEasing))
                    .size(spacerSize, 24.dp)
            )
            var goalTextColor = MaterialTheme.colorScheme.onSurface
            if (checked) {
                val defaultContrast = ColorUtils.calculateContrast(
                    MaterialTheme.colorScheme.onSurface.toArgb(),
                    color.toArgb()
                )
                val inverseContrast = ColorUtils.calculateContrast(
                    MaterialTheme.colorScheme.inverseOnSurface.toArgb(),
                    color.toArgb()
                )
                if (inverseContrast > defaultContrast) {
                    goalTextColor = MaterialTheme.colorScheme.inverseOnSurface
                }
            }
            Text(
                text = name,
                color = goalTextColor,
                fontSize = textSize,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            if (showCheckBox) {
                Spacer(modifier = Modifier.weight(1.0f))
                Icon(
                    if (checked) IconChecked else IconUnchecked,
                    tint = goalTextColor,
                    contentDescription = "check"
                )
            }
        }
    }
}

@Preview
@Composable
fun GoalPillPreview() {
    val checked = remember {
        mutableStateOf(false)
    }
    MaterialTheme(darkColorScheme()) {
        Surface() {
            GoalPill(
                name = "Hello World",
                color = Color.Gray,
                checked = checked.value,
                showCheckBox = true,
                compact = true
            ) {
                checked.value = !checked.value
            }
        }
    }
}