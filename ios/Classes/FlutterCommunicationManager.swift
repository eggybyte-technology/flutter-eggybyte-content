import Flutter
import Foundation

/**
 * High-level communication manager that serves as the central hub for all Flutter interactions.
 *
 * This class manages its own method channel registration and provides unified methods
 * for all native iOS to Flutter communication. It acts as an aggregation layer on top of
 * the NativeToFlutterEventForwarder base class, offering a more convenient and standardized
 * interface for plugin components.
 */
public class FlutterCommunicationManager {
    
    /// The main method channel for communication with Flutter
    private let methodChannel: FlutterMethodChannel
    
    /// Shared instance for singleton access across the plugin
    public static var shared: FlutterCommunicationManager?
    
    /**
     * Initializes the communication manager with a method channel.
     *
     * @param methodChannel The FlutterMethodChannel for communication.
     */
    public init(methodChannel: FlutterMethodChannel) {
        self.methodChannel = methodChannel
        PluginLogger.communication("FlutterCommunicationManager initialized with method channel")
    }
    
    /**
     * Sets the shared instance of the communication manager.
     *
     * This method establishes the singleton instance that other plugin components
     * can use for Flutter communication.
     *
     * @param manager The communication manager instance.
     */
    public static func setShared(_ manager: FlutterCommunicationManager) {
        shared = manager
        PluginLogger.communication("Shared FlutterCommunicationManager instance set")
    }
    
    /**
     * Gets the shared instance of the communication manager.
     *
     * @return The shared FlutterCommunicationManager instance, or nil if not set.
     */
    public static func getShared() -> FlutterCommunicationManager? {
        return shared
    }
    
    // MARK: - Method Channel Response Methods
    
    /**
     * Sends a success response back to Flutter through the method channel.
     *
     * This is the standardized way for all plugin components to send success responses.
     *
     * @param result The FlutterResult callback to send the response to.
     * @param message Optional success message to include in the response.
     * @param data Optional additional data to include in the response.
     */
    public func sendSuccessResponse(
        _ result: @escaping FlutterResult,
        message: String? = nil,
        data: Any? = nil
    ) {
        PluginLogger.communication("Sending success response via FlutterCommunicationManager")
        NativeToFlutterEventForwarder.sendSuccessResponse(result, message: message, data: data)
    }
    
    /**
     * Sends an error response back to Flutter through the method channel.
     *
     * This is the standardized way for all plugin components to send error responses.
     *
     * @param result The FlutterResult callback to send the response to.
     * @param message The error message to include in the response.
     * @param code Optional error code to include in the response.
     * @param details Optional additional error details.
     */
    public func sendErrorResponse(
        _ result: @escaping FlutterResult,
        message: String,
        code: String = "ERROR",
        details: Any? = nil
    ) {
        PluginLogger.communication("Sending error response via FlutterCommunicationManager - Code: \(code)")
        NativeToFlutterEventForwarder.sendErrorResponse(result, message: message, code: code, details: details)
    }
    
    // MARK: - Event Dispatching to Flutter
    
    /**
     * Dispatches a page lifecycle event to Flutter.
     *
     * This method handles all page-related events (enter, resume, pause, leave)
     * and forwards them to Flutter using the standardized event format.
     *
     * @param eventName The name of the event (e.g., "onPageEnter", "onPageResume").
     * @param contentItem The content item data in dictionary format.
     */
    public func dispatchPageEvent(_ eventName: String, contentItem: [String: Any]) {
        PluginLogger.communication("Dispatching page event: \(eventName)")
        PluginLogger.debug("Page event data: \(contentItem)", category: .communication)
        
        NativeToFlutterEventForwarder.sendEvent(
            methodChannel: methodChannel,
            eventName: eventName,
            eventData: contentItem
        )
    }
    
    /**
     * Dispatches a video playback event to Flutter.
     *
     * This method handles all video-related events (start, pause, resume, complete, error)
     * and forwards them to Flutter using the standardized event format.
     *
     * @param eventName The name of the event (e.g., "onVideoPlayStart", "onVideoPlayError").
     * @param contentItem The content item data in dictionary format.
     * @param errorCode Optional error code for error events.
     * @param extraCode Optional extra error code for error events.
     */
    public func dispatchVideoEvent(
        _ eventName: String,
        contentItem: [String: Any],
        errorCode: Int? = nil,
        extraCode: Int? = nil
    ) {
        PluginLogger.communication("Dispatching video event: \(eventName)")
        
        var eventData = contentItem
        if let errorCode = errorCode {
            eventData["errorCode"] = errorCode
        }
        if let extraCode = extraCode {
            eventData["extraCode"] = extraCode
        }
        
        PluginLogger.debug("Video event data: \(eventData)", category: .communication)
        
        NativeToFlutterEventForwarder.sendEvent(
            methodChannel: methodChannel,
            eventName: eventName,
            eventData: eventData
        )
    }
    
