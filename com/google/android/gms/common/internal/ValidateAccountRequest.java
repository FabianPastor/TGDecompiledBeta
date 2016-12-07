package com.google.android.gms.common.internal;

import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;

@Deprecated
public class ValidateAccountRequest extends AbstractSafeParcelable {
    public static final Creator<ValidateAccountRequest> CREATOR = new zzak();
    final IBinder AW;
    private final Scope[] AX;
    private final int De;
    private final Bundle Df;
    private final String Dg;
    final int mVersionCode;

    ValidateAccountRequest(int i, int i2, IBinder iBinder, Scope[] scopeArr, Bundle bundle, String str) {
        this.mVersionCode = i;
        this.De = i2;
        this.AW = iBinder;
        this.AX = scopeArr;
        this.Df = bundle;
        this.Dg = str;
    }

    public String getCallingPackage() {
        return this.Dg;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzak.zza(this, parcel, i);
    }

    public Scope[] zzavj() {
        return this.AX;
    }

    public int zzavl() {
        return this.De;
    }

    public Bundle zzavm() {
        return this.Df;
    }
}
