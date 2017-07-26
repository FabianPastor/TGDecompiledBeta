package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.internal.zzbaz;
import java.lang.ref.WeakReference;
import java.util.Map;

final class zzdq<T> extends zzfc<Status> {
    private WeakReference<Map<T, zzga<T>>> zzbSR;
    private WeakReference<T> zzbSS;

    zzdq(Map<T, zzga<T>> map, T t, zzbaz<Status> com_google_android_gms_internal_zzbaz_com_google_android_gms_common_api_Status) {
        super(com_google_android_gms_internal_zzbaz_com_google_android_gms_common_api_Status);
        this.zzbSR = new WeakReference(map);
        this.zzbSS = new WeakReference(t);
    }

    public final void zza(Status status) {
        Map map = (Map) this.zzbSR.get();
        Object obj = this.zzbSS.get();
        if (!(status.getStatus().isSuccess() || map == null || obj == null)) {
            synchronized (map) {
                zzga com_google_android_gms_wearable_internal_zzga = (zzga) map.remove(obj);
                if (com_google_android_gms_wearable_internal_zzga != null) {
                    com_google_android_gms_wearable_internal_zzga.clear();
                }
            }
        }
        zzR(status);
    }
}
