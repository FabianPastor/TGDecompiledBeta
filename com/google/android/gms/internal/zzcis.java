package com.google.android.gms.internal;

import android.content.ComponentName;

final class zzcis implements Runnable {
    private /* synthetic */ ComponentName val$name;
    private /* synthetic */ zzciq zzbuk;

    zzcis(zzciq com_google_android_gms_internal_zzciq, ComponentName componentName) {
        this.zzbuk = com_google_android_gms_internal_zzciq;
        this.val$name = componentName;
    }

    public final void run() {
        this.zzbuk.zzbua.onServiceDisconnected(this.val$name);
    }
}
