# EggyByte Content Flutter Plugin

<div align="center">
  <img src="https://img.shields.io/badge/version-0.1.0-green.svg" alt="Version 0.1.0">
  <img src="https://img.shields.io/badge/license-Proprietary-blue.svg" alt="License">
  <img src="https://img.shields.io/badge/Flutter-3.19+-blue.svg" alt="Flutter 3.19+">
  <img src="https://img.shields.io/badge/Android-Native%20Integration-yellow.svg" alt="Android Native Integration">
  <img src="https://img.shields.io/badge/Kuaishou%20SDK-Integrated-orange.svg" alt="Kuaishou SDK Integrated">
</div>

## 📋 Overview

The `eggybyte_content` Flutter plugin provides a Dart interface for integrating third-party native SDKs to display rich content formats, such as short videos and content feeds. This initial version focuses on integrating the Kuaishou SDK for Android to display dual-feed content.

## ✨ Features

- **Kuaishou SDK Integration (Android)**:
    - Seamless initialization of the Kuaishou SDK.
    - Display Kuaishou's dual feed content directly within your Flutter application using a `PlatformView`.
    - Comprehensive event handling for user interactions and content lifecycle:
        - Page Events: `onPageEnter`, `onPageResume`, `onPagePause`, `onPageLeave`.
        - Video Events: `onVideoPlayStart`, `onVideoPlayPaused`, `onVideoPlayResume`, `onVideoPlayCompleted`, `onVideoPlayError`.
        - Share Events: `onClickShareButton`.
- **Decoupled Event Forwarding (Android)**: Robust and maintainable system for sending events from native Android to Flutter.
- **Extensible Architecture**: Designed to support additional content SDKs and platforms in the future.

## 🏗️ Plugin Architecture (Dart)

The plugin follows a standard three-layer architecture:

```mermaid
graph TD
    A[eggybyte_content.dart (Main Plugin API)] -->|uses| B(eggybyte_content_platform_interface.dart);
    B -->|implemented by| C(eggybyte_content_method_channel.dart);
    C -->|invokes| D[Native Code (Android/iOS)];
```

- **Main Plugin API (`lib/eggybyte_content.dart`)**: Provides user-friendly Widgets and methods.
- **Platform Interface (`lib/eggybyte_content_platform_interface.dart`)**: Defines the abstract API.
- **Method Channel (`lib/eggybyte_content_method_channel.dart`)**: Handles native communication.

## 🚀 Getting Started

### 📋 Prerequisites

