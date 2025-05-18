package com.eggybyte.content

import androidx.annotation.MainThread
import com.kwad.sdk.api.KsContentPage
import io.flutter.plugin.common.MethodChannel

/**
 * Implements [KsContentPage.PageListener] to forward page lifecycle events
 * from the Kuaishou SDK to the Flutter side via a [MethodChannel],
 * using the centralized [KsEventForwarder].
 *
 * This class is a thin wrapper around [KsEventForwarder], responsible only
 * for implementing the Kuaishou SDK interface and invoking the appropriate
 * generic event forwarding method in [KsEventForwarder].
 *
 * @param methodChannel The [MethodChannel] used to communicate with Flutter,
 *                      which will be passed to the [KsEventForwarder].
 */
class KsPageListenerImpl(private val methodChannel: MethodChannel) : KsContentPage.PageListener {

    /**
     * Called when a content page is entered.
     *
     * Forwards the event to Flutter using [KsEventForwarder.forwardContentItemEvent].
     *
     * @param item The content item associated with the page that was entered.
     *             Nullable, as per Kuaishou SDK behavior.
     */
    @MainThread
    override fun onPageEnter(item: KsContentPage.ContentItem?) {
        KsEventForwarder.forwardContentItemEvent(methodChannel, "onPageEnter", item)
    }

    /**
     * Called when a content page is resumed.
     *
     * Forwards the event to Flutter using [KsEventForwarder.forwardContentItemEvent].
     *
     * @param item The content item associated with the page that was resumed.
     *             Nullable.
     */
    @MainThread
    override fun onPageResume(item: KsContentPage.ContentItem?) {
        KsEventForwarder.forwardContentItemEvent(methodChannel, "onPageResume", item)
    }

    /**
     * Called when a content page is paused.
     *
     * Forwards the event to Flutter using [KsEventForwarder.forwardContentItemEvent].
     *
     * @param item The content item associated with the page that was paused.
     *             Nullable.
     */
    @MainThread
    override fun onPagePause(item: KsContentPage.ContentItem?) {
        KsEventForwarder.forwardContentItemEvent(methodChannel, "onPagePause", item)
    }

    /**
     * Called when a content page is left.
     *
     * Forwards the event to Flutter using [KsEventForwarder.forwardContentItemEvent].
     *
     * @param item The content item associated with the page that was left.
     *             Nullable.
     */
    @MainThread
    override fun onPageLeave(item: KsContentPage.ContentItem?) {
        KsEventForwarder.forwardContentItemEvent(methodChannel, "onPageLeave", item)
    }
} 