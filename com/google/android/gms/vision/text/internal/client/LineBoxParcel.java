package com.google.android.gms.vision.text.internal.client;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;

public class LineBoxParcel extends AbstractSafeParcelable {
    public static final zzd CREATOR = new zzd();
    public final WordBoxParcel[] aLF;
    public final BoundingBoxParcel aLG;
    public final BoundingBoxParcel aLH;
    public final BoundingBoxParcel aLI;
    public final String aLJ;
    public final float aLK;
    public final int aLL;
    public final boolean aLM;
    public final int aLN;
    public final int aLO;
    public final String aLz;
    public final int versionCode;

    public LineBoxParcel(int i, WordBoxParcel[] wordBoxParcelArr, BoundingBoxParcel boundingBoxParcel, BoundingBoxParcel boundingBoxParcel2, BoundingBoxParcel boundingBoxParcel3, String str, float f, String str2, int i2, boolean z, int i3, int i4) {
        this.versionCode = i;
        this.aLF = wordBoxParcelArr;
        this.aLG = boundingBoxParcel;
        this.aLH = boundingBoxParcel2;
        this.aLI = boundingBoxParcel3;
        this.aLJ = str;
        this.aLK = f;
        this.aLz = str2;
        this.aLL = i2;
        this.aLM = z;
        this.aLN = i3;
        this.aLO = i4;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzd.zza(this, parcel, i);
    }
}
