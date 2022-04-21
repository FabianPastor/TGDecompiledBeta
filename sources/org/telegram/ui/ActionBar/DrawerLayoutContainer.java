package org.telegram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.DisplayCutout;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.Utilities;

public class DrawerLayoutContainer extends FrameLayout {
    private static final int MIN_DRAWER_MARGIN = 64;
    private boolean allowDrawContent = true;
    private boolean allowOpenDrawer;
    private boolean allowOpenDrawerBySwipe = true;
    private Paint backgroundPaint = new Paint();
    private boolean beginTrackingSent;
    private int behindKeyboardColor;
    private AnimatorSet currentAnimation;
    private boolean drawCurrentPreviewFragmentAbove;
    private ViewGroup drawerLayout;
    private boolean drawerOpened;
    private float drawerPosition;
    private boolean firstLayout = true;
    private boolean hasCutout;
    private int imeHeight;
    private boolean inLayout;
    private boolean keyboardVisibility;
    private Object lastInsets;
    private boolean maybeStartTracking;
    private int minDrawerMargin = ((int) ((AndroidUtilities.density * 64.0f) + 0.5f));
    private int paddingTop;
    private ActionBarLayout parentActionBarLayout;
    private BitmapDrawable previewBlurDrawable;
    private PreviewForegroundDrawable previewForegroundDrawable;
    private Rect rect = new Rect();
    private float scrimOpacity;
    private Paint scrimPaint = new Paint();
    private Drawable shadowLeft;
    private float startY;
    private boolean startedTracking;
    private int startedTrackingPointerId;
    private int startedTrackingX;
    private int startedTrackingY;
    private VelocityTracker velocityTracker;

    public DrawerLayoutContainer(Context context) {
        super(context);
        setDescendantFocusability(262144);
        setFocusableInTouchMode(true);
        if (Build.VERSION.SDK_INT >= 21) {
            setFitsSystemWindows(true);
            setOnApplyWindowInsetsListener(new DrawerLayoutContainer$$ExternalSyntheticLambda0(this));
            setSystemUiVisibility(1280);
        }
        this.shadowLeft = getResources().getDrawable(NUM);
    }

    /* renamed from: lambda$new$0$org-telegram-ui-ActionBar-DrawerLayoutContainer  reason: not valid java name */
    public /* synthetic */ WindowInsets m1280lambda$new$0$orgtelegramuiActionBarDrawerLayoutContainer(View v, WindowInsets insets) {
        if (Build.VERSION.SDK_INT >= 30) {
            boolean newKeyboardVisibility = insets.isVisible(WindowInsets.Type.ime());
            int imeHeight2 = insets.getInsets(WindowInsets.Type.ime()).bottom;
            if (!(this.keyboardVisibility == newKeyboardVisibility && this.imeHeight == imeHeight2)) {
                this.keyboardVisibility = newKeyboardVisibility;
                this.imeHeight = imeHeight2;
                requestLayout();
            }
        }
        DrawerLayoutContainer drawerLayoutContainer = (DrawerLayoutContainer) v;
        if (AndroidUtilities.statusBarHeight != insets.getSystemWindowInsetTop()) {
            drawerLayoutContainer.requestLayout();
        }
        int newTopInset = insets.getSystemWindowInsetTop();
        if ((newTopInset != 0 || AndroidUtilities.isInMultiwindow || this.firstLayout) && AndroidUtilities.statusBarHeight != newTopInset) {
            AndroidUtilities.statusBarHeight = newTopInset;
        }
        boolean z = false;
        this.firstLayout = false;
        this.lastInsets = insets;
        drawerLayoutContainer.setWillNotDraw(insets.getSystemWindowInsetTop() <= 0 && getBackground() == null);
        if (Build.VERSION.SDK_INT >= 28) {
            DisplayCutout cutout = insets.getDisplayCutout();
            if (!(cutout == null || cutout.getBoundingRects().size() == 0)) {
                z = true;
            }
            this.hasCutout = z;
        }
        invalidate();
        if (Build.VERSION.SDK_INT >= 30) {
            return WindowInsets.CONSUMED;
        }
        return insets.consumeSystemWindowInsets();
    }

