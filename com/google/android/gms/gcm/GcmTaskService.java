package com.google.android.gms.gcm;

import android.app.Service;
import android.content.Intent;
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
    private final Set<String> agT = new HashSet();
    private int agU;
    private ExecutorService zzahu;

    private class zza implements Runnable {
        final /* synthetic */ GcmTaskService agW;
        private final zzb agX;
        private final Bundle mExtras;
        private final String mTag;

        zza(GcmTaskService gcmTaskService, String str, IBinder iBinder, Bundle bundle) {
            this.agW = gcmTaskService;
            this.mTag = str;
            this.agX = com.google.android.gms.gcm.zzb.zza.zzgt(iBinder);
            this.mExtras = bundle;
        }

        public void run() {
            try {
                this.agX.zztn(this.agW.onRunTask(new TaskParams(this.mTag, this.mExtras)));
            } catch (RemoteException e) {
                String str = "GcmTaskService";
                String str2 = "Error reporting result of operation to scheduler for ";
                String valueOf = String.valueOf(this.mTag);
                Log.e(str, valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2));
            } finally {
                this.agW.zzkn(this.mTag);
            }
        }
    }

    private void zzkn(String str) {
        synchronized (this.agT) {
            this.agT.remove(str);
            if (this.agT.size() == 0) {
                stopSelf(this.agU);
            }
        }
    }

    private void zztm(int i) {
        synchronized (this.agT) {
            this.agU = i;
            if (this.agT.size() == 0) {
                stopSelf(this.agU);
            }
        }
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    @CallSuper
    public void onCreate() {
        super.onCreate();
        this.zzahu = zzbns();
    }

    @CallSuper
    public void onDestroy() {
        super.onDestroy();
        List shutdownNow = this.zzahu.shutdownNow();
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
            zztm(i2);
        } else {
            try {
                intent.setExtrasClassLoader(PendingCallback.class.getClassLoader());
                String action = intent.getAction();
                if (SERVICE_ACTION_EXECUTE_TASK.equals(action)) {
                    String stringExtra = intent.getStringExtra("tag");
                    Parcelable parcelableExtra = intent.getParcelableExtra("callback");
                    Bundle bundle = (Bundle) intent.getParcelableExtra("extras");
                    String valueOf;
                    if (parcelableExtra == null || !(parcelableExtra instanceof PendingCallback)) {
                        valueOf = String.valueOf(getPackageName());
                        Log.e("GcmTaskService", new StringBuilder((String.valueOf(valueOf).length() + 47) + String.valueOf(stringExtra).length()).append(valueOf).append(" ").append(stringExtra).append(": Could not process request, invalid callback.").toString());
                    } else {
                        synchronized (this.agT) {
                            if (this.agT.add(stringExtra)) {
                                this.zzahu.execute(new zza(this, stringExtra, ((PendingCallback) parcelableExtra).getIBinder(), bundle));
                            } else {
                                valueOf = String.valueOf(getPackageName());
                                Log.w("GcmTaskService", new StringBuilder((String.valueOf(valueOf).length() + 44) + String.valueOf(stringExtra).length()).append(valueOf).append(" ").append(stringExtra).append(": Task already running, won't start another").toString());
                                zztm(i2);
                            }
                        }
                    }
                } else if (SERVICE_ACTION_INITIALIZE.equals(action)) {
                    onInitializeTasks();
                } else {
                    Log.e("GcmTaskService", new StringBuilder(String.valueOf(action).length() + 37).append("Unknown action received ").append(action).append(", terminating").toString());
                }
                zztm(i2);
            } finally {
                zztm(i2);
            }
        }
        return 2;
    }

    protected ExecutorService zzbns() {
        return Executors.newFixedThreadPool(2, new ThreadFactory(this) {
            private final AtomicInteger agV = new AtomicInteger(1);
            final /* synthetic */ GcmTaskService agW;

            {
                this.agW = r3;
            }

            public Thread newThread(@NonNull Runnable runnable) {
                Thread thread = new Thread(runnable, "gcm-task#" + this.agV.getAndIncrement());
                thread.setPriority(4);
                return thread;
            }
        });
    }
}
