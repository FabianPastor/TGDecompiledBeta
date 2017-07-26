package com.google.android.gms.internal;

import android.support.annotation.WorkerThread;

final class zzcjc extends zzcer {
    private /* synthetic */ zzcja zzbuu;

    zzcjc(zzcja com_google_android_gms_internal_zzcja, zzcgl com_google_android_gms_internal_zzcgl) {
        this.zzbuu = com_google_android_gms_internal_zzcja;
        super(com_google_android_gms_internal_zzcgl);
    }

    @WorkerThread
    public final void run() {
        this.zzbuu.zzzp();
    }
}
