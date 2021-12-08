package org.telegram.ui.Components.voip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewOutlineProvider;
import android.view.ViewParent;
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
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.VoIPFragment;
import org.webrtc.RendererCommon;

public class VoIPPiPView implements VoIPService.StateListener, NotificationCenter.NotificationCenterDelegate {
    public static final int ANIMATION_ENTER_TYPE_NONE = 3;
    public static final int ANIMATION_ENTER_TYPE_SCALE = 0;
    public static final int ANIMATION_ENTER_TYPE_TRANSITION = 1;
    private static final float SCALE_EXPANDED = 0.4f;
    private static final float SCALE_NORMAL = 0.25f;
    public static int bottomInset;
    /* access modifiers changed from: private */
    public static VoIPPiPView expandedInstance;
    /* access modifiers changed from: private */
    public static VoIPPiPView instance;
    public static boolean switchingToPip = false;
    public static int topInset;
    int animationIndex = -1;
    ValueAnimator animatorToCameraMini;
    ValueAnimator.AnimatorUpdateListener animatorToCameraMiniUpdater = new VoIPPiPView$$ExternalSyntheticLambda0(this);
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
            VoIPPiPView.this.windowLayoutParams.x = (int) ((Float) valueAnimator.getAnimatedValue()).floatValue();
            if (VoIPPiPView.this.windowView.getParent() != null) {
                VoIPPiPView.this.windowManager.updateViewLayout(VoIPPiPView.this.windowView, VoIPPiPView.this.windowLayoutParams);
            }
        }
    };
    /* access modifiers changed from: private */
    public ValueAnimator.AnimatorUpdateListener updateYlistener = new ValueAnimator.AnimatorUpdateListener() {
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            VoIPPiPView.this.windowLayoutParams.y = (int) ((Float) valueAnimator.getAnimatedValue()).floatValue();
            if (VoIPPiPView.this.windowView.getParent() != null) {
                VoIPPiPView.this.windowManager.updateViewLayout(VoIPPiPView.this.windowView, VoIPPiPView.this.windowLayoutParams);
            }
        }
    };
    public WindowManager.LayoutParams windowLayoutParams;
    /* access modifiers changed from: private */
    public WindowManager windowManager;
    public FrameLayout windowView;
    public int xOffset;
    public int yOffset;

    public /* synthetic */ void onCameraFirstFrameAvailable() {
        VoIPService.StateListener.CC.$default$onCameraFirstFrameAvailable(this);
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-voip-VoIPPiPView  reason: not valid java name */
    public /* synthetic */ void m2768lambda$new$0$orgtelegramuiComponentsvoipVoIPPiPView(ValueAnimator valueAnimator) {
        this.progressToCameraMini = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.floatingView.invalidate();
    }

    public static void show(Activity activity, int account, int parentWidth2, int parentHeight2, int animationType) {
        WindowManager wm;
        if (instance == null && VideoCapturerDevice.eglBase != null) {
            WindowManager.LayoutParams windowLayoutParams2 = createWindowLayoutParams(activity, parentWidth2, parentHeight2, 0.25f);
            instance = new VoIPPiPView(activity, parentWidth2, parentHeight2, false);
            if (AndroidUtilities.checkInlinePermissions(activity)) {
                wm = (WindowManager) ApplicationLoader.applicationContext.getSystemService("window");
            } else {
                wm = (WindowManager) activity.getSystemService("window");
            }
            VoIPPiPView voIPPiPView = instance;
            voIPPiPView.currentAccount = account;
            voIPPiPView.windowManager = wm;
            voIPPiPView.windowLayoutParams = windowLayoutParams2;
            SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("voippipconfig", 0);
            instance.setRelativePosition(preferences.getFloat("relativeX", 1.0f), preferences.getFloat("relativeY", 0.0f));
            NotificationCenter.getGlobalInstance().addObserver(instance, NotificationCenter.didEndCall);
            wm.addView(instance.windowView, windowLayoutParams2);
            instance.currentUserTextureView.renderer.init(VideoCapturerDevice.eglBase.getEglBaseContext(), (RendererCommon.RendererEvents) null);
            instance.callingUserTextureView.renderer.init(VideoCapturerDevice.eglBase.getEglBaseContext(), (RendererCommon.RendererEvents) null);
            if (animationType == 0) {
                instance.windowView.setScaleX(0.5f);
                instance.windowView.setScaleY(0.5f);
                instance.windowView.setAlpha(0.0f);
                instance.windowView.animate().alpha(1.0f).scaleY(1.0f).scaleX(1.0f).start();
                if (VoIPService.getSharedInstance() != null) {
                    VoIPService.getSharedInstance().setSinks(instance.currentUserTextureView.renderer, instance.callingUserTextureView.renderer);
                }
            } else if (animationType == 1) {
                instance.windowView.setAlpha(0.0f);
                if (VoIPService.getSharedInstance() != null) {
                    VoIPService.getSharedInstance().setBackgroundSinks(instance.currentUserTextureView.renderer, instance.callingUserTextureView.renderer);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public static WindowManager.LayoutParams createWindowLayoutParams(Context context, int parentWidth2, int parentHeight2, float scale) {
        WindowManager.LayoutParams windowLayoutParams2 = new WindowManager.LayoutParams();
        windowLayoutParams2.height = (int) ((((float) parentHeight2) * scale) + ((float) ((((int) (((((float) parentHeight2) * 0.4f) * 1.05f) - (((float) parentHeight2) * 0.4f))) / 2) * 2)));
        windowLayoutParams2.width = (int) ((((float) parentWidth2) * scale) + ((float) ((((int) (((((float) parentWidth2) * 0.4f) * 1.05f) - (((float) parentWidth2) * 0.4f))) / 2) * 2)));
        windowLayoutParams2.gravity = 51;
        windowLayoutParams2.format = -3;
        if (!AndroidUtilities.checkInlinePermissions(context)) {
            windowLayoutParams2.type = 99;
        } else if (Build.VERSION.SDK_INT >= 26) {
            windowLayoutParams2.type = 2038;
        } else {
            windowLayoutParams2.type = 2003;
        }
        if (Build.VERSION.SDK_INT >= 21) {
            windowLayoutParams2.flags |= Integer.MIN_VALUE;
        }
        windowLayoutParams2.flags = 16778120;
        return windowLayoutParams2;
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

    private void setRelativePosition(float x, float y) {
        float width = (float) AndroidUtilities.displaySize.x;
        float height = (float) AndroidUtilities.displaySize.y;
        float leftPadding = (float) AndroidUtilities.dp(16.0f);
        float rightPadding = (float) AndroidUtilities.dp(16.0f);
        float topPadding = (float) AndroidUtilities.dp(60.0f);
        float bottomPadding = (float) AndroidUtilities.dp(16.0f);
        float widthNormal = ((float) this.parentWidth) * 0.25f;
        float heightNormal = ((float) this.parentHeight) * 0.25f;
        float floatingWidth = this.floatingView.getMeasuredWidth() == 0 ? widthNormal : (float) this.floatingView.getMeasuredWidth();
        float floatingHeight = this.floatingView.getMeasuredWidth() == 0 ? heightNormal : (float) this.floatingView.getMeasuredHeight();
        this.windowLayoutParams.x = (int) (((((width - leftPadding) - rightPadding) - floatingWidth) * x) - (((float) this.xOffset) - leftPadding));
        this.windowLayoutParams.y = (int) (((((height - topPadding) - bottomPadding) - floatingHeight) * y) - (((float) this.yOffset) - topPadding));
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

    public VoIPPiPView(Context context, int parentWidth2, int parentHeight2, boolean expanded2) {
        this.parentWidth = parentWidth2;
        this.parentHeight = parentHeight2;
        this.yOffset = ((int) (((((float) parentHeight2) * 0.4f) * 1.05f) - (((float) parentHeight2) * 0.4f))) / 2;
        this.xOffset = ((int) (((((float) parentWidth2) * 0.4f) * 1.05f) - (((float) parentWidth2) * 0.4f))) / 2;
        final Drawable outerDrawable = ContextCompat.getDrawable(context, NUM);
        AnonymousClass4 r3 = new FrameLayout(context) {
            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                canvas.save();
                canvas.scale(VoIPPiPView.this.floatingView.getScaleX(), VoIPPiPView.this.floatingView.getScaleY(), ((float) VoIPPiPView.this.floatingView.getLeft()) + VoIPPiPView.this.floatingView.getPivotX(), ((float) VoIPPiPView.this.floatingView.getTop()) + VoIPPiPView.this.floatingView.getPivotY());
                outerDrawable.setBounds(VoIPPiPView.this.floatingView.getLeft() - AndroidUtilities.dp(2.0f), VoIPPiPView.this.floatingView.getTop() - AndroidUtilities.dp(2.0f), VoIPPiPView.this.floatingView.getRight() + AndroidUtilities.dp(2.0f), VoIPPiPView.this.floatingView.getBottom() + AndroidUtilities.dp(2.0f));
                outerDrawable.draw(canvas);
                canvas.restore();
                super.onDraw(canvas);
            }
        };
        this.windowView = r3;
        r3.setWillNotDraw(false);
        FrameLayout frameLayout = this.windowView;
        int i = this.xOffset;
        int i2 = this.yOffset;
        frameLayout.setPadding(i, i2, i, i2);
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
        if (expanded2) {
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
            this.closeIcon.setOnClickListener(VoIPPiPView$$ExternalSyntheticLambda2.INSTANCE);
            this.enlargeIcon.setOnClickListener(new VoIPPiPView$$ExternalSyntheticLambda1(this, context));
        }
        VoIPService service = VoIPService.getSharedInstance();
        if (service != null) {
            service.registerStateListener(this);
        }
        updateViewState();
    }

    static /* synthetic */ void lambda$new$1(View v) {
        VoIPService service = VoIPService.getSharedInstance();
        if (service != null) {
            service.hangUp();
        } else {
            finish();
        }
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-voip-VoIPPiPView  reason: not valid java name */
    public /* synthetic */ void m2769lambda$new$2$orgtelegramuiComponentsvoipVoIPPiPView(Context context, View v) {
        if ((context instanceof LaunchActivity) && !ApplicationLoader.mainInterfacePaused) {
            VoIPFragment.show((Activity) context, this.currentAccount);
        } else if (context instanceof LaunchActivity) {
            Intent intent = new Intent(context, LaunchActivity.class);
            intent.setAction("voip");
            context.startActivity(intent);
        }
    }

    /* access modifiers changed from: private */
    public void finishInternal() {
        this.currentUserTextureView.renderer.release();
        this.callingUserTextureView.renderer.release();
        VoIPService service = VoIPService.getSharedInstance();
        if (service != null) {
            service.unregisterStateListener(this);
        }
        this.windowView.setVisibility(8);
        if (this.windowView.getParent() != null) {
            this.floatingView.getRelativePosition(this.point);
            float x = Math.min(1.0f, Math.max(0.0f, this.point[0]));
            ApplicationLoader.applicationContext.getSharedPreferences("voippipconfig", 0).edit().putFloat("relativeX", x).putFloat("relativeY", Math.min(1.0f, Math.max(0.0f, this.point[1]))).apply();
            try {
                this.windowManager.removeView(this.windowView);
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didEndCall);
    }

    public void onStateChanged(int state) {
        if (state == 11 || state == 17 || state == 4 || state == 10) {
            AndroidUtilities.runOnUIThread(VoIPPiPView$$ExternalSyntheticLambda3.INSTANCE, 200);
        }
        VoIPService service = VoIPService.getSharedInstance();
        if (service == null) {
            finish();
        } else if (state != 3 || service.isVideoAvailable()) {
            updateViewState();
        } else {
            finish();
        }
    }

    public void onSignalBarsCountChanged(int count) {
    }

    public void onAudioSettingsChanged() {
    }

    public void onMediaStateUpdated(int audioState, int videoState) {
        updateViewState();
    }

    public void onCameraSwitch(boolean isFrontFace) {
        updateViewState();
    }

    public void onVideoAvailableChange(boolean isAvailable) {
    }

    public void onScreenOnChange(boolean screenOn) {
        VoIPService service = VoIPService.getSharedInstance();
        if (service != null) {
            if (!screenOn && this.currentUserIsVideo) {
                service.setVideoState(false, 1);
            } else if (screenOn && service.getVideoState(false) == 1) {
                service.setVideoState(false, 2);
            }
        }
    }

    private void updateViewState() {
        boolean animated = this.floatingView.getMeasuredWidth() != 0;
        boolean callingUserWasVideo = this.callingUserIsVideo;
        VoIPService service = VoIPService.getSharedInstance();
        float f = 1.0f;
        if (service != null) {
            this.callingUserIsVideo = service.getRemoteVideoState() == 2;
            this.currentUserIsVideo = service.getVideoState(false) == 2 || service.getVideoState(false) == 1;
            this.currentUserTextureView.renderer.setMirror(service.isFrontFaceCamera());
            this.currentUserTextureView.setIsScreencast(service.isScreencast());
            this.currentUserTextureView.setScreenshareMiniProgress(1.0f, false);
        }
        if (!animated) {
            if (!this.callingUserIsVideo) {
                f = 0.0f;
            }
            this.progressToCameraMini = f;
        } else if (callingUserWasVideo != this.callingUserIsVideo) {
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
            VoIPService service = VoIPService.getSharedInstance();
            if (this.currentUserIsVideo) {
                service.setVideoState(false, 1);
            }
        }
    }

    public void onResume() {
        VoIPService service = VoIPService.getSharedInstance();
        if (service != null && service.getVideoState(false) == 1) {
            service.setVideoState(false, 2);
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.didEndCall) {
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
                    public void getOutline(View view, Outline outline) {
                        outline.setRoundRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight(), (1.0f / view.getScaleX()) * ((float) AndroidUtilities.dp(4.0f)));
                    }
                });
                setClipToOutline(true);
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
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
            VoIPPiPView.this.currentUserTextureView.setAlpha(Math.min(1.0f, 1.0f - VoIPPiPView.this.progressToCameraMini));
            super.dispatchDraw(canvas);
        }

        public boolean onTouchEvent(MotionEvent event) {
            if (VoIPPiPView.this.expandedAnimationInProgress || VoIPPiPView.switchingToPip || VoIPPiPView.instance == null) {
                return false;
            }
            AndroidUtilities.cancelRunOnUIThread(VoIPPiPView.this.collapseRunnable);
            float x = event.getRawX();
            float y = event.getRawY();
            ViewParent parent = getParent();
            switch (event.getAction()) {
                case 0:
                    VoIPPiPView.this.startX = x;
                    VoIPPiPView.this.startY = y;
                    VoIPPiPView.this.startTime = System.currentTimeMillis();
                    if (VoIPPiPView.this.moveToBoundsAnimator != null) {
                        VoIPPiPView.this.moveToBoundsAnimator.cancel();
                        break;
                    }
                    break;
                case 1:
                case 3:
                    if (VoIPPiPView.this.moveToBoundsAnimator != null) {
                        VoIPPiPView.this.moveToBoundsAnimator.cancel();
                    }
                    if (event.getAction() != 1 || VoIPPiPView.this.moving || System.currentTimeMillis() - VoIPPiPView.this.startTime >= 150) {
                        if (parent != null) {
                            parent.requestDisallowInterceptTouchEvent(false);
                            int parentWidth = AndroidUtilities.displaySize.x;
                            int parentHeight = AndroidUtilities.displaySize.y + VoIPPiPView.topInset;
                            float maxTop = this.topPadding;
                            float maxBottom = this.bottomPadding;
                            float left = (float) (VoIPPiPView.this.windowLayoutParams.x + VoIPPiPView.this.floatingView.getLeft());
                            float right = ((float) VoIPPiPView.this.floatingView.getMeasuredWidth()) + left;
                            float top = (float) (VoIPPiPView.this.windowLayoutParams.y + VoIPPiPView.this.floatingView.getTop());
                            float bottom = ((float) VoIPPiPView.this.floatingView.getMeasuredHeight()) + top;
                            VoIPPiPView.this.moveToBoundsAnimator = new AnimatorSet();
                            if (left < this.leftPadding) {
                                ValueAnimator animator = ValueAnimator.ofFloat(new float[]{(float) VoIPPiPView.this.windowLayoutParams.x, this.leftPadding - ((float) VoIPPiPView.this.floatingView.getLeft())});
                                animator.addUpdateListener(VoIPPiPView.this.updateXlistener);
                                VoIPPiPView.this.moveToBoundsAnimator.playTogether(new Animator[]{animator});
                            } else if (right > ((float) parentWidth) - this.rightPadding) {
                                ValueAnimator animator2 = ValueAnimator.ofFloat(new float[]{(float) VoIPPiPView.this.windowLayoutParams.x, ((float) (parentWidth - VoIPPiPView.this.floatingView.getRight())) - this.rightPadding});
                                animator2.addUpdateListener(VoIPPiPView.this.updateXlistener);
                                VoIPPiPView.this.moveToBoundsAnimator.playTogether(new Animator[]{animator2});
                            }
                            if (top < maxTop) {
                                ValueAnimator animator3 = ValueAnimator.ofFloat(new float[]{(float) VoIPPiPView.this.windowLayoutParams.y, maxTop - ((float) VoIPPiPView.this.floatingView.getTop())});
                                animator3.addUpdateListener(VoIPPiPView.this.updateYlistener);
                                VoIPPiPView.this.moveToBoundsAnimator.playTogether(new Animator[]{animator3});
                            } else if (bottom > ((float) parentHeight) - maxBottom) {
                                ValueAnimator animator4 = ValueAnimator.ofFloat(new float[]{(float) VoIPPiPView.this.windowLayoutParams.y, ((float) (parentHeight - VoIPPiPView.this.floatingView.getMeasuredHeight())) - maxBottom});
                                animator4.addUpdateListener(VoIPPiPView.this.updateYlistener);
                                VoIPPiPView.this.moveToBoundsAnimator.playTogether(new Animator[]{animator4});
                            }
                            VoIPPiPView.this.moveToBoundsAnimator.setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT);
                            VoIPPiPView.this.moveToBoundsAnimator.start();
                        }
                        VoIPPiPView.this.moving = false;
                        if (VoIPPiPView.instance.expanded) {
                            AndroidUtilities.runOnUIThread(VoIPPiPView.this.collapseRunnable, 3000);
                            break;
                        }
                    } else {
                        Context context = getContext();
                        if ((context instanceof LaunchActivity) && !ApplicationLoader.mainInterfacePaused) {
                            VoIPFragment.show((Activity) context, VoIPPiPView.this.currentAccount);
                        } else if (context instanceof LaunchActivity) {
                            Intent intent = new Intent(context, LaunchActivity.class);
                            intent.setAction("voip");
                            context.startActivity(intent);
                        }
                        VoIPPiPView.this.moving = false;
                        return false;
                    }
                    break;
                case 2:
                    float dx = x - VoIPPiPView.this.startX;
                    float dy = y - VoIPPiPView.this.startY;
                    if (!VoIPPiPView.this.moving) {
                        float f = (dx * dx) + (dy * dy);
                        float f2 = this.touchSlop;
                        if (f > f2 * f2) {
                            if (parent != null) {
                                parent.requestDisallowInterceptTouchEvent(true);
                            }
                            VoIPPiPView.this.moving = true;
                            VoIPPiPView.this.startX = x;
                            VoIPPiPView.this.startY = y;
                            dx = 0.0f;
                            dy = 0.0f;
                        }
                    }
                    if (VoIPPiPView.this.moving) {
                        WindowManager.LayoutParams layoutParams = VoIPPiPView.this.windowLayoutParams;
                        layoutParams.x = (int) (((float) layoutParams.x) + dx);
                        WindowManager.LayoutParams layoutParams2 = VoIPPiPView.this.windowLayoutParams;
                        layoutParams2.y = (int) (((float) layoutParams2.y) + dy);
                        VoIPPiPView.this.startX = x;
                        VoIPPiPView.this.startY = y;
                        if (VoIPPiPView.this.windowView.getParent() != null) {
                            VoIPPiPView.this.windowManager.updateViewLayout(VoIPPiPView.this.windowView, VoIPPiPView.this.windowLayoutParams);
                            break;
                        }
                    }
                    break;
            }
            return true;
        }

        /* access modifiers changed from: private */
        public void getRelativePosition(float[] point) {
            float f = this.leftPadding;
            point[0] = (((float) (VoIPPiPView.this.windowLayoutParams.x + VoIPPiPView.this.floatingView.getLeft())) - f) / (((((float) AndroidUtilities.displaySize.x) - f) - this.rightPadding) - ((float) VoIPPiPView.this.floatingView.getMeasuredWidth()));
            float f2 = this.topPadding;
            point[1] = (((float) (VoIPPiPView.this.windowLayoutParams.y + VoIPPiPView.this.floatingView.getTop())) - f2) / (((((float) AndroidUtilities.displaySize.y) - f2) - this.bottomPadding) - ((float) VoIPPiPView.this.floatingView.getMeasuredHeight()));
            point[0] = Math.min(1.0f, Math.max(0.0f, point[0]));
            point[1] = Math.min(1.0f, Math.max(0.0f, point[1]));
        }

        /* access modifiers changed from: private */
        public void expand(final boolean expanded) {
            AndroidUtilities.cancelRunOnUIThread(VoIPPiPView.this.collapseRunnable);
            if (VoIPPiPView.instance != null && !VoIPPiPView.this.expandedAnimationInProgress && VoIPPiPView.instance.expanded != expanded) {
                VoIPPiPView.instance.expanded = expanded;
                float widthNormal = (((float) VoIPPiPView.this.parentWidth) * 0.25f) + ((float) (VoIPPiPView.this.xOffset * 2));
                float heightNormal = (((float) VoIPPiPView.this.parentHeight) * 0.25f) + ((float) (VoIPPiPView.this.yOffset * 2));
                float widthExpanded = (((float) VoIPPiPView.this.parentWidth) * 0.4f) + ((float) (VoIPPiPView.this.xOffset * 2));
                float heightExpanded = (((float) VoIPPiPView.this.parentHeight) * 0.4f) + ((float) (VoIPPiPView.this.yOffset * 2));
                boolean unused = VoIPPiPView.this.expandedAnimationInProgress = true;
                if (expanded) {
                    WindowManager.LayoutParams layoutParams = VoIPPiPView.createWindowLayoutParams(VoIPPiPView.instance.windowView.getContext(), VoIPPiPView.this.parentWidth, VoIPPiPView.this.parentHeight, 0.4f);
                    VoIPPiPView pipViewExpanded = new VoIPPiPView(getContext(), VoIPPiPView.this.parentWidth, VoIPPiPView.this.parentHeight, true);
                    getRelativePosition(VoIPPiPView.this.point);
                    float cX = VoIPPiPView.this.point[0];
                    float cY = VoIPPiPView.this.point[1];
                    layoutParams.x = (int) (((float) VoIPPiPView.this.windowLayoutParams.x) - ((widthExpanded - widthNormal) * cX));
                    layoutParams.y = (int) (((float) VoIPPiPView.this.windowLayoutParams.y) - ((heightExpanded - heightNormal) * cY));
                    VoIPPiPView.this.windowManager.addView(pipViewExpanded.windowView, layoutParams);
                    pipViewExpanded.windowView.setAlpha(1.0f);
                    pipViewExpanded.windowLayoutParams = layoutParams;
                    WindowManager unused2 = pipViewExpanded.windowManager = VoIPPiPView.this.windowManager;
                    VoIPPiPView unused3 = VoIPPiPView.expandedInstance = pipViewExpanded;
                    swapRender(VoIPPiPView.instance, VoIPPiPView.expandedInstance);
                    float scale = VoIPPiPView.this.floatingView.getScaleX() * 0.625f;
                    pipViewExpanded.floatingView.setPivotX(((float) VoIPPiPView.this.parentWidth) * cX * 0.4f);
                    pipViewExpanded.floatingView.setPivotY(((float) VoIPPiPView.this.parentHeight) * cY * 0.4f);
                    pipViewExpanded.floatingView.setScaleX(scale);
                    pipViewExpanded.floatingView.setScaleY(scale);
                    VoIPPiPView.expandedInstance.topShadow.setAlpha(0.0f);
                    VoIPPiPView.expandedInstance.closeIcon.setAlpha(0.0f);
                    VoIPPiPView.expandedInstance.enlargeIcon.setAlpha(0.0f);
                    AndroidUtilities.runOnUIThread(new VoIPPiPView$FloatingView$$ExternalSyntheticLambda2(this, scale, pipViewExpanded), 64);
                } else if (VoIPPiPView.expandedInstance != null) {
                    VoIPPiPView.expandedInstance.floatingView.getRelativePosition(VoIPPiPView.this.point);
                    float cX2 = VoIPPiPView.this.point[0];
                    float cY2 = VoIPPiPView.this.point[1];
                    VoIPPiPView.instance.windowLayoutParams.x = (int) (((float) VoIPPiPView.expandedInstance.windowLayoutParams.x) + ((widthExpanded - widthNormal) * cX2));
                    VoIPPiPView.instance.windowLayoutParams.y = (int) (((float) VoIPPiPView.expandedInstance.windowLayoutParams.y) + ((heightExpanded - heightNormal) * cY2));
                    VoIPPiPView.expandedInstance.floatingView.setPivotX(((float) VoIPPiPView.this.parentWidth) * cX2 * 0.4f);
                    VoIPPiPView.expandedInstance.floatingView.setPivotY(((float) VoIPPiPView.this.parentHeight) * cY2 * 0.4f);
                    showUi(false);
                    ValueAnimator valueAnimator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                    valueAnimator.addUpdateListener(new VoIPPiPView$FloatingView$$ExternalSyntheticLambda0(VoIPPiPView.this.floatingView.getScaleX() * 0.625f));
                    valueAnimator.setDuration(300).setInterpolator(CubicBezierInterpolator.DEFAULT);
                    valueAnimator.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            if (VoIPPiPView.expandedInstance != null) {
                                FloatingView.this.swapRender(VoIPPiPView.expandedInstance, VoIPPiPView.instance);
                                VoIPPiPView.instance.windowView.setAlpha(1.0f);
                                VoIPPiPView.this.windowManager.addView(VoIPPiPView.instance.windowView, VoIPPiPView.instance.windowLayoutParams);
                                AndroidUtilities.runOnUIThread(new VoIPPiPView$FloatingView$3$$ExternalSyntheticLambda0(this, expanded), 64);
                            }
                        }

                        /* renamed from: lambda$onAnimationEnd$0$org-telegram-ui-Components-voip-VoIPPiPView$FloatingView$3  reason: not valid java name */
                        public /* synthetic */ void m2771xbeabb793(boolean expanded) {
                            if (VoIPPiPView.instance != null && VoIPPiPView.expandedInstance != null) {
                                VoIPPiPView.expandedInstance.windowView.setAlpha(0.0f);
                                VoIPPiPView.expandedInstance.finishInternal();
                                boolean unused = VoIPPiPView.this.expandedAnimationInProgress = false;
                                if (expanded) {
                                    AndroidUtilities.runOnUIThread(VoIPPiPView.this.collapseRunnable, 3000);
                                }
                            }
                        }
                    });
                    valueAnimator.start();
                    VoIPPiPView.this.expandAnimator = valueAnimator;
                }
            }
        }

        /* renamed from: lambda$expand$1$org-telegram-ui-Components-voip-VoIPPiPView$FloatingView  reason: not valid java name */
        public /* synthetic */ void m2770x8bCLASSNAMEf(float scale, VoIPPiPView pipViewExpanded) {
            if (VoIPPiPView.expandedInstance != null) {
                VoIPPiPView.this.windowView.setAlpha(0.0f);
                try {
                    VoIPPiPView.this.windowManager.removeView(VoIPPiPView.this.windowView);
                } catch (Throwable e) {
                    FileLog.e(e);
                }
                animate().cancel();
                showUi(true);
                ValueAnimator valueAnimator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                valueAnimator.addUpdateListener(new VoIPPiPView$FloatingView$$ExternalSyntheticLambda1(scale, 1.0f, pipViewExpanded));
                valueAnimator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        boolean unused = VoIPPiPView.this.expandedAnimationInProgress = false;
                    }
                });
                valueAnimator.setDuration(300).setInterpolator(CubicBezierInterpolator.DEFAULT);
                valueAnimator.start();
                VoIPPiPView.this.expandAnimator = valueAnimator;
            }
        }

        static /* synthetic */ void lambda$expand$0(float scale, float animateToScale, VoIPPiPView pipViewExpanded, ValueAnimator a) {
            float v = ((Float) a.getAnimatedValue()).floatValue();
            float sc = ((1.0f - v) * scale) + (animateToScale * v);
            pipViewExpanded.floatingView.setScaleX(sc);
            pipViewExpanded.floatingView.setScaleY(sc);
            pipViewExpanded.floatingView.invalidate();
            pipViewExpanded.windowView.invalidate();
            if (Build.VERSION.SDK_INT >= 21) {
                pipViewExpanded.floatingView.invalidateOutline();
            }
        }

        static /* synthetic */ void lambda$expand$2(float scale, ValueAnimator a) {
            float v = ((Float) a.getAnimatedValue()).floatValue();
            float sc = (1.0f - v) + (scale * v);
            if (VoIPPiPView.expandedInstance != null) {
                VoIPPiPView.expandedInstance.floatingView.setScaleX(sc);
                VoIPPiPView.expandedInstance.floatingView.setScaleY(sc);
                VoIPPiPView.expandedInstance.floatingView.invalidate();
                if (Build.VERSION.SDK_INT >= 21) {
                    VoIPPiPView.expandedInstance.floatingView.invalidateOutline();
                }
                VoIPPiPView.expandedInstance.windowView.invalidate();
            }
        }

        private void showUi(boolean show) {
            if (VoIPPiPView.expandedInstance != null) {
                float f = 0.0f;
                if (show) {
                    VoIPPiPView.expandedInstance.topShadow.setAlpha(0.0f);
                    VoIPPiPView.expandedInstance.closeIcon.setAlpha(0.0f);
                    VoIPPiPView.expandedInstance.enlargeIcon.setAlpha(0.0f);
                }
                VoIPPiPView.expandedInstance.topShadow.animate().alpha(show ? 1.0f : 0.0f).setDuration(300).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
                VoIPPiPView.expandedInstance.closeIcon.animate().alpha(show ? 1.0f : 0.0f).setDuration(300).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
                ViewPropertyAnimator animate = VoIPPiPView.expandedInstance.enlargeIcon.animate();
                if (show) {
                    f = 1.0f;
                }
                animate.alpha(f).setDuration(300).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            }
        }

        /* access modifiers changed from: private */
        public void swapRender(VoIPPiPView from, VoIPPiPView to) {
            to.currentUserTextureView.setStub(from.currentUserTextureView);
            to.callingUserTextureView.setStub(from.callingUserTextureView);
            from.currentUserTextureView.renderer.release();
            from.callingUserTextureView.renderer.release();
            if (VideoCapturerDevice.eglBase != null) {
                to.currentUserTextureView.renderer.init(VideoCapturerDevice.eglBase.getEglBaseContext(), (RendererCommon.RendererEvents) null);
                to.callingUserTextureView.renderer.init(VideoCapturerDevice.eglBase.getEglBaseContext(), (RendererCommon.RendererEvents) null);
                if (VoIPService.getSharedInstance() != null) {
                    VoIPService.getSharedInstance().setSinks(to.currentUserTextureView.renderer, to.callingUserTextureView.renderer);
                }
            }
        }
    }
}
