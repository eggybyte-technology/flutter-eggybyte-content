import Flutter
import UIKit
import KSAdSDK

/**
 * Platform view for Kuaishou (KS) Dual Feed.
 *
 * This class embeds the KSCUFeedPage from the Kuaishou SDK, handles its lifecycle,
 * and forwards events from the native SDK to the Dart side via FlutterCommunicationManager.
 * Based on the implementation in KSDemoFeedViewController.m and KSADRewardManager.m.
 * 
 * This platform view respects Flutter's size constraints and can be wrapped in Container
 * or other widgets that define specific width/height constraints.
 */
class KsDualFeedPlatformView: NSObject, FlutterPlatformView {
    
    // MARK: - Properties
    
    /// Container view that holds the KSCUFeedPage's view controller
    private var _view: UIView
    
    /// Reference to the FeedPage view controller
    private var feedViewController: UIViewController?
    
    /// Reference to the KSCUFeedPage object
    private var feedPage: KSCUFeedPage?
    
    /// The position ID for the feed
    private var posId: String = ""

    // MARK: - Initialization

    /**
     * Initializes the platform view.
     *
     * @param frame The frame rectangle for the view, respecting Flutter's size constraints.
     * @param viewId The unique identifier for the view.
     * @param args The arguments passed from Dart (e.g., `posId`).
     * @param channel The main method channel of the plugin (kept for compatibility but not used directly).
     */
    init(
        frame: CGRect,
        viewIdentifier viewId: Int64,
        arguments args: Any?,
        channel: FlutterMethodChannel
    ) {
        // Create a container view that will hold the KSCUFeedPage's view
        // This view will respect the size constraints from Flutter
        _view = UIView(frame: frame)
        super.init()
        
        // Configure the container view to properly handle size changes from Flutter
        _view.backgroundColor = .clear
        _view.clipsToBounds = true
        _view.autoresizingMask = []
        _view.translatesAutoresizingMaskIntoConstraints = true
        
        // Extract posId from the arguments
        if let arguments = args as? [String: Any] {
            if let extractedPosId = arguments["posId"] as? Int {
                self.posId = String(extractedPosId)
            } else if let extractedPosIdString = arguments["posId"] as? String {
                self.posId = extractedPosIdString
            } else {
                PluginLogger.error("'posId' not found or not an Int/String in arguments: \(arguments)", category: .platformView)
                displayError("Error: posId missing or invalid.")
                return
            }
        }

        if self.posId.isEmpty {
            PluginLogger.error("posId is empty after parsing arguments", category: .platformView)
            displayError("Error: posId is empty.")
            return
        }
        
        PluginLogger.info("KsDualFeedPlatformView initialized with posId: \(posId), frame: \(frame)", category: .platformView)
        createNativeView()
    }

    /**
     * Returns the underlying UIView for this platform view.
     *
     * @return The UIView instance.
     */
    func view() -> UIView {
        return _view
    }
    
    // MARK: - Private Methods
    
    /**
     * Displays an error message in the view.
     *
     * @param message The error message to display.
     */
    private func displayError(_ message: String) {
        let label = UILabel(frame: _view.bounds)
        label.text = message
        label.textAlignment = .center
        label.textColor = .red
        label.numberOfLines = 0
        label.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        _view.addSubview(label)
        PluginLogger.error("Displaying error in view: \(message)", category: .platformView)
    }

    /**
     * Creates and configures the native KSCUFeedPage view.
     *
     * This method initializes the KSCUFeedPage with the provided posId,
     * sets up the event listeners, and configures the view to respect
     * Flutter's size constraints rather than forcing full-screen display.
     */
    private func createNativeView() {
        // Ensure KS SDK is initialized before attempting to create a feed page
        if !EggybyteContentPlugin.ksSdkHasBeenInitialized {
            PluginLogger.error("KS SDK not initialized. Cannot create KSCUFeedPage", category: .platformView)
            displayError("KS SDK NOT INITIALIZED")
            return
        }

        PluginLogger.info("Creating KSCUFeedPage with posId: \(posId)", category: .platformView)

        // Create the feed page with the position ID
        feedPage = KSCUFeedPage(posId: self.posId)
        
        guard let feedPage = feedPage else {
            PluginLogger.error("Failed to create KSCUFeedPage", category: .platformView)
            displayError("Failed to create feed page")
            return
        }
        
        // Set up delegates
        feedPage.videoStateDelegate = self
        feedPage.stateDelegate = self
        feedPage.callBackDelegate = self
        
        // Get the feed view controller
        feedViewController = feedPage.feedViewController
        
        guard let feedViewController = feedViewController else {
            PluginLogger.error("feedViewController is nil", category: .platformView)
            displayError("Failed to get feed view controller")
            return
        }
        
        // Configure the feed view to respect the container's size constraints
        setupFeedViewConstraints(feedViewController: feedViewController)
        
        PluginLogger.info("KSCUFeedPage view added successfully with size constraints", category: .platformView)
    }
    
