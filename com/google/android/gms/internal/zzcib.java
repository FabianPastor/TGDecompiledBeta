package com.google.android.gms.internal;

final class zzcib implements Runnable {
    private /* synthetic */ zzchz zzbtQ;
    private /* synthetic */ zzcic zzbtR;

    zzcib(zzchz com_google_android_gms_internal_zzchz, zzcic com_google_android_gms_internal_zzcic) {
        this.zzbtQ = com_google_android_gms_internal_zzchz;
        this.zzbtR = com_google_android_gms_internal_zzcic;
    }

    public final void run() {
        this.zzbtQ.zza(this.zzbtR);
        this.zzbtQ.zzbtE = null;
        this.zzbtQ.zzww().zza(null);
    }
}
