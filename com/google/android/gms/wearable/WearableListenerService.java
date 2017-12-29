package com.google.android.gms.wearable;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.common.util.zzx;
import com.google.android.gms.wearable.CapabilityApi.CapabilityListener;
import com.google.android.gms.wearable.ChannelApi.ChannelListener;
import com.google.android.gms.wearable.ChannelClient.Channel;
import com.google.android.gms.wearable.ChannelClient.ChannelCallback;
import com.google.android.gms.wearable.DataApi.DataListener;
import com.google.android.gms.wearable.MessageApi.MessageListener;
import com.google.android.gms.wearable.internal.zzah;
import com.google.android.gms.wearable.internal.zzas;
import com.google.android.gms.wearable.internal.zzaw;
import com.google.android.gms.wearable.internal.zzen;
import com.google.android.gms.wearable.internal.zzfe;
import com.google.android.gms.wearable.internal.zzfo;
import com.google.android.gms.wearable.internal.zzhp;
import com.google.android.gms.wearable.internal.zzi;
import com.google.android.gms.wearable.internal.zzl;
import java.util.List;

public class WearableListenerService extends Service implements CapabilityListener, ChannelListener, DataListener, MessageListener {
    public static final String BIND_LISTENER_INTENT_ACTION = "com.google.android.gms.wearable.BIND_LISTENER";
    private IBinder zzfzf;
    private ComponentName zzlhd;
    private zzc zzlhe;
    private Intent zzlhf;
    private Looper zzlhg;
    private final Object zzlhh = new Object();
    private boolean zzlhi;
    private zzas zzlhj = new zzas(new zza());

    class zzb implements ServiceConnection {
        private zzb(WearableListenerService wearableListenerService) {
        }

        public final void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        }

