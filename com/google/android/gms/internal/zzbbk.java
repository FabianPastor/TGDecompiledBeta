package com.google.android.gms.internal;

final class zzbbk implements Runnable {
    private /* synthetic */ zzbbj zzaCx;

    zzbbk(zzbbj com_google_android_gms_internal_zzbbj) {
        this.zzaCx = com_google_android_gms_internal_zzbbj;
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
