package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public final class zzf extends zza {
    public static final Creator<zzf> CREATOR = new zzi();
    String label;
    String type;
    zzl zzbQJ;
    zzg zzbSC;

    zzf() {
    }

    zzf(String str, zzg com_google_android_gms_wallet_wobs_zzg, String str2, zzl com_google_android_gms_wallet_wobs_zzl) {
        this.label = str;
        this.zzbSC = com_google_android_gms_wallet_wobs_zzg;
        this.type = str2;
        this.zzbQJ = com_google_android_gms_wallet_wobs_zzl;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzi.zza(this, parcel, i);
    }
}
