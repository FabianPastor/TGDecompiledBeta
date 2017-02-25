package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzath implements Creator<zzatg> {
    static void zza(zzatg com_google_android_gms_internal_zzatg, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zzc(parcel, 1, com_google_android_gms_internal_zzatg.versionCode);
        zzc.zza(parcel, 2, com_google_android_gms_internal_zzatg.packageName, false);
        zzc.zza(parcel, 3, com_google_android_gms_internal_zzatg.zzbqZ, false);
        zzc.zza(parcel, 4, com_google_android_gms_internal_zzatg.zzbra, i, false);
        zzc.zza(parcel, 5, com_google_android_gms_internal_zzatg.zzbrb);
        zzc.zza(parcel, 6, com_google_android_gms_internal_zzatg.zzbrc);
        zzc.zza(parcel, 7, com_google_android_gms_internal_zzatg.zzbrd, false);
        zzc.zza(parcel, 8, com_google_android_gms_internal_zzatg.zzbre, i, false);
        zzc.zza(parcel, 9, com_google_android_gms_internal_zzatg.zzbrf);
        zzc.zza(parcel, 10, com_google_android_gms_internal_zzatg.zzbrg, i, false);
        zzc.zza(parcel, 11, com_google_android_gms_internal_zzatg.zzbrh);
        zzc.zza(parcel, 12, com_google_android_gms_internal_zzatg.zzbri, i, false);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzhR(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzlA(i);
    }

    public zzatg zzhR(Parcel parcel) {
        int zzaY = zzb.zzaY(parcel);
        int i = 0;
        String str = null;
        String str2 = null;
        zzauq com_google_android_gms_internal_zzauq = null;
        long j = 0;
        boolean z = false;
        String str3 = null;
        zzatq com_google_android_gms_internal_zzatq = null;
        long j2 = 0;
        zzatq com_google_android_gms_internal_zzatq2 = null;
        long j3 = 0;
        zzatq com_google_android_gms_internal_zzatq3 = null;
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 1:
                    i = zzb.zzg(parcel, zzaX);
                    break;
                case 2:
                    str = zzb.zzq(parcel, zzaX);
                    break;
                case 3:
                    str2 = zzb.zzq(parcel, zzaX);
                    break;
                case 4:
                    com_google_android_gms_internal_zzauq = (zzauq) zzb.zza(parcel, zzaX, zzauq.CREATOR);
                    break;
                case 5:
                    j = zzb.zzi(parcel, zzaX);
                    break;
                case 6:
                    z = zzb.zzc(parcel, zzaX);
                    break;
                case 7:
                    str3 = zzb.zzq(parcel, zzaX);
                    break;
                case 8:
                    com_google_android_gms_internal_zzatq = (zzatq) zzb.zza(parcel, zzaX, zzatq.CREATOR);
                    break;
                case 9:
                    j2 = zzb.zzi(parcel, zzaX);
                    break;
                case 10:
                    com_google_android_gms_internal_zzatq2 = (zzatq) zzb.zza(parcel, zzaX, zzatq.CREATOR);
                    break;
                case 11:
                    j3 = zzb.zzi(parcel, zzaX);
                    break;
                case 12:
                    com_google_android_gms_internal_zzatq3 = (zzatq) zzb.zza(parcel, zzaX, zzatq.CREATOR);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new zzatg(i, str, str2, com_google_android_gms_internal_zzauq, j, z, str3, com_google_android_gms_internal_zzatq, j2, com_google_android_gms_internal_zzatq2, j3, com_google_android_gms_internal_zzatq3);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public zzatg[] zzlA(int i) {
        return new zzatg[i];
    }
}
