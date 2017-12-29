package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;

public final class TimeInterval extends zzbfm {
    public static final Creator<TimeInterval> CREATOR = new zzk();
    private long zzlge;
    private long zzlgf;

    TimeInterval() {
    }

    public TimeInterval(long j, long j2) {
        this.zzlge = j;
        this.zzlgf = j2;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zza(parcel, 2, this.zzlge);
        zzbfp.zza(parcel, 3, this.zzlgf);
        zzbfp.zzai(parcel, zze);
    }
}
