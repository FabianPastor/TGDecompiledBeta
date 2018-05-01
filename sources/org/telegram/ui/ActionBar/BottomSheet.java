package org.telegram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnApplyWindowInsetsListener;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.Components.LayoutHelper;

public class BottomSheet extends Dialog {
    protected static int backgroundPaddingLeft;
    protected static int backgroundPaddingTop;
    private AccelerateInterpolator accelerateInterpolator = new AccelerateInterpolator();
    private boolean allowCustomAnimation = true;
    private boolean allowDrawContent = true;
    private boolean allowNestedScroll = true;
    private boolean applyBottomPadding = true;
    private boolean applyTopPadding = true;
    protected ColorDrawable backDrawable = new ColorDrawable(Theme.ACTION_BAR_VIDEO_EDIT_COLOR);
    protected ContainerView container;
    protected ViewGroup containerView;
    protected int currentAccount = UserConfig.selectedAccount;
    protected AnimatorSet currentSheetAnimation;
    private View customView;
    private DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
    private BottomSheetDelegateInterface delegate;
    private boolean dismissed;
    private boolean focusable;
    protected boolean fullWidth;
    private int[] itemIcons;
    private ArrayList<BottomSheetCell> itemViews = new ArrayList();
    private CharSequence[] items;
    private WindowInsets lastInsets;
    private int layoutCount;
    protected View nestedScrollChild;
    private OnClickListener onClickListener;
    private Drawable shadowDrawable;
    private boolean showWithoutAnimation;
    private Runnable startAnimationRunnable;
    private int tag;
    private CharSequence title;
    private int touchSlop;
    private boolean useFastDismiss;
    private boolean useHardwareLayer = true;

    /* renamed from: org.telegram.ui.ActionBar.BottomSheet$2 */
    class C07442 implements OnApplyWindowInsetsListener {
        C07442() {
        }

