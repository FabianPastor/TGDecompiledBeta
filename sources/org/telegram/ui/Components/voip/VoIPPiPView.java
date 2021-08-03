package org.telegram.ui.Components.voip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewOutlineProvider;
import android.view.ViewPropertyAnimator;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.voip.VideoCapturerDevice;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.voip.VoIPPiPView;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.VoIPFragment;
import org.webrtc.EglBase;
import org.webrtc.RendererCommon;

public class VoIPPiPView implements VoIPService.StateListener, NotificationCenter.NotificationCenterDelegate {
    public static int bottomInset = 0;
    /* access modifiers changed from: private */
    public static VoIPPiPView expandedInstance = null;
    /* access modifiers changed from: private */
    public static VoIPPiPView instance = null;
    public static boolean switchingToPip = false;
    public static int topInset;
    int animationIndex = -1;
    ValueAnimator animatorToCameraMini;
    ValueAnimator.AnimatorUpdateListener animatorToCameraMiniUpdater = new ValueAnimator.AnimatorUpdateListener() {
        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
            VoIPPiPView.this.lambda$new$0$VoIPPiPView(valueAnimator);
        }
    };
    boolean callingUserIsVideo;
    public final VoIPTextureView callingUserTextureView;
    ImageView closeIcon;
    Runnable collapseRunnable = new Runnable() {
        public void run() {
            if (VoIPPiPView.instance != null) {
                VoIPPiPView.instance.floatingView.expand(false);
            }
        }
    };
    /* access modifiers changed from: private */
    public int currentAccount;
    boolean currentUserIsVideo;
    public final VoIPTextureView currentUserTextureView;
    ImageView enlargeIcon;
    ValueAnimator expandAnimator;
    public boolean expanded;
    /* access modifiers changed from: private */
    public boolean expandedAnimationInProgress;
    FloatingView floatingView;
    AnimatorSet moveToBoundsAnimator;
    boolean moving;
    public final int parentHeight;
    public final int parentWidth;
    float[] point = new float[2];
    float progressToCameraMini;
    long startTime;
    float startX;
    float startY;
    View topShadow;
    /* access modifiers changed from: private */
    public ValueAnimator.AnimatorUpdateListener updateXlistener = new ValueAnimator.AnimatorUpdateListener() {
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            VoIPPiPView voIPPiPView = VoIPPiPView.this;
            voIPPiPView.windowLayoutParams.x = (int) floatValue;
            if (voIPPiPView.windowView.getParent() != null) {
                WindowManager access$200 = VoIPPiPView.this.windowManager;
                VoIPPiPView voIPPiPView2 = VoIPPiPView.this;
                access$200.updateViewLayout(voIPPiPView2.windowView, voIPPiPView2.windowLayoutParams);
            }
        }
    };
    /* access modifiers changed from: private */
    public ValueAnimator.AnimatorUpdateListener updateYlistener = new ValueAnimator.AnimatorUpdateListener() {
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            VoIPPiPView voIPPiPView = VoIPPiPView.this;
            voIPPiPView.windowLayoutParams.y = (int) floatValue;
            if (voIPPiPView.windowView.getParent() != null) {
                WindowManager access$200 = VoIPPiPView.this.windowManager;
                VoIPPiPView voIPPiPView2 = VoIPPiPView.this;
                access$200.updateViewLayout(voIPPiPView2.windowView, voIPPiPView2.windowLayoutParams);
            }
        }
    };
    public WindowManager.LayoutParams windowLayoutParams;
    /* access modifiers changed from: private */
    public WindowManager windowManager;
    public FrameLayout windowView;
    public int xOffset;
    public int yOffset;

    public void onAudioSettingsChanged() {
    }

    public /* synthetic */ void onCameraFirstFrameAvailable() {
        VoIPService.StateListener.CC.$default$onCameraFirstFrameAvailable(this);
    }

    public void onSignalBarsCountChanged(int i) {
    }

    public void onVideoAvailableChange(boolean z) {
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$VoIPPiPView(ValueAnimator valueAnimator) {
        this.progressToCameraMini = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.floatingView.invalidate();
    }

    public static void show(Activity activity, int i, int i2, int i3, int i4) {
        WindowManager windowManager2;
        if (instance == null && VideoCapturerDevice.eglBase != null) {
            WindowManager.LayoutParams createWindowLayoutParams = createWindowLayoutParams(activity, i2, i3, 0.25f);
            instance = new VoIPPiPView(activity, i2, i3, false);
            if (AndroidUtilities.checkInlinePermissions(activity)) {
                windowManager2 = (WindowManager) ApplicationLoader.applicationContext.getSystemService("window");
            } else {
                windowManager2 = (WindowManager) activity.getSystemService("window");
            }
            VoIPPiPView voIPPiPView = instance;
            voIPPiPView.currentAccount = i;
            voIPPiPView.windowManager = windowManager2;
            voIPPiPView.windowLayoutParams = createWindowLayoutParams;
            SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("voippipconfig", 0);
            instance.setRelativePosition(sharedPreferences.getFloat("relativeX", 1.0f), sharedPreferences.getFloat("relativeY", 0.0f));
            NotificationCenter.getGlobalInstance().addObserver(instance, NotificationCenter.didEndCall);
            windowManager2.addView(instance.windowView, createWindowLayoutParams);
            instance.currentUserTextureView.renderer.init(VideoCapturerDevice.eglBase.getEglBaseContext(), (RendererCommon.RendererEvents) null);
            instance.callingUserTextureView.renderer.init(VideoCapturerDevice.eglBase.getEglBaseContext(), (RendererCommon.RendererEvents) null);
            if (i4 == 0) {
                instance.windowView.setScaleX(0.5f);
                instance.windowView.setScaleY(0.5f);
                instance.windowView.setAlpha(0.0f);
                instance.windowView.animate().alpha(1.0f).scaleY(1.0f).scaleX(1.0f).start();
                if (VoIPService.getSharedInstance() != null) {
                    VoIPService sharedInstance = VoIPService.getSharedInstance();
                    VoIPPiPView voIPPiPView2 = instance;
                    sharedInstance.setSinks(voIPPiPView2.currentUserTextureView.renderer, voIPPiPView2.callingUserTextureView.renderer);
                }
            } else if (i4 == 1) {
                instance.windowView.setAlpha(0.0f);
                if (VoIPService.getSharedInstance() != null) {
                    VoIPService sharedInstance2 = VoIPService.getSharedInstance();
                    VoIPPiPView voIPPiPView3 = instance;
                    sharedInstance2.setBackgroundSinks(voIPPiPView3.currentUserTextureView.renderer, voIPPiPView3.callingUserTextureView.renderer);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public static WindowManager.LayoutParams createWindowLayoutParams(Context context, int i, int i2, float f) {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        float f2 = (float) i2;
        float f3 = f2 * 0.4f;
        float f4 = (float) i;
        float f5 = 0.4f * f4;
        layoutParams.height = (int) ((f2 * f) + ((float) ((((int) ((f3 * 1.05f) - f3)) / 2) * 2)));
        layoutParams.width = (int) ((f4 * f) + ((float) ((((int) ((1.05f * f5) - f5)) / 2) * 2)));
        layoutParams.gravity = 51;
        layoutParams.format = -3;
        if (!AndroidUtilities.checkInlinePermissions(context)) {
            layoutParams.type = 99;
        } else if (Build.VERSION.SDK_INT >= 26) {
            layoutParams.type = 2038;
        } else {
            layoutParams.type = 2003;
        }
        if (Build.VERSION.SDK_INT >= 21) {
            layoutParams.flags |= Integer.MIN_VALUE;
        }
        layoutParams.flags = 16778120;
        return layoutParams;
    }

    public static void prepareForTransition() {
        if (expandedInstance != null) {
            instance.expandAnimator.cancel();
        }
    }

    public static void finish() {
        if (!switchingToPip) {
            VoIPPiPView voIPPiPView = expandedInstance;
            if (voIPPiPView != null) {
                voIPPiPView.finishInternal();
            }
            VoIPPiPView voIPPiPView2 = instance;
            if (voIPPiPView2 != null) {
                voIPPiPView2.finishInternal();
            }
            expandedInstance = null;
            instance = null;
        }
    }

    public static boolean isExpanding() {
        return instance.expanded;
    }

    private void setRelativePosition(float f, float f2) {
        Point point2 = AndroidUtilities.displaySize;
        float f3 = (float) point2.x;
        float f4 = (float) point2.y;
        float dp = (float) AndroidUtilities.dp(16.0f);
        float dp2 = (float) AndroidUtilities.dp(16.0f);
        float dp3 = (float) AndroidUtilities.dp(60.0f);
        float dp4 = (float) AndroidUtilities.dp(16.0f);
        float f5 = ((float) this.parentWidth) * 0.25f;
        float f6 = ((float) this.parentHeight) * 0.25f;
        if (this.floatingView.getMeasuredWidth() != 0) {
            f5 = (float) this.floatingView.getMeasuredWidth();
        }
        if (this.floatingView.getMeasuredWidth() != 0) {
            f6 = (float) this.floatingView.getMeasuredHeight();
        }
        WindowManager.LayoutParams layoutParams = this.windowLayoutParams;
        layoutParams.x = (int) ((f * (((f3 - dp) - dp2) - f5)) - (((float) this.xOffset) - dp));
        layoutParams.y = (int) ((f2 * (((f4 - dp3) - dp4) - f6)) - (((float) this.yOffset) - dp3));
        if (this.windowView.getParent() != null) {
            this.windowManager.updateViewLayout(this.windowView, this.windowLayoutParams);
        }
    }

    public static VoIPPiPView getInstance() {
        VoIPPiPView voIPPiPView = expandedInstance;
        if (voIPPiPView != null) {
            return voIPPiPView;
        }
        return instance;
    }

    public VoIPPiPView(Context context, int i, int i2, boolean z) {
        this.parentWidth = i;
        this.parentHeight = i2;
        float f = ((float) i2) * 0.4f;
        this.yOffset = ((int) ((f * 1.05f) - f)) / 2;
        float f2 = ((float) i) * 0.4f;
        this.xOffset = ((int) ((1.05f * f2) - f2)) / 2;
        final Drawable drawable = ContextCompat.getDrawable(context, NUM);
        AnonymousClass4 r10 = new FrameLayout(context) {
            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                canvas.save();
                canvas.scale(VoIPPiPView.this.floatingView.getScaleX(), VoIPPiPView.this.floatingView.getScaleY(), ((float) VoIPPiPView.this.floatingView.getLeft()) + VoIPPiPView.this.floatingView.getPivotX(), ((float) VoIPPiPView.this.floatingView.getTop()) + VoIPPiPView.this.floatingView.getPivotY());
                drawable.setBounds(VoIPPiPView.this.floatingView.getLeft() - AndroidUtilities.dp(2.0f), VoIPPiPView.this.floatingView.getTop() - AndroidUtilities.dp(2.0f), VoIPPiPView.this.floatingView.getRight() + AndroidUtilities.dp(2.0f), VoIPPiPView.this.floatingView.getBottom() + AndroidUtilities.dp(2.0f));
                drawable.draw(canvas);
                canvas.restore();
                super.onDraw(canvas);
            }
        };
        this.windowView = r10;
        r10.setWillNotDraw(false);
        FrameLayout frameLayout = this.windowView;
        int i3 = this.xOffset;
        int i4 = this.yOffset;
        frameLayout.setPadding(i3, i4, i3, i4);
        this.floatingView = new FloatingView(context);
        VoIPTextureView voIPTextureView = new VoIPTextureView(context, false, true);
        this.callingUserTextureView = voIPTextureView;
        voIPTextureView.scaleType = VoIPTextureView.SCALE_TYPE_NONE;
        VoIPTextureView voIPTextureView2 = new VoIPTextureView(context, false, true);
        this.currentUserTextureView = voIPTextureView2;
        voIPTextureView2.renderer.setMirror(true);
        this.floatingView.addView(voIPTextureView);
        this.floatingView.addView(voIPTextureView2);
        this.floatingView.setBackgroundColor(-7829368);
        this.windowView.addView(this.floatingView);
        this.windowView.setClipChildren(false);
        this.windowView.setClipToPadding(false);
        if (z) {
            View view = new View(context);
            this.topShadow = view;
            view.setBackground(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{ColorUtils.setAlphaComponent(-16777216, 76), 0}));
            this.floatingView.addView(this.topShadow, -1, AndroidUtilities.dp(60.0f));
            ImageView imageView = new ImageView(context);
            this.closeIcon = imageView;
            imageView.setImageResource(NUM);
            this.closeIcon.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
            this.closeIcon.setContentDescription(LocaleController.getString("Close", NUM));
            this.floatingView.addView(this.closeIcon, LayoutHelper.createFrame(40, 40.0f, 53, 4.0f, 4.0f, 4.0f, 0.0f));
            ImageView imageView2 = new ImageView(context);
            this.enlargeIcon = imageView2;
            imageView2.setImageResource(NUM);
            this.enlargeIcon.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
            this.enlargeIcon.setContentDescription(LocaleController.getString("Open", NUM));
            this.floatingView.addView(this.enlargeIcon, LayoutHelper.createFrame(40, 40.0f, 51, 4.0f, 4.0f, 4.0f, 0.0f));
            this.closeIcon.setOnClickListener($$Lambda$VoIPPiPView$MEYxxqPwmNN1zowModgP4kq3s9A.INSTANCE);
            this.enlargeIcon.setOnClickListener(new View.OnClickListener(context) {
                public final /* synthetic */ Context f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(View view) {
                    VoIPPiPView.this.lambda$new$2$VoIPPiPView(this.f$1, view);
                }
            });
        }
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance != null) {
            sharedInstance.registerStateListener(this);
        }
        updateViewState();
    }

    static /* synthetic */ void lambda$new$1(View view) {
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance != null) {
            sharedInstance.hangUp();
        } else {
            finish();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$2 */
    public /* synthetic */ void lambda$new$2$VoIPPiPView(Context context, View view) {
        boolean z = context instanceof LaunchActivity;
        if (z && !ApplicationLoader.mainInterfacePaused) {
            VoIPFragment.show((Activity) context, this.currentAccount);
        } else if (z) {
            Intent intent = new Intent(context, LaunchActivity.class);
            intent.setAction("voip");
            context.startActivity(intent);
        }
    }

    /* access modifiers changed from: private */
    public void finishInternal() {
        this.currentUserTextureView.renderer.release();
        this.callingUserTextureView.renderer.release();
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance != null) {
            sharedInstance.unregisterStateListener(this);
        }
        this.windowView.setVisibility(8);
        if (this.windowView.getParent() != null) {
            this.floatingView.getRelativePosition(this.point);
            float min = Math.min(1.0f, Math.max(0.0f, this.point[0]));
            ApplicationLoader.applicationContext.getSharedPreferences("voippipconfig", 0).edit().putFloat("relativeX", min).putFloat("relativeY", Math.min(1.0f, Math.max(0.0f, this.point[1]))).apply();
            try {
                this.windowManager.removeView(this.windowView);
            } catch (Throwable th) {
                FileLog.e(th);
            }
        }
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didEndCall);
    }

    public void onStateChanged(int i) {
        if (i == 11 || i == 17 || i == 4 || i == 10) {
            AndroidUtilities.runOnUIThread($$Lambda$NKsCQZEaYu8U2hXDux7RXQqeP2M.INSTANCE, 200);
        }
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance == null) {
            finish();
        } else if (i != 3 || sharedInstance.isVideoAvailable()) {
            updateViewState();
        } else {
            finish();
        }
    }

    public void onMediaStateUpdated(int i, int i2) {
        updateViewState();
    }

    public void onCameraSwitch(boolean z) {
        updateViewState();
    }

    public void onScreenOnChange(boolean z) {
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance != null) {
            if (!z && this.currentUserIsVideo) {
                sharedInstance.setVideoState(false, 1);
            } else if (z && sharedInstance.getVideoState(false) == 1) {
                sharedInstance.setVideoState(false, 2);
            }
        }
    }

    private void updateViewState() {
        boolean z = this.floatingView.getMeasuredWidth() != 0;
        boolean z2 = this.callingUserIsVideo;
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        float f = 1.0f;
        if (sharedInstance != null) {
            this.callingUserIsVideo = sharedInstance.getRemoteVideoState() == 2;
            this.currentUserIsVideo = sharedInstance.getVideoState(false) == 2 || sharedInstance.getVideoState(false) == 1;
            this.currentUserTextureView.renderer.setMirror(sharedInstance.isFrontFaceCamera());
            this.currentUserTextureView.setIsScreencast(sharedInstance.isScreencast());
            this.currentUserTextureView.setScreenshareMiniProgress(1.0f, false);
        }
        if (!z) {
            if (!this.callingUserIsVideo) {
                f = 0.0f;
            }
            this.progressToCameraMini = f;
        } else if (z2 != this.callingUserIsVideo) {
            ValueAnimator valueAnimator = this.animatorToCameraMini;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            float[] fArr = new float[2];
            fArr[0] = this.progressToCameraMini;
            if (!this.callingUserIsVideo) {
                f = 0.0f;
            }
            fArr[1] = f;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
            this.animatorToCameraMini = ofFloat;
            ofFloat.addUpdateListener(this.animatorToCameraMiniUpdater);
            this.animatorToCameraMini.setDuration(300).setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.animatorToCameraMini.start();
        }
    }

    public void onTransitionEnd() {
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().swapSinks();
        }
    }

    public void onPause() {
        if (this.windowLayoutParams.type == 99) {
            VoIPService sharedInstance = VoIPService.getSharedInstance();
            if (this.currentUserIsVideo) {
                sharedInstance.setVideoState(false, 1);
            }
        }
    }

    public void onResume() {
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance != null && sharedInstance.getVideoState(false) == 1) {
            sharedInstance.setVideoState(false, 2);
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.didEndCall) {
            finish();
        }
    }

    private class FloatingView extends FrameLayout {
        float bottomPadding;
        float leftPadding;
        float rightPadding;
        float topPadding;
        float touchSlop;

        public FloatingView(Context context) {
            super(context);
            this.touchSlop = (float) ViewConfiguration.get(context).getScaledTouchSlop();
            if (Build.VERSION.SDK_INT >= 21) {
                setOutlineProvider(new ViewOutlineProvider(VoIPPiPView.this) {
                    @TargetApi(21)
                    public void getOutline(View view, Outline outline) {
                        outline.setRoundRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight(), (1.0f / view.getScaleX()) * ((float) AndroidUtilities.dp(4.0f)));
                    }
                });
                setClipToOutline(true);
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, i2);
            this.leftPadding = (float) AndroidUtilities.dp(16.0f);
            this.rightPadding = (float) AndroidUtilities.dp(16.0f);
            this.topPadding = (float) AndroidUtilities.dp(60.0f);
            this.bottomPadding = (float) AndroidUtilities.dp(16.0f);
        }

        /* access modifiers changed from: protected */
        public void dispatchDraw(Canvas canvas) {
            VoIPPiPView voIPPiPView = VoIPPiPView.this;
            voIPPiPView.currentUserTextureView.setPivotX((float) voIPPiPView.callingUserTextureView.getMeasuredWidth());
            VoIPPiPView voIPPiPView2 = VoIPPiPView.this;
            voIPPiPView2.currentUserTextureView.setPivotY((float) voIPPiPView2.callingUserTextureView.getMeasuredHeight());
            VoIPPiPView.this.currentUserTextureView.setTranslationX(((float) (-AndroidUtilities.dp(4.0f))) * (1.0f / getScaleX()) * VoIPPiPView.this.progressToCameraMini);
            VoIPPiPView.this.currentUserTextureView.setTranslationY(((float) (-AndroidUtilities.dp(4.0f))) * (1.0f / getScaleY()) * VoIPPiPView.this.progressToCameraMini);
            VoIPPiPView.this.currentUserTextureView.setRoundCorners(((float) AndroidUtilities.dp(8.0f)) * (1.0f / getScaleY()) * VoIPPiPView.this.progressToCameraMini);
            VoIPPiPView voIPPiPView3 = VoIPPiPView.this;
            voIPPiPView3.currentUserTextureView.setScaleX(((1.0f - voIPPiPView3.progressToCameraMini) * 0.6f) + 0.4f);
            VoIPPiPView voIPPiPView4 = VoIPPiPView.this;
            voIPPiPView4.currentUserTextureView.setScaleY(((1.0f - voIPPiPView4.progressToCameraMini) * 0.6f) + 0.4f);
            VoIPPiPView voIPPiPView5 = VoIPPiPView.this;
            voIPPiPView5.currentUserTextureView.setAlpha(Math.min(1.0f, 1.0f - voIPPiPView5.progressToCameraMini));
            super.dispatchDraw(canvas);
        }

        /* JADX WARNING: Code restructure failed: missing block: B:12:0x0035, code lost:
            if (r4 != 3) goto L_0x0245;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTouchEvent(android.view.MotionEvent r15) {
            /*
                r14 = this;
                org.telegram.ui.Components.voip.VoIPPiPView r0 = org.telegram.ui.Components.voip.VoIPPiPView.this
                boolean r0 = r0.expandedAnimationInProgress
                r1 = 0
                if (r0 != 0) goto L_0x0246
                boolean r0 = org.telegram.ui.Components.voip.VoIPPiPView.switchingToPip
                if (r0 != 0) goto L_0x0246
                org.telegram.ui.Components.voip.VoIPPiPView r0 = org.telegram.ui.Components.voip.VoIPPiPView.instance
                if (r0 != 0) goto L_0x0015
                goto L_0x0246
            L_0x0015:
                org.telegram.ui.Components.voip.VoIPPiPView r0 = org.telegram.ui.Components.voip.VoIPPiPView.this
                java.lang.Runnable r0 = r0.collapseRunnable
                org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r0)
                float r0 = r15.getRawX()
                float r2 = r15.getRawY()
                android.view.ViewParent r3 = r14.getParent()
                int r4 = r15.getAction()
                r5 = 1
                if (r4 == 0) goto L_0x0230
                r6 = 2
                if (r4 == r5) goto L_0x0097
                if (r4 == r6) goto L_0x0039
                r0 = 3
                if (r4 == r0) goto L_0x0097
                goto L_0x0245
            L_0x0039:
                org.telegram.ui.Components.voip.VoIPPiPView r15 = org.telegram.ui.Components.voip.VoIPPiPView.this
                float r1 = r15.startX
                float r1 = r0 - r1
                float r4 = r15.startY
                float r4 = r2 - r4
                boolean r15 = r15.moving
                r6 = 0
                if (r15 != 0) goto L_0x0064
                float r15 = r1 * r1
                float r7 = r4 * r4
                float r15 = r15 + r7
                float r7 = r14.touchSlop
                float r7 = r7 * r7
                int r15 = (r15 > r7 ? 1 : (r15 == r7 ? 0 : -1))
                if (r15 <= 0) goto L_0x0064
                if (r3 == 0) goto L_0x005a
                r3.requestDisallowInterceptTouchEvent(r5)
            L_0x005a:
                org.telegram.ui.Components.voip.VoIPPiPView r15 = org.telegram.ui.Components.voip.VoIPPiPView.this
                r15.moving = r5
                r15.startX = r0
                r15.startY = r2
                r1 = 0
                r4 = 0
            L_0x0064:
                org.telegram.ui.Components.voip.VoIPPiPView r15 = org.telegram.ui.Components.voip.VoIPPiPView.this
                boolean r3 = r15.moving
                if (r3 == 0) goto L_0x0245
                android.view.WindowManager$LayoutParams r3 = r15.windowLayoutParams
                int r6 = r3.x
                float r6 = (float) r6
                float r6 = r6 + r1
                int r1 = (int) r6
                r3.x = r1
                int r1 = r3.y
                float r1 = (float) r1
                float r1 = r1 + r4
                int r1 = (int) r1
                r3.y = r1
                r15.startX = r0
                r15.startY = r2
                android.widget.FrameLayout r15 = r15.windowView
                android.view.ViewParent r15 = r15.getParent()
                if (r15 == 0) goto L_0x0245
                org.telegram.ui.Components.voip.VoIPPiPView r15 = org.telegram.ui.Components.voip.VoIPPiPView.this
                android.view.WindowManager r15 = r15.windowManager
                org.telegram.ui.Components.voip.VoIPPiPView r0 = org.telegram.ui.Components.voip.VoIPPiPView.this
                android.widget.FrameLayout r1 = r0.windowView
                android.view.WindowManager$LayoutParams r0 = r0.windowLayoutParams
                r15.updateViewLayout(r1, r0)
                goto L_0x0245
            L_0x0097:
                org.telegram.ui.Components.voip.VoIPPiPView r0 = org.telegram.ui.Components.voip.VoIPPiPView.this
                android.animation.AnimatorSet r0 = r0.moveToBoundsAnimator
                if (r0 == 0) goto L_0x00a0
                r0.cancel()
            L_0x00a0:
                int r15 = r15.getAction()
                r7 = 150(0x96, double:7.4E-322)
                if (r15 != r5) goto L_0x00e9
                org.telegram.ui.Components.voip.VoIPPiPView r15 = org.telegram.ui.Components.voip.VoIPPiPView.this
                boolean r15 = r15.moving
                if (r15 != 0) goto L_0x00e9
                long r9 = java.lang.System.currentTimeMillis()
                org.telegram.ui.Components.voip.VoIPPiPView r15 = org.telegram.ui.Components.voip.VoIPPiPView.this
                long r11 = r15.startTime
                long r9 = r9 - r11
                int r15 = (r9 > r7 ? 1 : (r9 == r7 ? 0 : -1))
                if (r15 >= 0) goto L_0x00e9
                android.content.Context r15 = r14.getContext()
                boolean r0 = r15 instanceof org.telegram.ui.LaunchActivity
                if (r0 == 0) goto L_0x00d3
                boolean r2 = org.telegram.messenger.ApplicationLoader.mainInterfacePaused
                if (r2 != 0) goto L_0x00d3
                android.app.Activity r15 = (android.app.Activity) r15
                org.telegram.ui.Components.voip.VoIPPiPView r0 = org.telegram.ui.Components.voip.VoIPPiPView.this
                int r0 = r0.currentAccount
                org.telegram.ui.VoIPFragment.show(r15, r0)
                goto L_0x00e4
            L_0x00d3:
                if (r0 == 0) goto L_0x00e4
                android.content.Intent r0 = new android.content.Intent
                java.lang.Class<org.telegram.ui.LaunchActivity> r2 = org.telegram.ui.LaunchActivity.class
                r0.<init>(r15, r2)
                java.lang.String r2 = "voip"
                r0.setAction(r2)
                r15.startActivity(r0)
            L_0x00e4:
                org.telegram.ui.Components.voip.VoIPPiPView r15 = org.telegram.ui.Components.voip.VoIPPiPView.this
                r15.moving = r1
                return r1
            L_0x00e9:
                if (r3 == 0) goto L_0x021a
                r3.requestDisallowInterceptTouchEvent(r1)
                android.graphics.Point r15 = org.telegram.messenger.AndroidUtilities.displaySize
                int r0 = r15.x
                int r15 = r15.y
                int r2 = org.telegram.ui.Components.voip.VoIPPiPView.topInset
                int r15 = r15 + r2
                float r2 = r14.topPadding
                float r3 = r14.bottomPadding
                org.telegram.ui.Components.voip.VoIPPiPView r4 = org.telegram.ui.Components.voip.VoIPPiPView.this
                android.view.WindowManager$LayoutParams r9 = r4.windowLayoutParams
                int r9 = r9.x
                org.telegram.ui.Components.voip.VoIPPiPView$FloatingView r4 = r4.floatingView
                int r4 = r4.getLeft()
                int r9 = r9 + r4
                float r4 = (float) r9
                org.telegram.ui.Components.voip.VoIPPiPView r9 = org.telegram.ui.Components.voip.VoIPPiPView.this
                org.telegram.ui.Components.voip.VoIPPiPView$FloatingView r9 = r9.floatingView
                int r9 = r9.getMeasuredWidth()
                float r9 = (float) r9
                float r9 = r9 + r4
                org.telegram.ui.Components.voip.VoIPPiPView r10 = org.telegram.ui.Components.voip.VoIPPiPView.this
                android.view.WindowManager$LayoutParams r11 = r10.windowLayoutParams
                int r11 = r11.y
                org.telegram.ui.Components.voip.VoIPPiPView$FloatingView r10 = r10.floatingView
                int r10 = r10.getTop()
                int r11 = r11 + r10
                float r10 = (float) r11
                org.telegram.ui.Components.voip.VoIPPiPView r11 = org.telegram.ui.Components.voip.VoIPPiPView.this
                org.telegram.ui.Components.voip.VoIPPiPView$FloatingView r11 = r11.floatingView
                int r11 = r11.getMeasuredHeight()
                float r11 = (float) r11
                float r11 = r11 + r10
                org.telegram.ui.Components.voip.VoIPPiPView r12 = org.telegram.ui.Components.voip.VoIPPiPView.this
                android.animation.AnimatorSet r13 = new android.animation.AnimatorSet
                r13.<init>()
                r12.moveToBoundsAnimator = r13
                float r12 = r14.leftPadding
                int r4 = (r4 > r12 ? 1 : (r4 == r12 ? 0 : -1))
                if (r4 >= 0) goto L_0x0168
                float[] r0 = new float[r6]
                org.telegram.ui.Components.voip.VoIPPiPView r4 = org.telegram.ui.Components.voip.VoIPPiPView.this
                android.view.WindowManager$LayoutParams r9 = r4.windowLayoutParams
                int r9 = r9.x
                float r9 = (float) r9
                r0[r1] = r9
                org.telegram.ui.Components.voip.VoIPPiPView$FloatingView r4 = r4.floatingView
                int r4 = r4.getLeft()
                float r4 = (float) r4
                float r12 = r12 - r4
                r0[r5] = r12
                android.animation.ValueAnimator r0 = android.animation.ValueAnimator.ofFloat(r0)
                org.telegram.ui.Components.voip.VoIPPiPView r4 = org.telegram.ui.Components.voip.VoIPPiPView.this
                android.animation.ValueAnimator$AnimatorUpdateListener r4 = r4.updateXlistener
                r0.addUpdateListener(r4)
                org.telegram.ui.Components.voip.VoIPPiPView r4 = org.telegram.ui.Components.voip.VoIPPiPView.this
                android.animation.AnimatorSet r4 = r4.moveToBoundsAnimator
                android.animation.Animator[] r9 = new android.animation.Animator[r5]
                r9[r1] = r0
                r4.playTogether(r9)
                goto L_0x01a0
            L_0x0168:
                float r4 = (float) r0
                float r12 = r14.rightPadding
                float r4 = r4 - r12
                int r4 = (r9 > r4 ? 1 : (r9 == r4 ? 0 : -1))
                if (r4 <= 0) goto L_0x01a0
                float[] r4 = new float[r6]
                org.telegram.ui.Components.voip.VoIPPiPView r9 = org.telegram.ui.Components.voip.VoIPPiPView.this
                android.view.WindowManager$LayoutParams r12 = r9.windowLayoutParams
                int r12 = r12.x
                float r12 = (float) r12
                r4[r1] = r12
                org.telegram.ui.Components.voip.VoIPPiPView$FloatingView r9 = r9.floatingView
                int r9 = r9.getRight()
                int r0 = r0 - r9
                float r0 = (float) r0
                float r9 = r14.rightPadding
                float r0 = r0 - r9
                r4[r5] = r0
                android.animation.ValueAnimator r0 = android.animation.ValueAnimator.ofFloat(r4)
                org.telegram.ui.Components.voip.VoIPPiPView r4 = org.telegram.ui.Components.voip.VoIPPiPView.this
                android.animation.ValueAnimator$AnimatorUpdateListener r4 = r4.updateXlistener
                r0.addUpdateListener(r4)
                org.telegram.ui.Components.voip.VoIPPiPView r4 = org.telegram.ui.Components.voip.VoIPPiPView.this
                android.animation.AnimatorSet r4 = r4.moveToBoundsAnimator
                android.animation.Animator[] r9 = new android.animation.Animator[r5]
                r9[r1] = r0
                r4.playTogether(r9)
            L_0x01a0:
                int r0 = (r10 > r2 ? 1 : (r10 == r2 ? 0 : -1))
                if (r0 >= 0) goto L_0x01d2
                float[] r15 = new float[r6]
                org.telegram.ui.Components.voip.VoIPPiPView r0 = org.telegram.ui.Components.voip.VoIPPiPView.this
                android.view.WindowManager$LayoutParams r3 = r0.windowLayoutParams
                int r3 = r3.y
                float r3 = (float) r3
                r15[r1] = r3
                org.telegram.ui.Components.voip.VoIPPiPView$FloatingView r0 = r0.floatingView
                int r0 = r0.getTop()
                float r0 = (float) r0
                float r2 = r2 - r0
                r15[r5] = r2
                android.animation.ValueAnimator r15 = android.animation.ValueAnimator.ofFloat(r15)
                org.telegram.ui.Components.voip.VoIPPiPView r0 = org.telegram.ui.Components.voip.VoIPPiPView.this
                android.animation.ValueAnimator$AnimatorUpdateListener r0 = r0.updateYlistener
                r15.addUpdateListener(r0)
                org.telegram.ui.Components.voip.VoIPPiPView r0 = org.telegram.ui.Components.voip.VoIPPiPView.this
                android.animation.AnimatorSet r0 = r0.moveToBoundsAnimator
                android.animation.Animator[] r2 = new android.animation.Animator[r5]
                r2[r1] = r15
                r0.playTogether(r2)
                goto L_0x0206
            L_0x01d2:
                float r0 = (float) r15
                float r0 = r0 - r3
                int r0 = (r11 > r0 ? 1 : (r11 == r0 ? 0 : -1))
                if (r0 <= 0) goto L_0x0206
                float[] r0 = new float[r6]
                org.telegram.ui.Components.voip.VoIPPiPView r2 = org.telegram.ui.Components.voip.VoIPPiPView.this
                android.view.WindowManager$LayoutParams r4 = r2.windowLayoutParams
                int r4 = r4.y
                float r4 = (float) r4
                r0[r1] = r4
                org.telegram.ui.Components.voip.VoIPPiPView$FloatingView r2 = r2.floatingView
                int r2 = r2.getMeasuredHeight()
                int r15 = r15 - r2
                float r15 = (float) r15
                float r15 = r15 - r3
                r0[r5] = r15
                android.animation.ValueAnimator r15 = android.animation.ValueAnimator.ofFloat(r0)
                org.telegram.ui.Components.voip.VoIPPiPView r0 = org.telegram.ui.Components.voip.VoIPPiPView.this
                android.animation.ValueAnimator$AnimatorUpdateListener r0 = r0.updateYlistener
                r15.addUpdateListener(r0)
                org.telegram.ui.Components.voip.VoIPPiPView r0 = org.telegram.ui.Components.voip.VoIPPiPView.this
                android.animation.AnimatorSet r0 = r0.moveToBoundsAnimator
                android.animation.Animator[] r2 = new android.animation.Animator[r5]
                r2[r1] = r15
                r0.playTogether(r2)
            L_0x0206:
                org.telegram.ui.Components.voip.VoIPPiPView r15 = org.telegram.ui.Components.voip.VoIPPiPView.this
                android.animation.AnimatorSet r15 = r15.moveToBoundsAnimator
                android.animation.AnimatorSet r15 = r15.setDuration(r7)
                org.telegram.ui.Components.CubicBezierInterpolator r0 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
                r15.setInterpolator(r0)
                org.telegram.ui.Components.voip.VoIPPiPView r15 = org.telegram.ui.Components.voip.VoIPPiPView.this
                android.animation.AnimatorSet r15 = r15.moveToBoundsAnimator
                r15.start()
            L_0x021a:
                org.telegram.ui.Components.voip.VoIPPiPView r15 = org.telegram.ui.Components.voip.VoIPPiPView.this
                r15.moving = r1
                org.telegram.ui.Components.voip.VoIPPiPView r15 = org.telegram.ui.Components.voip.VoIPPiPView.instance
                boolean r15 = r15.expanded
                if (r15 == 0) goto L_0x0245
                org.telegram.ui.Components.voip.VoIPPiPView r15 = org.telegram.ui.Components.voip.VoIPPiPView.this
                java.lang.Runnable r15 = r15.collapseRunnable
                r0 = 3000(0xbb8, double:1.482E-320)
                org.telegram.messenger.AndroidUtilities.runOnUIThread(r15, r0)
                goto L_0x0245
            L_0x0230:
                org.telegram.ui.Components.voip.VoIPPiPView r15 = org.telegram.ui.Components.voip.VoIPPiPView.this
                r15.startX = r0
                r15.startY = r2
                long r0 = java.lang.System.currentTimeMillis()
                r15.startTime = r0
                org.telegram.ui.Components.voip.VoIPPiPView r15 = org.telegram.ui.Components.voip.VoIPPiPView.this
                android.animation.AnimatorSet r15 = r15.moveToBoundsAnimator
                if (r15 == 0) goto L_0x0245
                r15.cancel()
            L_0x0245:
                return r5
            L_0x0246:
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.voip.VoIPPiPView.FloatingView.onTouchEvent(android.view.MotionEvent):boolean");
        }

        /* access modifiers changed from: private */
        public void getRelativePosition(float[] fArr) {
            Point point = AndroidUtilities.displaySize;
            VoIPPiPView voIPPiPView = VoIPPiPView.this;
            float left = (float) (voIPPiPView.windowLayoutParams.x + voIPPiPView.floatingView.getLeft());
            float f = this.leftPadding;
            fArr[0] = (left - f) / (((((float) point.x) - f) - this.rightPadding) - ((float) VoIPPiPView.this.floatingView.getMeasuredWidth()));
            VoIPPiPView voIPPiPView2 = VoIPPiPView.this;
            float top = (float) (voIPPiPView2.windowLayoutParams.y + voIPPiPView2.floatingView.getTop());
            float f2 = this.topPadding;
            fArr[1] = (top - f2) / (((((float) point.y) - f2) - this.bottomPadding) - ((float) VoIPPiPView.this.floatingView.getMeasuredHeight()));
            fArr[0] = Math.min(1.0f, Math.max(0.0f, fArr[0]));
            fArr[1] = Math.min(1.0f, Math.max(0.0f, fArr[1]));
        }

        /* access modifiers changed from: private */
        public void expand(final boolean z) {
            AndroidUtilities.cancelRunOnUIThread(VoIPPiPView.this.collapseRunnable);
            if (VoIPPiPView.instance != null && !VoIPPiPView.this.expandedAnimationInProgress && VoIPPiPView.instance.expanded != z) {
                VoIPPiPView.instance.expanded = z;
                VoIPPiPView voIPPiPView = VoIPPiPView.this;
                int i = voIPPiPView.parentWidth;
                int i2 = voIPPiPView.xOffset;
                float f = (((float) i) * 0.25f) + ((float) (i2 * 2));
                int i3 = voIPPiPView.parentHeight;
                int i4 = voIPPiPView.yOffset;
                float f2 = (((float) i3) * 0.25f) + ((float) (i4 * 2));
                float f3 = (((float) i) * 0.4f) + ((float) (i2 * 2));
                float f4 = (((float) i3) * 0.4f) + ((float) (i4 * 2));
                boolean unused = voIPPiPView.expandedAnimationInProgress = true;
                if (z) {
                    Context context = VoIPPiPView.instance.windowView.getContext();
                    VoIPPiPView voIPPiPView2 = VoIPPiPView.this;
                    WindowManager.LayoutParams access$800 = VoIPPiPView.createWindowLayoutParams(context, voIPPiPView2.parentWidth, voIPPiPView2.parentHeight, 0.4f);
                    Context context2 = getContext();
                    VoIPPiPView voIPPiPView3 = VoIPPiPView.this;
                    VoIPPiPView voIPPiPView4 = new VoIPPiPView(context2, voIPPiPView3.parentWidth, voIPPiPView3.parentHeight, true);
                    getRelativePosition(VoIPPiPView.this.point);
                    VoIPPiPView voIPPiPView5 = VoIPPiPView.this;
                    float[] fArr = voIPPiPView5.point;
                    float f5 = fArr[0];
                    float f6 = fArr[1];
                    WindowManager.LayoutParams layoutParams = voIPPiPView5.windowLayoutParams;
                    access$800.x = (int) (((float) layoutParams.x) - ((f3 - f) * f5));
                    access$800.y = (int) (((float) layoutParams.y) - ((f4 - f2) * f6));
                    voIPPiPView5.windowManager.addView(voIPPiPView4.windowView, access$800);
                    voIPPiPView4.windowView.setAlpha(1.0f);
                    voIPPiPView4.windowLayoutParams = access$800;
                    WindowManager unused2 = voIPPiPView4.windowManager = VoIPPiPView.this.windowManager;
                    VoIPPiPView unused3 = VoIPPiPView.expandedInstance = voIPPiPView4;
                    swapRender(VoIPPiPView.instance, VoIPPiPView.expandedInstance);
                    float scaleX = VoIPPiPView.this.floatingView.getScaleX() * 0.625f;
                    voIPPiPView4.floatingView.setPivotX(f5 * ((float) VoIPPiPView.this.parentWidth) * 0.4f);
                    voIPPiPView4.floatingView.setPivotY(f6 * ((float) VoIPPiPView.this.parentHeight) * 0.4f);
                    voIPPiPView4.floatingView.setScaleX(scaleX);
                    voIPPiPView4.floatingView.setScaleY(scaleX);
                    VoIPPiPView.expandedInstance.topShadow.setAlpha(0.0f);
                    VoIPPiPView.expandedInstance.closeIcon.setAlpha(0.0f);
                    VoIPPiPView.expandedInstance.enlargeIcon.setAlpha(0.0f);
                    AndroidUtilities.runOnUIThread(new Runnable(scaleX, voIPPiPView4) {
                        public final /* synthetic */ float f$1;
                        public final /* synthetic */ VoIPPiPView f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void run() {
                            VoIPPiPView.FloatingView.this.lambda$expand$1$VoIPPiPView$FloatingView(this.f$1, this.f$2);
                        }
                    }, 64);
                } else if (VoIPPiPView.expandedInstance != null) {
                    VoIPPiPView.expandedInstance.floatingView.getRelativePosition(VoIPPiPView.this.point);
                    float[] fArr2 = VoIPPiPView.this.point;
                    float f7 = fArr2[0];
                    float f8 = fArr2[1];
                    VoIPPiPView.instance.windowLayoutParams.x = (int) (((float) VoIPPiPView.expandedInstance.windowLayoutParams.x) + ((f3 - f) * f7));
                    VoIPPiPView.instance.windowLayoutParams.y = (int) (((float) VoIPPiPView.expandedInstance.windowLayoutParams.y) + ((f4 - f2) * f8));
                    VoIPPiPView.expandedInstance.floatingView.setPivotX(f7 * ((float) VoIPPiPView.this.parentWidth) * 0.4f);
                    VoIPPiPView.expandedInstance.floatingView.setPivotY(f8 * ((float) VoIPPiPView.this.parentHeight) * 0.4f);
                    showUi(false);
                    ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                    ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(VoIPPiPView.this.floatingView.getScaleX() * 0.625f) {
                        public final /* synthetic */ float f$0;

                        {
                            this.f$0 = r1;
                        }

                        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                            VoIPPiPView.FloatingView.lambda$expand$2(this.f$0, valueAnimator);
                        }
                    });
                    ofFloat.setDuration(300).setInterpolator(CubicBezierInterpolator.DEFAULT);
                    ofFloat.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            if (VoIPPiPView.expandedInstance != null) {
                                FloatingView.this.swapRender(VoIPPiPView.expandedInstance, VoIPPiPView.instance);
                                VoIPPiPView.instance.windowView.setAlpha(1.0f);
                                VoIPPiPView.this.windowManager.addView(VoIPPiPView.instance.windowView, VoIPPiPView.instance.windowLayoutParams);
                                AndroidUtilities.runOnUIThread(
                                /*  JADX ERROR: Method code generation error
                                    jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x003f: INVOKE  
                                      (wrap: org.telegram.ui.Components.voip.-$$Lambda$VoIPPiPView$FloatingView$3$iG-hzrQPDlTOeOW_Jrz8inBcOeQ : 0x003a: CONSTRUCTOR  (r0v4 org.telegram.ui.Components.voip.-$$Lambda$VoIPPiPView$FloatingView$3$iG-hzrQPDlTOeOW_Jrz8inBcOeQ) = 
                                      (r3v0 'this' org.telegram.ui.Components.voip.VoIPPiPView$FloatingView$3 A[THIS])
                                      (wrap: boolean : 0x0036: IGET  (r4v8 boolean) = 
                                      (r3v0 'this' org.telegram.ui.Components.voip.VoIPPiPView$FloatingView$3 A[THIS])
                                     org.telegram.ui.Components.voip.VoIPPiPView.FloatingView.3.val$expanded boolean)
                                     call: org.telegram.ui.Components.voip.-$$Lambda$VoIPPiPView$FloatingView$3$iG-hzrQPDlTOeOW_Jrz8inBcOeQ.<init>(org.telegram.ui.Components.voip.VoIPPiPView$FloatingView$3, boolean):void type: CONSTRUCTOR)
                                      (64 long)
                                     org.telegram.messenger.AndroidUtilities.runOnUIThread(java.lang.Runnable, long):void type: STATIC in method: org.telegram.ui.Components.voip.VoIPPiPView.FloatingView.3.onAnimationEnd(android.animation.Animator):void, dex: classes3.dex
                                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                                    	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                                    	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                    	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                                    	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                                    	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                    	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                    	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                                    	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                                    	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                                    	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                                    	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                    	at java.util.ArrayList.forEach(ArrayList.java:1259)
                                    	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                    	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                    	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                    	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                    	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                    	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                    	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                    	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                    	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                    	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
                                    	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
                                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                    	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                                    	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                                    	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                                    	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                                    	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                                    	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                    	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                                    	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                                    	at jadx.core.codegen.RegionGen.connectElseIf(RegionGen.java:175)
                                    	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:152)
                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                                    	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                    	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                                    	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                                    	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                    	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                                    	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                                    	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                                    	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                                    	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                    	at java.util.ArrayList.forEach(ArrayList.java:1259)
                                    	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                    	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                    	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                    	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                    	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                    	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                    	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                    	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                    	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                    	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                                    	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                                    	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                    	at java.util.ArrayList.forEach(ArrayList.java:1259)
                                    	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                    	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                    	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                    	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                    	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                    	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                    	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                    	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                    	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                    	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                                    	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                                    	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                                    	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                                    	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                                    	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                                    	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                                    Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x003a: CONSTRUCTOR  (r0v4 org.telegram.ui.Components.voip.-$$Lambda$VoIPPiPView$FloatingView$3$iG-hzrQPDlTOeOW_Jrz8inBcOeQ) = 
                                      (r3v0 'this' org.telegram.ui.Components.voip.VoIPPiPView$FloatingView$3 A[THIS])
                                      (wrap: boolean : 0x0036: IGET  (r4v8 boolean) = 
                                      (r3v0 'this' org.telegram.ui.Components.voip.VoIPPiPView$FloatingView$3 A[THIS])
                                     org.telegram.ui.Components.voip.VoIPPiPView.FloatingView.3.val$expanded boolean)
                                     call: org.telegram.ui.Components.voip.-$$Lambda$VoIPPiPView$FloatingView$3$iG-hzrQPDlTOeOW_Jrz8inBcOeQ.<init>(org.telegram.ui.Components.voip.VoIPPiPView$FloatingView$3, boolean):void type: CONSTRUCTOR in method: org.telegram.ui.Components.voip.VoIPPiPView.FloatingView.3.onAnimationEnd(android.animation.Animator):void, dex: classes3.dex
                                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                                    	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                                    	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                                    	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                                    	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                                    	... 95 more
                                    Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: org.telegram.ui.Components.voip.-$$Lambda$VoIPPiPView$FloatingView$3$iG-hzrQPDlTOeOW_Jrz8inBcOeQ, state: NOT_LOADED
                                    	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                                    	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                    	... 101 more
                                    */
                                /*
                                    this = this;
                                    org.telegram.ui.Components.voip.VoIPPiPView r4 = org.telegram.ui.Components.voip.VoIPPiPView.expandedInstance
                                    if (r4 != 0) goto L_0x0007
                                    return
                                L_0x0007:
                                    org.telegram.ui.Components.voip.VoIPPiPView$FloatingView r4 = org.telegram.ui.Components.voip.VoIPPiPView.FloatingView.this
                                    org.telegram.ui.Components.voip.VoIPPiPView r0 = org.telegram.ui.Components.voip.VoIPPiPView.expandedInstance
                                    org.telegram.ui.Components.voip.VoIPPiPView r1 = org.telegram.ui.Components.voip.VoIPPiPView.instance
                                    r4.swapRender(r0, r1)
                                    org.telegram.ui.Components.voip.VoIPPiPView r4 = org.telegram.ui.Components.voip.VoIPPiPView.instance
                                    android.widget.FrameLayout r4 = r4.windowView
                                    r0 = 1065353216(0x3var_, float:1.0)
                                    r4.setAlpha(r0)
                                    org.telegram.ui.Components.voip.VoIPPiPView$FloatingView r4 = org.telegram.ui.Components.voip.VoIPPiPView.FloatingView.this
                                    org.telegram.ui.Components.voip.VoIPPiPView r4 = org.telegram.ui.Components.voip.VoIPPiPView.this
                                    android.view.WindowManager r4 = r4.windowManager
                                    org.telegram.ui.Components.voip.VoIPPiPView r0 = org.telegram.ui.Components.voip.VoIPPiPView.instance
                                    android.widget.FrameLayout r0 = r0.windowView
                                    org.telegram.ui.Components.voip.VoIPPiPView r1 = org.telegram.ui.Components.voip.VoIPPiPView.instance
                                    android.view.WindowManager$LayoutParams r1 = r1.windowLayoutParams
                                    r4.addView(r0, r1)
                                    boolean r4 = r13
                                    org.telegram.ui.Components.voip.-$$Lambda$VoIPPiPView$FloatingView$3$iG-hzrQPDlTOeOW_Jrz8inBcOeQ r0 = new org.telegram.ui.Components.voip.-$$Lambda$VoIPPiPView$FloatingView$3$iG-hzrQPDlTOeOW_Jrz8inBcOeQ
                                    r0.<init>(r3, r4)
                                    r1 = 64
                                    org.telegram.messenger.AndroidUtilities.runOnUIThread(r0, r1)
                                    return
                                */
                                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.voip.VoIPPiPView.FloatingView.AnonymousClass3.onAnimationEnd(android.animation.Animator):void");
                            }

                            /* access modifiers changed from: private */
                            /* renamed from: lambda$onAnimationEnd$0 */
                            public /* synthetic */ void lambda$onAnimationEnd$0$VoIPPiPView$FloatingView$3(boolean z) {
                                if (VoIPPiPView.instance != null && VoIPPiPView.expandedInstance != null) {
                                    VoIPPiPView.expandedInstance.windowView.setAlpha(0.0f);
                                    VoIPPiPView.expandedInstance.finishInternal();
                                    boolean unused = VoIPPiPView.this.expandedAnimationInProgress = false;
                                    if (z) {
                                        AndroidUtilities.runOnUIThread(VoIPPiPView.this.collapseRunnable, 3000);
                                    }
                                }
                            }
                        });
                        ofFloat.start();
                        VoIPPiPView.this.expandAnimator = ofFloat;
                    }
                }
            }

            /* access modifiers changed from: private */
            /* renamed from: lambda$expand$1 */
            public /* synthetic */ void lambda$expand$1$VoIPPiPView$FloatingView(float f, VoIPPiPView voIPPiPView) {
                if (VoIPPiPView.expandedInstance != null) {
                    VoIPPiPView.this.windowView.setAlpha(0.0f);
                    try {
                        VoIPPiPView.this.windowManager.removeView(VoIPPiPView.this.windowView);
                    } catch (Throwable th) {
                        FileLog.e(th);
                    }
                    animate().cancel();
                    showUi(true);
                    ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                    ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(f, 1.0f, voIPPiPView) {
                        public final /* synthetic */ float f$0;
                        public final /* synthetic */ float f$1;
                        public final /* synthetic */ VoIPPiPView f$2;

                        {
                            this.f$0 = r1;
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                            VoIPPiPView.FloatingView.lambda$expand$0(this.f$0, this.f$1, this.f$2, valueAnimator);
                        }
                    });
                    ofFloat.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            super.onAnimationEnd(animator);
                            boolean unused = VoIPPiPView.this.expandedAnimationInProgress = false;
                        }
                    });
                    ofFloat.setDuration(300).setInterpolator(CubicBezierInterpolator.DEFAULT);
                    ofFloat.start();
                    VoIPPiPView.this.expandAnimator = ofFloat;
                }
            }

            static /* synthetic */ void lambda$expand$0(float f, float f2, VoIPPiPView voIPPiPView, ValueAnimator valueAnimator) {
                float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                float f3 = (f * (1.0f - floatValue)) + (f2 * floatValue);
                voIPPiPView.floatingView.setScaleX(f3);
                voIPPiPView.floatingView.setScaleY(f3);
                voIPPiPView.floatingView.invalidate();
                voIPPiPView.windowView.invalidate();
                if (Build.VERSION.SDK_INT >= 21) {
                    voIPPiPView.floatingView.invalidateOutline();
                }
            }

            static /* synthetic */ void lambda$expand$2(float f, ValueAnimator valueAnimator) {
                float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                float f2 = (1.0f - floatValue) + (f * floatValue);
                if (VoIPPiPView.expandedInstance != null) {
                    VoIPPiPView.expandedInstance.floatingView.setScaleX(f2);
                    VoIPPiPView.expandedInstance.floatingView.setScaleY(f2);
                    VoIPPiPView.expandedInstance.floatingView.invalidate();
                    if (Build.VERSION.SDK_INT >= 21) {
                        VoIPPiPView.expandedInstance.floatingView.invalidateOutline();
                    }
                    VoIPPiPView.expandedInstance.windowView.invalidate();
                }
            }

            private void showUi(boolean z) {
                if (VoIPPiPView.expandedInstance != null) {
                    float f = 0.0f;
                    if (z) {
                        VoIPPiPView.expandedInstance.topShadow.setAlpha(0.0f);
                        VoIPPiPView.expandedInstance.closeIcon.setAlpha(0.0f);
                        VoIPPiPView.expandedInstance.enlargeIcon.setAlpha(0.0f);
                    }
                    ViewPropertyAnimator duration = VoIPPiPView.expandedInstance.topShadow.animate().alpha(z ? 1.0f : 0.0f).setDuration(300);
                    CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.DEFAULT;
                    duration.setInterpolator(cubicBezierInterpolator).start();
                    VoIPPiPView.expandedInstance.closeIcon.animate().alpha(z ? 1.0f : 0.0f).setDuration(300).setInterpolator(cubicBezierInterpolator).start();
                    ViewPropertyAnimator animate = VoIPPiPView.expandedInstance.enlargeIcon.animate();
                    if (z) {
                        f = 1.0f;
                    }
                    animate.alpha(f).setDuration(300).setInterpolator(cubicBezierInterpolator).start();
                }
            }

            /* access modifiers changed from: private */
            public void swapRender(VoIPPiPView voIPPiPView, VoIPPiPView voIPPiPView2) {
                voIPPiPView2.currentUserTextureView.setStub(voIPPiPView.currentUserTextureView);
                voIPPiPView2.callingUserTextureView.setStub(voIPPiPView.callingUserTextureView);
                voIPPiPView.currentUserTextureView.renderer.release();
                voIPPiPView.callingUserTextureView.renderer.release();
                EglBase eglBase = VideoCapturerDevice.eglBase;
                if (eglBase != null) {
                    voIPPiPView2.currentUserTextureView.renderer.init(eglBase.getEglBaseContext(), (RendererCommon.RendererEvents) null);
                    voIPPiPView2.callingUserTextureView.renderer.init(VideoCapturerDevice.eglBase.getEglBaseContext(), (RendererCommon.RendererEvents) null);
                    if (VoIPService.getSharedInstance() != null) {
                        VoIPService.getSharedInstance().setSinks(voIPPiPView2.currentUserTextureView.renderer, voIPPiPView2.callingUserTextureView.renderer);
                    }
                }
            }
        }
    }
