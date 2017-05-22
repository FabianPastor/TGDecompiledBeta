package com.google.android.gms.maps.model;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.dynamic.IObjectWrapper;

public final class MarkerOptions extends zza {
    public static final Creator<MarkerOptions> CREATOR = new zzh();
    private float mAlpha = 1.0f;
    private String zzamJ;
    private LatLng zzboK;
    private String zzbpB;
    private BitmapDescriptor zzbpC;
    private boolean zzbpD;
    private boolean zzbpE = false;
    private float zzbpF = 0.0f;
    private float zzbpG = 0.5f;
    private float zzbpH = 0.0f;
    private float zzbph;
    private boolean zzbpi = true;
    private float zzbpr = 0.5f;
    private float zzbps = 1.0f;

    MarkerOptions(LatLng latLng, String str, String str2, IBinder iBinder, float f, float f2, boolean z, boolean z2, boolean z3, float f3, float f4, float f5, float f6, float f7) {
        this.zzboK = latLng;
        this.zzamJ = str;
        this.zzbpB = str2;
        if (iBinder == null) {
            this.zzbpC = null;
        } else {
            this.zzbpC = new BitmapDescriptor(IObjectWrapper.zza.zzcd(iBinder));
        }
        this.zzbpr = f;
        this.zzbps = f2;
        this.zzbpD = z;
        this.zzbpi = z2;
        this.zzbpE = z3;
        this.zzbpF = f3;
        this.zzbpG = f4;
        this.zzbpH = f5;
        this.mAlpha = f6;
        this.zzbph = f7;
    }

    public MarkerOptions alpha(float f) {
        this.mAlpha = f;
        return this;
    }

    public MarkerOptions anchor(float f, float f2) {
        this.zzbpr = f;
        this.zzbps = f2;
        return this;
    }

    public MarkerOptions draggable(boolean z) {
        this.zzbpD = z;
        return this;
    }

    public MarkerOptions flat(boolean z) {
        this.zzbpE = z;
        return this;
    }

    public float getAlpha() {
        return this.mAlpha;
    }

    public float getAnchorU() {
        return this.zzbpr;
    }

    public float getAnchorV() {
        return this.zzbps;
    }

    public BitmapDescriptor getIcon() {
        return this.zzbpC;
    }

    public float getInfoWindowAnchorU() {
        return this.zzbpG;
    }

    public float getInfoWindowAnchorV() {
        return this.zzbpH;
    }

    public LatLng getPosition() {
        return this.zzboK;
    }

    public float getRotation() {
        return this.zzbpF;
    }

    public String getSnippet() {
        return this.zzbpB;
    }

    public String getTitle() {
        return this.zzamJ;
    }

    public float getZIndex() {
        return this.zzbph;
    }

    public MarkerOptions icon(@Nullable BitmapDescriptor bitmapDescriptor) {
        this.zzbpC = bitmapDescriptor;
        return this;
    }

    public MarkerOptions infoWindowAnchor(float f, float f2) {
        this.zzbpG = f;
        this.zzbpH = f2;
        return this;
    }

    public boolean isDraggable() {
        return this.zzbpD;
    }

    public boolean isFlat() {
        return this.zzbpE;
    }

    public boolean isVisible() {
        return this.zzbpi;
    }

    public MarkerOptions position(@NonNull LatLng latLng) {
        if (latLng == null) {
            throw new IllegalArgumentException("latlng cannot be null - a position is required.");
        }
        this.zzboK = latLng;
        return this;
    }

    public MarkerOptions rotation(float f) {
        this.zzbpF = f;
        return this;
    }

    public MarkerOptions snippet(@Nullable String str) {
        this.zzbpB = str;
        return this;
    }

    public MarkerOptions title(@Nullable String str) {
        this.zzamJ = str;
        return this;
    }

    public MarkerOptions visible(boolean z) {
        this.zzbpi = z;
        return this;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzh.zza(this, parcel, i);
    }

    public MarkerOptions zIndex(float f) {
        this.zzbph = f;
        return this;
    }

    IBinder zzJM() {
        return this.zzbpC == null ? null : this.zzbpC.zzJm().asBinder();
    }
}
