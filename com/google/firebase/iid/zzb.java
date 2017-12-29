package com.google.firebase.iid;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class zzb extends Service {
    private final Object mLock = new Object();
    final ExecutorService zzieo = Executors.newSingleThreadExecutor();
    private Binder zziep;
    private int zzieq;
    private int zzier = 0;

    private final void zzh(Intent intent) {
        if (intent != null) {
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }
        synchronized (this.mLock) {
            this.zzier--;
            if (this.zzier == 0) {
                stopSelfResult(this.zzieq);
            }
        }
    }

    public abstract void handleIntent(Intent intent);

    public final synchronized IBinder onBind(Intent intent) {
        if (Log.isLoggable("EnhancedIntentService", 3)) {
            Log.d("EnhancedIntentService", "Service received bind request");
        }
        if (this.zziep == null) {
            this.zziep = new zzf(this);
        }
        return this.zziep;
    }

    public final int onStartCommand(Intent intent, int i, int i2) {
        synchronized (this.mLock) {
            this.zzieq = i2;
            this.zzier++;
        }
        Intent zzp = zzp(intent);
        if (zzp == null) {
            zzh(intent);
            return 2;
        } else if (zzq(zzp)) {
            zzh(intent);
            return 2;
        } else {
            this.zzieo.execute(new zzc(this, zzp, intent));
            return 3;
        }
    }

    protected Intent zzp(Intent intent) {
        return intent;
    }

    public boolean zzq(Intent intent) {
        return false;
    }
}
