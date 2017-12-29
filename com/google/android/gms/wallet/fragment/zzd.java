package com.google.android.gms.wallet.fragment;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfn;
import com.google.android.gms.wallet.MaskedWallet;
import com.google.android.gms.wallet.MaskedWalletRequest;

public final class zzd implements Creator<WalletFragmentInitParams> {
    public final /* synthetic */ Object createFromParcel(Parcel parcel) {
        int zzd = zzbfn.zzd(parcel);
        int i = -1;
        MaskedWalletRequest maskedWalletRequest = null;
        String str = null;
        MaskedWallet maskedWallet = null;
        while (parcel.dataPosition() < zzd) {
            int readInt = parcel.readInt();
            switch (65535 & readInt) {
                case 2:
                    str = zzbfn.zzq(parcel, readInt);
                    break;
                case 3:
                    maskedWalletRequest = (MaskedWalletRequest) zzbfn.zza(parcel, readInt, MaskedWalletRequest.CREATOR);
                    break;
                case 4:
                    i = zzbfn.zzg(parcel, readInt);
                    break;
                case 5:
                    maskedWallet = (MaskedWallet) zzbfn.zza(parcel, readInt, MaskedWallet.CREATOR);
                    break;
                default:
                    zzbfn.zzb(parcel, readInt);
                    break;
            }
        }
        zzbfn.zzaf(parcel, zzd);
        return new WalletFragmentInitParams(str, maskedWalletRequest, i, maskedWallet);
    }

    public final /* synthetic */ Object[] newArray(int i) {
        return new WalletFragmentInitParams[i];
    }
}
