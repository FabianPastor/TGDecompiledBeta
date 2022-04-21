package org.telegram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Insets;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Property;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.view.NestedScrollingParent;
import androidx.core.view.NestedScrollingParentHelper;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.camera.CameraView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;

public class BottomSheet extends Dialog {
    private static final boolean AVOID_SYSTEM_CUTOUT_FULLSCREEN = false;
    /* access modifiers changed from: private */
    public boolean allowCustomAnimation;
    /* access modifiers changed from: private */
    public boolean allowDrawContent;
    protected boolean allowNestedScroll;
    /* access modifiers changed from: private */
    public boolean applyBottomPadding;
    /* access modifiers changed from: private */
    public boolean applyTopPadding;
    /* access modifiers changed from: protected */
    public ColorDrawable backDrawable;
    /* access modifiers changed from: protected */
    public int backgroundPaddingLeft;
    /* access modifiers changed from: protected */
    public int backgroundPaddingTop;
    protected int behindKeyboardColor;
    protected String behindKeyboardColorKey;
    /* access modifiers changed from: private */
    public boolean bigTitle;
    /* access modifiers changed from: private */
    public int bottomInset;
    protected boolean calcMandatoryInsets;
    private boolean canDismissWithSwipe;
    /* access modifiers changed from: protected */
    public ContainerView container;
    /* access modifiers changed from: protected */
    public ViewGroup containerView;
    /* access modifiers changed from: protected */
    public int currentAccount;
    /* access modifiers changed from: private */
    public float currentPanTranslationY;
    protected AnimatorSet currentSheetAnimation;
    protected int currentSheetAnimationType;
    /* access modifiers changed from: private */
    public View customView;
    protected BottomSheetDelegateInterface delegate;
    protected boolean dimBehind;
    protected int dimBehindAlpha;
    /* access modifiers changed from: private */
    public boolean disableScroll;
    /* access modifiers changed from: private */
    public Runnable dismissRunnable;
    /* access modifiers changed from: private */
    public boolean dismissed;
    public boolean drawNavigationBar;
    private boolean focusable;
    private boolean fullHeight;
    protected boolean fullWidth;
    /* access modifiers changed from: private */
    public float hideSystemVerticalInsetsProgress;
    /* access modifiers changed from: protected */
    public boolean isFullscreen;
    /* access modifiers changed from: private */
    public int[] itemIcons;
    private ArrayList<BottomSheetCell> itemViews;
    /* access modifiers changed from: private */
    public CharSequence[] items;
    /* access modifiers changed from: private */
    public ValueAnimator keyboardContentAnimator;
    /* access modifiers changed from: protected */
    public boolean keyboardVisible;
    /* access modifiers changed from: private */
    public WindowInsets lastInsets;
    /* access modifiers changed from: private */
    public int layoutCount;
    /* access modifiers changed from: private */
    public int leftInset;
    /* access modifiers changed from: protected */
    public int navBarColor;
    protected String navBarColorKey;
    protected View nestedScrollChild;
    /* access modifiers changed from: private */
    public DialogInterface.OnClickListener onClickListener;
    /* access modifiers changed from: private */
    public DialogInterface.OnDismissListener onHideListener;
    protected Interpolator openInterpolator;
    private boolean openNoDelay;
    /* access modifiers changed from: private */
    public int overlayDrawNavBarColor;
    protected Theme.ResourcesProvider resourcesProvider;
    /* access modifiers changed from: private */
    public int rightInset;
    public boolean scrollNavBar;
    protected Drawable shadowDrawable;
    private boolean showWithoutAnimation;
    protected boolean smoothKeyboardAnimationEnabled;
    protected Runnable startAnimationRunnable;
    private int statusBarHeight;
    /* access modifiers changed from: private */
    public int tag;
    /* access modifiers changed from: private */
    public CharSequence title;
    private TextView titleView;
    /* access modifiers changed from: private */
    public int touchSlop;
    /* access modifiers changed from: private */
    public boolean useFastDismiss;
    protected boolean useHardwareLayer;
    protected boolean useLightNavBar;
    protected boolean useLightStatusBar;
    protected boolean useSmoothKeyboard;

    public interface BottomSheetDelegateInterface {
        boolean canDismiss();

        void onOpenAnimationEnd();

        void onOpenAnimationStart();
    }

    static /* synthetic */ int access$1210(BottomSheet x0) {
        int i = x0.layoutCount;
        x0.layoutCount = i - 1;
        return i;
    }

    static /* synthetic */ int access$712(BottomSheet x0, int x1) {
        int i = x0.bottomInset + x1;
        x0.bottomInset = i;
        return i;
    }

    static /* synthetic */ int access$720(BottomSheet x0, int x1) {
        int i = x0.bottomInset - x1;
        x0.bottomInset = i;
        return i;
    }

    public void setDisableScroll(boolean b) {
        this.disableScroll = b;
    }

    protected class ContainerView extends FrameLayout implements NestedScrollingParent {
        private Paint backgroundPaint = new Paint();
        /* access modifiers changed from: private */
        public AnimatorSet currentAnimation = null;
        private boolean keyboardChanged;
        /* access modifiers changed from: private */
        public int keyboardHeight;
        private boolean maybeStartTracking = false;
        private NestedScrollingParentHelper nestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        private Rect rect = new Rect();
        private boolean startedTracking = false;
        private int startedTrackingPointerId = -1;
        private int startedTrackingX;
        private int startedTrackingY;
        private VelocityTracker velocityTracker = null;
        private float y = 0.0f;

        public ContainerView(Context context) {
            super(context);
            setWillNotDraw(false);
        }

        public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
            return (BottomSheet.this.nestedScrollChild == null || child == BottomSheet.this.nestedScrollChild) && !BottomSheet.this.dismissed && BottomSheet.this.allowNestedScroll && nestedScrollAxes == 2 && !BottomSheet.this.canDismissWithSwipe();
        }

        public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {
            this.nestedScrollingParentHelper.onNestedScrollAccepted(child, target, nestedScrollAxes);
            if (!BottomSheet.this.dismissed && BottomSheet.this.allowNestedScroll) {
                cancelCurrentAnimation();
            }
        }

        public void onStopNestedScroll(View target) {
            this.nestedScrollingParentHelper.onStopNestedScroll(target);
            if (!BottomSheet.this.dismissed && BottomSheet.this.allowNestedScroll) {
                float translationY = BottomSheet.this.containerView.getTranslationY();
                checkDismiss(0.0f, 0.0f);
            }
        }

