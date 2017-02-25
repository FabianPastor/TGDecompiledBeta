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
    private final List<LatLng> zzbpP;
    private final List<List<LatLng>> zzbpQ;
    private boolean zzbpR;
    private int zzbpS;
    private float zzbpm;
    private boolean zzbpn;
    private boolean zzbpo;
    @Nullable
    private List<PatternItem> zzbpp;

    public PolygonOptions() {
        this.mStrokeWidth = 10.0f;
        this.mStrokeColor = -16777216;
        this.mFillColor = 0;
        this.zzbpm = 0.0f;
        this.zzbpn = true;
        this.zzbpR = false;
        this.zzbpo = false;
        this.zzbpS = 0;
        this.zzbpp = null;
        this.zzbpP = new ArrayList();
        this.zzbpQ = new ArrayList();
    }

    PolygonOptions(List<LatLng> list, List list2, float f, int i, int i2, float f2, boolean z, boolean z2, boolean z3, int i3, @Nullable List<PatternItem> list3) {
        this.mStrokeWidth = 10.0f;
        this.mStrokeColor = -16777216;
        this.mFillColor = 0;
        this.zzbpm = 0.0f;
        this.zzbpn = true;
        this.zzbpR = false;
        this.zzbpo = false;
        this.zzbpS = 0;
        this.zzbpp = null;
        this.zzbpP = list;
        this.zzbpQ = list2;
        this.mStrokeWidth = f;
        this.mStrokeColor = i;
        this.mFillColor = i2;
        this.zzbpm = f2;
        this.zzbpn = z;
        this.zzbpR = z2;
        this.zzbpo = z3;
        this.zzbpS = i3;
        this.zzbpp = list3;
    }

    public PolygonOptions add(LatLng latLng) {
        this.zzbpP.add(latLng);
        return this;
    }

    public PolygonOptions add(LatLng... latLngArr) {
        this.zzbpP.addAll(Arrays.asList(latLngArr));
        return this;
    }

    public PolygonOptions addAll(Iterable<LatLng> iterable) {
        for (LatLng add : iterable) {
            this.zzbpP.add(add);
        }
        return this;
    }

    public PolygonOptions addHole(Iterable<LatLng> iterable) {
        ArrayList arrayList = new ArrayList();
        for (LatLng add : iterable) {
            arrayList.add(add);
        }
        this.zzbpQ.add(arrayList);
        return this;
    }

    public PolygonOptions clickable(boolean z) {
        this.zzbpo = z;
        return this;
    }

    public PolygonOptions fillColor(int i) {
        this.mFillColor = i;
        return this;
    }

    public PolygonOptions geodesic(boolean z) {
        this.zzbpR = z;
        return this;
    }

    public int getFillColor() {
        return this.mFillColor;
    }

    public List<List<LatLng>> getHoles() {
        return this.zzbpQ;
    }

    public List<LatLng> getPoints() {
        return this.zzbpP;
    }

    public int getStrokeColor() {
        return this.mStrokeColor;
    }

    public int getStrokeJointType() {
        return this.zzbpS;
    }

    @Nullable
    public List<PatternItem> getStrokePattern() {
        return this.zzbpp;
    }

    public float getStrokeWidth() {
        return this.mStrokeWidth;
    }

    public float getZIndex() {
        return this.zzbpm;
    }

    public boolean isClickable() {
        return this.zzbpo;
    }

    public boolean isGeodesic() {
        return this.zzbpR;
    }

    public boolean isVisible() {
        return this.zzbpn;
    }

    public PolygonOptions strokeColor(int i) {
        this.mStrokeColor = i;
        return this;
    }

    public PolygonOptions strokeJointType(int i) {
        this.zzbpS = i;
        return this;
    }

    public PolygonOptions strokePattern(@Nullable List<PatternItem> list) {
        this.zzbpp = list;
        return this;
    }

    public PolygonOptions strokeWidth(float f) {
        this.mStrokeWidth = f;
        return this;
    }

    public PolygonOptions visible(boolean z) {
        this.zzbpn = z;
        return this;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzk.zza(this, parcel, i);
    }

    public PolygonOptions zIndex(float f) {
        this.zzbpm = f;
        return this;
    }

    List zzJO() {
        return this.zzbpQ;
    }
}
