package com.google.android.gms.internal;

final class zzcfm implements Runnable {
    private /* synthetic */ String zzbqV;
    private /* synthetic */ zzcfl zzbqW;

    zzcfm(zzcfl com_google_android_gms_internal_zzcfl, String str) {
        this.zzbqW = com_google_android_gms_internal_zzcfl;
        this.zzbqV = str;
    }

    public final void run() {
        zzcfw zzwG = this.zzbqW.zzboe.zzwG();
        if (zzwG.isInitialized()) {
            zzwG.zzbrj.zzf(this.zzbqV, 1);
        } else {
            this.zzbqW.zzk(6, "Persisted config not initialized. Not logging error/warn");
        }
    }
}
