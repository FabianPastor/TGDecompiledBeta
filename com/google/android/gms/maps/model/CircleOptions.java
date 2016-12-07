package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;

public final class CircleOptions extends AbstractSafeParcelable {
    public static final Creator<CircleOptions> CREATOR = new zzb();
    private LatLng apH;
    private double apI;
    private float apJ;
    private boolean apK;
    private boolean apL;
    private int mFillColor;
    private int mStrokeColor;
    private float mStrokeWidth;
    private final int mVersionCode;

    public CircleOptions() {
        this.apH = null;
        this.apI = 0.0d;
        this.mStrokeWidth = 10.0f;
        this.mStrokeColor = -16777216;
        this.mFillColor = 0;
        this.apJ = 0.0f;
        this.apK = true;
        this.apL = false;
        this.mVersionCode = 1;
    }

    CircleOptions(int i, LatLng latLng, double d, float f, int i2, int i3, float f2, boolean z, boolean z2) {
        this.apH = null;
        this.apI = 0.0d;
        this.mStrokeWidth = 10.0f;
        this.mStrokeColor = -16777216;
        this.mFillColor = 0;
        this.apJ = 0.0f;
        this.apK = true;
        this.apL = false;
        this.mVersionCode = i;
        this.apH = latLng;
        this.apI = d;
        this.mStrokeWidth = f;
        this.mStrokeColor = i2;
        this.mFillColor = i3;
        this.apJ = f2;
        this.apK = z;
        this.apL = z2;
    }

    public CircleOptions center(LatLng latLng) {
        this.apH = latLng;
        return this;
    }

    public CircleOptions clickable(boolean z) {
        this.apL = z;
        return this;
    }

    public CircleOptions fillColor(int i) {
        this.mFillColor = i;
        return this;
    }

    public LatLng getCenter() {
        return this.apH;
    }

    public int getFillColor() {
        return this.mFillColor;
    }

    public double getRadius() {
        return this.apI;
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

    public boolean isVisible() {
        return this.apK;
    }

    public CircleOptions radius(double d) {
        this.apI = d;
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
        this.apK = z;
        return this;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzb.zza(this, parcel, i);
    }

    public CircleOptions zIndex(float f) {
        this.apJ = f;
        return this;
    }
}
