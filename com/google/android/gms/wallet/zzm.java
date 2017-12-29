package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfn;

public final class zzm implements Creator<FullWalletRequest> {
    public final /* synthetic */ Object createFromParcel(Parcel parcel) {
        Cart cart = null;
        int zzd = zzbfn.zzd(parcel);
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
                    cart = (Cart) zzbfn.zza(parcel, readInt, Cart.CREATOR);
                    break;
                default:
                    zzbfn.zzb(parcel, readInt);
                    break;
            }
        }
        zzbfn.zzaf(parcel, zzd);
        return new FullWalletRequest(str2, str, cart);
    }

    public final /* synthetic */ Object[] newArray(int i) {
        return new FullWalletRequest[i];
    }
}
