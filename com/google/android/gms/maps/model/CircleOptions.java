package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public final class CircleOptions extends zza {
    public static final Creator<CircleOptions> CREATOR = new zzb();
    private int mFillColor;
    private int mStrokeColor;
    private float mStrokeWidth;
    private final int mVersionCode;
    private LatLng zzboH;
    private double zzboI;
    private float zzboJ;
    private boolean zzboK;
    private boolean zzboL;

    public CircleOptions() {
        this.zzboH = null;
        this.zzboI = 0.0d;
        this.mStrokeWidth = 10.0f;
        this.mStrokeColor = -16777216;
        this.mFillColor = 0;
        this.zzboJ = 0.0f;
        this.zzboK = true;
        this.zzboL = false;
        this.mVersionCode = 1;
    }

    CircleOptions(int i, LatLng latLng, double d, float f, int i2, int i3, float f2, boolean z, boolean z2) {
        this.zzboH = null;
        this.zzboI = 0.0d;
        this.mStrokeWidth = 10.0f;
        this.mStrokeColor = -16777216;
        this.mFillColor = 0;
        this.zzboJ = 0.0f;
        this.zzboK = true;
        this.zzboL = false;
        this.mVersionCode = i;
        this.zzboH = latLng;
        this.zzboI = d;
        this.mStrokeWidth = f;
        this.mStrokeColor = i2;
        this.mFillColor = i3;
        this.zzboJ = f2;
        this.zzboK = z;
        this.zzboL = z2;
    }

    public CircleOptions center(LatLng latLng) {
        this.zzboH = latLng;
        return this;
    }

    public CircleOptions clickable(boolean z) {
        this.zzboL = z;
        return this;
    }

    public CircleOptions fillColor(int i) {
        this.mFillColor = i;
        return this;
    }

    public LatLng getCenter() {
        return this.zzboH;
    }

    public int getFillColor() {
        return this.mFillColor;
    }

    public double getRadius() {
        return this.zzboI;
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

    public boolean isVisible() {
        return this.zzboK;
    }

    public CircleOptions radius(double d) {
        this.zzboI = d;
        return this;
    }

    public CircleOptions strokeColor(int i) {
        this.mStrokeColor = i;
        return this;
    }

    public CircleOptions strokeWidth(float f) {
        this.mStrokeWidth = f;
        return this;
    }

    public CircleOptions visible(boolean z) {
        this.zzboK = z;
        return this;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzb.zza(this, parcel, i);
    }

    public CircleOptions zIndex(float f) {
        this.zzboJ = f;
        return this;
    }
}
