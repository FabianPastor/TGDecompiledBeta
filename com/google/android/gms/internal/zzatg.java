package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.zzac;

public final class zzatg extends zza {
    public static final Creator<zzatg> CREATOR = new zzath();
    public String packageName;
    public final int versionCode;
    public String zzbqZ;
    public zzauq zzbra;
    public long zzbrb;
    public boolean zzbrc;
    public String zzbrd;
    public zzatq zzbre;
    public long zzbrf;
    public zzatq zzbrg;
    public long zzbrh;
    public zzatq zzbri;

    zzatg(int i, String str, String str2, zzauq com_google_android_gms_internal_zzauq, long j, boolean z, String str3, zzatq com_google_android_gms_internal_zzatq, long j2, zzatq com_google_android_gms_internal_zzatq2, long j3, zzatq com_google_android_gms_internal_zzatq3) {
        this.versionCode = i;
        this.packageName = str;
        this.zzbqZ = str2;
        this.zzbra = com_google_android_gms_internal_zzauq;
        this.zzbrb = j;
        this.zzbrc = z;
        this.zzbrd = str3;
        this.zzbre = com_google_android_gms_internal_zzatq;
        this.zzbrf = j2;
        this.zzbrg = com_google_android_gms_internal_zzatq2;
        this.zzbrh = j3;
        this.zzbri = com_google_android_gms_internal_zzatq3;
    }

    zzatg(zzatg com_google_android_gms_internal_zzatg) {
        this.versionCode = 1;
        zzac.zzw(com_google_android_gms_internal_zzatg);
        this.packageName = com_google_android_gms_internal_zzatg.packageName;
        this.zzbqZ = com_google_android_gms_internal_zzatg.zzbqZ;
        this.zzbra = com_google_android_gms_internal_zzatg.zzbra;
        this.zzbrb = com_google_android_gms_internal_zzatg.zzbrb;
        this.zzbrc = com_google_android_gms_internal_zzatg.zzbrc;
        this.zzbrd = com_google_android_gms_internal_zzatg.zzbrd;
        this.zzbre = com_google_android_gms_internal_zzatg.zzbre;
        this.zzbrf = com_google_android_gms_internal_zzatg.zzbrf;
        this.zzbrg = com_google_android_gms_internal_zzatg.zzbrg;
        this.zzbrh = com_google_android_gms_internal_zzatg.zzbrh;
        this.zzbri = com_google_android_gms_internal_zzatg.zzbri;
    }

    zzatg(String str, String str2, zzauq com_google_android_gms_internal_zzauq, long j, boolean z, String str3, zzatq com_google_android_gms_internal_zzatq, long j2, zzatq com_google_android_gms_internal_zzatq2, long j3, zzatq com_google_android_gms_internal_zzatq3) {
        this.versionCode = 1;
        this.packageName = str;
        this.zzbqZ = str2;
        this.zzbra = com_google_android_gms_internal_zzauq;
        this.zzbrb = j;
        this.zzbrc = z;
        this.zzbrd = str3;
        this.zzbre = com_google_android_gms_internal_zzatq;
        this.zzbrf = j2;
        this.zzbrg = com_google_android_gms_internal_zzatq2;
        this.zzbrh = j3;
        this.zzbri = com_google_android_gms_internal_zzatq3;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzath.zza(this, parcel, i);
    }
}
