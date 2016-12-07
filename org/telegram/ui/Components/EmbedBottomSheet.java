package org.telegram.ui.Components;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build.VERSION;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.TextureView;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.exoplayer2.extractor.ts.PsExtractor;
import org.telegram.messenger.exoplayer2.ui.AspectRatioFrameLayout;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.BottomSheet.BottomSheetDelegate;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.WebPlayerView.WebPlayerViewDelegate;

@TargetApi(16)
public class EmbedBottomSheet extends BottomSheet {
    @SuppressLint({"StaticFieldLeak"})
    private static EmbedBottomSheet instance;
    private View customView;
    private CustomViewCallback customViewCallback;
    private String embedUrl;
    private AspectRatioFrameLayout fullscreenAspectRatioView;
    private TextureView fullscreenTextureView;
    private FrameLayout fullscreenVideoContainer;
    private boolean fullscreenedByButton;
    private boolean hasDescription;
    private int height;
    private int lastOrientation;
    private String openUrl;
    private OrientationEventListener orientationEventListener;
    private Activity parentActivity;
    private PipVideoView pipVideoView;
    private int prevOrientation;
    private ProgressBar progressBar;
    private boolean showingFromInline;
    private WebPlayerView videoView;
    private boolean wasInLandscape;
    private WebView webView;
    private int width;

