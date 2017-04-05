package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public class zzbjl extends zza {
    public static final Creator<zzbjl> CREATOR = new zzbjm();
    final int versionCode;
    public int zzbOG;

    public zzbjl() {
        this.versionCode = 1;
    }

    public zzbjl(int i, int i2) {
        this.versionCode = i;
        this.zzbOG = i2;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzbjm.zza(this, parcel, i);
    }
}
