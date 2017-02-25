package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.zzac;

public class zzatd extends zza {
    public static final Creator<zzatd> CREATOR = new zzate();
    public final String packageName;
    public final String zzbhM;
    public final String zzbqP;
    public final String zzbqQ;
    public final long zzbqR;
    public final long zzbqS;
    public final String zzbqT;
    public final boolean zzbqU;
    public final boolean zzbqV;
    public final long zzbqW;
    public final String zzbqX;
    public final long zzbqY;

    zzatd(String str, String str2, String str3, long j, String str4, long j2, long j3, String str5, boolean z, boolean z2, String str6, long j4) {
        zzac.zzdr(str);
        this.packageName = str;
        if (TextUtils.isEmpty(str2)) {
            str2 = null;
        }
        this.zzbqP = str2;
        this.zzbhM = str3;
        this.zzbqW = j;
        this.zzbqQ = str4;
        this.zzbqR = j2;
        this.zzbqS = j3;
        this.zzbqT = str5;
        this.zzbqU = z;
        this.zzbqV = z2;
        this.zzbqX = str6;
        this.zzbqY = j4;
    }

    zzatd(String str, String str2, String str3, String str4, long j, long j2, String str5, boolean z, boolean z2, long j3, String str6, long j4) {
        this.packageName = str;
        this.zzbqP = str2;
        this.zzbhM = str3;
        this.zzbqW = j3;
        this.zzbqQ = str4;
        this.zzbqR = j;
        this.zzbqS = j2;
        this.zzbqT = str5;
        this.zzbqU = z;
        this.zzbqV = z2;
        this.zzbqX = str6;
        this.zzbqY = j4;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzate.zza(this, parcel, i);
    }
}
