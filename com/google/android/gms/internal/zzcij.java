package com.google.android.gms.internal;

import android.os.RemoteException;
import android.text.TextUtils;

final class zzcij implements Runnable {
    private /* synthetic */ String zzbjh;
    private /* synthetic */ zzcey zzbtj;
    private /* synthetic */ zzcic zzbua;
    private /* synthetic */ boolean zzbud = true;
    private /* synthetic */ boolean zzbue;

    zzcij(zzcic com_google_android_gms_internal_zzcic, boolean z, boolean z2, zzcey com_google_android_gms_internal_zzcey, String str) {
        this.zzbua = com_google_android_gms_internal_zzcic;
        this.zzbue = z2;
        this.zzbtj = com_google_android_gms_internal_zzcey;
        this.zzbjh = str;
    }

    public final void run() {
        zzcfc zzd = this.zzbua.zzbtU;
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
