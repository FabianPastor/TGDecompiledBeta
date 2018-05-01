package org.telegram.messenger.support.util;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

public class SortedList<T> {
    private static final int CAPACITY_GROWTH = 10;
    private static final int DELETION = 2;
    private static final int INSERTION = 1;
    public static final int INVALID_POSITION = -1;
    private static final int LOOKUP = 4;
    private static final int MIN_CAPACITY = 10;
    private BatchedCallback mBatchedCallback;
    private Callback mCallback;
    T[] mData;
    private int mNewDataStart;
    private T[] mOldData;
    private int mOldDataSize;
    private int mOldDataStart;
    private int mSize;
    private final Class<T> mTClass;

    public static abstract class Callback<T2> implements Comparator<T2>, ListUpdateCallback {
        public abstract boolean areContentsTheSame(T2 t2, T2 t22);

        public abstract boolean areItemsTheSame(T2 t2, T2 t22);

        public abstract int compare(T2 t2, T2 t22);

        public abstract void onChanged(int i, int i2);

        public void onChanged(int position, int count, Object payload) {
            onChanged(position, count);
        }

        public Object getChangePayload(T2 t2, T2 t22) {
            return null;
        }
    }

    public static class BatchedCallback<T2> extends Callback<T2> {
        private final BatchingListUpdateCallback mBatchingListUpdateCallback = new BatchingListUpdateCallback(this.mWrappedCallback);
        final Callback<T2> mWrappedCallback;

        public BatchedCallback(Callback<T2> wrappedCallback) {
            this.mWrappedCallback = wrappedCallback;
        }

        public int compare(T2 o1, T2 o2) {
            return this.mWrappedCallback.compare(o1, o2);
        }

        public void onInserted(int position, int count) {
            this.mBatchingListUpdateCallback.onInserted(position, count);
        }

        public void onRemoved(int position, int count) {
            this.mBatchingListUpdateCallback.onRemoved(position, count);
        }

        public void onMoved(int fromPosition, int toPosition) {
            this.mBatchingListUpdateCallback.onMoved(fromPosition, toPosition);
        }

        public void onChanged(int position, int count) {
            this.mBatchingListUpdateCallback.onChanged(position, count, null);
        }

        public void onChanged(int position, int count, Object payload) {
            this.mBatchingListUpdateCallback.onChanged(position, count, payload);
        }

        public boolean areContentsTheSame(T2 oldItem, T2 newItem) {
            return this.mWrappedCallback.areContentsTheSame(oldItem, newItem);
        }

        public boolean areItemsTheSame(T2 item1, T2 item2) {
            return this.mWrappedCallback.areItemsTheSame(item1, item2);
        }

        public Object getChangePayload(T2 item1, T2 item2) {
            return this.mWrappedCallback.getChangePayload(item1, item2);
        }

        public void dispatchLastEvent() {
            this.mBatchingListUpdateCallback.dispatchLastEvent();
        }
    }

    public SortedList(Class<T> klass, Callback<T> callback) {
        this(klass, callback, 10);
    }

    public SortedList(Class<T> klass, Callback<T> callback, int initialCapacity) {
        this.mTClass = klass;
        this.mData = (Object[]) Array.newInstance(klass, initialCapacity);
        this.mCallback = callback;
        this.mSize = 0;
    }

    public int size() {
        return this.mSize;
    }

    public int add(T item) {
        throwIfInMutationOperation();
        return add(item, true);
    }

    public void addAll(T[] items, boolean mayModifyInput) {
        throwIfInMutationOperation();
        if (items.length != 0) {
            if (mayModifyInput) {
                addAllInternal(items);
            } else {
                addAllInternal(copyArray(items));
            }
        }
    }

    public void addAll(T... items) {
        addAll(items, false);
    }

    public void addAll(Collection<T> items) {
        addAll(items.toArray((Object[]) ((Object[]) Array.newInstance(this.mTClass, items.size()))), true);
    }

    public void replaceAll(T[] items, boolean mayModifyInput) {
        throwIfInMutationOperation();
        if (mayModifyInput) {
            replaceAllInternal(items);
        } else {
            replaceAllInternal(copyArray(items));
        }
    }

    public void replaceAll(T... items) {
        replaceAll(items, false);
    }

    public void replaceAll(Collection<T> items) {
        replaceAll(items.toArray((Object[]) ((Object[]) Array.newInstance(this.mTClass, items.size()))), true);
    }

