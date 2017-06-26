package com.google.android.gms.maps.model.internal;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper.zza;
import com.google.android.gms.internal.zzee;
import com.google.android.gms.internal.zzef;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public abstract class zzh extends zzee implements zzg {
    public static zzg zzac(IBinder iBinder) {
        if (iBinder == null) {
            return null;
        }
        IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.maps.model.internal.IGroundOverlayDelegate");
        return queryLocalInterface instanceof zzg ? (zzg) queryLocalInterface : new zzi(iBinder);
    }

    public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
        if (zza(i, parcel, parcel2, i2)) {
            return true;
        }
        Parcelable position;
        float width;
        boolean isVisible;
        IInterface queryLocalInterface;
        switch (i) {
            case 1:
                remove();
                parcel2.writeNoException();
                break;
            case 2:
                String id = getId();
                parcel2.writeNoException();
                parcel2.writeString(id);
                break;
            case 3:
                setPosition((LatLng) zzef.zza(parcel, LatLng.CREATOR));
                parcel2.writeNoException();
                break;
            case 4:
                position = getPosition();
                parcel2.writeNoException();
                zzef.zzb(parcel2, position);
                break;
            case 5:
                setDimensions(parcel.readFloat());
                parcel2.writeNoException();
                break;
            case 6:
                zzf(parcel.readFloat(), parcel.readFloat());
                parcel2.writeNoException();
                break;
            case 7:
                width = getWidth();
                parcel2.writeNoException();
                parcel2.writeFloat(width);
                break;
            case 8:
                width = getHeight();
                parcel2.writeNoException();
                parcel2.writeFloat(width);
                break;
            case 9:
                setPositionFromBounds((LatLngBounds) zzef.zza(parcel, LatLngBounds.CREATOR));
                parcel2.writeNoException();
                break;
            case 10:
                position = getBounds();
                parcel2.writeNoException();
                zzef.zzb(parcel2, position);
                break;
            case 11:
                setBearing(parcel.readFloat());
                parcel2.writeNoException();
                break;
            case 12:
                width = getBearing();
                parcel2.writeNoException();
                parcel2.writeFloat(width);
                break;
            case 13:
                setZIndex(parcel.readFloat());
                parcel2.writeNoException();
                break;
            case 14:
                width = getZIndex();
                parcel2.writeNoException();
                parcel2.writeFloat(width);
                break;
            case 15:
                setVisible(zzef.zza(parcel));
                parcel2.writeNoException();
                break;
            case 16:
                isVisible = isVisible();
                parcel2.writeNoException();
                zzef.zza(parcel2, isVisible);
                break;
            case 17:
                setTransparency(parcel.readFloat());
                parcel2.writeNoException();
                break;
            case 18:
                width = getTransparency();
                parcel2.writeNoException();
                parcel2.writeFloat(width);
                break;
            case 19:
                zzg com_google_android_gms_maps_model_internal_zzg;
                IBinder readStrongBinder = parcel.readStrongBinder();
                if (readStrongBinder == null) {
                    com_google_android_gms_maps_model_internal_zzg = null;
                } else {
                    queryLocalInterface = readStrongBinder.queryLocalInterface("com.google.android.gms.maps.model.internal.IGroundOverlayDelegate");
                    com_google_android_gms_maps_model_internal_zzg = queryLocalInterface instanceof zzg ? (zzg) queryLocalInterface : new zzi(readStrongBinder);
                }
                isVisible = zzb(com_google_android_gms_maps_model_internal_zzg);
                parcel2.writeNoException();
                zzef.zza(parcel2, isVisible);
                break;
            case 20:
                int hashCodeRemote = hashCodeRemote();
                parcel2.writeNoException();
                parcel2.writeInt(hashCodeRemote);
                break;
            case 21:
                zzJ(zza.zzM(parcel.readStrongBinder()));
                parcel2.writeNoException();
                break;
            case 22:
                setClickable(zzef.zza(parcel));
                parcel2.writeNoException();
                break;
            case 23:
                isVisible = isClickable();
                parcel2.writeNoException();
                zzef.zza(parcel2, isVisible);
                break;
            case 24:
                setTag(zza.zzM(parcel.readStrongBinder()));
                parcel2.writeNoException();
                break;
            case 25:
                queryLocalInterface = getTag();
                parcel2.writeNoException();
                zzef.zza(parcel2, queryLocalInterface);
                break;
            default:
                return false;
        }
        return true;
    }
}
