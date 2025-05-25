package com.eggybyte.content

import com.kwad.sdk.api.KsContentPage
import io.flutter.plugin.common.MethodChannel

/**
 * Implements [KsContentPage.KsShareListener] to forward share button click events
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
class KsShareListenerImpl(private val methodChannel: MethodChannel) : KsContentPage.KsShareListener {

    /**
     * Called when the share button is clicked within a Kuaishou content view.
     *
     * Forwards the event, along with the share information, to Flutter using
     * [KsEventForwarder.forwardStringEvent]. The share information is passed under the key "shareInfo".
     *
     * @param shareInfo A string containing information relevant to the share action,
     *                  typically a URL or an identifier for the content being shared.
     *                  This can be null if the SDK provides no specific share information.
     */
    override fun onClickShareButton(shareInfo: String?) {
        KsEventForwarder.forwardStringEvent(methodChannel, "onClickShareButton", shareInfo, "shareInfo")
    }
} 