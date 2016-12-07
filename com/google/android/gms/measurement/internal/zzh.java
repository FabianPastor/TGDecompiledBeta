package com.google.android.gms.measurement.internal;

import android.os.Bundle;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzaa;
import java.util.Iterator;

public class zzh {
    final String arA;
    final long arB;
    final EventParams arC;
    final String mName;
    final long vO;
    final String zzctj;

    zzh(zzx com_google_android_gms_measurement_internal_zzx, String str, String str2, String str3, long j, long j2, Bundle bundle) {
        zzaa.zzib(str2);
        zzaa.zzib(str3);
        this.zzctj = str2;
        this.mName = str3;
        if (TextUtils.isEmpty(str)) {
            str = null;
        }
        this.arA = str;
        this.vO = j;
        this.arB = j2;
        if (this.arB != 0 && this.arB > this.vO) {
            com_google_android_gms_measurement_internal_zzx.zzbwb().zzbxa().log("Event created with reverse previous/current timestamps");
        }
        this.arC = zza(com_google_android_gms_measurement_internal_zzx, bundle);
    }

    private zzh(zzx com_google_android_gms_measurement_internal_zzx, String str, String str2, String str3, long j, long j2, EventParams eventParams) {
        zzaa.zzib(str2);
        zzaa.zzib(str3);
        zzaa.zzy(eventParams);
        this.zzctj = str2;
        this.mName = str3;
        if (TextUtils.isEmpty(str)) {
            str = null;
        }
        this.arA = str;
        this.vO = j;
        this.arB = j2;
        if (this.arB != 0 && this.arB > this.vO) {
            com_google_android_gms_measurement_internal_zzx.zzbwb().zzbxa().log("Event created with reverse previous/current timestamps");
        }
        this.arC = eventParams;
    }

    static EventParams zza(zzx com_google_android_gms_measurement_internal_zzx, Bundle bundle) {
        if (bundle == null || bundle.isEmpty()) {
            return new EventParams(new Bundle());
        }
        Bundle bundle2 = new Bundle(bundle);
        Iterator it = bundle2.keySet().iterator();
        while (it.hasNext()) {
            String str = (String) it.next();
            if (str == null) {
                com_google_android_gms_measurement_internal_zzx.zzbwb().zzbwy().log("Param name can't be null");
                it.remove();
            } else {
                Object zzl = com_google_android_gms_measurement_internal_zzx.zzbvx().zzl(str, bundle2.get(str));
                if (zzl == null) {
                    com_google_android_gms_measurement_internal_zzx.zzbwb().zzbxa().zzj("Param value can't be null", str);
                    it.remove();
                } else {
                    com_google_android_gms_measurement_internal_zzx.zzbvx().zza(bundle2, str, zzl);
                }
            }
        }
        return new EventParams(bundle2);
    }

    public String toString() {
        String str = this.zzctj;
        String str2 = this.mName;
        String valueOf = String.valueOf(this.arC);
        return new StringBuilder(((String.valueOf(str).length() + 33) + String.valueOf(str2).length()) + String.valueOf(valueOf).length()).append("Event{appId='").append(str).append("'").append(", name='").append(str2).append("'").append(", params=").append(valueOf).append("}").toString();
    }

    zzh zza(zzx com_google_android_gms_measurement_internal_zzx, long j) {
        return new zzh(com_google_android_gms_measurement_internal_zzx, this.arA, this.zzctj, this.mName, this.vO, j, this.arC);
    }
}
