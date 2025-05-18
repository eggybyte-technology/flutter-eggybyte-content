import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'eggybyte_content_platform_interface.dart';

/// An implementation of [EggybyteContentPlatform] that uses method channels.
class MethodChannelEggybyteContent extends EggybyteContentPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('eggybyte_content');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>(
      'getPlatformVersion',
    );
    return version;
  }

  @override
  Future<Map<String, dynamic>?> initializeKsSdk({
    required String ksAppId,
    required String ksAppName,
  }) async {
    final result = await methodChannel.invokeMethod<Map<dynamic, dynamic>>(
      'initializeKsSdk',
      {'ksAppId': ksAppId, 'ksAppName': ksAppName},
    );
    return result?.map((key, value) => MapEntry(key.toString(), value));
  }

  // Obsolete method implementations removed:
  // @override
  // Future<void> showImmersiveVideo({Map<String, dynamic>? drawParams}) async {
  //   await methodChannel.invokeMethod('showImmersiveVideo', drawParams);
  // }
  //
  // @override
  // Future<void> showUserProfile({Map<String, dynamic>? userProfileParams}) async {
  //   await methodChannel.invokeMethod('showUserProfile', userProfileParams);
  // }
  //
  // @override
  // Future<void> showDualFeedVideo({Map<String, dynamic>? gridParams}) async {
  //   await methodChannel.invokeMethod('showDualFeedVideo', gridParams);
  // }
}
