package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzd;

@Deprecated
public final class zza extends com.google.android.gms.common.internal.safeparcel.zza {
    public static final Creator<zza> CREATOR = new zzb();
    private String name;
    private String phoneNumber;
    private String zzVJ;
    private String zzbOk;
    private String zzbOl;
    private String zzbgE;
    private String zzbgF;
    private String zzbgG;
    private String zzbgL;
    private boolean zzbgN;
    private String zzbgO;

    zza() {
    }

    zza(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, boolean z, String str10) {
        this.name = str;
        this.zzbgE = str2;
        this.zzbgF = str3;
        this.zzbgG = str4;
        this.zzVJ = str5;
        this.zzbOk = str6;
        this.zzbOl = str7;
        this.zzbgL = str8;
        this.phoneNumber = str9;
        this.zzbgN = z;
        this.zzbgO = str10;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zza(parcel, 2, this.name, false);
        zzd.zza(parcel, 3, this.zzbgE, false);
        zzd.zza(parcel, 4, this.zzbgF, false);
        zzd.zza(parcel, 5, this.zzbgG, false);
        zzd.zza(parcel, 6, this.zzVJ, false);
        zzd.zza(parcel, 7, this.zzbOk, false);
        zzd.zza(parcel, 8, this.zzbOl, false);
        zzd.zza(parcel, 9, this.zzbgL, false);
        zzd.zza(parcel, 10, this.phoneNumber, false);
        zzd.zza(parcel, 11, this.zzbgN);
        zzd.zza(parcel, 12, this.zzbgO, false);
        zzd.zzI(parcel, zze);
    }
}
