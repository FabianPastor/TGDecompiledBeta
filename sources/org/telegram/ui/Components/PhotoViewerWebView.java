package org.telegram.ui.Components;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import java.util.HashMap;
import java.util.Locale;
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

public class PhotoViewerWebView extends FrameLayout {
    private int currentAccount = UserConfig.selectedAccount;
    private TLRPC$WebPage currentWebpage;
    /* access modifiers changed from: private */
    public View customView;
    /* access modifiers changed from: private */
    public WebChromeClient.CustomViewCallback customViewCallback;
    /* access modifiers changed from: private */
    public FrameLayout fullscreenVideoContainer;
    /* access modifiers changed from: private */
    public boolean isYouTube;
    /* access modifiers changed from: private */
    public View pipItem;
    /* access modifiers changed from: private */
    public PipVideoView pipVideoView;
    /* access modifiers changed from: private */
    public float playbackSpeed;
    /* access modifiers changed from: private */
    public RadialProgressView progressBar;
    /* access modifiers changed from: private */
    public View progressBarBlackBackground;
    /* access modifiers changed from: private */
    public boolean setPlaybackSpeed;
    private WebView webView;

    /* access modifiers changed from: protected */
    public void drawBlackBackground(Canvas canvas, int i, int i2) {
    }

    private class YoutubeProxy {
        private YoutubeProxy() {
        }

