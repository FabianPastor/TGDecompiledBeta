package org.telegram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
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
    protected ColorDrawable backDrawable = new ColorDrawable(-16777216);
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
    protected ContainerView container;
    /* access modifiers changed from: protected */
    public ViewGroup containerView;
    /* access modifiers changed from: protected */
    public int currentAccount = UserConfig.selectedAccount;
    /* access modifiers changed from: private */
    public float currentPanTranslationY;
    protected AnimatorSet currentSheetAnimation;
    protected int currentSheetAnimationType;
    /* access modifiers changed from: private */
    public View customView;
    /* access modifiers changed from: private */
    public BottomSheetDelegateInterface delegate;
    /* access modifiers changed from: private */
    public boolean dimBehind = true;
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
    /* access modifiers changed from: protected */
    public boolean keyboardVisible;
    /* access modifiers changed from: private */
    public WindowInsets lastInsets;
    /* access modifiers changed from: private */
    public int layoutCount;
    protected View nestedScrollChild;
    /* access modifiers changed from: private */
    public DialogInterface.OnClickListener onClickListener;
    protected Interpolator openInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
    protected Drawable shadowDrawable;
    private boolean showWithoutAnimation;
    protected Runnable startAnimationRunnable;
    /* access modifiers changed from: private */
    public CharSequence title;
    private TextView titleView;
    /* access modifiers changed from: private */
    public int touchSlop;
    /* access modifiers changed from: private */
    public boolean useFastDismiss;
    /* access modifiers changed from: private */
    public boolean useHardwareLayer = true;
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

    static /* synthetic */ int access$910(BottomSheet bottomSheet) {
        int i = bottomSheet.layoutCount;
        bottomSheet.layoutCount = i - 1;
        return i;
    }

    public void setDisableScroll(boolean z) {
        this.disableScroll = z;
    }

    protected class ContainerView extends FrameLayout implements NestedScrollingParent {
        private Paint backgroundPaint = new Paint();
        /* access modifiers changed from: private */
        public AnimatorSet currentAnimation = null;
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
            View view3 = BottomSheet.this.nestedScrollChild;
            if ((view3 == null || view == view3) && !BottomSheet.this.dismissed) {
                BottomSheet bottomSheet = BottomSheet.this;
                return bottomSheet.allowNestedScroll && i == 2 && !bottomSheet.canDismissWithSwipe();
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
                    }
                } else if (motionEvent == null || (motionEvent != null && motionEvent.getPointerId(0) == this.startedTrackingPointerId && (motionEvent.getAction() == 3 || motionEvent.getAction() == 1 || motionEvent.getAction() == 6))) {
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
        /* JADX WARNING: Removed duplicated region for block: B:41:0x00dd  */
        /* JADX WARNING: Removed duplicated region for block: B:49:0x010a  */
        /* JADX WARNING: Removed duplicated region for block: B:52:0x0113  */
        /* JADX WARNING: Removed duplicated region for block: B:65:0x0186  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onMeasure(int r12, int r13) {
            /*
                r11 = this;
                int r12 = android.view.View.MeasureSpec.getSize(r12)
                int r13 = android.view.View.MeasureSpec.getSize(r13)
                android.view.View r0 = r11.getRootView()
                android.graphics.Rect r1 = r11.rect
                r11.getWindowVisibleDisplayFrame(r1)
                android.graphics.Rect r1 = r11.rect
                int r2 = r1.bottom
                r3 = 1101004800(0x41a00000, float:20.0)
                r4 = 0
                if (r2 == 0) goto L_0x0055
                int r1 = r1.top
                if (r1 == 0) goto L_0x0055
                int r1 = r0.getHeight()
                android.graphics.Rect r2 = r11.rect
                int r2 = r2.top
                if (r2 == 0) goto L_0x002b
                int r2 = org.telegram.messenger.AndroidUtilities.statusBarHeight
                goto L_0x002c
            L_0x002b:
                r2 = 0
            L_0x002c:
                int r1 = r1 - r2
                int r0 = org.telegram.messenger.AndroidUtilities.getViewInset(r0)
                int r1 = r1 - r0
                android.graphics.Rect r0 = r11.rect
                int r2 = r0.bottom
                int r0 = r0.top
                int r2 = r2 - r0
                int r1 = r1 - r2
                int r0 = java.lang.Math.max(r4, r1)
                r11.keyboardHeight = r0
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r3)
                if (r0 >= r1) goto L_0x0048
                r11.keyboardHeight = r4
            L_0x0048:
                org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.ActionBar.BottomSheet.this
                int r1 = r0.bottomInset
                int r2 = r11.keyboardHeight
                int r1 = r1 - r2
                int unused = r0.bottomInset = r1
                goto L_0x0057
            L_0x0055:
                r11.keyboardHeight = r4
            L_0x0057:
                org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.ActionBar.BottomSheet.this
                int r1 = r11.keyboardHeight
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r3)
                r3 = 1
                if (r1 <= r2) goto L_0x0064
                r1 = 1
                goto L_0x0065
            L_0x0064:
                r1 = 0
            L_0x0065:
                r0.keyboardVisible = r1
                org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.WindowInsets r0 = r0.lastInsets
                r1 = 29
                r2 = 21
                if (r0 == 0) goto L_0x00bf
                int r0 = android.os.Build.VERSION.SDK_INT
                if (r0 < r2) goto L_0x00bf
                org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.WindowInsets r5 = r0.lastInsets
                int r5 = r5.getSystemWindowInsetBottom()
                int unused = r0.bottomInset = r5
                int r0 = android.os.Build.VERSION.SDK_INT
                if (r0 < r1) goto L_0x0098
                org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.ActionBar.BottomSheet.this
                int r5 = r0.bottomInset
                org.telegram.ui.ActionBar.BottomSheet r6 = org.telegram.ui.ActionBar.BottomSheet.this
                int r6 = r6.getAdditionalMandatoryOffsets()
                int r5 = r5 + r6
                int unused = r0.bottomInset = r5
            L_0x0098:
                org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.ActionBar.BottomSheet.this
                boolean r5 = r0.keyboardVisible
                if (r5 == 0) goto L_0x00b2
                android.graphics.Rect r5 = r11.rect
                int r6 = r5.bottom
                if (r6 == 0) goto L_0x00b2
                int r5 = r5.top
                if (r5 == 0) goto L_0x00b2
                int r5 = r0.bottomInset
                int r6 = r11.keyboardHeight
                int r5 = r5 - r6
                int unused = r0.bottomInset = r5
            L_0x00b2:
                org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.ActionBar.BottomSheet.this
                boolean r5 = r0.drawNavigationBar
                if (r5 != 0) goto L_0x00bf
                int r0 = r0.bottomInset
                int r0 = r13 - r0
                goto L_0x00c0
            L_0x00bf:
                r0 = r13
            L_0x00c0:
                r11.setMeasuredDimension(r12, r0)
                org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.WindowInsets r0 = r0.lastInsets
                if (r0 == 0) goto L_0x00e5
                int r0 = android.os.Build.VERSION.SDK_INT
                if (r0 < r2) goto L_0x00e5
                org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.WindowInsets r0 = r0.lastInsets
                int r0 = r0.getSystemWindowInsetBottom()
                int r5 = android.os.Build.VERSION.SDK_INT
                if (r5 < r1) goto L_0x00e4
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                int r1 = r1.getAdditionalMandatoryOffsets()
                int r0 = r0 + r1
            L_0x00e4:
                int r13 = r13 - r0
            L_0x00e5:
                org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.WindowInsets r0 = r0.lastInsets
                if (r0 == 0) goto L_0x0107
                int r0 = android.os.Build.VERSION.SDK_INT
                if (r0 < r2) goto L_0x0107
                org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.WindowInsets r0 = r0.lastInsets
                int r0 = r0.getSystemWindowInsetRight()
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.WindowInsets r1 = r1.lastInsets
                int r1 = r1.getSystemWindowInsetLeft()
                int r0 = r0 + r1
                int r12 = r12 - r0
            L_0x0107:
                if (r12 >= r13) goto L_0x010a
                goto L_0x010b
            L_0x010a:
                r3 = 0
            L_0x010b:
                org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.ViewGroup r1 = r0.containerView
                r2 = 1073741824(0x40000000, float:2.0)
                if (r1 == 0) goto L_0x0180
                boolean r5 = r0.fullWidth
                r6 = -2147483648(0xfffffffvar_, float:-0.0)
                if (r5 != 0) goto L_0x0170
                boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
                r1 = 1061997773(0x3f4ccccd, float:0.8)
                if (r0 == 0) goto L_0x013c
                android.graphics.Point r0 = org.telegram.messenger.AndroidUtilities.displaySize
                int r3 = r0.x
                int r0 = r0.y
                int r0 = java.lang.Math.min(r3, r0)
                float r0 = (float) r0
                float r0 = r0 * r1
                int r0 = (int) r0
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                int r1 = r1.backgroundPaddingLeft
                int r1 = r1 * 2
                int r0 = r0 + r1
                int r0 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r2)
                goto L_0x0164
            L_0x013c:
                if (r3 == 0) goto L_0x0146
                org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.ActionBar.BottomSheet.this
                int r0 = r0.backgroundPaddingLeft
                int r0 = r0 * 2
                int r0 = r0 + r12
                goto L_0x0160
            L_0x0146:
                float r0 = (float) r12
                float r0 = r0 * r1
                r1 = 1139802112(0x43var_, float:480.0)
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                int r1 = java.lang.Math.min(r1, r12)
                float r1 = (float) r1
                float r0 = java.lang.Math.max(r0, r1)
                int r0 = (int) r0
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                int r1 = r1.backgroundPaddingLeft
                int r1 = r1 * 2
                int r0 = r0 + r1
            L_0x0160:
                int r0 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r2)
            L_0x0164:
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.ViewGroup r1 = r1.containerView
                int r3 = android.view.View.MeasureSpec.makeMeasureSpec(r13, r6)
                r1.measure(r0, r3)
                goto L_0x0180
            L_0x0170:
                int r0 = r0.backgroundPaddingLeft
                int r0 = r0 * 2
                int r0 = r0 + r12
                int r0 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r2)
                int r3 = android.view.View.MeasureSpec.makeMeasureSpec(r13, r6)
                r1.measure(r0, r3)
            L_0x0180:
                int r0 = r11.getChildCount()
            L_0x0184:
                if (r4 >= r0) goto L_0x01b0
                android.view.View r6 = r11.getChildAt(r4)
                int r1 = r6.getVisibility()
                r3 = 8
                if (r1 == r3) goto L_0x01ad
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.ViewGroup r3 = r1.containerView
                if (r6 != r3) goto L_0x0199
                goto L_0x01ad
            L_0x0199:
                boolean r1 = r1.onCustomMeasure(r6, r12, r13)
                if (r1 != 0) goto L_0x01ad
                int r7 = android.view.View.MeasureSpec.makeMeasureSpec(r12, r2)
                r8 = 0
                int r9 = android.view.View.MeasureSpec.makeMeasureSpec(r13, r2)
                r10 = 0
                r5 = r11
                r5.measureChildWithMargins(r6, r7, r8, r9, r10)
            L_0x01ad:
                int r4 = r4 + 1
                goto L_0x0184
            L_0x01b0:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.BottomSheet.ContainerView.onMeasure(int, int):void");
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Removed duplicated region for block: B:49:0x0114  */
        /* JADX WARNING: Removed duplicated region for block: B:53:0x0121  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onLayout(boolean r15, int r16, int r17, int r18, int r19) {
            /*
                r14 = this;
                r0 = r14
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                org.telegram.ui.ActionBar.BottomSheet.access$910(r1)
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.ViewGroup r1 = r1.containerView
                r7 = 21
                r8 = 0
                if (r1 == 0) goto L_0x00a6
                int r2 = r19 - r17
                int r1 = r1.getMeasuredHeight()
                int r2 = r2 - r1
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.WindowInsets r1 = r1.lastInsets
                if (r1 == 0) goto L_0x0066
                int r1 = android.os.Build.VERSION.SDK_INT
                if (r1 < r7) goto L_0x0066
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.WindowInsets r1 = r1.lastInsets
                int r1 = r1.getSystemWindowInsetLeft()
                int r1 = r16 + r1
                org.telegram.ui.ActionBar.BottomSheet r3 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.WindowInsets r3 = r3.lastInsets
                int r3 = r3.getSystemWindowInsetRight()
                int r3 = r18 - r3
                org.telegram.ui.ActionBar.BottomSheet r4 = org.telegram.ui.ActionBar.BottomSheet.this
                boolean r5 = r4.useSmoothKeyboard
                if (r5 == 0) goto L_0x0042
                r2 = 0
                goto L_0x006a
            L_0x0042:
                android.view.WindowInsets r4 = r4.lastInsets
                int r4 = r4.getSystemWindowInsetBottom()
                org.telegram.ui.ActionBar.BottomSheet r5 = org.telegram.ui.ActionBar.BottomSheet.this
                boolean r6 = r5.drawNavigationBar
                if (r6 == 0) goto L_0x0052
                r5 = 0
                goto L_0x0056
            L_0x0052:
                int r5 = r5.bottomInset
            L_0x0056:
                int r4 = r4 - r5
                int r2 = r2 - r4
                int r4 = android.os.Build.VERSION.SDK_INT
                r5 = 29
                if (r4 < r5) goto L_0x006a
                org.telegram.ui.ActionBar.BottomSheet r4 = org.telegram.ui.ActionBar.BottomSheet.this
                int r4 = r4.getAdditionalMandatoryOffsets()
                int r2 = r2 - r4
                goto L_0x006a
            L_0x0066:
                r1 = r16
                r3 = r18
            L_0x006a:
                int r4 = r3 - r1
                org.telegram.ui.ActionBar.BottomSheet r5 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.ViewGroup r5 = r5.containerView
                int r5 = r5.getMeasuredWidth()
                int r4 = r4 - r5
                int r4 = r4 / 2
                org.telegram.ui.ActionBar.BottomSheet r5 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.WindowInsets r5 = r5.lastInsets
                if (r5 == 0) goto L_0x008e
                int r5 = android.os.Build.VERSION.SDK_INT
                if (r5 < r7) goto L_0x008e
                org.telegram.ui.ActionBar.BottomSheet r5 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.WindowInsets r5 = r5.lastInsets
                int r5 = r5.getSystemWindowInsetLeft()
                int r4 = r4 + r5
            L_0x008e:
                org.telegram.ui.ActionBar.BottomSheet r5 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.ViewGroup r5 = r5.containerView
                int r6 = r5.getMeasuredWidth()
                int r6 = r6 + r4
                org.telegram.ui.ActionBar.BottomSheet r9 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.ViewGroup r9 = r9.containerView
                int r9 = r9.getMeasuredHeight()
                int r9 = r9 + r2
                r5.layout(r4, r2, r6, r9)
                r9 = r1
                r10 = r3
                goto L_0x00aa
            L_0x00a6:
                r9 = r16
                r10 = r18
            L_0x00aa:
                int r11 = r14.getChildCount()
                r12 = 0
            L_0x00af:
                if (r12 >= r11) goto L_0x014d
                android.view.View r13 = r14.getChildAt(r12)
                int r1 = r13.getVisibility()
                r2 = 8
                if (r1 == r2) goto L_0x0149
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.ViewGroup r2 = r1.containerView
                if (r13 != r2) goto L_0x00c5
                goto L_0x0149
            L_0x00c5:
                boolean r2 = r1.drawNavigationBar
                if (r2 == 0) goto L_0x00ce
                int r2 = r1.bottomInset
                goto L_0x00cf
            L_0x00ce:
                r2 = 0
            L_0x00cf:
                int r6 = r19 - r2
                r2 = r13
                r3 = r9
                r4 = r17
                r5 = r10
                boolean r1 = r1.onCustomLayout(r2, r3, r4, r5, r6)
                if (r1 != 0) goto L_0x0149
                android.view.ViewGroup$LayoutParams r1 = r13.getLayoutParams()
                android.widget.FrameLayout$LayoutParams r1 = (android.widget.FrameLayout.LayoutParams) r1
                int r2 = r13.getMeasuredWidth()
                int r3 = r13.getMeasuredHeight()
                int r4 = r1.gravity
                r5 = -1
                if (r4 != r5) goto L_0x00f1
                r4 = 51
            L_0x00f1:
                r5 = r4 & 7
                r4 = r4 & 112(0x70, float:1.57E-43)
                r5 = r5 & 7
                r6 = 1
                if (r5 == r6) goto L_0x0105
                r6 = 5
                if (r5 == r6) goto L_0x0100
                int r5 = r1.leftMargin
                goto L_0x0110
            L_0x0100:
                int r5 = r10 - r2
                int r6 = r1.rightMargin
                goto L_0x010f
            L_0x0105:
                int r5 = r10 - r9
                int r5 = r5 - r2
                int r5 = r5 / 2
                int r6 = r1.leftMargin
                int r5 = r5 + r6
                int r6 = r1.rightMargin
            L_0x010f:
                int r5 = r5 - r6
            L_0x0110:
                r6 = 16
                if (r4 == r6) goto L_0x0121
                r6 = 80
                if (r4 == r6) goto L_0x011b
                int r1 = r1.topMargin
                goto L_0x012d
            L_0x011b:
                int r4 = r19 - r17
                int r4 = r4 - r3
                int r1 = r1.bottomMargin
                goto L_0x012b
            L_0x0121:
                int r4 = r19 - r17
                int r4 = r4 - r3
                int r4 = r4 / 2
                int r6 = r1.topMargin
                int r4 = r4 + r6
                int r1 = r1.bottomMargin
            L_0x012b:
                int r1 = r4 - r1
            L_0x012d:
                org.telegram.ui.ActionBar.BottomSheet r4 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.WindowInsets r4 = r4.lastInsets
                if (r4 == 0) goto L_0x0144
                int r4 = android.os.Build.VERSION.SDK_INT
                if (r4 < r7) goto L_0x0144
                org.telegram.ui.ActionBar.BottomSheet r4 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.WindowInsets r4 = r4.lastInsets
                int r4 = r4.getSystemWindowInsetLeft()
                int r5 = r5 + r4
            L_0x0144:
                int r2 = r2 + r5
                int r3 = r3 + r1
                r13.layout(r5, r1, r2, r3)
            L_0x0149:
                int r12 = r12 + 1
                goto L_0x00af
            L_0x014d:
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                int r1 = r1.layoutCount
                if (r1 != 0) goto L_0x016a
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                java.lang.Runnable r1 = r1.startAnimationRunnable
                if (r1 == 0) goto L_0x016a
                org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r1)
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                java.lang.Runnable r1 = r1.startAnimationRunnable
                r1.run()
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                r2 = 0
                r1.startAnimationRunnable = r2
            L_0x016a:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.BottomSheet.ContainerView.onLayout(boolean, int, int, int, int):void");
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
            super.dispatchDraw(canvas);
            BottomSheet bottomSheet = BottomSheet.this;
            float f = 0.0f;
            if ((bottomSheet.drawNavigationBar && bottomSheet.bottomInset != 0) || BottomSheet.this.currentPanTranslationY != 0.0f) {
                if (Build.VERSION.SDK_INT >= 26) {
                    this.backgroundPaint.setColor(Theme.getColor("windowBackgroundGray"));
                } else {
                    this.backgroundPaint.setColor(-16777216);
                }
                if (Build.VERSION.SDK_INT >= 29 && BottomSheet.this.getAdditionalMandatoryOffsets() > 0) {
                    float measuredHeight = ((float) BottomSheet.this.containerView.getMeasuredHeight()) - BottomSheet.this.containerView.getTranslationY();
                    if (BottomSheet.this.currentSheetAnimationType == 1) {
                        measuredHeight *= 0.1f;
                    }
                    f = Math.max(0.0f, ((float) BottomSheet.this.bottomInset) - measuredHeight);
                }
                BottomSheet bottomSheet2 = BottomSheet.this;
                canvas.drawRect((float) (BottomSheet.this.containerView.getLeft() + BottomSheet.this.backgroundPaddingLeft), (((float) (getMeasuredHeight() - (bottomSheet2.drawNavigationBar ? bottomSheet2.bottomInset : 0))) + f) - BottomSheet.this.currentPanTranslationY, (float) (BottomSheet.this.containerView.getRight() - BottomSheet.this.backgroundPaddingLeft), ((float) getMeasuredHeight()) + f, this.backgroundPaint);
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
            int i3 = this.currentType == 2 ? 80 : 48;
            if (this.currentType == 0) {
                i = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM);
            }
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((float) i3), NUM));
        }

        public void setTextColor(int i) {
            this.textView.setTextColor(i);
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

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    public void setAllowNestedScroll(boolean z) {
        this.allowNestedScroll = z;
        if (!z) {
            this.containerView.setTranslationY(0.0f);
        }
    }

    public BottomSheet(Context context, boolean z) {
        super(context, NUM);
        if (Build.VERSION.SDK_INT >= 21) {
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
        AnonymousClass1 r7 = new ContainerView(getContext()) {
            public boolean drawChild(Canvas canvas, View view, long j) {
                try {
                    return BottomSheet.this.allowDrawContent && super.drawChild(canvas, view, j);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                    return true;
                }
            }
        };
        this.container = r7;
        r7.setBackgroundDrawable(this.backDrawable);
        this.focusable = z;
        if (Build.VERSION.SDK_INT >= 21) {
            this.container.setFitsSystemWindows(true);
            this.container.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                public final WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
                    return BottomSheet.this.lambda$new$0$BottomSheet(view, windowInsets);
                }
            });
            this.container.setSystemUiVisibility(1280);
        }
        this.backDrawable.setAlpha(0);
    }

    public /* synthetic */ WindowInsets lambda$new$0$BottomSheet(View view, WindowInsets windowInsets) {
        this.lastInsets = windowInsets;
        view.requestLayout();
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
        if (this.containerView == null) {
            AnonymousClass2 r2 = new FrameLayout(getContext()) {
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
            this.titleView.setOnTouchListener($$Lambda$BottomSheet$bysjO3P7kPXgYfq9zd4H2r0_8.INSTANCE);
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

    public /* synthetic */ void lambda$onCreate$2$BottomSheet(View view) {
        dismissWithButtonClick(((Integer) view.getTag()).intValue());
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
                i = 51;
            }
            colorDrawable.setAlpha(i);
            this.containerView.setTranslationY(0.0f);
            return;
        }
        this.backDrawable.setAlpha(0);
        if (Build.VERSION.SDK_INT >= 18) {
            this.layoutCount = 2;
            ViewGroup viewGroup = this.containerView;
            viewGroup.setTranslationY((float) viewGroup.getMeasuredHeight());
            AnonymousClass3 r0 = new Runnable() {
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

    private void cancelSheetAnimation() {
        AnimatorSet animatorSet = this.currentSheetAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.currentSheetAnimation = null;
            this.currentSheetAnimationType = 0;
        }
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
                iArr[0] = this.dimBehind ? 51 : 0;
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

    public void setItemColor(int i, int i2, int i3) {
        if (i >= 0 && i < this.itemViews.size()) {
            BottomSheetCell bottomSheetCell = this.itemViews.get(i);
            bottomSheetCell.textView.setTextColor(i2);
            bottomSheetCell.imageView.setColorFilter(new PorterDuffColorFilter(i3, PorterDuff.Mode.MULTIPLY));
        }
    }

    public void setItems(CharSequence[] charSequenceArr, int[] iArr, DialogInterface.OnClickListener onClickListener2) {
        this.items = charSequenceArr;
        this.itemIcons = iArr;
        this.onClickListener = onClickListener2;
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
                                BottomSheet.AnonymousClass5.this.lambda$onAnimationEnd$0$BottomSheet$5();
                            }
                        });
                    }
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 512);
                }

                public /* synthetic */ void lambda$onAnimationEnd$0$BottomSheet$5() {
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
        BottomSheetDelegateInterface bottomSheetDelegateInterface = this.delegate;
        if ((bottomSheetDelegateInterface == null || bottomSheetDelegateInterface.canDismiss()) && !this.dismissed) {
            this.dismissed = true;
            cancelSheetAnimation();
            if (!this.allowCustomAnimation || !onCustomCloseAnimation()) {
                this.currentSheetAnimationType = 2;
                AnimatorSet animatorSet = new AnimatorSet();
                this.currentSheetAnimation = animatorSet;
                ViewGroup viewGroup = this.containerView;
                animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(viewGroup, View.TRANSLATION_Y, new float[]{(float) (viewGroup.getMeasuredHeight() + this.container.keyboardHeight + AndroidUtilities.dp(10.0f))}), ObjectAnimator.ofInt(this.backDrawable, AnimationProperties.COLOR_DRAWABLE_ALPHA, new int[]{0})});
                if (this.useFastDismiss) {
                    float measuredHeight = (float) this.containerView.getMeasuredHeight();
                    this.currentSheetAnimation.setDuration((long) Math.max(60, (int) (((measuredHeight - this.containerView.getTranslationY()) * 250.0f) / measuredHeight)));
                    this.useFastDismiss = false;
                } else {
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
                                    BottomSheet.AnonymousClass6.this.lambda$onAnimationEnd$0$BottomSheet$6();
                                }
                            });
                        }
                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 512);
                    }

                    public /* synthetic */ void lambda$onAnimationEnd$0$BottomSheet$6() {
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
            setTitle(charSequence, false);
            return this;
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
}
