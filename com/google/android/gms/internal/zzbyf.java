package com.google.android.gms.internal;

public final class zzbyf implements Cloneable {
    private static final zzbyg zzcwE = new zzbyg();
    private int mSize;
    private boolean zzcwF;
    private int[] zzcwG;
    private zzbyg[] zzcwH;

    zzbyf() {
        this(10);
    }

    zzbyf(int i) {
        this.zzcwF = false;
        int idealIntArraySize = idealIntArraySize(i);
        this.zzcwG = new int[idealIntArraySize];
        this.zzcwH = new zzbyg[idealIntArraySize];
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

    private boolean zza(zzbyg[] com_google_android_gms_internal_zzbygArr, zzbyg[] com_google_android_gms_internal_zzbygArr2, int i) {
        for (int i2 = 0; i2 < i; i2++) {
            if (!com_google_android_gms_internal_zzbygArr[i2].equals(com_google_android_gms_internal_zzbygArr2[i2])) {
                return false;
            }
        }
        return true;
    }

    private int zzrv(int i) {
        int i2 = 0;
        int i3 = this.mSize - 1;
        while (i2 <= i3) {
            int i4 = (i2 + i3) >>> 1;
            int i5 = this.zzcwG[i4];
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
        return zzafr();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzbyf)) {
            return false;
        }
        zzbyf com_google_android_gms_internal_zzbyf = (zzbyf) obj;
        return size() != com_google_android_gms_internal_zzbyf.size() ? false : zza(this.zzcwG, com_google_android_gms_internal_zzbyf.zzcwG, this.mSize) && zza(this.zzcwH, com_google_android_gms_internal_zzbyf.zzcwH, this.mSize);
    }

    public int hashCode() {
        int i = 17;
        for (int i2 = 0; i2 < this.mSize; i2++) {
            i = (((i * 31) + this.zzcwG[i2]) * 31) + this.zzcwH[i2].hashCode();
        }
        return i;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    int size() {
        return this.mSize;
    }

    void zza(int i, zzbyg com_google_android_gms_internal_zzbyg) {
        int zzrv = zzrv(i);
        if (zzrv >= 0) {
            this.zzcwH[zzrv] = com_google_android_gms_internal_zzbyg;
            return;
        }
        zzrv ^= -1;
        if (zzrv >= this.mSize || this.zzcwH[zzrv] != zzcwE) {
            if (this.mSize >= this.zzcwG.length) {
                int idealIntArraySize = idealIntArraySize(this.mSize + 1);
                Object obj = new int[idealIntArraySize];
                Object obj2 = new zzbyg[idealIntArraySize];
                System.arraycopy(this.zzcwG, 0, obj, 0, this.zzcwG.length);
                System.arraycopy(this.zzcwH, 0, obj2, 0, this.zzcwH.length);
                this.zzcwG = obj;
                this.zzcwH = obj2;
            }
            if (this.mSize - zzrv != 0) {
                System.arraycopy(this.zzcwG, zzrv, this.zzcwG, zzrv + 1, this.mSize - zzrv);
                System.arraycopy(this.zzcwH, zzrv, this.zzcwH, zzrv + 1, this.mSize - zzrv);
            }
            this.zzcwG[zzrv] = i;
            this.zzcwH[zzrv] = com_google_android_gms_internal_zzbyg;
            this.mSize++;
            return;
        }
        this.zzcwG[zzrv] = i;
        this.zzcwH[zzrv] = com_google_android_gms_internal_zzbyg;
    }

    public final zzbyf zzafr() {
        int size = size();
        zzbyf com_google_android_gms_internal_zzbyf = new zzbyf(size);
        System.arraycopy(this.zzcwG, 0, com_google_android_gms_internal_zzbyf.zzcwG, 0, size);
        for (int i = 0; i < size; i++) {
            if (this.zzcwH[i] != null) {
                com_google_android_gms_internal_zzbyf.zzcwH[i] = (zzbyg) this.zzcwH[i].clone();
            }
        }
        com_google_android_gms_internal_zzbyf.mSize = size;
        return com_google_android_gms_internal_zzbyf;
    }

    zzbyg zzrt(int i) {
        int zzrv = zzrv(i);
        return (zzrv < 0 || this.zzcwH[zzrv] == zzcwE) ? null : this.zzcwH[zzrv];
    }

    zzbyg zzru(int i) {
        return this.zzcwH[i];
    }
}
