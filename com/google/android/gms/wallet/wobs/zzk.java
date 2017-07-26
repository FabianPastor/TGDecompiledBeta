package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public final class zzk extends zza {
    public static final Creator<zzk> CREATOR = new zzl();
    private String body;
    private String zzbQQ;

    zzk() {
    }

    public zzk(String str, String str2) {
        this.zzbQQ = str;
        this.body = str2;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zza(parcel, 2, this.zzbQQ, false);
        zzd.zza(parcel, 3, this.body, false);
        zzd.zzI(parcel, zze);
    }
}
