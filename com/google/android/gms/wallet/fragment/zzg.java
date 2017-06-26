package com.google.android.gms.wallet.fragment;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.v4.internal.view.SupportMenu;
import com.google.android.gms.common.internal.safeparcel.zzb;

public final class zzg implements Creator<WalletFragmentStyle> {
    public final /* synthetic */ Object createFromParcel(Parcel parcel) {
        int zzd = zzb.zzd(parcel);
        Bundle bundle = null;
        int i = 0;
        while (parcel.dataPosition() < zzd) {
            int readInt = parcel.readInt();
            switch (SupportMenu.USER_MASK & readInt) {
                case 2:
                    bundle = zzb.zzs(parcel, readInt);
                    break;
                case 3:
                    i = zzb.zzg(parcel, readInt);
                    break;
                default:
                    zzb.zzb(parcel, readInt);
                    break;
            }
        }
        zzb.zzF(parcel, zzd);
        return new WalletFragmentStyle(bundle, i);
    }

    public final /* synthetic */ Object[] newArray(int i) {
        return new WalletFragmentStyle[i];
    }
}
