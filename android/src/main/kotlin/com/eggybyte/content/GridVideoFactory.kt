package com.eggybyte.content

import android.app.Activity
import android.content.Context
import io.flutter.plugin.common.StandardMessageCodec
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory

class GridVideoFactory(
    private val activityProvider: () -> Activity?
) : PlatformViewFactory(StandardMessageCodec.INSTANCE) {

    companion object {
        private val CLASS_NAME = GridVideoFactory::class.java.simpleName
    }

    override fun create(context: Context?, viewId: Int, args: Any?): PlatformView {
        PluginLogger.d(CLASS_NAME, "Creating GridVideoPlatformView", mapOf("viewId" to viewId, "args" to args))
        val creationParams = args as? Map<String, Any?>
        if (context == null) {
            val errorMsg = "Context is null, cannot create GridVideoPlatformView."
            PluginLogger.e(CLASS_NAME, errorMsg)
            throw IllegalStateException(errorMsg)
        }
        return GridVideoPlatformView(context, viewId, creationParams, activityProvider)
    }
} 