| Requirement        | Version / Notes                     |
|--------------------|-------------------------------------|
| Flutter            | 3.19+ (with Dart 3.3+)              |
| Android Compile SDK | 34+ (as per plugin's `build.gradle`) |
| Android Min SDK    | 21+ (as per plugin's `build.gradle`) |

### 🔧 Installation

1.  Add `eggybyte_content` to your `pubspec.yaml` dependencies:

    ```yaml
    dependencies:
      flutter: 
        sdk: flutter
      eggybyte_content:
        # Replace with the correct path or version if published
        path: ../  # Or specific version e.g. ^0.1.0 
    ```

2.  Install the plugin:

    ```bash
    flutter pub get
    ```

### ⚙️ Native Configuration (Android)

1.  **Kuaishou App ID and App Name**: Obtain these credentials from the Kuaishou platform.

2.  **⚠️ Important: `MainActivity.kt` (or `.java`) Configuration**

    To ensure that native Android `Fragment` based views (like the Kuaishou feed) can be correctly embedded and displayed within your Flutter application, your app's main Android `Activity` **MUST** extend `io.flutter.embedding.android.FlutterFragmentActivity`.

    Modify your `android/app/src/main/kotlin/.../MainActivity.kt` (or the Java equivalent) as follows:

    ```kotlin
    // In your app's MainActivity.kt
    package com.example.your_app_package // Replace with your app's package

    import io.flutter.embedding.android.FlutterFragmentActivity // Ensure this import is present

    class MainActivity: FlutterFragmentActivity() {
        // No further modifications are typically needed here for the Kuaishou feed display
        // unless you have other specific Activity lifecycle requirements.
    }
    ```
    Failure to do this will result in errors when trying to display the Kuaishou feed view, as it relies on Android Fragment management provided by `FlutterFragmentActivity`.

3.  **Permissions**: The Kuaishou SDK may require certain permissions. Ensure your app's `AndroidManifest.xml` (usually at `android/app/src/main/AndroidManifest.xml`) includes necessary permissions like internet access:
    ```xml
    <uses-permission android:name="android.permission.INTERNET" />
    <!-- Add other permissions required by Kuaishou SDK -->
    ```
    Refer to the official Kuaishou SDK documentation for a complete list of required permissions and manifest configurations.

### ✨ Usage Example

See the `example/` directory for a comprehensive usage example.

**1. Initialize Kuaishou SDK:**

```dart
import 'package:eggybyte_content/eggybyte_content.dart';

final _eggybyteContentPlugin = EggybyteContent();

// Replace with your actual KS App ID and App Name
const String _ksAppId = "YOUR_KS_APP_ID"; 
const String _ksAppName = "YOUR_KS_APP_NAME";

Future<void> initializeKuaishou() async {
  try {
    final result = await _eggybyteContentPlugin.initializeKsSdk(
      ksAppId: _ksAppId,
      ksAppName: _ksAppName,
    );
    if (result != null && result['status'] == true) {
      print('KS SDK Initialized: ${result['message']}');
    } else {
      print('KS SDK Initialization Failed: ${result?['message']}');
    }
  } catch (e) {
    print('Error initializing KS SDK: $e');
  }
}
```

**2. Display Kuaishou Dual Feed:**

```dart
// In your widget tree
import 'package:eggybyte_content/eggybyte_content.dart';

// Replace with your actual KS Pos ID for the dual feed
const int _ksDualFeedPosId = 123456789;

// ... inside your Widget build method ...
if (Platform.isAndroid) {
  return KsDualFeedView(
    params: KsDualFeedParams(posId: _ksDualFeedPosId),
  );
} else {
  return Text('Kuaishou Dual Feed only available on Android.');
}
```

## 📁 Project Structure (Plugin)

```
eggybyte_content/
├── android/              # Android native implementation (Kotlin)
│   └── src/
│       └── main/
│           ├── kotlin/com/eggybyte/content/
│           │   ├── EggybyteContentPlugin.kt    # Main plugin class
│           │   ├── KsDualFeedFactory.kt      # PlatformView Factory
│           │   ├── KsDualFeedPlatformView.kt # PlatformView Implementation
│           │   ├── KsPageListenerImpl.kt     # Kuaishou Page Event Listener
│           │   ├── KsVideoListenerImpl.kt    # Kuaishou Video Event Listener
│           │   ├── KsShareListenerImpl.kt    # Kuaishou Share Event Listener
│           │   ├── NativeToFlutterEventForwarder.kt # Generic Native-to-Flutter Event Forwarder
│           │   └── KsEventForwarder.kt       # Kuaishou-specific Event Forwarder
│           └── AndroidManifest.xml
├── example/              # Example Flutter application demonstrating usage
│   ├── lib/main.dart     # Main example code
│   └── ...
├── ios/                  # iOS native implementation (Placeholder)
├── lib/                  # Dart API
│   ├── eggybyte_content.dart                 # Public API and Widgets
│   ├── eggybyte_content_method_channel.dart  # MethodChannel implementation
│   ├── eggybyte_content_platform_interface.dart # Platform interface definition
│   └── src/                # Internal utilities/listeners
│       └── eggybyte_content_listeners.dart # Event listener interfaces
├── .gitignore
├── CHANGELOG.md
├── LICENSE               # (Specify your license, e.g., Proprietary)
├── pubspec.yaml
└── README.md
```

## 📜 License

Copyright © 2024-2025 EggyByte Technology. All rights reserved.

This project is proprietary software. No part of this project may be copied, modified, or distributed without the express written permission of EggyByte Technology.

---

<div align="center">
  <p>Developed by EggyByte Technology • 2024-2025</p>
</div>

