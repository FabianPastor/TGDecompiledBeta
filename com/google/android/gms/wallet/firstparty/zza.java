package com.google.android.gms.wallet.firstparty;

import android.os.Parcel;
import android.os.Parcelable.Creator;

public class zza extends com.google.android.gms.common.internal.safeparcel.zza {
    public static final Creator<zza> CREATOR = new zzb();
    byte[] zzbRE;
    byte[] zzbRF;
    zzm zzbRG;

    public zza(byte[] bArr, byte[] bArr2, zzm com_google_android_gms_wallet_firstparty_zzm) {
        this.zzbRE = bArr;
        this.zzbRF = bArr2;
        this.zzbRG = com_google_android_gms_wallet_firstparty_zzm;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzb.zza(this, parcel, i);
    }
}
