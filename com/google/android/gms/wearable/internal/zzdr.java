package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.internal.zzbay;
import com.google.android.gms.wearable.WearableStatusCodes;
import java.lang.ref.WeakReference;
import java.util.Map;

final class zzdr<T> extends zzfc<Status> {
    private WeakReference<Map<T, zzga<T>>> zzbSP;
    private WeakReference<T> zzbSQ;

    zzdr(Map<T, zzga<T>> map, T t, zzbay<Status> com_google_android_gms_internal_zzbay_com_google_android_gms_common_api_Status) {
        super(com_google_android_gms_internal_zzbay_com_google_android_gms_common_api_Status);
        this.zzbSP = new WeakReference(map);
        this.zzbSQ = new WeakReference(t);
    }

    public final void zza(Status status) {
        Map map = (Map) this.zzbSP.get();
        Object obj = this.zzbSQ.get();
        if (!(status.getStatus().getStatusCode() != WearableStatusCodes.UNKNOWN_LISTENER || map == null || obj == null)) {
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