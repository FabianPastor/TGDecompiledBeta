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
    private LatLng zzboL;
    private String zzbpC;
    private BitmapDescriptor zzbpD;
    private boolean zzbpE;
    private boolean zzbpF = false;
    private float zzbpG = 0.0f;
    private float zzbpH = 0.5f;
    private float zzbpI = 0.0f;
    private float zzbpi;
    private boolean zzbpj = true;
    private float zzbps = 0.5f;
    private float zzbpt = 1.0f;

    MarkerOptions(LatLng latLng, String str, String str2, IBinder iBinder, float f, float f2, boolean z, boolean z2, boolean z3, float f3, float f4, float f5, float f6, float f7) {
        this.zzboL = latLng;
        this.zzamJ = str;
        this.zzbpC = str2;
        if (iBinder == null) {
            this.zzbpD = null;
        } else {
            this.zzbpD = new BitmapDescriptor(IObjectWrapper.zza.zzcd(iBinder));
        }
        this.zzbps = f;
        this.zzbpt = f2;
        this.zzbpE = z;
        this.zzbpj = z2;
        this.zzbpF = z3;
        this.zzbpG = f3;
        this.zzbpH = f4;
        this.zzbpI = f5;
        this.mAlpha = f6;
        this.zzbpi = f7;
    }

    public MarkerOptions alpha(float f) {
        this.mAlpha = f;
        return this;
    }

    public MarkerOptions anchor(float f, float f2) {
        this.zzbps = f;
        this.zzbpt = f2;
        return this;
    }

    public MarkerOptions draggable(boolean z) {
        this.zzbpE = z;
        return this;
    }

    public MarkerOptions flat(boolean z) {
        this.zzbpF = z;
        return this;
    }

    public float getAlpha() {
        return this.mAlpha;
    }

    public float getAnchorU() {
        return this.zzbps;
    }

    public float getAnchorV() {
        return this.zzbpt;
    }

    public BitmapDescriptor getIcon() {
        return this.zzbpD;
    }

    public float getInfoWindowAnchorU() {
        return this.zzbpH;
    }

    public float getInfoWindowAnchorV() {
        return this.zzbpI;
    }

    public LatLng getPosition() {
        return this.zzboL;
    }

    public float getRotation() {
        return this.zzbpG;
    }

    public String getSnippet() {
        return this.zzbpC;
    }

    public String getTitle() {
        return this.zzamJ;
    }

    public float getZIndex() {
        return this.zzbpi;
    }

    public MarkerOptions icon(@Nullable BitmapDescriptor bitmapDescriptor) {
        this.zzbpD = bitmapDescriptor;
        return this;
    }

    public MarkerOptions infoWindowAnchor(float f, float f2) {
        this.zzbpH = f;
        this.zzbpI = f2;
        return this;
    }

    public boolean isDraggable() {
        return this.zzbpE;
    }

    public boolean isFlat() {
        return this.zzbpF;
    }

    public boolean isVisible() {
        return this.zzbpj;
    }

    public MarkerOptions position(@NonNull LatLng latLng) {
        if (latLng == null) {
            throw new IllegalArgumentException("latlng cannot be null - a position is required.");
        }
        this.zzboL = latLng;
        return this;
    }

    public MarkerOptions rotation(float f) {
        this.zzbpG = f;
        return this;
    }

    public MarkerOptions snippet(@Nullable String str) {
        this.zzbpC = str;
        return this;
    }

    public MarkerOptions title(@Nullable String str) {
        this.zzamJ = str;
        return this;
    }

    public MarkerOptions visible(boolean z) {
        this.zzbpj = z;
        return this;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzh.zza(this, parcel, i);
    }

    public MarkerOptions zIndex(float f) {
        this.zzbpi = f;
        return this;
    }

    IBinder zzJM() {
        return this.zzbpD == null ? null : this.zzbpD.zzJm().asBinder();
    }
}
