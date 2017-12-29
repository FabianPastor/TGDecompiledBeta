package com.google.android.gms.common.internal;

import android.app.PendingIntent;
import android.os.Bundle;
import com.google.android.gms.common.ConnectionResult;

abstract class zze extends zzi<Boolean> {
    private int statusCode;
    private Bundle zzfyz;
    private /* synthetic */ zzd zzfza;

    protected zze(zzd com_google_android_gms_common_internal_zzd, int i, Bundle bundle) {
        this.zzfza = com_google_android_gms_common_internal_zzd;
        super(com_google_android_gms_common_internal_zzd, Boolean.valueOf(true));
        this.statusCode = i;
        this.zzfyz = bundle;
    }

    protected abstract boolean zzakr();

    protected abstract void zzj(ConnectionResult connectionResult);

    protected final /* synthetic */ void zzw(Object obj) {
        PendingIntent pendingIntent = null;
        if (((Boolean) obj) == null) {
            this.zzfza.zza(1, null);
            return;
        }
        switch (this.statusCode) {
            case 0:
                if (!zzakr()) {
                    this.zzfza.zza(1, null);
                    zzj(new ConnectionResult(8, null));
                    return;
                }
                return;
            case 10:
                this.zzfza.zza(1, null);
                throw new IllegalStateException("A fatal developer error has occurred. Check the logs for further information.");
            default:
                this.zzfza.zza(1, null);
                if (this.zzfyz != null) {
                    pendingIntent = (PendingIntent) this.zzfyz.getParcelable("pendingIntent");
                }
                zzj(new ConnectionResult(this.statusCode, pendingIntent));
                return;
        }
    }
}
