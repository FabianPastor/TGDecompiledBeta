package com.google.android.gms.maps;

import com.google.android.gms.maps.GoogleMap.OnGroundOverlayClickListener;
import com.google.android.gms.maps.internal.zzy;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.internal.zzg;

final class zzk extends zzy {
    private /* synthetic */ OnGroundOverlayClickListener zzblJ;

    zzk(GoogleMap googleMap, OnGroundOverlayClickListener onGroundOverlayClickListener) {
        this.zzblJ = onGroundOverlayClickListener;
    }

    public final void zza(zzg com_google_android_gms_maps_model_internal_zzg) {
        this.zzblJ.onGroundOverlayClick(new GroundOverlay(com_google_android_gms_maps_model_internal_zzg));
    }
}
