package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.RectF;
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
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
import org.telegram.messenger.AnimatorListenerAdapterProxy;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.DefaultLoadControl;
import org.telegram.messenger.exoplayer2.ui.AspectRatioFrameLayout;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.volley.DefaultRetryPolicy;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC.Photo;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate;

@TargetApi(16)
public class WebPlayerView extends ViewGroup implements VideoPlayerDelegate, OnAudioFocusChangeListener {
    private static final int AUDIO_FOCUSED = 2;
    private static final int AUDIO_NO_FOCUS_CAN_DUCK = 1;
    private static final int AUDIO_NO_FOCUS_NO_DUCK = 0;
    public static final Pattern coubIdRegex = Pattern.compile("(?:coub:|https?://(?:coub\\.com/(?:view|embed|coubs)/|c-cdn\\.coub\\.com/fb-player\\.swf\\?.*\\bcoub(?:ID|id)=))([\\da-z]+)");
    private static final String exprName = "[a-zA-Z_$][a-zA-Z_$0-9]*";
    private static final Pattern exprParensPattern = Pattern.compile("[()]");
    private static final Pattern jsPattern = Pattern.compile("\"assets\":.+?\"js\":\\s*(\"[^\"]+\")");
    private static final Pattern playerIdPattern = Pattern.compile(".*?-([a-zA-Z0-9_-]+)(?:/watch_as3|/html5player(?:-new)?|/base)?\\.([a-z]+)$");
    private static final Pattern sigPattern = Pattern.compile("\\.sig\\|\\|([a-zA-Z0-9$]+)\\(");
    private static final Pattern stmtReturnPattern = Pattern.compile("return(?:\\s+|$)");
    private static final Pattern stmtVarPattern = Pattern.compile("var\\s");
    private static final Pattern stsPattern = Pattern.compile("\"sts\"\\s*:\\s*(\\d+)");
    public static final Pattern vimeoIdRegex = Pattern.compile("https?://(?:(?:www|(player))\\.)?vimeo(pro)?\\.com/(?!(?:channels|album)/[^/?#]+/?(?:$|[?#])|[^/]+/review/|ondemand/)(?:.*?/)?(?:(?:play_redirect_hls|moogaloop\\.swf)\\?clip_id=)?(?:videos?/)?([0-9]+)(?:/[\\da-f]+)?/?(?:[?&].*)?(?:[#].*)?$");
    public static final Pattern youtubeIdRegex = Pattern.compile("(?:youtube(?:-nocookie)?\\.com/(?:[^/\\n\\s]+/\\S+/|(?:v|e(?:mbed)?)/|\\S*?[?&]v=)|youtu\\.be/)([a-zA-Z0-9_-]{11})");
    private AspectRatioFrameLayout aspectRatioFrameLayout;
    private int audioFocus;
    private Paint backgroundPaint = new Paint();
    private TextureView changedTextureView;
    private boolean changingTextureView;
    private ControlsView controlsView;
    private float currentAlpha;
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
    private Runnable progressRunnable = new Runnable() {
        public void run() {
            if (WebPlayerView.this.videoPlayer != null && WebPlayerView.this.videoPlayer.isPlaying()) {
                WebPlayerView.this.controlsView.setProgress((int) (WebPlayerView.this.videoPlayer.getCurrentPosition() / 1000));
                WebPlayerView.this.controlsView.setBufferedProgress((int) (WebPlayerView.this.videoPlayer.getBufferedPosition() / 1000), WebPlayerView.this.videoPlayer.getBufferedPercentage());
                AndroidUtilities.runOnUIThread(WebPlayerView.this.progressRunnable, 1000);
            }
        }
    };
    private RadialProgressView progressView;
    private boolean resumeAudioOnFocusGain;
    private ImageView shareButton;
    private TextureView textureView;
    private VideoPlayer videoPlayer;
    private WebView webView;

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
                this.durationLayout = new StaticLayout(String.format(Locale.US, "%d:%02d", new Object[]{Integer.valueOf(this.duration / 60), Integer.valueOf(this.duration % 60)}), this.textPaint, AndroidUtilities.dp(1000.0f), Alignment.ALIGN_NORMAL, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false);
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
                this.progressLayout = new StaticLayout(String.format(Locale.US, "%d:%02d", new Object[]{Integer.valueOf(this.progress / 60), Integer.valueOf(this.progress % 60)}), this.textPaint, AndroidUtilities.dp(1000.0f), Alignment.ALIGN_NORMAL, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false);
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
                        animatorArr[0] = ObjectAnimator.ofFloat(this, "alpha", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT});
                        animatorSet.playTogether(animatorArr);
                        this.currentAnimation.setDuration(150);
                        this.currentAnimation.addListener(new AnimatorListenerAdapterProxy() {
                            public void onAnimationEnd(Animator animator) {
                                ControlsView.this.currentAnimation = null;
                            }
                        });
                        this.currentAnimation.start();
                    } else {
                        setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                    }
                } else if (animated) {
                    this.currentAnimation = new AnimatorSet();
                    animatorSet = this.currentAnimation;
                    animatorArr = new Animator[1];
                    animatorArr[0] = ObjectAnimator.ofFloat(this, "alpha", new float[]{0.0f});
                    animatorSet.playTogether(animatorArr);
                    this.currentAnimation.setDuration(150);
                    this.currentAnimation.addListener(new AnimatorListenerAdapterProxy() {
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
                progressY = getMeasuredHeight() - AndroidUtilities.dp(7.0f);
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
                        canvas.translate((float) ((width - AndroidUtilities.dp(58.0f)) - this.durationWidth), (float) (height - AndroidUtilities.dp(34.0f)));
                        this.durationLayout.draw(canvas);
                        canvas.restore();
                    }
                    if (this.progressLayout != null) {
                        canvas.save();
                        canvas.translate((float) AndroidUtilities.dp(18.0f), (float) (height - AndroidUtilities.dp(34.0f)));
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
                        progressLineY = height - AndroidUtilities.dp(8.0f);
                        progressLineX = 0;
                        progressLineEndX = width;
                        cy = height - AndroidUtilities.dp(7.0f);
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
                FileLog.e("tmessages", e);
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
                WebPlayerView.this.delegate.onInitFailed();
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
            Matcher matcher;
            expr = expr.trim();
            if (!TextUtils.isEmpty(expr)) {
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
            matcher.find();
            if (!this.codeLines.contains(matcher.group())) {
                this.codeLines.add(matcher.group());
            }
            matcher = Pattern.compile("([a-zA-Z$0-9]+)\\s*:\\s*function\\(([a-z,]+)\\)\\{([^}]+)\\}").matcher(matcher.group(2));
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
                FileLog.e("tmessages", e);
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

    private class RadialProgressView extends View {
        private AccelerateInterpolator accelerateInterpolator = new AccelerateInterpolator();
        private RectF cicleRect = new RectF();
        private float currentCircleLength;
        private float currentProgressTime;
        private DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
        private long lastUpdateTime;
        private int progressColor = -1;
        private Paint progressPaint = new Paint(1);
        private float radOffset;
        private boolean risingCircleLength;
        private float risingTime = 500.0f;
        private float rotationTime = 2000.0f;

        public RadialProgressView(Context context) {
            super(context);
            this.progressPaint.setStyle(Style.STROKE);
            this.progressPaint.setStrokeCap(Cap.ROUND);
            this.progressPaint.setStrokeWidth((float) AndroidUtilities.dp(3.0f));
            this.progressPaint.setColor(this.progressColor);
        }

        private void updateAnimation() {
            long newTime = System.currentTimeMillis();
            long dt = newTime - this.lastUpdateTime;
            if (dt > 17) {
                dt = 17;
            }
            this.lastUpdateTime = newTime;
            this.radOffset += ((float) (360 * dt)) / this.rotationTime;
            this.radOffset -= (float) (((int) (this.radOffset / 360.0f)) * 360);
            this.currentProgressTime += (float) dt;
            if (this.currentProgressTime >= this.risingTime) {
                this.currentProgressTime = this.risingTime;
            }
            if (this.risingCircleLength) {
                this.currentCircleLength = (266.0f * this.accelerateInterpolator.getInterpolation(this.currentProgressTime / this.risingTime)) + 4.0f;
            } else {
                this.currentCircleLength = 4.0f - ((DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - this.decelerateInterpolator.getInterpolation(this.currentProgressTime / this.risingTime)) * BitmapDescriptorFactory.HUE_VIOLET);
            }
            if (this.currentProgressTime == this.risingTime) {
                if (this.risingCircleLength) {
                    this.radOffset += BitmapDescriptorFactory.HUE_VIOLET;
                    this.currentCircleLength = -266.0f;
                }
                this.risingCircleLength = !this.risingCircleLength;
                this.currentProgressTime = 0.0f;
            }
            invalidate();
        }

        public void setProgressColor(int color) {
            this.progressColor = color;
            this.progressPaint.setColor(this.progressColor);
        }

        protected void onDraw(Canvas canvas) {
            int diff = AndroidUtilities.dp(4.0f);
            this.cicleRect.set((float) diff, (float) diff, (float) (getMeasuredWidth() - diff), (float) (getMeasuredHeight() - diff));
            canvas.drawArc(this.cicleRect, this.radOffset, this.currentCircleLength, false, this.progressPaint);
            updateAnimation();
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
                FileLog.e("tmessages", e);
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
                WebPlayerView.this.delegate.onInitFailed();
            }
        }
    }

    public interface WebPlayerViewDelegate {
        void onInitFailed();

        void onPlayStateChanged(WebPlayerView webPlayerView, boolean z);

        void onSharePressed();

        TextureView onSwitchToFullscreen(View view, boolean z, float f, int i, boolean z2);

        TextureView onSwtichToInline(View view, boolean z, float f, int i);

        void onVideoSizeChanged(float f, int i);
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
            String params = "video_id=" + this.videoId;
            try {
                params = params + "&eurl=" + URLEncoder.encode("https://youtube.googleapis.com/v/" + this.videoId, "UTF-8");
            } catch (Throwable e) {
                FileLog.e("tmessages", e);
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
            String videoInfo = WebPlayerView.this.downloadUrlContent(this, "https://www.youtube.com/get_video_info?" + params);
            if (isCancelled()) {
                return null;
            }
            if (videoInfo != null) {
                String[] args = videoInfo.split("&");
                for (int a = 0; a < args.length; a++) {
                    String[] args2;
                    if (args[a].startsWith("dashmpd")) {
                        args2 = args[a].split("=");
                        if (args2.length == 2) {
                            try {
                                this.result[0] = URLDecoder.decode(args2[1], "UTF-8");
                            } catch (Throwable e2) {
                                FileLog.e("tmessages", e2);
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
            if (encrypted && this.result[0] != null) {
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
                            FileLog.e("tmessages", e22);
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
                            WebPlayerView webPlayerView = WebPlayerView.this;
                            if (jsUrl.startsWith("//")) {
                                jsUrl = "https:" + jsUrl;
                            }
                            String jsCode = webPlayerView.downloadUrlContent(this, jsUrl);
                            if (isCancelled()) {
                                return null;
                            }
                            if (jsCode != null) {
                                matcher = WebPlayerView.sigPattern.matcher(jsCode);
                                if (matcher.find()) {
                                    try {
                                        functionName = matcher.group(1);
                                        functionCode = new JSExtractor(jsCode).extractFunction(functionName);
                                        if (!(TextUtils.isEmpty(functionCode) || playerId == null)) {
                                            preferences.edit().putString(playerId, functionCode).putString(playerId + "n", functionName).commit();
                                        }
                                    } catch (Throwable e222) {
                                        FileLog.e("tmessages", e222);
                                    }
                                }
                            }
                        }
                        if (!TextUtils.isEmpty(functionCode)) {
                            if (VERSION.SDK_INT >= 26) {
                                functionCode = functionCode + functionName + "('" + this.sig.substring(3) + "');";
                            } else {
                                functionCode = functionCode + "window." + WebPlayerView.this.interfaceName + ".returnResultToJava(" + functionName + "('" + this.sig.substring(3) + "'));";
                            }
                            final String functionCodeFinal = functionCode;
                            try {
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public void run() {
                                        if (VERSION.SDK_INT >= 26) {
                                            WebPlayerView.this.webView.evaluateJavascript(functionCodeFinal, new ValueCallback<String>() {
                                                public void onReceiveValue(String value) {
                                                    YoutubeVideoTask.this.result[0] = YoutubeVideoTask.this.result[0].replace(YoutubeVideoTask.this.sig, "/signature/" + value.substring(1, value.length() - 1));
                                                    YoutubeVideoTask.this.semaphore.release();
                                                }
                                            });
                                        } else {
                                            WebPlayerView.this.webView.loadUrl("javascript:" + functionCodeFinal);
                                        }
                                    }
                                });
                                this.semaphore.acquire();
                                encrypted = false;
                            } catch (Throwable e2222) {
                                FileLog.e("tmessages", e2222);
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
                WebPlayerView.this.delegate.onInitFailed();
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
            FileLog.e("tmessages", e2);
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
                    FileLog.e("tmessages", e22);
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
                    FileLog.e("tmessages", e22);
                    if (httpConnectionStream != null) {
                        try {
                            httpConnectionStream.close();
                        } catch (Throwable e222) {
                            FileLog.e("tmessages", e222);
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
        FileLog.e("tmessages", e222);
        if (httpConnectionStream != null) {
            httpConnectionStream.close();
        }
        if (done) {
            return result.toString();
        }
        return null;
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    public WebPlayerView(Context context, boolean allowInline, boolean allowShare) {
        super(context);
        setWillNotDraw(false);
        this.backgroundPaint.setColor(-16777216);
        this.aspectRatioFrameLayout = new AspectRatioFrameLayout(context);
        addView(this.aspectRatioFrameLayout, LayoutHelper.createFrame(-1, -1, 17));
        this.interfaceName = "JavaScriptInterface" + Math.abs(Utilities.random.nextLong());
        this.webView = new WebView(context);
        this.webView.getSettings().setJavaScriptEnabled(true);
        this.webView.addJavascriptInterface(new JavaScriptInterface(new CallJavaResultInterface() {
            public void jsCallFinished(String value) {
                if (WebPlayerView.this.currentTask != null && !WebPlayerView.this.currentTask.isCancelled() && (WebPlayerView.this.currentTask instanceof YoutubeVideoTask)) {
                    ((YoutubeVideoTask) WebPlayerView.this.currentTask).onInterfaceResult(value);
                }
            }
        }), this.interfaceName);
        this.textureView = new TextureView(context);
        this.aspectRatioFrameLayout.addView(this.textureView, LayoutHelper.createFrame(-1, -1, 17));
        this.videoPlayer = new VideoPlayer();
        this.videoPlayer.setDelegate(this);
        this.videoPlayer.setTextureView(this.textureView);
        this.controlsView = new ControlsView(context);
        addView(this.controlsView, LayoutHelper.createFrame(-1, -1.0f));
        this.progressView = new RadialProgressView(context);
        addView(this.progressView, LayoutHelper.createFrame(48, 48, 17));
        this.fullscreenButton = new ImageView(context);
        this.fullscreenButton.setScaleType(ScaleType.CENTER);
        this.controlsView.addView(this.fullscreenButton, LayoutHelper.createFrame(56, 56, 85));
        this.fullscreenButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (WebPlayerView.this.initied) {
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
            this.controlsView.addView(this.inlineButton, LayoutHelper.createFrame(56, 48, 51));
            this.inlineButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (WebPlayerView.this.textureView != null) {
                        WebPlayerView.this.isInline = !WebPlayerView.this.isInline;
                        WebPlayerView.this.updatePlayButton();
                        WebPlayerView.this.updateShareButton();
                        WebPlayerView.this.updateFullscreenButton();
                        WebPlayerView.this.updateInlineButton();
                        WebPlayerView.this.changingTextureView = true;
                        if (WebPlayerView.this.isInline) {
                            WebPlayerView.this.removeView(WebPlayerView.this.controlsView);
                        } else {
                            ViewGroup parent = (ViewGroup) WebPlayerView.this.controlsView.getParent();
                            if (parent != WebPlayerView.this) {
                                if (parent != null) {
                                    parent.removeView(WebPlayerView.this.controlsView);
                                }
                                WebPlayerView.this.addView(WebPlayerView.this.controlsView, 1);
                            }
                        }
                        if (!WebPlayerView.this.isInline) {
                            WebPlayerView.this.aspectRatioFrameLayout.addView(WebPlayerView.this.textureView);
                        }
                        WebPlayerView.this.changedTextureView = WebPlayerView.this.delegate.onSwtichToInline(WebPlayerView.this.controlsView, WebPlayerView.this.isInline, WebPlayerView.this.aspectRatioFrameLayout.getAspectRatio(), WebPlayerView.this.aspectRatioFrameLayout.getVideoRotation());
                        if (WebPlayerView.this.isInline) {
                            WebPlayerView.this.aspectRatioFrameLayout.removeView(WebPlayerView.this.textureView);
                        }
                        WebPlayerView.this.controlsView.checkNeedHide();
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
        canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) (getMeasuredHeight() - AndroidUtilities.dp(5.0f)), this.backgroundPaint);
    }

    public void onError(Exception e) {
        FileLog.e("tmessages", (Throwable) e);
    }

    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        if (this.aspectRatioFrameLayout != null) {
            if (unappliedRotationDegrees == 90 || unappliedRotationDegrees == 270) {
                int temp = width;
                width = height;
                height = temp;
            }
            float ratio = height == 0 ? DefaultRetryPolicy.DEFAULT_BACKOFF_MULT : (((float) width) * pixelWidthHeightRatio) / ((float) height);
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
        if (!this.changingTextureView) {
            return false;
        }
        if (this.inFullscreen || this.isInline) {
            this.changedTextureView.setSurfaceTexture(surfaceTexture);
            this.changedTextureView.setSurfaceTextureListener(new SurfaceTextureListener() {
                public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                }

                public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                }

                public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                    if (!WebPlayerView.this.changingTextureView) {
                        return true;
                    }
                    WebPlayerView.this.textureView.setSurfaceTexture(surface);
                    return false;
                }

                public void onSurfaceTextureUpdated(SurfaceTexture surface) {
                }
            });
            return true;
        }
        this.changingTextureView = false;
        return false;
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int x = ((r - l) - this.aspectRatioFrameLayout.getMeasuredWidth()) / 2;
        int y = (((b - t) - AndroidUtilities.dp(5.0f)) - this.aspectRatioFrameLayout.getMeasuredHeight()) / 2;
        this.aspectRatioFrameLayout.layout(x, y, this.aspectRatioFrameLayout.getMeasuredWidth() + x, this.aspectRatioFrameLayout.getMeasuredHeight() + y);
        if (this.controlsView.getParent() == this) {
            this.controlsView.layout(0, 0, this.controlsView.getMeasuredWidth(), this.controlsView.getMeasuredHeight());
        }
        x = ((r - l) - this.progressView.getMeasuredWidth()) / 2;
        y = ((b - t) - this.progressView.getMeasuredHeight()) / 2;
        this.progressView.layout(x, y, this.progressView.getMeasuredWidth() + x, this.progressView.getMeasuredHeight() + y);
        this.controlsView.imageReceiver.setImageCoords(0, 0, getMeasuredWidth(), getMeasuredHeight() - AndroidUtilities.dp(5.0f));
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        this.aspectRatioFrameLayout.measure(MeasureSpec.makeMeasureSpec(width, NUM), MeasureSpec.makeMeasureSpec(height - AndroidUtilities.dp(5.0f), NUM));
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
        } else {
            this.fullscreenButton.setImageResource(R.drawable.ic_gofullscreen);
        }
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
            ImageView imageView = this.inlineButton;
            int i = (!this.videoPlayer.isPlayerPrepared() || this.inFullscreen) ? 8 : 0;
            imageView.setVisibility(i);
            if (this.isInline) {
                this.inlineButton.setLayoutParams(LayoutHelper.createFrame(40, 40, 51));
            } else {
                this.inlineButton.setLayoutParams(LayoutHelper.createFrame(56, 50, 51));
            }
        }
    }

    public void setDelegate(WebPlayerViewDelegate webPlayerViewDelegate) {
        this.delegate = webPlayerViewDelegate;
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
            this.changingTextureView = true;
            if (this.inFullscreen) {
                removeView(this.controlsView);
            } else {
                ViewGroup parent = (ViewGroup) this.controlsView.getParent();
                if (parent != this) {
                    if (parent != null) {
                        parent.removeView(this.controlsView);
                    }
                    addView(this.controlsView, 1);
                }
            }
            if (!this.inFullscreen) {
                this.aspectRatioFrameLayout.addView(this.textureView);
            }
            this.changedTextureView = this.delegate.onSwitchToFullscreen(this.controlsView, this.inFullscreen, this.aspectRatioFrameLayout.getAspectRatio(), this.aspectRatioFrameLayout.getVideoRotation(), byButton);
            if (this.inFullscreen) {
                this.aspectRatioFrameLayout.removeView(this.textureView);
            }
            this.controlsView.checkNeedHide();
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
        return this.isInline;
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

    public boolean loadVideo(String url, Photo thumb, boolean autoplay) {
        String youtubeId = null;
        String vimeoId = null;
        if (url != null) {
            Matcher matcher;
            String id;
            try {
                matcher = youtubeIdRegex.matcher(url);
                id = null;
                if (matcher.find()) {
                    id = matcher.group(1);
                }
                if (id != null) {
                    youtubeId = id;
                }
            } catch (Throwable e) {
                FileLog.e("tmessages", e);
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
                } catch (Throwable e2) {
                    FileLog.e("tmessages", e2);
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
        this.currentAlpha = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
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
        if (youtubeId != null) {
            YoutubeVideoTask task = new YoutubeVideoTask(youtubeId);
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
            this.currentTask = task;
        } else if (vimeoId != null) {
            VimeoVideoTask task2 = new VimeoVideoTask(vimeoId);
            task2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
            this.currentTask = task2;
        } else if (null != null) {
            CoubVideoTask task3 = new CoubVideoTask(null);
            task3.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
            this.currentTask = task3;
        }
        this.isLoading = true;
        this.controlsView.show(false, false);
        if (this.progressAnimation != null) {
            this.progressAnimation.cancel();
            this.progressAnimation = null;
        }
        this.controlsView.setProgress(0);
        showProgress(true, false);
        if (youtubeId == null && vimeoId == null && null == null) {
            return false;
        }
        return true;
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
        float f = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
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
            this.progressAnimation.addListener(new AnimatorListenerAdapterProxy() {
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
