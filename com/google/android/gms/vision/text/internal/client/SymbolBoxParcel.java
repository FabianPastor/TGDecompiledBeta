package com.google.android.gms.vision.text.internal.client;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;

public class SymbolBoxParcel extends AbstractSafeParcelable {
    public static final Creator<SymbolBoxParcel> CREATOR = new zzf();
    public final int versionCode;

    public SymbolBoxParcel(int i) {
        this.versionCode = i;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzf.zza(this, parcel, i);
    }
}
