package com.google.android.gms.gcm;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.RemoteException;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.util.Log;
import com.google.android.gms.common.util.zzq;
import com.google.android.gms.common.util.zzw;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class GcmTaskService extends Service {
    public static final String SERVICE_ACTION_EXECUTE_TASK = "com.google.android.gms.gcm.ACTION_TASK_READY";
    public static final String SERVICE_ACTION_INITIALIZE = "com.google.android.gms.gcm.SERVICE_ACTION_INITIALIZE";
    public static final String SERVICE_PERMISSION = "com.google.android.gms.permission.BIND_NETWORK_TASK_SERVICE";
    private ComponentName componentName;
    private final Object lock = new Object();
    private final Set<String> zzbfE = new HashSet();
    private int zzbfF;
    private Messenger zzbfG;
    private ExecutorService zzqF;

    @TargetApi(21)
    class zza extends Handler {
        private /* synthetic */ GcmTaskService zzbfI;

        zza(GcmTaskService gcmTaskService, Looper looper) {
            this.zzbfI = gcmTaskService;
            super(looper);
        }

        public final void handleMessage(Message message) {
            if (zzw.zzb(this.zzbfI, message.sendingUid, "com.google.android.gms")) {
                String valueOf;
                switch (message.what) {
                    case 1:
                        Bundle data = message.getData();
                        if (data != null) {
                            Messenger messenger = message.replyTo;
                            if (messenger != null) {
                                this.zzbfI.zza(new zzb(this.zzbfI, data.getString("tag"), messenger, data.getBundle("extras"), data.getParcelableArrayList("triggered_uris")));
                                return;
                            }
                            return;
                        }
                        return;
                    case 2:
                        if (Log.isLoggable("GcmTaskService", 3)) {
                            valueOf = String.valueOf(message);
                            Log.d("GcmTaskService", new StringBuilder(String.valueOf(valueOf).length() + 45).append("ignoring unimplemented stop message for now: ").append(valueOf).toString());
                            return;
                        }
                        return;
                    case 4:
                        this.zzbfI.onInitializeTasks();
                        return;
                    default:
                        valueOf = String.valueOf(message);
                        Log.e("GcmTaskService", new StringBuilder(String.valueOf(valueOf).length() + 31).append("Unrecognized message received: ").append(valueOf).toString());
                        return;
                }
            }
            Log.e("GcmTaskService", "unable to verify presence of Google Play Services");
        }
    }

    class zzb implements Runnable {
        private final Bundle mExtras;
        @Nullable
        private final Messenger mMessenger;
        private final String mTag;
        private /* synthetic */ GcmTaskService zzbfI;
        private final List<Uri> zzbfJ;
        @Nullable
        private final zzd zzbfK;

        zzb(GcmTaskService gcmTaskService, String str, IBinder iBinder, Bundle bundle, List<Uri> list) {
            zzd com_google_android_gms_gcm_zzd;
            this.zzbfI = gcmTaskService;
            this.mTag = str;
            if (iBinder == null) {
                com_google_android_gms_gcm_zzd = null;
            } else {
                IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.gcm.INetworkTaskCallback");
                com_google_android_gms_gcm_zzd = queryLocalInterface instanceof zzd ? (zzd) queryLocalInterface : new zze(iBinder);
            }
            this.zzbfK = com_google_android_gms_gcm_zzd;
            this.mExtras = bundle;
            this.zzbfJ = list;
            this.mMessenger = null;
        }

        zzb(GcmTaskService gcmTaskService, String str, Messenger messenger, Bundle bundle, List<Uri> list) {
            this.zzbfI = gcmTaskService;
            this.mTag = str;
            this.mMessenger = messenger;
            this.mExtras = bundle;
            this.zzbfJ = list;
            this.zzbfK = null;
        }

        private final void zzbg(int i) {
            synchronized (this.zzbfI.lock) {
                try {
                    if (zzvC()) {
                        Messenger messenger = this.mMessenger;
                        Message obtain = Message.obtain();
                        obtain.what = 3;
                        obtain.arg1 = i;
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("component", this.zzbfI.componentName);
                        bundle.putString("tag", this.mTag);
                        obtain.setData(bundle);
                        messenger.send(obtain);
                    } else {
                        this.zzbfK.zzbh(i);
                    }
                    if (!zzvC()) {
                        this.zzbfI.zzdp(this.mTag);
                    }
                } catch (RemoteException e) {
                    String str = "GcmTaskService";
                    String str2 = "Error reporting result of operation to scheduler for ";
                    String valueOf = String.valueOf(this.mTag);
                    Log.e(str, valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2));
                    if (!zzvC()) {
                        this.zzbfI.zzdp(this.mTag);
                    }
                } catch (Throwable th) {
                    if (!zzvC()) {
                        this.zzbfI.zzdp(this.mTag);
                    }
                }
            }
        }

        private final boolean zzvC() {
            return this.mMessenger != null;
        }

        public final void run() {
            zzbg(this.zzbfI.onRunTask(new TaskParams(this.mTag, this.mExtras, this.zzbfJ)));
        }
    }

    private final void zza(zzb com_google_android_gms_gcm_GcmTaskService_zzb) {
        try {
            this.zzqF.execute(com_google_android_gms_gcm_GcmTaskService_zzb);
        } catch (Throwable e) {
            Log.e("GcmTaskService", "Executor is shutdown. onDestroy was called but main looper had an unprocessed start task message. The task will be retried with backoff delay.", e);
            com_google_android_gms_gcm_GcmTaskService_zzb.zzbg(1);
        }
    }

    private final void zzbf(int i) {
        synchronized (this.lock) {
            this.zzbfF = i;
            if (this.zzbfE.isEmpty()) {
                stopSelf(this.zzbfF);
            }
        }
    }

    private final void zzdp(String str) {
        synchronized (this.lock) {
            this.zzbfE.remove(str);
            if (this.zzbfE.isEmpty()) {
                stopSelf(this.zzbfF);
            }
        }
    }

    @CallSuper
    public IBinder onBind(Intent intent) {
        return (intent != null && zzq.zzse() && SERVICE_ACTION_EXECUTE_TASK.equals(intent.getAction())) ? this.zzbfG.getBinder() : null;
    }

    @CallSuper
    public void onCreate() {
        super.onCreate();
        this.zzqF = Executors.newFixedThreadPool(2, new zzb(this));
        this.zzbfG = new Messenger(new zza(this, Looper.getMainLooper()));
        this.componentName = new ComponentName(this, getClass());
    }

    @CallSuper
    public void onDestroy() {
        super.onDestroy();
        List shutdownNow = this.zzqF.shutdownNow();
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
            zzbf(i2);
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
                            if (this.zzbfE.add(stringExtra)) {
                                zza(new zzb(this, stringExtra, ((PendingCallback) parcelableExtra).zzaHj, bundleExtra, parcelableArrayListExtra));
                            } else {
                                String valueOf = String.valueOf(getPackageName());
                                Log.w("GcmTaskService", new StringBuilder((String.valueOf(valueOf).length() + 44) + String.valueOf(stringExtra).length()).append(valueOf).append(" ").append(stringExtra).append(": Task already running, won't start another").toString());
                                zzbf(i2);
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
                zzbf(i2);
            } finally {
                zzbf(i2);
            }
        }
        return 2;
    }
}
