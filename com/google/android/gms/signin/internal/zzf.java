package com.google.android.gms.signin.internal;

import android.accounts.Account;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zzf implements Creator<RecordConsentRequest> {
    static void zza(RecordConsentRequest recordConsentRequest, Parcel parcel, int i) {
        int zzcs = zzb.zzcs(parcel);
        zzb.zzc(parcel, 1, recordConsentRequest.mVersionCode);
        zzb.zza(parcel, 2, recordConsentRequest.getAccount(), i, false);
        zzb.zza(parcel, 3, recordConsentRequest.zzcdk(), i, false);
        zzb.zza(parcel, 4, recordConsentRequest.zzaix(), false);
        zzb.zzaj(parcel, zzcs);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzsb(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzzw(i);
    }

    public RecordConsentRequest zzsb(Parcel parcel) {
        String str = null;
        int zzcr = zza.zzcr(parcel);
        int i = 0;
        Scope[] scopeArr = null;
        Account account = null;
        while (parcel.dataPosition() < zzcr) {
            Scope[] scopeArr2;
            Account account2;
            int zzg;
            String str2;
            int zzcq = zza.zzcq(parcel);
            String str3;
            switch (zza.zzgu(zzcq)) {
                case 1:
                    str3 = str;
                    scopeArr2 = scopeArr;
                    account2 = account;
                    zzg = zza.zzg(parcel, zzcq);
                    str2 = str3;
                    break;
                case 2:
                    zzg = i;
                    Scope[] scopeArr3 = scopeArr;
                    account2 = (Account) zza.zza(parcel, zzcq, Account.CREATOR);
                    str2 = str;
                    scopeArr2 = scopeArr3;
                    break;
                case 3:
                    account2 = account;
                    zzg = i;
                    str3 = str;
                    scopeArr2 = (Scope[]) zza.zzb(parcel, zzcq, Scope.CREATOR);
                    str2 = str3;
                    break;
                case 4:
                    str2 = zza.zzq(parcel, zzcq);
                    scopeArr2 = scopeArr;
                    account2 = account;
                    zzg = i;
                    break;
                default:
                    zza.zzb(parcel, zzcq);
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
        if (parcel.dataPosition() == zzcr) {
            return new RecordConsentRequest(i, account, scopeArr, str);
        }
        throw new zza.zza("Overread allowed size end=" + zzcr, parcel);
    }

    public RecordConsentRequest[] zzzw(int i) {
        return new RecordConsentRequest[i];
    }
}
