package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public final class zzatb extends zza {
    public static final Creator<zzatb> CREATOR = new zzatc();
    public final String name;
    public final int versionCode;
    public final zzasz zzbqP;
    public final String zzbqQ;
    public final long zzbqR;

    zzatb(int i, String str, zzasz com_google_android_gms_internal_zzasz, String str2, long j) {
        this.versionCode = i;
        this.name = str;
        this.zzbqP = com_google_android_gms_internal_zzasz;
        this.zzbqQ = str2;
        this.zzbqR = j;
    }

    public zzatb(String str, zzasz com_google_android_gms_internal_zzasz, String str2, long j) {
        this.versionCode = 1;
        this.name = str;
        this.zzbqP = com_google_android_gms_internal_zzasz;
        this.zzbqQ = str2;
        this.zzbqR = j;
    }

    public String toString() {
        String str = this.zzbqQ;
        String str2 = this.name;
        String valueOf = String.valueOf(this.zzbqP);
        return new StringBuilder(((String.valueOf(str).length() + 21) + String.valueOf(str2).length()) + String.valueOf(valueOf).length()).append("origin=").append(str).append(",name=").append(str2).append(",params=").append(valueOf).toString();
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzatc.zza(this, parcel, i);
    }
}
