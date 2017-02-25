package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzayy implements Creator<zzayx> {
    static void zza(zzayx com_google_android_gms_internal_zzayx, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zzc(parcel, 2, com_google_android_gms_internal_zzayx.zzbBC);
        zzc.zza(parcel, 3, com_google_android_gms_internal_zzayx.zzbBD, i, false);
        zzc.zza(parcel, 4, com_google_android_gms_internal_zzayx.zzbBE, false);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzja(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzmX(i);
    }

    public zzayx zzja(Parcel parcel) {
        String[] strArr = null;
        int zzaY = zzb.zzaY(parcel);
        int i = 0;
        zzayz[] com_google_android_gms_internal_zzayzArr = null;
        while (parcel.dataPosition() < zzaY) {
            zzayz[] com_google_android_gms_internal_zzayzArr2;
            int zzg;
            String[] strArr2;
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 2:
                    String[] strArr3 = strArr;
                    com_google_android_gms_internal_zzayzArr2 = com_google_android_gms_internal_zzayzArr;
                    zzg = zzb.zzg(parcel, zzaX);
                    strArr2 = strArr3;
                    break;
                case 3:
                    zzg = i;
                    zzayz[] com_google_android_gms_internal_zzayzArr3 = (zzayz[]) zzb.zzb(parcel, zzaX, zzayz.CREATOR);
                    strArr2 = strArr;
                    com_google_android_gms_internal_zzayzArr2 = com_google_android_gms_internal_zzayzArr3;
                    break;
                case 4:
                    strArr2 = zzb.zzC(parcel, zzaX);
                    com_google_android_gms_internal_zzayzArr2 = com_google_android_gms_internal_zzayzArr;
                    zzg = i;
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    strArr2 = strArr;
                    com_google_android_gms_internal_zzayzArr2 = com_google_android_gms_internal_zzayzArr;
                    zzg = i;
                    break;
            }
            i = zzg;
            com_google_android_gms_internal_zzayzArr = com_google_android_gms_internal_zzayzArr2;
            strArr = strArr2;
        }
        if (parcel.dataPosition() == zzaY) {
            return new zzayx(i, com_google_android_gms_internal_zzayzArr, strArr);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public zzayx[] zzmX(int i) {
        return new zzayx[i];
    }
}
