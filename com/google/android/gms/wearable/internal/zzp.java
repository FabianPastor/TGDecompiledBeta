package com.google.android.gms.wearable.internal;

import android.os.RemoteException;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.CapabilityApi.GetCapabilityResult;

final class zzp extends zzn<GetCapabilityResult> {
    private /* synthetic */ String zzlij;
    private /* synthetic */ int zzlik;

    zzp(zzo com_google_android_gms_wearable_internal_zzo, GoogleApiClient googleApiClient, String str, int i) {
        this.zzlij = str;
        this.zzlik = i;
        super(googleApiClient);
    }

    protected final /* synthetic */ void zza(zzb com_google_android_gms_common_api_Api_zzb) throws RemoteException {
        zzhg com_google_android_gms_wearable_internal_zzhg = (zzhg) com_google_android_gms_common_api_Api_zzb;
        ((zzep) com_google_android_gms_wearable_internal_zzhg.zzakn()).zza(new zzgr(this), this.zzlij, this.zzlik);
    }

    protected final /* synthetic */ Result zzb(Status status) {
        return new zzy(status, null);
    }
}
