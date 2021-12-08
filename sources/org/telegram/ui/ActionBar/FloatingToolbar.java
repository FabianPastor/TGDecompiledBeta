package org.telegram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Outline;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.util.Size;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils$$ExternalSyntheticBackport0;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.Theme;

public final class FloatingToolbar {
    private static final MenuItem.OnMenuItemClickListener NO_OP_MENUITEM_CLICK_LISTENER = FloatingToolbar$$ExternalSyntheticLambda0.INSTANCE;
    public static final int STYLE_BLACK = 2;
    public static final int STYLE_DIALOG = 0;
    public static final int STYLE_THEME = 1;
    /* access modifiers changed from: private */
    public int currentStyle;
    private final Rect mContentRect = new Rect();
    private Menu mMenu;
    private MenuItem.OnMenuItemClickListener mMenuItemClickListener = NO_OP_MENUITEM_CLICK_LISTENER;
    private final Comparator<MenuItem> mMenuItemComparator = FloatingToolbar$$ExternalSyntheticLambda1.INSTANCE;
    private final View.OnLayoutChangeListener mOrientationChangeHandler = new View.OnLayoutChangeListener() {
        private final Rect mNewRect = new Rect();
        private final Rect mOldRect = new Rect();

        public void onLayoutChange(View view, int newLeft, int newRight, int newTop, int newBottom, int oldLeft, int oldRight, int oldTop, int oldBottom) {
            this.mNewRect.set(newLeft, newRight, newTop, newBottom);
            this.mOldRect.set(oldLeft, oldRight, oldTop, oldBottom);
            if (FloatingToolbar.this.mPopup.isShowing() && !this.mNewRect.equals(this.mOldRect)) {
                boolean unused = FloatingToolbar.this.mWidthChanged = true;
                FloatingToolbar.this.updateLayout();
            }
        }
    };
    /* access modifiers changed from: private */
    public final FloatingToolbarPopup mPopup;
    private final Rect mPreviousContentRect = new Rect();
    private List<MenuItem> mShowingMenuItems = new ArrayList();
    private int mSuggestedWidth;
    /* access modifiers changed from: private */
    public boolean mWidthChanged = true;
    private final View mWindowView;
    private final Theme.ResourcesProvider resourcesProvider;

    static /* synthetic */ boolean lambda$static$0(MenuItem item) {
        return false;
    }

    static /* synthetic */ int lambda$new$1(MenuItem menuItem1, MenuItem menuItem2) {
        return menuItem1.getOrder() - menuItem2.getOrder();
    }

    public FloatingToolbar(Context context, View windowView, int style, Theme.ResourcesProvider resourcesProvider2) {
        this.mWindowView = windowView;
        this.currentStyle = style;
        this.resourcesProvider = resourcesProvider2;
        this.mPopup = new FloatingToolbarPopup(context, windowView);
    }

    public FloatingToolbar setMenu(Menu menu) {
        this.mMenu = menu;
        return this;
    }

    public FloatingToolbar setOnMenuItemClickListener(MenuItem.OnMenuItemClickListener menuItemClickListener) {
        if (menuItemClickListener != null) {
            this.mMenuItemClickListener = menuItemClickListener;
        } else {
            this.mMenuItemClickListener = NO_OP_MENUITEM_CLICK_LISTENER;
        }
        return this;
    }

    public FloatingToolbar setContentRect(Rect rect) {
        this.mContentRect.set(rect);
        return this;
    }

    public FloatingToolbar setSuggestedWidth(int suggestedWidth) {
        double d = (double) this.mSuggestedWidth;
        Double.isNaN(d);
        this.mWidthChanged = ((double) Math.abs(suggestedWidth - this.mSuggestedWidth)) > d * 0.2d;
        this.mSuggestedWidth = suggestedWidth;
        return this;
    }

    public FloatingToolbar show() {
        registerOrientationHandler();
        doShow();
        return this;
    }

    public FloatingToolbar updateLayout() {
        if (this.mPopup.isShowing()) {
            doShow();
        }
        return this;
    }

    public void dismiss() {
        unregisterOrientationHandler();
        this.mPopup.dismiss();
    }

    public void hide() {
        this.mPopup.hide();
    }

    public boolean isShowing() {
        return this.mPopup.isShowing();
    }

    public boolean isHidden() {
        return this.mPopup.isHidden();
    }

    public void setOutsideTouchable(boolean outsideTouchable, PopupWindow.OnDismissListener onDismiss) {
        if (this.mPopup.setOutsideTouchable(outsideTouchable, onDismiss) && isShowing()) {
            dismiss();
            doShow();
        }
    }

    private void doShow() {
        List<MenuItem> menuItems = getVisibleAndEnabledMenuItems(this.mMenu);
        Collections.sort(menuItems, this.mMenuItemComparator);
        if (!isCurrentlyShowing(menuItems) || this.mWidthChanged) {
            this.mPopup.dismiss();
            this.mPopup.layoutMenuItems(menuItems, this.mMenuItemClickListener, this.mSuggestedWidth);
            this.mShowingMenuItems = menuItems;
        }
        if (!this.mPopup.isShowing()) {
            this.mPopup.show(this.mContentRect);
        } else if (!this.mPreviousContentRect.equals(this.mContentRect)) {
            this.mPopup.updateCoordinates(this.mContentRect);
        }
        this.mWidthChanged = false;
        this.mPreviousContentRect.set(this.mContentRect);
    }

    private boolean isCurrentlyShowing(List<MenuItem> menuItems) {
        if (this.mShowingMenuItems == null || menuItems.size() != this.mShowingMenuItems.size()) {
            return false;
        }
        int size = menuItems.size();
        for (int i = 0; i < size; i++) {
            MenuItem menuItem = menuItems.get(i);
            MenuItem showingItem = this.mShowingMenuItems.get(i);
            if (menuItem.getItemId() != showingItem.getItemId() || !TextUtils.equals(menuItem.getTitle(), showingItem.getTitle()) || !ColorUtils$$ExternalSyntheticBackport0.m(menuItem.getIcon(), showingItem.getIcon()) || menuItem.getGroupId() != showingItem.getGroupId()) {
                return false;
            }
        }
        return true;
    }

    private List<MenuItem> getVisibleAndEnabledMenuItems(Menu menu) {
        List<MenuItem> menuItems = new ArrayList<>();
        int i = 0;
        while (menu != null && i < menu.size()) {
            MenuItem menuItem = menu.getItem(i);
            if (menuItem.isVisible() && menuItem.isEnabled()) {
                Menu subMenu = menuItem.getSubMenu();
                if (subMenu != null) {
                    menuItems.addAll(getVisibleAndEnabledMenuItems(subMenu));
                } else {
                    menuItems.add(menuItem);
                }
            }
            i++;
        }
        return menuItems;
    }

    private void registerOrientationHandler() {
        unregisterOrientationHandler();
        this.mWindowView.addOnLayoutChangeListener(this.mOrientationChangeHandler);
    }

    private void unregisterOrientationHandler() {
        this.mWindowView.removeOnLayoutChangeListener(this.mOrientationChangeHandler);
    }

