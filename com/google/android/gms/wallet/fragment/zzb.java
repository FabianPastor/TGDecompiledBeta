package com.google.android.gms.wallet.fragment;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzb implements Creator<WalletFragmentOptions> {
    static void zza(WalletFragmentOptions walletFragmentOptions, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zzc(parcel, 2, walletFragmentOptions.getEnvironment());
        zzc.zzc(parcel, 3, walletFragmentOptions.getTheme());
        zzc.zza(parcel, 4, walletFragmentOptions.getFragmentStyle(), i, false);
        zzc.zzc(parcel, 5, walletFragmentOptions.getMode());
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzkz(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzoV(i);
    }

    public WalletFragmentOptions zzkz(Parcel parcel) {
        int i = 1;
        int zzaY = com.google.android.gms.common.internal.safeparcel.zzb.zzaY(parcel);
        int i2 = 0;
        WalletFragmentStyle walletFragmentStyle = null;
        int i3 = 1;
        while (parcel.dataPosition() < zzaY) {
            WalletFragmentStyle walletFragmentStyle2;
            int i4;
            int zzaX = com.google.android.gms.common.internal.safeparcel.zzb.zzaX(parcel);
            int i5;
            switch (com.google.android.gms.common.internal.safeparcel.zzb.zzdc(zzaX)) {
                case 2:
                    i5 = i;
                    walletFragmentStyle2 = walletFragmentStyle;
                    i4 = i2;
                    i2 = com.google.android.gms.common.internal.safeparcel.zzb.zzg(parcel, zzaX);
                    zzaX = i5;
                    break;
                case 3:
                    i2 = i3;
                    WalletFragmentStyle walletFragmentStyle3 = walletFragmentStyle;
                    i4 = com.google.android.gms.common.internal.safeparcel.zzb.zzg(parcel, zzaX);
                    zzaX = i;
                    walletFragmentStyle2 = walletFragmentStyle3;
                    break;
                case 4:
                    i4 = i2;
                    i2 = i3;
                    i5 = i;
                    walletFragmentStyle2 = (WalletFragmentStyle) com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, zzaX, WalletFragmentStyle.CREATOR);
                    zzaX = i5;
                    break;
                case 5:
                    zzaX = com.google.android.gms.common.internal.safeparcel.zzb.zzg(parcel, zzaX);
                    walletFragmentStyle2 = walletFragmentStyle;
                    i4 = i2;
                    i2 = i3;
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.zzb.zzb(parcel, zzaX);
                    zzaX = i;
                    walletFragmentStyle2 = walletFragmentStyle;
                    i4 = i2;
                    i2 = i3;
                    break;
            }
            i3 = i2;
            i2 = i4;
            walletFragmentStyle = walletFragmentStyle2;
            i = zzaX;
        }
        if (parcel.dataPosition() == zzaY) {
            return new WalletFragmentOptions(i3, i2, walletFragmentStyle, i);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public WalletFragmentOptions[] zzoV(int i) {
        return new WalletFragmentOptions[i];
    }
}
