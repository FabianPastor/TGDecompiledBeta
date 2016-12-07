package com.google.android.gms.common.stats;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import java.util.List;

public final class WakeLockEvent extends StatsEvent {
    public static final Creator<WakeLockEvent> CREATOR = new zzg();
    private final long DM;
    private int DN;
    private final long DU;
    private long DW;
    private final String EA;
    private final int EB;
    private final List<String> EC;
    private final String ED;
    private int EE;
    private final String EF;
    private final float EG;
    private final String Ey;
    private final String Ez;
    private final long mTimeout;
    final int mVersionCode;

    WakeLockEvent(int i, long j, int i2, String str, int i3, List<String> list, String str2, long j2, int i4, String str3, String str4, float f, long j3, String str5) {
        this.mVersionCode = i;
        this.DM = j;
        this.DN = i2;
        this.Ey = str;
        this.Ez = str3;
        this.EA = str5;
        this.EB = i3;
        this.DW = -1;
        this.EC = list;
        this.ED = str2;
        this.DU = j2;
        this.EE = i4;
        this.EF = str4;
        this.EG = f;
        this.mTimeout = j3;
    }

    public WakeLockEvent(long j, int i, String str, int i2, List<String> list, String str2, long j2, int i3, String str3, String str4, float f, long j3, String str5) {
        this(2, j, i, str, i2, list, str2, j2, i3, str3, str4, f, j3, str5);
    }

    public int getEventType() {
        return this.DN;
    }

    public long getTimeMillis() {
        return this.DM;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzg.zza(this, parcel, i);
    }

    public String zzawp() {
        return this.ED;
    }

    public long zzawq() {
        return this.DW;
    }

    public long zzaws() {
        return this.DU;
    }

    public String zzawt() {
        String valueOf = String.valueOf("\t");
        String valueOf2 = String.valueOf(zzaww());
        String valueOf3 = String.valueOf("\t");
        int zzawz = zzawz();
        String valueOf4 = String.valueOf("\t");
        String join = zzaxa() == null ? "" : TextUtils.join(",", zzaxa());
        String valueOf5 = String.valueOf("\t");
        int zzaxb = zzaxb();
        String valueOf6 = String.valueOf("\t");
        String zzawx = zzawx() == null ? "" : zzawx();
        String valueOf7 = String.valueOf("\t");
        String zzaxc = zzaxc() == null ? "" : zzaxc();
        String valueOf8 = String.valueOf("\t");
        float zzaxd = zzaxd();
        String valueOf9 = String.valueOf("\t");
        String zzawy = zzawy() == null ? "" : zzawy();
        return new StringBuilder(((((((((((((String.valueOf(valueOf).length() + 37) + String.valueOf(valueOf2).length()) + String.valueOf(valueOf3).length()) + String.valueOf(valueOf4).length()) + String.valueOf(join).length()) + String.valueOf(valueOf5).length()) + String.valueOf(valueOf6).length()) + String.valueOf(zzawx).length()) + String.valueOf(valueOf7).length()) + String.valueOf(zzaxc).length()) + String.valueOf(valueOf8).length()) + String.valueOf(valueOf9).length()) + String.valueOf(zzawy).length()).append(valueOf).append(valueOf2).append(valueOf3).append(zzawz).append(valueOf4).append(join).append(valueOf5).append(zzaxb).append(valueOf6).append(zzawx).append(valueOf7).append(zzaxc).append(valueOf8).append(zzaxd).append(valueOf9).append(zzawy).toString();
    }

    public String zzaww() {
        return this.Ey;
    }

    public String zzawx() {
        return this.Ez;
    }

    public String zzawy() {
        return this.EA;
    }

    public int zzawz() {
        return this.EB;
    }

    public List<String> zzaxa() {
        return this.EC;
    }

    public int zzaxb() {
        return this.EE;
    }

    public String zzaxc() {
        return this.EF;
    }

    public float zzaxd() {
        return this.EG;
    }

    public long zzaxe() {
        return this.mTimeout;
    }
}
