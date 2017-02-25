package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzada implements Creator<zzacz> {
    static void zza(zzacz com_google_android_gms_internal_zzacz, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zzc(parcel, 1, com_google_android_gms_internal_zzacz.getVersionCode());
        zzc.zza(parcel, 2, com_google_android_gms_internal_zzacz.zzyH(), false);
        zzc.zza(parcel, 3, com_google_android_gms_internal_zzacz.zzyI(), i, false);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzbi(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzdm(i);
    }

    public zzacz zzbi(Parcel parcel) {
        zzacw com_google_android_gms_internal_zzacw = null;
        int zzaY = zzb.zzaY(parcel);
        int i = 0;
        Parcel parcel2 = null;
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 1:
                    i = zzb.zzg(parcel, zzaX);
                    break;
                case 2:
                    parcel2 = zzb.zzF(parcel, zzaX);
                    break;
                case 3:
                    com_google_android_gms_internal_zzacw = (zzacw) zzb.zza(parcel, zzaX, zzacw.CREATOR);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new zzacz(i, parcel2, com_google_android_gms_internal_zzacw);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public zzacz[] zzdm(int i) {
        return new zzacz[i];
    }
}
