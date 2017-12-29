package com.google.android.gms.common.internal;

import com.google.android.gms.common.ConnectionResult;

public final class zzm implements zzj {
    private /* synthetic */ zzd zzfza;

    public zzm(zzd com_google_android_gms_common_internal_zzd) {
        this.zzfza = com_google_android_gms_common_internal_zzd;
    }

    public final void zzf(ConnectionResult connectionResult) {
        if (connectionResult.isSuccess()) {
            this.zzfza.zza(null, this.zzfza.zzakp());
        } else if (this.zzfza.zzfys != null) {
            this.zzfza.zzfys.onConnectionFailed(connectionResult);
        }
    }
}
