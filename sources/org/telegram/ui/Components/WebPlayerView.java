package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.hockeyapp.android.UpdateFragment;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.Bitmaps;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.ui.AspectRatioFrameLayout;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.tgnet.TLRPC.Photo;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate;

public class WebPlayerView extends ViewGroup implements OnAudioFocusChangeListener, VideoPlayerDelegate {
    private static final int AUDIO_FOCUSED = 2;
    private static final int AUDIO_NO_FOCUS_CAN_DUCK = 1;
    private static final int AUDIO_NO_FOCUS_NO_DUCK = 0;
    private static final Pattern aparatFileListPattern = Pattern.compile("fileList\\s*=\\s*JSON\\.parse\\('([^']+)'\\)");
    private static final Pattern aparatIdRegex = Pattern.compile("^https?://(?:www\\.)?aparat\\.com/(?:v/|video/video/embed/videohash/)([a-zA-Z0-9]+)");
    private static final Pattern coubIdRegex = Pattern.compile("(?:coub:|https?://(?:coub\\.com/(?:view|embed|coubs)/|c-cdn\\.coub\\.com/fb-player\\.swf\\?.*\\bcoub(?:ID|id)=))([\\da-z]+)");
    private static final String exprName = "[a-zA-Z_$][a-zA-Z_$0-9]*";
    private static final Pattern exprParensPattern = Pattern.compile("[()]");
    private static final Pattern jsPattern = Pattern.compile("\"assets\":.+?\"js\":\\s*(\"[^\"]+\")");
    private static int lastContainerId = 4001;
    private static final Pattern playerIdPattern = Pattern.compile(".*?-([a-zA-Z0-9_-]+)(?:/watch_as3|/html5player(?:-new)?|(?:/[a-z]{2}_[A-Z]{2})?/base)?\\.([a-z]+)$");
    private static final Pattern sigPattern = Pattern.compile("\\.sig\\|\\|([a-zA-Z0-9$]+)\\(");
    private static final Pattern sigPattern2 = Pattern.compile("[\"']signature[\"']\\s*,\\s*([a-zA-Z0-9$]+)\\(");
    private static final Pattern stmtReturnPattern = Pattern.compile("return(?:\\s+|$)");
    private static final Pattern stmtVarPattern = Pattern.compile("var\\s");
    private static final Pattern stsPattern = Pattern.compile("\"sts\"\\s*:\\s*(\\d+)");
    private static final Pattern twitchClipFilePattern = Pattern.compile("clipInfo\\s*=\\s*(\\{[^']+\\});");
    private static final Pattern twitchClipIdRegex = Pattern.compile("https?://clips\\.twitch\\.tv/(?:[^/]+/)*([^/?#&]+)");
    private static final Pattern twitchStreamIdRegex = Pattern.compile("https?://(?:(?:www\\.)?twitch\\.tv/|player\\.twitch\\.tv/\\?.*?\\bchannel=)([^/#?]+)");
    private static final Pattern vimeoIdRegex = Pattern.compile("https?://(?:(?:www|(player))\\.)?vimeo(pro)?\\.com/(?!(?:channels|album)/[^/?#]+/?(?:$|[?#])|[^/]+/review/|ondemand/)(?:.*?/)?(?:(?:play_redirect_hls|moogaloop\\.swf)\\?clip_id=)?(?:videos?/)?([0-9]+)(?:/[\\da-f]+)?/?(?:[?&].*)?(?:[#].*)?$");
    private static final Pattern youtubeIdRegex = Pattern.compile("(?:youtube(?:-nocookie)?\\.com/(?:[^/\\n\\s]+/\\S+/|(?:v|e(?:mbed)?)/|\\S*?[?&]v=)|youtu\\.be/)([a-zA-Z0-9_-]{11})");
    private boolean allowInlineAnimation;
    private AspectRatioFrameLayout aspectRatioFrameLayout;
    private int audioFocus;
    private Paint backgroundPaint;
    private TextureView changedTextureView;
    private boolean changingTextureView;
    private ControlsView controlsView;
    private float currentAlpha;
    private Bitmap currentBitmap;
    private AsyncTask currentTask;
    private String currentYoutubeId;
    private WebPlayerViewDelegate delegate;
    private boolean drawImage;
    private boolean firstFrameRendered;
    private int fragment_container_id;
    private ImageView fullscreenButton;
    private boolean hasAudioFocus;
    private boolean inFullscreen;
    private boolean initFailed;
    private boolean initied;
    private ImageView inlineButton;
    private String interfaceName;
    private boolean isAutoplay;
    private boolean isCompleted;
    private boolean isInline;
    private boolean isLoading;
    private boolean isStream;
    private long lastUpdateTime;
    private String playAudioType;
    private String playAudioUrl;
    private ImageView playButton;
    private String playVideoType;
    private String playVideoUrl;
    private AnimatorSet progressAnimation;
    private Runnable progressRunnable;
    private RadialProgressView progressView;
    private boolean resumeAudioOnFocusGain;
    private int seekToTime;
    private ImageView shareButton;
    private SurfaceTextureListener surfaceTextureListener;
    private Runnable switchToInlineRunnable;
    private boolean switchingInlineMode;
    private ImageView textureImageView;
    private TextureView textureView;
    private ViewGroup textureViewContainer;
    private VideoPlayer videoPlayer;
    private int waitingForFirstTextureUpload;
    private WebView webView;

    /* renamed from: org.telegram.ui.Components.WebPlayerView$1 */
    class C13361 implements Runnable {
        C13361() {
        }

        public void run() {
            if (WebPlayerView.this.videoPlayer != null) {
                if (WebPlayerView.this.videoPlayer.isPlaying()) {
                    WebPlayerView.this.controlsView.setProgress((int) (WebPlayerView.this.videoPlayer.getCurrentPosition() / 1000));
                    WebPlayerView.this.controlsView.setBufferedProgress((int) (WebPlayerView.this.videoPlayer.getBufferedPosition() / 1000));
                    AndroidUtilities.runOnUIThread(WebPlayerView.this.progressRunnable, 1000);
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.WebPlayerView$2 */
    class C13392 implements SurfaceTextureListener {

        /* renamed from: org.telegram.ui.Components.WebPlayerView$2$1 */
        class C13381 implements OnPreDrawListener {

            /* renamed from: org.telegram.ui.Components.WebPlayerView$2$1$1 */
            class C13371 implements Runnable {
                C13371() {
                }

                public void run() {
                    WebPlayerView.this.delegate.onInlineSurfaceTextureReady();
                }
            }

            C13381() {
            }

            public boolean onPreDraw() {
                WebPlayerView.this.changedTextureView.getViewTreeObserver().removeOnPreDrawListener(this);
                if (WebPlayerView.this.textureImageView != null) {
                    WebPlayerView.this.textureImageView.setVisibility(4);
                    WebPlayerView.this.textureImageView.setImageDrawable(null);
                    if (WebPlayerView.this.currentBitmap != null) {
                        WebPlayerView.this.currentBitmap.recycle();
                        WebPlayerView.this.currentBitmap = null;
                    }
                }
                AndroidUtilities.runOnUIThread(new C13371());
                WebPlayerView.this.waitingForFirstTextureUpload = 0;
                return true;
            }
        }

        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
        }

        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
        }

        C13392() {
        }

        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            if (!WebPlayerView.this.changingTextureView) {
                return true;
            }
            if (WebPlayerView.this.switchingInlineMode) {
                WebPlayerView.this.waitingForFirstTextureUpload = 2;
            }
            WebPlayerView.this.textureView.setSurfaceTexture(surfaceTexture);
            WebPlayerView.this.textureView.setVisibility(0);
            WebPlayerView.this.changingTextureView = false;
            return false;
        }

        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
            if (WebPlayerView.this.waitingForFirstTextureUpload == 1) {
                WebPlayerView.this.changedTextureView.getViewTreeObserver().addOnPreDrawListener(new C13381());
                WebPlayerView.this.changedTextureView.invalidate();
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.WebPlayerView$3 */
    class C13403 implements Runnable {
        C13403() {
        }

        public void run() {
            WebPlayerView.this.switchingInlineMode = false;
            if (WebPlayerView.this.currentBitmap != null) {
                WebPlayerView.this.currentBitmap.recycle();
                WebPlayerView.this.currentBitmap = null;
            }
            WebPlayerView.this.changingTextureView = true;
            if (WebPlayerView.this.textureImageView != null) {
                try {
                    WebPlayerView.this.currentBitmap = Bitmaps.createBitmap(WebPlayerView.this.textureView.getWidth(), WebPlayerView.this.textureView.getHeight(), Config.ARGB_8888);
                    WebPlayerView.this.textureView.getBitmap(WebPlayerView.this.currentBitmap);
                } catch (Throwable th) {
                    if (WebPlayerView.this.currentBitmap != null) {
                        WebPlayerView.this.currentBitmap.recycle();
                        WebPlayerView.this.currentBitmap = null;
                    }
                    FileLog.m3e(th);
                }
                if (WebPlayerView.this.currentBitmap != null) {
                    WebPlayerView.this.textureImageView.setVisibility(0);
                    WebPlayerView.this.textureImageView.setImageBitmap(WebPlayerView.this.currentBitmap);
                } else {
                    WebPlayerView.this.textureImageView.setImageDrawable(null);
                }
            }
            WebPlayerView.this.isInline = true;
            WebPlayerView.this.updatePlayButton();
            WebPlayerView.this.updateShareButton();
            WebPlayerView.this.updateFullscreenButton();
            WebPlayerView.this.updateInlineButton();
            ViewGroup viewGroup = (ViewGroup) WebPlayerView.this.controlsView.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(WebPlayerView.this.controlsView);
            }
            WebPlayerView.this.changedTextureView = WebPlayerView.this.delegate.onSwitchInlineMode(WebPlayerView.this.controlsView, WebPlayerView.this.isInline, WebPlayerView.this.aspectRatioFrameLayout.getAspectRatio(), WebPlayerView.this.aspectRatioFrameLayout.getVideoRotation(), WebPlayerView.this.allowInlineAnimation);
            WebPlayerView.this.changedTextureView.setVisibility(4);
            viewGroup = (ViewGroup) WebPlayerView.this.textureView.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(WebPlayerView.this.textureView);
            }
            WebPlayerView.this.controlsView.show(false, false);
        }
    }

    /* renamed from: org.telegram.ui.Components.WebPlayerView$6 */
    class C13416 implements OnClickListener {
        C13416() {
        }

