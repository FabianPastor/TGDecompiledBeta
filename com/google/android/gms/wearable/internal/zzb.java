package com.google.android.gms.wearable.internal;

import android.os.RemoteException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.internal.zzbdv;

final class zzb<T> extends zzn<Status> {
    private T mListener;
    private zzbdv<T> zzaEU;
    private zzc<T> zzbRG;

    private zzb(GoogleApiClient googleApiClient, T t, zzbdv<T> com_google_android_gms_internal_zzbdv_T, zzc<T> com_google_android_gms_wearable_internal_zzc_T) {
        super(googleApiClient);
        this.mListener = zzbo.zzu(t);
        this.zzaEU = (zzbdv) zzbo.zzu(com_google_android_gms_internal_zzbdv_T);
        this.zzbRG = (zzc) zzbo.zzu(com_google_android_gms_wearable_internal_zzc_T);
    }

    static <T> PendingResult<Status> zza(GoogleApiClient googleApiClient, zzc<T> com_google_android_gms_wearable_internal_zzc_T, T t) {
        return googleApiClient.zzd(new zzb(googleApiClient, t, googleApiClient.zzp(t), com_google_android_gms_wearable_internal_zzc_T));
    }

    protected final /* synthetic */ void zza(com.google.android.gms.common.api.Api.zzb com_google_android_gms_common_api_Api_zzb) throws RemoteException {
        this.zzbRG.zza((zzfw) com_google_android_gms_common_api_Api_zzb, this, this.mListener, this.zzaEU);
        this.mListener = null;
        this.zzaEU = null;
    }

    protected final /* synthetic */ Result zzb(Status status) {
        this.mListener = null;
        this.zzaEU = null;
        return status;
    }
}
