package com.google.android.gms.common.internal;

import android.app.PendingIntent;
import android.os.Bundle;
import android.support.annotation.BinderThread;
import com.google.android.gms.common.ConnectionResult;

abstract class zze extends zzi<Boolean> {
    private int statusCode;
    private Bundle zzaHd;
    private /* synthetic */ zzd zzaHe;

    @BinderThread
    protected zze(zzd com_google_android_gms_common_internal_zzd, int i, Bundle bundle) {
        this.zzaHe = com_google_android_gms_common_internal_zzd;
        super(com_google_android_gms_common_internal_zzd, Boolean.valueOf(true));
        this.statusCode = i;
        this.zzaHd = bundle;
    }

    protected abstract void zzj(ConnectionResult connectionResult);

    protected abstract boolean zzrj();

    protected final /* synthetic */ void zzs(Object obj) {
        PendingIntent pendingIntent = null;
        if (((Boolean) obj) == null) {
            this.zzaHe.zza(1, null);
            return;
        }
        switch (this.statusCode) {
            case 0:
                if (!zzrj()) {
                    this.zzaHe.zza(1, null);
                    zzj(new ConnectionResult(8, null));
                    return;
                }
                return;
            case 10:
                this.zzaHe.zza(1, null);
                throw new IllegalStateException("A fatal developer error has occurred. Check the logs for further information.");
            default:
                this.zzaHe.zza(1, null);
                if (this.zzaHd != null) {
                    pendingIntent = (PendingIntent) this.zzaHd.getParcelable("pendingIntent");
                }
                zzj(new ConnectionResult(this.statusCode, pendingIntent));
                return;
        }
    }
}
