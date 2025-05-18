package com.eggybyte.content

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView // For displaying error messages
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.kwad.sdk.api.KsAdSDK
import com.kwad.sdk.api.KsContentPage // Required for listener types
import com.kwad.sdk.api.KsFeedPage
import com.kwad.sdk.api.KsScene
import io.flutter.plugin.common.MethodChannel // Required for the new property
import io.flutter.plugin.platform.PlatformView

/**
 * A Flutter [PlatformView] for displaying a Kuaishou (KS) dual feed.
 *
 * This class manages the lifecycle of the native Kuaishou feed fragment,
 * embeds it within a [FrameLayout], and handles its display and disposal.
 * It requires the hosting Android Activity to be a [FragmentActivity].
 *
 * @param context The application [Context] used for creating views and accessing resources.
 * @param activityProvider A lambda function that provides the current [Activity]. This is used
 *                         to obtain a [FragmentActivity] for managing the KS feed fragment.
 * @param viewId The unique identifier for this platform view instance, assigned by Flutter.
 * @param creationParams Optional parameters passed from Flutter during view creation. This map can
 *                       contain configuration for the feed, such as 'posId'.
 * @param eventMethodChannel The [MethodChannel] used to send feed events from these listeners
 *                           back to the Dart side.
 */
class KsDualFeedPlatformView(
    private val context: Context,
    private val activityProvider: () -> Activity?,
    private val viewId: Int,
    private val creationParams: Map<String, Any>?,
    private val eventMethodChannel: MethodChannel // Updated parameter
) : PlatformView {

    private val container: FrameLayout = FrameLayout(context)
    private var ksFeedPage: KsFeedPage? = null
    private var feedFragment: Fragment? = null

    // Listener instances, will be initialized in loadAndShowFeedFragment
    private var ksPageListener: KsPageListenerImpl? = null
    private var ksVideoListener: KsVideoListenerImpl? = null
    private var ksShareListener: KsShareListenerImpl? = null

    companion object {
        /**
         * Tag used for logging within the [KsDualFeedPlatformView] class.
         */
        private val CLASS_NAME = KsDualFeedPlatformView::class.java.simpleName
        /**
         * Default Kuaishou Dual Feed Ad Slot ID (PosId).
         * This is used if no 'posId' is provided in [creationParams].
         * Replace with a valid test ID or ensure it's always passed from Flutter.
         */
        private const val DEFAULT_KS_FEED_POS_ID = 40001L 
    }

    init {
        PluginLogger.d(CLASS_NAME, "Initializing KsDualFeedPlatformView", mapOf("viewId" to viewId))
        container.id = View.generateViewId() // Important for Fragment transactions
        loadAndShowFeedFragment()
    }

    /**
     * Displays an error message within the platform view's container.
     * This is used if the feed fragment cannot be loaded or displayed.
     *
     * @param message The error message to display.
     */
    private fun showErrorView(message: String) {
        PluginLogger.e(CLASS_NAME, "Displaying error in KsDualFeedPlatformView: $message")
        val errorTextView = TextView(context)
        errorTextView.text = message
        errorTextView.setTextColor(android.graphics.Color.RED)
        container.removeAllViews()
        container.addView(errorTextView)
    }

    /**
     * Loads the Kuaishou feed page and attempts to display its fragment.
     *
     * This method checks for necessary conditions like SDK initialization and
     * a [FragmentActivity] context before proceeding. It also initializes and registers
     * the Kuaishou feed event listeners.
     */
    private fun loadAndShowFeedFragment() {
        val activity = activityProvider()
        if (activity == null) {
            showErrorView("Activity is null. Cannot show KS Feed Fragment.")
            return
        }
        if (activity !is FragmentActivity) {
            val activityType = activity.javaClass.name
            val errorMsg = "Error: Activity is not a FragmentActivity (is $activityType). KS Feed requires FragmentActivity. Ensure your app's MainActivity extends FlutterFragmentActivity."
            PluginLogger.e(CLASS_NAME, "Activity is not a FragmentActivity. Actual type: $activityType. Cannot show KS Feed Fragment.")
            showErrorView(errorMsg)
            return
        }

        if (!EggybyteContentPlugin.isKsSdkInitialized()) {
            showErrorView("KS SDK not initialized. Cannot load feed page. Please initialize KS SDK first.")
            return
        }

        val posId = (creationParams?.get("posId") as? Number)?.toLong() 
            ?: (creationParams?.get("ksPosId") as? Number)?.toLong() 
            ?: DEFAULT_KS_FEED_POS_ID
        
        PluginLogger.d(CLASS_NAME, "Attempting to load KS Feed Page", mapOf("posId" to posId))

        try {
            val adScene = KsScene.Builder(posId).build()
            ksFeedPage = KsAdSDK.getLoadManager().loadFeedPage(adScene)

            if (ksFeedPage == null) {
                showErrorView("Failed to load KS Feed Page (loadFeedPage returned null).")
                return
            }

            // Initialize and set listeners
            ksPageListener = KsPageListenerImpl(eventMethodChannel)
            ksVideoListener = KsVideoListenerImpl(eventMethodChannel)
            ksShareListener = KsShareListenerImpl(eventMethodChannel)

            ksFeedPage?.setPageListener(ksPageListener)
            ksFeedPage?.setVideoListener(ksVideoListener)
            ksFeedPage?.setShareListener(ksShareListener)
            PluginLogger.i(CLASS_NAME, "Kuaishou event listeners registered on KsFeedPage.")
            
            feedFragment = ksFeedPage?.getFragment()

            if (feedFragment != null) {
                PluginLogger.i(CLASS_NAME, "KS Feed Fragment obtained. Adding to view hierarchy.")
                val fragmentManager = activity.supportFragmentManager
                fragmentManager.beginTransaction()
                    .replace(container.id, feedFragment!!)
                    .commitAllowingStateLoss() 
                PluginLogger.i(CLASS_NAME, "KS Feed Fragment transaction committed.")
            } else {
                showErrorView("KS Feed Fragment is null after loadFeedPage.")
            }
        } catch (e: Exception) {
            val errorMsg = "Error loading/displaying KS Feed Fragment: ${e.message}"
            showErrorView(errorMsg)
            PluginLogger.e(CLASS_NAME, "Error loading or displaying KS Feed Fragment", throwable = e)
        }
    }

    /**
     * Returns the Android [View] that represents this platform view.
     * @return The [FrameLayout] container holding the Kuaishou feed or an error message.
     */
    override fun getView(): View {
        return container
    }

    /**
     * Cleans up resources when the platform view is disposed.
     *
     * This includes removing the feed fragment from the activity's fragment manager,
     * unregistering listeners from the [KsFeedPage], and clearing references.
     */
    override fun dispose() {
        PluginLogger.d(CLASS_NAME, "Disposing KsDualFeedPlatformView", mapOf("viewId" to viewId))
        
        // Unregister listeners from KsFeedPage
        try {
            ksFeedPage?.setPageListener(null)
            ksFeedPage?.setVideoListener(null)
            ksFeedPage?.setShareListener(null)
            PluginLogger.i(CLASS_NAME, "Kuaishou event listeners cleared from KsFeedPage.")
        } catch (e: Exception) {
            PluginLogger.e(CLASS_NAME, "Error clearing listeners from KsFeedPage during dispose", throwable = e)
        }

        if (feedFragment != null) {
            val activity = activityProvider()
            if (activity is FragmentActivity) {
                 try {
                    activity.supportFragmentManager
                        .beginTransaction()
                        .remove(feedFragment!!)
                        .commitAllowingStateLoss()
                    PluginLogger.i(CLASS_NAME, "KS Feed Fragment removed.")
                } catch (e: Exception) {
                    PluginLogger.e(CLASS_NAME, "Error removing KS Feed Fragment during dispose", throwable = e)
                }
            } else {
                PluginLogger.w(CLASS_NAME, "Activity is not a FragmentActivity or null during dispose, cannot remove fragment.")
            }
        }
        ksFeedPage = null 
        feedFragment = null
        // Clear listener references
        ksPageListener = null
        ksVideoListener = null
        ksShareListener = null
        container.removeAllViews()
        PluginLogger.d(CLASS_NAME, "KsDualFeedPlatformView disposed.")
    }
} 