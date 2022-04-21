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
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
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
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.SharedConfig;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.BackButtonMenu;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.GroupCallPip;
import org.telegram.ui.Components.LayoutHelper;

public class ActionBarLayout extends FrameLayout {
    /* access modifiers changed from: private */
    public static Drawable headerShadowDrawable;
    private static Drawable layerShadowDrawable;
    private static Paint scrimPaint;
    private AccelerateDecelerateInterpolator accelerateDecelerateInterpolator = new AccelerateDecelerateInterpolator();
    /* access modifiers changed from: private */
    public ArrayList<int[]> animateEndColors = new ArrayList<>();
    private int animateSetThemeAccentIdAfterAnimation;
    private Theme.ThemeInfo animateSetThemeAfterAnimation;
    private boolean animateSetThemeNightAfterAnimation;
    /* access modifiers changed from: private */
    public ArrayList<int[]> animateStartColors = new ArrayList<>();
    private boolean animateThemeAfterAnimation;
    protected boolean animationInProgress;
    /* access modifiers changed from: private */
    public float animationProgress;
    public ThemeAnimationSettings.onAnimationProgress animationProgressListener;
    /* access modifiers changed from: private */
    public Runnable animationRunnable;
    private View backgroundView;
    private boolean beginTrackingSent;
    public LayoutContainer containerView;
    /* access modifiers changed from: private */
    public LayoutContainer containerViewBack;
    private ActionBar currentActionBar;
    private AnimatorSet currentAnimation;
    /* access modifiers changed from: private */
    public DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator(1.5f);
    /* access modifiers changed from: private */
    public boolean delayedAnimationResumed;
    /* access modifiers changed from: private */
    public Runnable delayedOpenAnimationRunnable;
    private ActionBarLayoutDelegate delegate;
    private DrawerLayoutContainer drawerLayoutContainer;
    public ArrayList<BaseFragment> fragmentsStack;
    private boolean inActionMode;
    private boolean inBubbleMode;
    /* access modifiers changed from: private */
    public boolean inPreviewMode;
    public float innerTranslationX;
    /* access modifiers changed from: private */
    public long lastFrameTime;
    private View layoutToIgnore;
    private boolean maybeStartTracking;
    public Theme.MessageDrawable messageDrawableOutMediaStart;
    public Theme.MessageDrawable messageDrawableOutStart;
    /* access modifiers changed from: private */
    public BaseFragment newFragment;
    /* access modifiers changed from: private */
    public BaseFragment oldFragment;
    private Runnable onCloseAnimationEndRunnable;
    private Runnable onFragmentStackChangedListener;
    private Runnable onOpenAnimationEndRunnable;
    private Runnable overlayAction;
    private int overrideWidthOffset = -1;
    protected Activity parentActivity;
    /* access modifiers changed from: private */
    public ArrayList<ThemeDescription> presentingFragmentDescriptions;
    /* access modifiers changed from: private */
    public ColorDrawable previewBackgroundDrawable;
    /* access modifiers changed from: private */
    public View previewMenu;
    /* access modifiers changed from: private */
    public boolean previewOpenAnimationInProgress;
    public ArrayList<BackButtonMenu.PulledDialog> pulledDialogs;
    private boolean rebuildAfterAnimation;
    private boolean rebuildLastAfterAnimation;
    private Rect rect = new Rect();
    private boolean removeActionBarExtraHeight;
    private boolean showLastAfterAnimation;
    StartColorsProvider startColorsProvider = new StartColorsProvider();
    protected boolean startedTracking;
    private int startedTrackingPointerId;
    private int startedTrackingX;
    private int startedTrackingY;
    private float themeAnimationValue;
    /* access modifiers changed from: private */
    public ArrayList<ThemeDescription.ThemeDescriptionDelegate> themeAnimatorDelegate = new ArrayList<>();
    /* access modifiers changed from: private */
    public ArrayList<ArrayList<ThemeDescription>> themeAnimatorDescriptions = new ArrayList<>();
    /* access modifiers changed from: private */
    public AnimatorSet themeAnimatorSet;
    private String titleOverlayText;
    private int titleOverlayTextId;
    private boolean transitionAnimationInProgress;
    /* access modifiers changed from: private */
    public boolean transitionAnimationPreviewMode;
    /* access modifiers changed from: private */
    public long transitionAnimationStartTime;
    private boolean useAlphaAnimations;
    private VelocityTracker velocityTracker;
    /* access modifiers changed from: private */
    public Runnable waitingForKeyboardCloseRunnable;

    public interface ActionBarLayoutDelegate {
        boolean needAddFragmentToStack(BaseFragment baseFragment, ActionBarLayout actionBarLayout);

        boolean needCloseLastFragment(ActionBarLayout actionBarLayout);

        boolean needPresentFragment(BaseFragment baseFragment, boolean z, boolean z2, ActionBarLayout actionBarLayout);

        boolean onPreIme();

        void onRebuildAllFragments(ActionBarLayout actionBarLayout, boolean z);
    }

    static /* synthetic */ float access$1116(ActionBarLayout x0, float x1) {
        float f = x0.animationProgress + x1;
        x0.animationProgress = f;
        return f;
    }

    public class LayoutContainer extends FrameLayout {
        private int backgroundColor;
        private Paint backgroundPaint = new Paint();
        private int fragmentPanTranslationOffset;
        /* access modifiers changed from: private */
        public boolean isKeyboardVisible;
        private Rect rect = new Rect();

        public LayoutContainer(Context context) {
            super(context);
            setWillNotDraw(false);
        }

        /* access modifiers changed from: protected */
        public boolean drawChild(Canvas canvas, View child, long drawingTime) {
            if (child instanceof ActionBar) {
                return super.drawChild(canvas, child, drawingTime);
            }
            int actionBarHeight = 0;
            int actionBarY = 0;
            int childCount = getChildCount();
            int a = 0;
            while (true) {
                if (a >= childCount) {
                    break;
                }
                View view = getChildAt(a);
                if (view == child || !(view instanceof ActionBar) || view.getVisibility() != 0) {
                    a++;
                } else if (((ActionBar) view).getCastShadows()) {
                    actionBarHeight = view.getMeasuredHeight();
                    actionBarY = (int) view.getY();
                }
            }
            boolean result = super.drawChild(canvas, child, drawingTime);
            if (!(actionBarHeight == 0 || ActionBarLayout.headerShadowDrawable == null)) {
                ActionBarLayout.headerShadowDrawable.setBounds(0, actionBarY + actionBarHeight, getMeasuredWidth(), actionBarY + actionBarHeight + ActionBarLayout.headerShadowDrawable.getIntrinsicHeight());
                ActionBarLayout.headerShadowDrawable.draw(canvas);
            }
            return result;
        }

        public boolean hasOverlappingRendering() {
            if (Build.VERSION.SDK_INT >= 28) {
                return true;
            }
            return false;
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int height = View.MeasureSpec.getSize(heightMeasureSpec);
            int count = getChildCount();
            int actionBarHeight = 0;
            int a = 0;
            while (true) {
                if (a >= count) {
                    break;
                }
                View child = getChildAt(a);
                if (child instanceof ActionBar) {
                    child.measure(View.MeasureSpec.makeMeasureSpec(width, NUM), View.MeasureSpec.makeMeasureSpec(height, 0));
                    actionBarHeight = child.getMeasuredHeight();
                    break;
                }
                a++;
            }
            for (int a2 = 0; a2 < count; a2++) {
                View child2 = getChildAt(a2);
                if (!(child2 instanceof ActionBar)) {
                    if (child2.getFitsSystemWindows()) {
                        measureChildWithMargins(child2, widthMeasureSpec, 0, heightMeasureSpec, 0);
                    } else {
                        measureChildWithMargins(child2, widthMeasureSpec, 0, heightMeasureSpec, actionBarHeight);
                    }
                }
            }
            setMeasuredDimension(width, height);
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean changed, int l, int t, int r, int b) {
            boolean z;
            int count = getChildCount();
            int actionBarHeight = 0;
            int a = 0;
            while (true) {
                z = false;
                if (a >= count) {
                    break;
                }
                View child = getChildAt(a);
                if (child instanceof ActionBar) {
                    actionBarHeight = child.getMeasuredHeight();
                    child.layout(0, 0, child.getMeasuredWidth(), actionBarHeight);
                    break;
                }
                a++;
            }
            for (int a2 = 0; a2 < count; a2++) {
                View child2 = getChildAt(a2);
                if (!(child2 instanceof ActionBar)) {
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) child2.getLayoutParams();
                    if (child2.getFitsSystemWindows()) {
                        child2.layout(layoutParams.leftMargin, layoutParams.topMargin, layoutParams.leftMargin + child2.getMeasuredWidth(), layoutParams.topMargin + child2.getMeasuredHeight());
                    } else {
                        child2.layout(layoutParams.leftMargin, layoutParams.topMargin + actionBarHeight, layoutParams.leftMargin + child2.getMeasuredWidth(), layoutParams.topMargin + actionBarHeight + child2.getMeasuredHeight());
                    }
                }
            }
            View rootView = getRootView();
            getWindowVisibleDisplayFrame(this.rect);
            if (((rootView.getHeight() - (this.rect.top != 0 ? AndroidUtilities.statusBarHeight : 0)) - AndroidUtilities.getViewInset(rootView)) - (this.rect.bottom - this.rect.top) > 0) {
                z = true;
            }
            this.isKeyboardVisible = z;
            if (ActionBarLayout.this.waitingForKeyboardCloseRunnable != null && !ActionBarLayout.this.containerView.isKeyboardVisible && !ActionBarLayout.this.containerViewBack.isKeyboardVisible) {
                AndroidUtilities.cancelRunOnUIThread(ActionBarLayout.this.waitingForKeyboardCloseRunnable);
                ActionBarLayout.this.waitingForKeyboardCloseRunnable.run();
                Runnable unused = ActionBarLayout.this.waitingForKeyboardCloseRunnable = null;
            }
        }

