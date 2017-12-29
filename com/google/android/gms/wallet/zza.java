package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;

@Deprecated
public final class zza extends zzbfm {
    public static final Creator<zza> CREATOR = new zzb();
    private String name;
    private String phoneNumber;
    private String zzctp;
    private String zziec;
    private String zzied;
    private String zziee;
    private String zziej;
    private boolean zziel;
    private String zziem;
    private String zzkyr;
    private String zzkys;

    zza() {
    }

    zza(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, boolean z, String str10) {
        this.name = str;
        this.zziec = str2;
        this.zzied = str3;
        this.zziee = str4;
        this.zzctp = str5;
        this.zzkyr = str6;
        this.zzkys = str7;
        this.zziej = str8;
        this.phoneNumber = str9;
        this.zziel = z;
        this.zziem = str10;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zza(parcel, 2, this.name, false);
        zzbfp.zza(parcel, 3, this.zziec, false);
        zzbfp.zza(parcel, 4, this.zzied, false);
        zzbfp.zza(parcel, 5, this.zziee, false);
        zzbfp.zza(parcel, 6, this.zzctp, false);
        zzbfp.zza(parcel, 7, this.zzkyr, false);
        zzbfp.zza(parcel, 8, this.zzkys, false);
        zzbfp.zza(parcel, 9, this.zziej, false);
        zzbfp.zza(parcel, 10, this.phoneNumber, false);
        zzbfp.zza(parcel, 11, this.zziel);
        zzbfp.zza(parcel, 12, this.zziem, false);
        zzbfp.zzai(parcel, zze);
    }
}
