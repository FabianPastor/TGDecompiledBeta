package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzbjm implements Creator<zzbjl> {
    static void zza(zzbjl com_google_android_gms_internal_zzbjl, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zzc(parcel, 1, com_google_android_gms_internal_zzbjl.versionCode);
        zzc.zzc(parcel, 2, com_google_android_gms_internal_zzbjl.zzbOG);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzjM(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzog(i);
    }

    public zzbjl zzjM(Parcel parcel) {
        int i = 0;
        int zzaY = zzb.zzaY(parcel);
        int i2 = 0;
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 1:
                    i2 = zzb.zzg(parcel, zzaX);
                    break;
                case 2:
                    i = zzb.zzg(parcel, zzaX);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new zzbjl(i2, i);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public zzbjl[] zzog(int i) {
        return new zzbjl[i];
    }
}
