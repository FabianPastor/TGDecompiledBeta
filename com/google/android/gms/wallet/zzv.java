package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.v4.internal.view.SupportMenu;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.wallet.wobs.CommonWalletObject;

public final class zzv implements Creator<OfferWalletObject> {
    public final /* synthetic */ Object createFromParcel(Parcel parcel) {
        CommonWalletObject commonWalletObject = null;
        int zzd = zzb.zzd(parcel);
        String str = null;
        int i = 0;
        String str2 = null;
        while (parcel.dataPosition() < zzd) {
            int readInt = parcel.readInt();
            switch (SupportMenu.USER_MASK & readInt) {
                case 1:
                    i = zzb.zzg(parcel, readInt);
                    break;
                case 2:
                    str = zzb.zzq(parcel, readInt);
                    break;
                case 3:
                    str2 = zzb.zzq(parcel, readInt);
                    break;
                case 4:
                    commonWalletObject = (CommonWalletObject) zzb.zza(parcel, readInt, CommonWalletObject.CREATOR);
                    break;
                default:
                    zzb.zzb(parcel, readInt);
                    break;
            }
        }
        zzb.zzF(parcel, zzd);
        return new OfferWalletObject(i, str, str2, commonWalletObject);
    }

    public final /* synthetic */ Object[] newArray(int i) {
        return new OfferWalletObject[i];
    }
}
