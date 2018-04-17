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
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BringAppForegroundService;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.exoplayer2.C0539C;
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
    class C11271 implements OnShowListener {

        /* renamed from: org.telegram.ui.Components.EmbedBottomSheet$1$1 */
        class C11251 implements OnPreDrawListener {
            C11251() {
            }

            public boolean onPreDraw() {
                EmbedBottomSheet.this.videoView.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        }

        C11271() {
        }

        public void onShow(DialogInterface dialog) {
            if (EmbedBottomSheet.this.pipVideoView != null && EmbedBottomSheet.this.videoView.isInline()) {
                EmbedBottomSheet.this.videoView.getViewTreeObserver().addOnPreDrawListener(new C11251());
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.EmbedBottomSheet$2 */
    class C11282 implements OnTouchListener {
        C11282() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
    }

    /* renamed from: org.telegram.ui.Components.EmbedBottomSheet$4 */
    class C11304 implements OnTouchListener {
        C11304() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
    }

    /* renamed from: org.telegram.ui.Components.EmbedBottomSheet$6 */
    class C11326 extends WebChromeClient {
        C11326() {
        }

        public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
            onShowCustomView(view, callback);
        }

        public void onShowCustomView(View view, CustomViewCallback callback) {
            if (EmbedBottomSheet.this.customView == null) {
                if (EmbedBottomSheet.this.pipVideoView == null) {
                    EmbedBottomSheet.this.exitFromPip();
                    EmbedBottomSheet.this.customView = view;
                    EmbedBottomSheet.this.getSheetContainer().setVisibility(4);
                    EmbedBottomSheet.this.fullscreenVideoContainer.setVisibility(0);
                    EmbedBottomSheet.this.fullscreenVideoContainer.addView(view, LayoutHelper.createFrame(-1, -1.0f));
                    EmbedBottomSheet.this.customViewCallback = callback;
                    return;
                }
            }
            callback.onCustomViewHidden();
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
    class C11337 extends WebViewClient {
        C11337() {
        }

        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }

        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (!EmbedBottomSheet.this.isYouTube || VERSION.SDK_INT < 17) {
                EmbedBottomSheet.this.progressBar.setVisibility(4);
                EmbedBottomSheet.this.progressBarBlackBackground.setVisibility(4);
                EmbedBottomSheet.this.pipButton.setEnabled(true);
                EmbedBottomSheet.this.pipButton.setAlpha(1.0f);
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.EmbedBottomSheet$8 */
    class C11348 implements OnClickListener {
        C11348() {
        }

        public void onClick(View v) {
            if (EmbedBottomSheet.this.youtubeLogoImage.getAlpha() != 0.0f) {
                EmbedBottomSheet.this.openInButton.callOnClick();
            }
        }
    }

    private class YoutubeProxy {
        private YoutubeProxy() {
        }

        @JavascriptInterface
        public void postEvent(final String eventName, String eventData) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    boolean z;
                    String str = eventName;
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
    class C20459 implements WebPlayerViewDelegate {

        /* renamed from: org.telegram.ui.Components.EmbedBottomSheet$9$2 */
        class C11362 extends AnimatorListenerAdapter {
            C11362() {
            }

            public void onAnimationEnd(Animator animation) {
                EmbedBottomSheet.this.animationInProgress = false;
            }
        }

        C20459() {
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
            HashMap<String, String> args = new HashMap();
            args.put("Referer", "http://youtube.com");
            try {
                EmbedBottomSheet.this.webView.loadUrl(EmbedBottomSheet.this.embedUrl, args);
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }

        public TextureView onSwitchToFullscreen(View controlsView, boolean fullscreen, float aspectRatio, int rotation, boolean byButton) {
            if (fullscreen) {
                EmbedBottomSheet.this.fullscreenVideoContainer.setVisibility(0);
                EmbedBottomSheet.this.fullscreenVideoContainer.setAlpha(1.0f);
                EmbedBottomSheet.this.fullscreenVideoContainer.addView(EmbedBottomSheet.this.videoView.getAspectRatioView());
                EmbedBottomSheet.this.wasInLandscape = false;
                EmbedBottomSheet.this.fullscreenedByButton = byButton;
                if (EmbedBottomSheet.this.parentActivity != null) {
                    try {
                        EmbedBottomSheet.this.prevOrientation = EmbedBottomSheet.this.parentActivity.getRequestedOrientation();
                        if (byButton) {
                            if (((WindowManager) EmbedBottomSheet.this.parentActivity.getSystemService("window")).getDefaultDisplay().getRotation() == 3) {
                                EmbedBottomSheet.this.parentActivity.setRequestedOrientation(8);
                            } else {
                                EmbedBottomSheet.this.parentActivity.setRequestedOrientation(0);
                            }
                        }
                        EmbedBottomSheet.this.containerView.setSystemUiVisibility(1028);
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                }
            } else {
                EmbedBottomSheet.this.fullscreenVideoContainer.setVisibility(4);
                EmbedBottomSheet.this.fullscreenedByButton = false;
                if (EmbedBottomSheet.this.parentActivity != null) {
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

        public void onVideoSizeChanged(float aspectRatio, int rotation) {
        }

        public void onInlineSurfaceTextureReady() {
            if (EmbedBottomSheet.this.videoView.isInline()) {
                EmbedBottomSheet.this.dismissInternal();
            }
        }

        public void prepareToSwitchInlineMode(boolean inline, Runnable switchInlineModeRunnable, float aspectRatio, boolean animated) {
            C20459 c20459 = this;
            final Runnable runnable;
            if (inline) {
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
                if (animated) {
                    TextureView textureView = EmbedBottomSheet.this.videoView.getTextureView();
                    View controlsView = EmbedBottomSheet.this.videoView.getControlsView();
                    ImageView textureImageView = EmbedBottomSheet.this.videoView.getTextureImageView();
                    Rect rect = PipVideoView.getPipRect(aspectRatio);
                    float scale = rect.width / ((float) textureView.getWidth());
                    if (VERSION.SDK_INT >= 21) {
                        rect.f27y += (float) AndroidUtilities.statusBarHeight;
                    }
                    AnimatorSet animatorSet = new AnimatorSet();
                    r12 = new Animator[12];
                    r12[0] = ObjectAnimator.ofFloat(textureImageView, "scaleX", new float[]{scale});
                    r12[1] = ObjectAnimator.ofFloat(textureImageView, "scaleY", new float[]{scale});
                    r12[2] = ObjectAnimator.ofFloat(textureImageView, "translationX", new float[]{rect.f26x});
                    r12[3] = ObjectAnimator.ofFloat(textureImageView, "translationY", new float[]{rect.f27y});
                    r12[4] = ObjectAnimator.ofFloat(textureView, "scaleX", new float[]{scale});
                    r12[5] = ObjectAnimator.ofFloat(textureView, "scaleY", new float[]{scale});
                    r12[6] = ObjectAnimator.ofFloat(textureView, "translationX", new float[]{rect.f26x});
                    r12[7] = ObjectAnimator.ofFloat(textureView, "translationY", new float[]{rect.f27y});
                    r12[8] = ObjectAnimator.ofFloat(EmbedBottomSheet.this.containerView, "translationY", new float[]{(float) (EmbedBottomSheet.this.containerView.getMeasuredHeight() + AndroidUtilities.dp(10.0f))});
                    r12[9] = ObjectAnimator.ofInt(EmbedBottomSheet.this.backDrawable, "alpha", new int[]{0});
                    r12[10] = ObjectAnimator.ofFloat(EmbedBottomSheet.this.fullscreenVideoContainer, "alpha", new float[]{0.0f});
                    r12[11] = ObjectAnimator.ofFloat(controlsView, "alpha", new float[]{0.0f});
                    animatorSet.playTogether(r12);
                    animatorSet.setInterpolator(new DecelerateInterpolator());
                    animatorSet.setDuration(250);
                    runnable = switchInlineModeRunnable;
                    animatorSet.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            if (EmbedBottomSheet.this.fullscreenVideoContainer.getVisibility() == 0) {
                                EmbedBottomSheet.this.fullscreenVideoContainer.setAlpha(1.0f);
                                EmbedBottomSheet.this.fullscreenVideoContainer.setVisibility(4);
                            }
                            runnable.run();
                        }
                    });
                    animatorSet.start();
                    return;
                }
                runnable = switchInlineModeRunnable;
                if (EmbedBottomSheet.this.fullscreenVideoContainer.getVisibility() == 0) {
                    EmbedBottomSheet.this.fullscreenVideoContainer.setAlpha(1.0f);
                    EmbedBottomSheet.this.fullscreenVideoContainer.setVisibility(4);
                }
                switchInlineModeRunnable.run();
                EmbedBottomSheet.this.dismissInternal();
                return;
            }
            runnable = switchInlineModeRunnable;
            if (ApplicationLoader.mainInterfacePaused) {
                try {
                    EmbedBottomSheet.this.parentActivity.startService(new Intent(ApplicationLoader.applicationContext, BringAppForegroundService.class));
                } catch (Throwable e2) {
                    FileLog.m3e(e2);
                }
            }
            if (animated) {
                EmbedBottomSheet.this.setOnShowListener(EmbedBottomSheet.this.onShowListener);
                Rect rect2 = PipVideoView.getPipRect(aspectRatio);
                TextureView textureView2 = EmbedBottomSheet.this.videoView.getTextureView();
                ImageView textureImageView2 = EmbedBottomSheet.this.videoView.getTextureImageView();
                float scale2 = rect2.width / ((float) textureView2.getLayoutParams().width);
                if (VERSION.SDK_INT >= 21) {
                    rect2.f27y += (float) AndroidUtilities.statusBarHeight;
                }
                textureImageView2.setScaleX(scale2);
                textureImageView2.setScaleY(scale2);
                textureImageView2.setTranslationX(rect2.f26x);
                textureImageView2.setTranslationY(rect2.f27y);
                textureView2.setScaleX(scale2);
                textureView2.setScaleY(scale2);
                textureView2.setTranslationX(rect2.f26x);
                textureView2.setTranslationY(rect2.f27y);
            } else {
                EmbedBottomSheet.this.pipVideoView.close();
                EmbedBottomSheet.this.pipVideoView = null;
            }
            EmbedBottomSheet.this.setShowWithoutAnimation(true);
            EmbedBottomSheet.this.show();
            if (animated) {
                EmbedBottomSheet.this.waitingForDraw = 4;
                EmbedBottomSheet.this.backDrawable.setAlpha(1);
                EmbedBottomSheet.this.containerView.setTranslationY((float) (EmbedBottomSheet.this.containerView.getMeasuredHeight() + AndroidUtilities.dp(10.0f)));
            }
        }

        public TextureView onSwitchInlineMode(View controlsView, boolean inline, float aspectRatio, int rotation, boolean animated) {
            C20459 c20459 = this;
            if (inline) {
                View view = controlsView;
                view.setTranslationY(0.0f);
                EmbedBottomSheet.this.pipVideoView = new PipVideoView();
                return EmbedBottomSheet.this.pipVideoView.show(EmbedBottomSheet.this.parentActivity, EmbedBottomSheet.this, view, aspectRatio, rotation, null);
            }
            view = controlsView;
            if (animated) {
                EmbedBottomSheet.this.animationInProgress = true;
                EmbedBottomSheet.this.videoView.getAspectRatioView().getLocationInWindow(EmbedBottomSheet.this.position);
                int[] access$3900 = EmbedBottomSheet.this.position;
                access$3900[0] = access$3900[0] - EmbedBottomSheet.this.getLeftInset();
                access$3900 = EmbedBottomSheet.this.position;
                access$3900[1] = (int) (((float) access$3900[1]) - EmbedBottomSheet.this.containerView.getTranslationY());
                TextureView textureView = EmbedBottomSheet.this.videoView.getTextureView();
                ImageView textureImageView = EmbedBottomSheet.this.videoView.getTextureImageView();
                AnimatorSet animatorSet = new AnimatorSet();
                Animator[] animatorArr = new Animator[10];
                animatorArr[0] = ObjectAnimator.ofFloat(textureImageView, "scaleX", new float[]{1.0f});
                animatorArr[1] = ObjectAnimator.ofFloat(textureImageView, "scaleY", new float[]{1.0f});
                animatorArr[2] = ObjectAnimator.ofFloat(textureImageView, "translationX", new float[]{(float) EmbedBottomSheet.this.position[0]});
                animatorArr[3] = ObjectAnimator.ofFloat(textureImageView, "translationY", new float[]{(float) EmbedBottomSheet.this.position[1]});
                animatorArr[4] = ObjectAnimator.ofFloat(textureView, "scaleX", new float[]{1.0f});
                animatorArr[5] = ObjectAnimator.ofFloat(textureView, "scaleY", new float[]{1.0f});
                animatorArr[6] = ObjectAnimator.ofFloat(textureView, "translationX", new float[]{(float) EmbedBottomSheet.this.position[0]});
                animatorArr[7] = ObjectAnimator.ofFloat(textureView, "translationY", new float[]{(float) EmbedBottomSheet.this.position[1]});
                animatorArr[8] = ObjectAnimator.ofFloat(EmbedBottomSheet.this.containerView, "translationY", new float[]{0.0f});
                animatorArr[9] = ObjectAnimator.ofInt(EmbedBottomSheet.this.backDrawable, "alpha", new int[]{51});
                animatorSet.playTogether(animatorArr);
                animatorSet.setInterpolator(new DecelerateInterpolator());
                animatorSet.setDuration(250);
                animatorSet.addListener(new C11362());
                animatorSet.start();
            } else {
                EmbedBottomSheet.this.containerView.setTranslationY(0.0f);
            }
            return null;
        }

        public void onSharePressed() {
        }

        public void onPlayStateChanged(WebPlayerView playerView, boolean playing) {
            if (playing) {
                try {
                    EmbedBottomSheet.this.parentActivity.getWindow().addFlags(128);
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
                return;
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

    public static void show(Context context, String title, String description, String originalUrl, String url, int w, int h) {
        if (instance != null) {
            instance.destroy();
        }
        new EmbedBottomSheet(context, title, description, originalUrl, url, w, h).show();
    }

    @SuppressLint({"SetJavaScriptEnabled"})
    private EmbedBottomSheet(Context context, String title, String description, String originalUrl, String url, int w, int h) {
        TextView textView;
        Context context2 = context;
        String str = description;
        super(context2, false);
        this.position = new int[2];
        this.lastOrientation = -1;
        this.prevOrientation = -2;
        this.youtubeFrame = "<!DOCTYPE html><html><head><style>body { margin: 0; width:100%%; height:100%%;  background-color:#000; }html { width:100%%; height:100%%; background-color:#000; }.embed-container iframe,.embed-container object,   .embed-container embed {       position: absolute;       top: 0;       left: 0;       width: 100%% !important;       height: 100%% !important;   }   </style></head><body>   <div class=\"embed-container\">       <div id=\"player\"></div>   </div>   <script src=\"https://www.youtube.com/iframe_api\"></script>   <script>   var player;   var observer;   var videoEl;   var playing;   var posted = false;   YT.ready(function() {       player = new YT.Player(\"player\", {                              \"width\" : \"100%%\",                              \"events\" : {                              \"onReady\" : \"onReady\",                              \"onError\" : \"onError\",                              },                              \"videoId\" : \"%1$s\",                              \"height\" : \"100%%\",                              \"playerVars\" : {                              \"start\" : %2$d,                              \"rel\" : 0,                              \"showinfo\" : 0,                              \"modestbranding\" : 1,                              \"iv_load_policy\" : 3,                              \"autohide\" : 1,                              \"autoplay\" : 1,                              \"cc_load_policy\" : 1,                              \"playsinline\" : 1,                              \"controls\" : 1                              }                            });        player.setSize(window.innerWidth, window.innerHeight);    });    function hideControls() {        playing = !videoEl.paused;       videoEl.controls = 0;       observer.observe(videoEl, {attributes: true});    }    function showControls() {        playing = !videoEl.paused;       observer.disconnect();       videoEl.controls = 1;    }    function onError(event) {       if (!posted) {            if (window.YoutubeProxy !== undefined) {                   YoutubeProxy.postEvent(\"loaded\", null);             }            posted = true;       }    }    function onReady(event) {       player.playVideo();       videoEl = player.getIframe().contentDocument.getElementsByTagName('video')[0];\n       videoEl.addEventListener(\"canplay\", function() {            if (playing) {               videoEl.play();            }       }, true);       videoEl.addEventListener(\"timeupdate\", function() {            if (!posted && videoEl.currentTime > 0) {               if (window.YoutubeProxy !== undefined) {                   YoutubeProxy.postEvent(\"loaded\", null);                }               posted = true;           }       }, true);       observer = new MutationObserver(function() {\n          if (videoEl.controls) {\n               videoEl.controls = 0;\n          }       });\n    }    window.onresize = function() {        player.setSize(window.innerWidth, window.innerHeight);    }    </script></body></html>";
        this.onShowListener = new C11271();
        this.fullWidth = true;
        setApplyTopPadding(false);
        setApplyBottomPadding(false);
        if (context2 instanceof Activity) {
            r0.parentActivity = (Activity) context2;
        }
        r0.embedUrl = url;
        boolean z = str != null && description.length() > 0;
        r0.hasDescription = z;
        r0.openUrl = originalUrl;
        r0.width = w;
        r0.height = h;
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
        r0.fullscreenVideoContainer.setOnTouchListener(new C11282());
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

            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec((AndroidUtilities.dp((float) (84 + (EmbedBottomSheet.this.hasDescription ? 22 : 0))) + ((int) Math.min(((float) EmbedBottomSheet.this.height) / (((float) EmbedBottomSheet.this.width) / ((float) MeasureSpec.getSize(widthMeasureSpec))), (float) (AndroidUtilities.displaySize.y / 2)))) + 1, NUM));
            }
        };
        r0.containerLayout.setOnTouchListener(new C11304());
        setCustomView(r0.containerLayout);
        r0.webView = new WebView(context2) {
            public boolean onTouchEvent(MotionEvent event) {
                if (EmbedBottomSheet.this.isYouTube && event.getAction() == 0) {
                    EmbedBottomSheet.this.showOrHideYoutubeLogo(true);
                }
                return super.onTouchEvent(event);
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
        r0.webView.setWebChromeClient(new C11326());
        r0.webView.setWebViewClient(new C11337());
        int i = 22;
        r0.containerLayout.addView(r0.webView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, (float) (84 + (r0.hasDescription ? 22 : 0))));
        r0.youtubeLogoImage = new ImageView(context2);
        r0.youtubeLogoImage.setVisibility(8);
        r0.containerLayout.addView(r0.youtubeLogoImage, LayoutHelper.createFrame(66, 28.0f, 53, 0.0f, 8.0f, 8.0f, 0.0f));
        r0.youtubeLogoImage.setOnClickListener(new C11348());
        r0.videoView = new WebPlayerView(context2, true, false, new C20459());
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
            i = 0;
        }
        frameLayout.addView(view, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 0.0f, 0.0f, (float) ((84 + i) / 2)));
        if (r0.hasDescription) {
            textView = new TextView(context2);
            textView.setTextSize(1, 16.0f);
            textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
            textView.setText(str);
            textView.setSingleLine(true);
            textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            textView.setEllipsize(TruncateAt.END);
            textView.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
            r0.containerLayout.addView(textView, LayoutHelper.createFrame(-1, -2.0f, 83, 0.0f, 0.0f, 0.0f, 77.0f));
        }
        textView = new TextView(context2);
        textView.setTextSize(1, 14.0f);
        textView.setTextColor(Theme.getColor(Theme.key_dialogTextGray));
        textView.setText(title);
        textView.setSingleLine(true);
        textView.setEllipsize(TruncateAt.END);
        textView.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        r0.containerLayout.addView(textView, LayoutHelper.createFrame(-1, -2.0f, 83, 0.0f, 0.0f, 0.0f, 57.0f));
        View lineView = new View(context2);
        lineView.setBackgroundColor(Theme.getColor(Theme.key_dialogGrayLine));
        r0.containerLayout.addView(lineView, new LayoutParams(-1, 1, 83));
        ((LayoutParams) lineView.getLayoutParams()).bottomMargin = AndroidUtilities.dp(48.0f);
        frameLayout = new FrameLayout(context2);
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground));
        r0.containerLayout.addView(frameLayout, LayoutHelper.createFrame(-1, 48, 83));
        LinearLayout linearLayout = new LinearLayout(context2);
        linearLayout.setOrientation(0);
        linearLayout.setWeightSum(1.0f);
        frameLayout.addView(linearLayout, LayoutHelper.createFrame(-2, -1, 53));
        textView = new TextView(context2);
        textView.setTextSize(1, 14.0f);
        textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlue4));
        textView.setGravity(17);
        textView.setSingleLine(true);
        textView.setEllipsize(TruncateAt.END);
        textView.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_dialogButtonSelector), 0));
        textView.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        textView.setText(LocaleController.getString("Close", R.string.Close).toUpperCase());
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        frameLayout.addView(textView, LayoutHelper.createLinear(-2, -1, 51));
        textView.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                EmbedBottomSheet.this.dismiss();
            }
        });
        r0.imageButtonsContainer = new LinearLayout(context2);
        r0.imageButtonsContainer.setVisibility(4);
        frameLayout.addView(r0.imageButtonsContainer, LayoutHelper.createFrame(-2, -1, 17));
        r0.pipButton = new ImageView(context2);
        r0.pipButton.setScaleType(ScaleType.CENTER);
        r0.pipButton.setImageResource(R.drawable.video_pip);
        r0.pipButton.setEnabled(false);
        r0.pipButton.setAlpha(0.5f);
        r0.pipButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogTextBlue4), Mode.MULTIPLY));
        r0.pipButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_dialogButtonSelector), 0));
        r0.imageButtonsContainer.addView(r0.pipButton, LayoutHelper.createFrame(48, 48.0f, 51, 0.0f, 0.0f, 4.0f, 0.0f));
        r0.pipButton.setOnClickListener(new OnClickListener() {

            /* renamed from: org.telegram.ui.Components.EmbedBottomSheet$11$1 */
            class C11261 extends AnimatorListenerAdapter {
                C11261() {
                }

                public void onAnimationEnd(Animator animation) {
                    EmbedBottomSheet.this.animationInProgress = false;
                }
            }

            public void onClick(View v) {
                if (EmbedBottomSheet.this.checkInlinePermissions() && EmbedBottomSheet.this.progressBar.getVisibility() != 0) {
                    EmbedBottomSheet.this.pipVideoView = new PipVideoView();
                    PipVideoView access$400 = EmbedBottomSheet.this.pipVideoView;
                    Activity access$2200 = EmbedBottomSheet.this.parentActivity;
                    EmbedBottomSheet embedBottomSheet = EmbedBottomSheet.this;
                    float access$800 = (EmbedBottomSheet.this.width == 0 || EmbedBottomSheet.this.height == 0) ? 1.0f : ((float) EmbedBottomSheet.this.width) / ((float) EmbedBottomSheet.this.height);
                    access$400.show(access$2200, embedBottomSheet, null, access$800, 0, EmbedBottomSheet.this.webView);
                    if (EmbedBottomSheet.this.isYouTube) {
                        EmbedBottomSheet.this.runJsCode("hideControls();");
                    }
                    if (false) {
                        EmbedBottomSheet.this.animationInProgress = true;
                        EmbedBottomSheet.this.videoView.getAspectRatioView().getLocationInWindow(EmbedBottomSheet.this.position);
                        int[] access$3900 = EmbedBottomSheet.this.position;
                        access$3900[0] = access$3900[0] - EmbedBottomSheet.this.getLeftInset();
                        access$3900 = EmbedBottomSheet.this.position;
                        access$3900[1] = (int) (((float) access$3900[1]) - EmbedBottomSheet.this.containerView.getTranslationY());
                        TextureView textureView = EmbedBottomSheet.this.videoView.getTextureView();
                        ImageView textureImageView = EmbedBottomSheet.this.videoView.getTextureImageView();
                        AnimatorSet animatorSet = new AnimatorSet();
                        Animator[] animatorArr = new Animator[10];
                        animatorArr[0] = ObjectAnimator.ofFloat(textureImageView, "scaleX", new float[]{1.0f});
                        animatorArr[1] = ObjectAnimator.ofFloat(textureImageView, "scaleY", new float[]{1.0f});
                        animatorArr[2] = ObjectAnimator.ofFloat(textureImageView, "translationX", new float[]{(float) EmbedBottomSheet.this.position[0]});
                        animatorArr[3] = ObjectAnimator.ofFloat(textureImageView, "translationY", new float[]{(float) EmbedBottomSheet.this.position[1]});
                        animatorArr[4] = ObjectAnimator.ofFloat(textureView, "scaleX", new float[]{1.0f});
                        animatorArr[5] = ObjectAnimator.ofFloat(textureView, "scaleY", new float[]{1.0f});
                        animatorArr[6] = ObjectAnimator.ofFloat(textureView, "translationX", new float[]{(float) EmbedBottomSheet.this.position[0]});
                        animatorArr[7] = ObjectAnimator.ofFloat(textureView, "translationY", new float[]{(float) EmbedBottomSheet.this.position[1]});
                        animatorArr[8] = ObjectAnimator.ofFloat(EmbedBottomSheet.this.containerView, "translationY", new float[]{0.0f});
                        animatorArr[9] = ObjectAnimator.ofInt(EmbedBottomSheet.this.backDrawable, "alpha", new int[]{51});
                        animatorSet.playTogether(animatorArr);
                        animatorSet.setInterpolator(new DecelerateInterpolator());
                        animatorSet.setDuration(250);
                        animatorSet.addListener(new C11261());
                        animatorSet.start();
                    } else {
                        EmbedBottomSheet.this.containerView.setTranslationY(0.0f);
                    }
                    EmbedBottomSheet.this.dismissInternal();
                }
            }
        });
        OnClickListener copyClickListener = new OnClickListener() {
            public void onClick(View v) {
                try {
                    ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", EmbedBottomSheet.this.openUrl));
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
                Toast.makeText(EmbedBottomSheet.this.getContext(), LocaleController.getString("LinkCopied", R.string.LinkCopied), 0).show();
                EmbedBottomSheet.this.dismiss();
            }
        };
        ImageView copyButton = new ImageView(context2);
        copyButton.setScaleType(ScaleType.CENTER);
        copyButton.setImageResource(R.drawable.video_copy);
        copyButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogTextBlue4), Mode.MULTIPLY));
        copyButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_dialogButtonSelector), 0));
        r0.imageButtonsContainer.addView(copyButton, LayoutHelper.createFrame(48, 48, 51));
        copyButton.setOnClickListener(copyClickListener);
        r0.copyTextButton = new TextView(context2);
        r0.copyTextButton.setTextSize(1, 14.0f);
        r0.copyTextButton.setTextColor(Theme.getColor(Theme.key_dialogTextBlue4));
        r0.copyTextButton.setGravity(17);
        r0.copyTextButton.setSingleLine(true);
        r0.copyTextButton.setEllipsize(TruncateAt.END);
        r0.copyTextButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_dialogButtonSelector), 0));
        r0.copyTextButton.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        r0.copyTextButton.setText(LocaleController.getString("Copy", R.string.Copy).toUpperCase());
        r0.copyTextButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        linearLayout.addView(r0.copyTextButton, LayoutHelper.createFrame(-2, -1, 51));
        r0.copyTextButton.setOnClickListener(copyClickListener);
        r0.openInButton = new TextView(context2);
        r0.openInButton.setTextSize(1, 14.0f);
        r0.openInButton.setTextColor(Theme.getColor(Theme.key_dialogTextBlue4));
        r0.openInButton.setGravity(17);
        r0.openInButton.setSingleLine(true);
        r0.openInButton.setEllipsize(TruncateAt.END);
        r0.openInButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_dialogButtonSelector), 0));
        r0.openInButton.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        r0.openInButton.setText(LocaleController.getString("OpenInBrowser", R.string.OpenInBrowser).toUpperCase());
        r0.openInButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        linearLayout.addView(r0.openInButton, LayoutHelper.createFrame(-2, -1, 51));
        r0.openInButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
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
                HashMap<String, String> args = new HashMap();
                args.put("Referer", "http://youtube.com");
                try {
                    if (EmbedBottomSheet.this.videoView.getYoutubeId() != null) {
                        EmbedBottomSheet.this.progressBarBlackBackground.setVisibility(0);
                        EmbedBottomSheet.this.youtubeLogoImage.setVisibility(0);
                        EmbedBottomSheet.this.youtubeLogoImage.setImageResource(R.drawable.ytlogo);
                        EmbedBottomSheet.this.isYouTube = true;
                        if (VERSION.SDK_INT >= 17) {
                            EmbedBottomSheet.this.webView.addJavascriptInterface(new YoutubeProxy(), "YoutubeProxy");
                        }
                        int seekToTime = 0;
                        if (EmbedBottomSheet.this.openUrl != null) {
                            try {
                                Uri uri = Uri.parse(EmbedBottomSheet.this.openUrl);
                                String t = uri.getQueryParameter("t");
                                if (t == null) {
                                    t = uri.getQueryParameter("time_continue");
                                }
                                if (t != null) {
                                    if (t.contains("m")) {
                                        String[] arg = t.split("m");
                                        seekToTime = (Utilities.parseInt(arg[0]).intValue() * 60) + Utilities.parseInt(arg[1]).intValue();
                                    } else {
                                        seekToTime = Utilities.parseInt(t).intValue();
                                    }
                                }
                            } catch (Throwable e) {
                                FileLog.m3e(e);
                            }
                        }
                        EmbedBottomSheet.this.webView.loadDataWithBaseURL("https://www.youtube.com", String.format("<!DOCTYPE html><html><head><style>body { margin: 0; width:100%%; height:100%%;  background-color:#000; }html { width:100%%; height:100%%; background-color:#000; }.embed-container iframe,.embed-container object,   .embed-container embed {       position: absolute;       top: 0;       left: 0;       width: 100%% !important;       height: 100%% !important;   }   </style></head><body>   <div class=\"embed-container\">       <div id=\"player\"></div>   </div>   <script src=\"https://www.youtube.com/iframe_api\"></script>   <script>   var player;   var observer;   var videoEl;   var playing;   var posted = false;   YT.ready(function() {       player = new YT.Player(\"player\", {                              \"width\" : \"100%%\",                              \"events\" : {                              \"onReady\" : \"onReady\",                              \"onError\" : \"onError\",                              },                              \"videoId\" : \"%1$s\",                              \"height\" : \"100%%\",                              \"playerVars\" : {                              \"start\" : %2$d,                              \"rel\" : 0,                              \"showinfo\" : 0,                              \"modestbranding\" : 1,                              \"iv_load_policy\" : 3,                              \"autohide\" : 1,                              \"autoplay\" : 1,                              \"cc_load_policy\" : 1,                              \"playsinline\" : 1,                              \"controls\" : 1                              }                            });        player.setSize(window.innerWidth, window.innerHeight);    });    function hideControls() {        playing = !videoEl.paused;       videoEl.controls = 0;       observer.observe(videoEl, {attributes: true});    }    function showControls() {        playing = !videoEl.paused;       observer.disconnect();       videoEl.controls = 1;    }    function onError(event) {       if (!posted) {            if (window.YoutubeProxy !== undefined) {                   YoutubeProxy.postEvent(\"loaded\", null);             }            posted = true;       }    }    function onReady(event) {       player.playVideo();       videoEl = player.getIframe().contentDocument.getElementsByTagName('video')[0];\n       videoEl.addEventListener(\"canplay\", function() {            if (playing) {               videoEl.play();            }       }, true);       videoEl.addEventListener(\"timeupdate\", function() {            if (!posted && videoEl.currentTime > 0) {               if (window.YoutubeProxy !== undefined) {                   YoutubeProxy.postEvent(\"loaded\", null);                }               posted = true;           }       }, true);       observer = new MutationObserver(function() {\n          if (videoEl.controls) {\n               videoEl.controls = 0;\n          }       });\n    }    window.onresize = function() {        player.setSize(window.innerWidth, window.innerHeight);    }    </script></body></html>", new Object[]{currentYoutubeId, Integer.valueOf(seekToTime)}), "text/html", C0539C.UTF8_NAME, "http://youtube.com");
                    } else {
                        EmbedBottomSheet.this.webView.loadUrl(EmbedBottomSheet.this.embedUrl, args);
                    }
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
            public void onOrientationChanged(int orientation) {
                if (EmbedBottomSheet.this.orientationEventListener != null) {
                    if (EmbedBottomSheet.this.videoView.getVisibility() == 0) {
                        if (EmbedBottomSheet.this.parentActivity != null && EmbedBottomSheet.this.videoView.isInFullscreen() && EmbedBottomSheet.this.fullscreenedByButton) {
                            if (orientation >= PsExtractor.VIDEO_STREAM_MASK && orientation <= 300) {
                                EmbedBottomSheet.this.wasInLandscape = true;
                            } else if (EmbedBottomSheet.this.wasInLandscape && (orientation >= 330 || orientation <= 30)) {
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

    private void runJsCode(String code) {
        if (VERSION.SDK_INT >= 21) {
            this.webView.evaluateJavascript(code, null);
            return;
        }
        try {
            WebView webView = this.webView;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("javascript:");
            stringBuilder.append(code);
            webView.loadUrl(stringBuilder.toString());
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
    }

    private void showOrHideYoutubeLogo(final boolean show) {
        this.youtubeLogoImage.animate().alpha(show ? 1.0f : 0.0f).setDuration(200).setStartDelay(show ? 0 : 2900).setInterpolator(new DecelerateInterpolator()).setListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                if (show) {
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
                new Builder(this.parentActivity).setTitle(LocaleController.getString("AppName", R.string.AppName)).setMessage(LocaleController.getString("PermissionDrawAboveOtherApps", R.string.PermissionDrawAboveOtherApps)).setPositiveButton(LocaleController.getString("PermissionOpenSettings", R.string.PermissionOpenSettings), new DialogInterface.OnClickListener() {
                    @TargetApi(23)
                    public void onClick(DialogInterface dialog, int which) {
                        if (EmbedBottomSheet.this.parentActivity != null) {
                            Activity access$2200 = EmbedBottomSheet.this.parentActivity;
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("package:");
                            stringBuilder.append(EmbedBottomSheet.this.parentActivity.getPackageName());
                            access$2200.startActivity(new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse(stringBuilder.toString())));
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
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                }
                if (this.isYouTube) {
                    runJsCode("showControls();");
                }
                ViewGroup parent = (ViewGroup) this.webView.getParent();
                if (parent != null) {
                    parent.removeView(this.webView);
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
        this.videoView.getAspectRatioView().getLocationInWindow(this.position);
        int[] iArr = this.position;
        iArr[0] = iArr[0] - getLeftInset();
        if (!(this.videoView.isInline() || this.animationInProgress)) {
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

    protected void onContainerTranslationYChanged(float translationY) {
        updateTextureViewPosition();
    }

    protected boolean onCustomMeasure(View view, int width, int height) {
        if (view == this.videoView.getControlsView()) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.width = this.videoView.getMeasuredWidth();
            layoutParams.height = this.videoView.getAspectRatioView().getMeasuredHeight() + (this.videoView.isInFullscreen() ? 0 : AndroidUtilities.dp(10.0f));
        }
        return false;
    }

    protected boolean onCustomLayout(View view, int left, int top, int right, int bottom) {
        if (view == this.videoView.getControlsView()) {
            updateTextureViewPosition();
        }
        return false;
    }

    public void pause() {
        if (this.videoView != null && this.videoView.isInitied()) {
            this.videoView.pause();
        }
    }

    public void onContainerDraw(Canvas canvas) {
        if (this.waitingForDraw != 0) {
            this.waitingForDraw--;
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
