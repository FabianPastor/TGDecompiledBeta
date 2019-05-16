package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.Bitmaps;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate;

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

    private class AparatVideoTask extends AsyncTask<Void, Void, String> {
        private boolean canRetry = true;
        private String[] results = new String[2];
        private String videoId;

        public AparatVideoTask(String str) {
            this.videoId = str;
        }

        /* Access modifiers changed, original: protected|varargs */
        public String doInBackground(Void... voidArr) {
            String str = "file";
            String downloadUrlContent = WebPlayerView.this.downloadUrlContent(this, String.format(Locale.US, "http://www.aparat.com/video/video/embed/vt/frame/showvideo/yes/videohash/%s", new Object[]{this.videoId}));
            String str2 = null;
            if (isCancelled()) {
                return null;
            }
            try {
                Matcher matcher = WebPlayerView.aparatFileListPattern.matcher(downloadUrlContent);
                if (matcher.find()) {
                    JSONArray jSONArray = new JSONArray(matcher.group(1));
                    for (int i = 0; i < jSONArray.length(); i++) {
                        JSONArray jSONArray2 = jSONArray.getJSONArray(i);
                        if (jSONArray2.length() != 0) {
                            JSONObject jSONObject = jSONArray2.getJSONObject(0);
                            if (jSONObject.has(str)) {
                                this.results[0] = jSONObject.getString(str);
                                this.results[1] = "other";
                            }
                        }
                    }
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
            if (!isCancelled()) {
                str2 = this.results[0];
            }
            return str2;
        }

        /* Access modifiers changed, original: protected */
        public void onPostExecute(String str) {
            if (str != null) {
                WebPlayerView.this.initied = true;
                WebPlayerView.this.playVideoUrl = str;
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
        private Runnable hideRunnable = new -$$Lambda$WebPlayerView$ControlsView$QYTgg3cx1r3S4djGCF7dtRzr3Os(this);
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

        public /* synthetic */ void lambda$new$0$WebPlayerView$ControlsView() {
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

        public void setDuration(int i) {
            if (this.duration != i && i >= 0 && !WebPlayerView.this.isStream) {
                this.duration = i;
                this.durationLayout = new StaticLayout(String.format(Locale.US, "%d:%02d", new Object[]{Integer.valueOf(this.duration / 60), Integer.valueOf(this.duration % 60)}), this.textPaint, AndroidUtilities.dp(1000.0f), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                if (this.durationLayout.getLineCount() > 0) {
                    this.durationWidth = (int) Math.ceil((double) this.durationLayout.getLineWidth(0));
                }
                invalidate();
            }
        }

        public void setBufferedProgress(int i) {
            this.bufferedPosition = i;
            invalidate();
        }

        public void setProgress(int i) {
            if (!this.progressPressed && i >= 0 && !WebPlayerView.this.isStream) {
                this.progress = i;
                this.progressLayout = new StaticLayout(String.format(Locale.US, "%d:%02d", new Object[]{Integer.valueOf(this.progress / 60), Integer.valueOf(this.progress % 60)}), this.textPaint, AndroidUtilities.dp(1000.0f), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                invalidate();
            }
        }

        public void show(boolean z, boolean z2) {
            if (this.isVisible != z) {
                this.isVisible = z;
                AnimatorSet animatorSet = this.currentAnimation;
                if (animatorSet != null) {
                    animatorSet.cancel();
                }
                String str = "alpha";
                AnimatorSet animatorSet2;
                Animator[] animatorArr;
                if (this.isVisible) {
                    if (z2) {
                        this.currentAnimation = new AnimatorSet();
                        animatorSet2 = this.currentAnimation;
                        animatorArr = new Animator[1];
                        animatorArr[0] = ObjectAnimator.ofFloat(this, str, new float[]{1.0f});
                        animatorSet2.playTogether(animatorArr);
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
                } else if (z2) {
                    this.currentAnimation = new AnimatorSet();
                    animatorSet2 = this.currentAnimation;
                    animatorArr = new Animator[1];
                    animatorArr[0] = ObjectAnimator.ofFloat(this, str, new float[]{0.0f});
                    animatorSet2.playTogether(animatorArr);
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
            int i = this.duration;
            i = (i != 0 ? (int) (((float) (measuredWidth - dp)) * (((float) this.progress) / ((float) i))) : 0) + dp;
            int y;
            if (motionEvent.getAction() == 0) {
                if (!this.isVisible || WebPlayerView.this.isInline || WebPlayerView.this.isStream) {
                    show(true, true);
                } else if (this.duration != 0) {
                    dp = (int) motionEvent.getX();
                    y = (int) motionEvent.getY();
                    if (dp >= i - AndroidUtilities.dp(10.0f) && dp <= AndroidUtilities.dp(10.0f) + i && y >= measuredHeight - AndroidUtilities.dp(10.0f) && y <= measuredHeight + AndroidUtilities.dp(10.0f)) {
                        this.progressPressed = true;
                        this.lastProgressX = dp;
                        this.currentProgressX = i;
                        getParent().requestDisallowInterceptTouchEvent(true);
                        invalidate();
                    }
                }
                AndroidUtilities.cancelRunOnUIThread(this.hideRunnable);
            } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
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
            } else if (motionEvent.getAction() == 2 && this.progressPressed) {
                y = (int) motionEvent.getX();
                this.currentProgressX -= this.lastProgressX - y;
                this.lastProgressX = y;
                y = this.currentProgressX;
                if (y < dp) {
                    this.currentProgressX = dp;
                } else if (y > measuredWidth) {
                    this.currentProgressX = measuredWidth;
                }
                setProgress((int) (((float) (this.duration * 1000)) * (((float) (this.currentProgressX - dp)) / ((float) (measuredWidth - dp)))));
                invalidate();
            }
            super.onTouchEvent(motionEvent);
            return true;
        }

        /* Access modifiers changed, original: protected */
        /* JADX WARNING: Removed duplicated region for block: B:42:0x014f  */
        /* JADX WARNING: Removed duplicated region for block: B:46:0x0166  */
        /* JADX WARNING: Removed duplicated region for block: B:45:0x0163  */
        /* JADX WARNING: Removed duplicated region for block: B:49:0x0179  */
        /* JADX WARNING: Removed duplicated region for block: B:66:? A:{SYNTHETIC, RETURN} */
        /* JADX WARNING: Removed duplicated region for block: B:58:0x01be  */
        public void onDraw(android.graphics.Canvas r16) {
            /*
            r15 = this;
            r0 = r15;
            r7 = r16;
            r1 = org.telegram.ui.Components.WebPlayerView.this;
            r1 = r1.drawImage;
            if (r1 == 0) goto L_0x005f;
        L_0x000b:
            r1 = org.telegram.ui.Components.WebPlayerView.this;
            r1 = r1.firstFrameRendered;
            if (r1 == 0) goto L_0x004f;
        L_0x0013:
            r1 = org.telegram.ui.Components.WebPlayerView.this;
            r1 = r1.currentAlpha;
            r2 = 0;
            r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1));
            if (r1 == 0) goto L_0x004f;
        L_0x001e:
            r3 = java.lang.System.currentTimeMillis();
            r1 = org.telegram.ui.Components.WebPlayerView.this;
            r5 = r1.lastUpdateTime;
            r5 = r3 - r5;
            r1 = org.telegram.ui.Components.WebPlayerView.this;
            r1.lastUpdateTime = r3;
            r1 = org.telegram.ui.Components.WebPlayerView.this;
            r3 = r1.currentAlpha;
            r4 = (float) r5;
            r5 = NUM; // 0x43160000 float:150.0 double:5.56078426E-315;
            r4 = r4 / r5;
            r3 = r3 - r4;
            r1.currentAlpha = r3;
            r1 = org.telegram.ui.Components.WebPlayerView.this;
            r1 = r1.currentAlpha;
            r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1));
            if (r1 >= 0) goto L_0x004c;
        L_0x0047:
            r1 = org.telegram.ui.Components.WebPlayerView.this;
            r1.currentAlpha = r2;
        L_0x004c:
            r15.invalidate();
        L_0x004f:
            r1 = r0.imageReceiver;
            r2 = org.telegram.ui.Components.WebPlayerView.this;
            r2 = r2.currentAlpha;
            r1.setAlpha(r2);
            r1 = r0.imageReceiver;
            r1.draw(r7);
        L_0x005f:
            r1 = org.telegram.ui.Components.WebPlayerView.this;
            r1 = r1.videoPlayer;
            r1 = r1.isPlayerPrepared();
            if (r1 == 0) goto L_0x01d0;
        L_0x006b:
            r1 = org.telegram.ui.Components.WebPlayerView.this;
            r1 = r1.isStream;
            if (r1 != 0) goto L_0x01d0;
        L_0x0073:
            r1 = r15.getMeasuredWidth();
            r2 = r15.getMeasuredHeight();
            r3 = org.telegram.ui.Components.WebPlayerView.this;
            r3 = r3.isInline;
            if (r3 != 0) goto L_0x00e8;
        L_0x0083:
            r3 = r0.durationLayout;
            r4 = 6;
            r5 = 10;
            if (r3 == 0) goto L_0x00ba;
        L_0x008a:
            r16.save();
            r3 = NUM; // 0x42680000 float:58.0 double:5.50444465E-315;
            r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
            r3 = r1 - r3;
            r6 = r0.durationWidth;
            r3 = r3 - r6;
            r3 = (float) r3;
            r6 = org.telegram.ui.Components.WebPlayerView.this;
            r6 = r6.inFullscreen;
            if (r6 == 0) goto L_0x00a3;
        L_0x00a1:
            r6 = 6;
            goto L_0x00a5;
        L_0x00a3:
            r6 = 10;
        L_0x00a5:
            r6 = r6 + 29;
            r6 = (float) r6;
            r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
            r6 = r2 - r6;
            r6 = (float) r6;
            r7.translate(r3, r6);
            r3 = r0.durationLayout;
            r3.draw(r7);
            r16.restore();
        L_0x00ba:
            r3 = r0.progressLayout;
            if (r3 == 0) goto L_0x00e8;
        L_0x00be:
            r16.save();
            r3 = NUM; // 0x41900000 float:18.0 double:5.43450582E-315;
            r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
            r3 = (float) r3;
            r6 = org.telegram.ui.Components.WebPlayerView.this;
            r6 = r6.inFullscreen;
            if (r6 == 0) goto L_0x00d1;
        L_0x00d0:
            goto L_0x00d3;
        L_0x00d1:
            r4 = 10;
        L_0x00d3:
            r4 = r4 + 29;
            r4 = (float) r4;
            r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
            r4 = r2 - r4;
            r4 = (float) r4;
            r7.translate(r3, r4);
            r3 = r0.progressLayout;
            r3.draw(r7);
            r16.restore();
        L_0x00e8:
            r3 = r0.duration;
            if (r3 == 0) goto L_0x01d0;
        L_0x00ec:
            r3 = org.telegram.ui.Components.WebPlayerView.this;
            r3 = r3.isInline;
            r8 = NUM; // 0x40e00000 float:7.0 double:5.37751863E-315;
            r4 = 0;
            r9 = NUM; // 0x40400000 float:3.0 double:5.325712093E-315;
            if (r3 == 0) goto L_0x0109;
        L_0x00f9:
            r3 = org.telegram.messenger.AndroidUtilities.dp(r9);
            r3 = r2 - r3;
            r5 = org.telegram.messenger.AndroidUtilities.dp(r8);
        L_0x0103:
            r2 = r2 - r5;
            r12 = r1;
            r13 = r2;
            r11 = r3;
            r10 = 0;
            goto L_0x0147;
        L_0x0109:
            r3 = org.telegram.ui.Components.WebPlayerView.this;
            r3 = r3.inFullscreen;
            if (r3 == 0) goto L_0x0138;
        L_0x0111:
            r3 = NUM; // 0x41e80000 float:29.0 double:5.46299942E-315;
            r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
            r3 = r2 - r3;
            r4 = NUM; // 0x42100000 float:36.0 double:5.47595105E-315;
            r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
            r5 = r0.durationWidth;
            r4 = r4 + r5;
            r5 = NUM; // 0x42980000 float:76.0 double:5.51998661E-315;
            r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
            r1 = r1 - r5;
            r5 = r0.durationWidth;
            r1 = r1 - r5;
            r5 = NUM; // 0x41e00000 float:28.0 double:5.46040909E-315;
            r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
            r2 = r2 - r5;
            r12 = r1;
            r13 = r2;
            r11 = r3;
            r10 = r4;
            goto L_0x0147;
        L_0x0138:
            r3 = NUM; // 0x41500000 float:13.0 double:5.413783207E-315;
            r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
            r3 = r2 - r3;
            r5 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
            r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
            goto L_0x0103;
        L_0x0147:
            r1 = org.telegram.ui.Components.WebPlayerView.this;
            r1 = r1.inFullscreen;
            if (r1 == 0) goto L_0x015f;
        L_0x014f:
            r2 = (float) r10;
            r3 = (float) r11;
            r4 = (float) r12;
            r1 = org.telegram.messenger.AndroidUtilities.dp(r9);
            r1 = r1 + r11;
            r5 = (float) r1;
            r6 = r0.progressInnerPaint;
            r1 = r16;
            r1.drawRect(r2, r3, r4, r5, r6);
        L_0x015f:
            r1 = r0.progressPressed;
            if (r1 == 0) goto L_0x0166;
        L_0x0163:
            r1 = r0.currentProgressX;
            goto L_0x0174;
        L_0x0166:
            r1 = r12 - r10;
            r1 = (float) r1;
            r2 = r0.progress;
            r2 = (float) r2;
            r3 = r0.duration;
            r3 = (float) r3;
            r2 = r2 / r3;
            r1 = r1 * r2;
            r1 = (int) r1;
            r1 = r1 + r10;
        L_0x0174:
            r14 = r1;
            r1 = r0.bufferedPosition;
            if (r1 == 0) goto L_0x01a5;
        L_0x0179:
            r2 = r0.duration;
            if (r2 == 0) goto L_0x01a5;
        L_0x017d:
            r3 = (float) r10;
            r4 = (float) r11;
            r12 = r12 - r10;
            r5 = (float) r12;
            r1 = (float) r1;
            r2 = (float) r2;
            r1 = r1 / r2;
            r5 = r5 * r1;
            r5 = r5 + r3;
            r1 = org.telegram.messenger.AndroidUtilities.dp(r9);
            r1 = r1 + r11;
            r6 = (float) r1;
            r1 = org.telegram.ui.Components.WebPlayerView.this;
            r1 = r1.inFullscreen;
            if (r1 == 0) goto L_0x0198;
        L_0x0195:
            r1 = r0.progressBufferedPaint;
            goto L_0x019a;
        L_0x0198:
            r1 = r0.progressInnerPaint;
        L_0x019a:
            r12 = r1;
            r1 = r16;
            r2 = r3;
            r3 = r4;
            r4 = r5;
            r5 = r6;
            r6 = r12;
            r1.drawRect(r2, r3, r4, r5, r6);
        L_0x01a5:
            r2 = (float) r10;
            r3 = (float) r11;
            r10 = (float) r14;
            r1 = org.telegram.messenger.AndroidUtilities.dp(r9);
            r11 = r11 + r1;
            r5 = (float) r11;
            r6 = r0.progressPaint;
            r1 = r16;
            r4 = r10;
            r1.drawRect(r2, r3, r4, r5, r6);
            r1 = org.telegram.ui.Components.WebPlayerView.this;
            r1 = r1.isInline;
            if (r1 != 0) goto L_0x01d0;
        L_0x01be:
            r1 = (float) r13;
            r2 = r0.progressPressed;
            if (r2 == 0) goto L_0x01c4;
        L_0x01c3:
            goto L_0x01c6;
        L_0x01c4:
            r8 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        L_0x01c6:
            r2 = org.telegram.messenger.AndroidUtilities.dp(r8);
            r2 = (float) r2;
            r3 = r0.progressPaint;
            r7.drawCircle(r10, r1, r2, r3);
        L_0x01d0:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.WebPlayerView$ControlsView.onDraw(android.graphics.Canvas):void");
        }
    }

    private class CoubVideoTask extends AsyncTask<Void, Void, String> {
        private boolean canRetry = true;
        private String[] results = new String[4];
        private String videoId;

        public CoubVideoTask(String str) {
            this.videoId = str;
        }

        private String decodeUrl(String str) {
            StringBuilder stringBuilder = new StringBuilder(str);
            for (int i = 0; i < stringBuilder.length(); i++) {
                char charAt = stringBuilder.charAt(i);
                char toLowerCase = Character.toLowerCase(charAt);
                if (charAt == toLowerCase) {
                    toLowerCase = Character.toUpperCase(charAt);
                }
                stringBuilder.setCharAt(i, toLowerCase);
            }
            try {
                return new String(Base64.decode(stringBuilder.toString(), 0), "UTF-8");
            } catch (Exception unused) {
                return null;
            }
        }

        /* Access modifiers changed, original: protected|varargs */
        public String doInBackground(Void... voidArr) {
            String str = "other";
            String downloadUrlContent = WebPlayerView.this.downloadUrlContent(this, String.format(Locale.US, "https://coub.com/api/v2/coubs/%s.json", new Object[]{this.videoId}));
            String str2 = null;
            if (isCancelled()) {
                return null;
            }
            try {
                JSONObject jSONObject = new JSONObject(downloadUrlContent).getJSONObject("file_versions").getJSONObject("mobile");
                String decodeUrl = decodeUrl(jSONObject.getString("gifv"));
                downloadUrlContent = jSONObject.getJSONArray("audio").getString(0);
                if (!(decodeUrl == null || downloadUrlContent == null)) {
                    this.results[0] = decodeUrl;
                    this.results[1] = str;
                    this.results[2] = downloadUrlContent;
                    this.results[3] = str;
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
            if (!isCancelled()) {
                str2 = this.results[0];
            }
            return str2;
        }

        /* Access modifiers changed, original: protected */
        public void onPostExecute(String str) {
            if (str != null) {
                WebPlayerView.this.initied = true;
                WebPlayerView.this.playVideoUrl = str;
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

        /*  JADX ERROR: JadxRuntimeException in pass: BlockProcessor
            jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:96:0x024c in {2, 10, 15, 17, 19, 25, 28, 29, 30, 33, 37, 42, 45, 50, 55, 58, 60, 68, 69, 71, 75, 81, 86, 88, 90, 93, 95} preds:[]
            	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:242)
            	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:52)
            	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:42)
            	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
            	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
            	at java.util.ArrayList.forEach(ArrayList.java:1257)
            	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
            	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$0(DepthTraversal.java:13)
            	at java.util.ArrayList.forEach(ArrayList.java:1257)
            	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:13)
            	at jadx.core.ProcessClass.process(ProcessClass.java:32)
            	at jadx.core.ProcessClass.lambda$processDependencies$0(ProcessClass.java:51)
            	at java.lang.Iterable.forEach(Iterable.java:75)
            	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:51)
            	at jadx.core.ProcessClass.process(ProcessClass.java:37)
            	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:292)
            	at jadx.api.JavaClass.decompile(JavaClass.java:62)
            	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
            */
        private void interpretExpression(java.lang.String r11, java.util.HashMap<java.lang.String, java.lang.String> r12, int r13) throws java.lang.Exception {
            /*
            r10 = this;
            r11 = r11.trim();
            r0 = android.text.TextUtils.isEmpty(r11);
            if (r0 == 0) goto L_0x000b;
            return;
            r0 = 0;
            r1 = r11.charAt(r0);
            r2 = 40;
            r3 = 1;
            if (r1 != r2) goto L_0x0068;
            r1 = org.telegram.ui.Components.WebPlayerView.exprParensPattern;
            r1 = r1.matcher(r11);
            r4 = 0;
            r5 = r1.find();
            if (r5 == 0) goto L_0x0055;
            r5 = r1.group(r0);
            r6 = 48;
            r5 = r5.indexOf(r6);
            if (r5 != r2) goto L_0x0033;
            r4 = r4 + 1;
            goto L_0x001e;
            r4 = r4 + -1;
            if (r4 != 0) goto L_0x001e;
            r2 = r1.start();
            r2 = r11.substring(r3, r2);
            r10.interpretExpression(r2, r12, r13);
            r1 = r1.end();
            r11 = r11.substring(r1);
            r11 = r11.trim();
            r1 = android.text.TextUtils.isEmpty(r11);
            if (r1 == 0) goto L_0x0055;
            return;
            if (r4 != 0) goto L_0x0058;
            goto L_0x0068;
            r12 = new java.lang.Exception;
            r13 = new java.lang.Object[r3];
            r13[r0] = r11;
            r11 = "Premature end of parens in %s";
            r11 = java.lang.String.format(r11, r13);
            r12.<init>(r11);
            throw r12;
            r1 = 0;
            r2 = r10.assign_operators;
            r4 = r2.length;
            r5 = "";
            r6 = 3;
            r7 = "[a-zA-Z_$][a-zA-Z_$0-9]*";
            r8 = 2;
            if (r1 >= r4) goto L_0x00b8;
            r2 = r2[r1];
            r4 = java.util.Locale.US;
            r9 = new java.lang.Object[r8];
            r9[r0] = r7;
            r2 = java.util.regex.Pattern.quote(r2);
            r9[r3] = r2;
            r2 = "(?x)(%s)(?:\\[([^\\]]+?)\\])?\\s*%s(.*)$";
            r2 = java.lang.String.format(r4, r2, r9);
            r2 = java.util.regex.Pattern.compile(r2);
            r2 = r2.matcher(r11);
            r4 = r2.find();
            if (r4 != 0) goto L_0x0099;
            r1 = r1 + 1;
            goto L_0x0069;
            r11 = r2.group(r6);
            r0 = r13 + -1;
            r10.interpretExpression(r11, r12, r0);
            r11 = r2.group(r8);
            r0 = android.text.TextUtils.isEmpty(r11);
            if (r0 != 0) goto L_0x00b0;
            r10.interpretExpression(r11, r12, r13);
            goto L_0x00b7;
            r11 = r2.group(r3);
            r12.put(r11, r5);
            return;
            java.lang.Integer.parseInt(r11);	 Catch:{ Exception -> 0x00bc }
            return;
            r1 = java.util.Locale.US;
            r2 = new java.lang.Object[r3];
            r2[r0] = r7;
            r4 = "(?!if|return|true|false)(%s)$";
            r1 = java.lang.String.format(r1, r4, r2);
            r1 = java.util.regex.Pattern.compile(r1);
            r1 = r1.matcher(r11);
            r1 = r1.find();
            if (r1 == 0) goto L_0x00d7;
            return;
            r1 = r11.charAt(r0);
            r2 = 34;
            if (r1 != r2) goto L_0x00eb;
            r1 = r11.length();
            r1 = r1 - r3;
            r1 = r11.charAt(r1);
            if (r1 != r2) goto L_0x00eb;
            return;
            r1 = new org.json.JSONObject;	 Catch:{ Exception -> 0x00f4 }
            r1.<init>(r11);	 Catch:{ Exception -> 0x00f4 }
            r1.toString();	 Catch:{ Exception -> 0x00f4 }
            return;
            r1 = java.util.Locale.US;
            r2 = new java.lang.Object[r3];
            r2[r0] = r7;
            r4 = "(%s)\\[(.+)\\]$";
            r1 = java.lang.String.format(r1, r4, r2);
            r1 = java.util.regex.Pattern.compile(r1);
            r1 = r1.matcher(r11);
            r2 = r1.find();
            if (r2 == 0) goto L_0x011b;
            r1.group(r3);
            r11 = r1.group(r8);
            r13 = r13 - r3;
            r10.interpretExpression(r11, r12, r13);
            return;
            r1 = java.util.Locale.US;
            r2 = new java.lang.Object[r3];
            r2[r0] = r7;
            r9 = "(%s)(?:\\.([^(]+)|\\[([^]]+)\\])\\s*(?:\\(+([^()]*)\\))?$";
            r1 = java.lang.String.format(r1, r9, r2);
            r1 = java.util.regex.Pattern.compile(r1);
            r1 = r1.matcher(r11);
            r2 = r1.find();
            if (r2 == 0) goto L_0x018b;
            r2 = r1.group(r3);
            r4 = r1.group(r8);
            r6 = r1.group(r6);
            r7 = android.text.TextUtils.isEmpty(r4);
            if (r7 == 0) goto L_0x0148;
            r4 = r6;
            r6 = "\"";
            r4.replace(r6, r5);
            r4 = 4;
            r1 = r1.group(r4);
            r4 = r12.get(r2);
            if (r4 != 0) goto L_0x015b;
            r10.extractObject(r2);
            if (r1 != 0) goto L_0x015e;
            return;
            r2 = r11.length();
            r2 = r2 - r3;
            r11 = r11.charAt(r2);
            r2 = 41;
            if (r11 != r2) goto L_0x0183;
            r11 = r1.length();
            if (r11 == 0) goto L_0x0182;
            r11 = ",";
            r11 = r1.split(r11);
            r1 = r11.length;
            if (r0 >= r1) goto L_0x0182;
            r1 = r11[r0];
            r10.interpretExpression(r1, r12, r13);
            r0 = r0 + 1;
            goto L_0x0177;
            return;
            r11 = new java.lang.Exception;
            r12 = "last char not ')'";
            r11.<init>(r12);
            throw r11;
            r1 = java.util.Locale.US;
            r2 = new java.lang.Object[r3];
            r2[r0] = r7;
            r1 = java.lang.String.format(r1, r4, r2);
            r1 = java.util.regex.Pattern.compile(r1);
            r1 = r1.matcher(r11);
            r2 = r1.find();
            if (r2 == 0) goto L_0x01b3;
            r11 = r1.group(r3);
            r12.get(r11);
            r11 = r1.group(r8);
            r13 = r13 - r3;
            r10.interpretExpression(r11, r12, r13);
            return;
            r1 = 0;
            r2 = r10.operators;
            r4 = r2.length;
            if (r1 >= r4) goto L_0x021b;
            r2 = r2[r1];
            r4 = java.util.Locale.US;
            r5 = new java.lang.Object[r3];
            r6 = java.util.regex.Pattern.quote(r2);
            r5[r0] = r6;
            r6 = "(.+?)%s(.+)";
            r4 = java.lang.String.format(r4, r6, r5);
            r4 = java.util.regex.Pattern.compile(r4);
            r4 = r4.matcher(r11);
            r5 = r4.find();
            if (r5 != 0) goto L_0x01da;
            goto L_0x01f4;
            r5 = new boolean[r3];
            r6 = r4.group(r3);
            r9 = r13 + -1;
            r10.interpretStatement(r6, r12, r5, r9);
            r6 = r5[r0];
            if (r6 != 0) goto L_0x0209;
            r4 = r4.group(r8);
            r10.interpretStatement(r4, r12, r5, r9);
            r4 = r5[r0];
            if (r4 != 0) goto L_0x01f7;
            r1 = r1 + 1;
            goto L_0x01b4;
            r12 = new java.lang.Exception;
            r13 = new java.lang.Object[r8];
            r13[r0] = r2;
            r13[r3] = r11;
            r11 = "Premature right-side return of %s in %s";
            r11 = java.lang.String.format(r11, r13);
            r12.<init>(r11);
            throw r12;
            r12 = new java.lang.Exception;
            r13 = new java.lang.Object[r8];
            r13[r0] = r2;
            r13[r3] = r11;
            r11 = "Premature left-side return of %s in %s";
            r11 = java.lang.String.format(r11, r13);
            r12.<init>(r11);
            throw r12;
            r12 = java.util.Locale.US;
            r13 = new java.lang.Object[r3];
            r13[r0] = r7;
            r1 = "^(%s)\\(([a-zA-Z0-9_$,]*)\\)$";
            r12 = java.lang.String.format(r12, r1, r13);
            r12 = java.util.regex.Pattern.compile(r12);
            r12 = r12.matcher(r11);
            r13 = r12.find();
            if (r13 == 0) goto L_0x023c;
            r12 = r12.group(r3);
            r10.extractFunction(r12);
            r12 = new java.lang.Exception;
            r13 = new java.lang.Object[r3];
            r13[r0] = r11;
            r11 = "Unsupported JS expression %s";
            r11 = java.lang.String.format(r11, r13);
            r12.<init>(r11);
            throw r12;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.WebPlayerView$JSExtractor.interpretExpression(java.lang.String, java.util.HashMap, int):void");
        }

        public JSExtractor(String str) {
            this.jsCode = str;
        }

        private void interpretStatement(String str, HashMap<String, String> hashMap, boolean[] zArr, int i) throws Exception {
            if (i >= 0) {
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
                return;
            }
            throw new Exception("recursion limit reached");
        }

        /* JADX WARNING: Removed duplicated region for block: B:13:0x0067 A:{LOOP_END, LOOP:1: B:11:0x0061->B:13:0x0067} */
        private java.util.HashMap<java.lang.String, java.lang.Object> extractObject(java.lang.String r9) throws java.lang.Exception {
            /*
            r8 = this;
            r0 = new java.util.HashMap;
            r0.<init>();
            r1 = java.util.Locale.US;
            r2 = 2;
            r3 = new java.lang.Object[r2];
            r9 = java.util.regex.Pattern.quote(r9);
            r4 = 0;
            r3[r4] = r9;
            r9 = 1;
            r5 = "(?:[a-zA-Z$0-9]+|\"[a-zA-Z$0-9]+\"|'[a-zA-Z$0-9]+')";
            r3[r9] = r5;
            r6 = "(?:var\\s+)?%s\\s*=\\s*\\{\\s*((%s\\s*:\\s*function\\(.*?\\)\\s*\\{.*?\\}(?:,\\s*)?)*)\\}\\s*;";
            r1 = java.lang.String.format(r1, r6, r3);
            r1 = java.util.regex.Pattern.compile(r1);
            r3 = r8.jsCode;
            r1 = r1.matcher(r3);
            r3 = 0;
        L_0x0027:
            r6 = r1.find();
            if (r6 == 0) goto L_0x004f;
        L_0x002d:
            r3 = r1.group();
            r6 = r1.group(r2);
            r7 = android.text.TextUtils.isEmpty(r6);
            if (r7 == 0) goto L_0x003d;
        L_0x003b:
            r3 = r6;
            goto L_0x0027;
        L_0x003d:
            r7 = r8.codeLines;
            r3 = r7.contains(r3);
            if (r3 != 0) goto L_0x004e;
        L_0x0045:
            r3 = r8.codeLines;
            r1 = r1.group();
            r3.add(r1);
        L_0x004e:
            r3 = r6;
        L_0x004f:
            r9 = new java.lang.Object[r9];
            r9[r4] = r5;
            r1 = "(%s)\\s*:\\s*function\\(([a-z,]+)\\)\\{([^}]+)\\}";
            r9 = java.lang.String.format(r1, r9);
            r9 = java.util.regex.Pattern.compile(r9);
            r9 = r9.matcher(r3);
        L_0x0061:
            r1 = r9.find();
            if (r1 == 0) goto L_0x007a;
        L_0x0067:
            r1 = r9.group(r2);
            r3 = ",";
            r1 = r1.split(r3);
            r3 = 3;
            r3 = r9.group(r3);
            r8.buildFunction(r1, r3);
            goto L_0x0061;
        L_0x007a:
            return r0;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.WebPlayerView$JSExtractor.extractObject(java.lang.String):java.util.HashMap");
        }

        private void buildFunction(String[] strArr, String str) throws Exception {
            HashMap hashMap = new HashMap();
            for (Object put : strArr) {
                hashMap.put(put, "");
            }
            strArr = str.split(";");
            boolean[] zArr = new boolean[1];
            int i = 0;
            while (i < strArr.length) {
                interpretStatement(strArr[i], hashMap, zArr, 100);
                if (!zArr[0]) {
                    i++;
                } else {
                    return;
                }
            }
        }

        private String extractFunction(String str) {
            try {
                str = Pattern.quote(str);
                Matcher matcher = Pattern.compile(String.format(Locale.US, "(?x)(?:function\\s+%s|[{;,]\\s*%s\\s*=\\s*function|var\\s+%s\\s*=\\s*function)\\s*\\(([^)]*)\\)\\s*\\{([^}]+)\\}", new Object[]{str, str, str})).matcher(this.jsCode);
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
            } catch (Exception e) {
                this.codeLines.clear();
                FileLog.e(e);
            }
            return TextUtils.join("", this.codeLines);
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

        /* Access modifiers changed, original: protected|varargs */
        public String doInBackground(Void... voidArr) {
            String str = null;
            String downloadUrlContent = WebPlayerView.this.downloadUrlContent(this, this.currentUrl, null, false);
            if (isCancelled()) {
                return null;
            }
            try {
                Matcher matcher = WebPlayerView.twitchClipFilePattern.matcher(downloadUrlContent);
                if (matcher.find()) {
                    this.results[0] = new JSONObject(matcher.group(1)).getJSONArray("quality_options").getJSONObject(0).getString("source");
                    this.results[1] = "other";
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
            if (!isCancelled()) {
                str = this.results[0];
            }
            return str;
        }

        /* Access modifiers changed, original: protected */
        public void onPostExecute(String str) {
            if (str != null) {
                WebPlayerView.this.initied = true;
                WebPlayerView.this.playVideoUrl = str;
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

        public TwitchStreamVideoTask(String str, String str2) {
            this.videoId = str2;
            this.currentUrl = str;
        }

        /* Access modifiers changed, original: protected|varargs */
        public String doInBackground(Void... voidArr) {
            String str = "UTF-8";
            HashMap hashMap = new HashMap();
            hashMap.put("Client-ID", "jzkbprfvar_iqj646a697cyrvl0zt2m6");
            int indexOf = this.videoId.indexOf(38);
            if (indexOf > 0) {
                this.videoId = this.videoId.substring(0, indexOf);
            }
            String downloadUrlContent = WebPlayerView.this.downloadUrlContent(this, String.format(Locale.US, "https://api.twitch.tv/kraken/streams/%s?stream_type=all", new Object[]{this.videoId}), hashMap, false);
            String str2 = null;
            if (isCancelled()) {
                return null;
            }
            try {
                new JSONObject(downloadUrlContent).getJSONObject("stream");
                JSONObject jSONObject = new JSONObject(WebPlayerView.this.downloadUrlContent(this, String.format(Locale.US, "https://api.twitch.tv/api/channels/%s/access_token", new Object[]{this.videoId}), hashMap, false));
                String encode = URLEncoder.encode(jSONObject.getString("sig"), str);
                downloadUrlContent = URLEncoder.encode(jSONObject.getString("token"), str);
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("https://youtube.googleapis.com/v/");
                stringBuilder.append(this.videoId);
                URLEncoder.encode(stringBuilder.toString(), str);
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("allow_source=true&allow_audio_only=true&allow_spectre=true&player=twitchweb&segment_preference=4&p=");
                stringBuilder2.append((int) (Math.random() * 1.0E7d));
                stringBuilder2.append("&sig=");
                stringBuilder2.append(encode);
                stringBuilder2.append("&token=");
                stringBuilder2.append(downloadUrlContent);
                str = stringBuilder2.toString();
                this.results[0] = String.format(Locale.US, "https://usher.ttvnw.net/api/channel/hls/%s.m3u8?%s", new Object[]{this.videoId, str});
                this.results[1] = "hls";
            } catch (Exception e) {
                FileLog.e(e);
            }
            if (!isCancelled()) {
                str2 = this.results[0];
            }
            return str2;
        }

        /* Access modifiers changed, original: protected */
        public void onPostExecute(String str) {
            if (str != null) {
                WebPlayerView.this.initied = true;
                WebPlayerView.this.playVideoUrl = str;
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

        public VimeoVideoTask(String str) {
            this.videoId = str;
        }

        /* Access modifiers changed, original: protected|varargs */
        /* JADX WARNING: Missing exception handler attribute for start block: B:11:0x0048 */
        /* JADX WARNING: Can't wrap try/catch for region: R(7:7|8|9|10|11|12|13) */
        public java.lang.String doInBackground(java.lang.Void... r8) {
            /*
            r7 = this;
            r8 = "progressive";
            r0 = "hls";
            r1 = org.telegram.ui.Components.WebPlayerView.this;
            r2 = java.util.Locale.US;
            r3 = 1;
            r4 = new java.lang.Object[r3];
            r5 = r7.videoId;
            r6 = 0;
            r4[r6] = r5;
            r5 = "https://player.vimeo.com/video/%s/config";
            r2 = java.lang.String.format(r2, r5, r4);
            r1 = r1.downloadUrlContent(r7, r2);
            r2 = r7.isCancelled();
            r4 = 0;
            if (r2 == 0) goto L_0x0022;
        L_0x0021:
            return r4;
        L_0x0022:
            r2 = new org.json.JSONObject;	 Catch:{ Exception -> 0x0082 }
            r2.<init>(r1);	 Catch:{ Exception -> 0x0082 }
            r1 = "request";
            r1 = r2.getJSONObject(r1);	 Catch:{ Exception -> 0x0082 }
            r2 = "files";
            r1 = r1.getJSONObject(r2);	 Catch:{ Exception -> 0x0082 }
            r2 = r1.has(r0);	 Catch:{ Exception -> 0x0082 }
            r5 = "url";
            if (r2 == 0) goto L_0x0065;
        L_0x003b:
            r8 = r1.getJSONObject(r0);	 Catch:{ Exception -> 0x0082 }
            r1 = r7.results;	 Catch:{ Exception -> 0x0048 }
            r2 = r8.getString(r5);	 Catch:{ Exception -> 0x0048 }
            r1[r6] = r2;	 Catch:{ Exception -> 0x0048 }
            goto L_0x0060;
        L_0x0048:
            r1 = "default_cdn";
            r1 = r8.getString(r1);	 Catch:{ Exception -> 0x0082 }
            r2 = "cdns";
            r8 = r8.getJSONObject(r2);	 Catch:{ Exception -> 0x0082 }
            r8 = r8.getJSONObject(r1);	 Catch:{ Exception -> 0x0082 }
            r1 = r7.results;	 Catch:{ Exception -> 0x0082 }
            r8 = r8.getString(r5);	 Catch:{ Exception -> 0x0082 }
            r1[r6] = r8;	 Catch:{ Exception -> 0x0082 }
        L_0x0060:
            r8 = r7.results;	 Catch:{ Exception -> 0x0082 }
            r8[r3] = r0;	 Catch:{ Exception -> 0x0082 }
            goto L_0x0086;
        L_0x0065:
            r0 = r1.has(r8);	 Catch:{ Exception -> 0x0082 }
            if (r0 == 0) goto L_0x0086;
        L_0x006b:
            r0 = r7.results;	 Catch:{ Exception -> 0x0082 }
            r2 = "other";
            r0[r3] = r2;	 Catch:{ Exception -> 0x0082 }
            r8 = r1.getJSONArray(r8);	 Catch:{ Exception -> 0x0082 }
            r8 = r8.getJSONObject(r6);	 Catch:{ Exception -> 0x0082 }
            r0 = r7.results;	 Catch:{ Exception -> 0x0082 }
            r8 = r8.getString(r5);	 Catch:{ Exception -> 0x0082 }
            r0[r6] = r8;	 Catch:{ Exception -> 0x0082 }
            goto L_0x0086;
        L_0x0082:
            r8 = move-exception;
            org.telegram.messenger.FileLog.e(r8);
        L_0x0086:
            r8 = r7.isCancelled();
            if (r8 == 0) goto L_0x008d;
        L_0x008c:
            goto L_0x0091;
        L_0x008d:
            r8 = r7.results;
            r4 = r8[r6];
        L_0x0091:
            return r4;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.WebPlayerView$VimeoVideoTask.doInBackground(java.lang.Void[]):java.lang.String");
        }

        /* Access modifiers changed, original: protected */
        public void onPostExecute(String str) {
            if (str != null) {
                WebPlayerView.this.initied = true;
                WebPlayerView.this.playVideoUrl = str;
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

        public YoutubeVideoTask(String str) {
            this.videoId = str;
        }

        /* Access modifiers changed, original: protected|varargs */
        /* JADX WARNING: Removed duplicated region for block: B:158:0x03d8  */
        /* JADX WARNING: Removed duplicated region for block: B:119:0x02d8  */
        /* JADX WARNING: Missing block: B:91:0x0252, code skipped:
            return null;
     */
        public java.lang.String[] doInBackground(java.lang.Void... r24) {
            /*
            r23 = this;
            r1 = r23;
            r2 = "UTF-8";
            r0 = org.telegram.ui.Components.WebPlayerView.this;
            r3 = new java.lang.StringBuilder;
            r3.<init>();
            r4 = "https://www.youtube.com/embed/";
            r3.append(r4);
            r4 = r1.videoId;
            r3.append(r4);
            r3 = r3.toString();
            r3 = r0.downloadUrlContent(r1, r3);
            r0 = r23.isCancelled();
            r4 = 0;
            if (r0 == 0) goto L_0x0025;
        L_0x0024:
            return r4;
        L_0x0025:
            r0 = new java.lang.StringBuilder;
            r0.<init>();
            r5 = "video_id=";
            r0.append(r5);
            r5 = r1.videoId;
            r0.append(r5);
            r5 = "&ps=default&gl=US&hl=en";
            r0.append(r5);
            r5 = r0.toString();
            r0 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0069 }
            r0.<init>();	 Catch:{ Exception -> 0x0069 }
            r0.append(r5);	 Catch:{ Exception -> 0x0069 }
            r6 = "&eurl=";
            r0.append(r6);	 Catch:{ Exception -> 0x0069 }
            r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0069 }
            r6.<init>();	 Catch:{ Exception -> 0x0069 }
            r7 = "https://youtube.googleapis.com/v/";
            r6.append(r7);	 Catch:{ Exception -> 0x0069 }
            r7 = r1.videoId;	 Catch:{ Exception -> 0x0069 }
            r6.append(r7);	 Catch:{ Exception -> 0x0069 }
            r6 = r6.toString();	 Catch:{ Exception -> 0x0069 }
            r6 = java.net.URLEncoder.encode(r6, r2);	 Catch:{ Exception -> 0x0069 }
            r0.append(r6);	 Catch:{ Exception -> 0x0069 }
            r5 = r0.toString();	 Catch:{ Exception -> 0x0069 }
            goto L_0x006d;
        L_0x0069:
            r0 = move-exception;
            org.telegram.messenger.FileLog.e(r0);
        L_0x006d:
            if (r3 == 0) goto L_0x00af;
        L_0x006f:
            r0 = org.telegram.ui.Components.WebPlayerView.stsPattern;
            r0 = r0.matcher(r3);
            r6 = r0.find();
            r7 = "&sts=";
            if (r6 == 0) goto L_0x00a0;
        L_0x007f:
            r6 = new java.lang.StringBuilder;
            r6.<init>();
            r6.append(r5);
            r6.append(r7);
            r5 = r0.start();
            r5 = r5 + 6;
            r0 = r0.end();
            r0 = r3.substring(r5, r0);
            r6.append(r0);
            r5 = r6.toString();
            goto L_0x00af;
        L_0x00a0:
            r0 = new java.lang.StringBuilder;
            r0.<init>();
            r0.append(r5);
            r0.append(r7);
            r5 = r0.toString();
        L_0x00af:
            r0 = r1.result;
            r6 = 1;
            r7 = "dash";
            r0[r6] = r7;
            r0 = 5;
            r7 = new java.lang.String[r0];
            r8 = 0;
            r0 = "";
            r7[r8] = r0;
            r0 = "&el=leanback";
            r7[r6] = r0;
            r9 = 2;
            r0 = "&el=embedded";
            r7[r9] = r0;
            r10 = 3;
            r0 = "&el=detailpage";
            r7[r10] = r0;
            r0 = 4;
            r11 = "&el=vevo";
            r7[r0] = r11;
            r0 = r4;
            r11 = 0;
            r12 = 0;
        L_0x00d4:
            r13 = r7.length;
            r14 = "/s/";
            if (r11 >= r13) goto L_0x025f;
        L_0x00d9:
            r13 = org.telegram.ui.Components.WebPlayerView.this;
            r15 = new java.lang.StringBuilder;
            r15.<init>();
            r10 = "https://www.youtube.com/get_video_info?";
            r15.append(r10);
            r15.append(r5);
            r10 = r7[r11];
            r15.append(r10);
            r10 = r15.toString();
            r10 = r13.downloadUrlContent(r1, r10);
            r13 = r23.isCancelled();
            if (r13 == 0) goto L_0x00fc;
        L_0x00fb:
            return r4;
        L_0x00fc:
            if (r10 == 0) goto L_0x0235;
        L_0x00fe:
            r13 = "&";
            r10 = r10.split(r13);
            r18 = r0;
            r15 = r4;
            r16 = r12;
            r12 = 0;
            r13 = 0;
            r17 = 0;
        L_0x010d:
            r0 = r10.length;
            if (r12 >= r0) goto L_0x0230;
        L_0x0110:
            r0 = r10[r12];
            r4 = "dashmpd";
            r0 = r0.startsWith(r4);
            r4 = "=";
            if (r0 == 0) goto L_0x0138;
        L_0x011c:
            r0 = r10[r12];
            r0 = r0.split(r4);
            r4 = r0.length;
            if (r4 != r9) goto L_0x0134;
        L_0x0125:
            r4 = r1.result;	 Catch:{ Exception -> 0x0130 }
            r0 = r0[r6];	 Catch:{ Exception -> 0x0130 }
            r0 = java.net.URLDecoder.decode(r0, r2);	 Catch:{ Exception -> 0x0130 }
            r4[r8] = r0;	 Catch:{ Exception -> 0x0130 }
            goto L_0x0134;
        L_0x0130:
            r0 = move-exception;
            org.telegram.messenger.FileLog.e(r0);
        L_0x0134:
            r17 = 1;
            goto L_0x0228;
        L_0x0138:
            r0 = r10[r12];
            r8 = "url_encoded_fmt_stream_map";
            r0 = r0.startsWith(r8);
            if (r0 == 0) goto L_0x01bc;
        L_0x0142:
            r0 = r10[r12];
            r0 = r0.split(r4);
            r8 = r0.length;
            if (r8 != r9) goto L_0x0228;
        L_0x014b:
            r0 = r0[r6];	 Catch:{ Exception -> 0x01b7 }
            r0 = java.net.URLDecoder.decode(r0, r2);	 Catch:{ Exception -> 0x01b7 }
            r8 = "[&,]";
            r0 = r0.split(r8);	 Catch:{ Exception -> 0x01b7 }
            r8 = 0;
            r20 = 0;
            r21 = 0;
        L_0x015c:
            r9 = r0.length;	 Catch:{ Exception -> 0x01b7 }
            if (r8 >= r9) goto L_0x0228;
        L_0x015f:
            r9 = r0[r8];	 Catch:{ Exception -> 0x01b7 }
            r9 = r9.split(r4);	 Catch:{ Exception -> 0x01b7 }
            r19 = 0;
            r6 = r9[r19];	 Catch:{ Exception -> 0x01b7 }
            r22 = r0;
            r0 = "type";
            r0 = r6.startsWith(r0);	 Catch:{ Exception -> 0x01b7 }
            if (r0 == 0) goto L_0x0185;
        L_0x0173:
            r6 = 1;
            r0 = r9[r6];	 Catch:{ Exception -> 0x01b7 }
            r0 = java.net.URLDecoder.decode(r0, r2);	 Catch:{ Exception -> 0x01b7 }
            r6 = "video/mp4";
            r0 = r0.contains(r6);	 Catch:{ Exception -> 0x01b7 }
            if (r0 == 0) goto L_0x01a9;
        L_0x0182:
            r20 = 1;
            goto L_0x01a9;
        L_0x0185:
            r6 = 0;
            r0 = r9[r6];	 Catch:{ Exception -> 0x01b7 }
            r6 = "url";
            r0 = r0.startsWith(r6);	 Catch:{ Exception -> 0x01b7 }
            if (r0 == 0) goto L_0x019a;
        L_0x0190:
            r6 = 1;
            r0 = r9[r6];	 Catch:{ Exception -> 0x01b7 }
            r0 = java.net.URLDecoder.decode(r0, r2);	 Catch:{ Exception -> 0x01b7 }
            r21 = r0;
            goto L_0x01a9;
        L_0x019a:
            r6 = 0;
            r0 = r9[r6];	 Catch:{ Exception -> 0x01b7 }
            r6 = "itag";
            r0 = r0.startsWith(r6);	 Catch:{ Exception -> 0x01b7 }
            if (r0 == 0) goto L_0x01a9;
        L_0x01a5:
            r20 = 0;
            r21 = 0;
        L_0x01a9:
            if (r20 == 0) goto L_0x01b1;
        L_0x01ab:
            if (r21 == 0) goto L_0x01b1;
        L_0x01ad:
            r18 = r21;
            goto L_0x0228;
        L_0x01b1:
            r8 = r8 + 1;
            r0 = r22;
            r6 = 1;
            goto L_0x015c;
        L_0x01b7:
            r0 = move-exception;
            org.telegram.messenger.FileLog.e(r0);
            goto L_0x0228;
        L_0x01bc:
            r0 = r10[r12];
            r6 = "use_cipher_signature";
            r0 = r0.startsWith(r6);
            if (r0 == 0) goto L_0x01e2;
        L_0x01c6:
            r0 = r10[r12];
            r0 = r0.split(r4);
            r4 = r0.length;
            r6 = 2;
            if (r4 != r6) goto L_0x0228;
        L_0x01d0:
            r4 = 1;
            r0 = r0[r4];
            r0 = r0.toLowerCase();
            r4 = "true";
            r0 = r0.equals(r4);
            if (r0 == 0) goto L_0x0228;
        L_0x01df:
            r16 = 1;
            goto L_0x0228;
        L_0x01e2:
            r0 = r10[r12];
            r6 = "hlsvp";
            r0 = r0.startsWith(r6);
            if (r0 == 0) goto L_0x0204;
        L_0x01ec:
            r0 = r10[r12];
            r0 = r0.split(r4);
            r4 = r0.length;
            r6 = 2;
            if (r4 != r6) goto L_0x0228;
        L_0x01f6:
            r4 = 1;
            r0 = r0[r4];	 Catch:{ Exception -> 0x01ff }
            r0 = java.net.URLDecoder.decode(r0, r2);	 Catch:{ Exception -> 0x01ff }
            r15 = r0;
            goto L_0x0228;
        L_0x01ff:
            r0 = move-exception;
            org.telegram.messenger.FileLog.e(r0);
            goto L_0x0228;
        L_0x0204:
            r0 = r10[r12];
            r6 = "livestream";
            r0 = r0.startsWith(r6);
            if (r0 == 0) goto L_0x0228;
        L_0x020e:
            r0 = r10[r12];
            r0 = r0.split(r4);
            r4 = r0.length;
            r6 = 2;
            if (r4 != r6) goto L_0x0228;
        L_0x0218:
            r4 = 1;
            r0 = r0[r4];
            r0 = r0.toLowerCase();
            r4 = "1";
            r0 = r0.equals(r4);
            if (r0 == 0) goto L_0x0228;
        L_0x0227:
            r13 = 1;
        L_0x0228:
            r12 = r12 + 1;
            r4 = 0;
            r6 = 1;
            r8 = 0;
            r9 = 2;
            goto L_0x010d;
        L_0x0230:
            r12 = r16;
            r0 = r18;
            goto L_0x0239;
        L_0x0235:
            r13 = 0;
            r15 = 0;
            r17 = 0;
        L_0x0239:
            if (r13 == 0) goto L_0x0253;
        L_0x023b:
            if (r15 == 0) goto L_0x0251;
        L_0x023d:
            if (r12 != 0) goto L_0x0251;
        L_0x023f:
            r4 = r15.contains(r14);
            if (r4 == 0) goto L_0x0246;
        L_0x0245:
            goto L_0x0251;
        L_0x0246:
            r4 = r1.result;
            r6 = 0;
            r4[r6] = r15;
            r6 = "hls";
            r8 = 1;
            r4[r8] = r6;
            goto L_0x0253;
        L_0x0251:
            r2 = 0;
            return r2;
        L_0x0253:
            if (r17 == 0) goto L_0x0256;
        L_0x0255:
            goto L_0x025f;
        L_0x0256:
            r11 = r11 + 1;
            r4 = 0;
            r6 = 1;
            r8 = 0;
            r9 = 2;
            r10 = 3;
            goto L_0x00d4;
        L_0x025f:
            r6 = r12;
            r2 = r1.result;
            r4 = 0;
            r5 = r2[r4];
            if (r5 != 0) goto L_0x0270;
        L_0x0267:
            if (r0 == 0) goto L_0x0270;
        L_0x0269:
            r2[r4] = r0;
            r0 = "other";
            r5 = 1;
            r2[r5] = r0;
        L_0x0270:
            r0 = r1.result;
            r2 = r0[r4];
            if (r2 == 0) goto L_0x044d;
        L_0x0276:
            if (r6 != 0) goto L_0x0280;
        L_0x0278:
            r0 = r0[r4];
            r0 = r0.contains(r14);
            if (r0 == 0) goto L_0x044d;
        L_0x0280:
            if (r3 == 0) goto L_0x044d;
        L_0x0282:
            r0 = r1.result;
            r0 = r0[r4];
            r0 = r0.indexOf(r14);
            r2 = r1.result;
            r2 = r2[r4];
            r5 = 47;
            r6 = r0 + 10;
            r2 = r2.indexOf(r5, r6);
            r5 = -1;
            if (r0 == r5) goto L_0x0449;
        L_0x0299:
            if (r2 != r5) goto L_0x02a3;
        L_0x029b:
            r2 = r1.result;
            r2 = r2[r4];
            r2 = r2.length();
        L_0x02a3:
            r5 = r1.result;
            r5 = r5[r4];
            r0 = r5.substring(r0, r2);
            r1.sig = r0;
            r0 = org.telegram.ui.Components.WebPlayerView.jsPattern;
            r0 = r0.matcher(r3);
            r2 = r0.find();
            if (r2 == 0) goto L_0x02d5;
        L_0x02bb:
            r2 = new org.json.JSONTokener;	 Catch:{ Exception -> 0x02d1 }
            r3 = 1;
            r0 = r0.group(r3);	 Catch:{ Exception -> 0x02d1 }
            r2.<init>(r0);	 Catch:{ Exception -> 0x02d1 }
            r0 = r2.nextValue();	 Catch:{ Exception -> 0x02d1 }
            r2 = r0 instanceof java.lang.String;	 Catch:{ Exception -> 0x02d1 }
            if (r2 == 0) goto L_0x02d5;
        L_0x02cd:
            r0 = (java.lang.String) r0;	 Catch:{ Exception -> 0x02d1 }
            r4 = r0;
            goto L_0x02d6;
        L_0x02d1:
            r0 = move-exception;
            org.telegram.messenger.FileLog.e(r0);
        L_0x02d5:
            r4 = 0;
        L_0x02d6:
            if (r4 == 0) goto L_0x0449;
        L_0x02d8:
            r0 = org.telegram.ui.Components.WebPlayerView.playerIdPattern;
            r0 = r0.matcher(r4);
            r2 = r0.find();
            if (r2 == 0) goto L_0x0300;
        L_0x02e6:
            r2 = new java.lang.StringBuilder;
            r2.<init>();
            r3 = 1;
            r5 = r0.group(r3);
            r2.append(r5);
            r3 = 2;
            r0 = r0.group(r3);
            r2.append(r0);
            r0 = r2.toString();
            goto L_0x0301;
        L_0x0300:
            r0 = 0;
        L_0x0301:
            r2 = org.telegram.messenger.ApplicationLoader.applicationContext;
            r3 = "youtubecode";
            r5 = 0;
            r2 = r2.getSharedPreferences(r3, r5);
            r3 = "n";
            if (r0 == 0) goto L_0x0328;
        L_0x030f:
            r6 = 0;
            r7 = r2.getString(r0, r6);
            r8 = new java.lang.StringBuilder;
            r8.<init>();
            r8.append(r0);
            r8.append(r3);
            r8 = r8.toString();
            r8 = r2.getString(r8, r6);
            goto L_0x032a;
        L_0x0328:
            r7 = 0;
            r8 = 0;
        L_0x032a:
            if (r7 != 0) goto L_0x03d0;
        L_0x032c:
            r6 = "//";
            r6 = r4.startsWith(r6);
            if (r6 == 0) goto L_0x0346;
        L_0x0334:
            r6 = new java.lang.StringBuilder;
            r6.<init>();
            r9 = "https:";
            r6.append(r9);
            r6.append(r4);
            r4 = r6.toString();
            goto L_0x035f;
        L_0x0346:
            r6 = "/";
            r6 = r4.startsWith(r6);
            if (r6 == 0) goto L_0x035f;
        L_0x034e:
            r6 = new java.lang.StringBuilder;
            r6.<init>();
            r9 = "https://www.youtube.com";
            r6.append(r9);
            r6.append(r4);
            r4 = r6.toString();
        L_0x035f:
            r6 = org.telegram.ui.Components.WebPlayerView.this;
            r4 = r6.downloadUrlContent(r1, r4);
            r6 = r23.isCancelled();
            if (r6 == 0) goto L_0x036d;
        L_0x036b:
            r9 = 0;
            return r9;
        L_0x036d:
            r9 = 0;
            if (r4 == 0) goto L_0x03d1;
        L_0x0370:
            r6 = org.telegram.ui.Components.WebPlayerView.sigPattern;
            r6 = r6.matcher(r4);
            r10 = r6.find();
            if (r10 == 0) goto L_0x0384;
        L_0x037e:
            r10 = 1;
            r8 = r6.group(r10);
            goto L_0x0397;
        L_0x0384:
            r10 = 1;
            r6 = org.telegram.ui.Components.WebPlayerView.sigPattern2;
            r6 = r6.matcher(r4);
            r11 = r6.find();
            if (r11 == 0) goto L_0x0397;
        L_0x0393:
            r8 = r6.group(r10);
        L_0x0397:
            if (r8 == 0) goto L_0x03d2;
        L_0x0399:
            r6 = new org.telegram.ui.Components.WebPlayerView$JSExtractor;	 Catch:{ Exception -> 0x03cb }
            r11 = org.telegram.ui.Components.WebPlayerView.this;	 Catch:{ Exception -> 0x03cb }
            r6.<init>(r4);	 Catch:{ Exception -> 0x03cb }
            r7 = r6.extractFunction(r8);	 Catch:{ Exception -> 0x03cb }
            r4 = android.text.TextUtils.isEmpty(r7);	 Catch:{ Exception -> 0x03cb }
            if (r4 != 0) goto L_0x03d2;
        L_0x03aa:
            if (r0 == 0) goto L_0x03d2;
        L_0x03ac:
            r2 = r2.edit();	 Catch:{ Exception -> 0x03cb }
            r2 = r2.putString(r0, r7);	 Catch:{ Exception -> 0x03cb }
            r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x03cb }
            r4.<init>();	 Catch:{ Exception -> 0x03cb }
            r4.append(r0);	 Catch:{ Exception -> 0x03cb }
            r4.append(r3);	 Catch:{ Exception -> 0x03cb }
            r0 = r4.toString();	 Catch:{ Exception -> 0x03cb }
            r0 = r2.putString(r0, r8);	 Catch:{ Exception -> 0x03cb }
            r0.commit();	 Catch:{ Exception -> 0x03cb }
            goto L_0x03d2;
        L_0x03cb:
            r0 = move-exception;
            org.telegram.messenger.FileLog.e(r0);
            goto L_0x03d2;
        L_0x03d0:
            r9 = 0;
        L_0x03d1:
            r10 = 1;
        L_0x03d2:
            r0 = android.text.TextUtils.isEmpty(r7);
            if (r0 != 0) goto L_0x044b;
        L_0x03d8:
            r0 = android.os.Build.VERSION.SDK_INT;
            r2 = 21;
            r3 = "('";
            if (r0 < r2) goto L_0x0402;
        L_0x03e0:
            r0 = new java.lang.StringBuilder;
            r0.<init>();
            r0.append(r7);
            r0.append(r8);
            r0.append(r3);
            r2 = r1.sig;
            r3 = 3;
            r2 = r2.substring(r3);
            r0.append(r2);
            r2 = "');";
            r0.append(r2);
            r0 = r0.toString();
            goto L_0x0436;
        L_0x0402:
            r0 = new java.lang.StringBuilder;
            r0.<init>();
            r0.append(r7);
            r2 = "window.";
            r0.append(r2);
            r2 = org.telegram.ui.Components.WebPlayerView.this;
            r2 = r2.interfaceName;
            r0.append(r2);
            r2 = ".returnResultToJava(";
            r0.append(r2);
            r0.append(r8);
            r0.append(r3);
            r2 = r1.sig;
            r3 = 3;
            r2 = r2.substring(r3);
            r0.append(r2);
            r2 = "'));";
            r0.append(r2);
            r0 = r0.toString();
        L_0x0436:
            r2 = new org.telegram.ui.Components.-$$Lambda$WebPlayerView$YoutubeVideoTask$GMLQkdVjUFyM84BTj7n250BCLASSNAMEg;	 Catch:{ Exception -> 0x0444 }
            r2.<init>(r1, r0);	 Catch:{ Exception -> 0x0444 }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r2);	 Catch:{ Exception -> 0x0444 }
            r0 = r1.countDownLatch;	 Catch:{ Exception -> 0x0444 }
            r0.await();	 Catch:{ Exception -> 0x0444 }
            goto L_0x044f;
        L_0x0444:
            r0 = move-exception;
            org.telegram.messenger.FileLog.e(r0);
            goto L_0x044b;
        L_0x0449:
            r9 = 0;
            r10 = 1;
        L_0x044b:
            r5 = 1;
            goto L_0x044f;
        L_0x044d:
            r9 = 0;
            r5 = r6;
        L_0x044f:
            r0 = r23.isCancelled();
            if (r0 != 0) goto L_0x045b;
        L_0x0455:
            if (r5 == 0) goto L_0x0458;
        L_0x0457:
            goto L_0x045b;
        L_0x0458:
            r4 = r1.result;
            goto L_0x045c;
        L_0x045b:
            r4 = r9;
        L_0x045c:
            return r4;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.WebPlayerView$YoutubeVideoTask.doInBackground(java.lang.Void[]):java.lang.String[]");
        }

        public /* synthetic */ void lambda$doInBackground$1$WebPlayerView$YoutubeVideoTask(String str) {
            if (VERSION.SDK_INT >= 21) {
                WebPlayerView.this.webView.evaluateJavascript(str, new -$$Lambda$WebPlayerView$YoutubeVideoTask$frhxjuVE3CuEISsmdJnF0IVDS2M(this));
                return;
            }
            try {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("<script>");
                stringBuilder.append(str);
                stringBuilder.append("</script>");
                str = Base64.encodeToString(stringBuilder.toString().getBytes("UTF-8"), 0);
                WebView access$2100 = WebPlayerView.this.webView;
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("data:text/html;charset=utf-8;base64,");
                stringBuilder2.append(str);
                access$2100.loadUrl(stringBuilder2.toString());
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        public /* synthetic */ void lambda$null$0$WebPlayerView$YoutubeVideoTask(String str) {
            String[] strArr = this.result;
            String str2 = strArr[0];
            String str3 = this.sig;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("/signature/");
            stringBuilder.append(str.substring(1, str.length() - 1));
            strArr[0] = str2.replace(str3, stringBuilder.toString());
            this.countDownLatch.countDown();
        }

        private void onInterfaceResult(String str) {
            String[] strArr = this.result;
            String str2 = strArr[0];
            String str3 = this.sig;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("/signature/");
            stringBuilder.append(str);
            strArr[0] = str2.replace(str3, stringBuilder.toString());
            this.countDownLatch.countDown();
        }

        /* Access modifiers changed, original: protected */
        public void onPostExecute(String[] strArr) {
            if (strArr[0] != null) {
                if (BuildVars.LOGS_ENABLED) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("start play youtube video ");
                    stringBuilder.append(strArr[1]);
                    stringBuilder.append(" ");
                    stringBuilder.append(strArr[0]);
                    FileLog.d(stringBuilder.toString());
                }
                WebPlayerView.this.initied = true;
                WebPlayerView.this.playVideoUrl = strArr[0];
                WebPlayerView.this.playVideoType = strArr[1];
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
    public String downloadUrlContent(AsyncTask asyncTask, String str) {
        return downloadUrlContent(asyncTask, str, null, true);
    }

    /* Access modifiers changed, original: protected */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x00ef A:{Catch:{ Throwable -> 0x00fb }} */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x00d9 A:{SYNTHETIC, Splitter:B:42:0x00d9} */
    /* JADX WARNING: Removed duplicated region for block: B:122:0x01a0  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x0136 A:{SYNTHETIC, Splitter:B:71:0x0136} */
    /* JADX WARNING: Removed duplicated region for block: B:125:0x01ab  */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x01a4  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x0136 A:{SYNTHETIC, Splitter:B:71:0x0136} */
    /* JADX WARNING: Removed duplicated region for block: B:122:0x01a0  */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x01a4  */
    /* JADX WARNING: Removed duplicated region for block: B:125:0x01ab  */
    /* JADX WARNING: Removed duplicated region for block: B:122:0x01a0  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x0136 A:{SYNTHETIC, Splitter:B:71:0x0136} */
    /* JADX WARNING: Removed duplicated region for block: B:125:0x01ab  */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x01a4  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x0136 A:{SYNTHETIC, Splitter:B:71:0x0136} */
    /* JADX WARNING: Removed duplicated region for block: B:122:0x01a0  */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x01a4  */
    /* JADX WARNING: Removed duplicated region for block: B:125:0x01ab  */
    /* JADX WARNING: Removed duplicated region for block: B:122:0x01a0  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x0136 A:{SYNTHETIC, Splitter:B:71:0x0136} */
    /* JADX WARNING: Removed duplicated region for block: B:125:0x01ab  */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x01a4  */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x010d  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x0106  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x0136 A:{SYNTHETIC, Splitter:B:71:0x0136} */
    /* JADX WARNING: Removed duplicated region for block: B:122:0x01a0  */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x01a4  */
    /* JADX WARNING: Removed duplicated region for block: B:125:0x01ab  */
    /* JADX WARNING: Removed duplicated region for block: B:117:0x0195 A:{SYNTHETIC, Splitter:B:117:0x0195} */
    /* JADX WARNING: Removed duplicated region for block: B:125:0x01ab  */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x01a4  */
    /* JADX WARNING: Removed duplicated region for block: B:117:0x0195 A:{SYNTHETIC, Splitter:B:117:0x0195} */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x01a4  */
    /* JADX WARNING: Removed duplicated region for block: B:125:0x01ab  */
    /* JADX WARNING: Removed duplicated region for block: B:117:0x0195 A:{SYNTHETIC, Splitter:B:117:0x0195} */
    /* JADX WARNING: Removed duplicated region for block: B:125:0x01ab  */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x01a4  */
    /* JADX WARNING: Removed duplicated region for block: B:117:0x0195 A:{SYNTHETIC, Splitter:B:117:0x0195} */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x01a4  */
    /* JADX WARNING: Removed duplicated region for block: B:125:0x01ab  */
    /* JADX WARNING: Removed duplicated region for block: B:117:0x0195 A:{SYNTHETIC, Splitter:B:117:0x0195} */
    /* JADX WARNING: Removed duplicated region for block: B:125:0x01ab  */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x01a4  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x0106  */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x010d  */
    /* JADX WARNING: Removed duplicated region for block: B:122:0x01a0  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x0136 A:{SYNTHETIC, Splitter:B:71:0x0136} */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x01a4  */
    /* JADX WARNING: Removed duplicated region for block: B:125:0x01ab  */
    /* JADX WARNING: Missing exception handler attribute for start block: B:44:0x00e3 */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    public java.lang.String downloadUrlContent(android.os.AsyncTask r21, java.lang.String r22, java.util.HashMap<java.lang.String, java.lang.String> r23, boolean r24) {
        /*
        r20 = this;
        r0 = "ISO-8859-1,utf-8;q=0.7,*;q=0.7";
        r1 = "Accept-Charset";
        r2 = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
        r3 = "Accept";
        r4 = "en-us,en;q=0.5";
        r5 = "Accept-Language";
        r6 = "Mozilla/5.0 (X11; Linux x86_64; rv:10.0) Gecko/20150101 Firefox/47.0 (Chrome)";
        r7 = "User-Agent";
        r8 = 1;
        r11 = new java.net.URL;	 Catch:{ Throwable -> 0x0100 }
        r12 = r22;
        r11.<init>(r12);	 Catch:{ Throwable -> 0x0100 }
        r12 = r11.openConnection();	 Catch:{ Throwable -> 0x0100 }
        r12.addRequestProperty(r7, r6);	 Catch:{ Throwable -> 0x00fe }
        r13 = "gzip, deflate";
        r14 = "Accept-Encoding";
        if (r24 == 0) goto L_0x0028;
    L_0x0025:
        r12.addRequestProperty(r14, r13);	 Catch:{ Throwable -> 0x00fe }
    L_0x0028:
        r12.addRequestProperty(r5, r4);	 Catch:{ Throwable -> 0x00fe }
        r12.addRequestProperty(r3, r2);	 Catch:{ Throwable -> 0x00fe }
        r12.addRequestProperty(r1, r0);	 Catch:{ Throwable -> 0x00fe }
        if (r23 == 0) goto L_0x005b;
    L_0x0033:
        r15 = r23.entrySet();	 Catch:{ Throwable -> 0x00fe }
        r15 = r15.iterator();	 Catch:{ Throwable -> 0x00fe }
    L_0x003b:
        r16 = r15.hasNext();	 Catch:{ Throwable -> 0x00fe }
        if (r16 == 0) goto L_0x005b;
    L_0x0041:
        r16 = r15.next();	 Catch:{ Throwable -> 0x00fe }
        r16 = (java.util.Map.Entry) r16;	 Catch:{ Throwable -> 0x00fe }
        r17 = r16.getKey();	 Catch:{ Throwable -> 0x00fe }
        r9 = r17;
        r9 = (java.lang.String) r9;	 Catch:{ Throwable -> 0x00fe }
        r16 = r16.getValue();	 Catch:{ Throwable -> 0x00fe }
        r10 = r16;
        r10 = (java.lang.String) r10;	 Catch:{ Throwable -> 0x00fe }
        r12.addRequestProperty(r9, r10);	 Catch:{ Throwable -> 0x00fe }
        goto L_0x003b;
    L_0x005b:
        r9 = 5000; // 0x1388 float:7.006E-42 double:2.4703E-320;
        r12.setConnectTimeout(r9);	 Catch:{ Throwable -> 0x00fe }
        r12.setReadTimeout(r9);	 Catch:{ Throwable -> 0x00fe }
        r9 = r12 instanceof java.net.HttpURLConnection;	 Catch:{ Throwable -> 0x00fe }
        if (r9 == 0) goto L_0x00d3;
    L_0x0067:
        r9 = r12;
        r9 = (java.net.HttpURLConnection) r9;	 Catch:{ Throwable -> 0x00fe }
        r9.setInstanceFollowRedirects(r8);	 Catch:{ Throwable -> 0x00fe }
        r10 = r9.getResponseCode();	 Catch:{ Throwable -> 0x00fe }
        r15 = 302; // 0x12e float:4.23E-43 double:1.49E-321;
        if (r10 == r15) goto L_0x007d;
    L_0x0075:
        r15 = 301; // 0x12d float:4.22E-43 double:1.487E-321;
        if (r10 == r15) goto L_0x007d;
    L_0x0079:
        r15 = 303; // 0x12f float:4.25E-43 double:1.497E-321;
        if (r10 != r15) goto L_0x00d3;
    L_0x007d:
        r10 = "Location";
        r10 = r9.getHeaderField(r10);	 Catch:{ Throwable -> 0x00fe }
        r11 = "Set-Cookie";
        r9 = r9.getHeaderField(r11);	 Catch:{ Throwable -> 0x00fe }
        r11 = new java.net.URL;	 Catch:{ Throwable -> 0x00fe }
        r11.<init>(r10);	 Catch:{ Throwable -> 0x00fe }
        r10 = r11.openConnection();	 Catch:{ Throwable -> 0x00fe }
        r12 = "Cookie";
        r10.setRequestProperty(r12, r9);	 Catch:{ Throwable -> 0x00d0 }
        r10.addRequestProperty(r7, r6);	 Catch:{ Throwable -> 0x00d0 }
        if (r24 == 0) goto L_0x009f;
    L_0x009c:
        r10.addRequestProperty(r14, r13);	 Catch:{ Throwable -> 0x00d0 }
    L_0x009f:
        r10.addRequestProperty(r5, r4);	 Catch:{ Throwable -> 0x00d0 }
        r10.addRequestProperty(r3, r2);	 Catch:{ Throwable -> 0x00d0 }
        r10.addRequestProperty(r1, r0);	 Catch:{ Throwable -> 0x00d0 }
        if (r23 == 0) goto L_0x00ce;
    L_0x00aa:
        r0 = r23.entrySet();	 Catch:{ Throwable -> 0x00d0 }
        r0 = r0.iterator();	 Catch:{ Throwable -> 0x00d0 }
    L_0x00b2:
        r1 = r0.hasNext();	 Catch:{ Throwable -> 0x00d0 }
        if (r1 == 0) goto L_0x00ce;
    L_0x00b8:
        r1 = r0.next();	 Catch:{ Throwable -> 0x00d0 }
        r1 = (java.util.Map.Entry) r1;	 Catch:{ Throwable -> 0x00d0 }
        r2 = r1.getKey();	 Catch:{ Throwable -> 0x00d0 }
        r2 = (java.lang.String) r2;	 Catch:{ Throwable -> 0x00d0 }
        r1 = r1.getValue();	 Catch:{ Throwable -> 0x00d0 }
        r1 = (java.lang.String) r1;	 Catch:{ Throwable -> 0x00d0 }
        r10.addRequestProperty(r2, r1);	 Catch:{ Throwable -> 0x00d0 }
        goto L_0x00b2;
    L_0x00ce:
        r9 = r10;
        goto L_0x00d4;
    L_0x00d0:
        r0 = move-exception;
        r12 = r10;
        goto L_0x0102;
    L_0x00d3:
        r9 = r12;
    L_0x00d4:
        r9.connect();	 Catch:{ Throwable -> 0x00fb }
        if (r24 == 0) goto L_0x00ef;
    L_0x00d9:
        r0 = new java.util.zip.GZIPInputStream;	 Catch:{ Exception -> 0x00e3 }
        r1 = r9.getInputStream();	 Catch:{ Exception -> 0x00e3 }
        r0.<init>(r1);	 Catch:{ Exception -> 0x00e3 }
        goto L_0x00f3;
    L_0x00e3:
        r9 = r11.openConnection();	 Catch:{ Throwable -> 0x00fb }
        r9.connect();	 Catch:{ Throwable -> 0x00fb }
        r0 = r9.getInputStream();	 Catch:{ Throwable -> 0x00fb }
        goto L_0x00f3;
    L_0x00ef:
        r0 = r9.getInputStream();	 Catch:{ Throwable -> 0x00fb }
    L_0x00f3:
        r19 = r9;
        r9 = r0;
        r0 = r19;
        r12 = r0;
        r1 = 1;
        goto L_0x0134;
    L_0x00fb:
        r0 = move-exception;
        r12 = r9;
        goto L_0x0102;
    L_0x00fe:
        r0 = move-exception;
        goto L_0x0102;
    L_0x0100:
        r0 = move-exception;
        r12 = 0;
    L_0x0102:
        r1 = r0 instanceof java.net.SocketTimeoutException;
        if (r1 == 0) goto L_0x010d;
    L_0x0106:
        r1 = org.telegram.messenger.ApplicationLoader.isNetworkOnline();
        if (r1 == 0) goto L_0x012f;
    L_0x010c:
        goto L_0x0111;
    L_0x010d:
        r1 = r0 instanceof java.net.UnknownHostException;
        if (r1 == 0) goto L_0x0113;
    L_0x0111:
        r1 = 0;
        goto L_0x0130;
    L_0x0113:
        r1 = r0 instanceof java.net.SocketException;
        if (r1 == 0) goto L_0x012a;
    L_0x0117:
        r1 = r0.getMessage();
        if (r1 == 0) goto L_0x012f;
    L_0x011d:
        r1 = r0.getMessage();
        r2 = "ECONNRESET";
        r1 = r1.contains(r2);
        if (r1 == 0) goto L_0x012f;
    L_0x0129:
        goto L_0x0111;
    L_0x012a:
        r1 = r0 instanceof java.io.FileNotFoundException;
        if (r1 == 0) goto L_0x012f;
    L_0x012e:
        goto L_0x0111;
    L_0x012f:
        r1 = 1;
    L_0x0130:
        org.telegram.messenger.FileLog.e(r0);
        r9 = 0;
    L_0x0134:
        if (r1 == 0) goto L_0x01a0;
    L_0x0136:
        r0 = r12 instanceof java.net.HttpURLConnection;	 Catch:{ Exception -> 0x0147 }
        if (r0 == 0) goto L_0x014b;
    L_0x013a:
        r12 = (java.net.HttpURLConnection) r12;	 Catch:{ Exception -> 0x0147 }
        r0 = r12.getResponseCode();	 Catch:{ Exception -> 0x0147 }
        r1 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
        if (r0 == r1) goto L_0x014b;
    L_0x0144:
        r1 = 202; // 0xca float:2.83E-43 double:1.0E-321;
        goto L_0x014b;
    L_0x0147:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x014b:
        if (r9 == 0) goto L_0x0190;
    L_0x014d:
        r0 = 32768; // 0x8000 float:4.5918E-41 double:1.61895E-319;
        r0 = new byte[r0];	 Catch:{ Throwable -> 0x0189 }
        r1 = 0;
    L_0x0153:
        r2 = r21.isCancelled();	 Catch:{ Throwable -> 0x0186 }
        if (r2 == 0) goto L_0x015b;
    L_0x0159:
        r5 = 0;
        goto L_0x0182;
    L_0x015b:
        r2 = r9.read(r0);	 Catch:{ Exception -> 0x017d }
        if (r2 <= 0) goto L_0x0177;
    L_0x0161:
        if (r1 != 0) goto L_0x0169;
    L_0x0163:
        r3 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x017d }
        r3.<init>();	 Catch:{ Exception -> 0x017d }
        r1 = r3;
    L_0x0169:
        r3 = new java.lang.String;	 Catch:{ Exception -> 0x017d }
        r4 = "UTF-8";
        r5 = 0;
        r3.<init>(r0, r5, r2, r4);	 Catch:{ Exception -> 0x0175 }
        r1.append(r3);	 Catch:{ Exception -> 0x0175 }
        goto L_0x0153;
    L_0x0175:
        r0 = move-exception;
        goto L_0x017f;
    L_0x0177:
        r5 = 0;
        r0 = -1;
        if (r2 != r0) goto L_0x0182;
    L_0x017b:
        r5 = 1;
        goto L_0x0182;
    L_0x017d:
        r0 = move-exception;
        r5 = 0;
    L_0x017f:
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ Throwable -> 0x0184 }
    L_0x0182:
        r10 = r5;
        goto L_0x0193;
    L_0x0184:
        r0 = move-exception;
        goto L_0x018c;
    L_0x0186:
        r0 = move-exception;
        r5 = 0;
        goto L_0x018c;
    L_0x0189:
        r0 = move-exception;
        r5 = 0;
        r1 = 0;
    L_0x018c:
        org.telegram.messenger.FileLog.e(r0);
        goto L_0x0192;
    L_0x0190:
        r5 = 0;
        r1 = 0;
    L_0x0192:
        r10 = 0;
    L_0x0193:
        if (r9 == 0) goto L_0x019e;
    L_0x0195:
        r9.close();	 Catch:{ Throwable -> 0x0199 }
        goto L_0x019e;
    L_0x0199:
        r0 = move-exception;
        r2 = r0;
        org.telegram.messenger.FileLog.e(r2);
    L_0x019e:
        r5 = r10;
        goto L_0x01a2;
    L_0x01a0:
        r5 = 0;
        r1 = 0;
    L_0x01a2:
        if (r5 == 0) goto L_0x01ab;
    L_0x01a4:
        r9 = r1.toString();
        r18 = r9;
        goto L_0x01ad;
    L_0x01ab:
        r18 = 0;
    L_0x01ad:
        return r18;
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
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
            }

            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
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
                            AndroidUtilities.runOnUIThread(new -$$Lambda$WebPlayerView$2$1$XktWEKlh2pqd2EX-7__wMiImvaQ(this));
                            WebPlayerView.this.waitingForFirstTextureUpload = 0;
                            return true;
                        }

                        public /* synthetic */ void lambda$onPreDraw$0$WebPlayerView$2$1() {
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
                    } catch (Throwable th) {
                        if (WebPlayerView.this.currentBitmap != null) {
                            WebPlayerView.this.currentBitmap.recycle();
                            WebPlayerView.this.currentBitmap = null;
                        }
                        FileLog.e(th);
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
                WebPlayerView webPlayerView = WebPlayerView.this;
                webPlayerView.changedTextureView = webPlayerView.delegate.onSwitchInlineMode(WebPlayerView.this.controlsView, WebPlayerView.this.isInline, WebPlayerView.this.aspectRatioFrameLayout.getAspectRatio(), WebPlayerView.this.aspectRatioFrameLayout.getVideoRotation(), WebPlayerView.this.allowInlineAnimation);
                WebPlayerView.this.changedTextureView.setVisibility(4);
                viewGroup = (ViewGroup) WebPlayerView.this.textureView.getParent();
                if (viewGroup != null) {
                    viewGroup.removeView(WebPlayerView.this.textureView);
                }
                WebPlayerView.this.controlsView.show(false, false);
            }
        };
        setWillNotDraw(false);
        this.delegate = webPlayerViewDelegate;
        this.backgroundPaint.setColor(-16777216);
        this.aspectRatioFrameLayout = new AspectRatioFrameLayout(context) {
            /* Access modifiers changed, original: protected */
            public void onMeasure(int i, int i2) {
                super.onMeasure(i, i2);
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
        this.webView.addJavascriptInterface(new JavaScriptInterface(new -$$Lambda$WebPlayerView$OTCqcKUzHnpxut9IWnkU_zTMUYs(this)), this.interfaceName);
        WebSettings settings = this.webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDefaultTextEncodingName("utf-8");
        this.textureViewContainer = this.delegate.getTextureViewContainer();
        this.textureView = new TextureView(context);
        this.textureView.setPivotX(0.0f);
        this.textureView.setPivotY(0.0f);
        ViewGroup viewGroup = this.textureViewContainer;
        if (viewGroup != null) {
            viewGroup.addView(this.textureView);
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
        viewGroup = this.textureViewContainer;
        if (viewGroup != null) {
            viewGroup.addView(this.controlsView);
        } else {
            addView(this.controlsView, LayoutHelper.createFrame(-1, -1.0f));
        }
        this.progressView = new RadialProgressView(context);
        this.progressView.setProgressColor(-1);
        addView(this.progressView, LayoutHelper.createFrame(48, 48, 17));
        this.fullscreenButton = new ImageView(context);
        this.fullscreenButton.setScaleType(ScaleType.CENTER);
        this.controlsView.addView(this.fullscreenButton, LayoutHelper.createFrame(56, 56.0f, 85, 0.0f, 0.0f, 0.0f, 5.0f));
        this.fullscreenButton.setOnClickListener(new -$$Lambda$WebPlayerView$W2P4sWOYF2snToxNUtlCSP61A2U(this));
        this.playButton = new ImageView(context);
        this.playButton.setScaleType(ScaleType.CENTER);
        this.controlsView.addView(this.playButton, LayoutHelper.createFrame(48, 48, 17));
        this.playButton.setOnClickListener(new -$$Lambda$WebPlayerView$8RPE5WMQJ4Qql9XUXKDSHuRZ8yA(this));
        if (z) {
            this.inlineButton = new ImageView(context);
            this.inlineButton.setScaleType(ScaleType.CENTER);
            this.controlsView.addView(this.inlineButton, LayoutHelper.createFrame(56, 48, 53));
            this.inlineButton.setOnClickListener(new -$$Lambda$WebPlayerView$FgDS8XBnRLuQdgwn4r4TJJnIjOo(this));
        }
        if (z2) {
            this.shareButton = new ImageView(context);
            this.shareButton.setScaleType(ScaleType.CENTER);
            this.shareButton.setImageResource(NUM);
            this.controlsView.addView(this.shareButton, LayoutHelper.createFrame(56, 48, 53));
            this.shareButton.setOnClickListener(new -$$Lambda$WebPlayerView$tu1I4PMipEbSXLy2AXDs0pmTjdo(this));
        }
        updatePlayButton();
        updateFullscreenButton();
        updateInlineButton();
        updateShareButton();
    }

    public /* synthetic */ void lambda$new$0$WebPlayerView(String str) {
        AsyncTask asyncTask = this.currentTask;
        if (asyncTask != null && !asyncTask.isCancelled()) {
            asyncTask = this.currentTask;
            if (asyncTask instanceof YoutubeVideoTask) {
                ((YoutubeVideoTask) asyncTask).onInterfaceResult(str);
            }
        }
    }

    public /* synthetic */ void lambda$new$1$WebPlayerView(View view) {
        if (this.initied && !this.changingTextureView && !this.switchingInlineMode && this.firstFrameRendered) {
            this.inFullscreen ^= 1;
            updateFullscreenState(true);
        }
    }

    public /* synthetic */ void lambda$new$2$WebPlayerView(View view) {
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

    public /* synthetic */ void lambda$new$3$WebPlayerView(View view) {
        if (this.textureView != null && this.delegate.checkInlinePermissions() && !this.changingTextureView && !this.switchingInlineMode && this.firstFrameRendered) {
            this.switchingInlineMode = true;
            if (this.isInline) {
                ViewGroup viewGroup = (ViewGroup) this.aspectRatioFrameLayout.getParent();
                if (viewGroup != this) {
                    if (viewGroup != null) {
                        viewGroup.removeView(this.aspectRatioFrameLayout);
                    }
                    addView(this.aspectRatioFrameLayout, 0, LayoutHelper.createFrame(-1, -1, 17));
                    this.aspectRatioFrameLayout.measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth(), NUM), MeasureSpec.makeMeasureSpec(getMeasuredHeight() - AndroidUtilities.dp(10.0f), NUM));
                }
                Bitmap bitmap = this.currentBitmap;
                if (bitmap != null) {
                    bitmap.recycle();
                    this.currentBitmap = null;
                }
                this.changingTextureView = true;
                this.isInline = false;
                updatePlayButton();
                updateShareButton();
                updateFullscreenButton();
                updateInlineButton();
                this.textureView.setVisibility(4);
                viewGroup = this.textureViewContainer;
                if (viewGroup != null) {
                    viewGroup.addView(this.textureView);
                } else {
                    this.aspectRatioFrameLayout.addView(this.textureView);
                }
                viewGroup = (ViewGroup) this.controlsView.getParent();
                if (viewGroup != this) {
                    if (viewGroup != null) {
                        viewGroup.removeView(this.controlsView);
                    }
                    viewGroup = this.textureViewContainer;
                    if (viewGroup != null) {
                        viewGroup.addView(this.controlsView);
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

    public /* synthetic */ void lambda$new$4$WebPlayerView(View view) {
        WebPlayerViewDelegate webPlayerViewDelegate = this.delegate;
        if (webPlayerViewDelegate != null) {
            webPlayerViewDelegate.onSharePressed();
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
            } catch (Throwable th) {
                Bitmap bitmap = this.currentBitmap;
                if (bitmap != null) {
                    bitmap.recycle();
                    this.currentBitmap = null;
                }
                FileLog.e(th);
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
            if (this.videoPlayer.getDuration() != -9223372036854775807L) {
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

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) (getMeasuredHeight() - AndroidUtilities.dp(10.0f)), this.backgroundPaint);
    }

    public void onError(Exception exception) {
        FileLog.e((Throwable) exception);
        onInitFailed();
    }

    public void onVideoSizeChanged(int i, int i2, int i3, float f) {
        if (this.aspectRatioFrameLayout != null) {
            if (!(i3 == 90 || i3 == 270)) {
                int i4 = i2;
                i2 = i;
                i = i4;
            }
            float f2 = i == 0 ? 1.0f : (((float) i2) * f) / ((float) i);
            this.aspectRatioFrameLayout.setAspectRatio(f2, i3);
            if (this.inFullscreen) {
                this.delegate.onVideoSizeChanged(f2, i3);
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
            ImageView imageView = this.textureImageView;
            if (imageView != null) {
                imageView.setVisibility(4);
                this.textureImageView.setImageDrawable(null);
                Bitmap bitmap = this.currentBitmap;
                if (bitmap != null) {
                    bitmap.recycle();
                    this.currentBitmap = null;
                }
            }
            this.switchingInlineMode = false;
            this.delegate.onSwitchInlineMode(this.controlsView, false, this.aspectRatioFrameLayout.getAspectRatio(), this.aspectRatioFrameLayout.getVideoRotation(), this.allowInlineAnimation);
            this.waitingForFirstTextureUpload = 0;
        }
    }

    /* Access modifiers changed, original: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        i3 -= i;
        int measuredWidth = (i3 - this.aspectRatioFrameLayout.getMeasuredWidth()) / 2;
        i4 -= i2;
        i2 = ((i4 - AndroidUtilities.dp(10.0f)) - this.aspectRatioFrameLayout.getMeasuredHeight()) / 2;
        AspectRatioFrameLayout aspectRatioFrameLayout = this.aspectRatioFrameLayout;
        aspectRatioFrameLayout.layout(measuredWidth, i2, aspectRatioFrameLayout.getMeasuredWidth() + measuredWidth, this.aspectRatioFrameLayout.getMeasuredHeight() + i2);
        if (this.controlsView.getParent() == this) {
            ControlsView controlsView = this.controlsView;
            controlsView.layout(0, 0, controlsView.getMeasuredWidth(), this.controlsView.getMeasuredHeight());
        }
        i3 = (i3 - this.progressView.getMeasuredWidth()) / 2;
        i4 = (i4 - this.progressView.getMeasuredHeight()) / 2;
        RadialProgressView radialProgressView = this.progressView;
        radialProgressView.layout(i3, i4, radialProgressView.getMeasuredWidth() + i3, this.progressView.getMeasuredHeight() + i4);
        this.controlsView.imageReceiver.setImageCoords(0, 0, getMeasuredWidth(), getMeasuredHeight() - AndroidUtilities.dp(10.0f));
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
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

    public void onAudioFocusChange(int i) {
        if (i == -1) {
            if (this.videoPlayer.isPlaying()) {
                this.videoPlayer.pause();
                updatePlayButton();
            }
            this.hasAudioFocus = false;
            this.audioFocus = 0;
        } else if (i == 1) {
            this.audioFocus = 2;
            if (this.resumeAudioOnFocusGain) {
                this.resumeAudioOnFocusGain = false;
                this.videoPlayer.play();
            }
        } else if (i == -3) {
            this.audioFocus = 1;
        } else if (i == -2) {
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
        } else {
            this.fullscreenButton.setImageResource(NUM);
            this.fullscreenButton.setLayoutParams(LayoutHelper.createFrame(56, 56.0f, 85, 0.0f, 0.0f, 0.0f, 5.0f));
        }
    }

    private void updateShareButton() {
        ImageView imageView = this.shareButton;
        if (imageView != null) {
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
        ImageView imageView = this.inlineButton;
        if (imageView != null) {
            imageView.setImageResource(this.isInline ? NUM : NUM);
            this.inlineButton.setVisibility(this.videoPlayer.isPlayerPrepared() ? 0 : 8);
            if (this.isInline) {
                this.inlineButton.setLayoutParams(LayoutHelper.createFrame(40, 40, 53));
            } else {
                this.inlineButton.setLayoutParams(LayoutHelper.createFrame(56, 50, 53));
            }
        }
    }

    private void preparePlayer() {
        String str = this.playVideoUrl;
        if (str != null) {
            if (str == null || this.playAudioUrl == null) {
                this.videoPlayer.preparePlayer(Uri.parse(this.playVideoUrl), this.playVideoType);
            } else {
                this.videoPlayer.preparePlayerLoop(Uri.parse(str), this.playVideoType, Uri.parse(this.playAudioUrl), this.playAudioType);
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
            int i = this.seekToTime;
            if (i != -1) {
                this.videoPlayer.seekTo((long) (i * 1000));
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
            ViewGroup viewGroup = this.textureViewContainer;
            if (viewGroup == null) {
                this.changingTextureView = true;
                if (!this.inFullscreen) {
                    if (viewGroup != null) {
                        viewGroup.addView(this.textureView);
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
                    viewGroup = (ViewGroup) this.controlsView.getParent();
                    if (viewGroup != this) {
                        if (viewGroup != null) {
                            viewGroup.removeView(this.controlsView);
                        }
                        viewGroup = this.textureViewContainer;
                        if (viewGroup != null) {
                            viewGroup.addView(this.controlsView);
                        } else {
                            addView(this.controlsView, 1);
                        }
                    }
                }
                this.changedTextureView = this.delegate.onSwitchToFullscreen(this.controlsView, this.inFullscreen, this.aspectRatioFrameLayout.getAspectRatio(), this.aspectRatioFrameLayout.getVideoRotation(), z);
                this.changedTextureView.setVisibility(4);
                if (this.inFullscreen && this.changedTextureView != null) {
                    ViewGroup viewGroup2 = (ViewGroup) this.textureView.getParent();
                    if (viewGroup2 != null) {
                        viewGroup2.removeView(this.textureView);
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

    /* JADX WARNING: Removed duplicated region for block: B:91:0x0147  */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x0182  */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x015a  */
    /* JADX WARNING: Removed duplicated region for block: B:100:0x0188  */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x0196  */
    /* JADX WARNING: Removed duplicated region for block: B:109:0x01b4  */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x019b  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x0101 A:{SYNTHETIC, Splitter:B:76:0x0101} */
    /* JADX WARNING: Removed duplicated region for block: B:91:0x0147  */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x015a  */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x0182  */
    /* JADX WARNING: Removed duplicated region for block: B:100:0x0188  */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x0196  */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x019b  */
    /* JADX WARNING: Removed duplicated region for block: B:109:0x01b4  */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x024d A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x00e2 A:{SYNTHETIC, Splitter:B:64:0x00e2} */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x0101 A:{SYNTHETIC, Splitter:B:76:0x0101} */
    /* JADX WARNING: Removed duplicated region for block: B:91:0x0147  */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x0182  */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x015a  */
    /* JADX WARNING: Removed duplicated region for block: B:100:0x0188  */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x0196  */
    /* JADX WARNING: Removed duplicated region for block: B:109:0x01b4  */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x019b  */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x024d A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x00c3 A:{SYNTHETIC, Splitter:B:52:0x00c3} */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x00e2 A:{SYNTHETIC, Splitter:B:64:0x00e2} */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x0101 A:{SYNTHETIC, Splitter:B:76:0x0101} */
    /* JADX WARNING: Removed duplicated region for block: B:91:0x0147  */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x015a  */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x0182  */
    /* JADX WARNING: Removed duplicated region for block: B:100:0x0188  */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x0196  */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x019b  */
    /* JADX WARNING: Removed duplicated region for block: B:109:0x01b4  */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x024d A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x00a4 A:{SYNTHETIC, Splitter:B:40:0x00a4} */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x00c3 A:{SYNTHETIC, Splitter:B:52:0x00c3} */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x00e2 A:{SYNTHETIC, Splitter:B:64:0x00e2} */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x0101 A:{SYNTHETIC, Splitter:B:76:0x0101} */
    /* JADX WARNING: Removed duplicated region for block: B:91:0x0147  */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x0182  */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x015a  */
    /* JADX WARNING: Removed duplicated region for block: B:100:0x0188  */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x0196  */
    /* JADX WARNING: Removed duplicated region for block: B:109:0x01b4  */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x019b  */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x024d A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:91:0x0147  */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x015a  */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x0182  */
    /* JADX WARNING: Removed duplicated region for block: B:100:0x0188  */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x0196  */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x019b  */
    /* JADX WARNING: Removed duplicated region for block: B:109:0x01b4  */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x024d A:{SKIP} */
    public boolean loadVideo(java.lang.String r26, org.telegram.tgnet.TLRPC.Photo r27, java.lang.Object r28, java.lang.String r29, boolean r30) {
        /*
        r25 = this;
        r1 = r25;
        r2 = r26;
        r3 = r27;
        r0 = "m";
        r4 = -1;
        r1.seekToTime = r4;
        r4 = 3;
        r5 = 0;
        r6 = 1;
        r7 = 0;
        if (r2 == 0) goto L_0x0127;
    L_0x0011:
        r8 = ".mp4";
        r8 = r2.endsWith(r8);
        if (r8 == 0) goto L_0x001d;
    L_0x0019:
        r0 = r2;
        r8 = r7;
        goto L_0x0129;
    L_0x001d:
        if (r29 == 0) goto L_0x0066;
    L_0x001f:
        r8 = android.net.Uri.parse(r29);	 Catch:{ Exception -> 0x0062 }
        r9 = "t";
        r9 = r8.getQueryParameter(r9);	 Catch:{ Exception -> 0x0062 }
        if (r9 != 0) goto L_0x0031;
    L_0x002b:
        r9 = "time_continue";
        r9 = r8.getQueryParameter(r9);	 Catch:{ Exception -> 0x0062 }
    L_0x0031:
        if (r9 == 0) goto L_0x0066;
    L_0x0033:
        r8 = r9.contains(r0);	 Catch:{ Exception -> 0x0062 }
        if (r8 == 0) goto L_0x0057;
    L_0x0039:
        r0 = r9.split(r0);	 Catch:{ Exception -> 0x0062 }
        r8 = r0[r5];	 Catch:{ Exception -> 0x0062 }
        r8 = org.telegram.messenger.Utilities.parseInt(r8);	 Catch:{ Exception -> 0x0062 }
        r8 = r8.intValue();	 Catch:{ Exception -> 0x0062 }
        r8 = r8 * 60;
        r0 = r0[r6];	 Catch:{ Exception -> 0x0062 }
        r0 = org.telegram.messenger.Utilities.parseInt(r0);	 Catch:{ Exception -> 0x0062 }
        r0 = r0.intValue();	 Catch:{ Exception -> 0x0062 }
        r8 = r8 + r0;
        r1.seekToTime = r8;	 Catch:{ Exception -> 0x0062 }
        goto L_0x0066;
    L_0x0057:
        r0 = org.telegram.messenger.Utilities.parseInt(r9);	 Catch:{ Exception -> 0x0062 }
        r0 = r0.intValue();	 Catch:{ Exception -> 0x0062 }
        r1.seekToTime = r0;	 Catch:{ Exception -> 0x0062 }
        goto L_0x0066;
    L_0x0062:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ Exception -> 0x007e }
    L_0x0066:
        r0 = youtubeIdRegex;	 Catch:{ Exception -> 0x007e }
        r0 = r0.matcher(r2);	 Catch:{ Exception -> 0x007e }
        r8 = r0.find();	 Catch:{ Exception -> 0x007e }
        if (r8 == 0) goto L_0x0077;
    L_0x0072:
        r0 = r0.group(r6);	 Catch:{ Exception -> 0x007e }
        goto L_0x0078;
    L_0x0077:
        r0 = r7;
    L_0x0078:
        if (r0 == 0) goto L_0x007b;
    L_0x007a:
        goto L_0x007c;
    L_0x007b:
        r0 = r7;
    L_0x007c:
        r8 = r0;
        goto L_0x0083;
    L_0x007e:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
        r8 = r7;
    L_0x0083:
        if (r8 != 0) goto L_0x00a1;
    L_0x0085:
        r0 = vimeoIdRegex;	 Catch:{ Exception -> 0x009d }
        r0 = r0.matcher(r2);	 Catch:{ Exception -> 0x009d }
        r9 = r0.find();	 Catch:{ Exception -> 0x009d }
        if (r9 == 0) goto L_0x0096;
    L_0x0091:
        r0 = r0.group(r4);	 Catch:{ Exception -> 0x009d }
        goto L_0x0097;
    L_0x0096:
        r0 = r7;
    L_0x0097:
        if (r0 == 0) goto L_0x009a;
    L_0x0099:
        goto L_0x009b;
    L_0x009a:
        r0 = r7;
    L_0x009b:
        r9 = r0;
        goto L_0x00a2;
    L_0x009d:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x00a1:
        r9 = r7;
    L_0x00a2:
        if (r9 != 0) goto L_0x00c0;
    L_0x00a4:
        r0 = aparatIdRegex;	 Catch:{ Exception -> 0x00bc }
        r0 = r0.matcher(r2);	 Catch:{ Exception -> 0x00bc }
        r10 = r0.find();	 Catch:{ Exception -> 0x00bc }
        if (r10 == 0) goto L_0x00b5;
    L_0x00b0:
        r0 = r0.group(r6);	 Catch:{ Exception -> 0x00bc }
        goto L_0x00b6;
    L_0x00b5:
        r0 = r7;
    L_0x00b6:
        if (r0 == 0) goto L_0x00b9;
    L_0x00b8:
        goto L_0x00ba;
    L_0x00b9:
        r0 = r7;
    L_0x00ba:
        r10 = r0;
        goto L_0x00c1;
    L_0x00bc:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x00c0:
        r10 = r7;
    L_0x00c1:
        if (r10 != 0) goto L_0x00df;
    L_0x00c3:
        r0 = twitchClipIdRegex;	 Catch:{ Exception -> 0x00db }
        r0 = r0.matcher(r2);	 Catch:{ Exception -> 0x00db }
        r11 = r0.find();	 Catch:{ Exception -> 0x00db }
        if (r11 == 0) goto L_0x00d4;
    L_0x00cf:
        r0 = r0.group(r6);	 Catch:{ Exception -> 0x00db }
        goto L_0x00d5;
    L_0x00d4:
        r0 = r7;
    L_0x00d5:
        if (r0 == 0) goto L_0x00d8;
    L_0x00d7:
        goto L_0x00d9;
    L_0x00d8:
        r0 = r7;
    L_0x00d9:
        r11 = r0;
        goto L_0x00e0;
    L_0x00db:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x00df:
        r11 = r7;
    L_0x00e0:
        if (r11 != 0) goto L_0x00fe;
    L_0x00e2:
        r0 = twitchStreamIdRegex;	 Catch:{ Exception -> 0x00fa }
        r0 = r0.matcher(r2);	 Catch:{ Exception -> 0x00fa }
        r12 = r0.find();	 Catch:{ Exception -> 0x00fa }
        if (r12 == 0) goto L_0x00f3;
    L_0x00ee:
        r0 = r0.group(r6);	 Catch:{ Exception -> 0x00fa }
        goto L_0x00f4;
    L_0x00f3:
        r0 = r7;
    L_0x00f4:
        if (r0 == 0) goto L_0x00f7;
    L_0x00f6:
        goto L_0x00f8;
    L_0x00f7:
        r0 = r7;
    L_0x00f8:
        r12 = r0;
        goto L_0x00ff;
    L_0x00fa:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x00fe:
        r12 = r7;
    L_0x00ff:
        if (r12 != 0) goto L_0x0121;
    L_0x0101:
        r0 = coubIdRegex;	 Catch:{ Exception -> 0x011d }
        r0 = r0.matcher(r2);	 Catch:{ Exception -> 0x011d }
        r13 = r0.find();	 Catch:{ Exception -> 0x011d }
        if (r13 == 0) goto L_0x0112;
    L_0x010d:
        r0 = r0.group(r6);	 Catch:{ Exception -> 0x011d }
        goto L_0x0113;
    L_0x0112:
        r0 = r7;
    L_0x0113:
        if (r0 == 0) goto L_0x0116;
    L_0x0115:
        goto L_0x0117;
    L_0x0116:
        r0 = r7;
    L_0x0117:
        r13 = r12;
        r12 = r11;
        r11 = r10;
        r10 = r0;
        r0 = r7;
        goto L_0x012e;
    L_0x011d:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x0121:
        r0 = r7;
        r13 = r12;
        r12 = r11;
        r11 = r10;
        r10 = r0;
        goto L_0x012e;
    L_0x0127:
        r0 = r7;
        r8 = r0;
    L_0x0129:
        r9 = r8;
        r10 = r9;
        r11 = r10;
        r12 = r11;
        r13 = r12;
    L_0x012e:
        r1.initied = r5;
        r1.isCompleted = r5;
        r14 = r30;
        r1.isAutoplay = r14;
        r1.playVideoUrl = r7;
        r1.playAudioUrl = r7;
        r25.destroy();
        r1.firstFrameRendered = r5;
        r14 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r1.currentAlpha = r14;
        r14 = r1.currentTask;
        if (r14 == 0) goto L_0x014c;
    L_0x0147:
        r14.cancel(r6);
        r1.currentTask = r7;
    L_0x014c:
        r25.updateFullscreenButton();
        r25.updateShareButton();
        r25.updateInlineButton();
        r25.updatePlayButton();
        if (r3 == 0) goto L_0x0182;
    L_0x015a:
        r14 = r3.sizes;
        r15 = 80;
        r14 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r14, r15, r6);
        if (r14 == 0) goto L_0x0184;
    L_0x0164:
        r15 = r1.controlsView;
        r16 = r15.imageReceiver;
        r17 = 0;
        r18 = 0;
        r19 = org.telegram.messenger.ImageLocation.getForPhoto(r14, r3);
        r21 = 0;
        r22 = 0;
        r24 = 1;
        r20 = "80_80_b";
        r23 = r28;
        r16.setImage(r17, r18, r19, r20, r21, r22, r23, r24);
        r1.drawImage = r6;
        goto L_0x0184;
    L_0x0182:
        r1.drawImage = r5;
    L_0x0184:
        r3 = r1.progressAnimation;
        if (r3 == 0) goto L_0x018d;
    L_0x0188:
        r3.cancel();
        r1.progressAnimation = r7;
    L_0x018d:
        r1.isLoading = r6;
        r3 = r1.controlsView;
        r3.setProgress(r5);
        if (r8 == 0) goto L_0x0199;
    L_0x0196:
        r1.currentYoutubeId = r8;
        r8 = r7;
    L_0x0199:
        if (r0 == 0) goto L_0x01b4;
    L_0x019b:
        r1.initied = r6;
        r1.playVideoUrl = r0;
        r2 = "other";
        r1.playVideoType = r2;
        r2 = r1.isAutoplay;
        if (r2 == 0) goto L_0x01aa;
    L_0x01a7:
        r25.preparePlayer();
    L_0x01aa:
        r1.showProgress(r5, r5);
        r2 = r1.controlsView;
        r2.show(r6, r6);
        goto L_0x024b;
    L_0x01b4:
        r3 = 2;
        if (r8 == 0) goto L_0x01cd;
    L_0x01b7:
        r2 = new org.telegram.ui.Components.WebPlayerView$YoutubeVideoTask;
        r2.<init>(r8);
        r14 = android.os.AsyncTask.THREAD_POOL_EXECUTOR;
        r4 = new java.lang.Void[r4];
        r4[r5] = r7;
        r4[r6] = r7;
        r4[r3] = r7;
        r2.executeOnExecutor(r14, r4);
        r1.currentTask = r2;
        goto L_0x0243;
    L_0x01cd:
        if (r9 == 0) goto L_0x01e4;
    L_0x01cf:
        r2 = new org.telegram.ui.Components.WebPlayerView$VimeoVideoTask;
        r2.<init>(r9);
        r14 = android.os.AsyncTask.THREAD_POOL_EXECUTOR;
        r4 = new java.lang.Void[r4];
        r4[r5] = r7;
        r4[r6] = r7;
        r4[r3] = r7;
        r2.executeOnExecutor(r14, r4);
        r1.currentTask = r2;
        goto L_0x0243;
    L_0x01e4:
        if (r10 == 0) goto L_0x01fd;
    L_0x01e6:
        r2 = new org.telegram.ui.Components.WebPlayerView$CoubVideoTask;
        r2.<init>(r10);
        r14 = android.os.AsyncTask.THREAD_POOL_EXECUTOR;
        r4 = new java.lang.Void[r4];
        r4[r5] = r7;
        r4[r6] = r7;
        r4[r3] = r7;
        r2.executeOnExecutor(r14, r4);
        r1.currentTask = r2;
        r1.isStream = r6;
        goto L_0x0243;
    L_0x01fd:
        if (r11 == 0) goto L_0x0214;
    L_0x01ff:
        r2 = new org.telegram.ui.Components.WebPlayerView$AparatVideoTask;
        r2.<init>(r11);
        r14 = android.os.AsyncTask.THREAD_POOL_EXECUTOR;
        r4 = new java.lang.Void[r4];
        r4[r5] = r7;
        r4[r6] = r7;
        r4[r3] = r7;
        r2.executeOnExecutor(r14, r4);
        r1.currentTask = r2;
        goto L_0x0243;
    L_0x0214:
        if (r12 == 0) goto L_0x022b;
    L_0x0216:
        r14 = new org.telegram.ui.Components.WebPlayerView$TwitchClipVideoTask;
        r14.<init>(r2, r12);
        r2 = android.os.AsyncTask.THREAD_POOL_EXECUTOR;
        r4 = new java.lang.Void[r4];
        r4[r5] = r7;
        r4[r6] = r7;
        r4[r3] = r7;
        r14.executeOnExecutor(r2, r4);
        r1.currentTask = r14;
        goto L_0x0243;
    L_0x022b:
        if (r13 == 0) goto L_0x0243;
    L_0x022d:
        r14 = new org.telegram.ui.Components.WebPlayerView$TwitchStreamVideoTask;
        r14.<init>(r2, r13);
        r2 = android.os.AsyncTask.THREAD_POOL_EXECUTOR;
        r4 = new java.lang.Void[r4];
        r4[r5] = r7;
        r4[r6] = r7;
        r4[r3] = r7;
        r14.executeOnExecutor(r2, r4);
        r1.currentTask = r14;
        r1.isStream = r6;
    L_0x0243:
        r2 = r1.controlsView;
        r2.show(r5, r5);
        r1.showProgress(r6, r5);
    L_0x024b:
        if (r8 != 0) goto L_0x0262;
    L_0x024d:
        if (r9 != 0) goto L_0x0262;
    L_0x024f:
        if (r10 != 0) goto L_0x0262;
    L_0x0251:
        if (r11 != 0) goto L_0x0262;
    L_0x0253:
        if (r0 != 0) goto L_0x0262;
    L_0x0255:
        if (r12 != 0) goto L_0x0262;
    L_0x0257:
        if (r13 == 0) goto L_0x025a;
    L_0x0259:
        goto L_0x0262;
    L_0x025a:
        r0 = r1.controlsView;
        r2 = 8;
        r0.setVisibility(r2);
        return r5;
    L_0x0262:
        r0 = r1.controlsView;
        r0.setVisibility(r5);
        return r6;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.WebPlayerView.loadVideo(java.lang.String, org.telegram.tgnet.TLRPC$Photo, java.lang.Object, java.lang.String, boolean):boolean");
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
        AsyncTask asyncTask = this.currentTask;
        if (asyncTask != null) {
            asyncTask.cancel(true);
            this.currentTask = null;
        }
        this.webView.stopLoading();
    }

    private void showProgress(boolean z, boolean z2) {
        float f = 1.0f;
        if (z2) {
            AnimatorSet animatorSet = this.progressAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            this.progressAnimation = new AnimatorSet();
            animatorSet = this.progressAnimation;
            Animator[] animatorArr = new Animator[1];
            RadialProgressView radialProgressView = this.progressView;
            float[] fArr = new float[1];
            if (!z) {
                f = 0.0f;
            }
            fArr[0] = f;
            animatorArr[0] = ObjectAnimator.ofFloat(radialProgressView, "alpha", fArr);
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
        if (!z) {
            f = 0.0f;
        }
        radialProgressView2.setAlpha(f);
    }
}
