package com.google.android.gms.wearable.internal;

import android.os.RemoteException;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.internal.zzbay;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi.GetFdForAssetResult;
import com.google.android.gms.wearable.DataItemAsset;

final class zzbp extends zzn<GetFdForAssetResult> {
    private /* synthetic */ DataItemAsset zzbSx;

    zzbp(zzbi com_google_android_gms_wearable_internal_zzbi, GoogleApiClient googleApiClient, DataItemAsset dataItemAsset) {
        this.zzbSx = dataItemAsset;
        super(googleApiClient);
    }

    protected final /* synthetic */ void zza(zzb com_google_android_gms_common_api_Api_zzb) throws RemoteException {
        ((zzfw) com_google_android_gms_common_api_Api_zzb).zza((zzbay) this, Asset.createFromRef(this.zzbSx.getId()));
    }

    protected final /* synthetic */ Result zzb(Status status) {
        return new zzbu(status, null);
    }
}