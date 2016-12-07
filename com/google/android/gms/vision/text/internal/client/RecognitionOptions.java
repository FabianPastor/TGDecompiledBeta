package com.google.android.gms.vision.text.internal.client;

import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;

public class RecognitionOptions extends AbstractSafeParcelable {
    public static final Creator<RecognitionOptions> CREATOR = new zze();
    public final Rect aPa;
    final int versionCode;

    public RecognitionOptions() {
        this.versionCode = 1;
        this.aPa = new Rect();
    }

    public RecognitionOptions(int i, Rect rect) {
        this.versionCode = i;
        this.aPa = rect;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zze.zza(this, parcel, i);
    }
}
