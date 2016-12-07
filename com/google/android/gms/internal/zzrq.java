package com.google.android.gms.internal;

import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.zzf;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public class zzrq {
    private static final zzqe<?>[] zt = new zzqe[0];
    private final zze vC;
    private final Map<com.google.android.gms.common.api.Api.zzc<?>, zze> xW;
    final Set<zzqe<?>> zu;
    private final zzb zv;
    private zzc zw;

    interface zzb {
        void zzc(zzqe<?> com_google_android_gms_internal_zzqe_);
    }

    interface zzc {
        void zzask();
    }

    private static class zza implements DeathRecipient, zzb {
        private final WeakReference<IBinder> zA;
        private final WeakReference<zzqe<?>> zy;
        private final WeakReference<zzf> zz;

        private zza(zzqe<?> com_google_android_gms_internal_zzqe_, zzf com_google_android_gms_common_api_zzf, IBinder iBinder) {
            this.zz = new WeakReference(com_google_android_gms_common_api_zzf);
            this.zy = new WeakReference(com_google_android_gms_internal_zzqe_);
            this.zA = new WeakReference(iBinder);
        }

        private void zzasd() {
            zzqe com_google_android_gms_internal_zzqe = (zzqe) this.zy.get();
            zzf com_google_android_gms_common_api_zzf = (zzf) this.zz.get();
            if (!(com_google_android_gms_common_api_zzf == null || com_google_android_gms_internal_zzqe == null)) {
                com_google_android_gms_common_api_zzf.remove(com_google_android_gms_internal_zzqe.zzaqf().intValue());
            }
            IBinder iBinder = (IBinder) this.zA.get();
            if (iBinder != null) {
                iBinder.unlinkToDeath(this, 0);
            }
        }

        public void binderDied() {
            zzasd();
        }

        public void zzc(zzqe<?> com_google_android_gms_internal_zzqe_) {
            zzasd();
        }
    }

    public zzrq(zze com_google_android_gms_common_api_Api_zze) {
        this.zu = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap()));
        this.zv = new zzb(this) {
            final /* synthetic */ zzrq zx;

            {
                this.zx = r1;
            }

            public void zzc(zzqe<?> com_google_android_gms_internal_zzqe_) {
                this.zx.zu.remove(com_google_android_gms_internal_zzqe_);
                if (!(com_google_android_gms_internal_zzqe_.zzaqf() == null || null == null)) {
                    null.remove(com_google_android_gms_internal_zzqe_.zzaqf().intValue());
                }
                if (this.zx.zw != null && this.zx.zu.isEmpty()) {
                    this.zx.zw.zzask();
                }
            }
        };
        this.zw = null;
        this.xW = null;
        this.vC = com_google_android_gms_common_api_Api_zze;
    }

    public zzrq(Map<com.google.android.gms.common.api.Api.zzc<?>, zze> map) {
        this.zu = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap()));
        this.zv = /* anonymous class already generated */;
        this.zw = null;
        this.xW = map;
        this.vC = null;
    }

    private static void zza(zzqe<?> com_google_android_gms_internal_zzqe_, zzf com_google_android_gms_common_api_zzf, IBinder iBinder) {
        if (com_google_android_gms_internal_zzqe_.isReady()) {
            com_google_android_gms_internal_zzqe_.zza(new zza(com_google_android_gms_internal_zzqe_, com_google_android_gms_common_api_zzf, iBinder));
        } else if (iBinder == null || !iBinder.isBinderAlive()) {
            com_google_android_gms_internal_zzqe_.zza(null);
            com_google_android_gms_internal_zzqe_.cancel();
            com_google_android_gms_common_api_zzf.remove(com_google_android_gms_internal_zzqe_.zzaqf().intValue());
        } else {
            zzb com_google_android_gms_internal_zzrq_zza = new zza(com_google_android_gms_internal_zzqe_, com_google_android_gms_common_api_zzf, iBinder);
            com_google_android_gms_internal_zzqe_.zza(com_google_android_gms_internal_zzrq_zza);
            try {
                iBinder.linkToDeath(com_google_android_gms_internal_zzrq_zza, 0);
            } catch (RemoteException e) {
                com_google_android_gms_internal_zzqe_.cancel();
                com_google_android_gms_common_api_zzf.remove(com_google_android_gms_internal_zzqe_.zzaqf().intValue());
            }
        }
    }

    public void dump(PrintWriter printWriter) {
        printWriter.append(" mUnconsumedApiCalls.size()=").println(this.zu.size());
    }

    public void release() {
        for (zzqe com_google_android_gms_internal_zzqe : (zzqe[]) this.zu.toArray(zt)) {
            com_google_android_gms_internal_zzqe.zza(null);
            if (com_google_android_gms_internal_zzqe.zzaqf() != null) {
                IBinder zzaps;
                com_google_android_gms_internal_zzqe.zzaqs();
                if (this.vC != null) {
                    zzaps = this.vC.zzaps();
                } else if (this.xW != null) {
                    zzaps = ((zze) this.xW.get(((com.google.android.gms.internal.zzqc.zza) com_google_android_gms_internal_zzqe).zzapp())).zzaps();
                } else {
                    Log.wtf("UnconsumedApiCalls", "Could not get service broker binder", new Exception());
                    zzaps = null;
                }
                zza(com_google_android_gms_internal_zzqe, null, zzaps);
                this.zu.remove(com_google_android_gms_internal_zzqe);
            } else if (com_google_android_gms_internal_zzqe.zzaqq()) {
                this.zu.remove(com_google_android_gms_internal_zzqe);
            }
        }
    }

    public void zza(zzc com_google_android_gms_internal_zzrq_zzc) {
        if (this.zu.isEmpty()) {
            com_google_android_gms_internal_zzrq_zzc.zzask();
        }
        this.zw = com_google_android_gms_internal_zzrq_zzc;
    }

    public void zzasw() {
        for (zzqe zzaa : (zzqe[]) this.zu.toArray(zt)) {
            zzaa.zzaa(new Status(8, "The connection to Google Play services was lost"));
        }
    }

    public boolean zzasx() {
        for (zzqe isReady : (zzqe[]) this.zu.toArray(zt)) {
            if (!isReady.isReady()) {
                return true;
            }
        }
        return false;
    }

    void zzb(zzqe<? extends Result> com_google_android_gms_internal_zzqe__extends_com_google_android_gms_common_api_Result) {
        this.zu.add(com_google_android_gms_internal_zzqe__extends_com_google_android_gms_common_api_Result);
        com_google_android_gms_internal_zzqe__extends_com_google_android_gms_common_api_Result.zza(this.zv);
    }
}
