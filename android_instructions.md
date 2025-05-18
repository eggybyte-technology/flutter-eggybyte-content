# SDK接入文档
一、SDK接入
1、依赖说明
1.1 安卓sdk依赖
小视频SDK支持安卓版本：21~35

依赖安卓内部库：support-v4，建议版本：24以上

依赖安卓内部库：recyclerview-v7，建议版本：24以上

如果接入小视频3100、3200版本需要依赖安卓内部库：constraint-layout，指定版本：1.1.2，具体方式如下(androidx和support根据具体使用环境使用其中一个即可)：

复制
configurations.all {
    resolutionStrategy {
        // support的用这个
        force 'com.android.support.constraint:constraint-layout:1.1.2'
        // androidx包用这个
        force 'androidx.constraintlayout:constraintlayout:1.1.2'
    }
}
1.2 外部库依赖
小视频SDK需要依赖两个外部库：

RangersAppLog：日志上报（通过Maven接入小视频SDK时会自动引入，开发者无需手动引入），赋能370x及异常版本SDK内部已处理相关逻辑，开发者不需要特殊处理；
穿山甲SDK：商业化变现（具体接入详见官方接入文档），① 穿山甲广告SDK需要开发者参考平台接入文档进行手动接入；② 小视频SDK依赖穿山甲SDK中的广告功能进行变现，两者是独立的两个SDK
注： 如果您已经通过其他途径接入过这两个库无需重复申请和接入。

重要：

① 请自查是否单独申请过火山引擎平台的appid/单独接入过applog，若其他业务侧生成的appid和穿山甲侧不同，开发者可以自行处理applog相关逻辑；

② 若开发者有接入穿山甲赋能输出其他方向，需确保其初始化时机早于小视频SDK，否则会导致小视频SDK不能正常工作&媒体自己的埋点失效；

使用 Applog.init(Context, InitConfig) 初始化 applog

③ 若开发者没有以上两种使用场景，即只接入小视频SDK，则不需要单独初始化applog，强烈建议自行接入applog的媒体升级到3901及以上版本，具体详见下面的1.3初始化SDK；



1.3 so库说明
小视频SDK内置点播相关的so库，只支持armeabi-v7a架构和arm64-v8a架构。

1.4 混淆说明
如果您接入了类似ResGuard的资源混淆框架，需要将对应sdk版本Demo中的keep_res_xxxx.txt中的资源都加入到这类框架的白名单中（xxxx为对应的赋能版本号），当前最新版本的keep_res.txt如下

keep_res_3700.txt
keep_res_3700.txt
1.06 KB
2、集成SDK
目前支持自动集成和手动集成两种方式，如果您使用Gradle编译，建议您使用自动接入方式配置库文件。

2.1 自动集成
打开project级别的build.gradle，添加如下两个仓库

复制
// 在allprojects的repositories中添加
maven {
    url "https://artifact.bytedance.com/repository/Volcengine/"
}
maven {
    url "https://artifact.bytedance.com/repository/pangle/"
}
在app module的build.gradle文件的dependencies中添加：

复制
// 在dependencies中添加，版本号以赋能平台生成的maven命令为准，获取方式如下图所示，如果有额外接入点播能力，可以使用剔除点播4210版本
implementation('com.pangle.cn:pangrowth-sdk:x.x.x.x') {
    exclude group: 'com.pangle.cn', module: 'pangrowth-game-sdk'
    exclude group: 'com.pangle.cn', module: 'pangrowth-novel-sdk'
    exclude group: 'com.pangle.cn', module: 'pangrowth-luckycat-sdk'
    exclude group: 'com.pangle.cn', module: 'partner-luckycat-api-sdk'
    exclude group: 'com.pangle.cn', module: 'pangrowth-reward-sdk'
    exclude group: 'com.pangle.cn', module: 'partner-live-sdk'
}
  

在Application的Module中添加命令依赖gradle脚本：

复制
//小视频3100-3500版本必须依赖gradle脚本，否则sdk不能正常运行，3600版本开始不需要添加`
apply from: 'https://sf3-fe-tos.pglstatp-toutiao.com/obj/pangle-empower/android/pangrowth_media/plugin_config.gradle'
AndroidManifest.xml配置：在app module的AndroidManifest.xml文件中添加：

复制
​
<!-- 这四个权限最好都申请，有助于视频推荐和ecpm -->
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
​
<provider
      android:name="com.bytedance.sdk.dp.act.DPProvider"
      android:authorities="${applicationId}.BDDPProvider"
      android:exported="false" />
3、初始化SDK
重要1：小视频2600版本开始，新客户接入时需要从穿山甲平台获取配置文件，在内容输出->接入管理模块找到对应的应用，点击「下载SDK参数配置」，然后将JSON配置文件拷贝到项目的assets文件夹下。

重要2：开发者需要自行接入并初始化穿山甲SDK，并在穿山甲SDK初始化完成回调中初始化小视频SDK。一定要注意初始化顺序：

① 在穿山甲广告SDK初始化成功的回调中(InitCallback.success)初始化小视频SDK；

初始化成功的回调：穿山甲 5600 以下版本在 TTAdSdk.init() 方法中，5600 及以上版本在 TTAdSdk.start() 方法中

② 其他聚合 SDK 需自行判断穿山甲SDK初始化完成状态，在初始化成功后再初始化小视频SDK

复制
//1. 初始化，最好放到application.onCreate()执行
val configBuilder = DPSdkConfig.Builder().debug(true)
DPSdk.init(application, "SDK_Setting_5175152.json", configBuilder.build())
4、启动SDK服务
在用户同意隐私权限后，使用SDK具体场景（如创建沉浸式小视频），必须先调用init接口，然后调用start接口，并在start结果成功回调之后才能使用具体的场景功能启动，建议在较早的时间启动Sdk

方式如下：

复制
DPSdk.start { isSuccess, message ->
    //请确保使用Sdk时Sdk已经成功启动
    //isSuccess=true表示启动成功
    //启动失败，可以再次调用启动接口（建议最多不要超过3次)
    isDPStarted = isSuccess
    Log.e(TAG, "start result=$isSuccess, msg=$message")
    Bus.getInstance().sendEvent(DPStartEvent(isSuccess))
}
建议在创建具体业务前判断小视频SDK启动状态，如下（可参考demo具体使用方式）

复制
if (DPSdk.isStartSuccess()) {
    //... 创建具体业务场景
}
启动监听器用来监听SDK服务是否启动成功

isSuccess=true表示启动服务成功
启动服务失败时，可以再次调用启动服务接口（建议最多不要超过3次)
复制
/**
 * 启动监听器
 */
public interface StartListener {
    /**
     * 启动完成回调
     *
     * @param isSuccess true启动服务成功、false启动服务失败
     * @param message 相关信息
     */
    void onStartComplete(boolean isSuccess, String message);
​
}
5、初始化配置参数说明
5.1 DPSdkConfig参数说明
复制
public final class DPSdkConfig {
    private boolean mIsDebug = false;//是否调试模式，默认false
    private IDPPrivacyController mPrivacyController;//隐私合规控制器，不建议使用
    private int mImageCacheSize;//图片缓存大小，单位为 MB
    private IDPToastController mToastController; // Toast回调监听器
    private boolean mIsNewUser = false;  // 是否新用户
    private ArticleDetailListTextStyle mFontStyle = ArticleDetailListTextStyle.FONT_NORMAL;  // 小视频3600增加，文章详情列表字体大小样式，默认为正常大小
}
5.2 IDPPrivacyController参数说明
隐私信息控制开关

复制
/**
 * 隐私协议合规控制器
 */
public abstract class IDPPrivacyController {
    /**
     * 是否允许SDK主动获取手机硬件参数，imei/imsi
     *
     * @return true可以获取，false禁止获取。默认为true
     */
    public boolean isCanUsePhoneState() {
        return true;
    }
​
    /**
     * 当isCanUsePhoneState=false时，可传入imei信息，炬焰SDK使用您传入的imei信息
     *
     * @return imei信息(TelephonyManager.getDeviceId ())
     */
    public String getImei() {
        return null;
    }
​
    /**
     * 当isCanUsePhoneState=false时，可传入imsi信息，炬焰SDK使用您传入的imsi信息
     *
     * @return imsi信息(TelephonyManager.getSubscriberId ())
     */
    public String getImsi() {
        return null;
    }
​
    /**
     * 是否允许SDK主动获取android_id参数
     *
     * @return true可以获取，false禁止获取。默认为true
     */
    public boolean isCanUseAndroidId() {
        return true;
    }
​
    /**
     * isCanUseAndroidId=false时，可传入android_id信息，炬焰SDK使用您传入的android_id信息
     *
     * @return android_id信息
     */
    public String getAndroidId() {
        return null;
    }
​
    /**
     * 是否允许SDK主动获取Mac地址
     *
     * @return
     */
    public boolean isCanUseMac() {
        return true;
    }
​
    /**
     * 是否允许SDK主动获取OAID
     *
     * @return
     */
    public boolean isCanUseOAID() {
        return true;
    }
​
    /**
     * 是否允许SDK主动获取ICCID
     *
     * @return
     */
    public boolean isCanUseICCID() {
        return true;
    }
​
    /**
     * 是否允许SDK主动获取SerialNumber
     *
     * @return
     */
    public boolean isCanUseSerialNumber() {
        return true;
    }
​
​
    /**
     * 是否允许SDK主动获取GAID
     *
     * @return
     */
    public boolean isCanUseGAID() {
        return true;
    }
​
    /**
     * 是否允许SDK主动获取运营商信息
     *
     * @return
     */
    public boolean isCanUseOperatorInfo() {
        return true;
    }
​
    /**
     * 是否青少年模式
     *
     * @return true青少年模式，false正常模式。默认为false
     */
    public boolean isTeenagerMode() {
        return false;
    }
}
5.3  IDPToastController参数说明
复制
public abstract class IDPToastController {
 
