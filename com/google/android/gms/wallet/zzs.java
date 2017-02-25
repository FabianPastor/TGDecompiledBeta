package com.google.android.gms.wallet;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzs implements Creator<PaymentMethodTokenizationParameters> {
    static void zza(PaymentMethodTokenizationParameters paymentMethodTokenizationParameters, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zzc(parcel, 2, paymentMethodTokenizationParameters.zzbRn);
        zzc.zza(parcel, 3, paymentMethodTokenizationParameters.zzbRo, false);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzkn(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzoJ(i);
    }

    public PaymentMethodTokenizationParameters zzkn(Parcel parcel) {
        int zzaY = zzb.zzaY(parcel);
        int i = 0;
        Bundle bundle = null;
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 2:
                    i = zzb.zzg(parcel, zzaX);
                    break;
                case 3:
                    bundle = zzb.zzs(parcel, zzaX);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new PaymentMethodTokenizationParameters(i, bundle);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public PaymentMethodTokenizationParameters[] zzoJ(int i) {
        return new PaymentMethodTokenizationParameters[i];
    }
}
