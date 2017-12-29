package com.google.android.gms.internal;

final class zzcks implements Runnable {
    private /* synthetic */ zzcgi zzjgn;
    private /* synthetic */ zzcln zzjgt;
    private /* synthetic */ zzckg zzjij;
    private /* synthetic */ boolean zzjin;

    zzcks(zzckg com_google_android_gms_internal_zzckg, boolean z, zzcln com_google_android_gms_internal_zzcln, zzcgi com_google_android_gms_internal_zzcgi) {
        this.zzjij = com_google_android_gms_internal_zzckg;
        this.zzjin = z;
        this.zzjgt = com_google_android_gms_internal_zzcln;
        this.zzjgn = com_google_android_gms_internal_zzcgi;
    }

    public final void run() {
        zzche zzd = this.zzjij.zzjid;
        if (zzd == null) {
            this.zzjij.zzawy().zzazd().log("Discarding data. Failed to set user attribute");
            return;
        }
        this.zzjij.zza(zzd, this.zzjin ? null : this.zzjgt, this.zzjgn);
        this.zzjij.zzxr();
    }
}
