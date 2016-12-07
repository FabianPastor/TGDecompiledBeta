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
        int zzaV = zzc.zzaV(parcel);
        zzc.zzc(parcel, 1, com_google_android_gms_common_internal_zzd.mVersionCode);
        zzc.zza(parcel, 2, com_google_android_gms_common_internal_zzd.zzaDx, false);
        zzc.zza(parcel, 3, com_google_android_gms_common_internal_zzd.zzaDy, i, false);
        zzc.zza(parcel, 4, com_google_android_gms_common_internal_zzd.zzaDz, false);
        zzc.zza(parcel, 5, com_google_android_gms_common_internal_zzd.zzaDA, false);
        zzc.zzJ(parcel, zzaV);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzaM(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzcL(i);
    }

    public zzd zzaM(Parcel parcel) {
        Integer num = null;
        int zzaU = zzb.zzaU(parcel);
        int i = 0;
        Integer num2 = null;
        Scope[] scopeArr = null;
        IBinder iBinder = null;
        while (parcel.dataPosition() < zzaU) {
            int zzaT = zzb.zzaT(parcel);
            switch (zzb.zzcW(zzaT)) {
                case 1:
                    i = zzb.zzg(parcel, zzaT);
                    break;
                case 2:
                    iBinder = zzb.zzr(parcel, zzaT);
                    break;
                case 3:
                    scopeArr = (Scope[]) zzb.zzb(parcel, zzaT, Scope.CREATOR);
                    break;
                case 4:
                    num2 = zzb.zzh(parcel, zzaT);
                    break;
                case 5:
                    num = zzb.zzh(parcel, zzaT);
                    break;
                default:
                    zzb.zzb(parcel, zzaT);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaU) {
            return new zzd(i, iBinder, scopeArr, num2, num);
        }
        throw new zza("Overread allowed size end=" + zzaU, parcel);
    }

    public zzd[] zzcL(int i) {
        return new zzd[i];
    }
}
