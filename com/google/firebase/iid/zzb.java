package com.google.firebase.iid;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.VisibleForTesting;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class zzb extends Service {
    private final Object mLock = new Object();
    @VisibleForTesting
    final ExecutorService zzbrV = Executors.newSingleThreadExecutor();
    private Binder zzckd;
    private int zzcke;
    private int zzckf = 0;

    private final void zzm(Intent intent) {
        if (intent != null) {
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }
        synchronized (this.mLock) {
            this.zzckf--;
            if (this.zzckf == 0) {
                stopSelfResult(this.zzcke);
            }
        }
    }

    public abstract void handleIntent(Intent intent);

    public final synchronized IBinder onBind(Intent intent) {
        if (Log.isLoggable("EnhancedIntentService", 3)) {
            Log.d("EnhancedIntentService", "Service received bind request");
        }
        if (this.zzckd == null) {
            this.zzckd = new zzf(this);
        }
        return this.zzckd;
    }

    public final int onStartCommand(Intent intent, int i, int i2) {
        synchronized (this.mLock) {
            this.zzcke = i2;
            this.zzckf++;
        }
        Intent zzn = zzn(intent);
        if (zzn == null) {
            zzm(intent);
            return 2;
        } else if (zzo(zzn)) {
            zzm(intent);
            return 2;
        } else {
            this.zzbrV.execute(new zzc(this, zzn, intent));
            return 3;
        }
    }

    protected Intent zzn(Intent intent) {
        return intent;
    }

    public boolean zzo(Intent intent) {
        return false;
    }
}