        public void onClick(View view) {
            if (WebPlayerView.this.initied != null && WebPlayerView.this.changingTextureView == null && WebPlayerView.this.switchingInlineMode == null) {
                if (WebPlayerView.this.firstFrameRendered != null) {
                    WebPlayerView.this.inFullscreen = WebPlayerView.this.inFullscreen ^ true;
                    WebPlayerView.this.updateFullscreenState(true);
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.WebPlayerView$7 */
    class C13427 implements OnClickListener {
        C13427() {
        }

        public void onClick(View view) {
            if (WebPlayerView.this.initied != null) {
                if (WebPlayerView.this.playVideoUrl != null) {
                    if (WebPlayerView.this.videoPlayer.isPlayerPrepared() == null) {
                        WebPlayerView.this.preparePlayer();
                    }
                    if (WebPlayerView.this.videoPlayer.isPlaying() != null) {
                        WebPlayerView.this.videoPlayer.pause();
                    } else {
                        WebPlayerView.this.isCompleted = false;
                        WebPlayerView.this.videoPlayer.play();
                    }
                    WebPlayerView.this.updatePlayButton();
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.WebPlayerView$8 */
    class C13438 implements OnClickListener {
        C13438() {
        }

        public void onClick(View view) {
            if (WebPlayerView.this.textureView != null && WebPlayerView.this.delegate.checkInlinePermissions() != null && WebPlayerView.this.changingTextureView == null && WebPlayerView.this.switchingInlineMode == null) {
                if (WebPlayerView.this.firstFrameRendered != null) {
                    WebPlayerView.this.switchingInlineMode = true;
                    if (WebPlayerView.this.isInline == null) {
                        WebPlayerView.this.inFullscreen = false;
                        WebPlayerView.this.delegate.prepareToSwitchInlineMode(true, WebPlayerView.this.switchToInlineRunnable, WebPlayerView.this.aspectRatioFrameLayout.getAspectRatio(), WebPlayerView.this.allowInlineAnimation);
                    } else {
                        ViewGroup viewGroup = (ViewGroup) WebPlayerView.this.aspectRatioFrameLayout.getParent();
                        if (viewGroup != WebPlayerView.this) {
                            if (viewGroup != null) {
                                viewGroup.removeView(WebPlayerView.this.aspectRatioFrameLayout);
                            }
                            WebPlayerView.this.addView(WebPlayerView.this.aspectRatioFrameLayout, 0, LayoutHelper.createFrame(-1, -1, 17));
                            WebPlayerView.this.aspectRatioFrameLayout.measure(MeasureSpec.makeMeasureSpec(WebPlayerView.this.getMeasuredWidth(), NUM), MeasureSpec.makeMeasureSpec(WebPlayerView.this.getMeasuredHeight() - AndroidUtilities.dp(10.0f), NUM));
                        }
                        if (WebPlayerView.this.currentBitmap != null) {
                            WebPlayerView.this.currentBitmap.recycle();
                            WebPlayerView.this.currentBitmap = null;
                        }
                        WebPlayerView.this.changingTextureView = true;
                        WebPlayerView.this.isInline = false;
                        WebPlayerView.this.updatePlayButton();
                        WebPlayerView.this.updateShareButton();
                        WebPlayerView.this.updateFullscreenButton();
                        WebPlayerView.this.updateInlineButton();
                        WebPlayerView.this.textureView.setVisibility(4);
                        if (WebPlayerView.this.textureViewContainer != null) {
                            WebPlayerView.this.textureViewContainer.addView(WebPlayerView.this.textureView);
                        } else {
                            WebPlayerView.this.aspectRatioFrameLayout.addView(WebPlayerView.this.textureView);
                        }
                        viewGroup = (ViewGroup) WebPlayerView.this.controlsView.getParent();
                        if (viewGroup != WebPlayerView.this) {
                            if (viewGroup != null) {
                                viewGroup.removeView(WebPlayerView.this.controlsView);
                            }
                            if (WebPlayerView.this.textureViewContainer != null) {
                                WebPlayerView.this.textureViewContainer.addView(WebPlayerView.this.controlsView);
                            } else {
                                WebPlayerView.this.addView(WebPlayerView.this.controlsView, 1);
                            }
                        }
                        WebPlayerView.this.controlsView.show(false, false);
                        WebPlayerView.this.delegate.prepareToSwitchInlineMode(false, null, WebPlayerView.this.aspectRatioFrameLayout.getAspectRatio(), WebPlayerView.this.allowInlineAnimation);
                    }
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.WebPlayerView$9 */
    class C13449 implements OnClickListener {
        C13449() {
        }

        public void onClick(View view) {
            if (WebPlayerView.this.delegate != null) {
                WebPlayerView.this.delegate.onSharePressed();
            }
        }
    }

    private class AparatVideoTask extends AsyncTask<Void, Void, String> {
        private boolean canRetry = true;
        private String[] results = new String[2];
        private String videoId;

        public AparatVideoTask(String str) {
            this.videoId = str;
        }

        protected String doInBackground(Void... voidArr) {
            voidArr = WebPlayerView.this.downloadUrlContent(this, String.format(Locale.US, "http://www.aparat.com/video/video/embed/vt/frame/showvideo/yes/videohash/%s", new Object[]{this.videoId}));
            String str = null;
            if (isCancelled()) {
                return null;
            }
            try {
                voidArr = WebPlayerView.aparatFileListPattern.matcher(voidArr);
                if (voidArr.find()) {
                    JSONArray jSONArray = new JSONArray(voidArr.group(1));
                    for (voidArr = null; voidArr < jSONArray.length(); voidArr++) {
                        JSONArray jSONArray2 = jSONArray.getJSONArray(voidArr);
                        if (jSONArray2.length() != 0) {
                            JSONObject jSONObject = jSONArray2.getJSONObject(0);
                            if (jSONObject.has("file")) {
                                this.results[0] = jSONObject.getString("file");
                                this.results[1] = "other";
                            }
                        }
                    }
                }
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
            if (isCancelled() == null) {
                str = this.results[0];
            }
            return str;
        }

        protected void onPostExecute(String str) {
            if (str != null) {
                WebPlayerView.this.initied = true;
                WebPlayerView.this.playVideoUrl = str;
                WebPlayerView.this.playVideoType = this.results[1];
                if (WebPlayerView.this.isAutoplay != null) {
                    WebPlayerView.this.preparePlayer();
                }
                WebPlayerView.this.showProgress(false, true);
                WebPlayerView.this.controlsView.show(true, true);
            } else if (isCancelled() == null) {
                WebPlayerView.this.onInitFailed();
            }
        }
    }

    public interface CallJavaResultInterface {
        void jsCallFinished(String str);
    }

    private class ControlsView extends FrameLayout {
        private int bufferedPosition;
        private AnimatorSet currentAnimation;
        private int currentProgressX;
        private int duration;
        private StaticLayout durationLayout;
        private int durationWidth;
        private Runnable hideRunnable = new C13451();
        private ImageReceiver imageReceiver;
        private boolean isVisible = true;
        private int lastProgressX;
        private int progress;
        private Paint progressBufferedPaint;
        private Paint progressInnerPaint;
        private StaticLayout progressLayout;
        private Paint progressPaint;
        private boolean progressPressed;
        private TextPaint textPaint;

        /* renamed from: org.telegram.ui.Components.WebPlayerView$ControlsView$1 */
        class C13451 implements Runnable {
            C13451() {
            }

            public void run() {
                ControlsView.this.show(false, true);
            }
        }

        /* renamed from: org.telegram.ui.Components.WebPlayerView$ControlsView$2 */
        class C13462 extends AnimatorListenerAdapter {
            C13462() {
            }

            public void onAnimationEnd(Animator animator) {
                ControlsView.this.currentAnimation = null;
            }
        }

        /* renamed from: org.telegram.ui.Components.WebPlayerView$ControlsView$3 */
        class C13473 extends AnimatorListenerAdapter {
            C13473() {
            }

            public void onAnimationEnd(Animator animator) {
                ControlsView.this.currentAnimation = null;
            }
        }

        public ControlsView(Context context) {
            super(context);
            setWillNotDraw(null);
            this.textPaint = new TextPaint(1);
            this.textPaint.setColor(-1);
            this.textPaint.setTextSize((float) AndroidUtilities.dp(12.0f));
            this.progressPaint = new Paint(1);
            this.progressPaint.setColor(-15095832);
            this.progressInnerPaint = new Paint();
            this.progressInnerPaint.setColor(-6975081);
            this.progressBufferedPaint = new Paint(1);
            this.progressBufferedPaint.setColor(-1);
            this.imageReceiver = new ImageReceiver(this);
        }

        public void setDuration(int i) {
            if (this.duration != i && i >= 0) {
                if (!WebPlayerView.this.isStream) {
                    this.duration = i;
                    this.durationLayout = new StaticLayout(String.format(Locale.US, "%d:%02d", new Object[]{Integer.valueOf(this.duration / 60), Integer.valueOf(this.duration % 60)}), this.textPaint, AndroidUtilities.dp(1000.0f), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    if (this.durationLayout.getLineCount() > 0) {
                        this.durationWidth = (int) Math.ceil((double) this.durationLayout.getLineWidth(0));
                    }
                    invalidate();
                }
            }
        }

        public void setBufferedProgress(int i) {
            this.bufferedPosition = i;
            invalidate();
        }

        public void setProgress(int i) {
            if (!this.progressPressed && i >= 0) {
                if (!WebPlayerView.this.isStream) {
                    this.progress = i;
                    this.progressLayout = new StaticLayout(String.format(Locale.US, "%d:%02d", new Object[]{Integer.valueOf(this.progress / 60), Integer.valueOf(this.progress % 60)}), this.textPaint, AndroidUtilities.dp(1000.0f), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    invalidate();
                }
            }
        }

        public void show(boolean z, boolean z2) {
            if (this.isVisible != z) {
                this.isVisible = z;
                if (this.currentAnimation) {
                    this.currentAnimation.cancel();
                }
                Animator[] animatorArr;
                if (this.isVisible) {
                    if (z2) {
                        this.currentAnimation = new AnimatorSet();
                        z2 = this.currentAnimation;
                        animatorArr = new Animator[1];
                        animatorArr[0] = ObjectAnimator.ofFloat(this, "alpha", new float[]{1.0f});
                        z2.playTogether(animatorArr);
                        this.currentAnimation.setDuration(150);
                        this.currentAnimation.addListener(new C13462());
                        this.currentAnimation.start();
                    } else {
                        setAlpha(1.0f);
                    }
                } else if (z2) {
                    this.currentAnimation = new AnimatorSet();
                    z2 = this.currentAnimation;
                    animatorArr = new Animator[1];
                    animatorArr[0] = ObjectAnimator.ofFloat(this, "alpha", new float[]{0.0f});
                    z2.playTogether(animatorArr);
                    this.currentAnimation.setDuration(150);
                    this.currentAnimation.addListener(new C13473());
                    this.currentAnimation.start();
                } else {
                    setAlpha(0.0f);
                }
                checkNeedHide();
            }
        }

        private void checkNeedHide() {
            AndroidUtilities.cancelRunOnUIThread(this.hideRunnable);
            if (this.isVisible && WebPlayerView.this.videoPlayer.isPlaying()) {
                AndroidUtilities.runOnUIThread(this.hideRunnable, 3000);
            }
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            if (motionEvent.getAction() != 0) {
                return super.onInterceptTouchEvent(motionEvent);
            }
            if (this.isVisible) {
                onTouchEvent(motionEvent);
                return this.progressPressed;
            }
            show(true, true);
            return true;
        }

        public void requestDisallowInterceptTouchEvent(boolean z) {
            super.requestDisallowInterceptTouchEvent(z);
            checkNeedHide();
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            int dp;
            int measuredWidth;
            int measuredHeight;
            if (WebPlayerView.this.inFullscreen) {
                dp = AndroidUtilities.dp(36.0f) + this.durationWidth;
                measuredWidth = (getMeasuredWidth() - AndroidUtilities.dp(76.0f)) - this.durationWidth;
                measuredHeight = getMeasuredHeight() - AndroidUtilities.dp(28.0f);
            } else {
                measuredWidth = getMeasuredWidth();
                measuredHeight = getMeasuredHeight() - AndroidUtilities.dp(12.0f);
                dp = 0;
            }
            int i = (this.duration != 0 ? (int) (((float) (measuredWidth - dp)) * (((float) this.progress) / ((float) this.duration))) : 0) + dp;
            int y;
            if (motionEvent.getAction() == 0) {
                if (!this.isVisible || WebPlayerView.this.isInline || WebPlayerView.this.isStream) {
                    show(true, true);
                } else if (this.duration != 0) {
                    dp = (int) motionEvent.getX();
                    y = (int) motionEvent.getY();
                    if (dp >= i - AndroidUtilities.dp(10.0f) && dp <= AndroidUtilities.dp(10.0f) + i && y >= r3 - AndroidUtilities.dp(10.0f) && y <= r3 + AndroidUtilities.dp(10.0f)) {
                        this.progressPressed = true;
                        this.lastProgressX = dp;
                        this.currentProgressX = i;
                        getParent().requestDisallowInterceptTouchEvent(true);
                        invalidate();
                    }
                }
                AndroidUtilities.cancelRunOnUIThread(this.hideRunnable);
            } else {
                if (motionEvent.getAction() != 1) {
                    if (motionEvent.getAction() != 3) {
                        if (motionEvent.getAction() == 2 && this.progressPressed) {
                            y = (int) motionEvent.getX();
                            this.currentProgressX -= this.lastProgressX - y;
                            this.lastProgressX = y;
                            if (this.currentProgressX < dp) {
                                this.currentProgressX = dp;
                            } else if (this.currentProgressX > measuredWidth) {
                                this.currentProgressX = measuredWidth;
                            }
                            setProgress((int) (((float) (this.duration * 1000)) * (((float) (this.currentProgressX - dp)) / ((float) (measuredWidth - dp)))));
                            invalidate();
                        }
                    }
                }
                if (WebPlayerView.this.initied && WebPlayerView.this.videoPlayer.isPlaying()) {
                    AndroidUtilities.runOnUIThread(this.hideRunnable, 3000);
                }
                if (this.progressPressed) {
                    this.progressPressed = false;
                    if (WebPlayerView.this.initied) {
                        this.progress = (int) (((float) this.duration) * (((float) (this.currentProgressX - dp)) / ((float) (measuredWidth - dp))));
                        WebPlayerView.this.videoPlayer.seekTo(((long) this.progress) * 1000);
                    }
                }
            }
            super.onTouchEvent(motionEvent);
            return true;
        }

        protected void onDraw(Canvas canvas) {
            Canvas canvas2 = canvas;
            if (WebPlayerView.this.drawImage) {
                if (WebPlayerView.this.firstFrameRendered && WebPlayerView.this.currentAlpha != 0.0f) {
                    long currentTimeMillis = System.currentTimeMillis();
                    long access$4900 = currentTimeMillis - WebPlayerView.this.lastUpdateTime;
                    WebPlayerView.this.lastUpdateTime = currentTimeMillis;
                    WebPlayerView.this.currentAlpha = WebPlayerView.this.currentAlpha - (((float) access$4900) / 150.0f);
                    if (WebPlayerView.this.currentAlpha < 0.0f) {
                        WebPlayerView.this.currentAlpha = 0.0f;
                    }
                    invalidate();
                }
                r0.imageReceiver.setAlpha(WebPlayerView.this.currentAlpha);
                r0.imageReceiver.draw(canvas2);
            }
            if (WebPlayerView.this.videoPlayer.isPlayerPrepared() && !WebPlayerView.this.isStream) {
                int i;
                int measuredWidth = getMeasuredWidth();
                int measuredHeight = getMeasuredHeight();
                if (!WebPlayerView.this.isInline) {
                    i = 10;
                    if (r0.durationLayout != null) {
                        canvas.save();
                        canvas2.translate((float) ((measuredWidth - AndroidUtilities.dp(58.0f)) - r0.durationWidth), (float) (measuredHeight - AndroidUtilities.dp((float) ((WebPlayerView.this.inFullscreen ? 6 : 10) + 29))));
                        r0.durationLayout.draw(canvas2);
                        canvas.restore();
                    }
                    if (r0.progressLayout != null) {
                        canvas.save();
                        float dp = (float) AndroidUtilities.dp(18.0f);
                        if (WebPlayerView.this.inFullscreen) {
                            i = 6;
                        }
                        canvas2.translate(dp, (float) (measuredHeight - AndroidUtilities.dp((float) (29 + i))));
                        r0.progressLayout.draw(canvas2);
                        canvas.restore();
                    }
                }
                if (r0.duration != 0) {
                    int dp2;
                    float f = 7.0f;
                    i = 0;
                    if (WebPlayerView.this.isInline) {
                        dp2 = measuredHeight - AndroidUtilities.dp(3.0f);
                        measuredHeight -= AndroidUtilities.dp(7.0f);
                    } else if (WebPlayerView.this.inFullscreen) {
                        dp2 = measuredHeight - AndroidUtilities.dp(29.0f);
                        i = AndroidUtilities.dp(36.0f) + r0.durationWidth;
                        measuredWidth = (measuredWidth - AndroidUtilities.dp(76.0f)) - r0.durationWidth;
                        measuredHeight -= AndroidUtilities.dp(28.0f);
                    } else {
                        dp2 = measuredHeight - AndroidUtilities.dp(13.0f);
                        measuredHeight -= AndroidUtilities.dp(12.0f);
                    }
                    int i2 = measuredWidth;
                    int i3 = measuredHeight;
                    int i4 = dp2;
                    int i5 = i;
                    if (WebPlayerView.this.inFullscreen) {
                        canvas2.drawRect((float) i5, (float) i4, (float) i2, (float) (AndroidUtilities.dp(3.0f) + i4), r0.progressInnerPaint);
                    }
                    if (r0.progressPressed) {
                        measuredWidth = r0.currentProgressX;
                    } else {
                        measuredWidth = ((int) (((float) (i2 - i5)) * (((float) r0.progress) / ((float) r0.duration)))) + i5;
                    }
                    int i6 = measuredWidth;
                    if (!(r0.bufferedPosition == 0 || r0.duration == 0)) {
                        float f2 = (float) i5;
                        canvas2.drawRect(f2, (float) i4, f2 + (((float) (i2 - i5)) * (((float) r0.bufferedPosition) / ((float) r0.duration))), (float) (AndroidUtilities.dp(3.0f) + i4), WebPlayerView.this.inFullscreen ? r0.progressBufferedPaint : r0.progressInnerPaint);
                    }
                    float f3 = (float) i6;
                    canvas2.drawRect((float) i5, (float) i4, f3, (float) (i4 + AndroidUtilities.dp(3.0f)), r0.progressPaint);
                    if (!WebPlayerView.this.isInline) {
                        float f4 = (float) i3;
                        if (!r0.progressPressed) {
                            f = 5.0f;
                        }
                        canvas2.drawCircle(f3, f4, (float) AndroidUtilities.dp(f), r0.progressPaint);
                    }
                }
            }
        }
    }

    private class CoubVideoTask extends AsyncTask<Void, Void, String> {
        private boolean canRetry = true;
        private String[] results = new String[4];
        private String videoId;

        public CoubVideoTask(String str) {
            this.videoId = str;
        }

        private java.lang.String decodeUrl(java.lang.String r5) {
            /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
            /*
            r4 = this;
            r0 = new java.lang.StringBuilder;
            r0.<init>(r5);
            r5 = 0;
            r1 = r5;
        L_0x0007:
            r2 = r0.length();
            if (r1 >= r2) goto L_0x0021;
        L_0x000d:
            r2 = r0.charAt(r1);
            r3 = java.lang.Character.toLowerCase(r2);
            if (r2 != r3) goto L_0x001b;
        L_0x0017:
            r3 = java.lang.Character.toUpperCase(r2);
        L_0x001b:
            r0.setCharAt(r1, r3);
            r1 = r1 + 1;
            goto L_0x0007;
        L_0x0021:
            r1 = new java.lang.String;	 Catch:{ Exception -> 0x0031 }
            r0 = r0.toString();	 Catch:{ Exception -> 0x0031 }
            r5 = android.util.Base64.decode(r0, r5);	 Catch:{ Exception -> 0x0031 }
            r0 = "UTF-8";	 Catch:{ Exception -> 0x0031 }
            r1.<init>(r5, r0);	 Catch:{ Exception -> 0x0031 }
            return r1;
        L_0x0031:
            r5 = 0;
            return r5;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.WebPlayerView.CoubVideoTask.decodeUrl(java.lang.String):java.lang.String");
        }

        protected String doInBackground(Void... voidArr) {
            voidArr = WebPlayerView.this.downloadUrlContent(this, String.format(Locale.US, "https://coub.com/api/v2/coubs/%s.json", new Object[]{this.videoId}));
            String str = null;
            if (isCancelled()) {
                return null;
            }
            try {
                voidArr = new JSONObject(voidArr).getJSONObject("file_versions").getJSONObject("mobile");
                String decodeUrl = decodeUrl(voidArr.getString("gifv"));
                voidArr = voidArr.getJSONArray(MimeTypes.BASE_TYPE_AUDIO).getString(0);
                if (!(decodeUrl == null || voidArr == null)) {
                    this.results[0] = decodeUrl;
                    this.results[1] = "other";
                    this.results[2] = voidArr;
                    this.results[3] = "other";
                }
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
            if (isCancelled() == null) {
                str = this.results[0];
            }
            return str;
        }

        protected void onPostExecute(String str) {
            if (str != null) {
                WebPlayerView.this.initied = true;
                WebPlayerView.this.playVideoUrl = str;
                WebPlayerView.this.playVideoType = this.results[1];
                WebPlayerView.this.playAudioUrl = this.results[2];
                WebPlayerView.this.playAudioType = this.results[3];
                if (WebPlayerView.this.isAutoplay != null) {
                    WebPlayerView.this.preparePlayer();
                }
                WebPlayerView.this.showProgress(false, true);
                WebPlayerView.this.controlsView.show(true, true);
            } else if (isCancelled() == null) {
                WebPlayerView.this.onInitFailed();
            }
        }
    }

    private class JSExtractor {
        private String[] assign_operators = new String[]{"|=", "^=", "&=", ">>=", "<<=", "-=", "+=", "%=", "/=", "*=", "="};
        ArrayList<String> codeLines = new ArrayList();
        private String jsCode;
        private String[] operators = new String[]{"|", "^", "&", ">>", "<<", "-", "+", "%", "/", "*"};

        public JSExtractor(String str) {
            this.jsCode = str;
        }

        private void interpretExpression(java.lang.String r11, java.util.HashMap<java.lang.String, java.lang.String> r12, int r13) throws java.lang.Exception {
            /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
            /*
            r10 = this;
            r11 = r11.trim();
            r0 = android.text.TextUtils.isEmpty(r11);
            if (r0 == 0) goto L_0x000b;
        L_0x000a:
            return;
        L_0x000b:
            r0 = 0;
            r1 = r11.charAt(r0);
            r2 = 40;
            r3 = 1;
            if (r1 != r2) goto L_0x0067;
        L_0x0015:
            r1 = org.telegram.ui.Components.WebPlayerView.exprParensPattern;
            r1 = r1.matcher(r11);
            r4 = r0;
        L_0x001e:
            r5 = r1.find();
            if (r5 == 0) goto L_0x0055;
        L_0x0024:
            r5 = r1.group(r0);
            r6 = 48;
            r5 = r5.indexOf(r6);
            if (r5 != r2) goto L_0x0033;
        L_0x0030:
            r4 = r4 + 1;
            goto L_0x001e;
        L_0x0033:
            r4 = r4 + -1;
            if (r4 != 0) goto L_0x001e;
        L_0x0037:
            r2 = r1.start();
            r2 = r11.substring(r3, r2);
            r10.interpretExpression(r2, r12, r13);
            r1 = r1.end();
            r11 = r11.substring(r1);
            r11 = r11.trim();
            r1 = android.text.TextUtils.isEmpty(r11);
            if (r1 == 0) goto L_0x0055;
        L_0x0054:
            return;
        L_0x0055:
            if (r4 == 0) goto L_0x0067;
        L_0x0057:
            r12 = new java.lang.Exception;
            r13 = "Premature end of parens in %s";
            r1 = new java.lang.Object[r3];
            r1[r0] = r11;
            r11 = java.lang.String.format(r13, r1);
            r12.<init>(r11);
            throw r12;
        L_0x0067:
            r1 = r0;
        L_0x0068:
            r2 = r10.assign_operators;
            r4 = 3;
            r5 = 2;
            r2 = r2.length;
            if (r1 >= r2) goto L_0x00b9;
        L_0x006f:
            r2 = r10.assign_operators;
            r2 = r2[r1];
            r6 = java.util.Locale.US;
            r7 = "(?x)(%s)(?:\\[([^\\]]+?)\\])?\\s*%s(.*)$";
            r8 = new java.lang.Object[r5];
            r9 = "[a-zA-Z_$][a-zA-Z_$0-9]*";
            r8[r0] = r9;
            r2 = java.util.regex.Pattern.quote(r2);
            r8[r3] = r2;
            r2 = java.lang.String.format(r6, r7, r8);
            r2 = java.util.regex.Pattern.compile(r2);
            r2 = r2.matcher(r11);
            r6 = r2.find();
            if (r6 != 0) goto L_0x0098;
        L_0x0095:
            r1 = r1 + 1;
            goto L_0x0068;
        L_0x0098:
            r11 = r2.group(r4);
            r0 = r13 + -1;
            r10.interpretExpression(r11, r12, r0);
            r11 = r2.group(r5);
            r0 = android.text.TextUtils.isEmpty(r11);
            if (r0 != 0) goto L_0x00af;
        L_0x00ab:
            r10.interpretExpression(r11, r12, r13);
            goto L_0x00b8;
        L_0x00af:
            r11 = r2.group(r3);
            r13 = "";
            r12.put(r11, r13);
        L_0x00b8:
            return;
        L_0x00b9:
            java.lang.Integer.parseInt(r11);	 Catch:{ Exception -> 0x00bd }
            return;
        L_0x00bd:
            r1 = java.util.Locale.US;
            r2 = "(?!if|return|true|false)(%s)$";
            r6 = new java.lang.Object[r3];
            r7 = "[a-zA-Z_$][a-zA-Z_$0-9]*";
            r6[r0] = r7;
            r1 = java.lang.String.format(r1, r2, r6);
            r1 = java.util.regex.Pattern.compile(r1);
            r1 = r1.matcher(r11);
            r1 = r1.find();
            if (r1 == 0) goto L_0x00da;
        L_0x00d9:
            return;
        L_0x00da:
            r1 = r11.charAt(r0);
            r2 = 34;
            if (r1 != r2) goto L_0x00ee;
        L_0x00e2:
            r1 = r11.length();
            r1 = r1 - r3;
            r1 = r11.charAt(r1);
            if (r1 != r2) goto L_0x00ee;
        L_0x00ed:
            return;
        L_0x00ee:
            r1 = new org.json.JSONObject;	 Catch:{ Exception -> 0x00f7 }
            r1.<init>(r11);	 Catch:{ Exception -> 0x00f7 }
            r1.toString();	 Catch:{ Exception -> 0x00f7 }
            return;
        L_0x00f7:
            r1 = java.util.Locale.US;
            r2 = "(%s)\\[(.+)\\]$";
            r6 = new java.lang.Object[r3];
            r7 = "[a-zA-Z_$][a-zA-Z_$0-9]*";
            r6[r0] = r7;
            r1 = java.lang.String.format(r1, r2, r6);
            r1 = java.util.regex.Pattern.compile(r1);
            r1 = r1.matcher(r11);
            r2 = r1.find();
            if (r2 == 0) goto L_0x011f;
        L_0x0113:
            r1.group(r3);
            r11 = r1.group(r5);
            r13 = r13 - r3;
            r10.interpretExpression(r11, r12, r13);
            return;
        L_0x011f:
            r1 = java.util.Locale.US;
            r2 = "(%s)(?:\\.([^(]+)|\\[([^]]+)\\])\\s*(?:\\(+([^()]*)\\))?$";
            r6 = new java.lang.Object[r3];
            r7 = "[a-zA-Z_$][a-zA-Z_$0-9]*";
            r6[r0] = r7;
            r1 = java.lang.String.format(r1, r2, r6);
            r1 = java.util.regex.Pattern.compile(r1);
            r1 = r1.matcher(r11);
            r2 = r1.find();
            if (r2 == 0) goto L_0x0194;
        L_0x013b:
            r2 = r1.group(r3);
            r5 = r1.group(r5);
            r4 = r1.group(r4);
            r6 = android.text.TextUtils.isEmpty(r5);
            if (r6 == 0) goto L_0x014e;
        L_0x014d:
            goto L_0x014f;
        L_0x014e:
            r4 = r5;
        L_0x014f:
            r5 = "\"";
            r6 = "";
            r4.replace(r5, r6);
            r4 = 4;
            r1 = r1.group(r4);
            r4 = r12.get(r2);
            if (r4 != 0) goto L_0x0164;
        L_0x0161:
            r10.extractObject(r2);
        L_0x0164:
            if (r1 != 0) goto L_0x0167;
        L_0x0166:
            return;
        L_0x0167:
            r2 = r11.length();
            r2 = r2 - r3;
            r11 = r11.charAt(r2);
            r2 = 41;
            if (r11 == r2) goto L_0x017c;
        L_0x0174:
            r11 = new java.lang.Exception;
            r12 = "last char not ')'";
            r11.<init>(r12);
            throw r11;
        L_0x017c:
            r11 = r1.length();
            if (r11 == 0) goto L_0x0193;
        L_0x0182:
            r11 = ",";
            r11 = r1.split(r11);
        L_0x0188:
            r1 = r11.length;
            if (r0 >= r1) goto L_0x0193;
        L_0x018b:
            r1 = r11[r0];
            r10.interpretExpression(r1, r12, r13);
            r0 = r0 + 1;
            goto L_0x0188;
        L_0x0193:
            return;
        L_0x0194:
            r1 = java.util.Locale.US;
            r2 = "(%s)\\[(.+)\\]$";
            r4 = new java.lang.Object[r3];
            r6 = "[a-zA-Z_$][a-zA-Z_$0-9]*";
            r4[r0] = r6;
            r1 = java.lang.String.format(r1, r2, r4);
            r1 = java.util.regex.Pattern.compile(r1);
            r1 = r1.matcher(r11);
            r2 = r1.find();
            if (r2 == 0) goto L_0x01c0;
        L_0x01b0:
            r11 = r1.group(r3);
            r12.get(r11);
            r11 = r1.group(r5);
            r13 = r13 - r3;
            r10.interpretExpression(r11, r12, r13);
            return;
        L_0x01c0:
            r1 = r0;
        L_0x01c1:
            r2 = r10.operators;
            r2 = r2.length;
            if (r1 >= r2) goto L_0x022a;
        L_0x01c6:
            r2 = r10.operators;
            r2 = r2[r1];
            r4 = java.util.Locale.US;
            r6 = "(.+?)%s(.+)";
            r7 = new java.lang.Object[r3];
            r8 = java.util.regex.Pattern.quote(r2);
            r7[r0] = r8;
            r4 = java.lang.String.format(r4, r6, r7);
            r4 = java.util.regex.Pattern.compile(r4);
            r4 = r4.matcher(r11);
            r6 = r4.find();
            if (r6 != 0) goto L_0x01e9;
        L_0x01e8:
            goto L_0x0227;
        L_0x01e9:
            r6 = new boolean[r3];
            r7 = r4.group(r3);
            r8 = r13 + -1;
            r10.interpretStatement(r7, r12, r6, r8);
            r7 = r6[r0];
            if (r7 == 0) goto L_0x020a;
        L_0x01f8:
            r12 = new java.lang.Exception;
            r13 = "Premature left-side return of %s in %s";
            r1 = new java.lang.Object[r5];
            r1[r0] = r2;
            r1[r3] = r11;
            r11 = java.lang.String.format(r13, r1);
            r12.<init>(r11);
            throw r12;
        L_0x020a:
            r4 = r4.group(r5);
            r10.interpretStatement(r4, r12, r6, r8);
            r4 = r6[r0];
            if (r4 == 0) goto L_0x0227;
        L_0x0215:
            r12 = new java.lang.Exception;
            r13 = "Premature right-side return of %s in %s";
            r1 = new java.lang.Object[r5];
            r1[r0] = r2;
            r1[r3] = r11;
            r11 = java.lang.String.format(r13, r1);
            r12.<init>(r11);
            throw r12;
        L_0x0227:
            r1 = r1 + 1;
            goto L_0x01c1;
        L_0x022a:
            r12 = java.util.Locale.US;
            r13 = "^(%s)\\(([a-zA-Z0-9_$,]*)\\)$";
            r1 = new java.lang.Object[r3];
            r2 = "[a-zA-Z_$][a-zA-Z_$0-9]*";
            r1[r0] = r2;
            r12 = java.lang.String.format(r12, r13, r1);
            r12 = java.util.regex.Pattern.compile(r12);
            r12 = r12.matcher(r11);
            r13 = r12.find();
            if (r13 == 0) goto L_0x024d;
        L_0x0246:
            r12 = r12.group(r3);
            r10.extractFunction(r12);
        L_0x024d:
            r12 = new java.lang.Exception;
            r13 = "Unsupported JS expression %s";
            r1 = new java.lang.Object[r3];
            r1[r0] = r11;
            r11 = java.lang.String.format(r13, r1);
            r12.<init>(r11);
            throw r12;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.WebPlayerView.JSExtractor.interpretExpression(java.lang.String, java.util.HashMap, int):void");
        }

        private void interpretStatement(String str, HashMap<String, String> hashMap, boolean[] zArr, int i) throws Exception {
            if (i < 0) {
                throw new Exception("recursion limit reached");
            }
            zArr[0] = false;
            str = str.trim();
            Matcher matcher = WebPlayerView.stmtVarPattern.matcher(str);
            if (matcher.find()) {
                str = str.substring(matcher.group(0).length());
            } else {
                matcher = WebPlayerView.stmtReturnPattern.matcher(str);
                if (matcher.find()) {
                    str = str.substring(matcher.group(0).length());
                    zArr[0] = true;
                }
            }
            interpretExpression(str, hashMap, i);
        }

        private HashMap<String, Object> extractObject(String str) throws Exception {
            String str2 = "(?:[a-zA-Z$0-9]+|\"[a-zA-Z$0-9]+\"|'[a-zA-Z$0-9]+')";
            HashMap<String, Object> hashMap = new HashMap();
            Matcher matcher = Pattern.compile(String.format(Locale.US, "(?:var\\s+)?%s\\s*=\\s*\\{\\s*((%s\\s*:\\s*function\\(.*?\\)\\s*\\{.*?\\}(?:,\\s*)?)*)\\}\\s*;", new Object[]{Pattern.quote(str), str2})).matcher(this.jsCode);
            CharSequence charSequence = null;
            while (matcher.find()) {
                String group = matcher.group();
                CharSequence group2 = matcher.group(2);
                if (TextUtils.isEmpty(group2)) {
                    charSequence = group2;
                } else {
                    if (!this.codeLines.contains(group)) {
                        this.codeLines.add(matcher.group());
                    }
                    charSequence = group2;
                    str = Pattern.compile(String.format("(%s)\\s*:\\s*function\\(([a-z,]+)\\)\\{([^}]+)\\}", new Object[]{str2})).matcher(charSequence);
                    while (str.find()) {
                        buildFunction(str.group(2).split(","), str.group(3));
                    }
                    return hashMap;
                }
            }
            str = Pattern.compile(String.format("(%s)\\s*:\\s*function\\(([a-z,]+)\\)\\{([^}]+)\\}", new Object[]{str2})).matcher(charSequence);
            while (str.find()) {
                buildFunction(str.group(2).split(","), str.group(3));
            }
            return hashMap;
        }

        private void buildFunction(String[] strArr, String str) throws Exception {
            HashMap hashMap = new HashMap();
            for (Object put : strArr) {
                hashMap.put(put, TtmlNode.ANONYMOUS_REGION_ID);
            }
            strArr = str.split(";");
            str = new boolean[1];
            int i = 0;
            while (i < strArr.length) {
                interpretStatement(strArr[i], hashMap, str, 100);
                if (!str[0]) {
                    i++;
                } else {
                    return;
                }
            }
        }

        private String extractFunction(String str) throws Exception {
            try {
                str = Pattern.quote(str);
                str = Pattern.compile(String.format(Locale.US, "(?x)(?:function\\s+%s|[{;,]\\s*%s\\s*=\\s*function|var\\s+%s\\s*=\\s*function)\\s*\\(([^)]*)\\)\\s*\\{([^}]+)\\}", new Object[]{str, str, str})).matcher(this.jsCode);
                if (str.find()) {
                    String group = str.group();
                    if (!this.codeLines.contains(group)) {
                        ArrayList arrayList = this.codeLines;
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(group);
                        stringBuilder.append(";");
                        arrayList.add(stringBuilder.toString());
                    }
                    buildFunction(str.group(1).split(","), str.group(2));
                }
            } catch (Throwable e) {
                this.codeLines.clear();
                FileLog.m3e(e);
            }
            return TextUtils.join(TtmlNode.ANONYMOUS_REGION_ID, this.codeLines);
        }
    }

    public class JavaScriptInterface {
        private final CallJavaResultInterface callJavaResultInterface;

        public JavaScriptInterface(CallJavaResultInterface callJavaResultInterface) {
            this.callJavaResultInterface = callJavaResultInterface;
        }

        @JavascriptInterface
        public void returnResultToJava(String str) {
            this.callJavaResultInterface.jsCallFinished(str);
        }
    }

    private class TwitchClipVideoTask extends AsyncTask<Void, Void, String> {
        private boolean canRetry = true;
        private String currentUrl;
        private String[] results = new String[2];
        private String videoId;

        public TwitchClipVideoTask(String str, String str2) {
            this.videoId = str2;
            this.currentUrl = str;
        }

        protected String doInBackground(Void... voidArr) {
            String str = null;
            voidArr = WebPlayerView.this.downloadUrlContent(this, this.currentUrl, null, false);
            if (isCancelled()) {
                return null;
            }
            try {
                voidArr = WebPlayerView.twitchClipFilePattern.matcher(voidArr);
                if (voidArr.find()) {
                    this.results[0] = new JSONObject(voidArr.group(1)).getJSONArray("quality_options").getJSONObject(0).getString("source");
                    this.results[1] = "other";
                }
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
            if (isCancelled() == null) {
                str = this.results[0];
            }
            return str;
        }

        protected void onPostExecute(String str) {
            if (str != null) {
                WebPlayerView.this.initied = true;
                WebPlayerView.this.playVideoUrl = str;
                WebPlayerView.this.playVideoType = this.results[1];
                if (WebPlayerView.this.isAutoplay != null) {
                    WebPlayerView.this.preparePlayer();
                }
                WebPlayerView.this.showProgress(false, true);
                WebPlayerView.this.controlsView.show(true, true);
            } else if (isCancelled() == null) {
                WebPlayerView.this.onInitFailed();
            }
        }
    }

    private class TwitchStreamVideoTask extends AsyncTask<Void, Void, String> {
        private boolean canRetry = true;
        private String currentUrl;
        private String[] results = new String[2];
        private String videoId;

        public TwitchStreamVideoTask(String str, String str2) {
            this.videoId = str2;
            this.currentUrl = str;
        }

        protected String doInBackground(Void... voidArr) {
            voidArr = new HashMap();
            voidArr.put("Client-ID", "jzkbprff40iqj646a697cyrvl0zt2m6");
            int indexOf = this.videoId.indexOf(38);
            if (indexOf > 0) {
                this.videoId = this.videoId.substring(0, indexOf);
            }
            String downloadUrlContent = WebPlayerView.this.downloadUrlContent(this, String.format(Locale.US, "https://api.twitch.tv/kraken/streams/%s?stream_type=all", new Object[]{this.videoId}), voidArr, false);
            String str = null;
            if (isCancelled()) {
                return null;
            }
            try {
                new JSONObject(downloadUrlContent).getJSONObject("stream");
                JSONObject jSONObject = new JSONObject(WebPlayerView.this.downloadUrlContent(this, String.format(Locale.US, "https://api.twitch.tv/api/channels/%s/access_token", new Object[]{this.videoId}), voidArr, false));
                voidArr = URLEncoder.encode(jSONObject.getString("sig"), C0542C.UTF8_NAME);
                downloadUrlContent = URLEncoder.encode(jSONObject.getString("token"), C0542C.UTF8_NAME);
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("https://youtube.googleapis.com/v/");
                stringBuilder.append(this.videoId);
                URLEncoder.encode(stringBuilder.toString(), C0542C.UTF8_NAME);
                stringBuilder = new StringBuilder();
                stringBuilder.append("allow_source=true&allow_audio_only=true&allow_spectre=true&player=twitchweb&segment_preference=4&p=");
                stringBuilder.append((int) (Math.random() * 1.0E7d));
                stringBuilder.append("&sig=");
                stringBuilder.append(voidArr);
                stringBuilder.append("&token=");
                stringBuilder.append(downloadUrlContent);
                voidArr = stringBuilder.toString();
                this.results[0] = String.format(Locale.US, "https://usher.ttvnw.net/api/channel/hls/%s.m3u8?%s", new Object[]{this.videoId, voidArr});
                this.results[1] = "hls";
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
            if (isCancelled() == null) {
                str = this.results[0];
            }
            return str;
        }

        protected void onPostExecute(String str) {
            if (str != null) {
                WebPlayerView.this.initied = true;
                WebPlayerView.this.playVideoUrl = str;
                WebPlayerView.this.playVideoType = this.results[1];
                if (WebPlayerView.this.isAutoplay != null) {
                    WebPlayerView.this.preparePlayer();
                }
                WebPlayerView.this.showProgress(false, true);
                WebPlayerView.this.controlsView.show(true, true);
            } else if (isCancelled() == null) {
                WebPlayerView.this.onInitFailed();
            }
        }
    }

    private class VimeoVideoTask extends AsyncTask<Void, Void, String> {
        private boolean canRetry = true;
        private String[] results = new String[2];
        private String videoId;

        public VimeoVideoTask(String str) {
            this.videoId = str;
        }

        protected java.lang.String doInBackground(java.lang.Void... r7) {
            /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
            /*
            r6 = this;
            r7 = org.telegram.ui.Components.WebPlayerView.this;
            r0 = java.util.Locale.US;
            r1 = "https://player.vimeo.com/video/%s/config";
            r2 = 1;
            r3 = new java.lang.Object[r2];
            r4 = r6.videoId;
            r5 = 0;
            r3[r5] = r4;
            r0 = java.lang.String.format(r0, r1, r3);
            r7 = r7.downloadUrlContent(r6, r0);
            r0 = r6.isCancelled();
            r1 = 0;
            if (r0 == 0) goto L_0x001e;
        L_0x001d:
            return r1;
        L_0x001e:
            r0 = new org.json.JSONObject;	 Catch:{ Exception -> 0x008c }
            r0.<init>(r7);	 Catch:{ Exception -> 0x008c }
            r7 = "request";	 Catch:{ Exception -> 0x008c }
            r7 = r0.getJSONObject(r7);	 Catch:{ Exception -> 0x008c }
            r0 = "files";	 Catch:{ Exception -> 0x008c }
            r7 = r7.getJSONObject(r0);	 Catch:{ Exception -> 0x008c }
            r0 = "hls";	 Catch:{ Exception -> 0x008c }
            r0 = r7.has(r0);	 Catch:{ Exception -> 0x008c }
            if (r0 == 0) goto L_0x0069;	 Catch:{ Exception -> 0x008c }
        L_0x0037:
            r0 = "hls";	 Catch:{ Exception -> 0x008c }
            r7 = r7.getJSONObject(r0);	 Catch:{ Exception -> 0x008c }
            r0 = r6.results;	 Catch:{ Exception -> 0x0048 }
            r3 = "url";	 Catch:{ Exception -> 0x0048 }
            r3 = r7.getString(r3);	 Catch:{ Exception -> 0x0048 }
            r0[r5] = r3;	 Catch:{ Exception -> 0x0048 }
            goto L_0x0062;
        L_0x0048:
            r0 = "default_cdn";	 Catch:{ Exception -> 0x008c }
            r0 = r7.getString(r0);	 Catch:{ Exception -> 0x008c }
            r3 = "cdns";	 Catch:{ Exception -> 0x008c }
            r7 = r7.getJSONObject(r3);	 Catch:{ Exception -> 0x008c }
            r7 = r7.getJSONObject(r0);	 Catch:{ Exception -> 0x008c }
            r0 = r6.results;	 Catch:{ Exception -> 0x008c }
            r3 = "url";	 Catch:{ Exception -> 0x008c }
            r7 = r7.getString(r3);	 Catch:{ Exception -> 0x008c }
            r0[r5] = r7;	 Catch:{ Exception -> 0x008c }
        L_0x0062:
            r7 = r6.results;	 Catch:{ Exception -> 0x008c }
            r0 = "hls";	 Catch:{ Exception -> 0x008c }
            r7[r2] = r0;	 Catch:{ Exception -> 0x008c }
            goto L_0x0090;	 Catch:{ Exception -> 0x008c }
        L_0x0069:
            r0 = "progressive";	 Catch:{ Exception -> 0x008c }
            r0 = r7.has(r0);	 Catch:{ Exception -> 0x008c }
            if (r0 == 0) goto L_0x0090;	 Catch:{ Exception -> 0x008c }
        L_0x0071:
            r0 = r6.results;	 Catch:{ Exception -> 0x008c }
            r3 = "other";	 Catch:{ Exception -> 0x008c }
            r0[r2] = r3;	 Catch:{ Exception -> 0x008c }
            r0 = "progressive";	 Catch:{ Exception -> 0x008c }
            r7 = r7.getJSONArray(r0);	 Catch:{ Exception -> 0x008c }
            r7 = r7.getJSONObject(r5);	 Catch:{ Exception -> 0x008c }
            r0 = r6.results;	 Catch:{ Exception -> 0x008c }
            r2 = "url";	 Catch:{ Exception -> 0x008c }
            r7 = r7.getString(r2);	 Catch:{ Exception -> 0x008c }
            r0[r5] = r7;	 Catch:{ Exception -> 0x008c }
            goto L_0x0090;
        L_0x008c:
            r7 = move-exception;
            org.telegram.messenger.FileLog.m3e(r7);
        L_0x0090:
            r7 = r6.isCancelled();
            if (r7 == 0) goto L_0x0097;
        L_0x0096:
            goto L_0x009b;
        L_0x0097:
            r7 = r6.results;
            r1 = r7[r5];
        L_0x009b:
            return r1;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.WebPlayerView.VimeoVideoTask.doInBackground(java.lang.Void[]):java.lang.String");
        }

        protected void onPostExecute(String str) {
            if (str != null) {
                WebPlayerView.this.initied = true;
                WebPlayerView.this.playVideoUrl = str;
                WebPlayerView.this.playVideoType = this.results[1];
                if (WebPlayerView.this.isAutoplay != null) {
                    WebPlayerView.this.preparePlayer();
                }
                WebPlayerView.this.showProgress(false, true);
                WebPlayerView.this.controlsView.show(true, true);
            } else if (isCancelled() == null) {
                WebPlayerView.this.onInitFailed();
            }
        }
    }

    public interface WebPlayerViewDelegate {
        boolean checkInlinePermissions();

        ViewGroup getTextureViewContainer();

        void onInitFailed();

        void onInlineSurfaceTextureReady();

        void onPlayStateChanged(WebPlayerView webPlayerView, boolean z);

        void onSharePressed();

        TextureView onSwitchInlineMode(View view, boolean z, float f, int i, boolean z2);

        TextureView onSwitchToFullscreen(View view, boolean z, float f, int i, boolean z2);

        void onVideoSizeChanged(float f, int i);

        void prepareToSwitchInlineMode(boolean z, Runnable runnable, float f, boolean z2);
    }

    private class YoutubeVideoTask extends AsyncTask<Void, Void, String[]> {
        private boolean canRetry = true;
        private CountDownLatch countDownLatch = new CountDownLatch(1);
        private String[] result = new String[2];
        private String sig;
        private String videoId;

        public YoutubeVideoTask(String str) {
            this.videoId = str;
        }

        protected String[] doInBackground(Void... voidArr) {
            Throwable th;
            StringBuilder stringBuilder;
            WebPlayerView webPlayerView = WebPlayerView.this;
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("https://www.youtube.com/embed/");
            stringBuilder2.append(this.videoId);
            CharSequence downloadUrlContent = webPlayerView.downloadUrlContent(this, stringBuilder2.toString());
            String[] strArr = null;
            if (isCancelled()) {
                return null;
            }
            stringBuilder2 = new StringBuilder();
            stringBuilder2.append("video_id=");
            stringBuilder2.append(r1.videoId);
            stringBuilder2.append("&ps=default&gl=US&hl=en");
            String stringBuilder3 = stringBuilder2.toString();
            try {
                StringBuilder stringBuilder4 = new StringBuilder();
                stringBuilder4.append(stringBuilder3);
                stringBuilder4.append("&eurl=");
                StringBuilder stringBuilder5 = new StringBuilder();
                stringBuilder5.append("https://youtube.googleapis.com/v/");
                stringBuilder5.append(r1.videoId);
                stringBuilder4.append(URLEncoder.encode(stringBuilder5.toString(), C0542C.UTF8_NAME));
                stringBuilder3 = stringBuilder4.toString();
            } catch (Throwable e) {
                Throwable e2;
                FileLog.m3e(e2);
            }
            if (downloadUrlContent != null) {
                Matcher matcher = WebPlayerView.stsPattern.matcher(downloadUrlContent);
                if (matcher.find()) {
                    stringBuilder5 = new StringBuilder();
                    stringBuilder5.append(stringBuilder3);
                    stringBuilder5.append("&sts=");
                    stringBuilder5.append(downloadUrlContent.substring(matcher.start() + 6, matcher.end()));
                    stringBuilder3 = stringBuilder5.toString();
                } else {
                    stringBuilder4 = new StringBuilder();
                    stringBuilder4.append(stringBuilder3);
                    stringBuilder4.append("&sts=");
                    stringBuilder3 = stringBuilder4.toString();
                }
            }
            int i = 1;
            r1.result[1] = "dash";
            r5 = new String[5];
            int i2 = 0;
            r5[0] = TtmlNode.ANONYMOUS_REGION_ID;
            r5[1] = "&el=leanback";
            int i3 = 2;
            r5[2] = "&el=embedded";
            r5[3] = "&el=detailpage";
            r5[4] = "&el=vevo";
            String str = null;
            int i4 = 0;
            int i5 = i4;
            while (i4 < r5.length) {
                WebPlayerView webPlayerView2 = WebPlayerView.this;
                StringBuilder stringBuilder6 = new StringBuilder();
                stringBuilder6.append("https://www.youtube.com/get_video_info?");
                stringBuilder6.append(stringBuilder3);
                stringBuilder6.append(r5[i4]);
                String downloadUrlContent2 = webPlayerView2.downloadUrlContent(r1, stringBuilder6.toString());
                if (isCancelled()) {
                    return strArr;
                }
                String str2;
                int i6;
                String[] strArr2;
                int i7;
                String str3;
                int i8;
                if (downloadUrlContent2 != null) {
                    String[] split = downloadUrlContent2.split("&");
                    str2 = strArr;
                    i6 = i2;
                    int i9 = i5;
                    strArr2 = str;
                    i5 = i6;
                    i7 = i5;
                    while (i5 < split.length) {
                        if (split[i5].startsWith("dashmpd")) {
                            strArr = split[i5].split("=");
                            if (strArr.length == i3) {
                                try {
                                    r1.result[i2] = URLDecoder.decode(strArr[i], C0542C.UTF8_NAME);
                                } catch (Throwable e22) {
                                    FileLog.m3e(e22);
                                }
                            }
                            str3 = stringBuilder3;
                            i6 = 1;
                        } else if (split[i5].startsWith("url_encoded_fmt_stream_map")) {
                            strArr = split[i5].split("=");
                            if (strArr.length == i3) {
                                try {
                                    strArr = URLDecoder.decode(strArr[1], C0542C.UTF8_NAME).split("[&,]");
                                    i = i2;
                                    i8 = i;
                                    String str4 = null;
                                    while (i < strArr.length) {
                                        String[] split2 = strArr[i].split("=");
                                        str3 = stringBuilder3;
                                        try {
                                            if (split2[0].startsWith("type")) {
                                                i8 = URLDecoder.decode(split2[1], C0542C.UTF8_NAME).contains(MimeTypes.VIDEO_MP4) ? 1 : i8;
                                            } else if (split2[0].startsWith(UpdateFragment.FRAGMENT_URL)) {
                                                str4 = URLDecoder.decode(split2[1], C0542C.UTF8_NAME);
                                            } else if (split2[0].startsWith("itag")) {
                                                i8 = 0;
                                                str4 = null;
                                            }
                                            if (i8 != 0 && str4 != null) {
                                                strArr2 = str4;
                                                break;
                                            }
                                            i++;
                                            stringBuilder3 = str3;
                                        } catch (Exception e3) {
                                            e22 = e3;
                                        }
                                    }
                                } catch (Exception e4) {
                                    e22 = e4;
                                    str3 = stringBuilder3;
                                    FileLog.m3e(e22);
                                    i5++;
                                    stringBuilder3 = str3;
                                    i = 1;
                                    i2 = 0;
                                    i3 = 2;
                                }
                            }
                            str3 = stringBuilder3;
                        } else {
                            str3 = stringBuilder3;
                            String[] split3;
                            if (split[i5].startsWith("use_cipher_signature")) {
                                split3 = split[i5].split("=");
                                if (split3.length == 2 && split3[1].toLowerCase().equals("true")) {
                                    i9 = 1;
                                }
                            } else if (split[i5].startsWith("hlsvp")) {
                                split3 = split[i5].split("=");
                                if (split3.length == 2) {
                                    try {
                                        str2 = URLDecoder.decode(split3[1], C0542C.UTF8_NAME);
                                    } catch (Throwable e222) {
                                        FileLog.m3e(e222);
                                    }
                                }
                            } else if (split[i5].startsWith("livestream")) {
                                split3 = split[i5].split("=");
                                if (split3.length == 2 && split3[1].toLowerCase().equals("1")) {
                                    i7 = 1;
                                }
                            }
                        }
                        i5++;
                        stringBuilder3 = str3;
                        i = 1;
                        i2 = 0;
                        i3 = 2;
                    }
                    str3 = stringBuilder3;
                    i5 = i9;
                } else {
                    str3 = stringBuilder3;
                    strArr2 = str;
                    i7 = 0;
                    str2 = null;
                    i6 = 0;
                }
                if (i7 != 0) {
                    if (str2 != null && i5 == 0) {
                        if (!str2.contains("/s/")) {
                            r1.result[0] = str2;
                            r1.result[1] = "hls";
                        }
                    }
                    return null;
                }
                if (i6 != 0) {
                    i = i5;
                    str = strArr2;
                    break;
                }
                i4++;
                str = strArr2;
                stringBuilder3 = str3;
                strArr = null;
                i = 1;
                i2 = 0;
                i3 = 2;
            }
            i = i5;
            if (r1.result[0] == null && str != null) {
                r1.result[0] = str;
                r1.result[1] = "other";
            }
            if (r1.result[0] == null || ((r7 == 0 && !r1.result[0].contains("/s/")) || downloadUrlContent == null)) {
                r5 = null;
            } else {
                int indexOf = r1.result[0].indexOf("/s/");
                int indexOf2 = r1.result[0].indexOf(47, indexOf + 10);
                if (indexOf != -1) {
                    String str5;
                    String str6;
                    SharedPreferences sharedPreferences;
                    String string;
                    StringBuilder stringBuilder7;
                    String string2;
                    Object downloadUrlContent3;
                    Matcher matcher2;
                    Editor putString;
                    if (indexOf2 == -1) {
                        indexOf2 = r1.result[0].length();
                    }
                    r1.sig = r1.result[0].substring(indexOf, indexOf2);
                    Matcher matcher3 = WebPlayerView.jsPattern.matcher(downloadUrlContent);
                    if (matcher3.find()) {
                        try {
                            Object nextValue = new JSONTokener(matcher3.group(1)).nextValue();
                            if (nextValue instanceof String) {
                                str5 = (String) nextValue;
                                if (str5 != null) {
                                    matcher3 = WebPlayerView.playerIdPattern.matcher(str5);
                                    if (matcher3.find()) {
                                        str6 = null;
                                    } else {
                                        stringBuilder2 = new StringBuilder();
                                        stringBuilder2.append(matcher3.group(1));
                                        stringBuilder2.append(matcher3.group(2));
                                        str6 = stringBuilder2.toString();
                                    }
                                    sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("youtubecode", 0);
                                    if (str6 == null) {
                                        string = sharedPreferences.getString(str6, null);
                                        stringBuilder7 = new StringBuilder();
                                        stringBuilder7.append(str6);
                                        stringBuilder7.append("n");
                                        string2 = sharedPreferences.getString(stringBuilder7.toString(), null);
                                    } else {
                                        string = null;
                                        string2 = null;
                                    }
                                    if (string != null) {
                                        if (str5.startsWith("//")) {
                                            stringBuilder4 = new StringBuilder();
                                            stringBuilder4.append("https:");
                                            stringBuilder4.append(str5);
                                            str5 = stringBuilder4.toString();
                                        } else if (str5.startsWith("/")) {
                                            stringBuilder4 = new StringBuilder();
                                            stringBuilder4.append("https://www.youtube.com");
                                            stringBuilder4.append(str5);
                                            str5 = stringBuilder4.toString();
                                        }
                                        downloadUrlContent3 = WebPlayerView.this.downloadUrlContent(r1, str5);
                                        if (isCancelled()) {
                                            return null;
                                        }
                                        r5 = null;
                                        if (downloadUrlContent3 != null) {
                                            matcher2 = WebPlayerView.sigPattern.matcher(downloadUrlContent3);
                                            if (matcher2.find()) {
                                                i8 = 1;
                                                matcher2 = WebPlayerView.sigPattern2.matcher(downloadUrlContent3);
                                                if (matcher2.find()) {
                                                    string2 = matcher2.group(1);
                                                }
                                            } else {
                                                i8 = 1;
                                                string2 = matcher2.group(1);
                                            }
                                            if (string2 != null) {
                                                try {
                                                    str5 = new JSExtractor(downloadUrlContent3).extractFunction(string2);
                                                    try {
                                                        if (!(TextUtils.isEmpty(str5) || str6 == null)) {
                                                            putString = sharedPreferences.edit().putString(str6, str5);
                                                            stringBuilder5 = new StringBuilder();
                                                            stringBuilder5.append(str6);
                                                            stringBuilder5.append("n");
                                                            putString.putString(stringBuilder5.toString(), string2).commit();
                                                        }
                                                        string = str5;
                                                    } catch (Throwable e2222) {
                                                        th = e2222;
                                                        string = str5;
                                                        FileLog.m3e(th);
                                                        if (!TextUtils.isEmpty(string)) {
                                                            if (VERSION.SDK_INT < 21) {
                                                                stringBuilder = new StringBuilder();
                                                                stringBuilder.append(string);
                                                                stringBuilder.append("window.");
                                                                stringBuilder.append(WebPlayerView.this.interfaceName);
                                                                stringBuilder.append(".returnResultToJava(");
                                                                stringBuilder.append(string2);
                                                                stringBuilder.append("('");
                                                                stringBuilder.append(r1.sig.substring(3));
                                                                stringBuilder.append("'));");
                                                                str6 = stringBuilder.toString();
                                                            } else {
                                                                stringBuilder = new StringBuilder();
                                                                stringBuilder.append(string);
                                                                stringBuilder.append(string2);
                                                                stringBuilder.append("('");
                                                                stringBuilder.append(r1.sig.substring(3));
                                                                stringBuilder.append("');");
                                                                str6 = stringBuilder.toString();
                                                            }
                                                            try {
                                                                AndroidUtilities.runOnUIThread(new Runnable() {

                                                                    /* renamed from: org.telegram.ui.Components.WebPlayerView$YoutubeVideoTask$1$1 */
                                                                    class C13481 implements ValueCallback<String> {
                                                                        C13481() {
                                                                        }

                                                                        public void onReceiveValue(String str) {
                                                                            String[] access$1300 = YoutubeVideoTask.this.result;
                                                                            String str2 = YoutubeVideoTask.this.result[0];
                                                                            CharSequence access$1400 = YoutubeVideoTask.this.sig;
                                                                            StringBuilder stringBuilder = new StringBuilder();
                                                                            stringBuilder.append("/signature/");
                                                                            stringBuilder.append(str.substring(1, str.length() - 1));
                                                                            access$1300[0] = str2.replace(access$1400, stringBuilder.toString());
                                                                            YoutubeVideoTask.this.countDownLatch.countDown();
                                                                        }
                                                                    }

                                                                    public void run() {
                                                                        if (VERSION.SDK_INT >= 21) {
                                                                            WebPlayerView.this.webView.evaluateJavascript(str6, new C13481());
                                                                            return;
                                                                        }
                                                                        try {
                                                                            StringBuilder stringBuilder = new StringBuilder();
                                                                            stringBuilder.append("<script>");
                                                                            stringBuilder.append(str6);
                                                                            stringBuilder.append("</script>");
                                                                            String encodeToString = Base64.encodeToString(stringBuilder.toString().getBytes(C0542C.UTF8_NAME), 0);
                                                                            WebView access$1600 = WebPlayerView.this.webView;
                                                                            StringBuilder stringBuilder2 = new StringBuilder();
                                                                            stringBuilder2.append("data:text/html;charset=utf-8;base64,");
                                                                            stringBuilder2.append(encodeToString);
                                                                            access$1600.loadUrl(stringBuilder2.toString());
                                                                        } catch (Throwable e) {
                                                                            FileLog.m3e(e);
                                                                        }
                                                                    }
                                                                });
                                                                r1.countDownLatch.await();
                                                                i = 0;
                                                            } catch (Throwable e22222) {
                                                                FileLog.m3e(e22222);
                                                            }
                                                            if (!isCancelled()) {
                                                                if (i == 0) {
                                                                    strArr = r1.result;
                                                                    return strArr;
                                                                }
                                                            }
                                                            strArr = r5;
                                                            return strArr;
                                                        }
                                                        i = i8;
                                                        if (isCancelled()) {
                                                            if (i == 0) {
                                                                strArr = r1.result;
                                                                return strArr;
                                                            }
                                                        }
                                                        strArr = r5;
                                                        return strArr;
                                                    }
                                                } catch (Throwable e222222) {
                                                    th = e222222;
                                                    FileLog.m3e(th);
                                                    if (TextUtils.isEmpty(string)) {
                                                        if (VERSION.SDK_INT < 21) {
                                                            stringBuilder = new StringBuilder();
                                                            stringBuilder.append(string);
                                                            stringBuilder.append(string2);
                                                            stringBuilder.append("('");
                                                            stringBuilder.append(r1.sig.substring(3));
                                                            stringBuilder.append("');");
                                                            str6 = stringBuilder.toString();
                                                        } else {
                                                            stringBuilder = new StringBuilder();
                                                            stringBuilder.append(string);
                                                            stringBuilder.append("window.");
                                                            stringBuilder.append(WebPlayerView.this.interfaceName);
                                                            stringBuilder.append(".returnResultToJava(");
                                                            stringBuilder.append(string2);
                                                            stringBuilder.append("('");
                                                            stringBuilder.append(r1.sig.substring(3));
                                                            stringBuilder.append("'));");
                                                            str6 = stringBuilder.toString();
                                                        }
                                                        AndroidUtilities.runOnUIThread(/* anonymous class already generated */);
                                                        r1.countDownLatch.await();
                                                        i = 0;
                                                        if (isCancelled()) {
                                                            if (i == 0) {
                                                                strArr = r1.result;
                                                                return strArr;
                                                            }
                                                        }
                                                        strArr = r5;
                                                        return strArr;
                                                    }
                                                    i = i8;
                                                    if (isCancelled()) {
                                                        if (i == 0) {
                                                            strArr = r1.result;
                                                            return strArr;
                                                        }
                                                    }
                                                    strArr = r5;
                                                    return strArr;
                                                }
                                            }
                                            if (TextUtils.isEmpty(string)) {
                                                if (VERSION.SDK_INT < 21) {
                                                    stringBuilder = new StringBuilder();
                                                    stringBuilder.append(string);
                                                    stringBuilder.append(string2);
                                                    stringBuilder.append("('");
                                                    stringBuilder.append(r1.sig.substring(3));
                                                    stringBuilder.append("');");
                                                    str6 = stringBuilder.toString();
                                                } else {
                                                    stringBuilder = new StringBuilder();
                                                    stringBuilder.append(string);
                                                    stringBuilder.append("window.");
                                                    stringBuilder.append(WebPlayerView.this.interfaceName);
                                                    stringBuilder.append(".returnResultToJava(");
                                                    stringBuilder.append(string2);
                                                    stringBuilder.append("('");
                                                    stringBuilder.append(r1.sig.substring(3));
                                                    stringBuilder.append("'));");
                                                    str6 = stringBuilder.toString();
                                                }
                                                AndroidUtilities.runOnUIThread(/* anonymous class already generated */);
                                                r1.countDownLatch.await();
                                                i = 0;
                                            }
                                            i = i8;
                                        }
                                    } else {
                                        r5 = null;
                                    }
                                    i8 = 1;
                                    if (TextUtils.isEmpty(string)) {
                                        if (VERSION.SDK_INT < 21) {
                                            stringBuilder = new StringBuilder();
                                            stringBuilder.append(string);
                                            stringBuilder.append("window.");
                                            stringBuilder.append(WebPlayerView.this.interfaceName);
                                            stringBuilder.append(".returnResultToJava(");
                                            stringBuilder.append(string2);
                                            stringBuilder.append("('");
                                            stringBuilder.append(r1.sig.substring(3));
                                            stringBuilder.append("'));");
                                            str6 = stringBuilder.toString();
                                        } else {
                                            stringBuilder = new StringBuilder();
                                            stringBuilder.append(string);
                                            stringBuilder.append(string2);
                                            stringBuilder.append("('");
                                            stringBuilder.append(r1.sig.substring(3));
                                            stringBuilder.append("');");
                                            str6 = stringBuilder.toString();
                                        }
                                        AndroidUtilities.runOnUIThread(/* anonymous class already generated */);
                                        r1.countDownLatch.await();
                                        i = 0;
                                    }
                                    i = i8;
                                }
                            }
                        } catch (Throwable e2222222) {
                            FileLog.m3e(e2222222);
                        }
                    }
                    str5 = null;
                    if (str5 != null) {
                        matcher3 = WebPlayerView.playerIdPattern.matcher(str5);
                        if (matcher3.find()) {
                            str6 = null;
                        } else {
                            stringBuilder2 = new StringBuilder();
                            stringBuilder2.append(matcher3.group(1));
                            stringBuilder2.append(matcher3.group(2));
                            str6 = stringBuilder2.toString();
                        }
                        sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("youtubecode", 0);
                        if (str6 == null) {
                            string = null;
                            string2 = null;
                        } else {
                            string = sharedPreferences.getString(str6, null);
                            stringBuilder7 = new StringBuilder();
                            stringBuilder7.append(str6);
                            stringBuilder7.append("n");
                            string2 = sharedPreferences.getString(stringBuilder7.toString(), null);
                        }
                        if (string != null) {
                            r5 = null;
                        } else {
                            if (str5.startsWith("//")) {
                                stringBuilder4 = new StringBuilder();
                                stringBuilder4.append("https:");
                                stringBuilder4.append(str5);
                                str5 = stringBuilder4.toString();
                            } else if (str5.startsWith("/")) {
                                stringBuilder4 = new StringBuilder();
                                stringBuilder4.append("https://www.youtube.com");
                                stringBuilder4.append(str5);
                                str5 = stringBuilder4.toString();
                            }
                            downloadUrlContent3 = WebPlayerView.this.downloadUrlContent(r1, str5);
                            if (isCancelled()) {
                                return null;
                            }
                            r5 = null;
                            if (downloadUrlContent3 != null) {
                                matcher2 = WebPlayerView.sigPattern.matcher(downloadUrlContent3);
                                if (matcher2.find()) {
                                    i8 = 1;
                                    matcher2 = WebPlayerView.sigPattern2.matcher(downloadUrlContent3);
                                    if (matcher2.find()) {
                                        string2 = matcher2.group(1);
                                    }
                                } else {
                                    i8 = 1;
                                    string2 = matcher2.group(1);
                                }
                                if (string2 != null) {
                                    str5 = new JSExtractor(downloadUrlContent3).extractFunction(string2);
                                    putString = sharedPreferences.edit().putString(str6, str5);
                                    stringBuilder5 = new StringBuilder();
                                    stringBuilder5.append(str6);
                                    stringBuilder5.append("n");
                                    putString.putString(stringBuilder5.toString(), string2).commit();
                                    string = str5;
                                }
                                if (TextUtils.isEmpty(string)) {
                                    if (VERSION.SDK_INT < 21) {
                                        stringBuilder = new StringBuilder();
                                        stringBuilder.append(string);
                                        stringBuilder.append(string2);
                                        stringBuilder.append("('");
                                        stringBuilder.append(r1.sig.substring(3));
                                        stringBuilder.append("');");
                                        str6 = stringBuilder.toString();
                                    } else {
                                        stringBuilder = new StringBuilder();
                                        stringBuilder.append(string);
                                        stringBuilder.append("window.");
                                        stringBuilder.append(WebPlayerView.this.interfaceName);
                                        stringBuilder.append(".returnResultToJava(");
                                        stringBuilder.append(string2);
                                        stringBuilder.append("('");
                                        stringBuilder.append(r1.sig.substring(3));
                                        stringBuilder.append("'));");
                                        str6 = stringBuilder.toString();
                                    }
                                    AndroidUtilities.runOnUIThread(/* anonymous class already generated */);
                                    r1.countDownLatch.await();
                                    i = 0;
                                }
                                i = i8;
                            }
                        }
                        i8 = 1;
                        if (TextUtils.isEmpty(string)) {
                            if (VERSION.SDK_INT < 21) {
                                stringBuilder = new StringBuilder();
                                stringBuilder.append(string);
                                stringBuilder.append("window.");
                                stringBuilder.append(WebPlayerView.this.interfaceName);
                                stringBuilder.append(".returnResultToJava(");
                                stringBuilder.append(string2);
                                stringBuilder.append("('");
                                stringBuilder.append(r1.sig.substring(3));
                                stringBuilder.append("'));");
                                str6 = stringBuilder.toString();
                            } else {
                                stringBuilder = new StringBuilder();
                                stringBuilder.append(string);
                                stringBuilder.append(string2);
                                stringBuilder.append("('");
                                stringBuilder.append(r1.sig.substring(3));
                                stringBuilder.append("');");
                                str6 = stringBuilder.toString();
                            }
                            AndroidUtilities.runOnUIThread(/* anonymous class already generated */);
                            r1.countDownLatch.await();
                            i = 0;
                        }
                        i = i8;
                    }
                }
                r5 = null;
                i8 = 1;
                i = i8;
            }
            if (isCancelled()) {
                if (i == 0) {
                    strArr = r1.result;
                    return strArr;
                }
            }
            strArr = r5;
            return strArr;
        }

