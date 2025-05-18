import 'package:flutter/foundation.dart';
import 'package:flutter/gestures.dart';
import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'package:flutter/services.dart';

import 'eggybyte_content_platform_interface.dart';
import 'src/ks_feed_event_listener.dart';

// Export necessary classes for plugin users
export 'src/ks_feed_event_listener.dart'
    show KsFeedEventListener, KsContentItem, KsTubeData;
export 'eggybyte_content_platform_interface.dart'
    show
        EggybyteContentPlatform; // Already effectively done by plugin_platform_interface

// --- Parameter Data Classes for Platform Views ---

/// Parameters for creating a Kuaishou (KS) Dual Feed view.
class KsDualFeedParams {
  /// The Kuaishou Ad Slot ID (PosId) for the dual feed.
  /// This will be passed as `posId` or `ksPosId` in creationParams on the native side.
  final int posId;
  // Add any other Kuaishou specific parameters here if needed.

  KsDualFeedParams({required this.posId});

  /// Converts parameters to a Map suitable for `AndroidView` creationParams.
  Map<String, dynamic> toMap() {
    return {
      'posId': posId,
      // Ensure keys match what KsDualFeedPlatformView expects (e.g., 'ksPosId' or 'posId').
      // The native side currently checks for 'posId' then 'ksPosId'.
    };
  }
}

// --- Plugin Class ---

class EggybyteContent {
  // Removed Pangle-specific view types and event channel prefixes
  // static const String immersiveVideoViewType = ...
  // static const String _immersiveVideoEventChannelPrefix = ...
  // static const String gridVideoViewType = ... // This was for Pangle grid
  // static const String _gridVideoEventChannelPrefix = ...

  /// View type for the Kuaishou (KS) Dual Feed Platform View.
  static const String ksDualFeedViewType = "com.eggybyte/ks_dual_feed_view";
  // Event channel prefix can be added later if KS feed view sends events
  // static const String _ksDualFeedEventChannelPrefix =
  //     "com.eggybyte/ks_dual_feed_event_channel_";

  Future<String?> getPlatformVersion() {
    return EggybyteContentPlatform.instance.getPlatformVersion();
  }

  // Removed Pangle-specific initializeSdk method
  // Future<void> initializeSdk(...) { ... }

  // Removed Pangle-specific triggerDpsdkStart method
  // Future<Map<String, dynamic>?> triggerDpsdkStart() { ... }

  /// Initializes the Kuaishou (KS) SDK.
  /// Returns a map with 'status' (bool) and 'message' (String) upon completion.
  Future<Map<String, dynamic>?> initializeKsSdk({
    required String ksAppId,
    required String ksAppName,
  }) {
    return EggybyteContentPlatform.instance.initializeKsSdk(
      ksAppId: ksAppId,
      ksAppName: ksAppName,
    );
  }

  /// Checks if the Kuaishou SDK has been initialized on the native side.
  ///
  /// Returns `true` if initialized, `false` otherwise. If the platform call
  /// fails or returns null, it defaults to `false`.
  Future<bool> checkKsSdkInitializationStatus() {
    return EggybyteContentPlatform.instance.checkKsSdkInitializationStatus();
  }

  /// Sets the listener for Kuaishou feed page events.
  ///
  /// Implement [KsFeedEventListener] and pass your instance to this method
  /// to receive callbacks for various feed page events like page lifecycle,
  /// video playback, and share actions from the Kuaishou SDK.
  ///
  /// Call [clearKsFeedEventListener] to remove the listener when it's no longer needed.
  ///
  /// - [listener]: The [KsFeedEventListener] to register.
  void setKsFeedEventListener(KsFeedEventListener listener) {
    EggybyteContentPlatform.instance.setKsFeedEventListener(listener);
  }

  /// Clears the currently registered Kuaishou feed event listener.
  ///
  /// Call this method when you no longer need to listen to feed events,
  /// for example, when a widget is disposed or you want to stop receiving events.
  void clearKsFeedEventListener() {
    EggybyteContentPlatform.instance.clearKsFeedEventListener();
  }
}

// Removed _dispatchPlatformEvent function as event handling is removed from KsDualFeedView for now.

// --- Platform View Wrapper Widgets ---

// Removed ImmersiveVideoView widget

/// A Flutter Widget that embeds the native Kuaishou (KS) Dual Feed view.
class KsDualFeedView extends StatelessWidget {
  final KsDualFeedParams params;
  // Listeners can be added back later if KS SDK events are implemented
  // final EggybyteContentDrawListener? drawListener;
  // final EggybyteContentAdListener? adListener;
  final Set<Factory<OneSequenceGestureRecognizer>>? gestureRecognizers;

  const KsDualFeedView({
    Key? key,
    required this.params,
    // this.drawListener,
    // this.adListener,
    this.gestureRecognizers,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    if (defaultTargetPlatform == TargetPlatform.android) {
      return AndroidView(
        viewType: EggybyteContent.ksDualFeedViewType,
        layoutDirection: TextDirection.ltr,
        creationParams: params.toMap(),
        creationParamsCodec: const StandardMessageCodec(),
        gestureRecognizers: gestureRecognizers,
        onPlatformViewCreated: (int id) {
          // Event channel setup removed for now. Can be added if KS view sends events.
          // if (drawListener != null || adListener != null) {
          //   final eventChannel = EventChannel(
          //     '${EggybyteContent._ksDualFeedEventChannelPrefix}$id',
          //   );
          //   eventChannel.receiveBroadcastStream().listen(
          //     (dynamic event) { ... },
          //     onError: (dynamic error) { ... },
          //   );
          // }
          if (kDebugMode) {
            print(
              'KsDualFeedView with id $id created. Creation params: ${params.toMap()}',
            );
          }
        },
      );
    }
    // Placeholder for other platforms or if unsupported
    return Center(
      child: Text(
        '${EggybyteContent.ksDualFeedViewType} is not yet supported on ${defaultTargetPlatform.name}.',
      ),
    );
  }
}