    /**
     * Toast回调，默认返回false，显示sdk默认toast
     *
     * @param context Toast上下文
     * @param msg     Toast消息内容
     * @param type    Toast类型，根据Type
     *
     * @return true：使用自定义toast；false：使用sdk默认toast
     */
    public boolean onToast(Context context, String msg, DPToastType type) {
        return false;
    }
 
}
5.4  DPUpdate说明（个性化开关）
复制
public class DPUpdate {
​
    /**
     * 设置个性化推荐开关
     *
     * @param personalRec true为开，false为关
     */
    public static void setPersonalRec(boolean personalRec);
​
    /**
     * 获取个性化开关状态
     *
     * @return true为开，false为关
     */
    public static boolean getPersonRec();
    //4700新增用户信息回传
    public static void setUserLabel(DPUserAge age, DPUserGender gender);
​
}
5.5 文章详情列表字体大小样式说明
复制
/**
 * 文章详情列表字体大小样式.
 */
public enum ArticleDetailListTextStyle {
    /**
     * 正常字体.
     */
    FONT_NORMAL,
    /**
     * 超大字体，大约为正常字体的1.3倍.
     */
    FONT_XL
}
5.6  其他配置说明
建议在接入调试时打开setDebug(true)，流媒体SDK会尽力帮您检查参数是否缺失，并给出toast提示
在使用小视频SDK时尽量保证已获取到权限：文件读写权限、read_phone权限
混淆配置已经打入aar中，不需要做额外处理
so库支持的架构是armeabi-v7a和armeabi-v8a两种
二、SDK错误码
错误码

说明

-1

sdk默认值

-2

sdk数据解析异常

0

成功

1

access_token验证失败

22

缺少必要参数

26

接口请求过于频繁

999

未知错误

8000

小视频定制频道异常



三、常见问题
Q：接入版本号小于等于0911，开发者需自行添加混淆，否则影响日志上报。其他版本不需要添加 混淆规则：

复制
-keep class com.bytedance.applog.AppLog{ public *; }
Q：系统自带fragment(android.app.Fragment)支持，要求版本不低于1.9.0.0

Q：使用androidx报错：Caused by: java.lang.ClassNotFoundException: Didn’t find class “androidx.swiperefreshlayout.widget.CircularProgressDrawable”，开发者在app级别的build.gradle下添加依赖

复制
implementation "androidx.swiperefreshlayout:swiperefreshlayout:1.1.0"

# 沉浸式小视频
一、简介
该文档介绍了沉浸式小视频接入方式。建议您最好先阅读下，然后对照demo再进行深入了解。相信看完文档和demo后基本接入应该没啥问题。如遇到未知问题请随时联系我们。

二、加载沉浸式小视频
接入方可以调用DPSdk.factory().createDraw(DPWidgetDrawParams)来获取fragment，将获取到的沉浸式小视频fragment放入到自己app中即可。

如果您想监听某些事件（播放、停止等等）详见下面的接口说明（# 2.1.5 监听器IDPDrawListener接口说明）。

1、 接口说明
1.1  获取组件工厂
复制
//代码位置：com.bytedance.sdk.dp.DPSdk
//DPSdk类是整个sdk的统一出口，业务方基本只需要关心这一个类就行
​
/**
 * 获取组件工厂，用来创建各种业务组件
 *
 * @return 组件工厂
 */
public static IDPWidgetFactory factory() {
    return DPWidgetFactoryImpl.INSTANCE;
}
1.2 创建组件
复制
/**
 * 组件工厂
 */
public interface IDPWidgetFactory {
    /**
     * 创建draw组件
     *
     * @param params 请求参数。可以为null
     * @return draw视频流组件
     */
    IDPWidget createDraw(@Nullable DPWidgetDrawParams params);
​
}
1.3 组件IDPWidget说明
复制
/**
 * fragment对外暴露接口
 */
public interface IDPWidget {
   /**
    * 获取fragment实例(support v4)
    *
    * @return fragment实例
    */
   @NonNull
   Fragment getFragment();
​
   /**
    * 获取fragment实例(安卓系统自带)
    *
    * @return fragment实例
    */
   @NonNull
   android.app.Fragment getFragment2();
​
   /**
    * 获取举报fragment实例(support v4)
    *
    * @return 举报fragment实例
    */
   @Deprecated
   @Nullable
   Fragment getReportFragment();
​
   /**
    * 获取举报fragment实例(安卓系统自带)
    *
    * @return 举报fragment实例
    */
   @Deprecated
   @Nullable
   android.app.Fragment getReportFragment2();
​
   /**
    * 刷新界面
    */
   void refresh();
​
   /**
    * 回到顶部（2.2.0.0 添加）
    */
   void scrollToTop();
​
   /**
    * back 时是否可以关闭当前 Activity（2.1.0.0 添加）
    * 用于评论页面逐级返回
    *
    * @return true:可以关闭当前页面；false:不可以关闭当前页面
    */
   boolean canBackPress();
​
   /**
    * 刷新视频，并且toast居中提醒“再按一次返回键退出”
    * 用于用户返回退出页面时挽留
    */
   void backRefresh();
​
   /**
    * 销毁资源（2.1.0.0 添加）
    * 离开该场景可以调用一下，可以避免内存泄漏
    */
   void destroy();
​
   /**
    * api接口可以设置播放数据进来
    *
    * @param data 加密的播放数据
    */
   void setAwakeData(String data);
  
   /**
    * pauseForWatchTogether与resumeForWatchTogether 一起看视频场景被控端才生效
    * pauseForWatchTogethe暂停播放
    * resumeForWatchTogether恢复播放
    */
   void resumeForWatchTogether();
   void pauseForWatchTogether();
​
    /**
     * 播放指定位置视频到指定时间点，必须主线程调用（3.5.0.0 添加）
     * 
     * @param position 当前列表位置
     * @param time 指定时间点，单位毫秒
     */
    void seekTo(int position, long time);
​
    /**
     * 设置播放特定位置的视频，必须主线程调用（3.5.0.0 添加）
     * @param position 当前列表位置
     */
    boolean setCurrentPage(int position);
​
    /**
     * 设置播放数据，必须主线程调用（3.5.0.0 添加）
     * @param data 加密数据
     * @param type 数据类型
     * @param listener 数据回调
     */
    void setSyncData(String data, @DPWidgetDrawParams.DataType  int type, IDPWidgetFactory.IEnterListener listener);
}
1.4 组件参数DPWidgetDrawParams说明
复制
/**
 * draw视频流定制参数
 */
public final class DPWidgetDrawParams {
​
   /**
    * 浅色进度条（白色），适用于深色系底tab的接入场景
    */
   public static final int PROGRESS_BAR_STYLE_LIGHT = 1;
   /**
    * 深色进度条（蓝色），适用于浅色系底tab的接入场景
    */
   public static final int PROGRESS_BAR_STYLE_DARK = 2;
   /**
    * 默认举报页面顶部Padding为64dp
    */
   private static final float DEFAULT_REPORT_TOP_PADDING = 64;
  
    /**
     * 混流只包含小视频+广告
     */
   public static final int DRAW_CONTENT_TYPE_ONLY_VIDEO = 1;
​
   public int mAdOffset; //穿山甲广告偏移（距离底部）
  
   public int mBottomOffset; // 小视频底部标题文案、进度条、评论按钮底部偏移
   public int mTitleTopMargin = -1; // 标题栏上边距
   public int mTitleLeftMargin = -1; // 标题栏左边间距
   public int mTitleRightMargin = -1; // 标题栏右边间距
​
   /**
    * 是否隐藏返回按钮：true隐藏、false显示。默认false
    */
   public boolean mIsHideClose = false;
​
   /**
    * 关闭按钮点击时回调。可以为null。默认是关闭activity
    */
   public View.OnClickListener mCloseListener;
​
   /**
    * 监听回调。可以为null
    */
   public IDPDrawListener mListener;
​
   /**
    * 广告监听回调，可以为 null（2.1.0.0 新增）
    */
   public IDPAdListener mAdListener;
​
   /**
    * Draw 视频场景，内容定制业务必传
    */
   public String mScene;
​
   /**
    * 自定义推荐频道名字，只有当推荐开关关闭时才生效
    */
   public String mCustomCategory;
​
   /**
    * 播放器进度条样式，默认为浅色样式。
    * 枚举值：{@link #PROGRESS_BAR_STYLE_LIGHT} {@link #PROGRESS_BAR_STYLE_DARK}
    */
   public int mProgressBarStyle = PROGRESS_BAR_STYLE_LIGHT;
​
   /**
    * 举报页面顶部Padding，单位dp，默认为{@link #DEFAULT_REPORT_TOP_PADDING}
    */
   public float mReportTopPadding = DEFAULT_REPORT_TOP_PADDING;
​
   public static DPWidgetDrawParams obtain() {
      return new DPWidgetDrawParams();
   }
​
   private DPWidgetDrawParams() {
​
   }
​
   /**
    * 设置广告距离底部的偏移（单位 dp）
    *
    * @param offset 广告偏移量（距离底部）
    */
   public DPWidgetDrawParams adOffset(int offset) {
      mAdOffset = offset;
      return this;
   }
​
   /**
    * 设置是否隐藏返回按钮
    *
    * @param isHideClose true隐藏、false显示。默认false（关闭当前activity）
    * @param listener    自定义点击返回按钮时回调
    */
   public DPWidgetDrawParams hideClose(boolean isHideClose, @Nullable View.OnClickListener listener) {
      mIsHideClose = isHideClose;
      mCloseListener = listener;
      return this;
   }
​
   /**
    * @param listener 小视频业务回调，可以为null
    */
   public DPWidgetDrawParams listener(@Nullable IDPDrawListener listener) {
      mListener = listener;
      return this;
   }
​
   /**
    * 设置广告相关监听
    *
    * @param listener 广告回调
    */
   public DPWidgetDrawParams adListener(@Nullable IDPAdListener listener) {
      mAdListener = listener;
      return this;
   }
​
​
   /**
    * 设置推荐频道名称，只有当推荐开关关闭时才生效
    *
    * @param customCategory 自定义频道名称
    */
   public DPWidgetDrawParams customCategory(String customCategory) {
      mCustomCategory = customCategory;
      return this;
   }
​
   /**
    * 设置播放进度条样式
    *
    * @param style 进度条样式
    */
   public DPWidgetDrawParams progressBarStyle(@ProgressBarStyle int style) {
      mProgressBarStyle = style;
      return this;
   }
​
   /**
    * 设置小视频外流底部标题文案、进度条、评论按钮底部偏移
    *
    * @param bottomOffset 底部偏移值，单位dp
    */
   public DPWidgetDrawParams bottomOffset(int bottomOffset) {
      mBottomOffset = bottomOffset;
      return this;
   }
​
   /**
    * 设置标题栏距离顶部间距
    *
    * @param offset 顶部间距，单位dp
    */
   public DPWidgetDrawParams titleTopMargin(int offset) {
      mTitleTopMargin = offset;
      return this;
   }
​
   /**
    * 设置标题栏距离左间距
    *
    * @param offset 左间距，单位dp
    */
   public DPWidgetDrawParams titleLeftMargin(int offset) {
      mTitleLeftMargin = offset;
      return this;
   }
​
   /**
    * 设置标题栏距离右间距
    *
    * @param offset 右间距，单位dp
    */
   public DPWidgetDrawParams titleRightMargin(int offset) {
      mTitleRightMargin = offset;
      return this;
   }
​
   /**
     * 混流内容
     *
     * @param mDrawContentType DRAW_CONTENT_TYPE_ONLY_VIDEO 小视频+广告
     *                       
     */
    public DPWidgetDrawParams drawContentType(int mDrawContentType) {
        this.mDrawContentType = mDrawContentType;
        return this;
    }
    
