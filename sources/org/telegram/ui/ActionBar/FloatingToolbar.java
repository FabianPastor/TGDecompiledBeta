package org.telegram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
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
import android.view.SubMenu;
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
import androidx.core.util.ObjectsCompat$$ExternalSynthetic0;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.FloatingToolbar;

@TargetApi(23)
public final class FloatingToolbar {
    private static final MenuItem.OnMenuItemClickListener NO_OP_MENUITEM_CLICK_LISTENER = $$Lambda$FloatingToolbar$UGQARNO28FBZ0TeffXeMTHJ8TdA.INSTANCE;
    /* access modifiers changed from: private */
    public int currentStyle;
    private final Rect mContentRect = new Rect();
    private Menu mMenu;
    private MenuItem.OnMenuItemClickListener mMenuItemClickListener = NO_OP_MENUITEM_CLICK_LISTENER;
    private final Comparator<MenuItem> mMenuItemComparator = $$Lambda$FloatingToolbar$NbZiCM1EM2S6HZEOEjuQwJRChQ.INSTANCE;
    private final View.OnLayoutChangeListener mOrientationChangeHandler = new View.OnLayoutChangeListener() {
        private final Rect mNewRect = new Rect();
        private final Rect mOldRect = new Rect();

        public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
            this.mNewRect.set(i, i2, i3, i4);
            this.mOldRect.set(i5, i6, i7, i8);
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

    static /* synthetic */ boolean lambda$static$0(MenuItem menuItem) {
        return false;
    }

    static /* synthetic */ int lambda$new$1(MenuItem menuItem, MenuItem menuItem2) {
        return menuItem.getOrder() - menuItem2.getOrder();
    }

    public FloatingToolbar(Context context, View view, int i) {
        this.mWindowView = view;
        this.currentStyle = i;
        this.mPopup = new FloatingToolbarPopup(context, view);
    }

    public FloatingToolbar setMenu(Menu menu) {
        this.mMenu = menu;
        return this;
    }

    public FloatingToolbar setOnMenuItemClickListener(MenuItem.OnMenuItemClickListener onMenuItemClickListener) {
        if (onMenuItemClickListener != null) {
            this.mMenuItemClickListener = onMenuItemClickListener;
        } else {
            this.mMenuItemClickListener = NO_OP_MENUITEM_CLICK_LISTENER;
        }
        return this;
    }

    public FloatingToolbar setContentRect(Rect rect) {
        this.mContentRect.set(rect);
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

    private void doShow() {
        List<MenuItem> visibleAndEnabledMenuItems = getVisibleAndEnabledMenuItems(this.mMenu);
        Collections.sort(visibleAndEnabledMenuItems, this.mMenuItemComparator);
        if (!isCurrentlyShowing(visibleAndEnabledMenuItems) || this.mWidthChanged) {
            this.mPopup.dismiss();
            this.mPopup.layoutMenuItems(visibleAndEnabledMenuItems, this.mMenuItemClickListener, this.mSuggestedWidth);
            this.mShowingMenuItems = visibleAndEnabledMenuItems;
        }
        if (!this.mPopup.isShowing()) {
            this.mPopup.show(this.mContentRect);
        } else if (!this.mPreviousContentRect.equals(this.mContentRect)) {
            this.mPopup.updateCoordinates(this.mContentRect);
        }
        this.mWidthChanged = false;
        this.mPreviousContentRect.set(this.mContentRect);
    }

    private boolean isCurrentlyShowing(List<MenuItem> list) {
        if (this.mShowingMenuItems == null || list.size() != this.mShowingMenuItems.size()) {
            return false;
        }
        int size = list.size();
        for (int i = 0; i < size; i++) {
            MenuItem menuItem = list.get(i);
            MenuItem menuItem2 = this.mShowingMenuItems.get(i);
            if (menuItem.getItemId() != menuItem2.getItemId() || !TextUtils.equals(menuItem.getTitle(), menuItem2.getTitle()) || !ObjectsCompat$$ExternalSynthetic0.m0(menuItem.getIcon(), menuItem2.getIcon()) || menuItem.getGroupId() != menuItem2.getGroupId()) {
                return false;
            }
        }
        return true;
    }

    private List<MenuItem> getVisibleAndEnabledMenuItems(Menu menu) {
        ArrayList arrayList = new ArrayList();
        int i = 0;
        while (menu != null && i < menu.size()) {
            MenuItem item = menu.getItem(i);
            if (item.isVisible() && item.isEnabled()) {
                SubMenu subMenu = item.getSubMenu();
                if (subMenu != null) {
                    arrayList.addAll(getVisibleAndEnabledMenuItems(subMenu));
                } else {
                    arrayList.add(item);
                }
            }
            i++;
        }
        return arrayList;
    }

    private void registerOrientationHandler() {
        unregisterOrientationHandler();
        this.mWindowView.addOnLayoutChangeListener(this.mOrientationChangeHandler);
    }

    private void unregisterOrientationHandler() {
        this.mWindowView.removeOnLayoutChangeListener(this.mOrientationChangeHandler);
    }

    private final class FloatingToolbarPopup {
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
            public void onClick(View view) {
                if ((view.getTag() instanceof MenuItem) && FloatingToolbarPopup.this.mOnMenuItemClickListener != null) {
                    FloatingToolbarPopup.this.mOnMenuItemClickListener.onMenuItemClick((MenuItem) view.getTag());
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

        /* access modifiers changed from: private */
        public boolean isInRTLMode() {
            return false;
        }

        private void setTouchableSurfaceInsetsComputer() {
        }

        public FloatingToolbarPopup(Context context, View view) {
            this.mParent = view;
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
            Animation.AnimationListener createOverflowAnimationListener = createOverflowAnimationListener();
            AnimationSet animationSet = new AnimationSet(true);
            this.mOpenOverflowAnimation = animationSet;
            animationSet.setAnimationListener(createOverflowAnimationListener);
            AnimationSet animationSet2 = new AnimationSet(true);
            this.mCloseOverflowAnimation = animationSet2;
            animationSet2.setAnimationListener(createOverflowAnimationListener);
            this.mShowAnimation = FloatingToolbar.createEnterAnimation(access$600);
            this.mDismissAnimation = FloatingToolbar.createExitAnimation(access$600, 150, new AnimatorListenerAdapter(FloatingToolbar.this) {
                public void onAnimationEnd(Animator animator) {
                    FloatingToolbarPopup.this.mPopupWindow.dismiss();
                    FloatingToolbarPopup.this.mContentContainer.removeAllViews();
                }
            });
            this.mHideAnimation = FloatingToolbar.createExitAnimation(access$600, 0, new AnimatorListenerAdapter(FloatingToolbar.this) {
                public void onAnimationEnd(Animator animator) {
                    FloatingToolbarPopup.this.mPopupWindow.dismiss();
                }
            });
        }

        public void layoutMenuItems(List<MenuItem> list, MenuItem.OnMenuItemClickListener onMenuItemClickListener, int i) {
            this.mOnMenuItemClickListener = onMenuItemClickListener;
            cancelOverflowAnimations();
            clearPanels();
            List<MenuItem> layoutMainPanelItems = layoutMainPanelItems(list, getAdjustedToolbarWidth(i));
            if (!layoutMainPanelItems.isEmpty()) {
                layoutOverflowPanelItems(layoutMainPanelItems);
            }
            updatePopupSize();
        }

        public void show(Rect rect) {
            if (!isShowing()) {
                this.mHidden = false;
                this.mDismissed = false;
                cancelDismissAndHideAnimations();
                cancelOverflowAnimations();
                refreshCoordinatesAndOverflowDirection(rect);
                preparePopupContent();
                PopupWindow popupWindow = this.mPopupWindow;
                View view = this.mParent;
                Point point = this.mCoordsOnWindow;
                popupWindow.showAtLocation(view, 0, point.x, point.y);
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

        public void updateCoordinates(Rect rect) {
            if (isShowing() && this.mPopupWindow.isShowing()) {
                cancelOverflowAnimations();
                refreshCoordinatesAndOverflowDirection(rect);
                preparePopupContent();
                PopupWindow popupWindow = this.mPopupWindow;
                Point point = this.mCoordsOnWindow;
                popupWindow.update(point.x, point.y, popupWindow.getWidth(), this.mPopupWindow.getHeight());
            }
        }

        private void refreshCoordinatesAndOverflowDirection(Rect rect) {
            int i;
            refreshViewPort();
            int min = Math.min(rect.centerX() - (this.mPopupWindow.getWidth() / 2), this.mViewPortOnScreen.right - this.mPopupWindow.getWidth());
            int i2 = rect.top;
            Rect rect2 = this.mViewPortOnScreen;
            int i3 = i2 - rect2.top;
            int i4 = rect2.bottom - rect.bottom;
            int i5 = this.mMarginVertical * 2;
            int i6 = this.mLineHeight + i5;
            if (hasOverflow()) {
                int calculateOverflowHeight = calculateOverflowHeight(2) + i5;
                Rect rect3 = this.mViewPortOnScreen;
                int i7 = (rect3.bottom - rect.top) + i6;
                int i8 = (rect.bottom - rect3.top) + i6;
                if (i3 >= calculateOverflowHeight) {
                    updateOverflowHeight(i3 - i5);
                    i = rect.top - this.mPopupWindow.getHeight();
                    this.mOpenOverflowUpwards = true;
                } else if (i3 >= i6 && i7 >= calculateOverflowHeight) {
                    updateOverflowHeight(i7 - i5);
                    i = rect.top - i6;
                    this.mOpenOverflowUpwards = false;
                } else if (i4 >= calculateOverflowHeight) {
                    updateOverflowHeight(i4 - i5);
                    i = rect.bottom;
                    this.mOpenOverflowUpwards = false;
                } else if (i4 < i6 || rect3.height() < calculateOverflowHeight) {
                    updateOverflowHeight(this.mViewPortOnScreen.height() - i5);
                    i = this.mViewPortOnScreen.top;
                    this.mOpenOverflowUpwards = false;
                } else {
                    updateOverflowHeight(i8 - i5);
                    i = (rect.bottom + i6) - this.mPopupWindow.getHeight();
                    this.mOpenOverflowUpwards = true;
                }
            } else if (i3 >= i6) {
                i = rect.top - i6;
            } else if (i4 >= i6) {
                i = rect.bottom;
            } else if (i4 >= this.mLineHeight) {
                i = rect.bottom - this.mMarginVertical;
            } else {
                i = Math.max(this.mViewPortOnScreen.top, rect.top - i6);
            }
            this.mParent.getRootView().getLocationOnScreen(this.mTmpCoords);
            int[] iArr = this.mTmpCoords;
            int i9 = iArr[0];
            int i10 = iArr[1];
            this.mParent.getRootView().getLocationInWindow(this.mTmpCoords);
            int[] iArr2 = this.mTmpCoords;
            this.mCoordsOnWindow.set(Math.max(0, min - (i9 - iArr2[0])), Math.max(0, i - (i10 - iArr2[1])));
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
            int width = this.mOverflowPanelSize.getWidth();
            final int height = this.mOverflowPanelSize.getHeight();
            final int width2 = this.mContentContainer.getWidth();
            final int height2 = this.mContentContainer.getHeight();
            final float y = this.mContentContainer.getY();
            final float x = this.mContentContainer.getX();
            final float width3 = x + ((float) this.mContentContainer.getWidth());
            final int i = width;
            final int i2 = width2;
            AnonymousClass5 r1 = new Animation() {
                /* access modifiers changed from: protected */
                public void applyTransformation(float f, Transformation transformation) {
                    FloatingToolbarPopup floatingToolbarPopup = FloatingToolbarPopup.this;
                    floatingToolbarPopup.setWidth(floatingToolbarPopup.mContentContainer, i2 + ((int) (f * ((float) (i - i2)))));
                    if (FloatingToolbarPopup.this.isInRTLMode()) {
                        FloatingToolbarPopup.this.mContentContainer.setX(x);
                        FloatingToolbarPopup.this.mMainPanel.setX(0.0f);
                        FloatingToolbarPopup.this.mOverflowPanel.setX(0.0f);
                        return;
                    }
                    FloatingToolbarPopup.this.mContentContainer.setX(width3 - ((float) FloatingToolbarPopup.this.mContentContainer.getWidth()));
                    FloatingToolbarPopup.this.mMainPanel.setX((float) (FloatingToolbarPopup.this.mContentContainer.getWidth() - i2));
                    FloatingToolbarPopup.this.mOverflowPanel.setX((float) (FloatingToolbarPopup.this.mContentContainer.getWidth() - i));
                }
            };
            AnonymousClass6 r12 = new Animation() {
                /* access modifiers changed from: protected */
                public void applyTransformation(float f, Transformation transformation) {
                    FloatingToolbarPopup floatingToolbarPopup = FloatingToolbarPopup.this;
                    floatingToolbarPopup.setHeight(floatingToolbarPopup.mContentContainer, height2 + ((int) (f * ((float) (height - height2)))));
                    if (FloatingToolbarPopup.this.mOpenOverflowUpwards) {
                        FloatingToolbarPopup.this.mContentContainer.setY(y - ((float) (FloatingToolbarPopup.this.mContentContainer.getHeight() - height2)));
                        FloatingToolbarPopup.this.positionContentYCoordinatesIfOpeningOverflowUpwards();
                    }
                }
            };
            final float x2 = this.mOverflowButton.getX();
            float f = (float) width;
            final float width4 = isInRTLMode() ? (f + x2) - ((float) this.mOverflowButton.getWidth()) : (x2 - f) + ((float) this.mOverflowButton.getWidth());
            AnonymousClass7 r3 = new Animation() {
                /* access modifiers changed from: protected */
                public void applyTransformation(float f, Transformation transformation) {
                    float f2 = x2;
                    FloatingToolbarPopup.this.mOverflowButton.setX(f2 + (f * (width4 - f2)) + (FloatingToolbarPopup.this.isInRTLMode() ? 0.0f : (float) (FloatingToolbarPopup.this.mContentContainer.getWidth() - width2)));
                }
            };
            r1.setInterpolator(this.mLogAccelerateInterpolator);
            r1.setDuration((long) getAdjustedDuration(250));
            r12.setInterpolator(this.mFastOutSlowInInterpolator);
            r12.setDuration((long) getAdjustedDuration(250));
            r3.setInterpolator(this.mFastOutSlowInInterpolator);
            r3.setDuration((long) getAdjustedDuration(250));
            this.mOpenOverflowAnimation.getAnimations().clear();
            this.mOpenOverflowAnimation.addAnimation(r1);
            this.mOpenOverflowAnimation.addAnimation(r12);
            this.mOpenOverflowAnimation.addAnimation(r3);
            this.mContentContainer.startAnimation(this.mOpenOverflowAnimation);
            this.mIsOverflowOpen = true;
            this.mMainPanel.animate().alpha(0.0f).withLayer().setInterpolator(this.mLinearOutSlowInInterpolator).setDuration(250).start();
            this.mOverflowPanel.setAlpha(1.0f);
        }

        private void closeOverflow() {
            final int width = this.mMainPanelSize.getWidth();
            final int width2 = this.mContentContainer.getWidth();
            final float x = this.mContentContainer.getX();
            final float width3 = x + ((float) this.mContentContainer.getWidth());
            final int i = width2;
            AnonymousClass8 r1 = new Animation() {
                /* access modifiers changed from: protected */
                public void applyTransformation(float f, Transformation transformation) {
                    FloatingToolbarPopup floatingToolbarPopup = FloatingToolbarPopup.this;
                    floatingToolbarPopup.setWidth(floatingToolbarPopup.mContentContainer, i + ((int) (f * ((float) (width - i)))));
                    if (FloatingToolbarPopup.this.isInRTLMode()) {
                        FloatingToolbarPopup.this.mContentContainer.setX(x);
                        FloatingToolbarPopup.this.mMainPanel.setX(0.0f);
                        FloatingToolbarPopup.this.mOverflowPanel.setX(0.0f);
                        return;
                    }
                    FloatingToolbarPopup.this.mContentContainer.setX(width3 - ((float) FloatingToolbarPopup.this.mContentContainer.getWidth()));
                    FloatingToolbarPopup.this.mMainPanel.setX((float) (FloatingToolbarPopup.this.mContentContainer.getWidth() - width));
                    FloatingToolbarPopup.this.mOverflowPanel.setX((float) (FloatingToolbarPopup.this.mContentContainer.getWidth() - i));
                }
            };
            final int height = this.mMainPanelSize.getHeight();
            final int height2 = this.mContentContainer.getHeight();
            final float y = this.mContentContainer.getY() + ((float) this.mContentContainer.getHeight());
            AnonymousClass9 r4 = new Animation() {
                /* access modifiers changed from: protected */
                public void applyTransformation(float f, Transformation transformation) {
                    FloatingToolbarPopup floatingToolbarPopup = FloatingToolbarPopup.this;
                    floatingToolbarPopup.setHeight(floatingToolbarPopup.mContentContainer, height2 + ((int) (f * ((float) (height - height2)))));
                    if (FloatingToolbarPopup.this.mOpenOverflowUpwards) {
                        FloatingToolbarPopup.this.mContentContainer.setY(y - ((float) FloatingToolbarPopup.this.mContentContainer.getHeight()));
                        FloatingToolbarPopup.this.positionContentYCoordinatesIfOpeningOverflowUpwards();
                    }
                }
            };
            final float x2 = this.mOverflowButton.getX();
            final float width4 = isInRTLMode() ? (x2 - ((float) width2)) + ((float) this.mOverflowButton.getWidth()) : (((float) width2) + x2) - ((float) this.mOverflowButton.getWidth());
            AnonymousClass10 r3 = new Animation() {
                /* access modifiers changed from: protected */
                public void applyTransformation(float f, Transformation transformation) {
                    float f2 = x2;
                    FloatingToolbarPopup.this.mOverflowButton.setX(f2 + (f * (width4 - f2)) + (FloatingToolbarPopup.this.isInRTLMode() ? 0.0f : (float) (FloatingToolbarPopup.this.mContentContainer.getWidth() - width2)));
                }
            };
            r1.setInterpolator(this.mFastOutSlowInInterpolator);
            r1.setDuration((long) getAdjustedDuration(250));
            r4.setInterpolator(this.mLogAccelerateInterpolator);
            r4.setDuration((long) getAdjustedDuration(250));
            r3.setInterpolator(this.mFastOutSlowInInterpolator);
            r3.setDuration((long) getAdjustedDuration(250));
            this.mCloseOverflowAnimation.getAnimations().clear();
            this.mCloseOverflowAnimation.addAnimation(r1);
            this.mCloseOverflowAnimation.addAnimation(r4);
            this.mCloseOverflowAnimation.addAnimation(r3);
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
                Size size = this.mOverflowPanelSize;
                setSize(this.mContentContainer, size);
                this.mMainPanel.setAlpha(0.0f);
                this.mMainPanel.setVisibility(4);
                this.mOverflowPanel.setAlpha(1.0f);
                this.mOverflowPanel.setVisibility(0);
                this.mOverflowButton.setImageDrawable(this.mArrow);
                this.mOverflowButton.setContentDescription(LocaleController.getString("AccDescrMoreOptions", NUM));
                if (isInRTLMode()) {
                    this.mContentContainer.setX((float) this.mMarginHorizontal);
                    this.mMainPanel.setX(0.0f);
                    this.mOverflowButton.setX((float) (size.getWidth() - this.mOverflowButtonSize.getWidth()));
                    this.mOverflowPanel.setX(0.0f);
                } else {
                    this.mContentContainer.setX((float) ((this.mPopupWindow.getWidth() - size.getWidth()) - this.mMarginHorizontal));
                    this.mMainPanel.setX(-this.mContentContainer.getX());
                    this.mOverflowButton.setX(0.0f);
                    this.mOverflowPanel.setX(0.0f);
                }
                if (this.mOpenOverflowUpwards) {
                    this.mContentContainer.setY((float) this.mMarginVertical);
                    this.mMainPanel.setY((float) (size.getHeight() - this.mContentContainer.getHeight()));
                    this.mOverflowButton.setY((float) (size.getHeight() - this.mOverflowButtonSize.getHeight()));
                    this.mOverflowPanel.setY(0.0f);
                    return;
                }
                this.mContentContainer.setY((float) this.mMarginVertical);
                this.mMainPanel.setY(0.0f);
                this.mOverflowButton.setY(0.0f);
                this.mOverflowPanel.setY((float) this.mOverflowButtonSize.getHeight());
                return;
            }
            Size size2 = this.mMainPanelSize;
            setSize(this.mContentContainer, size2);
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
                    this.mContentContainer.setX((float) ((this.mPopupWindow.getWidth() - size2.getWidth()) - this.mMarginHorizontal));
                    this.mMainPanel.setX(0.0f);
                    this.mOverflowButton.setX((float) (size2.getWidth() - this.mOverflowButtonSize.getWidth()));
                    this.mOverflowPanel.setX((float) (size2.getWidth() - this.mOverflowPanelSize.getWidth()));
                }
                if (this.mOpenOverflowUpwards) {
                    this.mContentContainer.setY((float) ((this.mMarginVertical + this.mOverflowPanelSize.getHeight()) - size2.getHeight()));
                    this.mMainPanel.setY(0.0f);
                    this.mOverflowButton.setY(0.0f);
                    this.mOverflowPanel.setY((float) (size2.getHeight() - this.mOverflowPanelSize.getHeight()));
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

        private void updateOverflowHeight(int i) {
            if (hasOverflow()) {
                int calculateOverflowHeight = calculateOverflowHeight((i - this.mOverflowButtonSize.getHeight()) / this.mLineHeight);
                if (this.mOverflowPanelSize.getHeight() != calculateOverflowHeight) {
                    this.mOverflowPanelSize = new Size(this.mOverflowPanelSize.getWidth(), calculateOverflowHeight);
                }
                setSize(this.mOverflowPanel, this.mOverflowPanelSize);
                if (this.mIsOverflowOpen) {
                    setSize(this.mContentContainer, this.mOverflowPanelSize);
                    if (this.mOpenOverflowUpwards) {
                        int height = this.mOverflowPanelSize.getHeight() - calculateOverflowHeight;
                        ViewGroup viewGroup = this.mContentContainer;
                        float f = (float) height;
                        viewGroup.setY(viewGroup.getY() + f);
                        ImageButton imageButton = this.mOverflowButton;
                        imageButton.setY(imageButton.getY() - f);
                    }
                } else {
                    setSize(this.mContentContainer, this.mMainPanelSize);
                }
                updatePopupSize();
            }
        }

        private void updatePopupSize() {
            int i;
            Size size = this.mMainPanelSize;
            int i2 = 0;
            if (size != null) {
                int max = Math.max(0, size.getWidth());
                i2 = max;
                i = Math.max(0, this.mMainPanelSize.getHeight());
            } else {
                i = 0;
            }
            Size size2 = this.mOverflowPanelSize;
            if (size2 != null) {
                i2 = Math.max(i2, size2.getWidth());
                i = Math.max(i, this.mOverflowPanelSize.getHeight());
            }
            this.mPopupWindow.setWidth(i2 + (this.mMarginHorizontal * 2));
            this.mPopupWindow.setHeight(i + (this.mMarginVertical * 2));
            maybeComputeTransitionDurationScale();
        }

        private void refreshViewPort() {
            this.mParent.getWindowVisibleDisplayFrame(this.mViewPortOnScreen);
        }

        private int getAdjustedToolbarWidth(int i) {
            refreshViewPort();
            int width = this.mViewPortOnScreen.width() - (AndroidUtilities.dp(16.0f) * 2);
            if (i <= 0) {
                i = AndroidUtilities.dp(400.0f);
            }
            return Math.min(i, width);
        }

        private void setZeroTouchableSurface() {
            this.mTouchableRegion.setEmpty();
        }

        /* access modifiers changed from: private */
        public void setContentAreaAsTouchableSurface() {
            int i;
            int i2;
            if (this.mIsOverflowOpen) {
                i2 = this.mOverflowPanelSize.getWidth();
                i = this.mOverflowPanelSize.getHeight();
            } else {
                i2 = this.mMainPanelSize.getWidth();
                i = this.mMainPanelSize.getHeight();
            }
            this.mTouchableRegion.set((int) this.mContentContainer.getX(), (int) this.mContentContainer.getY(), ((int) this.mContentContainer.getX()) + i2, ((int) this.mContentContainer.getY()) + i);
        }

        private boolean hasOverflow() {
            return this.mOverflowPanelSize != null;
        }

        public List<MenuItem> layoutMainPanelItems(List<MenuItem> list, int i) {
            LinkedList linkedList = new LinkedList(list);
            this.mMainPanel.removeAllViews();
            this.mMainPanel.setPaddingRelative(0, 0, 0, 0);
            int i2 = i;
            boolean z = true;
            while (!linkedList.isEmpty()) {
                MenuItem menuItem = (MenuItem) linkedList.peek();
                View access$2000 = FloatingToolbar.this.createMenuItemButton(this.mContext, menuItem, this.mIconTextSpacing);
                if (access$2000 instanceof LinearLayout) {
                    ((LinearLayout) access$2000).setGravity(17);
                }
                if (z) {
                    double paddingStart = (double) access$2000.getPaddingStart();
                    Double.isNaN(paddingStart);
                    access$2000.setPaddingRelative((int) (paddingStart * 1.5d), access$2000.getPaddingTop(), access$2000.getPaddingEnd(), access$2000.getPaddingBottom());
                }
                boolean z2 = linkedList.size() == 1;
                if (z2) {
                    int paddingStart2 = access$2000.getPaddingStart();
                    int paddingTop = access$2000.getPaddingTop();
                    double paddingEnd = (double) access$2000.getPaddingEnd();
                    Double.isNaN(paddingEnd);
                    access$2000.setPaddingRelative(paddingStart2, paddingTop, (int) (paddingEnd * 1.5d), access$2000.getPaddingBottom());
                }
                access$2000.measure(0, 0);
                int min = Math.min(access$2000.getMeasuredWidth(), i);
                boolean z3 = min <= i2 - this.mOverflowButtonSize.getWidth();
                boolean z4 = z2 && min <= i2;
                if (!z3 && !z4) {
                    break;
                }
                setButtonTagAndClickListener(access$2000, menuItem);
                this.mMainPanel.addView(access$2000);
                ViewGroup.LayoutParams layoutParams = access$2000.getLayoutParams();
                layoutParams.width = min;
                access$2000.setLayoutParams(layoutParams);
                i2 -= min;
                linkedList.pop();
                z = false;
            }
            if (!linkedList.isEmpty()) {
                this.mMainPanel.setPaddingRelative(0, 0, this.mOverflowButtonSize.getWidth(), 0);
            }
            this.mMainPanelSize = measure(this.mMainPanel);
            return linkedList;
        }

        private void layoutOverflowPanelItems(List<MenuItem> list) {
            ArrayAdapter arrayAdapter = (ArrayAdapter) this.mOverflowPanel.getAdapter();
            arrayAdapter.clear();
            int size = list.size();
            for (int i = 0; i < size; i++) {
                arrayAdapter.add(list.get(i));
            }
            this.mOverflowPanel.setAdapter(arrayAdapter);
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
            ArrayAdapter arrayAdapter = (ArrayAdapter) this.mOverflowPanel.getAdapter();
            arrayAdapter.clear();
            this.mOverflowPanel.setAdapter(arrayAdapter);
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
            int count = this.mOverflowPanel.getAdapter().getCount();
            int i = 0;
            for (int i2 = 0; i2 < count; i2++) {
                i = Math.max(this.mOverflowPanelViewHelper.calculateWidth((MenuItem) this.mOverflowPanel.getAdapter().getItem(i2)), i);
            }
            return i;
        }

        private int calculateOverflowHeight(int i) {
            int min = Math.min(4, Math.min(Math.max(2, i), this.mOverflowPanel.getCount()));
            return (min * this.mLineHeight) + this.mOverflowButtonSize.getHeight() + (min < this.mOverflowPanel.getCount() ? (int) (((float) this.mLineHeight) * 0.5f) : 0);
        }

        private void setButtonTagAndClickListener(View view, MenuItem menuItem) {
            view.setTag(menuItem);
            view.setOnClickListener(this.mMenuItemButtonOnClickListener);
        }

        private int getAdjustedDuration(int i) {
            int i2 = this.mTransitionDurationScale;
            if (i2 < 150) {
                return Math.max(i - 50, 0);
            }
            return i2 > 300 ? i + 50 : i;
        }

        private void maybeComputeTransitionDurationScale() {
            Size size = this.mMainPanelSize;
            if (size != null && this.mOverflowPanelSize != null) {
                int width = size.getWidth() - this.mOverflowPanelSize.getWidth();
                int height = this.mOverflowPanelSize.getHeight() - this.mMainPanelSize.getHeight();
                double sqrt = Math.sqrt((double) ((width * width) + (height * height)));
                double d = (double) this.mContentContainer.getContext().getResources().getDisplayMetrics().density;
                Double.isNaN(d);
                this.mTransitionDurationScale = (int) (sqrt / d);
            }
        }

        private ViewGroup createMainPanel() {
            return new LinearLayout(this.mContext) {
                /* access modifiers changed from: protected */
                public void onMeasure(int i, int i2) {
                    if (FloatingToolbarPopup.this.isOverflowAnimating()) {
                        i = View.MeasureSpec.makeMeasureSpec(FloatingToolbarPopup.this.mMainPanelSize.getWidth(), NUM);
                    }
                    super.onMeasure(i, i2);
                }

                public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                    return FloatingToolbarPopup.this.isOverflowAnimating();
                }
            };
        }

        private ImageButton createOverflowButton() {
            int i;
            ImageButton imageButton = new ImageButton(this.mContext);
            imageButton.setLayoutParams(new ViewGroup.LayoutParams(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(48.0f)));
            imageButton.setPaddingRelative(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(12.0f));
            imageButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageButton.setImageDrawable(this.mOverflow);
            if (FloatingToolbar.this.currentStyle == 0) {
                i = Theme.getColor("dialogTextBlack");
                imageButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21"), 1));
            } else if (FloatingToolbar.this.currentStyle == 2) {
                i = -328966;
                imageButton.setBackgroundDrawable(Theme.createSelectorDrawable(NUM, 1));
            } else {
                i = Theme.getColor("windowBackgroundWhiteBlackText");
                imageButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21"), 1));
            }
            this.mOverflow.setTint(i);
            this.mArrow.setTint(i);
            this.mToArrow.setTint(i);
            this.mToOverflow.setTint(i);
            imageButton.setOnClickListener(new View.OnClickListener(imageButton) {
                public final /* synthetic */ ImageButton f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(View view) {
                    FloatingToolbar.FloatingToolbarPopup.this.lambda$createOverflowButton$0$FloatingToolbar$FloatingToolbarPopup(this.f$1, view);
                }
            });
            return imageButton;
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$createOverflowButton$0 */
        public /* synthetic */ void lambda$createOverflowButton$0$FloatingToolbar$FloatingToolbarPopup(ImageButton imageButton, View view) {
            if (this.mIsOverflowOpen) {
                imageButton.setImageDrawable(this.mToOverflow);
                this.mToOverflow.start();
                closeOverflow();
                return;
            }
            imageButton.setImageDrawable(this.mToArrow);
            this.mToArrow.start();
            openOverflow();
        }

        private OverflowPanel createOverflowPanel() {
            OverflowPanel overflowPanel = new OverflowPanel(this);
            overflowPanel.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
            overflowPanel.setDivider((Drawable) null);
            overflowPanel.setDividerHeight(0);
            overflowPanel.setAdapter(new ArrayAdapter<MenuItem>(this.mContext, 0) {
                public View getView(int i, View view, ViewGroup viewGroup) {
                    return FloatingToolbarPopup.this.mOverflowPanelViewHelper.getView((MenuItem) getItem(i), FloatingToolbarPopup.this.mOverflowPanelSize.getWidth(), view);
                }
            });
            overflowPanel.setOnItemClickListener(new AdapterView.OnItemClickListener(overflowPanel) {
                public final /* synthetic */ FloatingToolbar.FloatingToolbarPopup.OverflowPanel f$1;

                {
                    this.f$1 = r2;
                }

                public final void onItemClick(AdapterView adapterView, View view, int i, long j) {
                    FloatingToolbar.FloatingToolbarPopup.this.lambda$createOverflowPanel$1$FloatingToolbar$FloatingToolbarPopup(this.f$1, adapterView, view, i, j);
                }
            });
            return overflowPanel;
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$createOverflowPanel$1 */
        public /* synthetic */ void lambda$createOverflowPanel$1$FloatingToolbar$FloatingToolbarPopup(OverflowPanel overflowPanel, AdapterView adapterView, View view, int i, long j) {
            MenuItem menuItem = (MenuItem) overflowPanel.getAdapter().getItem(i);
            MenuItem.OnMenuItemClickListener onMenuItemClickListener = this.mOnMenuItemClickListener;
            if (onMenuItemClickListener != null) {
                onMenuItemClickListener.onMenuItemClick(menuItem);
            }
        }

        /* access modifiers changed from: private */
        public boolean isOverflowAnimating() {
            boolean z = this.mOpenOverflowAnimation.hasStarted() && !this.mOpenOverflowAnimation.hasEnded();
            boolean z2 = this.mCloseOverflowAnimation.hasStarted() && !this.mCloseOverflowAnimation.hasEnded();
            if (z || z2) {
                return true;
            }
            return false;
        }

        private Animation.AnimationListener createOverflowAnimationListener() {
            return new Animation.AnimationListener() {
                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationStart(Animation animation) {
                    FloatingToolbarPopup.this.mOverflowButton.setEnabled(false);
                    FloatingToolbarPopup.this.mMainPanel.setVisibility(0);
                    FloatingToolbarPopup.this.mOverflowPanel.setVisibility(0);
                }

                public void onAnimationEnd(Animation animation) {
                    FloatingToolbarPopup.this.mContentContainer.post(
                    /*  JADX ERROR: Method code generation error
                        jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x000b: INVOKE  
                          (wrap: android.view.ViewGroup : 0x0002: INVOKE  (r2v2 android.view.ViewGroup) = 
                          (wrap: org.telegram.ui.ActionBar.FloatingToolbar$FloatingToolbarPopup : 0x0000: IGET  (r2v1 org.telegram.ui.ActionBar.FloatingToolbar$FloatingToolbarPopup) = 
                          (r1v0 'this' org.telegram.ui.ActionBar.FloatingToolbar$FloatingToolbarPopup$13 A[THIS])
                         org.telegram.ui.ActionBar.FloatingToolbar.FloatingToolbarPopup.13.this$1 org.telegram.ui.ActionBar.FloatingToolbar$FloatingToolbarPopup)
                         org.telegram.ui.ActionBar.FloatingToolbar.FloatingToolbarPopup.access$400(org.telegram.ui.ActionBar.FloatingToolbar$FloatingToolbarPopup):android.view.ViewGroup type: STATIC)
                          (wrap: org.telegram.ui.ActionBar.-$$Lambda$FloatingToolbar$FloatingToolbarPopup$13$d2okwCLASSNAMEm_XJUsyF7JZ4eggIbaA : 0x0008: CONSTRUCTOR  (r0v0 org.telegram.ui.ActionBar.-$$Lambda$FloatingToolbar$FloatingToolbarPopup$13$d2okwCLASSNAMEm_XJUsyF7JZ4eggIbaA) = 
                          (r1v0 'this' org.telegram.ui.ActionBar.FloatingToolbar$FloatingToolbarPopup$13 A[THIS])
                         call: org.telegram.ui.ActionBar.-$$Lambda$FloatingToolbar$FloatingToolbarPopup$13$d2okwCLASSNAMEm_XJUsyF7JZ4eggIbaA.<init>(org.telegram.ui.ActionBar.FloatingToolbar$FloatingToolbarPopup$13):void type: CONSTRUCTOR)
                         android.view.ViewGroup.post(java.lang.Runnable):boolean type: VIRTUAL in method: org.telegram.ui.ActionBar.FloatingToolbar.FloatingToolbarPopup.13.onAnimationEnd(android.view.animation.Animation):void, dex: classes3.dex
                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                        	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                        	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                        	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                        	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                        	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                        	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                        	at java.util.ArrayList.forEach(ArrayList.java:1259)
                        	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                        	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                        	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                        	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                        	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                        	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                        	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                        	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                        	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                        	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
                        	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
                        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                        	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                        	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:314)
                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                        	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                        	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                        	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                        	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                        	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                        	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                        	at java.util.ArrayList.forEach(ArrayList.java:1259)
                        	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                        	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                        	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                        	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                        	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                        	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                        	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                        	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                        	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                        	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                        	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                        	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                        	at java.util.ArrayList.forEach(ArrayList.java:1259)
                        	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                        	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                        	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                        	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                        	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                        	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                        	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                        	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                        	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                        	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                        	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                        	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                        	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                        	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                        Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0008: CONSTRUCTOR  (r0v0 org.telegram.ui.ActionBar.-$$Lambda$FloatingToolbar$FloatingToolbarPopup$13$d2okwCLASSNAMEm_XJUsyF7JZ4eggIbaA) = 
                          (r1v0 'this' org.telegram.ui.ActionBar.FloatingToolbar$FloatingToolbarPopup$13 A[THIS])
                         call: org.telegram.ui.ActionBar.-$$Lambda$FloatingToolbar$FloatingToolbarPopup$13$d2okwCLASSNAMEm_XJUsyF7JZ4eggIbaA.<init>(org.telegram.ui.ActionBar.FloatingToolbar$FloatingToolbarPopup$13):void type: CONSTRUCTOR in method: org.telegram.ui.ActionBar.FloatingToolbar.FloatingToolbarPopup.13.onAnimationEnd(android.view.animation.Animation):void, dex: classes3.dex
                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                        	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                        	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                        	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                        	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                        	... 74 more
                        Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: org.telegram.ui.ActionBar.-$$Lambda$FloatingToolbar$FloatingToolbarPopup$13$d2okwCLASSNAMEm_XJUsyF7JZ4eggIbaA, state: NOT_LOADED
                        	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                        	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                        	... 80 more
                        */
                    /*
                        this = this;
                        org.telegram.ui.ActionBar.FloatingToolbar$FloatingToolbarPopup r2 = org.telegram.ui.ActionBar.FloatingToolbar.FloatingToolbarPopup.this
                        android.view.ViewGroup r2 = r2.mContentContainer
                        org.telegram.ui.ActionBar.-$$Lambda$FloatingToolbar$FloatingToolbarPopup$13$d2okwCLASSNAMEm_XJUsyF7JZ4eggIbaA r0 = new org.telegram.ui.ActionBar.-$$Lambda$FloatingToolbar$FloatingToolbarPopup$13$d2okwCLASSNAMEm_XJUsyF7JZ4eggIbaA
                        r0.<init>(r1)
                        r2.post(r0)
                        return
                    */
                    throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.FloatingToolbar.FloatingToolbarPopup.AnonymousClass13.onAnimationEnd(android.view.animation.Animation):void");
                }

                /* access modifiers changed from: private */
                /* renamed from: lambda$onAnimationEnd$0 */
                public /* synthetic */ void lambda$onAnimationEnd$0$FloatingToolbar$FloatingToolbarPopup$13() {
                    FloatingToolbarPopup.this.setPanelsStatesAtRestingPosition();
                    FloatingToolbarPopup.this.setContentAreaAsTouchableSurface();
                }
            };
        }

        private Size measure(View view) {
            view.measure(0, 0);
            return new Size(view.getMeasuredWidth(), view.getMeasuredHeight());
        }

        private void setSize(View view, int i, int i2) {
            view.setMinimumWidth(i);
            view.setMinimumHeight(i2);
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            if (layoutParams == null) {
                layoutParams = new ViewGroup.LayoutParams(0, 0);
            }
            layoutParams.width = i;
            layoutParams.height = i2;
            view.setLayoutParams(layoutParams);
        }

        private void setSize(View view, Size size) {
            setSize(view, size.getWidth(), size.getHeight());
        }

        /* access modifiers changed from: private */
        public void setWidth(View view, int i) {
            setSize(view, i, view.getLayoutParams().height);
        }

        /* access modifiers changed from: private */
        public void setHeight(View view, int i) {
            setSize(view, view.getLayoutParams().width, i);
        }

        private final class OverflowPanel extends ListView {
            private final FloatingToolbarPopup mPopup;

            OverflowPanel(FloatingToolbarPopup floatingToolbarPopup) {
                super(floatingToolbarPopup.mContext);
                this.mPopup = floatingToolbarPopup;
                setVerticalScrollBarEnabled(false);
                setOutlineProvider(new ViewOutlineProvider(FloatingToolbarPopup.this) {
                    public void getOutline(View view, Outline outline) {
                        outline.setRoundRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight(), (float) AndroidUtilities.dp(6.0f));
                    }
                });
                setClipToOutline(true);
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(this.mPopup.mOverflowPanelSize.getHeight() - this.mPopup.mOverflowButtonSize.getHeight(), NUM));
            }

            public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                if (this.mPopup.isOverflowAnimating()) {
                    return true;
                }
                return super.dispatchTouchEvent(motionEvent);
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

            private float computeLog(float f, int i) {
                return (float) (1.0d - Math.pow((double) i, (double) (-f)));
            }

            public float getInterpolation(float f) {
                return 1.0f - (computeLog(1.0f - f, 100) * this.LOGS_SCALE);
            }
        }

        private final class OverflowPanelViewHelper {
            private final View mCalculator = createMenuButton((MenuItem) null);
            private final Context mContext;
            private final int mIconTextSpacing;
            private final int mSidePadding = AndroidUtilities.dp(18.0f);

            public OverflowPanelViewHelper(Context context, int i) {
                this.mContext = context;
                this.mIconTextSpacing = i;
            }

            public View getView(MenuItem menuItem, int i, View view) {
                if (view != null) {
                    FloatingToolbar.updateMenuItemButton(view, menuItem, this.mIconTextSpacing);
                } else {
                    view = createMenuButton(menuItem);
                }
                view.setMinimumWidth(i);
                return view;
            }

            public int calculateWidth(MenuItem menuItem) {
                FloatingToolbar.updateMenuItemButton(this.mCalculator, menuItem, this.mIconTextSpacing);
                this.mCalculator.measure(0, 0);
                return this.mCalculator.getMeasuredWidth();
            }

            private View createMenuButton(MenuItem menuItem) {
                View access$2000 = FloatingToolbar.this.createMenuItemButton(this.mContext, menuItem, this.mIconTextSpacing);
                int i = this.mSidePadding;
                access$2000.setPadding(i, 0, i, 0);
                return access$2000;
            }
        }
    }

    /* access modifiers changed from: private */
    public View createMenuItemButton(Context context, MenuItem menuItem, int i) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
        linearLayout.setOrientation(0);
        linearLayout.setMinimumWidth(AndroidUtilities.dp(48.0f));
        linearLayout.setMinimumHeight(AndroidUtilities.dp(48.0f));
        linearLayout.setPaddingRelative(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
        TextView textView = new TextView(context);
        textView.setGravity(17);
        textView.setSingleLine(true);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textView.setTextSize(1, 14.0f);
        textView.setFocusable(false);
        textView.setImportantForAccessibility(2);
        textView.setFocusableInTouchMode(false);
        int i2 = this.currentStyle;
        if (i2 == 0) {
            textView.setTextColor(Theme.getColor("dialogTextBlack"));
            linearLayout.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        } else if (i2 == 2) {
            textView.setTextColor(-328966);
            linearLayout.setBackgroundDrawable(Theme.getSelectorDrawable(NUM, false));
        } else if (i2 == 1) {
            textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            linearLayout.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        }
        textView.setPaddingRelative(AndroidUtilities.dp(11.0f), 0, 0, 0);
        linearLayout.addView(textView, new LinearLayout.LayoutParams(-2, AndroidUtilities.dp(48.0f)));
        if (menuItem != null) {
            updateMenuItemButton(linearLayout, menuItem, i);
        }
        return linearLayout;
    }

    /* access modifiers changed from: private */
    public static void updateMenuItemButton(View view, MenuItem menuItem, int i) {
        TextView textView = (TextView) ((ViewGroup) view).getChildAt(0);
        textView.setEllipsize((TextUtils.TruncateAt) null);
        if (TextUtils.isEmpty(menuItem.getTitle())) {
            textView.setVisibility(8);
        } else {
            textView.setVisibility(0);
            textView.setText(menuItem.getTitle());
        }
        textView.setPaddingRelative(0, 0, 0, 0);
    }

    /* access modifiers changed from: private */
    public ViewGroup createContentContainer(Context context) {
        RelativeLayout relativeLayout = new RelativeLayout(context);
        ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(-2, -2);
        int dp = AndroidUtilities.dp(20.0f);
        marginLayoutParams.rightMargin = dp;
        marginLayoutParams.topMargin = dp;
        marginLayoutParams.leftMargin = dp;
        marginLayoutParams.bottomMargin = dp;
        relativeLayout.setLayoutParams(marginLayoutParams);
        relativeLayout.setElevation((float) AndroidUtilities.dp(2.0f));
        relativeLayout.setFocusable(true);
        relativeLayout.setFocusableInTouchMode(true);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(0);
        float dp2 = (float) AndroidUtilities.dp(6.0f);
        gradientDrawable.setCornerRadii(new float[]{dp2, dp2, dp2, dp2, dp2, dp2, dp2, dp2});
        int i = this.currentStyle;
        if (i == 0) {
            gradientDrawable.setColor(Theme.getColor("dialogBackground"));
        } else if (i == 2) {
            gradientDrawable.setColor(-NUM);
        } else if (i == 1) {
            gradientDrawable.setColor(Theme.getColor("windowBackgroundWhite"));
        }
        relativeLayout.setBackgroundDrawable(gradientDrawable);
        relativeLayout.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
        relativeLayout.setClipToOutline(true);
        return relativeLayout;
    }

    /* access modifiers changed from: private */
    public static PopupWindow createPopupWindow(ViewGroup viewGroup) {
        LinearLayout linearLayout = new LinearLayout(viewGroup.getContext());
        PopupWindow popupWindow = new PopupWindow(linearLayout);
        popupWindow.setClippingEnabled(false);
        popupWindow.setAnimationStyle(0);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0));
        viewGroup.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
        linearLayout.addView(viewGroup);
        return popupWindow;
    }

    /* access modifiers changed from: private */
    public static AnimatorSet createEnterAnimation(View view) {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(view, View.ALPHA, new float[]{0.0f, 1.0f}).setDuration(150)});
        return animatorSet;
    }

    /* access modifiers changed from: private */
    public static AnimatorSet createExitAnimation(View view, int i, Animator.AnimatorListener animatorListener) {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(view, View.ALPHA, new float[]{1.0f, 0.0f}).setDuration(100)});
        animatorSet.setStartDelay((long) i);
        animatorSet.addListener(animatorListener);
        return animatorSet;
    }
}
