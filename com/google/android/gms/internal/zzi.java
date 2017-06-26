package com.google.android.gms.internal;

import android.os.Handler;
import java.util.concurrent.Executor;

final class zzi implements Executor {
    private /* synthetic */ Handler zzs;

    zzi(zzh com_google_android_gms_internal_zzh, Handler handler) {
        this.zzs = handler;
    }

    public final void execute(Runnable runnable) {
        this.zzs.post(runnable);
    }
}
