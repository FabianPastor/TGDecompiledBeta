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
    final ExecutorService aGI = Executors.newSingleThreadExecutor();
    private int agK;
    private int agL = 0;
    MessengerCompat aij = new MessengerCompat(new Handler(this, Looper.getMainLooper()) {
        final /* synthetic */ zzb bkx;

        public void handleMessage(Message message) {
            int zzc = MessengerCompat.zzc(message);
            zzf.zzdg(this.bkx);
            this.bkx.getPackageManager();
            if (zzc == zzf.aiv || zzc == zzf.aiu) {
                this.bkx.zzm((Intent) message.obj);
                return;
            }
            int i = zzf.aiu;
            Log.w("FirebaseInstanceId", "Message from unexpected caller " + zzc + " mine=" + i + " appid=" + zzf.aiv);
        }
    });
    private final Object zzako = new Object();

    private void zzaf(Intent intent) {
        if (intent != null) {
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }
        synchronized (this.zzako) {
            this.agL--;
            if (this.agL == 0) {
                zztl(this.agK);
            }
        }
    }

    public final IBinder onBind(Intent intent) {
        return (intent == null || !"com.google.firebase.INSTANCE_ID_EVENT".equals(intent.getAction())) ? null : this.aij.getBinder();
    }

    public final int onStartCommand(final Intent intent, int i, int i2) {
        synchronized (this.zzako) {
            this.agK = i2;
            this.agL++;
        }
        final Intent zzae = zzae(intent);
        if (zzae == null) {
            zzaf(intent);
            return 2;
        } else if (zzag(zzae)) {
            zzaf(intent);
            return 2;
        } else {
            this.aGI.execute(new Runnable(this) {
                final /* synthetic */ zzb bkx;

                public void run() {
                    this.bkx.zzm(zzae);
                    this.bkx.zzaf(intent);
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

    boolean zztl(int i) {
        return stopSelfResult(i);
    }
}
