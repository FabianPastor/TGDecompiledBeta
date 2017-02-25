package com.google.android.gms.wallet.firstparty;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.zza;

public class InitializeBuyFlowRequest extends zza implements ReflectedParcelable {
    public static final Creator<InitializeBuyFlowRequest> CREATOR = new zzl();
    private final int zzaiI;
    byte[][] zzbRN;

    InitializeBuyFlowRequest(int i, byte[][] bArr) {
        this.zzaiI = i;
        this.zzbRN = bArr;
    }

    public int getVersionCode() {
        return this.zzaiI;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzl.zza(this, parcel, i);
    }
}