    /**
     * 设置沉浸式小视频频道，默认显示推荐+关注两个频道（3.2.0.0 添加）
     * @param drawChannelType DRAW_CHANNEL_TYPE_ALL 推荐+关注 
     *                        DRAW_CHANNEL_TYPE_RECOMMEND 推荐
     *                        DRAW_CHANNEL_TYPE_FOLLOW 关注 
     */
    public DPWidgetDrawParams drawChannelType(int drawChannelType) {
        mDrawChannelType = drawChannelType;
        return this;
    }
    
    /**
     * 是否隐藏关注功能（3.2.0.0 添加，需要同时隐藏关注频道）
     * @param hideFollow
     */
    public DPWidgetDrawParams hideFollow(boolean hideFollow) {
        mIsHideFollow = hideFollow;
        return this;
    }
    
    /**
     * 是否隐藏频道名称（3.2.0.0 添加，仅显示推荐或关注其中一个频道的时候生效）
     * @param hideChannelName
     */
    public DPWidgetDrawParams hideChannelName(boolean hideChannelName) {
        mIsHideChannelName = hideChannelName;
        return this;
    }
    
    /**
     * 是否支持下拉刷新（3.2.0.0 添加）
     *
     * @param enableRefresh true支持、false不支持
     */
    public DPWidgetDrawParams enableRefresh(boolean enableRefresh) {
        
    }
    public long mHostGroupId = -1;
    public DPRole mRole = DPRole.NONE;
    /**
     * 设置一起看视频角色（3.5.0.0 添加）
     * @param role
     * @return
     */
    public DPWidgetDrawParams role(DPRole role) {
        mRole = role;
        return this;
    }
​
    /**
     * 仅在一起看视频主控端生效，传入携带的groupid（3.5.0.0 添加）
     *
     * @param hostGroupId
     */
    public DPWidgetDrawParams hostGroupId(long hostGroupId) {
        mHostGroupId = hostGroupId;
        return this;
    }
    
    
    @IntDef({REFRESH, APPEND})
    @Retention(RetentionPolicy.SOURCE)
    @interface DataType {
    }
    
}
​
角色枚举
复制
public enum DPRole {
​
    NONE, // 普通模式
    HOST, // 主控端
    USER  // 被控端
​
}
​
1.5 监听器IDPDrawListener接口说明
复制
/**
 * draw视频流监听器。只需要重写你关心的回调即可
 */
public abstract class IDPDrawListener {
​
   /**
    * 刷新任务完成后回调（数据刷新完成）
    */
   public void onDPRefreshFinish() {
​
   }
​
   /**
    * 页面切换时回调
    *
    * @param position 页面索引值
    */
   @Deprecated
   public void onDPPageChange(int position) {
​
   }
​
   /**
    * 页面切换时回调
    *
    * @param position 页面索引值
    * @param map      附加参数
    */
   public void onDPPageChange(int position, Map<String, Object> map) {
      //map参数说明：key，类型，说明，版本
      //"group_id"，long，新闻id，2.5.0.0添加
      //"extra"，String，扩展字段，2.5.0.0 添加
   }
​
   /**
    * 视频播放时回调
    *
    * @param map 附加参数
    */
   public void onDPVideoPlay(Map<String, Object> map) {
      //map参数说明：key，类型，说明，版本
      //"group_id"，long，新闻id，1.0.0添加
      //"category_name"，String，来源，2.1.0.0 添加
      //"extra"，String，扩展字段，2.5.0.0 添加
      // "video_width", int, 视频宽度，3.2.0.0添加
      // "video_height", int, 视频长度，3.2.0.0添加
   }
​
   /**
    * 视频暂停播放时回调（2.1.0.0 添加）
    *
    * @param map 附加参数
    */
   public void onDPVideoPause(Map<String, Object> map) {
      //map参数说明：key，类型，说明，版本
      //"group_id"，long，新闻id，2.1.0.0 添加
      //"category_name"，String，来源，2.1.0.0 添加
      //"duration"，long，视频播放进度，2.2.0.0 添加
      //"extra"，String，扩展字段，2.5.0.0 添加
   }
​
   /**
    * 视频继续播放时回调（2.1.0.0 添加）
    *
    * @param map 附加参数
    */
   public void onDPVideoContinue(Map<String, Object> map) {
      //map参数说明：key，类型，说明，版本
      //"group_id"，long，新闻id，2.1.0.0 添加
      //"category_name"，String，来源，2.1.0.0 添加
      //"extra"，String，扩展字段，2.5.0.0 添加
   }
​
   /**
    * 视频播放完成时回调（包含重复播放）。
    * 2000版本添加该接口回调。
    *
    * @param map 附加参数
    */
   public void onDPVideoCompletion(Map<String, Object> map) {
      //map参数说明：key，类型，说明，版本
      //"group_id"，long，新闻id，2.0.0添加
      //"category_name"，String，来源，2.1.0.0 添加
      //"extra"，String，扩展字段，2.5.0.0 添加
      // "title"，String，标题，2.9.1.6 添加
      // "video_duration"，int，视频时长，2.9.1.6 添加
      // "video_size"，long，文件大小，2.9.1.6 添加
      // "category"，int，一级类别，2.9.1.6 添加
      // "author_name"，String，作者昵称，2.9.1.6 添加
      // "is_stick"，boolean，是否置顶，2.9.1.6 添加
      // "cover_list"，List<Image>，封面列表，2.9.1.6 添加
   }
​
   /**
    * 视频播放结束时回调
    *
    * @param map 附加参数
    */
   public void onDPVideoOver(Map<String, Object> map) {
      //map参数说明：key，类型，说明，版本
      //"group_id"，long，新闻id，1.0.0添加
      //"percent"，int，播放百分比0-100，2.1.0.0 添加
      //"duration"，long，视频总时长，2.1.0.0 添加
      //"category_name"，String，来源，2.1.0.0 添加
      //"extra"，String，扩展字段，2.5.0.0 添加
   }
​
   /**
    * 界面关闭时回调
    */
   public void onDPClose() {
​
   }
​
   /**
    * 举报结果回调（根据举报成功失败结果，隐藏、展示举报界面及提示）（1.0.0.0 新增）
    *
    * @param isSucceed 举报成功：true 举报失败：false
    */
   @Deprecated
   public void onDPReportResult(boolean isSucceed) {
​
   }
​
   /**
    * 举报结果回调（根据举报成功失败结果，隐藏、展示举报界面及提示）（2.1.0.0 添加）
    *
    * @param isSucceed 举报成功：true 举报失败：false
    * @param map       附加参数
    */
   public void onDPReportResult(boolean isSucceed, Map<String, Object> map) {
      //map参数说明：key，类型，说明，版本
      //"group_id"，long，新闻id，2.1.0.0 添加
   }
​
   /**
    * 开始请求回调（2.1.0.0 添加）
    *
    * @param map 附加参数
    */
   public void onDPRequestStart(@Nullable Map<String, Object> map) {
​
   }
​
   /**
    * 请求失败回调（2.1.0.0 添加）
    *
    * @param code 错误码
    * @param msg  错误信息
    * @param map  附加参数
    */
   public void onDPRequestFail(int code, String msg, @Nullable Map<String, Object> map) {
      // map参数说明：key，类型，说明，版本
      // "req_id"，String，请求id，2.1.0.0 添加
   }
​
   /**
    * 请求成功回调（2.1.0.0 添加）
    *
    * @param list 附加参数
    */
   public void onDPRequestSuccess(List<Map<String, Object>> list) {
      // Map<String, Object> map = list.get(i);
      // map参数说明：key，类型，说明，版本
      // "req_id"，String，请求id，2.1.0.0 添加
      // "group_id"，long，新闻id，2.1.0.0 添加
      // "title"，String，标题，2.1.0.0 添加
      // "video_duration"，int，视频时长，2.1.0.0 添加
      // "video_size"，long，文件大小，2.1.0.0 添加
      // "category"，int，一级类别，2.1.0.0 添加
      // "author_name"，String，作者昵称，2.1.0.0 添加
      // "content_type"，String，内容类型，2.9.1.6 添加，文章：text，视频：video
      // "is_stick"，boolean，是否置顶，2.9.1.6 添加
      // "cover_list"，List<Image>，封面列表，2.9.1.6 添加
   }
​
   /**
    * 点击作者头像时回调（2.1.0.0 添加）
    *
    * @param map 附加参数
    */
   public void onDPClickAvatar(Map<String, Object> map) {
      //map参数说明：key，类型，说明，版本
      //"group_id"，long，新闻id，2.1.0.0添加
      //"category_name"，String，来源，2.1.0.0 添加
   }
​
   /**
    * 点击作者昵称时回调（2.1.0.0 添加）
    *
    * @param map 附加参数
    */
   public void onDPClickAuthorName(Map<String, Object> map) {
      //map参数说明：key，类型，说明，版本
      //"group_id"，long，新闻id，2.1.0.0添加
      //"category_name"，String，来源，2.1.0.0 添加
   }
​
   /**
    * 点击评论时回调（2.1.0.0 添加）
    *
    * @param map 附加参数
    */
   public void onDPClickComment(Map<String, Object> map) {
      //map参数说明：key，类型，说明，版本
      //"group_id"，long，新闻id，2.1.0.0添加
      //"category_name"，String，来源，2.1.0.0 添加
   }
​
   /**
    * 点赞时回调（2.1.0.0 添加）
    *
    * @param isLike 是否点赞 true 点赞，false 取消点赞
    * @param map    附加参数
    */
   public void onDPClickLike(boolean isLike, Map<String, Object> map) {
      //map参数说明：key，类型，说明，版本
      //"group_id"，long，新闻id，2.1.0.0添加
      //"category_name"，String，来源，2.1.0.0 添加
   }
    /**
    * 为唤起的页面设置groupId
    * @param groupId 内容id
    */
   void setAwakeShareData(long groupId);
​
   /**
   * 点击更多-分享时的回调，用于宿主侧处理分享链接
   * @param map 附加参数
   */
   public void onDPClickShare(Map<String, Object> map) {
    //"group_id"，long，新闻id，3.1.0.0添加
    // "title"，String，标题，3.1.0.0 添加
    // "author_name"，String，作者昵称，3.1.0.0 添加
    // "cover_list"，List<Image>，封面列表，3.1.0.0 添加
    // "publish_time", long ,发布时间，3.1.0.0 添加
}
   /**
    * 页面状态回调
    * @param pageState 页面状态{@link DPPageState}
    */
   public void onDPPageStateChanged(DPPageState pageState) {
​
   }
}
​
复制
/**
 * 3.5.0.0新增一起看视频回调
 */
