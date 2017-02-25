package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzaur implements Creator<zzauq> {
    static void zza(zzauq com_google_android_gms_internal_zzauq, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zzc(parcel, 1, com_google_android_gms_internal_zzauq.versionCode);
        zzc.zza(parcel, 2, com_google_android_gms_internal_zzauq.name, false);
        zzc.zza(parcel, 3, com_google_android_gms_internal_zzauq.zzbwg);
        zzc.zza(parcel, 4, com_google_android_gms_internal_zzauq.zzbwh, false);
        zzc.zza(parcel, 5, com_google_android_gms_internal_zzauq.zzbwi, false);
        zzc.zza(parcel, 6, com_google_android_gms_internal_zzauq.zzaGV, false);
        zzc.zza(parcel, 7, com_google_android_gms_internal_zzauq.zzbqZ, false);
        zzc.zza(parcel, 8, com_google_android_gms_internal_zzauq.zzbwj, false);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzhU(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzlE(i);
    }

    public zzauq zzhU(Parcel parcel) {
        Double d = null;
        int zzaY = zzb.zzaY(parcel);
        int i = 0;
        long j = 0;
        String str = null;
        String str2 = null;
        Float f = null;
        Long l = null;
        String str3 = null;
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 1:
                    i = zzb.zzg(parcel, zzaX);
                    break;
                case 2:
                    str3 = zzb.zzq(parcel, zzaX);
                    break;
                case 3:
                    j = zzb.zzi(parcel, zzaX);
                    break;
                case 4:
                    l = zzb.zzj(parcel, zzaX);
                    break;
                case 5:
                    f = zzb.zzm(parcel, zzaX);
                    break;
                case 6:
                    str2 = zzb.zzq(parcel, zzaX);
                    break;
                case 7:
                    str = zzb.zzq(parcel, zzaX);
                    break;
                case 8:
                    d = zzb.zzo(parcel, zzaX);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new zzauq(i, str3, j, l, f, str2, str, d);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public zzauq[] zzlE(int i) {
        return new zzauq[i];
    }
}
