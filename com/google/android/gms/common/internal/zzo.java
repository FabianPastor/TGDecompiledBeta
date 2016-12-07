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

final class zzo extends zzn implements Callback {
    private final Handler mHandler;
    private final HashMap<zza, zzb> zzaEF = new HashMap();
    private final com.google.android.gms.common.stats.zza zzaEG;
    private final long zzaEH;
    private final Context zzvZ;

    private static final class zza {
        private final String zzaEI;
        private final ComponentName zzaEJ;
        private final String zzaca;

        public zza(ComponentName componentName) {
            this.zzaca = null;
            this.zzaEI = null;
            this.zzaEJ = (ComponentName) zzac.zzw(componentName);
        }

        public zza(String str, String str2) {
            this.zzaca = zzac.zzdv(str);
            this.zzaEI = zzac.zzdv(str2);
            this.zzaEJ = null;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof zza)) {
                return false;
            }
            zza com_google_android_gms_common_internal_zzo_zza = (zza) obj;
            return zzaa.equal(this.zzaca, com_google_android_gms_common_internal_zzo_zza.zzaca) && zzaa.equal(this.zzaEJ, com_google_android_gms_common_internal_zzo_zza.zzaEJ);
        }

        public int hashCode() {
            return zzaa.hashCode(this.zzaca, this.zzaEJ);
        }

        public String toString() {
            return this.zzaca == null ? this.zzaEJ.flattenToString() : this.zzaca;
        }

        public Intent zzxs() {
            return this.zzaca != null ? new Intent(this.zzaca).setPackage(this.zzaEI) : new Intent().setComponent(this.zzaEJ);
        }
    }

    private final class zzb {
        private int mState = 2;
        private ComponentName zzaEJ;
        private final zza zzaEK = new zza(this);
        private final Set<ServiceConnection> zzaEL = new HashSet();
        private boolean zzaEM;
        private final zza zzaEN;
        final /* synthetic */ zzo zzaEO;
        private IBinder zzaEa;

        public class zza implements ServiceConnection {
            final /* synthetic */ zzb zzaEP;

            public zza(zzb com_google_android_gms_common_internal_zzo_zzb) {
                this.zzaEP = com_google_android_gms_common_internal_zzo_zzb;
            }

            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                synchronized (this.zzaEP.zzaEO.zzaEF) {
                    this.zzaEP.zzaEa = iBinder;
                    this.zzaEP.zzaEJ = componentName;
                    for (ServiceConnection onServiceConnected : this.zzaEP.zzaEL) {
                        onServiceConnected.onServiceConnected(componentName, iBinder);
                    }
                    this.zzaEP.mState = 1;
                }
            }

            public void onServiceDisconnected(ComponentName componentName) {
                synchronized (this.zzaEP.zzaEO.zzaEF) {
                    this.zzaEP.zzaEa = null;
                    this.zzaEP.zzaEJ = componentName;
                    for (ServiceConnection onServiceDisconnected : this.zzaEP.zzaEL) {
                        onServiceDisconnected.onServiceDisconnected(componentName);
                    }
                    this.zzaEP.mState = 2;
                }
            }
        }

        public zzb(zzo com_google_android_gms_common_internal_zzo, zza com_google_android_gms_common_internal_zzo_zza) {
            this.zzaEO = com_google_android_gms_common_internal_zzo;
            this.zzaEN = com_google_android_gms_common_internal_zzo_zza;
        }

        public IBinder getBinder() {
            return this.zzaEa;
        }

        public ComponentName getComponentName() {
            return this.zzaEJ;
        }

        public int getState() {
            return this.mState;
        }

        public boolean isBound() {
            return this.zzaEM;
        }

        public void zza(ServiceConnection serviceConnection, String str) {
            this.zzaEO.zzaEG.zza(this.zzaEO.zzvZ, serviceConnection, str, this.zzaEN.zzxs());
            this.zzaEL.add(serviceConnection);
        }

        public boolean zza(ServiceConnection serviceConnection) {
            return this.zzaEL.contains(serviceConnection);
        }

        public void zzb(ServiceConnection serviceConnection, String str) {
            this.zzaEO.zzaEG.zzb(this.zzaEO.zzvZ, serviceConnection);
            this.zzaEL.remove(serviceConnection);
        }

        @TargetApi(14)
        public void zzdr(String str) {
            this.mState = 3;
            this.zzaEM = this.zzaEO.zzaEG.zza(this.zzaEO.zzvZ, str, this.zzaEN.zzxs(), this.zzaEK, TsExtractor.TS_STREAM_TYPE_AC3);
            if (!this.zzaEM) {
                this.mState = 2;
                try {
                    this.zzaEO.zzaEG.zza(this.zzaEO.zzvZ, this.zzaEK);
                } catch (IllegalArgumentException e) {
                }
            }
        }

        public void zzds(String str) {
            this.zzaEO.zzaEG.zza(this.zzaEO.zzvZ, this.zzaEK);
            this.zzaEM = false;
            this.mState = 2;
        }

        public boolean zzxt() {
            return this.zzaEL.isEmpty();
        }
    }

    zzo(Context context) {
        this.zzvZ = context.getApplicationContext();
        this.mHandler = new Handler(context.getMainLooper(), this);
        this.zzaEG = com.google.android.gms.common.stats.zza.zzyc();
        this.zzaEH = ExoPlayerFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS;
    }

    private boolean zza(zza com_google_android_gms_common_internal_zzo_zza, ServiceConnection serviceConnection, String str) {
        boolean isBound;
        zzac.zzb((Object) serviceConnection, (Object) "ServiceConnection must not be null");
        synchronized (this.zzaEF) {
            zzb com_google_android_gms_common_internal_zzo_zzb = (zzb) this.zzaEF.get(com_google_android_gms_common_internal_zzo_zza);
            if (com_google_android_gms_common_internal_zzo_zzb != null) {
                this.mHandler.removeMessages(0, com_google_android_gms_common_internal_zzo_zza);
                if (!com_google_android_gms_common_internal_zzo_zzb.zza(serviceConnection)) {
                    com_google_android_gms_common_internal_zzo_zzb.zza(serviceConnection, str);
                    switch (com_google_android_gms_common_internal_zzo_zzb.getState()) {
                        case 1:
                            serviceConnection.onServiceConnected(com_google_android_gms_common_internal_zzo_zzb.getComponentName(), com_google_android_gms_common_internal_zzo_zzb.getBinder());
                            break;
                        case 2:
                            com_google_android_gms_common_internal_zzo_zzb.zzdr(str);
                            break;
                        default:
                            break;
                    }
                }
                String valueOf = String.valueOf(com_google_android_gms_common_internal_zzo_zza);
                throw new IllegalStateException(new StringBuilder(String.valueOf(valueOf).length() + 81).append("Trying to bind a GmsServiceConnection that was already connected before.  config=").append(valueOf).toString());
            }
            com_google_android_gms_common_internal_zzo_zzb = new zzb(this, com_google_android_gms_common_internal_zzo_zza);
            com_google_android_gms_common_internal_zzo_zzb.zza(serviceConnection, str);
            com_google_android_gms_common_internal_zzo_zzb.zzdr(str);
            this.zzaEF.put(com_google_android_gms_common_internal_zzo_zza, com_google_android_gms_common_internal_zzo_zzb);
            isBound = com_google_android_gms_common_internal_zzo_zzb.isBound();
        }
        return isBound;
    }

    private void zzb(zza com_google_android_gms_common_internal_zzo_zza, ServiceConnection serviceConnection, String str) {
        zzac.zzb((Object) serviceConnection, (Object) "ServiceConnection must not be null");
        synchronized (this.zzaEF) {
            zzb com_google_android_gms_common_internal_zzo_zzb = (zzb) this.zzaEF.get(com_google_android_gms_common_internal_zzo_zza);
            String valueOf;
            if (com_google_android_gms_common_internal_zzo_zzb == null) {
                valueOf = String.valueOf(com_google_android_gms_common_internal_zzo_zza);
                throw new IllegalStateException(new StringBuilder(String.valueOf(valueOf).length() + 50).append("Nonexistent connection status for service config: ").append(valueOf).toString());
            } else if (com_google_android_gms_common_internal_zzo_zzb.zza(serviceConnection)) {
                com_google_android_gms_common_internal_zzo_zzb.zzb(serviceConnection, str);
                if (com_google_android_gms_common_internal_zzo_zzb.zzxt()) {
                    this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(0, com_google_android_gms_common_internal_zzo_zza), this.zzaEH);
                }
            } else {
                valueOf = String.valueOf(com_google_android_gms_common_internal_zzo_zza);
                throw new IllegalStateException(new StringBuilder(String.valueOf(valueOf).length() + 76).append("Trying to unbind a GmsServiceConnection  that was not bound before.  config=").append(valueOf).toString());
            }
        }
    }

    public boolean handleMessage(Message message) {
        switch (message.what) {
            case 0:
                zza com_google_android_gms_common_internal_zzo_zza = (zza) message.obj;
                synchronized (this.zzaEF) {
                    zzb com_google_android_gms_common_internal_zzo_zzb = (zzb) this.zzaEF.get(com_google_android_gms_common_internal_zzo_zza);
                    if (com_google_android_gms_common_internal_zzo_zzb != null && com_google_android_gms_common_internal_zzo_zzb.zzxt()) {
                        if (com_google_android_gms_common_internal_zzo_zzb.isBound()) {
                            com_google_android_gms_common_internal_zzo_zzb.zzds("GmsClientSupervisor");
                        }
                        this.zzaEF.remove(com_google_android_gms_common_internal_zzo_zza);
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
