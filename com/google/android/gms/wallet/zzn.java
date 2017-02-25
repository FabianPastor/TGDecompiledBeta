package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;
import com.google.android.gms.identity.intents.model.UserAddress;

public class zzn implements Creator<MaskedWallet> {
    static void zza(MaskedWallet maskedWallet, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zza(parcel, 2, maskedWallet.zzbPW, false);
        zzc.zza(parcel, 3, maskedWallet.zzbPX, false);
        zzc.zza(parcel, 4, maskedWallet.zzbQc, false);
        zzc.zza(parcel, 5, maskedWallet.zzbPZ, false);
        zzc.zza(parcel, 6, maskedWallet.zzbQa, i, false);
        zzc.zza(parcel, 7, maskedWallet.zzbQb, i, false);
        zzc.zza(parcel, 8, maskedWallet.zzbQT, i, false);
        zzc.zza(parcel, 9, maskedWallet.zzbQU, i, false);
        zzc.zza(parcel, 10, maskedWallet.zzbQd, i, false);
        zzc.zza(parcel, 11, maskedWallet.zzbQe, i, false);
        zzc.zza(parcel, 12, maskedWallet.zzbQf, i, false);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzki(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzoE(i);
    }

    public MaskedWallet zzki(Parcel parcel) {
        InstrumentInfo[] instrumentInfoArr = null;
        int zzaY = zzb.zzaY(parcel);
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
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 2:
                    str3 = zzb.zzq(parcel, zzaX);
                    break;
                case 3:
                    str2 = zzb.zzq(parcel, zzaX);
                    break;
                case 4:
                    strArr = zzb.zzC(parcel, zzaX);
                    break;
                case 5:
                    str = zzb.zzq(parcel, zzaX);
                    break;
                case 6:
                    com_google_android_gms_wallet_zza2 = (zza) zzb.zza(parcel, zzaX, zza.CREATOR);
                    break;
                case 7:
                    com_google_android_gms_wallet_zza = (zza) zzb.zza(parcel, zzaX, zza.CREATOR);
                    break;
                case 8:
                    loyaltyWalletObjectArr = (LoyaltyWalletObject[]) zzb.zzb(parcel, zzaX, LoyaltyWalletObject.CREATOR);
                    break;
                case 9:
                    offerWalletObjectArr = (OfferWalletObject[]) zzb.zzb(parcel, zzaX, OfferWalletObject.CREATOR);
                    break;
                case 10:
                    userAddress2 = (UserAddress) zzb.zza(parcel, zzaX, UserAddress.CREATOR);
                    break;
                case 11:
                    userAddress = (UserAddress) zzb.zza(parcel, zzaX, UserAddress.CREATOR);
                    break;
                case 12:
                    instrumentInfoArr = (InstrumentInfo[]) zzb.zzb(parcel, zzaX, InstrumentInfo.CREATOR);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new MaskedWallet(str3, str2, strArr, str, com_google_android_gms_wallet_zza2, com_google_android_gms_wallet_zza, loyaltyWalletObjectArr, offerWalletObjectArr, userAddress2, userAddress, instrumentInfoArr);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public MaskedWallet[] zzoE(int i) {
        return new MaskedWallet[i];
    }
}