    private final class FloatingToolbarPopup {
        private static final int MAX_OVERFLOW_SIZE = 4;
        private static final int MIN_OVERFLOW_SIZE = 2;
        private final Drawable mArrow;
        private final AnimationSet mCloseOverflowAnimation;
        /* access modifiers changed from: private */
        public final ViewGroup mContentContainer;
        /* access modifiers changed from: private */
        public final Context mContext;
        private final Point mCoordsOnWindow = new Point();
        private final AnimatorSet mDismissAnimation;
        private boolean mDismissed = true;
        private final Interpolator mFastOutLinearInInterpolator;
        private final Interpolator mFastOutSlowInInterpolator;
        private boolean mHidden;
        private final AnimatorSet mHideAnimation;
        private final int mIconTextSpacing;
        private boolean mIsOverflowOpen;
        private final int mLineHeight;
        private final Interpolator mLinearOutSlowInInterpolator;
        private final Interpolator mLogAccelerateInterpolator;
        /* access modifiers changed from: private */
        public final ViewGroup mMainPanel;
        /* access modifiers changed from: private */
        public Size mMainPanelSize;
        private final int mMarginHorizontal;
        private final int mMarginVertical;
        private final View.OnClickListener mMenuItemButtonOnClickListener = new View.OnClickListener() {
            public void onClick(View v) {
                if ((v.getTag() instanceof MenuItem) && FloatingToolbarPopup.this.mOnMenuItemClickListener != null) {
                    FloatingToolbarPopup.this.mOnMenuItemClickListener.onMenuItemClick((MenuItem) v.getTag());
                }
            }
        };
        /* access modifiers changed from: private */
        public MenuItem.OnMenuItemClickListener mOnMenuItemClickListener;
        private final AnimationSet mOpenOverflowAnimation;
        /* access modifiers changed from: private */
        public boolean mOpenOverflowUpwards;
        private final Drawable mOverflow;
        /* access modifiers changed from: private */
        public final ImageButton mOverflowButton;
        /* access modifiers changed from: private */
        public final Size mOverflowButtonSize;
        /* access modifiers changed from: private */
        public final OverflowPanel mOverflowPanel;
        /* access modifiers changed from: private */
        public Size mOverflowPanelSize;
        /* access modifiers changed from: private */
        public final OverflowPanelViewHelper mOverflowPanelViewHelper;
        private final View mParent;
        /* access modifiers changed from: private */
        public final PopupWindow mPopupWindow;
        private final Runnable mPreparePopupContentRTLHelper = new Runnable() {
            public void run() {
                FloatingToolbarPopup.this.setPanelsStatesAtRestingPosition();
                FloatingToolbarPopup.this.setContentAreaAsTouchableSurface();
                FloatingToolbarPopup.this.mContentContainer.setAlpha(1.0f);
            }
        };
        private final AnimatorSet mShowAnimation;
        private final int[] mTmpCoords = new int[2];
        private final AnimatedVectorDrawable mToArrow;
        private final AnimatedVectorDrawable mToOverflow;
        private final Region mTouchableRegion = new Region();
        private int mTransitionDurationScale;
        private final Rect mViewPortOnScreen = new Rect();

        public FloatingToolbarPopup(Context context, View parent) {
            this.mParent = parent;
            this.mContext = context;
            ViewGroup access$600 = FloatingToolbar.this.createContentContainer(context);
            this.mContentContainer = access$600;
            this.mPopupWindow = FloatingToolbar.createPopupWindow(access$600);
            this.mMarginHorizontal = AndroidUtilities.dp(16.0f);
            this.mMarginVertical = AndroidUtilities.dp(8.0f);
            this.mLineHeight = AndroidUtilities.dp(48.0f);
            int dp = AndroidUtilities.dp(8.0f);
            this.mIconTextSpacing = dp;
            this.mLogAccelerateInterpolator = new LogAccelerateInterpolator();
            this.mFastOutSlowInInterpolator = AnimationUtils.loadInterpolator(context, 17563661);
            this.mLinearOutSlowInInterpolator = AnimationUtils.loadInterpolator(context, 17563662);
            this.mFastOutLinearInInterpolator = AnimationUtils.loadInterpolator(context, 17563663);
            Drawable mutate = context.getDrawable(NUM).mutate();
            this.mArrow = mutate;
            mutate.setAutoMirrored(true);
            Drawable mutate2 = context.getDrawable(NUM).mutate();
            this.mOverflow = mutate2;
            mutate2.setAutoMirrored(true);
            AnimatedVectorDrawable animatedVectorDrawable = (AnimatedVectorDrawable) context.getDrawable(NUM).mutate();
            this.mToArrow = animatedVectorDrawable;
            animatedVectorDrawable.setAutoMirrored(true);
            AnimatedVectorDrawable animatedVectorDrawable2 = (AnimatedVectorDrawable) context.getDrawable(NUM).mutate();
            this.mToOverflow = animatedVectorDrawable2;
            animatedVectorDrawable2.setAutoMirrored(true);
            ImageButton createOverflowButton = createOverflowButton();
            this.mOverflowButton = createOverflowButton;
            this.mOverflowButtonSize = measure(createOverflowButton);
            this.mMainPanel = createMainPanel();
            this.mOverflowPanelViewHelper = new OverflowPanelViewHelper(context, dp);
            this.mOverflowPanel = createOverflowPanel();
            Animation.AnimationListener mOverflowAnimationListener = createOverflowAnimationListener();
            AnimationSet animationSet = new AnimationSet(true);
            this.mOpenOverflowAnimation = animationSet;
            animationSet.setAnimationListener(mOverflowAnimationListener);
            AnimationSet animationSet2 = new AnimationSet(true);
            this.mCloseOverflowAnimation = animationSet2;
            animationSet2.setAnimationListener(mOverflowAnimationListener);
            this.mShowAnimation = FloatingToolbar.createEnterAnimation(access$600);
            this.mDismissAnimation = FloatingToolbar.createExitAnimation(access$600, 150, new AnimatorListenerAdapter(FloatingToolbar.this) {
                public void onAnimationEnd(Animator animation) {
                    NotificationCenter.getInstance(UserConfig.selectedAccount).doOnIdle(new FloatingToolbar$FloatingToolbarPopup$3$$ExternalSyntheticLambda0(this));
                }

                /* renamed from: lambda$onAnimationEnd$0$org-telegram-ui-ActionBar-FloatingToolbar$FloatingToolbarPopup$3  reason: not valid java name */
                public /* synthetic */ void m1330xe3adaCLASSNAME() {
                    FloatingToolbarPopup.this.mPopupWindow.dismiss();
                    FloatingToolbarPopup.this.mContentContainer.removeAllViews();
                }
            });
            this.mHideAnimation = FloatingToolbar.createExitAnimation(access$600, 0, new AnimatorListenerAdapter(FloatingToolbar.this) {
                public void onAnimationEnd(Animator animation) {
                    NotificationCenter.getInstance(UserConfig.selectedAccount).doOnIdle(new FloatingToolbar$FloatingToolbarPopup$4$$ExternalSyntheticLambda0(this));
                }

                /* renamed from: lambda$onAnimationEnd$0$org-telegram-ui-ActionBar-FloatingToolbar$FloatingToolbarPopup$4  reason: not valid java name */
                public /* synthetic */ void m1331xe3adaCLASSNAME() {
                    FloatingToolbarPopup.this.mPopupWindow.dismiss();
                }
            });
        }

        public boolean setOutsideTouchable(boolean outsideTouchable, PopupWindow.OnDismissListener onDismiss) {
            boolean ret = false;
            if (this.mPopupWindow.isOutsideTouchable() ^ outsideTouchable) {
                this.mPopupWindow.setOutsideTouchable(outsideTouchable);
                this.mPopupWindow.setFocusable(!outsideTouchable);
                ret = true;
            }
            this.mPopupWindow.setOnDismissListener(onDismiss);
            return ret;
        }

