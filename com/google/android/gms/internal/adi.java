package com.google.android.gms.internal;

import java.util.Arrays;

final class adi {
    final int tag;
    final byte[] zzbws;

    adi(int i, byte[] bArr) {
        this.tag = i;
        this.zzbws = bArr;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof adi)) {
            return false;
        }
        adi com_google_android_gms_internal_adi = (adi) obj;
        return this.tag == com_google_android_gms_internal_adi.tag && Arrays.equals(this.zzbws, com_google_android_gms_internal_adi.zzbws);
    }

    public final int hashCode() {
        return ((this.tag + 527) * 31) + Arrays.hashCode(this.zzbws);
    }
}
