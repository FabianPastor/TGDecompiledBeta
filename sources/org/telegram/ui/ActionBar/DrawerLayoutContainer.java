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
import android.view.DisplayCutout;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.WindowInsets;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ListView;
import androidx.annotation.Keep;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;

public class DrawerLayoutContainer extends FrameLayout {
    private static final int MIN_DRAWER_MARGIN = 64;
    private boolean allowDrawContent = true;
    private boolean allowOpenDrawer;
    private Paint backgroundPaint = new Paint();
    private boolean beginTrackingSent;
    private int behindKeyboardColor;
    private AnimatorSet currentAnimation;
    private ViewGroup drawerLayout;
    private boolean drawerOpened;
    private float drawerPosition;
    private boolean hasCutout;
    private boolean inLayout;
    private Object lastInsets;
    private boolean maybeStartTracking;
    private int minDrawerMargin = ((int) ((AndroidUtilities.density * 64.0f) + 0.5f));
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

    public boolean hasOverlappingRendering() {
        return false;
    }

    public DrawerLayoutContainer(Context context) {
        super(context);
        setDescendantFocusability(262144);
        setFocusableInTouchMode(true);
        if (VERSION.SDK_INT >= 21) {
            setFitsSystemWindows(true);
            setOnApplyWindowInsetsListener(new -$$Lambda$DrawerLayoutContainer$dOsUXLZuN_il_QGSmGuPct7OsoA(this));
            setSystemUiVisibility(1280);
        }
        this.shadowLeft = getResources().getDrawable(NUM);
    }

    public /* synthetic */ WindowInsets lambda$new$0$DrawerLayoutContainer(View view, WindowInsets windowInsets) {
        DrawerLayoutContainer drawerLayoutContainer = (DrawerLayoutContainer) view;
        if (AndroidUtilities.statusBarHeight != windowInsets.getSystemWindowInsetTop()) {
            drawerLayoutContainer.requestLayout();
        }
        AndroidUtilities.statusBarHeight = windowInsets.getSystemWindowInsetTop();
        this.lastInsets = windowInsets;
        boolean z = true;
        boolean z2 = windowInsets.getSystemWindowInsetTop() <= 0 && getBackground() == null;
        drawerLayoutContainer.setWillNotDraw(z2);
        if (VERSION.SDK_INT >= 28) {
            DisplayCutout displayCutout = windowInsets.getDisplayCutout();
            if (displayCutout == null || displayCutout.getBoundingRects().size() == 0) {
                z = false;
            }
            this.hasCutout = z;
        }
        invalidate();
        return windowInsets.consumeSystemWindowInsets();
    }

    @SuppressLint({"NewApi"})
    private void dispatchChildInsets(View view, Object obj, int i) {
        WindowInsets windowInsets = (WindowInsets) obj;
        if (i == 3) {
            windowInsets = windowInsets.replaceSystemWindowInsets(windowInsets.getSystemWindowInsetLeft(), windowInsets.getSystemWindowInsetTop(), 0, windowInsets.getSystemWindowInsetBottom());
        } else if (i == 5) {
            windowInsets = windowInsets.replaceSystemWindowInsets(0, windowInsets.getSystemWindowInsetTop(), windowInsets.getSystemWindowInsetRight(), windowInsets.getSystemWindowInsetBottom());
        }
        view.dispatchApplyWindowInsets(windowInsets);
    }

    @SuppressLint({"NewApi"})
    private void applyMarginInsets(MarginLayoutParams marginLayoutParams, Object obj, int i, boolean z) {
        WindowInsets windowInsets = (WindowInsets) obj;
        int i2 = 0;
        if (i == 3) {
            windowInsets = windowInsets.replaceSystemWindowInsets(windowInsets.getSystemWindowInsetLeft(), windowInsets.getSystemWindowInsetTop(), 0, windowInsets.getSystemWindowInsetBottom());
        } else if (i == 5) {
            windowInsets = windowInsets.replaceSystemWindowInsets(0, windowInsets.getSystemWindowInsetTop(), windowInsets.getSystemWindowInsetRight(), windowInsets.getSystemWindowInsetBottom());
        }
        marginLayoutParams.leftMargin = windowInsets.getSystemWindowInsetLeft();
        if (!z) {
            i2 = windowInsets.getSystemWindowInsetTop();
        }
        marginLayoutParams.topMargin = i2;
        marginLayoutParams.rightMargin = windowInsets.getSystemWindowInsetRight();
        marginLayoutParams.bottomMargin = windowInsets.getSystemWindowInsetBottom();
    }

