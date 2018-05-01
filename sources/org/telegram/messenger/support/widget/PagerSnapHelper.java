package org.telegram.messenger.support.widget;

import android.util.DisplayMetrics;
import android.view.View;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.SmoothScroller.Action;
import org.telegram.messenger.support.widget.RecyclerView.SmoothScroller.ScrollVectorProvider;
import org.telegram.messenger.support.widget.RecyclerView.State;
import org.telegram.tgnet.ConnectionsManager;

public class PagerSnapHelper extends SnapHelper {
    private static final int MAX_SCROLL_ON_FLING_DURATION = 100;
    private OrientationHelper mHorizontalHelper;
    private OrientationHelper mVerticalHelper;

    public int[] calculateDistanceToFinalSnap(LayoutManager layoutManager, View view) {
        int[] iArr = new int[2];
        if (layoutManager.canScrollHorizontally()) {
            iArr[0] = distanceToCenter(layoutManager, view, getHorizontalHelper(layoutManager));
        } else {
            iArr[0] = 0;
        }
        if (layoutManager.canScrollVertically()) {
            iArr[1] = distanceToCenter(layoutManager, view, getVerticalHelper(layoutManager));
        } else {
            iArr[1] = 0;
        }
        return iArr;
    }

    public View findSnapView(LayoutManager layoutManager) {
        if (layoutManager.canScrollVertically()) {
            return findCenterView(layoutManager, getVerticalHelper(layoutManager));
        }
        return layoutManager.canScrollHorizontally() ? findCenterView(layoutManager, getHorizontalHelper(layoutManager)) : null;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int findTargetSnapPosition(LayoutManager layoutManager, int i, int i2) {
        int itemCount = layoutManager.getItemCount();
        if (itemCount == 0) {
            return -1;
        }
        View view = null;
        if (layoutManager.canScrollVertically()) {
            view = findStartView(layoutManager, getVerticalHelper(layoutManager));
        } else if (layoutManager.canScrollHorizontally()) {
            view = findStartView(layoutManager, getHorizontalHelper(layoutManager));
        }
        if (view == null) {
            return -1;
        }
        int position = layoutManager.getPosition(view);
        if (position == -1) {
            return -1;
        }
        int i3 = 0;
        if (!layoutManager.canScrollHorizontally()) {
            if (i2 > 0) {
            }
            i = 0;
            if ((layoutManager instanceof ScrollVectorProvider) != 0) {
                layoutManager = ((ScrollVectorProvider) layoutManager).computeScrollVectorForPosition(itemCount - 1);
                if (layoutManager != null && (layoutManager.x < 0 || layoutManager.y < null)) {
                    i3 = 1;
                }
            }
            if (i3 == 0) {
                if (i != 0) {
                    position--;
                }
            } else if (i != 0) {
                position++;
            }
            return position;
        }
        i = 1;
        if ((layoutManager instanceof ScrollVectorProvider) != 0) {
            layoutManager = ((ScrollVectorProvider) layoutManager).computeScrollVectorForPosition(itemCount - 1);
            i3 = 1;
        }
        if (i3 == 0) {
            if (i != 0) {
                position++;
            }
        } else if (i != 0) {
            position--;
        }
        return position;
    }

    protected LinearSmoothScroller createSnapScroller(LayoutManager layoutManager) {
        if ((layoutManager instanceof ScrollVectorProvider) == null) {
            return null;
        }
        return new LinearSmoothScroller(this.mRecyclerView.getContext()) {
            protected void onTargetFound(View view, State state, Action action) {
                view = PagerSnapHelper.this.calculateDistanceToFinalSnap(PagerSnapHelper.this.mRecyclerView.getLayoutManager(), view);
                state = view[null];
                view = view[1];
                int calculateTimeForDeceleration = calculateTimeForDeceleration(Math.max(Math.abs(state), Math.abs(view)));
                if (calculateTimeForDeceleration > 0) {
                    action.update(state, view, calculateTimeForDeceleration, this.mDecelerateInterpolator);
                }
            }

            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                return 100.0f / ((float) displayMetrics.densityDpi);
            }

            protected int calculateTimeForScrolling(int i) {
                return Math.min(PagerSnapHelper.MAX_SCROLL_ON_FLING_DURATION, super.calculateTimeForScrolling(i));
            }
        };
    }

    private int distanceToCenter(LayoutManager layoutManager, View view, OrientationHelper orientationHelper) {
        int decoratedStart = orientationHelper.getDecoratedStart(view) + (orientationHelper.getDecoratedMeasurement(view) / 2);
        if (layoutManager.getClipToPadding() != null) {
            layoutManager = orientationHelper.getStartAfterPadding() + (orientationHelper.getTotalSpace() / 2);
        } else {
            layoutManager = orientationHelper.getEnd() / 2;
        }
        return decoratedStart - layoutManager;
    }

    private View findCenterView(LayoutManager layoutManager, OrientationHelper orientationHelper) {
        int childCount = layoutManager.getChildCount();
        View view = null;
        if (childCount == 0) {
            return null;
        }
        int startAfterPadding;
        if (layoutManager.getClipToPadding()) {
            startAfterPadding = orientationHelper.getStartAfterPadding() + (orientationHelper.getTotalSpace() / 2);
        } else {
            startAfterPadding = orientationHelper.getEnd() / 2;
        }
        int i = ConnectionsManager.DEFAULT_DATACENTER_ID;
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = layoutManager.getChildAt(i2);
            int abs = Math.abs((orientationHelper.getDecoratedStart(childAt) + (orientationHelper.getDecoratedMeasurement(childAt) / 2)) - startAfterPadding);
            if (abs < i) {
                view = childAt;
                i = abs;
            }
        }
        return view;
    }

    private View findStartView(LayoutManager layoutManager, OrientationHelper orientationHelper) {
        int childCount = layoutManager.getChildCount();
        View view = null;
        if (childCount == 0) {
            return null;
        }
        int i = ConnectionsManager.DEFAULT_DATACENTER_ID;
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = layoutManager.getChildAt(i2);
            int decoratedStart = orientationHelper.getDecoratedStart(childAt);
            if (decoratedStart < i) {
                view = childAt;
                i = decoratedStart;
            }
        }
        return view;
    }

    private OrientationHelper getVerticalHelper(LayoutManager layoutManager) {
        if (this.mVerticalHelper == null || this.mVerticalHelper.mLayoutManager != layoutManager) {
            this.mVerticalHelper = OrientationHelper.createVerticalHelper(layoutManager);
        }
        return this.mVerticalHelper;
    }

    private OrientationHelper getHorizontalHelper(LayoutManager layoutManager) {
        if (this.mHorizontalHelper == null || this.mHorizontalHelper.mLayoutManager != layoutManager) {
            this.mHorizontalHelper = OrientationHelper.createHorizontalHelper(layoutManager);
        }
        return this.mHorizontalHelper;
    }
}
