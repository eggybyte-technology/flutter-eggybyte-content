package com.eggybyte.content

import android.app.Activity
import android.content.Context
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.StandardMessageCodec
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory

class KsDualFeedFactory(
    private val activityProvider: () -> Activity?,
    private val messenger: BinaryMessenger 
) : PlatformViewFactory(StandardMessageCodec.INSTANCE) {

    companion object {
        private val CLASS_NAME = KsDualFeedFactory::class.java.simpleName
    }

    override fun create(context: Context?, viewId: Int, args: Any?): PlatformView {
        PluginLogger.d(CLASS_NAME, "Creating KsDualFeedPlatformView", mapOf("viewId" to viewId, "args" to args))
        val creationParams = args as? Map<String, Any>
        // Ensure context is not null, fallback to activity's applicationContext if needed
        val appContext = context ?: activityProvider()?.applicationContext
        if (appContext == null) {
            PluginLogger.e(CLASS_NAME, "ApplicationContext is null, cannot create KsDualFeedPlatformView.")
            // Consider throwing an exception or returning an error view
            throw IllegalStateException("ApplicationContext is null, cannot create KsDualFeedPlatformView.")
        }
        return KsDualFeedPlatformView(appContext, activityProvider, viewId, creationParams, messenger)
    }
} 