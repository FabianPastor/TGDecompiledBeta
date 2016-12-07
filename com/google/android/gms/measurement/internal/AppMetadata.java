package com.google.android.gms.measurement.internal;

import android.os.Parcel;
import android.text.TextUtils;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.zzac;

public class AppMetadata extends AbstractSafeParcelable {
    public static final zzb CREATOR = new zzb();
    public final String afY;
    public final String anQ;
    public final String anR;
    public final long anS;
    public final long anT;
    public final String anU;
    public final boolean anV;
    public final boolean anW;
    public final long anX;
    public final String anY;
    public final String packageName;
    public final int versionCode;

    AppMetadata(int i, String str, String str2, String str3, String str4, long j, long j2, String str5, boolean z, boolean z2, long j3, String str6) {
        this.versionCode = i;
        this.packageName = str;
        this.anQ = str2;
        this.afY = str3;
        if (i < 5) {
            j3 = -2147483648L;
        }
        this.anX = j3;
        this.anR = str4;
        this.anS = j;
        this.anT = j2;
        this.anU = str5;
        if (i >= 3) {
            this.anV = z;
        } else {
            this.anV = true;
        }
        this.anW = z2;
        this.anY = str6;
    }

    AppMetadata(String str, String str2, String str3, long j, String str4, long j2, long j3, String str5, boolean z, boolean z2, String str6) {
        zzac.zzhz(str);
        this.versionCode = 6;
        this.packageName = str;
        if (TextUtils.isEmpty(str2)) {
            str2 = null;
        }
        this.anQ = str2;
        this.afY = str3;
        this.anX = j;
        this.anR = str4;
        this.anS = j2;
        this.anT = j3;
        this.anU = str5;
        this.anV = z;
        this.anW = z2;
        this.anY = str6;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzb.zza(this, parcel, i);
    }
}
