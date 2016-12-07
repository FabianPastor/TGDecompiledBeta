package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public class zzbho extends zza {
    public static final Creator<zzbho> CREATOR = new zzbhp();
    public final int versionCode;

    public zzbho(int i) {
        this.versionCode = i;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzbhp.zza(this, parcel, i);
    }
}
