package com.google.android.gms.wearable.internal;

import android.os.RemoteException;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;

final class zzam extends zzn<Status> {
    private /* synthetic */ int zzKe;
    private /* synthetic */ zzak zzbSi;

    zzam(zzak com_google_android_gms_wearable_internal_zzak, GoogleApiClient googleApiClient, int i) {
        this.zzbSi = com_google_android_gms_wearable_internal_zzak;
        this.zzKe = i;
        super(googleApiClient);
    }

    protected final /* synthetic */ void zza(zzb com_google_android_gms_common_api_Api_zzb) throws RemoteException {
        zzfw com_google_android_gms_wearable_internal_zzfw = (zzfw) com_google_android_gms_common_api_Api_zzb;
        ((zzdn) com_google_android_gms_wearable_internal_zzfw.zzrf()).zzb(new zzfe(this), this.zzbSi.zzakv, this.zzKe);
    }

    protected final /* synthetic */ Result zzb(Status status) {
        return status;
    }
}
