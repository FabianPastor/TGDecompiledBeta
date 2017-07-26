package com.google.android.gms.internal;

import android.os.RemoteException;
import android.text.TextUtils;

final class zzcil implements Runnable {
    private /* synthetic */ zzcid zzbua;
    private /* synthetic */ boolean zzbud = true;
    private /* synthetic */ boolean zzbue;
    private /* synthetic */ zzcek zzbuf;
    private /* synthetic */ zzcek zzbug;

    zzcil(zzcid com_google_android_gms_internal_zzcid, boolean z, boolean z2, zzcek com_google_android_gms_internal_zzcek, zzcek com_google_android_gms_internal_zzcek2) {
        this.zzbua = com_google_android_gms_internal_zzcid;
        this.zzbue = z2;
        this.zzbuf = com_google_android_gms_internal_zzcek;
        this.zzbug = com_google_android_gms_internal_zzcek2;
    }

    public final void run() {
        zzcfd zzd = this.zzbua.zzbtU;
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
