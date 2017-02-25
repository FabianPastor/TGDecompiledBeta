package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public final class zzb extends zza {
    public static final Creator<zzb> CREATOR = new zzc();
    String label;
    String value;

    zzb() {
    }

    public zzb(String str, String str2) {
        this.label = str;
        this.value = str2;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzc.zza(this, parcel, i);
    }
}
