package com.google.android.gms.measurement.internal;

import android.os.Bundle;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzac;
import java.util.Iterator;

public class zzh {
    final String aoq;
    final long aor;
    final EventParams aos;
    final String mName;
    final long tr;
    final String zzcpe;

    zzh(zzx com_google_android_gms_measurement_internal_zzx, String str, String str2, String str3, long j, long j2, Bundle bundle) {
        zzac.zzhz(str2);
        zzac.zzhz(str3);
        this.zzcpe = str2;
        this.mName = str3;
        if (TextUtils.isEmpty(str)) {
            str = null;
        }
        this.aoq = str;
        this.tr = j;
        this.aor = j2;
        if (this.aor != 0 && this.aor > this.tr) {
            com_google_android_gms_measurement_internal_zzx.zzbvg().zzbwe().log("Event created with reverse previous/current timestamps");
        }
        this.aos = zza(com_google_android_gms_measurement_internal_zzx, bundle);
    }

    private zzh(zzx com_google_android_gms_measurement_internal_zzx, String str, String str2, String str3, long j, long j2, EventParams eventParams) {
        zzac.zzhz(str2);
        zzac.zzhz(str3);
        zzac.zzy(eventParams);
        this.zzcpe = str2;
        this.mName = str3;
        if (TextUtils.isEmpty(str)) {
            str = null;
        }
        this.aoq = str;
        this.tr = j;
        this.aor = j2;
        if (this.aor != 0 && this.aor > this.tr) {
            com_google_android_gms_measurement_internal_zzx.zzbvg().zzbwe().log("Event created with reverse previous/current timestamps");
        }
        this.aos = eventParams;
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
                com_google_android_gms_measurement_internal_zzx.zzbvg().zzbwc().log("Param name can't be null");
                it.remove();
            } else {
                Object zzl = com_google_android_gms_measurement_internal_zzx.zzbvc().zzl(str, bundle2.get(str));
                if (zzl == null) {
                    com_google_android_gms_measurement_internal_zzx.zzbvg().zzbwe().zzj("Param value can't be null", str);
                    it.remove();
                } else {
                    com_google_android_gms_measurement_internal_zzx.zzbvc().zza(bundle2, str, zzl);
                }
            }
        }
        return new EventParams(bundle2);
    }

    public String toString() {
        String str = this.zzcpe;
        String str2 = this.mName;
        String valueOf = String.valueOf(this.aos);
        return new StringBuilder(((String.valueOf(str).length() + 33) + String.valueOf(str2).length()) + String.valueOf(valueOf).length()).append("Event{appId='").append(str).append("'").append(", name='").append(str2).append("'").append(", params=").append(valueOf).append("}").toString();
    }

    zzh zza(zzx com_google_android_gms_measurement_internal_zzx, long j) {
        return new zzh(com_google_android_gms_measurement_internal_zzx, this.aoq, this.zzcpe, this.mName, this.tr, j, this.aos);
    }
}
