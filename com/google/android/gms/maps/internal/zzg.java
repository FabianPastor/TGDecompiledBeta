package com.google.android.gms.maps.internal;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.internal.zzeu;
import com.google.android.gms.internal.zzew;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.internal.zzp;
import com.google.android.gms.maps.model.internal.zzq;

public final class zzg extends zzeu implements IGoogleMapDelegate {
    zzg(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.maps.internal.IGoogleMapDelegate");
    }

    public final zzp addMarker(MarkerOptions markerOptions) throws RemoteException {
        Parcel zzbe = zzbe();
        zzew.zza(zzbe, (Parcelable) markerOptions);
        zzbe = zza(11, zzbe);
        zzp zzbk = zzq.zzbk(zzbe.readStrongBinder());
        zzbe.recycle();
        return zzbk;
    }

    public final void animateCamera(IObjectWrapper iObjectWrapper) throws RemoteException {
        Parcel zzbe = zzbe();
        zzew.zza(zzbe, (IInterface) iObjectWrapper);
        zzb(5, zzbe);
    }

    public final CameraPosition getCameraPosition() throws RemoteException {
        Parcel zza = zza(1, zzbe());
        CameraPosition cameraPosition = (CameraPosition) zzew.zza(zza, CameraPosition.CREATOR);
        zza.recycle();
        return cameraPosition;
    }

    public final float getMaxZoomLevel() throws RemoteException {
        Parcel zza = zza(2, zzbe());
        float readFloat = zza.readFloat();
        zza.recycle();
        return readFloat;
    }

    public final IUiSettingsDelegate getUiSettings() throws RemoteException {
        IUiSettingsDelegate iUiSettingsDelegate;
        Parcel zza = zza(25, zzbe());
        IBinder readStrongBinder = zza.readStrongBinder();
        if (readStrongBinder == null) {
            iUiSettingsDelegate = null;
        } else {
            IInterface queryLocalInterface = readStrongBinder.queryLocalInterface("com.google.android.gms.maps.internal.IUiSettingsDelegate");
            iUiSettingsDelegate = queryLocalInterface instanceof IUiSettingsDelegate ? (IUiSettingsDelegate) queryLocalInterface : new zzbx(readStrongBinder);
        }
        zza.recycle();
        return iUiSettingsDelegate;
    }

    public final void moveCamera(IObjectWrapper iObjectWrapper) throws RemoteException {
        Parcel zzbe = zzbe();
        zzew.zza(zzbe, (IInterface) iObjectWrapper);
        zzb(4, zzbe);
    }

    public final void setMapType(int i) throws RemoteException {
        Parcel zzbe = zzbe();
        zzbe.writeInt(i);
        zzb(16, zzbe);
    }

    public final void setMyLocationEnabled(boolean z) throws RemoteException {
        Parcel zzbe = zzbe();
        zzew.zza(zzbe, z);
        zzb(22, zzbe);
    }

    public final void setOnMyLocationChangeListener(zzax com_google_android_gms_maps_internal_zzax) throws RemoteException {
        Parcel zzbe = zzbe();
        zzew.zza(zzbe, (IInterface) com_google_android_gms_maps_internal_zzax);
        zzb(36, zzbe);
    }

    public final void setPadding(int i, int i2, int i3, int i4) throws RemoteException {
        Parcel zzbe = zzbe();
        zzbe.writeInt(i);
        zzbe.writeInt(i2);
        zzbe.writeInt(i3);
        zzbe.writeInt(i4);
        zzb(39, zzbe);
    }
}
