package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.wallet.wobs.CommonWalletObject;

public final class OfferWalletObject extends zza {
    public static final Creator<OfferWalletObject> CREATOR = new zzv();
    private final int zzaku;
    private CommonWalletObject zzbOD;
    private String zzbPH;

    OfferWalletObject() {
        this.zzaku = 3;
    }

    OfferWalletObject(int i, String str, String str2, CommonWalletObject commonWalletObject) {
        this.zzaku = i;
        this.zzbPH = str2;
        if (i < 3) {
            this.zzbOD = CommonWalletObject.zzDU().zzgi(str).zzDV();
        } else {
            this.zzbOD = commonWalletObject;
        }
    }

    public final String getId() {
        return this.zzbOD.getId();
    }

    public final String getRedemptionCode() {
        return this.zzbPH;
    }

    public final int getVersionCode() {
        return this.zzaku;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zzc(parcel, 1, getVersionCode());
        zzd.zza(parcel, 2, null, false);
        zzd.zza(parcel, 3, this.zzbPH, false);
        zzd.zza(parcel, 4, this.zzbOD, i, false);
        zzd.zzI(parcel, zze);
    }
}
