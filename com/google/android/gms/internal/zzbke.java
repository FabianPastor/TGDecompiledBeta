package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzbke implements Creator<zzbkd> {
    static void zza(zzbkd com_google_android_gms_internal_zzbkd, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zzc(parcel, 1, com_google_android_gms_internal_zzbkd.versionCode);
        zzc.zzc(parcel, 2, com_google_android_gms_internal_zzbkd.left);
        zzc.zzc(parcel, 3, com_google_android_gms_internal_zzbkd.top);
        zzc.zzc(parcel, 4, com_google_android_gms_internal_zzbkd.width);
        zzc.zzc(parcel, 5, com_google_android_gms_internal_zzbkd.height);
        zzc.zza(parcel, 6, com_google_android_gms_internal_zzbkd.zzbPz);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzjR(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzon(i);
    }

    public zzbkd zzjR(Parcel parcel) {
        int i = 0;
        int zzaY = zzb.zzaY(parcel);
        float f = 0.0f;
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
                    i = zzb.zzg(parcel, zzaX);
                    break;
                case 6:
                    f = zzb.zzl(parcel, zzaX);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new zzbkd(i5, i4, i3, i2, i, f);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public zzbkd[] zzon(int i) {
        return new zzbkd[i];
    }
}
