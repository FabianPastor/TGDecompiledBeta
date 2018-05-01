package org.telegram.messenger.support.widget;

import android.support.v4.util.Pools.Pool;
import android.support.v4.util.Pools.SimplePool;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;

class AdapterHelper implements Callback {
    private static final boolean DEBUG = false;
    static final int POSITION_TYPE_INVISIBLE = 0;
    static final int POSITION_TYPE_NEW_OR_LAID_OUT = 1;
    private static final String TAG = "AHT";
    final Callback mCallback;
    final boolean mDisableRecycler;
    private int mExistingUpdateTypes;
    Runnable mOnItemProcessedCallback;
    final OpReorderer mOpReorderer;
    final ArrayList<UpdateOp> mPendingUpdates;
    final ArrayList<UpdateOp> mPostponedList;
    private Pool<UpdateOp> mUpdateOpPool;

    interface Callback {
        ViewHolder findViewHolder(int i);

        void markViewHoldersUpdated(int i, int i2, Object obj);

        void offsetPositionsForAdd(int i, int i2);

        void offsetPositionsForMove(int i, int i2);

        void offsetPositionsForRemovingInvisible(int i, int i2);

        void offsetPositionsForRemovingLaidOutOrNewView(int i, int i2);

        void onDispatchFirstPass(UpdateOp updateOp);

        void onDispatchSecondPass(UpdateOp updateOp);
    }

    static class UpdateOp {
        static final int ADD = 1;
        static final int MOVE = 8;
        static final int POOL_SIZE = 30;
        static final int REMOVE = 2;
        static final int UPDATE = 4;
        int cmd;
        int itemCount;
        Object payload;
        int positionStart;

        UpdateOp(int i, int i2, int i3, Object obj) {
            this.cmd = i;
            this.positionStart = i2;
            this.itemCount = i3;
            this.payload = obj;
        }

