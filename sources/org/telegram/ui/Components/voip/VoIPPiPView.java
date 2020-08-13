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
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewOutlineProvider;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.voip.VideoCameraCapturer;
import org.telegram.messenger.voip.VoIPBaseService;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.voip.VoIPPiPView;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.VoIPFragment;
import org.webrtc.RendererCommon;

public class VoIPPiPView implements VoIPBaseService.StateListener {
    private static VoIPPiPView instance;
    int animationIndex = -1;
    ValueAnimator animatorToCameraMini;
    ValueAnimator.AnimatorUpdateListener animatorToCameraMiniUpdater = new ValueAnimator.AnimatorUpdateListener() {
        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
            VoIPPiPView.this.lambda$new$0$VoIPPiPView(valueAnimator);
        }
    };
    public int bottomInset;
    boolean callingUserIsVideo;
    /* access modifiers changed from: private */
    public VoIPTextureView callingUserTextureView;
    ImageView closeIcon;
    Runnable collapseRunnable = new Runnable() {
        public void run() {
            VoIPPiPView.this.floatingView.expand(false);
        }
    };
    /* access modifiers changed from: private */
    public VoIPTextureView currentUserTextureView;
    ImageView enlargeIcon;
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
    public boolean switchingToPip;
    public int topInset;
    View topShadow;
    /* access modifiers changed from: private */
    public ValueAnimator.AnimatorUpdateListener updateXlistener = new ValueAnimator.AnimatorUpdateListener() {
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            VoIPPiPView voIPPiPView = VoIPPiPView.this;
            voIPPiPView.windowLayoutParams.x = (int) floatValue;
            if (voIPPiPView.windowView.getParent() != null) {
                WindowManager access$100 = VoIPPiPView.this.windowManager;
                VoIPPiPView voIPPiPView2 = VoIPPiPView.this;
                access$100.updateViewLayout(voIPPiPView2.windowView, voIPPiPView2.windowLayoutParams);
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
                WindowManager access$100 = VoIPPiPView.this.windowManager;
                VoIPPiPView voIPPiPView2 = VoIPPiPView.this;
                access$100.updateViewLayout(voIPPiPView2.windowView, voIPPiPView2.windowLayoutParams);
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

    public void onSignalBarsCountChanged(int i) {
    }

    public void onVideoAvailableChange(boolean z) {
    }

    public /* synthetic */ void lambda$new$0$VoIPPiPView(ValueAnimator valueAnimator) {
        this.progressToCameraMini = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.floatingView.invalidate();
    }

    public static void show(Activity activity, int i, int i2, int i3) {
        if (instance == null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            float f = (float) i2;
            float f2 = f * 0.4f;
            float f3 = (float) i;
            float f4 = 0.4f * f3;
            layoutParams.height = (int) ((f * 0.25f) + ((float) ((((int) ((f2 * 1.05f) - f2)) / 2) * 2)));
            layoutParams.width = (int) ((f3 * 0.25f) + ((float) ((((int) ((1.05f * f4) - f4)) / 2) * 2)));
            layoutParams.gravity = 51;
            layoutParams.format = -3;
            if (Build.VERSION.SDK_INT >= 26) {
                layoutParams.type = 2038;
            } else {
                layoutParams.type = 2003;
            }
            instance = new VoIPPiPView(activity, i, i2);
            try {
                layoutParams.getClass().getField("privateFlags").set(layoutParams, Integer.valueOf(((Integer) layoutParams.getClass().getField("privateFlags").get(layoutParams)).intValue() | 64));
            } catch (Exception unused) {
            }
            layoutParams.flags = 16777992;
            if (Build.VERSION.SDK_INT >= 21) {
                layoutParams.flags = 16777992 | Integer.MIN_VALUE;
            }
            WindowManager windowManager2 = (WindowManager) activity.getSystemService("window");
            VoIPPiPView voIPPiPView = instance;
            voIPPiPView.windowManager = windowManager2;
            voIPPiPView.windowLayoutParams = layoutParams;
            SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("voippipconfig", 0);
            instance.setRelativePosition(sharedPreferences.getFloat("relativeX", 1.0f), sharedPreferences.getFloat("relativeY", 0.0f));
            windowManager2.addView(instance.windowView, layoutParams);
            instance.currentUserTextureView.renderer.init(VideoCameraCapturer.eglBase.getEglBaseContext(), (RendererCommon.RendererEvents) null);
            instance.callingUserTextureView.renderer.init(VideoCameraCapturer.eglBase.getEglBaseContext(), (RendererCommon.RendererEvents) null);
            if (i3 == 0) {
                instance.windowView.setScaleX(0.5f);
                instance.windowView.setScaleY(0.5f);
                instance.windowView.setAlpha(0.0f);
                instance.windowView.animate().alpha(1.0f).scaleY(1.0f).scaleX(1.0f).start();
                if (VoIPService.getSharedInstance() != null) {
                    VoIPService sharedInstance = VoIPService.getSharedInstance();
                    VoIPPiPView voIPPiPView2 = instance;
                    sharedInstance.setSinks(voIPPiPView2.currentUserTextureView.renderer, voIPPiPView2.callingUserTextureView.renderer);
                }
            } else if (i3 == 1) {
                instance.windowView.setAlpha(0.0f);
                if (VoIPService.getSharedInstance() != null) {
                    VoIPService sharedInstance2 = VoIPService.getSharedInstance();
                    VoIPPiPView voIPPiPView3 = instance;
                    sharedInstance2.setBackgroundSinks(voIPPiPView3.currentUserTextureView.renderer, voIPPiPView3.callingUserTextureView.renderer);
                }
            }
        }
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
        return instance;
    }

    public VoIPPiPView(Context context, int i, int i2) {
        this.parentWidth = i;
        this.parentHeight = i2;
        float f = ((float) i2) * 0.4f;
        this.yOffset = ((int) ((f * 1.05f) - f)) / 2;
        float f2 = ((float) i) * 0.4f;
        this.xOffset = ((int) ((1.05f * f2) - f2)) / 2;
        FrameLayout frameLayout = new FrameLayout(context);
        this.windowView = frameLayout;
        int i3 = this.xOffset;
        int i4 = this.yOffset;
        frameLayout.setPadding(i3, i4, i3, i4);
        this.floatingView = new FloatingView(context);
        this.callingUserTextureView = new VoIPTextureView(context, false);
        VoIPTextureView voIPTextureView = new VoIPTextureView(context, false);
        this.currentUserTextureView = voIPTextureView;
        voIPTextureView.renderer.setMirror(true);
        this.floatingView.addView(this.callingUserTextureView);
        this.floatingView.addView(this.currentUserTextureView);
        this.floatingView.setBackgroundColor(-7829368);
        this.windowView.addView(this.floatingView);
        this.windowView.setClipChildren(false);
        this.windowView.setClipToPadding(false);
        View view = new View(context);
        this.topShadow = view;
        view.setBackground(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{ColorUtils.setAlphaComponent(-16777216, 76), 0}));
        this.floatingView.addView(this.topShadow, -1, AndroidUtilities.dp(60.0f));
        ImageView imageView = new ImageView(context);
        this.closeIcon = imageView;
        imageView.setImageResource(NUM);
        this.closeIcon.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
        this.floatingView.addView(this.closeIcon, LayoutHelper.createFrame(40, 40.0f, 53, 4.0f, 4.0f, 4.0f, 0.0f));
        ImageView imageView2 = new ImageView(context);
        this.enlargeIcon = imageView2;
        imageView2.setImageResource(NUM);
        this.enlargeIcon.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
        this.floatingView.addView(this.enlargeIcon, LayoutHelper.createFrame(40, 40.0f, 51, 4.0f, 4.0f, 4.0f, 0.0f));
        this.closeIcon.setOnClickListener($$Lambda$VoIPPiPView$0Qsk63ZuxTpx_890HxG4bMQ6To.INSTANCE);
        this.enlargeIcon.setOnClickListener(new View.OnClickListener(context) {
            public final /* synthetic */ Context f$0;

            {
                this.f$0 = r1;
            }

            public final void onClick(View view) {
                VoIPPiPView.lambda$new$2(this.f$0, view);
            }
        });
        this.closeIcon.setVisibility(8);
        this.enlargeIcon.setVisibility(8);
        this.topShadow.setVisibility(8);
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
        }
    }

    static /* synthetic */ void lambda$new$2(Context context, View view) {
        boolean z = context instanceof LaunchActivity;
        if (z && !ApplicationLoader.mainInterfacePaused) {
            VoIPFragment.show((Activity) context);
        } else if (z) {
            Intent intent = new Intent(context, LaunchActivity.class);
            intent.setAction("voip");
            context.startActivity(intent);
        }
    }

    public void finish() {
        if (!this.switchingToPip) {
            this.currentUserTextureView.renderer.release();
            this.callingUserTextureView.renderer.release();
            VoIPService sharedInstance = VoIPService.getSharedInstance();
            if (sharedInstance != null) {
                sharedInstance.unregisterStateListener(this);
            }
            this.floatingView.getRelativePosition(this.point);
            float min = Math.min(1.0f, Math.max(0.0f, this.point[0]));
            ApplicationLoader.applicationContext.getSharedPreferences("voippipconfig", 0).edit().putFloat("relativeX", min).putFloat("relativeY", Math.min(1.0f, Math.max(0.0f, this.point[1]))).apply();
            this.windowView.setVisibility(8);
            this.windowManager.removeView(this.windowView);
            instance = null;
        }
    }

    public void onStateChanged(int i) {
        if (i == 11 || i == 17) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    VoIPPiPView.this.finish();
                }
            }, 200);
        }
        updateViewState();
    }

    public void onMediaStateUpdated(int i, int i2) {
        updateViewState();
    }

    public void onCameraSwitch(boolean z) {
        updateViewState();
    }

    private void updateViewState() {
        boolean z = this.floatingView.getMeasuredWidth() != 0;
        boolean z2 = this.callingUserIsVideo;
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance != null) {
            this.callingUserIsVideo = sharedInstance.getCurrentVideoState() == 2;
            int videoState = sharedInstance.getVideoState();
            this.currentUserTextureView.renderer.setMirror(sharedInstance.isFrontFaceCamera());
        }
        float f = 1.0f;
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

    private class FloatingView extends FrameLayout {
        float bottomPadding;
        int lastH;
        int lastW;
        float leftPadding;
        final Path path = new Path();
        final RectF rectF = new RectF();
        float rightPadding;
        float topPadding;
        float touchSlop;
        final Paint xRefPaint = new Paint(1);

        public FloatingView(Context context) {
            super(context);
            this.touchSlop = (float) ViewConfiguration.get(context).getScaledTouchSlop();
            if (Build.VERSION.SDK_INT >= 21) {
                setOutlineProvider(new ViewOutlineProvider(this, VoIPPiPView.this) {
                    @TargetApi(21)
                    public void getOutline(View view, Outline outline) {
                        outline.setRoundRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight(), (1.0f / view.getScaleX()) * ((float) AndroidUtilities.dp(4.0f)));
                    }
                });
                setClipToOutline(true);
                return;
            }
            this.xRefPaint.setColor(-16777216);
            this.xRefPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, i2);
            if (!(getMeasuredHeight() == this.lastH || getMeasuredWidth() == this.lastW)) {
                this.path.reset();
                this.rectF.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
                this.path.addRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), Path.Direction.CW);
                this.path.toggleInverseFillType();
            }
            this.lastH = getMeasuredHeight();
            this.lastW = getMeasuredWidth();
            this.leftPadding = (float) AndroidUtilities.dp(16.0f);
            this.rightPadding = (float) AndroidUtilities.dp(16.0f);
            this.topPadding = (float) AndroidUtilities.dp(60.0f);
            this.bottomPadding = (float) AndroidUtilities.dp(16.0f);
        }

        /* access modifiers changed from: protected */
        public void dispatchDraw(Canvas canvas) {
            VoIPPiPView.this.currentUserTextureView.setPivotX((float) VoIPPiPView.this.callingUserTextureView.getMeasuredWidth());
            VoIPPiPView.this.currentUserTextureView.setPivotY((float) VoIPPiPView.this.callingUserTextureView.getMeasuredHeight());
            VoIPPiPView.this.currentUserTextureView.setTranslationX(((float) (-AndroidUtilities.dp(4.0f))) * (1.0f / getScaleX()) * VoIPPiPView.this.progressToCameraMini);
            VoIPPiPView.this.currentUserTextureView.setTranslationY(((float) (-AndroidUtilities.dp(4.0f))) * (1.0f / getScaleY()) * VoIPPiPView.this.progressToCameraMini);
            VoIPPiPView.this.currentUserTextureView.setRoundCorners(((float) AndroidUtilities.dp(8.0f)) * (1.0f / getScaleY()) * VoIPPiPView.this.progressToCameraMini);
            VoIPPiPView.this.currentUserTextureView.setScaleX(((1.0f - VoIPPiPView.this.progressToCameraMini) * 0.6f) + 0.4f);
            VoIPPiPView.this.currentUserTextureView.setScaleY(((1.0f - VoIPPiPView.this.progressToCameraMini) * 0.6f) + 0.4f);
            super.dispatchDraw(canvas);
        }

        /* JADX WARNING: Code restructure failed: missing block: B:10:0x002f, code lost:
            if (r4 != 3) goto L_0x0214;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTouchEvent(android.view.MotionEvent r15) {
            /*
                r14 = this;
                org.telegram.ui.Components.voip.VoIPPiPView r0 = org.telegram.ui.Components.voip.VoIPPiPView.this
                boolean r0 = r0.expandedAnimationInProgress
                r1 = 0
                if (r0 != 0) goto L_0x0215
                org.telegram.ui.Components.voip.VoIPPiPView r0 = org.telegram.ui.Components.voip.VoIPPiPView.this
                boolean r2 = r0.switchingToPip
                if (r2 == 0) goto L_0x0011
                goto L_0x0215
            L_0x0011:
                java.lang.Runnable r0 = r0.collapseRunnable
                org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r0)
                float r0 = r15.getRawX()
                float r2 = r15.getRawY()
                android.view.ViewParent r3 = r14.getParent()
                int r4 = r15.getAction()
                r5 = 1
                if (r4 == 0) goto L_0x01ff
                r6 = 2
                if (r4 == r5) goto L_0x0091
                if (r4 == r6) goto L_0x0033
                r0 = 3
                if (r4 == r0) goto L_0x0091
                goto L_0x0214
            L_0x0033:
                org.telegram.ui.Components.voip.VoIPPiPView r15 = org.telegram.ui.Components.voip.VoIPPiPView.this
                float r1 = r15.startX
                float r1 = r0 - r1
                float r4 = r15.startY
                float r4 = r2 - r4
                boolean r15 = r15.moving
                r6 = 0
                if (r15 != 0) goto L_0x005e
                float r15 = r1 * r1
                float r7 = r4 * r4
                float r15 = r15 + r7
                float r7 = r14.touchSlop
                float r7 = r7 * r7
                int r15 = (r15 > r7 ? 1 : (r15 == r7 ? 0 : -1))
                if (r15 <= 0) goto L_0x005e
                if (r3 == 0) goto L_0x0054
                r3.requestDisallowInterceptTouchEvent(r5)
            L_0x0054:
                org.telegram.ui.Components.voip.VoIPPiPView r15 = org.telegram.ui.Components.voip.VoIPPiPView.this
                r15.moving = r5
                r15.startX = r0
                r15.startY = r2
                r1 = 0
                r4 = 0
            L_0x005e:
                org.telegram.ui.Components.voip.VoIPPiPView r15 = org.telegram.ui.Components.voip.VoIPPiPView.this
                boolean r3 = r15.moving
                if (r3 == 0) goto L_0x0214
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
                if (r15 == 0) goto L_0x0214
                org.telegram.ui.Components.voip.VoIPPiPView r15 = org.telegram.ui.Components.voip.VoIPPiPView.this
                android.view.WindowManager r15 = r15.windowManager
                org.telegram.ui.Components.voip.VoIPPiPView r0 = org.telegram.ui.Components.voip.VoIPPiPView.this
                android.widget.FrameLayout r1 = r0.windowView
                android.view.WindowManager$LayoutParams r0 = r0.windowLayoutParams
                r15.updateViewLayout(r1, r0)
                goto L_0x0214
            L_0x0091:
                org.telegram.ui.Components.voip.VoIPPiPView r0 = org.telegram.ui.Components.voip.VoIPPiPView.this
                android.animation.AnimatorSet r0 = r0.moveToBoundsAnimator
                if (r0 == 0) goto L_0x009a
                r0.cancel()
            L_0x009a:
                int r15 = r15.getAction()
                r7 = 150(0x96, double:7.4E-322)
                if (r15 != r5) goto L_0x00c0
                org.telegram.ui.Components.voip.VoIPPiPView r15 = org.telegram.ui.Components.voip.VoIPPiPView.this
                boolean r15 = r15.moving
                if (r15 != 0) goto L_0x00c0
                long r9 = java.lang.System.currentTimeMillis()
                org.telegram.ui.Components.voip.VoIPPiPView r15 = org.telegram.ui.Components.voip.VoIPPiPView.this
                long r11 = r15.startTime
                long r9 = r9 - r11
                int r0 = (r9 > r7 ? 1 : (r9 == r7 ? 0 : -1))
                if (r0 >= 0) goto L_0x00c0
                boolean r15 = r15.expanded
                r15 = r15 ^ r5
                r14.expand(r15)
                org.telegram.ui.Components.voip.VoIPPiPView r15 = org.telegram.ui.Components.voip.VoIPPiPView.this
                r15.moving = r1
                return r1
            L_0x00c0:
                if (r3 == 0) goto L_0x01ef
                r3.requestDisallowInterceptTouchEvent(r1)
                android.graphics.Point r15 = org.telegram.messenger.AndroidUtilities.displaySize
                int r0 = r15.x
                int r15 = r15.y
                org.telegram.ui.Components.voip.VoIPPiPView r2 = org.telegram.ui.Components.voip.VoIPPiPView.this
                int r3 = r2.topInset
                int r15 = r15 + r3
                float r3 = r14.topPadding
                float r4 = r14.bottomPadding
                android.view.WindowManager$LayoutParams r9 = r2.windowLayoutParams
                int r9 = r9.x
                org.telegram.ui.Components.voip.VoIPPiPView$FloatingView r2 = r2.floatingView
                int r2 = r2.getLeft()
                int r9 = r9 + r2
                float r2 = (float) r9
                org.telegram.ui.Components.voip.VoIPPiPView r9 = org.telegram.ui.Components.voip.VoIPPiPView.this
                org.telegram.ui.Components.voip.VoIPPiPView$FloatingView r9 = r9.floatingView
                int r9 = r9.getMeasuredWidth()
                float r9 = (float) r9
                float r9 = r9 + r2
                org.telegram.ui.Components.voip.VoIPPiPView r10 = org.telegram.ui.Components.voip.VoIPPiPView.this
                android.view.WindowManager$LayoutParams r11 = r10.windowLayoutParams
                int r11 = r11.y
                org.telegram.ui.Components.voip.VoIPPiPView$FloatingView r10 = r10.floatingView
                int r10 = r10.getTop()
                int r11 = r11 + r10
                float r10 = (float) r11
                org.telegram.ui.Components.voip.VoIPPiPView r11 = org.telegram.ui.Components.voip.VoIPPiPView.this
                org.telegram.ui.Components.voip.VoIPPiPView$FloatingView r11 = r11.floatingView
                int r11 = r11.getBottom()
                float r11 = (float) r11
                float r11 = r11 + r10
                org.telegram.ui.Components.voip.VoIPPiPView r12 = org.telegram.ui.Components.voip.VoIPPiPView.this
                android.animation.AnimatorSet r13 = new android.animation.AnimatorSet
                r13.<init>()
                r12.moveToBoundsAnimator = r13
                float r12 = r14.leftPadding
                int r2 = (r2 > r12 ? 1 : (r2 == r12 ? 0 : -1))
                if (r2 >= 0) goto L_0x013f
                float[] r0 = new float[r6]
                org.telegram.ui.Components.voip.VoIPPiPView r2 = org.telegram.ui.Components.voip.VoIPPiPView.this
                android.view.WindowManager$LayoutParams r9 = r2.windowLayoutParams
                int r9 = r9.x
                float r9 = (float) r9
                r0[r1] = r9
                org.telegram.ui.Components.voip.VoIPPiPView$FloatingView r2 = r2.floatingView
                int r2 = r2.getLeft()
                float r2 = (float) r2
                float r12 = r12 - r2
                r0[r5] = r12
                android.animation.ValueAnimator r0 = android.animation.ValueAnimator.ofFloat(r0)
                org.telegram.ui.Components.voip.VoIPPiPView r2 = org.telegram.ui.Components.voip.VoIPPiPView.this
                android.animation.ValueAnimator$AnimatorUpdateListener r2 = r2.updateXlistener
                r0.addUpdateListener(r2)
                org.telegram.ui.Components.voip.VoIPPiPView r2 = org.telegram.ui.Components.voip.VoIPPiPView.this
                android.animation.AnimatorSet r2 = r2.moveToBoundsAnimator
                android.animation.Animator[] r9 = new android.animation.Animator[r5]
                r9[r1] = r0
                r2.playTogether(r9)
                goto L_0x0177
            L_0x013f:
                float r2 = (float) r0
                float r12 = r14.rightPadding
                float r2 = r2 - r12
                int r2 = (r9 > r2 ? 1 : (r9 == r2 ? 0 : -1))
                if (r2 <= 0) goto L_0x0177
                float[] r2 = new float[r6]
                org.telegram.ui.Components.voip.VoIPPiPView r9 = org.telegram.ui.Components.voip.VoIPPiPView.this
                android.view.WindowManager$LayoutParams r12 = r9.windowLayoutParams
                int r12 = r12.x
                float r12 = (float) r12
                r2[r1] = r12
                org.telegram.ui.Components.voip.VoIPPiPView$FloatingView r9 = r9.floatingView
                int r9 = r9.getRight()
                int r0 = r0 - r9
                float r0 = (float) r0
                float r9 = r14.rightPadding
                float r0 = r0 - r9
                r2[r5] = r0
                android.animation.ValueAnimator r0 = android.animation.ValueAnimator.ofFloat(r2)
                org.telegram.ui.Components.voip.VoIPPiPView r2 = org.telegram.ui.Components.voip.VoIPPiPView.this
                android.animation.ValueAnimator$AnimatorUpdateListener r2 = r2.updateXlistener
                r0.addUpdateListener(r2)
                org.telegram.ui.Components.voip.VoIPPiPView r2 = org.telegram.ui.Components.voip.VoIPPiPView.this
                android.animation.AnimatorSet r2 = r2.moveToBoundsAnimator
                android.animation.Animator[] r9 = new android.animation.Animator[r5]
                r9[r1] = r0
                r2.playTogether(r9)
            L_0x0177:
                int r0 = (r10 > r3 ? 1 : (r10 == r3 ? 0 : -1))
                if (r0 >= 0) goto L_0x01a9
                float[] r15 = new float[r6]
                org.telegram.ui.Components.voip.VoIPPiPView r0 = org.telegram.ui.Components.voip.VoIPPiPView.this
                android.view.WindowManager$LayoutParams r2 = r0.windowLayoutParams
                int r2 = r2.y
                float r2 = (float) r2
                r15[r1] = r2
                org.telegram.ui.Components.voip.VoIPPiPView$FloatingView r0 = r0.floatingView
                int r0 = r0.getTop()
                float r0 = (float) r0
                float r3 = r3 - r0
                r15[r5] = r3
                android.animation.ValueAnimator r15 = android.animation.ValueAnimator.ofFloat(r15)
                org.telegram.ui.Components.voip.VoIPPiPView r0 = org.telegram.ui.Components.voip.VoIPPiPView.this
                android.animation.ValueAnimator$AnimatorUpdateListener r0 = r0.updateYlistener
                r15.addUpdateListener(r0)
                org.telegram.ui.Components.voip.VoIPPiPView r0 = org.telegram.ui.Components.voip.VoIPPiPView.this
                android.animation.AnimatorSet r0 = r0.moveToBoundsAnimator
                android.animation.Animator[] r2 = new android.animation.Animator[r5]
                r2[r1] = r15
                r0.playTogether(r2)
                goto L_0x01db
            L_0x01a9:
                float r0 = (float) r15
                float r0 = r0 - r4
                int r0 = (r11 > r0 ? 1 : (r11 == r0 ? 0 : -1))
                if (r0 <= 0) goto L_0x01db
                float[] r0 = new float[r6]
                org.telegram.ui.Components.voip.VoIPPiPView r2 = org.telegram.ui.Components.voip.VoIPPiPView.this
                android.view.WindowManager$LayoutParams r2 = r2.windowLayoutParams
                int r2 = r2.y
                float r2 = (float) r2
                r0[r1] = r2
                int r2 = r14.getMeasuredHeight()
                int r15 = r15 - r2
                float r15 = (float) r15
                float r15 = r15 - r4
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
            L_0x01db:
                org.telegram.ui.Components.voip.VoIPPiPView r15 = org.telegram.ui.Components.voip.VoIPPiPView.this
                android.animation.AnimatorSet r15 = r15.moveToBoundsAnimator
                android.animation.AnimatorSet r15 = r15.setDuration(r7)
                org.telegram.ui.Components.CubicBezierInterpolator r0 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
                r15.setInterpolator(r0)
                org.telegram.ui.Components.voip.VoIPPiPView r15 = org.telegram.ui.Components.voip.VoIPPiPView.this
                android.animation.AnimatorSet r15 = r15.moveToBoundsAnimator
                r15.start()
            L_0x01ef:
                org.telegram.ui.Components.voip.VoIPPiPView r15 = org.telegram.ui.Components.voip.VoIPPiPView.this
                r15.moving = r1
                boolean r0 = r15.expanded
                if (r0 == 0) goto L_0x0214
                java.lang.Runnable r15 = r15.collapseRunnable
                r0 = 3000(0xbb8, double:1.482E-320)
                org.telegram.messenger.AndroidUtilities.runOnUIThread(r15, r0)
                goto L_0x0214
            L_0x01ff:
                org.telegram.ui.Components.voip.VoIPPiPView r15 = org.telegram.ui.Components.voip.VoIPPiPView.this
                r15.startX = r0
                r15.startY = r2
                long r0 = java.lang.System.currentTimeMillis()
                r15.startTime = r0
                org.telegram.ui.Components.voip.VoIPPiPView r15 = org.telegram.ui.Components.voip.VoIPPiPView.this
                android.animation.AnimatorSet r15 = r15.moveToBoundsAnimator
                if (r15 == 0) goto L_0x0214
                r15.cancel()
            L_0x0214:
                return r5
            L_0x0215:
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.voip.VoIPPiPView.FloatingView.onTouchEvent(android.view.MotionEvent):boolean");
        }

        /* access modifiers changed from: private */
        public void expand(boolean z) {
            boolean z2 = z;
            AndroidUtilities.cancelRunOnUIThread(VoIPPiPView.this.collapseRunnable);
            if (!VoIPPiPView.this.expandedAnimationInProgress) {
                getRelativePosition(VoIPPiPView.this.point);
                float[] fArr = VoIPPiPView.this.point;
                float f = fArr[0];
                float f2 = fArr[1];
                animate().cancel();
                VoIPPiPView voIPPiPView = VoIPPiPView.this;
                int i = voIPPiPView.parentWidth;
                int i2 = voIPPiPView.xOffset;
                final float f3 = (((float) i) * 0.25f) + ((float) (i2 * 2));
                int i3 = voIPPiPView.parentHeight;
                int i4 = voIPPiPView.yOffset;
                final float f4 = (((float) i3) * 0.25f) + ((float) (i4 * 2));
                float f5 = (((float) i) * 0.4f) + ((float) (i2 * 2));
                float f6 = (((float) i3) * 0.4f) + ((float) (i4 * 2));
                float scaleX = 0.625f * voIPPiPView.floatingView.getScaleX();
                VoIPPiPView.this.floatingView.setPivotX(0.0f);
                VoIPPiPView.this.floatingView.setPivotY(0.0f);
                if (z2) {
                    VoIPPiPView voIPPiPView2 = VoIPPiPView.this;
                    WindowManager.LayoutParams layoutParams = voIPPiPView2.windowLayoutParams;
                    layoutParams.width = (int) f5;
                    layoutParams.height = (int) f6;
                    boolean unused = voIPPiPView2.expandedAnimationInProgress = true;
                    if (VoIPPiPView.this.windowView.getParent() != null) {
                        WindowManager access$100 = VoIPPiPView.this.windowManager;
                        VoIPPiPView voIPPiPView3 = VoIPPiPView.this;
                        access$100.updateViewLayout(voIPPiPView3.windowView, voIPPiPView3.windowLayoutParams);
                    }
                    VoIPPiPView.this.floatingView.invalidate();
                    VoIPPiPView.this.floatingView.setScaleX(scaleX);
                    VoIPPiPView.this.floatingView.setScaleY(scaleX);
                    VoIPPiPView.this.floatingView.animate().translationX(0.0f).translationY(0.0f);
                    VoIPPiPView voIPPiPView4 = VoIPPiPView.this;
                    WindowManager.LayoutParams layoutParams2 = voIPPiPView4.windowLayoutParams;
                    int i5 = layoutParams2.x;
                    int i6 = layoutParams2.y;
                    int i7 = (int) (((float) i6) - ((f6 - f4) * f2));
                    voIPPiPView4.closeIcon.setVisibility(0);
                    VoIPPiPView.this.enlargeIcon.setVisibility(0);
                    VoIPPiPView.this.topShadow.setVisibility(0);
                    VoIPPiPView.this.enlargeIcon.setAlpha(0.0f);
                    VoIPPiPView.this.closeIcon.setAlpha(0.0f);
                    VoIPPiPView.this.topShadow.setAlpha(0.0f);
                    VoIPPiPView.this.closeIcon.animate().alpha(1.0f).setDuration(200).start();
                    VoIPPiPView.this.enlargeIcon.animate().alpha(1.0f).setDuration(200).start();
                    VoIPPiPView.this.topShadow.animate().alpha(1.0f).setDuration(200).start();
                    ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                    ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(scaleX, 1.0f, i5, (int) (((float) i5) - ((f5 - f3) * f)), i6, i7) {
                        public final /* synthetic */ float f$1;
                        public final /* synthetic */ float f$2;
                        public final /* synthetic */ int f$3;
                        public final /* synthetic */ int f$4;
                        public final /* synthetic */ int f$5;
                        public final /* synthetic */ int f$6;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                            this.f$3 = r4;
                            this.f$4 = r5;
                            this.f$5 = r6;
                            this.f$6 = r7;
                        }

                        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                            VoIPPiPView.FloatingView.this.lambda$expand$0$VoIPPiPView$FloatingView(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, valueAnimator);
                        }
                    });
                    VoIPPiPView.this.animationIndex = NotificationCenter.getInstance(UserConfig.selectedAccount).setAnimationInProgress(VoIPPiPView.this.animationIndex, (int[]) null);
                    ofFloat.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            VoIPPiPView.this.floatingView.setScaleX(1.0f);
                            VoIPPiPView.this.floatingView.setScaleY(1.0f);
                            if (VoIPPiPView.this.windowView.getParent() != null) {
                                WindowManager access$100 = VoIPPiPView.this.windowManager;
                                VoIPPiPView voIPPiPView = VoIPPiPView.this;
                                access$100.updateViewLayout(voIPPiPView.windowView, voIPPiPView.windowLayoutParams);
                            }
                            VoIPPiPView.this.closeIcon.setAlpha(1.0f);
                            VoIPPiPView.this.enlargeIcon.setAlpha(1.0f);
                            VoIPPiPView.this.topShadow.setAlpha(1.0f);
                            VoIPPiPView.this.closeIcon.setVisibility(0);
                            VoIPPiPView.this.enlargeIcon.setVisibility(0);
                            VoIPPiPView.this.topShadow.setVisibility(0);
                            AndroidUtilities.runOnUIThread(VoIPPiPView.this.collapseRunnable, 3000);
                            NotificationCenter.getInstance(UserConfig.selectedAccount).onAnimationFinish(VoIPPiPView.this.animationIndex);
                            boolean unused = VoIPPiPView.this.expandedAnimationInProgress = false;
                        }
                    });
                    ofFloat.setDuration(300).setInterpolator(CubicBezierInterpolator.DEFAULT);
                    ofFloat.start();
                } else {
                    VoIPPiPView.this.floatingView.setScaleX(1.0f);
                    VoIPPiPView.this.floatingView.setScaleY(1.0f);
                    boolean unused2 = VoIPPiPView.this.expandedAnimationInProgress = true;
                    VoIPPiPView.this.closeIcon.animate().alpha(0.0f).setDuration(200).start();
                    VoIPPiPView.this.enlargeIcon.animate().alpha(0.0f).setDuration(200).start();
                    VoIPPiPView.this.topShadow.animate().alpha(0.0f).setDuration(200).start();
                    WindowManager.LayoutParams layoutParams3 = VoIPPiPView.this.windowLayoutParams;
                    int i8 = layoutParams3.x;
                    int i9 = layoutParams3.y;
                    ValueAnimator ofFloat2 = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                    ofFloat2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(1.0f, scaleX, i8, (int) (((float) i8) + ((f5 - f3) * f)), i9, (int) (((float) i9) + ((f6 - f4) * f2))) {
                        public final /* synthetic */ float f$1;
                        public final /* synthetic */ float f$2;
                        public final /* synthetic */ int f$3;
                        public final /* synthetic */ int f$4;
                        public final /* synthetic */ int f$5;
                        public final /* synthetic */ int f$6;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                            this.f$3 = r4;
                            this.f$4 = r5;
                            this.f$5 = r6;
                            this.f$6 = r7;
                        }

                        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                            VoIPPiPView.FloatingView.this.lambda$expand$1$VoIPPiPView$FloatingView(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, valueAnimator);
                        }
                    });
                    VoIPPiPView.this.animationIndex = NotificationCenter.getInstance(UserConfig.selectedAccount).setAnimationInProgress(VoIPPiPView.this.animationIndex, (int[]) null);
                    ofFloat2.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            NotificationCenter.getInstance(UserConfig.selectedAccount).onAnimationFinish(VoIPPiPView.this.animationIndex);
                            VoIPPiPView voIPPiPView = VoIPPiPView.this;
                            WindowManager.LayoutParams layoutParams = voIPPiPView.windowLayoutParams;
                            layoutParams.width = (int) f3;
                            layoutParams.height = (int) f4;
                            if (voIPPiPView.windowView.getParent() != null) {
                                WindowManager access$100 = VoIPPiPView.this.windowManager;
                                VoIPPiPView voIPPiPView2 = VoIPPiPView.this;
                                access$100.updateViewLayout(voIPPiPView2.windowView, voIPPiPView2.windowLayoutParams);
                            }
                            VoIPPiPView.this.floatingView.setScaleX(1.0f);
                            VoIPPiPView.this.floatingView.setScaleY(1.0f);
                            boolean unused = VoIPPiPView.this.expandedAnimationInProgress = false;
                            VoIPPiPView.this.closeIcon.setVisibility(8);
                            VoIPPiPView.this.enlargeIcon.setVisibility(8);
                            VoIPPiPView.this.topShadow.setVisibility(8);
                        }
                    });
                    ofFloat2.setDuration(300).setInterpolator(CubicBezierInterpolator.DEFAULT);
                    ofFloat2.start();
                }
                VoIPPiPView.this.expanded = z2;
            }
        }

        public /* synthetic */ void lambda$expand$0$VoIPPiPView$FloatingView(float f, float f2, int i, int i2, int i3, int i4, ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            float f3 = 1.0f - floatValue;
            float f4 = (f * f3) + (f2 * floatValue);
            VoIPPiPView.this.floatingView.setScaleX(f4);
            VoIPPiPView.this.floatingView.setScaleY(f4);
            VoIPPiPView voIPPiPView = VoIPPiPView.this;
            WindowManager.LayoutParams layoutParams = voIPPiPView.windowLayoutParams;
            layoutParams.x = (int) ((((float) i) * f3) + (((float) i2) * floatValue));
            layoutParams.y = (int) ((((float) i3) * f3) + (((float) i4) * floatValue));
            if (voIPPiPView.windowView.getParent() != null) {
                WindowManager access$100 = VoIPPiPView.this.windowManager;
                VoIPPiPView voIPPiPView2 = VoIPPiPView.this;
                access$100.updateViewLayout(voIPPiPView2.windowView, voIPPiPView2.windowLayoutParams);
            }
        }

        public /* synthetic */ void lambda$expand$1$VoIPPiPView$FloatingView(float f, float f2, int i, int i2, int i3, int i4, ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            float f3 = 1.0f - floatValue;
            float f4 = (f * f3) + (f2 * floatValue);
            VoIPPiPView.this.floatingView.setScaleX(f4);
            VoIPPiPView.this.floatingView.setScaleY(f4);
            VoIPPiPView voIPPiPView = VoIPPiPView.this;
            WindowManager.LayoutParams layoutParams = voIPPiPView.windowLayoutParams;
            layoutParams.x = (int) ((((float) i) * f3) + (((float) i2) * floatValue));
            layoutParams.y = (int) ((((float) i3) * f3) + (((float) i4) * floatValue));
            if (voIPPiPView.windowView.getParent() != null) {
                WindowManager access$100 = VoIPPiPView.this.windowManager;
                VoIPPiPView voIPPiPView2 = VoIPPiPView.this;
                access$100.updateViewLayout(voIPPiPView2.windowView, voIPPiPView2.windowLayoutParams);
            }
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
        }
    }
}
