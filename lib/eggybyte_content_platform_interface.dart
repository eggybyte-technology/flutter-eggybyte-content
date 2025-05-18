import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'eggybyte_content_method_channel.dart';
import 'src/ks_feed_event_listener.dart';

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
    throw UnimplementedError('getPlatformVersion() has not been implemented.');
  }

  /// Initializes the Kuaishou (KS) SDK.
  ///
  /// [ksAppId] The App ID provided by Kuaishou.
  /// [ksAppName] The App Name for your application.
  Future<Map<String, dynamic>?> initializeKsSdk({
    required String ksAppId,
    required String ksAppName,
  }) {
    throw UnimplementedError('initializeKsSdk() has not been implemented.');
  }

  /// Checks if the Kuaishou SDK has been initialized on the native side.
  /// Returns `true` if initialized, `false` otherwise.
  Future<bool> checkKsSdkInitializationStatus() {
    throw UnimplementedError(
      'checkKsSdkInitializationStatus() has not been implemented.',
    );
  }

  /// Sets the listener for Kuaishou feed page events.
  ///
  /// Implement [KsFeedEventListener] and pass your instance to this method
  /// to receive callbacks for various feed page events like page lifecycle,
  /// video playback, and share actions.
  ///
  /// - [listener]: The [KsFeedEventListener] to register.
  void setKsFeedEventListener(KsFeedEventListener listener) {
    throw UnimplementedError(
      'setKsFeedEventListener() has not been implemented.',
    );
  }

  /// Clears the currently registered Kuaishou feed event listener.
  ///
  /// Call this method when you no longer need to listen to feed events,
  /// for example, when a widget is disposed.
  void clearKsFeedEventListener() {
    throw UnimplementedError(
      'clearKsFeedEventListener() has not been implemented.',
    );
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
