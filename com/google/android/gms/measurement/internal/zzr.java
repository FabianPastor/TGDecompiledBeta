package com.google.android.gms.measurement.internal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.MainThread;
import android.support.annotation.WorkerThread;
import com.google.android.gms.common.internal.zzac;

class zzr extends BroadcastReceiver {
    static final String dq = zzr.class.getName();
    private final zzx anq;
    private boolean dr;
    private boolean ds;

    zzr(zzx com_google_android_gms_measurement_internal_zzx) {
        zzac.zzy(com_google_android_gms_measurement_internal_zzx);
        this.anq = com_google_android_gms_measurement_internal_zzx;
    }

    private Context getContext() {
        return this.anq.getContext();
    }

    private zzp zzbvg() {
        return this.anq.zzbvg();
    }

    @WorkerThread
    public boolean isRegistered() {
        this.anq.zzyl();
        return this.dr;
    }

    @MainThread
    public void onReceive(Context context, Intent intent) {
        this.anq.zzaax();
        String action = intent.getAction();
        zzbvg().zzbwj().zzj("NetworkBroadcastReceiver received action", action);
        if ("android.net.conn.CONNECTIVITY_CHANGE".equals(action)) {
            final boolean zzafa = this.anq.zzbxa().zzafa();
            if (this.ds != zzafa) {
                this.ds = zzafa;
                this.anq.zzbvf().zzm(new Runnable(this) {
                    final /* synthetic */ zzr apI;

                    public void run() {
                        this.apI.anq.zzav(zzafa);
                    }
                });
                return;
            }
            return;
        }
        zzbvg().zzbwe().zzj("NetworkBroadcastReceiver received unknown action", action);
    }

    @WorkerThread
    public void unregister() {
        this.anq.zzaax();
        this.anq.zzyl();
        if (isRegistered()) {
            zzbvg().zzbwj().log("Unregistering connectivity change receiver");
            this.dr = false;
            this.ds = false;
            try {
                getContext().unregisterReceiver(this);
            } catch (IllegalArgumentException e) {
                zzbvg().zzbwc().zzj("Failed to unregister the network broadcast receiver", e);
            }
        }
    }

    @WorkerThread
    public void zzaex() {
        this.anq.zzaax();
        this.anq.zzyl();
        if (!this.dr) {
            getContext().registerReceiver(this, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
            this.ds = this.anq.zzbxa().zzafa();
            zzbvg().zzbwj().zzj("Registering connectivity change receiver. Network connected", Boolean.valueOf(this.ds));
            this.dr = true;
        }
    }
}
