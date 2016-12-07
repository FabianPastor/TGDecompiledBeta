package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.vision.Frame;

public class zzbhd extends zza {
    public static final Creator<zzbhd> CREATOR = new zzbhe();
    public int height;
    public int id;
    public int rotation;
    final int versionCode;
    public int width;
    public long zzbNq;

    public zzbhd() {
        this.versionCode = 1;
    }

    public zzbhd(int i, int i2, int i3, int i4, long j, int i5) {
        this.versionCode = i;
        this.width = i2;
        this.height = i3;
        this.id = i4;
        this.zzbNq = j;
        this.rotation = i5;
    }

    public static zzbhd zzc(Frame frame) {
        zzbhd com_google_android_gms_internal_zzbhd = new zzbhd();
        com_google_android_gms_internal_zzbhd.width = frame.getMetadata().getWidth();
        com_google_android_gms_internal_zzbhd.height = frame.getMetadata().getHeight();
        com_google_android_gms_internal_zzbhd.rotation = frame.getMetadata().getRotation();
        com_google_android_gms_internal_zzbhd.id = frame.getMetadata().getId();
        com_google_android_gms_internal_zzbhd.zzbNq = frame.getMetadata().getTimestampMillis();
        return com_google_android_gms_internal_zzbhd;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzbhe.zza(this, parcel, i);
    }
}
