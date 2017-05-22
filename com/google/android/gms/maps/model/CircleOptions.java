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
    private LatLng zzbpf = null;
    private double zzbpg = 0.0d;
    private float zzbph = 0.0f;
    private boolean zzbpi = true;
    private boolean zzbpj = false;
    @Nullable
    private List<PatternItem> zzbpk = null;

    CircleOptions(LatLng latLng, double d, float f, int i, int i2, float f2, boolean z, boolean z2, @Nullable List<PatternItem> list) {
        this.zzbpf = latLng;
        this.zzbpg = d;
        this.mStrokeWidth = f;
        this.mStrokeColor = i;
        this.mFillColor = i2;
        this.zzbph = f2;
        this.zzbpi = z;
        this.zzbpj = z2;
        this.zzbpk = list;
    }

    public CircleOptions center(LatLng latLng) {
        this.zzbpf = latLng;
        return this;
    }

    public CircleOptions clickable(boolean z) {
        this.zzbpj = z;
        return this;
    }

    public CircleOptions fillColor(int i) {
        this.mFillColor = i;
        return this;
    }

    public LatLng getCenter() {
        return this.zzbpf;
    }

    public int getFillColor() {
        return this.mFillColor;
    }

    public double getRadius() {
        return this.zzbpg;
    }

    public int getStrokeColor() {
        return this.mStrokeColor;
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

    public boolean isVisible() {
        return this.zzbpi;
    }

    public CircleOptions radius(double d) {
        this.zzbpg = d;
        return this;
    }

    public CircleOptions strokeColor(int i) {
        this.mStrokeColor = i;
        return this;
    }

    public CircleOptions strokePattern(@Nullable List<PatternItem> list) {
        this.zzbpk = list;
        return this;
    }

    public CircleOptions strokeWidth(float f) {
        this.mStrokeWidth = f;
        return this;
    }

    public CircleOptions visible(boolean z) {
        this.zzbpi = z;
        return this;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzc.zza(this, parcel, i);
    }

    public CircleOptions zIndex(float f) {
        this.zzbph = f;
        return this;
    }
}
