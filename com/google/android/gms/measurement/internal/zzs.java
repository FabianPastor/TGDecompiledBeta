package com.google.android.gms.measurement.internal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.MainThread;
import android.support.annotation.WorkerThread;
import com.google.android.gms.common.internal.zzaa;

class zzs extends BroadcastReceiver {
    static final String fx = zzs.class.getName();
    private final zzx aqw;
    private boolean fy;
    private boolean fz;

    zzs(zzx com_google_android_gms_measurement_internal_zzx) {
        zzaa.zzy(com_google_android_gms_measurement_internal_zzx);
        this.aqw = com_google_android_gms_measurement_internal_zzx;
    }

    private Context getContext() {
        return this.aqw.getContext();
    }

    private zzq zzbwb() {
        return this.aqw.zzbwb();
    }

    @WorkerThread
    public boolean isRegistered() {
        this.aqw.zzzx();
        return this.fy;
    }

    @MainThread
    public void onReceive(Context context, Intent intent) {
        this.aqw.zzacj();
        String action = intent.getAction();
        zzbwb().zzbxe().zzj("NetworkBroadcastReceiver received action", action);
        if ("android.net.conn.CONNECTIVITY_CHANGE".equals(action)) {
            final boolean zzagk = this.aqw.zzbxv().zzagk();
            if (this.fz != zzagk) {
                this.fz = zzagk;
                this.aqw.zzbwa().zzm(new Runnable(this) {
                    final /* synthetic */ zzs asW;

                    public void run() {
                        this.asW.aqw.zzaw(zzagk);
                    }
                });
                return;
            }
            return;
        }
        zzbwb().zzbxa().zzj("NetworkBroadcastReceiver received unknown action", action);
    }

    @WorkerThread
    public void unregister() {
        this.aqw.zzacj();
        this.aqw.zzzx();
        if (isRegistered()) {
            zzbwb().zzbxe().log("Unregistering connectivity change receiver");
            this.fy = false;
            this.fz = false;
            try {
                getContext().unregisterReceiver(this);
            } catch (IllegalArgumentException e) {
                zzbwb().zzbwy().zzj("Failed to unregister the network broadcast receiver", e);
            }
        }
    }

    @WorkerThread
    public void zzagh() {
        this.aqw.zzacj();
        this.aqw.zzzx();
        if (!this.fy) {
            getContext().registerReceiver(this, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
            this.fz = this.aqw.zzbxv().zzagk();
            zzbwb().zzbxe().zzj("Registering connectivity change receiver. Network connected", Boolean.valueOf(this.fz));
            this.fy = true;
        }
    }
}
