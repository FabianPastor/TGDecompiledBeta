package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;

public final class InstrumentInfo extends zzbfm {
    public static final Creator<InstrumentInfo> CREATOR = new zzp();
    private String zzlaw;
    private String zzlax;
    private int zzlay;

    private InstrumentInfo() {
    }

    public InstrumentInfo(String str, String str2, int i) {
        this.zzlaw = str;
        this.zzlax = str2;
        this.zzlay = i;
    }

    public final int getCardClass() {
        switch (this.zzlay) {
            case 1:
            case 2:
            case 3:
                return this.zzlay;
            default:
                return 0;
        }
    }

    public final String getInstrumentDetails() {
        return this.zzlax;
    }

    public final String getInstrumentType() {
        return this.zzlaw;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zza(parcel, 2, getInstrumentType(), false);
        zzbfp.zza(parcel, 3, getInstrumentDetails(), false);
        zzbfp.zzc(parcel, 4, getCardClass());
        zzbfp.zzai(parcel, zze);
    }
}
