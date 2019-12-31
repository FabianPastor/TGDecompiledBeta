package org.telegram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import androidx.annotation.Keep;
import java.util.ArrayList;
import java.util.Iterator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MessagesController;
import org.telegram.ui.ActionBar.Theme.ThemeInfo;
import org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;

public class ActionBarLayout extends FrameLayout {
    private static Drawable headerShadowDrawable;
    private static Drawable layerShadowDrawable;
    private static Paint scrimPaint;
    private AccelerateDecelerateInterpolator accelerateDecelerateInterpolator = new AccelerateDecelerateInterpolator();
    private int[][] animateEndColors = new int[2][];
    private int animateSetThemeAccentIdAfterAnimation;
    private ThemeInfo animateSetThemeAfterAnimation;
    private boolean animateSetThemeNightAfterAnimation;
    private int[][] animateStartColors = new int[2][];
    private boolean animateThemeAfterAnimation;
    protected boolean animationInProgress;
    private float animationProgress;
    private Runnable animationRunnable;
    private View backgroundView;
    private boolean beginTrackingSent;
    private LinearLayoutContainer containerView;
    private LinearLayoutContainer containerViewBack;
    private ActionBar currentActionBar;
    private AnimatorSet currentAnimation;
    private DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator(1.5f);
    private Runnable delayedOpenAnimationRunnable;
    private ActionBarLayoutDelegate delegate;
    private DrawerLayoutContainer drawerLayoutContainer;
    public ArrayList<BaseFragment> fragmentsStack;
    private boolean inActionMode;
    private boolean inPreviewMode;
    public float innerTranslationX;
    private long lastFrameTime;
    private boolean maybeStartTracking;
    private Runnable onCloseAnimationEndRunnable;
    private Runnable onOpenAnimationEndRunnable;
    private Runnable overlayAction;
    protected Activity parentActivity;
    private ThemeDescription[] presentingFragmentDescriptions;
    private ColorDrawable previewBackgroundDrawable;
    private boolean rebuildAfterAnimation;
    private boolean rebuildLastAfterAnimation;
    private boolean removeActionBarExtraHeight;
    private boolean showLastAfterAnimation;
    protected boolean startedTracking;
    private int startedTrackingPointerId;
    private int startedTrackingX;
    private int startedTrackingY;
    private float themeAnimationValue;
    private ThemeDescriptionDelegate[] themeAnimatorDelegate = new ThemeDescriptionDelegate[2];
    private ThemeDescription[][] themeAnimatorDescriptions = new ThemeDescription[2][];
    private AnimatorSet themeAnimatorSet;
    private String titleOverlayText;
    private int titleOverlayTextId;
    private boolean transitionAnimationInProgress;
    private boolean transitionAnimationPreviewMode;
    private long transitionAnimationStartTime;
    private boolean useAlphaAnimations;
    private VelocityTracker velocityTracker;
    private Runnable waitingForKeyboardCloseRunnable;

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

