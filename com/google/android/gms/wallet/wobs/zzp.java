package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public final class zzp extends zza {
    public static final Creator<zzp> CREATOR = new zzq();
    String body;
    String zzbSI;
    zzl zzbSM;
    zzn zzbSN;
    zzn zzbSO;

    zzp() {
    }

    zzp(String str, String str2, zzl com_google_android_gms_wallet_wobs_zzl, zzn com_google_android_gms_wallet_wobs_zzn, zzn com_google_android_gms_wallet_wobs_zzn2) {
        this.zzbSI = str;
        this.body = str2;
        this.zzbSM = com_google_android_gms_wallet_wobs_zzl;
        this.zzbSN = com_google_android_gms_wallet_wobs_zzn;
        this.zzbSO = com_google_android_gms_wallet_wobs_zzn2;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzq.zza(this, parcel, i);
    }
}
