package com.google.android.gms.common.internal;

import android.os.Bundle;
import com.google.android.gms.common.ConnectionResult;

public final class zzo extends zze {
    private /* synthetic */ zzd zzfza;

    public zzo(zzd com_google_android_gms_common_internal_zzd, int i, Bundle bundle) {
        this.zzfza = com_google_android_gms_common_internal_zzd;
        super(com_google_android_gms_common_internal_zzd, i, null);
    }

    protected final boolean zzakr() {
        this.zzfza.zzfym.zzf(ConnectionResult.zzfkr);
        return true;
    }

    protected final void zzj(ConnectionResult connectionResult) {
        this.zzfza.zzfym.zzf(connectionResult);
        this.zzfza.onConnectionFailed(connectionResult);
    }
}
