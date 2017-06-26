package com.google.android.gms.internal;

import android.os.Bundle;
import android.os.DeadObjectException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.internal.zzbx;

public final class zzbbz implements zzbcv {
    private final zzbcw zzaCZ;
    private boolean zzaDa = false;

    public zzbbz(zzbcw com_google_android_gms_internal_zzbcw) {
        this.zzaCZ = com_google_android_gms_internal_zzbcw;
    }

    public final void begin() {
    }

    public final void connect() {
        if (this.zzaDa) {
            this.zzaDa = false;
            this.zzaCZ.zza(new zzbcb(this, this));
        }
    }

    public final boolean disconnect() {
        if (this.zzaDa) {
            return false;
        }
        if (this.zzaCZ.zzaCl.zzqf()) {
            this.zzaDa = true;
            for (zzber zzqK : this.zzaCZ.zzaCl.zzaDK) {
                zzqK.zzqK();
            }
            return false;
        }
        this.zzaCZ.zzg(null);
        return true;
    }

    public final void onConnected(Bundle bundle) {
    }

    public final void onConnectionSuspended(int i) {
        this.zzaCZ.zzg(null);
        this.zzaCZ.zzaDY.zze(i, this.zzaDa);
    }

    public final void zza(ConnectionResult connectionResult, Api<?> api, boolean z) {
    }

    public final <A extends zzb, R extends Result, T extends zzbax<R, A>> T zzd(T t) {
        return zze(t);
    }

    public final <A extends zzb, T extends zzbax<? extends Result, A>> T zze(T t) {
        try {
            this.zzaCZ.zzaCl.zzaDL.zzb(t);
            zzbco com_google_android_gms_internal_zzbco = this.zzaCZ.zzaCl;
            zzb com_google_android_gms_common_api_Api_zzb = (zze) com_google_android_gms_internal_zzbco.zzaDF.get(t.zzpd());
            zzbo.zzb((Object) com_google_android_gms_common_api_Api_zzb, (Object) "Appropriate Api was not requested.");
            if (com_google_android_gms_common_api_Api_zzb.isConnected() || !this.zzaCZ.zzaDU.containsKey(t.zzpd())) {
                if (com_google_android_gms_common_api_Api_zzb instanceof zzbx) {
                    zzbx com_google_android_gms_common_internal_zzbx = (zzbx) com_google_android_gms_common_api_Api_zzb;
                    com_google_android_gms_common_api_Api_zzb = null;
                }
                t.zzb(com_google_android_gms_common_api_Api_zzb);
                return t;
            }
            t.zzr(new Status(17));
            return t;
        } catch (DeadObjectException e) {
            this.zzaCZ.zza(new zzbca(this, this));
        }
    }

    final void zzpU() {
        if (this.zzaDa) {
            this.zzaDa = false;
            this.zzaCZ.zzaCl.zzaDL.release();
            disconnect();
        }
    }
}