        public void layoutMenuItems(List<MenuItem> menuItems, MenuItem.OnMenuItemClickListener menuItemClickListener, int suggestedWidth) {
            this.mOnMenuItemClickListener = menuItemClickListener;
            cancelOverflowAnimations();
            clearPanels();
            List<MenuItem> menuItems2 = layoutMainPanelItems(menuItems, getAdjustedToolbarWidth(suggestedWidth));
            if (!menuItems2.isEmpty()) {
                layoutOverflowPanelItems(menuItems2);
            }
            updatePopupSize();
        }

        public void show(Rect contentRectOnScreen) {
            if (!isShowing()) {
                this.mHidden = false;
                this.mDismissed = false;
                cancelDismissAndHideAnimations();
                cancelOverflowAnimations();
                refreshCoordinatesAndOverflowDirection(contentRectOnScreen);
                preparePopupContent();
                this.mPopupWindow.showAtLocation(this.mParent, 0, this.mCoordsOnWindow.x, this.mCoordsOnWindow.y);
                setTouchableSurfaceInsetsComputer();
                runShowAnimation();
            }
        }

        public void dismiss() {
            if (!this.mDismissed) {
                this.mHidden = false;
                this.mDismissed = true;
                this.mHideAnimation.cancel();
                runDismissAnimation();
                setZeroTouchableSurface();
            }
        }

        public void hide() {
            if (isShowing()) {
                this.mHidden = true;
                runHideAnimation();
                setZeroTouchableSurface();
            }
        }

        public boolean isShowing() {
            return !this.mDismissed && !this.mHidden;
        }

        public boolean isHidden() {
            return this.mHidden;
        }

        public void updateCoordinates(Rect contentRectOnScreen) {
            if (isShowing() && this.mPopupWindow.isShowing()) {
                cancelOverflowAnimations();
                refreshCoordinatesAndOverflowDirection(contentRectOnScreen);
                preparePopupContent();
                this.mPopupWindow.update(this.mCoordsOnWindow.x, this.mCoordsOnWindow.y, this.mPopupWindow.getWidth(), this.mPopupWindow.getHeight());
            }
        }

        private void refreshCoordinatesAndOverflowDirection(Rect contentRectOnScreen) {
            int minimumOverflowHeightWithMargin;
            Rect rect = contentRectOnScreen;
            refreshViewPort();
            int x = Math.min(contentRectOnScreen.centerX() - (this.mPopupWindow.getWidth() / 2), this.mViewPortOnScreen.right - this.mPopupWindow.getWidth());
            int availableHeightAboveContent = rect.top - this.mViewPortOnScreen.top;
            int availableHeightBelowContent = this.mViewPortOnScreen.bottom - rect.bottom;
            int margin = this.mMarginVertical * 2;
            int toolbarHeightWithVerticalMargin = this.mLineHeight + margin;
            if (hasOverflow()) {
                int minimumOverflowHeightWithMargin2 = calculateOverflowHeight(2) + margin;
                int availableHeightThroughContentDown = (this.mViewPortOnScreen.bottom - rect.top) + toolbarHeightWithVerticalMargin;
                int availableHeightThroughContentUp = (rect.bottom - this.mViewPortOnScreen.top) + toolbarHeightWithVerticalMargin;
                if (availableHeightAboveContent >= minimumOverflowHeightWithMargin2) {
                    updateOverflowHeight(availableHeightAboveContent - margin);
                    this.mOpenOverflowUpwards = true;
                    minimumOverflowHeightWithMargin = rect.top - this.mPopupWindow.getHeight();
                } else if (availableHeightAboveContent >= toolbarHeightWithVerticalMargin && availableHeightThroughContentDown >= minimumOverflowHeightWithMargin2) {
                    updateOverflowHeight(availableHeightThroughContentDown - margin);
                    this.mOpenOverflowUpwards = false;
                    minimumOverflowHeightWithMargin = rect.top - toolbarHeightWithVerticalMargin;
                } else if (availableHeightBelowContent >= minimumOverflowHeightWithMargin2) {
                    updateOverflowHeight(availableHeightBelowContent - margin);
                    int y = rect.bottom;
                    this.mOpenOverflowUpwards = false;
                    minimumOverflowHeightWithMargin = y;
                } else if (availableHeightBelowContent < toolbarHeightWithVerticalMargin || this.mViewPortOnScreen.height() < minimumOverflowHeightWithMargin2) {
                    updateOverflowHeight(this.mViewPortOnScreen.height() - margin);
                    int y2 = this.mViewPortOnScreen.top;
                    this.mOpenOverflowUpwards = false;
                    minimumOverflowHeightWithMargin = y2;
                } else {
                    updateOverflowHeight(availableHeightThroughContentUp - margin);
                    this.mOpenOverflowUpwards = true;
                    minimumOverflowHeightWithMargin = (rect.bottom + toolbarHeightWithVerticalMargin) - this.mPopupWindow.getHeight();
                }
            } else if (availableHeightAboveContent >= toolbarHeightWithVerticalMargin) {
                minimumOverflowHeightWithMargin = rect.top - toolbarHeightWithVerticalMargin;
            } else if (availableHeightBelowContent >= toolbarHeightWithVerticalMargin) {
                minimumOverflowHeightWithMargin = rect.bottom;
            } else if (availableHeightBelowContent >= this.mLineHeight) {
                minimumOverflowHeightWithMargin = rect.bottom - this.mMarginVertical;
            } else {
                minimumOverflowHeightWithMargin = Math.max(this.mViewPortOnScreen.top, rect.top - toolbarHeightWithVerticalMargin);
            }
            this.mParent.getRootView().getLocationOnScreen(this.mTmpCoords);
            int[] iArr = this.mTmpCoords;
            int rootViewLeftOnScreen = iArr[0];
            int rootViewTopOnScreen = iArr[1];
            this.mParent.getRootView().getLocationInWindow(this.mTmpCoords);
            int[] iArr2 = this.mTmpCoords;
            this.mCoordsOnWindow.set(Math.max(0, x - (rootViewLeftOnScreen - iArr2[0])), Math.max(0, minimumOverflowHeightWithMargin - (rootViewTopOnScreen - iArr2[1])));
        }

        private void runShowAnimation() {
            this.mShowAnimation.start();
        }

        private void runDismissAnimation() {
            this.mDismissAnimation.start();
        }

        private void runHideAnimation() {
            this.mHideAnimation.start();
        }

        private void cancelDismissAndHideAnimations() {
            this.mDismissAnimation.cancel();
            this.mHideAnimation.cancel();
        }

        private void cancelOverflowAnimations() {
            this.mContentContainer.clearAnimation();
            this.mMainPanel.animate().cancel();
            this.mOverflowPanel.animate().cancel();
            this.mToArrow.stop();
            this.mToOverflow.stop();
        }

