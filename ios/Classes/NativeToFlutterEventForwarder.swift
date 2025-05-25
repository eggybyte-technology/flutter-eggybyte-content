import Flutter
import Foundation

/**
 * Provides utility methods for forwarding events from native iOS code
 * to the Flutter side via a [FlutterMethodChannel].
 *
 * This class aims to be a general-purpose forwarder, decoupled from specific
 * SDKs or event types. It ensures that all event communication to Flutter
 * goes through a standardized, logged, and error-handled mechanism.
 */
public class NativeToFlutterEventForwarder {
    
    /**
     * Sends an event with associated data to Flutter through the provided [FlutterMethodChannel].
     *
     * All calls to Flutter should ideally use this method to ensure consistent
     * logging and error handling.
     *
     * @param methodChannel The [FlutterMethodChannel] instance to use for communication.
     *                      It must be the correct channel that Flutter is listening on
     *                      for these events.
     * @param eventName The name of the event or method to invoke on the Flutter side.
     *                  This acts as an identifier for the event type.
     * @param eventData A dictionary containing the data payload for the event.
     *                  Can be nil if no data needs to be sent with the event.
     *                  The keys should be strings, and values should be types
     *                  supported by the [FlutterStandardMessageCodec].
     */
    public static func sendEvent(
        methodChannel: FlutterMethodChannel,
        eventName: String,
        eventData: [String: Any?]?
    ) {
        PluginLogger.debug("Attempting to send event '\(eventName)' to Flutter", category: .communication)
        PluginLogger.debug("Event data: \(String(describing: eventData))", category: .communication)
        
        // Ensure this is called on the main thread for UI updates on Flutter side
        DispatchQueue.main.async {
            methodChannel.invokeMethod(eventName, arguments: eventData) { result in
                if let error = result as? FlutterError {
                    PluginLogger.error(
                        "Error sending event '\(eventName)' to Flutter: \(error.message ?? "Unknown error")",
                        category: .communication
                    )
                } else {
                    PluginLogger.info("Event '\(eventName)' successfully sent to Flutter", category: .communication)
                }
            }
        }
    }
    
    /**
     * Sends an event with associated data to Flutter through the provided [FlutterMethodChannel].
     * This is a convenience method that doesn't require a completion handler.
     *
     * @param methodChannel The [FlutterMethodChannel] instance to use for communication.
     * @param eventName The name of the event or method to invoke on the Flutter side.
     * @param eventData A dictionary containing the data payload for the event.
     */
    public static func sendEventSimple(
        methodChannel: FlutterMethodChannel,
        eventName: String,
        eventData: [String: Any?]? = nil
    ) {
        PluginLogger.debug("Sending simple event '\(eventName)' to Flutter", category: .communication)
        
        DispatchQueue.main.async {
            methodChannel.invokeMethod(eventName, arguments: eventData)
            PluginLogger.info("Simple event '\(eventName)' sent to Flutter", category: .communication)
        }
    }
    
    // MARK: - Method Channel Response Helpers
    
    /**
     * Sends a success response back to Flutter through a [FlutterResult] callback.
     *
     * This method standardizes success responses and ensures consistent logging.
     *
     * @param result The [FlutterResult] callback to send the response to.
     * @param message Optional success message to include in the response.
     * @param data Optional additional data to include in the response.
     */
    public static func sendSuccessResponse(
        _ result: @escaping FlutterResult,
        message: String? = nil,
        data: Any? = nil
    ) {
        PluginLogger.info("Sending success response: \(message ?? "Success")", category: .communication)
        
        var response: [String: Any] = ["success": true]
        if let message = message {
            response["message"] = message
        }
        if let data = data {
            response["data"] = data
        }
        
        DispatchQueue.main.async {
            result(response)
        }
    }
    
    /**
     * Sends an error response back to Flutter through a [FlutterResult] callback.
     *
     * This method standardizes error responses and ensures consistent logging.
     *
     * @param result The [FlutterResult] callback to send the response to.
     * @param message The error message to include in the response.
     * @param code Optional error code to include in the response.
     * @param details Optional additional error details.
     */
    public static func sendErrorResponse(
        _ result: @escaping FlutterResult,
        message: String,
        code: String = "ERROR",
        details: Any? = nil
    ) {
        PluginLogger.error("Sending error response - Code: \(code), Message: \(message)", category: .communication)
        
        DispatchQueue.main.async {
            result(FlutterError(code: code, message: message, details: details))
        }
    }
} 