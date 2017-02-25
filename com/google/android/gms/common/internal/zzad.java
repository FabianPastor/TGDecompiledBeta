package com.google.android.gms.common.internal;

import android.accounts.Account;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.Nullable;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.internal.safeparcel.zza;

public class zzad extends zza {
    public static final Creator<zzad> CREATOR = new zzae();
    private final int zzaGC;
    private final GoogleSignInAccount zzaGD;
    private final Account zzahh;
    final int zzaiI;

    zzad(int i, Account account, int i2, GoogleSignInAccount googleSignInAccount) {
        this.zzaiI = i;
        this.zzahh = account;
        this.zzaGC = i2;
        this.zzaGD = googleSignInAccount;
    }

    public zzad(Account account, int i, GoogleSignInAccount googleSignInAccount) {
        this(2, account, i, googleSignInAccount);
    }

    public Account getAccount() {
        return this.zzahh;
    }

    public int getSessionId() {
        return this.zzaGC;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzae.zza(this, parcel, i);
    }

    @Nullable
    public GoogleSignInAccount zzyf() {
        return this.zzaGD;
    }
}