        private void openOverflow() {
            int targetWidth = this.mOverflowPanelSize.getWidth();
            final int targetHeight = this.mOverflowPanelSize.getHeight();
            final int startWidth = this.mContentContainer.getWidth();
            final int startHeight = this.mContentContainer.getHeight();
            final float startY = this.mContentContainer.getY();
            float left = this.mContentContainer.getX();
            final int i = targetWidth;
            final int i2 = startWidth;
            final float f = left;
            final float width = left + ((float) this.mContentContainer.getWidth());
            Animation widthAnimation = new Animation() {
                /* access modifiers changed from: protected */
                public void applyTransformation(float interpolatedTime, Transformation t) {
                    FloatingToolbarPopup floatingToolbarPopup = FloatingToolbarPopup.this;
                    floatingToolbarPopup.setWidth(floatingToolbarPopup.mContentContainer, i2 + ((int) (((float) (i - i2)) * interpolatedTime)));
                    if (FloatingToolbarPopup.this.isInRTLMode()) {
                        FloatingToolbarPopup.this.mContentContainer.setX(f);
                        FloatingToolbarPopup.this.mMainPanel.setX(0.0f);
                        FloatingToolbarPopup.this.mOverflowPanel.setX(0.0f);
                        return;
                    }
                    FloatingToolbarPopup.this.mContentContainer.setX(width - ((float) FloatingToolbarPopup.this.mContentContainer.getWidth()));
                    FloatingToolbarPopup.this.mMainPanel.setX((float) (FloatingToolbarPopup.this.mContentContainer.getWidth() - i2));
                    FloatingToolbarPopup.this.mOverflowPanel.setX((float) (FloatingToolbarPopup.this.mContentContainer.getWidth() - i));
                }
            };
            Animation heightAnimation = new Animation() {
                /* access modifiers changed from: protected */
                public void applyTransformation(float interpolatedTime, Transformation t) {
                    FloatingToolbarPopup floatingToolbarPopup = FloatingToolbarPopup.this;
                    floatingToolbarPopup.setHeight(floatingToolbarPopup.mContentContainer, startHeight + ((int) (((float) (targetHeight - startHeight)) * interpolatedTime)));
                    if (FloatingToolbarPopup.this.mOpenOverflowUpwards) {
                        FloatingToolbarPopup.this.mContentContainer.setY(startY - ((float) (FloatingToolbarPopup.this.mContentContainer.getHeight() - startHeight)));
                        FloatingToolbarPopup.this.positionContentYCoordinatesIfOpeningOverflowUpwards();
                    }
                }
            };
            final float overflowButtonStartX = this.mOverflowButton.getX();
            final float overflowButtonTargetX = isInRTLMode() ? (((float) targetWidth) + overflowButtonStartX) - ((float) this.mOverflowButton.getWidth()) : (overflowButtonStartX - ((float) targetWidth)) + ((float) this.mOverflowButton.getWidth());
            Animation overflowButtonAnimation = new Animation() {
                /* access modifiers changed from: protected */
                public void applyTransformation(float interpolatedTime, Transformation t) {
                    float f = overflowButtonStartX;
                    FloatingToolbarPopup.this.mOverflowButton.setX(f + ((overflowButtonTargetX - f) * interpolatedTime) + (FloatingToolbarPopup.this.isInRTLMode() ? 0.0f : (float) (FloatingToolbarPopup.this.mContentContainer.getWidth() - startWidth)));
                }
            };
            widthAnimation.setInterpolator(this.mLogAccelerateInterpolator);
            widthAnimation.setDuration((long) getAdjustedDuration(250));
            heightAnimation.setInterpolator(this.mFastOutSlowInInterpolator);
            heightAnimation.setDuration((long) getAdjustedDuration(250));
            overflowButtonAnimation.setInterpolator(this.mFastOutSlowInInterpolator);
            overflowButtonAnimation.setDuration((long) getAdjustedDuration(250));
            this.mOpenOverflowAnimation.getAnimations().clear();
            this.mOpenOverflowAnimation.addAnimation(widthAnimation);
            this.mOpenOverflowAnimation.addAnimation(heightAnimation);
            this.mOpenOverflowAnimation.addAnimation(overflowButtonAnimation);
            this.mContentContainer.startAnimation(this.mOpenOverflowAnimation);
            this.mIsOverflowOpen = true;
            this.mMainPanel.animate().alpha(0.0f).withLayer().setInterpolator(this.mLinearOutSlowInInterpolator).setDuration(250).start();
            this.mOverflowPanel.setAlpha(1.0f);
        }

        private void closeOverflow() {
            int targetWidth = this.mMainPanelSize.getWidth();
            final int startWidth = this.mContentContainer.getWidth();
            float left = this.mContentContainer.getX();
            final int i = targetWidth;
            final int i2 = startWidth;
            final float f = left;
            final float width = left + ((float) this.mContentContainer.getWidth());
            Animation widthAnimation = new Animation() {
                /* access modifiers changed from: protected */
                public void applyTransformation(float interpolatedTime, Transformation t) {
                    FloatingToolbarPopup floatingToolbarPopup = FloatingToolbarPopup.this;
                    floatingToolbarPopup.setWidth(floatingToolbarPopup.mContentContainer, i2 + ((int) (((float) (i - i2)) * interpolatedTime)));
                    if (FloatingToolbarPopup.this.isInRTLMode()) {
                        FloatingToolbarPopup.this.mContentContainer.setX(f);
                        FloatingToolbarPopup.this.mMainPanel.setX(0.0f);
                        FloatingToolbarPopup.this.mOverflowPanel.setX(0.0f);
                        return;
                    }
                    FloatingToolbarPopup.this.mContentContainer.setX(width - ((float) FloatingToolbarPopup.this.mContentContainer.getWidth()));
                    FloatingToolbarPopup.this.mMainPanel.setX((float) (FloatingToolbarPopup.this.mContentContainer.getWidth() - i));
                    FloatingToolbarPopup.this.mOverflowPanel.setX((float) (FloatingToolbarPopup.this.mContentContainer.getWidth() - i2));
                }
            };
            final int targetHeight = this.mMainPanelSize.getHeight();
            final int startHeight = this.mContentContainer.getHeight();
            final float bottom = this.mContentContainer.getY() + ((float) this.mContentContainer.getHeight());
            Animation heightAnimation = new Animation() {
                /* access modifiers changed from: protected */
                public void applyTransformation(float interpolatedTime, Transformation t) {
                    FloatingToolbarPopup floatingToolbarPopup = FloatingToolbarPopup.this;
                    floatingToolbarPopup.setHeight(floatingToolbarPopup.mContentContainer, startHeight + ((int) (((float) (targetHeight - startHeight)) * interpolatedTime)));
                    if (FloatingToolbarPopup.this.mOpenOverflowUpwards) {
                        FloatingToolbarPopup.this.mContentContainer.setY(bottom - ((float) FloatingToolbarPopup.this.mContentContainer.getHeight()));
                        FloatingToolbarPopup.this.positionContentYCoordinatesIfOpeningOverflowUpwards();
                    }
                }
            };
            final float overflowButtonStartX = this.mOverflowButton.getX();
            final float overflowButtonTargetX = isInRTLMode() ? (overflowButtonStartX - ((float) startWidth)) + ((float) this.mOverflowButton.getWidth()) : (((float) startWidth) + overflowButtonStartX) - ((float) this.mOverflowButton.getWidth());
            Animation overflowButtonAnimation = new Animation() {
                /* access modifiers changed from: protected */
                public void applyTransformation(float interpolatedTime, Transformation t) {
                    float f = overflowButtonStartX;
                    FloatingToolbarPopup.this.mOverflowButton.setX(f + ((overflowButtonTargetX - f) * interpolatedTime) + (FloatingToolbarPopup.this.isInRTLMode() ? 0.0f : (float) (FloatingToolbarPopup.this.mContentContainer.getWidth() - startWidth)));
                }
            };
            widthAnimation.setInterpolator(this.mFastOutSlowInInterpolator);
            widthAnimation.setDuration((long) getAdjustedDuration(250));
            heightAnimation.setInterpolator(this.mLogAccelerateInterpolator);
            heightAnimation.setDuration((long) getAdjustedDuration(250));
            overflowButtonAnimation.setInterpolator(this.mFastOutSlowInInterpolator);
            overflowButtonAnimation.setDuration((long) getAdjustedDuration(250));
            this.mCloseOverflowAnimation.getAnimations().clear();
            this.mCloseOverflowAnimation.addAnimation(widthAnimation);
            this.mCloseOverflowAnimation.addAnimation(heightAnimation);
            this.mCloseOverflowAnimation.addAnimation(overflowButtonAnimation);
            this.mContentContainer.startAnimation(this.mCloseOverflowAnimation);
            this.mIsOverflowOpen = false;
            this.mMainPanel.animate().alpha(1.0f).withLayer().setInterpolator(this.mFastOutLinearInInterpolator).setDuration(100).start();
            this.mOverflowPanel.animate().alpha(0.0f).withLayer().setInterpolator(this.mLinearOutSlowInInterpolator).setDuration(150).start();
        }

