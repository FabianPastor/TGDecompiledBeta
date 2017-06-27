package com.google.android.gms.wearable.internal;

import android.os.RemoteException;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.internal.zzbay;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi.GetFdForAssetResult;

final class zzbo extends zzn<GetFdForAssetResult> {
    private /* synthetic */ Asset zzbSw;

    zzbo(zzbi com_google_android_gms_wearable_internal_zzbi, GoogleApiClient googleApiClient, Asset asset) {
        this.zzbSw = asset;
        super(googleApiClient);
    }

    protected final /* synthetic */ void zza(zzb com_google_android_gms_common_api_Api_zzb) throws RemoteException {
        ((zzfw) com_google_android_gms_common_api_Api_zzb).zza((zzbay) this, this.zzbSw);
    }

    protected final /* synthetic */ Result zzb(Status status) {
        return new zzbu(status, null);
    }
}