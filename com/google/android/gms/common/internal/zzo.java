package com.google.android.gms.common.internal;

import android.os.Bundle;
import android.support.annotation.BinderThread;
import android.support.annotation.Nullable;
import com.google.android.gms.common.ConnectionResult;

public final class zzo extends zze {
    private /* synthetic */ zzd zzaHe;

    @BinderThread
    public zzo(zzd com_google_android_gms_common_internal_zzd, @Nullable int i, Bundle bundle) {
        this.zzaHe = com_google_android_gms_common_internal_zzd;
        super(com_google_android_gms_common_internal_zzd, i, null);
    }

    protected final void zzj(ConnectionResult connectionResult) {
        this.zzaHe.zzaGQ.zzf(connectionResult);
        this.zzaHe.onConnectionFailed(connectionResult);
    }

    protected final boolean zzrj() {
        this.zzaHe.zzaGQ.zzf(ConnectionResult.zzazX);
        return true;
    }
}
