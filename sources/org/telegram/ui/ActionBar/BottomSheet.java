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
import android.graphics.Color;
import android.graphics.Insets;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
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
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;

public class BottomSheet extends Dialog {
    /* access modifiers changed from: private */
    public boolean allowCustomAnimation = true;
    /* access modifiers changed from: private */
    public boolean allowDrawContent = true;
    protected boolean allowNestedScroll = true;
    /* access modifiers changed from: private */
    public boolean applyBottomPadding = true;
    /* access modifiers changed from: private */
    public boolean applyTopPadding = true;
    /* access modifiers changed from: protected */
    public ColorDrawable backDrawable = new ColorDrawable(-16777216) {
        public void setAlpha(int i) {
            super.setAlpha(i);
            BottomSheet.this.container.invalidate();
        }
    };
    /* access modifiers changed from: protected */
    public int backgroundPaddingLeft;
    /* access modifiers changed from: protected */
    public int backgroundPaddingTop;
    protected int behindKeyboardColor;
    protected String behindKeyboardColorKey = "dialogBackground";
    /* access modifiers changed from: private */
    public boolean bigTitle;
    /* access modifiers changed from: private */
    public int bottomInset;
    protected boolean calcMandatoryInsets;
    private boolean canDismissWithSwipe = true;
    /* access modifiers changed from: protected */
    public ContainerView container;
    /* access modifiers changed from: protected */
    public ViewGroup containerView;
    /* access modifiers changed from: protected */
    public int currentAccount = UserConfig.selectedAccount;
    /* access modifiers changed from: private */
    public float currentPanTranslationY;
    protected AnimatorSet currentSheetAnimation;
    /* access modifiers changed from: protected */
    public int currentSheetAnimationType;
    /* access modifiers changed from: private */
    public View customView;
    /* access modifiers changed from: private */
    public BottomSheetDelegateInterface delegate;
    /* access modifiers changed from: private */
    public boolean dimBehind = true;
    private int dimBehindAlpha = 51;
    /* access modifiers changed from: private */
    public boolean disableScroll;
    /* access modifiers changed from: private */
    public Runnable dismissRunnable = new Runnable() {
        public final void run() {
            BottomSheet.this.dismiss();
        }
    };
    /* access modifiers changed from: private */
    public boolean dismissed;
    protected boolean drawNavigationBar;
    private boolean focusable;
    protected boolean fullWidth;
    /* access modifiers changed from: protected */
    public boolean isFullscreen;
    /* access modifiers changed from: private */
    public int[] itemIcons;
    private ArrayList<BottomSheetCell> itemViews = new ArrayList<>();
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
    protected String navBarColorKey = "windowBackgroundGray";
    protected View nestedScrollChild;
    /* access modifiers changed from: private */
    public DialogInterface.OnClickListener onClickListener;
    private DialogInterface.OnDismissListener onHideListener;
    protected Interpolator openInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
    /* access modifiers changed from: private */
    public int overlayDrawNavBarColor;
    /* access modifiers changed from: private */
    public int rightInset;
    protected boolean scrollNavBar;
    /* access modifiers changed from: protected */
    public Drawable shadowDrawable;
    private boolean showWithoutAnimation;
    protected boolean smoothKeyboardAnimationEnabled;
    protected Runnable startAnimationRunnable;
    /* access modifiers changed from: protected */
    public int statusBarHeight = AndroidUtilities.statusBarHeight;
    /* access modifiers changed from: private */
    public CharSequence title;
    private TextView titleView;
    /* access modifiers changed from: private */
    public int touchSlop;
    /* access modifiers changed from: private */
    public boolean useFastDismiss;
    /* access modifiers changed from: private */
    public boolean useHardwareLayer = true;
    protected boolean useLightNavBar;
    protected boolean useLightStatusBar = true;
    protected boolean useSmoothKeyboard;

    public static class BottomSheetDelegate implements BottomSheetDelegateInterface {
        public void onOpenAnimationEnd() {
        }
    }

    public interface BottomSheetDelegateInterface {
        boolean canDismiss();

        void onOpenAnimationEnd();
    }

