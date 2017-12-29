package com.google.android.gms.internal;

import android.os.RemoteException;
import android.text.TextUtils;

final class zzcko implements Runnable {
    private /* synthetic */ String zzimf;
    private /* synthetic */ zzcgi zzjgn;
    private /* synthetic */ zzcha zzjgs;
    private /* synthetic */ zzckg zzjij;
    private /* synthetic */ boolean zzjim = true;
    private /* synthetic */ boolean zzjin;

    zzcko(zzckg com_google_android_gms_internal_zzckg, boolean z, boolean z2, zzcha com_google_android_gms_internal_zzcha, zzcgi com_google_android_gms_internal_zzcgi, String str) {
        this.zzjij = com_google_android_gms_internal_zzckg;
        this.zzjin = z2;
        this.zzjgs = com_google_android_gms_internal_zzcha;
        this.zzjgn = com_google_android_gms_internal_zzcgi;
        this.zzimf = str;
    }

    public final void run() {
        zzche zzd = this.zzjij.zzjid;
        if (zzd == null) {
            this.zzjij.zzawy().zzazd().log("Discarding data. Failed to send event to service");
            return;
        }
        if (this.zzjim) {
            this.zzjij.zza(zzd, this.zzjin ? null : this.zzjgs, this.zzjgn);
        } else {
            try {
                if (TextUtils.isEmpty(this.zzimf)) {
                    zzd.zza(this.zzjgs, this.zzjgn);
                } else {
                    zzd.zza(this.zzjgs, this.zzimf, this.zzjij.zzawy().zzazk());
                }
            } catch (RemoteException e) {
                this.zzjij.zzawy().zzazd().zzj("Failed to send event to the service", e);
            }
        }
        this.zzjij.zzxr();
    }
}
