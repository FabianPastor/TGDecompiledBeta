package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzbhe implements Creator<zzbhd> {
    static void zza(zzbhd com_google_android_gms_internal_zzbhd, Parcel parcel, int i) {
        int zzaV = zzc.zzaV(parcel);
        zzc.zzc(parcel, 1, com_google_android_gms_internal_zzbhd.versionCode);
        zzc.zzc(parcel, 2, com_google_android_gms_internal_zzbhd.width);
        zzc.zzc(parcel, 3, com_google_android_gms_internal_zzbhd.height);
        zzc.zzc(parcel, 4, com_google_android_gms_internal_zzbhd.id);
        zzc.zza(parcel, 5, com_google_android_gms_internal_zzbhd.zzbNq);
        zzc.zzc(parcel, 6, com_google_android_gms_internal_zzbhd.rotation);
        zzc.zzJ(parcel, zzaV);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzjk(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zznA(i);
    }

    public zzbhd zzjk(Parcel parcel) {
        int i = 0;
        int zzaU = zzb.zzaU(parcel);
        long j = 0;
        int i2 = 0;
        int i3 = 0;
        int i4 = 0;
        int i5 = 0;
        while (parcel.dataPosition() < zzaU) {
            int zzaT = zzb.zzaT(parcel);
            switch (zzb.zzcW(zzaT)) {
                case 1:
                    i5 = zzb.zzg(parcel, zzaT);
                    break;
                case 2:
                    i4 = zzb.zzg(parcel, zzaT);
                    break;
                case 3:
                    i3 = zzb.zzg(parcel, zzaT);
                    break;
                case 4:
                    i2 = zzb.zzg(parcel, zzaT);
                    break;
                case 5:
                    j = zzb.zzi(parcel, zzaT);
                    break;
                case 6:
                    i = zzb.zzg(parcel, zzaT);
                    break;
                default:
                    zzb.zzb(parcel, zzaT);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaU) {
            return new zzbhd(i5, i4, i3, i2, j, i);
        }
        throw new zza("Overread allowed size end=" + zzaU, parcel);
    }

    public zzbhd[] zznA(int i) {
        return new zzbhd[i];
    }
}
