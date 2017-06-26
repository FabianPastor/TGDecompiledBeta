package com.google.android.gms.maps.model;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.maps.model.internal.zzaa;
import com.google.android.gms.maps.model.internal.zzz;

public final class TileOverlayOptions extends zza {
    public static final Creator<TileOverlayOptions> CREATOR = new zzt();
    private zzz zzbnY;
    private TileProvider zzbnZ;
    private float zzbnk;
    private boolean zzbnl = true;
    private float zzbnt = 0.0f;
    private boolean zzboa = true;

    TileOverlayOptions(IBinder iBinder, boolean z, float f, boolean z2, float f2) {
        this.zzbnY = zzaa.zzaj(iBinder);
        this.zzbnZ = this.zzbnY == null ? null : new zzr(this);
        this.zzbnl = z;
        this.zzbnk = f;
        this.zzboa = z2;
        this.zzbnt = f2;
    }

    public final TileOverlayOptions fadeIn(boolean z) {
        this.zzboa = z;
        return this;
    }

    public final boolean getFadeIn() {
        return this.zzboa;
    }

    public final TileProvider getTileProvider() {
        return this.zzbnZ;
    }

    public final float getTransparency() {
        return this.zzbnt;
    }

    public final float getZIndex() {
        return this.zzbnk;
    }

    public final boolean isVisible() {
        return this.zzbnl;
    }

    public final TileOverlayOptions tileProvider(TileProvider tileProvider) {
        this.zzbnZ = tileProvider;
        this.zzbnY = this.zzbnZ == null ? null : new zzs(this, tileProvider);
        return this;
    }

    public final TileOverlayOptions transparency(float f) {
        boolean z = f >= 0.0f && f <= 1.0f;
        zzbo.zzb(z, (Object) "Transparency must be in the range [0..1]");
        this.zzbnt = f;
        return this;
    }

    public final TileOverlayOptions visible(boolean z) {
        this.zzbnl = z;
        return this;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zza(parcel, 2, this.zzbnY.asBinder(), false);
        zzd.zza(parcel, 3, isVisible());
        zzd.zza(parcel, 4, getZIndex());
        zzd.zza(parcel, 5, getFadeIn());
        zzd.zza(parcel, 6, getTransparency());
        zzd.zzI(parcel, zze);
    }

    public final TileOverlayOptions zIndex(float f) {
        this.zzbnk = f;
        return this;
    }
}
