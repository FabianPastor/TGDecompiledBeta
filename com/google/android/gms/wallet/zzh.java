package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzh implements Creator<FullWalletRequest> {
    static void zza(FullWalletRequest fullWalletRequest, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zza(parcel, 2, fullWalletRequest.zzbPW, false);
        zzc.zza(parcel, 3, fullWalletRequest.zzbPX, false);
        zzc.zza(parcel, 4, fullWalletRequest.zzbQh, i, false);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzkc(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzoy(i);
    }

    public FullWalletRequest zzkc(Parcel parcel) {
        Cart cart = null;
        int zzaY = zzb.zzaY(parcel);
        String str = null;
        String str2 = null;
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 2:
                    str2 = zzb.zzq(parcel, zzaX);
                    break;
                case 3:
                    str = zzb.zzq(parcel, zzaX);
                    break;
                case 4:
                    cart = (Cart) zzb.zza(parcel, zzaX, Cart.CREATOR);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new FullWalletRequest(str2, str, cart);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public FullWalletRequest[] zzoy(int i) {
        return new FullWalletRequest[i];
    }
}
