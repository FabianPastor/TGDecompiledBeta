package com.google.android.gms.wearable.internal;

import android.os.RemoteException;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.wearable.ChannelApi.ChannelListener;

final class zzag extends zzn<Status> {
    private final String zzakv;
    private ChannelListener zzbSe;

    zzag(GoogleApiClient googleApiClient, ChannelListener channelListener, String str) {
        super(googleApiClient);
        this.zzbSe = (ChannelListener) zzbo.zzu(channelListener);
        this.zzakv = str;
    }

    protected final /* synthetic */ void zza(zzb com_google_android_gms_common_api_Api_zzb) throws RemoteException {
        ((zzfw) com_google_android_gms_common_api_Api_zzb).zza(this, this.zzbSe, this.zzakv);
        this.zzbSe = null;
    }

    public final /* synthetic */ Result zzb(Status status) {
        this.zzbSe = null;
        return status;
    }
}