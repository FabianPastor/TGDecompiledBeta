package org.telegram.messenger.support.widget;

import android.support.v4.util.ArrayMap;
import android.support.v4.util.LongSparseArray;
import android.support.v4.util.Pools.Pool;
import android.support.v4.util.Pools.SimplePool;
import org.telegram.messenger.support.widget.RecyclerView.ItemAnimator.ItemHolderInfo;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;

class ViewInfoStore {
    private static final boolean DEBUG = false;
    final ArrayMap<ViewHolder, InfoRecord> mLayoutHolderMap = new ArrayMap();
    final LongSparseArray<ViewHolder> mOldChangedHolders = new LongSparseArray();

    static class InfoRecord {
        static final int FLAG_APPEAR = 2;
        static final int FLAG_APPEAR_AND_DISAPPEAR = 3;
        static final int FLAG_APPEAR_PRE_AND_POST = 14;
        static final int FLAG_DISAPPEARED = 1;
        static final int FLAG_POST = 8;
        static final int FLAG_PRE = 4;
        static final int FLAG_PRE_AND_POST = 12;
        static Pool<InfoRecord> sPool = new SimplePool(20);
        int flags;
        ItemHolderInfo postInfo;
        ItemHolderInfo preInfo;

        private InfoRecord() {
        }

        static InfoRecord obtain() {
            InfoRecord infoRecord = (InfoRecord) sPool.acquire();
            return infoRecord == null ? new InfoRecord() : infoRecord;
        }

        static void recycle(InfoRecord infoRecord) {
            infoRecord.flags = 0;
            infoRecord.preInfo = null;
            infoRecord.postInfo = null;
            sPool.release(infoRecord);
        }

        static void drainCache() {
            while (sPool.acquire() != null) {
            }
        }
    }

    interface ProcessCallback {
        void processAppeared(ViewHolder viewHolder, ItemHolderInfo itemHolderInfo, ItemHolderInfo itemHolderInfo2);

        void processDisappeared(ViewHolder viewHolder, ItemHolderInfo itemHolderInfo, ItemHolderInfo itemHolderInfo2);

        void processPersistent(ViewHolder viewHolder, ItemHolderInfo itemHolderInfo, ItemHolderInfo itemHolderInfo2);

        void unused(ViewHolder viewHolder);
    }

    ViewInfoStore() {
    }

    void clear() {
        this.mLayoutHolderMap.clear();
        this.mOldChangedHolders.clear();
    }

    void addToPreLayout(ViewHolder viewHolder, ItemHolderInfo itemHolderInfo) {
        InfoRecord infoRecord = (InfoRecord) this.mLayoutHolderMap.get(viewHolder);
        if (infoRecord == null) {
            infoRecord = InfoRecord.obtain();
            this.mLayoutHolderMap.put(viewHolder, infoRecord);
        }
        infoRecord.preInfo = itemHolderInfo;
        infoRecord.flags |= 4;
    }

    boolean isDisappearing(ViewHolder viewHolder) {
        InfoRecord infoRecord = (InfoRecord) this.mLayoutHolderMap.get(viewHolder);
        if (infoRecord == null || (infoRecord.flags & 1) == null) {
            return false;
        }
        return true;
    }

    ItemHolderInfo popFromPreLayout(ViewHolder viewHolder) {
        return popFromLayoutStep(viewHolder, 4);
    }

    ItemHolderInfo popFromPostLayout(ViewHolder viewHolder) {
        return popFromLayoutStep(viewHolder, 8);
    }

    private ItemHolderInfo popFromLayoutStep(ViewHolder viewHolder, int i) {
        viewHolder = this.mLayoutHolderMap.indexOfKey(viewHolder);
        if (viewHolder < null) {
            return null;
        }
        InfoRecord infoRecord = (InfoRecord) this.mLayoutHolderMap.valueAt(viewHolder);
        if (infoRecord == null || (infoRecord.flags & i) == 0) {
            return null;
        }
        infoRecord.flags &= i ^ -1;
        if (i == 4) {
            i = infoRecord.preInfo;
        } else if (i == 8) {
            i = infoRecord.postInfo;
        } else {
            throw new IllegalArgumentException("Must provide flag PRE or POST");
        }
        if ((infoRecord.flags & 12) == 0) {
            this.mLayoutHolderMap.removeAt(viewHolder);
            InfoRecord.recycle(infoRecord);
        }
        return i;
    }

