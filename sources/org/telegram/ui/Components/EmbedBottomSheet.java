package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
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
import android.view.animation.DecelerateInterpolator;
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
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.browser.Browser;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PhotoViewer;

public class EmbedBottomSheet extends BottomSheet {
    /* access modifiers changed from: private */
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
        public void onShow(DialogInterface dialog) {
            if (PipVideoOverlay.isVisible() && EmbedBottomSheet.this.videoView.isInline()) {
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

    private class YoutubeProxy {
        private YoutubeProxy() {
        }

        @JavascriptInterface
        public void postEvent(String eventName, String eventData) {
            if ("loaded".equals(eventName)) {
                AndroidUtilities.runOnUIThread(new EmbedBottomSheet$YoutubeProxy$$ExternalSyntheticLambda0(this));
            }
        }

        /* renamed from: lambda$postEvent$0$org-telegram-ui-Components-EmbedBottomSheet$YoutubeProxy  reason: not valid java name */
        public /* synthetic */ void m3960x10d0a2ae() {
            EmbedBottomSheet.this.progressBar.setVisibility(4);
            EmbedBottomSheet.this.progressBarBlackBackground.setVisibility(4);
            EmbedBottomSheet.this.pipButton.setEnabled(true);
            EmbedBottomSheet.this.pipButton.setAlpha(1.0f);
        }
    }

    public static void show(Activity activity, MessageObject message, PhotoViewer.PhotoViewerProvider photoViewerProvider, String title, String description, String originalUrl, String url, int w, int h, boolean keyboardVisible) {
        show(activity, message, photoViewerProvider, title, description, originalUrl, url, w, h, -1, keyboardVisible);
    }

    public static void show(Activity activity, MessageObject message, PhotoViewer.PhotoViewerProvider photoViewerProvider, String title, String description, String originalUrl, String url, int w, int h, int seekTime, boolean keyboardVisible) {
        MessageObject messageObject = message;
        EmbedBottomSheet embedBottomSheet = instance;
        if (embedBottomSheet != null) {
            embedBottomSheet.destroy();
        }
        if (((messageObject == null || messageObject.messageOwner.media == null || messageObject.messageOwner.media.webpage == null) ? null : WebPlayerView.getYouTubeVideoId(url)) != null) {
            PhotoViewer.getInstance().setParentActivity(activity);
            PhotoViewer.getInstance().openPhoto(message, seekTime, (ChatActivity) null, 0, 0, photoViewerProvider);
            boolean z = keyboardVisible;
            return;
        }
        Activity activity2 = activity;
        EmbedBottomSheet embedBottomSheet2 = new EmbedBottomSheet(activity, title, description, originalUrl, url, w, h, seekTime);
        embedBottomSheet2.setCalcMandatoryInsets(keyboardVisible);
        embedBottomSheet2.show();
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private EmbedBottomSheet(android.content.Context r33, java.lang.String r34, java.lang.String r35, java.lang.String r36, java.lang.String r37, int r38, int r39, int r40) {
        /*
            r32 = this;
            r0 = r32
            r1 = r33
            r2 = r35
            r3 = r36
            r4 = r38
            r5 = r39
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
            r11 = r40
            r0.seekTimeOverride = r11
            boolean r12 = r1 instanceof android.app.Activity
            if (r12 == 0) goto L_0x003c
            r12 = r1
            android.app.Activity r12 = (android.app.Activity) r12
            r0.parentActivity = r12
        L_0x003c:
            r12 = r37
            r0.embedUrl = r12
            if (r2 == 0) goto L_0x004a
            int r13 = r35.length()
            if (r13 <= 0) goto L_0x004a
            r13 = 1
            goto L_0x004b
        L_0x004a:
            r13 = 0
        L_0x004b:
            r0.hasDescription = r13
            r0.openUrl = r3
            r0.width = r4
            r0.height = r5
            if (r4 == 0) goto L_0x0057
            if (r5 != 0) goto L_0x0064
        L_0x0057:
            android.graphics.Point r13 = org.telegram.messenger.AndroidUtilities.displaySize
            int r13 = r13.x
            r0.width = r13
            android.graphics.Point r13 = org.telegram.messenger.AndroidUtilities.displaySize
            int r13 = r13.y
            int r13 = r13 / r7
            r0.height = r13
        L_0x0064:
            android.widget.FrameLayout r13 = new android.widget.FrameLayout
            r13.<init>(r1)
            r0.fullscreenVideoContainer = r13
            r13.setKeepScreenOn(r10)
            android.widget.FrameLayout r13 = r0.fullscreenVideoContainer
            r14 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r13.setBackgroundColor(r14)
            int r13 = android.os.Build.VERSION.SDK_INT
            r15 = 21
            if (r13 < r15) goto L_0x0080
            android.widget.FrameLayout r13 = r0.fullscreenVideoContainer
            r13.setFitsSystemWindows(r10)
        L_0x0080:
            android.widget.FrameLayout r13 = r0.fullscreenVideoContainer
            org.telegram.ui.Components.EmbedBottomSheet$$ExternalSyntheticLambda4 r9 = org.telegram.ui.Components.EmbedBottomSheet$$ExternalSyntheticLambda4.INSTANCE
            r13.setOnTouchListener(r9)
            org.telegram.ui.ActionBar.BottomSheet$ContainerView r9 = r0.container
            android.widget.FrameLayout r13 = r0.fullscreenVideoContainer
            r7 = -1082130432(0xffffffffbvar_, float:-1.0)
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r7)
            r9.addView(r13, r7)
            android.widget.FrameLayout r7 = r0.fullscreenVideoContainer
            r9 = 4
            r7.setVisibility(r9)
            org.telegram.ui.Components.EmbedBottomSheet$2 r7 = new org.telegram.ui.Components.EmbedBottomSheet$2
            r7.<init>(r1)
            r0.containerLayout = r7
            org.telegram.ui.Components.EmbedBottomSheet$$ExternalSyntheticLambda5 r13 = org.telegram.ui.Components.EmbedBottomSheet$$ExternalSyntheticLambda5.INSTANCE
            r7.setOnTouchListener(r13)
            android.widget.FrameLayout r7 = r0.containerLayout
            r0.setCustomView(r7)
            org.telegram.ui.Components.EmbedBottomSheet$3 r7 = new org.telegram.ui.Components.EmbedBottomSheet$3
            r7.<init>(r1)
            r0.webView = r7
            android.webkit.WebSettings r7 = r7.getSettings()
            r7.setJavaScriptEnabled(r10)
            android.webkit.WebView r7 = r0.webView
            android.webkit.WebSettings r7 = r7.getSettings()
            r7.setDomStorageEnabled(r10)
            int r7 = android.os.Build.VERSION.SDK_INT
            r13 = 17
            if (r7 < r13) goto L_0x00d1
            android.webkit.WebView r7 = r0.webView
            android.webkit.WebSettings r7 = r7.getSettings()
            r7.setMediaPlaybackRequiresUserGesture(r6)
        L_0x00d1:
            int r7 = android.os.Build.VERSION.SDK_INT
            if (r7 < r15) goto L_0x00e7
            android.webkit.WebView r7 = r0.webView
            android.webkit.WebSettings r7 = r7.getSettings()
            r7.setMixedContentMode(r6)
            android.webkit.CookieManager r7 = android.webkit.CookieManager.getInstance()
            android.webkit.WebView r15 = r0.webView
            r7.setAcceptThirdPartyCookies(r15, r10)
        L_0x00e7:
            android.webkit.WebView r7 = r0.webView
            org.telegram.ui.Components.EmbedBottomSheet$4 r15 = new org.telegram.ui.Components.EmbedBottomSheet$4
            r15.<init>()
            r7.setWebChromeClient(r15)
            android.webkit.WebView r7 = r0.webView
            org.telegram.ui.Components.EmbedBottomSheet$5 r15 = new org.telegram.ui.Components.EmbedBottomSheet$5
            r15.<init>()
            r7.setWebViewClient(r15)
            android.widget.FrameLayout r7 = r0.containerLayout
            android.webkit.WebView r15 = r0.webView
            r16 = -1
            r17 = -1082130432(0xffffffffbvar_, float:-1.0)
            r18 = 51
            r19 = 0
            r20 = 0
            r21 = 0
            boolean r13 = r0.hasDescription
            r23 = 22
            if (r13 == 0) goto L_0x0114
            r13 = 22
            goto L_0x0115
        L_0x0114:
            r13 = 0
        L_0x0115:
            int r13 = r13 + 84
            float r13 = (float) r13
            r22 = r13
            android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
            r7.addView(r15, r13)
            org.telegram.ui.Components.WebPlayerView r7 = new org.telegram.ui.Components.WebPlayerView
            org.telegram.ui.Components.EmbedBottomSheet$6 r13 = new org.telegram.ui.Components.EmbedBottomSheet$6
            r13.<init>()
            r7.<init>(r1, r10, r6, r13)
            r0.videoView = r7
            r7.setVisibility(r9)
            android.widget.FrameLayout r7 = r0.containerLayout
            org.telegram.ui.Components.WebPlayerView r13 = r0.videoView
            r16 = -1
            r17 = -1082130432(0xffffffffbvar_, float:-1.0)
            r18 = 51
            r19 = 0
            r20 = 0
            r21 = 0
            boolean r15 = r0.hasDescription
            if (r15 == 0) goto L_0x0147
            r15 = 22
            goto L_0x0148
        L_0x0147:
            r15 = 0
        L_0x0148:
            int r15 = r15 + 84
            int r15 = r15 + -10
            float r15 = (float) r15
            r22 = r15
            android.widget.FrameLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
            r7.addView(r13, r15)
            android.view.View r7 = new android.view.View
            r7.<init>(r1)
            r0.progressBarBlackBackground = r7
            r7.setBackgroundColor(r14)
            android.view.View r7 = r0.progressBarBlackBackground
            r7.setVisibility(r9)
            android.widget.FrameLayout r7 = r0.containerLayout
            android.view.View r13 = r0.progressBarBlackBackground
            r16 = -1
            r17 = -1082130432(0xffffffffbvar_, float:-1.0)
            r18 = 51
            r19 = 0
            r20 = 0
            r21 = 0
            boolean r14 = r0.hasDescription
            if (r14 == 0) goto L_0x017c
            r14 = 22
            goto L_0x017d
        L_0x017c:
            r14 = 0
        L_0x017d:
            int r14 = r14 + 84
            float r14 = (float) r14
            r22 = r14
            android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
            r7.addView(r13, r14)
            org.telegram.ui.Components.RadialProgressView r7 = new org.telegram.ui.Components.RadialProgressView
            r7.<init>(r1)
            r0.progressBar = r7
            r7.setVisibility(r9)
            android.widget.FrameLayout r7 = r0.containerLayout
            org.telegram.ui.Components.RadialProgressView r13 = r0.progressBar
            r16 = -2
            r17 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r18 = 17
            r19 = 0
            r20 = 0
            r21 = 0
            boolean r14 = r0.hasDescription
            if (r14 == 0) goto L_0x01a8
            goto L_0x01aa
        L_0x01a8:
            r23 = 0
        L_0x01aa:
            int r23 = r23 + 84
            r14 = 2
            int r14 = r23 / 2
            float r14 = (float) r14
            r22 = r14
            android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
            r7.addView(r13, r14)
            boolean r7 = r0.hasDescription
            java.lang.String r13 = "fonts/rmedium.ttf"
            r14 = 1099956224(0x41900000, float:18.0)
            if (r7 == 0) goto L_0x0208
            android.widget.TextView r7 = new android.widget.TextView
            r7.<init>(r1)
            r15 = 1098907648(0x41800000, float:16.0)
            r7.setTextSize(r10, r15)
            java.lang.String r15 = "dialogTextBlack"
            int r15 = org.telegram.ui.ActionBar.Theme.getColor(r15)
            r7.setTextColor(r15)
            r7.setText(r2)
            r7.setSingleLine(r10)
            android.graphics.Typeface r15 = org.telegram.messenger.AndroidUtilities.getTypeface(r13)
            r7.setTypeface(r15)
            android.text.TextUtils$TruncateAt r15 = android.text.TextUtils.TruncateAt.END
            r7.setEllipsize(r15)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r14)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r14)
            r7.setPadding(r15, r6, r9, r6)
            android.widget.FrameLayout r9 = r0.containerLayout
            r16 = -1
            r17 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r18 = 83
            r19 = 0
            r20 = 0
            r21 = 0
            r22 = 1117388800(0x429a0000, float:77.0)
            android.widget.FrameLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
            r9.addView(r7, r15)
        L_0x0208:
            android.widget.TextView r7 = new android.widget.TextView
            r7.<init>(r1)
            r9 = 1096810496(0x41600000, float:14.0)
            r7.setTextSize(r10, r9)
            java.lang.String r15 = "dialogTextGray"
            int r15 = org.telegram.ui.ActionBar.Theme.getColor(r15)
            r7.setTextColor(r15)
            r15 = r34
            r7.setText(r15)
            r7.setSingleLine(r10)
            android.text.TextUtils$TruncateAt r9 = android.text.TextUtils.TruncateAt.END
            r7.setEllipsize(r9)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r14)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r14)
            r7.setPadding(r9, r6, r8, r6)
            android.widget.FrameLayout r8 = r0.containerLayout
            r23 = -1
            r24 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r25 = 83
            r26 = 0
            r27 = 0
            r28 = 0
            r29 = 1113849856(0x42640000, float:57.0)
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r23, r24, r25, r26, r27, r28, r29)
            r8.addView(r7, r9)
            android.view.View r8 = new android.view.View
            r8.<init>(r1)
            java.lang.String r9 = "dialogGrayLine"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r8.setBackgroundColor(r9)
            android.widget.FrameLayout r9 = r0.containerLayout
            android.widget.FrameLayout$LayoutParams r14 = new android.widget.FrameLayout$LayoutParams
            r6 = 83
            r2 = -1
            r14.<init>(r2, r10, r6)
            r9.addView(r8, r14)
            android.view.ViewGroup$LayoutParams r2 = r8.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r2 = (android.widget.FrameLayout.LayoutParams) r2
            r9 = 1111490560(0x42400000, float:48.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r2.bottomMargin = r9
            android.widget.FrameLayout r2 = new android.widget.FrameLayout
            r2.<init>(r1)
            java.lang.String r9 = "dialogBackground"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r2.setBackgroundColor(r9)
            android.widget.FrameLayout r9 = r0.containerLayout
            r14 = 48
            r10 = -1
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r10, (int) r14, (int) r6)
            r9.addView(r2, r6)
            android.widget.LinearLayout r6 = new android.widget.LinearLayout
            r6.<init>(r1)
            r9 = 0
            r6.setOrientation(r9)
            r9 = 1065353216(0x3var_, float:1.0)
            r6.setWeightSum(r9)
            r9 = 53
            r10 = -2
            r14 = -1
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r10, (int) r14, (int) r9)
            r2.addView(r6, r9)
            android.widget.TextView r9 = new android.widget.TextView
            r9.<init>(r1)
            r7 = r9
            r9 = 1096810496(0x41600000, float:14.0)
            r10 = 1
            r7.setTextSize(r10, r9)
            java.lang.String r9 = "dialogTextBlue4"
            int r14 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r7.setTextColor(r14)
            r14 = 17
            r7.setGravity(r14)
            r7.setSingleLine(r10)
            android.text.TextUtils$TruncateAt r10 = android.text.TextUtils.TruncateAt.END
            r7.setEllipsize(r10)
            java.lang.String r10 = "dialogButtonSelector"
            int r14 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            r4 = 0
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r14, r4)
            r7.setBackgroundDrawable(r14)
            r14 = 1099956224(0x41900000, float:18.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r14)
            r22 = r8
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r14)
            r7.setPadding(r5, r4, r8, r4)
            r4 = 2131625076(0x7f0e0474, float:1.887735E38)
            java.lang.String r5 = "Close"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            java.lang.String r4 = r4.toUpperCase()
            r7.setText(r4)
            android.graphics.Typeface r4 = org.telegram.messenger.AndroidUtilities.getTypeface(r13)
            r7.setTypeface(r4)
            r4 = 51
            r5 = -2
            r8 = -1
            android.widget.LinearLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r5, (int) r8, (int) r4)
            r2.addView(r7, r14)
            org.telegram.ui.Components.EmbedBottomSheet$$ExternalSyntheticLambda0 r5 = new org.telegram.ui.Components.EmbedBottomSheet$$ExternalSyntheticLambda0
            r5.<init>(r0)
            r7.setOnClickListener(r5)
            android.widget.LinearLayout r5 = new android.widget.LinearLayout
            r5.<init>(r1)
            r0.imageButtonsContainer = r5
            r8 = 4
            r5.setVisibility(r8)
            android.widget.LinearLayout r5 = r0.imageButtonsContainer
            r24 = r7
            r4 = -1
            r8 = 17
            r14 = -2
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r14, (int) r4, (int) r8)
            r2.addView(r5, r7)
            android.widget.ImageView r4 = new android.widget.ImageView
            r4.<init>(r1)
            r0.pipButton = r4
            android.widget.ImageView$ScaleType r5 = android.widget.ImageView.ScaleType.CENTER
            r4.setScaleType(r5)
            android.widget.ImageView r4 = r0.pipButton
            r5 = 2131166223(0x7var_f, float:1.7946685E38)
            r4.setImageResource(r5)
            android.widget.ImageView r4 = r0.pipButton
            r5 = 2131624011(0x7f0e004b, float:1.887519E38)
            java.lang.String r7 = "AccDescrPipMode"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r7, r5)
            r4.setContentDescription(r5)
            android.widget.ImageView r4 = r0.pipButton
            r5 = 0
            r4.setEnabled(r5)
            android.widget.ImageView r4 = r0.pipButton
            r5 = 1056964608(0x3var_, float:0.5)
            r4.setAlpha(r5)
            android.widget.ImageView r4 = r0.pipButton
            android.graphics.PorterDuffColorFilter r5 = new android.graphics.PorterDuffColorFilter
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            android.graphics.PorterDuff$Mode r8 = android.graphics.PorterDuff.Mode.MULTIPLY
            r5.<init>(r7, r8)
            r4.setColorFilter(r5)
            android.widget.ImageView r4 = r0.pipButton
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            r7 = 0
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r5, r7)
            r4.setBackgroundDrawable(r5)
            android.widget.LinearLayout r4 = r0.imageButtonsContainer
            android.widget.ImageView r5 = r0.pipButton
            r25 = 48
            r26 = 1111490560(0x42400000, float:48.0)
            r27 = 51
            r29 = 0
            r30 = 1082130432(0x40800000, float:4.0)
            r31 = 0
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r25, r26, r27, r28, r29, r30, r31)
            r4.addView(r5, r7)
            android.widget.ImageView r4 = r0.pipButton
            org.telegram.ui.Components.EmbedBottomSheet$$ExternalSyntheticLambda1 r5 = new org.telegram.ui.Components.EmbedBottomSheet$$ExternalSyntheticLambda1
            r5.<init>(r0)
            r4.setOnClickListener(r5)
            org.telegram.ui.Components.EmbedBottomSheet$$ExternalSyntheticLambda2 r4 = new org.telegram.ui.Components.EmbedBottomSheet$$ExternalSyntheticLambda2
            r4.<init>(r0)
            android.widget.ImageView r5 = new android.widget.ImageView
            r5.<init>(r1)
            android.widget.ImageView$ScaleType r7 = android.widget.ImageView.ScaleType.CENTER
            r5.setScaleType(r7)
            r7 = 2131166215(0x7var_, float:1.794667E38)
            r5.setImageResource(r7)
            r7 = 2131625165(0x7f0e04cd, float:1.887753E38)
            java.lang.String r8 = "CopyLink"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            r5.setContentDescription(r7)
            android.graphics.PorterDuffColorFilter r7 = new android.graphics.PorterDuffColorFilter
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            android.graphics.PorterDuff$Mode r14 = android.graphics.PorterDuff.Mode.MULTIPLY
            r7.<init>(r8, r14)
            r5.setColorFilter(r7)
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            r8 = 0
            android.graphics.drawable.Drawable r7 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r7, r8)
            r5.setBackgroundDrawable(r7)
            android.widget.LinearLayout r7 = r0.imageButtonsContainer
            r8 = 48
            r14 = 51
            android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r8, (int) r8, (int) r14)
            r7.addView(r5, r8)
            r5.setOnClickListener(r4)
            android.widget.TextView r7 = new android.widget.TextView
            r7.<init>(r1)
            r0.copyTextButton = r7
            r8 = 1096810496(0x41600000, float:14.0)
            r14 = 1
            r7.setTextSize(r14, r8)
            android.widget.TextView r7 = r0.copyTextButton
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r7.setTextColor(r8)
            android.widget.TextView r7 = r0.copyTextButton
            r8 = 17
            r7.setGravity(r8)
            android.widget.TextView r7 = r0.copyTextButton
            r7.setSingleLine(r14)
            android.widget.TextView r7 = r0.copyTextButton
            android.text.TextUtils$TruncateAt r8 = android.text.TextUtils.TruncateAt.END
            r7.setEllipsize(r8)
            android.widget.TextView r7 = r0.copyTextButton
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            r14 = 0
            android.graphics.drawable.Drawable r8 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r8, r14)
            r7.setBackgroundDrawable(r8)
            android.widget.TextView r7 = r0.copyTextButton
            r21 = r2
            r8 = 1099956224(0x41900000, float:18.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r25 = r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r7.setPadding(r2, r14, r5, r14)
            android.widget.TextView r2 = r0.copyTextButton
            r5 = 2131625163(0x7f0e04cb, float:1.8877526E38)
            java.lang.String r7 = "Copy"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r7, r5)
            java.lang.String r5 = r5.toUpperCase()
            r2.setText(r5)
            android.widget.TextView r2 = r0.copyTextButton
            android.graphics.Typeface r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r13)
            r2.setTypeface(r5)
            android.widget.TextView r2 = r0.copyTextButton
            r5 = 51
            r7 = -2
            r8 = -1
            android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r7, (int) r8, (int) r5)
            r6.addView(r2, r14)
            android.widget.TextView r2 = r0.copyTextButton
            r2.setOnClickListener(r4)
            android.widget.TextView r2 = new android.widget.TextView
            r2.<init>(r1)
            r5 = 1096810496(0x41600000, float:14.0)
            r7 = 1
            r2.setTextSize(r7, r5)
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r2.setTextColor(r5)
            r5 = 17
            r2.setGravity(r5)
            r2.setSingleLine(r7)
            android.text.TextUtils$TruncateAt r5 = android.text.TextUtils.TruncateAt.END
            r2.setEllipsize(r5)
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            r7 = 0
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r5, r7)
            r2.setBackgroundDrawable(r5)
            r5 = 1099956224(0x41900000, float:18.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r2.setPadding(r8, r7, r5, r7)
            r5 = 2131626942(0x7f0e0bbe, float:1.8881134E38)
            java.lang.String r7 = "OpenInBrowser"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r7, r5)
            java.lang.String r5 = r5.toUpperCase()
            r2.setText(r5)
            android.graphics.Typeface r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r13)
            r2.setTypeface(r5)
            r5 = 51
            r7 = -2
            r8 = -1
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r7, (int) r8, (int) r5)
            r6.addView(r2, r5)
            org.telegram.ui.Components.EmbedBottomSheet$$ExternalSyntheticLambda3 r5 = new org.telegram.ui.Components.EmbedBottomSheet$$ExternalSyntheticLambda3
            r5.<init>(r0)
            r2.setOnClickListener(r5)
            org.telegram.ui.Components.WebPlayerView r5 = r0.videoView
            java.lang.String r7 = r0.embedUrl
            boolean r5 = r5.canHandleUrl(r7)
            if (r5 != 0) goto L_0x04d0
            org.telegram.ui.Components.WebPlayerView r5 = r0.videoView
            boolean r5 = r5.canHandleUrl(r3)
            if (r5 == 0) goto L_0x04ce
            goto L_0x04d0
        L_0x04ce:
            r9 = 0
            goto L_0x04d1
        L_0x04d0:
            r9 = 1
        L_0x04d1:
            r5 = r9
            org.telegram.ui.Components.WebPlayerView r7 = r0.videoView
            if (r5 == 0) goto L_0x04d8
            r9 = 0
            goto L_0x04d9
        L_0x04d8:
            r9 = 4
        L_0x04d9:
            r7.setVisibility(r9)
            if (r5 == 0) goto L_0x04e3
            org.telegram.ui.Components.WebPlayerView r7 = r0.videoView
            r7.willHandle()
        L_0x04e3:
            org.telegram.ui.Components.EmbedBottomSheet$8 r7 = new org.telegram.ui.Components.EmbedBottomSheet$8
            r7.<init>(r5)
            r0.setDelegate(r7)
            org.telegram.ui.Components.EmbedBottomSheet$9 r7 = new org.telegram.ui.Components.EmbedBottomSheet$9
            android.content.Context r8 = org.telegram.messenger.ApplicationLoader.applicationContext
            r7.<init>(r8)
            r0.orientationEventListener = r7
            java.lang.String r7 = r0.embedUrl
            java.lang.String r7 = org.telegram.ui.Components.WebPlayerView.getYouTubeVideoId(r7)
            if (r7 != 0) goto L_0x04fe
            if (r5 != 0) goto L_0x0562
        L_0x04fe:
            org.telegram.ui.Components.RadialProgressView r8 = r0.progressBar
            r9 = 0
            r8.setVisibility(r9)
            android.webkit.WebView r8 = r0.webView
            r8.setVisibility(r9)
            android.widget.LinearLayout r8 = r0.imageButtonsContainer
            r8.setVisibility(r9)
            if (r7 == 0) goto L_0x0515
            android.view.View r8 = r0.progressBarBlackBackground
            r8.setVisibility(r9)
        L_0x0515:
            android.widget.TextView r8 = r0.copyTextButton
            r9 = 4
            r8.setVisibility(r9)
            android.webkit.WebView r8 = r0.webView
            r10 = 1
            r8.setKeepScreenOn(r10)
            org.telegram.ui.Components.WebPlayerView r8 = r0.videoView
            r8.setVisibility(r9)
            org.telegram.ui.Components.WebPlayerView r8 = r0.videoView
            android.view.View r8 = r8.getControlsView()
            r8.setVisibility(r9)
            org.telegram.ui.Components.WebPlayerView r8 = r0.videoView
            android.view.TextureView r8 = r8.getTextureView()
            r8.setVisibility(r9)
            org.telegram.ui.Components.WebPlayerView r8 = r0.videoView
            android.widget.ImageView r8 = r8.getTextureImageView()
            if (r8 == 0) goto L_0x0549
            org.telegram.ui.Components.WebPlayerView r8 = r0.videoView
            android.widget.ImageView r8 = r8.getTextureImageView()
            r8.setVisibility(r9)
        L_0x0549:
            if (r7 == 0) goto L_0x0562
            int r8 = r0.currentAccount
            org.telegram.messenger.MessagesController r8 = org.telegram.messenger.MessagesController.getInstance(r8)
            java.lang.String r8 = r8.youtubePipType
            java.lang.String r9 = "disabled"
            boolean r8 = r9.equals(r8)
            if (r8 == 0) goto L_0x0562
            android.widget.ImageView r8 = r0.pipButton
            r9 = 8
            r8.setVisibility(r9)
        L_0x0562:
            android.view.OrientationEventListener r8 = r0.orientationEventListener
            boolean r8 = r8.canDetectOrientation()
            if (r8 == 0) goto L_0x0570
            android.view.OrientationEventListener r8 = r0.orientationEventListener
            r8.enable()
            goto L_0x0578
        L_0x0570:
            android.view.OrientationEventListener r8 = r0.orientationEventListener
            r8.disable()
            r8 = 0
            r0.orientationEventListener = r8
        L_0x0578:
            instance = r0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmbedBottomSheet.<init>(android.content.Context, java.lang.String, java.lang.String, java.lang.String, java.lang.String, int, int, int):void");
    }

    static /* synthetic */ boolean lambda$new$0(View v, MotionEvent event) {
        return true;
    }

    static /* synthetic */ boolean lambda$new$1(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-EmbedBottomSheet  reason: not valid java name */
    public /* synthetic */ void m3956lambda$new$2$orgtelegramuiComponentsEmbedBottomSheet(View v) {
        dismiss();
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-EmbedBottomSheet  reason: not valid java name */
    public /* synthetic */ void m3957lambda$new$3$orgtelegramuiComponentsEmbedBottomSheet(View v) {
        if (PipVideoOverlay.isVisible()) {
            PipVideoOverlay.dismiss();
            v.getClass();
            AndroidUtilities.runOnUIThread(new ChatActivityEnterView$$ExternalSyntheticLambda26(v), 300);
            return;
        }
        View view = v;
        boolean inAppOnly = this.isYouTube && "inapp".equals(MessagesController.getInstance(this.currentAccount).youtubePipType);
        if ((inAppOnly || checkInlinePermissions()) && this.progressBar.getVisibility() != 0) {
            if (PipVideoOverlay.show(inAppOnly, this.parentActivity, this.webView, this.width, this.height)) {
                PipVideoOverlay.setParentSheet(this);
            }
            if (this.isYouTube) {
                runJsCode("hideControls();");
            }
            if (0 != 0) {
                this.animationInProgress = true;
                this.videoView.getAspectRatioView().getLocationInWindow(this.position);
                int[] iArr = this.position;
                iArr[0] = iArr[0] - getLeftInset();
                int[] iArr2 = this.position;
                iArr2[1] = (int) (((float) iArr2[1]) - this.containerView.getTranslationY());
                TextureView textureView = this.videoView.getTextureView();
                ImageView textureImageView = this.videoView.getTextureImageView();
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(textureImageView, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(textureImageView, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(textureImageView, View.TRANSLATION_X, new float[]{(float) this.position[0]}), ObjectAnimator.ofFloat(textureImageView, View.TRANSLATION_Y, new float[]{(float) this.position[1]}), ObjectAnimator.ofFloat(textureView, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(textureView, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(textureView, View.TRANSLATION_X, new float[]{(float) this.position[0]}), ObjectAnimator.ofFloat(textureView, View.TRANSLATION_Y, new float[]{(float) this.position[1]}), ObjectAnimator.ofFloat(this.containerView, View.TRANSLATION_Y, new float[]{0.0f}), ObjectAnimator.ofInt(this.backDrawable, AnimationProperties.COLOR_DRAWABLE_ALPHA, new int[]{51})});
                animatorSet.setInterpolator(new DecelerateInterpolator());
                animatorSet.setDuration(250);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        boolean unused = EmbedBottomSheet.this.animationInProgress = false;
                    }
                });
                animatorSet.start();
            } else {
                this.containerView.setTranslationY(0.0f);
            }
            dismissInternal();
        }
    }

    /* renamed from: lambda$new$4$org-telegram-ui-Components-EmbedBottomSheet  reason: not valid java name */
    public /* synthetic */ void m3958lambda$new$4$orgtelegramuiComponentsEmbedBottomSheet(View v) {
        try {
            ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", this.openUrl));
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        Activity activity = this.parentActivity;
        if (activity instanceof LaunchActivity) {
            ((LaunchActivity) activity).showBulletin(EmbedBottomSheet$$ExternalSyntheticLambda6.INSTANCE);
        }
        dismiss();
    }

    /* renamed from: lambda$new$5$org-telegram-ui-Components-EmbedBottomSheet  reason: not valid java name */
    public /* synthetic */ void m3959lambda$new$5$orgtelegramuiComponentsEmbedBottomSheet(View v) {
        Browser.openUrl((Context) this.parentActivity, this.openUrl);
        dismiss();
    }

    private void runJsCode(String code) {
        if (Build.VERSION.SDK_INT >= 21) {
            this.webView.evaluateJavascript(code, (ValueCallback) null);
            return;
        }
        try {
            WebView webView2 = this.webView;
            webView2.loadUrl("javascript:" + code);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public boolean checkInlinePermissions() {
        if (this.parentActivity == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT < 23 || Settings.canDrawOverlays(this.parentActivity)) {
            return true;
        }
        AlertsCreator.createDrawOverlayPermissionDialog(this.parentActivity, (DialogInterface.OnClickListener) null);
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return this.videoView.getVisibility() != 0 || !this.videoView.isInFullscreen();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        if (this.videoView.getVisibility() == 0 && this.videoView.isInitied() && !this.videoView.isInline()) {
            if (newConfig.orientation == 2) {
                if (!this.videoView.isInFullscreen()) {
                    this.videoView.enterFullscreen();
                }
            } else if (this.videoView.isInFullscreen()) {
                this.videoView.exitFullscreen();
            }
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
        PipVideoOverlay.dismiss();
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
        if (this.webView != null && PipVideoOverlay.isVisible()) {
            if (ApplicationLoader.mainInterfacePaused) {
                try {
                    this.parentActivity.startService(new Intent(ApplicationLoader.applicationContext, BringAppForegroundService.class));
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            }
            if (this.isYouTube) {
                runJsCode("showControls();");
            }
            ViewGroup parent = (ViewGroup) this.webView.getParent();
            if (parent != null) {
                parent.removeView(this.webView);
            }
            this.containerLayout.addView(this.webView, 0, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, (float) ((this.hasDescription ? 22 : 0) + 84)));
            setShowWithoutAnimation(true);
            show();
            PipVideoOverlay.dismiss(true);
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
            View textureImageView = this.videoView.getTextureImageView();
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
    public void onContainerTranslationYChanged(float translationY) {
        updateTextureViewPosition();
    }

    /* access modifiers changed from: protected */
    public boolean onCustomMeasure(View view, int width2, int height2) {
        if (view == this.videoView.getControlsView()) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.width = this.videoView.getMeasuredWidth();
            layoutParams.height = this.videoView.getAspectRatioView().getMeasuredHeight() + (this.videoView.isInFullscreen() ? 0 : AndroidUtilities.dp(10.0f));
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean onCustomLayout(View view, int left, int top, int right, int bottom) {
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
                PipVideoOverlay.dismiss();
                return;
            }
            this.container.invalidate();
        }
    }
}
