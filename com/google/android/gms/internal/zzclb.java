package com.google.android.gms.internal;

import android.content.Intent;

final /* synthetic */ class zzclb implements Runnable {
    private final zzcla zzjiv;
    private final int zzjiw;
    private final zzchm zzjix;
    private final Intent zzjiy;

    zzclb(zzcla com_google_android_gms_internal_zzcla, int i, zzchm com_google_android_gms_internal_zzchm, Intent intent) {
        this.zzjiv = com_google_android_gms_internal_zzcla;
        this.zzjiw = i;
        this.zzjix = com_google_android_gms_internal_zzchm;
        this.zzjiy = intent;
    }

    public final void run() {
        this.zzjiv.zza(this.zzjiw, this.zzjix, this.zzjiy);
    }
}