    void addToOldChangeHolders(long j, ViewHolder viewHolder) {
        this.mOldChangedHolders.put(j, viewHolder);
    }

    void addToAppearedInPreLayoutHolders(ViewHolder viewHolder, ItemHolderInfo itemHolderInfo) {
        InfoRecord infoRecord = (InfoRecord) this.mLayoutHolderMap.get(viewHolder);
        if (infoRecord == null) {
            infoRecord = InfoRecord.obtain();
            this.mLayoutHolderMap.put(viewHolder, infoRecord);
        }
        infoRecord.flags |= 2;
        infoRecord.preInfo = itemHolderInfo;
    }

    boolean isInPreLayout(ViewHolder viewHolder) {
        InfoRecord infoRecord = (InfoRecord) this.mLayoutHolderMap.get(viewHolder);
        return (infoRecord == null || (infoRecord.flags & 4) == null) ? null : true;
    }

    ViewHolder getFromOldChangeHolders(long j) {
        return (ViewHolder) this.mOldChangedHolders.get(j);
    }

    void addToPostLayout(ViewHolder viewHolder, ItemHolderInfo itemHolderInfo) {
        InfoRecord infoRecord = (InfoRecord) this.mLayoutHolderMap.get(viewHolder);
        if (infoRecord == null) {
            infoRecord = InfoRecord.obtain();
            this.mLayoutHolderMap.put(viewHolder, infoRecord);
        }
        infoRecord.postInfo = itemHolderInfo;
        infoRecord.flags |= 8;
    }

    void addToDisappearedInLayout(ViewHolder viewHolder) {
        InfoRecord infoRecord = (InfoRecord) this.mLayoutHolderMap.get(viewHolder);
        if (infoRecord == null) {
            infoRecord = InfoRecord.obtain();
            this.mLayoutHolderMap.put(viewHolder, infoRecord);
        }
        r0.flags |= 1;
    }

    void removeFromDisappearedInLayout(ViewHolder viewHolder) {
        InfoRecord infoRecord = (InfoRecord) this.mLayoutHolderMap.get(viewHolder);
        if (infoRecord != null) {
            infoRecord.flags &= -2;
        }
    }

    void process(ProcessCallback processCallback) {
        for (int size = this.mLayoutHolderMap.size() - 1; size >= 0; size--) {
            ViewHolder viewHolder = (ViewHolder) this.mLayoutHolderMap.keyAt(size);
            InfoRecord infoRecord = (InfoRecord) this.mLayoutHolderMap.removeAt(size);
            if ((infoRecord.flags & 3) == 3) {
                processCallback.unused(viewHolder);
            } else if ((infoRecord.flags & 1) != 0) {
                if (infoRecord.preInfo == null) {
                    processCallback.unused(viewHolder);
                } else {
                    processCallback.processDisappeared(viewHolder, infoRecord.preInfo, infoRecord.postInfo);
                }
            } else if ((infoRecord.flags & 14) == 14) {
                processCallback.processAppeared(viewHolder, infoRecord.preInfo, infoRecord.postInfo);
            } else if ((infoRecord.flags & 12) == 12) {
                processCallback.processPersistent(viewHolder, infoRecord.preInfo, infoRecord.postInfo);
            } else if ((infoRecord.flags & 4) != 0) {
                processCallback.processDisappeared(viewHolder, infoRecord.preInfo, null);
            } else if ((infoRecord.flags & 8) != 0) {
                processCallback.processAppeared(viewHolder, infoRecord.preInfo, infoRecord.postInfo);
            } else {
                int i = infoRecord.flags;
            }
            InfoRecord.recycle(infoRecord);
        }
    }

    void removeViewHolder(ViewHolder viewHolder) {
        for (int size = this.mOldChangedHolders.size() - 1; size >= 0; size--) {
            if (viewHolder == this.mOldChangedHolders.valueAt(size)) {
                this.mOldChangedHolders.removeAt(size);
                break;
            }
        }
        InfoRecord infoRecord = (InfoRecord) this.mLayoutHolderMap.remove(viewHolder);
        if (infoRecord != null) {
            InfoRecord.recycle(infoRecord);
        }
    }

    void onDetach() {
        InfoRecord.drainCache();
    }

    public void onViewDetached(ViewHolder viewHolder) {
        removeFromDisappearedInLayout(viewHolder);
    }
}
