package org.telegram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.Keep;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import java.util.ArrayList;
import java.util.Iterator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.exoplayer2.C0539C;
import org.telegram.ui.ActionBar.Theme.ThemeInfo;
import org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.ui.Components.LayoutHelper;

public class ActionBarLayout extends FrameLayout {
    private static Drawable headerShadowDrawable;
    private static Drawable layerShadowDrawable;
    private static Paint scrimPaint;
    private AccelerateDecelerateInterpolator accelerateDecelerateInterpolator = new AccelerateDecelerateInterpolator();
    private int[] animateEndColors;
    private ThemeInfo animateSetThemeAfterAnimation;
    private int[] animateStartColors;
    private boolean animateThemeAfterAnimation;
    protected boolean animationInProgress;
    private float animationProgress = 0.0f;
    private Runnable animationRunnable;
    private View backgroundView;
    private boolean beginTrackingSent;
    private LinearLayoutContainer containerView;
    private LinearLayoutContainer containerViewBack;
    private ActionBar currentActionBar;
    private AnimatorSet currentAnimation;
    private DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator(1.5f);
    private Runnable delayedOpenAnimationRunnable;
    private ActionBarLayoutDelegate delegate = null;
    private DrawerLayoutContainer drawerLayoutContainer;
    public ArrayList<BaseFragment> fragmentsStack = null;
    private boolean inActionMode;
    public float innerTranslationX;
    private long lastFrameTime;
    private boolean maybeStartTracking;
    private Runnable onCloseAnimationEndRunnable;
    private Runnable onOpenAnimationEndRunnable;
    private Runnable overlayAction;
    protected Activity parentActivity = null;
    private boolean rebuildAfterAnimation;
    private boolean rebuildLastAfterAnimation;
    private boolean removeActionBarExtraHeight;
    private boolean showLastAfterAnimation;
    protected boolean startedTracking;
    private int startedTrackingPointerId;
    private int startedTrackingX;
    private int startedTrackingY;
    private String subtitleOverlayText;
    private float themeAnimationValue;
    private ThemeDescriptionDelegate themeAnimatorDelegate;
    private ThemeDescription[] themeAnimatorDescriptions;
    private AnimatorSet themeAnimatorSet;
    private String titleOverlayText;
    private boolean transitionAnimationInProgress;
    private long transitionAnimationStartTime;
    private boolean useAlphaAnimations;
    private VelocityTracker velocityTracker;
    private Runnable waitingForKeyboardCloseRunnable;

    /* renamed from: org.telegram.ui.ActionBar.ActionBarLayout$4 */
    class C07084 extends AnimatorListenerAdapter {
        C07084() {
        }

        public void onAnimationEnd(Animator animation) {
            ActionBarLayout.this.onAnimationEndCheck(false);
        }
    }

    /* renamed from: org.telegram.ui.ActionBar.ActionBarLayout$6 */
    class C07106 implements Runnable {
        C07106() {
        }

        public void run() {
            ActionBarLayout.this.onAnimationEndCheck(false);
        }
    }

    /* renamed from: org.telegram.ui.ActionBar.ActionBarLayout$7 */
    class C07117 implements Runnable {
        C07117() {
        }

        public void run() {
            if (ActionBarLayout.this.waitingForKeyboardCloseRunnable == this) {
                ActionBarLayout.this.waitingForKeyboardCloseRunnable = null;
                ActionBarLayout.this.startLayoutAnimation(true, true);
            }
        }
    }

    /* renamed from: org.telegram.ui.ActionBar.ActionBarLayout$8 */
    class C07128 implements Runnable {
        C07128() {
        }

        public void run() {
            if (ActionBarLayout.this.delayedOpenAnimationRunnable == this) {
                ActionBarLayout.this.delayedOpenAnimationRunnable = null;
                ActionBarLayout.this.startLayoutAnimation(true, true);
            }
        }
    }

    public interface ActionBarLayoutDelegate {
        boolean needAddFragmentToStack(BaseFragment baseFragment, ActionBarLayout actionBarLayout);

        boolean needCloseLastFragment(ActionBarLayout actionBarLayout);

        boolean needPresentFragment(BaseFragment baseFragment, boolean z, boolean z2, ActionBarLayout actionBarLayout);

        boolean onPreIme();

        void onRebuildAllFragments(ActionBarLayout actionBarLayout, boolean z);
    }

    public class LinearLayoutContainer extends LinearLayout {
        private boolean isKeyboardVisible;
        private Rect rect = new Rect();

        public LinearLayoutContainer(Context context) {
            super(context);
            setOrientation(1);
        }

        protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
            if (child instanceof ActionBar) {
                return super.drawChild(canvas, child, drawingTime);
            }
            boolean result;
            int actionBarHeight = 0;
            int childCount = getChildCount();
            for (int a = 0; a < childCount; a++) {
                View view = getChildAt(a);
                if (view != child) {
                    if ((view instanceof ActionBar) && view.getVisibility() == 0) {
                        if (((ActionBar) view).getCastShadows()) {
                            actionBarHeight = view.getMeasuredHeight();
                        }
                        result = super.drawChild(canvas, child, drawingTime);
                        if (!(actionBarHeight == 0 || ActionBarLayout.headerShadowDrawable == null)) {
                            ActionBarLayout.headerShadowDrawable.setBounds(0, actionBarHeight, getMeasuredWidth(), ActionBarLayout.headerShadowDrawable.getIntrinsicHeight() + actionBarHeight);
                            ActionBarLayout.headerShadowDrawable.draw(canvas);
                        }
                        return result;
                    }
                }
            }
            result = super.drawChild(canvas, child, drawingTime);
            ActionBarLayout.headerShadowDrawable.setBounds(0, actionBarHeight, getMeasuredWidth(), ActionBarLayout.headerShadowDrawable.getIntrinsicHeight() + actionBarHeight);
            ActionBarLayout.headerShadowDrawable.draw(canvas);
            return result;
        }

