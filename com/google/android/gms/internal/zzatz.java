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
    private final zzaue zzbqb;

    zzatz(zzaue com_google_android_gms_internal_zzaue) {
        zzac.zzw(com_google_android_gms_internal_zzaue);
        this.zzbqb = com_google_android_gms_internal_zzaue;
    }

    private Context getContext() {
        return this.zzbqb.getContext();
    }

    private zzatx zzKl() {
        return this.zzbqb.zzKl();
    }

    @WorkerThread
    public boolean isRegistered() {
        this.zzbqb.zzmR();
        return this.zzagw;
    }

    @MainThread
    public void onReceive(Context context, Intent intent) {
        this.zzbqb.zzob();
        String action = intent.getAction();
        zzKl().zzMf().zzj("NetworkBroadcastReceiver received action", action);
        if ("android.net.conn.CONNECTIVITY_CHANGE".equals(action)) {
            final boolean zzqa = this.zzbqb.zzMz().zzqa();
            if (this.zzagx != zzqa) {
                this.zzagx = zzqa;
                this.zzbqb.zzKk().zzm(new Runnable(this) {
                    final /* synthetic */ zzatz zzbsY;

                    public void run() {
                        this.zzbsY.zzbqb.zzV(zzqa);
                    }
                });
                return;
            }
            return;
        }
        zzKl().zzMb().zzj("NetworkBroadcastReceiver received unknown action", action);
    }

    @WorkerThread
    public void unregister() {
        this.zzbqb.zzob();
        this.zzbqb.zzmR();
        if (isRegistered()) {
            zzKl().zzMf().log("Unregistering connectivity change receiver");
            this.zzagw = false;
            this.zzagx = false;
            try {
                getContext().unregisterReceiver(this);
            } catch (IllegalArgumentException e) {
                zzKl().zzLZ().zzj("Failed to unregister the network broadcast receiver", e);
            }
        }
    }

    @WorkerThread
    public void zzpX() {
        this.zzbqb.zzob();
        this.zzbqb.zzmR();
        if (!this.zzagw) {
            getContext().registerReceiver(this, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
            this.zzagx = this.zzbqb.zzMz().zzqa();
            zzKl().zzMf().zzj("Registering connectivity change receiver. Network connected", Boolean.valueOf(this.zzagx));
            this.zzagw = true;
        }
    }
}
