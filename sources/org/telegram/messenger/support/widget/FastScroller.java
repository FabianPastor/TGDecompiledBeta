package org.telegram.messenger.support.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.support.v4.view.ViewCompat;
import android.view.MotionEvent;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.telegram.messenger.support.widget.RecyclerView.ItemDecoration;
import org.telegram.messenger.support.widget.RecyclerView.OnItemTouchListener;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;

class FastScroller extends ItemDecoration implements OnItemTouchListener {
    private static final int ANIMATION_STATE_FADING_IN = 1;
    private static final int ANIMATION_STATE_FADING_OUT = 3;
    private static final int ANIMATION_STATE_IN = 2;
    private static final int ANIMATION_STATE_OUT = 0;
    private static final int DRAG_NONE = 0;
    private static final int DRAG_X = 1;
    private static final int DRAG_Y = 2;
    private static final int[] EMPTY_STATE_SET = new int[0];
    private static final int HIDE_DELAY_AFTER_DRAGGING_MS = 1200;
    private static final int HIDE_DELAY_AFTER_VISIBLE_MS = 1500;
    private static final int HIDE_DURATION_MS = 500;
    private static final int[] PRESSED_STATE_SET = new int[]{16842919};
    private static final int SCROLLBAR_FULL_OPAQUE = 255;
    private static final int SHOW_DURATION_MS = 500;
    private static final int STATE_DRAGGING = 2;
    private static final int STATE_HIDDEN = 0;
    private static final int STATE_VISIBLE = 1;
    private int mAnimationState = 0;
    private int mDragState = 0;
    private final Runnable mHideRunnable = new C06561();
    float mHorizontalDragX;
    private final int[] mHorizontalRange = new int[2];
    int mHorizontalThumbCenterX;
    private final StateListDrawable mHorizontalThumbDrawable;
    private final int mHorizontalThumbHeight;
    int mHorizontalThumbWidth;
    private final Drawable mHorizontalTrackDrawable;
    private final int mHorizontalTrackHeight;
    private final int mMargin;
    private boolean mNeedHorizontalScrollbar = false;
    private boolean mNeedVerticalScrollbar = false;
    private final OnScrollListener mOnScrollListener = new C18662();
    private RecyclerView mRecyclerView;
    private int mRecyclerViewHeight = 0;
    private int mRecyclerViewWidth = 0;
    private final int mScrollbarMinimumRange;
    private final ValueAnimator mShowHideAnimator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
    private int mState = 0;
    float mVerticalDragY;
    private final int[] mVerticalRange = new int[2];
    int mVerticalThumbCenterY;
    private final StateListDrawable mVerticalThumbDrawable;
    int mVerticalThumbHeight;
    private final int mVerticalThumbWidth;
    private final Drawable mVerticalTrackDrawable;
    private final int mVerticalTrackWidth;

    /* renamed from: org.telegram.messenger.support.widget.FastScroller$1 */
    class C06561 implements Runnable {
        C06561() {
        }

