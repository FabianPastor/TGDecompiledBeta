package com.google.android.gms.wearable.internal;

import android.os.RemoteException;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.MessageApi.SendMessageResult;

final class zzev extends zzn<SendMessageResult> {
    private /* synthetic */ String val$action;
    private /* synthetic */ byte[] zzkrx;
    private /* synthetic */ String zzliv;

    zzev(zzeu com_google_android_gms_wearable_internal_zzeu, GoogleApiClient googleApiClient, String str, String str2, byte[] bArr) {
        this.zzliv = str;
        this.val$action = str2;
        this.zzkrx = bArr;
        super(googleApiClient);
    }

    protected final /* synthetic */ void zza(zzb com_google_android_gms_common_api_Api_zzb) throws RemoteException {
        zzhg com_google_android_gms_wearable_internal_zzhg = (zzhg) com_google_android_gms_common_api_Api_zzb;
        ((zzep) com_google_android_gms_wearable_internal_zzhg.zzakn()).zza(new zzhe(this), this.zzliv, this.val$action, this.zzkrx);
    }

    protected final /* synthetic */ Result zzb(Status status) {
        return new zzey(status, -1);
    }
}
