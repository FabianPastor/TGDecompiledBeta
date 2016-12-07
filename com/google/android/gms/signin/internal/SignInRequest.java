package com.google.android.gms.signin.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ResolveAccountRequest;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;

public class SignInRequest extends AbstractSafeParcelable {
    public static final Creator<SignInRequest> CREATOR = new zzh();
    final ResolveAccountRequest aDw;
    final int mVersionCode;

    SignInRequest(int i, ResolveAccountRequest resolveAccountRequest) {
        this.mVersionCode = i;
        this.aDw = resolveAccountRequest;
    }

    public SignInRequest(ResolveAccountRequest resolveAccountRequest) {
        this(1, resolveAccountRequest);
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzh.zza(this, parcel, i);
    }

    public ResolveAccountRequest zzcdm() {
        return this.aDw;
    }
}
