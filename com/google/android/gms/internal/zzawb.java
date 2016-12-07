package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzawb implements Creator<zzawa> {
    static void zza(zzawa com_google_android_gms_internal_zzawa, Parcel parcel, int i) {
        int zzaV = zzc.zzaV(parcel);
        zzc.zzc(parcel, 1, com_google_android_gms_internal_zzawa.mVersionCode);
        zzc.zzc(parcel, 2, com_google_android_gms_internal_zzawa.zzbzp);
        zzc.zza(parcel, 3, com_google_android_gms_internal_zzawa.zzbzq, i, false);
        zzc.zza(parcel, 4, com_google_android_gms_internal_zzawa.zzbzr, false);
        zzc.zzJ(parcel, zzaV);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzit(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzml(i);
    }

    public zzawa zzit(Parcel parcel) {
        String[] strArr = null;
        int i = 0;
        int zzaU = zzb.zzaU(parcel);
        zzawc[] com_google_android_gms_internal_zzawcArr = null;
        int i2 = 0;
        while (parcel.dataPosition() < zzaU) {
            zzawc[] com_google_android_gms_internal_zzawcArr2;
            int i3;
            String[] strArr2;
            int zzaT = zzb.zzaT(parcel);
            String[] strArr3;
            switch (zzb.zzcW(zzaT)) {
                case 1:
                    strArr3 = strArr;
                    com_google_android_gms_internal_zzawcArr2 = com_google_android_gms_internal_zzawcArr;
                    i3 = i;
                    i = zzb.zzg(parcel, zzaT);
                    strArr2 = strArr3;
                    break;
                case 2:
                    i = i2;
                    zzawc[] com_google_android_gms_internal_zzawcArr3 = com_google_android_gms_internal_zzawcArr;
                    i3 = zzb.zzg(parcel, zzaT);
                    strArr2 = strArr;
                    com_google_android_gms_internal_zzawcArr2 = com_google_android_gms_internal_zzawcArr3;
                    break;
                case 3:
                    i3 = i;
                    i = i2;
                    strArr3 = strArr;
                    com_google_android_gms_internal_zzawcArr2 = (zzawc[]) zzb.zzb(parcel, zzaT, zzawc.CREATOR);
                    strArr2 = strArr3;
                    break;
                case 4:
                    strArr2 = zzb.zzC(parcel, zzaT);
                    com_google_android_gms_internal_zzawcArr2 = com_google_android_gms_internal_zzawcArr;
                    i3 = i;
                    i = i2;
                    break;
                default:
                    zzb.zzb(parcel, zzaT);
                    strArr2 = strArr;
                    com_google_android_gms_internal_zzawcArr2 = com_google_android_gms_internal_zzawcArr;
                    i3 = i;
                    i = i2;
                    break;
            }
            i2 = i;
            i = i3;
            com_google_android_gms_internal_zzawcArr = com_google_android_gms_internal_zzawcArr2;
            strArr = strArr2;
        }
        if (parcel.dataPosition() == zzaU) {
            return new zzawa(i2, i, com_google_android_gms_internal_zzawcArr, strArr);
        }
        throw new zza("Overread allowed size end=" + zzaU, parcel);
    }

    public zzawa[] zzml(int i) {
        return new zzawa[i];
    }
}
