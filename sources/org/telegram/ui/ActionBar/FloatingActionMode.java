package org.telegram.ui.ActionBar;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import java.util.Arrays;
import org.telegram.messenger.AndroidUtilities;

public final class FloatingActionMode extends ActionMode {
    private static final int MAX_HIDE_DURATION = 3000;
    private static final int MOVING_HIDE_DELAY = 50;
    private final int mBottomAllowance;
    private final ActionMode.Callback2 mCallback;
    private final Rect mContentRect;
    private final Rect mContentRectOnScreen;
    private final Context mContext;
    private final Point mDisplaySize;
    private FloatingToolbar mFloatingToolbar;
    /* access modifiers changed from: private */
    public FloatingToolbarVisibilityHelper mFloatingToolbarVisibilityHelper;
    private final Runnable mHideOff = new Runnable() {
        public void run() {
            if (FloatingActionMode.this.isViewStillActive()) {
                FloatingActionMode.this.mFloatingToolbarVisibilityHelper.setHideRequested(false);
                FloatingActionMode.this.mFloatingToolbarVisibilityHelper.updateToolbarVisibility();
            }
        }
    };
    private final Menu mMenu;
    private final Runnable mMovingOff = new Runnable() {
        public void run() {
            if (FloatingActionMode.this.isViewStillActive()) {
                FloatingActionMode.this.mFloatingToolbarVisibilityHelper.setMoving(false);
                FloatingActionMode.this.mFloatingToolbarVisibilityHelper.updateToolbarVisibility();
            }
        }
    };
    private final View mOriginatingView;
    private final Rect mPreviousContentRectOnScreen;
    private final int[] mPreviousViewPositionOnScreen;
    private final Rect mPreviousViewRectOnScreen;
    private final int[] mRootViewPositionOnScreen;
    private final Rect mScreenRect;
    private final int[] mViewPositionOnScreen;
    private final Rect mViewRectOnScreen;

    public FloatingActionMode(Context context, ActionMode.Callback2 callback, View originatingView, FloatingToolbar floatingToolbar) {
        this.mContext = context;
        this.mCallback = callback;
        PopupMenu p = new PopupMenu(context, (View) null);
        this.mMenu = p.getMenu();
        setType(1);
        p.setOnMenuItemClickListener(new FloatingActionMode$$ExternalSyntheticLambda1(this));
        this.mContentRect = new Rect();
        this.mContentRectOnScreen = new Rect();
        this.mPreviousContentRectOnScreen = new Rect();
        int[] iArr = new int[2];
        this.mViewPositionOnScreen = iArr;
        this.mPreviousViewPositionOnScreen = new int[2];
        this.mRootViewPositionOnScreen = new int[2];
        this.mViewRectOnScreen = new Rect();
        this.mPreviousViewRectOnScreen = new Rect();
        this.mScreenRect = new Rect();
        this.mOriginatingView = originatingView;
        originatingView.getLocationOnScreen(iArr);
        this.mBottomAllowance = AndroidUtilities.dp(20.0f);
        this.mDisplaySize = new Point();
        setFloatingToolbar(floatingToolbar);
    }

    /* renamed from: lambda$new$0$org-telegram-ui-ActionBar-FloatingActionMode  reason: not valid java name */
    public /* synthetic */ boolean m2572lambda$new$0$orgtelegramuiActionBarFloatingActionMode(MenuItem menuItem) {
        return this.mCallback.onActionItemClicked(this, menuItem);
    }

    private void setFloatingToolbar(FloatingToolbar floatingToolbar) {
        this.mFloatingToolbar = floatingToolbar.setMenu(this.mMenu).setOnMenuItemClickListener(new FloatingActionMode$$ExternalSyntheticLambda0(this));
        FloatingToolbarVisibilityHelper floatingToolbarVisibilityHelper = new FloatingToolbarVisibilityHelper(this.mFloatingToolbar);
        this.mFloatingToolbarVisibilityHelper = floatingToolbarVisibilityHelper;
        floatingToolbarVisibilityHelper.activate();
    }

