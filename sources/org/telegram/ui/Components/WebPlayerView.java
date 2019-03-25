package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
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
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
import org.telegram.tgnet.TLRPC.Photo;
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

    public interface CallJavaResultInterface {
        void jsCallFinished(String str);
    }

    private class AparatVideoTask extends AsyncTask<Void, Void, String> {
        private boolean canRetry = true;
        private String[] results = new String[2];
        private String videoId;

        public AparatVideoTask(String vid) {
            this.videoId = vid;
        }

        /* Access modifiers changed, original: protected|varargs */
        public String doInBackground(Void... voids) {
            String playerCode = WebPlayerView.this.downloadUrlContent(this, String.format(Locale.US, "http://www.aparat.com/video/video/embed/vt/frame/showvideo/yes/videohash/%s", new Object[]{this.videoId}));
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
            } catch (Exception e) {
                FileLog.e(e);
            }
            return isCancelled() ? null : this.results[0];
        }

        /* Access modifiers changed, original: protected */
        public void onPostExecute(String result) {
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

    private class ControlsView extends FrameLayout {
        private int bufferedPosition;
        private AnimatorSet currentAnimation;
        private int currentProgressX;
        private int duration;
        private StaticLayout durationLayout;
        private int durationWidth;
        private Runnable hideRunnable = new WebPlayerView$ControlsView$$Lambda$0(this);
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

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$0$WebPlayerView$ControlsView() {
            show(false, true);
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
            if (this.duration != value && value >= 0 && !WebPlayerView.this.isStream) {
                this.duration = value;
                this.durationLayout = new StaticLayout(String.format(Locale.US, "%d:%02d", new Object[]{Integer.valueOf(this.duration / 60), Integer.valueOf(this.duration % 60)}), this.textPaint, AndroidUtilities.dp(1000.0f), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                if (this.durationLayout.getLineCount() > 0) {
                    this.durationWidth = (int) Math.ceil((double) this.durationLayout.getLineWidth(0));
                }
                invalidate();
            }
        }

        public void setBufferedProgress(int position) {
            this.bufferedPosition = position;
            invalidate();
        }

        public void setProgress(int value) {
            if (!this.progressPressed && value >= 0 && !WebPlayerView.this.isStream) {
                this.progress = value;
                this.progressLayout = new StaticLayout(String.format(Locale.US, "%d:%02d", new Object[]{Integer.valueOf(this.progress / 60), Integer.valueOf(this.progress % 60)}), this.textPaint, AndroidUtilities.dp(1000.0f), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                invalidate();
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
                        this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                ControlsView.this.currentAnimation = null;
                            }
                        });
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
                    this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            ControlsView.this.currentAnimation = null;
                        }
                    });
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
            int i;
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
            if (this.duration != 0) {
                i = (int) (((float) (progressLineEndX - progressLineX)) * (((float) this.progress) / ((float) this.duration)));
            } else {
                i = 0;
            }
            int progressX = progressLineX + i;
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
            } else if (event.getAction() == 1 || event.getAction() == 3) {
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
            } else if (event.getAction() == 2 && this.progressPressed) {
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
            super.onTouchEvent(event);
            return true;
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
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
                this.imageReceiver.setAlpha(WebPlayerView.this.currentAlpha);
                this.imageReceiver.draw(canvas);
            }
            if (WebPlayerView.this.videoPlayer.isPlayerPrepared() && !WebPlayerView.this.isStream) {
                int width = getMeasuredWidth();
                int height = getMeasuredHeight();
                if (!WebPlayerView.this.isInline) {
                    if (this.durationLayout != null) {
                        canvas.save();
                        canvas.translate((float) ((width - AndroidUtilities.dp(58.0f)) - this.durationWidth), (float) (height - AndroidUtilities.dp((float) ((WebPlayerView.this.inFullscreen ? 6 : 10) + 29))));
                        this.durationLayout.draw(canvas);
                        canvas.restore();
                    }
                    if (this.progressLayout != null) {
                        canvas.save();
                        canvas.translate((float) AndroidUtilities.dp(18.0f), (float) (height - AndroidUtilities.dp((float) ((WebPlayerView.this.inFullscreen ? 6 : 10) + 29))));
                        this.progressLayout.draw(canvas);
                        canvas.restore();
                    }
                }
                if (this.duration != 0) {
                    int progressLineY;
                    int progressLineX;
                    int progressLineEndX;
                    int cy;
                    int progressX;
                    if (WebPlayerView.this.isInline) {
                        progressLineY = height - AndroidUtilities.dp(3.0f);
                        progressLineX = 0;
                        progressLineEndX = width;
                        cy = height - AndroidUtilities.dp(7.0f);
                    } else if (WebPlayerView.this.inFullscreen) {
                        progressLineY = height - AndroidUtilities.dp(29.0f);
                        progressLineX = AndroidUtilities.dp(36.0f) + this.durationWidth;
                        progressLineEndX = (width - AndroidUtilities.dp(76.0f)) - this.durationWidth;
                        cy = height - AndroidUtilities.dp(28.0f);
                    } else {
                        progressLineY = height - AndroidUtilities.dp(13.0f);
                        progressLineX = 0;
                        progressLineEndX = width;
                        cy = height - AndroidUtilities.dp(12.0f);
                    }
                    if (WebPlayerView.this.inFullscreen) {
                        canvas.drawRect((float) progressLineX, (float) progressLineY, (float) progressLineEndX, (float) (AndroidUtilities.dp(3.0f) + progressLineY), this.progressInnerPaint);
                    }
                    if (this.progressPressed) {
                        progressX = this.currentProgressX;
                    } else {
                        progressX = progressLineX + ((int) (((float) (progressLineEndX - progressLineX)) * (((float) this.progress) / ((float) this.duration))));
                    }
                    if (!(this.bufferedPosition == 0 || this.duration == 0)) {
                        canvas.drawRect((float) progressLineX, (float) progressLineY, (((float) (progressLineEndX - progressLineX)) * (((float) this.bufferedPosition) / ((float) this.duration))) + ((float) progressLineX), (float) (AndroidUtilities.dp(3.0f) + progressLineY), WebPlayerView.this.inFullscreen ? this.progressBufferedPaint : this.progressInnerPaint);
                    }
                    canvas.drawRect((float) progressLineX, (float) progressLineY, (float) progressX, (float) (AndroidUtilities.dp(3.0f) + progressLineY), this.progressPaint);
                    if (!WebPlayerView.this.isInline) {
                        canvas.drawCircle((float) progressX, (float) cy, (float) AndroidUtilities.dp(this.progressPressed ? 7.0f : 5.0f), this.progressPaint);
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
                if (c == lower) {
                    lower = Character.toUpperCase(c);
                }
                source.setCharAt(a, lower);
            }
            try {
                return new String(Base64.decode(source.toString(), 0), "UTF-8");
            } catch (Exception e) {
                return null;
            }
        }

        /* Access modifiers changed, original: protected|varargs */
        public String doInBackground(Void... voids) {
            String playerCode = WebPlayerView.this.downloadUrlContent(this, String.format(Locale.US, "https://coub.com/api/v2/coubs/%s.json", new Object[]{this.videoId}));
            if (isCancelled()) {
                return null;
            }
            try {
                JSONObject json = new JSONObject(playerCode).getJSONObject("file_versions").getJSONObject("mobile");
                String video = decodeUrl(json.getString("gifv"));
                String audio = json.getJSONArray("audio").getString(0);
                if (!(video == null || audio == null)) {
                    this.results[0] = video;
                    this.results[1] = "other";
                    this.results[2] = audio;
                    this.results[3] = "other";
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
            if (isCancelled()) {
                return null;
            }
            return this.results[0];
        }

        /* Access modifiers changed, original: protected */
        public void onPostExecute(String result) {
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

        /* JADX WARNING: Removed duplicated region for block: B:16:0x0085  */
        private void interpretExpression(java.lang.String r27, java.util.HashMap<java.lang.String, java.lang.String> r28, int r29) throws java.lang.Exception {
            /*
            r26 = this;
            r27 = r27.trim();
            r21 = android.text.TextUtils.isEmpty(r27);
            if (r21 == 0) goto L_0x000b;
        L_0x000a:
            return;
        L_0x000b:
            r21 = 0;
            r0 = r27;
            r1 = r21;
            r21 = r0.charAt(r1);
            r22 = 40;
            r0 = r21;
            r1 = r22;
            if (r0 != r1) goto L_0x009e;
        L_0x001d:
            r16 = 0;
            r21 = org.telegram.ui.Components.WebPlayerView.exprParensPattern;
            r0 = r21;
            r1 = r27;
            r14 = r0.matcher(r1);
        L_0x002b:
            r21 = r14.find();
            if (r21 == 0) goto L_0x0083;
        L_0x0031:
            r21 = 0;
            r0 = r21;
            r10 = r14.group(r0);
            r21 = 48;
            r0 = r21;
            r21 = r10.indexOf(r0);
            r22 = 40;
            r0 = r21;
            r1 = r22;
            if (r0 != r1) goto L_0x004c;
        L_0x0049:
            r16 = r16 + 1;
            goto L_0x002b;
        L_0x004c:
            r16 = r16 + -1;
            if (r16 != 0) goto L_0x002b;
        L_0x0050:
            r21 = 1;
            r22 = r14.start();
            r0 = r27;
            r1 = r21;
            r2 = r22;
            r18 = r0.substring(r1, r2);
            r0 = r26;
            r1 = r18;
            r2 = r28;
            r3 = r29;
            r0.interpretExpression(r1, r2, r3);
            r21 = r14.end();
            r0 = r27;
            r1 = r21;
            r21 = r0.substring(r1);
            r17 = r21.trim();
            r21 = android.text.TextUtils.isEmpty(r17);
            if (r21 != 0) goto L_0x000a;
        L_0x0081:
            r27 = r17;
        L_0x0083:
            if (r16 == 0) goto L_0x009e;
        L_0x0085:
            r21 = new java.lang.Exception;
            r22 = "Premature end of parens in %s";
            r23 = 1;
            r0 = r23;
            r0 = new java.lang.Object[r0];
            r23 = r0;
            r24 = 0;
            r23[r24] = r27;
            r22 = java.lang.String.format(r22, r23);
            r21.<init>(r22);
            throw r21;
        L_0x009e:
            r4 = 0;
        L_0x009f:
            r0 = r26;
            r0 = r0.assign_operators;
            r21 = r0;
            r0 = r21;
            r0 = r0.length;
            r21 = r0;
            r0 = r21;
            if (r4 >= r0) goto L_0x012f;
        L_0x00ae:
            r0 = r26;
            r0 = r0.assign_operators;
            r21 = r0;
            r9 = r21[r4];
            r21 = java.util.Locale.US;
            r22 = "(?x)(%s)(?:\\[([^\\]]+?)\\])?\\s*%s(.*)$";
            r23 = 2;
            r0 = r23;
            r0 = new java.lang.Object[r0];
            r23 = r0;
            r24 = 0;
            r25 = "[a-zA-Z_$][a-zA-Z_$0-9]*";
            r23[r24] = r25;
            r24 = 1;
            r25 = java.util.regex.Pattern.quote(r9);
            r23[r24] = r25;
            r21 = java.lang.String.format(r21, r22, r23);
            r21 = java.util.regex.Pattern.compile(r21);
            r0 = r21;
            r1 = r27;
            r14 = r0.matcher(r1);
            r21 = r14.find();
            if (r21 != 0) goto L_0x00eb;
        L_0x00e8:
            r4 = r4 + 1;
            goto L_0x009f;
        L_0x00eb:
            r21 = 3;
            r0 = r21;
            r21 = r14.group(r0);
            r22 = r29 + -1;
            r0 = r26;
            r1 = r21;
            r2 = r28;
            r3 = r22;
            r0.interpretExpression(r1, r2, r3);
            r21 = 2;
            r0 = r21;
            r11 = r14.group(r0);
            r21 = android.text.TextUtils.isEmpty(r11);
            if (r21 != 0) goto L_0x0119;
        L_0x010e:
            r0 = r26;
            r1 = r28;
            r2 = r29;
            r0.interpretExpression(r11, r1, r2);
            goto L_0x000a;
        L_0x0119:
            r21 = 1;
            r0 = r21;
            r21 = r14.group(r0);
            r22 = "";
            r0 = r28;
            r1 = r21;
            r2 = r22;
            r0.put(r1, r2);
            goto L_0x000a;
        L_0x012f:
            java.lang.Integer.parseInt(r27);	 Catch:{ Exception -> 0x0134 }
            goto L_0x000a;
        L_0x0134:
            r21 = move-exception;
            r21 = java.util.Locale.US;
            r22 = "(?!if|return|true|false)(%s)$";
            r23 = 1;
            r0 = r23;
            r0 = new java.lang.Object[r0];
            r23 = r0;
            r24 = 0;
            r25 = "[a-zA-Z_$][a-zA-Z_$0-9]*";
            r23[r24] = r25;
            r21 = java.lang.String.format(r21, r22, r23);
            r21 = java.util.regex.Pattern.compile(r21);
            r0 = r21;
            r1 = r27;
            r14 = r0.matcher(r1);
            r21 = r14.find();
            if (r21 != 0) goto L_0x000a;
        L_0x015f:
            r21 = 0;
            r0 = r27;
            r1 = r21;
            r21 = r0.charAt(r1);
            r22 = 34;
            r0 = r21;
            r1 = r22;
            if (r0 != r1) goto L_0x0187;
        L_0x0171:
            r21 = r27.length();
            r21 = r21 + -1;
            r0 = r27;
            r1 = r21;
            r21 = r0.charAt(r1);
            r22 = 34;
            r0 = r21;
            r1 = r22;
            if (r0 == r1) goto L_0x000a;
        L_0x0187:
            r21 = new org.json.JSONObject;	 Catch:{ Exception -> 0x0195 }
            r0 = r21;
            r1 = r27;
            r0.<init>(r1);	 Catch:{ Exception -> 0x0195 }
            r21.toString();	 Catch:{ Exception -> 0x0195 }
            goto L_0x000a;
        L_0x0195:
            r21 = move-exception;
            r21 = java.util.Locale.US;
            r22 = "(%s)\\[(.+)\\]$";
            r23 = 1;
            r0 = r23;
            r0 = new java.lang.Object[r0];
            r23 = r0;
            r24 = 0;
            r25 = "[a-zA-Z_$][a-zA-Z_$0-9]*";
            r23[r24] = r25;
            r21 = java.lang.String.format(r21, r22, r23);
            r21 = java.util.regex.Pattern.compile(r21);
            r0 = r21;
            r1 = r27;
            r14 = r0.matcher(r1);
            r21 = r14.find();
            if (r21 == 0) goto L_0x01df;
        L_0x01c0:
            r21 = 1;
            r0 = r21;
            r19 = r14.group(r0);
            r21 = 2;
            r0 = r21;
            r21 = r14.group(r0);
            r22 = r29 + -1;
            r0 = r26;
            r1 = r21;
            r2 = r28;
            r3 = r22;
            r0.interpretExpression(r1, r2, r3);
            goto L_0x000a;
        L_0x01df:
            r21 = java.util.Locale.US;
            r22 = "(%s)(?:\\.([^(]+)|\\[([^]]+)\\])\\s*(?:\\(+([^()]*)\\))?$";
            r23 = 1;
            r0 = r23;
            r0 = new java.lang.Object[r0];
            r23 = r0;
            r24 = 0;
            r25 = "[a-zA-Z_$][a-zA-Z_$0-9]*";
            r23[r24] = r25;
            r21 = java.lang.String.format(r21, r22, r23);
            r21 = java.util.regex.Pattern.compile(r21);
            r0 = r21;
            r1 = r27;
            r14 = r0.matcher(r1);
            r21 = r14.find();
            if (r21 == 0) goto L_0x0298;
        L_0x0209:
            r21 = 1;
            r0 = r21;
            r20 = r14.group(r0);
            r21 = 2;
            r0 = r21;
            r12 = r14.group(r0);
            r21 = 3;
            r0 = r21;
            r13 = r14.group(r0);
            r21 = android.text.TextUtils.isEmpty(r12);
            if (r21 == 0) goto L_0x026f;
        L_0x0227:
            r21 = "\"";
            r22 = "";
            r0 = r21;
            r1 = r22;
            r15 = r13.replace(r0, r1);
            r21 = 4;
            r0 = r21;
            r6 = r14.group(r0);
            r0 = r28;
            r1 = r20;
            r21 = r0.get(r1);
            if (r21 != 0) goto L_0x024e;
        L_0x0247:
            r0 = r26;
            r1 = r20;
            r0.extractObject(r1);
        L_0x024e:
            if (r6 == 0) goto L_0x000a;
        L_0x0250:
            r21 = r27.length();
            r21 = r21 + -1;
            r0 = r27;
            r1 = r21;
            r21 = r0.charAt(r1);
            r22 = 41;
            r0 = r21;
            r1 = r22;
            if (r0 == r1) goto L_0x0271;
        L_0x0266:
            r21 = new java.lang.Exception;
            r22 = "last char not ')'";
            r21.<init>(r22);
            throw r21;
        L_0x026f:
            r13 = r12;
            goto L_0x0227;
        L_0x0271:
            r21 = r6.length();
            if (r21 == 0) goto L_0x000a;
        L_0x0277:
            r21 = ",";
            r0 = r21;
            r7 = r6.split(r0);
            r4 = 0;
        L_0x0281:
            r0 = r7.length;
            r21 = r0;
            r0 = r21;
            if (r4 >= r0) goto L_0x000a;
        L_0x0288:
            r21 = r7[r4];
            r0 = r26;
            r1 = r21;
            r2 = r28;
            r3 = r29;
            r0.interpretExpression(r1, r2, r3);
            r4 = r4 + 1;
            goto L_0x0281;
        L_0x0298:
            r21 = java.util.Locale.US;
            r22 = "(%s)\\[(.+)\\]$";
            r23 = 1;
            r0 = r23;
            r0 = new java.lang.Object[r0];
            r23 = r0;
            r24 = 0;
            r25 = "[a-zA-Z_$][a-zA-Z_$0-9]*";
            r23[r24] = r25;
            r21 = java.lang.String.format(r21, r22, r23);
            r21 = java.util.regex.Pattern.compile(r21);
            r0 = r21;
            r1 = r27;
            r14 = r0.matcher(r1);
            r21 = r14.find();
            if (r21 == 0) goto L_0x02e9;
        L_0x02c2:
            r21 = 1;
            r0 = r21;
            r21 = r14.group(r0);
            r0 = r28;
            r1 = r21;
            r19 = r0.get(r1);
            r21 = 2;
            r0 = r21;
            r21 = r14.group(r0);
            r22 = r29 + -1;
            r0 = r26;
            r1 = r21;
            r2 = r28;
            r3 = r22;
            r0.interpretExpression(r1, r2, r3);
            goto L_0x000a;
        L_0x02e9:
            r4 = 0;
        L_0x02ea:
            r0 = r26;
            r0 = r0.operators;
            r21 = r0;
            r0 = r21;
            r0 = r0.length;
            r21 = r0;
            r0 = r21;
            if (r4 >= r0) goto L_0x03a5;
        L_0x02f9:
            r0 = r26;
            r0 = r0.operators;
            r21 = r0;
            r9 = r21[r4];
            r21 = java.util.Locale.US;
            r22 = "(.+?)%s(.+)";
            r23 = 1;
            r0 = r23;
            r0 = new java.lang.Object[r0];
            r23 = r0;
            r24 = 0;
            r25 = java.util.regex.Pattern.quote(r9);
            r23[r24] = r25;
            r21 = java.lang.String.format(r21, r22, r23);
            r21 = java.util.regex.Pattern.compile(r21);
            r0 = r21;
            r1 = r27;
            r14 = r0.matcher(r1);
            r21 = r14.find();
            if (r21 != 0) goto L_0x032f;
        L_0x032c:
            r4 = r4 + 1;
            goto L_0x02ea;
        L_0x032f:
            r21 = 1;
            r0 = r21;
            r5 = new boolean[r0];
            r21 = 1;
            r0 = r21;
            r21 = r14.group(r0);
            r22 = r29 + -1;
            r0 = r26;
            r1 = r21;
            r2 = r28;
            r3 = r22;
            r0.interpretStatement(r1, r2, r5, r3);
            r21 = 0;
            r21 = r5[r21];
            if (r21 == 0) goto L_0x036d;
        L_0x0350:
            r21 = new java.lang.Exception;
            r22 = "Premature left-side return of %s in %s";
            r23 = 2;
            r0 = r23;
            r0 = new java.lang.Object[r0];
            r23 = r0;
            r24 = 0;
            r23[r24] = r9;
            r24 = 1;
            r23[r24] = r27;
            r22 = java.lang.String.format(r22, r23);
            r21.<init>(r22);
            throw r21;
        L_0x036d:
            r21 = 2;
            r0 = r21;
            r21 = r14.group(r0);
            r22 = r29 + -1;
            r0 = r26;
            r1 = r21;
            r2 = r28;
            r3 = r22;
            r0.interpretStatement(r1, r2, r5, r3);
            r21 = 0;
            r21 = r5[r21];
            if (r21 == 0) goto L_0x032c;
        L_0x0388:
            r21 = new java.lang.Exception;
            r22 = "Premature right-side return of %s in %s";
            r23 = 2;
            r0 = r23;
            r0 = new java.lang.Object[r0];
            r23 = r0;
            r24 = 0;
            r23[r24] = r9;
            r24 = 1;
            r23[r24] = r27;
            r22 = java.lang.String.format(r22, r23);
            r21.<init>(r22);
            throw r21;
        L_0x03a5:
            r21 = java.util.Locale.US;
            r22 = "^(%s)\\(([a-zA-Z0-9_$,]*)\\)$";
            r23 = 1;
            r0 = r23;
            r0 = new java.lang.Object[r0];
            r23 = r0;
            r24 = 0;
            r25 = "[a-zA-Z_$][a-zA-Z_$0-9]*";
            r23[r24] = r25;
            r21 = java.lang.String.format(r21, r22, r23);
            r21 = java.util.regex.Pattern.compile(r21);
            r0 = r21;
            r1 = r27;
            r14 = r0.matcher(r1);
            r21 = r14.find();
            if (r21 == 0) goto L_0x03dc;
        L_0x03cf:
            r21 = 1;
            r0 = r21;
            r8 = r14.group(r0);
            r0 = r26;
            r0.extractFunction(r8);
        L_0x03dc:
            r21 = new java.lang.Exception;
            r22 = "Unsupported JS expression %s";
            r23 = 1;
            r0 = r23;
            r0 = new java.lang.Object[r0];
            r23 = r0;
            r24 = 0;
            r23[r24] = r27;
            r22 = java.lang.String.format(r22, r23);
            r21.<init>(r22);
            throw r21;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.WebPlayerView$JSExtractor.interpretExpression(java.lang.String, java.util.HashMap, int):void");
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
                    expr = stmt.substring(matcher.group(0).length());
                    abort[0] = true;
                } else {
                    expr = stmt;
                }
            }
            interpretExpression(expr, localVars, allowRecursion);
        }

        /* JADX WARNING: Removed duplicated region for block: B:11:0x0067 A:{LOOP_END, LOOP:1: B:9:0x0061->B:11:0x0067} */
        private java.util.HashMap<java.lang.String, java.lang.Object> extractObject(java.lang.String r14) throws java.lang.Exception {
            /*
            r13 = this;
            r12 = 1;
            r11 = 0;
            r10 = 2;
            r3 = "(?:[a-zA-Z$0-9]+|\"[a-zA-Z$0-9]+\"|'[a-zA-Z$0-9]+')";
            r5 = new java.util.HashMap;
            r5.<init>();
            r6 = java.util.Locale.US;
            r7 = "(?:var\\s+)?%s\\s*=\\s*\\{\\s*((%s\\s*:\\s*function\\(.*?\\)\\s*\\{.*?\\}(?:,\\s*)?)*)\\}\\s*;";
            r8 = new java.lang.Object[r10];
            r9 = java.util.regex.Pattern.quote(r14);
            r8[r11] = r9;
            r8[r12] = r3;
            r6 = java.lang.String.format(r6, r7, r8);
            r6 = java.util.regex.Pattern.compile(r6);
            r7 = r13.jsCode;
            r4 = r6.matcher(r7);
            r2 = 0;
        L_0x0029:
            r6 = r4.find();
            if (r6 == 0) goto L_0x004e;
        L_0x002f:
            r1 = r4.group();
            r2 = r4.group(r10);
            r6 = android.text.TextUtils.isEmpty(r2);
            if (r6 != 0) goto L_0x0029;
        L_0x003d:
            r6 = r13.codeLines;
            r6 = r6.contains(r1);
            if (r6 != 0) goto L_0x004e;
        L_0x0045:
            r6 = r13.codeLines;
            r7 = r4.group();
            r6.add(r7);
        L_0x004e:
            r6 = "(%s)\\s*:\\s*function\\(([a-z,]+)\\)\\{([^}]+)\\}";
            r7 = new java.lang.Object[r12];
            r7[r11] = r3;
            r6 = java.lang.String.format(r6, r7);
            r6 = java.util.regex.Pattern.compile(r6);
            r4 = r6.matcher(r2);
        L_0x0061:
            r6 = r4.find();
            if (r6 == 0) goto L_0x007b;
        L_0x0067:
            r6 = r4.group(r10);
            r7 = ",";
            r0 = r6.split(r7);
            r6 = 3;
            r6 = r4.group(r6);
            r13.buildFunction(r0, r6);
            goto L_0x0061;
        L_0x007b:
            return r5;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.WebPlayerView$JSExtractor.extractObject(java.lang.String):java.util.HashMap");
        }

        private void buildFunction(String[] argNames, String funcCode) throws Exception {
            HashMap<String, String> localVars = new HashMap();
            for (Object put : argNames) {
                localVars.put(put, "");
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

        private String extractFunction(String funcName) {
            try {
                String quote = Pattern.quote(funcName);
                Matcher matcher = Pattern.compile(String.format(Locale.US, "(?x)(?:function\\s+%s|[{;,]\\s*%s\\s*=\\s*function|var\\s+%s\\s*=\\s*function)\\s*\\(([^)]*)\\)\\s*\\{([^}]+)\\}", new Object[]{quote, quote, quote})).matcher(this.jsCode);
                if (matcher.find()) {
                    String group = matcher.group();
                    if (!this.codeLines.contains(group)) {
                        this.codeLines.add(group + ";");
                    }
                    buildFunction(matcher.group(1).split(","), matcher.group(2));
                }
            } catch (Exception e) {
                this.codeLines.clear();
                FileLog.e(e);
            }
            return TextUtils.join("", this.codeLines);
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

        /* Access modifiers changed, original: protected|varargs */
        public String doInBackground(Void... voids) {
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
            } catch (Exception e) {
                FileLog.e(e);
            }
            if (isCancelled()) {
                return null;
            }
            return this.results[0];
        }

        /* Access modifiers changed, original: protected */
        public void onPostExecute(String result) {
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

        /* Access modifiers changed, original: protected|varargs */
        public String doInBackground(Void... voids) {
            HashMap<String, String> headers = new HashMap();
            headers.put("Client-ID", "jzkbprfvar_iqj646a697cyrvl0zt2m6");
            int idx = this.videoId.indexOf(38);
            if (idx > 0) {
                this.videoId = this.videoId.substring(0, idx);
            }
            String streamCode = WebPlayerView.this.downloadUrlContent(this, String.format(Locale.US, "https://api.twitch.tv/kraken/streams/%s?stream_type=all", new Object[]{this.videoId}), headers, false);
            if (isCancelled()) {
                return null;
            }
            try {
                JSONObject stream = new JSONObject(streamCode).getJSONObject("stream");
                JSONObject accessToken = new JSONObject(WebPlayerView.this.downloadUrlContent(this, String.format(Locale.US, "https://api.twitch.tv/api/channels/%s/access_token", new Object[]{this.videoId}), headers, false));
                String sig = URLEncoder.encode(accessToken.getString("sig"), "UTF-8");
                String token = URLEncoder.encode(accessToken.getString("token"), "UTF-8");
                URLEncoder.encode("https://youtube.googleapis.com/v/" + this.videoId, "UTF-8");
                String params = "allow_source=true&allow_audio_only=true&allow_spectre=true&player=twitchweb&segment_preference=4&p=" + ((int) (Math.random() * 1.0E7d)) + "&sig=" + sig + "&token=" + token;
                this.results[0] = String.format(Locale.US, "https://usher.ttvnw.net/api/channel/hls/%s.m3u8?%s", new Object[]{this.videoId, params});
                this.results[1] = "hls";
            } catch (Exception e) {
                FileLog.e(e);
            }
            return isCancelled() ? null : this.results[0];
        }

        /* Access modifiers changed, original: protected */
        public void onPostExecute(String result) {
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

        /* Access modifiers changed, original: protected|varargs */
        /* JADX WARNING: Removed duplicated region for block: B:23:0x00ad  */
        /* JADX WARNING: Removed duplicated region for block: B:13:0x005c  */
        public java.lang.String doInBackground(java.lang.Void... r16) {
            /*
            r15 = this;
            r9 = org.telegram.ui.Components.WebPlayerView.this;
            r10 = java.util.Locale.US;
            r11 = "https://player.vimeo.com/video/%s/config";
            r12 = 1;
            r12 = new java.lang.Object[r12];
            r13 = 0;
            r14 = r15.videoId;
            r12[r13] = r14;
            r10 = java.lang.String.format(r10, r11, r12);
            r7 = r9.downloadUrlContent(r15, r10);
            r9 = r15.isCancelled();
            if (r9 == 0) goto L_0x001f;
        L_0x001d:
            r9 = 0;
        L_0x001e:
            return r9;
        L_0x001f:
            r6 = new org.json.JSONObject;	 Catch:{ Exception -> 0x007e }
            r6.<init>(r7);	 Catch:{ Exception -> 0x007e }
            r9 = "request";
            r9 = r6.getJSONObject(r9);	 Catch:{ Exception -> 0x007e }
            r10 = "files";
            r3 = r9.getJSONObject(r10);	 Catch:{ Exception -> 0x007e }
            r9 = "hls";
            r9 = r3.has(r9);	 Catch:{ Exception -> 0x007e }
            if (r9 == 0) goto L_0x0083;
        L_0x003b:
            r9 = "hls";
            r5 = r3.getJSONObject(r9);	 Catch:{ Exception -> 0x007e }
            r9 = r15.results;	 Catch:{ Exception -> 0x005e }
            r10 = 0;
            r11 = "url";
            r11 = r5.getString(r11);	 Catch:{ Exception -> 0x005e }
            r9[r10] = r11;	 Catch:{ Exception -> 0x005e }
        L_0x004e:
            r9 = r15.results;	 Catch:{ Exception -> 0x007e }
            r10 = 1;
            r11 = "hls";
            r9[r10] = r11;	 Catch:{ Exception -> 0x007e }
        L_0x0056:
            r9 = r15.isCancelled();
            if (r9 == 0) goto L_0x00ad;
        L_0x005c:
            r9 = 0;
            goto L_0x001e;
        L_0x005e:
            r2 = move-exception;
            r9 = "default_cdn";
            r1 = r5.getString(r9);	 Catch:{ Exception -> 0x007e }
            r9 = "cdns";
            r0 = r5.getJSONObject(r9);	 Catch:{ Exception -> 0x007e }
            r5 = r0.getJSONObject(r1);	 Catch:{ Exception -> 0x007e }
            r9 = r15.results;	 Catch:{ Exception -> 0x007e }
            r10 = 0;
            r11 = "url";
            r11 = r5.getString(r11);	 Catch:{ Exception -> 0x007e }
            r9[r10] = r11;	 Catch:{ Exception -> 0x007e }
            goto L_0x004e;
        L_0x007e:
            r2 = move-exception;
            org.telegram.messenger.FileLog.e(r2);
            goto L_0x0056;
        L_0x0083:
            r9 = "progressive";
            r9 = r3.has(r9);	 Catch:{ Exception -> 0x007e }
            if (r9 == 0) goto L_0x0056;
        L_0x008c:
            r9 = r15.results;	 Catch:{ Exception -> 0x007e }
            r10 = 1;
            r11 = "other";
            r9[r10] = r11;	 Catch:{ Exception -> 0x007e }
            r9 = "progressive";
            r8 = r3.getJSONArray(r9);	 Catch:{ Exception -> 0x007e }
            r9 = 0;
            r4 = r8.getJSONObject(r9);	 Catch:{ Exception -> 0x007e }
            r9 = r15.results;	 Catch:{ Exception -> 0x007e }
            r10 = 0;
            r11 = "url";
            r11 = r4.getString(r11);	 Catch:{ Exception -> 0x007e }
            r9[r10] = r11;	 Catch:{ Exception -> 0x007e }
            goto L_0x0056;
        L_0x00ad:
            r9 = r15.results;
            r10 = 0;
            r9 = r9[r10];
            goto L_0x001e;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.WebPlayerView$VimeoVideoTask.doInBackground(java.lang.Void[]):java.lang.String");
        }

        /* Access modifiers changed, original: protected */
        public void onPostExecute(String result) {
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

    private class YoutubeVideoTask extends AsyncTask<Void, Void, String[]> {
        private boolean canRetry = true;
        private CountDownLatch countDownLatch = new CountDownLatch(1);
        private String[] result = new String[2];
        private String sig;
        private String videoId;

        public YoutubeVideoTask(String vid) {
            this.videoId = vid;
        }

        /* Access modifiers changed, original: protected|varargs */
        public String[] doInBackground(Void... voids) {
            String embedCode = WebPlayerView.this.downloadUrlContent(this, "https://www.youtube.com/embed/" + this.videoId);
            if (isCancelled()) {
                return null;
            }
            Matcher matcher;
            String params = "video_id=" + this.videoId + "&ps=default&gl=US&hl=en";
            try {
                params = params + "&eurl=" + URLEncoder.encode("https://youtube.googleapis.com/v/" + this.videoId, "UTF-8");
            } catch (Exception e) {
                FileLog.e(e);
            }
            if (embedCode != null) {
                matcher = WebPlayerView.stsPattern.matcher(embedCode);
                if (matcher.find()) {
                    params = params + "&sts=" + embedCode.substring(matcher.start() + 6, matcher.end());
                } else {
                    params = params + "&sts=";
                }
            }
            this.result[1] = "dash";
            boolean encrypted = false;
            String otherUrl = null;
            String[] extra = new String[]{"", "&el=leanback", "&el=embedded", "&el=detailpage", "&el=vevo"};
            for (String str : extra) {
                String videoInfo = WebPlayerView.this.downloadUrlContent(this, "https://www.youtube.com/get_video_info?" + params + str);
                if (isCancelled()) {
                    return null;
                }
                boolean exists = false;
                String hls = null;
                boolean isLive = false;
                if (videoInfo != null) {
                    String[] args = videoInfo.split("&");
                    for (int a = 0; a < args.length; a++) {
                        String[] args2;
                        if (args[a].startsWith("dashmpd")) {
                            exists = true;
                            args2 = args[a].split("=");
                            if (args2.length == 2) {
                                try {
                                    this.result[0] = URLDecoder.decode(args2[1], "UTF-8");
                                } catch (Exception e2) {
                                    FileLog.e(e2);
                                }
                            }
                        } else if (args[a].startsWith("url_encoded_fmt_stream_map")) {
                            args2 = args[a].split("=");
                            if (args2.length == 2) {
                                try {
                                    String[] args3 = URLDecoder.decode(args2[1], "UTF-8").split("[&,]");
                                    String currentUrl = null;
                                    boolean isMp4 = false;
                                    for (String split : args3) {
                                        String[] args4 = split.split("=");
                                        if (args4[0].startsWith("type")) {
                                            if (URLDecoder.decode(args4[1], "UTF-8").contains("video/mp4")) {
                                                isMp4 = true;
                                            }
                                        } else if (args4[0].startsWith("url")) {
                                            currentUrl = URLDecoder.decode(args4[1], "UTF-8");
                                        } else if (args4[0].startsWith("itag")) {
                                            currentUrl = null;
                                            isMp4 = false;
                                        }
                                        if (isMp4 && currentUrl != null) {
                                            otherUrl = currentUrl;
                                            break;
                                        }
                                    }
                                } catch (Exception e22) {
                                    FileLog.e(e22);
                                }
                            }
                        } else if (args[a].startsWith("use_cipher_signature")) {
                            args2 = args[a].split("=");
                            if (args2.length == 2 && args2[1].toLowerCase().equals("true")) {
                                encrypted = true;
                            }
                        } else if (args[a].startsWith("hlsvp")) {
                            args2 = args[a].split("=");
                            if (args2.length == 2) {
                                try {
                                    hls = URLDecoder.decode(args2[1], "UTF-8");
                                } catch (Exception e222) {
                                    FileLog.e(e222);
                                }
                            }
                        } else if (args[a].startsWith("livestream")) {
                            args2 = args[a].split("=");
                            if (args2.length == 2 && args2[1].toLowerCase().equals("1")) {
                                isLive = true;
                            }
                        }
                    }
                }
                if (isLive) {
                    if (hls == null || encrypted || hls.contains("/s/")) {
                        return null;
                    }
                    this.result[0] = hls;
                    this.result[1] = "hls";
                }
                if (exists) {
                    break;
                }
            }
            if (this.result[0] == null && otherUrl != null) {
                this.result[0] = otherUrl;
                this.result[1] = "other";
            }
            if (this.result[0] != null && ((encrypted || this.result[0].contains("/s/")) && embedCode != null)) {
                encrypted = true;
                int index = this.result[0].indexOf("/s/");
                int index2 = this.result[0].indexOf(47, index + 10);
                if (index != -1) {
                    if (index2 == -1) {
                        index2 = this.result[0].length();
                    }
                    this.sig = this.result[0].substring(index, index2);
                    String jsUrl = null;
                    matcher = WebPlayerView.jsPattern.matcher(embedCode);
                    if (matcher.find()) {
                        try {
                            Object value = new JSONTokener(matcher.group(1)).nextValue();
                            if (value instanceof String) {
                                jsUrl = (String) value;
                            }
                        } catch (Exception e2222) {
                            FileLog.e(e2222);
                        }
                    }
                    if (jsUrl != null) {
                        String playerId;
                        matcher = WebPlayerView.playerIdPattern.matcher(jsUrl);
                        if (matcher.find()) {
                            playerId = matcher.group(1) + matcher.group(2);
                        } else {
                            playerId = null;
                        }
                        String functionCode = null;
                        String functionName = null;
                        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("youtubecode", 0);
                        if (playerId != null) {
                            functionCode = preferences.getString(playerId, null);
                            functionName = preferences.getString(playerId + "n", null);
                        }
                        if (functionCode == null) {
                            if (jsUrl.startsWith("//")) {
                                jsUrl = "https:" + jsUrl;
                            } else if (jsUrl.startsWith("/")) {
                                jsUrl = "https://www.youtube.com" + jsUrl;
                            }
                            String jsCode = WebPlayerView.this.downloadUrlContent(this, jsUrl);
                            if (isCancelled()) {
                                return null;
                            }
                            if (jsCode != null) {
                                matcher = WebPlayerView.sigPattern.matcher(jsCode);
                                if (matcher.find()) {
                                    functionName = matcher.group(1);
                                } else {
                                    matcher = WebPlayerView.sigPattern2.matcher(jsCode);
                                    if (matcher.find()) {
                                        functionName = matcher.group(1);
                                    }
                                }
                                if (functionName != null) {
                                    try {
                                        functionCode = new JSExtractor(jsCode).extractFunction(functionName);
                                        if (!(TextUtils.isEmpty(functionCode) || playerId == null)) {
                                            preferences.edit().putString(playerId, functionCode).putString(playerId + "n", functionName).commit();
                                        }
                                    } catch (Exception e22222) {
                                        FileLog.e(e22222);
                                    }
                                }
                            }
                        }
                        if (!TextUtils.isEmpty(functionCode)) {
                            if (VERSION.SDK_INT >= 21) {
                                functionCode = functionCode + functionName + "('" + this.sig.substring(3) + "');";
                            } else {
                                functionCode = functionCode + "window." + WebPlayerView.this.interfaceName + ".returnResultToJava(" + functionName + "('" + this.sig.substring(3) + "'));";
                            }
                            try {
                                AndroidUtilities.runOnUIThread(new WebPlayerView$YoutubeVideoTask$$Lambda$0(this, functionCode));
                                this.countDownLatch.await();
                                encrypted = false;
                            } catch (Exception e222222) {
                                FileLog.e(e222222);
                            }
                        }
                    }
                }
            }
            if (isCancelled() || encrypted) {
                return null;
            }
            return this.result;
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$doInBackground$1$WebPlayerView$YoutubeVideoTask(String functionCodeFinal) {
            if (VERSION.SDK_INT >= 21) {
                WebPlayerView.this.webView.evaluateJavascript(functionCodeFinal, new WebPlayerView$YoutubeVideoTask$$Lambda$1(this));
                return;
            }
            try {
                WebPlayerView.this.webView.loadUrl("data:text/html;charset=utf-8;base64," + Base64.encodeToString(("<script>" + functionCodeFinal + "</script>").getBytes("UTF-8"), 0));
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$null$0$WebPlayerView$YoutubeVideoTask(String value) {
            this.result[0] = this.result[0].replace(this.sig, "/signature/" + value.substring(1, value.length() - 1));
            this.countDownLatch.countDown();
        }

        private void onInterfaceResult(String value) {
            this.result[0] = this.result[0].replace(this.sig, "/signature/" + value);
            this.countDownLatch.countDown();
        }

        /* Access modifiers changed, original: protected */
        public void onPostExecute(String[] result) {
            if (result[0] != null) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("start play youtube video " + result[1] + " " + result[0]);
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

    /* Access modifiers changed, original: protected */
    public String downloadUrlContent(AsyncTask parentTask, String url) {
        return downloadUrlContent(parentTask, url, null, true);
    }

    /* Access modifiers changed, original: protected */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x00c6 A:{SYNTHETIC, Splitter:B:40:0x00c6} */
    /* JADX WARNING: Removed duplicated region for block: B:110:0x024c  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x00cb  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x00c6 A:{SYNTHETIC, Splitter:B:40:0x00c6} */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x00cb  */
    /* JADX WARNING: Removed duplicated region for block: B:110:0x024c  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x00c6 A:{SYNTHETIC, Splitter:B:40:0x00c6} */
    /* JADX WARNING: Removed duplicated region for block: B:110:0x024c  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x00cb  */
    public java.lang.String downloadUrlContent(android.os.AsyncTask r25, java.lang.String r26, java.util.HashMap<java.lang.String, java.lang.String> r27, boolean r28) {
        /*
        r24 = this;
        r4 = 1;
        r13 = 0;
        r8 = 0;
        r18 = 0;
        r12 = 0;
        r9 = new java.net.URL;	 Catch:{ Throwable -> 0x007e }
        r0 = r26;
        r9.<init>(r0);	 Catch:{ Throwable -> 0x007e }
        r12 = r9.openConnection();	 Catch:{ Throwable -> 0x007e }
        r21 = "User-Agent";
        r22 = "Mozilla/5.0 (X11; Linux x86_64; rv:10.0) Gecko/20150101 Firefox/47.0 (Chrome)";
        r0 = r21;
        r1 = r22;
        r12.addRequestProperty(r0, r1);	 Catch:{ Throwable -> 0x007e }
        if (r28 == 0) goto L_0x002d;
    L_0x0020:
        r21 = "Accept-Encoding";
        r22 = "gzip, deflate";
        r0 = r21;
        r1 = r22;
        r12.addRequestProperty(r0, r1);	 Catch:{ Throwable -> 0x007e }
    L_0x002d:
        r21 = "Accept-Language";
        r22 = "en-us,en;q=0.5";
        r0 = r21;
        r1 = r22;
        r12.addRequestProperty(r0, r1);	 Catch:{ Throwable -> 0x007e }
        r21 = "Accept";
        r22 = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
        r0 = r21;
        r1 = r22;
        r12.addRequestProperty(r0, r1);	 Catch:{ Throwable -> 0x007e }
        r21 = "Accept-Charset";
        r22 = "ISO-8859-1,utf-8;q=0.7,*;q=0.7";
        r0 = r21;
        r1 = r22;
        r12.addRequestProperty(r0, r1);	 Catch:{ Throwable -> 0x007e }
        if (r27 == 0) goto L_0x00d0;
    L_0x0056:
        r21 = r27.entrySet();	 Catch:{ Throwable -> 0x007e }
        r23 = r21.iterator();	 Catch:{ Throwable -> 0x007e }
    L_0x005e:
        r21 = r23.hasNext();	 Catch:{ Throwable -> 0x007e }
        if (r21 == 0) goto L_0x00d0;
    L_0x0064:
        r11 = r23.next();	 Catch:{ Throwable -> 0x007e }
        r11 = (java.util.Map.Entry) r11;	 Catch:{ Throwable -> 0x007e }
        r21 = r11.getKey();	 Catch:{ Throwable -> 0x007e }
        r21 = (java.lang.String) r21;	 Catch:{ Throwable -> 0x007e }
        r22 = r11.getValue();	 Catch:{ Throwable -> 0x007e }
        r22 = (java.lang.String) r22;	 Catch:{ Throwable -> 0x007e }
        r0 = r21;
        r1 = r22;
        r12.addRequestProperty(r0, r1);	 Catch:{ Throwable -> 0x007e }
        goto L_0x005e;
    L_0x007e:
        r10 = move-exception;
        r0 = r10 instanceof java.net.SocketTimeoutException;
        r21 = r0;
        if (r21 == 0) goto L_0x01c9;
    L_0x0085:
        r21 = org.telegram.messenger.ApplicationLoader.isNetworkOnline();
        if (r21 == 0) goto L_0x008c;
    L_0x008b:
        r4 = 0;
    L_0x008c:
        org.telegram.messenger.FileLog.e(r10);
    L_0x008f:
        if (r4 == 0) goto L_0x00c9;
    L_0x0091:
        if (r12 == 0) goto L_0x00b1;
    L_0x0093:
        r0 = r12 instanceof java.net.HttpURLConnection;	 Catch:{ Exception -> 0x01f7 }
        r21 = r0;
        if (r21 == 0) goto L_0x00b1;
    L_0x0099:
        r12 = (java.net.HttpURLConnection) r12;	 Catch:{ Exception -> 0x01f7 }
        r5 = r12.getResponseCode();	 Catch:{ Exception -> 0x01f7 }
        r21 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
        r0 = r21;
        if (r5 == r0) goto L_0x00b1;
    L_0x00a5:
        r21 = 202; // 0xca float:2.83E-43 double:1.0E-321;
        r0 = r21;
        if (r5 == r0) goto L_0x00b1;
    L_0x00ab:
        r21 = 304; // 0x130 float:4.26E-43 double:1.5E-321;
        r0 = r21;
        if (r5 == r0) goto L_0x00b1;
    L_0x00b1:
        if (r13 == 0) goto L_0x00c4;
    L_0x00b3:
        r21 = 32768; // 0x8000 float:4.5918E-41 double:1.61895E-319;
        r0 = r21;
        r7 = new byte[r0];	 Catch:{ Throwable -> 0x0240 }
        r19 = r18;
    L_0x00bc:
        r21 = r25.isCancelled();	 Catch:{ Throwable -> 0x0253 }
        if (r21 == 0) goto L_0x01fd;
    L_0x00c2:
        r18 = r19;
    L_0x00c4:
        if (r13 == 0) goto L_0x00c9;
    L_0x00c6:
        r13.close();	 Catch:{ Throwable -> 0x0246 }
    L_0x00c9:
        if (r8 == 0) goto L_0x024c;
    L_0x00cb:
        r21 = r18.toString();
    L_0x00cf:
        return r21;
    L_0x00d0:
        r21 = 5000; // 0x1388 float:7.006E-42 double:2.4703E-320;
        r0 = r21;
        r12.setConnectTimeout(r0);	 Catch:{ Throwable -> 0x007e }
        r21 = 5000; // 0x1388 float:7.006E-42 double:2.4703E-320;
        r0 = r21;
        r12.setReadTimeout(r0);	 Catch:{ Throwable -> 0x007e }
        r0 = r12 instanceof java.net.HttpURLConnection;	 Catch:{ Throwable -> 0x007e }
        r21 = r0;
        if (r21 == 0) goto L_0x019d;
    L_0x00e4:
        r0 = r12;
        r0 = (java.net.HttpURLConnection) r0;	 Catch:{ Throwable -> 0x007e }
        r15 = r0;
        r21 = 1;
        r0 = r21;
        r15.setInstanceFollowRedirects(r0);	 Catch:{ Throwable -> 0x007e }
        r20 = r15.getResponseCode();	 Catch:{ Throwable -> 0x007e }
        r21 = 302; // 0x12e float:4.23E-43 double:1.49E-321;
        r0 = r20;
        r1 = r21;
        if (r0 == r1) goto L_0x010b;
    L_0x00fb:
        r21 = 301; // 0x12d float:4.22E-43 double:1.487E-321;
        r0 = r20;
        r1 = r21;
        if (r0 == r1) goto L_0x010b;
    L_0x0103:
        r21 = 303; // 0x12f float:4.25E-43 double:1.497E-321;
        r0 = r20;
        r1 = r21;
        if (r0 != r1) goto L_0x019d;
    L_0x010b:
        r21 = "Location";
        r0 = r21;
        r16 = r15.getHeaderField(r0);	 Catch:{ Throwable -> 0x007e }
        r21 = "Set-Cookie";
        r0 = r21;
        r6 = r15.getHeaderField(r0);	 Catch:{ Throwable -> 0x007e }
        r9 = new java.net.URL;	 Catch:{ Throwable -> 0x007e }
        r0 = r16;
        r9.<init>(r0);	 Catch:{ Throwable -> 0x007e }
        r12 = r9.openConnection();	 Catch:{ Throwable -> 0x007e }
        r21 = "Cookie";
        r0 = r21;
        r12.setRequestProperty(r0, r6);	 Catch:{ Throwable -> 0x007e }
        r21 = "User-Agent";
        r22 = "Mozilla/5.0 (X11; Linux x86_64; rv:10.0) Gecko/20150101 Firefox/47.0 (Chrome)";
        r0 = r21;
        r1 = r22;
        r12.addRequestProperty(r0, r1);	 Catch:{ Throwable -> 0x007e }
        if (r28 == 0) goto L_0x014c;
    L_0x013f:
        r21 = "Accept-Encoding";
        r22 = "gzip, deflate";
        r0 = r21;
        r1 = r22;
        r12.addRequestProperty(r0, r1);	 Catch:{ Throwable -> 0x007e }
    L_0x014c:
        r21 = "Accept-Language";
        r22 = "en-us,en;q=0.5";
        r0 = r21;
        r1 = r22;
        r12.addRequestProperty(r0, r1);	 Catch:{ Throwable -> 0x007e }
        r21 = "Accept";
        r22 = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
        r0 = r21;
        r1 = r22;
        r12.addRequestProperty(r0, r1);	 Catch:{ Throwable -> 0x007e }
        r21 = "Accept-Charset";
        r22 = "ISO-8859-1,utf-8;q=0.7,*;q=0.7";
        r0 = r21;
        r1 = r22;
        r12.addRequestProperty(r0, r1);	 Catch:{ Throwable -> 0x007e }
        if (r27 == 0) goto L_0x019d;
    L_0x0175:
        r21 = r27.entrySet();	 Catch:{ Throwable -> 0x007e }
        r23 = r21.iterator();	 Catch:{ Throwable -> 0x007e }
    L_0x017d:
        r21 = r23.hasNext();	 Catch:{ Throwable -> 0x007e }
        if (r21 == 0) goto L_0x019d;
    L_0x0183:
        r11 = r23.next();	 Catch:{ Throwable -> 0x007e }
        r11 = (java.util.Map.Entry) r11;	 Catch:{ Throwable -> 0x007e }
        r21 = r11.getKey();	 Catch:{ Throwable -> 0x007e }
        r21 = (java.lang.String) r21;	 Catch:{ Throwable -> 0x007e }
        r22 = r11.getValue();	 Catch:{ Throwable -> 0x007e }
        r22 = (java.lang.String) r22;	 Catch:{ Throwable -> 0x007e }
        r0 = r21;
        r1 = r22;
        r12.addRequestProperty(r0, r1);	 Catch:{ Throwable -> 0x007e }
        goto L_0x017d;
    L_0x019d:
        r12.connect();	 Catch:{ Throwable -> 0x007e }
        if (r28 == 0) goto L_0x01c3;
    L_0x01a2:
        r14 = new java.util.zip.GZIPInputStream;	 Catch:{ Exception -> 0x01b0 }
        r21 = r12.getInputStream();	 Catch:{ Exception -> 0x01b0 }
        r0 = r21;
        r14.<init>(r0);	 Catch:{ Exception -> 0x01b0 }
        r13 = r14;
        goto L_0x008f;
    L_0x01b0:
        r10 = move-exception;
        if (r13 == 0) goto L_0x01b6;
    L_0x01b3:
        r13.close();	 Catch:{ Exception -> 0x0250 }
    L_0x01b6:
        r12 = r9.openConnection();	 Catch:{ Throwable -> 0x007e }
        r12.connect();	 Catch:{ Throwable -> 0x007e }
        r13 = r12.getInputStream();	 Catch:{ Throwable -> 0x007e }
        goto L_0x008f;
    L_0x01c3:
        r13 = r12.getInputStream();	 Catch:{ Throwable -> 0x007e }
        goto L_0x008f;
    L_0x01c9:
        r0 = r10 instanceof java.net.UnknownHostException;
        r21 = r0;
        if (r21 == 0) goto L_0x01d2;
    L_0x01cf:
        r4 = 0;
        goto L_0x008c;
    L_0x01d2:
        r0 = r10 instanceof java.net.SocketException;
        r21 = r0;
        if (r21 == 0) goto L_0x01ee;
    L_0x01d8:
        r21 = r10.getMessage();
        if (r21 == 0) goto L_0x008c;
    L_0x01de:
        r21 = r10.getMessage();
        r22 = "ECONNRESET";
        r21 = r21.contains(r22);
        if (r21 == 0) goto L_0x008c;
    L_0x01eb:
        r4 = 0;
        goto L_0x008c;
    L_0x01ee:
        r0 = r10 instanceof java.io.FileNotFoundException;
        r21 = r0;
        if (r21 == 0) goto L_0x008c;
    L_0x01f4:
        r4 = 0;
        goto L_0x008c;
    L_0x01f7:
        r10 = move-exception;
        org.telegram.messenger.FileLog.e(r10);
        goto L_0x00b1;
    L_0x01fd:
        r17 = r13.read(r7);	 Catch:{ Exception -> 0x0238 }
        if (r17 <= 0) goto L_0x0227;
    L_0x0203:
        if (r19 != 0) goto L_0x0259;
    L_0x0205:
        r18 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0238 }
        r18.<init>();	 Catch:{ Exception -> 0x0238 }
    L_0x020a:
        r21 = new java.lang.String;	 Catch:{ Exception -> 0x0257 }
        r22 = 0;
        r23 = "UTF-8";
        r0 = r21;
        r1 = r22;
        r2 = r17;
        r3 = r23;
        r0.<init>(r7, r1, r2, r3);	 Catch:{ Exception -> 0x0257 }
        r0 = r18;
        r1 = r21;
        r0.append(r1);	 Catch:{ Exception -> 0x0257 }
        r19 = r18;
        goto L_0x00bc;
    L_0x0227:
        r21 = -1;
        r0 = r17;
        r1 = r21;
        if (r0 != r1) goto L_0x0234;
    L_0x022f:
        r8 = 1;
        r18 = r19;
        goto L_0x00c4;
    L_0x0234:
        r18 = r19;
        goto L_0x00c4;
    L_0x0238:
        r10 = move-exception;
        r18 = r19;
    L_0x023b:
        org.telegram.messenger.FileLog.e(r10);	 Catch:{ Throwable -> 0x0240 }
        goto L_0x00c4;
    L_0x0240:
        r10 = move-exception;
    L_0x0241:
        org.telegram.messenger.FileLog.e(r10);
        goto L_0x00c4;
    L_0x0246:
        r10 = move-exception;
        org.telegram.messenger.FileLog.e(r10);
        goto L_0x00c9;
    L_0x024c:
        r21 = 0;
        goto L_0x00cf;
    L_0x0250:
        r21 = move-exception;
        goto L_0x01b6;
    L_0x0253:
        r10 = move-exception;
        r18 = r19;
        goto L_0x0241;
    L_0x0257:
        r10 = move-exception;
        goto L_0x023b;
    L_0x0259:
        r18 = r19;
        goto L_0x020a;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.WebPlayerView.downloadUrlContent(android.os.AsyncTask, java.lang.String, java.util.HashMap, boolean):java.lang.String");
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    public WebPlayerView(Context context, boolean allowInline, boolean allowShare, WebPlayerViewDelegate webPlayerViewDelegate) {
        boolean z;
        super(context);
        int i = lastContainerId;
        lastContainerId = i + 1;
        this.fragment_container_id = i;
        if (VERSION.SDK_INT >= 21) {
            z = true;
        } else {
            z = false;
        }
        this.allowInlineAnimation = z;
        this.backgroundPaint = new Paint();
        this.progressRunnable = new Runnable() {
            public void run() {
                if (WebPlayerView.this.videoPlayer != null && WebPlayerView.this.videoPlayer.isPlaying()) {
                    WebPlayerView.this.controlsView.setProgress((int) (WebPlayerView.this.videoPlayer.getCurrentPosition() / 1000));
                    WebPlayerView.this.controlsView.setBufferedProgress((int) (WebPlayerView.this.videoPlayer.getBufferedPosition() / 1000));
                    AndroidUtilities.runOnUIThread(WebPlayerView.this.progressRunnable, 1000);
                }
            }
        };
        this.surfaceTextureListener = new SurfaceTextureListener() {
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
                    WebPlayerView.this.changedTextureView.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
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
                            AndroidUtilities.runOnUIThread(new WebPlayerView$2$1$$Lambda$0(this));
                            WebPlayerView.this.waitingForFirstTextureUpload = 0;
                            return true;
                        }

                        /* Access modifiers changed, original: final|synthetic */
                        public final /* synthetic */ void lambda$onPreDraw$0$WebPlayerView$2$1() {
                            WebPlayerView.this.delegate.onInlineSurfaceTextureReady();
                        }
                    });
                    WebPlayerView.this.changedTextureView.invalidate();
                }
            }
        };
        this.switchToInlineRunnable = new Runnable() {
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
                        FileLog.e(e);
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
        };
        setWillNotDraw(false);
        this.delegate = webPlayerViewDelegate;
        this.backgroundPaint.setColor(-16777216);
        this.aspectRatioFrameLayout = new AspectRatioFrameLayout(context) {
            /* Access modifiers changed, original: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
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
        addView(this.aspectRatioFrameLayout, LayoutHelper.createFrame(-1, -1, 17));
        this.interfaceName = "JavaScriptInterface";
        this.webView = new WebView(context);
        this.webView.addJavascriptInterface(new JavaScriptInterface(new WebPlayerView$$Lambda$0(this)), this.interfaceName);
        WebSettings webSettings = this.webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDefaultTextEncodingName("utf-8");
        this.textureViewContainer = this.delegate.getTextureViewContainer();
        this.textureView = new TextureView(context);
        this.textureView.setPivotX(0.0f);
        this.textureView.setPivotY(0.0f);
        if (this.textureViewContainer != null) {
            this.textureViewContainer.addView(this.textureView);
        } else {
            this.aspectRatioFrameLayout.addView(this.textureView, LayoutHelper.createFrame(-1, -1, 17));
        }
        if (this.allowInlineAnimation && this.textureViewContainer != null) {
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
        this.fullscreenButton.setOnClickListener(new WebPlayerView$$Lambda$1(this));
        this.playButton = new ImageView(context);
        this.playButton.setScaleType(ScaleType.CENTER);
        this.controlsView.addView(this.playButton, LayoutHelper.createFrame(48, 48, 17));
        this.playButton.setOnClickListener(new WebPlayerView$$Lambda$2(this));
        if (allowInline) {
            this.inlineButton = new ImageView(context);
            this.inlineButton.setScaleType(ScaleType.CENTER);
            this.controlsView.addView(this.inlineButton, LayoutHelper.createFrame(56, 48, 53));
            this.inlineButton.setOnClickListener(new WebPlayerView$$Lambda$3(this));
        }
        if (allowShare) {
            this.shareButton = new ImageView(context);
            this.shareButton.setScaleType(ScaleType.CENTER);
            this.shareButton.setImageResource(NUM);
            this.controlsView.addView(this.shareButton, LayoutHelper.createFrame(56, 48, 53));
            this.shareButton.setOnClickListener(new WebPlayerView$$Lambda$4(this));
        }
        updatePlayButton();
        updateFullscreenButton();
        updateInlineButton();
        updateShareButton();
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$new$0$WebPlayerView(String value) {
        if (this.currentTask != null && !this.currentTask.isCancelled() && (this.currentTask instanceof YoutubeVideoTask)) {
            ((YoutubeVideoTask) this.currentTask).onInterfaceResult(value);
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$new$1$WebPlayerView(View v) {
        if (this.initied && !this.changingTextureView && !this.switchingInlineMode && this.firstFrameRendered) {
            this.inFullscreen = !this.inFullscreen;
            updateFullscreenState(true);
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$new$2$WebPlayerView(View v) {
        if (this.initied && this.playVideoUrl != null) {
            if (!this.videoPlayer.isPlayerPrepared()) {
                preparePlayer();
            }
            if (this.videoPlayer.isPlaying()) {
                this.videoPlayer.pause();
            } else {
                this.isCompleted = false;
                this.videoPlayer.play();
            }
            updatePlayButton();
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$new$3$WebPlayerView(View v) {
        if (this.textureView != null && this.delegate.checkInlinePermissions() && !this.changingTextureView && !this.switchingInlineMode && this.firstFrameRendered) {
            this.switchingInlineMode = true;
            if (this.isInline) {
                ViewGroup parent = (ViewGroup) this.aspectRatioFrameLayout.getParent();
                if (parent != this) {
                    if (parent != null) {
                        parent.removeView(this.aspectRatioFrameLayout);
                    }
                    addView(this.aspectRatioFrameLayout, 0, LayoutHelper.createFrame(-1, -1, 17));
                    this.aspectRatioFrameLayout.measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth(), NUM), MeasureSpec.makeMeasureSpec(getMeasuredHeight() - AndroidUtilities.dp(10.0f), NUM));
                }
                if (this.currentBitmap != null) {
                    this.currentBitmap.recycle();
                    this.currentBitmap = null;
                }
                this.changingTextureView = true;
                this.isInline = false;
                updatePlayButton();
                updateShareButton();
                updateFullscreenButton();
                updateInlineButton();
                this.textureView.setVisibility(4);
                if (this.textureViewContainer != null) {
                    this.textureViewContainer.addView(this.textureView);
                } else {
                    this.aspectRatioFrameLayout.addView(this.textureView);
                }
                parent = (ViewGroup) this.controlsView.getParent();
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
                this.controlsView.show(false, false);
                this.delegate.prepareToSwitchInlineMode(false, null, this.aspectRatioFrameLayout.getAspectRatio(), this.allowInlineAnimation);
                return;
            }
            this.inFullscreen = false;
            this.delegate.prepareToSwitchInlineMode(true, this.switchToInlineRunnable, this.aspectRatioFrameLayout.getAspectRatio(), this.allowInlineAnimation);
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$new$4$WebPlayerView(View v) {
        if (this.delegate != null) {
            this.delegate.onSharePressed();
        }
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
                FileLog.e(e);
            }
            if (this.currentBitmap != null) {
                this.textureImageView.setVisibility(0);
                this.textureImageView.setImageBitmap(this.currentBitmap);
                return;
            }
            this.textureImageView.setImageDrawable(null);
        }
    }

    public String getYoutubeId() {
        return this.currentYoutubeId;
    }

    public void onStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState != 2) {
            if (this.videoPlayer.getDuration() != -9223372036854775807L) {
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

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) (getMeasuredHeight() - AndroidUtilities.dp(10.0f)), this.backgroundPaint);
    }

    public void onError(Exception e) {
        FileLog.e((Throwable) e);
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

    /* Access modifiers changed, original: protected */
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        int x = ((r - l) - this.aspectRatioFrameLayout.getMeasuredWidth()) / 2;
        int y = (((b - t) - AndroidUtilities.dp(10.0f)) - this.aspectRatioFrameLayout.getMeasuredHeight()) / 2;
        this.aspectRatioFrameLayout.layout(x, y, this.aspectRatioFrameLayout.getMeasuredWidth() + x, this.aspectRatioFrameLayout.getMeasuredHeight() + y);
        if (this.controlsView.getParent() == this) {
            this.controlsView.layout(0, 0, this.controlsView.getMeasuredWidth(), this.controlsView.getMeasuredHeight());
        }
        x = ((r - l) - this.progressView.getMeasuredWidth()) / 2;
        y = ((b - t) - this.progressView.getMeasuredHeight()) / 2;
        this.progressView.layout(x, y, this.progressView.getMeasuredWidth() + x, this.progressView.getMeasuredHeight() + y);
        this.controlsView.imageReceiver.setImageCoords(0, 0, getMeasuredWidth(), getMeasuredHeight() - AndroidUtilities.dp(10.0f));
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
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
            this.playButton.setImageResource(this.isInline ? NUM : NUM);
            AndroidUtilities.runOnUIThread(this.progressRunnable, 500);
            checkAudioFocus();
        } else if (this.isCompleted) {
            this.playButton.setImageResource(this.isInline ? NUM : NUM);
        } else {
            this.playButton.setImageResource(this.isInline ? NUM : NUM);
        }
    }

    private void checkAudioFocus() {
        if (!this.hasAudioFocus) {
            AudioManager audioManager = (AudioManager) ApplicationLoader.applicationContext.getSystemService("audio");
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
        if (!this.videoPlayer.isPlayerPrepared() || this.isInline) {
            this.fullscreenButton.setVisibility(8);
            return;
        }
        this.fullscreenButton.setVisibility(0);
        if (this.inFullscreen) {
            this.fullscreenButton.setImageResource(NUM);
            this.fullscreenButton.setLayoutParams(LayoutHelper.createFrame(56, 56.0f, 85, 0.0f, 0.0f, 0.0f, 1.0f));
            return;
        }
        this.fullscreenButton.setImageResource(NUM);
        this.fullscreenButton.setLayoutParams(LayoutHelper.createFrame(56, 56.0f, 85, 0.0f, 0.0f, 0.0f, 5.0f));
    }

    private void updateShareButton() {
        if (this.shareButton != null) {
            ImageView imageView = this.shareButton;
            int i = (this.isInline || !this.videoPlayer.isPlayerPrepared()) ? 8 : 0;
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
            this.inlineButton.setImageResource(this.isInline ? NUM : NUM);
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
            if (this.videoPlayer.getDuration() != -9223372036854775807L) {
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
            ViewGroup parent;
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
                    parent = (ViewGroup) this.controlsView.getParent();
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
                    parent = (ViewGroup) this.textureView.getParent();
                    if (parent != null) {
                        parent.removeView(this.textureView);
                    }
                }
                this.controlsView.checkNeedHide();
                return;
            }
            if (this.inFullscreen) {
                viewGroup = (ViewGroup) this.aspectRatioFrameLayout.getParent();
                if (viewGroup != null) {
                    viewGroup.removeView(this.aspectRatioFrameLayout);
                }
            } else {
                parent = (ViewGroup) this.aspectRatioFrameLayout.getParent();
                if (parent != this) {
                    if (parent != null) {
                        parent.removeView(this.aspectRatioFrameLayout);
                    }
                    addView(this.aspectRatioFrameLayout, 0);
                }
            }
            this.delegate.onSwitchToFullscreen(this.controlsView, this.inFullscreen, this.aspectRatioFrameLayout.getAspectRatio(), this.aspectRatioFrameLayout.getVideoRotation(), byButton);
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
        return this.isInline || this.switchingInlineMode;
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

    public boolean loadVideo(String url, Photo thumb, Object parentObject, String originalUrl, boolean autoplay) {
        String youtubeId = null;
        String vimeoId = null;
        String coubId = null;
        String twitchClipId = null;
        String twitchStreamId = null;
        String mp4File = null;
        String aparatId = null;
        this.seekToTime = -1;
        if (url != null) {
            if (url.endsWith(".mp4")) {
                mp4File = url;
            } else {
                Matcher matcher;
                String id;
                if (originalUrl != null) {
                    try {
                        Uri uri = Uri.parse(originalUrl);
                        String t = uri.getQueryParameter("t");
                        if (t == null) {
                            t = uri.getQueryParameter("time_continue");
                        }
                        if (t != null) {
                            if (t.contains("m")) {
                                String[] args = t.split("m");
                                this.seekToTime = (Utilities.parseInt(args[0]).intValue() * 60) + Utilities.parseInt(args[1]).intValue();
                            } else {
                                this.seekToTime = Utilities.parseInt(t).intValue();
                            }
                        }
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
                try {
                    matcher = youtubeIdRegex.matcher(url);
                    id = null;
                    if (matcher.find()) {
                        id = matcher.group(1);
                    }
                    if (id != null) {
                        youtubeId = id;
                    }
                } catch (Exception e2) {
                    FileLog.e(e2);
                }
                if (youtubeId == null) {
                    try {
                        matcher = vimeoIdRegex.matcher(url);
                        id = null;
                        if (matcher.find()) {
                            id = matcher.group(3);
                        }
                        if (id != null) {
                            vimeoId = id;
                        }
                    } catch (Exception e22) {
                        FileLog.e(e22);
                    }
                }
                if (vimeoId == null) {
                    try {
                        matcher = aparatIdRegex.matcher(url);
                        id = null;
                        if (matcher.find()) {
                            id = matcher.group(1);
                        }
                        if (id != null) {
                            aparatId = id;
                        }
                    } catch (Exception e222) {
                        FileLog.e(e222);
                    }
                }
                if (aparatId == null) {
                    try {
                        matcher = twitchClipIdRegex.matcher(url);
                        id = null;
                        if (matcher.find()) {
                            id = matcher.group(1);
                        }
                        if (id != null) {
                            twitchClipId = id;
                        }
                    } catch (Exception e2222) {
                        FileLog.e(e2222);
                    }
                }
                if (twitchClipId == null) {
                    try {
                        matcher = twitchStreamIdRegex.matcher(url);
                        id = null;
                        if (matcher.find()) {
                            id = matcher.group(1);
                        }
                        if (id != null) {
                            twitchStreamId = id;
                        }
                    } catch (Exception e22222) {
                        FileLog.e(e22222);
                    }
                }
                if (twitchStreamId == null) {
                    try {
                        matcher = coubIdRegex.matcher(url);
                        id = null;
                        if (matcher.find()) {
                            id = matcher.group(1);
                        }
                        if (id != null) {
                            coubId = id;
                        }
                    } catch (Exception e222222) {
                        FileLog.e(e222222);
                    }
                }
            }
        }
        this.initied = false;
        this.isCompleted = false;
        this.isAutoplay = autoplay;
        this.playVideoUrl = null;
        this.playAudioUrl = null;
        destroy();
        this.firstFrameRendered = false;
        this.currentAlpha = 1.0f;
        if (this.currentTask != null) {
            this.currentTask.cancel(true);
            this.currentTask = null;
        }
        updateFullscreenButton();
        updateShareButton();
        updateInlineButton();
        updatePlayButton();
        if (thumb == null) {
            this.drawImage = false;
        } else if (FileLoader.getClosestPhotoSizeWithSize(thumb.sizes, 80, true) != null) {
            this.controlsView.imageReceiver.setImage(null, null, thumb, "80_80_b", 0, null, parentObject, 1);
            this.drawImage = true;
        }
        if (this.progressAnimation != null) {
            this.progressAnimation.cancel();
            this.progressAnimation = null;
        }
        this.isLoading = true;
        this.controlsView.setProgress(0);
        if (youtubeId != null) {
            this.currentYoutubeId = youtubeId;
            youtubeId = null;
        }
        if (mp4File != null) {
            this.initied = true;
            this.playVideoUrl = mp4File;
            this.playVideoType = "other";
            if (this.isAutoplay) {
                preparePlayer();
            }
            showProgress(false, false);
            this.controlsView.show(true, true);
        } else {
            AsyncTask youtubeVideoTask;
            if (youtubeId != null) {
                youtubeVideoTask = new YoutubeVideoTask(youtubeId);
                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                this.currentTask = youtubeVideoTask;
            } else if (vimeoId != null) {
                youtubeVideoTask = new VimeoVideoTask(vimeoId);
                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                this.currentTask = youtubeVideoTask;
            } else if (coubId != null) {
                youtubeVideoTask = new CoubVideoTask(coubId);
                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                this.currentTask = youtubeVideoTask;
                this.isStream = true;
            } else if (aparatId != null) {
                youtubeVideoTask = new AparatVideoTask(aparatId);
                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                this.currentTask = youtubeVideoTask;
            } else if (twitchClipId != null) {
                youtubeVideoTask = new TwitchClipVideoTask(url, twitchClipId);
                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                this.currentTask = youtubeVideoTask;
            } else if (twitchStreamId != null) {
                youtubeVideoTask = new TwitchStreamVideoTask(url, twitchStreamId);
                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                this.currentTask = youtubeVideoTask;
                this.isStream = true;
            }
            this.controlsView.show(false, false);
            showProgress(true, false);
        }
        if (youtubeId == null && vimeoId == null && coubId == null && aparatId == null && mp4File == null && twitchClipId == null && twitchStreamId == null) {
            this.controlsView.setVisibility(8);
            return false;
        }
        this.controlsView.setVisibility(0);
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
        this.videoPlayer.releasePlayer(false);
        if (this.currentTask != null) {
            this.currentTask.cancel(true);
            this.currentTask = null;
        }
        this.webView.stopLoading();
    }

    private void showProgress(boolean show, boolean animated) {
        float f = 1.0f;
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
            if (!show) {
                f = 0.0f;
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
        if (!show) {
            f = 0.0f;
        }
        radialProgressView2.setAlpha(f);
    }
}
