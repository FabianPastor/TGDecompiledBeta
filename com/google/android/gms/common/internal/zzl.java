package com.google.android.gms.common.internal;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.IInterface;

public final class zzl implements ServiceConnection {
    private /* synthetic */ zzd zzfza;
    private final int zzfzd;

    public zzl(zzd com_google_android_gms_common_internal_zzd, int i) {
        this.zzfza = com_google_android_gms_common_internal_zzd;
        this.zzfzd = i;
    }

    public final void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        if (iBinder == null) {
            this.zzfza.zzcf(16);
            return;
        }
        synchronized (this.zzfza.zzfyk) {
            zzay com_google_android_gms_common_internal_zzay;
            zzd com_google_android_gms_common_internal_zzd = this.zzfza;
            if (iBinder == null) {
                com_google_android_gms_common_internal_zzay = null;
            } else {
                IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.common.internal.IGmsServiceBroker");
                com_google_android_gms_common_internal_zzay = (queryLocalInterface == null || !(queryLocalInterface instanceof zzay)) ? new zzaz(iBinder) : (zzay) queryLocalInterface;
            }
            com_google_android_gms_common_internal_zzd.zzfyl = com_google_android_gms_common_internal_zzay;
        }
        this.zzfza.zza(0, null, this.zzfzd);
    }

    public final void onServiceDisconnected(ComponentName componentName) {
        synchronized (this.zzfza.zzfyk) {
            this.zzfza.zzfyl = null;
        }
        this.zzfza.mHandler.sendMessage(this.zzfza.mHandler.obtainMessage(6, this.zzfzd, 1));
    }
}
