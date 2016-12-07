package com.google.android.gms.auth.api.signin;

import android.accounts.Account;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.safeparcel.zza;
import java.util.ArrayList;

public class zzb implements Creator<GoogleSignInOptions> {
    static void zza(GoogleSignInOptions googleSignInOptions, Parcel parcel, int i) {
        int zzcs = com.google.android.gms.common.internal.safeparcel.zzb.zzcs(parcel);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 1, googleSignInOptions.versionCode);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 2, googleSignInOptions.zzait(), false);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 3, googleSignInOptions.getAccount(), i, false);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 4, googleSignInOptions.zzaiu());
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 5, googleSignInOptions.zzaiv());
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 6, googleSignInOptions.zzaiw());
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 7, googleSignInOptions.zzaix(), false);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 8, googleSignInOptions.zzaiy(), false);
        com.google.android.gms.common.internal.safeparcel.zzb.zzaj(parcel, zzcs);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzaw(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzdk(i);
    }

    public GoogleSignInOptions zzaw(Parcel parcel) {
        String str = null;
        boolean z = false;
        int zzcr = zza.zzcr(parcel);
        String str2 = null;
        boolean z2 = false;
        boolean z3 = false;
        Account account = null;
        ArrayList arrayList = null;
        int i = 0;
        while (parcel.dataPosition() < zzcr) {
            int zzcq = zza.zzcq(parcel);
            switch (zza.zzgu(zzcq)) {
                case 1:
                    i = zza.zzg(parcel, zzcq);
                    break;
                case 2:
                    arrayList = zza.zzc(parcel, zzcq, Scope.CREATOR);
                    break;
                case 3:
                    account = (Account) zza.zza(parcel, zzcq, Account.CREATOR);
                    break;
                case 4:
                    z3 = zza.zzc(parcel, zzcq);
                    break;
                case 5:
                    z2 = zza.zzc(parcel, zzcq);
                    break;
                case 6:
                    z = zza.zzc(parcel, zzcq);
                    break;
                case 7:
                    str2 = zza.zzq(parcel, zzcq);
                    break;
                case 8:
                    str = zza.zzq(parcel, zzcq);
                    break;
                default:
                    zza.zzb(parcel, zzcq);
                    break;
            }
        }
        if (parcel.dataPosition() == zzcr) {
            return new GoogleSignInOptions(i, arrayList, account, z3, z2, z, str2, str);
        }
        throw new zza.zza("Overread allowed size end=" + zzcr, parcel);
    }

    public GoogleSignInOptions[] zzdk(int i) {
        return new GoogleSignInOptions[i];
    }
}
