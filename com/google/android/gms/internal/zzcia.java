package com.google.android.gms.internal;

final class zzcia implements Runnable {
    private /* synthetic */ zzchy zzbtQ;
    private /* synthetic */ zzcib zzbtR;

    zzcia(zzchy com_google_android_gms_internal_zzchy, zzcib com_google_android_gms_internal_zzcib) {
        this.zzbtQ = com_google_android_gms_internal_zzchy;
        this.zzbtR = com_google_android_gms_internal_zzcib;
    }

    public final void run() {
        this.zzbtQ.zza(this.zzbtR);
        this.zzbtQ.zzbtE = null;
        this.zzbtQ.zzww().zza(null);
    }
}
