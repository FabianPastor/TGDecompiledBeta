package com.google.android.gms.maps;

import com.google.android.gms.maps.StreetViewPanorama.OnStreetViewPanoramaClickListener;
import com.google.android.gms.maps.internal.zzbk;
import com.google.android.gms.maps.model.StreetViewPanoramaOrientation;

final class zzae extends zzbk {
    private /* synthetic */ OnStreetViewPanoramaClickListener zzbmF;

    zzae(StreetViewPanorama streetViewPanorama, OnStreetViewPanoramaClickListener onStreetViewPanoramaClickListener) {
        this.zzbmF = onStreetViewPanoramaClickListener;
    }

    public final void onStreetViewPanoramaClick(StreetViewPanoramaOrientation streetViewPanoramaOrientation) {
        this.zzbmF.onStreetViewPanoramaClick(streetViewPanoramaOrientation);
    }
}
