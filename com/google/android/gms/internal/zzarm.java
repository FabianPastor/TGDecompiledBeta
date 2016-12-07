package com.google.android.gms.internal;

import java.io.IOException;
import java.util.Arrays;

final class zzarm {
    final byte[] avk;
    final int tag;

    zzarm(int i, byte[] bArr) {
        this.tag = i;
        this.avk = bArr;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzarm)) {
            return false;
        }
        zzarm com_google_android_gms_internal_zzarm = (zzarm) obj;
        return this.tag == com_google_android_gms_internal_zzarm.tag && Arrays.equals(this.avk, com_google_android_gms_internal_zzarm.avk);
    }

    public int hashCode() {
        return ((this.tag + 527) * 31) + Arrays.hashCode(this.avk);
    }

    void zza(zzard com_google_android_gms_internal_zzard) throws IOException {
        com_google_android_gms_internal_zzard.zzahm(this.tag);
        com_google_android_gms_internal_zzard.zzbh(this.avk);
    }

    int zzx() {
        return (zzard.zzahn(this.tag) + 0) + this.avk.length;
    }
}
