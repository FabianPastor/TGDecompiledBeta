package com.google.android.gms.internal;

import android.os.RemoteException;
import android.text.TextUtils;

final class zzcik implements Runnable {
    private /* synthetic */ zzcic zzbua;
    private /* synthetic */ boolean zzbud = true;
    private /* synthetic */ boolean zzbue;
    private /* synthetic */ zzcej zzbuf;
    private /* synthetic */ zzcej zzbug;

    zzcik(zzcic com_google_android_gms_internal_zzcic, boolean z, boolean z2, zzcej com_google_android_gms_internal_zzcej, zzcej com_google_android_gms_internal_zzcej2) {
        this.zzbua = com_google_android_gms_internal_zzcic;
        this.zzbue = z2;
        this.zzbuf = com_google_android_gms_internal_zzcej;
        this.zzbug = com_google_android_gms_internal_zzcej2;
    }

    public final void run() {
        zzcfc zzd = this.zzbua.zzbtU;
        if (zzd == null) {
            this.zzbua.zzwF().zzyx().log("Discarding data. Failed to send conditional user property to service");
            return;
        }
        if (this.zzbud) {
            this.zzbua.zza(zzd, this.zzbue ? null : this.zzbuf);
        } else {
            try {
                if (TextUtils.isEmpty(this.zzbug.packageName)) {
                    zzd.zza(this.zzbuf, this.zzbua.zzwu().zzdV(this.zzbua.zzwF().zzyE()));
                } else {
                    zzd.zzb(this.zzbuf);
                }
            } catch (RemoteException e) {
                this.zzbua.zzwF().zzyx().zzj("Failed to send conditional user property to the service", e);
            }
        }
        this.zzbua.zzkP();
    }
}
