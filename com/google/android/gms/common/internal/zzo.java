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
import org.telegram.messenger.exoplayer.hls.HlsChunkSource;

final class zzo extends zzn implements Callback {
    private final HashMap<zza, zzb> CB = new HashMap();
    private final com.google.android.gms.common.stats.zzb CC;
    private final long CD;
    private final Handler mHandler;
    private final Context zzask;

    private static final class zza {
        private final String CE;
        private final ComponentName CF;
        private final String V;

        public zza(ComponentName componentName) {
            this.V = null;
            this.CE = null;
            this.CF = (ComponentName) zzac.zzy(componentName);
        }

        public zza(String str, String str2) {
            this.V = zzac.zzhz(str);
            this.CE = zzac.zzhz(str2);
            this.CF = null;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof zza)) {
                return false;
            }
            zza com_google_android_gms_common_internal_zzo_zza = (zza) obj;
            return zzab.equal(this.V, com_google_android_gms_common_internal_zzo_zza.V) && zzab.equal(this.CF, com_google_android_gms_common_internal_zzo_zza.CF);
        }

        public int hashCode() {
            return zzab.hashCode(this.V, this.CF);
        }

        public String toString() {
            return this.V == null ? this.CF.flattenToString() : this.V;
        }

        public Intent zzauv() {
            return this.V != null ? new Intent(this.V).setPackage(this.CE) : new Intent().setComponent(this.CF);
        }
    }

    private final class zzb {
        private IBinder Bz;
        private ComponentName CF;
        private final zza CG = new zza(this);
        private final Set<ServiceConnection> CH = new HashSet();
        private boolean CI;
        private final zza CJ;
        final /* synthetic */ zzo CK;
        private int mState = 2;

        public class zza implements ServiceConnection {
            final /* synthetic */ zzb CL;

            public zza(zzb com_google_android_gms_common_internal_zzo_zzb) {
                this.CL = com_google_android_gms_common_internal_zzo_zzb;
            }

            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                synchronized (this.CL.CK.CB) {
                    this.CL.Bz = iBinder;
                    this.CL.CF = componentName;
                    for (ServiceConnection onServiceConnected : this.CL.CH) {
                        onServiceConnected.onServiceConnected(componentName, iBinder);
                    }
                    this.CL.mState = 1;
                }
            }

            public void onServiceDisconnected(ComponentName componentName) {
                synchronized (this.CL.CK.CB) {
                    this.CL.Bz = null;
                    this.CL.CF = componentName;
                    for (ServiceConnection onServiceDisconnected : this.CL.CH) {
                        onServiceDisconnected.onServiceDisconnected(componentName);
                    }
                    this.CL.mState = 2;
                }
            }
        }

        public zzb(zzo com_google_android_gms_common_internal_zzo, zza com_google_android_gms_common_internal_zzo_zza) {
            this.CK = com_google_android_gms_common_internal_zzo;
            this.CJ = com_google_android_gms_common_internal_zzo_zza;
        }

        public IBinder getBinder() {
            return this.Bz;
        }

        public ComponentName getComponentName() {
            return this.CF;
        }

        public int getState() {
            return this.mState;
        }

        public boolean isBound() {
            return this.CI;
        }

        public void zza(ServiceConnection serviceConnection, String str) {
            this.CK.CC.zza(this.CK.zzask, serviceConnection, str, this.CJ.zzauv());
            this.CH.add(serviceConnection);
        }

        public boolean zza(ServiceConnection serviceConnection) {
            return this.CH.contains(serviceConnection);
        }

        public boolean zzauw() {
            return this.CH.isEmpty();
        }

        public void zzb(ServiceConnection serviceConnection, String str) {
            this.CK.CC.zzb(this.CK.zzask, serviceConnection);
            this.CH.remove(serviceConnection);
        }

        @TargetApi(14)
        public void zzhu(String str) {
            this.mState = 3;
            this.CI = this.CK.CC.zza(this.CK.zzask, str, this.CJ.zzauv(), this.CG, 129);
            if (!this.CI) {
                this.mState = 2;
                try {
                    this.CK.CC.zza(this.CK.zzask, this.CG);
                } catch (IllegalArgumentException e) {
                }
            }
        }

        public void zzhv(String str) {
            this.CK.CC.zza(this.CK.zzask, this.CG);
            this.CI = false;
            this.mState = 2;
        }
    }

    zzo(Context context) {
        this.zzask = context.getApplicationContext();
        this.mHandler = new Handler(context.getMainLooper(), this);
        this.CC = com.google.android.gms.common.stats.zzb.zzawu();
        this.CD = HlsChunkSource.DEFAULT_MIN_BUFFER_TO_SWITCH_UP_MS;
    }

    private boolean zza(zza com_google_android_gms_common_internal_zzo_zza, ServiceConnection serviceConnection, String str) {
        boolean isBound;
        zzac.zzb((Object) serviceConnection, (Object) "ServiceConnection must not be null");
        synchronized (this.CB) {
            zzb com_google_android_gms_common_internal_zzo_zzb = (zzb) this.CB.get(com_google_android_gms_common_internal_zzo_zza);
            if (com_google_android_gms_common_internal_zzo_zzb != null) {
                this.mHandler.removeMessages(0, com_google_android_gms_common_internal_zzo_zzb);
                if (!com_google_android_gms_common_internal_zzo_zzb.zza(serviceConnection)) {
                    com_google_android_gms_common_internal_zzo_zzb.zza(serviceConnection, str);
                    switch (com_google_android_gms_common_internal_zzo_zzb.getState()) {
                        case 1:
                            serviceConnection.onServiceConnected(com_google_android_gms_common_internal_zzo_zzb.getComponentName(), com_google_android_gms_common_internal_zzo_zzb.getBinder());
                            break;
                        case 2:
                            com_google_android_gms_common_internal_zzo_zzb.zzhu(str);
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
            com_google_android_gms_common_internal_zzo_zzb.zzhu(str);
            this.CB.put(com_google_android_gms_common_internal_zzo_zza, com_google_android_gms_common_internal_zzo_zzb);
            isBound = com_google_android_gms_common_internal_zzo_zzb.isBound();
        }
        return isBound;
    }

    private void zzb(zza com_google_android_gms_common_internal_zzo_zza, ServiceConnection serviceConnection, String str) {
        zzac.zzb((Object) serviceConnection, (Object) "ServiceConnection must not be null");
        synchronized (this.CB) {
            zzb com_google_android_gms_common_internal_zzo_zzb = (zzb) this.CB.get(com_google_android_gms_common_internal_zzo_zza);
            String valueOf;
            if (com_google_android_gms_common_internal_zzo_zzb == null) {
                valueOf = String.valueOf(com_google_android_gms_common_internal_zzo_zza);
                throw new IllegalStateException(new StringBuilder(String.valueOf(valueOf).length() + 50).append("Nonexistent connection status for service config: ").append(valueOf).toString());
            } else if (com_google_android_gms_common_internal_zzo_zzb.zza(serviceConnection)) {
                com_google_android_gms_common_internal_zzo_zzb.zzb(serviceConnection, str);
                if (com_google_android_gms_common_internal_zzo_zzb.zzauw()) {
                    this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(0, com_google_android_gms_common_internal_zzo_zzb), this.CD);
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
                zzb com_google_android_gms_common_internal_zzo_zzb = (zzb) message.obj;
                synchronized (this.CB) {
                    if (com_google_android_gms_common_internal_zzo_zzb.zzauw()) {
                        if (com_google_android_gms_common_internal_zzo_zzb.isBound()) {
                            com_google_android_gms_common_internal_zzo_zzb.zzhv("GmsClientSupervisor");
                        }
                        this.CB.remove(com_google_android_gms_common_internal_zzo_zzb.CJ);
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