        public final void onServiceDisconnected(ComponentName componentName) {
        }
    }

    final class zzc extends Handler {
        private boolean started;
        private /* synthetic */ WearableListenerService zzlhk;
        private final zzb zzlhl = new zzb();

        zzc(WearableListenerService wearableListenerService, Looper looper) {
            this.zzlhk = wearableListenerService;
            super(looper);
        }

        @SuppressLint({"UntrackedBindService"})
        private final synchronized void zzbke() {
            if (!this.started) {
                if (Log.isLoggable("WearableLS", 2)) {
                    String valueOf = String.valueOf(this.zzlhk.zzlhd);
                    Log.v("WearableLS", new StringBuilder(String.valueOf(valueOf).length() + 13).append("bindService: ").append(valueOf).toString());
                }
                this.zzlhk.bindService(this.zzlhk.zzlhf, this.zzlhl, 1);
                this.started = true;
            }
        }

        @SuppressLint({"UntrackedBindService"})
        private final synchronized void zznx(String str) {
            if (this.started) {
                if (Log.isLoggable("WearableLS", 2)) {
                    String valueOf = String.valueOf(this.zzlhk.zzlhd);
                    Log.v("WearableLS", new StringBuilder((String.valueOf(str).length() + 17) + String.valueOf(valueOf).length()).append("unbindService: ").append(str).append(", ").append(valueOf).toString());
                }
                try {
                    this.zzlhk.unbindService(this.zzlhl);
                } catch (Throwable e) {
                    Log.e("WearableLS", "Exception when unbinding from local service", e);
                }
                this.started = false;
            }
        }

        public final void dispatchMessage(Message message) {
            zzbke();
            try {
                super.dispatchMessage(message);
            } finally {
                if (!hasMessages(0)) {
                    zznx("dispatch");
                }
            }
        }

        final void quit() {
            getLooper().quit();
            zznx("quit");
        }
    }

    class zza extends ChannelCallback {
        private /* synthetic */ WearableListenerService zzlhk;

        private zza(WearableListenerService wearableListenerService) {
            this.zzlhk = wearableListenerService;
        }

        public final void onChannelClosed(Channel channel, int i, int i2) {
            this.zzlhk.onChannelClosed(channel, i, i2);
        }

        public final void onChannelOpened(Channel channel) {
            this.zzlhk.onChannelOpened(channel);
        }

        public final void onInputClosed(Channel channel, int i, int i2) {
            this.zzlhk.onInputClosed(channel, i, i2);
        }

        public final void onOutputClosed(Channel channel, int i, int i2) {
            this.zzlhk.onOutputClosed(channel, i, i2);
        }
    }

    final class zzd extends zzen {
        final /* synthetic */ WearableListenerService zzlhk;
        private volatile int zzlhm;

        private zzd(WearableListenerService wearableListenerService) {
            this.zzlhk = wearableListenerService;
            this.zzlhm = -1;
        }

        private final boolean zza(Runnable runnable, String str, Object obj) {
            if (Log.isLoggable("WearableLS", 3)) {
                Log.d("WearableLS", String.format("%s: %s %s", new Object[]{str, this.zzlhk.zzlhd.toString(), obj}));
            }
            int callingUid = Binder.getCallingUid();
            if (callingUid == this.zzlhm) {
                callingUid = 1;
            } else if (zzhp.zzep(this.zzlhk).zznz("com.google.android.wearable.app.cn") && zzx.zzb(this.zzlhk, callingUid, "com.google.android.wearable.app.cn")) {
                this.zzlhm = callingUid;
                callingUid = 1;
            } else if (zzx.zzf(this.zzlhk, callingUid)) {
                this.zzlhm = callingUid;
                callingUid = 1;
            } else {
                Log.e("WearableLS", "Caller is not GooglePlayServices; caller UID: " + callingUid);
                boolean z = false;
            }
            if (callingUid == 0) {
                return false;
            }
            synchronized (this.zzlhk.zzlhh) {
                if (this.zzlhk.zzlhi) {
                    return false;
                }
                this.zzlhk.zzlhe.post(runnable);
                return true;
            }
        }

        public final void onConnectedNodes(List<zzfo> list) {
            zza(new zzp(this, list), "onConnectedNodes", list);
        }

        public final void zza(zzah com_google_android_gms_wearable_internal_zzah) {
            zza(new zzq(this, com_google_android_gms_wearable_internal_zzah), "onConnectedCapabilityChanged", com_google_android_gms_wearable_internal_zzah);
        }

        public final void zza(zzaw com_google_android_gms_wearable_internal_zzaw) {
            zza(new zzt(this, com_google_android_gms_wearable_internal_zzaw), "onChannelEvent", com_google_android_gms_wearable_internal_zzaw);
        }

        public final void zza(zzfe com_google_android_gms_wearable_internal_zzfe) {
            zza(new zzm(this, com_google_android_gms_wearable_internal_zzfe), "onMessageReceived", com_google_android_gms_wearable_internal_zzfe);
        }

        public final void zza(zzfo com_google_android_gms_wearable_internal_zzfo) {
            zza(new zzn(this, com_google_android_gms_wearable_internal_zzfo), "onPeerConnected", com_google_android_gms_wearable_internal_zzfo);
        }

        public final void zza(zzi com_google_android_gms_wearable_internal_zzi) {
            zza(new zzs(this, com_google_android_gms_wearable_internal_zzi), "onEntityUpdate", com_google_android_gms_wearable_internal_zzi);
        }

        public final void zza(zzl com_google_android_gms_wearable_internal_zzl) {
            zza(new zzr(this, com_google_android_gms_wearable_internal_zzl), "onNotificationReceived", com_google_android_gms_wearable_internal_zzl);
        }

        public final void zzas(DataHolder dataHolder) {
            Runnable com_google_android_gms_wearable_zzl = new zzl(this, dataHolder);
            try {
                String valueOf = String.valueOf(dataHolder);
                if (!zza(com_google_android_gms_wearable_zzl, "onDataItemChanged", new StringBuilder(String.valueOf(valueOf).length() + 18).append(valueOf).append(", rows=").append(dataHolder.getCount()).toString())) {
                }
            } finally {
                dataHolder.close();
            }
        }

        public final void zzb(zzfo com_google_android_gms_wearable_internal_zzfo) {
            zza(new zzo(this, com_google_android_gms_wearable_internal_zzfo), "onPeerDisconnected", com_google_android_gms_wearable_internal_zzfo);
        }
    }

    public Looper getLooper() {
        if (this.zzlhg == null) {
            HandlerThread handlerThread = new HandlerThread("WearableListenerService");
            handlerThread.start();
            this.zzlhg = handlerThread.getLooper();
        }
        return this.zzlhg;
    }

    public final IBinder onBind(Intent intent) {
        return BIND_LISTENER_INTENT_ACTION.equals(intent.getAction()) ? this.zzfzf : null;
    }

    public void onCapabilityChanged(CapabilityInfo capabilityInfo) {
    }

    public void onChannelClosed(Channel channel, int i, int i2) {
    }

    public void onChannelClosed(Channel channel, int i, int i2) {
    }

    public void onChannelOpened(Channel channel) {
    }

    public void onChannelOpened(Channel channel) {
    }

    public void onConnectedNodes(List<Node> list) {
    }

    public void onCreate() {
        super.onCreate();
        this.zzlhd = new ComponentName(this, getClass().getName());
        if (Log.isLoggable("WearableLS", 3)) {
            String valueOf = String.valueOf(this.zzlhd);
            Log.d("WearableLS", new StringBuilder(String.valueOf(valueOf).length() + 10).append("onCreate: ").append(valueOf).toString());
        }
        this.zzlhe = new zzc(this, getLooper());
        this.zzlhf = new Intent(BIND_LISTENER_INTENT_ACTION);
        this.zzlhf.setComponent(this.zzlhd);
        this.zzfzf = new zzd();
    }

    public void onDataChanged(DataEventBuffer dataEventBuffer) {
    }

    public void onDestroy() {
        if (Log.isLoggable("WearableLS", 3)) {
            String valueOf = String.valueOf(this.zzlhd);
            Log.d("WearableLS", new StringBuilder(String.valueOf(valueOf).length() + 11).append("onDestroy: ").append(valueOf).toString());
        }
        synchronized (this.zzlhh) {
            this.zzlhi = true;
            if (this.zzlhe == null) {
                String valueOf2 = String.valueOf(this.zzlhd);
                throw new IllegalStateException(new StringBuilder(String.valueOf(valueOf2).length() + 111).append("onDestroy: mServiceHandler not set, did you override onCreate() but forget to call super.onCreate()? component=").append(valueOf2).toString());
            } else {
                this.zzlhe.quit();
            }
        }
        super.onDestroy();
    }

    public void onEntityUpdate(zzb com_google_android_gms_wearable_zzb) {
    }

    public void onInputClosed(Channel channel, int i, int i2) {
    }

    public void onInputClosed(Channel channel, int i, int i2) {
    }

    public void onMessageReceived(MessageEvent messageEvent) {
    }

    public void onNotificationReceived(zzd com_google_android_gms_wearable_zzd) {
    }

    public void onOutputClosed(Channel channel, int i, int i2) {
    }

    public void onOutputClosed(Channel channel, int i, int i2) {
    }

    public void onPeerConnected(Node node) {
    }

    public void onPeerDisconnected(Node node) {
    }
}
