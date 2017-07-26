package com.google.android.gms.internal;

import java.util.Arrays;

final class adr {
    final int tag;
    final byte[] zzbws;

    adr(int i, byte[] bArr) {
        this.tag = i;
        this.zzbws = bArr;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof adr)) {
            return false;
        }
        adr com_google_android_gms_internal_adr = (adr) obj;
        return this.tag == com_google_android_gms_internal_adr.tag && Arrays.equals(this.zzbws, com_google_android_gms_internal_adr.zzbws);
    }

    public final int hashCode() {
        return ((this.tag + 527) * 31) + Arrays.hashCode(this.zzbws);
    }
}
