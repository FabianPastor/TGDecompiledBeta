package com.google.android.gms.wallet.fragment;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfn;

public final class zzf implements Creator<WalletFragmentOptions> {
    public final /* synthetic */ Object createFromParcel(Parcel parcel) {
        int zzd = zzbfn.zzd(parcel);
        int i = 0;
        int i2 = 1;
        WalletFragmentStyle walletFragmentStyle = null;
        int i3 = 1;
        while (parcel.dataPosition() < zzd) {
            int readInt = parcel.readInt();
            switch (65535 & readInt) {
                case 2:
                    i2 = zzbfn.zzg(parcel, readInt);
                    break;
                case 3:
                    i = zzbfn.zzg(parcel, readInt);
                    break;
                case 4:
                    walletFragmentStyle = (WalletFragmentStyle) zzbfn.zza(parcel, readInt, WalletFragmentStyle.CREATOR);
                    break;
                case 5:
                    i3 = zzbfn.zzg(parcel, readInt);
                    break;
                default:
                    zzbfn.zzb(parcel, readInt);
                    break;
            }
        }
        zzbfn.zzaf(parcel, zzd);
        return new WalletFragmentOptions(i2, i, walletFragmentStyle, i3);
    }

    public final /* synthetic */ Object[] newArray(int i) {
        return new WalletFragmentOptions[i];
    }
}
