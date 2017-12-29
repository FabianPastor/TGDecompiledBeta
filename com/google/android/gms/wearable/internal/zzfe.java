package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;
import com.google.android.gms.wearable.MessageEvent;

public final class zzfe extends zzbfm implements MessageEvent {
    public static final Creator<zzfe> CREATOR = new zzff();
    private final String mPath;
    private final String zzdrc;
    private final int zzgiq;
    private final byte[] zzhyw;

    public zzfe(int i, String str, byte[] bArr, String str2) {
        this.zzgiq = i;
        this.mPath = str;
        this.zzhyw = bArr;
        this.zzdrc = str2;
    }

    public final byte[] getData() {
        return this.zzhyw;
    }

    public final String getPath() {
        return this.mPath;
    }

    public final int getRequestId() {
        return this.zzgiq;
    }

    public final String getSourceNodeId() {
        return this.zzdrc;
    }

    public final String toString() {
        int i = this.zzgiq;
        String str = this.mPath;
        String valueOf = String.valueOf(this.zzhyw == null ? "null" : Integer.valueOf(this.zzhyw.length));
        return new StringBuilder((String.valueOf(str).length() + 43) + String.valueOf(valueOf).length()).append("MessageEventParcelable[").append(i).append(",").append(str).append(", size=").append(valueOf).append("]").toString();
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zzc(parcel, 2, getRequestId());
        zzbfp.zza(parcel, 3, getPath(), false);
        zzbfp.zza(parcel, 4, getData(), false);
        zzbfp.zza(parcel, 5, getSourceNodeId(), false);
        zzbfp.zzai(parcel, zze);
    }
}
