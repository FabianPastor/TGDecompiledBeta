package org.telegram.ui.Components;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Build;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BringAppForegroundService;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.TLRPC$WebPage;
import org.telegram.ui.PhotoViewer;

public class PhotoViewerWebView extends FrameLayout {
    /* access modifiers changed from: private */
    public float bufferedPosition;
    private int currentAccount = UserConfig.selectedAccount;
    /* access modifiers changed from: private */
    public int currentPosition;
    private TLRPC$WebPage currentWebpage;
    /* access modifiers changed from: private */
    public boolean isPlaying;
    private boolean isTouchDisabled;
    /* access modifiers changed from: private */
    public boolean isYouTube;
    /* access modifiers changed from: private */
    public PhotoViewer photoViewer;
    /* access modifiers changed from: private */
    public View pipItem;
    /* access modifiers changed from: private */
    public float playbackSpeed;
    /* access modifiers changed from: private */
    public RadialProgressView progressBar;
    /* access modifiers changed from: private */
    public View progressBarBlackBackground;
    private Runnable progressRunnable = new PhotoViewerWebView$$ExternalSyntheticLambda2(this);
    /* access modifiers changed from: private */
    public boolean setPlaybackSpeed;
    /* access modifiers changed from: private */
    public int videoDuration;
    private WebView webView;

    /* access modifiers changed from: protected */
    public void drawBlackBackground(Canvas canvas, int i, int i2) {
    }

    public void hideControls() {
    }

    /* access modifiers changed from: protected */
    public void processTouch(MotionEvent motionEvent) {
    }

    public void showControls() {
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        if (this.isYouTube) {
            runJsCode("pollPosition();");
        }
        if (this.isPlaying) {
            AndroidUtilities.runOnUIThread(this.progressRunnable, 500);
        }
    }

    private class YoutubeProxy {
        private YoutubeProxy() {
        }

