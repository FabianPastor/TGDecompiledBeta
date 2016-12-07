package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.zzac;

public class zzasq extends zza {
    public static final Creator<zzasq> CREATOR = new zzasr();
    public final String packageName;
    public final int versionCode;
    public final String zzbhg;
    public final String zzbqf;
    public final String zzbqg;
    public final long zzbqh;
    public final long zzbqi;
    public final String zzbqj;
    public final boolean zzbqk;
    public final boolean zzbql;
    public final long zzbqm;
    public final String zzbqn;

    zzasq(int i, String str, String str2, String str3, String str4, long j, long j2, String str5, boolean z, boolean z2, long j3, String str6) {
        this.versionCode = i;
        this.packageName = str;
        this.zzbqf = str2;
        this.zzbhg = str3;
        if (i < 5) {
            j3 = -2147483648L;
        }
        this.zzbqm = j3;
        this.zzbqg = str4;
        this.zzbqh = j;
        this.zzbqi = j2;
        this.zzbqj = str5;
        if (i >= 3) {
            this.zzbqk = z;
        } else {
            this.zzbqk = true;
        }
        this.zzbql = z2;
        this.zzbqn = str6;
    }

    zzasq(String str, String str2, String str3, long j, String str4, long j2, long j3, String str5, boolean z, boolean z2, String str6) {
        zzac.zzdv(str);
        this.versionCode = 6;
        this.packageName = str;
        if (TextUtils.isEmpty(str2)) {
            str2 = null;
        }
        this.zzbqf = str2;
        this.zzbhg = str3;
        this.zzbqm = j;
        this.zzbqg = str4;
        this.zzbqh = j2;
        this.zzbqi = j3;
        this.zzbqj = str5;
        this.zzbqk = z;
        this.zzbql = z2;
        this.zzbqn = str6;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzasr.zza(this, parcel, i);
    }
}
