package com.google.android.gms.wearable.internal;

import android.os.RemoteException;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;

final class zzaz extends zzn<Status> {
    private /* synthetic */ zzay zzljf;

    zzaz(zzay com_google_android_gms_wearable_internal_zzay, GoogleApiClient googleApiClient) {
        this.zzljf = com_google_android_gms_wearable_internal_zzay;
        super(googleApiClient);
    }

    protected final /* synthetic */ void zza(zzb com_google_android_gms_common_api_Api_zzb) throws RemoteException {
        zzhg com_google_android_gms_wearable_internal_zzhg = (zzhg) com_google_android_gms_common_api_Api_zzb;
        ((zzep) com_google_android_gms_wearable_internal_zzhg.zzakn()).zzc(new zzgn(this), this.zzljf.zzecl);
    }

    protected final /* synthetic */ Result zzb(Status status) {
        return status;
    }
}
