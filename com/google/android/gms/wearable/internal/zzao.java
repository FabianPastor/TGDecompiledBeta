package com.google.android.gms.wearable.internal;

import android.os.RemoteException;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.Channel.GetOutputStreamResult;

final class zzao extends zzn<GetOutputStreamResult> {
    private /* synthetic */ zzak zzbSk;

    zzao(zzak com_google_android_gms_wearable_internal_zzak, GoogleApiClient googleApiClient) {
        this.zzbSk = com_google_android_gms_wearable_internal_zzak;
        super(googleApiClient);
    }

    protected final /* synthetic */ void zza(zzb com_google_android_gms_common_api_Api_zzb) throws RemoteException {
        zzfw com_google_android_gms_wearable_internal_zzfw = (zzfw) com_google_android_gms_common_api_Api_zzb;
        String zza = this.zzbSk.zzakv;
        zzdg com_google_android_gms_wearable_internal_zzbd = new zzbd();
        ((zzdn) com_google_android_gms_wearable_internal_zzfw.zzrf()).zzb(new zzfj(this, com_google_android_gms_wearable_internal_zzbd), com_google_android_gms_wearable_internal_zzbd, zza);
    }

    public final /* synthetic */ Result zzb(Status status) {
        return new zzat(status, null);
    }
}
