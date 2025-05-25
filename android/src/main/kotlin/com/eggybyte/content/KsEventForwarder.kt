package com.eggybyte.content

import com.kwad.sdk.api.KsContentPage
import com.kwad.sdk.api.tube.KSTubeData
import io.flutter.plugin.common.MethodChannel

/**
 * Handles the forwarding of Kuaishou SDK specific events to Flutter
 * using the [NativeToFlutterEventForwarder].
 *
 * This class centralizes the logic for formatting Kuaishou event data based on common patterns
 * (ContentItem-based or String-based) and invoking the appropriate method calls on the Flutter side.
 * It provides generic static methods to forward these categorized events, ensuring that event data
 * is consistently structured and communication is routed through the [NativeToFlutterEventForwarder].
 */
object KsEventForwarder {
    private const val TAG = "KsEventForwarder" // Class name for PluginLogger

    /**
     * Converts [KSTubeData] from the Kuaishou SDK into a [Map] suitable for
     * sending through a [MethodChannel]. This remains a private utility.
     *
     * @param tubeData The [KSTubeData] object to convert. If null, returns null.
     * @return A map representation of [tubeData], or null if [tubeData] is null.
     */
    private fun convertKsTubeDataToMap(tubeData: KSTubeData?): Map<String, Any?>? {
        if (tubeData == null) return null
        return mapOf(
            "authorId" to tubeData.authorId,
            "authorName" to tubeData.authorName,
            "tubeId" to tubeData.tubeId,
            "tubeName" to tubeData.tubeName,
            "episodeNumber" to tubeData.episodeNumber,
            "totalEpisodeCount" to tubeData.totalEpisodeCount,
            "playCount" to tubeData.playCount,
            "coverUrl" to tubeData.coverUrl,
            "isFinished" to tubeData.isFinished,
            "locked" to tubeData.isLocked,
            "freeEpisodeCount" to tubeData.freeEpisodeCount,
            "unlockEpisodeCount" to tubeData.unlockEpisodeCount,
            "videoDesc" to tubeData.videoDesc
        )
    }

    /**
     * Creates a base argument map from a [KsContentPage.ContentItem].
     * This remains a private utility.
     *
     * @param item The [KsContentPage.ContentItem] to extract data from. Can be null.
     * @param additionalArgs An optional map of additional arguments to include in the result.
     * @return A map containing common data points from the [item] and any [additionalArgs].
     */
    private fun createItemArgs(item: KsContentPage.ContentItem?, additionalArgs: Map<String, Any?> = emptyMap()): Map<String, Any?> {
        val args = mutableMapOf<String, Any?>()
        item?.let {
            args["contentId"] = it.id
            args["position"] = it.position
            args["materialType"] = it.materialType
            args["videoDuration"] = it.videoDuration
            args["tubeData"] = convertKsTubeDataToMap(it.tubeData)
        }
        args.putAll(additionalArgs)
        return args
    }

    /**
     * Forwards an event associated with a [KsContentPage.ContentItem] to Flutter.
     *
     * This method prepares a standardized argument map from the [item] and any [additionalArgs],
     * logs the forwarding action, and then uses [NativeToFlutterEventForwarder] to send the event.
     *
     * @param methodChannel The [MethodChannel] for communication.
     * @param eventName The name of the event to be invoked on the Flutter side.
     * @param item The [KsContentPage.ContentItem] associated with the event. Can be null.
     * @param additionalArgs An optional map of further arguments to merge with the item data.
     *                       Useful for events like errors that have item context plus specific error codes.
     */
    fun forwardContentItemEvent(
        methodChannel: MethodChannel,
        eventName: String,
        item: KsContentPage.ContentItem?,
        additionalArgs: Map<String, Any?> = emptyMap()
    ) {
        PluginLogger.d(
            TAG,
            "Forwarding ContentItem event '$eventName': id=${item?.id}, pos=${item?.position}, tubeId=${item?.tubeData?.tubeId}",
            details = additionalArgs.takeIf { it.isNotEmpty() }
        )
        val eventData = createItemArgs(item, additionalArgs)
        NativeToFlutterEventForwarder.sendEvent(methodChannel, eventName, eventData)
    }

    /**
     * Forwards an event associated with a [String] data payload to Flutter.
     *
     * This method prepares an argument map containing the [stringData] under the specified [dataKey],
     * logs the forwarding action, and then uses [NativeToFlutterEventForwarder] to send the event.
     *
     * @param methodChannel The [MethodChannel] for communication.
     * @param eventName The name of the event to be invoked on the Flutter side.
     * @param stringData The [String] data associated with the event. Can be null.
     * @param dataKey The key under which the [stringData] will be placed in the event arguments map.
     *                Defaults to "data".
     */
    fun forwardStringEvent(
        methodChannel: MethodChannel,
        eventName: String,
        stringData: String?,
        dataKey: String = "data" // Default key for the string data
    ) {
        PluginLogger.d(
            TAG,
            "Forwarding String event '$eventName': $dataKey=$stringData"
        )
        val eventData = mapOf(dataKey to stringData)
        NativeToFlutterEventForwarder.sendEvent(methodChannel, eventName, eventData)
    }
} 