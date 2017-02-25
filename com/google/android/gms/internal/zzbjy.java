package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzbjy implements Creator<zzbjx> {
    static void zza(zzbjx com_google_android_gms_internal_zzbjx, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zzc(parcel, 1, com_google_android_gms_internal_zzbjx.versionCode);
        zzc.zza(parcel, 2, com_google_android_gms_internal_zzbjx.x);
        zzc.zza(parcel, 3, com_google_android_gms_internal_zzbjx.y);
        zzc.zzc(parcel, 4, com_google_android_gms_internal_zzbjx.type);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzjP(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzok(i);
    }

    public zzbjx zzjP(Parcel parcel) {
        int i = 0;
        float f = 0.0f;
        int zzaY = zzb.zzaY(parcel);
        float f2 = 0.0f;
        int i2 = 0;
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 1:
                    i2 = zzb.zzg(parcel, zzaX);
                    break;
                case 2:
                    f2 = zzb.zzl(parcel, zzaX);
                    break;
                case 3:
                    f = zzb.zzl(parcel, zzaX);
                    break;
                case 4:
                    i = zzb.zzg(parcel, zzaX);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new zzbjx(i2, f2, f, i);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public zzbjx[] zzok(int i) {
        return new zzbjx[i];
    }
}
