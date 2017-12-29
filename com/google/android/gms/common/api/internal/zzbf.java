package com.google.android.gms.common.api.internal;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

final class zzbf extends Handler {
    private /* synthetic */ zzba zzfsj;

    zzbf(zzba com_google_android_gms_common_api_internal_zzba, Looper looper) {
        this.zzfsj = com_google_android_gms_common_api_internal_zzba;
        super(looper);
    }

    public final void handleMessage(Message message) {
        switch (message.what) {
            case 1:
                this.zzfsj.zzaij();
                return;
            case 2:
                this.zzfsj.resume();
                return;
            default:
                Log.w("GoogleApiClientImpl", "Unknown message id: " + message.what);
                return;
        }
    }
}