        /* access modifiers changed from: private */
        public void setPanelsStatesAtRestingPosition() {
            this.mOverflowButton.setEnabled(true);
            this.mOverflowPanel.awakenScrollBars();
            if (this.mIsOverflowOpen) {
                Size containerSize = this.mOverflowPanelSize;
                setSize(this.mContentContainer, containerSize);
                this.mMainPanel.setAlpha(0.0f);
                this.mMainPanel.setVisibility(4);
                this.mOverflowPanel.setAlpha(1.0f);
                this.mOverflowPanel.setVisibility(0);
                this.mOverflowButton.setImageDrawable(this.mArrow);
                this.mOverflowButton.setContentDescription(LocaleController.getString("AccDescrMoreOptions", NUM));
                if (isInRTLMode()) {
                    this.mContentContainer.setX((float) this.mMarginHorizontal);
                    this.mMainPanel.setX(0.0f);
                    this.mOverflowButton.setX((float) (containerSize.getWidth() - this.mOverflowButtonSize.getWidth()));
                    this.mOverflowPanel.setX(0.0f);
                } else {
                    this.mContentContainer.setX((float) ((this.mPopupWindow.getWidth() - containerSize.getWidth()) - this.mMarginHorizontal));
                    this.mMainPanel.setX(-this.mContentContainer.getX());
                    this.mOverflowButton.setX(0.0f);
                    this.mOverflowPanel.setX(0.0f);
                }
                if (this.mOpenOverflowUpwards) {
                    this.mContentContainer.setY((float) this.mMarginVertical);
                    this.mMainPanel.setY((float) (containerSize.getHeight() - this.mContentContainer.getHeight()));
                    this.mOverflowButton.setY((float) (containerSize.getHeight() - this.mOverflowButtonSize.getHeight()));
                    this.mOverflowPanel.setY(0.0f);
                    return;
                }
                this.mContentContainer.setY((float) this.mMarginVertical);
                this.mMainPanel.setY(0.0f);
                this.mOverflowButton.setY(0.0f);
                this.mOverflowPanel.setY((float) this.mOverflowButtonSize.getHeight());
                return;
            }
            Size containerSize2 = this.mMainPanelSize;
            setSize(this.mContentContainer, containerSize2);
            this.mMainPanel.setAlpha(1.0f);
            this.mMainPanel.setVisibility(0);
            this.mOverflowPanel.setAlpha(0.0f);
            this.mOverflowPanel.setVisibility(4);
            this.mOverflowButton.setImageDrawable(this.mOverflow);
            this.mOverflowButton.setContentDescription(LocaleController.getString("AccDescrMoreOptions", NUM));
            if (hasOverflow()) {
                if (isInRTLMode()) {
                    this.mContentContainer.setX((float) this.mMarginHorizontal);
                    this.mMainPanel.setX(0.0f);
                    this.mOverflowButton.setX(0.0f);
                    this.mOverflowPanel.setX(0.0f);
                } else {
                    this.mContentContainer.setX((float) ((this.mPopupWindow.getWidth() - containerSize2.getWidth()) - this.mMarginHorizontal));
                    this.mMainPanel.setX(0.0f);
                    this.mOverflowButton.setX((float) (containerSize2.getWidth() - this.mOverflowButtonSize.getWidth()));
                    this.mOverflowPanel.setX((float) (containerSize2.getWidth() - this.mOverflowPanelSize.getWidth()));
                }
                if (this.mOpenOverflowUpwards) {
                    this.mContentContainer.setY((float) ((this.mMarginVertical + this.mOverflowPanelSize.getHeight()) - containerSize2.getHeight()));
                    this.mMainPanel.setY(0.0f);
                    this.mOverflowButton.setY(0.0f);
                    this.mOverflowPanel.setY((float) (containerSize2.getHeight() - this.mOverflowPanelSize.getHeight()));
                    return;
                }
                this.mContentContainer.setY((float) this.mMarginVertical);
                this.mMainPanel.setY(0.0f);
                this.mOverflowButton.setY(0.0f);
                this.mOverflowPanel.setY((float) this.mOverflowButtonSize.getHeight());
                return;
            }
            this.mContentContainer.setX((float) this.mMarginHorizontal);
            this.mContentContainer.setY((float) this.mMarginVertical);
            this.mMainPanel.setX(0.0f);
            this.mMainPanel.setY(0.0f);
        }

        private void updateOverflowHeight(int suggestedHeight) {
            if (hasOverflow()) {
                int newHeight = calculateOverflowHeight((suggestedHeight - this.mOverflowButtonSize.getHeight()) / this.mLineHeight);
                if (this.mOverflowPanelSize.getHeight() != newHeight) {
                    this.mOverflowPanelSize = new Size(this.mOverflowPanelSize.getWidth(), newHeight);
                }
                setSize(this.mOverflowPanel, this.mOverflowPanelSize);
                if (this.mIsOverflowOpen) {
                    setSize(this.mContentContainer, this.mOverflowPanelSize);
                    if (this.mOpenOverflowUpwards) {
                        int deltaHeight = this.mOverflowPanelSize.getHeight() - newHeight;
                        ViewGroup viewGroup = this.mContentContainer;
                        viewGroup.setY(viewGroup.getY() + ((float) deltaHeight));
                        ImageButton imageButton = this.mOverflowButton;
                        imageButton.setY(imageButton.getY() - ((float) deltaHeight));
                    }
                } else {
                    setSize(this.mContentContainer, this.mMainPanelSize);
                }
                updatePopupSize();
            }
        }

        private void updatePopupSize() {
            int width = 0;
            int height = 0;
            Size size = this.mMainPanelSize;
            if (size != null) {
                width = Math.max(0, size.getWidth());
                height = Math.max(0, this.mMainPanelSize.getHeight());
            }
            Size size2 = this.mOverflowPanelSize;
            if (size2 != null) {
                width = Math.max(width, size2.getWidth());
                height = Math.max(height, this.mOverflowPanelSize.getHeight());
            }
            this.mPopupWindow.setWidth((this.mMarginHorizontal * 2) + width);
            this.mPopupWindow.setHeight((this.mMarginVertical * 2) + height);
            maybeComputeTransitionDurationScale();
        }

        private void refreshViewPort() {
            this.mParent.getWindowVisibleDisplayFrame(this.mViewPortOnScreen);
        }

        private int getAdjustedToolbarWidth(int suggestedWidth) {
            int width = suggestedWidth;
            refreshViewPort();
            int maximumWidth = this.mViewPortOnScreen.width() - (AndroidUtilities.dp(16.0f) * 2);
            if (width <= 0) {
                width = AndroidUtilities.dp(400.0f);
            }
            return Math.min(width, maximumWidth);
        }

