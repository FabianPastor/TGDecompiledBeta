package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzbkb implements Creator<zzbka> {
    static void zza(zzbka com_google_android_gms_internal_zzbka, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zzc(parcel, 1, com_google_android_gms_internal_zzbka.versionCode);
        zzc.zzc(parcel, 2, com_google_android_gms_internal_zzbka.width);
        zzc.zzc(parcel, 3, com_google_android_gms_internal_zzbka.height);
        zzc.zzc(parcel, 4, com_google_android_gms_internal_zzbka.id);
        zzc.zza(parcel, 5, com_google_android_gms_internal_zzbka.zzbPo);
        zzc.zzc(parcel, 6, com_google_android_gms_internal_zzbka.rotation);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzjQ(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzol(i);
    }

    public zzbka zzjQ(Parcel parcel) {
        int i = 0;
        int zzaY = zzb.zzaY(parcel);
        long j = 0;
        int i2 = 0;
        int i3 = 0;
        int i4 = 0;
        int i5 = 0;
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 1:
                    i5 = zzb.zzg(parcel, zzaX);
                    break;
                case 2:
                    i4 = zzb.zzg(parcel, zzaX);
                    break;
                case 3:
                    i3 = zzb.zzg(parcel, zzaX);
                    break;
                case 4:
                    i2 = zzb.zzg(parcel, zzaX);
                    break;
                case 5:
                    j = zzb.zzi(parcel, zzaX);
                    break;
                case 6:
                    i = zzb.zzg(parcel, zzaX);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new zzbka(i5, i4, i3, i2, j, i);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public zzbka[] zzol(int i) {
        return new zzbka[i];
    }
}