    private void addAllInternal(T[] newItems) {
        if (newItems.length >= 1) {
            int newSize = sortAndDedup(newItems);
            if (this.mSize == 0) {
                this.mData = newItems;
                this.mSize = newSize;
                this.mCallback.onInserted(0, newSize);
                return;
            }
            merge(newItems, newSize);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void replaceAllInternal(T[] newData) {
        boolean forceBatchedUpdates;
        if (this.mCallback instanceof BatchedCallback) {
            forceBatchedUpdates = false;
        } else {
            forceBatchedUpdates = true;
        }
        if (forceBatchedUpdates) {
            beginBatchedUpdates();
        }
        this.mOldDataStart = 0;
        this.mOldDataSize = this.mSize;
        this.mOldData = this.mData;
        this.mNewDataStart = 0;
        int newSize = sortAndDedup(newData);
        this.mData = (Object[]) Array.newInstance(this.mTClass, newSize);
        while (true) {
            if (this.mNewDataStart >= newSize && this.mOldDataStart >= this.mOldDataSize) {
                break;
            } else if (this.mOldDataStart >= this.mOldDataSize) {
                break;
            } else if (this.mNewDataStart >= newSize) {
                break;
            } else {
                T oldItem = this.mOldData[this.mOldDataStart];
                T newItem = newData[this.mNewDataStart];
                int result = this.mCallback.compare(oldItem, newItem);
                if (result < 0) {
                    replaceAllRemove();
                } else if (result > 0) {
                    replaceAllInsert(newItem);
                } else if (this.mCallback.areItemsTheSame(oldItem, newItem)) {
                    this.mData[this.mNewDataStart] = newItem;
                    this.mOldDataStart++;
                    this.mNewDataStart++;
                    if (!this.mCallback.areContentsTheSame(oldItem, newItem)) {
                        this.mCallback.onChanged(this.mNewDataStart - 1, 1, this.mCallback.getChangePayload(oldItem, newItem));
                    }
                } else {
                    replaceAllRemove();
                    replaceAllInsert(newItem);
                }
            }
        }
        int insertIndex = this.mNewDataStart;
        int itemCount = newSize - this.mNewDataStart;
        System.arraycopy(newData, insertIndex, this.mData, insertIndex, itemCount);
        this.mNewDataStart += itemCount;
        this.mSize += itemCount;
        this.mCallback.onInserted(insertIndex, itemCount);
        this.mOldData = null;
        if (forceBatchedUpdates) {
            endBatchedUpdates();
        }
    }

    private void replaceAllInsert(T newItem) {
        this.mData[this.mNewDataStart] = newItem;
        this.mNewDataStart++;
        this.mSize++;
        this.mCallback.onInserted(this.mNewDataStart - 1, 1);
    }

    private void replaceAllRemove() {
        this.mSize--;
        this.mOldDataStart++;
        this.mCallback.onRemoved(this.mNewDataStart, 1);
    }

    private int sortAndDedup(T[] items) {
        if (items.length == 0) {
            return 0;
        }
        Arrays.sort(items, this.mCallback);
        int rangeStart = 0;
        int rangeEnd = 1;
        for (int i = 1; i < items.length; i++) {
            T currentItem = items[i];
            if (this.mCallback.compare(items[rangeStart], currentItem) == 0) {
                int sameItemPos = findSameItem(currentItem, items, rangeStart, rangeEnd);
                if (sameItemPos != -1) {
                    items[sameItemPos] = currentItem;
                } else {
                    if (rangeEnd != i) {
                        items[rangeEnd] = currentItem;
                    }
                    rangeEnd++;
                }
            } else {
                if (rangeEnd != i) {
                    items[rangeEnd] = currentItem;
                }
                rangeStart = rangeEnd;
                rangeEnd++;
            }
        }
        return rangeEnd;
    }

    private int findSameItem(T item, T[] items, int from, int to) {
        for (int pos = from; pos < to; pos++) {
            if (this.mCallback.areItemsTheSame(items[pos], item)) {
                return pos;
            }
        }
        return -1;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void merge(T[] newData, int newDataSize) {
        boolean forceBatchedUpdates;
        if (this.mCallback instanceof BatchedCallback) {
            forceBatchedUpdates = false;
        } else {
            forceBatchedUpdates = true;
        }
        if (forceBatchedUpdates) {
            beginBatchedUpdates();
        }
        this.mOldData = this.mData;
        this.mOldDataStart = 0;
        this.mOldDataSize = this.mSize;
        this.mData = (Object[]) Array.newInstance(this.mTClass, (this.mSize + newDataSize) + 10);
        this.mNewDataStart = 0;
        int newDataStart = 0;
        while (true) {
            if (this.mOldDataStart >= this.mOldDataSize && newDataStart >= newDataSize) {
                break;
            } else if (this.mOldDataStart == this.mOldDataSize) {
                break;
            } else if (newDataStart == newDataSize) {
                break;
            } else {
                T oldItem = this.mOldData[this.mOldDataStart];
                T newItem = newData[newDataStart];
                int compare = this.mCallback.compare(oldItem, newItem);
                Object[] objArr;
                int i;
                if (compare > 0) {
                    objArr = this.mData;
                    i = this.mNewDataStart;
                    this.mNewDataStart = i + 1;
                    objArr[i] = newItem;
                    this.mSize++;
                    newDataStart++;
                    this.mCallback.onInserted(this.mNewDataStart - 1, 1);
                } else if (compare == 0 && this.mCallback.areItemsTheSame(oldItem, newItem)) {
                    objArr = this.mData;
                    i = this.mNewDataStart;
                    this.mNewDataStart = i + 1;
                    objArr[i] = newItem;
                    newDataStart++;
                    this.mOldDataStart++;
                    if (!this.mCallback.areContentsTheSame(oldItem, newItem)) {
                        this.mCallback.onChanged(this.mNewDataStart - 1, 1, this.mCallback.getChangePayload(oldItem, newItem));
                    }
                } else {
                    objArr = this.mData;
                    i = this.mNewDataStart;
                    this.mNewDataStart = i + 1;
                    objArr[i] = oldItem;
                    this.mOldDataStart++;
                }
            }
        }
        int itemCount = newDataSize - newDataStart;
        System.arraycopy(newData, newDataStart, this.mData, this.mNewDataStart, itemCount);
        this.mNewDataStart += itemCount;
        this.mSize += itemCount;
        this.mCallback.onInserted(this.mNewDataStart - itemCount, itemCount);
        this.mOldData = null;
        if (forceBatchedUpdates) {
            endBatchedUpdates();
        }
    }

    private void throwIfInMutationOperation() {
        if (this.mOldData != null) {
            throw new IllegalStateException("Data cannot be mutated in the middle of a batch update operation such as addAll or replaceAll.");
        }
    }

    public void beginBatchedUpdates() {
        throwIfInMutationOperation();
        if (!(this.mCallback instanceof BatchedCallback)) {
            if (this.mBatchedCallback == null) {
                this.mBatchedCallback = new BatchedCallback(this.mCallback);
            }
            this.mCallback = this.mBatchedCallback;
        }
    }

    public void endBatchedUpdates() {
        throwIfInMutationOperation();
        if (this.mCallback instanceof BatchedCallback) {
            ((BatchedCallback) this.mCallback).dispatchLastEvent();
        }
        if (this.mCallback == this.mBatchedCallback) {
            this.mCallback = this.mBatchedCallback.mWrappedCallback;
        }
    }

    private int add(T item, boolean notify) {
        int index = findIndexOf(item, this.mData, 0, this.mSize, 1);
        if (index == -1) {
            index = 0;
        } else if (index < this.mSize) {
            T existing = this.mData[index];
            if (this.mCallback.areItemsTheSame(existing, item)) {
                int i;
                if (this.mCallback.areContentsTheSame(existing, item)) {
                    this.mData[index] = item;
                    i = index;
                    return index;
                }
                this.mData[index] = item;
                this.mCallback.onChanged(index, 1, this.mCallback.getChangePayload(existing, item));
                i = index;
                return index;
            }
        }
        addToData(index, item);
        if (notify) {
            this.mCallback.onInserted(index, 1);
        }
        return index;
    }

    public boolean remove(T item) {
        throwIfInMutationOperation();
        return remove(item, true);
    }

    public T removeItemAt(int index) {
        throwIfInMutationOperation();
        T item = get(index);
        removeItemAtIndex(index, true);
        return item;
    }

    private boolean remove(T item, boolean notify) {
        int index = findIndexOf(item, this.mData, 0, this.mSize, 2);
        if (index == -1) {
            return false;
        }
        removeItemAtIndex(index, notify);
        return true;
    }

    private void removeItemAtIndex(int index, boolean notify) {
        System.arraycopy(this.mData, index + 1, this.mData, index, (this.mSize - index) - 1);
        this.mSize--;
        this.mData[this.mSize] = null;
        if (notify) {
            this.mCallback.onRemoved(index, 1);
        }
    }

    public void updateItemAt(int index, T item) {
        boolean contentsChanged;
        throwIfInMutationOperation();
        T existing = get(index);
        if (existing == item || !this.mCallback.areContentsTheSame(existing, item)) {
            contentsChanged = true;
        } else {
            contentsChanged = false;
        }
        if (existing == item || this.mCallback.compare(existing, item) != 0) {
            if (contentsChanged) {
                this.mCallback.onChanged(index, 1, this.mCallback.getChangePayload(existing, item));
            }
            removeItemAtIndex(index, false);
            int newIndex = add(item, false);
            if (index != newIndex) {
                this.mCallback.onMoved(index, newIndex);
                return;
            }
            return;
        }
        this.mData[index] = item;
        if (contentsChanged) {
            this.mCallback.onChanged(index, 1, this.mCallback.getChangePayload(existing, item));
        }
    }

    public void recalculatePositionOfItemAt(int index) {
        throwIfInMutationOperation();
        T item = get(index);
        removeItemAtIndex(index, false);
        int newIndex = add(item, false);
        if (index != newIndex) {
            this.mCallback.onMoved(index, newIndex);
        }
    }

    public T get(int index) throws IndexOutOfBoundsException {
        if (index >= this.mSize || index < 0) {
            throw new IndexOutOfBoundsException("Asked to get item at " + index + " but size is " + this.mSize);
        } else if (this.mOldData == null || index < this.mNewDataStart) {
            return this.mData[index];
        } else {
            return this.mOldData[(index - this.mNewDataStart) + this.mOldDataStart];
        }
    }

    public int indexOf(T item) {
        if (this.mOldData != null) {
            int index = findIndexOf(item, this.mData, 0, this.mNewDataStart, 4);
            if (index != -1) {
                return index;
            }
            index = findIndexOf(item, this.mOldData, this.mOldDataStart, this.mOldDataSize, 4);
            return index != -1 ? (index - this.mOldDataStart) + this.mNewDataStart : -1;
        } else {
            return findIndexOf(item, this.mData, 0, this.mSize, 4);
        }
    }

    private int findIndexOf(T item, T[] mData, int left, int right, int reason) {
        while (left < right) {
            int middle = (left + right) / 2;
            T myItem = mData[middle];
            int cmp = this.mCallback.compare(myItem, item);
            if (cmp < 0) {
                left = middle + 1;
            } else if (cmp != 0) {
                right = middle;
            } else if (this.mCallback.areItemsTheSame(myItem, item)) {
                return middle;
            } else {
                int exact = linearEqualitySearch(item, middle, left, right);
                if (reason != 1) {
                    return exact;
                }
                if (exact != -1) {
                    return exact;
                }
                return middle;
            }
        }
        if (reason != 1) {
            left = -1;
        }
        return left;
    }

    private int linearEqualitySearch(T item, int middle, int left, int right) {
        int i;
        int next = middle - 1;
        while (next >= left) {
            T nextItem = this.mData[next];
            if (this.mCallback.compare(nextItem, item) != 0) {
                break;
            } else if (this.mCallback.areItemsTheSame(nextItem, item)) {
                i = next;
                return next;
            } else {
                next--;
            }
        }
        next = middle + 1;
        while (next < right) {
            nextItem = this.mData[next];
            if (this.mCallback.compare(nextItem, item) != 0) {
                break;
            } else if (this.mCallback.areItemsTheSame(nextItem, item)) {
                i = next;
                return next;
            } else {
                next++;
            }
        }
        i = next;
        return -1;
    }

    private void addToData(int index, T item) {
        if (index > this.mSize) {
            throw new IndexOutOfBoundsException("cannot add item to " + index + " because size is " + this.mSize);
        }
        if (this.mSize == this.mData.length) {
            Object[] newData = (Object[]) ((Object[]) Array.newInstance(this.mTClass, this.mData.length + 10));
            System.arraycopy(this.mData, 0, newData, 0, index);
            newData[index] = item;
            System.arraycopy(this.mData, index, newData, index + 1, this.mSize - index);
            this.mData = newData;
        } else {
            System.arraycopy(this.mData, index, this.mData, index + 1, this.mSize - index);
            this.mData[index] = item;
        }
        this.mSize++;
    }

    private T[] copyArray(T[] items) {
        Object[] copy = (Object[]) ((Object[]) Array.newInstance(this.mTClass, items.length));
        System.arraycopy(items, 0, copy, 0, items.length);
        return copy;
    }

    public void clear() {
        throwIfInMutationOperation();
        if (this.mSize != 0) {
            int prevSize = this.mSize;
            Arrays.fill(this.mData, 0, prevSize, null);
            this.mSize = 0;
            this.mCallback.onRemoved(0, prevSize);
        }
    }
}
