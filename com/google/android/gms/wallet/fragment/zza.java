package com.google.android.gms.wallet.fragment;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzc;
import com.google.android.gms.wallet.MaskedWallet;
import com.google.android.gms.wallet.MaskedWalletRequest;

public class zza implements Creator<WalletFragmentInitParams> {
    static void zza(WalletFragmentInitParams walletFragmentInitParams, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zza(parcel, 2, walletFragmentInitParams.getAccountName(), false);
        zzc.zza(parcel, 3, walletFragmentInitParams.getMaskedWalletRequest(), i, false);
        zzc.zzc(parcel, 4, walletFragmentInitParams.getMaskedWalletRequestCode());
        zzc.zza(parcel, 5, walletFragmentInitParams.getMaskedWallet(), i, false);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzky(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzoU(i);
    }

    public WalletFragmentInitParams zzky(Parcel parcel) {
        MaskedWallet maskedWallet = null;
        int zzaY = zzb.zzaY(parcel);
        int i = -1;
        MaskedWalletRequest maskedWalletRequest = null;
        String str = null;
        while (parcel.dataPosition() < zzaY) {
            int i2;
            MaskedWalletRequest maskedWalletRequest2;
            String zzq;
            MaskedWallet maskedWallet2;
            int zzaX = zzb.zzaX(parcel);
            MaskedWallet maskedWallet3;
            switch (zzb.zzdc(zzaX)) {
                case 2:
                    maskedWallet3 = maskedWallet;
                    i2 = i;
                    maskedWalletRequest2 = maskedWalletRequest;
                    zzq = zzb.zzq(parcel, zzaX);
                    maskedWallet2 = maskedWallet3;
                    break;
                case 3:
                    zzq = str;
                    int i3 = i;
                    maskedWalletRequest2 = (MaskedWalletRequest) zzb.zza(parcel, zzaX, MaskedWalletRequest.CREATOR);
                    maskedWallet2 = maskedWallet;
                    i2 = i3;
                    break;
                case 4:
                    maskedWalletRequest2 = maskedWalletRequest;
                    zzq = str;
                    maskedWallet3 = maskedWallet;
                    i2 = zzb.zzg(parcel, zzaX);
                    maskedWallet2 = maskedWallet3;
                    break;
                case 5:
                    maskedWallet2 = (MaskedWallet) zzb.zza(parcel, zzaX, MaskedWallet.CREATOR);
                    i2 = i;
                    maskedWalletRequest2 = maskedWalletRequest;
                    zzq = str;
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    maskedWallet2 = maskedWallet;
                    i2 = i;
                    maskedWalletRequest2 = maskedWalletRequest;
                    zzq = str;
                    break;
            }
            str = zzq;
            maskedWalletRequest = maskedWalletRequest2;
            i = i2;
            maskedWallet = maskedWallet2;
        }
        if (parcel.dataPosition() == zzaY) {
            return new WalletFragmentInitParams(str, maskedWalletRequest, i, maskedWallet);
        }
        throw new com.google.android.gms.common.internal.safeparcel.zzb.zza("Overread allowed size end=" + zzaY, parcel);
    }

    public WalletFragmentInitParams[] zzoU(int i) {
        return new WalletFragmentInitParams[i];
    }
}
