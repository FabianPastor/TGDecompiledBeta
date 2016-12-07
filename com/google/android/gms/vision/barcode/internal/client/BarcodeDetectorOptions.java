package com.google.android.gms.vision.barcode.internal.client;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;

public class BarcodeDetectorOptions extends AbstractSafeParcelable {
    public static final zza CREATOR = new zza();
    public int aKO;
    final int versionCode;

    public BarcodeDetectorOptions() {
        this.versionCode = 1;
    }

    public BarcodeDetectorOptions(int i, int i2) {
        this.versionCode = i;
        this.aKO = i2;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zza com_google_android_gms_vision_barcode_internal_client_zza = CREATOR;
        zza.zza(this, parcel, i);
    }
}
