package com.google.android.gms.internal;

import android.support.annotation.NonNull;
import com.google.android.gms.auth.api.signin.internal.zzy;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

final class zzbct implements ResultCallback<Status> {
    private /* synthetic */ zzbcp zzaDN;
    private /* synthetic */ zzben zzaDP;
    private /* synthetic */ boolean zzaDQ;
    private /* synthetic */ GoogleApiClient zzaqW;

    zzbct(zzbcp com_google_android_gms_internal_zzbcp, zzben com_google_android_gms_internal_zzben, boolean z, GoogleApiClient googleApiClient) {
        this.zzaDN = com_google_android_gms_internal_zzbcp;
        this.zzaDP = com_google_android_gms_internal_zzben;
        this.zzaDQ = z;
        this.zzaqW = googleApiClient;
    }

    public final /* synthetic */ void onResult(@NonNull Result result) {
        Status status = (Status) result;
        zzy.zzaj(this.zzaDN.mContext).zzmP();
        if (status.isSuccess() && this.zzaDN.isConnected()) {
            this.zzaDN.reconnect();
        }
        this.zzaDP.setResult(status);
        if (this.zzaDQ) {
            this.zzaqW.disconnect();
        }
    }
}
