package com.google.android.gms.internal;

import android.support.annotation.WorkerThread;

abstract class zzbcn implements Runnable {
    private /* synthetic */ zzbcd zzaDp;

    private zzbcn(zzbcd com_google_android_gms_internal_zzbcd) {
        this.zzaDp = com_google_android_gms_internal_zzbcd;
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
