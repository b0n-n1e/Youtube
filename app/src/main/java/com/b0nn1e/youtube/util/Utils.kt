package com.b0nn1e.youtube.util

import android.app.Activity
import android.app.Fragment
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import okhttp3.ResponseBody

/** 将类名作为 TAG，用于日志打印 */
val Activity.TAG
    get() = this::class.simpleName!!

val Fragment.TAG
    get() = this::class.simpleName!!

val ViewModel.TAG
    get() = this::class.simpleName!!

// 扩展属性，获取当前 Composable 函数的近似名称
// 依赖堆栈追踪，可能有性能开销
val Composable.TAG: String
    get() {
        // 通过堆栈跟踪获取调用者的函数名
        val stackTrace = Thread.currentThread().stackTrace
        // 找到调用 Composable 的函数（跳过内部 Compose 框架调用）
        val caller = stackTrace.firstOrNull { it.className.contains("com.b0nn1e.youtube") }
        return caller?.methodName ?: "UnknownComposable"
    }


inline fun <reified T> convertErrorBody(responseErrorBody : ResponseBody?) : T?{
    runCatching {
        return Gson().fromJson(responseErrorBody!!.string(), T::class.java)
    }
    //异常不做处理
    return null
}