    /**
     * Sets up proper size constraints for the feed view controller.
     * 
     * This method ensures the feed view respects Flutter's size constraints
     * rather than forcing full-screen display.
     * 
     * @param feedViewController The feed view controller to configure.
     */
    private func setupFeedViewConstraints(feedViewController: UIViewController) {
        let feedView = feedViewController.view!
        
        // Configure the feed view to respect container bounds
        feedView.frame = _view.bounds
        feedView.backgroundColor = .clear
        
        // Important: Use Auto Layout instead of autoresizing masks for better size control
        feedView.translatesAutoresizingMaskIntoConstraints = false
        
        // Add the feed view to our container
        _view.addSubview(feedView)
        
        // Set up constraints to make the feed view fill the container exactly
        NSLayoutConstraint.activate([
            feedView.topAnchor.constraint(equalTo: _view.topAnchor),
            feedView.leadingAnchor.constraint(equalTo: _view.leadingAnchor),
            feedView.trailingAnchor.constraint(equalTo: _view.trailingAnchor),
            feedView.bottomAnchor.constraint(equalTo: _view.bottomAnchor)
        ])
        
        // Configure the view controller to NOT extend beyond safe areas
        // This allows Flutter to control the actual display area
        feedViewController.extendedLayoutIncludesOpaqueBars = false
        feedViewController.edgesForExtendedLayout = []
        feedViewController.automaticallyAdjustsScrollViewInsets = true
        
        PluginLogger.info("Feed view configured with proper size constraints: \(_view.bounds)", category: .platformView)
    }
    
    /**
     * Updates the frame of the platform view.
     * 
     * This method is called when Flutter changes the size of the platform view,
     * typically when wrapped in a Container with specific width/height.
     * 
     * @param newFrame The new frame for the view.
     */
    func updateFrame(_ newFrame: CGRect) {
        PluginLogger.info("Updating platform view frame from \(_view.frame) to \(newFrame)", category: .platformView)
        
        _view.frame = newFrame
        
        // The Auto Layout constraints will automatically update the feed view size
        // to match the new container size, so no manual frame updates needed
    }
    
    /**
     * Sets the theme mode for the feed page.
     *
     * @param themeMode The theme mode (0 for light, 1 for dark).
     */
    func setThemeMode(_ themeMode: Int) {
        feedPage?.setThemeMode(themeMode)
        PluginLogger.info("Theme mode set to: \(themeMode)", category: .platformView)
    }
    
    /**
     * Gets the shared FlutterCommunicationManager instance for sending events.
     *
     * @return The shared FlutterCommunicationManager instance, or nil if not available.
     */
    private func getCommunicationManager() -> FlutterCommunicationManager? {
        guard let manager = FlutterCommunicationManager.getShared() else {
            PluginLogger.error("FlutterCommunicationManager shared instance not available", category: .platformView)
            return nil
        }
        return manager
    }
}

// MARK: - KSCUVideoStateDelegate

extension KsDualFeedPlatformView: KSCUVideoStateDelegate {
    
    /**
     * Called when video playback starts.
     * Forwards the event to Flutter via FlutterCommunicationManager.
     */
    func kscu_videoDidStartPlay(_ videoContent: any KSCUContentInfo) {
        PluginLogger.communication("kscu_videoDidStartPlay called")
        
        guard let manager = getCommunicationManager() else { return }
        
        let contentData = createContentItemData(from: videoContent)
        manager.dispatchVideoEvent("onVideoPlayStart", contentItem: contentData)
    }
    
    /**
     * Called when video playback is paused.
     * Forwards the event to Flutter via FlutterCommunicationManager.
     */
    func kscu_videoDidPause(_ videoContent: any KSCUContentInfo) {
        PluginLogger.communication("kscu_videoDidPause called")
        
        guard let manager = getCommunicationManager() else { return }
        
        let contentData = createContentItemData(from: videoContent)
        manager.dispatchVideoEvent("onVideoPlayPaused", contentItem: contentData)
    }
    
    /**
     * Called when video playback resumes.
     * Forwards the event to Flutter via FlutterCommunicationManager.
     */
    func kscu_videoDidResume(_ videoContent: any KSCUContentInfo) {
        PluginLogger.communication("kscu_videoDidResume called")
        
        guard let manager = getCommunicationManager() else { return }
        
        let contentData = createContentItemData(from: videoContent)
        manager.dispatchVideoEvent("onVideoPlayResume", contentItem: contentData)
    }
    
