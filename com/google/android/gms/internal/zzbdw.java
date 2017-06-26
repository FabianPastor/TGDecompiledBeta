package com.google.android.gms.internal;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.google.android.gms.common.internal.zzbo;

final class zzbdw extends Handler {
    private /* synthetic */ zzbdv zzaEO;

    public zzbdw(zzbdv com_google_android_gms_internal_zzbdv, Looper looper) {
        this.zzaEO = com_google_android_gms_internal_zzbdv;
        super(looper);
    }

    public final void handleMessage(Message message) {
        boolean z = true;
        if (message.what != 1) {
            z = false;
        }
        zzbo.zzaf(z);
        this.zzaEO.zzb((zzbdy) message.obj);
    }
}