    private void dispatchChildInsets(View child, Object insets, int drawerGravity) {
        WindowInsets wi = (WindowInsets) insets;
        if (drawerGravity == 3) {
            wi = wi.replaceSystemWindowInsets(wi.getSystemWindowInsetLeft(), wi.getSystemWindowInsetTop(), 0, wi.getSystemWindowInsetBottom());
        } else if (drawerGravity == 5) {
            wi = wi.replaceSystemWindowInsets(0, wi.getSystemWindowInsetTop(), wi.getSystemWindowInsetRight(), wi.getSystemWindowInsetBottom());
        }
        child.dispatchApplyWindowInsets(wi);
    }

    private void applyMarginInsets(ViewGroup.MarginLayoutParams lp, Object insets, int drawerGravity, boolean topOnly) {
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
        if (Build.VERSION.SDK_INT < 21 || insets == null) {
            return 0;
        }
        return ((WindowInsets) insets).getSystemWindowInsetTop();
    }

    public void setDrawerLayout(ViewGroup layout) {
        this.drawerLayout = layout;
        addView(layout);
        this.drawerLayout.setVisibility(4);
        if (Build.VERSION.SDK_INT >= 21) {
            this.drawerLayout.setFitsSystemWindows(true);
        }
    }

    public void moveDrawerByX(float dx) {
        setDrawerPosition(this.drawerPosition + dx);
    }

