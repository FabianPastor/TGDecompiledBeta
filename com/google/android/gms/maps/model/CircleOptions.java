package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.Nullable;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import java.util.List;

public final class CircleOptions extends zza {
    public static final Creator<CircleOptions> CREATOR = new zzc();
    private int mFillColor = 0;
    private int mStrokeColor = -16777216;
    private float mStrokeWidth = 10.0f;
    private LatLng zzbni = null;
    private double zzbnj = 0.0d;
    private float zzbnk = 0.0f;
    private boolean zzbnl = true;
    private boolean zzbnm = false;
    @Nullable
    private List<PatternItem> zzbnn = null;

    CircleOptions(LatLng latLng, double d, float f, int i, int i2, float f2, boolean z, boolean z2, @Nullable List<PatternItem> list) {
        this.zzbni = latLng;
        this.zzbnj = d;
        this.mStrokeWidth = f;
        this.mStrokeColor = i;
        this.mFillColor = i2;
        this.zzbnk = f2;
        this.zzbnl = z;
        this.zzbnm = z2;
        this.zzbnn = list;
    }

    public final CircleOptions center(LatLng latLng) {
        this.zzbni = latLng;
        return this;
    }

    public final CircleOptions clickable(boolean z) {
        this.zzbnm = z;
        return this;
    }

    public final CircleOptions fillColor(int i) {
        this.mFillColor = i;
        return this;
    }

    public final LatLng getCenter() {
        return this.zzbni;
    }

    public final int getFillColor() {
        return this.mFillColor;
    }

    public final double getRadius() {
        return this.zzbnj;
    }

    public final int getStrokeColor() {
        return this.mStrokeColor;
    }

    @Nullable
    public final List<PatternItem> getStrokePattern() {
        return this.zzbnn;
    }

    public final float getStrokeWidth() {
        return this.mStrokeWidth;
    }

    public final float getZIndex() {
        return this.zzbnk;
    }

    public final boolean isClickable() {
        return this.zzbnm;
    }

    public final boolean isVisible() {
        return this.zzbnl;
    }

    public final CircleOptions radius(double d) {
        this.zzbnj = d;
        return this;
    }

    public final CircleOptions strokeColor(int i) {
        this.mStrokeColor = i;
        return this;
    }

    public final CircleOptions strokePattern(@Nullable List<PatternItem> list) {
        this.zzbnn = list;
        return this;
    }

    public final CircleOptions strokeWidth(float f) {
        this.mStrokeWidth = f;
        return this;
    }

    public final CircleOptions visible(boolean z) {
        this.zzbnl = z;
        return this;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zza(parcel, 2, getCenter(), i, false);
        zzd.zza(parcel, 3, getRadius());
        zzd.zza(parcel, 4, getStrokeWidth());
        zzd.zzc(parcel, 5, getStrokeColor());
        zzd.zzc(parcel, 6, getFillColor());
        zzd.zza(parcel, 7, getZIndex());
        zzd.zza(parcel, 8, isVisible());
        zzd.zza(parcel, 9, isClickable());
        zzd.zzc(parcel, 10, getStrokePattern(), false);
        zzd.zzI(parcel, zze);
    }

    public final CircleOptions zIndex(float f) {
        this.zzbnk = f;
        return this;
    }
}
