package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build.VERSION;
import android.provider.Settings;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.TextureView;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BringAppForegroundService;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.exoplayer2.extractor.ts.PsExtractor;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.BottomSheet.BottomSheetDelegate;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.WebPlayerView.WebPlayerViewDelegate;

@TargetApi(16)
public class EmbedBottomSheet extends BottomSheet {
    @SuppressLint({"StaticFieldLeak"})
    private static EmbedBottomSheet instance;
    private boolean animationInProgress;
    private View customView;
    private CustomViewCallback customViewCallback;
    private String embedUrl;
    private FrameLayout fullscreenVideoContainer;
    private boolean fullscreenedByButton;
    private boolean hasDescription;
    private int height;
    private int lastOrientation;
    private OnShowListener onShowListener;
    private String openUrl;
    private OrientationEventListener orientationEventListener;
    private Activity parentActivity;
    private PipVideoView pipVideoView;
    private int[] position;
    private int prevOrientation;
    private ProgressBar progressBar;
    private WebPlayerView videoView;
    private boolean wasInLandscape;
    private WebView webView;
    private int width;

    public static void show(Context context, String title, String description, String originalUrl, String url, int w, int h) {
        if (instance != null) {
            instance.destroy();
        }
        new EmbedBottomSheet(context, title, description, originalUrl, url, w, h).show();
    }

