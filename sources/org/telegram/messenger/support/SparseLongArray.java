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

    public org.telegram.messenger.support.SparseLongArray clone() {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r2 = this;
        r0 = 0;
        r1 = super.clone();	 Catch:{ CloneNotSupportedException -> 0x001c }
        r1 = (org.telegram.messenger.support.SparseLongArray) r1;	 Catch:{ CloneNotSupportedException -> 0x001c }
        r0 = r2.mKeys;	 Catch:{ CloneNotSupportedException -> 0x001d }
        r0 = r0.clone();	 Catch:{ CloneNotSupportedException -> 0x001d }
        r0 = (int[]) r0;	 Catch:{ CloneNotSupportedException -> 0x001d }
        r1.mKeys = r0;	 Catch:{ CloneNotSupportedException -> 0x001d }
        r0 = r2.mValues;	 Catch:{ CloneNotSupportedException -> 0x001d }
        r0 = r0.clone();	 Catch:{ CloneNotSupportedException -> 0x001d }
        r0 = (long[]) r0;	 Catch:{ CloneNotSupportedException -> 0x001d }
        r1.mValues = r0;	 Catch:{ CloneNotSupportedException -> 0x001d }
        goto L_0x001d;
    L_0x001c:
        r1 = r0;
    L_0x001d:
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.support.SparseLongArray.clone():org.telegram.messenger.support.SparseLongArray");
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
        int i2 = i + 1;
        System.arraycopy(this.mKeys, i2, this.mKeys, i, this.mSize - i2);
        System.arraycopy(this.mValues, i2, this.mValues, i, this.mSize - i2);
        this.mSize--;
    }

    public void put(int i, long j) {
        int binarySearch = binarySearch(this.mKeys, 0, this.mSize, (long) i);
        if (binarySearch >= 0) {
            this.mValues[binarySearch] = j;
            return;
        }
        binarySearch ^= -1;
        if (this.mSize >= this.mKeys.length) {
            growKeyAndValueArrays(this.mSize + 1);
        }
        if (this.mSize - binarySearch != 0) {
            int i2 = binarySearch + 1;
            System.arraycopy(this.mKeys, binarySearch, this.mKeys, i2, this.mSize - binarySearch);
            System.arraycopy(this.mValues, binarySearch, this.mValues, i2, this.mSize - binarySearch);
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
        if (this.mSize == 0 || i > this.mKeys[this.mSize - 1]) {
            int i2 = this.mSize;
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
        Object obj = new int[i];
        i = new long[i];
        System.arraycopy(this.mKeys, 0, obj, 0, this.mKeys.length);
        System.arraycopy(this.mValues, 0, i, 0, this.mValues.length);
        this.mKeys = obj;
        this.mValues = i;
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
