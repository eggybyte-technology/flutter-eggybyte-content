package com.eggybyte.content

import com.bytedance.sdk.dp.IDPAdListener
import io.flutter.plugin.common.EventChannel

/**
 * KDoc for CustomAdListener
 * Extends com.bytedance.sdk.dp.listener.IDPAdListener
 * Handles callbacks for ad-related events.
 */
class CustomAdListener(
    private val eventSink: EventChannel.EventSink?,
    private val viewId: Int // Added viewId for consistency with PlatformView instantiation
) : IDPAdListener() {

    companion object {
        private val CLASS_NAME = CustomAdListener::class.java.simpleName
    }

    /**
     * Called when an ad request is made.
     * @param params Parameters associated with the ad request.
     * Namespace: com.bytedance.sdk.dp.listener.IDPAdListener
     */
    override fun onDPAdRequest(params: MutableMap<String, Any>?) {
        super.onDPAdRequest(params)
        PluginLogger.i(CLASS_NAME, "onDPAdRequest", mapOf("viewId" to viewId, "params" to params))
        eventSink?.success(mapOf("eventName" to "onDPAdRequest", "params" to params))
    }

    /**
     * Called when an ad request succeeds.
     * @param params Parameters associated with the successful ad request.
     * Namespace: com.bytedance.sdk.dp.listener.IDPAdListener
     */
    override fun onDPAdRequestSuccess(params: MutableMap<String, Any>?) {
        super.onDPAdRequestSuccess(params)
        PluginLogger.i(CLASS_NAME, "onDPAdRequestSuccess", mapOf("viewId" to viewId, "params" to params))
        eventSink?.success(mapOf("eventName" to "onDPAdRequestSuccess", "params" to params))
    }

    /**
     * Called when an ad request fails.
     * @param errorCode The error code.
     * @param errorMsg The error message.
     * @param params Parameters associated with the failed ad request.
     * Namespace: com.bytedance.sdk.dp.listener.IDPAdListener
     */
    override fun onDPAdRequestFail(errorCode: Int, errorMsg: String?, params: MutableMap<String, Any>?) {
        super.onDPAdRequestFail(errorCode, errorMsg, params)
        PluginLogger.e(CLASS_NAME, "onDPAdRequestFail", context = mapOf("viewId" to viewId, "errorCode" to errorCode, "errorMsg" to errorMsg, "params" to params))
        eventSink?.success(mapOf("eventName" to "onDPAdRequestFail", "errorCode" to errorCode, "errorMsg" to errorMsg, "params" to params))
    }

    /**
     * Called when the ad fill fails.
     * @param params Parameters associated with the ad fill failure.
     * Namespace: com.bytedance.sdk.dp.listener.IDPAdListener
     */
    override fun onDPAdFillFail(params: MutableMap<String, Any>?) {
        super.onDPAdFillFail(params)
        PluginLogger.w(CLASS_NAME, "onDPAdFillFail", mapOf("viewId" to viewId, "params" to params))
        eventSink?.success(mapOf("eventName" to "onDPAdFillFail", "params" to params))
    }

    /**
     * Called when an ad is shown.
     * @param params Parameters associated with the ad show event.
     * Namespace: com.bytedance.sdk.dp.listener.IDPAdListener
     */
    override fun onDPAdShow(params: MutableMap<String, Any>?) {
        super.onDPAdShow(params)
        PluginLogger.i(CLASS_NAME, "onDPAdShow", mapOf("viewId" to viewId, "params" to params))
        eventSink?.success(mapOf("eventName" to "onDPAdShow", "params" to params))
    }

    /**
     * Called when ad video playback starts.
     * @param params Parameters associated with the ad play start event.
     * Namespace: com.bytedance.sdk.dp.listener.IDPAdListener
     */
    override fun onDPAdPlayStart(params: MutableMap<String, Any>?) {
        super.onDPAdPlayStart(params)
        PluginLogger.i(CLASS_NAME, "onDPAdPlayStart", mapOf("viewId" to viewId, "params" to params))
        eventSink?.success(mapOf("eventName" to "onDPAdPlayStart", "params" to params))
    }

    /**
     * Called when ad video playback is paused.
     * @param params Parameters associated with the ad play pause event.
     * Namespace: com.bytedance.sdk.dp.listener.IDPAdListener
     */
    override fun onDPAdPlayPause(params: MutableMap<String, Any>?) {
        super.onDPAdPlayPause(params)
        PluginLogger.i(CLASS_NAME, "onDPAdPlayPause", mapOf("viewId" to viewId, "params" to params))
        eventSink?.success(mapOf("eventName" to "onDPAdPlayPause", "params" to params))
    }

    /**
     * Called when ad video playback continues.
     * @param params Parameters associated with the ad play continue event.
     * Namespace: com.bytedance.sdk.dp.listener.IDPAdListener
     */
    override fun onDPAdPlayContinue(params: MutableMap<String, Any>?) {
        super.onDPAdPlayContinue(params)
        PluginLogger.i(CLASS_NAME, "onDPAdPlayContinue", mapOf("viewId" to viewId, "params" to params))
        eventSink?.success(mapOf("eventName" to "onDPAdPlayContinue", "params" to params))
    }

    /**
     * Called when ad video playback completes.
     * @param params Parameters associated with the ad play complete event.
     * Namespace: com.bytedance.sdk.dp.listener.IDPAdListener
     */
    override fun onDPAdPlayComplete(params: MutableMap<String, Any>?) {
        super.onDPAdPlayComplete(params)
        PluginLogger.i(CLASS_NAME, "onDPAdPlayComplete", mapOf("viewId" to viewId, "params" to params))
        eventSink?.success(mapOf("eventName" to "onDPAdPlayComplete", "params" to params))
    }

    /**
     * Called when an ad is clicked.
     * @param params Parameters associated with the ad click event.
     * Namespace: com.bytedance.sdk.dp.listener.IDPAdListener
     */
    override fun onDPAdClicked(params: MutableMap<String, Any>?) {
        super.onDPAdClicked(params)
        PluginLogger.i(CLASS_NAME, "onDPAdClicked", mapOf("viewId" to viewId, "params" to params))
        eventSink?.success(mapOf("eventName" to "onDPAdClicked", "params" to params))
    }

    /**
     * Called when a rewarded ad interaction needs verification.
     * @param params Parameters associated with the reward verification.
     * Namespace: com.bytedance.sdk.dp.listener.IDPAdListener
     */
    override fun onRewardVerify(params: MutableMap<String, Any>?) {
        super.onRewardVerify(params)
        PluginLogger.i(CLASS_NAME, "onRewardVerify", mapOf("viewId" to viewId, "params" to params))
        eventSink?.success(mapOf("eventName" to "onRewardVerify", "params" to params))
    }

    /**
     * Called when a skippable video ad is skipped by the user.
     * @param params Parameters associated with the skip event.
     * Namespace: com.bytedance.sdk.dp.listener.IDPAdListener
     */
    override fun onSkippedVideo(params: MutableMap<String, Any>?) {
        super.onSkippedVideo(params)
        PluginLogger.i(CLASS_NAME, "onSkippedVideo", mapOf("viewId" to viewId, "params" to params))
        eventSink?.success(mapOf("eventName" to "onSkippedVideo", "params" to params))
    }
} 