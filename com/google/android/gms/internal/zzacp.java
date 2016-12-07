package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzc;
import com.google.android.gms.internal.zzaco.zza;
import java.util.ArrayList;

public class zzacp implements Creator<zzaco> {
    static void zza(zzaco com_google_android_gms_internal_zzaco, Parcel parcel, int i) {
        int zzaV = zzc.zzaV(parcel);
        zzc.zzc(parcel, 1, com_google_android_gms_internal_zzaco.mVersionCode);
        zzc.zzc(parcel, 2, com_google_android_gms_internal_zzaco.zzxX(), false);
        zzc.zza(parcel, 3, com_google_android_gms_internal_zzaco.zzxY(), false);
        zzc.zzJ(parcel, zzaV);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzbc(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzde(i);
    }

    public zzaco zzbc(Parcel parcel) {
        String str = null;
        int zzaU = zzb.zzaU(parcel);
        int i = 0;
        ArrayList arrayList = null;
        while (parcel.dataPosition() < zzaU) {
            int zzaT = zzb.zzaT(parcel);
            switch (zzb.zzcW(zzaT)) {
                case 1:
                    i = zzb.zzg(parcel, zzaT);
                    break;
                case 2:
                    arrayList = zzb.zzc(parcel, zzaT, zza.CREATOR);
                    break;
                case 3:
                    str = zzb.zzq(parcel, zzaT);
                    break;
                default:
                    zzb.zzb(parcel, zzaT);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaU) {
            return new zzaco(i, arrayList, str);
        }
        throw new zzb.zza("Overread allowed size end=" + zzaU, parcel);
    }

    public zzaco[] zzde(int i) {
        return new zzaco[i];
    }
}