        /* JADX WARNING: Code restructure failed: missing block: B:16:0x0033, code lost:
            if (r5 != r5.this$0.containerView) goto L_0x0035;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean dispatchTouchEvent(android.view.MotionEvent r6) {
            /*
                r5 = this;
                org.telegram.ui.ActionBar.ActionBarLayout r0 = org.telegram.ui.ActionBar.ActionBarLayout.this
                boolean r0 = r0.inPreviewMode
                r1 = 1
                r2 = 0
                if (r0 == 0) goto L_0x0014
                org.telegram.ui.ActionBar.ActionBarLayout r0 = org.telegram.ui.ActionBar.ActionBarLayout.this
                android.view.View r0 = r0.previewMenu
                if (r0 != 0) goto L_0x0014
                r0 = 1
                goto L_0x0015
            L_0x0014:
                r0 = 0
            L_0x0015:
                if (r0 != 0) goto L_0x001f
                org.telegram.ui.ActionBar.ActionBarLayout r3 = org.telegram.ui.ActionBar.ActionBarLayout.this
                boolean r3 = r3.transitionAnimationPreviewMode
                if (r3 == 0) goto L_0x002d
            L_0x001f:
                int r3 = r6.getActionMasked()
                if (r3 == 0) goto L_0x0043
                int r3 = r6.getActionMasked()
                r4 = 5
                if (r3 != r4) goto L_0x002d
                goto L_0x0043
            L_0x002d:
                if (r0 == 0) goto L_0x0035
                org.telegram.ui.ActionBar.ActionBarLayout r3 = org.telegram.ui.ActionBar.ActionBarLayout.this     // Catch:{ all -> 0x003e }
                org.telegram.ui.ActionBar.ActionBarLayout$LayoutContainer r3 = r3.containerView     // Catch:{ all -> 0x003e }
                if (r5 == r3) goto L_0x003c
            L_0x0035:
                boolean r3 = super.dispatchTouchEvent(r6)     // Catch:{ all -> 0x003e }
                if (r3 == 0) goto L_0x003c
                goto L_0x003d
            L_0x003c:
                r1 = 0
            L_0x003d:
                return r1
            L_0x003e:
                r1 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
                return r2
            L_0x0043:
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.ActionBarLayout.LayoutContainer.dispatchTouchEvent(android.view.MotionEvent):boolean");
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.fragmentPanTranslationOffset != 0) {
                if (this.backgroundColor != Theme.getColor("windowBackgroundWhite")) {
                    Paint paint = this.backgroundPaint;
                    int color = Theme.getColor("windowBackgroundWhite");
                    this.backgroundColor = color;
                    paint.setColor(color);
                }
                canvas.drawRect(0.0f, (float) ((getMeasuredHeight() - this.fragmentPanTranslationOffset) - 3), (float) getMeasuredWidth(), (float) getMeasuredHeight(), this.backgroundPaint);
            }
            super.onDraw(canvas);
        }

        public void setFragmentPanTranslationOffset(int fragmentPanTranslationOffset2) {
            this.fragmentPanTranslationOffset = fragmentPanTranslationOffset2;
            invalidate();
        }
    }

    public static class ThemeAnimationSettings {
        public final int accentId;
        public Runnable afterAnimationRunnable;
        public Runnable afterStartDescriptionsAddedRunnable;
        public onAnimationProgress animationProgress;
        public boolean applyTheme = true;
        public Runnable beforeAnimationRunnable;
        public long duration = 200;
        public final boolean instant;
        public final boolean nightTheme;
        public boolean onlyTopFragment;
        public Theme.ResourcesProvider resourcesProvider;
        public final Theme.ThemeInfo theme;

        public interface onAnimationProgress {
            void setProgress(float f);
        }

        public ThemeAnimationSettings(Theme.ThemeInfo theme2, int accentId2, boolean nightTheme2, boolean instant2) {
            this.theme = theme2;
            this.accentId = accentId2;
            this.nightTheme = nightTheme2;
            this.instant = instant2;
        }
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

    public void init(ArrayList<BaseFragment> stack) {
        this.fragmentsStack = stack;
        LayoutContainer layoutContainer = new LayoutContainer(this.parentActivity);
        this.containerViewBack = layoutContainer;
        addView(layoutContainer);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.containerViewBack.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        layoutParams.gravity = 51;
        this.containerViewBack.setLayoutParams(layoutParams);
        LayoutContainer layoutContainer2 = new LayoutContainer(this.parentActivity);
        this.containerView = layoutContainer2;
        addView(layoutContainer2);
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.containerView.getLayoutParams();
        layoutParams2.width = -1;
        layoutParams2.height = -1;
        layoutParams2.gravity = 51;
        this.containerView.setLayoutParams(layoutParams2);
        Iterator<BaseFragment> it = this.fragmentsStack.iterator();
        while (it.hasNext()) {
            it.next().setParentLayout(this);
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (!this.fragmentsStack.isEmpty()) {
            int N = this.fragmentsStack.size();
            for (int a = 0; a < N; a++) {
                BaseFragment fragment = this.fragmentsStack.get(a);
                fragment.onConfigurationChanged(newConfig);
                if (fragment.visibleDialog instanceof BottomSheet) {
                    ((BottomSheet) fragment.visibleDialog).onConfigurationChanged(newConfig);
                }
            }
        }
    }

    public void drawHeaderShadow(Canvas canvas, int y) {
        drawHeaderShadow(canvas, 255, y);
    }

    public void setInBubbleMode(boolean value) {
        this.inBubbleMode = value;
    }

    public boolean isInBubbleMode() {
        return this.inBubbleMode;
    }

    public void drawHeaderShadow(Canvas canvas, int alpha, int y) {
        if (headerShadowDrawable != null) {
            if (Build.VERSION.SDK_INT < 19) {
                headerShadowDrawable.setAlpha(alpha);
            } else if (headerShadowDrawable.getAlpha() != alpha) {
                headerShadowDrawable.setAlpha(alpha);
            }
            headerShadowDrawable.setBounds(0, y, getMeasuredWidth(), headerShadowDrawable.getIntrinsicHeight() + y);
            headerShadowDrawable.draw(canvas);
        }
    }

    public void setInnerTranslationX(float value) {
        int currNavigationBarColor;
        int prevNavigationBarColor;
        this.innerTranslationX = value;
        invalidate();
        if (this.fragmentsStack.size() >= 2 && this.containerView.getMeasuredWidth() > 0) {
            float progress = value / ((float) this.containerView.getMeasuredWidth());
            ArrayList<BaseFragment> arrayList = this.fragmentsStack;
            BaseFragment prevFragment = arrayList.get(arrayList.size() - 2);
            int newStatusBarColor = 0;
            prevFragment.onSlideProgress(false, progress);
            ArrayList<BaseFragment> arrayList2 = this.fragmentsStack;
            BaseFragment currFragment = arrayList2.get(arrayList2.size() - 1);
            float ratio = MathUtils.clamp(2.0f * progress, 0.0f, 1.0f);
            if (currFragment.isBeginToShow() && (currNavigationBarColor = currFragment.getNavigationBarColor()) != (prevNavigationBarColor = prevFragment.getNavigationBarColor())) {
                currFragment.setNavigationBarColor(ColorUtils.blendARGB(currNavigationBarColor, prevNavigationBarColor, ratio));
            }
            if (currFragment != null && !currFragment.inPreviewMode && Build.VERSION.SDK_INT >= 23 && !SharedConfig.noStatusBar) {
                int overlayColor = Theme.getColor("actionBarDefault") == -1 ? NUM : NUM;
                int oldStatusBarColor = (prevFragment == null || !prevFragment.hasForceLightStatusBar()) ? overlayColor : 0;
                if (currFragment == null || !currFragment.hasForceLightStatusBar()) {
                    newStatusBarColor = overlayColor;
                }
                this.parentActivity.getWindow().setStatusBarColor(ColorUtils.blendARGB(newStatusBarColor, oldStatusBarColor, ratio));
            }
        }
    }

    public float getInnerTranslationX() {
        return this.innerTranslationX;
    }

    public void dismissDialogs() {
        if (!this.fragmentsStack.isEmpty()) {
            ArrayList<BaseFragment> arrayList = this.fragmentsStack;
            arrayList.get(arrayList.size() - 1).dismissCurrentDialog();
        }
    }

    public void onResume() {
        if (this.transitionAnimationInProgress) {
            AnimatorSet animatorSet = this.currentAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.currentAnimation = null;
            }
            Runnable runnable = this.animationRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.animationRunnable = null;
            }
            Runnable runnable2 = this.waitingForKeyboardCloseRunnable;
            if (runnable2 != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable2);
                this.waitingForKeyboardCloseRunnable = null;
            }
            if (this.onCloseAnimationEndRunnable != null) {
                onCloseAnimationEnd();
            } else if (this.onOpenAnimationEndRunnable != null) {
                onOpenAnimationEnd();
            }
        }
        if (!this.fragmentsStack.isEmpty()) {
            ArrayList<BaseFragment> arrayList = this.fragmentsStack;
            arrayList.get(arrayList.size() - 1).onResume();
        }
    }

    public void onPause() {
        if (!this.fragmentsStack.isEmpty()) {
            ArrayList<BaseFragment> arrayList = this.fragmentsStack;
            arrayList.get(arrayList.size() - 1).onPause();
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return this.animationInProgress || checkTransitionAnimation() || onTouchEvent(ev);
    }

    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        onTouchEvent((MotionEvent) null);
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        if (event == null || event.getKeyCode() != 4 || event.getAction() != 1) {
            return super.dispatchKeyEventPreIme(event);
        }
        ActionBarLayoutDelegate actionBarLayoutDelegate = this.delegate;
        if ((actionBarLayoutDelegate == null || !actionBarLayoutDelegate.onPreIme()) && !super.dispatchKeyEventPreIme(event)) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean drawChild(Canvas canvas, View child, long drawingTime) {
        int clipRight;
        int clipLeft;
        LayoutContainer layoutContainer;
        Canvas canvas2 = canvas;
        View view = child;
        DrawerLayoutContainer drawerLayoutContainer2 = this.drawerLayoutContainer;
        if (drawerLayoutContainer2 != null && drawerLayoutContainer2.isDrawCurrentPreviewFragmentAbove() && (this.inPreviewMode || this.transitionAnimationPreviewMode || this.previewOpenAnimationInProgress)) {
            BaseFragment baseFragment = this.oldFragment;
            if (view == ((baseFragment == null || !baseFragment.inPreviewMode) ? this.containerView : this.containerViewBack)) {
                this.drawerLayoutContainer.invalidate();
                return false;
            }
        }
        int width = (getWidth() - getPaddingLeft()) - getPaddingRight();
        int translationX = ((int) this.innerTranslationX) + getPaddingRight();
        int clipLeft2 = getPaddingLeft();
        int clipRight2 = getPaddingLeft() + width;
        if (view == this.containerViewBack) {
            clipLeft = clipLeft2;
            clipRight = translationX + AndroidUtilities.dp(1.0f);
        } else if (view == this.containerView) {
            clipLeft = translationX;
            clipRight = clipRight2;
        } else {
            clipLeft = clipLeft2;
            clipRight = clipRight2;
        }
        int restoreCount = canvas.save();
        if (!isTransitionAnimationInProgress() && !this.inPreviewMode) {
            canvas2.clipRect(clipLeft, 0, clipRight, getHeight());
        }
        if ((this.inPreviewMode || this.transitionAnimationPreviewMode) && view == (layoutContainer = this.containerView)) {
            drawPreviewDrawables(canvas2, layoutContainer);
        }
        boolean result = super.drawChild(canvas, child, drawingTime);
        canvas2.restoreToCount(restoreCount);
        if (!(translationX == 0 && this.overrideWidthOffset == -1)) {
            int i = this.overrideWidthOffset;
            if (i == -1) {
                i = width - translationX;
            }
            int widthOffset = i;
            if (view == this.containerView) {
                float alpha = MathUtils.clamp(((float) widthOffset) / ((float) AndroidUtilities.dp(20.0f)), 0.0f, 1.0f);
                Drawable drawable = layerShadowDrawable;
                drawable.setBounds(translationX - drawable.getIntrinsicWidth(), child.getTop(), translationX, child.getBottom());
                layerShadowDrawable.setAlpha((int) (255.0f * alpha));
                layerShadowDrawable.draw(canvas2);
            } else if (view == this.containerViewBack) {
                scrimPaint.setColor(Color.argb((int) (153.0f * MathUtils.clamp(((float) widthOffset) / ((float) width), 0.0f, 0.8f)), 0, 0, 0));
                if (this.overrideWidthOffset != -1) {
                    canvas.drawRect(0.0f, 0.0f, (float) getWidth(), (float) getHeight(), scrimPaint);
                } else {
                    canvas.drawRect((float) clipLeft, 0.0f, (float) clipRight, (float) getHeight(), scrimPaint);
                }
            }
        }
        return result;
    }

    public void setOverrideWidthOffset(int overrideWidthOffset2) {
        this.overrideWidthOffset = overrideWidthOffset2;
        invalidate();
    }

    public float getCurrentPreviewFragmentAlpha() {
        if (!this.inPreviewMode && !this.transitionAnimationPreviewMode && !this.previewOpenAnimationInProgress) {
            return 0.0f;
        }
        BaseFragment baseFragment = this.oldFragment;
        return ((baseFragment == null || !baseFragment.inPreviewMode) ? this.containerView : this.containerViewBack).getAlpha();
    }

    public void drawCurrentPreviewFragment(Canvas canvas, Drawable foregroundDrawable) {
        if (this.inPreviewMode || this.transitionAnimationPreviewMode || this.previewOpenAnimationInProgress) {
            BaseFragment baseFragment = this.oldFragment;
            ViewGroup v = (baseFragment == null || !baseFragment.inPreviewMode) ? this.containerView : this.containerViewBack;
            drawPreviewDrawables(canvas, v);
            if (v.getAlpha() < 1.0f) {
                canvas.saveLayerAlpha(0.0f, 0.0f, (float) getWidth(), (float) getHeight(), (int) (v.getAlpha() * 255.0f), 31);
            } else {
                canvas.save();
            }
            canvas.concat(v.getMatrix());
            v.draw(canvas);
            if (foregroundDrawable != null) {
                int i = 0;
                View child = v.getChildAt(0);
                if (child != null) {
                    ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) child.getLayoutParams();
                    Rect rect2 = new Rect();
                    child.getLocalVisibleRect(rect2);
                    rect2.offset(lp.leftMargin, lp.topMargin);
                    int i2 = rect2.top;
                    if (Build.VERSION.SDK_INT >= 21) {
                        i = AndroidUtilities.statusBarHeight - 1;
                    }
                    rect2.top = i2 + i;
                    foregroundDrawable.setAlpha((int) (v.getAlpha() * 255.0f));
                    foregroundDrawable.setBounds(rect2);
                    foregroundDrawable.draw(canvas);
                }
            }
            canvas.restore();
        }
    }

    private void drawPreviewDrawables(Canvas canvas, ViewGroup containerView2) {
        int i = 0;
        View view = containerView2.getChildAt(0);
        if (view != null) {
            this.previewBackgroundDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
            this.previewBackgroundDrawable.draw(canvas);
            if (this.previewMenu == null) {
                int x = (getMeasuredWidth() - AndroidUtilities.dp(24.0f)) / 2;
                float top = ((float) view.getTop()) + containerView2.getTranslationY();
                if (Build.VERSION.SDK_INT < 21) {
                    i = 20;
                }
                int y = (int) (top - ((float) AndroidUtilities.dp((float) (i + 12))));
                Theme.moveUpDrawable.setBounds(x, y, AndroidUtilities.dp(24.0f) + x, AndroidUtilities.dp(24.0f) + y);
                Theme.moveUpDrawable.draw(canvas);
            }
        }
    }

    public void setDelegate(ActionBarLayoutDelegate actionBarLayoutDelegate) {
        this.delegate = actionBarLayoutDelegate;
    }

    /* access modifiers changed from: private */
    public void onSlideAnimationEnd(boolean backAnimation) {
        ViewGroup parent;
        ViewGroup parent2;
        if (backAnimation) {
            if (this.fragmentsStack.size() >= 2) {
                ArrayList<BaseFragment> arrayList = this.fragmentsStack;
                arrayList.get(arrayList.size() - 1).prepareFragmentToSlide(true, false);
                ArrayList<BaseFragment> arrayList2 = this.fragmentsStack;
                BaseFragment lastFragment = arrayList2.get(arrayList2.size() - 2);
                lastFragment.prepareFragmentToSlide(false, false);
                lastFragment.onPause();
                if (!(lastFragment.fragmentView == null || (parent2 = (ViewGroup) lastFragment.fragmentView.getParent()) == null)) {
                    lastFragment.onRemoveFromParent();
                    parent2.removeViewInLayout(lastFragment.fragmentView);
                }
                if (!(lastFragment.actionBar == null || !lastFragment.actionBar.shouldAddToContainer() || (parent = (ViewGroup) lastFragment.actionBar.getParent()) == null)) {
                    parent.removeViewInLayout(lastFragment.actionBar);
                }
            }
            this.layoutToIgnore = null;
        } else if (this.fragmentsStack.size() >= 2) {
            ArrayList<BaseFragment> arrayList3 = this.fragmentsStack;
            BaseFragment lastFragment2 = arrayList3.get(arrayList3.size() - 1);
            lastFragment2.prepareFragmentToSlide(true, false);
            lastFragment2.onPause();
            lastFragment2.onFragmentDestroy();
            lastFragment2.setParentLayout((ActionBarLayout) null);
            ArrayList<BaseFragment> arrayList4 = this.fragmentsStack;
            arrayList4.remove(arrayList4.size() - 1);
            onFragmentStackChanged();
            LayoutContainer temp = this.containerView;
            LayoutContainer layoutContainer = this.containerViewBack;
            this.containerView = layoutContainer;
            this.containerViewBack = temp;
            bringChildToFront(layoutContainer);
            ArrayList<BaseFragment> arrayList5 = this.fragmentsStack;
            BaseFragment lastFragment3 = arrayList5.get(arrayList5.size() - 1);
            this.currentActionBar = lastFragment3.actionBar;
            lastFragment3.onResume();
            lastFragment3.onBecomeFullyVisible();
            lastFragment3.prepareFragmentToSlide(false, false);
            this.layoutToIgnore = this.containerView;
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

    private void prepareForMoving(MotionEvent ev) {
        this.maybeStartTracking = false;
        this.startedTracking = true;
        this.layoutToIgnore = this.containerViewBack;
        this.startedTrackingX = (int) ev.getX();
        this.containerViewBack.setVisibility(0);
        this.beginTrackingSent = false;
        ArrayList<BaseFragment> arrayList = this.fragmentsStack;
        BaseFragment lastFragment = arrayList.get(arrayList.size() - 2);
        View fragmentView = lastFragment.fragmentView;
        if (fragmentView == null) {
            fragmentView = lastFragment.createView(this.parentActivity);
        }
        ViewGroup parent = (ViewGroup) fragmentView.getParent();
        if (parent != null) {
            lastFragment.onRemoveFromParent();
            parent.removeView(fragmentView);
        }
        this.containerViewBack.addView(fragmentView);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fragmentView.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        layoutParams.leftMargin = 0;
        layoutParams.rightMargin = 0;
        layoutParams.bottomMargin = 0;
        layoutParams.topMargin = 0;
        fragmentView.setLayoutParams(layoutParams);
        if (lastFragment.actionBar != null && lastFragment.actionBar.shouldAddToContainer()) {
            ViewGroup parent2 = (ViewGroup) lastFragment.actionBar.getParent();
            if (parent2 != null) {
                parent2.removeView(lastFragment.actionBar);
            }
            if (this.removeActionBarExtraHeight) {
                lastFragment.actionBar.setOccupyStatusBar(false);
            }
            this.containerViewBack.addView(lastFragment.actionBar);
            lastFragment.actionBar.setTitleOverlayText(this.titleOverlayText, this.titleOverlayTextId, this.overlayAction);
        }
        if (!lastFragment.hasOwnBackground && fragmentView.getBackground() == null) {
            fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        }
        lastFragment.onResume();
        if (this.themeAnimatorSet != null) {
            this.presentingFragmentDescriptions = lastFragment.getThemeDescriptions();
        }
        ArrayList<BaseFragment> arrayList2 = this.fragmentsStack;
        arrayList2.get(arrayList2.size() - 1).prepareFragmentToSlide(true, true);
        lastFragment.prepareFragmentToSlide(false, true);
    }

    public boolean onTouchEvent(MotionEvent ev) {
        VelocityTracker velocityTracker2;
        float distToMove;
        Animator customTransition;
        float distToMove2;
        MotionEvent motionEvent = ev;
        if (checkTransitionAnimation() || this.inActionMode || this.animationInProgress) {
            return false;
        }
        if (this.fragmentsStack.size() > 1) {
            if (motionEvent != null && ev.getAction() == 0) {
                ArrayList<BaseFragment> arrayList = this.fragmentsStack;
                if (!arrayList.get(arrayList.size() - 1).isSwipeBackEnabled(motionEvent)) {
                    this.maybeStartTracking = false;
                    this.startedTracking = false;
                    return false;
                }
                this.startedTrackingPointerId = motionEvent.getPointerId(0);
                this.maybeStartTracking = true;
                this.startedTrackingX = (int) ev.getX();
                this.startedTrackingY = (int) ev.getY();
                VelocityTracker velocityTracker3 = this.velocityTracker;
                if (velocityTracker3 != null) {
                    velocityTracker3.clear();
                }
            } else if (motionEvent != null && ev.getAction() == 2 && motionEvent.getPointerId(0) == this.startedTrackingPointerId) {
                if (this.velocityTracker == null) {
                    this.velocityTracker = VelocityTracker.obtain();
                }
                int dx = Math.max(0, (int) (ev.getX() - ((float) this.startedTrackingX)));
                int dy = Math.abs(((int) ev.getY()) - this.startedTrackingY);
                this.velocityTracker.addMovement(motionEvent);
                if (!this.transitionAnimationInProgress && !this.inPreviewMode && this.maybeStartTracking && !this.startedTracking && ((float) dx) >= AndroidUtilities.getPixelsInCM(0.4f, true) && Math.abs(dx) / 3 > dy) {
                    ArrayList<BaseFragment> arrayList2 = this.fragmentsStack;
                    if (!arrayList2.get(arrayList2.size() - 1).canBeginSlide() || findScrollingChild(this, ev.getX(), ev.getY()) != null) {
                        this.maybeStartTracking = false;
                    } else {
                        prepareForMoving(ev);
                    }
                } else if (this.startedTracking) {
                    if (!this.beginTrackingSent) {
                        if (this.parentActivity.getCurrentFocus() != null) {
                            AndroidUtilities.hideKeyboard(this.parentActivity.getCurrentFocus());
                        }
                        ArrayList<BaseFragment> arrayList3 = this.fragmentsStack;
                        arrayList3.get(arrayList3.size() - 1).onBeginSlide();
                        this.beginTrackingSent = true;
                    }
                    this.containerView.setTranslationX((float) dx);
                    setInnerTranslationX((float) dx);
                }
            } else if (motionEvent != null && motionEvent.getPointerId(0) == this.startedTrackingPointerId && (ev.getAction() == 3 || ev.getAction() == 1 || ev.getAction() == 6)) {
                if (this.velocityTracker == null) {
                    this.velocityTracker = VelocityTracker.obtain();
                }
                this.velocityTracker.computeCurrentVelocity(1000);
                ArrayList<BaseFragment> arrayList4 = this.fragmentsStack;
                BaseFragment currentFragment = arrayList4.get(arrayList4.size() - 1);
                if (!this.inPreviewMode && !this.transitionAnimationPreviewMode && !this.startedTracking && currentFragment.isSwipeBackEnabled(motionEvent)) {
                    float velX = this.velocityTracker.getXVelocity();
                    float velY = this.velocityTracker.getYVelocity();
                    if (velX >= 3500.0f && velX > Math.abs(velY) && currentFragment.canBeginSlide()) {
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
                    float x = this.containerView.getX();
                    AnimatorSet animatorSet = new AnimatorSet();
                    float velX2 = this.velocityTracker.getXVelocity();
                    float velY2 = this.velocityTracker.getYVelocity();
                    final boolean backAnimation = x < ((float) this.containerView.getMeasuredWidth()) / 3.0f && (velX2 < 3500.0f || velX2 < velY2);
                    boolean overrideTransition = currentFragment.shouldOverrideSlideTransition(false, backAnimation);
                    if (!backAnimation) {
                        float distToMove3 = ((float) this.containerView.getMeasuredWidth()) - x;
                        int duration = Math.max((int) ((200.0f / ((float) this.containerView.getMeasuredWidth())) * distToMove3), 50);
                        if (!overrideTransition) {
                            distToMove2 = distToMove3;
                            float f = velX2;
                            float f2 = velY2;
                            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.containerView, View.TRANSLATION_X, new float[]{(float) this.containerView.getMeasuredWidth()}).setDuration((long) duration), ObjectAnimator.ofFloat(this, "innerTranslationX", new float[]{(float) this.containerView.getMeasuredWidth()}).setDuration((long) duration)});
                        } else {
                            distToMove2 = distToMove3;
                            float distToMove4 = velX2;
                            float f3 = velY2;
                        }
                        distToMove = distToMove2;
                    } else {
                        float f4 = velY2;
                        distToMove = x;
                        int duration2 = Math.max((int) ((200.0f / ((float) this.containerView.getMeasuredWidth())) * distToMove), 50);
                        if (!overrideTransition) {
                            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.containerView, View.TRANSLATION_X, new float[]{0.0f}).setDuration((long) duration2), ObjectAnimator.ofFloat(this, "innerTranslationX", new float[]{0.0f}).setDuration((long) duration2)});
                        }
                    }
                    Animator customTransition2 = currentFragment.getCustomSlideTransition(false, backAnimation, distToMove);
                    if (customTransition2 != null) {
                        animatorSet.playTogether(new Animator[]{customTransition2});
                    }
                    ArrayList<BaseFragment> arrayList5 = this.fragmentsStack;
                    BaseFragment lastFragment = arrayList5.get(arrayList5.size() - 2);
                    if (!(lastFragment == null || (customTransition = lastFragment.getCustomSlideTransition(false, backAnimation, distToMove)) == null)) {
                        animatorSet.playTogether(new Animator[]{customTransition});
                    }
                    animatorSet.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            ActionBarLayout.this.onSlideAnimationEnd(backAnimation);
                        }
                    });
                    animatorSet.start();
                    this.animationInProgress = true;
                    this.layoutToIgnore = this.containerViewBack;
                    velocityTracker2 = null;
                } else {
                    this.maybeStartTracking = false;
                    this.startedTracking = false;
                    velocityTracker2 = null;
                    this.layoutToIgnore = null;
                }
                VelocityTracker velocityTracker4 = this.velocityTracker;
                if (velocityTracker4 != null) {
                    velocityTracker4.recycle();
                    this.velocityTracker = velocityTracker2;
                }
            } else if (motionEvent == null) {
                this.maybeStartTracking = false;
                this.startedTracking = false;
                this.layoutToIgnore = null;
                VelocityTracker velocityTracker5 = this.velocityTracker;
                if (velocityTracker5 != null) {
                    velocityTracker5.recycle();
                    this.velocityTracker = null;
                }
            }
        }
        return this.startedTracking;
    }

    public void onBackPressed() {
        if (!this.transitionAnimationPreviewMode && !this.startedTracking && !checkTransitionAnimation() && !this.fragmentsStack.isEmpty() && !GroupCallPip.onBackPressed()) {
            ActionBar actionBar = this.currentActionBar;
            if (actionBar == null || actionBar.isActionModeShowed() || !this.currentActionBar.isSearchFieldVisible) {
                ArrayList<BaseFragment> arrayList = this.fragmentsStack;
                if (arrayList.get(arrayList.size() - 1).onBackPressed() && !this.fragmentsStack.isEmpty()) {
                    closeLastFragment(true);
                    return;
                }
                return;
            }
            this.currentActionBar.closeSearchField();
        }
    }

    public void onLowMemory() {
        Iterator<BaseFragment> it = this.fragmentsStack.iterator();
        while (it.hasNext()) {
            it.next().onLowMemory();
        }
    }

    /* access modifiers changed from: private */
    public void onAnimationEndCheck(boolean byCheck) {
        onCloseAnimationEnd();
        onOpenAnimationEnd();
        Runnable runnable = this.waitingForKeyboardCloseRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.waitingForKeyboardCloseRunnable = null;
        }
        AnimatorSet animatorSet = this.currentAnimation;
        if (animatorSet != null) {
            if (byCheck) {
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
        ArrayList<BaseFragment> arrayList = this.fragmentsStack;
        return arrayList.get(arrayList.size() - 1);
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

    public boolean isPreviewOpenAnimationInProgress() {
        return this.previewOpenAnimationInProgress;
    }

    public boolean isTransitionAnimationInProgress() {
        return this.transitionAnimationInProgress || this.animationInProgress;
    }

    private void presentFragmentInternalRemoveOld(boolean removeLast, BaseFragment fragment) {
        ViewGroup parent;
        ViewGroup parent2;
        if (fragment != null) {
            fragment.onBecomeFullyHidden();
            fragment.onPause();
            if (removeLast) {
                fragment.onFragmentDestroy();
                fragment.setParentLayout((ActionBarLayout) null);
                this.fragmentsStack.remove(fragment);
                onFragmentStackChanged();
            } else {
                if (!(fragment.fragmentView == null || (parent2 = (ViewGroup) fragment.fragmentView.getParent()) == null)) {
                    fragment.onRemoveFromParent();
                    try {
                        parent2.removeViewInLayout(fragment.fragmentView);
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                        try {
                            parent2.removeView(fragment.fragmentView);
                        } catch (Exception e2) {
                            FileLog.e((Throwable) e2);
                        }
                    }
                }
                if (!(fragment.actionBar == null || !fragment.actionBar.shouldAddToContainer() || (parent = (ViewGroup) fragment.actionBar.getParent()) == null)) {
                    parent.removeViewInLayout(fragment.actionBar);
                }
            }
            this.containerViewBack.setVisibility(4);
        }
    }

    public boolean presentFragmentAsPreview(BaseFragment fragment) {
        return presentFragment(fragment, false, false, true, true, (View) null);
    }

    public boolean presentFragmentAsPreviewWithMenu(BaseFragment fragment, View menu) {
        return presentFragment(fragment, false, false, true, true, menu);
    }

    public boolean presentFragment(BaseFragment fragment) {
        return presentFragment(fragment, false, false, true, false, (View) null);
    }

    public boolean presentFragment(BaseFragment fragment, boolean removeLast) {
        return presentFragment(fragment, removeLast, false, true, false, (View) null);
    }

    /* access modifiers changed from: private */
    public void startLayoutAnimation(final boolean open, final boolean first, final boolean preview) {
        if (first) {
            this.animationProgress = 0.0f;
            this.lastFrameTime = System.nanoTime() / 1000000;
        }
        AnonymousClass2 r0 = new Runnable() {
            public void run() {
                if (ActionBarLayout.this.animationRunnable == this) {
                    Integer newNavigationBarColor = null;
                    Runnable unused = ActionBarLayout.this.animationRunnable = null;
                    if (first) {
                        long unused2 = ActionBarLayout.this.transitionAnimationStartTime = System.currentTimeMillis();
                    }
                    long newTime = System.nanoTime() / 1000000;
                    long dt = newTime - ActionBarLayout.this.lastFrameTime;
                    if (dt > 18) {
                        dt = 18;
                    }
                    long unused3 = ActionBarLayout.this.lastFrameTime = newTime;
                    ActionBarLayout.access$1116(ActionBarLayout.this, ((float) dt) / 150.0f);
                    if (ActionBarLayout.this.animationProgress > 1.0f) {
                        float unused4 = ActionBarLayout.this.animationProgress = 1.0f;
                    }
                    if (ActionBarLayout.this.newFragment != null) {
                        ActionBarLayout.this.newFragment.onTransitionAnimationProgress(true, ActionBarLayout.this.animationProgress);
                    }
                    if (ActionBarLayout.this.oldFragment != null) {
                        ActionBarLayout.this.oldFragment.onTransitionAnimationProgress(false, ActionBarLayout.this.animationProgress);
                    }
                    Integer oldNavigationBarColor = ActionBarLayout.this.oldFragment != null ? Integer.valueOf(ActionBarLayout.this.oldFragment.getNavigationBarColor()) : null;
                    if (ActionBarLayout.this.newFragment != null) {
                        newNavigationBarColor = Integer.valueOf(ActionBarLayout.this.newFragment.getNavigationBarColor());
                    }
                    if (!(ActionBarLayout.this.newFragment == null || ActionBarLayout.this.newFragment.inPreviewMode || oldNavigationBarColor == null)) {
                        ActionBarLayout.this.newFragment.setNavigationBarColor(ColorUtils.blendARGB(oldNavigationBarColor.intValue(), newNavigationBarColor.intValue(), MathUtils.clamp((ActionBarLayout.this.animationProgress * 2.0f) - (open ? 1.0f : 0.0f), 0.0f, 1.0f)));
                    }
                    float interpolated = ActionBarLayout.this.decelerateInterpolator.getInterpolation(ActionBarLayout.this.animationProgress);
                    if (open) {
                        ActionBarLayout.this.containerView.setAlpha(interpolated);
                        if (preview) {
                            ActionBarLayout.this.containerView.setScaleX((interpolated * 0.1f) + 0.9f);
                            ActionBarLayout.this.containerView.setScaleY((0.1f * interpolated) + 0.9f);
                            ActionBarLayout.this.previewBackgroundDrawable.setAlpha((int) (46.0f * interpolated));
                            Theme.moveUpDrawable.setAlpha((int) (255.0f * interpolated));
                            ActionBarLayout.this.containerView.invalidate();
                            ActionBarLayout.this.invalidate();
                        } else {
                            ActionBarLayout.this.containerView.setTranslationX(((float) AndroidUtilities.dp(48.0f)) * (1.0f - interpolated));
                        }
                    } else {
                        ActionBarLayout.this.containerViewBack.setAlpha(1.0f - interpolated);
                        if (preview) {
                            ActionBarLayout.this.containerViewBack.setScaleX(((1.0f - interpolated) * 0.1f) + 0.9f);
                            ActionBarLayout.this.containerViewBack.setScaleY(((1.0f - interpolated) * 0.1f) + 0.9f);
                            ActionBarLayout.this.previewBackgroundDrawable.setAlpha((int) ((1.0f - interpolated) * 46.0f));
                            Theme.moveUpDrawable.setAlpha((int) ((1.0f - interpolated) * 255.0f));
                            ActionBarLayout.this.containerView.invalidate();
                            ActionBarLayout.this.invalidate();
                        } else {
                            ActionBarLayout.this.containerViewBack.setTranslationX(((float) AndroidUtilities.dp(48.0f)) * interpolated);
                        }
                    }
                    if (ActionBarLayout.this.animationProgress < 1.0f) {
                        ActionBarLayout.this.startLayoutAnimation(open, false, preview);
                    } else {
                        ActionBarLayout.this.onAnimationEndCheck(false);
                    }
                }
            }
        };
        this.animationRunnable = r0;
        AndroidUtilities.runOnUIThread(r0);
    }

    public void resumeDelayedFragmentAnimation() {
        this.delayedAnimationResumed = true;
        Runnable runnable = this.delayedOpenAnimationRunnable;
        if (runnable != null && this.waitingForKeyboardCloseRunnable == null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.delayedOpenAnimationRunnable.run();
            this.delayedOpenAnimationRunnable = null;
        }
    }

    public boolean isInPreviewMode() {
        return this.inPreviewMode || this.transitionAnimationPreviewMode;
    }

    public boolean isInPassivePreviewMode() {
        return (this.inPreviewMode && this.previewMenu == null) || this.transitionAnimationPreviewMode;
    }

    public boolean isInPreviewMenuMode() {
        return isInPreviewMode() && this.previewMenu != null;
    }

    public boolean presentFragment(BaseFragment fragment, boolean removeLast, boolean forceWithoutAnimation, boolean check, boolean preview) {
        return presentFragment(fragment, removeLast, forceWithoutAnimation, check, preview, (View) null);
    }

    public boolean presentFragment(BaseFragment fragment, boolean removeLast, boolean forceWithoutAnimation, boolean check, boolean preview, View menu) {
        BaseFragment baseFragment;
        View fragmentView;
        int menuHeight;
        boolean z;
        AnimatorSet animation;
        long j;
        boolean z2;
        boolean z3;
        final BaseFragment baseFragment2 = fragment;
        boolean z4 = removeLast;
        boolean z5 = forceWithoutAnimation;
        final boolean z6 = preview;
        View view = menu;
        if (baseFragment2 == null || checkTransitionAnimation()) {
            return false;
        }
        ActionBarLayoutDelegate actionBarLayoutDelegate = this.delegate;
        if ((actionBarLayoutDelegate != null && check && !actionBarLayoutDelegate.needPresentFragment(baseFragment2, z4, z5, this)) || !fragment.onFragmentCreate()) {
            return false;
        }
        baseFragment2.setInPreviewMode(z6);
        baseFragment2.setInMenuMode(view != null);
        if (this.parentActivity.getCurrentFocus() != null && fragment.hideKeyboardOnShow() && !z6) {
            AndroidUtilities.hideKeyboard(this.parentActivity.getCurrentFocus());
        }
        boolean needAnimation = z6 || (!z5 && MessagesController.getGlobalMainSettings().getBoolean("view_animations", true));
        if (!this.fragmentsStack.isEmpty()) {
            ArrayList<BaseFragment> arrayList = this.fragmentsStack;
            baseFragment = arrayList.get(arrayList.size() - 1);
        } else {
            baseFragment = null;
        }
        BaseFragment currentFragment = baseFragment;
        baseFragment2.setParentLayout(this);
        View fragmentView2 = baseFragment2.fragmentView;
        if (fragmentView2 == null) {
            fragmentView = baseFragment2.createView(this.parentActivity);
        } else {
            ViewGroup parent = (ViewGroup) fragmentView2.getParent();
            if (parent != null) {
                fragment.onRemoveFromParent();
                parent.removeView(fragmentView2);
            }
            fragmentView = fragmentView2;
        }
        View wrappedView = fragmentView;
        this.containerViewBack.addView(wrappedView);
        if (view != null) {
            this.containerViewBack.addView(view);
            view.measure(View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), Integer.MIN_VALUE));
            int menuHeight2 = menu.getMeasuredHeight() + AndroidUtilities.dp(24.0f);
            FrameLayout.LayoutParams menuParams = (FrameLayout.LayoutParams) menu.getLayoutParams();
            menuParams.width = -2;
            menuParams.height = -2;
            menuParams.topMargin = (getMeasuredHeight() - menuHeight2) - AndroidUtilities.dp(6.0f);
            view.setLayoutParams(menuParams);
            menuHeight = menuHeight2;
        } else {
            menuHeight = 0;
        }
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) wrappedView.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        if (z6) {
            int height = fragment.getPreviewHeight();
            int statusBarHeight = Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0;
            if (height <= 0 || height >= getMeasuredHeight() - statusBarHeight) {
                int dp = AndroidUtilities.dp(view != null ? 0.0f : 46.0f);
                layoutParams.bottomMargin = dp;
                layoutParams.topMargin = dp;
                layoutParams.topMargin += AndroidUtilities.statusBarHeight;
            } else {
                layoutParams.height = height;
                layoutParams.topMargin = (((getMeasuredHeight() - statusBarHeight) - height) / 2) + statusBarHeight;
            }
            if (view != null) {
                layoutParams.bottomMargin += menuHeight + AndroidUtilities.dp(8.0f);
            }
            int dp2 = AndroidUtilities.dp(8.0f);
            layoutParams.leftMargin = dp2;
            layoutParams.rightMargin = dp2;
        } else {
            layoutParams.leftMargin = 0;
            layoutParams.rightMargin = 0;
            layoutParams.bottomMargin = 0;
            layoutParams.topMargin = 0;
        }
        wrappedView.setLayoutParams(layoutParams);
        if (baseFragment2.actionBar != null && baseFragment2.actionBar.shouldAddToContainer()) {
            if (this.removeActionBarExtraHeight) {
                baseFragment2.actionBar.setOccupyStatusBar(false);
            }
            ViewGroup parent2 = (ViewGroup) baseFragment2.actionBar.getParent();
            if (parent2 != null) {
                parent2.removeView(baseFragment2.actionBar);
            }
            this.containerViewBack.addView(baseFragment2.actionBar);
            baseFragment2.actionBar.setTitleOverlayText(this.titleOverlayText, this.titleOverlayTextId, this.overlayAction);
        }
        this.fragmentsStack.add(baseFragment2);
        onFragmentStackChanged();
        fragment.onResume();
        this.currentActionBar = baseFragment2.actionBar;
        if (!baseFragment2.hasOwnBackground && fragmentView.getBackground() == null) {
            fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        }
        LayoutContainer temp = this.containerView;
        LayoutContainer layoutContainer = this.containerViewBack;
        this.containerView = layoutContainer;
        this.containerViewBack = temp;
        layoutContainer.setVisibility(0);
        setInnerTranslationX(0.0f);
        this.containerView.setTranslationY(0.0f);
        if (z6) {
            if (Build.VERSION.SDK_INT >= 21) {
                fragmentView.setOutlineProvider(new ViewOutlineProvider() {
                    public void getOutline(View view, Outline outline) {
                        outline.setRoundRect(0, AndroidUtilities.statusBarHeight, view.getMeasuredWidth(), view.getMeasuredHeight(), (float) AndroidUtilities.dp(6.0f));
                    }
                });
                fragmentView.setClipToOutline(true);
                fragmentView.setElevation((float) AndroidUtilities.dp(4.0f));
            }
            if (this.previewBackgroundDrawable == null) {
                this.previewBackgroundDrawable = new ColorDrawable(NUM);
            }
            this.previewBackgroundDrawable.setAlpha(0);
            Theme.moveUpDrawable.setAlpha(0);
        }
        bringChildToFront(this.containerView);
        if (!needAnimation) {
            presentFragmentInternalRemoveOld(z4, currentFragment);
            View view2 = this.backgroundView;
            if (view2 != null) {
                view2.setVisibility(0);
            }
        }
        if (this.themeAnimatorSet != null) {
            this.presentingFragmentDescriptions = fragment.getThemeDescriptions();
        }
        if (needAnimation || z6) {
            boolean z7 = needAnimation;
            if (!this.useAlphaAnimations || this.fragmentsStack.size() != 1) {
                this.transitionAnimationPreviewMode = z6;
                this.transitionAnimationStartTime = System.currentTimeMillis();
                this.transitionAnimationInProgress = true;
                this.layoutToIgnore = this.containerView;
                FrameLayout.LayoutParams layoutParams2 = layoutParams;
                View view3 = wrappedView;
                View view4 = fragmentView;
                final BaseFragment currentFragment2 = currentFragment;
                this.onOpenAnimationEndRunnable = new ActionBarLayout$$ExternalSyntheticLambda4(this, preview, menu, removeLast, currentFragment, fragment);
                boolean z8 = !fragment.needDelayOpenAnimation();
                final boolean noDelay = z8;
                if (z8) {
                    if (currentFragment2 != null) {
                        z = false;
                        currentFragment2.onTransitionAnimationStart(false, false);
                    } else {
                        z = false;
                    }
                    baseFragment2.onTransitionAnimationStart(true, z);
                } else {
                    z = false;
                }
                this.delayedAnimationResumed = z;
                this.oldFragment = currentFragment2;
                this.newFragment = baseFragment2;
                if (!z6) {
                    animation = baseFragment2.onCustomTransitionAnimation(true, new ActionBarLayout$$ExternalSyntheticLambda1(this));
                } else {
                    animation = null;
                }
                if (animation == null) {
                    this.containerView.setAlpha(0.0f);
                    if (z6) {
                        this.containerView.setTranslationX(0.0f);
                        this.containerView.setScaleX(0.9f);
                        this.containerView.setScaleY(0.9f);
                    } else {
                        this.containerView.setTranslationX(48.0f);
                        this.containerView.setScaleX(1.0f);
                        this.containerView.setScaleY(1.0f);
                    }
                    if (this.containerView.isKeyboardVisible) {
                        j = 200;
                    } else if (this.containerViewBack.isKeyboardVisible) {
                        j = 200;
                    } else if (fragment.needDelayOpenAnimation()) {
                        AnonymousClass7 r0 = new Runnable() {
                            public void run() {
                                if (ActionBarLayout.this.delayedOpenAnimationRunnable == this) {
                                    Runnable unused = ActionBarLayout.this.delayedOpenAnimationRunnable = null;
                                    baseFragment2.onTransitionAnimationStart(true, false);
                                    ActionBarLayout.this.startLayoutAnimation(true, true, z6);
                                }
                            }
                        };
                        this.delayedOpenAnimationRunnable = r0;
                        AndroidUtilities.runOnUIThread(r0, 200);
                        return true;
                    } else {
                        startLayoutAnimation(true, true, z6);
                        return true;
                    }
                    if (currentFragment2 != null && !z6) {
                        currentFragment2.saveKeyboardPositionBeforeTransition();
                    }
                    final BaseFragment baseFragment3 = currentFragment2;
                    long j2 = j;
                    final BaseFragment baseFragment4 = fragment;
                    final boolean z9 = preview;
                    this.waitingForKeyboardCloseRunnable = new Runnable() {
                        public void run() {
                            if (ActionBarLayout.this.waitingForKeyboardCloseRunnable == this) {
                                Runnable unused = ActionBarLayout.this.waitingForKeyboardCloseRunnable = null;
                                if (noDelay) {
                                    BaseFragment baseFragment = baseFragment3;
                                    if (baseFragment != null) {
                                        baseFragment.onTransitionAnimationStart(false, false);
                                    }
                                    baseFragment4.onTransitionAnimationStart(true, false);
                                    ActionBarLayout.this.startLayoutAnimation(true, true, z9);
                                } else if (ActionBarLayout.this.delayedOpenAnimationRunnable != null) {
                                    AndroidUtilities.cancelRunOnUIThread(ActionBarLayout.this.delayedOpenAnimationRunnable);
                                    if (ActionBarLayout.this.delayedAnimationResumed) {
                                        ActionBarLayout.this.delayedOpenAnimationRunnable.run();
                                    } else {
                                        AndroidUtilities.runOnUIThread(ActionBarLayout.this.delayedOpenAnimationRunnable, 200);
                                    }
                                }
                            }
                        }
                    };
                    if (fragment.needDelayOpenAnimation()) {
                        this.delayedOpenAnimationRunnable = new Runnable() {
                            public void run() {
                                if (ActionBarLayout.this.delayedOpenAnimationRunnable == this) {
                                    Runnable unused = ActionBarLayout.this.delayedOpenAnimationRunnable = null;
                                    BaseFragment baseFragment = currentFragment2;
                                    if (baseFragment != null) {
                                        baseFragment.onTransitionAnimationStart(false, false);
                                    }
                                    baseFragment2.onTransitionAnimationStart(true, false);
                                    ActionBarLayout.this.startLayoutAnimation(true, true, z6);
                                }
                            }
                        };
                    }
                    AndroidUtilities.runOnUIThread(this.waitingForKeyboardCloseRunnable, SharedConfig.smoothKeyboard ? 250 : j2);
                    return true;
                }
                if (!z6 && ((this.containerView.isKeyboardVisible || this.containerViewBack.isKeyboardVisible) && currentFragment2 != null)) {
                    currentFragment2.saveKeyboardPositionBeforeTransition();
                }
                this.currentAnimation = animation;
                return true;
            }
            presentFragmentInternalRemoveOld(z4, currentFragment);
            this.transitionAnimationStartTime = System.currentTimeMillis();
            this.transitionAnimationInProgress = true;
            this.layoutToIgnore = this.containerView;
            this.onOpenAnimationEndRunnable = new ActionBarLayout$$ExternalSyntheticLambda5(currentFragment, baseFragment2);
            ArrayList<Animator> animators = new ArrayList<>();
            animators.add(ObjectAnimator.ofFloat(this, View.ALPHA, new float[]{0.0f, 1.0f}));
            View view5 = this.backgroundView;
            if (view5 != null) {
                view5.setVisibility(0);
                animators.add(ObjectAnimator.ofFloat(this.backgroundView, View.ALPHA, new float[]{0.0f, 1.0f}));
            }
            if (currentFragment != null) {
                z2 = false;
                currentFragment.onTransitionAnimationStart(false, false);
            } else {
                z2 = false;
            }
            baseFragment2.onTransitionAnimationStart(true, z2);
            AnimatorSet animatorSet = new AnimatorSet();
            this.currentAnimation = animatorSet;
            animatorSet.playTogether(animators);
            this.currentAnimation.setInterpolator(this.accelerateDecelerateInterpolator);
            this.currentAnimation.setDuration(200);
            this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    ActionBarLayout.this.onAnimationEndCheck(false);
                }
            });
            this.currentAnimation.start();
            FrameLayout.LayoutParams layoutParams3 = layoutParams;
            View view6 = wrappedView;
            View view7 = fragmentView;
            BaseFragment baseFragment5 = currentFragment;
            return true;
        }
        View view8 = this.backgroundView;
        if (view8 != null) {
            view8.setAlpha(1.0f);
            z3 = false;
            this.backgroundView.setVisibility(0);
        } else {
            z3 = false;
        }
        if (currentFragment != null) {
            currentFragment.onTransitionAnimationStart(z3, z3);
            currentFragment.onTransitionAnimationEnd(z3, z3);
        }
        baseFragment2.onTransitionAnimationStart(true, z3);
        baseFragment2.onTransitionAnimationEnd(true, z3);
        fragment.onBecomeFullyVisible();
        FrameLayout.LayoutParams layoutParams4 = layoutParams;
        View view9 = wrappedView;
        View view10 = fragmentView;
        boolean z10 = needAnimation;
        BaseFragment baseFragment6 = currentFragment;
        return true;
    }

    static /* synthetic */ void lambda$presentFragment$0(BaseFragment currentFragment, BaseFragment fragment) {
        if (currentFragment != null) {
            currentFragment.onTransitionAnimationEnd(false, false);
        }
        fragment.onTransitionAnimationEnd(true, false);
        fragment.onBecomeFullyVisible();
    }

    /* renamed from: lambda$presentFragment$1$org-telegram-ui-ActionBar-ActionBarLayout  reason: not valid java name */
    public /* synthetic */ void m1245xd7da5798(boolean preview, View menu, boolean removeLast, BaseFragment currentFragment, BaseFragment fragment) {
        if (preview) {
            this.inPreviewMode = true;
            this.previewMenu = menu;
            this.transitionAnimationPreviewMode = false;
            this.containerView.setScaleX(1.0f);
            this.containerView.setScaleY(1.0f);
        } else {
            presentFragmentInternalRemoveOld(removeLast, currentFragment);
            this.containerView.setTranslationX(0.0f);
        }
        if (currentFragment != null) {
            currentFragment.onTransitionAnimationEnd(false, false);
        }
        fragment.onTransitionAnimationEnd(true, false);
        fragment.onBecomeFullyVisible();
    }

    /* renamed from: lambda$presentFragment$2$org-telegram-ui-ActionBar-ActionBarLayout  reason: not valid java name */
    public /* synthetic */ void m1246xbd1bCLASSNAME() {
        onAnimationEndCheck(false);
    }

    public void setFragmentStackChangedListener(Runnable onFragmentStackChanged) {
        this.onFragmentStackChangedListener = onFragmentStackChanged;
    }

    private void onFragmentStackChanged() {
        Runnable runnable = this.onFragmentStackChangedListener;
        if (runnable != null) {
            runnable.run();
        }
    }

    public boolean addFragmentToStack(BaseFragment fragment) {
        return addFragmentToStack(fragment, -1);
    }

    public boolean addFragmentToStack(BaseFragment fragment, int position) {
        ViewGroup parent;
        ViewGroup parent2;
        ActionBarLayoutDelegate actionBarLayoutDelegate = this.delegate;
        if ((actionBarLayoutDelegate != null && !actionBarLayoutDelegate.needAddFragmentToStack(fragment, this)) || !fragment.onFragmentCreate()) {
            return false;
        }
        fragment.setParentLayout(this);
        if (position == -1) {
            if (!this.fragmentsStack.isEmpty()) {
                ArrayList<BaseFragment> arrayList = this.fragmentsStack;
                BaseFragment previousFragment = arrayList.get(arrayList.size() - 1);
                previousFragment.onPause();
                if (!(previousFragment.actionBar == null || !previousFragment.actionBar.shouldAddToContainer() || (parent2 = (ViewGroup) previousFragment.actionBar.getParent()) == null)) {
                    parent2.removeView(previousFragment.actionBar);
                }
                if (!(previousFragment.fragmentView == null || (parent = (ViewGroup) previousFragment.fragmentView.getParent()) == null)) {
                    previousFragment.onRemoveFromParent();
                    parent.removeView(previousFragment.fragmentView);
                }
            }
            this.fragmentsStack.add(fragment);
            onFragmentStackChanged();
        } else {
            this.fragmentsStack.add(position, fragment);
            onFragmentStackChanged();
        }
        return true;
    }

    private void closeLastFragmentInternalRemoveOld(BaseFragment fragment) {
        fragment.finishing = true;
        fragment.onPause();
        fragment.onFragmentDestroy();
        fragment.setParentLayout((ActionBarLayout) null);
        this.fragmentsStack.remove(fragment);
        this.containerViewBack.setVisibility(4);
        this.containerViewBack.setTranslationY(0.0f);
        bringChildToFront(this.containerView);
        onFragmentStackChanged();
    }

    public void movePreviewFragment(float dy) {
        if (!this.inPreviewMode || this.transitionAnimationPreviewMode) {
            float f = dy;
        } else if (this.previewMenu != null) {
            float f2 = dy;
        } else {
            float currentTranslation = this.containerView.getTranslationY();
            float nextTranslation = -dy;
            if (nextTranslation > 0.0f) {
                nextTranslation = 0.0f;
            } else if (nextTranslation < ((float) (-AndroidUtilities.dp(60.0f)))) {
                this.previewOpenAnimationInProgress = true;
                this.inPreviewMode = false;
                nextTranslation = 0.0f;
                ArrayList<BaseFragment> arrayList = this.fragmentsStack;
                BaseFragment prevFragment = arrayList.get(arrayList.size() - 2);
                ArrayList<BaseFragment> arrayList2 = this.fragmentsStack;
                final BaseFragment fragment = arrayList2.get(arrayList2.size() - 1);
                if (Build.VERSION.SDK_INT >= 21) {
                    fragment.fragmentView.setOutlineProvider((ViewOutlineProvider) null);
                    fragment.fragmentView.setClipToOutline(false);
                }
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fragment.fragmentView.getLayoutParams();
                layoutParams.leftMargin = 0;
                layoutParams.rightMargin = 0;
                layoutParams.bottomMargin = 0;
                layoutParams.topMargin = 0;
                layoutParams.height = -1;
                fragment.fragmentView.setLayoutParams(layoutParams);
                presentFragmentInternalRemoveOld(false, prevFragment);
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(fragment.fragmentView, View.SCALE_X, new float[]{1.0f, 1.05f, 1.0f}), ObjectAnimator.ofFloat(fragment.fragmentView, View.SCALE_Y, new float[]{1.0f, 1.05f, 1.0f})});
                animatorSet.setDuration(200);
                animatorSet.setInterpolator(new CubicBezierInterpolator(0.42d, 0.0d, 0.58d, 1.0d));
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        boolean unused = ActionBarLayout.this.previewOpenAnimationInProgress = false;
                        fragment.onPreviewOpenAnimationEnd();
                    }
                });
                animatorSet.start();
                performHapticFeedback(3);
                fragment.setInPreviewMode(false);
            }
            if (currentTranslation != nextTranslation) {
                this.containerView.setTranslationY(nextTranslation);
                invalidate();
            }
        }
    }

    public void finishPreviewFragment() {
        if (this.inPreviewMode || this.transitionAnimationPreviewMode) {
            Runnable runnable = this.delayedOpenAnimationRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.delayedOpenAnimationRunnable = null;
            }
            closeLastFragment(true);
        }
    }

    public void closeLastFragment(boolean animated) {
        BaseFragment previousFragment;
        View fragmentView;
        ActionBarLayoutDelegate actionBarLayoutDelegate = this.delegate;
        if ((actionBarLayoutDelegate == null || actionBarLayoutDelegate.needCloseLastFragment(this)) && !checkTransitionAnimation() && !this.fragmentsStack.isEmpty()) {
            if (this.parentActivity.getCurrentFocus() != null) {
                AndroidUtilities.hideKeyboard(this.parentActivity.getCurrentFocus());
            }
            setInnerTranslationX(0.0f);
            boolean needAnimation = this.inPreviewMode || this.transitionAnimationPreviewMode || (animated && MessagesController.getGlobalMainSettings().getBoolean("view_animations", true));
            ArrayList<BaseFragment> arrayList = this.fragmentsStack;
            BaseFragment currentFragment = arrayList.get(arrayList.size() - 1);
            if (this.fragmentsStack.size() > 1) {
                ArrayList<BaseFragment> arrayList2 = this.fragmentsStack;
                previousFragment = arrayList2.get(arrayList2.size() - 2);
            } else {
                previousFragment = null;
            }
            if (previousFragment != null) {
                AndroidUtilities.setLightStatusBar(this.parentActivity.getWindow(), Theme.getColor("actionBarDefault") == -1 || (previousFragment.hasForceLightStatusBar() && !Theme.getCurrentTheme().isDark()), previousFragment.hasForceLightStatusBar());
                LayoutContainer temp = this.containerView;
                this.containerView = this.containerViewBack;
                this.containerViewBack = temp;
                previousFragment.setParentLayout(this);
                View fragmentView2 = previousFragment.fragmentView;
                if (fragmentView2 == null) {
                    fragmentView = previousFragment.createView(this.parentActivity);
                } else {
                    fragmentView = fragmentView2;
                }
                if (!this.inPreviewMode) {
                    this.containerView.setVisibility(0);
                    ViewGroup parent = (ViewGroup) fragmentView.getParent();
                    if (parent != null) {
                        previousFragment.onRemoveFromParent();
                        try {
                            parent.removeView(fragmentView);
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                    }
                    this.containerView.addView(fragmentView);
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fragmentView.getLayoutParams();
                    layoutParams.width = -1;
                    layoutParams.height = -1;
                    layoutParams.leftMargin = 0;
                    layoutParams.rightMargin = 0;
                    layoutParams.bottomMargin = 0;
                    layoutParams.topMargin = 0;
                    fragmentView.setLayoutParams(layoutParams);
                    if (previousFragment.actionBar != null && previousFragment.actionBar.shouldAddToContainer()) {
                        if (this.removeActionBarExtraHeight) {
                            previousFragment.actionBar.setOccupyStatusBar(false);
                        }
                        ViewGroup parent2 = (ViewGroup) previousFragment.actionBar.getParent();
                        if (parent2 != null) {
                            parent2.removeView(previousFragment.actionBar);
                        }
                        this.containerView.addView(previousFragment.actionBar);
                        previousFragment.actionBar.setTitleOverlayText(this.titleOverlayText, this.titleOverlayTextId, this.overlayAction);
                    }
                }
                this.newFragment = previousFragment;
                this.oldFragment = currentFragment;
                previousFragment.onTransitionAnimationStart(true, true);
                currentFragment.onTransitionAnimationStart(false, true);
                previousFragment.onResume();
                if (this.themeAnimatorSet != null) {
                    this.presentingFragmentDescriptions = previousFragment.getThemeDescriptions();
                }
                this.currentActionBar = previousFragment.actionBar;
                if (!previousFragment.hasOwnBackground && fragmentView.getBackground() == null) {
                    fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                }
                if (!needAnimation) {
                    closeLastFragmentInternalRemoveOld(currentFragment);
                }
                if (needAnimation) {
                    this.transitionAnimationStartTime = System.currentTimeMillis();
                    this.transitionAnimationInProgress = true;
                    this.layoutToIgnore = this.containerView;
                    currentFragment.setRemovingFromStack(true);
                    this.onCloseAnimationEndRunnable = new ActionBarLayout$$ExternalSyntheticLambda3(this, currentFragment, previousFragment);
                    AnimatorSet animation = null;
                    if (!this.inPreviewMode && !this.transitionAnimationPreviewMode) {
                        animation = currentFragment.onCustomTransitionAnimation(false, new ActionBarLayout$$ExternalSyntheticLambda0(this));
                    }
                    if (animation != null) {
                        this.currentAnimation = animation;
                        if (Bulletin.getVisibleBulletin() != null && Bulletin.getVisibleBulletin().isShowing()) {
                            Bulletin.getVisibleBulletin().hide();
                        }
                    } else if (this.inPreviewMode || (!this.containerView.isKeyboardVisible && !this.containerViewBack.isKeyboardVisible)) {
                        startLayoutAnimation(false, true, this.inPreviewMode || this.transitionAnimationPreviewMode);
                    } else {
                        AnonymousClass9 r2 = new Runnable() {
                            public void run() {
                                if (ActionBarLayout.this.waitingForKeyboardCloseRunnable == this) {
                                    Runnable unused = ActionBarLayout.this.waitingForKeyboardCloseRunnable = null;
                                    ActionBarLayout.this.startLayoutAnimation(false, true, false);
                                }
                            }
                        };
                        this.waitingForKeyboardCloseRunnable = r2;
                        AndroidUtilities.runOnUIThread(r2, 200);
                    }
                    onFragmentStackChanged();
                    return;
                }
                currentFragment.onTransitionAnimationEnd(false, true);
                previousFragment.onTransitionAnimationEnd(true, true);
                previousFragment.onBecomeFullyVisible();
            } else if (this.useAlphaAnimations) {
                this.transitionAnimationStartTime = System.currentTimeMillis();
                this.transitionAnimationInProgress = true;
                this.layoutToIgnore = this.containerView;
                this.onCloseAnimationEndRunnable = new ActionBarLayout$$ExternalSyntheticLambda2(this, currentFragment);
                ArrayList<Animator> animators = new ArrayList<>();
                animators.add(ObjectAnimator.ofFloat(this, View.ALPHA, new float[]{1.0f, 0.0f}));
                View view = this.backgroundView;
                if (view != null) {
                    animators.add(ObjectAnimator.ofFloat(view, View.ALPHA, new float[]{1.0f, 0.0f}));
                }
                AnimatorSet animatorSet = new AnimatorSet();
                this.currentAnimation = animatorSet;
                animatorSet.playTogether(animators);
                this.currentAnimation.setInterpolator(this.accelerateDecelerateInterpolator);
                this.currentAnimation.setDuration(200);
                this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationStart(Animator animation) {
                        long unused = ActionBarLayout.this.transitionAnimationStartTime = System.currentTimeMillis();
                    }

                    public void onAnimationEnd(Animator animation) {
                        ActionBarLayout.this.onAnimationEndCheck(false);
                    }
                });
                this.currentAnimation.start();
            } else {
                removeFragmentFromStackInternal(currentFragment);
                setVisibility(8);
                View view2 = this.backgroundView;
                if (view2 != null) {
                    view2.setVisibility(8);
                }
            }
        }
    }

    /* renamed from: lambda$closeLastFragment$3$org-telegram-ui-ActionBar-ActionBarLayout  reason: not valid java name */
    public /* synthetic */ void m1242x789344cd(BaseFragment currentFragment, BaseFragment previousFragmentFinal) {
        ViewGroup parent;
        View view = this.previewMenu;
        if (!(view == null || (parent = (ViewGroup) view.getParent()) == null)) {
            parent.removeView(this.previewMenu);
        }
        if (this.inPreviewMode || this.transitionAnimationPreviewMode) {
            this.containerViewBack.setScaleX(1.0f);
            this.containerViewBack.setScaleY(1.0f);
            this.inPreviewMode = false;
            this.previewMenu = null;
            this.transitionAnimationPreviewMode = false;
        } else {
            this.containerViewBack.setTranslationX(0.0f);
        }
        closeLastFragmentInternalRemoveOld(currentFragment);
        currentFragment.setRemovingFromStack(false);
        currentFragment.onTransitionAnimationEnd(false, true);
        previousFragmentFinal.onTransitionAnimationEnd(true, true);
        previousFragmentFinal.onBecomeFullyVisible();
    }

    /* renamed from: lambda$closeLastFragment$4$org-telegram-ui-ActionBar-ActionBarLayout  reason: not valid java name */
    public /* synthetic */ void m1243x5dd4b38e() {
        onAnimationEndCheck(false);
    }

    /* renamed from: lambda$closeLastFragment$5$org-telegram-ui-ActionBar-ActionBarLayout  reason: not valid java name */
    public /* synthetic */ void m1244x4316224f(BaseFragment currentFragment) {
        removeFragmentFromStackInternal(currentFragment);
        setVisibility(8);
        View view = this.backgroundView;
        if (view != null) {
            view.setVisibility(8);
        }
        DrawerLayoutContainer drawerLayoutContainer2 = this.drawerLayoutContainer;
        if (drawerLayoutContainer2 != null) {
            drawerLayoutContainer2.setAllowOpenDrawer(true, false);
        }
    }

    public void showFragment(int i) {
        ViewGroup parent;
        ViewGroup parent2;
        if (!this.fragmentsStack.isEmpty()) {
            for (int a = 0; a < i; a++) {
                BaseFragment previousFragment = this.fragmentsStack.get(a);
                if (!(previousFragment.actionBar == null || !previousFragment.actionBar.shouldAddToContainer() || (parent2 = (ViewGroup) previousFragment.actionBar.getParent()) == null)) {
                    parent2.removeView(previousFragment.actionBar);
                }
                if (!(previousFragment.fragmentView == null || (parent = (ViewGroup) previousFragment.fragmentView.getParent()) == null)) {
                    previousFragment.onPause();
                    previousFragment.onRemoveFromParent();
                    parent.removeView(previousFragment.fragmentView);
                }
            }
            BaseFragment previousFragment2 = this.fragmentsStack.get(i);
            previousFragment2.setParentLayout(this);
            View fragmentView = previousFragment2.fragmentView;
            if (fragmentView == null) {
                fragmentView = previousFragment2.createView(this.parentActivity);
            } else {
                ViewGroup parent3 = (ViewGroup) fragmentView.getParent();
                if (parent3 != null) {
                    previousFragment2.onRemoveFromParent();
                    parent3.removeView(fragmentView);
                }
            }
            this.containerView.addView(fragmentView, LayoutHelper.createFrame(-1, -1.0f));
            if (previousFragment2.actionBar != null && previousFragment2.actionBar.shouldAddToContainer()) {
                if (this.removeActionBarExtraHeight) {
                    previousFragment2.actionBar.setOccupyStatusBar(false);
                }
                ViewGroup parent4 = (ViewGroup) previousFragment2.actionBar.getParent();
                if (parent4 != null) {
                    parent4.removeView(previousFragment2.actionBar);
                }
                this.containerView.addView(previousFragment2.actionBar);
                previousFragment2.actionBar.setTitleOverlayText(this.titleOverlayText, this.titleOverlayTextId, this.overlayAction);
            }
            previousFragment2.onResume();
            this.currentActionBar = previousFragment2.actionBar;
            if (!previousFragment2.hasOwnBackground && fragmentView.getBackground() == null) {
                fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            }
        }
    }

    public void showLastFragment() {
        if (!this.fragmentsStack.isEmpty()) {
            showFragment(this.fragmentsStack.size() - 1);
        }
    }

    private void removeFragmentFromStackInternal(BaseFragment fragment) {
        fragment.onPause();
        fragment.onFragmentDestroy();
        fragment.setParentLayout((ActionBarLayout) null);
        this.fragmentsStack.remove(fragment);
        onFragmentStackChanged();
    }

    public void removeFragmentFromStack(int num) {
        if (num < this.fragmentsStack.size()) {
            removeFragmentFromStackInternal(this.fragmentsStack.get(num));
        }
    }

    public void removeFragmentFromStack(BaseFragment fragment) {
        if (!this.useAlphaAnimations || this.fragmentsStack.size() != 1 || !AndroidUtilities.isTablet()) {
            if (this.delegate != null && this.fragmentsStack.size() == 1 && AndroidUtilities.isTablet()) {
                this.delegate.needCloseLastFragment(this);
            }
            removeFragmentFromStackInternal(fragment);
            return;
        }
        closeLastFragment(true);
    }

    public void removeAllFragments() {
        for (int a = 0; a < this.fragmentsStack.size(); a = (a - 1) + 1) {
            removeFragmentFromStackInternal(this.fragmentsStack.get(a));
        }
    }

    public void setThemeAnimationValue(float value) {
        float f = value;
        this.themeAnimationValue = f;
        int N = this.themeAnimatorDescriptions.size();
        for (int j = 0; j < N; j++) {
            ArrayList<ThemeDescription> descriptions = this.themeAnimatorDescriptions.get(j);
            int[] startColors = this.animateStartColors.get(j);
            int[] endColors = this.animateEndColors.get(j);
            int i = 0;
            int b = descriptions.size();
            while (i < b) {
                int rE = Color.red(endColors[i]);
                int gE = Color.green(endColors[i]);
                int bE = Color.blue(endColors[i]);
                int aE = Color.alpha(endColors[i]);
                int rS = Color.red(startColors[i]);
                int gS = Color.green(startColors[i]);
                int bS = Color.blue(startColors[i]);
                int N2 = N;
                int aS = Color.alpha(startColors[i]);
                int[] startColors2 = startColors;
                int a = Math.min(255, (int) (((float) aS) + (((float) (aE - aS)) * f)));
                int i2 = aS;
                int r = Math.min(255, (int) (((float) rS) + (((float) (rE - rS)) * f)));
                int i3 = rE;
                int color = Color.argb(a, r, Math.min(255, (int) (((float) gS) + (((float) (gE - gS)) * f))), Math.min(255, (int) (((float) bS) + (((float) (bE - bS)) * f))));
                int i4 = r;
                ThemeDescription description = descriptions.get(i);
                description.setAnimatedColor(color);
                int i5 = bS;
                description.setColor(color, false, false);
                i++;
                startColors = startColors2;
                N = N2;
                endColors = endColors;
                b = b;
            }
            int[] iArr = startColors;
            int[] iArr2 = endColors;
            int i6 = b;
        }
        int N3 = this.themeAnimatorDelegate.size();
        for (int j2 = 0; j2 < N3; j2++) {
            ThemeDescription.ThemeDescriptionDelegate delegate2 = this.themeAnimatorDelegate.get(j2);
            if (delegate2 != null) {
                delegate2.didSetColor();
                delegate2.onAnimationProgress(f);
            }
        }
        ArrayList<ThemeDescription> arrayList = this.presentingFragmentDescriptions;
        if (arrayList != null) {
            int N4 = arrayList.size();
            for (int i7 = 0; i7 < N4; i7++) {
                ThemeDescription description2 = this.presentingFragmentDescriptions.get(i7);
                description2.setColor(Theme.getColor(description2.getCurrentKey()), false, false);
            }
        }
        ThemeAnimationSettings.onAnimationProgress onanimationprogress = this.animationProgressListener;
        if (onanimationprogress != null) {
            onanimationprogress.setProgress(f);
        }
    }

    public float getThemeAnimationValue() {
        return this.themeAnimationValue;
    }

    private void addStartDescriptions(ArrayList<ThemeDescription> descriptions) {
        if (descriptions != null) {
            this.themeAnimatorDescriptions.add(descriptions);
            int[] startColors = new int[descriptions.size()];
            this.animateStartColors.add(startColors);
            int N = descriptions.size();
            for (int a = 0; a < N; a++) {
                ThemeDescription description = descriptions.get(a);
                startColors[a] = description.getSetColor();
                ThemeDescription.ThemeDescriptionDelegate delegate2 = description.setDelegateDisabled();
                if (delegate2 != null && !this.themeAnimatorDelegate.contains(delegate2)) {
                    this.themeAnimatorDelegate.add(delegate2);
                }
            }
        }
    }

    private void addEndDescriptions(ArrayList<ThemeDescription> descriptions) {
        if (descriptions != null) {
            int[] endColors = new int[descriptions.size()];
            this.animateEndColors.add(endColors);
            int N = descriptions.size();
            for (int a = 0; a < N; a++) {
                endColors[a] = descriptions.get(a).getSetColor();
            }
        }
    }

    public void animateThemedValues(Theme.ThemeInfo theme, int accentId, boolean nightTheme, boolean instant) {
        animateThemedValues(new ThemeAnimationSettings(theme, accentId, nightTheme, instant));
    }

    public void animateThemedValues(final ThemeAnimationSettings settings) {
        BaseFragment fragment;
        if (this.transitionAnimationInProgress || this.startedTracking) {
            this.animateThemeAfterAnimation = true;
            this.animateSetThemeAfterAnimation = settings.theme;
            this.animateSetThemeNightAfterAnimation = settings.nightTheme;
            this.animateSetThemeAccentIdAfterAnimation = settings.accentId;
            return;
        }
        AnimatorSet animatorSet = this.themeAnimatorSet;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.themeAnimatorSet = null;
        }
        boolean startAnimation = false;
        int fragmentCount = settings.onlyTopFragment ? 1 : this.fragmentsStack.size();
        for (int i = 0; i < fragmentCount; i++) {
            if (i == 0) {
                fragment = getLastFragment();
            } else {
                if ((this.inPreviewMode || this.transitionAnimationPreviewMode) && this.fragmentsStack.size() > 1) {
                    ArrayList<BaseFragment> arrayList = this.fragmentsStack;
                    fragment = arrayList.get(arrayList.size() - 2);
                }
            }
            if (fragment != null) {
                startAnimation = true;
                if (settings.resourcesProvider != null) {
                    if (this.messageDrawableOutStart == null) {
                        Theme.MessageDrawable messageDrawable = new Theme.MessageDrawable(0, true, false, this.startColorsProvider);
                        this.messageDrawableOutStart = messageDrawable;
                        messageDrawable.isCrossfadeBackground = true;
                        Theme.MessageDrawable messageDrawable2 = new Theme.MessageDrawable(1, true, false, this.startColorsProvider);
                        this.messageDrawableOutMediaStart = messageDrawable2;
                        messageDrawable2.isCrossfadeBackground = true;
                    }
                    this.startColorsProvider.saveColors(settings.resourcesProvider);
                }
                ArrayList<ThemeDescription> descriptions = fragment.getThemeDescriptions();
                addStartDescriptions(descriptions);
                if (fragment.visibleDialog instanceof BottomSheet) {
                    addStartDescriptions(((BottomSheet) fragment.visibleDialog).getThemeDescriptions());
                } else if (fragment.visibleDialog instanceof AlertDialog) {
                    addStartDescriptions(((AlertDialog) fragment.visibleDialog).getThemeDescriptions());
                }
                if (i == 0) {
                    if (settings.applyTheme) {
                        if (!(settings.accentId == -1 || settings.theme == null)) {
                            settings.theme.setCurrentAccentId(settings.accentId);
                            Theme.saveThemeAccents(settings.theme, true, false, true, false);
                        }
                        Theme.applyTheme(settings.theme, settings.nightTheme);
                    }
                    if (settings.afterStartDescriptionsAddedRunnable != null) {
                        settings.afterStartDescriptionsAddedRunnable.run();
                    }
                }
                addEndDescriptions(descriptions);
                if (fragment.visibleDialog instanceof BottomSheet) {
                    addEndDescriptions(((BottomSheet) fragment.visibleDialog).getThemeDescriptions());
                } else if (fragment.visibleDialog instanceof AlertDialog) {
                    addEndDescriptions(((AlertDialog) fragment.visibleDialog).getThemeDescriptions());
                }
            }
        }
        if (startAnimation) {
            if (!settings.onlyTopFragment) {
                int count = this.fragmentsStack.size() - ((this.inPreviewMode || this.transitionAnimationPreviewMode) ? 2 : 1);
                for (int a = 0; a < count; a++) {
                    BaseFragment fragment2 = this.fragmentsStack.get(a);
                    fragment2.clearViews();
                    fragment2.setParentLayout(this);
                }
            }
            if (settings.instant != 0) {
                setThemeAnimationValue(1.0f);
                this.themeAnimatorDescriptions.clear();
                this.animateStartColors.clear();
                this.animateEndColors.clear();
                this.themeAnimatorDelegate.clear();
                this.presentingFragmentDescriptions = null;
                if (settings.afterAnimationRunnable != null) {
                    settings.afterAnimationRunnable.run();
                    return;
                }
                return;
            }
            Theme.setAnimatingColor(true);
            if (settings.beforeAnimationRunnable != null) {
                settings.beforeAnimationRunnable.run();
            }
            ThemeAnimationSettings.onAnimationProgress onanimationprogress = settings.animationProgress;
            this.animationProgressListener = onanimationprogress;
            if (onanimationprogress != null) {
                onanimationprogress.setProgress(0.0f);
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.themeAnimatorSet = animatorSet2;
            animatorSet2.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (animation.equals(ActionBarLayout.this.themeAnimatorSet)) {
                        ActionBarLayout.this.themeAnimatorDescriptions.clear();
                        ActionBarLayout.this.animateStartColors.clear();
                        ActionBarLayout.this.animateEndColors.clear();
                        ActionBarLayout.this.themeAnimatorDelegate.clear();
                        Theme.setAnimatingColor(false);
                        ArrayList unused = ActionBarLayout.this.presentingFragmentDescriptions = null;
                        AnimatorSet unused2 = ActionBarLayout.this.themeAnimatorSet = null;
                        if (settings.afterAnimationRunnable != null) {
                            settings.afterAnimationRunnable.run();
                        }
                    }
                }

                public void onAnimationCancel(Animator animation) {
                    if (animation.equals(ActionBarLayout.this.themeAnimatorSet)) {
                        ActionBarLayout.this.themeAnimatorDescriptions.clear();
                        ActionBarLayout.this.animateStartColors.clear();
                        ActionBarLayout.this.animateEndColors.clear();
                        ActionBarLayout.this.themeAnimatorDelegate.clear();
                        Theme.setAnimatingColor(false);
                        ArrayList unused = ActionBarLayout.this.presentingFragmentDescriptions = null;
                        AnimatorSet unused2 = ActionBarLayout.this.themeAnimatorSet = null;
                        if (settings.afterAnimationRunnable != null) {
                            settings.afterAnimationRunnable.run();
                        }
                    }
                }
            });
            this.themeAnimatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, "themeAnimationValue", new float[]{0.0f, 1.0f})});
            this.themeAnimatorSet.setDuration(settings.duration);
            this.themeAnimatorSet.start();
        }
    }

    public void rebuildLogout() {
        this.containerView.removeAllViews();
        this.containerViewBack.removeAllViews();
        this.currentActionBar = null;
        this.newFragment = null;
        this.oldFragment = null;
    }

    public void rebuildAllFragmentViews(boolean last, boolean showLastAfter) {
        if (this.transitionAnimationInProgress || this.startedTracking) {
            this.rebuildAfterAnimation = true;
            this.rebuildLastAfterAnimation = last;
            this.showLastAfterAnimation = showLastAfter;
            return;
        }
        int size = this.fragmentsStack.size();
        if (!last) {
            size--;
        }
        if (this.inPreviewMode) {
            size--;
        }
        for (int a = 0; a < size; a++) {
            this.fragmentsStack.get(a).clearViews();
            this.fragmentsStack.get(a).setParentLayout(this);
        }
        ActionBarLayoutDelegate actionBarLayoutDelegate = this.delegate;
        if (actionBarLayoutDelegate != null) {
            actionBarLayoutDelegate.onRebuildAllFragments(this, last);
        }
        if (showLastAfter) {
            showLastFragment();
        }
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        ActionBar actionBar;
        if (keyCode == 82 && !checkTransitionAnimation() && !this.startedTracking && (actionBar = this.currentActionBar) != null) {
            actionBar.onMenuButtonPressed();
        }
        return super.onKeyUp(keyCode, event);
    }

    public void onActionModeStarted(Object mode) {
        ActionBar actionBar = this.currentActionBar;
        if (actionBar != null) {
            actionBar.setVisibility(8);
        }
        this.inActionMode = true;
    }

    public void onActionModeFinished(Object mode) {
        ActionBar actionBar = this.currentActionBar;
        if (actionBar != null) {
            actionBar.setVisibility(0);
        }
        this.inActionMode = false;
    }

    private void onCloseAnimationEnd() {
        if (this.transitionAnimationInProgress && this.onCloseAnimationEndRunnable != null) {
            this.transitionAnimationInProgress = false;
            this.layoutToIgnore = null;
            this.transitionAnimationPreviewMode = false;
            this.transitionAnimationStartTime = 0;
            this.newFragment = null;
            this.oldFragment = null;
            Runnable endRunnable = this.onCloseAnimationEndRunnable;
            this.onCloseAnimationEndRunnable = null;
            endRunnable.run();
            checkNeedRebuild();
            checkNeedRebuild();
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
        if (this.transitionAnimationInProgress && this.onOpenAnimationEndRunnable != null) {
            this.transitionAnimationInProgress = false;
            this.layoutToIgnore = null;
            this.transitionAnimationPreviewMode = false;
            this.transitionAnimationStartTime = 0;
            this.newFragment = null;
            this.oldFragment = null;
            Runnable endRunnable = this.onOpenAnimationEndRunnable;
            this.onOpenAnimationEndRunnable = null;
            endRunnable.run();
            checkNeedRebuild();
        }
    }

    public void startActivityForResult(Intent intent, int requestCode) {
        if (this.parentActivity != null) {
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
            }
            if (intent != null) {
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

    public void setTitleOverlayText(String title, int titleId, Runnable action) {
        this.titleOverlayText = title;
        this.titleOverlayTextId = titleId;
        this.overlayAction = action;
        for (int a = 0; a < this.fragmentsStack.size(); a++) {
            BaseFragment fragment = this.fragmentsStack.get(a);
            if (fragment.actionBar != null) {
                fragment.actionBar.setTitleOverlayText(this.titleOverlayText, this.titleOverlayTextId, action);
            }
        }
    }

    public boolean extendActionMode(Menu menu) {
        if (!this.fragmentsStack.isEmpty()) {
            ArrayList<BaseFragment> arrayList = this.fragmentsStack;
            if (arrayList.get(arrayList.size() - 1).extendActionMode(menu)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    public void setFragmentPanTranslationOffset(int offset) {
        LayoutContainer layoutContainer = this.containerView;
        if (layoutContainer != null) {
            layoutContainer.setFragmentPanTranslationOffset(offset);
        }
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

    private class StartColorsProvider implements Theme.ResourcesProvider {
        HashMap<String, Integer> colors;
        String[] keysToSave;

        public /* synthetic */ void applyServiceShaderMatrix(int i, int i2, float f, float f2) {
            Theme.ResourcesProvider.CC.$default$applyServiceShaderMatrix(this, i, i2, f, f2);
        }

        public /* synthetic */ int getColorOrDefault(String str) {
            return Theme.ResourcesProvider.CC.$default$getColorOrDefault(this, str);
        }

        public /* synthetic */ Drawable getDrawable(String str) {
            return Theme.ResourcesProvider.CC.$default$getDrawable(this, str);
        }

        public /* synthetic */ Paint getPaint(String str) {
            return Theme.ResourcesProvider.CC.$default$getPaint(this, str);
        }

        public /* synthetic */ boolean hasGradientService() {
            return Theme.ResourcesProvider.CC.$default$hasGradientService(this);
        }

        public /* synthetic */ void setAnimatedColor(String str, int i) {
            Theme.ResourcesProvider.CC.$default$setAnimatedColor(this, str, i);
        }

        private StartColorsProvider() {
            this.colors = new HashMap<>();
            this.keysToSave = new String[]{"chat_outBubble", "chat_outBubbleGradient", "chat_outBubbleGradient2", "chat_outBubbleGradient3", "chat_outBubbleGradientAnimated", "chat_outBubbleShadow"};
        }

        public Integer getColor(String key) {
            return this.colors.get(key);
        }

        public Integer getCurrentColor(String key) {
            return this.colors.get(key);
        }

        public void saveColors(Theme.ResourcesProvider fragmentResourceProvider) {
            this.colors.clear();
            for (String key : this.keysToSave) {
                this.colors.put(key, fragmentResourceProvider.getCurrentColor(key));
            }
        }
    }
}
