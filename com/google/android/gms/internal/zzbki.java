package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzbki implements Creator<zzbkh> {
    static void zza(zzbkh com_google_android_gms_internal_zzbkh, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zzc(parcel, 1, com_google_android_gms_internal_zzbkh.versionCode);
        zzc.zza(parcel, 2, com_google_android_gms_internal_zzbkh.zzbPA, i, false);
        zzc.zza(parcel, 3, com_google_android_gms_internal_zzbkh.zzbPB, i, false);
        zzc.zza(parcel, 4, com_google_android_gms_internal_zzbkh.zzbPC, i, false);
        zzc.zza(parcel, 5, com_google_android_gms_internal_zzbkh.zzbPD, i, false);
        zzc.zza(parcel, 6, com_google_android_gms_internal_zzbkh.zzbPE, false);
        zzc.zza(parcel, 7, com_google_android_gms_internal_zzbkh.zzbPF);
        zzc.zza(parcel, 8, com_google_android_gms_internal_zzbkh.zzbPv, false);
        zzc.zzc(parcel, 9, com_google_android_gms_internal_zzbkh.zzbPG);
        zzc.zza(parcel, 10, com_google_android_gms_internal_zzbkh.zzbPH);
        zzc.zzc(parcel, 11, com_google_android_gms_internal_zzbkh.zzbPI);
        zzc.zzc(parcel, 12, com_google_android_gms_internal_zzbkh.zzbPJ);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzjS(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzoo(i);
    }

    public zzbkh zzjS(Parcel parcel) {
        int zzaY = zzb.zzaY(parcel);
        int i = 0;
        zzbkq[] com_google_android_gms_internal_zzbkqArr = null;
        zzbkd com_google_android_gms_internal_zzbkd = null;
        zzbkd com_google_android_gms_internal_zzbkd2 = null;
        zzbkd com_google_android_gms_internal_zzbkd3 = null;
        String str = null;
        float f = 0.0f;
        String str2 = null;
        int i2 = 0;
        boolean z = false;
        int i3 = 0;
        int i4 = 0;
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 1:
                    i = zzb.zzg(parcel, zzaX);
                    break;
                case 2:
                    com_google_android_gms_internal_zzbkqArr = (zzbkq[]) zzb.zzb(parcel, zzaX, zzbkq.CREATOR);
                    break;
                case 3:
                    com_google_android_gms_internal_zzbkd = (zzbkd) zzb.zza(parcel, zzaX, zzbkd.CREATOR);
                    break;
                case 4:
                    com_google_android_gms_internal_zzbkd2 = (zzbkd) zzb.zza(parcel, zzaX, zzbkd.CREATOR);
                    break;
                case 5:
                    com_google_android_gms_internal_zzbkd3 = (zzbkd) zzb.zza(parcel, zzaX, zzbkd.CREATOR);
                    break;
                case 6:
                    str = zzb.zzq(parcel, zzaX);
                    break;
                case 7:
                    f = zzb.zzl(parcel, zzaX);
                    break;
                case 8:
                    str2 = zzb.zzq(parcel, zzaX);
                    break;
                case 9:
                    i2 = zzb.zzg(parcel, zzaX);
                    break;
                case 10:
                    z = zzb.zzc(parcel, zzaX);
                    break;
                case 11:
                    i3 = zzb.zzg(parcel, zzaX);
                    break;
                case 12:
                    i4 = zzb.zzg(parcel, zzaX);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new zzbkh(i, com_google_android_gms_internal_zzbkqArr, com_google_android_gms_internal_zzbkd, com_google_android_gms_internal_zzbkd2, com_google_android_gms_internal_zzbkd3, str, f, str2, i2, z, i3, i4);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public zzbkh[] zzoo(int i) {
        return new zzbkh[i];
    }
}
