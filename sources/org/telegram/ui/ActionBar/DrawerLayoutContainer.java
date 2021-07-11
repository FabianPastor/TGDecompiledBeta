package org.telegram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
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
import androidx.annotation.Keep;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.Utilities;

public class DrawerLayoutContainer extends FrameLayout {
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
    private boolean inLayout;
    private Object lastInsets;
    private boolean maybeStartTracking;
    private int minDrawerMargin = ((int) ((AndroidUtilities.density * 64.0f) + 0.5f));
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

    public boolean hasOverlappingRendering() {
        return false;
    }

    public DrawerLayoutContainer(Context context) {
        super(context);
        setDescendantFocusability(262144);
        setFocusableInTouchMode(true);
        if (Build.VERSION.SDK_INT >= 21) {
            setFitsSystemWindows(true);
            setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                public final WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
                    return DrawerLayoutContainer.this.lambda$new$0$DrawerLayoutContainer(view, windowInsets);
                }
            });
            setSystemUiVisibility(1280);
        }
        this.shadowLeft = getResources().getDrawable(NUM);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ WindowInsets lambda$new$0$DrawerLayoutContainer(View view, WindowInsets windowInsets) {
        DrawerLayoutContainer drawerLayoutContainer = (DrawerLayoutContainer) view;
        if (AndroidUtilities.statusBarHeight != windowInsets.getSystemWindowInsetTop()) {
            drawerLayoutContainer.requestLayout();
        }
        int systemWindowInsetTop = windowInsets.getSystemWindowInsetTop();
        if ((systemWindowInsetTop != 0 || AndroidUtilities.isInMultiwindow || this.firstLayout) && AndroidUtilities.statusBarHeight != systemWindowInsetTop) {
            AndroidUtilities.statusBarHeight = systemWindowInsetTop;
        }
        boolean z = false;
        this.firstLayout = false;
        this.lastInsets = windowInsets;
        drawerLayoutContainer.setWillNotDraw(windowInsets.getSystemWindowInsetTop() <= 0 && getBackground() == null);
        int i = Build.VERSION.SDK_INT;
        if (i >= 28) {
            DisplayCutout displayCutout = windowInsets.getDisplayCutout();
            if (!(displayCutout == null || displayCutout.getBoundingRects().size() == 0)) {
                z = true;
            }
            this.hasCutout = z;
        }
        invalidate();
        if (i >= 30) {
            return WindowInsets.CONSUMED;
        }
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
    private void applyMarginInsets(ViewGroup.MarginLayoutParams marginLayoutParams, Object obj, int i, boolean z) {
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

    public void setDrawerLayout(ViewGroup viewGroup) {
        this.drawerLayout = viewGroup;
        addView(viewGroup);
        this.drawerLayout.setVisibility(4);
        if (Build.VERSION.SDK_INT >= 21) {
            this.drawerLayout.setFitsSystemWindows(true);
        }
    }

    public void moveDrawerByX(float f) {
        setDrawerPosition(this.drawerPosition + f);
    }

    @Keep
    public void setDrawerPosition(float f) {
        ViewGroup viewGroup = this.drawerLayout;
        if (viewGroup != null) {
            this.drawerPosition = f;
            if (f > ((float) viewGroup.getMeasuredWidth())) {
                this.drawerPosition = (float) this.drawerLayout.getMeasuredWidth();
            } else if (this.drawerPosition < 0.0f) {
                this.drawerPosition = 0.0f;
            }
            this.drawerLayout.setTranslationX(this.drawerPosition);
            int i = this.drawerPosition > 0.0f ? 0 : 4;
            if (this.drawerLayout.getVisibility() != i) {
                this.drawerLayout.setVisibility(i);
            }
            if (!this.parentActionBarLayout.fragmentsStack.isEmpty()) {
                BaseFragment baseFragment = this.parentActionBarLayout.fragmentsStack.get(0);
                if (this.drawerPosition == ((float) this.drawerLayout.getMeasuredWidth())) {
                    baseFragment.setProgressToDrawerOpened(1.0f);
                } else {
                    float f2 = this.drawerPosition;
                    if (f2 == 0.0f) {
                        baseFragment.setProgressToDrawerOpened(0.0f);
                    } else {
                        baseFragment.setProgressToDrawerOpened(f2 / ((float) this.drawerLayout.getMeasuredWidth()));
                    }
                }
            }
            setScrimOpacity(this.drawerPosition / ((float) this.drawerLayout.getMeasuredWidth()));
        }
    }

    @Keep
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
        ActionBarLayout actionBarLayout;
        Activity activity;
        if (this.allowOpenDrawer && this.drawerLayout != null) {
            if (!(!AndroidUtilities.isTablet() || (actionBarLayout = this.parentActionBarLayout) == null || (activity = actionBarLayout.parentActivity) == null)) {
                AndroidUtilities.hideKeyboard(activity.getCurrentFocus());
            }
            cancelCurrentAnimation();
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, "drawerPosition", new float[]{(float) this.drawerLayout.getMeasuredWidth()})});
            animatorSet.setInterpolator(new DecelerateInterpolator());
            if (z) {
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

    public void closeDrawer(boolean z) {
        if (this.drawerLayout != null) {
            cancelCurrentAnimation();
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, "drawerPosition", new float[]{0.0f})});
            animatorSet.setInterpolator(new DecelerateInterpolator());
            if (z) {
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
    public void onDrawerAnimationEnd(boolean z) {
        this.startedTracking = false;
        this.currentAnimation = null;
        this.drawerOpened = z;
        if (Build.VERSION.SDK_INT >= 19) {
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

    public void closeDrawer() {
        if (this.drawerPosition != 0.0f) {
            setDrawerPosition(0.0f);
            onDrawerAnimationEnd(false);
        }
    }

    public void setAllowOpenDrawer(boolean z, boolean z2) {
        this.allowOpenDrawer = z;
        if (!z && this.drawerPosition != 0.0f) {
            if (!z2) {
                setDrawerPosition(0.0f);
                onDrawerAnimationEnd(false);
                return;
            }
            closeDrawer(true);
        }
    }

    public void setAllowOpenDrawerBySwipe(boolean z) {
        this.allowOpenDrawerBySwipe = z;
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

    public boolean isDrawCurrentPreviewFragmentAbove() {
        return this.drawCurrentPreviewFragmentAbove;
    }

    public void setDrawCurrentPreviewFragmentAbove(boolean z) {
        if (this.drawCurrentPreviewFragmentAbove != z) {
            this.drawCurrentPreviewFragmentAbove = z;
            if (z) {
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
        int i = (int) (((float) measuredWidth) / 6.0f);
        int i2 = (int) (((float) measuredHeight) / 6.0f);
        Bitmap createBitmap = Bitmap.createBitmap(i, i2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        canvas.scale(0.16666667f, 0.16666667f);
        draw(canvas);
        Utilities.stackBlurBitmap(createBitmap, Math.max(7, Math.max(i, i2) / 180));
        BitmapDrawable bitmapDrawable = new BitmapDrawable(createBitmap);
        this.previewBlurDrawable = bitmapDrawable;
        bitmapDrawable.setBounds(0, 0, measuredWidth, measuredHeight);
    }

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (!this.drawCurrentPreviewFragmentAbove || this.parentActionBarLayout == null) {
            return super.dispatchTouchEvent(motionEvent);
        }
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 2) {
            float f = this.startY;
            if (f == 0.0f) {
                this.startY = motionEvent.getY();
                MotionEvent obtain = MotionEvent.obtain(0, 0, 3, 0.0f, 0.0f, 0);
                super.dispatchTouchEvent(obtain);
                obtain.recycle();
            } else {
                this.parentActionBarLayout.movePreviewFragment(f - motionEvent.getY());
            }
        } else if (actionMasked == 1 || actionMasked == 6 || actionMasked == 3) {
            this.parentActionBarLayout.finishPreviewFragment();
        }
        return true;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:95:0x019d, code lost:
        if (r9 != ((float) r8.drawerLayout.getMeasuredWidth())) goto L_0x019f;
     */
    /* JADX WARNING: Removed duplicated region for block: B:123:0x020a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r9) {
        /*
            r8 = this;
            android.view.ViewGroup r0 = r8.drawerLayout
            r1 = 0
            if (r0 == 0) goto L_0x023c
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r8.parentActionBarLayout
            boolean r0 = r0.checkTransitionAnimation()
            if (r0 != 0) goto L_0x023c
            boolean r0 = r8.drawerOpened
            r2 = 1
            if (r0 == 0) goto L_0x002c
            if (r9 == 0) goto L_0x002c
            float r0 = r9.getX()
            float r3 = r8.drawerPosition
            int r0 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r0 <= 0) goto L_0x002c
            boolean r0 = r8.startedTracking
            if (r0 != 0) goto L_0x002c
            int r9 = r9.getAction()
            if (r9 != r2) goto L_0x002b
            r8.closeDrawer(r1)
        L_0x002b:
            return r2
        L_0x002c:
            boolean r0 = r8.allowOpenDrawerBySwipe
            r3 = 6
            r4 = 3
            r5 = 0
            if (r0 != 0) goto L_0x0037
            boolean r0 = r8.drawerOpened
            if (r0 == 0) goto L_0x0210
        L_0x0037:
            boolean r0 = r8.allowOpenDrawer
            if (r0 == 0) goto L_0x0210
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r8.parentActionBarLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            int r0 = r0.size()
            if (r0 != r2) goto L_0x0210
            r0 = 2
            if (r9 == 0) goto L_0x009e
            int r6 = r9.getAction()
            if (r6 == 0) goto L_0x0054
            int r6 = r9.getAction()
            if (r6 != r0) goto L_0x009e
        L_0x0054:
            boolean r6 = r8.startedTracking
            if (r6 != 0) goto L_0x009e
            boolean r6 = r8.maybeStartTracking
            if (r6 != 0) goto L_0x009e
            float r0 = r9.getX()
            float r3 = r9.getY()
            android.view.View r0 = r8.findScrollingChild(r8, r0, r3)
            if (r0 == 0) goto L_0x006b
            return r1
        L_0x006b:
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r8.parentActionBarLayout
            android.graphics.Rect r3 = r8.rect
            r0.getHitRect(r3)
            float r0 = r9.getX()
            int r0 = (int) r0
            r8.startedTrackingX = r0
            float r0 = r9.getY()
            int r0 = (int) r0
            r8.startedTrackingY = r0
            android.graphics.Rect r3 = r8.rect
            int r4 = r8.startedTrackingX
            boolean r0 = r3.contains(r4, r0)
            if (r0 == 0) goto L_0x0239
            int r9 = r9.getPointerId(r1)
            r8.startedTrackingPointerId = r9
            r8.maybeStartTracking = r2
            r8.cancelCurrentAnimation()
            android.view.VelocityTracker r9 = r8.velocityTracker
            if (r9 == 0) goto L_0x0239
            r9.clear()
            goto L_0x0239
        L_0x009e:
            r6 = 0
            if (r9 == 0) goto L_0x015d
            int r7 = r9.getAction()
            if (r7 != r0) goto L_0x015d
            int r0 = r9.getPointerId(r1)
            int r7 = r8.startedTrackingPointerId
            if (r0 != r7) goto L_0x015d
            android.view.VelocityTracker r0 = r8.velocityTracker
            if (r0 != 0) goto L_0x00b9
            android.view.VelocityTracker r0 = android.view.VelocityTracker.obtain()
            r8.velocityTracker = r0
        L_0x00b9:
            float r0 = r9.getX()
            int r1 = r8.startedTrackingX
            float r1 = (float) r1
            float r0 = r0 - r1
            int r0 = (int) r0
            float r0 = (float) r0
            float r1 = r9.getY()
            int r1 = (int) r1
            int r3 = r8.startedTrackingY
            int r1 = r1 - r3
            int r1 = java.lang.Math.abs(r1)
            float r1 = (float) r1
            android.view.VelocityTracker r3 = r8.velocityTracker
            r3.addMovement(r9)
            boolean r3 = r8.maybeStartTracking
            if (r3 == 0) goto L_0x012e
            boolean r3 = r8.startedTracking
            if (r3 != 0) goto L_0x012e
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
            boolean r3 = r8.drawerOpened
            if (r3 == 0) goto L_0x012e
            int r3 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
            if (r3 >= 0) goto L_0x012e
            float r3 = java.lang.Math.abs(r0)
            float r1 = java.lang.Math.abs(r1)
            int r1 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r1 < 0) goto L_0x012e
            float r1 = java.lang.Math.abs(r0)
            r3 = 1053609165(0x3ecccccd, float:0.4)
            float r3 = org.telegram.messenger.AndroidUtilities.getPixelsInCM(r3, r2)
            int r1 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r1 < 0) goto L_0x012e
        L_0x011f:
            r8.prepareForDrawerOpen(r9)
            float r9 = r9.getX()
            int r9 = (int) r9
            r8.startedTrackingX = r9
            r8.requestDisallowInterceptTouchEvent(r2)
            goto L_0x0239
        L_0x012e:
            boolean r1 = r8.startedTracking
            if (r1 == 0) goto L_0x0239
            boolean r1 = r8.beginTrackingSent
            if (r1 != 0) goto L_0x0151
            android.content.Context r1 = r8.getContext()
            android.app.Activity r1 = (android.app.Activity) r1
            android.view.View r1 = r1.getCurrentFocus()
            if (r1 == 0) goto L_0x014f
            android.content.Context r1 = r8.getContext()
            android.app.Activity r1 = (android.app.Activity) r1
            android.view.View r1 = r1.getCurrentFocus()
            org.telegram.messenger.AndroidUtilities.hideKeyboard(r1)
        L_0x014f:
            r8.beginTrackingSent = r2
        L_0x0151:
            r8.moveDrawerByX(r0)
            float r9 = r9.getX()
            int r9 = (int) r9
            r8.startedTrackingX = r9
            goto L_0x0239
        L_0x015d:
            if (r9 == 0) goto L_0x0179
            int r0 = r9.getPointerId(r1)
            int r7 = r8.startedTrackingPointerId
            if (r0 != r7) goto L_0x0239
            int r0 = r9.getAction()
            if (r0 == r4) goto L_0x0179
            int r0 = r9.getAction()
            if (r0 == r2) goto L_0x0179
            int r9 = r9.getAction()
            if (r9 != r3) goto L_0x0239
        L_0x0179:
            android.view.VelocityTracker r9 = r8.velocityTracker
            if (r9 != 0) goto L_0x0183
            android.view.VelocityTracker r9 = android.view.VelocityTracker.obtain()
            r8.velocityTracker = r9
        L_0x0183:
            android.view.VelocityTracker r9 = r8.velocityTracker
            r0 = 1000(0x3e8, float:1.401E-42)
            r9.computeCurrentVelocity(r0)
            boolean r9 = r8.startedTracking
            if (r9 != 0) goto L_0x019f
            float r9 = r8.drawerPosition
            int r0 = (r9 > r6 ? 1 : (r9 == r6 ? 0 : -1))
            if (r0 == 0) goto L_0x0202
            android.view.ViewGroup r0 = r8.drawerLayout
            int r0 = r0.getMeasuredWidth()
            float r0 = (float) r0
            int r9 = (r9 > r0 ? 1 : (r9 == r0 ? 0 : -1))
            if (r9 == 0) goto L_0x0202
        L_0x019f:
            android.view.VelocityTracker r9 = r8.velocityTracker
            float r9 = r9.getXVelocity()
            android.view.VelocityTracker r0 = r8.velocityTracker
            float r0 = r0.getYVelocity()
            float r3 = r8.drawerPosition
            android.view.ViewGroup r4 = r8.drawerLayout
            int r4 = r4.getMeasuredWidth()
            float r4 = (float) r4
            r7 = 1073741824(0x40000000, float:2.0)
            float r4 = r4 / r7
            r7 = 1163575296(0x455aCLASSNAME, float:3500.0)
            int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r3 >= 0) goto L_0x01ce
            int r3 = (r9 > r7 ? 1 : (r9 == r7 ? 0 : -1))
            if (r3 < 0) goto L_0x01da
            float r3 = java.lang.Math.abs(r9)
            float r0 = java.lang.Math.abs(r0)
            int r0 = (r3 > r0 ? 1 : (r3 == r0 ? 0 : -1))
            if (r0 < 0) goto L_0x01da
        L_0x01ce:
            int r0 = (r9 > r6 ? 1 : (r9 == r6 ? 0 : -1))
            if (r0 >= 0) goto L_0x01dc
            float r0 = java.lang.Math.abs(r9)
            int r0 = (r0 > r7 ? 1 : (r0 == r7 ? 0 : -1))
            if (r0 < 0) goto L_0x01dc
        L_0x01da:
            r0 = 1
            goto L_0x01dd
        L_0x01dc:
            r0 = 0
        L_0x01dd:
            if (r0 != 0) goto L_0x01f1
            boolean r0 = r8.drawerOpened
            if (r0 != 0) goto L_0x01ec
            float r9 = java.lang.Math.abs(r9)
            int r9 = (r9 > r7 ? 1 : (r9 == r7 ? 0 : -1))
            if (r9 < 0) goto L_0x01ec
            goto L_0x01ed
        L_0x01ec:
            r2 = 0
        L_0x01ed:
            r8.openDrawer(r2)
            goto L_0x0202
        L_0x01f1:
            boolean r0 = r8.drawerOpened
            if (r0 == 0) goto L_0x01fe
            float r9 = java.lang.Math.abs(r9)
            int r9 = (r9 > r7 ? 1 : (r9 == r7 ? 0 : -1))
            if (r9 < 0) goto L_0x01fe
            goto L_0x01ff
        L_0x01fe:
            r2 = 0
        L_0x01ff:
            r8.closeDrawer(r2)
        L_0x0202:
            r8.startedTracking = r1
            r8.maybeStartTracking = r1
            android.view.VelocityTracker r9 = r8.velocityTracker
            if (r9 == 0) goto L_0x0239
            r9.recycle()
            r8.velocityTracker = r5
            goto L_0x0239
        L_0x0210:
            if (r9 == 0) goto L_0x022c
            int r0 = r9.getPointerId(r1)
            int r6 = r8.startedTrackingPointerId
            if (r0 != r6) goto L_0x0239
            int r0 = r9.getAction()
            if (r0 == r4) goto L_0x022c
            int r0 = r9.getAction()
            if (r0 == r2) goto L_0x022c
            int r9 = r9.getAction()
            if (r9 != r3) goto L_0x0239
        L_0x022c:
            r8.startedTracking = r1
            r8.maybeStartTracking = r1
            android.view.VelocityTracker r9 = r8.velocityTracker
            if (r9 == 0) goto L_0x0239
            r9.recycle()
            r8.velocityTracker = r5
        L_0x0239:
            boolean r9 = r8.startedTracking
            return r9
        L_0x023c:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.DrawerLayoutContainer.onTouchEvent(android.view.MotionEvent):boolean");
    }

    private View findScrollingChild(ViewGroup viewGroup, float f, float f2) {
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt.getVisibility() == 0) {
                childAt.getHitRect(this.rect);
                if (!this.rect.contains((int) f, (int) f2)) {
                    continue;
                } else if (childAt.canScrollHorizontally(-1)) {
                    return childAt;
                } else {
                    if (childAt instanceof ViewGroup) {
                        Rect rect2 = this.rect;
                        View findScrollingChild = findScrollingChild((ViewGroup) childAt, f - ((float) rect2.left), f2 - ((float) rect2.top));
                        if (findScrollingChild != null) {
                            return findScrollingChild;
                        }
                    } else {
                        continue;
                    }
                }
            }
        }
        return null;
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return this.parentActionBarLayout.checkTransitionAnimation() || onTouchEvent(motionEvent);
    }

    public void requestDisallowInterceptTouchEvent(boolean z) {
        if (this.maybeStartTracking && !this.startedTracking) {
            onTouchEvent((MotionEvent) null);
        }
        super.requestDisallowInterceptTouchEvent(z);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        this.inLayout = true;
        int childCount = getChildCount();
        for (int i5 = 0; i5 < childCount; i5++) {
            View childAt = getChildAt(i5);
            if (childAt.getVisibility() != 8) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) childAt.getLayoutParams();
                if (!BuildVars.DEBUG_VERSION) {
                    try {
                        if (this.drawerLayout != childAt) {
                            childAt.layout(layoutParams.leftMargin, layoutParams.topMargin + getPaddingTop(), layoutParams.leftMargin + childAt.getMeasuredWidth(), layoutParams.topMargin + childAt.getMeasuredHeight() + getPaddingTop());
                        } else {
                            childAt.layout(-childAt.getMeasuredWidth(), layoutParams.topMargin + getPaddingTop(), 0, layoutParams.topMargin + childAt.getMeasuredHeight() + getPaddingTop());
                        }
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                } else if (this.drawerLayout != childAt) {
                    childAt.layout(layoutParams.leftMargin, layoutParams.topMargin + getPaddingTop(), layoutParams.leftMargin + childAt.getMeasuredWidth(), layoutParams.topMargin + childAt.getMeasuredHeight() + getPaddingTop());
                } else {
                    childAt.layout(-childAt.getMeasuredWidth(), layoutParams.topMargin + getPaddingTop(), 0, layoutParams.topMargin + childAt.getMeasuredHeight() + getPaddingTop());
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
    @SuppressLint({"NewApi"})
    public void onMeasure(int i, int i2) {
        int size = View.MeasureSpec.getSize(i);
        int size2 = View.MeasureSpec.getSize(i2);
        setMeasuredDimension(size, size2);
        int i3 = Build.VERSION.SDK_INT;
        if (i3 < 21) {
            this.inLayout = true;
            if (size2 == AndroidUtilities.displaySize.y + AndroidUtilities.statusBarHeight) {
                if (getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    setPadding(0, AndroidUtilities.statusBarHeight, 0, 0);
                }
                size2 = AndroidUtilities.displaySize.y;
            } else if (getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                setPadding(0, 0, 0, 0);
            }
            this.inLayout = false;
        } else {
            int i4 = size2 - AndroidUtilities.statusBarHeight;
            if (i4 > 0 && i4 < 4096) {
                AndroidUtilities.displaySize.y = i4;
            }
        }
        boolean z = this.lastInsets != null && i3 >= 21;
        int childCount = getChildCount();
        for (int i5 = 0; i5 < childCount; i5++) {
            View childAt = getChildAt(i5);
            if (childAt.getVisibility() != 8) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) childAt.getLayoutParams();
                if (z) {
                    if (childAt.getFitsSystemWindows()) {
                        dispatchChildInsets(childAt, this.lastInsets, layoutParams.gravity);
                    } else if (childAt.getTag() == null) {
                        applyMarginInsets(layoutParams, this.lastInsets, layoutParams.gravity, Build.VERSION.SDK_INT >= 21);
                    }
                }
                if (this.drawerLayout != childAt) {
                    int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec((size - layoutParams.leftMargin) - layoutParams.rightMargin, NUM);
                    int i6 = layoutParams.height;
                    if (i6 <= 0) {
                        i6 = View.MeasureSpec.makeMeasureSpec((size2 - layoutParams.topMargin) - layoutParams.bottomMargin, NUM);
                    }
                    childAt.measure(makeMeasureSpec, i6);
                } else {
                    childAt.setPadding(0, 0, 0, 0);
                    childAt.measure(FrameLayout.getChildMeasureSpec(i, this.minDrawerMargin + layoutParams.leftMargin + layoutParams.rightMargin, layoutParams.width), FrameLayout.getChildMeasureSpec(i2, layoutParams.topMargin + layoutParams.bottomMargin, layoutParams.height));
                }
            }
        }
    }

    public void setBehindKeyboardColor(int i) {
        this.behindKeyboardColor = i;
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
    public boolean drawChild(Canvas canvas, View view, long j) {
        int i;
        int ceil;
        Canvas canvas2 = canvas;
        View view2 = view;
        int i2 = 0;
        if (!this.allowDrawContent) {
            return false;
        }
        int height = getHeight();
        boolean z = view2 != this.drawerLayout;
        int width = getWidth();
        int save = canvas.save();
        if (z) {
            int childCount = getChildCount();
            i = 0;
            int i3 = 0;
            for (int i4 = 0; i4 < childCount; i4++) {
                View childAt = getChildAt(i4);
                if (childAt.getVisibility() == 0 && childAt != this.drawerLayout) {
                    i3 = i4;
                }
                if (childAt != view2 && childAt.getVisibility() == 0 && childAt == this.drawerLayout && childAt.getHeight() >= height && (ceil = ((int) Math.ceil((double) childAt.getX())) + childAt.getMeasuredWidth()) > i) {
                    i = ceil;
                }
            }
            if (i != 0) {
                canvas2.clipRect(i - AndroidUtilities.dp(1.0f), 0, width, getHeight());
            }
            i2 = i3;
        } else {
            i = 0;
        }
        boolean drawChild = super.drawChild(canvas, view, j);
        canvas2.restoreToCount(save);
        if (this.scrimOpacity <= 0.0f || !z) {
            if (this.shadowLeft != null) {
                float max = Math.max(0.0f, Math.min(this.drawerPosition / ((float) AndroidUtilities.dp(20.0f)), 1.0f));
                if (max != 0.0f) {
                    this.shadowLeft.setBounds((int) this.drawerPosition, view.getTop(), ((int) this.drawerPosition) + this.shadowLeft.getIntrinsicWidth(), view.getBottom());
                    this.shadowLeft.setAlpha((int) (max * 255.0f));
                    this.shadowLeft.draw(canvas2);
                }
            }
        } else if (indexOfChild(view2) == i2) {
            this.scrimPaint.setColor(((int) (this.scrimOpacity * 153.0f)) << 24);
            canvas.drawRect((float) i, 0.0f, (float) width, (float) getHeight(), this.scrimPaint);
        }
        return drawChild;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        Object obj;
        if (Build.VERSION.SDK_INT >= 21 && (obj = this.lastInsets) != null) {
            WindowInsets windowInsets = (WindowInsets) obj;
            int systemWindowInsetBottom = windowInsets.getSystemWindowInsetBottom();
            if (systemWindowInsetBottom > 0) {
                this.backgroundPaint.setColor(this.behindKeyboardColor);
                canvas.drawRect(0.0f, (float) (getMeasuredHeight() - systemWindowInsetBottom), (float) getMeasuredWidth(), (float) getMeasuredHeight(), this.backgroundPaint);
            }
            if (this.hasCutout) {
                this.backgroundPaint.setColor(-16777216);
                int systemWindowInsetLeft = windowInsets.getSystemWindowInsetLeft();
                if (systemWindowInsetLeft != 0) {
                    canvas.drawRect(0.0f, 0.0f, (float) systemWindowInsetLeft, (float) getMeasuredHeight(), this.backgroundPaint);
                }
                int systemWindowInsetRight = windowInsets.getSystemWindowInsetRight();
                if (systemWindowInsetRight != 0) {
                    canvas.drawRect((float) systemWindowInsetRight, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), this.backgroundPaint);
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

    private static class PreviewForegroundDrawable extends Drawable {
        private final GradientDrawable bottomDrawable;
        private final GradientDrawable topDrawable;

        public int getOpacity() {
            return -3;
        }

        public void setColorFilter(ColorFilter colorFilter) {
        }

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
            int i = bounds.left;
            int i2 = bounds.top;
            canvas.clipRect(i, i2, bounds.right, ActionBar.getCurrentActionBarHeight() + i2);
            this.topDrawable.draw(canvas);
            canvas.restore();
            canvas.save();
            canvas.clipRect(bounds.left, bounds.top + ActionBar.getCurrentActionBarHeight(), bounds.right, bounds.bottom);
            this.bottomDrawable.draw(canvas);
            canvas.restore();
        }

        /* access modifiers changed from: protected */
        public void onBoundsChange(Rect rect) {
            super.onBoundsChange(rect);
            this.topDrawable.setBounds(rect);
            this.bottomDrawable.setBounds(rect);
        }

        public void setAlpha(int i) {
            this.topDrawable.setAlpha(i);
            this.bottomDrawable.setAlpha(i);
        }
    }
}
