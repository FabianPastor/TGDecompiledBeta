package com.google.android.gms.internal;

import android.os.Bundle;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzbo;
import java.util.Iterator;

public final class zzcet {
    final String mAppId;
    final String mName;
    private String mOrigin;
    final long zzayS;
    final long zzbpE;
    final zzcev zzbpF;

    zzcet(zzcgk com_google_android_gms_internal_zzcgk, String str, String str2, String str3, long j, long j2, Bundle bundle) {
        zzbo.zzcF(str2);
        zzbo.zzcF(str3);
        this.mAppId = str2;
        this.mName = str3;
        if (TextUtils.isEmpty(str)) {
            str = null;
        }
        this.mOrigin = str;
        this.zzayS = j;
        this.zzbpE = j2;
        if (this.zzbpE != 0 && this.zzbpE > this.zzayS) {
            com_google_android_gms_internal_zzcgk.zzwF().zzyz().zzj("Event created with reverse previous/current timestamps. appId", zzcfk.zzdZ(str2));
        }
        this.zzbpF = zza(com_google_android_gms_internal_zzcgk, bundle);
    }

    private zzcet(zzcgk com_google_android_gms_internal_zzcgk, String str, String str2, String str3, long j, long j2, zzcev com_google_android_gms_internal_zzcev) {
        zzbo.zzcF(str2);
        zzbo.zzcF(str3);
        zzbo.zzu(com_google_android_gms_internal_zzcev);
        this.mAppId = str2;
        this.mName = str3;
        if (TextUtils.isEmpty(str)) {
            str = null;
        }
        this.mOrigin = str;
        this.zzayS = j;
        this.zzbpE = j2;
        if (this.zzbpE != 0 && this.zzbpE > this.zzayS) {
            com_google_android_gms_internal_zzcgk.zzwF().zzyz().zzj("Event created with reverse previous/current timestamps. appId", zzcfk.zzdZ(str2));
        }
        this.zzbpF = com_google_android_gms_internal_zzcev;
    }

    private static zzcev zza(zzcgk com_google_android_gms_internal_zzcgk, Bundle bundle) {
        if (bundle == null || bundle.isEmpty()) {
            return new zzcev(new Bundle());
        }
        Bundle bundle2 = new Bundle(bundle);
        Iterator it = bundle2.keySet().iterator();
        while (it.hasNext()) {
            String str = (String) it.next();
            if (str == null) {
                com_google_android_gms_internal_zzcgk.zzwF().zzyx().log("Param name can't be null");
                it.remove();
            } else {
                Object zzk = com_google_android_gms_internal_zzcgk.zzwB().zzk(str, bundle2.get(str));
                if (zzk == null) {
                    com_google_android_gms_internal_zzcgk.zzwF().zzyz().zzj("Param value can't be null", com_google_android_gms_internal_zzcgk.zzwA().zzdX(str));
                    it.remove();
                } else {
                    com_google_android_gms_internal_zzcgk.zzwB().zza(bundle2, str, zzk);
                }
            }
        }
        return new zzcev(bundle2);
    }

    public final String toString() {
        String str = this.mAppId;
        String str2 = this.mName;
        String valueOf = String.valueOf(this.zzbpF);
        return new StringBuilder(((String.valueOf(str).length() + 33) + String.valueOf(str2).length()) + String.valueOf(valueOf).length()).append("Event{appId='").append(str).append("', name='").append(str2).append("', params=").append(valueOf).append("}").toString();
    }

    final zzcet zza(zzcgk com_google_android_gms_internal_zzcgk, long j) {
        return new zzcet(com_google_android_gms_internal_zzcgk, this.mOrigin, this.mAppId, this.mName, this.zzayS, j, this.zzbpF);
    }
}
