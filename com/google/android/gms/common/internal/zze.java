package com.google.android.gms.common.internal;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zze implements Creator<zzd> {
    static void zza(zzd com_google_android_gms_common_internal_zzd, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zzc(parcel, 1, com_google_android_gms_common_internal_zzd.zzaiI);
        zzc.zza(parcel, 2, com_google_android_gms_common_internal_zzd.zzaEW, false);
        zzc.zza(parcel, 3, com_google_android_gms_common_internal_zzd.zzaEX, i, false);
        zzc.zza(parcel, 4, com_google_android_gms_common_internal_zzd.zzaEY, false);
        zzc.zza(parcel, 5, com_google_android_gms_common_internal_zzd.zzaEZ, false);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzaQ(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzcR(i);
    }

    public zzd zzaQ(Parcel parcel) {
        Integer num = null;
        int zzaY = zzb.zzaY(parcel);
        int i = 0;
        Integer num2 = null;
        Scope[] scopeArr = null;
        IBinder iBinder = null;
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 1:
                    i = zzb.zzg(parcel, zzaX);
                    break;
                case 2:
                    iBinder = zzb.zzr(parcel, zzaX);
                    break;
                case 3:
                    scopeArr = (Scope[]) zzb.zzb(parcel, zzaX, Scope.CREATOR);
                    break;
                case 4:
                    num2 = zzb.zzh(parcel, zzaX);
                    break;
                case 5:
                    num = zzb.zzh(parcel, zzaX);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new zzd(i, iBinder, scopeArr, num2, num);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public zzd[] zzcR(int i) {
        return new zzd[i];
    }
}
