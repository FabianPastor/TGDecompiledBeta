package org.telegram.ui.Components.youtube;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimatorListenerAdapterProxy;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.DefaultLoadControl;
import org.telegram.messenger.exoplayer2.ui.AspectRatioFrameLayout;
import org.telegram.messenger.volley.DefaultRetryPolicy;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.ui.Components.ContextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.VideoPlayer;
import org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate;

public class YouTubeView2 extends ViewGroup implements VideoPlayerDelegate {
    private static Pattern stsPattern = Pattern.compile("\"sts\"\\s*:\\s*(\\d+)");
    private AspectRatioFrameLayout aspectRatioFrameLayout;
    private ControlsView controlsView;
    private YouTubeViewDelegate delegate;
    private ImageView fullscreenButton;
    private boolean inFullscreen;
    private boolean initFailed;
    private boolean initied;
    private ImageView inlineButton;
    private boolean isAutoplay;
    private boolean isCompleted;
    private boolean isInline;
    private boolean isLoading;
    private int lastProgress = -1;
    private ImageView nextButton;
    private ImageView playButton;
    private ImageView prevButton;
    private AnimatorSet progressAnimation;
    private Runnable progressRunnable = new Runnable() {
        public void run() {
            if (YouTubeView2.this.videoPlayer != null && YouTubeView2.this.videoPlayer.isPlaying()) {
                YouTubeView2.this.controlsView.setProgress((int) (YouTubeView2.this.videoPlayer.getCurrentPosition() / 1000));
                YouTubeView2.this.controlsView.setBufferedProgress(YouTubeView2.this.videoPlayer.getBufferedPosition(), YouTubeView2.this.videoPlayer.getBufferedPercentage());
                AndroidUtilities.runOnUIThread(YouTubeView2.this.progressRunnable, 1000);
            }
        }
    };
    private ContextProgressView progressView;
    private ImageView shareButton;
    private TextureView textureView;
    private String videoId;
    private VideoPlayer videoPlayer;

    private class ControlsView extends FrameLayout {
        private int bufferedPercentage;
        private long bufferedPosition;
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
        private boolean isVisible = true;
        private int lastProgressX;
        private int progress;
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
            this.progressInnerPaint = new Paint(1);
            this.progressInnerPaint.setColor(-6975081);
        }

        public void setDuration(int value) {
            if (this.duration != value) {
                this.duration = value;
                this.durationLayout = new StaticLayout(String.format(Locale.US, "%d:%02d", new Object[]{Integer.valueOf(this.duration / 60), Integer.valueOf(this.duration % 60)}), this.textPaint, AndroidUtilities.dp(1000.0f), Alignment.ALIGN_NORMAL, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false);
                if (this.durationLayout.getLineCount() > 0) {
                    this.durationWidth = (int) Math.ceil((double) this.durationLayout.getLineWidth(0));
                }
                invalidate();
            }
        }

        public void setBufferedProgress(long position, int percentage) {
            this.bufferedPosition = position;
            this.bufferedPercentage = percentage;
        }

