package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzj implements Creator<InstrumentInfo> {
    static void zza(InstrumentInfo instrumentInfo, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zza(parcel, 2, instrumentInfo.getInstrumentType(), false);
        zzc.zza(parcel, 3, instrumentInfo.getInstrumentDetails(), false);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzke(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzoA(i);
    }

    public InstrumentInfo zzke(Parcel parcel) {
        String str = null;
        int zzaY = zzb.zzaY(parcel);
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
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new InstrumentInfo(str2, str);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public InstrumentInfo[] zzoA(int i) {
        return new InstrumentInfo[i];
    }
}
