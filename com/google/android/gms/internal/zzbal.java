package com.google.android.gms.internal;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzbal implements Creator<zzbak> {
    static void zza(zzbak com_google_android_gms_internal_zzbak, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zzc(parcel, 1, com_google_android_gms_internal_zzbak.zzaiI);
        zzc.zzc(parcel, 2, com_google_android_gms_internal_zzbak.zzPO());
        zzc.zza(parcel, 3, com_google_android_gms_internal_zzbak.zzPP(), i, false);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzju(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zznt(i);
    }

    public zzbak zzju(Parcel parcel) {
        int i = 0;
        int zzaY = zzb.zzaY(parcel);
        Intent intent = null;
        int i2 = 0;
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 1:
                    i2 = zzb.zzg(parcel, zzaX);
                    break;
                case 2:
                    i = zzb.zzg(parcel, zzaX);
                    break;
                case 3:
                    intent = (Intent) zzb.zza(parcel, zzaX, Intent.CREATOR);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new zzbak(i2, i, intent);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public zzbak[] zznt(int i) {
        return new zzbak[i];
    }
}
