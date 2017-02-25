package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public final class zzu extends zza {
    public static final Creator<zzu> CREATOR = new zzv();
    String zzbRA;

    private zzu() {
    }

    zzu(String str) {
        this.zzbRA = str;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzv.zza(this, parcel, i);
    }
}
