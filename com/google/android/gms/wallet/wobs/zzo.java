package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public final class zzo extends zza {
    public static final Creator<zzo> CREATOR = new zzp();
    private String description;
    private String zzbQT;

    zzo() {
    }

    public zzo(String str, String str2) {
        this.zzbQT = str;
        this.description = str2;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zza(parcel, 2, this.zzbQT, false);
        zzd.zza(parcel, 3, this.description, false);
        zzd.zzI(parcel, zze);
    }
}
