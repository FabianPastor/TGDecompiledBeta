package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzaza implements Creator<zzayz> {
    static void zza(zzayz com_google_android_gms_internal_zzayz, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zza(parcel, 2, com_google_android_gms_internal_zzayz.name, false);
        zzc.zza(parcel, 3, com_google_android_gms_internal_zzayz.zzbBG);
        zzc.zza(parcel, 4, com_google_android_gms_internal_zzayz.zzbhm);
        zzc.zza(parcel, 5, com_google_android_gms_internal_zzayz.zzbho);
        zzc.zza(parcel, 6, com_google_android_gms_internal_zzayz.zzaGV, false);
        zzc.zza(parcel, 7, com_google_android_gms_internal_zzayz.zzbBH, false);
        zzc.zzc(parcel, 8, com_google_android_gms_internal_zzayz.zzbBI);
        zzc.zzc(parcel, 9, com_google_android_gms_internal_zzayz.zzbBJ);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzjb(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzmY(i);
    }

    public zzayz zzjb(Parcel parcel) {
        byte[] bArr = null;
        int i = 0;
        int zzaY = zzb.zzaY(parcel);
        long j = 0;
        double d = 0.0d;
        int i2 = 0;
        String str = null;
        boolean z = false;
        String str2 = null;
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 2:
                    str2 = zzb.zzq(parcel, zzaX);
                    break;
                case 3:
                    j = zzb.zzi(parcel, zzaX);
                    break;
                case 4:
                    z = zzb.zzc(parcel, zzaX);
                    break;
                case 5:
                    d = zzb.zzn(parcel, zzaX);
                    break;
                case 6:
                    str = zzb.zzq(parcel, zzaX);
                    break;
                case 7:
                    bArr = zzb.zzt(parcel, zzaX);
                    break;
                case 8:
                    i2 = zzb.zzg(parcel, zzaX);
                    break;
                case 9:
                    i = zzb.zzg(parcel, zzaX);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new zzayz(str2, j, z, d, str, bArr, i2, i);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public zzayz[] zzmY(int i) {
        return new zzayz[i];
    }
}
