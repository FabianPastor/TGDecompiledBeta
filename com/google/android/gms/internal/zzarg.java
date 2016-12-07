package com.google.android.gms.internal;

public final class zzarg implements Cloneable {
    private static final zzarh bqx = new zzarh();
    private zzarh[] bqA;
    private boolean bqy;
    private int[] bqz;
    private int mSize;

    zzarg() {
        this(10);
    }

    zzarg(int i) {
        this.bqy = false;
        int idealIntArraySize = idealIntArraySize(i);
        this.bqz = new int[idealIntArraySize];
        this.bqA = new zzarh[idealIntArraySize];
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

    private boolean zza(zzarh[] com_google_android_gms_internal_zzarhArr, zzarh[] com_google_android_gms_internal_zzarhArr2, int i) {
        for (int i2 = 0; i2 < i; i2++) {
            if (!com_google_android_gms_internal_zzarhArr[i2].equals(com_google_android_gms_internal_zzarhArr2[i2])) {
                return false;
            }
        }
        return true;
    }

    private int zzahs(int i) {
        int i2 = 0;
        int i3 = this.mSize - 1;
        while (i2 <= i3) {
            int i4 = (i2 + i3) >>> 1;
            int i5 = this.bqz[i4];
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

    public final zzarg cR() {
        int size = size();
        zzarg com_google_android_gms_internal_zzarg = new zzarg(size);
        System.arraycopy(this.bqz, 0, com_google_android_gms_internal_zzarg.bqz, 0, size);
        for (int i = 0; i < size; i++) {
            if (this.bqA[i] != null) {
                com_google_android_gms_internal_zzarg.bqA[i] = (zzarh) this.bqA[i].clone();
            }
        }
        com_google_android_gms_internal_zzarg.mSize = size;
        return com_google_android_gms_internal_zzarg;
    }

    public /* synthetic */ Object clone() throws CloneNotSupportedException {
        return cR();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzarg)) {
            return false;
        }
        zzarg com_google_android_gms_internal_zzarg = (zzarg) obj;
        return size() != com_google_android_gms_internal_zzarg.size() ? false : zza(this.bqz, com_google_android_gms_internal_zzarg.bqz, this.mSize) && zza(this.bqA, com_google_android_gms_internal_zzarg.bqA, this.mSize);
    }

    public int hashCode() {
        int i = 17;
        for (int i2 = 0; i2 < this.mSize; i2++) {
            i = (((i * 31) + this.bqz[i2]) * 31) + this.bqA[i2].hashCode();
        }
        return i;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    int size() {
        return this.mSize;
    }

    void zza(int i, zzarh com_google_android_gms_internal_zzarh) {
        int zzahs = zzahs(i);
        if (zzahs >= 0) {
            this.bqA[zzahs] = com_google_android_gms_internal_zzarh;
            return;
        }
        zzahs ^= -1;
        if (zzahs >= this.mSize || this.bqA[zzahs] != bqx) {
            if (this.mSize >= this.bqz.length) {
                int idealIntArraySize = idealIntArraySize(this.mSize + 1);
                Object obj = new int[idealIntArraySize];
                Object obj2 = new zzarh[idealIntArraySize];
                System.arraycopy(this.bqz, 0, obj, 0, this.bqz.length);
                System.arraycopy(this.bqA, 0, obj2, 0, this.bqA.length);
                this.bqz = obj;
                this.bqA = obj2;
            }
            if (this.mSize - zzahs != 0) {
                System.arraycopy(this.bqz, zzahs, this.bqz, zzahs + 1, this.mSize - zzahs);
                System.arraycopy(this.bqA, zzahs, this.bqA, zzahs + 1, this.mSize - zzahs);
            }
            this.bqz[zzahs] = i;
            this.bqA[zzahs] = com_google_android_gms_internal_zzarh;
            this.mSize++;
            return;
        }
        this.bqz[zzahs] = i;
        this.bqA[zzahs] = com_google_android_gms_internal_zzarh;
    }

    zzarh zzahq(int i) {
        int zzahs = zzahs(i);
        return (zzahs < 0 || this.bqA[zzahs] == bqx) ? null : this.bqA[zzahs];
    }

    zzarh zzahr(int i) {
        return this.bqA[i];
    }
}
