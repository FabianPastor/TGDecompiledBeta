package org.telegram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.annotation.Keep;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnApplyWindowInsetsListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.WindowInsets;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ListView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.beta.R;

public class DrawerLayoutContainer extends FrameLayout {
    private static final int MIN_DRAWER_MARGIN = 64;
    private boolean allowDrawContent = true;
    private boolean allowOpenDrawer;
    private boolean beginTrackingSent;
    private AnimatorSet currentAnimation;
    private ViewGroup drawerLayout;
    private boolean drawerOpened;
    private float drawerPosition;
    private boolean inLayout;
    private Object lastInsets;
    private boolean maybeStartTracking;
    private int minDrawerMargin = ((int) ((64.0f * AndroidUtilities.density) + 0.5f));
    private int paddingTop;
    private ActionBarLayout parentActionBarLayout;
    private Rect rect = new Rect();
    private float scrimOpacity;
    private Paint scrimPaint = new Paint();
    private Drawable shadowLeft;
    private boolean startedTracking;
    private int startedTrackingPointerId;
    private int startedTrackingX;
    private int startedTrackingY;
    private VelocityTracker velocityTracker;

    /* renamed from: org.telegram.ui.ActionBar.DrawerLayoutContainer$1 */
    class C07481 implements OnApplyWindowInsetsListener {
        C07481() {
        }

