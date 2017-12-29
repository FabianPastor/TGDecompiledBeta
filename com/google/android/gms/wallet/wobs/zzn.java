package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfn;

public final class zzn implements Creator<WalletObjectMessage> {
    public final /* synthetic */ Object createFromParcel(Parcel parcel) {
        UriData uriData = null;
        int zzd = zzbfn.zzd(parcel);
        UriData uriData2 = null;
        TimeInterval timeInterval = null;
        String str = null;
        String str2 = null;
        while (parcel.dataPosition() < zzd) {
            int readInt = parcel.readInt();
            switch (65535 & readInt) {
                case 2:
                    str2 = zzbfn.zzq(parcel, readInt);
                    break;
                case 3:
                    str = zzbfn.zzq(parcel, readInt);
                    break;
                case 4:
                    timeInterval = (TimeInterval) zzbfn.zza(parcel, readInt, TimeInterval.CREATOR);
                    break;
                case 5:
                    uriData2 = (UriData) zzbfn.zza(parcel, readInt, UriData.CREATOR);
                    break;
                case 6:
                    uriData = (UriData) zzbfn.zza(parcel, readInt, UriData.CREATOR);
                    break;
                default:
                    zzbfn.zzb(parcel, readInt);
                    break;
            }
        }
        zzbfn.zzaf(parcel, zzd);
        return new WalletObjectMessage(str2, str, timeInterval, uriData2, uriData);
    }

    public final /* synthetic */ Object[] newArray(int i) {
        return new WalletObjectMessage[i];
    }
}
