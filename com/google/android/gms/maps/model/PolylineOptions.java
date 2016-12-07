package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class PolylineOptions extends AbstractSafeParcelable {
    public static final Creator<PolylineOptions> CREATOR = new zzj();
    private float apJ;
    private boolean apK;
    private boolean apL;
    private float apP;
    private final List<LatLng> aqk;
    private boolean aqm;
    private int mColor;
    private final int mVersionCode;

    public PolylineOptions() {
        this.apP = 10.0f;
        this.mColor = -16777216;
        this.apJ = 0.0f;
        this.apK = true;
        this.aqm = false;
        this.apL = false;
        this.mVersionCode = 1;
        this.aqk = new ArrayList();
    }

    PolylineOptions(int i, List list, float f, int i2, float f2, boolean z, boolean z2, boolean z3) {
        this.apP = 10.0f;
        this.mColor = -16777216;
        this.apJ = 0.0f;
        this.apK = true;
        this.aqm = false;
        this.apL = false;
        this.mVersionCode = i;
        this.aqk = list;
        this.apP = f;
        this.mColor = i2;
        this.apJ = f2;
        this.apK = z;
        this.aqm = z2;
        this.apL = z3;
    }

    public PolylineOptions add(LatLng latLng) {
        this.aqk.add(latLng);
        return this;
    }

    public PolylineOptions add(LatLng... latLngArr) {
        this.aqk.addAll(Arrays.asList(latLngArr));
        return this;
    }

    public PolylineOptions addAll(Iterable<LatLng> iterable) {
        for (LatLng add : iterable) {
            this.aqk.add(add);
        }
        return this;
    }

    public PolylineOptions clickable(boolean z) {
        this.apL = z;
        return this;
    }

    public PolylineOptions color(int i) {
        this.mColor = i;
        return this;
    }

    public PolylineOptions geodesic(boolean z) {
        this.aqm = z;
        return this;
    }

    public int getColor() {
        return this.mColor;
    }

    public List<LatLng> getPoints() {
        return this.aqk;
    }

    int getVersionCode() {
        return this.mVersionCode;
    }

    public float getWidth() {
        return this.apP;
    }

    public float getZIndex() {
        return this.apJ;
    }

    public boolean isClickable() {
        return this.apL;
    }

    public boolean isGeodesic() {
        return this.aqm;
    }

    public boolean isVisible() {
        return this.apK;
    }

    public PolylineOptions visible(boolean z) {
        this.apK = z;
        return this;
    }

    public PolylineOptions width(float f) {
        this.apP = f;
        return this;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzj.zza(this, parcel, i);
    }

    public PolylineOptions zIndex(float f) {
        this.apJ = f;
        return this;
    }
}