        public void setProgress(int value) {
            if (!this.progressPressed) {
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
            if (this.isVisible && YouTubeView2.this.videoPlayer.isPlaying()) {
                AndroidUtilities.runOnUIThread(this.hideRunnable, 3000);
            }
        }

        public boolean onInterceptTouchEvent(MotionEvent ev) {
            if (ev.getAction() != 0 || this.isVisible) {
                return super.onInterceptTouchEvent(ev);
            }
            onTouchEvent(ev);
            return true;
        }

        public boolean onTouchEvent(MotionEvent event) {
            int progressLineX;
            int progressLineEndX;
            int progressY;
            if (YouTubeView2.this.inFullscreen) {
                progressLineX = AndroidUtilities.dp(36.0f) + this.durationWidth;
                progressLineEndX = (getMeasuredWidth() - AndroidUtilities.dp(76.0f)) - this.durationWidth;
                progressY = getMeasuredHeight() - AndroidUtilities.dp(28.0f);
            } else {
                progressLineX = 0;
                progressLineEndX = getMeasuredWidth();
                progressY = getMeasuredHeight() - AndroidUtilities.dp(7.0f);
            }
            int progressX = progressLineX + ((int) (((float) (progressLineEndX - progressLineX)) * (((float) this.progress) / ((float) this.duration))));
            int x;
            if (event.getAction() == 0) {
                if (this.isVisible) {
                    x = (int) event.getX();
                    int y = (int) event.getY();
                    if (x >= progressX - AndroidUtilities.dp(10.0f) && x <= AndroidUtilities.dp(10.0f) + progressX && y >= progressY - AndroidUtilities.dp(10.0f) && y <= AndroidUtilities.dp(10.0f) + progressY) {
                        this.progressPressed = true;
                        this.lastProgressX = x;
                        this.currentProgressX = progressX;
                        getParent().requestDisallowInterceptTouchEvent(true);
                        invalidate();
                    }
                } else {
                    show(true, true);
                }
                AndroidUtilities.cancelRunOnUIThread(this.hideRunnable);
            } else if (event.getAction() == 1) {
                if (YouTubeView2.this.initied && YouTubeView2.this.videoPlayer.isPlaying()) {
                    AndroidUtilities.runOnUIThread(this.hideRunnable, 3000);
                }
                if (this.progressPressed) {
                    this.progressPressed = false;
                    if (YouTubeView2.this.initied) {
                        YouTubeView2.this.videoPlayer.seekTo((long) ((int) (((float) (this.duration * 1000)) * (((float) this.currentProgressX) / ((float) getMeasuredWidth())))));
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
            int progressLineY;
            int progressLineX;
            int progressLineEndX;
            int cy;
            int progressX;
            int width = getMeasuredWidth();
            int height = getMeasuredHeight();
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
            if (YouTubeView2.this.inFullscreen) {
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
            canvas.drawRect((float) progressLineX, (float) progressLineY, (float) progressLineEndX, (float) (AndroidUtilities.dp(3.0f) + progressLineY), this.progressInnerPaint);
            if (this.progressPressed) {
                progressX = this.currentProgressX;
            } else {
                progressX = progressLineX + ((int) (((float) (progressLineEndX - progressLineX)) * (((float) this.progress) / ((float) this.duration))));
            }
            canvas.drawRect((float) progressLineX, (float) progressLineY, (float) progressX, (float) (AndroidUtilities.dp(3.0f) + progressLineY), this.progressPaint);
            canvas.drawCircle((float) progressX, (float) cy, (float) AndroidUtilities.dp(this.progressPressed ? 7.0f : 5.0f), this.progressPaint);
        }
    }

    public interface YouTubeViewDelegate {
        void onInitFailed();

        void onSharePressed();

        void onSwithToFullscreen(boolean z);

        void onSwtichToInline(boolean z);
    }

    private class YoutubeVideoTask extends AsyncTask<Void, Void, String> {
        private boolean canRetry = true;
        private String videoId;

        public YoutubeVideoTask(String vid) {
            this.videoId = vid;
        }

        private String downloadUrlContent(String url) {
            Throwable e;
            InputStream httpConnectionStream = null;
            boolean done = false;
            StringBuilder result = null;
            URLConnection httpConnection = null;
            try {
                httpConnection = new URL(url).openConnection();
                httpConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 4.4; Nexus 5 Build/_BuildID_) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/30.0.0.0 Mobile Safari/537.36");
                httpConnection.addRequestProperty("Referer", "google.com");
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
                        httpConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 4.4; Nexus 5 Build/_BuildID_) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/30.0.0.0 Mobile Safari/537.36");
                        httpConnection.addRequestProperty("Referer", "google.com");
                    }
                }
                httpConnection.connect();
                httpConnectionStream = httpConnection.getInputStream();
            } catch (Throwable e2) {
                if (e2 instanceof SocketTimeoutException) {
                    if (ConnectionsManager.isNetworkOnline()) {
                        this.canRetry = false;
                    }
                } else if (e2 instanceof UnknownHostException) {
                    this.canRetry = false;
                } else if (e2 instanceof SocketException) {
                    if (e2.getMessage() != null && e2.getMessage().contains("ECONNRESET")) {
                        this.canRetry = false;
                    }
                } else if (e2 instanceof FileNotFoundException) {
                    this.canRetry = false;
                }
                FileLog.e("tmessages", e2);
            }
            if (this.canRetry) {
                if (httpConnection != null) {
                    try {
                        if (httpConnection instanceof HttpURLConnection) {
                            int code = ((HttpURLConnection) httpConnection).getResponseCode();
                            if (!(code == 200 || code == 202 || code == 304)) {
                                this.canRetry = false;
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
                        while (!isCancelled()) {
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

        protected String doInBackground(Void... voids) {
            String embed = downloadUrlContent("https://www.youtube.com/embed/" + this.videoId);
            String params = "video_id=" + this.videoId;
            try {
                params = params + "&eurl=" + URLEncoder.encode("https://youtube.googleapis.com/v/" + this.videoId, "UTF-8");
            } catch (Throwable e) {
                FileLog.e("tmessages", e);
            }
            if (embed != null) {
                Matcher matcher = YouTubeView2.stsPattern.matcher(embed);
                if (matcher.find()) {
                    params = params + "&sts=" + embed.substring(matcher.start() + 6, matcher.end());
                } else {
                    params = params + "&sts=";
                }
            }
            String str = null;
            boolean error = false;
            String videoInfo = downloadUrlContent("https://www.youtube.com/get_video_info?&" + params);
            if (videoInfo != null) {
                String[] args = videoInfo.split("&");
                for (int a = 0; a < args.length; a++) {
                    String[] args2;
                    if (args[a].startsWith("dashmpd")) {
                        args2 = args[a].split("=");
                        if (args2.length == 2) {
                            try {
                                str = URLDecoder.decode(args2[1], "UTF-8");
                            } catch (Throwable e2) {
                                FileLog.e("tmessages", e2);
                            }
                        }
                    } else if (args[a].startsWith("use_cipher_signature")) {
                        args2 = args[a].split("=");
                        if (args2.length == 2 && args2[1].toLowerCase().equals("true")) {
                            error = true;
                        }
                    }
                }
            }
            return error ? null : str;
        }

        protected void onPostExecute(String result) {
            if (result != null) {
                YouTubeView2.this.initied = true;
                YouTubeView2.this.videoPlayer.preparePlayer(Uri.parse(result));
                YouTubeView2.this.videoPlayer.setPlayWhenReady(YouTubeView2.this.isAutoplay);
                YouTubeView2.this.updateShareButton();
                YouTubeView2.this.isLoading = false;
                YouTubeView2.this.showProgress(false, true);
                YouTubeView2.this.controlsView.show(true, true);
                if (YouTubeView2.this.videoPlayer.getDuration() != C.TIME_UNSET) {
                    YouTubeView2.this.controlsView.setDuration((int) (YouTubeView2.this.videoPlayer.getDuration() / 1000));
                    return;
                } else {
                    YouTubeView2.this.controlsView.setDuration(0);
                    return;
                }
            }
            YouTubeView2.this.delegate.onInitFailed();
        }
    }

    public YouTubeView2(Context context, boolean allowInline) {
        super(context);
        setBackgroundColor(-16777216);
        this.aspectRatioFrameLayout = new AspectRatioFrameLayout(context);
        addView(this.aspectRatioFrameLayout, LayoutHelper.createFrame(-1, -1, 17));
        this.textureView = new TextureView(context);
        this.aspectRatioFrameLayout.addView(this.textureView, LayoutHelper.createFrame(-1, -1, 17));
        this.videoPlayer = new VideoPlayer();
        this.videoPlayer.setDelegate(this);
        this.videoPlayer.setTextureView(this.textureView);
        this.controlsView = new ControlsView(context);
        this.controlsView.setWillNotDraw(false);
        addView(this.controlsView, LayoutHelper.createFrame(-1, -1.0f));
        this.progressView = new ContextProgressView(context, 1);
        addView(this.progressView, LayoutHelper.createFrame(48, 48, 17));
        this.fullscreenButton = new ImageView(context);
        this.fullscreenButton.setScaleType(ScaleType.CENTER);
        this.controlsView.addView(this.fullscreenButton, LayoutHelper.createFrame(56, 56, 85));
        this.fullscreenButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (YouTubeView2.this.initied) {
                    YouTubeView2.this.inFullscreen = !YouTubeView2.this.inFullscreen;
                    YouTubeView2.this.updateFullscreenButton();
                    if (YouTubeView2.this.delegate != null) {
                        YouTubeView2.this.delegate.onSwithToFullscreen(YouTubeView2.this.inFullscreen);
                    }
                }
            }
        });
        this.playButton = new ImageView(context);
        this.playButton.setScaleType(ScaleType.CENTER);
        this.controlsView.addView(this.playButton, LayoutHelper.createFrame(48, 48, 17));
        this.playButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (YouTubeView2.this.initied) {
                    if (YouTubeView2.this.videoPlayer.isPlaying()) {
                        YouTubeView2.this.videoPlayer.pause();
                    } else {
                        YouTubeView2.this.videoPlayer.play();
                    }
                    YouTubeView2.this.updatePlayButton();
                }
            }
        });
        this.prevButton = new ImageView(context);
        this.prevButton.setScaleType(ScaleType.CENTER);
        this.prevButton.setImageResource(R.drawable.ic_previous);
        this.controlsView.addView(this.prevButton, LayoutHelper.createFrame(48, 48, 17));
        this.prevButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            }
        });
        this.nextButton = new ImageView(context);
        this.nextButton.setScaleType(ScaleType.CENTER);
        this.nextButton.setImageResource(R.drawable.ic_next);
        this.controlsView.addView(this.nextButton, LayoutHelper.createFrame(48, 48, 17));
        this.nextButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            }
        });
        if (allowInline) {
            this.inlineButton = new ImageView(context);
            this.inlineButton.setScaleType(ScaleType.CENTER);
            this.controlsView.addView(this.inlineButton, LayoutHelper.createFrame(56, 48, 51));
            this.inlineButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    YouTubeView2.this.isInline = !YouTubeView2.this.isInline;
                    YouTubeView2.this.updatePlayButton();
                    YouTubeView2.this.updateShareButton();
                    YouTubeView2.this.updateFullscreenButton();
                    YouTubeView2.this.updateInlineButton();
                    if (YouTubeView2.this.delegate != null) {
                        YouTubeView2.this.delegate.onSwtichToInline(YouTubeView2.this.isInline);
                    }
                }
            });
        }
        this.shareButton = new ImageView(context);
        this.shareButton.setScaleType(ScaleType.CENTER);
        this.shareButton.setImageResource(R.drawable.ic_share_video);
        this.controlsView.addView(this.shareButton, LayoutHelper.createFrame(56, 48, 53));
        this.shareButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (YouTubeView2.this.delegate != null) {
                    YouTubeView2.this.delegate.onSharePressed();
                }
            }
        });
        updatePlayButton();
        updateFullscreenButton();
        updateMoveButtons();
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
        if (this.videoPlayer.isPlaying() && playbackState != 4) {
            updatePlayButton();
        } else if (playbackState == 4) {
            this.isCompleted = true;
            updatePlayButton();
            this.controlsView.show(true, true);
        }
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
            this.aspectRatioFrameLayout.setAspectRatio(height == 0 ? DefaultRetryPolicy.DEFAULT_BACKOFF_MULT : (((float) width) * pixelWidthHeightRatio) / ((float) height), unappliedRotationDegrees);
        }
    }

    public void onRenderedFirstFrame() {
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int x = ((r - l) - this.aspectRatioFrameLayout.getMeasuredWidth()) / 2;
        int y = ((b - t) - this.aspectRatioFrameLayout.getMeasuredHeight()) / 2;
        this.aspectRatioFrameLayout.layout(x, y, this.aspectRatioFrameLayout.getMeasuredWidth() + x, this.aspectRatioFrameLayout.getMeasuredHeight() + y);
        this.controlsView.layout(0, 0, this.controlsView.getMeasuredWidth(), this.controlsView.getMeasuredHeight());
        x = ((r - l) - this.progressView.getMeasuredWidth()) / 2;
        y = ((b - t) - this.progressView.getMeasuredHeight()) / 2;
        this.progressView.layout(x, y, this.progressView.getMeasuredWidth() + x, this.progressView.getMeasuredHeight() + y);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        this.aspectRatioFrameLayout.measure(MeasureSpec.makeMeasureSpec(width, NUM), MeasureSpec.makeMeasureSpec(height - AndroidUtilities.dp(5.0f), NUM));
        this.controlsView.measure(MeasureSpec.makeMeasureSpec(width, NUM), MeasureSpec.makeMeasureSpec(height, NUM));
        this.progressView.measure(MeasureSpec.makeMeasureSpec(width, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(height, Integer.MIN_VALUE));
        setMeasuredDimension(width, height);
    }

    public View getChildAt(int index) {
        if (index == 1) {
            return null;
        }
        return super.getChildAt(index);
    }

    public int getChildCount() {
        return 1;
    }

    private void updatePlayButton() {
        this.controlsView.checkNeedHide();
        AndroidUtilities.cancelRunOnUIThread(this.progressRunnable);
        if (this.videoPlayer.isPlaying()) {
            this.playButton.setImageResource(this.isInline ? R.drawable.ic_pauseinline : R.drawable.ic_pause);
            AndroidUtilities.runOnUIThread(this.progressRunnable);
        } else if (this.isCompleted) {
            this.playButton.setImageResource(this.isInline ? R.drawable.ic_againinline : R.drawable.ic_again);
        } else {
            this.playButton.setImageResource(this.isInline ? R.drawable.ic_playinline : R.drawable.ic_play);
        }
    }

    private void updateFullscreenButton() {
        if (this.inFullscreen) {
            this.fullscreenButton.setImageResource(R.drawable.ic_outfullscreen);
        } else {
            this.fullscreenButton.setImageResource(R.drawable.ic_gofullscreen);
        }
    }

    private void updateMoveButtons() {
        this.prevButton.setVisibility(8);
        this.nextButton.setVisibility(8);
    }

    private void updateShareButton() {
        this.shareButton.setVisibility(this.isInline ? 8 : 0);
    }

    private void updateInlineButton() {
        if (this.inlineButton != null) {
            this.inlineButton.setImageResource(this.isInline ? R.drawable.ic_goinline : R.drawable.ic_outinline);
        }
    }

    public void setDelegate(YouTubeViewDelegate youTubeViewDelegate) {
        this.delegate = youTubeViewDelegate;
    }

    public void loadVideo(String id, boolean autoplay) {
        this.initied = false;
        this.lastProgress = -1;
        this.videoId = id;
        this.isAutoplay = autoplay;
        this.isAutoplay = true;
        if (id != null) {
            new YoutubeVideoTask(id).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
        }
        this.isLoading = true;
        this.controlsView.show(false, false);
        if (this.progressAnimation != null) {
            this.progressAnimation.cancel();
            this.progressAnimation = null;
        }
        showProgress(true, false);
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
            ContextProgressView contextProgressView = this.progressView;
            String str = "alpha";
            float[] fArr = new float[1];
            if (!show) {
                f = 0.0f;
            }
            fArr[0] = f;
            animatorArr[0] = ObjectAnimator.ofFloat(contextProgressView, str, fArr);
            animatorSet.playTogether(animatorArr);
            this.progressAnimation.setDuration(150);
            this.progressAnimation.addListener(new AnimatorListenerAdapterProxy() {
                public void onAnimationEnd(Animator animator) {
                    YouTubeView2.this.progressAnimation = null;
                }
            });
            this.progressAnimation.start();
            return;
        }
        ContextProgressView contextProgressView2 = this.progressView;
        if (!show) {
            f = 0.0f;
        }
        contextProgressView2.setAlpha(f);
    }
}
