package com.google.android.gms.internal;

import com.google.android.gms.common.internal.zzbq;

final class zzclp {
    final String mAppId;
    final String mName;
    final String mOrigin;
    final Object mValue;
    final long zzjjm;

    zzclp(String str, String str2, String str3, long j, Object obj) {
        zzbq.zzgm(str);
        zzbq.zzgm(str3);
        zzbq.checkNotNull(obj);
        this.mAppId = str;
        this.mOrigin = str2;
        this.mName = str3;
        this.zzjjm = j;
        this.mValue = obj;
    }
}
