package com.google.android.gms.internal;

final class zzbyx {
    final byte[] data;
    int limit;
    int pos;
    boolean zzcya;
    boolean zzcyb;
    zzbyx zzcyc;
    zzbyx zzcyd;

    zzbyx() {
        this.data = new byte[8192];
        this.zzcyb = true;
        this.zzcya = false;
    }

    zzbyx(zzbyx com_google_android_gms_internal_zzbyx) {
        this(com_google_android_gms_internal_zzbyx.data, com_google_android_gms_internal_zzbyx.pos, com_google_android_gms_internal_zzbyx.limit);
        com_google_android_gms_internal_zzbyx.zzcya = true;
    }

    zzbyx(byte[] bArr, int i, int i2) {
        this.data = bArr;
        this.pos = i;
        this.limit = i2;
        this.zzcyb = false;
        this.zzcya = true;
    }

    public zzbyx zza(zzbyx com_google_android_gms_internal_zzbyx) {
        com_google_android_gms_internal_zzbyx.zzcyd = this;
        com_google_android_gms_internal_zzbyx.zzcyc = this.zzcyc;
        this.zzcyc.zzcyd = com_google_android_gms_internal_zzbyx;
        this.zzcyc = com_google_android_gms_internal_zzbyx;
        return com_google_android_gms_internal_zzbyx;
    }

    public void zza(zzbyx com_google_android_gms_internal_zzbyx, int i) {
        if (com_google_android_gms_internal_zzbyx.zzcyb) {
            if (com_google_android_gms_internal_zzbyx.limit + i > 8192) {
                if (com_google_android_gms_internal_zzbyx.zzcya) {
                    throw new IllegalArgumentException();
                } else if ((com_google_android_gms_internal_zzbyx.limit + i) - com_google_android_gms_internal_zzbyx.pos > 8192) {
                    throw new IllegalArgumentException();
                } else {
                    System.arraycopy(com_google_android_gms_internal_zzbyx.data, com_google_android_gms_internal_zzbyx.pos, com_google_android_gms_internal_zzbyx.data, 0, com_google_android_gms_internal_zzbyx.limit - com_google_android_gms_internal_zzbyx.pos);
                    com_google_android_gms_internal_zzbyx.limit -= com_google_android_gms_internal_zzbyx.pos;
                    com_google_android_gms_internal_zzbyx.pos = 0;
                }
            }
            System.arraycopy(this.data, this.pos, com_google_android_gms_internal_zzbyx.data, com_google_android_gms_internal_zzbyx.limit, i);
            com_google_android_gms_internal_zzbyx.limit += i;
            this.pos += i;
            return;
        }
        throw new IllegalArgumentException();
    }

    public zzbyx zzafX() {
        zzbyx com_google_android_gms_internal_zzbyx = this.zzcyc != this ? this.zzcyc : null;
        this.zzcyd.zzcyc = this.zzcyc;
        this.zzcyc.zzcyd = this.zzcyd;
        this.zzcyc = null;
        this.zzcyd = null;
        return com_google_android_gms_internal_zzbyx;
    }

    public void zzafY() {
        if (this.zzcyd == this) {
            throw new IllegalStateException();
        } else if (this.zzcyd.zzcyb) {
            int i = this.limit - this.pos;
            if (i <= (this.zzcyd.zzcya ? 0 : this.zzcyd.pos) + (8192 - this.zzcyd.limit)) {
                zza(this.zzcyd, i);
                zzafX();
                zzbyy.zzb(this);
            }
        }
    }

    public zzbyx zzrz(int i) {
        if (i <= 0 || i > this.limit - this.pos) {
            throw new IllegalArgumentException();
        }
        zzbyx com_google_android_gms_internal_zzbyx;
        if (i >= 1024) {
            com_google_android_gms_internal_zzbyx = new zzbyx(this);
        } else {
            com_google_android_gms_internal_zzbyx = zzbyy.zzafZ();
            System.arraycopy(this.data, this.pos, com_google_android_gms_internal_zzbyx.data, 0, i);
        }
        com_google_android_gms_internal_zzbyx.limit = com_google_android_gms_internal_zzbyx.pos + i;
        this.pos += i;
        this.zzcyd.zza(com_google_android_gms_internal_zzbyx);
        return com_google_android_gms_internal_zzbyx;
    }
}
