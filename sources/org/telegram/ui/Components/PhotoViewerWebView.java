package org.telegram.ui.Components;

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
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.PhotoViewer;

public class PhotoViewerWebView extends FrameLayout {
    private static final String youtubeFrame = "<!DOCTYPE html><html><head><style>body { margin: 0; width:100%%; height:100%%;  background-color:#000; }html { width:100%%; height:100%%; background-color:#000; }.embed-container iframe,.embed-container object,   .embed-container embed {       position: absolute;       top: 0;       left: 0;       width: 100%% !important;       height: 100%% !important;   }   </style></head><body>   <div class=\"embed-container\">       <div id=\"player\"></div>   </div>   <script src=\"https://www.youtube.com/iframe_api\"></script>   <script>   var player;   var posted = false;   YT.ready(function() {       player = new YT.Player(\"player\", {                              \"width\" : \"100%%\",                              \"events\" : {                              \"onReady\" : \"onReady\",                              \"onError\" : \"onError\",                              \"onStateChange\" : \"onStateChange\",                              },                              \"videoId\" : \"%1$s\",                              \"height\" : \"100%%\",                              \"playerVars\" : {                              \"start\" : %2$d,                              \"rel\" : 1,                              \"showinfo\" : 0,                              \"modestbranding\" : 0,                              \"iv_load_policy\" : 3,                              \"autohide\" : 1,                              \"autoplay\" : 1,                              \"cc_load_policy\" : 1,                              \"playsinline\" : 1,                              \"controls\" : 1                              }                            });        player.setSize(window.innerWidth, window.innerHeight);    });    function setPlaybackSpeed(speed) {        player.setPlaybackRate(speed);    }    function onError(event) {       if (!posted) {            if (window.YoutubeProxy !== undefined) {                   YoutubeProxy.postEvent(\"loaded\", null);             }            posted = true;       }    }    function onStateChange(event) {       if (event.data == YT.PlayerState.PLAYING && !posted) {            if (window.YoutubeProxy !== undefined) {                   YoutubeProxy.postEvent(\"loaded\", null);             }            posted = true;       }    }    function onReady(event) {       player.playVideo();    }    window.onresize = function() {       player.setSize(window.innerWidth, window.innerHeight);       player.playVideo();    }    </script></body></html>";
    private int currentAccount = UserConfig.selectedAccount;
    private TLRPC.WebPage currentWebpage;
    /* access modifiers changed from: private */
    public boolean isYouTube;
    /* access modifiers changed from: private */
    public View pipItem;
    /* access modifiers changed from: private */
    public float playbackSpeed;
    /* access modifiers changed from: private */
    public RadialProgressView progressBar;
    /* access modifiers changed from: private */
    public View progressBarBlackBackground;
    /* access modifiers changed from: private */
    public boolean setPlaybackSpeed;
    private WebView webView;

    private class YoutubeProxy {
        private YoutubeProxy() {
        }

        @JavascriptInterface
        public void postEvent(String eventName, String eventData) {
            if ("loaded".equals(eventName)) {
                AndroidUtilities.runOnUIThread(new PhotoViewerWebView$YoutubeProxy$$ExternalSyntheticLambda0(this));
            }
        }

