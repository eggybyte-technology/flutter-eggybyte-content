/// Callbacks for Draw video widget events.
///
/// These methods correspond to the native `IDPDrawListener` (and by extension `IDPGridListener`) callbacks.
abstract class EggybyteContentDrawListener {
  /// Called when the refresh operation is finished.
  void onDPRefreshFinish() {}

  /// Called when the list data changes.
  /// - [data]: Data associated with the list change.
  void onDPListDataChange(Map<String, dynamic>? data) {}

  /// Called when a seek operation occurs.
  /// The [payload] map may contain 'progress' (int) and 'duration' (int/long).
  void onDPSeekTo(Map<String, dynamic>? payload) {}

  /// Called when the page changes. (Deprecated by SDK)
  /// - [position]: The new page position.
  void onDPPageChangeDeprecated(Map<String, dynamic>? payload) {}

  /// Called when the page changes, with additional data.
  /// The [payload] map may contain 'position' (int) and 'data' (Map).
  void onDPPageChange(Map<String, dynamic>? payload) {}

  /// Called when video playback starts.
  /// - [data]: Data associated with the video play event.
  void onDPVideoPlay(Map<String, dynamic>? data) {}

  /// Called when video playback is paused.
  /// - [data]: Data associated with the video pause event.
  void onDPVideoPause(Map<String, dynamic>? data) {}

  /// Called when video playback continues after a pause.
  /// - [data]: Data associated with the video continue event.
  void onDPVideoContinue(Map<String, dynamic>? data) {}

  /// Called when video playback completes.
  /// - [data]: Data associated with the video completion event.
  void onDPVideoCompletion(Map<String, dynamic>? data) {}

  /// Called when the video is over (might be different from completion, e.g., error).
  /// - [data]: Data associated with the video over event.
  void onDPVideoOver(Map<String, dynamic>? data) {}

  /// Called when the close event is triggered on the Draw widget.
  void onDPClose() {}

  /// Called with the result of a report operation. (Deprecated by SDK)
  /// The [payload] map may contain 'result' (bool).
  void onDPReportResultDeprecated(Map<String, dynamic>? payload) {}

  /// Called with the result of a report operation, with additional data.
  /// The [payload] map may contain 'result' (bool) and 'data' (Map).
  void onDPReportResult(Map<String, dynamic>? payload) {}

  /// Called when a data request starts.
  /// - [params]: Parameters of the request.
  void onDPRequestStart(Map<String, dynamic>? params) {}

  /// Called when a data request fails.
  /// The [payload] map may contain 'errorCode' (int), 'errorMsg' (String), and 'params' (Map).
  void onDPRequestFail(Map<String, dynamic>? payload) {}

  /// Called when a data request succeeds.
  /// - [data]: The data received (potentially a List of Maps).
  void onDPRequestSuccess(Map<String, dynamic>? payload) {}

  /// Called when the avatar is clicked.
  /// - [data]: Data associated with the avatar click event.
  void onDPClickAvatar(Map<String, dynamic>? data) {}

  /// Called when the author's name is clicked.
  /// - [data]: Data associated with the author name click event.
  void onDPClickAuthorName(Map<String, dynamic>? data) {}

  /// Called when the comment section/button is clicked.
  /// - [data]: Data associated with the comment click event.
  void onDPClickComment(Map<String, dynamic>? data) {}

  /// Called when the like button is clicked.
  /// The [payload] map may contain 'isLiked' (bool) and 'data' (Map).
  void onDPClickLike(Map<String, dynamic>? payload) {}

  /// Called when the share button is clicked.
  /// - [data]: Data associated with the share click event.
  void onDPClickShare(Map<String, dynamic>? data) {}

  /// Called when the page state changes (e.g., loading, success, error).
  /// The [payload] map may contain 'state' (String representation of DPPageState).
  void onDPPageStateChanged(Map<String, dynamic>? payload) {}

  /// Called when the channel tab changes.
  /// The [payload] map may contain 'tabIndex' (int).
  void onChannelTabChange(Map<String, dynamic>? payload) {}

  /// Called when the duration of the content (e.g., video) changes or becomes known.
  /// The [payload] map may contain 'duration' (int/long).
  void onDurationChange(Map<String, dynamic>? payload) {}

  // Note: onCreateQuizView and onQuizBindData are not included as they involve UI creation/binding
  // which is not directly translatable to simple event channel messages for listeners.
}

/// Callbacks for ad-related events.
///
/// These methods correspond to the native `IDPAdListener` callbacks.
abstract class EggybyteContentAdListener {
  /// Called when an ad request is made.
  /// - [params]: Parameters associated with the ad request.
  void onDPAdRequest(Map<String, dynamic>? params) {}

  /// Called when an ad request succeeds.
  /// - [params]: Parameters associated with the successful ad request.
  void onDPAdRequestSuccess(Map<String, dynamic>? params) {}

  /// Called when an ad request fails.
  /// The [payload] map may contain 'errorCode' (int), 'errorMsg' (String), and 'params' (Map).
  void onDPAdRequestFail(Map<String, dynamic>? payload) {}

  /// Called when the ad fill fails.
  /// - [params]: Parameters associated with the ad fill failure.
  void onDPAdFillFail(Map<String, dynamic>? params) {}

  /// Called when an ad is shown.
  /// - [params]: Parameters associated with the ad show event.
  void onDPAdShow(Map<String, dynamic>? params) {}

  /// Called when ad video playback starts.
  /// - [params]: Parameters associated with the ad play start event.
  void onDPAdPlayStart(Map<String, dynamic>? params) {}

  /// Called when ad video playback is paused.
  /// - [params]: Parameters associated with the ad play pause event.
  void onDPAdPlayPause(Map<String, dynamic>? params) {}

  /// Called when ad video playback continues.
  /// - [params]: Parameters associated with the ad play continue event.
  void onDPAdPlayContinue(Map<String, dynamic>? params) {}

  /// Called when ad video playback completes.
  /// - [params]: Parameters associated with the ad play complete event.
  void onDPAdPlayComplete(Map<String, dynamic>? params) {}

  /// Called when an ad is clicked.
  /// - [params]: Parameters associated with the ad click event.
  void onDPAdClicked(Map<String, dynamic>? params) {}

  /// Called when a rewarded ad interaction needs verification.
  /// - [params]: Parameters associated with the reward verification.
  void onRewardVerify(Map<String, dynamic>? params) {}

  /// Called when a skippable video ad is skipped by the user.
  /// - [params]: Parameters associated with the skip event.
  void onSkippedVideo(Map<String, dynamic>? params) {}
}
