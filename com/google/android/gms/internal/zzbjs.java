package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzbjs implements Creator<zzbjr> {
    static void zza(zzbjr com_google_android_gms_internal_zzbjr, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zzc(parcel, 1, com_google_android_gms_internal_zzbjr.versionCode);
        zzc.zzc(parcel, 2, com_google_android_gms_internal_zzbjr.id);
        zzc.zza(parcel, 3, com_google_android_gms_internal_zzbjr.centerX);
        zzc.zza(parcel, 4, com_google_android_gms_internal_zzbjr.centerY);
        zzc.zza(parcel, 5, com_google_android_gms_internal_zzbjr.width);
        zzc.zza(parcel, 6, com_google_android_gms_internal_zzbjr.height);
        zzc.zza(parcel, 7, com_google_android_gms_internal_zzbjr.zzbPc);
        zzc.zza(parcel, 8, com_google_android_gms_internal_zzbjr.zzbPd);
        zzc.zza(parcel, 9, com_google_android_gms_internal_zzbjr.zzbPe, i, false);
        zzc.zza(parcel, 10, com_google_android_gms_internal_zzbjr.zzbPf);
        zzc.zza(parcel, 11, com_google_android_gms_internal_zzbjr.zzbPg);
        zzc.zza(parcel, 12, com_google_android_gms_internal_zzbjr.zzbPh);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzjN(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzoi(i);
    }

    public zzbjr zzjN(Parcel parcel) {
        int zzaY = zzb.zzaY(parcel);
        int i = 0;
        int i2 = 0;
        float f = 0.0f;
        float f2 = 0.0f;
        float f3 = 0.0f;
        float f4 = 0.0f;
        float f5 = 0.0f;
        float f6 = 0.0f;
        zzbjx[] com_google_android_gms_internal_zzbjxArr = null;
        float f7 = 0.0f;
        float f8 = 0.0f;
        float f9 = 0.0f;
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 1:
                    i = zzb.zzg(parcel, zzaX);
                    break;
                case 2:
                    i2 = zzb.zzg(parcel, zzaX);
                    break;
                case 3:
                    f = zzb.zzl(parcel, zzaX);
                    break;
                case 4:
                    f2 = zzb.zzl(parcel, zzaX);
                    break;
                case 5:
                    f3 = zzb.zzl(parcel, zzaX);
                    break;
                case 6:
                    f4 = zzb.zzl(parcel, zzaX);
                    break;
                case 7:
                    f5 = zzb.zzl(parcel, zzaX);
                    break;
                case 8:
                    f6 = zzb.zzl(parcel, zzaX);
                    break;
                case 9:
                    com_google_android_gms_internal_zzbjxArr = (zzbjx[]) zzb.zzb(parcel, zzaX, zzbjx.CREATOR);
                    break;
                case 10:
                    f7 = zzb.zzl(parcel, zzaX);
                    break;
                case 11:
                    f8 = zzb.zzl(parcel, zzaX);
                    break;
                case 12:
                    f9 = zzb.zzl(parcel, zzaX);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new zzbjr(i, i2, f, f2, f3, f4, f5, f6, com_google_android_gms_internal_zzbjxArr, f7, f8, f9);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public zzbjr[] zzoi(int i) {
        return new zzbjr[i];
    }
}
