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

    public LinearSmoothScrollerEnd(Context context) {
        this.MILLISECONDS_PER_PX = MILLISECONDS_PER_INCH / ((float) context.getResources().getDisplayMetrics().densityDpi);
    }

    protected void onStart() {
    }

    protected void onTargetFound(View targetView, State state, Action action) {
        int dx = calculateDxToMakeVisible(targetView);
        int time = calculateTimeForDeceleration(dx);
        if (time > 0) {
            action.update(-dx, 0, Math.max(400, time), this.mDecelerateInterpolator);
        }
    }

    protected void onSeekTargetStep(int dx, int dy, State state, Action action) {
        if (getChildCount() == 0) {
            stop();
            return;
        }
        this.mInterimTargetDx = clampApplyScroll(this.mInterimTargetDx, dx);
        this.mInterimTargetDy = clampApplyScroll(this.mInterimTargetDy, dy);
        if (this.mInterimTargetDx == 0 && this.mInterimTargetDy == 0) {
            updateActionForInterimTarget(action);
        }
    }

    protected void onStop() {
        this.mInterimTargetDy = 0;
        this.mInterimTargetDx = 0;
        this.mTargetVector = null;
    }

    protected int calculateTimeForDeceleration(int dx) {
        return (int) Math.ceil(((double) calculateTimeForScrolling(dx)) / 0.3356d);
    }

    protected int calculateTimeForScrolling(int dx) {
        return (int) Math.ceil((double) (((float) Math.abs(dx)) * this.MILLISECONDS_PER_PX));
    }

    protected void updateActionForInterimTarget(Action action) {
        PointF scrollVector = computeScrollVectorForPosition(getTargetPosition());
        if (scrollVector != null) {
            if (scrollVector.x != 0.0f || scrollVector.y != 0.0f) {
                normalize(scrollVector);
                this.mTargetVector = scrollVector;
                this.mInterimTargetDx = (int) (scrollVector.x * 10000.0f);
                this.mInterimTargetDy = (int) (10000.0f * scrollVector.y);
                action.update((int) (((float) this.mInterimTargetDx) * TARGET_SEEK_EXTRA_SCROLL_RATIO), (int) (((float) this.mInterimTargetDy) * TARGET_SEEK_EXTRA_SCROLL_RATIO), (int) (((float) calculateTimeForScrolling(10000)) * TARGET_SEEK_EXTRA_SCROLL_RATIO), this.mLinearInterpolator);
                return;
            }
        }
        action.jumpTo(getTargetPosition());
        stop();
    }

    private int clampApplyScroll(int tmpDt, int dt) {
        int before = tmpDt;
        tmpDt -= dt;
        if (before * tmpDt <= 0) {
            return 0;
        }
        return tmpDt;
    }

    public int calculateDxToMakeVisible(View view) {
        LayoutManager layoutManager = getLayoutManager();
        if (layoutManager != null) {
            if (layoutManager.canScrollHorizontally()) {
                LayoutParams params = (LayoutParams) view.getLayoutParams();
                int left = layoutManager.getDecoratedLeft(view) - params.leftMargin;
                int rigth = layoutManager.getDecoratedRight(view) + params.rightMargin;
                int start = layoutManager.getPaddingLeft();
                int end = layoutManager.getWidth() - layoutManager.getPaddingRight();
                if (left > start && rigth < end) {
                    return 0;
                }
                int viewSize = rigth - left;
                start = (end - start) - viewSize;
                end = start + viewSize;
                int dtStart = start - left;
                if (dtStart > 0) {
                    return dtStart;
                }
                int dtEnd = end - rigth;
                if (dtEnd < 0) {
                    return dtEnd;
                }
                return 0;
            }
        }
        return 0;
    }

    public PointF computeScrollVectorForPosition(int targetPosition) {
        LayoutManager layoutManager = getLayoutManager();
        if (layoutManager instanceof ScrollVectorProvider) {
            return ((ScrollVectorProvider) layoutManager).computeScrollVectorForPosition(targetPosition);
        }
        return null;
    }
}
