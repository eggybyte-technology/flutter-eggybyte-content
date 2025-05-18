package com.eggybyte.content

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView // For displaying error messages
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.kwad.sdk.api.KsAdSDK
import com.kwad.sdk.api.KsFeedPage
import com.kwad.sdk.api.KsScene
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.platform.PlatformView

class KsDualFeedPlatformView(
    private val context: Context,
    private val activityProvider: () -> Activity?,
    private val viewId: Int,
    private val creationParams: Map<String, Any>?,
    private val messenger: BinaryMessenger // For potential future communication
) : PlatformView {

    private val container: FrameLayout = FrameLayout(context)
    private var ksFeedPage: KsFeedPage? = null
    private var feedFragment: Fragment? = null

    companion object {
        private val CLASS_NAME = KsDualFeedPlatformView::class.java.simpleName
        // Default Kuaishou Dual Feed Ad Slot ID. This should be configurable via creationParams.
        // Using a placeholder value. Replace with a valid test ID from Kuaishou.
        private const val DEFAULT_KS_FEED_POS_ID = 40001L 
    }

    init {
        PluginLogger.d(CLASS_NAME, "Initializing KsDualFeedPlatformView", mapOf("viewId" to viewId))
        container.id = View.generateViewId() // Important for Fragment transactions
        loadAndShowFeedFragment()
    }

    private fun showErrorView(message: String) {
        PluginLogger.e(CLASS_NAME, "Displaying error in KsDualFeedPlatformView: $message")
        val errorTextView = TextView(context)
        errorTextView.text = message
        errorTextView.setTextColor(android.graphics.Color.RED)
        container.removeAllViews()
        container.addView(errorTextView)
    }

    private fun loadAndShowFeedFragment() {
        val activity = activityProvider()
        if (activity == null) {
            showErrorView("Activity is null. Cannot show KS Feed Fragment.")
            return
        }
        if (activity !is FragmentActivity) {
            val activityType = activity.javaClass.name
            PluginLogger.e(CLASS_NAME, "Activity is not a FragmentActivity. Actual type: $activityType. Cannot show KS Feed Fragment.")
            showErrorView("Error: Activity is not a FragmentActivity (is $activityType). KS Feed requires FragmentActivity. Ensure your app\'s MainActivity extends FlutterFragmentActivity.")
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
            showErrorView("Error loading/displaying KS Feed Fragment: ${e.message}")
            PluginLogger.e(CLASS_NAME, "Error loading or displaying KS Feed Fragment", throwable = e)
        }
    }

    override fun getView(): View {
        return container
    }

    override fun dispose() {
        PluginLogger.d(CLASS_NAME, "Disposing KsDualFeedPlatformView", mapOf("viewId" to viewId))
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
        container.removeAllViews()
        PluginLogger.d(CLASS_NAME, "KsDualFeedPlatformView disposed.")
    }
} 