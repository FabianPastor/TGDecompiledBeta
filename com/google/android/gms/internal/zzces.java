package com.google.android.gms.internal;

import android.os.Looper;

final class zzces implements Runnable {
    private /* synthetic */ zzcer zzbpB;

    zzces(zzcer com_google_android_gms_internal_zzcer) {
        this.zzbpB = com_google_android_gms_internal_zzcer;
    }

    public final void run() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            this.zzbpB.zzboe.zzwE().zzj(this);
            return;
        }
        boolean zzbo = this.zzbpB.zzbo();
        this.zzbpB.zzagZ = 0;
        if (zzbo && this.zzbpB.zzbpA) {
            this.zzbpB.run();
        }
    }
}
