package com.google.android.gms.maps.internal;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.internal.zzeu;
import com.google.android.gms.internal.zzew;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.model.internal.zza;
import com.google.android.gms.maps.model.internal.zzb;

public final class zzf extends zzeu implements zze {
    zzf(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.maps.internal.ICreator");
    }

    public final IMapViewDelegate zza(IObjectWrapper iObjectWrapper, GoogleMapOptions googleMapOptions) throws RemoteException {
        IMapViewDelegate iMapViewDelegate;
        Parcel zzbe = zzbe();
        zzew.zza(zzbe, (IInterface) iObjectWrapper);
        zzew.zza(zzbe, (Parcelable) googleMapOptions);
        Parcel zza = zza(3, zzbe);
        IBinder readStrongBinder = zza.readStrongBinder();
        if (readStrongBinder == null) {
            iMapViewDelegate = null;
        } else {
            IInterface queryLocalInterface = readStrongBinder.queryLocalInterface("com.google.android.gms.maps.internal.IMapViewDelegate");
            iMapViewDelegate = queryLocalInterface instanceof IMapViewDelegate ? (IMapViewDelegate) queryLocalInterface : new zzk(readStrongBinder);
        }
        zza.recycle();
        return iMapViewDelegate;
    }

    public final ICameraUpdateFactoryDelegate zzawc() throws RemoteException {
        ICameraUpdateFactoryDelegate iCameraUpdateFactoryDelegate;
        Parcel zza = zza(4, zzbe());
        IBinder readStrongBinder = zza.readStrongBinder();
        if (readStrongBinder == null) {
            iCameraUpdateFactoryDelegate = null;
        } else {
            IInterface queryLocalInterface = readStrongBinder.queryLocalInterface("com.google.android.gms.maps.internal.ICameraUpdateFactoryDelegate");
            iCameraUpdateFactoryDelegate = queryLocalInterface instanceof ICameraUpdateFactoryDelegate ? (ICameraUpdateFactoryDelegate) queryLocalInterface : new zzb(readStrongBinder);
        }
        zza.recycle();
        return iCameraUpdateFactoryDelegate;
    }

    public final zza zzawd() throws RemoteException {
        Parcel zza = zza(5, zzbe());
        zza zzbf = zzb.zzbf(zza.readStrongBinder());
        zza.recycle();
        return zzbf;
    }

    public final void zzi(IObjectWrapper iObjectWrapper, int i) throws RemoteException {
        Parcel zzbe = zzbe();
        zzew.zza(zzbe, (IInterface) iObjectWrapper);
        zzbe.writeInt(i);
        zzb(6, zzbe);
    }
}
