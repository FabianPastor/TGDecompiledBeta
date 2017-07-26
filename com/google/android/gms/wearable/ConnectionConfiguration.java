package com.google.android.gms.wearable;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.common.internal.zzbe;
import java.util.Arrays;

public class ConnectionConfiguration extends zza implements ReflectedParcelable {
    public static final Creator<ConnectionConfiguration> CREATOR = new zzg();
    private final String mName;
    private final int zzaMJ;
    private final String zzaTl;
    private volatile boolean zzair;
    private final int zzamr;
    private final boolean zzbRb;
    private volatile String zzbRc;
    private boolean zzbRd;
    private String zzbRe;

    ConnectionConfiguration(String str, String str2, int i, int i2, boolean z, boolean z2, String str3, boolean z3, String str4) {
        this.mName = str;
        this.zzaTl = str2;
        this.zzamr = i;
        this.zzaMJ = i2;
        this.zzbRb = z;
        this.zzair = z2;
        this.zzbRc = str3;
        this.zzbRd = z3;
        this.zzbRe = str4;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof ConnectionConfiguration)) {
            return false;
        }
        ConnectionConfiguration connectionConfiguration = (ConnectionConfiguration) obj;
        return zzbe.equal(this.mName, connectionConfiguration.mName) && zzbe.equal(this.zzaTl, connectionConfiguration.zzaTl) && zzbe.equal(Integer.valueOf(this.zzamr), Integer.valueOf(connectionConfiguration.zzamr)) && zzbe.equal(Integer.valueOf(this.zzaMJ), Integer.valueOf(connectionConfiguration.zzaMJ)) && zzbe.equal(Boolean.valueOf(this.zzbRb), Boolean.valueOf(connectionConfiguration.zzbRb)) && zzbe.equal(Boolean.valueOf(this.zzbRd), Boolean.valueOf(connectionConfiguration.zzbRd));
    }

    public int hashCode() {
        return Arrays.hashCode(new Object[]{this.mName, this.zzaTl, Integer.valueOf(this.zzamr), Integer.valueOf(this.zzaMJ), Boolean.valueOf(this.zzbRb), Boolean.valueOf(this.zzbRd)});
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("ConnectionConfiguration[ ");
        String str = "mName=";
        String valueOf = String.valueOf(this.mName);
        stringBuilder.append(valueOf.length() != 0 ? str.concat(valueOf) : new String(str));
        str = ", mAddress=";
        valueOf = String.valueOf(this.zzaTl);
        stringBuilder.append(valueOf.length() != 0 ? str.concat(valueOf) : new String(str));
        stringBuilder.append(", mType=" + this.zzamr);
        stringBuilder.append(", mRole=" + this.zzaMJ);
        stringBuilder.append(", mEnabled=" + this.zzbRb);
        stringBuilder.append(", mIsConnected=" + this.zzair);
        str = ", mPeerNodeId=";
        valueOf = String.valueOf(this.zzbRc);
        stringBuilder.append(valueOf.length() != 0 ? str.concat(valueOf) : new String(str));
        stringBuilder.append(", mBtlePriority=" + this.zzbRd);
        str = ", mNodeId=";
        valueOf = String.valueOf(this.zzbRe);
        stringBuilder.append(valueOf.length() != 0 ? str.concat(valueOf) : new String(str));
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    public void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zza(parcel, 2, this.mName, false);
        zzd.zza(parcel, 3, this.zzaTl, false);
        zzd.zzc(parcel, 4, this.zzamr);
        zzd.zzc(parcel, 5, this.zzaMJ);
        zzd.zza(parcel, 6, this.zzbRb);
        zzd.zza(parcel, 7, this.zzair);
        zzd.zza(parcel, 8, this.zzbRc, false);
        zzd.zza(parcel, 9, this.zzbRd);
        zzd.zza(parcel, 10, this.zzbRe, false);
        zzd.zzI(parcel, zze);
    }
}
