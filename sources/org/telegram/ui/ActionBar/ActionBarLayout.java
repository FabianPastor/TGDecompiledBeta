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
import org.telegram.messenger.C0446R;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.exoplayer2.C0542C;
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
    class C07154 extends AnimatorListenerAdapter {
        C07154() {
        }

        public void onAnimationEnd(Animator animator) {
            ActionBarLayout.this.onAnimationEndCheck(false);
        }
    }

    /* renamed from: org.telegram.ui.ActionBar.ActionBarLayout$6 */
    class C07176 implements Runnable {
        C07176() {
        }

        public void run() {
            ActionBarLayout.this.onAnimationEndCheck(false);
        }
    }

    /* renamed from: org.telegram.ui.ActionBar.ActionBarLayout$7 */
    class C07187 implements Runnable {
        C07187() {
        }

        public void run() {
            if (ActionBarLayout.this.waitingForKeyboardCloseRunnable == this) {
                ActionBarLayout.this.waitingForKeyboardCloseRunnable = null;
                ActionBarLayout.this.startLayoutAnimation(true, true);
            }
        }
    }

    /* renamed from: org.telegram.ui.ActionBar.ActionBarLayout$8 */
    class C07198 implements Runnable {
        C07198() {
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

        public boolean hasOverlappingRendering() {
            return false;
        }

        public LinearLayoutContainer(Context context) {
            super(context);
            setOrientation(1);
        }

        protected boolean drawChild(Canvas canvas, View view, long j) {
            if (view instanceof ActionBar) {
                return super.drawChild(canvas, view, j);
            }
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = getChildAt(i);
                if (childAt != view) {
                    if ((childAt instanceof ActionBar) && childAt.getVisibility() == 0) {
                        if (((ActionBar) childAt).getCastShadows()) {
                            childCount = childAt.getMeasuredHeight();
                            view = super.drawChild(canvas, view, j);
                            if (!(childCount == 0 || ActionBarLayout.headerShadowDrawable == null)) {
                                ActionBarLayout.headerShadowDrawable.setBounds(0, childCount, getMeasuredWidth(), ActionBarLayout.headerShadowDrawable.getIntrinsicHeight() + childCount);
                                ActionBarLayout.headerShadowDrawable.draw(canvas);
                            }
                            return view;
                        }
                        childCount = 0;
                        view = super.drawChild(canvas, view, j);
                        ActionBarLayout.headerShadowDrawable.setBounds(0, childCount, getMeasuredWidth(), ActionBarLayout.headerShadowDrawable.getIntrinsicHeight() + childCount);
                        ActionBarLayout.headerShadowDrawable.draw(canvas);
                        return view;
                    }
                }
            }
            childCount = 0;
            view = super.drawChild(canvas, view, j);
            ActionBarLayout.headerShadowDrawable.setBounds(0, childCount, getMeasuredWidth(), ActionBarLayout.headerShadowDrawable.getIntrinsicHeight() + childCount);
            ActionBarLayout.headerShadowDrawable.draw(canvas);
            return view;
        }

        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            z = getRootView();
            getWindowVisibleDisplayFrame(this.rect);
            i3 = 0;
            if (((z.getHeight() - (this.rect.top != 0 ? AndroidUtilities.statusBarHeight : 0)) - AndroidUtilities.getViewInset(z)) - (this.rect.bottom - this.rect.top) > 0) {
                i3 = 1;
            }
            this.isKeyboardVisible = i3;
            if (ActionBarLayout.this.waitingForKeyboardCloseRunnable && !ActionBarLayout.this.containerView.isKeyboardVisible && !ActionBarLayout.this.containerViewBack.isKeyboardVisible) {
                AndroidUtilities.cancelRunOnUIThread(ActionBarLayout.this.waitingForKeyboardCloseRunnable);
                ActionBarLayout.this.waitingForKeyboardCloseRunnable.run();
                ActionBarLayout.this.waitingForKeyboardCloseRunnable = 0;
            }
        }
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    public ActionBarLayout(Context context) {
        super(context);
        this.parentActivity = (Activity) context;
        if (layerShadowDrawable == null) {
            layerShadowDrawable = getResources().getDrawable(C0446R.drawable.layer_shadow);
            headerShadowDrawable = getResources().getDrawable(C0446R.drawable.header_shadow).mutate();
            scrimPaint = new Paint();
        }
    }

    public void init(ArrayList<BaseFragment> arrayList) {
        this.fragmentsStack = arrayList;
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
        arrayList = this.fragmentsStack.iterator();
        while (arrayList.hasNext()) {
            ((BaseFragment) arrayList.next()).setParentLayout(this);
        }
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (!this.fragmentsStack.isEmpty()) {
            BaseFragment baseFragment = (BaseFragment) this.fragmentsStack.get(this.fragmentsStack.size() - 1);
            baseFragment.onConfigurationChanged(configuration);
            if (baseFragment.visibleDialog instanceof BottomSheet) {
                ((BottomSheet) baseFragment.visibleDialog).onConfigurationChanged(configuration);
            }
        }
    }

    public void drawHeaderShadow(Canvas canvas, int i) {
        if (headerShadowDrawable != null) {
            headerShadowDrawable.setBounds(0, i, getMeasuredWidth(), headerShadowDrawable.getIntrinsicHeight() + i);
            headerShadowDrawable.draw(canvas);
        }
    }

    @Keep
    public void setInnerTranslationX(float f) {
        this.innerTranslationX = f;
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

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (!(this.animationInProgress || checkTransitionAnimation())) {
            if (onTouchEvent(motionEvent) == null) {
                return null;
            }
        }
        return true;
    }

    public void requestDisallowInterceptTouchEvent(boolean z) {
        onTouchEvent(null);
        super.requestDisallowInterceptTouchEvent(z);
    }

    public boolean dispatchKeyEventPreIme(KeyEvent keyEvent) {
        if (keyEvent != null && keyEvent.getKeyCode() == 4) {
            boolean z = true;
            if (keyEvent.getAction() == 1) {
                if (this.delegate == null || !this.delegate.onPreIme()) {
                    if (super.dispatchKeyEventPreIme(keyEvent) == null) {
                        z = false;
                    }
                }
                return z;
            }
        }
        return super.dispatchKeyEventPreIme(keyEvent);
    }

    protected boolean drawChild(Canvas canvas, View view, long j) {
        int width = (getWidth() - getPaddingLeft()) - getPaddingRight();
        int paddingRight = ((int) this.innerTranslationX) + getPaddingRight();
        int paddingLeft = getPaddingLeft();
        int paddingLeft2 = getPaddingLeft() + width;
        if (view == this.containerViewBack) {
            paddingLeft2 = paddingRight;
        } else if (view == this.containerView) {
            paddingLeft = paddingRight;
        }
        int save = canvas.save();
        if (!this.transitionAnimationInProgress) {
            canvas.clipRect(paddingLeft, 0, paddingLeft2, getHeight());
        }
        j = super.drawChild(canvas, view, j);
        canvas.restoreToCount(save);
        if (paddingRight != 0) {
            if (view == this.containerView) {
                float max = Math.max(0.0f, Math.min(((float) (width - paddingRight)) / ((float) AndroidUtilities.dp(20.0f)), 1.0f));
                layerShadowDrawable.setBounds(paddingRight - layerShadowDrawable.getIntrinsicWidth(), view.getTop(), paddingRight, view.getBottom());
                layerShadowDrawable.setAlpha((int) (255.0f * max));
                layerShadowDrawable.draw(canvas);
            } else if (view == this.containerViewBack) {
                view = Math.min(0.8f, ((float) (width - paddingRight)) / ((float) width));
                if (view < null) {
                    view = null;
                }
                scrimPaint.setColor(((int) (153.0f * view)) << 24);
                canvas.drawRect((float) paddingLeft, 0.0f, (float) paddingLeft2, (float) getHeight(), scrimPaint);
            }
        }
        return j;
    }

    public void setDelegate(ActionBarLayoutDelegate actionBarLayoutDelegate) {
        this.delegate = actionBarLayoutDelegate;
    }

    private void onSlideAnimationEnd(boolean z) {
        BaseFragment baseFragment;
        if (z) {
            ViewGroup viewGroup;
            baseFragment = (BaseFragment) this.fragmentsStack.get(this.fragmentsStack.size() - 2);
            baseFragment.onPause();
            if (baseFragment.fragmentView != null) {
                viewGroup = (ViewGroup) baseFragment.fragmentView.getParent();
                if (viewGroup != null) {
                    baseFragment.onRemoveFromParent();
                    viewGroup.removeView(baseFragment.fragmentView);
                }
            }
            if (baseFragment.actionBar != null && baseFragment.actionBar.getAddToContainer()) {
                viewGroup = (ViewGroup) baseFragment.actionBar.getParent();
                if (viewGroup != null) {
                    viewGroup.removeView(baseFragment.actionBar);
                }
            }
        } else {
            baseFragment = (BaseFragment) this.fragmentsStack.get(this.fragmentsStack.size() - 1);
            baseFragment.onPause();
            baseFragment.onFragmentDestroy();
            baseFragment.setParentLayout(null);
            this.fragmentsStack.remove(this.fragmentsStack.size() - 1);
            z = this.containerView;
            this.containerView = this.containerViewBack;
            this.containerViewBack = z;
            bringChildToFront(this.containerView);
            baseFragment = (BaseFragment) this.fragmentsStack.get(this.fragmentsStack.size() - 1);
            this.currentActionBar = baseFragment.actionBar;
            baseFragment.onResume();
            baseFragment.onBecomeFullyVisible();
        }
        this.containerViewBack.setVisibility(8);
        this.startedTracking = false;
        this.animationInProgress = false;
        this.containerView.setTranslationX(0.0f);
        this.containerViewBack.setTranslationX(0.0f);
        setInnerTranslationX(0.0f);
    }

    private void prepareForMoving(MotionEvent motionEvent) {
        this.maybeStartTracking = false;
        this.startedTracking = true;
        this.startedTrackingX = (int) motionEvent.getX();
        this.containerViewBack.setVisibility(0);
        this.beginTrackingSent = false;
        BaseFragment baseFragment = (BaseFragment) this.fragmentsStack.get(this.fragmentsStack.size() - 2);
        View view = baseFragment.fragmentView;
        if (view == null) {
            view = baseFragment.createView(this.parentActivity);
        }
        ViewGroup viewGroup = (ViewGroup) view.getParent();
        if (viewGroup != null) {
            baseFragment.onRemoveFromParent();
            viewGroup.removeView(view);
        }
        if (baseFragment.actionBar != null && baseFragment.actionBar.getAddToContainer()) {
            viewGroup = (ViewGroup) baseFragment.actionBar.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(baseFragment.actionBar);
            }
            if (this.removeActionBarExtraHeight) {
                baseFragment.actionBar.setOccupyStatusBar(false);
            }
            this.containerViewBack.addView(baseFragment.actionBar);
            baseFragment.actionBar.setTitleOverlayText(this.titleOverlayText, this.subtitleOverlayText, this.overlayAction);
        }
        this.containerViewBack.addView(view);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        view.setLayoutParams(layoutParams);
        if (!baseFragment.hasOwnBackground && view.getBackground() == null) {
            view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        }
        baseFragment.onResume();
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (checkTransitionAnimation() || this.inActionMode || this.animationInProgress) {
            return false;
        }
        if (this.fragmentsStack.size() > 1) {
            if (motionEvent == null || motionEvent.getAction() != 0 || this.startedTracking || this.maybeStartTracking) {
                float f;
                if (motionEvent != null && motionEvent.getAction() == 2 && motionEvent.getPointerId(0) == this.startedTrackingPointerId) {
                    if (this.velocityTracker == null) {
                        this.velocityTracker = VelocityTracker.obtain();
                    }
                    int max = Math.max(0, (int) (motionEvent.getX() - ((float) this.startedTrackingX)));
                    int abs = Math.abs(((int) motionEvent.getY()) - this.startedTrackingY);
                    this.velocityTracker.addMovement(motionEvent);
                    if (this.maybeStartTracking && !this.startedTracking && ((float) max) >= AndroidUtilities.getPixelsInCM(0.4f, true) && Math.abs(max) / 3 > abs) {
                        prepareForMoving(motionEvent);
                    } else if (this.startedTracking != null) {
                        if (this.beginTrackingSent == null) {
                            if (this.parentActivity.getCurrentFocus() != null) {
                                AndroidUtilities.hideKeyboard(this.parentActivity.getCurrentFocus());
                            }
                            ((BaseFragment) this.fragmentsStack.get(this.fragmentsStack.size() - 1)).onBeginSlide();
                            this.beginTrackingSent = true;
                        }
                        f = (float) max;
                        this.containerView.setTranslationX(f);
                        setInnerTranslationX(f);
                    }
                } else if (motionEvent != null && motionEvent.getPointerId(0) == this.startedTrackingPointerId && (motionEvent.getAction() == 3 || motionEvent.getAction() == 1 || motionEvent.getAction() == 6)) {
                    float yVelocity;
                    if (this.velocityTracker == null) {
                        this.velocityTracker = VelocityTracker.obtain();
                    }
                    this.velocityTracker.computeCurrentVelocity(1000);
                    if (!this.startedTracking && ((BaseFragment) this.fragmentsStack.get(this.fragmentsStack.size() - 1)).swipeBackEnabled) {
                        f = this.velocityTracker.getXVelocity();
                        yVelocity = this.velocityTracker.getYVelocity();
                        if (f >= 3500.0f && f > Math.abs(yVelocity)) {
                            prepareForMoving(motionEvent);
                            if (this.beginTrackingSent == null) {
                                if (((Activity) getContext()).getCurrentFocus() != null) {
                                    AndroidUtilities.hideKeyboard(((Activity) getContext()).getCurrentFocus());
                                }
                                this.beginTrackingSent = true;
                            }
                        }
                    }
                    if (this.startedTracking != null) {
                        motionEvent = this.containerView.getX();
                        AnimatorSet animatorSet = new AnimatorSet();
                        yVelocity = this.velocityTracker.getXVelocity();
                        final boolean z = motionEvent < ((float) this.containerView.getMeasuredWidth()) / 3.0f && (yVelocity < 3500.0f || yVelocity < this.velocityTracker.getYVelocity());
                        Animator[] animatorArr;
                        if (z) {
                            animatorArr = new Animator[2];
                            animatorArr[0] = ObjectAnimator.ofFloat(this.containerView, "translationX", new float[]{0.0f});
                            animatorArr[1] = ObjectAnimator.ofFloat(this, "innerTranslationX", new float[]{0.0f});
                            animatorSet.playTogether(animatorArr);
                        } else {
                            motionEvent = ((float) this.containerView.getMeasuredWidth()) - motionEvent;
                            animatorArr = new Animator[2];
                            animatorArr[0] = ObjectAnimator.ofFloat(this.containerView, "translationX", new float[]{(float) this.containerView.getMeasuredWidth()});
                            animatorArr[1] = ObjectAnimator.ofFloat(this, "innerTranslationX", new float[]{(float) this.containerView.getMeasuredWidth()});
                            animatorSet.playTogether(animatorArr);
                        }
                        animatorSet.setDuration((long) Math.max((int) ((200.0f / ((float) this.containerView.getMeasuredWidth())) * motionEvent), 50));
                        animatorSet.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                ActionBarLayout.this.onSlideAnimationEnd(z);
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
                } else if (motionEvent == null) {
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
                this.startedTrackingPointerId = motionEvent.getPointerId(0);
                this.maybeStartTracking = true;
                this.startedTrackingX = (int) motionEvent.getX();
                this.startedTrackingY = (int) motionEvent.getY();
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

    private void onAnimationEndCheck(boolean z) {
        onCloseAnimationEnd();
        onOpenAnimationEnd();
        if (this.waitingForKeyboardCloseRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(this.waitingForKeyboardCloseRunnable);
            this.waitingForKeyboardCloseRunnable = null;
        }
        if (this.currentAnimation != null) {
            if (z) {
                this.currentAnimation.cancel();
            }
            this.currentAnimation = null;
        }
        if (this.animationRunnable) {
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

    private void presentFragmentInternalRemoveOld(boolean z, BaseFragment baseFragment) {
        if (baseFragment != null) {
            baseFragment.onPause();
            if (z) {
                baseFragment.onFragmentDestroy();
                baseFragment.setParentLayout(false);
                this.fragmentsStack.remove(baseFragment);
            } else {
                ViewGroup viewGroup;
                if (baseFragment.fragmentView) {
                    viewGroup = (ViewGroup) baseFragment.fragmentView.getParent();
                    if (viewGroup != null) {
                        baseFragment.onRemoveFromParent();
                        viewGroup.removeView(baseFragment.fragmentView);
                    }
                }
                if (baseFragment.actionBar && baseFragment.actionBar.getAddToContainer()) {
                    viewGroup = (ViewGroup) baseFragment.actionBar.getParent();
                    if (viewGroup != null) {
                        viewGroup.removeView(baseFragment.actionBar);
                    }
                }
            }
            this.containerViewBack.setVisibility(8);
        }
    }

    public boolean presentFragment(BaseFragment baseFragment) {
        return presentFragment(baseFragment, false, false, true);
    }

    public boolean presentFragment(BaseFragment baseFragment, boolean z) {
        return presentFragment(baseFragment, z, false, true);
    }

    private void startLayoutAnimation(final boolean z, final boolean z2) {
        if (z2) {
            this.animationProgress = 0.0f;
            this.lastFrameTime = System.nanoTime() / C0542C.MICROS_PER_SECOND;
        }
        Runnable c07132 = new Runnable() {
            public void run() {
                if (ActionBarLayout.this.animationRunnable == this) {
                    ActionBarLayout.this.animationRunnable = null;
                    if (z2) {
                        ActionBarLayout.this.transitionAnimationStartTime = System.currentTimeMillis();
                    }
                    long nanoTime = System.nanoTime() / C0542C.MICROS_PER_SECOND;
                    long access$700 = nanoTime - ActionBarLayout.this.lastFrameTime;
                    long j = 18;
                    if (access$700 <= 18) {
                        j = access$700;
                    }
                    ActionBarLayout.this.lastFrameTime = nanoTime;
                    ActionBarLayout.this.animationProgress = ActionBarLayout.this.animationProgress + (((float) j) / 150.0f);
                    if (ActionBarLayout.this.animationProgress > 1.0f) {
                        ActionBarLayout.this.animationProgress = 1.0f;
                    }
                    float interpolation = ActionBarLayout.this.decelerateInterpolator.getInterpolation(ActionBarLayout.this.animationProgress);
                    if (z) {
                        ActionBarLayout.this.containerView.setAlpha(interpolation);
                        ActionBarLayout.this.containerView.setTranslationX(((float) AndroidUtilities.dp(48.0f)) * (1.0f - interpolation));
                    } else {
                        ActionBarLayout.this.containerViewBack.setAlpha(1.0f - interpolation);
                        ActionBarLayout.this.containerViewBack.setTranslationX(((float) AndroidUtilities.dp(48.0f)) * interpolation);
                    }
                    if (ActionBarLayout.this.animationProgress < 1.0f) {
                        ActionBarLayout.this.startLayoutAnimation(z, false);
                    } else {
                        ActionBarLayout.this.onAnimationEndCheck(false);
                    }
                }
            }
        };
        this.animationRunnable = c07132;
        AndroidUtilities.runOnUIThread(c07132);
    }

    public void resumeDelayedFragmentAnimation() {
        if (this.delayedOpenAnimationRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(this.delayedOpenAnimationRunnable);
            this.delayedOpenAnimationRunnable.run();
            this.delayedOpenAnimationRunnable = null;
        }
    }

    public boolean presentFragment(final BaseFragment baseFragment, final boolean z, boolean z2, boolean z3) {
        if (!checkTransitionAnimation() && (this.delegate == null || !z3 || this.delegate.needPresentFragment(baseFragment, z, z2, this))) {
            if (baseFragment.onFragmentCreate()) {
                ViewGroup viewGroup;
                if (this.parentActivity.getCurrentFocus()) {
                    AndroidUtilities.hideKeyboard(this.parentActivity.getCurrentFocus());
                }
                z2 = !z2 && MessagesController.getGlobalMainSettings().getBoolean("view_animations", true);
                final BaseFragment baseFragment2 = !this.fragmentsStack.isEmpty() ? (BaseFragment) this.fragmentsStack.get(this.fragmentsStack.size() - 1) : null;
                baseFragment.setParentLayout(this);
                View view = baseFragment.fragmentView;
                if (view == null) {
                    view = baseFragment.createView(this.parentActivity);
                } else {
                    viewGroup = (ViewGroup) view.getParent();
                    if (viewGroup != null) {
                        baseFragment.onRemoveFromParent();
                        viewGroup.removeView(view);
                    }
                }
                if (baseFragment.actionBar != null && baseFragment.actionBar.getAddToContainer()) {
                    if (this.removeActionBarExtraHeight) {
                        baseFragment.actionBar.setOccupyStatusBar(false);
                    }
                    viewGroup = (ViewGroup) baseFragment.actionBar.getParent();
                    if (viewGroup != null) {
                        viewGroup.removeView(baseFragment.actionBar);
                    }
                    this.containerViewBack.addView(baseFragment.actionBar);
                    baseFragment.actionBar.setTitleOverlayText(this.titleOverlayText, this.subtitleOverlayText, this.overlayAction);
                }
                this.containerViewBack.addView(view);
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.width = -1;
                layoutParams.height = -1;
                view.setLayoutParams(layoutParams);
                this.fragmentsStack.add(baseFragment);
                baseFragment.onResume();
                this.currentActionBar = baseFragment.actionBar;
                if (!baseFragment.hasOwnBackground && view.getBackground() == null) {
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                }
                LinearLayoutContainer linearLayoutContainer = this.containerView;
                this.containerView = this.containerViewBack;
                this.containerViewBack = linearLayoutContainer;
                this.containerView.setVisibility(0);
                setInnerTranslationX(0.0f);
                bringChildToFront(this.containerView);
                if (!z2) {
                    presentFragmentInternalRemoveOld(z, baseFragment2);
                    if (this.backgroundView != null) {
                        this.backgroundView.setVisibility(0);
                    }
                }
                if (!z2) {
                    if (this.backgroundView) {
                        this.backgroundView.setAlpha(1.0f);
                        this.backgroundView.setVisibility(0);
                    }
                    baseFragment.onTransitionAnimationStart(true, false);
                    baseFragment.onTransitionAnimationEnd(true, false);
                    baseFragment.onBecomeFullyVisible();
                } else if (this.useAlphaAnimations && this.fragmentsStack.size()) {
                    presentFragmentInternalRemoveOld(z, baseFragment2);
                    this.transitionAnimationStartTime = System.currentTimeMillis();
                    this.transitionAnimationInProgress = true;
                    this.onOpenAnimationEndRunnable = new Runnable() {
                        public void run() {
                            baseFragment.onTransitionAnimationEnd(true, false);
                            baseFragment.onBecomeFullyVisible();
                        }
                    };
                    z = new ArrayList();
                    z.add(ObjectAnimator.ofFloat(this, "alpha", new float[]{0.0f, 1.0f}));
                    if (this.backgroundView) {
                        this.backgroundView.setVisibility(0);
                        z.add(ObjectAnimator.ofFloat(this.backgroundView, "alpha", new float[]{0.0f, 1.0f}));
                    }
                    baseFragment.onTransitionAnimationStart(true, false);
                    this.currentAnimation = new AnimatorSet();
                    this.currentAnimation.playTogether(z);
                    this.currentAnimation.setInterpolator(this.accelerateDecelerateInterpolator);
                    this.currentAnimation.setDuration(200);
                    this.currentAnimation.addListener(new C07154());
                    this.currentAnimation.start();
                } else {
                    this.transitionAnimationStartTime = System.currentTimeMillis();
                    this.transitionAnimationInProgress = true;
                    this.onOpenAnimationEndRunnable = new Runnable() {
                        public void run() {
                            ActionBarLayout.this.presentFragmentInternalRemoveOld(z, baseFragment2);
                            baseFragment.onTransitionAnimationEnd(true, false);
                            baseFragment.onBecomeFullyVisible();
                            ActionBarLayout.this.containerView.setTranslationX(0.0f);
                        }
                    };
                    baseFragment.onTransitionAnimationStart(true, false);
                    z = baseFragment.onCustomTransitionAnimation(true, new C07176());
                    if (z) {
                        this.containerView.setAlpha(1.0f);
                        this.containerView.setTranslationX(0.0f);
                        this.currentAnimation = z;
                    } else {
                        this.containerView.setAlpha(0.0f);
                        this.containerView.setTranslationX(true);
                        if (!this.containerView.isKeyboardVisible) {
                            if (!this.containerViewBack.isKeyboardVisible) {
                                if (baseFragment.needDelayOpenAnimation() != null) {
                                    this.delayedOpenAnimationRunnable = new C07198();
                                    AndroidUtilities.runOnUIThread(this.delayedOpenAnimationRunnable, 200);
                                } else {
                                    startLayoutAnimation(true, true);
                                }
                            }
                        }
                        this.waitingForKeyboardCloseRunnable = new C07187();
                        AndroidUtilities.runOnUIThread(this.waitingForKeyboardCloseRunnable, 200);
                    }
                }
                return true;
            }
        }
        return false;
    }

    public boolean addFragmentToStack(BaseFragment baseFragment) {
        return addFragmentToStack(baseFragment, -1);
    }

    public boolean addFragmentToStack(BaseFragment baseFragment, int i) {
        if ((this.delegate != null && !this.delegate.needAddFragmentToStack(baseFragment, this)) || !baseFragment.onFragmentCreate()) {
            return null;
        }
        baseFragment.setParentLayout(this);
        if (i == -1) {
            if (this.fragmentsStack.isEmpty() == 0) {
                ViewGroup viewGroup;
                BaseFragment baseFragment2 = (BaseFragment) this.fragmentsStack.get(this.fragmentsStack.size() - 1);
                baseFragment2.onPause();
                if (baseFragment2.actionBar != null && baseFragment2.actionBar.getAddToContainer()) {
                    viewGroup = (ViewGroup) baseFragment2.actionBar.getParent();
                    if (viewGroup != null) {
                        viewGroup.removeView(baseFragment2.actionBar);
                    }
                }
                if (baseFragment2.fragmentView != null) {
                    viewGroup = (ViewGroup) baseFragment2.fragmentView.getParent();
                    if (viewGroup != null) {
                        baseFragment2.onRemoveFromParent();
                        viewGroup.removeView(baseFragment2.fragmentView);
                    }
                }
            }
            this.fragmentsStack.add(baseFragment);
        } else {
            this.fragmentsStack.add(i, baseFragment);
        }
        return true;
    }

    private void closeLastFragmentInternalRemoveOld(BaseFragment baseFragment) {
        baseFragment.onPause();
        baseFragment.onFragmentDestroy();
        baseFragment.setParentLayout(null);
        this.fragmentsStack.remove(baseFragment);
        this.containerViewBack.setVisibility(8);
        bringChildToFront(this.containerView);
    }

    public void closeLastFragment(boolean z) {
        if ((this.delegate == null || this.delegate.needCloseLastFragment(this)) && !checkTransitionAnimation()) {
            if (!this.fragmentsStack.isEmpty()) {
                if (this.parentActivity.getCurrentFocus() != null) {
                    AndroidUtilities.hideKeyboard(this.parentActivity.getCurrentFocus());
                }
                setInnerTranslationX(0.0f);
                z = z && MessagesController.getGlobalMainSettings().getBoolean("view_animations", true);
                final BaseFragment baseFragment = (BaseFragment) this.fragmentsStack.get(this.fragmentsStack.size() - 1);
                BaseFragment baseFragment2 = null;
                if (this.fragmentsStack.size() > 1) {
                    baseFragment2 = (BaseFragment) this.fragmentsStack.get(this.fragmentsStack.size() - 2);
                }
                if (baseFragment2 != null) {
                    ViewGroup viewGroup;
                    LinearLayoutContainer linearLayoutContainer = this.containerView;
                    this.containerView = this.containerViewBack;
                    this.containerViewBack = linearLayoutContainer;
                    this.containerView.setVisibility(0);
                    baseFragment2.setParentLayout(this);
                    View view = baseFragment2.fragmentView;
                    if (view == null) {
                        view = baseFragment2.createView(this.parentActivity);
                    } else {
                        viewGroup = (ViewGroup) view.getParent();
                        if (viewGroup != null) {
                            baseFragment2.onRemoveFromParent();
                            viewGroup.removeView(view);
                        }
                    }
                    if (baseFragment2.actionBar != null && baseFragment2.actionBar.getAddToContainer()) {
                        if (this.removeActionBarExtraHeight) {
                            baseFragment2.actionBar.setOccupyStatusBar(false);
                        }
                        viewGroup = (ViewGroup) baseFragment2.actionBar.getParent();
                        if (viewGroup != null) {
                            viewGroup.removeView(baseFragment2.actionBar);
                        }
                        this.containerView.addView(baseFragment2.actionBar);
                        baseFragment2.actionBar.setTitleOverlayText(this.titleOverlayText, this.subtitleOverlayText, this.overlayAction);
                    }
                    this.containerView.addView(view);
                    ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                    layoutParams.width = -1;
                    layoutParams.height = -1;
                    view.setLayoutParams(layoutParams);
                    baseFragment2.onTransitionAnimationStart(true, true);
                    baseFragment.onTransitionAnimationStart(false, false);
                    baseFragment2.onResume();
                    this.currentActionBar = baseFragment2.actionBar;
                    if (!baseFragment2.hasOwnBackground && view.getBackground() == null) {
                        view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    }
                    if (!z) {
                        closeLastFragmentInternalRemoveOld(baseFragment);
                    }
                    if (z) {
                        this.transitionAnimationStartTime = System.currentTimeMillis();
                        this.transitionAnimationInProgress = true;
                        this.onCloseAnimationEndRunnable = new Runnable() {
                            public void run() {
                                ActionBarLayout.this.closeLastFragmentInternalRemoveOld(baseFragment);
                                ActionBarLayout.this.containerViewBack.setTranslationX(0.0f);
                                baseFragment.onTransitionAnimationEnd(false, false);
                                baseFragment2.onTransitionAnimationEnd(true, true);
                                baseFragment2.onBecomeFullyVisible();
                            }
                        };
                        z = baseFragment.onCustomTransitionAnimation(false, new Runnable() {
                            public void run() {
                                ActionBarLayout.this.onAnimationEndCheck(false);
                            }
                        });
                        if (z) {
                            this.currentAnimation = z;
                        } else {
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
                        }
                    } else {
                        baseFragment.onTransitionAnimationEnd(false, false);
                        baseFragment2.onTransitionAnimationEnd(true, true);
                        baseFragment2.onBecomeFullyVisible();
                    }
                } else if (this.useAlphaAnimations) {
                    this.transitionAnimationStartTime = System.currentTimeMillis();
                    this.transitionAnimationInProgress = true;
                    this.onCloseAnimationEndRunnable = new Runnable() {
                        public void run() {
                            ActionBarLayout.this.removeFragmentFromStackInternal(baseFragment);
                            ActionBarLayout.this.setVisibility(8);
                            if (ActionBarLayout.this.backgroundView != null) {
                                ActionBarLayout.this.backgroundView.setVisibility(8);
                            }
                            if (ActionBarLayout.this.drawerLayoutContainer != null) {
                                ActionBarLayout.this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                            }
                        }
                    };
                    z = new ArrayList();
                    z.add(ObjectAnimator.ofFloat(this, "alpha", new float[]{1.0f, 0.0f}));
                    if (this.backgroundView != null) {
                        z.add(ObjectAnimator.ofFloat(this.backgroundView, "alpha", new float[]{1.0f, 0.0f}));
                    }
                    this.currentAnimation = new AnimatorSet();
                    this.currentAnimation.playTogether(z);
                    this.currentAnimation.setInterpolator(this.accelerateDecelerateInterpolator);
                    this.currentAnimation.setDuration(200);
                    this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationStart(Animator animator) {
                            ActionBarLayout.this.transitionAnimationStartTime = System.currentTimeMillis();
                        }

                        public void onAnimationEnd(Animator animator) {
                            ActionBarLayout.this.onAnimationEndCheck(false);
                        }
                    });
                    this.currentAnimation.start();
                } else {
                    removeFragmentFromStackInternal(baseFragment);
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
            ViewGroup viewGroup;
            for (int i = 0; i < this.fragmentsStack.size() - 1; i++) {
                BaseFragment baseFragment = (BaseFragment) this.fragmentsStack.get(i);
                if (baseFragment.actionBar != null && baseFragment.actionBar.getAddToContainer()) {
                    viewGroup = (ViewGroup) baseFragment.actionBar.getParent();
                    if (viewGroup != null) {
                        viewGroup.removeView(baseFragment.actionBar);
                    }
                }
                if (baseFragment.fragmentView != null) {
                    viewGroup = (ViewGroup) baseFragment.fragmentView.getParent();
                    if (viewGroup != null) {
                        baseFragment.onPause();
                        baseFragment.onRemoveFromParent();
                        viewGroup.removeView(baseFragment.fragmentView);
                    }
                }
            }
            BaseFragment baseFragment2 = (BaseFragment) this.fragmentsStack.get(this.fragmentsStack.size() - 1);
            baseFragment2.setParentLayout(this);
            View view = baseFragment2.fragmentView;
            if (view == null) {
                view = baseFragment2.createView(this.parentActivity);
            } else {
                viewGroup = (ViewGroup) view.getParent();
                if (viewGroup != null) {
                    baseFragment2.onRemoveFromParent();
                    viewGroup.removeView(view);
                }
            }
            if (baseFragment2.actionBar != null && baseFragment2.actionBar.getAddToContainer()) {
                if (this.removeActionBarExtraHeight) {
                    baseFragment2.actionBar.setOccupyStatusBar(false);
                }
                ViewGroup viewGroup2 = (ViewGroup) baseFragment2.actionBar.getParent();
                if (viewGroup2 != null) {
                    viewGroup2.removeView(baseFragment2.actionBar);
                }
                this.containerView.addView(baseFragment2.actionBar);
                baseFragment2.actionBar.setTitleOverlayText(this.titleOverlayText, this.subtitleOverlayText, this.overlayAction);
            }
            this.containerView.addView(view, LayoutHelper.createLinear(-1, -1));
            baseFragment2.onResume();
            this.currentActionBar = baseFragment2.actionBar;
            if (!baseFragment2.hasOwnBackground && view.getBackground() == null) {
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            }
        }
    }

    private void removeFragmentFromStackInternal(BaseFragment baseFragment) {
        baseFragment.onPause();
        baseFragment.onFragmentDestroy();
        baseFragment.setParentLayout(null);
        this.fragmentsStack.remove(baseFragment);
    }

    public void removeFragmentFromStack(int i) {
        if (i < this.fragmentsStack.size()) {
            removeFragmentFromStackInternal((BaseFragment) this.fragmentsStack.get(i));
        }
    }

    public void removeFragmentFromStack(BaseFragment baseFragment) {
        if (this.useAlphaAnimations && this.fragmentsStack.size() == 1 && AndroidUtilities.isTablet()) {
            closeLastFragment(true);
        } else {
            removeFragmentFromStackInternal(baseFragment);
        }
    }

    public void removeAllFragments() {
        while (this.fragmentsStack.size() > 0) {
            removeFragmentFromStackInternal((BaseFragment) this.fragmentsStack.get(0));
        }
    }

    @Keep
    public void setThemeAnimationValue(float f) {
        this.themeAnimationValue = f;
        if (this.themeAnimatorDescriptions != null) {
            for (int i = 0; i < this.themeAnimatorDescriptions.length; i++) {
                int red = Color.red(this.animateEndColors[i]);
                int green = Color.green(this.animateEndColors[i]);
                int blue = Color.blue(this.animateEndColors[i]);
                int alpha = Color.alpha(this.animateEndColors[i]);
                int red2 = Color.red(this.animateStartColors[i]);
                int green2 = Color.green(this.animateStartColors[i]);
                int blue2 = Color.blue(this.animateStartColors[i]);
                int alpha2 = Color.alpha(this.animateStartColors[i]);
                this.themeAnimatorDescriptions[i].setColor(Color.argb(Math.min(255, (int) (((float) alpha2) + (((float) (alpha - alpha2)) * f))), Math.min(255, (int) (((float) red2) + (((float) (red - red2)) * f))), Math.min(255, (int) (((float) green2) + (((float) (green - green2)) * f))), Math.min(255, (int) (((float) blue2) + (((float) (blue - blue2)) * f)))), false, false);
            }
            if (this.themeAnimatorDelegate != null) {
                this.themeAnimatorDelegate.didSetColor();
            }
        }
    }

    @Keep
    public float getThemeAnimationValue() {
        return this.themeAnimationValue;
    }

    public void animateThemedValues(ThemeInfo themeInfo) {
        if (!this.transitionAnimationInProgress) {
            if (!this.startedTracking) {
                if (this.themeAnimatorSet != null) {
                    this.themeAnimatorSet.cancel();
                    this.themeAnimatorSet = null;
                }
                BaseFragment lastFragment = getLastFragment();
                if (lastFragment != null) {
                    this.themeAnimatorDescriptions = lastFragment.getThemeDescriptions();
                    int i = 0;
                    this.animateStartColors = new int[this.themeAnimatorDescriptions.length];
                    for (int i2 = 0; i2 < this.themeAnimatorDescriptions.length; i2++) {
                        this.animateStartColors[i2] = this.themeAnimatorDescriptions[i2].getSetColor();
                        ThemeDescriptionDelegate delegateDisabled = this.themeAnimatorDescriptions[i2].setDelegateDisabled();
                        if (this.themeAnimatorDelegate == null && delegateDisabled != null) {
                            this.themeAnimatorDelegate = delegateDisabled;
                        }
                    }
                    Theme.applyTheme(themeInfo, true);
                    this.animateEndColors = new int[this.themeAnimatorDescriptions.length];
                    for (themeInfo = null; themeInfo < this.themeAnimatorDescriptions.length; themeInfo++) {
                        this.animateEndColors[themeInfo] = this.themeAnimatorDescriptions[themeInfo].getSetColor();
                    }
                    this.themeAnimatorSet = new AnimatorSet();
                    this.themeAnimatorSet.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            if (animator.equals(ActionBarLayout.this.themeAnimatorSet) != null) {
                                ActionBarLayout.this.themeAnimatorDescriptions = null;
                                ActionBarLayout.this.themeAnimatorDelegate = null;
                                ActionBarLayout.this.themeAnimatorSet = null;
                                ActionBarLayout.this.animateStartColors = null;
                                ActionBarLayout.this.animateEndColors = null;
                            }
                        }

                        public void onAnimationCancel(Animator animator) {
                            if (animator.equals(ActionBarLayout.this.themeAnimatorSet) != null) {
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
                    while (i < this.fragmentsStack.size() - 1) {
                        BaseFragment baseFragment = (BaseFragment) this.fragmentsStack.get(i);
                        baseFragment.clearViews();
                        baseFragment.setParentLayout(this);
                        i++;
                    }
                }
                return;
            }
        }
        this.animateThemeAfterAnimation = true;
        this.animateSetThemeAfterAnimation = themeInfo;
    }

    public void rebuildAllFragmentViews(boolean z, boolean z2) {
        if (!this.transitionAnimationInProgress) {
            if (!this.startedTracking) {
                for (int i = 0; i < this.fragmentsStack.size() - (z ^ 1); i++) {
                    ((BaseFragment) this.fragmentsStack.get(i)).clearViews();
                    ((BaseFragment) this.fragmentsStack.get(i)).setParentLayout(this);
                }
                if (this.delegate != null) {
                    this.delegate.onRebuildAllFragments(this, z);
                }
                if (z2) {
                    showLastFragment();
                }
                return;
            }
        }
        this.rebuildAfterAnimation = true;
        this.rebuildLastAfterAnimation = z;
        this.showLastAfterAnimation = z2;
    }

    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        if (!(i != 82 || checkTransitionAnimation() || this.startedTracking || this.currentActionBar == null)) {
            this.currentActionBar.onMenuButtonPressed();
        }
        return super.onKeyUp(i, keyEvent);
    }

    public void onActionModeStarted(Object obj) {
        if (this.currentActionBar != null) {
            this.currentActionBar.setVisibility(8);
        }
        this.inActionMode = true;
    }

    public void onActionModeFinished(Object obj) {
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

    public void startActivityForResult(Intent intent, int i) {
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
                    this.parentActivity.startActivityForResult(intent, i);
                }
            } else if (intent != null) {
                this.parentActivity.startActivityForResult(intent, i);
            }
        }
    }

    public void setUseAlphaAnimations(boolean z) {
        this.useAlphaAnimations = z;
    }

    public void setBackgroundView(View view) {
        this.backgroundView = view;
    }

    public void setDrawerLayoutContainer(DrawerLayoutContainer drawerLayoutContainer) {
        this.drawerLayoutContainer = drawerLayoutContainer;
    }

    public DrawerLayoutContainer getDrawerLayoutContainer() {
        return this.drawerLayoutContainer;
    }

    public void setRemoveActionBarExtraHeight(boolean z) {
        this.removeActionBarExtraHeight = z;
    }

    public void setTitleOverlayText(String str, String str2, Runnable runnable) {
        this.titleOverlayText = str;
        this.subtitleOverlayText = str2;
        this.overlayAction = runnable;
        for (str = null; str < this.fragmentsStack.size(); str++) {
            BaseFragment baseFragment = (BaseFragment) this.fragmentsStack.get(str);
            if (baseFragment.actionBar != null) {
                baseFragment.actionBar.setTitleOverlayText(this.titleOverlayText, this.subtitleOverlayText, runnable);
            }
        }
    }

    public boolean extendActionMode(Menu menu) {
        return (this.fragmentsStack.isEmpty() || ((BaseFragment) this.fragmentsStack.get(this.fragmentsStack.size() - 1)).extendActionMode(menu) == null) ? false : true;
    }
}
