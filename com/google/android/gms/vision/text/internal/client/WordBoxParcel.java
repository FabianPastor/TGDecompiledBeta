package com.google.android.gms.vision.text.internal.client;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;

public class WordBoxParcel extends AbstractSafeParcelable {
    public static final Creator<WordBoxParcel> CREATOR = new zzi();
    public final String aOK;
    public final BoundingBoxParcel aOR;
    public final BoundingBoxParcel aOS;
    public final String aOU;
    public final float aOV;
    public final SymbolBoxParcel[] aPb;
    public final boolean aPc;
    public final int versionCode;

    public WordBoxParcel(int i, SymbolBoxParcel[] symbolBoxParcelArr, BoundingBoxParcel boundingBoxParcel, BoundingBoxParcel boundingBoxParcel2, String str, float f, String str2, boolean z) {
        this.versionCode = i;
        this.aPb = symbolBoxParcelArr;
        this.aOR = boundingBoxParcel;
        this.aOS = boundingBoxParcel2;
        this.aOU = str;
        this.aOV = f;
        this.aOK = str2;
        this.aPc = z;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzi.zza(this, parcel, i);
    }
}
