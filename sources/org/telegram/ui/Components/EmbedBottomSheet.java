package org.telegram.ui.Components;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.os.Build;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BringAppForegroundService;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.browser.Browser;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.Components.EmbedBottomSheet;
import org.telegram.ui.LaunchActivity;

public class EmbedBottomSheet extends BottomSheet {
    /* access modifiers changed from: private */
    @SuppressLint({"StaticFieldLeak"})
    public static EmbedBottomSheet instance;
    /* access modifiers changed from: private */
    public boolean animationInProgress;
    private FrameLayout containerLayout;
    /* access modifiers changed from: private */
    public TextView copyTextButton;
    /* access modifiers changed from: private */
    public View customView;
    /* access modifiers changed from: private */
    public WebChromeClient.CustomViewCallback customViewCallback;
    /* access modifiers changed from: private */
    public String embedUrl;
    /* access modifiers changed from: private */
    public FrameLayout fullscreenVideoContainer;
    /* access modifiers changed from: private */
    public boolean fullscreenedByButton;
    /* access modifiers changed from: private */
    public boolean hasDescription;
    /* access modifiers changed from: private */
    public int height;
    /* access modifiers changed from: private */
    public LinearLayout imageButtonsContainer;
    /* access modifiers changed from: private */
    public boolean isYouTube;
    private int lastOrientation = -1;
    /* access modifiers changed from: private */
    public DialogInterface.OnShowListener onShowListener = new DialogInterface.OnShowListener() {
        public void onShow(DialogInterface dialogInterface) {
            if (EmbedBottomSheet.this.pipVideoView != null && EmbedBottomSheet.this.videoView.isInline()) {
                EmbedBottomSheet.this.videoView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    public boolean onPreDraw() {
                        EmbedBottomSheet.this.videoView.getViewTreeObserver().removeOnPreDrawListener(this);
                        return true;
                    }
                });
            }
        }
    };
    /* access modifiers changed from: private */
    public String openUrl;
    /* access modifiers changed from: private */
    public OrientationEventListener orientationEventListener;
    /* access modifiers changed from: private */
    public Activity parentActivity;
    /* access modifiers changed from: private */
    public ImageView pipButton;
    /* access modifiers changed from: private */
    public PipVideoView pipVideoView;
    /* access modifiers changed from: private */
    public int[] position = new int[2];
    /* access modifiers changed from: private */
    public int prevOrientation = -2;
    /* access modifiers changed from: private */
    public RadialProgressView progressBar;
    /* access modifiers changed from: private */
    public View progressBarBlackBackground;
    /* access modifiers changed from: private */
    public int seekTimeOverride;
    /* access modifiers changed from: private */
    public WebPlayerView videoView;
    /* access modifiers changed from: private */
    public int waitingForDraw;
    /* access modifiers changed from: private */
    public boolean wasInLandscape;
    /* access modifiers changed from: private */
    public WebView webView;
    /* access modifiers changed from: private */
    public int width;
    private final String youtubeFrame = "<!DOCTYPE html><html><head><style>body { margin: 0; width:100%%; height:100%%;  background-color:#000; }html { width:100%%; height:100%%; background-color:#000; }.embed-container iframe,.embed-container object,   .embed-container embed {       position: absolute;       top: 0;       left: 0;       width: 100%% !important;       height: 100%% !important;   }   </style></head><body>   <div class=\"embed-container\">       <div id=\"player\"></div>   </div>   <script src=\"https://www.youtube.com/iframe_api\"></script>   <script>   var player;   var observer;   var videoEl;   var playing;   var posted = false;   YT.ready(function() {       player = new YT.Player(\"player\", {                              \"width\" : \"100%%\",                              \"events\" : {                              \"onReady\" : \"onReady\",                              \"onError\" : \"onError\",                              \"onStateChange\" : \"onStateChange\",                              },                              \"videoId\" : \"%1$s\",                              \"height\" : \"100%%\",                              \"playerVars\" : {                              \"start\" : %2$d,                              \"rel\" : 1,                              \"showinfo\" : 0,                              \"modestbranding\" : 0,                              \"iv_load_policy\" : 3,                              \"autohide\" : 1,                              \"autoplay\" : 1,                              \"cc_load_policy\" : 1,                              \"playsinline\" : 1,                              \"controls\" : 1                              }                            });        player.setSize(window.innerWidth, window.innerHeight);    });    function hideControls() {        playing = !videoEl.paused;       videoEl.controls = 0;       observer.observe(videoEl, {attributes: true});    }    function showControls() {        playing = !videoEl.paused;       observer.disconnect();       videoEl.controls = 1;    }    function onError(event) {       if (!posted) {            if (window.YoutubeProxy !== undefined) {                   YoutubeProxy.postEvent(\"loaded\", null);             }            posted = true;       }    }    function onStateChange(event) {       if (event.data == YT.PlayerState.PLAYING && !posted) {            if (window.YoutubeProxy !== undefined) {                   YoutubeProxy.postEvent(\"loaded\", null);             }            posted = true;       }    }    function onReady(event) {       player.playVideo();    }    window.onresize = function() {       player.setSize(window.innerWidth, window.innerHeight);       player.playVideo();    }    </script></body></html>";

    static /* synthetic */ boolean lambda$new$0(View view, MotionEvent motionEvent) {
        return true;
    }

    static /* synthetic */ boolean lambda$new$1(View view, MotionEvent motionEvent) {
        return true;
    }

    static /* synthetic */ boolean lambda$new$2(View view, MotionEvent motionEvent) {
        return true;
    }

    private class YoutubeProxy {
        private YoutubeProxy() {
        }

        @JavascriptInterface
        public void postEvent(String str, String str2) {
            if ("loaded".equals(str)) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public final void run() {
                        EmbedBottomSheet.YoutubeProxy.this.lambda$postEvent$0$EmbedBottomSheet$YoutubeProxy();
                    }
                });
            }
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$postEvent$0 */
        public /* synthetic */ void lambda$postEvent$0$EmbedBottomSheet$YoutubeProxy() {
            EmbedBottomSheet.this.progressBar.setVisibility(4);
            EmbedBottomSheet.this.progressBarBlackBackground.setVisibility(4);
            EmbedBottomSheet.this.pipButton.setEnabled(true);
            EmbedBottomSheet.this.pipButton.setAlpha(1.0f);
        }
    }

    public static void show(Context context, String str, String str2, String str3, String str4, int i, int i2, boolean z) {
        show(context, str, str2, str3, str4, i, i2, -1, z);
    }

    public static void show(Context context, String str, String str2, String str3, String str4, int i, int i2, int i3, boolean z) {
        EmbedBottomSheet embedBottomSheet = instance;
        if (embedBottomSheet != null) {
            embedBottomSheet.destroy();
        }
        EmbedBottomSheet embedBottomSheet2 = new EmbedBottomSheet(context, str, str2, str3, str4, i, i2, i3);
        embedBottomSheet2.setCalcMandatoryInsets(z);
        embedBottomSheet2.show();
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    @android.annotation.SuppressLint({"SetJavaScriptEnabled"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private EmbedBottomSheet(android.content.Context r25, java.lang.String r26, java.lang.String r27, java.lang.String r28, java.lang.String r29, int r30, int r31, int r32) {
        /*
            r24 = this;
            r0 = r24
            r1 = r25
            r2 = r27
            r3 = r28
            r4 = r30
            r5 = r31
            r6 = 0
            r0.<init>(r1, r6)
            r7 = 2
            int[] r8 = new int[r7]
            r0.position = r8
            r8 = -1
            r0.lastOrientation = r8
            r9 = -2
            r0.prevOrientation = r9
            java.lang.String r10 = "<!DOCTYPE html><html><head><style>body { margin: 0; width:100%%; height:100%%;  background-color:#000; }html { width:100%%; height:100%%; background-color:#000; }.embed-container iframe,.embed-container object,   .embed-container embed {       position: absolute;       top: 0;       left: 0;       width: 100%% !important;       height: 100%% !important;   }   </style></head><body>   <div class=\"embed-container\">       <div id=\"player\"></div>   </div>   <script src=\"https://www.youtube.com/iframe_api\"></script>   <script>   var player;   var observer;   var videoEl;   var playing;   var posted = false;   YT.ready(function() {       player = new YT.Player(\"player\", {                              \"width\" : \"100%%\",                              \"events\" : {                              \"onReady\" : \"onReady\",                              \"onError\" : \"onError\",                              \"onStateChange\" : \"onStateChange\",                              },                              \"videoId\" : \"%1$s\",                              \"height\" : \"100%%\",                              \"playerVars\" : {                              \"start\" : %2$d,                              \"rel\" : 1,                              \"showinfo\" : 0,                              \"modestbranding\" : 0,                              \"iv_load_policy\" : 3,                              \"autohide\" : 1,                              \"autoplay\" : 1,                              \"cc_load_policy\" : 1,                              \"playsinline\" : 1,                              \"controls\" : 1                              }                            });        player.setSize(window.innerWidth, window.innerHeight);    });    function hideControls() {        playing = !videoEl.paused;       videoEl.controls = 0;       observer.observe(videoEl, {attributes: true});    }    function showControls() {        playing = !videoEl.paused;       observer.disconnect();       videoEl.controls = 1;    }    function onError(event) {       if (!posted) {            if (window.YoutubeProxy !== undefined) {                   YoutubeProxy.postEvent(\"loaded\", null);             }            posted = true;       }    }    function onStateChange(event) {       if (event.data == YT.PlayerState.PLAYING && !posted) {            if (window.YoutubeProxy !== undefined) {                   YoutubeProxy.postEvent(\"loaded\", null);             }            posted = true;       }    }    function onReady(event) {       player.playVideo();    }    window.onresize = function() {       player.setSize(window.innerWidth, window.innerHeight);       player.playVideo();    }    </script></body></html>"
            r0.youtubeFrame = r10
            org.telegram.ui.Components.EmbedBottomSheet$1 r10 = new org.telegram.ui.Components.EmbedBottomSheet$1
            r10.<init>()
            r0.onShowListener = r10
            r10 = 1
            r0.fullWidth = r10
            r0.setApplyTopPadding(r6)
            r0.setApplyBottomPadding(r6)
            r11 = r32
            r0.seekTimeOverride = r11
            boolean r11 = r1 instanceof android.app.Activity
            if (r11 == 0) goto L_0x003c
            r11 = r1
            android.app.Activity r11 = (android.app.Activity) r11
            r0.parentActivity = r11
        L_0x003c:
            r11 = r29
            r0.embedUrl = r11
            if (r2 == 0) goto L_0x004a
            int r11 = r27.length()
            if (r11 <= 0) goto L_0x004a
            r11 = 1
            goto L_0x004b
        L_0x004a:
            r11 = 0
        L_0x004b:
            r0.hasDescription = r11
            r0.openUrl = r3
            r0.width = r4
            r0.height = r5
            if (r4 == 0) goto L_0x0057
            if (r5 != 0) goto L_0x0062
        L_0x0057:
            android.graphics.Point r4 = org.telegram.messenger.AndroidUtilities.displaySize
            int r5 = r4.x
            r0.width = r5
            int r4 = r4.y
            int r4 = r4 / r7
            r0.height = r4
        L_0x0062:
            android.widget.FrameLayout r4 = new android.widget.FrameLayout
            r4.<init>(r1)
            r0.fullscreenVideoContainer = r4
            r4.setKeepScreenOn(r10)
            android.widget.FrameLayout r4 = r0.fullscreenVideoContainer
            r5 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r4.setBackgroundColor(r5)
            int r4 = android.os.Build.VERSION.SDK_INT
            r11 = 21
            if (r4 < r11) goto L_0x007e
            android.widget.FrameLayout r12 = r0.fullscreenVideoContainer
            r12.setFitsSystemWindows(r10)
        L_0x007e:
            android.widget.FrameLayout r12 = r0.fullscreenVideoContainer
            org.telegram.ui.Components.-$$Lambda$EmbedBottomSheet$cb9sTrvuLN2SnbMqw2O6rOTFOTU r13 = org.telegram.ui.Components.$$Lambda$EmbedBottomSheet$cb9sTrvuLN2SnbMqw2O6rOTFOTU.INSTANCE
            r12.setOnTouchListener(r13)
            org.telegram.ui.ActionBar.BottomSheet$ContainerView r12 = r0.container
            android.widget.FrameLayout r13 = r0.fullscreenVideoContainer
            r14 = -1082130432(0xffffffffbvar_, float:-1.0)
            android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r14)
            r12.addView(r13, r14)
            android.widget.FrameLayout r12 = r0.fullscreenVideoContainer
            r13 = 4
            r12.setVisibility(r13)
            android.widget.FrameLayout r12 = r0.fullscreenVideoContainer
            org.telegram.ui.Components.-$$Lambda$EmbedBottomSheet$yeJ86qJGstOrYeqodDuAHTACaW8 r14 = org.telegram.ui.Components.$$Lambda$EmbedBottomSheet$yeJ86qJGstOrYeqodDuAHTACaW8.INSTANCE
            r12.setOnTouchListener(r14)
            org.telegram.ui.Components.EmbedBottomSheet$2 r12 = new org.telegram.ui.Components.EmbedBottomSheet$2
            r12.<init>(r1)
            r0.containerLayout = r12
            org.telegram.ui.Components.-$$Lambda$EmbedBottomSheet$tTd-7OOoZY26OWhGbfqVDTuIMW8 r14 = org.telegram.ui.Components.$$Lambda$EmbedBottomSheet$tTd7OOoZY26OWhGbfqVDTuIMW8.INSTANCE
            r12.setOnTouchListener(r14)
            android.widget.FrameLayout r12 = r0.containerLayout
            r0.setCustomView(r12)
            org.telegram.ui.Components.EmbedBottomSheet$3 r12 = new org.telegram.ui.Components.EmbedBottomSheet$3
            r12.<init>(r1)
            r0.webView = r12
            android.webkit.WebSettings r12 = r12.getSettings()
            r12.setJavaScriptEnabled(r10)
            android.webkit.WebView r12 = r0.webView
            android.webkit.WebSettings r12 = r12.getSettings()
            r12.setDomStorageEnabled(r10)
            r12 = 17
            if (r4 < r12) goto L_0x00d4
            android.webkit.WebView r14 = r0.webView
            android.webkit.WebSettings r14 = r14.getSettings()
            r14.setMediaPlaybackRequiresUserGesture(r6)
        L_0x00d4:
            if (r4 < r11) goto L_0x00e8
            android.webkit.WebView r4 = r0.webView
            android.webkit.WebSettings r4 = r4.getSettings()
            r4.setMixedContentMode(r6)
            android.webkit.CookieManager r4 = android.webkit.CookieManager.getInstance()
            android.webkit.WebView r11 = r0.webView
            r4.setAcceptThirdPartyCookies(r11, r10)
        L_0x00e8:
            android.webkit.WebView r4 = r0.webView
            org.telegram.ui.Components.EmbedBottomSheet$4 r11 = new org.telegram.ui.Components.EmbedBottomSheet$4
            r11.<init>()
            r4.setWebChromeClient(r11)
            android.webkit.WebView r4 = r0.webView
            org.telegram.ui.Components.EmbedBottomSheet$5 r11 = new org.telegram.ui.Components.EmbedBottomSheet$5
            r11.<init>()
            r4.setWebViewClient(r11)
            android.widget.FrameLayout r4 = r0.containerLayout
            android.webkit.WebView r11 = r0.webView
            r14 = -1
            r15 = -1082130432(0xffffffffbvar_, float:-1.0)
            r16 = 51
            r17 = 0
            r18 = 0
            r19 = 0
            boolean r12 = r0.hasDescription
            r21 = 22
            if (r12 == 0) goto L_0x0114
            r12 = 22
            goto L_0x0115
        L_0x0114:
            r12 = 0
        L_0x0115:
            int r12 = r12 + 84
            float r12 = (float) r12
            r20 = r12
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r14, r15, r16, r17, r18, r19, r20)
            r4.addView(r11, r12)
            org.telegram.ui.Components.WebPlayerView r4 = new org.telegram.ui.Components.WebPlayerView
            org.telegram.ui.Components.EmbedBottomSheet$6 r11 = new org.telegram.ui.Components.EmbedBottomSheet$6
            r11.<init>()
            r4.<init>(r1, r10, r6, r11)
            r0.videoView = r4
            r4.setVisibility(r13)
            android.widget.FrameLayout r4 = r0.containerLayout
            org.telegram.ui.Components.WebPlayerView r11 = r0.videoView
            r14 = -1
            r15 = -1082130432(0xffffffffbvar_, float:-1.0)
            r16 = 51
            r17 = 0
            r18 = 0
            r19 = 0
            boolean r12 = r0.hasDescription
            if (r12 == 0) goto L_0x0146
            r12 = 22
            goto L_0x0147
        L_0x0146:
            r12 = 0
        L_0x0147:
            int r12 = r12 + 84
            int r12 = r12 + -10
            float r12 = (float) r12
            r20 = r12
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r14, r15, r16, r17, r18, r19, r20)
            r4.addView(r11, r12)
            android.view.View r4 = new android.view.View
            r4.<init>(r1)
            r0.progressBarBlackBackground = r4
            r4.setBackgroundColor(r5)
            android.view.View r4 = r0.progressBarBlackBackground
            r4.setVisibility(r13)
            android.widget.FrameLayout r4 = r0.containerLayout
            android.view.View r5 = r0.progressBarBlackBackground
            r14 = -1
            r15 = -1082130432(0xffffffffbvar_, float:-1.0)
            r16 = 51
            r17 = 0
            r18 = 0
            r19 = 0
            boolean r11 = r0.hasDescription
            if (r11 == 0) goto L_0x017a
            r11 = 22
            goto L_0x017b
        L_0x017a:
            r11 = 0
        L_0x017b:
            int r11 = r11 + 84
            float r11 = (float) r11
            r20 = r11
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r14, r15, r16, r17, r18, r19, r20)
            r4.addView(r5, r11)
            org.telegram.ui.Components.RadialProgressView r4 = new org.telegram.ui.Components.RadialProgressView
            r4.<init>(r1)
            r0.progressBar = r4
            r4.setVisibility(r13)
            android.widget.FrameLayout r4 = r0.containerLayout
            org.telegram.ui.Components.RadialProgressView r5 = r0.progressBar
            r14 = -2
            r15 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r16 = 17
            r17 = 0
            r18 = 0
            r19 = 0
            boolean r11 = r0.hasDescription
            if (r11 == 0) goto L_0x01a5
            goto L_0x01a7
        L_0x01a5:
            r21 = 0
        L_0x01a7:
            int r21 = r21 + 84
            int r7 = r21 / 2
            float r7 = (float) r7
            r20 = r7
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r14, r15, r16, r17, r18, r19, r20)
            r4.addView(r5, r7)
            boolean r4 = r0.hasDescription
            java.lang.String r5 = "fonts/rmedium.ttf"
            r7 = 1099956224(0x41900000, float:18.0)
            if (r4 == 0) goto L_0x0203
            android.widget.TextView r4 = new android.widget.TextView
            r4.<init>(r1)
            r11 = 1098907648(0x41800000, float:16.0)
            r4.setTextSize(r10, r11)
            java.lang.String r11 = "dialogTextBlack"
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r4.setTextColor(r11)
            r4.setText(r2)
            r4.setSingleLine(r10)
            android.graphics.Typeface r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r5)
            r4.setTypeface(r2)
            android.text.TextUtils$TruncateAt r2 = android.text.TextUtils.TruncateAt.END
            r4.setEllipsize(r2)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r4.setPadding(r2, r6, r11, r6)
            android.widget.FrameLayout r2 = r0.containerLayout
            r14 = -1
            r15 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r16 = 83
            r17 = 0
            r18 = 0
            r19 = 0
            r20 = 1117388800(0x429a0000, float:77.0)
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r14, r15, r16, r17, r18, r19, r20)
            r2.addView(r4, r11)
        L_0x0203:
            android.widget.TextView r2 = new android.widget.TextView
            r2.<init>(r1)
            r4 = 1096810496(0x41600000, float:14.0)
            r2.setTextSize(r10, r4)
            java.lang.String r11 = "dialogTextGray"
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r2.setTextColor(r11)
            r11 = r26
            r2.setText(r11)
            r2.setSingleLine(r10)
            android.text.TextUtils$TruncateAt r11 = android.text.TextUtils.TruncateAt.END
            r2.setEllipsize(r11)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r2.setPadding(r11, r6, r12, r6)
            android.widget.FrameLayout r11 = r0.containerLayout
            r14 = -1
            r15 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r16 = 83
            r17 = 0
            r18 = 0
            r19 = 0
            r20 = 1113849856(0x42640000, float:57.0)
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r14, r15, r16, r17, r18, r19, r20)
            r11.addView(r2, r12)
            android.view.View r2 = new android.view.View
            r2.<init>(r1)
            java.lang.String r11 = "dialogGrayLine"
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r2.setBackgroundColor(r11)
            android.widget.FrameLayout r11 = r0.containerLayout
            android.widget.FrameLayout$LayoutParams r12 = new android.widget.FrameLayout$LayoutParams
            r14 = 83
            r12.<init>(r8, r10, r14)
            r11.addView(r2, r12)
            android.view.ViewGroup$LayoutParams r2 = r2.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r2 = (android.widget.FrameLayout.LayoutParams) r2
            r11 = 1111490560(0x42400000, float:48.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r2.bottomMargin = r11
            android.widget.FrameLayout r2 = new android.widget.FrameLayout
            r2.<init>(r1)
            java.lang.String r11 = "dialogBackground"
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r2.setBackgroundColor(r11)
            android.widget.FrameLayout r11 = r0.containerLayout
            r12 = 48
            android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r12, r14)
            r11.addView(r2, r14)
            android.widget.LinearLayout r11 = new android.widget.LinearLayout
            r11.<init>(r1)
            r11.setOrientation(r6)
            r14 = 1065353216(0x3var_, float:1.0)
            r11.setWeightSum(r14)
            r14 = 53
            android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r8, r14)
            r2.addView(r11, r14)
            android.widget.TextView r14 = new android.widget.TextView
            r14.<init>(r1)
            r14.setTextSize(r10, r4)
            java.lang.String r15 = "dialogTextBlue4"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r15)
            r14.setTextColor(r4)
            r4 = 17
            r14.setGravity(r4)
            r14.setSingleLine(r10)
            android.text.TextUtils$TruncateAt r4 = android.text.TextUtils.TruncateAt.END
            r14.setEllipsize(r4)
            java.lang.String r4 = "dialogButtonSelector"
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r10, r6)
            r14.setBackgroundDrawable(r10)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r14.setPadding(r10, r6, r12, r6)
            r10 = 2131624928(0x7f0e03e0, float:1.887705E38)
            java.lang.String r12 = "Close"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r12, r10)
            java.lang.String r10 = r10.toUpperCase()
            r14.setText(r10)
            android.graphics.Typeface r10 = org.telegram.messenger.AndroidUtilities.getTypeface(r5)
            r14.setTypeface(r10)
            r10 = 51
            android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r9, (int) r8, (int) r10)
            r2.addView(r14, r12)
            org.telegram.ui.Components.-$$Lambda$EmbedBottomSheet$1AQ1TuWQsKenxnL0mvKNj9FvFdU r12 = new org.telegram.ui.Components.-$$Lambda$EmbedBottomSheet$1AQ1TuWQsKenxnL0mvKNj9FvFdU
            r12.<init>()
            r14.setOnClickListener(r12)
            android.widget.LinearLayout r12 = new android.widget.LinearLayout
            r12.<init>(r1)
            r0.imageButtonsContainer = r12
            r12.setVisibility(r13)
            android.widget.LinearLayout r12 = r0.imageButtonsContainer
            r14 = 17
            android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r8, r14)
            r2.addView(r12, r13)
            android.widget.ImageView r2 = new android.widget.ImageView
            r2.<init>(r1)
            r0.pipButton = r2
            android.widget.ImageView$ScaleType r12 = android.widget.ImageView.ScaleType.CENTER
            r2.setScaleType(r12)
            android.widget.ImageView r2 = r0.pipButton
            r12 = 2131166113(0x7var_a1, float:1.7946462E38)
            r2.setImageResource(r12)
            android.widget.ImageView r2 = r0.pipButton
            r12 = 2131624010(0x7f0e004a, float:1.8875188E38)
            java.lang.String r13 = "AccDescrPipMode"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
            r2.setContentDescription(r12)
            android.widget.ImageView r2 = r0.pipButton
            r2.setEnabled(r6)
            android.widget.ImageView r2 = r0.pipButton
            r12 = 1056964608(0x3var_, float:0.5)
            r2.setAlpha(r12)
            android.widget.ImageView r2 = r0.pipButton
            android.graphics.PorterDuffColorFilter r12 = new android.graphics.PorterDuffColorFilter
            int r13 = org.telegram.ui.ActionBar.Theme.getColor(r15)
            android.graphics.PorterDuff$Mode r14 = android.graphics.PorterDuff.Mode.MULTIPLY
            r12.<init>(r13, r14)
            r2.setColorFilter(r12)
            android.widget.ImageView r2 = r0.pipButton
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r12, r6)
            r2.setBackgroundDrawable(r12)
            android.widget.LinearLayout r2 = r0.imageButtonsContainer
            android.widget.ImageView r12 = r0.pipButton
            r17 = 48
            r18 = 1111490560(0x42400000, float:48.0)
            r19 = 51
            r20 = 0
            r21 = 0
            r22 = 1082130432(0x40800000, float:4.0)
            r23 = 0
            android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame(r17, r18, r19, r20, r21, r22, r23)
            r2.addView(r12, r13)
            android.widget.ImageView r2 = r0.pipButton
            org.telegram.ui.Components.-$$Lambda$EmbedBottomSheet$JZq6mwtzPK_x1a6eXFenQU1hvuo r12 = new org.telegram.ui.Components.-$$Lambda$EmbedBottomSheet$JZq6mwtzPK_x1a6eXFenQU1hvuo
            r12.<init>()
            r2.setOnClickListener(r12)
            org.telegram.ui.Components.-$$Lambda$EmbedBottomSheet$AAO_ZXRWecV7BKJLqOOqd4xNiHY r2 = new org.telegram.ui.Components.-$$Lambda$EmbedBottomSheet$AAO_ZXRWecV7BKJLqOOqd4xNiHY
            r2.<init>()
            android.widget.ImageView r12 = new android.widget.ImageView
            r12.<init>(r1)
            android.widget.ImageView$ScaleType r13 = android.widget.ImageView.ScaleType.CENTER
            r12.setScaleType(r13)
            r13 = 2131166105(0x7var_, float:1.7946446E38)
            r12.setImageResource(r13)
            r13 = 2131625013(0x7f0e0435, float:1.8877222E38)
            java.lang.String r14 = "CopyLink"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r14, r13)
            r12.setContentDescription(r13)
            android.graphics.PorterDuffColorFilter r13 = new android.graphics.PorterDuffColorFilter
            int r14 = org.telegram.ui.ActionBar.Theme.getColor(r15)
            android.graphics.PorterDuff$Mode r8 = android.graphics.PorterDuff.Mode.MULTIPLY
            r13.<init>(r14, r8)
            r12.setColorFilter(r13)
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            android.graphics.drawable.Drawable r8 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r8, r6)
            r12.setBackgroundDrawable(r8)
            android.widget.LinearLayout r8 = r0.imageButtonsContainer
            r13 = 48
            android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame(r13, r13, r10)
            r8.addView(r12, r13)
            r12.setOnClickListener(r2)
            android.widget.TextView r8 = new android.widget.TextView
            r8.<init>(r1)
            r0.copyTextButton = r8
            r12 = 1096810496(0x41600000, float:14.0)
            r13 = 1
            r8.setTextSize(r13, r12)
            android.widget.TextView r8 = r0.copyTextButton
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r15)
            r8.setTextColor(r12)
            android.widget.TextView r8 = r0.copyTextButton
            r12 = 17
            r8.setGravity(r12)
            android.widget.TextView r8 = r0.copyTextButton
            r8.setSingleLine(r13)
            android.widget.TextView r8 = r0.copyTextButton
            android.text.TextUtils$TruncateAt r12 = android.text.TextUtils.TruncateAt.END
            r8.setEllipsize(r12)
            android.widget.TextView r8 = r0.copyTextButton
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r12, r6)
            r8.setBackgroundDrawable(r12)
            android.widget.TextView r8 = r0.copyTextButton
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r8.setPadding(r12, r6, r13, r6)
            android.widget.TextView r8 = r0.copyTextButton
            r12 = 2131625011(0x7f0e0433, float:1.8877218E38)
            java.lang.String r13 = "Copy"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
            java.lang.String r12 = r12.toUpperCase()
            r8.setText(r12)
            android.widget.TextView r8 = r0.copyTextButton
            android.graphics.Typeface r12 = org.telegram.messenger.AndroidUtilities.getTypeface(r5)
            r8.setTypeface(r12)
            android.widget.TextView r8 = r0.copyTextButton
            r12 = -1
            android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r12, r10)
            r11.addView(r8, r13)
            android.widget.TextView r8 = r0.copyTextButton
            r8.setOnClickListener(r2)
            android.widget.TextView r2 = new android.widget.TextView
            r2.<init>(r1)
            r1 = 1096810496(0x41600000, float:14.0)
            r8 = 1
            r2.setTextSize(r8, r1)
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r15)
            r2.setTextColor(r1)
            r1 = 17
            r2.setGravity(r1)
            r2.setSingleLine(r8)
            android.text.TextUtils$TruncateAt r1 = android.text.TextUtils.TruncateAt.END
            r2.setEllipsize(r1)
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r1, r6)
            r2.setBackgroundDrawable(r1)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r2.setPadding(r1, r6, r4, r6)
            r1 = 2131626553(0x7f0e0a39, float:1.8880345E38)
            java.lang.String r4 = "OpenInBrowser"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r4, r1)
            java.lang.String r1 = r1.toUpperCase()
            r2.setText(r1)
            android.graphics.Typeface r1 = org.telegram.messenger.AndroidUtilities.getTypeface(r5)
            r2.setTypeface(r1)
            r1 = -1
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r1, r10)
            r11.addView(r2, r1)
            org.telegram.ui.Components.-$$Lambda$EmbedBottomSheet$_pdhFnRV1w6sanI4Jg_8XImPJf4 r1 = new org.telegram.ui.Components.-$$Lambda$EmbedBottomSheet$_pdhFnRV1w6sanI4Jg_8XImPJf4
            r1.<init>()
            r2.setOnClickListener(r1)
            org.telegram.ui.Components.WebPlayerView r1 = r0.videoView
            java.lang.String r2 = r0.embedUrl
            boolean r1 = r1.canHandleUrl(r2)
            if (r1 != 0) goto L_0x04a2
            org.telegram.ui.Components.WebPlayerView r1 = r0.videoView
            boolean r1 = r1.canHandleUrl(r3)
            if (r1 == 0) goto L_0x04a0
            goto L_0x04a2
        L_0x04a0:
            r13 = 0
            goto L_0x04a3
        L_0x04a2:
            r13 = 1
        L_0x04a3:
            org.telegram.ui.Components.WebPlayerView r1 = r0.videoView
            if (r13 == 0) goto L_0x04a9
            r2 = 0
            goto L_0x04aa
        L_0x04a9:
            r2 = 4
        L_0x04aa:
            r1.setVisibility(r2)
            if (r13 == 0) goto L_0x04b4
            org.telegram.ui.Components.WebPlayerView r1 = r0.videoView
            r1.willHandle()
        L_0x04b4:
            org.telegram.ui.Components.EmbedBottomSheet$8 r1 = new org.telegram.ui.Components.EmbedBottomSheet$8
            r1.<init>(r13)
            r0.setDelegate(r1)
            org.telegram.ui.Components.EmbedBottomSheet$9 r1 = new org.telegram.ui.Components.EmbedBottomSheet$9
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext
            r1.<init>(r2)
            r0.orientationEventListener = r1
            org.telegram.ui.Components.WebPlayerView r1 = r0.videoView
            java.lang.String r2 = r0.embedUrl
            java.lang.String r1 = r1.getYouTubeVideoId(r2)
            if (r1 != 0) goto L_0x04d1
            if (r13 != 0) goto L_0x0534
        L_0x04d1:
            org.telegram.ui.Components.RadialProgressView r2 = r0.progressBar
            r2.setVisibility(r6)
            android.webkit.WebView r2 = r0.webView
            r2.setVisibility(r6)
            android.widget.LinearLayout r2 = r0.imageButtonsContainer
            r2.setVisibility(r6)
            if (r1 == 0) goto L_0x04e7
            android.view.View r2 = r0.progressBarBlackBackground
            r2.setVisibility(r6)
        L_0x04e7:
            android.widget.TextView r2 = r0.copyTextButton
            r3 = 4
            r2.setVisibility(r3)
            android.webkit.WebView r2 = r0.webView
            r4 = 1
            r2.setKeepScreenOn(r4)
            org.telegram.ui.Components.WebPlayerView r2 = r0.videoView
            r2.setVisibility(r3)
            org.telegram.ui.Components.WebPlayerView r2 = r0.videoView
            android.view.View r2 = r2.getControlsView()
            r2.setVisibility(r3)
            org.telegram.ui.Components.WebPlayerView r2 = r0.videoView
            android.view.TextureView r2 = r2.getTextureView()
            r2.setVisibility(r3)
            org.telegram.ui.Components.WebPlayerView r2 = r0.videoView
            android.widget.ImageView r2 = r2.getTextureImageView()
            if (r2 == 0) goto L_0x051b
            org.telegram.ui.Components.WebPlayerView r2 = r0.videoView
            android.widget.ImageView r2 = r2.getTextureImageView()
            r2.setVisibility(r3)
        L_0x051b:
            if (r1 == 0) goto L_0x0534
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            java.lang.String r1 = r1.youtubePipType
            java.lang.String r2 = "disabled"
            boolean r1 = r2.equals(r1)
            if (r1 == 0) goto L_0x0534
            android.widget.ImageView r1 = r0.pipButton
            r2 = 8
            r1.setVisibility(r2)
        L_0x0534:
            android.view.OrientationEventListener r1 = r0.orientationEventListener
            boolean r1 = r1.canDetectOrientation()
            if (r1 == 0) goto L_0x0542
            android.view.OrientationEventListener r1 = r0.orientationEventListener
            r1.enable()
            goto L_0x054a
        L_0x0542:
            android.view.OrientationEventListener r1 = r0.orientationEventListener
            r1.disable()
            r1 = 0
            r0.orientationEventListener = r1
        L_0x054a:
            instance = r0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmbedBottomSheet.<init>(android.content.Context, java.lang.String, java.lang.String, java.lang.String, java.lang.String, int, int, int):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$3 */
    public /* synthetic */ void lambda$new$3$EmbedBottomSheet(View view) {
        dismiss();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$4 */
    public /* synthetic */ void lambda$new$4$EmbedBottomSheet(View view) {
        int i;
        boolean z = this.isYouTube && "inapp".equals(MessagesController.getInstance(this.currentAccount).youtubePipType);
        if ((z || checkInlinePermissions()) && this.progressBar.getVisibility() != 0) {
            PipVideoView pipVideoView2 = new PipVideoView(z);
            this.pipVideoView = pipVideoView2;
            Activity activity = this.parentActivity;
            int i2 = this.width;
            pipVideoView2.show(activity, this, (View) null, (i2 == 0 || (i = this.height) == 0) ? 1.0f : ((float) i2) / ((float) i), 0, this.webView);
            if (this.isYouTube) {
                runJsCode("hideControls();");
            }
            this.containerView.setTranslationY(0.0f);
            dismissInternal();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$5 */
    public /* synthetic */ void lambda$new$5$EmbedBottomSheet(View view) {
        try {
            ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", this.openUrl));
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        Activity activity = this.parentActivity;
        if (activity instanceof LaunchActivity) {
            ((LaunchActivity) activity).showBulletin($$Lambda$jSCpt6qlbPOl03gEFikkUBgidwI.INSTANCE);
        }
        dismiss();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$6 */
    public /* synthetic */ void lambda$new$6$EmbedBottomSheet(View view) {
        Browser.openUrl((Context) this.parentActivity, this.openUrl);
        dismiss();
    }

    private void runJsCode(String str) {
        if (Build.VERSION.SDK_INT >= 21) {
            this.webView.evaluateJavascript(str, (ValueCallback) null);
            return;
        }
        try {
            WebView webView2 = this.webView;
            webView2.loadUrl("javascript:" + str);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public boolean checkInlinePermissions() {
        Activity activity = this.parentActivity;
        if (activity == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT < 23 || Settings.canDrawOverlays(activity)) {
            return true;
        }
        AlertsCreator.createDrawOverlayPermissionDialog(this.parentActivity, (DialogInterface.OnClickListener) null);
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return this.videoView.getVisibility() != 0 || !this.videoView.isInFullscreen();
    }

    public void onConfigurationChanged(Configuration configuration) {
        if (this.videoView.getVisibility() == 0 && this.videoView.isInitied() && !this.videoView.isInline()) {
            if (configuration.orientation == 2) {
                if (!this.videoView.isInFullscreen()) {
                    this.videoView.enterFullscreen();
                }
            } else if (this.videoView.isInFullscreen()) {
                this.videoView.exitFullscreen();
            }
        }
        PipVideoView pipVideoView2 = this.pipVideoView;
        if (pipVideoView2 != null) {
            pipVideoView2.onConfigurationChanged();
        }
    }

    public void destroy() {
        WebView webView2 = this.webView;
        if (webView2 != null && webView2.getVisibility() == 0) {
            this.containerLayout.removeView(this.webView);
            this.webView.stopLoading();
            this.webView.loadUrl("about:blank");
            this.webView.destroy();
        }
        PipVideoView pipVideoView2 = this.pipVideoView;
        if (pipVideoView2 != null) {
            pipVideoView2.close();
            this.pipVideoView = null;
        }
        WebPlayerView webPlayerView = this.videoView;
        if (webPlayerView != null) {
            webPlayerView.destroy();
        }
        instance = null;
        dismissInternal();
    }

    public void dismissInternal() {
        super.dismissInternal();
        OrientationEventListener orientationEventListener2 = this.orientationEventListener;
        if (orientationEventListener2 != null) {
            orientationEventListener2.disable();
            this.orientationEventListener = null;
        }
    }

    public void exitFromPip() {
        if (this.webView != null && this.pipVideoView != null) {
            if (ApplicationLoader.mainInterfacePaused) {
                try {
                    this.parentActivity.startService(new Intent(ApplicationLoader.applicationContext, BringAppForegroundService.class));
                } catch (Throwable th) {
                    FileLog.e(th);
                }
            }
            if (this.isYouTube) {
                runJsCode("showControls();");
            }
            ViewGroup viewGroup = (ViewGroup) this.webView.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(this.webView);
            }
            this.containerLayout.addView(this.webView, 0, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, (float) ((this.hasDescription ? 22 : 0) + 84)));
            setShowWithoutAnimation(true);
            show();
            this.pipVideoView.close();
            this.pipVideoView = null;
        }
    }

    public static EmbedBottomSheet getInstance() {
        return instance;
    }

    public void updateTextureViewPosition() {
        this.videoView.getAspectRatioView().getLocationInWindow(this.position);
        int[] iArr = this.position;
        iArr[0] = iArr[0] - getLeftInset();
        if (!this.videoView.isInline() && !this.animationInProgress) {
            TextureView textureView = this.videoView.getTextureView();
            textureView.setTranslationX((float) this.position[0]);
            textureView.setTranslationY((float) this.position[1]);
            ImageView textureImageView = this.videoView.getTextureImageView();
            if (textureImageView != null) {
                textureImageView.setTranslationX((float) this.position[0]);
                textureImageView.setTranslationY((float) this.position[1]);
            }
        }
        View controlsView = this.videoView.getControlsView();
        if (controlsView.getParent() == this.container) {
            controlsView.setTranslationY((float) this.position[1]);
        } else {
            controlsView.setTranslationY(0.0f);
        }
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithTouchOutside() {
        return this.fullscreenVideoContainer.getVisibility() != 0;
    }

    /* access modifiers changed from: protected */
    public void onContainerTranslationYChanged(float f) {
        updateTextureViewPosition();
    }

    /* access modifiers changed from: protected */
    public boolean onCustomMeasure(View view, int i, int i2) {
        if (view == this.videoView.getControlsView()) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.width = this.videoView.getMeasuredWidth();
            layoutParams.height = this.videoView.getAspectRatioView().getMeasuredHeight() + (this.videoView.isInFullscreen() ? 0 : AndroidUtilities.dp(10.0f));
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean onCustomLayout(View view, int i, int i2, int i3, int i4) {
        if (view != this.videoView.getControlsView()) {
            return false;
        }
        updateTextureViewPosition();
        return false;
    }

    public void pause() {
        WebPlayerView webPlayerView = this.videoView;
        if (webPlayerView != null && webPlayerView.isInitied()) {
            this.videoView.pause();
        }
    }

    public void onContainerDraw(Canvas canvas) {
        int i = this.waitingForDraw;
        if (i != 0) {
            int i2 = i - 1;
            this.waitingForDraw = i2;
            if (i2 == 0) {
                this.videoView.updateTextureImageView();
                this.pipVideoView.close();
                this.pipVideoView = null;
                return;
            }
            this.container.invalidate();
        }
    }
}
