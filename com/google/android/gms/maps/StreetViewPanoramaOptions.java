package com.google.android.gms.maps;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;

public final class StreetViewPanoramaOptions extends zza implements ReflectedParcelable {
    public static final Creator<StreetViewPanoramaOptions> CREATOR = new zzb();
    private final int mVersionCode;
    private Boolean zzbnB;
    private Boolean zzbnH;
    private StreetViewPanoramaCamera zzbol;
    private String zzbom;
    private LatLng zzbon;
    private Integer zzboo;
    private Boolean zzbop;
    private Boolean zzboq;
    private Boolean zzbor;

    public StreetViewPanoramaOptions() {
        this.zzbop = Boolean.valueOf(true);
        this.zzbnH = Boolean.valueOf(true);
        this.zzboq = Boolean.valueOf(true);
        this.zzbor = Boolean.valueOf(true);
        this.mVersionCode = 1;
    }

    StreetViewPanoramaOptions(int i, StreetViewPanoramaCamera streetViewPanoramaCamera, String str, LatLng latLng, Integer num, byte b, byte b2, byte b3, byte b4, byte b5) {
        this.zzbop = Boolean.valueOf(true);
        this.zzbnH = Boolean.valueOf(true);
        this.zzboq = Boolean.valueOf(true);
        this.zzbor = Boolean.valueOf(true);
        this.mVersionCode = i;
        this.zzbol = streetViewPanoramaCamera;
        this.zzbon = latLng;
        this.zzboo = num;
        this.zzbom = str;
        this.zzbop = com.google.android.gms.maps.internal.zza.zza(b);
        this.zzbnH = com.google.android.gms.maps.internal.zza.zza(b2);
        this.zzboq = com.google.android.gms.maps.internal.zza.zza(b3);
        this.zzbor = com.google.android.gms.maps.internal.zza.zza(b4);
        this.zzbnB = com.google.android.gms.maps.internal.zza.zza(b5);
    }

    public Boolean getPanningGesturesEnabled() {
        return this.zzboq;
    }

    public String getPanoramaId() {
        return this.zzbom;
    }

    public LatLng getPosition() {
        return this.zzbon;
    }

    public Integer getRadius() {
        return this.zzboo;
    }

    public Boolean getStreetNamesEnabled() {
        return this.zzbor;
    }

    public StreetViewPanoramaCamera getStreetViewPanoramaCamera() {
        return this.zzbol;
    }

    public Boolean getUseViewLifecycleInFragment() {
        return this.zzbnB;
    }

    public Boolean getUserNavigationEnabled() {
        return this.zzbop;
    }

    int getVersionCode() {
        return this.mVersionCode;
    }

    public Boolean getZoomGesturesEnabled() {
        return this.zzbnH;
    }

    public StreetViewPanoramaOptions panningGesturesEnabled(boolean z) {
        this.zzboq = Boolean.valueOf(z);
        return this;
    }

    public StreetViewPanoramaOptions panoramaCamera(StreetViewPanoramaCamera streetViewPanoramaCamera) {
        this.zzbol = streetViewPanoramaCamera;
        return this;
    }

    public StreetViewPanoramaOptions panoramaId(String str) {
        this.zzbom = str;
        return this;
    }

    public StreetViewPanoramaOptions position(LatLng latLng) {
        this.zzbon = latLng;
        return this;
    }

    public StreetViewPanoramaOptions position(LatLng latLng, Integer num) {
        this.zzbon = latLng;
        this.zzboo = num;
        return this;
    }

    public StreetViewPanoramaOptions streetNamesEnabled(boolean z) {
        this.zzbor = Boolean.valueOf(z);
        return this;
    }

    public StreetViewPanoramaOptions useViewLifecycleInFragment(boolean z) {
        this.zzbnB = Boolean.valueOf(z);
        return this;
    }

    public StreetViewPanoramaOptions userNavigationEnabled(boolean z) {
        this.zzbop = Boolean.valueOf(z);
        return this;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzb.zza(this, parcel, i);
    }

    public StreetViewPanoramaOptions zoomGesturesEnabled(boolean z) {
        this.zzbnH = Boolean.valueOf(z);
        return this;
    }

    byte zzIB() {
        return com.google.android.gms.maps.internal.zza.zze(this.zzbnB);
    }

    byte zzIF() {
        return com.google.android.gms.maps.internal.zza.zze(this.zzbnH);
    }

    byte zzIN() {
        return com.google.android.gms.maps.internal.zza.zze(this.zzbop);
    }

    byte zzIO() {
        return com.google.android.gms.maps.internal.zza.zze(this.zzboq);
    }

    byte zzIP() {
        return com.google.android.gms.maps.internal.zza.zze(this.zzbor);
    }
}
