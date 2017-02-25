package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzbju implements Creator<zzbjt> {
    static void zza(zzbjt com_google_android_gms_internal_zzbjt, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zzc(parcel, 1, com_google_android_gms_internal_zzbjt.versionCode);
        zzc.zzc(parcel, 2, com_google_android_gms_internal_zzbjt.mode);
        zzc.zzc(parcel, 3, com_google_android_gms_internal_zzbjt.zzbPi);
        zzc.zzc(parcel, 4, com_google_android_gms_internal_zzbjt.zzbPj);
        zzc.zza(parcel, 5, com_google_android_gms_internal_zzbjt.zzbPk);
        zzc.zza(parcel, 6, com_google_android_gms_internal_zzbjt.zzbPl);
        zzc.zza(parcel, 7, com_google_android_gms_internal_zzbjt.zzbPm);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzjO(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzoj(i);
    }

    public zzbjt zzjO(Parcel parcel) {
        boolean z = false;
        int zzaY = zzb.zzaY(parcel);
        float f = -1.0f;
        boolean z2 = false;
        int i = 0;
        int i2 = 0;
        int i3 = 0;
        int i4 = 0;
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 1:
                    i4 = zzb.zzg(parcel, zzaX);
                    break;
                case 2:
                    i3 = zzb.zzg(parcel, zzaX);
                    break;
                case 3:
                    i2 = zzb.zzg(parcel, zzaX);
                    break;
                case 4:
                    i = zzb.zzg(parcel, zzaX);
                    break;
                case 5:
                    z2 = zzb.zzc(parcel, zzaX);
                    break;
                case 6:
                    z = zzb.zzc(parcel, zzaX);
                    break;
                case 7:
                    f = zzb.zzl(parcel, zzaX);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new zzbjt(i4, i3, i2, i, z2, z, f);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public zzbjt[] zzoj(int i) {
        return new zzbjt[i];
    }
}