public abstract class IDPDrawListener extends IDPBaseListener {
​
    /**
     * 列表数据变化回调
     */
    public void onDPListDataChange(Map<String, Object> map) {
        // map参数说明：key，类型，说明，版本
        // data, String, 数据源加密字符串
        // type，String, 1刷新2追加
    }
​
    /**
     * 用户拖动进度条松手时回调
     *
     * @param position 视频位置
     * @param time 毫秒
     */
    public void onDPSeekTo(int position, long time) {
​
    }
}
1.6 广告监听器 IDPAdListener 接口说明
复制
/**
 * 广告监听器。只需要重写你关心的回调即可
 */
public abstract class IDPAdListener {
​
   /**
    * 广告请求
    *
    * @param map 附加参数
    */
   public void onDPAdRequest(Map<String, Object> map) {
      //map参数说明：key，类型，说明，版本
      //"ad_id"，String，广告位 id，2.1.0.0添加
   }
​
   /**
    * 广告请求成功
    *
    * @param map 附加参数
    */
   public void onDPAdRequestSuccess(Map<String, Object> map) {
      //map参数说明：key，类型，说明，版本
      //"ad_id"，String，广告位 id，2.1.0.0添加
      //"ad_count"，int，广告返回个数，2.1.0.0添加
      //"request_id"，String，广告请求 id，2.2.0.0 添加
   }
​
   /**
    * 广告请求失败
    *
    * @param code 错误码
    * @param msg  错误信息
    * @param map  附加参数
    */
   public void onDPAdRequestFail(int code, String msg, Map<String, Object> map) {
      //map参数说明：key，类型，说明，版本
      //"ad_id"，String，广告位 id，2.1.0.0添加
   }
​
   /**
    * 广告填充失败
    *
    * @param map 附加参数
    */
   public void onDPAdFillFail(Map<String, Object> map) {
      //map参数说明：key，类型，说明，版本
      //"ad_id"，String，广告位 id，2.1.0.0添加
      //"ad_first_pos"，int，首个广告的位置，2.1.0.0添加
      //"ad_follow_sep"，int，非首个广告的间隔，2.1.0.0添加
      //"ad_follow_pos"，int，广告在广告间隔中的位置，2.1.0.0添加
      //举个例子："ad_first_pos"、"ad_follow_sep"、"ad_follow_pos" 分别为 3，3，3
      //意思是第一个广告在第三个位置，接下来，每三条数据，第三个位置放广告
   }
​
   /**
    * 广告曝光
    *
    * @param map 附加参数
    */
   public void onDPAdShow(Map<String, Object> map) {
      //map参数说明：key，类型，说明，版本
      //"ad_id"，String，广告位 id，2.1.0.0添加
      //"request_id"，String，广告请求 id，2.2.0.0 添加
      //"open_ad_"，String，广告扩展字段 id，2.2.0.1 添加
   }
​
   /**
    * 广告开始播放
    *
    * @param map 附加参数
    */
   public void onDPAdPlayStart(Map<String, Object> map) {
      //map参数说明：key，类型，说明，版本
      //"ad_id"，String，广告位 id，2.1.0.0添加
      //"request_id"，String，广告请求 id，2.2.0.0 添加
      //"open_ad_"，String，广告扩展字段 id，2.2.0.1 添加
      // "ad_unique_id", String， 广告位唯一id，3.0.0.0添加
      // "total_duration", long, 总时长，3.0.0.0添加
   }
​
   /**
    * 广告暂停播放.
    *
    * @param map 附加参数
    */
   public void onDPAdPlayPause(Map<String, Object> map) {
      //map参数说明：key，类型，说明，版本
      //"ad_id"，String，广告位 id，2.1.0.0添加
      //"request_id"，String，广告请求 id，2.2.0.0 添加
      //"open_ad_"，String，广告扩展字段 id，2.2.0.1 添加
      // "ad_unique_id", String， 广告位唯一id，3.0.0.0添加
      // "total_duration", long, 总时长，3.0.0.0添加
      // "current_duration", long, 当前播放时长， 3.0.0.0添加
   }
​
   /**
    * 广告继续播放
    *
    * @param map 附加参数
    */
   public void onDPAdPlayContinue(Map<String, Object> map) {
      //map参数说明：key，类型，说明，版本
      //"ad_id"，String，广告位 id，2.1.0.0添加
      //"request_id"，String，广告请求 id，2.2.0.0 添加
      //"open_ad_"，String，广告扩展字段 id，2.2.0.1 添加
      // "ad_unique_id", String， 广告位唯一id，3.0.0.0添加
   }
​
   /**
    * 广告播放结束.
    *
    * @param map 附加参数
    */
   public void onDPAdPlayComplete(Map<String, Object> map) {
      //map参数说明：key，类型，说明，版本
      //"ad_id"，String，广告位 id，2.1.0.0添加
      //"request_id"，String，广告请求 id，2.2.0.0 添加
      //"open_ad_"，String，广告扩展字段 id，2.2.0.1 添加
      // "ad_unique_id", String， 广告位唯一id，3.0.0.0添加
      // "total_duration", long, 总时长，3.0.0.0添加
   }
​
   /**
    * 广告点击
    *
    * @param map 附加参数
    */
   public void onDPAdClicked(Map<String, Object> map) {
      //map参数说明：key，类型，说明，版本
      //"ad_id"，String，广告位 id，2.1.0.0添加
      //"request_id"，String，广告请求 id，2.2.0.0 添加
      //"open_ad_"，String，广告扩展字段 id，2.2.0.1 添加
   }
}
​
​
2、代码片段示例
代码来自demo：com.bytedance.dpdemo.activity.video.draw.DrawVideoFullScreenActivity

复制
private void initDrawWidget() {
    mIDPWidget = DPHolder.getInstance().buildDrawWidget(DPWidgetDrawParams.obtain()
            .hideClose(false, null)
            .listener(new IDPDrawListener() {
                @Override
                public void onDPRefreshFinish() {
                    log("onDPRefreshFinish");
                }
​
                @Override
                public void onDPPageChange(int position) {
                    log("onDPPageChange: " + position);
                }
​
                @Override
                public void onDPVideoPlay(Map<String, Object> map) {
                    log("onDPVideoPlay");
                }
​
                @Override
                public void onDPVideoOver(Map<String, Object> map) {
                    log("onDPVideoOver");
                }
​
                @Override
                public void onDPClose() {
                    log("onDPClose");
                }
                
                @Override
                public void onDPRequestStart() {
                     log("onDPRequestStart");
                }
                
                @Override
                public void onDPRequestResult(boolean isSucceed, List<Object> list) {
                    log("onDPRequestResult");
                }
                
                @Override
                public void onDPClickAuthorName(Map<String, Object> map) {
                    log("onDPClickAuthorName");
                }
                
                @Override
                public void onDPClickAvatar(Map<String, Object> map) {
                    log("onDPClickAvatar");
                }
                
                @Override
                public void onDPClickComment(Map<String, Object> map) {
                    log("onDPClickComment");
                }
                
                @Override
                public void onDPClickLike(boolean isLike, Map<String, Object> map) {
                    log("onDPClickLike");
                }
                
                @Override
                public void onDPVideoPause(Map<String, Object> map) {
                    log("onDPVideoPause");
                }
                
                @Override
                public void onDPVideoContinue(Map<String, Object> map) {
                    log("onDPVideoContinue");
                }
            }));
}
​
三、举报功能
接入方可以调用DPSdk.factory().createDraw(DPWidgetDrawParams).getReportFragment()来获取举报 fragment，将获取到的举报 fragment 放入到自己 app 中即可。

