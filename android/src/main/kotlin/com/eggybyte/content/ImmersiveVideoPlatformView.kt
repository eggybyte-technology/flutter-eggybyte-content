package com.eggybyte.content

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.bytedance.sdk.dp.DPSdk
import com.bytedance.sdk.dp.IDPWidget
import com.bytedance.sdk.dp.DPWidgetDrawParams
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.platform.PlatformView

class ImmersiveVideoPlatformView(
    private val context: Context,
    private val viewId: Int,
    private val creationParams: Map<String, Any?>?,
    private val activityProvider: () -> Activity?
) : PlatformView, EventChannel.StreamHandler {

    private var container: FrameLayout? = null
    private var idpWidget: IDPWidget? = null
    private var eventSink: EventChannel.EventSink? = null
    private lateinit var eventChannel: EventChannel

    companion object {
        private val CLASS_NAME = ImmersiveVideoPlatformView::class.java.simpleName
    }

    init {
        PluginLogger.d(CLASS_NAME, "Initializing ImmersiveVideoPlatformView", mapOf("viewId" to viewId))
        // The EventChannel name must be unique per view instance.
        eventChannel = EventChannel(EggybyteContentPlugin.flutterPluginBinding!!.binaryMessenger, "com.eggybyte/immersive_video_event_channel_" + viewId)
        eventChannel.setStreamHandler(this)

        createView()
    }

    private fun createView() {
        PluginLogger.d(CLASS_NAME, "createView called", mapOf("viewId" to viewId))
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        // It's good practice to use a dedicated container for the fragment.
        // Create a simple FrameLayout to host the fragment from IDPWidget.
        // Ensure it has an ID for fragment transactions.
        container = FrameLayout(context).apply {
            id = View.generateViewId() 
        }

        if (!DPSdk.isStartSuccess()) {
            PluginLogger.e(CLASS_NAME, "DPSdk not started. Cannot initialize Immersive Video (Draw) widget.", context = mapOf("viewId" to viewId))
            // Optionally, display an error message in the FrameLayout
            // val errorTextView = TextView(context)
            // errorTextView.text = "Error: DPSdk not started. Cannot display video."
            // container.addView(errorTextView)
            return // Don't proceed to create IDPWidget if SDK isn't ready
        }

        /**
         * Configures and obtains [DPWidgetDrawParams] for the immersive video (Draw) component.
         * Parameters are sourced from [creationParams] passed from Flutter.
         * Namespace for DPWidgetDrawParams: com.bytedance.sdk.dp.param
         * API Reference: jarr/classes/com/bytedance/sdk/dp/param/DPWidgetDrawParams.java
         */
        val drawParams = DPWidgetDrawParams.obtain().apply {
            PluginLogger.d(CLASS_NAME, "Configuring DPWidgetDrawParams", mapOf("viewId" to viewId, "creationParams" to creationParams))

            // adOffset: Int - Ad offset from bottom (in dp).
            // Default: 0
            val adOffsetParam = creationParams?.get("adOffset") as? Int
            adOffsetParam?.let { adOffset(it) }
            PluginLogger.d(CLASS_NAME, "Param: adOffset set to: $adOffsetParam", mapOf("viewId" to viewId))

            // hideClose: Boolean - Whether to hide the close button.
            // onClickListener: View.OnClickListener - Listener for the close button if shown.
            // Default: false (close button shown)
            val hideCloseParam = creationParams?.get("hideClose") as? Boolean ?: false
            hideClose(hideCloseParam, View.OnClickListener { 
                PluginLogger.i(CLASS_NAME, "Close button clicked for Immersive Video", mapOf("viewId" to viewId))
                // This click might need to be communicated to Flutter if custom action is needed.
                // For now, it primarily serves the SDK's internal close logic or can be tied to onDPClose.
                // If activityProvider().finish() was intended, that must be carefully managed.
            })
            PluginLogger.d(CLASS_NAME, "Param: hideClose set to: $hideCloseParam", mapOf("viewId" to viewId))

            // listener: IDPDrawListener - Listener for video stream events.
            // See CustomDrawListener for event forwarding to Flutter.
            listener(CustomDrawListener(eventSink, viewId))
            PluginLogger.d(CLASS_NAME, "Param: IDPDrawListener (CustomDrawListener) set.", mapOf("viewId" to viewId))

            // adListener: IDPAdListener - Listener for ad-related events.
            // See CustomAdListener for event forwarding to Flutter.
            adListener(CustomAdListener(eventSink, viewId))
            PluginLogger.d(CLASS_NAME, "Param: IDPAdListener (CustomAdListener) set.", mapOf("viewId" to viewId))

            // progressBarStyle: Int - Style for the progress bar.
            // Values: DPWidgetDrawParams.PROGRESS_BAR_STYLE_LIGHT (0), DPWidgetDrawParams.PROGRESS_BAR_STYLE_DARK (1)
            // Default: PROGRESS_BAR_STYLE_LIGHT (0)
            val progressBarStyleParam = creationParams?.get("progressBarStyle") as? Int ?: DPWidgetDrawParams.PROGRESS_BAR_STYLE_LIGHT
            progressBarStyle(progressBarStyleParam)
            PluginLogger.d(CLASS_NAME, "Param: progressBarStyle set to: $progressBarStyleParam", mapOf("viewId" to viewId))

            // bottomOffset: Int - Bottom offset for title, progress bar (in dp).
            // Default: 0
            val bottomOffsetParam = creationParams?.get("bottomOffset") as? Int
            bottomOffsetParam?.let { bottomOffset(it) }
            PluginLogger.d(CLASS_NAME, "Param: bottomOffset set to: $bottomOffsetParam", mapOf("viewId" to viewId))
            
            // titleTopMargin: Int - Top margin for the title (in dp). Optional.
            (creationParams?.get("titleTopMargin") as? Int)?.let { titleTopMargin(it) }

            // drawChannelType: Int - Channel type (SDK 3.2.0.0+). Optional.
            // Values: DPWidgetDrawParams.DRAW_CHANNEL_TYPE_RECOMMEND (0), DRAW_CHANNEL_TYPE_SEARCH (1), DRAW_CHANNEL_TYPE_FOLLOW (2)
            (creationParams?.get("drawChannelType") as? Int)?.let { drawChannelType(it) }

            // role: DPRole - Role for "watch together" feature (SDK 3.5.0.0+). Optional.
            // This parameter type is complex (enum/object) and needs careful mapping if exposed.
            // For now, not directly mapped from simple creationParams.
        }

        /**
         * Creates the Immersive Video (Draw) widget using DPSdk.factory().
         * API: IDPWidget createDraw(@Nullable DPWidgetDrawParams params)
         * Defined in: com.bytedance.sdk.dp.IDPFactory (reference jarr/classes/com/bytedance/sdk/dp/IDPFactory.java)
         */
        idpWidget = DPSdk.factory().createDraw(drawParams)
        PluginLogger.i(CLASS_NAME, "IDPWidget (Draw) created.", mapOf("viewId" to viewId, "idpWidgetNull" to (idpWidget == null)))

        val fragment2 = idpWidget?.fragment2 // fragment2 is android.app.Fragment

        if (fragment2 != null) {
            var unwrappedContext: Context = this.context
            while (unwrappedContext is android.content.ContextWrapper) {
                // We are looking for the base Activity context
                if (unwrappedContext is Activity) {
                    break 
                }
                unwrappedContext = (unwrappedContext as android.content.ContextWrapper).baseContext
            }
            
            val hostActivity = unwrappedContext as? Activity
            
            if (hostActivity != null) {
                PluginLogger.d(CLASS_NAME, "Adding android.app.Fragment (fragment2) to container using hostActivity context: ${hostActivity.javaClass.name}", mapOf("viewId" to viewId, "containerId" to container!!.id))
                try {
                    hostActivity.fragmentManager.beginTransaction()
                        .replace(container!!.id, fragment2) // Use android.app.FragmentManager
                        .commit()
                    PluginLogger.d(CLASS_NAME, "android.app.Fragment (fragment2) committed.", mapOf("viewId" to viewId))
                    
                    // Check fragment view after commit (might need a slight delay or run on UI thread post)
                    // For simplicity, checking immediately, but be aware of Fragment lifecycle.
                    if (fragment2.view == null) {
                        PluginLogger.e(CLASS_NAME, "android.app.Fragment (fragment2) view is null after transaction for viewId: $viewId", context = mapOf("viewId" to viewId))
                        val errorTextView = TextView(context).apply { text = "Error: Fragment2 view is null after commit." }
                        container?.addView(errorTextView)
                        eventSink?.error("FRAGMENT_VIEW_NULL", "Fragment2 view is null after commit.", null)
                        // Don't destroy idpWidget here as the fragment might still be managed by it.
                        // Consider if container should be cleared or if error text view is enough.
                    } else {
                        PluginLogger.i(CLASS_NAME, "ImmersiveVideoPlatformView created successfully with android.app.Fragment (fragment2). Fragment view is not null.", mapOf("viewId" to viewId))
                    }
                } catch (e: IllegalStateException) {
                    PluginLogger.e(CLASS_NAME, "Error committing android.app.Fragment (fragment2)", throwable = e, context = mapOf("viewId" to viewId))
                    val errorTextView = TextView(context).apply { text = "Error: Could not display video component (commit failed)." }
                    container?.addView(errorTextView)
                }
            } else {
                PluginLogger.e(CLASS_NAME, "Host Activity (after unwrapping) is null or not an Activity. Cannot add android.app.Fragment (fragment2).", 
                    context = mapOf("viewId" to viewId, "originalContext" to this.context.javaClass.name, "finalContextAttempt" to unwrappedContext.javaClass.name)
                )
                val errorTextView = TextView(this.context).apply { text = "Error: Invalid context for displaying this component." }
                container?.addView(errorTextView)                    
            }
        } else { // fragment2 is null
            PluginLogger.e(CLASS_NAME, "Failed to get android.app.Fragment (fragment2) from IDPWidget (Draw).", throwable = null, context = mapOf("viewId" to viewId))
            val errorTextView = TextView(context).apply { text = "Error: Could not obtain video component (fragment2 is null)." }
            container?.addView(errorTextView)
        }
    }

    override fun getView(): View? {
        PluginLogger.d(CLASS_NAME, "getView called for ImmersiveVideoPlatformView", mapOf("viewId" to viewId))
        return container
    }

    override fun dispose() {
        PluginLogger.i(CLASS_NAME, "Disposing ImmersiveVideoPlatformView", mapOf("viewId" to viewId))
        idpWidget?.destroy()
        idpWidget = null
        container = null
        eventChannel.setStreamHandler(null)
        eventSink = null
        PluginLogger.d(CLASS_NAME, "ImmersiveVideoPlatformView disposed.", mapOf("viewId" to viewId))
    }

    // EventChannel.StreamHandler methods
    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        PluginLogger.i(CLASS_NAME, "EventChannel onListen called for Immersive Video.", mapOf("viewId" to viewId))
        eventSink = events
        // Update listeners if they were created before eventSink was available
        // This is important if listeners are initialized in `init` or `createView` before `onListen` is called.
        if (idpWidget != null && DPSdk.isStartSuccess()) {
            // Re-setting listeners with the valid eventSink
            val existingDrawParams = idpWidget?.fragment2?.arguments?.getSerializable("draw_params") as? DPWidgetDrawParams
            existingDrawParams?.let {
                it.listener(CustomDrawListener(eventSink, viewId))
                it.adListener(CustomAdListener(eventSink, viewId))
                PluginLogger.d(CLASS_NAME, "Listeners updated with new EventSink post onListen.", mapOf("viewId" to viewId))
            } ?: {
                 // If params are not retrievable, this path implies listeners might not have the sink.
                 // This could happen if createDraw doesn't store params in fragment args.
                 // The initial setup in createView() should ideally use this sink once available.
                 // For robust re-initialization, might need to re-create or update widget if SDK allows.
                 // Fallback: If listeners were already created with a potentially null sink, they will start sending events now.
                 PluginLogger.w(CLASS_NAME, "Could not re-set listeners after onListen as DPWidgetDrawParams not found in fragment args.", mapOf("viewId" to viewId))
            }
        }
    }

    override fun onCancel(arguments: Any?) {
        PluginLogger.i(CLASS_NAME, "EventChannel onCancel called for Immersive Video.", mapOf("viewId" to viewId))
        eventSink = null
    }
} 