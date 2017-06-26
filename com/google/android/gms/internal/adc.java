package com.google.android.gms.internal;

public final class adc implements Cloneable {
    private static final add zzcsb = new add();
    private int mSize;
    private boolean zzcsc;
    private int[] zzcsd;
    private add[] zzcse;

    adc() {
        this(10);
    }

    private adc(int i) {
        this.zzcsc = false;
        int idealIntArraySize = idealIntArraySize(i);
        this.zzcsd = new int[idealIntArraySize];
        this.zzcse = new add[idealIntArraySize];
        this.mSize = 0;
    }

    private static int idealIntArraySize(int i) {
        int i2 = i << 2;
        for (int i3 = 4; i3 < 32; i3++) {
            if (i2 <= (1 << i3) - 12) {
                i2 = (1 << i3) - 12;
                break;
            }
        }
        return i2 / 4;
    }

    private final int zzcz(int i) {
        int i2 = 0;
        int i3 = this.mSize - 1;
        while (i2 <= i3) {
            int i4 = (i2 + i3) >>> 1;
            int i5 = this.zzcsd[i4];
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

    public final /* synthetic */ Object clone() throws CloneNotSupportedException {
        int i = this.mSize;
        adc com_google_android_gms_internal_adc = new adc(i);
        System.arraycopy(this.zzcsd, 0, com_google_android_gms_internal_adc.zzcsd, 0, i);
        for (int i2 = 0; i2 < i; i2++) {
            if (this.zzcse[i2] != null) {
                com_google_android_gms_internal_adc.zzcse[i2] = (add) this.zzcse[i2].clone();
            }
        }
        com_google_android_gms_internal_adc.mSize = i;
        return com_google_android_gms_internal_adc;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof adc)) {
            return false;
        }
        adc com_google_android_gms_internal_adc = (adc) obj;
        if (this.mSize != com_google_android_gms_internal_adc.mSize) {
            return false;
        }
        int i;
        boolean z;
        int[] iArr = this.zzcsd;
        int[] iArr2 = com_google_android_gms_internal_adc.zzcsd;
        int i2 = this.mSize;
        for (i = 0; i < i2; i++) {
            if (iArr[i] != iArr2[i]) {
                z = false;
                break;
            }
        }
        z = true;
        if (z) {
            add[] com_google_android_gms_internal_addArr = this.zzcse;
            add[] com_google_android_gms_internal_addArr2 = com_google_android_gms_internal_adc.zzcse;
            i2 = this.mSize;
            for (i = 0; i < i2; i++) {
                if (!com_google_android_gms_internal_addArr[i].equals(com_google_android_gms_internal_addArr2[i])) {
                    z = false;
                    break;
                }
            }
            z = true;
            if (z) {
                return true;
            }
        }
        return false;
    }

    public final int hashCode() {
        int i = 17;
        for (int i2 = 0; i2 < this.mSize; i2++) {
            i = (((i * 31) + this.zzcsd[i2]) * 31) + this.zzcse[i2].hashCode();
        }
        return i;
    }

    public final boolean isEmpty() {
        return this.mSize == 0;
    }

    final int size() {
        return this.mSize;
    }

    final void zza(int i, add com_google_android_gms_internal_add) {
        int zzcz = zzcz(i);
        if (zzcz >= 0) {
            this.zzcse[zzcz] = com_google_android_gms_internal_add;
            return;
        }
        zzcz ^= -1;
        if (zzcz >= this.mSize || this.zzcse[zzcz] != zzcsb) {
            if (this.mSize >= this.zzcsd.length) {
                int idealIntArraySize = idealIntArraySize(this.mSize + 1);
                Object obj = new int[idealIntArraySize];
                Object obj2 = new add[idealIntArraySize];
                System.arraycopy(this.zzcsd, 0, obj, 0, this.zzcsd.length);
                System.arraycopy(this.zzcse, 0, obj2, 0, this.zzcse.length);
                this.zzcsd = obj;
                this.zzcse = obj2;
            }
            if (this.mSize - zzcz != 0) {
                System.arraycopy(this.zzcsd, zzcz, this.zzcsd, zzcz + 1, this.mSize - zzcz);
                System.arraycopy(this.zzcse, zzcz, this.zzcse, zzcz + 1, this.mSize - zzcz);
            }
            this.zzcsd[zzcz] = i;
            this.zzcse[zzcz] = com_google_android_gms_internal_add;
            this.mSize++;
            return;
        }
        this.zzcsd[zzcz] = i;
        this.zzcse[zzcz] = com_google_android_gms_internal_add;
    }

    final add zzcx(int i) {
        int zzcz = zzcz(i);
        return (zzcz < 0 || this.zzcse[zzcz] == zzcsb) ? null : this.zzcse[zzcz];
    }

    final add zzcy(int i) {
        return this.zzcse[i];
    }
}
