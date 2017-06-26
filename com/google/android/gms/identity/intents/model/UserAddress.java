package com.google.android.gms.identity.intents.model;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.identity.intents.AddressConstants.Extras;

public final class UserAddress extends zza implements ReflectedParcelable {
    public static final Creator<UserAddress> CREATOR = new zzb();
    private String name;
    private String phoneNumber;
    private String zzVJ;
    private String zzbgE;
    private String zzbgF;
    private String zzbgG;
    private String zzbgH;
    private String zzbgI;
    private String zzbgJ;
    private String zzbgK;
    private String zzbgL;
    private String zzbgM;
    private boolean zzbgN;
    private String zzbgO;
    private String zzbgP;

    UserAddress() {
    }

    UserAddress(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, boolean z, String str13, String str14) {
        this.name = str;
        this.zzbgE = str2;
        this.zzbgF = str3;
        this.zzbgG = str4;
        this.zzbgH = str5;
        this.zzbgI = str6;
        this.zzbgJ = str7;
        this.zzbgK = str8;
        this.zzVJ = str9;
        this.zzbgL = str10;
        this.zzbgM = str11;
        this.phoneNumber = str12;
        this.zzbgN = z;
        this.zzbgO = str13;
        this.zzbgP = str14;
    }

    public static UserAddress fromIntent(Intent intent) {
        return (intent == null || !intent.hasExtra(Extras.EXTRA_ADDRESS)) ? null : (UserAddress) intent.getParcelableExtra(Extras.EXTRA_ADDRESS);
    }

    public final String getAddress1() {
        return this.zzbgE;
    }

    public final String getAddress2() {
        return this.zzbgF;
    }

    public final String getAddress3() {
        return this.zzbgG;
    }

    public final String getAddress4() {
        return this.zzbgH;
    }

    public final String getAddress5() {
        return this.zzbgI;
    }

    public final String getAdministrativeArea() {
        return this.zzbgJ;
    }

    public final String getCompanyName() {
        return this.zzbgO;
    }

    public final String getCountryCode() {
        return this.zzVJ;
    }

    public final String getEmailAddress() {
        return this.zzbgP;
    }

    public final String getLocality() {
        return this.zzbgK;
    }

    public final String getName() {
        return this.name;
    }

    public final String getPhoneNumber() {
        return this.phoneNumber;
    }

    public final String getPostalCode() {
        return this.zzbgL;
    }

    public final String getSortingCode() {
        return this.zzbgM;
    }

    public final boolean isPostBox() {
        return this.zzbgN;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zza(parcel, 2, this.name, false);
        zzd.zza(parcel, 3, this.zzbgE, false);
        zzd.zza(parcel, 4, this.zzbgF, false);
        zzd.zza(parcel, 5, this.zzbgG, false);
        zzd.zza(parcel, 6, this.zzbgH, false);
        zzd.zza(parcel, 7, this.zzbgI, false);
        zzd.zza(parcel, 8, this.zzbgJ, false);
        zzd.zza(parcel, 9, this.zzbgK, false);
        zzd.zza(parcel, 10, this.zzVJ, false);
        zzd.zza(parcel, 11, this.zzbgL, false);
        zzd.zza(parcel, 12, this.zzbgM, false);
        zzd.zza(parcel, 13, this.phoneNumber, false);
        zzd.zza(parcel, 14, this.zzbgN);
        zzd.zza(parcel, 15, this.zzbgO, false);
        zzd.zza(parcel, 16, this.zzbgP, false);
        zzd.zzI(parcel, zze);
    }
}
