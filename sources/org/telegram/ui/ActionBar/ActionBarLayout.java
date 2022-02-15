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
import androidx.annotation.Keep;
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
    private boolean maybeStartTracking;
    public Theme.MessageDrawable messageDrawableOutMediaStart;
    public Theme.MessageDrawable messageDrawableOutStart;
    /* access modifiers changed from: private */
    public BaseFragment newFragment;
    /* access modifiers changed from: private */
    public BaseFragment oldFragment;
    private Runnable onCloseAnimationEndRunnable;
    private Runnable onOpenAnimationEndRunnable;
    private Runnable overlayAction;
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

    public boolean hasOverlappingRendering() {
        return false;
    }

    static /* synthetic */ float access$1116(ActionBarLayout actionBarLayout, float f) {
        float f2 = actionBarLayout.animationProgress + f;
        actionBarLayout.animationProgress = f2;
        return f2;
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
        public boolean drawChild(Canvas canvas, View view, long j) {
            int i;
            int i2;
            if (view instanceof ActionBar) {
                return super.drawChild(canvas, view, j);
            }
            int childCount = getChildCount();
            int i3 = 0;
            while (true) {
                if (i3 >= childCount) {
                    break;
                }
                View childAt = getChildAt(i3);
                if (childAt == view || !(childAt instanceof ActionBar) || childAt.getVisibility() != 0) {
                    i3++;
                } else if (((ActionBar) childAt).getCastShadows()) {
                    i2 = childAt.getMeasuredHeight();
                    i = (int) childAt.getY();
                }
            }
            i2 = 0;
            i = 0;
            boolean drawChild = super.drawChild(canvas, view, j);
            if (!(i2 == 0 || ActionBarLayout.headerShadowDrawable == null)) {
                int i4 = i + i2;
                ActionBarLayout.headerShadowDrawable.setBounds(0, i4, getMeasuredWidth(), ActionBarLayout.headerShadowDrawable.getIntrinsicHeight() + i4);
                ActionBarLayout.headerShadowDrawable.draw(canvas);
            }
            return drawChild;
        }

        public boolean hasOverlappingRendering() {
            return Build.VERSION.SDK_INT >= 28;
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            int i3;
            int size = View.MeasureSpec.getSize(i);
            int size2 = View.MeasureSpec.getSize(i2);
            int childCount = getChildCount();
            int i4 = 0;
            while (true) {
                if (i4 >= childCount) {
                    i3 = 0;
                    break;
                }
                View childAt = getChildAt(i4);
                if (childAt instanceof ActionBar) {
                    childAt.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                    i3 = childAt.getMeasuredHeight();
                    break;
                }
                i4++;
            }
            for (int i5 = 0; i5 < childCount; i5++) {
                View childAt2 = getChildAt(i5);
                if (!(childAt2 instanceof ActionBar)) {
                    if (childAt2.getFitsSystemWindows()) {
                        measureChildWithMargins(childAt2, i, 0, i2, 0);
                    } else {
                        measureChildWithMargins(childAt2, i, 0, i2, i3);
                    }
                }
            }
            setMeasuredDimension(size, size2);
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            int i5;
            int childCount = getChildCount();
            boolean z2 = false;
            int i6 = 0;
            while (true) {
                if (i6 >= childCount) {
                    i5 = 0;
                    break;
                }
                View childAt = getChildAt(i6);
                if (childAt instanceof ActionBar) {
                    i5 = childAt.getMeasuredHeight();
                    childAt.layout(0, 0, childAt.getMeasuredWidth(), i5);
                    break;
                }
                i6++;
            }
            for (int i7 = 0; i7 < childCount; i7++) {
                View childAt2 = getChildAt(i7);
                if (!(childAt2 instanceof ActionBar)) {
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) childAt2.getLayoutParams();
                    if (childAt2.getFitsSystemWindows()) {
                        int i8 = layoutParams.leftMargin;
                        childAt2.layout(i8, layoutParams.topMargin, childAt2.getMeasuredWidth() + i8, layoutParams.topMargin + childAt2.getMeasuredHeight());
                    } else {
                        int i9 = layoutParams.leftMargin;
                        childAt2.layout(i9, layoutParams.topMargin + i5, childAt2.getMeasuredWidth() + i9, layoutParams.topMargin + i5 + childAt2.getMeasuredHeight());
                    }
                }
            }
            View rootView = getRootView();
            getWindowVisibleDisplayFrame(this.rect);
            int height = (rootView.getHeight() - (this.rect.top != 0 ? AndroidUtilities.statusBarHeight : 0)) - AndroidUtilities.getViewInset(rootView);
            Rect rect2 = this.rect;
            if (height - (rect2.bottom - rect2.top) > 0) {
                z2 = true;
            }
            this.isKeyboardVisible = z2;
            if (ActionBarLayout.this.waitingForKeyboardCloseRunnable != null) {
                ActionBarLayout actionBarLayout = ActionBarLayout.this;
                if (!actionBarLayout.containerView.isKeyboardVisible && !actionBarLayout.containerViewBack.isKeyboardVisible) {
                    AndroidUtilities.cancelRunOnUIThread(ActionBarLayout.this.waitingForKeyboardCloseRunnable);
                    ActionBarLayout.this.waitingForKeyboardCloseRunnable.run();
                    Runnable unused = ActionBarLayout.this.waitingForKeyboardCloseRunnable = null;
                }
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
                if (r3 == 0) goto L_0x0042
                int r3 = r6.getActionMasked()
                r4 = 5
                if (r3 != r4) goto L_0x002d
                goto L_0x0042
            L_0x002d:
                if (r0 == 0) goto L_0x0035
                org.telegram.ui.ActionBar.ActionBarLayout r0 = org.telegram.ui.ActionBar.ActionBarLayout.this     // Catch:{ all -> 0x003e }
                org.telegram.ui.ActionBar.ActionBarLayout$LayoutContainer r0 = r0.containerView     // Catch:{ all -> 0x003e }
                if (r5 == r0) goto L_0x003c
            L_0x0035:
                boolean r6 = super.dispatchTouchEvent(r6)     // Catch:{ all -> 0x003e }
                if (r6 == 0) goto L_0x003c
                goto L_0x003d
            L_0x003c:
                r1 = 0
            L_0x003d:
                return r1
            L_0x003e:
                r6 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r6)
            L_0x0042:
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

        public void setFragmentPanTranslationOffset(int i) {
            this.fragmentPanTranslationOffset = i;
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

        public ThemeAnimationSettings(Theme.ThemeInfo themeInfo, int i, boolean z, boolean z2) {
            this.theme = themeInfo;
            this.accentId = i;
            this.nightTheme = z;
            this.instant = z2;
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

    public void init(ArrayList<BaseFragment> arrayList) {
        this.fragmentsStack = arrayList;
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

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (!this.fragmentsStack.isEmpty()) {
            int size = this.fragmentsStack.size();
            for (int i = 0; i < size; i++) {
                BaseFragment baseFragment = this.fragmentsStack.get(i);
                baseFragment.onConfigurationChanged(configuration);
                Dialog dialog = baseFragment.visibleDialog;
                if (dialog instanceof BottomSheet) {
                    ((BottomSheet) dialog).onConfigurationChanged(configuration);
                }
            }
        }
    }

    public void drawHeaderShadow(Canvas canvas, int i) {
        drawHeaderShadow(canvas, 255, i);
    }

    public void setInBubbleMode(boolean z) {
        this.inBubbleMode = z;
    }

    public boolean isInBubbleMode() {
        return this.inBubbleMode;
    }

    public void drawHeaderShadow(Canvas canvas, int i, int i2) {
        Drawable drawable = headerShadowDrawable;
        if (drawable != null) {
            if (Build.VERSION.SDK_INT < 19) {
                drawable.setAlpha(i);
            } else if (drawable.getAlpha() != i) {
                headerShadowDrawable.setAlpha(i);
            }
            headerShadowDrawable.setBounds(0, i2, getMeasuredWidth(), headerShadowDrawable.getIntrinsicHeight() + i2);
            headerShadowDrawable.draw(canvas);
        }
    }

    @Keep
    public void setInnerTranslationX(float f) {
        int navigationBarColor;
        int navigationBarColor2;
        this.innerTranslationX = f;
        invalidate();
        if (this.fragmentsStack.size() >= 2 && this.containerView.getMeasuredWidth() > 0) {
            float measuredWidth = f / ((float) this.containerView.getMeasuredWidth());
            ArrayList<BaseFragment> arrayList = this.fragmentsStack;
            BaseFragment baseFragment = arrayList.get(arrayList.size() - 2);
            baseFragment.onSlideProgress(false, measuredWidth);
            ArrayList<BaseFragment> arrayList2 = this.fragmentsStack;
            BaseFragment baseFragment2 = arrayList2.get(arrayList2.size() - 1);
            if (baseFragment2.isBeginToShow() && (navigationBarColor = baseFragment2.getNavigationBarColor()) != (navigationBarColor2 = baseFragment.getNavigationBarColor())) {
                baseFragment2.setNavigationBarColor(ColorUtils.blendARGB(navigationBarColor, navigationBarColor2, MathUtils.clamp(measuredWidth * 2.0f, 0.0f, 1.0f)));
            }
        }
    }

    @Keep
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

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return this.animationInProgress || checkTransitionAnimation() || onTouchEvent(motionEvent);
    }

    public void requestDisallowInterceptTouchEvent(boolean z) {
        onTouchEvent((MotionEvent) null);
        super.requestDisallowInterceptTouchEvent(z);
    }

    public boolean dispatchKeyEventPreIme(KeyEvent keyEvent) {
        if (keyEvent == null || keyEvent.getKeyCode() != 4 || keyEvent.getAction() != 1) {
            return super.dispatchKeyEventPreIme(keyEvent);
        }
        ActionBarLayoutDelegate actionBarLayoutDelegate = this.delegate;
        if ((actionBarLayoutDelegate == null || !actionBarLayoutDelegate.onPreIme()) && !super.dispatchKeyEventPreIme(keyEvent)) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean drawChild(Canvas canvas, View view, long j) {
        LayoutContainer layoutContainer;
        DrawerLayoutContainer drawerLayoutContainer2 = this.drawerLayoutContainer;
        if (drawerLayoutContainer2 != null && drawerLayoutContainer2.isDrawCurrentPreviewFragmentAbove() && (this.inPreviewMode || this.transitionAnimationPreviewMode || this.previewOpenAnimationInProgress)) {
            BaseFragment baseFragment = this.oldFragment;
            if (view == ((baseFragment == null || !baseFragment.inPreviewMode) ? this.containerView : this.containerViewBack)) {
                this.drawerLayoutContainer.invalidate();
                return false;
            }
        }
        int width = (getWidth() - getPaddingLeft()) - getPaddingRight();
        int paddingRight = ((int) this.innerTranslationX) + getPaddingRight();
        int paddingLeft = getPaddingLeft();
        int paddingLeft2 = getPaddingLeft() + width;
        if (view == this.containerViewBack) {
            paddingLeft2 = AndroidUtilities.dp(1.0f) + paddingRight;
        } else if (view == this.containerView) {
            paddingLeft = paddingRight;
        }
        int save = canvas.save();
        if (!this.transitionAnimationInProgress && !this.inPreviewMode) {
            canvas.clipRect(paddingLeft, 0, paddingLeft2, getHeight());
        }
        if ((this.inPreviewMode || this.transitionAnimationPreviewMode) && view == (layoutContainer = this.containerView)) {
            drawPreviewDrawables(canvas, layoutContainer);
        }
        boolean drawChild = super.drawChild(canvas, view, j);
        canvas.restoreToCount(save);
        if (paddingRight != 0) {
            float f = 0.0f;
            if (view == this.containerView) {
                float max = Math.max(0.0f, Math.min(((float) (width - paddingRight)) / ((float) AndroidUtilities.dp(20.0f)), 1.0f));
                Drawable drawable = layerShadowDrawable;
                drawable.setBounds(paddingRight - drawable.getIntrinsicWidth(), view.getTop(), paddingRight, view.getBottom());
                layerShadowDrawable.setAlpha((int) (max * 255.0f));
                layerShadowDrawable.draw(canvas);
            } else if (view == this.containerViewBack) {
                float min = Math.min(0.8f, ((float) (width - paddingRight)) / ((float) width));
                if (min >= 0.0f) {
                    f = min;
                }
                scrimPaint.setColor(((int) (f * 153.0f)) << 24);
                canvas.drawRect((float) paddingLeft, 0.0f, (float) paddingLeft2, (float) getHeight(), scrimPaint);
            }
        }
        return drawChild;
    }

    public float getCurrentPreviewFragmentAlpha() {
        if (!this.inPreviewMode && !this.transitionAnimationPreviewMode && !this.previewOpenAnimationInProgress) {
            return 0.0f;
        }
        BaseFragment baseFragment = this.oldFragment;
        return ((baseFragment == null || !baseFragment.inPreviewMode) ? this.containerView : this.containerViewBack).getAlpha();
    }

    public void drawCurrentPreviewFragment(Canvas canvas, Drawable drawable) {
        if (this.inPreviewMode || this.transitionAnimationPreviewMode || this.previewOpenAnimationInProgress) {
            BaseFragment baseFragment = this.oldFragment;
            LayoutContainer layoutContainer = (baseFragment == null || !baseFragment.inPreviewMode) ? this.containerView : this.containerViewBack;
            drawPreviewDrawables(canvas, layoutContainer);
            if (layoutContainer.getAlpha() < 1.0f) {
                canvas.saveLayerAlpha(0.0f, 0.0f, (float) getWidth(), (float) getHeight(), (int) (layoutContainer.getAlpha() * 255.0f), 31);
            } else {
                canvas.save();
            }
            canvas.concat(layoutContainer.getMatrix());
            layoutContainer.draw(canvas);
            if (drawable != null) {
                int i = 0;
                View childAt = layoutContainer.getChildAt(0);
                if (childAt != null) {
                    ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) childAt.getLayoutParams();
                    Rect rect2 = new Rect();
                    childAt.getLocalVisibleRect(rect2);
                    rect2.offset(marginLayoutParams.leftMargin, marginLayoutParams.topMargin);
                    int i2 = rect2.top;
                    if (Build.VERSION.SDK_INT >= 21) {
                        i = AndroidUtilities.statusBarHeight - 1;
                    }
                    rect2.top = i2 + i;
                    drawable.setAlpha((int) (layoutContainer.getAlpha() * 255.0f));
                    drawable.setBounds(rect2);
                    drawable.draw(canvas);
                }
            }
            canvas.restore();
        }
    }

    private void drawPreviewDrawables(Canvas canvas, ViewGroup viewGroup) {
        int i = 0;
        View childAt = viewGroup.getChildAt(0);
        if (childAt != null) {
            this.previewBackgroundDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
            this.previewBackgroundDrawable.draw(canvas);
            if (this.previewMenu == null) {
                int measuredWidth = (getMeasuredWidth() - AndroidUtilities.dp(24.0f)) / 2;
                float top = ((float) childAt.getTop()) + viewGroup.getTranslationY();
                if (Build.VERSION.SDK_INT < 21) {
                    i = 20;
                }
                int dp = (int) (top - ((float) AndroidUtilities.dp((float) (i + 12))));
                Theme.moveUpDrawable.setBounds(measuredWidth, dp, AndroidUtilities.dp(24.0f) + measuredWidth, AndroidUtilities.dp(24.0f) + dp);
                Theme.moveUpDrawable.draw(canvas);
            }
        }
    }

    public void setDelegate(ActionBarLayoutDelegate actionBarLayoutDelegate) {
        this.delegate = actionBarLayoutDelegate;
    }

    /* access modifiers changed from: private */
    public void onSlideAnimationEnd(boolean z) {
        ViewGroup viewGroup;
        ViewGroup viewGroup2;
        if (!z) {
            if (this.fragmentsStack.size() >= 2) {
                ArrayList<BaseFragment> arrayList = this.fragmentsStack;
                BaseFragment baseFragment = arrayList.get(arrayList.size() - 1);
                baseFragment.prepareFragmentToSlide(true, false);
                baseFragment.onPause();
                baseFragment.onFragmentDestroy();
                baseFragment.setParentLayout((ActionBarLayout) null);
                ArrayList<BaseFragment> arrayList2 = this.fragmentsStack;
                arrayList2.remove(arrayList2.size() - 1);
                LayoutContainer layoutContainer = this.containerView;
                LayoutContainer layoutContainer2 = this.containerViewBack;
                this.containerView = layoutContainer2;
                this.containerViewBack = layoutContainer;
                bringChildToFront(layoutContainer2);
                ArrayList<BaseFragment> arrayList3 = this.fragmentsStack;
                BaseFragment baseFragment2 = arrayList3.get(arrayList3.size() - 1);
                this.currentActionBar = baseFragment2.actionBar;
                baseFragment2.onResume();
                baseFragment2.onBecomeFullyVisible();
                baseFragment2.prepareFragmentToSlide(false, false);
            } else {
                return;
            }
        } else if (this.fragmentsStack.size() >= 2) {
            ArrayList<BaseFragment> arrayList4 = this.fragmentsStack;
            arrayList4.get(arrayList4.size() - 1).prepareFragmentToSlide(true, false);
            ArrayList<BaseFragment> arrayList5 = this.fragmentsStack;
            BaseFragment baseFragment3 = arrayList5.get(arrayList5.size() - 2);
            baseFragment3.prepareFragmentToSlide(false, false);
            baseFragment3.onPause();
            View view = baseFragment3.fragmentView;
            if (!(view == null || (viewGroup2 = (ViewGroup) view.getParent()) == null)) {
                baseFragment3.onRemoveFromParent();
                viewGroup2.removeViewInLayout(baseFragment3.fragmentView);
            }
            ActionBar actionBar = baseFragment3.actionBar;
            if (!(actionBar == null || !actionBar.shouldAddToContainer() || (viewGroup = (ViewGroup) baseFragment3.actionBar.getParent()) == null)) {
                viewGroup.removeViewInLayout(baseFragment3.actionBar);
            }
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
        ArrayList<BaseFragment> arrayList = this.fragmentsStack;
        BaseFragment baseFragment = arrayList.get(arrayList.size() - 2);
        View view = baseFragment.fragmentView;
        if (view == null) {
            view = baseFragment.createView(this.parentActivity);
        }
        ViewGroup viewGroup = (ViewGroup) view.getParent();
        if (viewGroup != null) {
            baseFragment.onRemoveFromParent();
            viewGroup.removeView(view);
        }
        this.containerViewBack.addView(view);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        layoutParams.leftMargin = 0;
        layoutParams.rightMargin = 0;
        layoutParams.bottomMargin = 0;
        layoutParams.topMargin = 0;
        view.setLayoutParams(layoutParams);
        ActionBar actionBar = baseFragment.actionBar;
        if (actionBar != null && actionBar.shouldAddToContainer()) {
            ViewGroup viewGroup2 = (ViewGroup) baseFragment.actionBar.getParent();
            if (viewGroup2 != null) {
                viewGroup2.removeView(baseFragment.actionBar);
            }
            if (this.removeActionBarExtraHeight) {
                baseFragment.actionBar.setOccupyStatusBar(false);
            }
            this.containerViewBack.addView(baseFragment.actionBar);
            baseFragment.actionBar.setTitleOverlayText(this.titleOverlayText, this.titleOverlayTextId, this.overlayAction);
        }
        if (!baseFragment.hasOwnBackground && view.getBackground() == null) {
            view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        }
        baseFragment.onResume();
        if (this.themeAnimatorSet != null) {
            this.presentingFragmentDescriptions = baseFragment.getThemeDescriptions();
        }
        ArrayList<BaseFragment> arrayList2 = this.fragmentsStack;
        arrayList2.get(arrayList2.size() - 1).prepareFragmentToSlide(true, true);
        baseFragment.prepareFragmentToSlide(false, true);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        Animator customSlideTransition;
        if (checkTransitionAnimation() || this.inActionMode || this.animationInProgress) {
            return false;
        }
        if (this.fragmentsStack.size() > 1) {
            if (motionEvent != null && motionEvent.getAction() == 0) {
                ArrayList<BaseFragment> arrayList = this.fragmentsStack;
                if (!arrayList.get(arrayList.size() - 1).isSwipeBackEnabled(motionEvent)) {
                    this.maybeStartTracking = false;
                    this.startedTracking = false;
                    return false;
                }
                this.startedTrackingPointerId = motionEvent.getPointerId(0);
                this.maybeStartTracking = true;
                this.startedTrackingX = (int) motionEvent.getX();
                this.startedTrackingY = (int) motionEvent.getY();
                VelocityTracker velocityTracker2 = this.velocityTracker;
                if (velocityTracker2 != null) {
                    velocityTracker2.clear();
                }
            } else if (motionEvent != null && motionEvent.getAction() == 2 && motionEvent.getPointerId(0) == this.startedTrackingPointerId) {
                if (this.velocityTracker == null) {
                    this.velocityTracker = VelocityTracker.obtain();
                }
                int max = Math.max(0, (int) (motionEvent.getX() - ((float) this.startedTrackingX)));
                int abs = Math.abs(((int) motionEvent.getY()) - this.startedTrackingY);
                this.velocityTracker.addMovement(motionEvent);
                if (!this.transitionAnimationInProgress && !this.inPreviewMode && this.maybeStartTracking && !this.startedTracking && ((float) max) >= AndroidUtilities.getPixelsInCM(0.4f, true) && Math.abs(max) / 3 > abs) {
                    ArrayList<BaseFragment> arrayList2 = this.fragmentsStack;
                    if (!arrayList2.get(arrayList2.size() - 1).canBeginSlide() || findScrollingChild(this, motionEvent.getX(), motionEvent.getY()) != null) {
                        this.maybeStartTracking = false;
                    } else {
                        prepareForMoving(motionEvent);
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
                    float f = (float) max;
                    this.containerView.setTranslationX(f);
                    setInnerTranslationX(f);
                }
            } else if (motionEvent != null && motionEvent.getPointerId(0) == this.startedTrackingPointerId && (motionEvent.getAction() == 3 || motionEvent.getAction() == 1 || motionEvent.getAction() == 6)) {
                if (this.velocityTracker == null) {
                    this.velocityTracker = VelocityTracker.obtain();
                }
                this.velocityTracker.computeCurrentVelocity(1000);
                ArrayList<BaseFragment> arrayList4 = this.fragmentsStack;
                BaseFragment baseFragment = arrayList4.get(arrayList4.size() - 1);
                if (!this.inPreviewMode && !this.transitionAnimationPreviewMode && !this.startedTracking && baseFragment.isSwipeBackEnabled(motionEvent)) {
                    float xVelocity = this.velocityTracker.getXVelocity();
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
                    float xVelocity2 = this.velocityTracker.getXVelocity();
                    final boolean z = x < ((float) this.containerView.getMeasuredWidth()) / 3.0f && (xVelocity2 < 3500.0f || xVelocity2 < this.velocityTracker.getYVelocity());
                    if (!z) {
                        x = ((float) this.containerView.getMeasuredWidth()) - x;
                        int max2 = Math.max((int) ((200.0f / ((float) this.containerView.getMeasuredWidth())) * x), 50);
                        LayoutContainer layoutContainer = this.containerView;
                        long j = (long) max2;
                        animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(layoutContainer, View.TRANSLATION_X, new float[]{(float) layoutContainer.getMeasuredWidth()}).setDuration(j), ObjectAnimator.ofFloat(this, "innerTranslationX", new float[]{(float) this.containerView.getMeasuredWidth()}).setDuration(j)});
                    } else {
                        int max3 = Math.max((int) ((200.0f / ((float) this.containerView.getMeasuredWidth())) * x), 50);
                        long j2 = (long) max3;
                        animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.containerView, View.TRANSLATION_X, new float[]{0.0f}).setDuration(j2), ObjectAnimator.ofFloat(this, "innerTranslationX", new float[]{0.0f}).setDuration(j2)});
                    }
                    Animator customSlideTransition2 = baseFragment.getCustomSlideTransition(false, z, x);
                    if (customSlideTransition2 != null) {
                        animatorSet.playTogether(new Animator[]{customSlideTransition2});
                    }
                    ArrayList<BaseFragment> arrayList5 = this.fragmentsStack;
                    BaseFragment baseFragment2 = arrayList5.get(arrayList5.size() - 2);
                    if (!(baseFragment2 == null || (customSlideTransition = baseFragment2.getCustomSlideTransition(false, z, x)) == null)) {
                        animatorSet.playTogether(new Animator[]{customSlideTransition});
                    }
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
                VelocityTracker velocityTracker3 = this.velocityTracker;
                if (velocityTracker3 != null) {
                    velocityTracker3.recycle();
                    this.velocityTracker = null;
                }
            } else if (motionEvent == null) {
                this.maybeStartTracking = false;
                this.startedTracking = false;
                VelocityTracker velocityTracker4 = this.velocityTracker;
                if (velocityTracker4 != null) {
                    velocityTracker4.recycle();
                    this.velocityTracker = null;
                }
            }
        }
        return this.startedTracking;
    }

    public void onBackPressed() {
        if (!this.transitionAnimationPreviewMode && !this.startedTracking && !checkTransitionAnimation() && !this.fragmentsStack.isEmpty() && !GroupCallPip.onBackPressed()) {
            ActionBar actionBar = this.currentActionBar;
            if (actionBar != null && !actionBar.isActionModeShowed()) {
                ActionBar actionBar2 = this.currentActionBar;
                if (actionBar2.isSearchFieldVisible) {
                    actionBar2.closeSearchField();
                    return;
                }
            }
            ArrayList<BaseFragment> arrayList = this.fragmentsStack;
            if (arrayList.get(arrayList.size() - 1).onBackPressed() && !this.fragmentsStack.isEmpty()) {
                closeLastFragment(true);
            }
        }
    }

    public void onLowMemory() {
        Iterator<BaseFragment> it = this.fragmentsStack.iterator();
        while (it.hasNext()) {
            it.next().onLowMemory();
        }
    }

    /* access modifiers changed from: private */
    public void onAnimationEndCheck(boolean z) {
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

    private void presentFragmentInternalRemoveOld(boolean z, BaseFragment baseFragment) {
        ViewGroup viewGroup;
        ViewGroup viewGroup2;
        if (baseFragment != null) {
            baseFragment.onBecomeFullyHidden();
            baseFragment.onPause();
            if (z) {
                baseFragment.onFragmentDestroy();
                baseFragment.setParentLayout((ActionBarLayout) null);
                this.fragmentsStack.remove(baseFragment);
            } else {
                View view = baseFragment.fragmentView;
                if (!(view == null || (viewGroup2 = (ViewGroup) view.getParent()) == null)) {
                    baseFragment.onRemoveFromParent();
                    try {
                        viewGroup2.removeViewInLayout(baseFragment.fragmentView);
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                        try {
                            viewGroup2.removeView(baseFragment.fragmentView);
                        } catch (Exception e2) {
                            FileLog.e((Throwable) e2);
                        }
                    }
                }
                ActionBar actionBar = baseFragment.actionBar;
                if (!(actionBar == null || !actionBar.shouldAddToContainer() || (viewGroup = (ViewGroup) baseFragment.actionBar.getParent()) == null)) {
                    viewGroup.removeViewInLayout(baseFragment.actionBar);
                }
            }
            this.containerViewBack.setVisibility(4);
        }
    }

    public boolean presentFragmentAsPreview(BaseFragment baseFragment) {
        return presentFragment(baseFragment, false, false, true, true, (View) null);
    }

    public boolean presentFragmentAsPreviewWithMenu(BaseFragment baseFragment, View view) {
        return presentFragment(baseFragment, false, false, true, true, view);
    }

    public boolean presentFragment(BaseFragment baseFragment) {
        return presentFragment(baseFragment, false, false, true, false, (View) null);
    }

    public boolean presentFragment(BaseFragment baseFragment, boolean z) {
        return presentFragment(baseFragment, z, false, true, false, (View) null);
    }

    /* access modifiers changed from: private */
    public void startLayoutAnimation(final boolean z, final boolean z2, final boolean z3) {
        if (z2) {
            this.animationProgress = 0.0f;
            this.lastFrameTime = System.nanoTime() / 1000000;
        }
        AnonymousClass2 r0 = new Runnable() {
            public void run() {
                if (ActionBarLayout.this.animationRunnable == this) {
                    Integer num = null;
                    Runnable unused = ActionBarLayout.this.animationRunnable = null;
                    if (z2) {
                        long unused2 = ActionBarLayout.this.transitionAnimationStartTime = System.currentTimeMillis();
                    }
                    long nanoTime = System.nanoTime() / 1000000;
                    long access$1000 = nanoTime - ActionBarLayout.this.lastFrameTime;
                    if (access$1000 > 18) {
                        access$1000 = 18;
                    }
                    long unused3 = ActionBarLayout.this.lastFrameTime = nanoTime;
                    ActionBarLayout.access$1116(ActionBarLayout.this, ((float) access$1000) / 150.0f);
                    if (ActionBarLayout.this.animationProgress > 1.0f) {
                        float unused4 = ActionBarLayout.this.animationProgress = 1.0f;
                    }
                    if (ActionBarLayout.this.newFragment != null) {
                        ActionBarLayout.this.newFragment.onTransitionAnimationProgress(true, ActionBarLayout.this.animationProgress);
                    }
                    if (ActionBarLayout.this.oldFragment != null) {
                        ActionBarLayout.this.oldFragment.onTransitionAnimationProgress(false, ActionBarLayout.this.animationProgress);
                    }
                    Integer valueOf = ActionBarLayout.this.oldFragment != null ? Integer.valueOf(ActionBarLayout.this.oldFragment.getNavigationBarColor()) : null;
                    if (ActionBarLayout.this.newFragment != null) {
                        num = Integer.valueOf(ActionBarLayout.this.newFragment.getNavigationBarColor());
                    }
                    if (!(ActionBarLayout.this.newFragment == null || ActionBarLayout.this.newFragment.inPreviewMode || valueOf == null)) {
                        ActionBarLayout.this.newFragment.setNavigationBarColor(ColorUtils.blendARGB(valueOf.intValue(), num.intValue(), MathUtils.clamp((ActionBarLayout.this.animationProgress * 2.0f) - (z ? 1.0f : 0.0f), 0.0f, 1.0f)));
                    }
                    float interpolation = ActionBarLayout.this.decelerateInterpolator.getInterpolation(ActionBarLayout.this.animationProgress);
                    if (z) {
                        ActionBarLayout.this.containerView.setAlpha(interpolation);
                        if (z3) {
                            float f = (0.1f * interpolation) + 0.9f;
                            ActionBarLayout.this.containerView.setScaleX(f);
                            ActionBarLayout.this.containerView.setScaleY(f);
                            ActionBarLayout.this.previewBackgroundDrawable.setAlpha((int) (46.0f * interpolation));
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
                            float f3 = (0.1f * f2) + 0.9f;
                            ActionBarLayout.this.containerViewBack.setScaleX(f3);
                            ActionBarLayout.this.containerViewBack.setScaleY(f3);
                            ActionBarLayout.this.previewBackgroundDrawable.setAlpha((int) (46.0f * f2));
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

    public boolean presentFragment(BaseFragment baseFragment, boolean z, boolean z2, boolean z3, boolean z4) {
        return presentFragment(baseFragment, z, z2, z3, z4, (View) null);
    }

    public boolean presentFragment(BaseFragment baseFragment, boolean z, boolean z2, boolean z3, boolean z4, View view) {
        ActionBarLayoutDelegate actionBarLayoutDelegate;
        final BaseFragment baseFragment2;
        int i;
        final BaseFragment baseFragment3 = baseFragment;
        boolean z5 = z;
        boolean z6 = z2;
        final boolean z7 = z4;
        View view2 = view;
        if (baseFragment3 == null || checkTransitionAnimation() || (((actionBarLayoutDelegate = this.delegate) != null && z3 && !actionBarLayoutDelegate.needPresentFragment(baseFragment3, z5, z6, this)) || !baseFragment.onFragmentCreate())) {
            return false;
        }
        baseFragment3.setInPreviewMode(z7);
        baseFragment3.setInMenuMode(view2 != null);
        if (this.parentActivity.getCurrentFocus() != null && baseFragment.hideKeyboardOnShow() && !z7) {
            AndroidUtilities.hideKeyboard(this.parentActivity.getCurrentFocus());
        }
        boolean z8 = z7 || (!z6 && MessagesController.getGlobalMainSettings().getBoolean("view_animations", true));
        if (!this.fragmentsStack.isEmpty()) {
            ArrayList<BaseFragment> arrayList = this.fragmentsStack;
            baseFragment2 = arrayList.get(arrayList.size() - 1);
        } else {
            baseFragment2 = null;
        }
        baseFragment3.setParentLayout(this);
        View view3 = baseFragment3.fragmentView;
        if (view3 == null) {
            view3 = baseFragment3.createView(this.parentActivity);
        } else {
            ViewGroup viewGroup = (ViewGroup) view3.getParent();
            if (viewGroup != null) {
                baseFragment.onRemoveFromParent();
                viewGroup.removeView(view3);
            }
        }
        this.containerViewBack.addView(view3);
        if (view2 != null) {
            this.containerViewBack.addView(view2);
            view2.measure(View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), Integer.MIN_VALUE));
            i = view.getMeasuredHeight() + AndroidUtilities.dp(24.0f);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
            layoutParams.width = -2;
            layoutParams.height = -2;
            layoutParams.topMargin = (getMeasuredHeight() - i) - AndroidUtilities.dp(6.0f);
            view2.setLayoutParams(layoutParams);
        } else {
            i = 0;
        }
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) view3.getLayoutParams();
        layoutParams2.width = -1;
        layoutParams2.height = -1;
        if (z7) {
            int previewHeight = baseFragment.getPreviewHeight();
            int i2 = Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0;
            if (previewHeight <= 0 || previewHeight >= getMeasuredHeight() - i2) {
                int dp = AndroidUtilities.dp(view2 != null ? 0.0f : 46.0f);
                layoutParams2.bottomMargin = dp;
                layoutParams2.topMargin = dp;
                layoutParams2.topMargin = dp + AndroidUtilities.statusBarHeight;
            } else {
                layoutParams2.height = previewHeight;
                layoutParams2.topMargin = i2 + (((getMeasuredHeight() - i2) - previewHeight) / 2);
            }
            if (view2 != null) {
                layoutParams2.bottomMargin += i + AndroidUtilities.dp(8.0f);
            }
            int dp2 = AndroidUtilities.dp(8.0f);
            layoutParams2.leftMargin = dp2;
            layoutParams2.rightMargin = dp2;
        } else {
            layoutParams2.leftMargin = 0;
            layoutParams2.rightMargin = 0;
            layoutParams2.bottomMargin = 0;
            layoutParams2.topMargin = 0;
        }
        view3.setLayoutParams(layoutParams2);
        ActionBar actionBar = baseFragment3.actionBar;
        if (actionBar != null && actionBar.shouldAddToContainer()) {
            if (this.removeActionBarExtraHeight) {
                baseFragment3.actionBar.setOccupyStatusBar(false);
            }
            ViewGroup viewGroup2 = (ViewGroup) baseFragment3.actionBar.getParent();
            if (viewGroup2 != null) {
                viewGroup2.removeView(baseFragment3.actionBar);
            }
            this.containerViewBack.addView(baseFragment3.actionBar);
            baseFragment3.actionBar.setTitleOverlayText(this.titleOverlayText, this.titleOverlayTextId, this.overlayAction);
        }
        this.fragmentsStack.add(baseFragment3);
        baseFragment.onResume();
        this.currentActionBar = baseFragment3.actionBar;
        if (!baseFragment3.hasOwnBackground && view3.getBackground() == null) {
            view3.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        }
        LayoutContainer layoutContainer = this.containerView;
        LayoutContainer layoutContainer2 = this.containerViewBack;
        this.containerView = layoutContainer2;
        this.containerViewBack = layoutContainer;
        layoutContainer2.setVisibility(0);
        setInnerTranslationX(0.0f);
        this.containerView.setTranslationY(0.0f);
        if (z7) {
            if (Build.VERSION.SDK_INT >= 21) {
                view3.setOutlineProvider(new ViewOutlineProvider(this) {
                    @TargetApi(21)
                    public void getOutline(View view, Outline outline) {
                        outline.setRoundRect(0, AndroidUtilities.statusBarHeight, view.getMeasuredWidth(), view.getMeasuredHeight(), (float) AndroidUtilities.dp(6.0f));
                    }
                });
                view3.setClipToOutline(true);
                view3.setElevation((float) AndroidUtilities.dp(4.0f));
            }
            if (this.previewBackgroundDrawable == null) {
                this.previewBackgroundDrawable = new ColorDrawable(NUM);
            }
            this.previewBackgroundDrawable.setAlpha(0);
            Theme.moveUpDrawable.setAlpha(0);
        }
        bringChildToFront(this.containerView);
        if (!z8) {
            presentFragmentInternalRemoveOld(z5, baseFragment2);
            View view4 = this.backgroundView;
            if (view4 != null) {
                view4.setVisibility(0);
            }
        }
        if (this.themeAnimatorSet != null) {
            this.presentingFragmentDescriptions = baseFragment.getThemeDescriptions();
        }
        if (z8 || z7) {
            if (!this.useAlphaAnimations || this.fragmentsStack.size() != 1) {
                this.transitionAnimationPreviewMode = z7;
                this.transitionAnimationStartTime = System.currentTimeMillis();
                this.transitionAnimationInProgress = true;
                this.onOpenAnimationEndRunnable = new ActionBarLayout$$ExternalSyntheticLambda4(this, z4, view, z, baseFragment2, baseFragment);
                final boolean z9 = !baseFragment.needDelayOpenAnimation();
                if (z9) {
                    if (baseFragment2 != null) {
                        baseFragment2.onTransitionAnimationStart(false, false);
                    }
                    baseFragment3.onTransitionAnimationStart(true, false);
                }
                this.delayedAnimationResumed = false;
                this.oldFragment = baseFragment2;
                this.newFragment = baseFragment3;
                AnimatorSet onCustomTransitionAnimation = !z7 ? baseFragment3.onCustomTransitionAnimation(true, new ActionBarLayout$$ExternalSyntheticLambda1(this)) : null;
                if (onCustomTransitionAnimation == null) {
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
                        if (baseFragment2 != null && !z7) {
                            baseFragment2.saveKeyboardPositionBeforeTransition();
                        }
                        final BaseFragment baseFragment4 = baseFragment2;
                        final BaseFragment baseFragment5 = baseFragment;
                        final boolean z10 = z4;
                        this.waitingForKeyboardCloseRunnable = new Runnable() {
                            public void run() {
                                if (ActionBarLayout.this.waitingForKeyboardCloseRunnable == this) {
                                    Runnable unused = ActionBarLayout.this.waitingForKeyboardCloseRunnable = null;
                                    if (z9) {
                                        BaseFragment baseFragment = baseFragment4;
                                        if (baseFragment != null) {
                                            baseFragment.onTransitionAnimationStart(false, false);
                                        }
                                        baseFragment5.onTransitionAnimationStart(true, false);
                                        ActionBarLayout.this.startLayoutAnimation(true, true, z10);
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
                        if (baseFragment.needDelayOpenAnimation()) {
                            this.delayedOpenAnimationRunnable = new Runnable() {
                                public void run() {
                                    if (ActionBarLayout.this.delayedOpenAnimationRunnable == this) {
                                        Runnable unused = ActionBarLayout.this.delayedOpenAnimationRunnable = null;
                                        BaseFragment baseFragment = baseFragment2;
                                        if (baseFragment != null) {
                                            baseFragment.onTransitionAnimationStart(false, false);
                                        }
                                        baseFragment3.onTransitionAnimationStart(true, false);
                                        ActionBarLayout.this.startLayoutAnimation(true, true, z7);
                                    }
                                }
                            };
                        }
                        AndroidUtilities.runOnUIThread(this.waitingForKeyboardCloseRunnable, SharedConfig.smoothKeyboard ? 250 : 200);
                    } else if (baseFragment.needDelayOpenAnimation()) {
                        AnonymousClass7 r0 = new Runnable() {
                            public void run() {
                                if (ActionBarLayout.this.delayedOpenAnimationRunnable == this) {
                                    Runnable unused = ActionBarLayout.this.delayedOpenAnimationRunnable = null;
                                    baseFragment3.onTransitionAnimationStart(true, false);
                                    ActionBarLayout.this.startLayoutAnimation(true, true, z7);
                                }
                            }
                        };
                        this.delayedOpenAnimationRunnable = r0;
                        AndroidUtilities.runOnUIThread(r0, 200);
                    } else {
                        startLayoutAnimation(true, true, z7);
                    }
                } else {
                    if ((!z7 && this.containerView.isKeyboardVisible) || (this.containerViewBack.isKeyboardVisible && baseFragment2 != null)) {
                        baseFragment2.saveKeyboardPositionBeforeTransition();
                    }
                    this.currentAnimation = onCustomTransitionAnimation;
                }
            } else {
                presentFragmentInternalRemoveOld(z5, baseFragment2);
                this.transitionAnimationStartTime = System.currentTimeMillis();
                this.transitionAnimationInProgress = true;
                this.onOpenAnimationEndRunnable = new ActionBarLayout$$ExternalSyntheticLambda5(baseFragment2, baseFragment3);
                ArrayList arrayList2 = new ArrayList();
                arrayList2.add(ObjectAnimator.ofFloat(this, View.ALPHA, new float[]{0.0f, 1.0f}));
                View view5 = this.backgroundView;
                if (view5 != null) {
                    view5.setVisibility(0);
                    arrayList2.add(ObjectAnimator.ofFloat(this.backgroundView, View.ALPHA, new float[]{0.0f, 1.0f}));
                }
                if (baseFragment2 != null) {
                    baseFragment2.onTransitionAnimationStart(false, false);
                }
                baseFragment3.onTransitionAnimationStart(true, false);
                AnimatorSet animatorSet = new AnimatorSet();
                this.currentAnimation = animatorSet;
                animatorSet.playTogether(arrayList2);
                this.currentAnimation.setInterpolator(this.accelerateDecelerateInterpolator);
                this.currentAnimation.setDuration(200);
                this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        ActionBarLayout.this.onAnimationEndCheck(false);
                    }
                });
                this.currentAnimation.start();
            }
            return true;
        }
        View view6 = this.backgroundView;
        if (view6 != null) {
            view6.setAlpha(1.0f);
            this.backgroundView.setVisibility(0);
        }
        if (baseFragment2 != null) {
            baseFragment2.onTransitionAnimationStart(false, false);
            baseFragment2.onTransitionAnimationEnd(false, false);
        }
        baseFragment3.onTransitionAnimationStart(true, false);
        baseFragment3.onTransitionAnimationEnd(true, false);
        baseFragment.onBecomeFullyVisible();
        return true;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$presentFragment$0(BaseFragment baseFragment, BaseFragment baseFragment2) {
        if (baseFragment != null) {
            baseFragment.onTransitionAnimationEnd(false, false);
        }
        baseFragment2.onTransitionAnimationEnd(true, false);
        baseFragment2.onBecomeFullyVisible();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$presentFragment$1(boolean z, View view, boolean z2, BaseFragment baseFragment, BaseFragment baseFragment2) {
        if (z) {
            this.inPreviewMode = true;
            this.previewMenu = view;
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

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$presentFragment$2() {
        onAnimationEndCheck(false);
    }

    public boolean addFragmentToStack(BaseFragment baseFragment) {
        return addFragmentToStack(baseFragment, -1);
    }

    public boolean addFragmentToStack(BaseFragment baseFragment, int i) {
        ViewGroup viewGroup;
        ViewGroup viewGroup2;
        ActionBarLayoutDelegate actionBarLayoutDelegate = this.delegate;
        if ((actionBarLayoutDelegate != null && !actionBarLayoutDelegate.needAddFragmentToStack(baseFragment, this)) || !baseFragment.onFragmentCreate()) {
            return false;
        }
        baseFragment.setParentLayout(this);
        if (i == -1) {
            if (!this.fragmentsStack.isEmpty()) {
                ArrayList<BaseFragment> arrayList = this.fragmentsStack;
                BaseFragment baseFragment2 = arrayList.get(arrayList.size() - 1);
                baseFragment2.onPause();
                ActionBar actionBar = baseFragment2.actionBar;
                if (!(actionBar == null || !actionBar.shouldAddToContainer() || (viewGroup2 = (ViewGroup) baseFragment2.actionBar.getParent()) == null)) {
                    viewGroup2.removeView(baseFragment2.actionBar);
                }
                View view = baseFragment2.fragmentView;
                if (!(view == null || (viewGroup = (ViewGroup) view.getParent()) == null)) {
                    baseFragment2.onRemoveFromParent();
                    viewGroup.removeView(baseFragment2.fragmentView);
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
        baseFragment.setParentLayout((ActionBarLayout) null);
        this.fragmentsStack.remove(baseFragment);
        this.containerViewBack.setVisibility(4);
        this.containerViewBack.setTranslationY(0.0f);
        bringChildToFront(this.containerView);
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x00d6  */
    /* JADX WARNING: Removed duplicated region for block: B:21:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void movePreviewFragment(float r21) {
        /*
            r20 = this;
            r0 = r20
            boolean r1 = r0.inPreviewMode
            if (r1 == 0) goto L_0x00de
            boolean r1 = r0.transitionAnimationPreviewMode
            if (r1 != 0) goto L_0x00de
            android.view.View r1 = r0.previewMenu
            if (r1 == 0) goto L_0x0010
            goto L_0x00de
        L_0x0010:
            org.telegram.ui.ActionBar.ActionBarLayout$LayoutContainer r1 = r0.containerView
            float r1 = r1.getTranslationY()
            r2 = r21
            float r2 = -r2
            r3 = 0
            int r4 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r4 <= 0) goto L_0x0021
        L_0x001e:
            r2 = 0
            goto L_0x00d2
        L_0x0021:
            r4 = 1114636288(0x42700000, float:60.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r4 = -r4
            float r4 = (float) r4
            int r4 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r4 >= 0) goto L_0x00d2
            r2 = 1
            r0.previewOpenAnimationInProgress = r2
            r4 = 0
            r0.inPreviewMode = r4
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r5 = r0.fragmentsStack
            int r6 = r5.size()
            r7 = 2
            int r6 = r6 - r7
            java.lang.Object r5 = r5.get(r6)
            org.telegram.ui.ActionBar.BaseFragment r5 = (org.telegram.ui.ActionBar.BaseFragment) r5
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r6 = r0.fragmentsStack
            int r8 = r6.size()
            int r8 = r8 - r2
            java.lang.Object r6 = r6.get(r8)
            org.telegram.ui.ActionBar.BaseFragment r6 = (org.telegram.ui.ActionBar.BaseFragment) r6
            int r8 = android.os.Build.VERSION.SDK_INT
            r9 = 21
            if (r8 < r9) goto L_0x005f
            android.view.View r8 = r6.fragmentView
            r9 = 0
            r8.setOutlineProvider(r9)
            android.view.View r8 = r6.fragmentView
            r8.setClipToOutline(r4)
        L_0x005f:
            android.view.View r8 = r6.fragmentView
            android.view.ViewGroup$LayoutParams r8 = r8.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r8 = (android.widget.FrameLayout.LayoutParams) r8
            r8.leftMargin = r4
            r8.rightMargin = r4
            r8.bottomMargin = r4
            r8.topMargin = r4
            r9 = -1
            r8.height = r9
            android.view.View r9 = r6.fragmentView
            r9.setLayoutParams(r8)
            r0.presentFragmentInternalRemoveOld(r4, r5)
            android.animation.AnimatorSet r5 = new android.animation.AnimatorSet
            r5.<init>()
            android.animation.Animator[] r7 = new android.animation.Animator[r7]
            android.view.View r8 = r6.fragmentView
            android.util.Property r9 = android.view.View.SCALE_X
            r10 = 3
            float[] r11 = new float[r10]
            r11 = {NUM, NUM, NUM} // fill-array
            android.animation.ObjectAnimator r8 = android.animation.ObjectAnimator.ofFloat(r8, r9, r11)
            r7[r4] = r8
            android.view.View r8 = r6.fragmentView
            android.util.Property r9 = android.view.View.SCALE_Y
            float[] r11 = new float[r10]
            r11 = {NUM, NUM, NUM} // fill-array
            android.animation.ObjectAnimator r8 = android.animation.ObjectAnimator.ofFloat(r8, r9, r11)
            r7[r2] = r8
            r5.playTogether(r7)
            r7 = 200(0xc8, double:9.9E-322)
            r5.setDuration(r7)
            org.telegram.ui.Components.CubicBezierInterpolator r2 = new org.telegram.ui.Components.CubicBezierInterpolator
            r12 = 4601237667291888353(0x3fdae147ae147ae1, double:0.42)
            r14 = 0
            r16 = 4603399395113026191(0x3fe28f5CLASSNAMEf5CLASSNAMEf, double:0.58)
            r18 = 4607182418800017408(0x3ffNUM, double:1.0)
            r11 = r2
            r11.<init>((double) r12, (double) r14, (double) r16, (double) r18)
            r5.setInterpolator(r2)
            org.telegram.ui.ActionBar.ActionBarLayout$8 r2 = new org.telegram.ui.ActionBar.ActionBarLayout$8
            r2.<init>(r6)
            r5.addListener(r2)
            r5.start()
            r0.performHapticFeedback(r10)
            r6.setInPreviewMode(r4)
            goto L_0x001e
        L_0x00d2:
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 == 0) goto L_0x00de
            org.telegram.ui.ActionBar.ActionBarLayout$LayoutContainer r1 = r0.containerView
            r1.setTranslationY(r2)
            r20.invalidate()
        L_0x00de:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.ActionBarLayout.movePreviewFragment(float):void");
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

    public void closeLastFragment(boolean z) {
        BaseFragment baseFragment;
        ActionBarLayoutDelegate actionBarLayoutDelegate = this.delegate;
        if ((actionBarLayoutDelegate == null || actionBarLayoutDelegate.needCloseLastFragment(this)) && !checkTransitionAnimation() && !this.fragmentsStack.isEmpty()) {
            if (this.parentActivity.getCurrentFocus() != null) {
                AndroidUtilities.hideKeyboard(this.parentActivity.getCurrentFocus());
            }
            setInnerTranslationX(0.0f);
            boolean z2 = this.inPreviewMode || this.transitionAnimationPreviewMode || (z && MessagesController.getGlobalMainSettings().getBoolean("view_animations", true));
            ArrayList<BaseFragment> arrayList = this.fragmentsStack;
            BaseFragment baseFragment2 = arrayList.get(arrayList.size() - 1);
            AnimatorSet animatorSet = null;
            if (this.fragmentsStack.size() > 1) {
                ArrayList<BaseFragment> arrayList2 = this.fragmentsStack;
                baseFragment = arrayList2.get(arrayList2.size() - 2);
            } else {
                baseFragment = null;
            }
            if (baseFragment != null) {
                LayoutContainer layoutContainer = this.containerView;
                this.containerView = this.containerViewBack;
                this.containerViewBack = layoutContainer;
                baseFragment.setParentLayout(this);
                View view = baseFragment.fragmentView;
                if (view == null) {
                    view = baseFragment.createView(this.parentActivity);
                }
                if (!this.inPreviewMode) {
                    this.containerView.setVisibility(0);
                    ViewGroup viewGroup = (ViewGroup) view.getParent();
                    if (viewGroup != null) {
                        baseFragment.onRemoveFromParent();
                        try {
                            viewGroup.removeView(view);
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                    }
                    this.containerView.addView(view);
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
                    layoutParams.width = -1;
                    layoutParams.height = -1;
                    layoutParams.leftMargin = 0;
                    layoutParams.rightMargin = 0;
                    layoutParams.bottomMargin = 0;
                    layoutParams.topMargin = 0;
                    view.setLayoutParams(layoutParams);
                    ActionBar actionBar = baseFragment.actionBar;
                    if (actionBar != null && actionBar.shouldAddToContainer()) {
                        if (this.removeActionBarExtraHeight) {
                            baseFragment.actionBar.setOccupyStatusBar(false);
                        }
                        ViewGroup viewGroup2 = (ViewGroup) baseFragment.actionBar.getParent();
                        if (viewGroup2 != null) {
                            viewGroup2.removeView(baseFragment.actionBar);
                        }
                        this.containerView.addView(baseFragment.actionBar);
                        baseFragment.actionBar.setTitleOverlayText(this.titleOverlayText, this.titleOverlayTextId, this.overlayAction);
                    }
                }
                this.newFragment = baseFragment;
                this.oldFragment = baseFragment2;
                baseFragment.onTransitionAnimationStart(true, true);
                baseFragment2.onTransitionAnimationStart(false, true);
                baseFragment.onResume();
                if (this.themeAnimatorSet != null) {
                    this.presentingFragmentDescriptions = baseFragment.getThemeDescriptions();
                }
                this.currentActionBar = baseFragment.actionBar;
                if (!baseFragment.hasOwnBackground && view.getBackground() == null) {
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                }
                if (!z2) {
                    closeLastFragmentInternalRemoveOld(baseFragment2);
                }
                if (z2) {
                    this.transitionAnimationStartTime = System.currentTimeMillis();
                    this.transitionAnimationInProgress = true;
                    this.onCloseAnimationEndRunnable = new ActionBarLayout$$ExternalSyntheticLambda3(this, baseFragment2, baseFragment);
                    if (!this.inPreviewMode && !this.transitionAnimationPreviewMode) {
                        animatorSet = baseFragment2.onCustomTransitionAnimation(false, new ActionBarLayout$$ExternalSyntheticLambda0(this));
                    }
                    if (animatorSet != null) {
                        this.currentAnimation = animatorSet;
                        if (Bulletin.getVisibleBulletin() != null && Bulletin.getVisibleBulletin().isShowing()) {
                            Bulletin.getVisibleBulletin().hide();
                        }
                    } else if (this.inPreviewMode || (!this.containerView.isKeyboardVisible && !this.containerViewBack.isKeyboardVisible)) {
                        startLayoutAnimation(false, true, this.inPreviewMode || this.transitionAnimationPreviewMode);
                    } else {
                        AnonymousClass9 r13 = new Runnable() {
                            public void run() {
                                if (ActionBarLayout.this.waitingForKeyboardCloseRunnable == this) {
                                    Runnable unused = ActionBarLayout.this.waitingForKeyboardCloseRunnable = null;
                                    ActionBarLayout.this.startLayoutAnimation(false, true, false);
                                }
                            }
                        };
                        this.waitingForKeyboardCloseRunnable = r13;
                        AndroidUtilities.runOnUIThread(r13, 200);
                    }
                } else {
                    baseFragment2.onTransitionAnimationEnd(false, true);
                    baseFragment.onTransitionAnimationEnd(true, true);
                    baseFragment.onBecomeFullyVisible();
                }
            } else if (this.useAlphaAnimations) {
                this.transitionAnimationStartTime = System.currentTimeMillis();
                this.transitionAnimationInProgress = true;
                this.onCloseAnimationEndRunnable = new ActionBarLayout$$ExternalSyntheticLambda2(this, baseFragment2);
                ArrayList arrayList3 = new ArrayList();
                arrayList3.add(ObjectAnimator.ofFloat(this, View.ALPHA, new float[]{1.0f, 0.0f}));
                View view2 = this.backgroundView;
                if (view2 != null) {
                    arrayList3.add(ObjectAnimator.ofFloat(view2, View.ALPHA, new float[]{1.0f, 0.0f}));
                }
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.currentAnimation = animatorSet2;
                animatorSet2.playTogether(arrayList3);
                this.currentAnimation.setInterpolator(this.accelerateDecelerateInterpolator);
                this.currentAnimation.setDuration(200);
                this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationStart(Animator animator) {
                        long unused = ActionBarLayout.this.transitionAnimationStartTime = System.currentTimeMillis();
                    }

                    public void onAnimationEnd(Animator animator) {
                        ActionBarLayout.this.onAnimationEndCheck(false);
                    }
                });
                this.currentAnimation.start();
            } else {
                removeFragmentFromStackInternal(baseFragment2);
                setVisibility(8);
                View view3 = this.backgroundView;
                if (view3 != null) {
                    view3.setVisibility(8);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$closeLastFragment$3(BaseFragment baseFragment, BaseFragment baseFragment2) {
        ViewGroup viewGroup;
        View view = this.previewMenu;
        if (!(view == null || (viewGroup = (ViewGroup) view.getParent()) == null)) {
            viewGroup.removeView(this.previewMenu);
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
        closeLastFragmentInternalRemoveOld(baseFragment);
        baseFragment.onTransitionAnimationEnd(false, true);
        baseFragment2.onTransitionAnimationEnd(true, true);
        baseFragment2.onBecomeFullyVisible();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$closeLastFragment$4() {
        onAnimationEndCheck(false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$closeLastFragment$5(BaseFragment baseFragment) {
        removeFragmentFromStackInternal(baseFragment);
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
        ViewGroup viewGroup;
        ViewGroup viewGroup2;
        if (!this.fragmentsStack.isEmpty()) {
            for (int i2 = 0; i2 < i; i2++) {
                BaseFragment baseFragment = this.fragmentsStack.get(i2);
                ActionBar actionBar = baseFragment.actionBar;
                if (!(actionBar == null || !actionBar.shouldAddToContainer() || (viewGroup2 = (ViewGroup) baseFragment.actionBar.getParent()) == null)) {
                    viewGroup2.removeView(baseFragment.actionBar);
                }
                View view = baseFragment.fragmentView;
                if (!(view == null || (viewGroup = (ViewGroup) view.getParent()) == null)) {
                    baseFragment.onPause();
                    baseFragment.onRemoveFromParent();
                    viewGroup.removeView(baseFragment.fragmentView);
                }
            }
            BaseFragment baseFragment2 = this.fragmentsStack.get(i);
            baseFragment2.setParentLayout(this);
            View view2 = baseFragment2.fragmentView;
            if (view2 == null) {
                view2 = baseFragment2.createView(this.parentActivity);
            } else {
                ViewGroup viewGroup3 = (ViewGroup) view2.getParent();
                if (viewGroup3 != null) {
                    baseFragment2.onRemoveFromParent();
                    viewGroup3.removeView(view2);
                }
            }
            this.containerView.addView(view2, LayoutHelper.createFrame(-1, -1.0f));
            ActionBar actionBar2 = baseFragment2.actionBar;
            if (actionBar2 != null && actionBar2.shouldAddToContainer()) {
                if (this.removeActionBarExtraHeight) {
                    baseFragment2.actionBar.setOccupyStatusBar(false);
                }
                ViewGroup viewGroup4 = (ViewGroup) baseFragment2.actionBar.getParent();
                if (viewGroup4 != null) {
                    viewGroup4.removeView(baseFragment2.actionBar);
                }
                this.containerView.addView(baseFragment2.actionBar);
                baseFragment2.actionBar.setTitleOverlayText(this.titleOverlayText, this.titleOverlayTextId, this.overlayAction);
            }
            baseFragment2.onResume();
            this.currentActionBar = baseFragment2.actionBar;
            if (!baseFragment2.hasOwnBackground && view2.getBackground() == null) {
                view2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            }
        }
    }

    public void showLastFragment() {
        if (!this.fragmentsStack.isEmpty()) {
            showFragment(this.fragmentsStack.size() - 1);
        }
    }

    private void removeFragmentFromStackInternal(BaseFragment baseFragment) {
        baseFragment.onPause();
        baseFragment.onFragmentDestroy();
        baseFragment.setParentLayout((ActionBarLayout) null);
        this.fragmentsStack.remove(baseFragment);
    }

    public void removeFragmentFromStack(int i) {
        if (i < this.fragmentsStack.size()) {
            removeFragmentFromStackInternal(this.fragmentsStack.get(i));
        }
    }

    public void removeFragmentFromStack(BaseFragment baseFragment) {
        if (!this.useAlphaAnimations || this.fragmentsStack.size() != 1 || !AndroidUtilities.isTablet()) {
            if (this.delegate != null && this.fragmentsStack.size() == 1 && AndroidUtilities.isTablet()) {
                this.delegate.needCloseLastFragment(this);
            }
            removeFragmentFromStackInternal(baseFragment);
            return;
        }
        closeLastFragment(true);
    }

    public void removeAllFragments() {
        while (this.fragmentsStack.size() > 0) {
            removeFragmentFromStackInternal(this.fragmentsStack.get(0));
        }
    }

    @Keep
    public void setThemeAnimationValue(float f) {
        float f2 = f;
        this.themeAnimationValue = f2;
        int size = this.themeAnimatorDescriptions.size();
        for (int i = 0; i < size; i++) {
            ArrayList arrayList = this.themeAnimatorDescriptions.get(i);
            int[] iArr = this.animateStartColors.get(i);
            int[] iArr2 = this.animateEndColors.get(i);
            int size2 = arrayList.size();
            int i2 = 0;
            while (i2 < size2) {
                int red = Color.red(iArr2[i2]);
                int green = Color.green(iArr2[i2]);
                int blue = Color.blue(iArr2[i2]);
                int alpha = Color.alpha(iArr2[i2]);
                int red2 = Color.red(iArr[i2]);
                int green2 = Color.green(iArr[i2]);
                int blue2 = Color.blue(iArr[i2]);
                int i3 = size;
                int alpha2 = Color.alpha(iArr[i2]);
                int argb = Color.argb(Math.min(255, (int) (((float) alpha2) + (((float) (alpha - alpha2)) * f2))), Math.min(255, (int) (((float) red2) + (((float) (red - red2)) * f2))), Math.min(255, (int) (((float) green2) + (((float) (green - green2)) * f2))), Math.min(255, (int) (((float) blue2) + (((float) (blue - blue2)) * f2))));
                ThemeDescription themeDescription = (ThemeDescription) arrayList.get(i2);
                themeDescription.setAnimatedColor(argb);
                themeDescription.setColor(argb, false, false);
                i2++;
                iArr = iArr;
                size = i3;
            }
            int i4 = size;
        }
        int size3 = this.themeAnimatorDelegate.size();
        for (int i5 = 0; i5 < size3; i5++) {
            ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = this.themeAnimatorDelegate.get(i5);
            if (themeDescriptionDelegate != null) {
                themeDescriptionDelegate.didSetColor();
                themeDescriptionDelegate.onAnimationProgress(f2);
            }
        }
        ArrayList<ThemeDescription> arrayList2 = this.presentingFragmentDescriptions;
        if (arrayList2 != null) {
            int size4 = arrayList2.size();
            for (int i6 = 0; i6 < size4; i6++) {
                ThemeDescription themeDescription2 = this.presentingFragmentDescriptions.get(i6);
                themeDescription2.setColor(Theme.getColor(themeDescription2.getCurrentKey()), false, false);
            }
        }
        ThemeAnimationSettings.onAnimationProgress onanimationprogress = this.animationProgressListener;
        if (onanimationprogress != null) {
            onanimationprogress.setProgress(f2);
        }
    }

    @Keep
    public float getThemeAnimationValue() {
        return this.themeAnimationValue;
    }

    private void addStartDescriptions(ArrayList<ThemeDescription> arrayList) {
        if (arrayList != null) {
            this.themeAnimatorDescriptions.add(arrayList);
            int[] iArr = new int[arrayList.size()];
            this.animateStartColors.add(iArr);
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                ThemeDescription themeDescription = arrayList.get(i);
                iArr[i] = themeDescription.getSetColor();
                ThemeDescription.ThemeDescriptionDelegate delegateDisabled = themeDescription.setDelegateDisabled();
                if (delegateDisabled != null && !this.themeAnimatorDelegate.contains(delegateDisabled)) {
                    this.themeAnimatorDelegate.add(delegateDisabled);
                }
            }
        }
    }

    private void addEndDescriptions(ArrayList<ThemeDescription> arrayList) {
        if (arrayList != null) {
            int[] iArr = new int[arrayList.size()];
            this.animateEndColors.add(iArr);
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                iArr[i] = arrayList.get(i).getSetColor();
            }
        }
    }

    public void animateThemedValues(Theme.ThemeInfo themeInfo, int i, boolean z, boolean z2) {
        animateThemedValues(new ThemeAnimationSettings(themeInfo, i, z, z2));
    }

    public void animateThemedValues(final ThemeAnimationSettings themeAnimationSettings) {
        BaseFragment baseFragment;
        Theme.ThemeInfo themeInfo;
        if (this.transitionAnimationInProgress || this.startedTracking) {
            this.animateThemeAfterAnimation = true;
            this.animateSetThemeAfterAnimation = themeAnimationSettings.theme;
            this.animateSetThemeNightAfterAnimation = themeAnimationSettings.nightTheme;
            this.animateSetThemeAccentIdAfterAnimation = themeAnimationSettings.accentId;
            return;
        }
        AnimatorSet animatorSet = this.themeAnimatorSet;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.themeAnimatorSet = null;
        }
        int size = themeAnimationSettings.onlyTopFragment ? 1 : this.fragmentsStack.size();
        boolean z = false;
        for (int i = 0; i < size; i++) {
            if (i == 0) {
                baseFragment = getLastFragment();
            } else {
                if ((this.inPreviewMode || this.transitionAnimationPreviewMode) && this.fragmentsStack.size() > 1) {
                    ArrayList<BaseFragment> arrayList = this.fragmentsStack;
                    baseFragment = arrayList.get(arrayList.size() - 2);
                }
            }
            if (baseFragment != null) {
                if (themeAnimationSettings.resourcesProvider != null) {
                    if (this.messageDrawableOutStart == null) {
                        Theme.MessageDrawable messageDrawable = new Theme.MessageDrawable(0, true, false, this.startColorsProvider);
                        this.messageDrawableOutStart = messageDrawable;
                        messageDrawable.isCrossfadeBackground = true;
                        Theme.MessageDrawable messageDrawable2 = new Theme.MessageDrawable(1, true, false, this.startColorsProvider);
                        this.messageDrawableOutMediaStart = messageDrawable2;
                        messageDrawable2.isCrossfadeBackground = true;
                    }
                    this.startColorsProvider.saveColors(themeAnimationSettings.resourcesProvider);
                }
                ArrayList<ThemeDescription> themeDescriptions = baseFragment.getThemeDescriptions();
                addStartDescriptions(themeDescriptions);
                Dialog dialog = baseFragment.visibleDialog;
                if (dialog instanceof BottomSheet) {
                    addStartDescriptions(((BottomSheet) dialog).getThemeDescriptions());
                } else if (dialog instanceof AlertDialog) {
                    addStartDescriptions(((AlertDialog) dialog).getThemeDescriptions());
                }
                if (i == 0) {
                    if (themeAnimationSettings.applyTheme) {
                        int i2 = themeAnimationSettings.accentId;
                        if (!(i2 == -1 || (themeInfo = themeAnimationSettings.theme) == null)) {
                            themeInfo.setCurrentAccentId(i2);
                            Theme.saveThemeAccents(themeAnimationSettings.theme, true, false, true, false);
                        }
                        Theme.applyTheme(themeAnimationSettings.theme, themeAnimationSettings.nightTheme);
                    }
                    Runnable runnable = themeAnimationSettings.afterStartDescriptionsAddedRunnable;
                    if (runnable != null) {
                        runnable.run();
                    }
                }
                addEndDescriptions(themeDescriptions);
                Dialog dialog2 = baseFragment.visibleDialog;
                if (dialog2 instanceof BottomSheet) {
                    addEndDescriptions(((BottomSheet) dialog2).getThemeDescriptions());
                } else if (dialog2 instanceof AlertDialog) {
                    addEndDescriptions(((AlertDialog) dialog2).getThemeDescriptions());
                }
                z = true;
            }
        }
        if (z) {
            if (!themeAnimationSettings.onlyTopFragment) {
                int size2 = this.fragmentsStack.size() - ((this.inPreviewMode || this.transitionAnimationPreviewMode) ? 2 : 1);
                for (int i3 = 0; i3 < size2; i3++) {
                    BaseFragment baseFragment2 = this.fragmentsStack.get(i3);
                    baseFragment2.clearViews();
                    baseFragment2.setParentLayout(this);
                }
            }
            if (themeAnimationSettings.instant) {
                setThemeAnimationValue(1.0f);
                this.themeAnimatorDescriptions.clear();
                this.animateStartColors.clear();
                this.animateEndColors.clear();
                this.themeAnimatorDelegate.clear();
                this.presentingFragmentDescriptions = null;
                Runnable runnable2 = themeAnimationSettings.afterAnimationRunnable;
                if (runnable2 != null) {
                    runnable2.run();
                    return;
                }
                return;
            }
            Theme.setAnimatingColor(true);
            Runnable runnable3 = themeAnimationSettings.beforeAnimationRunnable;
            if (runnable3 != null) {
                runnable3.run();
            }
            ThemeAnimationSettings.onAnimationProgress onanimationprogress = themeAnimationSettings.animationProgress;
            this.animationProgressListener = onanimationprogress;
            if (onanimationprogress != null) {
                onanimationprogress.setProgress(0.0f);
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.themeAnimatorSet = animatorSet2;
            animatorSet2.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (animator.equals(ActionBarLayout.this.themeAnimatorSet)) {
                        ActionBarLayout.this.themeAnimatorDescriptions.clear();
                        ActionBarLayout.this.animateStartColors.clear();
                        ActionBarLayout.this.animateEndColors.clear();
                        ActionBarLayout.this.themeAnimatorDelegate.clear();
                        Theme.setAnimatingColor(false);
                        ArrayList unused = ActionBarLayout.this.presentingFragmentDescriptions = null;
                        AnimatorSet unused2 = ActionBarLayout.this.themeAnimatorSet = null;
                        Runnable runnable = themeAnimationSettings.afterAnimationRunnable;
                        if (runnable != null) {
                            runnable.run();
                        }
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    if (animator.equals(ActionBarLayout.this.themeAnimatorSet)) {
                        ActionBarLayout.this.themeAnimatorDescriptions.clear();
                        ActionBarLayout.this.animateStartColors.clear();
                        ActionBarLayout.this.animateEndColors.clear();
                        ActionBarLayout.this.themeAnimatorDelegate.clear();
                        Theme.setAnimatingColor(false);
                        ArrayList unused = ActionBarLayout.this.presentingFragmentDescriptions = null;
                        AnimatorSet unused2 = ActionBarLayout.this.themeAnimatorSet = null;
                        Runnable runnable = themeAnimationSettings.afterAnimationRunnable;
                        if (runnable != null) {
                            runnable.run();
                        }
                    }
                }
            });
            this.themeAnimatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, "themeAnimationValue", new float[]{0.0f, 1.0f})});
            this.themeAnimatorSet.setDuration(themeAnimationSettings.duration);
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
            this.fragmentsStack.get(i).clearViews();
            this.fragmentsStack.get(i).setParentLayout(this);
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
        ActionBar actionBar;
        if (i == 82 && !checkTransitionAnimation() && !this.startedTracking && (actionBar = this.currentActionBar) != null) {
            actionBar.onMenuButtonPressed();
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
        Runnable runnable;
        if (this.transitionAnimationInProgress && (runnable = this.onCloseAnimationEndRunnable) != null) {
            this.transitionAnimationInProgress = false;
            this.transitionAnimationPreviewMode = false;
            this.transitionAnimationStartTime = 0;
            this.newFragment = null;
            this.oldFragment = null;
            this.onCloseAnimationEndRunnable = null;
            runnable.run();
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
        Runnable runnable;
        if (this.transitionAnimationInProgress && (runnable = this.onOpenAnimationEndRunnable) != null) {
            this.transitionAnimationInProgress = false;
            this.transitionAnimationPreviewMode = false;
            this.transitionAnimationStartTime = 0;
            this.newFragment = null;
            this.oldFragment = null;
            this.onOpenAnimationEndRunnable = null;
            runnable.run();
            checkNeedRebuild();
        }
    }

    public void startActivityForResult(Intent intent, int i) {
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

    public void setDrawerLayoutContainer(DrawerLayoutContainer drawerLayoutContainer2) {
        this.drawerLayoutContainer = drawerLayoutContainer2;
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
            ActionBar actionBar = this.fragmentsStack.get(i2).actionBar;
            if (actionBar != null) {
                actionBar.setTitleOverlayText(this.titleOverlayText, this.titleOverlayTextId, runnable);
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

    public void setFragmentPanTranslationOffset(int i) {
        LayoutContainer layoutContainer = this.containerView;
        if (layoutContainer != null) {
            layoutContainer.setFragmentPanTranslationOffset(i);
        }
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

        private StartColorsProvider(ActionBarLayout actionBarLayout) {
            this.colors = new HashMap<>();
            this.keysToSave = new String[]{"chat_outBubble", "chat_outBubbleGradient", "chat_outBubbleGradient2", "chat_outBubbleGradient3", "chat_outBubbleGradientAnimated", "chat_outBubbleShadow"};
        }

        public Integer getColor(String str) {
            return this.colors.get(str);
        }

        public Integer getCurrentColor(String str) {
            return this.colors.get(str);
        }

        public void saveColors(Theme.ResourcesProvider resourcesProvider) {
            this.colors.clear();
            for (String str : this.keysToSave) {
                this.colors.put(str, resourcesProvider.getCurrentColor(str));
            }
        }
    }
}
