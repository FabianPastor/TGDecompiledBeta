package com.google.android.gms.iid;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

final class zzf extends Handler {
    private /* synthetic */ zze zzbhn;

    zzf(zze com_google_android_gms_iid_zze, Looper looper) {
        this.zzbhn = com_google_android_gms_iid_zze;
        super(looper);
    }

    public final void handleMessage(Message message) {
        this.zzbhn.zzc(message);
    }
}
