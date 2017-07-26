package com.google.android.gms.wearable.internal;

import android.os.RemoteException;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.internal.zzbaz;
import com.google.android.gms.wearable.DataApi.DataItemResult;
import com.google.android.gms.wearable.PutDataRequest;

final class zzbj extends zzn<DataItemResult> {
    private /* synthetic */ PutDataRequest zzbSw;

    zzbj(zzbi com_google_android_gms_wearable_internal_zzbi, GoogleApiClient googleApiClient, PutDataRequest putDataRequest) {
        this.zzbSw = putDataRequest;
        super(googleApiClient);
    }

    protected final /* synthetic */ void zza(zzb com_google_android_gms_common_api_Api_zzb) throws RemoteException {
        ((zzfw) com_google_android_gms_common_api_Api_zzb).zza((zzbaz) this, this.zzbSw);
    }

    public final /* synthetic */ Result zzb(Status status) {
        return new zzbs(status, null);
    }
}
