package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.wearable.MessageEvent;

public final class zzdx extends zza implements MessageEvent {
    public static final Creator<zzdx> CREATOR = new zzdy();
    private final String mPath;
    private final int zzaLT;
    private final String zzaeK;
    private final byte[] zzbdY;

    public zzdx(int i, String str, byte[] bArr, String str2) {
        this.zzaLT = i;
        this.mPath = str;
        this.zzbdY = bArr;
        this.zzaeK = str2;
    }

    public final byte[] getData() {
        return this.zzbdY;
    }

    public final String getPath() {
        return this.mPath;
    }

    public final int getRequestId() {
        return this.zzaLT;
    }

    public final String getSourceNodeId() {
        return this.zzaeK;
    }

    public final String toString() {
        int i = this.zzaLT;
        String str = this.mPath;
        String valueOf = String.valueOf(this.zzbdY == null ? "null" : Integer.valueOf(this.zzbdY.length));
        return new StringBuilder((String.valueOf(str).length() + 43) + String.valueOf(valueOf).length()).append("MessageEventParcelable[").append(i).append(",").append(str).append(", size=").append(valueOf).append("]").toString();
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zzc(parcel, 2, getRequestId());
        zzd.zza(parcel, 3, getPath(), false);
        zzd.zza(parcel, 4, getData(), false);
        zzd.zza(parcel, 5, getSourceNodeId(), false);
        zzd.zzI(parcel, zze);
    }
}
