package com.google.android.gms.identity.intents.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.zza;

public class CountrySpecification extends zza implements ReflectedParcelable {
    public static final Creator<CountrySpecification> CREATOR = new zza();
    String zzUI;

    public CountrySpecification(String str) {
        this.zzUI = str;
    }

    public String getCountryCode() {
        return this.zzUI;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zza.zza(this, parcel, i);
    }
}