    @SuppressLint({"SetJavaScriptEnabled"})
    private EmbedBottomSheet(Context context, String title, String description, String originalUrl, String url, int w, int h) {
        TextView textView;
        super(context, false);
        this.position = new int[2];
        this.lastOrientation = -1;
        this.prevOrientation = -2;
        this.onShowListener = new OnShowListener() {
            public void onShow(DialogInterface dialog) {
                if (EmbedBottomSheet.this.pipVideoView != null && EmbedBottomSheet.this.videoView.isInline()) {
                    EmbedBottomSheet.this.videoView.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                        public boolean onPreDraw() {
                            EmbedBottomSheet.this.videoView.getViewTreeObserver().removeOnPreDrawListener(this);
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    EmbedBottomSheet.this.videoView.updateTextureImageView();
                                    EmbedBottomSheet.this.pipVideoView.close();
                                    EmbedBottomSheet.this.pipVideoView = null;
                                }
                            }, 100);
                            return true;
                        }
                    });
                }
            }
        };
        this.fullWidth = true;
        setApplyTopPadding(false);
        setApplyBottomPadding(false);
        if (context instanceof Activity) {
            this.parentActivity = (Activity) context;
        }
        this.embedUrl = url;
        boolean z = description != null && description.length() > 0;
        this.hasDescription = z;
        this.openUrl = originalUrl;
        this.width = w;
        this.height = h;
        if (this.width == 0 || this.height == 0) {
            this.width = AndroidUtilities.displaySize.x;
            this.height = AndroidUtilities.displaySize.y / 2;
        }
        this.fullscreenVideoContainer = new FrameLayout(context);
        this.fullscreenVideoContainer.setBackgroundColor(-16777216);
        if (VERSION.SDK_INT >= 21) {
            this.fullscreenVideoContainer.setFitsSystemWindows(true);
        }
        this.container.addView(this.fullscreenVideoContainer, LayoutHelper.createFrame(-1, -1.0f));
        this.fullscreenVideoContainer.setVisibility(4);
        this.fullscreenVideoContainer.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        FrameLayout containerLayout = new FrameLayout(context) {
            protected void onDetachedFromWindow() {
                super.onDetachedFromWindow();
                try {
                    if (EmbedBottomSheet.this.webView.getParent() != null) {
                        removeView(EmbedBottomSheet.this.webView);
                        EmbedBottomSheet.this.webView.stopLoading();
                        EmbedBottomSheet.this.webView.loadUrl("about:blank");
                        EmbedBottomSheet.this.webView.destroy();
                    }
                    if (!EmbedBottomSheet.this.videoView.isInline()) {
                        if (EmbedBottomSheet.instance == EmbedBottomSheet.this) {
                            EmbedBottomSheet.instance = null;
                        }
                        EmbedBottomSheet.this.videoView.destroy();
                    }
                } catch (Throwable e) {
                    FileLog.e("tmessages", e);
                }
            }

            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec((AndroidUtilities.dp((float) ((EmbedBottomSheet.this.hasDescription ? 22 : 0) + 84)) + ((int) Math.min(((float) EmbedBottomSheet.this.height) / (((float) EmbedBottomSheet.this.width) / ((float) MeasureSpec.getSize(widthMeasureSpec))), (float) (AndroidUtilities.displaySize.y / 2)))) + 1, NUM));
            }
        };
        containerLayout.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        setCustomView(containerLayout);
        this.webView = new WebView(context);
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
            public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
                onShowCustomView(view, callback);
            }

            public void onShowCustomView(View view, CustomViewCallback callback) {
                if (EmbedBottomSheet.this.customView != null) {
                    callback.onCustomViewHidden();
                    return;
                }
                EmbedBottomSheet.this.customView = view;
                EmbedBottomSheet.this.getSheetContainer().setVisibility(4);
                EmbedBottomSheet.this.fullscreenVideoContainer.setVisibility(0);
                EmbedBottomSheet.this.fullscreenVideoContainer.addView(view, LayoutHelper.createFrame(-1, -1.0f));
                EmbedBottomSheet.this.customViewCallback = callback;
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
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
            }

            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                EmbedBottomSheet.this.progressBar.setVisibility(4);
            }
        });
        containerLayout.addView(this.webView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, (float) ((this.hasDescription ? 22 : 0) + 84)));
        this.videoView = new WebPlayerView(context, true, false, new WebPlayerViewDelegate() {
            public void onInitFailed() {
                EmbedBottomSheet.this.webView.setVisibility(0);
                EmbedBottomSheet.this.videoView.setVisibility(4);
                EmbedBottomSheet.this.videoView.loadVideo(null, null, null, false);
                HashMap<String, String> args = new HashMap();
                args.put("Referer", "http://youtube.com");
                try {
                    EmbedBottomSheet.this.webView.loadUrl(EmbedBottomSheet.this.embedUrl, args);
                } catch (Throwable e) {
                    FileLog.e("tmessages", e);
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
                            FileLog.e("tmessages", e);
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
                            FileLog.e("tmessages", e2);
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
                if (inline) {
                    if (EmbedBottomSheet.this.parentActivity != null) {
                        try {
                            EmbedBottomSheet.this.containerView.setSystemUiVisibility(0);
                            if (EmbedBottomSheet.this.prevOrientation != -2) {
                                EmbedBottomSheet.this.parentActivity.setRequestedOrientation(EmbedBottomSheet.this.prevOrientation);
                            }
                        } catch (Throwable e) {
                            FileLog.e("tmessages", e);
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
                            rect.y += (float) AndroidUtilities.statusBarHeight;
                        }
                        AnimatorSet animatorSet = new AnimatorSet();
                        r9 = new Animator[12];
                        r9[0] = ObjectAnimator.ofFloat(textureImageView, "scaleX", new float[]{scale});
                        r9[1] = ObjectAnimator.ofFloat(textureImageView, "scaleY", new float[]{scale});
                        r9[2] = ObjectAnimator.ofFloat(textureImageView, "translationX", new float[]{rect.x});
                        r9[3] = ObjectAnimator.ofFloat(textureImageView, "translationY", new float[]{rect.y});
                        r9[4] = ObjectAnimator.ofFloat(textureView, "scaleX", new float[]{scale});
                        r9[5] = ObjectAnimator.ofFloat(textureView, "scaleY", new float[]{scale});
                        r9[6] = ObjectAnimator.ofFloat(textureView, "translationX", new float[]{rect.x});
                        r9[7] = ObjectAnimator.ofFloat(textureView, "translationY", new float[]{rect.y});
                        r9[8] = ObjectAnimator.ofFloat(EmbedBottomSheet.this.containerView, "translationY", new float[]{(float) (EmbedBottomSheet.this.containerView.getMeasuredHeight() + AndroidUtilities.dp(10.0f))});
                        r9[9] = ObjectAnimator.ofInt(EmbedBottomSheet.this.backDrawable, "alpha", new int[]{0});
                        r9[10] = ObjectAnimator.ofFloat(EmbedBottomSheet.this.fullscreenVideoContainer, "alpha", new float[]{0.0f});
                        r9[11] = ObjectAnimator.ofFloat(controlsView, "alpha", new float[]{0.0f});
                        animatorSet.playTogether(r9);
                        animatorSet.setInterpolator(new DecelerateInterpolator());
                        animatorSet.setDuration(250);
                        final Runnable runnable = switchInlineModeRunnable;
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
                    if (EmbedBottomSheet.this.fullscreenVideoContainer.getVisibility() == 0) {
                        EmbedBottomSheet.this.fullscreenVideoContainer.setAlpha(1.0f);
                        EmbedBottomSheet.this.fullscreenVideoContainer.setVisibility(4);
                    }
                    switchInlineModeRunnable.run();
                    EmbedBottomSheet.this.dismissInternal();
                    return;
                }
                if (ApplicationLoader.mainInterfacePaused) {
                    EmbedBottomSheet.this.parentActivity.startService(new Intent(ApplicationLoader.applicationContext, BringAppForegroundService.class));
                }
                if (animated) {
                    EmbedBottomSheet.this.setOnShowListener(EmbedBottomSheet.this.onShowListener);
                    rect = PipVideoView.getPipRect(aspectRatio);
                    textureView = EmbedBottomSheet.this.videoView.getTextureView();
                    textureImageView = EmbedBottomSheet.this.videoView.getTextureImageView();
                    scale = rect.width / ((float) textureView.getLayoutParams().width);
                    if (VERSION.SDK_INT >= 21) {
                        rect.y += (float) AndroidUtilities.statusBarHeight;
                    }
                    textureImageView.setScaleX(scale);
                    textureImageView.setScaleY(scale);
                    textureImageView.setTranslationX(rect.x);
                    textureImageView.setTranslationY(rect.y);
                    textureView.setScaleX(scale);
                    textureView.setScaleY(scale);
                    textureView.setTranslationX(rect.x);
                    textureView.setTranslationY(rect.y);
                } else {
                    EmbedBottomSheet.this.pipVideoView.close();
                    EmbedBottomSheet.this.pipVideoView = null;
                }
                EmbedBottomSheet.this.setShowWithoutAnimation(true);
                EmbedBottomSheet.this.show();
                EmbedBottomSheet.this.backDrawable.setAlpha(0);
                EmbedBottomSheet.this.containerView.setTranslationY((float) (EmbedBottomSheet.this.containerView.getMeasuredHeight() + AndroidUtilities.dp(10.0f)));
            }

            public TextureView onSwitchInlineMode(View controlsView, boolean inline, float aspectRatio, int rotation, boolean animated) {
                if (inline) {
                    controlsView.setTranslationY(0.0f);
                    EmbedBottomSheet.this.pipVideoView = new PipVideoView();
                    return EmbedBottomSheet.this.pipVideoView.show(EmbedBottomSheet.this.parentActivity, EmbedBottomSheet.this, controlsView, aspectRatio, rotation);
                }
                if (animated) {
                    EmbedBottomSheet.this.animationInProgress = true;
                    EmbedBottomSheet.this.videoView.getAspectRatioView().getLocationInWindow(EmbedBottomSheet.this.position);
                    int[] access$3000 = EmbedBottomSheet.this.position;
                    access$3000[1] = (int) (((float) access$3000[1]) - EmbedBottomSheet.this.containerView.getTranslationY());
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
                    animatorSet.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            EmbedBottomSheet.this.animationInProgress = false;
                        }
                    });
                    animatorSet.start();
                } else {
                    EmbedBottomSheet.this.containerView.setTranslationY(0.0f);
                    EmbedBottomSheet.this.backDrawable.setAlpha(51);
                }
                return null;
            }

            public void onSharePressed() {
            }

            public void onPlayStateChanged(WebPlayerView playerView, boolean playing) {
                if (playing) {
                    try {
                        EmbedBottomSheet.this.parentActivity.getWindow().addFlags(128);
                        return;
                    } catch (Throwable e) {
                        FileLog.e("tmessages", e);
                        return;
                    }
                }
                try {
                    EmbedBottomSheet.this.parentActivity.getWindow().clearFlags(128);
                } catch (Throwable e2) {
                    FileLog.e("tmessages", e2);
                }
            }

            public boolean checkInlinePermissons() {
                if (EmbedBottomSheet.this.parentActivity == null) {
                    return false;
                }
                if (VERSION.SDK_INT < 23 || Settings.canDrawOverlays(EmbedBottomSheet.this.parentActivity)) {
                    return true;
                }
                new Builder(EmbedBottomSheet.this.parentActivity).setTitle(LocaleController.getString("AppName", R.string.AppName)).setMessage(LocaleController.getString("PermissionDrawAboveOtherApps", R.string.PermissionDrawAboveOtherApps)).setPositiveButton(LocaleController.getString("PermissionOpenSettings", R.string.PermissionOpenSettings), new OnClickListener() {
                    @TargetApi(23)
                    public void onClick(DialogInterface dialog, int which) {
                        if (EmbedBottomSheet.this.parentActivity != null) {
                            EmbedBottomSheet.this.parentActivity.startActivity(new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse("package:" + EmbedBottomSheet.this.parentActivity.getPackageName())));
                        }
                    }
                }).show();
                return false;
            }

            public ViewGroup getTextureViewContainer() {
                return EmbedBottomSheet.this.container;
            }
        });
        this.videoView.setVisibility(4);
        containerLayout.addView(this.videoView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, (float) (((this.hasDescription ? 22 : 0) + 84) - 10)));
        this.progressBar = new ProgressBar(context);
        this.progressBar.setVisibility(4);
        containerLayout.addView(this.progressBar, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 0.0f, 0.0f, (float) (((this.hasDescription ? 22 : 0) + 84) / 2)));
        if (this.hasDescription) {
            textView = new TextView(context);
            textView.setTextSize(1, 16.0f);
            textView.setTextColor(Theme.REPLY_PANEL_MESSAGE_TEXT_COLOR);
            textView.setText(description);
            textView.setSingleLine(true);
            textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            textView.setEllipsize(TruncateAt.END);
            textView.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
            containerLayout.addView(textView, LayoutHelper.createFrame(-1, -2.0f, 83, 0.0f, 0.0f, 0.0f, 77.0f));
        }
        textView = new TextView(context);
        textView.setTextSize(1, 14.0f);
        textView.setTextColor(-7697782);
        textView.setText(title);
        textView.setSingleLine(true);
        textView.setEllipsize(TruncateAt.END);
        textView.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        containerLayout.addView(textView, LayoutHelper.createFrame(-1, -2.0f, 83, 0.0f, 0.0f, 0.0f, 57.0f));
        View lineView = new View(context);
        lineView.setBackgroundColor(-2368549);
        containerLayout.addView(lineView, new LayoutParams(-1, 1, 83));
        ((LayoutParams) lineView.getLayoutParams()).bottomMargin = AndroidUtilities.dp(48.0f);
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setBackgroundColor(-1);
        containerLayout.addView(frameLayout, LayoutHelper.createFrame(-1, 48, 83));
        textView = new TextView(context);
        textView.setTextSize(1, 14.0f);
        textView.setTextColor(-15095832);
        textView.setGravity(17);
        textView.setBackgroundDrawable(Theme.createBarSelectorDrawable(788529152, false));
        textView.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        textView.setText(LocaleController.getString("Close", R.string.Close).toUpperCase());
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        frameLayout.addView(textView, LayoutHelper.createFrame(-2, -1, 51));
        textView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EmbedBottomSheet.this.dismiss();
            }
        });
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(0);
        frameLayout.addView(linearLayout, LayoutHelper.createFrame(-2, -1, 53));
        textView = new TextView(context);
        textView.setTextSize(1, 14.0f);
        textView.setTextColor(-15095832);
        textView.setGravity(17);
        textView.setBackgroundDrawable(Theme.createBarSelectorDrawable(788529152, false));
        textView.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        textView.setText(LocaleController.getString("Copy", R.string.Copy).toUpperCase());
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        linearLayout.addView(textView, LayoutHelper.createFrame(-2, -1, 51));
        textView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", EmbedBottomSheet.this.openUrl));
                } catch (Throwable e) {
                    FileLog.e("tmessages", e);
                }
                Toast.makeText(EmbedBottomSheet.this.getContext(), LocaleController.getString("LinkCopied", R.string.LinkCopied), 0).show();
                EmbedBottomSheet.this.dismiss();
            }
        });
        textView = new TextView(context);
        textView.setTextSize(1, 14.0f);
        textView.setTextColor(-15095832);
        textView.setGravity(17);
        textView.setBackgroundDrawable(Theme.createBarSelectorDrawable(788529152, false));
        textView.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        textView.setText(LocaleController.getString("OpenInBrowser", R.string.OpenInBrowser).toUpperCase());
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        linearLayout.addView(textView, LayoutHelper.createFrame(-2, -1, 51));
        textView.setOnClickListener(new View.OnClickListener() {
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
                EmbedBottomSheet.this.videoView.setVisibility(4);
                EmbedBottomSheet.this.videoView.loadVideo(null, null, null, false);
                HashMap<String, String> args = new HashMap();
                args.put("Referer", "http://youtube.com");
                try {
                    EmbedBottomSheet.this.webView.loadUrl(EmbedBottomSheet.this.embedUrl, args);
                } catch (Throwable e) {
                    FileLog.e("tmessages", e);
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
                    FileLog.e("tmessages", e);
                }
                return true;
            }
        });
        this.orientationEventListener = new OrientationEventListener(ApplicationLoader.applicationContext) {
            public void onOrientationChanged(int orientation) {
                if (EmbedBottomSheet.this.orientationEventListener == null || EmbedBottomSheet.this.videoView.getVisibility() != 0 || EmbedBottomSheet.this.parentActivity == null || !EmbedBottomSheet.this.videoView.isInFullscreen() || !EmbedBottomSheet.this.fullscreenedByButton) {
                    return;
                }
                if (orientation >= PsExtractor.VIDEO_STREAM_MASK && orientation <= 300) {
                    EmbedBottomSheet.this.wasInLandscape = true;
                } else if (!EmbedBottomSheet.this.wasInLandscape) {
                } else {
                    if (orientation >= 330 || orientation <= 30) {
                        EmbedBottomSheet.this.parentActivity.setRequestedOrientation(EmbedBottomSheet.this.prevOrientation);
                        EmbedBottomSheet.this.fullscreenedByButton = false;
                        EmbedBottomSheet.this.wasInLandscape = false;
                    }
                }
            }
        };
        if (this.orientationEventListener.canDetectOrientation()) {
            this.orientationEventListener.enable();
        } else {
            this.orientationEventListener.disable();
            this.orientationEventListener = null;
        }
        instance = this;
    }

    protected boolean canDismissWithSwipe() {
        return (this.videoView.getVisibility() == 0 && this.videoView.isInFullscreen()) ? false : true;
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

    public static EmbedBottomSheet getInstance() {
        return instance;
    }

    public void updateTextureViewPosition() {
        this.videoView.getAspectRatioView().getLocationInWindow(this.position);
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
            layoutParams.height = (this.videoView.isInFullscreen() ? 0 : AndroidUtilities.dp(10.0f)) + this.videoView.getAspectRatioView().getMeasuredHeight();
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
}
