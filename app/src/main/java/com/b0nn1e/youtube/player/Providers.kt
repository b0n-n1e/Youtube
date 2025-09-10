package com.b0nn1e.youtube.player

/**
 * 接收布尔值的回调接口。
 * 主要用于处理 JavaScript 返回的布尔值，如静音状态。
 * 仅用于兼容 Java 7 及以下版本。
 */
fun interface BooleanProvider {
    /**
     * 接收布尔值。
     * @param value 布尔值
     */
    fun accept(value: Boolean)
}