package org.telegram.ui.Components.voip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Path;
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
import java.util.List;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.voip.VideoCapturerDevice;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.TLRPC;
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
    private static final FloatPropertyCompat<RTMPStreamPipOverlay> PIP_X_PROPERTY = new SimpleFloatPropertyCompat("pipX", RTMPStreamPipOverlay$$ExternalSyntheticLambda5.INSTANCE, RTMPStreamPipOverlay$$ExternalSyntheticLambda7.INSTANCE);
    private static final FloatPropertyCompat<RTMPStreamPipOverlay> PIP_Y_PROPERTY = new SimpleFloatPropertyCompat("pipY", RTMPStreamPipOverlay$$ExternalSyntheticLambda6.INSTANCE, RTMPStreamPipOverlay$$ExternalSyntheticLambda8.INSTANCE);
    private static final float ROUNDED_CORNERS_DP = 10.0f;
    private static final float SIDE_PADDING_DP = 16.0f;
    private static RTMPStreamPipOverlay instance = new RTMPStreamPipOverlay();
    private AccountInstance accountInstance;
    /* access modifiers changed from: private */
    public Float aspectRatio;
    private BackupImageView avatarImageView;
    /* access modifiers changed from: private */
    public TLRPC.TL_groupCallParticipant boundParticipant;
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

    static /* synthetic */ void lambda$static$1(RTMPStreamPipOverlay obj, float value) {
        WindowManager.LayoutParams layoutParams = obj.windowLayoutParams;
        obj.pipX = value;
        layoutParams.x = (int) value;
        obj.windowManager.updateViewLayout(obj.contentView, obj.windowLayoutParams);
    }

    static /* synthetic */ void lambda$static$3(RTMPStreamPipOverlay obj, float value) {
        WindowManager.LayoutParams layoutParams = obj.windowLayoutParams;
        obj.pipY = value;
        layoutParams.y = (int) value;
        obj.windowManager.updateViewLayout(obj.contentView, obj.windowLayoutParams);
    }

    /* renamed from: lambda$new$4$org-telegram-ui-Components-voip-RTMPStreamPipOverlay  reason: not valid java name */
    public /* synthetic */ void m4571xc9eadd9b() {
        this.isShowingControls = false;
        toggleControls(false);
        this.postedDismissControls = false;
    }

    public static boolean isVisible() {
        return instance.isVisible;
    }

    /* access modifiers changed from: private */
    public int getSuggestedWidth() {
        if (getRatio() >= 1.0f) {
            return (int) (((float) Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) * 0.35f);
        }
        return (int) (((float) Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) * 0.6f);
    }

    /* access modifiers changed from: private */
    public int getSuggestedHeight() {
        return (int) (((float) getSuggestedWidth()) * getRatio());
    }

    private float getRatio() {
        if (this.aspectRatio == null) {
            float ratio = 0.5625f;
            if (VoIPService.getSharedInstance() != null && !VoIPService.getSharedInstance().groupCall.visibleVideoParticipants.isEmpty()) {
                ChatObject.VideoParticipant videoParticipant = VoIPService.getSharedInstance().groupCall.visibleVideoParticipants.get(0);
                if (videoParticipant.aspectRatio != 0.0f) {
                    ratio = 1.0f / videoParticipant.aspectRatio;
                }
            }
            this.aspectRatio = Float.valueOf(ratio);
            this.maxScaleFactor = ((float) (Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) - AndroidUtilities.dp(32.0f))) / ((float) getSuggestedWidth());
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
        this.scaleAnimator = duration;
        duration.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.scaleAnimator.addUpdateListener(new RTMPStreamPipOverlay$$ExternalSyntheticLambda0(this));
        this.scaleAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                ValueAnimator unused = RTMPStreamPipOverlay.this.scaleAnimator = null;
            }
        });
        this.scaleAnimator.start();
    }

    /* renamed from: lambda$toggleControls$5$org-telegram-ui-Components-voip-RTMPStreamPipOverlay  reason: not valid java name */
    public /* synthetic */ void m4572x4e63256(ValueAnimator animation) {
        this.controlsView.setAlpha(((Float) animation.getAnimatedValue()).floatValue());
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
            AnimatorSet set = new AnimatorSet();
            set.setDuration(250);
            set.setInterpolator(CubicBezierInterpolator.DEFAULT);
            set.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.contentView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.contentView, View.SCALE_X, new float[]{0.1f}), ObjectAnimator.ofFloat(this.contentView, View.SCALE_Y, new float[]{0.1f})});
            set.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    RTMPStreamPipOverlay.this.windowManager.removeViewImmediate(RTMPStreamPipOverlay.this.contentView);
                    RTMPStreamPipOverlay.this.textureView.renderer.release();
                    TLRPC.TL_groupCallParticipant unused = RTMPStreamPipOverlay.this.boundParticipant = null;
                    boolean unused2 = RTMPStreamPipOverlay.this.placeholderShown = true;
                    boolean unused3 = RTMPStreamPipOverlay.this.firstFrameRendered = false;
                    View unused4 = RTMPStreamPipOverlay.this.consumingChild = null;
                    boolean unused5 = RTMPStreamPipOverlay.this.isScrolling = false;
                }
            });
            set.start();
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
            final int touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
            this.scaleGestureDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.OnScaleGestureListener() {
                public boolean onScale(ScaleGestureDetector detector) {
                    RTMPStreamPipOverlay rTMPStreamPipOverlay = RTMPStreamPipOverlay.this;
                    float unused = rTMPStreamPipOverlay.scaleFactor = MathUtils.clamp(rTMPStreamPipOverlay.scaleFactor * detector.getScaleFactor(), RTMPStreamPipOverlay.this.minScaleFactor, RTMPStreamPipOverlay.this.maxScaleFactor);
                    RTMPStreamPipOverlay rTMPStreamPipOverlay2 = RTMPStreamPipOverlay.this;
                    int unused2 = rTMPStreamPipOverlay2.pipWidth = (int) (((float) rTMPStreamPipOverlay2.getSuggestedWidth()) * RTMPStreamPipOverlay.this.scaleFactor);
                    RTMPStreamPipOverlay rTMPStreamPipOverlay3 = RTMPStreamPipOverlay.this;
                    int unused3 = rTMPStreamPipOverlay3.pipHeight = (int) (((float) rTMPStreamPipOverlay3.getSuggestedHeight()) * RTMPStreamPipOverlay.this.scaleFactor);
                    AndroidUtilities.runOnUIThread(new RTMPStreamPipOverlay$3$$ExternalSyntheticLambda0(this));
                    ((SpringAnimation) RTMPStreamPipOverlay.this.pipXSpring.setStartValue(RTMPStreamPipOverlay.this.pipX)).getSpring().setFinalPosition((float) (detector.getFocusX() >= ((float) AndroidUtilities.displaySize.x) / 2.0f ? (AndroidUtilities.displaySize.x - RTMPStreamPipOverlay.this.pipWidth) - AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(16.0f)));
                    if (!RTMPStreamPipOverlay.this.pipXSpring.isRunning()) {
                        RTMPStreamPipOverlay.this.pipXSpring.start();
                    }
                    ((SpringAnimation) RTMPStreamPipOverlay.this.pipYSpring.setStartValue(RTMPStreamPipOverlay.this.pipY)).getSpring().setFinalPosition(MathUtils.clamp(detector.getFocusY() - (((float) RTMPStreamPipOverlay.this.pipHeight) / 2.0f), (float) AndroidUtilities.dp(16.0f), (float) ((AndroidUtilities.displaySize.y - RTMPStreamPipOverlay.this.pipHeight) - AndroidUtilities.dp(16.0f))));
                    if (RTMPStreamPipOverlay.this.pipYSpring.isRunning()) {
                        return true;
                    }
                    RTMPStreamPipOverlay.this.pipYSpring.start();
                    return true;
                }

                /* renamed from: lambda$onScale$0$org-telegram-ui-Components-voip-RTMPStreamPipOverlay$3  reason: not valid java name */
                public /* synthetic */ void m4573xe5a0dec3() {
                    RTMPStreamPipOverlay.this.contentFrameLayout.invalidate();
                    if (Build.VERSION.SDK_INT < 18 || !RTMPStreamPipOverlay.this.contentFrameLayout.isInLayout()) {
                        RTMPStreamPipOverlay.this.contentFrameLayout.requestLayout();
                        RTMPStreamPipOverlay.this.contentView.requestLayout();
                        RTMPStreamPipOverlay.this.textureView.requestLayout();
                    }
                }

                public boolean onScaleBegin(ScaleGestureDetector detector) {
                    if (RTMPStreamPipOverlay.this.isScrolling) {
                        boolean unused = RTMPStreamPipOverlay.this.isScrolling = false;
                    }
                    boolean unused2 = RTMPStreamPipOverlay.this.isScrollDisallowed = true;
                    RTMPStreamPipOverlay.this.windowLayoutParams.width = (int) (((float) RTMPStreamPipOverlay.this.getSuggestedWidth()) * RTMPStreamPipOverlay.this.maxScaleFactor);
                    RTMPStreamPipOverlay.this.windowLayoutParams.height = (int) (((float) RTMPStreamPipOverlay.this.getSuggestedHeight()) * RTMPStreamPipOverlay.this.maxScaleFactor);
                    RTMPStreamPipOverlay.this.windowManager.updateViewLayout(RTMPStreamPipOverlay.this.contentView, RTMPStreamPipOverlay.this.windowLayoutParams);
                    return true;
                }

                public void onScaleEnd(ScaleGestureDetector detector) {
                    if (RTMPStreamPipOverlay.this.pipXSpring.isRunning() || RTMPStreamPipOverlay.this.pipYSpring.isRunning()) {
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
                        if (!RTMPStreamPipOverlay.this.pipXSpring.isRunning()) {
                            springs.add(RTMPStreamPipOverlay.this.pipXSpring);
                        } else {
                            RTMPStreamPipOverlay.this.pipXSpring.addEndListener(endListener);
                        }
                        if (!RTMPStreamPipOverlay.this.pipYSpring.isRunning()) {
                            springs.add(RTMPStreamPipOverlay.this.pipYSpring);
                        } else {
                            RTMPStreamPipOverlay.this.pipYSpring.addEndListener(endListener);
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
            if (Build.VERSION.SDK_INT >= 19) {
                this.scaleGestureDetector.setQuickScaleEnabled(false);
            }
            if (Build.VERSION.SDK_INT >= 23) {
                this.scaleGestureDetector.setStylusScaleEnabled(false);
            }
            this.gestureDetector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {
                private float startPipX;
                private float startPipY;

                public boolean onDown(MotionEvent e) {
                    if (RTMPStreamPipOverlay.this.isShowingControls) {
                        for (int i = 1; i < RTMPStreamPipOverlay.this.contentFrameLayout.getChildCount(); i++) {
                            View child = RTMPStreamPipOverlay.this.contentFrameLayout.getChildAt(i);
                            if (child.dispatchTouchEvent(e)) {
                                View unused = RTMPStreamPipOverlay.this.consumingChild = child;
                                return true;
                            }
                        }
                    }
                    this.startPipX = RTMPStreamPipOverlay.this.pipX;
                    this.startPipY = RTMPStreamPipOverlay.this.pipY;
                    return true;
                }

                public boolean onSingleTapUp(MotionEvent e) {
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

                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    if (!RTMPStreamPipOverlay.this.isScrolling || RTMPStreamPipOverlay.this.isScrollDisallowed) {
                        return false;
                    }
                    ((SpringAnimation) ((SpringAnimation) RTMPStreamPipOverlay.this.pipXSpring.setStartVelocity(velocityX)).setStartValue(RTMPStreamPipOverlay.this.pipX)).getSpring().setFinalPosition((float) ((RTMPStreamPipOverlay.this.pipX + (((float) RTMPStreamPipOverlay.this.pipWidth) / 2.0f)) + (velocityX / 7.0f) >= ((float) AndroidUtilities.displaySize.x) / 2.0f ? (AndroidUtilities.displaySize.x - RTMPStreamPipOverlay.this.pipWidth) - AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(16.0f)));
                    RTMPStreamPipOverlay.this.pipXSpring.start();
                    ((SpringAnimation) ((SpringAnimation) RTMPStreamPipOverlay.this.pipYSpring.setStartVelocity(velocityX)).setStartValue(RTMPStreamPipOverlay.this.pipY)).getSpring().setFinalPosition(MathUtils.clamp(RTMPStreamPipOverlay.this.pipY + (velocityY / 10.0f), (float) AndroidUtilities.dp(16.0f), (float) ((AndroidUtilities.displaySize.y - RTMPStreamPipOverlay.this.pipHeight) - AndroidUtilities.dp(16.0f))));
                    RTMPStreamPipOverlay.this.pipYSpring.start();
                    return true;
                }

                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                    if (!RTMPStreamPipOverlay.this.isScrolling && RTMPStreamPipOverlay.this.scaleAnimator == null && !RTMPStreamPipOverlay.this.isScrollDisallowed && (Math.abs(distanceX) >= ((float) touchSlop) || Math.abs(distanceY) >= ((float) touchSlop))) {
                        boolean unused = RTMPStreamPipOverlay.this.isScrolling = true;
                        RTMPStreamPipOverlay.this.pipXSpring.cancel();
                        RTMPStreamPipOverlay.this.pipYSpring.cancel();
                    }
                    if (RTMPStreamPipOverlay.this.isScrolling) {
                        RTMPStreamPipOverlay.this.windowLayoutParams.x = (int) RTMPStreamPipOverlay.this.pipX = (this.startPipX + e2.getRawX()) - e1.getRawX();
                        RTMPStreamPipOverlay.this.windowLayoutParams.y = (int) RTMPStreamPipOverlay.this.pipY = (this.startPipY + e2.getRawY()) - e1.getRawY();
                        RTMPStreamPipOverlay.this.windowManager.updateViewLayout(RTMPStreamPipOverlay.this.contentView, RTMPStreamPipOverlay.this.windowLayoutParams);
                    }
                    return true;
                }
            });
            this.contentFrameLayout = new FrameLayout(context) {
                private Path path = new Path();

                public boolean dispatchTouchEvent(MotionEvent ev) {
                    int action = ev.getAction();
                    if (RTMPStreamPipOverlay.this.consumingChild != null) {
                        MotionEvent newEvent = MotionEvent.obtain(ev);
                        newEvent.offsetLocation(RTMPStreamPipOverlay.this.consumingChild.getX(), RTMPStreamPipOverlay.this.consumingChild.getY());
                        boolean consumed = RTMPStreamPipOverlay.this.consumingChild.dispatchTouchEvent(ev);
                        newEvent.recycle();
                        if (action == 1 || action == 3) {
                            View unused = RTMPStreamPipOverlay.this.consumingChild = null;
                        }
                        if (consumed) {
                            return true;
                        }
                    }
                    MotionEvent newEvent2 = MotionEvent.obtain(ev);
                    newEvent2.offsetLocation(ev.getRawX() - ev.getX(), ev.getRawY() - ev.getY());
                    boolean scaleDetector = RTMPStreamPipOverlay.this.scaleGestureDetector.onTouchEvent(newEvent2);
                    newEvent2.recycle();
                    boolean detector = !RTMPStreamPipOverlay.this.scaleGestureDetector.isInProgress() && RTMPStreamPipOverlay.this.gestureDetector.onTouchEvent(ev);
                    if (action == 1 || action == 3) {
                        boolean unused2 = RTMPStreamPipOverlay.this.isScrolling = false;
                        boolean unused3 = RTMPStreamPipOverlay.this.isScrollDisallowed = false;
                        if (!RTMPStreamPipOverlay.this.pipXSpring.isRunning()) {
                            ((SpringAnimation) RTMPStreamPipOverlay.this.pipXSpring.setStartValue(RTMPStreamPipOverlay.this.pipX)).getSpring().setFinalPosition((float) (RTMPStreamPipOverlay.this.pipX + (((float) RTMPStreamPipOverlay.this.pipWidth) / 2.0f) >= ((float) AndroidUtilities.displaySize.x) / 2.0f ? (AndroidUtilities.displaySize.x - RTMPStreamPipOverlay.this.pipWidth) - AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(16.0f)));
                            RTMPStreamPipOverlay.this.pipXSpring.start();
                        }
                        if (!RTMPStreamPipOverlay.this.pipYSpring.isRunning()) {
                            ((SpringAnimation) RTMPStreamPipOverlay.this.pipYSpring.setStartValue(RTMPStreamPipOverlay.this.pipY)).getSpring().setFinalPosition(MathUtils.clamp(RTMPStreamPipOverlay.this.pipY, (float) AndroidUtilities.dp(16.0f), (float) ((AndroidUtilities.displaySize.y - RTMPStreamPipOverlay.this.pipHeight) - AndroidUtilities.dp(16.0f))));
                            RTMPStreamPipOverlay.this.pipYSpring.start();
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
                public void onSizeChanged(int w, int h, int oldw, int oldh) {
                    super.onSizeChanged(w, h, oldw, oldh);
                    this.path.rewind();
                    AndroidUtilities.rectTmp.set(0.0f, 0.0f, (float) w, (float) h);
                    this.path.addRoundRect(AndroidUtilities.rectTmp, (float) AndroidUtilities.dp(10.0f), (float) AndroidUtilities.dp(10.0f), Path.Direction.CW);
                }
            };
            AnonymousClass6 r6 = new ViewGroup(context) {
                /* access modifiers changed from: protected */
                public void onLayout(boolean changed, int l, int t, int r, int b) {
                    RTMPStreamPipOverlay.this.contentFrameLayout.layout(0, 0, RTMPStreamPipOverlay.this.pipWidth, RTMPStreamPipOverlay.this.pipHeight);
                }

                /* access modifiers changed from: protected */
                public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.getSize(heightMeasureSpec));
                    RTMPStreamPipOverlay.this.contentFrameLayout.measure(View.MeasureSpec.makeMeasureSpec(RTMPStreamPipOverlay.this.pipWidth, NUM), View.MeasureSpec.makeMeasureSpec(RTMPStreamPipOverlay.this.pipHeight, NUM));
                }
            };
            this.contentView = r6;
            r6.addView(this.contentFrameLayout, LayoutHelper.createFrame(-1, -1.0f));
            if (Build.VERSION.SDK_INT >= 21) {
                this.contentFrameLayout.setOutlineProvider(new ViewOutlineProvider() {
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
            this.textureView.scaleType = VoIPTextureView.SCALE_TYPE_FILL;
            this.textureView.renderer.setRotateTextureWithScreen(true);
            this.textureView.renderer.init(VideoCapturerDevice.getEglBase().getEglBaseContext(), new RendererCommon.RendererEvents() {
                public void onFirstFrameRendered() {
                    boolean unused = RTMPStreamPipOverlay.this.firstFrameRendered = true;
                    AndroidUtilities.runOnUIThread(new RTMPStreamPipOverlay$8$$ExternalSyntheticLambda0(this));
                }

                /* renamed from: lambda$onFirstFrameRendered$0$org-telegram-ui-Components-voip-RTMPStreamPipOverlay$8  reason: not valid java name */
                public /* synthetic */ void m4574xadca3CLASSNAME() {
                    RTMPStreamPipOverlay.this.bindTextureView();
                }

                public void onFrameResolutionChanged(int videoWidth, int videoHeight, int rotation) {
                    if ((rotation / 90) % 2 == 0) {
                        Float unused = RTMPStreamPipOverlay.this.aspectRatio = Float.valueOf(((float) videoHeight) / ((float) videoWidth));
                    } else {
                        Float unused2 = RTMPStreamPipOverlay.this.aspectRatio = Float.valueOf(((float) videoWidth) / ((float) videoHeight));
                    }
                    AndroidUtilities.runOnUIThread(new RTMPStreamPipOverlay$8$$ExternalSyntheticLambda1(this));
                }

                /* renamed from: lambda$onFrameResolutionChanged$1$org-telegram-ui-Components-voip-RTMPStreamPipOverlay$8  reason: not valid java name */
                public /* synthetic */ void m4575xae41e31e() {
                    RTMPStreamPipOverlay.this.bindTextureView();
                }
            });
            this.contentFrameLayout.addView(this.textureView, LayoutHelper.createFrame(-1, -1.0f));
            AnonymousClass9 r7 = new View(context) {
                /* access modifiers changed from: protected */
                public void onDraw(Canvas canvas) {
                    if (getAlpha() != 0.0f) {
                        AndroidUtilities.rectTmp.set(0.0f, 0.0f, (float) getWidth(), (float) getHeight());
                        RTMPStreamPipOverlay.this.cellFlickerDrawable.draw(canvas, AndroidUtilities.rectTmp, (float) AndroidUtilities.dp(10.0f));
                        invalidate();
                    }
                }

                /* access modifiers changed from: protected */
                public void onSizeChanged(int w, int h, int oldw, int oldh) {
                    super.onSizeChanged(w, h, oldw, oldh);
                    RTMPStreamPipOverlay.this.cellFlickerDrawable.setParentWidth(w);
                }
            };
            this.flickerView = r7;
            this.contentFrameLayout.addView(r7, LayoutHelper.createFrame(-1, -1.0f));
            FrameLayout frameLayout = new FrameLayout(context);
            this.controlsView = frameLayout;
            frameLayout.setAlpha(0.0f);
            View scrim = new View(context);
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setColors(new int[]{NUM, 0});
            gradientDrawable.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM);
            scrim.setBackground(gradientDrawable);
            this.controlsView.addView(scrim, LayoutHelper.createFrame(-1, -1.0f));
            int padding = AndroidUtilities.dp(8.0f);
            ImageView closeButton = new ImageView(context);
            closeButton.setImageResource(NUM);
            closeButton.setColorFilter(Theme.getColor("voipgroup_actionBarItems"));
            closeButton.setBackground(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21")));
            closeButton.setPadding(padding, padding, padding, padding);
            closeButton.setOnClickListener(RTMPStreamPipOverlay$$ExternalSyntheticLambda2.INSTANCE);
            this.controlsView.addView(closeButton, LayoutHelper.createFrame(38, (float) 38, 5, 0.0f, (float) 4, (float) 4, 0.0f));
            ImageView expandButton = new ImageView(context);
            expandButton.setImageResource(NUM);
            expandButton.setColorFilter(Theme.getColor("voipgroup_actionBarItems"));
            expandButton.setBackground(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21")));
            expandButton.setPadding(padding, padding, padding, padding);
            expandButton.setOnClickListener(new RTMPStreamPipOverlay$$ExternalSyntheticLambda1(context));
            this.controlsView.addView(expandButton, LayoutHelper.createFrame(38, (float) 38, 5, 0.0f, (float) 4, (float) (38 + 4 + 6), 0.0f));
            this.contentFrameLayout.addView(this.controlsView, LayoutHelper.createFrame(-1, -1.0f));
            this.windowManager = (WindowManager) ApplicationLoader.applicationContext.getSystemService("window");
            WindowManager.LayoutParams createWindowLayoutParams = createWindowLayoutParams();
            this.windowLayoutParams = createWindowLayoutParams;
            createWindowLayoutParams.width = this.pipWidth;
            this.windowLayoutParams.height = this.pipHeight;
            WindowManager.LayoutParams layoutParams = this.windowLayoutParams;
            float dp = (float) ((AndroidUtilities.displaySize.x - this.pipWidth) - AndroidUtilities.dp(16.0f));
            this.pipX = dp;
            layoutParams.x = (int) dp;
            WindowManager.LayoutParams layoutParams2 = this.windowLayoutParams;
            float dp2 = (float) ((AndroidUtilities.displaySize.y - this.pipHeight) - AndroidUtilities.dp(16.0f));
            this.pipY = dp2;
            layoutParams2.y = (int) dp2;
            this.windowLayoutParams.dimAmount = 0.0f;
            this.windowLayoutParams.flags = 520;
            this.contentView.setAlpha(0.0f);
            this.contentView.setScaleX(0.1f);
            this.contentView.setScaleY(0.1f);
            this.windowManager.addView(this.contentView, this.windowLayoutParams);
            AnimatorSet set = new AnimatorSet();
            set.setDuration(250);
            set.setInterpolator(CubicBezierInterpolator.DEFAULT);
            ImageView imageView = expandButton;
            set.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.contentView, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.contentView, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.contentView, View.SCALE_Y, new float[]{1.0f})});
            set.start();
            bindTextureView();
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.groupCallVisibilityChanged, new Object[0]);
        }
    }

    static /* synthetic */ void lambda$showInternal$8(Context context, View v) {
        if (VoIPService.getSharedInstance() != null) {
            Intent intent = new Intent(context, LaunchActivity.class).setAction("voip_chat");
            intent.putExtra("currentAccount", VoIPService.getSharedInstance().getAccount());
            if (!(context instanceof Activity)) {
                intent.addFlags(NUM);
            }
            context.startActivity(intent);
            dismiss();
        }
    }

    /* access modifiers changed from: private */
    public void bindTextureView() {
        TLRPC.TL_groupCallParticipant tL_groupCallParticipant;
        boolean z = true;
        if (VoIPService.getSharedInstance() != null && VoIPService.getSharedInstance().groupCall != null && !VoIPService.getSharedInstance().groupCall.visibleVideoParticipants.isEmpty()) {
            TLRPC.TL_groupCallParticipant participant = VoIPService.getSharedInstance().groupCall.visibleVideoParticipants.get(0).participant;
            TLRPC.TL_groupCallParticipant tL_groupCallParticipant2 = this.boundParticipant;
            if (tL_groupCallParticipant2 == null || MessageObject.getPeerId(tL_groupCallParticipant2.peer) != MessageObject.getPeerId(participant.peer)) {
                if (this.boundParticipant != null) {
                    VoIPService.getSharedInstance().removeRemoteSink(this.boundParticipant, this.boundPresentation);
                }
                this.boundPresentation = participant.presentation != null;
                if (participant.self) {
                    VoIPService.getSharedInstance().setSinks(this.textureView.renderer, this.boundPresentation, (VideoSink) null);
                } else {
                    VoIPService.getSharedInstance().addRemoteSink(participant, this.boundPresentation, this.textureView.renderer, (VideoSink) null);
                }
                MessagesController messagesController = VoIPService.getSharedInstance().groupCall.currentAccount.getMessagesController();
                long peerId = MessageObject.getPeerId(participant.peer);
                if (peerId > 0) {
                    TLRPC.User user = messagesController.getUser(Long.valueOf(peerId));
                    ImageLocation imageLocation = ImageLocation.getForUser(user, 1);
                    int color = user != null ? AvatarDrawable.getColorForId(user.id) : ColorUtils.blendARGB(-16777216, -1, 0.2f);
                    this.avatarImageView.getImageReceiver().setImage(imageLocation, "50_50_b", new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{ColorUtils.blendARGB(color, -16777216, 0.2f), ColorUtils.blendARGB(color, -16777216, 0.4f)}), (String) null, user, 0);
                } else {
                    TLRPC.Chat chat = messagesController.getChat(Long.valueOf(-peerId));
                    ImageLocation imageLocation2 = ImageLocation.getForChat(chat, 1);
                    int color2 = chat != null ? AvatarDrawable.getColorForId(chat.id) : ColorUtils.blendARGB(-16777216, -1, 0.2f);
                    this.avatarImageView.getImageReceiver().setImage(imageLocation2, "50_50_b", new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{ColorUtils.blendARGB(color2, -16777216, 0.2f), ColorUtils.blendARGB(color2, -16777216, 0.4f)}), (String) null, chat, 0);
                }
                this.boundParticipant = participant;
            }
        } else if (this.boundParticipant != null) {
            VoIPService.getSharedInstance().removeRemoteSink(this.boundParticipant, false);
            this.boundParticipant = null;
        }
        if (this.firstFrameRendered && (tL_groupCallParticipant = this.boundParticipant) != null && (!(tL_groupCallParticipant.video == null && this.boundParticipant.presentation == null) && ((this.boundParticipant.video == null || !this.boundParticipant.video.paused) && (this.boundParticipant.presentation == null || !this.boundParticipant.presentation.paused)))) {
            z = false;
        }
        boolean showPlaceholder = z;
        if (this.placeholderShown != showPlaceholder) {
            this.flickerView.animate().cancel();
            float f = 1.0f;
            this.flickerView.animate().alpha(showPlaceholder ? 1.0f : 0.0f).setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            this.avatarImageView.animate().cancel();
            this.avatarImageView.animate().alpha(showPlaceholder ? 1.0f : 0.0f).setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            this.textureView.animate().cancel();
            ViewPropertyAnimator animate = this.textureView.animate();
            if (showPlaceholder) {
                f = 0.0f;
            }
            animate.alpha(f).setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            this.placeholderShown = showPlaceholder;
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
            ((SpringAnimation) this.pipXSpring.setStartValue(this.pipX)).getSpring().setFinalPosition(this.pipX + ((((float) getSuggestedWidth()) * this.scaleFactor) / 2.0f) >= ((float) AndroidUtilities.displaySize.x) / 2.0f ? (((float) AndroidUtilities.displaySize.x) - (((float) getSuggestedWidth()) * this.scaleFactor)) - ((float) AndroidUtilities.dp(16.0f)) : (float) AndroidUtilities.dp(16.0f));
            this.pipXSpring.start();
            ((SpringAnimation) this.pipYSpring.setStartValue(this.pipY)).getSpring().setFinalPosition(MathUtils.clamp(this.pipY, (float) AndroidUtilities.dp(16.0f), (((float) AndroidUtilities.displaySize.y) - (((float) getSuggestedHeight()) * this.scaleFactor)) - ((float) AndroidUtilities.dp(16.0f))));
            this.pipYSpring.start();
        }
    }

    private WindowManager.LayoutParams createWindowLayoutParams() {
        WindowManager.LayoutParams windowLayoutParams2 = new WindowManager.LayoutParams();
        windowLayoutParams2.gravity = 51;
        windowLayoutParams2.format = -3;
        if (!AndroidUtilities.checkInlinePermissions(ApplicationLoader.applicationContext)) {
            windowLayoutParams2.type = 2999;
        } else if (Build.VERSION.SDK_INT >= 26) {
            windowLayoutParams2.type = 2038;
        } else {
            windowLayoutParams2.type = 2003;
        }
        windowLayoutParams2.flags = 520;
        return windowLayoutParams2;
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.didEndCall) {
            dismiss();
        } else if (id == NotificationCenter.groupCallUpdated) {
            bindTextureView();
        }
    }
}
