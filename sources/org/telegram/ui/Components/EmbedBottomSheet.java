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
import android.net.Uri;
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
import android.widget.Toast;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BringAppForegroundService;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.browser.Browser;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.Components.EmbedBottomSheet;

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
    private final String youtubeFrame = "<!DOCTYPE html><html><head><style>body { margin: 0; width:100%%; height:100%%;  background-color:#000; }html { width:100%%; height:100%%; background-color:#000; }.embed-container iframe,.embed-container object,   .embed-container embed {       position: absolute;       top: 0;       left: 0;       width: 100%% !important;       height: 100%% !important;   }   </style></head><body>   <div class=\"embed-container\">       <div id=\"player\"></div>   </div>   <script src=\"https://www.youtube.com/iframe_api\"></script>   <script>   var player;   var observer;   var videoEl;   var playing;   var posted = false;   YT.ready(function() {       player = new YT.Player(\"player\", {                              \"width\" : \"100%%\",                              \"events\" : {                              \"onReady\" : \"onReady\",                              \"onError\" : \"onError\",                              },                              \"videoId\" : \"%1$s\",                              \"height\" : \"100%%\",                              \"playerVars\" : {                              \"start\" : %2$d,                              \"rel\" : 0,                              \"showinfo\" : 0,                              \"modestbranding\" : 1,                              \"iv_load_policy\" : 3,                              \"autohide\" : 1,                              \"autoplay\" : 1,                              \"cc_load_policy\" : 1,                              \"playsinline\" : 1,                              \"controls\" : 1                              }                            });        player.setSize(window.innerWidth, window.innerHeight);    });    function hideControls() {        playing = !videoEl.paused;       videoEl.controls = 0;       observer.observe(videoEl, {attributes: true});    }    function showControls() {        playing = !videoEl.paused;       observer.disconnect();       videoEl.controls = 1;    }    function onError(event) {       if (!posted) {            if (window.YoutubeProxy !== undefined) {                   YoutubeProxy.postEvent(\"loaded\", null);             }            posted = true;       }    }    function onReady(event) {       player.playVideo();       videoEl = player.getIframe().contentDocument.getElementsByTagName('video')[0];\n       videoEl.addEventListener(\"canplay\", function() {            if (playing) {               videoEl.play();            }       }, true);       videoEl.addEventListener(\"timeupdate\", function() {            if (!posted && videoEl.currentTime > 0) {               if (window.YoutubeProxy !== undefined) {                   YoutubeProxy.postEvent(\"loaded\", null);                }               posted = true;           }       }, true);       observer = new MutationObserver(function() {\n          if (videoEl.controls) {\n               videoEl.controls = 0;\n          }       });\n    }    window.onresize = function() {        player.setSize(window.innerWidth, window.innerHeight);    }    </script></body></html>";

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

        public /* synthetic */ void lambda$postEvent$0$EmbedBottomSheet$YoutubeProxy() {
            EmbedBottomSheet.this.progressBar.setVisibility(4);
            EmbedBottomSheet.this.progressBarBlackBackground.setVisibility(4);
            EmbedBottomSheet.this.pipButton.setEnabled(true);
            EmbedBottomSheet.this.pipButton.setAlpha(1.0f);
        }
    }

    public static void show(Context context, String str, String str2, String str3, String str4, int i, int i2) {
        show(context, str, str2, str3, str4, i, i2, -1);
    }

    public static void show(Context context, String str, String str2, String str3, String str4, int i, int i2, int i3) {
        EmbedBottomSheet embedBottomSheet = instance;
        if (embedBottomSheet != null) {
            embedBottomSheet.destroy();
        }
        new EmbedBottomSheet(context, str, str2, str3, str4, i, i2, i3).show();
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    @android.annotation.SuppressLint({"SetJavaScriptEnabled"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private EmbedBottomSheet(android.content.Context r27, java.lang.String r28, java.lang.String r29, java.lang.String r30, java.lang.String r31, int r32, int r33, int r34) {
        /*
            r26 = this;
            r0 = r26
            r1 = r27
            r2 = r29
            r3 = 0
            r0.<init>(r1, r3)
            r4 = 2
            int[] r5 = new int[r4]
            r0.position = r5
            r5 = -1
            r0.lastOrientation = r5
            r6 = -2
            r0.prevOrientation = r6
            java.lang.String r7 = "<!DOCTYPE html><html><head><style>body { margin: 0; width:100%%; height:100%%;  background-color:#000; }html { width:100%%; height:100%%; background-color:#000; }.embed-container iframe,.embed-container object,   .embed-container embed {       position: absolute;       top: 0;       left: 0;       width: 100%% !important;       height: 100%% !important;   }   </style></head><body>   <div class=\"embed-container\">       <div id=\"player\"></div>   </div>   <script src=\"https://www.youtube.com/iframe_api\"></script>   <script>   var player;   var observer;   var videoEl;   var playing;   var posted = false;   YT.ready(function() {       player = new YT.Player(\"player\", {                              \"width\" : \"100%%\",                              \"events\" : {                              \"onReady\" : \"onReady\",                              \"onError\" : \"onError\",                              },                              \"videoId\" : \"%1$s\",                              \"height\" : \"100%%\",                              \"playerVars\" : {                              \"start\" : %2$d,                              \"rel\" : 0,                              \"showinfo\" : 0,                              \"modestbranding\" : 1,                              \"iv_load_policy\" : 3,                              \"autohide\" : 1,                              \"autoplay\" : 1,                              \"cc_load_policy\" : 1,                              \"playsinline\" : 1,                              \"controls\" : 1                              }                            });        player.setSize(window.innerWidth, window.innerHeight);    });    function hideControls() {        playing = !videoEl.paused;       videoEl.controls = 0;       observer.observe(videoEl, {attributes: true});    }    function showControls() {        playing = !videoEl.paused;       observer.disconnect();       videoEl.controls = 1;    }    function onError(event) {       if (!posted) {            if (window.YoutubeProxy !== undefined) {                   YoutubeProxy.postEvent(\"loaded\", null);             }            posted = true;       }    }    function onReady(event) {       player.playVideo();       videoEl = player.getIframe().contentDocument.getElementsByTagName('video')[0];\n       videoEl.addEventListener(\"canplay\", function() {            if (playing) {               videoEl.play();            }       }, true);       videoEl.addEventListener(\"timeupdate\", function() {            if (!posted && videoEl.currentTime > 0) {               if (window.YoutubeProxy !== undefined) {                   YoutubeProxy.postEvent(\"loaded\", null);                }               posted = true;           }       }, true);       observer = new MutationObserver(function() {\n          if (videoEl.controls) {\n               videoEl.controls = 0;\n          }       });\n    }    window.onresize = function() {        player.setSize(window.innerWidth, window.innerHeight);    }    </script></body></html>"
            r0.youtubeFrame = r7
            org.telegram.ui.Components.EmbedBottomSheet$1 r7 = new org.telegram.ui.Components.EmbedBottomSheet$1
            r7.<init>()
            r0.onShowListener = r7
            r7 = 1
            r0.fullWidth = r7
            r0.setApplyTopPadding(r3)
            r0.setApplyBottomPadding(r3)
            r8 = r34
            r0.seekTimeOverride = r8
            boolean r8 = r1 instanceof android.app.Activity
            if (r8 == 0) goto L_0x0036
            r8 = r1
            android.app.Activity r8 = (android.app.Activity) r8
            r0.parentActivity = r8
        L_0x0036:
            r8 = r31
            r0.embedUrl = r8
            if (r2 == 0) goto L_0x0044
            int r8 = r29.length()
            if (r8 <= 0) goto L_0x0044
            r8 = 1
            goto L_0x0045
        L_0x0044:
            r8 = 0
        L_0x0045:
            r0.hasDescription = r8
            r8 = r30
            r0.openUrl = r8
            r8 = r32
            r0.width = r8
            r8 = r33
            r0.height = r8
            int r8 = r0.width
            if (r8 == 0) goto L_0x005b
            int r8 = r0.height
            if (r8 != 0) goto L_0x0066
        L_0x005b:
            android.graphics.Point r8 = org.telegram.messenger.AndroidUtilities.displaySize
            int r9 = r8.x
            r0.width = r9
            int r8 = r8.y
            int r8 = r8 / r4
            r0.height = r8
        L_0x0066:
            android.widget.FrameLayout r8 = new android.widget.FrameLayout
            r8.<init>(r1)
            r0.fullscreenVideoContainer = r8
            android.widget.FrameLayout r8 = r0.fullscreenVideoContainer
            r8.setKeepScreenOn(r7)
            android.widget.FrameLayout r8 = r0.fullscreenVideoContainer
            r9 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r8.setBackgroundColor(r9)
            int r8 = android.os.Build.VERSION.SDK_INT
            r9 = 21
            if (r8 < r9) goto L_0x0084
            android.widget.FrameLayout r8 = r0.fullscreenVideoContainer
            r8.setFitsSystemWindows(r7)
        L_0x0084:
            android.widget.FrameLayout r8 = r0.fullscreenVideoContainer
            org.telegram.ui.Components.-$$Lambda$EmbedBottomSheet$awgbBJJ9KT-HOZj5dBev_ioRvPo r9 = org.telegram.ui.Components.$$Lambda$EmbedBottomSheet$awgbBJJ9KTHOZj5dBev_ioRvPo.INSTANCE
            r8.setOnTouchListener(r9)
            org.telegram.ui.ActionBar.BottomSheet$ContainerView r8 = r0.container
            android.widget.FrameLayout r9 = r0.fullscreenVideoContainer
            r10 = -1082130432(0xffffffffbvar_, float:-1.0)
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r10)
            r8.addView(r9, r10)
            android.widget.FrameLayout r8 = r0.fullscreenVideoContainer
            r9 = 4
            r8.setVisibility(r9)
            android.widget.FrameLayout r8 = r0.fullscreenVideoContainer
            org.telegram.ui.Components.-$$Lambda$EmbedBottomSheet$wxc0LCDO5uuH8qs6VYdAXQWEEZE r10 = org.telegram.ui.Components.$$Lambda$EmbedBottomSheet$wxc0LCDO5uuH8qs6VYdAXQWEEZE.INSTANCE
            r8.setOnTouchListener(r10)
            org.telegram.ui.Components.EmbedBottomSheet$2 r8 = new org.telegram.ui.Components.EmbedBottomSheet$2
            r8.<init>(r1)
            r0.containerLayout = r8
            android.widget.FrameLayout r8 = r0.containerLayout
            org.telegram.ui.Components.-$$Lambda$EmbedBottomSheet$iYWCViD8wKuconyj-uGO4wKzEcQ r10 = org.telegram.ui.Components.$$Lambda$EmbedBottomSheet$iYWCViD8wKuconyjuGO4wKzEcQ.INSTANCE
            r8.setOnTouchListener(r10)
            android.widget.FrameLayout r8 = r0.containerLayout
            r0.setCustomView(r8)
            android.webkit.WebView r8 = new android.webkit.WebView
            r8.<init>(r1)
            r0.webView = r8
            android.webkit.WebView r8 = r0.webView
            android.webkit.WebSettings r8 = r8.getSettings()
            r8.setJavaScriptEnabled(r7)
            android.webkit.WebView r8 = r0.webView
            android.webkit.WebSettings r8 = r8.getSettings()
            r8.setDomStorageEnabled(r7)
            int r8 = android.os.Build.VERSION.SDK_INT
            r10 = 17
            if (r8 < r10) goto L_0x00e0
            android.webkit.WebView r8 = r0.webView
            android.webkit.WebSettings r8 = r8.getSettings()
            r8.setMediaPlaybackRequiresUserGesture(r3)
        L_0x00e0:
            int r8 = android.os.Build.VERSION.SDK_INT
            r11 = 21
            if (r8 < r11) goto L_0x00f8
            android.webkit.WebView r8 = r0.webView
            android.webkit.WebSettings r8 = r8.getSettings()
            r8.setMixedContentMode(r3)
            android.webkit.CookieManager r8 = android.webkit.CookieManager.getInstance()
            android.webkit.WebView r11 = r0.webView
            r8.setAcceptThirdPartyCookies(r11, r7)
        L_0x00f8:
            android.webkit.WebView r8 = r0.webView
            org.telegram.ui.Components.EmbedBottomSheet$3 r11 = new org.telegram.ui.Components.EmbedBottomSheet$3
            r11.<init>()
            r8.setWebChromeClient(r11)
            android.webkit.WebView r8 = r0.webView
            org.telegram.ui.Components.EmbedBottomSheet$4 r11 = new org.telegram.ui.Components.EmbedBottomSheet$4
            r11.<init>()
            r8.setWebViewClient(r11)
            android.widget.FrameLayout r8 = r0.containerLayout
            android.webkit.WebView r11 = r0.webView
            r12 = -1
            r13 = -1082130432(0xffffffffbvar_, float:-1.0)
            r14 = 51
            r15 = 0
            r16 = 0
            r17 = 0
            boolean r10 = r0.hasDescription
            r19 = 22
            if (r10 == 0) goto L_0x0123
            r10 = 22
            goto L_0x0124
        L_0x0123:
            r10 = 0
        L_0x0124:
            int r10 = r10 + 84
            float r10 = (float) r10
            r18 = r10
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r13, r14, r15, r16, r17, r18)
            r8.addView(r11, r10)
            org.telegram.ui.Components.WebPlayerView r8 = new org.telegram.ui.Components.WebPlayerView
            org.telegram.ui.Components.EmbedBottomSheet$5 r10 = new org.telegram.ui.Components.EmbedBottomSheet$5
            r10.<init>()
            r8.<init>(r1, r7, r3, r10)
            r0.videoView = r8
            org.telegram.ui.Components.WebPlayerView r8 = r0.videoView
            r8.setVisibility(r9)
            android.widget.FrameLayout r8 = r0.containerLayout
            org.telegram.ui.Components.WebPlayerView r10 = r0.videoView
            r11 = -1
            r12 = -1082130432(0xffffffffbvar_, float:-1.0)
            r13 = 51
            r14 = 0
            r15 = 0
            r16 = 0
            boolean r6 = r0.hasDescription
            if (r6 == 0) goto L_0x0155
            r6 = 22
            goto L_0x0156
        L_0x0155:
            r6 = 0
        L_0x0156:
            int r6 = r6 + 84
            int r6 = r6 + -10
            float r6 = (float) r6
            r17 = r6
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r12, r13, r14, r15, r16, r17)
            r8.addView(r10, r6)
            android.view.View r6 = new android.view.View
            r6.<init>(r1)
            r0.progressBarBlackBackground = r6
            android.view.View r6 = r0.progressBarBlackBackground
            r8 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r6.setBackgroundColor(r8)
            android.view.View r6 = r0.progressBarBlackBackground
            r6.setVisibility(r9)
            android.widget.FrameLayout r6 = r0.containerLayout
            android.view.View r8 = r0.progressBarBlackBackground
            r10 = -1
            r11 = -1082130432(0xffffffffbvar_, float:-1.0)
            r12 = 51
            r13 = 0
            r14 = 0
            r15 = 0
            boolean r5 = r0.hasDescription
            if (r5 == 0) goto L_0x018a
            r5 = 22
            goto L_0x018b
        L_0x018a:
            r5 = 0
        L_0x018b:
            int r5 = r5 + 84
            float r5 = (float) r5
            r16 = r5
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r11, r12, r13, r14, r15, r16)
            r6.addView(r8, r5)
            org.telegram.ui.Components.RadialProgressView r5 = new org.telegram.ui.Components.RadialProgressView
            r5.<init>(r1)
            r0.progressBar = r5
            org.telegram.ui.Components.RadialProgressView r5 = r0.progressBar
            r5.setVisibility(r9)
            android.widget.FrameLayout r5 = r0.containerLayout
            org.telegram.ui.Components.RadialProgressView r6 = r0.progressBar
            r10 = -2
            r11 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r12 = 17
            r13 = 0
            r14 = 0
            r15 = 0
            boolean r8 = r0.hasDescription
            if (r8 == 0) goto L_0x01b4
            goto L_0x01b6
        L_0x01b4:
            r19 = 0
        L_0x01b6:
            int r19 = r19 + 84
            int r4 = r19 / 2
            float r4 = (float) r4
            r16 = r4
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r11, r12, r13, r14, r15, r16)
            r5.addView(r6, r4)
            boolean r4 = r0.hasDescription
            java.lang.String r5 = "fonts/rmedium.ttf"
            r6 = 1099956224(0x41900000, float:18.0)
            if (r4 == 0) goto L_0x020f
            android.widget.TextView r4 = new android.widget.TextView
            r4.<init>(r1)
            r8 = 1098907648(0x41800000, float:16.0)
            r4.setTextSize(r7, r8)
            java.lang.String r8 = "dialogTextBlack"
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            r4.setTextColor(r8)
            r4.setText(r2)
            r4.setSingleLine(r7)
            android.graphics.Typeface r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r5)
            r4.setTypeface(r2)
            android.text.TextUtils$TruncateAt r2 = android.text.TextUtils.TruncateAt.END
            r4.setEllipsize(r2)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r4.setPadding(r2, r3, r8, r3)
            android.widget.FrameLayout r2 = r0.containerLayout
            r10 = -1
            r11 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r12 = 83
            r13 = 0
            r14 = 0
            r15 = 0
            r16 = 1117388800(0x429a0000, float:77.0)
            android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r11, r12, r13, r14, r15, r16)
            r2.addView(r4, r8)
        L_0x020f:
            android.widget.TextView r2 = new android.widget.TextView
            r2.<init>(r1)
            r4 = 1096810496(0x41600000, float:14.0)
            r2.setTextSize(r7, r4)
            java.lang.String r8 = "dialogTextGray"
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            r2.setTextColor(r8)
            r8 = r28
            r2.setText(r8)
            r2.setSingleLine(r7)
            android.text.TextUtils$TruncateAt r8 = android.text.TextUtils.TruncateAt.END
            r2.setEllipsize(r8)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r2.setPadding(r8, r3, r10, r3)
            android.widget.FrameLayout r8 = r0.containerLayout
            r10 = -1
            r11 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r12 = 83
            r13 = 0
            r14 = 0
            r15 = 0
            r16 = 1113849856(0x42640000, float:57.0)
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r11, r12, r13, r14, r15, r16)
            r8.addView(r2, r10)
            android.view.View r2 = new android.view.View
            r2.<init>(r1)
            java.lang.String r8 = "dialogGrayLine"
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            r2.setBackgroundColor(r8)
            android.widget.FrameLayout r8 = r0.containerLayout
            android.widget.FrameLayout$LayoutParams r10 = new android.widget.FrameLayout$LayoutParams
            r11 = 83
            r12 = -1
            r10.<init>(r12, r7, r11)
            r8.addView(r2, r10)
            android.view.ViewGroup$LayoutParams r2 = r2.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r2 = (android.widget.FrameLayout.LayoutParams) r2
            r8 = 1111490560(0x42400000, float:48.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r2.bottomMargin = r8
            android.widget.FrameLayout r2 = new android.widget.FrameLayout
            r2.<init>(r1)
            java.lang.String r8 = "dialogBackground"
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            r2.setBackgroundColor(r8)
            android.widget.FrameLayout r8 = r0.containerLayout
            r10 = 48
            r12 = -1
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r12, (int) r10, (int) r11)
            r8.addView(r2, r10)
            android.widget.LinearLayout r8 = new android.widget.LinearLayout
            r8.<init>(r1)
            r8.setOrientation(r3)
            r10 = 1065353216(0x3var_, float:1.0)
            r8.setWeightSum(r10)
            r10 = 53
            r11 = -2
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r11, (int) r12, (int) r10)
            r2.addView(r8, r10)
            android.widget.TextView r10 = new android.widget.TextView
            r10.<init>(r1)
            r10.setTextSize(r7, r4)
            java.lang.String r11 = "dialogTextBlue4"
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r10.setTextColor(r12)
            r12 = 17
            r10.setGravity(r12)
            r10.setSingleLine(r7)
            android.text.TextUtils$TruncateAt r12 = android.text.TextUtils.TruncateAt.END
            r10.setEllipsize(r12)
            java.lang.String r12 = "dialogButtonSelector"
            int r13 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r13, r3)
            r10.setBackgroundDrawable(r13)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r10.setPadding(r13, r3, r14, r3)
            r13 = 2131624716(0x7f0e030c, float:1.887662E38)
            java.lang.String r14 = "Close"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r14, r13)
            java.lang.String r13 = r13.toUpperCase()
            r10.setText(r13)
            android.graphics.Typeface r13 = org.telegram.messenger.AndroidUtilities.getTypeface(r5)
            r10.setTypeface(r13)
            r13 = 51
            r14 = -2
            r15 = -1
            android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r14, (int) r15, (int) r13)
            r2.addView(r10, r6)
            org.telegram.ui.Components.-$$Lambda$EmbedBottomSheet$7BVC7l6jtG_KhdXyPoXIswQ3uh8 r6 = new org.telegram.ui.Components.-$$Lambda$EmbedBottomSheet$7BVC7l6jtG_KhdXyPoXIswQ3uh8
            r6.<init>()
            r10.setOnClickListener(r6)
            android.widget.LinearLayout r6 = new android.widget.LinearLayout
            r6.<init>(r1)
            r0.imageButtonsContainer = r6
            android.widget.LinearLayout r6 = r0.imageButtonsContainer
            r6.setVisibility(r9)
            android.widget.LinearLayout r6 = r0.imageButtonsContainer
            r10 = 17
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r14, (int) r15, (int) r10)
            r2.addView(r6, r9)
            android.widget.ImageView r2 = new android.widget.ImageView
            r2.<init>(r1)
            r0.pipButton = r2
            android.widget.ImageView r2 = r0.pipButton
            android.widget.ImageView$ScaleType r6 = android.widget.ImageView.ScaleType.CENTER
            r2.setScaleType(r6)
            android.widget.ImageView r2 = r0.pipButton
            r6 = 2131165934(0x7var_ee, float:1.79461E38)
            r2.setImageResource(r6)
            android.widget.ImageView r2 = r0.pipButton
            r2.setEnabled(r3)
            android.widget.ImageView r2 = r0.pipButton
            r6 = 1056964608(0x3var_, float:0.5)
            r2.setAlpha(r6)
            android.widget.ImageView r2 = r0.pipButton
            android.graphics.PorterDuffColorFilter r6 = new android.graphics.PorterDuffColorFilter
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            android.graphics.PorterDuff$Mode r10 = android.graphics.PorterDuff.Mode.MULTIPLY
            r6.<init>(r9, r10)
            r2.setColorFilter(r6)
            android.widget.ImageView r2 = r0.pipButton
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r6, r3)
            r2.setBackgroundDrawable(r6)
            android.widget.LinearLayout r2 = r0.imageButtonsContainer
            android.widget.ImageView r6 = r0.pipButton
            r19 = 48
            r20 = 1111490560(0x42400000, float:48.0)
            r21 = 51
            r22 = 0
            r23 = 0
            r24 = 1082130432(0x40800000, float:4.0)
            r25 = 0
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25)
            r2.addView(r6, r9)
            android.widget.ImageView r2 = r0.pipButton
            org.telegram.ui.Components.-$$Lambda$EmbedBottomSheet$y37DL-JjK8uMG3joIQ1jW7ubjuE r6 = new org.telegram.ui.Components.-$$Lambda$EmbedBottomSheet$y37DL-JjK8uMG3joIQ1jW7ubjuE
            r6.<init>()
            r2.setOnClickListener(r6)
            org.telegram.ui.Components.-$$Lambda$EmbedBottomSheet$ME3BZek9Olzkn_G2jOkI4EyTOfQ r2 = new org.telegram.ui.Components.-$$Lambda$EmbedBottomSheet$ME3BZek9Olzkn_G2jOkI4EyTOfQ
            r2.<init>()
            android.widget.ImageView r6 = new android.widget.ImageView
            r6.<init>(r1)
            android.widget.ImageView$ScaleType r9 = android.widget.ImageView.ScaleType.CENTER
            r6.setScaleType(r9)
            r9 = 2131165927(0x7var_e7, float:1.7946085E38)
            r6.setImageResource(r9)
            android.graphics.PorterDuffColorFilter r9 = new android.graphics.PorterDuffColorFilter
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            android.graphics.PorterDuff$Mode r14 = android.graphics.PorterDuff.Mode.MULTIPLY
            r9.<init>(r10, r14)
            r6.setColorFilter(r9)
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r9, r3)
            r6.setBackgroundDrawable(r9)
            android.widget.LinearLayout r9 = r0.imageButtonsContainer
            r10 = 48
            r14 = 48
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r10, (int) r14, (int) r13)
            r9.addView(r6, r10)
            r6.setOnClickListener(r2)
            android.widget.TextView r6 = new android.widget.TextView
            r6.<init>(r1)
            r0.copyTextButton = r6
            android.widget.TextView r6 = r0.copyTextButton
            r6.setTextSize(r7, r4)
            android.widget.TextView r6 = r0.copyTextButton
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r6.setTextColor(r9)
            android.widget.TextView r6 = r0.copyTextButton
            r9 = 17
            r6.setGravity(r9)
            android.widget.TextView r6 = r0.copyTextButton
            r6.setSingleLine(r7)
            android.widget.TextView r6 = r0.copyTextButton
            android.text.TextUtils$TruncateAt r9 = android.text.TextUtils.TruncateAt.END
            r6.setEllipsize(r9)
            android.widget.TextView r6 = r0.copyTextButton
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r9, r3)
            r6.setBackgroundDrawable(r9)
            android.widget.TextView r6 = r0.copyTextButton
            r9 = 1099956224(0x41900000, float:18.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r6.setPadding(r10, r3, r14, r3)
            android.widget.TextView r6 = r0.copyTextButton
            r9 = 2131624770(0x7f0e0342, float:1.887673E38)
            java.lang.String r10 = "Copy"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            java.lang.String r9 = r9.toUpperCase()
            r6.setText(r9)
            android.widget.TextView r6 = r0.copyTextButton
            android.graphics.Typeface r9 = org.telegram.messenger.AndroidUtilities.getTypeface(r5)
            r6.setTypeface(r9)
            android.widget.TextView r6 = r0.copyTextButton
            r9 = -2
            r10 = -1
            android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r9, (int) r10, (int) r13)
            r8.addView(r6, r14)
            android.widget.TextView r6 = r0.copyTextButton
            r6.setOnClickListener(r2)
            android.widget.TextView r2 = new android.widget.TextView
            r2.<init>(r1)
            r2.setTextSize(r7, r4)
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r2.setTextColor(r1)
            r1 = 17
            r2.setGravity(r1)
            r2.setSingleLine(r7)
            android.text.TextUtils$TruncateAt r1 = android.text.TextUtils.TruncateAt.END
            r2.setEllipsize(r1)
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r1, r3)
            r2.setBackgroundDrawable(r1)
            r1 = 1099956224(0x41900000, float:18.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r2.setPadding(r4, r3, r1, r3)
            r1 = 2131625865(0x7f0e0789, float:1.887895E38)
            java.lang.String r4 = "OpenInBrowser"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r4, r1)
            java.lang.String r1 = r1.toUpperCase()
            r2.setText(r1)
            android.graphics.Typeface r1 = org.telegram.messenger.AndroidUtilities.getTypeface(r5)
            r2.setTypeface(r1)
            r1 = -2
            r4 = -1
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r1, (int) r4, (int) r13)
            r8.addView(r2, r1)
            org.telegram.ui.Components.-$$Lambda$EmbedBottomSheet$xNtglytXCFyYO3cCP1Moj3HVHKg r1 = new org.telegram.ui.Components.-$$Lambda$EmbedBottomSheet$xNtglytXCFyYO3cCP1Moj3HVHKg
            r1.<init>()
            r2.setOnClickListener(r1)
            org.telegram.ui.Components.EmbedBottomSheet$7 r1 = new org.telegram.ui.Components.EmbedBottomSheet$7
            r1.<init>()
            r0.setDelegate(r1)
            org.telegram.ui.Components.EmbedBottomSheet$8 r1 = new org.telegram.ui.Components.EmbedBottomSheet$8
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext
            r1.<init>(r2)
            r0.orientationEventListener = r1
            org.telegram.ui.Components.WebPlayerView r1 = r0.videoView
            java.lang.String r2 = r0.embedUrl
            java.lang.String r1 = r1.getYouTubeVideoId(r2)
            if (r1 == 0) goto L_0x0502
            org.telegram.ui.Components.RadialProgressView r1 = r0.progressBar
            r1.setVisibility(r3)
            android.webkit.WebView r1 = r0.webView
            r1.setVisibility(r3)
            android.widget.LinearLayout r1 = r0.imageButtonsContainer
            r1.setVisibility(r3)
            android.view.View r1 = r0.progressBarBlackBackground
            r1.setVisibility(r3)
            android.widget.TextView r1 = r0.copyTextButton
            r2 = 4
            r1.setVisibility(r2)
            android.webkit.WebView r1 = r0.webView
            r1.setKeepScreenOn(r7)
            org.telegram.ui.Components.WebPlayerView r1 = r0.videoView
            r1.setVisibility(r2)
            org.telegram.ui.Components.WebPlayerView r1 = r0.videoView
            android.view.View r1 = r1.getControlsView()
            r1.setVisibility(r2)
            org.telegram.ui.Components.WebPlayerView r1 = r0.videoView
            android.view.TextureView r1 = r1.getTextureView()
            r1.setVisibility(r2)
            org.telegram.ui.Components.WebPlayerView r1 = r0.videoView
            android.widget.ImageView r1 = r1.getTextureImageView()
            if (r1 == 0) goto L_0x04eb
            org.telegram.ui.Components.WebPlayerView r1 = r0.videoView
            android.widget.ImageView r1 = r1.getTextureImageView()
            r1.setVisibility(r2)
        L_0x04eb:
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            java.lang.String r1 = r1.youtubePipType
            java.lang.String r2 = "disabled"
            boolean r1 = r2.equals(r1)
            if (r1 == 0) goto L_0x0502
            android.widget.ImageView r1 = r0.pipButton
            r2 = 8
            r1.setVisibility(r2)
        L_0x0502:
            android.view.OrientationEventListener r1 = r0.orientationEventListener
            boolean r1 = r1.canDetectOrientation()
            if (r1 == 0) goto L_0x0510
            android.view.OrientationEventListener r1 = r0.orientationEventListener
            r1.enable()
            goto L_0x0518
        L_0x0510:
            android.view.OrientationEventListener r1 = r0.orientationEventListener
            r1.disable()
            r1 = 0
            r0.orientationEventListener = r1
        L_0x0518:
            instance = r0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmbedBottomSheet.<init>(android.content.Context, java.lang.String, java.lang.String, java.lang.String, java.lang.String, int, int, int):void");
    }

    public /* synthetic */ void lambda$new$3$EmbedBottomSheet(View view) {
        dismiss();
    }

    public /* synthetic */ void lambda$new$4$EmbedBottomSheet(View view) {
        int i;
        boolean z = this.isYouTube && "inapp".equals(MessagesController.getInstance(this.currentAccount).youtubePipType);
        if ((z || checkInlinePermissions()) && this.progressBar.getVisibility() != 0) {
            this.pipVideoView = new PipVideoView(z);
            PipVideoView pipVideoView2 = this.pipVideoView;
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

    public /* synthetic */ void lambda$new$5$EmbedBottomSheet(View view) {
        try {
            ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", this.openUrl));
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        Toast.makeText(getContext(), LocaleController.getString("LinkCopied", NUM), 0).show();
        dismiss();
    }

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
        new AlertDialog.Builder((Context) this.parentActivity).setTitle(LocaleController.getString("AppName", NUM)).setMessage(LocaleController.getString("PermissionDrawAboveOtherApps", NUM)).setPositiveButton(LocaleController.getString("PermissionOpenSettings", NUM), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                EmbedBottomSheet.this.lambda$checkInlinePermissions$7$EmbedBottomSheet(dialogInterface, i);
            }
        }).show();
        return false;
    }

    public /* synthetic */ void lambda$checkInlinePermissions$7$EmbedBottomSheet(DialogInterface dialogInterface, int i) {
        Activity activity = this.parentActivity;
        if (activity != null) {
            activity.startActivity(new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse("package:" + this.parentActivity.getPackageName())));
        }
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
            this.waitingForDraw = i - 1;
            if (this.waitingForDraw == 0) {
                this.videoView.updateTextureImageView();
                this.pipVideoView.close();
                this.pipVideoView = null;
                return;
            }
            this.container.invalidate();
        }
    }
}
