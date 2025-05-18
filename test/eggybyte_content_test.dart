// import 'package:flutter_test/flutter_test.dart';
// import 'package:eggybyte_content/eggybyte_content.dart';
// import 'package:eggybyte_content/eggybyte_content_platform_interface.dart';
// import 'package:eggybyte_content/eggybyte_content_method_channel.dart';
// import 'package:plugin_platform_interface/plugin_platform_interface.dart';

// class MockEggybyteContentPlatform
//     with MockPlatformInterfaceMixin
//     implements EggybyteContentPlatform {
//   @override
//   Future<String?> getPlatformVersion() => Future.value('42');

//   @override
//   Future<void> triggerDpsdkStart() async {}

//   @override
//   Future<void> showImmersiveVideo({Map<String, dynamic>? drawParams}) async {
//     print(
//       "MockEggybyteContentPlatform.showImmersiveVideo called with params: $drawParams",
//     );
//   }
// }

// void main() {
//   final EggybyteContentPlatform initialPlatform =
//       EggybyteContentPlatform.instance;

//   setUp(() {
//     EggybyteContentPlatform.instance = MockEggybyteContentPlatform();
//   });

//   tearDown(() {
//     EggybyteContentPlatform.instance = initialPlatform;
//   });

//   test('$MethodChannelEggybyteContent is the default instance', () {
//     expect(
//       EggybyteContentPlatform.instance,
//       isInstanceOf<MockEggybyteContentPlatform>(),
//     );
//   });

//   group('EggybyteContent public API tests', () {
//     late EggybyteContent eggybyteContentPlugin;
//     late MockEggybyteContentPlatform fakePlatform;

//     setUp(() {
//       eggybyteContentPlugin = EggybyteContent();
//       fakePlatform = MockEggybyteContentPlatform();
//       EggybyteContentPlatform.instance = fakePlatform;
//     });

//     test('getPlatformVersion', () async {
//       expect(await eggybyteContentPlugin.getPlatformVersion(), '42');
//     });

//     test('triggerDpsdkStart', () async {
//       await expectLater(eggybyteContentPlugin.triggerDpsdkStart(), completes);
//     });

//     test('showImmersiveVideo without params', () async {
//       await expectLater(eggybyteContentPlugin.showImmersiveVideo(), completes);
//     });

//     test('showImmersiveVideo with params', () async {
//       final params = {'adOffset': 50};
//       await expectLater(
//         eggybyteContentPlugin.showImmersiveVideo(drawParams: params),
//         completes,
//       );
//     });
//   });
// }
