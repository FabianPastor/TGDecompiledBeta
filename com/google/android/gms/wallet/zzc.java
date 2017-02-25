package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import java.util.ArrayList;

public class zzc implements Creator<Cart> {
    static void zza(Cart cart, Parcel parcel, int i) {
        int zzaZ = com.google.android.gms.common.internal.safeparcel.zzc.zzaZ(parcel);
        com.google.android.gms.common.internal.safeparcel.zzc.zza(parcel, 2, cart.zzbPP, false);
        com.google.android.gms.common.internal.safeparcel.zzc.zza(parcel, 3, cart.zzbPQ, false);
        com.google.android.gms.common.internal.safeparcel.zzc.zzc(parcel, 4, cart.zzbPR, false);
        com.google.android.gms.common.internal.safeparcel.zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzjY(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzou(i);
    }

    public Cart zzjY(Parcel parcel) {
        String str = null;
        int zzaY = zzb.zzaY(parcel);
        ArrayList arrayList = new ArrayList();
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
                    arrayList = zzb.zzc(parcel, zzaX, LineItem.CREATOR);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new Cart(str2, str, arrayList);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public Cart[] zzou(int i) {
        return new Cart[i];
    }
}
