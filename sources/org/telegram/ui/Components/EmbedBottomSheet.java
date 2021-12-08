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
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.browser.Browser;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PhotoViewer;

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

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$new$0(View view, MotionEvent motionEvent) {
        return true;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$new$1(View view, MotionEvent motionEvent) {
        return true;
    }

    private class YoutubeProxy {
        private YoutubeProxy() {
        }

        @JavascriptInterface
        public void postEvent(String str, String str2) {
            if ("loaded".equals(str)) {
                AndroidUtilities.runOnUIThread(new EmbedBottomSheet$YoutubeProxy$$ExternalSyntheticLambda0(this));
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$postEvent$0() {
            EmbedBottomSheet.this.progressBar.setVisibility(4);
            EmbedBottomSheet.this.progressBarBlackBackground.setVisibility(4);
            EmbedBottomSheet.this.pipButton.setEnabled(true);
            EmbedBottomSheet.this.pipButton.setAlpha(1.0f);
        }
    }

    public static void show(Activity activity, MessageObject messageObject, PhotoViewer.PhotoViewerProvider photoViewerProvider, String str, String str2, String str3, String str4, int i, int i2, boolean z) {
        show(activity, messageObject, photoViewerProvider, str, str2, str3, str4, i, i2, -1, z);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:4:0x000a, code lost:
        r0 = r1.messageOwner.media;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void show(android.app.Activity r10, org.telegram.messenger.MessageObject r11, org.telegram.ui.PhotoViewer.PhotoViewerProvider r12, java.lang.String r13, java.lang.String r14, java.lang.String r15, java.lang.String r16, int r17, int r18, int r19, boolean r20) {
        /*
            r1 = r11
            org.telegram.ui.Components.EmbedBottomSheet r0 = instance
            if (r0 == 0) goto L_0x0008
            r0.destroy()
        L_0x0008:
            if (r1 == 0) goto L_0x0019
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            if (r0 == 0) goto L_0x0019
            org.telegram.tgnet.TLRPC$WebPage r0 = r0.webpage
            if (r0 == 0) goto L_0x0019
            java.lang.String r0 = org.telegram.ui.Components.WebPlayerView.getYouTubeVideoId(r16)
            goto L_0x001a
        L_0x0019:
            r0 = 0
        L_0x001a:
            if (r0 == 0) goto L_0x0035
            org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.getInstance()
            r2 = r10
            r0.setParentActivity(r10)
            org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.getInstance()
            r3 = 0
            r4 = 0
            r6 = 0
            r1 = r11
            r2 = r19
            r8 = r12
            r0.openPhoto(r1, r2, r3, r4, r6, r8)
            goto L_0x004f
        L_0x0035:
            r2 = r10
            org.telegram.ui.Components.EmbedBottomSheet r0 = new org.telegram.ui.Components.EmbedBottomSheet
            r1 = r0
            r3 = r13
            r4 = r14
            r5 = r15
            r6 = r16
            r7 = r17
            r8 = r18
            r9 = r19
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9)
            r1 = r20
            r0.setCalcMandatoryInsets(r1)
            r0.show()
        L_0x004f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmbedBottomSheet.show(android.app.Activity, org.telegram.messenger.MessageObject, org.telegram.ui.PhotoViewer$PhotoViewerProvider, java.lang.String, java.lang.String, java.lang.String, java.lang.String, int, int, int, boolean):void");
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    @android.annotation.SuppressLint({"SetJavaScriptEnabled"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private EmbedBottomSheet(android.content.Context r26, java.lang.String r27, java.lang.String r28, java.lang.String r29, java.lang.String r30, int r31, int r32, int r33) {
        /*
            r25 = this;
            r0 = r25
            r1 = r26
            r2 = r28
            r3 = r29
            r4 = r31
            r5 = r32
            r6 = 0
            r0.<init>(r1, r6)
            r7 = 2
            int[] r8 = new int[r7]
            r0.position = r8
            r8 = -2
            r0.prevOrientation = r8
            org.telegram.ui.Components.EmbedBottomSheet$1 r9 = new org.telegram.ui.Components.EmbedBottomSheet$1
            r9.<init>()
            r0.onShowListener = r9
            r9 = 1
            r0.fullWidth = r9
            r0.setApplyTopPadding(r6)
            r0.setApplyBottomPadding(r6)
            r10 = r33
            r0.seekTimeOverride = r10
            boolean r10 = r1 instanceof android.app.Activity
            if (r10 == 0) goto L_0x0035
            r10 = r1
            android.app.Activity r10 = (android.app.Activity) r10
            r0.parentActivity = r10
        L_0x0035:
            r10 = r30
            r0.embedUrl = r10
            if (r2 == 0) goto L_0x0043
            int r10 = r28.length()
            if (r10 <= 0) goto L_0x0043
            r10 = 1
            goto L_0x0044
        L_0x0043:
            r10 = 0
        L_0x0044:
            r0.hasDescription = r10
            r0.openUrl = r3
            r0.width = r4
            r0.height = r5
            if (r4 == 0) goto L_0x0050
            if (r5 != 0) goto L_0x005b
        L_0x0050:
            android.graphics.Point r4 = org.telegram.messenger.AndroidUtilities.displaySize
            int r5 = r4.x
            r0.width = r5
            int r4 = r4.y
            int r4 = r4 / r7
            r0.height = r4
        L_0x005b:
            android.widget.FrameLayout r4 = new android.widget.FrameLayout
            r4.<init>(r1)
            r0.fullscreenVideoContainer = r4
            r4.setKeepScreenOn(r9)
            android.widget.FrameLayout r4 = r0.fullscreenVideoContainer
            r5 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r4.setBackgroundColor(r5)
            int r4 = android.os.Build.VERSION.SDK_INT
            r10 = 21
            if (r4 < r10) goto L_0x0077
            android.widget.FrameLayout r11 = r0.fullscreenVideoContainer
            r11.setFitsSystemWindows(r9)
        L_0x0077:
            android.widget.FrameLayout r11 = r0.fullscreenVideoContainer
            org.telegram.ui.Components.EmbedBottomSheet$$ExternalSyntheticLambda4 r12 = org.telegram.ui.Components.EmbedBottomSheet$$ExternalSyntheticLambda4.INSTANCE
            r11.setOnTouchListener(r12)
            org.telegram.ui.ActionBar.BottomSheet$ContainerView r11 = r0.container
            android.widget.FrameLayout r12 = r0.fullscreenVideoContainer
            r13 = -1082130432(0xffffffffbvar_, float:-1.0)
            r14 = -1
            android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame(r14, r13)
            r11.addView(r12, r13)
            android.widget.FrameLayout r11 = r0.fullscreenVideoContainer
            r12 = 4
            r11.setVisibility(r12)
            org.telegram.ui.Components.EmbedBottomSheet$2 r11 = new org.telegram.ui.Components.EmbedBottomSheet$2
            r11.<init>(r1)
            r0.containerLayout = r11
            org.telegram.ui.Components.EmbedBottomSheet$$ExternalSyntheticLambda5 r13 = org.telegram.ui.Components.EmbedBottomSheet$$ExternalSyntheticLambda5.INSTANCE
            r11.setOnTouchListener(r13)
            android.widget.FrameLayout r11 = r0.containerLayout
            r0.setCustomView(r11)
            org.telegram.ui.Components.EmbedBottomSheet$3 r11 = new org.telegram.ui.Components.EmbedBottomSheet$3
            r11.<init>(r1)
            r0.webView = r11
            android.webkit.WebSettings r11 = r11.getSettings()
            r11.setJavaScriptEnabled(r9)
            android.webkit.WebView r11 = r0.webView
            android.webkit.WebSettings r11 = r11.getSettings()
            r11.setDomStorageEnabled(r9)
            r11 = 17
            if (r4 < r11) goto L_0x00c7
            android.webkit.WebView r13 = r0.webView
            android.webkit.WebSettings r13 = r13.getSettings()
            r13.setMediaPlaybackRequiresUserGesture(r6)
        L_0x00c7:
            if (r4 < r10) goto L_0x00db
            android.webkit.WebView r4 = r0.webView
            android.webkit.WebSettings r4 = r4.getSettings()
            r4.setMixedContentMode(r6)
            android.webkit.CookieManager r4 = android.webkit.CookieManager.getInstance()
            android.webkit.WebView r10 = r0.webView
            r4.setAcceptThirdPartyCookies(r10, r9)
        L_0x00db:
            android.webkit.WebView r4 = r0.webView
            org.telegram.ui.Components.EmbedBottomSheet$4 r10 = new org.telegram.ui.Components.EmbedBottomSheet$4
            r10.<init>()
            r4.setWebChromeClient(r10)
            android.webkit.WebView r4 = r0.webView
            org.telegram.ui.Components.EmbedBottomSheet$5 r10 = new org.telegram.ui.Components.EmbedBottomSheet$5
            r10.<init>()
            r4.setWebViewClient(r10)
            android.widget.FrameLayout r4 = r0.containerLayout
            android.webkit.WebView r10 = r0.webView
            r15 = -1
            r16 = -1082130432(0xffffffffbvar_, float:-1.0)
            r17 = 51
            r18 = 0
            r19 = 0
            r20 = 0
            boolean r13 = r0.hasDescription
            r22 = 22
            if (r13 == 0) goto L_0x0107
            r13 = 22
            goto L_0x0108
        L_0x0107:
            r13 = 0
        L_0x0108:
            int r13 = r13 + 84
            float r13 = (float) r13
            r21 = r13
            android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame(r15, r16, r17, r18, r19, r20, r21)
            r4.addView(r10, r13)
            org.telegram.ui.Components.WebPlayerView r4 = new org.telegram.ui.Components.WebPlayerView
            org.telegram.ui.Components.EmbedBottomSheet$6 r10 = new org.telegram.ui.Components.EmbedBottomSheet$6
            r10.<init>()
            r4.<init>(r1, r9, r6, r10)
            r0.videoView = r4
            r4.setVisibility(r12)
            android.widget.FrameLayout r4 = r0.containerLayout
            org.telegram.ui.Components.WebPlayerView r10 = r0.videoView
            r15 = -1
            r16 = -1082130432(0xffffffffbvar_, float:-1.0)
            r17 = 51
            r18 = 0
            r19 = 0
            r20 = 0
            boolean r13 = r0.hasDescription
            if (r13 == 0) goto L_0x0139
            r13 = 22
            goto L_0x013a
        L_0x0139:
            r13 = 0
        L_0x013a:
            int r13 = r13 + 84
            int r13 = r13 + -10
            float r13 = (float) r13
            r21 = r13
            android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame(r15, r16, r17, r18, r19, r20, r21)
            r4.addView(r10, r13)
            android.view.View r4 = new android.view.View
            r4.<init>(r1)
            r0.progressBarBlackBackground = r4
            r4.setBackgroundColor(r5)
            android.view.View r4 = r0.progressBarBlackBackground
            r4.setVisibility(r12)
            android.widget.FrameLayout r4 = r0.containerLayout
            android.view.View r5 = r0.progressBarBlackBackground
            r15 = -1
            r16 = -1082130432(0xffffffffbvar_, float:-1.0)
            r17 = 51
            r18 = 0
            r19 = 0
            r20 = 0
            boolean r10 = r0.hasDescription
            if (r10 == 0) goto L_0x016d
            r10 = 22
            goto L_0x016e
        L_0x016d:
            r10 = 0
        L_0x016e:
            int r10 = r10 + 84
            float r10 = (float) r10
            r21 = r10
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r15, r16, r17, r18, r19, r20, r21)
            r4.addView(r5, r10)
            org.telegram.ui.Components.RadialProgressView r4 = new org.telegram.ui.Components.RadialProgressView
            r4.<init>(r1)
            r0.progressBar = r4
            r4.setVisibility(r12)
            android.widget.FrameLayout r4 = r0.containerLayout
            org.telegram.ui.Components.RadialProgressView r5 = r0.progressBar
            r15 = -2
            r16 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r17 = 17
            r18 = 0
            r19 = 0
            r20 = 0
            boolean r10 = r0.hasDescription
            if (r10 == 0) goto L_0x0198
            goto L_0x019a
        L_0x0198:
            r22 = 0
        L_0x019a:
            int r22 = r22 + 84
            int r7 = r22 / 2
            float r7 = (float) r7
            r21 = r7
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r15, r16, r17, r18, r19, r20, r21)
            r4.addView(r5, r7)
            boolean r4 = r0.hasDescription
            java.lang.String r5 = "fonts/rmedium.ttf"
            r7 = 1099956224(0x41900000, float:18.0)
            if (r4 == 0) goto L_0x01f6
            android.widget.TextView r4 = new android.widget.TextView
            r4.<init>(r1)
            r10 = 1098907648(0x41800000, float:16.0)
            r4.setTextSize(r9, r10)
            java.lang.String r10 = "dialogTextBlack"
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            r4.setTextColor(r10)
            r4.setText(r2)
            r4.setSingleLine(r9)
            android.graphics.Typeface r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r5)
            r4.setTypeface(r2)
            android.text.TextUtils$TruncateAt r2 = android.text.TextUtils.TruncateAt.END
            r4.setEllipsize(r2)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r4.setPadding(r2, r6, r10, r6)
            android.widget.FrameLayout r2 = r0.containerLayout
            r15 = -1
            r16 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r17 = 83
            r18 = 0
            r19 = 0
            r20 = 0
            r21 = 1117388800(0x429a0000, float:77.0)
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r15, r16, r17, r18, r19, r20, r21)
            r2.addView(r4, r10)
        L_0x01f6:
            android.widget.TextView r2 = new android.widget.TextView
            r2.<init>(r1)
            r4 = 1096810496(0x41600000, float:14.0)
            r2.setTextSize(r9, r4)
            java.lang.String r10 = "dialogTextGray"
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            r2.setTextColor(r10)
            r10 = r27
            r2.setText(r10)
            r2.setSingleLine(r9)
            android.text.TextUtils$TruncateAt r10 = android.text.TextUtils.TruncateAt.END
            r2.setEllipsize(r10)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r2.setPadding(r10, r6, r13, r6)
            android.widget.FrameLayout r10 = r0.containerLayout
            r15 = -1
            r16 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r17 = 83
            r18 = 0
            r19 = 0
            r20 = 0
            r21 = 1113849856(0x42640000, float:57.0)
            android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame(r15, r16, r17, r18, r19, r20, r21)
            r10.addView(r2, r13)
            android.view.View r2 = new android.view.View
            r2.<init>(r1)
            java.lang.String r10 = "dialogGrayLine"
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            r2.setBackgroundColor(r10)
            android.widget.FrameLayout r10 = r0.containerLayout
            android.widget.FrameLayout$LayoutParams r13 = new android.widget.FrameLayout$LayoutParams
            r15 = 83
            r13.<init>(r14, r9, r15)
            r10.addView(r2, r13)
            android.view.ViewGroup$LayoutParams r2 = r2.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r2 = (android.widget.FrameLayout.LayoutParams) r2
            r10 = 1111490560(0x42400000, float:48.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            r2.bottomMargin = r10
            android.widget.FrameLayout r2 = new android.widget.FrameLayout
            r2.<init>(r1)
            java.lang.String r10 = "dialogBackground"
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            r2.setBackgroundColor(r10)
            android.widget.FrameLayout r10 = r0.containerLayout
            r13 = 48
            android.widget.FrameLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createFrame(r14, r13, r15)
            r10.addView(r2, r15)
            android.widget.LinearLayout r10 = new android.widget.LinearLayout
            r10.<init>(r1)
            r10.setOrientation(r6)
            r15 = 1065353216(0x3var_, float:1.0)
            r10.setWeightSum(r15)
            r15 = 53
            android.widget.FrameLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r14, r15)
            r2.addView(r10, r15)
            android.widget.TextView r15 = new android.widget.TextView
            r15.<init>(r1)
            r15.setTextSize(r9, r4)
            java.lang.String r16 = "dialogTextBlue4"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r16)
            r15.setTextColor(r4)
            r15.setGravity(r11)
            r15.setSingleLine(r9)
            android.text.TextUtils$TruncateAt r4 = android.text.TextUtils.TruncateAt.END
            r15.setEllipsize(r4)
            java.lang.String r4 = "dialogButtonSelector"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r9, r6)
            r15.setBackgroundDrawable(r9)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r15.setPadding(r9, r6, r13, r6)
            r9 = 2131625006(0x7f0e042e, float:1.8877208E38)
            java.lang.String r13 = "Close"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r13, r9)
            java.lang.String r9 = r9.toUpperCase()
            r15.setText(r9)
            android.graphics.Typeface r9 = org.telegram.messenger.AndroidUtilities.getTypeface(r5)
            r15.setTypeface(r9)
            r9 = 51
            android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r8, (int) r14, (int) r9)
            r2.addView(r15, r13)
            org.telegram.ui.Components.EmbedBottomSheet$$ExternalSyntheticLambda1 r13 = new org.telegram.ui.Components.EmbedBottomSheet$$ExternalSyntheticLambda1
            r13.<init>(r0)
            r15.setOnClickListener(r13)
            android.widget.LinearLayout r13 = new android.widget.LinearLayout
            r13.<init>(r1)
            r0.imageButtonsContainer = r13
            r13.setVisibility(r12)
            android.widget.LinearLayout r13 = r0.imageButtonsContainer
            android.widget.FrameLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r14, r11)
            r2.addView(r13, r15)
            android.widget.ImageView r2 = new android.widget.ImageView
            r2.<init>(r1)
            r0.pipButton = r2
            android.widget.ImageView$ScaleType r13 = android.widget.ImageView.ScaleType.CENTER
            r2.setScaleType(r13)
            android.widget.ImageView r2 = r0.pipButton
            r13 = 2131166165(0x7var_d5, float:1.7946568E38)
            r2.setImageResource(r13)
            android.widget.ImageView r2 = r0.pipButton
            r13 = 2131624011(0x7f0e004b, float:1.887519E38)
            java.lang.String r15 = "AccDescrPipMode"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r15, r13)
            r2.setContentDescription(r13)
            android.widget.ImageView r2 = r0.pipButton
            r2.setEnabled(r6)
            android.widget.ImageView r2 = r0.pipButton
            r13 = 1056964608(0x3var_, float:0.5)
            r2.setAlpha(r13)
            android.widget.ImageView r2 = r0.pipButton
            android.graphics.PorterDuffColorFilter r13 = new android.graphics.PorterDuffColorFilter
            int r15 = org.telegram.ui.ActionBar.Theme.getColor(r16)
            android.graphics.PorterDuff$Mode r12 = android.graphics.PorterDuff.Mode.MULTIPLY
            r13.<init>(r15, r12)
            r2.setColorFilter(r13)
            android.widget.ImageView r2 = r0.pipButton
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r12, r6)
            r2.setBackgroundDrawable(r12)
            android.widget.LinearLayout r2 = r0.imageButtonsContainer
            android.widget.ImageView r12 = r0.pipButton
            r18 = 48
            r19 = 1111490560(0x42400000, float:48.0)
            r20 = 51
            r21 = 0
            r22 = 0
            r23 = 1082130432(0x40800000, float:4.0)
            r24 = 0
            android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame(r18, r19, r20, r21, r22, r23, r24)
            r2.addView(r12, r13)
            android.widget.ImageView r2 = r0.pipButton
            org.telegram.ui.Components.EmbedBottomSheet$$ExternalSyntheticLambda0 r12 = new org.telegram.ui.Components.EmbedBottomSheet$$ExternalSyntheticLambda0
            r12.<init>(r0)
            r2.setOnClickListener(r12)
            org.telegram.ui.Components.EmbedBottomSheet$$ExternalSyntheticLambda3 r2 = new org.telegram.ui.Components.EmbedBottomSheet$$ExternalSyntheticLambda3
            r2.<init>(r0)
            android.widget.ImageView r12 = new android.widget.ImageView
            r12.<init>(r1)
            android.widget.ImageView$ScaleType r13 = android.widget.ImageView.ScaleType.CENTER
            r12.setScaleType(r13)
            r13 = 2131166157(0x7var_cd, float:1.7946551E38)
            r12.setImageResource(r13)
            r13 = 2131625091(0x7f0e0483, float:1.887738E38)
            java.lang.String r15 = "CopyLink"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r15, r13)
            r12.setContentDescription(r13)
            android.graphics.PorterDuffColorFilter r13 = new android.graphics.PorterDuffColorFilter
            int r15 = org.telegram.ui.ActionBar.Theme.getColor(r16)
            android.graphics.PorterDuff$Mode r8 = android.graphics.PorterDuff.Mode.MULTIPLY
            r13.<init>(r15, r8)
            r12.setColorFilter(r13)
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            android.graphics.drawable.Drawable r8 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r8, r6)
            r12.setBackgroundDrawable(r8)
            android.widget.LinearLayout r8 = r0.imageButtonsContainer
            r13 = 48
            android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame(r13, r13, r9)
            r8.addView(r12, r13)
            r12.setOnClickListener(r2)
            android.widget.TextView r8 = new android.widget.TextView
            r8.<init>(r1)
            r0.copyTextButton = r8
            r12 = 1096810496(0x41600000, float:14.0)
            r13 = 1
            r8.setTextSize(r13, r12)
            android.widget.TextView r8 = r0.copyTextButton
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r16)
            r8.setTextColor(r12)
            android.widget.TextView r8 = r0.copyTextButton
            r8.setGravity(r11)
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
            r12 = 2131625089(0x7f0e0481, float:1.8877376E38)
            java.lang.String r13 = "Copy"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
            java.lang.String r12 = r12.toUpperCase()
            r8.setText(r12)
            android.widget.TextView r8 = r0.copyTextButton
            android.graphics.Typeface r12 = org.telegram.messenger.AndroidUtilities.getTypeface(r5)
            r8.setTypeface(r12)
            android.widget.TextView r8 = r0.copyTextButton
            r12 = -2
            android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r14, r9)
            r10.addView(r8, r13)
            android.widget.TextView r8 = r0.copyTextButton
            r8.setOnClickListener(r2)
            android.widget.TextView r2 = new android.widget.TextView
            r2.<init>(r1)
            r1 = 1096810496(0x41600000, float:14.0)
            r8 = 1
            r2.setTextSize(r8, r1)
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r16)
            r2.setTextColor(r1)
            r2.setGravity(r11)
            r2.setSingleLine(r8)
            android.text.TextUtils$TruncateAt r1 = android.text.TextUtils.TruncateAt.END
            r2.setEllipsize(r1)
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r1, r6)
            r2.setBackgroundDrawable(r1)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r2.setPadding(r1, r6, r4, r6)
            r1 = 2131626770(0x7f0e0b12, float:1.8880786E38)
            java.lang.String r4 = "OpenInBrowser"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r4, r1)
            java.lang.String r1 = r1.toUpperCase()
            r2.setText(r1)
            android.graphics.Typeface r1 = org.telegram.messenger.AndroidUtilities.getTypeface(r5)
            r2.setTypeface(r1)
            r1 = -2
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r1, r14, r9)
            r10.addView(r2, r1)
            org.telegram.ui.Components.EmbedBottomSheet$$ExternalSyntheticLambda2 r1 = new org.telegram.ui.Components.EmbedBottomSheet$$ExternalSyntheticLambda2
            r1.<init>(r0)
            r2.setOnClickListener(r1)
            org.telegram.ui.Components.WebPlayerView r1 = r0.videoView
            java.lang.String r2 = r0.embedUrl
            boolean r1 = r1.canHandleUrl(r2)
            if (r1 != 0) goto L_0x048d
            org.telegram.ui.Components.WebPlayerView r1 = r0.videoView
            boolean r1 = r1.canHandleUrl(r3)
            if (r1 == 0) goto L_0x048b
            goto L_0x048d
        L_0x048b:
            r13 = 0
            goto L_0x048e
        L_0x048d:
            r13 = 1
        L_0x048e:
            org.telegram.ui.Components.WebPlayerView r1 = r0.videoView
            if (r13 == 0) goto L_0x0494
            r2 = 0
            goto L_0x0495
        L_0x0494:
            r2 = 4
        L_0x0495:
            r1.setVisibility(r2)
            if (r13 == 0) goto L_0x049f
            org.telegram.ui.Components.WebPlayerView r1 = r0.videoView
            r1.willHandle()
        L_0x049f:
            org.telegram.ui.Components.EmbedBottomSheet$8 r1 = new org.telegram.ui.Components.EmbedBottomSheet$8
            r1.<init>(r13)
            r0.setDelegate(r1)
            org.telegram.ui.Components.EmbedBottomSheet$9 r1 = new org.telegram.ui.Components.EmbedBottomSheet$9
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext
            r1.<init>(r2)
            r0.orientationEventListener = r1
            java.lang.String r1 = r0.embedUrl
            java.lang.String r1 = org.telegram.ui.Components.WebPlayerView.getYouTubeVideoId(r1)
            if (r1 != 0) goto L_0x04ba
            if (r13 != 0) goto L_0x051d
        L_0x04ba:
            org.telegram.ui.Components.RadialProgressView r2 = r0.progressBar
            r2.setVisibility(r6)
            android.webkit.WebView r2 = r0.webView
            r2.setVisibility(r6)
            android.widget.LinearLayout r2 = r0.imageButtonsContainer
            r2.setVisibility(r6)
            if (r1 == 0) goto L_0x04d0
            android.view.View r2 = r0.progressBarBlackBackground
            r2.setVisibility(r6)
        L_0x04d0:
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
            if (r2 == 0) goto L_0x0504
            org.telegram.ui.Components.WebPlayerView r2 = r0.videoView
            android.widget.ImageView r2 = r2.getTextureImageView()
            r2.setVisibility(r3)
        L_0x0504:
            if (r1 == 0) goto L_0x051d
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            java.lang.String r1 = r1.youtubePipType
            java.lang.String r2 = "disabled"
            boolean r1 = r2.equals(r1)
            if (r1 == 0) goto L_0x051d
            android.widget.ImageView r1 = r0.pipButton
            r2 = 8
            r1.setVisibility(r2)
        L_0x051d:
            android.view.OrientationEventListener r1 = r0.orientationEventListener
            boolean r1 = r1.canDetectOrientation()
            if (r1 == 0) goto L_0x052b
            android.view.OrientationEventListener r1 = r0.orientationEventListener
            r1.enable()
            goto L_0x0533
        L_0x052b:
            android.view.OrientationEventListener r1 = r0.orientationEventListener
            r1.disable()
            r1 = 0
            r0.orientationEventListener = r1
        L_0x0533:
            instance = r0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmbedBottomSheet.<init>(android.content.Context, java.lang.String, java.lang.String, java.lang.String, java.lang.String, int, int, int):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2(View view) {
        dismiss();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$3(View view) {
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
    public /* synthetic */ void lambda$new$4(View view) {
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

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$5(View view) {
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
