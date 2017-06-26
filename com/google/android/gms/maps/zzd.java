package com.google.android.gms.maps;

import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.internal.zzac;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.internal.zzp;

final class zzd extends zzac {
    private /* synthetic */ OnInfoWindowClickListener zzblC;

    zzd(GoogleMap googleMap, OnInfoWindowClickListener onInfoWindowClickListener) {
        this.zzblC = onInfoWindowClickListener;
    }

    public final void zze(zzp com_google_android_gms_maps_model_internal_zzp) {
        this.zzblC.onInfoWindowClick(new Marker(com_google_android_gms_maps_model_internal_zzp));
    }
}
