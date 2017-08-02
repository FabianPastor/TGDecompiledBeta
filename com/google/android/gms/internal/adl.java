package com.google.android.gms.internal;

public final class adl implements Cloneable {
    private static final adm zzcsq = new adm();
    private int mSize;
    private boolean zzcsr;
    private int[] zzcss;
    private adm[] zzcst;

    adl() {
        this(10);
    }

    private adl(int i) {
        this.zzcsr = false;
        int idealIntArraySize = idealIntArraySize(i);
        this.zzcss = new int[idealIntArraySize];
        this.zzcst = new adm[idealIntArraySize];
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
            int i5 = this.zzcss[i4];
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
        adl com_google_android_gms_internal_adl = new adl(i);
        System.arraycopy(this.zzcss, 0, com_google_android_gms_internal_adl.zzcss, 0, i);
        for (int i2 = 0; i2 < i; i2++) {
            if (this.zzcst[i2] != null) {
                com_google_android_gms_internal_adl.zzcst[i2] = (adm) this.zzcst[i2].clone();
            }
        }
        com_google_android_gms_internal_adl.mSize = i;
        return com_google_android_gms_internal_adl;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof adl)) {
            return false;
        }
        adl com_google_android_gms_internal_adl = (adl) obj;
        if (this.mSize != com_google_android_gms_internal_adl.mSize) {
            return false;
        }
        int i;
        boolean z;
        int[] iArr = this.zzcss;
        int[] iArr2 = com_google_android_gms_internal_adl.zzcss;
        int i2 = this.mSize;
        for (i = 0; i < i2; i++) {
            if (iArr[i] != iArr2[i]) {
                z = false;
                break;
            }
        }
        z = true;
        if (z) {
            adm[] com_google_android_gms_internal_admArr = this.zzcst;
            adm[] com_google_android_gms_internal_admArr2 = com_google_android_gms_internal_adl.zzcst;
            i2 = this.mSize;
            for (i = 0; i < i2; i++) {
                if (!com_google_android_gms_internal_admArr[i].equals(com_google_android_gms_internal_admArr2[i])) {
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
            i = (((i * 31) + this.zzcss[i2]) * 31) + this.zzcst[i2].hashCode();
        }
        return i;
    }

    public final boolean isEmpty() {
        return this.mSize == 0;
    }

    final int size() {
        return this.mSize;
    }

    final void zza(int i, adm com_google_android_gms_internal_adm) {
        int zzcz = zzcz(i);
        if (zzcz >= 0) {
            this.zzcst[zzcz] = com_google_android_gms_internal_adm;
            return;
        }
        zzcz ^= -1;
        if (zzcz >= this.mSize || this.zzcst[zzcz] != zzcsq) {
            if (this.mSize >= this.zzcss.length) {
                int idealIntArraySize = idealIntArraySize(this.mSize + 1);
                Object obj = new int[idealIntArraySize];
                Object obj2 = new adm[idealIntArraySize];
                System.arraycopy(this.zzcss, 0, obj, 0, this.zzcss.length);
                System.arraycopy(this.zzcst, 0, obj2, 0, this.zzcst.length);
                this.zzcss = obj;
                this.zzcst = obj2;
            }
            if (this.mSize - zzcz != 0) {
                System.arraycopy(this.zzcss, zzcz, this.zzcss, zzcz + 1, this.mSize - zzcz);
                System.arraycopy(this.zzcst, zzcz, this.zzcst, zzcz + 1, this.mSize - zzcz);
            }
            this.zzcss[zzcz] = i;
            this.zzcst[zzcz] = com_google_android_gms_internal_adm;
            this.mSize++;
            return;
        }
        this.zzcss[zzcz] = i;
        this.zzcst[zzcz] = com_google_android_gms_internal_adm;
    }

    final adm zzcx(int i) {
        int zzcz = zzcz(i);
        return (zzcz < 0 || this.zzcst[zzcz] == zzcsq) ? null : this.zzcst[zzcz];
    }

    final adm zzcy(int i) {
        return this.zzcst[i];
    }
}
