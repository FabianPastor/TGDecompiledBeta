package org.telegram.messenger.support;

public class LongSparseIntArray implements Cloneable {
    private long[] mKeys;
    private int mSize;
    private int[] mValues;

    public LongSparseIntArray() {
        this(10);
    }

    public LongSparseIntArray(int initialCapacity) {
        int initialCapacity2 = ArrayUtils.idealLongArraySize(initialCapacity);
        this.mKeys = new long[initialCapacity2];
        this.mValues = new int[initialCapacity2];
        this.mSize = 0;
    }

    public LongSparseIntArray clone() {
        LongSparseIntArray clone = null;
        try {
            clone = (LongSparseIntArray) super.clone();
            clone.mKeys = (long[]) this.mKeys.clone();
            clone.mValues = (int[]) this.mValues.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            return clone;
        }
    }

    public int get(long key) {
        return get(key, 0);
    }

    public int get(long key, int valueIfKeyNotFound) {
        int i = binarySearch(this.mKeys, 0, this.mSize, key);
        if (i < 0) {
            return valueIfKeyNotFound;
        }
        return this.mValues[i];
    }

    public void delete(long key) {
        int i = binarySearch(this.mKeys, 0, this.mSize, key);
        if (i >= 0) {
            removeAt(i);
        }
    }

    public void removeAt(int index) {
        long[] jArr = this.mKeys;
        System.arraycopy(jArr, index + 1, jArr, index, this.mSize - (index + 1));
        int[] iArr = this.mValues;
        System.arraycopy(iArr, index + 1, iArr, index, this.mSize - (index + 1));
        this.mSize--;
    }

    public void put(long key, int value) {
        int i = binarySearch(this.mKeys, 0, this.mSize, key);
        if (i >= 0) {
            this.mValues[i] = value;
            return;
        }
        int i2 = i ^ -1;
        int i3 = this.mSize;
        if (i3 >= this.mKeys.length) {
            growKeyAndValueArrays(i3 + 1);
        }
        int i4 = this.mSize;
        if (i4 - i2 != 0) {
            long[] jArr = this.mKeys;
            System.arraycopy(jArr, i2, jArr, i2 + 1, i4 - i2);
            int[] iArr = this.mValues;
            System.arraycopy(iArr, i2, iArr, i2 + 1, this.mSize - i2);
        }
        this.mKeys[i2] = key;
        this.mValues[i2] = value;
        this.mSize++;
    }

    public int size() {
        return this.mSize;
    }

    public long keyAt(int index) {
        return this.mKeys[index];
    }

    public int valueAt(int index) {
        return this.mValues[index];
    }

    public int indexOfKey(long key) {
        return binarySearch(this.mKeys, 0, this.mSize, key);
    }

    public int indexOfValue(long value) {
        for (int i = 0; i < this.mSize; i++) {
            if (((long) this.mValues[i]) == value) {
                return i;
            }
        }
        return -1;
    }

    public void clear() {
        this.mSize = 0;
    }

    public void append(long key, int value) {
        int i = this.mSize;
        if (i == 0 || key > this.mKeys[i - 1]) {
            int pos = this.mSize;
            if (pos >= this.mKeys.length) {
                growKeyAndValueArrays(pos + 1);
            }
            this.mKeys[pos] = key;
            this.mValues[pos] = value;
            this.mSize = pos + 1;
            return;
        }
        put(key, value);
    }

    private void growKeyAndValueArrays(int minNeededSize) {
        int n = ArrayUtils.idealLongArraySize(minNeededSize);
        long[] nkeys = new long[n];
        int[] nvalues = new int[n];
        long[] jArr = this.mKeys;
        System.arraycopy(jArr, 0, nkeys, 0, jArr.length);
        int[] iArr = this.mValues;
        System.arraycopy(iArr, 0, nvalues, 0, iArr.length);
        this.mKeys = nkeys;
        this.mValues = nvalues;
    }

    private static int binarySearch(long[] a, int start, int len, long key) {
        int high = start + len;
        int low = start - 1;
        while (high - low > 1) {
            int guess = (high + low) / 2;
            if (a[guess] < key) {
                low = guess;
            } else {
                high = guess;
            }
        }
        if (high == start + len) {
            return (start + len) ^ -1;
        }
        if (a[high] == key) {
            return high;
        }
        return high ^ -1;
    }
}
