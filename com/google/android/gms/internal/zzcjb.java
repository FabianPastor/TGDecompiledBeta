package com.google.android.gms.internal;

import android.support.annotation.WorkerThread;

final class zzcjb extends zzceq {
    private /* synthetic */ zzciz zzbuu;

    zzcjb(zzciz com_google_android_gms_internal_zzciz, zzcgk com_google_android_gms_internal_zzcgk) {
        this.zzbuu = com_google_android_gms_internal_zzciz;
        super(com_google_android_gms_internal_zzcgk);
    }

    @WorkerThread
    public final void run() {
        this.zzbuu.zzzp();
    }
}
