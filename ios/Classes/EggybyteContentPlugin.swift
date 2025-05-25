import Flutter
import UIKit
import KSAdSDK

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
     * The communication manager for handling Flutter interactions.
     */
    private var communicationManager: FlutterCommunicationManager?

    /**
     * Registers the plugin with the Flutter engine.
     *
     * This method is called automatically by Flutter when the plugin is activated.
     * It sets up the method channel and registers the platform view factory.
     *
     * @param registrar The [FlutterPluginRegistrar] for this plugin.
     */
    public static func register(with registrar: FlutterPluginRegistrar) {
        PluginLogger.info("Starting plugin registration", category: .general)
        
        let channel = FlutterMethodChannel(name: "eggybyte_content", binaryMessenger: registrar.messenger())
        let instance = EggybyteContentPlugin()
        instance.channel = channel
        
        // Initialize communication manager
        let communicationManager = FlutterCommunicationManager(methodChannel: channel)
        instance.communicationManager = communicationManager
        FlutterCommunicationManager.setShared(communicationManager)
        
        registrar.addMethodCallDelegate(instance, channel: channel)

        // Register PlatformView factory for Kuaishou Dual Feed
        let ksFeedViewFactory = KsDualFeedPlatformViewFactory(messenger: registrar.messenger(), channel: channel)
        registrar.register(ksFeedViewFactory, withId: "com.eggybyte/ks_dual_feed_view")
        
        PluginLogger.info("Plugin registration completed successfully", category: .general)
    }

    /**
     * Handles method calls received from the Flutter side.
     *
     * @param call The [FlutterMethodCall] representing the invoked method.
     * @param result A [FlutterResult] callback to send a response back to Flutter.
     */
    public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        PluginLogger.communication("Received method call: \(call.method)")
        
        switch call.method {
        case "getPlatformVersion":
            handleGetPlatformVersion(result: result)
            
        case "initializeKsSdk":
            handleInitializeKsSdk(call: call, result: result)
            
        case "checkKsSdkInitializationStatus":
            handleCheckKsSdkInitializationStatus(result: result)
            
        default:
            PluginLogger.warning("Unhandled method call: \(call.method)")
            result(FlutterMethodNotImplemented)
        }
    }
    
    // MARK: - Method Call Handlers
    
    /**
     * Handles the getPlatformVersion method call.
     *
     * @param result The FlutterResult callback.
     */
    private func handleGetPlatformVersion(result: @escaping FlutterResult) {
        let version = "iOS " + UIDevice.current.systemVersion
        PluginLogger.info("Platform version: \(version)")
        result(version)
    }
    
    /**
     * Handles the initializeKsSdk method call.
     *
     * @param call The method call containing arguments.
     * @param result The FlutterResult callback.
     */
    private func handleInitializeKsSdk(call: FlutterMethodCall, result: @escaping FlutterResult) {
        guard let args = call.arguments as? [String: Any],
              let appId = args["ksAppId"] as? String,
              let appName = args["ksAppName"] as? String else {
            
            PluginLogger.error("initializeKsSdk - Invalid arguments: \(String(describing: call.arguments))", category: .sdk)
            communicationManager?.sendErrorResponse(
                result,
                message: "Missing or invalid ksAppId or ksAppName",
                code: "INVALID_ARGUMENTS"
            )
            return
        }
        
        performKsSdkInitialization(appId: appId, appName: appName, flutterResult: result)
    }
    
    /**
     * Handles the checkKsSdkInitializationStatus method call.
     *
     * @param result The FlutterResult callback.
     */
    private func handleCheckKsSdkInitializationStatus(result: @escaping FlutterResult) {
        let isInitialized = EggybyteContentPlugin.ksSdkHasBeenInitialized
        PluginLogger.info("checkKsSdkInitializationStatus returning: \(isInitialized)", category: .sdk)
        result(isInitialized)
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
            PluginLogger.info("KS SDK already initialized", category: .sdk)
            communicationManager?.sendSuccessResponse(
                flutterResult,
                message: "KS SDK already initialized"
            )
            return
        }

        PluginLogger.info("Initializing KS SDK with appId: \(appId), appName: \(appName)", category: .sdk)
        
        do {
            // Create and configure the SDK
            let configuration = KSAdSDKConfiguration.configuration()
            configuration.appId = appId
            
            // Set log level as per the documentation
            configuration.setLoglevel(KSAdSDKLogLevel.off)
            
            // Set the personalization settings
            configuration.setEnablePersonalRecommend(true)
            configuration.setEnableProgrammaticRecommend(true)

            // Try to start the SDK
            KSAdSDKManager.start { success, error in
                DispatchQueue.main.async {
                    if success {
                        EggybyteContentPlugin.ksSdkHasBeenInitialized = true
                        PluginLogger.info("KS SDK initialized successfully", category: .sdk)
                        self.communicationManager?.sendSuccessResponse(
                            flutterResult,
                            message: "KS SDK initialized successfully"
                        )
                    } else {
                        EggybyteContentPlugin.ksSdkHasBeenInitialized = false
                        let errorMessage = error?.localizedDescription ?? "Unknown error"
                        PluginLogger.error("KS SDK initialization failed: \(errorMessage)", error: error, category: .sdk)
                        self.communicationManager?.sendErrorResponse(
                            flutterResult,
                            message: "KS SDK initialization failed: \(errorMessage)",
                            code: "SDK_INIT_FAILED"
                        )
                    }
                }
            }
        } catch {
            EggybyteContentPlugin.ksSdkHasBeenInitialized = false
            PluginLogger.error("Exception during KS SDK initialization: \(error.localizedDescription)", error: error, category: .sdk)
            communicationManager?.sendErrorResponse(
                flutterResult,
                message: "Exception during KS SDK initialization: \(error.localizedDescription)",
                code: "SDK_INIT_EXCEPTION"
            )
        }
    }
}