    @SuppressLint({"SetJavaScriptEnabled"})
    public EmbedBottomSheet(Context context, String title, String descripton, String originalUrl, String url, int w, int h) {
        TextView textView;
        super(context, false);
        this.lastOrientation = -1;
        this.prevOrientation = -2;
        this.fullWidth = true;
        setApplyTopPadding(false);
        setApplyBottomPadding(false);
        if (context instanceof Activity) {
            this.parentActivity = (Activity) context;
        }
        this.embedUrl = url;
        boolean z = descripton != null && descripton.length() > 0;
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
        this.fullscreenAspectRatioView = new AspectRatioFrameLayout(context);
        this.fullscreenAspectRatioView.setVisibility(8);
        this.fullscreenVideoContainer.addView(this.fullscreenAspectRatioView, LayoutHelper.createFrame(-1, -1, 17));
        this.fullscreenTextureView = new TextureView(context);
        getContainer().addView(this.fullscreenVideoContainer, LayoutHelper.createFrame(-1, -1.0f));
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
                        EmbedBottomSheet.this.videoView.destroy();
                    }
                } catch (Throwable e) {
                    FileLog.e("tmessages", e);
                }
            }

            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec((AndroidUtilities.dp((float) ((EmbedBottomSheet.this.hasDescription ? 22 : 0) + 84)) + ((int) Math.min((((float) EmbedBottomSheet.this.height) / (((float) EmbedBottomSheet.this.width) / ((float) MeasureSpec.getSize(widthMeasureSpec)))) + ((float) AndroidUtilities.dp(5.0f)), (float) (AndroidUtilities.displaySize.y / 2)))) + 1, NUM));
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
        this.videoView = new WebPlayerView(context, true, false);
        this.videoView.setVisibility(4);
        this.videoView.setDelegate(new WebPlayerViewDelegate() {
            public void onInitFailed() {
                EmbedBottomSheet.this.webView.setVisibility(0);
                EmbedBottomSheet.this.videoView.setVisibility(4);
                EmbedBottomSheet.this.videoView.loadVideo(null, null, false);
                HashMap<String, String> args = new HashMap();
                args.put("Referer", "http://youtube.com");
                try {
                    EmbedBottomSheet.this.webView.loadUrl(EmbedBottomSheet.this.embedUrl, args);
                } catch (Throwable e) {
                    FileLog.e("tmessages", e);
                }
            }

            public TextureView onSwitchToFullscreen(View controlsView, boolean fullscreen, float aspectRation, int rotation, boolean byButton) {
                if (fullscreen) {
                    EmbedBottomSheet.this.fullscreenAspectRatioView.addView(EmbedBottomSheet.this.fullscreenTextureView, LayoutHelper.createFrame(-1, -1.0f));
                    EmbedBottomSheet.this.fullscreenAspectRatioView.setVisibility(0);
                    EmbedBottomSheet.this.fullscreenAspectRatioView.setAspectRatio(aspectRation, rotation);
                    EmbedBottomSheet.this.fullscreenVideoContainer.addView(controlsView, LayoutHelper.createFrame(-1, -1.0f));
                    EmbedBottomSheet.this.fullscreenVideoContainer.setVisibility(0);
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
                    EmbedBottomSheet.this.fullscreenedByButton = false;
                    EmbedBottomSheet.this.fullscreenAspectRatioView.removeView(EmbedBottomSheet.this.fullscreenTextureView);
                    EmbedBottomSheet.this.fullscreenAspectRatioView.setVisibility(8);
                    EmbedBottomSheet.this.fullscreenVideoContainer.setVisibility(4);
                    if (EmbedBottomSheet.this.parentActivity != null) {
                        try {
                            EmbedBottomSheet.this.containerView.setSystemUiVisibility(0);
                            EmbedBottomSheet.this.parentActivity.setRequestedOrientation(EmbedBottomSheet.this.prevOrientation);
                        } catch (Throwable e2) {
                            FileLog.e("tmessages", e2);
                        }
                    }
                }
                return EmbedBottomSheet.this.fullscreenTextureView;
            }

            public void onVideoSizeChanged(float aspectRation, int rotation) {
                EmbedBottomSheet.this.fullscreenAspectRatioView.setAspectRatio(aspectRation, rotation);
            }

            public TextureView onSwtichToInline(View controlsView, boolean inline, float aspectRation, int rotation) {
                if (inline) {
                    EmbedBottomSheet.this.pipVideoView = new PipVideoView();
                    return EmbedBottomSheet.this.pipVideoView.show(EmbedBottomSheet.this.parentActivity, EmbedBottomSheet.this, controlsView, aspectRation, rotation);
                }
                EmbedBottomSheet.this.showingFromInline = true;
                EmbedBottomSheet.this.pipVideoView.close(true);
                EmbedBottomSheet.this.pipVideoView = null;
                return null;
            }

            public void onSharePressed() {
            }

            public void onPlayStateChanged(WebPlayerView playerView, boolean playing) {
                if (playing) {
                    try {
                        EmbedBottomSheet.this.getWindow().addFlags(128);
                        return;
                    } catch (Throwable e) {
                        FileLog.e("tmessages", e);
                        return;
                    }
                }
                try {
                    EmbedBottomSheet.this.getWindow().clearFlags(128);
                } catch (Throwable e2) {
                    FileLog.e("tmessages", e2);
                }
            }
        });
        containerLayout.addView(this.videoView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, (float) ((this.hasDescription ? 22 : 0) + 84)));
        this.progressBar = new ProgressBar(context);
        this.progressBar.setVisibility(4);
        containerLayout.addView(this.progressBar, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 0.0f, 0.0f, (float) (((this.hasDescription ? 22 : 0) + 84) / 2)));
        if (this.hasDescription) {
            textView = new TextView(context);
            textView.setTextSize(1, 16.0f);
            textView.setTextColor(Theme.REPLY_PANEL_MESSAGE_TEXT_COLOR);
            textView.setText(descripton);
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
        textView.setOnClickListener(new OnClickListener() {
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
        textView.setOnClickListener(new OnClickListener() {
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
        textView.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Browser.openUrl(EmbedBottomSheet.this.parentActivity, EmbedBottomSheet.this.openUrl);
                EmbedBottomSheet.this.dismiss();
            }
        });
        setDelegate(new BottomSheetDelegate() {
            public void onOpenAnimationEnd() {
                if (EmbedBottomSheet.this.showingFromInline) {
                    EmbedBottomSheet.this.showingFromInline = false;
                } else if (EmbedBottomSheet.this.videoView.loadVideo(EmbedBottomSheet.this.embedUrl, null, true)) {
                    EmbedBottomSheet.this.progressBar.setVisibility(4);
                    EmbedBottomSheet.this.webView.setVisibility(4);
                    EmbedBottomSheet.this.videoView.setVisibility(0);
                } else {
                    EmbedBottomSheet.this.progressBar.setVisibility(0);
                    EmbedBottomSheet.this.webView.setVisibility(0);
                    EmbedBottomSheet.this.videoView.setVisibility(4);
                    EmbedBottomSheet.this.videoView.loadVideo(null, null, false);
                    HashMap<String, String> args = new HashMap();
                    args.put("Referer", "http://youtube.com");
                    try {
                        EmbedBottomSheet.this.webView.loadUrl(EmbedBottomSheet.this.embedUrl, args);
                    } catch (Throwable e) {
                        FileLog.e("tmessages", e);
                    }
                }
            }

            public boolean canDismiss() {
                if (EmbedBottomSheet.this.fullscreenVideoContainer.getVisibility() == 0 && EmbedBottomSheet.this.fullscreenAspectRatioView.getVisibility() == 0) {
                    EmbedBottomSheet.this.videoView.exitFullscreen();
                    return false;
                }
                try {
                    EmbedBottomSheet.this.getWindow().clearFlags(128);
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
                } else if (EmbedBottomSheet.this.wasInLandscape && orientation >= -30 && orientation <= 30) {
                    EmbedBottomSheet.this.parentActivity.setRequestedOrientation(EmbedBottomSheet.this.prevOrientation);
                    EmbedBottomSheet.this.fullscreenedByButton = false;
                    EmbedBottomSheet.this.wasInLandscape = false;
                }
            }
        };
        if (this.orientationEventListener.canDetectOrientation()) {
            this.orientationEventListener.enable();
        } else {
            this.orientationEventListener.disable();
            this.orientationEventListener = null;
        }
        if (instance != null) {
            instance.destroy();
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
            this.pipVideoView.close(false);
            this.pipVideoView = null;
        }
        if (this.videoView != null) {
            this.videoView.destroy();
        }
    }

    public static EmbedBottomSheet getInstance() {
        return instance;
    }

    public void pause() {
        if (this.videoView != null && this.videoView.isInitied()) {
            this.videoView.pause();
        }
    }
}
