package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;
import com.google.android.gms.wallet.wobs.CommonWalletObject;

public class zzi implements Creator<GiftCardWalletObject> {
    static void zza(GiftCardWalletObject giftCardWalletObject, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zza(parcel, 2, giftCardWalletObject.zzbQj, i, false);
        zzc.zza(parcel, 3, giftCardWalletObject.zzbQk, false);
        zzc.zza(parcel, 4, giftCardWalletObject.pin, false);
        zzc.zza(parcel, 5, giftCardWalletObject.zzbQl, false);
        zzc.zza(parcel, 6, giftCardWalletObject.zzbQm);
        zzc.zza(parcel, 7, giftCardWalletObject.zzbQn, false);
        zzc.zza(parcel, 8, giftCardWalletObject.zzbQo);
        zzc.zza(parcel, 9, giftCardWalletObject.zzbQp, false);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzkd(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzoz(i);
    }

    public GiftCardWalletObject zzkd(Parcel parcel) {
        long j = 0;
        String str = null;
        int zzaY = zzb.zzaY(parcel);
        String str2 = null;
        long j2 = 0;
        String str3 = null;
        String str4 = null;
        String str5 = null;
        CommonWalletObject commonWalletObject = null;
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 2:
                    commonWalletObject = (CommonWalletObject) zzb.zza(parcel, zzaX, CommonWalletObject.CREATOR);
                    break;
                case 3:
                    str5 = zzb.zzq(parcel, zzaX);
                    break;
                case 4:
                    str4 = zzb.zzq(parcel, zzaX);
                    break;
                case 5:
                    str3 = zzb.zzq(parcel, zzaX);
                    break;
                case 6:
                    j2 = zzb.zzi(parcel, zzaX);
                    break;
                case 7:
                    str2 = zzb.zzq(parcel, zzaX);
                    break;
                case 8:
                    j = zzb.zzi(parcel, zzaX);
                    break;
                case 9:
                    str = zzb.zzq(parcel, zzaX);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new GiftCardWalletObject(commonWalletObject, str5, str4, str3, j2, str2, j, str);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public GiftCardWalletObject[] zzoz(int i) {
        return new GiftCardWalletObject[i];
    }
}
