package com.google.android.gms.wearable.internal;

import android.os.RemoteException;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.CapabilityApi.GetCapabilityResult;

final class zzp extends zzn<GetCapabilityResult> {
    private /* synthetic */ String zzbRT;
    private /* synthetic */ int zzbRU;

    zzp(zzo com_google_android_gms_wearable_internal_zzo, GoogleApiClient googleApiClient, String str, int i) {
        this.zzbRT = str;
        this.zzbRU = i;
        super(googleApiClient);
    }

    protected final /* synthetic */ void zza(zzb com_google_android_gms_common_api_Api_zzb) throws RemoteException {
        zzfw com_google_android_gms_wearable_internal_zzfw = (zzfw) com_google_android_gms_common_api_Api_zzb;
        ((zzdn) com_google_android_gms_wearable_internal_zzfw.zzrf()).zza(new zzfh(this), this.zzbRT, this.zzbRU);
    }

    protected final /* synthetic */ Result zzb(Status status) {
        return new zzy(status, null);
    }
}