package com.google.android.gms.wearable;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.zzbg;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;
import java.util.Arrays;

public class ConnectionConfiguration extends zzbfm implements ReflectedParcelable {
    public static final Creator<ConnectionConfiguration> CREATOR = new zzg();
    private final String mName;
    private volatile boolean zzdyh;
    private final int zzeie;
    private final int zzgkq;
    private final String zzgzw;
    private final boolean zzlgo;
    private volatile String zzlgp;
    private boolean zzlgq;
    private String zzlgr;

    ConnectionConfiguration(String str, String str2, int i, int i2, boolean z, boolean z2, String str3, boolean z3, String str4) {
        this.mName = str;
        this.zzgzw = str2;
        this.zzeie = i;
        this.zzgkq = i2;
        this.zzlgo = z;
        this.zzdyh = z2;
        this.zzlgp = str3;
        this.zzlgq = z3;
        this.zzlgr = str4;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof ConnectionConfiguration)) {
            return false;
        }
        ConnectionConfiguration connectionConfiguration = (ConnectionConfiguration) obj;
        return zzbg.equal(this.mName, connectionConfiguration.mName) && zzbg.equal(this.zzgzw, connectionConfiguration.zzgzw) && zzbg.equal(Integer.valueOf(this.zzeie), Integer.valueOf(connectionConfiguration.zzeie)) && zzbg.equal(Integer.valueOf(this.zzgkq), Integer.valueOf(connectionConfiguration.zzgkq)) && zzbg.equal(Boolean.valueOf(this.zzlgo), Boolean.valueOf(connectionConfiguration.zzlgo)) && zzbg.equal(Boolean.valueOf(this.zzlgq), Boolean.valueOf(connectionConfiguration.zzlgq));
    }

    public int hashCode() {
        return Arrays.hashCode(new Object[]{this.mName, this.zzgzw, Integer.valueOf(this.zzeie), Integer.valueOf(this.zzgkq), Boolean.valueOf(this.zzlgo), Boolean.valueOf(this.zzlgq)});
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("ConnectionConfiguration[ ");
        String str = "mName=";
        String valueOf = String.valueOf(this.mName);
        stringBuilder.append(valueOf.length() != 0 ? str.concat(valueOf) : new String(str));
        str = ", mAddress=";
        valueOf = String.valueOf(this.zzgzw);
        stringBuilder.append(valueOf.length() != 0 ? str.concat(valueOf) : new String(str));
        stringBuilder.append(", mType=" + this.zzeie);
        stringBuilder.append(", mRole=" + this.zzgkq);
        stringBuilder.append(", mEnabled=" + this.zzlgo);
        stringBuilder.append(", mIsConnected=" + this.zzdyh);
        str = ", mPeerNodeId=";
        valueOf = String.valueOf(this.zzlgp);
        stringBuilder.append(valueOf.length() != 0 ? str.concat(valueOf) : new String(str));
        stringBuilder.append(", mBtlePriority=" + this.zzlgq);
        str = ", mNodeId=";
        valueOf = String.valueOf(this.zzlgr);
        stringBuilder.append(valueOf.length() != 0 ? str.concat(valueOf) : new String(str));
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    public void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zza(parcel, 2, this.mName, false);
        zzbfp.zza(parcel, 3, this.zzgzw, false);
        zzbfp.zzc(parcel, 4, this.zzeie);
        zzbfp.zzc(parcel, 5, this.zzgkq);
        zzbfp.zza(parcel, 6, this.zzlgo);
        zzbfp.zza(parcel, 7, this.zzdyh);
        zzbfp.zza(parcel, 8, this.zzlgp, false);
        zzbfp.zza(parcel, 9, this.zzlgq);
        zzbfp.zza(parcel, 10, this.zzlgr, false);
        zzbfp.zzai(parcel, zze);
    }
}
