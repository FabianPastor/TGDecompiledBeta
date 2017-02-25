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
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zzc(parcel, 1, com_google_android_gms_common_internal_zzj.version);
        zzc.zzc(parcel, 2, com_google_android_gms_common_internal_zzj.zzaFK);
        zzc.zzc(parcel, 3, com_google_android_gms_common_internal_zzj.zzaFL);
        zzc.zza(parcel, 4, com_google_android_gms_common_internal_zzj.zzaFM, false);
        zzc.zza(parcel, 5, com_google_android_gms_common_internal_zzj.zzaFN, false);
        zzc.zza(parcel, 6, com_google_android_gms_common_internal_zzj.zzaFO, i, false);
        zzc.zza(parcel, 7, com_google_android_gms_common_internal_zzj.zzaFP, false);
        zzc.zza(parcel, 8, com_google_android_gms_common_internal_zzj.zzaFQ, i, false);
        zzc.zza(parcel, 10, com_google_android_gms_common_internal_zzj.zzaFR, i, false);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzaS(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzcU(i);
    }

    public zzj zzaS(Parcel parcel) {
        int i = 0;
        com.google.android.gms.common.zzc[] com_google_android_gms_common_zzcArr = null;
        int zzaY = zzb.zzaY(parcel);
        Account account = null;
        Bundle bundle = null;
        Scope[] scopeArr = null;
        IBinder iBinder = null;
        String str = null;
        int i2 = 0;
        int i3 = 0;
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 1:
                    i3 = zzb.zzg(parcel, zzaX);
                    break;
                case 2:
                    i2 = zzb.zzg(parcel, zzaX);
                    break;
                case 3:
                    i = zzb.zzg(parcel, zzaX);
                    break;
                case 4:
                    str = zzb.zzq(parcel, zzaX);
                    break;
                case 5:
                    iBinder = zzb.zzr(parcel, zzaX);
                    break;
                case 6:
                    scopeArr = (Scope[]) zzb.zzb(parcel, zzaX, Scope.CREATOR);
                    break;
                case 7:
                    bundle = zzb.zzs(parcel, zzaX);
                    break;
                case 8:
                    account = (Account) zzb.zza(parcel, zzaX, Account.CREATOR);
                    break;
                case 10:
                    com_google_android_gms_common_zzcArr = (com.google.android.gms.common.zzc[]) zzb.zzb(parcel, zzaX, com.google.android.gms.common.zzc.CREATOR);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new zzj(i3, i2, i, str, iBinder, scopeArr, bundle, account, com_google_android_gms_common_zzcArr);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public zzj[] zzcU(int i) {
        return new zzj[i];
    }
}
