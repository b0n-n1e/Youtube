package com.b0nn1e.youtube.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.b0nn1e.youtube.ui.theme.YoutubeTheme.Theme

// 亮色主题：现代清新风格
private val LightColorPalette = YoutubeColors(
    bottomBar = white,        // 纯白底部导航栏，干净简洁
    background = white1,      // 柔和近白色背景，温暖不刺眼
    listItem = white2,       // 列表项略深，增加层次感
    divider = white4,        // 浅灰分隔线，微妙区分
    textPrimary = black3,    // 深灰黑色，清晰易读
    textSecondary = grey1,   // 浅灰色次要文本
    onBackground = grey3,    // 中灰色，用于背景上的次要元素
    icon = grey3,            // 未选中图标，中性灰色
    iconCurrent = red5,     // 选中图标，明亮蓝色，活力突出
    textFieldBackground = white2, // 文本输入框背景，略深白色
    more = grey4,            // 更多按钮，浅灰色
)

// 暗色主题：优雅沉浸风格
private val DarkColorPalette = YoutubeColors(
    bottomBar = black3,      // 深灰黑色导航栏，优雅突出
    background = black4,     // 深灰黑色背景，沉浸感强
    listItem = black2,       // 列表项稍亮，增加层次
    divider = black5,        // 深灰分隔线，微妙区分
    textPrimary = white2,    // 柔和白色，易读
    textSecondary = grey4,   // 浅灰色次要文本
    onBackground = grey1,    // 浅灰色，用于背景上的次要元素
    icon = grey4,            // 未选中图标，浅灰色
    iconCurrent = red5,     // 选中图标，热粉色，热情醒目
    textFieldBackground = black1, // 文本输入框背景，稍亮深色
    more = grey5,            // 更多按钮，深灰色
)

@Immutable
data class YoutubeColors(
    val bottomBar: Color,
    val background: Color,
    val listItem: Color,
    val divider: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val onBackground: Color,
    val icon: Color,
    val iconCurrent: Color,
    val textFieldBackground: Color,
    val more: Color
)

private val LocalYoutubeColors = staticCompositionLocalOf<YoutubeColors> {
    LightColorPalette
}

object YoutubeTheme {
    val colors: YoutubeColors
        @Composable
        get() = LocalYoutubeColors.current


    val isDarkTheme: Boolean
        @Composable
        get() = LocalYoutubeColors.current == DarkColorPalette

    enum class Theme {
        Light, Dark
    }
}

@Composable
fun ProvideYoutubeColors(
    colors: YoutubeColors,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalYoutubeColors provides colors, content = content)
}

@Composable
fun YoutubeTheme(
    theme: Theme = if (isSystemInDarkTheme()) Theme.Dark else Theme.Light,
    content: @Composable () -> Unit,
) {
    val targetColors = when(theme) {
        Theme.Light -> LightColorPalette
        Theme.Dark -> DarkColorPalette
//        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> dynamicColorPalette(isSystemInDarkTheme())
//        else -> if (isSystemInDarkTheme()) DarkColorPalette else LightColorPalette

    }

    val colors = YoutubeColors(
        bottomBar = animateColorAsState(targetColors.bottomBar, tween(600)).value,
        background = animateColorAsState(targetColors.background, tween(600)).value,
        listItem = animateColorAsState(targetColors.listItem, tween(600)).value,
        divider = animateColorAsState(targetColors.divider, tween(600)).value,
        textPrimary = animateColorAsState(targetColors.textPrimary, tween(600)).value,
        textSecondary = animateColorAsState(targetColors.textSecondary, tween(600)).value,
        onBackground = animateColorAsState(targetColors.onBackground, tween(600)).value,
        icon = animateColorAsState(targetColors.icon, tween(600)).value,
        iconCurrent = animateColorAsState(targetColors.iconCurrent, tween(600)).value,
        textFieldBackground = animateColorAsState(targetColors.textFieldBackground, tween(600)).value,
        more = animateColorAsState(targetColors.more, tween(600)).value,
    )

    ProvideYoutubeColors(colors) {
        MaterialTheme(
            colorScheme = when (theme) {
                Theme.Light -> lightColorScheme(
                    background = colors.background,
                    surface = colors.background,
                    onBackground = colors.textPrimary,
                    onSurface = colors.textPrimary
                )
                Theme.Dark -> darkColorScheme(
                    background = colors.background,
                    surface = colors.background,
                    onBackground = colors.textPrimary,
                    onSurface = colors.textPrimary
                )
            },
            typography = Typography,
            content = content
        )
    }
}