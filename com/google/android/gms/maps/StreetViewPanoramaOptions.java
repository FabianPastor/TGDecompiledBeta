package com.google.android.gms.maps;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;

public final class StreetViewPanoramaOptions extends zza implements ReflectedParcelable {
    public static final Creator<StreetViewPanoramaOptions> CREATOR = new zzb();
    private StreetViewPanoramaCamera zzboN;
    private String zzboO;
    private LatLng zzboP;
    private Integer zzboQ;
    private Boolean zzboR = Boolean.valueOf(true);
    private Boolean zzboS = Boolean.valueOf(true);
    private Boolean zzboT = Boolean.valueOf(true);
    private Boolean zzbod;
    private Boolean zzboj = Boolean.valueOf(true);

    StreetViewPanoramaOptions(StreetViewPanoramaCamera streetViewPanoramaCamera, String str, LatLng latLng, Integer num, byte b, byte b2, byte b3, byte b4, byte b5) {
        this.zzboN = streetViewPanoramaCamera;
        this.zzboP = latLng;
        this.zzboQ = num;
        this.zzboO = str;
        this.zzboR = com.google.android.gms.maps.internal.zza.zza(b);
        this.zzboj = com.google.android.gms.maps.internal.zza.zza(b2);
        this.zzboS = com.google.android.gms.maps.internal.zza.zza(b3);
        this.zzboT = com.google.android.gms.maps.internal.zza.zza(b4);
        this.zzbod = com.google.android.gms.maps.internal.zza.zza(b5);
    }

    public Boolean getPanningGesturesEnabled() {
        return this.zzboS;
    }

    public String getPanoramaId() {
        return this.zzboO;
    }

    public LatLng getPosition() {
        return this.zzboP;
    }

    public Integer getRadius() {
        return this.zzboQ;
    }

    public Boolean getStreetNamesEnabled() {
        return this.zzboT;
    }

    public StreetViewPanoramaCamera getStreetViewPanoramaCamera() {
        return this.zzboN;
    }

    public Boolean getUseViewLifecycleInFragment() {
        return this.zzbod;
    }

    public Boolean getUserNavigationEnabled() {
        return this.zzboR;
    }

    public Boolean getZoomGesturesEnabled() {
        return this.zzboj;
    }

    public StreetViewPanoramaOptions panningGesturesEnabled(boolean z) {
        this.zzboS = Boolean.valueOf(z);
        return this;
    }

    public StreetViewPanoramaOptions panoramaCamera(StreetViewPanoramaCamera streetViewPanoramaCamera) {
        this.zzboN = streetViewPanoramaCamera;
        return this;
    }

    public StreetViewPanoramaOptions panoramaId(String str) {
        this.zzboO = str;
        return this;
    }

    public StreetViewPanoramaOptions position(LatLng latLng) {
        this.zzboP = latLng;
        return this;
    }

    public StreetViewPanoramaOptions position(LatLng latLng, Integer num) {
        this.zzboP = latLng;
        this.zzboQ = num;
        return this;
    }

    public StreetViewPanoramaOptions streetNamesEnabled(boolean z) {
        this.zzboT = Boolean.valueOf(z);
        return this;
    }

    public StreetViewPanoramaOptions useViewLifecycleInFragment(boolean z) {
        this.zzbod = Boolean.valueOf(z);
        return this;
    }

    public StreetViewPanoramaOptions userNavigationEnabled(boolean z) {
        this.zzboR = Boolean.valueOf(z);
        return this;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzb.zza(this, parcel, i);
    }

    public StreetViewPanoramaOptions zoomGesturesEnabled(boolean z) {
        this.zzboj = Boolean.valueOf(z);
        return this;
    }

    byte zzJA() {
        return com.google.android.gms.maps.internal.zza.zzd(this.zzboR);
    }

    byte zzJB() {
        return com.google.android.gms.maps.internal.zza.zzd(this.zzboS);
    }

    byte zzJC() {
        return com.google.android.gms.maps.internal.zza.zzd(this.zzboT);
    }

    byte zzJo() {
        return com.google.android.gms.maps.internal.zza.zzd(this.zzbod);
    }

    byte zzJs() {
        return com.google.android.gms.maps.internal.zza.zzd(this.zzboj);
    }
}
