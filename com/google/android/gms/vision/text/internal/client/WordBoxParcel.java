package com.google.android.gms.vision.text.internal.client;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;

public class WordBoxParcel extends AbstractSafeParcelable {
    public static final zzi CREATOR = new zzi();
    public final BoundingBoxParcel aLG;
    public final BoundingBoxParcel aLH;
    public final String aLJ;
    public final float aLK;
    public final SymbolBoxParcel[] aLQ;
    public final boolean aLR;
    public final String aLz;
    public final int versionCode;

    public WordBoxParcel(int i, SymbolBoxParcel[] symbolBoxParcelArr, BoundingBoxParcel boundingBoxParcel, BoundingBoxParcel boundingBoxParcel2, String str, float f, String str2, boolean z) {
        this.versionCode = i;
        this.aLQ = symbolBoxParcelArr;
        this.aLG = boundingBoxParcel;
        this.aLH = boundingBoxParcel2;
        this.aLJ = str;
        this.aLK = f;
        this.aLz = str2;
        this.aLR = z;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzi.zza(this, parcel, i);
    }
}
