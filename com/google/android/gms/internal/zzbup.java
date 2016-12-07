package com.google.android.gms.internal;

public final class zzbup implements Cloneable {
    private static final zzbuq zzcrZ = new zzbuq();
    private int mSize;
    private boolean zzcsa;
    private int[] zzcsb;
    private zzbuq[] zzcsc;

    zzbup() {
        this(10);
    }

    zzbup(int i) {
        this.zzcsa = false;
        int idealIntArraySize = idealIntArraySize(i);
        this.zzcsb = new int[idealIntArraySize];
        this.zzcsc = new zzbuq[idealIntArraySize];
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

    private boolean zza(zzbuq[] com_google_android_gms_internal_zzbuqArr, zzbuq[] com_google_android_gms_internal_zzbuqArr2, int i) {
        for (int i2 = 0; i2 < i; i2++) {
            if (!com_google_android_gms_internal_zzbuqArr[i2].equals(com_google_android_gms_internal_zzbuqArr2[i2])) {
                return false;
            }
        }
        return true;
    }

    private int zzqz(int i) {
        int i2 = 0;
        int i3 = this.mSize - 1;
        while (i2 <= i3) {
            int i4 = (i2 + i3) >>> 1;
            int i5 = this.zzcsb[i4];
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
        return zzacP();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzbup)) {
            return false;
        }
        zzbup com_google_android_gms_internal_zzbup = (zzbup) obj;
        return size() != com_google_android_gms_internal_zzbup.size() ? false : zza(this.zzcsb, com_google_android_gms_internal_zzbup.zzcsb, this.mSize) && zza(this.zzcsc, com_google_android_gms_internal_zzbup.zzcsc, this.mSize);
    }

    public int hashCode() {
        int i = 17;
        for (int i2 = 0; i2 < this.mSize; i2++) {
            i = (((i * 31) + this.zzcsb[i2]) * 31) + this.zzcsc[i2].hashCode();
        }
        return i;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    int size() {
        return this.mSize;
    }

    void zza(int i, zzbuq com_google_android_gms_internal_zzbuq) {
        int zzqz = zzqz(i);
        if (zzqz >= 0) {
            this.zzcsc[zzqz] = com_google_android_gms_internal_zzbuq;
            return;
        }
        zzqz ^= -1;
        if (zzqz >= this.mSize || this.zzcsc[zzqz] != zzcrZ) {
            if (this.mSize >= this.zzcsb.length) {
                int idealIntArraySize = idealIntArraySize(this.mSize + 1);
                Object obj = new int[idealIntArraySize];
                Object obj2 = new zzbuq[idealIntArraySize];
                System.arraycopy(this.zzcsb, 0, obj, 0, this.zzcsb.length);
                System.arraycopy(this.zzcsc, 0, obj2, 0, this.zzcsc.length);
                this.zzcsb = obj;
                this.zzcsc = obj2;
            }
            if (this.mSize - zzqz != 0) {
                System.arraycopy(this.zzcsb, zzqz, this.zzcsb, zzqz + 1, this.mSize - zzqz);
                System.arraycopy(this.zzcsc, zzqz, this.zzcsc, zzqz + 1, this.mSize - zzqz);
            }
            this.zzcsb[zzqz] = i;
            this.zzcsc[zzqz] = com_google_android_gms_internal_zzbuq;
            this.mSize++;
            return;
        }
        this.zzcsb[zzqz] = i;
        this.zzcsc[zzqz] = com_google_android_gms_internal_zzbuq;
    }

    public final zzbup zzacP() {
        int size = size();
        zzbup com_google_android_gms_internal_zzbup = new zzbup(size);
        System.arraycopy(this.zzcsb, 0, com_google_android_gms_internal_zzbup.zzcsb, 0, size);
        for (int i = 0; i < size; i++) {
            if (this.zzcsc[i] != null) {
                com_google_android_gms_internal_zzbup.zzcsc[i] = (zzbuq) this.zzcsc[i].clone();
            }
        }
        com_google_android_gms_internal_zzbup.mSize = size;
        return com_google_android_gms_internal_zzbup;
    }

    zzbuq zzqx(int i) {
        int zzqz = zzqz(i);
        return (zzqz < 0 || this.zzcsc[zzqz] == zzcrZ) ? null : this.zzcsc[zzqz];
    }

    zzbuq zzqy(int i) {
        return this.zzcsc[i];
    }
}
