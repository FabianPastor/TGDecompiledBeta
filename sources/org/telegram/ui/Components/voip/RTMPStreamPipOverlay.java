package org.telegram.ui.Components.voip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewPropertyAnimator;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
import androidx.core.view.GestureDetectorCompat;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import java.util.ArrayList;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.voip.VideoCapturerDevice;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$TL_groupCallParticipant;
import org.telegram.tgnet.TLRPC$TL_groupCallParticipantVideo;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.SimpleFloatPropertyCompat;
import org.telegram.ui.LaunchActivity;
import org.webrtc.RendererCommon;
import org.webrtc.VideoSink;

public class RTMPStreamPipOverlay implements NotificationCenter.NotificationCenterDelegate {
    private static final FloatPropertyCompat<RTMPStreamPipOverlay> PIP_X_PROPERTY = new SimpleFloatPropertyCompat("pipX", RTMPStreamPipOverlay$$ExternalSyntheticLambda6.INSTANCE, RTMPStreamPipOverlay$$ExternalSyntheticLambda8.INSTANCE);
    private static final FloatPropertyCompat<RTMPStreamPipOverlay> PIP_Y_PROPERTY = new SimpleFloatPropertyCompat("pipY", RTMPStreamPipOverlay$$ExternalSyntheticLambda5.INSTANCE, RTMPStreamPipOverlay$$ExternalSyntheticLambda7.INSTANCE);
    @SuppressLint({"StaticFieldLeak"})
    private static RTMPStreamPipOverlay instance = new RTMPStreamPipOverlay();
    private AccountInstance accountInstance;
    /* access modifiers changed from: private */
    public Float aspectRatio;
    private BackupImageView avatarImageView;
    /* access modifiers changed from: private */
    public TLRPC$TL_groupCallParticipant boundParticipant;
    private boolean boundPresentation;
    /* access modifiers changed from: private */
    public CellFlickerDrawable cellFlickerDrawable = new CellFlickerDrawable();
    /* access modifiers changed from: private */
    public View consumingChild;
    /* access modifiers changed from: private */
    public FrameLayout contentFrameLayout;
    /* access modifiers changed from: private */
    public ViewGroup contentView;
    private FrameLayout controlsView;
    /* access modifiers changed from: private */
    public Runnable dismissControlsCallback = new RTMPStreamPipOverlay$$ExternalSyntheticLambda3(this);
    /* access modifiers changed from: private */
    public boolean firstFrameRendered;
    private View flickerView;
    /* access modifiers changed from: private */
    public GestureDetectorCompat gestureDetector;
    /* access modifiers changed from: private */
    public boolean isScrollDisallowed;
    /* access modifiers changed from: private */
    public boolean isScrolling;
    /* access modifiers changed from: private */
    public boolean isShowingControls;
    private boolean isVisible;
    /* access modifiers changed from: private */
    public float maxScaleFactor = 1.4f;
    /* access modifiers changed from: private */
    public float minScaleFactor = 0.6f;
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
    /* access modifiers changed from: private */
    public boolean placeholderShown = true;
    /* access modifiers changed from: private */
    public boolean postedDismissControls;
    /* access modifiers changed from: private */
    public ValueAnimator scaleAnimator;
    /* access modifiers changed from: private */
    public float scaleFactor = 1.0f;
    /* access modifiers changed from: private */
    public ScaleGestureDetector scaleGestureDetector;
    /* access modifiers changed from: private */
    public VoIPTextureView textureView;
    /* access modifiers changed from: private */
    public WindowManager.LayoutParams windowLayoutParams;
    /* access modifiers changed from: private */
    public WindowManager windowManager;

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$static$1(RTMPStreamPipOverlay rTMPStreamPipOverlay, float f) {
        WindowManager.LayoutParams layoutParams = rTMPStreamPipOverlay.windowLayoutParams;
        rTMPStreamPipOverlay.pipX = f;
        layoutParams.x = (int) f;
        rTMPStreamPipOverlay.windowManager.updateViewLayout(rTMPStreamPipOverlay.contentView, layoutParams);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$static$3(RTMPStreamPipOverlay rTMPStreamPipOverlay, float f) {
        WindowManager.LayoutParams layoutParams = rTMPStreamPipOverlay.windowLayoutParams;
        rTMPStreamPipOverlay.pipY = f;
        layoutParams.y = (int) f;
        rTMPStreamPipOverlay.windowManager.updateViewLayout(rTMPStreamPipOverlay.contentView, layoutParams);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$4() {
        this.isShowingControls = false;
        toggleControls(false);
        this.postedDismissControls = false;
    }

    public static boolean isVisible() {
        return instance.isVisible;
    }

    /* access modifiers changed from: private */
    public int getSuggestedWidth() {
        float min;
        float f;
        if (getRatio() >= 1.0f) {
            Point point = AndroidUtilities.displaySize;
            min = (float) Math.min(point.x, point.y);
            f = 0.35f;
        } else {
            Point point2 = AndroidUtilities.displaySize;
            min = (float) Math.min(point2.x, point2.y);
            f = 0.6f;
        }
        return (int) (min * f);
    }

    /* access modifiers changed from: private */
    public int getSuggestedHeight() {
        return (int) (((float) getSuggestedWidth()) * getRatio());
    }

    private float getRatio() {
        if (this.aspectRatio == null) {
            float f = 0.5625f;
            if (VoIPService.getSharedInstance() != null && !VoIPService.getSharedInstance().groupCall.visibleVideoParticipants.isEmpty()) {
                float f2 = VoIPService.getSharedInstance().groupCall.visibleVideoParticipants.get(0).aspectRatio;
                if (f2 != 0.0f) {
                    f = 1.0f / f2;
                }
            }
            this.aspectRatio = Float.valueOf(f);
            Point point = AndroidUtilities.displaySize;
            this.maxScaleFactor = ((float) (Math.min(point.x, point.y) - AndroidUtilities.dp(32.0f))) / ((float) getSuggestedWidth());
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
        this.scaleAnimator = duration;
        duration.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.scaleAnimator.addUpdateListener(new RTMPStreamPipOverlay$$ExternalSyntheticLambda0(this));
        this.scaleAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                ValueAnimator unused = RTMPStreamPipOverlay.this.scaleAnimator = null;
            }
        });
        this.scaleAnimator.start();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$toggleControls$5(ValueAnimator valueAnimator) {
        this.controlsView.setAlpha(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    public static void dismiss() {
        instance.dismissInternal();
    }

    private void dismissInternal() {
        if (this.isVisible) {
            this.isVisible = false;
            AndroidUtilities.runOnUIThread(RTMPStreamPipOverlay$$ExternalSyntheticLambda4.INSTANCE, 100);
            this.accountInstance.getNotificationCenter().removeObserver(this, NotificationCenter.groupCallUpdated);
            this.accountInstance.getNotificationCenter().removeObserver(this, NotificationCenter.applyGroupCallVisibleParticipants);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didEndCall);
            ValueAnimator valueAnimator = this.scaleAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            if (this.postedDismissControls) {
                AndroidUtilities.cancelRunOnUIThread(this.dismissControlsCallback);
                this.postedDismissControls = false;
            }
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(250);
            animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.contentView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.contentView, View.SCALE_X, new float[]{0.1f}), ObjectAnimator.ofFloat(this.contentView, View.SCALE_Y, new float[]{0.1f})});
            animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    RTMPStreamPipOverlay.this.windowManager.removeViewImmediate(RTMPStreamPipOverlay.this.contentView);
                    RTMPStreamPipOverlay.this.textureView.renderer.release();
                    TLRPC$TL_groupCallParticipant unused = RTMPStreamPipOverlay.this.boundParticipant = null;
                    boolean unused2 = RTMPStreamPipOverlay.this.placeholderShown = true;
                    boolean unused3 = RTMPStreamPipOverlay.this.firstFrameRendered = false;
                    View unused4 = RTMPStreamPipOverlay.this.consumingChild = null;
                    boolean unused5 = RTMPStreamPipOverlay.this.isScrolling = false;
                }
            });
            animatorSet.start();
        }
    }

    public static void show() {
        instance.showInternal();
    }

    private void showInternal() {
        if (VoIPService.getSharedInstance() != null && VoIPService.getSharedInstance().groupCall != null && !this.isVisible) {
            this.isVisible = true;
            AccountInstance accountInstance2 = VoIPService.getSharedInstance().groupCall.currentAccount;
            this.accountInstance = accountInstance2;
            accountInstance2.getNotificationCenter().addObserver(this, NotificationCenter.groupCallUpdated);
            this.accountInstance.getNotificationCenter().addObserver(this, NotificationCenter.applyGroupCallVisibleParticipants);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didEndCall);
            this.pipWidth = getSuggestedWidth();
            this.pipHeight = getSuggestedHeight();
            this.scaleFactor = 1.0f;
            this.isShowingControls = false;
            this.pipXSpring = new SpringAnimation(this, PIP_X_PROPERTY).setSpring(new SpringForce().setDampingRatio(0.75f).setStiffness(650.0f));
            this.pipYSpring = new SpringAnimation(this, PIP_Y_PROPERTY).setSpring(new SpringForce().setDampingRatio(0.75f).setStiffness(650.0f));
            Context context = ApplicationLoader.applicationContext;
            final int scaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
            ScaleGestureDetector scaleGestureDetector2 = new ScaleGestureDetector(context, new ScaleGestureDetector.OnScaleGestureListener() {
                public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
                    RTMPStreamPipOverlay rTMPStreamPipOverlay = RTMPStreamPipOverlay.this;
                    float unused = rTMPStreamPipOverlay.scaleFactor = MathUtils.clamp(rTMPStreamPipOverlay.scaleFactor * scaleGestureDetector.getScaleFactor(), RTMPStreamPipOverlay.this.minScaleFactor, RTMPStreamPipOverlay.this.maxScaleFactor);
                    RTMPStreamPipOverlay rTMPStreamPipOverlay2 = RTMPStreamPipOverlay.this;
                    int unused2 = rTMPStreamPipOverlay2.pipWidth = (int) (((float) rTMPStreamPipOverlay2.getSuggestedWidth()) * RTMPStreamPipOverlay.this.scaleFactor);
                    RTMPStreamPipOverlay rTMPStreamPipOverlay3 = RTMPStreamPipOverlay.this;
                    int unused3 = rTMPStreamPipOverlay3.pipHeight = (int) (((float) rTMPStreamPipOverlay3.getSuggestedHeight()) * RTMPStreamPipOverlay.this.scaleFactor);
                    AndroidUtilities.runOnUIThread(new RTMPStreamPipOverlay$3$$ExternalSyntheticLambda0(this));
                    SpringForce spring = ((SpringAnimation) RTMPStreamPipOverlay.this.pipXSpring.setStartValue(RTMPStreamPipOverlay.this.pipX)).getSpring();
                    float focusX = scaleGestureDetector.getFocusX();
                    int i = AndroidUtilities.displaySize.x;
                    spring.setFinalPosition(focusX >= ((float) i) / 2.0f ? (float) ((i - RTMPStreamPipOverlay.this.pipWidth) - AndroidUtilities.dp(16.0f)) : (float) AndroidUtilities.dp(16.0f));
                    if (!RTMPStreamPipOverlay.this.pipXSpring.isRunning()) {
                        RTMPStreamPipOverlay.this.pipXSpring.start();
                    }
                    ((SpringAnimation) RTMPStreamPipOverlay.this.pipYSpring.setStartValue(RTMPStreamPipOverlay.this.pipY)).getSpring().setFinalPosition(MathUtils.clamp(scaleGestureDetector.getFocusY() - (((float) RTMPStreamPipOverlay.this.pipHeight) / 2.0f), (float) AndroidUtilities.dp(16.0f), (float) ((AndroidUtilities.displaySize.y - RTMPStreamPipOverlay.this.pipHeight) - AndroidUtilities.dp(16.0f))));
                    if (RTMPStreamPipOverlay.this.pipYSpring.isRunning()) {
                        return true;
                    }
                    RTMPStreamPipOverlay.this.pipYSpring.start();
                    return true;
                }

                /* access modifiers changed from: private */
                public /* synthetic */ void lambda$onScale$0() {
                    RTMPStreamPipOverlay.this.contentFrameLayout.invalidate();
                    if (Build.VERSION.SDK_INT < 18 || !RTMPStreamPipOverlay.this.contentFrameLayout.isInLayout()) {
                        RTMPStreamPipOverlay.this.contentFrameLayout.requestLayout();
                        RTMPStreamPipOverlay.this.contentView.requestLayout();
                        RTMPStreamPipOverlay.this.textureView.requestLayout();
                    }
                }

                public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
                    if (RTMPStreamPipOverlay.this.isScrolling) {
                        boolean unused = RTMPStreamPipOverlay.this.isScrolling = false;
                    }
                    boolean unused2 = RTMPStreamPipOverlay.this.isScrollDisallowed = true;
                    RTMPStreamPipOverlay.this.windowLayoutParams.width = (int) (((float) RTMPStreamPipOverlay.this.getSuggestedWidth()) * RTMPStreamPipOverlay.this.maxScaleFactor);
                    RTMPStreamPipOverlay.this.windowLayoutParams.height = (int) (((float) RTMPStreamPipOverlay.this.getSuggestedHeight()) * RTMPStreamPipOverlay.this.maxScaleFactor);
                    RTMPStreamPipOverlay.this.windowManager.updateViewLayout(RTMPStreamPipOverlay.this.contentView, RTMPStreamPipOverlay.this.windowLayoutParams);
                    return true;
                }

                public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
                    if (RTMPStreamPipOverlay.this.pipXSpring.isRunning() || RTMPStreamPipOverlay.this.pipYSpring.isRunning()) {
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
                        if (!RTMPStreamPipOverlay.this.pipXSpring.isRunning()) {
                            arrayList.add(RTMPStreamPipOverlay.this.pipXSpring);
                        } else {
                            RTMPStreamPipOverlay.this.pipXSpring.addEndListener(r0);
                        }
                        if (!RTMPStreamPipOverlay.this.pipYSpring.isRunning()) {
                            arrayList.add(RTMPStreamPipOverlay.this.pipYSpring);
                        } else {
                            RTMPStreamPipOverlay.this.pipYSpring.addEndListener(r0);
                        }
                    } else {
                        updateLayout();
                    }
                }

                /* access modifiers changed from: private */
                public void updateLayout() {
                    RTMPStreamPipOverlay rTMPStreamPipOverlay = RTMPStreamPipOverlay.this;
                    WindowManager.LayoutParams access$2100 = rTMPStreamPipOverlay.windowLayoutParams;
                    int access$1300 = (int) (((float) RTMPStreamPipOverlay.this.getSuggestedWidth()) * RTMPStreamPipOverlay.this.scaleFactor);
                    access$2100.width = access$1300;
                    int unused = rTMPStreamPipOverlay.pipWidth = access$1300;
                    RTMPStreamPipOverlay rTMPStreamPipOverlay2 = RTMPStreamPipOverlay.this;
                    WindowManager.LayoutParams access$21002 = rTMPStreamPipOverlay2.windowLayoutParams;
                    int access$1500 = (int) (((float) RTMPStreamPipOverlay.this.getSuggestedHeight()) * RTMPStreamPipOverlay.this.scaleFactor);
                    access$21002.height = access$1500;
                    int unused2 = rTMPStreamPipOverlay2.pipHeight = access$1500;
                    RTMPStreamPipOverlay.this.windowManager.updateViewLayout(RTMPStreamPipOverlay.this.contentView, RTMPStreamPipOverlay.this.windowLayoutParams);
                }
            });
            this.scaleGestureDetector = scaleGestureDetector2;
            int i = Build.VERSION.SDK_INT;
            if (i >= 19) {
                scaleGestureDetector2.setQuickScaleEnabled(false);
            }
            if (i >= 23) {
                this.scaleGestureDetector.setStylusScaleEnabled(false);
            }
            this.gestureDetector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {
                private float startPipX;
                private float startPipY;

                public boolean onDown(MotionEvent motionEvent) {
                    if (RTMPStreamPipOverlay.this.isShowingControls) {
                        for (int i = 1; i < RTMPStreamPipOverlay.this.contentFrameLayout.getChildCount(); i++) {
                            View childAt = RTMPStreamPipOverlay.this.contentFrameLayout.getChildAt(i);
                            if (childAt.dispatchTouchEvent(motionEvent)) {
                                View unused = RTMPStreamPipOverlay.this.consumingChild = childAt;
                                return true;
                            }
                        }
                    }
                    this.startPipX = RTMPStreamPipOverlay.this.pipX;
                    this.startPipY = RTMPStreamPipOverlay.this.pipY;
                    return true;
                }

                public boolean onSingleTapUp(MotionEvent motionEvent) {
                    if (RTMPStreamPipOverlay.this.scaleAnimator != null) {
                        return true;
                    }
                    if (RTMPStreamPipOverlay.this.postedDismissControls) {
                        AndroidUtilities.cancelRunOnUIThread(RTMPStreamPipOverlay.this.dismissControlsCallback);
                        boolean unused = RTMPStreamPipOverlay.this.postedDismissControls = false;
                    }
                    RTMPStreamPipOverlay rTMPStreamPipOverlay = RTMPStreamPipOverlay.this;
                    boolean unused2 = rTMPStreamPipOverlay.isShowingControls = !rTMPStreamPipOverlay.isShowingControls;
                    RTMPStreamPipOverlay rTMPStreamPipOverlay2 = RTMPStreamPipOverlay.this;
                    rTMPStreamPipOverlay2.toggleControls(rTMPStreamPipOverlay2.isShowingControls);
                    if (RTMPStreamPipOverlay.this.isShowingControls && !RTMPStreamPipOverlay.this.postedDismissControls) {
                        AndroidUtilities.runOnUIThread(RTMPStreamPipOverlay.this.dismissControlsCallback, 2500);
                        boolean unused3 = RTMPStreamPipOverlay.this.postedDismissControls = true;
                    }
                    return true;
                }

                public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
                    if (!RTMPStreamPipOverlay.this.isScrolling || RTMPStreamPipOverlay.this.isScrollDisallowed) {
                        return false;
                    }
                    SpringForce spring = ((SpringAnimation) ((SpringAnimation) RTMPStreamPipOverlay.this.pipXSpring.setStartVelocity(f)).setStartValue(RTMPStreamPipOverlay.this.pipX)).getSpring();
                    float access$1600 = RTMPStreamPipOverlay.this.pipX + (((float) RTMPStreamPipOverlay.this.pipWidth) / 2.0f) + (f / 7.0f);
                    int i = AndroidUtilities.displaySize.x;
                    spring.setFinalPosition(access$1600 >= ((float) i) / 2.0f ? (float) ((i - RTMPStreamPipOverlay.this.pipWidth) - AndroidUtilities.dp(16.0f)) : (float) AndroidUtilities.dp(16.0f));
                    RTMPStreamPipOverlay.this.pipXSpring.start();
                    ((SpringAnimation) ((SpringAnimation) RTMPStreamPipOverlay.this.pipYSpring.setStartVelocity(f)).setStartValue(RTMPStreamPipOverlay.this.pipY)).getSpring().setFinalPosition(MathUtils.clamp(RTMPStreamPipOverlay.this.pipY + (f2 / 10.0f), (float) AndroidUtilities.dp(16.0f), (float) ((AndroidUtilities.displaySize.y - RTMPStreamPipOverlay.this.pipHeight) - AndroidUtilities.dp(16.0f))));
                    RTMPStreamPipOverlay.this.pipYSpring.start();
                    return true;
                }

                public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
                    if (!RTMPStreamPipOverlay.this.isScrolling && RTMPStreamPipOverlay.this.scaleAnimator == null && !RTMPStreamPipOverlay.this.isScrollDisallowed && (Math.abs(f) >= ((float) scaledTouchSlop) || Math.abs(f2) >= ((float) scaledTouchSlop))) {
                        boolean unused = RTMPStreamPipOverlay.this.isScrolling = true;
                        RTMPStreamPipOverlay.this.pipXSpring.cancel();
                        RTMPStreamPipOverlay.this.pipYSpring.cancel();
                    }
                    if (RTMPStreamPipOverlay.this.isScrolling) {
                        RTMPStreamPipOverlay.this.windowLayoutParams.x = (int) RTMPStreamPipOverlay.this.pipX = (this.startPipX + motionEvent2.getRawX()) - motionEvent.getRawX();
                        RTMPStreamPipOverlay.this.windowLayoutParams.y = (int) RTMPStreamPipOverlay.this.pipY = (this.startPipY + motionEvent2.getRawY()) - motionEvent.getRawY();
                        RTMPStreamPipOverlay.this.windowManager.updateViewLayout(RTMPStreamPipOverlay.this.contentView, RTMPStreamPipOverlay.this.windowLayoutParams);
                    }
                    return true;
                }
            });
            this.contentFrameLayout = new FrameLayout(context) {
                private Path path = new Path();

                public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                    int action = motionEvent.getAction();
                    if (RTMPStreamPipOverlay.this.consumingChild != null) {
                        MotionEvent obtain = MotionEvent.obtain(motionEvent);
                        obtain.offsetLocation(RTMPStreamPipOverlay.this.consumingChild.getX(), RTMPStreamPipOverlay.this.consumingChild.getY());
                        boolean dispatchTouchEvent = RTMPStreamPipOverlay.this.consumingChild.dispatchTouchEvent(motionEvent);
                        obtain.recycle();
                        if (action == 1 || action == 3) {
                            View unused = RTMPStreamPipOverlay.this.consumingChild = null;
                        }
                        if (dispatchTouchEvent) {
                            return true;
                        }
                    }
                    MotionEvent obtain2 = MotionEvent.obtain(motionEvent);
                    obtain2.offsetLocation(motionEvent.getRawX() - motionEvent.getX(), motionEvent.getRawY() - motionEvent.getY());
                    boolean onTouchEvent = RTMPStreamPipOverlay.this.scaleGestureDetector.onTouchEvent(obtain2);
                    obtain2.recycle();
                    boolean z = !RTMPStreamPipOverlay.this.scaleGestureDetector.isInProgress() && RTMPStreamPipOverlay.this.gestureDetector.onTouchEvent(motionEvent);
                    if (action == 1 || action == 3) {
                        boolean unused2 = RTMPStreamPipOverlay.this.isScrolling = false;
                        boolean unused3 = RTMPStreamPipOverlay.this.isScrollDisallowed = false;
                        if (!RTMPStreamPipOverlay.this.pipXSpring.isRunning()) {
                            SpringForce spring = ((SpringAnimation) RTMPStreamPipOverlay.this.pipXSpring.setStartValue(RTMPStreamPipOverlay.this.pipX)).getSpring();
                            float access$1600 = RTMPStreamPipOverlay.this.pipX + (((float) RTMPStreamPipOverlay.this.pipWidth) / 2.0f);
                            int i = AndroidUtilities.displaySize.x;
                            spring.setFinalPosition(access$1600 >= ((float) i) / 2.0f ? (float) ((i - RTMPStreamPipOverlay.this.pipWidth) - AndroidUtilities.dp(16.0f)) : (float) AndroidUtilities.dp(16.0f));
                            RTMPStreamPipOverlay.this.pipXSpring.start();
                        }
                        if (!RTMPStreamPipOverlay.this.pipYSpring.isRunning()) {
                            ((SpringAnimation) RTMPStreamPipOverlay.this.pipYSpring.setStartValue(RTMPStreamPipOverlay.this.pipY)).getSpring().setFinalPosition(MathUtils.clamp(RTMPStreamPipOverlay.this.pipY, (float) AndroidUtilities.dp(16.0f), (float) ((AndroidUtilities.displaySize.y - RTMPStreamPipOverlay.this.pipHeight) - AndroidUtilities.dp(16.0f))));
                            RTMPStreamPipOverlay.this.pipYSpring.start();
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
                    RTMPStreamPipOverlay.this.bindTextureView();
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
            AnonymousClass6 r5 = new ViewGroup(context) {
                /* access modifiers changed from: protected */
                public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                    RTMPStreamPipOverlay.this.contentFrameLayout.layout(0, 0, RTMPStreamPipOverlay.this.pipWidth, RTMPStreamPipOverlay.this.pipHeight);
                }

                /* access modifiers changed from: protected */
                public void onMeasure(int i, int i2) {
                    setMeasuredDimension(View.MeasureSpec.getSize(i), View.MeasureSpec.getSize(i2));
                    RTMPStreamPipOverlay.this.contentFrameLayout.measure(View.MeasureSpec.makeMeasureSpec(RTMPStreamPipOverlay.this.pipWidth, NUM), View.MeasureSpec.makeMeasureSpec(RTMPStreamPipOverlay.this.pipHeight, NUM));
                }
            };
            this.contentView = r5;
            r5.addView(this.contentFrameLayout, LayoutHelper.createFrame(-1, -1.0f));
            if (i >= 21) {
                this.contentFrameLayout.setOutlineProvider(new ViewOutlineProvider(this) {
                    public void getOutline(View view, Outline outline) {
                        outline.setRoundRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight(), (float) AndroidUtilities.dp(10.0f));
                    }
                });
                this.contentFrameLayout.setClipToOutline(true);
            }
            this.contentFrameLayout.setBackgroundColor(Theme.getColor("voipgroup_actionBar"));
            BackupImageView backupImageView = new BackupImageView(context);
            this.avatarImageView = backupImageView;
            this.contentFrameLayout.addView(backupImageView, LayoutHelper.createFrame(-1, -1.0f));
            VoIPTextureView voIPTextureView = new VoIPTextureView(context, false, false, false, false);
            this.textureView = voIPTextureView;
            voIPTextureView.setAlpha(0.0f);
            this.textureView.renderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL);
            VoIPTextureView voIPTextureView2 = this.textureView;
            voIPTextureView2.scaleType = VoIPTextureView.SCALE_TYPE_FILL;
            voIPTextureView2.renderer.setRotateTextureWithScreen(true);
            this.textureView.renderer.init(VideoCapturerDevice.getEglBase().getEglBaseContext(), new RendererCommon.RendererEvents() {
                public void onFirstFrameRendered() {
                    boolean unused = RTMPStreamPipOverlay.this.firstFrameRendered = true;
                    AndroidUtilities.runOnUIThread(new RTMPStreamPipOverlay$8$$ExternalSyntheticLambda1(this));
                }

                /* access modifiers changed from: private */
                public /* synthetic */ void lambda$onFirstFrameRendered$0() {
                    RTMPStreamPipOverlay.this.bindTextureView();
                }

                public void onFrameResolutionChanged(int i, int i2, int i3) {
                    if ((i3 / 90) % 2 == 0) {
                        Float unused = RTMPStreamPipOverlay.this.aspectRatio = Float.valueOf(((float) i2) / ((float) i));
                    } else {
                        Float unused2 = RTMPStreamPipOverlay.this.aspectRatio = Float.valueOf(((float) i) / ((float) i2));
                    }
                    AndroidUtilities.runOnUIThread(new RTMPStreamPipOverlay$8$$ExternalSyntheticLambda0(this));
                }

                /* access modifiers changed from: private */
                public /* synthetic */ void lambda$onFrameResolutionChanged$1() {
                    RTMPStreamPipOverlay.this.bindTextureView();
                }
            });
            this.contentFrameLayout.addView(this.textureView, LayoutHelper.createFrame(-1, -1.0f));
            AnonymousClass9 r6 = new View(context) {
                /* access modifiers changed from: protected */
                public void onDraw(Canvas canvas) {
                    if (getAlpha() != 0.0f) {
                        RectF rectF = AndroidUtilities.rectTmp;
                        rectF.set(0.0f, 0.0f, (float) getWidth(), (float) getHeight());
                        RTMPStreamPipOverlay.this.cellFlickerDrawable.draw(canvas, rectF, (float) AndroidUtilities.dp(10.0f), (View) null);
                        invalidate();
                    }
                }

                /* access modifiers changed from: protected */
                public void onSizeChanged(int i, int i2, int i3, int i4) {
                    super.onSizeChanged(i, i2, i3, i4);
                    RTMPStreamPipOverlay.this.cellFlickerDrawable.setParentWidth(i);
                }
            };
            this.flickerView = r6;
            this.contentFrameLayout.addView(r6, LayoutHelper.createFrame(-1, -1.0f));
            FrameLayout frameLayout = new FrameLayout(context);
            this.controlsView = frameLayout;
            frameLayout.setAlpha(0.0f);
            View view = new View(context);
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setColors(new int[]{NUM, 0});
            gradientDrawable.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM);
            view.setBackground(gradientDrawable);
            this.controlsView.addView(view, LayoutHelper.createFrame(-1, -1.0f));
            int dp = AndroidUtilities.dp(8.0f);
            ImageView imageView = new ImageView(context);
            imageView.setImageResource(NUM);
            imageView.setColorFilter(Theme.getColor("voipgroup_actionBarItems"));
            imageView.setBackground(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21")));
            imageView.setPadding(dp, dp, dp, dp);
            imageView.setOnClickListener(RTMPStreamPipOverlay$$ExternalSyntheticLambda2.INSTANCE);
            float f = (float) 4;
            float f2 = (float) 38;
            float f3 = f;
            this.controlsView.addView(imageView, LayoutHelper.createFrame(38, f2, 5, 0.0f, f3, f, 0.0f));
            ImageView imageView2 = new ImageView(context);
            imageView2.setImageResource(NUM);
            imageView2.setColorFilter(Theme.getColor("voipgroup_actionBarItems"));
            imageView2.setBackground(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21")));
            imageView2.setPadding(dp, dp, dp, dp);
            imageView2.setOnClickListener(new RTMPStreamPipOverlay$$ExternalSyntheticLambda1(context));
            this.controlsView.addView(imageView2, LayoutHelper.createFrame(38, f2, 5, 0.0f, f3, (float) 48, 0.0f));
            this.contentFrameLayout.addView(this.controlsView, LayoutHelper.createFrame(-1, -1.0f));
            this.windowManager = (WindowManager) ApplicationLoader.applicationContext.getSystemService("window");
            WindowManager.LayoutParams createWindowLayoutParams = createWindowLayoutParams();
            this.windowLayoutParams = createWindowLayoutParams;
            int i2 = this.pipWidth;
            createWindowLayoutParams.width = i2;
            createWindowLayoutParams.height = this.pipHeight;
            float dp2 = (float) ((AndroidUtilities.displaySize.x - i2) - AndroidUtilities.dp(16.0f));
            this.pipX = dp2;
            createWindowLayoutParams.x = (int) dp2;
            WindowManager.LayoutParams layoutParams = this.windowLayoutParams;
            float dp3 = (float) ((AndroidUtilities.displaySize.y - this.pipHeight) - AndroidUtilities.dp(16.0f));
            this.pipY = dp3;
            layoutParams.y = (int) dp3;
            WindowManager.LayoutParams layoutParams2 = this.windowLayoutParams;
            layoutParams2.dimAmount = 0.0f;
            layoutParams2.flags = 520;
            this.contentView.setAlpha(0.0f);
            this.contentView.setScaleX(0.1f);
            this.contentView.setScaleY(0.1f);
            this.windowManager.addView(this.contentView, this.windowLayoutParams);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(250);
            animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.contentView, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.contentView, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.contentView, View.SCALE_Y, new float[]{1.0f})});
            animatorSet.start();
            bindTextureView();
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.groupCallVisibilityChanged, new Object[0]);
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$showInternal$8(Context context, View view) {
        if (VoIPService.getSharedInstance() != null) {
            Intent action = new Intent(context, LaunchActivity.class).setAction("voip_chat");
            action.putExtra("currentAccount", VoIPService.getSharedInstance().getAccount());
            if (!(context instanceof Activity)) {
                action.addFlags(NUM);
            }
            context.startActivity(action);
            dismiss();
        }
    }

    /* access modifiers changed from: private */
    public void bindTextureView() {
        TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant;
        TLRPC$TL_groupCallParticipantVideo tLRPC$TL_groupCallParticipantVideo;
        TLRPC$TL_groupCallParticipantVideo tLRPC$TL_groupCallParticipantVideo2;
        boolean z = true;
        if (VoIPService.getSharedInstance() != null && VoIPService.getSharedInstance().groupCall != null && !VoIPService.getSharedInstance().groupCall.visibleVideoParticipants.isEmpty()) {
            TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant2 = VoIPService.getSharedInstance().groupCall.visibleVideoParticipants.get(0).participant;
            TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant3 = this.boundParticipant;
            if (tLRPC$TL_groupCallParticipant3 == null || MessageObject.getPeerId(tLRPC$TL_groupCallParticipant3.peer) != MessageObject.getPeerId(tLRPC$TL_groupCallParticipant2.peer)) {
                if (this.boundParticipant != null) {
                    VoIPService.getSharedInstance().removeRemoteSink(this.boundParticipant, this.boundPresentation);
                }
                this.boundPresentation = tLRPC$TL_groupCallParticipant2.presentation != null;
                if (tLRPC$TL_groupCallParticipant2.self) {
                    VoIPService.getSharedInstance().setSinks(this.textureView.renderer, this.boundPresentation, (VideoSink) null);
                } else {
                    VoIPService.getSharedInstance().addRemoteSink(tLRPC$TL_groupCallParticipant2, this.boundPresentation, this.textureView.renderer, (VideoSink) null);
                }
                MessagesController messagesController = VoIPService.getSharedInstance().groupCall.currentAccount.getMessagesController();
                long peerId = MessageObject.getPeerId(tLRPC$TL_groupCallParticipant2.peer);
                if (peerId > 0) {
                    TLRPC$User user = messagesController.getUser(Long.valueOf(peerId));
                    ImageLocation forUser = ImageLocation.getForUser(user, 1);
                    int colorForId = user != null ? AvatarDrawable.getColorForId(user.id) : ColorUtils.blendARGB(-16777216, -1, 0.2f);
                    this.avatarImageView.getImageReceiver().setImage(forUser, "50_50_b", new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{ColorUtils.blendARGB(colorForId, -16777216, 0.2f), ColorUtils.blendARGB(colorForId, -16777216, 0.4f)}), (String) null, user, 0);
                } else {
                    TLRPC$Chat chat = messagesController.getChat(Long.valueOf(-peerId));
                    ImageLocation forChat = ImageLocation.getForChat(chat, 1);
                    int colorForId2 = chat != null ? AvatarDrawable.getColorForId(chat.id) : ColorUtils.blendARGB(-16777216, -1, 0.2f);
                    this.avatarImageView.getImageReceiver().setImage(forChat, "50_50_b", new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{ColorUtils.blendARGB(colorForId2, -16777216, 0.2f), ColorUtils.blendARGB(colorForId2, -16777216, 0.4f)}), (String) null, chat, 0);
                }
                this.boundParticipant = tLRPC$TL_groupCallParticipant2;
            }
        } else if (this.boundParticipant != null) {
            VoIPService.getSharedInstance().removeRemoteSink(this.boundParticipant, false);
            this.boundParticipant = null;
        }
        if (this.firstFrameRendered && (tLRPC$TL_groupCallParticipant = this.boundParticipant) != null && (!((tLRPC$TL_groupCallParticipantVideo = tLRPC$TL_groupCallParticipant.video) == null && tLRPC$TL_groupCallParticipant.presentation == null) && ((tLRPC$TL_groupCallParticipantVideo == null || !tLRPC$TL_groupCallParticipantVideo.paused) && ((tLRPC$TL_groupCallParticipantVideo2 = tLRPC$TL_groupCallParticipant.presentation) == null || !tLRPC$TL_groupCallParticipantVideo2.paused)))) {
            z = false;
        }
        if (this.placeholderShown != z) {
            this.flickerView.animate().cancel();
            float f = 1.0f;
            ViewPropertyAnimator duration = this.flickerView.animate().alpha(z ? 1.0f : 0.0f).setDuration(150);
            CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.DEFAULT;
            duration.setInterpolator(cubicBezierInterpolator).start();
            this.avatarImageView.animate().cancel();
            this.avatarImageView.animate().alpha(z ? 1.0f : 0.0f).setDuration(150).setInterpolator(cubicBezierInterpolator).start();
            this.textureView.animate().cancel();
            ViewPropertyAnimator animate = this.textureView.animate();
            if (z) {
                f = 0.0f;
            }
            animate.alpha(f).setDuration(150).setInterpolator(cubicBezierInterpolator).start();
            this.placeholderShown = z;
        }
        if (((float) this.pipWidth) != ((float) getSuggestedWidth()) * this.scaleFactor || ((float) this.pipHeight) != ((float) getSuggestedHeight()) * this.scaleFactor) {
            WindowManager.LayoutParams layoutParams = this.windowLayoutParams;
            int suggestedWidth = (int) (((float) getSuggestedWidth()) * this.scaleFactor);
            this.pipWidth = suggestedWidth;
            layoutParams.width = suggestedWidth;
            WindowManager.LayoutParams layoutParams2 = this.windowLayoutParams;
            int suggestedHeight = (int) (((float) getSuggestedHeight()) * this.scaleFactor);
            this.pipHeight = suggestedHeight;
            layoutParams2.height = suggestedHeight;
            this.windowManager.updateViewLayout(this.contentView, this.windowLayoutParams);
            SpringForce spring = ((SpringAnimation) this.pipXSpring.setStartValue(this.pipX)).getSpring();
            float suggestedWidth2 = this.pipX + ((((float) getSuggestedWidth()) * this.scaleFactor) / 2.0f);
            int i = AndroidUtilities.displaySize.x;
            spring.setFinalPosition(suggestedWidth2 >= ((float) i) / 2.0f ? (((float) i) - (((float) getSuggestedWidth()) * this.scaleFactor)) - ((float) AndroidUtilities.dp(16.0f)) : (float) AndroidUtilities.dp(16.0f));
            this.pipXSpring.start();
            ((SpringAnimation) this.pipYSpring.setStartValue(this.pipY)).getSpring().setFinalPosition(MathUtils.clamp(this.pipY, (float) AndroidUtilities.dp(16.0f), (((float) AndroidUtilities.displaySize.y) - (((float) getSuggestedHeight()) * this.scaleFactor)) - ((float) AndroidUtilities.dp(16.0f))));
            this.pipYSpring.start();
        }
    }

    @SuppressLint({"WrongConstant"})
    private WindowManager.LayoutParams createWindowLayoutParams() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.gravity = 51;
        layoutParams.format = -3;
        if (!AndroidUtilities.checkInlinePermissions(ApplicationLoader.applicationContext)) {
            layoutParams.type = 2999;
        } else if (Build.VERSION.SDK_INT >= 26) {
            layoutParams.type = 2038;
        } else {
            layoutParams.type = 2003;
        }
        layoutParams.flags = 520;
        return layoutParams;
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.didEndCall) {
            dismiss();
        } else if (i == NotificationCenter.groupCallUpdated) {
            bindTextureView();
        }
    }
}
