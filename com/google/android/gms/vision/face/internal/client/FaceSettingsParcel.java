package com.google.android.gms.vision.face.internal.client;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;

public class FaceSettingsParcel extends AbstractSafeParcelable {
    public static final zzc CREATOR = new zzc();
    public int aLm;
    public int aLn;
    public boolean aLo;
    public boolean aLp;
    public float aLq;
    public int mode;
    public final int versionCode;

    public FaceSettingsParcel() {
        this.versionCode = 2;
    }

    public FaceSettingsParcel(int i, int i2, int i3, int i4, boolean z, boolean z2, float f) {
        this.versionCode = i;
        this.mode = i2;
        this.aLm = i3;
        this.aLn = i4;
        this.aLo = z;
        this.aLp = z2;
        this.aLq = f;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzc.zza(this, parcel, i);
    }
}