    public void setDrawerPosition(float value) {
        ViewGroup viewGroup = this.drawerLayout;
        if (viewGroup != null) {
            this.drawerPosition = value;
            if (value > ((float) viewGroup.getMeasuredWidth())) {
                this.drawerPosition = (float) this.drawerLayout.getMeasuredWidth();
            } else if (this.drawerPosition < 0.0f) {
                this.drawerPosition = 0.0f;
            }
            this.drawerLayout.setTranslationX(this.drawerPosition);
            int newVisibility = this.drawerPosition > 0.0f ? 0 : 4;
            if (this.drawerLayout.getVisibility() != newVisibility) {
                this.drawerLayout.setVisibility(newVisibility);
            }
            if (!this.parentActionBarLayout.fragmentsStack.isEmpty()) {
                BaseFragment currentFragment = this.parentActionBarLayout.fragmentsStack.get(0);
                if (this.drawerPosition == ((float) this.drawerLayout.getMeasuredWidth())) {
                    currentFragment.setProgressToDrawerOpened(1.0f);
                } else {
                    float f = this.drawerPosition;
                    if (f == 0.0f) {
                        currentFragment.setProgressToDrawerOpened(0.0f);
                    } else {
                        currentFragment.setProgressToDrawerOpened(f / ((float) this.drawerLayout.getMeasuredWidth()));
                    }
                }
            }
            setScrimOpacity(this.drawerPosition / ((float) this.drawerLayout.getMeasuredWidth()));
        }
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

    public void openDrawer(boolean fast) {
        ActionBarLayout actionBarLayout;
        if (this.allowOpenDrawer && this.drawerLayout != null) {
            if (!(!AndroidUtilities.isTablet() || (actionBarLayout = this.parentActionBarLayout) == null || actionBarLayout.parentActivity == null)) {
                AndroidUtilities.hideKeyboard(this.parentActionBarLayout.parentActivity.getCurrentFocus());
            }
            cancelCurrentAnimation();
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, "drawerPosition", new float[]{(float) this.drawerLayout.getMeasuredWidth()})});
            animatorSet.setInterpolator(new DecelerateInterpolator());
            if (fast) {
                animatorSet.setDuration((long) Math.max((int) ((200.0f / ((float) this.drawerLayout.getMeasuredWidth())) * (((float) this.drawerLayout.getMeasuredWidth()) - this.drawerPosition)), 50));
            } else {
                animatorSet.setDuration(250);
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

    public void closeDrawer(boolean fast) {
        if (this.drawerLayout != null) {
            cancelCurrentAnimation();
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, "drawerPosition", new float[]{0.0f})});
            animatorSet.setInterpolator(new DecelerateInterpolator());
            if (fast) {
                animatorSet.setDuration((long) Math.max((int) ((200.0f / ((float) this.drawerLayout.getMeasuredWidth())) * this.drawerPosition), 50));
            } else {
                animatorSet.setDuration(250);
            }
            animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    DrawerLayoutContainer.this.onDrawerAnimationEnd(false);
                }
            });
            animatorSet.start();
        }
    }

    /* access modifiers changed from: private */
    public void onDrawerAnimationEnd(boolean opened) {
        this.startedTracking = false;
        this.currentAnimation = null;
        this.drawerOpened = opened;
        if (Build.VERSION.SDK_INT >= 19) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (child != this.drawerLayout) {
                    child.setImportantForAccessibility(opened ? 4 : 0);
                }
            }
        }
        sendAccessibilityEvent(32);
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

    public void presentFragment(BaseFragment fragment) {
        ActionBarLayout actionBarLayout = this.parentActionBarLayout;
        if (actionBarLayout != null) {
            actionBarLayout.presentFragment(fragment);
        }
        closeDrawer(false);
    }

    public void closeDrawer() {
        if (this.drawerPosition != 0.0f) {
            setDrawerPosition(0.0f);
            onDrawerAnimationEnd(false);
        }
    }

    public void setAllowOpenDrawer(boolean value, boolean animated) {
        this.allowOpenDrawer = value;
        if (!value && this.drawerPosition != 0.0f) {
            if (!animated) {
                setDrawerPosition(0.0f);
                onDrawerAnimationEnd(false);
                return;
            }
            closeDrawer(true);
        }
    }

    public boolean isAllowOpenDrawer() {
        return this.allowOpenDrawer;
    }

    public void setAllowOpenDrawerBySwipe(boolean value) {
        this.allowOpenDrawerBySwipe = value;
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

    public boolean isDrawCurrentPreviewFragmentAbove() {
        return this.drawCurrentPreviewFragmentAbove;
    }

    public void setDrawCurrentPreviewFragmentAbove(boolean drawCurrentPreviewFragmentAbove2) {
        if (this.drawCurrentPreviewFragmentAbove != drawCurrentPreviewFragmentAbove2) {
            this.drawCurrentPreviewFragmentAbove = drawCurrentPreviewFragmentAbove2;
            if (drawCurrentPreviewFragmentAbove2) {
                createBlurDrawable();
                this.previewForegroundDrawable = new PreviewForegroundDrawable();
            } else {
                this.startY = 0.0f;
                this.previewBlurDrawable = null;
                this.previewForegroundDrawable = null;
            }
            invalidate();
        }
    }

    private void createBlurDrawable() {
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        int w = (int) (((float) measuredWidth) / 6.0f);
        int h = (int) (((float) measuredHeight) / 6.0f);
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.scale(0.16666667f, 0.16666667f);
        draw(canvas);
        Utilities.stackBlurBitmap(bitmap, Math.max(7, Math.max(w, h) / 180));
        BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
        this.previewBlurDrawable = bitmapDrawable;
        bitmapDrawable.setBounds(0, 0, measuredWidth, measuredHeight);
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!this.drawCurrentPreviewFragmentAbove || this.parentActionBarLayout == null) {
            return super.dispatchTouchEvent(ev);
        }
        int action = ev.getActionMasked();
        if (action == 2) {
            float f = this.startY;
            if (f == 0.0f) {
                this.startY = ev.getY();
                MotionEvent event = MotionEvent.obtain(0, 0, 3, 0.0f, 0.0f, 0);
                super.dispatchTouchEvent(event);
                event.recycle();
            } else {
                this.parentActionBarLayout.movePreviewFragment(f - ev.getY());
            }
        } else if (action == 1 || action == 6 || action == 3) {
            this.parentActionBarLayout.finishPreviewFragment();
        }
        return true;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:96:0x01a0, code lost:
        if (r0 != ((float) r9.drawerLayout.getMeasuredWidth())) goto L_0x01a2;
     */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x020d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r10) {
        /*
            r9 = this;
            android.view.ViewGroup r0 = r9.drawerLayout
            r1 = 0
            if (r0 == 0) goto L_0x0241
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r9.parentActionBarLayout
            boolean r0 = r0.checkTransitionAnimation()
            if (r0 != 0) goto L_0x0241
            boolean r0 = r9.drawerOpened
            r2 = 1
            if (r0 == 0) goto L_0x002c
            if (r10 == 0) goto L_0x002c
            float r0 = r10.getX()
            float r3 = r9.drawerPosition
            int r0 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r0 <= 0) goto L_0x002c
            boolean r0 = r9.startedTracking
            if (r0 != 0) goto L_0x002c
            int r0 = r10.getAction()
            if (r0 != r2) goto L_0x002b
            r9.closeDrawer(r1)
        L_0x002b:
            return r2
        L_0x002c:
            boolean r0 = r9.allowOpenDrawerBySwipe
            r3 = 6
            r4 = 3
            r5 = 0
            if (r0 != 0) goto L_0x0037
            boolean r0 = r9.drawerOpened
            if (r0 == 0) goto L_0x0213
        L_0x0037:
            boolean r0 = r9.allowOpenDrawer
            if (r0 == 0) goto L_0x0213
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r9.parentActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            int r0 = r0.size()
            if (r0 != r2) goto L_0x0213
            r0 = 2
            if (r10 == 0) goto L_0x009e
            int r6 = r10.getAction()
            if (r6 == 0) goto L_0x0054
            int r6 = r10.getAction()
            if (r6 != r0) goto L_0x009e
        L_0x0054:
            boolean r6 = r9.startedTracking
            if (r6 != 0) goto L_0x009e
            boolean r6 = r9.maybeStartTracking
            if (r6 != 0) goto L_0x009e
            float r0 = r10.getX()
            float r3 = r10.getY()
            android.view.View r0 = r9.findScrollingChild(r9, r0, r3)
            if (r0 == 0) goto L_0x006b
            return r1
        L_0x006b:
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r9.parentActionBarLayout
            android.graphics.Rect r4 = r9.rect
            r3.getHitRect(r4)
            float r3 = r10.getX()
            int r3 = (int) r3
            r9.startedTrackingX = r3
            float r3 = r10.getY()
            int r3 = (int) r3
            r9.startedTrackingY = r3
            android.graphics.Rect r4 = r9.rect
            int r5 = r9.startedTrackingX
            boolean r3 = r4.contains(r5, r3)
            if (r3 == 0) goto L_0x009c
            int r1 = r10.getPointerId(r1)
            r9.startedTrackingPointerId = r1
            r9.maybeStartTracking = r2
            r9.cancelCurrentAnimation()
            android.view.VelocityTracker r1 = r9.velocityTracker
            if (r1 == 0) goto L_0x009c
            r1.clear()
        L_0x009c:
            goto L_0x023e
        L_0x009e:
            r6 = 0
            if (r10 == 0) goto L_0x015b
            int r7 = r10.getAction()
            if (r7 != r0) goto L_0x015b
            int r0 = r10.getPointerId(r1)
            int r7 = r9.startedTrackingPointerId
            if (r0 != r7) goto L_0x015b
            android.view.VelocityTracker r0 = r9.velocityTracker
            if (r0 != 0) goto L_0x00b9
            android.view.VelocityTracker r0 = android.view.VelocityTracker.obtain()
            r9.velocityTracker = r0
        L_0x00b9:
            float r0 = r10.getX()
            int r1 = r9.startedTrackingX
            float r1 = (float) r1
            float r0 = r0 - r1
            int r0 = (int) r0
            float r0 = (float) r0
            float r1 = r10.getY()
            int r1 = (int) r1
            int r3 = r9.startedTrackingY
            int r1 = r1 - r3
            int r1 = java.lang.Math.abs(r1)
            float r1 = (float) r1
            android.view.VelocityTracker r3 = r9.velocityTracker
            r3.addMovement(r10)
            boolean r3 = r9.maybeStartTracking
            if (r3 == 0) goto L_0x012d
            boolean r3 = r9.startedTracking
            if (r3 != 0) goto L_0x012d
            int r3 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
            if (r3 <= 0) goto L_0x00fc
            r3 = 1077936128(0x40400000, float:3.0)
            float r3 = r0 / r3
            float r4 = java.lang.Math.abs(r1)
            int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r3 <= 0) goto L_0x00fc
            float r3 = java.lang.Math.abs(r0)
            r4 = 1045220557(0x3e4ccccd, float:0.2)
            float r4 = org.telegram.messenger.AndroidUtilities.getPixelsInCM(r4, r2)
            int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r3 >= 0) goto L_0x011f
        L_0x00fc:
            boolean r3 = r9.drawerOpened
            if (r3 == 0) goto L_0x012d
            int r3 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
            if (r3 >= 0) goto L_0x012d
            float r3 = java.lang.Math.abs(r0)
            float r4 = java.lang.Math.abs(r1)
            int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r3 < 0) goto L_0x012d
            float r3 = java.lang.Math.abs(r0)
            r4 = 1053609165(0x3ecccccd, float:0.4)
            float r4 = org.telegram.messenger.AndroidUtilities.getPixelsInCM(r4, r2)
            int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r3 < 0) goto L_0x012d
        L_0x011f:
            r9.prepareForDrawerOpen(r10)
            float r3 = r10.getX()
            int r3 = (int) r3
            r9.startedTrackingX = r3
            r9.requestDisallowInterceptTouchEvent(r2)
            goto L_0x017a
        L_0x012d:
            boolean r3 = r9.startedTracking
            if (r3 == 0) goto L_0x017a
            boolean r3 = r9.beginTrackingSent
            if (r3 != 0) goto L_0x0150
            android.content.Context r3 = r9.getContext()
            android.app.Activity r3 = (android.app.Activity) r3
            android.view.View r3 = r3.getCurrentFocus()
            if (r3 == 0) goto L_0x014e
            android.content.Context r3 = r9.getContext()
            android.app.Activity r3 = (android.app.Activity) r3
            android.view.View r3 = r3.getCurrentFocus()
            org.telegram.messenger.AndroidUtilities.hideKeyboard(r3)
        L_0x014e:
            r9.beginTrackingSent = r2
        L_0x0150:
            r9.moveDrawerByX(r0)
            float r2 = r10.getX()
            int r2 = (int) r2
            r9.startedTrackingX = r2
            goto L_0x017a
        L_0x015b:
            if (r10 == 0) goto L_0x017c
            if (r10 == 0) goto L_0x017a
            int r0 = r10.getPointerId(r1)
            int r7 = r9.startedTrackingPointerId
            if (r0 != r7) goto L_0x017a
            int r0 = r10.getAction()
            if (r0 == r4) goto L_0x017c
            int r0 = r10.getAction()
            if (r0 == r2) goto L_0x017c
            int r0 = r10.getAction()
            if (r0 != r3) goto L_0x017a
            goto L_0x017c
        L_0x017a:
            goto L_0x023e
        L_0x017c:
            android.view.VelocityTracker r0 = r9.velocityTracker
            if (r0 != 0) goto L_0x0186
            android.view.VelocityTracker r0 = android.view.VelocityTracker.obtain()
            r9.velocityTracker = r0
        L_0x0186:
            android.view.VelocityTracker r0 = r9.velocityTracker
            r3 = 1000(0x3e8, float:1.401E-42)
            r0.computeCurrentVelocity(r3)
            boolean r0 = r9.startedTracking
            if (r0 != 0) goto L_0x01a2
            float r0 = r9.drawerPosition
            int r3 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
            if (r3 == 0) goto L_0x0205
            android.view.ViewGroup r3 = r9.drawerLayout
            int r3 = r3.getMeasuredWidth()
            float r3 = (float) r3
            int r0 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r0 == 0) goto L_0x0205
        L_0x01a2:
            android.view.VelocityTracker r0 = r9.velocityTracker
            float r0 = r0.getXVelocity()
            android.view.VelocityTracker r3 = r9.velocityTracker
            float r3 = r3.getYVelocity()
            float r4 = r9.drawerPosition
            android.view.ViewGroup r7 = r9.drawerLayout
            int r7 = r7.getMeasuredWidth()
            float r7 = (float) r7
            r8 = 1073741824(0x40000000, float:2.0)
            float r7 = r7 / r8
            r8 = 1163575296(0x455aCLASSNAME, float:3500.0)
            int r4 = (r4 > r7 ? 1 : (r4 == r7 ? 0 : -1))
            if (r4 >= 0) goto L_0x01d1
            int r4 = (r0 > r8 ? 1 : (r0 == r8 ? 0 : -1))
            if (r4 < 0) goto L_0x01dd
            float r4 = java.lang.Math.abs(r0)
            float r7 = java.lang.Math.abs(r3)
            int r4 = (r4 > r7 ? 1 : (r4 == r7 ? 0 : -1))
            if (r4 < 0) goto L_0x01dd
        L_0x01d1:
            int r4 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
            if (r4 >= 0) goto L_0x01df
            float r4 = java.lang.Math.abs(r0)
            int r4 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1))
            if (r4 < 0) goto L_0x01df
        L_0x01dd:
            r4 = 1
            goto L_0x01e0
        L_0x01df:
            r4 = 0
        L_0x01e0:
            if (r4 != 0) goto L_0x01f4
            boolean r6 = r9.drawerOpened
            if (r6 != 0) goto L_0x01ef
            float r6 = java.lang.Math.abs(r0)
            int r6 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r6 < 0) goto L_0x01ef
            goto L_0x01f0
        L_0x01ef:
            r2 = 0
        L_0x01f0:
            r9.openDrawer(r2)
            goto L_0x0205
        L_0x01f4:
            boolean r6 = r9.drawerOpened
            if (r6 == 0) goto L_0x0201
            float r6 = java.lang.Math.abs(r0)
            int r6 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r6 < 0) goto L_0x0201
            goto L_0x0202
        L_0x0201:
            r2 = 0
        L_0x0202:
            r9.closeDrawer(r2)
        L_0x0205:
            r9.startedTracking = r1
            r9.maybeStartTracking = r1
            android.view.VelocityTracker r0 = r9.velocityTracker
            if (r0 == 0) goto L_0x023e
            r0.recycle()
            r9.velocityTracker = r5
            goto L_0x023e
        L_0x0213:
            if (r10 == 0) goto L_0x0231
            if (r10 == 0) goto L_0x023e
            int r0 = r10.getPointerId(r1)
            int r6 = r9.startedTrackingPointerId
            if (r0 != r6) goto L_0x023e
            int r0 = r10.getAction()
            if (r0 == r4) goto L_0x0231
            int r0 = r10.getAction()
            if (r0 == r2) goto L_0x0231
            int r0 = r10.getAction()
            if (r0 != r3) goto L_0x023e
        L_0x0231:
            r9.startedTracking = r1
            r9.maybeStartTracking = r1
            android.view.VelocityTracker r0 = r9.velocityTracker
            if (r0 == 0) goto L_0x023e
            r0.recycle()
            r9.velocityTracker = r5
        L_0x023e:
            boolean r0 = r9.startedTracking
            return r0
        L_0x0241:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.DrawerLayoutContainer.onTouchEvent(android.view.MotionEvent):boolean");
    }

    private View findScrollingChild(ViewGroup parent, float x, float y) {
        View v;
        int n = parent.getChildCount();
        for (int i = 0; i < n; i++) {
            View child = parent.getChildAt(i);
            if (child.getVisibility() == 0) {
                child.getHitRect(this.rect);
                if (!this.rect.contains((int) x, (int) y)) {
                    continue;
                } else if (child.canScrollHorizontally(-1)) {
                    return child;
                } else {
                    if ((child instanceof ViewGroup) && (v = findScrollingChild((ViewGroup) child, x - ((float) this.rect.left), y - ((float) this.rect.top))) != null) {
                        return v;
                    }
                }
            }
        }
        return null;
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return this.parentActionBarLayout.checkTransitionAnimation() || onTouchEvent(ev);
    }

    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        if (this.maybeStartTracking && !this.startedTracking) {
            onTouchEvent((MotionEvent) null);
        }
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        this.inLayout = true;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != 8) {
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();
                if (!BuildVars.DEBUG_VERSION) {
                    try {
                        if (this.drawerLayout != child) {
                            child.layout(lp.leftMargin, lp.topMargin + getPaddingTop(), lp.leftMargin + child.getMeasuredWidth(), lp.topMargin + child.getMeasuredHeight() + getPaddingTop());
                        } else {
                            child.layout(-child.getMeasuredWidth(), lp.topMargin + getPaddingTop(), 0, lp.topMargin + child.getMeasuredHeight() + getPaddingTop());
                        }
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                } else if (this.drawerLayout != child) {
                    child.layout(lp.leftMargin, lp.topMargin + getPaddingTop(), lp.leftMargin + child.getMeasuredWidth(), lp.topMargin + child.getMeasuredHeight() + getPaddingTop());
                } else {
                    child.layout(-child.getMeasuredWidth(), lp.topMargin + getPaddingTop(), 0, lp.topMargin + child.getMeasuredHeight() + getPaddingTop());
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

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int contentHeightSpec;
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(widthSize, heightSize);
        if (Build.VERSION.SDK_INT < 21) {
            this.inLayout = true;
            if (heightSize == AndroidUtilities.displaySize.y + AndroidUtilities.statusBarHeight) {
                if (getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    setPadding(0, AndroidUtilities.statusBarHeight, 0, 0);
                }
                heightSize = AndroidUtilities.displaySize.y;
            } else if (getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                setPadding(0, 0, 0, 0);
            }
            this.inLayout = false;
        } else {
            int newSize = heightSize - AndroidUtilities.statusBarHeight;
            if (newSize > 0 && newSize < 4096) {
                AndroidUtilities.displaySize.y = newSize;
            }
        }
        boolean applyInsets = this.lastInsets != null && Build.VERSION.SDK_INT >= 21;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == 8) {
                int i2 = widthMeasureSpec;
                int i3 = heightMeasureSpec;
            } else {
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();
                if (applyInsets) {
                    if (child.getFitsSystemWindows()) {
                        dispatchChildInsets(child, this.lastInsets, lp.gravity);
                    } else if (child.getTag() == null) {
                        applyMarginInsets(lp, this.lastInsets, lp.gravity, Build.VERSION.SDK_INT >= 21);
                    }
                }
                if (this.drawerLayout != child) {
                    int contentWidthSpec = View.MeasureSpec.makeMeasureSpec((widthSize - lp.leftMargin) - lp.rightMargin, NUM);
                    if (lp.height > 0) {
                        contentHeightSpec = lp.height;
                    } else {
                        contentHeightSpec = View.MeasureSpec.makeMeasureSpec((heightSize - lp.topMargin) - lp.bottomMargin, NUM);
                    }
                    child.measure(contentWidthSpec, contentHeightSpec);
                    int i4 = widthMeasureSpec;
                    int i5 = heightMeasureSpec;
                } else {
                    child.setPadding(0, 0, 0, 0);
                    child.measure(getChildMeasureSpec(widthMeasureSpec, this.minDrawerMargin + lp.leftMargin + lp.rightMargin, lp.width), getChildMeasureSpec(heightMeasureSpec, lp.topMargin + lp.bottomMargin, lp.height));
                }
            }
        }
        int i6 = widthMeasureSpec;
        int i7 = heightMeasureSpec;
    }

    public void setBehindKeyboardColor(int color) {
        this.behindKeyboardColor = color;
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        ActionBarLayout actionBarLayout;
        super.dispatchDraw(canvas);
        if (this.drawCurrentPreviewFragmentAbove && (actionBarLayout = this.parentActionBarLayout) != null) {
            BitmapDrawable bitmapDrawable = this.previewBlurDrawable;
            if (bitmapDrawable != null) {
                bitmapDrawable.setAlpha((int) (actionBarLayout.getCurrentPreviewFragmentAlpha() * 255.0f));
                this.previewBlurDrawable.draw(canvas);
            }
            this.parentActionBarLayout.drawCurrentPreviewFragment(canvas, Build.VERSION.SDK_INT >= 21 ? this.previewForegroundDrawable : null);
        }
    }

    /* access modifiers changed from: protected */
    public boolean drawChild(Canvas canvas, View child, long drawingTime) {
        int clipLeft;
        int lastVisibleChild;
        int vright;
        Canvas canvas2 = canvas;
        View view = child;
        if (!this.allowDrawContent) {
            return false;
        }
        int height = getHeight();
        boolean drawingContent = view != this.drawerLayout;
        int lastVisibleChild2 = 0;
        int clipLeft2 = 0;
        int clipRight = getWidth();
        int restoreCount = canvas.save();
        if (drawingContent) {
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View v = getChildAt(i);
                if (v.getVisibility() == 0 && v != this.drawerLayout) {
                    lastVisibleChild2 = i;
                }
                if (v != view && v.getVisibility() == 0 && v == this.drawerLayout && v.getHeight() >= height && (vright = ((int) Math.ceil((double) v.getX())) + v.getMeasuredWidth()) > clipLeft2) {
                    clipLeft2 = vright;
                }
            }
            if (clipLeft2 != 0) {
                canvas2.clipRect(clipLeft2 - AndroidUtilities.dp(1.0f), 0, clipRight, getHeight());
            }
            lastVisibleChild = lastVisibleChild2;
            clipLeft = clipLeft2;
        } else {
            lastVisibleChild = 0;
            clipLeft = 0;
        }
        boolean result = super.drawChild(canvas, child, drawingTime);
        canvas2.restoreToCount(restoreCount);
        if (this.scrimOpacity <= 0.0f || !drawingContent) {
            if (this.shadowLeft != null) {
                float alpha = Math.max(0.0f, Math.min(this.drawerPosition / ((float) AndroidUtilities.dp(20.0f)), 1.0f));
                if (alpha != 0.0f) {
                    this.shadowLeft.setBounds((int) this.drawerPosition, child.getTop(), ((int) this.drawerPosition) + this.shadowLeft.getIntrinsicWidth(), child.getBottom());
                    this.shadowLeft.setAlpha((int) (255.0f * alpha));
                    this.shadowLeft.draw(canvas2);
                }
            }
        } else if (indexOfChild(view) == lastVisibleChild) {
            this.scrimPaint.setColor(((int) (this.scrimOpacity * 153.0f)) << 24);
            canvas.drawRect((float) clipLeft, 0.0f, (float) clipRight, (float) getHeight(), this.scrimPaint);
        }
        return result;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        Object obj;
        if (Build.VERSION.SDK_INT >= 21 && (obj = this.lastInsets) != null) {
            WindowInsets insets = (WindowInsets) obj;
            int bottomInset = insets.getSystemWindowInsetBottom();
            if (bottomInset > 0) {
                this.backgroundPaint.setColor(this.behindKeyboardColor);
                canvas.drawRect(0.0f, (float) (getMeasuredHeight() - bottomInset), (float) getMeasuredWidth(), (float) getMeasuredHeight(), this.backgroundPaint);
            }
            if (this.hasCutout) {
                this.backgroundPaint.setColor(-16777216);
                int left = insets.getSystemWindowInsetLeft();
                if (left != 0) {
                    canvas.drawRect(0.0f, 0.0f, (float) left, (float) getMeasuredHeight(), this.backgroundPaint);
                }
                int right = insets.getSystemWindowInsetRight();
                if (right != 0) {
                    canvas.drawRect((float) right, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), this.backgroundPaint);
                }
            }
        }
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    public boolean onRequestSendAccessibilityEvent(View child, AccessibilityEvent event) {
        if (!this.drawerOpened || child == this.drawerLayout) {
            return super.onRequestSendAccessibilityEvent(child, event);
        }
        return false;
    }

    private static class PreviewForegroundDrawable extends Drawable {
        private final GradientDrawable bottomDrawable;
        private final GradientDrawable topDrawable;

        public PreviewForegroundDrawable() {
            GradientDrawable gradientDrawable = new GradientDrawable();
            this.topDrawable = gradientDrawable;
            gradientDrawable.setStroke(AndroidUtilities.dp(1.0f), Theme.getColor("actionBarDefault"));
            gradientDrawable.setCornerRadius((float) AndroidUtilities.dp(6.0f));
            GradientDrawable gradientDrawable2 = new GradientDrawable();
            this.bottomDrawable = gradientDrawable2;
            gradientDrawable2.setStroke(1, Theme.getColor("divider"));
            gradientDrawable2.setCornerRadius((float) AndroidUtilities.dp(6.0f));
        }

        public void draw(Canvas canvas) {
            Rect bounds = getBounds();
            canvas.save();
            canvas.clipRect(bounds.left, bounds.top, bounds.right, bounds.top + ActionBar.getCurrentActionBarHeight());
            this.topDrawable.draw(canvas);
            canvas.restore();
            canvas.save();
            canvas.clipRect(bounds.left, bounds.top + ActionBar.getCurrentActionBarHeight(), bounds.right, bounds.bottom);
            this.bottomDrawable.draw(canvas);
            canvas.restore();
        }

        /* access modifiers changed from: protected */
        public void onBoundsChange(Rect bounds) {
            super.onBoundsChange(bounds);
            this.topDrawable.setBounds(bounds);
            this.bottomDrawable.setBounds(bounds);
        }

        public void setAlpha(int i) {
            this.topDrawable.setAlpha(i);
            this.bottomDrawable.setAlpha(i);
        }

        public void setColorFilter(ColorFilter colorFilter) {
        }

        public int getOpacity() {
            return -3;
        }
    }
}