代码片段示例
代码来自demo：com.bytedance.dpdemo.activity.video.draw.DrawVideoFullScreenActivity

复制
private void initDrawWidget() {
    mIDPWidget = DPHolder.getInstance().buildDrawWidget(DPWidgetDrawParams.obtain()
            .adOffset(49) //单位 dp
            .hideClose(false, null)
            .listener(new IDPDrawListener() {
                @Override
                public void onDPRefreshFinish() {
                    log("onDPRefreshFinish");
                }
​
                @Override
                public void onDPPageChange(int position) {
                    log("onDPPageChange: " + position);
                }
​
                @Override
                public void onDPVideoPlay(Map<String, Object> map) {
                    log("onDPVideoPlay");
                }
​
                @Override
                public void onDPVideoOver(Map<String, Object> map) {
                    log("onDPVideoOver");
                }
​
                @Override
                public void onDPClose() {
                    log("onDPClose");
                }
                
                @Override
                public void onDPReportResult(boolean isSucceed) {
                    log("onDPReportResult");
                    if (isSucceed) {
                        Toast.makeText(DrawVideoStyle1Activity.this, "举报成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(DrawVideoStyle1Activity.this, "举报失败，请稍后再试", Toast.LENGTH_SHORT).show();
                    }
                }
            }));
}
​
Fragment reportFragment = mIDPWidget.getReportFragment();
​
四、常见问题
fragment嵌套小视频fragment，这样会导致内部小视频fragment生命周期回调有问题，需要外部开发者自行调用生命周期回调（onResume、onPause、setUserVisibleHint、onHiddenChanged）。
代码示例：

复制
      @Override
       public void onResume() {
           super.onResume();
           if (mIDPWidget.getFragment() != null) {
               mIDPWidget.getFragment().onResume();
           }
       }
   
       @Override
       public void onPause() {
           super.onPause();
           if (mIDPWidget.getFragment() != null) {
               mIDPWidget.getFragment().onPause();
           }
       }
   
       @Override
       public void setUserVisibleHint(boolean isVisibleToUser) {
           super.setUserVisibleHint(isVisibleToUser);
           if (mIDPWidget.getFragment() != null) {
               mIDPWidget.getFragment().setUserVisibleHint(isVisibleToUser);
           }
       }
   
       @Override
       public void onHiddenChanged(boolean hidden) {
           super.onHiddenChanged(hidden);
           if (mIDPWidget.getFragment() != null) {
               mIDPWidget.getFragment().onHiddenChanged(hidden);
           }
       }
​
若您是fragment嵌套小视频fragment，并且将您的fragment放置在ViewPager中，请在「创建小视频fragment之后、将fragment添加到FragmentManager之前」，调用一下IDPWidget.getFragment().setUserVisibleHint(isVisibleToUser);

代码示例：

复制
private var userVisible = false
override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        userVisible = isVisibleToUser
        dpWidget?.fragment?.userVisibleHint = isVisibleToUser
}
private fun initDrawWidget() {
        //创建小视频fragment
        dpWidget = DPHolder.getInstance().buildDrawWidget(
            DPWidgetDrawParams.obtain()
          //在这里主动setUserVisibleHint
        dpWidget?.fragment?.userVisibleHint = userVisible
          //添加到FragmentManager
        dpWidget?.let {
            childFragmentManager.beginTransaction().apply {
                replace(R.id.fl_container, it.fragment)
                this.commitNow()
            }
        }
    }

# 个人主页
一、简介
该文档介绍了个人主页组件的接入方式。建议您最好先阅读下，然后对照demo再进行深入了解。相信看完文档和demo后基本接入应该没啥问题。如遇到未知问题请随时联系我们。

二、加载个人主页组件
接入方可以调用DPSdk.factory().create(DPWidgetUserProfileParam)来获取fragment，将获取到的个人主页fragment放入到自己app中即可。

请注意：一定要在SDK初始化完成之后才能创建个人主页组件。

如果您想监听某些事件（视频播放、停止等等）详见下面的接口说明（# 2.1.5 监听器IDPDrawListener接口说明）。

1、接口说明
1.1 获取组件工厂
复制
//代码位置：com.bytedance.sdk.dp.DPSdk
//DPSdk类是整个sdk的统一出口，业务方基本只需要关心这一个类就行
​
/**
 * 获取组件工厂，用来创建各种业务组件
 *
 * @return 组件工厂
 */
public static IDPWidgetFactory factory() {
    return DPWidgetFactoryImpl.INSTANCE;
}
​
​
1.2 创建组件
复制
  /**
     * 创建param对应的内容.
     * 
     * DPWidgetUserProfileParam 对应个人主页组件
     * 
     * @param param 配置参数，不可为空.
     */
    <Param extends DPWidgetParam> IDPWidget create(@NonNull Param param);
​
​
1.3 组件IDPWidget说明
复制
/**
 * fragment对外暴露接口
 */
public interface IDPWidget {
    /**
     * 获取fragment实例
     *
     * @return fragment实例
     */
    @NonNull
    Fragment getFragment();
    
    /**
     * 获取 android.app.Fragment 实例(安卓系统自带)
     *
     * @return fragment实例
     */
     @NonNull
     android.app.Fragment getFragment2();
​
    /**
     * 获取举报 fragment 实例（1.0.0.0 新增）
     *
     * @return 举报 fragment 实例
     */
    @Nullable
    Fragment getReportFragment();
    
    /**
     * 获取举报fragment实例(安卓系统自带)
     *
     * @return 举报fragment实例
     */
    @Nullable
    android.app.Fragment getReportFragment2();
​
    /**
     * 刷新界面
     */
    void refresh();
​
    /**
     * 刷新视频，并且toast居中提醒“再按一次返回键退出”
     * 用于用户返回退出页面时挽留
     */
    void backRefresh();
​
    /**
     * back 时是否可以关闭当前Activity（2.1.0.0 添加）
     * 用于评论页面逐级返回
     *
     * @return true:可以关闭当前页面；false:不可以关闭当前页面
     */
    boolean canBackPress();
​
    /**
     * 销毁资源（2.1.0.0 添加）
     * 离开该场景可以调用一下，可以避免内存泄漏
     */
    void destroy();
}
​
​
1.4 组件参数DPWidgetUserProfileParam说明
复制
/**
 * 个人主页的配置参数.
 */
public class DPWidgetUserProfileParam extends DPWidgetParam {
    /**
     * 页面内容，详见PageType说明.
     */
    public PageType mPageType = PageType.USER_HOME_PAGE; 
    /**
     * 是否隐藏关闭按钮：true隐藏，false显示。默认false
     */
    public boolean mHideCloseIcon = false;
    /**
     * 组件宽度. 不设置时默认match_parent.
     */
    public int mWidth;
    /**
     * 组件宽度. 不设置时默认match_parent.
     */
    public int mHeight;
    /**
     * 业务场景.
     */
    public String mScene;
​
    /**
     * 监听回调.
     */
    public IDPDrawListener mIDPDrawListener;
​
    private DPWidgetUserProfileParam() {
    }
​
    /**
     * 获取配置参数对象实例.
     */
    public static DPWidgetUserProfileParam get() {
        return new DPWidgetUserProfileParam();
    }
​
    /**
     * 设置隐藏左上角返回键.
     *
     * @param hideCloseIcon true隐藏，false不隐藏.
     */
    public DPWidgetUserProfileParam hideCloseIcon(boolean hideCloseIcon) {
        this.mHideCloseIcon = hideCloseIcon;
        return this;
    }
​
    /**
     * 设置组件高度.
     */
    public DPWidgetUserProfileParam height(int height) {
        this.mHeight = height;
        return this;
    }
​
    /**
     * 设置场景.
     */
    public DPWidgetUserProfileParam scene(String scene) {
        this.mScene = scene;
        return this;
    }
​
    /**
     * 监听回调.
     */
    public DPWidgetUserProfileParam listener(IDPDrawListener listener) {
        this.mIDPDrawListener = listener;
        return this;
    }
​
    /**
     * 设置获取的组件类型
     *
     * @param pageType {@link PageType} <li>USER_HOME_PAGE 个人主页</li><li>USER_FAVORITE_VIDEO_PAGE 喜欢的视频页</li><li>USER_FOCUS_PAGE 关注页</li><br>默认为USER_HOME_PAGE.
     */
    public DPWidgetUserProfileParam pageType(PageType pageType) {
        this.mPageType = pageType;
        return this;
    }
​
    /**
     * 设置组件宽度.
     */
    public DPWidgetUserProfileParam width(int width) {
        this.mWidth = width;
        return this;
    }
​
    @Override
    public String toString() {
        return "mHideCloseIcon = " + mHideCloseIcon + ", mPageType = " + mPageType + ", mWidth = " + mWidth + ", mHeight = " + mHeight;
    }
​
    public enum PageType {
        USER_HOME_PAGE, USER_FAVORITE_VIDEO_PAGE, USER_FOCUS_PAGE, USER_DRAMA_HISTORY_PAGE
    }
​
    public enum PageType {
        /**
         * 完整个人主页内容组件，包含喜欢的视频和关注的用户列表.
         */
        USER_HOME_PAGE,
        /**
         * 仅包含喜欢的视频.
         */
        USER_FAVORITE_VIDEO_PAGE, 
        /**
         * 仅包含关注的用户列表.
         */
        USER_FOCUS_PAGE
    }
}
​
1.5 监听器IDPDrawListener接口说明
复制
/**
 * draw视频流监听器。只需要重写你关心的回调即可
 */
public abstract class IDPDrawListener {
​
    /**
     * 刷新任务完成后回调（数据刷新完成）
     */
    public void onDPRefreshFinish() {
​
    }
​
    /**
     * 页面切换时回调
     *
     * @param position 页面索引值
     */
    public void onDPPageChange(int position) {
​
    }
​
    /**
     * 视频播放时回调
     *
     * @param map 附加参数
     */
    public void onDPVideoPlay(Map<String, Object> map) {
        //map参数说明：key，类型，说明，版本
        //"group_id"，long，新闻id，1.0.0添加
        //"category_name"，String，来源，2.1.0.0 添加
    }
​
    /**
     * 视频暂停播放时回调（2.1.0.0 添加）
     *
     * @param map 附加参数
     */
    public void onDPVideoPause(Map<String, Object> map) {
        //map参数说明：key，类型，说明，版本
        //"group_id"，long，新闻id，2.1.0.0 添加
        //"category_name"，String，来源，2.1.0.0 添加
        //"duration"，long，视频播放进度，2.2.0.0 添加
    }
    
    /**
     * 视频继续播放时回调（2.1.0.0 添加）
     *
     * @param map 附加参数
     */
    public void onDPVideoContinue(Map<String, Object> map) {
        //map参数说明：key，类型，说明，版本
        //"group_id"，long，新闻id，2.1.0.0 添加
        //"category_name"，String，来源，2.1.0.0 添加
    }
​
    /**
     * 视频播放结束时回调
     *
     * @param map 附加参数
     */
    public void onDPVideoOver(Map<String, Object> map) {
       //map参数说明：key，类型，说明，版本
        //"group_id"，long，新闻id，1.0.0添加
        //"percent"，int，播放百分比0-100，2.1.0.0 添加
        //"duration"，long，视频总时长，2.1.0.0 添加
        //"category_name"，String，来源，2.1.0.0 添加
    }
    
    /**
     * 视频播放完成时回调（包含重复播放）。
     * 2000版本添加该接口回调。
     *
     * @param map 附加参数
     */
    public void onDPVideoCompletion(Map<String, Object> map) {
        //map参数说明：key，类型，说明，版本
        //"group_id"，long，新闻id，2.0.0添加
        //"category_name"，String，来源，2.1.0.0 添加
    }
​
    /**
     * 界面关闭时回调
     */
    public void onDPClose() {
​
    }
    
    /**
     * 举报结果回调（根据举报成功失败结果，隐藏、展示举报界面及提示）（1.0.0.0 新增）
     *
     * @param isSucceed 举报成功：true 举报失败：false
     */
    @Deprecated
    public void onDPReportResult(boolean isSucceed) {
    
    }
​
    /**
     * 举报结果回调（根据举报成功失败结果，隐藏、展示举报界面及提示）（2.1.0.0 新增）
     *
     * @param isSucceed 举报成功：true 举报失败：false
     * @param map       附加参数
     */
    public void onDPReportResult(boolean isSucceed, Map<String, Object> map) {
        //map参数说明：key，类型，说明，版本
        //"group_id"，long，新闻id，2.1.0.0 添加
    }
​
    /**
     * 开始请求回调（2.1.0.0 添加）
     *
     * @param map 附加参数
     */
    public void onDPRequestStart(@Nullable Map<String, Object> map) {
    
    }
​
    /**
     * 请求失败回调（2.1.0.0 添加）
     *
     * @param code 错误码
     * @param msg  错误信息
     * @param map  附加参数
     */
    public void onDPRequestFail(int code, String msg, @Nullable Map<String, Object> map) {
        // map参数说明：key，类型，说明，版本
        // "req_id"，String，请求id，2.1.0.0 添加
    }
​
    /**
     * 请求成功回调（2.1.0.0 添加）
     *
     * @param list 附加参数
     */
    public void onDPRequestSuccess(List<Map<String, Object>> list) {
        // Map<String, Object> map = list.get(i);
        // map参数说明：key，类型，说明，版本
        // "req_id"，String，请求id，2.1.0.0 添加
        // "group_id"，long，新闻id，2.1.0.0 添加
        // "title"，String，标题，2.1.0.0 添加
        // "video_duration"，int，视频时长，2.1.0.0 添加
        // "video_size"，long，文件大小，2.1.0.0 添加
        // "category"，int，一级类别，2.1.0.0 添加
        // "author_name"，String，作者昵称，2.1.0.0 添加
    }
    
    /**
     * 点击作者头像时回调（2.1.0.0 添加）
     *
     * @param map 附加参数
     */
    public void onDPClickAvatar(Map<String, Object> map) {
        //map参数说明：key，类型，说明，版本
        //"group_id"，long，新闻id，2.1.0.0添加
        //"category_name"，String，来源，2.1.0.0 添加
    }
    
    /**
     * 点击作者昵称时回调（2.1.0.0 添加）
     *
     * @param map 附加参数
     */
    public void onDPClickAuthorName(Map<String, Object> map) {
        //map参数说明：key，类型，说明，版本
        //"group_id"，long，新闻id，2.1.0.0添加
        //"category_name"，String，来源，2.1.0.0 添加
    }
    
    /**
     * 点击评论时回调（2.1.0.0 添加）
     *
     * @param map 附加参数
     */
    public void onDPClickComment(Map<String, Object> map) {
        //map参数说明：key，类型，说明，版本
        //"group_id"，long，新闻id，2.1.0.0添加
        //"category_name"，String，来源，2.1.0.0 添加
    }
    
    /**
     * 点赞时回调（2.1.0.0 添加）
     *
     * @param isLike 是否点赞 true 点赞，false 取消点赞
     * @param map    附加参数
     */
    public void onDPClickLike(boolean isLike, Map<String, Object> map) {
        //map参数说明：key，类型，说明，版本
        //"group_id"，long，新闻id，2.1.0.0添加
        //"category_name"，String，来源，2.1.0.0 添加
    }
}
​
2、代码片段示例
代码来自demo：com.bytedance.dpdemo.activity.video.HomePageActivity

复制
IDPWidget widget = DPWidgetFactoryImpl.INSTANCE.create(DPWidgetUserProfileParam.get().listener(new IDPDrawListener() {
            @Override
            public void onDPRefreshFinish() {
                log("onDPRefreshFinish");
​
            }
​
            @Override
            public void onDPPageChange(int position) {
                log("onDPPageChange: " + position);
​
            }
​
            @Override
            public void onDPVideoPlay(Map<String, Object> map) {
                log("onDPVideoPlay");
​
            }
​
            @Override
            public void onDPVideoCompletion(Map<String, Object> map) {
                log("onDPVideoCompletion: ");
​
            }
​
            @Override
            public void onDPVideoOver(Map<String, Object> map) {
                log("onDPVideoOver");
​
            }
​
            @Override
            public void onDPClose() {
                log("onDPClose");
​
            }
​
            @Override
            public void onDPReportResult(boolean isSucceed) {
                log("onDPReportResult");
            }
​
            @Override
            public void onDPReportResult(boolean isSucceed, Map<String, Object> map) {
                log("onDPReportResult");
            }
​
            @Override
            public void onDPRequestStart(@Nullable Map<String, Object> map) {
                log("onDPRequestStart");
            }
​
            @Override
            public void onDPRequestSuccess(List<Map<String, Object>> list) {
                log("onDPRequestSuccess");
            }
​
            @Override
            public void onDPRequestFail(int code, String msg, @Nullable Map<String, Object> map) {
                log("onDPRequestFail");
            }
​
            @Override
            public void onDPClickAuthorName(Map<String, Object> map) {
                log("onDPClickAuthorName");
            }
​
            @Override
            public void onDPClickAvatar(Map<String, Object> map) {
                log("onDPClickAvatar");
            }
​
            @Override
            public void onDPClickComment(Map<String, Object> map) {
                log("onDPClickComment");
            }
​
            @Override
            public void onDPClickLike(boolean isLike, Map<String, Object> map) {
                log("onDPClickLike");
            }
​
            @Override
            public void onDPVideoPause(Map<String, Object> map) {
                log("onDPVideoPause");
            }
​
            @Override
            public void onDPVideoContinue(Map<String, Object> map) {
                log("onDPVideoContinue");
            }
        }));
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, widget.getFragment(), "home_page").commitAllowingStateLoss();
    }
