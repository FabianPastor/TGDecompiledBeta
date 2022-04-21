package org.telegram.messenger.support;

public class SparseLongArray implements Cloneable {
    private int[] mKeys;
    private int mSize;
    private long[] mValues;

    public SparseLongArray() {
        this(10);
    }

    public SparseLongArray(int initialCapacity) {
        int initialCapacity2 = ArrayUtils.idealLongArraySize(initialCapacity);
        this.mKeys = new int[initialCapacity2];
        this.mValues = new long[initialCapacity2];
        this.mSize = 0;
    }

    public SparseLongArray clone() {
        SparseLongArray clone = null;
        try {
            clone = (SparseLongArray) super.clone();
            clone.mKeys = (int[]) this.mKeys.clone();
            clone.mValues = (long[]) this.mValues.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            return clone;
        }
    }

    public long get(int key) {
        return get(key, 0);
    }

    public long get(int key, long valueIfKeyNotFound) {
        int i = binarySearch(this.mKeys, 0, this.mSize, (long) key);
        if (i < 0) {
            return valueIfKeyNotFound;
        }
        return this.mValues[i];
    }

    public void delete(int key) {
        int i = binarySearch(this.mKeys, 0, this.mSize, (long) key);
        if (i >= 0) {
            removeAt(i);
        }
    }

    public void removeAt(int index) {
        int[] iArr = this.mKeys;
        System.arraycopy(iArr, index + 1, iArr, index, this.mSize - (index + 1));
        long[] jArr = this.mValues;
        System.arraycopy(jArr, index + 1, jArr, index, this.mSize - (index + 1));
        this.mSize--;
    }

    public void put(int key, long value) {
        int i = binarySearch(this.mKeys, 0, this.mSize, (long) key);
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
            int[] iArr = this.mKeys;
            System.arraycopy(iArr, i2, iArr, i2 + 1, i4 - i2);
            long[] jArr = this.mValues;
            System.arraycopy(jArr, i2, jArr, i2 + 1, this.mSize - i2);
        }
        this.mKeys[i2] = key;
        this.mValues[i2] = value;
        this.mSize++;
    }

    public int size() {
        return this.mSize;
    }

    public int keyAt(int index) {
        return this.mKeys[index];
    }

    public long valueAt(int index) {
        return this.mValues[index];
    }

    public int indexOfKey(int key) {
        return binarySearch(this.mKeys, 0, this.mSize, (long) key);
    }

    public int indexOfValue(long value) {
        for (int i = 0; i < this.mSize; i++) {
            if (this.mValues[i] == value) {
                return i;
            }
        }
        return -1;
    }

    public void clear() {
        this.mSize = 0;
    }

    public void append(int key, long value) {
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
        int[] nkeys = new int[n];
        long[] nvalues = new long[n];
        int[] iArr = this.mKeys;
        System.arraycopy(iArr, 0, nkeys, 0, iArr.length);
        long[] jArr = this.mValues;
        System.arraycopy(jArr, 0, nvalues, 0, jArr.length);
        this.mKeys = nkeys;
        this.mValues = nvalues;
    }

    private static int binarySearch(int[] a, int start, int len, long key) {
        int high = start + len;
        int low = start - 1;
        while (high - low > 1) {
            int guess = (high + low) / 2;
            if (((long) a[guess]) < key) {
                low = guess;
            } else {
                high = guess;
            }
        }
        if (high == start + len) {
            return (start + len) ^ -1;
        }
        if (((long) a[high]) == key) {
            return high;
        }
        return high ^ -1;
    }
}
