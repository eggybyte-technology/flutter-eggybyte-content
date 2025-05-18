package com.eggybyte.content

import android.app.Activity
import android.content.Context
import androidx.annotation.NonNull
import com.bytedance.applog.AppLog
import com.bytedance.applog.InitConfig
import com.bytedance.applog.util.UriConstants
import com.bytedance.sdk.dp.DPSdk
import com.bytedance.sdk.dp.DPSdkConfig
import com.bytedance.sdk.openadsdk.TTAdConfig
import com.bytedance.sdk.openadsdk.TTAdConstant
import com.bytedance.sdk.openadsdk.TTAdSdk
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result


/** EggybyteContentPlugin */
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

    // Use a companion object for the logger tag specific to this class
    companion object {
        private val CLASS_NAME = EggybyteContentPlugin::class.java.simpleName
        // Static reference to FlutterPluginBinding for access from PlatformViews
        // This should be initialized in onAttachedToEngine and cleared in onDetachedFromEngine
        @JvmStatic
        var flutterPluginBinding: FlutterPlugin.FlutterPluginBinding? = null
            private set // Ensure it's only set within this class or companion
    }

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        Companion.flutterPluginBinding = flutterPluginBinding // Store for static access
        PluginLogger.i(CLASS_NAME, "onAttachedToEngine called.")
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "eggybyte_content")
        channel.setMethodCallHandler(this)
        applicationContext = flutterPluginBinding.applicationContext

        // Register PlatformView factories
        val immersiveVideoFactory = ImmersiveVideoFactory { activity }
        flutterPluginBinding.platformViewRegistry.registerViewFactory(
            "com.eggybyte/immersive_video_view",
            immersiveVideoFactory
        )
        PluginLogger.i(CLASS_NAME, "ImmersiveVideoFactory registered with viewType: com.eggybyte/immersive_video_view")

        val gridVideoFactory = GridVideoFactory { activity }
        flutterPluginBinding.platformViewRegistry.registerViewFactory(
            "com.eggybyte/grid_video_view",
            gridVideoFactory
        )
        PluginLogger.i(CLASS_NAME, "GridVideoFactory registered with viewType: com.eggybyte/grid_video_view")

        PluginLogger.i(CLASS_NAME, "Plugin attached. SDK initialization will be triggered from Dart.",
            mapOf("channel" to "eggybyte_content", "applicationContextSet" to (applicationContext != null))
        )
    }

    // This method is kept for direct initialization if ever needed internally,
    // but the primary flow is via the method channel call.
    // private fun initializePangleSDKs(context: Context) { ... } // Commenting out or removing if not used

    // This method is kept for direct initialization if ever needed internally.
    // private fun initializeEggyByteContentSDK(context: Context) { ... } // Commenting out or removing

    // This method is kept for direct initialization if ever needed internally.
    // private fun startEggyByteContentSDKService() { ... } // Commenting out or removing

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        PluginLogger.d(CLASS_NAME, "onMethodCall received", mapOf("method" to call.method, "arguments" to call.arguments))
        when (call.method) {
            "getPlatformVersion" -> {
                val platformVersion = "Android ${android.os.Build.VERSION.RELEASE}"
                PluginLogger.i(CLASS_NAME, "getPlatformVersion returning: *$platformVersion*")
                result.success(platformVersion)
            }
            "initializeSdk" -> {
                val pangleAppId = call.argument<String>("pangleAppId")
                val pangleAppName = call.argument<String>("pangleAppName")
                val eggyByteConfigFileName = call.argument<String>("eggyByteConfigFileName")

                PluginLogger.i(CLASS_NAME, "initializeSdk called with:",
                    mapOf(
                        "pangleAppId" to pangleAppId,
                        "pangleAppName" to pangleAppName,
                        "eggyByteConfigFileName" to eggyByteConfigFileName
                    )
                )

                if (pangleAppId == null || pangleAppName == null || eggyByteConfigFileName == null) {
                    PluginLogger.e(CLASS_NAME, "initializeSdk: Invalid arguments - Missing Pangle App ID, App Name, or EggyByte Config File Name",
                        context = mapOf(
                            "pangleAppId_isNull" to (pangleAppId == null),
                            "pangleAppName_isNull" to (pangleAppName == null),
                            "eggyByteConfigFileName_isNull" to (eggyByteConfigFileName == null)
                        )
                    )
                    result.error("INVALID_ARGUMENTS", "Missing Pangle App ID, App Name, or EggyByte Config File Name", null)
                    return
                }
                performFullSdkInitialization(pangleAppId, pangleAppName, eggyByteConfigFileName, result)
            }
            "triggerDpsdkStart" -> {
                PluginLogger.i(CLASS_NAME, "triggerDpsdkStart called.")
                triggerDpsdkStartCommand(result)
            }
            else -> {
                PluginLogger.w(CLASS_NAME, "Method *${call.method}* not implemented.")
                result.notImplemented()
            }
        }
    }

    private fun performFullSdkInitialization(pangleAppId: String, pangleAppName: String, eggyByteConfigFileName: String, flutterResult: Result) {
        PluginLogger.i(CLASS_NAME, "performFullSdkInitialization started.")
        val currentContext = applicationContext ?: run {
            PluginLogger.e(CLASS_NAME, "performFullSdkInitialization: ApplicationContext is null.")
            flutterResult.error("CONTEXT_NOT_AVAILABLE", "ApplicationContext is null during SDK initialization", null)
            return
        }

        PluginLogger.d(CLASS_NAME, "Pangle Ad SDK Configuration:",
            mapOf(
                "appId" to pangleAppId,
                "appName" to pangleAppName,
                "useTextureView" to true, // As per guide recommendation
                "titleBarTheme" to "TTAdConstant.TITLE_BAR_THEME_DARK",
                "allowShowNotify" to true,
                "directDownloadNetworkType" to "WIFI, MOBILE",
                "supportMultiProcess" to false,
                "debug" to true // Should be configurable or false for release
            )
        )

        val config = InitConfig(pangleAppId, "channel1")
        config.setUriConfig (UriConstants.DEFAULT)
        AppLog.setEncryptAndCompress(true)
        config.setAutoStart(true)
        AppLog.init(currentContext, config)

        val ttAdConfig = TTAdConfig.Builder()
            .appId(pangleAppId)
            .appName(pangleAppName)
            .titleBarTheme(TTAdConstant.TITLE_BAR_THEME_DARK)
            .allowShowNotify(true)
            .directDownloadNetworkType(TTAdConstant.NETWORK_STATE_WIFI, TTAdConstant.NETWORK_STATE_MOBILE)
            .supportMultiProcess(false) // Adjust based on app needs
            .debug(true) // Set to false for release, or make configurable
            // .customController(YourPrivacyController()) // Optional, if privacy controls are needed
            .build()

        PluginLogger.i(CLASS_NAME, "Attempting TTAdSdk.init and TTAdSdk.start...")
        
        // Initialize Pangle Ad SDK with context and config first
        TTAdSdk.init(currentContext, ttAdConfig)
        PluginLogger.i(CLASS_NAME, "TTAdSdk.init(Context, Config) called.")

        // Then start the SDK with a callback.
        // Assuming TTAdSdk.InitCallback is the correct type of TTAdSdk.Callback expected by start().
        TTAdSdk.start(object : TTAdSdk.Callback {
            override fun success() {
                PluginLogger.i(CLASS_NAME, "Pangle Ad SDK started *successfully* via start(Callback).")
                initializeEggyByteContentSDKInternal(currentContext, eggyByteConfigFileName, flutterResult)
            }

            override fun fail(code: Int, msg: String?) {
                PluginLogger.e(CLASS_NAME, "Pangle Ad SDK start(Callback) *failed*.",
                    context = mapOf("code" to code, "message" to msg)
                )
                flutterResult.error("PANGLE_AD_INIT_FAILED", "Pangle Ad SDK start(Callback) failed: $msg", "Code: $code")
            }
        })
    }

    private fun initializeEggyByteContentSDKInternal(
        context: Context,
        eggyByteConfigFileName: String,
        flutterResult: Result
    ) {
        PluginLogger.i(CLASS_NAME, "initializeEggyByteContentSDKInternal called.",
            mapOf("eggyByteConfigFileName" to eggyByteConfigFileName)
        )
        try {
            val configBuilder = DPSdkConfig.Builder()
                .debug(true)
            PluginLogger.d(CLASS_NAME, "DPSdkConfig.Builder created with debug: *true*")

            DPSdk.init(context, eggyByteConfigFileName, configBuilder.build())
            PluginLogger.i(CLASS_NAME, "DPSdk.init() called with config file: *$eggyByteConfigFileName*.")
            flutterResult.success(mapOf("message" to "SDK initialization process started. Call triggerDpsdkStart next."))
        } catch (e: Exception) {
            PluginLogger.e(CLASS_NAME, "EggyByte Content SDK (DPSdk) initialization failed.", throwable = e,
                context = mapOf("configFileName" to eggyByteConfigFileName)
            )
            flutterResult.error("EGGYBYTE_INIT_FAILED", "DPSdk.init failed: ${e.message}", null)
        }
    }

    private fun triggerDpsdkStartCommand(flutterResult: Result) {
        PluginLogger.i(CLASS_NAME, "triggerDpsdkStartCommand invoked.")
        if (applicationContext == null) {
            PluginLogger.e(CLASS_NAME, "triggerDpsdkStartCommand: ApplicationContext is *null*.")
            flutterResult.error("CONTEXT_NOT_AVAILABLE", "ApplicationContext is null, cannot start DPSdk service.", null)
            return
        }
        if (!TTAdSdk.isSdkReady()) {
            PluginLogger.w(CLASS_NAME, "triggerDpsdkStartCommand: Pangle Ad SDK not ready (TTAdSdk.isSdkReady() is *false*).")
            flutterResult.error("PANGLE_SDK_NOT_READY", "Pangle Ad SDK is not ready. DPSdk.start requires Pangle SDK to be initialized.", null)
            return
        }
        PluginLogger.i(CLASS_NAME, "Pangle Ad SDK is ready. Calling DPSdk.start...")
        DPSdk.start { isSuccess, message ->
            if (isSuccess) {
                PluginLogger.i(CLASS_NAME, "DPSdk service started *successfully*.", mapOf("message" to message))
                flutterResult.success(mapOf("isSuccess" to true, "message" to (message ?: "DPSdk service started successfully.")))
            } else {
                PluginLogger.e(CLASS_NAME, "DPSdk service start *failed*.", context = mapOf("message" to message))
                flutterResult.success(mapOf("isSuccess" to false, "message" to (message ?: "DPSdk service start failed.")))
            }
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        PluginLogger.i(CLASS_NAME, "onDetachedFromEngine called.")
        channel.setMethodCallHandler(null)
        applicationContext = null
        Companion.flutterPluginBinding = null // Clear static reference
        PluginLogger.d(CLASS_NAME, "Plugin detached, channel handler and context cleared.")
    }

    // ActivityAware Implementation
    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity
        PluginLogger.i(CLASS_NAME, "onAttachedToActivity: Activity *${activity?.localClassName}* attached.")
    }

    override fun onDetachedFromActivityForConfigChanges() {
        PluginLogger.i(CLASS_NAME, "onDetachedFromActivityForConfigChanges: Activity *${activity?.localClassName}* detached for config changes.")
        activity = null
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        activity = binding.activity
        PluginLogger.i(CLASS_NAME, "onReattachedToActivityForConfigChanges: Activity *${activity?.localClassName}* re-attached.")
    }

    override fun onDetachedFromActivity() {
        PluginLogger.i(CLASS_NAME, "onDetachedFromActivity: Activity *${activity?.localClassName}* detached.")
        activity = null
    }
} 