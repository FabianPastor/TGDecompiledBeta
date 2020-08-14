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
            r3 = r30
            r4 = r31
            r5 = 0
            r0.<init>(r1, r5)
            r6 = 2
            int[] r7 = new int[r6]
            r0.position = r7
            r7 = -2
            r0.prevOrientation = r7
            org.telegram.ui.Components.EmbedBottomSheet$1 r8 = new org.telegram.ui.Components.EmbedBottomSheet$1
            r8.<init>()
            r0.onShowListener = r8
            r8 = 1
            r0.fullWidth = r8
            r0.setApplyTopPadding(r5)
            r0.setApplyBottomPadding(r5)
            r9 = r32
            r0.seekTimeOverride = r9
            boolean r9 = r1 instanceof android.app.Activity
            if (r9 == 0) goto L_0x0033
            r9 = r1
            android.app.Activity r9 = (android.app.Activity) r9
            r0.parentActivity = r9
        L_0x0033:
            r9 = r29
            r0.embedUrl = r9
            if (r2 == 0) goto L_0x0041
            int r9 = r27.length()
            if (r9 <= 0) goto L_0x0041
            r9 = 1
            goto L_0x0042
        L_0x0041:
            r9 = 0
        L_0x0042:
            r0.hasDescription = r9
            r9 = r28
            r0.openUrl = r9
            r0.width = r3
            r0.height = r4
            if (r3 == 0) goto L_0x0050
            if (r4 != 0) goto L_0x005b
        L_0x0050:
            android.graphics.Point r3 = org.telegram.messenger.AndroidUtilities.displaySize
            int r4 = r3.x
            r0.width = r4
            int r3 = r3.y
            int r3 = r3 / r6
            r0.height = r3
        L_0x005b:
            android.widget.FrameLayout r3 = new android.widget.FrameLayout
            r3.<init>(r1)
            r0.fullscreenVideoContainer = r3
            r3.setKeepScreenOn(r8)
            android.widget.FrameLayout r3 = r0.fullscreenVideoContainer
            r4 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r3.setBackgroundColor(r4)
            int r3 = android.os.Build.VERSION.SDK_INT
            r9 = 21
            if (r3 < r9) goto L_0x0077
            android.widget.FrameLayout r3 = r0.fullscreenVideoContainer
            r3.setFitsSystemWindows(r8)
        L_0x0077:
            android.widget.FrameLayout r3 = r0.fullscreenVideoContainer
            org.telegram.ui.Components.-$$Lambda$EmbedBottomSheet$awgbBJJ9KT-HOZj5dBev_ioRvPo r10 = org.telegram.ui.Components.$$Lambda$EmbedBottomSheet$awgbBJJ9KTHOZj5dBev_ioRvPo.INSTANCE
            r3.setOnTouchListener(r10)
            org.telegram.ui.ActionBar.BottomSheet$ContainerView r3 = r0.container
            android.widget.FrameLayout r10 = r0.fullscreenVideoContainer
            r11 = -1082130432(0xffffffffbvar_, float:-1.0)
            r12 = -1
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r11)
            r3.addView(r10, r11)
            android.widget.FrameLayout r3 = r0.fullscreenVideoContainer
            r10 = 4
            r3.setVisibility(r10)
            android.widget.FrameLayout r3 = r0.fullscreenVideoContainer
            org.telegram.ui.Components.-$$Lambda$EmbedBottomSheet$wxc0LCDO5uuH8qs6VYdAXQWEEZE r11 = org.telegram.ui.Components.$$Lambda$EmbedBottomSheet$wxc0LCDO5uuH8qs6VYdAXQWEEZE.INSTANCE
            r3.setOnTouchListener(r11)
            org.telegram.ui.Components.EmbedBottomSheet$2 r3 = new org.telegram.ui.Components.EmbedBottomSheet$2
            r3.<init>(r1)
            r0.containerLayout = r3
            org.telegram.ui.Components.-$$Lambda$EmbedBottomSheet$iYWCViD8wKuconyj-uGO4wKzEcQ r11 = org.telegram.ui.Components.$$Lambda$EmbedBottomSheet$iYWCViD8wKuconyjuGO4wKzEcQ.INSTANCE
            r3.setOnTouchListener(r11)
            android.widget.FrameLayout r3 = r0.containerLayout
            r0.setCustomView(r3)
            org.telegram.ui.Components.EmbedBottomSheet$3 r3 = new org.telegram.ui.Components.EmbedBottomSheet$3
            r3.<init>(r1)
            r0.webView = r3
            android.webkit.WebSettings r3 = r3.getSettings()
            r3.setJavaScriptEnabled(r8)
            android.webkit.WebView r3 = r0.webView
            android.webkit.WebSettings r3 = r3.getSettings()
            r3.setDomStorageEnabled(r8)
            int r3 = android.os.Build.VERSION.SDK_INT
            r11 = 17
            if (r3 < r11) goto L_0x00d0
            android.webkit.WebView r3 = r0.webView
            android.webkit.WebSettings r3 = r3.getSettings()
            r3.setMediaPlaybackRequiresUserGesture(r5)
        L_0x00d0:
            int r3 = android.os.Build.VERSION.SDK_INT
            if (r3 < r9) goto L_0x00e6
            android.webkit.WebView r3 = r0.webView
            android.webkit.WebSettings r3 = r3.getSettings()
            r3.setMixedContentMode(r5)
            android.webkit.CookieManager r3 = android.webkit.CookieManager.getInstance()
            android.webkit.WebView r9 = r0.webView
            r3.setAcceptThirdPartyCookies(r9, r8)
        L_0x00e6:
            android.webkit.WebView r3 = r0.webView
            org.telegram.ui.Components.EmbedBottomSheet$4 r9 = new org.telegram.ui.Components.EmbedBottomSheet$4
            r9.<init>()
            r3.setWebChromeClient(r9)
            android.webkit.WebView r3 = r0.webView
            org.telegram.ui.Components.EmbedBottomSheet$5 r9 = new org.telegram.ui.Components.EmbedBottomSheet$5
            r9.<init>()
            r3.setWebViewClient(r9)
            android.widget.FrameLayout r3 = r0.containerLayout
            android.webkit.WebView r9 = r0.webView
            r13 = -1
            r14 = -1082130432(0xffffffffbvar_, float:-1.0)
            r15 = 51
            r16 = 0
            r17 = 0
            r18 = 0
            boolean r11 = r0.hasDescription
            r20 = 22
            if (r11 == 0) goto L_0x0112
            r11 = 22
            goto L_0x0113
        L_0x0112:
            r11 = 0
        L_0x0113:
            int r11 = r11 + 84
            float r11 = (float) r11
            r19 = r11
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r13, r14, r15, r16, r17, r18, r19)
            r3.addView(r9, r11)
            org.telegram.ui.Components.WebPlayerView r3 = new org.telegram.ui.Components.WebPlayerView
            org.telegram.ui.Components.EmbedBottomSheet$6 r9 = new org.telegram.ui.Components.EmbedBottomSheet$6
            r9.<init>()
            r3.<init>(r1, r8, r5, r9)
            r0.videoView = r3
            r3.setVisibility(r10)
            android.widget.FrameLayout r3 = r0.containerLayout
            org.telegram.ui.Components.WebPlayerView r9 = r0.videoView
            r13 = -1
            r14 = -1082130432(0xffffffffbvar_, float:-1.0)
            r15 = 51
            r16 = 0
            r17 = 0
            r18 = 0
            boolean r11 = r0.hasDescription
            if (r11 == 0) goto L_0x0144
            r11 = 22
            goto L_0x0145
        L_0x0144:
            r11 = 0
        L_0x0145:
            int r11 = r11 + 84
            int r11 = r11 + -10
            float r11 = (float) r11
            r19 = r11
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r13, r14, r15, r16, r17, r18, r19)
            r3.addView(r9, r11)
            android.view.View r3 = new android.view.View
            r3.<init>(r1)
            r0.progressBarBlackBackground = r3
            r3.setBackgroundColor(r4)
            android.view.View r3 = r0.progressBarBlackBackground
            r3.setVisibility(r10)
            android.widget.FrameLayout r3 = r0.containerLayout
            android.view.View r4 = r0.progressBarBlackBackground
            r13 = -1
            r14 = -1082130432(0xffffffffbvar_, float:-1.0)
            r15 = 51
            r16 = 0
            r17 = 0
            r18 = 0
            boolean r9 = r0.hasDescription
            if (r9 == 0) goto L_0x0178
            r9 = 22
            goto L_0x0179
        L_0x0178:
            r9 = 0
        L_0x0179:
            int r9 = r9 + 84
            float r9 = (float) r9
            r19 = r9
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r13, r14, r15, r16, r17, r18, r19)
            r3.addView(r4, r9)
            org.telegram.ui.Components.RadialProgressView r3 = new org.telegram.ui.Components.RadialProgressView
            r3.<init>(r1)
            r0.progressBar = r3
            r3.setVisibility(r10)
            android.widget.FrameLayout r3 = r0.containerLayout
            org.telegram.ui.Components.RadialProgressView r4 = r0.progressBar
            r13 = -2
            r14 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r15 = 17
            r16 = 0
            r17 = 0
            r18 = 0
            boolean r9 = r0.hasDescription
            if (r9 == 0) goto L_0x01a3
            goto L_0x01a5
        L_0x01a3:
            r20 = 0
        L_0x01a5:
            int r20 = r20 + 84
            int r6 = r20 / 2
            float r6 = (float) r6
            r19 = r6
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r13, r14, r15, r16, r17, r18, r19)
            r3.addView(r4, r6)
            boolean r3 = r0.hasDescription
            java.lang.String r4 = "fonts/rmedium.ttf"
            r6 = 1099956224(0x41900000, float:18.0)
            if (r3 == 0) goto L_0x0201
            android.widget.TextView r3 = new android.widget.TextView
            r3.<init>(r1)
            r9 = 1098907648(0x41800000, float:16.0)
            r3.setTextSize(r8, r9)
            java.lang.String r9 = "dialogTextBlack"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r3.setTextColor(r9)
            r3.setText(r2)
            r3.setSingleLine(r8)
            android.graphics.Typeface r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r4)
            r3.setTypeface(r2)
            android.text.TextUtils$TruncateAt r2 = android.text.TextUtils.TruncateAt.END
            r3.setEllipsize(r2)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r3.setPadding(r2, r5, r9, r5)
            android.widget.FrameLayout r2 = r0.containerLayout
            r13 = -1
            r14 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r15 = 83
            r16 = 0
            r17 = 0
            r18 = 0
            r19 = 1117388800(0x429a0000, float:77.0)
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r13, r14, r15, r16, r17, r18, r19)
            r2.addView(r3, r9)
        L_0x0201:
            android.widget.TextView r2 = new android.widget.TextView
            r2.<init>(r1)
            r3 = 1096810496(0x41600000, float:14.0)
            r2.setTextSize(r8, r3)
            java.lang.String r9 = "dialogTextGray"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r2.setTextColor(r9)
            r9 = r26
            r2.setText(r9)
            r2.setSingleLine(r8)
            android.text.TextUtils$TruncateAt r9 = android.text.TextUtils.TruncateAt.END
            r2.setEllipsize(r9)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r2.setPadding(r9, r5, r11, r5)
            android.widget.FrameLayout r9 = r0.containerLayout
            r13 = -1
            r14 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r15 = 83
            r16 = 0
            r17 = 0
            r18 = 0
            r19 = 1113849856(0x42640000, float:57.0)
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r13, r14, r15, r16, r17, r18, r19)
            r9.addView(r2, r11)
            android.view.View r2 = new android.view.View
            r2.<init>(r1)
            java.lang.String r9 = "dialogGrayLine"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r2.setBackgroundColor(r9)
            android.widget.FrameLayout r9 = r0.containerLayout
            android.widget.FrameLayout$LayoutParams r11 = new android.widget.FrameLayout$LayoutParams
            r13 = 83
            r11.<init>(r12, r8, r13)
            r9.addView(r2, r11)
            android.view.ViewGroup$LayoutParams r2 = r2.getLayoutParams()
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
            r11 = 48
            android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r11, r13)
            r9.addView(r2, r13)
            android.widget.LinearLayout r9 = new android.widget.LinearLayout
            r9.<init>(r1)
            r9.setOrientation(r5)
            r13 = 1065353216(0x3var_, float:1.0)
            r9.setWeightSum(r13)
            r13 = 53
            android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r12, r13)
            r2.addView(r9, r13)
            android.widget.TextView r13 = new android.widget.TextView
            r13.<init>(r1)
            r13.setTextSize(r8, r3)
            java.lang.String r14 = "dialogTextBlue4"
            int r15 = org.telegram.ui.ActionBar.Theme.getColor(r14)
            r13.setTextColor(r15)
            r15 = 17
            r13.setGravity(r15)
            r13.setSingleLine(r8)
            android.text.TextUtils$TruncateAt r15 = android.text.TextUtils.TruncateAt.END
            r13.setEllipsize(r15)
            java.lang.String r15 = "dialogButtonSelector"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r15)
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r3, r5)
            r13.setBackgroundDrawable(r3)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r13.setPadding(r3, r5, r8, r5)
            r3 = 2131624816(0x7f0e0370, float:1.8876822E38)
            java.lang.String r8 = "Close"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r8, r3)
            java.lang.String r3 = r3.toUpperCase()
            r13.setText(r3)
            android.graphics.Typeface r3 = org.telegram.messenger.AndroidUtilities.getTypeface(r4)
            r13.setTypeface(r3)
            r3 = 51
            android.widget.LinearLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r7, (int) r12, (int) r3)
            r2.addView(r13, r8)
            org.telegram.ui.Components.-$$Lambda$EmbedBottomSheet$7BVC7l6jtG_KhdXyPoXIswQ3uh8 r8 = new org.telegram.ui.Components.-$$Lambda$EmbedBottomSheet$7BVC7l6jtG_KhdXyPoXIswQ3uh8
            r8.<init>()
            r13.setOnClickListener(r8)
            android.widget.LinearLayout r8 = new android.widget.LinearLayout
            r8.<init>(r1)
            r0.imageButtonsContainer = r8
            r8.setVisibility(r10)
            android.widget.LinearLayout r8 = r0.imageButtonsContainer
            r13 = 17
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r12, r13)
            r2.addView(r8, r10)
            android.widget.ImageView r2 = new android.widget.ImageView
            r2.<init>(r1)
            r0.pipButton = r2
            android.widget.ImageView$ScaleType r8 = android.widget.ImageView.ScaleType.CENTER
            r2.setScaleType(r8)
            android.widget.ImageView r2 = r0.pipButton
            r8 = 2131166009(0x7var_, float:1.7946251E38)
            r2.setImageResource(r8)
            android.widget.ImageView r2 = r0.pipButton
            r8 = 2131624005(0x7f0e0045, float:1.8875177E38)
            java.lang.String r10 = "AccDescrPipMode"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r10, r8)
            r2.setContentDescription(r8)
            android.widget.ImageView r2 = r0.pipButton
            r2.setEnabled(r5)
            android.widget.ImageView r2 = r0.pipButton
            r8 = 1056964608(0x3var_, float:0.5)
            r2.setAlpha(r8)
            android.widget.ImageView r2 = r0.pipButton
            android.graphics.PorterDuffColorFilter r8 = new android.graphics.PorterDuffColorFilter
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r14)
            android.graphics.PorterDuff$Mode r13 = android.graphics.PorterDuff.Mode.MULTIPLY
            r8.<init>(r10, r13)
            r2.setColorFilter(r8)
            android.widget.ImageView r2 = r0.pipButton
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r15)
            android.graphics.drawable.Drawable r8 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r8, r5)
            r2.setBackgroundDrawable(r8)
            android.widget.LinearLayout r2 = r0.imageButtonsContainer
            android.widget.ImageView r8 = r0.pipButton
            r17 = 48
            r18 = 1111490560(0x42400000, float:48.0)
            r19 = 51
            r20 = 0
            r21 = 0
            r22 = 1082130432(0x40800000, float:4.0)
            r23 = 0
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r17, r18, r19, r20, r21, r22, r23)
            r2.addView(r8, r10)
            android.widget.ImageView r2 = r0.pipButton
            org.telegram.ui.Components.-$$Lambda$EmbedBottomSheet$y37DL-JjK8uMG3joIQ1jW7ubjuE r8 = new org.telegram.ui.Components.-$$Lambda$EmbedBottomSheet$y37DL-JjK8uMG3joIQ1jW7ubjuE
            r8.<init>()
            r2.setOnClickListener(r8)
            org.telegram.ui.Components.-$$Lambda$EmbedBottomSheet$ME3BZek9Olzkn_G2jOkI4EyTOfQ r2 = new org.telegram.ui.Components.-$$Lambda$EmbedBottomSheet$ME3BZek9Olzkn_G2jOkI4EyTOfQ
            r2.<init>()
            android.widget.ImageView r8 = new android.widget.ImageView
            r8.<init>(r1)
            android.widget.ImageView$ScaleType r10 = android.widget.ImageView.ScaleType.CENTER
            r8.setScaleType(r10)
            r10 = 2131166001(0x7var_, float:1.7946235E38)
            r8.setImageResource(r10)
            r10 = 2131624881(0x7f0e03b1, float:1.8876954E38)
            java.lang.String r13 = "CopyLink"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r13, r10)
            r8.setContentDescription(r10)
            android.graphics.PorterDuffColorFilter r10 = new android.graphics.PorterDuffColorFilter
            int r13 = org.telegram.ui.ActionBar.Theme.getColor(r14)
            android.graphics.PorterDuff$Mode r7 = android.graphics.PorterDuff.Mode.MULTIPLY
            r10.<init>(r13, r7)
            r8.setColorFilter(r10)
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r15)
            android.graphics.drawable.Drawable r7 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r7, r5)
            r8.setBackgroundDrawable(r7)
            android.widget.LinearLayout r7 = r0.imageButtonsContainer
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r11, r3)
            r7.addView(r8, r10)
            r8.setOnClickListener(r2)
            android.widget.TextView r7 = new android.widget.TextView
            r7.<init>(r1)
            r0.copyTextButton = r7
            r8 = 1096810496(0x41600000, float:14.0)
            r10 = 1
            r7.setTextSize(r10, r8)
            android.widget.TextView r7 = r0.copyTextButton
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r14)
            r7.setTextColor(r8)
            android.widget.TextView r7 = r0.copyTextButton
            r8 = 17
            r7.setGravity(r8)
            android.widget.TextView r7 = r0.copyTextButton
            r7.setSingleLine(r10)
            android.widget.TextView r7 = r0.copyTextButton
            android.text.TextUtils$TruncateAt r8 = android.text.TextUtils.TruncateAt.END
            r7.setEllipsize(r8)
            android.widget.TextView r7 = r0.copyTextButton
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r15)
            android.graphics.drawable.Drawable r8 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r8, r5)
            r7.setBackgroundDrawable(r8)
            android.widget.TextView r7 = r0.copyTextButton
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r7.setPadding(r8, r5, r10, r5)
            android.widget.TextView r7 = r0.copyTextButton
            r8 = 2131624879(0x7f0e03af, float:1.887695E38)
            java.lang.String r10 = "Copy"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r10, r8)
            java.lang.String r8 = r8.toUpperCase()
            r7.setText(r8)
            android.widget.TextView r7 = r0.copyTextButton
            android.graphics.Typeface r8 = org.telegram.messenger.AndroidUtilities.getTypeface(r4)
            r7.setTypeface(r8)
            android.widget.TextView r7 = r0.copyTextButton
            r8 = -2
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r12, r3)
            r9.addView(r7, r10)
            android.widget.TextView r7 = r0.copyTextButton
            r7.setOnClickListener(r2)
            android.widget.TextView r2 = new android.widget.TextView
            r2.<init>(r1)
            r1 = 1096810496(0x41600000, float:14.0)
            r7 = 1
            r2.setTextSize(r7, r1)
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r14)
            r2.setTextColor(r1)
            r1 = 17
            r2.setGravity(r1)
            r2.setSingleLine(r7)
            android.text.TextUtils$TruncateAt r1 = android.text.TextUtils.TruncateAt.END
            r2.setEllipsize(r1)
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r15)
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r1, r5)
            r2.setBackgroundDrawable(r1)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r2.setPadding(r1, r5, r6, r5)
            r1 = 2131626156(0x7f0e08ac, float:1.887954E38)
            java.lang.String r6 = "OpenInBrowser"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r6, r1)
            java.lang.String r1 = r1.toUpperCase()
            r2.setText(r1)
            android.graphics.Typeface r1 = org.telegram.messenger.AndroidUtilities.getTypeface(r4)
            r2.setTypeface(r1)
            r1 = -2
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r1, r12, r3)
            r9.addView(r2, r1)
            org.telegram.ui.Components.-$$Lambda$EmbedBottomSheet$xNtglytXCFyYO3cCP1Moj3HVHKg r1 = new org.telegram.ui.Components.-$$Lambda$EmbedBottomSheet$xNtglytXCFyYO3cCP1Moj3HVHKg
            r1.<init>()
            r2.setOnClickListener(r1)
            org.telegram.ui.Components.WebPlayerView r1 = r0.videoView
            java.lang.String r2 = r0.embedUrl
            boolean r1 = r1.canHandleUrl(r2)
            if (r1 != 0) goto L_0x0499
            org.telegram.ui.Components.WebPlayerView r2 = r0.videoView
            r3 = 4
            r2.setVisibility(r3)
        L_0x0499:
            org.telegram.ui.Components.EmbedBottomSheet$8 r2 = new org.telegram.ui.Components.EmbedBottomSheet$8
            r2.<init>(r1)
            r0.setDelegate(r2)
            org.telegram.ui.Components.EmbedBottomSheet$9 r2 = new org.telegram.ui.Components.EmbedBottomSheet$9
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext
            r2.<init>(r3)
            r0.orientationEventListener = r2
            org.telegram.ui.Components.WebPlayerView r2 = r0.videoView
            java.lang.String r3 = r0.embedUrl
            java.lang.String r2 = r2.getYouTubeVideoId(r3)
            if (r2 != 0) goto L_0x04b6
            if (r1 != 0) goto L_0x0519
        L_0x04b6:
            org.telegram.ui.Components.RadialProgressView r1 = r0.progressBar
            r1.setVisibility(r5)
            android.webkit.WebView r1 = r0.webView
            r1.setVisibility(r5)
            android.widget.LinearLayout r1 = r0.imageButtonsContainer
            r1.setVisibility(r5)
            if (r2 == 0) goto L_0x04cc
            android.view.View r1 = r0.progressBarBlackBackground
            r1.setVisibility(r5)
        L_0x04cc:
            android.widget.TextView r1 = r0.copyTextButton
            r3 = 4
            r1.setVisibility(r3)
            android.webkit.WebView r1 = r0.webView
            r4 = 1
            r1.setKeepScreenOn(r4)
            org.telegram.ui.Components.WebPlayerView r1 = r0.videoView
            r1.setVisibility(r3)
            org.telegram.ui.Components.WebPlayerView r1 = r0.videoView
            android.view.View r1 = r1.getControlsView()
            r1.setVisibility(r3)
            org.telegram.ui.Components.WebPlayerView r1 = r0.videoView
            android.view.TextureView r1 = r1.getTextureView()
            r1.setVisibility(r3)
            org.telegram.ui.Components.WebPlayerView r1 = r0.videoView
            android.widget.ImageView r1 = r1.getTextureImageView()
            if (r1 == 0) goto L_0x0500
            org.telegram.ui.Components.WebPlayerView r1 = r0.videoView
            android.widget.ImageView r1 = r1.getTextureImageView()
            r1.setVisibility(r3)
        L_0x0500:
            if (r2 == 0) goto L_0x0519
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            java.lang.String r1 = r1.youtubePipType
            java.lang.String r2 = "disabled"
            boolean r1 = r2.equals(r1)
            if (r1 == 0) goto L_0x0519
            android.widget.ImageView r1 = r0.pipButton
            r2 = 8
            r1.setVisibility(r2)
        L_0x0519:
            android.view.OrientationEventListener r1 = r0.orientationEventListener
            boolean r1 = r1.canDetectOrientation()
            if (r1 == 0) goto L_0x0527
            android.view.OrientationEventListener r1 = r0.orientationEventListener
            r1.enable()
            goto L_0x052f
        L_0x0527:
            android.view.OrientationEventListener r1 = r0.orientationEventListener
            r1.disable()
            r1 = 0
            r0.orientationEventListener = r1
        L_0x052f:
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
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.parentActivity);
        builder.setTitle(LocaleController.getString("AppName", NUM));
        builder.setMessage(LocaleController.getString("PermissionDrawAboveOtherApps", NUM));
        builder.setPositiveButton(LocaleController.getString("PermissionOpenSettings", NUM), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                EmbedBottomSheet.this.lambda$checkInlinePermissions$7$EmbedBottomSheet(dialogInterface, i);
            }
        });
        builder.show();
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
