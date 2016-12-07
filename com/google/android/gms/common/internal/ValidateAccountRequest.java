package com.google.android.gms.common.internal;

import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;

@Deprecated
public class ValidateAccountRequest extends AbstractSafeParcelable {
    public static final Creator<ValidateAccountRequest> CREATOR = new zzai();
    final IBinder Df;
    private final Scope[] Dg;
    private final int ER;
    private final Bundle ES;
    private final String ET;
    final int mVersionCode;

    ValidateAccountRequest(int i, int i2, IBinder iBinder, Scope[] scopeArr, Bundle bundle, String str) {
        this.mVersionCode = i;
        this.ER = i2;
        this.Df = iBinder;
        this.Dg = scopeArr;
        this.ES = bundle;
        this.ET = str;
    }

    public String getCallingPackage() {
        return this.ET;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzai.zza(this, parcel, i);
    }

    public Scope[] zzaws() {
        return this.Dg;
    }

    public int zzawu() {
        return this.ER;
    }

    public Bundle zzawv() {
        return this.ES;
    }
}
