package at.markushi.checkmate.ui.icon

import androidx.compose.material.icons.materialIcon
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path

val IconUnchecked: ImageVector
    get() {
        if (_unchecked != null) {
            return _unchecked!!
        }
        _unchecked = materialIcon(name = "Rounded.UnCheck") {
            path(
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                strokeLineWidth = 2f, strokeAlpha = 0.66f, stroke = SolidColor(Color.White)
            ) {
                moveTo(4f, 4f)
                lineTo(20f, 4f)
                lineTo(20f, 20f)
                lineTo(4f, 20f)
                lineTo(4f, 4f)
                close()
            }
        }
        return _unchecked!!
    }

private var _unchecked: ImageVector? = null
