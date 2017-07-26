package com.google.android.gms.internal;

import android.os.RemoteException;
import android.text.TextUtils;

final class zzcik implements Runnable {
    private /* synthetic */ String zzbjh;
    private /* synthetic */ zzcez zzbtj;
    private /* synthetic */ zzcid zzbua;
    private /* synthetic */ boolean zzbud = true;
    private /* synthetic */ boolean zzbue;

    zzcik(zzcid com_google_android_gms_internal_zzcid, boolean z, boolean z2, zzcez com_google_android_gms_internal_zzcez, String str) {
        this.zzbua = com_google_android_gms_internal_zzcid;
        this.zzbue = z2;
        this.zzbtj = com_google_android_gms_internal_zzcez;
        this.zzbjh = str;
    }

    public final void run() {
        zzcfd zzd = this.zzbua.zzbtU;
        if (zzd == null) {
            this.zzbua.zzwF().zzyx().log("Discarding data. Failed to send event to service");
            return;
        }
        if (this.zzbud) {
            this.zzbua.zza(zzd, this.zzbue ? null : this.zzbtj);
        } else {
            try {
                if (TextUtils.isEmpty(this.zzbjh)) {
                    zzd.zza(this.zzbtj, this.zzbua.zzwu().zzdV(this.zzbua.zzwF().zzyE()));
                } else {
                    zzd.zza(this.zzbtj, this.zzbjh, this.zzbua.zzwF().zzyE());
                }
            } catch (RemoteException e) {
                this.zzbua.zzwF().zzyx().zzj("Failed to send event to the service", e);
            }
        }
        this.zzbua.zzkP();
    }
}