        @SuppressLint({"NewApi"})
        public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
            DrawerLayoutContainer drawerLayout = (DrawerLayoutContainer) v;
            AndroidUtilities.statusBarHeight = insets.getSystemWindowInsetTop();
            DrawerLayoutContainer.this.lastInsets = insets;
            boolean z = insets.getSystemWindowInsetTop() <= 0 && DrawerLayoutContainer.this.getBackground() == null;
            drawerLayout.setWillNotDraw(z);
            drawerLayout.requestLayout();
            return insets.consumeSystemWindowInsets();
        }
    }

    /* renamed from: org.telegram.ui.ActionBar.DrawerLayoutContainer$2 */
    class C07492 extends AnimatorListenerAdapter {
        C07492() {
        }

        public void onAnimationEnd(Animator animator) {
            DrawerLayoutContainer.this.onDrawerAnimationEnd(true);
        }
    }

    /* renamed from: org.telegram.ui.ActionBar.DrawerLayoutContainer$3 */
    class C07503 extends AnimatorListenerAdapter {
        C07503() {
        }

        public void onAnimationEnd(Animator animator) {
            DrawerLayoutContainer.this.onDrawerAnimationEnd(false);
        }
    }

    public DrawerLayoutContainer(Context context) {
        super(context);
        setDescendantFocusability(262144);
        setFocusableInTouchMode(true);
        if (VERSION.SDK_INT >= 21) {
            setFitsSystemWindows(true);
            setOnApplyWindowInsetsListener(new C07481());
            setSystemUiVisibility(1280);
        }
        this.shadowLeft = getResources().getDrawable(R.drawable.menu_shadow);
    }

    @SuppressLint({"NewApi"})
    private void dispatchChildInsets(View child, Object insets, int drawerGravity) {
        WindowInsets wi = (WindowInsets) insets;
        if (drawerGravity == 3) {
            wi = wi.replaceSystemWindowInsets(wi.getSystemWindowInsetLeft(), wi.getSystemWindowInsetTop(), 0, wi.getSystemWindowInsetBottom());
        } else if (drawerGravity == 5) {
            wi = wi.replaceSystemWindowInsets(0, wi.getSystemWindowInsetTop(), wi.getSystemWindowInsetRight(), wi.getSystemWindowInsetBottom());
        }
        child.dispatchApplyWindowInsets(wi);
    }

    @SuppressLint({"NewApi"})
    private void applyMarginInsets(MarginLayoutParams lp, Object insets, int drawerGravity, boolean topOnly) {
        WindowInsets wi = (WindowInsets) insets;
        int i = 0;
        if (drawerGravity == 3) {
            wi = wi.replaceSystemWindowInsets(wi.getSystemWindowInsetLeft(), wi.getSystemWindowInsetTop(), 0, wi.getSystemWindowInsetBottom());
        } else if (drawerGravity == 5) {
            wi = wi.replaceSystemWindowInsets(0, wi.getSystemWindowInsetTop(), wi.getSystemWindowInsetRight(), wi.getSystemWindowInsetBottom());
        }
        lp.leftMargin = wi.getSystemWindowInsetLeft();
        if (!topOnly) {
            i = wi.getSystemWindowInsetTop();
        }
        lp.topMargin = i;
        lp.rightMargin = wi.getSystemWindowInsetRight();
        lp.bottomMargin = wi.getSystemWindowInsetBottom();
    }

    private int getTopInset(Object insets) {
        int i = 0;
        if (VERSION.SDK_INT < 21) {
            return 0;
        }
        if (insets != null) {
            i = ((WindowInsets) insets).getSystemWindowInsetTop();
        }
        return i;
    }

    public void setDrawerLayout(ViewGroup layout) {
        this.drawerLayout = layout;
        addView(this.drawerLayout);
        if (VERSION.SDK_INT >= 21) {
            this.drawerLayout.setFitsSystemWindows(true);
        }
    }

    public void moveDrawerByX(float dx) {
        setDrawerPosition(this.drawerPosition + dx);
    }

    @Keep
    public void setDrawerPosition(float value) {
        this.drawerPosition = value;
        if (this.drawerPosition > ((float) this.drawerLayout.getMeasuredWidth())) {
            this.drawerPosition = (float) this.drawerLayout.getMeasuredWidth();
        } else if (this.drawerPosition < 0.0f) {
            this.drawerPosition = 0.0f;
        }
        this.drawerLayout.setTranslationX(this.drawerPosition);
        int newVisibility = this.drawerPosition > 0.0f ? 0 : 8;
        if (this.drawerLayout.getVisibility() != newVisibility) {
            this.drawerLayout.setVisibility(newVisibility);
        }
        setScrimOpacity(this.drawerPosition / ((float) this.drawerLayout.getMeasuredWidth()));
    }

    public float getDrawerPosition() {
        return this.drawerPosition;
    }

    public void cancelCurrentAnimation() {
        if (this.currentAnimation != null) {
            this.currentAnimation.cancel();
            this.currentAnimation = null;
        }
    }

    public void openDrawer(boolean fast) {
        if (this.allowOpenDrawer) {
            if (!(!AndroidUtilities.isTablet() || this.parentActionBarLayout == null || this.parentActionBarLayout.parentActivity == null)) {
                AndroidUtilities.hideKeyboard(this.parentActionBarLayout.parentActivity.getCurrentFocus());
            }
            cancelCurrentAnimation();
            AnimatorSet animatorSet = new AnimatorSet();
            Animator[] animatorArr = new Animator[1];
            animatorArr[0] = ObjectAnimator.ofFloat(this, "drawerPosition", new float[]{(float) this.drawerLayout.getMeasuredWidth()});
            animatorSet.playTogether(animatorArr);
            animatorSet.setInterpolator(new DecelerateInterpolator());
            if (fast) {
                animatorSet.setDuration((long) Math.max((int) ((200.0f / ((float) this.drawerLayout.getMeasuredWidth())) * (((float) this.drawerLayout.getMeasuredWidth()) - this.drawerPosition)), 50));
            } else {
                animatorSet.setDuration(300);
            }
            animatorSet.addListener(new C07492());
            animatorSet.start();
            this.currentAnimation = animatorSet;
        }
    }

    public void closeDrawer(boolean fast) {
        cancelCurrentAnimation();
        AnimatorSet animatorSet = new AnimatorSet();
        Animator[] animatorArr = new Animator[1];
        animatorArr[0] = ObjectAnimator.ofFloat(this, "drawerPosition", new float[]{0.0f});
        animatorSet.playTogether(animatorArr);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        if (fast) {
            animatorSet.setDuration((long) Math.max((int) ((200.0f / ((float) this.drawerLayout.getMeasuredWidth())) * this.drawerPosition), 50));
        } else {
            animatorSet.setDuration(300);
        }
        animatorSet.addListener(new C07503());
        animatorSet.start();
    }

    private void onDrawerAnimationEnd(boolean opened) {
        this.startedTracking = false;
        this.currentAnimation = null;
        this.drawerOpened = opened;
        if (!opened && (this.drawerLayout instanceof ListView)) {
            ((ListView) this.drawerLayout).setSelectionFromTop(0, 0);
        }
    }

    private void setScrimOpacity(float value) {
        this.scrimOpacity = value;
        invalidate();
    }

    private float getScrimOpacity() {
        return this.scrimOpacity;
    }

    public View getDrawerLayout() {
        return this.drawerLayout;
    }

    public void setParentActionBarLayout(ActionBarLayout layout) {
        this.parentActionBarLayout = layout;
    }

    public void setAllowOpenDrawer(boolean value, boolean animated) {
        this.allowOpenDrawer = value;
        if (!this.allowOpenDrawer && this.drawerPosition != 0.0f) {
            if (animated) {
                closeDrawer(true);
                return;
            }
            setDrawerPosition(0.0f);
            onDrawerAnimationEnd(false);
        }
    }

    private void prepareForDrawerOpen(MotionEvent ev) {
        this.maybeStartTracking = false;
        this.startedTracking = true;
        if (ev != null) {
            this.startedTrackingX = (int) ev.getX();
        }
        this.beginTrackingSent = false;
    }

    public boolean isDrawerOpened() {
        return this.drawerOpened;
    }

    public void setAllowDrawContent(boolean value) {
        if (this.allowDrawContent != value) {
            this.allowDrawContent = value;
            invalidate();
        }
    }

    public boolean onTouchEvent(MotionEvent ev) {
        if (this.parentActionBarLayout.checkTransitionAnimation()) {
            return false;
        }
        boolean z = true;
        if (!this.drawerOpened || ev == null || ev.getX() <= this.drawerPosition || this.startedTracking) {
            if (this.allowOpenDrawer && this.parentActionBarLayout.fragmentsStack.size() == 1) {
                if (ev != null && ((ev.getAction() == 0 || ev.getAction() == 2) && !this.startedTracking && !this.maybeStartTracking)) {
                    this.parentActionBarLayout.getHitRect(this.rect);
                    this.startedTrackingX = (int) ev.getX();
                    this.startedTrackingY = (int) ev.getY();
                    if (this.rect.contains(this.startedTrackingX, this.startedTrackingY)) {
                        this.startedTrackingPointerId = ev.getPointerId(0);
                        this.maybeStartTracking = true;
                        cancelCurrentAnimation();
                        if (this.velocityTracker != null) {
                            this.velocityTracker.clear();
                        }
                    }
                } else if (ev != null && ev.getAction() == 2 && ev.getPointerId(0) == this.startedTrackingPointerId) {
                    if (this.velocityTracker == null) {
                        this.velocityTracker = VelocityTracker.obtain();
                    }
                    dx = (float) ((int) (ev.getX() - ((float) this.startedTrackingX)));
                    float dy = (float) Math.abs(((int) ev.getY()) - this.startedTrackingY);
                    this.velocityTracker.addMovement(ev);
                    if (this.maybeStartTracking && !this.startedTracking && ((dx > 0.0f && dx / 3.0f > Math.abs(dy) && Math.abs(dx) >= AndroidUtilities.getPixelsInCM(0.2f, true)) || (dx < 0.0f && Math.abs(dx) >= Math.abs(dy) && Math.abs(dx) >= AndroidUtilities.getPixelsInCM(0.4f, true)))) {
                        prepareForDrawerOpen(ev);
                        this.startedTrackingX = (int) ev.getX();
                        requestDisallowInterceptTouchEvent(true);
                    } else if (this.startedTracking) {
                        if (!this.beginTrackingSent) {
                            if (((Activity) getContext()).getCurrentFocus() != null) {
                                AndroidUtilities.hideKeyboard(((Activity) getContext()).getCurrentFocus());
                            }
                            this.beginTrackingSent = true;
                        }
                        moveDrawerByX(dx);
                        this.startedTrackingX = (int) ev.getX();
                    }
                } else if (ev == null || (ev != null && ev.getPointerId(0) == this.startedTrackingPointerId && (ev.getAction() == 3 || ev.getAction() == 1 || ev.getAction() == 6))) {
                    if (this.velocityTracker == null) {
                        this.velocityTracker = VelocityTracker.obtain();
                    }
                    this.velocityTracker.computeCurrentVelocity(1000);
                    if (this.startedTracking || !(this.drawerPosition == 0.0f || this.drawerPosition == ((float) this.drawerLayout.getMeasuredWidth()))) {
                        dx = this.velocityTracker.getXVelocity();
                        boolean backAnimation = (this.drawerPosition < ((float) this.drawerLayout.getMeasuredWidth()) / 2.0f && (dx < 3500.0f || Math.abs(dx) < Math.abs(this.velocityTracker.getYVelocity()))) || (dx < 0.0f && Math.abs(dx) >= 3500.0f);
                        if (backAnimation) {
                            if (!this.drawerOpened || Math.abs(dx) < 3500.0f) {
                                z = false;
                            }
                            closeDrawer(z);
                        } else {
                            if (this.drawerOpened || Math.abs(dx) < 3500.0f) {
                                z = false;
                            }
                            openDrawer(z);
                        }
                    }
                    this.startedTracking = false;
                    this.maybeStartTracking = false;
                    if (this.velocityTracker != null) {
                        this.velocityTracker.recycle();
                        this.velocityTracker = null;
                    }
                }
            }
            return this.startedTracking;
        }
        if (ev.getAction() == 1) {
            closeDrawer(false);
        }
        return true;
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!this.parentActionBarLayout.checkTransitionAnimation()) {
            if (!onTouchEvent(ev)) {
                return false;
            }
        }
        return true;
    }

    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        if (this.maybeStartTracking && !this.startedTracking) {
            onTouchEvent(null);
        }
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        this.inLayout = true;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != 8) {
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                if (!BuildVars.DEBUG_VERSION) {
                    try {
                        if (this.drawerLayout != child) {
                            child.layout(lp.leftMargin, lp.topMargin + getPaddingTop(), lp.leftMargin + child.getMeasuredWidth(), (lp.topMargin + child.getMeasuredHeight()) + getPaddingTop());
                        } else {
                            child.layout(-child.getMeasuredWidth(), lp.topMargin + getPaddingTop(), 0, (lp.topMargin + child.getMeasuredHeight()) + getPaddingTop());
                        }
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                } else if (this.drawerLayout != child) {
                    child.layout(lp.leftMargin, lp.topMargin + getPaddingTop(), lp.leftMargin + child.getMeasuredWidth(), (lp.topMargin + child.getMeasuredHeight()) + getPaddingTop());
                } else {
                    child.layout(-child.getMeasuredWidth(), lp.topMargin + getPaddingTop(), 0, (lp.topMargin + child.getMeasuredHeight()) + getPaddingTop());
                }
            }
        }
        this.inLayout = false;
    }

    public void requestLayout() {
        if (!this.inLayout) {
            super.requestLayout();
        }
    }

    @SuppressLint({"NewApi"})
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int i;
        int i2;
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(widthSize, heightSize);
        if (VERSION.SDK_INT < 21) {
            r0.inLayout = true;
            if (heightSize == AndroidUtilities.displaySize.y + AndroidUtilities.statusBarHeight) {
                if (getLayoutParams() instanceof MarginLayoutParams) {
                    setPadding(0, AndroidUtilities.statusBarHeight, 0, 0);
                }
                heightSize = AndroidUtilities.displaySize.y;
            } else if (getLayoutParams() instanceof MarginLayoutParams) {
                setPadding(0, 0, 0, 0);
            }
            r0.inLayout = false;
        }
        boolean applyInsets = r0.lastInsets != null && VERSION.SDK_INT >= 21;
        int childCount = getChildCount();
        for (int i3 = 0; i3 < childCount; i3++) {
            View child = getChildAt(i3);
            if (child.getVisibility() != 8) {
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                if (applyInsets) {
                    if (child.getFitsSystemWindows()) {
                        dispatchChildInsets(child, r0.lastInsets, lp.gravity);
                    } else if (child.getTag() == null) {
                        applyMarginInsets(lp, r0.lastInsets, lp.gravity, VERSION.SDK_INT >= 21);
                    }
                }
                if (r0.drawerLayout != child) {
                    child.measure(MeasureSpec.makeMeasureSpec((widthSize - lp.leftMargin) - lp.rightMargin, NUM), MeasureSpec.makeMeasureSpec((heightSize - lp.topMargin) - lp.bottomMargin, NUM));
                } else {
                    child.setPadding(0, 0, 0, 0);
                    child.measure(getChildMeasureSpec(widthMeasureSpec, (r0.minDrawerMargin + lp.leftMargin) + lp.rightMargin, lp.width), getChildMeasureSpec(heightMeasureSpec, lp.topMargin + lp.bottomMargin, lp.height));
                }
            }
            i = widthMeasureSpec;
            i2 = heightMeasureSpec;
        }
        i = widthMeasureSpec;
        i2 = heightMeasureSpec;
    }

    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        Canvas canvas2 = canvas;
        View view = child;
        if (!this.allowDrawContent) {
            return false;
        }
        int vright;
        int clipLeft;
        int height = getHeight();
        boolean drawingContent = view != r0.drawerLayout;
        int clipLeft2 = 0;
        int clipRight = getWidth();
        int restoreCount = canvas.save();
        if (drawingContent) {
            int childCount = getChildCount();
            int lastVisibleChild = 0;
            for (int lastVisibleChild2 = 0; lastVisibleChild2 < childCount; lastVisibleChild2++) {
                View v = getChildAt(lastVisibleChild2);
                if (v.getVisibility() == 0 && v != r0.drawerLayout) {
                    lastVisibleChild = lastVisibleChild2;
                }
                if (v != view && v.getVisibility() == 0 && v == r0.drawerLayout) {
                    if (v.getHeight() >= height) {
                        vright = v.getRight();
                        if (vright > clipLeft2) {
                            clipLeft2 = vright;
                        }
                    }
                }
            }
            if (clipLeft2 != 0) {
                canvas2.clipRect(clipLeft2, 0, clipRight, getHeight());
            }
            clipLeft = clipLeft2;
            vright = lastVisibleChild;
        } else {
            vright = 0;
            clipLeft = 0;
        }
        boolean result = super.drawChild(canvas, child, drawingTime);
        canvas2.restoreToCount(restoreCount);
        if (r0.scrimOpacity <= 0.0f || !drawingContent) {
            if (r0.shadowLeft != null) {
                float alpha = Math.max(0.0f, Math.min(r0.drawerPosition / ((float) AndroidUtilities.dp(20.0f)), 1.0f));
                if (alpha != 0.0f) {
                    r0.shadowLeft.setBounds((int) r0.drawerPosition, child.getTop(), ((int) r0.drawerPosition) + r0.shadowLeft.getIntrinsicWidth(), child.getBottom());
                    r0.shadowLeft.setAlpha((int) (255.0f * alpha));
                    r0.shadowLeft.draw(canvas2);
                }
            }
        } else if (indexOfChild(view) == vright) {
            r0.scrimPaint.setColor(((int) (153.0f * r0.scrimOpacity)) << 24);
            canvas2.drawRect((float) clipLeft, 0.0f, (float) clipRight, (float) getHeight(), r0.scrimPaint);
        }
        return result;
    }

    public boolean hasOverlappingRendering() {
        return false;
    }
}
