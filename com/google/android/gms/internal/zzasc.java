package com.google.android.gms.internal;

import java.io.IOException;
import java.util.Arrays;

final class zzasc {
    final byte[] btQ;
    final int tag;

    zzasc(int i, byte[] bArr) {
        this.tag = i;
        this.btQ = bArr;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzasc)) {
            return false;
        }
        zzasc com_google_android_gms_internal_zzasc = (zzasc) obj;
        return this.tag == com_google_android_gms_internal_zzasc.tag && Arrays.equals(this.btQ, com_google_android_gms_internal_zzasc.btQ);
    }

    public int hashCode() {
        return ((this.tag + 527) * 31) + Arrays.hashCode(this.btQ);
    }

    void zza(zzart com_google_android_gms_internal_zzart) throws IOException {
        com_google_android_gms_internal_zzart.zzahd(this.tag);
        com_google_android_gms_internal_zzart.zzbh(this.btQ);
    }

    int zzx() {
        return (zzart.zzahe(this.tag) + 0) + this.btQ.length;
    }
}