        public void run() {
            FastScroller.this.hide(500);
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    private @interface AnimationState {
    }

    private class AnimatorListener extends AnimatorListenerAdapter {
        private boolean mCanceled;

        private AnimatorListener() {
            this.mCanceled = null;
        }

        public void onAnimationEnd(Animator animation) {
            if (this.mCanceled) {
                this.mCanceled = false;
                return;
            }
            if (((Float) FastScroller.this.mShowHideAnimator.getAnimatedValue()).floatValue() == 0.0f) {
                FastScroller.this.mAnimationState = 0;
                FastScroller.this.setState(0);
            } else {
                FastScroller.this.mAnimationState = 2;
                FastScroller.this.requestRedraw();
            }
        }

        public void onAnimationCancel(Animator animation) {
            this.mCanceled = true;
        }
    }

    private class AnimatorUpdater implements AnimatorUpdateListener {
        private AnimatorUpdater() {
        }

        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            int alpha = (int) (255.0f * ((Float) valueAnimator.getAnimatedValue()).floatValue());
            FastScroller.this.mVerticalThumbDrawable.setAlpha(alpha);
            FastScroller.this.mVerticalTrackDrawable.setAlpha(alpha);
            FastScroller.this.requestRedraw();
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    private @interface DragState {
    }

    @Retention(RetentionPolicy.SOURCE)
    private @interface State {
    }

    /* renamed from: org.telegram.messenger.support.widget.FastScroller$2 */
    class C18662 extends OnScrollListener {
        C18662() {
        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            FastScroller.this.updateScrollPosition(recyclerView.computeHorizontalScrollOffset(), recyclerView.computeVerticalScrollOffset());
        }
    }

    FastScroller(RecyclerView recyclerView, StateListDrawable verticalThumbDrawable, Drawable verticalTrackDrawable, StateListDrawable horizontalThumbDrawable, Drawable horizontalTrackDrawable, int defaultWidth, int scrollbarMinimumRange, int margin) {
        this.mVerticalThumbDrawable = verticalThumbDrawable;
        this.mVerticalTrackDrawable = verticalTrackDrawable;
        this.mHorizontalThumbDrawable = horizontalThumbDrawable;
        this.mHorizontalTrackDrawable = horizontalTrackDrawable;
        this.mVerticalThumbWidth = Math.max(defaultWidth, verticalThumbDrawable.getIntrinsicWidth());
        this.mVerticalTrackWidth = Math.max(defaultWidth, verticalTrackDrawable.getIntrinsicWidth());
        this.mHorizontalThumbHeight = Math.max(defaultWidth, horizontalThumbDrawable.getIntrinsicWidth());
        this.mHorizontalTrackHeight = Math.max(defaultWidth, horizontalTrackDrawable.getIntrinsicWidth());
        this.mScrollbarMinimumRange = scrollbarMinimumRange;
        this.mMargin = margin;
        this.mVerticalThumbDrawable.setAlpha(255);
        this.mVerticalTrackDrawable.setAlpha(255);
        this.mShowHideAnimator.addListener(new AnimatorListener());
        this.mShowHideAnimator.addUpdateListener(new AnimatorUpdater());
        attachToRecyclerView(recyclerView);
    }

    public void attachToRecyclerView(RecyclerView recyclerView) {
        if (this.mRecyclerView != recyclerView) {
            if (this.mRecyclerView != null) {
                destroyCallbacks();
            }
            this.mRecyclerView = recyclerView;
            if (this.mRecyclerView != null) {
                setupCallbacks();
            }
        }
    }

    private void setupCallbacks() {
        this.mRecyclerView.addItemDecoration(this);
        this.mRecyclerView.addOnItemTouchListener(this);
        this.mRecyclerView.addOnScrollListener(this.mOnScrollListener);
    }

    private void destroyCallbacks() {
        this.mRecyclerView.removeItemDecoration(this);
        this.mRecyclerView.removeOnItemTouchListener(this);
        this.mRecyclerView.removeOnScrollListener(this.mOnScrollListener);
        cancelHide();
    }

    private void requestRedraw() {
        this.mRecyclerView.invalidate();
    }

    private void setState(int state) {
        if (state == 2 && this.mState != 2) {
            this.mVerticalThumbDrawable.setState(PRESSED_STATE_SET);
            cancelHide();
        }
        if (state == 0) {
            requestRedraw();
        } else {
            show();
        }
        if (this.mState == 2 && state != 2) {
            this.mVerticalThumbDrawable.setState(EMPTY_STATE_SET);
            resetHideDelay(HIDE_DELAY_AFTER_DRAGGING_MS);
        } else if (state == 1) {
            resetHideDelay(HIDE_DELAY_AFTER_VISIBLE_MS);
        }
        this.mState = state;
    }

    private boolean isLayoutRTL() {
        return ViewCompat.getLayoutDirection(this.mRecyclerView) == 1;
    }

    public boolean isDragging() {
        return this.mState == 2;
    }

    boolean isVisible() {
        return this.mState == 1;
    }

    boolean isHidden() {
        return this.mState == 0;
    }

    public void show() {
        int i = this.mAnimationState;
        if (i != 0) {
            if (i == 3) {
                this.mShowHideAnimator.cancel();
            } else {
                return;
            }
        }
        this.mAnimationState = 1;
        this.mShowHideAnimator.setFloatValues(new float[]{((Float) this.mShowHideAnimator.getAnimatedValue()).floatValue(), 1.0f});
        this.mShowHideAnimator.setDuration(500);
        this.mShowHideAnimator.setStartDelay(0);
        this.mShowHideAnimator.start();
    }

    public void hide() {
        hide(0);
    }

    void hide(int duration) {
        switch (this.mAnimationState) {
            case 1:
                this.mShowHideAnimator.cancel();
                break;
            case 2:
                break;
            default:
                return;
        }
        this.mAnimationState = 3;
        this.mShowHideAnimator.setFloatValues(new float[]{((Float) this.mShowHideAnimator.getAnimatedValue()).floatValue(), 0.0f});
        this.mShowHideAnimator.setDuration((long) duration);
        this.mShowHideAnimator.start();
    }

    private void cancelHide() {
        this.mRecyclerView.removeCallbacks(this.mHideRunnable);
    }

    private void resetHideDelay(int delay) {
        cancelHide();
        this.mRecyclerView.postDelayed(this.mHideRunnable, (long) delay);
    }

    public void onDrawOver(Canvas canvas, RecyclerView parent, org.telegram.messenger.support.widget.RecyclerView.State state) {
        if (this.mRecyclerViewWidth == this.mRecyclerView.getWidth()) {
            if (this.mRecyclerViewHeight == this.mRecyclerView.getHeight()) {
                if (this.mAnimationState != 0) {
                    if (this.mNeedVerticalScrollbar) {
                        drawVerticalScrollbar(canvas);
                    }
                    if (this.mNeedHorizontalScrollbar) {
                        drawHorizontalScrollbar(canvas);
                    }
                }
                return;
            }
        }
        this.mRecyclerViewWidth = this.mRecyclerView.getWidth();
        this.mRecyclerViewHeight = this.mRecyclerView.getHeight();
        setState(0);
    }

    private void drawVerticalScrollbar(Canvas canvas) {
        int left = this.mRecyclerViewWidth - this.mVerticalThumbWidth;
        int top = this.mVerticalThumbCenterY - (this.mVerticalThumbHeight / 2);
        this.mVerticalThumbDrawable.setBounds(0, 0, this.mVerticalThumbWidth, this.mVerticalThumbHeight);
        this.mVerticalTrackDrawable.setBounds(0, 0, this.mVerticalTrackWidth, this.mRecyclerViewHeight);
        if (isLayoutRTL()) {
            this.mVerticalTrackDrawable.draw(canvas);
            canvas.translate((float) this.mVerticalThumbWidth, (float) top);
            canvas.scale(-1.0f, 1.0f);
            this.mVerticalThumbDrawable.draw(canvas);
            canvas.scale(1.0f, 1.0f);
            canvas.translate((float) (-this.mVerticalThumbWidth), (float) (-top));
            return;
        }
        canvas.translate((float) left, 0.0f);
        this.mVerticalTrackDrawable.draw(canvas);
        canvas.translate(0.0f, (float) top);
        this.mVerticalThumbDrawable.draw(canvas);
        canvas.translate((float) (-left), (float) (-top));
    }

    private void drawHorizontalScrollbar(Canvas canvas) {
        int top = this.mRecyclerViewHeight - this.mHorizontalThumbHeight;
        int left = this.mHorizontalThumbCenterX - (this.mHorizontalThumbWidth / 2);
        this.mHorizontalThumbDrawable.setBounds(0, 0, this.mHorizontalThumbWidth, this.mHorizontalThumbHeight);
        this.mHorizontalTrackDrawable.setBounds(0, 0, this.mRecyclerViewWidth, this.mHorizontalTrackHeight);
        canvas.translate(0.0f, (float) top);
        this.mHorizontalTrackDrawable.draw(canvas);
        canvas.translate((float) left, 0.0f);
        this.mHorizontalThumbDrawable.draw(canvas);
        canvas.translate((float) (-left), (float) (-top));
    }

    void updateScrollPosition(int offsetX, int offsetY) {
        int verticalContentLength = this.mRecyclerView.computeVerticalScrollRange();
        int verticalVisibleLength = this.mRecyclerViewHeight;
        boolean z = verticalContentLength - verticalVisibleLength > 0 && this.mRecyclerViewHeight >= this.mScrollbarMinimumRange;
        this.mNeedVerticalScrollbar = z;
        int horizontalContentLength = this.mRecyclerView.computeHorizontalScrollRange();
        int horizontalVisibleLength = this.mRecyclerViewWidth;
        boolean z2 = horizontalContentLength - horizontalVisibleLength > 0 && this.mRecyclerViewWidth >= this.mScrollbarMinimumRange;
        this.mNeedHorizontalScrollbar = z2;
        if (this.mNeedVerticalScrollbar || this.mNeedHorizontalScrollbar) {
            if (this.mNeedVerticalScrollbar) {
                this.mVerticalThumbCenterY = (int) ((((float) verticalVisibleLength) * (((float) offsetY) + (((float) verticalVisibleLength) / 2.0f))) / ((float) verticalContentLength));
                this.mVerticalThumbHeight = Math.min(verticalVisibleLength, (verticalVisibleLength * verticalVisibleLength) / verticalContentLength);
            }
            if (this.mNeedHorizontalScrollbar) {
                this.mHorizontalThumbCenterX = (int) ((((float) horizontalVisibleLength) * (((float) offsetX) + (((float) horizontalVisibleLength) / 2.0f))) / ((float) horizontalContentLength));
                this.mHorizontalThumbWidth = Math.min(horizontalVisibleLength, (horizontalVisibleLength * horizontalVisibleLength) / horizontalContentLength);
            }
            if (this.mState == 0 || this.mState == 1) {
                setState(1);
            }
            return;
        }
        if (this.mState != 0) {
            setState(0);
        }
    }

    public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent ev) {
        boolean handled = false;
        if (this.mState == 1) {
            boolean insideVerticalThumb = isPointInsideVerticalThumb(ev.getX(), ev.getY());
            boolean insideHorizontalThumb = isPointInsideHorizontalThumb(ev.getX(), ev.getY());
            if (ev.getAction() == 0 && (insideVerticalThumb || insideHorizontalThumb)) {
                if (insideHorizontalThumb) {
                    this.mDragState = 1;
                    this.mHorizontalDragX = (float) ((int) ev.getX());
                } else if (insideVerticalThumb) {
                    this.mDragState = 2;
                    this.mVerticalDragY = (float) ((int) ev.getY());
                }
                setState(2);
                handled = true;
            }
        } else if (this.mState == 2) {
            handled = true;
        }
        return handled;
    }

    public void onTouchEvent(RecyclerView recyclerView, MotionEvent me) {
        if (this.mState != 0) {
            if (me.getAction() == 0) {
                boolean insideVerticalThumb = isPointInsideVerticalThumb(me.getX(), me.getY());
                boolean insideHorizontalThumb = isPointInsideHorizontalThumb(me.getX(), me.getY());
                if (insideVerticalThumb || insideHorizontalThumb) {
                    if (insideHorizontalThumb) {
                        this.mDragState = 1;
                        this.mHorizontalDragX = (float) ((int) me.getX());
                    } else if (insideVerticalThumb) {
                        this.mDragState = 2;
                        this.mVerticalDragY = (float) ((int) me.getY());
                    }
                    setState(2);
                }
            } else if (me.getAction() == 1 && this.mState == 2) {
                this.mVerticalDragY = 0.0f;
                this.mHorizontalDragX = 0.0f;
                setState(1);
                this.mDragState = 0;
            } else if (me.getAction() == 2 && this.mState == 2) {
                show();
                if (this.mDragState == 1) {
                    horizontalScrollTo(me.getX());
                }
                if (this.mDragState == 2) {
                    verticalScrollTo(me.getY());
                }
            }
        }
    }

    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }

