package com.google.android.gms.wearable.internal;

import android.os.RemoteException;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.Channel.GetOutputStreamResult;

final class zzbc extends zzn<GetOutputStreamResult> {
    private /* synthetic */ zzay zzljf;

    zzbc(zzay com_google_android_gms_wearable_internal_zzay, GoogleApiClient googleApiClient) {
        this.zzljf = com_google_android_gms_wearable_internal_zzay;
        super(googleApiClient);
    }

    protected final /* synthetic */ void zza(zzb com_google_android_gms_common_api_Api_zzb) throws RemoteException {
        zzhg com_google_android_gms_wearable_internal_zzhg = (zzhg) com_google_android_gms_common_api_Api_zzb;
        String zza = this.zzljf.zzecl;
        zzei com_google_android_gms_wearable_internal_zzbr = new zzbr();
        ((zzep) com_google_android_gms_wearable_internal_zzhg.zzakn()).zzb(new zzgt(this, com_google_android_gms_wearable_internal_zzbr), com_google_android_gms_wearable_internal_zzbr, zza);
    }

    public final /* synthetic */ Result zzb(Status status) {
        return new zzbh(status, null);
    }
}
