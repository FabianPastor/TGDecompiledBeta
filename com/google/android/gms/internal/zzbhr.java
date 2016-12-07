package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public class zzbhr extends zza {
    public static final Creator<zzbhr> CREATOR = new zzbhs();
    final int versionCode;

    public zzbhr() {
        this.versionCode = 1;
    }

    public zzbhr(int i) {
        this.versionCode = i;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzbhs.zza(this, parcel, i);
    }
}
