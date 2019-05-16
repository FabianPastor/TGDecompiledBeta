package org.telegram.messenger.support;

public class SparseLongArray implements Cloneable {
    private int[] mKeys;
    private int mSize;
    private long[] mValues;

    public SparseLongArray() {
        this(10);
    }

    public SparseLongArray(int i) {
        i = ArrayUtils.idealLongArraySize(i);
        this.mKeys = new int[i];
        this.mValues = new long[i];
        this.mSize = 0;
    }

    public SparseLongArray clone() {
        try {
            SparseLongArray sparseLongArray = (SparseLongArray) super.clone();
            try {
                sparseLongArray.mKeys = (int[]) this.mKeys.clone();
                sparseLongArray.mValues = (long[]) this.mValues.clone();
                return sparseLongArray;
            } catch (CloneNotSupportedException unused) {
                return sparseLongArray;
            }
        } catch (CloneNotSupportedException unused2) {
            return null;
        }
    }

    public long get(int i) {
        return get(i, 0);
    }

    public long get(int i, long j) {
        i = binarySearch(this.mKeys, 0, this.mSize, (long) i);
        if (i < 0) {
            return j;
        }
        return this.mValues[i];
    }

    public void delete(int i) {
        i = binarySearch(this.mKeys, 0, this.mSize, (long) i);
        if (i >= 0) {
            removeAt(i);
        }
    }

    public void removeAt(int i) {
        int[] iArr = this.mKeys;
        int i2 = i + 1;
        System.arraycopy(iArr, i2, iArr, i, this.mSize - i2);
        long[] jArr = this.mValues;
        System.arraycopy(jArr, i2, jArr, i, this.mSize - i2);
        this.mSize--;
    }

    public void put(int i, long j) {
        int binarySearch = binarySearch(this.mKeys, 0, this.mSize, (long) i);
        if (binarySearch >= 0) {
            this.mValues[binarySearch] = j;
            return;
        }
        binarySearch ^= -1;
        int i2 = this.mSize;
        if (i2 >= this.mKeys.length) {
            growKeyAndValueArrays(i2 + 1);
        }
        i2 = this.mSize;
        if (i2 - binarySearch != 0) {
            int[] iArr = this.mKeys;
            int i3 = binarySearch + 1;
            System.arraycopy(iArr, binarySearch, iArr, i3, i2 - binarySearch);
            long[] jArr = this.mValues;
            System.arraycopy(jArr, binarySearch, jArr, i3, this.mSize - binarySearch);
        }
        this.mKeys[binarySearch] = i;
        this.mValues[binarySearch] = j;
        this.mSize++;
    }

    public int size() {
        return this.mSize;
    }

    public int keyAt(int i) {
        return this.mKeys[i];
    }

    public long valueAt(int i) {
        return this.mValues[i];
    }

    public int indexOfKey(int i) {
        return binarySearch(this.mKeys, 0, this.mSize, (long) i);
    }

    public int indexOfValue(long j) {
        for (int i = 0; i < this.mSize; i++) {
            if (this.mValues[i] == j) {
                return i;
            }
        }
        return -1;
    }

    public void clear() {
        this.mSize = 0;
    }

    public void append(int i, long j) {
        int i2 = this.mSize;
        if (i2 == 0 || i > this.mKeys[i2 - 1]) {
            i2 = this.mSize;
            if (i2 >= this.mKeys.length) {
                growKeyAndValueArrays(i2 + 1);
            }
            this.mKeys[i2] = i;
            this.mValues[i2] = j;
            this.mSize = i2 + 1;
            return;
        }
        put(i, j);
    }

    private void growKeyAndValueArrays(int i) {
        i = ArrayUtils.idealLongArraySize(i);
        int[] iArr = new int[i];
        long[] jArr = new long[i];
        int[] iArr2 = this.mKeys;
        System.arraycopy(iArr2, 0, iArr, 0, iArr2.length);
        long[] jArr2 = this.mValues;
        System.arraycopy(jArr2, 0, jArr, 0, jArr2.length);
        this.mKeys = iArr;
        this.mValues = jArr;
    }

    private static int binarySearch(int[] iArr, int i, int i2, long j) {
        i2 += i;
        int i3 = i - 1;
        i = i2;
        while (i - i3 > 1) {
            int i4 = (i + i3) / 2;
            if (((long) iArr[i4]) < j) {
                i3 = i4;
            } else {
                i = i4;
            }
        }
        if (i == i2) {
            return i2 ^ -1;
        }
        return ((long) iArr[i]) == j ? i : i ^ -1;
    }
}
