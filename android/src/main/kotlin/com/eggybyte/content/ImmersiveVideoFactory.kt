package com.eggybyte.content

import android.app.Activity
import android.content.Context
import io.flutter.plugin.common.StandardMessageCodec
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory

class ImmersiveVideoFactory(
    private val activityProvider: () -> Activity? // Provides access to the current activity if needed by the view
) : PlatformViewFactory(StandardMessageCodec.INSTANCE) {

    companion object {
        private val CLASS_NAME = ImmersiveVideoFactory::class.java.simpleName
    }

    override fun create(context: Context?, viewId: Int, args: Any?): PlatformView {
        PluginLogger.d(CLASS_NAME, "Creating ImmersiveVideoPlatformView", mapOf("viewId" to viewId, "args" to args))
        val creationParams = args as? Map<String, Any?>
        if (context == null) {
            val errorMsg = "Context is null, cannot create ImmersiveVideoPlatformView."
            PluginLogger.e(CLASS_NAME, errorMsg)
            throw IllegalStateException(errorMsg)
        }
        // It's important that ImmersiveVideoPlatformView has access to an Activity context
        // if the underlying SDK components require it (e.g., for dialogs, specific UI features).
        // The activityProvider helps in safely accessing the current activity.
        return ImmersiveVideoPlatformView(context, viewId, creationParams, activityProvider)
    }
} 