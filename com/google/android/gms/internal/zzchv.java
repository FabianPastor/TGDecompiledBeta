package com.google.android.gms.internal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.google.android.gms.common.internal.zzbq;

class zzchv extends BroadcastReceiver {
    private static String zzdyg = zzchv.class.getName();
    private boolean mRegistered;
    private boolean zzdyh;
    private final zzcim zziwf;

    zzchv(zzcim com_google_android_gms_internal_zzcim) {
        zzbq.checkNotNull(com_google_android_gms_internal_zzcim);
        this.zziwf = com_google_android_gms_internal_zzcim;
    }

    public void onReceive(Context context, Intent intent) {
        this.zziwf.zzxf();
        String action = intent.getAction();
        this.zziwf.zzawy().zzazj().zzj("NetworkBroadcastReceiver received action", action);
        if ("android.net.conn.CONNECTIVITY_CHANGE".equals(action)) {
            boolean zzzs = this.zziwf.zzbab().zzzs();
            if (this.zzdyh != zzzs) {
                this.zzdyh = zzzs;
                this.zziwf.zzawx().zzg(new zzchw(this, zzzs));
                return;
            }
            return;
        }
        this.zziwf.zzawy().zzazf().zzj("NetworkBroadcastReceiver received unknown action", action);
    }

    public final void unregister() {
        this.zziwf.zzxf();
        this.zziwf.zzawx().zzve();
        this.zziwf.zzawx().zzve();
        if (this.mRegistered) {
            this.zziwf.zzawy().zzazj().log("Unregistering connectivity change receiver");
            this.mRegistered = false;
            this.zzdyh = false;
            try {
                this.zziwf.getContext().unregisterReceiver(this);
            } catch (IllegalArgumentException e) {
                this.zziwf.zzawy().zzazd().zzj("Failed to unregister the network broadcast receiver", e);
            }
        }
    }

    public final void zzzp() {
        this.zziwf.zzxf();
        this.zziwf.zzawx().zzve();
        if (!this.mRegistered) {
            this.zziwf.getContext().registerReceiver(this, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
            this.zzdyh = this.zziwf.zzbab().zzzs();
            this.zziwf.zzawy().zzazj().zzj("Registering connectivity change receiver. Network connected", Boolean.valueOf(this.zzdyh));
            this.mRegistered = true;
        }
    }
}
