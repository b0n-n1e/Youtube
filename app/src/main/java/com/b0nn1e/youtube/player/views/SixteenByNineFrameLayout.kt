package com.b0nn1e.youtube.player.views

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.RestrictTo

/**
 * 一个保持 16:9 宽高比的 FrameLayout，当高度设置为 WRAP_CONTENT 时生效。
 * 专为视频播放器设计，适合用作 YouTube 播放器视图的容器。
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
open class SixteenByNineFrameLayout : FrameLayout {
    /**
     * 默认构造函数。
     * @param context 上下文
     */
    constructor(context: Context) : this(context, null)

    /**
     * 带属性集的构造函数，用于 XML 布局。
     * @param context 上下文
     * @param attrs 属性集
     */
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    /**
     * 完整构造函数，支持默认样式。
     * @param context 上下文
     * @param attrs 属性集
     * @param defStyleAttr 默认样式属性
     */
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr)

    /**
     * 重写测量逻辑，当高度为 WRAP_CONTENT 时，强制保持 16:9 宽高比。
     * @param widthMeasureSpec 宽度测量规格
     * @param heightMeasureSpec 高度测量规格
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (layoutParams.height == LayoutParams.WRAP_CONTENT) {
            val sixteenNineHeight = MeasureSpec.makeMeasureSpec(
                MeasureSpec.getSize(widthMeasureSpec) * 9 / 16,
                MeasureSpec.EXACTLY
            )
            super.onMeasure(widthMeasureSpec, sixteenNineHeight)
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }
}