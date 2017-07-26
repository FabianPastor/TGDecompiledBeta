package com.google.android.gms.wearable.internal;

import android.os.RemoteException;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;

final class zzal extends zzn<Status> {
    private /* synthetic */ zzak zzbSk;

    zzal(zzak com_google_android_gms_wearable_internal_zzak, GoogleApiClient googleApiClient) {
        this.zzbSk = com_google_android_gms_wearable_internal_zzak;
        super(googleApiClient);
    }

    protected final /* synthetic */ void zza(zzb com_google_android_gms_common_api_Api_zzb) throws RemoteException {
        zzfw com_google_android_gms_wearable_internal_zzfw = (zzfw) com_google_android_gms_common_api_Api_zzb;
        ((zzdn) com_google_android_gms_wearable_internal_zzfw.zzrf()).zzc(new zzfd(this), this.zzbSk.zzakv);
    }

    protected final /* synthetic */ Result zzb(Status status) {
        return status;
    }
}