        private void onInterfaceResult(String str) {
            String[] strArr = this.result;
            String str2 = this.result[0];
            CharSequence charSequence = this.sig;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("/signature/");
            stringBuilder.append(str);
            strArr[0] = str2.replace(charSequence, stringBuilder.toString());
            this.countDownLatch.countDown();
        }

        protected void onPostExecute(String[] strArr) {
            if (strArr[0] != null) {
                if (BuildVars.LOGS_ENABLED) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("start play youtube video ");
                    stringBuilder.append(strArr[1]);
                    stringBuilder.append(" ");
                    stringBuilder.append(strArr[0]);
                    FileLog.m0d(stringBuilder.toString());
                }
                WebPlayerView.this.initied = true;
                WebPlayerView.this.playVideoUrl = strArr[0];
                WebPlayerView.this.playVideoType = strArr[1];
                if (WebPlayerView.this.playVideoType.equals("hls") != null) {
                    WebPlayerView.this.isStream = true;
                }
                if (WebPlayerView.this.isAutoplay != null) {
                    WebPlayerView.this.preparePlayer();
                }
                WebPlayerView.this.showProgress(false, true);
                WebPlayerView.this.controlsView.show(true, true);
            } else if (isCancelled() == null) {
                WebPlayerView.this.onInitFailed();
            }
        }
    }

    private abstract class function {
        public abstract Object run(Object[] objArr);

        private function() {
        }
    }

    /* renamed from: org.telegram.ui.Components.WebPlayerView$5 */
    class C21045 implements CallJavaResultInterface {
        C21045() {
        }

        public void jsCallFinished(String str) {
            if (WebPlayerView.this.currentTask != null && !WebPlayerView.this.currentTask.isCancelled() && (WebPlayerView.this.currentTask instanceof YoutubeVideoTask)) {
                ((YoutubeVideoTask) WebPlayerView.this.currentTask).onInterfaceResult(str);
            }
        }
    }

    protected String downloadUrlContent(AsyncTask asyncTask, String str) {
        return downloadUrlContent(asyncTask, str, null, true);
    }

