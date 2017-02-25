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
    private LatLng zzboP;
    private String zzbpG;
    private BitmapDescriptor zzbpH;
    private boolean zzbpI;
    private boolean zzbpJ = false;
    private float zzbpK = 0.0f;
    private float zzbpL = 0.5f;
    private float zzbpM = 0.0f;
    private float zzbpm;
    private boolean zzbpn = true;
    private float zzbpw = 0.5f;
    private float zzbpx = 1.0f;

    MarkerOptions(LatLng latLng, String str, String str2, IBinder iBinder, float f, float f2, boolean z, boolean z2, boolean z3, float f3, float f4, float f5, float f6, float f7) {
        this.zzboP = latLng;
        this.zzamJ = str;
        this.zzbpG = str2;
        if (iBinder == null) {
            this.zzbpH = null;
        } else {
            this.zzbpH = new BitmapDescriptor(IObjectWrapper.zza.zzcd(iBinder));
        }
        this.zzbpw = f;
        this.zzbpx = f2;
        this.zzbpI = z;
        this.zzbpn = z2;
        this.zzbpJ = z3;
        this.zzbpK = f3;
        this.zzbpL = f4;
        this.zzbpM = f5;
        this.mAlpha = f6;
        this.zzbpm = f7;
    }

    public MarkerOptions alpha(float f) {
        this.mAlpha = f;
        return this;
    }

    public MarkerOptions anchor(float f, float f2) {
        this.zzbpw = f;
        this.zzbpx = f2;
        return this;
    }

    public MarkerOptions draggable(boolean z) {
        this.zzbpI = z;
        return this;
    }

    public MarkerOptions flat(boolean z) {
        this.zzbpJ = z;
        return this;
    }

    public float getAlpha() {
        return this.mAlpha;
    }

    public float getAnchorU() {
        return this.zzbpw;
    }

    public float getAnchorV() {
        return this.zzbpx;
    }

    public BitmapDescriptor getIcon() {
        return this.zzbpH;
    }

    public float getInfoWindowAnchorU() {
        return this.zzbpL;
    }

    public float getInfoWindowAnchorV() {
        return this.zzbpM;
    }

    public LatLng getPosition() {
        return this.zzboP;
    }

    public float getRotation() {
        return this.zzbpK;
    }

    public String getSnippet() {
        return this.zzbpG;
    }

    public String getTitle() {
        return this.zzamJ;
    }

    public float getZIndex() {
        return this.zzbpm;
    }

    public MarkerOptions icon(@Nullable BitmapDescriptor bitmapDescriptor) {
        this.zzbpH = bitmapDescriptor;
        return this;
    }

    public MarkerOptions infoWindowAnchor(float f, float f2) {
        this.zzbpL = f;
        this.zzbpM = f2;
        return this;
    }

    public boolean isDraggable() {
        return this.zzbpI;
    }

    public boolean isFlat() {
        return this.zzbpJ;
    }

    public boolean isVisible() {
        return this.zzbpn;
    }

    public MarkerOptions position(@NonNull LatLng latLng) {
        if (latLng == null) {
            throw new IllegalArgumentException("latlng cannot be null - a position is required.");
        }
        this.zzboP = latLng;
        return this;
    }

    public MarkerOptions rotation(float f) {
        this.zzbpK = f;
        return this;
    }

    public MarkerOptions snippet(@Nullable String str) {
        this.zzbpG = str;
        return this;
    }

    public MarkerOptions title(@Nullable String str) {
        this.zzamJ = str;
        return this;
    }

    public MarkerOptions visible(boolean z) {
        this.zzbpn = z;
        return this;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzh.zza(this, parcel, i);
    }

    public MarkerOptions zIndex(float f) {
        this.zzbpm = f;
        return this;
    }

    IBinder zzJL() {
        return this.zzbpH == null ? null : this.zzbpH.zzJl().asBinder();
    }
}
