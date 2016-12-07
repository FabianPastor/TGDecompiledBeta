package com.google.android.gms.internal;

import android.os.Bundle;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzac;
import java.util.Iterator;

public class zzasx {
    final String mName;
    final String zzVQ;
    final long zzavX;
    final String zzbqG;
    final long zzbqH;
    final zzasz zzbqI;

    zzasx(zzatp com_google_android_gms_internal_zzatp, String str, String str2, String str3, long j, long j2, Bundle bundle) {
        zzac.zzdv(str2);
        zzac.zzdv(str3);
        this.zzVQ = str2;
        this.mName = str3;
        if (TextUtils.isEmpty(str)) {
            str = null;
        }
        this.zzbqG = str;
        this.zzavX = j;
        this.zzbqH = j2;
        if (this.zzbqH != 0 && this.zzbqH > this.zzavX) {
            com_google_android_gms_internal_zzatp.zzJt().zzLc().zzj("Event created with reverse previous/current timestamps. appId", zzati.zzfI(str2));
        }
        this.zzbqI = zza(com_google_android_gms_internal_zzatp, bundle);
    }

    private zzasx(zzatp com_google_android_gms_internal_zzatp, String str, String str2, String str3, long j, long j2, zzasz com_google_android_gms_internal_zzasz) {
        zzac.zzdv(str2);
        zzac.zzdv(str3);
        zzac.zzw(com_google_android_gms_internal_zzasz);
        this.zzVQ = str2;
        this.mName = str3;
        if (TextUtils.isEmpty(str)) {
            str = null;
        }
        this.zzbqG = str;
        this.zzavX = j;
        this.zzbqH = j2;
        if (this.zzbqH != 0 && this.zzbqH > this.zzavX) {
            com_google_android_gms_internal_zzatp.zzJt().zzLc().zzj("Event created with reverse previous/current timestamps. appId", zzati.zzfI(str2));
        }
        this.zzbqI = com_google_android_gms_internal_zzasz;
    }

    static zzasz zza(zzatp com_google_android_gms_internal_zzatp, Bundle bundle) {
        if (bundle == null || bundle.isEmpty()) {
            return new zzasz(new Bundle());
        }
        Bundle bundle2 = new Bundle(bundle);
        Iterator it = bundle2.keySet().iterator();
        while (it.hasNext()) {
            String str = (String) it.next();
            if (str == null) {
                com_google_android_gms_internal_zzatp.zzJt().zzLa().log("Param name can't be null");
                it.remove();
            } else {
                Object zzl = com_google_android_gms_internal_zzatp.zzJp().zzl(str, bundle2.get(str));
                if (zzl == null) {
                    com_google_android_gms_internal_zzatp.zzJt().zzLc().zzj("Param value can't be null", str);
                    it.remove();
                } else {
                    com_google_android_gms_internal_zzatp.zzJp().zza(bundle2, str, zzl);
                }
            }
        }
        return new zzasz(bundle2);
    }

    public String toString() {
        String str = this.zzVQ;
        String str2 = this.mName;
        String valueOf = String.valueOf(this.zzbqI);
        return new StringBuilder(((String.valueOf(str).length() + 33) + String.valueOf(str2).length()) + String.valueOf(valueOf).length()).append("Event{appId='").append(str).append("'").append(", name='").append(str2).append("'").append(", params=").append(valueOf).append("}").toString();
    }

    zzasx zza(zzatp com_google_android_gms_internal_zzatp, long j) {
        return new zzasx(com_google_android_gms_internal_zzatp, this.zzbqG, this.zzVQ, this.mName, this.zzavX, j, this.zzbqI);
    }
}
