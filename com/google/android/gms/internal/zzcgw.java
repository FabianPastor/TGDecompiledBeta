package com.google.android.gms.internal;

import com.google.android.gms.common.internal.zzbq;

final class zzcgw {
    final String mAppId;
    final String mName;
    final long zzizk;
    final long zzizl;
    final long zzizm;
    final long zzizn;
    final Long zzizo;
    final Long zzizp;
    final Boolean zzizq;

    zzcgw(String str, String str2, long j, long j2, long j3, long j4, Long l, Long l2, Boolean bool) {
        zzbq.zzgm(str);
        zzbq.zzgm(str2);
        zzbq.checkArgument(j >= 0);
        zzbq.checkArgument(j2 >= 0);
        zzbq.checkArgument(j4 >= 0);
        this.mAppId = str;
        this.mName = str2;
        this.zzizk = j;
        this.zzizl = j2;
        this.zzizm = j3;
        this.zzizn = j4;
        this.zzizo = l;
        this.zzizp = l2;
        this.zzizq = bool;
    }

    final zzcgw zza(Long l, Long l2, Boolean bool) {
        Boolean bool2 = (bool == null || bool.booleanValue()) ? bool : null;
        return new zzcgw(this.mAppId, this.mName, this.zzizk, this.zzizl, this.zzizm, this.zzizn, l, l2, bool2);
    }

    final zzcgw zzayw() {
        return new zzcgw(this.mAppId, this.mName, this.zzizk + 1, this.zzizl + 1, this.zzizm, this.zzizn, this.zzizo, this.zzizp, this.zzizq);
    }

    final zzcgw zzbb(long j) {
        return new zzcgw(this.mAppId, this.mName, this.zzizk, this.zzizl, j, this.zzizn, this.zzizo, this.zzizp, this.zzizq);
    }

    final zzcgw zzbc(long j) {
        return new zzcgw(this.mAppId, this.mName, this.zzizk, this.zzizl, this.zzizm, j, this.zzizo, this.zzizp, this.zzizq);
    }
}
