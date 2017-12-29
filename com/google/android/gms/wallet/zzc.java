package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.identity.intents.model.UserAddress;
import com.google.android.gms.internal.zzbfn;

public final class zzc implements Creator<CardInfo> {
    public final /* synthetic */ Object createFromParcel(Parcel parcel) {
        UserAddress userAddress = null;
        int zzd = zzbfn.zzd(parcel);
        int i = 0;
        String str = null;
        String str2 = null;
        String str3 = null;
        while (parcel.dataPosition() < zzd) {
            int readInt = parcel.readInt();
            switch (65535 & readInt) {
                case 1:
                    str3 = zzbfn.zzq(parcel, readInt);
                    break;
                case 2:
                    str2 = zzbfn.zzq(parcel, readInt);
                    break;
                case 3:
                    str = zzbfn.zzq(parcel, readInt);
                    break;
                case 4:
                    i = zzbfn.zzg(parcel, readInt);
                    break;
                case 5:
                    userAddress = (UserAddress) zzbfn.zza(parcel, readInt, UserAddress.CREATOR);
                    break;
                default:
                    zzbfn.zzb(parcel, readInt);
                    break;
            }
        }
        zzbfn.zzaf(parcel, zzd);
        return new CardInfo(str3, str2, str, i, userAddress);
    }

    public final /* synthetic */ Object[] newArray(int i) {
        return new CardInfo[i];
    }
}
