package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public final class zze extends zza {
    public static final Creator<zze> CREATOR = new zzf();
    LoyaltyWalletObject zzbPT;
    OfferWalletObject zzbPU;
    GiftCardWalletObject zzbPV;

    zze() {
    }

    zze(LoyaltyWalletObject loyaltyWalletObject, OfferWalletObject offerWalletObject, GiftCardWalletObject giftCardWalletObject) {
        this.zzbPT = loyaltyWalletObject;
        this.zzbPU = offerWalletObject;
        this.zzbPV = giftCardWalletObject;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzf.zza(this, parcel, i);
    }
}
