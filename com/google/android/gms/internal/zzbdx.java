package com.google.android.gms.internal;

public final class zzbdx<L> {
    private final L mListener;
    private final String zzaEP;

    zzbdx(L l, String str) {
        this.mListener = l;
        this.zzaEP = str;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof zzbdx)) {
            return false;
        }
        zzbdx com_google_android_gms_internal_zzbdx = (zzbdx) obj;
        return this.mListener == com_google_android_gms_internal_zzbdx.mListener && this.zzaEP.equals(com_google_android_gms_internal_zzbdx.zzaEP);
    }

    public final int hashCode() {
        return (System.identityHashCode(this.mListener) * 31) + this.zzaEP.hashCode();
    }
}