        @SuppressLint({"NewApi"})
        public WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
            BottomSheet.this.lastInsets = windowInsets;
            view.requestLayout();
            return windowInsets.consumeSystemWindowInsets();
        }
    }

    /* renamed from: org.telegram.ui.ActionBar.BottomSheet$4 */
    class C07464 implements OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }

        C07464() {
        }
    }

    /* renamed from: org.telegram.ui.ActionBar.BottomSheet$5 */
    class C07475 implements View.OnClickListener {
        C07475() {
        }

        public void onClick(View view) {
            BottomSheet.this.dismissWithButtonClick(((Integer) view.getTag()).intValue());
        }
    }

    /* renamed from: org.telegram.ui.ActionBar.BottomSheet$6 */
    class C07486 implements Runnable {
        C07486() {
        }

        public void run() {
            if (BottomSheet.this.startAnimationRunnable == this) {
                if (!BottomSheet.this.dismissed) {
                    BottomSheet.this.startAnimationRunnable = null;
                    BottomSheet.this.startOpenAnimation();
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.ActionBar.BottomSheet$7 */
    class C07497 extends AnimatorListenerAdapter {
        C07497() {
        }

        public void onAnimationEnd(Animator animator) {
            if (BottomSheet.this.currentSheetAnimation != null && BottomSheet.this.currentSheetAnimation.equals(animator) != null) {
                BottomSheet.this.currentSheetAnimation = null;
                if (BottomSheet.this.delegate != null) {
                    BottomSheet.this.delegate.onOpenAnimationEnd();
                }
                if (BottomSheet.this.useHardwareLayer != null) {
                    BottomSheet.this.container.setLayerType(0, null);
                }
            }
        }

        public void onAnimationCancel(Animator animator) {
            if (BottomSheet.this.currentSheetAnimation != null && BottomSheet.this.currentSheetAnimation.equals(animator) != null) {
                BottomSheet.this.currentSheetAnimation = null;
            }
        }
    }

    /* renamed from: org.telegram.ui.ActionBar.BottomSheet$9 */
    class C07539 extends AnimatorListenerAdapter {

        /* renamed from: org.telegram.ui.ActionBar.BottomSheet$9$1 */
        class C07521 implements Runnable {
            C07521() {
            }

            public void run() {
                try {
                    BottomSheet.this.dismissInternal();
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        }

        C07539() {
        }

        public void onAnimationEnd(Animator animator) {
            if (BottomSheet.this.currentSheetAnimation != null && BottomSheet.this.currentSheetAnimation.equals(animator) != null) {
                BottomSheet.this.currentSheetAnimation = null;
                AndroidUtilities.runOnUIThread(new C07521());
            }
        }

        public void onAnimationCancel(Animator animator) {
            if (BottomSheet.this.currentSheetAnimation != null && BottomSheet.this.currentSheetAnimation.equals(animator) != null) {
                BottomSheet.this.currentSheetAnimation = null;
            }
        }
    }

    public static class BottomSheetCell extends FrameLayout {
        private ImageView imageView;
        private TextView textView;

        public BottomSheetCell(Context context, int i) {
            super(context);
            setBackgroundDrawable(Theme.getSelectorDrawable(false));
            setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
            this.imageView = new ImageView(context);
            this.imageView.setScaleType(ScaleType.CENTER);
            this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogIcon), Mode.MULTIPLY));
            int i2 = 3;
            addView(this.imageView, LayoutHelper.createFrame(24, 24, (LocaleController.isRTL ? 5 : 3) | 16));
            this.textView = new TextView(context);
            this.textView.setLines(1);
            this.textView.setSingleLine(true);
            this.textView.setGravity(1);
            this.textView.setEllipsize(TruncateAt.END);
            if (i == 0) {
                this.textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                this.textView.setTextSize(1, 16.0f);
                context = this.textView;
                if (LocaleController.isRTL != 0) {
                    i2 = 5;
                }
                addView(context, LayoutHelper.createFrame(-2, -2, i2 | 16));
            } else if (i == 1) {
                this.textView.setGravity(17);
                this.textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                this.textView.setTextSize(1, NUM);
                this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                addView(this.textView, LayoutHelper.createFrame(-1, -1.0f));
            }
        }

        protected void onMeasure(int i, int i2) {
            super.onMeasure(i, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(NUM), NUM));
        }

        public void setTextColor(int i) {
            this.textView.setTextColor(i);
        }

        public void setGravity(int i) {
            this.textView.setGravity(i);
        }

        public void setTextAndIcon(CharSequence charSequence, int i) {
            this.textView.setText(charSequence);
            if (i != 0) {
                this.imageView.setImageResource(i);
                this.imageView.setVisibility(0);
                this.textView.setPadding(LocaleController.isRTL ? 0 : AndroidUtilities.dp(56.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(56.0f) : 0, 0);
                return;
            }
            this.imageView.setVisibility(4);
            this.textView.setPadding(0, 0, 0, 0);
        }
    }

    public interface BottomSheetDelegateInterface {
        boolean canDismiss();

        void onOpenAnimationEnd();

        void onOpenAnimationStart();
    }

    public static class Builder {
        private BottomSheet bottomSheet;

        public Builder(Context context) {
            this.bottomSheet = new BottomSheet(context, false);
        }

        public Builder(Context context, boolean z) {
            this.bottomSheet = new BottomSheet(context, z);
        }

        public Builder setItems(CharSequence[] charSequenceArr, OnClickListener onClickListener) {
            this.bottomSheet.items = charSequenceArr;
            this.bottomSheet.onClickListener = onClickListener;
            return this;
        }

        public Builder setItems(CharSequence[] charSequenceArr, int[] iArr, OnClickListener onClickListener) {
            this.bottomSheet.items = charSequenceArr;
            this.bottomSheet.itemIcons = iArr;
            this.bottomSheet.onClickListener = onClickListener;
            return this;
        }

        public Builder setCustomView(View view) {
            this.bottomSheet.customView = view;
            return this;
        }

        public Builder setTitle(CharSequence charSequence) {
            this.bottomSheet.title = charSequence;
            return this;
        }

        public BottomSheet create() {
            return this.bottomSheet;
        }

        public BottomSheet show() {
            this.bottomSheet.show();
            return this.bottomSheet;
        }

        public Builder setTag(int i) {
            this.bottomSheet.tag = i;
            return this;
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
            this.bottomSheet.applyTopPadding = z;
            return this;
        }

        public Builder setApplyBottomPadding(boolean z) {
            this.bottomSheet.applyBottomPadding = z;
            return this;
        }

        public BottomSheet setUseFullWidth(boolean z) {
            this.bottomSheet.fullWidth = z;
            return this.bottomSheet;
        }
    }

    public static class BottomSheetDelegate implements BottomSheetDelegateInterface {
        public boolean canDismiss() {
            return true;
        }

        public void onOpenAnimationEnd() {
        }

        public void onOpenAnimationStart() {
        }
    }

    protected class ContainerView extends FrameLayout implements NestedScrollingParent {
        private AnimatorSet currentAnimation = null;
        private boolean maybeStartTracking = false;
        private NestedScrollingParentHelper nestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        private boolean startedTracking = false;
        private int startedTrackingPointerId = -1;
        private int startedTrackingX;
        private int startedTrackingY;
        private VelocityTracker velocityTracker = null;

        /* renamed from: org.telegram.ui.ActionBar.BottomSheet$ContainerView$1 */
        class C07541 extends AnimatorListenerAdapter {
            C07541() {
            }

            public void onAnimationEnd(Animator animator) {
                if (ContainerView.this.currentAnimation != null && ContainerView.this.currentAnimation.equals(animator) != null) {
                    ContainerView.this.currentAnimation = null;
                }
            }
        }

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
        }

        public boolean onStartNestedScroll(View view, View view2, int i) {
            return ((BottomSheet.this.nestedScrollChild == null || view == BottomSheet.this.nestedScrollChild) && BottomSheet.this.dismissed == null && BottomSheet.this.allowNestedScroll != null && i == 2 && BottomSheet.this.canDismissWithSwipe() == null) ? true : null;
        }

        public void onNestedScrollAccepted(View view, View view2, int i) {
            this.nestedScrollingParentHelper.onNestedScrollAccepted(view, view2, i);
            if (BottomSheet.this.dismissed == null) {
                if (BottomSheet.this.allowNestedScroll != null) {
                    cancelCurrentAnimation();
                }
            }
        }

        public void onStopNestedScroll(View view) {
            this.nestedScrollingParentHelper.onStopNestedScroll(view);
            if (BottomSheet.this.dismissed == null) {
                if (BottomSheet.this.allowNestedScroll != null) {
                    BottomSheet.this.containerView.getTranslationY();
                    checkDismiss(0.0f, 0.0f);
                }
            }
        }

        public void onNestedScroll(View view, int i, int i2, int i3, int i4) {
            if (BottomSheet.this.dismissed == null) {
                if (BottomSheet.this.allowNestedScroll != null) {
                    cancelCurrentAnimation();
                    if (i4 != 0) {
                        view = BottomSheet.this.containerView.getTranslationY() - ((float) i4);
                        if (view < null) {
                            view = null;
                        }
                        BottomSheet.this.containerView.setTranslationY(view);
                    }
                }
            }
        }

        public void onNestedPreScroll(View view, int i, int i2, int[] iArr) {
            if (BottomSheet.this.dismissed == null) {
                if (BottomSheet.this.allowNestedScroll != null) {
                    cancelCurrentAnimation();
                    view = BottomSheet.this.containerView.getTranslationY();
                    if (view > null && i2 > 0) {
                        view -= (float) i2;
                        iArr[1] = i2;
                        if (view < null) {
                            view = null;
                        }
                        BottomSheet.this.containerView.setTranslationY(view);
                    }
                }
            }
        }

        public int getNestedScrollAxes() {
            return this.nestedScrollingParentHelper.getNestedScrollAxes();
        }

        private void checkDismiss(float f, float f2) {
            float translationY = BottomSheet.this.containerView.getTranslationY();
            f = ((translationY >= AndroidUtilities.getPixelsInCM(0.8f, false) || (f2 >= 3500.0f && Math.abs(f2) >= Math.abs(f))) && (f2 >= 0.0f || Math.abs(f2) < 3500.0f)) ? 0.0f : Float.MIN_VALUE;
            if (f == null) {
                f = BottomSheet.this.allowCustomAnimation;
                BottomSheet.this.allowCustomAnimation = false;
                BottomSheet.this.useFastDismiss = true;
                BottomSheet.this.dismiss();
                BottomSheet.this.allowCustomAnimation = f;
                return;
            }
            this.currentAnimation = new AnimatorSet();
            f = this.currentAnimation;
            f2 = new Animator[1];
            f2[0] = ObjectAnimator.ofFloat(BottomSheet.this.containerView, "translationY", new float[]{0.0f});
            f.playTogether(f2);
            this.currentAnimation.setDuration((long) ((int) (150.0f * (translationY / AndroidUtilities.getPixelsInCM(0.8f, false)))));
            this.currentAnimation.setInterpolator(new DecelerateInterpolator());
            this.currentAnimation.addListener(new C07541());
            this.currentAnimation.start();
        }

        private void cancelCurrentAnimation() {
            if (this.currentAnimation != null) {
                this.currentAnimation.cancel();
                this.currentAnimation = null;
            }
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            boolean z = false;
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
                    if (this.maybeStartTracking && !this.startedTracking && y > 0.0f && y / 3.0f > Math.abs(abs) && Math.abs(y) >= ((float) BottomSheet.this.touchSlop)) {
                        this.startedTrackingY = (int) motionEvent.getY();
                        this.maybeStartTracking = false;
                        this.startedTracking = true;
                        requestDisallowInterceptTouchEvent(true);
                    } else if (this.startedTracking) {
                        abs = BottomSheet.this.containerView.getTranslationY() + y;
                        if (abs >= 0.0f) {
                            f = abs;
                        }
                        BottomSheet.this.containerView.setTranslationY(f);
                        this.startedTrackingY = (int) motionEvent.getY();
                    }
                } else if (motionEvent == null || (motionEvent != null && motionEvent.getPointerId(0) == this.startedTrackingPointerId && (motionEvent.getAction() == 3 || motionEvent.getAction() == 1 || motionEvent.getAction() == 6))) {
                    if (this.velocityTracker == null) {
                        this.velocityTracker = VelocityTracker.obtain();
                    }
                    this.velocityTracker.computeCurrentVelocity(1000);
                    motionEvent = BottomSheet.this.containerView.getTranslationY();
                    if (!this.startedTracking) {
                        if (motionEvent == null) {
                            this.maybeStartTracking = false;
                            this.startedTracking = false;
                            if (this.velocityTracker != null) {
                                this.velocityTracker.recycle();
                                this.velocityTracker = null;
                            }
                            this.startedTrackingPointerId = -1;
                        }
                    }
                    checkDismiss(this.velocityTracker.getXVelocity(), this.velocityTracker.getYVelocity());
                    this.startedTracking = false;
                    if (this.velocityTracker != null) {
                        this.velocityTracker.recycle();
                        this.velocityTracker = null;
                    }
                    this.startedTrackingPointerId = -1;
                }
            } else {
                this.startedTrackingX = (int) motionEvent.getX();
                this.startedTrackingY = (int) motionEvent.getY();
                if (this.startedTrackingY >= BottomSheet.this.containerView.getTop() && this.startedTrackingX >= BottomSheet.this.containerView.getLeft()) {
                    if (this.startedTrackingX <= BottomSheet.this.containerView.getRight()) {
                        this.startedTrackingPointerId = motionEvent.getPointerId(0);
                        this.maybeStartTracking = true;
                        cancelCurrentAnimation();
                        if (this.velocityTracker != null) {
                            this.velocityTracker.clear();
                        }
                    }
                }
                BottomSheet.this.dismiss();
                return true;
            }
            if (this.startedTracking != null || BottomSheet.this.canDismissWithSwipe() == null) {
                z = true;
            }
            return z;
        }

        protected void onMeasure(int i, int i2) {
            i = MeasureSpec.getSize(i);
            i2 = MeasureSpec.getSize(i2);
            if (BottomSheet.this.lastInsets != null && VERSION.SDK_INT >= 21) {
                i2 -= BottomSheet.this.lastInsets.getSystemWindowInsetBottom();
            }
            setMeasuredDimension(i, i2);
            if (BottomSheet.this.lastInsets != null && VERSION.SDK_INT >= 21) {
                i -= BottomSheet.this.lastInsets.getSystemWindowInsetRight() + BottomSheet.this.lastInsets.getSystemWindowInsetLeft();
            }
            int i3 = 0;
            int i4 = i < i2 ? 1 : 0;
            if (BottomSheet.this.containerView != null) {
                if (BottomSheet.this.fullWidth) {
                    BottomSheet.this.containerView.measure(MeasureSpec.makeMeasureSpec((BottomSheet.backgroundPaddingLeft * 2) + i, NUM), MeasureSpec.makeMeasureSpec(i2, Integer.MIN_VALUE));
                } else {
                    if (AndroidUtilities.isTablet()) {
                        i4 = MeasureSpec.makeMeasureSpec(((int) (((float) Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) * 0.8f)) + (BottomSheet.backgroundPaddingLeft * 2), NUM);
                    } else {
                        i4 = MeasureSpec.makeMeasureSpec(i4 != 0 ? (BottomSheet.backgroundPaddingLeft * 2) + i : ((int) Math.max(((float) i) * 0.8f, (float) Math.min(AndroidUtilities.dp(480.0f), i))) + (BottomSheet.backgroundPaddingLeft * 2), NUM);
                    }
                    BottomSheet.this.containerView.measure(i4, MeasureSpec.makeMeasureSpec(i2, Integer.MIN_VALUE));
                }
            }
            i4 = getChildCount();
            while (i3 < i4) {
                View childAt = getChildAt(i3);
                if (childAt.getVisibility() != 8) {
                    if (childAt != BottomSheet.this.containerView) {
                        if (!BottomSheet.this.onCustomMeasure(childAt, i, i2)) {
                            measureChildWithMargins(childAt, MeasureSpec.makeMeasureSpec(i, NUM), 0, MeasureSpec.makeMeasureSpec(i2, NUM), 0);
                        }
                    }
                }
                i3++;
            }
        }

        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            int i5;
            int i6;
            int measuredWidth;
            int i7;
            int i8;
            BottomSheet.this.layoutCount = BottomSheet.this.layoutCount - 1;
            if (BottomSheet.this.containerView != null) {
                if (BottomSheet.this.lastInsets == null || VERSION.SDK_INT < 21) {
                    i5 = i;
                    i6 = i3;
                } else {
                    i5 = BottomSheet.this.lastInsets.getSystemWindowInsetLeft() + i;
                    i6 = i3 - BottomSheet.this.lastInsets.getSystemWindowInsetRight();
                }
                int measuredHeight = (i4 - i2) - BottomSheet.this.containerView.getMeasuredHeight();
                measuredWidth = ((i6 - i5) - BottomSheet.this.containerView.getMeasuredWidth()) / 2;
                if (BottomSheet.this.lastInsets != null && VERSION.SDK_INT >= 21) {
                    measuredWidth += BottomSheet.this.lastInsets.getSystemWindowInsetLeft();
                }
                BottomSheet.this.containerView.layout(measuredWidth, measuredHeight, BottomSheet.this.containerView.getMeasuredWidth() + measuredWidth, BottomSheet.this.containerView.getMeasuredHeight() + measuredHeight);
                i7 = i5;
                i8 = i6;
            } else {
                i7 = i;
                i8 = i3;
            }
            int childCount = getChildCount();
            for (int i9 = 0; i9 < childCount; i9++) {
                View childAt = getChildAt(i9);
                if (childAt.getVisibility() != 8) {
                    if (childAt != BottomSheet.this.containerView) {
                        if (!BottomSheet.this.onCustomLayout(childAt, i7, i2, i8, i4)) {
                            LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                            i6 = childAt.getMeasuredWidth();
                            measuredHeight = childAt.getMeasuredHeight();
                            measuredWidth = layoutParams.gravity;
                            if (measuredWidth == -1) {
                                measuredWidth = 51;
                            }
                            int i10 = measuredWidth & 7;
                            measuredWidth &= 112;
                            i10 &= 7;
                            if (i10 == 1) {
                                i10 = ((((i8 - i7) - i6) / 2) + layoutParams.leftMargin) - layoutParams.rightMargin;
                            } else if (i10 != 5) {
                                i10 = layoutParams.leftMargin;
                            } else {
                                i10 = (i8 - i6) - layoutParams.rightMargin;
                            }
                            i5 = measuredWidth != 16 ? measuredWidth != 48 ? measuredWidth != 80 ? layoutParams.topMargin : ((i4 - i2) - measuredHeight) - layoutParams.bottomMargin : layoutParams.topMargin : ((((i4 - i2) - measuredHeight) / 2) + layoutParams.topMargin) - layoutParams.bottomMargin;
                            if (BottomSheet.this.lastInsets != null && VERSION.SDK_INT >= 21) {
                                i10 += BottomSheet.this.lastInsets.getSystemWindowInsetLeft();
                            }
                            childAt.layout(i10, i5, i6 + i10, measuredHeight + i5);
                        }
                    }
                }
            }
            if (BottomSheet.this.layoutCount == 0 && BottomSheet.this.startAnimationRunnable != null) {
                AndroidUtilities.cancelRunOnUIThread(BottomSheet.this.startAnimationRunnable);
                BottomSheet.this.startAnimationRunnable.run();
                BottomSheet.this.startAnimationRunnable = null;
            }
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            if (BottomSheet.this.canDismissWithSwipe()) {
                return onTouchEvent(motionEvent);
            }
            return super.onInterceptTouchEvent(motionEvent);
        }

        public void requestDisallowInterceptTouchEvent(boolean z) {
            if (this.maybeStartTracking && !this.startedTracking) {
                onTouchEvent(null);
            }
            super.requestDisallowInterceptTouchEvent(z);
        }

        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            BottomSheet.this.onContainerDraw(canvas);
        }
    }

    protected boolean canDismissWithSwipe() {
        return true;
    }

    protected boolean canDismissWithTouchOutside() {
        return true;
    }

    public void onConfigurationChanged(Configuration configuration) {
    }

    public void onContainerDraw(Canvas canvas) {
    }

    protected boolean onContainerTouchEvent(MotionEvent motionEvent) {
        return false;
    }

    protected void onContainerTranslationYChanged(float f) {
    }

    protected boolean onCustomCloseAnimation() {
        return false;
    }

    protected boolean onCustomLayout(View view, int i, int i2, int i3, int i4) {
        return false;
    }

    protected boolean onCustomMeasure(View view, int i, int i2) {
        return false;
    }

    protected boolean onCustomOpenAnimation() {
        return false;
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    public void setAllowNestedScroll(boolean z) {
        this.allowNestedScroll = z;
        if (!this.allowNestedScroll) {
            this.containerView.setTranslationY(0.0f);
        }
    }

    public BottomSheet(Context context, boolean z) {
        super(context, C0446R.style.TransparentDialog);
        if (VERSION.SDK_INT >= 21) {
            getWindow().addFlags(-NUM);
        }
        this.touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        Rect rect = new Rect();
        this.shadowDrawable = context.getResources().getDrawable(C0446R.drawable.sheet_shadow).mutate();
        this.shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogBackground), Mode.MULTIPLY));
        this.shadowDrawable.getPadding(rect);
        backgroundPaddingLeft = rect.left;
        backgroundPaddingTop = rect.top;
        this.container = new ContainerView(getContext()) {
            public boolean drawChild(Canvas canvas, View view, long j) {
                boolean z = true;
                try {
                    if (!BottomSheet.this.allowDrawContent || super.drawChild(canvas, view, j) == null) {
                        z = false;
                    }
                    return z;
                } catch (Throwable e) {
                    FileLog.m3e(e);
                    return true;
                }
            }
        };
        this.container.setBackgroundDrawable(this.backDrawable);
        this.focusable = z;
        if (VERSION.SDK_INT >= 21) {
            this.container.setFitsSystemWindows(true);
            this.container.setOnApplyWindowInsetsListener(new C07442());
            this.container.setSystemUiVisibility(true);
        }
        this.backDrawable.setAlpha(false);
    }

    protected void onCreate(Bundle bundle) {
        int i;
        super.onCreate(bundle);
        bundle = getWindow();
        bundle.setWindowAnimations(C0446R.style.DialogNoAnimation);
        setContentView(this.container, new ViewGroup.LayoutParams(-1, -1));
        if (this.containerView == null) {
            this.containerView = new FrameLayout(getContext()) {
                public boolean hasOverlappingRendering() {
                    return false;
                }

                public void setTranslationY(float f) {
                    super.setTranslationY(f);
                    BottomSheet.this.onContainerTranslationYChanged(f);
                }
            };
            this.containerView.setBackgroundDrawable(this.shadowDrawable);
            this.containerView.setPadding(backgroundPaddingLeft, ((this.applyTopPadding ? AndroidUtilities.dp(8.0f) : 0) + backgroundPaddingTop) - 1, backgroundPaddingLeft, this.applyBottomPadding ? AndroidUtilities.dp(8.0f) : 0);
        }
        if (VERSION.SDK_INT >= 21) {
            this.containerView.setFitsSystemWindows(true);
        }
        this.containerView.setVisibility(4);
        this.container.addView(this.containerView, 0, LayoutHelper.createFrame(-1, -2, 80));
        if (this.title != null) {
            View textView = new TextView(getContext());
            textView.setLines(1);
            textView.setSingleLine(true);
            textView.setText(this.title);
            textView.setTextColor(Theme.getColor(Theme.key_dialogTextGray2));
            textView.setTextSize(1, 16.0f);
            textView.setEllipsize(TruncateAt.MIDDLE);
            textView.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), AndroidUtilities.dp(8.0f));
            textView.setGravity(16);
            this.containerView.addView(textView, LayoutHelper.createFrame(-1, 48.0f));
            textView.setOnTouchListener(new C07464());
            i = 48;
        } else {
            i = 0;
        }
        if (this.customView != null) {
            if (this.customView.getParent() != null) {
                ((ViewGroup) this.customView.getParent()).removeView(this.customView);
            }
            this.containerView.addView(this.customView, LayoutHelper.createFrame(-1, -2.0f, 51, 0.0f, (float) i, 0.0f, 0.0f));
        } else if (this.items != null) {
            int i2 = i;
            i = 0;
            while (i < this.items.length) {
                if (this.items[i] != null) {
                    View bottomSheetCell = new BottomSheetCell(getContext(), 0);
                    bottomSheetCell.setTextAndIcon(this.items[i], this.itemIcons != null ? this.itemIcons[i] : 0);
                    this.containerView.addView(bottomSheetCell, LayoutHelper.createFrame(-1, 48.0f, 51, 0.0f, (float) i2, 0.0f, 0.0f));
                    i2 += 48;
                    bottomSheetCell.setTag(Integer.valueOf(i));
                    bottomSheetCell.setOnClickListener(new C07475());
                    this.itemViews.add(bottomSheetCell);
                }
                i++;
            }
        }
        WindowManager.LayoutParams attributes = bundle.getAttributes();
        attributes.width = -1;
        attributes.gravity = 51;
        attributes.dimAmount = 0.0f;
        attributes.flags &= -3;
        if (!this.focusable) {
            attributes.flags |= 131072;
        }
        attributes.height = -1;
        bundle.setAttributes(attributes);
    }

    public void setShowWithoutAnimation(boolean z) {
        this.showWithoutAnimation = z;
    }

    public void setBackgroundColor(int i) {
        this.shadowDrawable.setColorFilter(i, Mode.MULTIPLY);
    }

    public void show() {
        super.show();
        if (this.focusable) {
            getWindow().setSoftInputMode(16);
        }
        this.dismissed = false;
        cancelSheetAnimation();
        this.containerView.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.x + (backgroundPaddingLeft * 2), Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.y, Integer.MIN_VALUE));
        if (this.showWithoutAnimation) {
            this.backDrawable.setAlpha(51);
            this.containerView.setTranslationY(0.0f);
            return;
        }
        this.backDrawable.setAlpha(0);
        if (VERSION.SDK_INT >= 18) {
            this.layoutCount = 2;
            this.containerView.setTranslationY((float) this.containerView.getMeasuredHeight());
            Runnable c07486 = new C07486();
            this.startAnimationRunnable = c07486;
            AndroidUtilities.runOnUIThread(c07486, 150);
        } else {
            startOpenAnimation();
        }
    }

    public void setAllowDrawContent(boolean z) {
        if (this.allowDrawContent != z) {
            this.allowDrawContent = z;
            this.container.setBackgroundDrawable(this.allowDrawContent ? this.backDrawable : null);
            this.container.invalidate();
        }
    }

    public void setCustomView(View view) {
        this.customView = view;
    }

    public void setTitle(CharSequence charSequence) {
        this.title = charSequence;
    }

    public void setApplyTopPadding(boolean z) {
        this.applyTopPadding = z;
    }

    public void setApplyBottomPadding(boolean z) {
        this.applyBottomPadding = z;
    }

    private void cancelSheetAnimation() {
        if (this.currentSheetAnimation != null) {
            this.currentSheetAnimation.cancel();
            this.currentSheetAnimation = null;
        }
    }

    private void startOpenAnimation() {
        if (!this.dismissed) {
            this.containerView.setVisibility(0);
            if (!onCustomOpenAnimation()) {
                if (VERSION.SDK_INT >= 20 && this.useHardwareLayer) {
                    this.container.setLayerType(2, null);
                }
                this.containerView.setTranslationY((float) this.containerView.getMeasuredHeight());
                AnimatorSet animatorSet = new AnimatorSet();
                r2 = new Animator[2];
                r2[0] = ObjectAnimator.ofFloat(this.containerView, "translationY", new float[]{0.0f});
                r2[1] = ObjectAnimator.ofInt(this.backDrawable, "alpha", new int[]{51});
                animatorSet.playTogether(r2);
                animatorSet.setDuration(200);
                animatorSet.setStartDelay(20);
                animatorSet.setInterpolator(new DecelerateInterpolator());
                animatorSet.addListener(new C07497());
                animatorSet.start();
                this.currentSheetAnimation = animatorSet;
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

    public int getTag() {
        return this.tag;
    }

    public void setItemText(int i, CharSequence charSequence) {
        if (i >= 0) {
            if (i < this.itemViews.size()) {
                ((BottomSheetCell) this.itemViews.get(i)).textView.setText(charSequence);
            }
        }
    }

    public boolean isDismissed() {
        return this.dismissed;
    }

    public void dismissWithButtonClick(final int i) {
        if (!this.dismissed) {
            this.dismissed = true;
            cancelSheetAnimation();
            AnimatorSet animatorSet = new AnimatorSet();
            r2 = new Animator[2];
            r2[0] = ObjectAnimator.ofFloat(this.containerView, "translationY", new float[]{(float) (this.containerView.getMeasuredHeight() + AndroidUtilities.dp(10.0f))});
            r2[1] = ObjectAnimator.ofInt(this.backDrawable, "alpha", new int[]{0});
            animatorSet.playTogether(r2);
            animatorSet.setDuration(180);
            animatorSet.setInterpolator(new AccelerateInterpolator());
            animatorSet.addListener(new AnimatorListenerAdapter() {

                /* renamed from: org.telegram.ui.ActionBar.BottomSheet$8$1 */
                class C07501 implements Runnable {
                    C07501() {
                    }

                    public void run() {
                        try {
                            super.dismiss();
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                    }
                }

                public void onAnimationEnd(Animator animator) {
                    if (BottomSheet.this.currentSheetAnimation != null && BottomSheet.this.currentSheetAnimation.equals(animator) != null) {
                        BottomSheet.this.currentSheetAnimation = null;
                        if (BottomSheet.this.onClickListener != null) {
                            BottomSheet.this.onClickListener.onClick(BottomSheet.this, i);
                        }
                        AndroidUtilities.runOnUIThread(new C07501());
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    if (BottomSheet.this.currentSheetAnimation != null && BottomSheet.this.currentSheetAnimation.equals(animator) != null) {
                        BottomSheet.this.currentSheetAnimation = null;
                    }
                }
            });
            animatorSet.start();
            this.currentSheetAnimation = animatorSet;
        }
    }

    public void dismiss() {
        if ((this.delegate == null || this.delegate.canDismiss()) && !this.dismissed) {
            this.dismissed = true;
            cancelSheetAnimation();
            if (!(this.allowCustomAnimation && onCustomCloseAnimation())) {
                AnimatorSet animatorSet = new AnimatorSet();
                r2 = new Animator[2];
                r2[0] = ObjectAnimator.ofFloat(this.containerView, "translationY", new float[]{(float) (this.containerView.getMeasuredHeight() + AndroidUtilities.dp(10.0f))});
                r2[1] = ObjectAnimator.ofInt(this.backDrawable, "alpha", new int[]{0});
                animatorSet.playTogether(r2);
                if (this.useFastDismiss) {
                    float measuredHeight = (float) this.containerView.getMeasuredHeight();
                    animatorSet.setDuration((long) Math.max(60, (int) ((180.0f * (measuredHeight - this.containerView.getTranslationY())) / measuredHeight)));
                    this.useFastDismiss = false;
                } else {
                    animatorSet.setDuration(180);
                }
                animatorSet.setInterpolator(new AccelerateInterpolator());
                animatorSet.addListener(new C07539());
                animatorSet.start();
                this.currentSheetAnimation = animatorSet;
            }
        }
    }

    public void dismissInternal() {
        try {
            super.dismiss();
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
    }

    protected int getLeftInset() {
        return (this.lastInsets == null || VERSION.SDK_INT < 21) ? 0 : this.lastInsets.getSystemWindowInsetLeft();
    }
}
