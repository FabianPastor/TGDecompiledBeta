package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzasr implements Creator<zzasq> {
    static void zza(zzasq com_google_android_gms_internal_zzasq, Parcel parcel, int i) {
        int zzaV = zzc.zzaV(parcel);
        zzc.zzc(parcel, 1, com_google_android_gms_internal_zzasq.versionCode);
        zzc.zza(parcel, 2, com_google_android_gms_internal_zzasq.packageName, false);
        zzc.zza(parcel, 3, com_google_android_gms_internal_zzasq.zzbqf, false);
        zzc.zza(parcel, 4, com_google_android_gms_internal_zzasq.zzbhg, false);
        zzc.zza(parcel, 5, com_google_android_gms_internal_zzasq.zzbqg, false);
        zzc.zza(parcel, 6, com_google_android_gms_internal_zzasq.zzbqh);
        zzc.zza(parcel, 7, com_google_android_gms_internal_zzasq.zzbqi);
        zzc.zza(parcel, 8, com_google_android_gms_internal_zzasq.zzbqj, false);
        zzc.zza(parcel, 9, com_google_android_gms_internal_zzasq.zzbqk);
        zzc.zza(parcel, 10, com_google_android_gms_internal_zzasq.zzbql);
        zzc.zza(parcel, 11, com_google_android_gms_internal_zzasq.zzbqm);
        zzc.zza(parcel, 12, com_google_android_gms_internal_zzasq.zzbqn, false);
        zzc.zzJ(parcel, zzaV);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzhK(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzlp(i);
    }

    public zzasq zzhK(Parcel parcel) {
        int zzaU = zzb.zzaU(parcel);
        int i = 0;
        String str = null;
        String str2 = null;
        String str3 = null;
        String str4 = null;
        long j = 0;
        long j2 = 0;
        String str5 = null;
        boolean z = false;
        boolean z2 = false;
        long j3 = 0;
        String str6 = null;
        while (parcel.dataPosition() < zzaU) {
            int zzaT = zzb.zzaT(parcel);
            switch (zzb.zzcW(zzaT)) {
                case 1:
                    i = zzb.zzg(parcel, zzaT);
                    break;
                case 2:
                    str = zzb.zzq(parcel, zzaT);
                    break;
                case 3:
                    str2 = zzb.zzq(parcel, zzaT);
                    break;
                case 4:
                    str3 = zzb.zzq(parcel, zzaT);
                    break;
                case 5:
                    str4 = zzb.zzq(parcel, zzaT);
                    break;
                case 6:
                    j = zzb.zzi(parcel, zzaT);
                    break;
                case 7:
                    j2 = zzb.zzi(parcel, zzaT);
                    break;
                case 8:
                    str5 = zzb.zzq(parcel, zzaT);
                    break;
                case 9:
                    z = zzb.zzc(parcel, zzaT);
                    break;
                case 10:
                    z2 = zzb.zzc(parcel, zzaT);
                    break;
                case 11:
                    j3 = zzb.zzi(parcel, zzaT);
                    break;
                case 12:
                    str6 = zzb.zzq(parcel, zzaT);
                    break;
                default:
                    zzb.zzb(parcel, zzaT);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaU) {
            return new zzasq(i, str, str2, str3, str4, j, j2, str5, z, z2, j3, str6);
        }
        throw new zza("Overread allowed size end=" + zzaU, parcel);
    }

    public zzasq[] zzlp(int i) {
        return new zzasq[i];
    }
}
