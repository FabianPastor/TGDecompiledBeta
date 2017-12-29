package com.google.android.gms.common.api.internal;

import android.os.Bundle;
import android.os.DeadObjectException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.common.internal.zzbz;

public final class zzal implements zzbh {
    private final zzbi zzfqv;
    private boolean zzfqw = false;

    public zzal(zzbi com_google_android_gms_common_api_internal_zzbi) {
        this.zzfqv = com_google_android_gms_common_api_internal_zzbi;
    }

    public final void begin() {
    }

    public final void connect() {
        if (this.zzfqw) {
            this.zzfqw = false;
            this.zzfqv.zza(new zzan(this, this));
        }
    }

    public final boolean disconnect() {
        if (this.zzfqw) {
            return false;
        }
        if (this.zzfqv.zzfpi.zzail()) {
            this.zzfqw = true;
            for (zzdg zzajs : this.zzfqv.zzfpi.zzfsg) {
                zzajs.zzajs();
            }
            return false;
        }
        this.zzfqv.zzg(null);
        return true;
    }

    public final void onConnected(Bundle bundle) {
    }

    public final void onConnectionSuspended(int i) {
        this.zzfqv.zzg(null);
        this.zzfqv.zzfsu.zzf(i, this.zzfqw);
    }

    public final void zza(ConnectionResult connectionResult, Api<?> api, boolean z) {
    }

    final void zzaia() {
        if (this.zzfqw) {
            this.zzfqw = false;
            this.zzfqv.zzfpi.zzfsh.release();
            disconnect();
        }
    }

    public final <A extends zzb, R extends Result, T extends zzm<R, A>> T zzd(T t) {
        return zze(t);
    }

    public final <A extends zzb, T extends zzm<? extends Result, A>> T zze(T t) {
        try {
            this.zzfqv.zzfpi.zzfsh.zzb(t);
            zzba com_google_android_gms_common_api_internal_zzba = this.zzfqv.zzfpi;
            zzb com_google_android_gms_common_api_Api_zzb = (zze) com_google_android_gms_common_api_internal_zzba.zzfsb.get(t.zzagf());
            zzbq.checkNotNull(com_google_android_gms_common_api_Api_zzb, "Appropriate Api was not requested.");
            if (com_google_android_gms_common_api_Api_zzb.isConnected() || !this.zzfqv.zzfsq.containsKey(t.zzagf())) {
                if (com_google_android_gms_common_api_Api_zzb instanceof zzbz) {
                    com_google_android_gms_common_api_Api_zzb = zzbz.zzals();
                }
                t.zzb(com_google_android_gms_common_api_Api_zzb);
                return t;
            }
            t.zzu(new Status(17));
            return t;
        } catch (DeadObjectException e) {
            this.zzfqv.zza(new zzam(this, this));
        }
    }
}
