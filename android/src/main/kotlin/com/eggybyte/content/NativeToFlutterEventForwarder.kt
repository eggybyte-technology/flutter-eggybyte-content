package com.eggybyte.content

import io.flutter.plugin.common.MethodChannel

/**
 * Provides utility methods for forwarding events from native Android code
 * to the Flutter side via a [MethodChannel].
 *
 * This class aims to be a general-purpose forwarder, decoupled from specific
 * SDKs or event types. It ensures that all event communication to Flutter
 * goes through a standardized, logged, and error-handled mechanism.
 */
object NativeToFlutterEventForwarder {
    private const val TAG = "NativeToFlutterEventForwarder" // Class name for PluginLogger

    /**
     * Sends an event with associated data to Flutter through the provided [MethodChannel].
     *
     * All calls to Flutter should ideally use this method to ensure consistent
     * logging and error handling.
     *
     * @param methodChannel The [MethodChannel] instance to use for communication.
     *                      It must be the correct channel that Flutter is listening on
     *                      for these events.
     * @param eventName The name of the event or method to invoke on the Flutter side.
     *                  This acts as an identifier for the event type.
     * @param eventData A map containing the data payload for the event.
     *                  Can be null if no data needs to be sent with the event.
     *                  The keys should be strings, and values should be types
     *                  supported by the [StandardMessageCodec].
     */
    fun sendEvent(methodChannel: MethodChannel, eventName: String, eventData: Map<String, Any?>?) {
        try {
            PluginLogger.d(TAG, "Attempting to send event '$eventName' to Flutter.", details = eventData)
            // Ensure this is called on the main thread if UI updates on Flutter side are expected immediately.
            // For now, assuming MethodChannel handles thread safety appropriately or events are background.
            methodChannel.invokeMethod(eventName, eventData)
            PluginLogger.i(TAG, "Event '$eventName' successfully sent to Flutter.")
        } catch (e: Exception) {
            PluginLogger.e(
                TAG,
                "Error sending event '$eventName' to Flutter.",
                throwable = e,
                context = eventData
            )
            // Depending on requirements, might re-throw or handle specific exceptions differently.
        }
    }
} 