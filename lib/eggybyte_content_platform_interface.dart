import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'eggybyte_content_method_channel.dart';

abstract class EggybyteContentPlatform extends PlatformInterface {
  /// Constructs a EggybyteContentPlatform.
  EggybyteContentPlatform() : super(token: _token);

  static final Object _token = Object();

  static EggybyteContentPlatform _instance = MethodChannelEggybyteContent();

  /// The default instance of [EggybyteContentPlatform] to use.
  ///
  /// Defaults to [MethodChannelEggybyteContent].
  static EggybyteContentPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [EggybyteContentPlatform] when
  /// they register themselves.
  static set instance(EggybyteContentPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  Future<void> initializeSdk({
    required String pangleAppId,
    required String pangleAppName,
    required String eggyByteConfigFileName,
  }) {
    throw UnimplementedError('initializeSdk() has not been implemented.');
  }

  Future<Map<String, dynamic>?> triggerDpsdkStart() {
    throw UnimplementedError('triggerDpsdkStart() has not been implemented.');
  }

  /// Displays an immersive short video experience.
  ///
  /// Parameters are passed in [drawParams] and should correspond to
  /// the native `DPWidgetDrawParams` options.
  // Future<void> showImmersiveVideo({Map<String, dynamic>? drawParams}) {
  //   throw UnimplementedError('showImmersiveVideo() has not been implemented.');
  // }

  /// Displays the user profile page.
  ///
  /// Parameters are passed in [userProfileParams] and should correspond to
  /// the native `DPWidgetUserProfileParam` options.
  // Future<void> showUserProfile({Map<String, dynamic>? userProfileParams}) {
  //   throw UnimplementedError('showUserProfile() has not been implemented.');
  // }

  /// Displays a dual-feed grid video view.
  ///
  /// Parameters are passed in [gridParams] and should correspond to
  /// the native `DPWidgetGridParams` options for a dual-feed setup.
  // Future<void> showDualFeedVideo({Map<String, dynamic>? gridParams}) {
  //   throw UnimplementedError('showDualFeedVideo() has not been implemented.');
  // }
}
