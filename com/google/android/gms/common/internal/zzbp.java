package com.google.android.gms.common.internal;

import android.accounts.Account;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public final class zzbp extends zza {
    public static final Creator<zzbp> CREATOR = new zzbq();
    private final int zzaIo;
    private final GoogleSignInAccount zzaIp;
    private final Account zzajb;
    private int zzaku;

    zzbp(int i, Account account, int i2, GoogleSignInAccount googleSignInAccount) {
        this.zzaku = i;
        this.zzajb = account;
        this.zzaIo = i2;
        this.zzaIp = googleSignInAccount;
    }

    public zzbp(Account account, int i, GoogleSignInAccount googleSignInAccount) {
        this(2, account, i, googleSignInAccount);
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zzc(parcel, 1, this.zzaku);
        zzd.zza(parcel, 2, this.zzajb, i, false);
        zzd.zzc(parcel, 3, this.zzaIo);
        zzd.zza(parcel, 4, this.zzaIp, i, false);
        zzd.zzI(parcel, zze);
    }
}
