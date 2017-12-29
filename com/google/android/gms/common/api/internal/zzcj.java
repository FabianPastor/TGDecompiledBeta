package com.google.android.gms.common.api.internal;

import android.os.Handler;
import android.os.Message;
import com.google.android.gms.common.internal.zzbq;

final class zzcj extends Handler {
    private /* synthetic */ zzci zzfum;

    public final void handleMessage(Message message) {
        boolean z = true;
        if (message.what != 1) {
            z = false;
        }
        zzbq.checkArgument(z);
        this.zzfum.zzb((zzcl) message.obj);
    }
}
