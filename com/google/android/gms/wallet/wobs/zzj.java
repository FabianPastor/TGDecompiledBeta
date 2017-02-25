package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public final class zzj extends zza {
    public static final Creator<zzj> CREATOR = new zzk();
    String body;
    String zzbSI;

    zzj() {
    }

    public zzj(String str, String str2) {
        this.zzbSI = str;
        this.body = str2;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzk.zza(this, parcel, i);
    }
}
