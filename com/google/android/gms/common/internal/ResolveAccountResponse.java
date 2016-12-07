package com.google.android.gms.common.internal;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.zzr.zza;

public class ResolveAccountResponse extends AbstractSafeParcelable {
    public static final Creator<ResolveAccountResponse> CREATOR = new zzae();
    IBinder AW;
    private boolean CX;
    final int mVersionCode;
    private ConnectionResult vm;
    private boolean xz;

    ResolveAccountResponse(int i, IBinder iBinder, ConnectionResult connectionResult, boolean z, boolean z2) {
        this.mVersionCode = i;
        this.AW = iBinder;
        this.vm = connectionResult;
        this.xz = z;
        this.CX = z2;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ResolveAccountResponse)) {
            return false;
        }
        ResolveAccountResponse resolveAccountResponse = (ResolveAccountResponse) obj;
        return this.vm.equals(resolveAccountResponse.vm) && zzavd().equals(resolveAccountResponse.zzavd());
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzae.zza(this, parcel, i);
    }

    public zzr zzavd() {
        return zza.zzdr(this.AW);
    }

    public ConnectionResult zzave() {
        return this.vm;
    }

    public boolean zzavf() {
        return this.xz;
    }

    public boolean zzavg() {
        return this.CX;
    }
}
