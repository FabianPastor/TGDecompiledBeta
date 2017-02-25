package com.google.android.gms.wallet.fragment;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;

public class zzc implements Creator<WalletFragmentStyle> {
    static void zza(WalletFragmentStyle walletFragmentStyle, Parcel parcel, int i) {
        int zzaZ = com.google.android.gms.common.internal.safeparcel.zzc.zzaZ(parcel);
        com.google.android.gms.common.internal.safeparcel.zzc.zza(parcel, 2, walletFragmentStyle.zzbRP, false);
        com.google.android.gms.common.internal.safeparcel.zzc.zzc(parcel, 3, walletFragmentStyle.zzbSp);
        com.google.android.gms.common.internal.safeparcel.zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzkA(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzoX(i);
    }

    public WalletFragmentStyle zzkA(Parcel parcel) {
        int zzaY = zzb.zzaY(parcel);
        Bundle bundle = null;
        int i = 0;
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 2:
                    bundle = zzb.zzs(parcel, zzaX);
                    break;
                case 3:
                    i = zzb.zzg(parcel, zzaX);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new WalletFragmentStyle(bundle, i);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public WalletFragmentStyle[] zzoX(int i) {
        return new WalletFragmentStyle[i];
    }
}
