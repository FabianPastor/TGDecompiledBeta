package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.os.Build;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.core.math.MathUtils;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.GestureDetectorFixDoubleTap;
import org.telegram.ui.Components.VideoForwardDrawable;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PhotoViewer;

public class PipVideoOverlay {
    private static final FloatPropertyCompat<PipVideoOverlay> PIP_X_PROPERTY = new SimpleFloatPropertyCompat("pipX", PipVideoOverlay$$ExternalSyntheticLambda11.INSTANCE, PipVideoOverlay$$ExternalSyntheticLambda13.INSTANCE);
    private static final FloatPropertyCompat<PipVideoOverlay> PIP_Y_PROPERTY = new SimpleFloatPropertyCompat("pipY", PipVideoOverlay$$ExternalSyntheticLambda10.INSTANCE, PipVideoOverlay$$ExternalSyntheticLambda12.INSTANCE);
    @SuppressLint({"StaticFieldLeak"})
    private static PipVideoOverlay instance = new PipVideoOverlay();
    private Float aspectRatio;
    /* access modifiers changed from: private */
    public float bufferProgress;
    /* access modifiers changed from: private */
    public boolean canLongClick;
    /* access modifiers changed from: private */
    public View consumingChild;
    /* access modifiers changed from: private */
    public FrameLayout contentFrameLayout;
    /* access modifiers changed from: private */
    public ViewGroup contentView;
    /* access modifiers changed from: private */
    public ValueAnimator controlsAnimator;
    /* access modifiers changed from: private */
    public FrameLayout controlsView;
    /* access modifiers changed from: private */
    public Runnable dismissControlsCallback = new PipVideoOverlay$$ExternalSyntheticLambda9(this);
    /* access modifiers changed from: private */
    public GestureDetectorFixDoubleTap gestureDetector;
    private View innerView;
    /* access modifiers changed from: private */
    public boolean isDismissing;
    /* access modifiers changed from: private */
    public boolean isScrollDisallowed;
    /* access modifiers changed from: private */
    public boolean isScrolling;
    /* access modifiers changed from: private */
    public boolean isShowingControls;
    /* access modifiers changed from: private */
    public boolean isVideoCompleted;
    private boolean isVisible;
    /* access modifiers changed from: private */
    public boolean isWebView;
    /* access modifiers changed from: private */
    public Runnable longClickCallback = new PipVideoOverlay$$ExternalSyntheticLambda6(this);
    /* access modifiers changed from: private */
    public float[] longClickStartPoint = new float[2];
    private int mVideoHeight;
    private int mVideoWidth;
    /* access modifiers changed from: private */
    public float maxScaleFactor = 1.4f;
    /* access modifiers changed from: private */
    public float minScaleFactor = 0.75f;
    /* access modifiers changed from: private */
    public boolean onSideToDismiss;
    private EmbedBottomSheet parentSheet;
    /* access modifiers changed from: private */
    public PhotoViewer photoViewer;
    /* access modifiers changed from: private */
    public PhotoViewerWebView photoViewerWebView;
    /* access modifiers changed from: private */
    public PipConfig pipConfig;
    /* access modifiers changed from: private */
    public int pipHeight;
    /* access modifiers changed from: private */
    public int pipWidth;
    /* access modifiers changed from: private */
    public float pipX;
    /* access modifiers changed from: private */
    public SpringAnimation pipXSpring;
    /* access modifiers changed from: private */
    public float pipY;
    /* access modifiers changed from: private */
    public SpringAnimation pipYSpring;
    private ImageView playPauseButton;
    /* access modifiers changed from: private */
    public boolean postedDismissControls;
    private Runnable progressRunnable = new PipVideoOverlay$$ExternalSyntheticLambda8(this);
    /* access modifiers changed from: private */
    public float scaleFactor = 1.0f;
    /* access modifiers changed from: private */
    public ScaleGestureDetector scaleGestureDetector;
    /* access modifiers changed from: private */
    public VideoForwardDrawable videoForwardDrawable = new VideoForwardDrawable(false);
    /* access modifiers changed from: private */
    public float videoProgress;
    private VideoProgressView videoProgressView;
    /* access modifiers changed from: private */
    public WindowManager.LayoutParams windowLayoutParams;
    /* access modifiers changed from: private */
    public WindowManager windowManager;

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$static$1(PipVideoOverlay pipVideoOverlay, float f) {
        WindowManager.LayoutParams layoutParams = pipVideoOverlay.windowLayoutParams;
        pipVideoOverlay.pipX = f;
        layoutParams.x = (int) f;
        try {
            pipVideoOverlay.windowManager.updateViewLayout(pipVideoOverlay.contentView, layoutParams);
        } catch (IllegalArgumentException unused) {
            pipVideoOverlay.pipXSpring.cancel();
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$static$3(PipVideoOverlay pipVideoOverlay, float f) {
        WindowManager.LayoutParams layoutParams = pipVideoOverlay.windowLayoutParams;
        pipVideoOverlay.pipY = f;
        layoutParams.y = (int) f;
        try {
            pipVideoOverlay.windowManager.updateViewLayout(pipVideoOverlay.contentView, layoutParams);
        } catch (IllegalArgumentException unused) {
            pipVideoOverlay.pipYSpring.cancel();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$4() {
        PhotoViewer photoViewer2 = this.photoViewer;
        if (photoViewer2 != null) {
            PhotoViewerWebView photoViewerWebView2 = this.photoViewerWebView;
            if (photoViewerWebView2 != null) {
                this.videoProgress = ((float) photoViewerWebView2.getCurrentPosition()) / ((float) this.photoViewerWebView.getVideoDuration());
                this.bufferProgress = this.photoViewerWebView.getBufferedPosition();
            } else {
                VideoPlayer videoPlayer = photoViewer2.getVideoPlayer();
                if (videoPlayer != null) {
                    float duration = (float) getDuration();
                    this.videoProgress = ((float) videoPlayer.getCurrentPosition()) / duration;
                    this.bufferProgress = ((float) videoPlayer.getBufferedPosition()) / duration;
                } else {
                    return;
                }
            }
            this.videoProgressView.invalidate();
            AndroidUtilities.runOnUIThread(this.progressRunnable, 500);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$5() {
        PhotoViewer photoViewer2 = this.photoViewer;
        if (photoViewer2 == null || photoViewer2.getVideoPlayerRewinder().rewindCount <= 0) {
            this.isShowingControls = false;
            toggleControls(false);
            this.postedDismissControls = false;
            return;
        }
        AndroidUtilities.runOnUIThread(this.dismissControlsCallback, 1500);
    }

    public static void onRewindCanceled() {
        instance.onRewindCanceledInternal();
    }

    private void onRewindCanceledInternal() {
        this.videoForwardDrawable.setShowing(false);
    }

    public static void onUpdateRewindProgressUi(long j, float f, boolean z) {
        instance.onUpdateRewindProgressUiInternal(j, f, z);
    }

    /* access modifiers changed from: private */
    public void onUpdateRewindProgressUiInternal(long j, float f, boolean z) {
        this.videoForwardDrawable.setTime(0);
        if (z) {
            this.videoProgress = f;
            VideoProgressView videoProgressView2 = this.videoProgressView;
            if (videoProgressView2 != null) {
                videoProgressView2.invalidate();
            }
            FrameLayout frameLayout = this.controlsView;
            if (frameLayout != null) {
                frameLayout.invalidate();
            }
        }
    }

    public static void onRewindStart(boolean z) {
        instance.onRewindStartInternal(z);
    }

    private void onRewindStartInternal(boolean z) {
        this.videoForwardDrawable.setOneShootAnimation(false);
        this.videoForwardDrawable.setLeftSide(!z);
        this.videoForwardDrawable.setShowing(true);
        VideoProgressView videoProgressView2 = this.videoProgressView;
        if (videoProgressView2 != null) {
            videoProgressView2.invalidate();
        }
        FrameLayout frameLayout = this.controlsView;
        if (frameLayout != null) {
            frameLayout.invalidate();
        }
    }

    /* access modifiers changed from: private */
    public long getCurrentPosition() {
        PhotoViewerWebView photoViewerWebView2 = this.photoViewerWebView;
        if (photoViewerWebView2 != null) {
            return (long) photoViewerWebView2.getCurrentPosition();
        }
        VideoPlayer videoPlayer = this.photoViewer.getVideoPlayer();
        if (videoPlayer == null) {
            return 0;
        }
        return videoPlayer.getCurrentPosition();
    }

    /* access modifiers changed from: private */
    public void seekTo(long j) {
        PhotoViewerWebView photoViewerWebView2 = this.photoViewerWebView;
        if (photoViewerWebView2 != null) {
            photoViewerWebView2.seekTo(j);
            return;
        }
        VideoPlayer videoPlayer = this.photoViewer.getVideoPlayer();
        if (videoPlayer != null) {
            videoPlayer.seekTo(j);
        }
    }

    /* access modifiers changed from: private */
    public long getDuration() {
        PhotoViewerWebView photoViewerWebView2 = this.photoViewerWebView;
        if (photoViewerWebView2 != null) {
            return (long) photoViewerWebView2.getVideoDuration();
        }
        VideoPlayer videoPlayer = this.photoViewer.getVideoPlayer();
        if (videoPlayer == null) {
            return 0;
        }
        return videoPlayer.getDuration();
    }

    /* access modifiers changed from: protected */
    public void onLongClick() {
        PhotoViewer photoViewer2 = this.photoViewer;
        if (photoViewer2 == null) {
            return;
        }
        if (!(photoViewer2.getVideoPlayer() == null && this.photoViewerWebView == null) && !this.isDismissing && !this.isVideoCompleted && !this.isScrolling && !this.scaleGestureDetector.isInProgress() && this.canLongClick) {
            VideoPlayer videoPlayer = this.photoViewer.getVideoPlayer();
            boolean z = false;
            if (this.longClickStartPoint[0] >= ((float) getSuggestedWidth()) * this.scaleFactor * 0.5f) {
                z = true;
            }
            long currentPosition = getCurrentPosition();
            long duration = getDuration();
            if (currentPosition != -9223372036854775807L && duration >= 15000) {
                if (this.photoViewerWebView != null) {
                    this.photoViewer.getVideoPlayerRewinder().startRewind(this.photoViewerWebView, z, this.photoViewer.getCurrentVideoSpeed());
                } else {
                    this.photoViewer.getVideoPlayerRewinder().startRewind(videoPlayer, z, this.photoViewer.getCurrentVideoSpeed());
                }
                if (!this.isShowingControls) {
                    this.isShowingControls = true;
                    toggleControls(true);
                    if (!this.postedDismissControls) {
                        AndroidUtilities.runOnUIThread(this.dismissControlsCallback, 1500);
                        this.postedDismissControls = true;
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public PipConfig getPipConfig() {
        if (this.pipConfig == null) {
            Point point = AndroidUtilities.displaySize;
            this.pipConfig = new PipConfig(point.x, point.y);
        }
        return this.pipConfig;
    }

    public static boolean isVisible() {
        return instance.isVisible;
    }

    /* access modifiers changed from: private */
    public int getSuggestedWidth() {
        return getSuggestedWidth(getRatio());
    }

    private static int getSuggestedWidth(float f) {
        float min;
        float f2;
        if (f >= 1.0f) {
            Point point = AndroidUtilities.displaySize;
            min = (float) Math.min(point.x, point.y);
            f2 = 0.35f;
        } else {
            Point point2 = AndroidUtilities.displaySize;
            min = (float) Math.min(point2.x, point2.y);
            f2 = 0.6f;
        }
        return (int) (min * f2);
    }

    /* access modifiers changed from: private */
    public int getSuggestedHeight() {
        return getSuggestedHeight(getRatio());
    }

    private static int getSuggestedHeight(float f) {
        return (int) (((float) getSuggestedWidth(f)) * f);
    }

    private float getRatio() {
        if (this.aspectRatio == null) {
            this.aspectRatio = Float.valueOf(((float) this.mVideoHeight) / ((float) this.mVideoWidth));
            Point point = AndroidUtilities.displaySize;
            this.maxScaleFactor = ((float) (Math.min(point.x, point.y) - AndroidUtilities.dp(32.0f))) / ((float) getSuggestedWidth());
            this.videoForwardDrawable.setPlayScaleFactor(this.aspectRatio.floatValue() < 1.0f ? 0.6f : 0.45f);
        }
        return this.aspectRatio.floatValue();
    }

    /* access modifiers changed from: private */
    public void toggleControls(boolean z) {
        float[] fArr = new float[2];
        float f = 0.0f;
        fArr[0] = z ? 0.0f : 1.0f;
        if (z) {
            f = 1.0f;
        }
        fArr[1] = f;
        ValueAnimator duration = ValueAnimator.ofFloat(fArr).setDuration(200);
        this.controlsAnimator = duration;
        duration.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.controlsAnimator.addUpdateListener(new PipVideoOverlay$$ExternalSyntheticLambda0(this));
        this.controlsAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                ValueAnimator unused = PipVideoOverlay.this.controlsAnimator = null;
            }
        });
        this.controlsAnimator.start();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$toggleControls$6(ValueAnimator valueAnimator) {
        this.controlsView.setAlpha(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    public static void dimissAndDestroy() {
        PipVideoOverlay pipVideoOverlay = instance;
        EmbedBottomSheet embedBottomSheet = pipVideoOverlay.parentSheet;
        if (embedBottomSheet != null) {
            embedBottomSheet.destroy();
        } else {
            PhotoViewer photoViewer2 = pipVideoOverlay.photoViewer;
            if (photoViewer2 != null) {
                photoViewer2.destroyPhotoViewer();
            }
        }
        dismiss();
    }

    public static void dismiss() {
        dismiss(false);
    }

    public static void dismiss(boolean z) {
        instance.dismissInternal(z);
    }

    private void dismissInternal(boolean z) {
        if (!this.isDismissing) {
            this.isDismissing = true;
            ValueAnimator valueAnimator = this.controlsAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            if (this.postedDismissControls) {
                AndroidUtilities.cancelRunOnUIThread(this.dismissControlsCallback);
                this.postedDismissControls = false;
            }
            SpringAnimation springAnimation = this.pipXSpring;
            if (springAnimation != null) {
                springAnimation.cancel();
                this.pipYSpring.cancel();
            }
            if (z) {
                AndroidUtilities.runOnUIThread(new PipVideoOverlay$$ExternalSyntheticLambda7(this), 100);
                return;
            }
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(250);
            animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.contentView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.contentView, View.SCALE_X, new float[]{0.1f}), ObjectAnimator.ofFloat(this.contentView, View.SCALE_Y, new float[]{0.1f})});
            animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    PipVideoOverlay.this.onDismissedInternal();
                }
            });
            animatorSet.start();
        }
    }

    /* access modifiers changed from: private */
    public void onDismissedInternal() {
        try {
            if (this.controlsView.getParent() != null) {
                this.windowManager.removeViewImmediate(this.contentView);
            }
        } catch (IllegalArgumentException unused) {
        }
        PhotoViewerWebView photoViewerWebView2 = this.photoViewerWebView;
        if (photoViewerWebView2 != null) {
            photoViewerWebView2.showControls();
        }
        this.videoProgressView = null;
        this.innerView = null;
        this.photoViewer = null;
        this.photoViewerWebView = null;
        this.parentSheet = null;
        this.consumingChild = null;
        this.isScrolling = false;
        this.isVisible = false;
        this.isDismissing = false;
        this.canLongClick = false;
        cancelRewind();
        AndroidUtilities.cancelRunOnUIThread(this.longClickCallback);
    }

    public static View getInnerView() {
        return instance.innerView;
    }

    /* access modifiers changed from: private */
    public void cancelRewind() {
        PhotoViewer photoViewer2 = this.photoViewer;
        if (photoViewer2 != null && photoViewer2.getVideoPlayerRewinder().rewindCount > 0) {
            this.photoViewer.getVideoPlayerRewinder().cancelRewind();
        }
    }

    public static void updatePlayButton() {
        instance.updatePlayButtonInternal();
    }

    private void updatePlayButtonInternal() {
        boolean z;
        PhotoViewer photoViewer2 = this.photoViewer;
        if (photoViewer2 != null && this.playPauseButton != null) {
            PhotoViewerWebView photoViewerWebView2 = this.photoViewerWebView;
            if (photoViewerWebView2 != null) {
                z = photoViewerWebView2.isPlaying();
            } else {
                VideoPlayer videoPlayer = photoViewer2.getVideoPlayer();
                if (videoPlayer != null) {
                    z = videoPlayer.isPlaying();
                } else {
                    return;
                }
            }
            AndroidUtilities.cancelRunOnUIThread(this.progressRunnable);
            if (z) {
                this.playPauseButton.setImageResource(R.drawable.pip_pause_large);
                AndroidUtilities.runOnUIThread(this.progressRunnable, 500);
            } else if (this.isVideoCompleted) {
                this.playPauseButton.setImageResource(R.drawable.pip_replay_large);
            } else {
                this.playPauseButton.setImageResource(R.drawable.pip_play_large);
            }
        }
    }

    public static void onVideoCompleted() {
        instance.onVideoCompletedInternal();
    }

    private void onVideoCompletedInternal() {
        VideoProgressView videoProgressView2;
        if (this.isVisible && (videoProgressView2 = this.videoProgressView) != null) {
            this.isVideoCompleted = true;
            this.videoProgress = 0.0f;
            this.bufferProgress = 0.0f;
            if (videoProgressView2 != null) {
                videoProgressView2.invalidate();
            }
            updatePlayButtonInternal();
            AndroidUtilities.cancelRunOnUIThread(this.progressRunnable);
            if (!this.isShowingControls) {
                toggleControls(true);
                AndroidUtilities.cancelRunOnUIThread(this.dismissControlsCallback);
            }
        }
    }

    public static void setBufferedProgress(float f) {
        PipVideoOverlay pipVideoOverlay = instance;
        pipVideoOverlay.bufferProgress = f;
        VideoProgressView videoProgressView2 = pipVideoOverlay.videoProgressView;
        if (videoProgressView2 != null) {
            videoProgressView2.invalidate();
        }
    }

    public static void setParentSheet(EmbedBottomSheet embedBottomSheet) {
        instance.parentSheet = embedBottomSheet;
    }

    public static void setPhotoViewer(PhotoViewer photoViewer2) {
        PipVideoOverlay pipVideoOverlay = instance;
        pipVideoOverlay.photoViewer = photoViewer2;
        pipVideoOverlay.updatePlayButtonInternal();
    }

    public static Rect getPipRect(boolean z, float f) {
        Rect rect = new Rect();
        float f2 = 1.0f / f;
        PipVideoOverlay pipVideoOverlay = instance;
        if (!pipVideoOverlay.isVisible || z) {
            float access$300 = pipVideoOverlay.getPipConfig().getPipX();
            float access$400 = instance.getPipConfig().getPipY();
            float access$500 = instance.getPipConfig().getScaleFactor();
            rect.width = ((float) getSuggestedWidth(f2)) * access$500;
            rect.height = ((float) getSuggestedHeight(f2)) * access$500;
            if (access$300 != -1.0f) {
                float f3 = rect.width;
                float f4 = access$300 + (f3 / 2.0f);
                int i = AndroidUtilities.displaySize.x;
                rect.x = f4 >= ((float) i) / 2.0f ? (((float) i) - f3) - ((float) AndroidUtilities.dp(16.0f)) : (float) AndroidUtilities.dp(16.0f);
            } else {
                rect.x = (((float) AndroidUtilities.displaySize.x) - rect.width) - ((float) AndroidUtilities.dp(16.0f));
            }
            if (access$400 != -1.0f) {
                rect.y = MathUtils.clamp(access$400, (float) AndroidUtilities.dp(16.0f), ((float) (AndroidUtilities.displaySize.y - AndroidUtilities.dp(16.0f))) - rect.height) + ((float) AndroidUtilities.statusBarHeight);
            } else {
                rect.y = (float) (AndroidUtilities.dp(16.0f) + AndroidUtilities.statusBarHeight);
            }
            return rect;
        }
        rect.x = pipVideoOverlay.pipX;
        rect.y = pipVideoOverlay.pipY + ((float) AndroidUtilities.statusBarHeight);
        PipVideoOverlay pipVideoOverlay2 = instance;
        rect.width = (float) pipVideoOverlay2.pipWidth;
        rect.height = (float) pipVideoOverlay2.pipHeight;
        return rect;
    }

    public static boolean show(boolean z, Activity activity, View view, int i, int i2) {
        return show(z, activity, view, i, i2, false);
    }

    public static boolean show(boolean z, Activity activity, View view, int i, int i2, boolean z2) {
        return show(z, activity, (PhotoViewerWebView) null, view, i, i2, z2);
    }

    public static boolean show(boolean z, Activity activity, PhotoViewerWebView photoViewerWebView2, View view, int i, int i2, boolean z2) {
        return instance.showInternal(z, activity, view, photoViewerWebView2, i, i2, z2);
    }

    private boolean showInternal(boolean z, Activity activity, View view, PhotoViewerWebView photoViewerWebView2, int i, int i2, boolean z2) {
        Context context;
        PhotoViewerWebView photoViewerWebView3;
        boolean z3 = z;
        PhotoViewerWebView photoViewerWebView4 = photoViewerWebView2;
        if (this.isVisible) {
            return false;
        }
        this.isVisible = true;
        this.mVideoWidth = i;
        this.mVideoHeight = i2;
        this.aspectRatio = null;
        if (photoViewerWebView4 == null || !photoViewerWebView2.isControllable()) {
            this.photoViewerWebView = null;
        } else {
            this.photoViewerWebView = photoViewerWebView4;
            photoViewerWebView2.hideControls();
        }
        float access$300 = getPipConfig().getPipX();
        float access$400 = getPipConfig().getPipY();
        this.scaleFactor = getPipConfig().getScaleFactor();
        this.pipWidth = (int) (((float) getSuggestedWidth()) * this.scaleFactor);
        this.pipHeight = (int) (((float) getSuggestedHeight()) * this.scaleFactor);
        this.isShowingControls = false;
        this.pipXSpring = (SpringAnimation) new SpringAnimation(this, PIP_X_PROPERTY).setSpring(new SpringForce().setDampingRatio(0.75f).setStiffness(650.0f)).addEndListener(new PipVideoOverlay$$ExternalSyntheticLambda4(this));
        this.pipYSpring = (SpringAnimation) new SpringAnimation(this, PIP_Y_PROPERTY).setSpring(new SpringForce().setDampingRatio(0.75f).setStiffness(650.0f)).addEndListener(new PipVideoOverlay$$ExternalSyntheticLambda5(this));
        Context context2 = ApplicationLoader.applicationContext;
        final int scaledTouchSlop = ViewConfiguration.get(context2).getScaledTouchSlop();
        ScaleGestureDetector scaleGestureDetector2 = new ScaleGestureDetector(context2, new ScaleGestureDetector.OnScaleGestureListener() {
            public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
                PipVideoOverlay pipVideoOverlay = PipVideoOverlay.this;
                float unused = pipVideoOverlay.scaleFactor = MathUtils.clamp(pipVideoOverlay.scaleFactor * scaleGestureDetector.getScaleFactor(), PipVideoOverlay.this.minScaleFactor, PipVideoOverlay.this.maxScaleFactor);
                PipVideoOverlay pipVideoOverlay2 = PipVideoOverlay.this;
                int unused2 = pipVideoOverlay2.pipWidth = (int) (((float) pipVideoOverlay2.getSuggestedWidth()) * PipVideoOverlay.this.scaleFactor);
                PipVideoOverlay pipVideoOverlay3 = PipVideoOverlay.this;
                int unused3 = pipVideoOverlay3.pipHeight = (int) (((float) pipVideoOverlay3.getSuggestedHeight()) * PipVideoOverlay.this.scaleFactor);
                AndroidUtilities.runOnUIThread(new PipVideoOverlay$3$$ExternalSyntheticLambda0(this));
                float focusX = scaleGestureDetector.getFocusX();
                int i = AndroidUtilities.displaySize.x;
                float access$900 = focusX >= ((float) i) / 2.0f ? (float) ((i - PipVideoOverlay.this.pipWidth) - AndroidUtilities.dp(16.0f)) : (float) AndroidUtilities.dp(16.0f);
                if (!PipVideoOverlay.this.pipXSpring.isRunning()) {
                    ((SpringAnimation) PipVideoOverlay.this.pipXSpring.setStartValue(PipVideoOverlay.this.pipX)).getSpring().setFinalPosition(access$900);
                } else {
                    PipVideoOverlay.this.pipXSpring.getSpring().setFinalPosition(access$900);
                }
                PipVideoOverlay.this.pipXSpring.start();
                float clamp = MathUtils.clamp(scaleGestureDetector.getFocusY() - (((float) PipVideoOverlay.this.pipHeight) / 2.0f), (float) AndroidUtilities.dp(16.0f), (float) ((AndroidUtilities.displaySize.y - PipVideoOverlay.this.pipHeight) - AndroidUtilities.dp(16.0f)));
                if (!PipVideoOverlay.this.pipYSpring.isRunning()) {
                    ((SpringAnimation) PipVideoOverlay.this.pipYSpring.setStartValue(PipVideoOverlay.this.pipY)).getSpring().setFinalPosition(clamp);
                } else {
                    PipVideoOverlay.this.pipYSpring.getSpring().setFinalPosition(clamp);
                }
                PipVideoOverlay.this.pipYSpring.start();
                return true;
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onScale$0() {
                PipVideoOverlay.this.contentView.invalidate();
                PipVideoOverlay.this.contentFrameLayout.requestLayout();
            }

            public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
                if (PipVideoOverlay.this.isScrolling) {
                    boolean unused = PipVideoOverlay.this.isScrolling = false;
                    boolean unused2 = PipVideoOverlay.this.canLongClick = false;
                    PipVideoOverlay.this.cancelRewind();
                    AndroidUtilities.cancelRunOnUIThread(PipVideoOverlay.this.longClickCallback);
                }
                boolean unused3 = PipVideoOverlay.this.isScrollDisallowed = true;
                PipVideoOverlay.this.windowLayoutParams.width = (int) (((float) PipVideoOverlay.this.getSuggestedWidth()) * PipVideoOverlay.this.maxScaleFactor);
                PipVideoOverlay.this.windowLayoutParams.height = (int) (((float) PipVideoOverlay.this.getSuggestedHeight()) * PipVideoOverlay.this.maxScaleFactor);
                PipVideoOverlay.this.windowManager.updateViewLayout(PipVideoOverlay.this.contentView, PipVideoOverlay.this.windowLayoutParams);
                return true;
            }

            public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
                if (PipVideoOverlay.this.pipXSpring.isRunning() || PipVideoOverlay.this.pipYSpring.isRunning()) {
                    final ArrayList arrayList = new ArrayList();
                    AnonymousClass1 r0 = new DynamicAnimation.OnAnimationEndListener() {
                        public void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                            dynamicAnimation.removeEndListener(this);
                            arrayList.add((SpringAnimation) dynamicAnimation);
                            if (arrayList.size() == 2) {
                                AnonymousClass3.this.updateLayout();
                            }
                        }
                    };
                    if (!PipVideoOverlay.this.pipXSpring.isRunning()) {
                        arrayList.add(PipVideoOverlay.this.pipXSpring);
                    } else {
                        PipVideoOverlay.this.pipXSpring.addEndListener(r0);
                    }
                    if (!PipVideoOverlay.this.pipYSpring.isRunning()) {
                        arrayList.add(PipVideoOverlay.this.pipYSpring);
                    } else {
                        PipVideoOverlay.this.pipYSpring.addEndListener(r0);
                    }
                } else {
                    updateLayout();
                }
            }

            /* access modifiers changed from: private */
            public void updateLayout() {
                PipVideoOverlay pipVideoOverlay = PipVideoOverlay.this;
                WindowManager.LayoutParams access$2200 = pipVideoOverlay.windowLayoutParams;
                int access$1000 = (int) (((float) PipVideoOverlay.this.getSuggestedWidth()) * PipVideoOverlay.this.scaleFactor);
                access$2200.width = access$1000;
                int unused = pipVideoOverlay.pipWidth = access$1000;
                PipVideoOverlay pipVideoOverlay2 = PipVideoOverlay.this;
                WindowManager.LayoutParams access$22002 = pipVideoOverlay2.windowLayoutParams;
                int access$1200 = (int) (((float) PipVideoOverlay.this.getSuggestedHeight()) * PipVideoOverlay.this.scaleFactor);
                access$22002.height = access$1200;
                int unused2 = pipVideoOverlay2.pipHeight = access$1200;
                try {
                    PipVideoOverlay.this.windowManager.updateViewLayout(PipVideoOverlay.this.contentView, PipVideoOverlay.this.windowLayoutParams);
                } catch (IllegalArgumentException unused3) {
                }
            }
        });
        this.scaleGestureDetector = scaleGestureDetector2;
        int i3 = Build.VERSION.SDK_INT;
        if (i3 >= 19) {
            scaleGestureDetector2.setQuickScaleEnabled(false);
        }
        if (i3 >= 23) {
            this.scaleGestureDetector.setStylusScaleEnabled(false);
        }
        this.gestureDetector = new GestureDetectorFixDoubleTap(context2, new GestureDetectorFixDoubleTap.OnGestureListener() {
            private float startPipX;
            private float startPipY;

            public boolean onDown(MotionEvent motionEvent) {
                if (PipVideoOverlay.this.isShowingControls) {
                    for (int i = 1; i < PipVideoOverlay.this.contentFrameLayout.getChildCount(); i++) {
                        View childAt = PipVideoOverlay.this.contentFrameLayout.getChildAt(i);
                        if (childAt.dispatchTouchEvent(motionEvent)) {
                            View unused = PipVideoOverlay.this.consumingChild = childAt;
                            return true;
                        }
                    }
                }
                this.startPipX = PipVideoOverlay.this.pipX;
                this.startPipY = PipVideoOverlay.this.pipY;
                return true;
            }

            public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
                if (PipVideoOverlay.this.controlsAnimator != null) {
                    return true;
                }
                if (PipVideoOverlay.this.postedDismissControls) {
                    AndroidUtilities.cancelRunOnUIThread(PipVideoOverlay.this.dismissControlsCallback);
                    boolean unused = PipVideoOverlay.this.postedDismissControls = false;
                }
                PipVideoOverlay pipVideoOverlay = PipVideoOverlay.this;
                boolean unused2 = pipVideoOverlay.isShowingControls = !pipVideoOverlay.isShowingControls;
                PipVideoOverlay pipVideoOverlay2 = PipVideoOverlay.this;
                pipVideoOverlay2.toggleControls(pipVideoOverlay2.isShowingControls);
                if (PipVideoOverlay.this.isShowingControls && !PipVideoOverlay.this.postedDismissControls) {
                    AndroidUtilities.runOnUIThread(PipVideoOverlay.this.dismissControlsCallback, 2500);
                    boolean unused3 = PipVideoOverlay.this.postedDismissControls = true;
                }
                return true;
            }

            /* JADX WARNING: Removed duplicated region for block: B:41:0x00b6  */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public boolean onDoubleTap(android.view.MotionEvent r14) {
                /*
                    r13 = this;
                    org.telegram.ui.Components.PipVideoOverlay r0 = org.telegram.ui.Components.PipVideoOverlay.this
                    org.telegram.ui.PhotoViewer r0 = r0.photoViewer
                    r1 = 0
                    if (r0 == 0) goto L_0x010f
                    org.telegram.ui.Components.PipVideoOverlay r0 = org.telegram.ui.Components.PipVideoOverlay.this
                    org.telegram.ui.PhotoViewer r0 = r0.photoViewer
                    org.telegram.ui.Components.VideoPlayer r0 = r0.getVideoPlayer()
                    if (r0 != 0) goto L_0x001d
                    org.telegram.ui.Components.PipVideoOverlay r0 = org.telegram.ui.Components.PipVideoOverlay.this
                    org.telegram.ui.Components.PhotoViewerWebView r0 = r0.photoViewerWebView
                    if (r0 == 0) goto L_0x010f
                L_0x001d:
                    org.telegram.ui.Components.PipVideoOverlay r0 = org.telegram.ui.Components.PipVideoOverlay.this
                    boolean r0 = r0.isDismissing
                    if (r0 != 0) goto L_0x010f
                    org.telegram.ui.Components.PipVideoOverlay r0 = org.telegram.ui.Components.PipVideoOverlay.this
                    boolean r0 = r0.isVideoCompleted
                    if (r0 != 0) goto L_0x010f
                    org.telegram.ui.Components.PipVideoOverlay r0 = org.telegram.ui.Components.PipVideoOverlay.this
                    boolean r0 = r0.isScrolling
                    if (r0 != 0) goto L_0x010f
                    org.telegram.ui.Components.PipVideoOverlay r0 = org.telegram.ui.Components.PipVideoOverlay.this
                    android.view.ScaleGestureDetector r0 = r0.scaleGestureDetector
                    boolean r0 = r0.isInProgress()
                    if (r0 != 0) goto L_0x010f
                    org.telegram.ui.Components.PipVideoOverlay r0 = org.telegram.ui.Components.PipVideoOverlay.this
                    boolean r0 = r0.canLongClick
                    if (r0 != 0) goto L_0x004b
                    goto L_0x010f
                L_0x004b:
                    org.telegram.ui.Components.PipVideoOverlay r0 = org.telegram.ui.Components.PipVideoOverlay.this
                    org.telegram.ui.PhotoViewer r0 = r0.photoViewer
                    r0.getVideoPlayer()
                    float r14 = r14.getX()
                    org.telegram.ui.Components.PipVideoOverlay r0 = org.telegram.ui.Components.PipVideoOverlay.this
                    int r0 = r0.getSuggestedWidth()
                    float r0 = (float) r0
                    org.telegram.ui.Components.PipVideoOverlay r2 = org.telegram.ui.Components.PipVideoOverlay.this
                    float r2 = r2.scaleFactor
                    float r0 = r0 * r2
                    r2 = 1056964608(0x3var_, float:0.5)
                    float r0 = r0 * r2
                    r2 = 1
                    int r14 = (r14 > r0 ? 1 : (r14 == r0 ? 0 : -1))
                    if (r14 < 0) goto L_0x0072
                    r14 = 1
                    goto L_0x0073
                L_0x0072:
                    r14 = 0
                L_0x0073:
                    org.telegram.ui.Components.PipVideoOverlay r0 = org.telegram.ui.Components.PipVideoOverlay.this
                    long r3 = r0.getCurrentPosition()
                    org.telegram.ui.Components.PipVideoOverlay r0 = org.telegram.ui.Components.PipVideoOverlay.this
                    long r5 = r0.getDuration()
                    r7 = -9223372036854775807(0xNUM, double:-4.9E-324)
                    int r0 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
                    if (r0 == 0) goto L_0x010f
                    r7 = 15000(0x3a98, double:7.411E-320)
                    int r0 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
                    if (r0 >= 0) goto L_0x0090
                    goto L_0x010f
                L_0x0090:
                    r7 = 10000(0x2710, double:4.9407E-320)
                    if (r14 == 0) goto L_0x0097
                    long r9 = r3 + r7
                    goto L_0x0099
                L_0x0097:
                    long r9 = r3 - r7
                L_0x0099:
                    int r0 = (r3 > r9 ? 1 : (r3 == r9 ? 0 : -1))
                    if (r0 == 0) goto L_0x010f
                    r3 = 0
                    int r0 = (r9 > r5 ? 1 : (r9 == r5 ? 0 : -1))
                    if (r0 <= 0) goto L_0x00a5
                    r9 = r5
                    goto L_0x00b3
                L_0x00a5:
                    int r0 = (r9 > r3 ? 1 : (r9 == r3 ? 0 : -1))
                    if (r0 >= 0) goto L_0x00b3
                    r11 = -9000(0xffffffffffffdcd8, double:NaN)
                    int r0 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1))
                    if (r0 >= 0) goto L_0x00b0
                    goto L_0x00b1
                L_0x00b0:
                    r1 = 1
                L_0x00b1:
                    r9 = r3
                    goto L_0x00b4
                L_0x00b3:
                    r1 = 1
                L_0x00b4:
                    if (r1 == 0) goto L_0x010e
                    org.telegram.ui.Components.PipVideoOverlay r0 = org.telegram.ui.Components.PipVideoOverlay.this
                    org.telegram.ui.Components.VideoForwardDrawable r0 = r0.videoForwardDrawable
                    r0.setOneShootAnimation(r2)
                    org.telegram.ui.Components.PipVideoOverlay r0 = org.telegram.ui.Components.PipVideoOverlay.this
                    org.telegram.ui.Components.VideoForwardDrawable r0 = r0.videoForwardDrawable
                    r1 = r14 ^ 1
                    r0.setLeftSide(r1)
                    org.telegram.ui.Components.PipVideoOverlay r0 = org.telegram.ui.Components.PipVideoOverlay.this
                    org.telegram.ui.Components.VideoForwardDrawable r0 = r0.videoForwardDrawable
                    r0.addTime(r7)
                    org.telegram.ui.Components.PipVideoOverlay r0 = org.telegram.ui.Components.PipVideoOverlay.this
                    r0.seekTo(r9)
                    org.telegram.ui.Components.PipVideoOverlay r0 = org.telegram.ui.Components.PipVideoOverlay.this
                    if (r14 == 0) goto L_0x00dd
                    goto L_0x00df
                L_0x00dd:
                    r7 = -10000(0xffffffffffffd8f0, double:NaN)
                L_0x00df:
                    float r14 = (float) r9
                    float r1 = (float) r5
                    float r14 = r14 / r1
                    r0.onUpdateRewindProgressUiInternal(r7, r14, r2)
                    org.telegram.ui.Components.PipVideoOverlay r14 = org.telegram.ui.Components.PipVideoOverlay.this
                    boolean r14 = r14.isShowingControls
                    if (r14 != 0) goto L_0x010e
                    org.telegram.ui.Components.PipVideoOverlay r14 = org.telegram.ui.Components.PipVideoOverlay.this
                    boolean r0 = r14.isShowingControls = r2
                    r14.toggleControls(r0)
                    org.telegram.ui.Components.PipVideoOverlay r14 = org.telegram.ui.Components.PipVideoOverlay.this
                    boolean r14 = r14.postedDismissControls
                    if (r14 != 0) goto L_0x010e
                    org.telegram.ui.Components.PipVideoOverlay r14 = org.telegram.ui.Components.PipVideoOverlay.this
                    boolean unused = r14.postedDismissControls = r2
                    org.telegram.ui.Components.PipVideoOverlay r14 = org.telegram.ui.Components.PipVideoOverlay.this
                    java.lang.Runnable r14 = r14.dismissControlsCallback
                    r0 = 2500(0x9c4, double:1.235E-320)
                    org.telegram.messenger.AndroidUtilities.runOnUIThread(r14, r0)
                L_0x010e:
                    return r2
                L_0x010f:
                    return r1
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PipVideoOverlay.AnonymousClass4.onDoubleTap(android.view.MotionEvent):boolean");
            }

            public boolean onSingleTapUp(MotionEvent motionEvent) {
                if (!hasDoubleTap()) {
                    return onSingleTapConfirmed(motionEvent);
                }
                return super.onSingleTapUp(motionEvent);
            }

            public boolean hasDoubleTap() {
                if (PipVideoOverlay.this.photoViewer == null) {
                    return false;
                }
                if ((PipVideoOverlay.this.photoViewer.getVideoPlayer() == null && PipVideoOverlay.this.photoViewerWebView == null) || PipVideoOverlay.this.isDismissing || PipVideoOverlay.this.isVideoCompleted || PipVideoOverlay.this.isScrolling || PipVideoOverlay.this.scaleGestureDetector.isInProgress() || !PipVideoOverlay.this.canLongClick) {
                    return false;
                }
                long access$3700 = PipVideoOverlay.this.getCurrentPosition();
                long access$3800 = PipVideoOverlay.this.getDuration();
                if (access$3700 == -9223372036854775807L || access$3800 < 15000) {
                    return false;
                }
                return true;
            }

            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
                if (!PipVideoOverlay.this.isScrolling || PipVideoOverlay.this.isScrollDisallowed) {
                    return false;
                }
                SpringForce spring = ((SpringAnimation) ((SpringAnimation) PipVideoOverlay.this.pipXSpring.setStartVelocity(f)).setStartValue(PipVideoOverlay.this.pipX)).getSpring();
                float access$1400 = PipVideoOverlay.this.pipX + (((float) PipVideoOverlay.this.pipWidth) / 2.0f) + (f / 7.0f);
                int i = AndroidUtilities.displaySize.x;
                spring.setFinalPosition(access$1400 >= ((float) i) / 2.0f ? (float) ((i - PipVideoOverlay.this.pipWidth) - AndroidUtilities.dp(16.0f)) : (float) AndroidUtilities.dp(16.0f));
                PipVideoOverlay.this.pipXSpring.start();
                ((SpringAnimation) ((SpringAnimation) PipVideoOverlay.this.pipYSpring.setStartVelocity(f)).setStartValue(PipVideoOverlay.this.pipY)).getSpring().setFinalPosition(MathUtils.clamp(PipVideoOverlay.this.pipY + (f2 / 10.0f), (float) AndroidUtilities.dp(16.0f), (float) ((AndroidUtilities.displaySize.y - PipVideoOverlay.this.pipHeight) - AndroidUtilities.dp(16.0f))));
                PipVideoOverlay.this.pipYSpring.start();
                return true;
            }

            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
                int i;
                if (!PipVideoOverlay.this.isScrolling && PipVideoOverlay.this.controlsAnimator == null && !PipVideoOverlay.this.isScrollDisallowed && (Math.abs(f) >= ((float) scaledTouchSlop) || Math.abs(f2) >= ((float) scaledTouchSlop))) {
                    boolean unused = PipVideoOverlay.this.isScrolling = true;
                    PipVideoOverlay.this.pipXSpring.cancel();
                    PipVideoOverlay.this.pipYSpring.cancel();
                    boolean unused2 = PipVideoOverlay.this.canLongClick = false;
                    PipVideoOverlay.this.cancelRewind();
                    AndroidUtilities.cancelRunOnUIThread(PipVideoOverlay.this.longClickCallback);
                }
                if (PipVideoOverlay.this.isScrolling) {
                    float access$1400 = PipVideoOverlay.this.pipX;
                    float rawX = (this.startPipX + motionEvent2.getRawX()) - motionEvent.getRawX();
                    float unused3 = PipVideoOverlay.this.pipY = (this.startPipY + motionEvent2.getRawY()) - motionEvent.getRawY();
                    if (rawX <= ((float) (-PipVideoOverlay.this.pipWidth)) * 0.25f || rawX >= ((float) AndroidUtilities.displaySize.x) - (((float) PipVideoOverlay.this.pipWidth) * 0.75f)) {
                        if (!PipVideoOverlay.this.onSideToDismiss) {
                            SpringForce spring = ((SpringAnimation) PipVideoOverlay.this.pipXSpring.setStartValue(access$1400)).getSpring();
                            float access$900 = rawX + (((float) PipVideoOverlay.this.pipWidth) / 2.0f);
                            int i2 = AndroidUtilities.displaySize.x;
                            if (access$900 >= ((float) i2) / 2.0f) {
                                i = AndroidUtilities.dp(16.0f);
                            } else {
                                i2 = AndroidUtilities.dp(16.0f);
                                i = PipVideoOverlay.this.pipWidth;
                            }
                            spring.setFinalPosition((float) (i2 - i));
                            PipVideoOverlay.this.pipXSpring.start();
                        }
                        boolean unused4 = PipVideoOverlay.this.onSideToDismiss = true;
                    } else if (PipVideoOverlay.this.onSideToDismiss) {
                        if (PipVideoOverlay.this.onSideToDismiss) {
                            PipVideoOverlay.this.pipXSpring.addEndListener(new PipVideoOverlay$4$$ExternalSyntheticLambda0(this, rawX));
                            ((SpringAnimation) PipVideoOverlay.this.pipXSpring.setStartValue(access$1400)).getSpring().setFinalPosition(rawX);
                            PipVideoOverlay.this.pipXSpring.start();
                        }
                        boolean unused5 = PipVideoOverlay.this.onSideToDismiss = false;
                    } else {
                        if (PipVideoOverlay.this.pipXSpring.isRunning()) {
                            PipVideoOverlay.this.pipXSpring.getSpring().setFinalPosition(rawX);
                        } else {
                            PipVideoOverlay.this.windowLayoutParams.x = (int) PipVideoOverlay.this.pipX = rawX;
                            PipVideoOverlay.this.getPipConfig().setPipX(rawX);
                        }
                        PipVideoOverlay.this.windowLayoutParams.y = (int) PipVideoOverlay.this.pipY;
                        PipVideoOverlay.this.getPipConfig().setPipY(PipVideoOverlay.this.pipY);
                        PipVideoOverlay.this.windowManager.updateViewLayout(PipVideoOverlay.this.contentView, PipVideoOverlay.this.windowLayoutParams);
                    }
                }
                return true;
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onScroll$0(float f, DynamicAnimation dynamicAnimation, boolean z, float f2, float f3) {
                if (!z) {
                    SpringForce spring = PipVideoOverlay.this.pipXSpring.getSpring();
                    float access$900 = f + (((float) PipVideoOverlay.this.pipWidth) / 2.0f);
                    int i = AndroidUtilities.displaySize.x;
                    spring.setFinalPosition(access$900 >= ((float) i) / 2.0f ? (float) ((i - PipVideoOverlay.this.pipWidth) - AndroidUtilities.dp(16.0f)) : (float) AndroidUtilities.dp(16.0f));
                }
            }
        });
        this.contentFrameLayout = new FrameLayout(context2) {
            private Path path = new Path();

            public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                int actionMasked = motionEvent.getActionMasked();
                if (actionMasked == 0 || actionMasked == 5) {
                    if (motionEvent.getPointerCount() == 1) {
                        boolean unused = PipVideoOverlay.this.canLongClick = true;
                        float[] unused2 = PipVideoOverlay.this.longClickStartPoint = new float[]{motionEvent.getX(), motionEvent.getY()};
                        AndroidUtilities.runOnUIThread(PipVideoOverlay.this.longClickCallback, 500);
                    } else {
                        boolean unused3 = PipVideoOverlay.this.canLongClick = false;
                        PipVideoOverlay.this.cancelRewind();
                        AndroidUtilities.cancelRunOnUIThread(PipVideoOverlay.this.longClickCallback);
                    }
                }
                if (actionMasked == 1 || actionMasked == 3 || actionMasked == 6) {
                    boolean unused4 = PipVideoOverlay.this.canLongClick = false;
                    PipVideoOverlay.this.cancelRewind();
                    AndroidUtilities.cancelRunOnUIThread(PipVideoOverlay.this.longClickCallback);
                }
                if (PipVideoOverlay.this.consumingChild != null) {
                    MotionEvent obtain = MotionEvent.obtain(motionEvent);
                    obtain.offsetLocation(PipVideoOverlay.this.consumingChild.getX(), PipVideoOverlay.this.consumingChild.getY());
                    boolean dispatchTouchEvent = PipVideoOverlay.this.consumingChild.dispatchTouchEvent(motionEvent);
                    obtain.recycle();
                    if (actionMasked == 1 || actionMasked == 3 || actionMasked == 6) {
                        View unused5 = PipVideoOverlay.this.consumingChild = null;
                    }
                    if (dispatchTouchEvent) {
                        return true;
                    }
                }
                MotionEvent obtain2 = MotionEvent.obtain(motionEvent);
                obtain2.offsetLocation(motionEvent.getRawX() - motionEvent.getX(), motionEvent.getRawY() - motionEvent.getY());
                boolean onTouchEvent = PipVideoOverlay.this.scaleGestureDetector.onTouchEvent(obtain2);
                obtain2.recycle();
                boolean z = !PipVideoOverlay.this.scaleGestureDetector.isInProgress() && PipVideoOverlay.this.gestureDetector.onTouchEvent(motionEvent);
                if (actionMasked == 1 || actionMasked == 3 || actionMasked == 6) {
                    boolean unused6 = PipVideoOverlay.this.isScrolling = false;
                    boolean unused7 = PipVideoOverlay.this.isScrollDisallowed = false;
                    if (PipVideoOverlay.this.onSideToDismiss) {
                        boolean unused8 = PipVideoOverlay.this.onSideToDismiss = false;
                        PipVideoOverlay.dimissAndDestroy();
                    } else {
                        if (!PipVideoOverlay.this.pipXSpring.isRunning()) {
                            SpringForce spring = ((SpringAnimation) PipVideoOverlay.this.pipXSpring.setStartValue(PipVideoOverlay.this.pipX)).getSpring();
                            float access$1400 = PipVideoOverlay.this.pipX + (((float) PipVideoOverlay.this.pipWidth) / 2.0f);
                            int i = AndroidUtilities.displaySize.x;
                            spring.setFinalPosition(access$1400 >= ((float) i) / 2.0f ? (float) ((i - PipVideoOverlay.this.pipWidth) - AndroidUtilities.dp(16.0f)) : (float) AndroidUtilities.dp(16.0f));
                            PipVideoOverlay.this.pipXSpring.start();
                        }
                        if (!PipVideoOverlay.this.pipYSpring.isRunning()) {
                            ((SpringAnimation) PipVideoOverlay.this.pipYSpring.setStartValue(PipVideoOverlay.this.pipY)).getSpring().setFinalPosition(MathUtils.clamp(PipVideoOverlay.this.pipY, (float) AndroidUtilities.dp(16.0f), (float) ((AndroidUtilities.displaySize.y - PipVideoOverlay.this.pipHeight) - AndroidUtilities.dp(16.0f))));
                            PipVideoOverlay.this.pipYSpring.start();
                        }
                    }
                }
                if (onTouchEvent || z) {
                    return true;
                }
                return false;
            }

            /* access modifiers changed from: protected */
            public void onConfigurationChanged(Configuration configuration) {
                AndroidUtilities.checkDisplaySize(getContext(), configuration);
                PipConfig unused = PipVideoOverlay.this.pipConfig = null;
                if (((float) PipVideoOverlay.this.pipWidth) != ((float) PipVideoOverlay.this.getSuggestedWidth()) * PipVideoOverlay.this.scaleFactor || ((float) PipVideoOverlay.this.pipHeight) != ((float) PipVideoOverlay.this.getSuggestedHeight()) * PipVideoOverlay.this.scaleFactor) {
                    WindowManager.LayoutParams access$2200 = PipVideoOverlay.this.windowLayoutParams;
                    PipVideoOverlay pipVideoOverlay = PipVideoOverlay.this;
                    access$2200.width = pipVideoOverlay.pipWidth = (int) (((float) pipVideoOverlay.getSuggestedWidth()) * PipVideoOverlay.this.scaleFactor);
                    WindowManager.LayoutParams access$22002 = PipVideoOverlay.this.windowLayoutParams;
                    PipVideoOverlay pipVideoOverlay2 = PipVideoOverlay.this;
                    access$22002.height = pipVideoOverlay2.pipHeight = (int) (((float) pipVideoOverlay2.getSuggestedHeight()) * PipVideoOverlay.this.scaleFactor);
                    PipVideoOverlay.this.windowManager.updateViewLayout(PipVideoOverlay.this.contentView, PipVideoOverlay.this.windowLayoutParams);
                    SpringForce spring = ((SpringAnimation) PipVideoOverlay.this.pipXSpring.setStartValue(PipVideoOverlay.this.pipX)).getSpring();
                    float access$1400 = PipVideoOverlay.this.pipX + ((((float) PipVideoOverlay.this.getSuggestedWidth()) * PipVideoOverlay.this.scaleFactor) / 2.0f);
                    int i = AndroidUtilities.displaySize.x;
                    spring.setFinalPosition(access$1400 >= ((float) i) / 2.0f ? (((float) i) - (((float) PipVideoOverlay.this.getSuggestedWidth()) * PipVideoOverlay.this.scaleFactor)) - ((float) AndroidUtilities.dp(16.0f)) : (float) AndroidUtilities.dp(16.0f));
                    PipVideoOverlay.this.pipXSpring.start();
                    ((SpringAnimation) PipVideoOverlay.this.pipYSpring.setStartValue(PipVideoOverlay.this.pipY)).getSpring().setFinalPosition(MathUtils.clamp(PipVideoOverlay.this.pipY, (float) AndroidUtilities.dp(16.0f), (((float) AndroidUtilities.displaySize.y) - (((float) PipVideoOverlay.this.getSuggestedHeight()) * PipVideoOverlay.this.scaleFactor)) - ((float) AndroidUtilities.dp(16.0f))));
                    PipVideoOverlay.this.pipYSpring.start();
                }
            }

            public void draw(Canvas canvas) {
                if (Build.VERSION.SDK_INT >= 21) {
                    super.draw(canvas);
                    return;
                }
                canvas.save();
                canvas.clipPath(this.path);
                super.draw(canvas);
                canvas.restore();
            }

            /* access modifiers changed from: protected */
            public void onSizeChanged(int i, int i2, int i3, int i4) {
                super.onSizeChanged(i, i2, i3, i4);
                this.path.rewind();
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set(0.0f, 0.0f, (float) i, (float) i2);
                this.path.addRoundRect(rectF, (float) AndroidUtilities.dp(10.0f), (float) AndroidUtilities.dp(10.0f), Path.Direction.CW);
            }
        };
        AnonymousClass6 r7 = new ViewGroup(context2) {
            /* access modifiers changed from: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                PipVideoOverlay.this.contentFrameLayout.layout(0, 0, PipVideoOverlay.this.pipWidth, PipVideoOverlay.this.pipHeight);
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                setMeasuredDimension(View.MeasureSpec.getSize(i), View.MeasureSpec.getSize(i2));
                PipVideoOverlay.this.contentFrameLayout.measure(View.MeasureSpec.makeMeasureSpec(PipVideoOverlay.this.pipWidth, NUM), View.MeasureSpec.makeMeasureSpec(PipVideoOverlay.this.pipHeight, NUM));
            }

            public void draw(Canvas canvas) {
                canvas.save();
                canvas.scale(((float) PipVideoOverlay.this.pipWidth) / ((float) PipVideoOverlay.this.contentFrameLayout.getWidth()), ((float) PipVideoOverlay.this.pipHeight) / ((float) PipVideoOverlay.this.contentFrameLayout.getHeight()));
                super.draw(canvas);
                canvas.restore();
            }
        };
        this.contentView = r7;
        r7.addView(this.contentFrameLayout, LayoutHelper.createFrame(-1, -1.0f));
        if (i3 >= 21) {
            this.contentFrameLayout.setOutlineProvider(new ViewOutlineProvider(this) {
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight(), (float) AndroidUtilities.dp(10.0f));
                }
            });
            this.contentFrameLayout.setClipToOutline(true);
        }
        this.contentFrameLayout.setBackgroundColor(Theme.getColor("voipgroup_actionBar"));
        this.innerView = view;
        if (view.getParent() != null) {
            ((ViewGroup) this.innerView.getParent()).removeView(this.innerView);
        }
        this.contentFrameLayout.addView(this.innerView, LayoutHelper.createFrame(-1, -1.0f));
        this.videoForwardDrawable.setDelegate(new VideoForwardDrawable.VideoForwardDrawableDelegate() {
            public void onAnimationEnd() {
            }

            public void invalidate() {
                PipVideoOverlay.this.controlsView.invalidate();
            }
        });
        AnonymousClass9 r72 = new FrameLayout(context2) {
            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                if (PipVideoOverlay.this.videoForwardDrawable.isAnimating()) {
                    PipVideoOverlay.this.videoForwardDrawable.setBounds(getLeft(), getTop(), getRight(), getBottom());
                    PipVideoOverlay.this.videoForwardDrawable.draw(canvas);
                }
            }
        };
        this.controlsView = r72;
        r72.setWillNotDraw(false);
        this.controlsView.setAlpha(0.0f);
        View view2 = new View(context2);
        view2.setBackgroundColor(NUM);
        this.controlsView.addView(view2, LayoutHelper.createFrame(-1, -1.0f));
        int dp = AndroidUtilities.dp(8.0f);
        ImageView imageView = new ImageView(context2);
        imageView.setImageResource(R.drawable.pip_video_close);
        imageView.setColorFilter(Theme.getColor("voipgroup_actionBarItems"), PorterDuff.Mode.MULTIPLY);
        imageView.setBackground(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21")));
        imageView.setPadding(dp, dp, dp, dp);
        imageView.setOnClickListener(PipVideoOverlay$$ExternalSyntheticLambda3.INSTANCE);
        float f = (float) 4;
        float f2 = (float) 38;
        float f3 = f;
        this.controlsView.addView(imageView, LayoutHelper.createFrame(38, f2, 5, 0.0f, f3, f, 0.0f));
        ImageView imageView2 = new ImageView(context2);
        imageView2.setImageResource(R.drawable.pip_video_expand);
        imageView2.setColorFilter(Theme.getColor("voipgroup_actionBarItems"), PorterDuff.Mode.MULTIPLY);
        imageView2.setBackground(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21")));
        imageView2.setPadding(dp, dp, dp, dp);
        imageView2.setOnClickListener(new PipVideoOverlay$$ExternalSyntheticLambda2(this, z3));
        this.controlsView.addView(imageView2, LayoutHelper.createFrame(38, f2, 5, 0.0f, f3, (float) 48, 0.0f));
        ImageView imageView3 = new ImageView(context2);
        this.playPauseButton = imageView3;
        imageView3.setColorFilter(Theme.getColor("voipgroup_actionBarItems"), PorterDuff.Mode.MULTIPLY);
        this.playPauseButton.setBackground(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21")));
        this.playPauseButton.setOnClickListener(new PipVideoOverlay$$ExternalSyntheticLambda1(this));
        View view3 = this.innerView;
        boolean z4 = (view3 instanceof WebView) || (view3 instanceof PhotoViewerWebView);
        this.isWebView = z4;
        this.playPauseButton.setVisibility((!z4 || ((photoViewerWebView3 = this.photoViewerWebView) != null && photoViewerWebView3.isControllable())) ? 0 : 8);
        this.controlsView.addView(this.playPauseButton, LayoutHelper.createFrame(38, 38, 17));
        VideoProgressView videoProgressView2 = new VideoProgressView(context2);
        this.videoProgressView = videoProgressView2;
        this.controlsView.addView(videoProgressView2, LayoutHelper.createFrame(-1, -1.0f));
        this.contentFrameLayout.addView(this.controlsView, LayoutHelper.createFrame(-1, -1.0f));
        if (z3) {
            context = activity;
        } else {
            context = ApplicationLoader.applicationContext;
        }
        this.windowManager = (WindowManager) context.getSystemService("window");
        WindowManager.LayoutParams createWindowLayoutParams = createWindowLayoutParams(z);
        this.windowLayoutParams = createWindowLayoutParams;
        int i4 = this.pipWidth;
        createWindowLayoutParams.width = i4;
        createWindowLayoutParams.height = this.pipHeight;
        if (access$300 != -1.0f) {
            float f4 = access$300 + (((float) i4) / 2.0f);
            int i5 = AndroidUtilities.displaySize.x;
            float dp2 = f4 >= ((float) i5) / 2.0f ? (float) ((i5 - i4) - AndroidUtilities.dp(16.0f)) : (float) AndroidUtilities.dp(16.0f);
            this.pipX = dp2;
            createWindowLayoutParams.x = (int) dp2;
        } else {
            float dp3 = (float) ((AndroidUtilities.displaySize.x - i4) - AndroidUtilities.dp(16.0f));
            this.pipX = dp3;
            createWindowLayoutParams.x = (int) dp3;
        }
        if (access$400 != -1.0f) {
            WindowManager.LayoutParams layoutParams = this.windowLayoutParams;
            float clamp = MathUtils.clamp(access$400, (float) AndroidUtilities.dp(16.0f), (float) ((AndroidUtilities.displaySize.y - AndroidUtilities.dp(16.0f)) - this.pipHeight));
            this.pipY = clamp;
            layoutParams.y = (int) clamp;
        } else {
            WindowManager.LayoutParams layoutParams2 = this.windowLayoutParams;
            float dp4 = (float) AndroidUtilities.dp(16.0f);
            this.pipY = dp4;
            layoutParams2.y = (int) dp4;
        }
        WindowManager.LayoutParams layoutParams3 = this.windowLayoutParams;
        layoutParams3.dimAmount = 0.0f;
        layoutParams3.flags = 520;
        if (z2) {
            this.windowManager.addView(this.contentView, layoutParams3);
            return true;
        }
        this.contentView.setAlpha(0.0f);
        this.contentView.setScaleX(0.1f);
        this.contentView.setScaleY(0.1f);
        this.windowManager.addView(this.contentView, this.windowLayoutParams);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(250);
        animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
        animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.contentView, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.contentView, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.contentView, View.SCALE_Y, new float[]{1.0f})});
        animatorSet.start();
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showInternal$7(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        getPipConfig().setPipX(f);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showInternal$8(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        getPipConfig().setPipY(f);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showInternal$10(boolean z, View view) {
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses;
        boolean z2 = true;
        if (Build.VERSION.SDK_INT >= 21 && (runningAppProcesses = ((ActivityManager) view.getContext().getSystemService("activity")).getRunningAppProcesses()) != null && !runningAppProcesses.isEmpty() && runningAppProcesses.get(0).importance != 100) {
            z2 = false;
        }
        if (z || (z2 && LaunchActivity.isResumed)) {
            EmbedBottomSheet embedBottomSheet = this.parentSheet;
            if (embedBottomSheet != null) {
                embedBottomSheet.exitFromPip();
                return;
            }
            PhotoViewer photoViewer2 = this.photoViewer;
            if (photoViewer2 != null) {
                photoViewer2.exitFromPip();
                return;
            }
            return;
        }
        view.getClass();
        LaunchActivity.onResumeStaticCallback = new ChatActivityEnterView$$ExternalSyntheticLambda33(view);
        Context context = ApplicationLoader.applicationContext;
        Intent intent = new Intent(context, LaunchActivity.class);
        intent.addFlags(NUM);
        context.startActivity(intent);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showInternal$11(View view) {
        PhotoViewer photoViewer2 = this.photoViewer;
        if (photoViewer2 != null) {
            PhotoViewerWebView photoViewerWebView2 = this.photoViewerWebView;
            if (photoViewerWebView2 == null) {
                VideoPlayer videoPlayer = photoViewer2.getVideoPlayer();
                if (videoPlayer != null) {
                    if (videoPlayer.isPlaying()) {
                        videoPlayer.pause();
                    } else {
                        videoPlayer.play();
                    }
                } else {
                    return;
                }
            } else if (photoViewerWebView2.isPlaying()) {
                this.photoViewerWebView.pauseVideo();
            } else {
                this.photoViewerWebView.playVideo();
            }
            updatePlayButton();
        }
    }

    @SuppressLint({"WrongConstant"})
    private WindowManager.LayoutParams createWindowLayoutParams(boolean z) {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.gravity = 51;
        layoutParams.format = -3;
        if (z || !AndroidUtilities.checkInlinePermissions(ApplicationLoader.applicationContext)) {
            layoutParams.type = 99;
        } else if (Build.VERSION.SDK_INT >= 26) {
            layoutParams.type = 2038;
        } else {
            layoutParams.type = 2003;
        }
        layoutParams.flags = 520;
        return layoutParams;
    }

    private final class VideoProgressView extends View {
        private Paint bufferPaint = new Paint();
        private Paint progressPaint = new Paint();

        public VideoProgressView(Context context) {
            super(context);
            this.progressPaint.setColor(-1);
            this.progressPaint.setStyle(Paint.Style.STROKE);
            this.progressPaint.setStrokeCap(Paint.Cap.ROUND);
            this.progressPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
            this.bufferPaint.setColor(this.progressPaint.getColor());
            this.bufferPaint.setAlpha((int) (((float) this.progressPaint.getAlpha()) * 0.3f));
            this.bufferPaint.setStyle(Paint.Style.STROKE);
            this.bufferPaint.setStrokeCap(Paint.Cap.ROUND);
            this.bufferPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (!PipVideoOverlay.this.isWebView || (PipVideoOverlay.this.photoViewerWebView != null && PipVideoOverlay.this.photoViewerWebView.isControllable())) {
                int width = getWidth();
                int dp = AndroidUtilities.dp(10.0f);
                float f = (float) ((width - dp) - dp);
                int access$5100 = ((int) (PipVideoOverlay.this.videoProgress * f)) + dp;
                float height = (float) (getHeight() - AndroidUtilities.dp(8.0f));
                if (PipVideoOverlay.this.bufferProgress != 0.0f) {
                    float f2 = (float) dp;
                    canvas.drawLine(f2, height, f2 + (f * PipVideoOverlay.this.bufferProgress), height, this.bufferPaint);
                }
                canvas.drawLine((float) dp, height, (float) access$5100, height, this.progressPaint);
            }
        }
    }

    private static final class PipConfig {
        private SharedPreferences mPrefs;

        private PipConfig(int i, int i2) {
            Context context = ApplicationLoader.applicationContext;
            this.mPrefs = context.getSharedPreferences("pip_layout_" + i + "_" + i2, 0);
        }

        /* access modifiers changed from: private */
        public void setPipX(float f) {
            this.mPrefs.edit().putFloat("x", f).apply();
        }

        /* access modifiers changed from: private */
        public void setPipY(float f) {
            this.mPrefs.edit().putFloat("y", f).apply();
        }

        /* access modifiers changed from: private */
        public float getScaleFactor() {
            return this.mPrefs.getFloat("scale_factor", 1.0f);
        }

        /* access modifiers changed from: private */
        public float getPipX() {
            return this.mPrefs.getFloat("x", -1.0f);
        }

        /* access modifiers changed from: private */
        public float getPipY() {
            return this.mPrefs.getFloat("y", -1.0f);
        }
    }
}
