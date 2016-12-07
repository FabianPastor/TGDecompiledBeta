package com.google.android.gms.gcm;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.Process;
import android.os.RemoteException;
import android.support.annotation.CallSuper;
import android.util.Log;
import java.util.HashSet;
import java.util.Set;

public abstract class GcmTaskService extends Service {
    public static final String SERVICE_ACTION_EXECUTE_TASK = "com.google.android.gms.gcm.ACTION_TASK_READY";
    public static final String SERVICE_ACTION_INITIALIZE = "com.google.android.gms.gcm.SERVICE_ACTION_INITIALIZE";
    public static final String SERVICE_PERMISSION = "com.google.android.gms.permission.BIND_NETWORK_TASK_SERVICE";
    private final Set<String> aeL = new HashSet();
    private int aeM;

    private class zza extends Thread {
        private final zzb aeN;
        final /* synthetic */ GcmTaskService aeO;
        private final Bundle mExtras;
        private final String mTag;

        zza(GcmTaskService gcmTaskService, String str, IBinder iBinder, Bundle bundle) {
            this.aeO = gcmTaskService;
            super(String.valueOf(str).concat(" GCM Task"));
            this.mTag = str;
            this.aeN = com.google.android.gms.gcm.zzb.zza.zzgs(iBinder);
            this.mExtras = bundle;
        }

        public void run() {
            Process.setThreadPriority(10);
            try {
                this.aeN.zztm(this.aeO.onRunTask(new TaskParams(this.mTag, this.mExtras)));
            } catch (RemoteException e) {
                String str = "GcmTaskService";
                String str2 = "Error reporting result of operation to scheduler for ";
                String valueOf = String.valueOf(this.mTag);
                Log.e(str, valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2));
            } finally {
                this.aeO.zzkn(this.mTag);
            }
        }
    }

    private void zzkn(String str) {
        synchronized (this.aeL) {
            this.aeL.remove(str);
            if (this.aeL.size() == 0) {
                stopSelf(this.aeM);
            }
        }
    }

    private void zztl(int i) {
        synchronized (this.aeL) {
            this.aeM = i;
            if (this.aeL.size() == 0) {
                stopSelf(this.aeM);
            }
        }
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onInitializeTasks() {
    }

    public abstract int onRunTask(TaskParams taskParams);

    @CallSuper
    public int onStartCommand(Intent intent, int i, int i2) {
        if (intent == null) {
            zztl(i2);
        } else {
            try {
                intent.setExtrasClassLoader(PendingCallback.class.getClassLoader());
                String action = intent.getAction();
                if (SERVICE_ACTION_EXECUTE_TASK.equals(action)) {
                    String stringExtra = intent.getStringExtra("tag");
                    Parcelable parcelableExtra = intent.getParcelableExtra("callback");
                    Bundle bundle = (Bundle) intent.getParcelableExtra("extras");
                    if (parcelableExtra == null || !(parcelableExtra instanceof PendingCallback)) {
                        String valueOf = String.valueOf(getPackageName());
                        Log.e("GcmTaskService", new StringBuilder((String.valueOf(valueOf).length() + 47) + String.valueOf(stringExtra).length()).append(valueOf).append(" ").append(stringExtra).append(": Could not process request, invalid callback.").toString());
                    } else {
                        synchronized (this.aeL) {
                            this.aeL.add(stringExtra);
                        }
                        new zza(this, stringExtra, ((PendingCallback) parcelableExtra).getIBinder(), bundle).start();
                    }
                } else if (SERVICE_ACTION_INITIALIZE.equals(action)) {
                    onInitializeTasks();
                } else {
                    Log.e("GcmTaskService", new StringBuilder(String.valueOf(action).length() + 37).append("Unknown action received ").append(action).append(", terminating").toString());
                }
                zztl(i2);
            } finally {
                zztl(i2);
            }
        }
        return 2;
    }
}
