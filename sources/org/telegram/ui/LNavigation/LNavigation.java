package org.telegram.ui.LNavigation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
import androidx.core.view.GestureDetectorCompat;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatValueHolder;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.viewpager.widget.ViewPager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.DrawerLayoutContainer;
import org.telegram.ui.ActionBar.INavigationLayout;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Components.BackButtonMenu;
import org.telegram.ui.Components.FloatingDebug.FloatingDebugController;
import org.telegram.ui.Components.FloatingDebug.FloatingDebugProvider;
import org.telegram.ui.Components.GroupCallPip;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.SeekBarView;
import org.telegram.ui.LNavigation.LNavigation;
/* loaded from: classes3.dex */
public class LNavigation extends FrameLayout implements INavigationLayout, FloatingDebugProvider {
    private static float SPRING_DAMPING_RATIO = 1.0f;
    private static float SPRING_STIFFNESS = 1000.0f;
    private boolean allowToPressByHover;
    private ArrayList<int[]> animateEndColors;
    private ArrayList<int[]> animateStartColors;
    private INavigationLayout.ThemeAnimationSettings.onAnimationProgress animationProgressListener;
    private View backgroundView;
    private Paint blurPaint;
    private Bitmap blurredBackFragmentForPreview;
    private SpringAnimation currentSpringAnimation;
    private AnimatorSet customAnimation;
    private Runnable delayedPresentAnimation;
    private INavigationLayout.INavigationLayoutDelegate delegate;
    private Paint dimmPaint;
    private DrawerLayoutContainer drawerLayoutContainer;
    private List<BaseFragment> fragmentStack;
    private int fromBackgroundColor;
    private GestureDetectorCompat gestureDetector;
    private Drawable headerShadowDrawable;
    private boolean highlightActionButtons;
    private Rect ignoreRect;
    private boolean isFirstHoverAllowed;
    private boolean isInActionMode;
    private boolean isInBubbleMode;
    private boolean isSwipeDisallowed;
    private boolean isSwipeInProgress;
    private Drawable layerShadowDrawable;
    private Theme.MessageDrawable messageDrawableOutMediaStart;
    private Theme.MessageDrawable messageDrawableOutStart;
    private Runnable onFragmentStackChangedListener;
    private CheckBoxCell openChatCheckbox;
    private FrameLayout overlayLayout;
    private Path path;
    private ArrayList<ThemeDescription> presentingFragmentDescriptions;
    private float previewExpandProgress;
    private Rect previewFragmentRect;
    private Bitmap previewFragmentSnapshot;
    private ActionBarPopupWindow.ActionBarPopupWindowLayout previewMenu;
    private Runnable previewOpenCallback;
    private List<BackButtonMenu.PulledDialog> pulledDialogs;
    private boolean removeActionBarExtraHeight;
    private INavigationLayout.StartColorsProvider startColorsProvider;
    private float startScroll;
    private LinearLayout stiffnessControl;
    private float swipeProgress;
    private float themeAnimationValue;
    private ValueAnimator themeAnimator;
    private ArrayList<ThemeDescription.ThemeDescriptionDelegate> themeAnimatorDelegate;
    private ArrayList<ArrayList<ThemeDescription>> themeAnimatorDescriptions;
    private Runnable titleOverlayAction;
    private String titleOverlayTitle;
    private int titleOverlayTitleId;
    private View touchCapturedView;
    private List<BaseFragment> unmodifiableFragmentStack;
    private boolean useAlphaAnimations;
    private boolean useBackground;
    private boolean wasPortrait;

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public /* synthetic */ boolean addFragmentToStack(BaseFragment baseFragment) {
        boolean addFragmentToStack;
        addFragmentToStack = addFragmentToStack(baseFragment, -1);
        return addFragmentToStack;
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public /* synthetic */ void animateThemedValues(Theme.ThemeInfo themeInfo, int i, boolean z, boolean z2) {
        animateThemedValues(new INavigationLayout.ThemeAnimationSettings(themeInfo, i, z, z2), null);
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public /* synthetic */ void animateThemedValues(Theme.ThemeInfo themeInfo, int i, boolean z, boolean z2, Runnable runnable) {
        animateThemedValues(new INavigationLayout.ThemeAnimationSettings(themeInfo, i, z, z2), runnable);
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public /* synthetic */ void bringToFront(int i) {
        INavigationLayout.CC.$default$bringToFront(this, i);
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public /* synthetic */ void closeLastFragment() {
        closeLastFragment(true);
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public /* synthetic */ void closeLastFragment(boolean z) {
        closeLastFragment(z, false);
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public /* synthetic */ void dismissDialogs() {
        INavigationLayout.CC.$default$dismissDialogs(this);
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public /* synthetic */ void drawHeaderShadow(Canvas canvas, int i) {
        drawHeaderShadow(canvas, 255, i);
    }

    public /* bridge */ /* synthetic */ BaseFragment getBackgroundFragment() {
        return INavigationLayout.CC.$default$getBackgroundFragment(this);
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public /* bridge */ /* synthetic */ BaseFragment getLastFragment() {
        return INavigationLayout.CC.$default$getLastFragment(this);
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public /* bridge */ /* synthetic */ Activity getParentActivity() {
        return INavigationLayout.CC.$default$getParentActivity(this);
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public /* bridge */ /* synthetic */ ViewGroup getView() {
        return INavigationLayout.CC.$default$getView(this);
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public boolean hasIntegratedBlurInPreview() {
        return true;
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public /* synthetic */ boolean presentFragment(BaseFragment baseFragment) {
        boolean presentFragment;
        presentFragment = presentFragment(new INavigationLayout.NavigationParams(baseFragment));
        return presentFragment;
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public /* synthetic */ boolean presentFragment(BaseFragment baseFragment, boolean z) {
        boolean presentFragment;
        presentFragment = presentFragment(new INavigationLayout.NavigationParams(baseFragment).setRemoveLast(z));
        return presentFragment;
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public /* synthetic */ boolean presentFragment(BaseFragment baseFragment, boolean z, boolean z2, boolean z3, boolean z4) {
        boolean presentFragment;
        presentFragment = presentFragment(new INavigationLayout.NavigationParams(baseFragment).setRemoveLast(z).setNoAnimation(z2).setCheckPresentFromDelegate(z3).setPreview(z4));
        return presentFragment;
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public /* synthetic */ boolean presentFragment(BaseFragment baseFragment, boolean z, boolean z2, boolean z3, boolean z4, ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout) {
        boolean presentFragment;
        presentFragment = presentFragment(new INavigationLayout.NavigationParams(baseFragment).setRemoveLast(z).setNoAnimation(z2).setCheckPresentFromDelegate(z3).setPreview(z4).setMenuView(actionBarPopupWindowLayout));
        return presentFragment;
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public /* synthetic */ boolean presentFragmentAsPreview(BaseFragment baseFragment) {
        boolean presentFragment;
        presentFragment = presentFragment(new INavigationLayout.NavigationParams(baseFragment).setPreview(true));
        return presentFragment;
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public /* synthetic */ boolean presentFragmentAsPreviewWithMenu(BaseFragment baseFragment, ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout) {
        boolean presentFragment;
        presentFragment = presentFragment(new INavigationLayout.NavigationParams(baseFragment).setPreview(true).setMenuView(actionBarPopupWindowLayout));
        return presentFragment;
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public /* synthetic */ void rebuildAllFragmentViews(boolean z, boolean z2) {
        INavigationLayout.CC.$default$rebuildAllFragmentViews(this, z, z2);
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public /* synthetic */ void rebuildLogout() {
        INavigationLayout.CC.$default$rebuildLogout(this);
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public /* synthetic */ void removeAllFragments() {
        INavigationLayout.CC.$default$removeAllFragments(this);
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public /* synthetic */ void removeFragmentFromStack(int i) {
        INavigationLayout.CC.$default$removeFragmentFromStack(this, i);
    }

    public LNavigation(Context context) {
        this(context, null);
    }

    public LNavigation(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.pulledDialogs = new ArrayList();
        this.ignoreRect = new Rect();
        this.path = new Path();
        this.dimmPaint = new Paint(1);
        ArrayList arrayList = new ArrayList();
        this.fragmentStack = arrayList;
        this.unmodifiableFragmentStack = Collections.unmodifiableList(arrayList);
        this.highlightActionButtons = false;
        this.previewFragmentRect = new Rect();
        this.blurPaint = new Paint(5);
        this.useBackground = true;
        this.startColorsProvider = new INavigationLayout.StartColorsProvider();
        this.themeAnimatorDelegate = new ArrayList<>();
        this.themeAnimatorDescriptions = new ArrayList<>();
        this.animateStartColors = new ArrayList<>();
        this.animateEndColors = new ArrayList<>();
        FrameLayout frameLayout = new FrameLayout(context);
        this.overlayLayout = frameLayout;
        addView(frameLayout);
        this.headerShadowDrawable = getResources().getDrawable(R.drawable.header_shadow).mutate();
        this.layerShadowDrawable = getResources().getDrawable(R.drawable.layer_shadow).mutate();
        this.dimmPaint.setColor(NUM);
        setWillNotDraw(false);
        setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        final int scaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        GestureDetectorCompat gestureDetectorCompat = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() { // from class: org.telegram.ui.LNavigation.LNavigation.1
            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onDown(MotionEvent motionEvent) {
                return true;
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
                if (LNavigation.this.highlightActionButtons && !LNavigation.this.allowToPressByHover && LNavigation.this.isFirstHoverAllowed && LNavigation.this.isInPreviewMode() && ((Math.abs(f) >= scaledTouchSlop || Math.abs(f2) >= scaledTouchSlop) && !LNavigation.this.isSwipeInProgress && LNavigation.this.previewMenu != null)) {
                    LNavigation.this.allowToPressByHover = true;
                }
                if (LNavigation.this.allowToPressByHover && LNavigation.this.previewMenu != null && (LNavigation.this.previewMenu.getSwipeBack() == null || LNavigation.this.previewMenu.getSwipeBack().isForegroundOpen())) {
                    for (int i = 0; i < LNavigation.this.previewMenu.getItemsCount(); i++) {
                        ActionBarMenuSubItem actionBarMenuSubItem = (ActionBarMenuSubItem) LNavigation.this.previewMenu.getItemAt(i);
                        if (actionBarMenuSubItem != null) {
                            Drawable background = actionBarMenuSubItem.getBackground();
                            Rect rect = AndroidUtilities.rectTmp2;
                            actionBarMenuSubItem.getGlobalVisibleRect(rect);
                            boolean contains = rect.contains((int) motionEvent2.getX(), (int) motionEvent2.getY());
                            if (contains != (background.getState().length == 2)) {
                                background.setState(contains ? new int[]{16842919, 16842910} : new int[0]);
                                if (contains && Build.VERSION.SDK_INT >= 27) {
                                    try {
                                        actionBarMenuSubItem.performHapticFeedback(9, 1);
                                    } catch (Exception unused) {
                                    }
                                }
                            }
                        }
                    }
                }
                if (!LNavigation.this.isSwipeInProgress && !LNavigation.this.isSwipeDisallowed) {
                    if (Math.abs(f) >= Math.abs(f2) * 1.5f && f <= (-scaledTouchSlop)) {
                        LNavigation lNavigation = LNavigation.this;
                        if (!lNavigation.isIgnoredView(lNavigation.getForegroundView(), motionEvent2, LNavigation.this.ignoreRect) && LNavigation.this.getLastFragment().canBeginSlide() && LNavigation.this.getLastFragment().isSwipeBackEnabled(motionEvent2) && LNavigation.this.fragmentStack.size() >= 2 && !LNavigation.this.isInActionMode && !LNavigation.this.isInPreviewMode()) {
                            LNavigation.this.isSwipeInProgress = true;
                            LNavigation lNavigation2 = LNavigation.this;
                            lNavigation2.startScroll = lNavigation2.swipeProgress - MathUtils.clamp((motionEvent2.getX() - motionEvent.getX()) / LNavigation.this.getWidth(), 0.0f, 1.0f);
                            if (LNavigation.this.getParentActivity().getCurrentFocus() != null) {
                                AndroidUtilities.hideKeyboard(LNavigation.this.getParentActivity().getCurrentFocus());
                            }
                            if (LNavigation.this.getBackgroundView() != null) {
                                LNavigation.this.getBackgroundView().setVisibility(0);
                            }
                            LNavigation.this.getLastFragment().prepareFragmentToSlide(true, true);
                            LNavigation.this.getLastFragment().onBeginSlide();
                            BaseFragment backgroundFragment = LNavigation.this.getBackgroundFragment();
                            if (backgroundFragment != null) {
                                backgroundFragment.onResume();
                                backgroundFragment.prepareFragmentToSlide(false, true);
                                backgroundFragment.onBeginSlide();
                            }
                            MotionEvent obtain = MotionEvent.obtain(0L, 0L, 3, 0.0f, 0.0f, 0);
                            for (int i2 = 0; i2 < LNavigation.this.getChildCount(); i2++) {
                                LNavigation.this.getChildAt(i2).dispatchTouchEvent(obtain);
                            }
                            obtain.recycle();
                        }
                    }
                    LNavigation.this.isSwipeDisallowed = true;
                }
                if (LNavigation.this.isSwipeInProgress) {
                    LNavigation lNavigation3 = LNavigation.this;
                    lNavigation3.swipeProgress = MathUtils.clamp(lNavigation3.startScroll + ((motionEvent2.getX() - motionEvent.getX()) / LNavigation.this.getWidth()), 0.0f, 1.0f);
                    LNavigation.this.invalidateTranslation();
                }
                return LNavigation.this.isSwipeInProgress;
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
                if (!LNavigation.this.isSwipeInProgress || f < 800.0f) {
                    return false;
                }
                LNavigation.this.closeLastFragment(true, false, f / 15.0f);
                LNavigation.this.clearTouchFlags();
                return true;
            }
        });
        this.gestureDetector = gestureDetectorCompat;
        gestureDetectorCompat.setIsLongpressEnabled(false);
        LinearLayout linearLayout = new LinearLayout(context);
        this.stiffnessControl = linearLayout;
        if (Build.VERSION.SDK_INT >= 21) {
            linearLayout.setElevation(AndroidUtilities.dp(12.0f));
        }
        this.stiffnessControl.setOrientation(1);
        this.stiffnessControl.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        final TextView textView = new TextView(context);
        textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        textView.setGravity(17);
        Locale locale = Locale.ROOT;
        textView.setText(String.format(locale, "Stiffness: %f", Float.valueOf(SPRING_STIFFNESS)));
        this.stiffnessControl.addView(textView, LayoutHelper.createLinear(-1, 36));
        SeekBarView seekBarView = new SeekBarView(context);
        seekBarView.setReportChanges(true);
        seekBarView.setDelegate(new SeekBarView.SeekBarViewDelegate(this) { // from class: org.telegram.ui.LNavigation.LNavigation.2
            @Override // org.telegram.ui.Components.SeekBarView.SeekBarViewDelegate
            public /* synthetic */ CharSequence getContentDescription() {
                return SeekBarView.SeekBarViewDelegate.CC.$default$getContentDescription(this);
            }

            @Override // org.telegram.ui.Components.SeekBarView.SeekBarViewDelegate
            public /* synthetic */ int getStepsCount() {
                return SeekBarView.SeekBarViewDelegate.CC.$default$getStepsCount(this);
            }

            @Override // org.telegram.ui.Components.SeekBarView.SeekBarViewDelegate
            public void onSeekBarPressed(boolean z) {
            }

            @Override // org.telegram.ui.Components.SeekBarView.SeekBarViewDelegate
            public void onSeekBarDrag(boolean z, float f) {
                float f2 = (f * 1000.0f) + 500.0f;
                textView.setText(String.format(Locale.ROOT, "Stiffness: %f", Float.valueOf(f2)));
                if (z) {
                    float unused = LNavigation.SPRING_STIFFNESS = f2;
                }
            }
        });
        seekBarView.setProgress((SPRING_STIFFNESS - 500.0f) / 1000.0f);
        this.stiffnessControl.addView(seekBarView, LayoutHelper.createLinear(-1, 38));
        final TextView textView2 = new TextView(context);
        textView2.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        textView2.setGravity(17);
        textView2.setText(String.format(locale, "Damping ratio: %f", Float.valueOf(SPRING_DAMPING_RATIO)));
        this.stiffnessControl.addView(textView2, LayoutHelper.createLinear(-1, 36));
        SeekBarView seekBarView2 = new SeekBarView(context);
        seekBarView2.setReportChanges(true);
        seekBarView2.setDelegate(new SeekBarView.SeekBarViewDelegate(this) { // from class: org.telegram.ui.LNavigation.LNavigation.3
            @Override // org.telegram.ui.Components.SeekBarView.SeekBarViewDelegate
            public /* synthetic */ CharSequence getContentDescription() {
                return SeekBarView.SeekBarViewDelegate.CC.$default$getContentDescription(this);
            }

            @Override // org.telegram.ui.Components.SeekBarView.SeekBarViewDelegate
            public /* synthetic */ int getStepsCount() {
                return SeekBarView.SeekBarViewDelegate.CC.$default$getStepsCount(this);
            }

            @Override // org.telegram.ui.Components.SeekBarView.SeekBarViewDelegate
            public void onSeekBarPressed(boolean z) {
            }

            @Override // org.telegram.ui.Components.SeekBarView.SeekBarViewDelegate
            public void onSeekBarDrag(boolean z, float f) {
                float f2 = (f * 0.8f) + 0.2f;
                textView2.setText(String.format(Locale.ROOT, "Damping ratio: %f", Float.valueOf(f2)));
                if (z) {
                    float unused = LNavigation.SPRING_DAMPING_RATIO = f2;
                }
            }
        });
        seekBarView2.setProgress((SPRING_DAMPING_RATIO - 0.2f) / 0.8f);
        this.stiffnessControl.addView(seekBarView2, LayoutHelper.createLinear(-1, 38));
        CheckBoxCell checkBoxCell = new CheckBoxCell(context, 1);
        this.openChatCheckbox = checkBoxCell;
        checkBoxCell.setText("Show chat open measurement", null, false, false);
        this.openChatCheckbox.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.LNavigation.LNavigation$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                LNavigation.this.lambda$new$0(view);
            }
        });
        this.stiffnessControl.addView(this.openChatCheckbox, LayoutHelper.createLinear(-1, 36));
        this.stiffnessControl.setVisibility(8);
        this.overlayLayout.addView(this.stiffnessControl, LayoutHelper.createFrame(-1, -2, 80));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(View view) {
        CheckBoxCell checkBoxCell = this.openChatCheckbox;
        checkBoxCell.setChecked(!checkBoxCell.isChecked(), true);
    }

    @Override // android.view.ViewGroup
    public void addView(View view, int i, ViewGroup.LayoutParams layoutParams) {
        if (getChildCount() >= 3) {
            throw new IllegalStateException("LNavigation must have no more than 3 child views!");
        }
        super.addView(view, i, layoutParams);
    }

    public boolean doShowOpenChat() {
        return this.openChatCheckbox.isChecked();
    }

    public LinearLayout getStiffnessControl() {
        return this.stiffnessControl;
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public void requestDisallowInterceptTouchEvent(boolean z) {
        super.requestDisallowInterceptTouchEvent(z);
        if (z && this.isSwipeInProgress) {
            this.isSwipeInProgress = false;
            onReleaseTouch();
        }
        this.isSwipeDisallowed = z;
    }

    private void animateReset() {
        final BaseFragment lastFragment = getLastFragment();
        final BaseFragment backgroundFragment = getBackgroundFragment();
        if (lastFragment == null) {
            return;
        }
        lastFragment.onTransitionAnimationStart(true, true);
        if (backgroundFragment != null) {
            backgroundFragment.onTransitionAnimationStart(false, true);
        }
        SpringAnimation spring = new SpringAnimation(new FloatValueHolder(this.swipeProgress * 1000.0f)).setSpring(new SpringForce(0.0f).setStiffness(SPRING_STIFFNESS).setDampingRatio(SPRING_DAMPING_RATIO));
        this.currentSpringAnimation = spring;
        spring.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() { // from class: org.telegram.ui.LNavigation.LNavigation$$ExternalSyntheticLambda10
            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationUpdateListener
            public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f, float f2) {
                LNavigation.this.lambda$animateReset$1(lastFragment, backgroundFragment, dynamicAnimation, f, f2);
            }
        });
        final Runnable runnable = new Runnable() { // from class: org.telegram.ui.LNavigation.LNavigation$$ExternalSyntheticLambda17
            @Override // java.lang.Runnable
            public final void run() {
                LNavigation.this.lambda$animateReset$2(lastFragment, backgroundFragment);
            }
        };
        this.currentSpringAnimation.addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: org.telegram.ui.LNavigation.LNavigation$$ExternalSyntheticLambda4
            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
            public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                LNavigation.this.lambda$animateReset$3(runnable, dynamicAnimation, z, f, f2);
            }
        });
        if (this.swipeProgress != 0.0f) {
            this.currentSpringAnimation.start();
        } else {
            runnable.run();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$animateReset$1(BaseFragment baseFragment, BaseFragment baseFragment2, DynamicAnimation dynamicAnimation, float f, float f2) {
        this.swipeProgress = f / 1000.0f;
        invalidateTranslation();
        baseFragment.onTransitionAnimationProgress(true, 1.0f - this.swipeProgress);
        if (baseFragment2 != null) {
            baseFragment2.onTransitionAnimationProgress(false, 1.0f - this.swipeProgress);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$animateReset$2(BaseFragment baseFragment, BaseFragment baseFragment2) {
        baseFragment.onTransitionAnimationEnd(true, true);
        baseFragment.prepareFragmentToSlide(true, false);
        if (baseFragment2 != null) {
            baseFragment2.onTransitionAnimationEnd(false, true);
        }
        this.swipeProgress = 0.0f;
        invalidateTranslation();
        if (getBackgroundView() != null) {
            getBackgroundView().setVisibility(8);
        }
        baseFragment.onBecomeFullyVisible();
        if (baseFragment2 != null) {
            baseFragment2.onPause();
            baseFragment2.onBecomeFullyHidden();
            baseFragment2.prepareFragmentToSlide(false, false);
        }
        this.currentSpringAnimation = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$animateReset$3(Runnable runnable, DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        if (dynamicAnimation == this.currentSpringAnimation) {
            runnable.run();
        }
    }

    private boolean processTouchEvent(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        if (isTransitionAnimationInProgress()) {
            return true;
        }
        if (!this.gestureDetector.onTouchEvent(motionEvent) && (actionMasked == 1 || actionMasked == 3)) {
            if (this.isFirstHoverAllowed && !this.allowToPressByHover) {
                clearTouchFlags();
            } else if (this.allowToPressByHover) {
                for (int i = 0; i < this.previewMenu.getItemsCount(); i++) {
                    ActionBarMenuSubItem actionBarMenuSubItem = (ActionBarMenuSubItem) this.previewMenu.getItemAt(i);
                    if (actionBarMenuSubItem != null) {
                        Rect rect = AndroidUtilities.rectTmp2;
                        actionBarMenuSubItem.getGlobalVisibleRect(rect);
                        if (rect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                            actionBarMenuSubItem.performClick();
                        }
                    }
                }
                clearTouchFlags();
            } else if (this.isSwipeInProgress) {
                clearTouchFlags();
                onReleaseTouch();
            } else if (this.isSwipeDisallowed) {
                clearTouchFlags();
            }
            return false;
        }
        return this.isSwipeInProgress;
    }

    private void onReleaseTouch() {
        if (this.swipeProgress < 0.5f) {
            animateReset();
        } else {
            closeLastFragment(true, false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clearTouchFlags() {
        this.isSwipeDisallowed = false;
        this.isSwipeInProgress = false;
        this.allowToPressByHover = false;
        this.isFirstHoverAllowed = false;
    }

    @Override // android.view.View
    @SuppressLint({"ClickableViewAccessibility"})
    public boolean onTouchEvent(MotionEvent motionEvent) {
        processTouchEvent(motionEvent);
        return true;
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (!processTouchEvent(motionEvent) || this.touchCapturedView != null) {
            if (getChildCount() < 1) {
                return false;
            }
            if (getForegroundView() != null) {
                View view = this.touchCapturedView;
                FragmentHolderView foregroundView = getForegroundView();
                motionEvent.offsetLocation(-getPaddingLeft(), -getPaddingTop());
                boolean z = this.overlayLayout.dispatchTouchEvent(motionEvent) || view == this.overlayLayout;
                if (z && motionEvent.getAction() == 0) {
                    this.touchCapturedView = this.overlayLayout;
                    MotionEvent obtain = MotionEvent.obtain(0L, 0L, 3, 0.0f, 0.0f, 0);
                    for (int i = 0; i < getChildCount() - 1; i++) {
                        getChildAt(i).dispatchTouchEvent(obtain);
                    }
                    obtain.recycle();
                }
                if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                    this.touchCapturedView = null;
                }
                if (z) {
                    return true;
                }
                if (view != null) {
                    return view.dispatchTouchEvent(motionEvent) || motionEvent.getActionMasked() == 0;
                }
                boolean dispatchTouchEvent = foregroundView.dispatchTouchEvent(motionEvent);
                if (dispatchTouchEvent && motionEvent.getAction() == 0) {
                    this.touchCapturedView = foregroundView;
                }
                return dispatchTouchEvent || motionEvent.getActionMasked() == 0;
            }
            return super.dispatchTouchEvent(motionEvent);
        }
        return true;
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public boolean presentFragment(final INavigationLayout.NavigationParams navigationParams) {
        INavigationLayout.INavigationLayoutDelegate iNavigationLayoutDelegate;
        final BaseFragment baseFragment = navigationParams.fragment;
        if (navigationParams.isFromDelay || (baseFragment != null && !checkTransitionAnimation() && (((iNavigationLayoutDelegate = this.delegate) == null || !navigationParams.checkPresentFromDelegate || iNavigationLayoutDelegate.needPresentFragment(this, navigationParams)) && baseFragment.onFragmentCreate() && this.delayedPresentAnimation == null))) {
            if (getParentActivity().getCurrentFocus() != null) {
                AndroidUtilities.hideKeyboard(getParentActivity().getCurrentFocus());
            }
            if (!navigationParams.isFromDelay) {
                baseFragment.setInPreviewMode(navigationParams.preview);
                ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.previewMenu;
                if (actionBarPopupWindowLayout != null && actionBarPopupWindowLayout.getParent() != null) {
                    ((ViewGroup) this.previewMenu.getParent()).removeView(this.previewMenu);
                }
                ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout2 = navigationParams.menuView;
                this.previewMenu = actionBarPopupWindowLayout2;
                baseFragment.setInMenuMode(actionBarPopupWindowLayout2 != null);
                baseFragment.setParentLayout(this);
            }
            boolean z = navigationParams.preview || (MessagesController.getGlobalMainSettings().getBoolean("view_animations", true) && !navigationParams.noAnimation && (this.useAlphaAnimations || this.fragmentStack.size() >= 1));
            final BaseFragment backgroundFragment = navigationParams.isFromDelay ? getBackgroundFragment() : getLastFragment();
            final Runnable runnable = new Runnable() { // from class: org.telegram.ui.LNavigation.LNavigation$$ExternalSyntheticLambda21
                @Override // java.lang.Runnable
                public final void run() {
                    LNavigation.this.lambda$presentFragment$4(navigationParams, backgroundFragment);
                }
            };
            if (z) {
                if (!navigationParams.isFromDelay) {
                    if (navigationParams.preview) {
                        FragmentHolderView foregroundView = getForegroundView();
                        int measuredWidth = (int) (foregroundView.getMeasuredWidth() / 8.0f);
                        int measuredHeight = (int) (foregroundView.getMeasuredHeight() / 8.0f);
                        Bitmap createBitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(createBitmap);
                        canvas.scale(0.125f, 0.125f);
                        canvas.drawColor(Theme.getColor("windowBackgroundWhite"));
                        foregroundView.draw(canvas);
                        Utilities.stackBlurBitmap(createBitmap, Math.max(8, Math.max(measuredWidth, measuredHeight) / 150));
                        this.blurredBackFragmentForPreview = createBitmap;
                        if (getParent() != null) {
                            getParent().requestDisallowInterceptTouchEvent(true);
                        }
                        this.isFirstHoverAllowed = true;
                    }
                    FragmentHolderView onCreateHolderView = onCreateHolderView(baseFragment);
                    if (navigationParams.preview) {
                        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) onCreateHolderView.getLayoutParams();
                        int dp = AndroidUtilities.dp(8.0f);
                        marginLayoutParams.bottomMargin = dp;
                        marginLayoutParams.rightMargin = dp;
                        marginLayoutParams.topMargin = dp;
                        marginLayoutParams.leftMargin = dp;
                        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout3 = this.previewMenu;
                        if (actionBarPopupWindowLayout3 != null) {
                            actionBarPopupWindowLayout3.measure(View.MeasureSpec.makeMeasureSpec(getWidth(), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec((int) (getHeight() * 0.5f), Integer.MIN_VALUE));
                            ((ViewGroup.MarginLayoutParams) baseFragment.getFragmentView().getLayoutParams()).bottomMargin += AndroidUtilities.dp(8.0f) + this.previewMenu.getMeasuredHeight();
                            if (LocaleController.isRTL) {
                                this.previewMenu.setTranslationX((getWidth() - this.previewMenu.getMeasuredWidth()) - AndroidUtilities.dp(8.0f));
                            } else {
                                this.previewMenu.setTranslationX(-AndroidUtilities.dp(8.0f));
                            }
                            this.previewMenu.setTranslationY((getHeight() - AndroidUtilities.dp(24.0f)) - this.previewMenu.getMeasuredHeight());
                            onCreateHolderView.addView(this.previewMenu, LayoutHelper.createFrame(-2, -2.0f, LocaleController.isRTL ? 5 : 3, 0.0f, 0.0f, 0.0f, 8.0f));
                        } else {
                            marginLayoutParams.topMargin = dp + AndroidUtilities.dp(52.0f);
                        }
                        onCreateHolderView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.LNavigation.LNavigation$$ExternalSyntheticLambda1
                            @Override // android.view.View.OnClickListener
                            public final void onClick(View view) {
                                LNavigation.this.lambda$presentFragment$5(view);
                            }
                        });
                    }
                    addView(onCreateHolderView, getChildCount() - 1);
                    this.fragmentStack.add(baseFragment);
                    notifyFragmentStackChanged();
                    baseFragment.onResume();
                    this.swipeProgress = 1.0f;
                    invalidateTranslation();
                }
                if (baseFragment.needDelayOpenAnimation() && !navigationParams.delayDone) {
                    Runnable runnable2 = new Runnable() { // from class: org.telegram.ui.LNavigation.LNavigation$$ExternalSyntheticLambda19
                        @Override // java.lang.Runnable
                        public final void run() {
                            LNavigation.this.lambda$presentFragment$6(navigationParams);
                        }
                    };
                    this.delayedPresentAnimation = runnable2;
                    AndroidUtilities.runOnUIThread(runnable2, 200L);
                    return true;
                }
                baseFragment.onTransitionAnimationStart(true, false);
                if (backgroundFragment != null) {
                    backgroundFragment.onTransitionAnimationStart(false, true);
                }
                AnimatorSet onCustomTransitionAnimation = baseFragment.onCustomTransitionAnimation(true, new Runnable() { // from class: org.telegram.ui.LNavigation.LNavigation$$ExternalSyntheticLambda18
                    @Override // java.lang.Runnable
                    public final void run() {
                        LNavigation.this.lambda$presentFragment$7(baseFragment, backgroundFragment, runnable);
                    }
                });
                this.customAnimation = onCustomTransitionAnimation;
                if (onCustomTransitionAnimation != null) {
                    getForegroundView().setTranslationX(0.0f);
                    return true;
                }
                SpringAnimation spring = new SpringAnimation(new FloatValueHolder(1000.0f)).setSpring(new SpringForce(0.0f).setStiffness(navigationParams.preview ? 650.0f : SPRING_STIFFNESS).setDampingRatio(navigationParams.preview ? 0.6f : SPRING_DAMPING_RATIO));
                this.currentSpringAnimation = spring;
                spring.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() { // from class: org.telegram.ui.LNavigation.LNavigation$$ExternalSyntheticLambda9
                    @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationUpdateListener
                    public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f, float f2) {
                        LNavigation.this.lambda$presentFragment$8(baseFragment, dynamicAnimation, f, f2);
                    }
                });
                this.currentSpringAnimation.addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: org.telegram.ui.LNavigation.LNavigation$$ExternalSyntheticLambda7
                    @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
                    public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z2, float f, float f2) {
                        LNavigation.this.lambda$presentFragment$9(baseFragment, navigationParams, backgroundFragment, runnable, dynamicAnimation, z2, f, f2);
                    }
                });
                this.currentSpringAnimation.start();
            } else if (!navigationParams.preview) {
                if (baseFragment.needDelayOpenAnimation() && !navigationParams.delayDone && navigationParams.needDelayWithoutAnimation) {
                    Runnable runnable3 = new Runnable() { // from class: org.telegram.ui.LNavigation.LNavigation$$ExternalSyntheticLambda20
                        @Override // java.lang.Runnable
                        public final void run() {
                            LNavigation.this.lambda$presentFragment$10(navigationParams);
                        }
                    };
                    this.delayedPresentAnimation = runnable3;
                    AndroidUtilities.runOnUIThread(runnable3, 200L);
                    return true;
                }
                addFragmentToStack(baseFragment, -1, true);
                runnable.run();
            }
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$presentFragment$4(INavigationLayout.NavigationParams navigationParams, BaseFragment baseFragment) {
        if (!navigationParams.removeLast || baseFragment == null) {
            return;
        }
        removeFragmentFromStack(baseFragment);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$presentFragment$5(View view) {
        finishPreviewFragment();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$presentFragment$6(INavigationLayout.NavigationParams navigationParams) {
        this.delayedPresentAnimation = null;
        navigationParams.isFromDelay = true;
        navigationParams.delayDone = true;
        presentFragment(navigationParams);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$presentFragment$7(BaseFragment baseFragment, BaseFragment baseFragment2, Runnable runnable) {
        baseFragment.onTransitionAnimationEnd(true, false);
        this.swipeProgress = 0.0f;
        invalidateTranslation();
        getBackgroundView().setVisibility(8);
        baseFragment.onBecomeFullyVisible();
        if (baseFragment2 != null) {
            baseFragment2.onTransitionAnimationEnd(false, true);
            baseFragment2.onBecomeFullyHidden();
        }
        runnable.run();
        this.customAnimation = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$presentFragment$8(BaseFragment baseFragment, DynamicAnimation dynamicAnimation, float f, float f2) {
        this.swipeProgress = f / 1000.0f;
        invalidateTranslation();
        baseFragment.onTransitionAnimationProgress(true, 1.0f - this.swipeProgress);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$presentFragment$9(BaseFragment baseFragment, INavigationLayout.NavigationParams navigationParams, BaseFragment baseFragment2, Runnable runnable, DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        Runnable runnable2;
        if (dynamicAnimation == this.currentSpringAnimation) {
            baseFragment.onTransitionAnimationEnd(true, false);
            this.swipeProgress = 0.0f;
            invalidateTranslation();
            if (!navigationParams.preview && getBackgroundView() != null) {
                getBackgroundView().setVisibility(8);
            }
            baseFragment.onBecomeFullyVisible();
            if (baseFragment2 != null) {
                baseFragment2.onTransitionAnimationEnd(false, true);
                baseFragment2.onBecomeFullyHidden();
                baseFragment2.onPause();
            }
            runnable.run();
            this.currentSpringAnimation = null;
            if (navigationParams.preview && (runnable2 = this.previewOpenCallback) != null) {
                runnable2.run();
            }
            this.previewOpenCallback = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$presentFragment$10(INavigationLayout.NavigationParams navigationParams) {
        this.delayedPresentAnimation = null;
        navigationParams.isFromDelay = true;
        navigationParams.delayDone = true;
        presentFragment(navigationParams);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void invalidateTranslation() {
        if (this.useAlphaAnimations && this.fragmentStack.size() == 1) {
            this.backgroundView.setAlpha(1.0f - this.swipeProgress);
            setAlpha(1.0f - this.swipeProgress);
            return;
        }
        FragmentHolderView backgroundView = getBackgroundView();
        FragmentHolderView foregroundView = getForegroundView();
        boolean isInPreviewMode = isInPreviewMode();
        float width = (getWidth() - getPaddingLeft()) - getPaddingRight();
        float height = (getHeight() - getPaddingTop()) - getPaddingBottom();
        if (isInPreviewMode) {
            if (backgroundView != null) {
                backgroundView.setTranslationX(0.0f);
                backgroundView.invalidate();
            }
            if (foregroundView != null) {
                foregroundView.setPivotX(width / 2.0f);
                foregroundView.setPivotY(height / 2.0f);
                foregroundView.setTranslationX(0.0f);
                foregroundView.setTranslationY(0.0f);
                float f = ((1.0f - this.swipeProgress) * 0.5f) + 0.5f;
                foregroundView.setScaleX(f);
                foregroundView.setScaleY(f);
                foregroundView.setAlpha(1.0f - Math.max(this.swipeProgress, 0.0f));
                foregroundView.invalidate();
            }
        } else {
            if (backgroundView != null) {
                backgroundView.setTranslationX((-(1.0f - this.swipeProgress)) * 0.35f * width);
            }
            if (foregroundView != null) {
                foregroundView.setTranslationX(this.swipeProgress * width);
            }
        }
        invalidate();
        if (getBackgroundFragment() == null) {
            return;
        }
        getBackgroundFragment().onSlideProgress(false, this.swipeProgress);
    }

    @Override // org.telegram.ui.Components.FloatingDebug.FloatingDebugProvider
    public List<FloatingDebugController.DebugItem> onGetDebugItems() {
        ArrayList arrayList = new ArrayList();
        BaseFragment lastFragment = getLastFragment();
        if (lastFragment != null) {
            if (lastFragment instanceof FloatingDebugProvider) {
                arrayList.addAll(((FloatingDebugProvider) lastFragment).onGetDebugItems());
            }
            observeDebugItemsFromView(arrayList, lastFragment.getFragmentView());
        }
        return arrayList;
    }

    private void observeDebugItemsFromView(List<FloatingDebugController.DebugItem> list, View view) {
        if (view instanceof FloatingDebugProvider) {
            list.addAll(((FloatingDebugProvider) view).onGetDebugItems());
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                observeDebugItemsFromView(list, viewGroup.getChildAt(i));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public FragmentHolderView getForegroundView() {
        if (getChildCount() >= 2) {
            return (FragmentHolderView) getChildAt(getChildCount() >= 3 ? 1 : 0);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public FragmentHolderView getBackgroundView() {
        if (getChildCount() >= 3) {
            return (FragmentHolderView) getChildAt(0);
        }
        return null;
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public boolean checkTransitionAnimation() {
        return isTransitionAnimationInProgress();
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public boolean addFragmentToStack(BaseFragment baseFragment, int i) {
        return addFragmentToStack(baseFragment, i, false);
    }

    public boolean addFragmentToStack(BaseFragment baseFragment, int i, boolean z) {
        INavigationLayout.INavigationLayoutDelegate iNavigationLayoutDelegate;
        if (z || (((iNavigationLayoutDelegate = this.delegate) == null || iNavigationLayoutDelegate.needAddFragmentToStack(baseFragment, this)) && baseFragment.onFragmentCreate())) {
            baseFragment.setParentLayout(this);
            if (i == -1 || i >= this.fragmentStack.size()) {
                BaseFragment lastFragment = getLastFragment();
                if (lastFragment != null) {
                    lastFragment.onPause();
                    lastFragment.onTransitionAnimationStart(false, true);
                    lastFragment.onTransitionAnimationEnd(false, true);
                    lastFragment.onBecomeFullyHidden();
                }
                this.fragmentStack.add(baseFragment);
                notifyFragmentStackChanged();
                addView(onCreateHolderView(baseFragment), getChildCount() - 1);
                baseFragment.onResume();
                baseFragment.onTransitionAnimationStart(true, false);
                baseFragment.onTransitionAnimationEnd(true, false);
                baseFragment.onBecomeFullyVisible();
                if (getBackgroundView() != null) {
                    getBackgroundView().setVisibility(8);
                }
                getForegroundView().setVisibility(0);
            } else {
                this.fragmentStack.add(i, baseFragment);
                notifyFragmentStackChanged();
                if (i == this.fragmentStack.size() - 2) {
                    addView(onCreateHolderView(baseFragment), getChildCount() - 2);
                    getBackgroundView().setVisibility(8);
                    getForegroundView().setVisibility(0);
                }
            }
            invalidateTranslation();
            return true;
        }
        return false;
    }

    private FragmentHolderView onCreateHolderView(BaseFragment baseFragment) {
        FragmentHolderView fragmentHolderView;
        if (getChildCount() >= 3) {
            fragmentHolderView = getBackgroundView();
        } else {
            fragmentHolderView = new FragmentHolderView(getContext());
        }
        fragmentHolderView.setFragment(baseFragment);
        if (fragmentHolderView.getParent() != null) {
            fragmentHolderView.setVisibility(0);
            removeView(fragmentHolderView);
        }
        fragmentHolderView.setOnClickListener(null);
        resetViewProperties(fragmentHolderView);
        resetViewProperties(baseFragment.getFragmentView());
        if (baseFragment.getActionBar() != null) {
            baseFragment.getActionBar().setTitleOverlayText(this.titleOverlayTitle, this.titleOverlayTitleId, this.titleOverlayAction);
        }
        return fragmentHolderView;
    }

    private void resetViewProperties(View view) {
        if (view == null) {
            return;
        }
        view.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        view.setAlpha(1.0f);
        view.setPivotX(0.0f);
        view.setPivotY(0.0f);
        view.setScaleX(1.0f);
        view.setScaleY(1.0f);
        view.setTranslationX(0.0f);
        view.setTranslationY(0.0f);
    }

    private void notifyFragmentStackChanged() {
        Runnable runnable = this.onFragmentStackChangedListener;
        if (runnable != null) {
            runnable.run();
        }
        if (this.useAlphaAnimations) {
            if (this.fragmentStack.isEmpty()) {
                setVisibility(8);
                this.backgroundView.setVisibility(8);
            } else {
                setVisibility(0);
                this.backgroundView.setVisibility(0);
            }
            DrawerLayoutContainer drawerLayoutContainer = this.drawerLayoutContainer;
            if (drawerLayoutContainer != null) {
                drawerLayoutContainer.setAllowOpenDrawer(this.fragmentStack.isEmpty(), false);
            }
        } else if (this.fragmentStack.isEmpty() || this.useAlphaAnimations || !this.useBackground) {
            setBackgroundColor(0);
        } else {
            setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        }
        ImageLoader.getInstance().onFragmentStackChanged();
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public void removeFragmentFromStack(BaseFragment baseFragment) {
        int indexOf = this.fragmentStack.indexOf(baseFragment);
        if (indexOf == -1) {
            return;
        }
        int size = this.fragmentStack.size();
        baseFragment.setRemovingFromStack(true);
        baseFragment.onFragmentDestroy();
        baseFragment.setParentLayout(null);
        this.fragmentStack.remove(indexOf);
        notifyFragmentStackChanged();
        if (indexOf == size - 1) {
            BaseFragment lastFragment = getLastFragment();
            if (lastFragment != null) {
                lastFragment.onResume();
                lastFragment.onBecomeFullyVisible();
            }
            FragmentHolderView foregroundView = getForegroundView();
            if (foregroundView != null) {
                removeView(foregroundView);
                resetViewProperties(foregroundView);
            }
            if (getForegroundView() != null) {
                getForegroundView().setVisibility(0);
            }
            if (this.fragmentStack.size() >= 2) {
                BaseFragment backgroundFragment = getBackgroundFragment();
                backgroundFragment.setParentLayout(this);
                if (foregroundView != null) {
                    foregroundView.setFragment(backgroundFragment);
                } else {
                    foregroundView = onCreateHolderView(backgroundFragment);
                }
                backgroundFragment.onBecomeFullyHidden();
                foregroundView.setVisibility(8);
                addView(foregroundView, getChildCount() - 2);
            }
        } else if (indexOf == size - 2) {
            FragmentHolderView backgroundView = getBackgroundView();
            if (backgroundView != null) {
                removeView(backgroundView);
                resetViewProperties(backgroundView);
            }
            if (this.fragmentStack.size() >= 2) {
                BaseFragment backgroundFragment2 = getBackgroundFragment();
                backgroundFragment2.setParentLayout(this);
                if (backgroundView != null) {
                    backgroundView.setFragment(backgroundFragment2);
                } else {
                    backgroundView = onCreateHolderView(backgroundFragment2);
                }
                backgroundFragment2.onBecomeFullyHidden();
                backgroundView.setVisibility(8);
                addView(backgroundView, getChildCount() - 2);
            }
        }
        invalidateTranslation();
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public List<BaseFragment> getFragmentStack() {
        return this.unmodifiableFragmentStack;
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public void setFragmentStack(List<BaseFragment> list) {
        this.fragmentStack = list;
        this.unmodifiableFragmentStack = Collections.unmodifiableList(list);
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public void showLastFragment() {
        lambda$rebuildFragments$12(3);
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    /* renamed from: rebuildFragments */
    public void lambda$rebuildFragments$12(final int i) {
        SpringAnimation springAnimation = this.currentSpringAnimation;
        if (springAnimation != null && springAnimation.isRunning()) {
            this.currentSpringAnimation.addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: org.telegram.ui.LNavigation.LNavigation$$ExternalSyntheticLambda3
                @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
                public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                    LNavigation.this.lambda$rebuildFragments$13(i, dynamicAnimation, z, f, f2);
                }
            });
            return;
        }
        AnimatorSet animatorSet = this.customAnimation;
        if (animatorSet != null && animatorSet.isRunning()) {
            this.customAnimation.addListener(new AnonymousClass4(i));
            return;
        }
        int i2 = 0;
        if (this.fragmentStack.isEmpty()) {
            while (getChildCount() > 1) {
                removeViewAt(0);
            }
            return;
        }
        int i3 = (i & 1) != 0 ? 1 : 0;
        boolean z = (i & 2) == 0 || (i3 != 0 && (!(getBackgroundView() == null || getBackgroundView().fragment == getBackgroundFragment()) || (getForegroundView() != null && getForegroundView().fragment == getLastFragment())));
        if (z && getChildCount() >= 3) {
            removeViewAt(0);
        }
        if (i3 != 0 && getChildCount() >= 2) {
            removeViewAt(0);
        }
        if (!z) {
            i2 = this.fragmentStack.size() - 1;
        }
        while (i2 < this.fragmentStack.size() - (i3 ^ 1)) {
            BaseFragment baseFragment = this.fragmentStack.get(i2);
            baseFragment.clearViews();
            baseFragment.setParentLayout(this);
            FragmentHolderView fragmentHolderView = new FragmentHolderView(getContext());
            fragmentHolderView.setFragment(baseFragment);
            if (i2 >= this.fragmentStack.size() - 2) {
                addView(fragmentHolderView, getChildCount() - 1);
            }
            i2++;
        }
        if (getLastFragment() == null) {
            return;
        }
        getLastFragment().onResume();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$rebuildFragments$13(final int i, DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LNavigation.LNavigation$$ExternalSyntheticLambda14
            @Override // java.lang.Runnable
            public final void run() {
                LNavigation.this.lambda$rebuildFragments$12(i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.ui.LNavigation.LNavigation$4  reason: invalid class name */
    /* loaded from: classes3.dex */
    public class AnonymousClass4 extends AnimatorListenerAdapter {
        final /* synthetic */ int val$flags;

        AnonymousClass4(int i) {
            this.val$flags = i;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onAnimationEnd$0(int i) {
            LNavigation.this.lambda$rebuildFragments$12(i);
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animator) {
            final int i = this.val$flags;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LNavigation.LNavigation$4$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    LNavigation.AnonymousClass4.this.lambda$onAnimationEnd$0(i);
                }
            });
        }
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public void setDelegate(INavigationLayout.INavigationLayoutDelegate iNavigationLayoutDelegate) {
        this.delegate = iNavigationLayoutDelegate;
    }

    @Override // android.view.View
    public void draw(Canvas canvas) {
        Drawable drawable;
        if (this.useAlphaAnimations) {
            canvas.save();
            this.path.rewind();
            RectF rectF = AndroidUtilities.rectTmp;
            rectF.set(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
            this.path.addRoundRect(rectF, AndroidUtilities.dp(12.0f), AndroidUtilities.dp(12.0f), Path.Direction.CW);
            canvas.clipPath(this.path);
        }
        super.draw(canvas);
        if (!isInPreviewMode() && ((!this.useAlphaAnimations || this.fragmentStack.size() > 1) && ((isSwipeInProgress() || isTransitionAnimationInProgress()) && this.swipeProgress != 0.0f))) {
            int width = (getWidth() - getPaddingLeft()) - getPaddingRight();
            this.dimmPaint.setAlpha((int) ((1.0f - this.swipeProgress) * 122.0f));
            float f = width;
            canvas.drawRect(getPaddingLeft(), getPaddingTop(), (this.swipeProgress * f) + getPaddingLeft(), getHeight() - getPaddingBottom(), this.dimmPaint);
            this.layerShadowDrawable.setAlpha((int) ((1.0f - this.swipeProgress) * 255.0f));
            this.layerShadowDrawable.setBounds(((int) ((this.swipeProgress * f) - drawable.getIntrinsicWidth())) + getPaddingLeft(), getPaddingTop(), ((int) (f * this.swipeProgress)) + getPaddingLeft(), getHeight() - getPaddingBottom());
            this.layerShadowDrawable.draw(canvas);
        }
        if (this.useAlphaAnimations) {
            canvas.restore();
        }
        if (this.previewFragmentSnapshot != null) {
            canvas.save();
            this.path.rewind();
            RectF rectF2 = AndroidUtilities.rectTmp;
            Rect rect = this.previewFragmentRect;
            float f2 = this.previewExpandProgress;
            rectF2.set(rect.left * (1.0f - f2), rect.top * (1.0f - f2), AndroidUtilities.lerp(rect.right, getWidth(), this.previewExpandProgress), AndroidUtilities.lerp(this.previewFragmentRect.bottom, getHeight(), this.previewExpandProgress));
            this.path.addRoundRect(rectF2, AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), Path.Direction.CW);
            canvas.clipPath(this.path);
            Rect rect2 = this.previewFragmentRect;
            float f3 = this.previewExpandProgress;
            canvas.translate(rect2.left * (1.0f - f3), rect2.top * (1.0f - f3));
            canvas.scale(AndroidUtilities.lerp(1.0f, getWidth() / this.previewFragmentRect.width(), this.previewExpandProgress), AndroidUtilities.lerp(1.0f, getHeight() / this.previewFragmentRect.height(), this.previewExpandProgress));
            this.blurPaint.setAlpha((int) ((1.0f - Math.min(1.0f, this.previewExpandProgress)) * 255.0f));
            canvas.drawBitmap(this.previewFragmentSnapshot, 0.0f, 0.0f, this.blurPaint);
            canvas.restore();
        }
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public void resumeDelayedFragmentAnimation() {
        Runnable runnable = this.delayedPresentAnimation;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.delayedPresentAnimation.run();
        }
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public void setUseAlphaAnimations(boolean z) {
        this.useAlphaAnimations = z;
        if (z || !this.useBackground) {
            setBackgroundColor(0);
        } else {
            setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        }
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public void setUseBackground(boolean z) {
        this.useBackground = z;
        if (this.useAlphaAnimations || !z) {
            setBackgroundColor(0);
        } else {
            setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        }
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public void setBackgroundView(View view) {
        this.backgroundView = view;
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public void closeLastFragment(boolean z, boolean z2) {
        closeLastFragment(z, z2, 0.0f);
    }

    public void closeLastFragment(boolean z, boolean z2, float f) {
        if (this.fragmentStack.isEmpty() || checkTransitionAnimation()) {
            return;
        }
        INavigationLayout.INavigationLayoutDelegate iNavigationLayoutDelegate = this.delegate;
        if (iNavigationLayoutDelegate != null && !iNavigationLayoutDelegate.needCloseLastFragment(this)) {
            return;
        }
        if (z && !z2 && MessagesController.getGlobalMainSettings().getBoolean("view_animations", true) && (this.useAlphaAnimations || this.fragmentStack.size() >= 2)) {
            AndroidUtilities.hideKeyboard(this);
            final BaseFragment lastFragment = getLastFragment();
            final BaseFragment backgroundFragment = getBackgroundFragment();
            if (getBackgroundView() != null) {
                getBackgroundView().setVisibility(0);
            }
            lastFragment.onTransitionAnimationStart(false, true);
            if (backgroundFragment != null) {
                backgroundFragment.onResume();
                backgroundFragment.onTransitionAnimationStart(true, false);
            }
            if (this.swipeProgress == 0.0f) {
                AnimatorSet onCustomTransitionAnimation = lastFragment.onCustomTransitionAnimation(false, new Runnable() { // from class: org.telegram.ui.LNavigation.LNavigation$$ExternalSyntheticLambda16
                    @Override // java.lang.Runnable
                    public final void run() {
                        LNavigation.this.lambda$closeLastFragment$14(lastFragment, backgroundFragment);
                    }
                });
                this.customAnimation = onCustomTransitionAnimation;
                if (onCustomTransitionAnimation != null) {
                    getForegroundView().setTranslationX(0.0f);
                    if (getBackgroundView() == null) {
                        return;
                    }
                    getBackgroundView().setTranslationX(0.0f);
                    return;
                }
            }
            SpringAnimation spring = new SpringAnimation(new FloatValueHolder(this.swipeProgress * 1000.0f)).setSpring(new SpringForce(1000.0f).setStiffness(isInPreviewMode() ? 800.0f : SPRING_STIFFNESS).setDampingRatio(SPRING_DAMPING_RATIO));
            this.currentSpringAnimation = spring;
            if (f != 0.0f) {
                spring.setStartVelocity(f);
            }
            this.currentSpringAnimation.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() { // from class: org.telegram.ui.LNavigation.LNavigation$$ExternalSyntheticLambda11
                @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationUpdateListener
                public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f2, float f3) {
                    LNavigation.this.lambda$closeLastFragment$15(lastFragment, backgroundFragment, dynamicAnimation, f2, f3);
                }
            });
            this.currentSpringAnimation.addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: org.telegram.ui.LNavigation.LNavigation$$ExternalSyntheticLambda6
                @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
                public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z3, float f2, float f3) {
                    LNavigation.this.lambda$closeLastFragment$16(lastFragment, backgroundFragment, dynamicAnimation, z3, f2, f3);
                }
            });
            this.currentSpringAnimation.start();
            return;
        }
        this.swipeProgress = 0.0f;
        removeFragmentFromStack(getLastFragment());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$closeLastFragment$14(BaseFragment baseFragment, BaseFragment baseFragment2) {
        baseFragment.onTransitionAnimationEnd(false, true);
        onCloseAnimationEnd(baseFragment, baseFragment2);
        this.customAnimation = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$closeLastFragment$15(BaseFragment baseFragment, BaseFragment baseFragment2, DynamicAnimation dynamicAnimation, float f, float f2) {
        this.swipeProgress = f / 1000.0f;
        invalidateTranslation();
        baseFragment.onTransitionAnimationProgress(false, this.swipeProgress);
        if (baseFragment2 != null) {
            baseFragment.onTransitionAnimationProgress(true, this.swipeProgress);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$closeLastFragment$16(BaseFragment baseFragment, BaseFragment baseFragment2, DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        if (dynamicAnimation == this.currentSpringAnimation) {
            onCloseAnimationEnd(baseFragment, baseFragment2);
            this.currentSpringAnimation = null;
        }
    }

    private void onCloseAnimationEnd(BaseFragment baseFragment, BaseFragment baseFragment2) {
        baseFragment.onPause();
        baseFragment.setRemovingFromStack(true);
        baseFragment.onTransitionAnimationEnd(false, true);
        baseFragment.prepareFragmentToSlide(true, false);
        baseFragment.onBecomeFullyHidden();
        baseFragment.onFragmentDestroy();
        baseFragment.setParentLayout(null);
        this.fragmentStack.remove(baseFragment);
        notifyFragmentStackChanged();
        FragmentHolderView foregroundView = getForegroundView();
        foregroundView.setFragment(null);
        removeView(foregroundView);
        resetViewProperties(foregroundView);
        if (baseFragment2 != null) {
            baseFragment2.prepareFragmentToSlide(false, false);
            baseFragment2.onTransitionAnimationEnd(true, false);
            baseFragment2.onBecomeFullyVisible();
        }
        if (this.fragmentStack.size() >= 2) {
            BaseFragment backgroundFragment = getBackgroundFragment();
            backgroundFragment.setParentLayout(this);
            foregroundView.setFragment(backgroundFragment);
            foregroundView.setVisibility(8);
            addView(foregroundView, getChildCount() - 2);
        }
        this.swipeProgress = 0.0f;
        invalidateTranslation();
        this.previewMenu = null;
        Bitmap bitmap = this.blurredBackFragmentForPreview;
        if (bitmap != null) {
            bitmap.recycle();
            this.blurredBackFragmentForPreview = null;
        }
        this.previewOpenCallback = null;
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public DrawerLayoutContainer getDrawerLayoutContainer() {
        return this.drawerLayoutContainer;
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public void setDrawerLayoutContainer(DrawerLayoutContainer drawerLayoutContainer) {
        this.drawerLayoutContainer = drawerLayoutContainer;
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public void setRemoveActionBarExtraHeight(boolean z) {
        this.removeActionBarExtraHeight = z;
    }

    private ActionBar getCurrentActionBar() {
        if (getLastFragment() != null) {
            return getLastFragment().getActionBar();
        }
        return null;
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public void setTitleOverlayText(String str, int i, Runnable runnable) {
        this.titleOverlayTitle = str;
        this.titleOverlayTitleId = i;
        this.titleOverlayAction = runnable;
        for (BaseFragment baseFragment : this.fragmentStack) {
            if (baseFragment.getActionBar() != null) {
                baseFragment.getActionBar().setTitleOverlayText(str, i, runnable);
            }
        }
    }

    private void addStartDescriptions(ArrayList<ThemeDescription> arrayList) {
        if (arrayList == null) {
            return;
        }
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

    private void addEndDescriptions(ArrayList<ThemeDescription> arrayList) {
        if (arrayList == null) {
            return;
        }
        int[] iArr = new int[arrayList.size()];
        this.animateEndColors.add(iArr);
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            iArr[i] = arrayList.get(i).getSetColor();
        }
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public void animateThemedValues(final INavigationLayout.ThemeAnimationSettings themeAnimationSettings, final Runnable runnable) {
        Theme.ThemeInfo themeInfo;
        ValueAnimator valueAnimator = this.themeAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.themeAnimator = null;
        }
        final int size = themeAnimationSettings.onlyTopFragment ? 1 : this.fragmentStack.size();
        final Runnable runnable2 = new Runnable() { // from class: org.telegram.ui.LNavigation.LNavigation$$ExternalSyntheticLambda15
            @Override // java.lang.Runnable
            public final void run() {
                LNavigation.this.lambda$animateThemedValues$18(size, themeAnimationSettings, runnable);
            }
        };
        if (size >= 1 && themeAnimationSettings.applyTheme) {
            int i = themeAnimationSettings.accentId;
            if (i != -1 && (themeInfo = themeAnimationSettings.theme) != null) {
                themeInfo.setCurrentAccentId(i);
                Theme.saveThemeAccents(themeAnimationSettings.theme, true, false, true, false);
            }
            if (runnable == null) {
                Theme.applyTheme(themeAnimationSettings.theme, themeAnimationSettings.nightTheme);
                runnable2.run();
                return;
            }
            Theme.applyThemeInBackground(themeAnimationSettings.theme, themeAnimationSettings.nightTheme, new Runnable() { // from class: org.telegram.ui.LNavigation.LNavigation$$ExternalSyntheticLambda12
                @Override // java.lang.Runnable
                public final void run() {
                    AndroidUtilities.runOnUIThread(runnable2);
                }
            });
            return;
        }
        runnable2.run();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$animateThemedValues$18(int i, final INavigationLayout.ThemeAnimationSettings themeAnimationSettings, Runnable runnable) {
        BaseFragment baseFragment;
        Runnable runnable2;
        int i2 = 0;
        boolean z = false;
        for (int i3 = 0; i3 < i; i3++) {
            if (i3 == 0) {
                baseFragment = getLastFragment();
            } else {
                if ((isInPreviewMode() || isPreviewOpenAnimationInProgress()) && this.fragmentStack.size() > 1) {
                    List<BaseFragment> list = this.fragmentStack;
                    baseFragment = list.get(list.size() - 2);
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
                if (baseFragment.getVisibleDialog() instanceof BottomSheet) {
                    addStartDescriptions(((BottomSheet) baseFragment.getVisibleDialog()).getThemeDescriptions());
                } else if (baseFragment.getVisibleDialog() instanceof AlertDialog) {
                    addStartDescriptions(((AlertDialog) baseFragment.getVisibleDialog()).getThemeDescriptions());
                }
                if (i3 == 0 && (runnable2 = themeAnimationSettings.afterStartDescriptionsAddedRunnable) != null) {
                    runnable2.run();
                }
                addEndDescriptions(themeDescriptions);
                if (baseFragment.getVisibleDialog() instanceof BottomSheet) {
                    addEndDescriptions(((BottomSheet) baseFragment.getVisibleDialog()).getThemeDescriptions());
                } else if (baseFragment.getVisibleDialog() instanceof AlertDialog) {
                    addEndDescriptions(((AlertDialog) baseFragment.getVisibleDialog()).getThemeDescriptions());
                }
                z = true;
            }
        }
        if (z) {
            if (!themeAnimationSettings.onlyTopFragment) {
                int size = this.fragmentStack.size() - ((isInPreviewMode() || isPreviewOpenAnimationInProgress()) ? 2 : 1);
                boolean z2 = false;
                for (int i4 = 0; i4 < size; i4++) {
                    BaseFragment baseFragment2 = this.fragmentStack.get(i4);
                    baseFragment2.clearViews();
                    baseFragment2.setParentLayout(this);
                    if (i4 == this.fragmentStack.size() - 1) {
                        if (getForegroundView() != null) {
                            getForegroundView().setFragment(baseFragment2);
                        }
                        z2 = true;
                    } else if (i4 == this.fragmentStack.size() - 2) {
                        if (getBackgroundView() != null) {
                            getBackgroundView().setFragment(baseFragment2);
                        }
                        z2 = true;
                    }
                }
                if (z2) {
                    lambda$rebuildFragments$12(1);
                }
            }
            if (themeAnimationSettings.instant) {
                setThemeAnimationValue(1.0f);
                this.themeAnimatorDescriptions.clear();
                this.animateStartColors.clear();
                this.animateEndColors.clear();
                this.themeAnimatorDelegate.clear();
                this.presentingFragmentDescriptions = null;
                Runnable runnable3 = themeAnimationSettings.afterAnimationRunnable;
                if (runnable3 != null) {
                    runnable3.run();
                }
                if (runnable == null) {
                    return;
                }
                runnable.run();
                return;
            }
            Theme.setAnimatingColor(true);
            Runnable runnable4 = themeAnimationSettings.beforeAnimationRunnable;
            if (runnable4 != null) {
                runnable4.run();
            }
            INavigationLayout.ThemeAnimationSettings.onAnimationProgress onanimationprogress = themeAnimationSettings.animationProgress;
            this.animationProgressListener = onanimationprogress;
            if (onanimationprogress != null) {
                onanimationprogress.setProgress(0.0f);
            }
            if (getBackground() instanceof ColorDrawable) {
                i2 = ((ColorDrawable) getBackground()).getColor();
            }
            this.fromBackgroundColor = i2;
            ValueAnimator duration = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(themeAnimationSettings.duration);
            this.themeAnimator = duration;
            duration.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.LNavigation.LNavigation$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    LNavigation.this.lambda$animateThemedValues$17(valueAnimator);
                }
            });
            this.themeAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.LNavigation.LNavigation.5
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    if (animator.equals(LNavigation.this.themeAnimator)) {
                        LNavigation.this.themeAnimatorDescriptions.clear();
                        LNavigation.this.animateStartColors.clear();
                        LNavigation.this.animateEndColors.clear();
                        LNavigation.this.themeAnimatorDelegate.clear();
                        Theme.setAnimatingColor(false);
                        LNavigation.this.presentingFragmentDescriptions = null;
                        LNavigation.this.themeAnimator = null;
                        Runnable runnable5 = themeAnimationSettings.afterAnimationRunnable;
                        if (runnable5 == null) {
                            return;
                        }
                        runnable5.run();
                    }
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animator) {
                    if (animator.equals(LNavigation.this.themeAnimator)) {
                        LNavigation.this.themeAnimatorDescriptions.clear();
                        LNavigation.this.animateStartColors.clear();
                        LNavigation.this.animateEndColors.clear();
                        LNavigation.this.themeAnimatorDelegate.clear();
                        Theme.setAnimatingColor(false);
                        LNavigation.this.presentingFragmentDescriptions = null;
                        LNavigation.this.themeAnimator = null;
                        Runnable runnable5 = themeAnimationSettings.afterAnimationRunnable;
                        if (runnable5 == null) {
                            return;
                        }
                        runnable5.run();
                    }
                }
            });
            this.themeAnimator.start();
        }
        if (runnable != null) {
            runnable.run();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$animateThemedValues$17(ValueAnimator valueAnimator) {
        setThemeAnimationValue(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    private void setThemeAnimationValue(float f) {
        this.themeAnimationValue = f;
        int size = this.themeAnimatorDescriptions.size();
        for (int i = 0; i < size; i++) {
            ArrayList<ThemeDescription> arrayList = this.themeAnimatorDescriptions.get(i);
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
                int argb = Color.argb(Math.min(255, (int) (alpha2 + ((alpha - alpha2) * f))), Math.min(255, (int) (red2 + ((red - red2) * f))), Math.min(255, (int) (green2 + ((green - green2) * f))), Math.min(255, (int) (blue2 + ((blue - blue2) * f))));
                ThemeDescription themeDescription = arrayList.get(i2);
                themeDescription.setAnimatedColor(argb);
                themeDescription.setColor(argb, false, false);
                i2++;
                iArr = iArr;
                size = i3;
            }
        }
        int size3 = this.themeAnimatorDelegate.size();
        for (int i4 = 0; i4 < size3; i4++) {
            ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = this.themeAnimatorDelegate.get(i4);
            if (themeDescriptionDelegate != null) {
                themeDescriptionDelegate.didSetColor();
                themeDescriptionDelegate.onAnimationProgress(f);
            }
        }
        ArrayList<ThemeDescription> arrayList2 = this.presentingFragmentDescriptions;
        if (arrayList2 != null) {
            int size4 = arrayList2.size();
            for (int i5 = 0; i5 < size4; i5++) {
                ThemeDescription themeDescription2 = this.presentingFragmentDescriptions.get(i5);
                themeDescription2.setColor(Theme.getColor(themeDescription2.getCurrentKey()), false, false);
            }
        }
        INavigationLayout.ThemeAnimationSettings.onAnimationProgress onanimationprogress = this.animationProgressListener;
        if (onanimationprogress != null) {
            onanimationprogress.setProgress(f);
        }
        INavigationLayout.INavigationLayoutDelegate iNavigationLayoutDelegate = this.delegate;
        if (iNavigationLayoutDelegate != null) {
            iNavigationLayoutDelegate.onThemeProgress(f);
        }
        if (this.useAlphaAnimations || !this.useBackground) {
            return;
        }
        setBackgroundColor(ColorUtils.blendARGB(this.fromBackgroundColor, Theme.getColor("windowBackgroundWhite"), f));
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public float getThemeAnimationValue() {
        return this.themeAnimationValue;
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public void setFragmentStackChangedListener(Runnable runnable) {
        this.onFragmentStackChangedListener = runnable;
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public boolean isTransitionAnimationInProgress() {
        return (this.currentSpringAnimation == null && this.customAnimation == null) ? false : true;
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public void setInBubbleMode(boolean z) {
        this.isInBubbleMode = z;
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public boolean isInBubbleMode() {
        return this.isInBubbleMode;
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public boolean isInPreviewMode() {
        return (getLastFragment() != null && getLastFragment().isInPreviewMode()) || this.blurredBackFragmentForPreview != null;
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public boolean isPreviewOpenAnimationInProgress() {
        return isInPreviewMode() && isTransitionAnimationInProgress();
    }

    /* JADX WARN: Removed duplicated region for block: B:19:0x003b  */
    /* JADX WARN: Removed duplicated region for block: B:24:? A[RETURN, SYNTHETIC] */
    @Override // org.telegram.ui.ActionBar.INavigationLayout
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void movePreviewFragment(float r4) {
        /*
            r3 = this;
            boolean r0 = r3.isInPreviewMode()
            if (r0 == 0) goto L45
            org.telegram.ui.ActionBar.ActionBarPopupWindow$ActionBarPopupWindowLayout r0 = r3.previewMenu
            if (r0 != 0) goto L45
            boolean r0 = r3.isTransitionAnimationInProgress()
            if (r0 != 0) goto L45
            org.telegram.ui.LNavigation.LNavigation$FragmentHolderView r0 = r3.getForegroundView()
            if (r0 != 0) goto L17
            goto L45
        L17:
            org.telegram.ui.LNavigation.LNavigation$FragmentHolderView r0 = r3.getForegroundView()
            float r0 = r0.getTranslationY()
            float r4 = -r4
            r1 = 0
            int r2 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
            if (r2 <= 0) goto L27
        L25:
            r4 = 0
            goto L37
        L27:
            r2 = 1114636288(0x42700000, float:60.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r2 = -r2
            float r2 = (float) r2
            int r2 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r2 >= 0) goto L37
            r3.expandPreviewFragment()
            goto L25
        L37:
            int r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r0 == 0) goto L45
            org.telegram.ui.LNavigation.LNavigation$FragmentHolderView r0 = r3.getForegroundView()
            r0.setTranslationY(r4)
            r3.invalidate()
        L45:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LNavigation.LNavigation.movePreviewFragment(float):void");
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public void expandPreviewFragment() {
        if (!isInPreviewMode() || isTransitionAnimationInProgress() || this.fragmentStack.isEmpty()) {
            return;
        }
        try {
            performHapticFeedback(3, 2);
        } catch (Exception unused) {
        }
        final BaseFragment lastFragment = getLastFragment();
        final FragmentHolderView backgroundView = getBackgroundView();
        final FragmentHolderView foregroundView = getForegroundView();
        View fragmentView = lastFragment.getFragmentView();
        this.previewFragmentRect.set(fragmentView.getLeft(), fragmentView.getTop(), fragmentView.getRight(), fragmentView.getBottom());
        this.previewFragmentSnapshot = AndroidUtilities.snapshotView(foregroundView);
        resetViewProperties(foregroundView);
        resetViewProperties(lastFragment.getFragmentView());
        lastFragment.setInPreviewMode(false);
        this.swipeProgress = 0.0f;
        invalidateTranslation();
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.previewMenu;
        final float translationY = actionBarPopupWindowLayout != null ? actionBarPopupWindowLayout.getTranslationY() : 0.0f;
        SpringAnimation spring = new SpringAnimation(new FloatValueHolder(0.0f)).setSpring(new SpringForce(1000.0f).setStiffness(750.0f).setDampingRatio(0.6f));
        this.currentSpringAnimation = spring;
        spring.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() { // from class: org.telegram.ui.LNavigation.LNavigation$$ExternalSyntheticLambda8
            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationUpdateListener
            public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f, float f2) {
                LNavigation.this.lambda$expandPreviewFragment$20(backgroundView, foregroundView, translationY, dynamicAnimation, f, f2);
            }
        });
        this.currentSpringAnimation.addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: org.telegram.ui.LNavigation.LNavigation$$ExternalSyntheticLambda5
            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
            public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                LNavigation.this.lambda$expandPreviewFragment$21(lastFragment, dynamicAnimation, z, f, f2);
            }
        });
        this.currentSpringAnimation.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$expandPreviewFragment$20(View view, View view2, float f, DynamicAnimation dynamicAnimation, float f2, float f3) {
        this.previewExpandProgress = f2 / 1000.0f;
        view.invalidate();
        view2.setPivotX(this.previewFragmentRect.centerX());
        view2.setPivotY(this.previewFragmentRect.centerY());
        view2.setScaleX(AndroidUtilities.lerp(this.previewFragmentRect.width() / view2.getWidth(), 1.0f, this.previewExpandProgress));
        view2.setScaleY(AndroidUtilities.lerp(this.previewFragmentRect.height() / view2.getHeight(), 1.0f, this.previewExpandProgress));
        view2.invalidate();
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.previewMenu;
        if (actionBarPopupWindowLayout != null) {
            actionBarPopupWindowLayout.setTranslationY(AndroidUtilities.lerp(f, getHeight(), this.previewExpandProgress));
        }
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$expandPreviewFragment$21(BaseFragment baseFragment, DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        if (dynamicAnimation == this.currentSpringAnimation) {
            baseFragment.onPreviewOpenAnimationEnd();
            this.previewFragmentSnapshot.recycle();
            this.previewFragmentSnapshot = null;
            Bitmap bitmap = this.blurredBackFragmentForPreview;
            if (bitmap != null) {
                bitmap.recycle();
                this.blurredBackFragmentForPreview = null;
            }
            ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.previewMenu;
            if (actionBarPopupWindowLayout != null && actionBarPopupWindowLayout.getParent() != null) {
                ((ViewGroup) this.previewMenu.getParent()).removeView(this.previewMenu);
            }
            this.previewMenu = null;
            this.previewOpenCallback = null;
            this.previewExpandProgress = 0.0f;
            if (getBackgroundView() != null) {
                getBackgroundView().setVisibility(8);
            }
            this.currentSpringAnimation = null;
        }
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public void finishPreviewFragment() {
        if (isInPreviewMode()) {
            Runnable runnable = new Runnable() { // from class: org.telegram.ui.LNavigation.LNavigation$$ExternalSyntheticLambda13
                @Override // java.lang.Runnable
                public final void run() {
                    LNavigation.this.lambda$finishPreviewFragment$22();
                }
            };
            if (!isTransitionAnimationInProgress()) {
                runnable.run();
            } else {
                this.previewOpenCallback = runnable;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$finishPreviewFragment$22() {
        Runnable runnable = this.delayedPresentAnimation;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.delayedPresentAnimation = null;
        }
        closeLastFragment();
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public void setFragmentPanTranslationOffset(int i) {
        FragmentHolderView foregroundView = getForegroundView();
        if (foregroundView != null) {
            foregroundView.setFragmentPanTranslationOffset(i);
        }
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public ViewGroup getOverlayContainerView() {
        return this.overlayLayout;
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public void setHighlightActionButtons(boolean z) {
        this.highlightActionButtons = z;
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public float getCurrentPreviewFragmentAlpha() {
        if (isInPreviewMode()) {
            return getForegroundView().getAlpha();
        }
        return 0.0f;
    }

    @Override // android.view.ViewGroup
    protected boolean drawChild(Canvas canvas, View view, long j) {
        int indexOfChild = indexOfChild(view);
        DrawerLayoutContainer drawerLayoutContainer = this.drawerLayoutContainer;
        boolean z = false;
        if (drawerLayoutContainer != null && drawerLayoutContainer.isDrawCurrentPreviewFragmentAbove() && isInPreviewMode() && indexOfChild == 1) {
            this.drawerLayoutContainer.invalidate();
            return false;
        }
        if (getChildCount() >= 3 && indexOfChild == 0 && this.customAnimation == null && !isInPreviewMode()) {
            z = true;
        }
        if (z) {
            canvas.save();
            Rect rect = AndroidUtilities.rectTmp2;
            rect.set(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + ((int) (((getWidth() - getPaddingLeft()) - getPaddingRight()) * this.swipeProgress)), getHeight() - getPaddingBottom());
            canvas.clipRect(rect);
        }
        if (indexOfChild == 1 && isInPreviewMode()) {
            drawPreviewDrawables(canvas, (ViewGroup) view);
        }
        boolean drawChild = super.drawChild(canvas, view, j);
        if (indexOfChild == 0 && isInPreviewMode() && this.blurredBackFragmentForPreview != null) {
            canvas.save();
            if (this.previewFragmentSnapshot != null) {
                this.blurPaint.setAlpha((int) ((1.0f - Math.min(this.previewExpandProgress, 1.0f)) * 255.0f));
            } else {
                this.blurPaint.setAlpha((int) ((1.0f - Math.max(this.swipeProgress, 0.0f)) * 255.0f));
            }
            canvas.scale(view.getWidth() / this.blurredBackFragmentForPreview.getWidth(), view.getHeight() / this.blurredBackFragmentForPreview.getHeight());
            canvas.drawBitmap(this.blurredBackFragmentForPreview, 0.0f, 0.0f, this.blurPaint);
            canvas.restore();
        }
        if (z) {
            canvas.restore();
        }
        return drawChild;
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public void drawCurrentPreviewFragment(Canvas canvas, Drawable drawable) {
        if (isInPreviewMode()) {
            FragmentHolderView foregroundView = getForegroundView();
            drawPreviewDrawables(canvas, foregroundView);
            if (foregroundView.getAlpha() < 1.0f) {
                canvas.saveLayerAlpha(0.0f, 0.0f, getWidth(), getHeight(), (int) (foregroundView.getAlpha() * 255.0f), 31);
            } else {
                canvas.save();
            }
            canvas.concat(foregroundView.getMatrix());
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) foregroundView.getLayoutParams();
            canvas.translate(marginLayoutParams.leftMargin, marginLayoutParams.topMargin);
            this.path.rewind();
            RectF rectF = AndroidUtilities.rectTmp;
            rectF.set(0.0f, this.previewExpandProgress != 0.0f ? 0.0f : AndroidUtilities.statusBarHeight, foregroundView.getWidth(), foregroundView.getHeight());
            this.path.addRoundRect(rectF, AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), Path.Direction.CW);
            canvas.clipPath(this.path);
            foregroundView.draw(canvas);
            if (drawable != null) {
                int i = 0;
                View childAt = foregroundView.getChildAt(0);
                if (childAt != null) {
                    ViewGroup.MarginLayoutParams marginLayoutParams2 = (ViewGroup.MarginLayoutParams) childAt.getLayoutParams();
                    Rect rect = new Rect();
                    childAt.getLocalVisibleRect(rect);
                    rect.offset(marginLayoutParams2.leftMargin, marginLayoutParams2.topMargin);
                    int i2 = rect.top;
                    if (Build.VERSION.SDK_INT >= 21) {
                        i = AndroidUtilities.statusBarHeight - 1;
                    }
                    rect.top = i2 + i;
                    drawable.setAlpha((int) (foregroundView.getAlpha() * 255.0f));
                    drawable.setBounds(rect);
                    drawable.draw(canvas);
                }
            }
            canvas.restore();
        }
    }

    private void drawPreviewDrawables(Canvas canvas, ViewGroup viewGroup) {
        int i = 0;
        if (viewGroup.getChildAt(0) != null) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) viewGroup.getLayoutParams();
            float max = 1.0f - Math.max(this.swipeProgress, 0.0f);
            if (this.previewFragmentSnapshot != null) {
                max = 1.0f - Math.min(this.previewExpandProgress, 1.0f);
            }
            canvas.drawColor(Color.argb((int) (46.0f * max), 0, 0, 0));
            if (this.previewMenu != null) {
                return;
            }
            int dp = AndroidUtilities.dp(32.0f);
            int i2 = dp / 2;
            int measuredWidth = (getMeasuredWidth() - dp) / 2;
            float translationY = marginLayoutParams.topMargin + viewGroup.getTranslationY();
            if (Build.VERSION.SDK_INT < 21) {
                i = 20;
            }
            int dp2 = (int) (translationY - AndroidUtilities.dp(i + 12));
            Theme.moveUpDrawable.setAlpha((int) (max * 255.0f));
            Theme.moveUpDrawable.setBounds(measuredWidth, dp2, dp + measuredWidth, i2 + dp2);
            Theme.moveUpDrawable.draw(canvas);
        }
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public void drawHeaderShadow(Canvas canvas, int i, int i2) {
        Drawable drawable = this.headerShadowDrawable;
        if (drawable != null) {
            if (Build.VERSION.SDK_INT >= 19) {
                if (drawable.getAlpha() != i) {
                    this.headerShadowDrawable.setAlpha(i);
                }
            } else {
                drawable.setAlpha(i);
            }
            this.headerShadowDrawable.setBounds(0, i2, getMeasuredWidth(), this.headerShadowDrawable.getIntrinsicHeight() + i2);
            this.headerShadowDrawable.draw(canvas);
        }
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public boolean isSwipeInProgress() {
        return this.isSwipeInProgress;
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public void onPause() {
        BaseFragment lastFragment = getLastFragment();
        if (lastFragment != null) {
            lastFragment.onPause();
        }
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public void onResume() {
        BaseFragment lastFragment = getLastFragment();
        if (lastFragment != null) {
            lastFragment.onResume();
        }
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public void onUserLeaveHint() {
        BaseFragment lastFragment = getLastFragment();
        if (lastFragment != null) {
            lastFragment.onUserLeaveHint();
        }
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public void onLowMemory() {
        for (BaseFragment baseFragment : this.fragmentStack) {
            baseFragment.onLowMemory();
        }
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public void onBackPressed() {
        if (isSwipeInProgress() || checkTransitionAnimation() || this.fragmentStack.isEmpty() || GroupCallPip.onBackPressed()) {
            return;
        }
        if (getCurrentActionBar() != null && !getCurrentActionBar().isActionModeShowed() && getCurrentActionBar().isSearchFieldVisible()) {
            getCurrentActionBar().closeSearchField();
        } else if (!getLastFragment().onBackPressed()) {
        } else {
            closeLastFragment(true);
        }
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public boolean extendActionMode(Menu menu) {
        BaseFragment lastFragment = getLastFragment();
        return lastFragment != null && lastFragment.extendActionMode(menu);
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public void onActionModeStarted(Object obj) {
        if (getCurrentActionBar() != null) {
            getCurrentActionBar().setVisibility(8);
        }
        this.isInActionMode = true;
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public void onActionModeFinished(Object obj) {
        if (getCurrentActionBar() != null) {
            getCurrentActionBar().setVisibility(0);
        }
        this.isInActionMode = false;
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public void startActivityForResult(Intent intent, int i) {
        Activity parentActivity = getParentActivity();
        if (parentActivity == null || intent == null) {
            return;
        }
        parentActivity.startActivityForResult(intent, i);
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public Theme.MessageDrawable getMessageDrawableOutStart() {
        return this.messageDrawableOutStart;
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public Theme.MessageDrawable getMessageDrawableOutMediaStart() {
        return this.messageDrawableOutMediaStart;
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public List<BackButtonMenu.PulledDialog> getPulledDialogs() {
        return this.pulledDialogs;
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public void setPulledDialogs(List<BackButtonMenu.PulledDialog> list) {
        this.pulledDialogs = list;
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        if (i == 82 && !checkTransitionAnimation() && !isSwipeInProgress() && getCurrentActionBar() != null) {
            getCurrentActionBar().onMenuButtonPressed();
        }
        return super.onKeyUp(i, keyEvent);
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        boolean z = View.MeasureSpec.getSize(i2) > View.MeasureSpec.getSize(i);
        if (this.wasPortrait != z && isInPreviewMode()) {
            finishPreviewFragment();
        }
        this.wasPortrait = z;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public final class FragmentHolderView extends FrameLayout {
        private int backgroundColor;
        private Paint backgroundPaint;
        private BaseFragment fragment;
        private int fragmentPanTranslationOffset;

        public FragmentHolderView(Context context) {
            super(context);
            this.backgroundPaint = new Paint();
            setWillNotDraw(false);
        }

        public void invalidateBackgroundColor() {
            BaseFragment baseFragment = this.fragment;
            if (baseFragment == null || baseFragment.hasOwnBackground()) {
                setBackground(null);
            } else {
                setBackgroundColor(this.fragment.getThemedColor("windowBackgroundWhite"));
            }
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int i, int i2) {
            int size = View.MeasureSpec.getSize(i);
            int size2 = View.MeasureSpec.getSize(i2);
            int i3 = 0;
            for (int i4 = 0; i4 < getChildCount(); i4++) {
                View childAt = getChildAt(i4);
                if (childAt instanceof ActionBar) {
                    childAt.measure(i, View.MeasureSpec.makeMeasureSpec(0, 0));
                    i3 = childAt.getMeasuredHeight();
                }
            }
            for (int i5 = 0; i5 < getChildCount(); i5++) {
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

        @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            int i5 = 0;
            for (int i6 = 0; i6 < getChildCount(); i6++) {
                View childAt = getChildAt(i6);
                if (childAt instanceof ActionBar) {
                    childAt.layout(0, 0, childAt.getMeasuredWidth(), childAt.getMeasuredHeight());
                    i5 = childAt.getMeasuredHeight();
                }
            }
            for (int i7 = 0; i7 < getChildCount(); i7++) {
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
        }

        public void setFragmentPanTranslationOffset(int i) {
            this.fragmentPanTranslationOffset = i;
            invalidate();
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (this.fragmentPanTranslationOffset != 0) {
                if (this.backgroundColor != Theme.getColor("windowBackgroundWhite")) {
                    Paint paint = this.backgroundPaint;
                    int color = Theme.getColor("windowBackgroundWhite");
                    this.backgroundColor = color;
                    paint.setColor(color);
                }
                canvas.drawRect(0.0f, (getMeasuredHeight() - this.fragmentPanTranslationOffset) - 3, getMeasuredWidth(), getMeasuredHeight(), this.backgroundPaint);
            }
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void dispatchDraw(Canvas canvas) {
            super.dispatchDraw(canvas);
            this.fragment.drawOverlay(canvas, this);
        }

        @Override // android.view.ViewGroup
        protected boolean drawChild(Canvas canvas, View view, long j) {
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
                    i = childAt.getMeasuredHeight();
                    i2 = (int) childAt.getY();
                }
            }
            i = 0;
            i2 = 0;
            boolean z = indexOfChild(view) == 0 && this.fragment.isInPreviewMode();
            if (z) {
                canvas.save();
                LNavigation.this.path.rewind();
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set(view.getLeft(), view.getTop() + AndroidUtilities.statusBarHeight, view.getRight(), view.getBottom());
                LNavigation.this.path.addRoundRect(rectF, AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), Path.Direction.CW);
                canvas.clipPath(LNavigation.this.path);
            }
            boolean drawChild = super.drawChild(canvas, view, j);
            if (z) {
                canvas.restore();
            }
            if (i != 0 && LNavigation.this.headerShadowDrawable != null) {
                int i4 = i2 + i;
                LNavigation.this.headerShadowDrawable.setBounds(0, i4, getMeasuredWidth(), LNavigation.this.headerShadowDrawable.getIntrinsicHeight() + i4);
                LNavigation.this.headerShadowDrawable.draw(canvas);
            }
            return drawChild;
        }

        public void setFragment(BaseFragment baseFragment) {
            this.fragment = baseFragment;
            this.fragmentPanTranslationOffset = 0;
            invalidate();
            removeAllViews();
            if (baseFragment == null) {
                invalidateBackgroundColor();
                return;
            }
            View fragmentView = baseFragment.getFragmentView();
            if (fragmentView == null) {
                fragmentView = baseFragment.createView(getContext());
                baseFragment.setFragmentView(fragmentView);
            }
            if (fragmentView != null && (fragmentView.getParent() instanceof ViewGroup)) {
                ((ViewGroup) fragmentView.getParent()).removeView(fragmentView);
            }
            addView(fragmentView);
            if (LNavigation.this.removeActionBarExtraHeight) {
                baseFragment.getActionBar().setOccupyStatusBar(false);
            }
            if (baseFragment.getActionBar() != null && baseFragment.getActionBar().shouldAddToContainer()) {
                ViewGroup viewGroup = (ViewGroup) baseFragment.getActionBar().getParent();
                if (viewGroup != null) {
                    viewGroup.removeView(baseFragment.getActionBar());
                }
                addView(baseFragment.getActionBar());
            }
            invalidateBackgroundColor();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isIgnoredView(ViewGroup viewGroup, MotionEvent motionEvent, Rect rect) {
        if (viewGroup == null) {
            return false;
        }
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View childAt = viewGroup.getChildAt(i);
            if (isIgnoredView0(childAt, motionEvent, rect)) {
                return true;
            }
            if ((childAt instanceof ViewGroup) && isIgnoredView((ViewGroup) childAt, motionEvent, rect)) {
                return true;
            }
        }
        return isIgnoredView0(viewGroup, motionEvent, rect);
    }

    private boolean isIgnoredView0(View view, MotionEvent motionEvent, Rect rect) {
        view.getGlobalVisibleRect(rect);
        if (view.getVisibility() != 0 || !rect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
            return false;
        }
        return view instanceof ViewPager ? ((ViewPager) view).getCurrentItem() != 0 : view.canScrollHorizontally(-1) || (view instanceof SeekBarView);
    }
}
