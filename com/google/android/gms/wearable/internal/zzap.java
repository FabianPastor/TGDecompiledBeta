package com.google.android.gms.wearable.internal;

import android.net.Uri;
import android.os.RemoteException;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.internal.zzbaz;

final class zzap extends zzn<Status> {
    private /* synthetic */ zzak zzbSk;
    private /* synthetic */ boolean zzbSl;
    private /* synthetic */ Uri zzbzR;

    zzap(zzak com_google_android_gms_wearable_internal_zzak, GoogleApiClient googleApiClient, Uri uri, boolean z) {
        this.zzbSk = com_google_android_gms_wearable_internal_zzak;
        this.zzbzR = uri;
        this.zzbSl = z;
        super(googleApiClient);
    }

    protected final /* synthetic */ void zza(zzb com_google_android_gms_common_api_Api_zzb) throws RemoteException {
        ((zzfw) com_google_android_gms_common_api_Api_zzb).zza((zzbaz) this, this.zzbSk.zzakv, this.zzbzR, this.zzbSl);
    }

    public final /* synthetic */ Result zzb(Status status) {
        return status;
    }
}
