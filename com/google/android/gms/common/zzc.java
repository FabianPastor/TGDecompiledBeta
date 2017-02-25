package com.google.android.gms.common;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public class zzc extends zza {
    public static final Creator<zzc> CREATOR = new zzd();
    public final String name;
    public final int version;

    public zzc(String str, int i) {
        this.name = str;
        this.version = i;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzd.zza(this, parcel, i);
    }
}
