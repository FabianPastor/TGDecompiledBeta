package com.google.android.gms.maps.model;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;

public final class CircleOptions extends AbstractSafeParcelable {
    public static final zzb CREATOR = new zzb();
    private LatLng amB;
    private double amC;
    private float amD;
    private boolean amE;
    private boolean amF;
    private int mFillColor;
    private int mStrokeColor;
    private float mStrokeWidth;
    private final int mVersionCode;

    public CircleOptions() {
        this.amB = null;
        this.amC = 0.0d;
        this.mStrokeWidth = 10.0f;
        this.mStrokeColor = -16777216;
        this.mFillColor = 0;
        this.amD = 0.0f;
        this.amE = true;
        this.amF = false;
        this.mVersionCode = 1;
    }

    CircleOptions(int i, LatLng latLng, double d, float f, int i2, int i3, float f2, boolean z, boolean z2) {
        this.amB = null;
        this.amC = 0.0d;
        this.mStrokeWidth = 10.0f;
        this.mStrokeColor = -16777216;
        this.mFillColor = 0;
        this.amD = 0.0f;
        this.amE = true;
        this.amF = false;
        this.mVersionCode = i;
        this.amB = latLng;
        this.amC = d;
        this.mStrokeWidth = f;
        this.mStrokeColor = i2;
        this.mFillColor = i3;
        this.amD = f2;
        this.amE = z;
        this.amF = z2;
    }

    public CircleOptions center(LatLng latLng) {
        this.amB = latLng;
        return this;
    }

    public CircleOptions clickable(boolean z) {
        this.amF = z;
        return this;
    }

    public CircleOptions fillColor(int i) {
        this.mFillColor = i;
        return this;
    }

    public LatLng getCenter() {
        return this.amB;
    }

    public int getFillColor() {
        return this.mFillColor;
    }

    public double getRadius() {
        return this.amC;
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

    public boolean isVisible() {
        return this.amE;
    }

    public CircleOptions radius(double d) {
        this.amC = d;
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
        this.amE = z;
        return this;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzb.zza(this, parcel, i);
    }

    public CircleOptions zIndex(float f) {
        this.amD = f;
        return this;
    }
}
