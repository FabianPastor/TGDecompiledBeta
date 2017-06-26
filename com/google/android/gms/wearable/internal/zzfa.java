package com.google.android.gms.wearable.internal;

import com.google.android.gms.wearable.CapabilityInfo;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class zzfa {
    private static Map<String, CapabilityInfo> zzN(List<zzaa> list) {
        Map hashMap = new HashMap(list.size() << 1);
        for (zzaa com_google_android_gms_wearable_internal_zzaa : list) {
            hashMap.put(com_google_android_gms_wearable_internal_zzaa.getName(), new zzw(com_google_android_gms_wearable_internal_zzaa));
        }
        return hashMap;
    }
}
