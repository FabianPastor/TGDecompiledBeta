package com.google.android.gms.identity.intents.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public class CountrySpecification extends zza implements ReflectedParcelable {
    public static final Creator<CountrySpecification> CREATOR = new zza();
    private String zzVJ;

    public CountrySpecification(String str) {
        this.zzVJ = str;
    }

    public String getCountryCode() {
        return this.zzVJ;
    }

    public void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zza(parcel, 2, this.zzVJ, false);
        zzd.zzI(parcel, zze);
    }
}
