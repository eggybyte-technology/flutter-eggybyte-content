// Copyright (c) 2024 EggyByte. All rights reserved. Use of this source code
// is governed by a MIT-style license that can be found in the LICENSE file.

/// Represents Kuaishou Tube Data, corresponding to native `com.kwad.sdk.api.tube.KSTubeData`.
class KsTubeData {
  final String? authorId;
  final String? authorName;
  final int? tubeId; // Native type is long
  final String? tubeName;
  final int? episodeNumber;
  final int? totalEpisodeCount;
  final int? playCount; // Native type is long
  final String? coverUrl;
  final bool? isFinished;
  final bool? locked;
  final int? freeEpisodeCount;
  final int? unlockEpisodeCount;
  final String? videoDesc;

  KsTubeData({
    this.authorId,
    this.authorName,
    this.tubeId,
    this.tubeName,
    this.episodeNumber,
    this.totalEpisodeCount,
    this.playCount,
    this.coverUrl,
    this.isFinished,
    this.locked,
    this.freeEpisodeCount,
    this.unlockEpisodeCount,
    this.videoDesc,
  });

  /// Creates an instance of [KsTubeData] from a map (typically from method channel).
  factory KsTubeData.fromMap(Map<String, dynamic>? map) {
    if (map == null)
      return KsTubeData(); // Or throw error, or return pre-defined empty
    return KsTubeData(
      authorId: map['authorId'] as String?,
      authorName: map['authorName'] as String?,
      tubeId: map['tubeId'] as int?,
      tubeName: map['tubeName'] as String?,
      episodeNumber: map['episodeNumber'] as int?,
      totalEpisodeCount: map['totalEpisodeCount'] as int?,
      playCount: map['playCount'] as int?,
      coverUrl: map['coverUrl'] as String?,
      isFinished: map['isFinished'] as bool?,
      locked: map['locked'] as bool?,
      freeEpisodeCount: map['freeEpisodeCount'] as int?,
      unlockEpisodeCount: map['unlockEpisodeCount'] as int?,
      videoDesc: map['videoDesc'] as String?,
    );
  }

  /// Converts this [KsTubeData] object to a map.
  Map<String, dynamic> toMap() {
    return {
      'authorId': authorId,
      'authorName': authorName,
      'tubeId': tubeId,
      'tubeName': tubeName,
      'episodeNumber': episodeNumber,
      'totalEpisodeCount': totalEpisodeCount,
      'playCount': playCount,
      'coverUrl': coverUrl,
      'isFinished': isFinished,
      'locked': locked,
      'freeEpisodeCount': freeEpisodeCount,
      'unlockEpisodeCount': unlockEpisodeCount,
      'videoDesc': videoDesc,
    };
  }

  @override
  String toString() {
    return 'KsTubeData(authorId: $authorId, authorName: $authorName, tubeId: $tubeId, tubeName: $tubeName, episodeNumber: $episodeNumber, totalEpisodeCount: $totalEpisodeCount, playCount: $playCount, coverUrl: $coverUrl, isFinished: $isFinished, locked: $locked, freeEpisodeCount: $freeEpisodeCount, unlockEpisodeCount: $unlockEpisodeCount, videoDesc: $videoDesc)';
  }
}

/// Represents a Kuaishou Content Item, corresponding to native `KsContentPage.ContentItem`.
class KsContentItem {
  final String? contentId;
  final int? position;
  final int? materialType;
  final int? videoDuration; // Native type is long
  final KsTubeData? tubeData;

  KsContentItem({
    this.contentId,
    this.position,
    this.materialType,
    this.videoDuration,
    this.tubeData,
  });

  /// Creates an instance of [KsContentItem] from a map (typically from method channel).
  factory KsContentItem.fromMap(Map<String, dynamic>? map) {
    if (map == null)
      return KsContentItem(); // Or throw error, or return pre-defined empty
    return KsContentItem(
      contentId: map['contentId'] as String?,
      position: map['position'] as int?,
      materialType: map['materialType'] as int?,
      videoDuration: map['videoDuration'] as int?,
      tubeData: map['tubeData'] != null
          ? KsTubeData.fromMap(map['tubeData'] as Map<String, dynamic>)
          : null,
    );
  }

