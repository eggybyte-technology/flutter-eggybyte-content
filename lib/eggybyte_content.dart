import 'package:flutter/foundation.dart';
import 'package:flutter/gestures.dart';
import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'package:flutter/services.dart';

import 'eggybyte_content_platform_interface.dart';
import 'src/eggybyte_content_listeners.dart'; // Import listeners

// Export listeners for public use
export 'src/eggybyte_content_listeners.dart';

// --- Parameter Data Classes for Platform Views ---

/// Parameters for creating an Immersive Video (Draw) view.
class ImmersiveVideoParams {
  /// Ad offset from bottom (in dp).
  final int? adOffset;

  /// Whether to hide the close button.
  final bool? hideClose;

  /// Style for the progress bar.
  /// See native DPWidgetDrawParams for values (e.g., PROGRESS_BAR_STYLE_LIGHT = 0, PROGRESS_BAR_STYLE_DARK = 1).
  final int? progressBarStyle;

  /// Bottom offset for title, progress bar (in dp).
  final int? bottomOffset;

  /// Top margin for the title (in dp). Optional.
  final int? titleTopMargin;

  /// Scene identifier for content customization. Optional.
  final String? scene;

  /// Custom category if personalization is off. Optional.
  final String? customCategory;

  /// Channel type (SDK 3.2.0.0+). Optional.
  /// See native DPWidgetDrawParams for values (e.g., DRAW_CHANNEL_TYPE_RECOMMEND = 0).
  final int? drawChannelType;

  ImmersiveVideoParams({
    this.adOffset,
    this.hideClose,
    this.progressBarStyle,
    this.bottomOffset,
    this.titleTopMargin,
    this.scene,
    this.customCategory,
    this.drawChannelType,
  });

  /// Converts parameters to a Map suitable for `AndroidView` creationParams.
  Map<String, dynamic> toMap() {
    return {
      if (adOffset != null) 'adOffset': adOffset,
      if (hideClose != null) 'hideClose': hideClose,
      if (progressBarStyle != null) 'progressBarStyle': progressBarStyle,
      if (bottomOffset != null) 'bottomOffset': bottomOffset,
      if (titleTopMargin != null) 'titleTopMargin': titleTopMargin,
      if (scene != null) 'scene': scene,
      if (customCategory != null) 'customCategory': customCategory,
      if (drawChannelType != null) 'drawChannelType': drawChannelType,
    }..removeWhere((key, value) => value == null);
  }
}

/// Parameters for creating a Grid Video (Dual Feed) view.
class GridVideoParams {
  /// Style of the card in the grid.
  /// See native DPWidgetGridParams for values (e.g., CARD_NORMAL_STYLE = 0, CARD_STAGGERED_STYLE = 1).
  final int? cardStyle;

  /// Scene identifier. Optional.
  final String? scene;

  /// Whether to enable pull-to-refresh (SDK 3.2.0.0+).
  final bool? enableRefresh;

  GridVideoParams({this.cardStyle, this.scene, this.enableRefresh});

  /// Converts parameters to a Map suitable for `AndroidView` creationParams.
  Map<String, dynamic> toMap() {
    return {
      if (cardStyle != null) 'cardStyle': cardStyle,
      if (scene != null) 'scene': scene,
      if (enableRefresh != null) 'enableRefresh': enableRefresh,
    }..removeWhere((key, value) => value == null);
  }
}

// --- Plugin Class ---

class EggybyteContent {
  /// View type for the Immersive Video (Draw) Platform View.
  static const String immersiveVideoViewType =
      "com.eggybyte/immersive_video_view";
  static const String _immersiveVideoEventChannelPrefix =
      "com.eggybyte/immersive_video_event_channel_";

  /// View type for the Grid Video (Dual Feed) Platform View.
  static const String gridVideoViewType = "com.eggybyte/grid_video_view";
  static const String _gridVideoEventChannelPrefix =
      "com.eggybyte/grid_video_event_channel_";

  Future<String?> getPlatformVersion() {
    return EggybyteContentPlatform.instance.getPlatformVersion();
  }

  Future<void> initializeSdk({
    required String pangleAppId,
    required String pangleAppName,
    required String eggyByteConfigFileName,
  }) {
    return EggybyteContentPlatform.instance.initializeSdk(
      pangleAppId: pangleAppId,
      pangleAppName: pangleAppName,
      eggyByteConfigFileName: eggyByteConfigFileName,
    );
  }

