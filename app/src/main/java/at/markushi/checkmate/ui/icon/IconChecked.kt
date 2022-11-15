package at.markushi.checkmate.ui.icon

import androidx.compose.material.icons.materialIcon
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path

val IconChecked: ImageVector
    get() {
        if (_checked != null) {
            return _checked!!
        }
        _checked = materialIcon(name = "Rounded.Check") {
            path(
                strokeLineCap = StrokeCap.Round,
                strokeLineWidth = 2f,
                strokeAlpha = 0.87f,
                stroke = SolidColor(Color.White)
            ) {
                moveTo(18.0f, 6.7f)
                lineToRelative(-8.48f, 8.48f)
                lineToRelative(-3.54f, -3.54f)
                curveToRelative(-0.39f, -0.39f, -1.02f, -0.39f, -1.41f, 0.0f)
                lineToRelative(0.0f, 0.0f)
                curveToRelative(-0.39f, 0.39f, -0.39f, 1.02f, 0.0f, 1.41f)
                lineToRelative(4.24f, 4.24f)
                curveToRelative(0.39f, 0.39f, 1.02f, 0.39f, 1.41f, 0.0f)
                lineToRelative(9.18f, -9.18f)
                curveToRelative(0.39f, -0.39f, 0.39f, -1.03f, -0.01f, -1.42f)
                lineToRelative(0.0f, 0.0f)
                curveTo(19.02f, 6.31f, 18.39f, 6.31f, 18.0f, 6.7f)
                close()
            }
        }
        return _checked!!
    }

private var _checked: ImageVector? = null
