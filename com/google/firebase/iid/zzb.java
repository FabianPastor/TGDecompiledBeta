package com.google.firebase.iid;

import android.app.Service;
import android.content.BroadcastReceiver.PendingResult;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.os.Process;
import android.support.annotation.VisibleForTesting;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class zzb extends Service {
    @VisibleForTesting
    final ExecutorService zzbtK = Executors.newSingleThreadExecutor();
    private Binder zzckT;
    private int zzckU;
    private int zzckV = 0;
    private final Object zzrJ = new Object();

    static class zza {
        final Intent intent;
        private final PendingResult zzckY;
        private boolean zzckZ = false;
        private final ScheduledFuture<?> zzcla;

        zza(final Intent intent, PendingResult pendingResult, ScheduledExecutorService scheduledExecutorService) {
            this.intent = intent;
            this.zzckY = pendingResult;
            this.zzcla = scheduledExecutorService.schedule(new Runnable(this) {
                final /* synthetic */ zza zzclb;

                public void run() {
                    String valueOf = String.valueOf(intent.getAction());
                    Log.w("EnhancedIntentService", new StringBuilder(String.valueOf(valueOf).length() + 61).append("Service took too long to process intent: ").append(valueOf).append(" App may get closed.").toString());
                    this.zzclb.finish();
                }
            }, 9500, TimeUnit.MILLISECONDS);
        }

        synchronized void finish() {
            if (!this.zzckZ) {
                this.zzckY.finish();
                this.zzcla.cancel(false);
                this.zzckZ = true;
            }
        }
    }

    public static class zzb extends Binder {
        private final zzb zzclc;

        zzb(zzb com_google_firebase_iid_zzb) {
            this.zzclc = com_google_firebase_iid_zzb;
        }

        public void zza(final zza com_google_firebase_iid_zzb_zza) {
            if (Binder.getCallingUid() != Process.myUid()) {
                throw new SecurityException("Binding only allowed within app");
            }
            if (Log.isLoggable("EnhancedIntentService", 3)) {
                Log.d("EnhancedIntentService", "service received new intent via bind strategy");
            }
            if (this.zzclc.zzE(com_google_firebase_iid_zzb_zza.intent)) {
                com_google_firebase_iid_zzb_zza.finish();
                return;
            }
            if (Log.isLoggable("EnhancedIntentService", 3)) {
                Log.d("EnhancedIntentService", "intent being queued for bg execution");
            }
            this.zzclc.zzbtK.execute(new Runnable(this) {
                final /* synthetic */ zzb zzcle;

                public void run() {
                    if (Log.isLoggable("EnhancedIntentService", 3)) {
                        Log.d("EnhancedIntentService", "bg processing of the intent starting now");
                    }
                    this.zzcle.zzclc.handleIntent(com_google_firebase_iid_zzb_zza.intent);
                    com_google_firebase_iid_zzb_zza.finish();
                }
            });
        }
    }

    public static class zzc implements ServiceConnection {
        private final Intent zzclf;
        private final ScheduledExecutorService zzclg;
        private final Queue<zza> zzclh;
        private zzb zzcli;
        private boolean zzclj;
        private final Context zzqn;

        public zzc(Context context, String str) {
            this(context, str, new ScheduledThreadPoolExecutor(0));
        }

        @VisibleForTesting
        zzc(Context context, String str, ScheduledExecutorService scheduledExecutorService) {
            this.zzclh = new LinkedList();
            this.zzclj = false;
            this.zzqn = context.getApplicationContext();
            this.zzclf = new Intent(str).setPackage(this.zzqn.getPackageName());
            this.zzclg = scheduledExecutorService;
        }

        private synchronized void zzwH() {
            if (Log.isLoggable("EnhancedIntentService", 3)) {
                Log.d("EnhancedIntentService", "flush queue called");
            }
            while (!this.zzclh.isEmpty()) {
                if (Log.isLoggable("EnhancedIntentService", 3)) {
                    Log.d("EnhancedIntentService", "found intent to be delivered");
                }
                if (this.zzcli == null || !this.zzcli.isBinderAlive()) {
                    if (Log.isLoggable("EnhancedIntentService", 3)) {
                        Log.d("EnhancedIntentService", "binder is dead. start connection? " + (!this.zzclj));
                    }
                    if (!this.zzclj) {
                        this.zzclj = true;
                        try {
                            if (!com.google.android.gms.common.stats.zza.zzyJ().zza(this.zzqn, this.zzclf, (ServiceConnection) this, 65)) {
                                Log.e("EnhancedIntentService", "binding to the service failed");
                                while (!this.zzclh.isEmpty()) {
                                    ((zza) this.zzclh.poll()).finish();
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
                    this.zzcli.zza((zza) this.zzclh.poll());
                }
            }
        }

        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            synchronized (this) {
                this.zzclj = false;
                this.zzcli = (zzb) iBinder;
                if (Log.isLoggable("EnhancedIntentService", 3)) {
                    String valueOf = String.valueOf(componentName);
                    Log.d("EnhancedIntentService", new StringBuilder(String.valueOf(valueOf).length() + 20).append("onServiceConnected: ").append(valueOf).toString());
                }
                zzwH();
            }
        }

        public void onServiceDisconnected(ComponentName componentName) {
            if (Log.isLoggable("EnhancedIntentService", 3)) {
                String valueOf = String.valueOf(componentName);
                Log.d("EnhancedIntentService", new StringBuilder(String.valueOf(valueOf).length() + 23).append("onServiceDisconnected: ").append(valueOf).toString());
            }
            zzwH();
        }

        public synchronized void zza(Intent intent, PendingResult pendingResult) {
            if (Log.isLoggable("EnhancedIntentService", 3)) {
                Log.d("EnhancedIntentService", "new intent queued in the bind-strategy delivery");
            }
            this.zzclh.add(new zza(intent, pendingResult, this.zzclg));
            zzwH();
        }
    }

    private void zzC(Intent intent) {
        if (intent != null) {
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }
        synchronized (this.zzrJ) {
            this.zzckV--;
            if (this.zzckV == 0) {
                zzqE(this.zzckU);
            }
        }
    }

    public abstract void handleIntent(Intent intent);

    public final synchronized IBinder onBind(Intent intent) {
        if (Log.isLoggable("EnhancedIntentService", 3)) {
            Log.d("EnhancedIntentService", "Service received bind request");
        }
        if (this.zzckT == null) {
            this.zzckT = new zzb(this);
        }
        return this.zzckT;
    }

    public final int onStartCommand(final Intent intent, int i, int i2) {
        synchronized (this.zzrJ) {
            this.zzckU = i2;
            this.zzckV++;
        }
        final Intent zzD = zzD(intent);
        if (zzD == null) {
            zzC(intent);
            return 2;
        } else if (zzE(zzD)) {
            zzC(intent);
            return 2;
        } else {
            this.zzbtK.execute(new Runnable(this) {
                final /* synthetic */ zzb zzckX;

                public void run() {
                    this.zzckX.handleIntent(zzD);
                    this.zzckX.zzC(intent);
                }
            });
            return 3;
        }
    }

    protected Intent zzD(Intent intent) {
        return intent;
    }

    public boolean zzE(Intent intent) {
        return false;
    }

    boolean zzqE(int i) {
        return stopSelfResult(i);
    }
}