    protected java.lang.String downloadUrlContent(android.os.AsyncTask r9, java.lang.String r10, java.util.HashMap<java.lang.String, java.lang.String> r11, boolean r12) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r8 = this;
        r0 = 1;
        r1 = 0;
        r2 = 0;
        r3 = new java.net.URL;	 Catch:{ Throwable -> 0x010f }
        r3.<init>(r10);	 Catch:{ Throwable -> 0x010f }
        r10 = r3.openConnection();	 Catch:{ Throwable -> 0x010f }
        r4 = "User-Agent";	 Catch:{ Throwable -> 0x010d }
        r5 = "Mozilla/5.0 (X11; Linux x86_64; rv:10.0) Gecko/20150101 Firefox/47.0 (Chrome)";	 Catch:{ Throwable -> 0x010d }
        r10.addRequestProperty(r4, r5);	 Catch:{ Throwable -> 0x010d }
        if (r12 == 0) goto L_0x001c;	 Catch:{ Throwable -> 0x010d }
    L_0x0015:
        r4 = "Accept-Encoding";	 Catch:{ Throwable -> 0x010d }
        r5 = "gzip, deflate";	 Catch:{ Throwable -> 0x010d }
        r10.addRequestProperty(r4, r5);	 Catch:{ Throwable -> 0x010d }
    L_0x001c:
        r4 = "Accept-Language";	 Catch:{ Throwable -> 0x010d }
        r5 = "en-us,en;q=0.5";	 Catch:{ Throwable -> 0x010d }
        r10.addRequestProperty(r4, r5);	 Catch:{ Throwable -> 0x010d }
        r4 = "Accept";	 Catch:{ Throwable -> 0x010d }
        r5 = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";	 Catch:{ Throwable -> 0x010d }
        r10.addRequestProperty(r4, r5);	 Catch:{ Throwable -> 0x010d }
        r4 = "Accept-Charset";	 Catch:{ Throwable -> 0x010d }
        r5 = "ISO-8859-1,utf-8;q=0.7,*;q=0.7";	 Catch:{ Throwable -> 0x010d }
        r10.addRequestProperty(r4, r5);	 Catch:{ Throwable -> 0x010d }
        if (r11 == 0) goto L_0x0057;	 Catch:{ Throwable -> 0x010d }
    L_0x0033:
        r4 = r11.entrySet();	 Catch:{ Throwable -> 0x010d }
        r4 = r4.iterator();	 Catch:{ Throwable -> 0x010d }
    L_0x003b:
        r5 = r4.hasNext();	 Catch:{ Throwable -> 0x010d }
        if (r5 == 0) goto L_0x0057;	 Catch:{ Throwable -> 0x010d }
    L_0x0041:
        r5 = r4.next();	 Catch:{ Throwable -> 0x010d }
        r5 = (java.util.Map.Entry) r5;	 Catch:{ Throwable -> 0x010d }
        r6 = r5.getKey();	 Catch:{ Throwable -> 0x010d }
        r6 = (java.lang.String) r6;	 Catch:{ Throwable -> 0x010d }
        r5 = r5.getValue();	 Catch:{ Throwable -> 0x010d }
        r5 = (java.lang.String) r5;	 Catch:{ Throwable -> 0x010d }
        r10.addRequestProperty(r6, r5);	 Catch:{ Throwable -> 0x010d }
        goto L_0x003b;	 Catch:{ Throwable -> 0x010d }
    L_0x0057:
        r4 = 5000; // 0x1388 float:7.006E-42 double:2.4703E-320;	 Catch:{ Throwable -> 0x010d }
        r10.setConnectTimeout(r4);	 Catch:{ Throwable -> 0x010d }
        r10.setReadTimeout(r4);	 Catch:{ Throwable -> 0x010d }
        r4 = r10 instanceof java.net.HttpURLConnection;	 Catch:{ Throwable -> 0x010d }
        if (r4 == 0) goto L_0x00e4;	 Catch:{ Throwable -> 0x010d }
    L_0x0063:
        r4 = r10;	 Catch:{ Throwable -> 0x010d }
        r4 = (java.net.HttpURLConnection) r4;	 Catch:{ Throwable -> 0x010d }
        r4.setInstanceFollowRedirects(r0);	 Catch:{ Throwable -> 0x010d }
        r5 = r4.getResponseCode();	 Catch:{ Throwable -> 0x010d }
        r6 = 302; // 0x12e float:4.23E-43 double:1.49E-321;	 Catch:{ Throwable -> 0x010d }
        if (r5 == r6) goto L_0x0079;	 Catch:{ Throwable -> 0x010d }
    L_0x0071:
        r6 = 301; // 0x12d float:4.22E-43 double:1.487E-321;	 Catch:{ Throwable -> 0x010d }
        if (r5 == r6) goto L_0x0079;	 Catch:{ Throwable -> 0x010d }
    L_0x0075:
        r6 = 303; // 0x12f float:4.25E-43 double:1.497E-321;	 Catch:{ Throwable -> 0x010d }
        if (r5 != r6) goto L_0x00e4;	 Catch:{ Throwable -> 0x010d }
    L_0x0079:
        r3 = "Location";	 Catch:{ Throwable -> 0x010d }
        r3 = r4.getHeaderField(r3);	 Catch:{ Throwable -> 0x010d }
        r5 = "Set-Cookie";	 Catch:{ Throwable -> 0x010d }
        r4 = r4.getHeaderField(r5);	 Catch:{ Throwable -> 0x010d }
        r5 = new java.net.URL;	 Catch:{ Throwable -> 0x010d }
        r5.<init>(r3);	 Catch:{ Throwable -> 0x010d }
        r3 = r5.openConnection();	 Catch:{ Throwable -> 0x010d }
        r10 = "Cookie";	 Catch:{ Throwable -> 0x00e1 }
        r3.setRequestProperty(r10, r4);	 Catch:{ Throwable -> 0x00e1 }
        r10 = "User-Agent";	 Catch:{ Throwable -> 0x00e1 }
        r4 = "Mozilla/5.0 (X11; Linux x86_64; rv:10.0) Gecko/20150101 Firefox/47.0 (Chrome)";	 Catch:{ Throwable -> 0x00e1 }
        r3.addRequestProperty(r10, r4);	 Catch:{ Throwable -> 0x00e1 }
        if (r12 == 0) goto L_0x00a3;	 Catch:{ Throwable -> 0x00e1 }
    L_0x009c:
        r10 = "Accept-Encoding";	 Catch:{ Throwable -> 0x00e1 }
        r4 = "gzip, deflate";	 Catch:{ Throwable -> 0x00e1 }
        r3.addRequestProperty(r10, r4);	 Catch:{ Throwable -> 0x00e1 }
    L_0x00a3:
        r10 = "Accept-Language";	 Catch:{ Throwable -> 0x00e1 }
        r4 = "en-us,en;q=0.5";	 Catch:{ Throwable -> 0x00e1 }
        r3.addRequestProperty(r10, r4);	 Catch:{ Throwable -> 0x00e1 }
        r10 = "Accept";	 Catch:{ Throwable -> 0x00e1 }
        r4 = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";	 Catch:{ Throwable -> 0x00e1 }
        r3.addRequestProperty(r10, r4);	 Catch:{ Throwable -> 0x00e1 }
        r10 = "Accept-Charset";	 Catch:{ Throwable -> 0x00e1 }
        r4 = "ISO-8859-1,utf-8;q=0.7,*;q=0.7";	 Catch:{ Throwable -> 0x00e1 }
        r3.addRequestProperty(r10, r4);	 Catch:{ Throwable -> 0x00e1 }
        if (r11 == 0) goto L_0x00de;	 Catch:{ Throwable -> 0x00e1 }
    L_0x00ba:
        r10 = r11.entrySet();	 Catch:{ Throwable -> 0x00e1 }
        r10 = r10.iterator();	 Catch:{ Throwable -> 0x00e1 }
    L_0x00c2:
        r11 = r10.hasNext();	 Catch:{ Throwable -> 0x00e1 }
        if (r11 == 0) goto L_0x00de;	 Catch:{ Throwable -> 0x00e1 }
    L_0x00c8:
        r11 = r10.next();	 Catch:{ Throwable -> 0x00e1 }
        r11 = (java.util.Map.Entry) r11;	 Catch:{ Throwable -> 0x00e1 }
        r4 = r11.getKey();	 Catch:{ Throwable -> 0x00e1 }
        r4 = (java.lang.String) r4;	 Catch:{ Throwable -> 0x00e1 }
        r11 = r11.getValue();	 Catch:{ Throwable -> 0x00e1 }
        r11 = (java.lang.String) r11;	 Catch:{ Throwable -> 0x00e1 }
        r3.addRequestProperty(r4, r11);	 Catch:{ Throwable -> 0x00e1 }
        goto L_0x00c2;
    L_0x00de:
        r10 = r3;
        r3 = r5;
        goto L_0x00e4;
    L_0x00e1:
        r11 = move-exception;
        r10 = r3;
        goto L_0x0111;
    L_0x00e4:
        r10.connect();	 Catch:{ Throwable -> 0x010d }
        if (r12 == 0) goto L_0x0107;
    L_0x00e9:
        r11 = new java.util.zip.GZIPInputStream;	 Catch:{ Exception -> 0x00f3 }
        r12 = r10.getInputStream();	 Catch:{ Exception -> 0x00f3 }
        r11.<init>(r12);	 Catch:{ Exception -> 0x00f3 }
        goto L_0x010b;
    L_0x00f3:
        r11 = r3.openConnection();	 Catch:{ Throwable -> 0x010d }
        r11.connect();	 Catch:{ Throwable -> 0x0102 }
        r10 = r11.getInputStream();	 Catch:{ Throwable -> 0x0102 }
        r7 = r11;
        r11 = r10;
        r10 = r7;
        goto L_0x010b;
    L_0x0102:
        r10 = move-exception;
        r7 = r11;
        r11 = r10;
        r10 = r7;
        goto L_0x0111;
    L_0x0107:
        r11 = r10.getInputStream();	 Catch:{ Throwable -> 0x010d }
    L_0x010b:
        r12 = r0;
        goto L_0x0143;
    L_0x010d:
        r11 = move-exception;
        goto L_0x0111;
    L_0x010f:
        r11 = move-exception;
        r10 = r1;
    L_0x0111:
        r12 = r11 instanceof java.net.SocketTimeoutException;
        if (r12 == 0) goto L_0x011d;
    L_0x0115:
        r12 = org.telegram.tgnet.ConnectionsManager.isNetworkOnline();
        if (r12 == 0) goto L_0x013e;
    L_0x011b:
        r12 = r2;
        goto L_0x013f;
    L_0x011d:
        r12 = r11 instanceof java.net.UnknownHostException;
        if (r12 == 0) goto L_0x0122;
    L_0x0121:
        goto L_0x011b;
    L_0x0122:
        r12 = r11 instanceof java.net.SocketException;
        if (r12 == 0) goto L_0x0139;
    L_0x0126:
        r12 = r11.getMessage();
        if (r12 == 0) goto L_0x013e;
    L_0x012c:
        r12 = r11.getMessage();
        r3 = "ECONNRESET";
        r12 = r12.contains(r3);
        if (r12 == 0) goto L_0x013e;
    L_0x0138:
        goto L_0x011b;
    L_0x0139:
        r12 = r11 instanceof java.io.FileNotFoundException;
        if (r12 == 0) goto L_0x013e;
    L_0x013d:
        goto L_0x011b;
    L_0x013e:
        r12 = r0;
    L_0x013f:
        org.telegram.messenger.FileLog.m3e(r11);
        r11 = r1;
    L_0x0143:
        if (r12 == 0) goto L_0x01a2;
    L_0x0145:
        if (r10 == 0) goto L_0x015c;
    L_0x0147:
        r12 = r10 instanceof java.net.HttpURLConnection;	 Catch:{ Exception -> 0x0158 }
        if (r12 == 0) goto L_0x015c;	 Catch:{ Exception -> 0x0158 }
    L_0x014b:
        r10 = (java.net.HttpURLConnection) r10;	 Catch:{ Exception -> 0x0158 }
        r10 = r10.getResponseCode();	 Catch:{ Exception -> 0x0158 }
        r12 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
        if (r10 == r12) goto L_0x015c;
    L_0x0155:
        r12 = 202; // 0xca float:2.83E-43 double:1.0E-321;
        goto L_0x015c;
    L_0x0158:
        r10 = move-exception;
        org.telegram.messenger.FileLog.m3e(r10);
    L_0x015c:
        if (r11 == 0) goto L_0x0196;
    L_0x015e:
        r10 = 32768; // 0x8000 float:4.5918E-41 double:1.61895E-319;
        r10 = new byte[r10];	 Catch:{ Throwable -> 0x0190 }
        r12 = r1;
    L_0x0164:
        r3 = r9.isCancelled();	 Catch:{ Throwable -> 0x018e }
        if (r3 == 0) goto L_0x016b;
    L_0x016a:
        goto L_0x0197;
    L_0x016b:
        r3 = r11.read(r10);	 Catch:{ Exception -> 0x0189 }
        if (r3 <= 0) goto L_0x0184;	 Catch:{ Exception -> 0x0189 }
    L_0x0171:
        if (r12 != 0) goto L_0x0179;	 Catch:{ Exception -> 0x0189 }
    L_0x0173:
        r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0189 }
        r4.<init>();	 Catch:{ Exception -> 0x0189 }
        r12 = r4;	 Catch:{ Exception -> 0x0189 }
    L_0x0179:
        r4 = new java.lang.String;	 Catch:{ Exception -> 0x0189 }
        r5 = "UTF-8";	 Catch:{ Exception -> 0x0189 }
        r4.<init>(r10, r2, r3, r5);	 Catch:{ Exception -> 0x0189 }
        r12.append(r4);	 Catch:{ Exception -> 0x0189 }
        goto L_0x0164;
    L_0x0184:
        r9 = -1;
        if (r3 != r9) goto L_0x0197;
    L_0x0187:
        r2 = r0;
        goto L_0x0197;
    L_0x0189:
        r9 = move-exception;
        org.telegram.messenger.FileLog.m3e(r9);	 Catch:{ Throwable -> 0x018e }
        goto L_0x0197;
    L_0x018e:
        r9 = move-exception;
        goto L_0x0192;
    L_0x0190:
        r9 = move-exception;
        r12 = r1;
    L_0x0192:
        org.telegram.messenger.FileLog.m3e(r9);
        goto L_0x0197;
    L_0x0196:
        r12 = r1;
    L_0x0197:
        if (r11 == 0) goto L_0x01a3;
    L_0x0199:
        r11.close();	 Catch:{ Throwable -> 0x019d }
        goto L_0x01a3;
    L_0x019d:
        r9 = move-exception;
        org.telegram.messenger.FileLog.m3e(r9);
        goto L_0x01a3;
    L_0x01a2:
        r12 = r1;
    L_0x01a3:
        if (r2 == 0) goto L_0x01a9;
    L_0x01a5:
        r1 = r12.toString();
    L_0x01a9:
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.WebPlayerView.downloadUrlContent(android.os.AsyncTask, java.lang.String, java.util.HashMap, boolean):java.lang.String");
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    public WebPlayerView(Context context, boolean z, boolean z2, WebPlayerViewDelegate webPlayerViewDelegate) {
        super(context);
        int i = lastContainerId;
        lastContainerId = i + 1;
        this.fragment_container_id = i;
        this.allowInlineAnimation = VERSION.SDK_INT >= 21;
        this.backgroundPaint = new Paint();
        this.progressRunnable = new C13361();
        this.surfaceTextureListener = new C13392();
        this.switchToInlineRunnable = new C13403();
        setWillNotDraw(false);
        this.delegate = webPlayerViewDelegate;
        this.backgroundPaint.setColor(Theme.ACTION_BAR_VIDEO_EDIT_COLOR);
        this.aspectRatioFrameLayout = new AspectRatioFrameLayout(context) {
            protected void onMeasure(int i, int i2) {
                super.onMeasure(i, i2);
                if (WebPlayerView.this.textureViewContainer != 0) {
                    i = WebPlayerView.this.textureView.getLayoutParams();
                    i.width = getMeasuredWidth();
                    i.height = getMeasuredHeight();
                    if (WebPlayerView.this.textureImageView != 0) {
                        i = WebPlayerView.this.textureImageView.getLayoutParams();
                        i.width = getMeasuredWidth();
                        i.height = getMeasuredHeight();
                    }
                }
            }
        };
        addView(this.aspectRatioFrameLayout, LayoutHelper.createFrame(-1, -1, 17));
        this.interfaceName = "JavaScriptInterface";
        this.webView = new WebView(context);
        this.webView.addJavascriptInterface(new JavaScriptInterface(new C21045()), this.interfaceName);
        webPlayerViewDelegate = this.webView.getSettings();
        webPlayerViewDelegate.setJavaScriptEnabled(true);
        webPlayerViewDelegate.setDefaultTextEncodingName("utf-8");
        this.textureViewContainer = this.delegate.getTextureViewContainer();
        this.textureView = new TextureView(context);
        this.textureView.setPivotX(0.0f);
        this.textureView.setPivotY(0.0f);
        if (this.textureViewContainer != null) {
            this.textureViewContainer.addView(this.textureView);
        } else {
            this.aspectRatioFrameLayout.addView(this.textureView, LayoutHelper.createFrame(-1, -1, 17));
        }
        if (!(this.allowInlineAnimation == null || this.textureViewContainer == null)) {
            this.textureImageView = new ImageView(context);
            this.textureImageView.setBackgroundColor(-65536);
            this.textureImageView.setPivotX(0.0f);
            this.textureImageView.setPivotY(0.0f);
            this.textureImageView.setVisibility(4);
            this.textureViewContainer.addView(this.textureImageView);
        }
        this.videoPlayer = new VideoPlayer();
        this.videoPlayer.setDelegate(this);
        this.videoPlayer.setTextureView(this.textureView);
        this.controlsView = new ControlsView(context);
        if (this.textureViewContainer != null) {
            this.textureViewContainer.addView(this.controlsView);
        } else {
            addView(this.controlsView, LayoutHelper.createFrame(-1, -1.0f));
        }
        this.progressView = new RadialProgressView(context);
        this.progressView.setProgressColor(-1);
        addView(this.progressView, LayoutHelper.createFrame(48, 48, 17));
        this.fullscreenButton = new ImageView(context);
        this.fullscreenButton.setScaleType(ScaleType.CENTER);
        this.controlsView.addView(this.fullscreenButton, LayoutHelper.createFrame(56, 56.0f, 85, 0.0f, 0.0f, 0.0f, 5.0f));
        this.fullscreenButton.setOnClickListener(new C13416());
        this.playButton = new ImageView(context);
        this.playButton.setScaleType(ScaleType.CENTER);
        this.controlsView.addView(this.playButton, LayoutHelper.createFrame(48, 48, 17));
        this.playButton.setOnClickListener(new C13427());
        if (z) {
            this.inlineButton = new ImageView(context);
            this.inlineButton.setScaleType(ScaleType.CENTER);
            this.controlsView.addView(this.inlineButton, LayoutHelper.createFrame(56, 48, 53));
            this.inlineButton.setOnClickListener(new C13438());
        }
        if (z2) {
            this.shareButton = new ImageView(context);
            this.shareButton.setScaleType(ScaleType.CENTER);
            this.shareButton.setImageResource(true);
            this.controlsView.addView(this.shareButton, LayoutHelper.createFrame(56, 48, 53));
            this.shareButton.setOnClickListener(new C13449());
        }
        updatePlayButton();
        updateFullscreenButton();
        updateInlineButton();
        updateShareButton();
    }

    private void onInitFailed() {
        if (this.controlsView.getParent() != this) {
            this.controlsView.setVisibility(8);
        }
        this.delegate.onInitFailed();
    }

    public void updateTextureImageView() {
        if (this.textureImageView != null) {
            try {
                this.currentBitmap = Bitmaps.createBitmap(this.textureView.getWidth(), this.textureView.getHeight(), Config.ARGB_8888);
                this.changedTextureView.getBitmap(this.currentBitmap);
            } catch (Throwable th) {
                if (this.currentBitmap != null) {
                    this.currentBitmap.recycle();
                    this.currentBitmap = null;
                }
                FileLog.m3e(th);
            }
            if (this.currentBitmap != null) {
                this.textureImageView.setVisibility(0);
                this.textureImageView.setImageBitmap(this.currentBitmap);
            } else {
                this.textureImageView.setImageDrawable(null);
            }
        }
    }

    public String getYoutubeId() {
        return this.currentYoutubeId;
    }

    public void onStateChanged(boolean z, int i) {
        if (i != 2) {
            if (this.videoPlayer.getDuration() != C0542C.TIME_UNSET) {
                this.controlsView.setDuration((int) (this.videoPlayer.getDuration() / 1000));
            } else {
                this.controlsView.setDuration(0);
            }
        }
        if (i == 4 || i == 1 || !this.videoPlayer.isPlaying()) {
            this.delegate.onPlayStateChanged(this, false);
        } else {
            this.delegate.onPlayStateChanged(this, true);
        }
        if (this.videoPlayer.isPlaying() && i != 4) {
            updatePlayButton();
        } else if (i == 4) {
            this.isCompleted = true;
            this.videoPlayer.pause();
            this.videoPlayer.seekTo(0);
            updatePlayButton();
            this.controlsView.show(true, true);
        }
    }

    protected void onDraw(Canvas canvas) {
        canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) (getMeasuredHeight() - AndroidUtilities.dp(10.0f)), this.backgroundPaint);
    }

    public void onError(Exception exception) {
        FileLog.m3e((Throwable) exception);
        onInitFailed();
    }

    public void onVideoSizeChanged(int i, int i2, int i3, float f) {
        if (this.aspectRatioFrameLayout != null) {
            if (i3 != 90) {
                if (i3 != 270) {
                    int i4 = i2;
                    i2 = i;
                    i = i4;
                }
            }
            i = i == 0 ? NUM : (((float) i2) * f) / ((float) i);
            this.aspectRatioFrameLayout.setAspectRatio(i, i3);
            if (this.inFullscreen != 0) {
                this.delegate.onVideoSizeChanged(i, i3);
            }
        }
    }

    public void onRenderedFirstFrame() {
        this.firstFrameRendered = true;
        this.lastUpdateTime = System.currentTimeMillis();
        this.controlsView.invalidate();
    }

    public boolean onSurfaceDestroyed(SurfaceTexture surfaceTexture) {
        if (this.changingTextureView) {
            this.changingTextureView = false;
            if (this.inFullscreen || this.isInline) {
                if (this.isInline) {
                    this.waitingForFirstTextureUpload = 1;
                }
                this.changedTextureView.setSurfaceTexture(surfaceTexture);
                this.changedTextureView.setSurfaceTextureListener(this.surfaceTextureListener);
                this.changedTextureView.setVisibility(0);
                return true;
            }
        }
        return false;
    }

    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        if (this.waitingForFirstTextureUpload == 2) {
            if (this.textureImageView != null) {
                this.textureImageView.setVisibility(4);
                this.textureImageView.setImageDrawable(null);
                if (this.currentBitmap != null) {
                    this.currentBitmap.recycle();
                    this.currentBitmap = null;
                }
            }
            this.switchingInlineMode = false;
            this.delegate.onSwitchInlineMode(this.controlsView, false, this.aspectRatioFrameLayout.getAspectRatio(), this.aspectRatioFrameLayout.getVideoRotation(), this.allowInlineAnimation);
            this.waitingForFirstTextureUpload = 0;
        }
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        i3 -= i;
        z = (i3 - this.aspectRatioFrameLayout.getMeasuredWidth()) / 2;
        i4 -= i2;
        i2 = ((i4 - AndroidUtilities.dp(10.0f)) - this.aspectRatioFrameLayout.getMeasuredHeight()) / 2;
        this.aspectRatioFrameLayout.layout(z, i2, this.aspectRatioFrameLayout.getMeasuredWidth() + z, this.aspectRatioFrameLayout.getMeasuredHeight() + i2);
        if (this.controlsView.getParent() == this) {
            this.controlsView.layout(0, 0, this.controlsView.getMeasuredWidth(), this.controlsView.getMeasuredHeight());
        }
        i3 = (i3 - this.progressView.getMeasuredWidth()) / 2;
        i4 = (i4 - this.progressView.getMeasuredHeight()) / 2;
        this.progressView.layout(i3, i4, this.progressView.getMeasuredWidth() + i3, this.progressView.getMeasuredHeight() + i4);
        this.controlsView.imageReceiver.setImageCoords(0, 0, getMeasuredWidth(), getMeasuredHeight() - AndroidUtilities.dp(10.0f));
    }

    protected void onMeasure(int i, int i2) {
        i = MeasureSpec.getSize(i);
        i2 = MeasureSpec.getSize(i2);
        this.aspectRatioFrameLayout.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(i2 - AndroidUtilities.dp(10.0f), NUM));
        if (this.controlsView.getParent() == this) {
            this.controlsView.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(i2, NUM));
        }
        this.progressView.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(44.0f), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(44.0f), NUM));
        setMeasuredDimension(i, i2);
    }

    private void updatePlayButton() {
        this.controlsView.checkNeedHide();
        AndroidUtilities.cancelRunOnUIThread(this.progressRunnable);
        if (this.videoPlayer.isPlaying()) {
            this.playButton.setImageResource(this.isInline ? C0446R.drawable.ic_pauseinline : C0446R.drawable.ic_pause);
            AndroidUtilities.runOnUIThread(this.progressRunnable, 500);
            checkAudioFocus();
        } else if (this.isCompleted) {
            this.playButton.setImageResource(this.isInline ? C0446R.drawable.ic_againinline : C0446R.drawable.ic_again);
        } else {
            this.playButton.setImageResource(this.isInline ? C0446R.drawable.ic_playinline : C0446R.drawable.ic_play);
        }
    }

    private void checkAudioFocus() {
        if (!this.hasAudioFocus) {
            AudioManager audioManager = (AudioManager) ApplicationLoader.applicationContext.getSystemService(MimeTypes.BASE_TYPE_AUDIO);
            this.hasAudioFocus = true;
            if (audioManager.requestAudioFocus(this, 3, 1) == 1) {
                this.audioFocus = 2;
            }
        }
    }

    public void onAudioFocusChange(int i) {
        if (i == -1) {
            if (this.videoPlayer.isPlaying() != 0) {
                this.videoPlayer.pause();
                updatePlayButton();
            }
            this.hasAudioFocus = false;
            this.audioFocus = 0;
        } else if (i == 1) {
            this.audioFocus = 2;
            if (this.resumeAudioOnFocusGain != 0) {
                this.resumeAudioOnFocusGain = false;
                this.videoPlayer.play();
            }
        } else if (i == -3) {
            this.audioFocus = 1;
        } else if (i == -2) {
            this.audioFocus = 0;
            if (this.videoPlayer.isPlaying() != 0) {
                this.resumeAudioOnFocusGain = true;
                this.videoPlayer.pause();
                updatePlayButton();
            }
        }
    }

    private void updateFullscreenButton() {
        if (this.videoPlayer.isPlayerPrepared()) {
            if (!this.isInline) {
                this.fullscreenButton.setVisibility(0);
                if (this.inFullscreen) {
                    this.fullscreenButton.setImageResource(C0446R.drawable.ic_outfullscreen);
                    this.fullscreenButton.setLayoutParams(LayoutHelper.createFrame(56, 56.0f, 85, 0.0f, 0.0f, 0.0f, 1.0f));
                } else {
                    this.fullscreenButton.setImageResource(C0446R.drawable.ic_gofullscreen);
                    this.fullscreenButton.setLayoutParams(LayoutHelper.createFrame(56, 56.0f, 85, 0.0f, 0.0f, 0.0f, 5.0f));
                }
                return;
            }
        }
        this.fullscreenButton.setVisibility(8);
    }

    private void updateShareButton() {
        if (this.shareButton != null) {
            int i;
            ImageView imageView = this.shareButton;
            if (!this.isInline) {
                if (this.videoPlayer.isPlayerPrepared()) {
                    i = 0;
                    imageView.setVisibility(i);
                }
            }
            i = 8;
            imageView.setVisibility(i);
        }
    }

    private View getControlView() {
        return this.controlsView;
    }

    private View getProgressView() {
        return this.progressView;
    }

    private void updateInlineButton() {
        if (this.inlineButton != null) {
            this.inlineButton.setImageResource(this.isInline ? C0446R.drawable.ic_goinline : C0446R.drawable.ic_outinline);
            this.inlineButton.setVisibility(this.videoPlayer.isPlayerPrepared() ? 0 : 8);
            if (this.isInline) {
                this.inlineButton.setLayoutParams(LayoutHelper.createFrame(40, 40, 53));
            } else {
                this.inlineButton.setLayoutParams(LayoutHelper.createFrame(56, 50, 53));
            }
        }
    }

    private void preparePlayer() {
        if (this.playVideoUrl != null) {
            if (this.playVideoUrl == null || this.playAudioUrl == null) {
                this.videoPlayer.preparePlayer(Uri.parse(this.playVideoUrl), this.playVideoType);
            } else {
                this.videoPlayer.preparePlayerLoop(Uri.parse(this.playVideoUrl), this.playVideoType, Uri.parse(this.playAudioUrl), this.playAudioType);
            }
            this.videoPlayer.setPlayWhenReady(this.isAutoplay);
            this.isLoading = false;
            if (this.videoPlayer.getDuration() != C0542C.TIME_UNSET) {
                this.controlsView.setDuration((int) (this.videoPlayer.getDuration() / 1000));
            } else {
                this.controlsView.setDuration(0);
            }
            updateFullscreenButton();
            updateShareButton();
            updateInlineButton();
            this.controlsView.invalidate();
            if (this.seekToTime != -1) {
                this.videoPlayer.seekTo((long) (this.seekToTime * 1000));
            }
        }
    }

    public void pause() {
        this.videoPlayer.pause();
        updatePlayButton();
        this.controlsView.show(true, true);
    }

    private void updateFullscreenState(boolean z) {
        if (this.textureView != null) {
            updateFullscreenButton();
            ViewGroup viewGroup;
            if (this.textureViewContainer == null) {
                this.changingTextureView = true;
                if (!this.inFullscreen) {
                    if (this.textureViewContainer != null) {
                        this.textureViewContainer.addView(this.textureView);
                    } else {
                        this.aspectRatioFrameLayout.addView(this.textureView);
                    }
                }
                if (this.inFullscreen) {
                    viewGroup = (ViewGroup) this.controlsView.getParent();
                    if (viewGroup != null) {
                        viewGroup.removeView(this.controlsView);
                    }
                } else {
                    ViewGroup viewGroup2 = (ViewGroup) this.controlsView.getParent();
                    if (viewGroup2 != this) {
                        if (viewGroup2 != null) {
                            viewGroup2.removeView(this.controlsView);
                        }
                        if (this.textureViewContainer != null) {
                            this.textureViewContainer.addView(this.controlsView);
                        } else {
                            addView(this.controlsView, 1);
                        }
                    }
                }
                this.changedTextureView = this.delegate.onSwitchToFullscreen(this.controlsView, this.inFullscreen, this.aspectRatioFrameLayout.getAspectRatio(), this.aspectRatioFrameLayout.getVideoRotation(), z);
                this.changedTextureView.setVisibility(4);
                if (this.inFullscreen && this.changedTextureView) {
                    ViewGroup viewGroup3 = (ViewGroup) this.textureView.getParent();
                    if (viewGroup3 != null) {
                        viewGroup3.removeView(this.textureView);
                    }
                }
                this.controlsView.checkNeedHide();
            } else {
                if (this.inFullscreen) {
                    viewGroup = (ViewGroup) this.aspectRatioFrameLayout.getParent();
                    if (viewGroup != null) {
                        viewGroup.removeView(this.aspectRatioFrameLayout);
                    }
                } else {
                    viewGroup = (ViewGroup) this.aspectRatioFrameLayout.getParent();
                    if (viewGroup != this) {
                        if (viewGroup != null) {
                            viewGroup.removeView(this.aspectRatioFrameLayout);
                        }
                        addView(this.aspectRatioFrameLayout, 0);
                    }
                }
                this.delegate.onSwitchToFullscreen(this.controlsView, this.inFullscreen, this.aspectRatioFrameLayout.getAspectRatio(), this.aspectRatioFrameLayout.getVideoRotation(), z);
            }
        }
    }

    public void exitFullscreen() {
        if (this.inFullscreen) {
            this.inFullscreen = false;
            updateInlineButton();
            updateFullscreenState(false);
        }
    }

    public boolean isInitied() {
        return this.initied;
    }

    public boolean isInline() {
        if (!this.isInline) {
            if (!this.switchingInlineMode) {
                return false;
            }
        }
        return true;
    }

    public void enterFullscreen() {
        if (!this.inFullscreen) {
            this.inFullscreen = true;
            updateInlineButton();
            updateFullscreenState(false);
        }
    }

    public boolean isInFullscreen() {
        return this.inFullscreen;
    }

    public boolean loadVideo(String str, Photo photo, String str2, boolean z) {
        String str3;
        String str4;
        String str5;
        String group;
        String group2;
        String group3;
        String str6;
        PhotoSize closestPhotoSizeWithSize;
        AsyncTask youtubeVideoTask;
        AsyncTask twitchClipVideoTask;
        String str7 = str;
        Photo photo2 = photo;
        this.seekToTime = -1;
        if (str7 == null) {
            str3 = null;
            str4 = str3;
            str5 = str4;
        } else if (str7.endsWith(".mp4")) {
            str4 = str7;
            str3 = null;
            str5 = str3;
        } else {
            Matcher matcher;
            Matcher matcher2;
            Matcher matcher3;
            Matcher matcher4;
            Matcher matcher5;
            if (str2 != null) {
                try {
                    Uri parse = Uri.parse(str2);
                    str4 = parse.getQueryParameter("t");
                    if (str4 == null) {
                        str4 = parse.getQueryParameter("time_continue");
                    }
                    if (str4 != null) {
                        if (str4.contains("m")) {
                            String[] split = str4.split("m");
                            r1.seekToTime = (Utilities.parseInt(split[0]).intValue() * 60) + Utilities.parseInt(split[1]).intValue();
                        } else {
                            r1.seekToTime = Utilities.parseInt(str4).intValue();
                        }
                    }
                } catch (Throwable e) {
                    try {
                        FileLog.m3e(e);
                    } catch (Throwable e2) {
                        FileLog.m3e(e2);
                    }
                }
            }
            Matcher matcher6 = youtubeIdRegex.matcher(str7);
            str3 = matcher6.find() ? matcher6.group(1) : null;
            if (str3 != null) {
                if (str3 == null) {
                    try {
                        matcher = vimeoIdRegex.matcher(str7);
                        str4 = matcher.find() ? matcher.group(3) : null;
                        if (str4 != null) {
                            if (str4 == null) {
                                try {
                                    matcher2 = aparatIdRegex.matcher(str7);
                                    str5 = matcher2.find() ? matcher2.group(1) : null;
                                    if (str5 != null) {
                                        if (str5 == null) {
                                            try {
                                                matcher3 = twitchClipIdRegex.matcher(str7);
                                                group = matcher3.find() ? matcher3.group(1) : null;
                                                if (group != null) {
                                                    if (group == null) {
                                                        try {
                                                            matcher4 = twitchStreamIdRegex.matcher(str7);
                                                            group2 = matcher4.find() ? matcher4.group(1) : null;
                                                            if (group2 != null) {
                                                                if (group2 == null) {
                                                                    try {
                                                                        matcher5 = coubIdRegex.matcher(str7);
                                                                        group3 = matcher5.find() ? matcher5.group(1) : null;
                                                                        if (group3 == null) {
                                                                            group3 = null;
                                                                        }
                                                                        str6 = group2;
                                                                        group2 = group;
                                                                        group = str5;
                                                                        str5 = str4;
                                                                        str4 = null;
                                                                    } catch (Throwable e22) {
                                                                        FileLog.m3e(e22);
                                                                    }
                                                                    r1.initied = false;
                                                                    r1.isCompleted = false;
                                                                    r1.isAutoplay = z;
                                                                    r1.playVideoUrl = null;
                                                                    r1.playAudioUrl = null;
                                                                    destroy();
                                                                    r1.firstFrameRendered = false;
                                                                    r1.currentAlpha = 1.0f;
                                                                    if (r1.currentTask != null) {
                                                                        r1.currentTask.cancel(true);
                                                                        r1.currentTask = null;
                                                                    }
                                                                    updateFullscreenButton();
                                                                    updateShareButton();
                                                                    updateInlineButton();
                                                                    updatePlayButton();
                                                                    if (photo2 != null) {
                                                                        closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
                                                                        if (closestPhotoSizeWithSize != null) {
                                                                            r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                                                                            r1.drawImage = true;
                                                                        }
                                                                    } else {
                                                                        r1.drawImage = false;
                                                                    }
                                                                    if (r1.progressAnimation != null) {
                                                                        r1.progressAnimation.cancel();
                                                                        r1.progressAnimation = null;
                                                                    }
                                                                    r1.isLoading = true;
                                                                    r1.controlsView.setProgress(0);
                                                                    if (!(str3 == null || BuildVars.DEBUG_PRIVATE_VERSION)) {
                                                                        r1.currentYoutubeId = str3;
                                                                        str3 = null;
                                                                    }
                                                                    if (str4 != null) {
                                                                        r1.initied = true;
                                                                        r1.playVideoUrl = str4;
                                                                        r1.playVideoType = "other";
                                                                        if (r1.isAutoplay) {
                                                                            preparePlayer();
                                                                        }
                                                                        showProgress(false, false);
                                                                        r1.controlsView.show(true, true);
                                                                    } else {
                                                                        if (str3 != null) {
                                                                            youtubeVideoTask = new YoutubeVideoTask(str3);
                                                                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                                            r1.currentTask = youtubeVideoTask;
                                                                        } else if (str5 != null) {
                                                                            youtubeVideoTask = new VimeoVideoTask(str5);
                                                                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                                            r1.currentTask = youtubeVideoTask;
                                                                        } else if (group3 != null) {
                                                                            youtubeVideoTask = new CoubVideoTask(group3);
                                                                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                                            r1.currentTask = youtubeVideoTask;
                                                                            r1.isStream = true;
                                                                        } else if (group != null) {
                                                                            youtubeVideoTask = new AparatVideoTask(group);
                                                                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                                            r1.currentTask = youtubeVideoTask;
                                                                        } else if (group2 != null) {
                                                                            twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                                                                            twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                                            r1.currentTask = twitchClipVideoTask;
                                                                        } else if (str6 != null) {
                                                                            twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                                                                            twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                                            r1.currentTask = twitchClipVideoTask;
                                                                            r1.isStream = true;
                                                                        }
                                                                        r1.controlsView.show(false, false);
                                                                        showProgress(true, false);
                                                                    }
                                                                    if (str3 == null && str5 == null && group3 == null && group == null && str4 == null && group2 == null) {
                                                                        if (str6 == null) {
                                                                            r1.controlsView.setVisibility(8);
                                                                            return false;
                                                                        }
                                                                    }
                                                                    r1.controlsView.setVisibility(0);
                                                                    return true;
                                                                }
                                                                group3 = null;
                                                                str6 = group2;
                                                                group2 = group;
                                                                group = str5;
                                                                str5 = str4;
                                                                str4 = group3;
                                                                r1.initied = false;
                                                                r1.isCompleted = false;
                                                                r1.isAutoplay = z;
                                                                r1.playVideoUrl = null;
                                                                r1.playAudioUrl = null;
                                                                destroy();
                                                                r1.firstFrameRendered = false;
                                                                r1.currentAlpha = 1.0f;
                                                                if (r1.currentTask != null) {
                                                                    r1.currentTask.cancel(true);
                                                                    r1.currentTask = null;
                                                                }
                                                                updateFullscreenButton();
                                                                updateShareButton();
                                                                updateInlineButton();
                                                                updatePlayButton();
                                                                if (photo2 != null) {
                                                                    r1.drawImage = false;
                                                                } else {
                                                                    closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
                                                                    if (closestPhotoSizeWithSize != null) {
                                                                        if (photo2 != null) {
                                                                        }
                                                                        if (photo2 != null) {
                                                                        }
                                                                        r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                                                                        r1.drawImage = true;
                                                                    }
                                                                }
                                                                if (r1.progressAnimation != null) {
                                                                    r1.progressAnimation.cancel();
                                                                    r1.progressAnimation = null;
                                                                }
                                                                r1.isLoading = true;
                                                                r1.controlsView.setProgress(0);
                                                                r1.currentYoutubeId = str3;
                                                                str3 = null;
                                                                if (str4 != null) {
                                                                    if (str3 != null) {
                                                                        youtubeVideoTask = new YoutubeVideoTask(str3);
                                                                        youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                                        r1.currentTask = youtubeVideoTask;
                                                                    } else if (str5 != null) {
                                                                        youtubeVideoTask = new VimeoVideoTask(str5);
                                                                        youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                                        r1.currentTask = youtubeVideoTask;
                                                                    } else if (group3 != null) {
                                                                        youtubeVideoTask = new CoubVideoTask(group3);
                                                                        youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                                        r1.currentTask = youtubeVideoTask;
                                                                        r1.isStream = true;
                                                                    } else if (group != null) {
                                                                        youtubeVideoTask = new AparatVideoTask(group);
                                                                        youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                                        r1.currentTask = youtubeVideoTask;
                                                                    } else if (group2 != null) {
                                                                        twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                                                                        twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                                        r1.currentTask = twitchClipVideoTask;
                                                                    } else if (str6 != null) {
                                                                        twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                                                                        twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                                        r1.currentTask = twitchClipVideoTask;
                                                                        r1.isStream = true;
                                                                    }
                                                                    r1.controlsView.show(false, false);
                                                                    showProgress(true, false);
                                                                } else {
                                                                    r1.initied = true;
                                                                    r1.playVideoUrl = str4;
                                                                    r1.playVideoType = "other";
                                                                    if (r1.isAutoplay) {
                                                                        preparePlayer();
                                                                    }
                                                                    showProgress(false, false);
                                                                    r1.controlsView.show(true, true);
                                                                }
                                                                if (str6 == null) {
                                                                    r1.controlsView.setVisibility(8);
                                                                    return false;
                                                                }
                                                                r1.controlsView.setVisibility(0);
                                                                return true;
                                                            }
                                                        } catch (Throwable e222) {
                                                            FileLog.m3e(e222);
                                                        }
                                                    }
                                                    group2 = null;
                                                    if (group2 == null) {
                                                        matcher5 = coubIdRegex.matcher(str7);
                                                        if (matcher5.find()) {
                                                        }
                                                        if (group3 == null) {
                                                            group3 = null;
                                                        }
                                                        str6 = group2;
                                                        group2 = group;
                                                        group = str5;
                                                        str5 = str4;
                                                        str4 = null;
                                                        r1.initied = false;
                                                        r1.isCompleted = false;
                                                        r1.isAutoplay = z;
                                                        r1.playVideoUrl = null;
                                                        r1.playAudioUrl = null;
                                                        destroy();
                                                        r1.firstFrameRendered = false;
                                                        r1.currentAlpha = 1.0f;
                                                        if (r1.currentTask != null) {
                                                            r1.currentTask.cancel(true);
                                                            r1.currentTask = null;
                                                        }
                                                        updateFullscreenButton();
                                                        updateShareButton();
                                                        updateInlineButton();
                                                        updatePlayButton();
                                                        if (photo2 != null) {
                                                            closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
                                                            if (closestPhotoSizeWithSize != null) {
                                                                if (photo2 != null) {
                                                                }
                                                                if (photo2 != null) {
                                                                }
                                                                r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                                                                r1.drawImage = true;
                                                            }
                                                        } else {
                                                            r1.drawImage = false;
                                                        }
                                                        if (r1.progressAnimation != null) {
                                                            r1.progressAnimation.cancel();
                                                            r1.progressAnimation = null;
                                                        }
                                                        r1.isLoading = true;
                                                        r1.controlsView.setProgress(0);
                                                        r1.currentYoutubeId = str3;
                                                        str3 = null;
                                                        if (str4 != null) {
                                                            r1.initied = true;
                                                            r1.playVideoUrl = str4;
                                                            r1.playVideoType = "other";
                                                            if (r1.isAutoplay) {
                                                                preparePlayer();
                                                            }
                                                            showProgress(false, false);
                                                            r1.controlsView.show(true, true);
                                                        } else {
                                                            if (str3 != null) {
                                                                youtubeVideoTask = new YoutubeVideoTask(str3);
                                                                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                                r1.currentTask = youtubeVideoTask;
                                                            } else if (str5 != null) {
                                                                youtubeVideoTask = new VimeoVideoTask(str5);
                                                                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                                r1.currentTask = youtubeVideoTask;
                                                            } else if (group3 != null) {
                                                                youtubeVideoTask = new CoubVideoTask(group3);
                                                                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                                r1.currentTask = youtubeVideoTask;
                                                                r1.isStream = true;
                                                            } else if (group != null) {
                                                                youtubeVideoTask = new AparatVideoTask(group);
                                                                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                                r1.currentTask = youtubeVideoTask;
                                                            } else if (group2 != null) {
                                                                twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                                                                twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                                r1.currentTask = twitchClipVideoTask;
                                                            } else if (str6 != null) {
                                                                twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                                                                twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                                r1.currentTask = twitchClipVideoTask;
                                                                r1.isStream = true;
                                                            }
                                                            r1.controlsView.show(false, false);
                                                            showProgress(true, false);
                                                        }
                                                        if (str6 == null) {
                                                            r1.controlsView.setVisibility(0);
                                                            return true;
                                                        }
                                                        r1.controlsView.setVisibility(8);
                                                        return false;
                                                    }
                                                    group3 = null;
                                                    str6 = group2;
                                                    group2 = group;
                                                    group = str5;
                                                    str5 = str4;
                                                    str4 = group3;
                                                    r1.initied = false;
                                                    r1.isCompleted = false;
                                                    r1.isAutoplay = z;
                                                    r1.playVideoUrl = null;
                                                    r1.playAudioUrl = null;
                                                    destroy();
                                                    r1.firstFrameRendered = false;
                                                    r1.currentAlpha = 1.0f;
                                                    if (r1.currentTask != null) {
                                                        r1.currentTask.cancel(true);
                                                        r1.currentTask = null;
                                                    }
                                                    updateFullscreenButton();
                                                    updateShareButton();
                                                    updateInlineButton();
                                                    updatePlayButton();
                                                    if (photo2 != null) {
                                                        r1.drawImage = false;
                                                    } else {
                                                        closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
                                                        if (closestPhotoSizeWithSize != null) {
                                                            if (photo2 != null) {
                                                            }
                                                            if (photo2 != null) {
                                                            }
                                                            r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                                                            r1.drawImage = true;
                                                        }
                                                    }
                                                    if (r1.progressAnimation != null) {
                                                        r1.progressAnimation.cancel();
                                                        r1.progressAnimation = null;
                                                    }
                                                    r1.isLoading = true;
                                                    r1.controlsView.setProgress(0);
                                                    r1.currentYoutubeId = str3;
                                                    str3 = null;
                                                    if (str4 != null) {
                                                        if (str3 != null) {
                                                            youtubeVideoTask = new YoutubeVideoTask(str3);
                                                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                            r1.currentTask = youtubeVideoTask;
                                                        } else if (str5 != null) {
                                                            youtubeVideoTask = new VimeoVideoTask(str5);
                                                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                            r1.currentTask = youtubeVideoTask;
                                                        } else if (group3 != null) {
                                                            youtubeVideoTask = new CoubVideoTask(group3);
                                                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                            r1.currentTask = youtubeVideoTask;
                                                            r1.isStream = true;
                                                        } else if (group != null) {
                                                            youtubeVideoTask = new AparatVideoTask(group);
                                                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                            r1.currentTask = youtubeVideoTask;
                                                        } else if (group2 != null) {
                                                            twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                                                            twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                            r1.currentTask = twitchClipVideoTask;
                                                        } else if (str6 != null) {
                                                            twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                                                            twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                            r1.currentTask = twitchClipVideoTask;
                                                            r1.isStream = true;
                                                        }
                                                        r1.controlsView.show(false, false);
                                                        showProgress(true, false);
                                                    } else {
                                                        r1.initied = true;
                                                        r1.playVideoUrl = str4;
                                                        r1.playVideoType = "other";
                                                        if (r1.isAutoplay) {
                                                            preparePlayer();
                                                        }
                                                        showProgress(false, false);
                                                        r1.controlsView.show(true, true);
                                                    }
                                                    if (str6 == null) {
                                                        r1.controlsView.setVisibility(8);
                                                        return false;
                                                    }
                                                    r1.controlsView.setVisibility(0);
                                                    return true;
                                                }
                                            } catch (Throwable e2222) {
                                                FileLog.m3e(e2222);
                                            }
                                        }
                                        group = null;
                                        if (group == null) {
                                            matcher4 = twitchStreamIdRegex.matcher(str7);
                                            if (matcher4.find()) {
                                            }
                                            if (group2 != null) {
                                                if (group2 == null) {
                                                    matcher5 = coubIdRegex.matcher(str7);
                                                    if (matcher5.find()) {
                                                    }
                                                    if (group3 == null) {
                                                        group3 = null;
                                                    }
                                                    str6 = group2;
                                                    group2 = group;
                                                    group = str5;
                                                    str5 = str4;
                                                    str4 = null;
                                                    r1.initied = false;
                                                    r1.isCompleted = false;
                                                    r1.isAutoplay = z;
                                                    r1.playVideoUrl = null;
                                                    r1.playAudioUrl = null;
                                                    destroy();
                                                    r1.firstFrameRendered = false;
                                                    r1.currentAlpha = 1.0f;
                                                    if (r1.currentTask != null) {
                                                        r1.currentTask.cancel(true);
                                                        r1.currentTask = null;
                                                    }
                                                    updateFullscreenButton();
                                                    updateShareButton();
                                                    updateInlineButton();
                                                    updatePlayButton();
                                                    if (photo2 != null) {
                                                        closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
                                                        if (closestPhotoSizeWithSize != null) {
                                                            if (photo2 != null) {
                                                            }
                                                            if (photo2 != null) {
                                                            }
                                                            r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                                                            r1.drawImage = true;
                                                        }
                                                    } else {
                                                        r1.drawImage = false;
                                                    }
                                                    if (r1.progressAnimation != null) {
                                                        r1.progressAnimation.cancel();
                                                        r1.progressAnimation = null;
                                                    }
                                                    r1.isLoading = true;
                                                    r1.controlsView.setProgress(0);
                                                    r1.currentYoutubeId = str3;
                                                    str3 = null;
                                                    if (str4 != null) {
                                                        r1.initied = true;
                                                        r1.playVideoUrl = str4;
                                                        r1.playVideoType = "other";
                                                        if (r1.isAutoplay) {
                                                            preparePlayer();
                                                        }
                                                        showProgress(false, false);
                                                        r1.controlsView.show(true, true);
                                                    } else {
                                                        if (str3 != null) {
                                                            youtubeVideoTask = new YoutubeVideoTask(str3);
                                                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                            r1.currentTask = youtubeVideoTask;
                                                        } else if (str5 != null) {
                                                            youtubeVideoTask = new VimeoVideoTask(str5);
                                                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                            r1.currentTask = youtubeVideoTask;
                                                        } else if (group3 != null) {
                                                            youtubeVideoTask = new CoubVideoTask(group3);
                                                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                            r1.currentTask = youtubeVideoTask;
                                                            r1.isStream = true;
                                                        } else if (group != null) {
                                                            youtubeVideoTask = new AparatVideoTask(group);
                                                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                            r1.currentTask = youtubeVideoTask;
                                                        } else if (group2 != null) {
                                                            twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                                                            twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                            r1.currentTask = twitchClipVideoTask;
                                                        } else if (str6 != null) {
                                                            twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                                                            twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                            r1.currentTask = twitchClipVideoTask;
                                                            r1.isStream = true;
                                                        }
                                                        r1.controlsView.show(false, false);
                                                        showProgress(true, false);
                                                    }
                                                    if (str6 == null) {
                                                        r1.controlsView.setVisibility(0);
                                                        return true;
                                                    }
                                                    r1.controlsView.setVisibility(8);
                                                    return false;
                                                }
                                                group3 = null;
                                                str6 = group2;
                                                group2 = group;
                                                group = str5;
                                                str5 = str4;
                                                str4 = group3;
                                                r1.initied = false;
                                                r1.isCompleted = false;
                                                r1.isAutoplay = z;
                                                r1.playVideoUrl = null;
                                                r1.playAudioUrl = null;
                                                destroy();
                                                r1.firstFrameRendered = false;
                                                r1.currentAlpha = 1.0f;
                                                if (r1.currentTask != null) {
                                                    r1.currentTask.cancel(true);
                                                    r1.currentTask = null;
                                                }
                                                updateFullscreenButton();
                                                updateShareButton();
                                                updateInlineButton();
                                                updatePlayButton();
                                                if (photo2 != null) {
                                                    r1.drawImage = false;
                                                } else {
                                                    closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
                                                    if (closestPhotoSizeWithSize != null) {
                                                        if (photo2 != null) {
                                                        }
                                                        if (photo2 != null) {
                                                        }
                                                        r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                                                        r1.drawImage = true;
                                                    }
                                                }
                                                if (r1.progressAnimation != null) {
                                                    r1.progressAnimation.cancel();
                                                    r1.progressAnimation = null;
                                                }
                                                r1.isLoading = true;
                                                r1.controlsView.setProgress(0);
                                                r1.currentYoutubeId = str3;
                                                str3 = null;
                                                if (str4 != null) {
                                                    if (str3 != null) {
                                                        youtubeVideoTask = new YoutubeVideoTask(str3);
                                                        youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                        r1.currentTask = youtubeVideoTask;
                                                    } else if (str5 != null) {
                                                        youtubeVideoTask = new VimeoVideoTask(str5);
                                                        youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                        r1.currentTask = youtubeVideoTask;
                                                    } else if (group3 != null) {
                                                        youtubeVideoTask = new CoubVideoTask(group3);
                                                        youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                        r1.currentTask = youtubeVideoTask;
                                                        r1.isStream = true;
                                                    } else if (group != null) {
                                                        youtubeVideoTask = new AparatVideoTask(group);
                                                        youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                        r1.currentTask = youtubeVideoTask;
                                                    } else if (group2 != null) {
                                                        twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                                                        twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                        r1.currentTask = twitchClipVideoTask;
                                                    } else if (str6 != null) {
                                                        twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                                                        twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                        r1.currentTask = twitchClipVideoTask;
                                                        r1.isStream = true;
                                                    }
                                                    r1.controlsView.show(false, false);
                                                    showProgress(true, false);
                                                } else {
                                                    r1.initied = true;
                                                    r1.playVideoUrl = str4;
                                                    r1.playVideoType = "other";
                                                    if (r1.isAutoplay) {
                                                        preparePlayer();
                                                    }
                                                    showProgress(false, false);
                                                    r1.controlsView.show(true, true);
                                                }
                                                if (str6 == null) {
                                                    r1.controlsView.setVisibility(8);
                                                    return false;
                                                }
                                                r1.controlsView.setVisibility(0);
                                                return true;
                                            }
                                        }
                                        group2 = null;
                                        if (group2 == null) {
                                            matcher5 = coubIdRegex.matcher(str7);
                                            if (matcher5.find()) {
                                            }
                                            if (group3 == null) {
                                                group3 = null;
                                            }
                                            str6 = group2;
                                            group2 = group;
                                            group = str5;
                                            str5 = str4;
                                            str4 = null;
                                            r1.initied = false;
                                            r1.isCompleted = false;
                                            r1.isAutoplay = z;
                                            r1.playVideoUrl = null;
                                            r1.playAudioUrl = null;
                                            destroy();
                                            r1.firstFrameRendered = false;
                                            r1.currentAlpha = 1.0f;
                                            if (r1.currentTask != null) {
                                                r1.currentTask.cancel(true);
                                                r1.currentTask = null;
                                            }
                                            updateFullscreenButton();
                                            updateShareButton();
                                            updateInlineButton();
                                            updatePlayButton();
                                            if (photo2 != null) {
                                                closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
                                                if (closestPhotoSizeWithSize != null) {
                                                    if (photo2 != null) {
                                                    }
                                                    if (photo2 != null) {
                                                    }
                                                    r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                                                    r1.drawImage = true;
                                                }
                                            } else {
                                                r1.drawImage = false;
                                            }
                                            if (r1.progressAnimation != null) {
                                                r1.progressAnimation.cancel();
                                                r1.progressAnimation = null;
                                            }
                                            r1.isLoading = true;
                                            r1.controlsView.setProgress(0);
                                            r1.currentYoutubeId = str3;
                                            str3 = null;
                                            if (str4 != null) {
                                                r1.initied = true;
                                                r1.playVideoUrl = str4;
                                                r1.playVideoType = "other";
                                                if (r1.isAutoplay) {
                                                    preparePlayer();
                                                }
                                                showProgress(false, false);
                                                r1.controlsView.show(true, true);
                                            } else {
                                                if (str3 != null) {
                                                    youtubeVideoTask = new YoutubeVideoTask(str3);
                                                    youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                    r1.currentTask = youtubeVideoTask;
                                                } else if (str5 != null) {
                                                    youtubeVideoTask = new VimeoVideoTask(str5);
                                                    youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                    r1.currentTask = youtubeVideoTask;
                                                } else if (group3 != null) {
                                                    youtubeVideoTask = new CoubVideoTask(group3);
                                                    youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                    r1.currentTask = youtubeVideoTask;
                                                    r1.isStream = true;
                                                } else if (group != null) {
                                                    youtubeVideoTask = new AparatVideoTask(group);
                                                    youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                    r1.currentTask = youtubeVideoTask;
                                                } else if (group2 != null) {
                                                    twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                                                    twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                    r1.currentTask = twitchClipVideoTask;
                                                } else if (str6 != null) {
                                                    twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                                                    twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                    r1.currentTask = twitchClipVideoTask;
                                                    r1.isStream = true;
                                                }
                                                r1.controlsView.show(false, false);
                                                showProgress(true, false);
                                            }
                                            if (str6 == null) {
                                                r1.controlsView.setVisibility(0);
                                                return true;
                                            }
                                            r1.controlsView.setVisibility(8);
                                            return false;
                                        }
                                        group3 = null;
                                        str6 = group2;
                                        group2 = group;
                                        group = str5;
                                        str5 = str4;
                                        str4 = group3;
                                        r1.initied = false;
                                        r1.isCompleted = false;
                                        r1.isAutoplay = z;
                                        r1.playVideoUrl = null;
                                        r1.playAudioUrl = null;
                                        destroy();
                                        r1.firstFrameRendered = false;
                                        r1.currentAlpha = 1.0f;
                                        if (r1.currentTask != null) {
                                            r1.currentTask.cancel(true);
                                            r1.currentTask = null;
                                        }
                                        updateFullscreenButton();
                                        updateShareButton();
                                        updateInlineButton();
                                        updatePlayButton();
                                        if (photo2 != null) {
                                            r1.drawImage = false;
                                        } else {
                                            closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
                                            if (closestPhotoSizeWithSize != null) {
                                                if (photo2 != null) {
                                                }
                                                if (photo2 != null) {
                                                }
                                                r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                                                r1.drawImage = true;
                                            }
                                        }
                                        if (r1.progressAnimation != null) {
                                            r1.progressAnimation.cancel();
                                            r1.progressAnimation = null;
                                        }
                                        r1.isLoading = true;
                                        r1.controlsView.setProgress(0);
                                        r1.currentYoutubeId = str3;
                                        str3 = null;
                                        if (str4 != null) {
                                            if (str3 != null) {
                                                youtubeVideoTask = new YoutubeVideoTask(str3);
                                                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                r1.currentTask = youtubeVideoTask;
                                            } else if (str5 != null) {
                                                youtubeVideoTask = new VimeoVideoTask(str5);
                                                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                r1.currentTask = youtubeVideoTask;
                                            } else if (group3 != null) {
                                                youtubeVideoTask = new CoubVideoTask(group3);
                                                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                r1.currentTask = youtubeVideoTask;
                                                r1.isStream = true;
                                            } else if (group != null) {
                                                youtubeVideoTask = new AparatVideoTask(group);
                                                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                r1.currentTask = youtubeVideoTask;
                                            } else if (group2 != null) {
                                                twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                                                twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                r1.currentTask = twitchClipVideoTask;
                                            } else if (str6 != null) {
                                                twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                                                twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                r1.currentTask = twitchClipVideoTask;
                                                r1.isStream = true;
                                            }
                                            r1.controlsView.show(false, false);
                                            showProgress(true, false);
                                        } else {
                                            r1.initied = true;
                                            r1.playVideoUrl = str4;
                                            r1.playVideoType = "other";
                                            if (r1.isAutoplay) {
                                                preparePlayer();
                                            }
                                            showProgress(false, false);
                                            r1.controlsView.show(true, true);
                                        }
                                        if (str6 == null) {
                                            r1.controlsView.setVisibility(8);
                                            return false;
                                        }
                                        r1.controlsView.setVisibility(0);
                                        return true;
                                    }
                                } catch (Throwable e22222) {
                                    FileLog.m3e(e22222);
                                }
                            }
                            str5 = null;
                            if (str5 == null) {
                                matcher3 = twitchClipIdRegex.matcher(str7);
                                if (matcher3.find()) {
                                }
                                if (group != null) {
                                    if (group == null) {
                                        matcher4 = twitchStreamIdRegex.matcher(str7);
                                        if (matcher4.find()) {
                                        }
                                        if (group2 != null) {
                                            if (group2 == null) {
                                                matcher5 = coubIdRegex.matcher(str7);
                                                if (matcher5.find()) {
                                                }
                                                if (group3 == null) {
                                                    group3 = null;
                                                }
                                                str6 = group2;
                                                group2 = group;
                                                group = str5;
                                                str5 = str4;
                                                str4 = null;
                                                r1.initied = false;
                                                r1.isCompleted = false;
                                                r1.isAutoplay = z;
                                                r1.playVideoUrl = null;
                                                r1.playAudioUrl = null;
                                                destroy();
                                                r1.firstFrameRendered = false;
                                                r1.currentAlpha = 1.0f;
                                                if (r1.currentTask != null) {
                                                    r1.currentTask.cancel(true);
                                                    r1.currentTask = null;
                                                }
                                                updateFullscreenButton();
                                                updateShareButton();
                                                updateInlineButton();
                                                updatePlayButton();
                                                if (photo2 != null) {
                                                    closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
                                                    if (closestPhotoSizeWithSize != null) {
                                                        if (photo2 != null) {
                                                        }
                                                        if (photo2 != null) {
                                                        }
                                                        r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                                                        r1.drawImage = true;
                                                    }
                                                } else {
                                                    r1.drawImage = false;
                                                }
                                                if (r1.progressAnimation != null) {
                                                    r1.progressAnimation.cancel();
                                                    r1.progressAnimation = null;
                                                }
                                                r1.isLoading = true;
                                                r1.controlsView.setProgress(0);
                                                r1.currentYoutubeId = str3;
                                                str3 = null;
                                                if (str4 != null) {
                                                    r1.initied = true;
                                                    r1.playVideoUrl = str4;
                                                    r1.playVideoType = "other";
                                                    if (r1.isAutoplay) {
                                                        preparePlayer();
                                                    }
                                                    showProgress(false, false);
                                                    r1.controlsView.show(true, true);
                                                } else {
                                                    if (str3 != null) {
                                                        youtubeVideoTask = new YoutubeVideoTask(str3);
                                                        youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                        r1.currentTask = youtubeVideoTask;
                                                    } else if (str5 != null) {
                                                        youtubeVideoTask = new VimeoVideoTask(str5);
                                                        youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                        r1.currentTask = youtubeVideoTask;
                                                    } else if (group3 != null) {
                                                        youtubeVideoTask = new CoubVideoTask(group3);
                                                        youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                        r1.currentTask = youtubeVideoTask;
                                                        r1.isStream = true;
                                                    } else if (group != null) {
                                                        youtubeVideoTask = new AparatVideoTask(group);
                                                        youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                        r1.currentTask = youtubeVideoTask;
                                                    } else if (group2 != null) {
                                                        twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                                                        twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                        r1.currentTask = twitchClipVideoTask;
                                                    } else if (str6 != null) {
                                                        twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                                                        twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                        r1.currentTask = twitchClipVideoTask;
                                                        r1.isStream = true;
                                                    }
                                                    r1.controlsView.show(false, false);
                                                    showProgress(true, false);
                                                }
                                                if (str6 == null) {
                                                    r1.controlsView.setVisibility(0);
                                                    return true;
                                                }
                                                r1.controlsView.setVisibility(8);
                                                return false;
                                            }
                                            group3 = null;
                                            str6 = group2;
                                            group2 = group;
                                            group = str5;
                                            str5 = str4;
                                            str4 = group3;
                                            r1.initied = false;
                                            r1.isCompleted = false;
                                            r1.isAutoplay = z;
                                            r1.playVideoUrl = null;
                                            r1.playAudioUrl = null;
                                            destroy();
                                            r1.firstFrameRendered = false;
                                            r1.currentAlpha = 1.0f;
                                            if (r1.currentTask != null) {
                                                r1.currentTask.cancel(true);
                                                r1.currentTask = null;
                                            }
                                            updateFullscreenButton();
                                            updateShareButton();
                                            updateInlineButton();
                                            updatePlayButton();
                                            if (photo2 != null) {
                                                r1.drawImage = false;
                                            } else {
                                                closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
                                                if (closestPhotoSizeWithSize != null) {
                                                    if (photo2 != null) {
                                                    }
                                                    if (photo2 != null) {
                                                    }
                                                    r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                                                    r1.drawImage = true;
                                                }
                                            }
                                            if (r1.progressAnimation != null) {
                                                r1.progressAnimation.cancel();
                                                r1.progressAnimation = null;
                                            }
                                            r1.isLoading = true;
                                            r1.controlsView.setProgress(0);
                                            r1.currentYoutubeId = str3;
                                            str3 = null;
                                            if (str4 != null) {
                                                if (str3 != null) {
                                                    youtubeVideoTask = new YoutubeVideoTask(str3);
                                                    youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                    r1.currentTask = youtubeVideoTask;
                                                } else if (str5 != null) {
                                                    youtubeVideoTask = new VimeoVideoTask(str5);
                                                    youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                    r1.currentTask = youtubeVideoTask;
                                                } else if (group3 != null) {
                                                    youtubeVideoTask = new CoubVideoTask(group3);
                                                    youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                    r1.currentTask = youtubeVideoTask;
                                                    r1.isStream = true;
                                                } else if (group != null) {
                                                    youtubeVideoTask = new AparatVideoTask(group);
                                                    youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                    r1.currentTask = youtubeVideoTask;
                                                } else if (group2 != null) {
                                                    twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                                                    twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                    r1.currentTask = twitchClipVideoTask;
                                                } else if (str6 != null) {
                                                    twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                                                    twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                    r1.currentTask = twitchClipVideoTask;
                                                    r1.isStream = true;
                                                }
                                                r1.controlsView.show(false, false);
                                                showProgress(true, false);
                                            } else {
                                                r1.initied = true;
                                                r1.playVideoUrl = str4;
                                                r1.playVideoType = "other";
                                                if (r1.isAutoplay) {
                                                    preparePlayer();
                                                }
                                                showProgress(false, false);
                                                r1.controlsView.show(true, true);
                                            }
                                            if (str6 == null) {
                                                r1.controlsView.setVisibility(8);
                                                return false;
                                            }
                                            r1.controlsView.setVisibility(0);
                                            return true;
                                        }
                                    }
                                    group2 = null;
                                    if (group2 == null) {
                                        matcher5 = coubIdRegex.matcher(str7);
                                        if (matcher5.find()) {
                                        }
                                        if (group3 == null) {
                                            group3 = null;
                                        }
                                        str6 = group2;
                                        group2 = group;
                                        group = str5;
                                        str5 = str4;
                                        str4 = null;
                                        r1.initied = false;
                                        r1.isCompleted = false;
                                        r1.isAutoplay = z;
                                        r1.playVideoUrl = null;
                                        r1.playAudioUrl = null;
                                        destroy();
                                        r1.firstFrameRendered = false;
                                        r1.currentAlpha = 1.0f;
                                        if (r1.currentTask != null) {
                                            r1.currentTask.cancel(true);
                                            r1.currentTask = null;
                                        }
                                        updateFullscreenButton();
                                        updateShareButton();
                                        updateInlineButton();
                                        updatePlayButton();
                                        if (photo2 != null) {
                                            closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
                                            if (closestPhotoSizeWithSize != null) {
                                                if (photo2 != null) {
                                                }
                                                if (photo2 != null) {
                                                }
                                                r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                                                r1.drawImage = true;
                                            }
                                        } else {
                                            r1.drawImage = false;
                                        }
                                        if (r1.progressAnimation != null) {
                                            r1.progressAnimation.cancel();
                                            r1.progressAnimation = null;
                                        }
                                        r1.isLoading = true;
                                        r1.controlsView.setProgress(0);
                                        r1.currentYoutubeId = str3;
                                        str3 = null;
                                        if (str4 != null) {
                                            r1.initied = true;
                                            r1.playVideoUrl = str4;
                                            r1.playVideoType = "other";
                                            if (r1.isAutoplay) {
                                                preparePlayer();
                                            }
                                            showProgress(false, false);
                                            r1.controlsView.show(true, true);
                                        } else {
                                            if (str3 != null) {
                                                youtubeVideoTask = new YoutubeVideoTask(str3);
                                                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                r1.currentTask = youtubeVideoTask;
                                            } else if (str5 != null) {
                                                youtubeVideoTask = new VimeoVideoTask(str5);
                                                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                r1.currentTask = youtubeVideoTask;
                                            } else if (group3 != null) {
                                                youtubeVideoTask = new CoubVideoTask(group3);
                                                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                r1.currentTask = youtubeVideoTask;
                                                r1.isStream = true;
                                            } else if (group != null) {
                                                youtubeVideoTask = new AparatVideoTask(group);
                                                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                r1.currentTask = youtubeVideoTask;
                                            } else if (group2 != null) {
                                                twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                                                twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                r1.currentTask = twitchClipVideoTask;
                                            } else if (str6 != null) {
                                                twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                                                twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                r1.currentTask = twitchClipVideoTask;
                                                r1.isStream = true;
                                            }
                                            r1.controlsView.show(false, false);
                                            showProgress(true, false);
                                        }
                                        if (str6 == null) {
                                            r1.controlsView.setVisibility(0);
                                            return true;
                                        }
                                        r1.controlsView.setVisibility(8);
                                        return false;
                                    }
                                    group3 = null;
                                    str6 = group2;
                                    group2 = group;
                                    group = str5;
                                    str5 = str4;
                                    str4 = group3;
                                    r1.initied = false;
                                    r1.isCompleted = false;
                                    r1.isAutoplay = z;
                                    r1.playVideoUrl = null;
                                    r1.playAudioUrl = null;
                                    destroy();
                                    r1.firstFrameRendered = false;
                                    r1.currentAlpha = 1.0f;
                                    if (r1.currentTask != null) {
                                        r1.currentTask.cancel(true);
                                        r1.currentTask = null;
                                    }
                                    updateFullscreenButton();
                                    updateShareButton();
                                    updateInlineButton();
                                    updatePlayButton();
                                    if (photo2 != null) {
                                        r1.drawImage = false;
                                    } else {
                                        closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
                                        if (closestPhotoSizeWithSize != null) {
                                            if (photo2 != null) {
                                            }
                                            if (photo2 != null) {
                                            }
                                            r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                                            r1.drawImage = true;
                                        }
                                    }
                                    if (r1.progressAnimation != null) {
                                        r1.progressAnimation.cancel();
                                        r1.progressAnimation = null;
                                    }
                                    r1.isLoading = true;
                                    r1.controlsView.setProgress(0);
                                    r1.currentYoutubeId = str3;
                                    str3 = null;
                                    if (str4 != null) {
                                        if (str3 != null) {
                                            youtubeVideoTask = new YoutubeVideoTask(str3);
                                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                            r1.currentTask = youtubeVideoTask;
                                        } else if (str5 != null) {
                                            youtubeVideoTask = new VimeoVideoTask(str5);
                                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                            r1.currentTask = youtubeVideoTask;
                                        } else if (group3 != null) {
                                            youtubeVideoTask = new CoubVideoTask(group3);
                                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                            r1.currentTask = youtubeVideoTask;
                                            r1.isStream = true;
                                        } else if (group != null) {
                                            youtubeVideoTask = new AparatVideoTask(group);
                                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                            r1.currentTask = youtubeVideoTask;
                                        } else if (group2 != null) {
                                            twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                                            twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                            r1.currentTask = twitchClipVideoTask;
                                        } else if (str6 != null) {
                                            twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                                            twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                            r1.currentTask = twitchClipVideoTask;
                                            r1.isStream = true;
                                        }
                                        r1.controlsView.show(false, false);
                                        showProgress(true, false);
                                    } else {
                                        r1.initied = true;
                                        r1.playVideoUrl = str4;
                                        r1.playVideoType = "other";
                                        if (r1.isAutoplay) {
                                            preparePlayer();
                                        }
                                        showProgress(false, false);
                                        r1.controlsView.show(true, true);
                                    }
                                    if (str6 == null) {
                                        r1.controlsView.setVisibility(8);
                                        return false;
                                    }
                                    r1.controlsView.setVisibility(0);
                                    return true;
                                }
                            }
                            group = null;
                            if (group == null) {
                                matcher4 = twitchStreamIdRegex.matcher(str7);
                                if (matcher4.find()) {
                                }
                                if (group2 != null) {
                                    if (group2 == null) {
                                        matcher5 = coubIdRegex.matcher(str7);
                                        if (matcher5.find()) {
                                        }
                                        if (group3 == null) {
                                            group3 = null;
                                        }
                                        str6 = group2;
                                        group2 = group;
                                        group = str5;
                                        str5 = str4;
                                        str4 = null;
                                        r1.initied = false;
                                        r1.isCompleted = false;
                                        r1.isAutoplay = z;
                                        r1.playVideoUrl = null;
                                        r1.playAudioUrl = null;
                                        destroy();
                                        r1.firstFrameRendered = false;
                                        r1.currentAlpha = 1.0f;
                                        if (r1.currentTask != null) {
                                            r1.currentTask.cancel(true);
                                            r1.currentTask = null;
                                        }
                                        updateFullscreenButton();
                                        updateShareButton();
                                        updateInlineButton();
                                        updatePlayButton();
                                        if (photo2 != null) {
                                            closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
                                            if (closestPhotoSizeWithSize != null) {
                                                if (photo2 != null) {
                                                }
                                                if (photo2 != null) {
                                                }
                                                r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                                                r1.drawImage = true;
                                            }
                                        } else {
                                            r1.drawImage = false;
                                        }
                                        if (r1.progressAnimation != null) {
                                            r1.progressAnimation.cancel();
                                            r1.progressAnimation = null;
                                        }
                                        r1.isLoading = true;
                                        r1.controlsView.setProgress(0);
                                        r1.currentYoutubeId = str3;
                                        str3 = null;
                                        if (str4 != null) {
                                            r1.initied = true;
                                            r1.playVideoUrl = str4;
                                            r1.playVideoType = "other";
                                            if (r1.isAutoplay) {
                                                preparePlayer();
                                            }
                                            showProgress(false, false);
                                            r1.controlsView.show(true, true);
                                        } else {
                                            if (str3 != null) {
                                                youtubeVideoTask = new YoutubeVideoTask(str3);
                                                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                r1.currentTask = youtubeVideoTask;
                                            } else if (str5 != null) {
                                                youtubeVideoTask = new VimeoVideoTask(str5);
                                                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                r1.currentTask = youtubeVideoTask;
                                            } else if (group3 != null) {
                                                youtubeVideoTask = new CoubVideoTask(group3);
                                                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                r1.currentTask = youtubeVideoTask;
                                                r1.isStream = true;
                                            } else if (group != null) {
                                                youtubeVideoTask = new AparatVideoTask(group);
                                                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                r1.currentTask = youtubeVideoTask;
                                            } else if (group2 != null) {
                                                twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                                                twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                r1.currentTask = twitchClipVideoTask;
                                            } else if (str6 != null) {
                                                twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                                                twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                r1.currentTask = twitchClipVideoTask;
                                                r1.isStream = true;
                                            }
                                            r1.controlsView.show(false, false);
                                            showProgress(true, false);
                                        }
                                        if (str6 == null) {
                                            r1.controlsView.setVisibility(0);
                                            return true;
                                        }
                                        r1.controlsView.setVisibility(8);
                                        return false;
                                    }
                                    group3 = null;
                                    str6 = group2;
                                    group2 = group;
                                    group = str5;
                                    str5 = str4;
                                    str4 = group3;
                                    r1.initied = false;
                                    r1.isCompleted = false;
                                    r1.isAutoplay = z;
                                    r1.playVideoUrl = null;
                                    r1.playAudioUrl = null;
                                    destroy();
                                    r1.firstFrameRendered = false;
                                    r1.currentAlpha = 1.0f;
                                    if (r1.currentTask != null) {
                                        r1.currentTask.cancel(true);
                                        r1.currentTask = null;
                                    }
                                    updateFullscreenButton();
                                    updateShareButton();
                                    updateInlineButton();
                                    updatePlayButton();
                                    if (photo2 != null) {
                                        r1.drawImage = false;
                                    } else {
                                        closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
                                        if (closestPhotoSizeWithSize != null) {
                                            if (photo2 != null) {
                                            }
                                            if (photo2 != null) {
                                            }
                                            r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                                            r1.drawImage = true;
                                        }
                                    }
                                    if (r1.progressAnimation != null) {
                                        r1.progressAnimation.cancel();
                                        r1.progressAnimation = null;
                                    }
                                    r1.isLoading = true;
                                    r1.controlsView.setProgress(0);
                                    r1.currentYoutubeId = str3;
                                    str3 = null;
                                    if (str4 != null) {
                                        if (str3 != null) {
                                            youtubeVideoTask = new YoutubeVideoTask(str3);
                                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                            r1.currentTask = youtubeVideoTask;
                                        } else if (str5 != null) {
                                            youtubeVideoTask = new VimeoVideoTask(str5);
                                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                            r1.currentTask = youtubeVideoTask;
                                        } else if (group3 != null) {
                                            youtubeVideoTask = new CoubVideoTask(group3);
                                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                            r1.currentTask = youtubeVideoTask;
                                            r1.isStream = true;
                                        } else if (group != null) {
                                            youtubeVideoTask = new AparatVideoTask(group);
                                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                            r1.currentTask = youtubeVideoTask;
                                        } else if (group2 != null) {
                                            twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                                            twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                            r1.currentTask = twitchClipVideoTask;
                                        } else if (str6 != null) {
                                            twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                                            twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                            r1.currentTask = twitchClipVideoTask;
                                            r1.isStream = true;
                                        }
                                        r1.controlsView.show(false, false);
                                        showProgress(true, false);
                                    } else {
                                        r1.initied = true;
                                        r1.playVideoUrl = str4;
                                        r1.playVideoType = "other";
                                        if (r1.isAutoplay) {
                                            preparePlayer();
                                        }
                                        showProgress(false, false);
                                        r1.controlsView.show(true, true);
                                    }
                                    if (str6 == null) {
                                        r1.controlsView.setVisibility(8);
                                        return false;
                                    }
                                    r1.controlsView.setVisibility(0);
                                    return true;
                                }
                            }
                            group2 = null;
                            if (group2 == null) {
                                matcher5 = coubIdRegex.matcher(str7);
                                if (matcher5.find()) {
                                }
                                if (group3 == null) {
                                    group3 = null;
                                }
                                str6 = group2;
                                group2 = group;
                                group = str5;
                                str5 = str4;
                                str4 = null;
                                r1.initied = false;
                                r1.isCompleted = false;
                                r1.isAutoplay = z;
                                r1.playVideoUrl = null;
                                r1.playAudioUrl = null;
                                destroy();
                                r1.firstFrameRendered = false;
                                r1.currentAlpha = 1.0f;
                                if (r1.currentTask != null) {
                                    r1.currentTask.cancel(true);
                                    r1.currentTask = null;
                                }
                                updateFullscreenButton();
                                updateShareButton();
                                updateInlineButton();
                                updatePlayButton();
                                if (photo2 != null) {
                                    closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
                                    if (closestPhotoSizeWithSize != null) {
                                        if (photo2 != null) {
                                        }
                                        if (photo2 != null) {
                                        }
                                        r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                                        r1.drawImage = true;
                                    }
                                } else {
                                    r1.drawImage = false;
                                }
                                if (r1.progressAnimation != null) {
                                    r1.progressAnimation.cancel();
                                    r1.progressAnimation = null;
                                }
                                r1.isLoading = true;
                                r1.controlsView.setProgress(0);
                                r1.currentYoutubeId = str3;
                                str3 = null;
                                if (str4 != null) {
                                    r1.initied = true;
                                    r1.playVideoUrl = str4;
                                    r1.playVideoType = "other";
                                    if (r1.isAutoplay) {
                                        preparePlayer();
                                    }
                                    showProgress(false, false);
                                    r1.controlsView.show(true, true);
                                } else {
                                    if (str3 != null) {
                                        youtubeVideoTask = new YoutubeVideoTask(str3);
                                        youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                        r1.currentTask = youtubeVideoTask;
                                    } else if (str5 != null) {
                                        youtubeVideoTask = new VimeoVideoTask(str5);
                                        youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                        r1.currentTask = youtubeVideoTask;
                                    } else if (group3 != null) {
                                        youtubeVideoTask = new CoubVideoTask(group3);
                                        youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                        r1.currentTask = youtubeVideoTask;
                                        r1.isStream = true;
                                    } else if (group != null) {
                                        youtubeVideoTask = new AparatVideoTask(group);
                                        youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                        r1.currentTask = youtubeVideoTask;
                                    } else if (group2 != null) {
                                        twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                                        twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                        r1.currentTask = twitchClipVideoTask;
                                    } else if (str6 != null) {
                                        twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                                        twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                        r1.currentTask = twitchClipVideoTask;
                                        r1.isStream = true;
                                    }
                                    r1.controlsView.show(false, false);
                                    showProgress(true, false);
                                }
                                if (str6 == null) {
                                    r1.controlsView.setVisibility(0);
                                    return true;
                                }
                                r1.controlsView.setVisibility(8);
                                return false;
                            }
                            group3 = null;
                            str6 = group2;
                            group2 = group;
                            group = str5;
                            str5 = str4;
                            str4 = group3;
                            r1.initied = false;
                            r1.isCompleted = false;
                            r1.isAutoplay = z;
                            r1.playVideoUrl = null;
                            r1.playAudioUrl = null;
                            destroy();
                            r1.firstFrameRendered = false;
                            r1.currentAlpha = 1.0f;
                            if (r1.currentTask != null) {
                                r1.currentTask.cancel(true);
                                r1.currentTask = null;
                            }
                            updateFullscreenButton();
                            updateShareButton();
                            updateInlineButton();
                            updatePlayButton();
                            if (photo2 != null) {
                                r1.drawImage = false;
                            } else {
                                closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
                                if (closestPhotoSizeWithSize != null) {
                                    if (photo2 != null) {
                                    }
                                    if (photo2 != null) {
                                    }
                                    r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                                    r1.drawImage = true;
                                }
                            }
                            if (r1.progressAnimation != null) {
                                r1.progressAnimation.cancel();
                                r1.progressAnimation = null;
                            }
                            r1.isLoading = true;
                            r1.controlsView.setProgress(0);
                            r1.currentYoutubeId = str3;
                            str3 = null;
                            if (str4 != null) {
                                if (str3 != null) {
                                    youtubeVideoTask = new YoutubeVideoTask(str3);
                                    youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = youtubeVideoTask;
                                } else if (str5 != null) {
                                    youtubeVideoTask = new VimeoVideoTask(str5);
                                    youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = youtubeVideoTask;
                                } else if (group3 != null) {
                                    youtubeVideoTask = new CoubVideoTask(group3);
                                    youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = youtubeVideoTask;
                                    r1.isStream = true;
                                } else if (group != null) {
                                    youtubeVideoTask = new AparatVideoTask(group);
                                    youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = youtubeVideoTask;
                                } else if (group2 != null) {
                                    twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                                    twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = twitchClipVideoTask;
                                } else if (str6 != null) {
                                    twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                                    twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = twitchClipVideoTask;
                                    r1.isStream = true;
                                }
                                r1.controlsView.show(false, false);
                                showProgress(true, false);
                            } else {
                                r1.initied = true;
                                r1.playVideoUrl = str4;
                                r1.playVideoType = "other";
                                if (r1.isAutoplay) {
                                    preparePlayer();
                                }
                                showProgress(false, false);
                                r1.controlsView.show(true, true);
                            }
                            if (str6 == null) {
                                r1.controlsView.setVisibility(8);
                                return false;
                            }
                            r1.controlsView.setVisibility(0);
                            return true;
                        }
                    } catch (Throwable e222222) {
                        FileLog.m3e(e222222);
                    }
                }
                str4 = null;
                if (str4 == null) {
                    matcher2 = aparatIdRegex.matcher(str7);
                    if (matcher2.find()) {
                    }
                    if (str5 != null) {
                        if (str5 == null) {
                            matcher3 = twitchClipIdRegex.matcher(str7);
                            if (matcher3.find()) {
                            }
                            if (group != null) {
                                if (group == null) {
                                    matcher4 = twitchStreamIdRegex.matcher(str7);
                                    if (matcher4.find()) {
                                    }
                                    if (group2 != null) {
                                        if (group2 == null) {
                                            matcher5 = coubIdRegex.matcher(str7);
                                            if (matcher5.find()) {
                                            }
                                            if (group3 == null) {
                                                group3 = null;
                                            }
                                            str6 = group2;
                                            group2 = group;
                                            group = str5;
                                            str5 = str4;
                                            str4 = null;
                                            r1.initied = false;
                                            r1.isCompleted = false;
                                            r1.isAutoplay = z;
                                            r1.playVideoUrl = null;
                                            r1.playAudioUrl = null;
                                            destroy();
                                            r1.firstFrameRendered = false;
                                            r1.currentAlpha = 1.0f;
                                            if (r1.currentTask != null) {
                                                r1.currentTask.cancel(true);
                                                r1.currentTask = null;
                                            }
                                            updateFullscreenButton();
                                            updateShareButton();
                                            updateInlineButton();
                                            updatePlayButton();
                                            if (photo2 != null) {
                                                closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
                                                if (closestPhotoSizeWithSize != null) {
                                                    if (photo2 != null) {
                                                    }
                                                    if (photo2 != null) {
                                                    }
                                                    r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                                                    r1.drawImage = true;
                                                }
                                            } else {
                                                r1.drawImage = false;
                                            }
                                            if (r1.progressAnimation != null) {
                                                r1.progressAnimation.cancel();
                                                r1.progressAnimation = null;
                                            }
                                            r1.isLoading = true;
                                            r1.controlsView.setProgress(0);
                                            r1.currentYoutubeId = str3;
                                            str3 = null;
                                            if (str4 != null) {
                                                r1.initied = true;
                                                r1.playVideoUrl = str4;
                                                r1.playVideoType = "other";
                                                if (r1.isAutoplay) {
                                                    preparePlayer();
                                                }
                                                showProgress(false, false);
                                                r1.controlsView.show(true, true);
                                            } else {
                                                if (str3 != null) {
                                                    youtubeVideoTask = new YoutubeVideoTask(str3);
                                                    youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                    r1.currentTask = youtubeVideoTask;
                                                } else if (str5 != null) {
                                                    youtubeVideoTask = new VimeoVideoTask(str5);
                                                    youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                    r1.currentTask = youtubeVideoTask;
                                                } else if (group3 != null) {
                                                    youtubeVideoTask = new CoubVideoTask(group3);
                                                    youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                    r1.currentTask = youtubeVideoTask;
                                                    r1.isStream = true;
                                                } else if (group != null) {
                                                    youtubeVideoTask = new AparatVideoTask(group);
                                                    youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                    r1.currentTask = youtubeVideoTask;
                                                } else if (group2 != null) {
                                                    twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                                                    twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                    r1.currentTask = twitchClipVideoTask;
                                                } else if (str6 != null) {
                                                    twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                                                    twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                    r1.currentTask = twitchClipVideoTask;
                                                    r1.isStream = true;
                                                }
                                                r1.controlsView.show(false, false);
                                                showProgress(true, false);
                                            }
                                            if (str6 == null) {
                                                r1.controlsView.setVisibility(0);
                                                return true;
                                            }
                                            r1.controlsView.setVisibility(8);
                                            return false;
                                        }
                                        group3 = null;
                                        str6 = group2;
                                        group2 = group;
                                        group = str5;
                                        str5 = str4;
                                        str4 = group3;
                                        r1.initied = false;
                                        r1.isCompleted = false;
                                        r1.isAutoplay = z;
                                        r1.playVideoUrl = null;
                                        r1.playAudioUrl = null;
                                        destroy();
                                        r1.firstFrameRendered = false;
                                        r1.currentAlpha = 1.0f;
                                        if (r1.currentTask != null) {
                                            r1.currentTask.cancel(true);
                                            r1.currentTask = null;
                                        }
                                        updateFullscreenButton();
                                        updateShareButton();
                                        updateInlineButton();
                                        updatePlayButton();
                                        if (photo2 != null) {
                                            r1.drawImage = false;
                                        } else {
                                            closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
                                            if (closestPhotoSizeWithSize != null) {
                                                if (photo2 != null) {
                                                }
                                                if (photo2 != null) {
                                                }
                                                r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                                                r1.drawImage = true;
                                            }
                                        }
                                        if (r1.progressAnimation != null) {
                                            r1.progressAnimation.cancel();
                                            r1.progressAnimation = null;
                                        }
                                        r1.isLoading = true;
                                        r1.controlsView.setProgress(0);
                                        r1.currentYoutubeId = str3;
                                        str3 = null;
                                        if (str4 != null) {
                                            if (str3 != null) {
                                                youtubeVideoTask = new YoutubeVideoTask(str3);
                                                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                r1.currentTask = youtubeVideoTask;
                                            } else if (str5 != null) {
                                                youtubeVideoTask = new VimeoVideoTask(str5);
                                                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                r1.currentTask = youtubeVideoTask;
                                            } else if (group3 != null) {
                                                youtubeVideoTask = new CoubVideoTask(group3);
                                                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                r1.currentTask = youtubeVideoTask;
                                                r1.isStream = true;
                                            } else if (group != null) {
                                                youtubeVideoTask = new AparatVideoTask(group);
                                                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                r1.currentTask = youtubeVideoTask;
                                            } else if (group2 != null) {
                                                twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                                                twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                r1.currentTask = twitchClipVideoTask;
                                            } else if (str6 != null) {
                                                twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                                                twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                r1.currentTask = twitchClipVideoTask;
                                                r1.isStream = true;
                                            }
                                            r1.controlsView.show(false, false);
                                            showProgress(true, false);
                                        } else {
                                            r1.initied = true;
                                            r1.playVideoUrl = str4;
                                            r1.playVideoType = "other";
                                            if (r1.isAutoplay) {
                                                preparePlayer();
                                            }
                                            showProgress(false, false);
                                            r1.controlsView.show(true, true);
                                        }
                                        if (str6 == null) {
                                            r1.controlsView.setVisibility(8);
                                            return false;
                                        }
                                        r1.controlsView.setVisibility(0);
                                        return true;
                                    }
                                }
                                group2 = null;
                                if (group2 == null) {
                                    matcher5 = coubIdRegex.matcher(str7);
                                    if (matcher5.find()) {
                                    }
                                    if (group3 == null) {
                                        group3 = null;
                                    }
                                    str6 = group2;
                                    group2 = group;
                                    group = str5;
                                    str5 = str4;
                                    str4 = null;
                                    r1.initied = false;
                                    r1.isCompleted = false;
                                    r1.isAutoplay = z;
                                    r1.playVideoUrl = null;
                                    r1.playAudioUrl = null;
                                    destroy();
                                    r1.firstFrameRendered = false;
                                    r1.currentAlpha = 1.0f;
                                    if (r1.currentTask != null) {
                                        r1.currentTask.cancel(true);
                                        r1.currentTask = null;
                                    }
                                    updateFullscreenButton();
                                    updateShareButton();
                                    updateInlineButton();
                                    updatePlayButton();
                                    if (photo2 != null) {
                                        closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
                                        if (closestPhotoSizeWithSize != null) {
                                            if (photo2 != null) {
                                            }
                                            if (photo2 != null) {
                                            }
                                            r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                                            r1.drawImage = true;
                                        }
                                    } else {
                                        r1.drawImage = false;
                                    }
                                    if (r1.progressAnimation != null) {
                                        r1.progressAnimation.cancel();
                                        r1.progressAnimation = null;
                                    }
                                    r1.isLoading = true;
                                    r1.controlsView.setProgress(0);
                                    r1.currentYoutubeId = str3;
                                    str3 = null;
                                    if (str4 != null) {
                                        r1.initied = true;
                                        r1.playVideoUrl = str4;
                                        r1.playVideoType = "other";
                                        if (r1.isAutoplay) {
                                            preparePlayer();
                                        }
                                        showProgress(false, false);
                                        r1.controlsView.show(true, true);
                                    } else {
                                        if (str3 != null) {
                                            youtubeVideoTask = new YoutubeVideoTask(str3);
                                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                            r1.currentTask = youtubeVideoTask;
                                        } else if (str5 != null) {
                                            youtubeVideoTask = new VimeoVideoTask(str5);
                                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                            r1.currentTask = youtubeVideoTask;
                                        } else if (group3 != null) {
                                            youtubeVideoTask = new CoubVideoTask(group3);
                                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                            r1.currentTask = youtubeVideoTask;
                                            r1.isStream = true;
                                        } else if (group != null) {
                                            youtubeVideoTask = new AparatVideoTask(group);
                                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                            r1.currentTask = youtubeVideoTask;
                                        } else if (group2 != null) {
                                            twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                                            twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                            r1.currentTask = twitchClipVideoTask;
                                        } else if (str6 != null) {
                                            twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                                            twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                            r1.currentTask = twitchClipVideoTask;
                                            r1.isStream = true;
                                        }
                                        r1.controlsView.show(false, false);
                                        showProgress(true, false);
                                    }
                                    if (str6 == null) {
                                        r1.controlsView.setVisibility(0);
                                        return true;
                                    }
                                    r1.controlsView.setVisibility(8);
                                    return false;
                                }
                                group3 = null;
                                str6 = group2;
                                group2 = group;
                                group = str5;
                                str5 = str4;
                                str4 = group3;
                                r1.initied = false;
                                r1.isCompleted = false;
                                r1.isAutoplay = z;
                                r1.playVideoUrl = null;
                                r1.playAudioUrl = null;
                                destroy();
                                r1.firstFrameRendered = false;
                                r1.currentAlpha = 1.0f;
                                if (r1.currentTask != null) {
                                    r1.currentTask.cancel(true);
                                    r1.currentTask = null;
                                }
                                updateFullscreenButton();
                                updateShareButton();
                                updateInlineButton();
                                updatePlayButton();
                                if (photo2 != null) {
                                    r1.drawImage = false;
                                } else {
                                    closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
                                    if (closestPhotoSizeWithSize != null) {
                                        if (photo2 != null) {
                                        }
                                        if (photo2 != null) {
                                        }
                                        r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                                        r1.drawImage = true;
                                    }
                                }
                                if (r1.progressAnimation != null) {
                                    r1.progressAnimation.cancel();
                                    r1.progressAnimation = null;
                                }
                                r1.isLoading = true;
                                r1.controlsView.setProgress(0);
                                r1.currentYoutubeId = str3;
                                str3 = null;
                                if (str4 != null) {
                                    if (str3 != null) {
                                        youtubeVideoTask = new YoutubeVideoTask(str3);
                                        youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                        r1.currentTask = youtubeVideoTask;
                                    } else if (str5 != null) {
                                        youtubeVideoTask = new VimeoVideoTask(str5);
                                        youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                        r1.currentTask = youtubeVideoTask;
                                    } else if (group3 != null) {
                                        youtubeVideoTask = new CoubVideoTask(group3);
                                        youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                        r1.currentTask = youtubeVideoTask;
                                        r1.isStream = true;
                                    } else if (group != null) {
                                        youtubeVideoTask = new AparatVideoTask(group);
                                        youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                        r1.currentTask = youtubeVideoTask;
                                    } else if (group2 != null) {
                                        twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                                        twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                        r1.currentTask = twitchClipVideoTask;
                                    } else if (str6 != null) {
                                        twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                                        twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                        r1.currentTask = twitchClipVideoTask;
                                        r1.isStream = true;
                                    }
                                    r1.controlsView.show(false, false);
                                    showProgress(true, false);
                                } else {
                                    r1.initied = true;
                                    r1.playVideoUrl = str4;
                                    r1.playVideoType = "other";
                                    if (r1.isAutoplay) {
                                        preparePlayer();
                                    }
                                    showProgress(false, false);
                                    r1.controlsView.show(true, true);
                                }
                                if (str6 == null) {
                                    r1.controlsView.setVisibility(8);
                                    return false;
                                }
                                r1.controlsView.setVisibility(0);
                                return true;
                            }
                        }
                        group = null;
                        if (group == null) {
                            matcher4 = twitchStreamIdRegex.matcher(str7);
                            if (matcher4.find()) {
                            }
                            if (group2 != null) {
                                if (group2 == null) {
                                    matcher5 = coubIdRegex.matcher(str7);
                                    if (matcher5.find()) {
                                    }
                                    if (group3 == null) {
                                        group3 = null;
                                    }
                                    str6 = group2;
                                    group2 = group;
                                    group = str5;
                                    str5 = str4;
                                    str4 = null;
                                    r1.initied = false;
                                    r1.isCompleted = false;
                                    r1.isAutoplay = z;
                                    r1.playVideoUrl = null;
                                    r1.playAudioUrl = null;
                                    destroy();
                                    r1.firstFrameRendered = false;
                                    r1.currentAlpha = 1.0f;
                                    if (r1.currentTask != null) {
                                        r1.currentTask.cancel(true);
                                        r1.currentTask = null;
                                    }
                                    updateFullscreenButton();
                                    updateShareButton();
                                    updateInlineButton();
                                    updatePlayButton();
                                    if (photo2 != null) {
                                        closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
                                        if (closestPhotoSizeWithSize != null) {
                                            if (photo2 != null) {
                                            }
                                            if (photo2 != null) {
                                            }
                                            r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                                            r1.drawImage = true;
                                        }
                                    } else {
                                        r1.drawImage = false;
                                    }
                                    if (r1.progressAnimation != null) {
                                        r1.progressAnimation.cancel();
                                        r1.progressAnimation = null;
                                    }
                                    r1.isLoading = true;
                                    r1.controlsView.setProgress(0);
                                    r1.currentYoutubeId = str3;
                                    str3 = null;
                                    if (str4 != null) {
                                        r1.initied = true;
                                        r1.playVideoUrl = str4;
                                        r1.playVideoType = "other";
                                        if (r1.isAutoplay) {
                                            preparePlayer();
                                        }
                                        showProgress(false, false);
                                        r1.controlsView.show(true, true);
                                    } else {
                                        if (str3 != null) {
                                            youtubeVideoTask = new YoutubeVideoTask(str3);
                                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                            r1.currentTask = youtubeVideoTask;
                                        } else if (str5 != null) {
                                            youtubeVideoTask = new VimeoVideoTask(str5);
                                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                            r1.currentTask = youtubeVideoTask;
                                        } else if (group3 != null) {
                                            youtubeVideoTask = new CoubVideoTask(group3);
                                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                            r1.currentTask = youtubeVideoTask;
                                            r1.isStream = true;
                                        } else if (group != null) {
                                            youtubeVideoTask = new AparatVideoTask(group);
                                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                            r1.currentTask = youtubeVideoTask;
                                        } else if (group2 != null) {
                                            twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                                            twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                            r1.currentTask = twitchClipVideoTask;
                                        } else if (str6 != null) {
                                            twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                                            twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                            r1.currentTask = twitchClipVideoTask;
                                            r1.isStream = true;
                                        }
                                        r1.controlsView.show(false, false);
                                        showProgress(true, false);
                                    }
                                    if (str6 == null) {
                                        r1.controlsView.setVisibility(0);
                                        return true;
                                    }
                                    r1.controlsView.setVisibility(8);
                                    return false;
                                }
                                group3 = null;
                                str6 = group2;
                                group2 = group;
                                group = str5;
                                str5 = str4;
                                str4 = group3;
                                r1.initied = false;
                                r1.isCompleted = false;
                                r1.isAutoplay = z;
                                r1.playVideoUrl = null;
                                r1.playAudioUrl = null;
                                destroy();
                                r1.firstFrameRendered = false;
                                r1.currentAlpha = 1.0f;
                                if (r1.currentTask != null) {
                                    r1.currentTask.cancel(true);
                                    r1.currentTask = null;
                                }
                                updateFullscreenButton();
                                updateShareButton();
                                updateInlineButton();
                                updatePlayButton();
                                if (photo2 != null) {
                                    r1.drawImage = false;
                                } else {
                                    closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
                                    if (closestPhotoSizeWithSize != null) {
                                        if (photo2 != null) {
                                        }
                                        if (photo2 != null) {
                                        }
                                        r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                                        r1.drawImage = true;
                                    }
                                }
                                if (r1.progressAnimation != null) {
                                    r1.progressAnimation.cancel();
                                    r1.progressAnimation = null;
                                }
                                r1.isLoading = true;
                                r1.controlsView.setProgress(0);
                                r1.currentYoutubeId = str3;
                                str3 = null;
                                if (str4 != null) {
                                    if (str3 != null) {
                                        youtubeVideoTask = new YoutubeVideoTask(str3);
                                        youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                        r1.currentTask = youtubeVideoTask;
                                    } else if (str5 != null) {
                                        youtubeVideoTask = new VimeoVideoTask(str5);
                                        youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                        r1.currentTask = youtubeVideoTask;
                                    } else if (group3 != null) {
                                        youtubeVideoTask = new CoubVideoTask(group3);
                                        youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                        r1.currentTask = youtubeVideoTask;
                                        r1.isStream = true;
                                    } else if (group != null) {
                                        youtubeVideoTask = new AparatVideoTask(group);
                                        youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                        r1.currentTask = youtubeVideoTask;
                                    } else if (group2 != null) {
                                        twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                                        twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                        r1.currentTask = twitchClipVideoTask;
                                    } else if (str6 != null) {
                                        twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                                        twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                        r1.currentTask = twitchClipVideoTask;
                                        r1.isStream = true;
                                    }
                                    r1.controlsView.show(false, false);
                                    showProgress(true, false);
                                } else {
                                    r1.initied = true;
                                    r1.playVideoUrl = str4;
                                    r1.playVideoType = "other";
                                    if (r1.isAutoplay) {
                                        preparePlayer();
                                    }
                                    showProgress(false, false);
                                    r1.controlsView.show(true, true);
                                }
                                if (str6 == null) {
                                    r1.controlsView.setVisibility(8);
                                    return false;
                                }
                                r1.controlsView.setVisibility(0);
                                return true;
                            }
                        }
                        group2 = null;
                        if (group2 == null) {
                            matcher5 = coubIdRegex.matcher(str7);
                            if (matcher5.find()) {
                            }
                            if (group3 == null) {
                                group3 = null;
                            }
                            str6 = group2;
                            group2 = group;
                            group = str5;
                            str5 = str4;
                            str4 = null;
                            r1.initied = false;
                            r1.isCompleted = false;
                            r1.isAutoplay = z;
                            r1.playVideoUrl = null;
                            r1.playAudioUrl = null;
                            destroy();
                            r1.firstFrameRendered = false;
                            r1.currentAlpha = 1.0f;
                            if (r1.currentTask != null) {
                                r1.currentTask.cancel(true);
                                r1.currentTask = null;
                            }
                            updateFullscreenButton();
                            updateShareButton();
                            updateInlineButton();
                            updatePlayButton();
                            if (photo2 != null) {
                                closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
                                if (closestPhotoSizeWithSize != null) {
                                    if (photo2 != null) {
                                    }
                                    if (photo2 != null) {
                                    }
                                    r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                                    r1.drawImage = true;
                                }
                            } else {
                                r1.drawImage = false;
                            }
                            if (r1.progressAnimation != null) {
                                r1.progressAnimation.cancel();
                                r1.progressAnimation = null;
                            }
                            r1.isLoading = true;
                            r1.controlsView.setProgress(0);
                            r1.currentYoutubeId = str3;
                            str3 = null;
                            if (str4 != null) {
                                r1.initied = true;
                                r1.playVideoUrl = str4;
                                r1.playVideoType = "other";
                                if (r1.isAutoplay) {
                                    preparePlayer();
                                }
                                showProgress(false, false);
                                r1.controlsView.show(true, true);
                            } else {
                                if (str3 != null) {
                                    youtubeVideoTask = new YoutubeVideoTask(str3);
                                    youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = youtubeVideoTask;
                                } else if (str5 != null) {
                                    youtubeVideoTask = new VimeoVideoTask(str5);
                                    youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = youtubeVideoTask;
                                } else if (group3 != null) {
                                    youtubeVideoTask = new CoubVideoTask(group3);
                                    youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = youtubeVideoTask;
                                    r1.isStream = true;
                                } else if (group != null) {
                                    youtubeVideoTask = new AparatVideoTask(group);
                                    youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = youtubeVideoTask;
                                } else if (group2 != null) {
                                    twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                                    twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = twitchClipVideoTask;
                                } else if (str6 != null) {
                                    twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                                    twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = twitchClipVideoTask;
                                    r1.isStream = true;
                                }
                                r1.controlsView.show(false, false);
                                showProgress(true, false);
                            }
                            if (str6 == null) {
                                r1.controlsView.setVisibility(0);
                                return true;
                            }
                            r1.controlsView.setVisibility(8);
                            return false;
                        }
                        group3 = null;
                        str6 = group2;
                        group2 = group;
                        group = str5;
                        str5 = str4;
                        str4 = group3;
                        r1.initied = false;
                        r1.isCompleted = false;
                        r1.isAutoplay = z;
                        r1.playVideoUrl = null;
                        r1.playAudioUrl = null;
                        destroy();
                        r1.firstFrameRendered = false;
                        r1.currentAlpha = 1.0f;
                        if (r1.currentTask != null) {
                            r1.currentTask.cancel(true);
                            r1.currentTask = null;
                        }
                        updateFullscreenButton();
                        updateShareButton();
                        updateInlineButton();
                        updatePlayButton();
                        if (photo2 != null) {
                            r1.drawImage = false;
                        } else {
                            closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
                            if (closestPhotoSizeWithSize != null) {
                                if (photo2 != null) {
                                }
                                if (photo2 != null) {
                                }
                                r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                                r1.drawImage = true;
                            }
                        }
                        if (r1.progressAnimation != null) {
                            r1.progressAnimation.cancel();
                            r1.progressAnimation = null;
                        }
                        r1.isLoading = true;
                        r1.controlsView.setProgress(0);
                        r1.currentYoutubeId = str3;
                        str3 = null;
                        if (str4 != null) {
                            if (str3 != null) {
                                youtubeVideoTask = new YoutubeVideoTask(str3);
                                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                r1.currentTask = youtubeVideoTask;
                            } else if (str5 != null) {
                                youtubeVideoTask = new VimeoVideoTask(str5);
                                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                r1.currentTask = youtubeVideoTask;
                            } else if (group3 != null) {
                                youtubeVideoTask = new CoubVideoTask(group3);
                                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                r1.currentTask = youtubeVideoTask;
                                r1.isStream = true;
                            } else if (group != null) {
                                youtubeVideoTask = new AparatVideoTask(group);
                                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                r1.currentTask = youtubeVideoTask;
                            } else if (group2 != null) {
                                twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                                twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                r1.currentTask = twitchClipVideoTask;
                            } else if (str6 != null) {
                                twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                                twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                r1.currentTask = twitchClipVideoTask;
                                r1.isStream = true;
                            }
                            r1.controlsView.show(false, false);
                            showProgress(true, false);
                        } else {
                            r1.initied = true;
                            r1.playVideoUrl = str4;
                            r1.playVideoType = "other";
                            if (r1.isAutoplay) {
                                preparePlayer();
                            }
                            showProgress(false, false);
                            r1.controlsView.show(true, true);
                        }
                        if (str6 == null) {
                            r1.controlsView.setVisibility(8);
                            return false;
                        }
                        r1.controlsView.setVisibility(0);
                        return true;
                    }
                }
                str5 = null;
                if (str5 == null) {
                    matcher3 = twitchClipIdRegex.matcher(str7);
                    if (matcher3.find()) {
                    }
                    if (group != null) {
                        if (group == null) {
                            matcher4 = twitchStreamIdRegex.matcher(str7);
                            if (matcher4.find()) {
                            }
                            if (group2 != null) {
                                if (group2 == null) {
                                    matcher5 = coubIdRegex.matcher(str7);
                                    if (matcher5.find()) {
                                    }
                                    if (group3 == null) {
                                        group3 = null;
                                    }
                                    str6 = group2;
                                    group2 = group;
                                    group = str5;
                                    str5 = str4;
                                    str4 = null;
                                    r1.initied = false;
                                    r1.isCompleted = false;
                                    r1.isAutoplay = z;
                                    r1.playVideoUrl = null;
                                    r1.playAudioUrl = null;
                                    destroy();
                                    r1.firstFrameRendered = false;
                                    r1.currentAlpha = 1.0f;
                                    if (r1.currentTask != null) {
                                        r1.currentTask.cancel(true);
                                        r1.currentTask = null;
                                    }
                                    updateFullscreenButton();
                                    updateShareButton();
                                    updateInlineButton();
                                    updatePlayButton();
                                    if (photo2 != null) {
                                        closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
                                        if (closestPhotoSizeWithSize != null) {
                                            if (photo2 != null) {
                                            }
                                            if (photo2 != null) {
                                            }
                                            r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                                            r1.drawImage = true;
                                        }
                                    } else {
                                        r1.drawImage = false;
                                    }
                                    if (r1.progressAnimation != null) {
                                        r1.progressAnimation.cancel();
                                        r1.progressAnimation = null;
                                    }
                                    r1.isLoading = true;
                                    r1.controlsView.setProgress(0);
                                    r1.currentYoutubeId = str3;
                                    str3 = null;
                                    if (str4 != null) {
                                        r1.initied = true;
                                        r1.playVideoUrl = str4;
                                        r1.playVideoType = "other";
                                        if (r1.isAutoplay) {
                                            preparePlayer();
                                        }
                                        showProgress(false, false);
                                        r1.controlsView.show(true, true);
                                    } else {
                                        if (str3 != null) {
                                            youtubeVideoTask = new YoutubeVideoTask(str3);
                                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                            r1.currentTask = youtubeVideoTask;
                                        } else if (str5 != null) {
                                            youtubeVideoTask = new VimeoVideoTask(str5);
                                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                            r1.currentTask = youtubeVideoTask;
                                        } else if (group3 != null) {
                                            youtubeVideoTask = new CoubVideoTask(group3);
                                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                            r1.currentTask = youtubeVideoTask;
                                            r1.isStream = true;
                                        } else if (group != null) {
                                            youtubeVideoTask = new AparatVideoTask(group);
                                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                            r1.currentTask = youtubeVideoTask;
                                        } else if (group2 != null) {
                                            twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                                            twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                            r1.currentTask = twitchClipVideoTask;
                                        } else if (str6 != null) {
                                            twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                                            twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                            r1.currentTask = twitchClipVideoTask;
                                            r1.isStream = true;
                                        }
                                        r1.controlsView.show(false, false);
                                        showProgress(true, false);
                                    }
                                    if (str6 == null) {
                                        r1.controlsView.setVisibility(0);
                                        return true;
                                    }
                                    r1.controlsView.setVisibility(8);
                                    return false;
                                }
                                group3 = null;
                                str6 = group2;
                                group2 = group;
                                group = str5;
                                str5 = str4;
                                str4 = group3;
                                r1.initied = false;
                                r1.isCompleted = false;
                                r1.isAutoplay = z;
                                r1.playVideoUrl = null;
                                r1.playAudioUrl = null;
                                destroy();
                                r1.firstFrameRendered = false;
                                r1.currentAlpha = 1.0f;
                                if (r1.currentTask != null) {
                                    r1.currentTask.cancel(true);
                                    r1.currentTask = null;
                                }
                                updateFullscreenButton();
                                updateShareButton();
                                updateInlineButton();
                                updatePlayButton();
                                if (photo2 != null) {
                                    r1.drawImage = false;
                                } else {
                                    closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
                                    if (closestPhotoSizeWithSize != null) {
                                        if (photo2 != null) {
                                        }
                                        if (photo2 != null) {
                                        }
                                        r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                                        r1.drawImage = true;
                                    }
                                }
                                if (r1.progressAnimation != null) {
                                    r1.progressAnimation.cancel();
                                    r1.progressAnimation = null;
                                }
                                r1.isLoading = true;
                                r1.controlsView.setProgress(0);
                                r1.currentYoutubeId = str3;
                                str3 = null;
                                if (str4 != null) {
                                    if (str3 != null) {
                                        youtubeVideoTask = new YoutubeVideoTask(str3);
                                        youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                        r1.currentTask = youtubeVideoTask;
                                    } else if (str5 != null) {
                                        youtubeVideoTask = new VimeoVideoTask(str5);
                                        youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                        r1.currentTask = youtubeVideoTask;
                                    } else if (group3 != null) {
                                        youtubeVideoTask = new CoubVideoTask(group3);
                                        youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                        r1.currentTask = youtubeVideoTask;
                                        r1.isStream = true;
                                    } else if (group != null) {
                                        youtubeVideoTask = new AparatVideoTask(group);
                                        youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                        r1.currentTask = youtubeVideoTask;
                                    } else if (group2 != null) {
                                        twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                                        twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                        r1.currentTask = twitchClipVideoTask;
                                    } else if (str6 != null) {
                                        twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                                        twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                        r1.currentTask = twitchClipVideoTask;
                                        r1.isStream = true;
                                    }
                                    r1.controlsView.show(false, false);
                                    showProgress(true, false);
                                } else {
                                    r1.initied = true;
                                    r1.playVideoUrl = str4;
                                    r1.playVideoType = "other";
                                    if (r1.isAutoplay) {
                                        preparePlayer();
                                    }
                                    showProgress(false, false);
                                    r1.controlsView.show(true, true);
                                }
                                if (str6 == null) {
                                    r1.controlsView.setVisibility(8);
                                    return false;
                                }
                                r1.controlsView.setVisibility(0);
                                return true;
                            }
                        }
                        group2 = null;
                        if (group2 == null) {
                            matcher5 = coubIdRegex.matcher(str7);
                            if (matcher5.find()) {
                            }
                            if (group3 == null) {
                                group3 = null;
                            }
                            str6 = group2;
                            group2 = group;
                            group = str5;
                            str5 = str4;
                            str4 = null;
                            r1.initied = false;
                            r1.isCompleted = false;
                            r1.isAutoplay = z;
                            r1.playVideoUrl = null;
                            r1.playAudioUrl = null;
                            destroy();
                            r1.firstFrameRendered = false;
                            r1.currentAlpha = 1.0f;
                            if (r1.currentTask != null) {
                                r1.currentTask.cancel(true);
                                r1.currentTask = null;
                            }
                            updateFullscreenButton();
                            updateShareButton();
                            updateInlineButton();
                            updatePlayButton();
                            if (photo2 != null) {
                                closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
                                if (closestPhotoSizeWithSize != null) {
                                    if (photo2 != null) {
                                    }
                                    if (photo2 != null) {
                                    }
                                    r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                                    r1.drawImage = true;
                                }
                            } else {
                                r1.drawImage = false;
                            }
                            if (r1.progressAnimation != null) {
                                r1.progressAnimation.cancel();
                                r1.progressAnimation = null;
                            }
                            r1.isLoading = true;
                            r1.controlsView.setProgress(0);
                            r1.currentYoutubeId = str3;
                            str3 = null;
                            if (str4 != null) {
                                r1.initied = true;
                                r1.playVideoUrl = str4;
                                r1.playVideoType = "other";
                                if (r1.isAutoplay) {
                                    preparePlayer();
                                }
                                showProgress(false, false);
                                r1.controlsView.show(true, true);
                            } else {
                                if (str3 != null) {
                                    youtubeVideoTask = new YoutubeVideoTask(str3);
                                    youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = youtubeVideoTask;
                                } else if (str5 != null) {
                                    youtubeVideoTask = new VimeoVideoTask(str5);
                                    youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = youtubeVideoTask;
                                } else if (group3 != null) {
                                    youtubeVideoTask = new CoubVideoTask(group3);
                                    youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = youtubeVideoTask;
                                    r1.isStream = true;
                                } else if (group != null) {
                                    youtubeVideoTask = new AparatVideoTask(group);
                                    youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = youtubeVideoTask;
                                } else if (group2 != null) {
                                    twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                                    twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = twitchClipVideoTask;
                                } else if (str6 != null) {
                                    twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                                    twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = twitchClipVideoTask;
                                    r1.isStream = true;
                                }
                                r1.controlsView.show(false, false);
                                showProgress(true, false);
                            }
                            if (str6 == null) {
                                r1.controlsView.setVisibility(0);
                                return true;
                            }
                            r1.controlsView.setVisibility(8);
                            return false;
                        }
                        group3 = null;
                        str6 = group2;
                        group2 = group;
                        group = str5;
                        str5 = str4;
                        str4 = group3;
                        r1.initied = false;
                        r1.isCompleted = false;
                        r1.isAutoplay = z;
                        r1.playVideoUrl = null;
                        r1.playAudioUrl = null;
                        destroy();
                        r1.firstFrameRendered = false;
                        r1.currentAlpha = 1.0f;
                        if (r1.currentTask != null) {
                            r1.currentTask.cancel(true);
                            r1.currentTask = null;
                        }
                        updateFullscreenButton();
                        updateShareButton();
                        updateInlineButton();
                        updatePlayButton();
                        if (photo2 != null) {
                            r1.drawImage = false;
                        } else {
                            closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
                            if (closestPhotoSizeWithSize != null) {
                                if (photo2 != null) {
                                }
                                if (photo2 != null) {
                                }
                                r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                                r1.drawImage = true;
                            }
                        }
                        if (r1.progressAnimation != null) {
                            r1.progressAnimation.cancel();
                            r1.progressAnimation = null;
                        }
                        r1.isLoading = true;
                        r1.controlsView.setProgress(0);
                        r1.currentYoutubeId = str3;
                        str3 = null;
                        if (str4 != null) {
                            if (str3 != null) {
                                youtubeVideoTask = new YoutubeVideoTask(str3);
                                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                r1.currentTask = youtubeVideoTask;
                            } else if (str5 != null) {
                                youtubeVideoTask = new VimeoVideoTask(str5);
                                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                r1.currentTask = youtubeVideoTask;
                            } else if (group3 != null) {
                                youtubeVideoTask = new CoubVideoTask(group3);
                                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                r1.currentTask = youtubeVideoTask;
                                r1.isStream = true;
                            } else if (group != null) {
                                youtubeVideoTask = new AparatVideoTask(group);
                                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                r1.currentTask = youtubeVideoTask;
                            } else if (group2 != null) {
                                twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                                twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                r1.currentTask = twitchClipVideoTask;
                            } else if (str6 != null) {
                                twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                                twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                r1.currentTask = twitchClipVideoTask;
                                r1.isStream = true;
                            }
                            r1.controlsView.show(false, false);
                            showProgress(true, false);
                        } else {
                            r1.initied = true;
                            r1.playVideoUrl = str4;
                            r1.playVideoType = "other";
                            if (r1.isAutoplay) {
                                preparePlayer();
                            }
                            showProgress(false, false);
                            r1.controlsView.show(true, true);
                        }
                        if (str6 == null) {
                            r1.controlsView.setVisibility(8);
                            return false;
                        }
                        r1.controlsView.setVisibility(0);
                        return true;
                    }
                }
                group = null;
                if (group == null) {
                    matcher4 = twitchStreamIdRegex.matcher(str7);
                    if (matcher4.find()) {
                    }
                    if (group2 != null) {
                        if (group2 == null) {
                            matcher5 = coubIdRegex.matcher(str7);
                            if (matcher5.find()) {
                            }
                            if (group3 == null) {
                                group3 = null;
                            }
                            str6 = group2;
                            group2 = group;
                            group = str5;
                            str5 = str4;
                            str4 = null;
                            r1.initied = false;
                            r1.isCompleted = false;
                            r1.isAutoplay = z;
                            r1.playVideoUrl = null;
                            r1.playAudioUrl = null;
                            destroy();
                            r1.firstFrameRendered = false;
                            r1.currentAlpha = 1.0f;
                            if (r1.currentTask != null) {
                                r1.currentTask.cancel(true);
                                r1.currentTask = null;
                            }
                            updateFullscreenButton();
                            updateShareButton();
                            updateInlineButton();
                            updatePlayButton();
                            if (photo2 != null) {
                                closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
                                if (closestPhotoSizeWithSize != null) {
                                    if (photo2 != null) {
                                    }
                                    if (photo2 != null) {
                                    }
                                    r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                                    r1.drawImage = true;
                                }
                            } else {
                                r1.drawImage = false;
                            }
                            if (r1.progressAnimation != null) {
                                r1.progressAnimation.cancel();
                                r1.progressAnimation = null;
                            }
                            r1.isLoading = true;
                            r1.controlsView.setProgress(0);
                            r1.currentYoutubeId = str3;
                            str3 = null;
                            if (str4 != null) {
                                r1.initied = true;
                                r1.playVideoUrl = str4;
                                r1.playVideoType = "other";
                                if (r1.isAutoplay) {
                                    preparePlayer();
                                }
                                showProgress(false, false);
                                r1.controlsView.show(true, true);
                            } else {
                                if (str3 != null) {
                                    youtubeVideoTask = new YoutubeVideoTask(str3);
                                    youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = youtubeVideoTask;
                                } else if (str5 != null) {
                                    youtubeVideoTask = new VimeoVideoTask(str5);
                                    youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = youtubeVideoTask;
                                } else if (group3 != null) {
                                    youtubeVideoTask = new CoubVideoTask(group3);
                                    youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = youtubeVideoTask;
                                    r1.isStream = true;
                                } else if (group != null) {
                                    youtubeVideoTask = new AparatVideoTask(group);
                                    youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = youtubeVideoTask;
                                } else if (group2 != null) {
                                    twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                                    twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = twitchClipVideoTask;
                                } else if (str6 != null) {
                                    twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                                    twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = twitchClipVideoTask;
                                    r1.isStream = true;
                                }
                                r1.controlsView.show(false, false);
                                showProgress(true, false);
                            }
                            if (str6 == null) {
                                r1.controlsView.setVisibility(0);
                                return true;
                            }
                            r1.controlsView.setVisibility(8);
                            return false;
                        }
                        group3 = null;
                        str6 = group2;
                        group2 = group;
                        group = str5;
                        str5 = str4;
                        str4 = group3;
                        r1.initied = false;
                        r1.isCompleted = false;
                        r1.isAutoplay = z;
                        r1.playVideoUrl = null;
                        r1.playAudioUrl = null;
                        destroy();
                        r1.firstFrameRendered = false;
                        r1.currentAlpha = 1.0f;
                        if (r1.currentTask != null) {
                            r1.currentTask.cancel(true);
                            r1.currentTask = null;
                        }
                        updateFullscreenButton();
                        updateShareButton();
                        updateInlineButton();
                        updatePlayButton();
                        if (photo2 != null) {
                            r1.drawImage = false;
                        } else {
                            closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
                            if (closestPhotoSizeWithSize != null) {
                                if (photo2 != null) {
                                }
                                if (photo2 != null) {
                                }
                                r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                                r1.drawImage = true;
                            }
                        }
                        if (r1.progressAnimation != null) {
                            r1.progressAnimation.cancel();
                            r1.progressAnimation = null;
                        }
                        r1.isLoading = true;
                        r1.controlsView.setProgress(0);
                        r1.currentYoutubeId = str3;
                        str3 = null;
                        if (str4 != null) {
                            if (str3 != null) {
                                youtubeVideoTask = new YoutubeVideoTask(str3);
                                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                r1.currentTask = youtubeVideoTask;
                            } else if (str5 != null) {
                                youtubeVideoTask = new VimeoVideoTask(str5);
                                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                r1.currentTask = youtubeVideoTask;
                            } else if (group3 != null) {
                                youtubeVideoTask = new CoubVideoTask(group3);
                                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                r1.currentTask = youtubeVideoTask;
                                r1.isStream = true;
                            } else if (group != null) {
                                youtubeVideoTask = new AparatVideoTask(group);
                                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                r1.currentTask = youtubeVideoTask;
                            } else if (group2 != null) {
                                twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                                twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                r1.currentTask = twitchClipVideoTask;
                            } else if (str6 != null) {
                                twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                                twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                r1.currentTask = twitchClipVideoTask;
                                r1.isStream = true;
                            }
                            r1.controlsView.show(false, false);
                            showProgress(true, false);
                        } else {
                            r1.initied = true;
                            r1.playVideoUrl = str4;
                            r1.playVideoType = "other";
                            if (r1.isAutoplay) {
                                preparePlayer();
                            }
                            showProgress(false, false);
                            r1.controlsView.show(true, true);
                        }
                        if (str6 == null) {
                            r1.controlsView.setVisibility(8);
                            return false;
                        }
                        r1.controlsView.setVisibility(0);
                        return true;
                    }
                }
                group2 = null;
                if (group2 == null) {
                    matcher5 = coubIdRegex.matcher(str7);
                    if (matcher5.find()) {
                    }
                    if (group3 == null) {
                        group3 = null;
                    }
                    str6 = group2;
                    group2 = group;
                    group = str5;
                    str5 = str4;
                    str4 = null;
                    r1.initied = false;
                    r1.isCompleted = false;
                    r1.isAutoplay = z;
                    r1.playVideoUrl = null;
                    r1.playAudioUrl = null;
                    destroy();
                    r1.firstFrameRendered = false;
                    r1.currentAlpha = 1.0f;
                    if (r1.currentTask != null) {
                        r1.currentTask.cancel(true);
                        r1.currentTask = null;
                    }
                    updateFullscreenButton();
                    updateShareButton();
                    updateInlineButton();
                    updatePlayButton();
                    if (photo2 != null) {
                        closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
                        if (closestPhotoSizeWithSize != null) {
                            if (photo2 != null) {
                            }
                            if (photo2 != null) {
                            }
                            r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                            r1.drawImage = true;
                        }
                    } else {
                        r1.drawImage = false;
                    }
                    if (r1.progressAnimation != null) {
                        r1.progressAnimation.cancel();
                        r1.progressAnimation = null;
                    }
                    r1.isLoading = true;
                    r1.controlsView.setProgress(0);
                    r1.currentYoutubeId = str3;
                    str3 = null;
                    if (str4 != null) {
                        r1.initied = true;
                        r1.playVideoUrl = str4;
                        r1.playVideoType = "other";
                        if (r1.isAutoplay) {
                            preparePlayer();
                        }
                        showProgress(false, false);
                        r1.controlsView.show(true, true);
                    } else {
                        if (str3 != null) {
                            youtubeVideoTask = new YoutubeVideoTask(str3);
                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                            r1.currentTask = youtubeVideoTask;
                        } else if (str5 != null) {
                            youtubeVideoTask = new VimeoVideoTask(str5);
                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                            r1.currentTask = youtubeVideoTask;
                        } else if (group3 != null) {
                            youtubeVideoTask = new CoubVideoTask(group3);
                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                            r1.currentTask = youtubeVideoTask;
                            r1.isStream = true;
                        } else if (group != null) {
                            youtubeVideoTask = new AparatVideoTask(group);
                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                            r1.currentTask = youtubeVideoTask;
                        } else if (group2 != null) {
                            twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                            twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                            r1.currentTask = twitchClipVideoTask;
                        } else if (str6 != null) {
                            twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                            twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                            r1.currentTask = twitchClipVideoTask;
                            r1.isStream = true;
                        }
                        r1.controlsView.show(false, false);
                        showProgress(true, false);
                    }
                    if (str6 == null) {
                        r1.controlsView.setVisibility(0);
                        return true;
                    }
                    r1.controlsView.setVisibility(8);
                    return false;
                }
                group3 = null;
                str6 = group2;
                group2 = group;
                group = str5;
                str5 = str4;
                str4 = group3;
                r1.initied = false;
                r1.isCompleted = false;
                r1.isAutoplay = z;
                r1.playVideoUrl = null;
                r1.playAudioUrl = null;
                destroy();
                r1.firstFrameRendered = false;
                r1.currentAlpha = 1.0f;
                if (r1.currentTask != null) {
                    r1.currentTask.cancel(true);
                    r1.currentTask = null;
                }
                updateFullscreenButton();
                updateShareButton();
                updateInlineButton();
                updatePlayButton();
                if (photo2 != null) {
                    r1.drawImage = false;
                } else {
                    closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
                    if (closestPhotoSizeWithSize != null) {
                        if (photo2 != null) {
                        }
                        if (photo2 != null) {
                        }
                        r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                        r1.drawImage = true;
                    }
                }
                if (r1.progressAnimation != null) {
                    r1.progressAnimation.cancel();
                    r1.progressAnimation = null;
                }
                r1.isLoading = true;
                r1.controlsView.setProgress(0);
                r1.currentYoutubeId = str3;
                str3 = null;
                if (str4 != null) {
                    if (str3 != null) {
                        youtubeVideoTask = new YoutubeVideoTask(str3);
                        youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                        r1.currentTask = youtubeVideoTask;
                    } else if (str5 != null) {
                        youtubeVideoTask = new VimeoVideoTask(str5);
                        youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                        r1.currentTask = youtubeVideoTask;
                    } else if (group3 != null) {
                        youtubeVideoTask = new CoubVideoTask(group3);
                        youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                        r1.currentTask = youtubeVideoTask;
                        r1.isStream = true;
                    } else if (group != null) {
                        youtubeVideoTask = new AparatVideoTask(group);
                        youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                        r1.currentTask = youtubeVideoTask;
                    } else if (group2 != null) {
                        twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                        twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                        r1.currentTask = twitchClipVideoTask;
                    } else if (str6 != null) {
                        twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                        twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                        r1.currentTask = twitchClipVideoTask;
                        r1.isStream = true;
                    }
                    r1.controlsView.show(false, false);
                    showProgress(true, false);
                } else {
                    r1.initied = true;
                    r1.playVideoUrl = str4;
                    r1.playVideoType = "other";
                    if (r1.isAutoplay) {
                        preparePlayer();
                    }
                    showProgress(false, false);
                    r1.controlsView.show(true, true);
                }
                if (str6 == null) {
                    r1.controlsView.setVisibility(8);
                    return false;
                }
                r1.controlsView.setVisibility(0);
                return true;
            }
            str3 = null;
            if (str3 == null) {
                matcher = vimeoIdRegex.matcher(str7);
                if (matcher.find()) {
                }
                if (str4 != null) {
                    if (str4 == null) {
                        matcher2 = aparatIdRegex.matcher(str7);
                        if (matcher2.find()) {
                        }
                        if (str5 != null) {
                            if (str5 == null) {
                                matcher3 = twitchClipIdRegex.matcher(str7);
                                if (matcher3.find()) {
                                }
                                if (group != null) {
                                    if (group == null) {
                                        matcher4 = twitchStreamIdRegex.matcher(str7);
                                        if (matcher4.find()) {
                                        }
                                        if (group2 != null) {
                                            if (group2 == null) {
                                                matcher5 = coubIdRegex.matcher(str7);
                                                if (matcher5.find()) {
                                                }
                                                if (group3 == null) {
                                                    group3 = null;
                                                }
                                                str6 = group2;
                                                group2 = group;
                                                group = str5;
                                                str5 = str4;
                                                str4 = null;
                                                r1.initied = false;
                                                r1.isCompleted = false;
                                                r1.isAutoplay = z;
                                                r1.playVideoUrl = null;
                                                r1.playAudioUrl = null;
                                                destroy();
                                                r1.firstFrameRendered = false;
                                                r1.currentAlpha = 1.0f;
                                                if (r1.currentTask != null) {
                                                    r1.currentTask.cancel(true);
                                                    r1.currentTask = null;
                                                }
                                                updateFullscreenButton();
                                                updateShareButton();
                                                updateInlineButton();
                                                updatePlayButton();
                                                if (photo2 != null) {
                                                    closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
                                                    if (closestPhotoSizeWithSize != null) {
                                                        if (photo2 != null) {
                                                        }
                                                        if (photo2 != null) {
                                                        }
                                                        r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                                                        r1.drawImage = true;
                                                    }
                                                } else {
                                                    r1.drawImage = false;
                                                }
                                                if (r1.progressAnimation != null) {
                                                    r1.progressAnimation.cancel();
                                                    r1.progressAnimation = null;
                                                }
                                                r1.isLoading = true;
                                                r1.controlsView.setProgress(0);
                                                r1.currentYoutubeId = str3;
                                                str3 = null;
                                                if (str4 != null) {
                                                    r1.initied = true;
                                                    r1.playVideoUrl = str4;
                                                    r1.playVideoType = "other";
                                                    if (r1.isAutoplay) {
                                                        preparePlayer();
                                                    }
                                                    showProgress(false, false);
                                                    r1.controlsView.show(true, true);
                                                } else {
                                                    if (str3 != null) {
                                                        youtubeVideoTask = new YoutubeVideoTask(str3);
                                                        youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                        r1.currentTask = youtubeVideoTask;
                                                    } else if (str5 != null) {
                                                        youtubeVideoTask = new VimeoVideoTask(str5);
                                                        youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                        r1.currentTask = youtubeVideoTask;
                                                    } else if (group3 != null) {
                                                        youtubeVideoTask = new CoubVideoTask(group3);
                                                        youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                        r1.currentTask = youtubeVideoTask;
                                                        r1.isStream = true;
                                                    } else if (group != null) {
                                                        youtubeVideoTask = new AparatVideoTask(group);
                                                        youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                        r1.currentTask = youtubeVideoTask;
                                                    } else if (group2 != null) {
                                                        twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                                                        twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                        r1.currentTask = twitchClipVideoTask;
                                                    } else if (str6 != null) {
                                                        twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                                                        twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                        r1.currentTask = twitchClipVideoTask;
                                                        r1.isStream = true;
                                                    }
                                                    r1.controlsView.show(false, false);
                                                    showProgress(true, false);
                                                }
                                                if (str6 == null) {
                                                    r1.controlsView.setVisibility(0);
                                                    return true;
                                                }
                                                r1.controlsView.setVisibility(8);
                                                return false;
                                            }
                                            group3 = null;
                                            str6 = group2;
                                            group2 = group;
                                            group = str5;
                                            str5 = str4;
                                            str4 = group3;
                                            r1.initied = false;
                                            r1.isCompleted = false;
                                            r1.isAutoplay = z;
                                            r1.playVideoUrl = null;
                                            r1.playAudioUrl = null;
                                            destroy();
                                            r1.firstFrameRendered = false;
                                            r1.currentAlpha = 1.0f;
                                            if (r1.currentTask != null) {
                                                r1.currentTask.cancel(true);
                                                r1.currentTask = null;
                                            }
                                            updateFullscreenButton();
                                            updateShareButton();
                                            updateInlineButton();
                                            updatePlayButton();
                                            if (photo2 != null) {
                                                r1.drawImage = false;
                                            } else {
                                                closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
                                                if (closestPhotoSizeWithSize != null) {
                                                    if (photo2 != null) {
                                                    }
                                                    if (photo2 != null) {
                                                    }
                                                    r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                                                    r1.drawImage = true;
                                                }
                                            }
                                            if (r1.progressAnimation != null) {
                                                r1.progressAnimation.cancel();
                                                r1.progressAnimation = null;
                                            }
                                            r1.isLoading = true;
                                            r1.controlsView.setProgress(0);
                                            r1.currentYoutubeId = str3;
                                            str3 = null;
                                            if (str4 != null) {
                                                if (str3 != null) {
                                                    youtubeVideoTask = new YoutubeVideoTask(str3);
                                                    youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                    r1.currentTask = youtubeVideoTask;
                                                } else if (str5 != null) {
                                                    youtubeVideoTask = new VimeoVideoTask(str5);
                                                    youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                    r1.currentTask = youtubeVideoTask;
                                                } else if (group3 != null) {
                                                    youtubeVideoTask = new CoubVideoTask(group3);
                                                    youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                    r1.currentTask = youtubeVideoTask;
                                                    r1.isStream = true;
                                                } else if (group != null) {
                                                    youtubeVideoTask = new AparatVideoTask(group);
                                                    youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                    r1.currentTask = youtubeVideoTask;
                                                } else if (group2 != null) {
                                                    twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                                                    twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                    r1.currentTask = twitchClipVideoTask;
                                                } else if (str6 != null) {
                                                    twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                                                    twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                    r1.currentTask = twitchClipVideoTask;
                                                    r1.isStream = true;
                                                }
                                                r1.controlsView.show(false, false);
                                                showProgress(true, false);
                                            } else {
                                                r1.initied = true;
                                                r1.playVideoUrl = str4;
                                                r1.playVideoType = "other";
                                                if (r1.isAutoplay) {
                                                    preparePlayer();
                                                }
                                                showProgress(false, false);
                                                r1.controlsView.show(true, true);
                                            }
                                            if (str6 == null) {
                                                r1.controlsView.setVisibility(8);
                                                return false;
                                            }
                                            r1.controlsView.setVisibility(0);
                                            return true;
                                        }
                                    }
                                    group2 = null;
                                    if (group2 == null) {
                                        matcher5 = coubIdRegex.matcher(str7);
                                        if (matcher5.find()) {
                                        }
                                        if (group3 == null) {
                                            group3 = null;
                                        }
                                        str6 = group2;
                                        group2 = group;
                                        group = str5;
                                        str5 = str4;
                                        str4 = null;
                                        r1.initied = false;
                                        r1.isCompleted = false;
                                        r1.isAutoplay = z;
                                        r1.playVideoUrl = null;
                                        r1.playAudioUrl = null;
                                        destroy();
                                        r1.firstFrameRendered = false;
                                        r1.currentAlpha = 1.0f;
                                        if (r1.currentTask != null) {
                                            r1.currentTask.cancel(true);
                                            r1.currentTask = null;
                                        }
                                        updateFullscreenButton();
                                        updateShareButton();
                                        updateInlineButton();
                                        updatePlayButton();
                                        if (photo2 != null) {
                                            closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
                                            if (closestPhotoSizeWithSize != null) {
                                                if (photo2 != null) {
                                                }
                                                if (photo2 != null) {
                                                }
                                                r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                                                r1.drawImage = true;
                                            }
                                        } else {
                                            r1.drawImage = false;
                                        }
                                        if (r1.progressAnimation != null) {
                                            r1.progressAnimation.cancel();
                                            r1.progressAnimation = null;
                                        }
                                        r1.isLoading = true;
                                        r1.controlsView.setProgress(0);
                                        r1.currentYoutubeId = str3;
                                        str3 = null;
                                        if (str4 != null) {
                                            r1.initied = true;
                                            r1.playVideoUrl = str4;
                                            r1.playVideoType = "other";
                                            if (r1.isAutoplay) {
                                                preparePlayer();
                                            }
                                            showProgress(false, false);
                                            r1.controlsView.show(true, true);
                                        } else {
                                            if (str3 != null) {
                                                youtubeVideoTask = new YoutubeVideoTask(str3);
                                                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                r1.currentTask = youtubeVideoTask;
                                            } else if (str5 != null) {
                                                youtubeVideoTask = new VimeoVideoTask(str5);
                                                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                r1.currentTask = youtubeVideoTask;
                                            } else if (group3 != null) {
                                                youtubeVideoTask = new CoubVideoTask(group3);
                                                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                r1.currentTask = youtubeVideoTask;
                                                r1.isStream = true;
                                            } else if (group != null) {
                                                youtubeVideoTask = new AparatVideoTask(group);
                                                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                r1.currentTask = youtubeVideoTask;
                                            } else if (group2 != null) {
                                                twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                                                twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                r1.currentTask = twitchClipVideoTask;
                                            } else if (str6 != null) {
                                                twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                                                twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                r1.currentTask = twitchClipVideoTask;
                                                r1.isStream = true;
                                            }
                                            r1.controlsView.show(false, false);
                                            showProgress(true, false);
                                        }
                                        if (str6 == null) {
                                            r1.controlsView.setVisibility(0);
                                            return true;
                                        }
                                        r1.controlsView.setVisibility(8);
                                        return false;
                                    }
                                    group3 = null;
                                    str6 = group2;
                                    group2 = group;
                                    group = str5;
                                    str5 = str4;
                                    str4 = group3;
                                    r1.initied = false;
                                    r1.isCompleted = false;
                                    r1.isAutoplay = z;
                                    r1.playVideoUrl = null;
                                    r1.playAudioUrl = null;
                                    destroy();
                                    r1.firstFrameRendered = false;
                                    r1.currentAlpha = 1.0f;
                                    if (r1.currentTask != null) {
                                        r1.currentTask.cancel(true);
                                        r1.currentTask = null;
                                    }
                                    updateFullscreenButton();
                                    updateShareButton();
                                    updateInlineButton();
                                    updatePlayButton();
                                    if (photo2 != null) {
                                        r1.drawImage = false;
                                    } else {
                                        closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
                                        if (closestPhotoSizeWithSize != null) {
                                            if (photo2 != null) {
                                            }
                                            if (photo2 != null) {
                                            }
                                            r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                                            r1.drawImage = true;
                                        }
                                    }
                                    if (r1.progressAnimation != null) {
                                        r1.progressAnimation.cancel();
                                        r1.progressAnimation = null;
                                    }
                                    r1.isLoading = true;
                                    r1.controlsView.setProgress(0);
                                    r1.currentYoutubeId = str3;
                                    str3 = null;
                                    if (str4 != null) {
                                        if (str3 != null) {
                                            youtubeVideoTask = new YoutubeVideoTask(str3);
                                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                            r1.currentTask = youtubeVideoTask;
                                        } else if (str5 != null) {
                                            youtubeVideoTask = new VimeoVideoTask(str5);
                                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                            r1.currentTask = youtubeVideoTask;
                                        } else if (group3 != null) {
                                            youtubeVideoTask = new CoubVideoTask(group3);
                                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                            r1.currentTask = youtubeVideoTask;
                                            r1.isStream = true;
                                        } else if (group != null) {
                                            youtubeVideoTask = new AparatVideoTask(group);
                                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                            r1.currentTask = youtubeVideoTask;
                                        } else if (group2 != null) {
                                            twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                                            twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                            r1.currentTask = twitchClipVideoTask;
                                        } else if (str6 != null) {
                                            twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                                            twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                            r1.currentTask = twitchClipVideoTask;
                                            r1.isStream = true;
                                        }
                                        r1.controlsView.show(false, false);
                                        showProgress(true, false);
                                    } else {
                                        r1.initied = true;
                                        r1.playVideoUrl = str4;
                                        r1.playVideoType = "other";
                                        if (r1.isAutoplay) {
                                            preparePlayer();
                                        }
                                        showProgress(false, false);
                                        r1.controlsView.show(true, true);
                                    }
                                    if (str6 == null) {
                                        r1.controlsView.setVisibility(8);
                                        return false;
                                    }
                                    r1.controlsView.setVisibility(0);
                                    return true;
                                }
                            }
                            group = null;
                            if (group == null) {
                                matcher4 = twitchStreamIdRegex.matcher(str7);
                                if (matcher4.find()) {
                                }
                                if (group2 != null) {
                                    if (group2 == null) {
                                        matcher5 = coubIdRegex.matcher(str7);
                                        if (matcher5.find()) {
                                        }
                                        if (group3 == null) {
                                            group3 = null;
                                        }
                                        str6 = group2;
                                        group2 = group;
                                        group = str5;
                                        str5 = str4;
                                        str4 = null;
                                        r1.initied = false;
                                        r1.isCompleted = false;
                                        r1.isAutoplay = z;
                                        r1.playVideoUrl = null;
                                        r1.playAudioUrl = null;
                                        destroy();
                                        r1.firstFrameRendered = false;
                                        r1.currentAlpha = 1.0f;
                                        if (r1.currentTask != null) {
                                            r1.currentTask.cancel(true);
                                            r1.currentTask = null;
                                        }
                                        updateFullscreenButton();
                                        updateShareButton();
                                        updateInlineButton();
                                        updatePlayButton();
                                        if (photo2 != null) {
                                            closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
                                            if (closestPhotoSizeWithSize != null) {
                                                if (photo2 != null) {
                                                }
                                                if (photo2 != null) {
                                                }
                                                r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                                                r1.drawImage = true;
                                            }
                                        } else {
                                            r1.drawImage = false;
                                        }
                                        if (r1.progressAnimation != null) {
                                            r1.progressAnimation.cancel();
                                            r1.progressAnimation = null;
                                        }
                                        r1.isLoading = true;
                                        r1.controlsView.setProgress(0);
                                        r1.currentYoutubeId = str3;
                                        str3 = null;
                                        if (str4 != null) {
                                            r1.initied = true;
                                            r1.playVideoUrl = str4;
                                            r1.playVideoType = "other";
                                            if (r1.isAutoplay) {
                                                preparePlayer();
                                            }
                                            showProgress(false, false);
                                            r1.controlsView.show(true, true);
                                        } else {
                                            if (str3 != null) {
                                                youtubeVideoTask = new YoutubeVideoTask(str3);
                                                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                r1.currentTask = youtubeVideoTask;
                                            } else if (str5 != null) {
                                                youtubeVideoTask = new VimeoVideoTask(str5);
                                                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                r1.currentTask = youtubeVideoTask;
                                            } else if (group3 != null) {
                                                youtubeVideoTask = new CoubVideoTask(group3);
                                                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                r1.currentTask = youtubeVideoTask;
                                                r1.isStream = true;
                                            } else if (group != null) {
                                                youtubeVideoTask = new AparatVideoTask(group);
                                                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                r1.currentTask = youtubeVideoTask;
                                            } else if (group2 != null) {
                                                twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                                                twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                r1.currentTask = twitchClipVideoTask;
                                            } else if (str6 != null) {
                                                twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                                                twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                r1.currentTask = twitchClipVideoTask;
                                                r1.isStream = true;
                                            }
                                            r1.controlsView.show(false, false);
                                            showProgress(true, false);
                                        }
                                        if (str6 == null) {
                                            r1.controlsView.setVisibility(0);
                                            return true;
                                        }
                                        r1.controlsView.setVisibility(8);
                                        return false;
                                    }
                                    group3 = null;
                                    str6 = group2;
                                    group2 = group;
                                    group = str5;
                                    str5 = str4;
                                    str4 = group3;
                                    r1.initied = false;
                                    r1.isCompleted = false;
                                    r1.isAutoplay = z;
                                    r1.playVideoUrl = null;
                                    r1.playAudioUrl = null;
                                    destroy();
                                    r1.firstFrameRendered = false;
                                    r1.currentAlpha = 1.0f;
                                    if (r1.currentTask != null) {
                                        r1.currentTask.cancel(true);
                                        r1.currentTask = null;
                                    }
                                    updateFullscreenButton();
                                    updateShareButton();
                                    updateInlineButton();
                                    updatePlayButton();
                                    if (photo2 != null) {
                                        r1.drawImage = false;
                                    } else {
                                        closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
                                        if (closestPhotoSizeWithSize != null) {
                                            if (photo2 != null) {
                                            }
                                            if (photo2 != null) {
                                            }
                                            r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                                            r1.drawImage = true;
                                        }
                                    }
                                    if (r1.progressAnimation != null) {
                                        r1.progressAnimation.cancel();
                                        r1.progressAnimation = null;
                                    }
                                    r1.isLoading = true;
                                    r1.controlsView.setProgress(0);
                                    r1.currentYoutubeId = str3;
                                    str3 = null;
                                    if (str4 != null) {
                                        if (str3 != null) {
                                            youtubeVideoTask = new YoutubeVideoTask(str3);
                                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                            r1.currentTask = youtubeVideoTask;
                                        } else if (str5 != null) {
                                            youtubeVideoTask = new VimeoVideoTask(str5);
                                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                            r1.currentTask = youtubeVideoTask;
                                        } else if (group3 != null) {
                                            youtubeVideoTask = new CoubVideoTask(group3);
                                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                            r1.currentTask = youtubeVideoTask;
                                            r1.isStream = true;
                                        } else if (group != null) {
                                            youtubeVideoTask = new AparatVideoTask(group);
                                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                            r1.currentTask = youtubeVideoTask;
                                        } else if (group2 != null) {
                                            twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                                            twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                            r1.currentTask = twitchClipVideoTask;
                                        } else if (str6 != null) {
                                            twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                                            twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                            r1.currentTask = twitchClipVideoTask;
                                            r1.isStream = true;
                                        }
                                        r1.controlsView.show(false, false);
                                        showProgress(true, false);
                                    } else {
                                        r1.initied = true;
                                        r1.playVideoUrl = str4;
                                        r1.playVideoType = "other";
                                        if (r1.isAutoplay) {
                                            preparePlayer();
                                        }
                                        showProgress(false, false);
                                        r1.controlsView.show(true, true);
                                    }
                                    if (str6 == null) {
                                        r1.controlsView.setVisibility(8);
                                        return false;
                                    }
                                    r1.controlsView.setVisibility(0);
                                    return true;
                                }
                            }
                            group2 = null;
                            if (group2 == null) {
                                matcher5 = coubIdRegex.matcher(str7);
                                if (matcher5.find()) {
                                }
                                if (group3 == null) {
                                    group3 = null;
                                }
                                str6 = group2;
                                group2 = group;
                                group = str5;
                                str5 = str4;
                                str4 = null;
                                r1.initied = false;
                                r1.isCompleted = false;
                                r1.isAutoplay = z;
                                r1.playVideoUrl = null;
                                r1.playAudioUrl = null;
                                destroy();
                                r1.firstFrameRendered = false;
                                r1.currentAlpha = 1.0f;
                                if (r1.currentTask != null) {
                                    r1.currentTask.cancel(true);
                                    r1.currentTask = null;
                                }
                                updateFullscreenButton();
                                updateShareButton();
                                updateInlineButton();
                                updatePlayButton();
                                if (photo2 != null) {
                                    closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
                                    if (closestPhotoSizeWithSize != null) {
                                        if (photo2 != null) {
                                        }
                                        if (photo2 != null) {
                                        }
                                        r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                                        r1.drawImage = true;
                                    }
                                } else {
                                    r1.drawImage = false;
                                }
                                if (r1.progressAnimation != null) {
                                    r1.progressAnimation.cancel();
                                    r1.progressAnimation = null;
                                }
                                r1.isLoading = true;
                                r1.controlsView.setProgress(0);
                                r1.currentYoutubeId = str3;
                                str3 = null;
                                if (str4 != null) {
                                    r1.initied = true;
                                    r1.playVideoUrl = str4;
                                    r1.playVideoType = "other";
                                    if (r1.isAutoplay) {
                                        preparePlayer();
                                    }
                                    showProgress(false, false);
                                    r1.controlsView.show(true, true);
                                } else {
                                    if (str3 != null) {
                                        youtubeVideoTask = new YoutubeVideoTask(str3);
                                        youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                        r1.currentTask = youtubeVideoTask;
                                    } else if (str5 != null) {
                                        youtubeVideoTask = new VimeoVideoTask(str5);
                                        youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                        r1.currentTask = youtubeVideoTask;
                                    } else if (group3 != null) {
                                        youtubeVideoTask = new CoubVideoTask(group3);
                                        youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                        r1.currentTask = youtubeVideoTask;
                                        r1.isStream = true;
                                    } else if (group != null) {
                                        youtubeVideoTask = new AparatVideoTask(group);
                                        youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                        r1.currentTask = youtubeVideoTask;
                                    } else if (group2 != null) {
                                        twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                                        twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                        r1.currentTask = twitchClipVideoTask;
                                    } else if (str6 != null) {
                                        twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                                        twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                        r1.currentTask = twitchClipVideoTask;
                                        r1.isStream = true;
                                    }
                                    r1.controlsView.show(false, false);
                                    showProgress(true, false);
                                }
                                if (str6 == null) {
                                    r1.controlsView.setVisibility(0);
                                    return true;
                                }
                                r1.controlsView.setVisibility(8);
                                return false;
                            }
                            group3 = null;
                            str6 = group2;
                            group2 = group;
                            group = str5;
                            str5 = str4;
                            str4 = group3;
                            r1.initied = false;
                            r1.isCompleted = false;
                            r1.isAutoplay = z;
                            r1.playVideoUrl = null;
                            r1.playAudioUrl = null;
                            destroy();
                            r1.firstFrameRendered = false;
                            r1.currentAlpha = 1.0f;
                            if (r1.currentTask != null) {
                                r1.currentTask.cancel(true);
                                r1.currentTask = null;
                            }
                            updateFullscreenButton();
                            updateShareButton();
                            updateInlineButton();
                            updatePlayButton();
                            if (photo2 != null) {
                                r1.drawImage = false;
                            } else {
                                closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
                                if (closestPhotoSizeWithSize != null) {
                                    if (photo2 != null) {
                                    }
                                    if (photo2 != null) {
                                    }
                                    r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                                    r1.drawImage = true;
                                }
                            }
                            if (r1.progressAnimation != null) {
                                r1.progressAnimation.cancel();
                                r1.progressAnimation = null;
                            }
                            r1.isLoading = true;
                            r1.controlsView.setProgress(0);
                            r1.currentYoutubeId = str3;
                            str3 = null;
                            if (str4 != null) {
                                if (str3 != null) {
                                    youtubeVideoTask = new YoutubeVideoTask(str3);
                                    youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = youtubeVideoTask;
                                } else if (str5 != null) {
                                    youtubeVideoTask = new VimeoVideoTask(str5);
                                    youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = youtubeVideoTask;
                                } else if (group3 != null) {
                                    youtubeVideoTask = new CoubVideoTask(group3);
                                    youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = youtubeVideoTask;
                                    r1.isStream = true;
                                } else if (group != null) {
                                    youtubeVideoTask = new AparatVideoTask(group);
                                    youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = youtubeVideoTask;
                                } else if (group2 != null) {
                                    twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                                    twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = twitchClipVideoTask;
                                } else if (str6 != null) {
                                    twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                                    twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = twitchClipVideoTask;
                                    r1.isStream = true;
                                }
                                r1.controlsView.show(false, false);
                                showProgress(true, false);
                            } else {
                                r1.initied = true;
                                r1.playVideoUrl = str4;
                                r1.playVideoType = "other";
                                if (r1.isAutoplay) {
                                    preparePlayer();
                                }
                                showProgress(false, false);
                                r1.controlsView.show(true, true);
                            }
                            if (str6 == null) {
                                r1.controlsView.setVisibility(8);
                                return false;
                            }
                            r1.controlsView.setVisibility(0);
                            return true;
                        }
                    }
                    str5 = null;
                    if (str5 == null) {
                        matcher3 = twitchClipIdRegex.matcher(str7);
                        if (matcher3.find()) {
                        }
                        if (group != null) {
                            if (group == null) {
                                matcher4 = twitchStreamIdRegex.matcher(str7);
                                if (matcher4.find()) {
                                }
                                if (group2 != null) {
                                    if (group2 == null) {
                                        matcher5 = coubIdRegex.matcher(str7);
                                        if (matcher5.find()) {
                                        }
                                        if (group3 == null) {
                                            group3 = null;
                                        }
                                        str6 = group2;
                                        group2 = group;
                                        group = str5;
                                        str5 = str4;
                                        str4 = null;
                                        r1.initied = false;
                                        r1.isCompleted = false;
                                        r1.isAutoplay = z;
                                        r1.playVideoUrl = null;
                                        r1.playAudioUrl = null;
                                        destroy();
                                        r1.firstFrameRendered = false;
                                        r1.currentAlpha = 1.0f;
                                        if (r1.currentTask != null) {
                                            r1.currentTask.cancel(true);
                                            r1.currentTask = null;
                                        }
                                        updateFullscreenButton();
                                        updateShareButton();
                                        updateInlineButton();
                                        updatePlayButton();
                                        if (photo2 != null) {
                                            closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
                                            if (closestPhotoSizeWithSize != null) {
                                                if (photo2 != null) {
                                                }
                                                if (photo2 != null) {
                                                }
                                                r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                                                r1.drawImage = true;
                                            }
                                        } else {
                                            r1.drawImage = false;
                                        }
                                        if (r1.progressAnimation != null) {
                                            r1.progressAnimation.cancel();
                                            r1.progressAnimation = null;
                                        }
                                        r1.isLoading = true;
                                        r1.controlsView.setProgress(0);
                                        r1.currentYoutubeId = str3;
                                        str3 = null;
                                        if (str4 != null) {
                                            r1.initied = true;
                                            r1.playVideoUrl = str4;
                                            r1.playVideoType = "other";
                                            if (r1.isAutoplay) {
                                                preparePlayer();
                                            }
                                            showProgress(false, false);
                                            r1.controlsView.show(true, true);
                                        } else {
                                            if (str3 != null) {
                                                youtubeVideoTask = new YoutubeVideoTask(str3);
                                                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                r1.currentTask = youtubeVideoTask;
                                            } else if (str5 != null) {
                                                youtubeVideoTask = new VimeoVideoTask(str5);
                                                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                r1.currentTask = youtubeVideoTask;
                                            } else if (group3 != null) {
                                                youtubeVideoTask = new CoubVideoTask(group3);
                                                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                r1.currentTask = youtubeVideoTask;
                                                r1.isStream = true;
                                            } else if (group != null) {
                                                youtubeVideoTask = new AparatVideoTask(group);
                                                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                r1.currentTask = youtubeVideoTask;
                                            } else if (group2 != null) {
                                                twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                                                twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                r1.currentTask = twitchClipVideoTask;
                                            } else if (str6 != null) {
                                                twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                                                twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                r1.currentTask = twitchClipVideoTask;
                                                r1.isStream = true;
                                            }
                                            r1.controlsView.show(false, false);
                                            showProgress(true, false);
                                        }
                                        if (str6 == null) {
                                            r1.controlsView.setVisibility(0);
                                            return true;
                                        }
                                        r1.controlsView.setVisibility(8);
                                        return false;
                                    }
                                    group3 = null;
                                    str6 = group2;
                                    group2 = group;
                                    group = str5;
                                    str5 = str4;
                                    str4 = group3;
                                    r1.initied = false;
                                    r1.isCompleted = false;
                                    r1.isAutoplay = z;
                                    r1.playVideoUrl = null;
                                    r1.playAudioUrl = null;
                                    destroy();
                                    r1.firstFrameRendered = false;
                                    r1.currentAlpha = 1.0f;
                                    if (r1.currentTask != null) {
                                        r1.currentTask.cancel(true);
                                        r1.currentTask = null;
                                    }
                                    updateFullscreenButton();
                                    updateShareButton();
                                    updateInlineButton();
                                    updatePlayButton();
                                    if (photo2 != null) {
                                        r1.drawImage = false;
                                    } else {
                                        closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
                                        if (closestPhotoSizeWithSize != null) {
                                            if (photo2 != null) {
                                            }
                                            if (photo2 != null) {
                                            }
                                            r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                                            r1.drawImage = true;
                                        }
                                    }
                                    if (r1.progressAnimation != null) {
                                        r1.progressAnimation.cancel();
                                        r1.progressAnimation = null;
                                    }
                                    r1.isLoading = true;
                                    r1.controlsView.setProgress(0);
                                    r1.currentYoutubeId = str3;
                                    str3 = null;
                                    if (str4 != null) {
                                        if (str3 != null) {
                                            youtubeVideoTask = new YoutubeVideoTask(str3);
                                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                            r1.currentTask = youtubeVideoTask;
                                        } else if (str5 != null) {
                                            youtubeVideoTask = new VimeoVideoTask(str5);
                                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                            r1.currentTask = youtubeVideoTask;
                                        } else if (group3 != null) {
                                            youtubeVideoTask = new CoubVideoTask(group3);
                                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                            r1.currentTask = youtubeVideoTask;
                                            r1.isStream = true;
                                        } else if (group != null) {
                                            youtubeVideoTask = new AparatVideoTask(group);
                                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                            r1.currentTask = youtubeVideoTask;
                                        } else if (group2 != null) {
                                            twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                                            twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                            r1.currentTask = twitchClipVideoTask;
                                        } else if (str6 != null) {
                                            twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                                            twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                            r1.currentTask = twitchClipVideoTask;
                                            r1.isStream = true;
                                        }
                                        r1.controlsView.show(false, false);
                                        showProgress(true, false);
                                    } else {
                                        r1.initied = true;
                                        r1.playVideoUrl = str4;
                                        r1.playVideoType = "other";
                                        if (r1.isAutoplay) {
                                            preparePlayer();
                                        }
                                        showProgress(false, false);
                                        r1.controlsView.show(true, true);
                                    }
                                    if (str6 == null) {
                                        r1.controlsView.setVisibility(8);
                                        return false;
                                    }
                                    r1.controlsView.setVisibility(0);
                                    return true;
                                }
                            }
                            group2 = null;
                            if (group2 == null) {
                                matcher5 = coubIdRegex.matcher(str7);
                                if (matcher5.find()) {
                                }
                                if (group3 == null) {
                                    group3 = null;
                                }
                                str6 = group2;
                                group2 = group;
                                group = str5;
                                str5 = str4;
                                str4 = null;
                                r1.initied = false;
                                r1.isCompleted = false;
                                r1.isAutoplay = z;
                                r1.playVideoUrl = null;
                                r1.playAudioUrl = null;
                                destroy();
                                r1.firstFrameRendered = false;
                                r1.currentAlpha = 1.0f;
                                if (r1.currentTask != null) {
                                    r1.currentTask.cancel(true);
                                    r1.currentTask = null;
                                }
                                updateFullscreenButton();
                                updateShareButton();
                                updateInlineButton();
                                updatePlayButton();
                                if (photo2 != null) {
                                    closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
                                    if (closestPhotoSizeWithSize != null) {
                                        if (photo2 != null) {
                                        }
                                        if (photo2 != null) {
                                        }
                                        r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                                        r1.drawImage = true;
                                    }
                                } else {
                                    r1.drawImage = false;
                                }
                                if (r1.progressAnimation != null) {
                                    r1.progressAnimation.cancel();
                                    r1.progressAnimation = null;
                                }
                                r1.isLoading = true;
                                r1.controlsView.setProgress(0);
                                r1.currentYoutubeId = str3;
                                str3 = null;
                                if (str4 != null) {
                                    r1.initied = true;
                                    r1.playVideoUrl = str4;
                                    r1.playVideoType = "other";
                                    if (r1.isAutoplay) {
                                        preparePlayer();
                                    }
                                    showProgress(false, false);
                                    r1.controlsView.show(true, true);
                                } else {
                                    if (str3 != null) {
                                        youtubeVideoTask = new YoutubeVideoTask(str3);
                                        youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                        r1.currentTask = youtubeVideoTask;
                                    } else if (str5 != null) {
                                        youtubeVideoTask = new VimeoVideoTask(str5);
                                        youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                        r1.currentTask = youtubeVideoTask;
                                    } else if (group3 != null) {
                                        youtubeVideoTask = new CoubVideoTask(group3);
                                        youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                        r1.currentTask = youtubeVideoTask;
                                        r1.isStream = true;
                                    } else if (group != null) {
                                        youtubeVideoTask = new AparatVideoTask(group);
                                        youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                        r1.currentTask = youtubeVideoTask;
                                    } else if (group2 != null) {
                                        twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                                        twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                        r1.currentTask = twitchClipVideoTask;
                                    } else if (str6 != null) {
                                        twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                                        twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                        r1.currentTask = twitchClipVideoTask;
                                        r1.isStream = true;
                                    }
                                    r1.controlsView.show(false, false);
                                    showProgress(true, false);
                                }
                                if (str6 == null) {
                                    r1.controlsView.setVisibility(0);
                                    return true;
                                }
                                r1.controlsView.setVisibility(8);
                                return false;
                            }
                            group3 = null;
                            str6 = group2;
                            group2 = group;
                            group = str5;
                            str5 = str4;
                            str4 = group3;
                            r1.initied = false;
                            r1.isCompleted = false;
                            r1.isAutoplay = z;
                            r1.playVideoUrl = null;
                            r1.playAudioUrl = null;
                            destroy();
                            r1.firstFrameRendered = false;
                            r1.currentAlpha = 1.0f;
                            if (r1.currentTask != null) {
                                r1.currentTask.cancel(true);
                                r1.currentTask = null;
                            }
                            updateFullscreenButton();
                            updateShareButton();
                            updateInlineButton();
                            updatePlayButton();
                            if (photo2 != null) {
                                r1.drawImage = false;
                            } else {
                                closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
                                if (closestPhotoSizeWithSize != null) {
                                    if (photo2 != null) {
                                    }
                                    if (photo2 != null) {
                                    }
                                    r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                                    r1.drawImage = true;
                                }
                            }
                            if (r1.progressAnimation != null) {
                                r1.progressAnimation.cancel();
                                r1.progressAnimation = null;
                            }
                            r1.isLoading = true;
                            r1.controlsView.setProgress(0);
                            r1.currentYoutubeId = str3;
                            str3 = null;
                            if (str4 != null) {
                                if (str3 != null) {
                                    youtubeVideoTask = new YoutubeVideoTask(str3);
                                    youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = youtubeVideoTask;
                                } else if (str5 != null) {
                                    youtubeVideoTask = new VimeoVideoTask(str5);
                                    youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = youtubeVideoTask;
                                } else if (group3 != null) {
                                    youtubeVideoTask = new CoubVideoTask(group3);
                                    youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = youtubeVideoTask;
                                    r1.isStream = true;
                                } else if (group != null) {
                                    youtubeVideoTask = new AparatVideoTask(group);
                                    youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = youtubeVideoTask;
                                } else if (group2 != null) {
                                    twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                                    twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = twitchClipVideoTask;
                                } else if (str6 != null) {
                                    twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                                    twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = twitchClipVideoTask;
                                    r1.isStream = true;
                                }
                                r1.controlsView.show(false, false);
                                showProgress(true, false);
                            } else {
                                r1.initied = true;
                                r1.playVideoUrl = str4;
                                r1.playVideoType = "other";
                                if (r1.isAutoplay) {
                                    preparePlayer();
                                }
                                showProgress(false, false);
                                r1.controlsView.show(true, true);
                            }
                            if (str6 == null) {
                                r1.controlsView.setVisibility(8);
                                return false;
                            }
                            r1.controlsView.setVisibility(0);
                            return true;
                        }
                    }
                    group = null;
                    if (group == null) {
                        matcher4 = twitchStreamIdRegex.matcher(str7);
                        if (matcher4.find()) {
                        }
                        if (group2 != null) {
                            if (group2 == null) {
                                matcher5 = coubIdRegex.matcher(str7);
                                if (matcher5.find()) {
                                }
                                if (group3 == null) {
                                    group3 = null;
                                }
                                str6 = group2;
                                group2 = group;
                                group = str5;
                                str5 = str4;
                                str4 = null;
                                r1.initied = false;
                                r1.isCompleted = false;
                                r1.isAutoplay = z;
                                r1.playVideoUrl = null;
                                r1.playAudioUrl = null;
                                destroy();
                                r1.firstFrameRendered = false;
                                r1.currentAlpha = 1.0f;
                                if (r1.currentTask != null) {
                                    r1.currentTask.cancel(true);
                                    r1.currentTask = null;
                                }
                                updateFullscreenButton();
                                updateShareButton();
                                updateInlineButton();
                                updatePlayButton();
                                if (photo2 != null) {
                                    closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
                                    if (closestPhotoSizeWithSize != null) {
                                        if (photo2 != null) {
                                        }
                                        if (photo2 != null) {
                                        }
                                        r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                                        r1.drawImage = true;
                                    }
                                } else {
                                    r1.drawImage = false;
                                }
                                if (r1.progressAnimation != null) {
                                    r1.progressAnimation.cancel();
                                    r1.progressAnimation = null;
                                }
                                r1.isLoading = true;
                                r1.controlsView.setProgress(0);
                                r1.currentYoutubeId = str3;
                                str3 = null;
                                if (str4 != null) {
                                    r1.initied = true;
                                    r1.playVideoUrl = str4;
                                    r1.playVideoType = "other";
                                    if (r1.isAutoplay) {
                                        preparePlayer();
                                    }
                                    showProgress(false, false);
                                    r1.controlsView.show(true, true);
                                } else {
                                    if (str3 != null) {
                                        youtubeVideoTask = new YoutubeVideoTask(str3);
                                        youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                        r1.currentTask = youtubeVideoTask;
                                    } else if (str5 != null) {
                                        youtubeVideoTask = new VimeoVideoTask(str5);
                                        youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                        r1.currentTask = youtubeVideoTask;
                                    } else if (group3 != null) {
                                        youtubeVideoTask = new CoubVideoTask(group3);
                                        youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                        r1.currentTask = youtubeVideoTask;
                                        r1.isStream = true;
                                    } else if (group != null) {
                                        youtubeVideoTask = new AparatVideoTask(group);
                                        youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                        r1.currentTask = youtubeVideoTask;
                                    } else if (group2 != null) {
                                        twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                                        twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                        r1.currentTask = twitchClipVideoTask;
                                    } else if (str6 != null) {
                                        twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                                        twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                        r1.currentTask = twitchClipVideoTask;
                                        r1.isStream = true;
                                    }
                                    r1.controlsView.show(false, false);
                                    showProgress(true, false);
                                }
                                if (str6 == null) {
                                    r1.controlsView.setVisibility(0);
                                    return true;
                                }
                                r1.controlsView.setVisibility(8);
                                return false;
                            }
                            group3 = null;
                            str6 = group2;
                            group2 = group;
                            group = str5;
                            str5 = str4;
                            str4 = group3;
                            r1.initied = false;
                            r1.isCompleted = false;
                            r1.isAutoplay = z;
                            r1.playVideoUrl = null;
                            r1.playAudioUrl = null;
                            destroy();
                            r1.firstFrameRendered = false;
                            r1.currentAlpha = 1.0f;
                            if (r1.currentTask != null) {
                                r1.currentTask.cancel(true);
                                r1.currentTask = null;
                            }
                            updateFullscreenButton();
                            updateShareButton();
                            updateInlineButton();
                            updatePlayButton();
                            if (photo2 != null) {
                                r1.drawImage = false;
                            } else {
                                closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
                                if (closestPhotoSizeWithSize != null) {
                                    if (photo2 != null) {
                                    }
                                    if (photo2 != null) {
                                    }
                                    r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                                    r1.drawImage = true;
                                }
                            }
                            if (r1.progressAnimation != null) {
                                r1.progressAnimation.cancel();
                                r1.progressAnimation = null;
                            }
                            r1.isLoading = true;
                            r1.controlsView.setProgress(0);
                            r1.currentYoutubeId = str3;
                            str3 = null;
                            if (str4 != null) {
                                if (str3 != null) {
                                    youtubeVideoTask = new YoutubeVideoTask(str3);
                                    youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = youtubeVideoTask;
                                } else if (str5 != null) {
                                    youtubeVideoTask = new VimeoVideoTask(str5);
                                    youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = youtubeVideoTask;
                                } else if (group3 != null) {
                                    youtubeVideoTask = new CoubVideoTask(group3);
                                    youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = youtubeVideoTask;
                                    r1.isStream = true;
                                } else if (group != null) {
                                    youtubeVideoTask = new AparatVideoTask(group);
                                    youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = youtubeVideoTask;
                                } else if (group2 != null) {
                                    twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                                    twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = twitchClipVideoTask;
                                } else if (str6 != null) {
                                    twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                                    twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = twitchClipVideoTask;
                                    r1.isStream = true;
                                }
                                r1.controlsView.show(false, false);
                                showProgress(true, false);
                            } else {
                                r1.initied = true;
                                r1.playVideoUrl = str4;
                                r1.playVideoType = "other";
                                if (r1.isAutoplay) {
                                    preparePlayer();
                                }
                                showProgress(false, false);
                                r1.controlsView.show(true, true);
                            }
                            if (str6 == null) {
                                r1.controlsView.setVisibility(8);
                                return false;
                            }
                            r1.controlsView.setVisibility(0);
                            return true;
                        }
                    }
                    group2 = null;
                    if (group2 == null) {
                        matcher5 = coubIdRegex.matcher(str7);
                        if (matcher5.find()) {
                        }
                        if (group3 == null) {
                            group3 = null;
                        }
                        str6 = group2;
                        group2 = group;
                        group = str5;
                        str5 = str4;
                        str4 = null;
                        r1.initied = false;
                        r1.isCompleted = false;
                        r1.isAutoplay = z;
                        r1.playVideoUrl = null;
                        r1.playAudioUrl = null;
                        destroy();
                        r1.firstFrameRendered = false;
                        r1.currentAlpha = 1.0f;
                        if (r1.currentTask != null) {
                            r1.currentTask.cancel(true);
                            r1.currentTask = null;
                        }
                        updateFullscreenButton();
                        updateShareButton();
                        updateInlineButton();
                        updatePlayButton();
                        if (photo2 != null) {
                            closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
                            if (closestPhotoSizeWithSize != null) {
                                if (photo2 != null) {
                                }
                                if (photo2 != null) {
                                }
                                r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                                r1.drawImage = true;
                            }
                        } else {
                            r1.drawImage = false;
                        }
                        if (r1.progressAnimation != null) {
                            r1.progressAnimation.cancel();
                            r1.progressAnimation = null;
                        }
                        r1.isLoading = true;
                        r1.controlsView.setProgress(0);
                        r1.currentYoutubeId = str3;
                        str3 = null;
                        if (str4 != null) {
                            r1.initied = true;
                            r1.playVideoUrl = str4;
                            r1.playVideoType = "other";
                            if (r1.isAutoplay) {
                                preparePlayer();
                            }
                            showProgress(false, false);
                            r1.controlsView.show(true, true);
                        } else {
                            if (str3 != null) {
                                youtubeVideoTask = new YoutubeVideoTask(str3);
                                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                r1.currentTask = youtubeVideoTask;
                            } else if (str5 != null) {
                                youtubeVideoTask = new VimeoVideoTask(str5);
                                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                r1.currentTask = youtubeVideoTask;
                            } else if (group3 != null) {
                                youtubeVideoTask = new CoubVideoTask(group3);
                                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                r1.currentTask = youtubeVideoTask;
                                r1.isStream = true;
                            } else if (group != null) {
                                youtubeVideoTask = new AparatVideoTask(group);
                                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                r1.currentTask = youtubeVideoTask;
                            } else if (group2 != null) {
                                twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                                twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                r1.currentTask = twitchClipVideoTask;
                            } else if (str6 != null) {
                                twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                                twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                r1.currentTask = twitchClipVideoTask;
                                r1.isStream = true;
                            }
                            r1.controlsView.show(false, false);
                            showProgress(true, false);
                        }
                        if (str6 == null) {
                            r1.controlsView.setVisibility(0);
                            return true;
                        }
                        r1.controlsView.setVisibility(8);
                        return false;
                    }
                    group3 = null;
                    str6 = group2;
                    group2 = group;
                    group = str5;
                    str5 = str4;
                    str4 = group3;
                    r1.initied = false;
                    r1.isCompleted = false;
                    r1.isAutoplay = z;
                    r1.playVideoUrl = null;
                    r1.playAudioUrl = null;
                    destroy();
                    r1.firstFrameRendered = false;
                    r1.currentAlpha = 1.0f;
                    if (r1.currentTask != null) {
                        r1.currentTask.cancel(true);
                        r1.currentTask = null;
                    }
                    updateFullscreenButton();
                    updateShareButton();
                    updateInlineButton();
                    updatePlayButton();
                    if (photo2 != null) {
                        r1.drawImage = false;
                    } else {
                        closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
                        if (closestPhotoSizeWithSize != null) {
                            if (photo2 != null) {
                            }
                            if (photo2 != null) {
                            }
                            r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                            r1.drawImage = true;
                        }
                    }
                    if (r1.progressAnimation != null) {
                        r1.progressAnimation.cancel();
                        r1.progressAnimation = null;
                    }
                    r1.isLoading = true;
                    r1.controlsView.setProgress(0);
                    r1.currentYoutubeId = str3;
                    str3 = null;
                    if (str4 != null) {
                        if (str3 != null) {
                            youtubeVideoTask = new YoutubeVideoTask(str3);
                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                            r1.currentTask = youtubeVideoTask;
                        } else if (str5 != null) {
                            youtubeVideoTask = new VimeoVideoTask(str5);
                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                            r1.currentTask = youtubeVideoTask;
                        } else if (group3 != null) {
                            youtubeVideoTask = new CoubVideoTask(group3);
                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                            r1.currentTask = youtubeVideoTask;
                            r1.isStream = true;
                        } else if (group != null) {
                            youtubeVideoTask = new AparatVideoTask(group);
                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                            r1.currentTask = youtubeVideoTask;
                        } else if (group2 != null) {
                            twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                            twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                            r1.currentTask = twitchClipVideoTask;
                        } else if (str6 != null) {
                            twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                            twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                            r1.currentTask = twitchClipVideoTask;
                            r1.isStream = true;
                        }
                        r1.controlsView.show(false, false);
                        showProgress(true, false);
                    } else {
                        r1.initied = true;
                        r1.playVideoUrl = str4;
                        r1.playVideoType = "other";
                        if (r1.isAutoplay) {
                            preparePlayer();
                        }
                        showProgress(false, false);
                        r1.controlsView.show(true, true);
                    }
                    if (str6 == null) {
                        r1.controlsView.setVisibility(8);
                        return false;
                    }
                    r1.controlsView.setVisibility(0);
                    return true;
                }
            }
            str4 = null;
            if (str4 == null) {
                matcher2 = aparatIdRegex.matcher(str7);
                if (matcher2.find()) {
                }
                if (str5 != null) {
                    if (str5 == null) {
                        matcher3 = twitchClipIdRegex.matcher(str7);
                        if (matcher3.find()) {
                        }
                        if (group != null) {
                            if (group == null) {
                                matcher4 = twitchStreamIdRegex.matcher(str7);
                                if (matcher4.find()) {
                                }
                                if (group2 != null) {
                                    if (group2 == null) {
                                        matcher5 = coubIdRegex.matcher(str7);
                                        if (matcher5.find()) {
                                        }
                                        if (group3 == null) {
                                            group3 = null;
                                        }
                                        str6 = group2;
                                        group2 = group;
                                        group = str5;
                                        str5 = str4;
                                        str4 = null;
                                        r1.initied = false;
                                        r1.isCompleted = false;
                                        r1.isAutoplay = z;
                                        r1.playVideoUrl = null;
                                        r1.playAudioUrl = null;
                                        destroy();
                                        r1.firstFrameRendered = false;
                                        r1.currentAlpha = 1.0f;
                                        if (r1.currentTask != null) {
                                            r1.currentTask.cancel(true);
                                            r1.currentTask = null;
                                        }
                                        updateFullscreenButton();
                                        updateShareButton();
                                        updateInlineButton();
                                        updatePlayButton();
                                        if (photo2 != null) {
                                            closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
                                            if (closestPhotoSizeWithSize != null) {
                                                if (photo2 != null) {
                                                }
                                                if (photo2 != null) {
                                                }
                                                r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                                                r1.drawImage = true;
                                            }
                                        } else {
                                            r1.drawImage = false;
                                        }
                                        if (r1.progressAnimation != null) {
                                            r1.progressAnimation.cancel();
                                            r1.progressAnimation = null;
                                        }
                                        r1.isLoading = true;
                                        r1.controlsView.setProgress(0);
                                        r1.currentYoutubeId = str3;
                                        str3 = null;
                                        if (str4 != null) {
                                            r1.initied = true;
                                            r1.playVideoUrl = str4;
                                            r1.playVideoType = "other";
                                            if (r1.isAutoplay) {
                                                preparePlayer();
                                            }
                                            showProgress(false, false);
                                            r1.controlsView.show(true, true);
                                        } else {
                                            if (str3 != null) {
                                                youtubeVideoTask = new YoutubeVideoTask(str3);
                                                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                r1.currentTask = youtubeVideoTask;
                                            } else if (str5 != null) {
                                                youtubeVideoTask = new VimeoVideoTask(str5);
                                                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                r1.currentTask = youtubeVideoTask;
                                            } else if (group3 != null) {
                                                youtubeVideoTask = new CoubVideoTask(group3);
                                                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                r1.currentTask = youtubeVideoTask;
                                                r1.isStream = true;
                                            } else if (group != null) {
                                                youtubeVideoTask = new AparatVideoTask(group);
                                                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                r1.currentTask = youtubeVideoTask;
                                            } else if (group2 != null) {
                                                twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                                                twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                r1.currentTask = twitchClipVideoTask;
                                            } else if (str6 != null) {
                                                twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                                                twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                                r1.currentTask = twitchClipVideoTask;
                                                r1.isStream = true;
                                            }
                                            r1.controlsView.show(false, false);
                                            showProgress(true, false);
                                        }
                                        if (str6 == null) {
                                            r1.controlsView.setVisibility(0);
                                            return true;
                                        }
                                        r1.controlsView.setVisibility(8);
                                        return false;
                                    }
                                    group3 = null;
                                    str6 = group2;
                                    group2 = group;
                                    group = str5;
                                    str5 = str4;
                                    str4 = group3;
                                    r1.initied = false;
                                    r1.isCompleted = false;
                                    r1.isAutoplay = z;
                                    r1.playVideoUrl = null;
                                    r1.playAudioUrl = null;
                                    destroy();
                                    r1.firstFrameRendered = false;
                                    r1.currentAlpha = 1.0f;
                                    if (r1.currentTask != null) {
                                        r1.currentTask.cancel(true);
                                        r1.currentTask = null;
                                    }
                                    updateFullscreenButton();
                                    updateShareButton();
                                    updateInlineButton();
                                    updatePlayButton();
                                    if (photo2 != null) {
                                        r1.drawImage = false;
                                    } else {
                                        closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
                                        if (closestPhotoSizeWithSize != null) {
                                            if (photo2 != null) {
                                            }
                                            if (photo2 != null) {
                                            }
                                            r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                                            r1.drawImage = true;
                                        }
                                    }
                                    if (r1.progressAnimation != null) {
                                        r1.progressAnimation.cancel();
                                        r1.progressAnimation = null;
                                    }
                                    r1.isLoading = true;
                                    r1.controlsView.setProgress(0);
                                    r1.currentYoutubeId = str3;
                                    str3 = null;
                                    if (str4 != null) {
                                        if (str3 != null) {
                                            youtubeVideoTask = new YoutubeVideoTask(str3);
                                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                            r1.currentTask = youtubeVideoTask;
                                        } else if (str5 != null) {
                                            youtubeVideoTask = new VimeoVideoTask(str5);
                                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                            r1.currentTask = youtubeVideoTask;
                                        } else if (group3 != null) {
                                            youtubeVideoTask = new CoubVideoTask(group3);
                                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                            r1.currentTask = youtubeVideoTask;
                                            r1.isStream = true;
                                        } else if (group != null) {
                                            youtubeVideoTask = new AparatVideoTask(group);
                                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                            r1.currentTask = youtubeVideoTask;
                                        } else if (group2 != null) {
                                            twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                                            twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                            r1.currentTask = twitchClipVideoTask;
                                        } else if (str6 != null) {
                                            twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                                            twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                            r1.currentTask = twitchClipVideoTask;
                                            r1.isStream = true;
                                        }
                                        r1.controlsView.show(false, false);
                                        showProgress(true, false);
                                    } else {
                                        r1.initied = true;
                                        r1.playVideoUrl = str4;
                                        r1.playVideoType = "other";
                                        if (r1.isAutoplay) {
                                            preparePlayer();
                                        }
                                        showProgress(false, false);
                                        r1.controlsView.show(true, true);
                                    }
                                    if (str6 == null) {
                                        r1.controlsView.setVisibility(8);
                                        return false;
                                    }
                                    r1.controlsView.setVisibility(0);
                                    return true;
                                }
                            }
                            group2 = null;
                            if (group2 == null) {
                                matcher5 = coubIdRegex.matcher(str7);
                                if (matcher5.find()) {
                                }
                                if (group3 == null) {
                                    group3 = null;
                                }
                                str6 = group2;
                                group2 = group;
                                group = str5;
                                str5 = str4;
                                str4 = null;
                                r1.initied = false;
                                r1.isCompleted = false;
                                r1.isAutoplay = z;
                                r1.playVideoUrl = null;
                                r1.playAudioUrl = null;
                                destroy();
                                r1.firstFrameRendered = false;
                                r1.currentAlpha = 1.0f;
                                if (r1.currentTask != null) {
                                    r1.currentTask.cancel(true);
                                    r1.currentTask = null;
                                }
                                updateFullscreenButton();
                                updateShareButton();
                                updateInlineButton();
                                updatePlayButton();
                                if (photo2 != null) {
                                    closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
                                    if (closestPhotoSizeWithSize != null) {
                                        if (photo2 != null) {
                                        }
                                        if (photo2 != null) {
                                        }
                                        r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                                        r1.drawImage = true;
                                    }
                                } else {
                                    r1.drawImage = false;
                                }
                                if (r1.progressAnimation != null) {
                                    r1.progressAnimation.cancel();
                                    r1.progressAnimation = null;
                                }
                                r1.isLoading = true;
                                r1.controlsView.setProgress(0);
                                r1.currentYoutubeId = str3;
                                str3 = null;
                                if (str4 != null) {
                                    r1.initied = true;
                                    r1.playVideoUrl = str4;
                                    r1.playVideoType = "other";
                                    if (r1.isAutoplay) {
                                        preparePlayer();
                                    }
                                    showProgress(false, false);
                                    r1.controlsView.show(true, true);
                                } else {
                                    if (str3 != null) {
                                        youtubeVideoTask = new YoutubeVideoTask(str3);
                                        youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                        r1.currentTask = youtubeVideoTask;
                                    } else if (str5 != null) {
                                        youtubeVideoTask = new VimeoVideoTask(str5);
                                        youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                        r1.currentTask = youtubeVideoTask;
                                    } else if (group3 != null) {
                                        youtubeVideoTask = new CoubVideoTask(group3);
                                        youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                        r1.currentTask = youtubeVideoTask;
                                        r1.isStream = true;
                                    } else if (group != null) {
                                        youtubeVideoTask = new AparatVideoTask(group);
                                        youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                        r1.currentTask = youtubeVideoTask;
                                    } else if (group2 != null) {
                                        twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                                        twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                        r1.currentTask = twitchClipVideoTask;
                                    } else if (str6 != null) {
                                        twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                                        twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                        r1.currentTask = twitchClipVideoTask;
                                        r1.isStream = true;
                                    }
                                    r1.controlsView.show(false, false);
                                    showProgress(true, false);
                                }
                                if (str6 == null) {
                                    r1.controlsView.setVisibility(0);
                                    return true;
                                }
                                r1.controlsView.setVisibility(8);
                                return false;
                            }
                            group3 = null;
                            str6 = group2;
                            group2 = group;
                            group = str5;
                            str5 = str4;
                            str4 = group3;
                            r1.initied = false;
                            r1.isCompleted = false;
                            r1.isAutoplay = z;
                            r1.playVideoUrl = null;
                            r1.playAudioUrl = null;
                            destroy();
                            r1.firstFrameRendered = false;
                            r1.currentAlpha = 1.0f;
                            if (r1.currentTask != null) {
                                r1.currentTask.cancel(true);
                                r1.currentTask = null;
                            }
                            updateFullscreenButton();
                            updateShareButton();
                            updateInlineButton();
                            updatePlayButton();
                            if (photo2 != null) {
                                r1.drawImage = false;
                            } else {
                                closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
                                if (closestPhotoSizeWithSize != null) {
                                    if (photo2 != null) {
                                    }
                                    if (photo2 != null) {
                                    }
                                    r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                                    r1.drawImage = true;
                                }
                            }
                            if (r1.progressAnimation != null) {
                                r1.progressAnimation.cancel();
                                r1.progressAnimation = null;
                            }
                            r1.isLoading = true;
                            r1.controlsView.setProgress(0);
                            r1.currentYoutubeId = str3;
                            str3 = null;
                            if (str4 != null) {
                                if (str3 != null) {
                                    youtubeVideoTask = new YoutubeVideoTask(str3);
                                    youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = youtubeVideoTask;
                                } else if (str5 != null) {
                                    youtubeVideoTask = new VimeoVideoTask(str5);
                                    youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = youtubeVideoTask;
                                } else if (group3 != null) {
                                    youtubeVideoTask = new CoubVideoTask(group3);
                                    youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = youtubeVideoTask;
                                    r1.isStream = true;
                                } else if (group != null) {
                                    youtubeVideoTask = new AparatVideoTask(group);
                                    youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = youtubeVideoTask;
                                } else if (group2 != null) {
                                    twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                                    twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = twitchClipVideoTask;
                                } else if (str6 != null) {
                                    twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                                    twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = twitchClipVideoTask;
                                    r1.isStream = true;
                                }
                                r1.controlsView.show(false, false);
                                showProgress(true, false);
                            } else {
                                r1.initied = true;
                                r1.playVideoUrl = str4;
                                r1.playVideoType = "other";
                                if (r1.isAutoplay) {
                                    preparePlayer();
                                }
                                showProgress(false, false);
                                r1.controlsView.show(true, true);
                            }
                            if (str6 == null) {
                                r1.controlsView.setVisibility(8);
                                return false;
                            }
                            r1.controlsView.setVisibility(0);
                            return true;
                        }
                    }
                    group = null;
                    if (group == null) {
                        matcher4 = twitchStreamIdRegex.matcher(str7);
                        if (matcher4.find()) {
                        }
                        if (group2 != null) {
                            if (group2 == null) {
                                matcher5 = coubIdRegex.matcher(str7);
                                if (matcher5.find()) {
                                }
                                if (group3 == null) {
                                    group3 = null;
                                }
                                str6 = group2;
                                group2 = group;
                                group = str5;
                                str5 = str4;
                                str4 = null;
                                r1.initied = false;
                                r1.isCompleted = false;
                                r1.isAutoplay = z;
                                r1.playVideoUrl = null;
                                r1.playAudioUrl = null;
                                destroy();
                                r1.firstFrameRendered = false;
                                r1.currentAlpha = 1.0f;
                                if (r1.currentTask != null) {
                                    r1.currentTask.cancel(true);
                                    r1.currentTask = null;
                                }
                                updateFullscreenButton();
                                updateShareButton();
                                updateInlineButton();
                                updatePlayButton();
                                if (photo2 != null) {
                                    closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
                                    if (closestPhotoSizeWithSize != null) {
                                        if (photo2 != null) {
                                        }
                                        if (photo2 != null) {
                                        }
                                        r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                                        r1.drawImage = true;
                                    }
                                } else {
                                    r1.drawImage = false;
                                }
                                if (r1.progressAnimation != null) {
                                    r1.progressAnimation.cancel();
                                    r1.progressAnimation = null;
                                }
                                r1.isLoading = true;
                                r1.controlsView.setProgress(0);
                                r1.currentYoutubeId = str3;
                                str3 = null;
                                if (str4 != null) {
                                    r1.initied = true;
                                    r1.playVideoUrl = str4;
                                    r1.playVideoType = "other";
                                    if (r1.isAutoplay) {
                                        preparePlayer();
                                    }
                                    showProgress(false, false);
                                    r1.controlsView.show(true, true);
                                } else {
                                    if (str3 != null) {
                                        youtubeVideoTask = new YoutubeVideoTask(str3);
                                        youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                        r1.currentTask = youtubeVideoTask;
                                    } else if (str5 != null) {
                                        youtubeVideoTask = new VimeoVideoTask(str5);
                                        youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                        r1.currentTask = youtubeVideoTask;
                                    } else if (group3 != null) {
                                        youtubeVideoTask = new CoubVideoTask(group3);
                                        youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                        r1.currentTask = youtubeVideoTask;
                                        r1.isStream = true;
                                    } else if (group != null) {
                                        youtubeVideoTask = new AparatVideoTask(group);
                                        youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                        r1.currentTask = youtubeVideoTask;
                                    } else if (group2 != null) {
                                        twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                                        twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                        r1.currentTask = twitchClipVideoTask;
                                    } else if (str6 != null) {
                                        twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                                        twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                        r1.currentTask = twitchClipVideoTask;
                                        r1.isStream = true;
                                    }
                                    r1.controlsView.show(false, false);
                                    showProgress(true, false);
                                }
                                if (str6 == null) {
                                    r1.controlsView.setVisibility(0);
                                    return true;
                                }
                                r1.controlsView.setVisibility(8);
                                return false;
                            }
                            group3 = null;
                            str6 = group2;
                            group2 = group;
                            group = str5;
                            str5 = str4;
                            str4 = group3;
                            r1.initied = false;
                            r1.isCompleted = false;
                            r1.isAutoplay = z;
                            r1.playVideoUrl = null;
                            r1.playAudioUrl = null;
                            destroy();
                            r1.firstFrameRendered = false;
                            r1.currentAlpha = 1.0f;
                            if (r1.currentTask != null) {
                                r1.currentTask.cancel(true);
                                r1.currentTask = null;
                            }
                            updateFullscreenButton();
                            updateShareButton();
                            updateInlineButton();
                            updatePlayButton();
                            if (photo2 != null) {
                                r1.drawImage = false;
                            } else {
                                closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
                                if (closestPhotoSizeWithSize != null) {
                                    if (photo2 != null) {
                                    }
                                    if (photo2 != null) {
                                    }
                                    r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                                    r1.drawImage = true;
                                }
                            }
                            if (r1.progressAnimation != null) {
                                r1.progressAnimation.cancel();
                                r1.progressAnimation = null;
                            }
                            r1.isLoading = true;
                            r1.controlsView.setProgress(0);
                            r1.currentYoutubeId = str3;
                            str3 = null;
                            if (str4 != null) {
                                if (str3 != null) {
                                    youtubeVideoTask = new YoutubeVideoTask(str3);
                                    youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = youtubeVideoTask;
                                } else if (str5 != null) {
                                    youtubeVideoTask = new VimeoVideoTask(str5);
                                    youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = youtubeVideoTask;
                                } else if (group3 != null) {
                                    youtubeVideoTask = new CoubVideoTask(group3);
                                    youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = youtubeVideoTask;
                                    r1.isStream = true;
                                } else if (group != null) {
                                    youtubeVideoTask = new AparatVideoTask(group);
                                    youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = youtubeVideoTask;
                                } else if (group2 != null) {
                                    twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                                    twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = twitchClipVideoTask;
                                } else if (str6 != null) {
                                    twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                                    twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = twitchClipVideoTask;
                                    r1.isStream = true;
                                }
                                r1.controlsView.show(false, false);
                                showProgress(true, false);
                            } else {
                                r1.initied = true;
                                r1.playVideoUrl = str4;
                                r1.playVideoType = "other";
                                if (r1.isAutoplay) {
                                    preparePlayer();
                                }
                                showProgress(false, false);
                                r1.controlsView.show(true, true);
                            }
                            if (str6 == null) {
                                r1.controlsView.setVisibility(8);
                                return false;
                            }
                            r1.controlsView.setVisibility(0);
                            return true;
                        }
                    }
                    group2 = null;
                    if (group2 == null) {
                        matcher5 = coubIdRegex.matcher(str7);
                        if (matcher5.find()) {
                        }
                        if (group3 == null) {
                            group3 = null;
                        }
                        str6 = group2;
                        group2 = group;
                        group = str5;
                        str5 = str4;
                        str4 = null;
                        r1.initied = false;
                        r1.isCompleted = false;
                        r1.isAutoplay = z;
                        r1.playVideoUrl = null;
                        r1.playAudioUrl = null;
                        destroy();
                        r1.firstFrameRendered = false;
                        r1.currentAlpha = 1.0f;
                        if (r1.currentTask != null) {
                            r1.currentTask.cancel(true);
                            r1.currentTask = null;
                        }
                        updateFullscreenButton();
                        updateShareButton();
                        updateInlineButton();
                        updatePlayButton();
                        if (photo2 != null) {
                            closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
                            if (closestPhotoSizeWithSize != null) {
                                if (photo2 != null) {
                                }
                                if (photo2 != null) {
                                }
                                r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                                r1.drawImage = true;
                            }
                        } else {
                            r1.drawImage = false;
                        }
                        if (r1.progressAnimation != null) {
                            r1.progressAnimation.cancel();
                            r1.progressAnimation = null;
                        }
                        r1.isLoading = true;
                        r1.controlsView.setProgress(0);
                        r1.currentYoutubeId = str3;
                        str3 = null;
                        if (str4 != null) {
                            r1.initied = true;
                            r1.playVideoUrl = str4;
                            r1.playVideoType = "other";
                            if (r1.isAutoplay) {
                                preparePlayer();
                            }
                            showProgress(false, false);
                            r1.controlsView.show(true, true);
                        } else {
                            if (str3 != null) {
                                youtubeVideoTask = new YoutubeVideoTask(str3);
                                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                r1.currentTask = youtubeVideoTask;
                            } else if (str5 != null) {
                                youtubeVideoTask = new VimeoVideoTask(str5);
                                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                r1.currentTask = youtubeVideoTask;
                            } else if (group3 != null) {
                                youtubeVideoTask = new CoubVideoTask(group3);
                                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                r1.currentTask = youtubeVideoTask;
                                r1.isStream = true;
                            } else if (group != null) {
                                youtubeVideoTask = new AparatVideoTask(group);
                                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                r1.currentTask = youtubeVideoTask;
                            } else if (group2 != null) {
                                twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                                twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                r1.currentTask = twitchClipVideoTask;
                            } else if (str6 != null) {
                                twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                                twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                r1.currentTask = twitchClipVideoTask;
                                r1.isStream = true;
                            }
                            r1.controlsView.show(false, false);
                            showProgress(true, false);
                        }
                        if (str6 == null) {
                            r1.controlsView.setVisibility(0);
                            return true;
                        }
                        r1.controlsView.setVisibility(8);
                        return false;
                    }
                    group3 = null;
                    str6 = group2;
                    group2 = group;
                    group = str5;
                    str5 = str4;
                    str4 = group3;
                    r1.initied = false;
                    r1.isCompleted = false;
                    r1.isAutoplay = z;
                    r1.playVideoUrl = null;
                    r1.playAudioUrl = null;
                    destroy();
                    r1.firstFrameRendered = false;
                    r1.currentAlpha = 1.0f;
                    if (r1.currentTask != null) {
                        r1.currentTask.cancel(true);
                        r1.currentTask = null;
                    }
                    updateFullscreenButton();
                    updateShareButton();
                    updateInlineButton();
                    updatePlayButton();
                    if (photo2 != null) {
                        r1.drawImage = false;
                    } else {
                        closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
                        if (closestPhotoSizeWithSize != null) {
                            if (photo2 != null) {
                            }
                            if (photo2 != null) {
                            }
                            r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                            r1.drawImage = true;
                        }
                    }
                    if (r1.progressAnimation != null) {
                        r1.progressAnimation.cancel();
                        r1.progressAnimation = null;
                    }
                    r1.isLoading = true;
                    r1.controlsView.setProgress(0);
                    r1.currentYoutubeId = str3;
                    str3 = null;
                    if (str4 != null) {
                        if (str3 != null) {
                            youtubeVideoTask = new YoutubeVideoTask(str3);
                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                            r1.currentTask = youtubeVideoTask;
                        } else if (str5 != null) {
                            youtubeVideoTask = new VimeoVideoTask(str5);
                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                            r1.currentTask = youtubeVideoTask;
                        } else if (group3 != null) {
                            youtubeVideoTask = new CoubVideoTask(group3);
                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                            r1.currentTask = youtubeVideoTask;
                            r1.isStream = true;
                        } else if (group != null) {
                            youtubeVideoTask = new AparatVideoTask(group);
                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                            r1.currentTask = youtubeVideoTask;
                        } else if (group2 != null) {
                            twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                            twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                            r1.currentTask = twitchClipVideoTask;
                        } else if (str6 != null) {
                            twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                            twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                            r1.currentTask = twitchClipVideoTask;
                            r1.isStream = true;
                        }
                        r1.controlsView.show(false, false);
                        showProgress(true, false);
                    } else {
                        r1.initied = true;
                        r1.playVideoUrl = str4;
                        r1.playVideoType = "other";
                        if (r1.isAutoplay) {
                            preparePlayer();
                        }
                        showProgress(false, false);
                        r1.controlsView.show(true, true);
                    }
                    if (str6 == null) {
                        r1.controlsView.setVisibility(8);
                        return false;
                    }
                    r1.controlsView.setVisibility(0);
                    return true;
                }
            }
            str5 = null;
            if (str5 == null) {
                matcher3 = twitchClipIdRegex.matcher(str7);
                if (matcher3.find()) {
                }
                if (group != null) {
                    if (group == null) {
                        matcher4 = twitchStreamIdRegex.matcher(str7);
                        if (matcher4.find()) {
                        }
                        if (group2 != null) {
                            if (group2 == null) {
                                matcher5 = coubIdRegex.matcher(str7);
                                if (matcher5.find()) {
                                }
                                if (group3 == null) {
                                    group3 = null;
                                }
                                str6 = group2;
                                group2 = group;
                                group = str5;
                                str5 = str4;
                                str4 = null;
                                r1.initied = false;
                                r1.isCompleted = false;
                                r1.isAutoplay = z;
                                r1.playVideoUrl = null;
                                r1.playAudioUrl = null;
                                destroy();
                                r1.firstFrameRendered = false;
                                r1.currentAlpha = 1.0f;
                                if (r1.currentTask != null) {
                                    r1.currentTask.cancel(true);
                                    r1.currentTask = null;
                                }
                                updateFullscreenButton();
                                updateShareButton();
                                updateInlineButton();
                                updatePlayButton();
                                if (photo2 != null) {
                                    closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
                                    if (closestPhotoSizeWithSize != null) {
                                        if (photo2 != null) {
                                        }
                                        if (photo2 != null) {
                                        }
                                        r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                                        r1.drawImage = true;
                                    }
                                } else {
                                    r1.drawImage = false;
                                }
                                if (r1.progressAnimation != null) {
                                    r1.progressAnimation.cancel();
                                    r1.progressAnimation = null;
                                }
                                r1.isLoading = true;
                                r1.controlsView.setProgress(0);
                                r1.currentYoutubeId = str3;
                                str3 = null;
                                if (str4 != null) {
                                    r1.initied = true;
                                    r1.playVideoUrl = str4;
                                    r1.playVideoType = "other";
                                    if (r1.isAutoplay) {
                                        preparePlayer();
                                    }
                                    showProgress(false, false);
                                    r1.controlsView.show(true, true);
                                } else {
                                    if (str3 != null) {
                                        youtubeVideoTask = new YoutubeVideoTask(str3);
                                        youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                        r1.currentTask = youtubeVideoTask;
                                    } else if (str5 != null) {
                                        youtubeVideoTask = new VimeoVideoTask(str5);
                                        youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                        r1.currentTask = youtubeVideoTask;
                                    } else if (group3 != null) {
                                        youtubeVideoTask = new CoubVideoTask(group3);
                                        youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                        r1.currentTask = youtubeVideoTask;
                                        r1.isStream = true;
                                    } else if (group != null) {
                                        youtubeVideoTask = new AparatVideoTask(group);
                                        youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                        r1.currentTask = youtubeVideoTask;
                                    } else if (group2 != null) {
                                        twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                                        twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                        r1.currentTask = twitchClipVideoTask;
                                    } else if (str6 != null) {
                                        twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                                        twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                        r1.currentTask = twitchClipVideoTask;
                                        r1.isStream = true;
                                    }
                                    r1.controlsView.show(false, false);
                                    showProgress(true, false);
                                }
                                if (str6 == null) {
                                    r1.controlsView.setVisibility(0);
                                    return true;
                                }
                                r1.controlsView.setVisibility(8);
                                return false;
                            }
                            group3 = null;
                            str6 = group2;
                            group2 = group;
                            group = str5;
                            str5 = str4;
                            str4 = group3;
                            r1.initied = false;
                            r1.isCompleted = false;
                            r1.isAutoplay = z;
                            r1.playVideoUrl = null;
                            r1.playAudioUrl = null;
                            destroy();
                            r1.firstFrameRendered = false;
                            r1.currentAlpha = 1.0f;
                            if (r1.currentTask != null) {
                                r1.currentTask.cancel(true);
                                r1.currentTask = null;
                            }
                            updateFullscreenButton();
                            updateShareButton();
                            updateInlineButton();
                            updatePlayButton();
                            if (photo2 != null) {
                                r1.drawImage = false;
                            } else {
                                closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
                                if (closestPhotoSizeWithSize != null) {
                                    if (photo2 != null) {
                                    }
                                    if (photo2 != null) {
                                    }
                                    r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                                    r1.drawImage = true;
                                }
                            }
                            if (r1.progressAnimation != null) {
                                r1.progressAnimation.cancel();
                                r1.progressAnimation = null;
                            }
                            r1.isLoading = true;
                            r1.controlsView.setProgress(0);
                            r1.currentYoutubeId = str3;
                            str3 = null;
                            if (str4 != null) {
                                if (str3 != null) {
                                    youtubeVideoTask = new YoutubeVideoTask(str3);
                                    youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = youtubeVideoTask;
                                } else if (str5 != null) {
                                    youtubeVideoTask = new VimeoVideoTask(str5);
                                    youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = youtubeVideoTask;
                                } else if (group3 != null) {
                                    youtubeVideoTask = new CoubVideoTask(group3);
                                    youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = youtubeVideoTask;
                                    r1.isStream = true;
                                } else if (group != null) {
                                    youtubeVideoTask = new AparatVideoTask(group);
                                    youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = youtubeVideoTask;
                                } else if (group2 != null) {
                                    twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                                    twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = twitchClipVideoTask;
                                } else if (str6 != null) {
                                    twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                                    twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = twitchClipVideoTask;
                                    r1.isStream = true;
                                }
                                r1.controlsView.show(false, false);
                                showProgress(true, false);
                            } else {
                                r1.initied = true;
                                r1.playVideoUrl = str4;
                                r1.playVideoType = "other";
                                if (r1.isAutoplay) {
                                    preparePlayer();
                                }
                                showProgress(false, false);
                                r1.controlsView.show(true, true);
                            }
                            if (str6 == null) {
                                r1.controlsView.setVisibility(8);
                                return false;
                            }
                            r1.controlsView.setVisibility(0);
                            return true;
                        }
                    }
                    group2 = null;
                    if (group2 == null) {
                        matcher5 = coubIdRegex.matcher(str7);
                        if (matcher5.find()) {
                        }
                        if (group3 == null) {
                            group3 = null;
                        }
                        str6 = group2;
                        group2 = group;
                        group = str5;
                        str5 = str4;
                        str4 = null;
                        r1.initied = false;
                        r1.isCompleted = false;
                        r1.isAutoplay = z;
                        r1.playVideoUrl = null;
                        r1.playAudioUrl = null;
                        destroy();
                        r1.firstFrameRendered = false;
                        r1.currentAlpha = 1.0f;
                        if (r1.currentTask != null) {
                            r1.currentTask.cancel(true);
                            r1.currentTask = null;
                        }
                        updateFullscreenButton();
                        updateShareButton();
                        updateInlineButton();
                        updatePlayButton();
                        if (photo2 != null) {
                            closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
                            if (closestPhotoSizeWithSize != null) {
                                if (photo2 != null) {
                                }
                                if (photo2 != null) {
                                }
                                r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                                r1.drawImage = true;
                            }
                        } else {
                            r1.drawImage = false;
                        }
                        if (r1.progressAnimation != null) {
                            r1.progressAnimation.cancel();
                            r1.progressAnimation = null;
                        }
                        r1.isLoading = true;
                        r1.controlsView.setProgress(0);
                        r1.currentYoutubeId = str3;
                        str3 = null;
                        if (str4 != null) {
                            r1.initied = true;
                            r1.playVideoUrl = str4;
                            r1.playVideoType = "other";
                            if (r1.isAutoplay) {
                                preparePlayer();
                            }
                            showProgress(false, false);
                            r1.controlsView.show(true, true);
                        } else {
                            if (str3 != null) {
                                youtubeVideoTask = new YoutubeVideoTask(str3);
                                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                r1.currentTask = youtubeVideoTask;
                            } else if (str5 != null) {
                                youtubeVideoTask = new VimeoVideoTask(str5);
                                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                r1.currentTask = youtubeVideoTask;
                            } else if (group3 != null) {
                                youtubeVideoTask = new CoubVideoTask(group3);
                                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                r1.currentTask = youtubeVideoTask;
                                r1.isStream = true;
                            } else if (group != null) {
                                youtubeVideoTask = new AparatVideoTask(group);
                                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                r1.currentTask = youtubeVideoTask;
                            } else if (group2 != null) {
                                twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                                twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                r1.currentTask = twitchClipVideoTask;
                            } else if (str6 != null) {
                                twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                                twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                r1.currentTask = twitchClipVideoTask;
                                r1.isStream = true;
                            }
                            r1.controlsView.show(false, false);
                            showProgress(true, false);
                        }
                        if (str6 == null) {
                            r1.controlsView.setVisibility(0);
                            return true;
                        }
                        r1.controlsView.setVisibility(8);
                        return false;
                    }
                    group3 = null;
                    str6 = group2;
                    group2 = group;
                    group = str5;
                    str5 = str4;
                    str4 = group3;
                    r1.initied = false;
                    r1.isCompleted = false;
                    r1.isAutoplay = z;
                    r1.playVideoUrl = null;
                    r1.playAudioUrl = null;
                    destroy();
                    r1.firstFrameRendered = false;
                    r1.currentAlpha = 1.0f;
                    if (r1.currentTask != null) {
                        r1.currentTask.cancel(true);
                        r1.currentTask = null;
                    }
                    updateFullscreenButton();
                    updateShareButton();
                    updateInlineButton();
                    updatePlayButton();
                    if (photo2 != null) {
                        r1.drawImage = false;
                    } else {
                        closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
                        if (closestPhotoSizeWithSize != null) {
                            if (photo2 != null) {
                            }
                            if (photo2 != null) {
                            }
                            r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                            r1.drawImage = true;
                        }
                    }
                    if (r1.progressAnimation != null) {
                        r1.progressAnimation.cancel();
                        r1.progressAnimation = null;
                    }
                    r1.isLoading = true;
                    r1.controlsView.setProgress(0);
                    r1.currentYoutubeId = str3;
                    str3 = null;
                    if (str4 != null) {
                        if (str3 != null) {
                            youtubeVideoTask = new YoutubeVideoTask(str3);
                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                            r1.currentTask = youtubeVideoTask;
                        } else if (str5 != null) {
                            youtubeVideoTask = new VimeoVideoTask(str5);
                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                            r1.currentTask = youtubeVideoTask;
                        } else if (group3 != null) {
                            youtubeVideoTask = new CoubVideoTask(group3);
                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                            r1.currentTask = youtubeVideoTask;
                            r1.isStream = true;
                        } else if (group != null) {
                            youtubeVideoTask = new AparatVideoTask(group);
                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                            r1.currentTask = youtubeVideoTask;
                        } else if (group2 != null) {
                            twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                            twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                            r1.currentTask = twitchClipVideoTask;
                        } else if (str6 != null) {
                            twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                            twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                            r1.currentTask = twitchClipVideoTask;
                            r1.isStream = true;
                        }
                        r1.controlsView.show(false, false);
                        showProgress(true, false);
                    } else {
                        r1.initied = true;
                        r1.playVideoUrl = str4;
                        r1.playVideoType = "other";
                        if (r1.isAutoplay) {
                            preparePlayer();
                        }
                        showProgress(false, false);
                        r1.controlsView.show(true, true);
                    }
                    if (str6 == null) {
                        r1.controlsView.setVisibility(8);
                        return false;
                    }
                    r1.controlsView.setVisibility(0);
                    return true;
                }
            }
            group = null;
            if (group == null) {
                matcher4 = twitchStreamIdRegex.matcher(str7);
                if (matcher4.find()) {
                }
                if (group2 != null) {
                    if (group2 == null) {
                        matcher5 = coubIdRegex.matcher(str7);
                        if (matcher5.find()) {
                        }
                        if (group3 == null) {
                            group3 = null;
                        }
                        str6 = group2;
                        group2 = group;
                        group = str5;
                        str5 = str4;
                        str4 = null;
                        r1.initied = false;
                        r1.isCompleted = false;
                        r1.isAutoplay = z;
                        r1.playVideoUrl = null;
                        r1.playAudioUrl = null;
                        destroy();
                        r1.firstFrameRendered = false;
                        r1.currentAlpha = 1.0f;
                        if (r1.currentTask != null) {
                            r1.currentTask.cancel(true);
                            r1.currentTask = null;
                        }
                        updateFullscreenButton();
                        updateShareButton();
                        updateInlineButton();
                        updatePlayButton();
                        if (photo2 != null) {
                            closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
                            if (closestPhotoSizeWithSize != null) {
                                if (photo2 != null) {
                                }
                                if (photo2 != null) {
                                }
                                r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                                r1.drawImage = true;
                            }
                        } else {
                            r1.drawImage = false;
                        }
                        if (r1.progressAnimation != null) {
                            r1.progressAnimation.cancel();
                            r1.progressAnimation = null;
                        }
                        r1.isLoading = true;
                        r1.controlsView.setProgress(0);
                        r1.currentYoutubeId = str3;
                        str3 = null;
                        if (str4 != null) {
                            r1.initied = true;
                            r1.playVideoUrl = str4;
                            r1.playVideoType = "other";
                            if (r1.isAutoplay) {
                                preparePlayer();
                            }
                            showProgress(false, false);
                            r1.controlsView.show(true, true);
                        } else {
                            if (str3 != null) {
                                youtubeVideoTask = new YoutubeVideoTask(str3);
                                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                r1.currentTask = youtubeVideoTask;
                            } else if (str5 != null) {
                                youtubeVideoTask = new VimeoVideoTask(str5);
                                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                r1.currentTask = youtubeVideoTask;
                            } else if (group3 != null) {
                                youtubeVideoTask = new CoubVideoTask(group3);
                                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                r1.currentTask = youtubeVideoTask;
                                r1.isStream = true;
                            } else if (group != null) {
                                youtubeVideoTask = new AparatVideoTask(group);
                                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                r1.currentTask = youtubeVideoTask;
                            } else if (group2 != null) {
                                twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                                twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                r1.currentTask = twitchClipVideoTask;
                            } else if (str6 != null) {
                                twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                                twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                r1.currentTask = twitchClipVideoTask;
                                r1.isStream = true;
                            }
                            r1.controlsView.show(false, false);
                            showProgress(true, false);
                        }
                        if (str6 == null) {
                            r1.controlsView.setVisibility(0);
                            return true;
                        }
                        r1.controlsView.setVisibility(8);
                        return false;
                    }
                    group3 = null;
                    str6 = group2;
                    group2 = group;
                    group = str5;
                    str5 = str4;
                    str4 = group3;
                    r1.initied = false;
                    r1.isCompleted = false;
                    r1.isAutoplay = z;
                    r1.playVideoUrl = null;
                    r1.playAudioUrl = null;
                    destroy();
                    r1.firstFrameRendered = false;
                    r1.currentAlpha = 1.0f;
                    if (r1.currentTask != null) {
                        r1.currentTask.cancel(true);
                        r1.currentTask = null;
                    }
                    updateFullscreenButton();
                    updateShareButton();
                    updateInlineButton();
                    updatePlayButton();
                    if (photo2 != null) {
                        r1.drawImage = false;
                    } else {
                        closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
                        if (closestPhotoSizeWithSize != null) {
                            if (photo2 != null) {
                            }
                            if (photo2 != null) {
                            }
                            r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                            r1.drawImage = true;
                        }
                    }
                    if (r1.progressAnimation != null) {
                        r1.progressAnimation.cancel();
                        r1.progressAnimation = null;
                    }
                    r1.isLoading = true;
                    r1.controlsView.setProgress(0);
                    r1.currentYoutubeId = str3;
                    str3 = null;
                    if (str4 != null) {
                        if (str3 != null) {
                            youtubeVideoTask = new YoutubeVideoTask(str3);
                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                            r1.currentTask = youtubeVideoTask;
                        } else if (str5 != null) {
                            youtubeVideoTask = new VimeoVideoTask(str5);
                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                            r1.currentTask = youtubeVideoTask;
                        } else if (group3 != null) {
                            youtubeVideoTask = new CoubVideoTask(group3);
                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                            r1.currentTask = youtubeVideoTask;
                            r1.isStream = true;
                        } else if (group != null) {
                            youtubeVideoTask = new AparatVideoTask(group);
                            youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                            r1.currentTask = youtubeVideoTask;
                        } else if (group2 != null) {
                            twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                            twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                            r1.currentTask = twitchClipVideoTask;
                        } else if (str6 != null) {
                            twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                            twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                            r1.currentTask = twitchClipVideoTask;
                            r1.isStream = true;
                        }
                        r1.controlsView.show(false, false);
                        showProgress(true, false);
                    } else {
                        r1.initied = true;
                        r1.playVideoUrl = str4;
                        r1.playVideoType = "other";
                        if (r1.isAutoplay) {
                            preparePlayer();
                        }
                        showProgress(false, false);
                        r1.controlsView.show(true, true);
                    }
                    if (str6 == null) {
                        r1.controlsView.setVisibility(8);
                        return false;
                    }
                    r1.controlsView.setVisibility(0);
                    return true;
                }
            }
            group2 = null;
            if (group2 == null) {
                matcher5 = coubIdRegex.matcher(str7);
                if (matcher5.find()) {
                }
                if (group3 == null) {
                    group3 = null;
                }
                str6 = group2;
                group2 = group;
                group = str5;
                str5 = str4;
                str4 = null;
                r1.initied = false;
                r1.isCompleted = false;
                r1.isAutoplay = z;
                r1.playVideoUrl = null;
                r1.playAudioUrl = null;
                destroy();
                r1.firstFrameRendered = false;
                r1.currentAlpha = 1.0f;
                if (r1.currentTask != null) {
                    r1.currentTask.cancel(true);
                    r1.currentTask = null;
                }
                updateFullscreenButton();
                updateShareButton();
                updateInlineButton();
                updatePlayButton();
                if (photo2 != null) {
                    closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
                    if (closestPhotoSizeWithSize != null) {
                        if (photo2 != null) {
                        }
                        if (photo2 != null) {
                        }
                        r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                        r1.drawImage = true;
                    }
                } else {
                    r1.drawImage = false;
                }
                if (r1.progressAnimation != null) {
                    r1.progressAnimation.cancel();
                    r1.progressAnimation = null;
                }
                r1.isLoading = true;
                r1.controlsView.setProgress(0);
                r1.currentYoutubeId = str3;
                str3 = null;
                if (str4 != null) {
                    r1.initied = true;
                    r1.playVideoUrl = str4;
                    r1.playVideoType = "other";
                    if (r1.isAutoplay) {
                        preparePlayer();
                    }
                    showProgress(false, false);
                    r1.controlsView.show(true, true);
                } else {
                    if (str3 != null) {
                        youtubeVideoTask = new YoutubeVideoTask(str3);
                        youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                        r1.currentTask = youtubeVideoTask;
                    } else if (str5 != null) {
                        youtubeVideoTask = new VimeoVideoTask(str5);
                        youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                        r1.currentTask = youtubeVideoTask;
                    } else if (group3 != null) {
                        youtubeVideoTask = new CoubVideoTask(group3);
                        youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                        r1.currentTask = youtubeVideoTask;
                        r1.isStream = true;
                    } else if (group != null) {
                        youtubeVideoTask = new AparatVideoTask(group);
                        youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                        r1.currentTask = youtubeVideoTask;
                    } else if (group2 != null) {
                        twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                        twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                        r1.currentTask = twitchClipVideoTask;
                    } else if (str6 != null) {
                        twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                        twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                        r1.currentTask = twitchClipVideoTask;
                        r1.isStream = true;
                    }
                    r1.controlsView.show(false, false);
                    showProgress(true, false);
                }
                if (str6 == null) {
                    r1.controlsView.setVisibility(0);
                    return true;
                }
                r1.controlsView.setVisibility(8);
                return false;
            }
            group3 = null;
            str6 = group2;
            group2 = group;
            group = str5;
            str5 = str4;
            str4 = group3;
            r1.initied = false;
            r1.isCompleted = false;
            r1.isAutoplay = z;
            r1.playVideoUrl = null;
            r1.playAudioUrl = null;
            destroy();
            r1.firstFrameRendered = false;
            r1.currentAlpha = 1.0f;
            if (r1.currentTask != null) {
                r1.currentTask.cancel(true);
                r1.currentTask = null;
            }
            updateFullscreenButton();
            updateShareButton();
            updateInlineButton();
            updatePlayButton();
            if (photo2 != null) {
                r1.drawImage = false;
            } else {
                closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
                if (closestPhotoSizeWithSize != null) {
                    if (photo2 != null) {
                    }
                    if (photo2 != null) {
                    }
                    r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                    r1.drawImage = true;
                }
            }
            if (r1.progressAnimation != null) {
                r1.progressAnimation.cancel();
                r1.progressAnimation = null;
            }
            r1.isLoading = true;
            r1.controlsView.setProgress(0);
            r1.currentYoutubeId = str3;
            str3 = null;
            if (str4 != null) {
                if (str3 != null) {
                    youtubeVideoTask = new YoutubeVideoTask(str3);
                    youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                    r1.currentTask = youtubeVideoTask;
                } else if (str5 != null) {
                    youtubeVideoTask = new VimeoVideoTask(str5);
                    youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                    r1.currentTask = youtubeVideoTask;
                } else if (group3 != null) {
                    youtubeVideoTask = new CoubVideoTask(group3);
                    youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                    r1.currentTask = youtubeVideoTask;
                    r1.isStream = true;
                } else if (group != null) {
                    youtubeVideoTask = new AparatVideoTask(group);
                    youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                    r1.currentTask = youtubeVideoTask;
                } else if (group2 != null) {
                    twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                    twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                    r1.currentTask = twitchClipVideoTask;
                } else if (str6 != null) {
                    twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                    twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                    r1.currentTask = twitchClipVideoTask;
                    r1.isStream = true;
                }
                r1.controlsView.show(false, false);
                showProgress(true, false);
            } else {
                r1.initied = true;
                r1.playVideoUrl = str4;
                r1.playVideoType = "other";
                if (r1.isAutoplay) {
                    preparePlayer();
                }
                showProgress(false, false);
                r1.controlsView.show(true, true);
            }
            if (str6 == null) {
                r1.controlsView.setVisibility(8);
                return false;
            }
            r1.controlsView.setVisibility(0);
            return true;
        }
        group = str5;
        group2 = group;
        group3 = group2;
        str6 = group3;
        r1.initied = false;
        r1.isCompleted = false;
        r1.isAutoplay = z;
        r1.playVideoUrl = null;
        r1.playAudioUrl = null;
        destroy();
        r1.firstFrameRendered = false;
        r1.currentAlpha = 1.0f;
        if (r1.currentTask != null) {
            r1.currentTask.cancel(true);
            r1.currentTask = null;
        }
        updateFullscreenButton();
        updateShareButton();
        updateInlineButton();
        updatePlayButton();
        if (photo2 != null) {
            closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, 80, true);
            if (closestPhotoSizeWithSize != null) {
                if (photo2 != null) {
                }
                if (photo2 != null) {
                }
                r1.controlsView.imageReceiver.setImage(null, null, photo2 != null ? closestPhotoSizeWithSize.location : null, photo2 != null ? "80_80_b" : null, 0, null, 1);
                r1.drawImage = true;
            }
        } else {
            r1.drawImage = false;
        }
        if (r1.progressAnimation != null) {
            r1.progressAnimation.cancel();
            r1.progressAnimation = null;
        }
        r1.isLoading = true;
        r1.controlsView.setProgress(0);
        r1.currentYoutubeId = str3;
        str3 = null;
        if (str4 != null) {
            r1.initied = true;
            r1.playVideoUrl = str4;
            r1.playVideoType = "other";
            if (r1.isAutoplay) {
                preparePlayer();
            }
            showProgress(false, false);
            r1.controlsView.show(true, true);
        } else {
            if (str3 != null) {
                youtubeVideoTask = new YoutubeVideoTask(str3);
                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                r1.currentTask = youtubeVideoTask;
            } else if (str5 != null) {
                youtubeVideoTask = new VimeoVideoTask(str5);
                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                r1.currentTask = youtubeVideoTask;
            } else if (group3 != null) {
                youtubeVideoTask = new CoubVideoTask(group3);
                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                r1.currentTask = youtubeVideoTask;
                r1.isStream = true;
            } else if (group != null) {
                youtubeVideoTask = new AparatVideoTask(group);
                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                r1.currentTask = youtubeVideoTask;
            } else if (group2 != null) {
                twitchClipVideoTask = new TwitchClipVideoTask(str7, group2);
                twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                r1.currentTask = twitchClipVideoTask;
            } else if (str6 != null) {
                twitchClipVideoTask = new TwitchStreamVideoTask(str7, str6);
                twitchClipVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                r1.currentTask = twitchClipVideoTask;
                r1.isStream = true;
            }
            r1.controlsView.show(false, false);
            showProgress(true, false);
        }
        if (str6 == null) {
            r1.controlsView.setVisibility(0);
            return true;
        }
        r1.controlsView.setVisibility(8);
        return false;
    }

    public View getAspectRatioView() {
        return this.aspectRatioFrameLayout;
    }

    public TextureView getTextureView() {
        return this.textureView;
    }

    public ImageView getTextureImageView() {
        return this.textureImageView;
    }

    public View getControlsView() {
        return this.controlsView;
    }

    public void destroy() {
        this.videoPlayer.releasePlayer();
        if (this.currentTask != null) {
            this.currentTask.cancel(true);
            this.currentTask = null;
        }
        this.webView.stopLoading();
    }

    private void showProgress(boolean z, boolean z2) {
        float f = 0.0f;
        if (z2) {
            if (this.progressAnimation) {
                this.progressAnimation.cancel();
            }
            this.progressAnimation = new AnimatorSet();
            z2 = this.progressAnimation;
            Animator[] animatorArr = new Animator[1];
            RadialProgressView radialProgressView = this.progressView;
            String str = "alpha";
            float[] fArr = new float[1];
            if (z) {
                f = 1.0f;
            }
            fArr[0] = f;
            animatorArr[0] = ObjectAnimator.ofFloat(radialProgressView, str, fArr);
            z2.playTogether(animatorArr);
            this.progressAnimation.setDuration(150);
            this.progressAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    WebPlayerView.this.progressAnimation = null;
                }
            });
            this.progressAnimation.start();
            return;
        }
        z2 = this.progressView;
        if (z) {
            f = 1.0f;
        }
        z2.setAlpha(f);
    }
}
