package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.vision.Frame;

public class zzbka extends zza {
    public static final Creator<zzbka> CREATOR = new zzbkb();
    public int height;
    public int id;
    public int rotation;
    final int versionCode;
    public int width;
    public long zzbPo;

    public zzbka() {
        this.versionCode = 1;
    }

    public zzbka(int i, int i2, int i3, int i4, long j, int i5) {
        this.versionCode = i;
        this.width = i2;
        this.height = i3;
        this.id = i4;
        this.zzbPo = j;
        this.rotation = i5;
    }

    public static zzbka zzc(Frame frame) {
        zzbka com_google_android_gms_internal_zzbka = new zzbka();
        com_google_android_gms_internal_zzbka.width = frame.getMetadata().getWidth();
        com_google_android_gms_internal_zzbka.height = frame.getMetadata().getHeight();
        com_google_android_gms_internal_zzbka.rotation = frame.getMetadata().getRotation();
        com_google_android_gms_internal_zzbka.id = frame.getMetadata().getId();
        com_google_android_gms_internal_zzbka.zzbPo = frame.getMetadata().getTimestampMillis();
        return com_google_android_gms_internal_zzbka;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzbkb.zza(this, parcel, i);
    }
}
