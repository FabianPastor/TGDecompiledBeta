package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class PolygonOptions extends zza {
    public static final Creator<PolygonOptions> CREATOR = new zzi();
    private int mFillColor;
    private int mStrokeColor;
    private float mStrokeWidth;
    private final int mVersionCode;
    private float zzboJ;
    private boolean zzboK;
    private boolean zzboL;
    private final List<LatLng> zzbpk;
    private final List<List<LatLng>> zzbpl;
    private boolean zzbpm;

    public PolygonOptions() {
        this.mStrokeWidth = 10.0f;
        this.mStrokeColor = -16777216;
        this.mFillColor = 0;
        this.zzboJ = 0.0f;
        this.zzboK = true;
        this.zzbpm = false;
        this.zzboL = false;
        this.mVersionCode = 1;
        this.zzbpk = new ArrayList();
        this.zzbpl = new ArrayList();
    }

    PolygonOptions(int i, List<LatLng> list, List list2, float f, int i2, int i3, float f2, boolean z, boolean z2, boolean z3) {
        this.mStrokeWidth = 10.0f;
        this.mStrokeColor = -16777216;
        this.mFillColor = 0;
        this.zzboJ = 0.0f;
        this.zzboK = true;
        this.zzbpm = false;
        this.zzboL = false;
        this.mVersionCode = i;
        this.zzbpk = list;
        this.zzbpl = list2;
        this.mStrokeWidth = f;
        this.mStrokeColor = i2;
        this.mFillColor = i3;
        this.zzboJ = f2;
        this.zzboK = z;
        this.zzbpm = z2;
        this.zzboL = z3;
    }

    public PolygonOptions add(LatLng latLng) {
        this.zzbpk.add(latLng);
        return this;
    }

    public PolygonOptions add(LatLng... latLngArr) {
        this.zzbpk.addAll(Arrays.asList(latLngArr));
        return this;
    }

    public PolygonOptions addAll(Iterable<LatLng> iterable) {
        for (LatLng add : iterable) {
            this.zzbpk.add(add);
        }
        return this;
    }

    public PolygonOptions addHole(Iterable<LatLng> iterable) {
        ArrayList arrayList = new ArrayList();
        for (LatLng add : iterable) {
            arrayList.add(add);
        }
        this.zzbpl.add(arrayList);
        return this;
    }

    public PolygonOptions clickable(boolean z) {
        this.zzboL = z;
        return this;
    }

    public PolygonOptions fillColor(int i) {
        this.mFillColor = i;
        return this;
    }

    public PolygonOptions geodesic(boolean z) {
        this.zzbpm = z;
        return this;
    }

    public int getFillColor() {
        return this.mFillColor;
    }

    public List<List<LatLng>> getHoles() {
        return this.zzbpl;
    }

    public List<LatLng> getPoints() {
        return this.zzbpk;
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
        return this.zzboJ;
    }

    public boolean isClickable() {
        return this.zzboL;
    }

    public boolean isGeodesic() {
        return this.zzbpm;
    }

    public boolean isVisible() {
        return this.zzboK;
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
        this.zzboK = z;
        return this;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzi.zza(this, parcel, i);
    }

    public PolygonOptions zIndex(float f) {
        this.zzboJ = f;
        return this;
    }

    List zzIW() {
        return this.zzbpl;
    }
}
