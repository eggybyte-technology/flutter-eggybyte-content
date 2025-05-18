// import 'package:flutter/services.dart';
// import 'package:flutter_test/flutter_test.dart';
// import 'package:eggybyte_content/eggybyte_content_method_channel.dart';

// void main() {
//   TestWidgetsFlutterBinding.ensureInitialized();

//   MethodChannelEggybyteContent platform = MethodChannelEggybyteContent();
//   const MethodChannel channel = MethodChannel('eggybyte_content');

//   setUp(() {
//     TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
//         .setMockMethodCallHandler(channel, (MethodCall methodCall) async {
//           return '42';
//         });
//   });

//   tearDown(() {
//     TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
//         .setMockMethodCallHandler(channel, null);
//   });

//   test('getPlatformVersion', () async {
//     expect(await platform.getPlatformVersion(), '42');
//   });

//   test('triggerDpsdkStart', () async {
//     channel.setMockMethodCallHandler((MethodCall methodCall) async {
//       expect(methodCall.method, 'triggerDpsdkStart');
//       return null; // Adjusted to return null as the method is Future<void>
//     });
//     await platform.triggerDpsdkStart();
//   });

//   test('showImmersiveVideo without params', () async {
//     channel.setMockMethodCallHandler((MethodCall methodCall) async {
//       expect(methodCall.method, 'showImmersiveVideo');
//       expect(methodCall.arguments, null);
//       return null;
//     });
//     await platform.showImmersiveVideo();
//   });

//   test('showImmersiveVideo with params', () async {
//     final params = {'adOffset': 10, 'hideClose': true};
//     channel.setMockMethodCallHandler((MethodCall methodCall) async {
//       expect(methodCall.method, 'showImmersiveVideo');
//       expect(methodCall.arguments, params);
//       return null;
//     });
//     await platform.showImmersiveVideo(drawParams: params);
//   });
// }