    /**
     * Dispatches a share event to Flutter.
     *
     * This method handles share button click events and forwards them to Flutter.
     *
     * @param shareInfo The share information string.
     */
    public func dispatchShareEvent(shareInfo: String?) {
        PluginLogger.communication("Dispatching share event")
        
        let eventData: [String: Any?] = ["shareInfo": shareInfo]
        
        NativeToFlutterEventForwarder.sendEvent(
            methodChannel: methodChannel,
            eventName: "onClickShareButton",
            eventData: eventData
        )
    }
    
    /**
     * Dispatches a custom event to Flutter.
     *
     * This is a general-purpose method for sending any custom event to Flutter.
     * Other more specific methods should be preferred when available.
     *
     * @param eventName The name of the custom event.
     * @param eventData Optional data to include with the event.
     */
    public func dispatchCustomEvent(_ eventName: String, eventData: [String: Any?]? = nil) {
        PluginLogger.communication("Dispatching custom event: \(eventName)")
        
        NativeToFlutterEventForwarder.sendEvent(
            methodChannel: methodChannel,
            eventName: eventName,
            eventData: eventData
        )
    }
    
    // MARK: - Helper Methods for Data Conversion
    
    /**
     * Creates content item data dictionary from individual parameters.
     *
     * This method provides a standardized way to create content item data
     * that matches the expected structure on the Flutter side.
     *
     * @param contentId The content identifier.
     * @param position The position in the feed.
     * @param materialType The type of content material.
     * @param videoDuration The video duration in milliseconds.
     * @param tubeData Optional tube data dictionary.
     * @return A dictionary containing the content item data.
     */
    public static func createContentItemData(
        contentId: String?,
        position: Int = 0,
        materialType: Int = 0,
        videoDuration: Int64 = 0,
        tubeData: [String: Any]? = nil
    ) -> [String: Any] {
        var contentItem: [String: Any] = [:]
        
        if let contentId = contentId {
            contentItem["contentId"] = contentId
        }
        contentItem["position"] = position
        contentItem["materialType"] = materialType
        contentItem["videoDuration"] = videoDuration
        
        if let tubeData = tubeData {
            contentItem["tubeData"] = tubeData
        }
        
        PluginLogger.debug("Created content item data: \(contentItem)", category: .communication)
        return contentItem
    }
    
    /**
     * Creates tube data dictionary from individual parameters.
     *
     * This method provides a standardized way to create tube data
     * that matches the expected structure on the Flutter side.
     *
     * @param authorId The author identifier.
     * @param authorName The author name.
     * @param tubeId The tube identifier.
     * @param tubeName The tube name.
     * @param episodeNumber The episode number.
     * @param totalEpisodeCount The total episode count.
     * @param playCount The play count.
     * @param coverUrl The cover image URL.
     * @param isFinished Whether the tube is finished.
     * @param locked Whether the tube is locked.
     * @param freeEpisodeCount The free episode count.
     * @param unlockEpisodeCount The unlock episode count.
     * @param videoDesc The video description.
     * @return A dictionary containing the tube data.
     */
    public static func createTubeData(
        authorId: String? = nil,
        authorName: String? = nil,
        tubeId: Int64 = 0,
        tubeName: String? = nil,
        episodeNumber: Int = 0,
        totalEpisodeCount: Int = 0,
        playCount: Int64 = 0,
        coverUrl: String? = nil,
        isFinished: Bool = false,
        locked: Bool = false,
        freeEpisodeCount: Int = 0,
        unlockEpisodeCount: Int = 0,
        videoDesc: String? = nil
    ) -> [String: Any] {
        var tubeData: [String: Any] = [:]
        
        if let authorId = authorId {
            tubeData["authorId"] = authorId
        }
        if let authorName = authorName {
            tubeData["authorName"] = authorName
        }
        tubeData["tubeId"] = tubeId
        if let tubeName = tubeName {
            tubeData["tubeName"] = tubeName
        }
        tubeData["episodeNumber"] = episodeNumber
        tubeData["totalEpisodeCount"] = totalEpisodeCount
        tubeData["playCount"] = playCount
        if let coverUrl = coverUrl {
            tubeData["coverUrl"] = coverUrl
        }
        tubeData["isFinished"] = isFinished
        tubeData["locked"] = locked
        tubeData["freeEpisodeCount"] = freeEpisodeCount
        tubeData["unlockEpisodeCount"] = unlockEpisodeCount
        if let videoDesc = videoDesc {
            tubeData["videoDesc"] = videoDesc
        }
        
        PluginLogger.debug("Created tube data: \(tubeData)", category: .communication)
        return tubeData
    }
} 