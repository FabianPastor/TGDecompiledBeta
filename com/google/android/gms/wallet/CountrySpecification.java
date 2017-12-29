package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;

@Deprecated
public class CountrySpecification extends zzbfm {
    public static final Creator<CountrySpecification> CREATOR = new zzh();
    private String zzctp;

    public CountrySpecification(String str) {
        this.zzctp = str;
    }

    public void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zza(parcel, 2, this.zzctp, false);
        zzbfp.zzai(parcel, zze);
    }
}
