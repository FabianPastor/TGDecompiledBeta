package com.google.android.gms.internal;

import java.util.Arrays;

final class zzbyz extends zzbyu {
    final transient byte[][] zzcyf;
    final transient int[] zzcyg;

    zzbyz(zzbyr com_google_android_gms_internal_zzbyr, int i) {
        int i2 = 0;
        super(null);
        zzbzd.checkOffsetAndCount(com_google_android_gms_internal_zzbyr.zzaA, 0, (long) i);
        zzbyx com_google_android_gms_internal_zzbyx = com_google_android_gms_internal_zzbyr.zzcxU;
        int i3 = 0;
        int i4 = 0;
        while (i4 < i) {
            if (com_google_android_gms_internal_zzbyx.limit == com_google_android_gms_internal_zzbyx.pos) {
                throw new AssertionError("s.limit == s.pos");
            }
            i4 += com_google_android_gms_internal_zzbyx.limit - com_google_android_gms_internal_zzbyx.pos;
            i3++;
            com_google_android_gms_internal_zzbyx = com_google_android_gms_internal_zzbyx.zzcyc;
        }
        this.zzcyf = new byte[i3][];
        this.zzcyg = new int[(i3 * 2)];
        zzbyx com_google_android_gms_internal_zzbyx2 = com_google_android_gms_internal_zzbyr.zzcxU;
        i4 = 0;
        while (i2 < i) {
            this.zzcyf[i4] = com_google_android_gms_internal_zzbyx2.data;
            int i5 = (com_google_android_gms_internal_zzbyx2.limit - com_google_android_gms_internal_zzbyx2.pos) + i2;
            if (i5 > i) {
                i5 = i;
            }
            this.zzcyg[i4] = i5;
            this.zzcyg[this.zzcyf.length + i4] = com_google_android_gms_internal_zzbyx2.pos;
            com_google_android_gms_internal_zzbyx2.zzcya = true;
            i4++;
            com_google_android_gms_internal_zzbyx2 = com_google_android_gms_internal_zzbyx2.zzcyc;
            i2 = i5;
        }
    }

    private zzbyu zzaga() {
        return new zzbyu(toByteArray());
    }

    private int zzrA(int i) {
        int binarySearch = Arrays.binarySearch(this.zzcyg, 0, this.zzcyf.length, i + 1);
        return binarySearch >= 0 ? binarySearch : binarySearch ^ -1;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        boolean z = (obj instanceof zzbyu) && ((zzbyu) obj).size() == size() && zza(0, (zzbyu) obj, 0, size());
        return z;
    }

    public byte getByte(int i) {
        zzbzd.checkOffsetAndCount((long) this.zzcyg[this.zzcyf.length - 1], (long) i, 1);
        int zzrA = zzrA(i);
        return this.zzcyf[zzrA][(i - (zzrA == 0 ? 0 : this.zzcyg[zzrA - 1])) + this.zzcyg[this.zzcyf.length + zzrA]];
    }

    public int hashCode() {
        int i = this.zzcug;
        if (i == 0) {
            i = 1;
            int length = this.zzcyf.length;
            int i2 = 0;
            int i3 = 0;
            while (i2 < length) {
                byte[] bArr = this.zzcyf[i2];
                int i4 = this.zzcyg[length + i2];
                int i5 = this.zzcyg[i2];
                i3 = (i5 - i3) + i4;
                int i6 = i4;
                i4 = i;
                for (i = i6; i < i3; i++) {
                    i4 = (i4 * 31) + bArr[i];
                }
                i2++;
                i3 = i5;
                i = i4;
            }
            this.zzcug = i;
        }
        return i;
    }

    public int size() {
        return this.zzcyg[this.zzcyf.length - 1];
    }

    public byte[] toByteArray() {
        int i = 0;
        Object obj = new byte[this.zzcyg[this.zzcyf.length - 1]];
        int length = this.zzcyf.length;
        int i2 = 0;
        while (i < length) {
            int i3 = this.zzcyg[length + i];
            int i4 = this.zzcyg[i];
            System.arraycopy(this.zzcyf[i], i3, obj, i2, i4 - i2);
            i++;
            i2 = i4;
        }
        return obj;
    }

    public String toString() {
        return zzaga().toString();
    }

    public zzbyu zzP(int i, int i2) {
        return zzaga().zzP(i, i2);
    }

    public boolean zza(int i, zzbyu com_google_android_gms_internal_zzbyu, int i2, int i3) {
        if (i < 0 || i > size() - i3) {
            return false;
        }
        int zzrA = zzrA(i);
        while (i3 > 0) {
            int i4 = zzrA == 0 ? 0 : this.zzcyg[zzrA - 1];
            int min = Math.min(i3, ((this.zzcyg[zzrA] - i4) + i4) - i);
            if (!com_google_android_gms_internal_zzbyu.zza(i2, this.zzcyf[zzrA], (i - i4) + this.zzcyg[this.zzcyf.length + zzrA], min)) {
                return false;
            }
            i += min;
            i2 += min;
            i3 -= min;
            zzrA++;
        }
        return true;
    }

    public boolean zza(int i, byte[] bArr, int i2, int i3) {
        if (i < 0 || i > size() - i3 || i2 < 0 || i2 > bArr.length - i3) {
            return false;
        }
        int zzrA = zzrA(i);
        while (i3 > 0) {
            int i4 = zzrA == 0 ? 0 : this.zzcyg[zzrA - 1];
            int min = Math.min(i3, ((this.zzcyg[zzrA] - i4) + i4) - i);
            if (!zzbzd.zza(this.zzcyf[zzrA], (i - i4) + this.zzcyg[this.zzcyf.length + zzrA], bArr, i2, min)) {
                return false;
            }
            i += min;
            i2 += min;
            i3 -= min;
            zzrA++;
        }
        return true;
    }

    public String zzafV() {
        return zzaga().zzafV();
    }

    public String zzafW() {
        return zzaga().zzafW();
    }
}
