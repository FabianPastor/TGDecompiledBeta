package com.google.android.gms.internal;

final class zze implements Runnable {
    private /* synthetic */ zzp zzl;
    private /* synthetic */ zzd zzm;

    zze(zzd com_google_android_gms_internal_zzd, zzp com_google_android_gms_internal_zzp) {
        this.zzm = com_google_android_gms_internal_zzd;
        this.zzl = com_google_android_gms_internal_zzp;
    }

    public final void run() {
        try {
            this.zzm.zzh.put(this.zzl);
        } catch (InterruptedException e) {
        }
    }
}
