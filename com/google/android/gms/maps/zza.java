package com.google.android.gms.maps;

import com.google.android.gms.maps.GoogleMap.OnIndoorStateChangeListener;
import com.google.android.gms.maps.internal.zzaa;
import com.google.android.gms.maps.model.IndoorBuilding;
import com.google.android.gms.maps.model.internal.zzj;

final class zza extends zzaa {
    private /* synthetic */ OnIndoorStateChangeListener zzblz;

    zza(GoogleMap googleMap, OnIndoorStateChangeListener onIndoorStateChangeListener) {
        this.zzblz = onIndoorStateChangeListener;
    }

    public final void onIndoorBuildingFocused() {
        this.zzblz.onIndoorBuildingFocused();
    }

    public final void zza(zzj com_google_android_gms_maps_model_internal_zzj) {
        this.zzblz.onIndoorLevelActivated(new IndoorBuilding(com_google_android_gms_maps_model_internal_zzj));
    }
}
