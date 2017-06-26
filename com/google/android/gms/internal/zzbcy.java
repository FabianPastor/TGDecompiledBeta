package com.google.android.gms.internal;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

final class zzbcy extends Handler {
    private /* synthetic */ zzbcw zzaEa;

    zzbcy(zzbcw com_google_android_gms_internal_zzbcw, Looper looper) {
        this.zzaEa = com_google_android_gms_internal_zzbcw;
        super(looper);
    }

    public final void handleMessage(Message message) {
        switch (message.what) {
            case 1:
                ((zzbcx) message.obj).zzc(this.zzaEa);
                return;
            case 2:
                throw ((RuntimeException) message.obj);
            default:
                Log.w("GACStateManager", "Unknown message id: " + message.what);
                return;
        }
    }
}
