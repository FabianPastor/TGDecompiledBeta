package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.vision.Frame;

public final class zzdjw extends zzbfm {
    public static final Creator<zzdjw> CREATOR = new zzdjx();
    public int height;
    private int id;
    public int rotation;
    public int width;
    private long zzikn;

    public zzdjw(int i, int i2, int i3, long j, int i4) {
        this.width = i;
        this.height = i2;
        this.id = i3;
        this.zzikn = j;
        this.rotation = i4;
    }

    public static zzdjw zzc(Frame frame) {
        zzdjw com_google_android_gms_internal_zzdjw = new zzdjw();
        com_google_android_gms_internal_zzdjw.width = frame.getMetadata().getWidth();
        com_google_android_gms_internal_zzdjw.height = frame.getMetadata().getHeight();
        com_google_android_gms_internal_zzdjw.rotation = frame.getMetadata().getRotation();
        com_google_android_gms_internal_zzdjw.id = frame.getMetadata().getId();
        com_google_android_gms_internal_zzdjw.zzikn = frame.getMetadata().getTimestampMillis();
        return com_google_android_gms_internal_zzdjw;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zzc(parcel, 2, this.width);
        zzbfp.zzc(parcel, 3, this.height);
        zzbfp.zzc(parcel, 4, this.id);
        zzbfp.zza(parcel, 5, this.zzikn);
        zzbfp.zzc(parcel, 6, this.rotation);
        zzbfp.zzai(parcel, zze);
    }
}
