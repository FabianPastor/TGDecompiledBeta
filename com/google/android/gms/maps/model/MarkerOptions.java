package com.google.android.gms.maps.model;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.dynamic.zzd.zza;
import org.telegram.messenger.volley.DefaultRetryPolicy;

public final class MarkerOptions extends AbstractSafeParcelable {
    public static final Creator<MarkerOptions> CREATOR = new zzg();
    private String JB;
    private float apJ;
    private boolean apK;
    private float apS;
    private float apT;
    private LatLng apk;
    private String aqc;
    private BitmapDescriptor aqd;
    private boolean aqe;
    private boolean aqf;
    private float aqg;
    private float aqh;
    private float aqi;
    private float mAlpha;
    private final int mVersionCode;

    public MarkerOptions() {
        this.apS = 0.5f;
        this.apT = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        this.apK = true;
        this.aqf = false;
        this.aqg = 0.0f;
        this.aqh = 0.5f;
        this.aqi = 0.0f;
        this.mAlpha = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        this.mVersionCode = 1;
    }

    MarkerOptions(int i, LatLng latLng, String str, String str2, IBinder iBinder, float f, float f2, boolean z, boolean z2, boolean z3, float f3, float f4, float f5, float f6, float f7) {
        this.apS = 0.5f;
        this.apT = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        this.apK = true;
        this.aqf = false;
        this.aqg = 0.0f;
        this.aqh = 0.5f;
        this.aqi = 0.0f;
        this.mAlpha = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        this.mVersionCode = i;
        this.apk = latLng;
        this.JB = str;
        this.aqc = str2;
        this.aqd = iBinder == null ? null : new BitmapDescriptor(zza.zzfd(iBinder));
        this.apS = f;
        this.apT = f2;
        this.aqe = z;
        this.apK = z2;
        this.aqf = z3;
        this.aqg = f3;
        this.aqh = f4;
        this.aqi = f5;
        this.mAlpha = f6;
        this.apJ = f7;
    }

    public MarkerOptions alpha(float f) {
        this.mAlpha = f;
        return this;
    }

    public MarkerOptions anchor(float f, float f2) {
        this.apS = f;
        this.apT = f2;
        return this;
    }

    public MarkerOptions draggable(boolean z) {
        this.aqe = z;
        return this;
    }

    public MarkerOptions flat(boolean z) {
        this.aqf = z;
        return this;
    }

    public float getAlpha() {
        return this.mAlpha;
    }

    public float getAnchorU() {
        return this.apS;
    }

    public float getAnchorV() {
        return this.apT;
    }

    public BitmapDescriptor getIcon() {
        return this.aqd;
    }

    public float getInfoWindowAnchorU() {
        return this.aqh;
    }

    public float getInfoWindowAnchorV() {
        return this.aqi;
    }

    public LatLng getPosition() {
        return this.apk;
    }

    public float getRotation() {
        return this.aqg;
    }

    public String getSnippet() {
        return this.aqc;
    }

    public String getTitle() {
        return this.JB;
    }

    int getVersionCode() {
        return this.mVersionCode;
    }

    public float getZIndex() {
        return this.apJ;
    }

    public MarkerOptions icon(@Nullable BitmapDescriptor bitmapDescriptor) {
        this.aqd = bitmapDescriptor;
        return this;
    }

    public MarkerOptions infoWindowAnchor(float f, float f2) {
        this.aqh = f;
        this.aqi = f2;
        return this;
    }

    public boolean isDraggable() {
        return this.aqe;
    }

    public boolean isFlat() {
        return this.aqf;
    }

    public boolean isVisible() {
        return this.apK;
    }

    public MarkerOptions position(@NonNull LatLng latLng) {
        if (latLng == null) {
            throw new IllegalArgumentException("latlng cannot be null - a position is required.");
        }
        this.apk = latLng;
        return this;
    }

    public MarkerOptions rotation(float f) {
        this.aqg = f;
        return this;
    }

    public MarkerOptions snippet(@Nullable String str) {
        this.aqc = str;
        return this;
    }

    public MarkerOptions title(@Nullable String str) {
        this.JB = str;
        return this;
    }

    public MarkerOptions visible(boolean z) {
        this.apK = z;
        return this;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzg.zza(this, parcel, i);
    }

    public MarkerOptions zIndex(float f) {
        this.apJ = f;
        return this;
    }

    IBinder zzbsz() {
        return this.aqd == null ? null : this.aqd.zzbsc().asBinder();
    }
}
