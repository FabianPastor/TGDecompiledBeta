package com.google.android.gms.common.stats;

import android.os.Parcel;
import android.os.Parcelable.Creator;

public final class ConnectionEvent extends StatsEvent {
    public static final Creator<ConnectionEvent> CREATOR = new zza();
    private final long DM;
    private int DN;
    private final String DO;
    private final String DP;
    private final String DQ;
    private final String DR;
    private final String DS;
    private final String DT;
    private final long DU;
    private final long DV;
    private long DW;
    final int mVersionCode;

    ConnectionEvent(int i, long j, int i2, String str, String str2, String str3, String str4, String str5, String str6, long j2, long j3) {
        this.mVersionCode = i;
        this.DM = j;
        this.DN = i2;
        this.DO = str;
        this.DP = str2;
        this.DQ = str3;
        this.DR = str4;
        this.DW = -1;
        this.DS = str5;
        this.DT = str6;
        this.DU = j2;
        this.DV = j3;
    }

    public ConnectionEvent(long j, int i, String str, String str2, String str3, String str4, String str5, String str6, long j2, long j3) {
        this(1, j, i, str, str2, str3, str4, str5, str6, j2, j3);
    }

    public int getEventType() {
        return this.DN;
    }

    public long getTimeMillis() {
        return this.DM;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zza.zza(this, parcel, i);
    }

    public String zzawk() {
        return this.DO;
    }

    public String zzawl() {
        return this.DP;
    }

    public String zzawm() {
        return this.DQ;
    }

    public String zzawn() {
        return this.DR;
    }

    public String zzawo() {
        return this.DS;
    }

    public String zzawp() {
        return this.DT;
    }

    public long zzawq() {
        return this.DW;
    }

    public long zzawr() {
        return this.DV;
    }

    public long zzaws() {
        return this.DU;
    }

    public String zzawt() {
        String valueOf = String.valueOf("\t");
        String valueOf2 = String.valueOf(zzawk());
        String valueOf3 = String.valueOf(zzawl());
        String valueOf4 = String.valueOf("\t");
        String valueOf5 = String.valueOf(zzawm());
        String valueOf6 = String.valueOf(zzawn());
        String valueOf7 = String.valueOf("\t");
        String str = this.DS == null ? "" : this.DS;
        String valueOf8 = String.valueOf("\t");
        return new StringBuilder(((((((((String.valueOf(valueOf).length() + 22) + String.valueOf(valueOf2).length()) + String.valueOf(valueOf3).length()) + String.valueOf(valueOf4).length()) + String.valueOf(valueOf5).length()) + String.valueOf(valueOf6).length()) + String.valueOf(valueOf7).length()) + String.valueOf(str).length()) + String.valueOf(valueOf8).length()).append(valueOf).append(valueOf2).append("/").append(valueOf3).append(valueOf4).append(valueOf5).append("/").append(valueOf6).append(valueOf7).append(str).append(valueOf8).append(zzawr()).toString();
    }
}
