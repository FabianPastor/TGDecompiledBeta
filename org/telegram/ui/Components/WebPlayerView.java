package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
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
import android.support.v4.internal.view.SupportMenu;
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
import java.util.concurrent.Semaphore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.Bitmaps;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.DefaultLoadControl;
import org.telegram.messenger.exoplayer2.ui.AspectRatioFrameLayout;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC.Photo;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate;

@TargetApi(16)
public class WebPlayerView extends ViewGroup implements VideoPlayerDelegate, OnAudioFocusChangeListener {
    private static final int AUDIO_FOCUSED = 2;
    private static final int AUDIO_NO_FOCUS_CAN_DUCK = 1;
    private static final int AUDIO_NO_FOCUS_NO_DUCK = 0;
    private static final Pattern aparatFileListPattern = Pattern.compile("fileList\\s*=\\s*JSON\\.parse\\('([^']+)'\\)");
    private static final Pattern aparatIdRegex = Pattern.compile("^https?://(?:www\\.)?aparat\\.com/(?:v/|video/video/embed/videohash/)([a-zA-Z0-9]+)");
    private static final Pattern coubIdRegex = Pattern.compile("(?:coub:|https?://(?:coub\\.com/(?:view|embed|coubs)/|c-cdn\\.coub\\.com/fb-player\\.swf\\?.*\\bcoub(?:ID|id)=))([\\da-z]+)");
    private static final String exprName = "[a-zA-Z_$][a-zA-Z_$0-9]*";
    private static final Pattern exprParensPattern = Pattern.compile("[()]");
    private static final Pattern jsPattern = Pattern.compile("\"assets\":.+?\"js\":\\s*(\"[^\"]+\")");
    private static final Pattern playerIdPattern = Pattern.compile(".*?-([a-zA-Z0-9_-]+)(?:/watch_as3|/html5player(?:-new)?|/base)?\\.([a-z]+)$");
    private static final Pattern sigPattern = Pattern.compile("\\.sig\\|\\|([a-zA-Z0-9$]+)\\(");
    private static final Pattern sigPattern2 = Pattern.compile("[\"']signature[\"']\\s*,\\s*([a-zA-Z0-9$]+)\\(");
    private static final Pattern stmtReturnPattern = Pattern.compile("return(?:\\s+|$)");
    private static final Pattern stmtVarPattern = Pattern.compile("var\\s");
    private static final Pattern stsPattern = Pattern.compile("\"sts\"\\s*:\\s*(\\d+)");
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
    private WebPlayerViewDelegate delegate;
    private boolean drawImage;
    private boolean firstFrameRendered;
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

    private class AparatVideoTask extends AsyncTask<Void, Void, String> {
        private boolean canRetry = true;
        private String[] results = new String[2];
        private String videoId;

        public AparatVideoTask(String vid) {
            this.videoId = vid;
        }

        protected String doInBackground(Void... voids) {
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
            } catch (Throwable e) {
                FileLog.e(e);
            }
            return isCancelled() ? null : this.results[0];
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
        private int bufferedPercentage;
        private int bufferedPosition;
        private AnimatorSet currentAnimation;
        private int currentProgressX;
        private int duration;
        private StaticLayout durationLayout;
        private int durationWidth;
        private Runnable hideRunnable = new Runnable() {
            public void run() {
                ControlsView.this.show(false, true);
            }
        };
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
                this.duration = value;
                this.durationLayout = new StaticLayout(String.format(Locale.US, "%d:%02d", new Object[]{Integer.valueOf(this.duration / 60), Integer.valueOf(this.duration % 60)}), this.textPaint, AndroidUtilities.dp(1000.0f), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                if (this.durationLayout.getLineCount() > 0) {
                    this.durationWidth = (int) Math.ceil((double) this.durationLayout.getLineWidth(0));
                }
                invalidate();
            }
        }

        public void setBufferedProgress(int position, int percentage) {
            this.bufferedPosition = position;
            this.bufferedPercentage = percentage;
            invalidate();
        }

