import Flutter
import Foundation
import KSAdSDK // Ensure this is imported if KSAdSDK types are directly used by factory, though likely not here.

/**
 * Factory for creating `KsDualFeedPlatformView` instances.
 *
 * This class is registered with the Flutter engine to instantiate the native
 * Kuaishou Dual Feed view when requested by the Dart side of the plugin.
 * It receives creation arguments from Flutter, such as the `posId`.
 */
class KsDualFeedPlatformViewFactory: NSObject, FlutterPlatformViewFactory {
    private var messenger: FlutterBinaryMessenger
    private var channel: FlutterMethodChannel // Main plugin channel for sending events back to Dart

    /**
     * Initializes the factory.
     *
     * @param messenger The binary messenger for communication with Flutter.
     * @param channel The main method channel of the plugin, used by platform views
     *                to send events (e.g., feed events) back to Dart.
     */
    init(messenger: FlutterBinaryMessenger, channel: FlutterMethodChannel) {
        self.messenger = messenger
        self.channel = channel
        super.init()
    }

    /**
     * Creates a new `KsDualFeedPlatformView`.
     *
     * This method is called by Flutter when a `KsDualFeedView` widget is added to the tree.
     *
     * @param frame The frame rectangle for the view.
     * @param viewId The unique identifier for the view.
     * @param args The arguments passed from Dart during view creation (e.g., `posId`).
     * @return An instance of `KsDualFeedPlatformView`.
     */
    func create(
        withFrame frame: CGRect,
        viewIdentifier viewId: Int64,
        arguments args: Any?
    ) -> FlutterPlatformView {
        return KsDualFeedPlatformView(
            frame: frame,
            viewIdentifier: viewId,
            arguments: args,
            // binaryMessenger: messenger, // Messenger is available if needed directly by view
            channel: channel // Pass the main channel for event communication
        )
    }

    /**
     * Returns the message codec for encoding/decoding creation arguments.
     *
     * @return The message codec, or `nil` to use `FlutterStandardMessageCodec`.
     *         Defaults to `FlutterStandardMessageCodec.sharedInstance()`.
     */
    public func createArgsCodec() -> FlutterMessageCodec & NSObjectProtocol {
        return FlutterStandardMessageCodec.sharedInstance()
    }
} 