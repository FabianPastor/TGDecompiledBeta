package com.google.android.gms.auth.api.signin.internal;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzf implements Creator<zzg> {
    static void zza(zzg com_google_android_gms_auth_api_signin_internal_zzg, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zzc(parcel, 1, com_google_android_gms_auth_api_signin_internal_zzg.versionCode);
        zzc.zzc(parcel, 2, com_google_android_gms_auth_api_signin_internal_zzg.getType());
        zzc.zza(parcel, 3, com_google_android_gms_auth_api_signin_internal_zzg.getBundle(), false);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzZ(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzbo(i);
    }

    public zzg zzZ(Parcel parcel) {
        int i = 0;
        int zzaY = zzb.zzaY(parcel);
        Bundle bundle = null;
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
                    bundle = zzb.zzs(parcel, zzaX);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new zzg(i2, i, bundle);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public zzg[] zzbo(int i) {
        return new zzg[i];
    }
}
