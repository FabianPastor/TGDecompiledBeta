package com.google.android.gms.maps.model;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class PolygonOptions extends AbstractSafeParcelable {
    public static final zzi CREATOR = new zzi();
    private float amD;
    private boolean amE;
    private boolean amF;
    private final List<LatLng> ane;
    private final List<List<LatLng>> anf;
    private boolean ang;
    private int mFillColor;
    private int mStrokeColor;
    private float mStrokeWidth;
    private final int mVersionCode;

    public PolygonOptions() {
        this.mStrokeWidth = 10.0f;
        this.mStrokeColor = -16777216;
        this.mFillColor = 0;
        this.amD = 0.0f;
        this.amE = true;
        this.ang = false;
        this.amF = false;
        this.mVersionCode = 1;
        this.ane = new ArrayList();
        this.anf = new ArrayList();
    }

    PolygonOptions(int i, List<LatLng> list, List list2, float f, int i2, int i3, float f2, boolean z, boolean z2, boolean z3) {
        this.mStrokeWidth = 10.0f;
        this.mStrokeColor = -16777216;
        this.mFillColor = 0;
        this.amD = 0.0f;
        this.amE = true;
        this.ang = false;
        this.amF = false;
        this.mVersionCode = i;
        this.ane = list;
        this.anf = list2;
        this.mStrokeWidth = f;
        this.mStrokeColor = i2;
        this.mFillColor = i3;
        this.amD = f2;
        this.amE = z;
        this.ang = z2;
        this.amF = z3;
    }

    public PolygonOptions add(LatLng latLng) {
        this.ane.add(latLng);
        return this;
    }

    public PolygonOptions add(LatLng... latLngArr) {
        this.ane.addAll(Arrays.asList(latLngArr));
        return this;
    }

    public PolygonOptions addAll(Iterable<LatLng> iterable) {
        for (LatLng add : iterable) {
            this.ane.add(add);
        }
        return this;
    }

    public PolygonOptions addHole(Iterable<LatLng> iterable) {
        ArrayList arrayList = new ArrayList();
        for (LatLng add : iterable) {
            arrayList.add(add);
        }
        this.anf.add(arrayList);
        return this;
    }

    public PolygonOptions clickable(boolean z) {
        this.amF = z;
        return this;
    }

    public PolygonOptions fillColor(int i) {
        this.mFillColor = i;
        return this;
    }

    public PolygonOptions geodesic(boolean z) {
        this.ang = z;
        return this;
    }

    public int getFillColor() {
        return this.mFillColor;
    }

    public List<List<LatLng>> getHoles() {
        return this.anf;
    }

    public List<LatLng> getPoints() {
        return this.ane;
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
        return this.amD;
    }

    public boolean isClickable() {
        return this.amF;
    }

    public boolean isGeodesic() {
        return this.ang;
    }

    public boolean isVisible() {
        return this.amE;
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
        this.amE = z;
        return this;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzi.zza(this, parcel, i);
    }

    public PolygonOptions zIndex(float f) {
        this.amD = f;
        return this;
    }

    List zzbsk() {
        return this.anf;
    }
}
