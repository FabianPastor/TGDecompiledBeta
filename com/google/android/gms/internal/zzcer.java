package com.google.android.gms.internal;

import android.os.Looper;

final class zzcer implements Runnable {
    private /* synthetic */ zzceq zzbpB;

    zzcer(zzceq com_google_android_gms_internal_zzceq) {
        this.zzbpB = com_google_android_gms_internal_zzceq;
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
