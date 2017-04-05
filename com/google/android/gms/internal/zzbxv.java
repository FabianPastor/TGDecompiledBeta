package com.google.android.gms.internal;

import java.io.IOException;
import java.util.Arrays;

final class zzbxv {
    final int tag;
    final byte[] zzbxZ;

    zzbxv(int i, byte[] bArr) {
        this.tag = i;
        this.zzbxZ = bArr;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzbxv)) {
            return false;
        }
        zzbxv com_google_android_gms_internal_zzbxv = (zzbxv) obj;
        return this.tag == com_google_android_gms_internal_zzbxv.tag && Arrays.equals(this.zzbxZ, com_google_android_gms_internal_zzbxv.zzbxZ);
    }

    public int hashCode() {
        return ((this.tag + 527) * 31) + Arrays.hashCode(this.zzbxZ);
    }

    void zza(zzbxm com_google_android_gms_internal_zzbxm) throws IOException {
        com_google_android_gms_internal_zzbxm.zzrk(this.tag);
        com_google_android_gms_internal_zzbxm.zzaj(this.zzbxZ);
    }

    int zzu() {
        return (zzbxm.zzrl(this.tag) + 0) + this.zzbxZ.length;
    }
}
