package com.google.android.gms.internal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public final class zzqv extends BroadcastReceiver {
    protected Context mContext;
    private final zza yO;

    public static abstract class zza {
        public abstract void zzaqp();
    }

    public zzqv(zza com_google_android_gms_internal_zzqv_zza) {
        this.yO = com_google_android_gms_internal_zzqv_zza;
    }

    public void onReceive(Context context, Intent intent) {
        Uri data = intent.getData();
        Object obj = null;
        if (data != null) {
            obj = data.getSchemeSpecificPart();
        }
        if ("com.google.android.gms".equals(obj)) {
            this.yO.zzaqp();
            unregister();
        }
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

    public synchronized void unregister() {
        if (this.mContext != null) {
            this.mContext.unregisterReceiver(this);
        }
        this.mContext = null;
    }
}
