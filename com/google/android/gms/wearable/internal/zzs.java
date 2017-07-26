package com.google.android.gms.wearable.internal;

import android.os.RemoteException;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.CapabilityApi.RemoveLocalCapabilityResult;

final class zzs extends zzn<RemoveLocalCapabilityResult> {
    private /* synthetic */ String zzbRV;

    zzs(zzo com_google_android_gms_wearable_internal_zzo, GoogleApiClient googleApiClient, String str) {
        this.zzbRV = str;
        super(googleApiClient);
    }

    protected final /* synthetic */ void zza(zzb com_google_android_gms_common_api_Api_zzb) throws RemoteException {
        zzfw com_google_android_gms_wearable_internal_zzfw = (zzfw) com_google_android_gms_common_api_Api_zzb;
        ((zzdn) com_google_android_gms_wearable_internal_zzfw.zzrf()).zzb(new zzft(this), this.zzbRV);
    }

    protected final /* synthetic */ Result zzb(Status status) {
        return new zzu(status);
    }
}
