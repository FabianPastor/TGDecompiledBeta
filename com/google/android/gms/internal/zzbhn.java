package com.google.android.gms.internal;

import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzbhn implements Creator<zzbhm> {
    static void zza(zzbhm com_google_android_gms_internal_zzbhm, Parcel parcel, int i) {
        int zzaV = zzc.zzaV(parcel);
        zzc.zzc(parcel, 1, com_google_android_gms_internal_zzbhm.versionCode);
        zzc.zza(parcel, 2, com_google_android_gms_internal_zzbhm.zzbNL, i, false);
        zzc.zzJ(parcel, zzaV);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzjn(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zznE(i);
    }

    public zzbhm zzjn(Parcel parcel) {
        int zzaU = zzb.zzaU(parcel);
        int i = 0;
        Rect rect = null;
        while (parcel.dataPosition() < zzaU) {
            int zzaT = zzb.zzaT(parcel);
            switch (zzb.zzcW(zzaT)) {
                case 1:
                    i = zzb.zzg(parcel, zzaT);
                    break;
                case 2:
                    rect = (Rect) zzb.zza(parcel, zzaT, Rect.CREATOR);
                    break;
                default:
                    zzb.zzb(parcel, zzaT);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaU) {
            return new zzbhm(i, rect);
        }
        throw new zza("Overread allowed size end=" + zzaU, parcel);
    }

    public zzbhm[] zznE(int i) {
        return new zzbhm[i];
    }
}
