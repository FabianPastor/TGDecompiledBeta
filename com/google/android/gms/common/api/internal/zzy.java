package com.google.android.gms.common.api.internal;

import android.os.Bundle;
import com.google.android.gms.common.ConnectionResult;

final class zzy implements zzcd {
    private /* synthetic */ zzv zzfpu;

    private zzy(zzv com_google_android_gms_common_api_internal_zzv) {
        this.zzfpu = com_google_android_gms_common_api_internal_zzv;
    }

    public final void zzc(ConnectionResult connectionResult) {
        this.zzfpu.zzfps.lock();
        try {
            this.zzfpu.zzfpq = connectionResult;
            this.zzfpu.zzahl();
        } finally {
            this.zzfpu.zzfps.unlock();
        }
    }

    public final void zzf(int i, boolean z) {
        this.zzfpu.zzfps.lock();
        try {
            if (this.zzfpu.zzfpr) {
                this.zzfpu.zzfpr = false;
                this.zzfpu.zze(i, z);
                return;
            }
            this.zzfpu.zzfpr = true;
            this.zzfpu.zzfpj.onConnectionSuspended(i);
            this.zzfpu.zzfps.unlock();
        } finally {
            this.zzfpu.zzfps.unlock();
        }
    }

    public final void zzj(Bundle bundle) {
        this.zzfpu.zzfps.lock();
        try {
            this.zzfpu.zzfpq = ConnectionResult.zzfkr;
            this.zzfpu.zzahl();
        } finally {
            this.zzfpu.zzfps.unlock();
        }
    }
}
