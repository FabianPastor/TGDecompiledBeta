package com.google.android.gms.internal;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

final class zzbcz extends Handler {
    private /* synthetic */ zzbcx zzaEa;

    zzbcz(zzbcx com_google_android_gms_internal_zzbcx, Looper looper) {
        this.zzaEa = com_google_android_gms_internal_zzbcx;
        super(looper);
    }

    public final void handleMessage(Message message) {
        switch (message.what) {
            case 1:
                ((zzbcy) message.obj).zzc(this.zzaEa);
                return;
            case 2:
                throw ((RuntimeException) message.obj);
            default:
                Log.w("GACStateManager", "Unknown message id: " + message.what);
                return;
        }
    }
}
