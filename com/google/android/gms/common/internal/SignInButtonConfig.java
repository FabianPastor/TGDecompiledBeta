package com.google.android.gms.common.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;

public class SignInButtonConfig extends AbstractSafeParcelable {
    public static final Creator<SignInButtonConfig> CREATOR = new zzaf();
    @Deprecated
    private final Scope[] AX;
    private final int CY;
    private final int CZ;
    final int mVersionCode;

    SignInButtonConfig(int i, int i2, int i3, Scope[] scopeArr) {
        this.mVersionCode = i;
        this.CY = i2;
        this.CZ = i3;
        this.AX = scopeArr;
    }

    public SignInButtonConfig(int i, int i2, Scope[] scopeArr) {
        this(1, i, i2, null);
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzaf.zza(this, parcel, i);
    }

    public int zzavh() {
        return this.CY;
    }

    public int zzavi() {
        return this.CZ;
    }

    @Deprecated
    public Scope[] zzavj() {
        return this.AX;
    }
}
