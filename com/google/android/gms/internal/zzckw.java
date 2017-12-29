package com.google.android.gms.internal;

import android.content.ComponentName;

final class zzckw implements Runnable {
    private /* synthetic */ ComponentName val$name;
    private /* synthetic */ zzcku zzjit;

    zzckw(zzcku com_google_android_gms_internal_zzcku, ComponentName componentName) {
        this.zzjit = com_google_android_gms_internal_zzcku;
        this.val$name = componentName;
    }

    public final void run() {
        this.zzjit.zzjij.onServiceDisconnected(this.val$name);
    }
}
