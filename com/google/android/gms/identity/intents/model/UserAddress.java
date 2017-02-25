package com.google.android.gms.identity.intents.model;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.identity.intents.AddressConstants.Extras;

public final class UserAddress extends zza implements ReflectedParcelable {
    public static final Creator<UserAddress> CREATOR = new zzb();
    String name;
    String phoneNumber;
    String zzUI;
    String zzbhA;
    String zzbhB;
    String zzbhC;
    boolean zzbhD;
    String zzbhE;
    String zzbhF;
    String zzbhu;
    String zzbhv;
    String zzbhw;
    String zzbhx;
    String zzbhy;
    String zzbhz;

    UserAddress() {
    }

    UserAddress(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, boolean z, String str13, String str14) {
        this.name = str;
        this.zzbhu = str2;
        this.zzbhv = str3;
        this.zzbhw = str4;
        this.zzbhx = str5;
        this.zzbhy = str6;
        this.zzbhz = str7;
        this.zzbhA = str8;
        this.zzUI = str9;
        this.zzbhB = str10;
        this.zzbhC = str11;
        this.phoneNumber = str12;
        this.zzbhD = z;
        this.zzbhE = str13;
        this.zzbhF = str14;
    }

    public static UserAddress fromIntent(Intent intent) {
        return (intent == null || !intent.hasExtra(Extras.EXTRA_ADDRESS)) ? null : (UserAddress) intent.getParcelableExtra(Extras.EXTRA_ADDRESS);
    }

    public String getAddress1() {
        return this.zzbhu;
    }

    public String getAddress2() {
        return this.zzbhv;
    }

    public String getAddress3() {
        return this.zzbhw;
    }

    public String getAddress4() {
        return this.zzbhx;
    }

    public String getAddress5() {
        return this.zzbhy;
    }

    public String getAdministrativeArea() {
        return this.zzbhz;
    }

    public String getCompanyName() {
        return this.zzbhE;
    }

    public String getCountryCode() {
        return this.zzUI;
    }

    public String getEmailAddress() {
        return this.zzbhF;
    }

    public String getLocality() {
        return this.zzbhA;
    }

    public String getName() {
        return this.name;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public String getPostalCode() {
        return this.zzbhB;
    }

    public String getSortingCode() {
        return this.zzbhC;
    }

    public boolean isPostBox() {
        return this.zzbhD;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzb.zza(this, parcel, i);
    }
}
