package com.google.android.gms.internal;

import android.os.Looper;

final class zzcgt implements Runnable {
    private /* synthetic */ zzcgs zzize;

    zzcgt(zzcgs com_google_android_gms_internal_zzcgs) {
        this.zzize = com_google_android_gms_internal_zzcgs;
    }

    public final void run() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            this.zzize.zziwf.zzawx().zzg(this);
            return;
        }
        boolean zzdx = this.zzize.zzdx();
        this.zzize.zzdvq = 0;
        if (zzdx && this.zzize.zzizd) {
            this.zzize.run();
        }
    }
}
