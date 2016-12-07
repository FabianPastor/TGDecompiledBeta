package com.google.android.gms.internal;

public final class zzarw implements Cloneable {
    private static final zzarx btI = new zzarx();
    private boolean btJ;
    private int[] btK;
    private zzarx[] btL;
    private int mSize;

    zzarw() {
        this(10);
    }

    zzarw(int i) {
        this.btJ = false;
        int idealIntArraySize = idealIntArraySize(i);
        this.btK = new int[idealIntArraySize];
        this.btL = new zzarx[idealIntArraySize];
        this.mSize = 0;
    }

    private int idealByteArraySize(int i) {
        for (int i2 = 4; i2 < 32; i2++) {
            if (i <= (1 << i2) - 12) {
                return (1 << i2) - 12;
            }
        }
        return i;
    }

    private int idealIntArraySize(int i) {
        return idealByteArraySize(i * 4) / 4;
    }

    private boolean zza(int[] iArr, int[] iArr2, int i) {
        for (int i2 = 0; i2 < i; i2++) {
            if (iArr[i2] != iArr2[i2]) {
                return false;
            }
        }
        return true;
    }

    private boolean zza(zzarx[] com_google_android_gms_internal_zzarxArr, zzarx[] com_google_android_gms_internal_zzarxArr2, int i) {
        for (int i2 = 0; i2 < i; i2++) {
            if (!com_google_android_gms_internal_zzarxArr[i2].equals(com_google_android_gms_internal_zzarxArr2[i2])) {
                return false;
            }
        }
        return true;
    }

    private int zzahj(int i) {
        int i2 = 0;
        int i3 = this.mSize - 1;
        while (i2 <= i3) {
            int i4 = (i2 + i3) >>> 1;
            int i5 = this.btK[i4];
            if (i5 < i) {
                i2 = i4 + 1;
            } else if (i5 <= i) {
                return i4;
            } else {
                i3 = i4 - 1;
            }
        }
        return i2 ^ -1;
    }

    public /* synthetic */ Object clone() throws CloneNotSupportedException {
        return cp();
    }

    public final zzarw cp() {
        int size = size();
        zzarw com_google_android_gms_internal_zzarw = new zzarw(size);
        System.arraycopy(this.btK, 0, com_google_android_gms_internal_zzarw.btK, 0, size);
        for (int i = 0; i < size; i++) {
            if (this.btL[i] != null) {
                com_google_android_gms_internal_zzarw.btL[i] = (zzarx) this.btL[i].clone();
            }
        }
        com_google_android_gms_internal_zzarw.mSize = size;
        return com_google_android_gms_internal_zzarw;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzarw)) {
            return false;
        }
        zzarw com_google_android_gms_internal_zzarw = (zzarw) obj;
        return size() != com_google_android_gms_internal_zzarw.size() ? false : zza(this.btK, com_google_android_gms_internal_zzarw.btK, this.mSize) && zza(this.btL, com_google_android_gms_internal_zzarw.btL, this.mSize);
    }

    public int hashCode() {
        int i = 17;
        for (int i2 = 0; i2 < this.mSize; i2++) {
            i = (((i * 31) + this.btK[i2]) * 31) + this.btL[i2].hashCode();
        }
        return i;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    int size() {
        return this.mSize;
    }

    void zza(int i, zzarx com_google_android_gms_internal_zzarx) {
        int zzahj = zzahj(i);
        if (zzahj >= 0) {
            this.btL[zzahj] = com_google_android_gms_internal_zzarx;
            return;
        }
        zzahj ^= -1;
        if (zzahj >= this.mSize || this.btL[zzahj] != btI) {
            if (this.mSize >= this.btK.length) {
                int idealIntArraySize = idealIntArraySize(this.mSize + 1);
                Object obj = new int[idealIntArraySize];
                Object obj2 = new zzarx[idealIntArraySize];
                System.arraycopy(this.btK, 0, obj, 0, this.btK.length);
                System.arraycopy(this.btL, 0, obj2, 0, this.btL.length);
                this.btK = obj;
                this.btL = obj2;
            }
            if (this.mSize - zzahj != 0) {
                System.arraycopy(this.btK, zzahj, this.btK, zzahj + 1, this.mSize - zzahj);
                System.arraycopy(this.btL, zzahj, this.btL, zzahj + 1, this.mSize - zzahj);
            }
            this.btK[zzahj] = i;
            this.btL[zzahj] = com_google_android_gms_internal_zzarx;
            this.mSize++;
            return;
        }
        this.btK[zzahj] = i;
        this.btL[zzahj] = com_google_android_gms_internal_zzarx;
    }

    zzarx zzahh(int i) {
        int zzahj = zzahj(i);
        return (zzahj < 0 || this.btL[zzahj] == btI) ? null : this.btL[zzahj];
    }

    zzarx zzahi(int i) {
        return this.btL[i];
    }
}
