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
    final ExecutorService zzbFy = Executors.newSingleThreadExecutor();
    private int zzbfI;
    private int zzbfJ = 0;
    MessengerCompat zzbhh = new MessengerCompat(new Handler(this, Looper.getMainLooper()) {
        final /* synthetic */ zzb zzciN;

        public void handleMessage(Message message) {
            int zzc = MessengerCompat.zzc(message);
            zzf.zzbi(this.zzciN);
            this.zzciN.getPackageManager();
            if (zzc == zzf.zzbhs || zzc == zzf.zzbhr) {
                this.zzciN.zzm((Intent) message.obj);
                return;
            }
            int i = zzf.zzbhr;
            Log.w("FirebaseInstanceId", "Message from unexpected caller " + zzc + " mine=" + i + " appid=" + zzf.zzbhs);
        }
    });
    private final Object zzrN = new Object();

    private void zzG(Intent intent) {
        if (intent != null) {
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }
        synchronized (this.zzrN) {
            this.zzbfJ--;
            if (this.zzbfJ == 0) {
                zzjr(this.zzbfI);
            }
        }
    }

    public final IBinder onBind(Intent intent) {
        return (intent == null || !"com.google.firebase.INSTANCE_ID_EVENT".equals(intent.getAction())) ? null : this.zzbhh.getBinder();
    }

    public final int onStartCommand(final Intent intent, int i, int i2) {
        synchronized (this.zzrN) {
            this.zzbfI = i2;
            this.zzbfJ++;
        }
        final Intent zzF = zzF(intent);
        if (zzF == null) {
            zzG(intent);
            return 2;
        } else if (zzH(zzF)) {
            zzG(intent);
            return 2;
        } else {
            this.zzbFy.execute(new Runnable(this) {
                final /* synthetic */ zzb zzciN;

                public void run() {
                    this.zzciN.zzm(zzF);
                    this.zzciN.zzG(intent);
                }
            });
            return 3;
        }
    }

    protected abstract Intent zzF(Intent intent);

    public boolean zzH(Intent intent) {
        return false;
    }

    boolean zzjr(int i) {
        return stopSelfResult(i);
    }

    public abstract void zzm(Intent intent);
}
