package com.google.android.gms.internal;

import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.RemoteException;
import com.google.android.gms.common.api.Api.zzc;
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

public class zzabq {
    public static final Status zzaBV = new Status(8, "The connection to Google Play services was lost");
    private static final zzzx<?>[] zzaBW = new zzzx[0];
    private final Map<zzc<?>, zze> zzaAr;
    final Set<zzzx<?>> zzaBX = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap()));
    private final zzb zzaBY = new zzb(this) {
        final /* synthetic */ zzabq zzaBZ;

        {
            this.zzaBZ = r1;
        }

        public void zzc(zzzx<?> com_google_android_gms_internal_zzzx_) {
            this.zzaBZ.zzaBX.remove(com_google_android_gms_internal_zzzx_);
            if (com_google_android_gms_internal_zzzx_.zzuR() != null) {
                null;
            }
        }
    };

    interface zzb {
        void zzc(zzzx<?> com_google_android_gms_internal_zzzx_);
    }

    private static class zza implements DeathRecipient, zzb {
        private final WeakReference<zzzx<?>> zzaCa;
        private final WeakReference<zzf> zzaCb;
        private final WeakReference<IBinder> zzaCc;

        private zza(zzzx<?> com_google_android_gms_internal_zzzx_, zzf com_google_android_gms_common_api_zzf, IBinder iBinder) {
            this.zzaCb = new WeakReference(com_google_android_gms_common_api_zzf);
            this.zzaCa = new WeakReference(com_google_android_gms_internal_zzzx_);
            this.zzaCc = new WeakReference(iBinder);
        }

        private void zzwx() {
            zzzx com_google_android_gms_internal_zzzx = (zzzx) this.zzaCa.get();
            zzf com_google_android_gms_common_api_zzf = (zzf) this.zzaCb.get();
            if (!(com_google_android_gms_common_api_zzf == null || com_google_android_gms_internal_zzzx == null)) {
                com_google_android_gms_common_api_zzf.remove(com_google_android_gms_internal_zzzx.zzuR().intValue());
            }
            IBinder iBinder = (IBinder) this.zzaCc.get();
            if (iBinder != null) {
                iBinder.unlinkToDeath(this, 0);
            }
        }

        public void binderDied() {
            zzwx();
        }

        public void zzc(zzzx<?> com_google_android_gms_internal_zzzx_) {
            zzwx();
        }
    }

    public zzabq(Map<zzc<?>, zze> map) {
        this.zzaAr = map;
    }

    private static void zza(zzzx<?> com_google_android_gms_internal_zzzx_, zzf com_google_android_gms_common_api_zzf, IBinder iBinder) {
        if (com_google_android_gms_internal_zzzx_.isReady()) {
            com_google_android_gms_internal_zzzx_.zza(new zza(com_google_android_gms_internal_zzzx_, com_google_android_gms_common_api_zzf, iBinder));
        } else if (iBinder == null || !iBinder.isBinderAlive()) {
            com_google_android_gms_internal_zzzx_.zza(null);
            com_google_android_gms_internal_zzzx_.cancel();
            com_google_android_gms_common_api_zzf.remove(com_google_android_gms_internal_zzzx_.zzuR().intValue());
        } else {
            zzb com_google_android_gms_internal_zzabq_zza = new zza(com_google_android_gms_internal_zzzx_, com_google_android_gms_common_api_zzf, iBinder);
            com_google_android_gms_internal_zzzx_.zza(com_google_android_gms_internal_zzabq_zza);
            try {
                iBinder.linkToDeath(com_google_android_gms_internal_zzabq_zza, 0);
            } catch (RemoteException e) {
                com_google_android_gms_internal_zzzx_.cancel();
                com_google_android_gms_common_api_zzf.remove(com_google_android_gms_internal_zzzx_.zzuR().intValue());
            }
        }
    }

    public void dump(PrintWriter printWriter) {
        printWriter.append(" mUnconsumedApiCalls.size()=").println(this.zzaBX.size());
    }

    public void release() {
        for (zzzx com_google_android_gms_internal_zzzx : (zzzx[]) this.zzaBX.toArray(zzaBW)) {
            com_google_android_gms_internal_zzzx.zza(null);
            if (com_google_android_gms_internal_zzzx.zzuR() != null) {
                com_google_android_gms_internal_zzzx.zzve();
                zza(com_google_android_gms_internal_zzzx, null, ((zze) this.zzaAr.get(((com.google.android.gms.internal.zzzv.zza) com_google_android_gms_internal_zzzx).zzuH())).zzuJ());
                this.zzaBX.remove(com_google_android_gms_internal_zzzx);
            } else if (com_google_android_gms_internal_zzzx.zzvc()) {
                this.zzaBX.remove(com_google_android_gms_internal_zzzx);
            }
        }
    }

    void zzb(zzzx<? extends Result> com_google_android_gms_internal_zzzx__extends_com_google_android_gms_common_api_Result) {
        this.zzaBX.add(com_google_android_gms_internal_zzzx__extends_com_google_android_gms_common_api_Result);
        com_google_android_gms_internal_zzzx__extends_com_google_android_gms_common_api_Result.zza(this.zzaBY);
    }

    public void zzww() {
        for (zzzx zzB : (zzzx[]) this.zzaBX.toArray(zzaBW)) {
            zzB.zzB(zzaBV);
        }
    }
}