​
三、常见问题
fragment嵌套小视频fragment，这样会导致内部小视频fragment生命周期回调有问题，需要外部开发者自行调用生命周期回调（onResume、onPause、setUserVisibleHint、onHiddenChanged）。

代码示例：

复制
 @Override
       public void onResume() {
           super.onResume();
           if (mIDPWidget.getFragment() != null) {
               mIDPWidget.getFragment().onResume();
           }
       }
   
       @Override
       public void onPause() {
           super.onPause();
           if (mIDPWidget.getFragment() != null) {
               mIDPWidget.getFragment().onPause();
           }
       }
   
       @Override
       public void setUserVisibleHint(boolean isVisibleToUser) {
           super.setUserVisibleHint(isVisibleToUser);
           if (mIDPWidget.getFragment() != null) {
               mIDPWidget.getFragment().setUserVisibleHint(isVisibleToUser);
           }
       }
   
       @Override
       public void onHiddenChanged(boolean hidden) {
           super.onHiddenChanged(hidden);
           if (mIDPWidget.getFragment() != null) {
               mIDPWidget.getFragment().onHiddenChanged(hidden);
           }
       }
       
# 宫格小视频
一、简介
该文档介绍了宫格小视频接入方式。建议您最好先阅读下，然后对照 demo 再进行深入了解。相信看完文档和 demo 后基本接入应该没啥问题。如遇到未知问题请随时联系我们。

