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

public class zzaby {
    public static final Status zzaDu = new Status(8, "The connection to Google Play services was lost");
    private static final zzaaf<?>[] zzaDv = new zzaaf[0];
    private final Map<zzc<?>, zze> zzaBQ;
    final Set<zzaaf<?>> zzaDw = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap()));
    private final zzb zzaDx = new zzb(this) {
        final /* synthetic */ zzaby zzaDy;

        {
            this.zzaDy = r1;
        }

        public void zzc(zzaaf<?> com_google_android_gms_internal_zzaaf_) {
            this.zzaDy.zzaDw.remove(com_google_android_gms_internal_zzaaf_);
            if (com_google_android_gms_internal_zzaaf_.zzvr() != null) {
                null;
            }
        }
    };

    interface zzb {
        void zzc(zzaaf<?> com_google_android_gms_internal_zzaaf_);
    }

    private static class zza implements DeathRecipient, zzb {
        private final WeakReference<zzf> zzaDA;
        private final WeakReference<IBinder> zzaDB;
        private final WeakReference<zzaaf<?>> zzaDz;

        private zza(zzaaf<?> com_google_android_gms_internal_zzaaf_, zzf com_google_android_gms_common_api_zzf, IBinder iBinder) {
            this.zzaDA = new WeakReference(com_google_android_gms_common_api_zzf);
            this.zzaDz = new WeakReference(com_google_android_gms_internal_zzaaf_);
            this.zzaDB = new WeakReference(iBinder);
        }

        private void zzxe() {
            zzaaf com_google_android_gms_internal_zzaaf = (zzaaf) this.zzaDz.get();
            zzf com_google_android_gms_common_api_zzf = (zzf) this.zzaDA.get();
            if (!(com_google_android_gms_common_api_zzf == null || com_google_android_gms_internal_zzaaf == null)) {
                com_google_android_gms_common_api_zzf.remove(com_google_android_gms_internal_zzaaf.zzvr().intValue());
            }
            IBinder iBinder = (IBinder) this.zzaDB.get();
            if (iBinder != null) {
                iBinder.unlinkToDeath(this, 0);
            }
        }

        public void binderDied() {
            zzxe();
        }

        public void zzc(zzaaf<?> com_google_android_gms_internal_zzaaf_) {
            zzxe();
        }
    }

    public zzaby(Map<zzc<?>, zze> map) {
        this.zzaBQ = map;
    }

    private static void zza(zzaaf<?> com_google_android_gms_internal_zzaaf_, zzf com_google_android_gms_common_api_zzf, IBinder iBinder) {
        if (com_google_android_gms_internal_zzaaf_.isReady()) {
            com_google_android_gms_internal_zzaaf_.zza(new zza(com_google_android_gms_internal_zzaaf_, com_google_android_gms_common_api_zzf, iBinder));
        } else if (iBinder == null || !iBinder.isBinderAlive()) {
            com_google_android_gms_internal_zzaaf_.zza(null);
            com_google_android_gms_internal_zzaaf_.cancel();
            com_google_android_gms_common_api_zzf.remove(com_google_android_gms_internal_zzaaf_.zzvr().intValue());
        } else {
            zzb com_google_android_gms_internal_zzaby_zza = new zza(com_google_android_gms_internal_zzaaf_, com_google_android_gms_common_api_zzf, iBinder);
            com_google_android_gms_internal_zzaaf_.zza(com_google_android_gms_internal_zzaby_zza);
            try {
                iBinder.linkToDeath(com_google_android_gms_internal_zzaby_zza, 0);
            } catch (RemoteException e) {
                com_google_android_gms_internal_zzaaf_.cancel();
                com_google_android_gms_common_api_zzf.remove(com_google_android_gms_internal_zzaaf_.zzvr().intValue());
            }
        }
    }

    public void dump(PrintWriter printWriter) {
        printWriter.append(" mUnconsumedApiCalls.size()=").println(this.zzaDw.size());
    }

    public void release() {
        for (zzaaf com_google_android_gms_internal_zzaaf : (zzaaf[]) this.zzaDw.toArray(zzaDv)) {
            com_google_android_gms_internal_zzaaf.zza(null);
            if (com_google_android_gms_internal_zzaaf.zzvr() != null) {
                com_google_android_gms_internal_zzaaf.zzvH();
                zza(com_google_android_gms_internal_zzaaf, null, ((zze) this.zzaBQ.get(((com.google.android.gms.internal.zzaad.zza) com_google_android_gms_internal_zzaaf).zzvg())).zzvi());
                this.zzaDw.remove(com_google_android_gms_internal_zzaaf);
            } else if (com_google_android_gms_internal_zzaaf.zzvF()) {
                this.zzaDw.remove(com_google_android_gms_internal_zzaaf);
            }
        }
    }

    void zzb(zzaaf<? extends Result> com_google_android_gms_internal_zzaaf__extends_com_google_android_gms_common_api_Result) {
        this.zzaDw.add(com_google_android_gms_internal_zzaaf__extends_com_google_android_gms_common_api_Result);
        com_google_android_gms_internal_zzaaf__extends_com_google_android_gms_common_api_Result.zza(this.zzaDx);
    }

    public void zzxd() {
        for (zzaaf zzC : (zzaaf[]) this.zzaDw.toArray(zzaDv)) {
            zzC.zzC(zzaDu);
        }
    }
}
