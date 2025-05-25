package com.eggybyte.content

import com.kwad.sdk.api.KsContentPage
import io.flutter.plugin.common.MethodChannel

/**
 * Implements [KsContentPage.VideoListener] to forward video playback events
 * from the Kuaishou SDK to the Flutter side via a [MethodChannel],
 * using the centralized [KsEventForwarder].
 *
 * This class is a thin wrapper around [KsEventForwarder], responsible only
 * for implementing the Kuaishou SDK interface and invoking the appropriate
 * generic event forwarding method in [KsEventForwarder]. For error events,
 * it includes specific error codes as additional arguments.
 *
 * @param methodChannel The [MethodChannel] used to communicate with Flutter,
 *                      which will be passed to the [KsEventForwarder].
 */
class KsVideoListenerImpl(private val methodChannel: MethodChannel) : KsContentPage.VideoListener {

    /**
     * Called when video playback starts for a [KsContentPage.ContentItem].
     *
     * Forwards the event to Flutter using [KsEventForwarder.forwardContentItemEvent].
     *
     * @param item The content item whose video playback has started. Nullable.
     */
    override fun onVideoPlayStart(item: KsContentPage.ContentItem?) {
        KsEventForwarder.forwardContentItemEvent(methodChannel, "onVideoPlayStart", item)
    }

    /**
     * Called when video playback is paused for a [KsContentPage.ContentItem].
     *
     * Forwards the event to Flutter using [KsEventForwarder.forwardContentItemEvent].
     *
     * @param item The content item whose video playback has been paused. Nullable.
     */
    override fun onVideoPlayPaused(item: KsContentPage.ContentItem?) {
        KsEventForwarder.forwardContentItemEvent(methodChannel, "onVideoPlayPaused", item)
    }

    /**
     * Called when video playback resumes for a [KsContentPage.ContentItem].
     *
     * Forwards the event to Flutter using [KsEventForwarder.forwardContentItemEvent].
     *
     * @param item The content item whose video playback has resumed. Nullable.
     */
    override fun onVideoPlayResume(item: KsContentPage.ContentItem?) {
        KsEventForwarder.forwardContentItemEvent(methodChannel, "onVideoPlayResume", item)
    }

    /**
     * Called when video playback completes for a [KsContentPage.ContentItem].
     *
     * Forwards the event to Flutter using [KsEventForwarder.forwardContentItemEvent].
     *
     * @param item The content item whose video playback has completed. Nullable.
     */
    override fun onVideoPlayCompleted(item: KsContentPage.ContentItem?) {
        KsEventForwarder.forwardContentItemEvent(methodChannel, "onVideoPlayCompleted", item)
    }

    /**
     * Called when an error occurs during video playback for a [KsContentPage.ContentItem].
     *
     * Forwards the event to Flutter using [KsEventForwarder.forwardContentItemEvent],
     * including Kuaishou SDK error codes as additional arguments.
     *
     * @param item The content item for which the video playback error occurred. Nullable.
     * @param errorCode The Kuaishou SDK error code indicating the type of error.
     * @param extraCode An Kuaishou SDK extra code providing more details about the error.
     */
    override fun onVideoPlayError(item: KsContentPage.ContentItem?, errorCode: Int, extraCode: Int) {
        val additionalArgs = mapOf("errorCode" to errorCode, "extraCode" to extraCode)
        KsEventForwarder.forwardContentItemEvent(methodChannel, "onVideoPlayError", item, additionalArgs)
    }
} 