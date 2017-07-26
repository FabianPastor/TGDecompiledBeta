package com.google.android.gms.internal;

import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.zzal;
import com.google.android.gms.common.internal.zzj;
import java.util.Set;

final class zzbdh implements zzj, zzbel {
    private final zzbat<?> zzaAK;
    private final zze zzaCy;
    private zzal zzaDl = null;
    final /* synthetic */ zzbdb zzaEm;
    private boolean zzaEx = false;
    private Set<Scope> zzame = null;

    public zzbdh(zzbdb com_google_android_gms_internal_zzbdb, zze com_google_android_gms_common_api_Api_zze, zzbat<?> com_google_android_gms_internal_zzbat_) {
        this.zzaEm = com_google_android_gms_internal_zzbdb;
        this.zzaCy = com_google_android_gms_common_api_Api_zze;
        this.zzaAK = com_google_android_gms_internal_zzbat_;
    }

    @WorkerThread
    private final void zzqz() {
        if (this.zzaEx && this.zzaDl != null) {
            this.zzaCy.zza(this.zzaDl, this.zzame);
        }
    }

    @WorkerThread
    public final void zzb(zzal com_google_android_gms_common_internal_zzal, Set<Scope> set) {
        if (com_google_android_gms_common_internal_zzal == null || set == null) {
            Log.wtf("GoogleApiManager", "Received null response from onSignInSuccess", new Exception());
            zzh(new ConnectionResult(4));
            return;
        }
        this.zzaDl = com_google_android_gms_common_internal_zzal;
        this.zzame = set;
        zzqz();
    }

    public final void zzf(@NonNull ConnectionResult connectionResult) {
        this.zzaEm.mHandler.post(new zzbdi(this, connectionResult));
    }

    @WorkerThread
    public final void zzh(ConnectionResult connectionResult) {
        ((zzbdd) this.zzaEm.zzaCB.get(this.zzaAK)).zzh(connectionResult);
    }
}
