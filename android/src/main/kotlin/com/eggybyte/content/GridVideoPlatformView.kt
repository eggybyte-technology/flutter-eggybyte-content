package com.eggybyte.content

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.bytedance.sdk.dp.DPSdk
import com.bytedance.sdk.dp.IDPWidget
import com.bytedance.sdk.dp.DPWidgetGridParams
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.platform.PlatformView

class GridVideoPlatformView(
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
        private val CLASS_NAME = GridVideoPlatformView::class.java.simpleName
    }

    init {
        PluginLogger.d(CLASS_NAME, "Initializing GridVideoPlatformView", mapOf("viewId" to viewId))
        eventChannel = EventChannel(EggybyteContentPlugin.flutterPluginBinding!!.binaryMessenger, "com.eggybyte/grid_video_event_channel_" + viewId)
        eventChannel.setStreamHandler(this)
        createView()
    }

    private fun createView() {
        PluginLogger.d(CLASS_NAME, "createView called for Grid Video", mapOf("viewId" to viewId))
        container = FrameLayout(context).apply {
            id = View.generateViewId()
        }

        if (!DPSdk.isStartSuccess()) {
            PluginLogger.e(CLASS_NAME, "DPSdk not started. Cannot init Grid widget for viewId: $viewId", context = mapOf("viewId" to viewId))
            val errorTextView = TextView(context).apply { text = "Error: DPSdk not started." }
            container?.addView(errorTextView)
            // Notify Flutter about the error
            eventSink?.error("SDK_NOT_STARTED", "DPSdk not started. Cannot init Grid widget.", null)
            return
        }

        try {
            val cardStyleParam = creationParams?.get("cardStyle") as? Int ?: DPWidgetGridParams.CARD_NORMAL_STYLE
            val sceneParam = creationParams?.get("scene") as? String
            val enableRefreshParam = creationParams?.get("enableRefresh") as? Boolean ?: true

            PluginLogger.d(CLASS_NAME, "GridWidget Parameters for viewId: $viewId",
                mapOf(
                    "cardStyle" to cardStyleParam,
                    "scene" to sceneParam,
                    "enableRefresh" to enableRefreshParam,
                    "viewId" to viewId
                )
            )

            val gridParams = DPWidgetGridParams.obtain().apply {
                cardStyle(cardStyleParam)
                listener(CustomGridListener(eventSink, viewId))
                adListener(CustomAdListener(eventSink, viewId))

                if (sceneParam != null) {
                    scene(sceneParam)
                }
                enableRefresh(enableRefreshParam)
            }

            PluginLogger.i(CLASS_NAME, "Attempting to create DoubleFeed widget for viewId: $viewId")
            idpWidget = DPSdk.factory().createDoubleFeed(gridParams)

            if (idpWidget == null) {
                PluginLogger.e(CLASS_NAME, "DPSdk.factory().createDoubleFeed(gridParams) returned null for viewId: $viewId", context = mapOf("viewId" to viewId))
                val errorTextView = TextView(context).apply { text = "Error: Could not create DoubleFeed widget." }
                container?.addView(errorTextView)
                eventSink?.error("WIDGET_CREATION_FAILED", "createDoubleFeed returned null.", null)
                return
            }

            val fragment2 = idpWidget?.fragment2
            if (fragment2 == null) {
                PluginLogger.e(CLASS_NAME, "dpWidget.getFragment2() returned null for viewId: $viewId", context = mapOf("viewId" to viewId))
                val errorTextView = TextView(context).apply { text = "Error: Could not get fragment2 from DoubleFeed widget." }
                container?.addView(errorTextView)
                eventSink?.error("FRAGMENT_NULL", "getFragment2 returned null.", null)
                idpWidget?.destroy()
                idpWidget = null
                return
            }
            
            PluginLogger.d(CLASS_NAME, "android.app.Fragment (fragment2) obtained: ${fragment2.javaClass.simpleName} for viewId: $viewId")

            var unwrappedContext: Context = this.context
            while (unwrappedContext is android.content.ContextWrapper) {
                 if (unwrappedContext is Activity) { // We need any Activity for android.app.FragmentManager
                    break 
                }
                unwrappedContext = (unwrappedContext as android.content.ContextWrapper).baseContext
            }
            val hostActivity = unwrappedContext as? Activity

            if (hostActivity != null) {
                PluginLogger.d(CLASS_NAME, "Adding android.app.Fragment (fragment2) to container for Grid Video using hostActivity context: ${hostActivity.javaClass.name}", mapOf("viewId" to viewId, "containerId" to container!!.id))
                try {
                    hostActivity.fragmentManager.beginTransaction() // Use android.app.FragmentManager
                        .replace(container!!.id, fragment2)
                        .commit()
                    PluginLogger.d(CLASS_NAME, "android.app.Fragment (fragment2) committed for Grid Video.", mapOf("viewId" to viewId))

                    if (fragment2.view == null) {
                        PluginLogger.e(CLASS_NAME, "android.app.Fragment (fragment2) view is null after transaction for viewId: $viewId", context = mapOf("viewId" to viewId))
                        val errorTextView = TextView(context).apply { text = "Error: Fragment2 view is null after commit." }
                        container?.addView(errorTextView)
                        eventSink?.error("FRAGMENT_VIEW_NULL", "Fragment2 view is null after commit.", null)
                        // Consider if idpWidget needs destroy or container clearing
                    } else {
                        PluginLogger.i(CLASS_NAME, "GridVideoPlatformView created successfully with android.app.Fragment (fragment2). Fragment view is not null.", mapOf("viewId" to viewId))
                    }
                } catch (e: IllegalStateException) {
                    PluginLogger.e(CLASS_NAME, "Error committing android.app.Fragment (fragment2) for Grid Video", throwable = e, context = mapOf("viewId" to viewId))
                    val errorTextView = TextView(context).apply { text = "Error: Could not display grid component (commit failed)." }
                    container?.addView(errorTextView)
                }
            } else {
                PluginLogger.e(CLASS_NAME, "Host Activity (after unwrapping) is null or not an Activity. Cannot add Grid fragment2.", 
                    context = mapOf("viewId" to viewId, "originalContext" to this.context.javaClass.name, "finalContextAttempt" to unwrappedContext.javaClass.name))
                val errorTextView = TextView(this.context).apply { text = "Error: Activity context is invalid for displaying this component." }
                container?.addView(errorTextView)
            }
        } catch (e: Exception) {
            PluginLogger.e(CLASS_NAME, "Error creating GridVideoPlatformView for viewId: $viewId", throwable = e, context = mapOf("viewId" to viewId))
            val errorTextView = TextView(context).apply { text = "Error: ${e.message}" }
            container?.addView(errorTextView)
            eventSink?.error("CREATION_EXCEPTION", e.message, e.stackTraceToString())
            if (idpWidget != null) {
                idpWidget?.destroy()
                idpWidget = null
            }
        }
    }

    override fun getView(): View? {
        PluginLogger.d(CLASS_NAME, "getView called for GridVideoPlatformView", mapOf("viewId" to viewId))
        return container
    }

    override fun dispose() {
        PluginLogger.i(CLASS_NAME, "Disposing GridVideoPlatformView", mapOf("viewId" to viewId))
        idpWidget?.destroy()
        idpWidget = null
        container = null
        eventChannel.setStreamHandler(null)
        eventSink = null
        PluginLogger.d(CLASS_NAME, "GridVideoPlatformView disposed.", mapOf("viewId" to viewId))
    }

    // EventChannel.StreamHandler methods
    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        PluginLogger.i(CLASS_NAME, "EventChannel onListen called for Grid Video.", mapOf("viewId" to viewId))
        eventSink = events
        // Update listeners with the valid eventSink if they were created before it was available.
        if (idpWidget != null && DPSdk.isStartSuccess()) {
            val existingGridParams = idpWidget?.fragment2?.arguments?.getSerializable("grid_params") as? DPWidgetGridParams
             existingGridParams?.let {
                it.listener(CustomGridListener(eventSink, viewId))
                it.adListener(CustomAdListener(eventSink, viewId))
                PluginLogger.d(CLASS_NAME, "Grid Listeners updated with new EventSink post onListen.", mapOf("viewId" to viewId))
            } ?: {
                 PluginLogger.w(CLASS_NAME, "Could not re-set grid listeners after onListen as DPWidgetGridParams not found in fragment2 args.", mapOf("viewId" to viewId))
            }
        }
    }

    override fun onCancel(arguments: Any?) {
        PluginLogger.i(CLASS_NAME, "EventChannel onCancel called for Grid Video.", mapOf("viewId" to viewId))
        eventSink = null
    }
} 