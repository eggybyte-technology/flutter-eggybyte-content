package com.eggybyte.content

import android.app.Activity
import android.content.Context
import androidx.annotation.NonNull
import com.kwad.sdk.api.KsAdSDK
import com.kwad.sdk.api.KsInitCallback
import com.kwad.sdk.api.SdkConfig
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

/**
 * Main plugin class for the `eggybyte_content` Flutter plugin.
 *
 * This class handles method channel communication between Flutter and the native Android platform.
 * It is responsible for:
 * 1. Receiving method calls from Dart (e.g., to initialize SDKs).
 * 2. Executing native code based on these calls (e.g., initializing the Kuaishou SDK).
 * 3. Returning results (success or error) back to Dart.
 * 4. Registering [PlatformViewFactory] instances for embedding native views (e.g., Kuaishou feeds).
 *
 * It implements [FlutterPlugin] to be recognized by the Flutter engine and to access
 * resources like [BinaryMessenger] and [ApplicationContext].
 * It also implements [ActivityAware] to get a reference to the current Android [Activity],
 * which is often necessary for UI-related SDK operations or when an [Activity] context is required.
 * It implements [MethodCallHandler] to process incoming method calls on its [MethodChannel].
 */
class EggybyteContentPlugin :
    FlutterPlugin,
    MethodCallHandler,
    ActivityAware {
    // The MethodChannel that will the communication between Flutter and native Android
    //
    // This local reference serves to register the plugin with the Flutter Engine and unregister it
    // when the Flutter Engine is detached from the Activity
    private lateinit var channel: MethodChannel
    private var applicationContext: Context? = null
    private var activity: Activity? = null

    /**
     * Companion object for [EggybyteContentPlugin].
     * Holds static properties and utility methods related to the plugin.
     */
    companion object {
        /**
         * Tag used for logging within the [EggybyteContentPlugin] class.
         * Uses the simple name of the class for consistency.
         */
        private val CLASS_NAME = EggybyteContentPlugin::class.java.simpleName
        // Static reference to FlutterPluginBinding for access from PlatformViews
        // This should be initialized in onAttachedToEngine and cleared in onDetachedFromEngine
        @JvmStatic
        var flutterPluginBinding: FlutterPlugin.FlutterPluginBinding? = null
            private set // Ensure it's only set within this class or companion
        
        // Kuaishou SDK Initialization status
        @JvmStatic
        private var ksSdkHasBeenInitialized: Boolean = false

        /**
         * Checks if the Kuaishou SDK has been initialized.
         * @return `true` if the KS SDK is initialized, `false` otherwise.
         */
        @JvmStatic
        fun isKsSdkInitialized(): Boolean {
            return ksSdkHasBeenInitialized
        }
    }

    // Method channel names
    private object MethodNames {
        const val GET_PLATFORM_VERSION = "getPlatformVersion"
        const val INITIALIZE_KS_SDK = "initializeKsSdk" // Kuaishou
        const val CHECK_KS_SDK_INITIALIZATION_STATUS = "checkKsSdkInitializationStatus" // Kuaishou
    }

    // Platform view type names
    private object ViewTypes {
        const val KS_DUAL_FEED_VIEW = "com.eggybyte/ks_dual_feed_view" // Kuaishou
    }

    /**
     * Called when the plugin is attached to the Flutter engine.
     *
     * This is the first lifecycle method called. It is responsible for setting up
     * the [MethodChannel], context, and registering any [PlatformViewFactory] instances.
     *
     * @param flutterPluginBinding Provides access to Flutter engine services like [BinaryMessenger]
     *                             and [ApplicationContext].
     */
    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        Companion.flutterPluginBinding = flutterPluginBinding // Store for static access
        PluginLogger.i(CLASS_NAME, "onAttachedToEngine called.")
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "eggybyte_content")
        channel.setMethodCallHandler(this)
        applicationContext = flutterPluginBinding.applicationContext

        // Register Kuaishou PlatformView factories
        val ksDualFeedFactory = KsDualFeedFactory({ activity }, flutterPluginBinding.binaryMessenger)
        flutterPluginBinding.platformViewRegistry.registerViewFactory(
            ViewTypes.KS_DUAL_FEED_VIEW,
            ksDualFeedFactory
        )
        PluginLogger.i(CLASS_NAME, "KsDualFeedFactory registered with viewType: ${ViewTypes.KS_DUAL_FEED_VIEW}")

        PluginLogger.i(CLASS_NAME, "Plugin attached. SDK initialization will be triggered from Dart.",
            mapOf("channel" to "eggybyte_content", "applicationContextSet" to (applicationContext != null))
        )
    }

    /**
     * Called when a method is invoked on this plugin's [MethodChannel] from the Dart side.
     *
     * @param call The [MethodCall] object containing the method name and arguments.
     * @param result The [Result] object used to send a response (success or error) back to Dart.
     */
    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        PluginLogger.d(CLASS_NAME, "onMethodCall received", mapOf("method" to call.method, "arguments" to call.arguments))
        when (call.method) {
            MethodNames.GET_PLATFORM_VERSION -> {
                val platformVersion = "Android ${android.os.Build.VERSION.RELEASE}"
                PluginLogger.i(CLASS_NAME, "getPlatformVersion returning: *$platformVersion*")
                result.success(platformVersion)
            }
            MethodNames.INITIALIZE_KS_SDK -> {
                val ksAppId = call.argument<String>("ksAppId")
                val ksAppName = call.argument<String>("ksAppName")

                PluginLogger.i(CLASS_NAME, "${MethodNames.INITIALIZE_KS_SDK} called with:",
                    mapOf(
                        "ksAppId" to ksAppId,
                        "ksAppName" to ksAppName
                    )
                )

                if (ksAppId == null || ksAppName == null) { 
                    val errorMessage = "Missing Kuaishou ksAppId or ksAppName."
                    PluginLogger.e(CLASS_NAME, "${MethodNames.INITIALIZE_KS_SDK}: $errorMessage",
                        context = mapOf(
                            "ksAppId_isNull" to (ksAppId == null),
                            "ksAppName_isNull" to (ksAppName == null)
                        )
                    )
                    result.error("INVALID_ARGUMENTS", errorMessage, null)
                    return
                }
                performKsSdkInitialization(ksAppId, ksAppName, result)
            }
            MethodNames.CHECK_KS_SDK_INITIALIZATION_STATUS -> {
                val isInitialized = isKsSdkInitialized()
                PluginLogger.i(CLASS_NAME, "${MethodNames.CHECK_KS_SDK_INITIALIZATION_STATUS} returning: $isInitialized")
                result.success(isInitialized)
            }
            else -> {
                PluginLogger.w(CLASS_NAME, "Method *${call.method}* not implemented.")
                result.notImplemented()
            }
        }
    }

    /**
     * Performs the initialization of the Kuaishou (KS) SDK.
     *
     * This method constructs the [SdkConfig] for the KS SDK, initiates the SDK initialization,
     * and uses callbacks to report the success or failure asynchronously to the [flutterResult].
     * It also checks if the SDK has already been initialized to prevent redundant calls.
     *
     * @param appId The Kuaishou application ID, must not be null.
     * @param appName The Kuaishou application name, must not be null.
     * @param flutterResult The [MethodChannel.Result] to which the outcome of the SDK initialization
     *                      is reported.
     */
    private fun performKsSdkInitialization(appId: String, appName: String, flutterResult: MethodChannel.Result) {
        PluginLogger.i(CLASS_NAME, "performKsSdkInitialization started.")
        val currentContext = applicationContext ?: run {
            PluginLogger.e(CLASS_NAME, "performKsSdkInitialization: ApplicationContext is null.")
            flutterResult.error("CONTEXT_NOT_AVAILABLE", "ApplicationContext is null during KS SDK initialization", null)
            return
        }

        if (EggybyteContentPlugin.ksSdkHasBeenInitialized) {
            PluginLogger.i(CLASS_NAME, "KS SDK already initialized.")
            flutterResult.success(mapOf("status" to true, "message" to "KS SDK already initialized."))
            return
        }
        
        PluginLogger.d(CLASS_NAME, "Kuaishou SDK Configuration:",
            mapOf(
                "appId" to appId,
                "appName" to appName,
                "enableDebug" to true, 
                "showNotification" to true
            )
        )

        try {
            val sdkConfigBuilder = SdkConfig.Builder()
                .appId(appId) 
                .appName(appName) 
                .showNotification(true) 
                .debug(true)
                .setInitCallback(object : KsInitCallback {
                    override fun onSuccess() {
                        PluginLogger.i(CLASS_NAME, "KS SDK KsInitCallback: onSuccess. Waiting for StartCallback.")
                    }

                    override fun onFail(code: Int, msg: String?) {
                        PluginLogger.e(CLASS_NAME, "KS SDK KsInitCallback: onFail.", context = mapOf("code" to code, "message" to msg))
                    }
                })
                .setStartCallback(object : KsInitCallback { 
                    override fun onSuccess() {
                        PluginLogger.i(CLASS_NAME, "KS SDK started successfully (via StartCallback).")
                        EggybyteContentPlugin.ksSdkHasBeenInitialized = true
                        flutterResult.success(mapOf("status" to true, "message" to "KS SDK initialized and started successfully."))
                    }

                    override fun onFail(code: Int, msg: String?) {
                        PluginLogger.e(CLASS_NAME, "KS SDK start failed (via StartCallback).", context = mapOf("code" to code, "message" to msg))
                        EggybyteContentPlugin.ksSdkHasBeenInitialized = false 
                        flutterResult.error("KS_SDK_START_FAILED", "KS SDK start failed: $msg", "Code: $code")
                    }
                })
            
            val sdkConfig = sdkConfigBuilder.build()

            PluginLogger.i(CLASS_NAME, "Attempting KsAdSDK.init...")
            KsAdSDK.init(currentContext, sdkConfig)
            KsAdSDK.start();
        } catch (e: Exception) {
            PluginLogger.e(CLASS_NAME, "Exception during KS SDK initialization call (KsAdSDK.init).", throwable = e)
            EggybyteContentPlugin.ksSdkHasBeenInitialized = false
            flutterResult.error("KS_SDK_INIT_EXCEPTION", "Exception during KsAdSDK.init: ${e.message}", null)
        }
    }

    /**
     * Called when the plugin is detached from the Flutter engine.
     *
     * This is the final lifecycle method. It should clean up any resources, such as
     * nullifying the [MethodChannel] handler and context references.
     *
     * @param binding Provides access to Flutter engine services.
     */
    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        PluginLogger.i(CLASS_NAME, "onDetachedFromEngine called.")
        channel.setMethodCallHandler(null)
        applicationContext = null
        Companion.flutterPluginBinding = null // Clear static reference
        PluginLogger.d(CLASS_NAME, "Plugin detached, channel handler and context cleared.")
    }

    // ActivityAware Implementation
    /**
     * Called when the plugin is attached to an [Activity].
     * @param binding Provides the [Activity] and lifecycle callbacks.
     */
    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity
        PluginLogger.i(CLASS_NAME, "onAttachedToActivity: Activity *${activity?.localClassName}* attached.")
    }

    /**
     * Called when the plugin is detached from an [Activity] due to configuration changes.
     * The [Activity] reference should be cleared.
     */
    override fun onDetachedFromActivityForConfigChanges() {
        PluginLogger.i(CLASS_NAME, "onDetachedFromActivityForConfigChanges: Activity *${activity?.localClassName}* detached for config changes.")
        activity = null
    }

    /**
     * Called when the plugin is reattached to an [Activity] after configuration changes.
     * The [Activity] reference should be updated.
     * @param binding Provides the new [Activity] and lifecycle callbacks.
     */
    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        activity = binding.activity
        PluginLogger.i(CLASS_NAME, "onReattachedToActivityForConfigChanges: Activity *${activity?.localClassName}* re-attached.")
    }

    /**
     * Called when the plugin is detached from an [Activity].
     * The [Activity] reference should be cleared.
     */
    override fun onDetachedFromActivity() {
        PluginLogger.i(CLASS_NAME, "onDetachedFromActivity: Activity *${activity?.localClassName}* detached.")
        activity = null
    }
} 