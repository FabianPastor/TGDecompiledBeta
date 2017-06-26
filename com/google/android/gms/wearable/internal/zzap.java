package com.google.android.gms.wearable.internal;

import android.net.Uri;
import android.os.RemoteException;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.internal.zzbay;

final class zzap extends zzn<Status> {
    private /* synthetic */ zzak zzbSi;
    private /* synthetic */ boolean zzbSj;
    private /* synthetic */ Uri zzbzR;

    zzap(zzak com_google_android_gms_wearable_internal_zzak, GoogleApiClient googleApiClient, Uri uri, boolean z) {
        this.zzbSi = com_google_android_gms_wearable_internal_zzak;
        this.zzbzR = uri;
        this.zzbSj = z;
        super(googleApiClient);
    }

    protected final /* synthetic */ void zza(zzb com_google_android_gms_common_api_Api_zzb) throws RemoteException {
        ((zzfw) com_google_android_gms_common_api_Api_zzb).zza((zzbay) this, this.zzbSi.zzakv, this.zzbzR, this.zzbSj);
    }

    public final /* synthetic */ Result zzb(Status status) {
        return status;
    }
}
