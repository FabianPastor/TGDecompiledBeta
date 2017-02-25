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
    private int zzbgm;
    private int zzbgn = 0;
    MessengerCompat zzbhN = new MessengerCompat(new Handler(this, Looper.getMainLooper()) {
        final /* synthetic */ zzb zzckY;

        public void handleMessage(Message message) {
            int zzc = MessengerCompat.zzc(message);
            zzf.zzbA(this.zzckY);
            this.zzckY.getPackageManager();
            if (zzc == zzf.zzbhY || zzc == zzf.zzbhX) {
                this.zzckY.zzm((Intent) message.obj);
                return;
            }
            int i = zzf.zzbhX;
            Log.w("FirebaseInstanceId", "Message from unexpected caller " + zzc + " mine=" + i + " appid=" + zzf.zzbhY);
        }
    });
    @VisibleForTesting
    final ExecutorService zzbtM = Executors.newSingleThreadExecutor();
    private final Object zzrJ = new Object();

    private void zzG(Intent intent) {
        if (intent != null) {
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }
        synchronized (this.zzrJ) {
            this.zzbgn--;
            if (this.zzbgn == 0) {
                zzjA(this.zzbgm);
            }
        }
    }

    public final IBinder onBind(Intent intent) {
        return (intent == null || !"com.google.firebase.INSTANCE_ID_EVENT".equals(intent.getAction())) ? null : this.zzbhN.getBinder();
    }

    public final int onStartCommand(final Intent intent, int i, int i2) {
        synchronized (this.zzrJ) {
            this.zzbgm = i2;
            this.zzbgn++;
        }
        final Intent zzF = zzF(intent);
        if (zzF == null) {
            zzG(intent);
            return 2;
        } else if (zzH(zzF)) {
            zzG(intent);
            return 2;
        } else {
            this.zzbtM.execute(new Runnable(this) {
                final /* synthetic */ zzb zzckY;

                public void run() {
                    this.zzckY.zzm(zzF);
                    this.zzckY.zzG(intent);
                }
            });
            return 3;
        }
    }

    protected abstract Intent zzF(Intent intent);

    public boolean zzH(Intent intent) {
        return false;
    }

    boolean zzjA(int i) {
        return stopSelfResult(i);
    }

    public abstract void zzm(Intent intent);
}
