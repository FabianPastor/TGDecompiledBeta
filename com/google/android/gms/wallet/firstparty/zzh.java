package com.google.android.gms.wallet.firstparty;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public final class zzh extends zza {
    public static final Creator<zzh> CREATOR = new zzi();
    zzm zzbRK;
    boolean zzbRL;

    zzh() {
    }

    zzh(zzm com_google_android_gms_wallet_firstparty_zzm, boolean z) {
        this.zzbRK = com_google_android_gms_wallet_firstparty_zzm;
        this.zzbRL = z;
        if (com_google_android_gms_wallet_firstparty_zzm == null) {
            throw new NullPointerException("WalletCustomTheme is required");
        }
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzi.zza(this, parcel, i);
    }
}
