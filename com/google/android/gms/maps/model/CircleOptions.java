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
    private LatLng zzbpg = null;
    private double zzbph = 0.0d;
    private float zzbpi = 0.0f;
    private boolean zzbpj = true;
    private boolean zzbpk = false;
    @Nullable
    private List<PatternItem> zzbpl = null;

    CircleOptions(LatLng latLng, double d, float f, int i, int i2, float f2, boolean z, boolean z2, @Nullable List<PatternItem> list) {
        this.zzbpg = latLng;
        this.zzbph = d;
        this.mStrokeWidth = f;
        this.mStrokeColor = i;
        this.mFillColor = i2;
        this.zzbpi = f2;
        this.zzbpj = z;
        this.zzbpk = z2;
        this.zzbpl = list;
    }

    public CircleOptions center(LatLng latLng) {
        this.zzbpg = latLng;
        return this;
    }

    public CircleOptions clickable(boolean z) {
        this.zzbpk = z;
        return this;
    }

    public CircleOptions fillColor(int i) {
        this.mFillColor = i;
        return this;
    }

    public LatLng getCenter() {
        return this.zzbpg;
    }

    public int getFillColor() {
        return this.mFillColor;
    }

    public double getRadius() {
        return this.zzbph;
    }

    public int getStrokeColor() {
        return this.mStrokeColor;
    }

    @Nullable
    public List<PatternItem> getStrokePattern() {
        return this.zzbpl;
    }

    public float getStrokeWidth() {
        return this.mStrokeWidth;
    }

    public float getZIndex() {
        return this.zzbpi;
    }

    public boolean isClickable() {
        return this.zzbpk;
    }

    public boolean isVisible() {
        return this.zzbpj;
    }

    public CircleOptions radius(double d) {
        this.zzbph = d;
        return this;
    }

    public CircleOptions strokeColor(int i) {
        this.mStrokeColor = i;
        return this;
    }

    public CircleOptions strokePattern(@Nullable List<PatternItem> list) {
        this.zzbpl = list;
        return this;
    }

    public CircleOptions strokeWidth(float f) {
        this.mStrokeWidth = f;
        return this;
    }

    public CircleOptions visible(boolean z) {
        this.zzbpj = z;
        return this;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzc.zza(this, parcel, i);
    }

    public CircleOptions zIndex(float f) {
        this.zzbpi = f;
        return this;
    }
}
