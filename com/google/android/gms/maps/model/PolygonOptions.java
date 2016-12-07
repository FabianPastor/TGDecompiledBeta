package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class PolygonOptions extends AbstractSafeParcelable {
    public static final Creator<PolygonOptions> CREATOR = new zzi();
    private float apJ;
    private boolean apK;
    private boolean apL;
    private final List<LatLng> aqk;
    private final List<List<LatLng>> aql;
    private boolean aqm;
    private int mFillColor;
    private int mStrokeColor;
    private float mStrokeWidth;
    private final int mVersionCode;

    public PolygonOptions() {
        this.mStrokeWidth = 10.0f;
        this.mStrokeColor = -16777216;
        this.mFillColor = 0;
        this.apJ = 0.0f;
        this.apK = true;
        this.aqm = false;
        this.apL = false;
        this.mVersionCode = 1;
        this.aqk = new ArrayList();
        this.aql = new ArrayList();
    }

    PolygonOptions(int i, List<LatLng> list, List list2, float f, int i2, int i3, float f2, boolean z, boolean z2, boolean z3) {
        this.mStrokeWidth = 10.0f;
        this.mStrokeColor = -16777216;
        this.mFillColor = 0;
        this.apJ = 0.0f;
        this.apK = true;
        this.aqm = false;
        this.apL = false;
        this.mVersionCode = i;
        this.aqk = list;
        this.aql = list2;
        this.mStrokeWidth = f;
        this.mStrokeColor = i2;
        this.mFillColor = i3;
        this.apJ = f2;
        this.apK = z;
        this.aqm = z2;
        this.apL = z3;
    }

    public PolygonOptions add(LatLng latLng) {
        this.aqk.add(latLng);
        return this;
    }

    public PolygonOptions add(LatLng... latLngArr) {
        this.aqk.addAll(Arrays.asList(latLngArr));
        return this;
    }

    public PolygonOptions addAll(Iterable<LatLng> iterable) {
        for (LatLng add : iterable) {
            this.aqk.add(add);
        }
        return this;
    }

    public PolygonOptions addHole(Iterable<LatLng> iterable) {
        ArrayList arrayList = new ArrayList();
        for (LatLng add : iterable) {
            arrayList.add(add);
        }
        this.aql.add(arrayList);
        return this;
    }

    public PolygonOptions clickable(boolean z) {
        this.apL = z;
        return this;
    }

    public PolygonOptions fillColor(int i) {
        this.mFillColor = i;
        return this;
    }

    public PolygonOptions geodesic(boolean z) {
        this.aqm = z;
        return this;
    }

    public int getFillColor() {
        return this.mFillColor;
    }

    public List<List<LatLng>> getHoles() {
        return this.aql;
    }

    public List<LatLng> getPoints() {
        return this.aqk;
    }

    public int getStrokeColor() {
        return this.mStrokeColor;
    }

    public float getStrokeWidth() {
        return this.mStrokeWidth;
    }

    int getVersionCode() {
        return this.mVersionCode;
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

    public PolygonOptions strokeColor(int i) {
        this.mStrokeColor = i;
        return this;
    }

    public PolygonOptions strokeWidth(float f) {
        this.mStrokeWidth = f;
        return this;
    }

    public PolygonOptions visible(boolean z) {
        this.apK = z;
        return this;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzi.zza(this, parcel, i);
    }

    public PolygonOptions zIndex(float f) {
        this.apJ = f;
        return this;
    }

    List zzbta() {
        return this.aql;
    }
}