        private void setZeroTouchableSurface() {
            this.mTouchableRegion.setEmpty();
        }

        /* access modifiers changed from: private */
        public void setContentAreaAsTouchableSurface() {
            int height;
            int width;
            if (this.mIsOverflowOpen) {
                width = this.mOverflowPanelSize.getWidth();
                height = this.mOverflowPanelSize.getHeight();
            } else {
                width = this.mMainPanelSize.getWidth();
                height = this.mMainPanelSize.getHeight();
            }
            this.mTouchableRegion.set((int) this.mContentContainer.getX(), (int) this.mContentContainer.getY(), ((int) this.mContentContainer.getX()) + width, ((int) this.mContentContainer.getY()) + height);
        }

        private void setTouchableSurfaceInsetsComputer() {
        }

        /* access modifiers changed from: private */
        public boolean isInRTLMode() {
            return false;
        }

        private boolean hasOverflow() {
            return this.mOverflowPanelSize != null;
        }

        public List<MenuItem> layoutMainPanelItems(List<MenuItem> menuItems, int toolbarWidth) {
            int availableWidth = toolbarWidth;
            LinkedList<MenuItem> remainingMenuItems = new LinkedList<>(menuItems);
            this.mMainPanel.removeAllViews();
            this.mMainPanel.setPaddingRelative(0, 0, 0, 0);
            boolean isFirstItem = true;
            while (true) {
                if (remainingMenuItems.isEmpty()) {
                    int i = toolbarWidth;
                    break;
                }
                MenuItem menuItem = remainingMenuItems.peek();
                View menuItemButton = FloatingToolbar.this.createMenuItemButton(this.mContext, menuItem, this.mIconTextSpacing);
                if (menuItemButton instanceof LinearLayout) {
                    ((LinearLayout) menuItemButton).setGravity(17);
                }
                if (isFirstItem) {
                    double paddingStart = (double) menuItemButton.getPaddingStart();
                    Double.isNaN(paddingStart);
                    menuItemButton.setPaddingRelative((int) (paddingStart * 1.5d), menuItemButton.getPaddingTop(), menuItemButton.getPaddingEnd(), menuItemButton.getPaddingBottom());
                }
                boolean canFitNoOverflow = true;
                boolean isLastItem = remainingMenuItems.size() == 1;
                if (isLastItem) {
                    int paddingStart2 = menuItemButton.getPaddingStart();
                    int paddingTop = menuItemButton.getPaddingTop();
                    double paddingEnd = (double) menuItemButton.getPaddingEnd();
                    Double.isNaN(paddingEnd);
                    menuItemButton.setPaddingRelative(paddingStart2, paddingTop, (int) (paddingEnd * 1.5d), menuItemButton.getPaddingBottom());
                }
                menuItemButton.measure(0, 0);
                int menuItemButtonWidth = Math.min(menuItemButton.getMeasuredWidth(), toolbarWidth);
                boolean canFitWithOverflow = menuItemButtonWidth <= availableWidth - this.mOverflowButtonSize.getWidth();
                if (!isLastItem || menuItemButtonWidth > availableWidth) {
                    canFitNoOverflow = false;
                }
                if (!canFitWithOverflow && !canFitNoOverflow) {
                    break;
                }
                setButtonTagAndClickListener(menuItemButton, menuItem);
                this.mMainPanel.addView(menuItemButton);
                ViewGroup.LayoutParams params = menuItemButton.getLayoutParams();
                params.width = menuItemButtonWidth;
                menuItemButton.setLayoutParams(params);
                availableWidth -= menuItemButtonWidth;
                remainingMenuItems.pop();
                isFirstItem = false;
            }
            if (!remainingMenuItems.isEmpty()) {
                this.mMainPanel.setPaddingRelative(0, 0, this.mOverflowButtonSize.getWidth(), 0);
            }
            this.mMainPanelSize = measure(this.mMainPanel);
            return remainingMenuItems;
        }

        private void layoutOverflowPanelItems(List<MenuItem> menuItems) {
            ArrayAdapter<MenuItem> overflowPanelAdapter = (ArrayAdapter) this.mOverflowPanel.getAdapter();
            overflowPanelAdapter.clear();
            int size = menuItems.size();
            for (int i = 0; i < size; i++) {
                overflowPanelAdapter.add(menuItems.get(i));
            }
            this.mOverflowPanel.setAdapter(overflowPanelAdapter);
            if (this.mOpenOverflowUpwards) {
                this.mOverflowPanel.setY(0.0f);
            } else {
                this.mOverflowPanel.setY((float) this.mOverflowButtonSize.getHeight());
            }
            Size size2 = new Size(Math.max(getOverflowWidth(), this.mOverflowButtonSize.getWidth()), calculateOverflowHeight(4));
            this.mOverflowPanelSize = size2;
            setSize(this.mOverflowPanel, size2);
        }

        private void preparePopupContent() {
            this.mContentContainer.removeAllViews();
            if (hasOverflow()) {
                this.mContentContainer.addView(this.mOverflowPanel);
            }
            this.mContentContainer.addView(this.mMainPanel);
            if (hasOverflow()) {
                this.mContentContainer.addView(this.mOverflowButton);
            }
            setPanelsStatesAtRestingPosition();
            setContentAreaAsTouchableSurface();
            if (isInRTLMode()) {
                this.mContentContainer.setAlpha(0.0f);
                this.mContentContainer.post(this.mPreparePopupContentRTLHelper);
            }
        }

        private void clearPanels() {
            this.mOverflowPanelSize = null;
            this.mMainPanelSize = null;
            this.mIsOverflowOpen = false;
            this.mMainPanel.removeAllViews();
            ArrayAdapter<MenuItem> overflowPanelAdapter = (ArrayAdapter) this.mOverflowPanel.getAdapter();
            overflowPanelAdapter.clear();
            this.mOverflowPanel.setAdapter(overflowPanelAdapter);
            this.mContentContainer.removeAllViews();
        }

        /* access modifiers changed from: private */
        public void positionContentYCoordinatesIfOpeningOverflowUpwards() {
            if (this.mOpenOverflowUpwards) {
                this.mMainPanel.setY((float) (this.mContentContainer.getHeight() - this.mMainPanelSize.getHeight()));
                this.mOverflowButton.setY((float) (this.mContentContainer.getHeight() - this.mOverflowButton.getHeight()));
                this.mOverflowPanel.setY((float) (this.mContentContainer.getHeight() - this.mOverflowPanelSize.getHeight()));
            }
        }

        private int getOverflowWidth() {
            int overflowWidth = 0;
            int count = this.mOverflowPanel.getAdapter().getCount();
            for (int i = 0; i < count; i++) {
                overflowWidth = Math.max(this.mOverflowPanelViewHelper.calculateWidth((MenuItem) this.mOverflowPanel.getAdapter().getItem(i)), overflowWidth);
            }
            return overflowWidth;
        }

        private int calculateOverflowHeight(int maxItemSize) {
            int actualSize = Math.min(4, Math.min(Math.max(2, maxItemSize), this.mOverflowPanel.getCount()));
            int extension = 0;
            if (actualSize < this.mOverflowPanel.getCount()) {
                extension = (int) (((float) this.mLineHeight) * 0.5f);
            }
            return (this.mLineHeight * actualSize) + this.mOverflowButtonSize.getHeight() + extension;
        }

        private void setButtonTagAndClickListener(View menuItemButton, MenuItem menuItem) {
            menuItemButton.setTag(menuItem);
            menuItemButton.setOnClickListener(this.mMenuItemButtonOnClickListener);
        }

