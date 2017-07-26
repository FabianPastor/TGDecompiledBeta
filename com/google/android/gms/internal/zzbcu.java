package com.google.android.gms.internal;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

final class zzbcu extends Handler {
    private /* synthetic */ zzbcp zzaDN;

    zzbcu(zzbcp com_google_android_gms_internal_zzbcp, Looper looper) {
        this.zzaDN = com_google_android_gms_internal_zzbcp;
        super(looper);
    }

    public final void handleMessage(Message message) {
        switch (message.what) {
            case 1:
                this.zzaDN.zzqd();
                return;
            case 2:
                this.zzaDN.resume();
                return;
            default:
                Log.w("GoogleApiClientImpl", "Unknown message id: " + message.what);
                return;
        }
    }
}
