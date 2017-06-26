package com.google.android.gms.common.internal;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.IInterface;

public final class zzl implements ServiceConnection {
    private /* synthetic */ zzd zzaHe;
    private final int zzaHh;

    public zzl(zzd com_google_android_gms_common_internal_zzd, int i) {
        this.zzaHe = com_google_android_gms_common_internal_zzd;
        this.zzaHh = i;
    }

    public final void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        if (iBinder == null) {
            this.zzaHe.zzaz(16);
            return;
        }
        synchronized (this.zzaHe.zzaGO) {
            zzaw com_google_android_gms_common_internal_zzaw;
            zzd com_google_android_gms_common_internal_zzd = this.zzaHe;
            if (iBinder == null) {
                com_google_android_gms_common_internal_zzaw = null;
            } else {
                IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.common.internal.IGmsServiceBroker");
                com_google_android_gms_common_internal_zzaw = (queryLocalInterface == null || !(queryLocalInterface instanceof zzaw)) ? new zzax(iBinder) : (zzaw) queryLocalInterface;
            }
            com_google_android_gms_common_internal_zzd.zzaGP = com_google_android_gms_common_internal_zzaw;
        }
        this.zzaHe.zza(0, null, this.zzaHh);
    }

    public final void onServiceDisconnected(ComponentName componentName) {
        synchronized (this.zzaHe.zzaGO) {
            this.zzaHe.zzaGP = null;
        }
        this.zzaHe.mHandler.sendMessage(this.zzaHe.mHandler.obtainMessage(6, this.zzaHh, 1));
    }
}
