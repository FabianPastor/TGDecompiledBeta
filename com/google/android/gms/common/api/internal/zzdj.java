package com.google.android.gms.common.api.internal;

import android.os.IBinder;
import android.os.RemoteException;
import com.google.android.gms.common.api.Api.zzc;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public final class zzdj {
    public static final Status zzfvg = new Status(8, "The connection to Google Play services was lost");
    private static final BasePendingResult<?>[] zzfvh = new BasePendingResult[0];
    private final Map<zzc<?>, zze> zzfsb;
    final Set<BasePendingResult<?>> zzfvi = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap()));
    private final zzdm zzfvj = new zzdk(this);

    public zzdj(Map<zzc<?>, zze> map) {
        this.zzfsb = map;
    }

    public final void release() {
        zzdm com_google_android_gms_common_api_internal_zzdm = null;
        for (PendingResult pendingResult : (BasePendingResult[]) this.zzfvi.toArray(zzfvh)) {
            pendingResult.zza(com_google_android_gms_common_api_internal_zzdm);
            if (pendingResult.zzagv() != null) {
                pendingResult.setResultCallback(com_google_android_gms_common_api_internal_zzdm);
                IBinder zzagh = ((zze) this.zzfsb.get(((zzm) pendingResult).zzagf())).zzagh();
                if (pendingResult.isReady()) {
                    pendingResult.zza(new zzdl(pendingResult, com_google_android_gms_common_api_internal_zzdm, zzagh, com_google_android_gms_common_api_internal_zzdm));
                } else if (zzagh == null || !zzagh.isBinderAlive()) {
                    pendingResult.zza(com_google_android_gms_common_api_internal_zzdm);
                    pendingResult.cancel();
                    com_google_android_gms_common_api_internal_zzdm.remove(pendingResult.zzagv().intValue());
                } else {
                    zzdm com_google_android_gms_common_api_internal_zzdl = new zzdl(pendingResult, com_google_android_gms_common_api_internal_zzdm, zzagh, com_google_android_gms_common_api_internal_zzdm);
                    pendingResult.zza(com_google_android_gms_common_api_internal_zzdl);
                    try {
                        zzagh.linkToDeath(com_google_android_gms_common_api_internal_zzdl, 0);
                    } catch (RemoteException e) {
                        pendingResult.cancel();
                        com_google_android_gms_common_api_internal_zzdm.remove(pendingResult.zzagv().intValue());
                    }
                }
                this.zzfvi.remove(pendingResult);
            } else if (pendingResult.zzahh()) {
                this.zzfvi.remove(pendingResult);
            }
        }
    }

    public final void zzaju() {
        for (BasePendingResult zzv : (BasePendingResult[]) this.zzfvi.toArray(zzfvh)) {
            zzv.zzv(zzfvg);
        }
    }

    final void zzb(BasePendingResult<? extends Result> basePendingResult) {
        this.zzfvi.add(basePendingResult);
        basePendingResult.zza(this.zzfvj);
    }
}
