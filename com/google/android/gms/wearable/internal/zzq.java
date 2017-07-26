package com.google.android.gms.wearable.internal;

import android.os.RemoteException;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.CapabilityApi.GetAllCapabilitiesResult;

final class zzq extends zzn<GetAllCapabilitiesResult> {
    private /* synthetic */ int zzbRW;

    zzq(zzo com_google_android_gms_wearable_internal_zzo, GoogleApiClient googleApiClient, int i) {
        this.zzbRW = i;
        super(googleApiClient);
    }

    protected final /* synthetic */ void zza(zzb com_google_android_gms_common_api_Api_zzb) throws RemoteException {
        zzfw com_google_android_gms_wearable_internal_zzfw = (zzfw) com_google_android_gms_common_api_Api_zzb;
        ((zzdn) com_google_android_gms_wearable_internal_zzfw.zzrf()).zza(new zzfg(this), this.zzbRW);
    }

    protected final /* synthetic */ Result zzb(Status status) {
        return new zzx(status, null);
    }
}
