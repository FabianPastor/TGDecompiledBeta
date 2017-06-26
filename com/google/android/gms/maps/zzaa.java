package com.google.android.gms.maps;

import android.os.RemoteException;
import com.google.android.gms.maps.internal.IGoogleMapDelegate;
import com.google.android.gms.maps.internal.zzaq;

final class zzaa extends zzaq {
    private /* synthetic */ OnMapReadyCallback zzbmr;

    zzaa(zza com_google_android_gms_maps_MapFragment_zza, OnMapReadyCallback onMapReadyCallback) {
        this.zzbmr = onMapReadyCallback;
    }

    public final void zza(IGoogleMapDelegate iGoogleMapDelegate) throws RemoteException {
        this.zzbmr.onMapReady(new GoogleMap(iGoogleMapDelegate));
    }
}
