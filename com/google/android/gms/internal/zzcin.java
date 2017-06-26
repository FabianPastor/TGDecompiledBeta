package com.google.android.gms.internal;

final class zzcin implements Runnable {
    private /* synthetic */ zzcjh zzbtk;
    private /* synthetic */ zzcic zzbua;
    private /* synthetic */ boolean zzbue;

    zzcin(zzcic com_google_android_gms_internal_zzcic, boolean z, zzcjh com_google_android_gms_internal_zzcjh) {
        this.zzbua = com_google_android_gms_internal_zzcic;
        this.zzbue = z;
        this.zzbtk = com_google_android_gms_internal_zzcjh;
    }

    public final void run() {
        zzcfc zzd = this.zzbua.zzbtU;
        if (zzd == null) {
            this.zzbua.zzwF().zzyx().log("Discarding data. Failed to set user attribute");
            return;
        }
        this.zzbua.zza(zzd, this.zzbue ? null : this.zzbtk);
        this.zzbua.zzkP();
    }
}
