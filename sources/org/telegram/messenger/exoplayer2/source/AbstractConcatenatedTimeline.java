package org.telegram.messenger.exoplayer2.source;

import android.util.Pair;
import org.telegram.messenger.exoplayer2.Timeline;
import org.telegram.messenger.exoplayer2.Timeline.Period;
import org.telegram.messenger.exoplayer2.Timeline.Window;

abstract class AbstractConcatenatedTimeline extends Timeline {
    private final int childCount;
    private final ShuffleOrder shuffleOrder;

    protected abstract int getChildIndexByChildUid(Object obj);

    protected abstract int getChildIndexByPeriodIndex(int i);

    protected abstract int getChildIndexByWindowIndex(int i);

    protected abstract Object getChildUidByChildIndex(int i);

    protected abstract int getFirstPeriodIndexByChildIndex(int i);

    protected abstract int getFirstWindowIndexByChildIndex(int i);

    protected abstract Timeline getTimelineByChildIndex(int i);

    public AbstractConcatenatedTimeline(ShuffleOrder shuffleOrder) {
        this.shuffleOrder = shuffleOrder;
        this.childCount = shuffleOrder.getLength();
    }

    public int getNextWindowIndex(int windowIndex, int repeatMode, boolean shuffleModeEnabled) {
        int childIndex = getChildIndexByWindowIndex(windowIndex);
        int firstWindowIndexInChild = getFirstWindowIndexByChildIndex(childIndex);
        int nextWindowIndexInChild = getTimelineByChildIndex(childIndex).getNextWindowIndex(windowIndex - firstWindowIndexInChild, repeatMode == 2 ? 0 : repeatMode, shuffleModeEnabled);
        if (nextWindowIndexInChild != -1) {
            return firstWindowIndexInChild + nextWindowIndexInChild;
        }
        int nextChildIndex = getNextChildIndex(childIndex, shuffleModeEnabled);
        while (nextChildIndex != -1 && getTimelineByChildIndex(nextChildIndex).isEmpty()) {
            nextChildIndex = getNextChildIndex(nextChildIndex, shuffleModeEnabled);
        }
        if (nextChildIndex != -1) {
            return getFirstWindowIndexByChildIndex(nextChildIndex) + getTimelineByChildIndex(nextChildIndex).getFirstWindowIndex(shuffleModeEnabled);
        }
        if (repeatMode == 2) {
            return getFirstWindowIndex(shuffleModeEnabled);
        }
        return -1;
    }

    public int getPreviousWindowIndex(int windowIndex, int repeatMode, boolean shuffleModeEnabled) {
        int childIndex = getChildIndexByWindowIndex(windowIndex);
        int firstWindowIndexInChild = getFirstWindowIndexByChildIndex(childIndex);
        int previousWindowIndexInChild = getTimelineByChildIndex(childIndex).getPreviousWindowIndex(windowIndex - firstWindowIndexInChild, repeatMode == 2 ? 0 : repeatMode, shuffleModeEnabled);
        if (previousWindowIndexInChild != -1) {
            return firstWindowIndexInChild + previousWindowIndexInChild;
        }
        int previousChildIndex = getPreviousChildIndex(childIndex, shuffleModeEnabled);
        while (previousChildIndex != -1 && getTimelineByChildIndex(previousChildIndex).isEmpty()) {
            previousChildIndex = getPreviousChildIndex(previousChildIndex, shuffleModeEnabled);
        }
        if (previousChildIndex != -1) {
            return getFirstWindowIndexByChildIndex(previousChildIndex) + getTimelineByChildIndex(previousChildIndex).getLastWindowIndex(shuffleModeEnabled);
        }
        if (repeatMode == 2) {
            return getLastWindowIndex(shuffleModeEnabled);
        }
        return -1;
    }

    public int getLastWindowIndex(boolean shuffleModeEnabled) {
        if (this.childCount == 0) {
            return -1;
        }
        int lastChildIndex = shuffleModeEnabled ? this.shuffleOrder.getLastIndex() : this.childCount - 1;
        while (getTimelineByChildIndex(lastChildIndex).isEmpty()) {
            lastChildIndex = getPreviousChildIndex(lastChildIndex, shuffleModeEnabled);
            if (lastChildIndex == -1) {
                return -1;
            }
        }
        return getFirstWindowIndexByChildIndex(lastChildIndex) + getTimelineByChildIndex(lastChildIndex).getLastWindowIndex(shuffleModeEnabled);
    }

    public int getFirstWindowIndex(boolean shuffleModeEnabled) {
        if (this.childCount == 0) {
            return -1;
        }
        int firstChildIndex = shuffleModeEnabled ? this.shuffleOrder.getFirstIndex() : 0;
        while (getTimelineByChildIndex(firstChildIndex).isEmpty()) {
            firstChildIndex = getNextChildIndex(firstChildIndex, shuffleModeEnabled);
            if (firstChildIndex == -1) {
                return -1;
            }
        }
        return getFirstWindowIndexByChildIndex(firstChildIndex) + getTimelineByChildIndex(firstChildIndex).getFirstWindowIndex(shuffleModeEnabled);
    }

    public final Window getWindow(int windowIndex, Window window, boolean setIds, long defaultPositionProjectionUs) {
        int childIndex = getChildIndexByWindowIndex(windowIndex);
        int firstWindowIndexInChild = getFirstWindowIndexByChildIndex(childIndex);
        int firstPeriodIndexInChild = getFirstPeriodIndexByChildIndex(childIndex);
        getTimelineByChildIndex(childIndex).getWindow(windowIndex - firstWindowIndexInChild, window, setIds, defaultPositionProjectionUs);
        window.firstPeriodIndex += firstPeriodIndexInChild;
        window.lastPeriodIndex += firstPeriodIndexInChild;
        return window;
    }

    public final Period getPeriod(int periodIndex, Period period, boolean setIds) {
        int childIndex = getChildIndexByPeriodIndex(periodIndex);
        int firstWindowIndexInChild = getFirstWindowIndexByChildIndex(childIndex);
        getTimelineByChildIndex(childIndex).getPeriod(periodIndex - getFirstPeriodIndexByChildIndex(childIndex), period, setIds);
        period.windowIndex += firstWindowIndexInChild;
        if (setIds) {
            period.uid = Pair.create(getChildUidByChildIndex(childIndex), period.uid);
        }
        return period;
    }

    public final int getIndexOfPeriod(Object uid) {
        int i = -1;
        if (!(uid instanceof Pair)) {
            return -1;
        }
        Pair<?, ?> childUidAndPeriodUid = (Pair) uid;
        Object childUid = childUidAndPeriodUid.first;
        Object periodUid = childUidAndPeriodUid.second;
        int childIndex = getChildIndexByChildUid(childUid);
        if (childIndex == -1) {
            return -1;
        }
        int periodIndexInChild = getTimelineByChildIndex(childIndex).getIndexOfPeriod(periodUid);
        if (periodIndexInChild != -1) {
            i = getFirstPeriodIndexByChildIndex(childIndex) + periodIndexInChild;
        }
        return i;
    }

    private int getNextChildIndex(int childIndex, boolean shuffleModeEnabled) {
        if (shuffleModeEnabled) {
            return this.shuffleOrder.getNextIndex(childIndex);
        }
        return childIndex < this.childCount + -1 ? childIndex + 1 : -1;
    }

    private int getPreviousChildIndex(int childIndex, boolean shuffleModeEnabled) {
        if (shuffleModeEnabled) {
            return this.shuffleOrder.getPreviousIndex(childIndex);
        }
        return childIndex > 0 ? childIndex - 1 : -1;
    }
}
