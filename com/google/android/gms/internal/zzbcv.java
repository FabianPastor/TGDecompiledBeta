package com.google.android.gms.internal;

import java.lang.ref.WeakReference;

final class zzbcv extends zzbdl {
    private WeakReference<zzbcp> zzaDR;

    zzbcv(zzbcp com_google_android_gms_internal_zzbcp) {
        this.zzaDR = new WeakReference(com_google_android_gms_internal_zzbcp);
    }

    public final void zzpA() {
        zzbcp com_google_android_gms_internal_zzbcp = (zzbcp) this.zzaDR.get();
        if (com_google_android_gms_internal_zzbcp != null) {
            com_google_android_gms_internal_zzbcp.resume();
        }
    }
}
