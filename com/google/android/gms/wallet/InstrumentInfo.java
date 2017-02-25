package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public final class InstrumentInfo extends zza {
    public static final Creator<InstrumentInfo> CREATOR = new zzj();
    private String zzbQq;
    private String zzbQr;

    public InstrumentInfo(String str, String str2) {
        this.zzbQq = str;
        this.zzbQr = str2;
    }

    public String getInstrumentDetails() {
        return this.zzbQr;
    }

    public String getInstrumentType() {
        return this.zzbQq;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzj.zza(this, parcel, i);
    }
}
