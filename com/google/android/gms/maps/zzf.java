package com.google.android.gms.maps;

import com.google.android.gms.maps.GoogleMap.OnInfoWindowCloseListener;
import com.google.android.gms.maps.internal.zzae;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.internal.zzp;

final class zzf extends zzae {
    private /* synthetic */ OnInfoWindowCloseListener zzblE;

    zzf(GoogleMap googleMap, OnInfoWindowCloseListener onInfoWindowCloseListener) {
        this.zzblE = onInfoWindowCloseListener;
    }

    public final void zzg(zzp com_google_android_gms_maps_model_internal_zzp) {
        this.zzblE.onInfoWindowClose(new Marker(com_google_android_gms_maps_model_internal_zzp));
    }
}