  Future<Map<String, dynamic>?> triggerDpsdkStart() {
    return EggybyteContentPlatform.instance.triggerDpsdkStart();
  }
}

// --- Event Dispatcher Helper ---
void _dispatchPlatformEvent(
  String eventName,
  Map<String, dynamic>? payload,
  EggybyteContentDrawListener? drawListener,
  EggybyteContentAdListener? adListener,
) {
  // Dispatch to DrawListener
  if (drawListener != null) {
    switch (eventName) {
      case 'onDPRefreshFinish':
        drawListener.onDPRefreshFinish();
        return;
      case 'onDPListDataChange':
        drawListener.onDPListDataChange(payload);
        return;
      case 'onDPSeekTo':
        drawListener.onDPSeekTo(payload);
        return;
      case 'onDPPageChange_deprecated':
        drawListener.onDPPageChangeDeprecated(payload);
        return;
      case 'onDPPageChange':
        drawListener.onDPPageChange(payload);
        return;
      case 'onDPVideoPlay':
        drawListener.onDPVideoPlay(payload);
        return;
      case 'onDPVideoPause':
        drawListener.onDPVideoPause(payload);
        return;
      case 'onDPVideoContinue':
        drawListener.onDPVideoContinue(payload);
        return;
      case 'onDPVideoCompletion':
        drawListener.onDPVideoCompletion(payload);
        return;
      case 'onDPVideoOver':
        drawListener.onDPVideoOver(payload);
        return;
      case 'onDPClose':
        drawListener.onDPClose();
        return;
      case 'onDPReportResult_deprecated':
        drawListener.onDPReportResultDeprecated(payload);
        return;
      case 'onDPReportResult':
        drawListener.onDPReportResult(payload);
        return;
      case 'onDPRequestStart':
        drawListener.onDPRequestStart(payload);
        return;
      case 'onDPRequestFail':
        drawListener.onDPRequestFail(payload);
        return;
      case 'onDPRequestSuccess':
        drawListener.onDPRequestSuccess(payload);
        return;
      case 'onDPClickAvatar':
        drawListener.onDPClickAvatar(payload);
        return;
      case 'onDPClickAuthorName':
        drawListener.onDPClickAuthorName(payload);
        return;
      case 'onDPClickComment':
        drawListener.onDPClickComment(payload);
        return;
      case 'onDPClickLike':
        drawListener.onDPClickLike(payload);
        return;
      case 'onDPClickShare':
        drawListener.onDPClickShare(payload);
        return;
      case 'onDPPageStateChanged':
        drawListener.onDPPageStateChanged(payload);
        return;
      case 'onChannelTabChange':
        drawListener.onChannelTabChange(payload);
        return;
      case 'onDurationChange':
        drawListener.onDurationChange(payload);
        return;
    }
  }

  // Dispatch to AdListener
  if (adListener != null) {
    switch (eventName) {
      case 'onDPAdRequest':
        adListener.onDPAdRequest(payload);
        return;
      case 'onDPAdRequestSuccess':
        adListener.onDPAdRequestSuccess(payload);
        return;
      case 'onDPAdRequestFail':
        adListener.onDPAdRequestFail(payload);
        return;
      case 'onDPAdFillFail':
        adListener.onDPAdFillFail(payload);
        return;
      case 'onDPAdShow':
        adListener.onDPAdShow(payload);
        return;
      case 'onDPAdPlayStart':
        adListener.onDPAdPlayStart(payload);
        return;
      case 'onDPAdPlayPause':
        adListener.onDPAdPlayPause(payload);
        return;
      case 'onDPAdPlayContinue':
        adListener.onDPAdPlayContinue(payload);
        return;
      case 'onDPAdPlayComplete':
        adListener.onDPAdPlayComplete(payload);
        return;
      case 'onDPAdClicked':
        adListener.onDPAdClicked(payload);
        return;
      case 'onRewardVerify':
        adListener.onRewardVerify(payload);
        return;
      case 'onSkippedVideo':
        adListener.onSkippedVideo(payload);
        return;
    }
  }
}

