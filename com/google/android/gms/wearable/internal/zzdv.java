package com.google.android.gms.wearable.internal;

import android.content.IntentFilter;
import android.os.RemoteException;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.internal.zzbay;
import com.google.android.gms.internal.zzbdv;
import com.google.android.gms.wearable.MessageApi.MessageListener;

final class zzdv extends zzn<Status> {
    private zzbdv<MessageListener> zzaEU;
    private MessageListener zzbST;
    private IntentFilter[] zzbSU;

    private zzdv(GoogleApiClient googleApiClient, MessageListener messageListener, zzbdv<MessageListener> com_google_android_gms_internal_zzbdv_com_google_android_gms_wearable_MessageApi_MessageListener, IntentFilter[] intentFilterArr) {
        super(googleApiClient);
        this.zzbST = (MessageListener) zzbo.zzu(messageListener);
        this.zzaEU = (zzbdv) zzbo.zzu(com_google_android_gms_internal_zzbdv_com_google_android_gms_wearable_MessageApi_MessageListener);
        this.zzbSU = (IntentFilter[]) zzbo.zzu(intentFilterArr);
    }

    protected final /* synthetic */ void zza(zzb com_google_android_gms_common_api_Api_zzb) throws RemoteException {
        ((zzfw) com_google_android_gms_common_api_Api_zzb).zza((zzbay) this, this.zzbST, this.zzaEU, this.zzbSU);
        this.zzbST = null;
        this.zzaEU = null;
        this.zzbSU = null;
    }

    public final /* synthetic */ Result zzb(Status status) {
        this.zzbST = null;
        this.zzaEU = null;
        this.zzbSU = null;
        return status;
    }
}
