package at.markushi.checkmate.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import at.markushi.checkmate.R

@Composable
fun BottomAppBarButton(
    title: String? = null,
    icon: ImageVector? = null,
    active: Boolean = false,
    onClick: () -> Unit = { }
) {
    val activeColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
    val backgroundColor =
        animateColorAsState(targetValue = if (active) activeColor else Color.Transparent)
    Box(
        modifier = Modifier
            .padding(4.dp)
            .clip(RoundedCornerShape(50))
            .background(backgroundColor.value)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 12.dp)
    ) {
        title?.let {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onBackground,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }

        icon?.let {
            Icon(
                it,
                contentDescription = stringResource(id = R.string.main_menu_settings)
            )
        }

    }
}