    /* renamed from: lambda$setFloatingToolbar$1$org-telegram-ui-ActionBar-FloatingActionMode  reason: not valid java name */
    public /* synthetic */ boolean m2573x15cc3d2c(MenuItem item) {
        return this.mCallback.onActionItemClicked(this, item);
    }

    public void setTitle(CharSequence title) {
    }

    public void setTitle(int resId) {
    }

    public void setSubtitle(CharSequence subtitle) {
    }

    public void setSubtitle(int resId) {
    }

    public void setCustomView(View view) {
    }

    public void invalidate() {
        this.mCallback.onPrepareActionMode(this, this.mMenu);
        invalidateContentRect();
    }

    public void invalidateContentRect() {
        this.mCallback.onGetContentRect(this, this.mOriginatingView, this.mContentRect);
        if (this.mContentRect.left == 0 && this.mContentRect.right == 0) {
            this.mContentRect.left = 1;
            this.mContentRect.right = 1;
        }
        repositionToolbar();
    }

    public void updateViewLocationInWindow() {
        this.mOriginatingView.getLocationOnScreen(this.mViewPositionOnScreen);
        this.mOriginatingView.getRootView().getLocationOnScreen(this.mRootViewPositionOnScreen);
        this.mOriginatingView.getGlobalVisibleRect(this.mViewRectOnScreen);
        Rect rect = this.mViewRectOnScreen;
        int[] iArr = this.mRootViewPositionOnScreen;
        rect.offset(iArr[0], iArr[1]);
        if (!Arrays.equals(this.mViewPositionOnScreen, this.mPreviousViewPositionOnScreen) || !this.mViewRectOnScreen.equals(this.mPreviousViewRectOnScreen)) {
            repositionToolbar();
            int[] iArr2 = this.mPreviousViewPositionOnScreen;
            int[] iArr3 = this.mViewPositionOnScreen;
            iArr2[0] = iArr3[0];
            iArr2[1] = iArr3[1];
            this.mPreviousViewRectOnScreen.set(this.mViewRectOnScreen);
        }
    }

    private void repositionToolbar() {
        this.mContentRectOnScreen.set(this.mContentRect);
        ViewParent parent = this.mOriginatingView.getParent();
        if (parent instanceof ViewGroup) {
            parent.getChildVisibleRect(this.mOriginatingView, this.mContentRectOnScreen, (Point) null);
            Rect rect = this.mContentRectOnScreen;
            int[] iArr = this.mRootViewPositionOnScreen;
            rect.offset(iArr[0], iArr[1]);
        } else {
            Rect rect2 = this.mContentRectOnScreen;
            int[] iArr2 = this.mViewPositionOnScreen;
            rect2.offset(iArr2[0], iArr2[1]);
        }
        if (isContentRectWithinBounds()) {
            this.mFloatingToolbarVisibilityHelper.setOutOfBounds(false);
            Rect rect3 = this.mContentRectOnScreen;
            rect3.set(Math.max(rect3.left, this.mViewRectOnScreen.left), Math.max(this.mContentRectOnScreen.top, this.mViewRectOnScreen.top), Math.min(this.mContentRectOnScreen.right, this.mViewRectOnScreen.right), Math.min(this.mContentRectOnScreen.bottom, this.mViewRectOnScreen.bottom + this.mBottomAllowance));
            if (!this.mContentRectOnScreen.equals(this.mPreviousContentRectOnScreen)) {
                this.mOriginatingView.removeCallbacks(this.mMovingOff);
                this.mFloatingToolbarVisibilityHelper.setMoving(true);
                this.mOriginatingView.postDelayed(this.mMovingOff, 50);
                this.mFloatingToolbar.setContentRect(this.mContentRectOnScreen);
                this.mFloatingToolbar.updateLayout();
            }
        } else {
            this.mFloatingToolbarVisibilityHelper.setOutOfBounds(true);
            this.mContentRectOnScreen.setEmpty();
        }
        this.mFloatingToolbarVisibilityHelper.updateToolbarVisibility();
        this.mPreviousContentRectOnScreen.set(this.mContentRectOnScreen);
    }

