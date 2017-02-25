package com.google.android.gms.gcm;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.RemoteException;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.util.Log;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class GcmTaskService extends Service {
    public static final String SERVICE_ACTION_EXECUTE_TASK = "com.google.android.gms.gcm.ACTION_TASK_READY";
    public static final String SERVICE_ACTION_INITIALIZE = "com.google.android.gms.gcm.SERVICE_ACTION_INITIALIZE";
    public static final String SERVICE_PERMISSION = "com.google.android.gms.permission.BIND_NETWORK_TASK_SERVICE";
    private final Set<String> zzbgv = new HashSet();
    private int zzbgw;
    private ExecutorService zzqp;

    private class zza implements Runnable {
        private final Bundle mExtras;
        private final String mTag;
        final /* synthetic */ GcmTaskService zzbgA;
        private final zzb zzbgy;
        private final List<Uri> zzbgz;

        zza(GcmTaskService gcmTaskService, String str, IBinder iBinder, Bundle bundle, List<Uri> list) {
            this.zzbgA = gcmTaskService;
            this.mTag = str;
            this.zzbgy = com.google.android.gms.gcm.zzb.zza.zzcV(iBinder);
            this.mExtras = bundle;
            this.zzbgz = list;
        }

        public void run() {
            try {
                this.zzbgy.zzjC(this.zzbgA.onRunTask(new TaskParams(this.mTag, this.mExtras, this.zzbgz)));
            } catch (RemoteException e) {
                String str = "GcmTaskService";
                String str2 = "Error reporting result of operation to scheduler for ";
                String valueOf = String.valueOf(this.mTag);
                Log.e(str, valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2));
            } finally {
                this.zzbgA.zzeD(this.mTag);
            }
        }
    }

    private void zzeD(String str) {
        synchronized (this.zzbgv) {
            this.zzbgv.remove(str);
            if (this.zzbgv.size() == 0) {
                stopSelf(this.zzbgw);
            }
        }
    }

    private void zzjB(int i) {
        synchronized (this.zzbgv) {
            this.zzbgw = i;
            if (this.zzbgv.size() == 0) {
                stopSelf(this.zzbgw);
            }
        }
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    @CallSuper
    public void onCreate() {
        super.onCreate();
        this.zzqp = zzGQ();
    }

    @CallSuper
    public void onDestroy() {
        super.onDestroy();
        List shutdownNow = this.zzqp.shutdownNow();
        if (!shutdownNow.isEmpty()) {
            Log.e("GcmTaskService", "Shutting down, but not all tasks are finished executing. Remaining: " + shutdownNow.size());
        }
    }

    public void onInitializeTasks() {
    }

    public abstract int onRunTask(TaskParams taskParams);

    @CallSuper
    public int onStartCommand(Intent intent, int i, int i2) {
        if (intent == null) {
            zzjB(i2);
        } else {
            try {
                intent.setExtrasClassLoader(PendingCallback.class.getClassLoader());
                String action = intent.getAction();
                if (SERVICE_ACTION_EXECUTE_TASK.equals(action)) {
                    String stringExtra = intent.getStringExtra("tag");
                    Parcelable parcelableExtra = intent.getParcelableExtra("callback");
                    Bundle bundle = (Bundle) intent.getParcelableExtra("extras");
                    List parcelableArrayListExtra = intent.getParcelableArrayListExtra("triggered_uris");
                    if (parcelableExtra == null || !(parcelableExtra instanceof PendingCallback)) {
                        String valueOf = String.valueOf(getPackageName());
                        Log.e("GcmTaskService", new StringBuilder((String.valueOf(valueOf).length() + 47) + String.valueOf(stringExtra).length()).append(valueOf).append(" ").append(stringExtra).append(": Could not process request, invalid callback.").toString());
                    } else {
                        synchronized (this.zzbgv) {
                            if (this.zzbgv.add(stringExtra)) {
                                this.zzqp.execute(new zza(this, stringExtra, ((PendingCallback) parcelableExtra).getIBinder(), bundle, parcelableArrayListExtra));
                            } else {
                                String valueOf2 = String.valueOf(getPackageName());
                                Log.w("GcmTaskService", new StringBuilder((String.valueOf(valueOf2).length() + 44) + String.valueOf(stringExtra).length()).append(valueOf2).append(" ").append(stringExtra).append(": Task already running, won't start another").toString());
                                zzjB(i2);
                            }
                        }
                    }
                } else if (SERVICE_ACTION_INITIALIZE.equals(action)) {
                    onInitializeTasks();
                } else {
                    Log.e("GcmTaskService", new StringBuilder(String.valueOf(action).length() + 37).append("Unknown action received ").append(action).append(", terminating").toString());
                }
                zzjB(i2);
            } finally {
                zzjB(i2);
            }
        }
        return 2;
    }

    protected ExecutorService zzGQ() {
        return Executors.newFixedThreadPool(2, new ThreadFactory(this) {
            private final AtomicInteger zzbgx = new AtomicInteger(1);

            public Thread newThread(@NonNull Runnable runnable) {
                Thread thread = new Thread(runnable, "gcm-task#" + this.zzbgx.getAndIncrement());
                thread.setPriority(4);
                return thread;
            }
        });
    }
}
