package org.telegram.ui.ActionBar;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.Size;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;

@TargetApi(23)
public final class FloatingToolbar {
    private static final OnMenuItemClickListener NO_OP_MENUITEM_CLICK_LISTENER = -$$Lambda$FloatingToolbar$uB-ffYWeN3XYcSKbFDG7nEMNVsU.INSTANCE;
    public static final int STYLE_BLACK = 2;
    public static final int STYLE_DIALOG = 0;
    public static final int STYLE_THEME = 1;
    private int currentStyle;
    private final Rect mContentRect = new Rect();
    private Menu mMenu;
    private OnMenuItemClickListener mMenuItemClickListener = NO_OP_MENUITEM_CLICK_LISTENER;
    private final Comparator<MenuItem> mMenuItemComparator = -$$Lambda$FloatingToolbar$id3tSGGj4IAqzijkydDavDmoZuo.INSTANCE;
    private final OnLayoutChangeListener mOrientationChangeHandler = new OnLayoutChangeListener() {
        private final Rect mNewRect = new Rect();
        private final Rect mOldRect = new Rect();

        public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
            this.mNewRect.set(i, i2, i3, i4);
            this.mOldRect.set(i5, i6, i7, i8);
            if (FloatingToolbar.this.mPopup.isShowing() && !this.mNewRect.equals(this.mOldRect)) {
                FloatingToolbar.this.mWidthChanged = true;
                FloatingToolbar.this.updateLayout();
            }
        }
    };
    private final FloatingToolbarPopup mPopup;
    private final Rect mPreviousContentRect = new Rect();
    private List<MenuItem> mShowingMenuItems = new ArrayList();
    private int mSuggestedWidth;
    private boolean mWidthChanged = true;
    private final View mWindowView;

    private final class FloatingToolbarPopup {
        private static final int MAX_OVERFLOW_SIZE = 4;
        private static final int MIN_OVERFLOW_SIZE = 2;
        private final Drawable mArrow;
        private final AnimationSet mCloseOverflowAnimation;
        private final ViewGroup mContentContainer;
        private final Context mContext;
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
        private final ViewGroup mMainPanel;
        private Size mMainPanelSize;
        private final int mMarginHorizontal;
        private final int mMarginVertical;
        private final OnClickListener mMenuItemButtonOnClickListener = new OnClickListener() {
            public void onClick(View view) {
                if ((view.getTag() instanceof MenuItem) && FloatingToolbarPopup.this.mOnMenuItemClickListener != null) {
                    FloatingToolbarPopup.this.mOnMenuItemClickListener.onMenuItemClick((MenuItem) view.getTag());
                }
            }
        };
        private OnMenuItemClickListener mOnMenuItemClickListener;
        private final AnimationSet mOpenOverflowAnimation;
        private boolean mOpenOverflowUpwards;
        private final Drawable mOverflow;
        private final ImageButton mOverflowButton;
        private final Size mOverflowButtonSize;
        private final OverflowPanel mOverflowPanel;
        private Size mOverflowPanelSize;
        private final OverflowPanelViewHelper mOverflowPanelViewHelper;
        private final View mParent;
        private final PopupWindow mPopupWindow;
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

        private final class LogAccelerateInterpolator implements Interpolator {
            private final int BASE;
            private final float LOGS_SCALE;

            private LogAccelerateInterpolator() {
                this.BASE = 100;
                this.LOGS_SCALE = 1.0f / computeLog(1.0f, 100);
            }

            /* synthetic */ LogAccelerateInterpolator(FloatingToolbarPopup floatingToolbarPopup, AnonymousClass1 anonymousClass1) {
                this();
            }

            private float computeLog(float f, int i) {
                return (float) (1.0d - Math.pow((double) i, (double) (-f)));
            }

            public float getInterpolation(float f) {
                return 1.0f - (computeLog(1.0f - f, 100) * this.LOGS_SCALE);
            }
        }

        private final class OverflowPanel extends ListView {
            private final FloatingToolbarPopup mPopup;

            OverflowPanel(FloatingToolbarPopup floatingToolbarPopup) {
                super(floatingToolbarPopup.mContext);
                this.mPopup = floatingToolbarPopup;
                setScrollBarDefaultDelayBeforeFade(ViewConfiguration.getScrollDefaultDelay() * 3);
                setScrollIndicators(3);
            }

            /* Access modifiers changed, original: protected */
            public void onMeasure(int i, int i2) {
                super.onMeasure(i, MeasureSpec.makeMeasureSpec(this.mPopup.mOverflowPanelSize.getHeight() - this.mPopup.mOverflowButtonSize.getHeight(), NUM));
            }

            public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                if (this.mPopup.isOverflowAnimating()) {
                    return true;
                }
                return super.dispatchTouchEvent(motionEvent);
            }

            /* Access modifiers changed, original: protected */
            public boolean awakenScrollBars() {
                return super.awakenScrollBars();
            }
        }

        private final class OverflowPanelViewHelper {
            private final View mCalculator = createMenuButton(null);
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

        private boolean isInRTLMode() {
            return false;
        }

        private void setTouchableSurfaceInsetsComputer() {
        }

        public FloatingToolbarPopup(Context context, View view) {
            this.mParent = view;
            this.mContext = context;
            this.mContentContainer = FloatingToolbar.this.createContentContainer(context);
            this.mPopupWindow = FloatingToolbar.createPopupWindow(this.mContentContainer);
            this.mMarginHorizontal = AndroidUtilities.dp(16.0f);
            this.mMarginVertical = AndroidUtilities.dp(8.0f);
            this.mLineHeight = AndroidUtilities.dp(48.0f);
            this.mIconTextSpacing = AndroidUtilities.dp(8.0f);
            this.mLogAccelerateInterpolator = new LogAccelerateInterpolator(this, null);
            this.mFastOutSlowInInterpolator = AnimationUtils.loadInterpolator(this.mContext, 17563661);
            this.mLinearOutSlowInInterpolator = AnimationUtils.loadInterpolator(this.mContext, 17563662);
            this.mFastOutLinearInInterpolator = AnimationUtils.loadInterpolator(this.mContext, 17563663);
            this.mArrow = this.mContext.getResources().getDrawable(NUM).mutate();
            this.mArrow.setAutoMirrored(true);
            this.mOverflow = this.mContext.getResources().getDrawable(NUM).mutate();
            this.mOverflow.setAutoMirrored(true);
            this.mToArrow = (AnimatedVectorDrawable) this.mContext.getResources().getDrawable(NUM).mutate();
            this.mToArrow.setAutoMirrored(true);
            this.mToOverflow = (AnimatedVectorDrawable) this.mContext.getResources().getDrawable(NUM).mutate();
            this.mToOverflow.setAutoMirrored(true);
            this.mOverflowButton = createOverflowButton();
            this.mOverflowButtonSize = measure(this.mOverflowButton);
            this.mMainPanel = createMainPanel();
            this.mOverflowPanelViewHelper = new OverflowPanelViewHelper(this.mContext, this.mIconTextSpacing);
            this.mOverflowPanel = createOverflowPanel();
            AnimationListener createOverflowAnimationListener = createOverflowAnimationListener();
            this.mOpenOverflowAnimation = new AnimationSet(true);
            this.mOpenOverflowAnimation.setAnimationListener(createOverflowAnimationListener);
            this.mCloseOverflowAnimation = new AnimationSet(true);
            this.mCloseOverflowAnimation.setAnimationListener(createOverflowAnimationListener);
            this.mShowAnimation = FloatingToolbar.createEnterAnimation(this.mContentContainer);
            this.mDismissAnimation = FloatingToolbar.createExitAnimation(this.mContentContainer, 150, new AnimatorListenerAdapter(FloatingToolbar.this) {
                public void onAnimationEnd(Animator animator) {
                    FloatingToolbarPopup.this.mPopupWindow.dismiss();
                    FloatingToolbarPopup.this.mContentContainer.removeAllViews();
                }
            });
            this.mHideAnimation = FloatingToolbar.createExitAnimation(this.mContentContainer, 0, new AnimatorListenerAdapter(FloatingToolbar.this) {
                public void onAnimationEnd(Animator animator) {
                    FloatingToolbarPopup.this.mPopupWindow.dismiss();
                }
            });
        }

        public boolean setOutsideTouchable(boolean z, OnDismissListener onDismissListener) {
            boolean z2 = true;
            if ((this.mPopupWindow.isOutsideTouchable() ^ z) != 0) {
                this.mPopupWindow.setOutsideTouchable(z);
                this.mPopupWindow.setFocusable(z ^ 1);
            } else {
                z2 = false;
            }
            this.mPopupWindow.setOnDismissListener(onDismissListener);
            return z2;
        }

        public void layoutMenuItems(List<MenuItem> list, OnMenuItemClickListener onMenuItemClickListener, int i) {
            this.mOnMenuItemClickListener = onMenuItemClickListener;
            cancelOverflowAnimations();
            clearPanels();
            List layoutMainPanelItems = layoutMainPanelItems(list, getAdjustedToolbarWidth(i));
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
            return (this.mDismissed || this.mHidden) ? false : true;
        }

        public boolean isHidden() {
            return this.mHidden;
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
            int calculateOverflowHeight;
            int height;
            refreshViewPort();
            int min = Math.min(rect.centerX() - (this.mPopupWindow.getWidth() / 2), this.mViewPortOnScreen.right - this.mPopupWindow.getWidth());
            int i = rect.top;
            Rect rect2 = this.mViewPortOnScreen;
            i -= rect2.top;
            int i2 = rect2.bottom - rect.bottom;
            int i3 = this.mMarginVertical * 2;
            int i4 = this.mLineHeight + i3;
            if (hasOverflow()) {
                calculateOverflowHeight = calculateOverflowHeight(2) + i3;
                Rect rect3 = this.mViewPortOnScreen;
                int i5 = (rect3.bottom - rect.top) + i4;
                int i6 = (rect.bottom - rect3.top) + i4;
                if (i >= calculateOverflowHeight) {
                    updateOverflowHeight(i - i3);
                    height = rect.top - this.mPopupWindow.getHeight();
                    this.mOpenOverflowUpwards = true;
                } else if (i >= i4 && i5 >= calculateOverflowHeight) {
                    updateOverflowHeight(i5 - i3);
                    height = rect.top - i4;
                    this.mOpenOverflowUpwards = false;
                } else if (i2 >= calculateOverflowHeight) {
                    updateOverflowHeight(i2 - i3);
                    height = rect.bottom;
                    this.mOpenOverflowUpwards = false;
                } else if (i2 < i4 || this.mViewPortOnScreen.height() < calculateOverflowHeight) {
                    updateOverflowHeight(this.mViewPortOnScreen.height() - i3);
                    height = this.mViewPortOnScreen.top;
                    this.mOpenOverflowUpwards = false;
                } else {
                    updateOverflowHeight(i6 - i3);
                    height = (rect.bottom + i4) - this.mPopupWindow.getHeight();
                    this.mOpenOverflowUpwards = true;
                }
            } else if (i >= i4) {
                height = rect.top - i4;
            } else if (i2 >= i4) {
                height = rect.bottom;
            } else if (i2 >= this.mLineHeight) {
                height = rect.bottom - this.mMarginVertical;
            } else {
                height = Math.max(this.mViewPortOnScreen.top, rect.top - i4);
            }
            this.mParent.getRootView().getLocationOnScreen(this.mTmpCoords);
            int[] iArr = this.mTmpCoords;
            calculateOverflowHeight = iArr[0];
            i = iArr[1];
            this.mParent.getRootView().getLocationInWindow(this.mTmpCoords);
            int[] iArr2 = this.mTmpCoords;
            this.mCoordsOnWindow.set(Math.max(0, min - (calculateOverflowHeight - iArr2[0])), Math.max(0, height - (i - iArr2[1])));
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
            AnonymousClass5 anonymousClass5 = new Animation() {
                /* Access modifiers changed, original: protected */
                public void applyTransformation(float f, Transformation transformation) {
                    int i = (int) (f * ((float) (i - i2)));
                    FloatingToolbarPopup floatingToolbarPopup = FloatingToolbarPopup.this;
                    floatingToolbarPopup.setWidth(floatingToolbarPopup.mContentContainer, i2 + i);
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
            AnonymousClass6 anonymousClass6 = new Animation() {
                /* Access modifiers changed, original: protected */
                public void applyTransformation(float f, Transformation transformation) {
                    int i = (int) (f * ((float) (height - height2)));
                    FloatingToolbarPopup floatingToolbarPopup = FloatingToolbarPopup.this;
                    floatingToolbarPopup.setHeight(floatingToolbarPopup.mContentContainer, height2 + i);
                    if (FloatingToolbarPopup.this.mOpenOverflowUpwards) {
                        FloatingToolbarPopup.this.mContentContainer.setY(y - ((float) (FloatingToolbarPopup.this.mContentContainer.getHeight() - height2)));
                        FloatingToolbarPopup.this.positionContentYCoordinatesIfOpeningOverflowUpwards();
                    }
                }
            };
            final float x2 = this.mOverflowButton.getX();
            float f = (float) width;
            f = isInRTLMode() ? (f + x2) - ((float) this.mOverflowButton.getWidth()) : (x2 - f) + ((float) this.mOverflowButton.getWidth());
            AnonymousClass7 anonymousClass7 = new Animation() {
                /* Access modifiers changed, original: protected */
                public void applyTransformation(float f, Transformation transformation) {
                    float f2 = x2;
                    FloatingToolbarPopup.this.mOverflowButton.setX((f2 + (f * (f - f2))) + (FloatingToolbarPopup.this.isInRTLMode() ? 0.0f : (float) (FloatingToolbarPopup.this.mContentContainer.getWidth() - width2)));
                }
            };
            anonymousClass5.setInterpolator(this.mLogAccelerateInterpolator);
            anonymousClass5.setDuration((long) getAdjustedDuration(250));
            anonymousClass6.setInterpolator(this.mFastOutSlowInInterpolator);
            anonymousClass6.setDuration((long) getAdjustedDuration(250));
            anonymousClass7.setInterpolator(this.mFastOutSlowInInterpolator);
            anonymousClass7.setDuration((long) getAdjustedDuration(250));
            this.mOpenOverflowAnimation.getAnimations().clear();
            this.mOpenOverflowAnimation.addAnimation(anonymousClass5);
            this.mOpenOverflowAnimation.addAnimation(anonymousClass6);
            this.mOpenOverflowAnimation.addAnimation(anonymousClass7);
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
            AnonymousClass8 anonymousClass8 = new Animation() {
                /* Access modifiers changed, original: protected */
                public void applyTransformation(float f, Transformation transformation) {
                    int i = (int) (f * ((float) (width - i)));
                    FloatingToolbarPopup floatingToolbarPopup = FloatingToolbarPopup.this;
                    floatingToolbarPopup.setWidth(floatingToolbarPopup.mContentContainer, i + i);
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
            AnonymousClass9 anonymousClass9 = new Animation() {
                /* Access modifiers changed, original: protected */
                public void applyTransformation(float f, Transformation transformation) {
                    int i = (int) (f * ((float) (height - height2)));
                    FloatingToolbarPopup floatingToolbarPopup = FloatingToolbarPopup.this;
                    floatingToolbarPopup.setHeight(floatingToolbarPopup.mContentContainer, height2 + i);
                    if (FloatingToolbarPopup.this.mOpenOverflowUpwards) {
                        FloatingToolbarPopup.this.mContentContainer.setY(y - ((float) FloatingToolbarPopup.this.mContentContainer.getHeight()));
                        FloatingToolbarPopup.this.positionContentYCoordinatesIfOpeningOverflowUpwards();
                    }
                }
            };
            final float x2 = this.mOverflowButton.getX();
            final float width4 = isInRTLMode() ? (x2 - ((float) width2)) + ((float) this.mOverflowButton.getWidth()) : (((float) width2) + x2) - ((float) this.mOverflowButton.getWidth());
            AnonymousClass10 anonymousClass10 = new Animation() {
                /* Access modifiers changed, original: protected */
                public void applyTransformation(float f, Transformation transformation) {
                    float f2 = x2;
                    FloatingToolbarPopup.this.mOverflowButton.setX((f2 + (f * (width4 - f2))) + (FloatingToolbarPopup.this.isInRTLMode() ? 0.0f : (float) (FloatingToolbarPopup.this.mContentContainer.getWidth() - width2)));
                }
            };
            anonymousClass8.setInterpolator(this.mFastOutSlowInInterpolator);
            anonymousClass8.setDuration((long) getAdjustedDuration(250));
            anonymousClass9.setInterpolator(this.mLogAccelerateInterpolator);
            anonymousClass9.setDuration((long) getAdjustedDuration(250));
            anonymousClass10.setInterpolator(this.mFastOutSlowInInterpolator);
            anonymousClass10.setDuration((long) getAdjustedDuration(250));
            this.mCloseOverflowAnimation.getAnimations().clear();
            this.mCloseOverflowAnimation.addAnimation(anonymousClass8);
            this.mCloseOverflowAnimation.addAnimation(anonymousClass9);
            this.mCloseOverflowAnimation.addAnimation(anonymousClass10);
            this.mContentContainer.startAnimation(this.mCloseOverflowAnimation);
            this.mIsOverflowOpen = false;
            this.mMainPanel.animate().alpha(1.0f).withLayer().setInterpolator(this.mFastOutLinearInInterpolator).setDuration(100).start();
            this.mOverflowPanel.animate().alpha(0.0f).withLayer().setInterpolator(this.mLinearOutSlowInInterpolator).setDuration(150).start();
        }

        private void setPanelsStatesAtRestingPosition() {
            this.mOverflowButton.setEnabled(true);
            this.mOverflowPanel.awakenScrollBars();
            String str = "AccDescrMoreOptions";
            Size size;
            if (this.mIsOverflowOpen) {
                size = this.mOverflowPanelSize;
                setSize(this.mContentContainer, size);
                this.mMainPanel.setAlpha(0.0f);
                this.mMainPanel.setVisibility(4);
                this.mOverflowPanel.setAlpha(1.0f);
                this.mOverflowPanel.setVisibility(0);
                this.mOverflowButton.setImageDrawable(this.mArrow);
                this.mOverflowButton.setContentDescription(LocaleController.getString(str, NUM));
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
            size = this.mMainPanelSize;
            setSize(this.mContentContainer, size);
            this.mMainPanel.setAlpha(1.0f);
            this.mMainPanel.setVisibility(0);
            this.mOverflowPanel.setAlpha(0.0f);
            this.mOverflowPanel.setVisibility(4);
            this.mOverflowButton.setImageDrawable(this.mOverflow);
            this.mOverflowButton.setContentDescription(LocaleController.getString(str, NUM));
            if (hasOverflow()) {
                if (isInRTLMode()) {
                    this.mContentContainer.setX((float) this.mMarginHorizontal);
                    this.mMainPanel.setX(0.0f);
                    this.mOverflowButton.setX(0.0f);
                    this.mOverflowPanel.setX(0.0f);
                } else {
                    this.mContentContainer.setX((float) ((this.mPopupWindow.getWidth() - size.getWidth()) - this.mMarginHorizontal));
                    this.mMainPanel.setX(0.0f);
                    this.mOverflowButton.setX((float) (size.getWidth() - this.mOverflowButtonSize.getWidth()));
                    this.mOverflowPanel.setX((float) (size.getWidth() - this.mOverflowPanelSize.getWidth()));
                }
                if (this.mOpenOverflowUpwards) {
                    this.mContentContainer.setY((float) ((this.mMarginVertical + this.mOverflowPanelSize.getHeight()) - size.getHeight()));
                    this.mMainPanel.setY(0.0f);
                    this.mOverflowButton.setY(0.0f);
                    this.mOverflowPanel.setY((float) (size.getHeight() - this.mOverflowPanelSize.getHeight()));
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
                i = calculateOverflowHeight((i - this.mOverflowButtonSize.getHeight()) / this.mLineHeight);
                if (this.mOverflowPanelSize.getHeight() != i) {
                    this.mOverflowPanelSize = new Size(this.mOverflowPanelSize.getWidth(), i);
                }
                setSize(this.mOverflowPanel, this.mOverflowPanelSize);
                if (this.mIsOverflowOpen) {
                    setSize(this.mContentContainer, this.mOverflowPanelSize);
                    if (this.mOpenOverflowUpwards) {
                        int height = this.mOverflowPanelSize.getHeight() - i;
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
            int max;
            Size size = this.mMainPanelSize;
            int i = 0;
            if (size != null) {
                max = Math.max(0, size.getWidth());
                i = Math.max(0, this.mMainPanelSize.getHeight());
            } else {
                max = 0;
            }
            Size size2 = this.mOverflowPanelSize;
            if (size2 != null) {
                max = Math.max(max, size2.getWidth());
                i = Math.max(i, this.mOverflowPanelSize.getHeight());
            }
            this.mPopupWindow.setWidth(max + (this.mMarginHorizontal * 2));
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

        private void setContentAreaAsTouchableSurface() {
            int width;
            int height;
            if (this.mIsOverflowOpen) {
                width = this.mOverflowPanelSize.getWidth();
                height = this.mOverflowPanelSize.getHeight();
            } else {
                width = this.mMainPanelSize.getWidth();
                height = this.mMainPanelSize.getHeight();
            }
            this.mTouchableRegion.set((int) this.mContentContainer.getX(), (int) this.mContentContainer.getY(), ((int) this.mContentContainer.getX()) + width, ((int) this.mContentContainer.getY()) + height);
        }

        private boolean hasOverflow() {
            return this.mOverflowPanelSize != null;
        }

        public List<MenuItem> layoutMainPanelItems(List<MenuItem> list, int i) {
            LinkedList linkedList = new LinkedList(list);
            this.mMainPanel.removeAllViews();
            this.mMainPanel.setPaddingRelative(0, 0, 0, 0);
            int i2 = i;
            Object obj = 1;
            while (!linkedList.isEmpty()) {
                MenuItem menuItem = (MenuItem) linkedList.peek();
                View access$2000 = FloatingToolbar.this.createMenuItemButton(this.mContext, menuItem, this.mIconTextSpacing);
                if (access$2000 instanceof LinearLayout) {
                    ((LinearLayout) access$2000).setGravity(17);
                }
                if (obj != null) {
                    double paddingStart = (double) access$2000.getPaddingStart();
                    Double.isNaN(paddingStart);
                    access$2000.setPaddingRelative((int) (paddingStart * 1.5d), access$2000.getPaddingTop(), access$2000.getPaddingEnd(), access$2000.getPaddingBottom());
                }
                obj = linkedList.size() == 1 ? 1 : null;
                if (obj != null) {
                    int paddingStart2 = access$2000.getPaddingStart();
                    int paddingTop = access$2000.getPaddingTop();
                    double paddingEnd = (double) access$2000.getPaddingEnd();
                    Double.isNaN(paddingEnd);
                    access$2000.setPaddingRelative(paddingStart2, paddingTop, (int) (paddingEnd * 1.5d), access$2000.getPaddingBottom());
                }
                access$2000.measure(0, 0);
                int min = Math.min(access$2000.getMeasuredWidth(), i);
                Object obj2 = min <= i2 - this.mOverflowButtonSize.getWidth() ? 1 : null;
                obj = (obj == null || min > i2) ? null : 1;
                if (obj2 == null && obj == null) {
                    break;
                }
                setButtonTagAndClickListener(access$2000, menuItem);
                this.mMainPanel.addView(access$2000);
                LayoutParams layoutParams = access$2000.getLayoutParams();
                layoutParams.width = min;
                access$2000.setLayoutParams(layoutParams);
                i2 -= min;
                linkedList.pop();
                obj = null;
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
            this.mOverflowPanelSize = new Size(Math.max(getOverflowWidth(), this.mOverflowButtonSize.getWidth()), calculateOverflowHeight(4));
            setSize(this.mOverflowPanel, this.mOverflowPanelSize);
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

        private void positionContentYCoordinatesIfOpeningOverflowUpwards() {
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
            i = Math.min(4, Math.min(Math.max(2, i), this.mOverflowPanel.getCount()));
            return ((i * this.mLineHeight) + this.mOverflowButtonSize.getHeight()) + (i < this.mOverflowPanel.getCount() ? (int) (((float) this.mLineHeight) * 0.5f) : 0);
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
            if (i2 > 300) {
                i += 50;
            }
            return i;
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
                /* Access modifiers changed, original: protected */
                public void onMeasure(int i, int i2) {
                    if (FloatingToolbarPopup.this.isOverflowAnimating()) {
                        i = MeasureSpec.makeMeasureSpec(FloatingToolbarPopup.this.mMainPanelSize.getWidth(), NUM);
                    }
                    super.onMeasure(i, i2);
                }

                public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                    return FloatingToolbarPopup.this.isOverflowAnimating();
                }
            };
        }

        private ImageButton createOverflowButton() {
            int color;
            ImageButton imageButton = new ImageButton(this.mContext);
            imageButton.setLayoutParams(new LayoutParams(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(48.0f)));
            imageButton.setPaddingRelative(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(12.0f));
            imageButton.setScaleType(ScaleType.CENTER_INSIDE);
            imageButton.setImageDrawable(this.mOverflow);
            String str = "listSelectorSDK21";
            if (FloatingToolbar.this.currentStyle == 0) {
                color = Theme.getColor("dialogTextBlack");
                imageButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(str), 1));
            } else if (FloatingToolbar.this.currentStyle == 2) {
                color = -328966;
                imageButton.setBackgroundDrawable(Theme.createSelectorDrawable(NUM, 1));
            } else {
                color = Theme.getColor("windowBackgroundWhiteBlackText");
                imageButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(str), 1));
            }
            this.mOverflow.setTint(color);
            this.mArrow.setTint(color);
            this.mToArrow.setTint(color);
            this.mToOverflow.setTint(color);
            imageButton.setOnClickListener(new -$$Lambda$FloatingToolbar$FloatingToolbarPopup$qbxCGqmh38_G8_8p_Vg-eZP_0Tg(this, imageButton));
            return imageButton;
        }

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
            overflowPanel.setLayoutParams(new LayoutParams(-1, -1));
            overflowPanel.setDivider(null);
            overflowPanel.setDividerHeight(0);
            overflowPanel.setAdapter(new ArrayAdapter<MenuItem>(this.mContext, 0) {
                public View getView(int i, View view, ViewGroup viewGroup) {
                    return FloatingToolbarPopup.this.mOverflowPanelViewHelper.getView((MenuItem) getItem(i), FloatingToolbarPopup.this.mOverflowPanelSize.getWidth(), view);
                }
            });
            overflowPanel.setOnItemClickListener(new -$$Lambda$FloatingToolbar$FloatingToolbarPopup$RtHxtTZRy0svpOSVJ9SzTq9Z7Rk(this, overflowPanel));
            return overflowPanel;
        }

        public /* synthetic */ void lambda$createOverflowPanel$1$FloatingToolbar$FloatingToolbarPopup(OverflowPanel overflowPanel, AdapterView adapterView, View view, int i, long j) {
            MenuItem menuItem = (MenuItem) overflowPanel.getAdapter().getItem(i);
            OnMenuItemClickListener onMenuItemClickListener = this.mOnMenuItemClickListener;
            if (onMenuItemClickListener != null) {
                onMenuItemClickListener.onMenuItemClick(menuItem);
            }
        }

        private boolean isOverflowAnimating() {
            Object obj = (!this.mOpenOverflowAnimation.hasStarted() || this.mOpenOverflowAnimation.hasEnded()) ? null : 1;
            Object obj2 = (!this.mCloseOverflowAnimation.hasStarted() || this.mCloseOverflowAnimation.hasEnded()) ? null : 1;
            if (obj == null && obj2 == null) {
                return false;
            }
            return true;
        }

        private AnimationListener createOverflowAnimationListener() {
            return new AnimationListener() {
                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationStart(Animation animation) {
                    FloatingToolbarPopup.this.mOverflowButton.setEnabled(false);
                    FloatingToolbarPopup.this.mMainPanel.setVisibility(0);
                    FloatingToolbarPopup.this.mOverflowPanel.setVisibility(0);
                }

                public void onAnimationEnd(Animation animation) {
                    FloatingToolbarPopup.this.mContentContainer.post(new -$$Lambda$FloatingToolbar$FloatingToolbarPopup$13$Zp9K0kX9lUDbB0TcGe7rcdA3FKM(this));
                }

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
            LayoutParams layoutParams = view.getLayoutParams();
            if (layoutParams == null) {
                layoutParams = new LayoutParams(0, 0);
            }
            layoutParams.width = i;
            layoutParams.height = i2;
            view.setLayoutParams(layoutParams);
        }

        private void setSize(View view, Size size) {
            setSize(view, size.getWidth(), size.getHeight());
        }

        private void setWidth(View view, int i) {
            setSize(view, i, view.getLayoutParams().height);
        }

        private void setHeight(View view, int i) {
            setSize(view, view.getLayoutParams().width, i);
        }
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

    public FloatingToolbar setOnMenuItemClickListener(OnMenuItemClickListener onMenuItemClickListener) {
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

    public FloatingToolbar setSuggestedWidth(int i) {
        double abs = (double) Math.abs(i - this.mSuggestedWidth);
        double d = (double) this.mSuggestedWidth;
        Double.isNaN(d);
        this.mWidthChanged = abs > d * 0.2d;
        this.mSuggestedWidth = i;
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

    public void setOutsideTouchable(boolean z, OnDismissListener onDismissListener) {
        if (this.mPopup.setOutsideTouchable(z, onDismissListener) && isShowing()) {
            dismiss();
            doShow();
        }
    }

    private void doShow() {
        List visibleAndEnabledMenuItems = getVisibleAndEnabledMenuItems(this.mMenu);
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
            MenuItem menuItem = (MenuItem) list.get(i);
            MenuItem menuItem2 = (MenuItem) this.mShowingMenuItems.get(i);
            if (menuItem.getItemId() != menuItem2.getItemId() || !TextUtils.equals(menuItem.getTitle(), menuItem2.getTitle()) || !Objects.equals(menuItem.getIcon(), menuItem2.getIcon()) || menuItem.getGroupId() != menuItem2.getGroupId()) {
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

    private View createMenuItemButton(Context context, MenuItem menuItem, int i) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(new LayoutParams(-2, -2));
        linearLayout.setOrientation(0);
        linearLayout.setMinimumWidth(AndroidUtilities.dp(48.0f));
        linearLayout.setMinimumHeight(AndroidUtilities.dp(48.0f));
        linearLayout.setPaddingRelative(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
        TextView textView = new TextView(context);
        textView.setGravity(17);
        textView.setSingleLine(true);
        textView.setEllipsize(TruncateAt.END);
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

    private static void updateMenuItemButton(View view, MenuItem menuItem, int i) {
        TextView textView = (TextView) ((ViewGroup) view).getChildAt(0);
        textView.setEllipsize(null);
        if (TextUtils.isEmpty(menuItem.getTitle())) {
            textView.setVisibility(8);
        } else {
            textView.setVisibility(0);
            textView.setText(menuItem.getTitle());
        }
        textView.setPaddingRelative(0, 0, 0, 0);
    }

    private ViewGroup createContentContainer(Context context) {
        RelativeLayout relativeLayout = new RelativeLayout(context);
        MarginLayoutParams marginLayoutParams = new MarginLayoutParams(-2, -2);
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
        r5 = new float[8];
        float dp2 = (float) AndroidUtilities.dp(6.0f);
        r5[0] = dp2;
        r5[1] = dp2;
        r5[2] = dp2;
        r5[3] = dp2;
        r5[4] = dp2;
        r5[5] = dp2;
        r5[6] = dp2;
        r5[7] = dp2;
        gradientDrawable.setCornerRadii(r5);
        int i = this.currentStyle;
        if (i == 0) {
            gradientDrawable.setColor(Theme.getColor("dialogBackground"));
        } else if (i == 2) {
            gradientDrawable.setColor(-NUM);
        } else if (i == 1) {
            gradientDrawable.setColor(Theme.getColor("windowBackgroundWhite"));
        }
        relativeLayout.setBackgroundDrawable(gradientDrawable);
        relativeLayout.setLayoutParams(new LayoutParams(-2, -2));
        relativeLayout.setClipToOutline(true);
        return relativeLayout;
    }

    private static PopupWindow createPopupWindow(ViewGroup viewGroup) {
        LinearLayout linearLayout = new LinearLayout(viewGroup.getContext());
        PopupWindow popupWindow = new PopupWindow(linearLayout);
        popupWindow.setClippingEnabled(false);
        popupWindow.setAnimationStyle(0);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0));
        viewGroup.setLayoutParams(new LayoutParams(-2, -2));
        linearLayout.addView(viewGroup);
        return popupWindow;
    }

    private static AnimatorSet createEnterAnimation(View view) {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(view, View.ALPHA, new float[]{0.0f, 1.0f}).setDuration(150)});
        return animatorSet;
    }

    private static AnimatorSet createExitAnimation(View view, int i, AnimatorListener animatorListener) {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(view, View.ALPHA, new float[]{1.0f, 0.0f}).setDuration(100)});
        animatorSet.setStartDelay((long) i);
        animatorSet.addListener(animatorListener);
        return animatorSet;
    }
}
