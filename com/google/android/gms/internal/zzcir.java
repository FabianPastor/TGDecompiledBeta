package com.google.android.gms.internal;

import android.content.ComponentName;

final class zzcir implements Runnable {
    private /* synthetic */ ComponentName val$name;
    private /* synthetic */ zzcip zzbuk;

    zzcir(zzcip com_google_android_gms_internal_zzcip, ComponentName componentName) {
        this.zzbuk = com_google_android_gms_internal_zzcip;
        this.val$name = componentName;
    }

    public final void run() {
        this.zzbuk.zzbua.onServiceDisconnected(this.val$name);
    }
}
