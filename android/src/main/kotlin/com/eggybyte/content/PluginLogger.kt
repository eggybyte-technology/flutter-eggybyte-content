package com.eggybyte.content

import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * A utility object for standardized logging within the plugin.
 *
 * Provides methods for different log levels (DEBUG, INFO, WARNING, ERROR)
 * and ensures consistent formatting including timestamps, log levels, class names,
 * and optional structured details or exception information.
 */
object PluginLogger {

    private const val TAG_PREFIX = "EggyByteSDK"
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())

    /**
     * Represents the different log levels supported by the logger.
     */
    private enum class LogType {
        DEBUG, INFO, WARNING, ERROR
    }

    /**
     * Retrieves the current system timestamp formatted as a string.
     * @return A string representation of the current timestamp.
     */
    private fun getCurrentTimestamp(): String {
        return dateFormat.format(Date())
    }

    /**
     * Formats a log message with a standard structure.
     *
     * @param logType The [LogType] of the message (e.g., DEBUG, INFO).
     * @param className The name of the class from which the log originated.
     * @param message The main log message string.
     * @param details An optional map of key-value pairs providing additional context to the log message.
     * @return The formatted log string.
     */
    private fun formatMessage(logType: LogType, className: String, message: String, details: Map<String, Any?>? = null): String {
        val time = getCurrentTimestamp()
        val detailsString = details?.takeIf { it.isNotEmpty() }?.entries?.joinToString(separator = ", ", prefix = " {", postfix = "}") {
            "${it.key}: '${it.value}'"
        } ?: ""
        return "$time [${logType.name}] ($className): $message$detailsString"
    }

    /**
     * Formats an error message, including optional throwable information and context.
     *
     * @param className The name of the class from which the error originated.
     * @param message The main error message string.
     * @param throwable An optional [Throwable] associated with the error.
     * @param context An optional map of key-value pairs providing additional context to the error.
     * @return The formatted error log string.
     */
    private fun formatErrorMessage(className: String, message: String, throwable: Throwable? = null, context: Map<String, Any?>? = null): String {
        val time = getCurrentTimestamp()
        val contextString = context?.takeIf { it.isNotEmpty() }?.entries?.joinToString(separator = ", ", prefix = " [Context: ", postfix = "]") {
            "${it.key}: '${it.value}'"
        } ?: ""
        val throwableMessage = throwable?.message?.let { " | Exception: $it" } ?: ""
        return "$time [ERROR] ($className): $message$contextString$throwableMessage"
    }

    /**
     * Logs a DEBUG level message.
     *
     * @param className The name of the class from which the log originated.
     * @param message The main log message string.
     * @param details An optional map of key-value pairs providing additional context.
     */
    fun d(className: String, message: String, details: Map<String, Any?>? = null) {
        Log.d("$TAG_PREFIX-$className", formatMessage(LogType.DEBUG, className, message, details))
    }

    /**
     * Logs an INFO level message.
     *
     * @param className The name of the class from which the log originated.
     * @param message The main log message string.
     * @param details An optional map of key-value pairs providing additional context.
     */
    fun i(className: String, message: String, details: Map<String, Any?>? = null) {
        Log.i("$TAG_PREFIX-$className", formatMessage(LogType.INFO, className, message, details))
    }

    /**
     * Logs a WARNING level message.
     *
     * @param className The name of the class from which the log originated.
     * @param message The main log message string.
     * @param details An optional map of key-value pairs providing additional context.
     * @param throwable An optional [Throwable] associated with the warning.
     */
    fun w(className: String, message: String, details: Map<String, Any?>? = null, throwable: Throwable? = null) {
        if (throwable != null) {
            Log.w("$TAG_PREFIX-$className", formatErrorMessage(className, message, throwable, details), throwable)
        } else {
            Log.w("$TAG_PREFIX-$className", formatMessage(LogType.WARNING, className, message, details))
        }
    }

    /**
     * Logs an ERROR level message.
     *
     * @param className The name of the class from which the log originated.
     * @param message The main error message string.
     * @param throwable An optional [Throwable] associated with the error. Its stack trace will be printed.
     * @param context An optional map of key-value pairs providing additional context to the error.
     */
    fun e(className: String, message: String, throwable: Throwable? = null, context: Map<String, Any?>? = null) {
        Log.e("$TAG_PREFIX-$className", formatErrorMessage(className, message, throwable, context), throwable)
    }
} 