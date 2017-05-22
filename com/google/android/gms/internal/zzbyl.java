package com.google.android.gms.internal;

import java.io.IOException;
import java.util.Arrays;

final class zzbyl {
    final int tag;
    final byte[] zzbyc;

    zzbyl(int i, byte[] bArr) {
        this.tag = i;
        this.zzbyc = bArr;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzbyl)) {
            return false;
        }
        zzbyl com_google_android_gms_internal_zzbyl = (zzbyl) obj;
        return this.tag == com_google_android_gms_internal_zzbyl.tag && Arrays.equals(this.zzbyc, com_google_android_gms_internal_zzbyl.zzbyc);
    }

    public int hashCode() {
        return ((this.tag + 527) * 31) + Arrays.hashCode(this.zzbyc);
    }

    void zza(zzbyc com_google_android_gms_internal_zzbyc) throws IOException {
        com_google_android_gms_internal_zzbyc.zzrp(this.tag);
        com_google_android_gms_internal_zzbyc.zzak(this.zzbyc);
    }

    int zzu() {
        return (zzbyc.zzrq(this.tag) + 0) + this.zzbyc.length;
    }
}