        /* renamed from: lambda$postEvent$0$org-telegram-ui-Components-PhotoViewerWebView$YoutubeProxy  reason: not valid java name */
        public /* synthetic */ void m1208x81e88var_() {
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

    public PhotoViewerWebView(Context context, View pip) {
        super(context);
        this.pipItem = pip;
        AnonymousClass1 r0 = new WebView(context) {
            public boolean onTouchEvent(MotionEvent event) {
                PhotoViewerWebView.this.processTouch(event);
                return super.onTouchEvent(event);
            }
        };
        this.webView = r0;
        r0.getSettings().setJavaScriptEnabled(true);
        this.webView.getSettings().setDomStorageEnabled(true);
        if (Build.VERSION.SDK_INT >= 17) {
            this.webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            this.webView.getSettings().setMixedContentMode(0);
            CookieManager.getInstance().setAcceptThirdPartyCookies(this.webView, true);
        }
        this.webView.setWebViewClient(new WebViewClient() {
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
            }

            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (!PhotoViewerWebView.this.isYouTube || Build.VERSION.SDK_INT < 17) {
                    PhotoViewerWebView.this.progressBar.setVisibility(4);
                    PhotoViewerWebView.this.progressBarBlackBackground.setVisibility(4);
                    PhotoViewerWebView.this.pipItem.setEnabled(true);
                    PhotoViewerWebView.this.pipItem.setAlpha(1.0f);
                }
            }

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!PhotoViewerWebView.this.isYouTube) {
                    return super.shouldOverrideUrlLoading(view, url);
                }
                Browser.openUrl(view.getContext(), url);
                return true;
            }
        });
        addView(this.webView, LayoutHelper.createFrame(-1, -1, 51));
        AnonymousClass3 r02 = new View(context) {
            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                PhotoViewerWebView.this.drawBlackBackground(canvas, getMeasuredWidth(), getMeasuredHeight());
            }
        };
        this.progressBarBlackBackground = r02;
        r02.setBackgroundColor(-16777216);
        this.progressBarBlackBackground.setVisibility(4);
        addView(this.progressBarBlackBackground, LayoutHelper.createFrame(-1, -1.0f));
        RadialProgressView radialProgressView = new RadialProgressView(context);
        this.progressBar = radialProgressView;
        radialProgressView.setVisibility(4);
        addView(this.progressBar, LayoutHelper.createFrame(-2, -2, 17));
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

    /* access modifiers changed from: protected */
    public void processTouch(MotionEvent event) {
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.webView.getParent() == this) {
            int h = 100;
            int w = this.currentWebpage.embed_width != 0 ? this.currentWebpage.embed_width : 100;
            if (this.currentWebpage.embed_height != 0) {
                h = this.currentWebpage.embed_height;
            }
            int viewWidth = View.MeasureSpec.getSize(widthMeasureSpec);
            int viewHeight = View.MeasureSpec.getSize(heightMeasureSpec);
            float minScale = Math.min(((float) viewWidth) / ((float) w), ((float) viewHeight) / ((float) h));
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.webView.getLayoutParams();
            layoutParams.width = (int) (((float) w) * minScale);
            layoutParams.height = (int) (((float) h) * minScale);
            layoutParams.topMargin = (viewHeight - layoutParams.height) / 2;
            layoutParams.leftMargin = (viewWidth - layoutParams.width) / 2;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /* access modifiers changed from: protected */
    public void drawBlackBackground(Canvas canvas, int w, int h) {
    }

    public boolean isLoaded() {
        return this.progressBar.getVisibility() != 0;
    }

    public boolean isInAppOnly() {
        return this.isYouTube && "inapp".equals(MessagesController.getInstance(this.currentAccount).youtubePipType);
    }

    public boolean openInPip() {
        boolean inAppOnly = isInAppOnly();
        if ((!inAppOnly && !checkInlinePermissions()) || this.progressBar.getVisibility() == 0) {
            return false;
        }
        if (PipVideoOverlay.isVisible()) {
            PipVideoOverlay.dismiss();
            AndroidUtilities.runOnUIThread(new PhotoViewerWebView$$ExternalSyntheticLambda0(this), 300);
            return true;
        }
        if (PipVideoOverlay.show(inAppOnly, (Activity) getContext(), this.webView, this.currentWebpage.embed_width, this.currentWebpage.embed_height)) {
            PipVideoOverlay.setPhotoViewer(PhotoViewer.getInstance());
        }
        return true;
    }

    public void setPlaybackSpeed(float speed) {
        this.playbackSpeed = speed;
        if (this.progressBar.getVisibility() == 0) {
            this.setPlaybackSpeed = true;
        } else if (this.isYouTube) {
            runJsCode("setPlaybackSpeed(" + speed + ");");
        }
    }

    public void init(int seekTime, TLRPC.WebPage webPage) {
        this.currentWebpage = webPage;
        String currentYoutubeId = WebPlayerView.getYouTubeVideoId(webPage.embed_url);
        String originalUrl = webPage.url;
        requestLayout();
        if (currentYoutubeId != null) {
            try {
                this.progressBarBlackBackground.setVisibility(0);
                this.isYouTube = true;
                String t = null;
                if (Build.VERSION.SDK_INT >= 17) {
                    this.webView.addJavascriptInterface(new YoutubeProxy(), "YoutubeProxy");
                }
                int seekToTime = 0;
                if (originalUrl != null) {
                    try {
                        Uri uri = Uri.parse(originalUrl);
                        if (seekTime > 0) {
                            t = "" + seekTime;
                        }
                        if (t == null && (t = uri.getQueryParameter("t")) == null) {
                            t = uri.getQueryParameter("time_continue");
                        }
                        if (t != null) {
                            if (t.contains("m")) {
                                String[] arg = t.split("m");
                                seekToTime = (Utilities.parseInt((CharSequence) arg[0]).intValue() * 60) + Utilities.parseInt((CharSequence) arg[1]).intValue();
                            } else {
                                seekToTime = Utilities.parseInt((CharSequence) t).intValue();
                            }
                        }
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                }
                this.webView.loadDataWithBaseURL("https://messenger.telegram.org/", String.format(Locale.US, "<!DOCTYPE html><html><head><style>body { margin: 0; width:100%%; height:100%%;  background-color:#000; }html { width:100%%; height:100%%; background-color:#000; }.embed-container iframe,.embed-container object,   .embed-container embed {       position: absolute;       top: 0;       left: 0;       width: 100%% !important;       height: 100%% !important;   }   </style></head><body>   <div class=\"embed-container\">       <div id=\"player\"></div>   </div>   <script src=\"https://www.youtube.com/iframe_api\"></script>   <script>   var player;   var posted = false;   YT.ready(function() {       player = new YT.Player(\"player\", {                              \"width\" : \"100%%\",                              \"events\" : {                              \"onReady\" : \"onReady\",                              \"onError\" : \"onError\",                              \"onStateChange\" : \"onStateChange\",                              },                              \"videoId\" : \"%1$s\",                              \"height\" : \"100%%\",                              \"playerVars\" : {                              \"start\" : %2$d,                              \"rel\" : 1,                              \"showinfo\" : 0,                              \"modestbranding\" : 0,                              \"iv_load_policy\" : 3,                              \"autohide\" : 1,                              \"autoplay\" : 1,                              \"cc_load_policy\" : 1,                              \"playsinline\" : 1,                              \"controls\" : 1                              }                            });        player.setSize(window.innerWidth, window.innerHeight);    });    function setPlaybackSpeed(speed) {        player.setPlaybackRate(speed);    }    function onError(event) {       if (!posted) {            if (window.YoutubeProxy !== undefined) {                   YoutubeProxy.postEvent(\"loaded\", null);             }            posted = true;       }    }    function onStateChange(event) {       if (event.data == YT.PlayerState.PLAYING && !posted) {            if (window.YoutubeProxy !== undefined) {                   YoutubeProxy.postEvent(\"loaded\", null);             }            posted = true;       }    }    function onReady(event) {       player.playVideo();    }    window.onresize = function() {       player.setSize(window.innerWidth, window.innerHeight);       player.playVideo();    }    </script></body></html>", new Object[]{currentYoutubeId, Integer.valueOf(seekToTime)}), "text/html", "UTF-8", "https://youtube.com");
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
        } else {
            HashMap<String, String> args = new HashMap<>();
            args.put("Referer", "messenger.telegram.org");
            this.webView.loadUrl(webPage.embed_url, args);
        }
        this.pipItem.setEnabled(false);
        this.pipItem.setAlpha(0.5f);
        this.progressBar.setVisibility(0);
        if (currentYoutubeId != null) {
            this.progressBarBlackBackground.setVisibility(0);
        }
        this.webView.setVisibility(0);
        this.webView.setKeepScreenOn(true);
        if (currentYoutubeId != null && "disabled".equals(MessagesController.getInstance(this.currentAccount).youtubePipType)) {
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
        if (this.webView != null) {
            if (ApplicationLoader.mainInterfacePaused) {
                try {
                    getContext().startService(new Intent(ApplicationLoader.applicationContext, BringAppForegroundService.class));
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            }
            ViewGroup parent = (ViewGroup) this.webView.getParent();
            if (parent != null) {
                parent.removeView(this.webView);
            }
            addView(this.webView, 0, LayoutHelper.createFrame(-1, -1, 51));
            PipVideoOverlay.dismiss();
        }
    }

    public void release() {
        this.webView.stopLoading();
        this.webView.loadUrl("about:blank");
        this.webView.destroy();
    }
}