        private int getAdjustedDuration(int originalDuration) {
            int i = this.mTransitionDurationScale;
            if (i < 150) {
                return Math.max(originalDuration - 50, 0);
            }
            if (i > 300) {
                return originalDuration + 50;
            }
            return originalDuration;
        }

        private void maybeComputeTransitionDurationScale() {
            Size size = this.mMainPanelSize;
            if (size != null && this.mOverflowPanelSize != null) {
                int w = size.getWidth() - this.mOverflowPanelSize.getWidth();
                int h = this.mOverflowPanelSize.getHeight() - this.mMainPanelSize.getHeight();
                double sqrt = Math.sqrt((double) ((w * w) + (h * h)));
                double d = (double) this.mContentContainer.getContext().getResources().getDisplayMetrics().density;
                Double.isNaN(d);
                this.mTransitionDurationScale = (int) (sqrt / d);
            }
        }

        private ViewGroup createMainPanel() {
            return new LinearLayout(this.mContext) {
                /* access modifiers changed from: protected */
                public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    if (FloatingToolbarPopup.this.isOverflowAnimating() && FloatingToolbarPopup.this.mMainPanelSize != null) {
                        widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(FloatingToolbarPopup.this.mMainPanelSize.getWidth(), NUM);
                    }
                    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                }

                public boolean onInterceptTouchEvent(MotionEvent ev) {
                    return FloatingToolbarPopup.this.isOverflowAnimating();
                }
            };
        }

        private ImageButton createOverflowButton() {
            int color;
            ImageButton overflowButton = new ImageButton(this.mContext);
            overflowButton.setLayoutParams(new ViewGroup.LayoutParams(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(48.0f)));
            overflowButton.setPaddingRelative(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(12.0f));
            overflowButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            overflowButton.setImageDrawable(this.mOverflow);
            if (FloatingToolbar.this.currentStyle == 0) {
                color = FloatingToolbar.this.getThemedColor("dialogTextBlack");
                overflowButton.setBackgroundDrawable(Theme.createSelectorDrawable(FloatingToolbar.this.getThemedColor("listSelectorSDK21"), 1));
            } else if (FloatingToolbar.this.currentStyle == 2) {
                color = -328966;
                overflowButton.setBackgroundDrawable(Theme.createSelectorDrawable(NUM, 1));
            } else {
                color = FloatingToolbar.this.getThemedColor("windowBackgroundWhiteBlackText");
                overflowButton.setBackgroundDrawable(Theme.createSelectorDrawable(FloatingToolbar.this.getThemedColor("listSelectorSDK21"), 1));
            }
            this.mOverflow.setTint(color);
            this.mArrow.setTint(color);
            this.mToArrow.setTint(color);
            this.mToOverflow.setTint(color);
            overflowButton.setOnClickListener(new FloatingToolbar$FloatingToolbarPopup$$ExternalSyntheticLambda0(this, overflowButton));
            return overflowButton;
        }

        /* renamed from: lambda$createOverflowButton$0$org-telegram-ui-ActionBar-FloatingToolbar$FloatingToolbarPopup  reason: not valid java name */
        public /* synthetic */ void m1327xCLASSNAMEd917(ImageButton overflowButton, View v) {
            if (this.mIsOverflowOpen) {
                overflowButton.setImageDrawable(this.mToOverflow);
                this.mToOverflow.start();
                closeOverflow();
                return;
            }
            overflowButton.setImageDrawable(this.mToArrow);
            this.mToArrow.start();
            openOverflow();
        }

        private OverflowPanel createOverflowPanel() {
            OverflowPanel overflowPanel = new OverflowPanel(this);
            overflowPanel.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
            overflowPanel.setDivider((Drawable) null);
            overflowPanel.setDividerHeight(0);
            overflowPanel.setAdapter(new ArrayAdapter<MenuItem>(this.mContext, 0) {
                public View getView(int position, View convertView, ViewGroup parent) {
                    return FloatingToolbarPopup.this.mOverflowPanelViewHelper.getView((MenuItem) getItem(position), FloatingToolbarPopup.this.mOverflowPanelSize.getWidth(), convertView);
                }
            });
            overflowPanel.setOnItemClickListener(new FloatingToolbar$FloatingToolbarPopup$$ExternalSyntheticLambda1(this, overflowPanel));
            return overflowPanel;
        }

        /* renamed from: lambda$createOverflowPanel$1$org-telegram-ui-ActionBar-FloatingToolbar$FloatingToolbarPopup  reason: not valid java name */
        public /* synthetic */ void m1328x73cfc3fa(OverflowPanel overflowPanel, AdapterView parent, View view, int position, long id) {
            MenuItem menuItem = (MenuItem) overflowPanel.getAdapter().getItem(position);
            MenuItem.OnMenuItemClickListener onMenuItemClickListener = this.mOnMenuItemClickListener;
            if (onMenuItemClickListener != null) {
                onMenuItemClickListener.onMenuItemClick(menuItem);
            }
        }

        /* access modifiers changed from: private */
        public boolean isOverflowAnimating() {
            boolean overflowOpening = this.mOpenOverflowAnimation.hasStarted() && !this.mOpenOverflowAnimation.hasEnded();
            boolean overflowClosing = this.mCloseOverflowAnimation.hasStarted() && !this.mCloseOverflowAnimation.hasEnded();
            if (overflowOpening || overflowClosing) {
                return true;
            }
            return false;
        }

        private Animation.AnimationListener createOverflowAnimationListener() {
            return new Animation.AnimationListener() {
                public void onAnimationStart(Animation animation) {
                    FloatingToolbarPopup.this.mOverflowButton.setEnabled(false);
                    FloatingToolbarPopup.this.mMainPanel.setVisibility(0);
                    FloatingToolbarPopup.this.mOverflowPanel.setVisibility(0);
                }

                public void onAnimationEnd(Animation animation) {
                    FloatingToolbarPopup.this.mContentContainer.post(new FloatingToolbar$FloatingToolbarPopup$13$$ExternalSyntheticLambda0(this));
                }

                /* renamed from: lambda$onAnimationEnd$0$org-telegram-ui-ActionBar-FloatingToolbar$FloatingToolbarPopup$13  reason: not valid java name */
                public /* synthetic */ void m1329x9207d7d5() {
                    FloatingToolbarPopup.this.setPanelsStatesAtRestingPosition();
                    FloatingToolbarPopup.this.setContentAreaAsTouchableSurface();
                }

                public void onAnimationRepeat(Animation animation) {
                }
            };
        }

        private Size measure(View view) {
            view.measure(0, 0);
            return new Size(view.getMeasuredWidth(), view.getMeasuredHeight());
        }

        private void setSize(View view, int width, int height) {
            view.setMinimumWidth(width);
            view.setMinimumHeight(height);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            ViewGroup.LayoutParams params2 = params == null ? new ViewGroup.LayoutParams(0, 0) : params;
            params2.width = width;
            params2.height = height;
            view.setLayoutParams(params2);
        }

        private void setSize(View view, Size size) {
            setSize(view, size.getWidth(), size.getHeight());
        }

        /* access modifiers changed from: private */
        public void setWidth(View view, int width) {
            setSize(view, width, view.getLayoutParams().height);
        }

        /* access modifiers changed from: private */
        public void setHeight(View view, int height) {
            setSize(view, view.getLayoutParams().width, height);
        }

        private final class OverflowPanel extends ListView {
            private final FloatingToolbarPopup mPopup;

