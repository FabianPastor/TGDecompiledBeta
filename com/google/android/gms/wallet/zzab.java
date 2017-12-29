package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfn;
import com.google.android.gms.wallet.wobs.CommonWalletObject;

public final class zzab implements Creator<OfferWalletObject> {
    public final /* synthetic */ Object createFromParcel(Parcel parcel) {
        CommonWalletObject commonWalletObject = null;
        int zzd = zzbfn.zzd(parcel);
        String str = null;
        int i = 0;
        String str2 = null;
        while (parcel.dataPosition() < zzd) {
            int readInt = parcel.readInt();
            switch (65535 & readInt) {
                case 1:
                    i = zzbfn.zzg(parcel, readInt);
                    break;
                case 2:
                    str = zzbfn.zzq(parcel, readInt);
                    break;
                case 3:
                    str2 = zzbfn.zzq(parcel, readInt);
                    break;
                case 4:
                    commonWalletObject = (CommonWalletObject) zzbfn.zza(parcel, readInt, CommonWalletObject.CREATOR);
                    break;
                default:
                    zzbfn.zzb(parcel, readInt);
                    break;
            }
        }
        zzbfn.zzaf(parcel, zzd);
        return new OfferWalletObject(i, str, str2, commonWalletObject);
    }

    public final /* synthetic */ Object[] newArray(int i) {
        return new OfferWalletObject[i];
    }
}
