package com.google.android.gms.maps;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;

public final class StreetViewPanoramaOptions extends zza implements ReflectedParcelable {
    public static final Creator<StreetViewPanoramaOptions> CREATOR = new zzb();
    private Boolean zzbnY;
    private StreetViewPanoramaCamera zzboI;
    private String zzboJ;
    private LatLng zzboK;
    private Integer zzboL;
    private Boolean zzboM = Boolean.valueOf(true);
    private Boolean zzboN = Boolean.valueOf(true);
    private Boolean zzboO = Boolean.valueOf(true);
    private Boolean zzboe = Boolean.valueOf(true);

    StreetViewPanoramaOptions(StreetViewPanoramaCamera streetViewPanoramaCamera, String str, LatLng latLng, Integer num, byte b, byte b2, byte b3, byte b4, byte b5) {
        this.zzboI = streetViewPanoramaCamera;
        this.zzboK = latLng;
        this.zzboL = num;
        this.zzboJ = str;
        this.zzboM = com.google.android.gms.maps.internal.zza.zza(b);
        this.zzboe = com.google.android.gms.maps.internal.zza.zza(b2);
        this.zzboN = com.google.android.gms.maps.internal.zza.zza(b3);
        this.zzboO = com.google.android.gms.maps.internal.zza.zza(b4);
        this.zzbnY = com.google.android.gms.maps.internal.zza.zza(b5);
    }

    public Boolean getPanningGesturesEnabled() {
        return this.zzboN;
    }

    public String getPanoramaId() {
        return this.zzboJ;
    }

    public LatLng getPosition() {
        return this.zzboK;
    }

    public Integer getRadius() {
        return this.zzboL;
    }

    public Boolean getStreetNamesEnabled() {
        return this.zzboO;
    }

    public StreetViewPanoramaCamera getStreetViewPanoramaCamera() {
        return this.zzboI;
    }

    public Boolean getUseViewLifecycleInFragment() {
        return this.zzbnY;
    }

    public Boolean getUserNavigationEnabled() {
        return this.zzboM;
    }

    public Boolean getZoomGesturesEnabled() {
        return this.zzboe;
    }

    public StreetViewPanoramaOptions panningGesturesEnabled(boolean z) {
        this.zzboN = Boolean.valueOf(z);
        return this;
    }

    public StreetViewPanoramaOptions panoramaCamera(StreetViewPanoramaCamera streetViewPanoramaCamera) {
        this.zzboI = streetViewPanoramaCamera;
        return this;
    }

    public StreetViewPanoramaOptions panoramaId(String str) {
        this.zzboJ = str;
        return this;
    }

    public StreetViewPanoramaOptions position(LatLng latLng) {
        this.zzboK = latLng;
        return this;
    }

    public StreetViewPanoramaOptions position(LatLng latLng, Integer num) {
        this.zzboK = latLng;
        this.zzboL = num;
        return this;
    }

    public StreetViewPanoramaOptions streetNamesEnabled(boolean z) {
        this.zzboO = Boolean.valueOf(z);
        return this;
    }

    public StreetViewPanoramaOptions useViewLifecycleInFragment(boolean z) {
        this.zzbnY = Boolean.valueOf(z);
        return this;
    }

    public StreetViewPanoramaOptions userNavigationEnabled(boolean z) {
        this.zzboM = Boolean.valueOf(z);
        return this;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzb.zza(this, parcel, i);
    }

    public StreetViewPanoramaOptions zoomGesturesEnabled(boolean z) {
        this.zzboe = Boolean.valueOf(z);
        return this;
    }

    byte zzJB() {
        return com.google.android.gms.maps.internal.zza.zzd(this.zzboM);
    }

    byte zzJC() {
        return com.google.android.gms.maps.internal.zza.zzd(this.zzboN);
    }

    byte zzJD() {
        return com.google.android.gms.maps.internal.zza.zzd(this.zzboO);
    }

    byte zzJp() {
        return com.google.android.gms.maps.internal.zza.zzd(this.zzbnY);
    }

    byte zzJt() {
        return com.google.android.gms.maps.internal.zza.zzd(this.zzboe);
    }
}
