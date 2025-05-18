// package com.kwad.demo.open;

// import android.content.Context;
// import android.support.annotation.NonNull;
// import android.util.Log;


// import com.kwad.demo.open.utils.ToastUtil;
// import com.kwad.sdk.api.KsAdSDK;
// import com.kwad.sdk.api.KsInitCallback;
// import com.kwad.sdk.api.KsLoadManager;
// import com.kwad.sdk.api.KsScene;
// import com.kwad.sdk.api.SdkConfig;

// /**
//  * 快手sdk 初始化工具类
//  * 1、强烈建议媒体调用SDK方法前判断SDK是否初始化，没有初始化先进行初始化，避免进程恢复导致空指针问题
//  */
// public final class KSSdkInitUtil {
//   private static final String TAG = "KSAdSDK";
//   private static final String APP_KEY = "831899f8-567c-4e75-8922-7f345bb57f7c";
//   private static final String APP_WB_KEY = "cK7PgwbAr";

//   private static volatile boolean sHasInit;

//   public static void initSDK(Context context,string appId, string appName,) {
//     //建议只初始化一次
//     Log.i(TAG, "init sdk start");
//     sHasInit = true;
//     final Context appContext = context.getApplicationContext();
//     final long startTime = System.currentTimeMillis();
//     KsAdSDK.init(appContext, new SdkConfig.Builder()
//         .appId(appId) // 测试aapId，请联系快手平台申请正式AppId，必填
//         .appName(appName) // 测试appName，请填写您应用的名称，非必填
//         .showNotification(true) // 是否展示下载通知栏，非必填
//         .customController(UserDataObtainController.getInstance()) // 控制SDK获取用户设备信息接口，非必填
//         .debug(true)
//         .setInitCallback(new KsInitCallback() {
//           @Override
//           public void onSuccess() {
//             Log.i(TAG, "init success time: " + (System.currentTimeMillis() - startTime));
//           }

//           @Override
//           public void onFail(int code, String msg) {
//             Log.i(TAG, "init fail code:" + code + "--msg:" + msg);
//           }
//         }).setStartCallback(new KsInitCallback() {
//           @Override
//           public void onSuccess() {
//             Log.i(TAG, "start success");
//             ToastUtil.showToast(appContext, "SDK启动成功");
//           }

//           @Override
//           public void onFail(int code, String msg) {
//             Log.i(TAG, "start fail msg: " + msg);
//             ToastUtil.showToast(appContext, "SDK启动失败：" + msg);
//           }
//         })
//         .build());
//   }


//   private static void checkSDKInit() {
//     //保证一定是先初始化SDK，再调用
//     if (!sHasInit) {
//       initSDK(DemoApplication.sContext);
//     }
//   }

//   /**
//    * 获取快手SDK的LoadManager
//    */
//   @NonNull
//   public static KsLoadManager getLoadManager() {
//     checkSDKInit();
//     return KsAdSDK.getLoadManager();
//   }

//   /**
//    * 返回SDK的场景参数构造器
//    */
//   public static KsScene.Builder createKSSceneBuilder(long posId) {
//     checkSDKInit();
//     KsScene.Builder builder = new KsScene.Builder(posId);
//     // 可选，跳转第三方应用后，配置返回响应页面;
//     builder.setBackUrl("ksad://returnback");
//     return builder;
//   }
// }
