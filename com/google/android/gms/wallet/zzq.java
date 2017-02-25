package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;
import com.google.android.gms.wallet.wobs.CommonWalletObject;

public class zzq implements Creator<OfferWalletObject> {
    static void zza(OfferWalletObject offerWalletObject, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zzc(parcel, 1, offerWalletObject.getVersionCode());
        zzc.zza(parcel, 2, offerWalletObject.zzkl, false);
        zzc.zza(parcel, 3, offerWalletObject.zzbRm, false);
        zzc.zza(parcel, 4, offerWalletObject.zzbQj, i, false);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzkl(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzoH(i);
    }

    public OfferWalletObject zzkl(Parcel parcel) {
        CommonWalletObject commonWalletObject = null;
        int zzaY = zzb.zzaY(parcel);
        int i = 0;
        String str = null;
        String str2 = null;
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 1:
                    i = zzb.zzg(parcel, zzaX);
                    break;
                case 2:
                    str2 = zzb.zzq(parcel, zzaX);
                    break;
                case 3:
                    str = zzb.zzq(parcel, zzaX);
                    break;
                case 4:
                    commonWalletObject = (CommonWalletObject) zzb.zza(parcel, zzaX, CommonWalletObject.CREATOR);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new OfferWalletObject(i, str2, str, commonWalletObject);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public OfferWalletObject[] zzoH(int i) {
        return new OfferWalletObject[i];
    }
}
