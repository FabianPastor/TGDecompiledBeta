package com.google.android.gms.common.internal;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.IBinder;
import android.os.Message;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.telegram.messenger.exoplayer2.ExoPlayerFactory;
import org.telegram.messenger.exoplayer2.extractor.ts.TsExtractor;

final class zzm extends zzl implements Callback {
    private final HashMap<zza, zzb> En = new HashMap();
    private final com.google.android.gms.common.stats.zza Eo;
    private final long Ep;
    private final Handler mHandler;
    private final Context zzatc;

    private static final class zza {
        private final String Eq;
        private final ComponentName Er;
        private final String cd;

        public zza(ComponentName componentName) {
            this.cd = null;
            this.Eq = null;
            this.Er = (ComponentName) zzaa.zzy(componentName);
        }

        public zza(String str, String str2) {
            this.cd = zzaa.zzib(str);
            this.Eq = zzaa.zzib(str2);
            this.Er = null;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof zza)) {
                return false;
            }
            zza com_google_android_gms_common_internal_zzm_zza = (zza) obj;
            return zzz.equal(this.cd, com_google_android_gms_common_internal_zzm_zza.cd) && zzz.equal(this.Er, com_google_android_gms_common_internal_zzm_zza.Er);
        }

        public int hashCode() {
            return zzz.hashCode(this.cd, this.Er);
        }

        public String toString() {
            return this.cd == null ? this.Er.flattenToString() : this.cd;
        }

        public Intent zzawe() {
            return this.cd != null ? new Intent(this.cd).setPackage(this.Eq) : new Intent().setComponent(this.Er);
        }
    }

    private final class zzb {
        private IBinder DI;
        private ComponentName Er;
        private final zza Es = new zza(this);
        private final Set<ServiceConnection> Et = new HashSet();
        private boolean Eu;
        private final zza Ev;
        final /* synthetic */ zzm Ew;
        private int mState = 2;

        public class zza implements ServiceConnection {
            final /* synthetic */ zzb Ex;

            public zza(zzb com_google_android_gms_common_internal_zzm_zzb) {
                this.Ex = com_google_android_gms_common_internal_zzm_zzb;
            }

            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                synchronized (this.Ex.Ew.En) {
                    this.Ex.DI = iBinder;
                    this.Ex.Er = componentName;
                    for (ServiceConnection onServiceConnected : this.Ex.Et) {
                        onServiceConnected.onServiceConnected(componentName, iBinder);
                    }
                    this.Ex.mState = 1;
                }
            }

            public void onServiceDisconnected(ComponentName componentName) {
                synchronized (this.Ex.Ew.En) {
                    this.Ex.DI = null;
                    this.Ex.Er = componentName;
                    for (ServiceConnection onServiceDisconnected : this.Ex.Et) {
                        onServiceDisconnected.onServiceDisconnected(componentName);
                    }
                    this.Ex.mState = 2;
                }
            }
        }

        public zzb(zzm com_google_android_gms_common_internal_zzm, zza com_google_android_gms_common_internal_zzm_zza) {
            this.Ew = com_google_android_gms_common_internal_zzm;
            this.Ev = com_google_android_gms_common_internal_zzm_zza;
        }

        public IBinder getBinder() {
            return this.DI;
        }

        public ComponentName getComponentName() {
            return this.Er;
        }

        public int getState() {
            return this.mState;
        }

        public boolean isBound() {
            return this.Eu;
        }

        public void zza(ServiceConnection serviceConnection, String str) {
            this.Ew.Eo.zza(this.Ew.zzatc, serviceConnection, str, this.Ev.zzawe());
            this.Et.add(serviceConnection);
        }

        public boolean zza(ServiceConnection serviceConnection) {
            return this.Et.contains(serviceConnection);
        }

        public boolean zzawf() {
            return this.Et.isEmpty();
        }

        public void zzb(ServiceConnection serviceConnection, String str) {
            this.Ew.Eo.zzb(this.Ew.zzatc, serviceConnection);
            this.Et.remove(serviceConnection);
        }

        @TargetApi(14)
        public void zzhw(String str) {
            this.mState = 3;
            this.Eu = this.Ew.Eo.zza(this.Ew.zzatc, str, this.Ev.zzawe(), this.Es, TsExtractor.TS_STREAM_TYPE_AC3);
            if (!this.Eu) {
                this.mState = 2;
                try {
                    this.Ew.Eo.zza(this.Ew.zzatc, this.Es);
                } catch (IllegalArgumentException e) {
                }
            }
        }

        public void zzhx(String str) {
            this.Ew.Eo.zza(this.Ew.zzatc, this.Es);
            this.Eu = false;
            this.mState = 2;
        }
    }

    zzm(Context context) {
        this.zzatc = context.getApplicationContext();
        this.mHandler = new Handler(context.getMainLooper(), this);
        this.Eo = com.google.android.gms.common.stats.zza.zzaxr();
        this.Ep = ExoPlayerFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS;
    }

    private boolean zza(zza com_google_android_gms_common_internal_zzm_zza, ServiceConnection serviceConnection, String str) {
        boolean isBound;
        zzaa.zzb((Object) serviceConnection, (Object) "ServiceConnection must not be null");
        synchronized (this.En) {
            zzb com_google_android_gms_common_internal_zzm_zzb = (zzb) this.En.get(com_google_android_gms_common_internal_zzm_zza);
            if (com_google_android_gms_common_internal_zzm_zzb != null) {
                this.mHandler.removeMessages(0, com_google_android_gms_common_internal_zzm_zza);
                if (!com_google_android_gms_common_internal_zzm_zzb.zza(serviceConnection)) {
                    com_google_android_gms_common_internal_zzm_zzb.zza(serviceConnection, str);
                    switch (com_google_android_gms_common_internal_zzm_zzb.getState()) {
                        case 1:
                            serviceConnection.onServiceConnected(com_google_android_gms_common_internal_zzm_zzb.getComponentName(), com_google_android_gms_common_internal_zzm_zzb.getBinder());
                            break;
                        case 2:
                            com_google_android_gms_common_internal_zzm_zzb.zzhw(str);
                            break;
                        default:
                            break;
                    }
                }
                String valueOf = String.valueOf(com_google_android_gms_common_internal_zzm_zza);
                throw new IllegalStateException(new StringBuilder(String.valueOf(valueOf).length() + 81).append("Trying to bind a GmsServiceConnection that was already connected before.  config=").append(valueOf).toString());
            }
            com_google_android_gms_common_internal_zzm_zzb = new zzb(this, com_google_android_gms_common_internal_zzm_zza);
            com_google_android_gms_common_internal_zzm_zzb.zza(serviceConnection, str);
            com_google_android_gms_common_internal_zzm_zzb.zzhw(str);
            this.En.put(com_google_android_gms_common_internal_zzm_zza, com_google_android_gms_common_internal_zzm_zzb);
            isBound = com_google_android_gms_common_internal_zzm_zzb.isBound();
        }
        return isBound;
    }

    private void zzb(zza com_google_android_gms_common_internal_zzm_zza, ServiceConnection serviceConnection, String str) {
        zzaa.zzb((Object) serviceConnection, (Object) "ServiceConnection must not be null");
        synchronized (this.En) {
            zzb com_google_android_gms_common_internal_zzm_zzb = (zzb) this.En.get(com_google_android_gms_common_internal_zzm_zza);
            String valueOf;
            if (com_google_android_gms_common_internal_zzm_zzb == null) {
                valueOf = String.valueOf(com_google_android_gms_common_internal_zzm_zza);
                throw new IllegalStateException(new StringBuilder(String.valueOf(valueOf).length() + 50).append("Nonexistent connection status for service config: ").append(valueOf).toString());
            } else if (com_google_android_gms_common_internal_zzm_zzb.zza(serviceConnection)) {
                com_google_android_gms_common_internal_zzm_zzb.zzb(serviceConnection, str);
                if (com_google_android_gms_common_internal_zzm_zzb.zzawf()) {
                    this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(0, com_google_android_gms_common_internal_zzm_zza), this.Ep);
                }
            } else {
                valueOf = String.valueOf(com_google_android_gms_common_internal_zzm_zza);
                throw new IllegalStateException(new StringBuilder(String.valueOf(valueOf).length() + 76).append("Trying to unbind a GmsServiceConnection  that was not bound before.  config=").append(valueOf).toString());
            }
        }
    }

    public boolean handleMessage(Message message) {
        switch (message.what) {
            case 0:
                zza com_google_android_gms_common_internal_zzm_zza = (zza) message.obj;
                synchronized (this.En) {
                    zzb com_google_android_gms_common_internal_zzm_zzb = (zzb) this.En.get(com_google_android_gms_common_internal_zzm_zza);
                    if (com_google_android_gms_common_internal_zzm_zzb != null && com_google_android_gms_common_internal_zzm_zzb.zzawf()) {
                        if (com_google_android_gms_common_internal_zzm_zzb.isBound()) {
                            com_google_android_gms_common_internal_zzm_zzb.zzhx("GmsClientSupervisor");
                        }
                        this.En.remove(com_google_android_gms_common_internal_zzm_zza);
                    }
                }
                return true;
            default:
                return false;
        }
    }

    public boolean zza(ComponentName componentName, ServiceConnection serviceConnection, String str) {
        return zza(new zza(componentName), serviceConnection, str);
    }

    public boolean zza(String str, String str2, ServiceConnection serviceConnection, String str3) {
        return zza(new zza(str, str2), serviceConnection, str3);
    }

    public void zzb(ComponentName componentName, ServiceConnection serviceConnection, String str) {
        zzb(new zza(componentName), serviceConnection, str);
    }

    public void zzb(String str, String str2, ServiceConnection serviceConnection, String str3) {
        zzb(new zza(str, str2), serviceConnection, str3);
    }
}
