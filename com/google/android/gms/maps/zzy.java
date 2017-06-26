package com.google.android.gms.maps;

import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.internal.zzao;
import com.google.android.gms.maps.model.LatLng;

final class zzy extends zzao {
    private /* synthetic */ OnMapLongClickListener zzblX;

    zzy(GoogleMap googleMap, OnMapLongClickListener onMapLongClickListener) {
        this.zzblX = onMapLongClickListener;
    }

    public final void onMapLongClick(LatLng latLng) {
        this.zzblX.onMapLongClick(latLng);
    }
}
