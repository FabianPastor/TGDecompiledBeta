package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzx implements Creator<zzw> {
    static void zza(zzw com_google_android_gms_wallet_zzw, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zza(parcel, 2, com_google_android_gms_wallet_zzw.zzbRB, i, false);
        zzc.zza(parcel, 3, com_google_android_gms_wallet_zzw.zzbRC, false);
        zzc.zza(parcel, 4, com_google_android_gms_wallet_zzw.zzbRD, false);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzkq(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzoM(i);
    }

    public zzw zzkq(Parcel parcel) {
        String str = null;
        int zzaY = zzb.zzaY(parcel);
        String str2 = null;
        Cart cart = null;
        while (parcel.dataPosition() < zzaY) {
            Cart cart2;
            String str3;
            int zzaX = zzb.zzaX(parcel);
            String str4;
            switch (zzb.zzdc(zzaX)) {
                case 2:
                    str4 = str;
                    str = str2;
                    cart2 = (Cart) zzb.zza(parcel, zzaX, Cart.CREATOR);
                    str3 = str4;
                    break;
                case 3:
                    cart2 = cart;
                    str4 = zzb.zzq(parcel, zzaX);
                    str3 = str;
                    str = str4;
                    break;
                case 4:
                    str3 = zzb.zzq(parcel, zzaX);
                    str = str2;
                    cart2 = cart;
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    str3 = str;
                    str = str2;
                    cart2 = cart;
                    break;
            }
            cart = cart2;
            str2 = str;
            str = str3;
        }
        if (parcel.dataPosition() == zzaY) {
            return new zzw(cart, str2, str);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public zzw[] zzoM(int i) {
        return new zzw[i];
    }
}
