package com.google.android.gms.wearable.internal;

import android.os.RemoteException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.internal.zzbdw;

final class zzb<T> extends zzn<Status> {
    private T mListener;
    private zzbdw<T> zzaEU;
    private zzc<T> zzbRI;

    private zzb(GoogleApiClient googleApiClient, T t, zzbdw<T> com_google_android_gms_internal_zzbdw_T, zzc<T> com_google_android_gms_wearable_internal_zzc_T) {
        super(googleApiClient);
        this.mListener = zzbo.zzu(t);
        this.zzaEU = (zzbdw) zzbo.zzu(com_google_android_gms_internal_zzbdw_T);
        this.zzbRI = (zzc) zzbo.zzu(com_google_android_gms_wearable_internal_zzc_T);
    }

    static <T> PendingResult<Status> zza(GoogleApiClient googleApiClient, zzc<T> com_google_android_gms_wearable_internal_zzc_T, T t) {
        return googleApiClient.zzd(new zzb(googleApiClient, t, googleApiClient.zzp(t), com_google_android_gms_wearable_internal_zzc_T));
    }

    protected final /* synthetic */ void zza(com.google.android.gms.common.api.Api.zzb com_google_android_gms_common_api_Api_zzb) throws RemoteException {
        this.zzbRI.zza((zzfw) com_google_android_gms_common_api_Api_zzb, this, this.mListener, this.zzaEU);
        this.mListener = null;
        this.zzaEU = null;
    }

    protected final /* synthetic */ Result zzb(Status status) {
        this.mListener = null;
        this.zzaEU = null;
        return status;
    }
}