    private int getTopInset(Object obj) {
        if (VERSION.SDK_INT < 21 || obj == null) {
            return 0;
        }
        return ((WindowInsets) obj).getSystemWindowInsetTop();
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
        AnimatorSet animatorSet = this.currentAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.currentAnimation = null;
        }
    }

    public void openDrawer(boolean z) {
        if (this.allowOpenDrawer) {
            if (AndroidUtilities.isTablet()) {
                ActionBarLayout actionBarLayout = this.parentActionBarLayout;
                if (actionBarLayout != null) {
                    Activity activity = actionBarLayout.parentActivity;
                    if (activity != null) {
                        AndroidUtilities.hideKeyboard(activity.getCurrentFocus());
                    }
                }
            }
            cancelCurrentAnimation();
            AnimatorSet animatorSet = new AnimatorSet();
            Animator[] animatorArr = new Animator[1];
            animatorArr[0] = ObjectAnimator.ofFloat(this, "drawerPosition", new float[]{(float) this.drawerLayout.getMeasuredWidth()});
            animatorSet.playTogether(animatorArr);
            animatorSet.setInterpolator(new DecelerateInterpolator());
            if (z) {
                animatorSet.setDuration((long) Math.max((int) ((200.0f / ((float) this.drawerLayout.getMeasuredWidth())) * (((float) this.drawerLayout.getMeasuredWidth()) - this.drawerPosition)), 50));
            } else {
                animatorSet.setDuration(300);
            }
            animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    DrawerLayoutContainer.this.onDrawerAnimationEnd(true);
                }
            });
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
            animatorSet.setDuration((long) Math.max((int) ((200.0f / ((float) this.drawerLayout.getMeasuredWidth())) * this.drawerPosition), 50));
        } else {
            animatorSet.setDuration(300);
        }
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                DrawerLayoutContainer.this.onDrawerAnimationEnd(false);
            }
        });
        animatorSet.start();
    }

    private void onDrawerAnimationEnd(boolean z) {
        this.startedTracking = false;
        this.currentAnimation = null;
        this.drawerOpened = z;
        if (!z) {
            ViewGroup viewGroup = this.drawerLayout;
            if (viewGroup instanceof ListView) {
                ((ListView) viewGroup).setSelectionFromTop(0, 0);
            }
        }
        if (VERSION.SDK_INT >= 19) {
            for (int i = 0; i < getChildCount(); i++) {
                View childAt = getChildAt(i);
                if (childAt != this.drawerLayout) {
                    childAt.setImportantForAccessibility(z ? 4 : 0);
                }
            }
        }
        sendAccessibilityEvent(32);
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
        if (!this.allowOpenDrawer && this.drawerPosition != 0.0f) {
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

    /* JADX WARNING: Removed duplicated region for block: B:115:0x01f2  */
    /* JADX WARNING: Missing block: B:87:0x0185, code skipped:
            if (r8 != ((float) r7.drawerLayout.getMeasuredWidth())) goto L_0x0187;
     */
    public boolean onTouchEvent(android.view.MotionEvent r8) {
        /*
        r7 = this;
        r0 = r7.parentActionBarLayout;
        r0 = r0.checkTransitionAnimation();
        r1 = 0;
        if (r0 != 0) goto L_0x01fb;
    L_0x0009:
        r0 = r7.drawerOpened;
        r2 = 1;
        if (r0 == 0) goto L_0x0028;
    L_0x000e:
        if (r8 == 0) goto L_0x0028;
    L_0x0010:
        r0 = r8.getX();
        r3 = r7.drawerPosition;
        r0 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1));
        if (r0 <= 0) goto L_0x0028;
    L_0x001a:
        r0 = r7.startedTracking;
        if (r0 != 0) goto L_0x0028;
    L_0x001e:
        r8 = r8.getAction();
        if (r8 != r2) goto L_0x0027;
    L_0x0024:
        r7.closeDrawer(r1);
    L_0x0027:
        return r2;
    L_0x0028:
        r0 = r7.allowOpenDrawer;
        if (r0 == 0) goto L_0x01f8;
    L_0x002c:
        r0 = r7.parentActionBarLayout;
        r0 = r0.fragmentsStack;
        r0 = r0.size();
        if (r0 != r2) goto L_0x01f8;
    L_0x0036:
        r0 = 2;
        if (r8 == 0) goto L_0x0082;
    L_0x0039:
        r3 = r8.getAction();
        if (r3 == 0) goto L_0x0045;
    L_0x003f:
        r3 = r8.getAction();
        if (r3 != r0) goto L_0x0082;
    L_0x0045:
        r3 = r7.startedTracking;
        if (r3 != 0) goto L_0x0082;
    L_0x0049:
        r3 = r7.maybeStartTracking;
        if (r3 != 0) goto L_0x0082;
    L_0x004d:
        r0 = r7.parentActionBarLayout;
        r3 = r7.rect;
        r0.getHitRect(r3);
        r0 = r8.getX();
        r0 = (int) r0;
        r7.startedTrackingX = r0;
        r0 = r8.getY();
        r0 = (int) r0;
        r7.startedTrackingY = r0;
        r0 = r7.rect;
        r3 = r7.startedTrackingX;
        r4 = r7.startedTrackingY;
        r0 = r0.contains(r3, r4);
        if (r0 == 0) goto L_0x01f8;
    L_0x006e:
        r8 = r8.getPointerId(r1);
        r7.startedTrackingPointerId = r8;
        r7.maybeStartTracking = r2;
        r7.cancelCurrentAnimation();
        r8 = r7.velocityTracker;
        if (r8 == 0) goto L_0x01f8;
    L_0x007d:
        r8.clear();
        goto L_0x01f8;
    L_0x0082:
        r3 = 0;
        if (r8 == 0) goto L_0x0141;
    L_0x0085:
        r4 = r8.getAction();
        if (r4 != r0) goto L_0x0141;
    L_0x008b:
        r0 = r8.getPointerId(r1);
        r4 = r7.startedTrackingPointerId;
        if (r0 != r4) goto L_0x0141;
    L_0x0093:
        r0 = r7.velocityTracker;
        if (r0 != 0) goto L_0x009d;
    L_0x0097:
        r0 = android.view.VelocityTracker.obtain();
        r7.velocityTracker = r0;
    L_0x009d:
        r0 = r8.getX();
        r1 = r7.startedTrackingX;
        r1 = (float) r1;
        r0 = r0 - r1;
        r0 = (int) r0;
        r0 = (float) r0;
        r1 = r8.getY();
        r1 = (int) r1;
        r4 = r7.startedTrackingY;
        r1 = r1 - r4;
        r1 = java.lang.Math.abs(r1);
        r1 = (float) r1;
        r4 = r7.velocityTracker;
        r4.addMovement(r8);
        r4 = r7.maybeStartTracking;
        if (r4 == 0) goto L_0x0112;
    L_0x00bd:
        r4 = r7.startedTracking;
        if (r4 != 0) goto L_0x0112;
    L_0x00c1:
        r4 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1));
        if (r4 <= 0) goto L_0x00e0;
    L_0x00c5:
        r4 = NUM; // 0x40400000 float:3.0 double:5.325712093E-315;
        r4 = r0 / r4;
        r5 = java.lang.Math.abs(r1);
        r4 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1));
        if (r4 <= 0) goto L_0x00e0;
    L_0x00d1:
        r4 = java.lang.Math.abs(r0);
        r5 = NUM; // 0x3e4ccccd float:0.2 double:5.164075695E-315;
        r5 = org.telegram.messenger.AndroidUtilities.getPixelsInCM(r5, r2);
        r4 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1));
        if (r4 >= 0) goto L_0x0103;
    L_0x00e0:
        r4 = r7.drawerOpened;
        if (r4 == 0) goto L_0x0112;
    L_0x00e4:
        r3 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1));
        if (r3 >= 0) goto L_0x0112;
    L_0x00e8:
        r3 = java.lang.Math.abs(r0);
        r1 = java.lang.Math.abs(r1);
        r1 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1));
        if (r1 < 0) goto L_0x0112;
    L_0x00f4:
        r1 = java.lang.Math.abs(r0);
        r3 = NUM; // 0x3ecccccd float:0.4 double:5.205520926E-315;
        r3 = org.telegram.messenger.AndroidUtilities.getPixelsInCM(r3, r2);
        r1 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1));
        if (r1 < 0) goto L_0x0112;
    L_0x0103:
        r7.prepareForDrawerOpen(r8);
        r8 = r8.getX();
        r8 = (int) r8;
        r7.startedTrackingX = r8;
        r7.requestDisallowInterceptTouchEvent(r2);
        goto L_0x01f8;
    L_0x0112:
        r1 = r7.startedTracking;
        if (r1 == 0) goto L_0x01f8;
    L_0x0116:
        r1 = r7.beginTrackingSent;
        if (r1 != 0) goto L_0x0135;
    L_0x011a:
        r1 = r7.getContext();
        r1 = (android.app.Activity) r1;
        r1 = r1.getCurrentFocus();
        if (r1 == 0) goto L_0x0133;
    L_0x0126:
        r1 = r7.getContext();
        r1 = (android.app.Activity) r1;
        r1 = r1.getCurrentFocus();
        org.telegram.messenger.AndroidUtilities.hideKeyboard(r1);
    L_0x0133:
        r7.beginTrackingSent = r2;
    L_0x0135:
        r7.moveDrawerByX(r0);
        r8 = r8.getX();
        r8 = (int) r8;
        r7.startedTrackingX = r8;
        goto L_0x01f8;
    L_0x0141:
        if (r8 == 0) goto L_0x0161;
    L_0x0143:
        if (r8 == 0) goto L_0x01f8;
    L_0x0145:
        r0 = r8.getPointerId(r1);
        r4 = r7.startedTrackingPointerId;
        if (r0 != r4) goto L_0x01f8;
    L_0x014d:
        r0 = r8.getAction();
        r4 = 3;
        if (r0 == r4) goto L_0x0161;
    L_0x0154:
        r0 = r8.getAction();
        if (r0 == r2) goto L_0x0161;
    L_0x015a:
        r8 = r8.getAction();
        r0 = 6;
        if (r8 != r0) goto L_0x01f8;
    L_0x0161:
        r8 = r7.velocityTracker;
        if (r8 != 0) goto L_0x016b;
    L_0x0165:
        r8 = android.view.VelocityTracker.obtain();
        r7.velocityTracker = r8;
    L_0x016b:
        r8 = r7.velocityTracker;
        r0 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r8.computeCurrentVelocity(r0);
        r8 = r7.startedTracking;
        if (r8 != 0) goto L_0x0187;
    L_0x0176:
        r8 = r7.drawerPosition;
        r0 = (r8 > r3 ? 1 : (r8 == r3 ? 0 : -1));
        if (r0 == 0) goto L_0x01ea;
    L_0x017c:
        r0 = r7.drawerLayout;
        r0 = r0.getMeasuredWidth();
        r0 = (float) r0;
        r8 = (r8 > r0 ? 1 : (r8 == r0 ? 0 : -1));
        if (r8 == 0) goto L_0x01ea;
    L_0x0187:
        r8 = r7.velocityTracker;
        r8 = r8.getXVelocity();
        r0 = r7.velocityTracker;
        r0 = r0.getYVelocity();
        r4 = r7.drawerPosition;
        r5 = r7.drawerLayout;
        r5 = r5.getMeasuredWidth();
        r5 = (float) r5;
        r6 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r5 = r5 / r6;
        r6 = NUM; // 0x455aCLASSNAME float:3500.0 double:5.7488258E-315;
        r4 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1));
        if (r4 >= 0) goto L_0x01b6;
    L_0x01a6:
        r4 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1));
        if (r4 < 0) goto L_0x01c2;
    L_0x01aa:
        r4 = java.lang.Math.abs(r8);
        r0 = java.lang.Math.abs(r0);
        r0 = (r4 > r0 ? 1 : (r4 == r0 ? 0 : -1));
        if (r0 < 0) goto L_0x01c2;
    L_0x01b6:
        r0 = (r8 > r3 ? 1 : (r8 == r3 ? 0 : -1));
        if (r0 >= 0) goto L_0x01c4;
    L_0x01ba:
        r0 = java.lang.Math.abs(r8);
        r0 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1));
        if (r0 < 0) goto L_0x01c4;
    L_0x01c2:
        r0 = 1;
        goto L_0x01c5;
    L_0x01c4:
        r0 = 0;
    L_0x01c5:
        if (r0 != 0) goto L_0x01d9;
    L_0x01c7:
        r0 = r7.drawerOpened;
        if (r0 != 0) goto L_0x01d4;
    L_0x01cb:
        r8 = java.lang.Math.abs(r8);
        r8 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1));
        if (r8 < 0) goto L_0x01d4;
    L_0x01d3:
        goto L_0x01d5;
    L_0x01d4:
        r2 = 0;
    L_0x01d5:
        r7.openDrawer(r2);
        goto L_0x01ea;
    L_0x01d9:
        r0 = r7.drawerOpened;
        if (r0 == 0) goto L_0x01e6;
    L_0x01dd:
        r8 = java.lang.Math.abs(r8);
        r8 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1));
        if (r8 < 0) goto L_0x01e6;
    L_0x01e5:
        goto L_0x01e7;
    L_0x01e6:
        r2 = 0;
    L_0x01e7:
        r7.closeDrawer(r2);
    L_0x01ea:
        r7.startedTracking = r1;
        r7.maybeStartTracking = r1;
        r8 = r7.velocityTracker;
        if (r8 == 0) goto L_0x01f8;
    L_0x01f2:
        r8.recycle();
        r8 = 0;
        r7.velocityTracker = r8;
    L_0x01f8:
        r8 = r7.startedTracking;
        return r8;
    L_0x01fb:
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.DrawerLayoutContainer.onTouchEvent(android.view.MotionEvent):boolean");
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return this.parentActionBarLayout.checkTransitionAnimation() || onTouchEvent(motionEvent);
    }

    public void requestDisallowInterceptTouchEvent(boolean z) {
        if (this.maybeStartTracking && !this.startedTracking) {
            onTouchEvent(null);
        }
        super.requestDisallowInterceptTouchEvent(z);
    }

    /* Access modifiers changed, original: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        this.inLayout = true;
        int childCount = getChildCount();
        for (i2 = 0; i2 < childCount; i2++) {
            View childAt = getChildAt(i2);
            if (childAt.getVisibility() != 8) {
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                if (!BuildVars.DEBUG_VERSION) {
                    try {
                        if (this.drawerLayout != childAt) {
                            childAt.layout(layoutParams.leftMargin, layoutParams.topMargin + getPaddingTop(), layoutParams.leftMargin + childAt.getMeasuredWidth(), (layoutParams.topMargin + childAt.getMeasuredHeight()) + getPaddingTop());
                        } else {
                            childAt.layout(-childAt.getMeasuredWidth(), layoutParams.topMargin + getPaddingTop(), 0, (layoutParams.topMargin + childAt.getMeasuredHeight()) + getPaddingTop());
                        }
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                } else if (this.drawerLayout != childAt) {
                    childAt.layout(layoutParams.leftMargin, layoutParams.topMargin + getPaddingTop(), layoutParams.leftMargin + childAt.getMeasuredWidth(), (layoutParams.topMargin + childAt.getMeasuredHeight()) + getPaddingTop());
                } else {
                    childAt.layout(-childAt.getMeasuredWidth(), layoutParams.topMargin + getPaddingTop(), 0, (layoutParams.topMargin + childAt.getMeasuredHeight()) + getPaddingTop());
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

    /* Access modifiers changed, original: protected */
    @SuppressLint({"NewApi"})
    public void onMeasure(int i, int i2) {
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
        Object obj = (this.lastInsets == null || VERSION.SDK_INT < 21) ? null : 1;
        int childCount = getChildCount();
        for (int i3 = 0; i3 < childCount; i3++) {
            View childAt = getChildAt(i3);
            if (childAt.getVisibility() != 8) {
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                if (obj != null) {
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
                    childAt.measure(FrameLayout.getChildMeasureSpec(i, (this.minDrawerMargin + layoutParams.leftMargin) + layoutParams.rightMargin, layoutParams.width), FrameLayout.getChildMeasureSpec(i2, layoutParams.topMargin + layoutParams.bottomMargin, layoutParams.height));
                }
            }
        }
    }

    public void setBehindKeyboardColor(int i) {
        this.behindKeyboardColor = i;
        invalidate();
    }

    /* Access modifiers changed, original: protected */
    public boolean drawChild(Canvas canvas, View view, long j) {
        Canvas canvas2 = canvas;
        View view2 = view;
        int i = 0;
        if (!this.allowDrawContent) {
            return false;
        }
        int i2;
        int height = getHeight();
        Object obj = view2 != this.drawerLayout ? 1 : null;
        int width = getWidth();
        int save = canvas.save();
        if (obj != null) {
            int childCount = getChildCount();
            int i3 = 0;
            i2 = 0;
            for (int i4 = 0; i4 < childCount; i4++) {
                View childAt = getChildAt(i4);
                if (childAt.getVisibility() == 0 && childAt != this.drawerLayout) {
                    i2 = i4;
                }
                if (childAt != view2 && childAt.getVisibility() == 0 && childAt == this.drawerLayout && childAt.getHeight() >= height) {
                    int measuredWidth = childAt.getMeasuredWidth() + ((int) childAt.getX());
                    if (measuredWidth > i3) {
                        i3 = measuredWidth;
                    }
                }
            }
            if (i3 != 0) {
                canvas.clipRect(i3, 0, width, getHeight());
            }
            i = i3;
        } else {
            i2 = 0;
        }
        boolean drawChild = super.drawChild(canvas, view, j);
        canvas.restoreToCount(save);
        if (this.scrimOpacity <= 0.0f || obj == null) {
            if (this.shadowLeft != null) {
                float max = Math.max(0.0f, Math.min(this.drawerPosition / ((float) AndroidUtilities.dp(20.0f)), 1.0f));
                if (max != 0.0f) {
                    this.shadowLeft.setBounds((int) this.drawerPosition, view.getTop(), ((int) this.drawerPosition) + this.shadowLeft.getIntrinsicWidth(), view.getBottom());
                    this.shadowLeft.setAlpha((int) (max * 255.0f));
                    this.shadowLeft.draw(canvas);
                }
            }
        } else if (indexOfChild(view2) == i2) {
            this.scrimPaint.setColor(((int) (this.scrimOpacity * 153.0f)) << 24);
            canvas.drawRect((float) i, 0.0f, (float) width, (float) getHeight(), this.scrimPaint);
        }
        return drawChild;
    }

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        if (VERSION.SDK_INT >= 21) {
            Object obj = this.lastInsets;
            if (obj != null) {
                WindowInsets windowInsets = (WindowInsets) obj;
                int systemWindowInsetBottom = windowInsets.getSystemWindowInsetBottom();
                if (systemWindowInsetBottom > 0) {
                    this.backgroundPaint.setColor(this.behindKeyboardColor);
                    canvas.drawRect(0.0f, (float) (getMeasuredHeight() - systemWindowInsetBottom), (float) getMeasuredWidth(), (float) getMeasuredHeight(), this.backgroundPaint);
                }
                if (this.hasCutout) {
                    this.backgroundPaint.setColor(-16777216);
                    systemWindowInsetBottom = windowInsets.getSystemWindowInsetLeft();
                    if (systemWindowInsetBottom != 0) {
                        canvas.drawRect(0.0f, 0.0f, (float) systemWindowInsetBottom, (float) getMeasuredHeight(), this.backgroundPaint);
                    }
                    int systemWindowInsetRight = windowInsets.getSystemWindowInsetRight();
                    if (systemWindowInsetRight != 0) {
                        canvas.drawRect((float) systemWindowInsetRight, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), this.backgroundPaint);
                    }
                }
            }
        }
    }

    public boolean onRequestSendAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent) {
        if (!this.drawerOpened || view == this.drawerLayout) {
            return super.onRequestSendAccessibilityEvent(view, accessibilityEvent);
        }
        return false;
    }
}
