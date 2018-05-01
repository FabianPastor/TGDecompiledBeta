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

public class LinearSmoothScrollerEnd extends SmoothScroller {
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

    protected void onStart() {
    }

    public LinearSmoothScrollerEnd(Context context) {
        this.MILLISECONDS_PER_PX = MILLISECONDS_PER_INCH / ((float) context.getResources().getDisplayMetrics().densityDpi);
    }

    protected void onTargetFound(View view, State state, Action action) {
        view = calculateDxToMakeVisible(view);
        state = calculateTimeForDeceleration(view);
        if (state > null) {
            action.update(-view, 0, Math.max(400, state), this.mDecelerateInterpolator);
        }
    }

    protected void onSeekTargetStep(int i, int i2, State state, Action action) {
        if (getChildCount() == null) {
            stop();
            return;
        }
        this.mInterimTargetDx = clampApplyScroll(this.mInterimTargetDx, i);
        this.mInterimTargetDy = clampApplyScroll(this.mInterimTargetDy, i2);
        if (this.mInterimTargetDx == 0 && this.mInterimTargetDy == 0) {
            updateActionForInterimTarget(action);
        }
    }

    protected void onStop() {
        this.mInterimTargetDy = 0;
        this.mInterimTargetDx = 0;
        this.mTargetVector = null;
    }

    protected int calculateTimeForDeceleration(int i) {
        return (int) Math.ceil(((double) calculateTimeForScrolling(i)) / 0.3356d);
    }

    protected int calculateTimeForScrolling(int i) {
        return (int) Math.ceil((double) (((float) Math.abs(i)) * this.MILLISECONDS_PER_PX));
    }

    protected void updateActionForInterimTarget(Action action) {
        PointF computeScrollVectorForPosition = computeScrollVectorForPosition(getTargetPosition());
        if (computeScrollVectorForPosition != null) {
            if (computeScrollVectorForPosition.x != 0.0f || computeScrollVectorForPosition.y != 0.0f) {
                normalize(computeScrollVectorForPosition);
                this.mTargetVector = computeScrollVectorForPosition;
                this.mInterimTargetDx = (int) (computeScrollVectorForPosition.x * 10000.0f);
                this.mInterimTargetDy = (int) (10000.0f * computeScrollVectorForPosition.y);
                action.update((int) (((float) this.mInterimTargetDx) * TARGET_SEEK_EXTRA_SCROLL_RATIO), (int) (((float) this.mInterimTargetDy) * TARGET_SEEK_EXTRA_SCROLL_RATIO), (int) (((float) calculateTimeForScrolling(10000)) * TARGET_SEEK_EXTRA_SCROLL_RATIO), this.mLinearInterpolator);
                return;
            }
        }
        action.jumpTo(getTargetPosition());
        stop();
    }

    public int calculateDxToMakeVisible(View view) {
        LayoutManager layoutManager = getLayoutManager();
        if (layoutManager != null) {
            if (layoutManager.canScrollHorizontally()) {
                LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
                int decoratedLeft = layoutManager.getDecoratedLeft(view) - layoutParams.leftMargin;
                view = layoutManager.getDecoratedRight(view) + layoutParams.rightMargin;
                int paddingLeft = layoutManager.getPaddingLeft();
                int width = layoutManager.getWidth() - layoutManager.getPaddingRight();
                if (decoratedLeft > paddingLeft && view < width) {
                    return 0;
                }
                int i = view - decoratedLeft;
                width = (width - paddingLeft) - i;
                i += width;
                width -= decoratedLeft;
                if (width > 0) {
                    return width;
                }
                i -= view;
                if (i < 0) {
                    return i;
                }
                return 0;
            }
        }
        return 0;
    }

    public PointF computeScrollVectorForPosition(int i) {
        LayoutManager layoutManager = getLayoutManager();
        return layoutManager instanceof ScrollVectorProvider ? ((ScrollVectorProvider) layoutManager).computeScrollVectorForPosition(i) : 0;
    }
}
