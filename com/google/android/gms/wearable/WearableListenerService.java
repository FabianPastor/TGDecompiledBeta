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
import com.google.android.gms.common.util.zzw;
import com.google.android.gms.wearable.CapabilityApi.CapabilityListener;
import com.google.android.gms.wearable.ChannelApi.ChannelListener;
import com.google.android.gms.wearable.DataApi.DataListener;
import com.google.android.gms.wearable.MessageApi.MessageListener;
import com.google.android.gms.wearable.NodeApi.NodeListener;
import com.google.android.gms.wearable.internal.zzaa;
import com.google.android.gms.wearable.internal.zzai;
import com.google.android.gms.wearable.internal.zzdl;
import com.google.android.gms.wearable.internal.zzdx;
import com.google.android.gms.wearable.internal.zzeg;
import com.google.android.gms.wearable.internal.zzgh;
import com.google.android.gms.wearable.internal.zzi;
import com.google.android.gms.wearable.internal.zzl;
import java.util.List;

public class WearableListenerService extends Service implements CapabilityListener, ChannelListener, DataListener, MessageListener, NodeListener {
    public static final String BIND_LISTENER_INTENT_ACTION = "com.google.android.gms.wearable.BIND_LISTENER";
    private IBinder zzaHj;
    private ComponentName zzbRq;
    private zzb zzbRr;
    private Intent zzbRs;
    private Looper zzbRt;
    private final Object zzbRu = new Object();
    private boolean zzbRv;

    class zza implements ServiceConnection {
        private zza(WearableListenerService wearableListenerService) {
        }

        public final void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        }

