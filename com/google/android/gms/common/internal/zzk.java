package com.google.android.gms.common.internal;

import android.accounts.Account;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzk implements Creator<zzj> {
    static void zza(zzj com_google_android_gms_common_internal_zzj, Parcel parcel, int i) {
        int zzaV = zzc.zzaV(parcel);
        zzc.zzc(parcel, 1, com_google_android_gms_common_internal_zzj.version);
        zzc.zzc(parcel, 2, com_google_android_gms_common_internal_zzj.zzaEm);
        zzc.zzc(parcel, 3, com_google_android_gms_common_internal_zzj.zzaEn);
        zzc.zza(parcel, 4, com_google_android_gms_common_internal_zzj.zzaEo, false);
        zzc.zza(parcel, 5, com_google_android_gms_common_internal_zzj.zzaEp, false);
        zzc.zza(parcel, 6, com_google_android_gms_common_internal_zzj.zzaEq, i, false);
        zzc.zza(parcel, 7, com_google_android_gms_common_internal_zzj.zzaEr, false);
        zzc.zza(parcel, 8, com_google_android_gms_common_internal_zzj.zzaEs, i, false);
        zzc.zza(parcel, 9, com_google_android_gms_common_internal_zzj.zzaEt);
        zzc.zzJ(parcel, zzaV);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzaO(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzcO(i);
    }

    public zzj zzaO(Parcel parcel) {
        int i = 0;
        Account account = null;
        int zzaU = zzb.zzaU(parcel);
        long j = 0;
        Bundle bundle = null;
        Scope[] scopeArr = null;
        IBinder iBinder = null;
        String str = null;
        int i2 = 0;
        int i3 = 0;
        while (parcel.dataPosition() < zzaU) {
            int zzaT = zzb.zzaT(parcel);
            switch (zzb.zzcW(zzaT)) {
                case 1:
                    i3 = zzb.zzg(parcel, zzaT);
                    break;
                case 2:
                    i2 = zzb.zzg(parcel, zzaT);
                    break;
                case 3:
                    i = zzb.zzg(parcel, zzaT);
                    break;
                case 4:
                    str = zzb.zzq(parcel, zzaT);
                    break;
                case 5:
                    iBinder = zzb.zzr(parcel, zzaT);
                    break;
                case 6:
                    scopeArr = (Scope[]) zzb.zzb(parcel, zzaT, Scope.CREATOR);
                    break;
                case 7:
                    bundle = zzb.zzs(parcel, zzaT);
                    break;
                case 8:
                    account = (Account) zzb.zza(parcel, zzaT, Account.CREATOR);
                    break;
                case 9:
                    j = zzb.zzi(parcel, zzaT);
                    break;
                default:
                    zzb.zzb(parcel, zzaT);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaU) {
            return new zzj(i3, i2, i, str, iBinder, scopeArr, bundle, account, j);
        }
        throw new zza("Overread allowed size end=" + zzaU, parcel);
    }

    public zzj[] zzcO(int i) {
        return new zzj[i];
    }
}
