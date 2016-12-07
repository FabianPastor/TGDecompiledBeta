package com.google.firebase.iid;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.VisibleForTesting;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import com.google.android.gms.iid.MessengerCompat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class zzb extends Service {
    @VisibleForTesting
    final ExecutorService aDx = Executors.newSingleThreadExecutor();
    private int aeC;
    private int aeD = 0;
    MessengerCompat afZ = new MessengerCompat(new Handler(this, Looper.getMainLooper()) {
        final /* synthetic */ zzb bhl;

        public void handleMessage(Message message) {
            int zzc = MessengerCompat.zzc(message);
            zzf.zzdj(this.bhl);
            this.bhl.getPackageManager();
            if (zzc == zzf.agl || zzc == zzf.agk) {
                this.bhl.zzm((Intent) message.obj);
                return;
            }
            int i = zzf.agk;
            Log.w("FirebaseInstanceId", "Message from unexpected caller " + zzc + " mine=" + i + " appid=" + zzf.agl);
        }
    });
    private final Object zzakd = new Object();

    private void zzaf(Intent intent) {
        if (intent != null) {
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }
        synchronized (this.zzakd) {
            this.aeD--;
            if (this.aeD == 0) {
                zztk(this.aeC);
            }
        }
    }

    public final IBinder onBind(Intent intent) {
        return (intent == null || !"com.google.firebase.INSTANCE_ID_EVENT".equals(intent.getAction())) ? null : this.afZ.getBinder();
    }

    public final int onStartCommand(final Intent intent, int i, int i2) {
        synchronized (this.zzakd) {
            this.aeC = i2;
            this.aeD++;
        }
        final Intent zzae = zzae(intent);
        if (zzae == null) {
            zzaf(intent);
            return 2;
        } else if (zzag(zzae)) {
            zzaf(intent);
            return 2;
        } else {
            this.aDx.execute(new Runnable(this) {
                final /* synthetic */ zzb bhl;

                public void run() {
                    this.bhl.zzm(zzae);
                    this.bhl.zzaf(intent);
                }
            });
            return 3;
        }
    }

    protected abstract Intent zzae(Intent intent);

    public boolean zzag(Intent intent) {
        return false;
    }

    public abstract void zzm(Intent intent);

    boolean zztk(int i) {
        return stopSelfResult(i);
    }
}
