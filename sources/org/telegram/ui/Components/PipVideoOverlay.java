package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
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
import android.graphics.PorterDuff;
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
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.GestureDetectorFixDoubleTap;
import org.telegram.ui.Components.VideoForwardDrawable;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PhotoViewer;

public class PipVideoOverlay {
    public static final boolean IS_TRANSITION_ANIMATION_SUPPORTED = true;
    private static final FloatPropertyCompat<PipVideoOverlay> PIP_X_PROPERTY = new SimpleFloatPropertyCompat("pipX", PipVideoOverlay$$ExternalSyntheticLambda2.INSTANCE, PipVideoOverlay$$ExternalSyntheticLambda4.INSTANCE);
    private static final FloatPropertyCompat<PipVideoOverlay> PIP_Y_PROPERTY = new SimpleFloatPropertyCompat("pipY", PipVideoOverlay$$ExternalSyntheticLambda3.INSTANCE, PipVideoOverlay$$ExternalSyntheticLambda5.INSTANCE);
    public static final float ROUNDED_CORNERS_DP = 10.0f;
    private static final float SIDE_PADDING_DP = 16.0f;
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
    public Runnable dismissControlsCallback = new PipVideoOverlay$$ExternalSyntheticLambda12(this);
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
    public Runnable longClickCallback = new PipVideoOverlay$$ExternalSyntheticLambda13(this);
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
    private Runnable progressRunnable = new PipVideoOverlay$$ExternalSyntheticLambda11(this);
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

    static /* synthetic */ void lambda$static$1(PipVideoOverlay obj, float value) {
        WindowManager.LayoutParams layoutParams = obj.windowLayoutParams;
        obj.pipX = value;
        layoutParams.x = (int) value;
        try {
            obj.windowManager.updateViewLayout(obj.contentView, obj.windowLayoutParams);
        } catch (IllegalArgumentException e) {
            obj.pipXSpring.cancel();
        }
    }

    static /* synthetic */ void lambda$static$3(PipVideoOverlay obj, float value) {
        WindowManager.LayoutParams layoutParams = obj.windowLayoutParams;
        obj.pipY = value;
        layoutParams.y = (int) value;
        try {
            obj.windowManager.updateViewLayout(obj.contentView, obj.windowLayoutParams);
        } catch (IllegalArgumentException e) {
            obj.pipYSpring.cancel();
        }
    }

    /* renamed from: lambda$new$4$org-telegram-ui-Components-PipVideoOverlay  reason: not valid java name */
    public /* synthetic */ void m1211lambda$new$4$orgtelegramuiComponentsPipVideoOverlay() {
        VideoPlayer videoPlayer;
        PhotoViewer photoViewer2 = this.photoViewer;
        if (photoViewer2 != null && (videoPlayer = photoViewer2.getVideoPlayer()) != null) {
            this.videoProgress = ((float) videoPlayer.getCurrentPosition()) / ((float) videoPlayer.getDuration());
            if (this.photoViewer == null) {
                this.bufferProgress = ((float) videoPlayer.getBufferedPosition()) / ((float) videoPlayer.getDuration());
            }
            this.videoProgressView.invalidate();
            AndroidUtilities.runOnUIThread(this.progressRunnable, 500);
        }
    }