    static /* synthetic */ boolean lambda$onCreate$1(View view, MotionEvent motionEvent) {
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithTouchOutside() {
        return true;
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        return null;
    }

    public void onConfigurationChanged(Configuration configuration) {
    }

    public void onContainerDraw(Canvas canvas) {
    }

    /* access modifiers changed from: protected */
    public boolean onContainerTouchEvent(MotionEvent motionEvent) {
        return false;
    }

    /* access modifiers changed from: protected */
    public void onContainerTranslationYChanged(float f) {
    }

    /* access modifiers changed from: protected */
    public boolean onCustomCloseAnimation() {
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean onCustomLayout(View view, int i, int i2, int i3, int i4) {
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean onCustomMeasure(View view, int i, int i2) {
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean onCustomOpenAnimation() {
        return false;
    }

    static /* synthetic */ int access$1110(BottomSheet bottomSheet) {
        int i = bottomSheet.layoutCount;
        bottomSheet.layoutCount = i - 1;
        return i;
    }

    static /* synthetic */ int access$612(BottomSheet bottomSheet, int i) {
        int i2 = bottomSheet.bottomInset + i;
        bottomSheet.bottomInset = i2;
        return i2;
    }

    static /* synthetic */ int access$620(BottomSheet bottomSheet, int i) {
        int i2 = bottomSheet.bottomInset - i;
        bottomSheet.bottomInset = i2;
        return i2;
    }

    public void setDisableScroll(boolean z) {
        this.disableScroll = z;
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

        public boolean hasOverlappingRendering() {
            return false;
        }

        public boolean onNestedFling(View view, float f, float f2, boolean z) {
            return false;
        }

        public boolean onNestedPreFling(View view, float f, float f2) {
            return false;
        }

        public ContainerView(Context context) {
            super(context);
            setWillNotDraw(false);
        }

        public boolean onStartNestedScroll(View view, View view2, int i) {
            BottomSheet bottomSheet = BottomSheet.this;
            View view3 = bottomSheet.nestedScrollChild;
            if ((view3 == null || view == view3) && !bottomSheet.dismissed) {
                BottomSheet bottomSheet2 = BottomSheet.this;
                return bottomSheet2.allowNestedScroll && i == 2 && !bottomSheet2.canDismissWithSwipe();
            }
        }

        public void onNestedScrollAccepted(View view, View view2, int i) {
            this.nestedScrollingParentHelper.onNestedScrollAccepted(view, view2, i);
            if (!BottomSheet.this.dismissed && BottomSheet.this.allowNestedScroll) {
                cancelCurrentAnimation();
            }
        }

        public void onStopNestedScroll(View view) {
            this.nestedScrollingParentHelper.onStopNestedScroll(view);
            if (!BottomSheet.this.dismissed) {
                BottomSheet bottomSheet = BottomSheet.this;
                if (bottomSheet.allowNestedScroll) {
                    bottomSheet.containerView.getTranslationY();
                    checkDismiss(0.0f, 0.0f);
                }
            }
        }

        public void onNestedScroll(View view, int i, int i2, int i3, int i4) {
            if (!BottomSheet.this.dismissed && BottomSheet.this.allowNestedScroll) {
                cancelCurrentAnimation();
                if (i4 != 0) {
                    float translationY = BottomSheet.this.containerView.getTranslationY() - ((float) i4);
                    if (translationY < 0.0f) {
                        translationY = 0.0f;
                    }
                    BottomSheet.this.containerView.setTranslationY(translationY);
                    BottomSheet.this.container.invalidate();
                }
            }
        }

        public void onNestedPreScroll(View view, int i, int i2, int[] iArr) {
            if (!BottomSheet.this.dismissed && BottomSheet.this.allowNestedScroll) {
                cancelCurrentAnimation();
                float translationY = BottomSheet.this.containerView.getTranslationY();
                float f = 0.0f;
                if (translationY > 0.0f && i2 > 0) {
                    float f2 = translationY - ((float) i2);
                    iArr[1] = i2;
                    if (f2 >= 0.0f) {
                        f = f2;
                    }
                    BottomSheet.this.containerView.setTranslationY(f);
                    BottomSheet.this.container.invalidate();
                }
            }
        }

        public int getNestedScrollAxes() {
            return this.nestedScrollingParentHelper.getNestedScrollAxes();
        }

        private void checkDismiss(float f, float f2) {
            float translationY = BottomSheet.this.containerView.getTranslationY();
            if (!((translationY < AndroidUtilities.getPixelsInCM(0.8f, false) && (f2 < 3500.0f || Math.abs(f2) < Math.abs(f))) || (f2 < 0.0f && Math.abs(f2) >= 3500.0f))) {
                boolean access$100 = BottomSheet.this.allowCustomAnimation;
                boolean unused = BottomSheet.this.allowCustomAnimation = false;
                boolean unused2 = BottomSheet.this.useFastDismiss = true;
                BottomSheet.this.dismiss();
                boolean unused3 = BottomSheet.this.allowCustomAnimation = access$100;
                return;
            }
            AnimatorSet animatorSet = new AnimatorSet();
            this.currentAnimation = animatorSet;
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(BottomSheet.this.containerView, "translationY", new float[]{0.0f})});
            this.currentAnimation.setDuration((long) ((int) ((Math.max(0.0f, translationY) / AndroidUtilities.getPixelsInCM(0.8f, false)) * 150.0f)));
            this.currentAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (ContainerView.this.currentAnimation != null && ContainerView.this.currentAnimation.equals(animator)) {
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

        /* access modifiers changed from: package-private */
        public boolean processTouchEvent(MotionEvent motionEvent, boolean z) {
            if (BottomSheet.this.dismissed) {
                return false;
            }
            if (BottomSheet.this.onContainerTouchEvent(motionEvent)) {
                return true;
            }
            if (!BottomSheet.this.canDismissWithTouchOutside() || motionEvent == null || (!(motionEvent.getAction() == 0 || motionEvent.getAction() == 2) || this.startedTracking || this.maybeStartTracking || motionEvent.getPointerCount() != 1)) {
                float f = 0.0f;
                if (motionEvent != null && motionEvent.getAction() == 2 && motionEvent.getPointerId(0) == this.startedTrackingPointerId) {
                    if (this.velocityTracker == null) {
                        this.velocityTracker = VelocityTracker.obtain();
                    }
                    float abs = (float) Math.abs((int) (motionEvent.getX() - ((float) this.startedTrackingX)));
                    float y = (float) (((int) motionEvent.getY()) - this.startedTrackingY);
                    this.velocityTracker.addMovement(motionEvent);
                    if (!BottomSheet.this.disableScroll && this.maybeStartTracking && !this.startedTracking && y > 0.0f && y / 3.0f > Math.abs(abs) && Math.abs(y) >= ((float) BottomSheet.this.touchSlop)) {
                        this.startedTrackingY = (int) motionEvent.getY();
                        this.maybeStartTracking = false;
                        this.startedTracking = true;
                        requestDisallowInterceptTouchEvent(true);
                    } else if (this.startedTracking) {
                        float translationY = BottomSheet.this.containerView.getTranslationY() + y;
                        if (translationY >= 0.0f) {
                            f = translationY;
                        }
                        BottomSheet.this.containerView.setTranslationY(f);
                        this.startedTrackingY = (int) motionEvent.getY();
                        BottomSheet.this.container.invalidate();
                    }
                } else if (motionEvent == null || (motionEvent.getPointerId(0) == this.startedTrackingPointerId && (motionEvent.getAction() == 3 || motionEvent.getAction() == 1 || motionEvent.getAction() == 6))) {
                    if (this.velocityTracker == null) {
                        this.velocityTracker = VelocityTracker.obtain();
                    }
                    this.velocityTracker.computeCurrentVelocity(1000);
                    float translationY2 = BottomSheet.this.containerView.getTranslationY();
                    if (this.startedTracking || translationY2 != 0.0f) {
                        checkDismiss(this.velocityTracker.getXVelocity(), this.velocityTracker.getYVelocity());
                        this.startedTracking = false;
                    } else {
                        this.maybeStartTracking = false;
                        this.startedTracking = false;
                    }
                    VelocityTracker velocityTracker2 = this.velocityTracker;
                    if (velocityTracker2 != null) {
                        velocityTracker2.recycle();
                        this.velocityTracker = null;
                    }
                    this.startedTrackingPointerId = -1;
                }
            } else {
                this.startedTrackingX = (int) motionEvent.getX();
                int y2 = (int) motionEvent.getY();
                this.startedTrackingY = y2;
                if (y2 < BottomSheet.this.containerView.getTop() || this.startedTrackingX < BottomSheet.this.containerView.getLeft() || this.startedTrackingX > BottomSheet.this.containerView.getRight()) {
                    BottomSheet.this.dismiss();
                    return true;
                }
                this.startedTrackingPointerId = motionEvent.getPointerId(0);
                this.maybeStartTracking = true;
                cancelCurrentAnimation();
                VelocityTracker velocityTracker3 = this.velocityTracker;
                if (velocityTracker3 != null) {
                    velocityTracker3.clear();
                }
            }
            if ((z || !this.maybeStartTracking) && !this.startedTracking && BottomSheet.this.canDismissWithSwipe()) {
                return false;
            }
            return true;
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            return processTouchEvent(motionEvent, false);
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Removed duplicated region for block: B:44:0x00e8  */
        /* JADX WARNING: Removed duplicated region for block: B:52:0x0115  */
        /* JADX WARNING: Removed duplicated region for block: B:55:0x011e  */
        /* JADX WARNING: Removed duplicated region for block: B:68:0x0191  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onMeasure(int r13, int r14) {
            /*
                r12 = this;
                int r13 = android.view.View.MeasureSpec.getSize(r13)
                int r14 = android.view.View.MeasureSpec.getSize(r14)
                android.view.View r0 = r12.getRootView()
                android.graphics.Rect r1 = r12.rect
                r12.getWindowVisibleDisplayFrame(r1)
                int r1 = r12.keyboardHeight
                android.graphics.Rect r2 = r12.rect
                int r3 = r2.bottom
                r4 = 1101004800(0x41a00000, float:20.0)
                r5 = 0
                if (r3 == 0) goto L_0x0052
                int r2 = r2.top
                if (r2 == 0) goto L_0x0052
                int r2 = r0.getHeight()
                android.graphics.Rect r3 = r12.rect
                int r3 = r3.top
                if (r3 == 0) goto L_0x002d
                int r3 = org.telegram.messenger.AndroidUtilities.statusBarHeight
                goto L_0x002e
            L_0x002d:
                r3 = 0
            L_0x002e:
                int r2 = r2 - r3
                int r0 = org.telegram.messenger.AndroidUtilities.getViewInset(r0)
                int r2 = r2 - r0
                android.graphics.Rect r0 = r12.rect
                int r3 = r0.bottom
                int r0 = r0.top
                int r3 = r3 - r0
                int r2 = r2 - r3
                int r0 = java.lang.Math.max(r5, r2)
                r12.keyboardHeight = r0
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r4)
                if (r0 >= r2) goto L_0x004a
                r12.keyboardHeight = r5
            L_0x004a:
                org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.ActionBar.BottomSheet.this
                int r2 = r12.keyboardHeight
                org.telegram.ui.ActionBar.BottomSheet.access$620(r0, r2)
                goto L_0x0054
            L_0x0052:
                r12.keyboardHeight = r5
            L_0x0054:
                int r0 = r12.keyboardHeight
                r2 = 1
                if (r1 == r0) goto L_0x005b
                r12.keyboardChanged = r2
            L_0x005b:
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r4)
                if (r0 <= r3) goto L_0x0065
                r0 = 1
                goto L_0x0066
            L_0x0065:
                r0 = 0
            L_0x0066:
                r1.keyboardVisible = r0
                org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.WindowInsets r0 = r0.lastInsets
                r1 = 29
                r3 = 21
                if (r0 == 0) goto L_0x00cc
                int r0 = android.os.Build.VERSION.SDK_INT
                if (r0 < r3) goto L_0x00cc
                org.telegram.ui.ActionBar.BottomSheet r4 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.WindowInsets r6 = r4.lastInsets
                int r6 = r6.getSystemWindowInsetBottom()
                int unused = r4.bottomInset = r6
                org.telegram.ui.ActionBar.BottomSheet r4 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.WindowInsets r6 = r4.lastInsets
                int r6 = r6.getSystemWindowInsetLeft()
                int unused = r4.leftInset = r6
                org.telegram.ui.ActionBar.BottomSheet r4 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.WindowInsets r6 = r4.lastInsets
                int r6 = r6.getSystemWindowInsetRight()
                int unused = r4.rightInset = r6
                if (r0 < r1) goto L_0x00aa
                org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.ActionBar.BottomSheet.this
                int r4 = r0.getAdditionalMandatoryOffsets()
                org.telegram.ui.ActionBar.BottomSheet.access$612(r0, r4)
            L_0x00aa:
                org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.ActionBar.BottomSheet.this
                boolean r4 = r0.keyboardVisible
                if (r4 == 0) goto L_0x00bf
                android.graphics.Rect r4 = r12.rect
                int r6 = r4.bottom
                if (r6 == 0) goto L_0x00bf
                int r4 = r4.top
                if (r4 == 0) goto L_0x00bf
                int r4 = r12.keyboardHeight
                org.telegram.ui.ActionBar.BottomSheet.access$620(r0, r4)
            L_0x00bf:
                org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.ActionBar.BottomSheet.this
                boolean r4 = r0.drawNavigationBar
                if (r4 != 0) goto L_0x00cc
                int r0 = r0.bottomInset
                int r0 = r14 - r0
                goto L_0x00cd
            L_0x00cc:
                r0 = r14
            L_0x00cd:
                r12.setMeasuredDimension(r13, r0)
                org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.WindowInsets r0 = r0.lastInsets
                if (r0 == 0) goto L_0x00f0
                int r0 = android.os.Build.VERSION.SDK_INT
                if (r0 < r3) goto L_0x00f0
                org.telegram.ui.ActionBar.BottomSheet r4 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.WindowInsets r4 = r4.lastInsets
                int r4 = r4.getSystemWindowInsetBottom()
                if (r0 < r1) goto L_0x00ef
                org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.ActionBar.BottomSheet.this
                int r0 = r0.getAdditionalMandatoryOffsets()
                int r4 = r4 + r0
            L_0x00ef:
                int r14 = r14 - r4
            L_0x00f0:
                org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.WindowInsets r0 = r0.lastInsets
                if (r0 == 0) goto L_0x0112
                int r0 = android.os.Build.VERSION.SDK_INT
                if (r0 < r3) goto L_0x0112
                org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.WindowInsets r0 = r0.lastInsets
                int r0 = r0.getSystemWindowInsetRight()
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.WindowInsets r1 = r1.lastInsets
                int r1 = r1.getSystemWindowInsetLeft()
                int r0 = r0 + r1
                int r13 = r13 - r0
            L_0x0112:
                if (r13 >= r14) goto L_0x0115
                goto L_0x0116
            L_0x0115:
                r2 = 0
            L_0x0116:
                org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.ViewGroup r1 = r0.containerView
                r3 = 1073741824(0x40000000, float:2.0)
                if (r1 == 0) goto L_0x018b
                boolean r4 = r0.fullWidth
                r6 = -2147483648(0xfffffffvar_, float:-0.0)
                if (r4 != 0) goto L_0x017b
                boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
                r1 = 1061997773(0x3f4ccccd, float:0.8)
                if (r0 == 0) goto L_0x0147
                android.graphics.Point r0 = org.telegram.messenger.AndroidUtilities.displaySize
                int r2 = r0.x
                int r0 = r0.y
                int r0 = java.lang.Math.min(r2, r0)
                float r0 = (float) r0
                float r0 = r0 * r1
                int r0 = (int) r0
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                int r1 = r1.backgroundPaddingLeft
                int r1 = r1 * 2
                int r0 = r0 + r1
                int r0 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r3)
                goto L_0x016f
            L_0x0147:
                if (r2 == 0) goto L_0x0151
                org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.ActionBar.BottomSheet.this
                int r0 = r0.backgroundPaddingLeft
                int r0 = r0 * 2
                int r0 = r0 + r13
                goto L_0x016b
            L_0x0151:
                float r0 = (float) r13
                float r0 = r0 * r1
                r1 = 1139802112(0x43var_, float:480.0)
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                int r1 = java.lang.Math.min(r1, r13)
                float r1 = (float) r1
                float r0 = java.lang.Math.max(r0, r1)
                int r0 = (int) r0
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                int r1 = r1.backgroundPaddingLeft
                int r1 = r1 * 2
                int r0 = r0 + r1
            L_0x016b:
                int r0 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r3)
            L_0x016f:
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.ViewGroup r1 = r1.containerView
                int r2 = android.view.View.MeasureSpec.makeMeasureSpec(r14, r6)
                r1.measure(r0, r2)
                goto L_0x018b
            L_0x017b:
                int r0 = r0.backgroundPaddingLeft
                int r0 = r0 * 2
                int r0 = r0 + r13
                int r0 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r3)
                int r2 = android.view.View.MeasureSpec.makeMeasureSpec(r14, r6)
                r1.measure(r0, r2)
            L_0x018b:
                int r0 = r12.getChildCount()
            L_0x018f:
                if (r5 >= r0) goto L_0x01bb
                android.view.View r7 = r12.getChildAt(r5)
                int r1 = r7.getVisibility()
                r2 = 8
                if (r1 == r2) goto L_0x01b8
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.ViewGroup r2 = r1.containerView
                if (r7 != r2) goto L_0x01a4
                goto L_0x01b8
            L_0x01a4:
                boolean r1 = r1.onCustomMeasure(r7, r13, r14)
                if (r1 != 0) goto L_0x01b8
                int r8 = android.view.View.MeasureSpec.makeMeasureSpec(r13, r3)
                r9 = 0
                int r10 = android.view.View.MeasureSpec.makeMeasureSpec(r14, r3)
                r11 = 0
                r6 = r12
                r6.measureChildWithMargins(r7, r8, r9, r10, r11)
            L_0x01b8:
                int r5 = r5 + 1
                goto L_0x018f
            L_0x01bb:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.BottomSheet.ContainerView.onMeasure(int, int):void");
        }

        public void requestLayout() {
            super.requestLayout();
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Removed duplicated region for block: B:63:0x019a  */
        /* JADX WARNING: Removed duplicated region for block: B:67:0x01a7  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onLayout(boolean r17, int r18, int r19, int r20, int r21) {
            /*
                r16 = this;
                r0 = r16
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                org.telegram.ui.ActionBar.BottomSheet.access$1110(r1)
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.ViewGroup r1 = r1.containerView
                r7 = 1
                r8 = 21
                r9 = 2
                r10 = 0
                if (r1 == 0) goto L_0x012e
                int r2 = r21 - r19
                int r1 = r1.getMeasuredHeight()
                int r2 = r2 - r1
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.WindowInsets r1 = r1.lastInsets
                if (r1 == 0) goto L_0x0067
                int r1 = android.os.Build.VERSION.SDK_INT
                if (r1 < r8) goto L_0x0067
                org.telegram.ui.ActionBar.BottomSheet r3 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.WindowInsets r3 = r3.lastInsets
                int r3 = r3.getSystemWindowInsetLeft()
                int r3 = r18 + r3
                org.telegram.ui.ActionBar.BottomSheet r4 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.WindowInsets r4 = r4.lastInsets
                int r4 = r4.getSystemWindowInsetRight()
                int r4 = r20 - r4
                org.telegram.ui.ActionBar.BottomSheet r5 = org.telegram.ui.ActionBar.BottomSheet.this
                boolean r6 = r5.useSmoothKeyboard
                if (r6 == 0) goto L_0x0045
                r2 = 0
                goto L_0x006b
            L_0x0045:
                android.view.WindowInsets r5 = r5.lastInsets
                int r5 = r5.getSystemWindowInsetBottom()
                org.telegram.ui.ActionBar.BottomSheet r6 = org.telegram.ui.ActionBar.BottomSheet.this
                boolean r11 = r6.drawNavigationBar
                if (r11 == 0) goto L_0x0055
                r6 = 0
                goto L_0x0059
            L_0x0055:
                int r6 = r6.bottomInset
            L_0x0059:
                int r5 = r5 - r6
                int r2 = r2 - r5
                r5 = 29
                if (r1 < r5) goto L_0x006b
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                int r1 = r1.getAdditionalMandatoryOffsets()
                int r2 = r2 - r1
                goto L_0x006b
            L_0x0067:
                r3 = r18
                r4 = r20
            L_0x006b:
                int r1 = r4 - r3
                org.telegram.ui.ActionBar.BottomSheet r5 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.ViewGroup r5 = r5.containerView
                int r5 = r5.getMeasuredWidth()
                int r1 = r1 - r5
                int r1 = r1 / r9
                org.telegram.ui.ActionBar.BottomSheet r5 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.WindowInsets r5 = r5.lastInsets
                if (r5 == 0) goto L_0x008e
                int r5 = android.os.Build.VERSION.SDK_INT
                if (r5 < r8) goto L_0x008e
                org.telegram.ui.ActionBar.BottomSheet r5 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.WindowInsets r5 = r5.lastInsets
                int r5 = r5.getSystemWindowInsetLeft()
                int r1 = r1 + r5
            L_0x008e:
                org.telegram.ui.ActionBar.BottomSheet r5 = org.telegram.ui.ActionBar.BottomSheet.this
                boolean r6 = r5.smoothKeyboardAnimationEnabled
                if (r6 == 0) goto L_0x0116
                java.lang.Runnable r6 = r5.startAnimationRunnable
                if (r6 != 0) goto L_0x0116
                boolean r6 = r0.keyboardChanged
                if (r6 == 0) goto L_0x0116
                boolean r5 = r5.dismissed
                if (r5 != 0) goto L_0x0116
                org.telegram.ui.ActionBar.BottomSheet r5 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.ViewGroup r5 = r5.containerView
                int r5 = r5.getTop()
                if (r5 == r2) goto L_0x0116
                org.telegram.ui.ActionBar.BottomSheet r5 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.ViewGroup r5 = r5.containerView
                int r6 = r5.getTop()
                int r6 = r6 - r2
                float r6 = (float) r6
                r5.setTranslationY(r6)
                org.telegram.ui.ActionBar.BottomSheet r5 = org.telegram.ui.ActionBar.BottomSheet.this
                android.animation.ValueAnimator r5 = r5.keyboardContentAnimator
                if (r5 == 0) goto L_0x00ca
                org.telegram.ui.ActionBar.BottomSheet r5 = org.telegram.ui.ActionBar.BottomSheet.this
                android.animation.ValueAnimator r5 = r5.keyboardContentAnimator
                r5.cancel()
            L_0x00ca:
                org.telegram.ui.ActionBar.BottomSheet r5 = org.telegram.ui.ActionBar.BottomSheet.this
                float[] r6 = new float[r9]
                android.view.ViewGroup r11 = r5.containerView
                float r11 = r11.getTranslationY()
                r6[r10] = r11
                r11 = 0
                r6[r7] = r11
                android.animation.ValueAnimator r6 = android.animation.ValueAnimator.ofFloat(r6)
                android.animation.ValueAnimator unused = r5.keyboardContentAnimator = r6
                org.telegram.ui.ActionBar.BottomSheet r5 = org.telegram.ui.ActionBar.BottomSheet.this
                android.animation.ValueAnimator r5 = r5.keyboardContentAnimator
                org.telegram.ui.ActionBar.-$$Lambda$BottomSheet$ContainerView$Vx5MsKKO92sZml4EfbA66LWaEck r6 = new org.telegram.ui.ActionBar.-$$Lambda$BottomSheet$ContainerView$Vx5MsKKO92sZml4EfbA66LWaEck
                r6.<init>()
                r5.addUpdateListener(r6)
                org.telegram.ui.ActionBar.BottomSheet r5 = org.telegram.ui.ActionBar.BottomSheet.this
                android.animation.ValueAnimator r5 = r5.keyboardContentAnimator
                org.telegram.ui.ActionBar.BottomSheet$ContainerView$2 r6 = new org.telegram.ui.ActionBar.BottomSheet$ContainerView$2
                r6.<init>()
                r5.addListener(r6)
                org.telegram.ui.ActionBar.BottomSheet r5 = org.telegram.ui.ActionBar.BottomSheet.this
                android.animation.ValueAnimator r5 = r5.keyboardContentAnimator
                r11 = 250(0xfa, double:1.235E-321)
                android.animation.ValueAnimator r5 = r5.setDuration(r11)
                android.view.animation.Interpolator r6 = org.telegram.ui.ActionBar.AdjustPanLayoutHelper.keyboardInterpolator
                r5.setInterpolator(r6)
                org.telegram.ui.ActionBar.BottomSheet r5 = org.telegram.ui.ActionBar.BottomSheet.this
                android.animation.ValueAnimator r5 = r5.keyboardContentAnimator
                r5.start()
            L_0x0116:
                org.telegram.ui.ActionBar.BottomSheet r5 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.ViewGroup r5 = r5.containerView
                int r6 = r5.getMeasuredWidth()
                int r6 = r6 + r1
                org.telegram.ui.ActionBar.BottomSheet r11 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.ViewGroup r11 = r11.containerView
                int r11 = r11.getMeasuredHeight()
                int r11 = r11 + r2
                r5.layout(r1, r2, r6, r11)
                r11 = r3
                r12 = r4
                goto L_0x0132
            L_0x012e:
                r11 = r18
                r12 = r20
            L_0x0132:
                int r13 = r16.getChildCount()
                r14 = 0
            L_0x0137:
                if (r14 >= r13) goto L_0x01d2
                android.view.View r15 = r0.getChildAt(r14)
                int r1 = r15.getVisibility()
                r2 = 8
                if (r1 == r2) goto L_0x01ce
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.ViewGroup r2 = r1.containerView
                if (r15 != r2) goto L_0x014d
                goto L_0x01ce
            L_0x014d:
                boolean r2 = r1.drawNavigationBar
                if (r2 == 0) goto L_0x0156
                int r2 = r1.bottomInset
                goto L_0x0157
            L_0x0156:
                r2 = 0
            L_0x0157:
                int r6 = r21 - r2
                r2 = r15
                r3 = r11
                r4 = r19
                r5 = r12
                boolean r1 = r1.onCustomLayout(r2, r3, r4, r5, r6)
                if (r1 != 0) goto L_0x01ce
                android.view.ViewGroup$LayoutParams r1 = r15.getLayoutParams()
                android.widget.FrameLayout$LayoutParams r1 = (android.widget.FrameLayout.LayoutParams) r1
                int r2 = r15.getMeasuredWidth()
                int r3 = r15.getMeasuredHeight()
                int r4 = r1.gravity
                r5 = -1
                if (r4 != r5) goto L_0x0179
                r4 = 51
            L_0x0179:
                r5 = r4 & 7
                r4 = r4 & 112(0x70, float:1.57E-43)
                r5 = r5 & 7
                if (r5 == r7) goto L_0x018c
                r6 = 5
                if (r5 == r6) goto L_0x0187
                int r5 = r1.leftMargin
                goto L_0x0196
            L_0x0187:
                int r5 = r12 - r2
                int r6 = r1.rightMargin
                goto L_0x0195
            L_0x018c:
                int r5 = r12 - r11
                int r5 = r5 - r2
                int r5 = r5 / r9
                int r6 = r1.leftMargin
                int r5 = r5 + r6
                int r6 = r1.rightMargin
            L_0x0195:
                int r5 = r5 - r6
            L_0x0196:
                r6 = 16
                if (r4 == r6) goto L_0x01a7
                r6 = 80
                if (r4 == r6) goto L_0x01a1
                int r1 = r1.topMargin
                goto L_0x01b2
            L_0x01a1:
                int r4 = r21 - r19
                int r4 = r4 - r3
                int r1 = r1.bottomMargin
                goto L_0x01b0
            L_0x01a7:
                int r4 = r21 - r19
                int r4 = r4 - r3
                int r4 = r4 / r9
                int r6 = r1.topMargin
                int r4 = r4 + r6
                int r1 = r1.bottomMargin
            L_0x01b0:
                int r1 = r4 - r1
            L_0x01b2:
                org.telegram.ui.ActionBar.BottomSheet r4 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.WindowInsets r4 = r4.lastInsets
                if (r4 == 0) goto L_0x01c9
                int r4 = android.os.Build.VERSION.SDK_INT
                if (r4 < r8) goto L_0x01c9
                org.telegram.ui.ActionBar.BottomSheet r4 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.WindowInsets r4 = r4.lastInsets
                int r4 = r4.getSystemWindowInsetLeft()
                int r5 = r5 + r4
            L_0x01c9:
                int r2 = r2 + r5
                int r3 = r3 + r1
                r15.layout(r5, r1, r2, r3)
            L_0x01ce:
                int r14 = r14 + 1
                goto L_0x0137
            L_0x01d2:
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                int r1 = r1.layoutCount
                if (r1 != 0) goto L_0x01ef
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                java.lang.Runnable r1 = r1.startAnimationRunnable
                if (r1 == 0) goto L_0x01ef
                org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r1)
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                java.lang.Runnable r1 = r1.startAnimationRunnable
                r1.run()
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                r2 = 0
                r1.startAnimationRunnable = r2
            L_0x01ef:
                r0.keyboardChanged = r10
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.BottomSheet.ContainerView.onLayout(boolean, int, int, int, int):void");
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onLayout$0 */
        public /* synthetic */ void lambda$onLayout$0$BottomSheet$ContainerView(ValueAnimator valueAnimator) {
            BottomSheet.this.containerView.setTranslationY(((Float) valueAnimator.getAnimatedValue()).floatValue());
            invalidate();
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            if (BottomSheet.this.canDismissWithSwipe()) {
                return processTouchEvent(motionEvent, true);
            }
            return super.onInterceptTouchEvent(motionEvent);
        }

        public void requestDisallowInterceptTouchEvent(boolean z) {
            if (this.maybeStartTracking && !this.startedTracking) {
                onTouchEvent((MotionEvent) null);
            }
            super.requestDisallowInterceptTouchEvent(z);
        }

        /* access modifiers changed from: protected */
        public void dispatchDraw(Canvas canvas) {
            float f;
            super.dispatchDraw(canvas);
            int i = Build.VERSION.SDK_INT;
            if (i >= 26) {
                BottomSheet bottomSheet = BottomSheet.this;
                String str = bottomSheet.navBarColorKey;
                if (str != null) {
                    this.backgroundPaint.setColor(Theme.getColor(str));
                } else {
                    this.backgroundPaint.setColor(bottomSheet.navBarColor);
                }
            } else {
                this.backgroundPaint.setColor(-16777216);
            }
            BottomSheet bottomSheet2 = BottomSheet.this;
            if ((bottomSheet2.drawNavigationBar && bottomSheet2.bottomInset != 0) || BottomSheet.this.currentPanTranslationY != 0.0f) {
                BottomSheet bottomSheet3 = BottomSheet.this;
                if (bottomSheet3.scrollNavBar || (i >= 29 && bottomSheet3.getAdditionalMandatoryOffsets() > 0)) {
                    f = Math.max(0.0f, ((float) BottomSheet.this.bottomInset) - (((float) BottomSheet.this.containerView.getMeasuredHeight()) - BottomSheet.this.containerView.getTranslationY()));
                } else {
                    f = 0.0f;
                }
                BottomSheet bottomSheet4 = BottomSheet.this;
                int access$600 = bottomSheet4.drawNavigationBar ? bottomSheet4.bottomInset : 0;
                canvas.drawRect((float) (BottomSheet.this.containerView.getLeft() + BottomSheet.this.backgroundPaddingLeft), (((float) (getMeasuredHeight() - access$600)) + f) - BottomSheet.this.currentPanTranslationY, (float) (BottomSheet.this.containerView.getRight() - BottomSheet.this.backgroundPaddingLeft), ((float) getMeasuredHeight()) + f, this.backgroundPaint);
                if (BottomSheet.this.overlayDrawNavBarColor != 0) {
                    this.backgroundPaint.setColor(BottomSheet.this.overlayDrawNavBarColor);
                    canvas.drawRect((float) (BottomSheet.this.containerView.getLeft() + BottomSheet.this.backgroundPaddingLeft), (((float) (getMeasuredHeight() - access$600)) + f) - BottomSheet.this.currentPanTranslationY, (float) (BottomSheet.this.containerView.getRight() - BottomSheet.this.backgroundPaddingLeft), ((float) getMeasuredHeight()) + f, this.backgroundPaint);
                }
            }
            BottomSheet bottomSheet5 = BottomSheet.this;
            if (bottomSheet5.drawNavigationBar && bottomSheet5.rightInset != 0 && BottomSheet.this.rightInset > BottomSheet.this.leftInset) {
                BottomSheet bottomSheet6 = BottomSheet.this;
                if (bottomSheet6.fullWidth) {
                    Point point = AndroidUtilities.displaySize;
                    if (point.x > point.y) {
                        int right = bottomSheet6.containerView.getRight();
                        BottomSheet bottomSheet7 = BottomSheet.this;
                        canvas.drawRect((float) (right - bottomSheet7.backgroundPaddingLeft), bottomSheet7.containerView.getTranslationY(), (float) (BottomSheet.this.containerView.getRight() + BottomSheet.this.rightInset), (float) getMeasuredHeight(), this.backgroundPaint);
                    }
                }
            }
            BottomSheet bottomSheet8 = BottomSheet.this;
            if (bottomSheet8.drawNavigationBar && bottomSheet8.leftInset != 0 && BottomSheet.this.leftInset > BottomSheet.this.rightInset) {
                BottomSheet bottomSheet9 = BottomSheet.this;
                if (bottomSheet9.fullWidth) {
                    Point point2 = AndroidUtilities.displaySize;
                    if (point2.x > point2.y) {
                        canvas.drawRect(0.0f, bottomSheet9.containerView.getTranslationY(), (float) (BottomSheet.this.containerView.getLeft() + BottomSheet.this.backgroundPaddingLeft), (float) getMeasuredHeight(), this.backgroundPaint);
                    }
                }
            }
            if (BottomSheet.this.containerView.getTranslationY() < 0.0f) {
                Paint paint = this.backgroundPaint;
                BottomSheet bottomSheet10 = BottomSheet.this;
                String str2 = bottomSheet10.behindKeyboardColorKey;
                paint.setColor(str2 != null ? Theme.getColor(str2) : bottomSheet10.behindKeyboardColor);
                int left = BottomSheet.this.containerView.getLeft();
                BottomSheet bottomSheet11 = BottomSheet.this;
                canvas.drawRect((float) (left + bottomSheet11.backgroundPaddingLeft), bottomSheet11.containerView.getY() + ((float) BottomSheet.this.containerView.getMeasuredHeight()), (float) (BottomSheet.this.containerView.getRight() - BottomSheet.this.backgroundPaddingLeft), (float) getMeasuredHeight(), this.backgroundPaint);
            }
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (!(BottomSheet.this.lastInsets == null || this.keyboardHeight == 0)) {
                Paint paint = this.backgroundPaint;
                BottomSheet bottomSheet = BottomSheet.this;
                String str = bottomSheet.behindKeyboardColorKey;
                paint.setColor(str != null ? Theme.getColor(str) : bottomSheet.behindKeyboardColor);
                float left = (float) (BottomSheet.this.containerView.getLeft() + BottomSheet.this.backgroundPaddingLeft);
                int measuredHeight = getMeasuredHeight() - this.keyboardHeight;
                BottomSheet bottomSheet2 = BottomSheet.this;
                int i = 0;
                float access$600 = (float) (measuredHeight - (bottomSheet2.drawNavigationBar ? bottomSheet2.bottomInset : 0));
                float right = (float) (BottomSheet.this.containerView.getRight() - BottomSheet.this.backgroundPaddingLeft);
                int measuredHeight2 = getMeasuredHeight();
                BottomSheet bottomSheet3 = BottomSheet.this;
                if (bottomSheet3.drawNavigationBar) {
                    i = bottomSheet3.bottomInset;
                }
                canvas.drawRect(left, access$600, right, (float) (measuredHeight2 - i), this.backgroundPaint);
            }
            BottomSheet.this.onContainerDraw(canvas);
        }
    }

    /* access modifiers changed from: private */
    public int getAdditionalMandatoryOffsets() {
        if (!this.calcMandatoryInsets) {
            return 0;
        }
        Insets systemGestureInsets = this.lastInsets.getSystemGestureInsets();
        if (this.keyboardVisible || !this.drawNavigationBar || systemGestureInsets == null) {
            return 0;
        }
        if (systemGestureInsets.left == 0 && systemGestureInsets.right == 0) {
            return 0;
        }
        return systemGestureInsets.bottom;
    }

    public void setCalcMandatoryInsets(boolean z) {
        this.calcMandatoryInsets = z;
        this.drawNavigationBar = z;
    }

    public static class BottomSheetCell extends FrameLayout {
        int currentType;
        /* access modifiers changed from: private */
        public ImageView imageView;
        /* access modifiers changed from: private */
        public TextView textView;

        public BottomSheetCell(Context context, int i) {
            super(context);
            this.currentType = i;
            setBackgroundDrawable(Theme.getSelectorDrawable(false));
            ImageView imageView2 = new ImageView(context);
            this.imageView = imageView2;
            imageView2.setScaleType(ImageView.ScaleType.CENTER);
            this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogIcon"), PorterDuff.Mode.MULTIPLY));
            int i2 = 5;
            addView(this.imageView, LayoutHelper.createFrame(56, 48, (LocaleController.isRTL ? 5 : 3) | 16));
            TextView textView2 = new TextView(context);
            this.textView = textView2;
            textView2.setLines(1);
            this.textView.setSingleLine(true);
            this.textView.setGravity(1);
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
            if (i == 0) {
                this.textView.setTextColor(Theme.getColor("dialogTextBlack"));
                this.textView.setTextSize(1, 16.0f);
                addView(this.textView, LayoutHelper.createFrame(-2, -2, (!LocaleController.isRTL ? 3 : i2) | 16));
            } else if (i == 1) {
                this.textView.setGravity(17);
                this.textView.setTextColor(Theme.getColor("dialogTextBlack"));
                this.textView.setTextSize(1, 14.0f);
                this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                addView(this.textView, LayoutHelper.createFrame(-1, -1.0f));
            } else if (i == 2) {
                this.textView.setGravity(17);
                this.textView.setTextColor(Theme.getColor("featuredStickers_buttonText"));
                this.textView.setTextSize(1, 14.0f);
                this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                this.textView.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed")));
                addView(this.textView, LayoutHelper.createFrame(-1, -1.0f, 0, 16.0f, 16.0f, 16.0f, 16.0f));
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            int i3 = this.currentType;
            int i4 = i3 == 2 ? 80 : 48;
            if (i3 == 0) {
                i = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM);
            }
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((float) i4), NUM));
        }

        public void setTextColor(int i) {
            this.textView.setTextColor(i);
        }

        public void setIconColor(int i) {
            this.imageView.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
        }

        public void setGravity(int i) {
            this.textView.setGravity(i);
        }

        public void setTextAndIcon(CharSequence charSequence, int i) {
            setTextAndIcon(charSequence, i, (Drawable) null, false);
        }

        public void setTextAndIcon(CharSequence charSequence, Drawable drawable) {
            setTextAndIcon(charSequence, 0, drawable, false);
        }

        public void setTextAndIcon(CharSequence charSequence, int i, Drawable drawable, boolean z) {
            this.textView.setText(charSequence);
            float f = 21.0f;
            float f2 = 16.0f;
            if (i == 0 && drawable == null) {
                this.imageView.setVisibility(4);
                TextView textView2 = this.textView;
                int dp = AndroidUtilities.dp(z ? 21.0f : 16.0f);
                if (!z) {
                    f = 16.0f;
                }
                textView2.setPadding(dp, 0, AndroidUtilities.dp(f), 0);
                return;
            }
            if (drawable != null) {
                this.imageView.setImageDrawable(drawable);
            } else {
                this.imageView.setImageResource(i);
            }
            this.imageView.setVisibility(0);
            if (z) {
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
    }

    public void setAllowNestedScroll(boolean z) {
        this.allowNestedScroll = z;
        if (!z) {
            this.containerView.setTranslationY(0.0f);
        }
    }

    public BottomSheet(Context context, boolean z) {
        super(context, NUM);
        int i = Build.VERSION.SDK_INT;
        if (i >= 30) {
            getWindow().addFlags(-NUM);
        } else if (i >= 21) {
            getWindow().addFlags(-NUM);
        }
        this.touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        Rect rect = new Rect();
        Drawable mutate = context.getResources().getDrawable(NUM).mutate();
        this.shadowDrawable = mutate;
        mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogBackground"), PorterDuff.Mode.MULTIPLY));
        this.shadowDrawable.getPadding(rect);
        this.backgroundPaddingLeft = rect.left;
        this.backgroundPaddingTop = rect.top;
        AnonymousClass2 r9 = new ContainerView(getContext()) {
            public boolean drawChild(Canvas canvas, View view, long j) {
                try {
                    return BottomSheet.this.allowDrawContent && super.drawChild(canvas, view, j);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                    return true;
                }
            }
        };
        this.container = r9;
        r9.setBackgroundDrawable(this.backDrawable);
        this.focusable = z;
        if (i >= 21) {
            this.container.setFitsSystemWindows(true);
            this.container.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                public final WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
                    return BottomSheet.this.lambda$new$0$BottomSheet(view, windowInsets);
                }
            });
            if (i >= 30) {
                this.container.setSystemUiVisibility(1792);
            } else {
                this.container.setSystemUiVisibility(1280);
            }
        }
        this.backDrawable.setAlpha(0);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ WindowInsets lambda$new$0$BottomSheet(View view, WindowInsets windowInsets) {
        int i;
        int systemWindowInsetTop = windowInsets.getSystemWindowInsetTop();
        if (!((systemWindowInsetTop == 0 && !AndroidUtilities.isInMultiwindow) || (i = this.statusBarHeight) == 0 || i == systemWindowInsetTop)) {
            this.statusBarHeight = systemWindowInsetTop;
        }
        this.lastInsets = windowInsets;
        view.requestLayout();
        if (Build.VERSION.SDK_INT >= 30) {
            return WindowInsets.CONSUMED;
        }
        return windowInsets.consumeSystemWindowInsets();
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        int i;
        super.onCreate(bundle);
        Window window = getWindow();
        window.setWindowAnimations(NUM);
        setContentView(this.container, new ViewGroup.LayoutParams(-1, -1));
        if (this.useLightStatusBar && Build.VERSION.SDK_INT >= 23 && Theme.getColor("actionBarDefault", (boolean[]) null, true) == -1) {
            this.container.setSystemUiVisibility(this.container.getSystemUiVisibility() | 8192);
        }
        if (this.useLightNavBar && Build.VERSION.SDK_INT >= 26) {
            AndroidUtilities.setLightNavigationBar(getWindow(), false);
        }
        if (this.containerView == null) {
            AnonymousClass3 r2 = new FrameLayout(getContext()) {
                public boolean hasOverlappingRendering() {
                    return false;
                }

                public void setTranslationY(float f) {
                    super.setTranslationY(f);
                    BottomSheet.this.onContainerTranslationYChanged(f);
                }
            };
            this.containerView = r2;
            r2.setBackgroundDrawable(this.shadowDrawable);
            this.containerView.setPadding(this.backgroundPaddingLeft, ((this.applyTopPadding ? AndroidUtilities.dp(8.0f) : 0) + this.backgroundPaddingTop) - 1, this.backgroundPaddingLeft, this.applyBottomPadding ? AndroidUtilities.dp(8.0f) : 0);
        }
        this.containerView.setVisibility(4);
        this.container.addView(this.containerView, 0, LayoutHelper.createFrame(-1, -2, 80));
        if (this.title != null) {
            TextView textView = new TextView(getContext());
            this.titleView = textView;
            textView.setLines(1);
            this.titleView.setSingleLine(true);
            this.titleView.setText(this.title);
            if (this.bigTitle) {
                this.titleView.setTextColor(Theme.getColor("dialogTextBlack"));
                this.titleView.setTextSize(1, 20.0f);
                this.titleView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                this.titleView.setPadding(AndroidUtilities.dp(21.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(21.0f), AndroidUtilities.dp(8.0f));
            } else {
                this.titleView.setTextColor(Theme.getColor("dialogTextGray2"));
                this.titleView.setTextSize(1, 16.0f);
                this.titleView.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), AndroidUtilities.dp(8.0f));
            }
            this.titleView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
            this.titleView.setGravity(16);
            this.containerView.addView(this.titleView, LayoutHelper.createFrame(-1, 48.0f));
            this.titleView.setOnTouchListener($$Lambda$BottomSheet$trMYiBYKZEsSZf_EBiGuVqT0ic8.INSTANCE);
            i = 48;
        } else {
            i = 0;
        }
        View view = this.customView;
        if (view != null) {
            if (view.getParent() != null) {
                ((ViewGroup) this.customView.getParent()).removeView(this.customView);
            }
            this.containerView.addView(this.customView, LayoutHelper.createFrame(-1, -2.0f, 51, 0.0f, (float) i, 0.0f, 0.0f));
        } else if (this.items != null) {
            int i2 = 0;
            while (true) {
                CharSequence[] charSequenceArr = this.items;
                if (i2 >= charSequenceArr.length) {
                    break;
                }
                if (charSequenceArr[i2] != null) {
                    BottomSheetCell bottomSheetCell = new BottomSheetCell(getContext(), 0);
                    CharSequence charSequence = this.items[i2];
                    int[] iArr = this.itemIcons;
                    bottomSheetCell.setTextAndIcon(charSequence, iArr != null ? iArr[i2] : 0, (Drawable) null, this.bigTitle);
                    this.containerView.addView(bottomSheetCell, LayoutHelper.createFrame(-1, 48.0f, 51, 0.0f, (float) i, 0.0f, 0.0f));
                    i += 48;
                    bottomSheetCell.setTag(Integer.valueOf(i2));
                    bottomSheetCell.setOnClickListener(new View.OnClickListener() {
                        public final void onClick(View view) {
                            BottomSheet.this.lambda$onCreate$2$BottomSheet(view);
                        }
                    });
                    this.itemViews.add(bottomSheetCell);
                }
                i2++;
            }
        }
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = -1;
        attributes.gravity = 51;
        attributes.dimAmount = 0.0f;
        int i3 = attributes.flags & -3;
        attributes.flags = i3;
        if (this.focusable) {
            attributes.softInputMode = 16;
        } else {
            attributes.flags = i3 | 131072;
        }
        if (this.isFullscreen) {
            if (Build.VERSION.SDK_INT >= 21) {
                attributes.flags |= -NUM;
            }
            attributes.flags |= 1024;
            this.container.setSystemUiVisibility(1284);
        }
        attributes.height = -1;
        if (Build.VERSION.SDK_INT >= 28) {
            attributes.layoutInDisplayCutoutMode = 1;
        }
        window.setAttributes(attributes);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreate$2 */
    public /* synthetic */ void lambda$onCreate$2$BottomSheet(View view) {
        dismissWithButtonClick(((Integer) view.getTag()).intValue());
    }

    public void setUseLightStatusBar(boolean z) {
        this.useLightStatusBar = z;
        if (Build.VERSION.SDK_INT >= 23) {
            int color = Theme.getColor("actionBarDefault", (boolean[]) null, true);
            int systemUiVisibility = this.container.getSystemUiVisibility();
            this.container.setSystemUiVisibility((!this.useLightStatusBar || color != -1) ? systemUiVisibility & -8193 : systemUiVisibility | 8192);
        }
    }

    public void setFocusable(boolean z) {
        if (this.focusable != z) {
            this.focusable = z;
            Window window = getWindow();
            WindowManager.LayoutParams attributes = window.getAttributes();
            if (this.focusable) {
                attributes.softInputMode = 16;
                attributes.flags &= -131073;
            } else {
                attributes.softInputMode = 48;
                attributes.flags |= 131072;
            }
            window.setAttributes(attributes);
        }
    }

    public void setShowWithoutAnimation(boolean z) {
        this.showWithoutAnimation = z;
    }

    public void setBackgroundColor(int i) {
        this.shadowDrawable.setColorFilter(i, PorterDuff.Mode.MULTIPLY);
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
        int i2 = Build.VERSION.SDK_INT;
        if (i2 >= 18) {
            this.layoutCount = 2;
            ViewGroup viewGroup = this.containerView;
            if (i2 >= 21) {
                i = AndroidUtilities.statusBarHeight;
            }
            viewGroup.setTranslationY((float) (i + viewGroup.getMeasuredHeight()));
            AnonymousClass4 r0 = new Runnable() {
                public void run() {
                    BottomSheet bottomSheet = BottomSheet.this;
                    if (bottomSheet.startAnimationRunnable == this && !bottomSheet.dismissed) {
                        BottomSheet bottomSheet2 = BottomSheet.this;
                        bottomSheet2.startAnimationRunnable = null;
                        bottomSheet2.startOpenAnimation();
                    }
                }
            };
            this.startAnimationRunnable = r0;
            AndroidUtilities.runOnUIThread(r0, 150);
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

    public void setAllowDrawContent(boolean z) {
        if (this.allowDrawContent != z) {
            this.allowDrawContent = z;
            this.container.setBackgroundDrawable(z ? this.backDrawable : null);
            this.container.invalidate();
        }
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return this.canDismissWithSwipe;
    }

    public void setCustomView(View view) {
        this.customView = view;
    }

    public void setTitle(CharSequence charSequence) {
        setTitle(charSequence, false);
    }

    public void setTitle(CharSequence charSequence, boolean z) {
        this.title = charSequence;
        this.bigTitle = z;
    }

    public void setApplyTopPadding(boolean z) {
        this.applyTopPadding = z;
    }

    public void setApplyBottomPadding(boolean z) {
        this.applyBottomPadding = z;
    }

    public TextView getTitleView() {
        return this.titleView;
    }

    private void cancelSheetAnimation() {
        AnimatorSet animatorSet = this.currentSheetAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.currentSheetAnimation = null;
            this.currentSheetAnimationType = 0;
        }
    }

    public void setOnHideListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onHideListener = onDismissListener;
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
                viewGroup.setTranslationY((float) viewGroup.getMeasuredHeight());
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
                    public void onAnimationEnd(Animator animator) {
                        AnimatorSet animatorSet = BottomSheet.this.currentSheetAnimation;
                        if (animatorSet != null && animatorSet.equals(animator)) {
                            BottomSheet bottomSheet = BottomSheet.this;
                            bottomSheet.currentSheetAnimation = null;
                            bottomSheet.currentSheetAnimationType = 0;
                            if (bottomSheet.delegate != null) {
                                BottomSheet.this.delegate.onOpenAnimationEnd();
                            }
                            if (BottomSheet.this.useHardwareLayer) {
                                BottomSheet.this.container.setLayerType(0, (Paint) null);
                            }
                            BottomSheet bottomSheet2 = BottomSheet.this;
                            if (bottomSheet2.isFullscreen) {
                                WindowManager.LayoutParams attributes = bottomSheet2.getWindow().getAttributes();
                                attributes.flags &= -1025;
                                BottomSheet.this.getWindow().setAttributes(attributes);
                            }
                        }
                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 512);
                    }

                    public void onAnimationCancel(Animator animator) {
                        AnimatorSet animatorSet = BottomSheet.this.currentSheetAnimation;
                        if (animatorSet != null && animatorSet.equals(animator)) {
                            BottomSheet bottomSheet = BottomSheet.this;
                            bottomSheet.currentSheetAnimation = null;
                            bottomSheet.currentSheetAnimationType = 0;
                        }
                    }
                });
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 512);
                this.currentSheetAnimation.start();
            }
        }
    }

    public void setDelegate(BottomSheetDelegateInterface bottomSheetDelegateInterface) {
        this.delegate = bottomSheetDelegateInterface;
    }

    public FrameLayout getContainer() {
        return this.container;
    }

    public ViewGroup getSheetContainer() {
        return this.containerView;
    }

    public void setDimBehind(boolean z) {
        this.dimBehind = z;
    }

    public void setDimBehindAlpha(int i) {
        this.dimBehindAlpha = i;
    }

    public void setItemColor(int i, int i2, int i3) {
        if (i >= 0 && i < this.itemViews.size()) {
            BottomSheetCell bottomSheetCell = this.itemViews.get(i);
            bottomSheetCell.textView.setTextColor(i2);
            bottomSheetCell.imageView.setColorFilter(new PorterDuffColorFilter(i3, PorterDuff.Mode.MULTIPLY));
        }
    }

    public ArrayList<BottomSheetCell> getItemViews() {
        return this.itemViews;
    }

    public void setItems(CharSequence[] charSequenceArr, int[] iArr, DialogInterface.OnClickListener onClickListener2) {
        this.items = charSequenceArr;
        this.itemIcons = iArr;
        this.onClickListener = onClickListener2;
    }

    public void setTitleColor(int i) {
        TextView textView = this.titleView;
        if (textView != null) {
            textView.setTextColor(i);
        }
    }

    public boolean isDismissed() {
        return this.dismissed;
    }

    public void dismissWithButtonClick(final int i) {
        if (!this.dismissed) {
            this.dismissed = true;
            cancelSheetAnimation();
            this.currentSheetAnimationType = 2;
            AnimatorSet animatorSet = new AnimatorSet();
            this.currentSheetAnimation = animatorSet;
            ViewGroup viewGroup = this.containerView;
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(viewGroup, View.TRANSLATION_Y, new float[]{(float) (viewGroup.getMeasuredHeight() + AndroidUtilities.dp(10.0f))}), ObjectAnimator.ofInt(this.backDrawable, AnimationProperties.COLOR_DRAWABLE_ALPHA, new int[]{0})});
            this.currentSheetAnimation.setDuration(180);
            this.currentSheetAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            this.currentSheetAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    AnimatorSet animatorSet = BottomSheet.this.currentSheetAnimation;
                    if (animatorSet != null && animatorSet.equals(animator)) {
                        BottomSheet bottomSheet = BottomSheet.this;
                        bottomSheet.currentSheetAnimation = null;
                        bottomSheet.currentSheetAnimationType = 0;
                        if (bottomSheet.onClickListener != null) {
                            BottomSheet.this.onClickListener.onClick(BottomSheet.this, i);
                        }
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public final void run() {
                                BottomSheet.AnonymousClass6.this.lambda$onAnimationEnd$0$BottomSheet$6();
                            }
                        });
                    }
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 512);
                }

                /* access modifiers changed from: private */
                /* renamed from: lambda$onAnimationEnd$0 */
                public /* synthetic */ void lambda$onAnimationEnd$0$BottomSheet$6() {
                    try {
                        BottomSheet.super.dismiss();
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    AnimatorSet animatorSet = BottomSheet.this.currentSheetAnimation;
                    if (animatorSet != null && animatorSet.equals(animator)) {
                        BottomSheet bottomSheet = BottomSheet.this;
                        bottomSheet.currentSheetAnimation = null;
                        bottomSheet.currentSheetAnimationType = 0;
                    }
                }
            });
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 512);
            this.currentSheetAnimation.start();
        }
    }

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (this.dismissed) {
            return false;
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    public void dismiss() {
        long j;
        BottomSheetDelegateInterface bottomSheetDelegateInterface = this.delegate;
        if ((bottomSheetDelegateInterface == null || bottomSheetDelegateInterface.canDismiss()) && !this.dismissed) {
            this.dismissed = true;
            DialogInterface.OnDismissListener onDismissListener = this.onHideListener;
            if (onDismissListener != null) {
                onDismissListener.onDismiss(this);
            }
            cancelSheetAnimation();
            if (!this.allowCustomAnimation || !onCustomCloseAnimation()) {
                this.currentSheetAnimationType = 2;
                AnimatorSet animatorSet = new AnimatorSet();
                this.currentSheetAnimation = animatorSet;
                ViewGroup viewGroup = this.containerView;
                animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(viewGroup, View.TRANSLATION_Y, new float[]{(float) (viewGroup.getMeasuredHeight() + this.container.keyboardHeight + AndroidUtilities.dp(10.0f))}), ObjectAnimator.ofInt(this.backDrawable, AnimationProperties.COLOR_DRAWABLE_ALPHA, new int[]{0})});
                if (this.useFastDismiss) {
                    float measuredHeight = (float) this.containerView.getMeasuredHeight();
                    j = (long) Math.max(60, (int) (((measuredHeight - this.containerView.getTranslationY()) * 250.0f) / measuredHeight));
                    this.currentSheetAnimation.setDuration(j);
                    this.useFastDismiss = false;
                } else {
                    j = 250;
                    this.currentSheetAnimation.setDuration(250);
                }
                this.currentSheetAnimation.setInterpolator(CubicBezierInterpolator.DEFAULT);
                this.currentSheetAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        AnimatorSet animatorSet = BottomSheet.this.currentSheetAnimation;
                        if (animatorSet != null && animatorSet.equals(animator)) {
                            BottomSheet bottomSheet = BottomSheet.this;
                            bottomSheet.currentSheetAnimation = null;
                            bottomSheet.currentSheetAnimationType = 0;
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public final void run() {
                                    BottomSheet.AnonymousClass7.this.lambda$onAnimationEnd$0$BottomSheet$7();
                                }
                            });
                        }
                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 512);
                    }

                    /* access modifiers changed from: private */
                    /* renamed from: lambda$onAnimationEnd$0 */
                    public /* synthetic */ void lambda$onAnimationEnd$0$BottomSheet$7() {
                        try {
                            BottomSheet.this.dismissInternal();
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                    }

                    public void onAnimationCancel(Animator animator) {
                        AnimatorSet animatorSet = BottomSheet.this.currentSheetAnimation;
                        if (animatorSet != null && animatorSet.equals(animator)) {
                            BottomSheet bottomSheet = BottomSheet.this;
                            bottomSheet.currentSheetAnimation = null;
                            bottomSheet.currentSheetAnimationType = 0;
                        }
                    }
                });
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 512);
                this.currentSheetAnimation.start();
            } else {
                j = 0;
            }
            Bulletin visibleBulletin = Bulletin.getVisibleBulletin();
            if (visibleBulletin != null && visibleBulletin.isShowing()) {
                if (j > 0) {
                    visibleBulletin.hide((long) (((float) j) * 0.6f));
                } else {
                    visibleBulletin.hide();
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

    public static class Builder {
        private BottomSheet bottomSheet;

        public Builder(Context context) {
            this.bottomSheet = new BottomSheet(context, false);
        }

        public Builder(Context context, boolean z) {
            this.bottomSheet = new BottomSheet(context, z);
        }

        public Builder setItems(CharSequence[] charSequenceArr, DialogInterface.OnClickListener onClickListener) {
            CharSequence[] unused = this.bottomSheet.items = charSequenceArr;
            DialogInterface.OnClickListener unused2 = this.bottomSheet.onClickListener = onClickListener;
            return this;
        }

        public Builder setItems(CharSequence[] charSequenceArr, int[] iArr, DialogInterface.OnClickListener onClickListener) {
            CharSequence[] unused = this.bottomSheet.items = charSequenceArr;
            int[] unused2 = this.bottomSheet.itemIcons = iArr;
            DialogInterface.OnClickListener unused3 = this.bottomSheet.onClickListener = onClickListener;
            return this;
        }

        public Builder setCustomView(View view) {
            View unused = this.bottomSheet.customView = view;
            return this;
        }

        public Builder setTitle(CharSequence charSequence) {
            return setTitle(charSequence, false);
        }

        public Builder setTitle(CharSequence charSequence, boolean z) {
            CharSequence unused = this.bottomSheet.title = charSequence;
            boolean unused2 = this.bottomSheet.bigTitle = z;
            return this;
        }

        public BottomSheet create() {
            return this.bottomSheet;
        }

        public BottomSheet setDimBehind(boolean z) {
            boolean unused = this.bottomSheet.dimBehind = z;
            return this.bottomSheet;
        }

        public BottomSheet show() {
            this.bottomSheet.show();
            return this.bottomSheet;
        }

        public Builder setUseHardwareLayer(boolean z) {
            boolean unused = this.bottomSheet.useHardwareLayer = z;
            return this;
        }

        public Builder setDelegate(BottomSheetDelegate bottomSheetDelegate) {
            this.bottomSheet.setDelegate(bottomSheetDelegate);
            return this;
        }

        public Builder setApplyTopPadding(boolean z) {
            boolean unused = this.bottomSheet.applyTopPadding = z;
            return this;
        }

        public Builder setApplyBottomPadding(boolean z) {
            boolean unused = this.bottomSheet.applyBottomPadding = z;
            return this;
        }

        public Runnable getDismissRunnable() {
            return this.bottomSheet.dismissRunnable;
        }
    }

    public int getLeftInset() {
        WindowInsets windowInsets = this.lastInsets;
        if (windowInsets == null || Build.VERSION.SDK_INT < 21) {
            return 0;
        }
        return windowInsets.getSystemWindowInsetLeft();
    }

    public int getRightInset() {
        WindowInsets windowInsets = this.lastInsets;
        if (windowInsets == null || Build.VERSION.SDK_INT < 21) {
            return 0;
        }
        return windowInsets.getSystemWindowInsetRight();
    }

    public int getBottomInset() {
        return this.bottomInset;
    }

    public void setCurrentPanTranslationY(float f) {
        this.currentPanTranslationY = f;
        this.container.invalidate();
    }

    public void setOverlayNavBarColor(int i) {
        this.overlayDrawNavBarColor = i;
        ContainerView containerView2 = this.container;
        if (containerView2 != null) {
            containerView2.invalidate();
        }
        if (Color.alpha(i) > 120) {
            AndroidUtilities.setLightStatusBar(getWindow(), false);
            AndroidUtilities.setLightNavigationBar(getWindow(), false);
            return;
        }
        AndroidUtilities.setLightNavigationBar(getWindow(), !this.useLightNavBar);
        AndroidUtilities.setLightStatusBar(getWindow(), !this.useLightStatusBar);
    }

    public ViewGroup getContainerView() {
        return this.containerView;
    }

    public int getCurrentAccount() {
        return this.currentAccount;
    }
}