        String cmdToString() {
            int i = this.cmd;
            if (i == 4) {
                return "up";
            }
            if (i == 8) {
                return "mv";
            }
            switch (i) {
                case 1:
                    return "add";
                case 2:
                    return "rm";
                default:
                    return "??";
            }
        }

        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
            stringBuilder.append("[");
            stringBuilder.append(cmdToString());
            stringBuilder.append(",s:");
            stringBuilder.append(this.positionStart);
            stringBuilder.append("c:");
            stringBuilder.append(this.itemCount);
            stringBuilder.append(",p:");
            stringBuilder.append(this.payload);
            stringBuilder.append("]");
            return stringBuilder.toString();
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj != null) {
                if (getClass() == obj.getClass()) {
                    UpdateOp updateOp = (UpdateOp) obj;
                    if (this.cmd != updateOp.cmd) {
                        return false;
                    }
                    if (this.cmd == 8 && Math.abs(this.itemCount - this.positionStart) == 1 && this.itemCount == updateOp.positionStart && this.positionStart == updateOp.itemCount) {
                        return true;
                    }
                    if (this.itemCount != updateOp.itemCount || this.positionStart != updateOp.positionStart) {
                        return false;
                    }
                    if (this.payload != null) {
                        if (this.payload.equals(updateOp.payload) == null) {
                            return false;
                        }
                    } else if (updateOp.payload != null) {
                        return false;
                    }
                    return true;
                }
            }
            return false;
        }

        public int hashCode() {
            return (31 * ((this.cmd * 31) + this.positionStart)) + this.itemCount;
        }
    }

    AdapterHelper(Callback callback) {
        this(callback, false);
    }

    AdapterHelper(Callback callback, boolean z) {
        this.mUpdateOpPool = new SimplePool(30);
        this.mPendingUpdates = new ArrayList();
        this.mPostponedList = new ArrayList();
        this.mExistingUpdateTypes = 0;
        this.mCallback = callback;
        this.mDisableRecycler = z;
        this.mOpReorderer = new OpReorderer(this);
    }

    AdapterHelper addUpdateOp(UpdateOp... updateOpArr) {
        Collections.addAll(this.mPendingUpdates, updateOpArr);
        return this;
    }

    void reset() {
        recycleUpdateOpsAndClearList(this.mPendingUpdates);
        recycleUpdateOpsAndClearList(this.mPostponedList);
        this.mExistingUpdateTypes = 0;
    }

    void preProcess() {
        this.mOpReorderer.reorderOps(this.mPendingUpdates);
        int size = this.mPendingUpdates.size();
        for (int i = 0; i < size; i++) {
            UpdateOp updateOp = (UpdateOp) this.mPendingUpdates.get(i);
            int i2 = updateOp.cmd;
            if (i2 == 4) {
                applyUpdate(updateOp);
            } else if (i2 != 8) {
                switch (i2) {
                    case 1:
                        applyAdd(updateOp);
                        break;
                    case 2:
                        applyRemove(updateOp);
                        break;
                    default:
                        break;
                }
            } else {
                applyMove(updateOp);
            }
            if (this.mOnItemProcessedCallback != null) {
                this.mOnItemProcessedCallback.run();
            }
        }
        this.mPendingUpdates.clear();
    }

    void consumePostponedUpdates() {
        int size = this.mPostponedList.size();
        for (int i = 0; i < size; i++) {
            this.mCallback.onDispatchSecondPass((UpdateOp) this.mPostponedList.get(i));
        }
        recycleUpdateOpsAndClearList(this.mPostponedList);
        this.mExistingUpdateTypes = 0;
    }

    private void applyMove(UpdateOp updateOp) {
        postponeAndUpdateViewHolders(updateOp);
    }

    private void applyRemove(UpdateOp updateOp) {
        int i = updateOp.positionStart;
        int i2 = updateOp.positionStart + updateOp.itemCount;
        int i3 = updateOp.positionStart;
        int i4 = -1;
        int i5 = 0;
        while (i3 < i2) {
            int i6;
            if (this.mCallback.findViewHolder(i3) == null) {
                if (!canFindInPreLayout(i3)) {
                    if (i4 == 1) {
                        postponeAndUpdateViewHolders(obtainUpdateOp(2, i, i5, null));
                        i4 = 1;
                    } else {
                        i4 = 0;
                    }
                    i6 = 0;
                    if (i4 == 0) {
                        i3 -= i5;
                        i2 -= i5;
                        i5 = 1;
                    } else {
                        i5++;
                    }
                    i3++;
                    i4 = i6;
                }
            }
            if (i4 == 0) {
                dispatchAndUpdateViewHolders(obtainUpdateOp(2, i, i5, null));
                i4 = 1;
            } else {
                i4 = 0;
            }
            i6 = 1;
            if (i4 == 0) {
                i5++;
            } else {
                i3 -= i5;
                i2 -= i5;
                i5 = 1;
            }
            i3++;
            i4 = i6;
        }
        if (i5 != updateOp.itemCount) {
            recycleUpdateOp(updateOp);
            updateOp = obtainUpdateOp(2, i, i5, null);
        }
        if (i4 == 0) {
            dispatchAndUpdateViewHolders(updateOp);
        } else {
            postponeAndUpdateViewHolders(updateOp);
        }
    }

    private void applyUpdate(UpdateOp updateOp) {
        int i = updateOp.positionStart;
        int i2 = updateOp.positionStart + updateOp.itemCount;
        int i3 = -1;
        int i4 = i;
        i = 0;
        for (int i5 = updateOp.positionStart; i5 < i2; i5++) {
            if (this.mCallback.findViewHolder(i5) == null) {
                if (!canFindInPreLayout(i5)) {
                    if (i3 == 1) {
                        postponeAndUpdateViewHolders(obtainUpdateOp(4, i4, i, updateOp.payload));
                        i4 = i5;
                        i = 0;
                    }
                    i3 = 0;
                    i++;
                }
            }
            if (i3 == 0) {
                dispatchAndUpdateViewHolders(obtainUpdateOp(4, i4, i, updateOp.payload));
                i4 = i5;
                i = 0;
            }
            i3 = 1;
            i++;
        }
        if (i != updateOp.itemCount) {
            Object obj = updateOp.payload;
            recycleUpdateOp(updateOp);
            updateOp = obtainUpdateOp(4, i4, i, obj);
        }
        if (i3 == 0) {
            dispatchAndUpdateViewHolders(updateOp);
        } else {
            postponeAndUpdateViewHolders(updateOp);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void dispatchAndUpdateViewHolders(UpdateOp updateOp) {
        if (updateOp.cmd != 1) {
            if (updateOp.cmd != 8) {
                int updatePositionWithPostponed = updatePositionWithPostponed(updateOp.positionStart, updateOp.cmd);
                int i = updateOp.positionStart;
                int i2 = updateOp.cmd;
                if (i2 == 2) {
                    i2 = 0;
                } else if (i2 != 4) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("op should be remove or update.");
                    stringBuilder.append(updateOp);
                    throw new IllegalArgumentException(stringBuilder.toString());
                } else {
                    i2 = 1;
                }
                int i3 = updatePositionWithPostponed;
                updatePositionWithPostponed = 1;
                int i4 = i;
                i = updatePositionWithPostponed;
                while (updatePositionWithPostponed < updateOp.itemCount) {
                    Object obj;
                    UpdateOp obtainUpdateOp;
                    int updatePositionWithPostponed2 = updatePositionWithPostponed(updateOp.positionStart + (i2 * updatePositionWithPostponed), updateOp.cmd);
                    int i5 = updateOp.cmd;
                    if (i5 != 2) {
                        if (i5 == 4) {
                        }
                    } else if (updatePositionWithPostponed2 == i3) {
                        obj = 1;
                        if (obj == null) {
                            i++;
                        } else {
                            obtainUpdateOp = obtainUpdateOp(updateOp.cmd, i3, i, updateOp.payload);
                            dispatchFirstPassAndUpdateViewHolders(obtainUpdateOp, i4);
                            recycleUpdateOp(obtainUpdateOp);
                            if (updateOp.cmd == 4) {
                                i4 += i;
                            }
                            i = 1;
                            i3 = updatePositionWithPostponed2;
                        }
                        updatePositionWithPostponed++;
                    }
                    obj = null;
                    if (obj == null) {
                        obtainUpdateOp = obtainUpdateOp(updateOp.cmd, i3, i, updateOp.payload);
                        dispatchFirstPassAndUpdateViewHolders(obtainUpdateOp, i4);
                        recycleUpdateOp(obtainUpdateOp);
                        if (updateOp.cmd == 4) {
                            i4 += i;
                        }
                        i = 1;
                        i3 = updatePositionWithPostponed2;
                    } else {
                        i++;
                    }
                    updatePositionWithPostponed++;
                }
                Object obj2 = updateOp.payload;
                recycleUpdateOp(updateOp);
                if (i > 0) {
                    updateOp = obtainUpdateOp(updateOp.cmd, i3, i, obj2);
                    dispatchFirstPassAndUpdateViewHolders(updateOp, i4);
                    recycleUpdateOp(updateOp);
                    return;
                }
                return;
            }
        }
        throw new IllegalArgumentException("should not dispatch add or move for pre layout");
    }

    void dispatchFirstPassAndUpdateViewHolders(UpdateOp updateOp, int i) {
        this.mCallback.onDispatchFirstPass(updateOp);
        int i2 = updateOp.cmd;
        if (i2 == 2) {
            this.mCallback.offsetPositionsForRemovingInvisible(i, updateOp.itemCount);
        } else if (i2 != 4) {
            throw new IllegalArgumentException("only remove and update ops can be dispatched in first pass");
        } else {
            this.mCallback.markViewHoldersUpdated(i, updateOp.itemCount, updateOp.payload);
        }
    }

    private int updatePositionWithPostponed(int i, int i2) {
        for (int size = this.mPostponedList.size() - 1; size >= 0; size--) {
            UpdateOp updateOp = (UpdateOp) this.mPostponedList.get(size);
            if (updateOp.cmd == 8) {
                int i3;
                int i4;
                if (updateOp.positionStart < updateOp.itemCount) {
                    i3 = updateOp.positionStart;
                    i4 = updateOp.itemCount;
                } else {
                    i3 = updateOp.itemCount;
                    i4 = updateOp.positionStart;
                }
                if (i < i3 || i > r4) {
                    if (i < updateOp.positionStart) {
                        if (i2 == 1) {
                            updateOp.positionStart++;
                            updateOp.itemCount++;
                        } else if (i2 == 2) {
                            updateOp.positionStart--;
                            updateOp.itemCount--;
                        }
                    }
                } else if (i3 == updateOp.positionStart) {
                    if (i2 == 1) {
                        updateOp.itemCount++;
                    } else if (i2 == 2) {
                        updateOp.itemCount--;
                    }
                    i++;
                } else {
                    if (i2 == 1) {
                        updateOp.positionStart++;
                    } else if (i2 == 2) {
                        updateOp.positionStart--;
                    }
                    i--;
                }
            } else if (updateOp.positionStart <= i) {
                if (updateOp.cmd == 1) {
                    i -= updateOp.itemCount;
                } else if (updateOp.cmd == 2) {
                    i += updateOp.itemCount;
                }
            } else if (i2 == 1) {
                updateOp.positionStart++;
            } else if (i2 == 2) {
                updateOp.positionStart--;
            }
        }
        for (i2 = this.mPostponedList.size() - 1; i2 >= 0; i2--) {
            UpdateOp updateOp2 = (UpdateOp) this.mPostponedList.get(i2);
            if (updateOp2.cmd == 8) {
                if (updateOp2.itemCount == updateOp2.positionStart || updateOp2.itemCount < 0) {
                    this.mPostponedList.remove(i2);
                    recycleUpdateOp(updateOp2);
                }
            } else if (updateOp2.itemCount <= 0) {
                this.mPostponedList.remove(i2);
                recycleUpdateOp(updateOp2);
            }
        }
        return i;
    }

    private boolean canFindInPreLayout(int i) {
        int size = this.mPostponedList.size();
        for (int i2 = 0; i2 < size; i2++) {
            UpdateOp updateOp = (UpdateOp) this.mPostponedList.get(i2);
            if (updateOp.cmd == 8) {
                if (findPositionOffset(updateOp.itemCount, i2 + 1) == i) {
                    return true;
                }
            } else if (updateOp.cmd == 1) {
                int i3 = updateOp.positionStart + updateOp.itemCount;
                for (int i4 = updateOp.positionStart; i4 < i3; i4++) {
                    if (findPositionOffset(i4, i2 + 1) == i) {
                        return true;
                    }
                }
                continue;
            } else {
                continue;
            }
        }
        return false;
    }

    private void applyAdd(UpdateOp updateOp) {
        postponeAndUpdateViewHolders(updateOp);
    }

    private void postponeAndUpdateViewHolders(UpdateOp updateOp) {
        this.mPostponedList.add(updateOp);
        int i = updateOp.cmd;
        if (i == 4) {
            this.mCallback.markViewHoldersUpdated(updateOp.positionStart, updateOp.itemCount, updateOp.payload);
        } else if (i != 8) {
            switch (i) {
                case 1:
                    this.mCallback.offsetPositionsForAdd(updateOp.positionStart, updateOp.itemCount);
                    return;
                case 2:
                    this.mCallback.offsetPositionsForRemovingLaidOutOrNewView(updateOp.positionStart, updateOp.itemCount);
                    return;
                default:
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Unknown update op type for ");
                    stringBuilder.append(updateOp);
                    throw new IllegalArgumentException(stringBuilder.toString());
            }
        } else {
            this.mCallback.offsetPositionsForMove(updateOp.positionStart, updateOp.itemCount);
        }
    }

    boolean hasPendingUpdates() {
        return this.mPendingUpdates.size() > 0;
    }

    boolean hasAnyUpdateTypes(int i) {
        return (i & this.mExistingUpdateTypes) != 0;
    }

    int findPositionOffset(int i) {
        return findPositionOffset(i, 0);
    }

    int findPositionOffset(int i, int i2) {
        int size = this.mPostponedList.size();
        while (i2 < size) {
            UpdateOp updateOp = (UpdateOp) this.mPostponedList.get(i2);
            if (updateOp.cmd == 8) {
                if (updateOp.positionStart == i) {
                    i = updateOp.itemCount;
                } else {
                    if (updateOp.positionStart < i) {
                        i--;
                    }
                    if (updateOp.itemCount <= i) {
                        i++;
                    }
                }
            } else if (updateOp.positionStart > i) {
                continue;
            } else if (updateOp.cmd == 2) {
                if (i < updateOp.positionStart + updateOp.itemCount) {
                    return -1;
                }
                i -= updateOp.itemCount;
            } else if (updateOp.cmd == 1) {
                i += updateOp.itemCount;
            }
            i2++;
        }
        return i;
    }

    boolean onItemRangeChanged(int i, int i2, Object obj) {
        boolean z = false;
        if (i2 < 1) {
            return false;
        }
        this.mPendingUpdates.add(obtainUpdateOp(4, i, i2, obj));
        this.mExistingUpdateTypes |= 4;
        if (this.mPendingUpdates.size() == 1) {
            z = true;
        }
        return z;
    }

    boolean onItemRangeInserted(int i, int i2) {
        boolean z = false;
        if (i2 < 1) {
            return false;
        }
        this.mPendingUpdates.add(obtainUpdateOp(1, i, i2, null));
        this.mExistingUpdateTypes |= 1;
        if (this.mPendingUpdates.size() == 1) {
            z = true;
        }
        return z;
    }

    boolean onItemRangeRemoved(int i, int i2) {
        boolean z = false;
        if (i2 < 1) {
            return false;
        }
        this.mPendingUpdates.add(obtainUpdateOp(2, i, i2, null));
        this.mExistingUpdateTypes |= 2;
        if (this.mPendingUpdates.size() == 1) {
            z = true;
        }
        return z;
    }

    boolean onItemRangeMoved(int i, int i2, int i3) {
        boolean z = false;
        if (i == i2) {
            return false;
        }
        if (i3 != 1) {
            throw new IllegalArgumentException("Moving more than 1 item is not supported yet");
        }
        this.mPendingUpdates.add(obtainUpdateOp(8, i, i2, null));
        this.mExistingUpdateTypes |= 8;
        if (this.mPendingUpdates.size() == 1) {
            z = true;
        }
        return z;
    }

    void consumeUpdatesInOnePass() {
        consumePostponedUpdates();
        int size = this.mPendingUpdates.size();
        for (int i = 0; i < size; i++) {
            UpdateOp updateOp = (UpdateOp) this.mPendingUpdates.get(i);
            int i2 = updateOp.cmd;
            if (i2 == 4) {
                this.mCallback.onDispatchSecondPass(updateOp);
                this.mCallback.markViewHoldersUpdated(updateOp.positionStart, updateOp.itemCount, updateOp.payload);
            } else if (i2 != 8) {
                switch (i2) {
                    case 1:
                        this.mCallback.onDispatchSecondPass(updateOp);
                        this.mCallback.offsetPositionsForAdd(updateOp.positionStart, updateOp.itemCount);
                        break;
                    case 2:
                        this.mCallback.onDispatchSecondPass(updateOp);
                        this.mCallback.offsetPositionsForRemovingInvisible(updateOp.positionStart, updateOp.itemCount);
                        break;
                    default:
                        break;
                }
            } else {
                this.mCallback.onDispatchSecondPass(updateOp);
                this.mCallback.offsetPositionsForMove(updateOp.positionStart, updateOp.itemCount);
            }
            if (this.mOnItemProcessedCallback != null) {
                this.mOnItemProcessedCallback.run();
            }
        }
        recycleUpdateOpsAndClearList(this.mPendingUpdates);
        this.mExistingUpdateTypes = 0;
    }

    public int applyPendingUpdatesToPosition(int i) {
        int size = this.mPendingUpdates.size();
        for (int i2 = 0; i2 < size; i2++) {
            UpdateOp updateOp = (UpdateOp) this.mPendingUpdates.get(i2);
            int i3 = updateOp.cmd;
            if (i3 != 8) {
                switch (i3) {
                    case 1:
                        if (updateOp.positionStart > i) {
                            break;
                        }
                        i += updateOp.itemCount;
                        break;
                    case 2:
                        if (updateOp.positionStart <= i) {
                            if (updateOp.positionStart + updateOp.itemCount <= i) {
                                i -= updateOp.itemCount;
                                break;
                            }
                            return -1;
                        }
                        continue;
                    default:
                        break;
                }
            } else if (updateOp.positionStart == i) {
                i = updateOp.itemCount;
            } else {
                if (updateOp.positionStart < i) {
                    i--;
                }
                if (updateOp.itemCount <= i) {
                    i++;
                }
            }
        }
        return i;
    }

    boolean hasUpdates() {
        return (this.mPostponedList.isEmpty() || this.mPendingUpdates.isEmpty()) ? false : true;
    }

    public UpdateOp obtainUpdateOp(int i, int i2, int i3, Object obj) {
        UpdateOp updateOp = (UpdateOp) this.mUpdateOpPool.acquire();
        if (updateOp == null) {
            return new UpdateOp(i, i2, i3, obj);
        }
        updateOp.cmd = i;
        updateOp.positionStart = i2;
        updateOp.itemCount = i3;
        updateOp.payload = obj;
        return updateOp;
    }

    public void recycleUpdateOp(UpdateOp updateOp) {
        if (!this.mDisableRecycler) {
            updateOp.payload = null;
            this.mUpdateOpPool.release(updateOp);
        }
    }

    void recycleUpdateOpsAndClearList(List<UpdateOp> list) {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            recycleUpdateOp((UpdateOp) list.get(i));
        }
        list.clear();
    }
}