        public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
            if (!BottomSheet.this.dismissed && BottomSheet.this.allowNestedScroll) {
                cancelCurrentAnimation();
                if (dyUnconsumed != 0) {
                    float currentTranslation = BottomSheet.this.containerView.getTranslationY() - ((float) dyUnconsumed);
                    if (currentTranslation < 0.0f) {
                        currentTranslation = 0.0f;
                    }
                    BottomSheet.this.containerView.setTranslationY(currentTranslation);
                    BottomSheet.this.container.invalidate();
                }
            }
        }

        public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
            if (!BottomSheet.this.dismissed && BottomSheet.this.allowNestedScroll) {
                cancelCurrentAnimation();
                float currentTranslation = BottomSheet.this.containerView.getTranslationY();
                if (currentTranslation > 0.0f && dy > 0) {
                    float currentTranslation2 = currentTranslation - ((float) dy);
                    consumed[1] = dy;
                    if (currentTranslation2 < 0.0f) {
                        currentTranslation2 = 0.0f;
                    }
                    BottomSheet.this.containerView.setTranslationY(currentTranslation2);
                    BottomSheet.this.container.invalidate();
                }
            }
        }

        public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
            return false;
        }

        public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
            return false;
        }

        public int getNestedScrollAxes() {
            return this.nestedScrollingParentHelper.getNestedScrollAxes();
        }

        private void checkDismiss(float velX, float velY) {
            float translationY = BottomSheet.this.containerView.getTranslationY();
            if (!((translationY < AndroidUtilities.getPixelsInCM(0.8f, false) && (velY < 3500.0f || Math.abs(velY) < Math.abs(velX))) || (velY < 0.0f && Math.abs(velY) >= 3500.0f))) {
                boolean allowOld = BottomSheet.this.allowCustomAnimation;
                boolean unused = BottomSheet.this.allowCustomAnimation = false;
                boolean unused2 = BottomSheet.this.useFastDismiss = true;
                BottomSheet.this.dismiss();
                boolean unused3 = BottomSheet.this.allowCustomAnimation = allowOld;
                return;
            }
            AnimatorSet animatorSet = new AnimatorSet();
            this.currentAnimation = animatorSet;
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(BottomSheet.this.containerView, "translationY", new float[]{0.0f})});
            this.currentAnimation.setDuration((long) ((int) ((Math.max(0.0f, translationY) / AndroidUtilities.getPixelsInCM(0.8f, false)) * 150.0f)));
            this.currentAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (ContainerView.this.currentAnimation != null && ContainerView.this.currentAnimation.equals(animation)) {
                        AnimatorSet unused = ContainerView.this.currentAnimation = null;
                    }
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 512);
                }
            });
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 512);
            this.currentAnimation.start();
        }

        private void cancelCurrentAnimation() {
            AnimatorSet animatorSet = this.currentAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.currentAnimation = null;
            }
        }

        public boolean processTouchEvent(MotionEvent ev, boolean intercept) {
            if (BottomSheet.this.dismissed) {
                return false;
            }
            if (BottomSheet.this.onContainerTouchEvent(ev)) {
                return true;
            }
            if (BottomSheet.this.canDismissWithTouchOutside() && ev != null && ((ev.getAction() == 0 || ev.getAction() == 2) && !this.startedTracking && !this.maybeStartTracking && ev.getPointerCount() == 1)) {
                this.startedTrackingX = (int) ev.getX();
                int y2 = (int) ev.getY();
                this.startedTrackingY = y2;
                if (y2 < BottomSheet.this.containerView.getTop() || this.startedTrackingX < BottomSheet.this.containerView.getLeft() || this.startedTrackingX > BottomSheet.this.containerView.getRight()) {
                    BottomSheet.this.dismiss();
                    return true;
                }
                BottomSheet.this.onScrollUpBegin(this.y);
                this.startedTrackingPointerId = ev.getPointerId(0);
                this.maybeStartTracking = true;
                cancelCurrentAnimation();
                VelocityTracker velocityTracker2 = this.velocityTracker;
                if (velocityTracker2 != null) {
                    velocityTracker2.clear();
                }
            } else if (ev != null && ev.getAction() == 2 && ev.getPointerId(0) == this.startedTrackingPointerId) {
                if (this.velocityTracker == null) {
                    this.velocityTracker = VelocityTracker.obtain();
                }
                float dx = (float) Math.abs((int) (ev.getX() - ((float) this.startedTrackingX)));
                float dy = (float) (((int) ev.getY()) - this.startedTrackingY);
                boolean canScrollUp = BottomSheet.this.onScrollUp(this.y + dy);
                this.velocityTracker.addMovement(ev);
                if (!BottomSheet.this.disableScroll && this.maybeStartTracking && !this.startedTracking && dy > 0.0f && dy / 3.0f > Math.abs(dx) && Math.abs(dy) >= ((float) BottomSheet.this.touchSlop)) {
                    this.startedTrackingY = (int) ev.getY();
                    this.maybeStartTracking = false;
                    this.startedTracking = true;
                    requestDisallowInterceptTouchEvent(true);
                } else if (this.startedTracking) {
                    float f = this.y + dy;
                    this.y = f;
                    if (!canScrollUp) {
                        this.y = Math.max(f, 0.0f);
                    }
                    BottomSheet.this.containerView.setTranslationY(Math.max(this.y, 0.0f));
                    this.startedTrackingY = (int) ev.getY();
                    BottomSheet.this.container.invalidate();
                }
            } else if (ev == null || (ev.getPointerId(0) == this.startedTrackingPointerId && (ev.getAction() == 3 || ev.getAction() == 1 || ev.getAction() == 6))) {
                if (this.velocityTracker == null) {
                    this.velocityTracker = VelocityTracker.obtain();
                }
                this.velocityTracker.computeCurrentVelocity(1000);
                BottomSheet.this.onScrollUpEnd(this.y);
                if (this.startedTracking || this.y > 0.0f) {
                    checkDismiss(this.velocityTracker.getXVelocity(), this.velocityTracker.getYVelocity());
                } else {
                    this.maybeStartTracking = false;
                }
                this.startedTracking = false;
                VelocityTracker velocityTracker3 = this.velocityTracker;
                if (velocityTracker3 != null) {
                    velocityTracker3.recycle();
                    this.velocityTracker = null;
                }
                this.startedTrackingPointerId = -1;
            }
            if ((intercept || !this.maybeStartTracking) && !this.startedTracking && BottomSheet.this.canDismissWithSwipe()) {
                return false;
            }
            return true;
        }

        public boolean onTouchEvent(MotionEvent ev) {
            return processTouchEvent(ev, false);
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Removed duplicated region for block: B:42:0x0106  */
        /* JADX WARNING: Removed duplicated region for block: B:46:0x0129  */
        /* JADX WARNING: Removed duplicated region for block: B:51:0x0136  */
        /* JADX WARNING: Removed duplicated region for block: B:52:0x0146  */
        /* JADX WARNING: Removed duplicated region for block: B:54:0x0149  */
        /* JADX WARNING: Removed duplicated region for block: B:57:0x0152  */
        /* JADX WARNING: Removed duplicated region for block: B:70:0x01d1  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onMeasure(int r19, int r20) {
            /*
                r18 = this;
                r6 = r18
                int r0 = android.view.View.MeasureSpec.getSize(r19)
                int r1 = android.view.View.MeasureSpec.getSize(r20)
                r2 = r1
                android.view.View r7 = r18.getRootView()
                android.graphics.Rect r3 = r6.rect
                r6.getWindowVisibleDisplayFrame(r3)
                int r8 = r6.keyboardHeight
                android.graphics.Rect r3 = r6.rect
                int r3 = r3.bottom
                r4 = 1101004800(0x41a00000, float:20.0)
                r5 = 1065353216(0x3var_, float:1.0)
                r9 = 0
                if (r3 == 0) goto L_0x0074
                android.graphics.Rect r3 = r6.rect
                int r3 = r3.top
                if (r3 == 0) goto L_0x0074
                int r3 = r7.getHeight()
                float r3 = (float) r3
                android.graphics.Rect r10 = r6.rect
                int r10 = r10.top
                if (r10 == 0) goto L_0x0040
                int r10 = org.telegram.messenger.AndroidUtilities.statusBarHeight
                float r10 = (float) r10
                org.telegram.ui.ActionBar.BottomSheet r11 = org.telegram.ui.ActionBar.BottomSheet.this
                float r11 = r11.hideSystemVerticalInsetsProgress
                float r11 = r5 - r11
                float r10 = r10 * r11
                goto L_0x0041
            L_0x0040:
                r10 = 0
            L_0x0041:
                float r3 = r3 - r10
                int r10 = org.telegram.messenger.AndroidUtilities.getViewInset(r7)
                float r10 = (float) r10
                org.telegram.ui.ActionBar.BottomSheet r11 = org.telegram.ui.ActionBar.BottomSheet.this
                float r11 = r11.hideSystemVerticalInsetsProgress
                float r11 = r5 - r11
                float r10 = r10 * r11
                float r3 = r3 - r10
                int r3 = (int) r3
                android.graphics.Rect r10 = r6.rect
                int r10 = r10.bottom
                android.graphics.Rect r11 = r6.rect
                int r11 = r11.top
                int r10 = r10 - r11
                int r10 = r3 - r10
                int r10 = java.lang.Math.max(r9, r10)
                r6.keyboardHeight = r10
                int r11 = org.telegram.messenger.AndroidUtilities.dp(r4)
                if (r10 >= r11) goto L_0x006c
                r6.keyboardHeight = r9
            L_0x006c:
                org.telegram.ui.ActionBar.BottomSheet r10 = org.telegram.ui.ActionBar.BottomSheet.this
                int r11 = r6.keyboardHeight
                org.telegram.ui.ActionBar.BottomSheet.access$720(r10, r11)
                goto L_0x0076
            L_0x0074:
                r6.keyboardHeight = r9
            L_0x0076:
                int r3 = r6.keyboardHeight
                r10 = 1
                if (r8 == r3) goto L_0x007d
                r6.keyboardChanged = r10
            L_0x007d:
                org.telegram.ui.ActionBar.BottomSheet r11 = org.telegram.ui.ActionBar.BottomSheet.this
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
                if (r3 <= r4) goto L_0x0087
                r3 = 1
                goto L_0x0088
            L_0x0087:
                r3 = 0
            L_0x0088:
                r11.keyboardVisible = r3
                org.telegram.ui.ActionBar.BottomSheet r3 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.WindowInsets r3 = r3.lastInsets
                r4 = 29
                r11 = 21
                if (r3 == 0) goto L_0x00f6
                int r3 = android.os.Build.VERSION.SDK_INT
                if (r3 < r11) goto L_0x00f6
                org.telegram.ui.ActionBar.BottomSheet r3 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.WindowInsets r12 = r3.lastInsets
                int r12 = r12.getSystemWindowInsetBottom()
                int unused = r3.bottomInset = r12
                org.telegram.ui.ActionBar.BottomSheet r3 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.WindowInsets r12 = r3.lastInsets
                int r12 = r12.getSystemWindowInsetLeft()
                int unused = r3.leftInset = r12
                org.telegram.ui.ActionBar.BottomSheet r3 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.WindowInsets r12 = r3.lastInsets
                int r12 = r12.getSystemWindowInsetRight()
                int unused = r3.rightInset = r12
                int r3 = android.os.Build.VERSION.SDK_INT
                if (r3 < r4) goto L_0x00ce
                org.telegram.ui.ActionBar.BottomSheet r3 = org.telegram.ui.ActionBar.BottomSheet.this
                int r12 = r3.getAdditionalMandatoryOffsets()
                org.telegram.ui.ActionBar.BottomSheet.access$712(r3, r12)
            L_0x00ce:
                org.telegram.ui.ActionBar.BottomSheet r3 = org.telegram.ui.ActionBar.BottomSheet.this
                boolean r3 = r3.keyboardVisible
                if (r3 == 0) goto L_0x00e7
                android.graphics.Rect r3 = r6.rect
                int r3 = r3.bottom
                if (r3 == 0) goto L_0x00e7
                android.graphics.Rect r3 = r6.rect
                int r3 = r3.top
                if (r3 == 0) goto L_0x00e7
                org.telegram.ui.ActionBar.BottomSheet r3 = org.telegram.ui.ActionBar.BottomSheet.this
                int r12 = r6.keyboardHeight
                org.telegram.ui.ActionBar.BottomSheet.access$720(r3, r12)
            L_0x00e7:
                org.telegram.ui.ActionBar.BottomSheet r3 = org.telegram.ui.ActionBar.BottomSheet.this
                boolean r3 = r3.drawNavigationBar
                if (r3 != 0) goto L_0x00f6
                org.telegram.ui.ActionBar.BottomSheet r3 = org.telegram.ui.ActionBar.BottomSheet.this
                int r3 = r3.getBottomInset()
                int r2 = r2 - r3
                r12 = r2
                goto L_0x00f7
            L_0x00f6:
                r12 = r2
            L_0x00f7:
                r6.setMeasuredDimension(r0, r12)
                org.telegram.ui.ActionBar.BottomSheet r2 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.WindowInsets r2 = r2.lastInsets
                if (r2 == 0) goto L_0x0129
                int r2 = android.os.Build.VERSION.SDK_INT
                if (r2 < r11) goto L_0x0129
                org.telegram.ui.ActionBar.BottomSheet r2 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.WindowInsets r2 = r2.lastInsets
                int r2 = r2.getSystemWindowInsetBottom()
                float r2 = (float) r2
                org.telegram.ui.ActionBar.BottomSheet r3 = org.telegram.ui.ActionBar.BottomSheet.this
                float r3 = r3.hideSystemVerticalInsetsProgress
                float r5 = r5 - r3
                float r2 = r2 * r5
                int r2 = (int) r2
                int r3 = android.os.Build.VERSION.SDK_INT
                if (r3 < r4) goto L_0x0126
                org.telegram.ui.ActionBar.BottomSheet r3 = org.telegram.ui.ActionBar.BottomSheet.this
                int r3 = r3.getAdditionalMandatoryOffsets()
                int r2 = r2 + r3
            L_0x0126:
                int r1 = r1 - r2
                r13 = r1
                goto L_0x012a
            L_0x0129:
                r13 = r1
            L_0x012a:
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.WindowInsets r1 = r1.lastInsets
                if (r1 == 0) goto L_0x0146
                int r1 = android.os.Build.VERSION.SDK_INT
                if (r1 < r11) goto L_0x0146
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                int r1 = r1.getRightInset()
                org.telegram.ui.ActionBar.BottomSheet r2 = org.telegram.ui.ActionBar.BottomSheet.this
                int r2 = r2.getLeftInset()
                int r1 = r1 + r2
                int r0 = r0 - r1
                r11 = r0
                goto L_0x0147
            L_0x0146:
                r11 = r0
            L_0x0147:
                if (r11 >= r13) goto L_0x014a
                r9 = 1
            L_0x014a:
                org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.ViewGroup r0 = r0.containerView
                r10 = 1073741824(0x40000000, float:2.0)
                if (r0 == 0) goto L_0x01c9
                org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.ActionBar.BottomSheet.this
                boolean r0 = r0.fullWidth
                r1 = -2147483648(0xfffffffvar_, float:-0.0)
                if (r0 != 0) goto L_0x01b3
                boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
                r2 = 1061997773(0x3f4ccccd, float:0.8)
                if (r0 == 0) goto L_0x017f
                android.graphics.Point r0 = org.telegram.messenger.AndroidUtilities.displaySize
                int r0 = r0.x
                android.graphics.Point r3 = org.telegram.messenger.AndroidUtilities.displaySize
                int r3 = r3.y
                int r0 = java.lang.Math.min(r0, r3)
                float r0 = (float) r0
                float r0 = r0 * r2
                int r0 = (int) r0
                org.telegram.ui.ActionBar.BottomSheet r2 = org.telegram.ui.ActionBar.BottomSheet.this
                int r2 = r2.backgroundPaddingLeft
                int r2 = r2 * 2
                int r0 = r0 + r2
                int r0 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r10)
                goto L_0x01a7
            L_0x017f:
                if (r9 == 0) goto L_0x0189
                org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.ActionBar.BottomSheet.this
                int r0 = r0.backgroundPaddingLeft
                int r0 = r0 * 2
                int r0 = r0 + r11
                goto L_0x01a3
            L_0x0189:
                float r0 = (float) r11
                float r0 = r0 * r2
                r2 = 1139802112(0x43var_, float:480.0)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r2 = java.lang.Math.min(r2, r11)
                float r2 = (float) r2
                float r0 = java.lang.Math.max(r0, r2)
                int r0 = (int) r0
                org.telegram.ui.ActionBar.BottomSheet r2 = org.telegram.ui.ActionBar.BottomSheet.this
                int r2 = r2.backgroundPaddingLeft
                int r2 = r2 * 2
                int r0 = r0 + r2
            L_0x01a3:
                int r0 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r10)
            L_0x01a7:
                org.telegram.ui.ActionBar.BottomSheet r2 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.ViewGroup r2 = r2.containerView
                int r1 = android.view.View.MeasureSpec.makeMeasureSpec(r13, r1)
                r2.measure(r0, r1)
                goto L_0x01c9
            L_0x01b3:
                org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.ViewGroup r0 = r0.containerView
                org.telegram.ui.ActionBar.BottomSheet r2 = org.telegram.ui.ActionBar.BottomSheet.this
                int r2 = r2.backgroundPaddingLeft
                int r2 = r2 * 2
                int r2 = r2 + r11
                int r2 = android.view.View.MeasureSpec.makeMeasureSpec(r2, r10)
                int r1 = android.view.View.MeasureSpec.makeMeasureSpec(r13, r1)
                r0.measure(r2, r1)
            L_0x01c9:
                int r14 = r18.getChildCount()
                r0 = 0
                r15 = r0
            L_0x01cf:
                if (r15 >= r14) goto L_0x020a
                android.view.View r5 = r6.getChildAt(r15)
                int r0 = r5.getVisibility()
                r1 = 8
                if (r0 == r1) goto L_0x0205
                org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.ViewGroup r0 = r0.containerView
                if (r5 != r0) goto L_0x01e4
                goto L_0x0207
            L_0x01e4:
                org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.ActionBar.BottomSheet.this
                boolean r0 = r0.onCustomMeasure(r5, r11, r13)
                if (r0 != 0) goto L_0x0202
                int r2 = android.view.View.MeasureSpec.makeMeasureSpec(r11, r10)
                r3 = 0
                int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r13, r10)
                r16 = 0
                r0 = r18
                r1 = r5
                r17 = r5
                r5 = r16
                r0.measureChildWithMargins(r1, r2, r3, r4, r5)
                goto L_0x0207
            L_0x0202:
                r17 = r5
                goto L_0x0207
            L_0x0205:
                r17 = r5
            L_0x0207:
                int r15 = r15 + 1
                goto L_0x01cf
            L_0x020a:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.BottomSheet.ContainerView.onMeasure(int, int):void");
        }

        public void requestLayout() {
            super.requestLayout();
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean changed, int left, int top, int right, int bottom) {
            int right2;
            int left2;
            int childLeft;
            int childTop;
            int right3;
            int left3;
            BottomSheet.access$1210(BottomSheet.this);
            int i = 2;
            if (BottomSheet.this.containerView != null) {
                int t = (bottom - top) - BottomSheet.this.containerView.getMeasuredHeight();
                if (BottomSheet.this.lastInsets == null || Build.VERSION.SDK_INT < 21) {
                    left3 = left;
                    right3 = right;
                } else {
                    left3 = left + BottomSheet.this.getLeftInset();
                    right3 = right - BottomSheet.this.getRightInset();
                    if (BottomSheet.this.useSmoothKeyboard) {
                        t = 0;
                    } else {
                        t = (int) (((float) t) - ((((float) BottomSheet.this.lastInsets.getSystemWindowInsetBottom()) * (1.0f - BottomSheet.this.hideSystemVerticalInsetsProgress)) - ((float) (BottomSheet.this.drawNavigationBar ? 0 : BottomSheet.this.getBottomInset()))));
                        if (Build.VERSION.SDK_INT >= 29) {
                            t -= BottomSheet.this.getAdditionalMandatoryOffsets();
                        }
                    }
                }
                int l = ((right3 - left3) - BottomSheet.this.containerView.getMeasuredWidth()) / 2;
                if (BottomSheet.this.lastInsets != null && Build.VERSION.SDK_INT >= 21) {
                    l += BottomSheet.this.getLeftInset();
                }
                if (BottomSheet.this.smoothKeyboardAnimationEnabled && BottomSheet.this.startAnimationRunnable == null && this.keyboardChanged && !BottomSheet.this.dismissed && BottomSheet.this.containerView.getTop() != t) {
                    BottomSheet.this.containerView.setTranslationY((float) (BottomSheet.this.containerView.getTop() - t));
                    if (BottomSheet.this.keyboardContentAnimator != null) {
                        BottomSheet.this.keyboardContentAnimator.cancel();
                    }
                    BottomSheet bottomSheet = BottomSheet.this;
                    ValueAnimator unused = bottomSheet.keyboardContentAnimator = ValueAnimator.ofFloat(new float[]{bottomSheet.containerView.getTranslationY(), 0.0f});
                    BottomSheet.this.keyboardContentAnimator.addUpdateListener(new BottomSheet$ContainerView$$ExternalSyntheticLambda0(this));
                    BottomSheet.this.keyboardContentAnimator.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            BottomSheet.this.containerView.setTranslationY(0.0f);
                            ContainerView.this.invalidate();
                        }
                    });
                    BottomSheet.this.keyboardContentAnimator.setDuration(250).setInterpolator(AdjustPanLayoutHelper.keyboardInterpolator);
                    BottomSheet.this.keyboardContentAnimator.start();
                }
                BottomSheet.this.containerView.layout(l, t, BottomSheet.this.containerView.getMeasuredWidth() + l, BottomSheet.this.containerView.getMeasuredHeight() + t);
                left2 = left3;
                right2 = right3;
            } else {
                left2 = left;
                right2 = right;
            }
            int count = getChildCount();
            int i2 = 0;
            while (i2 < count) {
                View child = getChildAt(i2);
                if (!(child.getVisibility() == 8 || child == BottomSheet.this.containerView)) {
                    BottomSheet bottomSheet2 = BottomSheet.this;
                    if (!bottomSheet2.onCustomLayout(child, left2, top, right2, bottom - (bottomSheet2.drawNavigationBar ? BottomSheet.this.getBottomInset() : 0))) {
                        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();
                        int width = child.getMeasuredWidth();
                        int height = child.getMeasuredHeight();
                        int gravity = lp.gravity;
                        if (gravity == -1) {
                            gravity = 51;
                        }
                        int verticalGravity = gravity & 112;
                        switch (gravity & 7 & 7) {
                            case 1:
                                childLeft = ((((right2 - left2) - width) / i) + lp.leftMargin) - lp.rightMargin;
                                break;
                            case 5:
                                childLeft = (right2 - width) - lp.rightMargin;
                                break;
                            default:
                                childLeft = lp.leftMargin;
                                break;
                        }
                        switch (verticalGravity) {
                            case 16:
                                childTop = ((((bottom - top) - height) / i) + lp.topMargin) - lp.bottomMargin;
                                break;
                            case 80:
                                childTop = ((bottom - top) - height) - lp.bottomMargin;
                                break;
                            default:
                                childTop = lp.topMargin;
                                break;
                        }
                        if (BottomSheet.this.lastInsets != null) {
                            if (Build.VERSION.SDK_INT >= 21) {
                                childLeft += BottomSheet.this.getLeftInset();
                            }
                        }
                        child.layout(childLeft, childTop, childLeft + width, childTop + height);
                    }
                }
                i2++;
                i = 2;
            }
            if (BottomSheet.this.layoutCount == 0 && BottomSheet.this.startAnimationRunnable != null) {
                AndroidUtilities.cancelRunOnUIThread(BottomSheet.this.startAnimationRunnable);
                BottomSheet.this.startAnimationRunnable.run();
                BottomSheet.this.startAnimationRunnable = null;
            }
            this.keyboardChanged = false;
        }

        /* renamed from: lambda$onLayout$0$org-telegram-ui-ActionBar-BottomSheet$ContainerView  reason: not valid java name */
        public /* synthetic */ void m1279xf4dvar_e(ValueAnimator valueAnimator) {
            BottomSheet.this.containerView.setTranslationY(((Float) valueAnimator.getAnimatedValue()).floatValue());
            invalidate();
        }

        public boolean onInterceptTouchEvent(MotionEvent event) {
            if (BottomSheet.this.canDismissWithSwipe()) {
                return processTouchEvent(event, true);
            }
            return super.onInterceptTouchEvent(event);
        }

        public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            if (this.maybeStartTracking && !this.startedTracking) {
                onTouchEvent((MotionEvent) null);
            }
            super.requestDisallowInterceptTouchEvent(disallowIntercept);
        }

        public boolean hasOverlappingRendering() {
            return false;
        }

        /* access modifiers changed from: protected */
        public void dispatchDraw(Canvas canvas) {
            int i;
            if (Build.VERSION.SDK_INT < 26) {
                this.backgroundPaint.setColor(-16777216);
            } else if (BottomSheet.this.navBarColorKey != null) {
                Paint paint = this.backgroundPaint;
                BottomSheet bottomSheet = BottomSheet.this;
                paint.setColor(bottomSheet.getThemedColor(bottomSheet.navBarColorKey));
            } else {
                this.backgroundPaint.setColor(BottomSheet.this.navBarColor);
            }
            if (this.backgroundPaint.getAlpha() >= 255 || !BottomSheet.this.drawNavigationBar) {
                super.dispatchDraw(canvas);
            } else {
                float translation = 0.0f;
                if (BottomSheet.this.scrollNavBar || (Build.VERSION.SDK_INT >= 29 && BottomSheet.this.getAdditionalMandatoryOffsets() > 0)) {
                    translation = Math.max(0.0f, ((float) BottomSheet.this.getBottomInset()) - (((float) BottomSheet.this.containerView.getMeasuredHeight()) - BottomSheet.this.containerView.getTranslationY()));
                }
                int navBarHeight = BottomSheet.this.drawNavigationBar ? BottomSheet.this.getBottomInset() : 0;
                canvas.save();
                canvas.clipRect((float) (BottomSheet.this.containerView.getLeft() + BottomSheet.this.backgroundPaddingLeft), (((float) (getMeasuredHeight() - navBarHeight)) + translation) - BottomSheet.this.currentPanTranslationY, (float) (BottomSheet.this.containerView.getRight() - BottomSheet.this.backgroundPaddingLeft), ((float) getMeasuredHeight()) + translation, Region.Op.DIFFERENCE);
                super.dispatchDraw(canvas);
                canvas.restore();
            }
            if (!BottomSheet.this.shouldOverlayCameraViewOverNavBar()) {
                drawNavigationBar(canvas);
            }
            if (BottomSheet.this.drawNavigationBar && BottomSheet.this.rightInset != 0 && BottomSheet.this.rightInset > BottomSheet.this.leftInset && BottomSheet.this.fullWidth && AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
                canvas.drawRect((float) (BottomSheet.this.containerView.getRight() - BottomSheet.this.backgroundPaddingLeft), BottomSheet.this.containerView.getTranslationY(), (float) (BottomSheet.this.containerView.getRight() + BottomSheet.this.rightInset), (float) getMeasuredHeight(), this.backgroundPaint);
            }
            if (BottomSheet.this.drawNavigationBar && BottomSheet.this.leftInset != 0 && BottomSheet.this.leftInset > BottomSheet.this.rightInset && BottomSheet.this.fullWidth && AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
                canvas.drawRect(0.0f, BottomSheet.this.containerView.getTranslationY(), (float) (BottomSheet.this.containerView.getLeft() + BottomSheet.this.backgroundPaddingLeft), (float) getMeasuredHeight(), this.backgroundPaint);
            }
            if (BottomSheet.this.containerView.getTranslationY() < 0.0f) {
                Paint paint2 = this.backgroundPaint;
                if (BottomSheet.this.behindKeyboardColorKey != null) {
                    BottomSheet bottomSheet2 = BottomSheet.this;
                    i = bottomSheet2.getThemedColor(bottomSheet2.behindKeyboardColorKey);
                } else {
                    i = BottomSheet.this.behindKeyboardColor;
                }
                paint2.setColor(i);
                canvas.drawRect((float) (BottomSheet.this.containerView.getLeft() + BottomSheet.this.backgroundPaddingLeft), BottomSheet.this.containerView.getY() + ((float) BottomSheet.this.containerView.getMeasuredHeight()), (float) (BottomSheet.this.containerView.getRight() - BottomSheet.this.backgroundPaddingLeft), (float) getMeasuredHeight(), this.backgroundPaint);
            }
        }

        /* access modifiers changed from: protected */
        public boolean drawChild(Canvas canvas, View child, long drawingTime) {
            if (!(child instanceof CameraView)) {
                return super.drawChild(canvas, child, drawingTime);
            }
            if (BottomSheet.this.shouldOverlayCameraViewOverNavBar()) {
                drawNavigationBar(canvas);
            }
            return super.drawChild(canvas, child, drawingTime);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            int i;
            boolean restore = false;
            int i2 = 0;
            if (this.backgroundPaint.getAlpha() < 255 && BottomSheet.this.drawNavigationBar) {
                float translation = 0.0f;
                if (BottomSheet.this.scrollNavBar || (Build.VERSION.SDK_INT >= 29 && BottomSheet.this.getAdditionalMandatoryOffsets() > 0)) {
                    translation = Math.max(0.0f, ((float) BottomSheet.this.getBottomInset()) - (((float) BottomSheet.this.containerView.getMeasuredHeight()) - BottomSheet.this.containerView.getTranslationY()));
                }
                int navBarHeight = BottomSheet.this.drawNavigationBar ? BottomSheet.this.getBottomInset() : 0;
                canvas.save();
                canvas.clipRect((float) (BottomSheet.this.containerView.getLeft() + BottomSheet.this.backgroundPaddingLeft), (((float) (getMeasuredHeight() - navBarHeight)) + translation) - BottomSheet.this.currentPanTranslationY, (float) (BottomSheet.this.containerView.getRight() - BottomSheet.this.backgroundPaddingLeft), ((float) getMeasuredHeight()) + translation, Region.Op.DIFFERENCE);
                restore = true;
            }
            super.onDraw(canvas);
            if (!(BottomSheet.this.lastInsets == null || this.keyboardHeight == 0)) {
                Paint paint = this.backgroundPaint;
                if (BottomSheet.this.behindKeyboardColorKey != null) {
                    BottomSheet bottomSheet = BottomSheet.this;
                    i = bottomSheet.getThemedColor(bottomSheet.behindKeyboardColorKey);
                } else {
                    i = BottomSheet.this.behindKeyboardColor;
                }
                paint.setColor(i);
                float left = (float) (BottomSheet.this.containerView.getLeft() + BottomSheet.this.backgroundPaddingLeft);
                float measuredHeight = (float) ((getMeasuredHeight() - this.keyboardHeight) - (BottomSheet.this.drawNavigationBar ? BottomSheet.this.getBottomInset() : 0));
                float right = (float) (BottomSheet.this.containerView.getRight() - BottomSheet.this.backgroundPaddingLeft);
                int measuredHeight2 = getMeasuredHeight();
                if (BottomSheet.this.drawNavigationBar) {
                    i2 = BottomSheet.this.getBottomInset();
                }
                canvas.drawRect(left, measuredHeight, right, (float) (measuredHeight2 - i2), this.backgroundPaint);
            }
            BottomSheet.this.onContainerDraw(canvas);
            if (restore) {
                canvas.restore();
            }
        }

        public void drawNavigationBar(Canvas canvas) {
            if (Build.VERSION.SDK_INT < 26) {
                this.backgroundPaint.setColor(-16777216);
            } else if (BottomSheet.this.navBarColorKey != null) {
                Paint paint = this.backgroundPaint;
                BottomSheet bottomSheet = BottomSheet.this;
                paint.setColor(bottomSheet.getThemedColor(bottomSheet.navBarColorKey));
            } else {
                this.backgroundPaint.setColor(BottomSheet.this.navBarColor);
            }
            if ((BottomSheet.this.drawNavigationBar && BottomSheet.this.bottomInset != 0) || BottomSheet.this.currentPanTranslationY != 0.0f) {
                float translation = 0.0f;
                if (BottomSheet.this.scrollNavBar || (Build.VERSION.SDK_INT >= 29 && BottomSheet.this.getAdditionalMandatoryOffsets() > 0)) {
                    translation = Math.max(0.0f, ((float) BottomSheet.this.getBottomInset()) - (((float) BottomSheet.this.containerView.getMeasuredHeight()) - BottomSheet.this.containerView.getTranslationY()));
                }
                int navBarHeight = BottomSheet.this.drawNavigationBar ? BottomSheet.this.getBottomInset() : 0;
                canvas.drawRect((float) (BottomSheet.this.containerView.getLeft() + BottomSheet.this.backgroundPaddingLeft), (((float) (getMeasuredHeight() - navBarHeight)) + translation) - BottomSheet.this.currentPanTranslationY, (float) (BottomSheet.this.containerView.getRight() - BottomSheet.this.backgroundPaddingLeft), ((float) getMeasuredHeight()) + translation, this.backgroundPaint);
                if (BottomSheet.this.overlayDrawNavBarColor != 0) {
                    this.backgroundPaint.setColor(BottomSheet.this.overlayDrawNavBarColor);
                    canvas.drawRect((float) (BottomSheet.this.containerView.getLeft() + BottomSheet.this.backgroundPaddingLeft), (((float) (getMeasuredHeight() - navBarHeight)) + translation) - BottomSheet.this.currentPanTranslationY, (float) (BottomSheet.this.containerView.getRight() - BottomSheet.this.backgroundPaddingLeft), ((float) getMeasuredHeight()) + translation, this.backgroundPaint);
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public boolean shouldOverlayCameraViewOverNavBar() {
        return false;
    }

    public void setHideSystemVerticalInsets(boolean hideSystemVerticalInsets) {
        float[] fArr = new float[2];
        fArr[0] = this.hideSystemVerticalInsetsProgress;
        fArr[1] = hideSystemVerticalInsets ? 1.0f : 0.0f;
        ValueAnimator animator = ValueAnimator.ofFloat(fArr).setDuration(180);
        animator.setInterpolator(CubicBezierInterpolator.DEFAULT);
        animator.addUpdateListener(new BottomSheet$$ExternalSyntheticLambda0(this));
        animator.start();
    }

    /* renamed from: lambda$setHideSystemVerticalInsets$0$org-telegram-ui-ActionBar-BottomSheet  reason: not valid java name */
    public /* synthetic */ void m1276x3bf1CLASSNAME(ValueAnimator animation) {
        this.hideSystemVerticalInsetsProgress = ((Float) animation.getAnimatedValue()).floatValue();
        this.container.requestLayout();
        this.containerView.requestLayout();
    }

    /* access modifiers changed from: private */
    public int getAdditionalMandatoryOffsets() {
        if (!this.calcMandatoryInsets) {
            return 0;
        }
        Insets insets = this.lastInsets.getSystemGestureInsets();
        if (this.keyboardVisible || !this.drawNavigationBar || insets == null) {
            return 0;
        }
        if (insets.left == 0 && insets.right == 0) {
            return 0;
        }
        return insets.bottom;
    }

    public void setCalcMandatoryInsets(boolean value) {
        this.calcMandatoryInsets = value;
        this.drawNavigationBar = value;
    }

    public static class BottomSheetDelegate implements BottomSheetDelegateInterface {
        public void onOpenAnimationStart() {
        }

        public void onOpenAnimationEnd() {
        }

        public boolean canDismiss() {
            return true;
        }
    }

    public static class BottomSheetCell extends FrameLayout {
        int currentType;
        /* access modifiers changed from: private */
        public ImageView imageView;
        private final Theme.ResourcesProvider resourcesProvider;
        /* access modifiers changed from: private */
        public TextView textView;

        public BottomSheetCell(Context context, int type) {
            this(context, type, (Theme.ResourcesProvider) null);
        }

        public BottomSheetCell(Context context, int type, Theme.ResourcesProvider resourcesProvider2) {
            super(context);
            this.resourcesProvider = resourcesProvider2;
            this.currentType = type;
            setBackgroundDrawable(Theme.getSelectorDrawable(false));
            ImageView imageView2 = new ImageView(context);
            this.imageView = imageView2;
            imageView2.setScaleType(ImageView.ScaleType.CENTER);
            this.imageView.setColorFilter(new PorterDuffColorFilter(getThemedColor("dialogIcon"), PorterDuff.Mode.MULTIPLY));
            int i = 5;
            addView(this.imageView, LayoutHelper.createFrame(56, 48, (LocaleController.isRTL ? 5 : 3) | 16));
            TextView textView2 = new TextView(context);
            this.textView = textView2;
            textView2.setLines(1);
            this.textView.setSingleLine(true);
            this.textView.setGravity(1);
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
            if (type == 0) {
                this.textView.setTextColor(getThemedColor("dialogTextBlack"));
                this.textView.setTextSize(1, 16.0f);
                addView(this.textView, LayoutHelper.createFrame(-2, -2, (!LocaleController.isRTL ? 3 : i) | 16));
            } else if (type == 1) {
                this.textView.setGravity(17);
                this.textView.setTextColor(getThemedColor("dialogTextBlack"));
                this.textView.setTextSize(1, 14.0f);
                this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                addView(this.textView, LayoutHelper.createFrame(-1, -1.0f));
            } else if (type == 2) {
                this.textView.setGravity(17);
                this.textView.setTextColor(getThemedColor("featuredStickers_buttonText"));
                this.textView.setTextSize(1, 14.0f);
                this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                this.textView.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), getThemedColor("featuredStickers_addButton"), getThemedColor("featuredStickers_addButtonPressed")));
                addView(this.textView, LayoutHelper.createFrame(-1, -1.0f, 0, 16.0f, 16.0f, 16.0f, 16.0f));
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int i = this.currentType;
            int height = i == 2 ? 80 : 48;
            if (i == 0) {
                widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM);
            }
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((float) height), NUM));
        }

        public void setTextColor(int color) {
            this.textView.setTextColor(color);
        }

        public void setIconColor(int color) {
            this.imageView.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
        }

        public void setGravity(int gravity) {
            this.textView.setGravity(gravity);
        }

        public void setTextAndIcon(CharSequence text, int icon) {
            setTextAndIcon(text, icon, (Drawable) null, false);
        }

        public void setTextAndIcon(CharSequence text, Drawable icon) {
            setTextAndIcon(text, 0, icon, false);
        }

        public void setTextAndIcon(CharSequence text, int icon, Drawable drawable, boolean bigTitle) {
            this.textView.setText(text);
            float f = 21.0f;
            float f2 = 16.0f;
            if (icon == 0 && drawable == null) {
                this.imageView.setVisibility(4);
                TextView textView2 = this.textView;
                int dp = AndroidUtilities.dp(bigTitle ? 21.0f : 16.0f);
                if (!bigTitle) {
                    f = 16.0f;
                }
                textView2.setPadding(dp, 0, AndroidUtilities.dp(f), 0);
                return;
            }
            if (drawable != null) {
                this.imageView.setImageDrawable(drawable);
            } else {
                this.imageView.setImageResource(icon);
            }
            this.imageView.setVisibility(0);
            if (bigTitle) {
                TextView textView3 = this.textView;
                int dp2 = AndroidUtilities.dp(LocaleController.isRTL ? 21.0f : 72.0f);
                if (LocaleController.isRTL) {
                    f = 72.0f;
                }
                textView3.setPadding(dp2, 0, AndroidUtilities.dp(f), 0);
                this.imageView.setPadding(LocaleController.isRTL ? 0 : AndroidUtilities.dp(5.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(5.0f) : 5, 0);
                return;
            }
            TextView textView4 = this.textView;
            int dp3 = AndroidUtilities.dp(LocaleController.isRTL ? 16.0f : 72.0f);
            if (LocaleController.isRTL) {
                f2 = 72.0f;
            }
            textView4.setPadding(dp3, 0, AndroidUtilities.dp(f2), 0);
            this.imageView.setPadding(0, 0, 0, 0);
        }

        public TextView getTextView() {
            return this.textView;
        }

        public ImageView getImageView() {
            return this.imageView;
        }

        private int getThemedColor(String key) {
            Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
            Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
            return color != null ? color.intValue() : Theme.getColor(key);
        }
    }

    public void setAllowNestedScroll(boolean value) {
        this.allowNestedScroll = value;
        if (!value) {
            this.containerView.setTranslationY(0.0f);
        }
    }

    public BottomSheet(Context context, boolean needFocus) {
        this(context, needFocus, (Theme.ResourcesProvider) null);
    }

    public BottomSheet(Context context, boolean needFocus, Theme.ResourcesProvider resourcesProvider2) {
        super(context, NUM);
        this.currentAccount = UserConfig.selectedAccount;
        this.allowDrawContent = true;
        this.useHardwareLayer = true;
        this.backDrawable = new ColorDrawable(-16777216) {
            public void setAlpha(int alpha) {
                super.setAlpha(alpha);
                BottomSheet.this.container.invalidate();
            }
        };
        this.useLightStatusBar = true;
        this.behindKeyboardColorKey = "dialogBackground";
        this.canDismissWithSwipe = true;
        this.allowCustomAnimation = true;
        this.statusBarHeight = AndroidUtilities.statusBarHeight;
        this.openInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
        this.dimBehind = true;
        this.dimBehindAlpha = 51;
        this.allowNestedScroll = true;
        this.applyTopPadding = true;
        this.applyBottomPadding = true;
        this.itemViews = new ArrayList<>();
        this.dismissRunnable = new BottomSheet$$ExternalSyntheticLambda4(this);
        this.navBarColorKey = "windowBackgroundGray";
        this.resourcesProvider = resourcesProvider2;
        if (Build.VERSION.SDK_INT >= 30) {
            getWindow().addFlags(-NUM);
        } else if (Build.VERSION.SDK_INT >= 21) {
            getWindow().addFlags(-NUM);
        }
        this.touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        Rect padding = new Rect();
        Drawable mutate = context.getResources().getDrawable(NUM).mutate();
        this.shadowDrawable = mutate;
        mutate.setColorFilter(new PorterDuffColorFilter(getThemedColor("dialogBackground"), PorterDuff.Mode.MULTIPLY));
        this.shadowDrawable.getPadding(padding);
        this.backgroundPaddingLeft = padding.left;
        this.backgroundPaddingTop = padding.top;
        AnonymousClass2 r1 = new ContainerView(getContext()) {
            public boolean drawChild(Canvas canvas, View child, long drawingTime) {
                try {
                    return BottomSheet.this.allowDrawContent && super.drawChild(canvas, child, drawingTime);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                    return true;
                }
            }
        };
        this.container = r1;
        r1.setBackgroundDrawable(this.backDrawable);
        this.focusable = needFocus;
        if (Build.VERSION.SDK_INT >= 21) {
            this.container.setFitsSystemWindows(true);
            this.container.setOnApplyWindowInsetsListener(new BottomSheet$$ExternalSyntheticLambda1(this));
            if (Build.VERSION.SDK_INT >= 30) {
                this.container.setSystemUiVisibility(1792);
            } else {
                this.container.setSystemUiVisibility(1280);
            }
        }
        this.backDrawable.setAlpha(0);
    }

    /* renamed from: lambda$new$1$org-telegram-ui-ActionBar-BottomSheet  reason: not valid java name */
    public /* synthetic */ WindowInsets m1274lambda$new$1$orgtelegramuiActionBarBottomSheet(View v, WindowInsets insets) {
        int newTopInset = insets.getSystemWindowInsetTop();
        if ((newTopInset != 0 || AndroidUtilities.isInMultiwindow) && this.statusBarHeight != newTopInset) {
            this.statusBarHeight = newTopInset;
        }
        this.lastInsets = insets;
        v.requestLayout();
        if (Build.VERSION.SDK_INT >= 30) {
            return WindowInsets.CONSUMED;
        }
        return insets.consumeSystemWindowInsets();
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setWindowAnimations(NUM);
        setContentView(this.container, new ViewGroup.LayoutParams(-1, -1));
        Drawable drawable = null;
        if (this.useLightStatusBar && Build.VERSION.SDK_INT >= 23 && Theme.getColor("actionBarDefault", (boolean[]) null, true) == -1) {
            this.container.setSystemUiVisibility(this.container.getSystemUiVisibility() | 8192);
        }
        if (this.useLightNavBar != 0 && Build.VERSION.SDK_INT >= 26) {
            AndroidUtilities.setLightNavigationBar(getWindow(), false);
        }
        if (this.containerView == null) {
            AnonymousClass3 r2 = new FrameLayout(getContext()) {
                public boolean hasOverlappingRendering() {
                    return false;
                }

                public void setTranslationY(float translationY) {
                    super.setTranslationY(translationY);
                    BottomSheet.this.onContainerTranslationYChanged(translationY);
                }
            };
            this.containerView = r2;
            r2.setBackgroundDrawable(this.shadowDrawable);
            this.containerView.setPadding(this.backgroundPaddingLeft, ((this.applyTopPadding ? AndroidUtilities.dp(8.0f) : 0) + this.backgroundPaddingTop) - 1, this.backgroundPaddingLeft, this.applyBottomPadding ? AndroidUtilities.dp(8.0f) : 0);
        }
        this.containerView.setVisibility(4);
        this.container.addView(this.containerView, 0, LayoutHelper.createFrame(-1, -2, 80));
        int topOffset = 0;
        if (this.title != null) {
            TextView textView = new TextView(getContext());
            this.titleView = textView;
            textView.setLines(1);
            this.titleView.setSingleLine(true);
            this.titleView.setText(this.title);
            if (this.bigTitle) {
                this.titleView.setTextColor(getThemedColor("dialogTextBlack"));
                this.titleView.setTextSize(1, 20.0f);
                this.titleView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                this.titleView.setPadding(AndroidUtilities.dp(21.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(21.0f), AndroidUtilities.dp(8.0f));
            } else {
                this.titleView.setTextColor(getThemedColor("dialogTextGray2"));
                this.titleView.setTextSize(1, 16.0f);
                this.titleView.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), AndroidUtilities.dp(8.0f));
            }
            this.titleView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
            this.titleView.setGravity(16);
            this.containerView.addView(this.titleView, LayoutHelper.createFrame(-1, 48.0f));
            this.titleView.setOnTouchListener(BottomSheet$$ExternalSyntheticLambda3.INSTANCE);
            topOffset = 0 + 48;
        }
        View view = this.customView;
        if (view != null) {
            if (view.getParent() != null) {
                ((ViewGroup) this.customView.getParent()).removeView(this.customView);
            }
            this.containerView.addView(this.customView, LayoutHelper.createFrame(-1, -2.0f, 51, 0.0f, (float) topOffset, 0.0f, 0.0f));
        } else if (this.items != null) {
            int a = 0;
            while (true) {
                CharSequence[] charSequenceArr = this.items;
                if (a >= charSequenceArr.length) {
                    break;
                }
                if (charSequenceArr[a] != null) {
                    BottomSheetCell cell = new BottomSheetCell(getContext(), 0, this.resourcesProvider);
                    CharSequence charSequence = this.items[a];
                    int[] iArr = this.itemIcons;
                    cell.setTextAndIcon(charSequence, iArr != null ? iArr[a] : 0, drawable, this.bigTitle);
                    this.containerView.addView(cell, LayoutHelper.createFrame(-1, 48.0f, 51, 0.0f, (float) topOffset, 0.0f, 0.0f));
                    topOffset += 48;
                    cell.setTag(Integer.valueOf(a));
                    cell.setOnClickListener(new BottomSheet$$ExternalSyntheticLambda2(this));
                    this.itemViews.add(cell);
                }
                a++;
                drawable = null;
            }
        }
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = -1;
        params.gravity = 51;
        params.dimAmount = 0.0f;
        params.flags &= -3;
        if (this.focusable) {
            params.softInputMode = 16;
        } else {
            params.flags |= 131072;
        }
        if (this.isFullscreen) {
            if (Build.VERSION.SDK_INT >= 21) {
                params.flags |= -NUM;
            }
            params.flags |= 1024;
            this.container.setSystemUiVisibility(1284);
        }
        params.height = -1;
        if (Build.VERSION.SDK_INT >= 28) {
            params.layoutInDisplayCutoutMode = 1;
        }
        window.setAttributes(params);
    }

    static /* synthetic */ boolean lambda$onCreate$2(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: lambda$onCreate$3$org-telegram-ui-ActionBar-BottomSheet  reason: not valid java name */
    public /* synthetic */ void m1275lambda$onCreate$3$orgtelegramuiActionBarBottomSheet(View v) {
        dismissWithButtonClick(((Integer) v.getTag()).intValue());
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
    }

    public void setUseLightStatusBar(boolean value) {
        int flags;
        this.useLightStatusBar = value;
        if (Build.VERSION.SDK_INT >= 23) {
            int color = Theme.getColor("actionBarDefault", (boolean[]) null, true);
            int flags2 = this.container.getSystemUiVisibility();
            if (!this.useLightStatusBar || color != -1) {
                flags = flags2 & -8193;
            } else {
                flags = flags2 | 8192;
            }
            this.container.setSystemUiVisibility(flags);
        }
    }

    public boolean isFocusable() {
        return this.focusable;
    }

    public void setFocusable(boolean value) {
        if (this.focusable != value) {
            this.focusable = value;
            Window window = getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            if (this.focusable) {
                params.softInputMode = 16;
                params.flags &= -131073;
            } else {
                params.softInputMode = 48;
                params.flags |= 131072;
            }
            window.setAttributes(params);
        }
    }

    public void setShowWithoutAnimation(boolean value) {
        this.showWithoutAnimation = value;
    }

    public void setBackgroundColor(int color) {
        this.shadowDrawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
    }

    public void show() {
        super.show();
        if (this.focusable) {
            getWindow().setSoftInputMode(16);
        }
        int i = 0;
        this.dismissed = false;
        cancelSheetAnimation();
        this.containerView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.x + (this.backgroundPaddingLeft * 2), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.y, Integer.MIN_VALUE));
        float f = 0.0f;
        if (this.showWithoutAnimation) {
            ColorDrawable colorDrawable = this.backDrawable;
            if (this.dimBehind) {
                i = this.dimBehindAlpha;
            }
            colorDrawable.setAlpha(i);
            this.containerView.setTranslationY(0.0f);
            return;
        }
        this.backDrawable.setAlpha(0);
        if (Build.VERSION.SDK_INT >= 18) {
            this.layoutCount = 2;
            ViewGroup viewGroup = this.containerView;
            if (Build.VERSION.SDK_INT >= 21) {
                f = ((float) AndroidUtilities.statusBarHeight) * (1.0f - this.hideSystemVerticalInsetsProgress);
            }
            float measuredHeight = f + ((float) this.containerView.getMeasuredHeight());
            if (this.scrollNavBar) {
                i = getBottomInset();
            }
            viewGroup.setTranslationY(measuredHeight + ((float) i));
            AnonymousClass4 r0 = new Runnable() {
                public void run() {
                    if (BottomSheet.this.startAnimationRunnable == this && !BottomSheet.this.dismissed) {
                        BottomSheet.this.startAnimationRunnable = null;
                        BottomSheet.this.startOpenAnimation();
                    }
                }
            };
            this.startAnimationRunnable = r0;
            AndroidUtilities.runOnUIThread(r0, this.openNoDelay ? 0 : 150);
            return;
        }
        startOpenAnimation();
    }

    public ColorDrawable getBackDrawable() {
        return this.backDrawable;
    }

    public int getBackgroundPaddingTop() {
        return this.backgroundPaddingTop;
    }

    public void setAllowDrawContent(boolean value) {
        if (this.allowDrawContent != value) {
            this.allowDrawContent = value;
            this.container.setBackgroundDrawable(value ? this.backDrawable : null);
            this.container.invalidate();
        }
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return this.canDismissWithSwipe;
    }

    public void setCanDismissWithSwipe(boolean value) {
        this.canDismissWithSwipe = value;
    }

    /* access modifiers changed from: protected */
    public boolean onContainerTouchEvent(MotionEvent event) {
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean onScrollUp(float translationY) {
        return false;
    }

    /* access modifiers changed from: protected */
    public void onScrollUpEnd(float translationY) {
    }

    /* access modifiers changed from: protected */
    public void onScrollUpBegin(float translationY) {
    }

    public void setCustomView(View view) {
        this.customView = view;
    }

    public void setTitle(CharSequence value) {
        setTitle(value, false);
    }

    public void setTitle(CharSequence value, boolean big) {
        this.title = value;
        this.bigTitle = big;
    }

    public void setApplyTopPadding(boolean value) {
        this.applyTopPadding = value;
    }

    public void setApplyBottomPadding(boolean value) {
        this.applyBottomPadding = value;
    }

    /* access modifiers changed from: protected */
    public boolean onCustomMeasure(View view, int width, int height) {
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean onCustomLayout(View view, int left, int top, int right, int bottom) {
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithTouchOutside() {
        return true;
    }

    public TextView getTitleView() {
        return this.titleView;
    }

    /* access modifiers changed from: protected */
    public void onContainerTranslationYChanged(float translationY) {
    }

    /* access modifiers changed from: protected */
    public void cancelSheetAnimation() {
        AnimatorSet animatorSet = this.currentSheetAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.currentSheetAnimation = null;
            this.currentSheetAnimationType = 0;
        }
    }

    public void setOnHideListener(DialogInterface.OnDismissListener listener) {
        this.onHideListener = listener;
    }

    /* access modifiers changed from: protected */
    public int getTargetOpenTranslationY() {
        return 0;
    }

    /* access modifiers changed from: private */
    public void startOpenAnimation() {
        if (!this.dismissed) {
            this.containerView.setVisibility(0);
            if (!onCustomOpenAnimation()) {
                if (Build.VERSION.SDK_INT >= 20 && this.useHardwareLayer) {
                    this.container.setLayerType(2, (Paint) null);
                }
                ViewGroup viewGroup = this.containerView;
                viewGroup.setTranslationY((float) (viewGroup.getMeasuredHeight() + (this.scrollNavBar ? getBottomInset() : 0)));
                this.currentSheetAnimationType = 1;
                AnimatorSet animatorSet = new AnimatorSet();
                this.currentSheetAnimation = animatorSet;
                Animator[] animatorArr = new Animator[2];
                animatorArr[0] = ObjectAnimator.ofFloat(this.containerView, View.TRANSLATION_Y, new float[]{0.0f});
                ColorDrawable colorDrawable = this.backDrawable;
                Property<ColorDrawable, Integer> property = AnimationProperties.COLOR_DRAWABLE_ALPHA;
                int[] iArr = new int[1];
                iArr[0] = this.dimBehind ? this.dimBehindAlpha : 0;
                animatorArr[1] = ObjectAnimator.ofInt(colorDrawable, property, iArr);
                animatorSet.playTogether(animatorArr);
                this.currentSheetAnimation.setDuration(400);
                this.currentSheetAnimation.setStartDelay(20);
                this.currentSheetAnimation.setInterpolator(this.openInterpolator);
                this.currentSheetAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (BottomSheet.this.currentSheetAnimation != null && BottomSheet.this.currentSheetAnimation.equals(animation)) {
                            BottomSheet.this.currentSheetAnimation = null;
                            BottomSheet.this.currentSheetAnimationType = 0;
                            if (BottomSheet.this.delegate != null) {
                                BottomSheet.this.delegate.onOpenAnimationEnd();
                            }
                            if (BottomSheet.this.useHardwareLayer) {
                                BottomSheet.this.container.setLayerType(0, (Paint) null);
                            }
                            if (BottomSheet.this.isFullscreen) {
                                WindowManager.LayoutParams params = BottomSheet.this.getWindow().getAttributes();
                                params.flags &= -1025;
                                BottomSheet.this.getWindow().setAttributes(params);
                            }
                        }
                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 512);
                    }

                    public void onAnimationCancel(Animator animation) {
                        if (BottomSheet.this.currentSheetAnimation != null && BottomSheet.this.currentSheetAnimation.equals(animation)) {
                            BottomSheet.this.currentSheetAnimation = null;
                            BottomSheet.this.currentSheetAnimationType = 0;
                        }
                    }
                });
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 512);
                this.currentSheetAnimation.start();
            }
        }
    }

    public void setDelegate(BottomSheetDelegateInterface bottomSheetDelegate) {
        this.delegate = bottomSheetDelegate;
    }

    public FrameLayout getContainer() {
        return this.container;
    }

    public ViewGroup getSheetContainer() {
        return this.containerView;
    }

    public int getTag() {
        return this.tag;
    }

    public void setDimBehind(boolean value) {
        this.dimBehind = value;
    }

    public void setDimBehindAlpha(int value) {
        this.dimBehindAlpha = value;
    }

    public void setItemText(int item, CharSequence text) {
        if (item >= 0 && item < this.itemViews.size()) {
            this.itemViews.get(item).textView.setText(text);
        }
    }

    public void setItemColor(int item, int color, int icon) {
        if (item >= 0 && item < this.itemViews.size()) {
            BottomSheetCell cell = this.itemViews.get(item);
            cell.textView.setTextColor(color);
            cell.imageView.setColorFilter(new PorterDuffColorFilter(icon, PorterDuff.Mode.MULTIPLY));
        }
    }

    public ArrayList<BottomSheetCell> getItemViews() {
        return this.itemViews;
    }

    public void setItems(CharSequence[] i, int[] icons, DialogInterface.OnClickListener listener) {
        this.items = i;
        this.itemIcons = icons;
        this.onClickListener = listener;
    }

    public void setTitleColor(int color) {
        TextView textView = this.titleView;
        if (textView != null) {
            textView.setTextColor(color);
        }
    }

    public boolean isDismissed() {
        return this.dismissed;
    }

    public void dismissWithButtonClick(final int item) {
        if (!this.dismissed) {
            this.dismissed = true;
            cancelSheetAnimation();
            this.currentSheetAnimationType = 2;
            AnimatorSet animatorSet = new AnimatorSet();
            this.currentSheetAnimation = animatorSet;
            Animator[] animatorArr = new Animator[2];
            ViewGroup viewGroup = this.containerView;
            Property property = View.TRANSLATION_Y;
            float[] fArr = new float[1];
            fArr[0] = (float) (this.containerView.getMeasuredHeight() + AndroidUtilities.dp(10.0f) + (this.scrollNavBar ? getBottomInset() : 0));
            animatorArr[0] = ObjectAnimator.ofFloat(viewGroup, property, fArr);
            animatorArr[1] = ObjectAnimator.ofInt(this.backDrawable, AnimationProperties.COLOR_DRAWABLE_ALPHA, new int[]{0});
            animatorSet.playTogether(animatorArr);
            this.currentSheetAnimation.setDuration(180);
            this.currentSheetAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            this.currentSheetAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (BottomSheet.this.currentSheetAnimation != null && BottomSheet.this.currentSheetAnimation.equals(animation)) {
                        BottomSheet.this.currentSheetAnimation = null;
                        BottomSheet.this.currentSheetAnimationType = 0;
                        if (BottomSheet.this.onClickListener != null) {
                            BottomSheet.this.onClickListener.onClick(BottomSheet.this, item);
                        }
                        AndroidUtilities.runOnUIThread(new BottomSheet$6$$ExternalSyntheticLambda0(this));
                    }
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 512);
                }

                /* renamed from: lambda$onAnimationEnd$0$org-telegram-ui-ActionBar-BottomSheet$6  reason: not valid java name */
                public /* synthetic */ void m1277lambda$onAnimationEnd$0$orgtelegramuiActionBarBottomSheet$6() {
                    if (BottomSheet.this.onHideListener != null) {
                        BottomSheet.this.onHideListener.onDismiss(BottomSheet.this);
                    }
                    try {
                        BottomSheet.this.dismissInternal();
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                }

                public void onAnimationCancel(Animator animation) {
                    if (BottomSheet.this.currentSheetAnimation != null && BottomSheet.this.currentSheetAnimation.equals(animation)) {
                        BottomSheet.this.currentSheetAnimation = null;
                        BottomSheet.this.currentSheetAnimationType = 0;
                    }
                }
            });
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 512);
            this.currentSheetAnimation.start();
        }
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (this.dismissed) {
            return false;
        }
        return super.dispatchTouchEvent(ev);
    }

    public void dismiss() {
        BottomSheetDelegateInterface bottomSheetDelegateInterface = this.delegate;
        if ((bottomSheetDelegateInterface == null || bottomSheetDelegateInterface.canDismiss()) && !this.dismissed) {
            this.dismissed = true;
            DialogInterface.OnDismissListener onDismissListener = this.onHideListener;
            if (onDismissListener != null) {
                onDismissListener.onDismiss(this);
            }
            cancelSheetAnimation();
            long duration = 0;
            if (!this.allowCustomAnimation || !onCustomCloseAnimation()) {
                this.currentSheetAnimationType = 2;
                AnimatorSet animatorSet = new AnimatorSet();
                this.currentSheetAnimation = animatorSet;
                Animator[] animatorArr = new Animator[2];
                ViewGroup viewGroup = this.containerView;
                Property property = View.TRANSLATION_Y;
                float[] fArr = new float[1];
                fArr[0] = (float) (this.containerView.getMeasuredHeight() + this.container.keyboardHeight + AndroidUtilities.dp(10.0f) + (this.scrollNavBar ? getBottomInset() : 0));
                animatorArr[0] = ObjectAnimator.ofFloat(viewGroup, property, fArr);
                animatorArr[1] = ObjectAnimator.ofInt(this.backDrawable, AnimationProperties.COLOR_DRAWABLE_ALPHA, new int[]{0});
                animatorSet.playTogether(animatorArr);
                if (this.useFastDismiss) {
                    int height = this.containerView.getMeasuredHeight();
                    duration = (long) Math.max(60, (int) (((((float) height) - this.containerView.getTranslationY()) * 250.0f) / ((float) height)));
                    this.currentSheetAnimation.setDuration(duration);
                    this.useFastDismiss = false;
                } else {
                    duration = 250;
                    this.currentSheetAnimation.setDuration(250);
                }
                this.currentSheetAnimation.setInterpolator(CubicBezierInterpolator.DEFAULT);
                this.currentSheetAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (BottomSheet.this.currentSheetAnimation != null && BottomSheet.this.currentSheetAnimation.equals(animation)) {
                            BottomSheet.this.currentSheetAnimation = null;
                            BottomSheet.this.currentSheetAnimationType = 0;
                            AndroidUtilities.runOnUIThread(new BottomSheet$7$$ExternalSyntheticLambda0(this));
                        }
                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 512);
                    }

                    /* renamed from: lambda$onAnimationEnd$0$org-telegram-ui-ActionBar-BottomSheet$7  reason: not valid java name */
                    public /* synthetic */ void m1278lambda$onAnimationEnd$0$orgtelegramuiActionBarBottomSheet$7() {
                        try {
                            BottomSheet.this.dismissInternal();
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                    }

                    public void onAnimationCancel(Animator animation) {
                        if (BottomSheet.this.currentSheetAnimation != null && BottomSheet.this.currentSheetAnimation.equals(animation)) {
                            BottomSheet.this.currentSheetAnimation = null;
                            BottomSheet.this.currentSheetAnimationType = 0;
                        }
                    }
                });
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 512);
                this.currentSheetAnimation.start();
            }
            Bulletin bulletin = Bulletin.getVisibleBulletin();
            if (bulletin != null && bulletin.isShowing()) {
                if (duration > 0) {
                    bulletin.hide((long) (((float) duration) * 0.6f));
                } else {
                    bulletin.hide();
                }
            }
        }
    }

    public int getSheetAnimationType() {
        return this.currentSheetAnimationType;
    }

    public void dismissInternal() {
        try {
            super.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: protected */
    public boolean onCustomCloseAnimation() {
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean onCustomOpenAnimation() {
        return false;
    }

    public static class Builder {
        private BottomSheet bottomSheet;

        public Builder(Context context) {
            this(context, false);
        }

        public Builder(Context context, boolean needFocus) {
            this(context, needFocus, (Theme.ResourcesProvider) null);
        }

        public Builder(Context context, boolean needFocus, Theme.ResourcesProvider resourcesProvider) {
            this.bottomSheet = new BottomSheet(context, needFocus, resourcesProvider);
        }

        public Builder setItems(CharSequence[] items, DialogInterface.OnClickListener onClickListener) {
            CharSequence[] unused = this.bottomSheet.items = items;
            DialogInterface.OnClickListener unused2 = this.bottomSheet.onClickListener = onClickListener;
            return this;
        }

        public Builder setItems(CharSequence[] items, int[] icons, DialogInterface.OnClickListener onClickListener) {
            CharSequence[] unused = this.bottomSheet.items = items;
            int[] unused2 = this.bottomSheet.itemIcons = icons;
            DialogInterface.OnClickListener unused3 = this.bottomSheet.onClickListener = onClickListener;
            return this;
        }

        public Builder setCustomView(View view) {
            View unused = this.bottomSheet.customView = view;
            return this;
        }

        public View getCustomView() {
            return this.bottomSheet.customView;
        }

        public Builder setTitle(CharSequence title) {
            return setTitle(title, false);
        }

        public Builder setTitle(CharSequence title, boolean big) {
            CharSequence unused = this.bottomSheet.title = title;
            boolean unused2 = this.bottomSheet.bigTitle = big;
            return this;
        }

        public BottomSheet create() {
            return this.bottomSheet;
        }

        public BottomSheet setDimBehind(boolean value) {
            this.bottomSheet.dimBehind = value;
            return this.bottomSheet;
        }

        public BottomSheet show() {
            this.bottomSheet.show();
            return this.bottomSheet;
        }

        public Builder setTag(int tag) {
            int unused = this.bottomSheet.tag = tag;
            return this;
        }

        public Builder setUseHardwareLayer(boolean value) {
            this.bottomSheet.useHardwareLayer = value;
            return this;
        }

        public Builder setDelegate(BottomSheetDelegate delegate) {
            this.bottomSheet.setDelegate(delegate);
            return this;
        }

        public Builder setApplyTopPadding(boolean value) {
            boolean unused = this.bottomSheet.applyTopPadding = value;
            return this;
        }

        public Builder setApplyBottomPadding(boolean value) {
            boolean unused = this.bottomSheet.applyBottomPadding = value;
            return this;
        }

        public Runnable getDismissRunnable() {
            return this.bottomSheet.dismissRunnable;
        }

        public BottomSheet setUseFullWidth(boolean value) {
            this.bottomSheet.fullWidth = value;
            return this.bottomSheet;
        }

        public BottomSheet setUseFullscreen(boolean value) {
            this.bottomSheet.isFullscreen = value;
            return this.bottomSheet;
        }

        public Builder setOnPreDismissListener(DialogInterface.OnDismissListener onDismissListener) {
            this.bottomSheet.setOnHideListener(onDismissListener);
            return this;
        }

        public Builder fixNavigationBar() {
            this.bottomSheet.drawNavigationBar = true;
            this.bottomSheet.scrollNavBar = true;
            BottomSheet bottomSheet2 = this.bottomSheet;
            bottomSheet2.setOverlayNavBarColor(bottomSheet2.getThemedColor("dialogBackground"));
            return this;
        }
    }

    public int getLeftInset() {
        if (this.lastInsets == null || Build.VERSION.SDK_INT < 21) {
            return 0;
        }
        return (int) (((float) this.lastInsets.getSystemWindowInsetLeft()) * (1.0f - this.hideSystemVerticalInsetsProgress));
    }

    public int getRightInset() {
        if (this.lastInsets == null || Build.VERSION.SDK_INT < 21) {
            return 0;
        }
        return (int) (((float) this.lastInsets.getSystemWindowInsetRight()) * (1.0f - this.hideSystemVerticalInsetsProgress));
    }

    public int getStatusBarHeight() {
        return (int) (((float) this.statusBarHeight) * (1.0f - this.hideSystemVerticalInsetsProgress));
    }

    public int getBottomInset() {
        return (int) (((float) this.bottomInset) * (1.0f - this.hideSystemVerticalInsetsProgress));
    }

    public void onConfigurationChanged(Configuration newConfig) {
    }

    public void onContainerDraw(Canvas canvas) {
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        return null;
    }

    public void setCurrentPanTranslationY(float currentPanTranslationY2) {
        this.currentPanTranslationY = currentPanTranslationY2;
        this.container.invalidate();
    }

    public void setOverlayNavBarColor(int color) {
        this.overlayDrawNavBarColor = color;
        ContainerView containerView2 = this.container;
        if (containerView2 != null) {
            containerView2.invalidate();
        }
        AndroidUtilities.setNavigationBarColor(getWindow(), this.overlayDrawNavBarColor);
        AndroidUtilities.setLightNavigationBar(getWindow(), ((double) AndroidUtilities.computePerceivedBrightness(this.overlayDrawNavBarColor)) > 0.721d);
    }

    public ViewGroup getContainerView() {
        return this.containerView;
    }

    public int getCurrentAccount() {
        return this.currentAccount;
    }

    /* access modifiers changed from: protected */
    public int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }

    public void setOpenNoDelay(boolean openNoDelay2) {
        this.openNoDelay = openNoDelay2;
    }

    public int getBackgroundPaddingLeft() {
        return this.backgroundPaddingLeft;
    }
}
