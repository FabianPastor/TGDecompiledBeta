package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.os.Build.VERSION;
import android.provider.Settings;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.TextureView;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
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
import java.util.Map;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BringAppForegroundService;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.extractor.ts.PsExtractor;
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
    private int lastOrientation;
    private OnShowListener onShowListener;
    private TextView openInButton;
    private String openUrl;
    private OrientationEventListener orientationEventListener;
    private Activity parentActivity;
    private ImageView pipButton;
    private PipVideoView pipVideoView;
    private int[] position;
    private int prevOrientation;
    private RadialProgressView progressBar;
    private View progressBarBlackBackground;
    private WebPlayerView videoView;
    private int waitingForDraw;
    private boolean wasInLandscape;
    private WebView webView;
    private int width;
    private final String youtubeFrame;
    private ImageView youtubeLogoImage;

    /* renamed from: org.telegram.ui.Components.EmbedBottomSheet$1 */
    class C11331 implements OnShowListener {

        /* renamed from: org.telegram.ui.Components.EmbedBottomSheet$1$1 */
        class C11311 implements OnPreDrawListener {
            C11311() {
            }

            public boolean onPreDraw() {
                EmbedBottomSheet.this.videoView.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        }

        C11331() {
        }

        public void onShow(DialogInterface dialogInterface) {
            if (EmbedBottomSheet.this.pipVideoView != null && EmbedBottomSheet.this.videoView.isInline() != null) {
                EmbedBottomSheet.this.videoView.getViewTreeObserver().addOnPreDrawListener(new C11311());
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.EmbedBottomSheet$2 */
    class C11342 implements OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }

        C11342() {
        }
    }

    /* renamed from: org.telegram.ui.Components.EmbedBottomSheet$4 */
    class C11364 implements OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }

        C11364() {
        }
    }

    /* renamed from: org.telegram.ui.Components.EmbedBottomSheet$6 */
    class C11386 extends WebChromeClient {
        C11386() {
        }

        public void onShowCustomView(View view, int i, CustomViewCallback customViewCallback) {
            onShowCustomView(view, customViewCallback);
        }

        public void onShowCustomView(View view, CustomViewCallback customViewCallback) {
            if (EmbedBottomSheet.this.customView == null) {
                if (EmbedBottomSheet.this.pipVideoView == null) {
                    EmbedBottomSheet.this.exitFromPip();
                    EmbedBottomSheet.this.customView = view;
                    EmbedBottomSheet.this.getSheetContainer().setVisibility(4);
                    EmbedBottomSheet.this.fullscreenVideoContainer.setVisibility(0);
                    EmbedBottomSheet.this.fullscreenVideoContainer.addView(view, LayoutHelper.createFrame(-1, -1.0f));
                    EmbedBottomSheet.this.customViewCallback = customViewCallback;
                    return;
                }
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
    }

    /* renamed from: org.telegram.ui.Components.EmbedBottomSheet$7 */
    class C11397 extends WebViewClient {
        C11397() {
        }

        public void onLoadResource(WebView webView, String str) {
            super.onLoadResource(webView, str);
        }

        public void onPageFinished(WebView webView, String str) {
            super.onPageFinished(webView, str);
            if (EmbedBottomSheet.this.isYouTube == null || VERSION.SDK_INT < 17) {
                EmbedBottomSheet.this.progressBar.setVisibility(4);
                EmbedBottomSheet.this.progressBarBlackBackground.setVisibility(4);
                EmbedBottomSheet.this.pipButton.setEnabled(true);
                EmbedBottomSheet.this.pipButton.setAlpha(1.0f);
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.EmbedBottomSheet$8 */
    class C11408 implements OnClickListener {
        C11408() {
        }

        public void onClick(View view) {
            if (EmbedBottomSheet.this.youtubeLogoImage.getAlpha() != 0.0f) {
                EmbedBottomSheet.this.openInButton.callOnClick();
            }
        }
    }

    private class YoutubeProxy {
        private YoutubeProxy() {
        }

        @JavascriptInterface
        public void postEvent(final String str, String str2) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    boolean z;
                    String str = str;
                    if (str.hashCode() == -NUM) {
                        if (str.equals("loaded")) {
                            z = false;
                            if (z) {
                                EmbedBottomSheet.this.progressBar.setVisibility(4);
                                EmbedBottomSheet.this.progressBarBlackBackground.setVisibility(4);
                                EmbedBottomSheet.this.pipButton.setEnabled(true);
                                EmbedBottomSheet.this.pipButton.setAlpha(1.0f);
                                EmbedBottomSheet.this.showOrHideYoutubeLogo(false);
                            }
                        }
                    }
                    z = true;
                    if (z) {
                        EmbedBottomSheet.this.progressBar.setVisibility(4);
                        EmbedBottomSheet.this.progressBarBlackBackground.setVisibility(4);
                        EmbedBottomSheet.this.pipButton.setEnabled(true);
                        EmbedBottomSheet.this.pipButton.setAlpha(1.0f);
                        EmbedBottomSheet.this.showOrHideYoutubeLogo(false);
                    }
                }
            });
        }
    }

    /* renamed from: org.telegram.ui.Components.EmbedBottomSheet$9 */
    class C20519 implements WebPlayerViewDelegate {

        /* renamed from: org.telegram.ui.Components.EmbedBottomSheet$9$2 */
        class C11422 extends AnimatorListenerAdapter {
            C11422() {
            }

            public void onAnimationEnd(Animator animator) {
                EmbedBottomSheet.this.animationInProgress = false;
            }
        }

        public void onSharePressed() {
        }

        public void onVideoSizeChanged(float f, int i) {
        }

        C20519() {
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
            EmbedBottomSheet.this.videoView.loadVideo(null, null, null, false);
            Map hashMap = new HashMap();
            hashMap.put("Referer", "http://youtube.com");
            try {
                EmbedBottomSheet.this.webView.loadUrl(EmbedBottomSheet.this.embedUrl, hashMap);
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }

        public TextureView onSwitchToFullscreen(View view, boolean z, float f, int i, boolean z2) {
            if (z) {
                EmbedBottomSheet.this.fullscreenVideoContainer.setVisibility(0);
                EmbedBottomSheet.this.fullscreenVideoContainer.setAlpha(1.0f);
                EmbedBottomSheet.this.fullscreenVideoContainer.addView(EmbedBottomSheet.this.videoView.getAspectRatioView());
                EmbedBottomSheet.this.wasInLandscape = false;
                EmbedBottomSheet.this.fullscreenedByButton = z2;
                if (EmbedBottomSheet.this.parentActivity) {
                    try {
                        EmbedBottomSheet.this.prevOrientation = EmbedBottomSheet.this.parentActivity.getRequestedOrientation();
                        if (z2) {
                            if (((WindowManager) EmbedBottomSheet.this.parentActivity.getSystemService("window")).getDefaultDisplay().getRotation()) {
                                EmbedBottomSheet.this.parentActivity.setRequestedOrientation(true);
                            } else {
                                EmbedBottomSheet.this.parentActivity.setRequestedOrientation(0);
                            }
                        }
                        EmbedBottomSheet.this.containerView.setSystemUiVisibility(true);
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                }
            } else {
                EmbedBottomSheet.this.fullscreenVideoContainer.setVisibility(5.6E-45f);
                EmbedBottomSheet.this.fullscreenedByButton = false;
                if (EmbedBottomSheet.this.parentActivity) {
                    try {
                        EmbedBottomSheet.this.containerView.setSystemUiVisibility(0);
                        EmbedBottomSheet.this.parentActivity.setRequestedOrientation(EmbedBottomSheet.this.prevOrientation);
                    } catch (Throwable e2) {
                        FileLog.m3e(e2);
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
            C20519 c20519 = this;
            if (z) {
                if (EmbedBottomSheet.this.parentActivity != null) {
                    try {
                        EmbedBottomSheet.this.containerView.setSystemUiVisibility(0);
                        if (EmbedBottomSheet.this.prevOrientation != -2) {
                            EmbedBottomSheet.this.parentActivity.setRequestedOrientation(EmbedBottomSheet.this.prevOrientation);
                        }
                    } catch (Throwable e) {
                        FileLog.m3e(e);
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
                        pipRect.f27y += (float) AndroidUtilities.statusBarHeight;
                    }
                    AnimatorSet animatorSet = new AnimatorSet();
                    r12 = new Animator[12];
                    r12[0] = ObjectAnimator.ofFloat(textureImageView, "scaleX", new float[]{width});
                    r12[1] = ObjectAnimator.ofFloat(textureImageView, "scaleY", new float[]{width});
                    r12[2] = ObjectAnimator.ofFloat(textureImageView, "translationX", new float[]{pipRect.f26x});
                    r12[3] = ObjectAnimator.ofFloat(textureImageView, "translationY", new float[]{pipRect.f27y});
                    r12[4] = ObjectAnimator.ofFloat(textureView, "scaleX", new float[]{width});
                    r12[5] = ObjectAnimator.ofFloat(textureView, "scaleY", new float[]{width});
                    r12[6] = ObjectAnimator.ofFloat(textureView, "translationX", new float[]{pipRect.f26x});
                    r12[7] = ObjectAnimator.ofFloat(textureView, "translationY", new float[]{pipRect.f27y});
                    r12[8] = ObjectAnimator.ofFloat(EmbedBottomSheet.this.containerView, "translationY", new float[]{(float) (EmbedBottomSheet.this.containerView.getMeasuredHeight() + AndroidUtilities.dp(10.0f))});
                    r12[9] = ObjectAnimator.ofInt(EmbedBottomSheet.this.backDrawable, "alpha", new int[]{0});
                    r12[10] = ObjectAnimator.ofFloat(EmbedBottomSheet.this.fullscreenVideoContainer, "alpha", new float[]{0.0f});
                    r12[11] = ObjectAnimator.ofFloat(controlsView, "alpha", new float[]{0.0f});
                    animatorSet.playTogether(r12);
                    animatorSet.setInterpolator(new DecelerateInterpolator());
                    animatorSet.setDuration(250);
                    runnable2 = runnable;
                    animatorSet.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            if (EmbedBottomSheet.this.fullscreenVideoContainer.getVisibility() == null) {
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
                    FileLog.m3e(e2);
                }
            }
            if (z2) {
                EmbedBottomSheet.this.setOnShowListener(EmbedBottomSheet.this.onShowListener);
                Rect pipRect2 = PipVideoView.getPipRect(f);
                TextureView textureView2 = EmbedBottomSheet.this.videoView.getTextureView();
                textureImageView = EmbedBottomSheet.this.videoView.getTextureImageView();
                float f2 = pipRect2.width / ((float) textureView2.getLayoutParams().width);
                if (VERSION.SDK_INT >= 21) {
                    pipRect2.f27y += (float) AndroidUtilities.statusBarHeight;
                }
                textureImageView.setScaleX(f2);
                textureImageView.setScaleY(f2);
                textureImageView.setTranslationX(pipRect2.f26x);
                textureImageView.setTranslationY(pipRect2.f27y);
                textureView2.setScaleX(f2);
                textureView2.setScaleY(f2);
                textureView2.setTranslationX(pipRect2.f26x);
                textureView2.setTranslationY(pipRect2.f27y);
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
                view = EmbedBottomSheet.this.position;
                view[0] = view[0] - EmbedBottomSheet.this.getLeftInset();
                view = EmbedBottomSheet.this.position;
                view[1] = (int) (((float) view[1]) - EmbedBottomSheet.this.containerView.getTranslationY());
                view = EmbedBottomSheet.this.videoView.getTextureView();
                i = EmbedBottomSheet.this.videoView.getTextureImageView();
                z2 = new AnimatorSet();
                Animator[] animatorArr = new Animator[10];
                animatorArr[0] = ObjectAnimator.ofFloat(i, "scaleX", new float[]{1.0f});
                animatorArr[1] = ObjectAnimator.ofFloat(i, "scaleY", new float[]{1.0f});
                animatorArr[2] = ObjectAnimator.ofFloat(i, "translationX", new float[]{(float) EmbedBottomSheet.this.position[0]});
                animatorArr[3] = ObjectAnimator.ofFloat(i, "translationY", new float[]{(float) EmbedBottomSheet.this.position[1]});
                animatorArr[4] = ObjectAnimator.ofFloat(view, "scaleX", new float[]{1.0f});
                animatorArr[5] = ObjectAnimator.ofFloat(view, "scaleY", new float[]{1.0f});
                animatorArr[6] = ObjectAnimator.ofFloat(view, "translationX", new float[]{(float) EmbedBottomSheet.this.position[0]});
                animatorArr[7] = ObjectAnimator.ofFloat(view, "translationY", new float[]{(float) EmbedBottomSheet.this.position[1]});
                animatorArr[8] = ObjectAnimator.ofFloat(EmbedBottomSheet.this.containerView, "translationY", new float[]{0.0f});
                animatorArr[9] = ObjectAnimator.ofInt(EmbedBottomSheet.this.backDrawable, "alpha", new int[]{51});
                z2.playTogether(animatorArr);
                z2.setInterpolator(new DecelerateInterpolator());
                z2.setDuration(250);
                z2.addListener(new C11422());
                z2.start();
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
                } catch (Throwable e) {
                    FileLog.m3e(e);
                    return;
                }
            }
            try {
                EmbedBottomSheet.this.parentActivity.getWindow().clearFlags(128);
            } catch (Throwable e2) {
                FileLog.m3e(e2);
            }
        }

        public boolean checkInlinePermissions() {
            return EmbedBottomSheet.this.checkInlinePermissions();
        }

        public ViewGroup getTextureViewContainer() {
            return EmbedBottomSheet.this.container;
        }
    }

    public static void show(Context context, String str, String str2, String str3, String str4, int i, int i2) {
        if (instance != null) {
            instance.destroy();
        }
        new EmbedBottomSheet(context, str, str2, str3, str4, i, i2).show();
    }

    @SuppressLint({"SetJavaScriptEnabled"})
    private EmbedBottomSheet(Context context, String str, String str2, String str3, String str4, int i, int i2) {
        Context context2 = context;
        String str5 = str2;
        super(context2, false);
        this.position = new int[2];
        this.lastOrientation = -1;
        this.prevOrientation = -2;
        this.youtubeFrame = "<!DOCTYPE html><html><head><style>body { margin: 0; width:100%%; height:100%%;  background-color:#000; }html { width:100%%; height:100%%; background-color:#000; }.embed-container iframe,.embed-container object,   .embed-container embed {       position: absolute;       top: 0;       left: 0;       width: 100%% !important;       height: 100%% !important;   }   </style></head><body>   <div class=\"embed-container\">       <div id=\"player\"></div>   </div>   <script src=\"https://www.youtube.com/iframe_api\"></script>   <script>   var player;   var observer;   var videoEl;   var playing;   var posted = false;   YT.ready(function() {       player = new YT.Player(\"player\", {                              \"width\" : \"100%%\",                              \"events\" : {                              \"onReady\" : \"onReady\",                              \"onError\" : \"onError\",                              },                              \"videoId\" : \"%1$s\",                              \"height\" : \"100%%\",                              \"playerVars\" : {                              \"start\" : %2$d,                              \"rel\" : 0,                              \"showinfo\" : 0,                              \"modestbranding\" : 1,                              \"iv_load_policy\" : 3,                              \"autohide\" : 1,                              \"autoplay\" : 1,                              \"cc_load_policy\" : 1,                              \"playsinline\" : 1,                              \"controls\" : 1                              }                            });        player.setSize(window.innerWidth, window.innerHeight);    });    function hideControls() {        playing = !videoEl.paused;       videoEl.controls = 0;       observer.observe(videoEl, {attributes: true});    }    function showControls() {        playing = !videoEl.paused;       observer.disconnect();       videoEl.controls = 1;    }    function onError(event) {       if (!posted) {            if (window.YoutubeProxy !== undefined) {                   YoutubeProxy.postEvent(\"loaded\", null);             }            posted = true;       }    }    function onReady(event) {       player.playVideo();       videoEl = player.getIframe().contentDocument.getElementsByTagName('video')[0];\n       videoEl.addEventListener(\"canplay\", function() {            if (playing) {               videoEl.play();            }       }, true);       videoEl.addEventListener(\"timeupdate\", function() {            if (!posted && videoEl.currentTime > 0) {               if (window.YoutubeProxy !== undefined) {                   YoutubeProxy.postEvent(\"loaded\", null);                }               posted = true;           }       }, true);       observer = new MutationObserver(function() {\n          if (videoEl.controls) {\n               videoEl.controls = 0;\n          }       });\n    }    window.onresize = function() {        player.setSize(window.innerWidth, window.innerHeight);    }    </script></body></html>";
        this.onShowListener = new C11331();
        this.fullWidth = true;
        setApplyTopPadding(false);
        setApplyBottomPadding(false);
        if (context2 instanceof Activity) {
            r0.parentActivity = (Activity) context2;
        }
        r0.embedUrl = str4;
        boolean z = str5 != null && str2.length() > 0;
        r0.hasDescription = z;
        r0.openUrl = str3;
        r0.width = i;
        r0.height = i2;
        if (r0.width == 0 || r0.height == 0) {
            r0.width = AndroidUtilities.displaySize.x;
            r0.height = AndroidUtilities.displaySize.y / 2;
        }
        r0.fullscreenVideoContainer = new FrameLayout(context2);
        r0.fullscreenVideoContainer.setBackgroundColor(Theme.ACTION_BAR_VIDEO_EDIT_COLOR);
        if (VERSION.SDK_INT >= 21) {
            r0.fullscreenVideoContainer.setFitsSystemWindows(true);
        }
        r0.container.addView(r0.fullscreenVideoContainer, LayoutHelper.createFrame(-1, -1.0f));
        r0.fullscreenVideoContainer.setVisibility(4);
        r0.fullscreenVideoContainer.setOnTouchListener(new C11342());
        r0.containerLayout = new FrameLayout(context2) {
            protected void onDetachedFromWindow() {
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
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }

            protected void onMeasure(int i, int i2) {
                super.onMeasure(i, MeasureSpec.makeMeasureSpec((((int) Math.min(((float) EmbedBottomSheet.this.height) / (((float) EmbedBottomSheet.this.width) / ((float) MeasureSpec.getSize(i))), (float) (AndroidUtilities.displaySize.y / 2))) + AndroidUtilities.dp((float) (84 + (EmbedBottomSheet.this.hasDescription ? 22 : 0)))) + 1, NUM));
            }
        };
        r0.containerLayout.setOnTouchListener(new C11364());
        setCustomView(r0.containerLayout);
        r0.webView = new WebView(context2) {
            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (EmbedBottomSheet.this.isYouTube && motionEvent.getAction() == 0) {
                    EmbedBottomSheet.this.showOrHideYoutubeLogo(true);
                }
                return super.onTouchEvent(motionEvent);
            }
        };
        r0.webView.getSettings().setJavaScriptEnabled(true);
        r0.webView.getSettings().setDomStorageEnabled(true);
        if (VERSION.SDK_INT >= 17) {
            r0.webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        }
        if (VERSION.SDK_INT >= 21) {
            r0.webView.getSettings().setMixedContentMode(0);
            CookieManager.getInstance().setAcceptThirdPartyCookies(r0.webView, true);
        }
        r0.webView.setWebChromeClient(new C11386());
        r0.webView.setWebViewClient(new C11397());
        int i3 = 22;
        r0.containerLayout.addView(r0.webView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, (float) (84 + (r0.hasDescription ? 22 : 0))));
        r0.youtubeLogoImage = new ImageView(context2);
        r0.youtubeLogoImage.setVisibility(8);
        r0.containerLayout.addView(r0.youtubeLogoImage, LayoutHelper.createFrame(66, 28.0f, 53, 0.0f, 8.0f, 8.0f, 0.0f));
        r0.youtubeLogoImage.setOnClickListener(new C11408());
        r0.videoView = new WebPlayerView(context2, true, false, new C20519());
        r0.videoView.setVisibility(4);
        r0.containerLayout.addView(r0.videoView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, (float) ((84 + (r0.hasDescription ? 22 : 0)) - 10)));
        r0.progressBarBlackBackground = new View(context2);
        r0.progressBarBlackBackground.setBackgroundColor(Theme.ACTION_BAR_VIDEO_EDIT_COLOR);
        r0.progressBarBlackBackground.setVisibility(4);
        r0.containerLayout.addView(r0.progressBarBlackBackground, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, (float) (84 + (r0.hasDescription ? 22 : 0))));
        r0.progressBar = new RadialProgressView(context2);
        r0.progressBar.setVisibility(4);
        FrameLayout frameLayout = r0.containerLayout;
        View view = r0.progressBar;
        if (!r0.hasDescription) {
            i3 = 0;
        }
        frameLayout.addView(view, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 0.0f, 0.0f, (float) ((84 + i3) / 2)));
        if (r0.hasDescription) {
            View textView = new TextView(context2);
            textView.setTextSize(1, 16.0f);
            textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
            textView.setText(str5);
            textView.setSingleLine(true);
            textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            textView.setEllipsize(TruncateAt.END);
            textView.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
            r0.containerLayout.addView(textView, LayoutHelper.createFrame(-1, -2.0f, 83, 0.0f, 0.0f, 0.0f, 77.0f));
        }
        View textView2 = new TextView(context2);
        textView2.setTextSize(1, 14.0f);
        textView2.setTextColor(Theme.getColor(Theme.key_dialogTextGray));
        textView2.setText(str);
        textView2.setSingleLine(true);
        textView2.setEllipsize(TruncateAt.END);
        textView2.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        r0.containerLayout.addView(textView2, LayoutHelper.createFrame(-1, -2.0f, 83, 0.0f, 0.0f, 0.0f, 57.0f));
        textView2 = new View(context2);
        textView2.setBackgroundColor(Theme.getColor(Theme.key_dialogGrayLine));
        r0.containerLayout.addView(textView2, new LayoutParams(-1, 1, 83));
        ((LayoutParams) textView2.getLayoutParams()).bottomMargin = AndroidUtilities.dp(48.0f);
        textView2 = new FrameLayout(context2);
        textView2.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground));
        r0.containerLayout.addView(textView2, LayoutHelper.createFrame(-1, 48, 83));
        view = new LinearLayout(context2);
        view.setOrientation(0);
        view.setWeightSum(1.0f);
        textView2.addView(view, LayoutHelper.createFrame(-2, -1, 53));
        View textView3 = new TextView(context2);
        textView3.setTextSize(1, 14.0f);
        textView3.setTextColor(Theme.getColor(Theme.key_dialogTextBlue4));
        textView3.setGravity(17);
        textView3.setSingleLine(true);
        textView3.setEllipsize(TruncateAt.END);
        textView3.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_dialogButtonSelector), 0));
        textView3.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        textView3.setText(LocaleController.getString("Close", C0446R.string.Close).toUpperCase());
        textView3.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textView2.addView(textView3, LayoutHelper.createLinear(-2, -1, 51));
        textView3.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                EmbedBottomSheet.this.dismiss();
            }
        });
        r0.imageButtonsContainer = new LinearLayout(context2);
        r0.imageButtonsContainer.setVisibility(4);
        textView2.addView(r0.imageButtonsContainer, LayoutHelper.createFrame(-2, -1, 17));
        r0.pipButton = new ImageView(context2);
        r0.pipButton.setScaleType(ScaleType.CENTER);
        r0.pipButton.setImageResource(C0446R.drawable.video_pip);
        r0.pipButton.setEnabled(false);
        r0.pipButton.setAlpha(0.5f);
        r0.pipButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogTextBlue4), Mode.MULTIPLY));
        r0.pipButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_dialogButtonSelector), 0));
        r0.imageButtonsContainer.addView(r0.pipButton, LayoutHelper.createFrame(48, 48.0f, 51, 0.0f, 0.0f, 4.0f, 0.0f));
        r0.pipButton.setOnClickListener(new OnClickListener() {

            /* renamed from: org.telegram.ui.Components.EmbedBottomSheet$11$1 */
            class C11321 extends AnimatorListenerAdapter {
                C11321() {
                }

                public void onAnimationEnd(Animator animator) {
                    EmbedBottomSheet.this.animationInProgress = false;
                }
            }

            public void onClick(View view) {
                if (EmbedBottomSheet.this.checkInlinePermissions() != null && EmbedBottomSheet.this.progressBar.getVisibility() != null) {
                    EmbedBottomSheet.this.pipVideoView = new PipVideoView();
                    PipVideoView access$400 = EmbedBottomSheet.this.pipVideoView;
                    Activity access$2200 = EmbedBottomSheet.this.parentActivity;
                    EmbedBottomSheet embedBottomSheet = EmbedBottomSheet.this;
                    view = (EmbedBottomSheet.this.width == null || EmbedBottomSheet.this.height == null) ? NUM : ((float) EmbedBottomSheet.this.width) / ((float) EmbedBottomSheet.this.height);
                    access$400.show(access$2200, embedBottomSheet, null, view, 0, EmbedBottomSheet.this.webView);
                    if (EmbedBottomSheet.this.isYouTube != null) {
                        EmbedBottomSheet.this.runJsCode("hideControls();");
                    }
                    EmbedBottomSheet.this.containerView.setTranslationY(0.0f);
                    EmbedBottomSheet.this.dismissInternal();
                }
            }
        });
        OnClickListener anonymousClass12 = new OnClickListener() {
            public void onClick(View view) {
                try {
                    ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", EmbedBottomSheet.this.openUrl));
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
                Toast.makeText(EmbedBottomSheet.this.getContext(), LocaleController.getString("LinkCopied", C0446R.string.LinkCopied), 0).show();
                EmbedBottomSheet.this.dismiss();
            }
        };
        textView3 = new ImageView(context2);
        textView3.setScaleType(ScaleType.CENTER);
        textView3.setImageResource(C0446R.drawable.video_copy);
        textView3.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogTextBlue4), Mode.MULTIPLY));
        textView3.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_dialogButtonSelector), 0));
        r0.imageButtonsContainer.addView(textView3, LayoutHelper.createFrame(48, 48, 51));
        textView3.setOnClickListener(anonymousClass12);
        r0.copyTextButton = new TextView(context2);
        r0.copyTextButton.setTextSize(1, 14.0f);
        r0.copyTextButton.setTextColor(Theme.getColor(Theme.key_dialogTextBlue4));
        r0.copyTextButton.setGravity(17);
        r0.copyTextButton.setSingleLine(true);
        r0.copyTextButton.setEllipsize(TruncateAt.END);
        r0.copyTextButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_dialogButtonSelector), 0));
        r0.copyTextButton.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        r0.copyTextButton.setText(LocaleController.getString("Copy", C0446R.string.Copy).toUpperCase());
        r0.copyTextButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        view.addView(r0.copyTextButton, LayoutHelper.createFrame(-2, -1, 51));
        r0.copyTextButton.setOnClickListener(anonymousClass12);
        r0.openInButton = new TextView(context2);
        r0.openInButton.setTextSize(1, 14.0f);
        r0.openInButton.setTextColor(Theme.getColor(Theme.key_dialogTextBlue4));
        r0.openInButton.setGravity(17);
        r0.openInButton.setSingleLine(true);
        r0.openInButton.setEllipsize(TruncateAt.END);
        r0.openInButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_dialogButtonSelector), 0));
        r0.openInButton.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        r0.openInButton.setText(LocaleController.getString("OpenInBrowser", C0446R.string.OpenInBrowser).toUpperCase());
        r0.openInButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        view.addView(r0.openInButton, LayoutHelper.createFrame(-2, -1, 51));
        r0.openInButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Browser.openUrl(EmbedBottomSheet.this.parentActivity, EmbedBottomSheet.this.openUrl);
                EmbedBottomSheet.this.dismiss();
            }
        });
        setDelegate(new BottomSheetDelegate() {
            public void onOpenAnimationEnd() {
                if (EmbedBottomSheet.this.videoView.loadVideo(EmbedBottomSheet.this.embedUrl, null, EmbedBottomSheet.this.openUrl, true)) {
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
                EmbedBottomSheet.this.videoView.loadVideo(null, null, null, false);
                Map hashMap = new HashMap();
                hashMap.put("Referer", "http://youtube.com");
                try {
                    if (EmbedBottomSheet.this.videoView.getYoutubeId() != null) {
                        int intValue;
                        EmbedBottomSheet.this.progressBarBlackBackground.setVisibility(0);
                        EmbedBottomSheet.this.youtubeLogoImage.setVisibility(0);
                        EmbedBottomSheet.this.youtubeLogoImage.setImageResource(C0446R.drawable.ytlogo);
                        EmbedBottomSheet.this.isYouTube = true;
                        if (VERSION.SDK_INT >= 17) {
                            EmbedBottomSheet.this.webView.addJavascriptInterface(new YoutubeProxy(), "YoutubeProxy");
                        }
                        if (EmbedBottomSheet.this.openUrl != null) {
                            try {
                                Uri parse = Uri.parse(EmbedBottomSheet.this.openUrl);
                                String queryParameter = parse.getQueryParameter("t");
                                if (queryParameter == null) {
                                    queryParameter = parse.getQueryParameter("time_continue");
                                }
                                if (queryParameter != null) {
                                    if (queryParameter.contains("m")) {
                                        String[] split = queryParameter.split("m");
                                        intValue = (Utilities.parseInt(split[0]).intValue() * 60) + Utilities.parseInt(split[1]).intValue();
                                    } else {
                                        intValue = Utilities.parseInt(queryParameter).intValue();
                                    }
                                    EmbedBottomSheet.this.webView.loadDataWithBaseURL("https://www.youtube.com", String.format("<!DOCTYPE html><html><head><style>body { margin: 0; width:100%%; height:100%%;  background-color:#000; }html { width:100%%; height:100%%; background-color:#000; }.embed-container iframe,.embed-container object,   .embed-container embed {       position: absolute;       top: 0;       left: 0;       width: 100%% !important;       height: 100%% !important;   }   </style></head><body>   <div class=\"embed-container\">       <div id=\"player\"></div>   </div>   <script src=\"https://www.youtube.com/iframe_api\"></script>   <script>   var player;   var observer;   var videoEl;   var playing;   var posted = false;   YT.ready(function() {       player = new YT.Player(\"player\", {                              \"width\" : \"100%%\",                              \"events\" : {                              \"onReady\" : \"onReady\",                              \"onError\" : \"onError\",                              },                              \"videoId\" : \"%1$s\",                              \"height\" : \"100%%\",                              \"playerVars\" : {                              \"start\" : %2$d,                              \"rel\" : 0,                              \"showinfo\" : 0,                              \"modestbranding\" : 1,                              \"iv_load_policy\" : 3,                              \"autohide\" : 1,                              \"autoplay\" : 1,                              \"cc_load_policy\" : 1,                              \"playsinline\" : 1,                              \"controls\" : 1                              }                            });        player.setSize(window.innerWidth, window.innerHeight);    });    function hideControls() {        playing = !videoEl.paused;       videoEl.controls = 0;       observer.observe(videoEl, {attributes: true});    }    function showControls() {        playing = !videoEl.paused;       observer.disconnect();       videoEl.controls = 1;    }    function onError(event) {       if (!posted) {            if (window.YoutubeProxy !== undefined) {                   YoutubeProxy.postEvent(\"loaded\", null);             }            posted = true;       }    }    function onReady(event) {       player.playVideo();       videoEl = player.getIframe().contentDocument.getElementsByTagName('video')[0];\n       videoEl.addEventListener(\"canplay\", function() {            if (playing) {               videoEl.play();            }       }, true);       videoEl.addEventListener(\"timeupdate\", function() {            if (!posted && videoEl.currentTime > 0) {               if (window.YoutubeProxy !== undefined) {                   YoutubeProxy.postEvent(\"loaded\", null);                }               posted = true;           }       }, true);       observer = new MutationObserver(function() {\n          if (videoEl.controls) {\n               videoEl.controls = 0;\n          }       });\n    }    window.onresize = function() {        player.setSize(window.innerWidth, window.innerHeight);    }    </script></body></html>", new Object[]{r1, Integer.valueOf(intValue)}), "text/html", C0542C.UTF8_NAME, "http://youtube.com");
                                    return;
                                }
                            } catch (Throwable e) {
                                FileLog.m3e(e);
                            }
                        }
                        intValue = 0;
                        EmbedBottomSheet.this.webView.loadDataWithBaseURL("https://www.youtube.com", String.format("<!DOCTYPE html><html><head><style>body { margin: 0; width:100%%; height:100%%;  background-color:#000; }html { width:100%%; height:100%%; background-color:#000; }.embed-container iframe,.embed-container object,   .embed-container embed {       position: absolute;       top: 0;       left: 0;       width: 100%% !important;       height: 100%% !important;   }   </style></head><body>   <div class=\"embed-container\">       <div id=\"player\"></div>   </div>   <script src=\"https://www.youtube.com/iframe_api\"></script>   <script>   var player;   var observer;   var videoEl;   var playing;   var posted = false;   YT.ready(function() {       player = new YT.Player(\"player\", {                              \"width\" : \"100%%\",                              \"events\" : {                              \"onReady\" : \"onReady\",                              \"onError\" : \"onError\",                              },                              \"videoId\" : \"%1$s\",                              \"height\" : \"100%%\",                              \"playerVars\" : {                              \"start\" : %2$d,                              \"rel\" : 0,                              \"showinfo\" : 0,                              \"modestbranding\" : 1,                              \"iv_load_policy\" : 3,                              \"autohide\" : 1,                              \"autoplay\" : 1,                              \"cc_load_policy\" : 1,                              \"playsinline\" : 1,                              \"controls\" : 1                              }                            });        player.setSize(window.innerWidth, window.innerHeight);    });    function hideControls() {        playing = !videoEl.paused;       videoEl.controls = 0;       observer.observe(videoEl, {attributes: true});    }    function showControls() {        playing = !videoEl.paused;       observer.disconnect();       videoEl.controls = 1;    }    function onError(event) {       if (!posted) {            if (window.YoutubeProxy !== undefined) {                   YoutubeProxy.postEvent(\"loaded\", null);             }            posted = true;       }    }    function onReady(event) {       player.playVideo();       videoEl = player.getIframe().contentDocument.getElementsByTagName('video')[0];\n       videoEl.addEventListener(\"canplay\", function() {            if (playing) {               videoEl.play();            }       }, true);       videoEl.addEventListener(\"timeupdate\", function() {            if (!posted && videoEl.currentTime > 0) {               if (window.YoutubeProxy !== undefined) {                   YoutubeProxy.postEvent(\"loaded\", null);                }               posted = true;           }       }, true);       observer = new MutationObserver(function() {\n          if (videoEl.controls) {\n               videoEl.controls = 0;\n          }       });\n    }    window.onresize = function() {        player.setSize(window.innerWidth, window.innerHeight);    }    </script></body></html>", new Object[]{r1, Integer.valueOf(intValue)}), "text/html", C0542C.UTF8_NAME, "http://youtube.com");
                        return;
                    }
                    EmbedBottomSheet.this.webView.loadUrl(EmbedBottomSheet.this.embedUrl, hashMap);
                } catch (Throwable e2) {
                    FileLog.m3e(e2);
                }
            }

            public boolean canDismiss() {
                if (EmbedBottomSheet.this.videoView.isInFullscreen()) {
                    EmbedBottomSheet.this.videoView.exitFullscreen();
                    return false;
                }
                try {
                    EmbedBottomSheet.this.parentActivity.getWindow().clearFlags(128);
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
                return true;
            }
        });
        r0.orientationEventListener = new OrientationEventListener(ApplicationLoader.applicationContext) {
            public void onOrientationChanged(int i) {
                if (EmbedBottomSheet.this.orientationEventListener != null) {
                    if (EmbedBottomSheet.this.videoView.getVisibility() == 0) {
                        if (EmbedBottomSheet.this.parentActivity != null && EmbedBottomSheet.this.videoView.isInFullscreen() && EmbedBottomSheet.this.fullscreenedByButton) {
                            if (i >= PsExtractor.VIDEO_STREAM_MASK && i <= 300) {
                                EmbedBottomSheet.this.wasInLandscape = true;
                            } else if (EmbedBottomSheet.this.wasInLandscape && (i >= 330 || i <= 30)) {
                                EmbedBottomSheet.this.parentActivity.setRequestedOrientation(EmbedBottomSheet.this.prevOrientation);
                                EmbedBottomSheet.this.fullscreenedByButton = false;
                                EmbedBottomSheet.this.wasInLandscape = false;
                            }
                        }
                    }
                }
            }
        };
        if (r0.orientationEventListener.canDetectOrientation()) {
            r0.orientationEventListener.enable();
        } else {
            r0.orientationEventListener.disable();
            r0.orientationEventListener = null;
        }
        instance = r0;
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
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
    }

    private void showOrHideYoutubeLogo(final boolean z) {
        this.youtubeLogoImage.animate().alpha(z ? 1.0f : 0.0f).setDuration(200).setStartDelay(z ? 0 : 2900).setInterpolator(new DecelerateInterpolator()).setListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                if (z != null) {
                    EmbedBottomSheet.this.showOrHideYoutubeLogo(false);
                }
            }
        }).start();
    }

    public boolean checkInlinePermissions() {
        if (this.parentActivity == null) {
            return false;
        }
        if (VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this.parentActivity)) {
                new Builder(this.parentActivity).setTitle(LocaleController.getString("AppName", C0446R.string.AppName)).setMessage(LocaleController.getString("PermissionDrawAboveOtherApps", C0446R.string.PermissionDrawAboveOtherApps)).setPositiveButton(LocaleController.getString("PermissionOpenSettings", C0446R.string.PermissionOpenSettings), new DialogInterface.OnClickListener() {
                    @TargetApi(23)
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (EmbedBottomSheet.this.parentActivity != null) {
                            dialogInterface = EmbedBottomSheet.this.parentActivity;
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("package:");
                            stringBuilder.append(EmbedBottomSheet.this.parentActivity.getPackageName());
                            dialogInterface.startActivity(new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse(stringBuilder.toString())));
                        }
                    }
                }).show();
                return false;
            }
        }
        return true;
    }

    protected boolean canDismissWithSwipe() {
        if (this.videoView.getVisibility() == 0) {
            if (this.videoView.isInFullscreen()) {
                return false;
            }
        }
        return true;
    }

    public void onConfigurationChanged(Configuration configuration) {
        if (this.videoView.getVisibility() == 0 && this.videoView.isInitied() && !this.videoView.isInline()) {
            if (configuration.orientation == 2) {
                if (this.videoView.isInFullscreen() == null) {
                    this.videoView.enterFullscreen();
                }
            } else if (this.videoView.isInFullscreen() != null) {
                this.videoView.exitFullscreen();
            }
        }
        if (this.pipVideoView != null) {
            this.pipVideoView.onConfigurationChanged();
        }
    }

    public void destroy() {
        if (this.webView != null && this.webView.getVisibility() == 0) {
            this.containerLayout.removeView(this.webView);
            this.webView.stopLoading();
            this.webView.loadUrl("about:blank");
            this.webView.destroy();
        }
        if (this.pipVideoView != null) {
            this.pipVideoView.close();
            this.pipVideoView = null;
        }
        if (this.videoView != null) {
            this.videoView.destroy();
        }
        instance = null;
        dismissInternal();
    }

    public void exitFromPip() {
        if (this.webView != null) {
            if (this.pipVideoView != null) {
                if (ApplicationLoader.mainInterfacePaused) {
                    try {
                        this.parentActivity.startService(new Intent(ApplicationLoader.applicationContext, BringAppForegroundService.class));
                    } catch (Throwable th) {
                        FileLog.m3e(th);
                    }
                }
                if (this.isYouTube) {
                    runJsCode("showControls();");
                }
                ViewGroup viewGroup = (ViewGroup) this.webView.getParent();
                if (viewGroup != null) {
                    viewGroup.removeView(this.webView);
                }
                this.containerLayout.addView(this.webView, 0, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, (float) (84 + (this.hasDescription ? 22 : 0))));
                setShowWithoutAnimation(true);
                show();
                this.pipVideoView.close();
                this.pipVideoView = null;
            }
        }
    }

    public static EmbedBottomSheet getInstance() {
        return instance;
    }

    public void updateTextureViewPosition() {
        View textureImageView;
        this.videoView.getAspectRatioView().getLocationInWindow(this.position);
        int[] iArr = this.position;
        iArr[0] = iArr[0] - getLeftInset();
        if (!(this.videoView.isInline() || this.animationInProgress)) {
            TextureView textureView = this.videoView.getTextureView();
            textureView.setTranslationX((float) this.position[0]);
            textureView.setTranslationY((float) this.position[1]);
            textureImageView = this.videoView.getTextureImageView();
            if (textureImageView != null) {
                textureImageView.setTranslationX((float) this.position[0]);
                textureImageView.setTranslationY((float) this.position[1]);
            }
        }
        textureImageView = this.videoView.getControlsView();
        if (textureImageView.getParent() == this.container) {
            textureImageView.setTranslationY((float) this.position[1]);
        } else {
            textureImageView.setTranslationY(0.0f);
        }
    }

    protected void onContainerTranslationYChanged(float f) {
        updateTextureViewPosition();
    }

    protected boolean onCustomMeasure(View view, int i, int i2) {
        if (view == this.videoView.getControlsView()) {
            view = view.getLayoutParams();
            view.width = this.videoView.getMeasuredWidth();
            view.height = this.videoView.getAspectRatioView().getMeasuredHeight() + (this.videoView.isInFullscreen() ? 0 : AndroidUtilities.dp(10.0f));
        }
        return false;
    }

    protected boolean onCustomLayout(View view, int i, int i2, int i3, int i4) {
        if (view == this.videoView.getControlsView()) {
            updateTextureViewPosition();
        }
        return null;
    }

    public void pause() {
        if (this.videoView != null && this.videoView.isInitied()) {
            this.videoView.pause();
        }
    }

    public void onContainerDraw(Canvas canvas) {
        if (this.waitingForDraw != null) {
            this.waitingForDraw--;
            if (this.waitingForDraw == null) {
                this.videoView.updateTextureImageView();
                this.pipVideoView.close();
                this.pipVideoView = null;
                return;
            }
            this.container.invalidate();
        }
    }
}
