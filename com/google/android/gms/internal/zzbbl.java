package com.google.android.gms.internal;

final class zzbbl implements Runnable {
    private /* synthetic */ zzbbk zzaCx;

    zzbbl(zzbbk com_google_android_gms_internal_zzbbk) {
        this.zzaCx = com_google_android_gms_internal_zzbbk;
    }

    public final void run() {
        this.zzaCx.zzaCv.lock();
        try {
            this.zzaCx.zzpF();
        } finally {
            this.zzaCx.zzaCv.unlock();
        }
    }
}
