package com.google.android.gms.wearable.internal;

import android.os.RemoteException;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.MessageApi.SendMessageResult;

final class zzdt extends zzn<SendMessageResult> {
    private /* synthetic */ byte[] zzbKO;
    private /* synthetic */ String zzbSR;
    private /* synthetic */ String zzbSc;

    zzdt(zzds com_google_android_gms_wearable_internal_zzds, GoogleApiClient googleApiClient, String str, String str2, byte[] bArr) {
        this.zzbSc = str;
        this.zzbSR = str2;
        this.zzbKO = bArr;
        super(googleApiClient);
    }

    protected final /* synthetic */ void zza(zzb com_google_android_gms_common_api_Api_zzb) throws RemoteException {
        zzfw com_google_android_gms_wearable_internal_zzfw = (zzfw) com_google_android_gms_common_api_Api_zzb;
        ((zzdn) com_google_android_gms_wearable_internal_zzfw.zzrf()).zza(new zzfu(this), this.zzbSc, this.zzbSR, this.zzbKO);
    }

    protected final /* synthetic */ Result zzb(Status status) {
        return new zzdw(status, -1);
    }
}