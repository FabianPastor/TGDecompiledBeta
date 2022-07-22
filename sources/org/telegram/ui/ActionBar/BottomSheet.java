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
import android.view.accessibility.AccessibilityNodeInfo;
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
    protected ColorDrawable backDrawable;
    protected int backgroundPaddingLeft;
    protected int backgroundPaddingTop;
    protected int behindKeyboardColor;
    protected String behindKeyboardColorKey;
    /* access modifiers changed from: private */
    public boolean bigTitle;
    /* access modifiers changed from: private */
    public int bottomInset;
    protected boolean calcMandatoryInsets;
    private boolean canDismissWithSwipe;
    protected ContainerView container;
    protected ViewGroup containerView;
    protected int currentAccount;
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
    public boolean drawDoubleNavigationBar;
    public boolean drawNavigationBar;
    private boolean focusable;
    private boolean fullHeight;
    protected boolean fullWidth;
    /* access modifiers changed from: private */
    public float hideSystemVerticalInsetsProgress;
    protected boolean isFullscreen;
    protected boolean isPortrait;
    /* access modifiers changed from: private */
    public int[] itemIcons;
    private ArrayList<BottomSheetCell> itemViews;
    /* access modifiers changed from: private */
    public CharSequence[] items;
    /* access modifiers changed from: private */
    public ValueAnimator keyboardContentAnimator;
    protected boolean keyboardVisible;
    /* access modifiers changed from: private */
    public WindowInsets lastInsets;
    /* access modifiers changed from: private */
    public int layoutCount;
    /* access modifiers changed from: private */
    public int leftInset;
    /* access modifiers changed from: private */
    public boolean multipleLinesTitle;
    protected int navBarColor;
    protected String navBarColorKey;
    protected float navigationBarAlpha;
    protected ValueAnimator navigationBarAnimation;
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
    public boolean useBackgroundTopPadding;
    /* access modifiers changed from: private */
    public boolean useFastDismiss;
    protected boolean useHardwareLayer;
    protected boolean useLightNavBar;
    protected boolean useLightStatusBar;
    protected boolean useSmoothKeyboard;

    public static class BottomSheetDelegate implements BottomSheetDelegateInterface {
        public boolean canDismiss() {
            return true;
        }

        public void onOpenAnimationEnd() {
        }
    }

    public interface BottomSheetDelegateInterface {
        boolean canDismiss();

        void onOpenAnimationEnd();
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$onCreate$2(View view, MotionEvent motionEvent) {
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithTouchOutside() {
        return true;
    }

    /* access modifiers changed from: protected */
    public int getTargetOpenTranslationY() {
        return 0;
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        return null;
    }

    /* access modifiers changed from: protected */
    public void mainContainerDispatchDraw(Canvas canvas) {
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

    public void onDismissAnimationStart() {
    }

    /* access modifiers changed from: protected */
    public boolean onScrollUp(float f) {
        return false;
    }

    /* access modifiers changed from: protected */
    public void onScrollUpBegin(float f) {
    }

    /* access modifiers changed from: protected */
    public void onScrollUpEnd(float f) {
    }

    /* access modifiers changed from: protected */
    public boolean shouldOverlayCameraViewOverNavBar() {
        return false;
    }

    static /* synthetic */ int access$1210(BottomSheet bottomSheet) {
        int i = bottomSheet.layoutCount;
        bottomSheet.layoutCount = i - 1;
        return i;
    }

    static /* synthetic */ int access$712(BottomSheet bottomSheet, int i) {
        int i2 = bottomSheet.bottomInset + i;
        bottomSheet.bottomInset = i2;
        return i2;
    }

    static /* synthetic */ int access$720(BottomSheet bottomSheet, int i) {
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
        private float y = 0.0f;

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
            this.currentAnimation = new AnimatorSet();
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            ofFloat.addUpdateListener(new BottomSheet$ContainerView$$ExternalSyntheticLambda1(this));
            this.currentAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(BottomSheet.this.containerView, "translationY", new float[]{0.0f}), ofFloat});
            this.currentAnimation.setDuration((long) ((int) ((Math.max(0.0f, translationY) / AndroidUtilities.getPixelsInCM(0.8f, false)) * 250.0f)));
            this.currentAnimation.setInterpolator(CubicBezierInterpolator.DEFAULT);
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

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$checkDismiss$0(ValueAnimator valueAnimator) {
            ContainerView containerView = BottomSheet.this.container;
            if (containerView != null) {
                containerView.invalidate();
            }
        }

        private void cancelCurrentAnimation() {
            AnimatorSet animatorSet = this.currentAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.currentAnimation = null;
            }
        }

        public boolean processTouchEvent(MotionEvent motionEvent, boolean z) {
            if (BottomSheet.this.dismissed) {
                return false;
            }
            if (BottomSheet.this.onContainerTouchEvent(motionEvent)) {
                return true;
            }
            if (BottomSheet.this.canDismissWithTouchOutside() && motionEvent != null && ((motionEvent.getAction() == 0 || motionEvent.getAction() == 2) && !this.startedTracking && !this.maybeStartTracking && motionEvent.getPointerCount() == 1)) {
                this.startedTrackingX = (int) motionEvent.getX();
                int y2 = (int) motionEvent.getY();
                this.startedTrackingY = y2;
                if (y2 < BottomSheet.this.containerView.getTop() || this.startedTrackingX < BottomSheet.this.containerView.getLeft() || this.startedTrackingX > BottomSheet.this.containerView.getRight()) {
                    BottomSheet.this.onDismissWithTouchOutside();
                    return true;
                }
                BottomSheet.this.onScrollUpBegin(this.y);
                this.startedTrackingPointerId = motionEvent.getPointerId(0);
                this.maybeStartTracking = true;
                cancelCurrentAnimation();
                VelocityTracker velocityTracker2 = this.velocityTracker;
                if (velocityTracker2 != null) {
                    velocityTracker2.clear();
                }
            } else if (motionEvent != null && motionEvent.getAction() == 2 && motionEvent.getPointerId(0) == this.startedTrackingPointerId) {
                if (this.velocityTracker == null) {
                    this.velocityTracker = VelocityTracker.obtain();
                }
                float abs = (float) Math.abs((int) (motionEvent.getX() - ((float) this.startedTrackingX)));
                float y3 = (float) (((int) motionEvent.getY()) - this.startedTrackingY);
                boolean onScrollUp = BottomSheet.this.onScrollUp(this.y + y3);
                this.velocityTracker.addMovement(motionEvent);
                if (!BottomSheet.this.disableScroll && this.maybeStartTracking && !this.startedTracking && y3 > 0.0f && y3 / 3.0f > Math.abs(abs) && Math.abs(y3) >= ((float) BottomSheet.this.touchSlop)) {
                    this.startedTrackingY = (int) motionEvent.getY();
                    this.maybeStartTracking = false;
                    this.startedTracking = true;
                    requestDisallowInterceptTouchEvent(true);
                } else if (this.startedTracking) {
                    float f = this.y + y3;
                    this.y = f;
                    if (!onScrollUp) {
                        this.y = Math.max(f, 0.0f);
                    }
                    BottomSheet.this.containerView.setTranslationY(Math.max(this.y, 0.0f));
                    this.startedTrackingY = (int) motionEvent.getY();
                    BottomSheet.this.container.invalidate();
                }
            } else if (motionEvent == null || (motionEvent.getPointerId(0) == this.startedTrackingPointerId && (motionEvent.getAction() == 3 || motionEvent.getAction() == 1 || motionEvent.getAction() == 6))) {
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
            if ((z || !this.maybeStartTracking) && !this.startedTracking && BottomSheet.this.canDismissWithSwipe()) {
                return false;
            }
            return true;
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            return processTouchEvent(motionEvent, false);
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Removed duplicated region for block: B:44:0x010d  */
        /* JADX WARNING: Removed duplicated region for block: B:53:0x0134  */
        /* JADX WARNING: Removed duplicated region for block: B:56:0x013d  */
        /* JADX WARNING: Removed duplicated region for block: B:66:0x019b  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onMeasure(int r14, int r15) {
            /*
                r13 = this;
                int r14 = android.view.View.MeasureSpec.getSize(r14)
                int r15 = android.view.View.MeasureSpec.getSize(r15)
                android.view.View r0 = r13.getRootView()
                android.graphics.Rect r1 = r13.rect
                r13.getWindowVisibleDisplayFrame(r1)
                int r1 = r13.keyboardHeight
                android.graphics.Rect r2 = r13.rect
                int r3 = r2.bottom
                r4 = 1101004800(0x41a00000, float:20.0)
                r5 = 1065353216(0x3var_, float:1.0)
                r6 = 0
                if (r3 == 0) goto L_0x006c
                int r2 = r2.top
                if (r2 == 0) goto L_0x006c
                int r2 = r0.getHeight()
                float r2 = (float) r2
                android.graphics.Rect r3 = r13.rect
                int r3 = r3.top
                if (r3 == 0) goto L_0x003b
                int r3 = org.telegram.messenger.AndroidUtilities.statusBarHeight
                float r3 = (float) r3
                org.telegram.ui.ActionBar.BottomSheet r7 = org.telegram.ui.ActionBar.BottomSheet.this
                float r7 = r7.hideSystemVerticalInsetsProgress
                float r7 = r5 - r7
                float r3 = r3 * r7
                goto L_0x003c
            L_0x003b:
                r3 = 0
            L_0x003c:
                float r2 = r2 - r3
                int r0 = org.telegram.messenger.AndroidUtilities.getViewInset(r0)
                float r0 = (float) r0
                org.telegram.ui.ActionBar.BottomSheet r3 = org.telegram.ui.ActionBar.BottomSheet.this
                float r3 = r3.hideSystemVerticalInsetsProgress
                float r3 = r5 - r3
                float r0 = r0 * r3
                float r2 = r2 - r0
                int r0 = (int) r2
                android.graphics.Rect r2 = r13.rect
                int r3 = r2.bottom
                int r2 = r2.top
                int r3 = r3 - r2
                int r0 = r0 - r3
                int r0 = java.lang.Math.max(r6, r0)
                r13.keyboardHeight = r0
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r4)
                if (r0 >= r2) goto L_0x0064
                r13.keyboardHeight = r6
            L_0x0064:
                org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.ActionBar.BottomSheet.this
                int r2 = r13.keyboardHeight
                org.telegram.ui.ActionBar.BottomSheet.access$720(r0, r2)
                goto L_0x006e
            L_0x006c:
                r13.keyboardHeight = r6
            L_0x006e:
                int r0 = r13.keyboardHeight
                r2 = 1
                if (r1 == r0) goto L_0x0075
                r13.keyboardChanged = r2
            L_0x0075:
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r4)
                if (r0 <= r3) goto L_0x007f
                r0 = 1
                goto L_0x0080
            L_0x007f:
                r0 = 0
            L_0x0080:
                r1.keyboardVisible = r0
                org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.WindowInsets r0 = r0.lastInsets
                r1 = 29
                r3 = 21
                if (r0 == 0) goto L_0x00e6
                int r0 = android.os.Build.VERSION.SDK_INT
                if (r0 < r3) goto L_0x00e6
                org.telegram.ui.ActionBar.BottomSheet r4 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.WindowInsets r7 = r4.lastInsets
                int r7 = r7.getSystemWindowInsetBottom()
                int unused = r4.bottomInset = r7
                org.telegram.ui.ActionBar.BottomSheet r4 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.WindowInsets r7 = r4.lastInsets
                int r7 = r7.getSystemWindowInsetLeft()
                int unused = r4.leftInset = r7
                org.telegram.ui.ActionBar.BottomSheet r4 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.WindowInsets r7 = r4.lastInsets
                int r7 = r7.getSystemWindowInsetRight()
                int unused = r4.rightInset = r7
                if (r0 < r1) goto L_0x00c4
                org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.ActionBar.BottomSheet.this
                int r4 = r0.getAdditionalMandatoryOffsets()
                org.telegram.ui.ActionBar.BottomSheet.access$712(r0, r4)
            L_0x00c4:
                org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.ActionBar.BottomSheet.this
                boolean r4 = r0.keyboardVisible
                if (r4 == 0) goto L_0x00d9
                android.graphics.Rect r4 = r13.rect
                int r7 = r4.bottom
                if (r7 == 0) goto L_0x00d9
                int r4 = r4.top
                if (r4 == 0) goto L_0x00d9
                int r4 = r13.keyboardHeight
                org.telegram.ui.ActionBar.BottomSheet.access$720(r0, r4)
            L_0x00d9:
                org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.ActionBar.BottomSheet.this
                boolean r4 = r0.drawNavigationBar
                if (r4 != 0) goto L_0x00e6
                int r0 = r0.getBottomInset()
                int r0 = r15 - r0
                goto L_0x00e7
            L_0x00e6:
                r0 = r15
            L_0x00e7:
                r13.setMeasuredDimension(r14, r0)
                org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.WindowInsets r0 = r0.lastInsets
                if (r0 == 0) goto L_0x0115
                int r0 = android.os.Build.VERSION.SDK_INT
                if (r0 < r3) goto L_0x0115
                org.telegram.ui.ActionBar.BottomSheet r4 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.WindowInsets r4 = r4.lastInsets
                int r4 = r4.getSystemWindowInsetBottom()
                float r4 = (float) r4
                org.telegram.ui.ActionBar.BottomSheet r7 = org.telegram.ui.ActionBar.BottomSheet.this
                float r7 = r7.hideSystemVerticalInsetsProgress
                float r5 = r5 - r7
                float r4 = r4 * r5
                int r4 = (int) r4
                if (r0 < r1) goto L_0x0114
                org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.ActionBar.BottomSheet.this
                int r0 = r0.getAdditionalMandatoryOffsets()
                int r4 = r4 + r0
            L_0x0114:
                int r15 = r15 - r4
            L_0x0115:
                org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.WindowInsets r0 = r0.lastInsets
                if (r0 == 0) goto L_0x012f
                int r0 = android.os.Build.VERSION.SDK_INT
                if (r0 < r3) goto L_0x012f
                org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.ActionBar.BottomSheet.this
                int r0 = r0.getRightInset()
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                int r1 = r1.getLeftInset()
                int r0 = r0 + r1
                int r14 = r14 - r0
            L_0x012f:
                org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.ActionBar.BottomSheet.this
                if (r14 >= r15) goto L_0x0134
                goto L_0x0135
            L_0x0134:
                r2 = 0
            L_0x0135:
                r0.isPortrait = r2
                android.view.ViewGroup r1 = r0.containerView
                r2 = 1073741824(0x40000000, float:2.0)
                if (r1 == 0) goto L_0x0195
                boolean r3 = r0.fullWidth
                r4 = -2147483648(0xfffffffvar_, float:-0.0)
                if (r3 != 0) goto L_0x0185
                boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
                if (r0 == 0) goto L_0x0166
                android.graphics.Point r0 = org.telegram.messenger.AndroidUtilities.displaySize
                int r1 = r0.x
                int r0 = r0.y
                int r0 = java.lang.Math.min(r1, r0)
                float r0 = (float) r0
                r1 = 1061997773(0x3f4ccccd, float:0.8)
                float r0 = r0 * r1
                int r0 = (int) r0
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                int r1 = r1.backgroundPaddingLeft
                int r1 = r1 * 2
                int r0 = r0 + r1
                int r0 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r2)
                goto L_0x0179
            L_0x0166:
                org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.ActionBar.BottomSheet.this
                boolean r1 = r0.isPortrait
                int r0 = r0.getBottomSheetWidth(r1, r14, r15)
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                int r1 = r1.backgroundPaddingLeft
                int r1 = r1 * 2
                int r0 = r0 + r1
                int r0 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r2)
            L_0x0179:
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.ViewGroup r1 = r1.containerView
                int r3 = android.view.View.MeasureSpec.makeMeasureSpec(r15, r4)
                r1.measure(r0, r3)
                goto L_0x0195
            L_0x0185:
                int r0 = r0.backgroundPaddingLeft
                int r0 = r0 * 2
                int r0 = r0 + r14
                int r0 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r2)
                int r3 = android.view.View.MeasureSpec.makeMeasureSpec(r15, r4)
                r1.measure(r0, r3)
            L_0x0195:
                int r0 = r13.getChildCount()
            L_0x0199:
                if (r6 >= r0) goto L_0x01c5
                android.view.View r8 = r13.getChildAt(r6)
                int r1 = r8.getVisibility()
                r3 = 8
                if (r1 == r3) goto L_0x01c2
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.ViewGroup r3 = r1.containerView
                if (r8 != r3) goto L_0x01ae
                goto L_0x01c2
            L_0x01ae:
                boolean r1 = r1.onCustomMeasure(r8, r14, r15)
                if (r1 != 0) goto L_0x01c2
                int r9 = android.view.View.MeasureSpec.makeMeasureSpec(r14, r2)
                r10 = 0
                int r11 = android.view.View.MeasureSpec.makeMeasureSpec(r15, r2)
                r12 = 0
                r7 = r13
                r7.measureChildWithMargins(r8, r9, r10, r11, r12)
            L_0x01c2:
                int r6 = r6 + 1
                goto L_0x0199
            L_0x01c5:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.BottomSheet.ContainerView.onMeasure(int, int):void");
        }

        public void requestLayout() {
            super.requestLayout();
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Removed duplicated region for block: B:63:0x019d  */
        /* JADX WARNING: Removed duplicated region for block: B:67:0x01aa  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onLayout(boolean r17, int r18, int r19, int r20, int r21) {
            /*
                r16 = this;
                r0 = r16
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                org.telegram.ui.ActionBar.BottomSheet.access$1210(r1)
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.ViewGroup r1 = r1.containerView
                r7 = 1
                r8 = 21
                r9 = 2
                r10 = 0
                if (r1 == 0) goto L_0x0131
                int r2 = r21 - r19
                int r1 = r1.getMeasuredHeight()
                int r2 = r2 - r1
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.WindowInsets r1 = r1.lastInsets
                if (r1 == 0) goto L_0x006e
                int r1 = android.os.Build.VERSION.SDK_INT
                if (r1 < r8) goto L_0x006e
                org.telegram.ui.ActionBar.BottomSheet r3 = org.telegram.ui.ActionBar.BottomSheet.this
                int r3 = r3.getLeftInset()
                int r3 = r18 + r3
                org.telegram.ui.ActionBar.BottomSheet r4 = org.telegram.ui.ActionBar.BottomSheet.this
                int r4 = r4.getRightInset()
                int r4 = r20 - r4
                org.telegram.ui.ActionBar.BottomSheet r5 = org.telegram.ui.ActionBar.BottomSheet.this
                boolean r6 = r5.useSmoothKeyboard
                if (r6 == 0) goto L_0x003d
                r2 = 0
                goto L_0x0072
            L_0x003d:
                float r2 = (float) r2
                android.view.WindowInsets r5 = r5.lastInsets
                int r5 = r5.getSystemWindowInsetBottom()
                float r5 = (float) r5
                r6 = 1065353216(0x3var_, float:1.0)
                org.telegram.ui.ActionBar.BottomSheet r11 = org.telegram.ui.ActionBar.BottomSheet.this
                float r11 = r11.hideSystemVerticalInsetsProgress
                float r6 = r6 - r11
                float r5 = r5 * r6
                org.telegram.ui.ActionBar.BottomSheet r6 = org.telegram.ui.ActionBar.BottomSheet.this
                boolean r11 = r6.drawNavigationBar
                if (r11 == 0) goto L_0x005a
                r6 = 0
                goto L_0x005e
            L_0x005a:
                int r6 = r6.getBottomInset()
            L_0x005e:
                float r6 = (float) r6
                float r5 = r5 - r6
                float r2 = r2 - r5
                int r2 = (int) r2
                r5 = 29
                if (r1 < r5) goto L_0x0072
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                int r1 = r1.getAdditionalMandatoryOffsets()
                int r2 = r2 - r1
                goto L_0x0072
            L_0x006e:
                r3 = r18
                r4 = r20
            L_0x0072:
                int r1 = r4 - r3
                org.telegram.ui.ActionBar.BottomSheet r5 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.ViewGroup r5 = r5.containerView
                int r5 = r5.getMeasuredWidth()
                int r1 = r1 - r5
                int r1 = r1 / r9
                org.telegram.ui.ActionBar.BottomSheet r5 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.WindowInsets r5 = r5.lastInsets
                if (r5 == 0) goto L_0x0091
                int r5 = android.os.Build.VERSION.SDK_INT
                if (r5 < r8) goto L_0x0091
                org.telegram.ui.ActionBar.BottomSheet r5 = org.telegram.ui.ActionBar.BottomSheet.this
                int r5 = r5.getLeftInset()
                int r1 = r1 + r5
            L_0x0091:
                org.telegram.ui.ActionBar.BottomSheet r5 = org.telegram.ui.ActionBar.BottomSheet.this
                boolean r6 = r5.smoothKeyboardAnimationEnabled
                if (r6 == 0) goto L_0x0119
                java.lang.Runnable r6 = r5.startAnimationRunnable
                if (r6 != 0) goto L_0x0119
                boolean r6 = r0.keyboardChanged
                if (r6 == 0) goto L_0x0119
                boolean r5 = r5.dismissed
                if (r5 != 0) goto L_0x0119
                org.telegram.ui.ActionBar.BottomSheet r5 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.ViewGroup r5 = r5.containerView
                int r5 = r5.getTop()
                if (r5 == r2) goto L_0x0119
                org.telegram.ui.ActionBar.BottomSheet r5 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.ViewGroup r5 = r5.containerView
                int r6 = r5.getTop()
                int r6 = r6 - r2
                float r6 = (float) r6
                r5.setTranslationY(r6)
                org.telegram.ui.ActionBar.BottomSheet r5 = org.telegram.ui.ActionBar.BottomSheet.this
                android.animation.ValueAnimator r5 = r5.keyboardContentAnimator
                if (r5 == 0) goto L_0x00cd
                org.telegram.ui.ActionBar.BottomSheet r5 = org.telegram.ui.ActionBar.BottomSheet.this
                android.animation.ValueAnimator r5 = r5.keyboardContentAnimator
                r5.cancel()
            L_0x00cd:
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
                org.telegram.ui.ActionBar.BottomSheet$ContainerView$$ExternalSyntheticLambda0 r6 = new org.telegram.ui.ActionBar.BottomSheet$ContainerView$$ExternalSyntheticLambda0
                r6.<init>(r0)
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
            L_0x0119:
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
                goto L_0x0135
            L_0x0131:
                r11 = r18
                r12 = r20
            L_0x0135:
                int r13 = r16.getChildCount()
                r14 = 0
            L_0x013a:
                if (r14 >= r13) goto L_0x01d1
                android.view.View r15 = r0.getChildAt(r14)
                int r1 = r15.getVisibility()
                r2 = 8
                if (r1 == r2) goto L_0x01cd
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.ViewGroup r2 = r1.containerView
                if (r15 != r2) goto L_0x0150
                goto L_0x01cd
            L_0x0150:
                boolean r2 = r1.drawNavigationBar
                if (r2 == 0) goto L_0x0159
                int r2 = r1.getBottomInset()
                goto L_0x015a
            L_0x0159:
                r2 = 0
            L_0x015a:
                int r6 = r21 - r2
                r2 = r15
                r3 = r11
                r4 = r19
                r5 = r12
                boolean r1 = r1.onCustomLayout(r2, r3, r4, r5, r6)
                if (r1 != 0) goto L_0x01cd
                android.view.ViewGroup$LayoutParams r1 = r15.getLayoutParams()
                android.widget.FrameLayout$LayoutParams r1 = (android.widget.FrameLayout.LayoutParams) r1
                int r2 = r15.getMeasuredWidth()
                int r3 = r15.getMeasuredHeight()
                int r4 = r1.gravity
                r5 = -1
                if (r4 != r5) goto L_0x017c
                r4 = 51
            L_0x017c:
                r5 = r4 & 7
                r4 = r4 & 112(0x70, float:1.57E-43)
                r5 = r5 & 7
                if (r5 == r7) goto L_0x018f
                r6 = 5
                if (r5 == r6) goto L_0x018a
                int r5 = r1.leftMargin
                goto L_0x0199
            L_0x018a:
                int r5 = r12 - r2
                int r6 = r1.rightMargin
                goto L_0x0198
            L_0x018f:
                int r5 = r12 - r11
                int r5 = r5 - r2
                int r5 = r5 / r9
                int r6 = r1.leftMargin
                int r5 = r5 + r6
                int r6 = r1.rightMargin
            L_0x0198:
                int r5 = r5 - r6
            L_0x0199:
                r6 = 16
                if (r4 == r6) goto L_0x01aa
                r6 = 80
                if (r4 == r6) goto L_0x01a4
                int r1 = r1.topMargin
                goto L_0x01b5
            L_0x01a4:
                int r4 = r21 - r19
                int r4 = r4 - r3
                int r1 = r1.bottomMargin
                goto L_0x01b3
            L_0x01aa:
                int r4 = r21 - r19
                int r4 = r4 - r3
                int r4 = r4 / r9
                int r6 = r1.topMargin
                int r4 = r4 + r6
                int r1 = r1.bottomMargin
            L_0x01b3:
                int r1 = r4 - r1
            L_0x01b5:
                org.telegram.ui.ActionBar.BottomSheet r4 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.WindowInsets r4 = r4.lastInsets
                if (r4 == 0) goto L_0x01c8
                int r4 = android.os.Build.VERSION.SDK_INT
                if (r4 < r8) goto L_0x01c8
                org.telegram.ui.ActionBar.BottomSheet r4 = org.telegram.ui.ActionBar.BottomSheet.this
                int r4 = r4.getLeftInset()
                int r5 = r5 + r4
            L_0x01c8:
                int r2 = r2 + r5
                int r3 = r3 + r1
                r15.layout(r5, r1, r2, r3)
            L_0x01cd:
                int r14 = r14 + 1
                goto L_0x013a
            L_0x01d1:
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                int r1 = r1.layoutCount
                if (r1 != 0) goto L_0x01ee
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                java.lang.Runnable r1 = r1.startAnimationRunnable
                if (r1 == 0) goto L_0x01ee
                org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r1)
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                java.lang.Runnable r1 = r1.startAnimationRunnable
                r1.run()
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                r2 = 0
                r1.startAnimationRunnable = r2
            L_0x01ee:
                r0.keyboardChanged = r10
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.BottomSheet.ContainerView.onLayout(boolean, int, int, int, int):void");
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onLayout$1(ValueAnimator valueAnimator) {
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
        /* JADX WARNING: Removed duplicated region for block: B:32:0x00cf  */
        /* JADX WARNING: Removed duplicated region for block: B:44:0x00ff  */
        /* JADX WARNING: Removed duplicated region for block: B:55:0x0154  */
        /* JADX WARNING: Removed duplicated region for block: B:60:0x0188  */
        /* JADX WARNING: Removed duplicated region for block: B:66:? A[RETURN, SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void dispatchDraw(android.graphics.Canvas r12) {
            /*
                r11 = this;
                int r0 = android.os.Build.VERSION.SDK_INT
                r1 = 26
                if (r0 < r1) goto L_0x001e
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                java.lang.String r2 = r1.navBarColorKey
                if (r2 == 0) goto L_0x0016
                android.graphics.Paint r3 = r11.backgroundPaint
                int r1 = r1.getThemedColor(r2)
                r3.setColor(r1)
                goto L_0x0025
            L_0x0016:
                android.graphics.Paint r2 = r11.backgroundPaint
                int r1 = r1.navBarColor
                r2.setColor(r1)
                goto L_0x0025
            L_0x001e:
                android.graphics.Paint r1 = r11.backgroundPaint
                r2 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
                r1.setColor(r2)
            L_0x0025:
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                boolean r2 = r1.drawDoubleNavigationBar
                r3 = 1065353216(0x3var_, float:1.0)
                if (r2 == 0) goto L_0x0036
                boolean r1 = r1.shouldOverlayCameraViewOverNavBar()
                if (r1 != 0) goto L_0x0036
                r11.drawNavigationBar(r12, r3)
            L_0x0036:
                android.graphics.Paint r1 = r11.backgroundPaint
                int r1 = r1.getAlpha()
                r2 = 255(0xff, float:3.57E-43)
                r4 = 0
                if (r1 >= r2) goto L_0x00c4
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                boolean r2 = r1.drawNavigationBar
                if (r2 == 0) goto L_0x00c4
                boolean r2 = r1.scrollNavBar
                if (r2 != 0) goto L_0x0058
                r2 = 29
                if (r0 < r2) goto L_0x0056
                int r0 = r1.getAdditionalMandatoryOffsets()
                if (r0 <= 0) goto L_0x0056
                goto L_0x0058
            L_0x0056:
                r0 = 0
                goto L_0x0076
            L_0x0058:
                org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.ViewGroup r0 = r0.containerView
                int r0 = r0.getMeasuredHeight()
                float r0 = (float) r0
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.ViewGroup r1 = r1.containerView
                float r1 = r1.getTranslationY()
                float r0 = r0 - r1
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                int r1 = r1.getBottomInset()
                float r1 = (float) r1
                float r1 = r1 - r0
                float r0 = java.lang.Math.max(r4, r1)
            L_0x0076:
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                boolean r2 = r1.drawNavigationBar
                if (r2 == 0) goto L_0x0081
                int r1 = r1.getBottomInset()
                goto L_0x0082
            L_0x0081:
                r1 = 0
            L_0x0082:
                r12.save()
                org.telegram.ui.ActionBar.BottomSheet r2 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.ViewGroup r2 = r2.containerView
                int r2 = r2.getLeft()
                org.telegram.ui.ActionBar.BottomSheet r5 = org.telegram.ui.ActionBar.BottomSheet.this
                int r5 = r5.backgroundPaddingLeft
                int r2 = r2 + r5
                float r6 = (float) r2
                int r2 = r11.getMeasuredHeight()
                int r2 = r2 - r1
                float r1 = (float) r2
                float r1 = r1 + r0
                org.telegram.ui.ActionBar.BottomSheet r2 = org.telegram.ui.ActionBar.BottomSheet.this
                float r2 = r2.currentPanTranslationY
                float r7 = r1 - r2
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.ViewGroup r1 = r1.containerView
                int r1 = r1.getRight()
                org.telegram.ui.ActionBar.BottomSheet r2 = org.telegram.ui.ActionBar.BottomSheet.this
                int r2 = r2.backgroundPaddingLeft
                int r1 = r1 - r2
                float r8 = (float) r1
                int r1 = r11.getMeasuredHeight()
                float r1 = (float) r1
                float r9 = r1 + r0
                android.graphics.Region$Op r10 = android.graphics.Region.Op.DIFFERENCE
                r5 = r12
                r5.clipRect(r6, r7, r8, r9, r10)
                super.dispatchDraw(r12)
                r12.restore()
                goto L_0x00c7
            L_0x00c4:
                super.dispatchDraw(r12)
            L_0x00c7:
                org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.ActionBar.BottomSheet.this
                boolean r0 = r0.shouldOverlayCameraViewOverNavBar()
                if (r0 != 0) goto L_0x00df
                org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.ActionBar.BottomSheet.this
                boolean r1 = r0.drawDoubleNavigationBar
                if (r1 == 0) goto L_0x00dc
                r1 = 1060320051(0x3var_, float:0.7)
                float r0 = r0.navigationBarAlpha
                float r3 = r0 * r1
            L_0x00dc:
                r11.drawNavigationBar(r12, r3)
            L_0x00df:
                org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.ActionBar.BottomSheet.this
                boolean r1 = r0.drawNavigationBar
                if (r1 == 0) goto L_0x0134
                int r0 = r0.rightInset
                if (r0 == 0) goto L_0x0134
                org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.ActionBar.BottomSheet.this
                int r0 = r0.rightInset
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                int r1 = r1.leftInset
                if (r0 <= r1) goto L_0x0134
                org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.ActionBar.BottomSheet.this
                boolean r1 = r0.fullWidth
                if (r1 == 0) goto L_0x0134
                android.graphics.Point r1 = org.telegram.messenger.AndroidUtilities.displaySize
                int r2 = r1.x
                int r1 = r1.y
                if (r2 <= r1) goto L_0x0134
                android.view.ViewGroup r0 = r0.containerView
                int r0 = r0.getRight()
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                int r2 = r1.backgroundPaddingLeft
                int r0 = r0 - r2
                float r6 = (float) r0
                android.view.ViewGroup r0 = r1.containerView
                float r7 = r0.getTranslationY()
                org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.ViewGroup r0 = r0.containerView
                int r0 = r0.getRight()
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                int r1 = r1.rightInset
                int r0 = r0 + r1
                float r8 = (float) r0
                int r0 = r11.getMeasuredHeight()
                float r9 = (float) r0
                android.graphics.Paint r10 = r11.backgroundPaint
                r5 = r12
                r5.drawRect(r6, r7, r8, r9, r10)
            L_0x0134:
                org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.ActionBar.BottomSheet.this
                boolean r1 = r0.drawNavigationBar
                if (r1 == 0) goto L_0x017c
                int r0 = r0.leftInset
                if (r0 == 0) goto L_0x017c
                org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.ActionBar.BottomSheet.this
                int r0 = r0.leftInset
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                int r1 = r1.rightInset
                if (r0 <= r1) goto L_0x017c
                org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.ActionBar.BottomSheet.this
                boolean r1 = r0.fullWidth
                if (r1 == 0) goto L_0x017c
                android.graphics.Point r1 = org.telegram.messenger.AndroidUtilities.displaySize
                int r2 = r1.x
                int r1 = r1.y
                if (r2 <= r1) goto L_0x017c
                r6 = 0
                android.view.ViewGroup r0 = r0.containerView
                float r7 = r0.getTranslationY()
                org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.ViewGroup r0 = r0.containerView
                int r0 = r0.getLeft()
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                int r1 = r1.backgroundPaddingLeft
                int r0 = r0 + r1
                float r8 = (float) r0
                int r0 = r11.getMeasuredHeight()
                float r9 = (float) r0
                android.graphics.Paint r10 = r11.backgroundPaint
                r5 = r12
                r5.drawRect(r6, r7, r8, r9, r10)
            L_0x017c:
                org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.ViewGroup r0 = r0.containerView
                float r0 = r0.getTranslationY()
                int r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
                if (r0 >= 0) goto L_0x01d2
                android.graphics.Paint r0 = r11.backgroundPaint
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                java.lang.String r2 = r1.behindKeyboardColorKey
                if (r2 == 0) goto L_0x0195
                int r1 = r1.getThemedColor(r2)
                goto L_0x0197
            L_0x0195:
                int r1 = r1.behindKeyboardColor
            L_0x0197:
                r0.setColor(r1)
                org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.ViewGroup r0 = r0.containerView
                int r0 = r0.getLeft()
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                int r2 = r1.backgroundPaddingLeft
                int r0 = r0 + r2
                float r3 = (float) r0
                android.view.ViewGroup r0 = r1.containerView
                float r0 = r0.getY()
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.ViewGroup r1 = r1.containerView
                int r1 = r1.getMeasuredHeight()
                float r1 = (float) r1
                float r4 = r0 + r1
                org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.ViewGroup r0 = r0.containerView
                int r0 = r0.getRight()
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                int r1 = r1.backgroundPaddingLeft
                int r0 = r0 - r1
                float r5 = (float) r0
                int r0 = r11.getMeasuredHeight()
                float r6 = (float) r0
                android.graphics.Paint r7 = r11.backgroundPaint
                r2 = r12
                r2.drawRect(r3, r4, r5, r6, r7)
            L_0x01d2:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.BottomSheet.ContainerView.dispatchDraw(android.graphics.Canvas):void");
        }

        /* access modifiers changed from: protected */
        public boolean drawChild(Canvas canvas, View view, long j) {
            if (!(view instanceof CameraView)) {
                return super.drawChild(canvas, view, j);
            }
            if (BottomSheet.this.shouldOverlayCameraViewOverNavBar()) {
                drawNavigationBar(canvas, 1.0f);
            }
            return super.drawChild(canvas, view, j);
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Removed duplicated region for block: B:23:0x00a1  */
        /* JADX WARNING: Removed duplicated region for block: B:24:0x00a6  */
        /* JADX WARNING: Removed duplicated region for block: B:27:0x00c6  */
        /* JADX WARNING: Removed duplicated region for block: B:28:0x00cb  */
        /* JADX WARNING: Removed duplicated region for block: B:31:0x00e6  */
        /* JADX WARNING: Removed duplicated region for block: B:35:0x00f9  */
        /* JADX WARNING: Removed duplicated region for block: B:37:? A[RETURN, SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onDraw(android.graphics.Canvas r11) {
            /*
                r10 = this;
                android.graphics.Paint r0 = r10.backgroundPaint
                int r0 = r0.getAlpha()
                r1 = 0
                r2 = 255(0xff, float:3.57E-43)
                if (r0 >= r2) goto L_0x0089
                org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.ActionBar.BottomSheet.this
                boolean r2 = r0.drawNavigationBar
                if (r2 == 0) goto L_0x0089
                boolean r2 = r0.scrollNavBar
                r3 = 0
                if (r2 != 0) goto L_0x0022
                int r2 = android.os.Build.VERSION.SDK_INT
                r4 = 29
                if (r2 < r4) goto L_0x0040
                int r0 = r0.getAdditionalMandatoryOffsets()
                if (r0 <= 0) goto L_0x0040
            L_0x0022:
                org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.ViewGroup r0 = r0.containerView
                int r0 = r0.getMeasuredHeight()
                float r0 = (float) r0
                org.telegram.ui.ActionBar.BottomSheet r2 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.ViewGroup r2 = r2.containerView
                float r2 = r2.getTranslationY()
                float r0 = r0 - r2
                org.telegram.ui.ActionBar.BottomSheet r2 = org.telegram.ui.ActionBar.BottomSheet.this
                int r2 = r2.getBottomInset()
                float r2 = (float) r2
                float r2 = r2 - r0
                float r3 = java.lang.Math.max(r3, r2)
            L_0x0040:
                org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.ActionBar.BottomSheet.this
                boolean r2 = r0.drawNavigationBar
                if (r2 == 0) goto L_0x004b
                int r0 = r0.getBottomInset()
                goto L_0x004c
            L_0x004b:
                r0 = 0
            L_0x004c:
                r11.save()
                org.telegram.ui.ActionBar.BottomSheet r2 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.ViewGroup r2 = r2.containerView
                int r2 = r2.getLeft()
                org.telegram.ui.ActionBar.BottomSheet r4 = org.telegram.ui.ActionBar.BottomSheet.this
                int r4 = r4.backgroundPaddingLeft
                int r2 = r2 + r4
                float r5 = (float) r2
                int r2 = r10.getMeasuredHeight()
                int r2 = r2 - r0
                float r0 = (float) r2
                float r0 = r0 + r3
                org.telegram.ui.ActionBar.BottomSheet r2 = org.telegram.ui.ActionBar.BottomSheet.this
                float r2 = r2.currentPanTranslationY
                float r6 = r0 - r2
                org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.ViewGroup r0 = r0.containerView
                int r0 = r0.getRight()
                org.telegram.ui.ActionBar.BottomSheet r2 = org.telegram.ui.ActionBar.BottomSheet.this
                int r2 = r2.backgroundPaddingLeft
                int r0 = r0 - r2
                float r7 = (float) r0
                int r0 = r10.getMeasuredHeight()
                float r0 = (float) r0
                float r8 = r0 + r3
                android.graphics.Region$Op r9 = android.graphics.Region.Op.DIFFERENCE
                r4 = r11
                r4.clipRect(r5, r6, r7, r8, r9)
                r0 = 1
                goto L_0x008a
            L_0x0089:
                r0 = 0
            L_0x008a:
                super.onDraw(r11)
                org.telegram.ui.ActionBar.BottomSheet r2 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.WindowInsets r2 = r2.lastInsets
                if (r2 == 0) goto L_0x00f2
                int r2 = r10.keyboardHeight
                if (r2 == 0) goto L_0x00f2
                android.graphics.Paint r2 = r10.backgroundPaint
                org.telegram.ui.ActionBar.BottomSheet r3 = org.telegram.ui.ActionBar.BottomSheet.this
                java.lang.String r4 = r3.behindKeyboardColorKey
                if (r4 == 0) goto L_0x00a6
                int r3 = r3.getThemedColor(r4)
                goto L_0x00a8
            L_0x00a6:
                int r3 = r3.behindKeyboardColor
            L_0x00a8:
                r2.setColor(r3)
                org.telegram.ui.ActionBar.BottomSheet r2 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.ViewGroup r2 = r2.containerView
                int r2 = r2.getLeft()
                org.telegram.ui.ActionBar.BottomSheet r3 = org.telegram.ui.ActionBar.BottomSheet.this
                int r3 = r3.backgroundPaddingLeft
                int r2 = r2 + r3
                float r4 = (float) r2
                int r2 = r10.getMeasuredHeight()
                int r3 = r10.keyboardHeight
                int r2 = r2 - r3
                org.telegram.ui.ActionBar.BottomSheet r3 = org.telegram.ui.ActionBar.BottomSheet.this
                boolean r5 = r3.drawNavigationBar
                if (r5 == 0) goto L_0x00cb
                int r3 = r3.getBottomInset()
                goto L_0x00cc
            L_0x00cb:
                r3 = 0
            L_0x00cc:
                int r2 = r2 - r3
                float r5 = (float) r2
                org.telegram.ui.ActionBar.BottomSheet r2 = org.telegram.ui.ActionBar.BottomSheet.this
                android.view.ViewGroup r2 = r2.containerView
                int r2 = r2.getRight()
                org.telegram.ui.ActionBar.BottomSheet r3 = org.telegram.ui.ActionBar.BottomSheet.this
                int r3 = r3.backgroundPaddingLeft
                int r2 = r2 - r3
                float r6 = (float) r2
                int r2 = r10.getMeasuredHeight()
                org.telegram.ui.ActionBar.BottomSheet r3 = org.telegram.ui.ActionBar.BottomSheet.this
                boolean r7 = r3.drawNavigationBar
                if (r7 == 0) goto L_0x00ea
                int r1 = r3.getBottomInset()
            L_0x00ea:
                int r2 = r2 - r1
                float r7 = (float) r2
                android.graphics.Paint r8 = r10.backgroundPaint
                r3 = r11
                r3.drawRect(r4, r5, r6, r7, r8)
            L_0x00f2:
                org.telegram.ui.ActionBar.BottomSheet r1 = org.telegram.ui.ActionBar.BottomSheet.this
                r1.onContainerDraw(r11)
                if (r0 == 0) goto L_0x00fc
                r11.restore()
            L_0x00fc:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.BottomSheet.ContainerView.onDraw(android.graphics.Canvas):void");
        }

        public void drawNavigationBar(Canvas canvas, float f) {
            float f2;
            int i = Build.VERSION.SDK_INT;
            if (i >= 26) {
                BottomSheet bottomSheet = BottomSheet.this;
                String str = bottomSheet.navBarColorKey;
                if (str != null) {
                    this.backgroundPaint.setColor(bottomSheet.getThemedColor(str));
                } else {
                    this.backgroundPaint.setColor(bottomSheet.navBarColor);
                }
            } else {
                this.backgroundPaint.setColor(-16777216);
            }
            BottomSheet bottomSheet2 = BottomSheet.this;
            float f3 = 0.0f;
            if ((bottomSheet2.drawNavigationBar && bottomSheet2.bottomInset != 0) || BottomSheet.this.currentPanTranslationY != 0.0f) {
                BottomSheet bottomSheet3 = BottomSheet.this;
                int bottomInset = bottomSheet3.drawNavigationBar ? bottomSheet3.getBottomInset() : 0;
                BottomSheet bottomSheet4 = BottomSheet.this;
                if (bottomSheet4.scrollNavBar || (i >= 29 && bottomSheet4.getAdditionalMandatoryOffsets() > 0)) {
                    BottomSheet bottomSheet5 = BottomSheet.this;
                    if (bottomSheet5.drawDoubleNavigationBar) {
                        f2 = Math.max(0.0f, Math.min(((float) bottomInset) - bottomSheet5.currentPanTranslationY, BottomSheet.this.containerView.getTranslationY()));
                    } else {
                        f2 = Math.max(0.0f, ((float) BottomSheet.this.getBottomInset()) - (((float) bottomSheet5.containerView.getMeasuredHeight()) - BottomSheet.this.containerView.getTranslationY()));
                    }
                } else {
                    f2 = 0.0f;
                }
                int alpha = this.backgroundPaint.getAlpha();
                if (f < 1.0f) {
                    this.backgroundPaint.setAlpha((int) (((float) alpha) * f));
                }
                canvas.drawRect((float) (BottomSheet.this.containerView.getLeft() + BottomSheet.this.backgroundPaddingLeft), (((float) (getMeasuredHeight() - bottomInset)) + f2) - BottomSheet.this.currentPanTranslationY, (float) (BottomSheet.this.containerView.getRight() - BottomSheet.this.backgroundPaddingLeft), ((float) getMeasuredHeight()) + f2, this.backgroundPaint);
                this.backgroundPaint.setAlpha(alpha);
                if (BottomSheet.this.overlayDrawNavBarColor != 0) {
                    this.backgroundPaint.setColor(BottomSheet.this.overlayDrawNavBarColor);
                    int alpha2 = this.backgroundPaint.getAlpha();
                    if (f < 1.0f) {
                        this.backgroundPaint.setAlpha((int) (((float) alpha2) * f));
                    } else {
                        f3 = f2;
                    }
                    canvas.drawRect((float) (BottomSheet.this.containerView.getLeft() + BottomSheet.this.backgroundPaddingLeft), (((float) (getMeasuredHeight() - bottomInset)) + f3) - BottomSheet.this.currentPanTranslationY, (float) (BottomSheet.this.containerView.getRight() - BottomSheet.this.backgroundPaddingLeft), ((float) getMeasuredHeight()) + f3, this.backgroundPaint);
                    this.backgroundPaint.setAlpha(alpha2);
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public int getBottomSheetWidth(boolean z, int i, int i2) {
        return z ? i : (int) Math.max(((float) i) * 0.8f, (float) Math.min(AndroidUtilities.dp(480.0f), i));
    }

    public void setHideSystemVerticalInsets(boolean z) {
        float[] fArr = new float[2];
        fArr[0] = this.hideSystemVerticalInsetsProgress;
        fArr[1] = z ? 1.0f : 0.0f;
        ValueAnimator duration = ValueAnimator.ofFloat(fArr).setDuration(180);
        duration.setInterpolator(CubicBezierInterpolator.DEFAULT);
        duration.addUpdateListener(new BottomSheet$$ExternalSyntheticLambda1(this));
        duration.start();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setHideSystemVerticalInsets$0(ValueAnimator valueAnimator) {
        this.hideSystemVerticalInsetsProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.container.requestLayout();
        this.containerView.requestLayout();
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
        public boolean isSelected;
        private final Theme.ResourcesProvider resourcesProvider;
        /* access modifiers changed from: private */
        public TextView textView;

        public BottomSheetCell(Context context, int i) {
            this(context, i, (Theme.ResourcesProvider) null);
        }

        public BottomSheetCell(Context context, int i, Theme.ResourcesProvider resourcesProvider2) {
            super(context);
            this.isSelected = false;
            this.resourcesProvider = resourcesProvider2;
            this.currentType = i;
            setBackgroundDrawable(Theme.getSelectorDrawable(false));
            ImageView imageView2 = new ImageView(context);
            this.imageView = imageView2;
            imageView2.setScaleType(ImageView.ScaleType.CENTER);
            this.imageView.setColorFilter(new PorterDuffColorFilter(getThemedColor("dialogIcon"), PorterDuff.Mode.MULTIPLY));
            int i2 = 5;
            addView(this.imageView, LayoutHelper.createFrame(56, 48, (LocaleController.isRTL ? 5 : 3) | 16));
            TextView textView2 = new TextView(context);
            this.textView = textView2;
            textView2.setLines(1);
            this.textView.setSingleLine(true);
            this.textView.setGravity(1);
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
            if (i == 0) {
                this.textView.setTextColor(getThemedColor("dialogTextBlack"));
                this.textView.setTextSize(1, 16.0f);
                addView(this.textView, LayoutHelper.createFrame(-2, -2, (!LocaleController.isRTL ? 3 : i2) | 16));
            } else if (i == 1) {
                this.textView.setGravity(17);
                this.textView.setTextColor(getThemedColor("dialogTextBlack"));
                this.textView.setTextSize(1, 14.0f);
                this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                addView(this.textView, LayoutHelper.createFrame(-1, -1.0f));
            } else if (i == 2) {
                this.textView.setGravity(17);
                this.textView.setTextColor(getThemedColor("featuredStickers_buttonText"));
                this.textView.setTextSize(1, 14.0f);
                this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                this.textView.setBackground(Theme.AdaptiveRipple.filledRect(getThemedColor("featuredStickers_addButton"), 4.0f));
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

        private int getThemedColor(String str) {
            Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
            Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(str) : null;
            return color != null ? color.intValue() : Theme.getColor(str);
        }

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            if (this.isSelected) {
                accessibilityNodeInfo.setSelected(true);
            }
        }
    }

    public void setAllowNestedScroll(boolean z) {
        this.allowNestedScroll = z;
        if (!z) {
            this.containerView.setTranslationY(0.0f);
        }
    }

    public BottomSheet(Context context, boolean z) {
        this(context, z, (Theme.ResourcesProvider) null);
    }

    public BottomSheet(Context context, boolean z, Theme.ResourcesProvider resourcesProvider2) {
        super(context, NUM);
        this.currentAccount = UserConfig.selectedAccount;
        this.allowDrawContent = true;
        this.useHardwareLayer = true;
        this.backDrawable = new ColorDrawable(-16777216) {
            public void setAlpha(int i) {
                super.setAlpha(i);
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
        this.dismissRunnable = new BottomSheet$$ExternalSyntheticLambda6(this);
        this.navigationBarAlpha = 0.0f;
        this.navBarColorKey = "windowBackgroundGray";
        this.useBackgroundTopPadding = true;
        this.resourcesProvider = resourcesProvider2;
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
        mutate.setColorFilter(new PorterDuffColorFilter(getThemedColor("dialogBackground"), PorterDuff.Mode.MULTIPLY));
        this.shadowDrawable.getPadding(rect);
        this.backgroundPaddingLeft = rect.left;
        this.backgroundPaddingTop = rect.top;
        AnonymousClass2 r8 = new ContainerView(getContext()) {
            public boolean drawChild(Canvas canvas, View view, long j) {
                try {
                    return BottomSheet.this.allowDrawContent && super.drawChild(canvas, view, j);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                    return true;
                }
            }

            /* access modifiers changed from: protected */
            public void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
                BottomSheet.this.mainContainerDispatchDraw(canvas);
            }
        };
        this.container = r8;
        r8.setBackgroundDrawable(this.backDrawable);
        this.focusable = z;
        if (i >= 21) {
            this.container.setFitsSystemWindows(true);
            this.container.setOnApplyWindowInsetsListener(new BottomSheet$$ExternalSyntheticLambda3(this));
            if (i >= 30) {
                this.container.setSystemUiVisibility(1792);
            } else {
                this.container.setSystemUiVisibility(1280);
            }
        }
        this.backDrawable.setAlpha(0);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ WindowInsets lambda$new$1(View view, WindowInsets windowInsets) {
        int systemWindowInsetTop = windowInsets.getSystemWindowInsetTop();
        if ((systemWindowInsetTop != 0 || AndroidUtilities.isInMultiwindow) && this.statusBarHeight != systemWindowInsetTop) {
            this.statusBarHeight = systemWindowInsetTop;
        }
        this.lastInsets = windowInsets;
        view.requestLayout();
        if (Build.VERSION.SDK_INT >= 30) {
            return WindowInsets.CONSUMED;
        }
        return windowInsets.consumeSystemWindowInsets();
    }

    public void fixNavigationBar() {
        fixNavigationBar(getThemedColor("dialogBackground"));
    }

    public void fixNavigationBar(int i) {
        this.drawNavigationBar = true;
        this.drawDoubleNavigationBar = true;
        this.scrollNavBar = true;
        this.navBarColorKey = null;
        this.navBarColor = i;
        setOverlayNavBarColor(i);
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Window window = getWindow();
        window.setWindowAnimations(NUM);
        setContentView(this.container, new ViewGroup.LayoutParams(-1, -1));
        Drawable drawable = null;
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
        int i = 48;
        if (this.title != null) {
            AnonymousClass4 r22 = new TextView(getContext()) {
                /* access modifiers changed from: protected */
                public void onMeasure(int i, int i2) {
                    super.onMeasure(i, i2);
                    if (BottomSheet.this.multipleLinesTitle) {
                        int measuredHeight = getMeasuredHeight();
                        if (BottomSheet.this.customView != null) {
                            ((ViewGroup.MarginLayoutParams) BottomSheet.this.customView.getLayoutParams()).topMargin = measuredHeight;
                        } else if (BottomSheet.this.containerView != null) {
                            for (int i3 = 1; i3 < BottomSheet.this.containerView.getChildCount(); i3++) {
                                View childAt = BottomSheet.this.containerView.getChildAt(i3);
                                if (childAt instanceof BottomSheetCell) {
                                    ((ViewGroup.MarginLayoutParams) childAt.getLayoutParams()).topMargin = measuredHeight;
                                    measuredHeight += AndroidUtilities.dp(48.0f);
                                }
                            }
                        }
                    }
                }
            };
            this.titleView = r22;
            r22.setText(this.title);
            if (this.bigTitle) {
                this.titleView.setTextColor(getThemedColor("dialogTextBlack"));
                this.titleView.setTextSize(1, 20.0f);
                this.titleView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                this.titleView.setPadding(AndroidUtilities.dp(21.0f), AndroidUtilities.dp(this.multipleLinesTitle ? 14.0f : 6.0f), AndroidUtilities.dp(21.0f), AndroidUtilities.dp(8.0f));
            } else {
                this.titleView.setTextColor(getThemedColor("dialogTextGray2"));
                this.titleView.setTextSize(1, 16.0f);
                this.titleView.setPadding(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(this.multipleLinesTitle ? 8.0f : 0.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(8.0f));
            }
            if (this.multipleLinesTitle) {
                this.titleView.setSingleLine(false);
                this.titleView.setMaxLines(5);
                this.titleView.setEllipsize(TextUtils.TruncateAt.END);
            } else {
                this.titleView.setLines(1);
                this.titleView.setSingleLine(true);
                this.titleView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
            }
            this.titleView.setGravity(16);
            this.containerView.addView(this.titleView, LayoutHelper.createFrame(-1, this.multipleLinesTitle ? -2.0f : (float) 48));
            this.titleView.setOnTouchListener(BottomSheet$$ExternalSyntheticLambda5.INSTANCE);
        } else {
            i = 0;
        }
        View view = this.customView;
        if (view != null) {
            if (view.getParent() != null) {
                ((ViewGroup) this.customView.getParent()).removeView(this.customView);
            }
            if (!this.useBackgroundTopPadding) {
                this.containerView.setClipToPadding(false);
                this.containerView.setClipChildren(false);
                this.container.setClipToPadding(false);
                this.container.setClipChildren(false);
                this.containerView.addView(this.customView, LayoutHelper.createFrame(-1, -2.0f, 51, 0.0f, (float) ((-this.backgroundPaddingTop) + i), 0.0f, 0.0f));
            } else {
                this.containerView.addView(this.customView, LayoutHelper.createFrame(-1, -2.0f, 51, 0.0f, (float) i, 0.0f, 0.0f));
            }
        } else if (this.items != null) {
            int i2 = 0;
            while (true) {
                CharSequence[] charSequenceArr = this.items;
                if (i2 >= charSequenceArr.length) {
                    break;
                }
                if (charSequenceArr[i2] != null) {
                    BottomSheetCell bottomSheetCell = new BottomSheetCell(getContext(), 0, this.resourcesProvider);
                    CharSequence charSequence = this.items[i2];
                    int[] iArr = this.itemIcons;
                    bottomSheetCell.setTextAndIcon(charSequence, iArr != null ? iArr[i2] : 0, drawable, this.bigTitle);
                    this.containerView.addView(bottomSheetCell, LayoutHelper.createFrame(-1, 48.0f, 51, 0.0f, (float) i, 0.0f, 0.0f));
                    i += 48;
                    bottomSheetCell.setTag(Integer.valueOf(i2));
                    bottomSheetCell.setOnClickListener(new BottomSheet$$ExternalSyntheticLambda4(this));
                    this.itemViews.add(bottomSheetCell);
                }
                i2++;
                drawable = null;
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
    public /* synthetic */ void lambda$onCreate$3(View view) {
        dismissWithButtonClick(((Integer) view.getTag()).intValue());
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
    }

    public void setUseLightStatusBar(boolean z) {
        this.useLightStatusBar = z;
        if (Build.VERSION.SDK_INT >= 23) {
            int color = Theme.getColor("actionBarDefault", (boolean[]) null, true);
            int systemUiVisibility = this.container.getSystemUiVisibility();
            this.container.setSystemUiVisibility((!this.useLightStatusBar || color != -1) ? systemUiVisibility & -8193 : systemUiVisibility | 8192);
        }
    }

    public boolean isFocusable() {
        return this.focusable;
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
        int i2 = Build.VERSION.SDK_INT;
        if (i2 >= 18) {
            this.layoutCount = 2;
            ViewGroup viewGroup = this.containerView;
            if (i2 >= 21) {
                f = (1.0f - this.hideSystemVerticalInsetsProgress) * ((float) AndroidUtilities.statusBarHeight);
            }
            float measuredHeight = f + ((float) viewGroup.getMeasuredHeight());
            if (this.scrollNavBar) {
                i = getBottomInset();
            }
            viewGroup.setTranslationY(measuredHeight + ((float) i));
            AnonymousClass5 r0 = new Runnable() {
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

    public void setCanDismissWithSwipe(boolean z) {
        this.canDismissWithSwipe = z;
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

    /* access modifiers changed from: protected */
    public void onDismissWithTouchOutside() {
        dismiss();
    }

    public TextView getTitleView() {
        return this.titleView;
    }

    /* access modifiers changed from: protected */
    public void cancelSheetAnimation() {
        AnimatorSet animatorSet = this.currentSheetAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.currentSheetAnimation = null;
        }
        this.currentSheetAnimationType = 0;
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
                this.containerView.setTranslationY((float) (getContainerViewHeight() + this.container.keyboardHeight + AndroidUtilities.dp(10.0f) + (this.scrollNavBar ? getBottomInset() : 0)));
                this.currentSheetAnimationType = 1;
                ValueAnimator valueAnimator = this.navigationBarAnimation;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                }
                ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{this.navigationBarAlpha, 1.0f});
                this.navigationBarAnimation = ofFloat;
                ofFloat.addUpdateListener(new BottomSheet$$ExternalSyntheticLambda0(this));
                AnimatorSet animatorSet = new AnimatorSet();
                this.currentSheetAnimation = animatorSet;
                Animator[] animatorArr = new Animator[3];
                animatorArr[0] = ObjectAnimator.ofFloat(this.containerView, View.TRANSLATION_Y, new float[]{0.0f});
                ColorDrawable colorDrawable = this.backDrawable;
                Property<ColorDrawable, Integer> property = AnimationProperties.COLOR_DRAWABLE_ALPHA;
                int[] iArr = new int[1];
                iArr[0] = this.dimBehind ? this.dimBehindAlpha : 0;
                animatorArr[1] = ObjectAnimator.ofInt(colorDrawable, property, iArr);
                animatorArr[2] = this.navigationBarAnimation;
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
                            BottomSheetDelegateInterface bottomSheetDelegateInterface = bottomSheet.delegate;
                            if (bottomSheetDelegateInterface != null) {
                                bottomSheetDelegateInterface.onOpenAnimationEnd();
                            }
                            BottomSheet bottomSheet2 = BottomSheet.this;
                            if (bottomSheet2.useHardwareLayer) {
                                bottomSheet2.container.setLayerType(0, (Paint) null);
                            }
                            BottomSheet bottomSheet3 = BottomSheet.this;
                            if (bottomSheet3.isFullscreen) {
                                WindowManager.LayoutParams attributes = bottomSheet3.getWindow().getAttributes();
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

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$startOpenAnimation$4(ValueAnimator valueAnimator) {
        this.navigationBarAlpha = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        ContainerView containerView2 = this.container;
        if (containerView2 != null) {
            containerView2.invalidate();
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

    public int getTag() {
        return this.tag;
    }

    public void setDimBehind(boolean z) {
        this.dimBehind = z;
    }

    public void setDimBehindAlpha(int i) {
        this.dimBehindAlpha = i;
    }

    public void setItemText(int i, CharSequence charSequence) {
        if (i >= 0 && i < this.itemViews.size()) {
            this.itemViews.get(i).textView.setText(charSequence);
        }
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
            Animator[] animatorArr = new Animator[2];
            ViewGroup viewGroup = this.containerView;
            Property property = View.TRANSLATION_Y;
            float[] fArr = new float[1];
            fArr[0] = (float) (getContainerViewHeight() + this.container.keyboardHeight + AndroidUtilities.dp(10.0f) + (this.scrollNavBar ? getBottomInset() : 0));
            animatorArr[0] = ObjectAnimator.ofFloat(viewGroup, property, fArr);
            animatorArr[1] = ObjectAnimator.ofInt(this.backDrawable, AnimationProperties.COLOR_DRAWABLE_ALPHA, new int[]{0});
            animatorSet.playTogether(animatorArr);
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
                        AndroidUtilities.runOnUIThread(new BottomSheet$7$$ExternalSyntheticLambda0(this));
                    }
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 512);
                }

                /* access modifiers changed from: private */
                public /* synthetic */ void lambda$onAnimationEnd$0() {
                    if (BottomSheet.this.onHideListener != null) {
                        BottomSheet.this.onHideListener.onDismiss(BottomSheet.this);
                    }
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

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (this.dismissed) {
            return false;
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    public int getContainerViewHeight() {
        ViewGroup viewGroup = this.containerView;
        if (viewGroup == null) {
            return 0;
        }
        return viewGroup.getMeasuredHeight();
    }

    public void dismiss() {
        long j;
        ObjectAnimator objectAnimator;
        BottomSheetDelegateInterface bottomSheetDelegateInterface = this.delegate;
        if ((bottomSheetDelegateInterface == null || bottomSheetDelegateInterface.canDismiss()) && !this.dismissed) {
            this.dismissed = true;
            DialogInterface.OnDismissListener onDismissListener = this.onHideListener;
            if (onDismissListener != null) {
                onDismissListener.onDismiss(this);
            }
            cancelSheetAnimation();
            onDismissAnimationStart();
            if (!this.allowCustomAnimation || !onCustomCloseAnimation()) {
                this.currentSheetAnimationType = 2;
                this.currentSheetAnimation = new AnimatorSet();
                ValueAnimator valueAnimator = this.navigationBarAnimation;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                }
                ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{this.navigationBarAlpha, 0.0f});
                this.navigationBarAnimation = ofFloat;
                ofFloat.addUpdateListener(new BottomSheet$$ExternalSyntheticLambda2(this));
                AnimatorSet animatorSet = this.currentSheetAnimation;
                Animator[] animatorArr = new Animator[3];
                ViewGroup viewGroup = this.containerView;
                if (viewGroup == null) {
                    objectAnimator = null;
                } else {
                    Property property = View.TRANSLATION_Y;
                    float[] fArr = new float[1];
                    fArr[0] = (float) (getContainerViewHeight() + this.container.keyboardHeight + AndroidUtilities.dp(10.0f) + (this.scrollNavBar ? getBottomInset() : 0));
                    objectAnimator = ObjectAnimator.ofFloat(viewGroup, property, fArr);
                }
                animatorArr[0] = objectAnimator;
                animatorArr[1] = ObjectAnimator.ofInt(this.backDrawable, AnimationProperties.COLOR_DRAWABLE_ALPHA, new int[]{0});
                animatorArr[2] = this.navigationBarAnimation;
                animatorSet.playTogether(animatorArr);
                j = 250;
                this.currentSheetAnimation.setDuration(250);
                this.currentSheetAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                this.currentSheetAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        AnimatorSet animatorSet = BottomSheet.this.currentSheetAnimation;
                        if (animatorSet != null && animatorSet.equals(animator)) {
                            BottomSheet bottomSheet = BottomSheet.this;
                            bottomSheet.currentSheetAnimation = null;
                            bottomSheet.currentSheetAnimationType = 0;
                            AndroidUtilities.runOnUIThread(new BottomSheet$8$$ExternalSyntheticLambda0(this));
                        }
                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 512);
                    }

                    /* access modifiers changed from: private */
                    public /* synthetic */ void lambda$onAnimationEnd$0() {
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

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$dismiss$5(ValueAnimator valueAnimator) {
        this.navigationBarAlpha = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        ContainerView containerView2 = this.container;
        if (containerView2 != null) {
            containerView2.invalidate();
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
            this(context, false);
        }

        public Builder(Context context, boolean z) {
            this(context, z, (Theme.ResourcesProvider) null);
        }

        public Builder(Context context, boolean z, Theme.ResourcesProvider resourcesProvider) {
            BottomSheet bottomSheet2 = new BottomSheet(context, z, resourcesProvider);
            this.bottomSheet = bottomSheet2;
            bottomSheet2.fixNavigationBar();
        }

        public Builder(Context context, boolean z, Theme.ResourcesProvider resourcesProvider, int i) {
            BottomSheet bottomSheet2 = new BottomSheet(context, z, resourcesProvider);
            this.bottomSheet = bottomSheet2;
            bottomSheet2.setBackgroundColor(i);
            this.bottomSheet.fixNavigationBar(i);
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

        public Builder setTitleMultipleLines(boolean z) {
            boolean unused = this.bottomSheet.multipleLinesTitle = z;
            return this;
        }

        public BottomSheet create() {
            return this.bottomSheet;
        }

        public BottomSheet setDimBehind(boolean z) {
            BottomSheet bottomSheet2 = this.bottomSheet;
            bottomSheet2.dimBehind = z;
            return bottomSheet2;
        }

        public BottomSheet show() {
            this.bottomSheet.show();
            return this.bottomSheet;
        }

        public Builder setUseHardwareLayer(boolean z) {
            this.bottomSheet.useHardwareLayer = z;
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

        public Builder setOnPreDismissListener(DialogInterface.OnDismissListener onDismissListener) {
            this.bottomSheet.setOnHideListener(onDismissListener);
            return this;
        }
    }

    public int getLeftInset() {
        WindowInsets windowInsets = this.lastInsets;
        if (windowInsets == null || Build.VERSION.SDK_INT < 21) {
            return 0;
        }
        return (int) (((float) windowInsets.getSystemWindowInsetLeft()) * (1.0f - this.hideSystemVerticalInsetsProgress));
    }

    public int getRightInset() {
        WindowInsets windowInsets = this.lastInsets;
        if (windowInsets == null || Build.VERSION.SDK_INT < 21) {
            return 0;
        }
        return (int) (((float) windowInsets.getSystemWindowInsetRight()) * (1.0f - this.hideSystemVerticalInsetsProgress));
    }

    public int getStatusBarHeight() {
        return (int) (((float) this.statusBarHeight) * (1.0f - this.hideSystemVerticalInsetsProgress));
    }

    public int getBottomInset() {
        return (int) (((float) this.bottomInset) * (1.0f - this.hideSystemVerticalInsetsProgress));
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
    public int getThemedColor(String str) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
    }

    public void setOpenNoDelay(boolean z) {
        this.openNoDelay = z;
    }

    public int getBackgroundPaddingLeft() {
        return this.backgroundPaddingLeft;
    }
}
