package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.zzac;

public final class zzatg extends zza {
    public static final Creator<zzatg> CREATOR = new zzath();
    public String packageName;
    public final int versionCode;
    public String zzbqW;
    public zzauq zzbqX;
    public long zzbqY;
    public boolean zzbqZ;
    public String zzbra;
    public zzatq zzbrb;
    public long zzbrc;
    public zzatq zzbrd;
    public long zzbre;
    public zzatq zzbrf;

    zzatg(int i, String str, String str2, zzauq com_google_android_gms_internal_zzauq, long j, boolean z, String str3, zzatq com_google_android_gms_internal_zzatq, long j2, zzatq com_google_android_gms_internal_zzatq2, long j3, zzatq com_google_android_gms_internal_zzatq3) {
        this.versionCode = i;
        this.packageName = str;
        this.zzbqW = str2;
        this.zzbqX = com_google_android_gms_internal_zzauq;
        this.zzbqY = j;
        this.zzbqZ = z;
        this.zzbra = str3;
        this.zzbrb = com_google_android_gms_internal_zzatq;
        this.zzbrc = j2;
        this.zzbrd = com_google_android_gms_internal_zzatq2;
        this.zzbre = j3;
        this.zzbrf = com_google_android_gms_internal_zzatq3;
    }

    zzatg(zzatg com_google_android_gms_internal_zzatg) {
        this.versionCode = 1;
        zzac.zzw(com_google_android_gms_internal_zzatg);
        this.packageName = com_google_android_gms_internal_zzatg.packageName;
        this.zzbqW = com_google_android_gms_internal_zzatg.zzbqW;
        this.zzbqX = com_google_android_gms_internal_zzatg.zzbqX;
        this.zzbqY = com_google_android_gms_internal_zzatg.zzbqY;
        this.zzbqZ = com_google_android_gms_internal_zzatg.zzbqZ;
        this.zzbra = com_google_android_gms_internal_zzatg.zzbra;
        this.zzbrb = com_google_android_gms_internal_zzatg.zzbrb;
        this.zzbrc = com_google_android_gms_internal_zzatg.zzbrc;
        this.zzbrd = com_google_android_gms_internal_zzatg.zzbrd;
        this.zzbre = com_google_android_gms_internal_zzatg.zzbre;
        this.zzbrf = com_google_android_gms_internal_zzatg.zzbrf;
    }

    zzatg(String str, String str2, zzauq com_google_android_gms_internal_zzauq, long j, boolean z, String str3, zzatq com_google_android_gms_internal_zzatq, long j2, zzatq com_google_android_gms_internal_zzatq2, long j3, zzatq com_google_android_gms_internal_zzatq3) {
        this.versionCode = 1;
        this.packageName = str;
        this.zzbqW = str2;
        this.zzbqX = com_google_android_gms_internal_zzauq;
        this.zzbqY = j;
        this.zzbqZ = z;
        this.zzbra = str3;
        this.zzbrb = com_google_android_gms_internal_zzatq;
        this.zzbrc = j2;
        this.zzbrd = com_google_android_gms_internal_zzatq2;
        this.zzbre = j3;
        this.zzbrf = com_google_android_gms_internal_zzatq3;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzath.zza(this, parcel, i);
    }
}
