import Flutter
import UIKit
import KSAdSDK // Import Kuaishou SDK

/**
 * Main plugin class for eggybyte_content on iOS.
 *
 * This class handles method channel communication between Flutter and native iOS,
 * manages Kuaishou SDK initialization, and registers the PlatformView factory
 * for Kuaishou dual feed views. It implements [FlutterPlugin] to hook into
 * the Flutter engine lifecycle.
 */
public class EggybyteContentPlugin: NSObject, FlutterPlugin {
    /**
     * Tracks whether the Kuaishou SDK has been successfully initialized.
     * True if initialized, false otherwise. This status is used to prevent
     * re-initialization or to gate SDK-dependent operations like creating feed views.
     */
    @objc public static var ksSdkHasBeenInitialized: Bool = false

    /**
     * The method channel used for communication with the Dart side.
     */
    private var channel: FlutterMethodChannel?

    /**
     * Registers the plugin with the Flutter engine.
     *
     * This method is called automatically by Flutter when the plugin is activated.
     * It sets up the method channel and registers the platform view factory.
     *
     * @param registrar The [FlutterPluginRegistrar] for this plugin.
     */
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "eggybyte_content", binaryMessenger: registrar.messenger())
    let instance = EggybyteContentPlugin()
    instance.channel = channel // Store the channel
    registrar.addMethodCallDelegate(instance, channel: channel)

    // Register PlatformView factory for Kuaishou Dual Feed
    let ksFeedViewFactory = KsDualFeedPlatformViewFactory(messenger: registrar.messenger(), channel: channel)
    registrar.register(ksFeedViewFactory, withId: "com.eggybyte/ks_dual_feed_view")
    print("EggybyteContentPlugin: KsDualFeedPlatformViewFactory registered.")
  }

  /**
   * Handles method calls received from the Flutter side.
   *
   * @param call The [FlutterMethodCall] representing the invoked method.
   * @param result A [FlutterResult] callback to send a response back to Flutter.
   */
  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
    switch call.method {
    case "getPlatformVersion":
      result("iOS " + UIDevice.current.systemVersion)
    case "initializeKsSdk":
        guard let args = call.arguments as? [String: Any],
              let appId = args["ksAppId"] as? String,
              let appName = args["ksAppName"] as? String // appName might not be used by KSAdSDK iOS init
        else {
            print("EggybyteContentPlugin: initializeKsSdk - Invalid arguments: \(String(describing: call.arguments))")
            result(FlutterError(code: "INVALID_ARGUMENTS",
                                message: "Missing or invalid ksAppId or ksAppName",
                                details: nil))
            return
        }
        performKsSdkInitialization(appId: appId, appName: appName, flutterResult: result)
    case "checkKsSdkInitializationStatus":
        print("EggybyteContentPlugin: checkKsSdkInitializationStatus called, returning: \(EggybyteContentPlugin.ksSdkHasBeenInitialized)")
        result(EggybyteContentPlugin.ksSdkHasBeenInitialized)
    default:
      result(FlutterMethodNotImplemented)
    }
  }

  /**
   * Initializes the Kuaishou (KS) SDK with the provided application credentials.
   *
   * This method configures and starts the KS SDK. The result of the initialization
   * is reported asynchronously via the [flutterResult].
   *
   * @param appId The Kuaishou application ID.
   * @param appName The Kuaishou application name (Note: KSAdSDK iOS might not use appName directly for init).
   * @param flutterResult The [FlutterResult] to which the success or failure of the
   *                      initialization will be reported.
   */
  private func performKsSdkInitialization(appId: String, appName: String, flutterResult: @escaping FlutterResult) {
    if EggybyteContentPlugin.ksSdkHasBeenInitialized {
        print("EggybyteContentPlugin: KS SDK already initialized.")
        flutterResult(["status": true, "message": "KS SDK already initialized"])
        return
    }

    print("EggybyteContentPlugin: Initializing KS SDK with appId: \(appId), appName: \(appName)")
    let configuration = SDKConfiguration.configuration()
    configuration.appId = appId
    // As per ios-instructions-ks.md:
    // configuration.setLoglevel(KSAdSDKLogLevelOff) // Uncomment and adjust if needed
    // configuration.setEnablePersonalRecommend(true) // Uncomment and adjust if needed
    // configuration.setEnableProgrammaticRecommend(true) // Uncomment and adjust if needed

    KSAdSDKManager.start(completionHandler: { success, error in
        if success {
            EggybyteContentPlugin.ksSdkHasBeenInitialized = true
            print("EggybyteContentPlugin: KS SDK initialized successfully.")
            flutterResult(["status": true, "message": "KS SDK initialized successfully"])
        } else {
            EggybyteContentPlugin.ksSdkHasBeenInitialized = false
            let errorMessage = error?.localizedDescription ?? "Unknown error"
            print("EggybyteContentPlugin: KS SDK initialization failed: \(errorMessage)")
            flutterResult(["status": false, "message": "KS SDK initialization failed: \(errorMessage)"])
        }
    })
  }
}
