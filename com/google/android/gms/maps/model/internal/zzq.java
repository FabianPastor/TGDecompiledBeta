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
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;

public abstract class zzq extends zzee implements zzp {
    public static zzp zzaf(IBinder iBinder) {
        if (iBinder == null) {
            return null;
        }
        IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.maps.model.internal.IMarkerDelegate");
        return queryLocalInterface instanceof zzp ? (zzp) queryLocalInterface : new zzr(iBinder);
    }

    public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
        if (zza(i, parcel, parcel2, i2)) {
            return true;
        }
        String id;
        boolean isDraggable;
        IInterface queryLocalInterface;
        float rotation;
        switch (i) {
            case 1:
                remove();
                parcel2.writeNoException();
                break;
            case 2:
                id = getId();
                parcel2.writeNoException();
                parcel2.writeString(id);
                break;
            case 3:
                setPosition((LatLng) zzef.zza(parcel, LatLng.CREATOR));
                parcel2.writeNoException();
                break;
            case 4:
                Parcelable position = getPosition();
                parcel2.writeNoException();
                zzef.zzb(parcel2, position);
                break;
            case 5:
                setTitle(parcel.readString());
                parcel2.writeNoException();
                break;
            case 6:
                id = getTitle();
                parcel2.writeNoException();
                parcel2.writeString(id);
                break;
            case 7:
                setSnippet(parcel.readString());
                parcel2.writeNoException();
                break;
            case 8:
                id = getSnippet();
                parcel2.writeNoException();
                parcel2.writeString(id);
                break;
            case 9:
                setDraggable(zzef.zza(parcel));
                parcel2.writeNoException();
                break;
            case 10:
                isDraggable = isDraggable();
                parcel2.writeNoException();
                zzef.zza(parcel2, isDraggable);
                break;
            case 11:
                showInfoWindow();
                parcel2.writeNoException();
                break;
            case 12:
                hideInfoWindow();
                parcel2.writeNoException();
                break;
            case 13:
                isDraggable = isInfoWindowShown();
                parcel2.writeNoException();
                zzef.zza(parcel2, isDraggable);
                break;
            case 14:
                setVisible(zzef.zza(parcel));
                parcel2.writeNoException();
                break;
            case 15:
                isDraggable = isVisible();
                parcel2.writeNoException();
                zzef.zza(parcel2, isDraggable);
                break;
            case 16:
                zzp com_google_android_gms_maps_model_internal_zzp;
                IBinder readStrongBinder = parcel.readStrongBinder();
                if (readStrongBinder == null) {
                    com_google_android_gms_maps_model_internal_zzp = null;
                } else {
                    queryLocalInterface = readStrongBinder.queryLocalInterface("com.google.android.gms.maps.model.internal.IMarkerDelegate");
                    com_google_android_gms_maps_model_internal_zzp = queryLocalInterface instanceof zzp ? (zzp) queryLocalInterface : new zzr(readStrongBinder);
                }
                isDraggable = zzj(com_google_android_gms_maps_model_internal_zzp);
                parcel2.writeNoException();
                zzef.zza(parcel2, isDraggable);
                break;
            case 17:
                int hashCodeRemote = hashCodeRemote();
                parcel2.writeNoException();
                parcel2.writeInt(hashCodeRemote);
                break;
            case 18:
                zzK(zza.zzM(parcel.readStrongBinder()));
                parcel2.writeNoException();
                break;
            case 19:
                setAnchor(parcel.readFloat(), parcel.readFloat());
                parcel2.writeNoException();
                break;
            case 20:
                setFlat(zzef.zza(parcel));
                parcel2.writeNoException();
                break;
            case 21:
                isDraggable = isFlat();
                parcel2.writeNoException();
                zzef.zza(parcel2, isDraggable);
                break;
            case 22:
                setRotation(parcel.readFloat());
                parcel2.writeNoException();
                break;
            case 23:
                rotation = getRotation();
                parcel2.writeNoException();
                parcel2.writeFloat(rotation);
                break;
            case 24:
                setInfoWindowAnchor(parcel.readFloat(), parcel.readFloat());
                parcel2.writeNoException();
                break;
            case 25:
                setAlpha(parcel.readFloat());
                parcel2.writeNoException();
                break;
            case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                rotation = getAlpha();
                parcel2.writeNoException();
                parcel2.writeFloat(rotation);
                break;
            case 27:
                setZIndex(parcel.readFloat());
                parcel2.writeNoException();
                break;
            case 28:
                rotation = getZIndex();
                parcel2.writeNoException();
                parcel2.writeFloat(rotation);
                break;
            case NalUnitTypes.NAL_TYPE_RSV_VCL29 /*29*/:
                setTag(zza.zzM(parcel.readStrongBinder()));
                parcel2.writeNoException();
                break;
            case NalUnitTypes.NAL_TYPE_RSV_VCL30 /*30*/:
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
