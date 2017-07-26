package com.google.android.gms.internal;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.google.android.gms.common.internal.zzbo;

final class zzbdx extends Handler {
    private /* synthetic */ zzbdw zzaEO;

    public zzbdx(zzbdw com_google_android_gms_internal_zzbdw, Looper looper) {
        this.zzaEO = com_google_android_gms_internal_zzbdw;
        super(looper);
    }

    public final void handleMessage(Message message) {
        boolean z = true;
        if (message.what != 1) {
            z = false;
        }
        zzbo.zzaf(z);
        this.zzaEO.zzb((zzbdz) message.obj);
    }
}
