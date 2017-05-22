package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.Nullable;
import com.google.android.gms.common.internal.safeparcel.zza;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class PolygonOptions extends zza {
    public static final Creator<PolygonOptions> CREATOR = new zzk();
    private int mFillColor;
    private int mStrokeColor;
    private float mStrokeWidth;
    private final List<LatLng> zzbpK;
    private final List<List<LatLng>> zzbpL;
    private boolean zzbpM;
    private int zzbpN;
    private float zzbph;
    private boolean zzbpi;
    private boolean zzbpj;
    @Nullable
    private List<PatternItem> zzbpk;

    public PolygonOptions() {
        this.mStrokeWidth = 10.0f;
        this.mStrokeColor = -16777216;
        this.mFillColor = 0;
        this.zzbph = 0.0f;
        this.zzbpi = true;
        this.zzbpM = false;
        this.zzbpj = false;
        this.zzbpN = 0;
        this.zzbpk = null;
        this.zzbpK = new ArrayList();
        this.zzbpL = new ArrayList();
    }

    PolygonOptions(List<LatLng> list, List list2, float f, int i, int i2, float f2, boolean z, boolean z2, boolean z3, int i3, @Nullable List<PatternItem> list3) {
        this.mStrokeWidth = 10.0f;
        this.mStrokeColor = -16777216;
        this.mFillColor = 0;
        this.zzbph = 0.0f;
        this.zzbpi = true;
        this.zzbpM = false;
        this.zzbpj = false;
        this.zzbpN = 0;
        this.zzbpk = null;
        this.zzbpK = list;
        this.zzbpL = list2;
        this.mStrokeWidth = f;
        this.mStrokeColor = i;
        this.mFillColor = i2;
        this.zzbph = f2;
        this.zzbpi = z;
        this.zzbpM = z2;
        this.zzbpj = z3;
        this.zzbpN = i3;
        this.zzbpk = list3;
    }

    public PolygonOptions add(LatLng latLng) {
        this.zzbpK.add(latLng);
        return this;
    }

    public PolygonOptions add(LatLng... latLngArr) {
        this.zzbpK.addAll(Arrays.asList(latLngArr));
        return this;
    }

    public PolygonOptions addAll(Iterable<LatLng> iterable) {
        for (LatLng add : iterable) {
            this.zzbpK.add(add);
        }
        return this;
    }

    public PolygonOptions addHole(Iterable<LatLng> iterable) {
        ArrayList arrayList = new ArrayList();
        for (LatLng add : iterable) {
            arrayList.add(add);
        }
        this.zzbpL.add(arrayList);
        return this;
    }

    public PolygonOptions clickable(boolean z) {
        this.zzbpj = z;
        return this;
    }

    public PolygonOptions fillColor(int i) {
        this.mFillColor = i;
        return this;
    }

    public PolygonOptions geodesic(boolean z) {
        this.zzbpM = z;
        return this;
    }

    public int getFillColor() {
        return this.mFillColor;
    }

    public List<List<LatLng>> getHoles() {
        return this.zzbpL;
    }

    public List<LatLng> getPoints() {
        return this.zzbpK;
    }

    public int getStrokeColor() {
        return this.mStrokeColor;
    }

    public int getStrokeJointType() {
        return this.zzbpN;
    }

    @Nullable
    public List<PatternItem> getStrokePattern() {
        return this.zzbpk;
    }

    public float getStrokeWidth() {
        return this.mStrokeWidth;
    }

    public float getZIndex() {
        return this.zzbph;
    }

    public boolean isClickable() {
        return this.zzbpj;
    }

    public boolean isGeodesic() {
        return this.zzbpM;
    }

    public boolean isVisible() {
        return this.zzbpi;
    }

    public PolygonOptions strokeColor(int i) {
        this.mStrokeColor = i;
        return this;
    }

    public PolygonOptions strokeJointType(int i) {
        this.zzbpN = i;
        return this;
    }

    public PolygonOptions strokePattern(@Nullable List<PatternItem> list) {
        this.zzbpk = list;
        return this;
    }

    public PolygonOptions strokeWidth(float f) {
        this.mStrokeWidth = f;
        return this;
    }

    public PolygonOptions visible(boolean z) {
        this.zzbpi = z;
        return this;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzk.zza(this, parcel, i);
    }

    public PolygonOptions zIndex(float f) {
        this.zzbph = f;
        return this;
    }

    List zzJP() {
        return this.zzbpL;
    }
}
