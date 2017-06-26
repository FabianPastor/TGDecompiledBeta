package com.google.android.gms.maps;

import com.google.android.gms.maps.GoogleMap.OnPolygonClickListener;
import com.google.android.gms.maps.internal.zzbc;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.internal.zzs;

final class zzo extends zzbc {
    private /* synthetic */ OnPolygonClickListener zzblN;

    zzo(GoogleMap googleMap, OnPolygonClickListener onPolygonClickListener) {
        this.zzblN = onPolygonClickListener;
    }

    public final void zza(zzs com_google_android_gms_maps_model_internal_zzs) {
        this.zzblN.onPolygonClick(new Polygon(com_google_android_gms_maps_model_internal_zzs));
    }
}
