package com.google.android.gms.maps.model;

import android.os.RemoteException;
import com.google.android.gms.maps.model.internal.zzz;

final class zzr implements TileProvider {
    private final zzz zzbob = this.zzboc.zzbnY;
    private /* synthetic */ TileOverlayOptions zzboc;

    zzr(TileOverlayOptions tileOverlayOptions) {
        this.zzboc = tileOverlayOptions;
    }

    public final Tile getTile(int i, int i2, int i3) {
        try {
            return this.zzbob.getTile(i, i2, i3);
        } catch (RemoteException e) {
            return null;
        }
    }
}
