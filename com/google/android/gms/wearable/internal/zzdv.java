package com.google.android.gms.wearable.internal;

import android.content.IntentFilter;
import android.os.RemoteException;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.internal.zzbaz;
import com.google.android.gms.internal.zzbdw;
import com.google.android.gms.wearable.MessageApi.MessageListener;

final class zzdv extends zzn<Status> {
    private zzbdw<MessageListener> zzaEU;
    private MessageListener zzbSV;
    private IntentFilter[] zzbSW;

    private zzdv(GoogleApiClient googleApiClient, MessageListener messageListener, zzbdw<MessageListener> com_google_android_gms_internal_zzbdw_com_google_android_gms_wearable_MessageApi_MessageListener, IntentFilter[] intentFilterArr) {
        super(googleApiClient);
        this.zzbSV = (MessageListener) zzbo.zzu(messageListener);
        this.zzaEU = (zzbdw) zzbo.zzu(com_google_android_gms_internal_zzbdw_com_google_android_gms_wearable_MessageApi_MessageListener);
        this.zzbSW = (IntentFilter[]) zzbo.zzu(intentFilterArr);
    }

    protected final /* synthetic */ void zza(zzb com_google_android_gms_common_api_Api_zzb) throws RemoteException {
        ((zzfw) com_google_android_gms_common_api_Api_zzb).zza((zzbaz) this, this.zzbSV, this.zzaEU, this.zzbSW);
        this.zzbSV = null;
        this.zzaEU = null;
        this.zzbSW = null;
    }

    public final /* synthetic */ Result zzb(Status status) {
        this.zzbSV = null;
        this.zzaEU = null;
        this.zzbSW = null;
        return status;
    }
}
