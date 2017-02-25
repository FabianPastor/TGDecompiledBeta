package com.google.android.gms.internal;

public final class zzbxp implements Cloneable {
    private static final zzbxq zzcuC = new zzbxq();
    private int mSize;
    private boolean zzcuD;
    private int[] zzcuE;
    private zzbxq[] zzcuF;

    zzbxp() {
        this(10);
    }

    zzbxp(int i) {
        this.zzcuD = false;
        int idealIntArraySize = idealIntArraySize(i);
        this.zzcuE = new int[idealIntArraySize];
        this.zzcuF = new zzbxq[idealIntArraySize];
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

    private int zzrp(int i) {
        int i2 = 0;
        int i3 = this.mSize - 1;
        while (i2 <= i3) {
            int i4 = (i2 + i3) >>> 1;
            int i5 = this.zzcuE[i4];
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
        return zzaeI();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzbxp)) {
            return false;
        }
        zzbxp com_google_android_gms_internal_zzbxp = (zzbxp) obj;
        return size() != com_google_android_gms_internal_zzbxp.size() ? false : zza(this.zzcuE, com_google_android_gms_internal_zzbxp.zzcuE, this.mSize) && zza(this.zzcuF, com_google_android_gms_internal_zzbxp.zzcuF, this.mSize);
    }

    public int hashCode() {
        int i = 17;
        for (int i2 = 0; i2 < this.mSize; i2++) {
            i = (((i * 31) + this.zzcuE[i2]) * 31) + this.zzcuF[i2].hashCode();
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
        int zzrp = zzrp(i);
        if (zzrp >= 0) {
            this.zzcuF[zzrp] = com_google_android_gms_internal_zzbxq;
            return;
        }
        zzrp ^= -1;
        if (zzrp >= this.mSize || this.zzcuF[zzrp] != zzcuC) {
            if (this.mSize >= this.zzcuE.length) {
                int idealIntArraySize = idealIntArraySize(this.mSize + 1);
                Object obj = new int[idealIntArraySize];
                Object obj2 = new zzbxq[idealIntArraySize];
                System.arraycopy(this.zzcuE, 0, obj, 0, this.zzcuE.length);
                System.arraycopy(this.zzcuF, 0, obj2, 0, this.zzcuF.length);
                this.zzcuE = obj;
                this.zzcuF = obj2;
            }
            if (this.mSize - zzrp != 0) {
                System.arraycopy(this.zzcuE, zzrp, this.zzcuE, zzrp + 1, this.mSize - zzrp);
                System.arraycopy(this.zzcuF, zzrp, this.zzcuF, zzrp + 1, this.mSize - zzrp);
            }
            this.zzcuE[zzrp] = i;
            this.zzcuF[zzrp] = com_google_android_gms_internal_zzbxq;
            this.mSize++;
            return;
        }
        this.zzcuE[zzrp] = i;
        this.zzcuF[zzrp] = com_google_android_gms_internal_zzbxq;
    }

    public final zzbxp zzaeI() {
        int size = size();
        zzbxp com_google_android_gms_internal_zzbxp = new zzbxp(size);
        System.arraycopy(this.zzcuE, 0, com_google_android_gms_internal_zzbxp.zzcuE, 0, size);
        for (int i = 0; i < size; i++) {
            if (this.zzcuF[i] != null) {
                com_google_android_gms_internal_zzbxp.zzcuF[i] = (zzbxq) this.zzcuF[i].clone();
            }
        }
        com_google_android_gms_internal_zzbxp.mSize = size;
        return com_google_android_gms_internal_zzbxp;
    }

    zzbxq zzrn(int i) {
        int zzrp = zzrp(i);
        return (zzrp < 0 || this.zzcuF[zzrp] == zzcuC) ? null : this.zzcuF[zzrp];
    }

    zzbxq zzro(int i) {
        return this.zzcuF[i];
    }
}
