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
import org.telegram.messenger.C0446R;
import org.telegram.messenger.FileLog;

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
    class C07551 implements OnApplyWindowInsetsListener {
        C07551() {
        }

        @SuppressLint({"NewApi"})
        public WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
            DrawerLayoutContainer drawerLayoutContainer = (DrawerLayoutContainer) view;
            AndroidUtilities.statusBarHeight = windowInsets.getSystemWindowInsetTop();
            DrawerLayoutContainer.this.lastInsets = windowInsets;
            boolean z = windowInsets.getSystemWindowInsetTop() <= 0 && DrawerLayoutContainer.this.getBackground() == null;
            drawerLayoutContainer.setWillNotDraw(z);
            drawerLayoutContainer.requestLayout();
            return windowInsets.consumeSystemWindowInsets();
        }
    }

    /* renamed from: org.telegram.ui.ActionBar.DrawerLayoutContainer$2 */
    class C07562 extends AnimatorListenerAdapter {
        C07562() {
        }

        public void onAnimationEnd(Animator animator) {
            DrawerLayoutContainer.this.onDrawerAnimationEnd(true);
        }
    }

    /* renamed from: org.telegram.ui.ActionBar.DrawerLayoutContainer$3 */
    class C07573 extends AnimatorListenerAdapter {
        C07573() {
        }

        public void onAnimationEnd(Animator animator) {
            DrawerLayoutContainer.this.onDrawerAnimationEnd(false);
        }
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    public DrawerLayoutContainer(Context context) {
        super(context);
        setDescendantFocusability(262144);
        setFocusableInTouchMode(true);
        if (VERSION.SDK_INT >= 21) {
            setFitsSystemWindows(true);
            setOnApplyWindowInsetsListener(new C07551());
            setSystemUiVisibility(1280);
        }
        this.shadowLeft = getResources().getDrawable(C0446R.drawable.menu_shadow);
    }

    @SuppressLint({"NewApi"})
    private void dispatchChildInsets(View view, Object obj, int i) {
        obj = (WindowInsets) obj;
        if (i == 3) {
            obj = obj.replaceSystemWindowInsets(obj.getSystemWindowInsetLeft(), obj.getSystemWindowInsetTop(), 0, obj.getSystemWindowInsetBottom());
        } else if (i == 5) {
            obj = obj.replaceSystemWindowInsets(0, obj.getSystemWindowInsetTop(), obj.getSystemWindowInsetRight(), obj.getSystemWindowInsetBottom());
        }
        view.dispatchApplyWindowInsets(obj);
    }

    @SuppressLint({"NewApi"})
    private void applyMarginInsets(MarginLayoutParams marginLayoutParams, Object obj, int i, boolean z) {
        obj = (WindowInsets) obj;
        int i2 = 0;
        if (i == 3) {
            obj = obj.replaceSystemWindowInsets(obj.getSystemWindowInsetLeft(), obj.getSystemWindowInsetTop(), 0, obj.getSystemWindowInsetBottom());
        } else if (i == 5) {
            obj = obj.replaceSystemWindowInsets(0, obj.getSystemWindowInsetTop(), obj.getSystemWindowInsetRight(), obj.getSystemWindowInsetBottom());
        }
        marginLayoutParams.leftMargin = obj.getSystemWindowInsetLeft();
        if (!z) {
            i2 = obj.getSystemWindowInsetTop();
        }
        marginLayoutParams.topMargin = i2;
        marginLayoutParams.rightMargin = obj.getSystemWindowInsetRight();
        marginLayoutParams.bottomMargin = obj.getSystemWindowInsetBottom();
    }

    private int getTopInset(Object obj) {
        int i = 0;
        if (VERSION.SDK_INT < 21) {
            return 0;
        }
        if (obj != null) {
            i = ((WindowInsets) obj).getSystemWindowInsetTop();
        }
        return i;
    }

    public void setDrawerLayout(ViewGroup viewGroup) {
        this.drawerLayout = viewGroup;
        addView(this.drawerLayout);
        if (VERSION.SDK_INT >= 21) {
            this.drawerLayout.setFitsSystemWindows(true);
        }
    }

    public void moveDrawerByX(float f) {
        setDrawerPosition(this.drawerPosition + f);
    }

    @Keep
    public void setDrawerPosition(float f) {
        this.drawerPosition = f;
        if (this.drawerPosition > ((float) this.drawerLayout.getMeasuredWidth())) {
            this.drawerPosition = (float) this.drawerLayout.getMeasuredWidth();
        } else if (this.drawerPosition < 0.0f) {
            this.drawerPosition = 0.0f;
        }
        this.drawerLayout.setTranslationX(this.drawerPosition);
        int i = this.drawerPosition > 0.0f ? 0 : 8;
        if (this.drawerLayout.getVisibility() != i) {
            this.drawerLayout.setVisibility(i);
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

    public void openDrawer(boolean z) {
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
            if (z) {
                animatorSet.setDuration((long) Math.max((int) ((true / ((float) this.drawerLayout.getMeasuredWidth())) * (((float) this.drawerLayout.getMeasuredWidth()) - this.drawerPosition)), 50));
            } else {
                animatorSet.setDuration(300);
            }
            animatorSet.addListener(new C07562());
            animatorSet.start();
            this.currentAnimation = animatorSet;
        }
    }

    public void closeDrawer(boolean z) {
        cancelCurrentAnimation();
        AnimatorSet animatorSet = new AnimatorSet();
        Animator[] animatorArr = new Animator[1];
        animatorArr[0] = ObjectAnimator.ofFloat(this, "drawerPosition", new float[]{0.0f});
        animatorSet.playTogether(animatorArr);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        if (z) {
            animatorSet.setDuration((long) Math.max((int) ((true / ((float) this.drawerLayout.getMeasuredWidth())) * this.drawerPosition), 50));
        } else {
            animatorSet.setDuration(300);
        }
        animatorSet.addListener(new C07573());
        animatorSet.start();
    }

    private void onDrawerAnimationEnd(boolean z) {
        this.startedTracking = false;
        this.currentAnimation = null;
        this.drawerOpened = z;
        if (!z && (this.drawerLayout instanceof ListView)) {
            ((ListView) this.drawerLayout).setSelectionFromTop(0, 0);
        }
    }

    private void setScrimOpacity(float f) {
        this.scrimOpacity = f;
        invalidate();
    }

    private float getScrimOpacity() {
        return this.scrimOpacity;
    }

    public View getDrawerLayout() {
        return this.drawerLayout;
    }

    public void setParentActionBarLayout(ActionBarLayout actionBarLayout) {
        this.parentActionBarLayout = actionBarLayout;
    }

    public void setAllowOpenDrawer(boolean z, boolean z2) {
        this.allowOpenDrawer = z;
        if (!this.allowOpenDrawer && this.drawerPosition) {
            if (z2) {
                closeDrawer(true);
                return;
            }
            setDrawerPosition(0.0f);
            onDrawerAnimationEnd(false);
        }
    }

    private void prepareForDrawerOpen(MotionEvent motionEvent) {
        this.maybeStartTracking = false;
        this.startedTracking = true;
        if (motionEvent != null) {
            this.startedTrackingX = (int) motionEvent.getX();
        }
        this.beginTrackingSent = false;
    }

    public boolean isDrawerOpened() {
        return this.drawerOpened;
    }

    public void setAllowDrawContent(boolean z) {
        if (this.allowDrawContent != z) {
            this.allowDrawContent = z;
            invalidate();
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.parentActionBarLayout.checkTransitionAnimation()) {
            return false;
        }
        boolean z = true;
        if (!this.drawerOpened || motionEvent == null || motionEvent.getX() <= this.drawerPosition || this.startedTracking) {
            if (this.allowOpenDrawer && this.parentActionBarLayout.fragmentsStack.size() == 1) {
                if (motionEvent != null && ((motionEvent.getAction() == 0 || motionEvent.getAction() == 2) && !this.startedTracking && !this.maybeStartTracking)) {
                    this.parentActionBarLayout.getHitRect(this.rect);
                    this.startedTrackingX = (int) motionEvent.getX();
                    this.startedTrackingY = (int) motionEvent.getY();
                    if (this.rect.contains(this.startedTrackingX, this.startedTrackingY)) {
                        this.startedTrackingPointerId = motionEvent.getPointerId(0);
                        this.maybeStartTracking = true;
                        cancelCurrentAnimation();
                        if (this.velocityTracker != null) {
                            this.velocityTracker.clear();
                        }
                    }
                } else if (motionEvent != null && motionEvent.getAction() == 2 && motionEvent.getPointerId(0) == this.startedTrackingPointerId) {
                    if (this.velocityTracker == null) {
                        this.velocityTracker = VelocityTracker.obtain();
                    }
                    float x = (float) ((int) (motionEvent.getX() - ((float) this.startedTrackingX)));
                    float abs = (float) Math.abs(((int) motionEvent.getY()) - this.startedTrackingY);
                    this.velocityTracker.addMovement(motionEvent);
                    if (this.maybeStartTracking && !this.startedTracking && ((x > 0.0f && x / 3.0f > Math.abs(abs) && Math.abs(x) >= AndroidUtilities.getPixelsInCM(0.2f, true)) || (x < 0.0f && Math.abs(x) >= Math.abs(abs) && Math.abs(x) >= AndroidUtilities.getPixelsInCM(0.4f, true)))) {
                        prepareForDrawerOpen(motionEvent);
                        this.startedTrackingX = (int) motionEvent.getX();
                        requestDisallowInterceptTouchEvent(true);
                    } else if (this.startedTracking) {
                        if (!this.beginTrackingSent) {
                            if (((Activity) getContext()).getCurrentFocus() != null) {
                                AndroidUtilities.hideKeyboard(((Activity) getContext()).getCurrentFocus());
                            }
                            this.beginTrackingSent = true;
                        }
                        moveDrawerByX(x);
                        this.startedTrackingX = (int) motionEvent.getX();
                    }
                } else if (motionEvent == null || (motionEvent != null && motionEvent.getPointerId(0) == this.startedTrackingPointerId && (motionEvent.getAction() == 3 || motionEvent.getAction() == 1 || motionEvent.getAction() == 6))) {
                    if (this.velocityTracker == null) {
                        this.velocityTracker = VelocityTracker.obtain();
                    }
                    this.velocityTracker.computeCurrentVelocity(1000);
                    if (!(this.startedTracking == null && (this.drawerPosition == null || this.drawerPosition == ((float) this.drawerLayout.getMeasuredWidth())))) {
                        motionEvent = this.velocityTracker.getXVelocity();
                        boolean z2 = (this.drawerPosition < ((float) this.drawerLayout.getMeasuredWidth()) / 2.0f && (motionEvent < NUM || Math.abs(motionEvent) < Math.abs(this.velocityTracker.getYVelocity()))) || (motionEvent < null && Math.abs(motionEvent) >= 3500.0f);
                        if (z2) {
                            if (!this.drawerOpened || Math.abs(motionEvent) < NUM) {
                                z = false;
                            }
                            closeDrawer(z);
                        } else {
                            if (this.drawerOpened || Math.abs(motionEvent) < NUM) {
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
        if (motionEvent.getAction() == 1) {
            closeDrawer(false);
        }
        return true;
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (!this.parentActionBarLayout.checkTransitionAnimation()) {
            if (onTouchEvent(motionEvent) == null) {
                return null;
            }
        }
        return true;
    }

    public void requestDisallowInterceptTouchEvent(boolean z) {
        if (this.maybeStartTracking && !this.startedTracking) {
            onTouchEvent(null);
        }
        super.requestDisallowInterceptTouchEvent(z);
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        this.inLayout = true;
        z = getChildCount();
        for (boolean z2 = false; z2 < z; z2++) {
            i3 = getChildAt(z2);
            if (i3.getVisibility() != 8) {
                LayoutParams layoutParams = (LayoutParams) i3.getLayoutParams();
                if (!BuildVars.DEBUG_VERSION) {
                    try {
                        if (this.drawerLayout != i3) {
                            i3.layout(layoutParams.leftMargin, layoutParams.topMargin + getPaddingTop(), layoutParams.leftMargin + i3.getMeasuredWidth(), (layoutParams.topMargin + i3.getMeasuredHeight()) + getPaddingTop());
                        } else {
                            i3.layout(-i3.getMeasuredWidth(), layoutParams.topMargin + getPaddingTop(), 0, (layoutParams.topMargin + i3.getMeasuredHeight()) + getPaddingTop());
                        }
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                } else if (this.drawerLayout != i3) {
                    i3.layout(layoutParams.leftMargin, layoutParams.topMargin + getPaddingTop(), layoutParams.leftMargin + i3.getMeasuredWidth(), (layoutParams.topMargin + i3.getMeasuredHeight()) + getPaddingTop());
                } else {
                    i3.layout(-i3.getMeasuredWidth(), layoutParams.topMargin + getPaddingTop(), 0, (layoutParams.topMargin + i3.getMeasuredHeight()) + getPaddingTop());
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
    protected void onMeasure(int i, int i2) {
        int size = MeasureSpec.getSize(i);
        int size2 = MeasureSpec.getSize(i2);
        setMeasuredDimension(size, size2);
        if (VERSION.SDK_INT < 21) {
            this.inLayout = true;
            if (size2 == AndroidUtilities.displaySize.y + AndroidUtilities.statusBarHeight) {
                if (getLayoutParams() instanceof MarginLayoutParams) {
                    setPadding(0, AndroidUtilities.statusBarHeight, 0, 0);
                }
                size2 = AndroidUtilities.displaySize.y;
            } else if (getLayoutParams() instanceof MarginLayoutParams) {
                setPadding(0, 0, 0, 0);
            }
            this.inLayout = false;
        }
        boolean z = this.lastInsets != null && VERSION.SDK_INT >= 21;
        int childCount = getChildCount();
        for (int i3 = 0; i3 < childCount; i3++) {
            View childAt = getChildAt(i3);
            if (childAt.getVisibility() != 8) {
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                if (z) {
                    if (childAt.getFitsSystemWindows()) {
                        dispatchChildInsets(childAt, this.lastInsets, layoutParams.gravity);
                    } else if (childAt.getTag() == null) {
                        applyMarginInsets(layoutParams, this.lastInsets, layoutParams.gravity, VERSION.SDK_INT >= 21);
                    }
                }
                if (this.drawerLayout != childAt) {
                    childAt.measure(MeasureSpec.makeMeasureSpec((size - layoutParams.leftMargin) - layoutParams.rightMargin, NUM), MeasureSpec.makeMeasureSpec((size2 - layoutParams.topMargin) - layoutParams.bottomMargin, NUM));
                } else {
                    childAt.setPadding(0, 0, 0, 0);
                    childAt.measure(getChildMeasureSpec(i, (this.minDrawerMargin + layoutParams.leftMargin) + layoutParams.rightMargin, layoutParams.width), getChildMeasureSpec(i2, layoutParams.topMargin + layoutParams.bottomMargin, layoutParams.height));
                }
            }
        }
    }

    protected boolean drawChild(Canvas canvas, View view, long j) {
        Canvas canvas2 = canvas;
        View view2 = view;
        if (!this.allowDrawContent) {
            return false;
        }
        int i;
        int i2;
        int height = getHeight();
        boolean z = view2 != r0.drawerLayout;
        int width = getWidth();
        int save = canvas2.save();
        if (z) {
            int childCount = getChildCount();
            int i3 = 0;
            i = i3;
            i2 = i;
            while (i3 < childCount) {
                View childAt = getChildAt(i3);
                if (childAt.getVisibility() == 0 && childAt != r0.drawerLayout) {
                    i2 = i3;
                }
                if (childAt != view2 && childAt.getVisibility() == 0 && childAt == r0.drawerLayout) {
                    if (childAt.getHeight() >= height) {
                        int right = childAt.getRight();
                        if (right > i) {
                            i = right;
                        }
                    }
                }
                i3++;
            }
            if (i != 0) {
                canvas2.clipRect(i, 0, width, getHeight());
            }
        } else {
            i = 0;
            i2 = i;
        }
        boolean drawChild = super.drawChild(canvas, view, j);
        canvas2.restoreToCount(save);
        if (r0.scrimOpacity <= 0.0f || !z) {
            if (r0.shadowLeft != null) {
                float max = Math.max(0.0f, Math.min(r0.drawerPosition / ((float) AndroidUtilities.dp(20.0f)), 1.0f));
                if (max != 0.0f) {
                    r0.shadowLeft.setBounds((int) r0.drawerPosition, view.getTop(), ((int) r0.drawerPosition) + r0.shadowLeft.getIntrinsicWidth(), view.getBottom());
                    r0.shadowLeft.setAlpha((int) (255.0f * max));
                    r0.shadowLeft.draw(canvas2);
                }
            }
        } else if (indexOfChild(view2) == i2) {
            r0.scrimPaint.setColor(((int) (153.0f * r0.scrimOpacity)) << 24);
            canvas2.drawRect((float) i, 0.0f, (float) width, (float) getHeight(), r0.scrimPaint);
        }
        return drawChild;
    }
}
