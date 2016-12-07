package com.google.android.gms.playlog.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.internal.zzz;

public class PlayLoggerContext extends AbstractSafeParcelable {
    public static final Creator<PlayLoggerContext> CREATOR = new zza();
    public final int aAB;
    public final int aAC;
    public final String aAD;
    public final String aAE;
    public final boolean aAF;
    public final String aAG;
    public final boolean aAH;
    public final int aAI;
    public final String packageName;
    public final int versionCode;

    public PlayLoggerContext(int i, String str, int i2, int i3, String str2, String str3, boolean z, String str4, boolean z2, int i4) {
        this.versionCode = i;
        this.packageName = str;
        this.aAB = i2;
        this.aAC = i3;
        this.aAD = str2;
        this.aAE = str3;
        this.aAF = z;
        this.aAG = str4;
        this.aAH = z2;
        this.aAI = i4;
    }

    public PlayLoggerContext(String str, int i, int i2, String str2, String str3, String str4, boolean z, int i3) {
        this.versionCode = 1;
        this.packageName = (String) zzaa.zzy(str);
        this.aAB = i;
        this.aAC = i2;
        this.aAG = str2;
        this.aAD = str3;
        this.aAE = str4;
        this.aAF = !z;
        this.aAH = z;
        this.aAI = i3;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof PlayLoggerContext)) {
            return false;
        }
        PlayLoggerContext playLoggerContext = (PlayLoggerContext) obj;
        return this.versionCode == playLoggerContext.versionCode && this.packageName.equals(playLoggerContext.packageName) && this.aAB == playLoggerContext.aAB && this.aAC == playLoggerContext.aAC && zzz.equal(this.aAG, playLoggerContext.aAG) && zzz.equal(this.aAD, playLoggerContext.aAD) && zzz.equal(this.aAE, playLoggerContext.aAE) && this.aAF == playLoggerContext.aAF && this.aAH == playLoggerContext.aAH && this.aAI == playLoggerContext.aAI;
    }

    public int hashCode() {
        return zzz.hashCode(Integer.valueOf(this.versionCode), this.packageName, Integer.valueOf(this.aAB), Integer.valueOf(this.aAC), this.aAG, this.aAD, this.aAE, Boolean.valueOf(this.aAF), Boolean.valueOf(this.aAH), Integer.valueOf(this.aAI));
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("PlayLoggerContext[");
        stringBuilder.append("versionCode=").append(this.versionCode).append(',');
        stringBuilder.append("package=").append(this.packageName).append(',');
        stringBuilder.append("packageVersionCode=").append(this.aAB).append(',');
        stringBuilder.append("logSource=").append(this.aAC).append(',');
        stringBuilder.append("logSourceName=").append(this.aAG).append(',');
        stringBuilder.append("uploadAccount=").append(this.aAD).append(',');
        stringBuilder.append("loggingId=").append(this.aAE).append(',');
        stringBuilder.append("logAndroidId=").append(this.aAF).append(',');
        stringBuilder.append("isAnonymous=").append(this.aAH).append(',');
        stringBuilder.append("qosTier=").append(this.aAI);
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    public void writeToParcel(Parcel parcel, int i) {
        zza.zza(this, parcel, i);
    }
}
