package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzbhu implements Creator<zzbht> {
    static void zza(zzbht com_google_android_gms_internal_zzbht, Parcel parcel, int i) {
        int zzaV = zzc.zzaV(parcel);
        zzc.zzc(parcel, 1, com_google_android_gms_internal_zzbht.versionCode);
        zzc.zza(parcel, 2, com_google_android_gms_internal_zzbht.zzbNM, i, false);
        zzc.zza(parcel, 3, com_google_android_gms_internal_zzbht.zzbNC, i, false);
        zzc.zza(parcel, 4, com_google_android_gms_internal_zzbht.zzbND, i, false);
        zzc.zza(parcel, 5, com_google_android_gms_internal_zzbht.zzbNF, false);
        zzc.zza(parcel, 6, com_google_android_gms_internal_zzbht.zzbNG);
        zzc.zza(parcel, 7, com_google_android_gms_internal_zzbht.zzbNw, false);
        zzc.zza(parcel, 8, com_google_android_gms_internal_zzbht.zzbNN);
        zzc.zzJ(parcel, zzaV);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzjq(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zznH(i);
    }

    public zzbht zzjq(Parcel parcel) {
        boolean z = false;
        String str = null;
        int zzaU = zzb.zzaU(parcel);
        float f = 0.0f;
        String str2 = null;
        zzbhg com_google_android_gms_internal_zzbhg = null;
        zzbhg com_google_android_gms_internal_zzbhg2 = null;
        zzbho[] com_google_android_gms_internal_zzbhoArr = null;
        int i = 0;
        while (parcel.dataPosition() < zzaU) {
            int zzaT = zzb.zzaT(parcel);
            switch (zzb.zzcW(zzaT)) {
                case 1:
                    i = zzb.zzg(parcel, zzaT);
                    break;
                case 2:
                    com_google_android_gms_internal_zzbhoArr = (zzbho[]) zzb.zzb(parcel, zzaT, zzbho.CREATOR);
                    break;
                case 3:
                    com_google_android_gms_internal_zzbhg2 = (zzbhg) zzb.zza(parcel, zzaT, zzbhg.CREATOR);
                    break;
                case 4:
                    com_google_android_gms_internal_zzbhg = (zzbhg) zzb.zza(parcel, zzaT, zzbhg.CREATOR);
                    break;
                case 5:
                    str2 = zzb.zzq(parcel, zzaT);
                    break;
                case 6:
                    f = zzb.zzl(parcel, zzaT);
                    break;
                case 7:
                    str = zzb.zzq(parcel, zzaT);
                    break;
                case 8:
                    z = zzb.zzc(parcel, zzaT);
                    break;
                default:
                    zzb.zzb(parcel, zzaT);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaU) {
            return new zzbht(i, com_google_android_gms_internal_zzbhoArr, com_google_android_gms_internal_zzbhg2, com_google_android_gms_internal_zzbhg, str2, f, str, z);
        }
        throw new zza("Overread allowed size end=" + zzaU, parcel);
    }

    public zzbht[] zznH(int i) {
        return new zzbht[i];
    }
}
