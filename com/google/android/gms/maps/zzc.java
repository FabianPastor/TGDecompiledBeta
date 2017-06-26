package com.google.android.gms.maps;

import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.internal.zzau;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.internal.zzp;

final class zzc extends zzau {
    private /* synthetic */ OnMarkerDragListener zzblB;

    zzc(GoogleMap googleMap, OnMarkerDragListener onMarkerDragListener) {
        this.zzblB = onMarkerDragListener;
    }

    public final void zzb(zzp com_google_android_gms_maps_model_internal_zzp) {
        this.zzblB.onMarkerDragStart(new Marker(com_google_android_gms_maps_model_internal_zzp));
    }

    public final void zzc(zzp com_google_android_gms_maps_model_internal_zzp) {
        this.zzblB.onMarkerDragEnd(new Marker(com_google_android_gms_maps_model_internal_zzp));
    }

    public final void zzd(zzp com_google_android_gms_maps_model_internal_zzp) {
        this.zzblB.onMarkerDrag(new Marker(com_google_android_gms_maps_model_internal_zzp));
    }
}
