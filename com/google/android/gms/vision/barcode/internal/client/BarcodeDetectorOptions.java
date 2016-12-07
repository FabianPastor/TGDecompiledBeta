package com.google.android.gms.vision.barcode.internal.client;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;

public class BarcodeDetectorOptions extends AbstractSafeParcelable {
    public static final Creator<BarcodeDetectorOptions> CREATOR = new zza();
    public int aNZ;
    final int versionCode;

    public BarcodeDetectorOptions() {
        this.versionCode = 1;
    }

    public BarcodeDetectorOptions(int i, int i2) {
        this.versionCode = i;
        this.aNZ = i2;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zza.zza(this, parcel, i);
    }
}