    private boolean isContentRectWithinBounds() {
        ((WindowManager) this.mContext.getSystemService(WindowManager.class)).getDefaultDisplay().getRealSize(this.mDisplaySize);
        this.mScreenRect.set(0, 0, this.mDisplaySize.x, this.mDisplaySize.y);
        if (!intersectsClosed(this.mContentRectOnScreen, this.mScreenRect) || !intersectsClosed(this.mContentRectOnScreen, this.mViewRectOnScreen)) {
            return false;
        }
        return true;
    }

    private static boolean intersectsClosed(Rect a, Rect b) {
        return a.left <= b.right && b.left <= a.right && a.top <= b.bottom && b.top <= a.bottom;
    }

    public void hide(long duration) {
        if (duration == -1) {
            duration = ViewConfiguration.getDefaultActionModeHideDuration();
        }
        long duration2 = Math.min(3000, duration);
        this.mOriginatingView.removeCallbacks(this.mHideOff);
        if (duration2 <= 0) {
            this.mHideOff.run();
            return;
        }
        this.mFloatingToolbarVisibilityHelper.setHideRequested(true);
        this.mFloatingToolbarVisibilityHelper.updateToolbarVisibility();
        this.mOriginatingView.postDelayed(this.mHideOff, duration2);
    }

    public void setOutsideTouchable(boolean outsideTouchable, PopupWindow.OnDismissListener onDismiss) {
        this.mFloatingToolbar.setOutsideTouchable(outsideTouchable, onDismiss);
    }

    public void onWindowFocusChanged(boolean hasWindowFocus) {
        this.mFloatingToolbarVisibilityHelper.setWindowFocused(hasWindowFocus);
        this.mFloatingToolbarVisibilityHelper.updateToolbarVisibility();
    }

    public void finish() {
        reset();
        this.mCallback.onDestroyActionMode(this);
    }

    public Menu getMenu() {
        return this.mMenu;
    }

    public CharSequence getTitle() {
        return null;
    }

    public CharSequence getSubtitle() {
        return null;
    }

    public View getCustomView() {
        return null;
    }

    public MenuInflater getMenuInflater() {
        return new MenuInflater(this.mContext);
    }

    private void reset() {
        this.mFloatingToolbar.dismiss();
        this.mFloatingToolbarVisibilityHelper.deactivate();
        this.mOriginatingView.removeCallbacks(this.mMovingOff);
        this.mOriginatingView.removeCallbacks(this.mHideOff);
    }

    /* access modifiers changed from: private */
    public boolean isViewStillActive() {
        return this.mOriginatingView.getWindowVisibility() == 0 && this.mOriginatingView.isShown();
    }

    private static final class FloatingToolbarVisibilityHelper {
        private static final long MIN_SHOW_DURATION_FOR_MOVE_HIDE = 500;
        private boolean mActive;
        private boolean mHideRequested;
        private long mLastShowTime;
        private boolean mMoving;
        private boolean mOutOfBounds;
        private final FloatingToolbar mToolbar;
        private boolean mWindowFocused = true;

        public FloatingToolbarVisibilityHelper(FloatingToolbar toolbar) {
            this.mToolbar = toolbar;
        }

        public void activate() {
            this.mHideRequested = false;
            this.mMoving = false;
            this.mOutOfBounds = false;
            this.mWindowFocused = true;
            this.mActive = true;
        }

        public void deactivate() {
            this.mActive = false;
            this.mToolbar.dismiss();
        }

        public void setHideRequested(boolean hide) {
            this.mHideRequested = hide;
        }

        public void setMoving(boolean moving) {
            boolean showingLongEnough = System.currentTimeMillis() - this.mLastShowTime > 500;
            if (!moving || showingLongEnough) {
                this.mMoving = moving;
            }
        }

        public void setOutOfBounds(boolean outOfBounds) {
            this.mOutOfBounds = outOfBounds;
        }

        public void setWindowFocused(boolean windowFocused) {
            this.mWindowFocused = windowFocused;
        }

        public void updateToolbarVisibility() {
            if (this.mActive) {
                if (this.mHideRequested || this.mMoving || this.mOutOfBounds || !this.mWindowFocused) {
                    this.mToolbar.hide();
                    return;
                }
                this.mToolbar.show();
                this.mLastShowTime = System.currentTimeMillis();
            }
        }
    }
}