    private void verticalScrollTo(float y) {
        int[] scrollbarRange = getVerticalRange();
        y = Math.max((float) scrollbarRange[0], Math.min((float) scrollbarRange[1], y));
        if (Math.abs(((float) this.mVerticalThumbCenterY) - y) >= 2.0f) {
            int scrollingBy = scrollTo(this.mVerticalDragY, y, scrollbarRange, this.mRecyclerView.computeVerticalScrollRange(), this.mRecyclerView.computeVerticalScrollOffset(), this.mRecyclerViewHeight);
            if (scrollingBy != 0) {
                this.mRecyclerView.scrollBy(0, scrollingBy);
            }
            this.mVerticalDragY = y;
        }
    }

    private void horizontalScrollTo(float x) {
        int[] scrollbarRange = getHorizontalRange();
        x = Math.max((float) scrollbarRange[0], Math.min((float) scrollbarRange[1], x));
        if (Math.abs(((float) this.mHorizontalThumbCenterX) - x) >= 2.0f) {
            int scrollingBy = scrollTo(this.mHorizontalDragX, x, scrollbarRange, this.mRecyclerView.computeHorizontalScrollRange(), this.mRecyclerView.computeHorizontalScrollOffset(), this.mRecyclerViewWidth);
            if (scrollingBy != 0) {
                this.mRecyclerView.scrollBy(scrollingBy, 0);
            }
            this.mHorizontalDragX = x;
        }
    }