二、加载宫格小视频
1、宫格
接入方可以调用DPSdk.factory().createGrid(DPWidgetGridParams)来获取 fragment，将获取到的宫格小视频 fragment 放入到自己 app 中即可。

2、双Feed流
接入方可以调用DPSdk.factory().createDoubleFeed(DPWidgetGridParams)来获取 fragment，将获取到的双Feed小视频 fragment 放入到自己 app 中即可。

如果您想监听某些事件（播放、停止等等）详见下面的接口说明（# 2.1.5 监听器 IDPGridListener 接口说明）。

3、接口说明
3.1 获取组件工厂
复制
//代码位置：com.bytedance.sdk.dp.DPSdk
//DPSdk 类是整个 sdk 的统一出口，业务方基本只需要关心这一个类就行
​
/**
 * 获取组件工厂，用来创建各种业务组件
 *
 * @return 组件工厂
 */
public static IDPWidgetFactory factory() {
    return DPWidgetFactoryImpl.INSTANCE;
}
​
3.2 创建组件
复制
/**
 * 组件工厂
 */
public interface IDPWidgetFactory {
    /**
     * 创建 grid 组件
     *
     * @param params 请求参数。可以为null
     * @return grid 视频流组件
     */
    IDPWidget createGrid(@Nullable DPWidgetGridParams params);
​
}
​
3.3 组件IDPWidget说明
复制
/**
 * fragment 对外暴露接口
 */
public interface IDPWidget {
   /**
    * 获取fragment实例(support v4)
    *
    * @return fragment实例
    */
   @NonNull
   Fragment getFragment();
​
   /**
    * 获取fragment实例(安卓系统自带)
    *
    * @return fragment实例
    */
   @NonNull
   android.app.Fragment getFragment2();
​
   /**
    * 获取举报fragment实例(support v4)
    *
    * @return 举报fragment实例
    */
   @Deprecated
   @Nullable
   Fragment getReportFragment();
​
   /**
    * 获取举报fragment实例(安卓系统自带)
    *
    * @return 举报fragment实例
    */
   @Deprecated
   @Nullable
   android.app.Fragment getReportFragment2();
​
   /**
    * 刷新界面
    */
   void refresh();
​
   /**
    * 回到顶部（2.2.0.0 添加）
    */
   void scrollToTop();
​
   /**
    * back 时是否可以关闭当前 Activity（2.1.0.0 添加）
    * 用于评论页面逐级返回
    *
    * @return true:可以关闭当前页面；false:不可以关闭当前页面
    */
   boolean canBackPress();
​
   /**
    * 刷新视频，并且toast居中提醒“再按一次返回键退出”
    * 用于用户返回退出页面时挽留
    */
   void backRefresh();
​
   /**
    * 销毁资源（2.1.0.0 添加）
    * 离开该场景可以调用一下，可以避免内存泄漏
    */
   void destroy();
}
​
3.4 组件参数 DPWidgetGridParams 说明
复制
/**
 * grid 视频流定制参数
 */
public final class DPWidgetGridParams {
​
   /**
    * 宫格卡片普通样式
    */
   public static final int CARD_NORMAL_STYLE = 1;
​
   /**
    * 宫格卡片瀑布流样式
    */
   public static final int CARD_STAGGERED_STYLE = 2;
​
   /**
    * 默认举报页面顶部Padding为64dp
    */
   private static final float DEFAULT_REPORT_TOP_PADDING = 64;
​
   /**
    * 宫格卡片样式，默认为普通样式
    * 枚举值：{@link #CARD_NORMAL_STYLE} {@link #CARD_STAGGERED_STYLE}
    */
   public int mCardStyle = CARD_NORMAL_STYLE;
​
   /**
    * 监听回调。可以为 null
    */
   public IDPGridListener mListener;
​
   /**
    * 广告监听回调，可以为 null（2.1.0.0 新增）
    */
   public IDPAdListener mAdListener;
​
   /**
    * 宫格场景，内容定制业务必传
    */
   public String mScene;
​
   /**
    * 举报页面顶部Padding，单位dp，默认为{@link #DEFAULT_REPORT_TOP_PADDING}
    */
   public float mReportTopPadding = DEFAULT_REPORT_TOP_PADDING;
​
   public static DPWidgetGridParams obtain() {
      return new DPWidgetGridParams();
   }
​
   private DPWidgetGridParams() {
​
   }
​
   public DPWidgetGridParams listener(@Nullable IDPGridListener listener) {
      mListener = listener;
      return this;
   }
​
   /**
    * 设置宫格卡片样式，默认为普通卡片样式
    *
    * @param cardStyle 卡片样式
    */
   public DPWidgetGridParams cardStyle(@CardStyle int cardStyle) {
      mCardStyle = cardStyle;
      return this;
   }
​
   /**
    * 设置广告相关监听（2.1.0.0 新增）
    *
    * @param listener
    * @return
    */
   public DPWidgetGridParams adListener(@Nullable IDPAdListener listener) {
      mAdListener = listener;
      return this;
   }
​
   /**
    * 设置宫格场景（2.5.0.0 添加）
    *
    * @param scene 宫格场景，内容定制业务必传
    */
   public DPWidgetGridParams scene(String scene) {
      this.mScene = scene;
      return this;
   }
   
    /**
     * 是否支持下拉刷新（3.2.0.0 添加）
     *
     * @param enableRefresh true支持、false不支持
     */
    public DPWidgetGridParams enableRefresh(boolean enableRefresh) {
        
    }
​
}
​
3.5 监听器IDPDrawListener接口说明
复制
 /**
 * grid 视频流监听器。只需要重写你关心的回调即可
 */
public abstract class IDPGridListener {
​
    /**
     * 刷新任务完成后回调（数据刷新完成）
     */
    public void onDPRefreshFinish() {
​
    }
​
    /**
     * 宫格 item 点击时回调
     *
     * @param map 附加参数
     */
    public void onDPGridItemClick(Map<String, Object> map) {
        //map参数说明：key，类型，说明，版本
        //"group_id"，long，新闻id，1.0.0添加
    }
​
    /**
     * 宫格展示时回调（2.1.0.0 添加）
     *
     * @param map 附加参数
     */
    public void onDPClientShow(@Nullable Map<String, Object> map) {
    }
​
   /**
     * 视频播放时回调（2.1.0.0 添加）
     *
     * @param map 附加参数
     */
    public void onDPVideoPlay(Map<String, Object> map) {
        //map参数说明：key，类型，说明，版本
        //"group_id"，long，新闻id，2.1.0.0 添加
        //"category_name"，String，来源，2.1.0.0 添加
    }
​
    /**
     * 视频暂停播放时回调（2.1.0.0 添加）
     *
     * @param map 附加参数
     */
    public void onDPVideoPause(Map<String, Object> map) {
        //map参数说明：key，类型，说明，版本
        //"group_id"，long，新闻id，2.1.0.0 添加
        //"category_name"，String，来源，2.1.0.0 添加
        //"duration"，long，视频播放进度，2.2.0.0 添加
    }
    
    /**
     * 视频继续播放时回调（2.1.0.0 添加）
     *
     * @param map 附加参数
     */
    public void onDPVideoContinue(Map<String, Object> map) {
        //map参数说明：key，类型，说明，版本
        //"group_id"，long，新闻id，2.1.0.0 添加
        //"category_name"，String，来源，2.1.0.0 添加
    }
​
    /**
     * 视频播放结束时回调（2.1.0.0 添加）
     *
     * @param map 附加参数
     */
    public void onDPVideoOver(Map<String, Object> map) {
       //map参数说明：key，类型，说明，版本
        //"group_id"，long，新闻id，2.1.0.0 添加
        //"percent"，int，播放百分比0-100，2.1.0.0 添加
        //"duration"，long，视频总时长，2.1.0.0 添加
        //"category_name"，String，来源，2.1.0.0 添加
    }
    
    /**
     * 视频播放完成时回调（包含重复播放）（2.1.0.0 添加）
     *
     * @param map 附加参数
     */
    public void onDPVideoCompletion(Map<String, Object> map) {
        //map参数说明：key，类型，说明，版本
        //"group_id"，long，新闻id，2.1.0.0 添加
        //"category_name"，String，来源，2.1.0.0 添加
    }
    
    /**
     * 开始请求回调（2.1.0.0 添加）
     *
     * @param map 附加参数
     */
    public void onDPRequestStart(@Nullable Map<String, Object> map) {
    
    }
    
    /**
     * 请求失败回调（2.1.0.0 添加）
     *
     * @param code 错误码
     * @param msg  错误信息
     * @param map  附加参数
     */
    public void onDPRequestFail(int code, String msg, @Nullable Map<String, Object> map) {
        // map参数说明：key，类型，说明，版本
        // "req_id"，String，请求id，2.1.0.0 添加
    }
    
    /**
     * 请求成功回调（2.1.0.0 添加）
     *
     * @param list 附加参数
     */
    public void onDPRequestSuccess(List<Map<String, Object>> list) {
        // Map<String, Object> map = list.get(i);
        // map参数说明：key，类型，说明，版本
        // "req_id"，String，请求id，2.1.0.0 添加
        // "group_id"，long，新闻id，2.1.0.0 添加
        // "title"，String，标题，2.1.0.0 添加
        // "video_duration"，int，视频时长，2.1.0.0 添加
        // "video_size"，long，文件大小，2.1.0.0 添加
        // "category"，int，一级类别，2.1.0.0 添加
        // "author_name"，String，作者昵称，2.1.0.0 添加
    }
    
    /**
     * 点击作者头像时回调（2.1.0.0 添加）
     *
     * @param map 附加参数
     */
    public void onDPClickAvatar(Map<String, Object> map) {
        //map参数说明：key，类型，说明，版本
        //"group_id"，long，新闻id，2.1.0.0添加
        //"category_name"，String，来源，2.1.0.0 添加
    }
    
