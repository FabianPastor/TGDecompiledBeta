package com.google.android.gms.internal;

import android.support.annotation.WorkerThread;

abstract class zzbcm implements Runnable {
    private /* synthetic */ zzbcc zzaDp;

    private zzbcm(zzbcc com_google_android_gms_internal_zzbcc) {
        this.zzaDp = com_google_android_gms_internal_zzbcc;
    }

    @WorkerThread
    public void run() {
        this.zzaDp.zzaCv.lock();
        try {
            if (!Thread.interrupted()) {
                zzpV();
                this.zzaDp.zzaCv.unlock();
            }
        } catch (RuntimeException e) {
            this.zzaDp.zzaCZ.zza(e);
        } finally {
            this.zzaDp.zzaCv.unlock();
        }
    }

    @WorkerThread
    protected abstract void zzpV();
}