        public boolean hasOverlappingRendering() {
            return false;
        }

        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            super.onLayout(changed, l, t, r, b);
            View rootView = getRootView();
            getWindowVisibleDisplayFrame(this.rect);
            boolean z = false;
            if (((rootView.getHeight() - (this.rect.top != 0 ? AndroidUtilities.statusBarHeight : 0)) - AndroidUtilities.getViewInset(rootView)) - (this.rect.bottom - this.rect.top) > 0) {
                z = true;
            }
            this.isKeyboardVisible = z;
            if (ActionBarLayout.this.waitingForKeyboardCloseRunnable != null && !ActionBarLayout.this.containerView.isKeyboardVisible && !ActionBarLayout.this.containerViewBack.isKeyboardVisible) {
                AndroidUtilities.cancelRunOnUIThread(ActionBarLayout.this.waitingForKeyboardCloseRunnable);
                ActionBarLayout.this.waitingForKeyboardCloseRunnable.run();
                ActionBarLayout.this.waitingForKeyboardCloseRunnable = null;
            }
        }
    }

    public ActionBarLayout(Context context) {
        super(context);
        this.parentActivity = (Activity) context;
        if (layerShadowDrawable == null) {
            layerShadowDrawable = getResources().getDrawable(R.drawable.layer_shadow);
            headerShadowDrawable = getResources().getDrawable(R.drawable.header_shadow).mutate();
            scrimPaint = new Paint();
        }
    }

    public void init(ArrayList<BaseFragment> stack) {
        this.fragmentsStack = stack;
        this.containerViewBack = new LinearLayoutContainer(this.parentActivity);
        addView(this.containerViewBack);
        LayoutParams layoutParams = (LayoutParams) this.containerViewBack.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        layoutParams.gravity = 51;
        this.containerViewBack.setLayoutParams(layoutParams);
        this.containerView = new LinearLayoutContainer(this.parentActivity);
        addView(this.containerView);
        layoutParams = (LayoutParams) this.containerView.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        layoutParams.gravity = 51;
        this.containerView.setLayoutParams(layoutParams);
        Iterator it = this.fragmentsStack.iterator();
        while (it.hasNext()) {
            ((BaseFragment) it.next()).setParentLayout(this);
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (!this.fragmentsStack.isEmpty()) {
            BaseFragment lastFragment = (BaseFragment) this.fragmentsStack.get(this.fragmentsStack.size() - 1);
            lastFragment.onConfigurationChanged(newConfig);
            if (lastFragment.visibleDialog instanceof BottomSheet) {
                ((BottomSheet) lastFragment.visibleDialog).onConfigurationChanged(newConfig);
            }
        }
    }

    public void drawHeaderShadow(Canvas canvas, int y) {
        if (headerShadowDrawable != null) {
            headerShadowDrawable.setBounds(0, y, getMeasuredWidth(), headerShadowDrawable.getIntrinsicHeight() + y);
            headerShadowDrawable.draw(canvas);
        }
    }

    @Keep
    public void setInnerTranslationX(float value) {
        this.innerTranslationX = value;
        invalidate();
    }

    @Keep
    public float getInnerTranslationX() {
        return this.innerTranslationX;
    }

    public void dismissDialogs() {
        if (!this.fragmentsStack.isEmpty()) {
            ((BaseFragment) this.fragmentsStack.get(this.fragmentsStack.size() - 1)).dismissCurrentDialig();
        }
    }

    public void onResume() {
        if (this.transitionAnimationInProgress) {
            if (this.currentAnimation != null) {
                this.currentAnimation.cancel();
                this.currentAnimation = null;
            }
            if (this.onCloseAnimationEndRunnable != null) {
                onCloseAnimationEnd();
            } else if (this.onOpenAnimationEndRunnable != null) {
                onOpenAnimationEnd();
            }
        }
        if (!this.fragmentsStack.isEmpty()) {
            ((BaseFragment) this.fragmentsStack.get(this.fragmentsStack.size() - 1)).onResume();
        }
    }

    public void onPause() {
        if (!this.fragmentsStack.isEmpty()) {
            ((BaseFragment) this.fragmentsStack.get(this.fragmentsStack.size() - 1)).onPause();
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!(this.animationInProgress || checkTransitionAnimation())) {
            if (!onTouchEvent(ev)) {
                return false;
            }
        }
        return true;
    }

    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        onTouchEvent(null);
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        if (event != null && event.getKeyCode() == 4) {
            boolean z = true;
            if (event.getAction() == 1) {
                if ((this.delegate == null || !this.delegate.onPreIme()) && !super.dispatchKeyEventPreIme(event)) {
                    z = false;
                }
                return z;
            }
        }
        return super.dispatchKeyEventPreIme(event);
    }

    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        Canvas canvas2 = canvas;
        View view = child;
        int width = (getWidth() - getPaddingLeft()) - getPaddingRight();
        int translationX = ((int) this.innerTranslationX) + getPaddingRight();
        int clipLeft = getPaddingLeft();
        int clipRight = getPaddingLeft() + width;
        if (view == this.containerViewBack) {
            clipRight = translationX;
        } else if (view == r0.containerView) {
            clipLeft = translationX;
        }
        int clipLeft2 = clipLeft;
        int clipRight2 = clipRight;
        int restoreCount = canvas.save();
        if (!r0.transitionAnimationInProgress) {
            canvas2.clipRect(clipLeft2, 0, clipRight2, getHeight());
        }
        boolean result = super.drawChild(canvas, child, drawingTime);
        canvas2.restoreToCount(restoreCount);
        if (translationX != 0) {
            float alpha;
            if (view == r0.containerView) {
                alpha = Math.max(0.0f, Math.min(((float) (width - translationX)) / ((float) AndroidUtilities.dp(20.0f)), 1.0f));
                layerShadowDrawable.setBounds(translationX - layerShadowDrawable.getIntrinsicWidth(), child.getTop(), translationX, child.getBottom());
                layerShadowDrawable.setAlpha((int) (255.0f * alpha));
                layerShadowDrawable.draw(canvas2);
            } else if (view == r0.containerViewBack) {
                alpha = Math.min(0.8f, ((float) (width - translationX)) / ((float) width));
                if (alpha < 0.0f) {
                    alpha = 0.0f;
                }
                scrimPaint.setColor(((int) (153.0f * alpha)) << 24);
                canvas2.drawRect((float) clipLeft2, 0.0f, (float) clipRight2, (float) getHeight(), scrimPaint);
            }
        }
        return result;
    }

    public void setDelegate(ActionBarLayoutDelegate delegate) {
        this.delegate = delegate;
    }

    private void onSlideAnimationEnd(boolean backAnimation) {
        BaseFragment lastFragment;
        if (backAnimation) {
            ViewGroup parent;
            lastFragment = (BaseFragment) this.fragmentsStack.get(this.fragmentsStack.size() - 2);
            lastFragment.onPause();
            if (lastFragment.fragmentView != null) {
                parent = (ViewGroup) lastFragment.fragmentView.getParent();
                if (parent != null) {
                    lastFragment.onRemoveFromParent();
                    parent.removeView(lastFragment.fragmentView);
                }
            }
            if (lastFragment.actionBar != null && lastFragment.actionBar.getAddToContainer()) {
                parent = (ViewGroup) lastFragment.actionBar.getParent();
                if (parent != null) {
                    parent.removeView(lastFragment.actionBar);
                }
            }
        } else {
            lastFragment = (BaseFragment) this.fragmentsStack.get(this.fragmentsStack.size() - 1);
            lastFragment.onPause();
            lastFragment.onFragmentDestroy();
            lastFragment.setParentLayout(null);
            this.fragmentsStack.remove(this.fragmentsStack.size() - 1);
            LinearLayoutContainer temp = this.containerView;
            this.containerView = this.containerViewBack;
            this.containerViewBack = temp;
            bringChildToFront(this.containerView);
            lastFragment = (BaseFragment) this.fragmentsStack.get(this.fragmentsStack.size() - 1);
            this.currentActionBar = lastFragment.actionBar;
            lastFragment.onResume();
            lastFragment.onBecomeFullyVisible();
        }
        this.containerViewBack.setVisibility(8);
        this.startedTracking = false;
        this.animationInProgress = false;
        this.containerView.setTranslationX(0.0f);
        this.containerViewBack.setTranslationX(0.0f);
        setInnerTranslationX(0.0f);
    }

    private void prepareForMoving(MotionEvent ev) {
        this.maybeStartTracking = false;
        this.startedTracking = true;
        this.startedTrackingX = (int) ev.getX();
        this.containerViewBack.setVisibility(0);
        this.beginTrackingSent = false;
        BaseFragment lastFragment = (BaseFragment) this.fragmentsStack.get(this.fragmentsStack.size() - 2);
        View fragmentView = lastFragment.fragmentView;
        if (fragmentView == null) {
            fragmentView = lastFragment.createView(this.parentActivity);
        }
        ViewGroup parent = (ViewGroup) fragmentView.getParent();
        if (parent != null) {
            lastFragment.onRemoveFromParent();
            parent.removeView(fragmentView);
        }
        if (lastFragment.actionBar != null && lastFragment.actionBar.getAddToContainer()) {
            parent = (ViewGroup) lastFragment.actionBar.getParent();
            if (parent != null) {
                parent.removeView(lastFragment.actionBar);
            }
            if (this.removeActionBarExtraHeight) {
                lastFragment.actionBar.setOccupyStatusBar(false);
            }
            this.containerViewBack.addView(lastFragment.actionBar);
            lastFragment.actionBar.setTitleOverlayText(this.titleOverlayText, this.subtitleOverlayText, this.overlayAction);
        }
        this.containerViewBack.addView(fragmentView);
        ViewGroup.LayoutParams layoutParams = fragmentView.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        fragmentView.setLayoutParams(layoutParams);
        if (!lastFragment.hasOwnBackground && fragmentView.getBackground() == null) {
            fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        }
        lastFragment.onResume();
    }

    public boolean onTouchEvent(MotionEvent ev) {
        if (checkTransitionAnimation() || this.inActionMode || this.animationInProgress) {
            return false;
        }
        if (this.fragmentsStack.size() > 1) {
            if (ev == null || ev.getAction() != 0 || this.startedTracking || this.maybeStartTracking) {
                if (ev != null && ev.getAction() == 2 && ev.getPointerId(0) == this.startedTrackingPointerId) {
                    if (this.velocityTracker == null) {
                        this.velocityTracker = VelocityTracker.obtain();
                    }
                    int dx = Math.max(0, (int) (ev.getX() - ((float) this.startedTrackingX)));
                    int dy = Math.abs(((int) ev.getY()) - this.startedTrackingY);
                    this.velocityTracker.addMovement(ev);
                    if (this.maybeStartTracking && !this.startedTracking && ((float) dx) >= AndroidUtilities.getPixelsInCM(0.4f, true) && Math.abs(dx) / 3 > dy) {
                        prepareForMoving(ev);
                    } else if (this.startedTracking) {
                        if (!this.beginTrackingSent) {
                            if (this.parentActivity.getCurrentFocus() != null) {
                                AndroidUtilities.hideKeyboard(this.parentActivity.getCurrentFocus());
                            }
                            ((BaseFragment) this.fragmentsStack.get(this.fragmentsStack.size() - 1)).onBeginSlide();
                            this.beginTrackingSent = true;
                        }
                        this.containerView.setTranslationX((float) dx);
                        setInnerTranslationX((float) dx);
                    }
                } else if (ev != null && ev.getPointerId(0) == this.startedTrackingPointerId && (ev.getAction() == 3 || ev.getAction() == 1 || ev.getAction() == 6)) {
                    float velX;
                    if (this.velocityTracker == null) {
                        this.velocityTracker = VelocityTracker.obtain();
                    }
                    this.velocityTracker.computeCurrentVelocity(1000);
                    if (!this.startedTracking && ((BaseFragment) this.fragmentsStack.get(this.fragmentsStack.size() - 1)).swipeBackEnabled) {
                        velX = this.velocityTracker.getXVelocity();
                        float velY = this.velocityTracker.getYVelocity();
                        if (velX >= 3500.0f && velX > Math.abs(velY)) {
                            prepareForMoving(ev);
                            if (!this.beginTrackingSent) {
                                if (((Activity) getContext()).getCurrentFocus() != null) {
                                    AndroidUtilities.hideKeyboard(((Activity) getContext()).getCurrentFocus());
                                }
                                this.beginTrackingSent = true;
                            }
                        }
                    }
                    if (this.startedTracking) {
                        float distToMove;
                        velX = this.containerView.getX();
                        AnimatorSet animatorSet = new AnimatorSet();
                        float velX2 = this.velocityTracker.getXVelocity();
                        final boolean backAnimation = velX < ((float) this.containerView.getMeasuredWidth()) / 3.0f && (velX2 < 3500.0f || velX2 < this.velocityTracker.getYVelocity());
                        Animator[] animatorArr;
                        if (backAnimation) {
                            distToMove = velX;
                            animatorArr = new Animator[2];
                            animatorArr[0] = ObjectAnimator.ofFloat(this.containerView, "translationX", new float[]{0.0f});
                            animatorArr[1] = ObjectAnimator.ofFloat(this, "innerTranslationX", new float[]{0.0f});
                            animatorSet.playTogether(animatorArr);
                        } else {
                            distToMove = ((float) this.containerView.getMeasuredWidth()) - velX;
                            animatorArr = new Animator[2];
                            animatorArr[0] = ObjectAnimator.ofFloat(this.containerView, "translationX", new float[]{(float) this.containerView.getMeasuredWidth()});
                            animatorArr[1] = ObjectAnimator.ofFloat(this, "innerTranslationX", new float[]{(float) this.containerView.getMeasuredWidth()});
                            animatorSet.playTogether(animatorArr);
                        }
                        animatorSet.setDuration((long) Math.max((int) ((200.0f / ((float) this.containerView.getMeasuredWidth())) * distToMove), 50));
                        animatorSet.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                ActionBarLayout.this.onSlideAnimationEnd(backAnimation);
                            }
                        });
                        animatorSet.start();
                        this.animationInProgress = true;
                    } else {
                        this.maybeStartTracking = false;
                        this.startedTracking = false;
                    }
                    if (this.velocityTracker != null) {
                        this.velocityTracker.recycle();
                        this.velocityTracker = null;
                    }
                } else if (ev == null) {
                    this.maybeStartTracking = false;
                    this.startedTracking = false;
                    if (this.velocityTracker != null) {
                        this.velocityTracker.recycle();
                        this.velocityTracker = null;
                    }
                }
            } else if (!((BaseFragment) this.fragmentsStack.get(this.fragmentsStack.size() - 1)).swipeBackEnabled) {
                return false;
            } else {
                this.startedTrackingPointerId = ev.getPointerId(0);
                this.maybeStartTracking = true;
                this.startedTrackingX = (int) ev.getX();
                this.startedTrackingY = (int) ev.getY();
                if (this.velocityTracker != null) {
                    this.velocityTracker.clear();
                }
            }
        }
        return this.startedTracking;
    }

    public void onBackPressed() {
        if (!(this.startedTracking || checkTransitionAnimation())) {
            if (!this.fragmentsStack.isEmpty()) {
                if (this.currentActionBar == null || !this.currentActionBar.isSearchFieldVisible) {
                    if (((BaseFragment) this.fragmentsStack.get(this.fragmentsStack.size() - 1)).onBackPressed() && !this.fragmentsStack.isEmpty()) {
                        closeLastFragment(true);
                    }
                    return;
                }
                this.currentActionBar.closeSearchField();
            }
        }
    }

    public void onLowMemory() {
        Iterator it = this.fragmentsStack.iterator();
        while (it.hasNext()) {
            ((BaseFragment) it.next()).onLowMemory();
        }
    }

    private void onAnimationEndCheck(boolean byCheck) {
        onCloseAnimationEnd();
        onOpenAnimationEnd();
        if (this.waitingForKeyboardCloseRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(this.waitingForKeyboardCloseRunnable);
            this.waitingForKeyboardCloseRunnable = null;
        }
        if (this.currentAnimation != null) {
            if (byCheck) {
                this.currentAnimation.cancel();
            }
            this.currentAnimation = null;
        }
        if (this.animationRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(this.animationRunnable);
            this.animationRunnable = null;
        }
        setAlpha(1.0f);
        this.containerView.setAlpha(1.0f);
        this.containerView.setScaleX(1.0f);
        this.containerView.setScaleY(1.0f);
        this.containerViewBack.setAlpha(1.0f);
        this.containerViewBack.setScaleX(1.0f);
        this.containerViewBack.setScaleY(1.0f);
    }

    public BaseFragment getLastFragment() {
        if (this.fragmentsStack.isEmpty()) {
            return null;
        }
        return (BaseFragment) this.fragmentsStack.get(this.fragmentsStack.size() - 1);
    }

    public boolean checkTransitionAnimation() {
        if (this.transitionAnimationInProgress && this.transitionAnimationStartTime < System.currentTimeMillis() - 1500) {
            onAnimationEndCheck(true);
        }
        return this.transitionAnimationInProgress;
    }

    private void presentFragmentInternalRemoveOld(boolean removeLast, BaseFragment fragment) {
        if (fragment != null) {
            fragment.onPause();
            if (removeLast) {
                fragment.onFragmentDestroy();
                fragment.setParentLayout(null);
                this.fragmentsStack.remove(fragment);
            } else {
                ViewGroup parent;
                if (fragment.fragmentView != null) {
                    parent = (ViewGroup) fragment.fragmentView.getParent();
                    if (parent != null) {
                        fragment.onRemoveFromParent();
                        parent.removeView(fragment.fragmentView);
                    }
                }
                if (fragment.actionBar != null && fragment.actionBar.getAddToContainer()) {
                    parent = (ViewGroup) fragment.actionBar.getParent();
                    if (parent != null) {
                        parent.removeView(fragment.actionBar);
                    }
                }
            }
            this.containerViewBack.setVisibility(8);
        }
    }

    public boolean presentFragment(BaseFragment fragment) {
        return presentFragment(fragment, false, false, true);
    }

    public boolean presentFragment(BaseFragment fragment, boolean removeLast) {
        return presentFragment(fragment, removeLast, false, true);
    }

    private void startLayoutAnimation(final boolean open, final boolean first) {
        if (first) {
            this.animationProgress = 0.0f;
            this.lastFrameTime = System.nanoTime() / C0539C.MICROS_PER_SECOND;
        }
        Runnable c07062 = new Runnable() {
            public void run() {
                if (ActionBarLayout.this.animationRunnable == this) {
                    ActionBarLayout.this.animationRunnable = null;
                    if (first) {
                        ActionBarLayout.this.transitionAnimationStartTime = System.currentTimeMillis();
                    }
                    long newTime = System.nanoTime() / C0539C.MICROS_PER_SECOND;
                    long dt = newTime - ActionBarLayout.this.lastFrameTime;
                    if (dt > 18) {
                        dt = 18;
                    }
                    ActionBarLayout.this.lastFrameTime = newTime;
                    ActionBarLayout.this.animationProgress = ActionBarLayout.this.animationProgress + (((float) dt) / 150.0f);
                    if (ActionBarLayout.this.animationProgress > 1.0f) {
                        ActionBarLayout.this.animationProgress = 1.0f;
                    }
                    float interpolated = ActionBarLayout.this.decelerateInterpolator.getInterpolation(ActionBarLayout.this.animationProgress);
                    if (open) {
                        ActionBarLayout.this.containerView.setAlpha(interpolated);
                        ActionBarLayout.this.containerView.setTranslationX(((float) AndroidUtilities.dp(48.0f)) * (1.0f - interpolated));
                    } else {
                        ActionBarLayout.this.containerViewBack.setAlpha(1.0f - interpolated);
                        ActionBarLayout.this.containerViewBack.setTranslationX(((float) AndroidUtilities.dp(48.0f)) * interpolated);
                    }
                    if (ActionBarLayout.this.animationProgress < 1.0f) {
                        ActionBarLayout.this.startLayoutAnimation(open, false);
                    } else {
                        ActionBarLayout.this.onAnimationEndCheck(false);
                    }
                }
            }
        };
        this.animationRunnable = c07062;
        AndroidUtilities.runOnUIThread(c07062);
    }

    public void resumeDelayedFragmentAnimation() {
        if (this.delayedOpenAnimationRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(this.delayedOpenAnimationRunnable);
            this.delayedOpenAnimationRunnable.run();
            this.delayedOpenAnimationRunnable = null;
        }
    }

    public boolean presentFragment(BaseFragment fragment, boolean removeLast, boolean forceWithoutAnimation, boolean check) {
        ActionBarLayout actionBarLayout = this;
        final BaseFragment baseFragment = fragment;
        final boolean z = removeLast;
        boolean z2 = forceWithoutAnimation;
        if (!checkTransitionAnimation() && (actionBarLayout.delegate == null || !check || actionBarLayout.delegate.needPresentFragment(baseFragment, z, z2, actionBarLayout))) {
            if (fragment.onFragmentCreate()) {
                ViewGroup parent;
                if (actionBarLayout.parentActivity.getCurrentFocus() != null) {
                    AndroidUtilities.hideKeyboard(actionBarLayout.parentActivity.getCurrentFocus());
                }
                boolean needAnimation = !z2 && MessagesController.getGlobalMainSettings().getBoolean("view_animations", true);
                final BaseFragment currentFragment = !actionBarLayout.fragmentsStack.isEmpty() ? (BaseFragment) actionBarLayout.fragmentsStack.get(actionBarLayout.fragmentsStack.size() - 1) : null;
                baseFragment.setParentLayout(actionBarLayout);
                View fragmentView = baseFragment.fragmentView;
                if (fragmentView == null) {
                    fragmentView = baseFragment.createView(actionBarLayout.parentActivity);
                } else {
                    parent = (ViewGroup) fragmentView.getParent();
                    if (parent != null) {
                        fragment.onRemoveFromParent();
                        parent.removeView(fragmentView);
                    }
                }
                if (baseFragment.actionBar != null && baseFragment.actionBar.getAddToContainer()) {
                    if (actionBarLayout.removeActionBarExtraHeight) {
                        baseFragment.actionBar.setOccupyStatusBar(false);
                    }
                    parent = (ViewGroup) baseFragment.actionBar.getParent();
                    if (parent != null) {
                        parent.removeView(baseFragment.actionBar);
                    }
                    actionBarLayout.containerViewBack.addView(baseFragment.actionBar);
                    baseFragment.actionBar.setTitleOverlayText(actionBarLayout.titleOverlayText, actionBarLayout.subtitleOverlayText, actionBarLayout.overlayAction);
                }
                actionBarLayout.containerViewBack.addView(fragmentView);
                ViewGroup.LayoutParams layoutParams = fragmentView.getLayoutParams();
                layoutParams.width = -1;
                layoutParams.height = -1;
                fragmentView.setLayoutParams(layoutParams);
                actionBarLayout.fragmentsStack.add(baseFragment);
                fragment.onResume();
                actionBarLayout.currentActionBar = baseFragment.actionBar;
                if (!baseFragment.hasOwnBackground && fragmentView.getBackground() == null) {
                    fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                }
                LinearLayoutContainer temp = actionBarLayout.containerView;
                actionBarLayout.containerView = actionBarLayout.containerViewBack;
                actionBarLayout.containerViewBack = temp;
                actionBarLayout.containerView.setVisibility(0);
                setInnerTranslationX(0.0f);
                bringChildToFront(actionBarLayout.containerView);
                if (!needAnimation) {
                    presentFragmentInternalRemoveOld(z, currentFragment);
                    if (actionBarLayout.backgroundView != null) {
                        actionBarLayout.backgroundView.setVisibility(0);
                    }
                }
                if (!needAnimation) {
                    if (actionBarLayout.backgroundView != null) {
                        actionBarLayout.backgroundView.setAlpha(1.0f);
                        actionBarLayout.backgroundView.setVisibility(0);
                    }
                    baseFragment.onTransitionAnimationStart(true, false);
                    baseFragment.onTransitionAnimationEnd(true, false);
                    fragment.onBecomeFullyVisible();
                } else if (actionBarLayout.useAlphaAnimations && actionBarLayout.fragmentsStack.size() == 1) {
                    presentFragmentInternalRemoveOld(z, currentFragment);
                    actionBarLayout.transitionAnimationStartTime = System.currentTimeMillis();
                    actionBarLayout.transitionAnimationInProgress = true;
                    actionBarLayout.onOpenAnimationEndRunnable = new Runnable() {
                        public void run() {
                            baseFragment.onTransitionAnimationEnd(true, false);
                            baseFragment.onBecomeFullyVisible();
                        }
                    };
                    ArrayList<Animator> animators = new ArrayList();
                    animators.add(ObjectAnimator.ofFloat(actionBarLayout, "alpha", new float[]{0.0f, 1.0f}));
                    if (actionBarLayout.backgroundView != null) {
                        actionBarLayout.backgroundView.setVisibility(0);
                        animators.add(ObjectAnimator.ofFloat(actionBarLayout.backgroundView, "alpha", new float[]{0.0f, 1.0f}));
                    }
                    baseFragment.onTransitionAnimationStart(true, false);
                    actionBarLayout.currentAnimation = new AnimatorSet();
                    actionBarLayout.currentAnimation.playTogether(animators);
                    actionBarLayout.currentAnimation.setInterpolator(actionBarLayout.accelerateDecelerateInterpolator);
                    actionBarLayout.currentAnimation.setDuration(200);
                    actionBarLayout.currentAnimation.addListener(new C07084());
                    actionBarLayout.currentAnimation.start();
                } else {
                    actionBarLayout.transitionAnimationStartTime = System.currentTimeMillis();
                    actionBarLayout.transitionAnimationInProgress = true;
                    actionBarLayout.onOpenAnimationEndRunnable = new Runnable() {
                        public void run() {
                            ActionBarLayout.this.presentFragmentInternalRemoveOld(z, currentFragment);
                            baseFragment.onTransitionAnimationEnd(true, false);
                            baseFragment.onBecomeFullyVisible();
                            ActionBarLayout.this.containerView.setTranslationX(0.0f);
                        }
                    };
                    baseFragment.onTransitionAnimationStart(true, false);
                    AnimatorSet animation = baseFragment.onCustomTransitionAnimation(true, new C07106());
                    if (animation == null) {
                        actionBarLayout.containerView.setAlpha(0.0f);
                        actionBarLayout.containerView.setTranslationX(48.0f);
                        if (!actionBarLayout.containerView.isKeyboardVisible) {
                            if (!actionBarLayout.containerViewBack.isKeyboardVisible) {
                                if (fragment.needDelayOpenAnimation()) {
                                    actionBarLayout.delayedOpenAnimationRunnable = new C07128();
                                    AndroidUtilities.runOnUIThread(actionBarLayout.delayedOpenAnimationRunnable, 200);
                                } else {
                                    startLayoutAnimation(true, true);
                                }
                            }
                        }
                        actionBarLayout.waitingForKeyboardCloseRunnable = new C07117();
                        AndroidUtilities.runOnUIThread(actionBarLayout.waitingForKeyboardCloseRunnable, 200);
                    } else {
                        actionBarLayout.containerView.setAlpha(1.0f);
                        actionBarLayout.containerView.setTranslationX(0.0f);
                        actionBarLayout.currentAnimation = animation;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public boolean addFragmentToStack(BaseFragment fragment) {
        return addFragmentToStack(fragment, -1);
    }

    public boolean addFragmentToStack(BaseFragment fragment, int position) {
        if ((this.delegate != null && !this.delegate.needAddFragmentToStack(fragment, this)) || !fragment.onFragmentCreate()) {
            return false;
        }
        fragment.setParentLayout(this);
        if (position == -1) {
            if (!this.fragmentsStack.isEmpty()) {
                ViewGroup parent;
                BaseFragment previousFragment = (BaseFragment) this.fragmentsStack.get(this.fragmentsStack.size() - 1);
                previousFragment.onPause();
                if (previousFragment.actionBar != null && previousFragment.actionBar.getAddToContainer()) {
                    parent = (ViewGroup) previousFragment.actionBar.getParent();
                    if (parent != null) {
                        parent.removeView(previousFragment.actionBar);
                    }
                }
                if (previousFragment.fragmentView != null) {
                    parent = (ViewGroup) previousFragment.fragmentView.getParent();
                    if (parent != null) {
                        previousFragment.onRemoveFromParent();
                        parent.removeView(previousFragment.fragmentView);
                    }
                }
            }
            this.fragmentsStack.add(fragment);
        } else {
            this.fragmentsStack.add(position, fragment);
        }
        return true;
    }

    private void closeLastFragmentInternalRemoveOld(BaseFragment fragment) {
        fragment.onPause();
        fragment.onFragmentDestroy();
        fragment.setParentLayout(null);
        this.fragmentsStack.remove(fragment);
        this.containerViewBack.setVisibility(8);
        bringChildToFront(this.containerView);
    }

    public void closeLastFragment(boolean animated) {
        if ((this.delegate == null || this.delegate.needCloseLastFragment(this)) && !checkTransitionAnimation()) {
            if (!this.fragmentsStack.isEmpty()) {
                if (this.parentActivity.getCurrentFocus() != null) {
                    AndroidUtilities.hideKeyboard(this.parentActivity.getCurrentFocus());
                }
                setInnerTranslationX(0.0f);
                boolean needAnimation = animated && MessagesController.getGlobalMainSettings().getBoolean("view_animations", true);
                final BaseFragment currentFragment = (BaseFragment) this.fragmentsStack.get(this.fragmentsStack.size() - 1);
                BaseFragment previousFragment = null;
                if (this.fragmentsStack.size() > 1) {
                    previousFragment = (BaseFragment) this.fragmentsStack.get(this.fragmentsStack.size() - 2);
                }
                if (previousFragment != null) {
                    ViewGroup parent;
                    LinearLayoutContainer temp = this.containerView;
                    this.containerView = this.containerViewBack;
                    this.containerViewBack = temp;
                    this.containerView.setVisibility(0);
                    previousFragment.setParentLayout(this);
                    View fragmentView = previousFragment.fragmentView;
                    if (fragmentView == null) {
                        fragmentView = previousFragment.createView(this.parentActivity);
                    } else {
                        parent = (ViewGroup) fragmentView.getParent();
                        if (parent != null) {
                            previousFragment.onRemoveFromParent();
                            parent.removeView(fragmentView);
                        }
                    }
                    if (previousFragment.actionBar != null && previousFragment.actionBar.getAddToContainer()) {
                        if (this.removeActionBarExtraHeight) {
                            previousFragment.actionBar.setOccupyStatusBar(false);
                        }
                        parent = (ViewGroup) previousFragment.actionBar.getParent();
                        if (parent != null) {
                            parent.removeView(previousFragment.actionBar);
                        }
                        this.containerView.addView(previousFragment.actionBar);
                        previousFragment.actionBar.setTitleOverlayText(this.titleOverlayText, this.subtitleOverlayText, this.overlayAction);
                    }
                    this.containerView.addView(fragmentView);
                    ViewGroup.LayoutParams layoutParams = fragmentView.getLayoutParams();
                    layoutParams.width = -1;
                    layoutParams.height = -1;
                    fragmentView.setLayoutParams(layoutParams);
                    previousFragment.onTransitionAnimationStart(true, true);
                    currentFragment.onTransitionAnimationStart(false, false);
                    previousFragment.onResume();
                    this.currentActionBar = previousFragment.actionBar;
                    if (!previousFragment.hasOwnBackground && fragmentView.getBackground() == null) {
                        fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    }
                    if (!needAnimation) {
                        closeLastFragmentInternalRemoveOld(currentFragment);
                    }
                    if (needAnimation) {
                        this.transitionAnimationStartTime = System.currentTimeMillis();
                        this.transitionAnimationInProgress = true;
                        final BaseFragment previousFragmentFinal = previousFragment;
                        this.onCloseAnimationEndRunnable = new Runnable() {
                            public void run() {
                                ActionBarLayout.this.closeLastFragmentInternalRemoveOld(currentFragment);
                                ActionBarLayout.this.containerViewBack.setTranslationX(0.0f);
                                currentFragment.onTransitionAnimationEnd(false, false);
                                previousFragmentFinal.onTransitionAnimationEnd(true, true);
                                previousFragmentFinal.onBecomeFullyVisible();
                            }
                        };
                        AnimatorSet animation = currentFragment.onCustomTransitionAnimation(false, new Runnable() {
                            public void run() {
                                ActionBarLayout.this.onAnimationEndCheck(false);
                            }
                        });
                        if (animation == null) {
                            if (!this.containerView.isKeyboardVisible) {
                                if (!this.containerViewBack.isKeyboardVisible) {
                                    startLayoutAnimation(false, true);
                                }
                            }
                            this.waitingForKeyboardCloseRunnable = new Runnable() {
                                public void run() {
                                    if (ActionBarLayout.this.waitingForKeyboardCloseRunnable == this) {
                                        ActionBarLayout.this.waitingForKeyboardCloseRunnable = null;
                                        ActionBarLayout.this.startLayoutAnimation(false, true);
                                    }
                                }
                            };
                            AndroidUtilities.runOnUIThread(this.waitingForKeyboardCloseRunnable, 200);
                        } else {
                            this.currentAnimation = animation;
                        }
                    } else {
                        currentFragment.onTransitionAnimationEnd(false, false);
                        previousFragment.onTransitionAnimationEnd(true, true);
                        previousFragment.onBecomeFullyVisible();
                    }
                } else if (this.useAlphaAnimations) {
                    this.transitionAnimationStartTime = System.currentTimeMillis();
                    this.transitionAnimationInProgress = true;
                    this.onCloseAnimationEndRunnable = new Runnable() {
                        public void run() {
                            ActionBarLayout.this.removeFragmentFromStackInternal(currentFragment);
                            ActionBarLayout.this.setVisibility(8);
                            if (ActionBarLayout.this.backgroundView != null) {
                                ActionBarLayout.this.backgroundView.setVisibility(8);
                            }
                            if (ActionBarLayout.this.drawerLayoutContainer != null) {
                                ActionBarLayout.this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                            }
                        }
                    };
                    ArrayList<Animator> animators = new ArrayList();
                    animators.add(ObjectAnimator.ofFloat(this, "alpha", new float[]{1.0f, 0.0f}));
                    if (this.backgroundView != null) {
                        animators.add(ObjectAnimator.ofFloat(this.backgroundView, "alpha", new float[]{1.0f, 0.0f}));
                    }
                    this.currentAnimation = new AnimatorSet();
                    this.currentAnimation.playTogether(animators);
                    this.currentAnimation.setInterpolator(this.accelerateDecelerateInterpolator);
                    this.currentAnimation.setDuration(200);
                    this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationStart(Animator animation) {
                            ActionBarLayout.this.transitionAnimationStartTime = System.currentTimeMillis();
                        }

                        public void onAnimationEnd(Animator animation) {
                            ActionBarLayout.this.onAnimationEndCheck(false);
                        }
                    });
                    this.currentAnimation.start();
                } else {
                    removeFragmentFromStackInternal(currentFragment);
                    setVisibility(8);
                    if (this.backgroundView != null) {
                        this.backgroundView.setVisibility(8);
                    }
                }
            }
        }
    }

    public void showLastFragment() {
        if (!this.fragmentsStack.isEmpty()) {
            ViewGroup parent;
            for (int a = 0; a < this.fragmentsStack.size() - 1; a++) {
                BaseFragment previousFragment = (BaseFragment) this.fragmentsStack.get(a);
                if (previousFragment.actionBar != null && previousFragment.actionBar.getAddToContainer()) {
                    parent = (ViewGroup) previousFragment.actionBar.getParent();
                    if (parent != null) {
                        parent.removeView(previousFragment.actionBar);
                    }
                }
                if (previousFragment.fragmentView != null) {
                    parent = (ViewGroup) previousFragment.fragmentView.getParent();
                    if (parent != null) {
                        previousFragment.onPause();
                        previousFragment.onRemoveFromParent();
                        parent.removeView(previousFragment.fragmentView);
                    }
                }
            }
            BaseFragment previousFragment2 = (BaseFragment) this.fragmentsStack.get(this.fragmentsStack.size() - 1);
            previousFragment2.setParentLayout(this);
            View fragmentView = previousFragment2.fragmentView;
            if (fragmentView == null) {
                fragmentView = previousFragment2.createView(this.parentActivity);
            } else {
                parent = (ViewGroup) fragmentView.getParent();
                if (parent != null) {
                    previousFragment2.onRemoveFromParent();
                    parent.removeView(fragmentView);
                }
            }
            if (previousFragment2.actionBar != null && previousFragment2.actionBar.getAddToContainer()) {
                if (this.removeActionBarExtraHeight) {
                    previousFragment2.actionBar.setOccupyStatusBar(false);
                }
                ViewGroup parent2 = (ViewGroup) previousFragment2.actionBar.getParent();
                if (parent2 != null) {
                    parent2.removeView(previousFragment2.actionBar);
                }
                this.containerView.addView(previousFragment2.actionBar);
                previousFragment2.actionBar.setTitleOverlayText(this.titleOverlayText, this.subtitleOverlayText, this.overlayAction);
            }
            this.containerView.addView(fragmentView, LayoutHelper.createLinear(-1, -1));
            previousFragment2.onResume();
            this.currentActionBar = previousFragment2.actionBar;
            if (!previousFragment2.hasOwnBackground && fragmentView.getBackground() == null) {
                fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            }
        }
    }

    private void removeFragmentFromStackInternal(BaseFragment fragment) {
        fragment.onPause();
        fragment.onFragmentDestroy();
        fragment.setParentLayout(null);
        this.fragmentsStack.remove(fragment);
    }

    public void removeFragmentFromStack(int num) {
        if (num < this.fragmentsStack.size()) {
            removeFragmentFromStackInternal((BaseFragment) this.fragmentsStack.get(num));
        }
    }

    public void removeFragmentFromStack(BaseFragment fragment) {
        if (this.useAlphaAnimations && this.fragmentsStack.size() == 1 && AndroidUtilities.isTablet()) {
            closeLastFragment(true);
        } else {
            removeFragmentFromStackInternal(fragment);
        }
    }

    public void removeAllFragments() {
        for (int a = 0; a < this.fragmentsStack.size(); a = (a - 1) + 1) {
            removeFragmentFromStackInternal((BaseFragment) this.fragmentsStack.get(a));
        }
    }

    @Keep
    public void setThemeAnimationValue(float value) {
        float f = value;
        this.themeAnimationValue = f;
        if (this.themeAnimatorDescriptions != null) {
            int i = 0;
            while (i < r0.themeAnimatorDescriptions.length) {
                int rE = Color.red(r0.animateEndColors[i]);
                int gE = Color.green(r0.animateEndColors[i]);
                int bE = Color.blue(r0.animateEndColors[i]);
                int aE = Color.alpha(r0.animateEndColors[i]);
                int rS = Color.red(r0.animateStartColors[i]);
                int gS = Color.green(r0.animateStartColors[i]);
                int bS = Color.blue(r0.animateStartColors[i]);
                int aS = Color.alpha(r0.animateStartColors[i]);
                int a = Math.min(255, (int) (((float) aS) + (((float) (aE - aS)) * f)));
                int r = Math.min(255, (int) (((float) rS) + (((float) (rE - rS)) * f)));
                int g = Math.min(255, (int) (((float) gS) + (((float) (gE - gS)) * f)));
                r0.themeAnimatorDescriptions[i].setColor(Color.argb(a, r, g, Math.min(255, (int) (((float) bS) + (((float) (bE - bS)) * f)))), false, false);
                i++;
                f = value;
            }
            if (r0.themeAnimatorDelegate != null) {
                r0.themeAnimatorDelegate.didSetColor();
            }
        }
    }

    @Keep
    public float getThemeAnimationValue() {
        return this.themeAnimationValue;
    }

    public void animateThemedValues(ThemeInfo theme) {
        if (!this.transitionAnimationInProgress) {
            if (!this.startedTracking) {
                if (this.themeAnimatorSet != null) {
                    this.themeAnimatorSet.cancel();
                    this.themeAnimatorSet = null;
                }
                BaseFragment fragment = getLastFragment();
                if (fragment != null) {
                    int a;
                    this.themeAnimatorDescriptions = fragment.getThemeDescriptions();
                    int a2 = 0;
                    this.animateStartColors = new int[this.themeAnimatorDescriptions.length];
                    for (a = 0; a < this.themeAnimatorDescriptions.length; a++) {
                        this.animateStartColors[a] = this.themeAnimatorDescriptions[a].getSetColor();
                        ThemeDescriptionDelegate delegate = this.themeAnimatorDescriptions[a].setDelegateDisabled();
                        if (this.themeAnimatorDelegate == null && delegate != null) {
                            this.themeAnimatorDelegate = delegate;
                        }
                    }
                    Theme.applyTheme(theme, true);
                    this.animateEndColors = new int[this.themeAnimatorDescriptions.length];
                    for (a = 0; a < this.themeAnimatorDescriptions.length; a++) {
                        this.animateEndColors[a] = this.themeAnimatorDescriptions[a].getSetColor();
                    }
                    this.themeAnimatorSet = new AnimatorSet();
                    this.themeAnimatorSet.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            if (animation.equals(ActionBarLayout.this.themeAnimatorSet)) {
                                ActionBarLayout.this.themeAnimatorDescriptions = null;
                                ActionBarLayout.this.themeAnimatorDelegate = null;
                                ActionBarLayout.this.themeAnimatorSet = null;
                                ActionBarLayout.this.animateStartColors = null;
                                ActionBarLayout.this.animateEndColors = null;
                            }
                        }

                        public void onAnimationCancel(Animator animation) {
                            if (animation.equals(ActionBarLayout.this.themeAnimatorSet)) {
                                ActionBarLayout.this.themeAnimatorDescriptions = null;
                                ActionBarLayout.this.themeAnimatorSet = null;
                                ActionBarLayout.this.animateStartColors = null;
                                ActionBarLayout.this.animateEndColors = null;
                            }
                        }
                    });
                    this.themeAnimatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, "themeAnimationValue", new float[]{0.0f, 1.0f})});
                    this.themeAnimatorSet.setDuration(200);
                    this.themeAnimatorSet.start();
                    while (true) {
                        a = a2;
                        if (a >= this.fragmentsStack.size() - 1) {
                            break;
                        }
                        fragment = (BaseFragment) this.fragmentsStack.get(a);
                        fragment.clearViews();
                        fragment.setParentLayout(this);
                        a2 = a + 1;
                    }
                }
                return;
            }
        }
        this.animateThemeAfterAnimation = true;
        this.animateSetThemeAfterAnimation = theme;
    }

    public void rebuildAllFragmentViews(boolean last, boolean showLastAfter) {
        if (!this.transitionAnimationInProgress) {
            if (!this.startedTracking) {
                for (int a = 0; a < this.fragmentsStack.size() - (last ^ 1); a++) {
                    ((BaseFragment) this.fragmentsStack.get(a)).clearViews();
                    ((BaseFragment) this.fragmentsStack.get(a)).setParentLayout(this);
                }
                if (this.delegate != null) {
                    this.delegate.onRebuildAllFragments(this, last);
                }
                if (showLastAfter) {
                    showLastFragment();
                }
                return;
            }
        }
        this.rebuildAfterAnimation = true;
        this.rebuildLastAfterAnimation = last;
        this.showLastAfterAnimation = showLastAfter;
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (!(keyCode != 82 || checkTransitionAnimation() || this.startedTracking || this.currentActionBar == null)) {
            this.currentActionBar.onMenuButtonPressed();
        }
        return super.onKeyUp(keyCode, event);
    }

    public void onActionModeStarted(Object mode) {
        if (this.currentActionBar != null) {
            this.currentActionBar.setVisibility(8);
        }
        this.inActionMode = true;
    }

    public void onActionModeFinished(Object mode) {
        if (this.currentActionBar != null) {
            this.currentActionBar.setVisibility(0);
        }
        this.inActionMode = false;
    }

    private void onCloseAnimationEnd() {
        if (this.transitionAnimationInProgress && this.onCloseAnimationEndRunnable != null) {
            this.transitionAnimationInProgress = false;
            this.transitionAnimationStartTime = 0;
            this.onCloseAnimationEndRunnable.run();
            this.onCloseAnimationEndRunnable = null;
            checkNeedRebuild();
        }
    }

    private void checkNeedRebuild() {
        if (this.rebuildAfterAnimation) {
            rebuildAllFragmentViews(this.rebuildLastAfterAnimation, this.showLastAfterAnimation);
            this.rebuildAfterAnimation = false;
        } else if (this.animateThemeAfterAnimation) {
            animateThemedValues(this.animateSetThemeAfterAnimation);
            this.animateSetThemeAfterAnimation = null;
            this.animateThemeAfterAnimation = false;
        }
    }

    private void onOpenAnimationEnd() {
        if (this.transitionAnimationInProgress && this.onOpenAnimationEndRunnable != null) {
            this.transitionAnimationInProgress = false;
            this.transitionAnimationStartTime = 0;
            this.onOpenAnimationEndRunnable.run();
            this.onOpenAnimationEndRunnable = null;
            checkNeedRebuild();
        }
    }

    public void startActivityForResult(Intent intent, int requestCode) {
        if (this.parentActivity != null) {
            if (this.transitionAnimationInProgress) {
                if (this.currentAnimation != null) {
                    this.currentAnimation.cancel();
                    this.currentAnimation = null;
                }
                if (this.onCloseAnimationEndRunnable != null) {
                    onCloseAnimationEnd();
                } else if (this.onOpenAnimationEndRunnable != null) {
                    onOpenAnimationEnd();
                }
                this.containerView.invalidate();
                if (intent != null) {
                    this.parentActivity.startActivityForResult(intent, requestCode);
                }
            } else if (intent != null) {
                this.parentActivity.startActivityForResult(intent, requestCode);
            }
        }
    }

    public void setUseAlphaAnimations(boolean value) {
        this.useAlphaAnimations = value;
    }

    public void setBackgroundView(View view) {
        this.backgroundView = view;
    }

    public void setDrawerLayoutContainer(DrawerLayoutContainer layout) {
        this.drawerLayoutContainer = layout;
    }

    public DrawerLayoutContainer getDrawerLayoutContainer() {
        return this.drawerLayoutContainer;
    }

    public void setRemoveActionBarExtraHeight(boolean value) {
        this.removeActionBarExtraHeight = value;
    }

    public void setTitleOverlayText(String title, String subtitle, Runnable action) {
        this.titleOverlayText = title;
        this.subtitleOverlayText = subtitle;
        this.overlayAction = action;
        for (int a = 0; a < this.fragmentsStack.size(); a++) {
            BaseFragment fragment = (BaseFragment) this.fragmentsStack.get(a);
            if (fragment.actionBar != null) {
                fragment.actionBar.setTitleOverlayText(this.titleOverlayText, this.subtitleOverlayText, action);
            }
        }
    }

    public boolean extendActionMode(Menu menu) {
        return !this.fragmentsStack.isEmpty() && ((BaseFragment) this.fragmentsStack.get(this.fragmentsStack.size() - 1)).extendActionMode(menu);
    }

    public boolean hasOverlappingRendering() {
        return false;
    }
}
