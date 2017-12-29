package com.google.android.gms.iid;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

final class zzm extends Handler {
    private /* synthetic */ zzl zzigd;

    zzm(zzl com_google_android_gms_iid_zzl, Looper looper) {
        this.zzigd = com_google_android_gms_iid_zzl;
        super(looper);
    }

    public final void handleMessage(Message message) {
        this.zzigd.zzc(message);
    }
}
