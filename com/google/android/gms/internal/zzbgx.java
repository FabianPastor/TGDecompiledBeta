package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzbgx implements Creator<zzbgw> {
    static void zza(zzbgw com_google_android_gms_internal_zzbgw, Parcel parcel, int i) {
        int zzaV = zzc.zzaV(parcel);
        zzc.zzc(parcel, 1, com_google_android_gms_internal_zzbgw.versionCode);
        zzc.zzc(parcel, 2, com_google_android_gms_internal_zzbgw.mode);
        zzc.zzc(parcel, 3, com_google_android_gms_internal_zzbgw.zzbNj);
        zzc.zzc(parcel, 4, com_google_android_gms_internal_zzbgw.zzbNk);
        zzc.zza(parcel, 5, com_google_android_gms_internal_zzbgw.zzbNl);
        zzc.zza(parcel, 6, com_google_android_gms_internal_zzbgw.zzbNm);
        zzc.zza(parcel, 7, com_google_android_gms_internal_zzbgw.zzbNn);
        zzc.zzJ(parcel, zzaV);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzji(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzny(i);
    }

    public zzbgw zzji(Parcel parcel) {
        boolean z = false;
        int zzaU = zzb.zzaU(parcel);
        float f = -1.0f;
        boolean z2 = false;
        int i = 0;
        int i2 = 0;
        int i3 = 0;
        int i4 = 0;
        while (parcel.dataPosition() < zzaU) {
            int zzaT = zzb.zzaT(parcel);
            switch (zzb.zzcW(zzaT)) {
                case 1:
                    i4 = zzb.zzg(parcel, zzaT);
                    break;
                case 2:
                    i3 = zzb.zzg(parcel, zzaT);
                    break;
                case 3:
                    i2 = zzb.zzg(parcel, zzaT);
                    break;
                case 4:
                    i = zzb.zzg(parcel, zzaT);
                    break;
                case 5:
                    z2 = zzb.zzc(parcel, zzaT);
                    break;
                case 6:
                    z = zzb.zzc(parcel, zzaT);
                    break;
                case 7:
                    f = zzb.zzl(parcel, zzaT);
                    break;
                default:
                    zzb.zzb(parcel, zzaT);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaU) {
            return new zzbgw(i4, i3, i2, i, z2, z, f);
        }
        throw new zza("Overread allowed size end=" + zzaU, parcel);
    }

    public zzbgw[] zzny(int i) {
        return new zzbgw[i];
    }
}
