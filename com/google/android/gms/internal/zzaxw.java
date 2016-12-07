package com.google.android.gms.internal;

import android.accounts.Account;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.safeparcel.zza;

public class zzaxw extends zza {
    public static final Creator<zzaxw> CREATOR = new zzaxx();
    final int mVersionCode;
    private final Account zzagg;
    private final String zzajk;
    private final Scope[] zzbCp;

    zzaxw(int i, Account account, Scope[] scopeArr, String str) {
        this.mVersionCode = i;
        this.zzagg = account;
        this.zzbCp = scopeArr;
        this.zzajk = str;
    }

    public Account getAccount() {
        return this.zzagg;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzaxx.zza(this, parcel, i);
    }

    public Scope[] zzOm() {
        return this.zzbCp;
    }

    public String zzqN() {
        return this.zzajk;
    }
}
