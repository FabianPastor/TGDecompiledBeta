package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;

@Deprecated
public final class zza extends com.google.android.gms.common.internal.safeparcel.zza {
    public static final Creator<zza> CREATOR = new zzb();
    String name;
    String phoneNumber;
    String zzUI;
    String zzbPN;
    String zzbPO;
    String zzbhB;
    boolean zzbhD;
    String zzbhE;
    String zzbhu;
    String zzbhv;
    String zzbhw;

    zza() {
    }

    zza(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, boolean z, String str10) {
        this.name = str;
        this.zzbhu = str2;
        this.zzbhv = str3;
        this.zzbhw = str4;
        this.zzUI = str5;
        this.zzbPN = str6;
        this.zzbPO = str7;
        this.zzbhB = str8;
        this.phoneNumber = str9;
        this.zzbhD = z;
        this.zzbhE = str10;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzb.zza(this, parcel, i);
    }
}
