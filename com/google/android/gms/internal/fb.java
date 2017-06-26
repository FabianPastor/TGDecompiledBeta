package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.vision.Frame;

public final class fb extends zza {
    public static final Creator<fb> CREATOR = new fc();
    public int height;
    private int id;
    public int rotation;
    public int width;
    private long zzbiv;

    public fb(int i, int i2, int i3, long j, int i4) {
        this.width = i;
        this.height = i2;
        this.id = i3;
        this.zzbiv = j;
        this.rotation = i4;
    }

    public static fb zzc(Frame frame) {
        fb fbVar = new fb();
        fbVar.width = frame.getMetadata().getWidth();
        fbVar.height = frame.getMetadata().getHeight();
        fbVar.rotation = frame.getMetadata().getRotation();
        fbVar.id = frame.getMetadata().getId();
        fbVar.zzbiv = frame.getMetadata().getTimestampMillis();
        return fbVar;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zzc(parcel, 2, this.width);
        zzd.zzc(parcel, 3, this.height);
        zzd.zzc(parcel, 4, this.id);
        zzd.zza(parcel, 5, this.zzbiv);
        zzd.zzc(parcel, 6, this.rotation);
        zzd.zzI(parcel, zze);
    }
}
