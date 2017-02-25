package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.Nullable;
import com.google.android.gms.common.internal.safeparcel.zza;
import java.util.List;

public final class CircleOptions extends zza {
    public static final Creator<CircleOptions> CREATOR = new zzc();
    private int mFillColor = 0;
    private int mStrokeColor = -16777216;
    private float mStrokeWidth = 10.0f;
    private LatLng zzbpk = null;
    private double zzbpl = 0.0d;
    private float zzbpm = 0.0f;
    private boolean zzbpn = true;
    private boolean zzbpo = false;
    @Nullable
    private List<PatternItem> zzbpp = null;

    CircleOptions(LatLng latLng, double d, float f, int i, int i2, float f2, boolean z, boolean z2, @Nullable List<PatternItem> list) {
        this.zzbpk = latLng;
        this.zzbpl = d;
        this.mStrokeWidth = f;
        this.mStrokeColor = i;
        this.mFillColor = i2;
        this.zzbpm = f2;
        this.zzbpn = z;
        this.zzbpo = z2;
        this.zzbpp = list;
    }

    public CircleOptions center(LatLng latLng) {
        this.zzbpk = latLng;
        return this;
    }

    public CircleOptions clickable(boolean z) {
        this.zzbpo = z;
        return this;
    }

    public CircleOptions fillColor(int i) {
        this.mFillColor = i;
        return this;
    }

    public LatLng getCenter() {
        return this.zzbpk;
    }

    public int getFillColor() {
        return this.mFillColor;
    }

    public double getRadius() {
        return this.zzbpl;
    }

    public int getStrokeColor() {
        return this.mStrokeColor;
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

    public boolean isVisible() {
        return this.zzbpn;
    }

    public CircleOptions radius(double d) {
        this.zzbpl = d;
        return this;
    }

    public CircleOptions strokeColor(int i) {
        this.mStrokeColor = i;
        return this;
    }

    public CircleOptions strokePattern(@Nullable List<PatternItem> list) {
        this.zzbpp = list;
        return this;
    }

    public CircleOptions strokeWidth(float f) {
        this.mStrokeWidth = f;
        return this;
    }

    public CircleOptions visible(boolean z) {
        this.zzbpn = z;
        return this;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzc.zza(this, parcel, i);
    }

    public CircleOptions zIndex(float f) {
        this.zzbpm = f;
        return this;
    }
}
