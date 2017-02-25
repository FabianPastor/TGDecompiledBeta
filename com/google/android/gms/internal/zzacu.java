package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzc;
import com.google.android.gms.internal.zzacs.zza;

public class zzacu implements Creator<zza> {
    static void zza(zza com_google_android_gms_internal_zzacs_zza, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zzc(parcel, 1, com_google_android_gms_internal_zzacs_zza.getVersionCode());
        zzc.zzc(parcel, 2, com_google_android_gms_internal_zzacs_zza.zzys());
        zzc.zza(parcel, 3, com_google_android_gms_internal_zzacs_zza.zzyt());
        zzc.zzc(parcel, 4, com_google_android_gms_internal_zzacs_zza.zzyu());
        zzc.zza(parcel, 5, com_google_android_gms_internal_zzacs_zza.zzyv());
        zzc.zza(parcel, 6, com_google_android_gms_internal_zzacs_zza.zzyw(), false);
        zzc.zzc(parcel, 7, com_google_android_gms_internal_zzacs_zza.zzyx());
        zzc.zza(parcel, 8, com_google_android_gms_internal_zzacs_zza.zzyz(), false);
        zzc.zza(parcel, 9, com_google_android_gms_internal_zzacs_zza.zzyB(), i, false);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzbe(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzdi(i);
    }

    public zza zzbe(Parcel parcel) {
        zzacn com_google_android_gms_internal_zzacn = null;
        int i = 0;
        int zzaY = zzb.zzaY(parcel);
        String str = null;
        String str2 = null;
        boolean z = false;
        int i2 = 0;
        boolean z2 = false;
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
                    z2 = zzb.zzc(parcel, zzaX);
                    break;
                case 4:
                    i2 = zzb.zzg(parcel, zzaX);
                    break;
                case 5:
                    z = zzb.zzc(parcel, zzaX);
                    break;
                case 6:
                    str2 = zzb.zzq(parcel, zzaX);
                    break;
                case 7:
                    i = zzb.zzg(parcel, zzaX);
                    break;
                case 8:
                    str = zzb.zzq(parcel, zzaX);
                    break;
                case 9:
                    com_google_android_gms_internal_zzacn = (zzacn) zzb.zza(parcel, zzaX, zzacn.CREATOR);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new zza(i4, i3, z2, i2, z, str2, i, str, com_google_android_gms_internal_zzacn);
        }
        throw new zzb.zza("Overread allowed size end=" + zzaY, parcel);
    }

    public zza[] zzdi(int i) {
        return new zza[i];
    }
}
