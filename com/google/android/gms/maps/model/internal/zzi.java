package com.google.android.gms.maps.model.internal;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.dynamic.IObjectWrapper.zza;
import com.google.android.gms.internal.zzed;
import com.google.android.gms.internal.zzef;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public final class zzi extends zzed implements zzg {
    zzi(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.maps.model.internal.IGroundOverlayDelegate");
    }

    public final float getBearing() throws RemoteException {
        Parcel zza = zza(12, zzZ());
        float readFloat = zza.readFloat();
        zza.recycle();
        return readFloat;
    }

    public final LatLngBounds getBounds() throws RemoteException {
        Parcel zza = zza(10, zzZ());
        LatLngBounds latLngBounds = (LatLngBounds) zzef.zza(zza, LatLngBounds.CREATOR);
        zza.recycle();
        return latLngBounds;
    }

    public final float getHeight() throws RemoteException {
        Parcel zza = zza(8, zzZ());
        float readFloat = zza.readFloat();
        zza.recycle();
        return readFloat;
    }

    public final String getId() throws RemoteException {
        Parcel zza = zza(2, zzZ());
        String readString = zza.readString();
        zza.recycle();
        return readString;
    }

    public final LatLng getPosition() throws RemoteException {
        Parcel zza = zza(4, zzZ());
        LatLng latLng = (LatLng) zzef.zza(zza, LatLng.CREATOR);
        zza.recycle();
        return latLng;
    }

    public final IObjectWrapper getTag() throws RemoteException {
        Parcel zza = zza(25, zzZ());
        IObjectWrapper zzM = zza.zzM(zza.readStrongBinder());
        zza.recycle();
        return zzM;
    }

    public final float getTransparency() throws RemoteException {
        Parcel zza = zza(18, zzZ());
        float readFloat = zza.readFloat();
        zza.recycle();
        return readFloat;
    }

    public final float getWidth() throws RemoteException {
        Parcel zza = zza(7, zzZ());
        float readFloat = zza.readFloat();
        zza.recycle();
        return readFloat;
    }

    public final float getZIndex() throws RemoteException {
        Parcel zza = zza(14, zzZ());
        float readFloat = zza.readFloat();
        zza.recycle();
        return readFloat;
    }

    public final int hashCodeRemote() throws RemoteException {
        Parcel zza = zza(20, zzZ());
        int readInt = zza.readInt();
        zza.recycle();
        return readInt;
    }

    public final boolean isClickable() throws RemoteException {
        Parcel zza = zza(23, zzZ());
        boolean zza2 = zzef.zza(zza);
        zza.recycle();
        return zza2;
    }

    public final boolean isVisible() throws RemoteException {
        Parcel zza = zza(16, zzZ());
        boolean zza2 = zzef.zza(zza);
        zza.recycle();
        return zza2;
    }

    public final void remove() throws RemoteException {
        zzb(1, zzZ());
    }

    public final void setBearing(float f) throws RemoteException {
        Parcel zzZ = zzZ();
        zzZ.writeFloat(f);
        zzb(11, zzZ);
    }

    public final void setClickable(boolean z) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, z);
        zzb(22, zzZ);
    }

    public final void setDimensions(float f) throws RemoteException {
        Parcel zzZ = zzZ();
        zzZ.writeFloat(f);
        zzb(5, zzZ);
    }

    public final void setPosition(LatLng latLng) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (Parcelable) latLng);
        zzb(3, zzZ);
    }

    public final void setPositionFromBounds(LatLngBounds latLngBounds) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (Parcelable) latLngBounds);
        zzb(9, zzZ);
    }

    public final void setTag(IObjectWrapper iObjectWrapper) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (IInterface) iObjectWrapper);
        zzb(24, zzZ);
    }

    public final void setTransparency(float f) throws RemoteException {
        Parcel zzZ = zzZ();
        zzZ.writeFloat(f);
        zzb(17, zzZ);
    }

    public final void setVisible(boolean z) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, z);
        zzb(15, zzZ);
    }

    public final void setZIndex(float f) throws RemoteException {
        Parcel zzZ = zzZ();
        zzZ.writeFloat(f);
        zzb(13, zzZ);
    }

    public final void zzJ(IObjectWrapper iObjectWrapper) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (IInterface) iObjectWrapper);
        zzb(21, zzZ);
    }

    public final boolean zzb(zzg com_google_android_gms_maps_model_internal_zzg) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (IInterface) com_google_android_gms_maps_model_internal_zzg);
        zzZ = zza(19, zzZ);
        boolean zza = zzef.zza(zzZ);
        zzZ.recycle();
        return zza;
    }

    public final void zzf(float f, float f2) throws RemoteException {
        Parcel zzZ = zzZ();
        zzZ.writeFloat(f);
        zzZ.writeFloat(f2);
        zzb(6, zzZ);
    }
}
