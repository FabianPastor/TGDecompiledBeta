package com.google.android.gms.common.api.internal;

import android.os.Bundle;
import com.google.android.gms.common.ConnectionResult;

final class zzx implements zzcd {
    private /* synthetic */ zzv zzfpu;

    private zzx(zzv com_google_android_gms_common_api_internal_zzv) {
        this.zzfpu = com_google_android_gms_common_api_internal_zzv;
    }

    public final void zzc(ConnectionResult connectionResult) {
        this.zzfpu.zzfps.lock();
        try {
            this.zzfpu.zzfpp = connectionResult;
            this.zzfpu.zzahl();
        } finally {
            this.zzfpu.zzfps.unlock();
        }
    }

    public final void zzf(int i, boolean z) {
        this.zzfpu.zzfps.lock();
        try {
            if (this.zzfpu.zzfpr || this.zzfpu.zzfpq == null || !this.zzfpu.zzfpq.isSuccess()) {
                this.zzfpu.zzfpr = false;
                this.zzfpu.zze(i, z);
                return;
            }
            this.zzfpu.zzfpr = true;
            this.zzfpu.zzfpk.onConnectionSuspended(i);
            this.zzfpu.zzfps.unlock();
        } finally {
            this.zzfpu.zzfps.unlock();
        }
    }

    public final void zzj(Bundle bundle) {
        this.zzfpu.zzfps.lock();
        try {
            this.zzfpu.zzi(bundle);
            this.zzfpu.zzfpp = ConnectionResult.zzfkr;
            this.zzfpu.zzahl();
        } finally {
            this.zzfpu.zzfps.unlock();
        }
    }
}
