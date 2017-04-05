package com.google.android.gms.gcm;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.RemoteException;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.google.android.gms.common.util.zzt;
import com.google.android.gms.common.util.zzy;
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
    private ComponentName componentName;
    private final Object lock = new Object();
    private final Set<String> zzbgv = new HashSet();
    private int zzbgw;
    private Messenger zzbgx;
    private ExecutorService zzqp;

    @TargetApi(21)
    private class zza extends Handler {
        final /* synthetic */ GcmTaskService zzbgz;

        zza(GcmTaskService gcmTaskService, Looper looper) {
            this.zzbgz = gcmTaskService;
            super(looper);
        }

        private void zzc(Message message) {
            Bundle data = message.getData();
            if (data != null) {
                Messenger messenger = message.replyTo;
                if (messenger != null) {
                    this.zzbgz.zzqp.execute(new zzb(this.zzbgz, data.getString("tag"), messenger, data.getBundle("extras"), data.getParcelableArrayList("triggered_uris")));
                }
            }
        }

        private void zzd(Message message) {
            if (Log.isLoggable("GcmTaskService", 3)) {
                String valueOf = String.valueOf(message);
                Log.d("GcmTaskService", new StringBuilder(String.valueOf(valueOf).length() + 45).append("ignoring unimplemented stop message for now: ").append(valueOf).toString());
            }
        }

        public void handleMessage(Message message) {
            if (zzy.zzc(this.zzbgz, message.sendingUid, "com.google.android.gms")) {
                switch (message.what) {
                    case 1:
                        zzc(message);
                        return;
                    case 2:
                        zzd(message);
                        return;
                    case 4:
                        this.zzbgz.onInitializeTasks();
                        return;
                    default:
                        String valueOf = String.valueOf(message);
                        Log.e("GcmTaskService", new StringBuilder(String.valueOf(valueOf).length() + 31).append("Unrecognized message received: ").append(valueOf).toString());
                        return;
                }
            }
            Log.e("GcmTaskService", "unable to verify presence of Google Play Services");
        }
    }

    private class zzb implements Runnable {
        private final Bundle mExtras;
        @Nullable
        private final Messenger mMessenger;
        private final String mTag;
        private final List<Uri> zzbgA;
        @Nullable
        private final zzb zzbgB;
        final /* synthetic */ GcmTaskService zzbgz;

        zzb(GcmTaskService gcmTaskService, String str, IBinder iBinder, Bundle bundle, List<Uri> list) {
            this.zzbgz = gcmTaskService;
            this.mTag = str;
            this.zzbgB = com.google.android.gms.gcm.zzb.zza.zzcV(iBinder);
            this.mExtras = bundle;
            this.zzbgA = list;
            this.mMessenger = null;
        }

        zzb(GcmTaskService gcmTaskService, String str, Messenger messenger, Bundle bundle, List<Uri> list) {
            this.zzbgz = gcmTaskService;
            this.mTag = str;
            this.mMessenger = messenger;
            this.mExtras = bundle;
            this.zzbgA = list;
            this.zzbgB = null;
        }

        private boolean zzGR() {
            return this.mMessenger != null;
        }

        private void zzjB(int i) throws RemoteException {
            if (zzGR()) {
                this.mMessenger.send(zzjC(i));
            } else {
                this.zzbgB.zzjD(i);
            }
        }

        @NonNull
        private Message zzjC(int i) {
            Message obtain = Message.obtain();
            obtain.what = 3;
            obtain.arg1 = i;
            Bundle bundle = new Bundle();
            bundle.putParcelable("component", this.zzbgz.componentName);
            bundle.putString("tag", this.mTag);
            obtain.setData(bundle);
            return obtain;
        }

        public void run() {
            /* JADX: method processing error */
/*
Error: java.util.NoSuchElementException
	at java.util.HashMap$HashIterator.nextNode(HashMap.java:1439)
	at java.util.HashMap$KeyIterator.next(HashMap.java:1461)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.applyRemove(BlockFinallyExtract.java:535)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.extractFinally(BlockFinallyExtract.java:175)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.processExceptionHandler(BlockFinallyExtract.java:80)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.visit(BlockFinallyExtract.java:51)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:199)
*/
            /*
            r4 = this;
            r0 = new com.google.android.gms.gcm.TaskParams;
            r1 = r4.mTag;
            r2 = r4.mExtras;
            r3 = r4.zzbgA;
            r0.<init>(r1, r2, r3);
            r1 = r4.zzbgz;
            r0 = r1.onRunTask(r0);
            r4.zzjB(r0);	 Catch:{ RemoteException -> 0x0022, all -> 0x0050 }
            r0 = r4.zzGR();
            if (r0 != 0) goto L_0x0021;
        L_0x001a:
            r0 = r4.zzbgz;
            r1 = r4.mTag;
            r0.zzeD(r1);
        L_0x0021:
            return;
        L_0x0022:
            r0 = move-exception;
            r1 = "GcmTaskService";	 Catch:{ RemoteException -> 0x0022, all -> 0x0050 }
            r2 = "Error reporting result of operation to scheduler for ";	 Catch:{ RemoteException -> 0x0022, all -> 0x0050 }
            r0 = r4.mTag;	 Catch:{ RemoteException -> 0x0022, all -> 0x0050 }
            r0 = java.lang.String.valueOf(r0);	 Catch:{ RemoteException -> 0x0022, all -> 0x0050 }
            r3 = r0.length();	 Catch:{ RemoteException -> 0x0022, all -> 0x0050 }
            if (r3 == 0) goto L_0x004a;	 Catch:{ RemoteException -> 0x0022, all -> 0x0050 }
        L_0x0035:
            r0 = r2.concat(r0);	 Catch:{ RemoteException -> 0x0022, all -> 0x0050 }
        L_0x0039:
            android.util.Log.e(r1, r0);	 Catch:{ RemoteException -> 0x0022, all -> 0x0050 }
            r0 = r4.zzGR();
            if (r0 != 0) goto L_0x0021;
        L_0x0042:
            r0 = r4.zzbgz;
            r1 = r4.mTag;
            r0.zzeD(r1);
            goto L_0x0021;
        L_0x004a:
            r0 = new java.lang.String;	 Catch:{ RemoteException -> 0x0022, all -> 0x0050 }
            r0.<init>(r2);	 Catch:{ RemoteException -> 0x0022, all -> 0x0050 }
            goto L_0x0039;
        L_0x0050:
            r0 = move-exception;
            r1 = r4.zzGR();
            if (r1 != 0) goto L_0x005e;
        L_0x0057:
            r1 = r4.zzbgz;
            r2 = r4.mTag;
            r1.zzeD(r2);
        L_0x005e:
            throw r0;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.gcm.GcmTaskService.zzb.run():void");
        }
    }

    private void zzeD(String str) {
        synchronized (this.lock) {
            this.zzbgv.remove(str);
            if (this.zzbgv.isEmpty()) {
                stopSelf(this.zzbgw);
            }
        }
    }

    private void zzjA(int i) {
        synchronized (this.lock) {
            this.zzbgw = i;
            if (this.zzbgv.isEmpty()) {
                stopSelf(this.zzbgw);
            }
        }
    }

    @CallSuper
    public IBinder onBind(Intent intent) {
        return (intent != null && zzt.zzzo() && SERVICE_ACTION_EXECUTE_TASK.equals(intent.getAction())) ? this.zzbgx.getBinder() : null;
    }

    @CallSuper
    public void onCreate() {
        super.onCreate();
        this.zzqp = zzGQ();
        this.zzbgx = new Messenger(new zza(this, Looper.getMainLooper()));
        this.componentName = new ComponentName(this, getClass());
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
            zzjA(i2);
        } else {
            try {
                intent.setExtrasClassLoader(PendingCallback.class.getClassLoader());
                String action = intent.getAction();
                if (SERVICE_ACTION_EXECUTE_TASK.equals(action)) {
                    String stringExtra = intent.getStringExtra("tag");
                    Parcelable parcelableExtra = intent.getParcelableExtra("callback");
                    Bundle bundleExtra = intent.getBundleExtra("extras");
                    List parcelableArrayListExtra = intent.getParcelableArrayListExtra("triggered_uris");
                    if (parcelableExtra instanceof PendingCallback) {
                        synchronized (this.lock) {
                            if (this.zzbgv.add(stringExtra)) {
                                this.zzqp.execute(new zzb(this, stringExtra, ((PendingCallback) parcelableExtra).getIBinder(), bundleExtra, parcelableArrayListExtra));
                            } else {
                                String valueOf = String.valueOf(getPackageName());
                                Log.w("GcmTaskService", new StringBuilder((String.valueOf(valueOf).length() + 44) + String.valueOf(stringExtra).length()).append(valueOf).append(" ").append(stringExtra).append(": Task already running, won't start another").toString());
                                zzjA(i2);
                            }
                        }
                    } else {
                        String valueOf2 = String.valueOf(getPackageName());
                        Log.e("GcmTaskService", new StringBuilder((String.valueOf(valueOf2).length() + 47) + String.valueOf(stringExtra).length()).append(valueOf2).append(" ").append(stringExtra).append(": Could not process request, invalid callback.").toString());
                    }
                } else if (SERVICE_ACTION_INITIALIZE.equals(action)) {
                    onInitializeTasks();
                } else {
                    Log.e("GcmTaskService", new StringBuilder(String.valueOf(action).length() + 37).append("Unknown action received ").append(action).append(", terminating").toString());
                }
                zzjA(i2);
            } finally {
                zzjA(i2);
            }
        }
        return 2;
    }

    protected ExecutorService zzGQ() {
        return Executors.newFixedThreadPool(2, new ThreadFactory(this) {
            private final AtomicInteger zzbgy = new AtomicInteger(1);

            public Thread newThread(@NonNull Runnable runnable) {
                Thread thread = new Thread(runnable, "gcm-task#" + this.zzbgy.getAndIncrement());
                thread.setPriority(4);
                return thread;
            }
        });
    }
}
