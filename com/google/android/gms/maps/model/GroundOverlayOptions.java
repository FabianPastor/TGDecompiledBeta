package com.google.android.gms.maps.model;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.dynamic.zzd;

public final class GroundOverlayOptions extends zza {
    public static final Creator<GroundOverlayOptions> CREATOR = new zzc();
    public static final float NO_DIMENSION = -1.0f;
    private final int mVersionCode;
    private LatLngBounds zzbmR;
    private float zzboF;
    private float zzboJ;
    private boolean zzboK;
    private boolean zzboL;
    private BitmapDescriptor zzboN;
    private LatLng zzboO;
    private float zzboP;
    private float zzboQ;
    private float zzboR;
    private float zzboS;
    private float zzboT;

    public GroundOverlayOptions() {
        this.zzboK = true;
        this.zzboR = 0.0f;
        this.zzboS = 0.5f;
        this.zzboT = 0.5f;
        this.zzboL = false;
        this.mVersionCode = 1;
    }

    GroundOverlayOptions(int i, IBinder iBinder, LatLng latLng, float f, float f2, LatLngBounds latLngBounds, float f3, float f4, boolean z, float f5, float f6, float f7, boolean z2) {
        this.zzboK = true;
        this.zzboR = 0.0f;
        this.zzboS = 0.5f;
        this.zzboT = 0.5f;
        this.zzboL = false;
        this.mVersionCode = i;
        this.zzboN = new BitmapDescriptor(zzd.zza.zzcd(iBinder));
        this.zzboO = latLng;
        this.zzboP = f;
        this.zzboQ = f2;
        this.zzbmR = latLngBounds;
        this.zzboF = f3;
        this.zzboJ = f4;
        this.zzboK = z;
        this.zzboR = f5;
        this.zzboS = f6;
        this.zzboT = f7;
        this.zzboL = z2;
    }

    private GroundOverlayOptions zza(LatLng latLng, float f, float f2) {
        this.zzboO = latLng;
        this.zzboP = f;
        this.zzboQ = f2;
        return this;
    }

    public GroundOverlayOptions anchor(float f, float f2) {
        this.zzboS = f;
        this.zzboT = f2;
        return this;
    }

    public GroundOverlayOptions bearing(float f) {
        this.zzboF = ((f % 360.0f) + 360.0f) % 360.0f;
        return this;
    }

    public GroundOverlayOptions clickable(boolean z) {
        this.zzboL = z;
        return this;
    }

    public float getAnchorU() {
        return this.zzboS;
    }

    public float getAnchorV() {
        return this.zzboT;
    }

    public float getBearing() {
        return this.zzboF;
    }

    public LatLngBounds getBounds() {
        return this.zzbmR;
    }

    public float getHeight() {
        return this.zzboQ;
    }

    public BitmapDescriptor getImage() {
        return this.zzboN;
    }

    public LatLng getLocation() {
        return this.zzboO;
    }

    public float getTransparency() {
        return this.zzboR;
    }

    int getVersionCode() {
        return this.mVersionCode;
    }

    public float getWidth() {
        return this.zzboP;
    }

    public float getZIndex() {
        return this.zzboJ;
    }

    public GroundOverlayOptions image(BitmapDescriptor bitmapDescriptor) {
        this.zzboN = bitmapDescriptor;
        return this;
    }

    public boolean isClickable() {
        return this.zzboL;
    }

    public boolean isVisible() {
        return this.zzboK;
    }

    public GroundOverlayOptions position(LatLng latLng, float f) {
        boolean z = true;
        zzac.zza(this.zzbmR == null, (Object) "Position has already been set using positionFromBounds");
        zzac.zzb(latLng != null, (Object) "Location must be specified");
        if (f < 0.0f) {
            z = false;
        }
        zzac.zzb(z, (Object) "Width must be non-negative");
        return zza(latLng, f, -1.0f);
    }

    public GroundOverlayOptions position(LatLng latLng, float f, float f2) {
        boolean z = true;
        zzac.zza(this.zzbmR == null, (Object) "Position has already been set using positionFromBounds");
        zzac.zzb(latLng != null, (Object) "Location must be specified");
        zzac.zzb(f >= 0.0f, (Object) "Width must be non-negative");
        if (f2 < 0.0f) {
            z = false;
        }
        zzac.zzb(z, (Object) "Height must be non-negative");
        return zza(latLng, f, f2);
    }

    public GroundOverlayOptions positionFromBounds(LatLngBounds latLngBounds) {
        boolean z = this.zzboO == null;
        String valueOf = String.valueOf(this.zzboO);
        zzac.zza(z, new StringBuilder(String.valueOf(valueOf).length() + 46).append("Position has already been set using position: ").append(valueOf).toString());
        this.zzbmR = latLngBounds;
        return this;
    }

    public GroundOverlayOptions transparency(float f) {
        boolean z = f >= 0.0f && f <= 1.0f;
        zzac.zzb(z, (Object) "Transparency must be in the range [0..1]");
        this.zzboR = f;
        return this;
    }

    public GroundOverlayOptions visible(boolean z) {
        this.zzboK = z;
        return this;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzc.zza(this, parcel, i);
    }

    public GroundOverlayOptions zIndex(float f) {
        this.zzboJ = f;
        return this;
    }

    IBinder zzIT() {
        return this.zzboN.zzIy().asBinder();
    }
}
