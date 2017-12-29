package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.identity.intents.model.UserAddress;
import com.google.android.gms.internal.zzbfn;

public final class zzx implements Creator<MaskedWallet> {
    public final /* synthetic */ Object createFromParcel(Parcel parcel) {
        InstrumentInfo[] instrumentInfoArr = null;
        int zzd = zzbfn.zzd(parcel);
        UserAddress userAddress = null;
        UserAddress userAddress2 = null;
        OfferWalletObject[] offerWalletObjectArr = null;
        LoyaltyWalletObject[] loyaltyWalletObjectArr = null;
        zza com_google_android_gms_wallet_zza = null;
        zza com_google_android_gms_wallet_zza2 = null;
        String str = null;
        String[] strArr = null;
        String str2 = null;
        String str3 = null;
        while (parcel.dataPosition() < zzd) {
            int readInt = parcel.readInt();
            switch (65535 & readInt) {
                case 2:
                    str3 = zzbfn.zzq(parcel, readInt);
                    break;
                case 3:
                    str2 = zzbfn.zzq(parcel, readInt);
                    break;
                case 4:
                    strArr = zzbfn.zzaa(parcel, readInt);
                    break;
                case 5:
                    str = zzbfn.zzq(parcel, readInt);
                    break;
                case 6:
                    com_google_android_gms_wallet_zza2 = (zza) zzbfn.zza(parcel, readInt, zza.CREATOR);
                    break;
                case 7:
                    com_google_android_gms_wallet_zza = (zza) zzbfn.zza(parcel, readInt, zza.CREATOR);
                    break;
                case 8:
                    loyaltyWalletObjectArr = (LoyaltyWalletObject[]) zzbfn.zzb(parcel, readInt, LoyaltyWalletObject.CREATOR);
                    break;
                case 9:
                    offerWalletObjectArr = (OfferWalletObject[]) zzbfn.zzb(parcel, readInt, OfferWalletObject.CREATOR);
                    break;
                case 10:
                    userAddress2 = (UserAddress) zzbfn.zza(parcel, readInt, UserAddress.CREATOR);
                    break;
                case 11:
                    userAddress = (UserAddress) zzbfn.zza(parcel, readInt, UserAddress.CREATOR);
                    break;
                case 12:
                    instrumentInfoArr = (InstrumentInfo[]) zzbfn.zzb(parcel, readInt, InstrumentInfo.CREATOR);
                    break;
                default:
                    zzbfn.zzb(parcel, readInt);
                    break;
            }
        }
        zzbfn.zzaf(parcel, zzd);
        return new MaskedWallet(str3, str2, strArr, str, com_google_android_gms_wallet_zza2, com_google_android_gms_wallet_zza, loyaltyWalletObjectArr, offerWalletObjectArr, userAddress2, userAddress, instrumentInfoArr);
    }

    public final /* synthetic */ Object[] newArray(int i) {
        return new MaskedWallet[i];
    }
}
