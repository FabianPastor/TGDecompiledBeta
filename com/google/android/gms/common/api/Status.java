package com.google.android.gms.common.api;

import android.app.PendingIntent;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.zzbg;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;
import java.util.Arrays;

public final class Status extends zzbfm implements Result, ReflectedParcelable {
    public static final Creator<Status> CREATOR = new zzg();
    public static final Status zzfni = new Status(0);
    public static final Status zzfnj = new Status(14);
    public static final Status zzfnk = new Status(8);
    public static final Status zzfnl = new Status(15);
    public static final Status zzfnm = new Status(16);
    public static final Status zzfnn = new Status(17);
    private static Status zzfno = new Status(18);
    private final int zzcd;
    private int zzeck;
    private final PendingIntent zzeeo;
    private final String zzfks;

    public Status(int i) {
        this(i, null);
    }

    Status(int i, int i2, String str, PendingIntent pendingIntent) {
        this.zzeck = i;
        this.zzcd = i2;
        this.zzfks = str;
        this.zzeeo = pendingIntent;
    }

    public Status(int i, String str) {
        this(1, i, str, null);
    }

    public Status(int i, String str, PendingIntent pendingIntent) {
        this(1, i, str, pendingIntent);
    }

    public final boolean equals(Object obj) {
        if (!(obj instanceof Status)) {
            return false;
        }
        Status status = (Status) obj;
        return this.zzeck == status.zzeck && this.zzcd == status.zzcd && zzbg.equal(this.zzfks, status.zzfks) && zzbg.equal(this.zzeeo, status.zzeeo);
    }

    public final Status getStatus() {
        return this;
    }

    public final int getStatusCode() {
        return this.zzcd;
    }

    public final String getStatusMessage() {
        return this.zzfks;
    }

    public final boolean hasResolution() {
        return this.zzeeo != null;
    }

    public final int hashCode() {
        return Arrays.hashCode(new Object[]{Integer.valueOf(this.zzeck), Integer.valueOf(this.zzcd), this.zzfks, this.zzeeo});
    }

    public final boolean isSuccess() {
        return this.zzcd <= 0;
    }

    public final String toString() {
        return zzbg.zzx(this).zzg("statusCode", zzagx()).zzg("resolution", this.zzeeo).toString();
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zzc(parcel, 1, getStatusCode());
        zzbfp.zza(parcel, 2, getStatusMessage(), false);
        zzbfp.zza(parcel, 3, this.zzeeo, i, false);
        zzbfp.zzc(parcel, 1000, this.zzeck);
        zzbfp.zzai(parcel, zze);
    }

    public final String zzagx() {
        return this.zzfks != null ? this.zzfks : CommonStatusCodes.getStatusCodeString(this.zzcd);
    }
}