        public void setProgress(int value) {
            if (!this.progressPressed && value >= 0) {
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
                if (!this.isVisible || WebPlayerView.this.isInline) {
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

        protected void onDraw(Canvas canvas) {
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
            if (WebPlayerView.this.videoPlayer.isPlayerPrepared()) {
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
                    if (!(this.bufferedPercentage == 0 || this.duration == 0)) {
                        int start = progressLineX + (this.bufferedPosition * ((progressLineEndX - progressLineX) / this.duration));
                        int additional = 0;
                        if (progressX < start) {
                            additional = start - progressX;
                        }
                        canvas.drawRect((float) (start - additional), (float) progressLineY, (((float) ((progressLineEndX - start) * this.bufferedPercentage)) / 100.0f) + ((float) start), (float) (AndroidUtilities.dp(3.0f) + progressLineY), WebPlayerView.this.inFullscreen ? this.progressBufferedPaint : this.progressInnerPaint);
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

        protected String doInBackground(Void... voids) {
            String playerCode = WebPlayerView.this.downloadUrlContent(this, String.format(Locale.US, "https://coub.com/api/v2/coubs/%s.json", new Object[]{this.videoId}));
            if (isCancelled()) {
                return null;
            }
            try {
                JSONObject json = new JSONObject(playerCode);
                String video = json.getString("file");
                String audio = json.getString("audio_file_url");
                if (!(video == null || audio == null)) {
                    this.results[0] = video;
                    this.results[1] = "other";
                    this.results[2] = audio;
                    this.results[3] = "other";
                }
            } catch (Throwable e) {
                FileLog.e(e);
            }
            if (isCancelled()) {
                return null;
            }
            return this.results[0];
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
                Matcher matcher;
                if (expr.charAt(0) == '(') {
                    int parens_count = 0;
                    matcher = WebPlayerView.exprParensPattern.matcher(expr);
                    while (matcher.find()) {
                        if (matcher.group(0).indexOf(48) == 40) {
                            parens_count++;
                        } else {
                            parens_count--;
                            if (parens_count == 0) {
                                interpretExpression(expr.substring(1, matcher.start()), localVars, allowRecursion);
                                String remaining_expr = expr.substring(matcher.end()).trim();
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
                for (String func : this.assign_operators) {
                    matcher = Pattern.compile(String.format(Locale.US, "(?x)(%s)(?:\\[([^\\]]+?)\\])?\\s*%s(.*)$", new Object[]{WebPlayerView.exprName, Pattern.quote(func)})).matcher(expr);
                    if (matcher.find()) {
                        interpretExpression(matcher.group(3), localVars, allowRecursion - 1);
                        String index = matcher.group(2);
                        if (TextUtils.isEmpty(index)) {
                            localVars.put(matcher.group(1), "");
                            return;
                        }
                        interpretExpression(index, localVars, allowRecursion);
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
                                matcher = Pattern.compile(String.format(Locale.US, "(%s)\\.([^(]+)(?:\\(+([^()]*)\\))?$", new Object[]{WebPlayerView.exprName})).matcher(expr);
                                if (matcher.find()) {
                                    String variable = matcher.group(1);
                                    String member = matcher.group(2);
                                    String arg_str = matcher.group(3);
                                    if (localVars.get(variable) == null) {
                                        extractObject(variable);
                                    }
                                    if (arg_str == null) {
                                        return;
                                    }
                                    if (expr.charAt(expr.length() - 1) != ')') {
                                        throw new Exception("last char not ')'");
                                    } else if (arg_str.length() != 0) {
                                        String[] args = arg_str.split(",");
                                        for (String interpretExpression : args) {
                                            interpretExpression(interpretExpression, localVars, allowRecursion);
                                        }
                                        return;
                                    } else {
                                        return;
                                    }
                                }
                                matcher = Pattern.compile(String.format(Locale.US, "(%s)\\[(.+)\\]$", new Object[]{WebPlayerView.exprName})).matcher(expr);
                                if (matcher.find()) {
                                    Object val = localVars.get(matcher.group(1));
                                    interpretExpression(matcher.group(2), localVars, allowRecursion - 1);
                                    return;
                                }
                                for (String func2 : this.operators) {
                                    matcher = Pattern.compile(String.format(Locale.US, "(.+?)%s(.+)", new Object[]{Pattern.quote(func2)})).matcher(expr);
                                    if (matcher.find()) {
                                        boolean[] abort = new boolean[1];
                                        interpretStatement(matcher.group(1), localVars, abort, allowRecursion - 1);
                                        if (abort[0]) {
                                            throw new Exception(String.format("Premature left-side return of %s in %s", new Object[]{func2, expr}));
                                        }
                                        interpretStatement(matcher.group(2), localVars, abort, allowRecursion - 1);
                                        if (abort[0]) {
                                            throw new Exception(String.format("Premature right-side return of %s in %s", new Object[]{func2, expr}));
                                        }
                                    }
                                }
                                matcher = Pattern.compile(String.format(Locale.US, "^(%s)\\(([a-zA-Z0-9_$,]*)\\)$", new Object[]{WebPlayerView.exprName})).matcher(expr);
                                if (matcher.find()) {
                                    extractFunction(matcher.group(1));
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
                    expr = stmt.substring(matcher.group(0).length());
                    abort[0] = true;
                } else {
                    expr = stmt;
                }
            }
            interpretExpression(expr, localVars, allowRecursion);
        }

        private HashMap<String, Object> extractObject(String objname) throws Exception {
            HashMap<String, Object> obj = new HashMap();
            Matcher matcher = Pattern.compile(String.format(Locale.US, "(?:var\\s+)?%s\\s*=\\s*\\{\\s*(([a-zA-Z$0-9]+\\s*:\\s*function\\(.*?\\)\\s*\\{.*?\\}(?:,\\s*)?)*)\\}\\s*;", new Object[]{Pattern.quote(objname)})).matcher(this.jsCode);
            String fields = null;
            while (matcher.find()) {
                String code = matcher.group();
                fields = matcher.group(2);
                if (!TextUtils.isEmpty(fields)) {
                    if (!this.codeLines.contains(code)) {
                        this.codeLines.add(matcher.group());
                    }
                    matcher = Pattern.compile("([a-zA-Z$0-9]+)\\s*:\\s*function\\(([a-z,]+)\\)\\{([^}]+)\\}").matcher(fields);
                    while (matcher.find()) {
                        buildFunction(matcher.group(2).split(","), matcher.group(3));
                    }
                    return obj;
                }
            }
            matcher = Pattern.compile("([a-zA-Z$0-9]+)\\s*:\\s*function\\(([a-z,]+)\\)\\{([^}]+)\\}").matcher(fields);
            while (matcher.find()) {
                buildFunction(matcher.group(2).split(","), matcher.group(3));
            }
            return obj;
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

        private String extractFunction(String funcName) throws Exception {
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
            } catch (Throwable e) {
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

    private class VimeoVideoTask extends AsyncTask<Void, Void, String> {
        private boolean canRetry = true;
        private String[] results = new String[2];
        private String videoId;

        public VimeoVideoTask(String vid) {
            this.videoId = vid;
        }

        protected String doInBackground(Void... voids) {
            String playerCode = WebPlayerView.this.downloadUrlContent(this, String.format(Locale.US, "https://player.vimeo.com/video/%s/config", new Object[]{this.videoId}));
            if (isCancelled()) {
                return null;
            }
            try {
                JSONObject files = new JSONObject(playerCode).getJSONObject("request").getJSONObject("files");
                if (files.has("hls")) {
                    this.results[0] = files.getJSONObject("hls").getString("url");
                    this.results[1] = "hls";
                } else if (files.has("progressive")) {
                    this.results[1] = "other";
                    JSONArray progressive = files.getJSONArray("progressive");
                    if (0 < progressive.length()) {
                        this.results[0] = progressive.getJSONObject(0).getString("url");
                    }
                }
            } catch (Throwable e) {
                FileLog.e(e);
            }
            if (isCancelled()) {
                return null;
            }
            return this.results[0];
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
        boolean checkInlinePermissons();

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

    private class YoutubeVideoTask extends AsyncTask<Void, Void, String> {
        private boolean canRetry = true;
        private String[] result = new String[1];
        private Semaphore semaphore = new Semaphore(0);
        private String sig;
        private String videoId;

        public YoutubeVideoTask(String vid) {
            this.videoId = vid;
        }

        protected String doInBackground(Void... voids) {
            String embedCode = WebPlayerView.this.downloadUrlContent(this, "https://www.youtube.com/embed/" + this.videoId);
            if (isCancelled()) {
                return null;
            }
            Matcher matcher;
            String params = "video_id=" + this.videoId + "&ps=default&gl=US&hl=en";
            try {
                params = params + "&eurl=" + URLEncoder.encode("https://youtube.googleapis.com/v/" + this.videoId, "UTF-8");
            } catch (Throwable e) {
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
            boolean encrypted = false;
            String[] extra = new String[]{"", "&el=info", "&el=embedded", "&el=detailpage", "&el=vevo"};
            for (String str : extra) {
                String videoInfo = WebPlayerView.this.downloadUrlContent(this, "https://www.youtube.com/get_video_info?" + params + str);
                if (isCancelled()) {
                    return null;
                }
                boolean exists = false;
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
                                } catch (Throwable e2) {
                                    FileLog.e(e2);
                                }
                            }
                        } else if (args[a].startsWith("use_cipher_signature")) {
                            args2 = args[a].split("=");
                            if (args2.length == 2 && args2[1].toLowerCase().equals("true")) {
                                encrypted = true;
                            }
                        }
                    }
                }
                if (exists) {
                    break;
                }
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
                        } catch (Throwable e22) {
                            FileLog.e(e22);
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
                                    } catch (Throwable e222) {
                                        FileLog.e(e222);
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
                            final String functionCodeFinal = functionCode;
                            try {
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public void run() {
                                        if (VERSION.SDK_INT >= 21) {
                                            WebPlayerView.this.webView.evaluateJavascript(functionCodeFinal, new ValueCallback<String>() {
                                                public void onReceiveValue(String value) {
                                                    YoutubeVideoTask.this.result[0] = YoutubeVideoTask.this.result[0].replace(YoutubeVideoTask.this.sig, "/signature/" + value.substring(1, value.length() - 1));
                                                    YoutubeVideoTask.this.semaphore.release();
                                                }
                                            });
                                            return;
                                        }
                                        try {
                                            WebPlayerView.this.webView.loadUrl("data:text/html;charset=utf-8;base64," + Base64.encodeToString(("<script>" + functionCodeFinal + "</script>").getBytes("UTF-8"), 0));
                                        } catch (Throwable e) {
                                            FileLog.e(e);
                                        }
                                    }
                                });
                                this.semaphore.acquire();
                                encrypted = false;
                            } catch (Throwable e2222) {
                                FileLog.e(e2222);
                            }
                        }
                    }
                }
            }
            if (isCancelled() || encrypted) {
                return null;
            }
            return this.result[0];
        }

        private void onInterfaceResult(String value) {
            this.result[0] = this.result[0].replace(this.sig, "/signature/" + value);
            this.semaphore.release();
        }

        protected void onPostExecute(String result) {
            if (result != null) {
                WebPlayerView.this.initied = true;
                WebPlayerView.this.playVideoType = "dash";
                WebPlayerView.this.playVideoUrl = result;
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

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected String downloadUrlContent(AsyncTask parentTask, String url) {
        Throwable e;
        boolean canRetry = true;
        InputStream httpConnectionStream = null;
        boolean done = false;
        StringBuilder result = null;
        URLConnection httpConnection = null;
        try {
            httpConnection = new URL(url).openConnection();
            httpConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:10.0) Gecko/20150101 Firefox/47.0 (Chrome)");
            httpConnection.addRequestProperty("Accept-Encoding", "gzip, deflate");
            httpConnection.addRequestProperty("Accept-Language", "en-us,en;q=0.5");
            httpConnection.addRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            httpConnection.addRequestProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
            httpConnection.setConnectTimeout(DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS);
            httpConnection.setReadTimeout(DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS);
            if (httpConnection instanceof HttpURLConnection) {
                HttpURLConnection httpURLConnection = (HttpURLConnection) httpConnection;
                httpURLConnection.setInstanceFollowRedirects(true);
                int status = httpURLConnection.getResponseCode();
                if (status == 302 || status == 301 || status == 303) {
                    String newUrl = httpURLConnection.getHeaderField("Location");
                    String cookies = httpURLConnection.getHeaderField("Set-Cookie");
                    httpConnection = new URL(newUrl).openConnection();
                    httpConnection.setRequestProperty("Cookie", cookies);
                    httpConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:10.0) Gecko/20150101 Firefox/47.0 (Chrome)");
                    httpConnection.addRequestProperty("Accept-Encoding", "gzip, deflate");
                    httpConnection.addRequestProperty("Accept-Language", "en-us,en;q=0.5");
                    httpConnection.addRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
                    httpConnection.addRequestProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
                }
            }
            httpConnection.connect();
            httpConnectionStream = new GZIPInputStream(httpConnection.getInputStream());
        } catch (Throwable e2) {
            if (e2 instanceof SocketTimeoutException) {
                if (ConnectionsManager.isNetworkOnline()) {
                    canRetry = false;
                }
            } else if (e2 instanceof UnknownHostException) {
                canRetry = false;
            } else if (e2 instanceof SocketException) {
                if (e2.getMessage() != null && e2.getMessage().contains("ECONNRESET")) {
                    canRetry = false;
                }
            } else if (e2 instanceof FileNotFoundException) {
                canRetry = false;
            }
            FileLog.e(e2);
        }
        if (canRetry) {
            if (httpConnection != null) {
                try {
                    if (httpConnection instanceof HttpURLConnection) {
                        int code = ((HttpURLConnection) httpConnection).getResponseCode();
                        if (code != 200) {
                            if (code != 202) {
                            }
                        }
                    }
                } catch (Throwable e22) {
                    FileLog.e(e22);
                }
            }
            if (httpConnectionStream != null) {
                try {
                    byte[] data = new byte[32768];
                    StringBuilder result2 = null;
                    while (!parentTask.isCancelled()) {
                        try {
                            try {
                                int read = httpConnectionStream.read(data);
                                if (read > 0) {
                                    if (result2 == null) {
                                        result = new StringBuilder();
                                    } else {
                                        result = result2;
                                    }
                                    try {
                                        result.append(new String(data, 0, read, "UTF-8"));
                                        result2 = result;
                                    } catch (Exception e3) {
                                        e22 = e3;
                                    }
                                } else if (read == -1) {
                                    done = true;
                                    result = result2;
                                } else {
                                    result = result2;
                                }
                            } catch (Exception e4) {
                                e22 = e4;
                                result = result2;
                            }
                        } catch (Throwable th) {
                            e22 = th;
                            result = result2;
                        }
                    }
                    result = result2;
                } catch (Throwable th2) {
                    e22 = th2;
                    FileLog.e(e22);
                    if (httpConnectionStream != null) {
                        try {
                            httpConnectionStream.close();
                        } catch (Throwable e222) {
                            FileLog.e(e222);
                        }
                    }
                    if (done) {
                        return null;
                    }
                    return result.toString();
                }
            }
            if (httpConnectionStream != null) {
                httpConnectionStream.close();
            }
        }
        if (done) {
            return result.toString();
        }
        return null;
        FileLog.e(e222);
        if (httpConnectionStream != null) {
            httpConnectionStream.close();
        }
        if (done) {
            return result.toString();
        }
        return null;
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    public WebPlayerView(Context context, boolean allowInline, boolean allowShare, WebPlayerViewDelegate webPlayerViewDelegate) {
        boolean z;
        super(context);
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
                    WebPlayerView.this.controlsView.setBufferedProgress((int) (WebPlayerView.this.videoPlayer.getBufferedPosition() / 1000), WebPlayerView.this.videoPlayer.getBufferedPercentage());
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
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    WebPlayerView.this.delegate.onInlineSurfaceTextureReady();
                                }
                            });
                            WebPlayerView.this.waitingForFirstTextureUpload = 0;
                            return true;
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
        addView(this.aspectRatioFrameLayout, LayoutHelper.createFrame(-1, -1, 17));
        this.interfaceName = "JavaScriptInterface";
        this.webView = new WebView(context);
        this.webView.addJavascriptInterface(new JavaScriptInterface(new CallJavaResultInterface() {
            public void jsCallFinished(String value) {
                if (WebPlayerView.this.currentTask != null && !WebPlayerView.this.currentTask.isCancelled() && (WebPlayerView.this.currentTask instanceof YoutubeVideoTask)) {
                    ((YoutubeVideoTask) WebPlayerView.this.currentTask).onInterfaceResult(value);
                }
            }
        }), this.interfaceName);
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
            this.textureImageView.setBackgroundColor(SupportMenu.CATEGORY_MASK);
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
        this.fullscreenButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (WebPlayerView.this.initied && !WebPlayerView.this.changingTextureView && !WebPlayerView.this.switchingInlineMode && WebPlayerView.this.firstFrameRendered) {
                    WebPlayerView.this.inFullscreen = !WebPlayerView.this.inFullscreen;
                    WebPlayerView.this.updateFullscreenState(true);
                }
            }
        });
        this.playButton = new ImageView(context);
        this.playButton.setScaleType(ScaleType.CENTER);
        this.controlsView.addView(this.playButton, LayoutHelper.createFrame(48, 48, 17));
        this.playButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (WebPlayerView.this.initied && WebPlayerView.this.playVideoUrl != null) {
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
        });
        if (allowInline) {
            this.inlineButton = new ImageView(context);
            this.inlineButton.setScaleType(ScaleType.CENTER);
            this.controlsView.addView(this.inlineButton, LayoutHelper.createFrame(56, 48, 53));
            this.inlineButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (WebPlayerView.this.textureView != null && WebPlayerView.this.delegate.checkInlinePermissons() && !WebPlayerView.this.changingTextureView && !WebPlayerView.this.switchingInlineMode && WebPlayerView.this.firstFrameRendered) {
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
                            return;
                        }
                        WebPlayerView.this.inFullscreen = false;
                        WebPlayerView.this.delegate.prepareToSwitchInlineMode(true, WebPlayerView.this.switchToInlineRunnable, WebPlayerView.this.aspectRatioFrameLayout.getAspectRatio(), WebPlayerView.this.allowInlineAnimation);
                    }
                }
            });
        }
        if (allowShare) {
            this.shareButton = new ImageView(context);
            this.shareButton.setScaleType(ScaleType.CENTER);
            this.shareButton.setImageResource(R.drawable.ic_share_video);
            this.controlsView.addView(this.shareButton, LayoutHelper.createFrame(56, 48, 53));
            this.shareButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (WebPlayerView.this.delegate != null) {
                        WebPlayerView.this.delegate.onSharePressed();
                    }
                }
            });
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

    public void onStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState != 2) {
            if (this.videoPlayer.getDuration() != C.TIME_UNSET) {
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
        FileLog.e((Throwable) e);
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
        x = ((r - l) - this.progressView.getMeasuredWidth()) / 2;
        y = ((b - t) - this.progressView.getMeasuredHeight()) / 2;
        this.progressView.layout(x, y, this.progressView.getMeasuredWidth() + x, this.progressView.getMeasuredHeight() + y);
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
        if (!this.videoPlayer.isPlayerPrepared() || this.isInline) {
            this.fullscreenButton.setVisibility(8);
            return;
        }
        this.fullscreenButton.setVisibility(0);
        if (this.inFullscreen) {
            this.fullscreenButton.setImageResource(R.drawable.ic_outfullscreen);
            this.fullscreenButton.setLayoutParams(LayoutHelper.createFrame(56, 56.0f, 85, 0.0f, 0.0f, 0.0f, 1.0f));
            return;
        }
        this.fullscreenButton.setImageResource(R.drawable.ic_gofullscreen);
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
            if (this.videoPlayer.getDuration() != C.TIME_UNSET) {
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

    public boolean loadVideo(String url, Photo thumb, String originalUrl, boolean autoplay) {
        String youtubeId = null;
        String vimeoId = null;
        String aparatId = null;
        String mp4File = null;
        this.seekToTime = -1;
        if (url != null) {
            if (url.endsWith(".mp4")) {
                mp4File = url;
            } else {
                Matcher matcher;
                String id;
                if (originalUrl != null) {
                    try {
                        String t = Uri.parse(originalUrl).getQueryParameter("t");
                        if (t != null) {
                            if (t.contains("m")) {
                                String[] args = t.split("m");
                                this.seekToTime = (Utilities.parseInt(args[0]).intValue() * 60) + Utilities.parseInt(args[1]).intValue();
                            } else {
                                this.seekToTime = Utilities.parseInt(t).intValue();
                            }
                        }
                    } catch (Throwable e) {
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
                } catch (Throwable e2) {
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
                    } catch (Throwable e22) {
                        FileLog.e(e22);
                    }
                }
                if (youtubeId == null && vimeoId == null) {
                    try {
                        matcher = aparatIdRegex.matcher(url);
                        id = null;
                        if (matcher.find()) {
                            id = matcher.group(1);
                        }
                        if (id != null) {
                            aparatId = id;
                        }
                    } catch (Throwable e222) {
                        FileLog.e(e222);
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
        if (thumb != null) {
            PhotoSize photoSize = FileLoader.getClosestPhotoSizeWithSize(thumb.sizes, 80, true);
            if (photoSize != null) {
                this.controlsView.imageReceiver.setImage(null, null, thumb != null ? photoSize.location : null, thumb != null ? "80_80_b" : null, 0, null, true);
                this.drawImage = true;
            }
        } else {
            this.drawImage = false;
        }
        if (this.progressAnimation != null) {
            this.progressAnimation.cancel();
            this.progressAnimation = null;
        }
        this.isLoading = true;
        this.controlsView.setProgress(0);
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
            } else if (null != null) {
                youtubeVideoTask = new CoubVideoTask(null);
                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                this.currentTask = youtubeVideoTask;
            } else if (aparatId != null) {
                youtubeVideoTask = new AparatVideoTask(aparatId);
                youtubeVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                this.currentTask = youtubeVideoTask;
            }
            this.controlsView.show(false, false);
            showProgress(true, false);
        }
        if (youtubeId != null || vimeoId != null || null != null || aparatId != null || mp4File != null) {
            return true;
        }
        this.controlsView.setVisibility(8);
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
