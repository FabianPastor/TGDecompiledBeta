package com.google.android.gms.vision.face.internal.client;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;

public class FaceSettingsParcel extends AbstractSafeParcelable {
    public static final Creator<FaceSettingsParcel> CREATOR = new zzc();
    public boolean aOA;
    public float aOB;
    public int aOx;
    public int aOy;
    public boolean aOz;
    public int mode;
    public final int versionCode;

    public FaceSettingsParcel() {
        this.versionCode = 2;
    }

    public FaceSettingsParcel(int i, int i2, int i3, int i4, boolean z, boolean z2, float f) {
        this.versionCode = i;
        this.mode = i2;
        this.aOx = i3;
        this.aOy = i4;
        this.aOz = z;
        this.aOA = z2;
        this.aOB = f;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzc.zza(this, parcel, i);
    }
}
