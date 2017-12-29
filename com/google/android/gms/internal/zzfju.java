package com.google.android.gms.internal;

import java.util.Arrays;

final class zzfju {
    final int tag;
    final byte[] zzjng;

    zzfju(int i, byte[] bArr) {
        this.tag = i;
        this.zzjng = bArr;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzfju)) {
            return false;
        }
        zzfju com_google_android_gms_internal_zzfju = (zzfju) obj;
        return this.tag == com_google_android_gms_internal_zzfju.tag && Arrays.equals(this.zzjng, com_google_android_gms_internal_zzfju.zzjng);
    }

    public final int hashCode() {
        return ((this.tag + 527) * 31) + Arrays.hashCode(this.zzjng);
    }
}
