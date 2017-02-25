package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public class zzbkl extends zza {
    public static final Creator<zzbkl> CREATOR = new zzbkm();
    public final int versionCode;

    public zzbkl(int i) {
        this.versionCode = i;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzbkm.zza(this, parcel, i);
    }
}
