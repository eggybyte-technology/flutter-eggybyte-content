package com.eggybyte.content

import android.view.View
import android.view.ViewGroup
import com.bytedance.sdk.dp.IDPGridListener
import com.bytedance.sdk.dp.IDPQuizHandler
import com.bytedance.sdk.dp.DPPageState
import io.flutter.plugin.common.EventChannel
import androidx.annotation.Nullable

/**
 * KDoc for CustomGridListener
 * Extends com.bytedance.sdk.dp.listener.IDPGridListener.
 * Handles callbacks for Grid video widget events. Inherits from IDPDrawListener methods as well.
 */
class CustomGridListener(
    private val eventSink: EventChannel.EventSink?,
    private val viewId: Int // Added viewId for consistency
) : IDPGridListener() {

    companion object {
        private val CLASS_NAME = CustomGridListener::class.java.simpleName
    }

    // Placeholder for how events might be sent.
    // private fun sendEventToFlutter(eventName: String, params: Map<String, Any?>?) { ... }

    // Methods from IDPDrawListener are inherited. 
    // Override them here if grid-specific behavior is needed for those events,
    // otherwise, the behavior from a CustomDrawListener base (if CustomGridListener were to extend that directly)
    // or the default SDK IDPDrawListener behavior (if IDPGridListener itself calls super) would apply.
    // For clarity, assuming IDPGridListener properly calls super for IDPDrawListener methods.

    // Method: onDPRefreshFinish()
    // Purpose: Called when the grid refresh operation is finished. (Inherited from IDPDrawListener, can be overridden for specific grid behavior)
    // Parameters: None
    // Namespace: com.bytedance.sdk.dp.listener.IDPGridListener (behavior specific to grid if overridden)
    override fun onDPRefreshFinish() {
        super.onDPRefreshFinish() // Calls IDPDrawListener's onDPRefreshFinish via IDPGridListener
        PluginLogger.i(CLASS_NAME, "onDPRefreshFinish (Grid)", mapOf("viewId" to viewId))
        eventSink?.success(mapOf("eventName" to "onDPRefreshFinish"))
    }

    // Method: onDPGridItemClick(Map<String, Object> itemInfo)
    // Purpose: Called when an item in the grid is clicked.
    // Parameters:
    //   itemInfo: Map<String, Object> - Information about the clicked item.
    // Namespace: com.bytedance.sdk.dp.listener.IDPGridListener
    override fun onDPGridItemClick(itemInfo: MutableMap<String, Any>?) {
        super.onDPGridItemClick(itemInfo)
        PluginLogger.i(CLASS_NAME, "onDPGridItemClick", mapOf("viewId" to viewId, "itemInfo_keys" to itemInfo?.keys?.joinToString()))
        eventSink?.success(mapOf("eventName" to "onDPGridItemClick", "itemInfo" to itemInfo))
    }
    
    // Method: onDPClientShow(@Nullable Map<String, Object> data)
    // Purpose: Called when the client view for the grid is shown.
    // Parameters:
    //   data: Map<String, Object> (Nullable) - Data associated with the client show event.
    // Namespace: com.bytedance.sdk.dp.listener.IDPGridListener
    override fun onDPClientShow(@Nullable data: MutableMap<String, Any>?) {
        super.onDPClientShow(data)
        PluginLogger.i(CLASS_NAME, "onDPClientShow", mapOf("viewId" to viewId, "data_keys" to data?.keys?.joinToString()))
        eventSink?.success(mapOf("eventName" to "onDPClientShow", "data" to data))
    }

    // All other methods from IDPDrawListener are inherited via IDPGridListener.
    // Refer to CustomDrawListener for the KDocs and signatures of those inherited methods.
    // Only override them here if specific grid behavior is required for an inherited IDPDrawListener event.

    // Inherited methods from IDPDrawListener (via IDPGridListener)
    // These must be overridden to ensure events are sent for Grid views too.

    override fun onDPListDataChange(data: MutableMap<String, Any>?) {
        super.onDPListDataChange(data)
        PluginLogger.i(CLASS_NAME, "onDPListDataChange (Grid)", mapOf("viewId" to viewId, "data" to data))
        eventSink?.success(mapOf("eventName" to "onDPListDataChange", "data" to data))
    }

    override fun onDPSeekTo(progress: Int, duration: Long) {
        super.onDPSeekTo(progress, duration)
        PluginLogger.i(CLASS_NAME, "onDPSeekTo (Grid)", mapOf("viewId" to viewId, "progress" to progress, "duration" to duration))
        eventSink?.success(mapOf("eventName" to "onDPSeekTo", "progress" to progress, "duration" to duration))
    }

    @Deprecated("Use onDPPageChange(int position, Map<String, Object> data)")
    override fun onDPPageChange(position: Int) {
        super.onDPPageChange(position)
        PluginLogger.i(CLASS_NAME, "onDPPageChange (Grid, deprecated)", mapOf("viewId" to viewId, "position" to position))
        eventSink?.success(mapOf("eventName" to "onDPPageChange_deprecated", "position" to position))
    }

    override fun onDPPageChange(position: Int, data: MutableMap<String, Any>?) {
        super.onDPPageChange(position, data)
        PluginLogger.i(CLASS_NAME, "onDPPageChange (Grid)", mapOf("viewId" to viewId, "position" to position, "data" to data))
        eventSink?.success(mapOf("eventName" to "onDPPageChange", "position" to position, "data" to data))
    }

    override fun onDPVideoPlay(data: MutableMap<String, Any>?) {
        super.onDPVideoPlay(data)
        PluginLogger.i(CLASS_NAME, "onDPVideoPlay (Grid)", mapOf("viewId" to viewId, "data" to data))
        eventSink?.success(mapOf("eventName" to "onDPVideoPlay", "data" to data))
    }

    override fun onDPVideoPause(data: MutableMap<String, Any>?) {
        super.onDPVideoPause(data)
        PluginLogger.i(CLASS_NAME, "onDPVideoPause (Grid)", mapOf("viewId" to viewId, "data" to data))
        eventSink?.success(mapOf("eventName" to "onDPVideoPause", "data" to data))
    }

    override fun onDPVideoContinue(data: MutableMap<String, Any>?) {
        super.onDPVideoContinue(data)
        PluginLogger.i(CLASS_NAME, "onDPVideoContinue (Grid)", mapOf("viewId" to viewId, "data" to data))
        eventSink?.success(mapOf("eventName" to "onDPVideoContinue", "data" to data))
    }

    override fun onDPVideoCompletion(data: MutableMap<String, Any>?) {
        super.onDPVideoCompletion(data)
        PluginLogger.i(CLASS_NAME, "onDPVideoCompletion (Grid)", mapOf("viewId" to viewId, "data" to data))
        eventSink?.success(mapOf("eventName" to "onDPVideoCompletion", "data" to data))
    }

    override fun onDPVideoOver(data: MutableMap<String, Any>?) {
        super.onDPVideoOver(data)
        PluginLogger.i(CLASS_NAME, "onDPVideoOver (Grid)", mapOf("viewId" to viewId, "data" to data))
        eventSink?.success(mapOf("eventName" to "onDPVideoOver", "data" to data))
    }
    
    override fun onDPClose() {
        super.onDPClose()
        PluginLogger.i(CLASS_NAME, "onDPClose (Grid)", mapOf("viewId" to viewId))
        eventSink?.success(mapOf("eventName" to "onDPClose"))
    }

    @Deprecated("Use onDPReportResult(boolean result, Map<String, Object> data)")
    override fun onDPReportResult(result: Boolean) {
        super.onDPReportResult(result)
        PluginLogger.i(CLASS_NAME, "onDPReportResult (Grid, deprecated)", mapOf("viewId" to viewId, "result" to result))
        eventSink?.success(mapOf("eventName" to "onDPReportResult_deprecated", "result" to result))
    }

    override fun onDPReportResult(result: Boolean, data: MutableMap<String, Any>?) {
        super.onDPReportResult(result, data)
        PluginLogger.i(CLASS_NAME, "onDPReportResult (Grid)", mapOf("viewId" to viewId, "result" to result, "data" to data))
        eventSink?.success(mapOf("eventName" to "onDPReportResult", "result" to result, "data" to data))
    }

    override fun onDPRequestStart(@Nullable params: MutableMap<String, Any>?) {
        super.onDPRequestStart(params)
        PluginLogger.i(CLASS_NAME, "onDPRequestStart (Grid)", mapOf("viewId" to viewId, "params" to params))
        eventSink?.success(mapOf("eventName" to "onDPRequestStart", "params" to params))
    }
    
    override fun onDPRequestFail(errorCode: Int, errorMsg: String?, @Nullable params: MutableMap<String, Any>?) {
        super.onDPRequestFail(errorCode, errorMsg, params)
        PluginLogger.e(CLASS_NAME, "onDPRequestFail (Grid)", context = mapOf("viewId" to viewId, "errorCode" to errorCode, "errorMsg" to errorMsg, "params" to params))
        eventSink?.success(mapOf("eventName" to "onDPRequestFail", "errorCode" to errorCode, "errorMsg" to errorMsg, "params" to params))
    }

    override fun onDPRequestSuccess(data: MutableList<MutableMap<String, Any>>?) {
        super.onDPRequestSuccess(data)
        PluginLogger.i(CLASS_NAME, "onDPRequestSuccess (Grid)", mapOf("viewId" to viewId, "data_count" to (data?.size ?: 0)))
        eventSink?.success(mapOf("eventName" to "onDPRequestSuccess", "data" to data))
    }

    override fun onDPClickAvatar(data: MutableMap<String, Any>?) {
        super.onDPClickAvatar(data)
        PluginLogger.i(CLASS_NAME, "onDPClickAvatar (Grid)", mapOf("viewId" to viewId, "data" to data))
        eventSink?.success(mapOf("eventName" to "onDPClickAvatar", "data" to data))
    }

    override fun onDPClickAuthorName(data: MutableMap<String, Any>?) {
        super.onDPClickAuthorName(data)
        PluginLogger.i(CLASS_NAME, "onDPClickAuthorName (Grid)", mapOf("viewId" to viewId, "data" to data))
        eventSink?.success(mapOf("eventName" to "onDPClickAuthorName", "data" to data))
    }

    override fun onDPClickComment(data: MutableMap<String, Any>?) {
        super.onDPClickComment(data)
        PluginLogger.i(CLASS_NAME, "onDPClickComment (Grid)", mapOf("viewId" to viewId, "data" to data))
        eventSink?.success(mapOf("eventName" to "onDPClickComment", "data" to data))
    }

    override fun onDPClickLike(isLiked: Boolean, data: MutableMap<String, Any>?) {
        super.onDPClickLike(isLiked, data)
        PluginLogger.i(CLASS_NAME, "onDPClickLike (Grid)", mapOf("viewId" to viewId, "isLiked" to isLiked, "data" to data))
        eventSink?.success(mapOf("eventName" to "onDPClickLike", "isLiked" to isLiked, "data" to data))
    }

    override fun onDPClickShare(data: MutableMap<String, Any>?) {
        super.onDPClickShare(data)
        PluginLogger.i(CLASS_NAME, "onDPClickShare (Grid)", mapOf("viewId" to viewId, "data" to data))
        eventSink?.success(mapOf("eventName" to "onDPClickShare", "data" to data))
    }

    override fun onDPPageStateChanged(state: DPPageState?) {
        super.onDPPageStateChanged(state)
        PluginLogger.i(CLASS_NAME, "onDPPageStateChanged (Grid)", mapOf("viewId" to viewId, "state" to state?.name))
        eventSink?.success(mapOf("eventName" to "onDPPageStateChanged", "state" to state?.name))
    }

    override fun onCreateQuizView(parent: ViewGroup?): View? {
        PluginLogger.d(CLASS_NAME, "onCreateQuizView (Grid) called", mapOf("viewId" to viewId))
        return super.onCreateQuizView(parent)
    }

    override fun onQuizBindData(quizView: View?, options: MutableList<String>?, answerIndex: Int, selectedIndex: Int, handler: IDPQuizHandler?, data: MutableMap<String, Any>?) {
        super.onQuizBindData(quizView, options, answerIndex, selectedIndex, handler, data)
        PluginLogger.d(CLASS_NAME, "onQuizBindData (Grid) called", mapOf("viewId" to viewId, "options_count" to (options?.size ?: 0), "answerIndex" to answerIndex, "selectedIndex" to selectedIndex))
    }

    override fun onChannelTabChange(tabIndex: Int) {
        super.onChannelTabChange(tabIndex)
        PluginLogger.i(CLASS_NAME, "onChannelTabChange (Grid)", mapOf("viewId" to viewId, "tabIndex" to tabIndex))
        eventSink?.success(mapOf("eventName" to "onChannelTabChange", "tabIndex" to tabIndex))
    }
    
    override fun onDurationChange(duration: Long) {
        super.onDurationChange(duration)
        PluginLogger.i(CLASS_NAME, "onDurationChange (Grid)", mapOf("viewId" to viewId, "duration" to duration))
        eventSink?.success(mapOf("eventName" to "onDurationChange", "duration" to duration))
    }
} 