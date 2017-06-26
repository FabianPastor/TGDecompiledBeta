package com.google.android.gms.wearable.internal;

import android.os.RemoteException;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.NodeApi.GetConnectedNodesResult;
import java.util.ArrayList;

final class zzeb extends zzn<GetConnectedNodesResult> {
    zzeb(zzdz com_google_android_gms_wearable_internal_zzdz, GoogleApiClient googleApiClient) {
        super(googleApiClient);
    }

    protected final /* synthetic */ void zza(zzb com_google_android_gms_common_api_Api_zzb) throws RemoteException {
        ((zzdn) ((zzfw) com_google_android_gms_common_api_Api_zzb).zzrf()).zzc(new zzfk(this));
    }

    protected final /* synthetic */ Result zzb(Status status) {
        return new zzee(status, new ArrayList());
    }
}
