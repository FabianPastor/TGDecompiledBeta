package com.google.android.gms.maps.model.internal;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import com.google.android.gms.internal.zzeu;
import com.google.android.gms.internal.zzew;
import com.google.android.gms.maps.model.LatLng;

public final class zzr extends zzeu implements zzp {
    zzr(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.maps.model.internal.IMarkerDelegate");
    }

    public final LatLng getPosition() throws RemoteException {
        Parcel zza = zza(4, zzbe());
        LatLng latLng = (LatLng) zzew.zza(zza, LatLng.CREATOR);
        zza.recycle();
        return latLng;
    }

    public final int hashCodeRemote() throws RemoteException {
        Parcel zza = zza(17, zzbe());
        int readInt = zza.readInt();
        zza.recycle();
        return readInt;
    }

    public final void setPosition(LatLng latLng) throws RemoteException {
        Parcel zzbe = zzbe();
        zzew.zza(zzbe, (Parcelable) latLng);
        zzb(3, zzbe);
    }

    public final boolean zzj(zzp com_google_android_gms_maps_model_internal_zzp) throws RemoteException {
        Parcel zzbe = zzbe();
        zzew.zza(zzbe, (IInterface) com_google_android_gms_maps_model_internal_zzp);
        zzbe = zza(16, zzbe);
        boolean zza = zzew.zza(zzbe);
        zzbe.recycle();
        return zza;
    }
}
