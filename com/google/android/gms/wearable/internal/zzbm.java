package com.google.android.gms.wearable.internal;

import android.net.Uri;
import android.os.RemoteException;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.wearable.DataItemBuffer;

final class zzbm extends zzn<DataItemBuffer> {
    private /* synthetic */ int zzbSx;
    private /* synthetic */ Uri zzbzR;

    zzbm(zzbi com_google_android_gms_wearable_internal_zzbi, GoogleApiClient googleApiClient, Uri uri, int i) {
        this.zzbzR = uri;
        this.zzbSx = i;
        super(googleApiClient);
    }

    protected final /* synthetic */ void zza(zzb com_google_android_gms_common_api_Api_zzb) throws RemoteException {
        zzfw com_google_android_gms_wearable_internal_zzfw = (zzfw) com_google_android_gms_common_api_Api_zzb;
        ((zzdn) com_google_android_gms_wearable_internal_zzfw.zzrf()).zza(new zzfm(this), this.zzbzR, this.zzbSx);
    }

    protected final /* synthetic */ Result zzb(Status status) {
        return new DataItemBuffer(DataHolder.zzau(status.getStatusCode()));
    }
}
