package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzf implements Creator<zze> {
    static void zza(zze com_google_android_gms_wallet_zze, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zza(parcel, 2, com_google_android_gms_wallet_zze.zzbPT, i, false);
        zzc.zza(parcel, 3, com_google_android_gms_wallet_zze.zzbPU, i, false);
        zzc.zza(parcel, 4, com_google_android_gms_wallet_zze.zzbPV, i, false);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzka(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzow(i);
    }

    public zze zzka(Parcel parcel) {
        GiftCardWalletObject giftCardWalletObject = null;
        int zzaY = zzb.zzaY(parcel);
        OfferWalletObject offerWalletObject = null;
        LoyaltyWalletObject loyaltyWalletObject = null;
        while (parcel.dataPosition() < zzaY) {
            OfferWalletObject offerWalletObject2;
            LoyaltyWalletObject loyaltyWalletObject2;
            GiftCardWalletObject giftCardWalletObject2;
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 2:
                    GiftCardWalletObject giftCardWalletObject3 = giftCardWalletObject;
                    offerWalletObject2 = offerWalletObject;
                    loyaltyWalletObject2 = (LoyaltyWalletObject) zzb.zza(parcel, zzaX, LoyaltyWalletObject.CREATOR);
                    giftCardWalletObject2 = giftCardWalletObject3;
                    break;
                case 3:
                    loyaltyWalletObject2 = loyaltyWalletObject;
                    OfferWalletObject offerWalletObject3 = (OfferWalletObject) zzb.zza(parcel, zzaX, OfferWalletObject.CREATOR);
                    giftCardWalletObject2 = giftCardWalletObject;
                    offerWalletObject2 = offerWalletObject3;
                    break;
                case 4:
                    giftCardWalletObject2 = (GiftCardWalletObject) zzb.zza(parcel, zzaX, GiftCardWalletObject.CREATOR);
                    offerWalletObject2 = offerWalletObject;
                    loyaltyWalletObject2 = loyaltyWalletObject;
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    giftCardWalletObject2 = giftCardWalletObject;
                    offerWalletObject2 = offerWalletObject;
                    loyaltyWalletObject2 = loyaltyWalletObject;
                    break;
            }
            loyaltyWalletObject = loyaltyWalletObject2;
            offerWalletObject = offerWalletObject2;
            giftCardWalletObject = giftCardWalletObject2;
        }
        if (parcel.dataPosition() == zzaY) {
            return new zze(loyaltyWalletObject, offerWalletObject, giftCardWalletObject);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public zze[] zzow(int i) {
        return new zze[i];
    }
}
