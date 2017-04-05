package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzbkr implements Creator<zzbkq> {
    static void zza(zzbkq com_google_android_gms_internal_zzbkq, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zzc(parcel, 1, com_google_android_gms_internal_zzbkq.versionCode);
        zzc.zza(parcel, 2, com_google_android_gms_internal_zzbkq.zzbPH, i, false);
        zzc.zza(parcel, 3, com_google_android_gms_internal_zzbkq.zzbPx, i, false);
        zzc.zza(parcel, 4, com_google_android_gms_internal_zzbkq.zzbPy, i, false);
        zzc.zza(parcel, 5, com_google_android_gms_internal_zzbkq.zzbPA, false);
        zzc.zza(parcel, 6, com_google_android_gms_internal_zzbkq.zzbPB);
        zzc.zza(parcel, 7, com_google_android_gms_internal_zzbkq.zzbPr, false);
        zzc.zza(parcel, 8, com_google_android_gms_internal_zzbkq.zzbPI);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzjW(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzos(i);
    }

    public zzbkq zzjW(Parcel parcel) {
        boolean z = false;
        String str = null;
        int zzaY = zzb.zzaY(parcel);
        float f = 0.0f;
        String str2 = null;
        zzbkd com_google_android_gms_internal_zzbkd = null;
        zzbkd com_google_android_gms_internal_zzbkd2 = null;
        zzbkl[] com_google_android_gms_internal_zzbklArr = null;
        int i = 0;
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 1:
                    i = zzb.zzg(parcel, zzaX);
                    break;
                case 2:
                    com_google_android_gms_internal_zzbklArr = (zzbkl[]) zzb.zzb(parcel, zzaX, zzbkl.CREATOR);
                    break;
                case 3:
                    com_google_android_gms_internal_zzbkd2 = (zzbkd) zzb.zza(parcel, zzaX, zzbkd.CREATOR);
                    break;
                case 4:
                    com_google_android_gms_internal_zzbkd = (zzbkd) zzb.zza(parcel, zzaX, zzbkd.CREATOR);
                    break;
                case 5:
                    str2 = zzb.zzq(parcel, zzaX);
                    break;
                case 6:
                    f = zzb.zzl(parcel, zzaX);
                    break;
                case 7:
                    str = zzb.zzq(parcel, zzaX);
                    break;
                case 8:
                    z = zzb.zzc(parcel, zzaX);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new zzbkq(i, com_google_android_gms_internal_zzbklArr, com_google_android_gms_internal_zzbkd2, com_google_android_gms_internal_zzbkd, str2, f, str, z);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public zzbkq[] zzos(int i) {
        return new zzbkq[i];
    }
}
