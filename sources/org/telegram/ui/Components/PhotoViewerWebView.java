package org.telegram.ui.Components;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Build;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BringAppForegroundService;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.TLRPC$WebPage;
import org.telegram.ui.Components.PhotoViewerWebView;
import org.telegram.ui.PhotoViewer;
/* loaded from: classes3.dex */
public class PhotoViewerWebView extends FrameLayout {
    private float bufferedPosition;
    private int currentAccount;
    private int currentPosition;
    private TLRPC$WebPage currentWebpage;
    private String currentYoutubeId;
    private boolean isPlaying;
    private boolean isTouchDisabled;
    private boolean isYouTube;
    private PhotoViewer photoViewer;
    private View pipItem;
    private float playbackSpeed;
    private RadialProgressView progressBar;
    private View progressBarBlackBackground;
    private Runnable progressRunnable;
    private boolean setPlaybackSpeed;
    private int videoDuration;
    private WebView webView;
    private List<String> youtubeStoryboards;
    private String youtubeStoryboardsSpecUrl;

    protected void drawBlackBackground(Canvas canvas, int i, int i2) {
    }

    public void hideControls() {
    }

    protected void processTouch(MotionEvent motionEvent) {
    }

    public void showControls() {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        if (this.isYouTube) {
            runJsCode("pollPosition();");
        }
        if (this.isPlaying) {
            AndroidUtilities.runOnUIThread(this.progressRunnable, 500L);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class YoutubeProxy {
        private YoutubeProxy() {
        }

        @JavascriptInterface
        public void onPlayerLoaded() {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.PhotoViewerWebView$YoutubeProxy$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    PhotoViewerWebView.YoutubeProxy.this.lambda$onPlayerLoaded$0();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onPlayerLoaded$0() {
            PhotoViewerWebView.this.progressBar.setVisibility(4);
            if (PhotoViewerWebView.this.setPlaybackSpeed) {
                PhotoViewerWebView.this.setPlaybackSpeed = false;
                PhotoViewerWebView photoViewerWebView = PhotoViewerWebView.this;
                photoViewerWebView.setPlaybackSpeed(photoViewerWebView.playbackSpeed);
            }
            PhotoViewerWebView.this.pipItem.setEnabled(true);
            PhotoViewerWebView.this.pipItem.setAlpha(1.0f);
            if (PhotoViewerWebView.this.photoViewer != null) {
                PhotoViewerWebView.this.photoViewer.checkFullscreenButton();
            }
        }

        @JavascriptInterface
        public void onPlayerStateChange(String str) {
            int parseInt = Integer.parseInt(str);
            boolean z = PhotoViewerWebView.this.isPlaying;
            final boolean z2 = false;
            final int i = 1;
            PhotoViewerWebView.this.isPlaying = parseInt == 1 || parseInt == 3;
            PhotoViewerWebView.this.checkPlayingPoll(z);
            if (parseInt != 0) {
                if (parseInt == 1) {
                    z2 = true;
                } else if (parseInt != 2) {
                    if (parseInt == 3) {
                        z2 = true;
                        i = 2;
                    }
                }
                i = 3;
            } else {
                i = 4;
            }
            if (i == 3 && PhotoViewerWebView.this.progressBarBlackBackground.getVisibility() != 4) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.PhotoViewerWebView$YoutubeProxy$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        PhotoViewerWebView.YoutubeProxy.this.lambda$onPlayerStateChange$1();
                    }
                }, 300L);
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.PhotoViewerWebView$YoutubeProxy$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    PhotoViewerWebView.YoutubeProxy.this.lambda$onPlayerStateChange$2(z2, i);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onPlayerStateChange$1() {
            PhotoViewerWebView.this.progressBarBlackBackground.setVisibility(4);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onPlayerStateChange$2(boolean z, int i) {
            PhotoViewerWebView.this.photoViewer.updateWebPlayerState(z, i);
        }

        @JavascriptInterface
        public void onPlayerNotifyDuration(int i) {
            PhotoViewerWebView.this.videoDuration = i * 1000;
            if (PhotoViewerWebView.this.youtubeStoryboardsSpecUrl != null) {
                PhotoViewerWebView photoViewerWebView = PhotoViewerWebView.this;
                photoViewerWebView.processYoutubeStoryboards(photoViewerWebView.youtubeStoryboardsSpecUrl);
                PhotoViewerWebView.this.youtubeStoryboardsSpecUrl = null;
            }
        }

        @JavascriptInterface
        public void onPlayerNotifyCurrentPosition(int i) {
            PhotoViewerWebView.this.currentPosition = i * 1000;
        }

        @JavascriptInterface
        public void onPlayerNotifyBufferedPosition(float f) {
            PhotoViewerWebView.this.bufferedPosition = f;
        }
    }

    @SuppressLint({"SetJavaScriptEnabled"})
    public PhotoViewerWebView(PhotoViewer photoViewer, Context context, View view) {
        super(context);
        this.currentAccount = UserConfig.selectedAccount;
        this.youtubeStoryboards = new ArrayList();
        this.progressRunnable = new Runnable() { // from class: org.telegram.ui.Components.PhotoViewerWebView$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                PhotoViewerWebView.this.lambda$new$0();
            }
        };
        this.photoViewer = photoViewer;
        this.pipItem = view;
        WebView webView = new WebView(context) { // from class: org.telegram.ui.Components.PhotoViewerWebView.1
            @Override // android.webkit.WebView, android.view.View
            public boolean onTouchEvent(MotionEvent motionEvent) {
                PhotoViewerWebView.this.processTouch(motionEvent);
                return super.onTouchEvent(motionEvent);
            }

            @Override // android.view.View
            public void draw(Canvas canvas) {
                super.draw(canvas);
                if (PipVideoOverlay.getInnerView() == this && PhotoViewerWebView.this.progressBarBlackBackground.getVisibility() == 0) {
                    canvas.drawColor(-16777216);
                    PhotoViewerWebView.this.drawBlackBackground(canvas, getWidth(), getHeight());
                }
            }
        };
        this.webView = webView;
        webView.getSettings().setJavaScriptEnabled(true);
        this.webView.getSettings().setDomStorageEnabled(true);
        int i = Build.VERSION.SDK_INT;
        if (i >= 17) {
            this.webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        }
        if (i >= 21) {
            this.webView.getSettings().setMixedContentMode(0);
            CookieManager.getInstance().setAcceptThirdPartyCookies(this.webView, true);
        }
        this.webView.setWebViewClient(new AnonymousClass2());
        addView(this.webView, LayoutHelper.createFrame(-1, -1, 51));
        View view2 = new View(context) { // from class: org.telegram.ui.Components.PhotoViewerWebView.3
            @Override // android.view.View
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                PhotoViewerWebView.this.drawBlackBackground(canvas, getMeasuredWidth(), getMeasuredHeight());
            }
        };
        this.progressBarBlackBackground = view2;
        view2.setBackgroundColor(-16777216);
        this.progressBarBlackBackground.setVisibility(4);
        addView(this.progressBarBlackBackground, LayoutHelper.createFrame(-1, -1.0f));
        RadialProgressView radialProgressView = new RadialProgressView(context);
        this.progressBar = radialProgressView;
        radialProgressView.setVisibility(4);
        addView(this.progressBar, LayoutHelper.createFrame(-2, -2, 17));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.ui.Components.PhotoViewerWebView$2  reason: invalid class name */
    /* loaded from: classes3.dex */
    public class AnonymousClass2 extends WebViewClient {
        AnonymousClass2() {
        }

        @Override // android.webkit.WebViewClient
        public void onPageFinished(WebView webView, String str) {
            super.onPageFinished(webView, str);
            if (!PhotoViewerWebView.this.isYouTube || Build.VERSION.SDK_INT < 17) {
                PhotoViewerWebView.this.progressBar.setVisibility(4);
                PhotoViewerWebView.this.progressBarBlackBackground.setVisibility(4);
                PhotoViewerWebView.this.pipItem.setEnabled(true);
                PhotoViewerWebView.this.pipItem.setAlpha(1.0f);
            }
        }

        @Override // android.webkit.WebViewClient
        public WebResourceResponse shouldInterceptRequest(WebView webView, final WebResourceRequest webResourceRequest) {
            final String uri = webResourceRequest.getUrl().toString();
            if (!PhotoViewerWebView.this.isYouTube || !uri.startsWith("https://www.youtube.com/youtubei/v1/player?key=")) {
                return null;
            }
            Utilities.externalNetworkQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.Components.PhotoViewerWebView$2$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    PhotoViewerWebView.AnonymousClass2.this.lambda$shouldInterceptRequest$0(uri, webResourceRequest);
                }
            });
            return null;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$shouldInterceptRequest$0(String str, WebResourceRequest webResourceRequest) {
            JSONObject optJSONObject;
            String optString;
            try {
                HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(str).openConnection();
                httpURLConnection.setRequestMethod("POST");
                for (Map.Entry<String, String> entry : webResourceRequest.getRequestHeaders().entrySet()) {
                    httpURLConnection.addRequestProperty(entry.getKey(), entry.getValue());
                }
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                JSONObject jSONObject = new JSONObject();
                JSONObject jSONObject2 = new JSONObject();
                JSONObject put = new JSONObject().put("userAgent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36,gzip(gfe)").put("clientName", "WEB").put("clientVersion", webResourceRequest.getRequestHeaders().get("X-Youtube-Client-Version")).put("osName", "Windows").put("osVersion", "10.0");
                outputStream.write(jSONObject.put("context", jSONObject2.put("client", put.put("originalUrl", "https://www.youtube.com/watch?v=" + PhotoViewerWebView.this.currentYoutubeId).put("platform", "DESKTOP"))).put("videoId", PhotoViewerWebView.this.currentYoutubeId).toString().getBytes("UTF-8"));
                outputStream.close();
                InputStream inputStream = httpURLConnection.getResponseCode() == 200 ? httpURLConnection.getInputStream() : httpURLConnection.getErrorStream();
                byte[] bArr = new byte[10240];
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                while (true) {
                    int read = inputStream.read(bArr);
                    if (read == -1) {
                        break;
                    }
                    byteArrayOutputStream.write(bArr, 0, read);
                }
                byteArrayOutputStream.close();
                inputStream.close();
                JSONObject optJSONObject2 = new JSONObject(byteArrayOutputStream.toString("UTF-8")).optJSONObject("storyboards");
                if (optJSONObject2 == null || (optJSONObject = optJSONObject2.optJSONObject("playerStoryboardSpecRenderer")) == null || (optString = optJSONObject.optString("spec")) == null) {
                    return;
                }
                if (PhotoViewerWebView.this.videoDuration == 0) {
                    PhotoViewerWebView.this.youtubeStoryboardsSpecUrl = optString;
                } else {
                    PhotoViewerWebView.this.processYoutubeStoryboards(optString);
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        @Override // android.webkit.WebViewClient
        public boolean shouldOverrideUrlLoading(WebView webView, String str) {
            if (PhotoViewerWebView.this.isYouTube) {
                Browser.openUrl(webView.getContext(), str);
                return true;
            }
            return super.shouldOverrideUrlLoading(webView, str);
        }
    }

    public boolean hasYoutubeStoryboards() {
        return !this.youtubeStoryboards.isEmpty();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void processYoutubeStoryboards(String str) {
        String str2;
        double ceil;
        int videoDuration = getVideoDuration() / 1000;
        this.youtubeStoryboards.clear();
        if (videoDuration <= 15) {
            return;
        }
        String[] split = str.split("\\|");
        String str3 = split[0].split("\\$")[0] + "2/";
        String str4 = split[0].split("\\$N")[1];
        if (split.length == 3) {
            str2 = split[2].split("M#")[1];
        } else if (split.length == 2) {
            str2 = split[1].split("t#")[1];
        } else {
            str2 = split[3].split("M#")[1];
        }
        if (videoDuration <= 100) {
            ceil = Math.ceil(videoDuration / 25.0f);
        } else if (videoDuration <= 250) {
            ceil = Math.ceil((videoDuration / 2.0f) / 25.0f);
        } else if (videoDuration <= 500) {
            ceil = Math.ceil((videoDuration / 4.0f) / 25.0f);
        } else if (videoDuration <= 1000) {
            ceil = Math.ceil((videoDuration / 5.0f) / 25.0f);
        } else {
            ceil = Math.ceil((videoDuration / 10.0f) / 25.0f);
        }
        int i = (int) ceil;
        for (int i2 = 0; i2 < i; i2++) {
            this.youtubeStoryboards.add(String.format(Locale.ROOT, "%sM%d%s&sigh=%s", str3, Integer.valueOf(i2), str4, str2));
        }
    }

    public int getYoutubeStoryboardImageCount(int i) {
        double ceil;
        int indexOf = this.youtubeStoryboards.indexOf(getYoutubeStoryboard(i));
        if (indexOf != -1) {
            if (indexOf != this.youtubeStoryboards.size() - 1) {
                return 25;
            }
            int videoDuration = getVideoDuration() / 1000;
            if (videoDuration <= 100) {
                ceil = Math.ceil(videoDuration);
            } else if (videoDuration <= 250) {
                ceil = Math.ceil(videoDuration / 2.0f);
            } else if (videoDuration <= 500) {
                ceil = Math.ceil(videoDuration / 4.0f);
            } else if (videoDuration <= 1000) {
                ceil = Math.ceil(videoDuration / 5.0f);
            } else {
                ceil = Math.ceil(videoDuration / 10.0f);
            }
            return Math.min(25, (((int) ceil) - ((this.youtubeStoryboards.size() - 1) * 25)) + 1);
        }
        return 0;
    }

    /* JADX WARN: Removed duplicated region for block: B:17:0x0040  */
    /* JADX WARN: Removed duplicated region for block: B:18:0x0049 A[ORIG_RETURN, RETURN] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public java.lang.String getYoutubeStoryboard(int r5) {
        /*
            r4 = this;
            int r0 = r4.getVideoDuration()
            r1 = 1000(0x3e8, float:1.401E-42)
            int r0 = r0 / r1
            r2 = 1103626240(0x41CLASSNAME, float:25.0)
            r3 = 100
            if (r0 > r3) goto L11
            float r5 = (float) r5
        Le:
            float r5 = r5 / r2
            int r5 = (int) r5
            goto L38
        L11:
            r3 = 250(0xfa, float:3.5E-43)
            if (r0 > r3) goto L1d
            float r5 = (float) r5
            r0 = 1073741824(0x40000000, float:2.0)
            float r5 = r5 / r0
            int r5 = (int) r5
            int r5 = r5 / 25
            goto L38
        L1d:
            r3 = 500(0x1f4, float:7.0E-43)
            if (r0 > r3) goto L29
            float r5 = (float) r5
            r0 = 1082130432(0x40800000, float:4.0)
            float r5 = r5 / r0
            int r5 = (int) r5
            int r5 = r5 / 25
            goto L38
        L29:
            if (r0 > r1) goto L33
            float r5 = (float) r5
            r0 = 1084227584(0x40a00000, float:5.0)
            float r5 = r5 / r0
            int r5 = (int) r5
            int r5 = r5 / 25
            goto L38
        L33:
            float r5 = (float) r5
            r0 = 1092616192(0x41200000, float:10.0)
            float r5 = r5 / r0
            goto Le
        L38:
            java.util.List<java.lang.String> r0 = r4.youtubeStoryboards
            int r0 = r0.size()
            if (r5 >= r0) goto L49
            java.util.List<java.lang.String> r0 = r4.youtubeStoryboards
            java.lang.Object r5 = r0.get(r5)
            java.lang.String r5 = (java.lang.String) r5
            goto L4a
        L49:
            r5 = 0
        L4a:
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PhotoViewerWebView.getYoutubeStoryboard(int):java.lang.String");
    }

    public int getYoutubeStoryboardImageIndex(int i) {
        int videoDuration = getVideoDuration() / 1000;
        if (videoDuration <= 100) {
            return ((int) Math.ceil(i)) % 25;
        }
        if (videoDuration <= 250) {
            return ((int) Math.ceil(i / 2.0f)) % 25;
        }
        if (videoDuration <= 500) {
            return ((int) Math.ceil(i / 4.0f)) % 25;
        }
        if (videoDuration <= 1000) {
            return ((int) Math.ceil(i / 5.0f)) % 25;
        }
        return ((int) Math.ceil(i / 10.0f)) % 25;
    }

    public void setTouchDisabled(boolean z) {
        this.isTouchDisabled = z;
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (this.isTouchDisabled) {
            return false;
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    public WebView getWebView() {
        return this.webView;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkPlayingPoll(boolean z) {
        if (!z && this.isPlaying) {
            AndroidUtilities.runOnUIThread(this.progressRunnable, 500L);
        } else if (!z || this.isPlaying) {
        } else {
            AndroidUtilities.cancelRunOnUIThread(this.progressRunnable);
        }
    }

    public void seekTo(long j) {
        seekTo(j, true);
    }

    public void seekTo(final long j, final boolean z) {
        boolean z2 = this.isPlaying;
        this.currentPosition = (int) j;
        if (z2) {
            pauseVideo();
        }
        if (z2) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.PhotoViewerWebView$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    PhotoViewerWebView.this.lambda$seekTo$1(j, z);
                }
            }, 100L);
            return;
        }
        runJsCode("seekTo(" + Math.round(((float) j) / 1000.0f) + ", " + z + ");");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$seekTo$1(long j, boolean z) {
        runJsCode("seekTo(" + Math.round(((float) j) / 1000.0f) + ", " + z + ");");
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.PhotoViewerWebView$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                PhotoViewerWebView.this.playVideo();
            }
        }, 100L);
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
            this.webView.evaluateJavascript(str, null);
            return;
        }
        try {
            WebView webView = this.webView;
            webView.loadUrl("javascript:" + str);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int i, int i2) {
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
            float f = i3;
            float f2 = i4;
            float min = Math.min(size / f, size2 / f2);
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
        if ((isInAppOnly || checkInlinePermissions()) && this.progressBar.getVisibility() != 0) {
            if (PipVideoOverlay.isVisible()) {
                PipVideoOverlay.dismiss();
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.PhotoViewerWebView$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        PhotoViewerWebView.this.openInPip();
                    }
                }, 300L);
                return true;
            }
            this.progressBarBlackBackground.setVisibility(0);
            WebView webView = this.webView;
            TLRPC$WebPage tLRPC$WebPage = this.currentWebpage;
            if (PipVideoOverlay.show(isInAppOnly, (Activity) getContext(), this, webView, tLRPC$WebPage.embed_width, tLRPC$WebPage.embed_height, false)) {
                PipVideoOverlay.setPhotoViewer(PhotoViewer.getInstance());
            }
            return true;
        }
        return false;
    }

    public boolean isYouTube() {
        return this.isYouTube;
    }

    public boolean isControllable() {
        return isYouTube();
    }

    public boolean isPlaying() {
        return this.isPlaying;
    }

    public void playVideo() {
        if (this.isPlaying || !isControllable()) {
            return;
        }
        runJsCode("playVideo();");
        this.isPlaying = true;
        checkPlayingPoll(false);
    }

    public void pauseVideo() {
        if (!this.isPlaying || !isControllable()) {
            return;
        }
        runJsCode("pauseVideo();");
        this.isPlaying = false;
        checkPlayingPoll(true);
    }

    public void setPlaybackSpeed(float f) {
        this.playbackSpeed = f;
        if (this.progressBar.getVisibility() != 0) {
            if (!this.isYouTube) {
                return;
            }
            runJsCode("setPlaybackSpeed(" + f + ");");
            return;
        }
        this.setPlaybackSpeed = true;
    }

    /* JADX WARN: Removed duplicated region for block: B:28:0x00aa A[Catch: Exception -> 0x00ef, LOOP:0: B:26:0x00a3->B:28:0x00aa, LOOP_END, TryCatch #0 {Exception -> 0x00ef, blocks: (B:3:0x0013, B:5:0x0017, B:7:0x0025, B:25:0x008c, B:26:0x00a3, B:28:0x00aa, B:29:0x00ae, B:23:0x0088, B:30:0x00db, B:9:0x0033, B:11:0x0039, B:13:0x004c, B:15:0x0054, B:17:0x005c, B:19:0x0062, B:20:0x007e), top: B:43:0x0013, inners: #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:45:0x00ae A[EDGE_INSN: B:45:0x00ae->B:29:0x00ae ?: BREAK  , SYNTHETIC] */
    @android.annotation.SuppressLint({"AddJavascriptInterface"})
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void init(int r11, org.telegram.tgnet.TLRPC$WebPage r12) {
        /*
            Method dump skipped, instructions count: 307
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PhotoViewerWebView.init(int, org.telegram.tgnet.TLRPC$WebPage):void");
    }

    public boolean checkInlinePermissions() {
        if (Build.VERSION.SDK_INT < 23 || Settings.canDrawOverlays(getContext())) {
            return true;
        }
        AlertsCreator.createDrawOverlayPermissionDialog((Activity) getContext(), null);
        return false;
    }

    public void exitFromPip() {
        if (this.webView == null) {
            return;
        }
        if (ApplicationLoader.mainInterfacePaused) {
            try {
                getContext().startService(new Intent(ApplicationLoader.applicationContext, BringAppForegroundService.class));
            } catch (Throwable th) {
                FileLog.e(th);
            }
        }
        this.progressBarBlackBackground.setVisibility(0);
        ViewGroup viewGroup = (ViewGroup) this.webView.getParent();
        if (viewGroup != null) {
            viewGroup.removeView(this.webView);
        }
        addView(this.webView, 0, LayoutHelper.createFrame(-1, -1, 51));
        PipVideoOverlay.dismiss();
    }

    public void release() {
        this.webView.stopLoading();
        this.webView.loadUrl("about:blank");
        this.webView.destroy();
        this.videoDuration = 0;
        this.currentPosition = 0;
        AndroidUtilities.cancelRunOnUIThread(this.progressRunnable);
    }
}
