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
    /* access modifiers changed from: protected */
    public ColorDrawable backDrawable = new ColorDrawable(-16777216);
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
    /* access modifiers changed from: protected */
    public Drawable shadowDrawable;
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
        /* JADX WARNING: Removed duplicated region for block: B:39:0x00d7  */
        /* JADX WARNING: Removed duplicated region for block: B:46:0x0102  */
        /* JADX WARNING: Removed duplicated region for block: B:49:0x010b  */
        /* JADX WARNING: Removed duplicated region for block: B:62:0x017e  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onMeasure(int r13, int r14) {
            /*
                r12 = this;
                int r0 = android.os.Build.VERSION.SDK_INT
                int r13 = android.view.View.MeasureSpec.getSize(r13)
                int r14 = android.view.View.MeasureSpec.getSize(r14)
                android.view.View r1 = r12.getRootView()
                android.graphics.Rect r2 = r12.rect
                r12.getWindowVisibleDisplayFrame(r2)
                android.graphics.Rect r2 = r12.rect
                int r3 = r2.bottom
                r4 = 1101004800(0x41a00000, float:20.0)
                r5 = 0
                if (r3 == 0) goto L_0x0057
                int r2 = r2.top
                if (r2 == 0) goto L_0x0057
                int r2 = r1.getHeight()
                android.graphics.Rect r3 = r12.rect
                int r3 = r3.top
                if (r3 == 0) goto L_0x002d
                int r3 = org.telegram.messenger.AndroidUtilities.statusBarHeight
                goto L_0x002e
            L_0x002d:
                r3 = 0
            L_0x002e:
                int r2 = r2 - r3
                int r1 = org.telegram.messenger.AndroidUtilities.getViewInset(r1)
                int r2 = r2 - r1
                android.graphics.Rect r1 = r12.rect
                int r3 = r1.bottom
                int r1 = r1.top
                int r3 = r3 - r1
                int r2 = r2 - r3
                int r1 = java.lang.Math.max(r5, r2)
                r12.keyboardHeight = r1
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r4)
                if (r1 >= r2) goto L_0x004a
                r12.keyboardHeight = r5
            L_0x004a:
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                int r2 = r1.bottomInset
                int r3 = r12.keyboardHeight
                int r2 = r2 - r3
                int unused = r1.bottomInset = r2
                goto L_0x0059
            L_0x0057:
                r12.keyboardHeight = r5
            L_0x0059:
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                int r2 = r12.keyboardHeight
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r4)
                r4 = 1
                if (r2 <= r3) goto L_0x0066
                r2 = 1
                goto L_0x0067
            L_0x0066:
                r2 = 0
            L_0x0067:
                r1.keyboardVisible = r2
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.WindowInsets r1 = r1.lastInsets
                r2 = 29
                r3 = 21
                if (r1 == 0) goto L_0x00bd
                if (r0 < r3) goto L_0x00bd
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.WindowInsets r6 = r1.lastInsets
                int r6 = r6.getSystemWindowInsetBottom()
                int unused = r1.bottomInset = r6
                if (r0 < r2) goto L_0x0096
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                int r6 = r1.bottomInset
                org.telegram.ui.ActionBar.BottomSheet r7 = org.telegram.ui.ActionBar.BottomSheet.this
                int r7 = r7.getAdditionalMandatoryOffsets()
                int r6 = r6 + r7
                int unused = r1.bottomInset = r6
            L_0x0096:
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                boolean r6 = r1.keyboardVisible
                if (r6 == 0) goto L_0x00b0
                android.graphics.Rect r6 = r12.rect
                int r7 = r6.bottom
                if (r7 == 0) goto L_0x00b0
                int r6 = r6.top
                if (r6 == 0) goto L_0x00b0
                int r6 = r1.bottomInset
                int r7 = r12.keyboardHeight
                int r6 = r6 - r7
                int unused = r1.bottomInset = r6
            L_0x00b0:
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                boolean r6 = r1.drawNavigationBar
                if (r6 != 0) goto L_0x00bd
                int r1 = r1.bottomInset
                int r1 = r14 - r1
                goto L_0x00be
            L_0x00bd:
                r1 = r14
            L_0x00be:
                r12.setMeasuredDimension(r13, r1)
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.WindowInsets r1 = r1.lastInsets
                if (r1 == 0) goto L_0x00df
                if (r0 < r3) goto L_0x00df
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.WindowInsets r1 = r1.lastInsets
                int r1 = r1.getSystemWindowInsetBottom()
                if (r0 < r2) goto L_0x00de
                org.telegram.ui.ActionBar.BottomSheet r2 = org.telegram.ui.ActionBar.BottomSheet.this
                int r2 = r2.getAdditionalMandatoryOffsets()
                int r1 = r1 + r2
            L_0x00de:
                int r14 = r14 - r1
            L_0x00df:
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.WindowInsets r1 = r1.lastInsets
                if (r1 == 0) goto L_0x00ff
                if (r0 < r3) goto L_0x00ff
                org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.WindowInsets r0 = r0.lastInsets
                int r0 = r0.getSystemWindowInsetRight()
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.WindowInsets r1 = r1.lastInsets
                int r1 = r1.getSystemWindowInsetLeft()
                int r0 = r0 + r1
                int r13 = r13 - r0
            L_0x00ff:
                if (r13 >= r14) goto L_0x0102
                goto L_0x0103
            L_0x0102:
                r4 = 0
            L_0x0103:
                org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.ViewGroup r1 = r0.containerView
                r2 = 1073741824(0x40000000, float:2.0)
                if (r1 == 0) goto L_0x0178
                boolean r3 = r0.fullWidth
                r6 = -2147483648(0xfffffffvar_, float:-0.0)
                if (r3 != 0) goto L_0x0168
                boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
                r1 = 1061997773(0x3f4ccccd, float:0.8)
                if (r0 == 0) goto L_0x0134
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
                goto L_0x015c
            L_0x0134:
                if (r4 == 0) goto L_0x013e
                org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.ActionBar.BottomSheet.this
                int r0 = r0.backgroundPaddingLeft
                int r0 = r0 * 2
                int r0 = r0 + r13
                goto L_0x0158
            L_0x013e:
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
            L_0x0158:
                int r0 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r2)
            L_0x015c:
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.ViewGroup r1 = r1.containerView
                int r3 = android.view.View.MeasureSpec.makeMeasureSpec(r14, r6)
                r1.measure(r0, r3)
                goto L_0x0178
            L_0x0168:
                int r0 = r0.backgroundPaddingLeft
                int r0 = r0 * 2
                int r0 = r0 + r13
                int r0 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r2)
                int r3 = android.view.View.MeasureSpec.makeMeasureSpec(r14, r6)
                r1.measure(r0, r3)
            L_0x0178:
                int r0 = r12.getChildCount()
            L_0x017c:
                if (r5 >= r0) goto L_0x01a8
                android.view.View r7 = r12.getChildAt(r5)
                int r1 = r7.getVisibility()
                r3 = 8
                if (r1 == r3) goto L_0x01a5
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.ViewGroup r3 = r1.containerView
                if (r7 != r3) goto L_0x0191
                goto L_0x01a5
            L_0x0191:
                boolean r1 = r1.onCustomMeasure(r7, r13, r14)
                if (r1 != 0) goto L_0x01a5
                int r8 = android.view.View.MeasureSpec.makeMeasureSpec(r13, r2)
                r9 = 0
                int r10 = android.view.View.MeasureSpec.makeMeasureSpec(r14, r2)
                r11 = 0
                r6 = r12
                r6.measureChildWithMargins(r7, r8, r9, r10, r11)
            L_0x01a5:
                int r5 = r5 + 1
                goto L_0x017c
            L_0x01a8:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.BottomSheet.ContainerView.onMeasure(int, int):void");
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Removed duplicated region for block: B:47:0x0110  */
        /* JADX WARNING: Removed duplicated region for block: B:51:0x011d  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onLayout(boolean r16, int r17, int r18, int r19, int r20) {
            /*
                r15 = this;
                r0 = r15
                int r7 = android.os.Build.VERSION.SDK_INT
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                org.telegram.ui.ActionBar.BottomSheet.access$910(r1)
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.ViewGroup r1 = r1.containerView
                r8 = 21
                r9 = 0
                if (r1 == 0) goto L_0x00a2
                int r2 = r20 - r18
                int r1 = r1.getMeasuredHeight()
                int r2 = r2 - r1
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.WindowInsets r1 = r1.lastInsets
                if (r1 == 0) goto L_0x0064
                if (r7 < r8) goto L_0x0064
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.WindowInsets r1 = r1.lastInsets
                int r1 = r1.getSystemWindowInsetLeft()
                int r1 = r17 + r1
                org.telegram.ui.ActionBar.BottomSheet r3 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.WindowInsets r3 = r3.lastInsets
                int r3 = r3.getSystemWindowInsetRight()
                int r3 = r19 - r3
                org.telegram.ui.ActionBar.BottomSheet r4 = org.telegram.ui.ActionBar.BottomSheet.this
                boolean r5 = r4.useSmoothKeyboard
                if (r5 == 0) goto L_0x0042
                r2 = 0
                goto L_0x0068
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
                r4 = 29
                if (r7 < r4) goto L_0x0068
                org.telegram.ui.ActionBar.BottomSheet r4 = org.telegram.ui.ActionBar.BottomSheet.this
                int r4 = r4.getAdditionalMandatoryOffsets()
                int r2 = r2 - r4
                goto L_0x0068
            L_0x0064:
                r1 = r17
                r3 = r19
            L_0x0068:
                int r4 = r3 - r1
                org.telegram.ui.ActionBar.BottomSheet r5 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.ViewGroup r5 = r5.containerView
                int r5 = r5.getMeasuredWidth()
                int r4 = r4 - r5
                int r4 = r4 / 2
                org.telegram.ui.ActionBar.BottomSheet r5 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.WindowInsets r5 = r5.lastInsets
                if (r5 == 0) goto L_0x008a
                if (r7 < r8) goto L_0x008a
                org.telegram.ui.ActionBar.BottomSheet r5 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.WindowInsets r5 = r5.lastInsets
                int r5 = r5.getSystemWindowInsetLeft()
                int r4 = r4 + r5
            L_0x008a:
                org.telegram.ui.ActionBar.BottomSheet r5 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.ViewGroup r5 = r5.containerView
                int r6 = r5.getMeasuredWidth()
                int r6 = r6 + r4
                org.telegram.ui.ActionBar.BottomSheet r10 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.ViewGroup r10 = r10.containerView
                int r10 = r10.getMeasuredHeight()
                int r10 = r10 + r2
                r5.layout(r4, r2, r6, r10)
                r10 = r1
                r11 = r3
                goto L_0x00a6
            L_0x00a2:
                r10 = r17
                r11 = r19
            L_0x00a6:
                int r12 = r15.getChildCount()
                r13 = 0
            L_0x00ab:
                if (r13 >= r12) goto L_0x0147
                android.view.View r14 = r15.getChildAt(r13)
                int r1 = r14.getVisibility()
                r2 = 8
                if (r1 == r2) goto L_0x0143
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.ViewGroup r2 = r1.containerView
                if (r14 != r2) goto L_0x00c1
                goto L_0x0143
            L_0x00c1:
                boolean r2 = r1.drawNavigationBar
                if (r2 == 0) goto L_0x00ca
                int r2 = r1.bottomInset
                goto L_0x00cb
            L_0x00ca:
                r2 = 0
            L_0x00cb:
                int r6 = r20 - r2
                r2 = r14
                r3 = r10
                r4 = r18
                r5 = r11
                boolean r1 = r1.onCustomLayout(r2, r3, r4, r5, r6)
                if (r1 != 0) goto L_0x0143
                android.view.ViewGroup$LayoutParams r1 = r14.getLayoutParams()
                android.widget.FrameLayout$LayoutParams r1 = (android.widget.FrameLayout.LayoutParams) r1
                int r2 = r14.getMeasuredWidth()
                int r3 = r14.getMeasuredHeight()
                int r4 = r1.gravity
                r5 = -1
                if (r4 != r5) goto L_0x00ed
                r4 = 51
            L_0x00ed:
                r5 = r4 & 7
                r4 = r4 & 112(0x70, float:1.57E-43)
                r5 = r5 & 7
                r6 = 1
                if (r5 == r6) goto L_0x0101
                r6 = 5
                if (r5 == r6) goto L_0x00fc
                int r5 = r1.leftMargin
                goto L_0x010c
            L_0x00fc:
                int r5 = r11 - r2
                int r6 = r1.rightMargin
                goto L_0x010b
            L_0x0101:
                int r5 = r11 - r10
                int r5 = r5 - r2
                int r5 = r5 / 2
                int r6 = r1.leftMargin
                int r5 = r5 + r6
                int r6 = r1.rightMargin
            L_0x010b:
                int r5 = r5 - r6
            L_0x010c:
                r6 = 16
                if (r4 == r6) goto L_0x011d
                r6 = 80
                if (r4 == r6) goto L_0x0117
                int r1 = r1.topMargin
                goto L_0x0129
            L_0x0117:
                int r4 = r20 - r18
                int r4 = r4 - r3
                int r1 = r1.bottomMargin
                goto L_0x0127
            L_0x011d:
                int r4 = r20 - r18
                int r4 = r4 - r3
                int r4 = r4 / 2
                int r6 = r1.topMargin
                int r4 = r4 + r6
                int r1 = r1.bottomMargin
            L_0x0127:
                int r1 = r4 - r1
            L_0x0129:
                org.telegram.ui.ActionBar.BottomSheet r4 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.WindowInsets r4 = r4.lastInsets
                if (r4 == 0) goto L_0x013e
                if (r7 < r8) goto L_0x013e
                org.telegram.ui.ActionBar.BottomSheet r4 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.WindowInsets r4 = r4.lastInsets
                int r4 = r4.getSystemWindowInsetLeft()
                int r5 = r5 + r4
            L_0x013e:
                int r2 = r2 + r5
                int r3 = r3 + r1
                r14.layout(r5, r1, r2, r3)
            L_0x0143:
                int r13 = r13 + 1
                goto L_0x00ab
            L_0x0147:
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                int r1 = r1.layoutCount
                if (r1 != 0) goto L_0x0164
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                java.lang.Runnable r1 = r1.startAnimationRunnable
                if (r1 == 0) goto L_0x0164
                org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r1)
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                java.lang.Runnable r1 = r1.startAnimationRunnable
                r1.run()
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                r2 = 0
                r1.startAnimationRunnable = r2
            L_0x0164:
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
                int i = Build.VERSION.SDK_INT;
                if (i >= 26) {
                    this.backgroundPaint.setColor(Theme.getColor("windowBackgroundGray"));
                } else {
                    this.backgroundPaint.setColor(-16777216);
                }
                if (i >= 29 && BottomSheet.this.getAdditionalMandatoryOffsets() > 0) {
                    f = Math.max(0.0f, ((float) BottomSheet.this.bottomInset) - (((float) BottomSheet.this.containerView.getMeasuredHeight()) - BottomSheet.this.containerView.getTranslationY()));
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
        if (i >= 21) {
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
        AnonymousClass1 r8 = new ContainerView(getContext()) {
            public boolean drawChild(Canvas canvas, View view, long j) {
                try {
                    return BottomSheet.this.allowDrawContent && super.drawChild(canvas, view, j);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                    return true;
                }
            }
        };
        this.container = r8;
        r8.setBackgroundDrawable(this.backDrawable);
        this.focusable = z;
        if (i >= 21) {
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ WindowInsets lambda$new$0$BottomSheet(View view, WindowInsets windowInsets) {
        this.lastInsets = windowInsets;
        view.requestLayout();
        return windowInsets.consumeSystemWindowInsets();
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        int i;
        int i2 = Build.VERSION.SDK_INT;
        super.onCreate(bundle);
        Window window = getWindow();
        window.setWindowAnimations(NUM);
        setContentView(this.container, new ViewGroup.LayoutParams(-1, -1));
        Drawable drawable = null;
        if (this.useLightStatusBar && i2 >= 23 && Theme.getColor("actionBarDefault", (boolean[]) null, true) == -1) {
            this.container.setSystemUiVisibility(this.container.getSystemUiVisibility() | 8192);
        }
        if (this.containerView == null) {
            AnonymousClass2 r3 = new FrameLayout(getContext()) {
                public boolean hasOverlappingRendering() {
                    return false;
                }

                public void setTranslationY(float f) {
                    super.setTranslationY(f);
                    BottomSheet.this.onContainerTranslationYChanged(f);
                }
            };
            this.containerView = r3;
            r3.setBackgroundDrawable(this.shadowDrawable);
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
            int i3 = 0;
            while (true) {
                CharSequence[] charSequenceArr = this.items;
                if (i3 >= charSequenceArr.length) {
                    break;
                }
                if (charSequenceArr[i3] != null) {
                    BottomSheetCell bottomSheetCell = new BottomSheetCell(getContext(), 0);
                    CharSequence charSequence = this.items[i3];
                    int[] iArr = this.itemIcons;
                    bottomSheetCell.setTextAndIcon(charSequence, iArr != null ? iArr[i3] : 0, drawable, this.bigTitle);
                    this.containerView.addView(bottomSheetCell, LayoutHelper.createFrame(-1, 48.0f, 51, 0.0f, (float) i, 0.0f, 0.0f));
                    i += 48;
                    bottomSheetCell.setTag(Integer.valueOf(i3));
                    bottomSheetCell.setOnClickListener(new View.OnClickListener() {
                        public final void onClick(View view) {
                            BottomSheet.this.lambda$onCreate$2$BottomSheet(view);
                        }
                    });
                    this.itemViews.add(bottomSheetCell);
                }
                i3++;
                drawable = null;
            }
        }
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = -1;
        attributes.gravity = 51;
        attributes.dimAmount = 0.0f;
        int i4 = attributes.flags & -3;
        attributes.flags = i4;
        if (this.focusable) {
            attributes.softInputMode = 16;
        } else {
            attributes.flags = i4 | 131072;
        }
        if (this.isFullscreen) {
            if (i2 >= 21) {
                attributes.flags |= -NUM;
            }
            attributes.flags |= 1024;
            this.container.setSystemUiVisibility(1284);
        }
        attributes.height = -1;
        if (i2 >= 28) {
            attributes.layoutInDisplayCutoutMode = 1;
        }
        window.setAttributes(attributes);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreate$2 */
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

                /* access modifiers changed from: private */
                /* renamed from: lambda$onAnimationEnd$0 */
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

                    /* access modifiers changed from: private */
                    /* renamed from: lambda$onAnimationEnd$0 */
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
