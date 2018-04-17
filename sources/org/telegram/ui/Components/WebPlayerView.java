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
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import net.hockeyapp.android.UpdateFragment;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.Bitmaps;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.DefaultLoadControl;
import org.telegram.messenger.exoplayer2.ui.AspectRatioFrameLayout;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import org.telegram.tgnet.ConnectionsManager;
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

        C13392() {
        }

        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        }

        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        }

        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            if (!WebPlayerView.this.changingTextureView) {
                return true;
            }
            if (WebPlayerView.this.switchingInlineMode) {
                WebPlayerView.this.waitingForFirstTextureUpload = 2;
            }
            WebPlayerView.this.textureView.setSurfaceTexture(surface);
            WebPlayerView.this.textureView.setVisibility(0);
            WebPlayerView.this.changingTextureView = false;
            return false;
        }

        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
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
                } catch (Throwable e) {
                    if (WebPlayerView.this.currentBitmap != null) {
                        WebPlayerView.this.currentBitmap.recycle();
                        WebPlayerView.this.currentBitmap = null;
                    }
                    FileLog.m3e(e);
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
            ViewGroup parent = (ViewGroup) WebPlayerView.this.textureView.getParent();
            if (parent != null) {
                parent.removeView(WebPlayerView.this.textureView);
            }
            WebPlayerView.this.controlsView.show(false, false);
        }
    }

    /* renamed from: org.telegram.ui.Components.WebPlayerView$6 */
    class C13416 implements OnClickListener {
        C13416() {
        }

        public void onClick(View v) {
            if (!(!WebPlayerView.this.initied || WebPlayerView.this.changingTextureView || WebPlayerView.this.switchingInlineMode)) {
                if (WebPlayerView.this.firstFrameRendered) {
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

        public void onClick(View v) {
            if (WebPlayerView.this.initied) {
                if (WebPlayerView.this.playVideoUrl != null) {
                    if (!WebPlayerView.this.videoPlayer.isPlayerPrepared()) {
                        WebPlayerView.this.preparePlayer();
                    }
                    if (WebPlayerView.this.videoPlayer.isPlaying()) {
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

        public void onClick(View v) {
            if (!(WebPlayerView.this.textureView == null || !WebPlayerView.this.delegate.checkInlinePermissions() || WebPlayerView.this.changingTextureView || WebPlayerView.this.switchingInlineMode)) {
                if (WebPlayerView.this.firstFrameRendered) {
                    WebPlayerView.this.switchingInlineMode = true;
                    if (WebPlayerView.this.isInline) {
                        ViewGroup parent = (ViewGroup) WebPlayerView.this.aspectRatioFrameLayout.getParent();
                        if (parent != WebPlayerView.this) {
                            if (parent != null) {
                                parent.removeView(WebPlayerView.this.aspectRatioFrameLayout);
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
                        parent = (ViewGroup) WebPlayerView.this.controlsView.getParent();
                        if (parent != WebPlayerView.this) {
                            if (parent != null) {
                                parent.removeView(WebPlayerView.this.controlsView);
                            }
                            if (WebPlayerView.this.textureViewContainer != null) {
                                WebPlayerView.this.textureViewContainer.addView(WebPlayerView.this.controlsView);
                            } else {
                                WebPlayerView.this.addView(WebPlayerView.this.controlsView, 1);
                            }
                        }
                        WebPlayerView.this.controlsView.show(false, false);
                        WebPlayerView.this.delegate.prepareToSwitchInlineMode(false, null, WebPlayerView.this.aspectRatioFrameLayout.getAspectRatio(), WebPlayerView.this.allowInlineAnimation);
                    } else {
                        WebPlayerView.this.inFullscreen = false;
                        WebPlayerView.this.delegate.prepareToSwitchInlineMode(true, WebPlayerView.this.switchToInlineRunnable, WebPlayerView.this.aspectRatioFrameLayout.getAspectRatio(), WebPlayerView.this.allowInlineAnimation);
                    }
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.WebPlayerView$9 */
    class C13449 implements OnClickListener {
        C13449() {
        }

        public void onClick(View v) {
            if (WebPlayerView.this.delegate != null) {
                WebPlayerView.this.delegate.onSharePressed();
            }
        }
    }

    private class AparatVideoTask extends AsyncTask<Void, Void, String> {
        private boolean canRetry = true;
        private String[] results = new String[2];
        private String videoId;

        public AparatVideoTask(String vid) {
            this.videoId = vid;
        }

        protected String doInBackground(Void... voids) {
            String playerCode = WebPlayerView.this.downloadUrlContent(this, String.format(Locale.US, "http://www.aparat.com/video/video/embed/vt/frame/showvideo/yes/videohash/%s", new Object[]{this.videoId}));
            String str = null;
            if (isCancelled()) {
                return null;
            }
            try {
                Matcher filelist = WebPlayerView.aparatFileListPattern.matcher(playerCode);
                if (filelist.find()) {
                    JSONArray json = new JSONArray(filelist.group(1));
                    for (int a = 0; a < json.length(); a++) {
                        JSONArray array = json.getJSONArray(a);
                        if (array.length() != 0) {
                            JSONObject object = array.getJSONObject(0);
                            if (object.has("file")) {
                                this.results[0] = object.getString("file");
                                this.results[1] = "other";
                            }
                        }
                    }
                }
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
            if (!isCancelled()) {
                str = this.results[0];
            }
            return str;
        }

        protected void onPostExecute(String result) {
            if (result != null) {
                WebPlayerView.this.initied = true;
                WebPlayerView.this.playVideoUrl = result;
                WebPlayerView.this.playVideoType = this.results[1];
                if (WebPlayerView.this.isAutoplay) {
                    WebPlayerView.this.preparePlayer();
                }
                WebPlayerView.this.showProgress(false, true);
                WebPlayerView.this.controlsView.show(true, true);
            } else if (!isCancelled()) {
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
            setWillNotDraw(false);
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

        public void setDuration(int value) {
            if (this.duration != value && value >= 0) {
                if (!WebPlayerView.this.isStream) {
                    this.duration = value;
                    this.durationLayout = new StaticLayout(String.format(Locale.US, "%d:%02d", new Object[]{Integer.valueOf(this.duration / 60), Integer.valueOf(this.duration % 60)}), this.textPaint, AndroidUtilities.dp(1000.0f), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    if (this.durationLayout.getLineCount() > 0) {
                        this.durationWidth = (int) Math.ceil((double) this.durationLayout.getLineWidth(0));
                    }
                    invalidate();
                }
            }
        }

        public void setBufferedProgress(int position) {
            this.bufferedPosition = position;
            invalidate();
        }

        public void setProgress(int value) {
            if (!this.progressPressed && value >= 0) {
                if (!WebPlayerView.this.isStream) {
                    this.progress = value;
                    this.progressLayout = new StaticLayout(String.format(Locale.US, "%d:%02d", new Object[]{Integer.valueOf(this.progress / 60), Integer.valueOf(this.progress % 60)}), this.textPaint, AndroidUtilities.dp(1000.0f), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    invalidate();
                }
            }
        }

        public void show(boolean value, boolean animated) {
            if (this.isVisible != value) {
                this.isVisible = value;
                if (this.currentAnimation != null) {
                    this.currentAnimation.cancel();
                }
                AnimatorSet animatorSet;
                Animator[] animatorArr;
                if (this.isVisible) {
                    if (animated) {
                        this.currentAnimation = new AnimatorSet();
                        animatorSet = this.currentAnimation;
                        animatorArr = new Animator[1];
                        animatorArr[0] = ObjectAnimator.ofFloat(this, "alpha", new float[]{1.0f});
                        animatorSet.playTogether(animatorArr);
                        this.currentAnimation.setDuration(150);
                        this.currentAnimation.addListener(new C13462());
                        this.currentAnimation.start();
                    } else {
                        setAlpha(1.0f);
                    }
                } else if (animated) {
                    this.currentAnimation = new AnimatorSet();
                    animatorSet = this.currentAnimation;
                    animatorArr = new Animator[1];
                    animatorArr[0] = ObjectAnimator.ofFloat(this, "alpha", new float[]{0.0f});
                    animatorSet.playTogether(animatorArr);
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

        public boolean onInterceptTouchEvent(MotionEvent ev) {
            if (ev.getAction() != 0) {
                return super.onInterceptTouchEvent(ev);
            }
            if (this.isVisible) {
                onTouchEvent(ev);
                return this.progressPressed;
            }
            show(true, true);
            return true;
        }

        public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            super.requestDisallowInterceptTouchEvent(disallowIntercept);
            checkNeedHide();
        }

        public boolean onTouchEvent(MotionEvent event) {
            int progressLineX;
            int progressLineEndX;
            int progressY;
            if (WebPlayerView.this.inFullscreen) {
                progressLineX = AndroidUtilities.dp(36.0f) + this.durationWidth;
                progressLineEndX = (getMeasuredWidth() - AndroidUtilities.dp(76.0f)) - this.durationWidth;
                progressY = getMeasuredHeight() - AndroidUtilities.dp(28.0f);
            } else {
                progressLineX = 0;
                progressLineEndX = getMeasuredWidth();
                progressY = getMeasuredHeight() - AndroidUtilities.dp(12.0f);
            }
            int progressX = (this.duration != 0 ? (int) (((float) (progressLineEndX - progressLineX)) * (((float) this.progress) / ((float) this.duration))) : 0) + progressLineX;
            int x;
            if (event.getAction() == 0) {
                if (!this.isVisible || WebPlayerView.this.isInline || WebPlayerView.this.isStream) {
                    show(true, true);
                } else if (this.duration != 0) {
                    x = (int) event.getX();
                    int y = (int) event.getY();
                    if (x >= progressX - AndroidUtilities.dp(10.0f) && x <= AndroidUtilities.dp(10.0f) + progressX && y >= progressY - AndroidUtilities.dp(10.0f) && y <= AndroidUtilities.dp(10.0f) + progressY) {
                        this.progressPressed = true;
                        this.lastProgressX = x;
                        this.currentProgressX = progressX;
                        getParent().requestDisallowInterceptTouchEvent(true);
                        invalidate();
                    }
                }
                AndroidUtilities.cancelRunOnUIThread(this.hideRunnable);
            } else {
                if (event.getAction() != 1) {
                    if (event.getAction() != 3) {
                        if (event.getAction() == 2 && this.progressPressed) {
                            x = (int) event.getX();
                            this.currentProgressX -= this.lastProgressX - x;
                            this.lastProgressX = x;
                            if (this.currentProgressX < progressLineX) {
                                this.currentProgressX = progressLineX;
                            } else if (this.currentProgressX > progressLineEndX) {
                                this.currentProgressX = progressLineEndX;
                            }
                            setProgress((int) (((float) (this.duration * 1000)) * (((float) (this.currentProgressX - progressLineX)) / ((float) (progressLineEndX - progressLineX)))));
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
                        this.progress = (int) (((float) this.duration) * (((float) (this.currentProgressX - progressLineX)) / ((float) (progressLineEndX - progressLineX))));
                        WebPlayerView.this.videoPlayer.seekTo(((long) this.progress) * 1000);
                    }
                }
            }
            super.onTouchEvent(event);
            return true;
        }

        protected void onDraw(Canvas canvas) {
            Canvas canvas2 = canvas;
            if (WebPlayerView.this.drawImage) {
                if (WebPlayerView.this.firstFrameRendered && WebPlayerView.this.currentAlpha != 0.0f) {
                    long newTime = System.currentTimeMillis();
                    long dt = newTime - WebPlayerView.this.lastUpdateTime;
                    WebPlayerView.this.lastUpdateTime = newTime;
                    WebPlayerView.this.currentAlpha = WebPlayerView.this.currentAlpha - (((float) dt) / 150.0f);
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
                int width = getMeasuredWidth();
                int height = getMeasuredHeight();
                if (!WebPlayerView.this.isInline) {
                    i = 10;
                    if (r0.durationLayout != null) {
                        canvas.save();
                        canvas2.translate((float) ((width - AndroidUtilities.dp(58.0f)) - r0.durationWidth), (float) (height - AndroidUtilities.dp((float) ((WebPlayerView.this.inFullscreen ? 6 : 10) + 29))));
                        r0.durationLayout.draw(canvas2);
                        canvas.restore();
                    }
                    if (r0.progressLayout != null) {
                        canvas.save();
                        float dp = (float) AndroidUtilities.dp(18.0f);
                        if (WebPlayerView.this.inFullscreen) {
                            i = 6;
                        }
                        canvas2.translate(dp, (float) (height - AndroidUtilities.dp((float) (29 + i))));
                        r0.progressLayout.draw(canvas2);
                        canvas.restore();
                    }
                }
                if (r0.duration != 0) {
                    int progressLineY;
                    int progressLineEndX;
                    int cy;
                    int progressX;
                    if (WebPlayerView.this.isInline) {
                        progressLineY = height - AndroidUtilities.dp(3.0f);
                        i = 0;
                        progressLineEndX = width;
                        cy = height - AndroidUtilities.dp(7.0f);
                    } else if (WebPlayerView.this.inFullscreen) {
                        progressLineY = height - AndroidUtilities.dp(29.0f);
                        i = AndroidUtilities.dp(36.0f) + r0.durationWidth;
                        progressLineEndX = (width - AndroidUtilities.dp(76.0f)) - r0.durationWidth;
                        cy = height - AndroidUtilities.dp(28.0f);
                    } else {
                        progressLineY = height - AndroidUtilities.dp(13.0f);
                        i = 0;
                        progressLineEndX = width;
                        cy = height - AndroidUtilities.dp(12.0f);
                    }
                    int progressLineY2 = progressLineY;
                    int progressLineX = i;
                    int progressLineEndX2 = progressLineEndX;
                    int cy2 = cy;
                    if (WebPlayerView.this.inFullscreen) {
                        canvas2.drawRect((float) progressLineX, (float) progressLineY2, (float) progressLineEndX2, (float) (AndroidUtilities.dp(3.0f) + progressLineY2), r0.progressInnerPaint);
                    }
                    if (r0.progressPressed) {
                        progressLineY = r0.currentProgressX;
                    } else {
                        progressLineY = ((int) (((float) (progressLineEndX2 - progressLineX)) * (((float) r0.progress) / ((float) r0.duration)))) + progressLineX;
                    }
                    int progressX2 = progressLineY;
                    if (r0.bufferedPosition == 0 || r0.duration == 0) {
                        progressX = progressX2;
                    } else {
                        progressX = progressX2;
                        canvas2.drawRect((float) progressLineX, (float) progressLineY2, (((float) (progressLineEndX2 - progressLineX)) * (((float) r0.bufferedPosition) / ((float) r0.duration))) + ((float) progressLineX), (float) (AndroidUtilities.dp(3.0f) + progressLineY2), WebPlayerView.this.inFullscreen ? r0.progressBufferedPaint : r0.progressInnerPaint);
                    }
                    canvas2.drawRect((float) progressLineX, (float) progressLineY2, (float) progressX, (float) (AndroidUtilities.dp(3.0f) + progressLineY2), r0.progressPaint);
                    if (!WebPlayerView.this.isInline) {
                        canvas2.drawCircle((float) progressX, (float) cy2, (float) AndroidUtilities.dp(r0.progressPressed ? 7.0f : 5.0f), r0.progressPaint);
                    }
                }
            }
        }
    }

    private class CoubVideoTask extends AsyncTask<Void, Void, String> {
        private boolean canRetry = true;
        private String[] results = new String[4];
        private String videoId;

        public CoubVideoTask(String vid) {
            this.videoId = vid;
        }

        private String decodeUrl(String input) {
            StringBuilder source = new StringBuilder(input);
            for (int a = 0; a < source.length(); a++) {
                char c = source.charAt(a);
                char lower = Character.toLowerCase(c);
                source.setCharAt(a, c == lower ? Character.toUpperCase(c) : lower);
            }
            try {
                return new String(Base64.decode(source.toString(), 0), C0542C.UTF8_NAME);
            } catch (Exception e) {
                return null;
            }
        }

        protected String doInBackground(Void... voids) {
            String playerCode = WebPlayerView.this.downloadUrlContent(this, String.format(Locale.US, "https://coub.com/api/v2/coubs/%s.json", new Object[]{this.videoId}));
            String str = null;
            if (isCancelled()) {
                return null;
            }
            try {
                JSONObject json = new JSONObject(playerCode).getJSONObject("file_versions").getJSONObject("mobile");
                String video = decodeUrl(json.getString("gifv"));
                String audio = json.getJSONArray(MimeTypes.BASE_TYPE_AUDIO).getString(0);
                if (!(video == null || audio == null)) {
                    this.results[0] = video;
                    this.results[1] = "other";
                    this.results[2] = audio;
                    this.results[3] = "other";
                }
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
            if (!isCancelled()) {
                str = this.results[0];
            }
            return str;
        }

        protected void onPostExecute(String result) {
            if (result != null) {
                WebPlayerView.this.initied = true;
                WebPlayerView.this.playVideoUrl = result;
                WebPlayerView.this.playVideoType = this.results[1];
                WebPlayerView.this.playAudioUrl = this.results[2];
                WebPlayerView.this.playAudioType = this.results[3];
                if (WebPlayerView.this.isAutoplay) {
                    WebPlayerView.this.preparePlayer();
                }
                WebPlayerView.this.showProgress(false, true);
                WebPlayerView.this.controlsView.show(true, true);
            } else if (!isCancelled()) {
                WebPlayerView.this.onInitFailed();
            }
        }
    }

    private class JSExtractor {
        private String[] assign_operators = new String[]{"|=", "^=", "&=", ">>=", "<<=", "-=", "+=", "%=", "/=", "*=", "="};
        ArrayList<String> codeLines = new ArrayList();
        private String jsCode;
        private String[] operators = new String[]{"|", "^", "&", ">>", "<<", "-", "+", "%", "/", "*"};

        public JSExtractor(String js) {
            this.jsCode = js;
        }

        private void interpretExpression(String expr, HashMap<String, String> localVars, int allowRecursion) throws Exception {
            expr = expr.trim();
            if (!TextUtils.isEmpty(expr)) {
                int parens_count;
                String remaining_expr;
                String func;
                String index;
                int a = 0;
                if (expr.charAt(0) == '(') {
                    parens_count = 0;
                    Matcher matcher = WebPlayerView.exprParensPattern.matcher(expr);
                    while (matcher.find()) {
                        if (matcher.group(0).indexOf(48) == 40) {
                            parens_count++;
                        } else {
                            parens_count--;
                            if (parens_count == 0) {
                                interpretExpression(expr.substring(1, matcher.start()), localVars, allowRecursion);
                                remaining_expr = expr.substring(matcher.end()).trim();
                                if (!TextUtils.isEmpty(remaining_expr)) {
                                    expr = remaining_expr;
                                    if (parens_count != 0) {
                                        throw new Exception(String.format("Premature end of parens in %s", new Object[]{expr}));
                                    }
                                }
                                return;
                            }
                        }
                    }
                    if (parens_count != 0) {
                        throw new Exception(String.format("Premature end of parens in %s", new Object[]{expr}));
                    }
                }
                for (String func2 : this.assign_operators) {
                    Matcher matcher2 = Pattern.compile(String.format(Locale.US, "(?x)(%s)(?:\\[([^\\]]+?)\\])?\\s*%s(.*)$", new Object[]{WebPlayerView.exprName, Pattern.quote(func2)})).matcher(expr);
                    if (matcher2.find()) {
                        interpretExpression(matcher2.group(3), localVars, allowRecursion - 1);
                        index = matcher2.group(2);
                        if (TextUtils.isEmpty(index)) {
                            localVars.put(matcher2.group(1), TtmlNode.ANONYMOUS_REGION_ID);
                        } else {
                            interpretExpression(index, localVars, allowRecursion);
                        }
                        return;
                    }
                }
                try {
                    Integer.parseInt(expr);
                } catch (Exception e) {
                    if (!Pattern.compile(String.format(Locale.US, "(?!if|return|true|false)(%s)$", new Object[]{WebPlayerView.exprName})).matcher(expr).find()) {
                        if (expr.charAt(0) != '\"' || expr.charAt(expr.length() - 1) != '\"') {
                            try {
                                new JSONObject(expr).toString();
                            } catch (Exception e2) {
                                Matcher matcher3 = Pattern.compile(String.format(Locale.US, "(%s)\\[(.+)\\]$", new Object[]{WebPlayerView.exprName})).matcher(expr);
                                if (matcher3.find()) {
                                    index = matcher3.group(1);
                                    interpretExpression(matcher3.group(2), localVars, allowRecursion - 1);
                                    return;
                                }
                                matcher3 = Pattern.compile(String.format(Locale.US, "(%s)(?:\\.([^(]+)|\\[([^]]+)\\])\\s*(?:\\(+([^()]*)\\))?$", new Object[]{WebPlayerView.exprName})).matcher(expr);
                                if (matcher3.find()) {
                                    func2 = matcher3.group(1);
                                    String m1 = matcher3.group(2);
                                    remaining_expr = (TextUtils.isEmpty(m1) ? matcher3.group(3) : m1).replace("\"", TtmlNode.ANONYMOUS_REGION_ID);
                                    String arg_str = matcher3.group(4);
                                    if (localVars.get(func2) == null) {
                                        extractObject(func2);
                                    }
                                    if (arg_str != null) {
                                        if (expr.charAt(expr.length() - 1) != ')') {
                                            throw new Exception("last char not ')'");
                                        }
                                        if (arg_str.length() != 0) {
                                            String[] args = arg_str.split(",");
                                            while (a < args.length) {
                                                interpretExpression(args[a], localVars, allowRecursion);
                                                a++;
                                            }
                                        }
                                        return;
                                    }
                                    return;
                                }
                                matcher3 = Pattern.compile(String.format(Locale.US, "(%s)\\[(.+)\\]$", new Object[]{WebPlayerView.exprName})).matcher(expr);
                                if (matcher3.find()) {
                                    Object val = localVars.get(matcher3.group(1));
                                    interpretExpression(matcher3.group(2), localVars, allowRecursion - 1);
                                    return;
                                }
                                for (String func3 : this.operators) {
                                    Matcher matcher4 = Pattern.compile(String.format(Locale.US, "(.+?)%s(.+)", new Object[]{Pattern.quote(func3)})).matcher(expr);
                                    if (matcher4.find()) {
                                        boolean[] abort = new boolean[1];
                                        interpretStatement(matcher4.group(1), localVars, abort, allowRecursion - 1);
                                        if (abort[0]) {
                                            throw new Exception(String.format("Premature left-side return of %s in %s", new Object[]{func3, expr}));
                                        }
                                        interpretStatement(matcher4.group(2), localVars, abort, allowRecursion - 1);
                                        if (abort[0]) {
                                            throw new Exception(String.format("Premature right-side return of %s in %s", new Object[]{func3, expr}));
                                        }
                                    }
                                }
                                matcher3 = Pattern.compile(String.format(Locale.US, "^(%s)\\(([a-zA-Z0-9_$,]*)\\)$", new Object[]{WebPlayerView.exprName})).matcher(expr);
                                if (matcher3.find()) {
                                    extractFunction(matcher3.group(1));
                                }
                                throw new Exception(String.format("Unsupported JS expression %s", new Object[]{expr}));
                            }
                        }
                    }
                }
            }
        }

        private void interpretStatement(String stmt, HashMap<String, String> localVars, boolean[] abort, int allowRecursion) throws Exception {
            if (allowRecursion < 0) {
                throw new Exception("recursion limit reached");
            }
            String expr;
            abort[0] = false;
            stmt = stmt.trim();
            Matcher matcher = WebPlayerView.stmtVarPattern.matcher(stmt);
            if (matcher.find()) {
                expr = stmt.substring(matcher.group(0).length());
            } else {
                matcher = WebPlayerView.stmtReturnPattern.matcher(stmt);
                if (matcher.find()) {
                    String expr2 = stmt.substring(matcher.group(0).length());
                    abort[0] = true;
                    expr = expr2;
                } else {
                    expr = stmt;
                }
            }
            interpretExpression(expr, localVars, allowRecursion);
        }

        private HashMap<String, Object> extractObject(String objname) throws Exception {
            String funcName = "(?:[a-zA-Z$0-9]+|\"[a-zA-Z$0-9]+\"|'[a-zA-Z$0-9]+')";
            HashMap<String, Object> obj = new HashMap();
            Matcher matcher = Pattern.compile(String.format(Locale.US, "(?:var\\s+)?%s\\s*=\\s*\\{\\s*((%s\\s*:\\s*function\\(.*?\\)\\s*\\{.*?\\}(?:,\\s*)?)*)\\}\\s*;", new Object[]{Pattern.quote(objname), funcName})).matcher(this.jsCode);
            String fields = null;
            while (matcher.find()) {
                String code = matcher.group();
                fields = matcher.group(2);
                if (!TextUtils.isEmpty(fields)) {
                    if (!this.codeLines.contains(code)) {
                        this.codeLines.add(matcher.group());
                    }
                    matcher = Pattern.compile(String.format("(%s)\\s*:\\s*function\\(([a-z,]+)\\)\\{([^}]+)\\}", new Object[]{funcName})).matcher(fields);
                    while (matcher.find()) {
                        buildFunction(matcher.group(2).split(","), matcher.group(3));
                    }
                    return obj;
                }
            }
            matcher = Pattern.compile(String.format("(%s)\\s*:\\s*function\\(([a-z,]+)\\)\\{([^}]+)\\}", new Object[]{funcName})).matcher(fields);
            while (matcher.find()) {
                buildFunction(matcher.group(2).split(","), matcher.group(3));
            }
            return obj;
        }

        private void buildFunction(String[] argNames, String funcCode) throws Exception {
            HashMap<String, String> localVars = new HashMap();
            for (Object put : argNames) {
                localVars.put(put, TtmlNode.ANONYMOUS_REGION_ID);
            }
            String[] stmts = funcCode.split(";");
            boolean[] abort = new boolean[1];
            int a = 0;
            while (a < stmts.length) {
                interpretStatement(stmts[a], localVars, abort, 100);
                if (!abort[0]) {
                    a++;
                } else {
                    return;
                }
            }
        }

        private String extractFunction(String funcName) throws Exception {
            try {
                String quote = Pattern.quote(funcName);
                Matcher matcher = Pattern.compile(String.format(Locale.US, "(?x)(?:function\\s+%s|[{;,]\\s*%s\\s*=\\s*function|var\\s+%s\\s*=\\s*function)\\s*\\(([^)]*)\\)\\s*\\{([^}]+)\\}", new Object[]{quote, quote, quote})).matcher(this.jsCode);
                if (matcher.find()) {
                    String group = matcher.group();
                    if (!this.codeLines.contains(group)) {
                        ArrayList arrayList = this.codeLines;
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(group);
                        stringBuilder.append(";");
                        arrayList.add(stringBuilder.toString());
                    }
                    buildFunction(matcher.group(1).split(","), matcher.group(2));
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

        public JavaScriptInterface(CallJavaResultInterface callJavaResult) {
            this.callJavaResultInterface = callJavaResult;
        }

        @JavascriptInterface
        public void returnResultToJava(String value) {
            this.callJavaResultInterface.jsCallFinished(value);
        }
    }

    private class TwitchClipVideoTask extends AsyncTask<Void, Void, String> {
        private boolean canRetry = true;
        private String currentUrl;
        private String[] results = new String[2];
        private String videoId;

        public TwitchClipVideoTask(String url, String vid) {
            this.videoId = vid;
            this.currentUrl = url;
        }

        protected String doInBackground(Void... voids) {
            String str = null;
            String playerCode = WebPlayerView.this.downloadUrlContent(this, this.currentUrl, null, false);
            if (isCancelled()) {
                return null;
            }
            try {
                Matcher filelist = WebPlayerView.twitchClipFilePattern.matcher(playerCode);
                if (filelist.find()) {
                    this.results[0] = new JSONObject(filelist.group(1)).getJSONArray("quality_options").getJSONObject(0).getString("source");
                    this.results[1] = "other";
                }
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
            if (!isCancelled()) {
                str = this.results[0];
            }
            return str;
        }

        protected void onPostExecute(String result) {
            if (result != null) {
                WebPlayerView.this.initied = true;
                WebPlayerView.this.playVideoUrl = result;
                WebPlayerView.this.playVideoType = this.results[1];
                if (WebPlayerView.this.isAutoplay) {
                    WebPlayerView.this.preparePlayer();
                }
                WebPlayerView.this.showProgress(false, true);
                WebPlayerView.this.controlsView.show(true, true);
            } else if (!isCancelled()) {
                WebPlayerView.this.onInitFailed();
            }
        }
    }

    private class TwitchStreamVideoTask extends AsyncTask<Void, Void, String> {
        private boolean canRetry = true;
        private String currentUrl;
        private String[] results = new String[2];
        private String videoId;

        public TwitchStreamVideoTask(String url, String vid) {
            this.videoId = vid;
            this.currentUrl = url;
        }

        protected String doInBackground(Void... voids) {
            HashMap<String, String> headers = new HashMap();
            headers.put("Client-ID", "jzkbprff40iqj646a697cyrvl0zt2m6");
            int indexOf = this.videoId.indexOf(38);
            int idx = indexOf;
            if (indexOf > 0) {
                r1.videoId = r1.videoId.substring(0, idx);
            }
            String streamCode = WebPlayerView.this.downloadUrlContent(r1, String.format(Locale.US, "https://api.twitch.tv/kraken/streams/%s?stream_type=all", new Object[]{r1.videoId}), headers, false);
            if (isCancelled()) {
                return null;
            }
            try {
                JSONObject stream = new JSONObject(streamCode).getJSONObject("stream");
                JSONObject accessToken = new JSONObject(WebPlayerView.this.downloadUrlContent(r1, String.format(Locale.US, "https://api.twitch.tv/api/channels/%s/access_token", new Object[]{r1.videoId}), headers, false));
                String sig = URLEncoder.encode(accessToken.getString("sig"), C0542C.UTF8_NAME);
                String token = URLEncoder.encode(accessToken.getString("token"), C0542C.UTF8_NAME);
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("https://youtube.googleapis.com/v/");
                stringBuilder.append(r1.videoId);
                URLEncoder.encode(stringBuilder.toString(), C0542C.UTF8_NAME);
                stringBuilder = new StringBuilder();
                stringBuilder.append("allow_source=true&allow_audio_only=true&allow_spectre=true&player=twitchweb&segment_preference=4&p=");
                stringBuilder.append((int) (Math.random() * 1.0E7d));
                stringBuilder.append("&sig=");
                stringBuilder.append(sig);
                stringBuilder.append("&token=");
                stringBuilder.append(token);
                String params = stringBuilder.toString();
                r1.results[0] = String.format(Locale.US, "https://usher.ttvnw.net/api/channel/hls/%s.m3u8?%s", new Object[]{r1.videoId, params});
                r1.results[1] = "hls";
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
            return isCancelled() ? null : r1.results[0];
        }

        protected void onPostExecute(String result) {
            if (result != null) {
                WebPlayerView.this.initied = true;
                WebPlayerView.this.playVideoUrl = result;
                WebPlayerView.this.playVideoType = this.results[1];
                if (WebPlayerView.this.isAutoplay) {
                    WebPlayerView.this.preparePlayer();
                }
                WebPlayerView.this.showProgress(false, true);
                WebPlayerView.this.controlsView.show(true, true);
            } else if (!isCancelled()) {
                WebPlayerView.this.onInitFailed();
            }
        }
    }

    private class VimeoVideoTask extends AsyncTask<Void, Void, String> {
        private boolean canRetry = true;
        private String[] results = new String[2];
        private String videoId;

        public VimeoVideoTask(String vid) {
            this.videoId = vid;
        }

        protected String doInBackground(Void... voids) {
            String playerCode = WebPlayerView.this.downloadUrlContent(this, String.format(Locale.US, "https://player.vimeo.com/video/%s/config", new Object[]{this.videoId}));
            String str = null;
            if (isCancelled()) {
                return null;
            }
            try {
                JSONObject files = new JSONObject(playerCode).getJSONObject("request").getJSONObject("files");
                if (files.has("hls")) {
                    JSONObject hls = files.getJSONObject("hls");
                    try {
                        this.results[0] = hls.getString(UpdateFragment.FRAGMENT_URL);
                    } catch (Exception e) {
                        this.results[0] = hls.getJSONObject("cdns").getJSONObject(hls.getString("default_cdn")).getString(UpdateFragment.FRAGMENT_URL);
                    }
                    this.results[1] = "hls";
                } else if (files.has("progressive")) {
                    this.results[1] = "other";
                    this.results[0] = files.getJSONArray("progressive").getJSONObject(0).getString(UpdateFragment.FRAGMENT_URL);
                }
            } catch (Throwable e2) {
                FileLog.m3e(e2);
            }
            if (!isCancelled()) {
                str = this.results[0];
            }
            return str;
        }

        protected void onPostExecute(String result) {
            if (result != null) {
                WebPlayerView.this.initied = true;
                WebPlayerView.this.playVideoUrl = result;
                WebPlayerView.this.playVideoType = this.results[1];
                if (WebPlayerView.this.isAutoplay) {
                    WebPlayerView.this.preparePlayer();
                }
                WebPlayerView.this.showProgress(false, true);
                WebPlayerView.this.controlsView.show(true, true);
            } else if (!isCancelled()) {
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

        public YoutubeVideoTask(String vid) {
            this.videoId = vid;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        protected String[] doInBackground(Void... voids) {
            Throwable params;
            Throwable embedCode;
            int i;
            String embedCode2 = WebPlayerView.this;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("https://www.youtube.com/embed/");
            stringBuilder.append(this.videoId);
            embedCode2 = embedCode2.downloadUrlContent(this, stringBuilder.toString());
            String[] strArr = null;
            if (isCancelled()) {
                return null;
            }
            String currentUrl;
            String[] strArr2;
            String params2 = new StringBuilder();
            params2.append("video_id=");
            params2.append(r1.videoId);
            params2.append("&ps=default&gl=US&hl=en");
            params2 = params2.toString();
            try {
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append(params2);
                stringBuilder2.append("&eurl=");
                StringBuilder stringBuilder3 = new StringBuilder();
                stringBuilder3.append("https://youtube.googleapis.com/v/");
                stringBuilder3.append(r1.videoId);
                stringBuilder2.append(URLEncoder.encode(stringBuilder3.toString(), C0542C.UTF8_NAME));
                params2 = stringBuilder2.toString();
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
            if (embedCode2 != null) {
                Matcher matcher = WebPlayerView.stsPattern.matcher(embedCode2);
                if (matcher.find()) {
                    stringBuilder3 = new StringBuilder();
                    stringBuilder3.append(params2);
                    stringBuilder3.append("&sts=");
                    stringBuilder3.append(embedCode2.substring(matcher.start() + 6, matcher.end()));
                    params2 = stringBuilder3.toString();
                } else {
                    stringBuilder3 = new StringBuilder();
                    stringBuilder3.append(params2);
                    stringBuilder3.append("&sts=");
                    params2 = stringBuilder3.toString();
                }
            }
            int i2 = 1;
            r1.result[1] = "dash";
            String otherUrl = null;
            extra = new String[5];
            int i3 = 0;
            extra[0] = TtmlNode.ANONYMOUS_REGION_ID;
            extra[1] = "&el=leanback";
            int i4 = 2;
            extra[2] = "&el=embedded";
            extra[3] = "&el=detailpage";
            extra[4] = "&el=vevo";
            boolean encrypted = false;
            int i5 = 0;
            while (i5 < extra.length) {
                String videoInfo = WebPlayerView.this;
                StringBuilder stringBuilder4 = new StringBuilder();
                stringBuilder4.append("https://www.youtube.com/get_video_info?");
                stringBuilder4.append(params2);
                stringBuilder4.append(extra[i5]);
                videoInfo = videoInfo.downloadUrlContent(r1, stringBuilder4.toString());
                if (isCancelled()) {
                    return strArr;
                }
                String params3;
                boolean exists = false;
                String hls = null;
                boolean isLive = false;
                if (videoInfo != null) {
                    String[] args = videoInfo.split("&");
                    String otherUrl2 = otherUrl;
                    int a = i3;
                    while (a < args.length) {
                        if (args[a].startsWith("dashmpd")) {
                            exists = true;
                            strArr = args[a].split("=");
                            if (strArr.length == i4) {
                                try {
                                    r1.result[0] = URLDecoder.decode(strArr[i2], C0542C.UTF8_NAME);
                                } catch (Throwable e2) {
                                    FileLog.m3e(e2);
                                }
                            }
                            params3 = params2;
                        } else if (args[a].startsWith("url_encoded_fmt_stream_map")) {
                            strArr = args[a].split("=");
                            if (strArr.length == 2) {
                                String[] args2;
                                try {
                                    String[] args3 = URLDecoder.decode(strArr[1], C0542C.UTF8_NAME).split("[&,]");
                                    boolean isMp4 = false;
                                    currentUrl = null;
                                    i3 = 0;
                                    while (true) {
                                        params3 = params2;
                                        try {
                                            if (i3 >= args3.length) {
                                                break;
                                            }
                                            args2 = strArr;
                                            try {
                                                params2 = args3[i3].split("=");
                                                String[] args32 = args3;
                                                if (params2[null].startsWith("type") != null) {
                                                    if (URLDecoder.decode(params2[1], C0542C.UTF8_NAME).contains(MimeTypes.VIDEO_MP4)) {
                                                        isMp4 = true;
                                                    }
                                                } else if (params2[null].startsWith(UpdateFragment.FRAGMENT_URL) != null) {
                                                    currentUrl = URLDecoder.decode(params2[1], C0542C.UTF8_NAME);
                                                } else if (params2[null].startsWith("itag") != null) {
                                                    currentUrl = null;
                                                    isMp4 = false;
                                                }
                                                if (isMp4 && currentUrl != null) {
                                                    break;
                                                }
                                                i3++;
                                                params2 = params3;
                                                strArr = args2;
                                                args3 = args32;
                                            } catch (Throwable e22) {
                                                params = e22;
                                            }
                                        } catch (Throwable e222) {
                                            args2 = strArr;
                                            params = e222;
                                        }
                                    }
                                } catch (Throwable e2222) {
                                    params3 = params2;
                                    args2 = strArr;
                                    params = e2222;
                                    FileLog.m3e(params);
                                    a++;
                                    params2 = params3;
                                    i2 = 1;
                                    i4 = 2;
                                }
                            } else {
                                params3 = params2;
                            }
                        } else {
                            params3 = params2;
                            if (args[a].startsWith("use_cipher_signature") != null) {
                                params2 = args[a].split("=");
                                if (params2.length == 2 && params2[1].toLowerCase().equals("true")) {
                                    encrypted = true;
                                }
                            } else if (args[a].startsWith("hlsvp") != null) {
                                params2 = args[a].split("=");
                                if (params2.length == 2) {
                                    try {
                                        hls = URLDecoder.decode(params2[1], C0542C.UTF8_NAME);
                                    } catch (Throwable e22222) {
                                        FileLog.m3e(e22222);
                                    }
                                }
                            } else if (args[a].startsWith("livestream") != null) {
                                params2 = args[a].split("=");
                                if (params2.length == 2 && params2[1].toLowerCase().equals("1")) {
                                    isLive = true;
                                }
                            }
                        }
                        a++;
                        params2 = params3;
                        i2 = 1;
                        i4 = 2;
                    }
                    params3 = params2;
                    otherUrl = otherUrl2;
                } else {
                    params3 = params2;
                }
                if (isLive) {
                    if (!(hls == null || encrypted)) {
                        if (!hls.contains("/s/")) {
                            r1.result[0] = hls;
                            r1.result[1] = "hls";
                        }
                    }
                    return null;
                }
                if (exists) {
                    break;
                }
                i5++;
                params2 = params3;
                strArr = null;
                i2 = 1;
                i3 = 0;
                i4 = 2;
            }
            if (r1.result[0] == null && otherUrl != null) {
                r1.result[0] = otherUrl;
                r1.result[1] = "other";
            }
            if (r1.result[0] != null) {
                String str;
                if (!encrypted) {
                    if (!r1.result[0].contains("/s/")) {
                        str = embedCode2;
                        if (!isCancelled()) {
                            if (encrypted) {
                                strArr2 = r1.result;
                                return strArr2;
                            }
                        }
                        strArr2 = null;
                        return strArr2;
                    }
                }
                if (embedCode2 != null) {
                    encrypted = true;
                    int index = r1.result[0].indexOf("/s/");
                    int index2 = r1.result[0].indexOf(47, index + 10);
                    if (index != -1) {
                        if (index2 == -1) {
                            i2 = 0;
                            index2 = r1.result[0].length();
                        } else {
                            i2 = 0;
                        }
                        r1.sig = r1.result[i2].substring(index, index2);
                        String jsUrl = null;
                        Matcher matcher2 = WebPlayerView.jsPattern.matcher(embedCode2);
                        if (matcher2.find()) {
                            try {
                                Object value = new JSONTokener(matcher2.group(1)).nextValue();
                                if (value instanceof String) {
                                    jsUrl = (String) value;
                                }
                            } catch (Throwable e222222) {
                                FileLog.m3e(e222222);
                            }
                        }
                        if (jsUrl != null) {
                            String playerId;
                            matcher2 = WebPlayerView.playerIdPattern.matcher(jsUrl);
                            if (matcher2.find()) {
                                playerId = new StringBuilder();
                                playerId.append(matcher2.group(1));
                                playerId.append(matcher2.group(2));
                                playerId = playerId.toString();
                            } else {
                                playerId = null;
                            }
                            currentUrl = null;
                            String functionName = null;
                            SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("youtubecode", 0);
                            if (playerId != null) {
                                currentUrl = preferences.getString(playerId, null);
                                StringBuilder stringBuilder5 = new StringBuilder();
                                stringBuilder5.append(playerId);
                                stringBuilder5.append("n");
                                functionName = preferences.getString(stringBuilder5.toString(), null);
                            }
                            if (currentUrl == null) {
                                if (jsUrl.startsWith("//")) {
                                    stringBuilder4 = new StringBuilder();
                                    stringBuilder4.append("https:");
                                    stringBuilder4.append(jsUrl);
                                    jsUrl = stringBuilder4.toString();
                                } else if (jsUrl.startsWith("/")) {
                                    stringBuilder4 = new StringBuilder();
                                    stringBuilder4.append("https://www.youtube.com");
                                    stringBuilder4.append(jsUrl);
                                    jsUrl = stringBuilder4.toString();
                                }
                                String jsCode = WebPlayerView.this.downloadUrlContent(r1, jsUrl);
                                if (isCancelled()) {
                                    return null;
                                }
                                if (jsCode != null) {
                                    matcher2 = WebPlayerView.sigPattern.matcher(jsCode);
                                    if (matcher2.find()) {
                                        functionName = matcher2.group(1);
                                    } else {
                                        matcher2 = WebPlayerView.sigPattern2.matcher(jsCode);
                                        if (matcher2.find()) {
                                            functionName = matcher2.group(1);
                                        }
                                    }
                                    if (functionName != null) {
                                        try {
                                            try {
                                                JSExtractor embedCode3 = new JSExtractor(jsCode);
                                                currentUrl = embedCode3.extractFunction(functionName);
                                                if (TextUtils.isEmpty(currentUrl) || playerId == null) {
                                                } else {
                                                    Editor putString = preferences.edit().putString(playerId, currentUrl);
                                                    JSExtractor extractor = embedCode3;
                                                    embedCode2 = new StringBuilder();
                                                    embedCode2.append(playerId);
                                                    try {
                                                        embedCode2.append("n");
                                                        putString.putString(embedCode2.toString(), functionName).commit();
                                                    } catch (Throwable e2222222) {
                                                        embedCode = e2222222;
                                                        FileLog.m3e(embedCode);
                                                        if (TextUtils.isEmpty(currentUrl) == null) {
                                                            if (VERSION.SDK_INT >= 21) {
                                                                embedCode2 = new StringBuilder();
                                                                embedCode2.append(currentUrl);
                                                                embedCode2.append("window.");
                                                                embedCode2.append(WebPlayerView.this.interfaceName);
                                                                embedCode2.append(".returnResultToJava(");
                                                                embedCode2.append(functionName);
                                                                embedCode2.append("('");
                                                                embedCode2.append(r1.sig.substring(3));
                                                                embedCode2.append("'));");
                                                                embedCode2 = embedCode2.toString();
                                                            } else {
                                                                embedCode2 = new StringBuilder();
                                                                embedCode2.append(currentUrl);
                                                                embedCode2.append(functionName);
                                                                embedCode2.append("('");
                                                                embedCode2.append(r1.sig.substring(3));
                                                                embedCode2.append("');");
                                                                embedCode2 = embedCode2.toString();
                                                            }
                                                            params2 = embedCode2;
                                                            try {
                                                                AndroidUtilities.runOnUIThread(new Runnable() {

                                                                    /* renamed from: org.telegram.ui.Components.WebPlayerView$YoutubeVideoTask$1$1 */
                                                                    class C13481 implements ValueCallback<String> {
                                                                        C13481() {
                                                                        }

                                                                        public void onReceiveValue(String value) {
                                                                            String[] access$1300 = YoutubeVideoTask.this.result;
                                                                            String str = YoutubeVideoTask.this.result[0];
                                                                            CharSequence access$1400 = YoutubeVideoTask.this.sig;
                                                                            StringBuilder stringBuilder = new StringBuilder();
                                                                            stringBuilder.append("/signature/");
                                                                            stringBuilder.append(value.substring(1, value.length() - 1));
                                                                            access$1300[0] = str.replace(access$1400, stringBuilder.toString());
                                                                            YoutubeVideoTask.this.countDownLatch.countDown();
                                                                        }
                                                                    }

                                                                    public void run() {
                                                                        if (VERSION.SDK_INT >= 21) {
                                                                            WebPlayerView.this.webView.evaluateJavascript(params2, new C13481());
                                                                            return;
                                                                        }
                                                                        try {
                                                                            String javascript = new StringBuilder();
                                                                            javascript.append("<script>");
                                                                            javascript.append(params2);
                                                                            javascript.append("</script>");
                                                                            String base64 = Base64.encodeToString(javascript.toString().getBytes(C0542C.UTF8_NAME), null);
                                                                            WebView access$1600 = WebPlayerView.this.webView;
                                                                            StringBuilder stringBuilder = new StringBuilder();
                                                                            stringBuilder.append("data:text/html;charset=utf-8;base64,");
                                                                            stringBuilder.append(base64);
                                                                            access$1600.loadUrl(stringBuilder.toString());
                                                                        } catch (Throwable e) {
                                                                            FileLog.m3e(e);
                                                                        }
                                                                    }
                                                                });
                                                                r1.countDownLatch.await();
                                                                encrypted = false;
                                                            } catch (Throwable e22222222) {
                                                                FileLog.m3e(e22222222);
                                                            }
                                                        }
                                                        if (isCancelled()) {
                                                            if (encrypted) {
                                                                strArr2 = r1.result;
                                                                return strArr2;
                                                            }
                                                        }
                                                        strArr2 = null;
                                                        return strArr2;
                                                    }
                                                }
                                            } catch (Throwable e222222222) {
                                                i = index;
                                                embedCode = e222222222;
                                                FileLog.m3e(embedCode);
                                                if (TextUtils.isEmpty(currentUrl) == null) {
                                                    if (VERSION.SDK_INT >= 21) {
                                                        embedCode2 = new StringBuilder();
                                                        embedCode2.append(currentUrl);
                                                        embedCode2.append(functionName);
                                                        embedCode2.append("('");
                                                        embedCode2.append(r1.sig.substring(3));
                                                        embedCode2.append("');");
                                                        embedCode2 = embedCode2.toString();
                                                    } else {
                                                        embedCode2 = new StringBuilder();
                                                        embedCode2.append(currentUrl);
                                                        embedCode2.append("window.");
                                                        embedCode2.append(WebPlayerView.this.interfaceName);
                                                        embedCode2.append(".returnResultToJava(");
                                                        embedCode2.append(functionName);
                                                        embedCode2.append("('");
                                                        embedCode2.append(r1.sig.substring(3));
                                                        embedCode2.append("'));");
                                                        embedCode2 = embedCode2.toString();
                                                    }
                                                    params2 = embedCode2;
                                                    AndroidUtilities.runOnUIThread(/* anonymous class already generated */);
                                                    r1.countDownLatch.await();
                                                    encrypted = false;
                                                }
                                                if (isCancelled()) {
                                                    if (encrypted) {
                                                        strArr2 = r1.result;
                                                        return strArr2;
                                                    }
                                                }
                                                strArr2 = null;
                                                return strArr2;
                                            }
                                        } catch (Throwable eNUM) {
                                            str = embedCode2;
                                            i = index;
                                            embedCode = eNUM;
                                            FileLog.m3e(embedCode);
                                            if (TextUtils.isEmpty(currentUrl) == null) {
                                                if (VERSION.SDK_INT >= 21) {
                                                    embedCode2 = new StringBuilder();
                                                    embedCode2.append(currentUrl);
                                                    embedCode2.append(functionName);
                                                    embedCode2.append("('");
                                                    embedCode2.append(r1.sig.substring(3));
                                                    embedCode2.append("');");
                                                    embedCode2 = embedCode2.toString();
                                                } else {
                                                    embedCode2 = new StringBuilder();
                                                    embedCode2.append(currentUrl);
                                                    embedCode2.append("window.");
                                                    embedCode2.append(WebPlayerView.this.interfaceName);
                                                    embedCode2.append(".returnResultToJava(");
                                                    embedCode2.append(functionName);
                                                    embedCode2.append("('");
                                                    embedCode2.append(r1.sig.substring(3));
                                                    embedCode2.append("'));");
                                                    embedCode2 = embedCode2.toString();
                                                }
                                                params2 = embedCode2;
                                                AndroidUtilities.runOnUIThread(/* anonymous class already generated */);
                                                r1.countDownLatch.await();
                                                encrypted = false;
                                            }
                                            if (isCancelled()) {
                                                if (encrypted) {
                                                    strArr2 = r1.result;
                                                    return strArr2;
                                                }
                                            }
                                            strArr2 = null;
                                            return strArr2;
                                        }
                                        if (TextUtils.isEmpty(currentUrl) == null) {
                                            if (VERSION.SDK_INT >= 21) {
                                                embedCode2 = new StringBuilder();
                                                embedCode2.append(currentUrl);
                                                embedCode2.append(functionName);
                                                embedCode2.append("('");
                                                embedCode2.append(r1.sig.substring(3));
                                                embedCode2.append("');");
                                                embedCode2 = embedCode2.toString();
                                            } else {
                                                embedCode2 = new StringBuilder();
                                                embedCode2.append(currentUrl);
                                                embedCode2.append("window.");
                                                embedCode2.append(WebPlayerView.this.interfaceName);
                                                embedCode2.append(".returnResultToJava(");
                                                embedCode2.append(functionName);
                                                embedCode2.append("('");
                                                embedCode2.append(r1.sig.substring(3));
                                                embedCode2.append("'));");
                                                embedCode2 = embedCode2.toString();
                                            }
                                            params2 = embedCode2;
                                            AndroidUtilities.runOnUIThread(/* anonymous class already generated */);
                                            r1.countDownLatch.await();
                                            encrypted = false;
                                        }
                                        if (isCancelled()) {
                                            if (encrypted) {
                                                strArr2 = r1.result;
                                                return strArr2;
                                            }
                                        }
                                        strArr2 = null;
                                        return strArr2;
                                    }
                                }
                            }
                            i = index;
                            if (TextUtils.isEmpty(currentUrl) == null) {
                                if (VERSION.SDK_INT >= 21) {
                                    embedCode2 = new StringBuilder();
                                    embedCode2.append(currentUrl);
                                    embedCode2.append("window.");
                                    embedCode2.append(WebPlayerView.this.interfaceName);
                                    embedCode2.append(".returnResultToJava(");
                                    embedCode2.append(functionName);
                                    embedCode2.append("('");
                                    embedCode2.append(r1.sig.substring(3));
                                    embedCode2.append("'));");
                                    embedCode2 = embedCode2.toString();
                                } else {
                                    embedCode2 = new StringBuilder();
                                    embedCode2.append(currentUrl);
                                    embedCode2.append(functionName);
                                    embedCode2.append("('");
                                    embedCode2.append(r1.sig.substring(3));
                                    embedCode2.append("');");
                                    embedCode2 = embedCode2.toString();
                                }
                                params2 = embedCode2;
                                AndroidUtilities.runOnUIThread(/* anonymous class already generated */);
                                r1.countDownLatch.await();
                                encrypted = false;
                            }
                            if (isCancelled()) {
                                if (encrypted) {
                                    strArr2 = r1.result;
                                    return strArr2;
                                }
                            }
                            strArr2 = null;
                            return strArr2;
                        }
                    }
                }
            }
            if (isCancelled()) {
                if (encrypted) {
                    strArr2 = r1.result;
                    return strArr2;
                }
            }
            strArr2 = null;
            return strArr2;
        }

        private void onInterfaceResult(String value) {
            String[] strArr = this.result;
            String str = this.result[0];
            CharSequence charSequence = this.sig;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("/signature/");
            stringBuilder.append(value);
            strArr[0] = str.replace(charSequence, stringBuilder.toString());
            this.countDownLatch.countDown();
        }

        protected void onPostExecute(String[] result) {
            if (result[0] != null) {
                if (BuildVars.LOGS_ENABLED) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("start play youtube video ");
                    stringBuilder.append(result[1]);
                    stringBuilder.append(" ");
                    stringBuilder.append(result[0]);
                    FileLog.m0d(stringBuilder.toString());
                }
                WebPlayerView.this.initied = true;
                WebPlayerView.this.playVideoUrl = result[0];
                WebPlayerView.this.playVideoType = result[1];
                if (WebPlayerView.this.playVideoType.equals("hls")) {
                    WebPlayerView.this.isStream = true;
                }
                if (WebPlayerView.this.isAutoplay) {
                    WebPlayerView.this.preparePlayer();
                }
                WebPlayerView.this.showProgress(false, true);
                WebPlayerView.this.controlsView.show(true, true);
            } else if (!isCancelled()) {
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

        public void jsCallFinished(String value) {
            if (WebPlayerView.this.currentTask != null && !WebPlayerView.this.currentTask.isCancelled() && (WebPlayerView.this.currentTask instanceof YoutubeVideoTask)) {
                ((YoutubeVideoTask) WebPlayerView.this.currentTask).onInterfaceResult(value);
            }
        }
    }

    protected String downloadUrlContent(AsyncTask parentTask, String url) {
        return downloadUrlContent(parentTask, url, null, true);
    }

    protected String downloadUrlContent(AsyncTask parentTask, String url, HashMap<String, String> headers, boolean tryGzip) {
        Throwable e;
        Throwable th;
        boolean canRetry;
        int code;
        byte[] data;
        int read;
        StringBuilder result;
        boolean canRetry2 = true;
        InputStream httpConnectionStream = null;
        boolean done = false;
        StringBuilder result2 = null;
        URLConnection httpConnection = null;
        try {
            URL downloadUrl = new URL(url);
            httpConnection = downloadUrl.openConnection();
            httpConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:10.0) Gecko/20150101 Firefox/47.0 (Chrome)");
            if (tryGzip) {
                try {
                    httpConnection.addRequestProperty("Accept-Encoding", "gzip, deflate");
                } catch (Throwable th2) {
                    e = th2;
                    canRetry = true;
                    if (!(e instanceof SocketTimeoutException)) {
                        if (ConnectionsManager.isNetworkOnline()) {
                            canRetry2 = false;
                        }
                        canRetry2 = canRetry;
                        FileLog.m3e(e);
                        if (canRetry2) {
                            if (httpConnection != null) {
                                try {
                                    if (httpConnection instanceof HttpURLConnection) {
                                        code = ((HttpURLConnection) httpConnection).getResponseCode();
                                    }
                                } catch (Throwable th22) {
                                    FileLog.m3e(th22);
                                }
                            }
                            if (httpConnectionStream != null) {
                                try {
                                    data = new byte[32768];
                                    while (!parentTask.isCancelled()) {
                                        try {
                                            read = httpConnectionStream.read(data);
                                            if (read > 0) {
                                                if (result2 == null) {
                                                    result2 = new StringBuilder();
                                                }
                                                result2.append(new String(data, 0, read, C0542C.UTF8_NAME));
                                            } else if (read == -1) {
                                                done = true;
                                            }
                                        } catch (Throwable th222) {
                                            result = result2;
                                            FileLog.m3e(th222);
                                            result2 = result;
                                        } catch (Throwable th2222) {
                                            e = th2222;
                                            result2 = result;
                                            FileLog.m3e(e);
                                            if (httpConnectionStream != null) {
                                                try {
                                                    httpConnectionStream.close();
                                                } catch (Throwable th22222) {
                                                    FileLog.m3e(th22222);
                                                }
                                            }
                                            return done ? null : result2.toString();
                                        }
                                    }
                                } catch (Throwable th222222) {
                                    e = th222222;
                                    FileLog.m3e(e);
                                    if (httpConnectionStream != null) {
                                        httpConnectionStream.close();
                                    }
                                    if (done) {
                                    }
                                }
                            }
                            if (httpConnectionStream != null) {
                                httpConnectionStream.close();
                            }
                        }
                        if (done) {
                        }
                    } else if (!(e instanceof UnknownHostException)) {
                        canRetry2 = false;
                    } else if (e instanceof SocketException) {
                        canRetry2 = false;
                    } else {
                        if (e instanceof FileNotFoundException) {
                            canRetry2 = false;
                        }
                        canRetry2 = canRetry;
                        FileLog.m3e(e);
                        if (canRetry2) {
                            if (httpConnection != null) {
                                if (httpConnection instanceof HttpURLConnection) {
                                    code = ((HttpURLConnection) httpConnection).getResponseCode();
                                }
                            }
                            if (httpConnectionStream != null) {
                                data = new byte[32768];
                                while (!parentTask.isCancelled()) {
                                    read = httpConnectionStream.read(data);
                                    if (read > 0) {
                                        if (result2 == null) {
                                            result2 = new StringBuilder();
                                        }
                                        result2.append(new String(data, 0, read, C0542C.UTF8_NAME));
                                    } else if (read == -1) {
                                        done = true;
                                    }
                                }
                            }
                            if (httpConnectionStream != null) {
                                httpConnectionStream.close();
                            }
                        }
                        if (done) {
                        }
                    }
                    FileLog.m3e(e);
                    if (canRetry2) {
                        if (httpConnection != null) {
                            if (httpConnection instanceof HttpURLConnection) {
                                code = ((HttpURLConnection) httpConnection).getResponseCode();
                            }
                        }
                        if (httpConnectionStream != null) {
                            data = new byte[32768];
                            while (!parentTask.isCancelled()) {
                                read = httpConnectionStream.read(data);
                                if (read > 0) {
                                    if (result2 == null) {
                                        result2 = new StringBuilder();
                                    }
                                    result2.append(new String(data, 0, read, C0542C.UTF8_NAME));
                                } else if (read == -1) {
                                    done = true;
                                }
                            }
                        }
                        if (httpConnectionStream != null) {
                            httpConnectionStream.close();
                        }
                    }
                    if (done) {
                    }
                }
            }
            try {
                InputStream inputStream;
                httpConnection.addRequestProperty("Accept-Language", "en-us,en;q=0.5");
                httpConnection.addRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
                httpConnection.addRequestProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
                if (headers != null) {
                    for (Entry<String, String> entry : headers.entrySet()) {
                        httpConnection.addRequestProperty((String) entry.getKey(), (String) entry.getValue());
                    }
                }
                httpConnection.setConnectTimeout(DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS);
                httpConnection.setReadTimeout(DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS);
                if (httpConnection instanceof HttpURLConnection) {
                    HttpURLConnection httpURLConnection = (HttpURLConnection) httpConnection;
                    httpURLConnection.setInstanceFollowRedirects(true);
                    int status = httpURLConnection.getResponseCode();
                    if (!(status == 302 || status == 301)) {
                        if (status != 303) {
                            canRetry = true;
                            httpConnection.connect();
                            if (tryGzip) {
                                inputStream = httpConnection.getInputStream();
                            } else {
                                inputStream = new GZIPInputStream(httpConnection.getInputStream());
                            }
                            httpConnectionStream = inputStream;
                            canRetry2 = canRetry;
                            if (canRetry2) {
                                if (httpConnection != null) {
                                    if (httpConnection instanceof HttpURLConnection) {
                                        code = ((HttpURLConnection) httpConnection).getResponseCode();
                                        if (!(code == Callback.DEFAULT_DRAG_ANIMATION_DURATION || code == 202)) {
                                        }
                                    }
                                }
                                if (httpConnectionStream != null) {
                                    data = new byte[32768];
                                    while (!parentTask.isCancelled()) {
                                        read = httpConnectionStream.read(data);
                                        if (read > 0) {
                                            if (result2 == null) {
                                                result2 = new StringBuilder();
                                            }
                                            result2.append(new String(data, 0, read, C0542C.UTF8_NAME));
                                        } else if (read == -1) {
                                            done = true;
                                        }
                                    }
                                }
                                if (httpConnectionStream != null) {
                                    httpConnectionStream.close();
                                }
                            }
                            if (done) {
                            }
                        }
                    }
                    String newUrl = httpURLConnection.getHeaderField("Location");
                    String cookies = httpURLConnection.getHeaderField("Set-Cookie");
                    downloadUrl = new URL(newUrl);
                    httpConnection = downloadUrl.openConnection();
                    httpConnection.setRequestProperty("Cookie", cookies);
                    httpConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:10.0) Gecko/20150101 Firefox/47.0 (Chrome)");
                    if (tryGzip) {
                        httpConnection.addRequestProperty("Accept-Encoding", "gzip, deflate");
                    }
                    httpConnection.addRequestProperty("Accept-Language", "en-us,en;q=0.5");
                    httpConnection.addRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
                    httpConnection.addRequestProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
                    if (headers != null) {
                        for (Entry<String, String> entry2 : headers.entrySet()) {
                            canRetry = canRetry2;
                            try {
                                httpConnection.addRequestProperty((String) entry2.getKey(), (String) entry2.getValue());
                                canRetry2 = canRetry;
                            } catch (Exception e2) {
                                if (httpConnectionStream != null) {
                                    try {
                                        httpConnectionStream.close();
                                    } catch (Exception e3) {
                                        httpConnection = downloadUrl.openConnection();
                                        httpConnection.connect();
                                        inputStream = httpConnection.getInputStream();
                                        httpConnectionStream = inputStream;
                                        canRetry2 = canRetry;
                                        if (canRetry2) {
                                            if (httpConnection != null) {
                                                if (httpConnection instanceof HttpURLConnection) {
                                                    code = ((HttpURLConnection) httpConnection).getResponseCode();
                                                }
                                            }
                                            if (httpConnectionStream != null) {
                                                data = new byte[32768];
                                                while (!parentTask.isCancelled()) {
                                                    read = httpConnectionStream.read(data);
                                                    if (read > 0) {
                                                        if (result2 == null) {
                                                            result2 = new StringBuilder();
                                                        }
                                                        result2.append(new String(data, 0, read, C0542C.UTF8_NAME));
                                                    } else if (read == -1) {
                                                        done = true;
                                                    }
                                                }
                                            }
                                            if (httpConnectionStream != null) {
                                                httpConnectionStream.close();
                                            }
                                        }
                                        if (done) {
                                        }
                                    }
                                }
                                httpConnection = downloadUrl.openConnection();
                                httpConnection.connect();
                                inputStream = httpConnection.getInputStream();
                            } catch (Throwable th2222222) {
                                e = th2222222;
                            }
                        }
                    }
                }
                canRetry = canRetry2;
                httpConnection.connect();
                if (tryGzip) {
                    inputStream = httpConnection.getInputStream();
                } else {
                    inputStream = new GZIPInputStream(httpConnection.getInputStream());
                }
                httpConnectionStream = inputStream;
                canRetry2 = canRetry;
            } catch (Throwable th3) {
                th2222222 = th3;
                canRetry = true;
                e = th2222222;
                if (!(e instanceof SocketTimeoutException)) {
                    if (ConnectionsManager.isNetworkOnline()) {
                        canRetry2 = false;
                    }
                    canRetry2 = canRetry;
                    FileLog.m3e(e);
                    if (canRetry2) {
                        if (httpConnection != null) {
                            if (httpConnection instanceof HttpURLConnection) {
                                code = ((HttpURLConnection) httpConnection).getResponseCode();
                            }
                        }
                        if (httpConnectionStream != null) {
                            data = new byte[32768];
                            while (!parentTask.isCancelled()) {
                                read = httpConnectionStream.read(data);
                                if (read > 0) {
                                    if (result2 == null) {
                                        result2 = new StringBuilder();
                                    }
                                    result2.append(new String(data, 0, read, C0542C.UTF8_NAME));
                                } else if (read == -1) {
                                    done = true;
                                }
                            }
                        }
                        if (httpConnectionStream != null) {
                            httpConnectionStream.close();
                        }
                    }
                    if (done) {
                    }
                } else if (!(e instanceof UnknownHostException)) {
                    canRetry2 = false;
                } else if (e instanceof SocketException) {
                    if (e.getMessage() != null && e.getMessage().contains("ECONNRESET")) {
                        canRetry2 = false;
                    }
                    canRetry2 = canRetry;
                    FileLog.m3e(e);
                    if (canRetry2) {
                        if (httpConnection != null) {
                            if (httpConnection instanceof HttpURLConnection) {
                                code = ((HttpURLConnection) httpConnection).getResponseCode();
                            }
                        }
                        if (httpConnectionStream != null) {
                            data = new byte[32768];
                            while (!parentTask.isCancelled()) {
                                read = httpConnectionStream.read(data);
                                if (read > 0) {
                                    if (result2 == null) {
                                        result2 = new StringBuilder();
                                    }
                                    result2.append(new String(data, 0, read, C0542C.UTF8_NAME));
                                } else if (read == -1) {
                                    done = true;
                                }
                            }
                        }
                        if (httpConnectionStream != null) {
                            httpConnectionStream.close();
                        }
                    }
                    if (done) {
                    }
                } else {
                    if (e instanceof FileNotFoundException) {
                        canRetry2 = false;
                    }
                    canRetry2 = canRetry;
                    FileLog.m3e(e);
                    if (canRetry2) {
                        if (httpConnection != null) {
                            if (httpConnection instanceof HttpURLConnection) {
                                code = ((HttpURLConnection) httpConnection).getResponseCode();
                            }
                        }
                        if (httpConnectionStream != null) {
                            data = new byte[32768];
                            while (!parentTask.isCancelled()) {
                                read = httpConnectionStream.read(data);
                                if (read > 0) {
                                    if (result2 == null) {
                                        result2 = new StringBuilder();
                                    }
                                    result2.append(new String(data, 0, read, C0542C.UTF8_NAME));
                                } else if (read == -1) {
                                    done = true;
                                }
                            }
                        }
                        if (httpConnectionStream != null) {
                            httpConnectionStream.close();
                        }
                    }
                    if (done) {
                    }
                }
                FileLog.m3e(e);
                if (canRetry2) {
                    if (httpConnection != null) {
                        if (httpConnection instanceof HttpURLConnection) {
                            code = ((HttpURLConnection) httpConnection).getResponseCode();
                        }
                    }
                    if (httpConnectionStream != null) {
                        data = new byte[32768];
                        while (!parentTask.isCancelled()) {
                            read = httpConnectionStream.read(data);
                            if (read > 0) {
                                if (result2 == null) {
                                    result2 = new StringBuilder();
                                }
                                result2.append(new String(data, 0, read, C0542C.UTF8_NAME));
                            } else if (read == -1) {
                                done = true;
                            }
                        }
                    }
                    if (httpConnectionStream != null) {
                        httpConnectionStream.close();
                    }
                }
                if (done) {
                }
            }
        } catch (Throwable th4) {
            th2222222 = th4;
            String str = url;
            canRetry = true;
            e = th2222222;
            if (!(e instanceof SocketTimeoutException)) {
                if (ConnectionsManager.isNetworkOnline()) {
                    canRetry2 = false;
                }
                canRetry2 = canRetry;
                FileLog.m3e(e);
                if (canRetry2) {
                    if (httpConnection != null) {
                        if (httpConnection instanceof HttpURLConnection) {
                            code = ((HttpURLConnection) httpConnection).getResponseCode();
                        }
                    }
                    if (httpConnectionStream != null) {
                        data = new byte[32768];
                        while (!parentTask.isCancelled()) {
                            read = httpConnectionStream.read(data);
                            if (read > 0) {
                                if (result2 == null) {
                                    result2 = new StringBuilder();
                                }
                                result2.append(new String(data, 0, read, C0542C.UTF8_NAME));
                            } else if (read == -1) {
                                done = true;
                            }
                        }
                    }
                    if (httpConnectionStream != null) {
                        httpConnectionStream.close();
                    }
                }
                if (done) {
                }
            } else if (!(e instanceof UnknownHostException)) {
                canRetry2 = false;
            } else if (e instanceof SocketException) {
                if (e instanceof FileNotFoundException) {
                    canRetry2 = false;
                }
                canRetry2 = canRetry;
                FileLog.m3e(e);
                if (canRetry2) {
                    if (httpConnection != null) {
                        if (httpConnection instanceof HttpURLConnection) {
                            code = ((HttpURLConnection) httpConnection).getResponseCode();
                        }
                    }
                    if (httpConnectionStream != null) {
                        data = new byte[32768];
                        while (!parentTask.isCancelled()) {
                            read = httpConnectionStream.read(data);
                            if (read > 0) {
                                if (result2 == null) {
                                    result2 = new StringBuilder();
                                }
                                result2.append(new String(data, 0, read, C0542C.UTF8_NAME));
                            } else if (read == -1) {
                                done = true;
                            }
                        }
                    }
                    if (httpConnectionStream != null) {
                        httpConnectionStream.close();
                    }
                }
                if (done) {
                }
            } else {
                canRetry2 = false;
            }
            FileLog.m3e(e);
            if (canRetry2) {
                if (httpConnection != null) {
                    if (httpConnection instanceof HttpURLConnection) {
                        code = ((HttpURLConnection) httpConnection).getResponseCode();
                    }
                }
                if (httpConnectionStream != null) {
                    data = new byte[32768];
                    while (!parentTask.isCancelled()) {
                        read = httpConnectionStream.read(data);
                        if (read > 0) {
                            if (result2 == null) {
                                result2 = new StringBuilder();
                            }
                            result2.append(new String(data, 0, read, C0542C.UTF8_NAME));
                        } else if (read == -1) {
                            done = true;
                        }
                    }
                }
                if (httpConnectionStream != null) {
                    httpConnectionStream.close();
                }
            }
            if (done) {
            }
        }
        if (canRetry2) {
            if (httpConnection != null) {
                if (httpConnection instanceof HttpURLConnection) {
                    code = ((HttpURLConnection) httpConnection).getResponseCode();
                }
            }
            if (httpConnectionStream != null) {
                data = new byte[32768];
                while (!parentTask.isCancelled()) {
                    read = httpConnectionStream.read(data);
                    if (read > 0) {
                        if (result2 == null) {
                            result2 = new StringBuilder();
                        }
                        result2.append(new String(data, 0, read, C0542C.UTF8_NAME));
                    } else if (read == -1) {
                        done = true;
                    }
                }
            }
            if (httpConnectionStream != null) {
                httpConnectionStream.close();
            }
        }
        if (done) {
        }
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    public WebPlayerView(Context context, boolean allowInline, boolean allowShare, WebPlayerViewDelegate webPlayerViewDelegate) {
        Context context2 = context;
        super(context);
        int i = lastContainerId;
        lastContainerId = i + 1;
        this.fragment_container_id = i;
        r0.allowInlineAnimation = VERSION.SDK_INT >= 21;
        r0.backgroundPaint = new Paint();
        r0.progressRunnable = new C13361();
        r0.surfaceTextureListener = new C13392();
        r0.switchToInlineRunnable = new C13403();
        setWillNotDraw(false);
        r0.delegate = webPlayerViewDelegate;
        r0.backgroundPaint.setColor(Theme.ACTION_BAR_VIDEO_EDIT_COLOR);
        r0.aspectRatioFrameLayout = new AspectRatioFrameLayout(context2) {
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                if (WebPlayerView.this.textureViewContainer != null) {
                    LayoutParams layoutParams = WebPlayerView.this.textureView.getLayoutParams();
                    layoutParams.width = getMeasuredWidth();
                    layoutParams.height = getMeasuredHeight();
                    if (WebPlayerView.this.textureImageView != null) {
                        layoutParams = WebPlayerView.this.textureImageView.getLayoutParams();
                        layoutParams.width = getMeasuredWidth();
                        layoutParams.height = getMeasuredHeight();
                    }
                }
            }
        };
        addView(r0.aspectRatioFrameLayout, LayoutHelper.createFrame(-1, -1, 17));
        r0.interfaceName = "JavaScriptInterface";
        r0.webView = new WebView(context2);
        r0.webView.addJavascriptInterface(new JavaScriptInterface(new C21045()), r0.interfaceName);
        WebSettings webSettings = r0.webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDefaultTextEncodingName("utf-8");
        r0.textureViewContainer = r0.delegate.getTextureViewContainer();
        r0.textureView = new TextureView(context2);
        r0.textureView.setPivotX(0.0f);
        r0.textureView.setPivotY(0.0f);
        if (r0.textureViewContainer != null) {
            r0.textureViewContainer.addView(r0.textureView);
        } else {
            r0.aspectRatioFrameLayout.addView(r0.textureView, LayoutHelper.createFrame(-1, -1, 17));
        }
        if (r0.allowInlineAnimation && r0.textureViewContainer != null) {
            r0.textureImageView = new ImageView(context2);
            r0.textureImageView.setBackgroundColor(-65536);
            r0.textureImageView.setPivotX(0.0f);
            r0.textureImageView.setPivotY(0.0f);
            r0.textureImageView.setVisibility(4);
            r0.textureViewContainer.addView(r0.textureImageView);
        }
        r0.videoPlayer = new VideoPlayer();
        r0.videoPlayer.setDelegate(r0);
        r0.videoPlayer.setTextureView(r0.textureView);
        r0.controlsView = new ControlsView(context2);
        if (r0.textureViewContainer != null) {
            r0.textureViewContainer.addView(r0.controlsView);
        } else {
            addView(r0.controlsView, LayoutHelper.createFrame(-1, -1.0f));
        }
        r0.progressView = new RadialProgressView(context2);
        r0.progressView.setProgressColor(-1);
        addView(r0.progressView, LayoutHelper.createFrame(48, 48, 17));
        r0.fullscreenButton = new ImageView(context2);
        r0.fullscreenButton.setScaleType(ScaleType.CENTER);
        r0.controlsView.addView(r0.fullscreenButton, LayoutHelper.createFrame(56, 56.0f, 85, 0.0f, 0.0f, 0.0f, 5.0f));
        r0.fullscreenButton.setOnClickListener(new C13416());
        r0.playButton = new ImageView(context2);
        r0.playButton.setScaleType(ScaleType.CENTER);
        r0.controlsView.addView(r0.playButton, LayoutHelper.createFrame(48, 48, 17));
        r0.playButton.setOnClickListener(new C13427());
        if (allowInline) {
            r0.inlineButton = new ImageView(context2);
            r0.inlineButton.setScaleType(ScaleType.CENTER);
            r0.controlsView.addView(r0.inlineButton, LayoutHelper.createFrame(56, 48, 53));
            r0.inlineButton.setOnClickListener(new C13438());
        }
        if (allowShare) {
            r0.shareButton = new ImageView(context2);
            r0.shareButton.setScaleType(ScaleType.CENTER);
            r0.shareButton.setImageResource(R.drawable.ic_share_video);
            r0.controlsView.addView(r0.shareButton, LayoutHelper.createFrame(56, 48, 53));
            r0.shareButton.setOnClickListener(new C13449());
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
            } catch (Throwable e) {
                if (this.currentBitmap != null) {
                    this.currentBitmap.recycle();
                    this.currentBitmap = null;
                }
                FileLog.m3e(e);
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

    public void onStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState != 2) {
            if (this.videoPlayer.getDuration() != C0542C.TIME_UNSET) {
                this.controlsView.setDuration((int) (this.videoPlayer.getDuration() / 1000));
            } else {
                this.controlsView.setDuration(0);
            }
        }
        if (playbackState == 4 || playbackState == 1 || !this.videoPlayer.isPlaying()) {
            this.delegate.onPlayStateChanged(this, false);
        } else {
            this.delegate.onPlayStateChanged(this, true);
        }
        if (this.videoPlayer.isPlaying() && playbackState != 4) {
            updatePlayButton();
        } else if (playbackState == 4) {
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

    public void onError(Exception e) {
        FileLog.m3e((Throwable) e);
        onInitFailed();
    }

    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        if (this.aspectRatioFrameLayout != null) {
            if (unappliedRotationDegrees == 90 || unappliedRotationDegrees == 270) {
                int temp = width;
                width = height;
                height = temp;
            }
            float ratio = height == 0 ? 1.0f : (((float) width) * pixelWidthHeightRatio) / ((float) height);
            this.aspectRatioFrameLayout.setAspectRatio(ratio, unappliedRotationDegrees);
            if (this.inFullscreen) {
                this.delegate.onVideoSizeChanged(ratio, unappliedRotationDegrees);
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

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int x = ((r - l) - this.aspectRatioFrameLayout.getMeasuredWidth()) / 2;
        int y = (((b - t) - AndroidUtilities.dp(10.0f)) - this.aspectRatioFrameLayout.getMeasuredHeight()) / 2;
        this.aspectRatioFrameLayout.layout(x, y, this.aspectRatioFrameLayout.getMeasuredWidth() + x, this.aspectRatioFrameLayout.getMeasuredHeight() + y);
        if (this.controlsView.getParent() == this) {
            this.controlsView.layout(0, 0, this.controlsView.getMeasuredWidth(), this.controlsView.getMeasuredHeight());
        }
        int x2 = ((r - l) - this.progressView.getMeasuredWidth()) / 2;
        x = ((b - t) - this.progressView.getMeasuredHeight()) / 2;
        this.progressView.layout(x2, x, this.progressView.getMeasuredWidth() + x2, this.progressView.getMeasuredHeight() + x);
        this.controlsView.imageReceiver.setImageCoords(0, 0, getMeasuredWidth(), getMeasuredHeight() - AndroidUtilities.dp(10.0f));
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        this.aspectRatioFrameLayout.measure(MeasureSpec.makeMeasureSpec(width, NUM), MeasureSpec.makeMeasureSpec(height - AndroidUtilities.dp(10.0f), NUM));
        if (this.controlsView.getParent() == this) {
            this.controlsView.measure(MeasureSpec.makeMeasureSpec(width, NUM), MeasureSpec.makeMeasureSpec(height, NUM));
        }
        this.progressView.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(44.0f), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(44.0f), NUM));
        setMeasuredDimension(width, height);
    }

    private void updatePlayButton() {
        this.controlsView.checkNeedHide();
        AndroidUtilities.cancelRunOnUIThread(this.progressRunnable);
        if (this.videoPlayer.isPlaying()) {
            this.playButton.setImageResource(this.isInline ? R.drawable.ic_pauseinline : R.drawable.ic_pause);
            AndroidUtilities.runOnUIThread(this.progressRunnable, 500);
            checkAudioFocus();
        } else if (this.isCompleted) {
            this.playButton.setImageResource(this.isInline ? R.drawable.ic_againinline : R.drawable.ic_again);
        } else {
            this.playButton.setImageResource(this.isInline ? R.drawable.ic_playinline : R.drawable.ic_play);
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

    public void onAudioFocusChange(int focusChange) {
        if (focusChange == -1) {
            if (this.videoPlayer.isPlaying()) {
                this.videoPlayer.pause();
                updatePlayButton();
            }
            this.hasAudioFocus = false;
            this.audioFocus = 0;
        } else if (focusChange == 1) {
            this.audioFocus = 2;
            if (this.resumeAudioOnFocusGain) {
                this.resumeAudioOnFocusGain = false;
                this.videoPlayer.play();
            }
        } else if (focusChange == -3) {
            this.audioFocus = 1;
        } else if (focusChange == -2) {
            this.audioFocus = 0;
            if (this.videoPlayer.isPlaying()) {
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
                    this.fullscreenButton.setImageResource(R.drawable.ic_outfullscreen);
                    this.fullscreenButton.setLayoutParams(LayoutHelper.createFrame(56, 56.0f, 85, 0.0f, 0.0f, 0.0f, 1.0f));
                } else {
                    this.fullscreenButton.setImageResource(R.drawable.ic_gofullscreen);
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
            this.inlineButton.setImageResource(this.isInline ? R.drawable.ic_goinline : R.drawable.ic_outinline);
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

    private void updateFullscreenState(boolean byButton) {
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
                    ViewGroup parent = (ViewGroup) this.controlsView.getParent();
                    if (parent != this) {
                        if (parent != null) {
                            parent.removeView(this.controlsView);
                        }
                        if (this.textureViewContainer != null) {
                            this.textureViewContainer.addView(this.controlsView);
                        } else {
                            addView(this.controlsView, 1);
                        }
                    }
                }
                this.changedTextureView = this.delegate.onSwitchToFullscreen(this.controlsView, this.inFullscreen, this.aspectRatioFrameLayout.getAspectRatio(), this.aspectRatioFrameLayout.getVideoRotation(), byButton);
                this.changedTextureView.setVisibility(4);
                if (this.inFullscreen && this.changedTextureView != null) {
                    viewGroup = (ViewGroup) this.textureView.getParent();
                    if (viewGroup != null) {
                        viewGroup.removeView(this.textureView);
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
                this.delegate.onSwitchToFullscreen(this.controlsView, this.inFullscreen, this.aspectRatioFrameLayout.getAspectRatio(), this.aspectRatioFrameLayout.getVideoRotation(), byButton);
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

    public boolean loadVideo(String url, Photo thumb, String originalUrl, boolean autoplay) {
        String str;
        Throwable e;
        Throwable youtubeId;
        Matcher matcher;
        Matcher matcher2;
        String id;
        PhotoSize photoSize;
        YoutubeVideoTask task;
        VimeoVideoTask task2;
        CoubVideoTask task3;
        AparatVideoTask task4;
        TwitchClipVideoTask task5;
        TwitchStreamVideoTask task6;
        Executor executor;
        Void[] voidArr;
        boolean z;
        String str2 = url;
        Photo photo = thumb;
        String youtubeId2 = null;
        String vimeoId = null;
        String coubId = null;
        String twitchClipId = null;
        String twitchStreamId = null;
        String mp4File = null;
        String aparatId = null;
        this.seekToTime = -1;
        if (str2 == null) {
            str = null;
        } else if (str2.endsWith(".mp4")) {
            mp4File = str2;
        } else {
            String t;
            if (originalUrl != null) {
                try {
                    Uri uri = Uri.parse(originalUrl);
                    t = uri.getQueryParameter("t");
                    if (t == null) {
                        try {
                            t = uri.getQueryParameter("time_continue");
                        } catch (Exception e2) {
                            e = e2;
                            str = null;
                            youtubeId = e;
                            try {
                                FileLog.m3e(youtubeId);
                                matcher = youtubeIdRegex.matcher(str2);
                                t = null;
                                if (matcher.find()) {
                                    t = matcher.group(1);
                                }
                                if (t != null) {
                                    str = t;
                                }
                            } catch (Throwable e3) {
                                FileLog.m3e(e3);
                            }
                            youtubeId2 = str;
                            if (youtubeId2 == null) {
                                try {
                                    matcher2 = vimeoIdRegex.matcher(str2);
                                    id = null;
                                    if (matcher2.find()) {
                                        id = matcher2.group(3);
                                    }
                                    if (id != null) {
                                        vimeoId = id;
                                    }
                                } catch (Throwable e32) {
                                    FileLog.m3e(e32);
                                }
                            }
                            if (vimeoId == null) {
                                try {
                                    matcher2 = aparatIdRegex.matcher(str2);
                                    id = null;
                                    if (matcher2.find()) {
                                        id = matcher2.group(1);
                                    }
                                    if (id != null) {
                                        aparatId = id;
                                    }
                                } catch (Throwable e322) {
                                    FileLog.m3e(e322);
                                }
                            }
                            if (aparatId == null) {
                                try {
                                    matcher2 = twitchClipIdRegex.matcher(str2);
                                    id = null;
                                    if (matcher2.find()) {
                                        id = matcher2.group(1);
                                    }
                                    if (id != null) {
                                        twitchClipId = id;
                                    }
                                } catch (Throwable e3222) {
                                    FileLog.m3e(e3222);
                                }
                            }
                            if (twitchClipId == null) {
                                try {
                                    matcher2 = twitchStreamIdRegex.matcher(str2);
                                    id = null;
                                    if (matcher2.find()) {
                                        id = matcher2.group(1);
                                    }
                                    if (id != null) {
                                        twitchStreamId = id;
                                    }
                                } catch (Throwable e32222) {
                                    FileLog.m3e(e32222);
                                }
                            }
                            if (twitchStreamId == null) {
                                try {
                                    matcher2 = coubIdRegex.matcher(str2);
                                    id = null;
                                    if (matcher2.find()) {
                                        id = matcher2.group(1);
                                    }
                                    if (id != null) {
                                        coubId = id;
                                    }
                                } catch (Throwable e322222) {
                                    FileLog.m3e(e322222);
                                }
                            }
                            r1.initied = false;
                            r1.isCompleted = false;
                            r1.isAutoplay = autoplay;
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
                            if (photo == null) {
                                photoSize = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 80, true);
                                if (photoSize != null) {
                                    r1.controlsView.imageReceiver.setImage(null, null, photo == null ? photoSize.location : null, photo == null ? "80_80_b" : null, 0, null, 1);
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
                            r1.currentYoutubeId = youtubeId2;
                            youtubeId2 = null;
                            if (mp4File == null) {
                                r1.initied = true;
                                r1.playVideoUrl = mp4File;
                                r1.playVideoType = "other";
                                if (r1.isAutoplay) {
                                    preparePlayer();
                                }
                                showProgress(false, false);
                                r1.controlsView.show(true, true);
                            } else {
                                if (youtubeId2 == null) {
                                    task = new YoutubeVideoTask(youtubeId2);
                                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = task;
                                } else if (vimeoId == null) {
                                    task2 = new VimeoVideoTask(vimeoId);
                                    task2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = task2;
                                } else if (coubId == null) {
                                    task3 = new CoubVideoTask(coubId);
                                    task3.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = task3;
                                    r1.isStream = true;
                                } else if (aparatId == null) {
                                    task4 = new AparatVideoTask(aparatId);
                                    task4.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = task4;
                                } else if (twitchClipId == null) {
                                    task5 = new TwitchClipVideoTask(str2, twitchClipId);
                                    task5.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = task5;
                                } else if (twitchStreamId != null) {
                                    task6 = new TwitchStreamVideoTask(str2, twitchStreamId);
                                    executor = AsyncTask.THREAD_POOL_EXECUTOR;
                                    voidArr = new Void[3];
                                    z = true;
                                    voidArr[1] = null;
                                    voidArr[2] = null;
                                    task6.executeOnExecutor(executor, voidArr);
                                    r1.currentTask = task6;
                                    r1.isStream = true;
                                    r1.controlsView.show(false, false);
                                    showProgress(z, false);
                                }
                                z = true;
                                r1.controlsView.show(false, false);
                                showProgress(z, false);
                            }
                            if (twitchStreamId != null) {
                                r1.controlsView.setVisibility(0);
                                return true;
                            }
                            r1.controlsView.setVisibility(8);
                            return false;
                        }
                    }
                    if (t == null) {
                        str = null;
                    } else if (t.contains("m")) {
                        String[] args = t.split("m");
                        str = null;
                        try {
                            r1.seekToTime = (Utilities.parseInt(args[0]).intValue() * 60) + Utilities.parseInt(args[1]).intValue();
                        } catch (Exception e4) {
                            e322222 = e4;
                            youtubeId = e322222;
                            FileLog.m3e(youtubeId);
                            matcher = youtubeIdRegex.matcher(str2);
                            t = null;
                            if (matcher.find()) {
                                t = matcher.group(1);
                            }
                            if (t != null) {
                                str = t;
                            }
                            youtubeId2 = str;
                            if (youtubeId2 == null) {
                                matcher2 = vimeoIdRegex.matcher(str2);
                                id = null;
                                if (matcher2.find()) {
                                    id = matcher2.group(3);
                                }
                                if (id != null) {
                                    vimeoId = id;
                                }
                            }
                            if (vimeoId == null) {
                                matcher2 = aparatIdRegex.matcher(str2);
                                id = null;
                                if (matcher2.find()) {
                                    id = matcher2.group(1);
                                }
                                if (id != null) {
                                    aparatId = id;
                                }
                            }
                            if (aparatId == null) {
                                matcher2 = twitchClipIdRegex.matcher(str2);
                                id = null;
                                if (matcher2.find()) {
                                    id = matcher2.group(1);
                                }
                                if (id != null) {
                                    twitchClipId = id;
                                }
                            }
                            if (twitchClipId == null) {
                                matcher2 = twitchStreamIdRegex.matcher(str2);
                                id = null;
                                if (matcher2.find()) {
                                    id = matcher2.group(1);
                                }
                                if (id != null) {
                                    twitchStreamId = id;
                                }
                            }
                            if (twitchStreamId == null) {
                                matcher2 = coubIdRegex.matcher(str2);
                                id = null;
                                if (matcher2.find()) {
                                    id = matcher2.group(1);
                                }
                                if (id != null) {
                                    coubId = id;
                                }
                            }
                            r1.initied = false;
                            r1.isCompleted = false;
                            r1.isAutoplay = autoplay;
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
                            if (photo == null) {
                                r1.drawImage = false;
                            } else {
                                photoSize = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 80, true);
                                if (photoSize != null) {
                                    if (photo == null) {
                                    }
                                    if (photo == null) {
                                    }
                                    r1.controlsView.imageReceiver.setImage(null, null, photo == null ? photoSize.location : null, photo == null ? "80_80_b" : null, 0, null, 1);
                                    r1.drawImage = true;
                                }
                            }
                            if (r1.progressAnimation != null) {
                                r1.progressAnimation.cancel();
                                r1.progressAnimation = null;
                            }
                            r1.isLoading = true;
                            r1.controlsView.setProgress(0);
                            r1.currentYoutubeId = youtubeId2;
                            youtubeId2 = null;
                            if (mp4File == null) {
                                if (youtubeId2 == null) {
                                    task = new YoutubeVideoTask(youtubeId2);
                                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = task;
                                } else if (vimeoId == null) {
                                    task2 = new VimeoVideoTask(vimeoId);
                                    task2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = task2;
                                } else if (coubId == null) {
                                    task3 = new CoubVideoTask(coubId);
                                    task3.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = task3;
                                    r1.isStream = true;
                                } else if (aparatId == null) {
                                    task4 = new AparatVideoTask(aparatId);
                                    task4.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = task4;
                                } else if (twitchClipId == null) {
                                    task5 = new TwitchClipVideoTask(str2, twitchClipId);
                                    task5.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                                    r1.currentTask = task5;
                                } else if (twitchStreamId != null) {
                                    task6 = new TwitchStreamVideoTask(str2, twitchStreamId);
                                    executor = AsyncTask.THREAD_POOL_EXECUTOR;
                                    voidArr = new Void[3];
                                    z = true;
                                    voidArr[1] = null;
                                    voidArr[2] = null;
                                    task6.executeOnExecutor(executor, voidArr);
                                    r1.currentTask = task6;
                                    r1.isStream = true;
                                    r1.controlsView.show(false, false);
                                    showProgress(z, false);
                                }
                                z = true;
                                r1.controlsView.show(false, false);
                                showProgress(z, false);
                            } else {
                                r1.initied = true;
                                r1.playVideoUrl = mp4File;
                                r1.playVideoType = "other";
                                if (r1.isAutoplay) {
                                    preparePlayer();
                                }
                                showProgress(false, false);
                                r1.controlsView.show(true, true);
                            }
                            if (twitchStreamId != null) {
                                r1.controlsView.setVisibility(8);
                                return false;
                            }
                            r1.controlsView.setVisibility(0);
                            return true;
                        }
                    } else {
                        str = null;
                        r1.seekToTime = Utilities.parseInt(t).intValue();
                    }
                } catch (Throwable e3222222) {
                    str = null;
                    youtubeId = e3222222;
                    FileLog.m3e(youtubeId);
                    matcher = youtubeIdRegex.matcher(str2);
                    t = null;
                    if (matcher.find()) {
                        t = matcher.group(1);
                    }
                    if (t != null) {
                        str = t;
                    }
                    youtubeId2 = str;
                    if (youtubeId2 == null) {
                        matcher2 = vimeoIdRegex.matcher(str2);
                        id = null;
                        if (matcher2.find()) {
                            id = matcher2.group(3);
                        }
                        if (id != null) {
                            vimeoId = id;
                        }
                    }
                    if (vimeoId == null) {
                        matcher2 = aparatIdRegex.matcher(str2);
                        id = null;
                        if (matcher2.find()) {
                            id = matcher2.group(1);
                        }
                        if (id != null) {
                            aparatId = id;
                        }
                    }
                    if (aparatId == null) {
                        matcher2 = twitchClipIdRegex.matcher(str2);
                        id = null;
                        if (matcher2.find()) {
                            id = matcher2.group(1);
                        }
                        if (id != null) {
                            twitchClipId = id;
                        }
                    }
                    if (twitchClipId == null) {
                        matcher2 = twitchStreamIdRegex.matcher(str2);
                        id = null;
                        if (matcher2.find()) {
                            id = matcher2.group(1);
                        }
                        if (id != null) {
                            twitchStreamId = id;
                        }
                    }
                    if (twitchStreamId == null) {
                        matcher2 = coubIdRegex.matcher(str2);
                        id = null;
                        if (matcher2.find()) {
                            id = matcher2.group(1);
                        }
                        if (id != null) {
                            coubId = id;
                        }
                    }
                    r1.initied = false;
                    r1.isCompleted = false;
                    r1.isAutoplay = autoplay;
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
                    if (photo == null) {
                        r1.drawImage = false;
                    } else {
                        photoSize = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 80, true);
                        if (photoSize != null) {
                            if (photo == null) {
                            }
                            if (photo == null) {
                            }
                            r1.controlsView.imageReceiver.setImage(null, null, photo == null ? photoSize.location : null, photo == null ? "80_80_b" : null, 0, null, 1);
                            r1.drawImage = true;
                        }
                    }
                    if (r1.progressAnimation != null) {
                        r1.progressAnimation.cancel();
                        r1.progressAnimation = null;
                    }
                    r1.isLoading = true;
                    r1.controlsView.setProgress(0);
                    r1.currentYoutubeId = youtubeId2;
                    youtubeId2 = null;
                    if (mp4File == null) {
                        if (youtubeId2 == null) {
                            task = new YoutubeVideoTask(youtubeId2);
                            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                            r1.currentTask = task;
                        } else if (vimeoId == null) {
                            task2 = new VimeoVideoTask(vimeoId);
                            task2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                            r1.currentTask = task2;
                        } else if (coubId == null) {
                            task3 = new CoubVideoTask(coubId);
                            task3.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                            r1.currentTask = task3;
                            r1.isStream = true;
                        } else if (aparatId == null) {
                            task4 = new AparatVideoTask(aparatId);
                            task4.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                            r1.currentTask = task4;
                        } else if (twitchClipId == null) {
                            task5 = new TwitchClipVideoTask(str2, twitchClipId);
                            task5.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                            r1.currentTask = task5;
                        } else if (twitchStreamId != null) {
                            task6 = new TwitchStreamVideoTask(str2, twitchStreamId);
                            executor = AsyncTask.THREAD_POOL_EXECUTOR;
                            voidArr = new Void[3];
                            z = true;
                            voidArr[1] = null;
                            voidArr[2] = null;
                            task6.executeOnExecutor(executor, voidArr);
                            r1.currentTask = task6;
                            r1.isStream = true;
                            r1.controlsView.show(false, false);
                            showProgress(z, false);
                        }
                        z = true;
                        r1.controlsView.show(false, false);
                        showProgress(z, false);
                    } else {
                        r1.initied = true;
                        r1.playVideoUrl = mp4File;
                        r1.playVideoType = "other";
                        if (r1.isAutoplay) {
                            preparePlayer();
                        }
                        showProgress(false, false);
                        r1.controlsView.show(true, true);
                    }
                    if (twitchStreamId != null) {
                        r1.controlsView.setVisibility(8);
                        return false;
                    }
                    r1.controlsView.setVisibility(0);
                    return true;
                }
            }
            str = null;
            matcher = youtubeIdRegex.matcher(str2);
            t = null;
            if (matcher.find()) {
                t = matcher.group(1);
            }
            if (t != null) {
                str = t;
            }
            youtubeId2 = str;
            if (youtubeId2 == null) {
                matcher2 = vimeoIdRegex.matcher(str2);
                id = null;
                if (matcher2.find()) {
                    id = matcher2.group(3);
                }
                if (id != null) {
                    vimeoId = id;
                }
            }
            if (vimeoId == null) {
                matcher2 = aparatIdRegex.matcher(str2);
                id = null;
                if (matcher2.find()) {
                    id = matcher2.group(1);
                }
                if (id != null) {
                    aparatId = id;
                }
            }
            if (aparatId == null) {
                matcher2 = twitchClipIdRegex.matcher(str2);
                id = null;
                if (matcher2.find()) {
                    id = matcher2.group(1);
                }
                if (id != null) {
                    twitchClipId = id;
                }
            }
            if (twitchClipId == null) {
                matcher2 = twitchStreamIdRegex.matcher(str2);
                id = null;
                if (matcher2.find()) {
                    id = matcher2.group(1);
                }
                if (id != null) {
                    twitchStreamId = id;
                }
            }
            if (twitchStreamId == null) {
                matcher2 = coubIdRegex.matcher(str2);
                id = null;
                if (matcher2.find()) {
                    id = matcher2.group(1);
                }
                if (id != null) {
                    coubId = id;
                }
            }
        }
        r1.initied = false;
        r1.isCompleted = false;
        r1.isAutoplay = autoplay;
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
        if (photo == null) {
            photoSize = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 80, true);
            if (photoSize != null) {
                if (photo == null) {
                }
                if (photo == null) {
                }
                r1.controlsView.imageReceiver.setImage(null, null, photo == null ? photoSize.location : null, photo == null ? "80_80_b" : null, 0, null, 1);
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
        if (!(youtubeId2 == null || BuildVars.DEBUG_PRIVATE_VERSION)) {
            r1.currentYoutubeId = youtubeId2;
            youtubeId2 = null;
        }
        if (mp4File == null) {
            r1.initied = true;
            r1.playVideoUrl = mp4File;
            r1.playVideoType = "other";
            if (r1.isAutoplay) {
                preparePlayer();
            }
            showProgress(false, false);
            r1.controlsView.show(true, true);
        } else {
            if (youtubeId2 == null) {
                task = new YoutubeVideoTask(youtubeId2);
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                r1.currentTask = task;
            } else if (vimeoId == null) {
                task2 = new VimeoVideoTask(vimeoId);
                task2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                r1.currentTask = task2;
            } else if (coubId == null) {
                task3 = new CoubVideoTask(coubId);
                task3.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                r1.currentTask = task3;
                r1.isStream = true;
            } else if (aparatId == null) {
                task4 = new AparatVideoTask(aparatId);
                task4.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                r1.currentTask = task4;
            } else if (twitchClipId == null) {
                task5 = new TwitchClipVideoTask(str2, twitchClipId);
                task5.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                r1.currentTask = task5;
            } else if (twitchStreamId != null) {
                task6 = new TwitchStreamVideoTask(str2, twitchStreamId);
                executor = AsyncTask.THREAD_POOL_EXECUTOR;
                voidArr = new Void[3];
                z = true;
                voidArr[1] = null;
                voidArr[2] = null;
                task6.executeOnExecutor(executor, voidArr);
                r1.currentTask = task6;
                r1.isStream = true;
                r1.controlsView.show(false, false);
                showProgress(z, false);
            }
            z = true;
            r1.controlsView.show(false, false);
            showProgress(z, false);
        }
        if (youtubeId2 == null && vimeoId == null && coubId == null && aparatId == null && mp4File == null && twitchClipId == null) {
            if (twitchStreamId != null) {
                r1.controlsView.setVisibility(8);
                return false;
            }
        }
        r1.controlsView.setVisibility(0);
        return true;
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

    private void showProgress(boolean show, boolean animated) {
        float f = 0.0f;
        if (animated) {
            if (this.progressAnimation != null) {
                this.progressAnimation.cancel();
            }
            this.progressAnimation = new AnimatorSet();
            AnimatorSet animatorSet = this.progressAnimation;
            Animator[] animatorArr = new Animator[1];
            RadialProgressView radialProgressView = this.progressView;
            String str = "alpha";
            float[] fArr = new float[1];
            if (show) {
                f = 1.0f;
            }
            fArr[0] = f;
            animatorArr[0] = ObjectAnimator.ofFloat(radialProgressView, str, fArr);
            animatorSet.playTogether(animatorArr);
            this.progressAnimation.setDuration(150);
            this.progressAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    WebPlayerView.this.progressAnimation = null;
                }
            });
            this.progressAnimation.start();
            return;
        }
        RadialProgressView radialProgressView2 = this.progressView;
        if (show) {
            f = 1.0f;
        }
        radialProgressView2.setAlpha(f);
    }
}
