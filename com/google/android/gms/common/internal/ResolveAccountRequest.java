package com.google.android.gms.common.internal;

import android.accounts.Account;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.Nullable;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;

public class ResolveAccountRequest extends AbstractSafeParcelable {
    public static final Creator<ResolveAccountRequest> CREATOR = new zzab();
    private final int EH;
    private final GoogleSignInAccount EI;
    private final Account gj;
    final int mVersionCode;

    ResolveAccountRequest(int i, Account account, int i2, GoogleSignInAccount googleSignInAccount) {
        this.mVersionCode = i;
        this.gj = account;
        this.EH = i2;
        this.EI = googleSignInAccount;
    }

    public ResolveAccountRequest(Account account, int i, GoogleSignInAccount googleSignInAccount) {
        this(2, account, i, googleSignInAccount);
    }

    public Account getAccount() {
        return this.gj;
    }

    public int getSessionId() {
        return this.EH;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzab.zza(this, parcel, i);
    }

    @Nullable
    public GoogleSignInAccount zzawl() {
        return this.EI;
    }
}
