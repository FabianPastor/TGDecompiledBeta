package com.google.android.gms.internal;

import android.accounts.Account;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzbas implements Creator<zzbar> {
    static void zza(zzbar com_google_android_gms_internal_zzbar, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zzc(parcel, 1, com_google_android_gms_internal_zzbar.zzaiI);
        zzc.zza(parcel, 2, com_google_android_gms_internal_zzbar.getAccount(), i, false);
        zzc.zza(parcel, 3, com_google_android_gms_internal_zzbar.zzPQ(), i, false);
        zzc.zza(parcel, 4, com_google_android_gms_internal_zzbar.getServerClientId(), false);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzjw(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zznw(i);
    }

    public zzbar zzjw(Parcel parcel) {
        String str = null;
        int zzaY = zzb.zzaY(parcel);
        int i = 0;
        Scope[] scopeArr = null;
        Account account = null;
        while (parcel.dataPosition() < zzaY) {
            Scope[] scopeArr2;
            Account account2;
            int zzg;
            String str2;
            int zzaX = zzb.zzaX(parcel);
            String str3;
            switch (zzb.zzdc(zzaX)) {
                case 1:
                    str3 = str;
                    scopeArr2 = scopeArr;
                    account2 = account;
                    zzg = zzb.zzg(parcel, zzaX);
                    str2 = str3;
                    break;
                case 2:
                    zzg = i;
                    Scope[] scopeArr3 = scopeArr;
                    account2 = (Account) zzb.zza(parcel, zzaX, Account.CREATOR);
                    str2 = str;
                    scopeArr2 = scopeArr3;
                    break;
                case 3:
                    account2 = account;
                    zzg = i;
                    str3 = str;
                    scopeArr2 = (Scope[]) zzb.zzb(parcel, zzaX, Scope.CREATOR);
                    str2 = str3;
                    break;
                case 4:
                    str2 = zzb.zzq(parcel, zzaX);
                    scopeArr2 = scopeArr;
                    account2 = account;
                    zzg = i;
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
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
        if (parcel.dataPosition() == zzaY) {
            return new zzbar(i, account, scopeArr, str);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public zzbar[] zznw(int i) {
        return new zzbar[i];
    }
}
