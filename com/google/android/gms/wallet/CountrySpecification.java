package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

@Deprecated
public class CountrySpecification extends zza {
    public static final Creator<CountrySpecification> CREATOR = new zzd();
    String zzUI;

    public CountrySpecification(String str) {
        this.zzUI = str;
    }

    public String getCountryCode() {
        return this.zzUI;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzd.zza(this, parcel, i);
    }
}
