package com.google.android.gms.wearable.internal;

import android.os.RemoteException;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.ChannelApi.OpenChannelResult;

final class zzad extends zzn<OpenChannelResult> {
    private /* synthetic */ String zzKU;
    private /* synthetic */ String zzbSc;

    zzad(zzac com_google_android_gms_wearable_internal_zzac, GoogleApiClient googleApiClient, String str, String str2) {
        this.zzbSc = str;
        this.zzKU = str2;
        super(googleApiClient);
    }

    protected final /* synthetic */ void zza(zzb com_google_android_gms_common_api_Api_zzb) throws RemoteException {
        zzfw com_google_android_gms_wearable_internal_zzfw = (zzfw) com_google_android_gms_common_api_Api_zzb;
        ((zzdn) com_google_android_gms_wearable_internal_zzfw.zzrf()).zza(new zzfq(this), this.zzbSc, this.zzKU);
    }

    public final /* synthetic */ Result zzb(Status status) {
        return new zzaf(status, null);
    }
}
