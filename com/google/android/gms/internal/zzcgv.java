package com.google.android.gms.internal;

import android.os.Bundle;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzbq;
import java.util.Iterator;

public final class zzcgv {
    final String mAppId;
    final String mName;
    private String mOrigin;
    final long zzfij;
    final long zzizi;
    final zzcgx zzizj;

    zzcgv(zzcim com_google_android_gms_internal_zzcim, String str, String str2, String str3, long j, long j2, Bundle bundle) {
        zzbq.zzgm(str2);
        zzbq.zzgm(str3);
        this.mAppId = str2;
        this.mName = str3;
        if (TextUtils.isEmpty(str)) {
            str = null;
        }
        this.mOrigin = str;
        this.zzfij = j;
        this.zzizi = j2;
        if (this.zzizi != 0 && this.zzizi > this.zzfij) {
            com_google_android_gms_internal_zzcim.zzawy().zzazf().zzj("Event created with reverse previous/current timestamps. appId", zzchm.zzjk(str2));
        }
        this.zzizj = zza(com_google_android_gms_internal_zzcim, bundle);
    }

    private zzcgv(zzcim com_google_android_gms_internal_zzcim, String str, String str2, String str3, long j, long j2, zzcgx com_google_android_gms_internal_zzcgx) {
        zzbq.zzgm(str2);
        zzbq.zzgm(str3);
        zzbq.checkNotNull(com_google_android_gms_internal_zzcgx);
        this.mAppId = str2;
        this.mName = str3;
        if (TextUtils.isEmpty(str)) {
            str = null;
        }
        this.mOrigin = str;
        this.zzfij = j;
        this.zzizi = j2;
        if (this.zzizi != 0 && this.zzizi > this.zzfij) {
            com_google_android_gms_internal_zzcim.zzawy().zzazf().zzj("Event created with reverse previous/current timestamps. appId", zzchm.zzjk(str2));
        }
        this.zzizj = com_google_android_gms_internal_zzcgx;
    }

    private static zzcgx zza(zzcim com_google_android_gms_internal_zzcim, Bundle bundle) {
        if (bundle == null || bundle.isEmpty()) {
            return new zzcgx(new Bundle());
        }
        Bundle bundle2 = new Bundle(bundle);
        Iterator it = bundle2.keySet().iterator();
        while (it.hasNext()) {
            String str = (String) it.next();
            if (str == null) {
                com_google_android_gms_internal_zzcim.zzawy().zzazd().log("Param name can't be null");
                it.remove();
            } else {
                Object zzk = com_google_android_gms_internal_zzcim.zzawu().zzk(str, bundle2.get(str));
                if (zzk == null) {
                    com_google_android_gms_internal_zzcim.zzawy().zzazf().zzj("Param value can't be null", com_google_android_gms_internal_zzcim.zzawt().zzji(str));
                    it.remove();
                } else {
                    com_google_android_gms_internal_zzcim.zzawu().zza(bundle2, str, zzk);
                }
            }
        }
        return new zzcgx(bundle2);
    }

    public final String toString() {
        String str = this.mAppId;
        String str2 = this.mName;
        String valueOf = String.valueOf(this.zzizj);
        return new StringBuilder(((String.valueOf(str).length() + 33) + String.valueOf(str2).length()) + String.valueOf(valueOf).length()).append("Event{appId='").append(str).append("', name='").append(str2).append("', params=").append(valueOf).append("}").toString();
    }

    final zzcgv zza(zzcim com_google_android_gms_internal_zzcim, long j) {
        return new zzcgv(com_google_android_gms_internal_zzcim, this.mOrigin, this.mAppId, this.mName, this.zzfij, j, this.zzizj);
    }
}
