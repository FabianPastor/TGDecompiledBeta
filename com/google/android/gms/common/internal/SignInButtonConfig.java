package com.google.android.gms.common.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;

public class SignInButtonConfig extends AbstractSafeParcelable {
    public static final Creator<SignInButtonConfig> CREATOR = new zzad();
    @Deprecated
    private final Scope[] Dg;
    private final int EL;
    private final int EM;
    final int mVersionCode;

    SignInButtonConfig(int i, int i2, int i3, Scope[] scopeArr) {
        this.mVersionCode = i;
        this.EL = i2;
        this.EM = i3;
        this.Dg = scopeArr;
    }

    public SignInButtonConfig(int i, int i2, Scope[] scopeArr) {
        this(1, i, i2, null);
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzad.zza(this, parcel, i);
    }

    public int zzawq() {
        return this.EL;
    }

    public int zzawr() {
        return this.EM;
    }

    @Deprecated
    public Scope[] zzaws() {
        return this.Dg;
    }
}
