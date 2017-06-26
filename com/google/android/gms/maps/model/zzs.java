package com.google.android.gms.maps.model;

import com.google.android.gms.maps.model.internal.zzaa;

final class zzs extends zzaa {
    private /* synthetic */ TileProvider zzbod;

    zzs(TileOverlayOptions tileOverlayOptions, TileProvider tileProvider) {
        this.zzbod = tileProvider;
    }

    public final Tile getTile(int i, int i2, int i3) {
        return this.zzbod.getTile(i, i2, i3);
    }
}
