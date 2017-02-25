package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;
import java.util.ArrayList;

public class zzk implements Creator<IsReadyToPayRequest> {
    static void zza(IsReadyToPayRequest isReadyToPayRequest, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zza(parcel, 2, isReadyToPayRequest.zzbQs, false);
        zzc.zza(parcel, 4, isReadyToPayRequest.zzbQt, false);
        zzc.zza(parcel, 5, isReadyToPayRequest.zzbQu, false);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzkf(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzoB(i);
    }

    public IsReadyToPayRequest zzkf(Parcel parcel) {
        String str = null;
        int zzaY = zzb.zzaY(parcel);
        String str2 = null;
        ArrayList arrayList = null;
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 2:
                    arrayList = zzb.zzD(parcel, zzaX);
                    break;
                case 4:
                    str2 = zzb.zzq(parcel, zzaX);
                    break;
                case 5:
                    str = zzb.zzq(parcel, zzaX);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new IsReadyToPayRequest(arrayList, str2, str);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public IsReadyToPayRequest[] zzoB(int i) {
        return new IsReadyToPayRequest[i];
    }
}
