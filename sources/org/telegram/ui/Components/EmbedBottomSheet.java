package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.os.Build.VERSION;
import android.provider.Settings;
import android.text.TextUtils.TruncateAt;
import android.view.OrientationEventListener;
import android.view.TextureView;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.HashMap;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BringAppForegroundService;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.BottomSheet.BottomSheetDelegate;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.WebPlayerView.WebPlayerViewDelegate;

public class EmbedBottomSheet extends BottomSheet {
    @SuppressLint({"StaticFieldLeak"})
    private static EmbedBottomSheet instance;
    private boolean animationInProgress;
    private FrameLayout containerLayout;
    private TextView copyTextButton;
    private View customView;
    private CustomViewCallback customViewCallback;
    private String embedUrl;
    private FrameLayout fullscreenVideoContainer;
    private boolean fullscreenedByButton;
    private boolean hasDescription;
    private int height;
    private LinearLayout imageButtonsContainer;
    private boolean isYouTube;
    private int lastOrientation = -1;
    private OnShowListener onShowListener = new OnShowListener() {
        public void onShow(DialogInterface dialogInterface) {
            if (EmbedBottomSheet.this.pipVideoView != null && EmbedBottomSheet.this.videoView.isInline()) {
                EmbedBottomSheet.this.videoView.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                    public boolean onPreDraw() {
                        EmbedBottomSheet.this.videoView.getViewTreeObserver().removeOnPreDrawListener(this);
                        return true;
                    }
                });
            }
        }
    };
    private String openUrl;
    private OrientationEventListener orientationEventListener;
    private Activity parentActivity;
    private ImageView pipButton;
    private PipVideoView pipVideoView;
    private int[] position = new int[2];
    private int prevOrientation = -2;
    private RadialProgressView progressBar;
    private View progressBarBlackBackground;
    private int seekTimeOverride;
    private WebPlayerView videoView;
    private int waitingForDraw;
    private boolean wasInLandscape;
    private WebView webView;
    private int width;
    private final String youtubeFrame = "<!DOCTYPE html><html><head><style>body { margin: 0; width:100%%; height:100%%;  background-color:#000; }html { width:100%%; height:100%%; background-color:#000; }.embed-container iframe,.embed-container object,   .embed-container embed {       position: absolute;       top: 0;       left: 0;       width: 100%% !important;       height: 100%% !important;   }   </style></head><body>   <div class=\"embed-container\">       <div id=\"player\"></div>   </div>   <script src=\"https://www.youtube.com/iframe_api\"></script>   <script>   var player;   var observer;   var videoEl;   var playing;   var posted = false;   YT.ready(function() {       player = new YT.Player(\"player\", {                              \"width\" : \"100%%\",                              \"events\" : {                              \"onReady\" : \"onReady\",                              \"onError\" : \"onError\",                              },                              \"videoId\" : \"%1$s\",                              \"height\" : \"100%%\",                              \"playerVars\" : {                              \"start\" : %2$d,                              \"rel\" : 0,                              \"showinfo\" : 0,                              \"modestbranding\" : 1,                              \"iv_load_policy\" : 3,                              \"autohide\" : 1,                              \"autoplay\" : 1,                              \"cc_load_policy\" : 1,                              \"playsinline\" : 1,                              \"controls\" : 1                              }                            });        player.setSize(window.innerWidth, window.innerHeight);    });    function hideControls() {        playing = !videoEl.paused;       videoEl.controls = 0;       observer.observe(videoEl, {attributes: true});    }    function showControls() {        playing = !videoEl.paused;       observer.disconnect();       videoEl.controls = 1;    }    function onError(event) {       if (!posted) {            if (window.YoutubeProxy !== undefined) {                   YoutubeProxy.postEvent(\"loaded\", null);             }            posted = true;       }    }    function onReady(event) {       player.playVideo();       videoEl = player.getIframe().contentDocument.getElementsByTagName('video')[0];\n       videoEl.addEventListener(\"canplay\", function() {            if (playing) {               videoEl.play();            }       }, true);       videoEl.addEventListener(\"timeupdate\", function() {            if (!posted && videoEl.currentTime > 0) {               if (window.YoutubeProxy !== undefined) {                   YoutubeProxy.postEvent(\"loaded\", null);                }               posted = true;           }       }, true);       observer = new MutationObserver(function() {\n          if (videoEl.controls) {\n               videoEl.controls = 0;\n          }       });\n    }    window.onresize = function() {        player.setSize(window.innerWidth, window.innerHeight);    }    </script></body></html>";

    private class YoutubeProxy {
        private YoutubeProxy() {
        }

        /* synthetic */ YoutubeProxy(EmbedBottomSheet embedBottomSheet, AnonymousClass1 anonymousClass1) {
            this();
        }

        @JavascriptInterface
        public void postEvent(String str, String str2) {
            if ("loaded".equals(str)) {
                AndroidUtilities.runOnUIThread(new -$$Lambda$EmbedBottomSheet$YoutubeProxy$scxxFmJ3K1Pa5Qhai5Pgt8kfyvA(this));
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

    @SuppressLint({"SetJavaScriptEnabled"})
    private EmbedBottomSheet(Context context, String str, String str2, String str3, String str4, int i, int i2, int i3) {
        Context context2 = context;
        String str5 = str2;
        super(context2, false, 0);
        setApplyTopPadding(false);
        setApplyBottomPadding(false);
        this.seekTimeOverride = i3;
        if (context2 instanceof Activity) {
            this.parentActivity = (Activity) context2;
        }
        this.embedUrl = str4;
        boolean z = str5 != null && str2.length() > 0;
        this.hasDescription = z;
        this.openUrl = str3;
        this.width = i;
        this.height = i2;
        if (this.width == 0 || this.height == 0) {
            Point point = AndroidUtilities.displaySize;
            this.width = point.x;
            this.height = point.y / 2;
        }
        this.fullscreenVideoContainer = new FrameLayout(context2);
        this.fullscreenVideoContainer.setKeepScreenOn(true);
        this.fullscreenVideoContainer.setBackgroundColor(-16777216);
        if (VERSION.SDK_INT >= 21) {
            this.fullscreenVideoContainer.setFitsSystemWindows(true);
        }
        this.fullscreenVideoContainer.setOnTouchListener(-$$Lambda$EmbedBottomSheet$awgbBJJ9KT-HOZj5dBev_ioRvPo.INSTANCE);
        this.container.addView(this.fullscreenVideoContainer, LayoutHelper.createFrame(-1, -1.0f));
        this.fullscreenVideoContainer.setVisibility(4);
        this.fullscreenVideoContainer.setOnTouchListener(-$$Lambda$EmbedBottomSheet$wxc0LCDO5uuH8qs6VYdAXQWEEZE.INSTANCE);
        this.containerLayout = new FrameLayout(context2) {
            /* Access modifiers changed, original: protected */
            public void onDetachedFromWindow() {
                super.onDetachedFromWindow();
                try {
                    if ((EmbedBottomSheet.this.pipVideoView == null || EmbedBottomSheet.this.webView.getVisibility() != 0) && EmbedBottomSheet.this.webView.getParent() != null) {
                        removeView(EmbedBottomSheet.this.webView);
                        EmbedBottomSheet.this.webView.stopLoading();
                        EmbedBottomSheet.this.webView.loadUrl("about:blank");
                        EmbedBottomSheet.this.webView.destroy();
                    }
                    if (!EmbedBottomSheet.this.videoView.isInline() && EmbedBottomSheet.this.pipVideoView == null) {
                        if (EmbedBottomSheet.instance == EmbedBottomSheet.this) {
                            EmbedBottomSheet.instance = null;
                        }
                        EmbedBottomSheet.this.videoView.destroy();
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }

            /* Access modifiers changed, original: protected */
            public void onMeasure(int i, int i2) {
                super.onMeasure(i, MeasureSpec.makeMeasureSpec((((int) Math.min(((float) EmbedBottomSheet.this.height) / (((float) EmbedBottomSheet.this.width) / ((float) MeasureSpec.getSize(i))), (float) (AndroidUtilities.displaySize.y / 2))) + AndroidUtilities.dp((float) ((EmbedBottomSheet.this.hasDescription ? 22 : 0) + 84))) + 1, NUM));
            }
        };
        this.containerLayout.setOnTouchListener(-$$Lambda$EmbedBottomSheet$iYWCViD8wKuconyj-uGO4wKzEcQ.INSTANCE);
        setCustomView(this.containerLayout);
        this.webView = new WebView(context2);
        this.webView.getSettings().setJavaScriptEnabled(true);
        this.webView.getSettings().setDomStorageEnabled(true);
        if (VERSION.SDK_INT >= 17) {
            this.webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        }
        if (VERSION.SDK_INT >= 21) {
            this.webView.getSettings().setMixedContentMode(0);
            CookieManager.getInstance().setAcceptThirdPartyCookies(this.webView, true);
        }
        this.webView.setWebChromeClient(new WebChromeClient() {
            public void onShowCustomView(View view, int i, CustomViewCallback customViewCallback) {
                onShowCustomView(view, customViewCallback);
            }

            public void onShowCustomView(View view, CustomViewCallback customViewCallback) {
                if (EmbedBottomSheet.this.customView == null && EmbedBottomSheet.this.pipVideoView == null) {
                    EmbedBottomSheet.this.exitFromPip();
                    EmbedBottomSheet.this.customView = view;
                    EmbedBottomSheet.this.getSheetContainer().setVisibility(4);
                    EmbedBottomSheet.this.fullscreenVideoContainer.setVisibility(0);
                    EmbedBottomSheet.this.fullscreenVideoContainer.addView(view, LayoutHelper.createFrame(-1, -1.0f));
                    EmbedBottomSheet.this.customViewCallback = customViewCallback;
                    return;
                }
                customViewCallback.onCustomViewHidden();
            }

            public void onHideCustomView() {
                super.onHideCustomView();
                if (EmbedBottomSheet.this.customView != null) {
                    EmbedBottomSheet.this.getSheetContainer().setVisibility(0);
                    EmbedBottomSheet.this.fullscreenVideoContainer.setVisibility(4);
                    EmbedBottomSheet.this.fullscreenVideoContainer.removeView(EmbedBottomSheet.this.customView);
                    if (!(EmbedBottomSheet.this.customViewCallback == null || EmbedBottomSheet.this.customViewCallback.getClass().getName().contains(".chromium."))) {
                        EmbedBottomSheet.this.customViewCallback.onCustomViewHidden();
                    }
                    EmbedBottomSheet.this.customView = null;
                }
            }
        });
        this.webView.setWebViewClient(new WebViewClient() {
            public void onLoadResource(WebView webView, String str) {
                super.onLoadResource(webView, str);
            }

            public void onPageFinished(WebView webView, String str) {
                super.onPageFinished(webView, str);
                if (!EmbedBottomSheet.this.isYouTube || VERSION.SDK_INT < 17) {
                    EmbedBottomSheet.this.progressBar.setVisibility(4);
                    EmbedBottomSheet.this.progressBarBlackBackground.setVisibility(4);
                    EmbedBottomSheet.this.pipButton.setEnabled(true);
                    EmbedBottomSheet.this.pipButton.setAlpha(1.0f);
                }
            }
        });
        int i4 = 22;
        this.containerLayout.addView(this.webView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, (float) ((this.hasDescription ? 22 : 0) + 84)));
        this.videoView = new WebPlayerView(context2, true, false, new WebPlayerViewDelegate() {
            public void onSharePressed() {
            }

            public void onVideoSizeChanged(float f, int i) {
            }

            public void onInitFailed() {
                EmbedBottomSheet.this.webView.setVisibility(0);
                EmbedBottomSheet.this.imageButtonsContainer.setVisibility(0);
                EmbedBottomSheet.this.copyTextButton.setVisibility(4);
                EmbedBottomSheet.this.webView.setKeepScreenOn(true);
                EmbedBottomSheet.this.videoView.setVisibility(4);
                EmbedBottomSheet.this.videoView.getControlsView().setVisibility(4);
                EmbedBottomSheet.this.videoView.getTextureView().setVisibility(4);
                if (EmbedBottomSheet.this.videoView.getTextureImageView() != null) {
                    EmbedBottomSheet.this.videoView.getTextureImageView().setVisibility(4);
                }
                EmbedBottomSheet.this.videoView.loadVideo(null, null, null, null, false);
                HashMap hashMap = new HashMap();
                hashMap.put("Referer", "http://youtube.com");
                try {
                    EmbedBottomSheet.this.webView.loadUrl(EmbedBottomSheet.this.embedUrl, hashMap);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }

            public TextureView onSwitchToFullscreen(View view, boolean z, float f, int i, boolean z2) {
                if (z) {
                    EmbedBottomSheet.this.fullscreenVideoContainer.setVisibility(0);
                    EmbedBottomSheet.this.fullscreenVideoContainer.setAlpha(1.0f);
                    EmbedBottomSheet.this.fullscreenVideoContainer.addView(EmbedBottomSheet.this.videoView.getAspectRatioView());
                    EmbedBottomSheet.this.wasInLandscape = false;
                    EmbedBottomSheet.this.fullscreenedByButton = z2;
                    if (EmbedBottomSheet.this.parentActivity != null) {
                        try {
                            EmbedBottomSheet.this.prevOrientation = EmbedBottomSheet.this.parentActivity.getRequestedOrientation();
                            if (z2) {
                                if (((WindowManager) EmbedBottomSheet.this.parentActivity.getSystemService("window")).getDefaultDisplay().getRotation() == 3) {
                                    EmbedBottomSheet.this.parentActivity.setRequestedOrientation(8);
                                } else {
                                    EmbedBottomSheet.this.parentActivity.setRequestedOrientation(0);
                                }
                            }
                            EmbedBottomSheet.this.containerView.setSystemUiVisibility(1028);
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
                    }
                } else {
                    EmbedBottomSheet.this.fullscreenVideoContainer.setVisibility(4);
                    EmbedBottomSheet.this.fullscreenedByButton = false;
                    if (EmbedBottomSheet.this.parentActivity != null) {
                        try {
                            EmbedBottomSheet.this.containerView.setSystemUiVisibility(0);
                            EmbedBottomSheet.this.parentActivity.setRequestedOrientation(EmbedBottomSheet.this.prevOrientation);
                        } catch (Exception e2) {
                            FileLog.e(e2);
                        }
                    }
                }
                return null;
            }

            public void onInlineSurfaceTextureReady() {
                if (EmbedBottomSheet.this.videoView.isInline()) {
                    EmbedBottomSheet.this.dismissInternal();
                }
            }

            public void prepareToSwitchInlineMode(boolean z, Runnable runnable, float f, boolean z2) {
                if (z) {
                    if (EmbedBottomSheet.this.parentActivity != null) {
                        try {
                            EmbedBottomSheet.this.containerView.setSystemUiVisibility(0);
                            if (EmbedBottomSheet.this.prevOrientation != -2) {
                                EmbedBottomSheet.this.parentActivity.setRequestedOrientation(EmbedBottomSheet.this.prevOrientation);
                            }
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
                    }
                    if (EmbedBottomSheet.this.fullscreenVideoContainer.getVisibility() == 0) {
                        EmbedBottomSheet.this.containerView.setTranslationY((float) (EmbedBottomSheet.this.containerView.getMeasuredHeight() + AndroidUtilities.dp(10.0f)));
                        EmbedBottomSheet.this.backDrawable.setAlpha(0);
                    }
                    EmbedBottomSheet.this.setOnShowListener(null);
                    final Runnable runnable2;
                    if (z2) {
                        TextureView textureView = EmbedBottomSheet.this.videoView.getTextureView();
                        View controlsView = EmbedBottomSheet.this.videoView.getControlsView();
                        ImageView textureImageView = EmbedBottomSheet.this.videoView.getTextureImageView();
                        Rect pipRect = PipVideoView.getPipRect(f);
                        float width = pipRect.width / ((float) textureView.getWidth());
                        if (VERSION.SDK_INT >= 21) {
                            pipRect.y += (float) AndroidUtilities.statusBarHeight;
                        }
                        AnimatorSet animatorSet = new AnimatorSet();
                        r11 = new Animator[12];
                        r11[0] = ObjectAnimator.ofFloat(textureImageView, View.SCALE_X, new float[]{width});
                        r11[1] = ObjectAnimator.ofFloat(textureImageView, View.SCALE_Y, new float[]{width});
                        r11[2] = ObjectAnimator.ofFloat(textureImageView, View.TRANSLATION_X, new float[]{pipRect.x});
                        r11[3] = ObjectAnimator.ofFloat(textureImageView, View.TRANSLATION_Y, new float[]{pipRect.y});
                        r11[4] = ObjectAnimator.ofFloat(textureView, View.SCALE_X, new float[]{width});
                        r11[5] = ObjectAnimator.ofFloat(textureView, View.SCALE_Y, new float[]{width});
                        r11[6] = ObjectAnimator.ofFloat(textureView, View.TRANSLATION_X, new float[]{pipRect.x});
                        r11[7] = ObjectAnimator.ofFloat(textureView, View.TRANSLATION_Y, new float[]{pipRect.y});
                        r11[8] = ObjectAnimator.ofFloat(EmbedBottomSheet.this.containerView, View.TRANSLATION_Y, new float[]{(float) (EmbedBottomSheet.this.containerView.getMeasuredHeight() + AndroidUtilities.dp(10.0f))});
                        r11[9] = ObjectAnimator.ofInt(EmbedBottomSheet.this.backDrawable, AnimationProperties.COLOR_DRAWABLE_ALPHA, new int[]{0});
                        r11[10] = ObjectAnimator.ofFloat(EmbedBottomSheet.this.fullscreenVideoContainer, View.ALPHA, new float[]{0.0f});
                        r11[11] = ObjectAnimator.ofFloat(controlsView, View.ALPHA, new float[]{0.0f});
                        animatorSet.playTogether(r11);
                        animatorSet.setInterpolator(new DecelerateInterpolator());
                        animatorSet.setDuration(250);
                        runnable2 = runnable;
                        animatorSet.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                if (EmbedBottomSheet.this.fullscreenVideoContainer.getVisibility() == 0) {
                                    EmbedBottomSheet.this.fullscreenVideoContainer.setAlpha(1.0f);
                                    EmbedBottomSheet.this.fullscreenVideoContainer.setVisibility(4);
                                }
                                runnable2.run();
                            }
                        });
                        animatorSet.start();
                        return;
                    }
                    runnable2 = runnable;
                    if (EmbedBottomSheet.this.fullscreenVideoContainer.getVisibility() == 0) {
                        EmbedBottomSheet.this.fullscreenVideoContainer.setAlpha(1.0f);
                        EmbedBottomSheet.this.fullscreenVideoContainer.setVisibility(4);
                    }
                    runnable.run();
                    EmbedBottomSheet.this.dismissInternal();
                    return;
                }
                if (ApplicationLoader.mainInterfacePaused) {
                    try {
                        EmbedBottomSheet.this.parentActivity.startService(new Intent(ApplicationLoader.applicationContext, BringAppForegroundService.class));
                    } catch (Throwable e2) {
                        FileLog.e(e2);
                    }
                }
                if (z2) {
                    EmbedBottomSheet embedBottomSheet = EmbedBottomSheet.this;
                    embedBottomSheet.setOnShowListener(embedBottomSheet.onShowListener);
                    Rect pipRect2 = PipVideoView.getPipRect(f);
                    TextureView textureView2 = EmbedBottomSheet.this.videoView.getTextureView();
                    ImageView textureImageView2 = EmbedBottomSheet.this.videoView.getTextureImageView();
                    float f2 = pipRect2.width / ((float) textureView2.getLayoutParams().width);
                    if (VERSION.SDK_INT >= 21) {
                        pipRect2.y += (float) AndroidUtilities.statusBarHeight;
                    }
                    textureImageView2.setScaleX(f2);
                    textureImageView2.setScaleY(f2);
                    textureImageView2.setTranslationX(pipRect2.x);
                    textureImageView2.setTranslationY(pipRect2.y);
                    textureView2.setScaleX(f2);
                    textureView2.setScaleY(f2);
                    textureView2.setTranslationX(pipRect2.x);
                    textureView2.setTranslationY(pipRect2.y);
                } else {
                    EmbedBottomSheet.this.pipVideoView.close();
                    EmbedBottomSheet.this.pipVideoView = null;
                }
                EmbedBottomSheet.this.setShowWithoutAnimation(true);
                EmbedBottomSheet.this.show();
                if (z2) {
                    EmbedBottomSheet.this.waitingForDraw = 4;
                    EmbedBottomSheet.this.backDrawable.setAlpha(1);
                    EmbedBottomSheet.this.containerView.setTranslationY((float) (EmbedBottomSheet.this.containerView.getMeasuredHeight() + AndroidUtilities.dp(10.0f)));
                }
            }

            public TextureView onSwitchInlineMode(View view, boolean z, float f, int i, boolean z2) {
                if (z) {
                    view.setTranslationY(0.0f);
                    EmbedBottomSheet.this.pipVideoView = new PipVideoView();
                    return EmbedBottomSheet.this.pipVideoView.show(EmbedBottomSheet.this.parentActivity, EmbedBottomSheet.this, view, f, i, null);
                }
                if (z2) {
                    EmbedBottomSheet.this.animationInProgress = true;
                    EmbedBottomSheet.this.videoView.getAspectRatioView().getLocationInWindow(EmbedBottomSheet.this.position);
                    int[] access$3600 = EmbedBottomSheet.this.position;
                    access$3600[0] = access$3600[0] - EmbedBottomSheet.this.getLeftInset();
                    access$3600 = EmbedBottomSheet.this.position;
                    access$3600[1] = (int) (((float) access$3600[1]) - EmbedBottomSheet.this.containerView.getTranslationY());
                    TextureView textureView = EmbedBottomSheet.this.videoView.getTextureView();
                    ImageView textureImageView = EmbedBottomSheet.this.videoView.getTextureImageView();
                    AnimatorSet animatorSet = new AnimatorSet();
                    Animator[] animatorArr = new Animator[10];
                    animatorArr[0] = ObjectAnimator.ofFloat(textureImageView, View.SCALE_X, new float[]{1.0f});
                    animatorArr[1] = ObjectAnimator.ofFloat(textureImageView, View.SCALE_Y, new float[]{1.0f});
                    animatorArr[2] = ObjectAnimator.ofFloat(textureImageView, View.TRANSLATION_X, new float[]{(float) EmbedBottomSheet.this.position[0]});
                    animatorArr[3] = ObjectAnimator.ofFloat(textureImageView, View.TRANSLATION_Y, new float[]{(float) EmbedBottomSheet.this.position[1]});
                    animatorArr[4] = ObjectAnimator.ofFloat(textureView, View.SCALE_X, new float[]{1.0f});
                    animatorArr[5] = ObjectAnimator.ofFloat(textureView, View.SCALE_Y, new float[]{1.0f});
                    animatorArr[6] = ObjectAnimator.ofFloat(textureView, View.TRANSLATION_X, new float[]{(float) EmbedBottomSheet.this.position[0]});
                    animatorArr[7] = ObjectAnimator.ofFloat(textureView, View.TRANSLATION_Y, new float[]{(float) EmbedBottomSheet.this.position[1]});
                    animatorArr[8] = ObjectAnimator.ofFloat(EmbedBottomSheet.this.containerView, View.TRANSLATION_Y, new float[]{0.0f});
                    animatorArr[9] = ObjectAnimator.ofInt(EmbedBottomSheet.this.backDrawable, AnimationProperties.COLOR_DRAWABLE_ALPHA, new int[]{51});
                    animatorSet.playTogether(animatorArr);
                    animatorSet.setInterpolator(new DecelerateInterpolator());
                    animatorSet.setDuration(250);
                    animatorSet.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            EmbedBottomSheet.this.animationInProgress = false;
                        }
                    });
                    animatorSet.start();
                } else {
                    EmbedBottomSheet.this.containerView.setTranslationY(0.0f);
                }
                return null;
            }

            public void onPlayStateChanged(WebPlayerView webPlayerView, boolean z) {
                if (z) {
                    try {
                        EmbedBottomSheet.this.parentActivity.getWindow().addFlags(128);
                        return;
                    } catch (Exception e) {
                        FileLog.e(e);
                        return;
                    }
                }
                try {
                    EmbedBottomSheet.this.parentActivity.getWindow().clearFlags(128);
                } catch (Exception e2) {
                    FileLog.e(e2);
                }
            }

            public boolean checkInlinePermissions() {
                return EmbedBottomSheet.this.checkInlinePermissions();
            }

            public ViewGroup getTextureViewContainer() {
                return EmbedBottomSheet.this.container;
            }
        });
        this.videoView.setVisibility(4);
        this.containerLayout.addView(this.videoView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, (float) (((this.hasDescription ? 22 : 0) + 84) - 10)));
        this.progressBarBlackBackground = new View(context2);
        this.progressBarBlackBackground.setBackgroundColor(-16777216);
        this.progressBarBlackBackground.setVisibility(4);
        this.containerLayout.addView(this.progressBarBlackBackground, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, (float) ((this.hasDescription ? 22 : 0) + 84)));
        this.progressBar = new RadialProgressView(context2);
        this.progressBar.setVisibility(4);
        FrameLayout frameLayout = this.containerLayout;
        RadialProgressView radialProgressView = this.progressBar;
        if (!this.hasDescription) {
            i4 = 0;
        }
        frameLayout.addView(radialProgressView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 0.0f, 0.0f, (float) ((i4 + 84) / 2)));
        String str6 = "fonts/rmedium.ttf";
        if (this.hasDescription) {
            TextView textView = new TextView(context2);
            textView.setTextSize(1, 16.0f);
            textView.setTextColor(Theme.getColor("dialogTextBlack"));
            textView.setText(str5);
            textView.setSingleLine(true);
            textView.setTypeface(AndroidUtilities.getTypeface(str6));
            textView.setEllipsize(TruncateAt.END);
            textView.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
            this.containerLayout.addView(textView, LayoutHelper.createFrame(-1, -2.0f, 83, 0.0f, 0.0f, 0.0f, 77.0f));
        }
        TextView textView2 = new TextView(context2);
        textView2.setTextSize(1, 14.0f);
        textView2.setTextColor(Theme.getColor("dialogTextGray"));
        textView2.setText(str);
        textView2.setSingleLine(true);
        textView2.setEllipsize(TruncateAt.END);
        textView2.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        this.containerLayout.addView(textView2, LayoutHelper.createFrame(-1, -2.0f, 83, 0.0f, 0.0f, 0.0f, 57.0f));
        View view = new View(context2);
        view.setBackgroundColor(Theme.getColor("dialogGrayLine"));
        this.containerLayout.addView(view, new LayoutParams(-1, 1, 83));
        ((LayoutParams) view.getLayoutParams()).bottomMargin = AndroidUtilities.dp(48.0f);
        FrameLayout frameLayout2 = new FrameLayout(context2);
        frameLayout2.setBackgroundColor(Theme.getColor("dialogBackground"));
        this.containerLayout.addView(frameLayout2, LayoutHelper.createFrame(-1, 48, 83));
        LinearLayout linearLayout = new LinearLayout(context2);
        linearLayout.setOrientation(0);
        linearLayout.setWeightSum(1.0f);
        frameLayout2.addView(linearLayout, LayoutHelper.createFrame(-2, -1, 53));
        TextView textView3 = new TextView(context2);
        textView3.setTextSize(1, 14.0f);
        String str7 = "dialogTextBlue4";
        textView3.setTextColor(Theme.getColor(str7));
        textView3.setGravity(17);
        textView3.setSingleLine(true);
        textView3.setEllipsize(TruncateAt.END);
        String str8 = "dialogButtonSelector";
        textView3.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(str8), 0));
        textView3.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        textView3.setText(LocaleController.getString("Close", NUM).toUpperCase());
        textView3.setTypeface(AndroidUtilities.getTypeface(str6));
        frameLayout2.addView(textView3, LayoutHelper.createLinear(-2, -1, 51));
        textView3.setOnClickListener(new -$$Lambda$EmbedBottomSheet$7BVC7l6jtG_KhdXyPoXIswQ3uh8(this));
        this.imageButtonsContainer = new LinearLayout(context2);
        this.imageButtonsContainer.setVisibility(4);
        frameLayout2.addView(this.imageButtonsContainer, LayoutHelper.createFrame(-2, -1, 17));
        this.pipButton = new ImageView(context2);
        this.pipButton.setScaleType(ScaleType.CENTER);
        this.pipButton.setImageResource(NUM);
        this.pipButton.setEnabled(false);
        this.pipButton.setAlpha(0.5f);
        this.pipButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str7), Mode.MULTIPLY));
        this.pipButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(str8), 0));
        this.imageButtonsContainer.addView(this.pipButton, LayoutHelper.createFrame(48, 48.0f, 51, 0.0f, 0.0f, 4.0f, 0.0f));
        this.pipButton.setOnClickListener(new -$$Lambda$EmbedBottomSheet$y37DL-JjK8uMG3joIQ1jW7ubjuE(this));
        -$$Lambda$EmbedBottomSheet$ME3BZek9Olzkn_G2jOkI4EyTOfQ -__lambda_embedbottomsheet_me3bzek9olzkn_g2joki4eytofq = new -$$Lambda$EmbedBottomSheet$ME3BZek9Olzkn_G2jOkI4EyTOfQ(this);
        ImageView imageView = new ImageView(context2);
        imageView.setScaleType(ScaleType.CENTER);
        imageView.setImageResource(NUM);
        imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str7), Mode.MULTIPLY));
        imageView.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(str8), 0));
        this.imageButtonsContainer.addView(imageView, LayoutHelper.createFrame(48, 48, 51));
        imageView.setOnClickListener(-__lambda_embedbottomsheet_me3bzek9olzkn_g2joki4eytofq);
        this.copyTextButton = new TextView(context2);
        this.copyTextButton.setTextSize(1, 14.0f);
        this.copyTextButton.setTextColor(Theme.getColor(str7));
        this.copyTextButton.setGravity(17);
        this.copyTextButton.setSingleLine(true);
        this.copyTextButton.setEllipsize(TruncateAt.END);
        this.copyTextButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(str8), 0));
        this.copyTextButton.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        this.copyTextButton.setText(LocaleController.getString("Copy", NUM).toUpperCase());
        this.copyTextButton.setTypeface(AndroidUtilities.getTypeface(str6));
        linearLayout.addView(this.copyTextButton, LayoutHelper.createFrame(-2, -1, 51));
        this.copyTextButton.setOnClickListener(-__lambda_embedbottomsheet_me3bzek9olzkn_g2joki4eytofq);
        textView2 = new TextView(context2);
        textView2.setTextSize(1, 14.0f);
        textView2.setTextColor(Theme.getColor(str7));
        textView2.setGravity(17);
        textView2.setSingleLine(true);
        textView2.setEllipsize(TruncateAt.END);
        textView2.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(str8), 0));
        textView2.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        textView2.setText(LocaleController.getString("OpenInBrowser", NUM).toUpperCase());
        textView2.setTypeface(AndroidUtilities.getTypeface(str6));
        linearLayout.addView(textView2, LayoutHelper.createFrame(-2, -1, 51));
        textView2.setOnClickListener(new -$$Lambda$EmbedBottomSheet$xNtglytXCFyYO3cCP1Moj3HVHKg(this));
        setDelegate(new BottomSheetDelegate() {
            public void onOpenAnimationEnd() {
                String str = "m";
                if (EmbedBottomSheet.this.videoView.loadVideo(EmbedBottomSheet.this.embedUrl, null, null, EmbedBottomSheet.this.openUrl, true)) {
                    EmbedBottomSheet.this.progressBar.setVisibility(4);
                    EmbedBottomSheet.this.webView.setVisibility(4);
                    EmbedBottomSheet.this.videoView.setVisibility(0);
                    return;
                }
                EmbedBottomSheet.this.progressBar.setVisibility(0);
                EmbedBottomSheet.this.webView.setVisibility(0);
                EmbedBottomSheet.this.imageButtonsContainer.setVisibility(0);
                EmbedBottomSheet.this.copyTextButton.setVisibility(4);
                EmbedBottomSheet.this.webView.setKeepScreenOn(true);
                EmbedBottomSheet.this.videoView.setVisibility(4);
                EmbedBottomSheet.this.videoView.getControlsView().setVisibility(4);
                EmbedBottomSheet.this.videoView.getTextureView().setVisibility(4);
                if (EmbedBottomSheet.this.videoView.getTextureImageView() != null) {
                    EmbedBottomSheet.this.videoView.getTextureImageView().setVisibility(4);
                }
                EmbedBottomSheet.this.videoView.loadVideo(null, null, null, null, false);
                HashMap hashMap = new HashMap();
                hashMap.put("Referer", "http://youtube.com");
                try {
                    if (EmbedBottomSheet.this.videoView.getYoutubeId() != null) {
                        int intValue;
                        EmbedBottomSheet.this.progressBarBlackBackground.setVisibility(0);
                        EmbedBottomSheet.this.isYouTube = true;
                        String str2 = null;
                        if (VERSION.SDK_INT >= 17) {
                            EmbedBottomSheet.this.webView.addJavascriptInterface(new YoutubeProxy(EmbedBottomSheet.this, null), "YoutubeProxy");
                        }
                        if (EmbedBottomSheet.this.openUrl != null) {
                            try {
                                Uri parse = Uri.parse(EmbedBottomSheet.this.openUrl);
                                if (EmbedBottomSheet.this.seekTimeOverride > 0) {
                                    StringBuilder stringBuilder = new StringBuilder();
                                    stringBuilder.append("");
                                    stringBuilder.append(EmbedBottomSheet.this.seekTimeOverride);
                                    str2 = stringBuilder.toString();
                                }
                                if (str2 == null) {
                                    str2 = parse.getQueryParameter("t");
                                    if (str2 == null) {
                                        str2 = parse.getQueryParameter("time_continue");
                                    }
                                }
                                if (str2 != null) {
                                    if (str2.contains(str)) {
                                        String[] split = str2.split(str);
                                        intValue = (Utilities.parseInt(split[0]).intValue() * 60) + Utilities.parseInt(split[1]).intValue();
                                    } else {
                                        intValue = Utilities.parseInt(str2).intValue();
                                    }
                                    EmbedBottomSheet.this.webView.loadDataWithBaseURL("https://www.youtube.com", String.format(Locale.US, "<!DOCTYPE html><html><head><style>body { margin: 0; width:100%%; height:100%%;  background-color:#000; }html { width:100%%; height:100%%; background-color:#000; }.embed-container iframe,.embed-container object,   .embed-container embed {       position: absolute;       top: 0;       left: 0;       width: 100%% !important;       height: 100%% !important;   }   </style></head><body>   <div class=\"embed-container\">       <div id=\"player\"></div>   </div>   <script src=\"https://www.youtube.com/iframe_api\"></script>   <script>   var player;   var observer;   var videoEl;   var playing;   var posted = false;   YT.ready(function() {       player = new YT.Player(\"player\", {                              \"width\" : \"100%%\",                              \"events\" : {                              \"onReady\" : \"onReady\",                              \"onError\" : \"onError\",                              },                              \"videoId\" : \"%1$s\",                              \"height\" : \"100%%\",                              \"playerVars\" : {                              \"start\" : %2$d,                              \"rel\" : 0,                              \"showinfo\" : 0,                              \"modestbranding\" : 1,                              \"iv_load_policy\" : 3,                              \"autohide\" : 1,                              \"autoplay\" : 1,                              \"cc_load_policy\" : 1,                              \"playsinline\" : 1,                              \"controls\" : 1                              }                            });        player.setSize(window.innerWidth, window.innerHeight);    });    function hideControls() {        playing = !videoEl.paused;       videoEl.controls = 0;       observer.observe(videoEl, {attributes: true});    }    function showControls() {        playing = !videoEl.paused;       observer.disconnect();       videoEl.controls = 1;    }    function onError(event) {       if (!posted) {            if (window.YoutubeProxy !== undefined) {                   YoutubeProxy.postEvent(\"loaded\", null);             }            posted = true;       }    }    function onReady(event) {       player.playVideo();       videoEl = player.getIframe().contentDocument.getElementsByTagName('video')[0];\n       videoEl.addEventListener(\"canplay\", function() {            if (playing) {               videoEl.play();            }       }, true);       videoEl.addEventListener(\"timeupdate\", function() {            if (!posted && videoEl.currentTime > 0) {               if (window.YoutubeProxy !== undefined) {                   YoutubeProxy.postEvent(\"loaded\", null);                }               posted = true;           }       }, true);       observer = new MutationObserver(function() {\n          if (videoEl.controls) {\n               videoEl.controls = 0;\n          }       });\n    }    window.onresize = function() {        player.setSize(window.innerWidth, window.innerHeight);    }    </script></body></html>", new Object[]{r2, Integer.valueOf(intValue)}), "text/html", "UTF-8", "http://youtube.com");
                                    return;
                                }
                            } catch (Exception e) {
                                FileLog.e(e);
                            }
                        }
                        intValue = 0;
                        EmbedBottomSheet.this.webView.loadDataWithBaseURL("https://www.youtube.com", String.format(Locale.US, "<!DOCTYPE html><html><head><style>body { margin: 0; width:100%%; height:100%%;  background-color:#000; }html { width:100%%; height:100%%; background-color:#000; }.embed-container iframe,.embed-container object,   .embed-container embed {       position: absolute;       top: 0;       left: 0;       width: 100%% !important;       height: 100%% !important;   }   </style></head><body>   <div class=\"embed-container\">       <div id=\"player\"></div>   </div>   <script src=\"https://www.youtube.com/iframe_api\"></script>   <script>   var player;   var observer;   var videoEl;   var playing;   var posted = false;   YT.ready(function() {       player = new YT.Player(\"player\", {                              \"width\" : \"100%%\",                              \"events\" : {                              \"onReady\" : \"onReady\",                              \"onError\" : \"onError\",                              },                              \"videoId\" : \"%1$s\",                              \"height\" : \"100%%\",                              \"playerVars\" : {                              \"start\" : %2$d,                              \"rel\" : 0,                              \"showinfo\" : 0,                              \"modestbranding\" : 1,                              \"iv_load_policy\" : 3,                              \"autohide\" : 1,                              \"autoplay\" : 1,                              \"cc_load_policy\" : 1,                              \"playsinline\" : 1,                              \"controls\" : 1                              }                            });        player.setSize(window.innerWidth, window.innerHeight);    });    function hideControls() {        playing = !videoEl.paused;       videoEl.controls = 0;       observer.observe(videoEl, {attributes: true});    }    function showControls() {        playing = !videoEl.paused;       observer.disconnect();       videoEl.controls = 1;    }    function onError(event) {       if (!posted) {            if (window.YoutubeProxy !== undefined) {                   YoutubeProxy.postEvent(\"loaded\", null);             }            posted = true;       }    }    function onReady(event) {       player.playVideo();       videoEl = player.getIframe().contentDocument.getElementsByTagName('video')[0];\n       videoEl.addEventListener(\"canplay\", function() {            if (playing) {               videoEl.play();            }       }, true);       videoEl.addEventListener(\"timeupdate\", function() {            if (!posted && videoEl.currentTime > 0) {               if (window.YoutubeProxy !== undefined) {                   YoutubeProxy.postEvent(\"loaded\", null);                }               posted = true;           }       }, true);       observer = new MutationObserver(function() {\n          if (videoEl.controls) {\n               videoEl.controls = 0;\n          }       });\n    }    window.onresize = function() {        player.setSize(window.innerWidth, window.innerHeight);    }    </script></body></html>", new Object[]{r2, Integer.valueOf(intValue)}), "text/html", "UTF-8", "http://youtube.com");
                        return;
                    }
                    EmbedBottomSheet.this.webView.loadUrl(EmbedBottomSheet.this.embedUrl, hashMap);
                } catch (Exception e2) {
                    FileLog.e(e2);
                }
            }

            public boolean canDismiss() {
                if (EmbedBottomSheet.this.videoView.isInFullscreen()) {
                    EmbedBottomSheet.this.videoView.exitFullscreen();
                    return false;
                }
                try {
                    EmbedBottomSheet.this.parentActivity.getWindow().clearFlags(128);
                } catch (Exception e) {
                    FileLog.e(e);
                }
                return true;
            }
        });
        this.orientationEventListener = new OrientationEventListener(ApplicationLoader.applicationContext) {
            public void onOrientationChanged(int i) {
                if (EmbedBottomSheet.this.orientationEventListener == null || EmbedBottomSheet.this.videoView.getVisibility() != 0 || EmbedBottomSheet.this.parentActivity == null || !EmbedBottomSheet.this.videoView.isInFullscreen() || !EmbedBottomSheet.this.fullscreenedByButton) {
                    return;
                }
                if (i >= 240 && i <= 300) {
                    EmbedBottomSheet.this.wasInLandscape = true;
                } else if (!EmbedBottomSheet.this.wasInLandscape) {
                } else {
                    if (i >= 330 || i <= 30) {
                        EmbedBottomSheet.this.parentActivity.setRequestedOrientation(EmbedBottomSheet.this.prevOrientation);
                        EmbedBottomSheet.this.fullscreenedByButton = false;
                        EmbedBottomSheet.this.wasInLandscape = false;
                    }
                }
            }
        };
        if (this.videoView.getYouTubeVideoId(this.embedUrl) != null) {
            this.progressBar.setVisibility(0);
            this.webView.setVisibility(0);
            this.imageButtonsContainer.setVisibility(0);
            this.progressBarBlackBackground.setVisibility(0);
            this.copyTextButton.setVisibility(4);
            this.webView.setKeepScreenOn(true);
            this.videoView.setVisibility(4);
            this.videoView.getControlsView().setVisibility(4);
            this.videoView.getTextureView().setVisibility(4);
            if (this.videoView.getTextureImageView() != null) {
                this.videoView.getTextureImageView().setVisibility(4);
            }
        }
        if (this.orientationEventListener.canDetectOrientation()) {
            this.orientationEventListener.enable();
        } else {
            this.orientationEventListener.disable();
            this.orientationEventListener = null;
        }
        instance = this;
    }

    public /* synthetic */ void lambda$new$3$EmbedBottomSheet(View view) {
        dismiss();
    }

    /* JADX WARNING: Removed duplicated region for block: B:14:0x0038  */
    public /* synthetic */ void lambda$new$4$EmbedBottomSheet(android.view.View r8) {
        /*
        r7 = this;
        r8 = r7.checkInlinePermissions();
        if (r8 != 0) goto L_0x0007;
    L_0x0006:
        return;
    L_0x0007:
        r8 = r7.progressBar;
        r8 = r8.getVisibility();
        if (r8 != 0) goto L_0x0010;
    L_0x000f:
        return;
    L_0x0010:
        r8 = new org.telegram.ui.Components.PipVideoView;
        r8.<init>();
        r7.pipVideoView = r8;
        r0 = r7.pipVideoView;
        r1 = r7.parentActivity;
        r3 = 0;
        r8 = r7.width;
        if (r8 == 0) goto L_0x0029;
    L_0x0020:
        r2 = r7.height;
        if (r2 == 0) goto L_0x0029;
    L_0x0024:
        r8 = (float) r8;
        r2 = (float) r2;
        r8 = r8 / r2;
        r4 = r8;
        goto L_0x002d;
    L_0x0029:
        r8 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r4 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
    L_0x002d:
        r5 = 0;
        r6 = r7.webView;
        r2 = r7;
        r0.show(r1, r2, r3, r4, r5, r6);
        r8 = r7.isYouTube;
        if (r8 == 0) goto L_0x003d;
    L_0x0038:
        r8 = "hideControls();";
        r7.runJsCode(r8);
    L_0x003d:
        r8 = r7.containerView;
        r0 = 0;
        r8.setTranslationY(r0);
        r7.dismissInternal();
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmbedBottomSheet.lambda$new$4$EmbedBottomSheet(android.view.View):void");
    }

    public /* synthetic */ void lambda$new$5$EmbedBottomSheet(View view) {
        try {
            ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", this.openUrl));
        } catch (Exception e) {
            FileLog.e(e);
        }
        Toast.makeText(getContext(), LocaleController.getString("LinkCopied", NUM), 0).show();
        dismiss();
    }

    public /* synthetic */ void lambda$new$6$EmbedBottomSheet(View view) {
        Browser.openUrl(this.parentActivity, this.openUrl);
        dismiss();
    }

    private void runJsCode(String str) {
        if (VERSION.SDK_INT >= 21) {
            this.webView.evaluateJavascript(str, null);
            return;
        }
        try {
            WebView webView = this.webView;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("javascript:");
            stringBuilder.append(str);
            webView.loadUrl(stringBuilder.toString());
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public boolean checkInlinePermissions() {
        Activity activity = this.parentActivity;
        if (activity == null) {
            return false;
        }
        if (VERSION.SDK_INT < 23 || Settings.canDrawOverlays(activity)) {
            return true;
        }
        new Builder(this.parentActivity).setTitle(LocaleController.getString("AppName", NUM)).setMessage(LocaleController.getString("PermissionDrawAboveOtherApps", NUM)).setPositiveButton(LocaleController.getString("PermissionOpenSettings", NUM), new -$$Lambda$EmbedBottomSheet$tysJhT9JYmKbbUAs4BBLzE6Oi5c(this)).show();
        return false;
    }

    public /* synthetic */ void lambda$checkInlinePermissions$7$EmbedBottomSheet(DialogInterface dialogInterface, int i) {
        Activity activity = this.parentActivity;
        if (activity != null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("package:");
            stringBuilder.append(this.parentActivity.getPackageName());
            activity.startActivity(new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse(stringBuilder.toString())));
        }
    }

    /* Access modifiers changed, original: protected */
    public boolean canDismissWithSwipe() {
        return (this.videoView.getVisibility() == 0 && this.videoView.isInFullscreen()) ? false : true;
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
        PipVideoView pipVideoView = this.pipVideoView;
        if (pipVideoView != null) {
            pipVideoView.onConfigurationChanged();
        }
    }

    public void destroy() {
        WebView webView = this.webView;
        if (webView != null && webView.getVisibility() == 0) {
            this.containerLayout.removeView(this.webView);
            this.webView.stopLoading();
            this.webView.loadUrl("about:blank");
            this.webView.destroy();
        }
        PipVideoView pipVideoView = this.pipVideoView;
        if (pipVideoView != null) {
            pipVideoView.close();
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
        if (!(this.videoView.isInline() || this.animationInProgress)) {
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

    /* Access modifiers changed, original: protected */
    public boolean canDismissWithTouchOutside() {
        return this.fullscreenVideoContainer.getVisibility() != 0;
    }

    /* Access modifiers changed, original: protected */
    public void onContainerTranslationYChanged(float f) {
        updateTextureViewPosition();
    }

    /* Access modifiers changed, original: protected */
    public boolean onCustomMeasure(View view, int i, int i2) {
        if (view == this.videoView.getControlsView()) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.width = this.videoView.getMeasuredWidth();
            layoutParams.height = this.videoView.getAspectRatioView().getMeasuredHeight() + (this.videoView.isInFullscreen() ? 0 : AndroidUtilities.dp(10.0f));
        }
        return false;
    }

    /* Access modifiers changed, original: protected */
    public boolean onCustomLayout(View view, int i, int i2, int i3, int i4) {
        if (view == this.videoView.getControlsView()) {
            updateTextureViewPosition();
        }
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