        @JavascriptInterface
        public void postEvent(String str, String str2) {
            if ("loaded".equals(str)) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public final void run() {
                        PhotoViewerWebView.YoutubeProxy.this.lambda$postEvent$0$PhotoViewerWebView$YoutubeProxy();
                    }
                });
            }
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$postEvent$0 */
        public /* synthetic */ void lambda$postEvent$0$PhotoViewerWebView$YoutubeProxy() {
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
    }

    @SuppressLint({"SetJavaScriptEnabled"})
    public PhotoViewerWebView(Context context, View view) {
        super(context);
        this.pipItem = view;
        AnonymousClass1 r11 = new WebView(context) {
            public boolean onTouchEvent(MotionEvent motionEvent) {
                boolean onTouchEvent = super.onTouchEvent(motionEvent);
                if (onTouchEvent) {
                    motionEvent.getAction();
                }
                return onTouchEvent;
            }
        };
        this.webView = r11;
        r11.getSettings().setJavaScriptEnabled(true);
        this.webView.getSettings().setDomStorageEnabled(true);
        int i = Build.VERSION.SDK_INT;
        if (i >= 17) {
            this.webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        }
        if (i >= 21) {
            this.webView.getSettings().setMixedContentMode(0);
            CookieManager.getInstance().setAcceptThirdPartyCookies(this.webView, true);
        }
        this.webView.setWebChromeClient(new WebChromeClient() {
            public void onShowCustomView(View view, int i, WebChromeClient.CustomViewCallback customViewCallback) {
                onShowCustomView(view, customViewCallback);
            }

            public void onShowCustomView(View view, WebChromeClient.CustomViewCallback customViewCallback) {
                if (PhotoViewerWebView.this.customView == null && PhotoViewerWebView.this.pipVideoView == null) {
                    PhotoViewerWebView.this.exitFromPip();
                    View unused = PhotoViewerWebView.this.customView = view;
                    PhotoViewerWebView.this.fullscreenVideoContainer.setVisibility(0);
                    PhotoViewerWebView.this.fullscreenVideoContainer.addView(view, LayoutHelper.createFrame(-1, -1.0f));
                    WebChromeClient.CustomViewCallback unused2 = PhotoViewerWebView.this.customViewCallback = customViewCallback;
                    return;
                }
                customViewCallback.onCustomViewHidden();
            }

            public void onHideCustomView() {
                super.onHideCustomView();
                if (PhotoViewerWebView.this.customView != null) {
                    PhotoViewerWebView.this.fullscreenVideoContainer.setVisibility(4);
                    PhotoViewerWebView.this.fullscreenVideoContainer.removeView(PhotoViewerWebView.this.customView);
                    if (PhotoViewerWebView.this.customViewCallback != null && !PhotoViewerWebView.this.customViewCallback.getClass().getName().contains(".chromium.")) {
                        PhotoViewerWebView.this.customViewCallback.onCustomViewHidden();
                    }
                    View unused = PhotoViewerWebView.this.customView = null;
                }
            }
        });
        this.webView.setWebViewClient(new WebViewClient() {
            public void onLoadResource(WebView webView, String str) {
                super.onLoadResource(webView, str);
            }

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
        AnonymousClass4 r1 = new View(context) {
            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                PhotoViewerWebView.this.drawBlackBackground(canvas, getMeasuredWidth(), getMeasuredHeight());
            }
        };
        this.progressBarBlackBackground = r1;
        r1.setBackgroundColor(-16777216);
        this.progressBarBlackBackground.setVisibility(4);
        addView(this.progressBarBlackBackground, LayoutHelper.createFrame(-1, -1.0f));
        RadialProgressView radialProgressView = new RadialProgressView(context);
        this.progressBar = radialProgressView;
        radialProgressView.setVisibility(4);
        addView(this.progressBar, LayoutHelper.createFrame(-2, -2, 17));
        FrameLayout frameLayout = new FrameLayout(context);
        this.fullscreenVideoContainer = frameLayout;
        frameLayout.setKeepScreenOn(true);
        this.fullscreenVideoContainer.setBackgroundColor(-16777216);
        if (i >= 21) {
            this.fullscreenVideoContainer.setFitsSystemWindows(true);
        }
        addView(this.fullscreenVideoContainer, LayoutHelper.createFrame(-1, -1.0f));
        this.fullscreenVideoContainer.setVisibility(4);
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

    public PipVideoView openInPip() {
        int i;
        boolean z = this.isYouTube && "inapp".equals(MessagesController.getInstance(this.currentAccount).youtubePipType);
        if ((!z && !checkInlinePermissions()) || this.progressBar.getVisibility() == 0) {
            return null;
        }
        PipVideoView pipVideoView2 = new PipVideoView(z);
        this.pipVideoView = pipVideoView2;
        Activity activity = (Activity) getContext();
        PhotoViewer instance = PhotoViewer.getInstance();
        TLRPC$WebPage tLRPC$WebPage = this.currentWebpage;
        int i2 = tLRPC$WebPage.embed_width;
        pipVideoView2.show(activity, instance, (i2 == 0 || (i = tLRPC$WebPage.embed_height) == 0) ? 1.0f : ((float) i2) / ((float) i), 0, this.webView);
        if (this.isYouTube) {
            runJsCode("hideControls();");
        }
        return this.pipVideoView;
    }

    public void setPlaybackSpeed(float f) {
        this.playbackSpeed = f;
        if (this.progressBar.getVisibility() == 0) {
            this.setPlaybackSpeed = true;
        } else if (this.isYouTube) {
            runJsCode("setPlaybackSpeed(" + f + ");");
        }
    }

    @SuppressLint({"AddJavascriptInterface"})
    public void init(int i, TLRPC$WebPage tLRPC$WebPage) {
        int i2;
        this.currentWebpage = tLRPC$WebPage;
        String youTubeVideoId = WebPlayerView.getYouTubeVideoId(tLRPC$WebPage.embed_url);
        String str = tLRPC$WebPage.url;
        requestLayout();
        if (youTubeVideoId != null) {
            try {
                this.progressBarBlackBackground.setVisibility(0);
                this.isYouTube = true;
                String str2 = null;
                if (Build.VERSION.SDK_INT >= 17) {
                    this.webView.addJavascriptInterface(new YoutubeProxy(), "YoutubeProxy");
                }
                if (str != null) {
                    try {
                        Uri parse = Uri.parse(str);
                        if (i > 0) {
                            str2 = "" + i;
                        }
                        if (str2 == null && (str2 = parse.getQueryParameter("t")) == null) {
                            str2 = parse.getQueryParameter("time_continue");
                        }
                        if (str2 != null) {
                            if (str2.contains("m")) {
                                String[] split = str2.split("m");
                                i2 = (Utilities.parseInt(split[0]).intValue() * 60) + Utilities.parseInt(split[1]).intValue();
                            } else {
                                i2 = Utilities.parseInt(str2).intValue();
                            }
                            this.webView.loadDataWithBaseURL("https://messenger.telegram.org/", String.format(Locale.US, "<!DOCTYPE html><html><head><style>body { margin: 0; width:100%%; height:100%%;  background-color:#000; }html { width:100%%; height:100%%; background-color:#000; }.embed-container iframe,.embed-container object,   .embed-container embed {       position: absolute;       top: 0;       left: 0;       width: 100%% !important;       height: 100%% !important;   }   </style></head><body>   <div class=\"embed-container\">       <div id=\"player\"></div>   </div>   <script src=\"https://www.youtube.com/iframe_api\"></script>   <script>   var player;   var observer;   var videoEl;   var playing;   var posted = false;   YT.ready(function() {       player = new YT.Player(\"player\", {                              \"width\" : \"100%%\",                              \"events\" : {                              \"onReady\" : \"onReady\",                              \"onError\" : \"onError\",                              \"onStateChange\" : \"onStateChange\",                              },                              \"videoId\" : \"%1$s\",                              \"height\" : \"100%%\",                              \"playerVars\" : {                              \"start\" : %2$d,                              \"rel\" : 1,                              \"showinfo\" : 0,                              \"modestbranding\" : 0,                              \"iv_load_policy\" : 3,                              \"autohide\" : 1,                              \"autoplay\" : 1,                              \"cc_load_policy\" : 1,                              \"playsinline\" : 1,                              \"controls\" : 1                              }                            });        player.setSize(window.innerWidth, window.innerHeight);    });    function setPlaybackSpeed(speed) {        player.setPlaybackRate(speed);    }    function hideControls() {        playing = !videoEl.paused;       videoEl.controls = 0;       observer.observe(videoEl, {attributes: true});    }    function showControls() {        playing = !videoEl.paused;       observer.disconnect();       videoEl.controls = 1;    }    function onError(event) {       if (!posted) {            if (window.YoutubeProxy !== undefined) {                   YoutubeProxy.postEvent(\"loaded\", null);             }            posted = true;       }    }    function onStateChange(event) {       if (event.data == YT.PlayerState.PLAYING && !posted) {            if (window.YoutubeProxy !== undefined) {                   YoutubeProxy.postEvent(\"loaded\", null);             }            posted = true;       }    }    function onReady(event) {       player.playVideo();    }    window.onresize = function() {       player.setSize(window.innerWidth, window.innerHeight);       player.playVideo();    }    </script></body></html>", new Object[]{youTubeVideoId, Integer.valueOf(i2)}), "text/html", "UTF-8", "https://youtube.com");
                        }
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                }
                i2 = 0;
                this.webView.loadDataWithBaseURL("https://messenger.telegram.org/", String.format(Locale.US, "<!DOCTYPE html><html><head><style>body { margin: 0; width:100%%; height:100%%;  background-color:#000; }html { width:100%%; height:100%%; background-color:#000; }.embed-container iframe,.embed-container object,   .embed-container embed {       position: absolute;       top: 0;       left: 0;       width: 100%% !important;       height: 100%% !important;   }   </style></head><body>   <div class=\"embed-container\">       <div id=\"player\"></div>   </div>   <script src=\"https://www.youtube.com/iframe_api\"></script>   <script>   var player;   var observer;   var videoEl;   var playing;   var posted = false;   YT.ready(function() {       player = new YT.Player(\"player\", {                              \"width\" : \"100%%\",                              \"events\" : {                              \"onReady\" : \"onReady\",                              \"onError\" : \"onError\",                              \"onStateChange\" : \"onStateChange\",                              },                              \"videoId\" : \"%1$s\",                              \"height\" : \"100%%\",                              \"playerVars\" : {                              \"start\" : %2$d,                              \"rel\" : 1,                              \"showinfo\" : 0,                              \"modestbranding\" : 0,                              \"iv_load_policy\" : 3,                              \"autohide\" : 1,                              \"autoplay\" : 1,                              \"cc_load_policy\" : 1,                              \"playsinline\" : 1,                              \"controls\" : 1                              }                            });        player.setSize(window.innerWidth, window.innerHeight);    });    function setPlaybackSpeed(speed) {        player.setPlaybackRate(speed);    }    function hideControls() {        playing = !videoEl.paused;       videoEl.controls = 0;       observer.observe(videoEl, {attributes: true});    }    function showControls() {        playing = !videoEl.paused;       observer.disconnect();       videoEl.controls = 1;    }    function onError(event) {       if (!posted) {            if (window.YoutubeProxy !== undefined) {                   YoutubeProxy.postEvent(\"loaded\", null);             }            posted = true;       }    }    function onStateChange(event) {       if (event.data == YT.PlayerState.PLAYING && !posted) {            if (window.YoutubeProxy !== undefined) {                   YoutubeProxy.postEvent(\"loaded\", null);             }            posted = true;       }    }    function onReady(event) {       player.playVideo();    }    window.onresize = function() {       player.setSize(window.innerWidth, window.innerHeight);       player.playVideo();    }    </script></body></html>", new Object[]{youTubeVideoId, Integer.valueOf(i2)}), "text/html", "UTF-8", "https://youtube.com");
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
        } else {
            HashMap hashMap = new HashMap();
            hashMap.put("Referer", "messenger.telegram.org");
            this.webView.loadUrl(tLRPC$WebPage.embed_url, hashMap);
        }
        this.pipItem.setEnabled(false);
        this.pipItem.setAlpha(0.5f);
        this.progressBar.setVisibility(0);
        if (youTubeVideoId != null) {
            this.progressBarBlackBackground.setVisibility(0);
        }
        this.webView.setVisibility(0);
        this.webView.setKeepScreenOn(true);
        if (youTubeVideoId != null && "disabled".equals(MessagesController.getInstance(this.currentAccount).youtubePipType)) {
            this.pipItem.setVisibility(8);
        }
    }

    public boolean checkInlinePermissions() {
        if (Build.VERSION.SDK_INT < 23 || Settings.canDrawOverlays(getContext())) {
            return true;
        }
        AlertsCreator.createDrawOverlayPermissionDialog((Activity) getContext(), (DialogInterface.OnClickListener) null);
        return false;
    }

    public void exitFromPip() {
        if (this.webView != null && this.pipVideoView != null) {
            if (ApplicationLoader.mainInterfacePaused) {
                try {
                    getContext().startService(new Intent(ApplicationLoader.applicationContext, BringAppForegroundService.class));
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
            addView(this.webView, 0, LayoutHelper.createFrame(-1, -1, 51));
            this.pipVideoView.close();
            this.pipVideoView = null;
        }
    }

    public void release() {
        this.webView.stopLoading();
        this.webView.loadUrl("about:blank");
        this.webView.destroy();
    }
}
