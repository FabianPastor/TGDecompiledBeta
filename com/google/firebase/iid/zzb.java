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
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.util.Pair;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class zzb extends Service {
    @VisibleForTesting
    final ExecutorService zzbtI = Executors.newSingleThreadExecutor();
    private Binder zzckU;
    private int zzckV;
    private int zzckW = 0;
    private final Object zzrJ = new Object();

    public static class zza extends Binder {
        private final zzb zzckZ;

        zza(zzb com_google_firebase_iid_zzb) {
            this.zzckZ = com_google_firebase_iid_zzb;
        }

        private static void zza(@Nullable PendingResult pendingResult) {
            if (pendingResult != null) {
                pendingResult.finish();
            }
        }

        public void zza(final Intent intent, @Nullable final PendingResult pendingResult) {
            if (Binder.getCallingUid() != Process.myUid()) {
                throw new SecurityException("Binding only allowed within app");
            }
            if (Log.isLoggable("EnhancedIntentService", 3)) {
                Log.d("EnhancedIntentService", "service received new intent via bind strategy");
            }
            if (this.zzckZ.zzE(intent)) {
                zza(pendingResult);
                return;
            }
            if (Log.isLoggable("EnhancedIntentService", 3)) {
                Log.d("EnhancedIntentService", "intent being queued for bg execution");
            }
            this.zzckZ.zzbtI.execute(new Runnable(this) {
                final /* synthetic */ zza zzclb;

                public void run() {
                    if (Log.isLoggable("EnhancedIntentService", 3)) {
                        Log.d("EnhancedIntentService", "bg processing of the intent starting now");
                    }
                    this.zzclb.zzckZ.handleIntent(intent);
                    zza.zza(pendingResult);
                }
            });
        }
    }

    public static class zzb implements ServiceConnection {
        private final Intent zzclc;
        private final Queue<Pair<Intent, PendingResult>> zzcld = new LinkedList();
        private zza zzcle;
        private boolean zzclf = false;
        private final Context zzqn;

        public zzb(Context context, String str) {
            this.zzqn = context.getApplicationContext();
            this.zzclc = new Intent(str).setPackage(this.zzqn.getPackageName());
        }

        private synchronized void zzwH() {
            if (Log.isLoggable("EnhancedIntentService", 3)) {
                Log.d("EnhancedIntentService", "flush queue called");
            }
            while (!this.zzcld.isEmpty()) {
                if (Log.isLoggable("EnhancedIntentService", 3)) {
                    Log.d("EnhancedIntentService", "found intent to be delivered");
                }
                if (this.zzcle == null || !this.zzcle.isBinderAlive()) {
                    if (Log.isLoggable("EnhancedIntentService", 3)) {
                        Log.d("EnhancedIntentService", "binder is dead. start connection? " + (!this.zzclf));
                    }
                    if (!this.zzclf) {
                        this.zzclf = true;
                        try {
                            if (!com.google.android.gms.common.stats.zza.zzyJ().zza(this.zzqn, this.zzclc, (ServiceConnection) this, 65)) {
                                Log.e("EnhancedIntentService", "binding to the service failed");
                                while (!this.zzcld.isEmpty()) {
                                    ((PendingResult) ((Pair) this.zzcld.poll()).second).finish();
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
                    Pair pair = (Pair) this.zzcld.poll();
                    this.zzcle.zza((Intent) pair.first, (PendingResult) pair.second);
                }
            }
        }

        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            synchronized (this) {
                this.zzclf = false;
                this.zzcle = (zza) iBinder;
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

        public synchronized void zzb(Intent intent, PendingResult pendingResult) {
            if (Log.isLoggable("EnhancedIntentService", 3)) {
                Log.d("EnhancedIntentService", "new intent queued in the bind-strategy delivery");
            }
            this.zzcld.add(new Pair(intent, pendingResult));
            zzwH();
        }
    }

    private void zzC(Intent intent) {
        if (intent != null) {
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }
        synchronized (this.zzrJ) {
            this.zzckW--;
            if (this.zzckW == 0) {
                zzqE(this.zzckV);
            }
        }
    }

    public abstract void handleIntent(Intent intent);

    public final synchronized IBinder onBind(Intent intent) {
        if (Log.isLoggable("EnhancedIntentService", 3)) {
            Log.d("EnhancedIntentService", "Service received bind request");
        }
        if (this.zzckU == null) {
            this.zzckU = new zza(this);
        }
        return this.zzckU;
    }

    public final int onStartCommand(final Intent intent, int i, int i2) {
        synchronized (this.zzrJ) {
            this.zzckV = i2;
            this.zzckW++;
        }
        final Intent zzD = zzD(intent);
        if (zzD == null) {
            zzC(intent);
            return 2;
        } else if (zzE(zzD)) {
            zzC(intent);
            return 2;
        } else {
            this.zzbtI.execute(new Runnable(this) {
                final /* synthetic */ zzb zzckY;

                public void run() {
                    this.zzckY.handleIntent(zzD);
                    this.zzckY.zzC(intent);
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
