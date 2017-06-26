package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public final class zzc extends zza {
    public static final Creator<zzc> CREATOR = new zzd();
    private String label;
    private String value;

    zzc() {
    }

    public zzc(String str, String str2) {
        this.label = str;
        this.value = str2;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zza(parcel, 2, this.label, false);
        zzd.zza(parcel, 3, this.value, false);
        zzd.zzI(parcel, zze);
    }
}
