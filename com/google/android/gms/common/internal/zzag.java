package com.google.android.gms.common.internal;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import com.google.android.gms.common.stats.zza;
import java.util.HashMap;
import org.telegram.messenger.exoplayer2.DefaultRenderersFactory;

final class zzag extends zzae implements Callback {
    private final Context mApplicationContext;
    private final Handler mHandler;
    private final HashMap<zzaf, zzah> zzaHP = new HashMap();
    private final zza zzaHQ;
    private final long zzaHR;
    private final long zzaHS;

    zzag(Context context) {
        this.mApplicationContext = context.getApplicationContext();
        this.mHandler = new Handler(context.getMainLooper(), this);
        this.zzaHQ = zza.zzrU();
        this.zzaHR = DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS;
        this.zzaHS = 300000;
    }

    public final boolean handleMessage(Message message) {
        zzaf com_google_android_gms_common_internal_zzaf;
        zzah com_google_android_gms_common_internal_zzah;
        switch (message.what) {
            case 0:
                synchronized (this.zzaHP) {
                    com_google_android_gms_common_internal_zzaf = (zzaf) message.obj;
                    com_google_android_gms_common_internal_zzah = (zzah) this.zzaHP.get(com_google_android_gms_common_internal_zzaf);
                    if (com_google_android_gms_common_internal_zzah != null && com_google_android_gms_common_internal_zzah.zzrC()) {
                        if (com_google_android_gms_common_internal_zzah.isBound()) {
                            com_google_android_gms_common_internal_zzah.zzcC("GmsClientSupervisor");
                        }
                        this.zzaHP.remove(com_google_android_gms_common_internal_zzaf);
                    }
                }
                return true;
            case 1:
                synchronized (this.zzaHP) {
                    com_google_android_gms_common_internal_zzaf = (zzaf) message.obj;
                    com_google_android_gms_common_internal_zzah = (zzah) this.zzaHP.get(com_google_android_gms_common_internal_zzaf);
                    if (com_google_android_gms_common_internal_zzah != null && com_google_android_gms_common_internal_zzah.getState() == 3) {
                        String valueOf = String.valueOf(com_google_android_gms_common_internal_zzaf);
                        Log.wtf("GmsClientSupervisor", new StringBuilder(String.valueOf(valueOf).length() + 47).append("Timeout waiting for ServiceConnection callback ").append(valueOf).toString(), new Exception());
                        ComponentName componentName = com_google_android_gms_common_internal_zzah.getComponentName();
                        if (componentName == null) {
                            componentName = com_google_android_gms_common_internal_zzaf.getComponentName();
                        }
                        com_google_android_gms_common_internal_zzah.onServiceDisconnected(componentName == null ? new ComponentName(com_google_android_gms_common_internal_zzaf.getPackage(), "unknown") : componentName);
                    }
                }
                return true;
            default:
                return false;
        }
    }

    protected final boolean zza(zzaf com_google_android_gms_common_internal_zzaf, ServiceConnection serviceConnection, String str) {
        boolean isBound;
        zzbo.zzb((Object) serviceConnection, (Object) "ServiceConnection must not be null");
        synchronized (this.zzaHP) {
            zzah com_google_android_gms_common_internal_zzah = (zzah) this.zzaHP.get(com_google_android_gms_common_internal_zzaf);
            if (com_google_android_gms_common_internal_zzah != null) {
                this.mHandler.removeMessages(0, com_google_android_gms_common_internal_zzaf);
                if (!com_google_android_gms_common_internal_zzah.zza(serviceConnection)) {
                    com_google_android_gms_common_internal_zzah.zza(serviceConnection, str);
                    switch (com_google_android_gms_common_internal_zzah.getState()) {
                        case 1:
                            serviceConnection.onServiceConnected(com_google_android_gms_common_internal_zzah.getComponentName(), com_google_android_gms_common_internal_zzah.getBinder());
                            break;
                        case 2:
                            com_google_android_gms_common_internal_zzah.zzcB(str);
                            break;
                        default:
                            break;
                    }
                }
                String valueOf = String.valueOf(com_google_android_gms_common_internal_zzaf);
                throw new IllegalStateException(new StringBuilder(String.valueOf(valueOf).length() + 81).append("Trying to bind a GmsServiceConnection that was already connected before.  config=").append(valueOf).toString());
            }
            com_google_android_gms_common_internal_zzah = new zzah(this, com_google_android_gms_common_internal_zzaf);
            com_google_android_gms_common_internal_zzah.zza(serviceConnection, str);
            com_google_android_gms_common_internal_zzah.zzcB(str);
            this.zzaHP.put(com_google_android_gms_common_internal_zzaf, com_google_android_gms_common_internal_zzah);
            isBound = com_google_android_gms_common_internal_zzah.isBound();
        }
        return isBound;
    }

    protected final void zzb(zzaf com_google_android_gms_common_internal_zzaf, ServiceConnection serviceConnection, String str) {
        zzbo.zzb((Object) serviceConnection, (Object) "ServiceConnection must not be null");
        synchronized (this.zzaHP) {
            zzah com_google_android_gms_common_internal_zzah = (zzah) this.zzaHP.get(com_google_android_gms_common_internal_zzaf);
            String valueOf;
            if (com_google_android_gms_common_internal_zzah == null) {
                valueOf = String.valueOf(com_google_android_gms_common_internal_zzaf);
                throw new IllegalStateException(new StringBuilder(String.valueOf(valueOf).length() + 50).append("Nonexistent connection status for service config: ").append(valueOf).toString());
            } else if (com_google_android_gms_common_internal_zzah.zza(serviceConnection)) {
                com_google_android_gms_common_internal_zzah.zzb(serviceConnection, str);
                if (com_google_android_gms_common_internal_zzah.zzrC()) {
                    this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(0, com_google_android_gms_common_internal_zzaf), this.zzaHR);
                }
            } else {
                valueOf = String.valueOf(com_google_android_gms_common_internal_zzaf);
                throw new IllegalStateException(new StringBuilder(String.valueOf(valueOf).length() + 76).append("Trying to unbind a GmsServiceConnection  that was not bound before.  config=").append(valueOf).toString());
            }
        }
    }
}
