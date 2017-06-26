package com.google.android.gms.internal;

final class zzcfl implements Runnable {
    private /* synthetic */ String zzbqV;
    private /* synthetic */ zzcfk zzbqW;

    zzcfl(zzcfk com_google_android_gms_internal_zzcfk, String str) {
        this.zzbqW = com_google_android_gms_internal_zzcfk;
        this.zzbqV = str;
    }

    public final void run() {
        zzcfv zzwG = this.zzbqW.zzboe.zzwG();
        if (zzwG.isInitialized()) {
            zzwG.zzbrj.zzf(this.zzbqV, 1);
        } else {
            this.zzbqW.zzk(6, "Persisted config not initialized. Not logging error/warn");
        }
    }
}
