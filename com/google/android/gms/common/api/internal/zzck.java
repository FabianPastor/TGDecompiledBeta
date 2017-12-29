package com.google.android.gms.common.api.internal;

public final class zzck<L> {
    private final L zzfuk;
    private final String zzfun;

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof zzck)) {
            return false;
        }
        zzck com_google_android_gms_common_api_internal_zzck = (zzck) obj;
        return this.zzfuk == com_google_android_gms_common_api_internal_zzck.zzfuk && this.zzfun.equals(com_google_android_gms_common_api_internal_zzck.zzfun);
    }

    public final int hashCode() {
        return (System.identityHashCode(this.zzfuk) * 31) + this.zzfun.hashCode();
    }
}
