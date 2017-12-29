package com.google.android.gms.common.api.internal;

import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.zzan;
import com.google.android.gms.common.internal.zzj;
import java.util.Set;

final class zzbu implements zzcy, zzj {
    private Set<Scope> zzehs = null;
    private final zzh<?> zzfmf;
    private final zze zzfpv;
    private zzan zzfrh = null;
    final /* synthetic */ zzbm zzfti;
    private boolean zzftu = false;

    public zzbu(zzbm com_google_android_gms_common_api_internal_zzbm, zze com_google_android_gms_common_api_Api_zze, zzh<?> com_google_android_gms_common_api_internal_zzh_) {
        this.zzfti = com_google_android_gms_common_api_internal_zzbm;
        this.zzfpv = com_google_android_gms_common_api_Api_zze;
        this.zzfmf = com_google_android_gms_common_api_internal_zzh_;
    }

    private final void zzajg() {
        if (this.zzftu && this.zzfrh != null) {
            this.zzfpv.zza(this.zzfrh, this.zzehs);
        }
    }

    public final void zzb(zzan com_google_android_gms_common_internal_zzan, Set<Scope> set) {
        if (com_google_android_gms_common_internal_zzan == null || set == null) {
            Log.wtf("GoogleApiManager", "Received null response from onSignInSuccess", new Exception());
            zzh(new ConnectionResult(4));
            return;
        }
        this.zzfrh = com_google_android_gms_common_internal_zzan;
        this.zzehs = set;
        zzajg();
    }

    public final void zzf(ConnectionResult connectionResult) {
        this.zzfti.mHandler.post(new zzbv(this, connectionResult));
    }

    public final void zzh(ConnectionResult connectionResult) {
        ((zzbo) this.zzfti.zzfpy.get(this.zzfmf)).zzh(connectionResult);
    }
}