        /* Access modifiers changed, original: protected */
        public boolean drawChild(Canvas canvas, View view, long j) {
            if (view instanceof ActionBar) {
                return super.drawChild(canvas, view, j);
            }
            boolean drawChild;
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = getChildAt(i);
                if (childAt != view && (childAt instanceof ActionBar) && childAt.getVisibility() == 0) {
                    if (((ActionBar) childAt).getCastShadows()) {
                        childCount = childAt.getMeasuredHeight();
                        drawChild = super.drawChild(canvas, view, j);
                        if (!(childCount == 0 || ActionBarLayout.headerShadowDrawable == null)) {
                            ActionBarLayout.headerShadowDrawable.setBounds(0, childCount, getMeasuredWidth(), ActionBarLayout.headerShadowDrawable.getIntrinsicHeight() + childCount);
                            ActionBarLayout.headerShadowDrawable.draw(canvas);
                        }
                        return drawChild;
                    }
                    childCount = 0;
                    drawChild = super.drawChild(canvas, view, j);
                    ActionBarLayout.headerShadowDrawable.setBounds(0, childCount, getMeasuredWidth(), ActionBarLayout.headerShadowDrawable.getIntrinsicHeight() + childCount);
                    ActionBarLayout.headerShadowDrawable.draw(canvas);
                    return drawChild;
                }
            }
            childCount = 0;
            drawChild = super.drawChild(canvas, view, j);
            ActionBarLayout.headerShadowDrawable.setBounds(0, childCount, getMeasuredWidth(), ActionBarLayout.headerShadowDrawable.getIntrinsicHeight() + childCount);
            ActionBarLayout.headerShadowDrawable.draw(canvas);
            return drawChild;
        }

        public boolean hasOverlappingRendering() {
            return VERSION.SDK_INT >= 28;
        }

        /* Access modifiers changed, original: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            View rootView = getRootView();
            getWindowVisibleDisplayFrame(this.rect);
            boolean z2 = false;
            i = (rootView.getHeight() - (this.rect.top != 0 ? AndroidUtilities.statusBarHeight : 0)) - AndroidUtilities.getViewInset(rootView);
            Rect rect = this.rect;
            if (i - (rect.bottom - rect.top) > 0) {
                z2 = true;
            }
            this.isKeyboardVisible = z2;
            if (ActionBarLayout.this.waitingForKeyboardCloseRunnable != null && !ActionBarLayout.this.containerView.isKeyboardVisible && !ActionBarLayout.this.containerViewBack.isKeyboardVisible) {
                AndroidUtilities.cancelRunOnUIThread(ActionBarLayout.this.waitingForKeyboardCloseRunnable);
                ActionBarLayout.this.waitingForKeyboardCloseRunnable.run();
                ActionBarLayout.this.waitingForKeyboardCloseRunnable = null;
            }
        }

        public boolean dispatchTouchEvent(MotionEvent motionEvent) {
            boolean z = false;
            if (!((ActionBarLayout.this.inPreviewMode || ActionBarLayout.this.transitionAnimationPreviewMode) && (motionEvent.getActionMasked() == 0 || motionEvent.getActionMasked() == 5))) {
                try {
                    if (!(ActionBarLayout.this.inPreviewMode && this == ActionBarLayout.this.containerView) && super.dispatchTouchEvent(motionEvent)) {
                        z = true;
                    }
                    return z;
                } catch (Throwable th) {
                    FileLog.e(th);
                }
            }
            return false;
        }
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    public ActionBarLayout(Context context) {
        super(context);
        this.parentActivity = (Activity) context;
        if (layerShadowDrawable == null) {
            layerShadowDrawable = getResources().getDrawable(NUM);
            headerShadowDrawable = getResources().getDrawable(NUM).mutate();
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
        Iterator it = this.fragmentsStack.iterator();
        while (it.hasNext()) {
            ((BaseFragment) it.next()).setParentLayout(this);
        }
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (!this.fragmentsStack.isEmpty()) {
            int size = this.fragmentsStack.size();
            for (int i = 0; i < size; i++) {
                BaseFragment baseFragment = (BaseFragment) this.fragmentsStack.get(i);
                baseFragment.onConfigurationChanged(configuration);
                Dialog dialog = baseFragment.visibleDialog;
                if (dialog instanceof BottomSheet) {
                    ((BottomSheet) dialog).onConfigurationChanged(configuration);
                }
            }
        }
    }

    public void drawHeaderShadow(Canvas canvas, int i) {
        Drawable drawable = headerShadowDrawable;
        if (drawable != null) {
            drawable.setBounds(0, i, getMeasuredWidth(), headerShadowDrawable.getIntrinsicHeight() + i);
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
            ArrayList arrayList = this.fragmentsStack;
            ((BaseFragment) arrayList.get(arrayList.size() - 1)).dismissCurrentDialig();
        }
    }

    public void onResume() {
        if (this.transitionAnimationInProgress) {
            AnimatorSet animatorSet = this.currentAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.currentAnimation = null;
            }
            if (this.onCloseAnimationEndRunnable != null) {
                onCloseAnimationEnd();
            } else if (this.onOpenAnimationEndRunnable != null) {
                onOpenAnimationEnd();
            }
        }
        if (!this.fragmentsStack.isEmpty()) {
            ArrayList arrayList = this.fragmentsStack;
            ((BaseFragment) arrayList.get(arrayList.size() - 1)).onResume();
        }
    }

    public void onPause() {
        if (!this.fragmentsStack.isEmpty()) {
            ArrayList arrayList = this.fragmentsStack;
            ((BaseFragment) arrayList.get(arrayList.size() - 1)).onPause();
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return this.animationInProgress || checkTransitionAnimation() || onTouchEvent(motionEvent);
    }

    public void requestDisallowInterceptTouchEvent(boolean z) {
        onTouchEvent(null);
        super.requestDisallowInterceptTouchEvent(z);
    }

    public boolean dispatchKeyEventPreIme(KeyEvent keyEvent) {
        if (keyEvent != null && keyEvent.getKeyCode() == 4) {
            boolean z = true;
            if (keyEvent.getAction() == 1) {
                ActionBarLayoutDelegate actionBarLayoutDelegate = this.delegate;
                if ((actionBarLayoutDelegate == null || !actionBarLayoutDelegate.onPreIme()) && !super.dispatchKeyEventPreIme(keyEvent)) {
                    z = false;
                }
                return z;
            }
        }
        return super.dispatchKeyEventPreIme(keyEvent);
    }

    /* Access modifiers changed, original: protected */
    public boolean drawChild(Canvas canvas, View view, long j) {
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
        int i = 0;
        if (!(this.transitionAnimationInProgress || this.inPreviewMode)) {
            canvas.clipRect(paddingLeft, 0, paddingLeft2, getHeight());
        }
        if (this.inPreviewMode || this.transitionAnimationPreviewMode) {
            View view2 = this.containerView;
            if (view == view2) {
                view2 = view2.getChildAt(0);
                if (view2 != null) {
                    this.previewBackgroundDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
                    this.previewBackgroundDrawable.draw(canvas);
                    int measuredWidth = (getMeasuredWidth() - AndroidUtilities.dp(24.0f)) / 2;
                    float top = ((float) view2.getTop()) + this.containerView.getTranslationY();
                    if (VERSION.SDK_INT < 21) {
                        i = 20;
                    }
                    int dp = (int) (top - ((float) AndroidUtilities.dp((float) (i + 12))));
                    Theme.moveUpDrawable.setBounds(measuredWidth, dp, AndroidUtilities.dp(24.0f) + measuredWidth, AndroidUtilities.dp(24.0f) + dp);
                    Theme.moveUpDrawable.draw(canvas);
                }
            }
        }
        boolean drawChild = super.drawChild(canvas, view, j);
        canvas.restoreToCount(save);
        if (paddingRight != 0) {
            if (view == this.containerView) {
                float max = Math.max(0.0f, Math.min(((float) (width - paddingRight)) / ((float) AndroidUtilities.dp(20.0f)), 1.0f));
                Drawable drawable = layerShadowDrawable;
                drawable.setBounds(paddingRight - drawable.getIntrinsicWidth(), view.getTop(), paddingRight, view.getBottom());
                layerShadowDrawable.setAlpha((int) (max * 255.0f));
                layerShadowDrawable.draw(canvas);
            } else if (view == this.containerViewBack) {
                float min = Math.min(0.8f, ((float) (width - paddingRight)) / ((float) width));
                if (min < 0.0f) {
                    min = 0.0f;
                }
                scrimPaint.setColor(((int) (min * 153.0f)) << 24);
                canvas.drawRect((float) paddingLeft, 0.0f, (float) paddingLeft2, (float) getHeight(), scrimPaint);
            }
        }
        return drawChild;
    }

    public void setDelegate(ActionBarLayoutDelegate actionBarLayoutDelegate) {
        this.delegate = actionBarLayoutDelegate;
    }

    private void onSlideAnimationEnd(boolean z) {
        ArrayList arrayList;
        BaseFragment baseFragment;
        if (z) {
            if (this.fragmentsStack.size() >= 2) {
                ViewGroup viewGroup;
                arrayList = this.fragmentsStack;
                baseFragment = (BaseFragment) arrayList.get(arrayList.size() - 2);
                baseFragment.onPause();
                View view = baseFragment.fragmentView;
                if (view != null) {
                    viewGroup = (ViewGroup) view.getParent();
                    if (viewGroup != null) {
                        baseFragment.onRemoveFromParent();
                        viewGroup.removeViewInLayout(baseFragment.fragmentView);
                    }
                }
                ActionBar actionBar = baseFragment.actionBar;
                if (actionBar != null && actionBar.getAddToContainer()) {
                    viewGroup = (ViewGroup) baseFragment.actionBar.getParent();
                    if (viewGroup != null) {
                        viewGroup.removeViewInLayout(baseFragment.actionBar);
                    }
                }
            }
        } else if (this.fragmentsStack.size() >= 2) {
            arrayList = this.fragmentsStack;
            baseFragment = (BaseFragment) arrayList.get(arrayList.size() - 1);
            baseFragment.onPause();
            baseFragment.onFragmentDestroy();
            baseFragment.setParentLayout(null);
            arrayList = this.fragmentsStack;
            arrayList.remove(arrayList.size() - 1);
            LinearLayoutContainer linearLayoutContainer = this.containerView;
            this.containerView = this.containerViewBack;
            this.containerViewBack = linearLayoutContainer;
            bringChildToFront(this.containerView);
            arrayList = this.fragmentsStack;
            baseFragment = (BaseFragment) arrayList.get(arrayList.size() - 1);
            this.currentActionBar = baseFragment.actionBar;
            baseFragment.onResume();
            baseFragment.onBecomeFullyVisible();
        } else {
            return;
        }
        this.containerViewBack.setVisibility(4);
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
        ArrayList arrayList = this.fragmentsStack;
        BaseFragment baseFragment = (BaseFragment) arrayList.get(arrayList.size() - 2);
        View view = baseFragment.fragmentView;
        if (view == null) {
            view = baseFragment.createView(this.parentActivity);
        }
        ViewGroup viewGroup = (ViewGroup) view.getParent();
        if (viewGroup != null) {
            baseFragment.onRemoveFromParent();
            viewGroup.removeView(view);
        }
        ActionBar actionBar = baseFragment.actionBar;
        if (actionBar != null && actionBar.getAddToContainer()) {
            viewGroup = (ViewGroup) baseFragment.actionBar.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(baseFragment.actionBar);
            }
            if (this.removeActionBarExtraHeight) {
                baseFragment.actionBar.setOccupyStatusBar(false);
            }
            this.containerViewBack.addView(baseFragment.actionBar);
            baseFragment.actionBar.setTitleOverlayText(this.titleOverlayText, this.titleOverlayTextId, this.overlayAction);
        }
        this.containerViewBack.addView(view);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        layoutParams.leftMargin = 0;
        layoutParams.rightMargin = 0;
        layoutParams.bottomMargin = 0;
        layoutParams.topMargin = 0;
        view.setLayoutParams(layoutParams);
        if (!baseFragment.hasOwnBackground && view.getBackground() == null) {
            view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        }
        baseFragment.onResume();
        if (this.themeAnimatorSet != null) {
            this.presentingFragmentDescriptions = baseFragment.getThemeDescriptions();
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (checkTransitionAnimation() || this.inActionMode || this.animationInProgress) {
            return false;
        }
        if (this.fragmentsStack.size() > 1) {
            ArrayList arrayList;
            VelocityTracker velocityTracker;
            if (motionEvent != null && motionEvent.getAction() == 0 && !this.startedTracking && !this.maybeStartTracking) {
                arrayList = this.fragmentsStack;
                if (!((BaseFragment) arrayList.get(arrayList.size() - 1)).swipeBackEnabled) {
                    return false;
                }
                this.startedTrackingPointerId = motionEvent.getPointerId(0);
                this.maybeStartTracking = true;
                this.startedTrackingX = (int) motionEvent.getX();
                this.startedTrackingY = (int) motionEvent.getY();
                velocityTracker = this.velocityTracker;
                if (velocityTracker != null) {
                    velocityTracker.clear();
                }
            } else if (motionEvent != null && motionEvent.getAction() == 2 && motionEvent.getPointerId(0) == this.startedTrackingPointerId) {
                if (this.velocityTracker == null) {
                    this.velocityTracker = VelocityTracker.obtain();
                }
                int max = Math.max(0, (int) (motionEvent.getX() - ((float) this.startedTrackingX)));
                int abs = Math.abs(((int) motionEvent.getY()) - this.startedTrackingY);
                this.velocityTracker.addMovement(motionEvent);
                if (!this.inPreviewMode && this.maybeStartTracking && !this.startedTracking && ((float) max) >= AndroidUtilities.getPixelsInCM(0.4f, true) && Math.abs(max) / 3 > abs) {
                    arrayList = this.fragmentsStack;
                    if (((BaseFragment) arrayList.get(arrayList.size() - 1)).canBeginSlide()) {
                        prepareForMoving(motionEvent);
                    } else {
                        this.maybeStartTracking = false;
                    }
                } else if (this.startedTracking) {
                    if (!this.beginTrackingSent) {
                        if (this.parentActivity.getCurrentFocus() != null) {
                            AndroidUtilities.hideKeyboard(this.parentActivity.getCurrentFocus());
                        }
                        ArrayList arrayList2 = this.fragmentsStack;
                        ((BaseFragment) arrayList2.get(arrayList2.size() - 1)).onBeginSlide();
                        this.beginTrackingSent = true;
                    }
                    float f = (float) max;
                    this.containerView.setTranslationX(f);
                    setInnerTranslationX(f);
                }
            } else if (motionEvent != null && motionEvent.getPointerId(0) == this.startedTrackingPointerId && (motionEvent.getAction() == 3 || motionEvent.getAction() == 1 || motionEvent.getAction() == 6)) {
                float xVelocity;
                if (this.velocityTracker == null) {
                    this.velocityTracker = VelocityTracker.obtain();
                }
                this.velocityTracker.computeCurrentVelocity(1000);
                arrayList = this.fragmentsStack;
                BaseFragment baseFragment = (BaseFragment) arrayList.get(arrayList.size() - 1);
                if (!(this.inPreviewMode || this.transitionAnimationPreviewMode || this.startedTracking || !baseFragment.swipeBackEnabled)) {
                    xVelocity = this.velocityTracker.getXVelocity();
                    float yVelocity = this.velocityTracker.getYVelocity();
                    if (xVelocity >= 3500.0f && xVelocity > Math.abs(yVelocity) && baseFragment.canBeginSlide()) {
                        prepareForMoving(motionEvent);
                        if (!this.beginTrackingSent) {
                            if (((Activity) getContext()).getCurrentFocus() != null) {
                                AndroidUtilities.hideKeyboard(((Activity) getContext()).getCurrentFocus());
                            }
                            this.beginTrackingSent = true;
                        }
                    }
                }
                if (this.startedTracking) {
                    float x = this.containerView.getX();
                    AnimatorSet animatorSet = new AnimatorSet();
                    xVelocity = this.velocityTracker.getXVelocity();
                    final boolean z = x < ((float) this.containerView.getMeasuredWidth()) / 3.0f && (xVelocity < 3500.0f || xVelocity < this.velocityTracker.getYVelocity());
                    String str = "innerTranslationX";
                    String str2 = "translationX";
                    Animator[] animatorArr;
                    if (z) {
                        animatorArr = new Animator[2];
                        animatorArr[0] = ObjectAnimator.ofFloat(this.containerView, str2, new float[]{0.0f});
                        animatorArr[1] = ObjectAnimator.ofFloat(this, str, new float[]{0.0f});
                        animatorSet.playTogether(animatorArr);
                    } else {
                        x = ((float) this.containerView.getMeasuredWidth()) - x;
                        animatorArr = new Animator[2];
                        animatorArr[0] = ObjectAnimator.ofFloat(this.containerView, str2, new float[]{(float) this.containerView.getMeasuredWidth()});
                        animatorArr[1] = ObjectAnimator.ofFloat(this, str, new float[]{(float) this.containerView.getMeasuredWidth()});
                        animatorSet.playTogether(animatorArr);
                    }
                    animatorSet.setDuration((long) Math.max((int) ((200.0f / ((float) this.containerView.getMeasuredWidth())) * x), 50));
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
                velocityTracker = this.velocityTracker;
                if (velocityTracker != null) {
                    velocityTracker.recycle();
                    this.velocityTracker = null;
                }
            } else if (motionEvent == null) {
                this.maybeStartTracking = false;
                this.startedTracking = false;
                velocityTracker = this.velocityTracker;
                if (velocityTracker != null) {
                    velocityTracker.recycle();
                    this.velocityTracker = null;
                }
            }
        }
        return this.startedTracking;
    }

    public void onBackPressed() {
        if (!(this.transitionAnimationPreviewMode || this.startedTracking || checkTransitionAnimation() || this.fragmentsStack.isEmpty())) {
            if (!this.currentActionBar.isActionModeShowed()) {
                ActionBar actionBar = this.currentActionBar;
                if (actionBar != null && actionBar.isSearchFieldVisible) {
                    actionBar.closeSearchField();
                    return;
                }
            }
            ArrayList arrayList = this.fragmentsStack;
            if (((BaseFragment) arrayList.get(arrayList.size() - 1)).onBackPressed() && !this.fragmentsStack.isEmpty()) {
                closeLastFragment(true);
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
        Runnable runnable = this.waitingForKeyboardCloseRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.waitingForKeyboardCloseRunnable = null;
        }
        AnimatorSet animatorSet = this.currentAnimation;
        if (animatorSet != null) {
            if (z) {
                animatorSet.cancel();
            }
            this.currentAnimation = null;
        }
        Runnable runnable2 = this.animationRunnable;
        if (runnable2 != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable2);
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
        ArrayList arrayList = this.fragmentsStack;
        return (BaseFragment) arrayList.get(arrayList.size() - 1);
    }

    public boolean checkTransitionAnimation() {
        if (this.transitionAnimationPreviewMode) {
            return false;
        }
        if (this.transitionAnimationInProgress && this.transitionAnimationStartTime < System.currentTimeMillis() - 1500) {
            onAnimationEndCheck(true);
        }
        return this.transitionAnimationInProgress;
    }

    private void presentFragmentInternalRemoveOld(boolean z, BaseFragment baseFragment) {
        if (baseFragment != null) {
            baseFragment.onBecomeFullyHidden();
            baseFragment.onPause();
            if (z) {
                baseFragment.onFragmentDestroy();
                baseFragment.setParentLayout(null);
                this.fragmentsStack.remove(baseFragment);
            } else {
                ViewGroup viewGroup;
                View view = baseFragment.fragmentView;
                if (view != null) {
                    viewGroup = (ViewGroup) view.getParent();
                    if (viewGroup != null) {
                        baseFragment.onRemoveFromParent();
                        viewGroup.removeViewInLayout(baseFragment.fragmentView);
                    }
                }
                ActionBar actionBar = baseFragment.actionBar;
                if (actionBar != null && actionBar.getAddToContainer()) {
                    viewGroup = (ViewGroup) baseFragment.actionBar.getParent();
                    if (viewGroup != null) {
                        viewGroup.removeViewInLayout(baseFragment.actionBar);
                    }
                }
            }
            this.containerViewBack.setVisibility(4);
        }
    }

    public boolean presentFragmentAsPreview(BaseFragment baseFragment) {
        return presentFragment(baseFragment, false, false, true, true);
    }

    public boolean presentFragment(BaseFragment baseFragment) {
        return presentFragment(baseFragment, false, false, true, false);
    }

    public boolean presentFragment(BaseFragment baseFragment, boolean z) {
        return presentFragment(baseFragment, z, false, true, false);
    }

    private void startLayoutAnimation(final boolean z, final boolean z2, final boolean z3) {
        if (z2) {
            this.animationProgress = 0.0f;
            this.lastFrameTime = System.nanoTime() / 1000000;
        }
        AnonymousClass2 anonymousClass2 = new Runnable() {
            public void run() {
                if (ActionBarLayout.this.animationRunnable == this) {
                    ActionBarLayout.this.animationRunnable = null;
                    if (z2) {
                        ActionBarLayout.this.transitionAnimationStartTime = System.currentTimeMillis();
                    }
                    long nanoTime = System.nanoTime() / 1000000;
                    long access$900 = nanoTime - ActionBarLayout.this.lastFrameTime;
                    if (access$900 > 18) {
                        access$900 = 18;
                    }
                    ActionBarLayout.this.lastFrameTime = nanoTime;
                    ActionBarLayout actionBarLayout = ActionBarLayout.this;
                    actionBarLayout.animationProgress = actionBarLayout.animationProgress + (((float) access$900) / 150.0f);
                    if (ActionBarLayout.this.animationProgress > 1.0f) {
                        ActionBarLayout.this.animationProgress = 1.0f;
                    }
                    float interpolation = ActionBarLayout.this.decelerateInterpolator.getInterpolation(ActionBarLayout.this.animationProgress);
                    float f;
                    if (z) {
                        ActionBarLayout.this.containerView.setAlpha(interpolation);
                        if (z3) {
                            f = (0.1f * interpolation) + 0.9f;
                            ActionBarLayout.this.containerView.setScaleX(f);
                            ActionBarLayout.this.containerView.setScaleY(f);
                            ActionBarLayout.this.previewBackgroundDrawable.setAlpha((int) (128.0f * interpolation));
                            Theme.moveUpDrawable.setAlpha((int) (interpolation * 255.0f));
                            ActionBarLayout.this.containerView.invalidate();
                            ActionBarLayout.this.invalidate();
                        } else {
                            ActionBarLayout.this.containerView.setTranslationX(((float) AndroidUtilities.dp(48.0f)) * (1.0f - interpolation));
                        }
                    } else {
                        float f2 = 1.0f - interpolation;
                        ActionBarLayout.this.containerViewBack.setAlpha(f2);
                        if (z3) {
                            f = (0.1f * f2) + 0.9f;
                            ActionBarLayout.this.containerViewBack.setScaleX(f);
                            ActionBarLayout.this.containerViewBack.setScaleY(f);
                            ActionBarLayout.this.previewBackgroundDrawable.setAlpha((int) (128.0f * f2));
                            Theme.moveUpDrawable.setAlpha((int) (f2 * 255.0f));
                            ActionBarLayout.this.containerView.invalidate();
                            ActionBarLayout.this.invalidate();
                        } else {
                            ActionBarLayout.this.containerViewBack.setTranslationX(((float) AndroidUtilities.dp(48.0f)) * interpolation);
                        }
                    }
                    if (ActionBarLayout.this.animationProgress < 1.0f) {
                        ActionBarLayout.this.startLayoutAnimation(z, false, z3);
                    } else {
                        ActionBarLayout.this.onAnimationEndCheck(false);
                    }
                }
            }
        };
        this.animationRunnable = anonymousClass2;
        AndroidUtilities.runOnUIThread(anonymousClass2);
    }

    public void resumeDelayedFragmentAnimation() {
        Runnable runnable = this.delayedOpenAnimationRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.delayedOpenAnimationRunnable.run();
            this.delayedOpenAnimationRunnable = null;
        }
    }

    public boolean isInPreviewMode() {
        return this.inPreviewMode || this.transitionAnimationPreviewMode;
    }

    public boolean presentFragment(BaseFragment baseFragment, boolean z, boolean z2, boolean z3, boolean z4) {
        BaseFragment baseFragment2 = baseFragment;
        boolean z5 = z;
        boolean z6 = z2;
        final boolean z7 = z4;
        if (!(baseFragment2 == null || checkTransitionAnimation())) {
            ActionBarLayoutDelegate actionBarLayoutDelegate = this.delegate;
            if ((actionBarLayoutDelegate == null || !z3 || actionBarLayoutDelegate.needPresentFragment(baseFragment2, z5, z6, this)) && baseFragment.onFragmentCreate()) {
                BaseFragment baseFragment3;
                ViewGroup viewGroup;
                baseFragment2.setInPreviewMode(z7);
                if (this.parentActivity.getCurrentFocus() != null) {
                    AndroidUtilities.hideKeyboard(this.parentActivity.getCurrentFocus());
                }
                Object obj = (z7 || (!z6 && MessagesController.getGlobalMainSettings().getBoolean("view_animations", true))) ? 1 : null;
                AnimatorSet animatorSet = null;
                if (this.fragmentsStack.isEmpty()) {
                    baseFragment3 = null;
                } else {
                    ArrayList arrayList = this.fragmentsStack;
                    baseFragment3 = (BaseFragment) arrayList.get(arrayList.size() - 1);
                }
                baseFragment2.setParentLayout(this);
                View view = baseFragment2.fragmentView;
                if (view == null) {
                    view = baseFragment2.createView(this.parentActivity);
                } else {
                    viewGroup = (ViewGroup) view.getParent();
                    if (viewGroup != null) {
                        baseFragment.onRemoveFromParent();
                        viewGroup.removeView(view);
                    }
                }
                ActionBar actionBar = baseFragment2.actionBar;
                if (actionBar != null && actionBar.getAddToContainer()) {
                    if (this.removeActionBarExtraHeight) {
                        baseFragment2.actionBar.setOccupyStatusBar(false);
                    }
                    viewGroup = (ViewGroup) baseFragment2.actionBar.getParent();
                    if (viewGroup != null) {
                        viewGroup.removeView(baseFragment2.actionBar);
                    }
                    this.containerViewBack.addView(baseFragment2.actionBar);
                    baseFragment2.actionBar.setTitleOverlayText(this.titleOverlayText, this.titleOverlayTextId, this.overlayAction);
                }
                this.containerViewBack.addView(view);
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
                layoutParams.width = -1;
                layoutParams.height = -1;
                if (z7) {
                    int dp = AndroidUtilities.dp(8.0f);
                    layoutParams.leftMargin = dp;
                    layoutParams.rightMargin = dp;
                    dp = AndroidUtilities.dp(46.0f);
                    layoutParams.bottomMargin = dp;
                    layoutParams.topMargin = dp;
                    layoutParams.topMargin += AndroidUtilities.statusBarHeight;
                } else {
                    layoutParams.leftMargin = 0;
                    layoutParams.rightMargin = 0;
                    layoutParams.bottomMargin = 0;
                    layoutParams.topMargin = 0;
                }
                view.setLayoutParams(layoutParams);
                this.fragmentsStack.add(baseFragment2);
                baseFragment.onResume();
                this.currentActionBar = baseFragment2.actionBar;
                if (!baseFragment2.hasOwnBackground && view.getBackground() == null) {
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                }
                LinearLayoutContainer linearLayoutContainer = this.containerView;
                this.containerView = this.containerViewBack;
                this.containerViewBack = linearLayoutContainer;
                this.containerView.setVisibility(0);
                setInnerTranslationX(0.0f);
                this.containerView.setTranslationY(0.0f);
                if (z7) {
                    if (VERSION.SDK_INT >= 21) {
                        view.setOutlineProvider(new ViewOutlineProvider() {
                            @TargetApi(21)
                            public void getOutline(View view, Outline outline) {
                                outline.setRoundRect(0, AndroidUtilities.statusBarHeight, view.getMeasuredWidth(), view.getMeasuredHeight(), (float) AndroidUtilities.dp(6.0f));
                            }
                        });
                        view.setClipToOutline(true);
                        view.setElevation((float) AndroidUtilities.dp(4.0f));
                    }
                    if (this.previewBackgroundDrawable == null) {
                        this.previewBackgroundDrawable = new ColorDrawable(Integer.MIN_VALUE);
                    }
                    this.previewBackgroundDrawable.setAlpha(0);
                    Theme.moveUpDrawable.setAlpha(0);
                }
                bringChildToFront(this.containerView);
                if (obj == null) {
                    presentFragmentInternalRemoveOld(z5, baseFragment3);
                    view = this.backgroundView;
                    if (view != null) {
                        view.setVisibility(0);
                    }
                }
                if (this.themeAnimatorSet != null) {
                    this.presentingFragmentDescriptions = baseFragment.getThemeDescriptions();
                }
                if (obj == null && !z7) {
                    View view2 = this.backgroundView;
                    if (view2 != null) {
                        view2.setAlpha(1.0f);
                        this.backgroundView.setVisibility(0);
                    }
                    if (baseFragment3 != null) {
                        baseFragment3.onTransitionAnimationStart(false, false);
                        baseFragment3.onTransitionAnimationEnd(false, false);
                    }
                    baseFragment2.onTransitionAnimationStart(true, false);
                    baseFragment2.onTransitionAnimationEnd(true, false);
                    baseFragment.onBecomeFullyVisible();
                } else if (this.useAlphaAnimations && this.fragmentsStack.size() == 1) {
                    presentFragmentInternalRemoveOld(z5, baseFragment3);
                    this.transitionAnimationStartTime = System.currentTimeMillis();
                    this.transitionAnimationInProgress = true;
                    this.onOpenAnimationEndRunnable = new -$$Lambda$ActionBarLayout$rN6X9ltc9Ag_bAyne2dx8uCZ0cs(baseFragment3, baseFragment2);
                    ArrayList arrayList2 = new ArrayList();
                    String str = "alpha";
                    arrayList2.add(ObjectAnimator.ofFloat(this, str, new float[]{0.0f, 1.0f}));
                    View view3 = this.backgroundView;
                    if (view3 != null) {
                        view3.setVisibility(0);
                        arrayList2.add(ObjectAnimator.ofFloat(this.backgroundView, str, new float[]{0.0f, 1.0f}));
                    }
                    if (baseFragment3 != null) {
                        baseFragment3.onTransitionAnimationStart(false, false);
                    }
                    baseFragment2.onTransitionAnimationStart(true, false);
                    this.currentAnimation = new AnimatorSet();
                    this.currentAnimation.playTogether(arrayList2);
                    this.currentAnimation.setInterpolator(this.accelerateDecelerateInterpolator);
                    this.currentAnimation.setDuration(200);
                    this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            ActionBarLayout.this.onAnimationEndCheck(false);
                        }
                    });
                    this.currentAnimation.start();
                } else {
                    this.transitionAnimationPreviewMode = z7;
                    this.transitionAnimationStartTime = System.currentTimeMillis();
                    this.transitionAnimationInProgress = true;
                    this.onOpenAnimationEndRunnable = new -$$Lambda$ActionBarLayout$gS41pICx_migujhqTs-lczeRv1Y(this, z4, z, baseFragment3, baseFragment);
                    if (baseFragment3 != null) {
                        baseFragment3.onTransitionAnimationStart(false, false);
                    }
                    baseFragment2.onTransitionAnimationStart(true, false);
                    if (!z7) {
                        animatorSet = baseFragment2.onCustomTransitionAnimation(true, new -$$Lambda$ActionBarLayout$ZVBs3Yp413UBaNwwGzpjbW7oZTc(this));
                    }
                    if (animatorSet == null) {
                        this.containerView.setAlpha(0.0f);
                        if (z7) {
                            this.containerView.setTranslationX(0.0f);
                            this.containerView.setScaleX(0.9f);
                            this.containerView.setScaleY(0.9f);
                        } else {
                            this.containerView.setTranslationX(48.0f);
                            this.containerView.setScaleX(1.0f);
                            this.containerView.setScaleY(1.0f);
                        }
                        if (this.containerView.isKeyboardVisible || this.containerViewBack.isKeyboardVisible) {
                            this.waitingForKeyboardCloseRunnable = new Runnable() {
                                public void run() {
                                    if (ActionBarLayout.this.waitingForKeyboardCloseRunnable == this) {
                                        ActionBarLayout.this.waitingForKeyboardCloseRunnable = null;
                                        ActionBarLayout.this.startLayoutAnimation(true, true, z7);
                                    }
                                }
                            };
                            AndroidUtilities.runOnUIThread(this.waitingForKeyboardCloseRunnable, 200);
                        } else if (baseFragment.needDelayOpenAnimation()) {
                            this.delayedOpenAnimationRunnable = new Runnable() {
                                public void run() {
                                    if (ActionBarLayout.this.delayedOpenAnimationRunnable == this) {
                                        ActionBarLayout.this.delayedOpenAnimationRunnable = null;
                                        ActionBarLayout.this.startLayoutAnimation(true, true, z7);
                                    }
                                }
                            };
                            AndroidUtilities.runOnUIThread(this.delayedOpenAnimationRunnable, 200);
                        } else {
                            startLayoutAnimation(true, true, z7);
                        }
                    } else {
                        this.containerView.setAlpha(1.0f);
                        this.containerView.setTranslationX(0.0f);
                        this.currentAnimation = animatorSet;
                    }
                }
                return true;
            }
        }
        return false;
    }

    static /* synthetic */ void lambda$presentFragment$0(BaseFragment baseFragment, BaseFragment baseFragment2) {
        if (baseFragment != null) {
            baseFragment.onTransitionAnimationEnd(false, false);
        }
        baseFragment2.onTransitionAnimationEnd(true, false);
        baseFragment2.onBecomeFullyVisible();
    }

    public /* synthetic */ void lambda$presentFragment$1$ActionBarLayout(boolean z, boolean z2, BaseFragment baseFragment, BaseFragment baseFragment2) {
        if (z) {
            this.inPreviewMode = true;
            this.transitionAnimationPreviewMode = false;
            this.containerView.setScaleX(1.0f);
            this.containerView.setScaleY(1.0f);
        } else {
            presentFragmentInternalRemoveOld(z2, baseFragment);
            this.containerView.setTranslationX(0.0f);
        }
        if (baseFragment != null) {
            baseFragment.onTransitionAnimationEnd(false, false);
        }
        baseFragment2.onTransitionAnimationEnd(true, false);
        baseFragment2.onBecomeFullyVisible();
    }

    public /* synthetic */ void lambda$presentFragment$2$ActionBarLayout() {
        onAnimationEndCheck(false);
    }

    public boolean addFragmentToStack(BaseFragment baseFragment) {
        return addFragmentToStack(baseFragment, -1);
    }

    public boolean addFragmentToStack(BaseFragment baseFragment, int i) {
        ActionBarLayoutDelegate actionBarLayoutDelegate = this.delegate;
        if ((actionBarLayoutDelegate != null && !actionBarLayoutDelegate.needAddFragmentToStack(baseFragment, this)) || !baseFragment.onFragmentCreate()) {
            return false;
        }
        baseFragment.setParentLayout(this);
        if (i == -1) {
            if (!this.fragmentsStack.isEmpty()) {
                ViewGroup viewGroup;
                ArrayList arrayList = this.fragmentsStack;
                BaseFragment baseFragment2 = (BaseFragment) arrayList.get(arrayList.size() - 1);
                baseFragment2.onPause();
                ActionBar actionBar = baseFragment2.actionBar;
                if (actionBar != null && actionBar.getAddToContainer()) {
                    viewGroup = (ViewGroup) baseFragment2.actionBar.getParent();
                    if (viewGroup != null) {
                        viewGroup.removeView(baseFragment2.actionBar);
                    }
                }
                View view = baseFragment2.fragmentView;
                if (view != null) {
                    viewGroup = (ViewGroup) view.getParent();
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
        this.containerViewBack.setVisibility(4);
        bringChildToFront(this.containerView);
    }

    public void movePreviewFragment(float f) {
        if (this.inPreviewMode && !this.transitionAnimationPreviewMode) {
            float translationY = this.containerView.getTranslationY();
            float f2 = -f;
            float f3 = 0.0f;
            if (f2 <= 0.0f) {
                if (f2 < ((float) (-AndroidUtilities.dp(60.0f)))) {
                    this.inPreviewMode = false;
                    ArrayList arrayList = this.fragmentsStack;
                    BaseFragment baseFragment = (BaseFragment) arrayList.get(arrayList.size() - 2);
                    ArrayList arrayList2 = this.fragmentsStack;
                    BaseFragment baseFragment2 = (BaseFragment) arrayList2.get(arrayList2.size() - 1);
                    if (VERSION.SDK_INT >= 21) {
                        baseFragment2.fragmentView.setOutlineProvider(null);
                        baseFragment2.fragmentView.setClipToOutline(false);
                    }
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) baseFragment2.fragmentView.getLayoutParams();
                    layoutParams.leftMargin = 0;
                    layoutParams.rightMargin = 0;
                    layoutParams.bottomMargin = 0;
                    layoutParams.topMargin = 0;
                    baseFragment2.fragmentView.setLayoutParams(layoutParams);
                    presentFragmentInternalRemoveOld(false, baseFragment);
                    AnimatorSet animatorSet = new AnimatorSet();
                    r6 = new Animator[2];
                    r6[0] = ObjectAnimator.ofFloat(baseFragment2.fragmentView, "scaleX", new float[]{1.0f, 1.05f, 1.0f});
                    r6[1] = ObjectAnimator.ofFloat(baseFragment2.fragmentView, "scaleY", new float[]{1.0f, 1.05f, 1.0f});
                    animatorSet.playTogether(r6);
                    animatorSet.setDuration(200);
                    animatorSet.setInterpolator(new CubicBezierInterpolator(0.42d, 0.0d, 0.58d, 1.0d));
                    animatorSet.start();
                    performHapticFeedback(3);
                    baseFragment2.setInPreviewMode(false);
                } else {
                    f3 = f2;
                }
            }
            if (translationY != f3) {
                this.containerView.setTranslationY(f3);
                invalidate();
            }
        }
    }

    public void finishPreviewFragment() {
        if (this.inPreviewMode || this.transitionAnimationPreviewMode) {
            closeLastFragment(true);
        }
    }

    public void closeLastFragment(boolean z) {
        ActionBarLayoutDelegate actionBarLayoutDelegate = this.delegate;
        if ((actionBarLayoutDelegate == null || actionBarLayoutDelegate.needCloseLastFragment(this)) && !checkTransitionAnimation() && !this.fragmentsStack.isEmpty()) {
            BaseFragment baseFragment;
            if (this.parentActivity.getCurrentFocus() != null) {
                AndroidUtilities.hideKeyboard(this.parentActivity.getCurrentFocus());
            }
            setInnerTranslationX(0.0f);
            Object obj = (this.inPreviewMode || this.transitionAnimationPreviewMode || (z && MessagesController.getGlobalMainSettings().getBoolean("view_animations", true))) ? 1 : null;
            ArrayList arrayList = this.fragmentsStack;
            BaseFragment baseFragment2 = (BaseFragment) arrayList.get(arrayList.size() - 1);
            AnimatorSet animatorSet = null;
            if (this.fragmentsStack.size() > 1) {
                ArrayList arrayList2 = this.fragmentsStack;
                baseFragment = (BaseFragment) arrayList2.get(arrayList2.size() - 2);
            } else {
                baseFragment = null;
            }
            View view;
            if (baseFragment != null) {
                LinearLayoutContainer linearLayoutContainer = this.containerView;
                this.containerView = this.containerViewBack;
                this.containerViewBack = linearLayoutContainer;
                baseFragment.setParentLayout(this);
                View view2 = baseFragment.fragmentView;
                if (view2 == null) {
                    view2 = baseFragment.createView(this.parentActivity);
                }
                if (!this.inPreviewMode) {
                    ViewGroup viewGroup;
                    this.containerView.setVisibility(0);
                    ActionBar actionBar = baseFragment.actionBar;
                    if (actionBar != null && actionBar.getAddToContainer()) {
                        if (this.removeActionBarExtraHeight) {
                            baseFragment.actionBar.setOccupyStatusBar(false);
                        }
                        viewGroup = (ViewGroup) baseFragment.actionBar.getParent();
                        if (viewGroup != null) {
                            viewGroup.removeView(baseFragment.actionBar);
                        }
                        this.containerView.addView(baseFragment.actionBar);
                        baseFragment.actionBar.setTitleOverlayText(this.titleOverlayText, this.titleOverlayTextId, this.overlayAction);
                    }
                    viewGroup = (ViewGroup) view2.getParent();
                    if (viewGroup != null) {
                        baseFragment.onRemoveFromParent();
                        try {
                            viewGroup.removeView(view2);
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
                    }
                    this.containerView.addView(view2);
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view2.getLayoutParams();
                    layoutParams.width = -1;
                    layoutParams.height = -1;
                    layoutParams.leftMargin = 0;
                    layoutParams.rightMargin = 0;
                    layoutParams.bottomMargin = 0;
                    layoutParams.topMargin = 0;
                    view2.setLayoutParams(layoutParams);
                }
                baseFragment.onTransitionAnimationStart(true, true);
                baseFragment2.onTransitionAnimationStart(false, true);
                baseFragment.onResume();
                if (this.themeAnimatorSet != null) {
                    this.presentingFragmentDescriptions = baseFragment.getThemeDescriptions();
                }
                this.currentActionBar = baseFragment.actionBar;
                if (!baseFragment.hasOwnBackground && view2.getBackground() == null) {
                    view2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                }
                if (obj == null) {
                    closeLastFragmentInternalRemoveOld(baseFragment2);
                }
                if (obj != null) {
                    this.transitionAnimationStartTime = System.currentTimeMillis();
                    this.transitionAnimationInProgress = true;
                    this.onCloseAnimationEndRunnable = new -$$Lambda$ActionBarLayout$pBlWjaMhbocc2CQiKAJuI1kS-Ds(this, baseFragment2, baseFragment);
                    if (!(this.inPreviewMode || this.transitionAnimationPreviewMode)) {
                        animatorSet = baseFragment2.onCustomTransitionAnimation(false, new -$$Lambda$ActionBarLayout$r7PgXBy38d_b4XaDN8FoveXk1BE(this));
                    }
                    if (animatorSet != null) {
                        this.currentAnimation = animatorSet;
                        return;
                    } else if (this.containerView.isKeyboardVisible || this.containerViewBack.isKeyboardVisible) {
                        this.waitingForKeyboardCloseRunnable = new Runnable() {
                            public void run() {
                                if (ActionBarLayout.this.waitingForKeyboardCloseRunnable == this) {
                                    ActionBarLayout.this.waitingForKeyboardCloseRunnable = null;
                                    ActionBarLayout.this.startLayoutAnimation(false, true, false);
                                }
                            }
                        };
                        AndroidUtilities.runOnUIThread(this.waitingForKeyboardCloseRunnable, 200);
                        return;
                    } else {
                        z = this.inPreviewMode || this.transitionAnimationPreviewMode;
                        startLayoutAnimation(false, true, z);
                        return;
                    }
                }
                baseFragment2.onTransitionAnimationEnd(false, true);
                baseFragment.onTransitionAnimationEnd(true, true);
                baseFragment.onBecomeFullyVisible();
            } else if (this.useAlphaAnimations) {
                this.transitionAnimationStartTime = System.currentTimeMillis();
                this.transitionAnimationInProgress = true;
                this.onCloseAnimationEndRunnable = new -$$Lambda$ActionBarLayout$S9HTTIgsI9OBg6Q7_NNccIiX628(this, baseFragment2);
                ArrayList arrayList3 = new ArrayList();
                String str = "alpha";
                arrayList3.add(ObjectAnimator.ofFloat(this, str, new float[]{1.0f, 0.0f}));
                view = this.backgroundView;
                if (view != null) {
                    arrayList3.add(ObjectAnimator.ofFloat(view, str, new float[]{1.0f, 0.0f}));
                }
                this.currentAnimation = new AnimatorSet();
                this.currentAnimation.playTogether(arrayList3);
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
                removeFragmentFromStackInternal(baseFragment2);
                setVisibility(8);
                view = this.backgroundView;
                if (view != null) {
                    view.setVisibility(8);
                }
            }
        }
    }

    public /* synthetic */ void lambda$closeLastFragment$3$ActionBarLayout(BaseFragment baseFragment, BaseFragment baseFragment2) {
        if (this.inPreviewMode || this.transitionAnimationPreviewMode) {
            this.containerViewBack.setScaleX(1.0f);
            this.containerViewBack.setScaleY(1.0f);
            this.inPreviewMode = false;
            this.transitionAnimationPreviewMode = false;
        } else {
            this.containerViewBack.setTranslationX(0.0f);
        }
        closeLastFragmentInternalRemoveOld(baseFragment);
        baseFragment.onTransitionAnimationEnd(false, true);
        baseFragment2.onTransitionAnimationEnd(true, true);
        baseFragment2.onBecomeFullyVisible();
    }

    public /* synthetic */ void lambda$closeLastFragment$4$ActionBarLayout() {
        onAnimationEndCheck(false);
    }

    public /* synthetic */ void lambda$closeLastFragment$5$ActionBarLayout(BaseFragment baseFragment) {
        removeFragmentFromStackInternal(baseFragment);
        setVisibility(8);
        View view = this.backgroundView;
        if (view != null) {
            view.setVisibility(8);
        }
        DrawerLayoutContainer drawerLayoutContainer = this.drawerLayoutContainer;
        if (drawerLayoutContainer != null) {
            drawerLayoutContainer.setAllowOpenDrawer(true, false);
        }
    }

    public void showLastFragment() {
        if (!this.fragmentsStack.isEmpty()) {
            ActionBar actionBar;
            ViewGroup viewGroup;
            for (int i = 0; i < this.fragmentsStack.size() - 1; i++) {
                BaseFragment baseFragment = (BaseFragment) this.fragmentsStack.get(i);
                actionBar = baseFragment.actionBar;
                if (actionBar != null && actionBar.getAddToContainer()) {
                    viewGroup = (ViewGroup) baseFragment.actionBar.getParent();
                    if (viewGroup != null) {
                        viewGroup.removeView(baseFragment.actionBar);
                    }
                }
                View view = baseFragment.fragmentView;
                if (view != null) {
                    viewGroup = (ViewGroup) view.getParent();
                    if (viewGroup != null) {
                        baseFragment.onPause();
                        baseFragment.onRemoveFromParent();
                        viewGroup.removeView(baseFragment.fragmentView);
                    }
                }
            }
            ArrayList arrayList = this.fragmentsStack;
            BaseFragment baseFragment2 = (BaseFragment) arrayList.get(arrayList.size() - 1);
            baseFragment2.setParentLayout(this);
            View view2 = baseFragment2.fragmentView;
            if (view2 == null) {
                view2 = baseFragment2.createView(this.parentActivity);
            } else {
                viewGroup = (ViewGroup) view2.getParent();
                if (viewGroup != null) {
                    baseFragment2.onRemoveFromParent();
                    viewGroup.removeView(view2);
                }
            }
            actionBar = baseFragment2.actionBar;
            if (actionBar != null && actionBar.getAddToContainer()) {
                if (this.removeActionBarExtraHeight) {
                    baseFragment2.actionBar.setOccupyStatusBar(false);
                }
                ViewGroup viewGroup2 = (ViewGroup) baseFragment2.actionBar.getParent();
                if (viewGroup2 != null) {
                    viewGroup2.removeView(baseFragment2.actionBar);
                }
                this.containerView.addView(baseFragment2.actionBar);
                baseFragment2.actionBar.setTitleOverlayText(this.titleOverlayText, this.titleOverlayTextId, this.overlayAction);
            }
            this.containerView.addView(view2, LayoutHelper.createLinear(-1, -1));
            baseFragment2.onResume();
            this.currentActionBar = baseFragment2.actionBar;
            if (!baseFragment2.hasOwnBackground && view2.getBackground() == null) {
                view2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
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
        for (int i = 0; i < 2; i++) {
            if (this.themeAnimatorDescriptions[i] != null) {
                for (int i2 = 0; i2 < this.themeAnimatorDescriptions[i].length; i2++) {
                    int red = Color.red(this.animateEndColors[i][i2]);
                    int green = Color.green(this.animateEndColors[i][i2]);
                    int blue = Color.blue(this.animateEndColors[i][i2]);
                    int alpha = Color.alpha(this.animateEndColors[i][i2]);
                    int red2 = Color.red(this.animateStartColors[i][i2]);
                    int green2 = Color.green(this.animateStartColors[i][i2]);
                    int blue2 = Color.blue(this.animateStartColors[i][i2]);
                    int alpha2 = Color.alpha(this.animateStartColors[i][i2]);
                    red = Color.argb(Math.min(255, (int) (((float) alpha2) + (((float) (alpha - alpha2)) * f))), Math.min(255, (int) (((float) red2) + (((float) (red - red2)) * f))), Math.min(255, (int) (((float) green2) + (((float) (green - green2)) * f))), Math.min(255, (int) (((float) blue2) + (((float) (blue - blue2)) * f))));
                    Theme.setAnimatedColor(this.themeAnimatorDescriptions[i][i2].getCurrentKey(), red);
                    this.themeAnimatorDescriptions[i][i2].setColor(red, false, false);
                }
                ThemeDescriptionDelegate[] themeDescriptionDelegateArr = this.themeAnimatorDelegate;
                if (themeDescriptionDelegateArr[i] != null) {
                    themeDescriptionDelegateArr[i].didSetColor();
                }
            }
        }
        if (this.presentingFragmentDescriptions != null) {
            int i3 = 0;
            while (true) {
                ThemeDescription[] themeDescriptionArr = this.presentingFragmentDescriptions;
                if (i3 < themeDescriptionArr.length) {
                    this.presentingFragmentDescriptions[i3].setColor(Theme.getColor(themeDescriptionArr[i3].getCurrentKey()), false, false);
                    i3++;
                } else {
                    return;
                }
            }
        }
    }

    @Keep
    public float getThemeAnimationValue() {
        return this.themeAnimationValue;
    }

    public void animateThemedValues(ThemeInfo themeInfo, int i, boolean z, boolean z2) {
        if (this.transitionAnimationInProgress || this.startedTracking) {
            this.animateThemeAfterAnimation = true;
            this.animateSetThemeAfterAnimation = themeInfo;
            this.animateSetThemeNightAfterAnimation = z;
            this.animateSetThemeAccentIdAfterAnimation = i;
            return;
        }
        AnimatorSet animatorSet = this.themeAnimatorSet;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.themeAnimatorSet = null;
        }
        int i2 = 0;
        Object obj = null;
        for (int i3 = 0; i3 < 2; i3++) {
            BaseFragment lastFragment;
            if (i3 == 0) {
                lastFragment = getLastFragment();
            } else if ((this.inPreviewMode || this.transitionAnimationPreviewMode) && this.fragmentsStack.size() > 1) {
                ArrayList arrayList = this.fragmentsStack;
                lastFragment = (BaseFragment) arrayList.get(arrayList.size() - 2);
            } else {
                this.themeAnimatorDescriptions[i3] = null;
                this.animateStartColors[i3] = null;
                this.animateEndColors[i3] = null;
                this.themeAnimatorDelegate[i3] = null;
            }
            if (lastFragment != null) {
                ThemeDescription[][] themeDescriptionArr;
                this.themeAnimatorDescriptions[i3] = lastFragment.getThemeDescriptions();
                this.animateStartColors[i3] = new int[this.themeAnimatorDescriptions[i3].length];
                int i4 = 0;
                while (true) {
                    themeDescriptionArr = this.themeAnimatorDescriptions;
                    if (i4 >= themeDescriptionArr[i3].length) {
                        break;
                    }
                    this.animateStartColors[i3][i4] = themeDescriptionArr[i3][i4].getSetColor();
                    ThemeDescriptionDelegate delegateDisabled = this.themeAnimatorDescriptions[i3][i4].setDelegateDisabled();
                    ThemeDescriptionDelegate[] themeDescriptionDelegateArr = this.themeAnimatorDelegate;
                    if (themeDescriptionDelegateArr[i3] == null && delegateDisabled != null) {
                        themeDescriptionDelegateArr[i3] = delegateDisabled;
                    }
                    i4++;
                }
                if (i3 == 0) {
                    if (i != -1) {
                        themeInfo.setCurrentAccentId(i);
                        Theme.saveThemeAccents(themeInfo, true, false, true, false);
                    }
                    Theme.applyTheme(themeInfo, z);
                }
                this.animateEndColors[i3] = new int[this.themeAnimatorDescriptions[i3].length];
                i4 = 0;
                while (true) {
                    themeDescriptionArr = this.themeAnimatorDescriptions;
                    if (i4 >= themeDescriptionArr[i3].length) {
                        break;
                    }
                    this.animateEndColors[i3][i4] = themeDescriptionArr[i3][i4].getSetColor();
                    i4++;
                }
                obj = 1;
            }
        }
        if (obj != null) {
            int size = this.fragmentsStack.size();
            i = (this.inPreviewMode || this.transitionAnimationPreviewMode) ? 2 : 1;
            size -= i;
            for (i = 0; i < size; i++) {
                BaseFragment baseFragment = (BaseFragment) this.fragmentsStack.get(i);
                baseFragment.clearViews();
                baseFragment.setParentLayout(this);
            }
            if (z2) {
                setThemeAnimationValue(1.0f);
                while (i2 < 2) {
                    this.themeAnimatorDescriptions[i2] = null;
                    this.animateStartColors[i2] = null;
                    this.animateEndColors[i2] = null;
                    this.themeAnimatorDelegate[i2] = null;
                    i2++;
                }
                this.presentingFragmentDescriptions = null;
                return;
            }
            Theme.setAnimatingColor(true);
            this.themeAnimatorSet = new AnimatorSet();
            this.themeAnimatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (animator.equals(ActionBarLayout.this.themeAnimatorSet)) {
                        for (int i = 0; i < 2; i++) {
                            ActionBarLayout.this.themeAnimatorDescriptions[i] = null;
                            ActionBarLayout.this.animateStartColors[i] = null;
                            ActionBarLayout.this.animateEndColors[i] = null;
                            ActionBarLayout.this.themeAnimatorDelegate[i] = null;
                        }
                        Theme.setAnimatingColor(false);
                        ActionBarLayout.this.presentingFragmentDescriptions = null;
                        ActionBarLayout.this.themeAnimatorSet = null;
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    if (animator.equals(ActionBarLayout.this.themeAnimatorSet)) {
                        for (int i = 0; i < 2; i++) {
                            ActionBarLayout.this.themeAnimatorDescriptions[i] = null;
                            ActionBarLayout.this.animateStartColors[i] = null;
                            ActionBarLayout.this.animateEndColors[i] = null;
                            ActionBarLayout.this.themeAnimatorDelegate[i] = null;
                        }
                        Theme.setAnimatingColor(false);
                        ActionBarLayout.this.presentingFragmentDescriptions = null;
                        ActionBarLayout.this.themeAnimatorSet = null;
                    }
                }
            });
            AnimatorSet animatorSet2 = this.themeAnimatorSet;
            Animator[] animatorArr = new Animator[1];
            animatorArr[0] = ObjectAnimator.ofFloat(this, "themeAnimationValue", new float[]{0.0f, 1.0f});
            animatorSet2.playTogether(animatorArr);
            this.themeAnimatorSet.setDuration(200);
            this.themeAnimatorSet.start();
        }
    }

    public void rebuildAllFragmentViews(boolean z, boolean z2) {
        if (this.transitionAnimationInProgress || this.startedTracking) {
            this.rebuildAfterAnimation = true;
            this.rebuildLastAfterAnimation = z;
            this.showLastAfterAnimation = z2;
            return;
        }
        int size = this.fragmentsStack.size();
        if (!z) {
            size--;
        }
        if (this.inPreviewMode) {
            size--;
        }
        for (int i = 0; i < size; i++) {
            ((BaseFragment) this.fragmentsStack.get(i)).clearViews();
            ((BaseFragment) this.fragmentsStack.get(i)).setParentLayout(this);
        }
        ActionBarLayoutDelegate actionBarLayoutDelegate = this.delegate;
        if (actionBarLayoutDelegate != null) {
            actionBarLayoutDelegate.onRebuildAllFragments(this, z);
        }
        if (z2) {
            showLastFragment();
        }
    }

    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        if (!(i != 82 || checkTransitionAnimation() || this.startedTracking)) {
            ActionBar actionBar = this.currentActionBar;
            if (actionBar != null) {
                actionBar.onMenuButtonPressed();
            }
        }
        return super.onKeyUp(i, keyEvent);
    }

    public void onActionModeStarted(Object obj) {
        ActionBar actionBar = this.currentActionBar;
        if (actionBar != null) {
            actionBar.setVisibility(8);
        }
        this.inActionMode = true;
    }

    public void onActionModeFinished(Object obj) {
        ActionBar actionBar = this.currentActionBar;
        if (actionBar != null) {
            actionBar.setVisibility(0);
        }
        this.inActionMode = false;
    }

    private void onCloseAnimationEnd() {
        if (this.transitionAnimationInProgress) {
            Runnable runnable = this.onCloseAnimationEndRunnable;
            if (runnable != null) {
                this.transitionAnimationInProgress = false;
                this.transitionAnimationPreviewMode = false;
                this.transitionAnimationStartTime = 0;
                runnable.run();
                this.onCloseAnimationEndRunnable = null;
                checkNeedRebuild();
            }
        }
    }

    private void checkNeedRebuild() {
        if (this.rebuildAfterAnimation) {
            rebuildAllFragmentViews(this.rebuildLastAfterAnimation, this.showLastAfterAnimation);
            this.rebuildAfterAnimation = false;
        } else if (this.animateThemeAfterAnimation) {
            animateThemedValues(this.animateSetThemeAfterAnimation, this.animateSetThemeAccentIdAfterAnimation, this.animateSetThemeNightAfterAnimation, false);
            this.animateSetThemeAfterAnimation = null;
            this.animateThemeAfterAnimation = false;
        }
    }

    private void onOpenAnimationEnd() {
        if (this.transitionAnimationInProgress) {
            Runnable runnable = this.onOpenAnimationEndRunnable;
            if (runnable != null) {
                this.transitionAnimationInProgress = false;
                this.transitionAnimationPreviewMode = false;
                this.transitionAnimationStartTime = 0;
                runnable.run();
                this.onOpenAnimationEndRunnable = null;
                checkNeedRebuild();
            }
        }
    }

    public void startActivityForResult(Intent intent, int i) {
        Activity activity = this.parentActivity;
        if (activity != null) {
            if (this.transitionAnimationInProgress) {
                AnimatorSet animatorSet = this.currentAnimation;
                if (animatorSet != null) {
                    animatorSet.cancel();
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
                activity.startActivityForResult(intent, i);
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

    public void setTitleOverlayText(String str, int i, Runnable runnable) {
        this.titleOverlayText = str;
        this.titleOverlayTextId = i;
        this.overlayAction = runnable;
        for (int i2 = 0; i2 < this.fragmentsStack.size(); i2++) {
            ActionBar actionBar = ((BaseFragment) this.fragmentsStack.get(i2)).actionBar;
            if (actionBar != null) {
                actionBar.setTitleOverlayText(this.titleOverlayText, this.titleOverlayTextId, runnable);
            }
        }
    }

    public boolean extendActionMode(Menu menu) {
        if (!this.fragmentsStack.isEmpty()) {
            ArrayList arrayList = this.fragmentsStack;
            if (((BaseFragment) arrayList.get(arrayList.size() - 1)).extendActionMode(menu)) {
                return true;
            }
        }
        return false;
    }
}
