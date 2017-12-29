package com.google.android.gms.common.api.internal;

abstract class zzay implements Runnable {
    private /* synthetic */ zzao zzfrl;

    private zzay(zzao com_google_android_gms_common_api_internal_zzao) {
        this.zzfrl = com_google_android_gms_common_api_internal_zzao;
    }

    public void run() {
        this.zzfrl.zzfps.lock();
        try {
            if (!Thread.interrupted()) {
                zzaib();
                this.zzfrl.zzfps.unlock();
            }
        } catch (RuntimeException e) {
            this.zzfrl.zzfqv.zza(e);
        } finally {
            this.zzfrl.zzfps.unlock();
        }
    }

    protected abstract void zzaib();
}
