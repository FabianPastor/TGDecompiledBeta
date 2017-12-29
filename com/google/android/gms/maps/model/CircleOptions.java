package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;
import java.util.List;
import org.telegram.ui.ActionBar.Theme;

public final class CircleOptions extends zzbfm {
    public static final Creator<CircleOptions> CREATOR = new zzc();
    private int mFillColor = 0;
    private int mStrokeColor = Theme.ACTION_BAR_VIDEO_EDIT_COLOR;
    private float mStrokeWidth = 10.0f;
    private LatLng zziuk = null;
    private double zziul = 0.0d;
    private float zzium = 0.0f;
    private boolean zziun = true;
    private boolean zziuo = false;
    private List<PatternItem> zziup = null;

    CircleOptions(LatLng latLng, double d, float f, int i, int i2, float f2, boolean z, boolean z2, List<PatternItem> list) {
        this.zziuk = latLng;
        this.zziul = d;
        this.mStrokeWidth = f;
        this.mStrokeColor = i;
        this.mFillColor = i2;
        this.zzium = f2;
        this.zziun = z;
        this.zziuo = z2;
        this.zziup = list;
    }

    public final LatLng getCenter() {
        return this.zziuk;
    }

    public final int getFillColor() {
        return this.mFillColor;
    }

    public final double getRadius() {
        return this.zziul;
    }

    public final int getStrokeColor() {
        return this.mStrokeColor;
    }

    public final List<PatternItem> getStrokePattern() {
        return this.zziup;
    }

    public final float getStrokeWidth() {
        return this.mStrokeWidth;
    }

    public final float getZIndex() {
        return this.zzium;
    }

    public final boolean isClickable() {
        return this.zziuo;
    }

    public final boolean isVisible() {
        return this.zziun;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zza(parcel, 2, getCenter(), i, false);
        zzbfp.zza(parcel, 3, getRadius());
        zzbfp.zza(parcel, 4, getStrokeWidth());
        zzbfp.zzc(parcel, 5, getStrokeColor());
        zzbfp.zzc(parcel, 6, getFillColor());
        zzbfp.zza(parcel, 7, getZIndex());
        zzbfp.zza(parcel, 8, isVisible());
        zzbfp.zza(parcel, 9, isClickable());
        zzbfp.zzc(parcel, 10, getStrokePattern(), false);
        zzbfp.zzai(parcel, zze);
    }
}
