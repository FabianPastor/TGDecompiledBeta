package com.google.android.gms.iid;

import android.content.BroadcastReceiver.PendingResult;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import com.google.android.gms.common.stats.zza;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public final class zzh implements ServiceConnection {
    private final Context zzair;
    private final Intent zzifb;
    private final ScheduledExecutorService zzifc;
    private final Queue<zzd> zzifd;
    private zzf zzife;
    private boolean zziff;

    public zzh(Context context, String str) {
        this(context, str, new ScheduledThreadPoolExecutor(0));
    }

    private zzh(Context context, String str, ScheduledExecutorService scheduledExecutorService) {
        this.zzifd = new LinkedList();
        this.zziff = false;
        this.zzair = context.getApplicationContext();
        this.zzifb = new Intent(str).setPackage(this.zzair.getPackageName());
        this.zzifc = scheduledExecutorService;
    }

    private final synchronized void zzavd() {
        if (Log.isLoggable("EnhancedIntentService", 3)) {
            Log.d("EnhancedIntentService", "flush queue called");
        }
        while (!this.zzifd.isEmpty()) {
            if (Log.isLoggable("EnhancedIntentService", 3)) {
                Log.d("EnhancedIntentService", "found intent to be delivered");
            }
            if (this.zzife == null || !this.zzife.isBinderAlive()) {
                if (Log.isLoggable("EnhancedIntentService", 3)) {
                    Log.d("EnhancedIntentService", "binder is dead. start connection? " + (!this.zziff));
                }
                if (!this.zziff) {
                    this.zziff = true;
                    try {
                        if (!zza.zzamc().zza(this.zzair, this.zzifb, this, 65)) {
                            Log.e("EnhancedIntentService", "binding to the service failed");
                            while (!this.zzifd.isEmpty()) {
                                ((zzd) this.zzifd.poll()).finish();
                            }
                        }
                    } catch (Throwable e) {
                        Log.e("EnhancedIntentService", "Exception while binding the service", e);
                    }
                }
            } else {
                if (Log.isLoggable("EnhancedIntentService", 3)) {
                    Log.d("EnhancedIntentService", "binder is alive, sending the intent.");
                }
                this.zzife.zza((zzd) this.zzifd.poll());
            }
        }
    }

    public final void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        synchronized (this) {
            this.zziff = false;
            this.zzife = (zzf) iBinder;
            if (Log.isLoggable("EnhancedIntentService", 3)) {
                String valueOf = String.valueOf(componentName);
                Log.d("EnhancedIntentService", new StringBuilder(String.valueOf(valueOf).length() + 20).append("onServiceConnected: ").append(valueOf).toString());
            }
            zzavd();
        }
    }

    public final void onServiceDisconnected(ComponentName componentName) {
        if (Log.isLoggable("EnhancedIntentService", 3)) {
            String valueOf = String.valueOf(componentName);
            Log.d("EnhancedIntentService", new StringBuilder(String.valueOf(valueOf).length() + 23).append("onServiceDisconnected: ").append(valueOf).toString());
        }
        zzavd();
    }

    public final synchronized void zza(Intent intent, PendingResult pendingResult) {
        if (Log.isLoggable("EnhancedIntentService", 3)) {
            Log.d("EnhancedIntentService", "new intent queued in the bind-strategy delivery");
        }
        this.zzifd.add(new zzd(intent, pendingResult, this.zzifc));
        zzavd();
    }
}