        @JavascriptInterface
        public void onPlayerLoaded() {
            AndroidUtilities.runOnUIThread(new PhotoViewerWebView$YoutubeProxy$$ExternalSyntheticLambda0(this));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onPlayerLoaded$0() {
            PhotoViewerWebView.this.progressBar.setVisibility(4);
            PhotoViewerWebView.this.progressBarBlackBackground.setVisibility(4);
            if (PhotoViewerWebView.this.setPlaybackSpeed) {
                boolean unused = PhotoViewerWebView.this.setPlaybackSpeed = false;
                PhotoViewerWebView photoViewerWebView = PhotoViewerWebView.this;
                photoViewerWebView.setPlaybackSpeed(photoViewerWebView.playbackSpeed);
            }
            PhotoViewerWebView.this.pipItem.setEnabled(true);
            PhotoViewerWebView.this.pipItem.setAlpha(1.0f);
        }

        @JavascriptInterface
        public void onPlayerStateChange(String str) {
            int parseInt = Integer.parseInt(str);
            boolean access$000 = PhotoViewerWebView.this.isPlaying;
            boolean z = false;
            int i = 3;
            boolean unused = PhotoViewerWebView.this.isPlaying = parseInt == 1 || parseInt == 3;
            PhotoViewerWebView.this.checkPlayingPoll(access$000);
            if (parseInt == 0) {
                i = 4;
            } else if (parseInt == 1) {
                z = true;
            } else if (parseInt != 2) {
                if (parseInt != 3) {
                    i = 1;
                } else {
                    z = true;
                    i = 2;
                }
            }
            AndroidUtilities.runOnUIThread(new PhotoViewerWebView$YoutubeProxy$$ExternalSyntheticLambda1(this, z, i));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onPlayerStateChange$1(boolean z, int i) {
            PhotoViewerWebView.this.photoViewer.updateWebPlayerState(z, i);
        }

        @JavascriptInterface
        public void onPlayerNotifyDuration(int i) {
            int unused = PhotoViewerWebView.this.videoDuration = i * 1000;
        }

        @JavascriptInterface
        public void onPlayerNotifyCurrentPosition(int i) {
            int unused = PhotoViewerWebView.this.currentPosition = i * 1000;
        }

        @JavascriptInterface
        public void onPlayerNotifyBufferedPosition(float f) {
            float unused = PhotoViewerWebView.this.bufferedPosition = f;
        }
    }

    @SuppressLint({"SetJavaScriptEnabled"})
    public PhotoViewerWebView(PhotoViewer photoViewer2, Context context, View view) {
        super(context);
        this.photoViewer = photoViewer2;
        this.pipItem = view;
        AnonymousClass1 r4 = new WebView(context) {
            public boolean onTouchEvent(MotionEvent motionEvent) {
                PhotoViewerWebView.this.processTouch(motionEvent);
                return super.onTouchEvent(motionEvent);
            }
        };
        this.webView = r4;
        r4.getSettings().setJavaScriptEnabled(true);
        this.webView.getSettings().setDomStorageEnabled(true);
        int i = Build.VERSION.SDK_INT;
        if (i >= 17) {
            this.webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        }
        if (i >= 21) {
            this.webView.getSettings().setMixedContentMode(0);
            CookieManager.getInstance().setAcceptThirdPartyCookies(this.webView, true);
        }
        this.webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView webView, String str) {
                super.onPageFinished(webView, str);
                if (!PhotoViewerWebView.this.isYouTube || Build.VERSION.SDK_INT < 17) {
                    PhotoViewerWebView.this.progressBar.setVisibility(4);
                    PhotoViewerWebView.this.progressBarBlackBackground.setVisibility(4);
                    PhotoViewerWebView.this.pipItem.setEnabled(true);
                    PhotoViewerWebView.this.pipItem.setAlpha(1.0f);
                }
            }

            public boolean shouldOverrideUrlLoading(WebView webView, String str) {
                if (!PhotoViewerWebView.this.isYouTube) {
                    return super.shouldOverrideUrlLoading(webView, str);
                }
                Browser.openUrl(webView.getContext(), str);
                return true;
            }
        });
        addView(this.webView, LayoutHelper.createFrame(-1, -1, 51));
        AnonymousClass3 r42 = new View(context) {
            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                PhotoViewerWebView.this.drawBlackBackground(canvas, getMeasuredWidth(), getMeasuredHeight());
            }
        };
        this.progressBarBlackBackground = r42;
        r42.setBackgroundColor(-16777216);
        this.progressBarBlackBackground.setVisibility(4);
        addView(this.progressBarBlackBackground, LayoutHelper.createFrame(-1, -1.0f));
        RadialProgressView radialProgressView = new RadialProgressView(context);
        this.progressBar = radialProgressView;
        radialProgressView.setVisibility(4);
        addView(this.progressBar, LayoutHelper.createFrame(-2, -2, 17));
    }

    public void setTouchDisabled(boolean z) {
        this.isTouchDisabled = z;
    }

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (this.isTouchDisabled) {
            return false;
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    public WebView getWebView() {
        return this.webView;
    }

    /* access modifiers changed from: private */
    public void checkPlayingPoll(boolean z) {
        if (!z && this.isPlaying) {
            AndroidUtilities.runOnUIThread(this.progressRunnable, 500);
        } else if (z && !this.isPlaying) {
            AndroidUtilities.cancelRunOnUIThread(this.progressRunnable);
        }
    }

    public void seekTo(long j) {
        seekTo(j, true);
    }

    public void seekTo(long j, boolean z) {
        boolean z2 = this.isPlaying;
        this.currentPosition = (int) j;
        if (z2) {
            pauseVideo();
        }
        if (z2) {
            AndroidUtilities.runOnUIThread(new PhotoViewerWebView$$ExternalSyntheticLambda3(this, j, z), 100);
            return;
        }
        runJsCode("seekTo(" + Math.round(((float) j) / 1000.0f) + ", " + z + ");");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$seekTo$1(long j, boolean z) {
        runJsCode("seekTo(" + Math.round(((float) j) / 1000.0f) + ", " + z + ");");
        AndroidUtilities.runOnUIThread(new PhotoViewerWebView$$ExternalSyntheticLambda1(this), 100);
    }

    public int getVideoDuration() {
        return this.videoDuration;
    }

    public int getCurrentPosition() {
        return this.currentPosition;
    }

    public float getBufferedPosition() {
        return this.bufferedPosition;
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

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        if (this.webView.getParent() == this) {
            TLRPC$WebPage tLRPC$WebPage = this.currentWebpage;
            int i3 = tLRPC$WebPage.embed_width;
            int i4 = 100;
            if (i3 == 0) {
                i3 = 100;
            }
            int i5 = tLRPC$WebPage.embed_height;
            if (i5 != 0) {
                i4 = i5;
            }
            int size = View.MeasureSpec.getSize(i);
            int size2 = View.MeasureSpec.getSize(i2);
            float f = (float) i3;
            float f2 = (float) i4;
            float min = Math.min(((float) size) / f, ((float) size2) / f2);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.webView.getLayoutParams();
            int i6 = (int) (f * min);
            layoutParams.width = i6;
            int i7 = (int) (f2 * min);
            layoutParams.height = i7;
            layoutParams.topMargin = (size2 - i7) / 2;
            layoutParams.leftMargin = (size - i6) / 2;
        }
        super.onMeasure(i, i2);
    }

    public boolean isLoaded() {
        return this.progressBar.getVisibility() != 0;
    }

    public boolean isInAppOnly() {
        return this.isYouTube && "inapp".equals(MessagesController.getInstance(this.currentAccount).youtubePipType);
    }

    public boolean openInPip() {
        boolean isInAppOnly = isInAppOnly();
        if ((!isInAppOnly && !checkInlinePermissions()) || this.progressBar.getVisibility() == 0) {
            return false;
        }
        if (PipVideoOverlay.isVisible()) {
            PipVideoOverlay.dismiss();
            AndroidUtilities.runOnUIThread(new PhotoViewerWebView$$ExternalSyntheticLambda0(this), 300);
            return true;
        }
        WebView webView2 = this.webView;
        TLRPC$WebPage tLRPC$WebPage = this.currentWebpage;
        if (PipVideoOverlay.show(isInAppOnly, (Activity) getContext(), this, webView2, tLRPC$WebPage.embed_width, tLRPC$WebPage.embed_height, false)) {
            PipVideoOverlay.setPhotoViewer(PhotoViewer.getInstance());
        }
        return true;
    }

    public boolean isControllable() {
        return this.isYouTube;
    }

    public boolean isPlaying() {
        return this.isPlaying;
    }

    public void playVideo() {
        if (!this.isPlaying && isControllable()) {
            runJsCode("playVideo();");
            this.isPlaying = true;
            checkPlayingPoll(false);
        }
    }

    public void pauseVideo() {
        if (this.isPlaying && isControllable()) {
            runJsCode("pauseVideo();");
            this.isPlaying = false;
            checkPlayingPoll(true);
        }
    }

    public void setPlaybackSpeed(float f) {
        this.playbackSpeed = f;
        if (this.progressBar.getVisibility() == 0) {
            this.setPlaybackSpeed = true;
        } else if (this.isYouTube) {
            runJsCode("setPlaybackSpeed(" + f + ");");
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:27:0x00a6 A[Catch:{ Exception -> 0x00e9 }, LOOP:0: B:25:0x009f->B:27:0x00a6, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x00aa A[EDGE_INSN: B:40:0x00aa->B:28:0x00aa ?: BREAK  , SYNTHETIC] */
    @android.annotation.SuppressLint({"AddJavascriptInterface"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void init(int r12, org.telegram.tgnet.TLRPC$WebPage r13) {
        /*
            r11 = this;
            java.lang.String r0 = "m"
            r11.currentWebpage = r13
            java.lang.String r1 = r13.embed_url
            java.lang.String r1 = org.telegram.ui.Components.WebPlayerView.getYouTubeVideoId(r1)
            java.lang.String r2 = r13.url
            r11.requestLayout()
            r3 = 1
            r4 = 0
            if (r1 == 0) goto L_0x00d5
            android.view.View r13 = r11.progressBarBlackBackground     // Catch:{ Exception -> 0x00e9 }
            r13.setVisibility(r4)     // Catch:{ Exception -> 0x00e9 }
            r11.isYouTube = r3     // Catch:{ Exception -> 0x00e9 }
            int r13 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x00e9 }
            r5 = 17
            r6 = 0
            if (r13 < r5) goto L_0x002d
            android.webkit.WebView r13 = r11.webView     // Catch:{ Exception -> 0x00e9 }
            org.telegram.ui.Components.PhotoViewerWebView$YoutubeProxy r5 = new org.telegram.ui.Components.PhotoViewerWebView$YoutubeProxy     // Catch:{ Exception -> 0x00e9 }
            r5.<init>()     // Catch:{ Exception -> 0x00e9 }
            java.lang.String r7 = "YoutubeProxy"
            r13.addJavascriptInterface(r5, r7)     // Catch:{ Exception -> 0x00e9 }
        L_0x002d:
            if (r2 == 0) goto L_0x0087
            android.net.Uri r13 = android.net.Uri.parse(r2)     // Catch:{ Exception -> 0x0083 }
            if (r12 <= 0) goto L_0x0046
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0083 }
            r2.<init>()     // Catch:{ Exception -> 0x0083 }
            java.lang.String r5 = ""
            r2.append(r5)     // Catch:{ Exception -> 0x0083 }
            r2.append(r12)     // Catch:{ Exception -> 0x0083 }
            java.lang.String r6 = r2.toString()     // Catch:{ Exception -> 0x0083 }
        L_0x0046:
            if (r6 != 0) goto L_0x0056
            java.lang.String r12 = "t"
            java.lang.String r6 = r13.getQueryParameter(r12)     // Catch:{ Exception -> 0x0083 }
            if (r6 != 0) goto L_0x0056
            java.lang.String r12 = "time_continue"
            java.lang.String r6 = r13.getQueryParameter(r12)     // Catch:{ Exception -> 0x0083 }
        L_0x0056:
            if (r6 == 0) goto L_0x0087
            boolean r12 = r6.contains(r0)     // Catch:{ Exception -> 0x0083 }
            if (r12 == 0) goto L_0x007a
            java.lang.String[] r12 = r6.split(r0)     // Catch:{ Exception -> 0x0083 }
            r13 = r12[r4]     // Catch:{ Exception -> 0x0083 }
            java.lang.Integer r13 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r13)     // Catch:{ Exception -> 0x0083 }
            int r13 = r13.intValue()     // Catch:{ Exception -> 0x0083 }
            int r13 = r13 * 60
            r12 = r12[r3]     // Catch:{ Exception -> 0x0083 }
            java.lang.Integer r12 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r12)     // Catch:{ Exception -> 0x0083 }
            int r12 = r12.intValue()     // Catch:{ Exception -> 0x0083 }
            int r13 = r13 + r12
            goto L_0x0088
        L_0x007a:
            java.lang.Integer r12 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r6)     // Catch:{ Exception -> 0x0083 }
            int r13 = r12.intValue()     // Catch:{ Exception -> 0x0083 }
            goto L_0x0088
        L_0x0083:
            r12 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r12)     // Catch:{ Exception -> 0x00e9 }
        L_0x0087:
            r13 = 0
        L_0x0088:
            android.content.Context r12 = r11.getContext()     // Catch:{ Exception -> 0x00e9 }
            android.content.res.AssetManager r12 = r12.getAssets()     // Catch:{ Exception -> 0x00e9 }
            java.lang.String r0 = "youtube_embed.html"
            java.io.InputStream r12 = r12.open(r0)     // Catch:{ Exception -> 0x00e9 }
            java.io.ByteArrayOutputStream r0 = new java.io.ByteArrayOutputStream     // Catch:{ Exception -> 0x00e9 }
            r0.<init>()     // Catch:{ Exception -> 0x00e9 }
            r2 = 10240(0x2800, float:1.4349E-41)
            byte[] r2 = new byte[r2]     // Catch:{ Exception -> 0x00e9 }
        L_0x009f:
            int r5 = r12.read(r2)     // Catch:{ Exception -> 0x00e9 }
            r6 = -1
            if (r5 == r6) goto L_0x00aa
            r0.write(r2, r4, r5)     // Catch:{ Exception -> 0x00e9 }
            goto L_0x009f
        L_0x00aa:
            r0.close()     // Catch:{ Exception -> 0x00e9 }
            r12.close()     // Catch:{ Exception -> 0x00e9 }
            android.webkit.WebView r5 = r11.webView     // Catch:{ Exception -> 0x00e9 }
            java.lang.String r6 = "https://messenger.telegram.org/"
            java.util.Locale r12 = java.util.Locale.US     // Catch:{ Exception -> 0x00e9 }
            java.lang.String r2 = "UTF-8"
            java.lang.String r0 = r0.toString(r2)     // Catch:{ Exception -> 0x00e9 }
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ Exception -> 0x00e9 }
            r2[r4] = r1     // Catch:{ Exception -> 0x00e9 }
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)     // Catch:{ Exception -> 0x00e9 }
            r2[r3] = r13     // Catch:{ Exception -> 0x00e9 }
            java.lang.String r7 = java.lang.String.format(r12, r0, r2)     // Catch:{ Exception -> 0x00e9 }
            java.lang.String r8 = "text/html"
            java.lang.String r9 = "UTF-8"
            java.lang.String r10 = "https://youtube.com"
            r5.loadDataWithBaseURL(r6, r7, r8, r9, r10)     // Catch:{ Exception -> 0x00e9 }
            goto L_0x00ed
        L_0x00d5:
            java.util.HashMap r12 = new java.util.HashMap     // Catch:{ Exception -> 0x00e9 }
            r12.<init>()     // Catch:{ Exception -> 0x00e9 }
            java.lang.String r0 = "Referer"
            java.lang.String r2 = "messenger.telegram.org"
            r12.put(r0, r2)     // Catch:{ Exception -> 0x00e9 }
            android.webkit.WebView r0 = r11.webView     // Catch:{ Exception -> 0x00e9 }
            java.lang.String r13 = r13.embed_url     // Catch:{ Exception -> 0x00e9 }
            r0.loadUrl(r13, r12)     // Catch:{ Exception -> 0x00e9 }
            goto L_0x00ed
        L_0x00e9:
            r12 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r12)
        L_0x00ed:
            android.view.View r12 = r11.pipItem
            r12.setEnabled(r4)
            android.view.View r12 = r11.pipItem
            r13 = 1056964608(0x3var_, float:0.5)
            r12.setAlpha(r13)
            org.telegram.ui.Components.RadialProgressView r12 = r11.progressBar
            r12.setVisibility(r4)
            if (r1 == 0) goto L_0x0105
            android.view.View r12 = r11.progressBarBlackBackground
            r12.setVisibility(r4)
        L_0x0105:
            android.webkit.WebView r12 = r11.webView
            r12.setVisibility(r4)
            android.webkit.WebView r12 = r11.webView
            r12.setKeepScreenOn(r3)
            if (r1 == 0) goto L_0x0128
            int r12 = r11.currentAccount
            org.telegram.messenger.MessagesController r12 = org.telegram.messenger.MessagesController.getInstance(r12)
            java.lang.String r12 = r12.youtubePipType
            java.lang.String r13 = "disabled"
            boolean r12 = r13.equals(r12)
            if (r12 == 0) goto L_0x0128
            android.view.View r12 = r11.pipItem
            r13 = 8
            r12.setVisibility(r13)
        L_0x0128:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PhotoViewerWebView.init(int, org.telegram.tgnet.TLRPC$WebPage):void");
    }

    public boolean checkInlinePermissions() {
        if (Build.VERSION.SDK_INT < 23 || Settings.canDrawOverlays(getContext())) {
            return true;
        }
        AlertsCreator.createDrawOverlayPermissionDialog((Activity) getContext(), (DialogInterface.OnClickListener) null);
        return false;
    }

    public void exitFromPip() {
        if (this.webView != null) {
            if (ApplicationLoader.mainInterfacePaused) {
                try {
                    getContext().startService(new Intent(ApplicationLoader.applicationContext, BringAppForegroundService.class));
                } catch (Throwable th) {
                    FileLog.e(th);
                }
            }
            ViewGroup viewGroup = (ViewGroup) this.webView.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(this.webView);
            }
            addView(this.webView, 0, LayoutHelper.createFrame(-1, -1, 51));
            PipVideoOverlay.dismiss();
        }
    }

    public void release() {
        this.webView.stopLoading();
        this.webView.loadUrl("about:blank");
        this.webView.destroy();
        AndroidUtilities.cancelRunOnUIThread(this.progressRunnable);
    }
}