    /**
     * 点击作者昵称时回调（2.1.0.0 添加）
     *
     * @param map 附加参数
     */
    public void onDPClickAuthorName(Map<String, Object> map) {
        //map参数说明：key，类型，说明，版本
        //"group_id"，long，新闻id，2.1.0.0添加
        //"category_name"，String，来源，2.1.0.0 添加
    }
    
    /**
     * 点击评论时回调（2.1.0.0 添加）
     *
     * @param map 附加参数
     */
    public void onDPClickComment(Map<String, Object> map) {
        //map参数说明：key，类型，说明，版本
        //"group_id"，long，新闻id，2.1.0.0添加
        //"category_name"，String，来源，2.1.0.0 添加
    }
    
    /**
     * 点赞时回调（2.1.0.0 添加）
     *
     * @param isLike 是否点赞 true 点赞，false 取消点赞
     * @param map    附加参数
     */
    public void onDPClickLike(boolean isLike, Map<String, Object> map) {
        //map参数说明：key，类型，说明，版本
        //"group_id"，long，新闻id，2.1.0.0添加
        //"category_name"，String，来源，2.1.0.0 添加
    }
​
    /**
     * 举报结果回调（根据举报成功失败结果，隐藏、展示举报界面及提示）（2.1.0.0 添加）
     *
     * @param isSucceed 举报成功：true 举报失败：false
     * @param map       附加参数
     */
    public void onDPReportResult(boolean isSucceed, Map<String, Object> map) {
        //map参数说明：key，类型，说明，版本
        //"group_id"，long，新闻id，2.1.0.0 添加
    }
}
​
3.6 广告监听器 IDPAdListener 接口说明
复制
/**
 * 广告监听器。只需要重写你关心的回调即可
 */
public abstract class IDPAdListener {
​
   /**
    * 广告请求
    *
    * @param map 附加参数
    */
   public void onDPAdRequest(Map<String, Object> map) {
      //map参数说明：key，类型，说明，版本
      //"ad_id"，String，广告位 id，2.1.0.0添加
   }
​
   /**
    * 广告请求成功
    *
    * @param map 附加参数
    */
   public void onDPAdRequestSuccess(Map<String, Object> map) {
      //map参数说明：key，类型，说明，版本
      //"ad_id"，String，广告位 id，2.1.0.0添加
      //"ad_count"，int，广告返回个数，2.1.0.0添加
      //"request_id"，String，广告请求 id，2.2.0.0 添加
   }
​
   /**
    * 广告请求失败
    *
    * @param code 错误码
    * @param msg  错误信息
    * @param map  附加参数
    */
   public void onDPAdRequestFail(int code, String msg, Map<String, Object> map) {
      //map参数说明：key，类型，说明，版本
      //"ad_id"，String，广告位 id，2.1.0.0添加
   }
​
   /**
    * 广告填充失败
    *
    * @param map 附加参数
    */
   public void onDPAdFillFail(Map<String, Object> map) {
      //map参数说明：key，类型，说明，版本
      //"ad_id"，String，广告位 id，2.1.0.0添加
      //"ad_first_pos"，int，首个广告的位置，2.1.0.0添加
      //"ad_follow_sep"，int，非首个广告的间隔，2.1.0.0添加
      //"ad_follow_pos"，int，广告在广告间隔中的位置，2.1.0.0添加
      //举个例子："ad_first_pos"、"ad_follow_sep"、"ad_follow_pos" 分别为 3，3，3
      //意思是第一个广告在第三个位置，接下来，每三条数据，第三个位置放广告
   }
​
   /**
    * 广告曝光
    *
    * @param map 附加参数
    */
   public void onDPAdShow(Map<String, Object> map) {
      //map参数说明：key，类型，说明，版本
      //"ad_id"，String，广告位 id，2.1.0.0添加
      //"request_id"，String，广告请求 id，2.2.0.0 添加
      //"open_ad_"，String，广告扩展字段 id，2.2.0.1 添加
   }
​
   /**
    * 广告开始播放
    *
    * @param map 附加参数
    */
   public void onDPAdPlayStart(Map<String, Object> map) {
      //map参数说明：key，类型，说明，版本
      //"ad_id"，String，广告位 id，2.1.0.0添加
      //"request_id"，String，广告请求 id，2.2.0.0 添加
      //"open_ad_"，String，广告扩展字段 id，2.2.0.1 添加
      // "ad_unique_id", String， 广告位唯一id，3.0.0.0添加
      // "total_duration", long, 总时长，3.0.0.0添加
   }
​
   /**
    * 广告暂停播放.
    *
    * @param map 附加参数
    */
   public void onDPAdPlayPause(Map<String, Object> map) {
      //map参数说明：key，类型，说明，版本
      //"ad_id"，String，广告位 id，2.1.0.0添加
      //"request_id"，String，广告请求 id，2.2.0.0 添加
      //"open_ad_"，String，广告扩展字段 id，2.2.0.1 添加
      // "ad_unique_id", String， 广告位唯一id，3.0.0.0添加
      // "total_duration", long, 总时长，3.0.0.0添加
      // "current_duration", long, 当前播放时长， 3.0.0.0添加
   }
​
   /**
    * 广告继续播放
    *
    * @param map 附加参数
    */
   public void onDPAdPlayContinue(Map<String, Object> map) {
      //map参数说明：key，类型，说明，版本
      //"ad_id"，String，广告位 id，2.1.0.0添加
      //"request_id"，String，广告请求 id，2.2.0.0 添加
      //"open_ad_"，String，广告扩展字段 id，2.2.0.1 添加
      // "ad_unique_id", String， 广告位唯一id，3.0.0.0添加
   }
​
   /**
    * 广告播放结束.
    *
    * @param map 附加参数
    */
   public void onDPAdPlayComplete(Map<String, Object> map) {
      //map参数说明：key，类型，说明，版本
      //"ad_id"，String，广告位 id，2.1.0.0添加
      //"request_id"，String，广告请求 id，2.2.0.0 添加
      //"open_ad_"，String，广告扩展字段 id，2.2.0.1 添加
      // "ad_unique_id", String， 广告位唯一id，3.0.0.0添加
      // "total_duration", long, 总时长，3.0.0.0添加
   }
​
   /**
    * 广告点击
    *
    * @param map 附加参数
    */
   public void onDPAdClicked(Map<String, Object> map) {
      //map参数说明：key，类型，说明，版本
      //"ad_id"，String，广告位 id，2.1.0.0添加
      //"request_id"，String，广告请求 id，2.2.0.0 添加
      //"open_ad_"，String，广告扩展字段 id，2.2.0.1 添加
   }
}
​
4、代码片段示例
代码来自demo：com.bytedance.dpdemo.activity.video.grid.GridFullScreenActivity

复制
private void initGridWidget() {
    mIDPWidget = DPHolder.getInstance().buildGridWidget(DPWidgetGridParams.obtain()
            .listener(new IDPGridListener() {
                @Override
                public void onDPRefreshFinish() {
                    log("onDPRefreshFinish");
                }
​
                @Override
                public void onDPGridItemClick(Map<String, Object> map) {
                    log("onDPGridItemClick");
                }
​
                @Override
                public void onDPRequestStart() {
                    log("onDPRequestStart");
                }
                
                @Override
                public void onDPRequestResult(boolean isSucceed, List<Object> list) {
                    log("onDPRequestResult");
                }
                
                @Override
                public void onDPClientShow() {
                    log("onDPClientShow");
                }
                
                @Override
                public void onDPClickAuthorName(Map<String, Object> map) {
                    log("onDPClickAuthorName");
                }
                
                @Override
                public void onDPClickAvatar(Map<String, Object> map) {
                    log("onDPClickAvatar");
                }
                
                @Override
                public void onDPClickComment(Map<String, Object> map) {
                    log("onDPClickComment");
                }
                
                @Override
                public void onDPClickLike(boolean isLike, Map<String, Object> map) {
                    log("onDPClickLike");
                }
                
                @Override
                public void onDPVideoPlay(Map<String, Object> map) {
                    log("onDPVideoPlay");
                }
                
                @Override
                public void onDPVideoPause(Map<String, Object> map) {
                    log("onDPVideoPause");
                }
                
                @Override
                public void onDPVideoContinue(Map<String, Object> map) {
                    log("onDPVideoContinue");
                }
                
                @Override
                public void onDPVideoOver(Map<String, Object> map) {
                    log("onDPVideoOver");
                }
                
                @Override
                public void onDPVideoCompletion(Map<String, Object> map) {
                    log("onDPVideoCompletion");
                }
            }));
}
​
三、常见问题
fragemnt 嵌套宫格视频 fragment，这样会导致内部宫格视频 fragment 生命周期回调有问题，需要外部开发者自行调用生命周期回调（onResume、onPause、setUserVisibleHint、onHiddenChanged）。
代码示例：

复制
 @Override
       public void onResume() {
           super.onResume();
           if (mIDPWidget.getFragment() != null) {
               mIDPWidget.getFragment().onResume();
           }
       }
   
       @Override
       public void onPause() {
           super.onPause();
           if (mIDPWidget.getFragment() != null) {
               mIDPWidget.getFragment().onPause();
           }
       }
   
       @Override
       public void setUserVisibleHint(boolean isVisibleToUser) {
           super.setUserVisibleHint(isVisibleToUser);
           if (mIDPWidget.getFragment() != null) {
               mIDPWidget.getFragment().setUserVisibleHint(isVisibleToUser);
           }
       }
   
       @Override
       public void onHiddenChanged(boolean hidden) {
           super.onHiddenChanged(hidden);
           if (mIDPWidget.getFragment() != null) {
               mIDPWidget.getFragment().onHiddenChanged(hidden);
           }
       }