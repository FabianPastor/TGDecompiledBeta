package com.google.android.gms.common.internal;

import android.accounts.Account;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.Nullable;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.internal.safeparcel.zza;

public class zzad extends zza {
    public static final Creator<zzad> CREATOR = new zzae();
    final int mVersionCode;
    private final int zzaFf;
    private final GoogleSignInAccount zzaFg;
    private final Account zzagg;

    zzad(int i, Account account, int i2, GoogleSignInAccount googleSignInAccount) {
        this.mVersionCode = i;
        this.zzagg = account;
        this.zzaFf = i2;
        this.zzaFg = googleSignInAccount;
    }

    public zzad(Account account, int i, GoogleSignInAccount googleSignInAccount) {
        this(2, account, i, googleSignInAccount);
    }

    public Account getAccount() {
        return this.zzagg;
    }

    public int getSessionId() {
        return this.zzaFf;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzae.zza(this, parcel, i);
    }

    @Nullable
    public GoogleSignInAccount zzxy() {
        return this.zzaFg;
    }
}
