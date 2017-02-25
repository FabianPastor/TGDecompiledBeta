package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzp implements Creator<NotifyTransactionStatusRequest> {
    static void zza(NotifyTransactionStatusRequest notifyTransactionStatusRequest, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zza(parcel, 2, notifyTransactionStatusRequest.zzbPW, false);
        zzc.zzc(parcel, 3, notifyTransactionStatusRequest.status);
        zzc.zza(parcel, 4, notifyTransactionStatusRequest.zzbRk, false);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzkk(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzoG(i);
    }

    public NotifyTransactionStatusRequest zzkk(Parcel parcel) {
        String str = null;
        int zzaY = zzb.zzaY(parcel);
        int i = 0;
        String str2 = null;
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 2:
                    str2 = zzb.zzq(parcel, zzaX);
                    break;
                case 3:
                    i = zzb.zzg(parcel, zzaX);
                    break;
                case 4:
                    str = zzb.zzq(parcel, zzaX);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new NotifyTransactionStatusRequest(str2, i, str);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public NotifyTransactionStatusRequest[] zzoG(int i) {
        return new NotifyTransactionStatusRequest[i];
    }
}
