package org.telegram.messenger.support.widget;

import android.content.Context;
import android.graphics.PointF;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.SmoothScroller;
import org.telegram.messenger.support.widget.RecyclerView.SmoothScroller.Action;
import org.telegram.messenger.support.widget.RecyclerView.SmoothScroller.ScrollVectorProvider;
import org.telegram.messenger.support.widget.RecyclerView.State;

public class LinearSmoothScrollerMiddle extends SmoothScroller {
    private static final float MILLISECONDS_PER_INCH = 25.0f;
    private static final float TARGET_SEEK_EXTRA_SCROLL_RATIO = 1.2f;
    private static final int TARGET_SEEK_SCROLL_DISTANCE_PX = 10000;
    private final float MILLISECONDS_PER_PX;
    protected final DecelerateInterpolator mDecelerateInterpolator = new DecelerateInterpolator(1.5f);
    protected int mInterimTargetDx = 0;
    protected int mInterimTargetDy = 0;
    protected final LinearInterpolator mLinearInterpolator = new LinearInterpolator();
    protected PointF mTargetVector;

    private int clampApplyScroll(int i, int i2) {
        i2 = i - i2;
        return i * i2 <= 0 ? 0 : i2;
    }

    /* Access modifiers changed, original: protected */
    public void onStart() {
    }

    public LinearSmoothScrollerMiddle(Context context) {
        this.MILLISECONDS_PER_PX = 25.0f / ((float) context.getResources().getDisplayMetrics().densityDpi);
    }

    /* Access modifiers changed, original: protected */
    public void onTargetFound(View view, State state, Action action) {
        int calculateDyToMakeVisible = calculateDyToMakeVisible(view);
        int calculateTimeForDeceleration = calculateTimeForDeceleration(calculateDyToMakeVisible);
        if (calculateTimeForDeceleration > 0) {
            action.update(0, -calculateDyToMakeVisible, Math.max(400, calculateTimeForDeceleration), this.mDecelerateInterpolator);
        }
    }

    /* Access modifiers changed, original: protected */
    public void onSeekTargetStep(int i, int i2, State state, Action action) {
        if (getChildCount() == 0) {
            stop();
            return;
        }
        this.mInterimTargetDx = clampApplyScroll(this.mInterimTargetDx, i);
        this.mInterimTargetDy = clampApplyScroll(this.mInterimTargetDy, i2);
        if (this.mInterimTargetDx == 0 && this.mInterimTargetDy == 0) {
            updateActionForInterimTarget(action);
        }
    }

    /* Access modifiers changed, original: protected */
    public void onStop() {
        this.mInterimTargetDy = 0;
        this.mInterimTargetDx = 0;
        this.mTargetVector = null;
    }

    /* Access modifiers changed, original: protected */
    public int calculateTimeForDeceleration(int i) {
        double calculateTimeForScrolling = (double) calculateTimeForScrolling(i);
        Double.isNaN(calculateTimeForScrolling);
        return (int) Math.ceil(calculateTimeForScrolling / 0.3356d);
    }

    /* Access modifiers changed, original: protected */
    public int calculateTimeForScrolling(int i) {
        return (int) Math.ceil((double) (((float) Math.abs(i)) * this.MILLISECONDS_PER_PX));
    }

    /* Access modifiers changed, original: protected */
    public void updateActionForInterimTarget(Action action) {
        PointF computeScrollVectorForPosition = computeScrollVectorForPosition(getTargetPosition());
        if (computeScrollVectorForPosition == null || (computeScrollVectorForPosition.x == 0.0f && computeScrollVectorForPosition.y == 0.0f)) {
            action.jumpTo(getTargetPosition());
            stop();
            return;
        }
        normalize(computeScrollVectorForPosition);
        this.mTargetVector = computeScrollVectorForPosition;
        this.mInterimTargetDx = (int) (computeScrollVectorForPosition.x * 10000.0f);
        this.mInterimTargetDy = (int) (computeScrollVectorForPosition.y * 10000.0f);
        action.update((int) (((float) this.mInterimTargetDx) * 1.2f), (int) (((float) this.mInterimTargetDy) * 1.2f), (int) (((float) calculateTimeForScrolling(10000)) * 1.2f), this.mLinearInterpolator);
    }

    public int calculateDyToMakeVisible(View view) {
        LayoutManager layoutManager = getLayoutManager();
        if (layoutManager != null && layoutManager.canScrollVertically()) {
            int i;
            LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
            int decoratedTop = layoutManager.getDecoratedTop(view) - layoutParams.topMargin;
            int decoratedBottom = layoutManager.getDecoratedBottom(view) + layoutParams.bottomMargin;
            int height = (layoutManager.getHeight() - layoutManager.getPaddingBottom()) - layoutManager.getPaddingTop();
            int i2 = decoratedBottom - decoratedTop;
            if (i2 > height) {
                i = 0;
            } else {
                i = (height - i2) / 2;
            }
            i2 += i;
            i -= decoratedTop;
            if (i > 0) {
                return i;
            }
            i2 -= decoratedBottom;
            if (i2 < 0) {
                return i2;
            }
        }
        return 0;
    }

    public PointF computeScrollVectorForPosition(int i) {
        LayoutManager layoutManager = getLayoutManager();
        return layoutManager instanceof ScrollVectorProvider ? ((ScrollVectorProvider) layoutManager).computeScrollVectorForPosition(i) : null;
    }
}