            OverflowPanel(FloatingToolbarPopup popup) {
                super(popup.mContext);
                this.mPopup = popup;
                setVerticalScrollBarEnabled(false);
                setOutlineProvider(new ViewOutlineProvider(FloatingToolbarPopup.this) {
                    public void getOutline(View view, Outline outline) {
                        outline.setRoundRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight(), (float) AndroidUtilities.dp(6.0f));
                    }
                });
                setClipToOutline(true);
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(this.mPopup.mOverflowPanelSize.getHeight() - this.mPopup.mOverflowButtonSize.getHeight(), NUM));
            }

            public boolean dispatchTouchEvent(MotionEvent ev) {
                if (this.mPopup.isOverflowAnimating()) {
                    return true;
                }
                return super.dispatchTouchEvent(ev);
            }

            /* access modifiers changed from: protected */
            public boolean awakenScrollBars() {
                return super.awakenScrollBars();
            }
        }

        private final class LogAccelerateInterpolator implements Interpolator {
            private final int BASE;
            private final float LOGS_SCALE;

            private LogAccelerateInterpolator() {
                this.BASE = 100;
                this.LOGS_SCALE = 1.0f / computeLog(1.0f, 100);
            }

            private float computeLog(float t, int base) {
                return (float) (1.0d - Math.pow((double) base, (double) (-t)));
            }

            public float getInterpolation(float t) {
                return 1.0f - (computeLog(1.0f - t, 100) * this.LOGS_SCALE);
            }
        }

        private final class OverflowPanelViewHelper {
            private final View mCalculator = createMenuButton((MenuItem) null);
            private final Context mContext;
            private final int mIconTextSpacing;
            private final int mSidePadding = AndroidUtilities.dp(18.0f);

            public OverflowPanelViewHelper(Context context, int iconTextSpacing) {
                this.mContext = context;
                this.mIconTextSpacing = iconTextSpacing;
            }

            public View getView(MenuItem menuItem, int minimumWidth, View convertView) {
                if (convertView != null) {
                    FloatingToolbar.updateMenuItemButton(convertView, menuItem, this.mIconTextSpacing);
                } else {
                    convertView = createMenuButton(menuItem);
                }
                convertView.setMinimumWidth(minimumWidth);
                return convertView;
            }

            public int calculateWidth(MenuItem menuItem) {
                FloatingToolbar.updateMenuItemButton(this.mCalculator, menuItem, this.mIconTextSpacing);
                this.mCalculator.measure(0, 0);
                return this.mCalculator.getMeasuredWidth();
            }

            private View createMenuButton(MenuItem menuItem) {
                View button = FloatingToolbar.this.createMenuItemButton(this.mContext, menuItem, this.mIconTextSpacing);
                int i = this.mSidePadding;
                button.setPadding(i, 0, i, 0);
                return button;
            }
        }
    }

    /* access modifiers changed from: private */
    public View createMenuItemButton(Context context, MenuItem menuItem, int iconTextSpacing) {
        LinearLayout menuItemButton = new LinearLayout(context);
        menuItemButton.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
        menuItemButton.setOrientation(0);
        menuItemButton.setMinimumWidth(AndroidUtilities.dp(48.0f));
        menuItemButton.setMinimumHeight(AndroidUtilities.dp(48.0f));
        menuItemButton.setPaddingRelative(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
        TextView textView = new TextView(context);
        textView.setGravity(17);
        textView.setSingleLine(true);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textView.setTextSize(1, 14.0f);
        textView.setFocusable(false);
        textView.setImportantForAccessibility(2);
        textView.setFocusableInTouchMode(false);
        int i = this.currentStyle;
        if (i == 0) {
            textView.setTextColor(getThemedColor("dialogTextBlack"));
            menuItemButton.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        } else if (i == 2) {
            textView.setTextColor(-328966);
            menuItemButton.setBackgroundDrawable(Theme.getSelectorDrawable(NUM, false));
        } else if (i == 1) {
            textView.setTextColor(getThemedColor("windowBackgroundWhiteBlackText"));
            menuItemButton.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        }
        textView.setPaddingRelative(AndroidUtilities.dp(11.0f), 0, 0, 0);
        menuItemButton.addView(textView, new LinearLayout.LayoutParams(-2, AndroidUtilities.dp(48.0f)));
        if (menuItem != null) {
            updateMenuItemButton(menuItemButton, menuItem, iconTextSpacing);
        }
        return menuItemButton;
    }

    /* access modifiers changed from: private */
    public static void updateMenuItemButton(View menuItemButton, MenuItem menuItem, int iconTextSpacing) {
        TextView buttonText = (TextView) ((ViewGroup) menuItemButton).getChildAt(0);
        buttonText.setEllipsize((TextUtils.TruncateAt) null);
        if (TextUtils.isEmpty(menuItem.getTitle())) {
            buttonText.setVisibility(8);
        } else {
            buttonText.setVisibility(0);
            buttonText.setText(menuItem.getTitle());
        }
        buttonText.setPaddingRelative(0, 0, 0, 0);
    }

    /* access modifiers changed from: private */
    public ViewGroup createContentContainer(Context context) {
        RelativeLayout contentContainer = new RelativeLayout(context);
        ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(-2, -2);
        int dp = AndroidUtilities.dp(20.0f);
        layoutParams.rightMargin = dp;
        layoutParams.topMargin = dp;
        layoutParams.leftMargin = dp;
        layoutParams.bottomMargin = dp;
        contentContainer.setLayoutParams(layoutParams);
        contentContainer.setElevation((float) AndroidUtilities.dp(2.0f));
        contentContainer.setFocusable(true);
        contentContainer.setFocusableInTouchMode(true);
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(0);
        int r = AndroidUtilities.dp(6.0f);
        shape.setCornerRadii(new float[]{(float) r, (float) r, (float) r, (float) r, (float) r, (float) r, (float) r, (float) r});
        int i = this.currentStyle;
        if (i == 0) {
            shape.setColor(getThemedColor("dialogBackground"));
        } else if (i == 2) {
            shape.setColor(-NUM);
        } else if (i == 1) {
            shape.setColor(getThemedColor("windowBackgroundWhite"));
        }
        contentContainer.setBackgroundDrawable(shape);
        contentContainer.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
        contentContainer.setClipToOutline(true);
        return contentContainer;
    }

    /* access modifiers changed from: private */
    public int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }

    /* access modifiers changed from: private */
    public static PopupWindow createPopupWindow(ViewGroup content) {
        ViewGroup popupContentHolder = new LinearLayout(content.getContext());
        PopupWindow popupWindow = new PopupWindow(popupContentHolder);
        popupWindow.setClippingEnabled(false);
        popupWindow.setAnimationStyle(0);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0));
        content.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
        popupContentHolder.addView(content);
        return popupWindow;
    }

    /* access modifiers changed from: private */
    public static AnimatorSet createEnterAnimation(View view) {
        AnimatorSet animation = new AnimatorSet();
        animation.playTogether(new Animator[]{ObjectAnimator.ofFloat(view, View.ALPHA, new float[]{0.0f, 1.0f}).setDuration(150)});
        return animation;
    }

    /* access modifiers changed from: private */
    public static AnimatorSet createExitAnimation(View view, int startDelay, Animator.AnimatorListener listener) {
        AnimatorSet animation = new AnimatorSet();
        animation.playTogether(new Animator[]{ObjectAnimator.ofFloat(view, View.ALPHA, new float[]{1.0f, 0.0f}).setDuration(100)});
        animation.setStartDelay((long) startDelay);
        animation.addListener(listener);
        return animation;
    }
}
