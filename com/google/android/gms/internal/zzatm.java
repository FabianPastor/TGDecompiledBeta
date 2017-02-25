package com.google.android.gms.internal;

import android.os.Bundle;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzac;
import java.util.Iterator;

public class zzatm {
    final String mAppId;
    final String mName;
    final String mOrigin;
    final long zzaxb;
    final long zzbrC;
    final zzato zzbrD;

    zzatm(zzaue com_google_android_gms_internal_zzaue, String str, String str2, String str3, long j, long j2, Bundle bundle) {
        zzac.zzdr(str2);
        zzac.zzdr(str3);
        this.mAppId = str2;
        this.mName = str3;
        if (TextUtils.isEmpty(str)) {
            str = null;
        }
        this.mOrigin = str;
        this.zzaxb = j;
        this.zzbrC = j2;
        if (this.zzbrC != 0 && this.zzbrC > this.zzaxb) {
            com_google_android_gms_internal_zzaue.zzKk().zzLZ().zzj("Event created with reverse previous/current timestamps. appId", zzatx.zzfE(str2));
        }
        this.zzbrD = zza(com_google_android_gms_internal_zzaue, bundle);
    }

    private zzatm(zzaue com_google_android_gms_internal_zzaue, String str, String str2, String str3, long j, long j2, zzato com_google_android_gms_internal_zzato) {
        zzac.zzdr(str2);
        zzac.zzdr(str3);
        zzac.zzw(com_google_android_gms_internal_zzato);
        this.mAppId = str2;
        this.mName = str3;
        if (TextUtils.isEmpty(str)) {
            str = null;
        }
        this.mOrigin = str;
        this.zzaxb = j;
        this.zzbrC = j2;
        if (this.zzbrC != 0 && this.zzbrC > this.zzaxb) {
            com_google_android_gms_internal_zzaue.zzKk().zzLZ().zzj("Event created with reverse previous/current timestamps. appId", zzatx.zzfE(str2));
        }
        this.zzbrD = com_google_android_gms_internal_zzato;
    }

    static zzato zza(zzaue com_google_android_gms_internal_zzaue, Bundle bundle) {
        if (bundle == null || bundle.isEmpty()) {
            return new zzato(new Bundle());
        }
        Bundle bundle2 = new Bundle(bundle);
        Iterator it = bundle2.keySet().iterator();
        while (it.hasNext()) {
            String str = (String) it.next();
            if (str == null) {
                com_google_android_gms_internal_zzaue.zzKk().zzLX().log("Param name can't be null");
                it.remove();
            } else {
                Object zzl = com_google_android_gms_internal_zzaue.zzKg().zzl(str, bundle2.get(str));
                if (zzl == null) {
                    com_google_android_gms_internal_zzaue.zzKk().zzLZ().zzj("Param value can't be null", str);
                    it.remove();
                } else {
                    com_google_android_gms_internal_zzaue.zzKg().zza(bundle2, str, zzl);
                }
            }
        }
        return new zzato(bundle2);
    }

    public String toString() {
        String str = this.mAppId;
        String str2 = this.mName;
        String valueOf = String.valueOf(this.zzbrD);
        return new StringBuilder(((String.valueOf(str).length() + 33) + String.valueOf(str2).length()) + String.valueOf(valueOf).length()).append("Event{appId='").append(str).append("'").append(", name='").append(str2).append("'").append(", params=").append(valueOf).append("}").toString();
    }

    zzatm zza(zzaue com_google_android_gms_internal_zzaue, long j) {
        return new zzatm(com_google_android_gms_internal_zzaue, this.mOrigin, this.mAppId, this.mName, this.zzaxb, j, this.zzbrD);
    }
}
