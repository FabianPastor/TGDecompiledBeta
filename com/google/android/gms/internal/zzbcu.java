package com.google.android.gms.internal;

import java.lang.ref.WeakReference;

final class zzbcu extends zzbdk {
    private WeakReference<zzbco> zzaDR;

    zzbcu(zzbco com_google_android_gms_internal_zzbco) {
        this.zzaDR = new WeakReference(com_google_android_gms_internal_zzbco);
    }

    public final void zzpA() {
        zzbco com_google_android_gms_internal_zzbco = (zzbco) this.zzaDR.get();
        if (com_google_android_gms_internal_zzbco != null) {
            com_google_android_gms_internal_zzbco.resume();
        }
    }
}
