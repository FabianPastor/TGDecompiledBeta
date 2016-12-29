package com.google.android.gms.maps.model;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.dynamic.zzd;

public final class MarkerOptions extends zza {
    public static final Creator<MarkerOptions> CREATOR = new zzg();
    private float mAlpha;
    private final int mVersionCode;
    private String zzalD;
    private float zzboJ;
    private boolean zzboK;
    private float zzboS;
    private float zzboT;
    private LatLng zzbon;
    private String zzbpc;
    private BitmapDescriptor zzbpd;
    private boolean zzbpe;
    private boolean zzbpf;
    private float zzbpg;
    private float zzbph;
    private float zzbpi;

    public MarkerOptions() {
        this.zzboS = 0.5f;
        this.zzboT = 1.0f;
        this.zzboK = true;
        this.zzbpf = false;
        this.zzbpg = 0.0f;
        this.zzbph = 0.5f;
        this.zzbpi = 0.0f;
        this.mAlpha = 1.0f;
        this.mVersionCode = 1;
    }

    MarkerOptions(int i, LatLng latLng, String str, String str2, IBinder iBinder, float f, float f2, boolean z, boolean z2, boolean z3, float f3, float f4, float f5, float f6, float f7) {
        this.zzboS = 0.5f;
        this.zzboT = 1.0f;
        this.zzboK = true;
        this.zzbpf = false;
        this.zzbpg = 0.0f;
        this.zzbph = 0.5f;
        this.zzbpi = 0.0f;
        this.mAlpha = 1.0f;
        this.mVersionCode = i;
        this.zzbon = latLng;
        this.zzalD = str;
        this.zzbpc = str2;
        this.zzbpd = iBinder == null ? null : new BitmapDescriptor(zzd.zza.zzcd(iBinder));
        this.zzboS = f;
        this.zzboT = f2;
        this.zzbpe = z;
        this.zzboK = z2;
        this.zzbpf = z3;
        this.zzbpg = f3;
        this.zzbph = f4;
        this.zzbpi = f5;
        this.mAlpha = f6;
        this.zzboJ = f7;
    }

    public MarkerOptions alpha(float f) {
        this.mAlpha = f;
        return this;
    }

    public MarkerOptions anchor(float f, float f2) {
        this.zzboS = f;
        this.zzboT = f2;
        return this;
    }

    public MarkerOptions draggable(boolean z) {
        this.zzbpe = z;
        return this;
    }

    public MarkerOptions flat(boolean z) {
        this.zzbpf = z;
        return this;
    }

    public float getAlpha() {
        return this.mAlpha;
    }

    public float getAnchorU() {
        return this.zzboS;
    }

    public float getAnchorV() {
        return this.zzboT;
    }

    public BitmapDescriptor getIcon() {
        return this.zzbpd;
    }

    public float getInfoWindowAnchorU() {
        return this.zzbph;
    }

    public float getInfoWindowAnchorV() {
        return this.zzbpi;
    }

    public LatLng getPosition() {
        return this.zzbon;
    }

    public float getRotation() {
        return this.zzbpg;
    }

    public String getSnippet() {
        return this.zzbpc;
    }

    public String getTitle() {
        return this.zzalD;
    }

    int getVersionCode() {
        return this.mVersionCode;
    }

    public float getZIndex() {
        return this.zzboJ;
    }

    public MarkerOptions icon(@Nullable BitmapDescriptor bitmapDescriptor) {
        this.zzbpd = bitmapDescriptor;
        return this;
    }

    public MarkerOptions infoWindowAnchor(float f, float f2) {
        this.zzbph = f;
        this.zzbpi = f2;
        return this;
    }

    public boolean isDraggable() {
        return this.zzbpe;
    }

    public boolean isFlat() {
        return this.zzbpf;
    }

    public boolean isVisible() {
        return this.zzboK;
    }

    public MarkerOptions position(@NonNull LatLng latLng) {
        if (latLng == null) {
            throw new IllegalArgumentException("latlng cannot be null - a position is required.");
        }
        this.zzbon = latLng;
        return this;
    }

    public MarkerOptions rotation(float f) {
        this.zzbpg = f;
        return this;
    }

    public MarkerOptions snippet(@Nullable String str) {
        this.zzbpc = str;
        return this;
    }

    public MarkerOptions title(@Nullable String str) {
        this.zzalD = str;
        return this;
    }

    public MarkerOptions visible(boolean z) {
        this.zzboK = z;
        return this;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzg.zza(this, parcel, i);
    }

    public MarkerOptions zIndex(float f) {
        this.zzboJ = f;
        return this;
    }

    IBinder zzIV() {
        return this.zzbpd == null ? null : this.zzbpd.zzIy().asBinder();
    }
}
