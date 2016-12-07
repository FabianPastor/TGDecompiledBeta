package com.google.android.gms.common.stats;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import java.util.List;

public final class WakeLockEvent extends StatsEvent {
    public static final Creator<WakeLockEvent> CREATOR = new zzf();
    private final long Ga;
    private int Gb;
    private final String Gc;
    private final String Gd;
    private final String Ge;
    private final int Gf;
    private final List<String> Gg;
    private final String Gh;
    private final long Gi;
    private int Gj;
    private final String Gk;
    private final float Gl;
    private long Gm;
    private final long mTimeout;
    final int mVersionCode;

    WakeLockEvent(int i, long j, int i2, String str, int i3, List<String> list, String str2, long j2, int i4, String str3, String str4, float f, long j3, String str5) {
        this.mVersionCode = i;
        this.Ga = j;
        this.Gb = i2;
        this.Gc = str;
        this.Gd = str3;
        this.Ge = str5;
        this.Gf = i3;
        this.Gm = -1;
        this.Gg = list;
        this.Gh = str2;
        this.Gi = j2;
        this.Gj = i4;
        this.Gk = str4;
        this.Gl = f;
        this.mTimeout = j3;
    }

    public WakeLockEvent(long j, int i, String str, int i2, List<String> list, String str2, long j2, int i3, String str3, String str4, float f, long j3, String str5) {
        this(2, j, i, str, i2, list, str2, j2, i3, str3, str4, f, j3, str5);
    }

    public int getEventType() {
        return this.Gb;
    }

    public long getTimeMillis() {
        return this.Ga;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzf.zza(this, parcel, i);
    }

    public long zzaxt() {
        return this.Gm;
    }

    public String zzaxu() {
        String valueOf = String.valueOf("\t");
        String valueOf2 = String.valueOf(zzaxv());
        String valueOf3 = String.valueOf("\t");
        int zzaxy = zzaxy();
        String valueOf4 = String.valueOf("\t");
        String join = zzaxz() == null ? "" : TextUtils.join(",", zzaxz());
        String valueOf5 = String.valueOf("\t");
        int zzayc = zzayc();
        String valueOf6 = String.valueOf("\t");
        String zzaxw = zzaxw() == null ? "" : zzaxw();
        String valueOf7 = String.valueOf("\t");
        String zzayd = zzayd() == null ? "" : zzayd();
        String valueOf8 = String.valueOf("\t");
        float zzaye = zzaye();
        String valueOf9 = String.valueOf("\t");
        String zzaxx = zzaxx() == null ? "" : zzaxx();
        return new StringBuilder(((((((((((((String.valueOf(valueOf).length() + 37) + String.valueOf(valueOf2).length()) + String.valueOf(valueOf3).length()) + String.valueOf(valueOf4).length()) + String.valueOf(join).length()) + String.valueOf(valueOf5).length()) + String.valueOf(valueOf6).length()) + String.valueOf(zzaxw).length()) + String.valueOf(valueOf7).length()) + String.valueOf(zzayd).length()) + String.valueOf(valueOf8).length()) + String.valueOf(valueOf9).length()) + String.valueOf(zzaxx).length()).append(valueOf).append(valueOf2).append(valueOf3).append(zzaxy).append(valueOf4).append(join).append(valueOf5).append(zzayc).append(valueOf6).append(zzaxw).append(valueOf7).append(zzayd).append(valueOf8).append(zzaye).append(valueOf9).append(zzaxx).toString();
    }

    public String zzaxv() {
        return this.Gc;
    }

    public String zzaxw() {
        return this.Gd;
    }

    public String zzaxx() {
        return this.Ge;
    }

    public int zzaxy() {
        return this.Gf;
    }

    public List<String> zzaxz() {
        return this.Gg;
    }

    public String zzaya() {
        return this.Gh;
    }

    public long zzayb() {
        return this.Gi;
    }

    public int zzayc() {
        return this.Gj;
    }

    public String zzayd() {
        return this.Gk;
    }

    public float zzaye() {
        return this.Gl;
    }

    public long zzayf() {
        return this.mTimeout;
    }
}
