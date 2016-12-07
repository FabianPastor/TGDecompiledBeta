package com.google.android.gms.maps;

import android.os.Parcel;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.maps.internal.zza;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;

public final class StreetViewPanoramaOptions extends AbstractSafeParcelable implements ReflectedParcelable {
    public static final zzb CREATOR = new zzb();
    private Boolean aln;
    private Boolean alt;
    private StreetViewPanoramaCamera amc;
    private String amd;
    private LatLng ame;
    private Integer amf;
    private Boolean amg;
    private Boolean amh;
    private Boolean ami;
    private final int mVersionCode;

    public StreetViewPanoramaOptions() {
        this.amg = Boolean.valueOf(true);
        this.alt = Boolean.valueOf(true);
        this.amh = Boolean.valueOf(true);
        this.ami = Boolean.valueOf(true);
        this.mVersionCode = 1;
    }

    StreetViewPanoramaOptions(int i, StreetViewPanoramaCamera streetViewPanoramaCamera, String str, LatLng latLng, Integer num, byte b, byte b2, byte b3, byte b4, byte b5) {
        this.amg = Boolean.valueOf(true);
        this.alt = Boolean.valueOf(true);
        this.amh = Boolean.valueOf(true);
        this.ami = Boolean.valueOf(true);
        this.mVersionCode = i;
        this.amc = streetViewPanoramaCamera;
        this.ame = latLng;
        this.amf = num;
        this.amd = str;
        this.amg = zza.zza(b);
        this.alt = zza.zza(b2);
        this.amh = zza.zza(b3);
        this.ami = zza.zza(b4);
        this.aln = zza.zza(b5);
    }

    public Boolean getPanningGesturesEnabled() {
        return this.amh;
    }

    public String getPanoramaId() {
        return this.amd;
    }

    public LatLng getPosition() {
        return this.ame;
    }

    public Integer getRadius() {
        return this.amf;
    }

    public Boolean getStreetNamesEnabled() {
        return this.ami;
    }

    public StreetViewPanoramaCamera getStreetViewPanoramaCamera() {
        return this.amc;
    }

    public Boolean getUseViewLifecycleInFragment() {
        return this.aln;
    }

    public Boolean getUserNavigationEnabled() {
        return this.amg;
    }

    int getVersionCode() {
        return this.mVersionCode;
    }

    public Boolean getZoomGesturesEnabled() {
        return this.alt;
    }

    public StreetViewPanoramaOptions panningGesturesEnabled(boolean z) {
        this.amh = Boolean.valueOf(z);
        return this;
    }

    public StreetViewPanoramaOptions panoramaCamera(StreetViewPanoramaCamera streetViewPanoramaCamera) {
        this.amc = streetViewPanoramaCamera;
        return this;
    }

    public StreetViewPanoramaOptions panoramaId(String str) {
        this.amd = str;
        return this;
    }

    public StreetViewPanoramaOptions position(LatLng latLng) {
        this.ame = latLng;
        return this;
    }

    public StreetViewPanoramaOptions position(LatLng latLng, Integer num) {
        this.ame = latLng;
        this.amf = num;
        return this;
    }

    public StreetViewPanoramaOptions streetNamesEnabled(boolean z) {
        this.ami = Boolean.valueOf(z);
        return this;
    }

    public StreetViewPanoramaOptions useViewLifecycleInFragment(boolean z) {
        this.aln = Boolean.valueOf(z);
        return this;
    }

    public StreetViewPanoramaOptions userNavigationEnabled(boolean z) {
        this.amg = Boolean.valueOf(z);
        return this;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzb.zza(this, parcel, i);
    }

    public StreetViewPanoramaOptions zoomGesturesEnabled(boolean z) {
        this.alt = Boolean.valueOf(z);
        return this;
    }

    byte zzbrk() {
        return zza.zze(this.aln);
    }

    byte zzbro() {
        return zza.zze(this.alt);
    }

    byte zzbry() {
        return zza.zze(this.amg);
    }

    byte zzbrz() {
        return zza.zze(this.amh);
    }

    byte zzbsa() {
        return zza.zze(this.ami);
    }
}