  /// Converts this [KsContentItem] object to a map.
  Map<String, dynamic> toMap() {
    return {
      'contentId': contentId,
      'position': position,
      'materialType': materialType,
      'videoDuration': videoDuration,
      'tubeData': tubeData?.toMap(),
    };
  }

  @override
  String toString() {
    return 'KsContentItem(contentId: $contentId, position: $position, materialType: $materialType, videoDuration: $videoDuration, tubeData: ${tubeData.toString()})';
  }
}

/// Abstract listener for Kuaishou (KS) Feed Page events.
///
/// Implement this class to receive and handle various events occurring
/// within the native KS Feed Page, such as page lifecycle changes,
/// video playback events, and share actions.
///
/// Event methods will receive a [KsContentItem] object containing details
/// corresponding to the native Kuaishou `KsContentPage.ContentItem`.
abstract class KsFeedEventListener {
  // --- PageListener Events ---

  /// Called when a content page is entered.
  /// Corresponds to `KsContentPage.PageListener.onPageEnter(ContentItem)`.
  /// - [item]: The [KsContentItem] associated with the page.
  void onPageEnter(KsContentItem item);

  /// Called when a content page is resumed.
  /// Corresponds to `KsContentPage.PageListener.onPageResume(ContentItem)`.
  /// - [item]: The [KsContentItem] associated with the page.
  void onPageResume(KsContentItem item);

  /// Called when a content page is paused.
  /// Corresponds to `KsContentPage.PageListener.onPagePause(ContentItem)`.
  /// - [item]: The [KsContentItem] associated with the page.
  void onPagePause(KsContentItem item);

  /// Called when a content page is left.
  /// Corresponds to `KsContentPage.PageListener.onPageLeave(ContentItem)`.
  /// - [item]: The [KsContentItem] associated with the page.
  void onPageLeave(KsContentItem item);

  // --- VideoListener Events ---

  /// Called when video playback starts for a content item.
  /// Corresponds to `KsContentPage.VideoListener.onVideoPlayStart(ContentItem)`.
  /// - [item]: The [KsContentItem] whose video started.
  void onVideoPlayStart(KsContentItem item);

  /// Called when video playback is paused for a content item.
  /// Corresponds to `KsContentPage.VideoListener.onVideoPlayPaused(ContentItem)`.
  /// - [item]: The [KsContentItem] whose video was paused.
  void onVideoPlayPaused(KsContentItem item);

  /// Called when video playback resumes for a content item.
  /// Corresponds to `KsContentPage.VideoListener.onVideoPlayResume(ContentItem)`.
  /// - [item]: The [KsContentItem] whose video resumed.
  void onVideoPlayResume(KsContentItem item);

  /// Called when video playback completes for a content item.
  /// Corresponds to `KsContentPage.VideoListener.onVideoPlayCompleted(ContentItem)`.
  /// - [item]: The [KsContentItem] whose video completed.
  void onVideoPlayCompleted(KsContentItem item);

  /// Called when an error occurs during video playback.
  /// Corresponds to `KsContentPage.VideoListener.onVideoPlayError(ContentItem, int, int)`.
  /// - [item]: The [KsContentItem] where the error occurred.
  /// - [errorCode]: The primary error code from the native SDK.
  /// - [extraCode]: An additional error code providing more details.
  void onVideoPlayError(KsContentItem item, int errorCode, int extraCode);

  // --- KsShareListener Events ---

  /// Called when the share button is clicked within the feed page.
  /// Corresponds to `KsContentPage.KsShareListener.onClickShareButton(String)`.
  /// - [shareInfo]: A string containing information relevant to the share action,
  ///                such as a URL or content identifier. Can be null.
  void onClickShareButton(String? shareInfo);
}
