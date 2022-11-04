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
import org.telegram.messenger.R;
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
import org.telegram.ui.Components.voip.RTMPStreamPipOverlay;
import org.telegram.ui.LaunchActivity;
import org.webrtc.RendererCommon;
/* loaded from: classes3.dex */
public class RTMPStreamPipOverlay implements NotificationCenter.NotificationCenterDelegate {
    private static final FloatPropertyCompat<RTMPStreamPipOverlay> PIP_X_PROPERTY = new SimpleFloatPropertyCompat("pipX", RTMPStreamPipOverlay$$ExternalSyntheticLambda6.INSTANCE, RTMPStreamPipOverlay$$ExternalSyntheticLambda8.INSTANCE);
    private static final FloatPropertyCompat<RTMPStreamPipOverlay> PIP_Y_PROPERTY = new SimpleFloatPropertyCompat("pipY", RTMPStreamPipOverlay$$ExternalSyntheticLambda5.INSTANCE, RTMPStreamPipOverlay$$ExternalSyntheticLambda7.INSTANCE);
    @SuppressLint({"StaticFieldLeak"})
    private static RTMPStreamPipOverlay instance = new RTMPStreamPipOverlay();
    private AccountInstance accountInstance;
    private Float aspectRatio;
    private BackupImageView avatarImageView;
    private TLRPC$TL_groupCallParticipant boundParticipant;
    private boolean boundPresentation;
    private View consumingChild;
    private FrameLayout contentFrameLayout;
    private ViewGroup contentView;
    private FrameLayout controlsView;
    private boolean firstFrameRendered;
    private View flickerView;
    private GestureDetectorCompat gestureDetector;
    private boolean isScrollDisallowed;
    private boolean isScrolling;
    private boolean isShowingControls;
    private boolean isVisible;
    private int pipHeight;
    private int pipWidth;
    private float pipX;
    private SpringAnimation pipXSpring;
    private float pipY;
    private SpringAnimation pipYSpring;
    private boolean postedDismissControls;
    private ValueAnimator scaleAnimator;
    private ScaleGestureDetector scaleGestureDetector;
    private VoIPTextureView textureView;
    private WindowManager.LayoutParams windowLayoutParams;
    private WindowManager windowManager;
    private float minScaleFactor = 0.6f;
    private float maxScaleFactor = 1.4f;
    private CellFlickerDrawable cellFlickerDrawable = new CellFlickerDrawable();
    private boolean placeholderShown = true;
    private float scaleFactor = 1.0f;
    private Runnable dismissControlsCallback = new Runnable() { // from class: org.telegram.ui.Components.voip.RTMPStreamPipOverlay$$ExternalSyntheticLambda3
        @Override // java.lang.Runnable
        public final void run() {
            RTMPStreamPipOverlay.this.lambda$new$4();
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$static$1(RTMPStreamPipOverlay rTMPStreamPipOverlay, float f) {
        WindowManager.LayoutParams layoutParams = rTMPStreamPipOverlay.windowLayoutParams;
        rTMPStreamPipOverlay.pipX = f;
        layoutParams.x = (int) f;
        rTMPStreamPipOverlay.windowManager.updateViewLayout(rTMPStreamPipOverlay.contentView, layoutParams);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$static$3(RTMPStreamPipOverlay rTMPStreamPipOverlay, float f) {
        WindowManager.LayoutParams layoutParams = rTMPStreamPipOverlay.windowLayoutParams;
        rTMPStreamPipOverlay.pipY = f;
        layoutParams.y = (int) f;
        rTMPStreamPipOverlay.windowManager.updateViewLayout(rTMPStreamPipOverlay.contentView, layoutParams);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$4() {
        this.isShowingControls = false;
        toggleControls(false);
        this.postedDismissControls = false;
    }

    public static boolean isVisible() {
        return instance.isVisible;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getSuggestedWidth() {
        float min;
        float f;
        if (getRatio() >= 1.0f) {
            Point point = AndroidUtilities.displaySize;
            min = Math.min(point.x, point.y);
            f = 0.35f;
        } else {
            Point point2 = AndroidUtilities.displaySize;
            min = Math.min(point2.x, point2.y);
            f = 0.6f;
        }
        return (int) (min * f);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getSuggestedHeight() {
        return (int) (getSuggestedWidth() * getRatio());
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
            this.maxScaleFactor = (Math.min(point.x, point.y) - AndroidUtilities.dp(32.0f)) / getSuggestedWidth();
        }
        return this.aspectRatio.floatValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void toggleControls(boolean z) {
        float[] fArr = new float[2];
        float f = 0.0f;
        fArr[0] = z ? 0.0f : 1.0f;
        if (z) {
            f = 1.0f;
        }
        fArr[1] = f;
        ValueAnimator duration = ValueAnimator.ofFloat(fArr).setDuration(200L);
        this.scaleAnimator = duration;
        duration.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.scaleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.voip.RTMPStreamPipOverlay$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                RTMPStreamPipOverlay.this.lambda$toggleControls$5(valueAnimator);
            }
        });
        this.scaleAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.voip.RTMPStreamPipOverlay.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                RTMPStreamPipOverlay.this.scaleAnimator = null;
            }
        });
        this.scaleAnimator.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$toggleControls$5(ValueAnimator valueAnimator) {
        this.controlsView.setAlpha(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    public static void dismiss() {
        instance.dismissInternal();
    }

    private void dismissInternal() {
        if (!this.isVisible) {
            return;
        }
        this.isVisible = false;
        AndroidUtilities.runOnUIThread(RTMPStreamPipOverlay$$ExternalSyntheticLambda4.INSTANCE, 100L);
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
        animatorSet.setDuration(250L);
        animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
        animatorSet.playTogether(ObjectAnimator.ofFloat(this.contentView, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.contentView, View.SCALE_X, 0.1f), ObjectAnimator.ofFloat(this.contentView, View.SCALE_Y, 0.1f));
        animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.voip.RTMPStreamPipOverlay.2
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                RTMPStreamPipOverlay.this.windowManager.removeViewImmediate(RTMPStreamPipOverlay.this.contentView);
                RTMPStreamPipOverlay.this.textureView.renderer.release();
                RTMPStreamPipOverlay.this.boundParticipant = null;
                RTMPStreamPipOverlay.this.placeholderShown = true;
                RTMPStreamPipOverlay.this.firstFrameRendered = false;
                RTMPStreamPipOverlay.this.consumingChild = null;
                RTMPStreamPipOverlay.this.isScrolling = false;
            }
        });
        animatorSet.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$dismissInternal$6() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.groupCallVisibilityChanged, new Object[0]);
    }

    public static void show() {
        instance.showInternal();
    }

    private void showInternal() {
        if (VoIPService.getSharedInstance() == null || VoIPService.getSharedInstance().groupCall == null || this.isVisible) {
            return;
        }
        this.isVisible = true;
        AccountInstance accountInstance = VoIPService.getSharedInstance().groupCall.currentAccount;
        this.accountInstance = accountInstance;
        accountInstance.getNotificationCenter().addObserver(this, NotificationCenter.groupCallUpdated);
        this.accountInstance.getNotificationCenter().addObserver(this, NotificationCenter.applyGroupCallVisibleParticipants);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didEndCall);
        this.pipWidth = getSuggestedWidth();
        this.pipHeight = getSuggestedHeight();
        this.scaleFactor = 1.0f;
        this.isShowingControls = false;
        this.pipXSpring = new SpringAnimation(this, PIP_X_PROPERTY).setSpring(new SpringForce().setDampingRatio(0.75f).setStiffness(650.0f));
        this.pipYSpring = new SpringAnimation(this, PIP_Y_PROPERTY).setSpring(new SpringForce().setDampingRatio(0.75f).setStiffness(650.0f));
        final Context context = ApplicationLoader.applicationContext;
        final int scaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(context, new AnonymousClass3());
        this.scaleGestureDetector = scaleGestureDetector;
        int i = Build.VERSION.SDK_INT;
        if (i >= 19) {
            scaleGestureDetector.setQuickScaleEnabled(false);
        }
        if (i >= 23) {
            this.scaleGestureDetector.setStylusScaleEnabled(false);
        }
        this.gestureDetector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() { // from class: org.telegram.ui.Components.voip.RTMPStreamPipOverlay.4
            private float startPipX;
            private float startPipY;

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onDown(MotionEvent motionEvent) {
                if (RTMPStreamPipOverlay.this.isShowingControls) {
                    for (int i2 = 1; i2 < RTMPStreamPipOverlay.this.contentFrameLayout.getChildCount(); i2++) {
                        View childAt = RTMPStreamPipOverlay.this.contentFrameLayout.getChildAt(i2);
                        if (childAt.dispatchTouchEvent(motionEvent)) {
                            RTMPStreamPipOverlay.this.consumingChild = childAt;
                            return true;
                        }
                    }
                }
                this.startPipX = RTMPStreamPipOverlay.this.pipX;
                this.startPipY = RTMPStreamPipOverlay.this.pipY;
                return true;
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                if (RTMPStreamPipOverlay.this.scaleAnimator != null) {
                    return true;
                }
                if (RTMPStreamPipOverlay.this.postedDismissControls) {
                    AndroidUtilities.cancelRunOnUIThread(RTMPStreamPipOverlay.this.dismissControlsCallback);
                    RTMPStreamPipOverlay.this.postedDismissControls = false;
                }
                RTMPStreamPipOverlay rTMPStreamPipOverlay = RTMPStreamPipOverlay.this;
                rTMPStreamPipOverlay.isShowingControls = !rTMPStreamPipOverlay.isShowingControls;
                RTMPStreamPipOverlay rTMPStreamPipOverlay2 = RTMPStreamPipOverlay.this;
                rTMPStreamPipOverlay2.toggleControls(rTMPStreamPipOverlay2.isShowingControls);
                if (RTMPStreamPipOverlay.this.isShowingControls && !RTMPStreamPipOverlay.this.postedDismissControls) {
                    AndroidUtilities.runOnUIThread(RTMPStreamPipOverlay.this.dismissControlsCallback, 2500L);
                    RTMPStreamPipOverlay.this.postedDismissControls = true;
                }
                return true;
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
                if (!RTMPStreamPipOverlay.this.isScrolling || RTMPStreamPipOverlay.this.isScrollDisallowed) {
                    return false;
                }
                SpringForce spring = RTMPStreamPipOverlay.this.pipXSpring.setStartVelocity(f).setStartValue(RTMPStreamPipOverlay.this.pipX).getSpring();
                float f3 = RTMPStreamPipOverlay.this.pipX + (RTMPStreamPipOverlay.this.pipWidth / 2.0f) + (f / 7.0f);
                int i2 = AndroidUtilities.displaySize.x;
                spring.setFinalPosition(f3 >= ((float) i2) / 2.0f ? (i2 - RTMPStreamPipOverlay.this.pipWidth) - AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(16.0f));
                RTMPStreamPipOverlay.this.pipXSpring.start();
                RTMPStreamPipOverlay.this.pipYSpring.setStartVelocity(f).setStartValue(RTMPStreamPipOverlay.this.pipY).getSpring().setFinalPosition(MathUtils.clamp(RTMPStreamPipOverlay.this.pipY + (f2 / 10.0f), AndroidUtilities.dp(16.0f), (AndroidUtilities.displaySize.y - RTMPStreamPipOverlay.this.pipHeight) - AndroidUtilities.dp(16.0f)));
                RTMPStreamPipOverlay.this.pipYSpring.start();
                return true;
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
                if (!RTMPStreamPipOverlay.this.isScrolling && RTMPStreamPipOverlay.this.scaleAnimator == null && !RTMPStreamPipOverlay.this.isScrollDisallowed && (Math.abs(f) >= scaledTouchSlop || Math.abs(f2) >= scaledTouchSlop)) {
                    RTMPStreamPipOverlay.this.isScrolling = true;
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
        this.contentFrameLayout = new FrameLayout(context) { // from class: org.telegram.ui.Components.voip.RTMPStreamPipOverlay.5
            private Path path = new Path();

            @Override // android.view.ViewGroup, android.view.View
            public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if (RTMPStreamPipOverlay.this.consumingChild != null) {
                    MotionEvent obtain = MotionEvent.obtain(motionEvent);
                    obtain.offsetLocation(RTMPStreamPipOverlay.this.consumingChild.getX(), RTMPStreamPipOverlay.this.consumingChild.getY());
                    boolean dispatchTouchEvent = RTMPStreamPipOverlay.this.consumingChild.dispatchTouchEvent(motionEvent);
                    obtain.recycle();
                    if (action == 1 || action == 3) {
                        RTMPStreamPipOverlay.this.consumingChild = null;
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
                    RTMPStreamPipOverlay.this.isScrolling = false;
                    RTMPStreamPipOverlay.this.isScrollDisallowed = false;
                    if (!RTMPStreamPipOverlay.this.pipXSpring.isRunning()) {
                        SpringForce spring = RTMPStreamPipOverlay.this.pipXSpring.setStartValue(RTMPStreamPipOverlay.this.pipX).getSpring();
                        float f = RTMPStreamPipOverlay.this.pipX + (RTMPStreamPipOverlay.this.pipWidth / 2.0f);
                        int i2 = AndroidUtilities.displaySize.x;
                        spring.setFinalPosition(f >= ((float) i2) / 2.0f ? (i2 - RTMPStreamPipOverlay.this.pipWidth) - AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(16.0f));
                        RTMPStreamPipOverlay.this.pipXSpring.start();
                    }
                    if (!RTMPStreamPipOverlay.this.pipYSpring.isRunning()) {
                        RTMPStreamPipOverlay.this.pipYSpring.setStartValue(RTMPStreamPipOverlay.this.pipY).getSpring().setFinalPosition(MathUtils.clamp(RTMPStreamPipOverlay.this.pipY, AndroidUtilities.dp(16.0f), (AndroidUtilities.displaySize.y - RTMPStreamPipOverlay.this.pipHeight) - AndroidUtilities.dp(16.0f)));
                        RTMPStreamPipOverlay.this.pipYSpring.start();
                    }
                }
                return onTouchEvent || z;
            }

            @Override // android.view.View
            protected void onConfigurationChanged(Configuration configuration) {
                AndroidUtilities.checkDisplaySize(getContext(), configuration);
                RTMPStreamPipOverlay.this.bindTextureView();
            }

            @Override // android.view.View
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

            @Override // android.view.View
            protected void onSizeChanged(int i2, int i3, int i4, int i5) {
                super.onSizeChanged(i2, i3, i4, i5);
                this.path.rewind();
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set(0.0f, 0.0f, i2, i3);
                this.path.addRoundRect(rectF, AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), Path.Direction.CW);
            }
        };
        ViewGroup viewGroup = new ViewGroup(context) { // from class: org.telegram.ui.Components.voip.RTMPStreamPipOverlay.6
            @Override // android.view.ViewGroup, android.view.View
            protected void onLayout(boolean z, int i2, int i3, int i4, int i5) {
                RTMPStreamPipOverlay.this.contentFrameLayout.layout(0, 0, RTMPStreamPipOverlay.this.pipWidth, RTMPStreamPipOverlay.this.pipHeight);
            }

            @Override // android.view.View
            protected void onMeasure(int i2, int i3) {
                setMeasuredDimension(View.MeasureSpec.getSize(i2), View.MeasureSpec.getSize(i3));
                RTMPStreamPipOverlay.this.contentFrameLayout.measure(View.MeasureSpec.makeMeasureSpec(RTMPStreamPipOverlay.this.pipWidth, NUM), View.MeasureSpec.makeMeasureSpec(RTMPStreamPipOverlay.this.pipHeight, NUM));
            }
        };
        this.contentView = viewGroup;
        viewGroup.addView(this.contentFrameLayout, LayoutHelper.createFrame(-1, -1.0f));
        if (i >= 21) {
            this.contentFrameLayout.setOutlineProvider(new ViewOutlineProvider(this) { // from class: org.telegram.ui.Components.voip.RTMPStreamPipOverlay.7
                @Override // android.view.ViewOutlineProvider
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight(), AndroidUtilities.dp(10.0f));
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
        this.textureView.renderer.init(VideoCapturerDevice.getEglBase().mo2455getEglBaseContext(), new AnonymousClass8());
        this.contentFrameLayout.addView(this.textureView, LayoutHelper.createFrame(-1, -1.0f));
        View view = new View(context) { // from class: org.telegram.ui.Components.voip.RTMPStreamPipOverlay.9
            @Override // android.view.View
            protected void onDraw(Canvas canvas) {
                if (getAlpha() == 0.0f) {
                    return;
                }
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set(0.0f, 0.0f, getWidth(), getHeight());
                RTMPStreamPipOverlay.this.cellFlickerDrawable.draw(canvas, rectF, AndroidUtilities.dp(10.0f), null);
                invalidate();
            }

            @Override // android.view.View
            protected void onSizeChanged(int i2, int i3, int i4, int i5) {
                super.onSizeChanged(i2, i3, i4, i5);
                RTMPStreamPipOverlay.this.cellFlickerDrawable.setParentWidth(i2);
            }
        };
        this.flickerView = view;
        this.contentFrameLayout.addView(view, LayoutHelper.createFrame(-1, -1.0f));
        FrameLayout frameLayout = new FrameLayout(context);
        this.controlsView = frameLayout;
        frameLayout.setAlpha(0.0f);
        View view2 = new View(context);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColors(new int[]{NUM, 0});
        gradientDrawable.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM);
        view2.setBackground(gradientDrawable);
        this.controlsView.addView(view2, LayoutHelper.createFrame(-1, -1.0f));
        int dp = AndroidUtilities.dp(8.0f);
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(R.drawable.pip_video_close);
        imageView.setColorFilter(Theme.getColor("voipgroup_actionBarItems"));
        imageView.setBackground(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21")));
        imageView.setPadding(dp, dp, dp, dp);
        imageView.setOnClickListener(RTMPStreamPipOverlay$$ExternalSyntheticLambda2.INSTANCE);
        float f = 38;
        float f2 = 4;
        this.controlsView.addView(imageView, LayoutHelper.createFrame(38, f, 5, 0.0f, f2, f2, 0.0f));
        ImageView imageView2 = new ImageView(context);
        imageView2.setImageResource(R.drawable.pip_video_expand);
        imageView2.setColorFilter(Theme.getColor("voipgroup_actionBarItems"));
        imageView2.setBackground(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21")));
        imageView2.setPadding(dp, dp, dp, dp);
        imageView2.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.voip.RTMPStreamPipOverlay$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view3) {
                RTMPStreamPipOverlay.lambda$showInternal$8(context, view3);
            }
        });
        this.controlsView.addView(imageView2, LayoutHelper.createFrame(38, f, 5, 0.0f, f2, 48, 0.0f));
        this.contentFrameLayout.addView(this.controlsView, LayoutHelper.createFrame(-1, -1.0f));
        this.windowManager = (WindowManager) ApplicationLoader.applicationContext.getSystemService("window");
        WindowManager.LayoutParams createWindowLayoutParams = createWindowLayoutParams();
        this.windowLayoutParams = createWindowLayoutParams;
        int i2 = this.pipWidth;
        createWindowLayoutParams.width = i2;
        createWindowLayoutParams.height = this.pipHeight;
        float dp2 = (AndroidUtilities.displaySize.x - i2) - AndroidUtilities.dp(16.0f);
        this.pipX = dp2;
        createWindowLayoutParams.x = (int) dp2;
        WindowManager.LayoutParams layoutParams = this.windowLayoutParams;
        float dp3 = (AndroidUtilities.displaySize.y - this.pipHeight) - AndroidUtilities.dp(16.0f);
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
        animatorSet.setDuration(250L);
        animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
        animatorSet.playTogether(ObjectAnimator.ofFloat(this.contentView, View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.contentView, View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.contentView, View.SCALE_Y, 1.0f));
        animatorSet.start();
        bindTextureView();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.groupCallVisibilityChanged, new Object[0]);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.ui.Components.voip.RTMPStreamPipOverlay$3  reason: invalid class name */
    /* loaded from: classes3.dex */
    public class AnonymousClass3 implements ScaleGestureDetector.OnScaleGestureListener {
        AnonymousClass3() {
        }

        @Override // android.view.ScaleGestureDetector.OnScaleGestureListener
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            RTMPStreamPipOverlay rTMPStreamPipOverlay = RTMPStreamPipOverlay.this;
            rTMPStreamPipOverlay.scaleFactor = MathUtils.clamp(rTMPStreamPipOverlay.scaleFactor * scaleGestureDetector.getScaleFactor(), RTMPStreamPipOverlay.this.minScaleFactor, RTMPStreamPipOverlay.this.maxScaleFactor);
            RTMPStreamPipOverlay rTMPStreamPipOverlay2 = RTMPStreamPipOverlay.this;
            rTMPStreamPipOverlay2.pipWidth = (int) (rTMPStreamPipOverlay2.getSuggestedWidth() * RTMPStreamPipOverlay.this.scaleFactor);
            RTMPStreamPipOverlay rTMPStreamPipOverlay3 = RTMPStreamPipOverlay.this;
            rTMPStreamPipOverlay3.pipHeight = (int) (rTMPStreamPipOverlay3.getSuggestedHeight() * RTMPStreamPipOverlay.this.scaleFactor);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.voip.RTMPStreamPipOverlay$3$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    RTMPStreamPipOverlay.AnonymousClass3.this.lambda$onScale$0();
                }
            });
            SpringForce spring = RTMPStreamPipOverlay.this.pipXSpring.setStartValue(RTMPStreamPipOverlay.this.pipX).getSpring();
            float focusX = scaleGestureDetector.getFocusX();
            int i = AndroidUtilities.displaySize.x;
            spring.setFinalPosition(focusX >= ((float) i) / 2.0f ? (i - RTMPStreamPipOverlay.this.pipWidth) - AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(16.0f));
            if (!RTMPStreamPipOverlay.this.pipXSpring.isRunning()) {
                RTMPStreamPipOverlay.this.pipXSpring.start();
            }
            RTMPStreamPipOverlay.this.pipYSpring.setStartValue(RTMPStreamPipOverlay.this.pipY).getSpring().setFinalPosition(MathUtils.clamp(scaleGestureDetector.getFocusY() - (RTMPStreamPipOverlay.this.pipHeight / 2.0f), AndroidUtilities.dp(16.0f), (AndroidUtilities.displaySize.y - RTMPStreamPipOverlay.this.pipHeight) - AndroidUtilities.dp(16.0f)));
            if (!RTMPStreamPipOverlay.this.pipYSpring.isRunning()) {
                RTMPStreamPipOverlay.this.pipYSpring.start();
                return true;
            }
            return true;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onScale$0() {
            RTMPStreamPipOverlay.this.contentFrameLayout.invalidate();
            if (Build.VERSION.SDK_INT < 18 || !RTMPStreamPipOverlay.this.contentFrameLayout.isInLayout()) {
                RTMPStreamPipOverlay.this.contentFrameLayout.requestLayout();
                RTMPStreamPipOverlay.this.contentView.requestLayout();
                RTMPStreamPipOverlay.this.textureView.requestLayout();
            }
        }

        @Override // android.view.ScaleGestureDetector.OnScaleGestureListener
        public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
            if (RTMPStreamPipOverlay.this.isScrolling) {
                RTMPStreamPipOverlay.this.isScrolling = false;
            }
            RTMPStreamPipOverlay.this.isScrollDisallowed = true;
            RTMPStreamPipOverlay.this.windowLayoutParams.width = (int) (RTMPStreamPipOverlay.this.getSuggestedWidth() * RTMPStreamPipOverlay.this.maxScaleFactor);
            RTMPStreamPipOverlay.this.windowLayoutParams.height = (int) (RTMPStreamPipOverlay.this.getSuggestedHeight() * RTMPStreamPipOverlay.this.maxScaleFactor);
            RTMPStreamPipOverlay.this.windowManager.updateViewLayout(RTMPStreamPipOverlay.this.contentView, RTMPStreamPipOverlay.this.windowLayoutParams);
            return true;
        }

        @Override // android.view.ScaleGestureDetector.OnScaleGestureListener
        public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
            if (RTMPStreamPipOverlay.this.pipXSpring.isRunning() || RTMPStreamPipOverlay.this.pipYSpring.isRunning()) {
                final ArrayList arrayList = new ArrayList();
                DynamicAnimation.OnAnimationEndListener onAnimationEndListener = new DynamicAnimation.OnAnimationEndListener() { // from class: org.telegram.ui.Components.voip.RTMPStreamPipOverlay.3.1
                    @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
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
                    RTMPStreamPipOverlay.this.pipXSpring.addEndListener(onAnimationEndListener);
                }
                if (!RTMPStreamPipOverlay.this.pipYSpring.isRunning()) {
                    arrayList.add(RTMPStreamPipOverlay.this.pipYSpring);
                    return;
                } else {
                    RTMPStreamPipOverlay.this.pipYSpring.addEndListener(onAnimationEndListener);
                    return;
                }
            }
            updateLayout();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void updateLayout() {
            RTMPStreamPipOverlay rTMPStreamPipOverlay = RTMPStreamPipOverlay.this;
            WindowManager.LayoutParams layoutParams = rTMPStreamPipOverlay.windowLayoutParams;
            int suggestedWidth = (int) (RTMPStreamPipOverlay.this.getSuggestedWidth() * RTMPStreamPipOverlay.this.scaleFactor);
            layoutParams.width = suggestedWidth;
            rTMPStreamPipOverlay.pipWidth = suggestedWidth;
            RTMPStreamPipOverlay rTMPStreamPipOverlay2 = RTMPStreamPipOverlay.this;
            WindowManager.LayoutParams layoutParams2 = rTMPStreamPipOverlay2.windowLayoutParams;
            int suggestedHeight = (int) (RTMPStreamPipOverlay.this.getSuggestedHeight() * RTMPStreamPipOverlay.this.scaleFactor);
            layoutParams2.height = suggestedHeight;
            rTMPStreamPipOverlay2.pipHeight = suggestedHeight;
            RTMPStreamPipOverlay.this.windowManager.updateViewLayout(RTMPStreamPipOverlay.this.contentView, RTMPStreamPipOverlay.this.windowLayoutParams);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.ui.Components.voip.RTMPStreamPipOverlay$8  reason: invalid class name */
    /* loaded from: classes3.dex */
    public class AnonymousClass8 implements RendererCommon.RendererEvents {
        AnonymousClass8() {
        }

        @Override // org.webrtc.RendererCommon.RendererEvents
        public void onFirstFrameRendered() {
            RTMPStreamPipOverlay.this.firstFrameRendered = true;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.voip.RTMPStreamPipOverlay$8$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    RTMPStreamPipOverlay.AnonymousClass8.this.lambda$onFirstFrameRendered$0();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onFirstFrameRendered$0() {
            RTMPStreamPipOverlay.this.bindTextureView();
        }

        @Override // org.webrtc.RendererCommon.RendererEvents
        public void onFrameResolutionChanged(int i, int i2, int i3) {
            if ((i3 / 90) % 2 == 0) {
                RTMPStreamPipOverlay.this.aspectRatio = Float.valueOf(i2 / i);
            } else {
                RTMPStreamPipOverlay.this.aspectRatio = Float.valueOf(i / i2);
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.voip.RTMPStreamPipOverlay$8$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    RTMPStreamPipOverlay.AnonymousClass8.this.lambda$onFrameResolutionChanged$1();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onFrameResolutionChanged$1() {
            RTMPStreamPipOverlay.this.bindTextureView();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
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

    /* JADX INFO: Access modifiers changed from: private */
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
                    VoIPService.getSharedInstance().setSinks(this.textureView.renderer, this.boundPresentation, null);
                } else {
                    VoIPService.getSharedInstance().addRemoteSink(tLRPC$TL_groupCallParticipant2, this.boundPresentation, this.textureView.renderer, null);
                }
                MessagesController messagesController = VoIPService.getSharedInstance().groupCall.currentAccount.getMessagesController();
                long peerId = MessageObject.getPeerId(tLRPC$TL_groupCallParticipant2.peer);
                if (peerId > 0) {
                    TLRPC$User user = messagesController.getUser(Long.valueOf(peerId));
                    ImageLocation forUser = ImageLocation.getForUser(user, 1);
                    int colorForId = user != null ? AvatarDrawable.getColorForId(user.id) : ColorUtils.blendARGB(-16777216, -1, 0.2f);
                    this.avatarImageView.getImageReceiver().setImage(forUser, "50_50_b", new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{ColorUtils.blendARGB(colorForId, -16777216, 0.2f), ColorUtils.blendARGB(colorForId, -16777216, 0.4f)}), null, user, 0);
                } else {
                    TLRPC$Chat chat = messagesController.getChat(Long.valueOf(-peerId));
                    ImageLocation forChat = ImageLocation.getForChat(chat, 1);
                    int colorForId2 = chat != null ? AvatarDrawable.getColorForId(chat.id) : ColorUtils.blendARGB(-16777216, -1, 0.2f);
                    this.avatarImageView.getImageReceiver().setImage(forChat, "50_50_b", new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{ColorUtils.blendARGB(colorForId2, -16777216, 0.2f), ColorUtils.blendARGB(colorForId2, -16777216, 0.4f)}), null, chat, 0);
                }
                this.boundParticipant = tLRPC$TL_groupCallParticipant2;
            }
        } else if (this.boundParticipant != null) {
            VoIPService.getSharedInstance().removeRemoteSink(this.boundParticipant, false);
            this.boundParticipant = null;
        }
        if (this.firstFrameRendered && (tLRPC$TL_groupCallParticipant = this.boundParticipant) != null && (((tLRPC$TL_groupCallParticipantVideo = tLRPC$TL_groupCallParticipant.video) != null || tLRPC$TL_groupCallParticipant.presentation != null) && ((tLRPC$TL_groupCallParticipantVideo == null || !tLRPC$TL_groupCallParticipantVideo.paused) && ((tLRPC$TL_groupCallParticipantVideo2 = tLRPC$TL_groupCallParticipant.presentation) == null || !tLRPC$TL_groupCallParticipantVideo2.paused)))) {
            z = false;
        }
        if (this.placeholderShown != z) {
            this.flickerView.animate().cancel();
            float f = 1.0f;
            ViewPropertyAnimator duration = this.flickerView.animate().alpha(z ? 1.0f : 0.0f).setDuration(150L);
            CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.DEFAULT;
            duration.setInterpolator(cubicBezierInterpolator).start();
            this.avatarImageView.animate().cancel();
            this.avatarImageView.animate().alpha(z ? 1.0f : 0.0f).setDuration(150L).setInterpolator(cubicBezierInterpolator).start();
            this.textureView.animate().cancel();
            ViewPropertyAnimator animate = this.textureView.animate();
            if (z) {
                f = 0.0f;
            }
            animate.alpha(f).setDuration(150L).setInterpolator(cubicBezierInterpolator).start();
            this.placeholderShown = z;
        }
        if (this.pipWidth == getSuggestedWidth() * this.scaleFactor && this.pipHeight == getSuggestedHeight() * this.scaleFactor) {
            return;
        }
        WindowManager.LayoutParams layoutParams = this.windowLayoutParams;
        int suggestedWidth = (int) (getSuggestedWidth() * this.scaleFactor);
        this.pipWidth = suggestedWidth;
        layoutParams.width = suggestedWidth;
        WindowManager.LayoutParams layoutParams2 = this.windowLayoutParams;
        int suggestedHeight = (int) (getSuggestedHeight() * this.scaleFactor);
        this.pipHeight = suggestedHeight;
        layoutParams2.height = suggestedHeight;
        this.windowManager.updateViewLayout(this.contentView, this.windowLayoutParams);
        SpringForce spring = this.pipXSpring.setStartValue(this.pipX).getSpring();
        float suggestedWidth2 = this.pipX + ((getSuggestedWidth() * this.scaleFactor) / 2.0f);
        int i = AndroidUtilities.displaySize.x;
        spring.setFinalPosition(suggestedWidth2 >= ((float) i) / 2.0f ? (i - (getSuggestedWidth() * this.scaleFactor)) - AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(16.0f));
        this.pipXSpring.start();
        this.pipYSpring.setStartValue(this.pipY).getSpring().setFinalPosition(MathUtils.clamp(this.pipY, AndroidUtilities.dp(16.0f), (AndroidUtilities.displaySize.y - (getSuggestedHeight() * this.scaleFactor)) - AndroidUtilities.dp(16.0f)));
        this.pipYSpring.start();
    }

    @SuppressLint({"WrongConstant"})
    private WindowManager.LayoutParams createWindowLayoutParams() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.gravity = 51;
        layoutParams.format = -3;
        if (AndroidUtilities.checkInlinePermissions(ApplicationLoader.applicationContext)) {
            if (Build.VERSION.SDK_INT >= 26) {
                layoutParams.type = 2038;
            } else {
                layoutParams.type = 2003;
            }
        } else {
            layoutParams.type = 2999;
        }
        layoutParams.flags = 520;
        return layoutParams;
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.didEndCall) {
            dismiss();
        } else if (i != NotificationCenter.groupCallUpdated) {
        } else {
            bindTextureView();
        }
    }
}
