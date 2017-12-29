package com.google.android.gms.internal;

final class zzchn implements Runnable {
    private /* synthetic */ String zzjcd;
    private /* synthetic */ zzchm zzjce;

    zzchn(zzchm com_google_android_gms_internal_zzchm, String str) {
        this.zzjce = com_google_android_gms_internal_zzchm;
        this.zzjcd = str;
    }

    public final void run() {
        zzcjl zzawz = this.zzjce.zziwf.zzawz();
        if (zzawz.isInitialized()) {
            zzawz.zzjcq.zzf(this.zzjcd, 1);
        } else {
            this.zzjce.zzk(6, "Persisted config not initialized. Not logging error/warn");
        }
    }
}
