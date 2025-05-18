import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:eggybyte_core/eggybyte_core.dart';

import 'eggybyte_content_platform_interface.dart';
import 'src/ks_feed_event_listener.dart';

/// An implementation of [EggybyteContentPlatform] that uses method channels.
class MethodChannelEggybyteContent extends EggybyteContentPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final MethodChannel methodChannel = MethodChannel('eggybyte_content');

  /// The registered listener for Kuaishou feed events.
  KsFeedEventListener? _ksFeedEventListener;

  /// Constructor that sets up the method call handler for feed events.
  MethodChannelEggybyteContent() {
    // For debugging, confirm when this constructor and handler setup occurs.
    LoggingUtils.debug(
      "MethodChannelEggybyteContent: CONSTRUCTOR - Setting call handler for channel 'eggybyte_content'",
    );
    methodChannel.setMethodCallHandler(_handleMethodCall);
  }

  /// Handles incoming method calls from the native platform.
  /// This is used to dispatch events to the [KsFeedEventListener].
  Future<void> _handleMethodCall(MethodCall call) async {
    LoggingUtils.debug(
      'MethodChannelEggybyteContent: _handleMethodCall ENTERED with method: ${call.method}',
    );

    if (_ksFeedEventListener == null) {
      LoggingUtils.warning(
        'MethodChannelEggybyteContent: _handleMethodCall - KS FEED EVENT LISTENER IS NULL. Method: ${call.method}. Returning.',
      );
      return;
    }

    // LoggingUtils.debug(
    //   'MethodChannelEggybyteContent: _handleMethodCall - Received arguments type: ${call.arguments?.runtimeType}, value: ${call.arguments}',
    // );

    Map<String, dynamic>? arguments;
    if (call.arguments == null) {
      arguments = null;
      LoggingUtils.debug(
        'MethodChannelEggybyteContent: _handleMethodCall - Arguments are null.',
      );
    } else if (call.arguments is Map) {
      // Safely convert to Map<String, dynamic>
      try {
        arguments = Map<String, dynamic>.from(call.arguments as Map);
        LoggingUtils.debug(
          'MethodChannelEggybyteContent: _handleMethodCall - Arguments converted to Map<String, dynamic> successfully.',
        );
      } catch (e, s) {
        LoggingUtils.error(
          'MethodChannelEggybyteContent: _handleMethodCall - FAILED TO CONVERT ARGUMENTS to Map<String, dynamic>. Method: ${call.method}, Error: $e, Arguments: ${call.arguments}',
          stackTrace: s,
        );
        return; // Stop further processing if conversion fails
      }
    } else {
      LoggingUtils.error(
        'MethodChannelEggybyteContent: _handleMethodCall - ARGUMENTS ARE NOT A MAP. Method: ${call.method}, Type: ${call.arguments?.runtimeType}, Arguments: ${call.arguments}',
      );
      return; // Stop further processing if arguments are not a map
    }

    switch (call.method) {
      // PageListener Events
      case 'onPageEnter':
        LoggingUtils.info('Event: *onPageEnter*, Args: $arguments');
        _ksFeedEventListener!.onPageEnter(KsContentItem.fromMap(arguments));
        break;
      case 'onPageResume':
        LoggingUtils.info('Event: *onPageResume*, Args: $arguments');
        _ksFeedEventListener!.onPageResume(KsContentItem.fromMap(arguments));
        break;
      case 'onPagePause':
        LoggingUtils.info('Event: *onPagePause*, Args: $arguments');
        _ksFeedEventListener!.onPagePause(KsContentItem.fromMap(arguments));
        break;
      case 'onPageLeave':
        LoggingUtils.info('Event: *onPageLeave*, Args: $arguments');
        _ksFeedEventListener!.onPageLeave(KsContentItem.fromMap(arguments));
        break;

      // VideoListener Events
      case 'onVideoPlayStart':
        LoggingUtils.info('Event: *onVideoPlayStart*, Args: $arguments');
        _ksFeedEventListener!.onVideoPlayStart(
          KsContentItem.fromMap(arguments),
        );
        break;
      case 'onVideoPlayPaused':
        LoggingUtils.info('Event: *onVideoPlayPaused*, Args: $arguments');
        _ksFeedEventListener!.onVideoPlayPaused(
          KsContentItem.fromMap(arguments),
        );
        break;
      case 'onVideoPlayResume':
        LoggingUtils.info('Event: *onVideoPlayResume*, Args: $arguments');
        _ksFeedEventListener!.onVideoPlayResume(
          KsContentItem.fromMap(arguments),
        );
        break;
      case 'onVideoPlayCompleted':
        LoggingUtils.info('Event: *onVideoPlayCompleted*, Args: $arguments');
        _ksFeedEventListener!.onVideoPlayCompleted(
          KsContentItem.fromMap(arguments),
        );
        break;
      case 'onVideoPlayError':
        if (arguments != null) {
          final int errorCode = arguments['errorCode'] as int? ?? 0;
          final int extraCode = arguments['extraCode'] as int? ?? 0;
          LoggingUtils.error(
            'Event: *onVideoPlayError*, Args: $arguments, ErrorCode: $errorCode, ExtraCode: $extraCode',
          );
          // Create a mutable copy to remove error codes before passing to KsContentItem.fromMap
          final Map<String, dynamic> itemArgs = Map<String, dynamic>.from(
            arguments,
          );
          itemArgs.remove('errorCode');
          itemArgs.remove('extraCode');
          _ksFeedEventListener!.onVideoPlayError(
            KsContentItem.fromMap(itemArgs),
            errorCode,
            extraCode,
          );
        } else {
          LoggingUtils.error(
            'Event: *onVideoPlayError* received with null arguments.',
          );
          _ksFeedEventListener!.onVideoPlayError(KsContentItem(), 0, 0);
        }
        break;

      // KsShareListener Events
      case 'onClickShareButton':
        final String? shareInfo = arguments?['shareInfo'] as String?;
        LoggingUtils.info('Event: *onClickShareButton*, ShareInfo: $shareInfo');
        _ksFeedEventListener!.onClickShareButton(shareInfo);
        break;

      default:
        LoggingUtils.warning(
          'MethodChannelEggybyteContent: Received unknown method call: *${call.method}*',
        );
        break;
    }
  }

  @override
  Future<String?> getPlatformVersion() async {
    LoggingUtils.debug(
      'MethodChannelEggybyteContent: getPlatformVersion called',
    );
    final version = await methodChannel.invokeMethod<String>(
      'getPlatformVersion',
    );
    LoggingUtils.info(
      'MethodChannelEggybyteContent: Platform version: $version',
    );
    return version;
  }

  @override
  Future<Map<String, dynamic>?> initializeKsSdk({
    required String ksAppId,
    required String ksAppName,
  }) async {
    LoggingUtils.debug(
      'MethodChannelEggybyteContent: initializeKsSdk called with ksAppId: $ksAppId, ksAppName: $ksAppName',
    );
    final result = await methodChannel.invokeMethod<Map<dynamic, dynamic>>(
      'initializeKsSdk',
      {'ksAppId': ksAppId, 'ksAppName': ksAppName},
    );
    final Map<String, dynamic>? typedResult = result?.map(
      (key, value) => MapEntry(key.toString(), value),
    );
    LoggingUtils.info(
      'MethodChannelEggybyteContent: initializeKsSdk result: $typedResult',
    );
    return typedResult;
  }

  @override
  Future<bool> checkKsSdkInitializationStatus() async {
    LoggingUtils.debug(
      'MethodChannelEggybyteContent: checkKsSdkInitializationStatus called',
    );
    final bool? isInitialized = await methodChannel.invokeMethod<bool>(
      'checkKsSdkInitializationStatus',
    );
    LoggingUtils.info(
      'MethodChannelEggybyteContent: checkKsSdkInitializationStatus result: $isInitialized',
    );
    return isInitialized ?? false;
  }

  /// Registers a listener for Kuaishou feed events.
  ///
  /// The provided [listener] will receive callbacks for various feed events
  /// originating from the native Kuaishou SDK.
  @override
  void setKsFeedEventListener(KsFeedEventListener listener) {
    _ksFeedEventListener = listener;
    LoggingUtils.info(
      'MethodChannelEggybyteContent: KsFeedEventListener registered.',
    );
  }

  /// Clears the registered Kuaishou feed event listener.
  @override
  void clearKsFeedEventListener() {
    _ksFeedEventListener = null;
    LoggingUtils.info(
      'MethodChannelEggybyteContent: KsFeedEventListener cleared.',
    );
  }
}
