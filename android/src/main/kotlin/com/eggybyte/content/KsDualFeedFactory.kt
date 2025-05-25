package com.eggybyte.content

import android.app.Activity
import android.content.Context
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.StandardMessageCodec
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory

/**
 * Factory class for creating [KsDualFeedPlatformView] instances.
 *
 * This factory is registered with Flutter's platform view registry to enable
 * the embedding of Kuaishou dual feed views within the Flutter UI.
 *
 * @property activityProvider A lambda function that provides the current [Activity].
 *                          This is crucial for accessing context and fragment managers.
 * @property messenger The [BinaryMessenger] used for communication between Flutter and native code,
 *                     specifically for creating the [MethodChannel] for event forwarding.
 */
class KsDualFeedFactory(
    private val activityProvider: () -> Activity?,
    private val messenger: BinaryMessenger
) : PlatformViewFactory(StandardMessageCodec.INSTANCE) {

    companion object {
        /**
         * Tag used for logging within the [KsDualFeedFactory] class.
         */
        private val CLASS_NAME = KsDualFeedFactory::class.java.simpleName
        /**
         * Name of the method channel used for KS feed events.
         * Must match the channel name used on the Dart side for handling these events.
         */
        private const val KS_FEED_EVENT_CHANNEL_NAME = "eggybyte_content"
    }

    /**
     * Creates a new [KsDualFeedPlatformView].
     *
     * This method is called by Flutter when a new platform view of the registered type
     * needs to be created.
     *
     * @param context The Android [Context] for the view. Can be null, in which case the activity's context is used.
     * @param viewId The unique identifier for this platform view instance.
     * @param args Optional arguments passed from Flutter during view creation. Expected to be a [Map].
     *             These arguments can include parameters like 'posId' for Kuaishou.
     * @return A new instance of [KsDualFeedPlatformView].
     * @throws IllegalStateException if the application context cannot be obtained.
     */
    override fun create(context: Context?, viewId: Int, args: Any?): PlatformView {
        PluginLogger.d(CLASS_NAME, "Creating KsDualFeedPlatformView", mapOf("viewId" to viewId, "args" to args))
        val creationParams = args as? Map<String, Any> // Cast args to the expected Map type
        
        // Ensure context is not null; fallback to activity's applicationContext if the provided context is null.
        val appContext = context ?: activityProvider()?.applicationContext
        
        if (appContext == null) {
            val errorMsg = "ApplicationContext is null, cannot create KsDualFeedPlatformView."
            PluginLogger.e(CLASS_NAME, errorMsg)
            throw IllegalStateException(errorMsg)
        }

        // Create a MethodChannel instance for this view to send events to Dart.
        val eventMethodChannel = MethodChannel(messenger, KS_FEED_EVENT_CHANNEL_NAME)
        PluginLogger.d(CLASS_NAME, "Created event MethodChannel for KsDualFeedPlatformView", mapOf("viewId" to viewId, "channelName" to KS_FEED_EVENT_CHANNEL_NAME))

        return KsDualFeedPlatformView(appContext, activityProvider, viewId, creationParams, eventMethodChannel)
    }
} 