    /**
     * Called when video playback ends.
     * Forwards the event to Flutter via FlutterCommunicationManager.
     */
    func kscu_videoDidEndPlay(_ videoContent: any KSCUContentInfo, isFinished finished: Bool) {
        PluginLogger.communication("kscu_videoDidEndPlay called with finished: \(finished)")
        
        guard let manager = getCommunicationManager() else { return }
        
        let contentData = createContentItemData(from: videoContent)
        manager.dispatchVideoEvent("onVideoPlayCompleted", contentItem: contentData)
    }
    
    /**
     * Called when video playback fails.
     * Forwards the event to Flutter via FlutterCommunicationManager.
     */
    func kscu_videoDidFailed(toPlay videoContent: any KSCUContentInfo, withError error: Error) {
        PluginLogger.communication("kscu_videoDidFailedToPlay called with error: \(error.localizedDescription)")
        
        guard let manager = getCommunicationManager() else { return }
        
        let contentData = createContentItemData(from: videoContent)
        let nsError = error as NSError
        manager.dispatchVideoEvent(
            "onVideoPlayError", 
            contentItem: contentData, 
            errorCode: nsError.code, 
            extraCode: nsError.userInfo["extraCode"] as? Int ?? 0
        )
    }
}

// MARK: - KSCUContentStateDelegate

extension KsDualFeedPlatformView: KSCUContentStateDelegate {
    
    /**
     * Called when content is fully displayed.
     * Forwards the event to Flutter via FlutterCommunicationManager.
     */
    func kscu_contentDidFullDisplay(_ content: any KSCUContentInfo) {
        PluginLogger.communication("kscu_contentDidFullDisplay called")
        
        guard let manager = getCommunicationManager() else { return }
        
        let contentData = createContentItemData(from: content)
        manager.dispatchPageEvent("onPageEnter", contentItem: contentData)
    }
    
    /**
     * Called when content display ends.
     * Forwards the event to Flutter via FlutterCommunicationManager.
     */
    func kscu_contentDidEndDisplay(_ content: any KSCUContentInfo) {
        PluginLogger.communication("kscu_contentDidEndDisplay called")
        
        guard let manager = getCommunicationManager() else { return }
        
        let contentData = createContentItemData(from: content)
        manager.dispatchPageEvent("onPageLeave", contentItem: contentData)
    }
    
    /**
     * Called when content display is paused.
     * Forwards the event to Flutter via FlutterCommunicationManager.
     */
    func kscu_contentDidPause(_ content: any KSCUContentInfo) {
        PluginLogger.communication("kscu_contentDidPause called")
        
        guard let manager = getCommunicationManager() else { return }
        
        let contentData = createContentItemData(from: content)
        manager.dispatchPageEvent("onPagePause", contentItem: contentData)
    }
    
    /**
     * Called when content display resumes.
     * Forwards the event to Flutter via FlutterCommunicationManager.
     */
    func kscu_contentDidResume(_ content: any KSCUContentInfo) {
        PluginLogger.communication("kscu_contentDidResume called")
        
        guard let manager = getCommunicationManager() else { return }
        
        let contentData = createContentItemData(from: content)
        manager.dispatchPageEvent("onPageResume", contentItem: contentData)
    }
}

// MARK: - KSCUFeedPageCallBackProtocol

extension KsDualFeedPlatformView: KSCUFeedPageCallBackProtocol {
    
    /**
     * Called when content share is clicked.
     * Forwards the event to Flutter via FlutterCommunicationManager.
     */
    func kscuClickContentShare(withItem shareItem: String) {
        PluginLogger.communication("kscuClickContentShareWithItem called")
        
        guard let manager = getCommunicationManager() else { return }
        
        manager.dispatchShareEvent(shareInfo: shareItem)
    }
}

// MARK: - Helper Methods

extension KsDualFeedPlatformView {
    
    /**
     * Converts KS content info to a dictionary format suitable for Flutter.
     *
     * This method transforms native KS content info data into a format that
     * matches the expected structure on the Flutter side.
     *
     * @param contentInfo The native KSCUContentInfo object.
     * @return A dictionary containing the content item data.
     */
    private func createContentItemData(from contentInfo: any KSCUContentInfo) -> [String: Any] {
        var contentData: [String: Any] = [:]
        
        contentData["contentId"] = contentInfo.publicContentId()
        contentData["position"] = 0 // Position is not available in KSCUContentInfo protocol
        contentData["materialType"] = contentInfo.publicContentType().rawValue
        contentData["videoDuration"] = Int64(contentInfo.publicVideoDuration() * 1000) // Convert to milliseconds
        
        PluginLogger.debug("Created content item data: \(contentData)", category: .communication)
        return contentData
    }
} 