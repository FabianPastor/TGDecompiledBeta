package com.google.android.gms.vision.text.internal.client;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;

public class TextRecognizerOptions extends AbstractSafeParcelable {
    public static final zzh CREATOR = new zzh();
    final int versionCode;

    public TextRecognizerOptions() {
        this.versionCode = 1;
    }

    public TextRecognizerOptions(int i) {
        this.versionCode = i;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzh.zza(this, parcel, i);
    }
}
