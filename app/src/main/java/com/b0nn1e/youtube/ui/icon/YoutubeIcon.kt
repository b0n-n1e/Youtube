package com.b0nn1e.youtube.ui.icon

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.b0nn1e.youtube.ui.theme.YoutubeTheme

val Youtube: ImageVector
    @Composable
    get() {
        if (_Youtube != null) return _Youtube!!

        _Youtube = ImageVector.Builder(
            name = "Youtube",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(YoutubeTheme.colors.iconCurrent),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(2.5f, 17f)
                arcToRelative(24.1f, 24.1f, 0f, false, true, 0f, -10f)
                arcToRelative(2f, 2f, 0f, false, true, 1.4f, -1.4f)
                arcToRelative(49.6f, 49.6f, 0f, false, true, 16.2f, 0f)
                arcTo(2f, 2f, 0f, false, true, 21.5f, 7f)
                arcToRelative(24.1f, 24.1f, 0f, false, true, 0f, 10f)
                arcToRelative(2f, 2f, 0f, false, true, -1.4f, 1.4f)
                arcToRelative(49.6f, 49.6f, 0f, false, true, -16.2f, 0f)
                arcTo(2f, 2f, 0f, false, true, 2.5f, 17f)
            }
            path(
                stroke = SolidColor(YoutubeTheme.colors.iconCurrent),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveToRelative(10f, 15f)
                lineToRelative(5f, -3f)
                lineToRelative(-5f, -3f)
                close()
            }
        }.build()

        return _Youtube!!
    }

private var _Youtube: ImageVector? = null

@Composable
fun YouTubeIcon() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(YoutubeTheme.colors.background),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Youtube,
            contentDescription = "YouTube Icon",
            modifier = Modifier
                .size(width = 120.dp, height = 120.dp),
            tint = YoutubeTheme.colors.iconCurrent
        )

    }
}

@Composable
@Preview(showBackground = true)
private fun YoutubeIconPre(){
    YoutubeTheme {
        YouTubeIcon()
    }
}