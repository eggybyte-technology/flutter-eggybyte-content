package com.eggybyte.content

import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PluginLogger {

    private const val TAG_PREFIX = "EggyByteSDK"
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())

    private enum class LogType {
        DEBUG, INFO, WARNING, ERROR
    }

    private fun getCurrentTimestamp(): String {
        return dateFormat.format(Date())
    }

    private fun formatMessage(logType: LogType, className: String, message: String, details: Map<String, Any?>? = null): String {
        val time = getCurrentTimestamp()
        val detailsString = details?.takeIf { it.isNotEmpty() }?.entries?.joinToString(separator = ", ", prefix = " {", postfix = "}") {
            "${it.key}: '${it.value}'"
        } ?: ""
        return "$time [${logType.name}] ($className): $message$detailsString"
    }

    private fun formatErrorMessage(className: String, message: String, throwable: Throwable? = null, context: Map<String, Any?>? = null): String {
        val time = getCurrentTimestamp()
        val contextString = context?.takeIf { it.isNotEmpty() }?.entries?.joinToString(separator = ", ", prefix = " [Context: ", postfix = "]") {
            "${it.key}: '${it.value}'"
        } ?: ""
        val throwableMessage = throwable?.message?.let { " | Exception: $it" } ?: ""
        return "$time [ERROR] ($className): $message$contextString$throwableMessage"
    }

    fun d(className: String, message: String, details: Map<String, Any?>? = null) {
        Log.d("$TAG_PREFIX-$className", formatMessage(LogType.DEBUG, className, message, details))
    }

    fun i(className: String, message: String, details: Map<String, Any?>? = null) {
        Log.i("$TAG_PREFIX-$className", formatMessage(LogType.INFO, className, message, details))
    }

    fun w(className: String, message: String, details: Map<String, Any?>? = null, throwable: Throwable? = null) {
        if (throwable != null) {
            Log.w("$TAG_PREFIX-$className", formatErrorMessage(className, message, throwable, details), throwable)
        } else {
            Log.w("$TAG_PREFIX-$className", formatMessage(LogType.WARNING, className, message, details))
        }
    }

    fun e(className: String, message: String, throwable: Throwable? = null, context: Map<String, Any?>? = null) {
        Log.e("$TAG_PREFIX-$className", formatErrorMessage(className, message, throwable, context), throwable)
    }
} 