// --- Platform View Wrapper Widgets ---

/// A Flutter Widget that embeds the native Immersive Video view.
/// This widget wraps the underlying PlatformView, providing a user-friendly API.
class ImmersiveVideoView extends StatelessWidget {
  final ImmersiveVideoParams params;
  final EggybyteContentDrawListener? drawListener;
  final EggybyteContentAdListener? adListener;
  final Set<Factory<OneSequenceGestureRecognizer>>? gestureRecognizers;

  const ImmersiveVideoView({
    Key? key,
    required this.params,
    this.drawListener,
    this.adListener,
    this.gestureRecognizers,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    if (defaultTargetPlatform == TargetPlatform.android) {
      return AndroidView(
        viewType: EggybyteContent.immersiveVideoViewType,
        layoutDirection: TextDirection.ltr,
        creationParams: params.toMap(),
        creationParamsCodec: const StandardMessageCodec(),
        gestureRecognizers: gestureRecognizers,
        onPlatformViewCreated: (int id) {
          if (drawListener != null || adListener != null) {
            final eventChannel = EventChannel(
              '${EggybyteContent._immersiveVideoEventChannelPrefix}$id',
            );
            eventChannel.receiveBroadcastStream().listen(
              (dynamic event) {
                if (event is Map<dynamic, dynamic>) {
                  final eventData = event.cast<String, dynamic>();
                  final eventName = eventData['eventName'] as String?;
                  if (eventName == null) return;

                  final Map<String, dynamic> payload = Map.from(eventData)
                    ..remove('eventName');
                  _dispatchPlatformEvent(
                    eventName,
                    payload,
                    drawListener,
                    adListener,
                  );
                }
              },
              onError: (dynamic error) {
                // Handle or log errors from the event channel
                if (kDebugMode) {
                  print(
                    'EggybyteContent: Error on ImmersiveVideoView EventChannel for view $id: $error',
                  );
                }
              },
            );
          }
        },
      );
    }
    // Placeholder for other platforms or if unsupported
    return Center(
      child: Text(
        '${EggybyteContent.immersiveVideoViewType} is not yet supported on ${defaultTargetPlatform.name}.',
      ),
    );
  }
}

/// A Flutter Widget that embeds the native Grid Video (Dual Feed) view.
/// This widget wraps the underlying PlatformView, providing a user-friendly API.
class GridVideoView extends StatelessWidget {
  final GridVideoParams params;
  final EggybyteContentDrawListener? drawListener;
  final EggybyteContentAdListener? adListener;
  final Set<Factory<OneSequenceGestureRecognizer>>? gestureRecognizers;

  const GridVideoView({
    Key? key,
    required this.params,
    this.drawListener,
    this.adListener,
    this.gestureRecognizers,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    if (defaultTargetPlatform == TargetPlatform.android) {
      return AndroidView(
        viewType: EggybyteContent.gridVideoViewType,
        layoutDirection: TextDirection.ltr,
        creationParams: params.toMap(),
        creationParamsCodec: const StandardMessageCodec(),
        gestureRecognizers: gestureRecognizers,
        onPlatformViewCreated: (int id) {
          if (drawListener != null || adListener != null) {
            final eventChannel = EventChannel(
              '${EggybyteContent._gridVideoEventChannelPrefix}$id',
            );
            eventChannel.receiveBroadcastStream().listen(
              (dynamic event) {
                if (event is Map<dynamic, dynamic>) {
                  final eventData = event.cast<String, dynamic>();
                  final eventName = eventData['eventName'] as String?;
                  if (eventName == null) return;

                  final Map<String, dynamic> payload = Map.from(eventData)
                    ..remove('eventName');
                  _dispatchPlatformEvent(
                    eventName,
                    payload,
                    drawListener,
                    adListener,
                  );
                }
              },
              onError: (dynamic error) {
                // Handle or log errors from the event channel
                if (kDebugMode) {
                  print(
                    'EggybyteContent: Error on GridVideoView EventChannel for view $id: $error',
                  );
                }
              },
            );
          }
        },
      );
    }
    // Placeholder for other platforms or if unsupported
    return Center(
      child: Text(
        '${EggybyteContent.gridVideoViewType} is not yet supported on ${defaultTargetPlatform.name}.',
      ),
    );
  }
}
