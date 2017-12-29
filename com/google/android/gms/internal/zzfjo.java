package com.google.android.gms.internal;

public final class zzfjo implements Cloneable {
    private static final zzfjp zzpne = new zzfjp();
    private int mSize;
    private boolean zzpnf;
    private int[] zzpng;
    private zzfjp[] zzpnh;

    zzfjo() {
        this(10);
    }

    private zzfjo(int i) {
        this.zzpnf = false;
        int idealIntArraySize = idealIntArraySize(i);
        this.zzpng = new int[idealIntArraySize];
        this.zzpnh = new zzfjp[idealIntArraySize];
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

    private final int zzml(int i) {
        int i2 = 0;
        int i3 = this.mSize - 1;
        while (i2 <= i3) {
            int i4 = (i2 + i3) >>> 1;
            int i5 = this.zzpng[i4];
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
        zzfjo com_google_android_gms_internal_zzfjo = new zzfjo(i);
        System.arraycopy(this.zzpng, 0, com_google_android_gms_internal_zzfjo.zzpng, 0, i);
        for (int i2 = 0; i2 < i; i2++) {
            if (this.zzpnh[i2] != null) {
                com_google_android_gms_internal_zzfjo.zzpnh[i2] = (zzfjp) this.zzpnh[i2].clone();
            }
        }
        com_google_android_gms_internal_zzfjo.mSize = i;
        return com_google_android_gms_internal_zzfjo;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzfjo)) {
            return false;
        }
        zzfjo com_google_android_gms_internal_zzfjo = (zzfjo) obj;
        if (this.mSize != com_google_android_gms_internal_zzfjo.mSize) {
            return false;
        }
        int i;
        boolean z;
        int[] iArr = this.zzpng;
        int[] iArr2 = com_google_android_gms_internal_zzfjo.zzpng;
        int i2 = this.mSize;
        for (i = 0; i < i2; i++) {
            if (iArr[i] != iArr2[i]) {
                z = false;
                break;
            }
        }
        z = true;
        if (z) {
            zzfjp[] com_google_android_gms_internal_zzfjpArr = this.zzpnh;
            zzfjp[] com_google_android_gms_internal_zzfjpArr2 = com_google_android_gms_internal_zzfjo.zzpnh;
            i2 = this.mSize;
            for (i = 0; i < i2; i++) {
                if (!com_google_android_gms_internal_zzfjpArr[i].equals(com_google_android_gms_internal_zzfjpArr2[i])) {
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
            i = (((i * 31) + this.zzpng[i2]) * 31) + this.zzpnh[i2].hashCode();
        }
        return i;
    }

    public final boolean isEmpty() {
        return this.mSize == 0;
    }

    final int size() {
        return this.mSize;
    }

    final void zza(int i, zzfjp com_google_android_gms_internal_zzfjp) {
        int zzml = zzml(i);
        if (zzml >= 0) {
            this.zzpnh[zzml] = com_google_android_gms_internal_zzfjp;
            return;
        }
        zzml ^= -1;
        if (zzml >= this.mSize || this.zzpnh[zzml] != zzpne) {
            if (this.mSize >= this.zzpng.length) {
                int idealIntArraySize = idealIntArraySize(this.mSize + 1);
                Object obj = new int[idealIntArraySize];
                Object obj2 = new zzfjp[idealIntArraySize];
                System.arraycopy(this.zzpng, 0, obj, 0, this.zzpng.length);
                System.arraycopy(this.zzpnh, 0, obj2, 0, this.zzpnh.length);
                this.zzpng = obj;
                this.zzpnh = obj2;
            }
            if (this.mSize - zzml != 0) {
                System.arraycopy(this.zzpng, zzml, this.zzpng, zzml + 1, this.mSize - zzml);
                System.arraycopy(this.zzpnh, zzml, this.zzpnh, zzml + 1, this.mSize - zzml);
            }
            this.zzpng[zzml] = i;
            this.zzpnh[zzml] = com_google_android_gms_internal_zzfjp;
            this.mSize++;
            return;
        }
        this.zzpng[zzml] = i;
        this.zzpnh[zzml] = com_google_android_gms_internal_zzfjp;
    }

    final zzfjp zzmj(int i) {
        int zzml = zzml(i);
        return (zzml < 0 || this.zzpnh[zzml] == zzpne) ? null : this.zzpnh[zzml];
    }

    final zzfjp zzmk(int i) {
        return this.zzpnh[i];
    }
}
