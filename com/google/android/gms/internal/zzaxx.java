package com.google.android.gms.internal;

import android.accounts.Account;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzaxx implements Creator<zzaxw> {
    static void zza(zzaxw com_google_android_gms_internal_zzaxw, Parcel parcel, int i) {
        int zzaV = zzc.zzaV(parcel);
        zzc.zzc(parcel, 1, com_google_android_gms_internal_zzaxw.mVersionCode);
        zzc.zza(parcel, 2, com_google_android_gms_internal_zzaxw.getAccount(), i, false);
        zzc.zza(parcel, 3, com_google_android_gms_internal_zzaxw.zzOm(), i, false);
        zzc.zza(parcel, 4, com_google_android_gms_internal_zzaxw.zzqN(), false);
        zzc.zzJ(parcel, zzaV);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zziQ(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzmL(i);
    }

    public zzaxw zziQ(Parcel parcel) {
        String str = null;
        int zzaU = zzb.zzaU(parcel);
        int i = 0;
        Scope[] scopeArr = null;
        Account account = null;
        while (parcel.dataPosition() < zzaU) {
            Scope[] scopeArr2;
            Account account2;
            int zzg;
            String str2;
            int zzaT = zzb.zzaT(parcel);
            String str3;
            switch (zzb.zzcW(zzaT)) {
                case 1:
                    str3 = str;
                    scopeArr2 = scopeArr;
                    account2 = account;
                    zzg = zzb.zzg(parcel, zzaT);
                    str2 = str3;
                    break;
                case 2:
                    zzg = i;
                    Scope[] scopeArr3 = scopeArr;
                    account2 = (Account) zzb.zza(parcel, zzaT, Account.CREATOR);
                    str2 = str;
                    scopeArr2 = scopeArr3;
                    break;
                case 3:
                    account2 = account;
                    zzg = i;
                    str3 = str;
                    scopeArr2 = (Scope[]) zzb.zzb(parcel, zzaT, Scope.CREATOR);
                    str2 = str3;
                    break;
                case 4:
                    str2 = zzb.zzq(parcel, zzaT);
                    scopeArr2 = scopeArr;
                    account2 = account;
                    zzg = i;
                    break;
                default:
                    zzb.zzb(parcel, zzaT);
                    str2 = str;
                    scopeArr2 = scopeArr;
                    account2 = account;
                    zzg = i;
                    break;
            }
            i = zzg;
            account = account2;
            scopeArr = scopeArr2;
            str = str2;
        }
        if (parcel.dataPosition() == zzaU) {
            return new zzaxw(i, account, scopeArr, str);
        }
        throw new zza("Overread allowed size end=" + zzaU, parcel);
    }

    public zzaxw[] zzmL(int i) {
        return new zzaxw[i];
    }
}
