package com.google.android.gms.common.internal;

import android.accounts.Account;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzae implements Creator<zzad> {
    static void zza(zzad com_google_android_gms_common_internal_zzad, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zzc(parcel, 1, com_google_android_gms_common_internal_zzad.zzaiI);
        zzc.zza(parcel, 2, com_google_android_gms_common_internal_zzad.getAccount(), i, false);
        zzc.zzc(parcel, 3, com_google_android_gms_common_internal_zzad.getSessionId());
        zzc.zza(parcel, 4, com_google_android_gms_common_internal_zzad.zzyf(), i, false);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzaT(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzcY(i);
    }

    public zzad zzaT(Parcel parcel) {
        GoogleSignInAccount googleSignInAccount = null;
        int i = 0;
        int zzaY = zzb.zzaY(parcel);
        Account account = null;
        int i2 = 0;
        while (parcel.dataPosition() < zzaY) {
            int i3;
            Account account2;
            int zzg;
            GoogleSignInAccount googleSignInAccount2;
            int zzaX = zzb.zzaX(parcel);
            GoogleSignInAccount googleSignInAccount3;
            switch (zzb.zzdc(zzaX)) {
                case 1:
                    googleSignInAccount3 = googleSignInAccount;
                    i3 = i;
                    account2 = account;
                    zzg = zzb.zzg(parcel, zzaX);
                    googleSignInAccount2 = googleSignInAccount3;
                    break;
                case 2:
                    zzg = i2;
                    int i4 = i;
                    account2 = (Account) zzb.zza(parcel, zzaX, Account.CREATOR);
                    googleSignInAccount2 = googleSignInAccount;
                    i3 = i4;
                    break;
                case 3:
                    account2 = account;
                    zzg = i2;
                    googleSignInAccount3 = googleSignInAccount;
                    i3 = zzb.zzg(parcel, zzaX);
                    googleSignInAccount2 = googleSignInAccount3;
                    break;
                case 4:
                    googleSignInAccount2 = (GoogleSignInAccount) zzb.zza(parcel, zzaX, GoogleSignInAccount.CREATOR);
                    i3 = i;
                    account2 = account;
                    zzg = i2;
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    googleSignInAccount2 = googleSignInAccount;
                    i3 = i;
                    account2 = account;
                    zzg = i2;
                    break;
            }
            i2 = zzg;
            account = account2;
            i = i3;
            googleSignInAccount = googleSignInAccount2;
        }
        if (parcel.dataPosition() == zzaY) {
            return new zzad(i2, account, i, googleSignInAccount);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public zzad[] zzcY(int i) {
        return new zzad[i];
    }
}