    /* renamed from: lambda$new$5$org-telegram-ui-Components-PipVideoOverlay  reason: not valid java name */
    public /* synthetic */ void m1212lambda$new$5$orgtelegramuiComponentsPipVideoOverlay() {
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

    public static void onUpdateRewindProgressUi(long timeDiff, float progress, boolean rewindByBackSeek) {
        instance.onUpdateRewindProgressUiInternal(timeDiff, progress, rewindByBackSeek);
    }

    /* access modifiers changed from: private */
    public void onUpdateRewindProgressUiInternal(long timeDiff, float progress, boolean rewindByBackSeek) {
        this.videoForwardDrawable.setTime(0);
        if (rewindByBackSeek) {
            this.videoProgress = progress;
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

    public static void onRewindStart(boolean rewindForward) {
        instance.onRewindStartInternal(rewindForward);
    }

    private void onRewindStartInternal(boolean rewindForward) {
        this.videoForwardDrawable.setOneShootAnimation(false);
        this.videoForwardDrawable.setLeftSide(!rewindForward);
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

    /* access modifiers changed from: protected */
    public void onLongClick() {
        PhotoViewer photoViewer2 = this.photoViewer;
        if (photoViewer2 != null && photoViewer2.getVideoPlayer() != null && !this.isDismissing && !this.isVideoCompleted && !this.isScrolling && !this.scaleGestureDetector.isInProgress() && this.canLongClick) {
            VideoPlayer videoPlayer = this.photoViewer.getVideoPlayer();
            boolean z = false;
            if (this.longClickStartPoint[0] >= ((float) getSuggestedWidth()) * this.scaleFactor * 0.5f) {
                z = true;
            }
            boolean forward = z;
            long current = videoPlayer.getCurrentPosition();
            long total = videoPlayer.getDuration();
            if (current != -9223372036854775807L && total >= 15000) {
                this.photoViewer.getVideoPlayerRewinder().startRewind(videoPlayer, forward, this.photoViewer.getCurrentVideoSpeed());
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
            this.pipConfig = new PipConfig(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y);
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

    private static int getSuggestedWidth(float ratio) {
        if (ratio >= 1.0f) {
            return (int) (((float) Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) * 0.35f);
        }
        return (int) (((float) Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) * 0.6f);
    }

    /* access modifiers changed from: private */
    public int getSuggestedHeight() {
        return getSuggestedHeight(getRatio());
    }

    private static int getSuggestedHeight(float ratio) {
        return (int) (((float) getSuggestedWidth(ratio)) * ratio);
    }

    private float getRatio() {
        if (this.aspectRatio == null) {
            this.aspectRatio = Float.valueOf(((float) this.mVideoHeight) / ((float) this.mVideoWidth));
            this.maxScaleFactor = ((float) (Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) - AndroidUtilities.dp(32.0f))) / ((float) getSuggestedWidth());
            this.videoForwardDrawable.setPlayScaleFactor(this.aspectRatio.floatValue() < 1.0f ? 0.6f : 0.45f);
        }
        return this.aspectRatio.floatValue();
    }

    /* access modifiers changed from: private */
    public void toggleControls(boolean show) {
        float[] fArr = new float[2];
        float f = 0.0f;
        fArr[0] = show ? 0.0f : 1.0f;
        if (show) {
            f = 1.0f;
        }
        fArr[1] = f;
        ValueAnimator duration = ValueAnimator.ofFloat(fArr).setDuration(200);
        this.controlsAnimator = duration;
        duration.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.controlsAnimator.addUpdateListener(new PipVideoOverlay$$ExternalSyntheticLambda0(this));
        this.controlsAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                ValueAnimator unused = PipVideoOverlay.this.controlsAnimator = null;
            }
        });
        this.controlsAnimator.start();
    }

    /* renamed from: lambda$toggleControls$6$org-telegram-ui-Components-PipVideoOverlay  reason: not valid java name */
    public /* synthetic */ void m1217x6d8831f6(ValueAnimator animation) {
        this.controlsView.setAlpha(((Float) animation.getAnimatedValue()).floatValue());
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

    public static void dismiss(boolean animate) {
        instance.dismissInternal(animate);
    }

    private void dismissInternal(boolean animate) {
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
            if (animate) {
                AndroidUtilities.runOnUIThread(new PipVideoOverlay$$ExternalSyntheticLambda1(this), 100);
                return;
            }
            AnimatorSet set = new AnimatorSet();
            set.setDuration(250);
            set.setInterpolator(CubicBezierInterpolator.DEFAULT);
            set.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.contentView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.contentView, View.SCALE_X, new float[]{0.1f}), ObjectAnimator.ofFloat(this.contentView, View.SCALE_Y, new float[]{0.1f})});
            set.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    PipVideoOverlay.this.onDismissedInternal();
                }
            });
            set.start();
        }
    }

    /* access modifiers changed from: private */
    public void onDismissedInternal() {
        try {
            if (this.controlsView.getParent() != null) {
                this.windowManager.removeViewImmediate(this.contentView);
            }
        } catch (IllegalArgumentException e) {
        }
        this.videoProgressView = null;
        this.innerView = null;
        this.photoViewer = null;
        this.parentSheet = null;
        this.consumingChild = null;
        this.isScrolling = false;
        this.isVisible = false;
        this.isDismissing = false;
        this.canLongClick = false;
        cancelRewind();
        AndroidUtilities.cancelRunOnUIThread(this.longClickCallback);
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
        VideoPlayer videoPlayer;
        PhotoViewer photoViewer2 = this.photoViewer;
        if (photoViewer2 != null && (videoPlayer = photoViewer2.getVideoPlayer()) != null && this.playPauseButton != null) {
            AndroidUtilities.cancelRunOnUIThread(this.progressRunnable);
            if (videoPlayer.isPlaying()) {
                this.playPauseButton.setImageResource(NUM);
                AndroidUtilities.runOnUIThread(this.progressRunnable, 500);
            } else if (this.isVideoCompleted) {
                this.playPauseButton.setImageResource(NUM);
            } else {
                this.playPauseButton.setImageResource(NUM);
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

    public static void setBufferedProgress(float progress) {
        PipVideoOverlay pipVideoOverlay = instance;
        pipVideoOverlay.bufferProgress = progress;
        VideoProgressView videoProgressView2 = pipVideoOverlay.videoProgressView;
        if (videoProgressView2 != null) {
            videoProgressView2.invalidate();
        }
    }

    public static void setParentSheet(EmbedBottomSheet parentSheet2) {
        instance.parentSheet = parentSheet2;
    }

    public static void setPhotoViewer(PhotoViewer photoViewer2) {
        PipVideoOverlay pipVideoOverlay = instance;
        pipVideoOverlay.photoViewer = photoViewer2;
        pipVideoOverlay.updatePlayButtonInternal();
    }

    public static Rect getPipRect(boolean inAnimation, float aspectRatio2) {
        Rect rect = new Rect();
        float ratio = 1.0f / aspectRatio2;
        PipVideoOverlay pipVideoOverlay = instance;
        if (!pipVideoOverlay.isVisible || inAnimation) {
            float savedPipX = pipVideoOverlay.getPipConfig().getPipX();
            float savedPipY = instance.getPipConfig().getPipY();
            float scaleFactor2 = instance.getPipConfig().getScaleFactor();
            rect.width = ((float) getSuggestedWidth(ratio)) * scaleFactor2;
            rect.height = ((float) getSuggestedHeight(ratio)) * scaleFactor2;
            if (savedPipX != -1.0f) {
                rect.x = (rect.width / 2.0f) + savedPipX >= ((float) AndroidUtilities.displaySize.x) / 2.0f ? (((float) AndroidUtilities.displaySize.x) - rect.width) - ((float) AndroidUtilities.dp(16.0f)) : (float) AndroidUtilities.dp(16.0f);
            } else {
                rect.x = (((float) AndroidUtilities.displaySize.x) - rect.width) - ((float) AndroidUtilities.dp(16.0f));
            }
            if (savedPipY != -1.0f) {
                rect.y = MathUtils.clamp(savedPipY, (float) AndroidUtilities.dp(16.0f), ((float) (AndroidUtilities.displaySize.y - AndroidUtilities.dp(16.0f))) - rect.height) + ((float) AndroidUtilities.statusBarHeight);
            } else {
                rect.y = (float) (AndroidUtilities.dp(16.0f) + AndroidUtilities.statusBarHeight);
            }
            return rect;
        }
        rect.x = pipVideoOverlay.pipX;
        rect.y = instance.pipY + ((float) AndroidUtilities.statusBarHeight);
        rect.width = (float) instance.pipWidth;
        rect.height = (float) instance.pipHeight;
        return rect;
    }

    public static boolean show(boolean inAppOnly, Activity activity, View pipContentView, int videoWidth, int videoHeight) {
        return show(inAppOnly, activity, pipContentView, videoWidth, videoHeight, false);
    }

    public static boolean show(boolean inAppOnly, Activity activity, View pipContentView, int videoWidth, int videoHeight, boolean animate) {
        return instance.showInternal(inAppOnly, activity, pipContentView, videoWidth, videoHeight, animate);
    }

    private boolean showInternal(boolean inAppOnly, Activity activity, View pipContentView, int videoWidth, int videoHeight, boolean animate) {
        boolean z = inAppOnly;
        if (this.isVisible) {
            return false;
        }
        this.isVisible = true;
        this.mVideoWidth = videoWidth;
        this.mVideoHeight = videoHeight;
        this.aspectRatio = null;
        float savedPipX = getPipConfig().getPipX();
        float savedPipY = getPipConfig().getPipY();
        this.scaleFactor = getPipConfig().getScaleFactor();
        this.pipWidth = (int) (((float) getSuggestedWidth()) * this.scaleFactor);
        this.pipHeight = (int) (((float) getSuggestedHeight()) * this.scaleFactor);
        this.isShowingControls = false;
        this.pipXSpring = (SpringAnimation) new SpringAnimation(this, PIP_X_PROPERTY).setSpring(new SpringForce().setDampingRatio(0.75f).setStiffness(650.0f)).addEndListener(new PipVideoOverlay$$ExternalSyntheticLambda9(this));
        this.pipYSpring = (SpringAnimation) new SpringAnimation(this, PIP_Y_PROPERTY).setSpring(new SpringForce().setDampingRatio(0.75f).setStiffness(650.0f)).addEndListener(new PipVideoOverlay$$ExternalSyntheticLambda10(this));
        Context context = ApplicationLoader.applicationContext;
        final int touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        this.scaleGestureDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.OnScaleGestureListener() {
            public boolean onScale(ScaleGestureDetector detector) {
                PipVideoOverlay pipVideoOverlay = PipVideoOverlay.this;
                float unused = pipVideoOverlay.scaleFactor = MathUtils.clamp(pipVideoOverlay.scaleFactor * detector.getScaleFactor(), PipVideoOverlay.this.minScaleFactor, PipVideoOverlay.this.maxScaleFactor);
                PipVideoOverlay pipVideoOverlay2 = PipVideoOverlay.this;
                int unused2 = pipVideoOverlay2.pipWidth = (int) (((float) pipVideoOverlay2.getSuggestedWidth()) * PipVideoOverlay.this.scaleFactor);
                PipVideoOverlay pipVideoOverlay3 = PipVideoOverlay.this;
                int unused3 = pipVideoOverlay3.pipHeight = (int) (((float) pipVideoOverlay3.getSuggestedHeight()) * PipVideoOverlay.this.scaleFactor);
                AndroidUtilities.runOnUIThread(new PipVideoOverlay$3$$ExternalSyntheticLambda0(this));
                float finalX = (float) (detector.getFocusX() >= ((float) AndroidUtilities.displaySize.x) / 2.0f ? (AndroidUtilities.displaySize.x - PipVideoOverlay.this.pipWidth) - AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(16.0f));
                if (!PipVideoOverlay.this.pipXSpring.isRunning()) {
                    ((SpringAnimation) PipVideoOverlay.this.pipXSpring.setStartValue(PipVideoOverlay.this.pipX)).getSpring().setFinalPosition(finalX);
                } else {
                    PipVideoOverlay.this.pipXSpring.getSpring().setFinalPosition(finalX);
                }
                PipVideoOverlay.this.pipXSpring.start();
                float finalY = MathUtils.clamp(detector.getFocusY() - (((float) PipVideoOverlay.this.pipHeight) / 2.0f), (float) AndroidUtilities.dp(16.0f), (float) ((AndroidUtilities.displaySize.y - PipVideoOverlay.this.pipHeight) - AndroidUtilities.dp(16.0f)));
                if (!PipVideoOverlay.this.pipYSpring.isRunning()) {
                    ((SpringAnimation) PipVideoOverlay.this.pipYSpring.setStartValue(PipVideoOverlay.this.pipY)).getSpring().setFinalPosition(finalY);
                } else {
                    PipVideoOverlay.this.pipYSpring.getSpring().setFinalPosition(finalY);
                }
                PipVideoOverlay.this.pipYSpring.start();
                return true;
            }

            /* renamed from: lambda$onScale$0$org-telegram-ui-Components-PipVideoOverlay$3  reason: not valid java name */
            public /* synthetic */ void m1218lambda$onScale$0$orgtelegramuiComponentsPipVideoOverlay$3() {
                PipVideoOverlay.this.contentView.invalidate();
                PipVideoOverlay.this.contentFrameLayout.requestLayout();
            }

            public boolean onScaleBegin(ScaleGestureDetector detector) {
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

            public void onScaleEnd(ScaleGestureDetector detector) {
                if (PipVideoOverlay.this.pipXSpring.isRunning() || PipVideoOverlay.this.pipYSpring.isRunning()) {
                    final List<SpringAnimation> springs = new ArrayList<>();
                    DynamicAnimation.OnAnimationEndListener endListener = new DynamicAnimation.OnAnimationEndListener() {
                        public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
                            animation.removeEndListener(this);
                            springs.add((SpringAnimation) animation);
                            if (springs.size() == 2) {
                                AnonymousClass3.this.updateLayout();
                            }
                        }
                    };
                    if (!PipVideoOverlay.this.pipXSpring.isRunning()) {
                        springs.add(PipVideoOverlay.this.pipXSpring);
                    } else {
                        PipVideoOverlay.this.pipXSpring.addEndListener(endListener);
                    }
                    if (!PipVideoOverlay.this.pipYSpring.isRunning()) {
                        springs.add(PipVideoOverlay.this.pipYSpring);
                    } else {
                        PipVideoOverlay.this.pipYSpring.addEndListener(endListener);
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
                } catch (IllegalArgumentException e) {
                }
            }
        });
        if (Build.VERSION.SDK_INT >= 19) {
            this.scaleGestureDetector.setQuickScaleEnabled(false);
        }
        if (Build.VERSION.SDK_INT >= 23) {
            this.scaleGestureDetector.setStylusScaleEnabled(false);
        }
        this.gestureDetector = new GestureDetectorFixDoubleTap(context, new GestureDetectorFixDoubleTap.OnGestureListener() {
            private float startPipX;
            private float startPipY;

            public boolean onDown(MotionEvent e) {
                if (PipVideoOverlay.this.isShowingControls) {
                    for (int i = 1; i < PipVideoOverlay.this.contentFrameLayout.getChildCount(); i++) {
                        View child = PipVideoOverlay.this.contentFrameLayout.getChildAt(i);
                        if (child.dispatchTouchEvent(e)) {
                            View unused = PipVideoOverlay.this.consumingChild = child;
                            return true;
                        }
                    }
                }
                this.startPipX = PipVideoOverlay.this.pipX;
                this.startPipY = PipVideoOverlay.this.pipY;
                return true;
            }

            public boolean onSingleTapConfirmed(MotionEvent e) {
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

            public boolean onDoubleTap(MotionEvent e) {
                long current;
                boolean z = false;
                if (PipVideoOverlay.this.photoViewer == null || PipVideoOverlay.this.photoViewer.getVideoPlayer() == null || PipVideoOverlay.this.isDismissing || PipVideoOverlay.this.isVideoCompleted || PipVideoOverlay.this.isScrolling || PipVideoOverlay.this.scaleGestureDetector.isInProgress() || !PipVideoOverlay.this.canLongClick) {
                    return false;
                }
                VideoPlayer videoPlayer = PipVideoOverlay.this.photoViewer.getVideoPlayer();
                boolean forward = e.getX() >= (((float) PipVideoOverlay.this.getSuggestedWidth()) * PipVideoOverlay.this.scaleFactor) * 0.5f;
                long current2 = videoPlayer.getCurrentPosition();
                long total = videoPlayer.getDuration();
                if (current2 == -9223372036854775807L || total < 15000) {
                    return false;
                }
                long old = current2;
                long j = 10000;
                if (forward) {
                    current = current2 + 10000;
                } else {
                    current = current2 - 10000;
                }
                if (old == current) {
                    return false;
                }
                boolean apply = true;
                if (current > total) {
                    current = total;
                } else if (current < 0) {
                    if (current < -9000) {
                        apply = false;
                    }
                    current = 0;
                }
                if (apply) {
                    PipVideoOverlay.this.videoForwardDrawable.setOneShootAnimation(true);
                    VideoForwardDrawable access$3600 = PipVideoOverlay.this.videoForwardDrawable;
                    if (!forward) {
                        z = true;
                    }
                    access$3600.setLeftSide(z);
                    PipVideoOverlay.this.videoForwardDrawable.addTime(10000);
                    videoPlayer.seekTo(current);
                    PipVideoOverlay pipVideoOverlay = PipVideoOverlay.this;
                    if (!forward) {
                        j = -10000;
                    }
                    pipVideoOverlay.onUpdateRewindProgressUiInternal(j, ((float) current) / ((float) total), true);
                    if (!PipVideoOverlay.this.isShowingControls) {
                        PipVideoOverlay pipVideoOverlay2 = PipVideoOverlay.this;
                        pipVideoOverlay2.toggleControls(pipVideoOverlay2.isShowingControls = true);
                        if (!PipVideoOverlay.this.postedDismissControls) {
                            boolean unused = PipVideoOverlay.this.postedDismissControls = true;
                            AndroidUtilities.runOnUIThread(PipVideoOverlay.this.dismissControlsCallback, 2500);
                        }
                    }
                }
                return true;
            }

            public boolean onSingleTapUp(MotionEvent e) {
                if (!hasDoubleTap()) {
                    return onSingleTapConfirmed(e);
                }
                return super.onSingleTapUp(e);
            }

            public boolean hasDoubleTap() {
                if (PipVideoOverlay.this.photoViewer == null || PipVideoOverlay.this.photoViewer.getVideoPlayer() == null || PipVideoOverlay.this.isDismissing || PipVideoOverlay.this.isVideoCompleted || PipVideoOverlay.this.isScrolling || PipVideoOverlay.this.scaleGestureDetector.isInProgress() || !PipVideoOverlay.this.canLongClick) {
                    return false;
                }
                VideoPlayer videoPlayer = PipVideoOverlay.this.photoViewer.getVideoPlayer();
                long current = videoPlayer.getCurrentPosition();
                long total = videoPlayer.getDuration();
                if (current == -9223372036854775807L || total < 15000) {
                    return false;
                }
                return true;
            }

            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (!PipVideoOverlay.this.isScrolling || PipVideoOverlay.this.isScrollDisallowed) {
                    return false;
                }
                ((SpringAnimation) ((SpringAnimation) PipVideoOverlay.this.pipXSpring.setStartVelocity(velocityX)).setStartValue(PipVideoOverlay.this.pipX)).getSpring().setFinalPosition((float) ((PipVideoOverlay.this.pipX + (((float) PipVideoOverlay.this.pipWidth) / 2.0f)) + (velocityX / 7.0f) >= ((float) AndroidUtilities.displaySize.x) / 2.0f ? (AndroidUtilities.displaySize.x - PipVideoOverlay.this.pipWidth) - AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(16.0f)));
                PipVideoOverlay.this.pipXSpring.start();
                ((SpringAnimation) ((SpringAnimation) PipVideoOverlay.this.pipYSpring.setStartVelocity(velocityX)).setStartValue(PipVideoOverlay.this.pipY)).getSpring().setFinalPosition(MathUtils.clamp(PipVideoOverlay.this.pipY + (velocityY / 10.0f), (float) AndroidUtilities.dp(16.0f), (float) ((AndroidUtilities.displaySize.y - PipVideoOverlay.this.pipHeight) - AndroidUtilities.dp(16.0f))));
                PipVideoOverlay.this.pipYSpring.start();
                return true;
            }

            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                int i;
                int i2;
                if (!PipVideoOverlay.this.isScrolling && PipVideoOverlay.this.controlsAnimator == null && !PipVideoOverlay.this.isScrollDisallowed && (Math.abs(distanceX) >= ((float) touchSlop) || Math.abs(distanceY) >= ((float) touchSlop))) {
                    boolean unused = PipVideoOverlay.this.isScrolling = true;
                    PipVideoOverlay.this.pipXSpring.cancel();
                    PipVideoOverlay.this.pipYSpring.cancel();
                    boolean unused2 = PipVideoOverlay.this.canLongClick = false;
                    PipVideoOverlay.this.cancelRewind();
                    AndroidUtilities.cancelRunOnUIThread(PipVideoOverlay.this.longClickCallback);
                }
                if (PipVideoOverlay.this.isScrolling) {
                    float wasPipX = PipVideoOverlay.this.pipX;
                    float newPipX = (this.startPipX + e2.getRawX()) - e1.getRawX();
                    float unused3 = PipVideoOverlay.this.pipY = (this.startPipY + e2.getRawY()) - e1.getRawY();
                    if (newPipX <= ((float) (-PipVideoOverlay.this.pipWidth)) * 0.25f || newPipX >= ((float) AndroidUtilities.displaySize.x) - (((float) PipVideoOverlay.this.pipWidth) * 0.75f)) {
                        if (!PipVideoOverlay.this.onSideToDismiss) {
                            SpringForce spring = ((SpringAnimation) PipVideoOverlay.this.pipXSpring.setStartValue(wasPipX)).getSpring();
                            if ((((float) PipVideoOverlay.this.pipWidth) / 2.0f) + newPipX >= ((float) AndroidUtilities.displaySize.x) / 2.0f) {
                                i2 = AndroidUtilities.displaySize.x;
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
                            PipVideoOverlay.this.pipXSpring.addEndListener(new PipVideoOverlay$4$$ExternalSyntheticLambda0(this, newPipX));
                            ((SpringAnimation) PipVideoOverlay.this.pipXSpring.setStartValue(wasPipX)).getSpring().setFinalPosition(newPipX);
                            PipVideoOverlay.this.pipXSpring.start();
                        }
                        boolean unused5 = PipVideoOverlay.this.onSideToDismiss = false;
                    } else {
                        if (PipVideoOverlay.this.pipXSpring.isRunning()) {
                            PipVideoOverlay.this.pipXSpring.getSpring().setFinalPosition(newPipX);
                        } else {
                            PipVideoOverlay.this.windowLayoutParams.x = (int) PipVideoOverlay.this.pipX = newPipX;
                            PipVideoOverlay.this.getPipConfig().setPipX(newPipX);
                        }
                        PipVideoOverlay.this.windowLayoutParams.y = (int) PipVideoOverlay.this.pipY;
                        PipVideoOverlay.this.getPipConfig().setPipY(PipVideoOverlay.this.pipY);
                        PipVideoOverlay.this.windowManager.updateViewLayout(PipVideoOverlay.this.contentView, PipVideoOverlay.this.windowLayoutParams);
                    }
                }
                return true;
            }

            /* renamed from: lambda$onScroll$0$org-telegram-ui-Components-PipVideoOverlay$4  reason: not valid java name */
            public /* synthetic */ void m1219lambda$onScroll$0$orgtelegramuiComponentsPipVideoOverlay$4(float newPipX, DynamicAnimation animation, boolean canceled, float value, float velocity) {
                if (!canceled) {
                    PipVideoOverlay.this.pipXSpring.getSpring().setFinalPosition((float) ((((float) PipVideoOverlay.this.pipWidth) / 2.0f) + newPipX >= ((float) AndroidUtilities.displaySize.x) / 2.0f ? (AndroidUtilities.displaySize.x - PipVideoOverlay.this.pipWidth) - AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(16.0f)));
                }
            }
        });
        this.contentFrameLayout = new FrameLayout(context) {
            private Path path = new Path();

            public boolean dispatchTouchEvent(MotionEvent ev) {
                int action = ev.getActionMasked();
                if (action == 0 || action == 5) {
                    if (ev.getPointerCount() == 1) {
                        boolean unused = PipVideoOverlay.this.canLongClick = true;
                        float[] unused2 = PipVideoOverlay.this.longClickStartPoint = new float[]{ev.getX(), ev.getY()};
                        AndroidUtilities.runOnUIThread(PipVideoOverlay.this.longClickCallback, 500);
                    } else {
                        boolean unused3 = PipVideoOverlay.this.canLongClick = false;
                        PipVideoOverlay.this.cancelRewind();
                        AndroidUtilities.cancelRunOnUIThread(PipVideoOverlay.this.longClickCallback);
                    }
                }
                if (action == 1 || action == 3 || action == 6) {
                    boolean unused4 = PipVideoOverlay.this.canLongClick = false;
                    PipVideoOverlay.this.cancelRewind();
                    AndroidUtilities.cancelRunOnUIThread(PipVideoOverlay.this.longClickCallback);
                }
                if (PipVideoOverlay.this.consumingChild != null) {
                    MotionEvent newEvent = MotionEvent.obtain(ev);
                    newEvent.offsetLocation(PipVideoOverlay.this.consumingChild.getX(), PipVideoOverlay.this.consumingChild.getY());
                    boolean consumed = PipVideoOverlay.this.consumingChild.dispatchTouchEvent(ev);
                    newEvent.recycle();
                    if (action == 1 || action == 3 || action == 6) {
                        View unused5 = PipVideoOverlay.this.consumingChild = null;
                    }
                    if (consumed) {
                        return true;
                    }
                }
                MotionEvent newEvent2 = MotionEvent.obtain(ev);
                newEvent2.offsetLocation(ev.getRawX() - ev.getX(), ev.getRawY() - ev.getY());
                boolean scaleDetector = PipVideoOverlay.this.scaleGestureDetector.onTouchEvent(newEvent2);
                newEvent2.recycle();
                boolean detector = !PipVideoOverlay.this.scaleGestureDetector.isInProgress() && PipVideoOverlay.this.gestureDetector.onTouchEvent(ev);
                if (action == 1 || action == 3 || action == 6) {
                    boolean unused6 = PipVideoOverlay.this.isScrolling = false;
                    boolean unused7 = PipVideoOverlay.this.isScrollDisallowed = false;
                    if (PipVideoOverlay.this.onSideToDismiss) {
                        boolean unused8 = PipVideoOverlay.this.onSideToDismiss = false;
                        PipVideoOverlay.dimissAndDestroy();
                    } else {
                        if (!PipVideoOverlay.this.pipXSpring.isRunning()) {
                            ((SpringAnimation) PipVideoOverlay.this.pipXSpring.setStartValue(PipVideoOverlay.this.pipX)).getSpring().setFinalPosition((float) (PipVideoOverlay.this.pipX + (((float) PipVideoOverlay.this.pipWidth) / 2.0f) >= ((float) AndroidUtilities.displaySize.x) / 2.0f ? (AndroidUtilities.displaySize.x - PipVideoOverlay.this.pipWidth) - AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(16.0f)));
                            PipVideoOverlay.this.pipXSpring.start();
                        }
                        if (!PipVideoOverlay.this.pipYSpring.isRunning()) {
                            ((SpringAnimation) PipVideoOverlay.this.pipYSpring.setStartValue(PipVideoOverlay.this.pipY)).getSpring().setFinalPosition(MathUtils.clamp(PipVideoOverlay.this.pipY, (float) AndroidUtilities.dp(16.0f), (float) ((AndroidUtilities.displaySize.y - PipVideoOverlay.this.pipHeight) - AndroidUtilities.dp(16.0f))));
                            PipVideoOverlay.this.pipYSpring.start();
                        }
                    }
                }
                if (scaleDetector || detector) {
                    return true;
                }
                return false;
            }

            /* access modifiers changed from: protected */
            public void onConfigurationChanged(Configuration newConfig) {
                AndroidUtilities.checkDisplaySize(getContext(), newConfig);
                PipConfig unused = PipVideoOverlay.this.pipConfig = null;
                if (((float) PipVideoOverlay.this.pipWidth) != ((float) PipVideoOverlay.this.getSuggestedWidth()) * PipVideoOverlay.this.scaleFactor || ((float) PipVideoOverlay.this.pipHeight) != ((float) PipVideoOverlay.this.getSuggestedHeight()) * PipVideoOverlay.this.scaleFactor) {
                    WindowManager.LayoutParams access$2200 = PipVideoOverlay.this.windowLayoutParams;
                    PipVideoOverlay pipVideoOverlay = PipVideoOverlay.this;
                    access$2200.width = pipVideoOverlay.pipWidth = (int) (((float) pipVideoOverlay.getSuggestedWidth()) * PipVideoOverlay.this.scaleFactor);
                    WindowManager.LayoutParams access$22002 = PipVideoOverlay.this.windowLayoutParams;
                    PipVideoOverlay pipVideoOverlay2 = PipVideoOverlay.this;
                    access$22002.height = pipVideoOverlay2.pipHeight = (int) (((float) pipVideoOverlay2.getSuggestedHeight()) * PipVideoOverlay.this.scaleFactor);
                    PipVideoOverlay.this.windowManager.updateViewLayout(PipVideoOverlay.this.contentView, PipVideoOverlay.this.windowLayoutParams);
                    ((SpringAnimation) PipVideoOverlay.this.pipXSpring.setStartValue(PipVideoOverlay.this.pipX)).getSpring().setFinalPosition(PipVideoOverlay.this.pipX + ((((float) PipVideoOverlay.this.getSuggestedWidth()) * PipVideoOverlay.this.scaleFactor) / 2.0f) >= ((float) AndroidUtilities.displaySize.x) / 2.0f ? (((float) AndroidUtilities.displaySize.x) - (((float) PipVideoOverlay.this.getSuggestedWidth()) * PipVideoOverlay.this.scaleFactor)) - ((float) AndroidUtilities.dp(16.0f)) : (float) AndroidUtilities.dp(16.0f));
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
            public void onSizeChanged(int w, int h, int oldw, int oldh) {
                super.onSizeChanged(w, h, oldw, oldh);
                this.path.rewind();
                AndroidUtilities.rectTmp.set(0.0f, 0.0f, (float) w, (float) h);
                this.path.addRoundRect(AndroidUtilities.rectTmp, (float) AndroidUtilities.dp(10.0f), (float) AndroidUtilities.dp(10.0f), Path.Direction.CW);
            }
        };
        AnonymousClass6 r11 = new ViewGroup(context) {
            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int l, int t, int r, int b) {
                PipVideoOverlay.this.contentFrameLayout.layout(0, 0, PipVideoOverlay.this.pipWidth, PipVideoOverlay.this.pipHeight);
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.getSize(heightMeasureSpec));
                PipVideoOverlay.this.contentFrameLayout.measure(View.MeasureSpec.makeMeasureSpec(PipVideoOverlay.this.pipWidth, NUM), View.MeasureSpec.makeMeasureSpec(PipVideoOverlay.this.pipHeight, NUM));
            }

            public void draw(Canvas canvas) {
                canvas.save();
                canvas.scale(((float) PipVideoOverlay.this.pipWidth) / ((float) PipVideoOverlay.this.contentFrameLayout.getWidth()), ((float) PipVideoOverlay.this.pipHeight) / ((float) PipVideoOverlay.this.contentFrameLayout.getHeight()));
                super.draw(canvas);
                canvas.restore();
            }
        };
        this.contentView = r11;
        r11.addView(this.contentFrameLayout, LayoutHelper.createFrame(-1, -1.0f));
        if (Build.VERSION.SDK_INT >= 21) {
            this.contentFrameLayout.setOutlineProvider(new ViewOutlineProvider() {
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight(), (float) AndroidUtilities.dp(10.0f));
                }
            });
            this.contentFrameLayout.setClipToOutline(true);
        }
        this.contentFrameLayout.setBackgroundColor(Theme.getColor("voipgroup_actionBar"));
        this.innerView = pipContentView;
        if (pipContentView.getParent() != null) {
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
        AnonymousClass9 r2 = new FrameLayout(context) {
            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                if (PipVideoOverlay.this.videoForwardDrawable.isAnimating()) {
                    PipVideoOverlay.this.videoForwardDrawable.setBounds(getLeft(), getTop(), getRight(), getBottom());
                    PipVideoOverlay.this.videoForwardDrawable.draw(canvas);
                }
            }
        };
        this.controlsView = r2;
        r2.setWillNotDraw(false);
        this.controlsView.setAlpha(0.0f);
        View scrim = new View(context);
        scrim.setBackgroundColor(NUM);
        this.controlsView.addView(scrim, LayoutHelper.createFrame(-1, -1.0f));
        int padding = AndroidUtilities.dp(8.0f);
        ImageView closeButton = new ImageView(context);
        closeButton.setImageResource(NUM);
        View view = scrim;
        closeButton.setColorFilter(Theme.getColor("voipgroup_actionBarItems"), PorterDuff.Mode.MULTIPLY);
        closeButton.setBackground(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21")));
        closeButton.setPadding(padding, padding, padding, padding);
        closeButton.setOnClickListener(PipVideoOverlay$$ExternalSyntheticLambda8.INSTANCE);
        int i = touchSlop;
        this.controlsView.addView(closeButton, LayoutHelper.createFrame(38, (float) 38, 5, 0.0f, (float) 4, (float) 4, 0.0f));
        ImageView expandButton = new ImageView(context);
        expandButton.setImageResource(NUM);
        expandButton.setColorFilter(Theme.getColor("voipgroup_actionBarItems"), PorterDuff.Mode.MULTIPLY);
        expandButton.setBackground(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21")));
        expandButton.setPadding(padding, padding, padding, padding);
        expandButton.setOnClickListener(new PipVideoOverlay$$ExternalSyntheticLambda7(this, z));
        int i2 = padding;
        this.controlsView.addView(expandButton, LayoutHelper.createFrame(38, (float) 38, 5, 0.0f, (float) 4, (float) (38 + 4 + 6), 0.0f));
        ImageView imageView = new ImageView(context);
        this.playPauseButton = imageView;
        imageView.setColorFilter(Theme.getColor("voipgroup_actionBarItems"), PorterDuff.Mode.MULTIPLY);
        this.playPauseButton.setBackground(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21")));
        this.playPauseButton.setOnClickListener(new PipVideoOverlay$$ExternalSyntheticLambda6(this));
        this.playPauseButton.setVisibility(this.innerView instanceof WebView ? 8 : 0);
        this.controlsView.addView(this.playPauseButton, LayoutHelper.createFrame(38, 38, 17));
        VideoProgressView videoProgressView2 = new VideoProgressView(context);
        this.videoProgressView = videoProgressView2;
        this.controlsView.addView(videoProgressView2, LayoutHelper.createFrame(-1, -1.0f));
        this.contentFrameLayout.addView(this.controlsView, LayoutHelper.createFrame(-1, -1.0f));
        this.windowManager = (WindowManager) (z ? activity : ApplicationLoader.applicationContext).getSystemService("window");
        WindowManager.LayoutParams createWindowLayoutParams = createWindowLayoutParams(inAppOnly);
        this.windowLayoutParams = createWindowLayoutParams;
        createWindowLayoutParams.width = this.pipWidth;
        this.windowLayoutParams.height = this.pipHeight;
        if (savedPipX != -1.0f) {
            WindowManager.LayoutParams layoutParams = this.windowLayoutParams;
            float dp = (float) ((((float) this.pipWidth) / 2.0f) + savedPipX >= ((float) AndroidUtilities.displaySize.x) / 2.0f ? (AndroidUtilities.displaySize.x - this.pipWidth) - AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(16.0f));
            this.pipX = dp;
            layoutParams.x = (int) dp;
        } else {
            WindowManager.LayoutParams layoutParams2 = this.windowLayoutParams;
            float dp2 = (float) ((AndroidUtilities.displaySize.x - this.pipWidth) - AndroidUtilities.dp(16.0f));
            this.pipX = dp2;
            layoutParams2.x = (int) dp2;
        }
        if (savedPipY != -1.0f) {
            WindowManager.LayoutParams layoutParams3 = this.windowLayoutParams;
            float clamp = MathUtils.clamp(savedPipY, (float) AndroidUtilities.dp(16.0f), (float) ((AndroidUtilities.displaySize.y - AndroidUtilities.dp(16.0f)) - this.pipHeight));
            this.pipY = clamp;
            layoutParams3.y = (int) clamp;
        } else {
            WindowManager.LayoutParams layoutParams4 = this.windowLayoutParams;
            float dp3 = (float) AndroidUtilities.dp(16.0f);
            this.pipY = dp3;
            layoutParams4.y = (int) dp3;
        }
        this.windowLayoutParams.dimAmount = 0.0f;
        this.windowLayoutParams.flags = 520;
        if (animate) {
            this.windowManager.addView(this.contentView, this.windowLayoutParams);
            ImageView imageView2 = expandButton;
            return true;
        }
        this.contentView.setAlpha(0.0f);
        this.contentView.setScaleX(0.1f);
        this.contentView.setScaleY(0.1f);
        this.windowManager.addView(this.contentView, this.windowLayoutParams);
        AnimatorSet set = new AnimatorSet();
        ImageView imageView3 = expandButton;
        set.setDuration(250);
        set.setInterpolator(CubicBezierInterpolator.DEFAULT);
        set.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.contentView, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.contentView, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.contentView, View.SCALE_Y, new float[]{1.0f})});
        set.start();
        return true;
    }

    /* renamed from: lambda$showInternal$7$org-telegram-ui-Components-PipVideoOverlay  reason: not valid java name */
    public /* synthetic */ void m1215lambda$showInternal$7$orgtelegramuiComponentsPipVideoOverlay(DynamicAnimation animation, boolean canceled, float value, float velocity) {
        getPipConfig().setPipX(value);
    }

    /* renamed from: lambda$showInternal$8$org-telegram-ui-Components-PipVideoOverlay  reason: not valid java name */
    public /* synthetic */ void m1216lambda$showInternal$8$orgtelegramuiComponentsPipVideoOverlay(DynamicAnimation animation, boolean canceled, float value, float velocity) {
        getPipConfig().setPipY(value);
    }

    /* renamed from: lambda$showInternal$10$org-telegram-ui-Components-PipVideoOverlay  reason: not valid java name */
    public /* synthetic */ void m1213x4var_a443(boolean inAppOnly, View v) {
        boolean isResumedByActivityManager = true;
        if (Build.VERSION.SDK_INT >= 21) {
            List<ActivityManager.RunningAppProcessInfo> appProcessInfos = ((ActivityManager) v.getContext().getSystemService("activity")).getRunningAppProcesses();
            if (!appProcessInfos.isEmpty()) {
                boolean z = false;
                if (appProcessInfos.get(0).importance == 100) {
                    z = true;
                }
                isResumedByActivityManager = z;
            }
        }
        if (inAppOnly || (isResumedByActivityManager && LaunchActivity.isResumed)) {
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
        v.getClass();
        LaunchActivity.onResumeStaticCallback = new ChatActivityEnterView$$ExternalSyntheticLambda26(v);
        Context ctx = ApplicationLoader.applicationContext;
        Intent intent = new Intent(ctx, LaunchActivity.class);
        intent.addFlags(NUM);
        ctx.startActivity(intent);
    }

    /* renamed from: lambda$showInternal$11$org-telegram-ui-Components-PipVideoOverlay  reason: not valid java name */
    public /* synthetic */ void m1214x12330da2(View v) {
        VideoPlayer videoPlayer;
        PhotoViewer photoViewer2 = this.photoViewer;
        if (photoViewer2 != null && (videoPlayer = photoViewer2.getVideoPlayer()) != null) {
            if (videoPlayer.isPlaying()) {
                videoPlayer.pause();
            } else {
                videoPlayer.play();
            }
            updatePlayButton();
        }
    }

    private WindowManager.LayoutParams createWindowLayoutParams(boolean inAppOnly) {
        WindowManager.LayoutParams windowLayoutParams2 = new WindowManager.LayoutParams();
        windowLayoutParams2.gravity = 51;
        windowLayoutParams2.format = -3;
        if (inAppOnly || !AndroidUtilities.checkInlinePermissions(ApplicationLoader.applicationContext)) {
            windowLayoutParams2.type = 99;
        } else if (Build.VERSION.SDK_INT >= 26) {
            windowLayoutParams2.type = 2038;
        } else {
            windowLayoutParams2.type = 2003;
        }
        windowLayoutParams2.flags = 520;
        return windowLayoutParams2;
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
            int width = getWidth();
            int progressSidePadding = AndroidUtilities.dp(10.0f);
            int progressLeft = progressSidePadding;
            int progressRight = ((int) (((float) ((width - progressLeft) - progressSidePadding)) * PipVideoOverlay.this.videoProgress)) + progressLeft;
            float y = (float) (getHeight() - AndroidUtilities.dp(8.0f));
            if (PipVideoOverlay.this.bufferProgress != 0.0f) {
                canvas.drawLine((float) progressLeft, y, ((float) progressLeft) + (((float) ((width - progressLeft) - progressSidePadding)) * PipVideoOverlay.this.bufferProgress), y, this.bufferPaint);
            }
            canvas.drawLine((float) progressLeft, y, (float) progressRight, y, this.progressPaint);
        }
    }

    private static final class PipConfig {
        private SharedPreferences mPrefs;

        private PipConfig(int width, int height) {
            Context context = ApplicationLoader.applicationContext;
            this.mPrefs = context.getSharedPreferences("pip_layout_" + width + "_" + height, 0);
        }

        /* access modifiers changed from: private */
        public void setPipX(float x) {
            this.mPrefs.edit().putFloat("x", x).apply();
        }

        /* access modifiers changed from: private */
        public void setPipY(float y) {
            this.mPrefs.edit().putFloat("y", y).apply();
        }

        private void setScaleFactor(float scaleFactor) {
            this.mPrefs.edit().putFloat("scale_factor", scaleFactor).apply();
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
