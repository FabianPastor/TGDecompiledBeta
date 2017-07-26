package com.google.android.gms.internal;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.gms.common.ConnectionResult;

final class zzbbm implements zzbdq {
    private /* synthetic */ zzbbk zzaCx;

    private zzbbm(zzbbk com_google_android_gms_internal_zzbbk) {
        this.zzaCx = com_google_android_gms_internal_zzbbk;
    }

    public final void zzc(@NonNull ConnectionResult connectionResult) {
        this.zzaCx.zzaCv.lock();
        try {
            this.zzaCx.zzaCs = connectionResult;
            this.zzaCx.zzpF();
        } finally {
            this.zzaCx.zzaCv.unlock();
        }
    }

    public final void zze(int i, boolean z) {
        this.zzaCx.zzaCv.lock();
        try {
            if (this.zzaCx.zzaCu || this.zzaCx.zzaCt == null || !this.zzaCx.zzaCt.isSuccess()) {
                this.zzaCx.zzaCu = false;
                this.zzaCx.zzd(i, z);
                return;
            }
            this.zzaCx.zzaCu = true;
            this.zzaCx.zzaCn.onConnectionSuspended(i);
            this.zzaCx.zzaCv.unlock();
        } finally {
            this.zzaCx.zzaCv.unlock();
        }
    }

    public final void zzm(@Nullable Bundle bundle) {
        this.zzaCx.zzaCv.lock();
        try {
            this.zzaCx.zzl(bundle);
            this.zzaCx.zzaCs = ConnectionResult.zzazX;
            this.zzaCx.zzpF();
        } finally {
            this.zzaCx.zzaCv.unlock();
        }
    }
}