        public final void onServiceDisconnected(ComponentName componentName) {
        }
    }

    final class zzb extends Handler {
        private boolean started;
        private final zza zzbRw = new zza();
        private /* synthetic */ WearableListenerService zzbRx;

        zzb(WearableListenerService wearableListenerService, Looper looper) {
            this.zzbRx = wearableListenerService;
            super(looper);
        }

        @SuppressLint({"UntrackedBindService"})
        private final synchronized void zzDW() {
            if (!this.started) {
                if (Log.isLoggable("WearableLS", 2)) {
                    String valueOf = String.valueOf(this.zzbRx.zzbRq);
                    Log.v("WearableLS", new StringBuilder(String.valueOf(valueOf).length() + 13).append("bindService: ").append(valueOf).toString());
                }
                this.zzbRx.bindService(this.zzbRx.zzbRs, this.zzbRw, 1);
                this.started = true;
            }
        }

        @SuppressLint({"UntrackedBindService"})
        private final synchronized void zzgk(String str) {
            if (this.started) {
                if (Log.isLoggable("WearableLS", 2)) {
                    String valueOf = String.valueOf(this.zzbRx.zzbRq);
                    Log.v("WearableLS", new StringBuilder((String.valueOf(str).length() + 17) + String.valueOf(valueOf).length()).append("unbindService: ").append(str).append(", ").append(valueOf).toString());
                }
                try {
                    this.zzbRx.unbindService(this.zzbRw);
                } catch (Throwable e) {
                    Log.e("WearableLS", "Exception when unbinding from local service", e);
                }
                this.started = false;
            }
        }

        public final void dispatchMessage(Message message) {
            zzDW();
            try {
                super.dispatchMessage(message);
            } finally {
                if (!hasMessages(0)) {
                    zzgk("dispatch");
                }
            }
        }

        final void quit() {
            getLooper().quit();
            zzgk("quit");
        }
    }

    final class zzc extends zzdl {
        final /* synthetic */ WearableListenerService zzbRx;
        private volatile int zzbRy;

        private zzc(WearableListenerService wearableListenerService) {
            this.zzbRx = wearableListenerService;
            this.zzbRy = -1;
        }

        private final boolean zza(Runnable runnable, String str, Object obj) {
            if (Log.isLoggable("WearableLS", 3)) {
                Log.d("WearableLS", String.format("%s: %s %s", new Object[]{str, this.zzbRx.zzbRq.toString(), obj}));
            }
            int callingUid = Binder.getCallingUid();
            if (callingUid == this.zzbRy) {
                callingUid = 1;
            } else if (zzgh.zzbz(this.zzbRx).zzgm("com.google.android.wearable.app.cn") && zzw.zzb(this.zzbRx, callingUid, "com.google.android.wearable.app.cn")) {
                this.zzbRy = callingUid;
                callingUid = 1;
            } else if (zzw.zzf(this.zzbRx, callingUid)) {
                this.zzbRy = callingUid;
                callingUid = 1;
            } else {
                Log.e("WearableLS", "Caller is not GooglePlayServices; caller UID: " + callingUid);
                boolean z = false;
            }
            if (callingUid == 0) {
                return false;
            }
            synchronized (this.zzbRx.zzbRu) {
                if (this.zzbRx.zzbRv) {
                    return false;
                }
                this.zzbRx.zzbRr.post(runnable);
                return true;
            }
        }

        public final void onConnectedNodes(List<zzeg> list) {
            zza(new zzp(this, list), "onConnectedNodes", list);
        }

        public final void zzS(DataHolder dataHolder) {
            Runnable com_google_android_gms_wearable_zzl = new zzl(this, dataHolder);
            try {
                String valueOf = String.valueOf(dataHolder);
                if (!zza(com_google_android_gms_wearable_zzl, "onDataItemChanged", new StringBuilder(String.valueOf(valueOf).length() + 18).append(valueOf).append(", rows=").append(dataHolder.getCount()).toString())) {
                }
            } finally {
                dataHolder.close();
            }
        }

        public final void zza(zzaa com_google_android_gms_wearable_internal_zzaa) {
            zza(new zzq(this, com_google_android_gms_wearable_internal_zzaa), "onConnectedCapabilityChanged", com_google_android_gms_wearable_internal_zzaa);
        }

        public final void zza(zzai com_google_android_gms_wearable_internal_zzai) {
            zza(new zzt(this, com_google_android_gms_wearable_internal_zzai), "onChannelEvent", com_google_android_gms_wearable_internal_zzai);
        }

        public final void zza(zzdx com_google_android_gms_wearable_internal_zzdx) {
            zza(new zzm(this, com_google_android_gms_wearable_internal_zzdx), "onMessageReceived", com_google_android_gms_wearable_internal_zzdx);
        }

        public final void zza(zzeg com_google_android_gms_wearable_internal_zzeg) {
            zza(new zzn(this, com_google_android_gms_wearable_internal_zzeg), "onPeerConnected", com_google_android_gms_wearable_internal_zzeg);
        }

        public final void zza(zzi com_google_android_gms_wearable_internal_zzi) {
            zza(new zzs(this, com_google_android_gms_wearable_internal_zzi), "onEntityUpdate", com_google_android_gms_wearable_internal_zzi);
        }

        public final void zza(zzl com_google_android_gms_wearable_internal_zzl) {
            zza(new zzr(this, com_google_android_gms_wearable_internal_zzl), "onNotificationReceived", com_google_android_gms_wearable_internal_zzl);
        }

        public final void zzb(zzeg com_google_android_gms_wearable_internal_zzeg) {
            zza(new zzo(this, com_google_android_gms_wearable_internal_zzeg), "onPeerDisconnected", com_google_android_gms_wearable_internal_zzeg);
        }
    }

    public Looper getLooper() {
        if (this.zzbRt == null) {
            HandlerThread handlerThread = new HandlerThread("WearableListenerService");
            handlerThread.start();
            this.zzbRt = handlerThread.getLooper();
        }
        return this.zzbRt;
    }

    public final IBinder onBind(Intent intent) {
        return BIND_LISTENER_INTENT_ACTION.equals(intent.getAction()) ? this.zzaHj : null;
    }

    public void onCapabilityChanged(CapabilityInfo capabilityInfo) {
    }

    public void onChannelClosed(Channel channel, int i, int i2) {
    }

    public void onChannelOpened(Channel channel) {
    }

    public void onConnectedNodes(List<Node> list) {
    }

    public void onCreate() {
        super.onCreate();
        this.zzbRq = new ComponentName(this, getClass().getName());
        if (Log.isLoggable("WearableLS", 3)) {
            String valueOf = String.valueOf(this.zzbRq);
            Log.d("WearableLS", new StringBuilder(String.valueOf(valueOf).length() + 10).append("onCreate: ").append(valueOf).toString());
        }
        this.zzbRr = new zzb(this, getLooper());
        this.zzbRs = new Intent(BIND_LISTENER_INTENT_ACTION);
        this.zzbRs.setComponent(this.zzbRq);
        this.zzaHj = new zzc();
    }

    public void onDataChanged(DataEventBuffer dataEventBuffer) {
    }

    public void onDestroy() {
        if (Log.isLoggable("WearableLS", 3)) {
            String valueOf = String.valueOf(this.zzbRq);
            Log.d("WearableLS", new StringBuilder(String.valueOf(valueOf).length() + 11).append("onDestroy: ").append(valueOf).toString());
        }
        synchronized (this.zzbRu) {
            this.zzbRv = true;
            if (this.zzbRr == null) {
                String valueOf2 = String.valueOf(this.zzbRq);
                throw new IllegalStateException(new StringBuilder(String.valueOf(valueOf2).length() + 111).append("onDestroy: mServiceHandler not set, did you override onCreate() but forget to call super.onCreate()? component=").append(valueOf2).toString());
            } else {
                this.zzbRr.quit();
            }
        }
        super.onDestroy();
    }

    public void onEntityUpdate(zzb com_google_android_gms_wearable_zzb) {
    }

    public void onInputClosed(Channel channel, int i, int i2) {
    }

    public void onMessageReceived(MessageEvent messageEvent) {
    }

    public void onNotificationReceived(zzd com_google_android_gms_wearable_zzd) {
    }

    public void onOutputClosed(Channel channel, int i, int i2) {
    }

    public void onPeerConnected(Node node) {
    }

    public void onPeerDisconnected(Node node) {
    }
}
