package com.google.android.gms.maps;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.common.internal.zzbe;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;

public final class StreetViewPanoramaOptions extends zza implements ReflectedParcelable {
    public static final Creator<StreetViewPanoramaOptions> CREATOR = new zzah();
    private StreetViewPanoramaCamera zzbmL;
    private String zzbmM;
    private LatLng zzbmN;
    private Integer zzbmO;
    private Boolean zzbmP = Boolean.valueOf(true);
    private Boolean zzbmQ = Boolean.valueOf(true);
    private Boolean zzbmR = Boolean.valueOf(true);
    private Boolean zzbma;
    private Boolean zzbmg = Boolean.valueOf(true);

    StreetViewPanoramaOptions(StreetViewPanoramaCamera streetViewPanoramaCamera, String str, LatLng latLng, Integer num, byte b, byte b2, byte b3, byte b4, byte b5) {
        this.zzbmL = streetViewPanoramaCamera;
        this.zzbmN = latLng;
        this.zzbmO = num;
        this.zzbmM = str;
        this.zzbmP = com.google.android.gms.maps.internal.zza.zza(b);
        this.zzbmg = com.google.android.gms.maps.internal.zza.zza(b2);
        this.zzbmQ = com.google.android.gms.maps.internal.zza.zza(b3);
        this.zzbmR = com.google.android.gms.maps.internal.zza.zza(b4);
        this.zzbma = com.google.android.gms.maps.internal.zza.zza(b5);
    }

    public final Boolean getPanningGesturesEnabled() {
        return this.zzbmQ;
    }

    public final String getPanoramaId() {
        return this.zzbmM;
    }

    public final LatLng getPosition() {
        return this.zzbmN;
    }

    public final Integer getRadius() {
        return this.zzbmO;
    }

    public final Boolean getStreetNamesEnabled() {
        return this.zzbmR;
    }

    public final StreetViewPanoramaCamera getStreetViewPanoramaCamera() {
        return this.zzbmL;
    }

    public final Boolean getUseViewLifecycleInFragment() {
        return this.zzbma;
    }

    public final Boolean getUserNavigationEnabled() {
        return this.zzbmP;
    }

    public final Boolean getZoomGesturesEnabled() {
        return this.zzbmg;
    }

    public final StreetViewPanoramaOptions panningGesturesEnabled(boolean z) {
        this.zzbmQ = Boolean.valueOf(z);
        return this;
    }

    public final StreetViewPanoramaOptions panoramaCamera(StreetViewPanoramaCamera streetViewPanoramaCamera) {
        this.zzbmL = streetViewPanoramaCamera;
        return this;
    }

    public final StreetViewPanoramaOptions panoramaId(String str) {
        this.zzbmM = str;
        return this;
    }

    public final StreetViewPanoramaOptions position(LatLng latLng) {
        this.zzbmN = latLng;
        return this;
    }

    public final StreetViewPanoramaOptions position(LatLng latLng, Integer num) {
        this.zzbmN = latLng;
        this.zzbmO = num;
        return this;
    }

    public final StreetViewPanoramaOptions streetNamesEnabled(boolean z) {
        this.zzbmR = Boolean.valueOf(z);
        return this;
    }

    public final String toString() {
        return zzbe.zzt(this).zzg("PanoramaId", this.zzbmM).zzg("Position", this.zzbmN).zzg("Radius", this.zzbmO).zzg("StreetViewPanoramaCamera", this.zzbmL).zzg("UserNavigationEnabled", this.zzbmP).zzg("ZoomGesturesEnabled", this.zzbmg).zzg("PanningGesturesEnabled", this.zzbmQ).zzg("StreetNamesEnabled", this.zzbmR).zzg("UseViewLifecycleInFragment", this.zzbma).toString();
    }

    public final StreetViewPanoramaOptions useViewLifecycleInFragment(boolean z) {
        this.zzbma = Boolean.valueOf(z);
        return this;
    }

    public final StreetViewPanoramaOptions userNavigationEnabled(boolean z) {
        this.zzbmP = Boolean.valueOf(z);
        return this;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zza(parcel, 2, getStreetViewPanoramaCamera(), i, false);
        zzd.zza(parcel, 3, getPanoramaId(), false);
        zzd.zza(parcel, 4, getPosition(), i, false);
        zzd.zza(parcel, 5, getRadius(), false);
        zzd.zza(parcel, 6, com.google.android.gms.maps.internal.zza.zzb(this.zzbmP));
        zzd.zza(parcel, 7, com.google.android.gms.maps.internal.zza.zzb(this.zzbmg));
        zzd.zza(parcel, 8, com.google.android.gms.maps.internal.zza.zzb(this.zzbmQ));
        zzd.zza(parcel, 9, com.google.android.gms.maps.internal.zza.zzb(this.zzbmR));
        zzd.zza(parcel, 10, com.google.android.gms.maps.internal.zza.zzb(this.zzbma));
        zzd.zzI(parcel, zze);
    }

    public final StreetViewPanoramaOptions zoomGesturesEnabled(boolean z) {
        this.zzbmg = Boolean.valueOf(z);
        return this;
    }
}
