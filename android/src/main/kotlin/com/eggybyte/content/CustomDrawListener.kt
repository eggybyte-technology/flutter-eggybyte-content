package com.eggybyte.content

import android.view.View
import android.view.ViewGroup
import com.bytedance.sdk.dp.IDPQuizHandler
import com.bytedance.sdk.dp.DPPageState
import com.bytedance.sdk.dp.IDPDrawListener
import io.flutter.plugin.common.EventChannel
import androidx.annotation.Nullable

/**
 * KDoc for CustomDrawListener
 * Extends com.bytedance.sdk.dp.listener.IDPDrawListener
 * Handles callbacks for Draw video widget events.
 */
class CustomDrawListener(
    private val eventSink: EventChannel.EventSink?,
    private val viewId: Int // Added viewId for consistency
) : IDPDrawListener() {

    companion object {
        private val CLASS_NAME = CustomDrawListener::class.java.simpleName
    }

    // Placeholder for how events might be sent.
    // private fun sendEventToFlutter(eventName: String, params: Map<String, Any?>?) { ... }

    // Method: onDPRefreshFinish()
    // Purpose: Called when the refresh operation is finished.
    // Parameters: None
    // Namespace: com.bytedance.sdk.dp.listener.IDPDrawListener
    override fun onDPRefreshFinish() {
        super.onDPRefreshFinish()
        PluginLogger.i(CLASS_NAME, "onDPRefreshFinish", mapOf("viewId" to viewId))
        eventSink?.success(mapOf("eventName" to "onDPRefreshFinish"))
    }

    // Method: onDPListDataChange(Map<String, Object> data)
    // Purpose: Called when the list data changes.
    // Parameters:
    //   data: Map<String, Object> - Data associated with the list change.
    // Namespace: com.bytedance.sdk.dp.listener.IDPDrawListener
    override fun onDPListDataChange(data: MutableMap<String, Any>?) {
        super.onDPListDataChange(data)
        PluginLogger.i(CLASS_NAME, "onDPListDataChange", mapOf("viewId" to viewId, "data" to data))
        eventSink?.success(mapOf("eventName" to "onDPListDataChange", "data" to data))
    }

    // Method: onDPSeekTo(int progress, long duration)
    // Purpose: Called when a seek operation occurs.
    // Parameters:
    //   progress: Int - The progress of the seek.
    //   duration: Long - The total duration.
    // Namespace: com.bytedance.sdk.dp.listener.IDPDrawListener
    override fun onDPSeekTo(progress: Int, duration: Long) {
        super.onDPSeekTo(progress, duration)
        PluginLogger.i(CLASS_NAME, "onDPSeekTo", mapOf("viewId" to viewId, "progress" to progress, "duration" to duration))
        eventSink?.success(mapOf("eventName" to "onDPSeekTo", "progress" to progress, "duration" to duration))
    }

    // Method: onDPPageChange(int position)
    // Purpose: Called when the page changes. (Deprecated)
    // Parameters:
    //   position: Int - The new page position.
    // Namespace: com.bytedance.sdk.dp.listener.IDPDrawListener
    @Deprecated("Use onDPPageChange(int position, Map<String, Object> data)")
    override fun onDPPageChange(position: Int) {
        super.onDPPageChange(position)
        PluginLogger.i(CLASS_NAME, "onDPPageChange (deprecated)", mapOf("viewId" to viewId, "position" to position))
        eventSink?.success(mapOf("eventName" to "onDPPageChange_deprecated", "position" to position))
    }

    // Method: onDPPageChange(int position, Map<String, Object> data)
    // Purpose: Called when the page changes, with additional data.
    // Parameters:
    //   position: Int - The new page position.
    //   data: Map<String, Object> - Additional data related to the page change.
    // Namespace: com.bytedance.sdk.dp.listener.IDPDrawListener
    override fun onDPPageChange(position: Int, data: MutableMap<String, Any>?) {
        super.onDPPageChange(position, data)
        PluginLogger.i(CLASS_NAME, "onDPPageChange", mapOf("viewId" to viewId, "position" to position, "data" to data))
        eventSink?.success(mapOf("eventName" to "onDPPageChange", "position" to position, "data" to data))
    }

    // Method: onDPVideoPlay(Map<String, Object> data)
    // Purpose: Called when video playback starts.
    // Parameters:
    //   data: Map<String, Object> - Data associated with the video play event.
    // Namespace: com.bytedance.sdk.dp.listener.IDPDrawListener
    override fun onDPVideoPlay(data: MutableMap<String, Any>?) { // Signature changed from (long) to (Map)
        super.onDPVideoPlay(data)
        PluginLogger.i(CLASS_NAME, "onDPVideoPlay", mapOf("viewId" to viewId, "data" to data))
        eventSink?.success(mapOf("eventName" to "onDPVideoPlay", "data" to data))
    }

    // Method: onDPVideoPause(Map<String, Object> data)
    // Purpose: Called when video playback is paused.
    // Parameters:
    //   data: Map<String, Object> - Data associated with the video pause event.
    // Namespace: com.bytedance.sdk.dp.listener.IDPDrawListener
    override fun onDPVideoPause(data: MutableMap<String, Any>?) {
        super.onDPVideoPause(data)
        PluginLogger.i(CLASS_NAME, "onDPVideoPause", mapOf("viewId" to viewId, "data" to data))
        eventSink?.success(mapOf("eventName" to "onDPVideoPause", "data" to data))
    }

    // Method: onDPVideoContinue(Map<String, Object> data)
    // Purpose: Called when video playback continues after a pause.
    // Parameters:
    //   data: Map<String, Object> - Data associated with the video continue event.
    // Namespace: com.bytedance.sdk.dp.listener.IDPDrawListener
    override fun onDPVideoContinue(data: MutableMap<String, Any>?) {
        super.onDPVideoContinue(data)
        PluginLogger.i(CLASS_NAME, "onDPVideoContinue", mapOf("viewId" to viewId, "data" to data))
        eventSink?.success(mapOf("eventName" to "onDPVideoContinue", "data" to data))
    }

    // Method: onDPVideoCompletion(Map<String, Object> data)
    // Purpose: Called when video playback completes.
    // Parameters:
    //   data: Map<String, Object> - Data associated with the video completion event.
    // Namespace: com.bytedance.sdk.dp.listener.IDPDrawListener
    override fun onDPVideoCompletion(data: MutableMap<String, Any>?) { // Signature changed from no-arg to (Map)
        super.onDPVideoCompletion(data)
        PluginLogger.i(CLASS_NAME, "onDPVideoCompletion", mapOf("viewId" to viewId, "data" to data))
        eventSink?.success(mapOf("eventName" to "onDPVideoCompletion", "data" to data))
    }

    // Method: onDPVideoOver(Map<String, Object> data)
    // Purpose: Called when the video is over (might be different from completion, e.g., error).
    // Parameters:
    //   data: Map<String, Object> - Data associated with the video over event.
    // Namespace: com.bytedance.sdk.dp.listener.IDPDrawListener
    override fun onDPVideoOver(data: MutableMap<String, Any>?) {
        super.onDPVideoOver(data)
        PluginLogger.i(CLASS_NAME, "onDPVideoOver", mapOf("viewId" to viewId, "data" to data))
        eventSink?.success(mapOf("eventName" to "onDPVideoOver", "data" to data))
    }
    
    // Method: onDPClose()
    // Purpose: Called when the close event is triggered on the Draw widget.
    // Parameters: None
    // Namespace: com.bytedance.sdk.dp.listener.IDPDrawListener
    override fun onDPClose() {
        super.onDPClose()
        PluginLogger.i(CLASS_NAME, "onDPClose", mapOf("viewId" to viewId))
        eventSink?.success(mapOf("eventName" to "onDPClose"))
    }

    // Method: onDPReportResult(boolean result)
    // Purpose: Called with the result of a report operation. (Deprecated)
    // Parameters:
    //   result: Boolean - The result of the report.
    // Namespace: com.bytedance.sdk.dp.listener.IDPDrawListener
    @Deprecated("Use onDPReportResult(boolean result, Map<String, Object> data)")
    override fun onDPReportResult(result: Boolean) {
        super.onDPReportResult(result)
        PluginLogger.i(CLASS_NAME, "onDPReportResult (deprecated)", mapOf("viewId" to viewId, "result" to result))
        eventSink?.success(mapOf("eventName" to "onDPReportResult_deprecated", "result" to result))
    }

    // Method: onDPReportResult(boolean result, Map<String, Object> data)
    // Purpose: Called with the result of a report operation, with additional data.
    // Parameters:
    //   result: Boolean - The result of the report.
    //   data: Map<String, Object> - Additional data related to the report result.
    // Namespace: com.bytedance.sdk.dp.listener.IDPDrawListener
    override fun onDPReportResult(result: Boolean, data: MutableMap<String, Any>?) {
        super.onDPReportResult(result, data)
        PluginLogger.i(CLASS_NAME, "onDPReportResult", mapOf("viewId" to viewId, "result" to result, "data" to data))
        eventSink?.success(mapOf("eventName" to "onDPReportResult", "result" to result, "data" to data))
    }

    // Method: onDPRequestStart(@Nullable Map<String, Object> params)
    // Purpose: Called when a data request starts.
    // Parameters:
    //   params: Map<String, Object> (Nullable) - Parameters of the request.
    // Namespace: com.bytedance.sdk.dp.listener.IDPDrawListener
    override fun onDPRequestStart(@Nullable params: MutableMap<String, Any>?) {
        super.onDPRequestStart(params)
        PluginLogger.i(CLASS_NAME, "onDPRequestStart", mapOf("viewId" to viewId, "params" to params))
        eventSink?.success(mapOf("eventName" to "onDPRequestStart", "params" to params))
    }
    
    // Method: onDPRequestFail(int errorCode, String errorMsg, @Nullable Map<String, Object> params)
    // Purpose: Called when a data request fails.
    // Parameters:
    //   errorCode: Int - The error code.
    //   errorMsg: String - The error message.
    //   params: Map<String, Object> (Nullable) - Parameters of the failed request.
    // Namespace: com.bytedance.sdk.dp.listener.IDPDrawListener
    override fun onDPRequestFail(errorCode: Int, errorMsg: String?, @Nullable params: MutableMap<String, Any>?) {
        super.onDPRequestFail(errorCode, errorMsg, params)
        PluginLogger.e(CLASS_NAME, "onDPRequestFail", context = mapOf("viewId" to viewId, "errorCode" to errorCode, "errorMsg" to errorMsg, "params" to params))
        eventSink?.success(mapOf("eventName" to "onDPRequestFail", "errorCode" to errorCode, "errorMsg" to errorMsg, "params" to params))
    }

    // Method: onDPRequestSuccess(List<Map<String, Object>> data)
    // Purpose: Called when a data request succeeds.
    // Parameters:
    //   data: List<Map<String, Object>> - The data received from the successful request.
    // Namespace: com.bytedance.sdk.dp.listener.IDPDrawListener
    override fun onDPRequestSuccess(data: MutableList<MutableMap<String, Any>>?) { // Changed to MutableList<MutableMap<...>>
        super.onDPRequestSuccess(data)
        PluginLogger.i(CLASS_NAME, "onDPRequestSuccess", mapOf("viewId" to viewId, "data_count" to (data?.size ?: 0)))
        eventSink?.success(mapOf("eventName" to "onDPRequestSuccess", "data" to data))
    }

    // Method: onDPClickAvatar(Map<String, Object> data)
    // Purpose: Called when the avatar is clicked.
    // Parameters:
    //   data: Map<String, Object> - Data associated with the avatar click event.
    // Namespace: com.bytedance.sdk.dp.listener.IDPDrawListener
    override fun onDPClickAvatar(data: MutableMap<String, Any>?) {
        super.onDPClickAvatar(data)
        PluginLogger.i(CLASS_NAME, "onDPClickAvatar", mapOf("viewId" to viewId, "data" to data))
        eventSink?.success(mapOf("eventName" to "onDPClickAvatar", "data" to data))
    }

    // Method: onDPClickAuthorName(Map<String, Object> data)
    // Purpose: Called when the author's name is clicked.
    // Parameters:
    //   data: Map<String, Object> - Data associated with the author name click event.
    // Namespace: com.bytedance.sdk.dp.listener.IDPDrawListener
    override fun onDPClickAuthorName(data: MutableMap<String, Any>?) {
        super.onDPClickAuthorName(data)
        PluginLogger.i(CLASS_NAME, "onDPClickAuthorName", mapOf("viewId" to viewId, "data" to data))
        eventSink?.success(mapOf("eventName" to "onDPClickAuthorName", "data" to data))
    }

    // Method: onDPClickComment(Map<String, Object> data)
    // Purpose: Called when the comment section/button is clicked.
    // Parameters:
    //   data: Map<String, Object> - Data associated with the comment click event.
    // Namespace: com.bytedance.sdk.dp.listener.IDPDrawListener
    override fun onDPClickComment(data: MutableMap<String, Any>?) {
        super.onDPClickComment(data)
        PluginLogger.i(CLASS_NAME, "onDPClickComment", mapOf("viewId" to viewId, "data" to data))
        eventSink?.success(mapOf("eventName" to "onDPClickComment", "data" to data))
    }

    // Method: onDPClickLike(boolean isLiked, Map<String, Object> data)
    // Purpose: Called when the like button is clicked.
    // Parameters:
    //   isLiked: Boolean - True if the item is now liked, false otherwise.
    //   data: Map<String, Object> - Data associated with the like click event.
    // Namespace: com.bytedance.sdk.dp.listener.IDPDrawListener
    override fun onDPClickLike(isLiked: Boolean, data: MutableMap<String, Any>?) {
        super.onDPClickLike(isLiked, data)
        PluginLogger.i(CLASS_NAME, "onDPClickLike", mapOf("viewId" to viewId, "isLiked" to isLiked, "data" to data))
        eventSink?.success(mapOf("eventName" to "onDPClickLike", "isLiked" to isLiked, "data" to data))
    }

    // Method: onDPClickShare(Map<String, Object> data)
    // Purpose: Called when the share button is clicked.
    // Parameters:
    //   data: Map<String, Object> - Data associated with the share click event.
    // Namespace: com.bytedance.sdk.dp.listener.IDPDrawListener
    override fun onDPClickShare(data: MutableMap<String, Any>?) {
        super.onDPClickShare(data)
        PluginLogger.i(CLASS_NAME, "onDPClickShare", mapOf("viewId" to viewId, "data" to data))
        eventSink?.success(mapOf("eventName" to "onDPClickShare", "data" to data))
    }

    // Method: onDPPageStateChanged(DPPageState state)
    // Purpose: Called when the page state changes (e.g., loading, success, error).
    // Parameters:
    //   state: DPPageState - The new page state.
    // Namespace: com.bytedance.sdk.dp.listener.IDPDrawListener
    override fun onDPPageStateChanged(state: DPPageState?) {
        super.onDPPageStateChanged(state)
        PluginLogger.i(CLASS_NAME, "onDPPageStateChanged", mapOf("viewId" to viewId, "state" to state?.name))
        eventSink?.success(mapOf("eventName" to "onDPPageStateChanged", "state" to state?.name))
    }

    // Method: onCreateQuizView(ViewGroup parent)
    // Purpose: Called to create a custom view for quizzes.
    // Parameters:
    //   parent: ViewGroup - The parent view group for the quiz view.
    // Returns: View? - The custom quiz view, or null for default behavior.
    // Namespace: com.bytedance.sdk.dp.listener.IDPDrawListener
    override fun onCreateQuizView(parent: ViewGroup?): View? {
        PluginLogger.d(CLASS_NAME, "onCreateQuizView called", mapOf("viewId" to viewId))
        // Event sending is typically not needed for view creation/binding methods unless specific info must go to Flutter.
        // For now, focusing on logging as per original file.
        return super.onCreateQuizView(parent)
    }

    // Method: onQuizBindData(View quizView, List<String> options, int answerIndex, int selectedIndex, IDPQuizHandler handler, Map<String, Object> data)
    // Purpose: Called to bind data to the quiz view.
    // Parameters:
    //   quizView: View - The quiz view.
    //   options: List<String> - The quiz options.
    //   answerIndex: Int - The correct answer index.
    //   selectedIndex: Int - The currently selected index by the user (-1 if none).
    //   handler: IDPQuizHandler - Handler to notify about quiz interactions.
    //   data: Map<String, Object> - Additional data for the quiz.
    // Namespace: com.bytedance.sdk.dp.listener.IDPDrawListener
    override fun onQuizBindData(quizView: View?, options: MutableList<String>?, answerIndex: Int, selectedIndex: Int, handler: IDPQuizHandler?, data: MutableMap<String, Any>?) {
        super.onQuizBindData(quizView, options, answerIndex, selectedIndex, handler, data)
        PluginLogger.d(CLASS_NAME, "onQuizBindData called", mapOf("viewId" to viewId, "options_count" to (options?.size ?: 0), "answerIndex" to answerIndex, "selectedIndex" to selectedIndex))
        // Event sending is typically not needed for view creation/binding methods.
    }

    // Method: onChannelTabChange(int tabIndex)
    // Purpose: Called when the channel tab changes.
    // Parameters:
    //   tabIndex: Int - The index of the newly selected tab.
    // Namespace: com.bytedance.sdk.dp.listener.IDPDrawListener
    override fun onChannelTabChange(tabIndex: Int) {
        super.onChannelTabChange(tabIndex)
        PluginLogger.i(CLASS_NAME, "onChannelTabChange", mapOf("viewId" to viewId, "tabIndex" to tabIndex))
        eventSink?.success(mapOf("eventName" to "onChannelTabChange", "tabIndex" to tabIndex))
    }
    
    // Method: onDurationChange(long duration)
    // Purpose: Called when the duration of the content (e.g., video) changes or becomes known.
    // Parameters:
    //   duration: long - The duration in milliseconds.
    // Namespace: com.bytedance.sdk.dp.listener.IDPDrawListener
    override fun onDurationChange(duration: Long) {
        super.onDurationChange(duration)
        PluginLogger.i(CLASS_NAME, "onDurationChange", mapOf("viewId" to viewId, "duration" to duration))
        eventSink?.success(mapOf("eventName" to "onDurationChange", "duration" to duration))
    }

    // Methods like onDPPause(), onDPResume(), onDPVideoError(String?) from the original CustomDrawListener.kt
    // are not part of the strict IDPDrawListener Java definition provided in the user query for update
    // (which includes onDPVideoPlay(Map), onDPVideoPause(Map), onDPVideoCompletion(Map) etc.).
    // They have been removed or replaced by the Map-based versions if applicable.
} 