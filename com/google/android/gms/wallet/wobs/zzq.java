package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public final class zzq extends zza {
    public static final Creator<zzq> CREATOR = new zzr();
    private String body;
    private String zzbQO;
    private zzm zzbQS;
    private zzo zzbQT;
    private zzo zzbQU;

    zzq() {
    }

    zzq(String str, String str2, zzm com_google_android_gms_wallet_wobs_zzm, zzo com_google_android_gms_wallet_wobs_zzo, zzo com_google_android_gms_wallet_wobs_zzo2) {
        this.zzbQO = str;
        this.body = str2;
        this.zzbQS = com_google_android_gms_wallet_wobs_zzm;
        this.zzbQT = com_google_android_gms_wallet_wobs_zzo;
        this.zzbQU = com_google_android_gms_wallet_wobs_zzo2;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zza(parcel, 2, this.zzbQO, false);
        zzd.zza(parcel, 3, this.body, false);
        zzd.zza(parcel, 4, this.zzbQS, i, false);
        zzd.zza(parcel, 5, this.zzbQT, i, false);
        zzd.zza(parcel, 6, this.zzbQU, i, false);
        zzd.zzI(parcel, zze);
    }
}
