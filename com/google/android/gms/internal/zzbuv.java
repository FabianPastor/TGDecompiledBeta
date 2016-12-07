package com.google.android.gms.internal;

import java.io.IOException;
import java.util.Arrays;

final class zzbuv {
    final int tag;
    final byte[] zzcsh;

    zzbuv(int i, byte[] bArr) {
        this.tag = i;
        this.zzcsh = bArr;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzbuv)) {
            return false;
        }
        zzbuv com_google_android_gms_internal_zzbuv = (zzbuv) obj;
        return this.tag == com_google_android_gms_internal_zzbuv.tag && Arrays.equals(this.zzcsh, com_google_android_gms_internal_zzbuv.zzcsh);
    }

    public int hashCode() {
        return ((this.tag + 527) * 31) + Arrays.hashCode(this.zzcsh);
    }

    void zza(zzbum com_google_android_gms_internal_zzbum) throws IOException {
        com_google_android_gms_internal_zzbum.zzqt(this.tag);
        com_google_android_gms_internal_zzbum.zzah(this.zzcsh);
    }

    int zzv() {
        return (zzbum.zzqu(this.tag) + 0) + this.zzcsh.length;
    }
}
