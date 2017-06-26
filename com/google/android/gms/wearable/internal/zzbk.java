package com.google.android.gms.wearable.internal;

import android.net.Uri;
import android.os.RemoteException;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.DataApi.DataItemResult;

final class zzbk extends zzn<DataItemResult> {
    private /* synthetic */ Uri zzbzR;

    zzbk(zzbi com_google_android_gms_wearable_internal_zzbi, GoogleApiClient googleApiClient, Uri uri) {
        this.zzbzR = uri;
        super(googleApiClient);
    }

    protected final /* synthetic */ void zza(zzb com_google_android_gms_common_api_Api_zzb) throws RemoteException {
        zzfw com_google_android_gms_wearable_internal_zzfw = (zzfw) com_google_android_gms_common_api_Api_zzb;
        ((zzdn) com_google_android_gms_wearable_internal_zzfw.zzrf()).zza(new zzfl(this), this.zzbzR);
    }

    protected final /* synthetic */ Result zzb(Status status) {
        return new zzbs(status, null);
    }
}
