package com.google.android.gms.internal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.MainThread;
import android.support.annotation.WorkerThread;
import com.google.android.gms.common.internal.zzac;

class zzatz extends BroadcastReceiver {
    static final String zzagv = zzatz.class.getName();
    private boolean zzagw;
    private boolean zzagx;
    private final zzaue zzbqg;

    zzatz(zzaue com_google_android_gms_internal_zzaue) {
        zzac.zzw(com_google_android_gms_internal_zzaue);
        this.zzbqg = com_google_android_gms_internal_zzaue;
    }

    private Context getContext() {
        return this.zzbqg.getContext();
    }

    private zzatx zzKk() {
        return this.zzbqg.zzKk();
    }

    @WorkerThread
    public boolean isRegistered() {
        this.zzbqg.zzmR();
        return this.zzagw;
    }

    @MainThread
    public void onReceive(Context context, Intent intent) {
        this.zzbqg.zzob();
        String action = intent.getAction();
        zzKk().zzMd().zzj("NetworkBroadcastReceiver received action", action);
        if ("android.net.conn.CONNECTIVITY_CHANGE".equals(action)) {
            final boolean zzqa = this.zzbqg.zzMx().zzqa();
            if (this.zzagx != zzqa) {
                this.zzagx = zzqa;
                this.zzbqg.zzKj().zzm(new Runnable(this) {
                    final /* synthetic */ zzatz zzbta;

                    public void run() {
                        this.zzbta.zzbqg.zzW(zzqa);
                    }
                });
                return;
            }
            return;
        }
        zzKk().zzLZ().zzj("NetworkBroadcastReceiver received unknown action", action);
    }

    @WorkerThread
    public void unregister() {
        this.zzbqg.zzob();
        this.zzbqg.zzmR();
        if (isRegistered()) {
            zzKk().zzMd().log("Unregistering connectivity change receiver");
            this.zzagw = false;
            this.zzagx = false;
            try {
                getContext().unregisterReceiver(this);
            } catch (IllegalArgumentException e) {
                zzKk().zzLX().zzj("Failed to unregister the network broadcast receiver", e);
            }
        }
    }

    @WorkerThread
    public void zzpX() {
        this.zzbqg.zzob();
        this.zzbqg.zzmR();
        if (!this.zzagw) {
            getContext().registerReceiver(this, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
            this.zzagx = this.zzbqg.zzMx().zzqa();
            zzKk().zzMd().zzj("Registering connectivity change receiver. Network connected", Boolean.valueOf(this.zzagx));
            this.zzagw = true;
        }
    }
}
