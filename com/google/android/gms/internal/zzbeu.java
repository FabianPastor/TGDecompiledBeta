package com.google.android.gms.internal;

import com.google.android.gms.common.api.Api.zzc;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public final class zzbeu {
    public static final Status zzaFj = new Status(8, "The connection to Google Play services was lost");
    private static final zzbbd<?>[] zzaFk = new zzbbd[0];
    private final Map<zzc<?>, zze> zzaDF;
    final Set<zzbbd<?>> zzaFl = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap()));
    private final zzbew zzaFm = new zzbev(this);

    public zzbeu(Map<zzc<?>, zze> map) {
        this.zzaDF = map;
    }

    public final void release() {
        for (zzbbd com_google_android_gms_internal_zzbbd : (zzbbd[]) this.zzaFl.toArray(zzaFk)) {
            com_google_android_gms_internal_zzbbd.zza(null);
            com_google_android_gms_internal_zzbbd.zzpo();
            if (com_google_android_gms_internal_zzbbd.zzpB()) {
                this.zzaFl.remove(com_google_android_gms_internal_zzbbd);
            }
        }
    }

    final void zzb(zzbbd<? extends Result> com_google_android_gms_internal_zzbbd__extends_com_google_android_gms_common_api_Result) {
        this.zzaFl.add(com_google_android_gms_internal_zzbbd__extends_com_google_android_gms_common_api_Result);
        com_google_android_gms_internal_zzbbd__extends_com_google_android_gms_common_api_Result.zza(this.zzaFm);
    }

    public final void zzqM() {
        for (zzbbd zzs : (zzbbd[]) this.zzaFl.toArray(zzaFk)) {
            zzs.zzs(zzaFj);
        }
    }
}
