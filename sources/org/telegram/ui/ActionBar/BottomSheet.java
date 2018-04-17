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
import android.view.Window;
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
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
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
    class C07372 implements OnApplyWindowInsetsListener {
        C07372() {
        }

        @SuppressLint({"NewApi"})
        public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
            BottomSheet.this.lastInsets = insets;
            v.requestLayout();
            return insets.consumeSystemWindowInsets();
        }
    }

    /* renamed from: org.telegram.ui.ActionBar.BottomSheet$4 */
    class C07394 implements OnTouchListener {
        C07394() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
    }

    /* renamed from: org.telegram.ui.ActionBar.BottomSheet$5 */
    class C07405 implements View.OnClickListener {
        C07405() {
        }

        public void onClick(View v) {
            BottomSheet.this.dismissWithButtonClick(((Integer) v.getTag()).intValue());
        }
    }

    /* renamed from: org.telegram.ui.ActionBar.BottomSheet$6 */
    class C07416 implements Runnable {
        C07416() {
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
    class C07427 extends AnimatorListenerAdapter {
        C07427() {
        }

        public void onAnimationEnd(Animator animation) {
            if (BottomSheet.this.currentSheetAnimation != null && BottomSheet.this.currentSheetAnimation.equals(animation)) {
                BottomSheet.this.currentSheetAnimation = null;
                if (BottomSheet.this.delegate != null) {
                    BottomSheet.this.delegate.onOpenAnimationEnd();
                }
                if (BottomSheet.this.useHardwareLayer) {
                    BottomSheet.this.container.setLayerType(0, null);
                }
            }
        }

        public void onAnimationCancel(Animator animation) {
            if (BottomSheet.this.currentSheetAnimation != null && BottomSheet.this.currentSheetAnimation.equals(animation)) {
                BottomSheet.this.currentSheetAnimation = null;
            }
        }
    }

    /* renamed from: org.telegram.ui.ActionBar.BottomSheet$9 */
    class C07469 extends AnimatorListenerAdapter {

        /* renamed from: org.telegram.ui.ActionBar.BottomSheet$9$1 */
        class C07451 implements Runnable {
            C07451() {
            }

            public void run() {
                try {
                    BottomSheet.this.dismissInternal();
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        }

        C07469() {
        }

        public void onAnimationEnd(Animator animation) {
            if (BottomSheet.this.currentSheetAnimation != null && BottomSheet.this.currentSheetAnimation.equals(animation)) {
                BottomSheet.this.currentSheetAnimation = null;
                AndroidUtilities.runOnUIThread(new C07451());
            }
        }

        public void onAnimationCancel(Animator animation) {
            if (BottomSheet.this.currentSheetAnimation != null && BottomSheet.this.currentSheetAnimation.equals(animation)) {
                BottomSheet.this.currentSheetAnimation = null;
            }
        }
    }

    public static class BottomSheetCell extends FrameLayout {
        private ImageView imageView;
        private TextView textView;

        public BottomSheetCell(Context context, int type) {
            super(context);
            setBackgroundDrawable(Theme.getSelectorDrawable(false));
            setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
            this.imageView = new ImageView(context);
            this.imageView.setScaleType(ScaleType.CENTER);
            this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogIcon), Mode.MULTIPLY));
            int i = 3;
            addView(this.imageView, LayoutHelper.createFrame(24, 24, (LocaleController.isRTL ? 5 : 3) | 16));
            this.textView = new TextView(context);
            this.textView.setLines(1);
            this.textView.setSingleLine(true);
            this.textView.setGravity(1);
            this.textView.setEllipsize(TruncateAt.END);
            if (type == 0) {
                this.textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                this.textView.setTextSize(1, 16.0f);
                View view = this.textView;
                if (LocaleController.isRTL) {
                    i = 5;
                }
                addView(view, LayoutHelper.createFrame(-2, -2, i | 16));
            } else if (type == 1) {
                this.textView.setGravity(17);
                this.textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                this.textView.setTextSize(1, 14.0f);
                this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                addView(this.textView, LayoutHelper.createFrame(-1, -1.0f));
            }
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM));
        }

        public void setTextColor(int color) {
            this.textView.setTextColor(color);
        }

        public void setGravity(int gravity) {
            this.textView.setGravity(gravity);
        }

        public void setTextAndIcon(CharSequence text, int icon) {
            this.textView.setText(text);
            if (icon != 0) {
                this.imageView.setImageResource(icon);
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

        public Builder(Context context, boolean needFocus) {
            this.bottomSheet = new BottomSheet(context, needFocus);
        }

        public Builder setItems(CharSequence[] items, OnClickListener onClickListener) {
            this.bottomSheet.items = items;
            this.bottomSheet.onClickListener = onClickListener;
            return this;
        }

        public Builder setItems(CharSequence[] items, int[] icons, OnClickListener onClickListener) {
            this.bottomSheet.items = items;
            this.bottomSheet.itemIcons = icons;
            this.bottomSheet.onClickListener = onClickListener;
            return this;
        }

        public Builder setCustomView(View view) {
            this.bottomSheet.customView = view;
            return this;
        }

        public Builder setTitle(CharSequence title) {
            this.bottomSheet.title = title;
            return this;
        }

        public BottomSheet create() {
            return this.bottomSheet;
        }

        public BottomSheet show() {
            this.bottomSheet.show();
            return this.bottomSheet;
        }

        public Builder setTag(int tag) {
            this.bottomSheet.tag = tag;
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
            this.bottomSheet.applyTopPadding = value;
            return this;
        }

        public Builder setApplyBottomPadding(boolean value) {
            this.bottomSheet.applyBottomPadding = value;
            return this;
        }

        public BottomSheet setUseFullWidth(boolean value) {
            this.bottomSheet.fullWidth = value;
            return this.bottomSheet;
        }
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
        class C07471 extends AnimatorListenerAdapter {
            C07471() {
            }

            public void onAnimationEnd(Animator animation) {
                if (ContainerView.this.currentAnimation != null && ContainerView.this.currentAnimation.equals(animation)) {
                    ContainerView.this.currentAnimation = null;
                }
            }
        }

        public ContainerView(Context context) {
            super(context);
        }

        public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
            return (BottomSheet.this.nestedScrollChild == null || child == BottomSheet.this.nestedScrollChild) && !BottomSheet.this.dismissed && BottomSheet.this.allowNestedScroll && nestedScrollAxes == 2 && !BottomSheet.this.canDismissWithSwipe();
        }

        public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {
            this.nestedScrollingParentHelper.onNestedScrollAccepted(child, target, nestedScrollAxes);
            if (!BottomSheet.this.dismissed) {
                if (BottomSheet.this.allowNestedScroll) {
                    cancelCurrentAnimation();
                }
            }
        }

        public void onStopNestedScroll(View target) {
            this.nestedScrollingParentHelper.onStopNestedScroll(target);
            if (!BottomSheet.this.dismissed) {
                if (BottomSheet.this.allowNestedScroll) {
                    float currentTranslation = BottomSheet.this.containerView.getTranslationY();
                    checkDismiss(0.0f, 0.0f);
                }
            }
        }

        public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
            if (!BottomSheet.this.dismissed) {
                if (BottomSheet.this.allowNestedScroll) {
                    cancelCurrentAnimation();
                    if (dyUnconsumed != 0) {
                        float currentTranslation = BottomSheet.this.containerView.getTranslationY() - ((float) dyUnconsumed);
                        if (currentTranslation < 0.0f) {
                            currentTranslation = 0.0f;
                        }
                        BottomSheet.this.containerView.setTranslationY(currentTranslation);
                    }
                }
            }
        }

        public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
            if (!BottomSheet.this.dismissed) {
                if (BottomSheet.this.allowNestedScroll) {
                    cancelCurrentAnimation();
                    float currentTranslation = BottomSheet.this.containerView.getTranslationY();
                    if (currentTranslation > 0.0f && dy > 0) {
                        currentTranslation -= (float) dy;
                        consumed[1] = dy;
                        if (currentTranslation < 0.0f) {
                            currentTranslation = 0.0f;
                        }
                        BottomSheet.this.containerView.setTranslationY(currentTranslation);
                    }
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
            boolean backAnimation = (translationY < AndroidUtilities.getPixelsInCM(0.8f, false) && (velY < 3500.0f || Math.abs(velY) < Math.abs(velX))) || (velY < 0.0f && Math.abs(velY) >= 3500.0f);
            if (backAnimation) {
                this.currentAnimation = new AnimatorSet();
                AnimatorSet animatorSet = this.currentAnimation;
                Animator[] animatorArr = new Animator[1];
                animatorArr[0] = ObjectAnimator.ofFloat(BottomSheet.this.containerView, "translationY", new float[]{0.0f});
                animatorSet.playTogether(animatorArr);
                this.currentAnimation.setDuration((long) ((int) (150.0f * (translationY / AndroidUtilities.getPixelsInCM(0.8f, false)))));
                this.currentAnimation.setInterpolator(new DecelerateInterpolator());
                this.currentAnimation.addListener(new C07471());
                this.currentAnimation.start();
                return;
            }
            boolean allowOld = BottomSheet.this.allowCustomAnimation;
            BottomSheet.this.allowCustomAnimation = false;
            BottomSheet.this.useFastDismiss = true;
            BottomSheet.this.dismiss();
            BottomSheet.this.allowCustomAnimation = allowOld;
        }

        private void cancelCurrentAnimation() {
            if (this.currentAnimation != null) {
                this.currentAnimation.cancel();
                this.currentAnimation = null;
            }
        }

        public boolean onTouchEvent(MotionEvent ev) {
            boolean z = false;
            if (BottomSheet.this.dismissed) {
                return false;
            }
            if (BottomSheet.this.onContainerTouchEvent(ev)) {
                return true;
            }
            if (BottomSheet.this.canDismissWithTouchOutside() && ev != null && ((ev.getAction() == 0 || ev.getAction() == 2) && !this.startedTracking && !this.maybeStartTracking && ev.getPointerCount() == 1)) {
                this.startedTrackingX = (int) ev.getX();
                this.startedTrackingY = (int) ev.getY();
                if (this.startedTrackingY >= BottomSheet.this.containerView.getTop() && this.startedTrackingX >= BottomSheet.this.containerView.getLeft()) {
                    if (this.startedTrackingX <= BottomSheet.this.containerView.getRight()) {
                        this.startedTrackingPointerId = ev.getPointerId(0);
                        this.maybeStartTracking = true;
                        cancelCurrentAnimation();
                        if (this.velocityTracker != null) {
                            this.velocityTracker.clear();
                        }
                    }
                }
                BottomSheet.this.dismiss();
                return true;
            } else if (ev != null && ev.getAction() == 2 && ev.getPointerId(0) == this.startedTrackingPointerId) {
                if (this.velocityTracker == null) {
                    this.velocityTracker = VelocityTracker.obtain();
                }
                dx = (float) Math.abs((int) (ev.getX() - ((float) this.startedTrackingX)));
                float dy = (float) (((int) ev.getY()) - this.startedTrackingY);
                this.velocityTracker.addMovement(ev);
                if (this.maybeStartTracking && !this.startedTracking && dy > 0.0f && dy / 3.0f > Math.abs(dx) && Math.abs(dy) >= ((float) BottomSheet.this.touchSlop)) {
                    this.startedTrackingY = (int) ev.getY();
                    this.maybeStartTracking = false;
                    this.startedTracking = true;
                    requestDisallowInterceptTouchEvent(true);
                } else if (this.startedTracking) {
                    float translationY = BottomSheet.this.containerView.getTranslationY() + dy;
                    if (translationY < 0.0f) {
                        translationY = 0.0f;
                    }
                    BottomSheet.this.containerView.setTranslationY(translationY);
                    this.startedTrackingY = (int) ev.getY();
                }
            } else if (ev == null || (ev != null && ev.getPointerId(0) == this.startedTrackingPointerId && (ev.getAction() == 3 || ev.getAction() == 1 || ev.getAction() == 6))) {
                if (this.velocityTracker == null) {
                    this.velocityTracker = VelocityTracker.obtain();
                }
                this.velocityTracker.computeCurrentVelocity(1000);
                dx = BottomSheet.this.containerView.getTranslationY();
                if (!this.startedTracking) {
                    if (dx == 0.0f) {
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
            if (!this.startedTracking) {
                if (BottomSheet.this.canDismissWithSwipe()) {
                    return z;
                }
            }
            z = true;
            return z;
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int widthSpec;
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = MeasureSpec.getSize(heightMeasureSpec);
            if (BottomSheet.this.lastInsets != null && VERSION.SDK_INT >= 21) {
                height -= BottomSheet.this.lastInsets.getSystemWindowInsetBottom();
            }
            setMeasuredDimension(width, height);
            if (BottomSheet.this.lastInsets != null && VERSION.SDK_INT >= 21) {
                width -= BottomSheet.this.lastInsets.getSystemWindowInsetRight() + BottomSheet.this.lastInsets.getSystemWindowInsetLeft();
            }
            int i = 0;
            boolean isPortrait = width < height;
            if (BottomSheet.this.containerView != null) {
                if (BottomSheet.this.fullWidth) {
                    BottomSheet.this.containerView.measure(MeasureSpec.makeMeasureSpec((BottomSheet.backgroundPaddingLeft * 2) + width, NUM), MeasureSpec.makeMeasureSpec(height, Integer.MIN_VALUE));
                } else {
                    if (AndroidUtilities.isTablet()) {
                        widthSpec = MeasureSpec.makeMeasureSpec(((int) (((float) Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) * 0.8f)) + (BottomSheet.backgroundPaddingLeft * 2), NUM);
                    } else {
                        widthSpec = MeasureSpec.makeMeasureSpec(isPortrait ? (BottomSheet.backgroundPaddingLeft * 2) + width : ((int) Math.max(((float) width) * 0.8f, (float) Math.min(AndroidUtilities.dp(480.0f), width))) + (BottomSheet.backgroundPaddingLeft * 2), NUM);
                    }
                    BottomSheet.this.containerView.measure(widthSpec, MeasureSpec.makeMeasureSpec(height, Integer.MIN_VALUE));
                }
            }
            widthSpec = getChildCount();
            while (i < widthSpec) {
                View child = getChildAt(i);
                if (child.getVisibility() != 8) {
                    if (child != BottomSheet.this.containerView) {
                        if (!BottomSheet.this.onCustomMeasure(child, width, height)) {
                            measureChildWithMargins(child, MeasureSpec.makeMeasureSpec(width, NUM), 0, MeasureSpec.makeMeasureSpec(height, NUM), 0);
                        }
                    }
                }
                i++;
            }
        }

        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            int left2;
            int right2;
            int l;
            int left3;
            int right3;
            BottomSheet.this.layoutCount = BottomSheet.this.layoutCount - 1;
            if (BottomSheet.this.containerView != null) {
                if (BottomSheet.this.lastInsets == null || VERSION.SDK_INT < 21) {
                    left2 = left;
                    right2 = right;
                } else {
                    left2 = left + BottomSheet.this.lastInsets.getSystemWindowInsetLeft();
                    right2 = right - BottomSheet.this.lastInsets.getSystemWindowInsetRight();
                }
                int t = (bottom - top) - BottomSheet.this.containerView.getMeasuredHeight();
                l = ((right2 - left2) - BottomSheet.this.containerView.getMeasuredWidth()) / 2;
                if (BottomSheet.this.lastInsets != null && VERSION.SDK_INT >= 21) {
                    l += BottomSheet.this.lastInsets.getSystemWindowInsetLeft();
                }
                BottomSheet.this.containerView.layout(l, t, BottomSheet.this.containerView.getMeasuredWidth() + l, BottomSheet.this.containerView.getMeasuredHeight() + t);
                left3 = left2;
                right3 = right2;
            } else {
                left3 = left;
                right3 = right;
            }
            int count = getChildCount();
            left2 = 0;
            while (true) {
                int i = left2;
                if (i >= count) {
                    break;
                }
                View child = getChildAt(i);
                if (child.getVisibility() != 8) {
                    if (child != BottomSheet.this.containerView) {
                        if (!BottomSheet.this.onCustomLayout(child, left3, top, right3, bottom)) {
                            int childLeft;
                            LayoutParams lp = (LayoutParams) child.getLayoutParams();
                            right2 = child.getMeasuredWidth();
                            t = child.getMeasuredHeight();
                            l = lp.gravity;
                            if (l == -1) {
                                l = 51;
                            }
                            int verticalGravity = l & 112;
                            int i2 = (l & 7) & 7;
                            if (i2 == 1) {
                                childLeft = ((((right3 - left3) - right2) / 2) + lp.leftMargin) - lp.rightMargin;
                            } else if (i2 != 5) {
                                childLeft = lp.leftMargin;
                            } else {
                                childLeft = (right3 - right2) - lp.rightMargin;
                            }
                            if (verticalGravity == 16) {
                                i2 = ((((bottom - top) - t) / 2) + lp.topMargin) - lp.bottomMargin;
                            } else if (verticalGravity == 48) {
                                i2 = lp.topMargin;
                            } else if (verticalGravity != 80) {
                                i2 = lp.topMargin;
                                int i3 = l;
                            } else {
                                i2 = ((bottom - top) - t) - lp.bottomMargin;
                            }
                            l = i2;
                            if (BottomSheet.this.lastInsets != null) {
                                if (VERSION.SDK_INT >= 21) {
                                    childLeft += BottomSheet.this.lastInsets.getSystemWindowInsetLeft();
                                }
                            }
                            child.layout(childLeft, l, childLeft + right2, l + t);
                        }
                    }
                }
                left2 = i + 1;
            }
            if (BottomSheet.this.layoutCount == 0 && BottomSheet.this.startAnimationRunnable != null) {
                AndroidUtilities.cancelRunOnUIThread(BottomSheet.this.startAnimationRunnable);
                BottomSheet.this.startAnimationRunnable.run();
                BottomSheet.this.startAnimationRunnable = null;
            }
        }

        public boolean onInterceptTouchEvent(MotionEvent event) {
            if (BottomSheet.this.canDismissWithSwipe()) {
                return onTouchEvent(event);
            }
            return super.onInterceptTouchEvent(event);
        }

        public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            if (this.maybeStartTracking && !this.startedTracking) {
                onTouchEvent(null);
            }
            super.requestDisallowInterceptTouchEvent(disallowIntercept);
        }

        public boolean hasOverlappingRendering() {
            return false;
        }

        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            BottomSheet.this.onContainerDraw(canvas);
        }
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    public void setAllowNestedScroll(boolean value) {
        this.allowNestedScroll = value;
        if (!this.allowNestedScroll) {
            this.containerView.setTranslationY(0.0f);
        }
    }

    public BottomSheet(Context context, boolean needFocus) {
        super(context, R.style.TransparentDialog);
        if (VERSION.SDK_INT >= 21) {
            getWindow().addFlags(-NUM);
        }
        this.touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        Rect padding = new Rect();
        this.shadowDrawable = context.getResources().getDrawable(R.drawable.sheet_shadow).mutate();
        this.shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogBackground), Mode.MULTIPLY));
        this.shadowDrawable.getPadding(padding);
        backgroundPaddingLeft = padding.left;
        backgroundPaddingTop = padding.top;
        this.container = new ContainerView(getContext()) {
            public boolean drawChild(Canvas canvas, View child, long drawingTime) {
                boolean z = true;
                try {
                    if (!BottomSheet.this.allowDrawContent || !super.drawChild(canvas, child, drawingTime)) {
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
        this.focusable = needFocus;
        if (VERSION.SDK_INT >= 21) {
            this.container.setFitsSystemWindows(true);
            this.container.setOnApplyWindowInsetsListener(new C07372());
            this.container.setSystemUiVisibility(1280);
        }
        this.backDrawable.setAlpha(0);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setWindowAnimations(R.style.DialogNoAnimation);
        setContentView(this.container, new ViewGroup.LayoutParams(-1, -1));
        if (this.containerView == null) {
            r0.containerView = new FrameLayout(getContext()) {
                public boolean hasOverlappingRendering() {
                    return false;
                }

                public void setTranslationY(float translationY) {
                    super.setTranslationY(translationY);
                    BottomSheet.this.onContainerTranslationYChanged(translationY);
                }
            };
            r0.containerView.setBackgroundDrawable(r0.shadowDrawable);
            r0.containerView.setPadding(backgroundPaddingLeft, ((r0.applyTopPadding ? AndroidUtilities.dp(8.0f) : 0) + backgroundPaddingTop) - 1, backgroundPaddingLeft, r0.applyBottomPadding ? AndroidUtilities.dp(8.0f) : 0);
        }
        if (VERSION.SDK_INT >= 21) {
            r0.containerView.setFitsSystemWindows(true);
        }
        r0.containerView.setVisibility(4);
        r0.container.addView(r0.containerView, 0, LayoutHelper.createFrame(-1, -2, 80));
        int topOffset = 0;
        if (r0.title != null) {
            TextView titleView = new TextView(getContext());
            titleView.setLines(1);
            titleView.setSingleLine(true);
            titleView.setText(r0.title);
            titleView.setTextColor(Theme.getColor(Theme.key_dialogTextGray2));
            titleView.setTextSize(1, 16.0f);
            titleView.setEllipsize(TruncateAt.MIDDLE);
            titleView.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), AndroidUtilities.dp(8.0f));
            titleView.setGravity(16);
            r0.containerView.addView(titleView, LayoutHelper.createFrame(-1, 48.0f));
            titleView.setOnTouchListener(new C07394());
            topOffset = 0 + 48;
        }
        if (r0.customView != null) {
            if (r0.customView.getParent() != null) {
                ((ViewGroup) r0.customView.getParent()).removeView(r0.customView);
            }
            r0.containerView.addView(r0.customView, LayoutHelper.createFrame(-1, -2.0f, 51, 0.0f, (float) topOffset, 0.0f, 0.0f));
        } else if (r0.items != null) {
            int topOffset2 = topOffset;
            topOffset = 0;
            while (topOffset < r0.items.length) {
                if (r0.items[topOffset] != null) {
                    BottomSheetCell cell = new BottomSheetCell(getContext(), 0);
                    cell.setTextAndIcon(r0.items[topOffset], r0.itemIcons != null ? r0.itemIcons[topOffset] : 0);
                    r0.containerView.addView(cell, LayoutHelper.createFrame(-1, 48.0f, 51, 0.0f, (float) topOffset2, 0.0f, 0.0f));
                    topOffset2 += 48;
                    cell.setTag(Integer.valueOf(topOffset));
                    cell.setOnClickListener(new C07405());
                    r0.itemViews.add(cell);
                }
                topOffset++;
            }
        }
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = -1;
        params.gravity = 51;
        params.dimAmount = 0.0f;
        params.flags &= -3;
        if (!r0.focusable) {
            params.flags |= 131072;
        }
        params.height = -1;
        window.setAttributes(params);
    }

    public void setShowWithoutAnimation(boolean value) {
        this.showWithoutAnimation = value;
    }

    public void setBackgroundColor(int color) {
        this.shadowDrawable.setColorFilter(color, Mode.MULTIPLY);
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
            Runnable c07416 = new C07416();
            this.startAnimationRunnable = c07416;
            AndroidUtilities.runOnUIThread(c07416, 150);
        } else {
            startOpenAnimation();
        }
    }

    public void setAllowDrawContent(boolean value) {
        if (this.allowDrawContent != value) {
            this.allowDrawContent = value;
            this.container.setBackgroundDrawable(this.allowDrawContent ? this.backDrawable : null);
            this.container.invalidate();
        }
    }

    protected boolean canDismissWithSwipe() {
        return true;
    }

    protected boolean onContainerTouchEvent(MotionEvent event) {
        return false;
    }

    public void setCustomView(View view) {
        this.customView = view;
    }

    public void setTitle(CharSequence value) {
        this.title = value;
    }

    public void setApplyTopPadding(boolean value) {
        this.applyTopPadding = value;
    }

    public void setApplyBottomPadding(boolean value) {
        this.applyBottomPadding = value;
    }

    protected boolean onCustomMeasure(View view, int width, int height) {
        return false;
    }

    protected boolean onCustomLayout(View view, int left, int top, int right, int bottom) {
        return false;
    }

    protected boolean canDismissWithTouchOutside() {
        return true;
    }

    protected void onContainerTranslationYChanged(float translationY) {
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
                animatorSet.addListener(new C07427());
                animatorSet.start();
                this.currentSheetAnimation = animatorSet;
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

    public void setItemText(int item, CharSequence text) {
        if (item >= 0) {
            if (item < this.itemViews.size()) {
                ((BottomSheetCell) this.itemViews.get(item)).textView.setText(text);
            }
        }
    }

    public boolean isDismissed() {
        return this.dismissed;
    }

    public void dismissWithButtonClick(final int item) {
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
                class C07431 implements Runnable {
                    C07431() {
                    }

                    public void run() {
                        try {
                            super.dismiss();
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                    }
                }

                public void onAnimationEnd(Animator animation) {
                    if (BottomSheet.this.currentSheetAnimation != null && BottomSheet.this.currentSheetAnimation.equals(animation)) {
                        BottomSheet.this.currentSheetAnimation = null;
                        if (BottomSheet.this.onClickListener != null) {
                            BottomSheet.this.onClickListener.onClick(BottomSheet.this, item);
                        }
                        AndroidUtilities.runOnUIThread(new C07431());
                    }
                }

                public void onAnimationCancel(Animator animation) {
                    if (BottomSheet.this.currentSheetAnimation != null && BottomSheet.this.currentSheetAnimation.equals(animation)) {
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
                    int height = this.containerView.getMeasuredHeight();
                    animatorSet.setDuration((long) Math.max(60, (int) ((180.0f * (((float) height) - this.containerView.getTranslationY())) / ((float) height))));
                    this.useFastDismiss = false;
                } else {
                    animatorSet.setDuration(180);
                }
                animatorSet.setInterpolator(new AccelerateInterpolator());
                animatorSet.addListener(new C07469());
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

    protected boolean onCustomCloseAnimation() {
        return false;
    }

    protected boolean onCustomOpenAnimation() {
        return false;
    }

    protected int getLeftInset() {
        if (this.lastInsets == null || VERSION.SDK_INT < 21) {
            return 0;
        }
        return this.lastInsets.getSystemWindowInsetLeft();
    }

    public void onConfigurationChanged(Configuration newConfig) {
    }

    public void onContainerDraw(Canvas canvas) {
    }
}
