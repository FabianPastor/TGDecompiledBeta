package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public final class zzg extends zza {
    public static final Creator<zzg> CREATOR = new zzj();
    private String label;
    private String type;
    private zzm zzbPc;
    private zzh zzbQI;

    zzg() {
    }

    zzg(String str, zzh com_google_android_gms_wallet_wobs_zzh, String str2, zzm com_google_android_gms_wallet_wobs_zzm) {
        this.label = str;
        this.zzbQI = com_google_android_gms_wallet_wobs_zzh;
        this.type = str2;
        this.zzbPc = com_google_android_gms_wallet_wobs_zzm;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zza(parcel, 2, this.label, false);
        zzd.zza(parcel, 3, this.zzbQI, i, false);
        zzd.zza(parcel, 4, this.type, false);
        zzd.zza(parcel, 5, this.zzbPc, i, false);
        zzd.zzI(parcel, zze);
    }
}
