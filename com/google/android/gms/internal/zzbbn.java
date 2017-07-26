package com.google.android.gms.internal;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.gms.common.ConnectionResult;

final class zzbbn implements zzbdq {
    private /* synthetic */ zzbbk zzaCx;

    private zzbbn(zzbbk com_google_android_gms_internal_zzbbk) {
        this.zzaCx = com_google_android_gms_internal_zzbbk;
    }

    public final void zzc(@NonNull ConnectionResult connectionResult) {
        this.zzaCx.zzaCv.lock();
        try {
            this.zzaCx.zzaCt = connectionResult;
            this.zzaCx.zzpF();
        } finally {
            this.zzaCx.zzaCv.unlock();
        }
    }

    public final void zze(int i, boolean z) {
        this.zzaCx.zzaCv.lock();
        try {
            if (this.zzaCx.zzaCu) {
                this.zzaCx.zzaCu = false;
                this.zzaCx.zzd(i, z);
                return;
            }
            this.zzaCx.zzaCu = true;
            this.zzaCx.zzaCm.onConnectionSuspended(i);
            this.zzaCx.zzaCv.unlock();
        } finally {
            this.zzaCx.zzaCv.unlock();
        }
    }

    public final void zzm(@Nullable Bundle bundle) {
        this.zzaCx.zzaCv.lock();
        try {
            this.zzaCx.zzaCt = ConnectionResult.zzazX;
            this.zzaCx.zzpF();
        } finally {
            this.zzaCx.zzaCv.unlock();
        }
    }
}
