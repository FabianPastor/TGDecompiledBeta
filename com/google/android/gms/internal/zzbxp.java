package com.google.android.gms.internal;

public final class zzbxp implements Cloneable {
    private static final zzbxq zzcuK = new zzbxq();
    private int mSize;
    private boolean zzcuL;
    private int[] zzcuM;
    private zzbxq[] zzcuN;

    zzbxp() {
        this(10);
    }

    zzbxp(int i) {
        this.zzcuL = false;
        int idealIntArraySize = idealIntArraySize(i);
        this.zzcuM = new int[idealIntArraySize];
        this.zzcuN = new zzbxq[idealIntArraySize];
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

    private boolean zza(zzbxq[] com_google_android_gms_internal_zzbxqArr, zzbxq[] com_google_android_gms_internal_zzbxqArr2, int i) {
        for (int i2 = 0; i2 < i; i2++) {
            if (!com_google_android_gms_internal_zzbxqArr[i2].equals(com_google_android_gms_internal_zzbxqArr2[i2])) {
                return false;
            }
        }
        return true;
    }

    private int zzrq(int i) {
        int i2 = 0;
        int i3 = this.mSize - 1;
        while (i2 <= i3) {
            int i4 = (i2 + i3) >>> 1;
            int i5 = this.zzcuM[i4];
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
        return zzaeJ();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzbxp)) {
            return false;
        }
        zzbxp com_google_android_gms_internal_zzbxp = (zzbxp) obj;
        return size() != com_google_android_gms_internal_zzbxp.size() ? false : zza(this.zzcuM, com_google_android_gms_internal_zzbxp.zzcuM, this.mSize) && zza(this.zzcuN, com_google_android_gms_internal_zzbxp.zzcuN, this.mSize);
    }

    public int hashCode() {
        int i = 17;
        for (int i2 = 0; i2 < this.mSize; i2++) {
            i = (((i * 31) + this.zzcuM[i2]) * 31) + this.zzcuN[i2].hashCode();
        }
        return i;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    int size() {
        return this.mSize;
    }

    void zza(int i, zzbxq com_google_android_gms_internal_zzbxq) {
        int zzrq = zzrq(i);
        if (zzrq >= 0) {
            this.zzcuN[zzrq] = com_google_android_gms_internal_zzbxq;
            return;
        }
        zzrq ^= -1;
        if (zzrq >= this.mSize || this.zzcuN[zzrq] != zzcuK) {
            if (this.mSize >= this.zzcuM.length) {
                int idealIntArraySize = idealIntArraySize(this.mSize + 1);
                Object obj = new int[idealIntArraySize];
                Object obj2 = new zzbxq[idealIntArraySize];
                System.arraycopy(this.zzcuM, 0, obj, 0, this.zzcuM.length);
                System.arraycopy(this.zzcuN, 0, obj2, 0, this.zzcuN.length);
                this.zzcuM = obj;
                this.zzcuN = obj2;
            }
            if (this.mSize - zzrq != 0) {
                System.arraycopy(this.zzcuM, zzrq, this.zzcuM, zzrq + 1, this.mSize - zzrq);
                System.arraycopy(this.zzcuN, zzrq, this.zzcuN, zzrq + 1, this.mSize - zzrq);
            }
            this.zzcuM[zzrq] = i;
            this.zzcuN[zzrq] = com_google_android_gms_internal_zzbxq;
            this.mSize++;
            return;
        }
        this.zzcuM[zzrq] = i;
        this.zzcuN[zzrq] = com_google_android_gms_internal_zzbxq;
    }

    public final zzbxp zzaeJ() {
        int size = size();
        zzbxp com_google_android_gms_internal_zzbxp = new zzbxp(size);
        System.arraycopy(this.zzcuM, 0, com_google_android_gms_internal_zzbxp.zzcuM, 0, size);
        for (int i = 0; i < size; i++) {
            if (this.zzcuN[i] != null) {
                com_google_android_gms_internal_zzbxp.zzcuN[i] = (zzbxq) this.zzcuN[i].clone();
            }
        }
        com_google_android_gms_internal_zzbxp.mSize = size;
        return com_google_android_gms_internal_zzbxp;
    }

    zzbxq zzro(int i) {
        int zzrq = zzrq(i);
        return (zzrq < 0 || this.zzcuN[zzrq] == zzcuK) ? null : this.zzcuN[zzrq];
    }

    zzbxq zzrp(int i) {
        return this.zzcuN[i];
    }
}
