package com.google.android.gms.wearable.internal;

import android.os.RemoteException;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.internal.zzbay;
import com.google.android.gms.wearable.DataApi.DataListener;

final class zzbr extends zzn<Status> {
    private /* synthetic */ DataListener zzbSy;

    zzbr(zzbi com_google_android_gms_wearable_internal_zzbi, GoogleApiClient googleApiClient, DataListener dataListener) {
        this.zzbSy = dataListener;
        super(googleApiClient);
    }

    protected final /* synthetic */ void zza(zzb com_google_android_gms_common_api_Api_zzb) throws RemoteException {
        ((zzfw) com_google_android_gms_common_api_Api_zzb).zza((zzbay) this, this.zzbSy);
    }

    public final /* synthetic */ Result zzb(Status status) {
        return status;
    }
}
