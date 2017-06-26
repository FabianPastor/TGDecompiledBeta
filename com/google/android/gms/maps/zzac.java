package com.google.android.gms.maps;

import com.google.android.gms.maps.StreetViewPanorama.OnStreetViewPanoramaChangeListener;
import com.google.android.gms.maps.internal.zzbi;
import com.google.android.gms.maps.model.StreetViewPanoramaLocation;

final class zzac extends zzbi {
    private /* synthetic */ OnStreetViewPanoramaChangeListener zzbmD;

    zzac(StreetViewPanorama streetViewPanorama, OnStreetViewPanoramaChangeListener onStreetViewPanoramaChangeListener) {
        this.zzbmD = onStreetViewPanoramaChangeListener;
    }

    public final void onStreetViewPanoramaChange(StreetViewPanoramaLocation streetViewPanoramaLocation) {
        this.zzbmD.onStreetViewPanoramaChange(streetViewPanoramaLocation);
    }
}
