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

public final class zzr extends zzed implements zzp {
    zzr(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.maps.model.internal.IMarkerDelegate");
    }

    public final float getAlpha() throws RemoteException {
        Parcel zza = zza(26, zzZ());
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

    public final float getRotation() throws RemoteException {
        Parcel zza = zza(23, zzZ());
        float readFloat = zza.readFloat();
        zza.recycle();
        return readFloat;
    }

    public final String getSnippet() throws RemoteException {
        Parcel zza = zza(8, zzZ());
        String readString = zza.readString();
        zza.recycle();
        return readString;
    }

    public final IObjectWrapper getTag() throws RemoteException {
        Parcel zza = zza(30, zzZ());
        IObjectWrapper zzM = zza.zzM(zza.readStrongBinder());
        zza.recycle();
        return zzM;
    }

    public final String getTitle() throws RemoteException {
        Parcel zza = zza(6, zzZ());
        String readString = zza.readString();
        zza.recycle();
        return readString;
    }

    public final float getZIndex() throws RemoteException {
        Parcel zza = zza(28, zzZ());
        float readFloat = zza.readFloat();
        zza.recycle();
        return readFloat;
    }

    public final int hashCodeRemote() throws RemoteException {
        Parcel zza = zza(17, zzZ());
        int readInt = zza.readInt();
        zza.recycle();
        return readInt;
    }

    public final void hideInfoWindow() throws RemoteException {
        zzb(12, zzZ());
    }

    public final boolean isDraggable() throws RemoteException {
        Parcel zza = zza(10, zzZ());
        boolean zza2 = zzef.zza(zza);
        zza.recycle();
        return zza2;
    }

    public final boolean isFlat() throws RemoteException {
        Parcel zza = zza(21, zzZ());
        boolean zza2 = zzef.zza(zza);
        zza.recycle();
        return zza2;
    }

    public final boolean isInfoWindowShown() throws RemoteException {
        Parcel zza = zza(13, zzZ());
        boolean zza2 = zzef.zza(zza);
        zza.recycle();
        return zza2;
    }

    public final boolean isVisible() throws RemoteException {
        Parcel zza = zza(15, zzZ());
        boolean zza2 = zzef.zza(zza);
        zza.recycle();
        return zza2;
    }

    public final void remove() throws RemoteException {
        zzb(1, zzZ());
    }

    public final void setAlpha(float f) throws RemoteException {
        Parcel zzZ = zzZ();
        zzZ.writeFloat(f);
        zzb(25, zzZ);
    }

    public final void setAnchor(float f, float f2) throws RemoteException {
        Parcel zzZ = zzZ();
        zzZ.writeFloat(f);
        zzZ.writeFloat(f2);
        zzb(19, zzZ);
    }

    public final void setDraggable(boolean z) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, z);
        zzb(9, zzZ);
    }

    public final void setFlat(boolean z) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, z);
        zzb(20, zzZ);
    }

    public final void setInfoWindowAnchor(float f, float f2) throws RemoteException {
        Parcel zzZ = zzZ();
        zzZ.writeFloat(f);
        zzZ.writeFloat(f2);
        zzb(24, zzZ);
    }

    public final void setPosition(LatLng latLng) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (Parcelable) latLng);
        zzb(3, zzZ);
    }

    public final void setRotation(float f) throws RemoteException {
        Parcel zzZ = zzZ();
        zzZ.writeFloat(f);
        zzb(22, zzZ);
    }

    public final void setSnippet(String str) throws RemoteException {
        Parcel zzZ = zzZ();
        zzZ.writeString(str);
        zzb(7, zzZ);
    }

    public final void setTag(IObjectWrapper iObjectWrapper) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (IInterface) iObjectWrapper);
        zzb(29, zzZ);
    }

    public final void setTitle(String str) throws RemoteException {
        Parcel zzZ = zzZ();
        zzZ.writeString(str);
        zzb(5, zzZ);
    }

    public final void setVisible(boolean z) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, z);
        zzb(14, zzZ);
    }

    public final void setZIndex(float f) throws RemoteException {
        Parcel zzZ = zzZ();
        zzZ.writeFloat(f);
        zzb(27, zzZ);
    }

    public final void showInfoWindow() throws RemoteException {
        zzb(11, zzZ());
    }

    public final void zzK(IObjectWrapper iObjectWrapper) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (IInterface) iObjectWrapper);
        zzb(18, zzZ);
    }

    public final boolean zzj(zzp com_google_android_gms_maps_model_internal_zzp) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (IInterface) com_google_android_gms_maps_model_internal_zzp);
        zzZ = zza(16, zzZ);
        boolean zza = zzef.zza(zzZ);
        zzZ.recycle();
        return zza;
    }
}
