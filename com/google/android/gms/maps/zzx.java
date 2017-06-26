package com.google.android.gms.maps;

import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.internal.zzak;
import com.google.android.gms.maps.model.LatLng;

final class zzx extends zzak {
    private /* synthetic */ OnMapClickListener zzblW;

    zzx(GoogleMap googleMap, OnMapClickListener onMapClickListener) {
        this.zzblW = onMapClickListener;
    }

    public final void onMapClick(LatLng latLng) {
        this.zzblW.onMapClick(latLng);
    }
}
