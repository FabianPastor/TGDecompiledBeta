package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.zzac;

public class zzatd extends zza {
    public static final Creator<zzatd> CREATOR = new zzate();
    public final String packageName;
    public final String zzbhN;
    public final String zzbqL;
    public final String zzbqM;
    public final long zzbqN;
    public final long zzbqO;
    public final String zzbqP;
    public final boolean zzbqQ;
    public final boolean zzbqR;
    public final long zzbqS;
    public final String zzbqT;
    public final long zzbqU;

    zzatd(String str, String str2, String str3, long j, String str4, long j2, long j3, String str5, boolean z, boolean z2, String str6, long j4) {
        zzac.zzdr(str);
        this.packageName = str;
        if (TextUtils.isEmpty(str2)) {
            str2 = null;
        }
        this.zzbqL = str2;
        this.zzbhN = str3;
        this.zzbqS = j;
        this.zzbqM = str4;
        this.zzbqN = j2;
        this.zzbqO = j3;
        this.zzbqP = str5;
        this.zzbqQ = z;
        this.zzbqR = z2;
        this.zzbqT = str6;
        this.zzbqU = j4;
    }

    zzatd(String str, String str2, String str3, String str4, long j, long j2, String str5, boolean z, boolean z2, long j3, String str6, long j4) {
        this.packageName = str;
        this.zzbqL = str2;
        this.zzbhN = str3;
        this.zzbqS = j3;
        this.zzbqM = str4;
        this.zzbqN = j;
        this.zzbqO = j2;
        this.zzbqP = str5;
        this.zzbqQ = z;
        this.zzbqR = z2;
        this.zzbqT = str6;
        this.zzbqU = j4;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzate.zza(this, parcel, i);
    }
}
