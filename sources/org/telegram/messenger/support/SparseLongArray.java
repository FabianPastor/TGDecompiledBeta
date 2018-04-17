package org.telegram.messenger.support;

public class SparseLongArray implements Cloneable {
    private int[] mKeys;
    private int mSize;
    private long[] mValues;

    public SparseLongArray() {
        this(10);
    }

    public SparseLongArray(int initialCapacity) {
        initialCapacity = ArrayUtils.idealLongArraySize(initialCapacity);
        this.mKeys = new int[initialCapacity];
        this.mValues = new long[initialCapacity];
        this.mSize = 0;
    }

    public SparseLongArray clone() {
        SparseLongArray clone = null;
        try {
            clone = (SparseLongArray) super.clone();
            clone.mKeys = (int[]) this.mKeys.clone();
            clone.mValues = (long[]) this.mValues.clone();
        } catch (CloneNotSupportedException e) {
        }
        return clone;
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
        System.arraycopy(this.mKeys, index + 1, this.mKeys, index, this.mSize - (index + 1));
        System.arraycopy(this.mValues, index + 1, this.mValues, index, this.mSize - (index + 1));
        this.mSize--;
    }

    public void put(int key, long value) {
        int i = binarySearch(this.mKeys, 0, this.mSize, (long) key);
        if (i >= 0) {
            this.mValues[i] = value;
            return;
        }
        i ^= -1;
        if (this.mSize >= this.mKeys.length) {
            growKeyAndValueArrays(this.mSize + 1);
        }
        if (this.mSize - i != 0) {
            System.arraycopy(this.mKeys, i, this.mKeys, i + 1, this.mSize - i);
            System.arraycopy(this.mValues, i, this.mValues, i + 1, this.mSize - i);
        }
        this.mKeys[i] = key;
        this.mValues[i] = value;
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
        if (this.mSize == 0 || key > this.mKeys[this.mSize - 1]) {
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
        System.arraycopy(this.mKeys, 0, nkeys, 0, this.mKeys.length);
        System.arraycopy(this.mValues, 0, nvalues, 0, this.mValues.length);
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
