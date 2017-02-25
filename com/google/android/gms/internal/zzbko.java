package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public class zzbko extends zza {
    public static final Creator<zzbko> CREATOR = new zzbkp();
    final int versionCode;

    public zzbko() {
        this.versionCode = 1;
    }

    public zzbko(int i) {
        this.versionCode = i;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzbkp.zza(this, parcel, i);
    }
}
