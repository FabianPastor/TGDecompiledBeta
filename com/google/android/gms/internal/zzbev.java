package com.google.android.gms.internal;

import com.google.android.gms.common.api.Api.zzc;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public final class zzbev {
    public static final Status zzaFj = new Status(8, "The connection to Google Play services was lost");
    private static final zzbbe<?>[] zzaFk = new zzbbe[0];
    private final Map<zzc<?>, zze> zzaDF;
    final Set<zzbbe<?>> zzaFl = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap()));
    private final zzbex zzaFm = new zzbew(this);

    public zzbev(Map<zzc<?>, zze> map) {
        this.zzaDF = map;
    }

    public final void release() {
        for (zzbbe com_google_android_gms_internal_zzbbe : (zzbbe[]) this.zzaFl.toArray(zzaFk)) {
            com_google_android_gms_internal_zzbbe.zza(null);
            com_google_android_gms_internal_zzbbe.zzpo();
            if (com_google_android_gms_internal_zzbbe.zzpB()) {
                this.zzaFl.remove(com_google_android_gms_internal_zzbbe);
            }
        }
    }

    final void zzb(zzbbe<? extends Result> com_google_android_gms_internal_zzbbe__extends_com_google_android_gms_common_api_Result) {
        this.zzaFl.add(com_google_android_gms_internal_zzbbe__extends_com_google_android_gms_common_api_Result);
        com_google_android_gms_internal_zzbbe__extends_com_google_android_gms_common_api_Result.zza(this.zzaFm);
    }

    public final void zzqM() {
        for (zzbbe zzs : (zzbbe[]) this.zzaFl.toArray(zzaFk)) {
            zzs.zzs(zzaFj);
        }
    }
}