    private int scrollTo(float oldDragPos, float newDragPos, int[] scrollbarRange, int scrollRange, int scrollOffset, int viewLength) {
        int scrollbarLength = scrollbarRange[1] - scrollbarRange[0];
        if (scrollbarLength == 0) {
            return 0;
        }
        int totalPossibleOffset = scrollRange - viewLength;
        int scrollingBy = (int) (((float) totalPossibleOffset) * ((newDragPos - oldDragPos) / ((float) scrollbarLength)));
        int absoluteOffset = scrollOffset + scrollingBy;
        if (absoluteOffset >= totalPossibleOffset || absoluteOffset < 0) {
            return 0;
        }
        return scrollingBy;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    boolean isPointInsideVerticalThumb(float x, float y) {
        if (isLayoutRTL()) {
            if (x <= ((float) (this.mVerticalThumbWidth / 2))) {
            }
            return false;
        }
        if (y >= ((float) (this.mVerticalThumbCenterY - (this.mVerticalThumbHeight / 2))) && y <= ((float) (this.mVerticalThumbCenterY + (this.mVerticalThumbHeight / 2)))) {
            return true;
        }
        return false;
    }

    boolean isPointInsideHorizontalThumb(float x, float y) {
        return y >= ((float) (this.mRecyclerViewHeight - this.mHorizontalThumbHeight)) && x >= ((float) (this.mHorizontalThumbCenterX - (this.mHorizontalThumbWidth / 2))) && x <= ((float) (this.mHorizontalThumbCenterX + (this.mHorizontalThumbWidth / 2)));
    }

    Drawable getHorizontalTrackDrawable() {
        return this.mHorizontalTrackDrawable;
    }

    Drawable getHorizontalThumbDrawable() {
        return this.mHorizontalThumbDrawable;
    }

    Drawable getVerticalTrackDrawable() {
        return this.mVerticalTrackDrawable;
    }

    Drawable getVerticalThumbDrawable() {
        return this.mVerticalThumbDrawable;
    }

    private int[] getVerticalRange() {
        this.mVerticalRange[0] = this.mMargin;
        this.mVerticalRange[1] = this.mRecyclerViewHeight - this.mMargin;
        return this.mVerticalRange;
    }

    private int[] getHorizontalRange() {
        this.mHorizontalRange[0] = this.mMargin;
        this.mHorizontalRange[1] = this.mRecyclerViewWidth - this.mMargin;
        return this.mHorizontalRange;
    }
}
