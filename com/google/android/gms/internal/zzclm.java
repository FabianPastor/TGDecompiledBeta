package com.google.android.gms.internal;

import android.content.Intent;

final class zzclm extends zzcgs {
    private /* synthetic */ zzcll zzjjh;

    zzclm(zzcll com_google_android_gms_internal_zzcll, zzcim com_google_android_gms_internal_zzcim) {
        this.zzjjh = com_google_android_gms_internal_zzcll;
        super(com_google_android_gms_internal_zzcim);
    }

    public final void run() {
        this.zzjjh.cancel();
        this.zzjjh.zzawy().zzazj().log("Sending upload intent from DelayedRunnable");
        Intent className = new Intent().setClassName(this.zzjjh.getContext(), "com.google.android.gms.measurement.AppMeasurementReceiver");
        className.setAction("com.google.android.gms.measurement.UPLOAD");
        this.zzjjh.getContext().sendBroadcast(className);
    }
}
