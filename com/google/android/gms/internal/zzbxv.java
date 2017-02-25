package com.google.android.gms.internal;

import java.io.IOException;
import java.util.Arrays;

final class zzbxv {
    final int tag;
    final byte[] zzbyd;

    zzbxv(int i, byte[] bArr) {
        this.tag = i;
        this.zzbyd = bArr;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzbxv)) {
            return false;
        }
        zzbxv com_google_android_gms_internal_zzbxv = (zzbxv) obj;
        return this.tag == com_google_android_gms_internal_zzbxv.tag && Arrays.equals(this.zzbyd, com_google_android_gms_internal_zzbxv.zzbyd);
    }

    public int hashCode() {
        return ((this.tag + 527) * 31) + Arrays.hashCode(this.zzbyd);
    }

    void zza(zzbxm com_google_android_gms_internal_zzbxm) throws IOException {
        com_google_android_gms_internal_zzbxm.zzrj(this.tag);
        com_google_android_gms_internal_zzbxm.zzaj(this.zzbyd);
    }

    int zzu() {
        return (zzbxm.zzrk(this.tag) + 0) + this.zzbyd.length;
    }
}
