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
import com.google.android.gms.maps.model.PatternItem;
import java.util.List;

public abstract class zze extends zzee implements zzd {
    public static zzd zzab(IBinder iBinder) {
        if (iBinder == null) {
            return null;
        }
        IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.maps.model.internal.ICircleDelegate");
        return queryLocalInterface instanceof zzd ? (zzd) queryLocalInterface : new zzf(iBinder);
    }

    public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
        if (zza(i, parcel, parcel2, i2)) {
            return true;
        }
        float strokeWidth;
        int strokeColor;
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
                setCenter((LatLng) zzef.zza(parcel, LatLng.CREATOR));
                parcel2.writeNoException();
                break;
            case 4:
                Parcelable center = getCenter();
                parcel2.writeNoException();
                zzef.zzb(parcel2, center);
                break;
            case 5:
                setRadius(parcel.readDouble());
                parcel2.writeNoException();
                break;
            case 6:
                double radius = getRadius();
                parcel2.writeNoException();
                parcel2.writeDouble(radius);
                break;
            case 7:
                setStrokeWidth(parcel.readFloat());
                parcel2.writeNoException();
                break;
            case 8:
                strokeWidth = getStrokeWidth();
                parcel2.writeNoException();
                parcel2.writeFloat(strokeWidth);
                break;
            case 9:
                setStrokeColor(parcel.readInt());
                parcel2.writeNoException();
                break;
            case 10:
                strokeColor = getStrokeColor();
                parcel2.writeNoException();
                parcel2.writeInt(strokeColor);
                break;
            case 11:
                setFillColor(parcel.readInt());
                parcel2.writeNoException();
                break;
            case 12:
                strokeColor = getFillColor();
                parcel2.writeNoException();
                parcel2.writeInt(strokeColor);
                break;
            case 13:
                setZIndex(parcel.readFloat());
                parcel2.writeNoException();
                break;
            case 14:
                strokeWidth = getZIndex();
                parcel2.writeNoException();
                parcel2.writeFloat(strokeWidth);
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
                zzd com_google_android_gms_maps_model_internal_zzd;
                IBinder readStrongBinder = parcel.readStrongBinder();
                if (readStrongBinder == null) {
                    com_google_android_gms_maps_model_internal_zzd = null;
                } else {
                    queryLocalInterface = readStrongBinder.queryLocalInterface("com.google.android.gms.maps.model.internal.ICircleDelegate");
                    com_google_android_gms_maps_model_internal_zzd = queryLocalInterface instanceof zzd ? (zzd) queryLocalInterface : new zzf(readStrongBinder);
                }
                isVisible = zzb(com_google_android_gms_maps_model_internal_zzd);
                parcel2.writeNoException();
                zzef.zza(parcel2, isVisible);
                break;
            case 18:
                strokeColor = hashCodeRemote();
                parcel2.writeNoException();
                parcel2.writeInt(strokeColor);
                break;
            case 19:
                setClickable(zzef.zza(parcel));
                parcel2.writeNoException();
                break;
            case 20:
                isVisible = isClickable();
                parcel2.writeNoException();
                zzef.zza(parcel2, isVisible);
                break;
            case 21:
                setStrokePattern(parcel.createTypedArrayList(PatternItem.CREATOR));
                parcel2.writeNoException();
                break;
            case 22:
                List strokePattern = getStrokePattern();
                parcel2.writeNoException();
                parcel2.writeTypedList(strokePattern);
                break;
            case 23:
                setTag(zza.zzM(parcel.readStrongBinder()));
                parcel2.writeNoException();
                break;
            case 24:
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
