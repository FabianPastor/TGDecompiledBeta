package com.google.android.gms.identity.intents.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;

public final class UserAddress extends zzbfm implements ReflectedParcelable {
    public static final Creator<UserAddress> CREATOR = new zzb();
    private String name;
    private String phoneNumber;
    private String zzctp;
    private String zziec;
    private String zzied;
    private String zziee;
    private String zzief;
    private String zzieg;
    private String zzieh;
    private String zziei;
    private String zziej;
    private String zziek;
    private boolean zziel;
    private String zziem;
    private String zzien;

    UserAddress() {
    }

    UserAddress(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, boolean z, String str13, String str14) {
        this.name = str;
        this.zziec = str2;
        this.zzied = str3;
        this.zziee = str4;
        this.zzief = str5;
        this.zzieg = str6;
        this.zzieh = str7;
        this.zziei = str8;
        this.zzctp = str9;
        this.zziej = str10;
        this.zziek = str11;
        this.phoneNumber = str12;
        this.zziel = z;
        this.zziem = str13;
        this.zzien = str14;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zza(parcel, 2, this.name, false);
        zzbfp.zza(parcel, 3, this.zziec, false);
        zzbfp.zza(parcel, 4, this.zzied, false);
        zzbfp.zza(parcel, 5, this.zziee, false);
        zzbfp.zza(parcel, 6, this.zzief, false);
        zzbfp.zza(parcel, 7, this.zzieg, false);
        zzbfp.zza(parcel, 8, this.zzieh, false);
        zzbfp.zza(parcel, 9, this.zziei, false);
        zzbfp.zza(parcel, 10, this.zzctp, false);
        zzbfp.zza(parcel, 11, this.zziej, false);
        zzbfp.zza(parcel, 12, this.zziek, false);
        zzbfp.zza(parcel, 13, this.phoneNumber, false);
        zzbfp.zza(parcel, 14, this.zziel);
        zzbfp.zza(parcel, 15, this.zziem, false);
        zzbfp.zza(parcel, 16, this.zzien, false);
        zzbfp.zzai(parcel, zze);
    }
}
