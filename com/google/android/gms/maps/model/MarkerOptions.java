package com.google.android.gms.maps.model;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.dynamic.IObjectWrapper;

public final class MarkerOptions extends zza {
    public static final Creator<MarkerOptions> CREATOR = new zzh();
    private float mAlpha = 1.0f;
    private String zzaoy;
    private LatLng zzbmN;
    private String zzbnE;
    private BitmapDescriptor zzbnF;
    private boolean zzbnG;
    private boolean zzbnH = false;
    private float zzbnI = 0.0f;
    private float zzbnJ = 0.5f;
    private float zzbnK = 0.0f;
    private float zzbnk;
    private boolean zzbnl = true;
    private float zzbnu = 0.5f;
    private float zzbnv = 1.0f;

    MarkerOptions(LatLng latLng, String str, String str2, IBinder iBinder, float f, float f2, boolean z, boolean z2, boolean z3, float f3, float f4, float f5, float f6, float f7) {
        this.zzbmN = latLng;
        this.zzaoy = str;
        this.zzbnE = str2;
        if (iBinder == null) {
            this.zzbnF = null;
        } else {
            this.zzbnF = new BitmapDescriptor(IObjectWrapper.zza.zzM(iBinder));
        }
        this.zzbnu = f;
        this.zzbnv = f2;
        this.zzbnG = z;
        this.zzbnl = z2;
        this.zzbnH = z3;
        this.zzbnI = f3;
        this.zzbnJ = f4;
        this.zzbnK = f5;
        this.mAlpha = f6;
        this.zzbnk = f7;
    }

    public final MarkerOptions alpha(float f) {
        this.mAlpha = f;
        return this;
    }

    public final MarkerOptions anchor(float f, float f2) {
        this.zzbnu = f;
        this.zzbnv = f2;
        return this;
    }

    public final MarkerOptions draggable(boolean z) {
        this.zzbnG = z;
        return this;
    }

    public final MarkerOptions flat(boolean z) {
        this.zzbnH = z;
        return this;
    }

    public final float getAlpha() {
        return this.mAlpha;
    }

    public final float getAnchorU() {
        return this.zzbnu;
    }

    public final float getAnchorV() {
        return this.zzbnv;
    }

    public final BitmapDescriptor getIcon() {
        return this.zzbnF;
    }

    public final float getInfoWindowAnchorU() {
        return this.zzbnJ;
    }

    public final float getInfoWindowAnchorV() {
        return this.zzbnK;
    }

    public final LatLng getPosition() {
        return this.zzbmN;
    }

    public final float getRotation() {
        return this.zzbnI;
    }

    public final String getSnippet() {
        return this.zzbnE;
    }

    public final String getTitle() {
        return this.zzaoy;
    }

    public final float getZIndex() {
        return this.zzbnk;
    }

    public final MarkerOptions icon(@Nullable BitmapDescriptor bitmapDescriptor) {
        this.zzbnF = bitmapDescriptor;
        return this;
    }

    public final MarkerOptions infoWindowAnchor(float f, float f2) {
        this.zzbnJ = f;
        this.zzbnK = f2;
        return this;
    }

    public final boolean isDraggable() {
        return this.zzbnG;
    }

    public final boolean isFlat() {
        return this.zzbnH;
    }

    public final boolean isVisible() {
        return this.zzbnl;
    }

    public final MarkerOptions position(@NonNull LatLng latLng) {
        if (latLng == null) {
            throw new IllegalArgumentException("latlng cannot be null - a position is required.");
        }
        this.zzbmN = latLng;
        return this;
    }

    public final MarkerOptions rotation(float f) {
        this.zzbnI = f;
        return this;
    }

    public final MarkerOptions snippet(@Nullable String str) {
        this.zzbnE = str;
        return this;
    }

    public final MarkerOptions title(@Nullable String str) {
        this.zzaoy = str;
        return this;
    }

    public final MarkerOptions visible(boolean z) {
        this.zzbnl = z;
        return this;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zza(parcel, 2, getPosition(), i, false);
        zzd.zza(parcel, 3, getTitle(), false);
        zzd.zza(parcel, 4, getSnippet(), false);
        zzd.zza(parcel, 5, this.zzbnF == null ? null : this.zzbnF.zzwe().asBinder(), false);
        zzd.zza(parcel, 6, getAnchorU());
        zzd.zza(parcel, 7, getAnchorV());
        zzd.zza(parcel, 8, isDraggable());
        zzd.zza(parcel, 9, isVisible());
        zzd.zza(parcel, 10, isFlat());
        zzd.zza(parcel, 11, getRotation());
        zzd.zza(parcel, 12, getInfoWindowAnchorU());
        zzd.zza(parcel, 13, getInfoWindowAnchorV());
        zzd.zza(parcel, 14, getAlpha());
        zzd.zza(parcel, 15, getZIndex());
        zzd.zzI(parcel, zze);
    }

    public final MarkerOptions zIndex(float f) {
        this.zzbnk = f;
        return this;
    }
}
