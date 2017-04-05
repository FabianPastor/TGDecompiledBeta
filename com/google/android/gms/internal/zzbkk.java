package com.google.android.gms.internal;

import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzbkk implements Creator<zzbkj> {
    static void zza(zzbkj com_google_android_gms_internal_zzbkj, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zzc(parcel, 1, com_google_android_gms_internal_zzbkj.versionCode);
        zzc.zza(parcel, 2, com_google_android_gms_internal_zzbkj.zzbPG, i, false);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzjT(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzop(i);
    }

    public zzbkj zzjT(Parcel parcel) {
        int zzaY = zzb.zzaY(parcel);
        int i = 0;
        Rect rect = null;
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 1:
                    i = zzb.zzg(parcel, zzaX);
                    break;
                case 2:
                    rect = (Rect) zzb.zza(parcel, zzaX, Rect.CREATOR);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new zzbkj(i, rect);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public zzbkj[] zzop(int i) {
        return new zzbkj[i];
    }
}
