package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzc;
import com.google.android.gms.internal.zzacw.zza;
import java.util.ArrayList;

public class zzacy implements Creator<zza> {
    static void zza(zza com_google_android_gms_internal_zzacw_zza, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zzc(parcel, 1, com_google_android_gms_internal_zzacw_zza.versionCode);
        zzc.zza(parcel, 2, com_google_android_gms_internal_zzacw_zza.className, false);
        zzc.zzc(parcel, 3, com_google_android_gms_internal_zzacw_zza.zzaHk, false);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzbh(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzdl(i);
    }

    public zza zzbh(Parcel parcel) {
        ArrayList arrayList = null;
        int zzaY = zzb.zzaY(parcel);
        int i = 0;
        String str = null;
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 1:
                    i = zzb.zzg(parcel, zzaX);
                    break;
                case 2:
                    str = zzb.zzq(parcel, zzaX);
                    break;
                case 3:
                    arrayList = zzb.zzc(parcel, zzaX, zzacw.zzb.CREATOR);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new zza(i, str, arrayList);
        }
        throw new zzb.zza("Overread allowed size end=" + zzaY, parcel);
    }

    public zza[] zzdl(int i) {
        return new zza[i];
    }
}
