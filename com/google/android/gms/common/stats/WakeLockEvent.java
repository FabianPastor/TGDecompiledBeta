package com.google.android.gms.common.stats;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import java.util.List;

public final class WakeLockEvent extends StatsEvent {
    public static final Creator<WakeLockEvent> CREATOR = new zzf();
    private final long mTimeout;
    final int mVersionCode;
    private final List<String> zzaGA;
    private final String zzaGB;
    private final long zzaGC;
    private int zzaGD;
    private final String zzaGE;
    private final float zzaGF;
    private long zzaGG;
    private final long zzaGu;
    private int zzaGv;
    private final String zzaGw;
    private final String zzaGx;
    private final String zzaGy;
    private final int zzaGz;

    WakeLockEvent(int i, long j, int i2, String str, int i3, List<String> list, String str2, long j2, int i4, String str3, String str4, float f, long j3, String str5) {
        this.mVersionCode = i;
        this.zzaGu = j;
        this.zzaGv = i2;
        this.zzaGw = str;
        this.zzaGx = str3;
        this.zzaGy = str5;
        this.zzaGz = i3;
        this.zzaGG = -1;
        this.zzaGA = list;
        this.zzaGB = str2;
        this.zzaGC = j2;
        this.zzaGD = i4;
        this.zzaGE = str4;
        this.zzaGF = f;
        this.mTimeout = j3;
    }

    public WakeLockEvent(long j, int i, String str, int i2, List<String> list, String str2, long j2, int i3, String str3, String str4, float f, long j3, String str5) {
        this(2, j, i, str, i2, list, str2, j2, i3, str3, str4, f, j3, str5);
    }

    public int getEventType() {
        return this.zzaGv;
    }

    public long getTimeMillis() {
        return this.zzaGu;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzf.zza(this, parcel, i);
    }

    public long zzye() {
        return this.zzaGG;
    }

    public String zzyf() {
        String valueOf = String.valueOf("\t");
        String valueOf2 = String.valueOf(zzyg());
        String valueOf3 = String.valueOf("\t");
        int zzyj = zzyj();
        String valueOf4 = String.valueOf("\t");
        String join = zzyk() == null ? "" : TextUtils.join(",", zzyk());
        String valueOf5 = String.valueOf("\t");
        int zzyn = zzyn();
        String valueOf6 = String.valueOf("\t");
        String zzyh = zzyh() == null ? "" : zzyh();
        String valueOf7 = String.valueOf("\t");
        String zzyo = zzyo() == null ? "" : zzyo();
        String valueOf8 = String.valueOf("\t");
        float zzyp = zzyp();
        String valueOf9 = String.valueOf("\t");
        String zzyi = zzyi() == null ? "" : zzyi();
        return new StringBuilder(((((((((((((String.valueOf(valueOf).length() + 37) + String.valueOf(valueOf2).length()) + String.valueOf(valueOf3).length()) + String.valueOf(valueOf4).length()) + String.valueOf(join).length()) + String.valueOf(valueOf5).length()) + String.valueOf(valueOf6).length()) + String.valueOf(zzyh).length()) + String.valueOf(valueOf7).length()) + String.valueOf(zzyo).length()) + String.valueOf(valueOf8).length()) + String.valueOf(valueOf9).length()) + String.valueOf(zzyi).length()).append(valueOf).append(valueOf2).append(valueOf3).append(zzyj).append(valueOf4).append(join).append(valueOf5).append(zzyn).append(valueOf6).append(zzyh).append(valueOf7).append(zzyo).append(valueOf8).append(zzyp).append(valueOf9).append(zzyi).toString();
    }

    public String zzyg() {
        return this.zzaGw;
    }

    public String zzyh() {
        return this.zzaGx;
    }

    public String zzyi() {
        return this.zzaGy;
    }

    public int zzyj() {
        return this.zzaGz;
    }

    public List<String> zzyk() {
        return this.zzaGA;
    }

    public String zzyl() {
        return this.zzaGB;
    }

    public long zzym() {
        return this.zzaGC;
    }

    public int zzyn() {
        return this.zzaGD;
    }

    public String zzyo() {
        return this.zzaGE;
    }

    public float zzyp() {
        return this.zzaGF;
    }

    public long zzyq() {
        return this.mTimeout;
    }
}
